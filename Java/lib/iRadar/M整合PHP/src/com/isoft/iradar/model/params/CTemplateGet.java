package com.isoft.iradar.model.params;

public class CTemplateGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] parentTemplateIds;
	private Long[] hostIds;
	private Long[] graphIds;
	private Long[] itemIds;
	private Long[] triggerIds;
	private Boolean withItems;
	private Boolean withTriggers;
	private Boolean withGraphs;
	private Boolean withHttpTests;
	private Object selectGroups;
	private Object selectHosts;
	private Object selectTemplates;
	private Object selectParentTemplates;
	private Object selectItems;
	private Object selectDiscoveries;
	private Object selectTriggers;
	private Object selectGraphs;
	private Object selectApplications;
	private Object selectMacros;
	private Object selectScreens;
	private Object selectHttpTests;

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

	public Long[] getParentTemplateIds() {
		return parentTemplateIds;
	}

	public void setParentTemplateIds(Long... parentTemplateIds) {
		this.parentTemplateIds = parentTemplateIds;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long[] getGraphIds() {
		return graphIds;
	}

	public void setGraphIds(Long... graphIds) {
		this.graphIds = graphIds;
	}

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public Long[] getTriggerIds() {
		return triggerIds;
	}

	public void setTriggerIds(Long... triggerIds) {
		this.triggerIds = triggerIds;
	}

	public Boolean getWithItems() {
		return withItems;
	}

	public void setWithItems(Boolean withItems) {
		this.withItems = withItems;
	}

	public Boolean getWithTriggers() {
		return withTriggers;
	}

	public void setWithTriggers(Boolean withTriggers) {
		this.withTriggers = withTriggers;
	}

	public Boolean getWithGraphs() {
		return withGraphs;
	}

	public void setWithGraphs(Boolean withGraphs) {
		this.withGraphs = withGraphs;
	}

	public Boolean getWithHttpTests() {
		return withHttpTests;
	}

	public void setWithHttpTests(Boolean withHttpTests) {
		this.withHttpTests = withHttpTests;
	}

	public Object getSelectGroups() {
		return selectGroups;
	}

	public void setSelectGroups(Object selectGroups) {
		this.selectGroups = selectGroups;
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

	public Object getSelectParentTemplates() {
		return selectParentTemplates;
	}

	public void setSelectParentTemplates(Object selectParentTemplates) {
		this.selectParentTemplates = selectParentTemplates;
	}

	public Object getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(Object selectItems) {
		this.selectItems = selectItems;
	}

	public Object getSelectDiscoveries() {
		return selectDiscoveries;
	}

	public void setSelectDiscoveries(Object selectDiscoveries) {
		this.selectDiscoveries = selectDiscoveries;
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

	public Object getSelectApplications() {
		return selectApplications;
	}

	public void setSelectApplications(Object selectApplications) {
		this.selectApplications = selectApplications;
	}

	public Object getSelectMacros() {
		return selectMacros;
	}

	public void setSelectMacros(Object selectMacros) {
		this.selectMacros = selectMacros;
	}

	public Object getSelectScreens() {
		return selectScreens;
	}

	public void setSelectScreens(Object selectScreens) {
		this.selectScreens = selectScreens;
	}

	public Object getSelectHttpTests() {
		return selectHttpTests;
	}

	public void setSelectHttpTests(Object selectHttpTests) {
		this.selectHttpTests = selectHttpTests;
	}

}
