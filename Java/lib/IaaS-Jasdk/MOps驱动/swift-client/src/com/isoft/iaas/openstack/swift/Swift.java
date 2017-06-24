package com.isoft.iaas.openstack.swift;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackClientConnector;
import com.isoft.iaas.openstack.swift.api.AccountResource;
import com.isoft.iaas.openstack.swift.api.ContainersResource;

public class Swift extends OpenStackClient {

	private final AccountResource ACCOUNT;

	private final ContainersResource CONTAINERS;

	public Swift(String endpoint, OpenStackClientConnector connector) {
		super(endpoint, connector);
		CONTAINERS = new ContainersResource(this);
		ACCOUNT = new AccountResource(this);
	}

	public Swift(String endpoint) {
		this(endpoint, null);
	}

	public ContainersResource containers() {
		return CONTAINERS;
	}

	public AccountResource account() {
		return ACCOUNT;
	}

}
