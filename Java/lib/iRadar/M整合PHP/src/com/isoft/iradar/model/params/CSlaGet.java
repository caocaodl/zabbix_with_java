package com.isoft.iradar.model.params;

import java.util.Map;

import com.isoft.types.CArray;

public class CSlaGet extends CParamWrapper {

	private static final long serialVersionUID = 1L;

	private Long[] serviceIds;
	private CArray<Map> intervals;

	public Long[] getServiceIds() {
		return serviceIds;
	}

	public void setServiceIds(Long... serviceIds) {
		this.serviceIds = serviceIds;
	}

	public CArray<Map> getIntervals() {
		return intervals;
	}

	public void setIntervals(CArray<Map> intervals) {
		this.intervals = intervals;
	}

	public void setInterval(Map intervals) {
		this.intervals = array(intervals);
	}
}
