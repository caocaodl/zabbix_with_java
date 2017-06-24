package com.isoft.iaas.openstack.quantum.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class RouterInterface implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("subnet_id")
	String subnetId;
	@JsonProperty("port_id")
	String portId;
	@JsonProperty("tenant_id")
	String tenantId;
	@JsonProperty("id")
	String id;

}
