package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.keystone.model.Role;
import com.isoft.iaas.openstack.keystone.model.Roles;

public class ProjectGroupRolesResource extends GenericResource<Role, Roles> {

	public ProjectGroupRolesResource(OpenStackClient client, String path) {
		super(client, path, Role.class, Roles.class);
	}

	@Override
	public OpenStackRequest<Role> create(Role one) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OpenStackRequest<Role> show(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OpenStackRequest<Role> update(String id, Role one) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OpenStackRequest<Role> delete(String id) {
		throw new UnsupportedOperationException();
	}

}
