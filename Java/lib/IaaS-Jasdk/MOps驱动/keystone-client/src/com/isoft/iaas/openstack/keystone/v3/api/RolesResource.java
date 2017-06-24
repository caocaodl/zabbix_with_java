package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.keystone.v3.model.Role;
import com.isoft.iaas.openstack.keystone.v3.model.Roles;
import com.isoft.iaas.openstack.keystone.v3.model.Users;

public class RolesResource extends GenericResource<Role, Roles> {

	public RolesResource(OpenStackClient client) {
		super(client, "/roles", Role.class, Roles.class);
	}

	public OpenStackRequest<Users> users(String domainId, String userId) {
		return CLIENT.get(new StringBuilder(path).append("/").append(domainId)
				.append("/users/").append(userId).append("/roles").toString(),
				Users.class);
	}

}
