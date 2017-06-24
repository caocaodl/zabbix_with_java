package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class Sample implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("counter_name")
	private String countername;

	@JsonProperty("counter_type")
	private String countertype;

	@JsonProperty("counter_unit")
	private String counterunit;

	@JsonProperty("counter_volume")
	private String countervolume;

	@JsonProperty("message_id")
	private String messageid;

	@JsonProperty("project_id")
	private String projectid;

	@JsonProperty("recorded_at")
	private Date recordedAt;

	@JsonProperty("resource_id")
	private String resourceid;

	@JsonProperty("resource_metadata")
	private Map<String, Object> metadata;

	@JsonProperty
	private String source;

	@JsonProperty("user_id")
	private String userid;

	@JsonProperty
	private Date timestamp;

	public String getCountername() {
		return countername;
	}

	public String getCountertype() {
		return countertype;
	}

	public String getCounterunit() {
		return counterunit;
	}

	public String getCountervolume() {
		return countervolume;
	}

	public String getMessageid() {
		return messageid;
	}

	public String getProjectid() {
		return projectid;
	}

	public Date getRecordedAt() {
		return recordedAt;
	}

	public String getResourceid() {
		return resourceid;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public String getSource() {
		return source;
	}

	public String getUserid() {
		return userid;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
