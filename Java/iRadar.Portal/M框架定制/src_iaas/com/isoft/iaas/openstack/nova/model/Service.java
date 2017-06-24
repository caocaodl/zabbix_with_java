package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class Service implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String binary;
	
	@JsonProperty("disabled_reason")
	private String disabledReason;
	
	@JsonProperty
	private String host;
	
	@JsonProperty
	private Integer id;
	
	@JsonProperty
	private String state;
	
	@JsonProperty
	private String status;
	
	@JsonProperty("updated_at")
	private String updateAt;
	
	@JsonProperty
	private String zone;

	public String getBinary() {
		return binary;
	}

	public String getDisabledReason() {
		return disabledReason;
	}

	public String getHost() {
		return host;
	}

	public Integer getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public String getStatus() {
		return status;
	}

	public String getUpdateAt() {
		return updateAt;
	}

	public String getZone() {
		return zone;
	}
}
