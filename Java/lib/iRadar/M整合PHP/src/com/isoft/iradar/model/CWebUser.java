package com.isoft.iradar.model;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.FuncsUtil.clear_messages;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.rda_setcookie;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import static com.isoft.Feature.*;
import com.isoft.biz.Delegator;
import com.isoft.biz.daoimpl.radar.CUserDAO;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;

public class CWebUser {
	
	public static Map<String, Object> data() {
		RadarContext ctx = RadarContext.getContext();
		HttpSession session = ctx.getRequest().getSession(true);
		Map<String, Object> data = (Map) session.getAttribute(CWebUser.class.getName());
		if (data == null) {
			data = new IMap();
			session.setAttribute(CWebUser.class.getName(), data);
		}
		return data;
	}
	
	public static void set(Map<String, Object> userInfo) {
		RadarContext ctx = RadarContext.getContext();
		HttpSession session = ctx.getRequest().getSession(true);
		session.setAttribute(CWebUser.class.getName(), userInfo);
		
		if (userInfo != null) {
			Map uinfo = new HashMap();
			uinfo.put("tenantId", Nest.value(userInfo, "tenantid").asString());
			uinfo.put("tenantRole", Role.LESSOR.magic());
			uinfo.put("userId", Nest.value(userInfo, "userid").asString());
			uinfo.put("userName", Nest.value(userInfo, "alias").asString());
			uinfo.put("admin", "Y".equals(Nest.value(userInfo, "admin").asString()) ? "Y" : "N");
			uinfo.put("admin", (Nest.value(userInfo,"type").asInteger()==2 || Nest.value(userInfo,"type").asInteger()==3) ? "Y":"N");
			((IdentityBean)RadarContext.getIdentityBean()).init(uinfo);
		}
	}
	
	public static Object get(String key) {
		return data().get(key);
	}
	
	public static boolean login(final String login, final String password) {
		try{
			setDefault();
			return CDelegator.doDelegate(RadarContext.getIdentityBean(), new Delegator<Boolean>() {
				@Override
				public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor)  throws Exception{
					Map<String, Object> params = new IMap();
					params.put("user", login);
					params.put("password", password);
					params.put("userData", true);
					Map data = (Map)API.User(idBean, executor).login(params);
					set(data);
					
					if (data == null || data.isEmpty()) {
						throw new Exception();
					}
					
					if ((Integer) data.get("gui_access") == GROUP_GUI_ACCESS_DISABLED) {
						error(_("GUI access disabled."));
						throw new Exception();
					}
					
					if (empty(data.get("url"))) {
						data.put("url", CProfile.get(idBean, executor, "web.menu.view.last", "index.action"));
					}
					
					if (isset(data, "attempt_failed")
							&& ((Integer) data.get("attempt_failed")) > 0) {
						CProfile.init(idBean, executor);
						CProfile.update(idBean, executor, "web.login.attempt.failed", data.get("attempt_failed"), PROFILE_TYPE_INT);
						CProfile.update(idBean, executor, "web.login.attempt.ip", data.get("attempt_ip"), PROFILE_TYPE_STR);
						CProfile.update(idBean, executor, "web.login.attempt.clock", data.get("attempt_clock"), PROFILE_TYPE_INT);
						CProfile.flush(idBean, executor);
					}
					
					params.put("sessionId", RadarContext.sessionId());
					executor.executeInsertDeleteUpdate(
							"DELETE FROM sessions WHERE sessionid=#{sessionId}", 
							params);
					RadarContext.sessionId((String)data.get("sessionid"));
					rda_setcookie("rda_sessionid", Nest.value(data,"sessionid").asString(), Nest.value(data,"autologin").asBoolean() ? (int)time() + SEC_PER_DAY * 31 : -1);
					
					return true;
				}
			});
		} catch (Exception e) {
			setDefault();
			return false;
		}
	}
	
	public static void logout() {
		CDelegator.doDelegate(RadarContext.getIdentityBean(), new Delegator<Boolean>() {
			@Override
			public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				CUserDAO idao = new CUserDAO(idBean, executor);
				return idao.logout();
			}
		});
		data().clear();
	}
	
	public static boolean checkAuthentication(final String sessionid) {
		try{
			return CDelegator.doDelegate(RadarContext.getIdentityBean(), new Delegator<Boolean>() {
				@Override
				public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor)  throws Exception{
					if (sessionid != null) {
						set(API.User(idBean, executor).checkAuthentication(sessionid));
					}
					
					if (sessionid == null || data().isEmpty()) {
						setDefault();
						Map<String, Object> params = new IMap();
						params.put("user", RDA_GUEST_USER);
						params.put("password", "");
						params.put("userData", true);
						Map data = (Map) API.User(idBean, executor).login(params);
						set(data);
						
						if (data.isEmpty()) {
							clear_messages(1);
							throw new Exception();
						}
					}
					
					if (data().containsKey("gui_access") && (Integer) data().get("gui_access") == GROUP_GUI_ACCESS_DISABLED) {
						error(_("GUI access disabled."));
						throw new Exception();
					}
	
					return true;
				}
			});
		} catch (Exception e) {
			setDefault();
			return false;
		}
	}
	
	public static void setDefault() {
		if (enableGuestUser) {
			Map<String, Object> data = data();
			data.put("tenantid", defaultTenantId);
			data.put("alias", RDA_GUEST_USER);
			data.put("userid", "");
			data.put("lang", "en_gb");
			data.put("type", 0);
			data.put("admin", "N");
			
			if (data != null) {
				Map uinfo = new HashMap();
				uinfo.put("tenantId", Nest.value(data, "tenantid").asString());
				uinfo.put("tenantRole", Role.TENANT.magic());
				uinfo.put("userId", Nest.value(data, "userid").asString());
				uinfo.put("userName", Nest.value(data, "alias").asString());
				uinfo.put("admin", "Y".equals(Nest.value(data, "admin").asString()) ? "Y" : "N");
				((IdentityBean)RadarContext.getIdentityBean()).init(uinfo);
			}
		}
	}
	
	/**
	 * Returns the type of the current user.
	 * @return
	 */
	public static int getType() {
		return Nest.value(data(), "type").asInteger();
	}
	
	/**
	 * Returns true if the current user is logged in.
	 * @return
	 */
	public static boolean isLoggedIn() {
		return !is_null(data().get("userid"));
	}
	
	/**
	 * Returns true if the user is not logged in or logged in as Guest.
	 * @return
	 */
	public static boolean isGuest() {
		return RDA_GUEST_USER.equals(data().get("alias"));
	}
}
