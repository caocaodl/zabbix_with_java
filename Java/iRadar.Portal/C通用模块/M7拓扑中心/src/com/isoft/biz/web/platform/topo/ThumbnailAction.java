package com.isoft.biz.web.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.platform.topo.INodeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IThumbnailHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.common.IaasPageAction;

public class ThumbnailAction  extends IaasPageAction {

    public String doDetail(){
    	String topoId = this.getParameter("topoId");
    	String topoName = this.getParameter("topoName");
		setAttribute("topoId", topoId);
		setAttribute("topoName", topoName);
    	return "detail";
    }
    
    @SuppressWarnings("unchecked")
	public String doXml() throws Exception {
    	RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailXml);
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
    
    public String doTreeIndex() throws Exception {
    	String topoId = this.getParameter("topoId");
    	String topoName = this.getParameter("topoName");
		setAttribute("topoId", topoId);
		setAttribute("topoName", topoName);
		
		return "thumbnailTreeIndex";
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String doTree() throws Exception {
    	String ctxPath = this.getCtxPath();
    	RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailTree);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		DataPage dataPage = new DataPage(true, getPage(), getRows());
		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		param.put("ctxPath", ctxPath);
		paramDTO.setMapParam(param);
		paramDTO.setDataPage(dataPage);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List dataList = dto.getListParam();
		setResultList(dataList);
		return "resultList";
	}

    
	
	@SuppressWarnings("unchecked")
	public String doAdd() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailAdd);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
	
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
		return "resultMap";
	}
	
	@SuppressWarnings("unchecked")
	public String doCheckOper() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailCheckOper);
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
	
	public String doAllCheckOper() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailAllCheckOper);
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
	public String doDel() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailDel);
		request.setModuleName(ModuleConstants.MODULE_TOPO);
		
		ParamDTO paramDTO = new ParamDTO();
		Map<String,Object> param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);
		
		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		getResultMap().put(SUCCESS, dto.getBoolParam());
		getResultMap().put("error", dto.getStrParam());
		return "resultMap";
	}
	
	@SuppressWarnings("unchecked")
	public String doSave() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailSave);
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
	public String doNodeClear() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailNodeClear);
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
	public String doAllNodeClear() throws Exception {
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doThumbnailAllNodeClear);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String doTNodeTbUnchecked() throws Exception {
    	RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doTNodeThumbnailUnchecked);
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
	
	@SuppressWarnings({ "rawtypes" })
	public String doNodeTypeList() throws Exception {
    	RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doNodeTypeList);
		request.setModuleName(ModuleConstants.MODULE_TOPO);

		ParamDTO paramDTO = new ParamDTO();
		Map param = getVo();
		paramDTO.setMapParam(param);
		request.setDTO(paramDTO);

		IResponseEvent response = delegator(request);
		ParamDTO dto = (ParamDTO) response.getDTO();
		List dataList = dto.getListParam();
		setResultList(dataList);
		return "resultList";
	}
	
	@SuppressWarnings("unchecked")
	public String doLineAutoOper()throws Exception{
		RequestEvent request = new RequestEvent();
		request.setCallHandlerIF(IThumbnailHandler.class);
		request.setCallDAOIF(INodeDAO.class);
		request.setCallHandlerMethod(IThumbnailHandler.doLineAutoOper);
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
}
