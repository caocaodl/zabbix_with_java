package com.isoft.biz.handlerimpl.platform.topo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.NodeList;

import prefuse.demos.RadialGraphView;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.BackGPicDAO;
import com.isoft.biz.daoimpl.platform.topo.CloudTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.daoimpl.platform.topo.LineDAO;
import com.isoft.biz.daoimpl.platform.topo.LinkDAO;
import com.isoft.biz.daoimpl.platform.topo.NetTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.NodeDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.INetTopoHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.biz.vo.platform.topo.LineVo;
import com.isoft.biz.vo.platform.topo.LinkVo;
import com.isoft.biz.vo.platform.topo.NodeVo;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.util.Page;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.imon.topo.web.view.TopoNode;
import com.isoft.imon.topo.web.view.TopoView;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.StringUtil;
import com.isoft.types.Mapper.Nest;

public class NetTopoHandler extends BaseLogicHandler implements INetTopoHandler {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNodePage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		int currentPage = Integer.valueOf(param.get("currentPage").toString());
		int pageSize = Integer.valueOf(param.get("pageSize").toString());
		String category = (String) param.get("equipmentTypeName");
		String searchName = (String) param.get("searchName");
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		NodeDAO idao = (NodeDAO) dao;
		List<Integer> hostIds = idao.doHostIdByTopoId(null, param);
		Map<Integer, TopoNode> nodes = TopoView.getInstance().getNodes();
		for (TopoNode topoNode : nodes.values()) {
			param.put("hostId", topoNode.getId());
			List<Map> hostInIradar = idao.doNodeGetHostIdInIradar(param);
			if((!Cphp.empty(hostInIradar))&&Cphp.count(hostInIradar)>0){
				topoNode.setNameInIradar(Nest.as(Cphp.empty(hostInIradar.get(0).get("name"))?hostInIradar.get(0).get("host"):hostInIradar.get(0).get("name")).asString());
			}
			int key = topoNode.getId();
			if (!hostIds.contains(key)) {
				if (StringUtil.isEmptyStr(category)
						&& StringUtil.isEmptyStr(searchName)) {
					resultList.add(topoNode.toJSON());
					continue;
				}
				boolean categoryFlag = false;
				boolean searchNameFlag = false;
				if (!StringUtil.isEmptyStr(category)) {
					if("all".equals(category)){
						categoryFlag = true;
					}else if (topoNode.getCategory().equals(category)) {
						categoryFlag = true;
					}
				}
				if (!StringUtil.isEmptyStr(searchName)) {
					if (topoNode.getSearchName().contains(searchName)) {
						searchNameFlag = true;
					}
				}else{
					searchNameFlag = true;
				}
				if (categoryFlag && searchNameFlag) {
					resultList.add(topoNode.toJSON());
				}
			}
		}

		Page page = new Page(resultList);
		page.setCurrentPage(currentPage);
		page.setPageRecords(pageSize);
		List subResutlList = page.getPage(currentPage);

		resultMap.put("pageSize", pageSize);
		resultMap.put("currentPage", page.getCurrentPage());
//		resultMap.put("totalCount", page.getTotalPage());
		resultMap.put("totalResult", page.getTotalRecord());
		resultMap.put("rows", subResutlList);

