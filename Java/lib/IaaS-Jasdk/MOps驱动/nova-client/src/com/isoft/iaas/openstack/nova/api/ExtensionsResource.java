package com.isoft.iaas.openstack.nova.api;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Extensions;

public class ExtensionsResource {

	private final OpenStackClient CLIENT;

	public ExtensionsResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list(boolean detail) {
		return new List(detail);
	}

	public class List extends OpenStackRequest<Extensions> {

		public List(boolean detail) {
			super(CLIENT, HttpMethod.GET, detail ? "extensions/detail"
					: "extensions", null, Extensions.class);
		}

	}

}
