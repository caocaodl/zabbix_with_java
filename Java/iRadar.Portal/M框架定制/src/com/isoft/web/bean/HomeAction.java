package com.isoft.web.bean;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGOUT;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.Feature;
import com.isoft.biz.Delegator;
import com.isoft.biz.dao.common.IUserDAO;
import com.isoft.biz.daoimpl.radar.CUserDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IUserHandler;
import com.isoft.biz.method.Role;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.core.utils.StringUtil;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.model.CWebUser;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.CDelegator;
import com.isoft.web.common.IaasPageAction;

public class HomeAction extends IaasPageAction {

	private int status;

	public int getStatus() {
		return status;
	}

	public String index() {
		return "index";
	}
	
	public String checkcode() {
		String checkNumber = getParameter("randomCode");
		String timestamp = getParameter("timestamp");
		String randomCode = (String) getSession().getAttribute("RANDOMVALIDATECODEKEY" + timestamp);
		getResultMap().put("success", checkNumber!=null && checkNumber.toUpperCase().equals(randomCode));
		return "resultMap";
	}

	public String login() {
		cleanLoginInfo();
		this.status = -1;
		String tenant = getParameter("tenant");
		String username = getParameter("username");
		String password = getParameter("password");
		
		String msgKey = null;
		
		if (isNotEmpty(username)
				&& isNotEmpty(password)
				&& isNotEmpty(tenant)) {
			
			if (CWebUser.login(username, password)) {
				this.status = 1;
				Map user = (Map)this.getSession().getAttribute(CWebUser.class.getName());
				
				String tenantId = Nest.value(user, "tenantid").asString();
				boolean islessorTenantId = Feature.defaultTenantId.equals(tenantId);
				
				String userName = Nest.value(user,"alias").asString();
				if(StringUtil.isEmptyStr(userName)) {
					userName = Nest.value(user,"name").asString();
				}
				
				Map uinfo = new HashMap();
				uinfo.put("tenantId", tenantId);
				uinfo.put("osTenantId", "0");
				uinfo.put("tenantRole", islessorTenantId? Role.LESSOR.magic(): Role.TENANT.magic());
				uinfo.put("userId", Nest.value(user,"userid").asString());
				uinfo.put("userName", userName);
				uinfo.put("admin", "Y");
				uinfo.put("osUser", user.get("osUser"));
				this.getIdentityBean().init(uinfo);
				
				this.getSession().setMaxInactiveInterval(1800);//session超时设置为30分钟
				
				msgKey = "Login Success";
			} else {
				msgKey = "Login Failure";
				
				Integer loginErr = (Integer)RadarContext.getContext().define(CUserDAO.KEY_LOGIN);
				if(loginErr != null) {
					this.status = loginErr;
				}
			}
			doAudit(msgKey);
		}
		
		return "login";
	}

	public Map<String,String> getUserData(String userName){
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserViewByName);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        
        Map param = new HashMap();
        param.put("name",userName);
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        List dataList = dto.getListParam();
        Map<String,String> userMap = new HashMap<String,String>();
        if(!dataList.isEmpty()){
        	userMap.putAll((Map)dataList.get(0));
        }
        return userMap;
	}

	public String logout() {
		CDelegator.doDelegate(RadarContext.getIdentityBean(), new Delegator() {
			@Override
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_LOGOUT, AUDIT_RESOURCE_USER, _("Manual Logout"));
				return null;
			}
		});
		
		cleanLoginInfo();
		
		return "logout";
	}
	
	private void cleanLoginInfo() {
		CWebUser.logout();
		
		this.getSession().removeAttribute(CWebUser.class.getName());
		this.clearIdentityBean();
	}
	
	private void doAudit(final String msgKey){
		CDelegator.doDelegate(RadarContext.getIdentityBean(), new Delegator() {
			@Override
			public Object doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_LOGIN, AUDIT_RESOURCE_USER, _(msgKey));
				return null;
			}
		});
	}
}
