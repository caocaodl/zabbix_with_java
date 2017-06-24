package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LinkedMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.isoft.Feature;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.BizLineDAO;
import com.isoft.biz.daoimpl.platform.topo.BizTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.daoimpl.platform.topo.TPicDAO;
import com.isoft.biz.daoimpl.platform.topo.TopoDataOperDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.IBizTopoHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.biz.vo.platform.topo.TBizLine;
import com.isoft.biz.vo.platform.topo.TBizNode;
import com.isoft.biz.vo.platform.topo.TPic;
import com.isoft.biz.web.platform.topo.TopoDataOperAction;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.nova.model.Hypervisors;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Hypervisor;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Server;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.util.Page;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.StringUtil;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


public class BizTopoHandler extends BaseLogicHandler implements IBizTopoHandler {

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
		List<Long> groupsList = null;
		if(Cphp.empty(category)||"all".equals(category)){
			groupsList = TopoUtil.tenantGroupsCA.toList();
		}else{
			groupsList = new ArrayList();
			groupsList.add(Long.valueOf(category));
		}
		param.put("groupid", groupsList);
		BizTopoDAO idao = (BizTopoDAO) dao;
		List<Integer> hostIds = idao.doHostIdByTopoId(param);
		
		HostExpDAO hostExpDao = new HostExpDAO(dao.getSqlExecutor());
		List<Map> hosts = hostExpDao.doAssetsHostList(param);
       	
		for(Map host:hosts){
			String hostId = Nest.value(host, "hostId").asString();
			String ip = Nest.value(host, "ip").asString();
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("hostId", hostId);
			String elementType = hostExpDao.doAssetsCategoryByHostId(tempMap);
			host.put("category", elementType);
		    if (!hostIds.contains(hostId)) {
				if (StringUtil.isEmptyStr(category)
						&& StringUtil.isEmptyStr(searchName)) {
					resultList.add(TopoUtil.toJson(host));
					continue;
				}
				boolean categoryFlag = false;
				boolean searchNameFlag = false;
				if (!StringUtil.isEmptyStr(category)) {
					if("all".equals(category)){
						categoryFlag = true;
					}else if (elementType.equals(category)) {
						categoryFlag = true;
					}
				}
				if (!StringUtil.isEmptyStr(searchName)) {
					if (Nest.value(host, "host").asString().contains(searchName)) {
						searchNameFlag = true;
					}
				}else{
					searchNameFlag = true;
				}
				if (categoryFlag && searchNameFlag) {
					resultList.add(TopoUtil.toJson(host));
				}
			}
		}

		Page page = new Page(resultList);
		page.setCurrentPage(currentPage);
		page.setPageRecords(pageSize);
		List subResutlList = page.getPage(currentPage);

		resultMap.put("pageSize", pageSize);
//		resultMap.put("currentPage", page.getCurrentPage());
		resultMap.put("totalResult", page.getTotalRecord());
		resultMap.put("totalCount", page.getTotalPage());
		resultMap.put("rows", subResutlList);

