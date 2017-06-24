package com.isoft.iaas.openstack.test;

import java.util.List;

import com.isoft.iaas.openstack.Configuration;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.OpsUtils;
import com.isoft.iaas.openstack.cinder.v2.model.BackStorInfo;
import com.isoft.iaas.openstack.cinder.v2.model.Volume;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Service;
import com.isoft.iaas.openstack.keystone.model.Services;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Flavor;
import com.isoft.iaas.openstack.nova.model.Hypervisors;
import com.isoft.iaas.openstack.nova.model.Hypervisors.Hypervisor;
import com.isoft.iaas.openstack.nova.model.OsConfigVal;
import com.isoft.iaas.openstack.nova.model.SecurityGroup;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Statistic;
import com.isoft.iaas.openstack.quantum.model.FloatingIp;
import com.isoft.iaas.openstack.quantum.model.FloatingIps;
import com.isoft.iaas.openstack.quantum.model.Subnet;

public class AA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		osConfigInfos();
//		admin();
	}
	
	public static void osConfigInfos() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		OsConfigVal a = $.getComputeClient().osConfigInfos().getVal("controller", "cpu_allocation_ratio").execute();
		System.out.println(a.getKeyNum());
	}
	
	public static void hypervisor_statistics() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		Statistic a = $.getComputeClient().hypervisors().statistic().execute();
		System.out.println(a.getVcpus());
	}
	
	public static void floatip() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		FloatingIps s = $.getNetworkClient().floatingIps().list().execute();
		
		for(FloatingIp u: s) {
			System.out.println(u.getTenantId() + " : " + u.getFloatingIp());
		}
	}
	
	public static void users() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		Users s = $.getIdentityClient().users().list().execute();
		
		for(User u: s) {
			System.out.println(u.getId() + ": " + u.getName());
		}
	}
	
	public static void hypervisorStatistics() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		Statistic s = $.getComputeClient().hypervisors().statistic().execute();
		
		System.out.println(s.getMemoryMb());
	}
	
	public static void servers() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		for(Subnet v: $.getNetworkClient().subnets().list().execute()) {
			System.out.println(v.getName());
		}
	}
	
	public static void volumnUsed() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		int size = 0;
		for(Volume v: $.getVolumeClient().volumes().list(false).execute().getList()) {
			size += v.getSize();
		}
		System.out.println(size);
	}
	
	public static void backStor() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		BackStorInfo a = $.getVolumeClient().volumes().backStor().execute();
		a.getAllCapGb();
		a.getFreeCapGb();
		System.out.println(a);
	}
	
	public static void tenants() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		Tenants tenants = OpsUtils.getOpenStackClientForAdmin().getIdentityClient().tenants().list().execute();
		for(Tenant t: tenants.getList()) {
			IaaSClient $t = OpsUtils.getOpenStackClient(t.getId());
			
			FloatingIps floatingips = $.getNetworkClient().floatingIps().list().execute();
			for(FloatingIp f: floatingips.getList()) {
				
			}
		}
	}
	
	public static void admin() {
		IaaSClient $ = OpsUtils.getOpenStackClientForAdmin();
		
		Object a = $.getTelemetryClient().alarms().list().execute();
		System.out.println(a);
	}
	
	
	static void log(Object o){
		System.out.println(o);
	}
	
	static void bak(){
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		Access access = keystone.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName(Configuration.KEYSTONE_ADMIN_TENANT_NAME).execute();
		Access.User user = access.getUser();
//		System.out.println(user);
		keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		keystone.token(access.getToken().getId());
		Services echoServices = keystone.services().list().execute();
		List<Service> services = echoServices.getList();
		for(Service s:services){
//			System.out.println(s);
		}
		User user1 = keystone.users().show(user.getId()).execute();
//		System.out.println(user1);
		
		Tenants tenants = keystone.tenants().list().execute();
		for(Tenant t: tenants.getList()) {
		//	System.out.println(t);
		}

		IaaSClient admClient = OpsUtils.getOpenStackClientForAdmin();
		
		admClient.getIdentityClient().tenants().list().execute();
		
		Nova novaClient = admClient.getComputeClient();
		Hypervisors hypervisors = novaClient.hypervisors().list().execute();
		for(Hypervisor hypervisor : hypervisors){
			System.out.println(hypervisor.getHypervisorHostname());
			Hypervisors hprs = novaClient.hypervisors().servers(hypervisor.getHypervisorHostname()).execute();
			List<com.isoft.iaas.openstack.nova.model.Hypervisors.Server> servers = hprs.getHypervisors().get(0).getServers();
			for(com.isoft.iaas.openstack.nova.model.Hypervisors.Server server :servers){
				System.out.println(server.getName());
			}
		}
		
//		Servers servers = novaClient.servers().list(false).execute();
//		for (Server server : servers) {
//			server = novaClient.servers().show(server.getId()).execute();
//			System.out.println(server.getAddresses().getAddresses().get("test").get(0).getAddr());
//			System.out.println(server);
//		}
//		
//		Quantum networkClient = admClient.getNetworkClient();
//		
//		Routers routerEcho = networkClient.routers().list().execute();
//		List<Router> routers = routerEcho.getList();
//		for (Router router : routers) {
//			System.out.println("---------- route ------------");
//			System.out.println(router);
//		}
//		
//		Subnets subnetEcho = networkClient.subnets().list().execute();
//		List<Subnet> subnets = subnetEcho.getList();
//		for(Subnet subnet : subnets){
//			System.out.println(subnet);
//		}
//		
//		Ports portEcho = networkClient.ports().list().execute();
//		List<Port> ports = portEcho.getList();
//		for(Port port : ports){
//			System.out.println(port);
//		}
	}
}
