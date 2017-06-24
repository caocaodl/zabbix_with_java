package com.isoft.web.bean.iface;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.http.client.ClientProtocolException;
import org.ovirt.engine.sdk.Api;
import org.ovirt.engine.sdk.decorators.Cluster;
import org.ovirt.engine.sdk.decorators.DataCenter;
import org.ovirt.engine.sdk.decorators.Host;
import org.ovirt.engine.sdk.decorators.StorageDomain;
import org.ovirt.engine.sdk.decorators.VM;
import org.ovirt.engine.sdk.decorators.VmPool;
import org.ovirt.engine.sdk.exceptions.ServerException;

import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.StringUtil;
import com.isoft.iradar.data.DataDriver;
import com.isoft.ovirt.OvirtUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@Path("/srv")
public class IfaceIRadarServer {
	
	private final static String HEAD = "{\"data\": [";
	private final static String TAIL = "]}";
	
	private final static String DATA_HEAD_TENANTS = "{\"{#TID}\":\"";
	private final static String DATA_HEAD_COMMON = "{\"{#ID}\":\"";
	private final static String DATA_TAIL = "\"}";
			
	@Path("/tenants")
	@GET
	public String getAllTenants() {
		CArray<Map> tenants = DataDriver.getAllTenants();
		
		List<String> dataStrLs = EasyList.build();
		for(Map tenant: tenants) {
			String id = Nest.value(tenant, "id").asString();
			dataStrLs.add(DATA_HEAD_TENANTS+id+DATA_TAIL);
		}
		
		String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
		
		return HEAD+data+TAIL;
	}
	
	@Path("/datacenters")
	@GET
	public String getAllDataCenters(){
		try {
			Api api = OvirtUtil.getOvirtClient();
			List<String> dataStrLs = EasyList.build();
			List<DataCenter> dataCenters;
			dataCenters = api.getDataCenters().list();
			for (DataCenter dcataCenter : dataCenters) {
				dataStrLs.add(DATA_HEAD_COMMON+dcataCenter.getId()+DATA_TAIL);
			}
			String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
			return HEAD+data+TAIL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/clusters")
	@GET
	public String getAllClusters() throws ClientProtocolException {
		try {
			Api api = OvirtUtil.getOvirtClient();
			List<String> dataStrLs = EasyList.build();
			List<Cluster> clusters;
			clusters = api.getClusters().list();
			for (Cluster cluster : clusters) {
				dataStrLs.add(DATA_HEAD_COMMON+cluster.getId()+DATA_TAIL);
			}
			String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
			return HEAD+data+TAIL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/hostsNodes")
	@GET
	public String getHostsNodes() throws ClientProtocolException {
		try {
			Api api = OvirtUtil.getOvirtClient();
			List<String> dataStrLs = EasyList.build();
			List<Host> hostsNodes;
			hostsNodes = api.getHosts().list();
			for (Host hostNode : hostsNodes) {
				dataStrLs.add(DATA_HEAD_COMMON+hostNode.getId()+DATA_TAIL);
			}
			String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
			return HEAD+data+TAIL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/vmpools")
	@GET
	public String getVmPools() throws ClientProtocolException {
		try {
			Api api = OvirtUtil.getOvirtClient();
			List<String> dataStrLs = EasyList.build();
			List<VmPool> vmPools;
			vmPools = api.getVmPools().list();
			for (VmPool vmPool : vmPools) {
				dataStrLs.add(DATA_HEAD_COMMON+vmPool.getId()+DATA_TAIL);
			}
			String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
			return HEAD+data+TAIL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/storagedomains")
	@GET
	public String getStorageDomains() throws ClientProtocolException {
		try {
			Api api = OvirtUtil.getOvirtClient();
			List<String> dataStrLs = EasyList.build();
			List<StorageDomain> storageDomains;
			storageDomains = api.getStorageDomains().list();
			for (StorageDomain storageDomain : storageDomains) {
				dataStrLs.add(DATA_HEAD_COMMON+storageDomain.getId()+DATA_TAIL);
			}
			String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
			return HEAD+data+TAIL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Path("/vms")
	@GET
	public String getVms() throws ClientProtocolException {
		try {
			Api api = OvirtUtil.getOvirtClient();
			List<String> dataStrLs = EasyList.build();
			List<VM> vms;
			vms = api.getVMs().list();
			for (VM vm : vms) {
				dataStrLs.add(DATA_HEAD_COMMON+vm.getId()+DATA_TAIL);
			}
			String data = StringUtil.join(dataStrLs.toArray(new String[dataStrLs.size()]), ",");
			return HEAD+data+TAIL;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
