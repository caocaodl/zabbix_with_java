package com.isoft.web.bean.common;

import java.util.Map;

import com.isoft.biz.dao.common.IHostTypeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IHostTypeHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class HostTypeAction extends IaasPageAction {

	public Object[] doAdd(Map param) {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IHostTypeHandler.class);
		request.setCallDAOIF(IHostTypeDAO.class);
		request.setCallHandlerMethod(IHostTypeHandler.doAdd);
		request.setModuleName(ModuleConstants.MODULE_COMMON);
		
		ParamDTO paramDTO = new ParamDTO();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		Object[] obj = dto.getArrayParam();
		return obj;
	}
	
	public boolean doUpdate(Map param) {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IHostTypeHandler.class);
		request.setCallDAOIF(IHostTypeDAO.class);
		request.setCallHandlerMethod(IHostTypeHandler.doUpdate);
		request.setModuleName(ModuleConstants.MODULE_COMMON);
		
		ParamDTO paramDTO = new ParamDTO();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		
		return dto.getBoolParam();
	}
	
	public void doDelete(Map param) {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IHostTypeHandler.class);
		request.setCallDAOIF(IHostTypeDAO.class);
		request.setCallHandlerMethod(IHostTypeHandler.doDelete);
		request.setModuleName(ModuleConstants.MODULE_COMMON);
		
		ParamDTO paramDTO = new ParamDTO();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
	}
}
