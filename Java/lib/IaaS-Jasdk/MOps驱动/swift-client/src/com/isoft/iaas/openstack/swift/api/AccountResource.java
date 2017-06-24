package com.isoft.iaas.openstack.swift.api;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;

public class AccountResource {

	private final OpenStackClient CLIENT;

	public AccountResource(OpenStackClient client) {
		CLIENT = client;
	}

	public class ShowAccount extends OpenStackRequest<Void> {

		public ShowAccount() {
			// return target.request(MediaType.APPLICATION_JSON).head();
		}

	}

}
