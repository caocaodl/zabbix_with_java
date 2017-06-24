package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.keystone.v3.model.Policies;
import com.isoft.iaas.openstack.keystone.v3.model.Policy;

public class PoliciesResource extends GenericResource<Policy, Policies> {

	public PoliciesResource(OpenStackClient client) {
		super(client, "/policies", Policy.class, Policies.class);
	}

}
