package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlarmCombinationRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("alarm_ids")
	private String[] alarmids;

	@JsonProperty
	private String operator;

	public void setAlarmids(String[] alarmids) {
		this.alarmids = alarmids;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String[] getAlarmids() {
		return alarmids;
	}

	public String getOperator() {
		return operator;
	}

}