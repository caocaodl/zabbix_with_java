package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.keystone.v3.model.User;
import com.isoft.iaas.openstack.keystone.v3.model.Users;

public class GroupUsersResource extends GenericResource<User, Users> {

	public GroupUsersResource(OpenStackClient client, String path) {
		super(client, path, User.class, Users.class);
	}

}
