package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.daoimpl.platform.topo.LineDAO;
import com.isoft.biz.daoimpl.platform.topo.LinkDAO;
import com.isoft.biz.daoimpl.platform.topo.NodeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IThumbnailHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.biz.vo.platform.topo.LineVo;
import com.isoft.biz.vo.platform.topo.LinkVo;
import com.isoft.biz.vo.platform.topo.NodeVo;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.util.Page;
import com.isoft.imon.topo.util.TopoUtil;


public class ThumbnailHandler extends BaseLogicHandler implements IThumbnailHandler {

	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doThumbnailXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		String xml = TopoUtil.doGetTopoXml(param,dao);

		ParamDTO dto = new ParamDTO();
		dto.setStrParam(xml);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doThumbnailNodeClear(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		NodeDAO idao = (NodeDAO)dao;
		Map param = paramDTO.getMapParam();
		param.put("userName", identityBean.getUserName());
		String oldTbnailId = (String)param.get("tbnailId");
		param.put("oldTbnailId",oldTbnailId);
		param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		String nodeIds = (String)param.get("nodeIds");
		String[] ids = nodeIds.split(",");
		for(String nodeId : ids){
			param.put("nodeId", nodeId);
			idao.doTNodeModifyTbnailId(param);
		}

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doThumbnailAllNodeClear(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		NodeDAO idao = (NodeDAO)dao;
		Map param = paramDTO.getMapParam();
		String tbnailName = (String)param.get("tbnailName");
		String oldTbnailId = (String)param.get("tbnailId");
		param.put("oldTbnailId",oldTbnailId);
		param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		param.put("userName", identityBean.getUserName());
		idao.doTNodeModifyTbnailId(param);
		LineDAO lineDao = new LineDAO(dao.getSqlExecutor());
		Map<String,Object> lineMap = new HashMap<String,Object>();
		lineMap.put("nodeId", oldTbnailId);
		lineMap.put("toNode", tbnailName);
		lineDao.doLineDelByNodeIdOrToNode(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doNodeTypeList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		NodeDAO idao = (NodeDAO)dao;
		Map param = paramDTO.getMapParam();
		List<String> categorys = idao.doNodeTypeList(param);
		List<Map> resultList = new ArrayList<Map>();
		for(String category : categorys){
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("name", category);
			tempMap.put("text", HostConstants.categoryMap.get(category));
			resultList.add(tempMap);
		}
		
		ParamDTO dto = new ParamDTO();
		dto.setListParam(resultList);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doTNodeThumbnailUnchecked(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map> resultList = new ArrayList<Map>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		int currentPage = Integer.valueOf(param.get("currentPage").toString());
		int pageSize    = Integer.valueOf(param.get("pageSize").toString());
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		HostExpDAO hostDao = new HostExpDAO(dao.getSqlExecutor());
		NodeDAO idao = (NodeDAO)dao;
	    List<NodeVo> nodes = idao.doTNodeThumbnailUnchecked(null, param);
	    for(NodeVo vo : nodes){
	    	Map<String,Object> tempMap = new HashMap<String,Object>();
	    	tempMap.put("id", vo.getNodeId());
	    	tempMap.put("hostId", vo.getHostId());
	    	tempMap.put("tagName", vo.getTagName());
	    	tempMap.put("category", vo.getCategory());
	    	tempMap.put("name", vo.getName());
	    	tempMap.put("searchName", vo.getName());
	    	tempMap.put("tbnailId", vo.getTbnailId());
	    	Host host = hostDao.doHostExpLoadById(vo.getHostId());
	    	tempMap.put("image",vo.getImage(host));
	    	tempMap.put("width", vo.getG().split(",")[2]);
	    	tempMap.put("height", vo.getG().split(",")[3]);
	    	resultList.add(tempMap);
	    }
	    
	    Page page = new Page(resultList);
	    page.setCurrentPage(currentPage);
	    page.setPageRecords(pageSize);
	    List subResutlList = page.getPage(currentPage);
	  
	    resultMap.put("pageSize", pageSize);
	    resultMap.put("currentPage", page.getCurrentPage());
	    resultMap.put("totalCount", page.getTotalPage());
		resultMap.put("rows", subResutlList);
		
		ParamDTO dto = new ParamDTO();
		dto.setMapParam(resultMap);
		response.setDTO(dto);	
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doThumbnailSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String topoId = (String)param.get("topoId");
		String oldTbnailId = (String)param.get("tbnailId");
		param.put("oldTbnailId",oldTbnailId);
		
		NodeDAO idao = (NodeDAO)dao;
		idao.doTNodeModifyTbnailId(param);
		
		LineDAO lineDao = new LineDAO(idao.getSqlExecutor());
		
		String xml = (String)param.get("xml");
		xml = TopoUtil.translateXml(xml);
		NodeList nodeList = TopoUtil.getXmlNodeList(xml);
		List<NodeVo> nodes = TopoUtil.nodeListToNodeVos(nodeList);
		Map<String,Object> nodeMap = new HashMap<String,Object>();
		for(NodeVo nodeVo : nodes){
			String nodeId = nodeVo.getNodeId();
			nodeMap.put("nodeId", nodeId);
			nodeMap.put("thumbnailId", oldTbnailId);
			nodeMap.put("topoId", topoId);
			nodeMap.put("g", nodeVo.getG());
			nodeMap.put("userName", identityBean.getUserName());
			idao.doThumbnailUpdateTbnailIdG(nodeMap);
			
			List<LineVo> lines = nodeVo.getLines();
			for(LineVo line: lines){
				String toNode = line.getToNode();
				Map<String, Object> lineMap = new HashMap<String, Object>();
				lineMap.put("topoId", topoId);
				lineMap.put("nodeId", nodeId);
				lineMap.put("tbnailId", oldTbnailId);
				lineMap.put("toNode", toNode);
				lineMap.put("g", line.getG());
				lineMap.put("tagName", line.getTagName());
				lineMap.put("strokeWeight", line.getStrokeWeight());
				lineMap.put("tenantId", identityBean.getTenantId());
				lineMap.put("userId", identityBean.getUserId());
				lineMap.put("userName", identityBean.getUserName());
				if(lineDao.doTNodeDuplicateCheck(lineMap)==0){
				   lineDao.doLineAdd(lineMap);
				}else{
					lineMap.put("lineId", line.getLineId());
					lineDao.doLineUpdate(lineMap);
				}
			}
		}
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IResponseEvent doThumbnailTree(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		NodeDAO idao = (NodeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String ctxPath = (String)param.get("ctxPath");
		param.put("tagName", "thumbnail");
		param.put("tenantId", identityBean.getOsTenantId());
		param.put("userId", identityBean.getUserId());
		ParamDTO dto = new ParamDTO();
		List<NodeVo> dataList = idao.doTNodeThumbnailTree(param);
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(NodeVo node : dataList){
			Map<String,Object> map  = new HashMap<String,Object>();
			map.put("id", node.getNodeId());
			map.put("pId", node.getTopoId());
			map.put("name", node.getName());
			map.put("icon",ctxPath+"/assets/icons/tree_module.png");
			map.put("isParent", false);
		    resultList.add(map);	
		}
		dto.setListParam(resultList);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doThumbnailAdd(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		NodeDAO idao = (NodeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getOsTenantId());
		param.put("userId", identityBean.getUserId());
		param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		param.put("tagName", "thumbnail");
		param.put("category", "thumbnail");
		param.put("g", "0,0,50,30");
		String result = idao.doTNodeAdd(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result!=null?true:false);
		dto.setStrParam(result==null?"缩略图名称重复！":"");
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doThumbnailCheckOper(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		NodeDAO idao = (NodeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String userName = identityBean.getUserName();
		param.put("userName", userName);
		idao.doThumbnailCheckOper(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doThumbnailAllCheckOper(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		NodeDAO idao = (NodeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String userName = identityBean.getUserName();
		param.put("userName", userName);
		idao.doThumbnailAllCheckOper(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doThumbnailDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		NodeDAO idao = (NodeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
	    boolean flag = idao.doTNodeDel(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(flag);
		dto.setStrParam(flag==false?"缩略图删除失败":"");
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doLineAutoOper(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		SQLExecutor executor = dao.getSqlExecutor();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		String xml = (String)param.get("xml");
		xml = TopoUtil.translateXml(xml);
		NodeList nodeList = TopoUtil.getXmlNodeList(xml);
		List<NodeVo> nodes = TopoUtil.nodeListToNodeVos(nodeList);
		boolean flag = false;
		for(NodeVo vo : nodes){
			if(HostConstants.isGenericNetworkDevice(vo.getCategory())){
				flag = true;
				break;
			}
		}
		if(flag){ //存在网络设备
			List<NodeVo> syncNodes = syncLine(nodes,executor);
			xml = TopoUtil.nodesToXml(syncNodes,dao);
		}
				
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(xml);
		response.setDTO(dto);	
		return response;
	}

	/**
	 * 线路同步
	 * @param nodes
	 * @param executor
	 * @return
	 */
	private List<NodeVo> syncLine(List<NodeVo> nodes,SQLExecutor executor){
		List<NodeVo> syncNodes = new ArrayList<NodeVo>();
		LinkDAO linkDao = new LinkDAO(executor);
		for(NodeVo startNode : nodes){
			List<LineVo> lines = new ArrayList<LineVo>();
			String hostId = startNode.getHostId();
			String nodeId = startNode.getNodeId();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("startId", hostId);
			List<LinkVo> links = linkDao.doLinkList(param);
			for(LinkVo link : links){
			    String endHostId = String.valueOf(link.getEndId());
			    NodeVo endNode = TopoUtil.getNodeVo(endHostId, nodes);
			    if(endNode != null){
			    	LineVo line = TopoUtil.getLineVo(endNode.getName(),startNode.getLines());
			    	if(line!=null){  //这条链路已经存在，加载存在的链路
			    		lines.add(line);
			    	}else{  //这条链路不存在，添加新的链路
			    		LineVo newLine = new LineVo();
			    		newLine.setLineId("-1");
			    		newLine.setTagName("line");
			    		newLine.setNodeId(nodeId);
			    		newLine.setTbnailId(startNode.getTbnailId());
			    		newLine.setToNode(endNode.getName());
						lines.add(newLine);
			    	}
			    }
			  } 
			startNode.setLines(lines);
			syncNodes.add(startNode);
		}
	   	return syncNodes;
	}
}
