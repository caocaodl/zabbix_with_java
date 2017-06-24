package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Services;

public class ServicesExtension {

	private final OpenStackClient CLIENT;

	public ServicesExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}
	public class List extends OpenStackRequest<Services> {
		public List() {
			super(CLIENT, HttpMethod.GET, "/os-services", null, Services.class);
		}
	}

}