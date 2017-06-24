package com.isoft.iradar.model.params;

public class CHttpTestGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] httptestIds;
	private Long[] applicationIds;
	private Long[] hostIds;
	private Long[] groupIds;
	private Long[] templateIds;
	private Boolean inherited;
	private Boolean templated;
	private Boolean monitored;
	private Object expandName;
	private Object expandStepName;
	private Object selectHosts;
	private Object selectSteps;

	public Long[] getHttptestIds() {
		return httptestIds;
	}

	public void setHttptestIds(Long... httptestIds) {
		this.httptestIds = httptestIds;
	}

	public Long[] getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(Long... applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long... groupIds) {
		this.groupIds = groupIds;
	}

	public Long[] getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(Long... templateIds) {
		this.templateIds = templateIds;
	}

	public Boolean getInherited() {
		return inherited;
	}

	public void setInherited(Boolean inherited) {
		this.inherited = inherited;
	}

	public Boolean getTemplated() {
		return templated;
	}

	public void setTemplated(Boolean templated) {
		this.templated = templated;
	}

	public Boolean getMonitored() {
		return monitored;
	}

	public void setMonitored(Boolean monitored) {
		this.monitored = monitored;
	}

	public Object getExpandName() {
		return expandName;
	}

	public void setExpandName(Object expandName) {
		this.expandName = expandName;
	}

	public Object getExpandStepName() {
		return expandStepName;
	}

	public void setExpandStepName(Object expandStepName) {
		this.expandStepName = expandStepName;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Object getSelectSteps() {
		return selectSteps;
	}

	public void setSelectSteps(Object selectSteps) {
		this.selectSteps = selectSteps;
	}

}
