package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class Meter implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("meter_id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("project_id")
	private String projectid;

	@JsonProperty("resource_id")
	private String resourceid;

	@JsonProperty("source")
	private String source;

	@JsonProperty("type")
	private String type;

	@JsonProperty("unit")
	private String unit;

	@JsonProperty("user_id")
	private String userid;

	public String getName() {
		return name;
	}

	public String getProjectid() {
		return projectid;
	}

	public String getResourceid() {
		return resourceid;
	}

	public String getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

	public String getUnit() {
		return unit;
	}

	public String getUserid() {
		return userid;
	}

}
