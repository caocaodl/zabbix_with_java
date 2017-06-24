package com.isoft.biz.daoimpl.platform.topo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.platform.topo.IHostExpDAO;
import com.isoft.biz.dao.platform.topo.INetTopoDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.biz.method.Role;
import com.isoft.biz.vo.platform.topo.HostExp;
import com.isoft.biz.web.platform.topo.TopoDataOperAction;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.nova.model.Hypervisors;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Hypervisor;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Server;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class NetTopoDAO extends BaseDAO implements INetTopoDAO{

	public NetTopoDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static String SQL_GET_EXP_HOST_DATA = "SQL_GET_EXP_HOST_DATA";
	private static String SQL_GET_SUBNET_DATA = "SQL_GET_SUBNET_DATA";
	private static String SQL_GET_LINK_DATA = "SQL_GET_LINK_DATA";
	@SuppressWarnings("unchecked")
	public Map doGetPhyLinkTopoData() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_EXP_HOST_DATA);
		Map sqlVO = getSqlVO(SQL_GET_EXP_HOST_DATA);
		List<Map> nodes = executor.executeNameParaQuery(sql,sqlVO);
		sql = getSql(SQL_GET_LINK_DATA);
		sqlVO = getSqlVO(SQL_GET_LINK_DATA);
		List<Map> links = executor.executeNameParaQuery(sql,sqlVO);
		for(Map link:links){
			Nest.value(link, "name").$("");
			for(Map node:nodes){
				if(Nest.value(node,"id").asString().equals(Nest.value(link, "f").asString()) || Nest.value(node,"id").asString().equals(Nest.value(link, "t").asString()))
					Nest.value(link, "name").$(Nest.value(link, "name").asString().concat(Nest.value(node,"name").asString().concat(" ")));
				if(Cphp.isset(link,"name")&&Nest.value(link, "name").asString().split(" ").length>1)
					break;
				Nest.value(node,"error").$(Nest.value(node,"error").asBoolean());
			}
		}
		List<Map> edges = new ArrayList();
		for(Map link:links){
			Map edge = new HashMap(4);
			String[] keys = Nest.as(link).asCArray().keys();
			for(String key:keys){
				String value = Nest.value(link, key).asString();
				if("f".equals(key))
					key = "from";
				if("t".endsWith(key))
					key = "to";
				edge.put(key, value);
			}
			edges.add(edge);
		}
		
		Map paramMap = new LinkedMap();
		List<String> subnetList = new ArrayList();
		for(Map node:nodes){
			if(!Cphp.empty(Nest.value(node, "subnet").$())){
				subnetList.add(Nest.value(node,"subnet").asString());
			}
		}
		if(subnetList.size()>0){
			paramMap.put("subnets", subnetList);
			sql = getSql(SQL_GET_SUBNET_DATA);
			sqlVO = getSqlVO(SQL_GET_SUBNET_DATA);
			List<Map> subnets = executor.executeNameParaQuery(sql,paramMap,sqlVO);
			for(Map subnet:subnets)
				subnet.put("hostType", HostConstants.CATEGORY_SUBNET);
			nodes.addAll(subnets);
		}
		
		Map dataMap = new HashMap();
		dataMap.put("nodes", nodes);
		dataMap.put("edges", edges);
		doGetHypervisor(dataMap);
		doTopoDataSetXY((List<Map>)dataMap.get("nodes"),executor);
		TopoDataOperDAO.doGetGroupIdByHostTypeAndHostId(executor, (List<Map>)dataMap.get("nodes"));
		return dataMap;
	}
	
	public void doGetHypervisor(Map dataMap){
		CArray vmHostNameCA = doGetVMHostName();
		List<Map> nodes = Nest.value(dataMap, "nodes").asCArray().toList();
		List<Map> edges = Nest.value(dataMap, "edges").asCArray().toList();
		List<Hypervisors> hypervisors = DataDriver.getAllHypervisors();
		for(Hypervisors node : hypervisors){
			List<Hypervisor> hypervisorList = node.getHypervisors();
			for(Hypervisor hypervisor:hypervisorList){
				for(int i=0,length=nodes.size();i<length;i++){
					if(Nest.as(nodes.get(i).get("name")).asString().equals(hypervisor.getHypervisorHostname())){
						List<Server> servers = hypervisor.getServers();
						if(Cphp.empty(servers) || servers.size()==0)
							continue;
						for(Server server : servers){
							String vmName = server.getName();
							if(vmHostNameCA.containsKey(server.getUuid()))
								vmName = Nest.value(vmHostNameCA, server.getUuid()).asString();
							Map<String,String> nodeMap = new HashMap(3);
							nodeMap.put("id"      , server.getUuid());
							nodeMap.put("hostType", HostConstants.CATEGORY_VM);
							nodeMap.put("name"    , vmName);
							nodeMap.put("ownerHost", Nest.as(nodes.get(i).get("id")).asString());
							nodeMap.put("subnet"  , Nest.as(nodes.get(i).get("subnet")).asString());
							Map<String,String> edgeMap = new HashMap(4);
							edgeMap.put("id"  , Nest.as(nodes.get(i).get("id")).asString().concat("_").concat(server.getUuid()));
							edgeMap.put("name", Nest.as(nodes.get(i).get("name")).asString().concat(" ").concat(vmName));
							edgeMap.put("from", Nest.as(nodes.get(i).get("id")).asString());
							edgeMap.put("to"  , server.getUuid());
							nodes.add(nodeMap);
							edges.add(edgeMap);
						}
					}
				}
			}
		}
		Nest.value(dataMap, "nodes").$(nodes);
		Nest.value(dataMap, "edges").$(edges);
	}
	
	public CArray doGetVMHostName() {
		SQLExecutor executor = getSqlExecutor();
		SqlBuilder sqlparts = new SqlBuilder();
		sqlparts.select.put("host");
		sqlparts.select.put("hostid_os");
		sqlparts.select.put("name");
		sqlparts.from.put("hosts");
		sqlparts.where.put("hostid_os IS NOT NULL");
		CArray<Map> resultCA = DBUtil.DBselect(executor,sqlparts);
		CArray data = CArray.map();
		for(Map result:resultCA){
			data.put(Nest.value(result, "hostid_os").asString(), Nest.value(result, "name").asString());
		}
		return data;
	}

	public void doTopoDataSetXY(List<Map> dataList,SQLExecutor sqlExecutor){
		Map paraMap = new LinkedMap();
		paraMap.put("topoType", TopoDataOperAction.TOPO_PHY);
		TopoDataOperDAO topoDataOperDAO = new TopoDataOperDAO(sqlExecutor);
		
		Map location = topoDataOperDAO.doTopoDataLocOperGet(paraMap);
		Map tbnailHost = topoDataOperDAO.doPhyTopoTbnailHostGet();
		Map<String,Map> tbnailHostMap = Nest.value(tbnailHost, "dataMap").asCArray().toMap();
		
		List<String> tbnailHostNeedRemove = new ArrayList();
		CArray<String> dataIdsList = FuncsUtil.rda_objectValues(dataList, "id");
		
		for(Map data:dataList){
			if(Cphp.isset(location, Nest.as(data.get("id")).asString())){
				data.put("X", Nest.value(location, Nest.as(data.get("id")).asString(),"X").asDouble());
				data.put("Y", Nest.value(location, Nest.as(data.get("id")).asString(),"Y").asDouble());
			}
			if(Cphp.isset(tbnailHostMap, Nest.as(data.get("id")).asString())){
				data.put("ownerHost", Nest.value(tbnailHostMap, Nest.as(data.get("id")).asString(),"tbnailId").asString());
//				data.put("tbnailId", Nest.value(tbnailHostMap, Nest.as(data.get("id")).asString(),"tbnailId").asString());
//				data.put("tbnailNX", Nest.value(tbnailHostMap, Nest.as(data.get("id")).asString(),"tbnailNX").asDouble());
//				data.put("tbnailNY", Nest.value(tbnailHostMap, Nest.as(data.get("id")).asString(),"tbnailNY").asDouble());
				tbnailHostNeedRemove.add(Nest.as(data.get("id")).asString());
			}
		}
		Map<String,Map> tbnailHostMapNew = new LinkedMap();
		for(Entry<String, Map> e : tbnailHostMap.entrySet()){
			String key = Nest.as(e.getKey()).asString();
			Map value = e.getValue();
			if(!tbnailHostNeedRemove.contains(key))
				tbnailHostMapNew.put(key, value);
		}
		for(Entry<String, Map> e : tbnailHostMapNew.entrySet()){
			String key = e.getKey();
			Map value = e.getValue();
			Map hostMap = new LinkedMap();
			hostMap.put("id", key);
			hostMap.put("hostType", HostConstants.CATEGORY_GROUP);
			hostMap.put("name", Nest.value(value, "tbnailName").asString());
			hostMap.put("ownerHost", Nest.value(value, "tbnailId").asString());
			hostMap.put("width", Nest.value(value, "width").asString());
			hostMap.put("height", Nest.value(value, "height").asString());
			if(Cphp.isset(location, key)){
				hostMap.put("X", Nest.value(location, key,"X").asDouble());
				hostMap.put("Y", Nest.value(location, key,"Y").asDouble());
			}
			dataList.add(hostMap);
		}
	}
	
}
