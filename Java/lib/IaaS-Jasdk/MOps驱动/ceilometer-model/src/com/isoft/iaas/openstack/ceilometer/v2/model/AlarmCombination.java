package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlarmCombination implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("alarm_id")
	private String id;

	@JsonProperty("alarm_actions")
	private String[] alarmActions;

	@JsonProperty("combination_rule")
	private AlarmCombinationRule combinationRule;

	@JsonProperty
	private String description;

	@JsonProperty
	private Boolean enabled;

	@JsonProperty("insufficient_data_actions")
	private String[] insufficientDataActions;

	@JsonProperty
	private String name;

	@JsonProperty("ok_actions")
	private String[] okActions;

	@JsonProperty("project_id")
	private String projectid;

	@JsonProperty("repeat_actions")
	private Boolean repeatActions;

	@JsonProperty
	private String state;

	@JsonProperty
	private String type;

	@JsonProperty("user_id")
	private String userid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAlarmActions(String[] alarmActions) {
		this.alarmActions = alarmActions;
	}

	public void setCombinationRule(AlarmCombinationRule combinationRule) {
		this.combinationRule = combinationRule;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setInsufficientDataActions(String[] insufficientDataActions) {
		this.insufficientDataActions = insufficientDataActions;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOkActions(String[] okActions) {
		this.okActions = okActions;
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

	public void setType(String type) {
		this.type = type;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String[] getAlarmActions() {
		return alarmActions;
	}

	public AlarmCombinationRule getCombinationRule() {
		return combinationRule;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String[] getInsufficientDataActions() {
		return insufficientDataActions;
	}

	public String getName() {
		return name;
	}

	public String[] getOkActions() {
		return okActions;
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

	public String getType() {
		return type;
	}

	public String getUserid() {
		return userid;
	}

}
