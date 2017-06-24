package com.isoft.biz.daoimpl.platform.topo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.Feature;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.IBizTopoDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.TBizLine;
import com.isoft.biz.vo.platform.topo.TBizNode;
import com.isoft.biz.web.platform.topo.TopoDataOperAction;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.virtualresource.KeystoneSyncer;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class BizTopoDAO extends BaseDAO implements IBizTopoDAO {

	public static String BIZTOPONODEIDSEPERATOR = "#";
	
	public BizTopoDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static String SQL_T_BIZ_NODE_HOST_ID_LIST_BY_TOPO_ID = "SQL_T_BIZ_NODE_HOST_ID_LIST_BY_TOPO_ID";
	@SuppressWarnings("unchecked")
	public List<Integer> doHostIdByTopoId(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_NODE_HOST_ID_LIST_BY_TOPO_ID);
		return (List<Integer>) executor.executeNameParaQuery(sql, paraMap, Integer.class);	
	}
	
	private static String SQL_T_BIZ_NODE_LIST = "SQL_T_BIZ_NODE_LIST";
	@SuppressWarnings("unchecked")
	public List<TBizNode> doBizTopoList(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_NODE_LIST);
		List<TBizNode> nodes = (List<TBizNode>) executor.executeNameParaQuery(sql, paraMap,TBizNode.class);
		for(TBizNode node : nodes){
			BizLineDAO lineDao = new BizLineDAO(this.getSqlExecutor());
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("nodeId", node.getNodeId());
			tempMap.put("topoId", node.getTopoId());
			List<TBizLine> lines = lineDao.doBizLineList(tempMap);
			node.setLines(lines);
		}
		return nodes;
	}

	private static final String SQL_T_BIZ_NODE_ADD = "SQL_T_BIZ_NODE_ADD";
	public String doTBizNodeAdd(Map<String, Object> paraMap) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_T_BIZ_NODE_ADD);
			String id = getFlowcode(NameSpaceEnum.T_CABINET_NODE);
			paraMap.put("nodeId", id);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				return id;
			}
		return null;
	}

	private static final String SQL_T_BIZ_NODE_DEL_BY_TOPOID = "SQL_T_BIZ_NODE_DEL_BY_TOPOID";

	@SuppressWarnings("rawtypes")
	public int doTBizNodeDelByTopoId(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_NODE_DEL_BY_TOPOID);
		int result = executor.executeInsertDeleteUpdate(sql, param);
		if(result!=0){
		    BizLineDAO lineDao = new BizLineDAO(this.getSqlExecutor());
			lineDao.doLineDelByTopoId(param);
		}
		return result;
	}

	private static final String SQL_T_BIZ_NODE_DEL_BY_NODE_ID = "SQL_T_BIZ_NODE_DEL_BY_NODE_ID";
	public String doBizNodeDelByNodeId(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_NODE_DEL_BY_NODE_ID);
		String id = (String)paraMap.get("id");
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			BizLineDAO lineDao = new BizLineDAO(executor);
			lineDao.doLineDelByLineId(paraMap);
			return id;
		}
		return null;
	}
	
	private static final String SQL_T_BIZ_LINE_DEL_BY_LINE_ID = "SQL_T_BIZ_LINE_DEL_BY_LINE_ID";
	public String doBizLineDelByLineId(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_LINE_DEL_BY_LINE_ID);
		String id = (String)paraMap.get("id");
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			BizLineDAO lineDao = new BizLineDAO(executor);
			lineDao.doLineDelByLineId(paraMap);
			return id;
		}
		return null;
	}
	
	private static final String SQL_T_CABINET_NODE_UPDATE_G = "SQL_T_CABINET_NODE_UPDATE_G";

	public String doBizUpdateG(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_CABINET_NODE_UPDATE_G);
		String id = (String)paraMap.get("nodeId");
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			return id;
		}
		return null;
	}
	
	private static String SQL_GET_BIZ_TOPO_DATA = "SQL_GET_BIZ_TOPO_DATA";
	private static String SQL_GET_BIZ_TOPO_VM_DATA = "SQL_GET_BIZ_TOPO_VM_DATA";
	private static String SQL_GET_BIZ_TOPO_APP_DATA = "SQL_GET_BIZ_TOPO_APP_DATA";
	@SuppressWarnings("rawtypes")
	public List<Map> doGetBizTopoData(boolean adminFlag) {
		Map param = new LinkedMap();
		List<Map> bizTopoData = new ArrayList();
		SQLExecutor executor = getSqlExecutor();
		param.put("adminFlag", adminFlag);
		String sql = getSql(SQL_GET_BIZ_TOPO_VM_DATA);
		Map sqlVO = getSqlVO(SQL_GET_BIZ_TOPO_VM_DATA);
		bizTopoData.addAll(executor.executeNameParaQuery(sql,param,sqlVO));
		sql = getSql(SQL_GET_BIZ_TOPO_APP_DATA);
		sqlVO = getSqlVO(SQL_GET_BIZ_TOPO_APP_DATA);
		List<Integer> appType = new ArrayList();
		appType.add(IMonConsts.A_TYPE_MYSQL);
		appType.add(IMonConsts.A_TYPE_TOMCAT);
		param.put("appType", appType);
		bizTopoData.addAll(executor.executeNameParaQuery(sql,param,sqlVO));
		return bizTopoData;
	}
	
	private static String SQL_BIZ_TOPO_DATA_SAVE = "SQL_BIZ_TOPO_DATA_SAVE";
	@SuppressWarnings("rawtypes")
	public String doTopoDataSave(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_DATA_SAVE);
		String id = getFlowcode(NameSpaceEnum.T_TOPO);
		param.put("id", id);
		if(executor.executeInsertDeleteUpdate(sql, param)==1)
			return id;
		return null;
	}
	
	private static String SQL_BIZ_TOPO_BIZ_NODE_DATA_SAVE = "SQL_BIZ_TOPO_BIZ_NODE_DATA_SAVE";
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET = "SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET";
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE = "SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE";
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_UPDATE = "SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_UPDATE";
	@SuppressWarnings("rawtypes")
	public String doTopoBizNodeDataSave() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_SAVE);
		String id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
		Map param = new LinkedMap();
		String bizNodeName = Nest.value(RadarContext._REQUEST(), "bizNodeName").asString();
		String bizTopoId = Nest.value(RadarContext._REQUEST(), "bizTopoId").asString();
		param.put("id", id);
		param.put("bizTopoId", bizTopoId);
		param.put("bizNodeName", bizNodeName);
		param.put("topoType", TopoDataOperAction.TOPO_BIZ);
		param.put("nodeType", TopoDataOperAction.NODE_BIZNODE);
		param.put("hostId", id);
		if(executor.executeInsertDeleteUpdate(sql, param)==1){
			CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "children").asCArray();
			List<String> hostIds = FuncsUtil.rda_objectValues(nodes, "hostId").toList();
			Map bizNodeHostGet = new LinkedMap();
			bizNodeHostGet.put("bizTopoId", bizTopoId);
			bizNodeHostGet.put("hostIds", hostIds);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET);
			Map sqlVO = getSqlVO(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET);
			List<Map> bizNodeHostList = executor.executeNameParaQuery(sql, bizNodeHostGet, sqlVO);
			Map bizNodeHostMap = new LinkedMap();
			for(Map bizNodeHost:bizNodeHostList)
				bizNodeHostMap.put(bizNodeHost.get("hostId"), bizNodeHost);
			List<Map> needToAdd = new ArrayList();
			List<Map> needToUpdate = new ArrayList();
			String bizTopoNodeId = "";
			for(Map node:nodes){
				node.put("bizTopoId", bizTopoId);
				node.put("topoType", TopoDataOperAction.TOPO_BIZ);
				node.put("nodeType", TopoDataOperAction.NODE_HOST);
				if(Cphp.isset(bizNodeHostMap, Nest.as(node.get("hostId")).asString())){
					bizTopoNodeId = Nest.value(bizNodeHostMap, Nest.as(node.get("hostId")).asString(),"bizNodeId").asString().concat("BIZTOPONODEIDSEPERATOR").concat(id);
					node.put("bizNodeId", bizTopoNodeId);
					node.put("id", Nest.value(bizNodeHostMap, Nest.as(node.get("hostId")).asString(),"id").asString());
					needToUpdate.add(node);
				}else{
					bizTopoNodeId = id;
					node.put("id", getFlowcode(NameSpaceEnum.T_TOPO_LOCATION));
					node.put("bizNodeId", bizTopoNodeId);
					needToAdd.add(node);
				}
			}
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_UPDATE);
			for(Map node:needToUpdate)
				executor.executeInsertDeleteUpdate(sql, node);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE);
			for(Map node:needToAdd)
				executor.executeInsertDeleteUpdate(sql, node);
			return id;
		}
		return null;
	}
	
	private static String SQL_BIZ_TOPO_BIZ_NODE_DATA_EDIT = "SQL_BIZ_TOPO_BIZ_NODE_DATA_EDIT";
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_EDIT_GET = "SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_EDIT_GET";
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_DEL = "SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_DEL";
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_LOCATION_DATA_DEL = "SQL_BIZ_TOPO_BIZ_NODE_HOST_LOCATION_DATA_DEL";
	@SuppressWarnings("rawtypes")
	public String doTopoBizNodeDataEdit() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_EDIT);
		String bizNodeName = Nest.value(RadarContext._REQUEST(), "bizNodeName").asString();
		String bizNodeId = Nest.value(RadarContext._REQUEST(), "bizNodeId").asString();
		String bizTopoId = Nest.value(RadarContext._REQUEST(), "bizTopoId").asString();
		Map param = new LinkedMap();
		param.put("id", bizNodeId);
		param.put("bizNodeName", bizNodeName);
		if(executor.executeInsertDeleteUpdate(sql, param)==1){
			CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "children").asCArray();
			Map bizNodeHostGet = new LinkedMap();
			bizNodeHostGet.put("bizTopoId", bizTopoId);
