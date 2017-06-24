package com.isoft.iaas.openstack.quantum.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class GatewayInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("network_id")
	private String networkId;

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String id) {
		this.networkId = id;
	}

	@Override
	public String toString() {
		return "[networkId=" + networkId + "]";
	}
}