		ParamDTO dto = new ParamDTO();
		dto.setMapParam(resultMap);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doTBizDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		idao.doTBizNodeDelByTopoId(param);

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doTBizNodeDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String nodeIds = (String)param.get("nodeIds");
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
	    String[] ids = nodeIds.split(",");
	    for(String id : ids){
	    	param.put("nodeId", id);
	    	idao.doBizNodeDelByNodeId(param);	    	
 	    }

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTBizLineDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String lineId = (String)param.get("lineId");
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
	    idao.doBizLineDelByLineId(param);	    	

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doTBizNodeUpdateG(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String result = idao.doBizUpdateG(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(result);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doTBizXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		List<TBizNode> nodes = idao.doBizTopoList(param);
		String xml = nodesToXml(nodes,dao);
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(xml);
		response.setDTO(dto);
		return response;
	}
	
	private String nodesToXml(List<TBizNode> nodes,IDAO dao){
		StringBuilder s = new StringBuilder();
		s.append("<process xmlns='http://jbpm.org/4.4/jpdl'>");
		HostExpDAO hostExpDao = new HostExpDAO(dao.getSqlExecutor());
		for (TBizNode node : nodes) {
			String image = TopoUtil.getCloudHostImage(new Object());
//			if (node.getHostId()!=null&&!"".equals(node.getHostId())) {
//				Host host = hostExpDao.doHostExpLoadById(node.getHostId());
//				image = node.getImage(host);
//			} 
			s.append("<" + node.getTagName() + " id='" + node.getNodeId()
					+ "' hostId='" + node.getHostId()+ "' remark='" 
					+ node.getRemark()+ "' priority='" + node.getPriority()
					+ "' g='" + node.getG()
					+ "' strokeweight='" + node.getStrokeweight()
					+ "' fill='" + node.getFill()
					+ "' stroke='" + node.getStroke()
					+ "' type='" + node.getType()
					+ "' ownerHost='" + node.getOwnerHost()
					+ "' image='" + image + "' name='" + node.getName() + "'>");
			List<TBizLine> lines = node.getLines();
			for (TBizLine line : lines) {
				s.append("<" + line.getTagName() + " to='" + line.getToNode()
						+ "' id='" + line.getLineId() + "' strokeweight='"
						+ line.getStrokeWeight() + "' nodeId='"
						+ line.getNodeId() + "' g='" + line.getG()
						+ "' name='"+line.getName()+"' color=''>");
				s.append("</" + line.getTagName() + ">");
			}
			s.append("</" + node.getTagName() + ">");
		}
		s.append("</process>");
		return s.toString();
	}
	
	private List<TBizNode> listToTBizNodes(NodeList nodeList){
		List<TBizNode> nodes = new ArrayList<TBizNode>();
		for (int i = 0, imax = nodeList.getLength(); i < imax; i++) {
			Node topoNode = nodeList.item(i);
			if (!topoNode.hasAttributes()) {
				continue;
			}
			NamedNodeMap attrs = topoNode.getAttributes();
			String nodeId = attrs.getNamedItem("id").getTextContent();
			String remark = "";
			if (attrs.getNamedItem("remark") != null) {
				remark = attrs.getNamedItem("remark").getTextContent();
			}
			String priority = "";
			if (attrs.getNamedItem("priority") != null) {
				priority = attrs.getNamedItem("priority").getTextContent();
			}
			String hostId = "";
			if (attrs.getNamedItem("hostId") != null) {
				hostId = attrs.getNamedItem("hostId").getTextContent();
			}
			TBizNode tBizNode = new TBizNode();
			tBizNode.setNodeId(nodeId);
			
			tBizNode.setHostId(hostId);
			tBizNode.setPriority(priority);
			tBizNode.setG(attrs.getNamedItem("g").getTextContent());
			tBizNode.setTagName(topoNode.getNodeName());
			tBizNode.setRemark(remark);
			tBizNode.setName(attrs.getNamedItem("name").getTextContent());
			if(attrs.getNamedItem("strokeweight")!=null){
				tBizNode.setStrokeweight(attrs.getNamedItem("strokeweight").getTextContent());
			}
			if(attrs.getNamedItem("stroke")!=null){
				tBizNode.setStroke(attrs.getNamedItem("stroke").getTextContent());
			}
			if(attrs.getNamedItem("fill")!=null){
				tBizNode.setFill(attrs.getNamedItem("fill").getTextContent());
			}
			
			if (topoNode.hasChildNodes()) {
				List<TBizLine> lines = new ArrayList<TBizLine>();
				NodeList lineList = topoNode.getChildNodes();
				for (int j = 0, jmax = lineList.getLength(); j < jmax; j++) {
					Node lineNode = lineList.item(j);
					if (!lineNode.hasAttributes()) {
						continue;
					}
					NamedNodeMap childAttrs = lineNode.getAttributes();
					TBizLine line = new TBizLine();
					String strokeWeight =  "";
					if(childAttrs.getNamedItem("strokeweight")!=null){
						strokeWeight = childAttrs.getNamedItem("strokeweight").getTextContent();
					}
					line.setNodeId(nodeId);
					line.setName(childAttrs.getNamedItem("name").getTextContent());
					line.setStrokeWeight(strokeWeight);
					line.setTagName(lineNode.getNodeName());
					line.setToNode(childAttrs.getNamedItem("to")
							.getTextContent());
					if (childAttrs.getNamedItem("g") != null) {
						line.setG(childAttrs.getNamedItem("g")
								.getTextContent());
					}
					lines.add(line);
				}
				tBizNode.setLines(lines);
			}
			nodes.add(tBizNode);
		}
		return nodes;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doTBizSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		String xml = (String)param.get("xml");
		xml = TopoUtil.translateXml(xml);
		String topoId = (String)param.get("topoId");
		NodeList nodeList = TopoUtil.getXmlNodeList(xml);
		List<TBizNode> nodes = listToTBizNodes(nodeList);
		//清空表数据
		idao.doTBizNodeDelByTopoId(param);
        BizLineDAO lineDao = new BizLineDAO(dao.getSqlExecutor());
		for(TBizNode node : nodes) {
			Map<String,Object> paraMap = new HashMap<String, Object>();
			String tagName = node.getTagName();
			paraMap.put("topoId", topoId);
			paraMap.put("hostId", node.getHostId());
			paraMap.put("priority",node.getPriority());
			paraMap.put("g", node.getG());
			paraMap.put("strokeweight", node.getStrokeweight());
			paraMap.put("fill", node.getFill());
			paraMap.put("stroke", node.getStroke());
			paraMap.put("tagName", tagName);
			paraMap.put("name", node.getName());
			paraMap.put("remark", node.getRemark());
			paraMap.put("userId", identityBean.getUserId());
			paraMap.put("tenantId", identityBean.getTenantId());
			paraMap.put("createdUser", identityBean.getUserName());
			String nodeId = idao.doTBizNodeAdd(paraMap);
			List<TBizLine> lines = node.getLines();
			if(lines != null){
				for(TBizLine line : lines){
					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("nodeId", nodeId);
					tempMap.put("topoId", topoId);
					tempMap.put("tagName", line.getTagName());
					tempMap.put("strokeWeight", line.getStrokeWeight());
					tempMap.put("toNode", line.getToNode());
					tempMap.put("name", line.getName());
					tempMap.put("userId", identityBean.getUserId());
					tempMap.put("tenantId", identityBean.getTenantId());
					tempMap.put("createdUser", identityBean.getUserName());
					lineDao.doLineAdd(tempMap);
				}
			}
		}
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public IResponseEvent doPicList(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
	    TPicDAO tpicDao = new TPicDAO(dao.getSqlExecutor());
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		List<TPic> tpics = tpicDao.doTPicList(param);
		for(TPic tpic : tpics){
			resultList.add(tpic.toJson());
		}
		ParamDTO dto = new ParamDTO();
		dto.setListParam(resultList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetBizTopoData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		Map<String,List> data = new HashMap();
		List<Map> nodes = new ArrayList();
		List<Map> edges = new ArrayList();
		BizTopoDAO idao = (BizTopoDAO) dao;
		List<Map> hosts = idao.doGetBizTopoData(Feature.defaultTenantId.equals(identityBean.getTenantId()));
		List<Map> bizNodeHostList = idao.doGetBizNodeHost(param);
		CArray<String> bizNodeHostIds = FuncsUtil.rda_objectValues(bizNodeHostList, "hostId");
		Map bizNodeHostMap = new LinkedMap();
		List<Map> bizNodeList = new ArrayList();
		for(Map bizNodeHost:bizNodeHostList){
			bizNodeHostMap.put(bizNodeHost.get("hostId"), bizNodeHost);
			if(TopoDataOperAction.NODE_BIZNODE.equals(Nest.value(bizNodeHost, "nodeType").asString())){
				bizNodeList.add(bizNodeHost);
				Map bizNodeMap = new LinkedMap();
				bizNodeMap.put("id", Nest.value(bizNodeHost, "hostId").asString());
				bizNodeMap.put("name", Nest.value(bizNodeHost, "bizNodeName").asString());
				bizNodeMap.put("nodeType", Nest.value(bizNodeHost, "nodeType").asString());
				bizNodeMap.put("hostType", HostConstants.CATEGORY_BIZNODE);
				nodes.add(bizNodeMap);
			}
		}
		CArray<String> bizNodeIdList = FuncsUtil.rda_objectValues(bizNodeList, "hostId");
		for(Map host:hosts){
			if(bizNodeHostIds.containsValue(Nest.value(host, "id").asString())){
				String hostType = "";
				Map node = new HashMap();
				node.put("name", host.get("name"));
				node.put("id", host.get("id"));
				if(!Cphp.empty(Nest.value(host,"type").asString())){
					if(IMonConsts.A_TYPE_MYSQL == Nest.value(host, "type").asInteger())
						hostType = HostConstants.CATEGORY_MYSQL;
					else if(IMonConsts.A_TYPE_TOMCAT == Nest.value(host, "type").asInteger())
						hostType = HostConstants.CATEGORY_TOMCAT;
				}else
					hostType = HostConstants.CATEGORY_VM;
				node.put("hostType",hostType);
				node.put("nodeType", TopoDataOperAction.NODE_HOST);
				nodes.add(node);
				
				CArray<String> bizNodeIdCA = Nest.as(Nest.value(bizNodeHostMap, Nest.value(host, "id").asString(),"bizNodeId").asString().split(BizTopoDAO.BIZTOPONODEIDSEPERATOR)).asCArray();
				for(String bizNodeId:bizNodeIdList){
					if(bizNodeIdCA.containsValue(bizNodeId)){
						Map edge = new LinkedMap();
						edge.put("id", Nest.value(host, "id").asString().concat("_").concat(bizNodeId));
						edge.put("name", Nest.value(host, "name").asString().concat("_").concat(Nest.value(bizNodeHostMap, Nest.value(host, "id").asString(),"bizNodeName").asString()));
						edge.put("from", bizNodeId);
						edge.put("to", Nest.value(host, "id").asString());
						edges.add(edge);
					}
				}
				
				if(!Cphp.empty(Nest.value(host,"ownerHost").asString())){
					Map edge = new HashMap(4);
					edge.put("id", Nest.value(host, "ownerHost").asString().concat("_").concat(Nest.value(host, "id").asString()));
					edge.put("name", Nest.value(host, "name").asString().concat("_").concat(Nest.value(host, "ownerHost").asString()));
					edge.put("from", Nest.value(host, "id").asString());
					edge.put("to", Nest.value(host, "ownerHost").asString());
					edges.add(edge);
				}
			}
		}
		ParamDTO dto = new ParamDTO();
		doTopoDataSetXY(nodes,idao.getSqlExecutor(),identityBean,Nest.as(param.get("bizTopoId")).asString());
		data.put("nodes", nodes);
		data.put("edges", edges);
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}
	
	public void doTopoDataSetXY(List<Map> nodes,SQLExecutor sqlExecutor,IIdentityBean identityBean,String bizTopoId){
		Map paraMap = new LinkedMap();
		paraMap.put("topoType", TopoDataOperAction.TOPO_BIZ);
		paraMap.put("tenant", identityBean.getTenantId());
		paraMap.put("bizTopoId", bizTopoId);
		TopoDataOperDAO topoDataOperDAO = new TopoDataOperDAO(sqlExecutor);
		Map location = topoDataOperDAO.doBizTopoDataLocOperGet(paraMap);
		for(Map node:nodes){
			if(!Cphp.empty(Nest.value(location, Nest.as(node.get("id")).asString()).$())){
				node.put("X", Nest.value(location, Nest.as(node.get("id")).asString(),"X").asString());
				node.put("Y", Nest.value(location, Nest.as(node.get("id")).asString(),"Y").asString());
			}
		}
	}
	
	public IResponseEvent doTopoDataSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(idao.doTopoDataSave(param));
		response.setDTO(dto);
		return response;
	}

	public IResponseEvent doTopoBizNodeDataSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(idao.doTopoBizNodeDataSave());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizNodeDataEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(idao.doTopoBizNodeDataEdit());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizNodeDataDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(idao.doTopoBizNodeDataDel());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoDataDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(idao.doTopoDataDel());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoDataEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(idao.doTopoDataEdit(paramDTO.getMapParam()));
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizDataGet(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setListParam(idao.doTopoBizDataGet());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizHostDataGet(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		List<Map> dataList = idao.doGetBizTopoData(Feature.defaultTenantId.equals(identityBean.getTenantId()));
		List<Map> treeList = new ArrayList();
		Map<String,Map> treeMap = new LinkedMap();
		Map<String,List> vmDataChildrenMap = new LinkedMap();
		List<Map> appDataList = new ArrayList();
		for(Map dataMap:dataList){
			if(Cphp.empty(Nest.value(dataMap, "ownerHost").asString())){
				vmDataChildrenMap.put(Nest.value(dataMap, "id").asString(), new ArrayList());
				treeMap.put(Nest.value(dataMap, "id").asString(), dataMap);
			}else
				appDataList.add(dataMap);
		}
		for(Map appDataMap:appDataList){
			List<Map> childrenList = Nest.value(vmDataChildrenMap, Nest.value(appDataMap, "ownerHost").asString()).asCArray().toList();
			Map appMap = new LinkedMap();
			appMap.put("id", Nest.value(appDataMap, "id").asString());
			appMap.put("text", Nest.value(appDataMap, "name").asString());
			String hostType = "";
			if(IMonConsts.A_TYPE_MYSQL == Nest.value(appDataMap, "type").asInteger())
				hostType = HostConstants.CATEGORY_MYSQL;
			else if(IMonConsts.A_TYPE_TOMCAT == Nest.value(appDataMap, "type").asInteger())
				hostType = HostConstants.CATEGORY_TOMCAT;
			appMap.put("hostType", hostType);
			childrenList.add(appMap);
			Nest.value(vmDataChildrenMap, Nest.value(appDataMap, "ownerHost").asString()).$(childrenList);
		}
		for(Entry<String, Map> e:treeMap.entrySet()){
			String key = e.getKey();
			Map value = e.getValue();
			Map vmDataMap = new LinkedMap();
			vmDataMap.put("id", key);
			vmDataMap.put("text", Nest.value(value, "name").asString());
			vmDataMap.put("hostType", HostConstants.CATEGORY_VM);
			vmDataMap.put("children", Nest.value(vmDataChildrenMap, key).asCArray().toList());
			treeList.add(vmDataMap);
		}
		dto.setListParam(treeList);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizTopoAndNodeDataSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		Map bizNodeParam = new LinkedMap();
		bizNodeParam.put("bizNodeName",Nest.value(RadarContext._REQUEST(), "bizNodeName").asString());
		bizNodeParam.put("bizTopoId",Nest.value(RadarContext._REQUEST(), "bizTopoId").asString());
		if(idao.doBizNodeNameConflictCheck(bizNodeParam)){
			Map nameConflict = new LinkedMap();
			nameConflict.put("error", "业务拓扑名称重复");
			dto.setMapParam(nameConflict);
		}else
			dto.setMapParam(idao.doTopoBizTopoAndNodeDataSave());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetBizTopoAllData(IIdentityBean identityBean,
			IRequestEvent request, IDAO idao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		
		ParamDTO dto = new ParamDTO();
		Map data = doGetBizTopoAllDataCommon(identityBean,paramDTO.getMapParam(),idao);
		TopoDataOperDAO.doGetGroupIdByHostTypeAndHostId(idao.getSqlExecutor(), (List<Map>)data.get("nodes"));
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizTopoAndNodeDataEdit(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		Map bizNodeParam = new LinkedMap();
		bizNodeParam.put("bizNodeName",Nest.value(RadarContext._REQUEST(), "bizNodeName").asString());
		bizNodeParam.put("bizTopoId",Nest.value(RadarContext._REQUEST(), "bizTopoId").asString());
		if(idao.doBizNodeNameConflictCheck(bizNodeParam)){
			Map nameConflict = new LinkedMap();
			nameConflict.put("error", "业务拓扑名称重复");
			dto.setMapParam(nameConflict);
		}else
			dto.setMapParam(idao.doTopoBizTopoAndNodeDataEdit());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doTopoBizDataGetToAdmin(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		BizTopoDAO idao = (BizTopoDAO)dao;
	    List<String> resultList = new ArrayList<String>();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setListParam(idao.doTopoBizDataGetToAdmin());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetBizTopoAllDataToAdmin(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		BizTopoDAO idao = (BizTopoDAO) dao;
		Map<String,List> data = doGetBizTopoAllDataCommon(identityBean,paramDTO.getMapParam(),idao);
		List<Map> nodes = data.get("nodes");
		List<Map> edges = data.get("edges");
		Map nodeBizVMAreaMap = nodes.get(2);
		Map nodeBizServerAreaMap = nodes.get(3);
		Map nodeBizNetDevAreaMap = nodes.get(4);
		List<Map> nodeBizVMList = Nest.value(nodeBizVMAreaMap,"children").asCArray().toList();
		CArray<Object> vmHostIdCA = FuncsUtil.rda_objectValues(nodeBizVMList, "id");
		CHostGet options = new CHostGet();
		options.setHostIds(vmHostIdCA.valuesAsLong());
		options.setOutput(new String[]{"hostid_os","hostid"});
		options.setEditable(false);
		CArray<Map<Object,Map>> vmHosts = API.Host(identityBean, dao.getSqlExecutor()).get(options);
		Map<String,String> vmHostMap = new LinkedMap();
		for(Entry<Object,Map<Object,Map>> vmHost:vmHosts.entrySet()){
			Map value = vmHost.getValue();
			vmHostMap.put(Nest.value(value, "hostid_os").asString(),Nest.value(value, "hostid").asString());
		}
		
		Map nodesAndLink = idao.doGetBizNodeAndLink();
		List<Map> netNodes = Nest.value(nodesAndLink, "nodes").asCArray().toList();
		List<Map> netLinks = Nest.value(nodesAndLink, "edges").asCArray().toList();
		CArray<String> hostVmServerEdges = CArray.array();
		
		List<Hypervisors> hypervisorsList = DataDriver.getAllHypervisors();
		for(Hypervisors hypervisors:hypervisorsList){
			List<Hypervisor> hypervisorList = hypervisors.getHypervisors();
			for(Hypervisor hypervisor:hypervisorList){
				for(int i=0,length=netNodes.size();i<length;i++){
					if((HostConstants.CATEGORY_SERVER.equals(netNodes.get(i).get("hostType")))&&hypervisor.getHypervisorHostname().equals(Nest.as(netNodes.get(i).get("name")).asString())){
						List<Server> servers = hypervisor.getServers();
						if(Cphp.empty(servers)||servers.size()==0)
							continue;
						for(Server server : servers){
							String vmName = server.getName();
							if(vmHostMap.containsKey(server.getUuid())){
								Map<String,String> edgeMap = new HashMap(4);
								edgeMap.put("id"  , Nest.as(netNodes.get(i).get("id")).asString().concat("_").concat(Nest.value(vmHostMap,server.getUuid()).asString()));
								edgeMap.put("name", Nest.as(netNodes.get(i).get("name")).asString().concat(" ").concat(vmName));
								edgeMap.put("from", Nest.as(netNodes.get(i).get("id")).asString());
								edgeMap.put("to"  , Nest.value(vmHostMap,server.getUuid()).asString());
								edges.add(edgeMap);
							}
						}
						hostVmServerEdges.add(Nest.as(netNodes.get(i).get("id")).asString());
					}
				}
			}
		}
		
		if(!Cphp.empty(hostVmServerEdges)){
			Map param = new LinkedMap();
			param.put("hostIds", hostVmServerEdges.toList());
			List<Map> needToShowEdges = idao.doGetBizNodeAndLinkByHost(param);
			List<Map> netEdges = new ArrayList();
			for(Map needToShowEdge:needToShowEdges){
				Map netEdge = new LinkedMap();
				netEdge.put("id", Nest.value(needToShowEdge, "id").asString());
				netEdge.put("from", Nest.value(needToShowEdge, "f").asString());
				netEdge.put("to", Nest.value(needToShowEdge, "t").asString());
				netEdges.add(netEdge);
			}
			edges.addAll(netEdges);
			CArray<String> netEdgesFrom = FuncsUtil.rda_objectValues(netEdges, "from");
			CArray<String> netEdgesTo = FuncsUtil.rda_objectValues(netEdges, "to");
			
			List<Map> bizServerAreaChildren = new ArrayList();
			List<Map> bizNetDevAreaChildren = new ArrayList();
			String netNodeHostType = "";
			
			for(Map netNode:netNodes){
				netNodeHostType = Nest.value(netNode, "hostType").asString();
				if(HostConstants.CATEGORY_SERVER.equals(netNodeHostType)){
					if(hostVmServerEdges.containsValue(Nest.value(netNode, "id").asString())){
						bizServerAreaChildren.add(netNode);
						netNode.put("nodeType", TopoDataOperAction.NODE_HOST);
					}
				}else if(!HostConstants.CATEGORY_SERVER.equals(netNodeHostType)&&!HostConstants.CATEGORY_SUBNET.equals(netNodeHostType)){
					if(netEdgesFrom.containsValue(Nest.value(netNode, "id").asString())||netEdgesTo.containsValue(Nest.value(netNode, "id").asString())){
						bizNetDevAreaChildren.add(netNode);
						netNode.put("nodeType", TopoDataOperAction.NODE_HOST);
					}
				}
			}
			nodeBizServerAreaMap.put("children", bizServerAreaChildren);
			nodeBizNetDevAreaMap.put("children", bizNetDevAreaChildren);
			nodes.remove(nodeBizServerAreaMap);
			nodes.remove(nodeBizNetDevAreaMap);
			nodes.add(nodeBizServerAreaMap);
			nodes.add(nodeBizNetDevAreaMap);
		}
		data.put("nodes",nodes);
		data.put("edges",edges);
		TopoDataOperDAO.doGetGroupIdByHostTypeAndHostId(idao.getSqlExecutor(), (List<Map>)data.get("nodes"));
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}
	
	public Map<String,List> doGetBizTopoAllDataCommon(IIdentityBean identityBean,Map param,IDAO dao) throws ParseException {
		Map<String,List> data = new LinkedMap();
		List<Map> nodes = new ArrayList();
		List<Map> edges = new ArrayList();
		BizTopoDAO idao = (BizTopoDAO) dao;
		List<Map> hosts = idao.doGetBizTopoData(Feature.defaultTenantId.equals(identityBean.getTenantId()));
		List<Map> bizNodeHostList = idao.doGetBizNodeHost(param);
		Map<String,Map> bizAreasMap = new LinkedMap();
		String nodeType = "";
		boolean bizAreaFlag = false;
		boolean bizVMAreaFlag = false;
		boolean bizAPPAreaFlag = false;
		boolean bizServerAreaFlag = false;
		boolean bizNetDevAreaFlag = false;
		for(Map bizNodeHostMap:bizNodeHostList){
			nodeType = Nest.value(bizNodeHostMap, "nodeType").asString();
			if(TopoDataOperAction.NODE_BIZAREA.equals(nodeType)){
				bizAreasMap.put(TopoDataOperAction.NODE_BIZAREA, bizNodeHostMap);
				bizAreaFlag = true;
			}else if(TopoDataOperAction.NODE_BIZVMAREA.equals(nodeType)){
				bizAreasMap.put(TopoDataOperAction.NODE_BIZVMAREA, bizNodeHostMap);
				bizVMAreaFlag = true;
			}else if(TopoDataOperAction.NODE_BIZAPPAREA.equals(nodeType)){
				bizAreasMap.put(TopoDataOperAction.NODE_BIZAPPAREA, bizNodeHostMap);
				bizAPPAreaFlag = true;
			}else if(TopoDataOperAction.NODE_BIZSERVERAREA.equals(nodeType)){
				bizAreasMap.put(TopoDataOperAction.NODE_BIZSERVERAREA, bizNodeHostMap);
				bizServerAreaFlag = true;
			}else if(TopoDataOperAction.NODE_BIZNETDEVAREA.equals(nodeType)){
				bizAreasMap.put(TopoDataOperAction.NODE_BIZNETDEVAREA, bizNodeHostMap);
				bizNetDevAreaFlag = true;
			}
			if(bizAreaFlag&&bizVMAreaFlag&&bizAPPAreaFlag&bizServerAreaFlag&bizNetDevAreaFlag)
				break;
		}
		
		List<Map> bizAreaChildren = new ArrayList();
		List<Map> bizVMAreaChildren = new ArrayList();
		List<Map> bizAPPAreaChildren = new ArrayList();
		
		CArray<String> bizNodeHostIds = FuncsUtil.rda_objectValues(bizNodeHostList, "hostId");
		Map bizNodeHostMap = new LinkedMap();
		List<Map> bizNodeList = new ArrayList();
		Map<String,Map> bizNodeMaps = new LinkedMap();
		for(Map bizNodeHost:bizNodeHostList){
			bizNodeHostMap.put(bizNodeHost.get("hostId"), bizNodeHost);
			if(TopoDataOperAction.NODE_BIZNODE.equals(Nest.value(bizNodeHost, "nodeType").asString())){
				bizNodeList.add(bizNodeHost);
				Map bizNodeMap = new LinkedMap();
				bizNodeMap.put("id", Nest.value(bizNodeHost, "hostId").asString());
				bizNodeMap.put("name", Nest.value(bizNodeHost, "bizNodeName").asString());
				bizNodeMap.put("nodeType", Nest.value(bizNodeHost, "nodeType").asString());
				bizNodeMap.put("hostType", HostConstants.CATEGORY_BIZNODE);
				bizAreaChildren.add(bizNodeMap);
				bizNodeMaps.put(Nest.value(bizNodeHost, "hostId").asString(), bizNodeMap);
			}
		}
		CArray<String> bizNodeIdList = FuncsUtil.rda_objectValues(bizNodeList, "hostId");
		List<Map> vmNodeList = new ArrayList();
		for(Map host:hosts){
			if(bizNodeHostIds.containsValue(Nest.value(host, "id").asString())){
				Map bizNodeToUI = Nest.value(bizNodeHostMap, Nest.value(host, "id").asString()).asCArray().toMap();
				String hostType = "";
				Map node = new HashMap();
				node.put("name", host.get("name"));
				node.put("id", host.get("id"));
				if(!Cphp.empty(Nest.value(host,"type").asString())){
					if(IMonConsts.A_TYPE_MYSQL == Nest.value(host, "type").asInteger())
						hostType = HostConstants.CATEGORY_MYSQL;
					else if(IMonConsts.A_TYPE_TOMCAT == Nest.value(host, "type").asInteger())
						hostType = HostConstants.CATEGORY_TOMCAT;
					node.put("ownerHost", Nest.value(host, "ownerHost").asString());
					node.put("type", Nest.value(host,"type").asString());
				}else{
					hostType = HostConstants.CATEGORY_VM;
					node.put("type", IMonConsts.A_TYPE_VM);
				}
				node.put("hostType",hostType);
				node.put("nodeType", TopoDataOperAction.NODE_HOST);
				if(HostConstants.CATEGORY_VM.equals(hostType))
					bizVMAreaChildren.add(node);
				else
					bizAPPAreaChildren.add(node);
				
				if(!Cphp.empty(Nest.value(host,"ownerHost").asString())){
					Map VMEdge = new HashMap(4);
					VMEdge.put("id", Nest.value(host, "ownerHost").asString().concat("_").concat(Nest.value(host, "id").asString()));
					VMEdge.put("name", Nest.value(host, "name").asString().concat("_").concat(Nest.value(host, "ownerHost").asString()));
					VMEdge.put("from", Nest.value(host, "ownerHost").asString());
					VMEdge.put("to", Nest.value(host, "id").asString());
					edges.add(VMEdge);
					
					Map bizEdge = new LinkedMap();
					bizEdge.put("id", Nest.value(host, "id").asString().concat("_").concat(Nest.value(bizNodeToUI, "bizNodeId").asString()));
					bizEdge.put("name", Nest.value(host, "name").asString().concat("_").concat(Nest.value(bizNodeMaps, Nest.value(bizNodeToUI, "bizNodeId").asString(),"name").asString()));
					bizEdge.put("from", Nest.value(host, "id").asString());
					bizEdge.put("to", Nest.value(bizNodeToUI, "bizNodeId").asString());
					edges.add(bizEdge);
				}else
					vmNodeList.add(host);
			}
		}
		CArray<String> fromVmIds = FuncsUtil.rda_objectValues(edges, "from");
		for(Map vmNode:vmNodeList){
			if(!fromVmIds.containsValue(Nest.value(vmNode, "id").asString())){
				Map bizNodeToUI = Nest.value(bizNodeHostMap, Nest.value(vmNode, "id").asString()).asCArray().toMap();
				Map bizEdge = new LinkedMap();
				bizEdge.put("id", Nest.value(vmNode, "id").asString().concat("_").concat(Nest.value(bizNodeToUI, "bizNodeId").asString()));
				bizEdge.put("name", Nest.value(vmNode, "name").asString().concat("_").concat(Nest.value(bizNodeMaps, Nest.value(bizNodeToUI, "bizNodeId").asString(),"name").asString()));
				bizEdge.put("from", Nest.value(vmNode, "id").asString());
				bizEdge.put("to", Nest.value(bizNodeToUI, "bizNodeId").asString());
				edges.add(bizEdge);
			}
		}
		doTopoDataSetXY(bizAreaChildren,idao.getSqlExecutor(),identityBean,Nest.as(param.get("bizTopoId")).asString());
		doTopoDataSetXY(bizVMAreaChildren,idao.getSqlExecutor(),identityBean,Nest.as(param.get("bizTopoId")).asString());
		doTopoDataSetXY(bizAPPAreaChildren,idao.getSqlExecutor(),identityBean,Nest.as(param.get("bizTopoId")).asString());
		Map nodeBizAreaMap = new LinkedMap();
		nodeBizAreaMap.putAll(Nest.value(bizAreasMap, TopoDataOperAction.NODE_BIZAREA).asCArray().toMap());
		nodeBizAreaMap.put("children", bizAreaChildren);
		Map nodeBizVMAreaMap = new LinkedMap(); 
		nodeBizVMAreaMap.putAll(Nest.value(bizAreasMap, TopoDataOperAction.NODE_BIZVMAREA).asCArray().toMap());
		nodeBizVMAreaMap.put("children", bizVMAreaChildren);
		Map nodeBizAPPAreaMap = new LinkedMap();
		nodeBizAPPAreaMap.putAll(Nest.value(bizAreasMap, TopoDataOperAction.NODE_BIZAPPAREA).asCArray().toMap());
		nodeBizAPPAreaMap.put("children", bizAPPAreaChildren);
		nodes.add(nodeBizAreaMap);
		nodes.add(nodeBizAPPAreaMap);
		nodes.add(nodeBizVMAreaMap);
		if(Feature.defaultTenantId.equals(identityBean.getTenantId())){
			Map nodeBizServerAreaMap = new LinkedMap();
			nodeBizServerAreaMap.putAll(Nest.value(bizAreasMap, TopoDataOperAction.NODE_BIZSERVERAREA).asCArray().toMap());
			Map nodeBizNetDevAreaMap = new LinkedMap();
			nodeBizNetDevAreaMap.putAll(Nest.value(bizAreasMap, TopoDataOperAction.NODE_BIZNETDEVAREA).asCArray().toMap());
			nodes.add(nodeBizServerAreaMap);
			nodes.add(nodeBizNetDevAreaMap);
		}
		data.put("nodes", nodes);
		data.put("edges", edges);
		return data;
	}
	
}