//			bizNodeHostGet.put("bizNodeId", bizNodeId);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_EDIT_GET);
			Map sqlVO = getSqlVO(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET);
			List<Map> bizNodeHostList = executor.executeNameParaQuery(sql, bizNodeHostGet, sqlVO);
			List<Map> bizNodeHostFilterList = new ArrayList();
			for(Map bizNodeHost:bizNodeHostList){
				if(Nest.value(bizNodeHost, "bizNodeId").asString().indexOf(bizNodeId)>-1)
					bizNodeHostFilterList.add(bizNodeHost);
			}
			Map<String,Map> bizNodeHostMap = new LinkedMap();
			for(Map bizNodeHost:bizNodeHostFilterList)
				bizNodeHostMap.put(Nest.as(bizNodeHost.get("hostId")).asString(), bizNodeHost);
			List<Map> needToAdd = new ArrayList();
			List<Map> needToUpdate = new ArrayList();
			List<Map> needToDel = new ArrayList();
			for(Map node:nodes){
				node.put("bizTopoId", bizTopoId);
				node.put("topoType", TopoDataOperAction.TOPO_BIZ);
				node.put("nodeType", TopoDataOperAction.NODE_HOST);
				if(Cphp.isset(bizNodeHostMap, Nest.as(node.get("hostId")).asString())){
					bizNodeHostMap.remove(Nest.as(node.get("hostId")).asString());
				}else{
					node.put("id", getFlowcode(NameSpaceEnum.T_TOPO_LOCATION));
					node.put("bizNodeId", bizNodeId);
					needToAdd.add(node);
				}
			}
			for (Entry<String, Map> e : bizNodeHostMap.entrySet()) {
				Map value = e.getValue();
				String hostBizNodeId = Nest.value(value, "bizNodeId").asString();
				if(hostBizNodeId.indexOf(BIZTOPONODEIDSEPERATOR)>-1){
					Map<String,String> hostBizNodeIdsMap = FuncsUtil.rda_toHash(Nest.as(hostBizNodeId.split(BIZTOPONODEIDSEPERATOR)).asCArray());
					hostBizNodeIdsMap.remove(bizNodeId);
					hostBizNodeId = Cphp.implode(BIZTOPONODEIDSEPERATOR, Nest.as(hostBizNodeIdsMap).asCArray().valuesAsString());
					value.put("bizNodeId", hostBizNodeId);
					needToUpdate.add(value);
				}else
					needToDel.add(value);
			}
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_UPDATE);
			for(Map node:needToUpdate)
				executor.executeInsertDeleteUpdate(sql, node);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE);
			for(Map node:needToAdd)
				executor.executeInsertDeleteUpdate(sql, node);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_DEL);
			List<String> hostIds = FuncsUtil.rda_objectValues(needToDel, "hostId").toList();
