package com.isoft.iradar.model.params;

public class CHostGroupGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] templateIds;
	private Long[] graphIds;
	private Long[] triggerIds;
	private Long[] maintenanceIds;
	private Boolean onlyHostGroup;
	private Boolean monitoredHosts;
	private Boolean templatedHosts;
	private Boolean realHosts;
	private Boolean notProxyHosts;
	private Boolean withHostsAndTemplates;
	private Boolean withItems;
	private Boolean withSimpleGraphItems;
	private Boolean withMonitoredItems;
	private Boolean withHistoricalItems;
	private Boolean withTriggers;
	private Boolean withMonitoredTriggers;
	private Boolean withHttpTests;
	private Boolean withMonitoredHttpTests;
	private Boolean withGraphs;
	private Boolean withApplications;
	private Object selectHosts;
	private Object selectTemplates;
	private Object selectDiscoveryRule;
	private Object selectGroupDiscovery;

	public Object getSelectGroupDiscovery() {
		return selectGroupDiscovery;
	}

	public void setSelectGroupDiscovery(Object selectGroupDiscovery) {
		this.selectGroupDiscovery = selectGroupDiscovery;
	}

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long... groupIds) {
		this.groupIds = groupIds;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long[] getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(Long... templateIds) {
		this.templateIds = templateIds;
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

	public Long[] getMaintenanceIds() {
		return maintenanceIds;
	}

	public void setMaintenanceIds(Long... maintenanceIds) {
		this.maintenanceIds = maintenanceIds;
	}

	public Boolean getOnlyHostGroup() {
		return onlyHostGroup;
	}

	public void setOnlyHostGroup(Boolean onlyHostGroup) {
		this.onlyHostGroup = onlyHostGroup;
	}

	public Boolean getMonitoredHosts() {
		return monitoredHosts;
	}

	public void setMonitoredHosts(Boolean monitoredHosts) {
		this.monitoredHosts = monitoredHosts;
	}

	public Boolean getTemplatedHosts() {
		return templatedHosts;
	}

	public void setTemplatedHosts(Boolean templatedHosts) {
		this.templatedHosts = templatedHosts;
	}

	public Boolean getRealHosts() {
		return realHosts;
	}

	public void setRealHosts(Boolean realHosts) {
		this.realHosts = realHosts;
	}

	public Boolean getNotProxyHosts() {
		return notProxyHosts;
	}

	public void setNotProxyHosts(Boolean notProxyHosts) {
		this.notProxyHosts = notProxyHosts;
	}

	public Boolean getWithHostsAndTemplates() {
		return withHostsAndTemplates;
	}

	public void setWithHostsAndTemplates(Boolean withHostsAndTemplates) {
		this.withHostsAndTemplates = withHostsAndTemplates;
	}

	public Boolean getWithItems() {
		return withItems;
	}

	public void setWithItems(Boolean withItems) {
		this.withItems = withItems;
	}

	public Boolean getWithSimpleGraphItems() {
		return withSimpleGraphItems;
	}

	public void setWithSimpleGraphItems(Boolean withSimpleGraphItems) {
		this.withSimpleGraphItems = withSimpleGraphItems;
	}

	public Boolean getWithMonitoredItems() {
		return withMonitoredItems;
	}

	public void setWithMonitoredItems(Boolean withMonitoredItems) {
		this.withMonitoredItems = withMonitoredItems;
	}

	public Boolean getWithHistoricalItems() {
		return withHistoricalItems;
	}

	public void setWithHistoricalItems(Boolean withHistoricalItems) {
		this.withHistoricalItems = withHistoricalItems;
	}

	public Boolean getWithTriggers() {
		return withTriggers;
	}

	public void setWithTriggers(Boolean withTriggers) {
		this.withTriggers = withTriggers;
	}

	public Boolean getWithMonitoredTriggers() {
		return withMonitoredTriggers;
	}

	public void setWithMonitoredTriggers(Boolean withMonitoredTriggers) {
		this.withMonitoredTriggers = withMonitoredTriggers;
	}

	public Boolean getWithHttpTests() {
		return withHttpTests;
	}

	public void setWithHttpTests(Boolean withHttpTests) {
		this.withHttpTests = withHttpTests;
	}

	public Boolean getWithMonitoredHttpTests() {
		return withMonitoredHttpTests;
	}

	public void setWithMonitoredHttpTests(Boolean withMonitoredHttpTests) {
		this.withMonitoredHttpTests = withMonitoredHttpTests;
	}

	public Boolean getWithGraphs() {
		return withGraphs;
	}

	public void setWithGraphs(Boolean withGraphs) {
		this.withGraphs = withGraphs;
	}

	public Boolean getWithApplications() {
		return withApplications;
	}

	public void setWithApplications(Boolean withApplications) {
		this.withApplications = withApplications;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Object getSelectTemplates() {
		return selectTemplates;
	}

	public void setSelectTemplates(Object selectTemplates) {
		this.selectTemplates = selectTemplates;
	}

}
