package com.isoft.iaas.openstack.nova.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class NetworkForAssign {

	@JsonProperty("uuid")
	private String id;
	@JsonProperty("fixed_ip")
	private String fixedIp;

	public String getId() {
		return id;
	}

	public String getFixedIp() {
		return fixedIp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFixedIp(String fixedIp) {
		this.fixedIp = fixedIp;
	}

}
