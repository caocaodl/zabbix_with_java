package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class AlarmThresholdRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("comparison_operator")
	private String comparisonOperator;

	@JsonProperty("evaluation_periods")
	private Integer evaluationPeriods;

	@JsonProperty("exclude_outliers")
	private Boolean excludeOutliers;

	@JsonProperty("meter_name")
	private String meterName;

	@JsonProperty
	private Integer period;

	@JsonProperty
	private Object[] query;

	@JsonProperty
	private String statistic;

	@JsonProperty
	private Integer threshold;

	public String getComparisonOperator() {
		return comparisonOperator;
	}

	public Integer getEvaluationPeriods() {
		return evaluationPeriods;
	}

	public Boolean getExcludeOutliers() {
		return excludeOutliers;
	}

	public String getMeterName() {
		return meterName;
	}

	public Integer getPeriod() {
		return period;
	}

	public Object[] getQuery() {
		return query;
	}

	public String getStatistic() {
		return statistic;
	}

	public Integer getThreshold() {
		return threshold;
	}

	public void setComparisonOperator(String comparisonOperator) {
		this.comparisonOperator = comparisonOperator;
	}

	public void setEvaluationPeriods(Integer evaluationPeriods) {
		this.evaluationPeriods = evaluationPeriods;
	}

	public void setMeterName(String meterName) {
		this.meterName = meterName;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public void setQuery(Object[] query) {
		this.query = query;
	}

	public void setStatistic(String statistic) {
		this.statistic = statistic;
	}

	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}

}