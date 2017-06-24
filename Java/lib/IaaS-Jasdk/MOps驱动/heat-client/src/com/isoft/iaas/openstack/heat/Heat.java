package com.isoft.iaas.openstack.heat;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackClientConnector;

/**
 * Reference: http://api.openstack.org/api-ref-orchestration.html
 */
public class Heat extends OpenStackClient {

	private final StackResource stacks;
	private final ResourcesResource resources;

	public Heat(String endpoint, OpenStackClientConnector connector) {
		super(endpoint, connector);
		stacks = new StackResource(this);
		resources = new ResourcesResource(this);
	}

	public Heat(String endpoint) {
		this(endpoint, null);
	}

	public StackResource getStacks() {
		return stacks;
	}

	public ResourcesResource getResources() {
		return resources;
	}
}
