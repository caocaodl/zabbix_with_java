package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.platform.topo.ICloudTopoDAO;
import com.isoft.biz.dao.platform.topo.ILineDAO;
import com.isoft.biz.dao.platform.topo.INetTopoDAO;
import com.isoft.biz.dao.platform.topo.INodeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.ICloudTopoHandler;
import com.isoft.biz.handler.platform.topo.INetTopoHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;
/**
 * 物理链路拓扑
 * @author BT
 *
 */
public class NetTopoAction  extends IaasPageAction {

	public String doPhyIndex(){
		return "successPhy";
	}
	
	public String doPhyDashIndex(){
		return "successDash";
	}
	
	@SuppressWarnings("unchecked")
	public String doXml() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNetTopoXml);
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
	
	@SuppressWarnings("unchecked")
	public String doCircleLayout() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doCircleLayout);
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
	
	@SuppressWarnings("unchecked")
	public String doSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNetTopoSave);
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

	@SuppressWarnings("unchecked")
	public String doUpdateG() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNetTopoUpdateG);
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
	
	@SuppressWarnings("unchecked")
	public String doUpdateLineAttr() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(ILineDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNetTopoUpdateLineAttr);
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
	
	@SuppressWarnings("unchecked")
	public String doLineAutoOper()throws Exception{
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doLineAutoOper);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String doNodePage() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNodePage);
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
	public String doNodeDel()throws Exception{
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNodeDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put("success",dto.getBoolParam());
		return "resultMap";
	}
	
	@SuppressWarnings("unchecked")
	public String doDel()throws Exception{
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doNetTopoDel);
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
	
	/**
	 * 供缩略图管理模块使用
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String doTbnailPage() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doTbnailPage);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

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
	
	public String doGetPhyLinkTopoData() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(INetTopoHandler.class);
		request.setCallDAOIF(INetTopoDAO.class);
		request.setCallHandlerMethod(INetTopoHandler.doGetPhyLinkTopoData);
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