		ParamDTO dto = new ParamDTO();
		dto.setMapParam(resultMap);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doLineAutoOper(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		SQLExecutor executor = dao.getSqlExecutor();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		String xml = (String) param.get("xml");
		xml = TopoUtil.translateXml(xml);
		NodeList nodeList = TopoUtil.getXmlNodeList(xml);
		List<NodeVo> nodes = TopoUtil.nodeListToNodeVos(nodeList);
		List<NodeVo> syncNodes = syncLine(nodes, param, executor);
		String resultXml = TopoUtil.nodesToXml(syncNodes,dao);

		ParamDTO dto = new ParamDTO();
		dto.setStrParam(resultXml);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNodeDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		SQLExecutor executor = dao.getSqlExecutor();
		boolean flag = false;
		NodeDAO idao = (NodeDAO) dao;
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		String nodeIds = (String) param.get("nodeIds");
		String[] ids = nodeIds.split(",");
		for (String id : ids) {
			param.put("nodeId", id);
			NodeVo vo = idao.doTNodeLoadById(param);
			param.put("toNode", vo.getName());
			flag = idao.doTNodeDel(param);
			if (flag) {
				if (vo.getTagName().equals("thumbnail")) {
					// 删除和节点相关的线路
					LineDAO lineDao = new LineDAO(executor);
					param.put("tabnailId", vo.getNodeId());
					lineDao.doLineDelByNodeIdOrToNode(param);

					// 释放所属的缩略图内的节点
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
					tempMap.put("oldTbnailId", vo.getNodeId());
					tempMap.put("userName", identityBean.getUserName());
					tempMap.put("topoId", vo.getTopoId());
					idao.doTNodeModifyTbnailId(tempMap);
				} else {
					// 删除和节点相关的线路
					LineDAO lineDao = new LineDAO(executor);
					param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
					lineDao.doLineDelByNodeIdOrToNode(param);
				}

			}
		}

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(flag);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNetTopoDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		SQLExecutor executor = dao.getSqlExecutor();
		NodeDAO idao = (NodeDAO) dao;
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		boolean flag = idao.doNetTopoDel(param);
		if (flag) {
			// 删除和节点相关的线路
			LineDAO lineDao = new LineDAO(executor);
			lineDao.doLineDelByTopoId(param);
			//删除相关联的背景图片关系
			BackGPicDAO bpd = new BackGPicDAO(executor);
			bpd.doBackGPicDelByTopoId(param);
		}

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(flag);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNetTopoUpdateG(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();

		Map param = paramDTO.getMapParam();
		param.put("userName", identityBean.getUserName());
		NodeDAO idao = (NodeDAO) dao;
		boolean flag = idao.doTNodeModifyG(param);

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(flag);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNetTopoUpdateLineAttr(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("userName", identityBean.getUserName());

		LineDAO idao = (LineDAO) dao;
		boolean flag = idao.doLineUpdateAttr(param);

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(flag);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes" })
	public IResponseEvent doNetTopoSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		// 保存拓扑图
		saveTopoXMLToDB(param, dao);
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNetTopoXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		String xml = TopoUtil.doGetTopoXml(param, dao);

		ParamDTO dto = new ParamDTO();
		dto.setStrParam(xml);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doCircleLayout(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		NodeDAO idao = (NodeDAO)dao;
		List<NodeVo> nodes = idao.doTNodeList(null, param);
		StringBuffer s = new StringBuffer("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		if(nodes.size()!=0){
			Element chart = new Element("graphml");		
			Element graph = new Element("graph");
			graph.setAttribute("edgedefault","undirected");
			chart.addContent(graph);
			Element key = new Element("key");
			key.setAttribute("id","index");
			key.setAttribute("for","node");
			key.setAttribute("attr.name","index");
			key.setAttribute("attr.type","string");
			graph.addContent(key);
			List<String> hts = new ArrayList<String>();
			for(NodeVo node : nodes){
				Element nd = new Element("node");
				nd.setAttribute("id", node.getNodeId());
				hts.add(node.getNodeId());
				Element data = new Element("data");
				data.setAttribute("key","index");
				data.setText(node.getNodeId());
				nd.addContent(data);
				graph.addContent(nd);
			}
			List<String> allNode=new ArrayList<String>();
			LineDAO lineDao = new LineDAO(dao.getSqlExecutor());
			for(NodeVo node : nodes){
				Map<String,Object> tempMap = new HashMap<String,Object>();
				tempMap.put("nodeId", node.getNodeId());
				List<String> targetNodeIds = lineDao.doTargetNodeIdByNodeId(tempMap);
				for(String targetNodeId : targetNodeIds){
					Element edge = new Element("edge");
					if(hts.contains(node.getNodeId())&&hts.contains(targetNodeId)){
					edge.setAttribute("source",node.getNodeId());
					allNode.add(node.getNodeId());
					edge.setAttribute("target",targetNodeId);
					allNode.add(targetNodeId);
					graph.addContent(edge);}
				}
			}
			
			XMLOutputter XMLOut = new XMLOutputter();  
		    try {
				XMLOut.output(chart, new FileOutputStream(TopoUtil.webRootUrl + "platform/iradar/conf/circleLayout.xml"));
			} catch (FileNotFoundException e) {				
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			    
		    Map<String,Integer> counts=new HashMap<String,Integer>();
		    for (String string : allNode) {
			   if(!counts.containsKey(string)){
				   counts.put(string, 1);
			   }else{
				   counts.put(string, counts.get(string).intValue()+1);
			   }
		    }
		    int maxValue = 0;
	        String maxKey = null;
		    Iterator it = counts.entrySet().iterator();
	        for(int i=0;i<counts.size();i++){
	    	   if(it.hasNext()){
	    		   Map.Entry entry =(Map.Entry)it.next();
		           int value = Integer.parseInt(entry.getValue().toString());
		           if(value > maxValue){
		               maxValue = value;
		               maxKey = entry.getKey().toString();
		           }
	    	   }
	        }
		    
	        Map<String, String> coords = RadialGraphView.coords;	  
		    RadialGraphView.demo(TopoUtil.webRootUrl + "platform/iradar/conf/circleLayout.xml", "index",maxKey);
		    
		    HostExpDAO hostExpDao = new HostExpDAO(dao.getSqlExecutor());
		  
			for (NodeVo node : nodes) {
				String g = "",image = "",category="";;
				if(!node.getCategory().equals("thumbnail")){
					Host host = hostExpDao.doHostExpLoadById(node.getHostId());
					image = node.getImage(host);
					category = host.getCategory();
				}else{
					image = NodeVo.ICON_PATH + "submap.png";
					category = "thumbnail";
				}
				String[] xy = coords.get(node.getNodeId()).split("-");
				g = xy[0]+","+xy[1]+","+node.getG().split(",")[2]+","+node.getG().split(",")[3];
				
				s.append("<" + node.getTagName() + " id='" + node.getNodeId()
						+ "' hostId='" + node.getHostId() + "' tbnailId='"
						+ node.getTbnailId() + "' g='" + g + 
						"' category='" + category +
						"' image='" + image +
						"' name='" + node.getName() + "'>");

				param.put("nodeId", node.getNodeId());
				List<LineVo> lines = lineDao.doLineList(param);
				for (LineVo line : lines) {
					s.append("<" + line.getTagName() + " to='" + line.getToNode()
							+ "' id='" + line.getLineId() + "' strokeweight='"
							+ line.getStrokeWeight() + "' tbnailId='"
							+ line.getTbnailId() + "' g='" + line.getG()
							+ "' name='' color=''>");
					s.append("</" + line.getTagName() + ">");
				}
				s.append("</" + node.getTagName() + ">");
			}
		}
		s.append("</process>");

			    
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(s.toString());
		response.setDTO(dto);
		return response;
	}
	
	/**
	 * 保存拓扑XML到数据库 思路：先清空与缩略图无关的节点和线路， 然后对节点是缩略图的进行更改坐标，其他的直接添加。
	 * 
	 * @param xml
	 */
	@SuppressWarnings("rawtypes")
	public void saveTopoXMLToDB(Map param, IDAO dao) {
		SQLExecutor executor = dao.getSqlExecutor();
		String xml = (String) param.get("xml");
		xml = TopoUtil.translateXml(xml);
		String topoId = (String) param.get("topoId");
		NodeList nodeList = TopoUtil.getXmlNodeList(xml);
		// 清空与缩略图无关的节点和线路
		delNLExcludeThumbanil(topoId, executor);

		List<NodeVo> nodes = TopoUtil.nodeListToNodeVos(nodeList);
		for (NodeVo node : nodes) {
			String nodeId = node.getNodeId();
			String nodeTagName = node.getTagName();

			NodeDAO tNodeDao = new NodeDAO(executor);
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("topoId", topoId);
			paraMap.put("g", node.getG());
			paraMap.put("hostId", node.getHostId());
			paraMap.put("category", node.getCategory());
			paraMap.put("tagName", nodeTagName);
			paraMap.put("name", node.getName());
			paraMap.put("category", node.getCategory());
			if (nodeTagName.equals("thumbnail")) {
				paraMap.put("nodeId", nodeId);
				tNodeDao.doTNodeModifyG(paraMap);
			} else {
				nodeId = tNodeDao.doTNodeAdd(paraMap);
			}
			// 链路的操作
			List<LineVo> lines = node.getLines();
			for (LineVo line : lines) {
				Map<String, Object> lineMap = new HashMap<String, Object>();
				lineMap.put("topoId", topoId);
				lineMap.put("tagName", line.getTagName());
				lineMap.put("g", line.getG());
				lineMap.put("toNode", line.getToNode());
				lineMap.put("nodeId", nodeId);
				lineMap.put("strokeWeight", line.getStrokeWeight());
				lineMap.put("tbnailId", line.getTbnailId());
				LineDAO lineDao = new LineDAO(executor);
				if (nodeTagName.equals("thumbnail")) {
					String lineId = line.getLineId();
					lineMap.put("lineId", lineId);
					if (!lineId.equals("-1")) {
						lineDao.doLineUpdate(lineMap);
					} else {
						lineDao.doLineAdd(lineMap);
					}

				} else {
					lineDao.doLineAdd(lineMap);
				}
			}
		}

	}

	/**
	 * 删除与缩略图无关的节点和线路
	 */
	private void delNLExcludeThumbanil(String topoId, SQLExecutor executor) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("topoId", topoId);
		NodeDAO nodeDao = new NodeDAO(executor);
		// 删除跟缩略图无关的节点
		nodeDao.doDelExcludeNodeOfThumbnail(param);
		// 删除与缩略图无关的线
		LineDAO lineDao = new LineDAO(executor);
		param.put("tbnailId", TopoUtil.INIT_NODE_THUMBNAIL_ID);
		lineDao.doDelExcludeLineOfThumbnail(param);

	}

	/**
	 * 线路同步
	 * 
	 * @param nodes
	 * @param executor
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<NodeVo> syncLine(List<NodeVo> nodes, Map paraMap,
			SQLExecutor executor) {
		List<NodeVo> syncNodes = new ArrayList<NodeVo>();
		LinkDAO linkDao = new LinkDAO(executor);
		NodeDAO nodeDao = new NodeDAO(executor);
		String topoId = (String) paraMap.get("topoId");
		List<NodeVo> wholeNodes = getWholeNode(nodes, paraMap, executor);

		for (NodeVo startNode : nodes) {
			List<LineVo> lines = new ArrayList<LineVo>();
			String hostId = startNode.getHostId();
			String nodeId = startNode.getNodeId();
			String tagName = startNode.getTagName();
			String nodeName = startNode.getName();
			if (!tagName.toLowerCase().equals("thumbnail")) { // 设备节点
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("startId", hostId);
				List<LinkVo> links = linkDao.doLinkList(param);
				if (nodeId.equals("-1")) { // 新增设备节点的情况,存在链路就可以加
					for (LinkVo link : links) {
						String endHostId = String.valueOf(link.getEndId());
						NodeVo endNodeVo = TopoUtil.getNodeVo(endHostId,
								wholeNodes);
						if (endNodeVo != null) {
							// 存在就添加线路
							LineVo line = new LineVo();
							line.setLineId("-1");
							line.setTagName("line");
							line.setNodeId(nodeId);
							line.setTbnailId(TopoUtil.INIT_NODE_THUMBNAIL_ID);

							if (!endNodeVo.getTbnailId().equals(
									TopoUtil.INIT_NODE_THUMBNAIL_ID)) {
								Map<String, Object> tempMap = new HashMap<String, Object>();
								tempMap.put("nodeId", endNodeVo.getTbnailId());
								tempMap.put("topoId", topoId);
								NodeVo tVo = nodeDao.doTNodeLoadById(tempMap);

								line.setToNode(tVo.getName());
							} else {
								line.setToNode(endNodeVo.getName());
							}
				
							if (!TopoUtil.contains(lines, line) && !nodeName.equals(line.getToNode())) {
								lines.add(line);
							}
						}
					}
				} else { // 不是新增的设备的情况
					for (LinkVo link : links) {
						String endHostId = String.valueOf(link.getEndId());
						NodeVo endNodeVo = TopoUtil.getNodeVo(endHostId,
								wholeNodes);
						if (endNodeVo != null) {
							if (!endNodeVo.getTbnailId().equals(
									TopoUtil.INIT_NODE_THUMBNAIL_ID)) { // 隶属于缩略图
								if (endNodeVo.getTbnailId().equals(nodeId)) { // 不允许自连
									continue;
								}
								Map<String, Object> tempMap = new HashMap<String, Object>();
								tempMap.put("nodeId", endNodeVo.getTbnailId());
								tempMap.put("topoId", topoId);
								NodeVo tVo = nodeDao.doTNodeLoadById(tempMap);

								LineVo line = TopoUtil.getLineVo(tVo.getName(),
										startNode.getLines());
								if (line != null) {
									if (!TopoUtil.contains(lines, line)&& !nodeName.equals(line.getToNode())) {
										lines.add(line);
									}
								} else {
									LineVo newLine = new LineVo();
									newLine.setLineId("-1");
									newLine.setTagName("line");
									newLine.setNodeId(nodeId);
									newLine.setToNode(tVo.getName());
									newLine.setTbnailId(TopoUtil.INIT_NODE_THUMBNAIL_ID);
									if (!TopoUtil.contains(lines, newLine)&& !nodeName.equals(newLine.getToNode())) {
										lines.add(newLine);
									}
								}
							} else {
								LineVo line = TopoUtil.getLineVo(
										endNodeVo.getName(),
										startNode.getLines());
								if (line != null) { // 这条链路已经存在，加载存在的链路
									if (!TopoUtil.contains(lines, line)&& !nodeName.equals(line.getToNode())) {
										lines.add(line);
									}
								} else { // 这条链路不存在，添加新的链路
									LineVo newLine = new LineVo();
									newLine.setLineId("-1");
									newLine.setTagName("line");
									newLine.setNodeId(nodeId);
									newLine.setToNode(endNodeVo.getName());
									newLine.setTbnailId(TopoUtil.INIT_NODE_THUMBNAIL_ID);
									if (!TopoUtil.contains(lines, newLine)&& !nodeName.equals(newLine.getToNode())) {
										lines.add(newLine);
									}
								}
							}

						}
					}
				}
			} else { // 缩略图节点
				paraMap.put("tbnailId", nodeId);
				List<NodeVo> nodeVos = nodeDao.doTNodeList(null, paraMap);
				for (NodeVo vo : nodeVos) {
					Map<String, Object> linkMap = new HashMap<String, Object>();
					linkMap.put("startId", vo.getHostId());
					List<LinkVo> linkList = linkDao.doLinkList(linkMap);
					for (LinkVo link : linkList) {
						String endHostId = String.valueOf(link.getEndId());
						NodeVo endNodeVo = TopoUtil.getNodeVo(endHostId,
								wholeNodes);
						if (endNodeVo != null) {
							if (!endNodeVo.getTbnailId().equals(
									TopoUtil.INIT_NODE_THUMBNAIL_ID)) { // 隶属于缩略图内节点
								if (endNodeVo.getTbnailId().equals(nodeId)) { // 不允许自连
									continue;
								}
								Map<String, Object> tempMap = new HashMap<String, Object>();
								tempMap.put("nodeId", endNodeVo.getTbnailId());
								tempMap.put("topoId", topoId);
								NodeVo tVo = nodeDao.doTNodeLoadById(tempMap);

								LineVo line = TopoUtil.getLineVo(tVo.getName(),
										startNode.getLines());
								if (line != null) {
									if (!TopoUtil.contains(lines, line)&& !nodeName.equals(line.getToNode())) {
										lines.add(line);
									}
								} else {
									LineVo newLine = new LineVo();
									newLine.setLineId("-1");
									newLine.setTagName("line");
									newLine.setNodeId(nodeId);
									newLine.setToNode(tVo.getName());
									newLine.setTbnailId(TopoUtil.INIT_NODE_THUMBNAIL_ID);
									if (!TopoUtil.contains(lines, newLine)&& !nodeName.equals(newLine.getToNode())) {
										lines.add(newLine);
									}
								}
							} else { // 单个节点
								LineVo line = TopoUtil.getLineVo(
										endNodeVo.getName(),
										startNode.getLines());
								if (line != null) { // 这条链路已经存在，加载存在的链路
									//判断是否存在从节点到缩略图的连线
									LineVo lvo = TopoUtil.getLineVo(startNode.getName(), endNodeVo.getLines());
									if(lvo==null&& !nodeName.equals(line.getToNode())){
									   lines.add(line);
									}
								} else {
									//判断是否存在从节点到缩略图的连线
									LineVo lvo = TopoUtil.getLineVo(startNode.getName(), endNodeVo.getLines());
									if(lvo==null){
										// 这条链路不存在，添加新的链路
										LineVo newLine = new LineVo();
										newLine.setLineId("-1");
										newLine.setTagName("line");
										newLine.setNodeId(nodeId);
										newLine.setToNode(endNodeVo.getName());
										newLine.setTbnailId(TopoUtil.INIT_NODE_THUMBNAIL_ID);
										if (!TopoUtil.contains(lines, newLine)&& !nodeName.equals(newLine.getToNode())) {
											lines.add(newLine);
										}
									}
								}
							}
						}
					}
					startNode.getLines().addAll(lines);
				}
			}
			startNode.setLines(lines);
			syncNodes.add(startNode);
		}
		return syncNodes;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<NodeVo> getWholeNode(List<NodeVo> nodes, Map paraMap,
			SQLExecutor sqlExecutor) {
		List<NodeVo> wholeNodes = new ArrayList<NodeVo>();
		NodeDAO nodeDao = new NodeDAO(sqlExecutor);
		if (!TopoUtil.containThumbnail(nodes)) {
			return nodes;
		}
		for (NodeVo vo : nodes) {
			if (vo.getTagName().toLowerCase().equals("thumbnail")) {
				paraMap.put("tbnailId", vo.getNodeId());
				List<NodeVo> nvs = nodeDao.doTNodeList(null, paraMap);
				wholeNodes.addAll(nvs);
			} else {
				wholeNodes.add(vo);
			}
		}
		return wholeNodes;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IResponseEvent doTbnailPage(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		NodeDAO idao = (NodeDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		DataPage dataPage = paramDTO.getDataPage();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getOsTenantId());
		param.put("userId", identityBean.getUserId());
		String tbnailId = (String)param.get("tbnailId");
		String checkStatus = (String)param.get("checkStatus");
		List<NodeVo> dataList = new ArrayList<NodeVo>();
		if(tbnailId != null){
			if(checkStatus==null){
				List<NodeVo> thumbnailList = idao.doTNodeList(null, param);
				param.remove("tbnailId");
				dataList = idao.doTNodeListExcluedThumbnail(dataPage, param);
				for(NodeVo node : dataList){
					for(NodeVo thNodeVo:thumbnailList){
						if(thNodeVo.getNodeId().equals(node.getNodeId())){
							node.setChecked(true);
							break;
						}
					}
				}	
			}else if(checkStatus.equals("checked")){
				dataList = idao.doTNodeList(dataPage, param);
				for(NodeVo node : dataList){
					node.setChecked(true);
				}
			}else if(checkStatus.equals("unchecked")){
				dataList = idao.doTNodeThumbnailUnchecked(dataPage, param);
			}		
		}else{
			dataList = idao.doTNodeListExcluedThumbnail(dataPage, param);
		}
		ParamDTO dto = new ParamDTO();
		dto.setListParam(dataList);
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doGetPhyLinkTopoData(IIdentityBean identityBean,IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		NetTopoDAO idao = (NetTopoDAO) dao;
		Map data = idao.doGetPhyLinkTopoData();
		ParamDTO dto = new ParamDTO();
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}

}
