package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.FloatingIpPools;

public class FloatingIpPoolsExtension {

	private final OpenStackClient CLIENT;

	public FloatingIpPoolsExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public class List extends OpenStackRequest<FloatingIpPools> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/os-floating-ip-pools", null,
					FloatingIpPools.class);
		}

	}

}
