package com.isoft.iaas.openstack.keystone.v3.api;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;

public class ProjectRolesResource {

	private final OpenStackClient CLIENT;

	private final String PATH;

	public ProjectRolesResource(OpenStackClient client, String path) {
		this.CLIENT = client;
		this.PATH = path;
	}

	public OpenStackRequest<Void> add(String roleId) {
		return new OpenStackRequest<Void>(CLIENT, HttpMethod.PUT,
				new StringBuilder(PATH).append("/").append(roleId).toString(),
				Entity.json(""), Void.class);
	}

	public OpenStackRequest<Void> remove(String roleId) {
		return new OpenStackRequest<Void>(CLIENT, HttpMethod.DELETE,
				new StringBuilder(PATH).append("/").append(roleId).toString(),
				null, Void.class);
	}

}
