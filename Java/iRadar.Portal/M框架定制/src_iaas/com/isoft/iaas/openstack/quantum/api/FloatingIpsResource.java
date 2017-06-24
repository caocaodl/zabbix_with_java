package com.isoft.iaas.openstack.quantum.api;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.quantum.model.FloatingIps;

public class FloatingIpsResource {

	private final OpenStackClient CLIENT;

	public FloatingIpsResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public class List extends OpenStackRequest<FloatingIps> {
		public List() {
			super(CLIENT, HttpMethod.GET, "floatingips", null, FloatingIps.class);
		}
	}

}
