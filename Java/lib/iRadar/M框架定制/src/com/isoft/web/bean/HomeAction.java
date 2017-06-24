package com.isoft.web.bean;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.common.IUserDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IUserHandler;
import com.isoft.biz.method.Role;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.iradar.model.CWebUser;
import com.isoft.types.Mapper.Nest;
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
		logout();
		this.status = -1;
		String tenant = getParameter("tenant");
		String username = getParameter("username");
		String password = getParameter("password");
		
		if (isNotEmpty(username)
				&& isNotEmpty(password)
				&& isNotEmpty(tenant)) {
			
			if (CWebUser.login(username, password)) {
				this.status = 1;
				Map user = (Map)this.getSession().getAttribute(CWebUser.class.getName());
				Map uinfo = new HashMap();
				uinfo.put("tenantId", "0");
				uinfo.put("osTenantId", "0");
				uinfo.put("tenantRole", Role.LESSOR.magic());
				uinfo.put("userId", Nest.value(user,"userid").asString());
				uinfo.put("userName", Nest.value(user,"alias").asString());
				uinfo.put("admin", (Nest.value(user,"type").asInteger()==2 || Nest.value(user,"type").asInteger()==3) ? "Y":"N");
				this.getIdentityBean().init(uinfo);
			}
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
		this.getSession().removeAttribute(CWebUser.class.getName());
		this.clearIdentityBean();
		return "logout";
	}

}
