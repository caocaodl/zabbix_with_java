package com.isoft.web.bean.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.common.IUserDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IUserHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class UserAction extends IaasPageAction {
	
	public String doRoleTree() {
		String userId = getParameter("userId");
		if(StringUtils.isNotEmpty(userId)){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IUserHandler.class);
			request.setCallDAOIF(IUserDAO.class);
			request.setCallHandlerMethod(IUserHandler.getAllRoleSet);
			request.setModuleName(ModuleConstants.MODULE_COMMON);
	
			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setStrParam(userId);
			request.setDTO(paramDTO);
	
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List dataList = dto.getListParam();
			setResultList(dataList);
		}
		return "roleTree";
	}

	public String doPage() throws Exception {
        RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserPage);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        DataPage dataPage = new DataPage(true, getPage(),getRows());
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        paramDTO.setDataPage(dataPage);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        List dataList = dto.getListParam();
        setResultList(dataList);
        setDataPage(dataPage);
		return JSON;
	}
	
	public String doOperView() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserView);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        List dataList = dto.getListParam();
        if(!dataList.isEmpty()){
        	getResultMap().putAll((Map)dataList.get(0));
        }
        getResultMap().put(SUCCESS, !dataList.isEmpty());
        return "resultMap";
	}

	public String doOperAdd() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserAdd);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}
	
	public String doOperActive() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserActive);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}
	
	public String doOperForbid() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserForbid);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}
	
	public String doOperResume() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserResume);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}

	public String doOperDel() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserDel);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}

	public String doOperEdit() {
		RequestEvent request = new RequestEvent();
        request.setCallHandlerIF(IUserHandler.class);
        request.setCallDAOIF(IUserDAO.class);
        request.setCallHandlerMethod(IUserHandler.doUserEdit);
        request.setModuleName(ModuleConstants.MODULE_COMMON);
        
        ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
        paramDTO.setMapParam(param);
        request.setDTO(paramDTO);

        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
        return "resultMap";
	}
	
	public String doGrantRoles() {
		String userId = getParameter("userId");
		if(StringUtils.isNotEmpty(userId)){
			String[] roleIds = this.getParameterValues("roleId[]");
			
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IUserHandler.class);
			request.setCallDAOIF(IUserDAO.class);
			request.setCallHandlerMethod(IUserHandler.doUserGrantRoles);
			request.setModuleName(ModuleConstants.MODULE_COMMON);

			ParamDTO paramDTO = new ParamDTO();
			Map param = new LinkedMap();
			param.put("userId", userId);
			param.put("roleIds", roleIds);
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			delegator(request);
		}
		return "resultMap";
	}

	public String doOperDisOrg() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IUserHandler.class);
		request.setCallDAOIF(IUserDAO.class);
		request.setCallHandlerMethod(IUserHandler.doUserDisOrg);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
        Map param = getVo();
		paramDTO.setMapParam(param);
        request.setDTO(paramDTO);
		
        IResponseEvent response = delegator(request);
        ParamDTO dto = (ParamDTO)response.getDTO();
        getResultMap().put(SUCCESS, dto.getBoolParam());
        return "resultMap";
	}
}
