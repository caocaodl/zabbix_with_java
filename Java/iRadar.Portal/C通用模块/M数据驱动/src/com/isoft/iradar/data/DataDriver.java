package com.isoft.iradar.data;

import static com.isoft.iradar.inc.DBUtil.DBselect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.glance.Glance;
import com.isoft.iaas.openstack.glance.model.Image;
import com.isoft.iaas.openstack.glance.model.Images;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Hypervisors;
import com.isoft.iaas.openstack.nova.model.OsConfigVal;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Hypervisor;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.quantum.Quantum;
import com.isoft.iaas.openstack.quantum.model.Network;
import com.isoft.iaas.openstack.quantum.model.Networks;
import com.isoft.iaas.openstack.quantum.model.Port;
import com.isoft.iaas.openstack.quantum.model.Port.Ip;
import com.isoft.iaas.openstack.quantum.model.Ports;
import com.isoft.iaas.openstack.quantum.model.Router;
import com.isoft.iaas.openstack.quantum.model.Routers;
import com.isoft.iaas.openstack.quantum.model.Subnet;
import com.isoft.iaas.openstack.quantum.model.Subnets;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 *
 * 设备的 
 * 负载(load average) 健康度 资源利用率 关键指标
 * 
 * @author BluE
 *
 */
public class DataDriver {
	public final static String PROTOTYPE_KEY_MODEL = "{#";
	/**
	 * 获取系统中所有的租户列表
	 * 
	 * @return 所有租户列表，字段含义
	 * 			id		租户ID
	 * 			name	租户名称
	 * 			desc	详细描述
	 * 			enable	是否可用
	 */
	public static CArray<Map> getAllTenants(){
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Tenants tenants = admClient.getIdentityClient().tenants().list().execute();
		CArray<Map> result = CArray.array();
		for(Tenant tenant: tenants.getList()) {
			String id = tenant.getId();
			String name = tenant.getName();
			String desc = tenant.getDescription();
			boolean enable = EasyObject.asBoolean(tenant.getEnabled());
			
			id = transformTentantId(id, name);
			
			result.put(id, CArray.map(
				"id", 		id,
				"name", 	name,
				"desc", 	desc,
				"enable", 	enable
			));
		}
		return result;
	}

	/**
	 * 获取当前用户所在租户的所有用户
	 * 
	 * @return 当前租户的所有用户列表，字段含义
	 * 			id		用户ID
	 * 			name	用户名称
	 * 			email	用户邮箱
	 * 			enable	是否可用
	 */
	public static CArray<Map> getUsersOfCurTenant(){
		String curTenantId = RadarContext.getIdentityBean().getTenantId();
		return getUsersOfTenant(curTenantId);
	}
	
	/**
	 * 获取指定租户的用户
	 * 
	 * @param tenantId 
	 * 
	 * @return 指定租户的所有用户列表，字段含义
	 * 			id		用户ID
	 * 			name	用户名称
	 * 			email	用户邮箱
	 * 			enable	是否可用
	 */
	private static CArray<Map> getUsersOfTenant(String tenantId){
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Users users = admClient.getIdentityClient().users().list().execute();
		CArray<Map> result = CArray.array();
		for(User tenant: users.getList()) {
			String tid = tenant.getTenantId();
			if(tid == null) {
				tid = tenant.getDefaultProjectId();
			}
			if(!tenantId.equals(tid)) {
				continue;
			}
			
			String id = tenant.getId();
			String name = tenant.getName();
			String email = tenant.getEmail();
			boolean enable = tenant.getEnabled();
			
			result.put(id, CArray.map(
				"id", 		id,
				"name", 	name,
				"email", 	email,
				"enable", 	enable
			));
		}
		return result;
	}
	
	/**
	 * 对租户ID进行转译
	 * 此方法主要是为了适用当前数据库的默认数据 和 Keystone的BUG，后期可直接返回当前租户ID
	 * 
	 * @param tenantId
	 * @param tenantName
	 * @return
	 */
	public static String transformTentantId(String tenantId, String tenantName) {
		return tenantId;
	}
	
	
	private final static String SQL_ITEM_BY_KEY = "select t.tenantid,t.itemid,t.value_type,t.units,t.valuemapid,t.name,t.delay,t.flags,t.hostid,t.key_,t.type FROM items t where t.hostid = #{hostId} and t.key_ = #{key}";
	/**
	 * 通过键值获取设备对应的监控指标ID
	 * 
	 * @param sqlExecutor
	 * @param hostId
	 * @param key
	 * @return 有返回ID，无返回NULL
	 */
	public static CArray<Map> getItemId(SQLExecutor sqlExecutor, Long hostId, String key) {
		try {
			Map sqlParam = EasyMap.build("hostId", hostId, "key", key);
			CArray<Map> results = DBselect(sqlExecutor, SQL_ITEM_BY_KEY,
					sqlParam);
			//		return Nest.value(results, 0, "itemid").asLong();
			return results;
		} catch (Exception e) {
			return null;
		}
	}
	
