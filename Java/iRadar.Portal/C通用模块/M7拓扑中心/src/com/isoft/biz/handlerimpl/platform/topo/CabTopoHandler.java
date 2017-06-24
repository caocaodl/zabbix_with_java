package com.isoft.biz.handlerimpl.platform.topo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

import com.isoft.biz.dao.IDAO;
import com.isoft.biz.daoimpl.platform.topo.CabTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.HostExpDAO;
import com.isoft.biz.daoimpl.platform.topo.TPicDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.platform.topo.ICabTopoHandler;
import com.isoft.biz.handlerimpl.BaseLogicHandler;
import com.isoft.biz.vo.platform.topo.TCabNode;
import com.isoft.biz.vo.platform.topo.TPic;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.imon.topo.util.Page;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.StringUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


public class CabTopoHandler extends BaseLogicHandler implements ICabTopoHandler {

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
			groupsList = TopoUtil.groupsCA.toList();
		}else{
			groupsList = new ArrayList();
			groupsList.add(Long.valueOf(category));
		}
		param.put("groupid", groupsList);
		CabTopoDAO idao = (CabTopoDAO) dao;
		List<Integer> hostIds = idao.doHostIdByTopoId(null, param);
		
		HostExpDAO hostExpDao = new HostExpDAO(dao.getSqlExecutor());
		List<Map> hosts = hostExpDao.doAssetsHostList(param);	
		for(Map host:hosts){
			String hostId = Nest.value(host, "hostId").asString();
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("hostId", hostId);
			tempMap.put("groupid", groupsList);
			String elementType = hostExpDao.doAssetsCategoryByHostId(tempMap);
			Long type=Long.parseLong(elementType);
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
					if("all".equals(category)&&(TopoUtil.groupsCA.containsValue(type))){
						categoryFlag = true;
					}else if (elementType.equals(category)) {
						categoryFlag = true;
					}
				}
				if (!StringUtil.isEmptyStr(searchName)) {
					if (Nest.value(host, "host").asString().contains(searchName) || Nest.value(host, "name").asString().contains(searchName)) {
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
		resultMap.put("currentPage", page.getCurrentPage());
		resultMap.put("totalResult", page.getTotalRecord());
		resultMap.put("rows", subResutlList);

		ParamDTO dto = new ParamDTO();
		dto.setMapParam(resultMap);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doNodeDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String nodeIds = (String)param.get("nodeIds");
		String[] ids = nodeIds.split(",");
		for(String id : ids){
			param.put("nodeId", id);
			idao.doCabinetNodeDelByNodeId(param);
		}
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public IResponseEvent doTCabTopoDel(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		idao.doCabinetNodeDelByTopoId(param);

		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public IResponseEvent doCabTopoXml(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) throws ParseException {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO)dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		param.put("tenantId", identityBean.getTenantId());
		param.put("userId", identityBean.getUserId());
		List<TCabNode> nodes = idao.doCabinetTopoList(param);
		String xml = TopoUtil.tCabNodesToXml(nodes, idao);
		
		ParamDTO dto = new ParamDTO();
		dto.setStrParam(xml);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public IResponseEvent doTCabTopoNodeSave(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		String xml = (String)param.get("xml");
		xml = TopoUtil.translateXml(xml);
		String topoId = (String)param.get("topoId");
		NodeList nodeList = TopoUtil.getXmlNodeList(xml);
		//清空表数据
		idao.doCabinetNodeDelByTopoId(param);
		List<TCabNode> nodes = TopoUtil.nodeListToTCabNode(nodeList);
		for(TCabNode node : nodes) {
			Map<String,Object> paraMap = new HashMap<String, Object>();
			String tagName = node.getTagName();
			paraMap.put("topoId", topoId);
			paraMap.put("hostId", node.getHostId());
			paraMap.put("category",node.getCategory());
			paraMap.put("picId", node.getPicId());
			paraMap.put("tagName", tagName);
			paraMap.put("g", node.getG());
			paraMap.put("name", node.getName());
			paraMap.put("userId", identityBean.getUserId());
			paraMap.put("tenantId", identityBean.getTenantId());
			paraMap.put("createdUser", identityBean.getUserName());
			if(tagName.startsWith(TopoUtil.TOPO_PIC_ROOM_CATEGORY)){
				paraMap.put("priority", TopoUtil.cab_room_priority);
			}else if(tagName.startsWith(TopoUtil.TOPO_PIC_CABINET_CATEGORY)){
				paraMap.put("priority", TopoUtil.cab_cabinet_priority);
			}else {
				paraMap.put("priority", TopoUtil.cab_server_priority);
			}
			idao.doTCabinetNodeAdd(paraMap);
		}
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(true);
		response.setDTO(dto);
		return response;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IResponseEvent doCabTopoUpdateG(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		Map param = paramDTO.getMapParam();
		boolean result = idao.doCabinetUpdateG(param);
		
		ParamDTO dto = new ParamDTO();
		dto.setBoolParam(result);
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
	
	@SuppressWarnings({ "rawtypes" })
	public IResponseEvent doGetRoomData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setListParam(idao.doGetRoomData());
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetCabData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		dto.setListParam(idao.doGetCabData(paramDTO.getMapParam()));
		response.setDTO(dto);
		return response;
	}
	
	public IResponseEvent doGetCabTopoData(IIdentityBean identityBean,
			IRequestEvent request, IDAO dao) {
		IResponseEvent response = new ResponseEvent();
		Map data = new HashMap();
		CabTopoDAO idao = (CabTopoDAO) dao;
		ParamDTO paramDTO = (ParamDTO) request.getDTO();
		ParamDTO dto = new ParamDTO();
		data.put("nodes", idao.doGetCabTopoData(paramDTO.getMapParam()));
		dto.setMapParam(data);
		response.setDTO(dto);
		return response;
	}
	
}
