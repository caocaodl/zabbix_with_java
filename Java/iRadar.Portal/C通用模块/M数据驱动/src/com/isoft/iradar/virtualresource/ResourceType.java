package com.isoft.iradar.virtualresource;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Server.Addresses.Address;
import com.isoft.iaas.openstack.nova.model.Servers;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyList;

public class ResourceType{
	
	private final static ResourceCollector VM_COLLECTOR = new ResourceCollector() {
		@Override public Collection<VirtualResource> collect(IaaSClient client, ResourceType type) {
			Servers srvs = client.getComputeClient().servers().list(true).queryParam("all_tenants", "1").execute();
			Collection<VirtualResource> rs = EasyList.build();
			for(Server srv: srvs) {
				String hostidOs = srv.getId();
				String tenantId = srv.getTenantId();
				List<String> ips = EasyList.build();
				if(srv.getAddresses() != null) {
					Iterator<List<Address>> it = srv.getAddresses().getAddresses().values().iterator();
					while(it.hasNext()) {
						List<Address> addrs = it.next();
						for(Address addr: addrs) {
							String ip = addr.getAddr();
							if(!Cphp.empty(ip)) {
								ips.add(ip);
							}
						}
					}
				}
				
				if(Cphp.empty(ips) || Cphp.empty(tenantId) || Cphp.empty(hostidOs)) {
					continue;
				}
				
				VirtualResource vr = new VirtualResource();
				vr.setId(hostidOs);
				vr.setIps(ips);
				vr.setName(srv.getName());
				vr.setTenantId(tenantId);
				vr.setType(type);
				rs.add(vr);
			}
			return rs;
		}
	};
	
	public final static List<ResourceType> ALL = EasyList.build(
		new ResourceType(String.valueOf(IMonConsts.MON_VM), "云主机", VM_COLLECTOR)
	);
	
	
	private String id;
	private String name;
	private ResourceCollector collector;
	
	protected ResourceType(String id, String name, ResourceCollector collector) {
		this.id = id;
		this.name = name;
		this.collector = collector;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public ResourceCollector getCollector() {
		return collector;
	}
	
}