	public final static String SQL_ITEM_BY_PROTOTYPE_KEY = "" +
			"select tenantid,itemid,value_type,units,valuemapid,name,delay,hostid,key_,type from items where itemid in (" + 
			"  select itemid from item_discovery where parent_itemid in (" + 
			"    select itemid from items " +
			"		where " +
			"			hostid = #{hostId} " +
			"		and key_ = #{prototypeKey}" + 
			"		and flags = " + Defines.RDA_FLAG_DISCOVERY_PROTOTYPE +
			"  )" + 
			" and flags = " + Defines.RDA_FLAG_DISCOVERY_CREATED +
			")";
	/**
	 * 通过发现原型的item键值，获取对应生成的
	 * 
	 * @param sqlExecutor
	 * @param hostId
	 * @param prototypeKey
	 * @return
	 */
	public static CArray<Map> getItemIds(SQLExecutor sqlExecutor, Long hostId, String prototypeKey) {
		try {
			Map sqlParam = EasyMap.build("hostId", hostId, "prototypeKey",
					prototypeKey);
			CArray<Map> results = DBselect(sqlExecutor,
					SQL_ITEM_BY_PROTOTYPE_KEY, sqlParam);
			//		return TArray.as(FuncsUtil.objectValues(results, "itemid")).asLong();
			return results;
		} catch (Exception e) {
			return null;
		}
	}
	
	public final static String SQL_HOSTID_BY_HOST = " select hostid from hosts where host = #{host} and tenantid = #{tenantid} ";
	public static String getHostId(SQLExecutor sqlExecutor, String host, String tenantid) {
		Map sqlParam = EasyMap.build("host", host,"tenantid",tenantid);
		CArray<Map> results = DBselect(sqlExecutor, SQL_HOSTID_BY_HOST, sqlParam);
		return Cphp.empty(results)?null:Nest.value(results, 0,"hostid").asString();
	}
	
	/**判断是否是原型key，调用不同getitem方法
	 * @param sqlExecutor
	 * @param hostId
	 * @param Key
	 * @return
	 */
	public static CArray<Map> getItemsBykey(SQLExecutor sqlExecutor, Long hostId, String Key){
		CArray<Map> results= null;
		try {
			if (Key.contains(PROTOTYPE_KEY_MODEL)) {//是原型
				results = getItemIds(sqlExecutor, hostId, Key);
			} else {
				results = getItemId(sqlExecutor, hostId, Key);
			}
			return results;
		} catch (Exception e) {
			return results;
		}
	}
	
	/**
	 * 
	 * 获取IaaS中所有的物理机(包括从属的云主机)列表
	 * 
	 * @return  List<Hypervisors>
	 */
	public static List<Hypervisors> getAllHypervisors() {
		List<Hypervisors> hypervisorss = new ArrayList<Hypervisors>();
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Nova novaClient = admClient.getComputeClient();
		Hypervisors hypervisors = novaClient.hypervisors().list().execute();
		for (Hypervisor hypervisor : hypervisors) {
			hypervisorss.add(novaClient.hypervisors().servers(hypervisor.getHypervisorHostname()).execute());
		}
		return hypervisorss;
	}
	
	/**
	 * 
	 * 获取IaaS中所有subnet
	 * 
	 * @return  List<Subnet>
	 */
	public static List<Subnet> getAllSubnet() {
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Quantum networkClient = admClient.getNetworkClient();
		Subnets subnetEcho = networkClient.subnets().list().execute();
		return subnetEcho.getList();
	}
	
	public static List<Subnet> getAllSubnet(IaaSClient Client) {
		Quantum networkClient = Client.getNetworkClient();
		Subnets subnetEcho = networkClient.subnets().list().execute();
		return subnetEcho.getList();
	}
	
