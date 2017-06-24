package com.isoft.web.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.common.IUserDAO;
import com.isoft.biz.dao.home.ILoginDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IUserHandler;
import com.isoft.biz.handler.home.ILoginHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class HomeAction extends IaasPageAction {

	private int status;

	public int getStatus() {
		return status;
	}

	public String index() {
		return "index";
	}

	public String login() {
		logout();
		String tenant = getParameter("tenant");
		String username = getParameter("username");
		String password = getParameter("password");
		
		if (StringUtils.isNotEmpty(username)
				&& StringUtils.isNotEmpty(password)
				&& StringUtils.isNotEmpty(tenant)) {
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(ILoginHandler.class);
			request.setCallDAOIF(ILoginDAO.class);
			request.setCallHandlerMethod(ILoginHandler.METHOD_DOLOGIN);
			request.setModuleName(ModuleConstants.MODULE_HOME);

			ParamDTO paramDTO = new ParamDTO();
			Map param = new LinkedMap();
			param.put("tenant", tenant);
			param.put("username", username);
			param.put("password", password);
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);

			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			this.status = dto.getIntParam();
			if (this.status == 1) {
				this.getIdentityBean().init(dto.getMapParam());
			}
		}
		return "login";
	}

	public Map<String,String> getUserData(String userName){
			System.out.println(userName);
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
		clearIdentityBean();
		return "logout";
	}

}
