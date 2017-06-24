package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class Alarm implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("alarm_actions")
	private String[] alarmActions;

	@JsonProperty("ok_actions")
	private String[] okActions;

	@JsonProperty("insufficient_data_actions")
	private String[] insufficientDataActions;

	@JsonProperty("alarm_id")
	private String id;

	@JsonProperty
	private String description;

	@JsonProperty
	private Boolean enabled;

	@JsonProperty
	private String name;

	@JsonProperty("project_id")
	private String projectid;

	@JsonProperty("repeat_actions")
	private Boolean repeatActions;

	@JsonProperty
	private String state;

	@JsonProperty("state_timestamp")
	private Date stateTimestamp;

	@JsonProperty("threshold_rule")
	private AlarmThresholdRule thresholdRule;

	@JsonProperty("time_constraints")
	private TimeConstraint[] timeConstraints;

	@JsonProperty
	private Date timestamp;

	@JsonProperty
	private String type;

	@JsonProperty("user_id")
	private String userid;

	public String[] getAlarmActions() {
		return alarmActions;
	}

	public String[] getOkActions() {
		return okActions;
	}

	public String[] getInsufficientDataActions() {
		return insufficientDataActions;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getName() {
		return name;
	}

	public String getProjectid() {
		return projectid;
	}

	public Boolean getRepeatActions() {
		return repeatActions;
	}

	public String getState() {
		return state;
	}

	public Date getStateTimestamp() {
		return stateTimestamp;
	}

	public AlarmThresholdRule getThresholdRule() {
		return thresholdRule;
	}

	public TimeConstraint[] getTimeConstraints() {
		return timeConstraints;
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

	public void setAlarmActions(String[] alarmActions) {
		this.alarmActions = alarmActions;
	}

	public void setOkActions(String[] okActions) {
		this.okActions = okActions;
	}

	public void setInsufficientDataActions(String[] insufficientDataActions) {
		this.insufficientDataActions = insufficientDataActions;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public void setRepeatActions(Boolean repeatActions) {
		this.repeatActions = repeatActions;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setThresholdRule(AlarmThresholdRule thresholdRule) {
		this.thresholdRule = thresholdRule;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
