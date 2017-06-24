package com.isoft.iaas.openstack.heat;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.heat.model.Resources;

/**
 * v1/​{tenant_id}​/stacks/​{stack_name}​/resources
 */
public class ResourcesResource {
	private final OpenStackClient client;

	public ResourcesResource(OpenStackClient client) {
		this.client = client;
	}

	public ListResources listResources(String name) {
		return new ListResources(name);
	}

	/**
	 * v1/​{tenant_id}​/stacks/​{stack_name}​/resources
	 */
	public class ListResources extends OpenStackRequest<Resources> {
		public ListResources(String name) {
			super(client, HttpMethod.GET, "/stacks/" + name + "/resources",
					null, Resources.class);
		}
	}
}
