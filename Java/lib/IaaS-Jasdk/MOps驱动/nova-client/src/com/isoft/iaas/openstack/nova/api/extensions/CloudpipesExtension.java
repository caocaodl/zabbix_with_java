package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Cloudpipe;
import com.isoft.iaas.openstack.nova.model.Cloudpipes;

public class CloudpipesExtension {

	private final OpenStackClient CLIENT;

	public CloudpipesExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public Create create(Cloudpipe cloudpipe) {
		return new Create(cloudpipe);
	}

	public class List extends OpenStackRequest<Cloudpipes> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/os-cloudpipes", null,
					Cloudpipes.class);
		}

	}

	public class Create extends OpenStackRequest<Cloudpipe> {

		public Create(Cloudpipe cloudpipe) {
			super(CLIENT, HttpMethod.POST, "/os-cloudpipes", Entity
					.json(cloudpipe), Cloudpipe.class);
		}

	}

}
