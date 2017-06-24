package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.keystone.model.Services;
import com.isoft.iaas.openstack.keystone.v3.model.User;
import com.isoft.iaas.openstack.keystone.v3.model.Users;

public class UsersResource extends GenericResource<User, Users> {

	public UsersResource(OpenStackClient client) {
		super(client, "/users", User.class, Users.class);
	}

	public OpenStackRequest<Services> groups(String userId) {
		return CLIENT.get(new StringBuilder(path).append("/").append(userId)
				.append("/groups").toString(), Services.class);
	}

	public OpenStackRequest<Services> projects(String userId) {
		return CLIENT.get(new StringBuilder(path).append("/").append(userId)
				.append("/projects").toString(), Services.class);
	}

	public OpenStackRequest<Services> roles(String userId) {
		return CLIENT.get(new StringBuilder(path).append("/").append(userId)
				.append("/roles").toString(), Services.class);
	}

}
