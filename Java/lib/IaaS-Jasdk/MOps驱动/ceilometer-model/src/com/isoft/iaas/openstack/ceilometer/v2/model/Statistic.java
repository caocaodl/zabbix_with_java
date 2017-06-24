package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class Statistic implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private BigDecimal max;
	
	@JsonProperty
	private BigDecimal min;
	
	@JsonProperty
	private BigDecimal avg;
	
	@JsonProperty
	private BigDecimal sum;
	
	@JsonProperty
	private int count;
	
	@JsonProperty
	private String unit;
	
	@JsonProperty
	private String groupby;
	
	@JsonProperty
	private Long duration;
	
	@JsonProperty("duration_start")
	private Date durationStart;
	
	@JsonProperty("duration_end")
	private Date durationEnd;
	
	@JsonProperty
	private Long period;
	
	@JsonProperty("period_start")
	private Date periodStart;
	
	@JsonProperty("period_end")
	private Date periodEnd;

}
