package com.isoft.iaas.openstack.quantum.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class FloatingIp implements Serializable {
	
	private static final long serialVersionUID = 1L;
    
	@JsonProperty
	private String id;

	@JsonProperty("router_id")
	private String routerId;
	
	@JsonProperty("tenant_id")
	private String tenantId;
	
	@JsonProperty("floating_network_id")
	private String floatingNetworkId;
	
	@JsonProperty("fixed_ip_address")
	private String fixedIp;
	
	@JsonProperty("floating_ip_address")
	private String floatingIp;
	
	@JsonProperty("port_id")
	private String portId;
	
	@JsonProperty
	private String status;

	
	public String getId() {
		return id;
	}

	public String getRouterId() {
		return routerId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getFloatingNetworkId() {
		return floatingNetworkId;
	}

	public String getFixedIp() {
		return fixedIp;
	}

	public String getFloatingIp() {
		return floatingIp;
	}

	public String getPortId() {
		return portId;
	}

	public String getStatus() {
		return status;
	}
}
