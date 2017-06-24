package com.isoft.biz.daoimpl.radar;

import static com.isoft.iaas.openstack.OpsUtils.getOpenStackClientForAdmin;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_ATTEMPTS;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_BLOCK;
import static com.isoft.iradar.inc.Defines.RDA_SESSION_ACTIVE;
import static com.isoft.iradar.inc.MD5Util.MD5;
import static com.isoft.iradar.inc.PermUtil.check_perm2system;
import static com.isoft.types.CArray.map;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.Configuration;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.base.client.OpenStackResponseException;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Token;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class CUserDAO extends CUserGeneralDAO {
	
	private static CArray<User> OS_USERS = new CArray();

	public CUserDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}

	@Override
	public Object login(Map<String, Object> user) {
		String username = Nest.value(user,"user").asString(true);
		String password = Nest.value(user,"password").asString(true);
		String tenantId = null;
		boolean loginSuccess = false;
		Token osToken = null;
		Tenant osTenant  = null;
		Access.User osUser = null;
		try {
			tenantId = getOsTenantIdByUsername(username);
			if (!isEmpty(tenantId)) {
				Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
				Access access = keystone.tokens()
						.authenticate(new UsernamePassword(username, password))
						.withTenantId(tenantId).execute();
				osToken = access.getToken();
				osTenant = osToken.getTenant();
				osUser = access.getUser();
				loginSuccess = true;
			} else {
				loginSuccess = false;
			}
		} catch (OpenStackResponseException e) {
			loginSuccess = false;
		}
		
		//FIXME: 修复tenantId不随用户改变的BUG
		tenantId = this.idBean.getTenantId();
		
		Map params = new HashMap();
		params.put("tenantid", tenantId);
		params.put("alias", username);
		
		SQLExecutor executor = getSqlExecutor();
		List<Map> userInfos = executor.executeNameParaQuery(
				"SELECT u.tenantid,u.userid,u.attempt_failed,u.attempt_clock,u.attempt_ip "+
				" FROM users u "+
				" WHERE u.tenantid=#{tenantid}"+
				    " AND u.alias=#{alias}", 
				params);
		if (userInfos.isEmpty()) {
			if (loginSuccess) {
				executor.executeInsertDeleteUpdate(
					"insert into users(tenantid,userid,alias,name,surname,passwd,url,autologin,autologout,lang,refresh,type,theme,attempt_failed,attempt_ip,attempt_clock,rows_per_page)"+
					"values(#{tenantid},#{userid},#{alias},#{name},#{surname},#{passwd},#{url},#{autologin},#{autologout},#{lang},#{refresh},#{type},#{theme},#{attempt_failed},#{attempt_ip},#{attempt_clock},#{rows_per_page})", 
					map(
							"tenantid", tenantId,
							"userid",osUser.getId(),
							"alias",username,
							"name",osUser.getUsername(),
							"surname","",
							"passwd","",
							"url","",
							"autologin",1,
							"autologout",0,
							"lang","en_US",
							"refresh",3000,
							"type",3,
							"theme","default",
							"attempt_failed",0,
							"attempt_ip","",
							"attempt_clock",0,
							"rows_per_page",10
					)
				);
				executor.executeInsertDeleteUpdate(
					"INSERT INTO users_groups VALUES (#{tenantid},#{id},#{usrgrpid},#{userid})", 
					map(
							"tenantid", tenantId,
							"id", get_dbid(this.idBean, getSqlExecutor(),"users_groups", "id"),
							"usrgrpid",Feature.defaultOsUserGroup,
							"userid",osUser.getId()
					)
				);
				userInfos.add(map(
						"tenantid", tenantId,
						"userid",osUser.getId(),
						"attempt_failed",0,
						"attempt_ip","",
						"attempt_clock",0
				));
				
				//增加租户Config的数据添加
				//CArray configs = DBUtil.DBselect(executor, "select 1 from config where tenantid = #{tenantid}", map("tenantid", tenantId));
				/*CArray configs = DBUtil.DBselect(executor, "select 1 from config where tenantid = #{tenantid}", map("tenantid", "-"));
				if(empty(configs)) {
					Long id = reserveIds("config", 1);
					executor.executeInsertDeleteUpdate(
						"INSERT INTO `config` VALUES (" +
							"#{tenantid}, #{id},600,'1-5,09:00-18:00;',7,1,7,100,'originalblue',0,'',389,'','','','',1,1,5,10,1000,'DBDBDB','D6F6FF','FFF6A5','FFB689','FF9999','FF3838','Not classified','Information','Warning','Average','High','Disaster',1800,1800,'DC0000','DC0000','00AA00','00AA00',1,1,1,1,1,10,1,365,365,365,365,1,365,1,365,1,365,1,0,90,1,0,365"+
						")", 
						map(
							"id", id,
							//"tenantid", tenantId
							"tenantid", "-"
						)
					);
				}*/
			} else {
				throw new RuntimeException(_("Login name or password is incorrect."));
			}
		}
		
		Map userInfo = userInfos.get(0);
		long time = time();
		// check if user is blocked
		int attempt_failed = (Integer) userInfo.get("attempt_failed");
		if (attempt_failed >= RDA_LOGIN_ATTEMPTS) {
			int attempt_clock = (Integer) userInfo.get("attempt_clock");
			if ((time - attempt_clock) < RDA_LOGIN_BLOCK) {
				throw new RuntimeException(_s("Account is blocked for %s seconds", RDA_LOGIN_BLOCK - ( time - attempt_clock)));
			}
			params.put("time", time);
			executor.executeInsertDeleteUpdate(
					"UPDATE users SET attempt_clock=#{time}"+
					" WHERE tenantid=#{tenantid}"+
					    " AND alias=#{alias}", 
					params);
		}
		
		// check system permissions
		String userId = Nest.value(userInfo, "userid").asString();
		if (!check_perm2system(this.idBean, executor, userId)) {
			throw new RuntimeException(_("No permissions for system access."));
		}
		
		params.put("userId", userId);
		List<Map> datas = executor.executeNameParaQuery(
				"SELECT MAX(g.gui_access) AS gui_access "+
				" FROM usrgrp g,users_groups ug "+
				" WHERE g.tenantid=#{tenantid} "+
				    " AND ug.userid=#{userId} "+
				   " AND g.tenantid=ug.tenantid "+
				   " AND g.usrgrpid=ug.usrgrpid", 
				params);
		Map dbAccess = !datas.isEmpty() ? datas.get(0) : new IMap();
		Integer guiAccess = (Integer)dbAccess.get("gui_access");
		if(empty(guiAccess)){
			guiAccess = GROUP_GUI_ACCESS_SYSTEM;
		}
		
		if (!loginSuccess) {
			HttpServletRequest request = RadarContext.request();
			String ip = !empty(request.getHeader("HTTP_X_FORWARDED_FOR")) 
					? request.getHeader("HTTP_X_FORWARDED_FOR") 
					: request.getRemoteAddr();
			userInfo.put("attempt_failed", attempt_failed+1);
			params.put("attempt_failed", userInfo.get("attempt_failed"));
			params.put("attempt_clock", time());
			params.put("attempt_ip", ip);
			executor.executeInsertDeleteUpdate(
					"UPDATE users "+
					" SET attempt_failed=#{attempt_failed}, attempt_clock=#{attempt_clock}, attempt_ip=#{attempt_ip} "+
					" WHERE tenantid=#{tenantid}"+
					    " AND userid=#{userId}", 
					params);
			
			add_audit(this.idBean, getSqlExecutor(), AUDIT_ACTION_LOGIN,
					AUDIT_RESOURCE_USER, _s("Login failed \"%s\".", username));
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Login name or password is incorrect."));
		}
		// start session
		String sessionId = MD5(osToken.getId());
		params.put("sessionId", sessionId);
		params.put("lastaccess", time);
		params.put("status", RDA_SESSION_ACTIVE);
		executor.executeInsertDeleteUpdate(
				"INSERT INTO sessions (tenantid,sessionid,userid,lastaccess,status)"+ 
				"VALUES (#{tenantid},#{sessionId},#{userId},#{lastaccess},#{status})", 
				params);
		
		add_audit(this.idBean, getSqlExecutor(), AUDIT_ACTION_LOGIN, AUDIT_RESOURCE_USER, _s("Correct login \"%s\".", username));
		
		Map<String, Object> udata = getUserData(userId);
		udata.put("sessionid", sessionId);
		udata.put("gui_access", guiAccess);
		udata.put("userid", userId);
		udata.put("name", osUser.getUsername());
		udata.put("osUser", osUser);
		udata.put("osTenant", osTenant);
		
		if (attempt_failed>0) {
			executor.executeInsertDeleteUpdate(
					"UPDATE users SET attempt_failed=0 "+
					" WHERE tenantid=#{tenantid}"+
			            " AND userid=#{userId}", 
					params);
		}
		
		CWebUser.data().clear();
		CWebUser.set(udata);
		
		return isset(user, "userData") ? userData() : sessionId;
	}

	private String getOsTenantIdByUsername(String username) {
		if (OS_USERS.isEmpty()) {
			synchronized (OS_USERS) {
				if (OS_USERS.isEmpty()) {
					IaaSClient osClient = getOpenStackClientForAdmin();
					Keystone keystone = osClient.getIdentityClient();
					Users echoUsers = keystone.users().list().execute();
					List<User> osUsers = echoUsers.getList();
					for (User u : osUsers) {
						User osUser = keystone.users().show(u.getId()).execute();
						OS_USERS.put(osUser.getUsername(), osUser);
					}
				}
			}
		}
		if (OS_USERS.containsKey(username)) {
			return OS_USERS.get(username).getTenantId();
		} else {
			return null;
		}
	}

}
