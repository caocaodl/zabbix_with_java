package com.isoft.iaas.openstack.quantum;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackClientConnector;
import com.isoft.iaas.openstack.quantum.api.NetworksResource;
import com.isoft.iaas.openstack.quantum.api.PortsResource;
import com.isoft.iaas.openstack.quantum.api.RoutersResource;
import com.isoft.iaas.openstack.quantum.api.SubnetsResource;

public class Quantum extends OpenStackClient {

	private final NetworksResource NETWORKS;

	private final PortsResource PORTS;

	private final SubnetsResource SUBNETS;
	private final RoutersResource ROUTERS;

	public Quantum(String endpoint, OpenStackClientConnector connector) {
		super(endpoint, connector);
		NETWORKS = new NetworksResource(this);
		PORTS = new PortsResource(this);
		SUBNETS = new SubnetsResource(this);
		ROUTERS = new RoutersResource(this);

	}

	public Quantum(String endpoint) {
		this(endpoint, null);
	}

	public NetworksResource networks() {
		return NETWORKS;
	}

	public PortsResource ports() {
		return PORTS;
	}

	public SubnetsResource subnets() {
		return SUBNETS;
	}

	public RoutersResource routers() {
		return ROUTERS;
	}

}