	/**
	 * 
	 * 获取IaaS中所有subnet的ipList
	 * 
	 * @return List<Subnet>
	 */
	public static List<Ip> getAllIps() {
		List<Ip> ipList = new ArrayList<Ip>();
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Quantum networkClient = admClient.getNetworkClient();
		Ports portEcho = networkClient.ports().list().execute();
		List<Port> ports = portEcho.getList();
		for (Port port : ports) {
			 ipList.addAll(port.getList());
		}
		return ipList;
	}
	
	public static List<Ip> getAllIps(IaaSClient Client) {
		List<Ip> ipList = new ArrayList<Ip>();
		Quantum networkClient = Client.getNetworkClient();
		Ports portEcho = networkClient.ports().list().execute();
		List<Port> ports = portEcho.getList();
		for (Port port : ports) {
			 ipList.addAll(port.getList());
		}
		return ipList;
	}
	
	/**
	 * 
	 * 获取IaaS中所有的Router
	 * 
	 * @return List<Subnet>
	 */
	public static List<Router> getAllRoute() {
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Quantum networkClient = admClient.getNetworkClient();
		Routers routeEcho = networkClient.routers().list().execute();
		return routeEcho.getList();
	}
	
	public static List<Router> getAllRoute(IaaSClient Client) {
		Quantum networkClient = Client.getNetworkClient();
		Routers routeEcho = networkClient.routers().list().execute();
		return routeEcho.getList();
	}
	
	/**
	 * 
	 * 获取IaaS中所有的Rort
	 * 
	 * @return List<Subnet>
	 */
	public static List<Port> getAllPort() {
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Quantum networkClient = admClient.getNetworkClient();
		Ports portEcho = networkClient.ports().list().execute();
		return  portEcho.getList();
	}
	
	public static List<Port> getAllPort(IaaSClient Client) {
		Quantum networkClient = Client.getNetworkClient();
		Ports portEcho = networkClient.ports().list().execute();
		return  portEcho.getList();
	}
	
    /**
	 * 
	 * 获取IaaS中所有的Network
	 * 
	 * @return List<Subnet>
	 */
	public static List<Network> getAllNetwork() {
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Quantum networkClient = admClient.getNetworkClient();
		Networks networkEcho = networkClient.networks().list().execute();
	    return networkEcho.getList();
	}
	
	public static List<Network> getAllNetwork(IaaSClient Client) {
		Quantum networkClient = Client.getNetworkClient();
		Networks networkEcho = networkClient.networks().list().execute();
	    return networkEcho.getList();
	}
	
    /**
	 * 
	 * 获取IaaS中所有的Image
	 * 
	 * @return List<Image>
	 */
	public static List<Image> getAllImages() {
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		Glance imageClient = admClient.getImageClient();
		Images images = imageClient.images().list(false).execute();
	    return images.getList();
	}

	
	public static Map getAllVmStatus(SQLExecutor executor) {
		return getAllVmStatus(executor,null);
	}
	
	public static Map getAllVmStatus(SQLExecutor executor,String hostid) {
		String sql = "select hostid,hostid_os from hosts where hostid_os=host ";
		CArray<Map> hosts = null;
		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		List<Server> servers = null;
		Map serverMap = CArray.map();
		if(!Cphp.empty(hostid)){
			sql = sql.concat("and hostid=#{hostid}");
			hosts = DBUtil.DBselect(executor, sql, EasyMap.build("hostid",hostid));
			String status = admClient.getComputeClient().servers().show(Nest.value(hosts, 0,"hostid_os").asString()).execute().getStatus();
			serverMap.put(hostid, status);
		}else{
			hosts = DBUtil.DBselect(executor, sql);
			servers = admClient.getComputeClient().servers().list(true).queryParam("all_tenants", "1").execute().getList();
			for(Map host:hosts){
				for(Server server:servers){
					if(Nest.value(host, "hostid_os").asString().equals(server.getId())){
						serverMap.put(Nest.value(host, "hostid").asString(), server.getStatus());
					}
				}
			}
		}
	    return serverMap;
	}
	
	public static float[] osConfigInfos() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		OsConfigVal cpu = $.getComputeClient().osConfigInfos().getVal("controller", "cpu_allocation_ratio").execute();
		OsConfigVal mem = $.getComputeClient().osConfigInfos().getVal("controller", "ram_allocation_ratio").execute();
		float[] ratios = {cpu.getKeyNum(),mem.getKeyNum()};
		return ratios;
	}
	
}
