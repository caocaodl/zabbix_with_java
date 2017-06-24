package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("first_sample_timestamp")
	private Date firstSampleTimestamp;

	@JsonProperty("last_sample_timestamp")
	private Date lastSampleTimestamp;

	@JsonProperty
	private Map<String, Object> metadata;

	@JsonProperty("project_id")
	private String projectid;

	@JsonProperty("user_id")
	private String userid;

	@JsonProperty("resource_id")
	private String resourceid;

	@JsonProperty
	private String source;

	public Date getFirstSampleTimestamp() {
		return firstSampleTimestamp;
	}

	public Date getLastSampleTimestamp() {
		return lastSampleTimestamp;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public String getProjectid() {
		return projectid;
	}

	public String getUserid() {
		return userid;
	}

	public String getResourceid() {
		return resourceid;
	}

	public String getSource() {
		return source;
	}

}