//			for(Map node:needToDel)
			Map needDelParam = new LinkedMap();
			needDelParam.put("hostIds", hostIds);
			needDelParam.put("bizTopoId", bizTopoId);
			needDelParam.put("bizNodeId", bizNodeId);
			executor.executeInsertDeleteUpdate(sql, needDelParam);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_LOCATION_DATA_DEL);
			executor.executeInsertDeleteUpdate(sql, needDelParam);
			return bizNodeId;
		}
		return null;
	}
	
	private static String SQL_BIZ_TOPO_BIZ_NODE_DATA_DEL = "SQL_BIZ_TOPO_BIZ_NODE_DATA_DEL";
	public String doTopoBizNodeDataDel() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_DEL);
		String bizNodeId = Nest.value(RadarContext._REQUEST(), "bizNodeId").asString();
		String bizTopoId = Nest.value(RadarContext._REQUEST(), "bizTopoId").asString();
		Map param = new LinkedMap();
		param.put("id", bizNodeId);
		if(executor.executeInsertDeleteUpdate(sql, param)==1){
			Map bizNodeHostGet = new LinkedMap();
			bizNodeHostGet.put("bizTopoId", bizTopoId);
			bizNodeHostGet.put("bizNodeId", bizNodeId);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_EDIT_GET);
			Map sqlVO = getSqlVO(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET);
			List<Map> bizNodeHostList = executor.executeNameParaQuery(sql, bizNodeHostGet, sqlVO);
			List<Map> bizNodeHostFilterList = new ArrayList();
			for(Map bizNodeHost:bizNodeHostList){
				if(Nest.value(bizNodeHost, "bizNodeId").asString().indexOf(bizNodeId)>-1)
					bizNodeHostFilterList.add(bizNodeHost);
			}
			Map<String,Map> bizNodeHostMap = new LinkedMap();
			for(Map bizNodeHost:bizNodeHostFilterList)
				bizNodeHostMap.put(Nest.as(bizNodeHost.get("hostId")).asString(), bizNodeHost);
			List<Map> needToUpdate = new ArrayList();
			List<Map> needToDel = new ArrayList();
			for (Entry<String, Map> e : bizNodeHostMap.entrySet()) {
				Map value = e.getValue();
				String hostBizNodeId = Nest.value(value, "bizNodeId").asString();
				if(hostBizNodeId.indexOf(BIZTOPONODEIDSEPERATOR)>-1){
					Map<String,String> hostBizNodeIdsMap = FuncsUtil.rda_toHash(Nest.as(hostBizNodeId.split(BIZTOPONODEIDSEPERATOR)).asCArray());
					hostBizNodeIdsMap.remove(bizNodeId);
					hostBizNodeId = Cphp.implode(BIZTOPONODEIDSEPERATOR, Nest.as(hostBizNodeIdsMap).asCArray().valuesAsString());
					value.put("bizNodeId", hostBizNodeId);
					needToUpdate.add(value);
				}else
					needToDel.add(value);
			}
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_UPDATE);
			for(Map node:needToUpdate)
				executor.executeInsertDeleteUpdate(sql, node);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_DEL);
			List<String> hostIds = FuncsUtil.rda_objectValues(needToDel, "hostId").toList();
