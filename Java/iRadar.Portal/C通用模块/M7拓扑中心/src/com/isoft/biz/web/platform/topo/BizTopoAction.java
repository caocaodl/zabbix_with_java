package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.Map;

import com.isoft.biz.dao.platform.topo.IBizTopoDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IBizTopoHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.iradar.Cphp;
import com.isoft.web.common.IaasPageAction;

public class BizTopoAction extends IaasPageAction {

	public String doTenantIndex(){
		return "successTenant";
	}

	public String doAdminIndex(){
		return "successAdmin";
	}
	
	@SuppressWarnings("unchecked")
	public String doXml() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTBizXml);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", true);
		getResultMap().put("xml", dto.getStrParam());
		return "resultMap";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String doNodePage() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doNodePage);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		DataPage dataPage = new DataPage(true, getPage(), getRows());
		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		paramDTO.setDataPage(dataPage);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultMap((HashMap<String, Object>) dto.getMapParam());
		return "resultMap";
	}
	
	@SuppressWarnings("unchecked")
	public String doSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTBizSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}

	@SuppressWarnings("unchecked")
	public String doDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTBizDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
	
	@SuppressWarnings("unchecked")
	public String doUpdateG() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTBizNodeUpdateG);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getStrParam());
		return "resultMap";
	}
	
	@SuppressWarnings("unchecked")
	public String doNodeDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTBizNodeDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	
	public String doLineDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTBizLineDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success", dto.getBoolParam());
		return "resultMap";
	}
	

	@SuppressWarnings("unchecked")
	public String doPicList() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String, Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultList(dto.getListParam());
		return "resultList";
	}
	
	public String doGetBizTopoData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doGetBizTopoData);
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
	
	public String doGetBizTopoAllData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doGetBizTopoAllData);
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
	
	public String doTopoDataSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoDataSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("topoId", dto.getStrParam());
		return "resultMap";
	}
	
	public String doTopoDataDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoDataDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("bizTopoId", dto.getStrParam());
		return "resultMap";
	}
	
	public String doTopoDataEdit() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoDataEdit);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("topoId", dto.getStrParam());
		return "resultMap";
	}
	
	public String doTopoBizNodeDataSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizNodeDataSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		paramDTO.setMapParam(getVo());
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("bizNodeId", dto.getStrParam());
		return "resultMap";
	}
	
	public String doTopoBizNodeDataEdit() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizNodeDataEdit);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("bizNodeId", dto.getStrParam());
		return "resultMap";
	}
	
	public String doTopoBizNodeDataDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizNodeDataDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("bizNodeId", dto.getStrParam());
		return "resultMap";
	}
	
	public String doTopoBizDataGet() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizDataGet);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultList().addAll(dto.getListParam());
		return "resultList";
	}
	
	public String doTopoBizHostDataGet() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizHostDataGet);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultList().addAll(dto.getListParam());
		return "resultList";
	}
	
	public String doTopoBizTopoAndNodeDataSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizTopoAndNodeDataSave);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().putAll(dto.getMapParam());
		return "resultMap";
	}
	
	public String doTopoBizTopoAndNodeDataEdit() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizTopoAndNodeDataEdit);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().putAll(dto.getMapParam());
		return "resultMap";
	}

	public String doTopoBizDataGetToAdmin() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doTopoBizDataGetToAdmin);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		request.setDTO(paramDTO);
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultList().addAll(dto.getListParam());
		return "resultList";
	}
	
	public String doGetBizTopoAllDataToAdmin() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IBizTopoHandler.class);
		request.setCallDAOIF(IBizTopoDAO.class);
		request.setCallHandlerMethod(IBizTopoHandler.doGetBizTopoAllDataToAdmin);
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
