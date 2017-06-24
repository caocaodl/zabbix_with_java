package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.keystone.model.Service;
import com.isoft.iaas.openstack.keystone.model.Services;

public class ServicesResource extends GenericResource<Service, Services> {

	public ServicesResource(OpenStackClient client) {
		super(client, "/services", Service.class, Services.class);
	}

}