//			for(Map node:needToDel)
			Map needDelParam = new LinkedMap();
			needDelParam.put("hostIds", hostIds);
			needDelParam.put("bizTopoId", bizTopoId);
			needDelParam.put("bizNodeId", bizNodeId);
			executor.executeInsertDeleteUpdate(sql, needDelParam);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_LOCATION_DATA_DEL);
			executor.executeInsertDeleteUpdate(sql, needDelParam);
			return bizNodeId;
		}
		return null;
	}
	
	private static String SQL_BIZ_TOPO_DATA_DEL = "SQL_BIZ_TOPO_DATA_DEL";
	private static String SQL_BIZ_TOPO_BIZ_NODE_AND_HOST_DATA_DEL = "SQL_BIZ_TOPO_BIZ_NODE_AND_HOST_DATA_DEL";
	@SuppressWarnings("rawtypes")
	public String doTopoDataDel() {
		SQLExecutor executor = getSqlExecutor();
		String bizTopoId = Nest.value(RadarContext._REQUEST(), "bizTopoId").asString();
//		String sql = getSql(SQL_BIZ_TOPO_DATA_DEL);
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_AND_HOST_DATA_DEL);
		Map paraMap = new LinkedMap();
		paraMap.put("bizTopoId", bizTopoId);
		if(executor.executeInsertDeleteUpdate(sql, paraMap)>0){
//			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_AND_HOST_DATA_DEL);
//			executor.executeInsertDeleteUpdate(sql, paraMap);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_LOCATION_DATA_DEL);
			executor.executeInsertDeleteUpdate(sql, paraMap);
			return bizTopoId;
		}
		return null;
	}
	
	private static String SQL_BIZ_TOPO_DATA_EDIT = "SQL_BIZ_TOPO_DATA_EDIT";
	@SuppressWarnings("rawtypes")
	public String doTopoDataEdit(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_DATA_EDIT);
		if(executor.executeInsertDeleteUpdate(sql, param)==1)
			return Nest.value(param, "bizTopoId").asString();
		return null;
	}
	
	private static String SQL_BIZ_TOPO_DATA_GET = "SQL_BIZ_TOPO_DATA_GET";
	@SuppressWarnings("rawtypes")
	public List<Map> doTopoBizDataGet() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_DATA_GET);
		Map sqlVO = getSqlVO(SQL_BIZ_TOPO_DATA_GET);
		Map param = new LinkedMap();
		param.put("topoType", TopoDataOperAction.TOPO_BIZ);
		return executor.executeNameParaQuery(sql, param, sqlVO);
	}
	
	private static String SQL_BIZ_TOPO_BIZ_NODE_HOST_GET = "SQL_BIZ_TOPO_BIZ_NODE_HOST_GET";
	@SuppressWarnings("rawtypes")
	public List<Map> doGetBizNodeHost(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_GET);
		Map sqlVO = getSqlVO(SQL_BIZ_TOPO_BIZ_NODE_HOST_GET);
		param.put("topoType", TopoDataOperAction.TOPO_BIZ);
		return executor.executeNameParaQuery(sql, param, sqlVO);
	}
	
	@SuppressWarnings("rawtypes")
	public Map doTopoBizTopoAndNodeDataSave() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_SAVE);
		Map bizNodeParam = new LinkedMap();
		String id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
		bizNodeParam.put("id",id);
		bizNodeParam.put("topoType",Nest.value(RadarContext._REQUEST(), "topoType").asString());
		bizNodeParam.put("nodeType",Nest.value(RadarContext._REQUEST(), "nodeType").asString());
		bizNodeParam.put("hostId",id);
		bizNodeParam.put("bizTopoId",id);
		bizNodeParam.put("bizNodeName",Nest.value(RadarContext._REQUEST(), "bizNodeName").asString());
		if(executor.executeInsertDeleteUpdate(sql, bizNodeParam)==1){
			String bizTopoId , bizNodeId;
			bizTopoId = bizNodeId = id;
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE);
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			Map areaMap = new LinkedMap();
			areaMap.put("id", id);
			areaMap.put("topoType", Nest.value(RadarContext._REQUEST(), "topoType").asString());
			areaMap.put("nodeType", TopoDataOperAction.NODE_BIZAREA);
			areaMap.put("hostId", id);
			areaMap.put("bizTopoId", bizTopoId);
			areaMap.put("bizNodeId", bizNodeId);
			executor.executeInsertDeleteUpdate(sql, areaMap);
			String bizAreaId = id;
			
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			areaMap.put("id", id);
			areaMap.put("nodeType", TopoDataOperAction.NODE_BIZVMAREA);
			areaMap.put("hostId", id);
			executor.executeInsertDeleteUpdate(sql, areaMap);
			String bizVMAreaId = id;
			
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			areaMap.put("id", id);
			areaMap.put("nodeType", TopoDataOperAction.NODE_BIZAPPAREA);
			areaMap.put("hostId", id);
			executor.executeInsertDeleteUpdate(sql, areaMap);
			String bizAPPAreaId = id;
			
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			areaMap.put("id", id);
			areaMap.put("nodeType", TopoDataOperAction.NODE_BIZSERVERAREA);
			areaMap.put("hostId", id);
			executor.executeInsertDeleteUpdate(sql, areaMap);
			String bizServerAreaId = id;
			
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			areaMap.put("id", id);
			areaMap.put("nodeType", TopoDataOperAction.NODE_BIZNETDEVAREA);
			areaMap.put("hostId", id);
			executor.executeInsertDeleteUpdate(sql, areaMap);
			String bizNetDevAreaId = id;
			
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_BIZAREA_UPDATE);
			bizNodeParam.put("bizAreaId", bizAreaId);
			executor.executeInsertDeleteUpdate(sql, bizNodeParam);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE);
			CArray<Map> vmHostCA = Nest.value(RadarContext._REQUEST(), "children").asCArray();
			for(Map vmHost:vmHostCA){
				id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
				vmHost.put("id", id);
				vmHost.put("bizTopoId", bizTopoId);
				vmHost.put("bizNodeId", bizNodeId);
				vmHost.put("bizAreaId", bizVMAreaId);
				if(executor.executeInsertDeleteUpdate(sql, vmHost)==1){
					CArray<Map> appHostCA = Nest.value(vmHost, "children").asCArray();
					if(!Cphp.empty(appHostCA)){
						for(Map appHost:appHostCA){
							id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
							appHost.put("id", id);
							appHost.put("bizTopoId", bizTopoId);
							appHost.put("bizNodeId", bizNodeId);
							appHost.put("bizAreaId", bizAPPAreaId);
							executor.executeInsertDeleteUpdate(sql, appHost);
						}
					}
				}
			}
			Map resultMap = new LinkedMap();
			resultMap.put("bizTopoId", bizTopoId);
			resultMap.put("toponame", Nest.value(RadarContext._REQUEST(), "bizNodeName").asString());
			return resultMap;
		}
		return null;
	}
	
	private static String SQL_BIZ_TOPO_BIZ_NODE_DATA_BIZAREA_UPDATE = "SQL_BIZ_TOPO_BIZ_NODE_DATA_BIZAREA_UPDATE";
	public Map doTopoBizTopoAndNodeDataEdit() {
		SQLExecutor executor = getSqlExecutor();
		Map bizNodeParam = new LinkedMap();
		String id = Nest.value(RadarContext._REQUEST(), "bizTopoId").asString();
		bizNodeParam.put("id",id);
		bizNodeParam.put("bizNodeName",Nest.value(RadarContext._REQUEST(), "bizNodeName").asString());
		String sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_EDIT);
		if(executor.executeInsertDeleteUpdate(sql, bizNodeParam)==1){
			String bizTopoId , bizNodeId;
			bizTopoId = bizNodeId = id;
			String bizVMAreaId="" , bizAPPAreaId="";
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET);
			Map sqlVO = getSqlVO(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_GET);
			Map param = new LinkedMap();
			param.put("bizTopoId", bizTopoId);
			List<Map> bizNodeHostList = executor.executeNameParaQuery(sql, param, sqlVO);
			List<String> bizNodeHostIds = new ArrayList();
			for(Map bizNodeHost:bizNodeHostList){
				if(TopoDataOperAction.NODE_BIZVMAREA.equals(Nest.value(bizNodeHost, "nodeType").asString()))
					bizVMAreaId = Nest.value(bizNodeHost, "id").asString();
				else if(TopoDataOperAction.NODE_BIZAPPAREA.equals(Nest.value(bizNodeHost, "nodeType").asString()))
					bizAPPAreaId = Nest.value(bizNodeHost, "id").asString();
				bizNodeHostIds.add(Nest.value(bizNodeHost, "hostId").asString());
			}
			List<Map> vmAndAppParam = new ArrayList();
			List<String> vmAndAppIds = new ArrayList();
			CArray<Map> vmHostCA = Nest.value(RadarContext._REQUEST(), "children").asCArray();
			for(Map vmHost:vmHostCA){
				vmHost.put("bizTopoId", bizTopoId);
				vmHost.put("bizNodeId", bizNodeId);
				vmHost.put("bizAreaId", bizVMAreaId);
				vmAndAppParam.add(vmHost);
				vmAndAppIds.add(Nest.value(vmHost, "hostId").asString());
				CArray<Map> appHostCA = Nest.value(vmHost, "children").asCArray();
				if(!Cphp.empty(appHostCA)){
					for(Map appHost:appHostCA){
						appHost.put("bizTopoId", bizTopoId);
						appHost.put("bizNodeId", bizNodeId);
						appHost.put("bizAreaId", bizAPPAreaId);
						vmAndAppParam.add(appHost);
						vmAndAppIds.add(Nest.value(appHost, "hostId").asString());
					}
				}
			}
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_DEL);
			Map bizHostParam = new LinkedMap();
			bizHostParam.put("bizTopoId", bizTopoId);
			bizHostParam.put("bizNodeId", bizNodeId);
			bizHostParam.put("hostIds", vmAndAppIds);
			bizHostParam.put("nodeType", TopoDataOperAction.NODE_HOST);
			executor.executeInsertDeleteUpdate(sql, bizHostParam);
			sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_HOST_DATA_SAVE);
			for(Map vmAndAppMap:vmAndAppParam){
				if(!bizNodeHostIds.contains(Nest.value(vmAndAppMap, "hostId").asString())){
					id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
					vmAndAppMap.put("id", id);
					executor.executeInsertDeleteUpdate(sql, vmAndAppMap);
				}
			}
			Map resultMap = new LinkedMap();
			resultMap.put("bizTopoId", bizTopoId);
			resultMap.put("toponame", Nest.value(RadarContext._REQUEST(), "bizNodeName").asString());
			return resultMap;
		}
		return null;
	}
	
	private static String SQL_BIZ_TOPO_DATA_GET_TENANT_TO_ADMIN = "SQL_BIZ_TOPO_DATA_GET_TENANT_TO_ADMIN";
	private static String SQL_BIZ_TOPO_DATA_GET_SUB_TO_ADMIN = "SQL_BIZ_TOPO_DATA_GET_SUB_TO_ADMIN";
	@SuppressWarnings("rawtypes")
	public List<Map> doTopoBizDataGetToAdmin() {
		SQLExecutor executor = getSqlExecutor();
//		String sql = getSql(SQL_BIZ_TOPO_DATA_GET_TENANT_TO_ADMIN);
//		Map sqlVO = getSqlVO(SQL_BIZ_TOPO_DATA_GET_TENANT_TO_ADMIN);
		Map param = new LinkedMap();
		param.put("topoType", TopoDataOperAction.TOPO_BIZ);
//		List<Map> tenantsList = executor.executeNameParaQuery(sql, param, sqlVO);
		List<Map> tenantsList = new ArrayList();
		for(Tenant tenant:KeystoneSyncer.tenantsData){
			if(Feature.defaultTenantId.equals(tenant.getId()))
				continue;
			Map tenantMap = new LinkedMap();
			tenantMap.put("tenantId", tenant.getId());
			tenantMap.put("tenantName", tenant.getName());
			tenantsList.add(tenantMap);
		}
		
		if(!Cphp.empty(tenantsList) && tenantsList.size()>0){
			param.put("nodeType", TopoDataOperAction.NODE_BIZNODE);
			String sql = getSql(SQL_BIZ_TOPO_DATA_GET_SUB_TO_ADMIN);
			Map sqlVO = getSqlVO(SQL_BIZ_TOPO_DATA_GET_SUB_TO_ADMIN);
			List<Map> topoDatasList = executor.executeNameParaQuery(sql, param, sqlVO);
			if(!Cphp.empty(topoDatasList) && topoDatasList.size()>0){
				for(Map tenantMap:tenantsList){
					List<Map> tenantSubData = new ArrayList();
					for(Map topoDataMap:topoDatasList){
						if(Nest.value(topoDataMap, "tenantId").asString().equals(Nest.value(tenantMap, "tenantId").asString()))
							tenantSubData.add(topoDataMap);
					}
					tenantMap.put("children", tenantSubData);
				}
			}
		}
		return tenantsList;
	}
	
	private static String SQL_GET_EXP_HOST_DATA = "SQL_GET_EXP_HOST_DATA";
	private static String SQL_GET_LINK_DATA = "SQL_GET_LINK_DATA";
	@SuppressWarnings("rawtypes")
	public Map doGetBizNodeAndLink() {
		SQLExecutor executor = getSqlExecutor();
		return new NetTopoDAO(executor).doGetPhyLinkTopoData();
	}
	
	private static String SQL_GET_LINK_DATA_BY_HOST = "SQL_GET_LINK_DATA_BY_HOST";
	@SuppressWarnings("rawtypes")
	public List<Map> doGetBizNodeAndLinkByHost(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_LINK_DATA_BY_HOST);
		Map sqlVO = getSqlVO(SQL_GET_LINK_DATA_BY_HOST);
		return executor.executeNameParaQuery(sql, param, sqlVO);
	}
	
	private static String SQL_CHECK_BIZNODENAME_CONFLICT = "SQL_CHECK_BIZNODENAME_CONFLICT";
	@SuppressWarnings("rawtypes")
	public boolean doBizNodeNameConflictCheck(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CHECK_BIZNODENAME_CONFLICT);
		Map sqlVO = getSqlVO(SQL_CHECK_BIZNODENAME_CONFLICT);
		List<Map> biznode = executor.executeNameParaQuery(sql, param, sqlVO);
		return biznode!=null&&biznode.size()>0;
	}
	
}
