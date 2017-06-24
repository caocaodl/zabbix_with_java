package com.isoft.biz.daoimpl.platform.topo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;


import com.isoft.biz.dao.IDAO;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ITopoDataOperDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.web.platform.topo.TopoDataOperAction;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TopoDataOperDAO extends BaseDAO implements ITopoDataOperDAO {

	public TopoDataOperDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	/**
	 * param:
	 * 		1.tenant
	 * 		2.user
	 * 		tenant和user在租户视图的  客户业务拓扑 和 虚拟链路拓扑 下生效
	 */
	private static String SQL_TOPO_DATA_LOCATION_SAVE = "SQL_TOPO_DATA_LOCATION_SAVE";
	public boolean doTopoDataLocOperSave(IIdentityBean identityBean) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DATA_LOCATION_SAVE);
		CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "nodes").asCArray();
		if(!Cphp.empty(nodes)){
			Map param = new LinkedMap();
			for(Map node:nodes){
				String topoType = Nest.value(node, "topoType").asString();
				param.put("topoType", topoType);
				if(TopoDataOperAction.TOPO_BIZ.equals(topoType)||TopoDataOperAction.TOPO_VIRTTENANT.equals(topoType)){
					param.put("tenant", identityBean.getTenantId());
				}
				break;
			}
			doTopoDataLocOperDel(param);
		}
		String id = null;
		int count = 0;
		for(Map node:nodes){
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			node.put("id", id);
			count += executor.executeInsertDeleteUpdate(sql, node);
		}
		if(count!=nodes.size()){
			return false;
		}
		return true;
	}
	
	/**
	 * param:
	 * 		1.tenant
	 * 		2.user
	 * 		tenant和user在租户视图的  客户业务拓扑 和 虚拟链路拓扑 下生效
	 */
	private static String SQL_TOPO_DATA_LOCATION_DEL = "SQL_TOPO_DATA_LOCATION_DEL";
	public void doTopoDataLocOperDel(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DATA_LOCATION_DEL);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static String SQL_TOPO_DATA_LOCATION_GET = "SQL_TOPO_DATA_LOCATION_GET";
	public Map doTopoDataLocOperGet(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DATA_LOCATION_GET);
		Map sqlVO = getSqlVO(SQL_TOPO_DATA_LOCATION_GET);
		List<Map> locations = executor.executeNameParaQuery(sql, paraMap, sqlVO);
		Map xy = new LinkedMap();
		for(Map location:locations){
			xy.put(location.get("hostId"), location);
		}
		return xy;
	}
	
	private static String SQL_BIZ_TOPO_DATA_LOCATION_GET = "SQL_BIZ_TOPO_DATA_LOCATION_GET";
	public Map doBizTopoDataLocOperGet(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_BIZ_TOPO_DATA_LOCATION_GET);
		Map sqlVO = getSqlVO(SQL_BIZ_TOPO_DATA_LOCATION_GET);
		List<Map> locations = executor.executeNameParaQuery(sql, paraMap, sqlVO);
		Map xy = new LinkedMap();
		for(Map location:locations){
			xy.put(location.get("hostId"), location);
		}
		return xy;
	}
	
	/**
	 * 保存机房拓扑中的机柜节点坐标数据
	 * 所需参数：
	 * 		1.hostId	对应机柜Id
	 * 		2.nodeType	NODECABINET
	 * 		3.topoType	TopoCab
	 * 		4.X			x坐标
	 * 		5.Y			y坐标
	 */
	private static String SQL_CAB_TOPO_CABINET_DATA_LOCATION_SAVE = "SQL_CAB_TOPO_CABINET_DATA_LOCATION_SAVE";
	public boolean doCabTopoCabinetDataLocSave(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CAB_TOPO_CABINET_DATA_LOCATION_SAVE);
		CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "nodes").asCArray();
		if(!Cphp.empty(nodes)){
			doTopoDataLocOperDel(nodes,TopoDataOperAction.TOPO_CAB);
			String id = null;
			int count = 0;
			for(Map node:nodes){
				id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
				node.put("id", id);
				count += executor.executeInsertDeleteUpdate(sql, node);
			}
			if(count!=nodes.size()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 保存机房拓扑中的设备节点坐标数据
	 * 所需参数：
	 * 		1.hostId	对应设备hostId
	 * 		2.nodeType	NODEHOST
	 * 		3.topoType	TopoCab
	 * 		4.X			x坐标
	 * 		5.Y			y坐标
	 */
	private static String SQL_CAB_TOPO_HOST_DATA_LOCATION_SAVE = "SQL_CAB_TOPO_HOST_DATA_LOCATION_SAVE";
	public boolean doCabTopoHostDataLocSave(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CAB_TOPO_HOST_DATA_LOCATION_SAVE);
		CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "nodes").asCArray();
		if(!Cphp.empty(nodes)){
			doTopoDataLocOperDel(nodes,TopoDataOperAction.TOPO_CAB);
			String id = null;
			int count = 0;
			for(Map node:nodes){
				id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
				node.put("id", id);
				count += executor.executeInsertDeleteUpdate(sql, node);
			}
			if(count!=nodes.size()){
				return false;
			}
		}
		return true;
	}
	
	private static String SQL_TOPO_DATA_LOCATION_DEL_BY_HOSTIDS = "SQL_TOPO_DATA_LOCATION_DEL_BY_HOSTIDS";
	public void doTopoDataLocOperDel(CArray<Map> nodes,String topoType){
		SQLExecutor executor = getSqlExecutor();
		List<String> hostIds = new ArrayList();
		for(Map node:nodes){
			hostIds.add(Nest.value(node, "hostId").asString());
		}
		Map paraMap = new LinkedMap();
		paraMap.put("hostIds", hostIds);
		paraMap.put("topoType", topoType);
		String sql = getSql(SQL_TOPO_DATA_LOCATION_DEL_BY_HOSTIDS);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static String SQL_PHY_TOPO_TBNAIL_DATA_SAVE = "SQL_PHY_TOPO_TBNAIL_DATA_SAVE";
	private static String SQL_PHY_TOPO_TBNAIL_HOST_DATA_SAVE = "SQL_PHY_TOPO_TBNAIL_HOST_DATA_SAVE";
	public String doPhyTopoTbnailSave(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PHY_TOPO_TBNAIL_DATA_SAVE);
		String tbnailId = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
		Map paraMap = new LinkedMap();
		String tbnailName = Nest.value(RadarContext._REQUEST(), "name").asString();
		paraMap.put("id", tbnailId);
		paraMap.put("tbnailName", tbnailName);
		paraMap.put("hostId", tbnailId);
		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
			sql = getSql(SQL_PHY_TOPO_TBNAIL_HOST_DATA_SAVE);
			CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "children").asCArray();
			for(Map node:nodes){
				node.put("id", getFlowcode(NameSpaceEnum.T_TOPO_LOCATION));
				node.put("tbnailId", tbnailId);
				executor.executeInsertDeleteUpdate(sql, node);
			}
			return tbnailId;
		}
		return null;
	}
	
	private static String SQL_PHY_TOPO_TBNAIL_DATA_DEL = "SQL_PHY_TOPO_TBNAIL_DATA_DEL";
	private static String SQL_PHY_TOPO_TBNAIL_HOST_DATA_GET = "SQL_PHY_TOPO_TBNAIL_HOST_DATA_GET";
	private static String SQL_PHY_TOPO_TBNAIL_HOST_DATA_DEL = "SQL_PHY_TOPO_TBNAIL_HOST_DATA_DEL";
	public List<Map> doPhyTopoTbnailDel(Map param){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PHY_TOPO_TBNAIL_DATA_DEL);
		if(executor.executeInsertDeleteUpdate(sql, param)==1){
			sql = getSql(SQL_PHY_TOPO_TBNAIL_HOST_DATA_GET);
			Map sqlVO = getSqlVO(SQL_PHY_TOPO_TBNAIL_HOST_DATA_GET);
			List<Map> tbnailHostList = executor.executeNameParaQuery(sql, param, sqlVO);
			sql = getSql(SQL_PHY_TOPO_TBNAIL_HOST_DATA_DEL);
			executor.executeInsertDeleteUpdate(sql, param);
			return tbnailHostList;
		}
		return null;
	}
	
	private static String SQL_PHY_TOPO_DATA_SAVE = "SQL_PHY_TOPO_DATA_SAVE";
	private static String SQL_PHY_TOPO_NODE_DATA_DELETE = "SQL_PHY_TOPO_NODE_DATA_DELETE";
	private static String SQL_PHY_TOPO_LOC_DATA_DELETE = "SQL_PHY_TOPO_LOC_DATA_DELETE";
	public boolean doPhyTopoLocSave(){
		SQLExecutor executor = getSqlExecutor();
		CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "nodes").asCArray();
		if(!Cphp.empty(nodes)){
			Map topoTypeMap = new LinkedMap();
			topoTypeMap.put("TopoPhy", TopoDataOperAction.TOPO_PHY);
			String sql = getSql(SQL_PHY_TOPO_NODE_DATA_DELETE);
			executor.executeInsertDeleteUpdate(sql, topoTypeMap);
			sql = getSql(SQL_PHY_TOPO_LOC_DATA_DELETE);
			executor.executeInsertDeleteUpdate(sql, topoTypeMap);
			int count = 0;
			List<Map> paraList = new ArrayList();
			doPhyTopoDataAnalysis(nodes,paraList,null);
			Map sqlMap = CArray.map(TopoDataOperAction.NODE_GROUP,getSql(SQL_PHY_TOPO_TBNAIL_DATA_SAVE),
									TopoDataOperAction.NODE_HOST,getSql(SQL_PHY_TOPO_TBNAIL_HOST_DATA_SAVE),
									"LOCATIONSQL",getSql(SQL_TOPO_DATA_LOCATION_SAVE));
			for(Map paraMap:paraList){
				if(TopoDataOperAction.NODE_GROUP.equals(Nest.value(paraMap, "nodeType").asString()))
					executor.executeInsertDeleteUpdate(Nest.value(sqlMap, TopoDataOperAction.NODE_GROUP).asString(), paraMap);
				else
					executor.executeInsertDeleteUpdate(Nest.value(sqlMap, TopoDataOperAction.NODE_HOST).asString(), paraMap);
				executor.executeInsertDeleteUpdate(Nest.value(sqlMap, "LOCATIONSQL").asString(), paraMap);
				count++;
			}
			if(count==paraList.size()){
				return true;
			}
		}
		return false;
	}
	
	public void doPhyTopoDataAnalysis(CArray<Map> nodes,List<Map> paraList,String tbnailId){
		String id = null;
		for(Map node:nodes){
			id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
			Map paraMap = new LinkedMap();
			paraMap.put("id", id);
			if(HostConstants.CATEGORY_GROUP.equals(Nest.value(node, "hostType").asString())){
				paraMap.put("tbnailName", Nest.value(node, "name").asString());
				paraMap.put("hostId", id);
				if(!Cphp.empty(tbnailId))
					paraMap.put("tbnailId", tbnailId);
				paraMap.put("nodeType", TopoDataOperAction.NODE_GROUP);
				paraMap.put("width", Nest.value(node, "width").asString());
				paraMap.put("height", Nest.value(node, "height").asString());
				if(!Cphp.empty(Nest.value(node, "children").$())){
					doPhyTopoDataAnalysis(Nest.value(node, "children").asCArray(),paraList,id);
				}
			}else{
				//paraMap.put("id", getFlowcode(NameSpaceEnum.T_TOPO_LOCATION));
				if(!Cphp.empty(tbnailId))
					paraMap.put("tbnailId", tbnailId);
				paraMap.put("hostId", Nest.value(node, "hostId").asString());
				paraMap.put("nodeType", TopoDataOperAction.NODE_HOST);
			}
			paraMap.put("X", Nest.value(node, "X").asString());
			paraMap.put("Y", Nest.value(node, "Y").asString());
			paraMap.put("topoType", TopoDataOperAction.TOPO_PHY);
			paraList.add(paraMap);
		}
	}
	
	private static String SQL_PHY_TOPO_TBNAIL_HOST_DATA = "SQL_PHY_TOPO_TBNAIL_HOST_DATA";
	public Map doPhyTopoTbnailHostGet(){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PHY_TOPO_TBNAIL_HOST_DATA );
		Map sqlVO = getSqlVO(SQL_PHY_TOPO_TBNAIL_HOST_DATA);
		List<Map> dataList =  executor.executeNameParaQuery(sql, new LinkedMap(), sqlVO);
		Map dataMap = new LinkedMap();
		for(Map data:dataList){
			dataMap.put(data.get("hostId"), data);
		}
		Map tbnailHostMap = new LinkedMap();
		tbnailHostMap.put("dataMap", dataMap);
		tbnailHostMap.put("dataList", dataList);
		return tbnailHostMap;
	}
	
	private static String SQL_BIZ_TOPO_DATA_SAVE = "SQL_BIZ_TOPO_DATA_SAVE";
	private static String SQL_BIZ_TOPO_BIZ_NODE_DATA_UPDATE = "SQL_BIZ_TOPO_BIZ_NODE_DATA_UPDATE";
	public boolean doBizTopoLocSave(){
		SQLExecutor executor = getSqlExecutor();
		String sql = "";
		String bizTopoId = Nest.value(RadarContext._REQUEST(), "bizTopoId").asString();
		CArray<Map> nodes = Nest.value(RadarContext._REQUEST(), "nodes").asCArray();
		if(!Cphp.empty(nodes)){
			doBizTopoDataLocOperDel(nodes,bizTopoId);
			String id = null;
			int count = 0;
			for(Map node:nodes){
				if(TopoDataOperAction.bizAreaCA.containsValue(Nest.value(node, "nodeType").asString())){
					sql = getSql(SQL_BIZ_TOPO_BIZ_NODE_DATA_UPDATE);
					executor.executeInsertDeleteUpdate(sql, node);
				}
				sql = getSql(SQL_BIZ_TOPO_DATA_SAVE);
				id = getFlowcode(NameSpaceEnum.T_TOPO_LOCATION);
				node.put("id", id);
				count += executor.executeInsertDeleteUpdate(sql, node);
			}
			if(count!=nodes.size()){
				return true;
			}
		}
		return false;
	}
	
	private static String SQL_BIZ_TOPO_DATA_LOCATION_DEL_BY_HOSTIDS = "SQL_BIZ_TOPO_DATA_LOCATION_DEL_BY_HOSTIDS";
	public void doBizTopoDataLocOperDel(CArray<Map> nodes,String bizTopoId){
		SQLExecutor executor = getSqlExecutor();
		List<String> hostIds = new ArrayList();
		for(Map node:nodes){
			hostIds.add(Nest.value(node, "hostId").asString());
		}
		Map paraMap = new LinkedMap();
		paraMap.put("bizTopoId", bizTopoId);
		paraMap.put("hostIds", hostIds);
		String sql = getSql(SQL_BIZ_TOPO_DATA_LOCATION_DEL_BY_HOSTIDS);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static String SQL_TOPO_GET_GROUP_ID_BY_HOSTTYPE_AND_HOST_ID = "SQL_TOPO_GET_GROUP_ID_BY_HOSTTYPE_AND_HOST_ID";
	public String doGetGroupIdByHostTypeAndHostId(String hostType,String hostId){
		CArray<Long> MON_DEVICE_HOSTTYPE = Nest.value(HostConstants.MON_DEVICE_HOSTTYPE,hostType).asCArray();
		Map paraMap = new LinkedMap();
		paraMap.put("hostId", hostId);
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_GET_GROUP_ID_BY_HOSTTYPE_AND_HOST_ID);
		Map sqlVO = getSqlVO(SQL_TOPO_GET_GROUP_ID_BY_HOSTTYPE_AND_HOST_ID);
		List<Map> groups = executor.executeNameParaQuery(sql, paraMap, sqlVO);
		Long groupId = 0L;
		boolean resultFlag = false;
		for(Map group:groups){
			groupId = Nest.value(group, "groupId").asLong();
			if(Cphp.empty(MON_DEVICE_HOSTTYPE))
				return null;
			if(MON_DEVICE_HOSTTYPE.containsValue(Cphp.empty(groupId)?"":groupId)){
				resultFlag = true;
				return Nest.as(groupId).asString();
			}
		}
		if(!resultFlag){
			CArray<IMonGroup> defaultGroups = IMonGroup.showableGroups();
			CArray<Long> defaultGroupIds = Nest.as(defaultGroups.keysAsLong()).asCArray();
			for(Map group:groups){
				groupId = Nest.value(group, "groupId").asLong();
				if(defaultGroupIds.containsValue(groupId)){
					resultFlag = true;
					return Nest.as(groupId).asString();
				}
			}
		}
		return !resultFlag?null:Nest.as(groupId).asString();
	}
	
	public static String doGetGroupIdByHostTypeAndHostId(TopoDataOperDAO topoDataOperDAO,String hostType,String hostId){
		return topoDataOperDAO.doGetGroupIdByHostTypeAndHostId(hostType, hostId);
	}
	
	public static void doGetGroupIdByHostTypeAndHostId(SQLExecutor sqlExecutor,List<Map> nodes){
		TopoDataOperDAO topoDataOperDAO = new TopoDataOperDAO(sqlExecutor);
		String hostId = "";
		for(Map node:nodes){
			hostId = topoDataOperDAO.doGetHostIdByVMTypeAndHostIdOs(sqlExecutor,node);
			node.put("hostId", hostId);
			if(!Cphp.empty(Nest.value(node, "children").$())){
				List<Map> children = Nest.value(node, "children").asCArray().toList();
				for(Map child:children){
					hostId = topoDataOperDAO.doGetHostIdByVMTypeAndHostIdOs(sqlExecutor,child);
					child.put("hostId", hostId);
					child.put("groupId", doGetGroupIdByHostTypeAndHostId(topoDataOperDAO,Nest.value(child, "hostType").asString(),hostId));
				}
			}else
				node.put("groupId", doGetGroupIdByHostTypeAndHostId(topoDataOperDAO,Nest.value(node, "hostType").asString(),hostId));
		}
	}
	
	private static String SQL_TOPO_GET_GROUP_ID_BY_HOSTNAME_FROM_NET = "SQL_TOPO_GET_GROUP_ID_BY_HOSTNAME_FROM_NET";
	public String doGetHostIdByVMTypeAndHostIdOs(SQLExecutor sqlExecutor,Map node){
		String id = Nest.value(node, "id").asString();
		CHostGet option = new CHostGet();
		if(HostConstants.CATEGORY_VM.equals(Nest.value(node, "hostType").asString())){
			option.setOutput(new String[]{"hostid"});
			option.setFilter("hostid_os",id);
			option.setEditable(false);
			CArray<Map> result = API.Host(RadarContext.getIdentityBean(), sqlExecutor).get(option);
			if(!Cphp.empty(result))
				return Nest.value(result, 0,"hostid").asString();
		}
		option = new CHostGet();
		option.setHostIds(Nest.as(id).asLong());
		option.setOutput(new String[]{"hostid"});
		option.setEditable(false);
		CArray<Map> result = API.Host(RadarContext.getIdentityBean(), sqlExecutor).get(option);
		if(!Cphp.empty(result))
			return Nest.value(result, 0,"hostid").asString();
		else{
			String sql = getSql(SQL_TOPO_GET_GROUP_ID_BY_HOSTNAME_FROM_NET);
			Map sqlVO = getSqlVO(SQL_TOPO_GET_GROUP_ID_BY_HOSTNAME_FROM_NET);
			List<Map> netResult = sqlExecutor.executeNameParaQuery(sql, node, sqlVO);
			if(!Cphp.empty(netResult))
				return Nest.value(Nest.as(netResult).asCArray(), 0,"hostid").asString();
		}
		return id;
	}
	
}