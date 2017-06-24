package com.isoft.iradar.model.params;

public class CItemPrototypeGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Long[] itemIds;
	private Long[] discoveryIds;
	private Long[] graphIds;
	private Long[] triggerIds;

	private Boolean inherited;
	private Boolean templated;
	private Boolean monitored;

	private Object selectHosts;
	private Object selectApplications;
	private Object selectTriggers;
	private Object selectGraphs;
	private Object selectDiscoveryRule;

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

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public Long[] getDiscoveryIds() {
		return discoveryIds;
	}

	public void setDiscoveryIds(Long... discoveryIds) {
		this.discoveryIds = discoveryIds;
	}

	public Long[] getGraphIds() {
		return graphIds;
	}

	public void setGraphIds(Long... graphIds) {
		this.graphIds = graphIds;
	}

	public Long[] getTriggerIds() {
		return triggerIds;
	}

	public void setTriggerIds(Long... triggerIds) {
		this.triggerIds = triggerIds;
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

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Object getSelectApplications() {
		return selectApplications;
	}

	public void setSelectApplications(Object selectApplications) {
		this.selectApplications = selectApplications;
	}

	public Object getSelectTriggers() {
		return selectTriggers;
	}

	public void setSelectTriggers(Object selectTriggers) {
		this.selectTriggers = selectTriggers;
	}

	public Object getSelectGraphs() {
		return selectGraphs;
	}

	public void setSelectGraphs(Object selectGraphs) {
		this.selectGraphs = selectGraphs;
	}

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

}
