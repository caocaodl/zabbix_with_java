package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.Map;

import com.isoft.biz.dao.platform.topo.IBackGPicDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IBackGPicHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class BackGPicAction  extends IaasPageAction {

    @SuppressWarnings("unchecked")
	public String doList() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBackGPicHandler.class);
		request.setCallDAOIF(IBackGPicDAO.class);
		request.setCallHandlerMethod(IBackGPicHandler.doBackGPicList);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		param.put("category", "backgroup");
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
	
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
        setResultList(dto.getListParam());
		return "resultList";
	}
	
	@SuppressWarnings("unchecked")
	public String doInit() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBackGPicHandler.class);
		request.setCallDAOIF(IBackGPicDAO.class);
		request.setCallHandlerMethod(IBackGPicHandler.doBackGPicInit);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
	
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultMap((HashMap<String, Object>) dto.getMapParam());
		return "resultMap";
	}
	
	@SuppressWarnings("unchecked")
	public String doChange() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBackGPicHandler.class);
		request.setCallDAOIF(IBackGPicDAO.class);
		request.setCallHandlerMethod(IBackGPicHandler.doBackGPicChange);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
}
