package com.isoft.biz.web.platform.topo;

import java.util.Map;

import com.isoft.biz.dao.platform.topo.INodeDAO;
import com.isoft.biz.dao.platform.topo.IVirtLinkTopoDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IVirtLinkTopoHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class VirtLinkTopoAction extends IaasPageAction{
	
	public String doTenantIndex(){
		return "successtenantindex";
	}
	
	public String doAdminIndex(){
//		Map<String,Object> param = getVo();
//		getRequest().getSession().setAttribute("tenantId", param.get("tenantId"));
		return "successadmin";
	}
	
	@SuppressWarnings("unchecked")
	public String doXml() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IVirtLinkTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IVirtLinkTopoHandler.doVirtLinkTopoXml);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", true);
		getResultMap().put("xml", dto.getStrParam());
		return "resultMap";
	}
	
	public String doGetVirtLinkTopoData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IVirtLinkTopoHandler.class);
		request.setCallDAOIF(IVirtLinkTopoDAO.class);
		request.setCallHandlerMethod(IVirtLinkTopoHandler.doGetVirtLinkTopoData);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().putAll(dto.getMapParam());
		return "resultMap";
	}
	
	public String doGetVirtLinkVMData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IVirtLinkTopoHandler.class);
		request.setCallDAOIF(IVirtLinkTopoDAO.class);
		request.setCallHandlerMethod(IVirtLinkTopoHandler.doGetVirtLinkVMData);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().putAll(dto.getMapParam());
		return "resultMap";
	}
	
	public String doGetTenantData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IVirtLinkTopoHandler.class);
		request.setCallDAOIF(IVirtLinkTopoDAO.class);
		request.setCallHandlerMethod(IVirtLinkTopoHandler.doGetTenantData);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().putAll(dto.getMapParam());
		return "resultMap";
	}

}