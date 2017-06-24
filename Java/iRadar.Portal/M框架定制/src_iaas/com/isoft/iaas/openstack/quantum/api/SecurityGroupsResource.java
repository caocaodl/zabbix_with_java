package com.isoft.iaas.openstack.quantum.api;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.quantum.model.SecurityGroups;

public class SecurityGroupsResource {

	private final OpenStackClient CLIENT;

	public SecurityGroupsResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public class List extends OpenStackRequest<SecurityGroups> {
		public List() {
			super(CLIENT, HttpMethod.GET, "security-groups", null, SecurityGroups.class);
		}
	}

}
