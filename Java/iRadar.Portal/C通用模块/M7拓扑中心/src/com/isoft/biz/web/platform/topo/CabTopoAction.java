package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.Map;

import com.isoft.biz.dao.platform.topo.ICabTopoDAO;
import com.isoft.biz.dao.platform.topo.INetTopoDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.ICabTopoHandler;
import com.isoft.biz.handler.platform.topo.INetTopoHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
/**
 * 机房拓扑
 * @author BT
 *
 */
public class CabTopoAction extends IaasPageAction {

	public String doTopoIndex(){
		return "successCab";
	}
	
	public String doCabHostIndex(){
		return "successCabHost";
	}
	
	@SuppressWarnings("unchecked")
	public String doXml() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doCabTopoXml);
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
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doNodePage);
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
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doTCabTopoNodeSave);
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
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doTCabTopoDel);
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
	public String doNodeDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doNodeDel);
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
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doCabTopoUpdateG);
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
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doPicList);
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

	public String doGetRoomCabData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doGetRoomData);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultList(dto.getListParam());
		return "resultList";
	}
	
	public String doGetCabData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doGetCabData);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		setResultList(dto.getListParam());
		return "resultList";
	}
	
	public String doGetCabTopoData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(ICabTopoHandler.class);
		request.setCallDAOIF(ICabTopoDAO.class);
		request.setCallHandlerMethod(ICabTopoHandler.doGetCabTopoData);
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
