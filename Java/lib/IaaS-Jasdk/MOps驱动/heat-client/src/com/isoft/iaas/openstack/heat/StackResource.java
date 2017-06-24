package com.isoft.iaas.openstack.heat;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.heat.model.CreateStackParam;
import com.isoft.iaas.openstack.heat.model.Stack;
import com.isoft.iaas.openstack.heat.model.Stacks;

public class StackResource {

	private final OpenStackClient client;

	public StackResource(OpenStackClient client) {
		this.client = client;
	}

	public CreateStack create(CreateStackParam param) {
		return new CreateStack(param);
	}

	public List list() {
		return new List();
	}

	public GetStack byName(String name) {
		return new GetStack(name);
	}

	public DeleteStack deleteByName(String name) {
		return new DeleteStack(name);
	}

	public class CreateStack extends OpenStackRequest<Stack> {
		public CreateStack(CreateStackParam params) {
			super(client, HttpMethod.POST, "/stacks", Entity.json(params),
					Stack.class);
		}
	}

	public class DeleteStack extends OpenStackRequest<Void> {
		public DeleteStack(String name) {
			super(client, HttpMethod.DELETE, "/stacks/" + name, null,
					Void.class);
		}
	}

	public class GetStack extends OpenStackRequest<Stack> {
		public GetStack(String name) {
			super(client, HttpMethod.GET, "/stacks/" + name, null, Stack.class);
		}
	}

	public class List extends OpenStackRequest<Stacks> {
		public List() {
			super(client, HttpMethod.GET, "/stacks", null, Stacks.class);
		}
	}

}
