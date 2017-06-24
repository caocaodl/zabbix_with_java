package com.isoft.iaas.openstack.keystone.api;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.keystone.model.Endpoint;
import com.isoft.iaas.openstack.keystone.model.Endpoints;

public class EndpointsResource {

	private OpenStackClient client;

	public EndpointsResource(OpenStackClient client) {
		this.client = client;
	}

	public List list() {
		return new List();
	}

	public Create create(Endpoint endpoint) {
		return new Create(endpoint);
	}

	public Show show(String id) {
		return new Show(id);
	}

	public Delete delete(String id) {
		return new Delete(id);
	}

	public class List extends OpenStackRequest<Endpoints> {

		public List() {
			super(client, HttpMethod.GET, "/endpoints", null, Endpoints.class);
		}

	}

	public class Create extends OpenStackRequest<Endpoint> {

		public Create(Endpoint endpoint) {
			super(client, HttpMethod.POST, "/endpoints", Entity.json(endpoint),
					Endpoint.class);
		}

	}

	public class Show extends OpenStackRequest<Endpoint> {

		public Show(String id) {
			super(client, HttpMethod.GET, new StringBuilder("/endpoints/")
					.append(id).toString(), null, Endpoint.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(client, HttpMethod.DELETE, new StringBuilder("/endpoints/")
					.append(id).toString(), null, Void.class);
		}

	}

}
