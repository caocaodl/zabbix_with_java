package com.isoft.web.bean.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.common.IRoleDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IRoleHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class RoleAction extends IaasPageAction {

	public String doFuncTree() {
		String roleId = getParameter("roleId");
		if(StringUtils.isNotEmpty(roleId)){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IRoleHandler.class);
			request.setCallDAOIF(IRoleDAO.class);
			request.setCallHandlerMethod(IRoleHandler.getAllFuncSet);
			request.setModuleName(ModuleConstants.MODULE_COMMON);
	
			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setStrParam(roleId);
			request.setDTO(paramDTO);
	
			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List dataList = dto.getListParam();
			setResultList(dataList);
		}
		return "funcTree";
	}

	public String doPage() throws Exception {

		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IRoleHandler.class);
		request.setCallDAOIF(IRoleDAO.class);
		request.setCallHandlerMethod(IRoleHandler.doRolePage);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		DataPage dataPage = new DataPage(true, getPage(), getRows());
		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		paramDTO.setDataPage(dataPage);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List dataList = dto.getListParam();
		setResultList(dataList);
		setDataPage(dataPage);
		return JSON;
	}

	public String doOperAdd() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IRoleHandler.class);
		request.setCallDAOIF(IRoleDAO.class);
		request.setCallHandlerMethod(IRoleHandler.doRoleAdd);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
		return "resultMap";
	}

	public String doOperDel() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IRoleHandler.class);
		request.setCallDAOIF(IRoleDAO.class);
		request.setCallHandlerMethod(IRoleHandler.doRoleDel);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
		return "resultMap";
	}

	public String doOperEdit() {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IRoleHandler.class);
		request.setCallDAOIF(IRoleDAO.class);
		request.setCallHandlerMethod(IRoleHandler.doRoleEdit);
		request.setModuleName(ModuleConstants.MODULE_COMMON);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
		return "resultMap";
	}
	
	public String doGetFuncs() {
		String roleId = getParameter("roleId");
		if(StringUtils.isNotEmpty(roleId)){
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IRoleHandler.class);
			request.setCallDAOIF(IRoleDAO.class);
			request.setCallHandlerMethod(IRoleHandler.getFuncs);
			request.setModuleName(ModuleConstants.MODULE_COMMON);

			ParamDTO paramDTO = new ParamDTO();
			paramDTO.setStrParam(roleId);
			request.setDTO(paramDTO);

			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			List results = dto.getListParam();
			setResultList(results);
		}		
		return "resultList";
	}
	
	public String doGrantFuncs() {
		String roleId = getParameter("roleId");
		if(StringUtils.isNotEmpty(roleId)){
			String[] funcIds = this.getParameterValues("funcId[]");
			
			RequestEvent request = new RequestEvent();
			request.setCallHandlerIF(IRoleHandler.class);
			request.setCallDAOIF(IRoleDAO.class);
			request.setCallHandlerMethod(IRoleHandler.doRoleGrantFuncs);
			request.setModuleName(ModuleConstants.MODULE_COMMON);

			ParamDTO paramDTO = new ParamDTO();
			Map param = new LinkedMap();
			param.put("roleId", roleId);
			param.put("funcIds", funcIds);
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);
			delegator(request);
		}
		return "resultMap";
	}
}
