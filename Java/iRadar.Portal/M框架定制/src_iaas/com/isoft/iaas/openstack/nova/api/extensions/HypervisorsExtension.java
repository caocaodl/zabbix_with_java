package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Hypervisors;
import com.isoft.iaas.openstack.nova.model.Statistic;

public class HypervisorsExtension {

	private final OpenStackClient CLIENT;

	public HypervisorsExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public class List extends OpenStackRequest<Hypervisors> {
		public List() {
			super(CLIENT, HttpMethod.GET, "/os-hypervisors", null,Hypervisors.class);
		}
	}

	public Servers servers(String hostName) {
		return new Servers(hostName);
	}

	public class Servers extends OpenStackRequest<Hypervisors> {
		public Servers(String hostName) {
			super(CLIENT, HttpMethod.GET, new StringBuffer("/os-hypervisors/")
					.append(hostName).append("/servers").toString(), null, Hypervisors.class);
		}
	}
	
	public Statistics statistic() {
		return new Statistics();
	}
	public class Statistics extends OpenStackRequest<Statistic> {
		public Statistics() {
			super(CLIENT, HttpMethod.GET, "/os-hypervisors/statistics", null, Statistic.class);
		}
	}
}