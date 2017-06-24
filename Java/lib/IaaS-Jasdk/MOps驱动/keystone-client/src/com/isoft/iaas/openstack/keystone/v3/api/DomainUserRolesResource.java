package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.keystone.model.Role;
import com.isoft.iaas.openstack.keystone.model.Roles;

public class DomainUserRolesResource extends GenericResource<Role, Roles> {

	public DomainUserRolesResource(OpenStackClient client, String path) {
		super(client, path, Role.class, Roles.class);
	}

	public OpenStackRequest<Void> add(String roleId) {
		return new OpenStackRequest<Void>(CLIENT, HttpMethod.PUT,
				new StringBuilder(path).append("/").append(roleId).toString(),
				Entity.json(""), Void.class);
	}

	public OpenStackRequest<Void> remove(String roleId) {
		return new OpenStackRequest<Void>(CLIENT, HttpMethod.DELETE,
				new StringBuilder(path).append("/").append(roleId).toString(),
				null, Void.class);
	}

}
