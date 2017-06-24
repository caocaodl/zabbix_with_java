package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.keystone.v3.model.Endpoint;
import com.isoft.iaas.openstack.keystone.v3.model.Endpoints;

public class EndpointsResource extends GenericResource<Endpoint, Endpoints> {

	public EndpointsResource(OpenStackClient client) {
		super(client, "/endpoints", Endpoint.class, Endpoints.class);
	}

}
