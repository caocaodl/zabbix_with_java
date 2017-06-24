package com.isoft.iradar.model.params;

public class CServiceGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] serviceIds;
	private Long[] parentIds;
	private Long[] childIds;

	private Object selectParent;
	private Object selectDependencies;
	private Object selectParentDependencies;
	private Object selectTimes;
	private Object selectAlarms;
	private Object selectTrigger;

	public Long[] getServiceIds() {
		return serviceIds;
	}

	public void setServiceIds(Long... serviceIds) {
		this.serviceIds = serviceIds;
	}

	public Long[] getParentIds() {
		return parentIds;
	}

	public void setParentIds(Long... parentIds) {
		this.parentIds = parentIds;
	}

	public Long[] getChildIds() {
		return childIds;
	}

	public void setChildIds(Long... childIds) {
		this.childIds = childIds;
	}

	public Object getSelectParent() {
		return selectParent;
	}

	public void setSelectParent(Object selectParent) {
		this.selectParent = selectParent;
	}

	public Object getSelectDependencies() {
		return selectDependencies;
	}

	public void setSelectDependencies(Object selectDependencies) {
		this.selectDependencies = selectDependencies;
	}

	public Object getSelectParentDependencies() {
		return selectParentDependencies;
	}

	public void setSelectParentDependencies(Object selectParentDependencies) {
		this.selectParentDependencies = selectParentDependencies;
	}

	public Object getSelectTimes() {
		return selectTimes;
	}

	public void setSelectTimes(Object selectTimes) {
		this.selectTimes = selectTimes;
	}

	public Object getSelectAlarms() {
		return selectAlarms;
	}

	public void setSelectAlarms(Object selectAlarms) {
		this.selectAlarms = selectAlarms;
	}

	public Object getSelectTrigger() {
		return selectTrigger;
	}

	public void setSelectTrigger(Object selectTrigger) {
		this.selectTrigger = selectTrigger;
	}

}
