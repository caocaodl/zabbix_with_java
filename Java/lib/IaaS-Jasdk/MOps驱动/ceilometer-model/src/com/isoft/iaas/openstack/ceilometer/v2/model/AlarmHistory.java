package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlarmHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("alarm_id")
	private String alarmid;

	@JsonProperty
	private String detail;

	@JsonProperty("event_id")
	private String eventid;

	@JsonProperty("on_behalf_of")
	private String onBehalfOf;

	@JsonProperty("project_id")
	private String projectid;

	@JsonProperty
	private Date timestamp;

	@JsonProperty
	private String type;

	@JsonProperty("user_id")
	private String userid;

	public String getAlarmid() {
		return alarmid;
	}

	public String getDetail() {
		return detail;
	}

	public String getEventid() {
		return eventid;
	}

	public String getOnBehalfOf() {
		return onBehalfOf;
	}

	public String getProjectid() {
		return projectid;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getType() {
		return type;
	}

	public String getUserid() {
		return userid;
	}

}
