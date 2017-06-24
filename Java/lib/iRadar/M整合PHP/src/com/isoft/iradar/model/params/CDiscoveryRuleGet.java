package com.isoft.iradar.model.params;

public class CDiscoveryRuleGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Long[] itemIds;
	private Long[] interfaceIds;
	private Boolean inherited;
	private Boolean templated;
	private Boolean monitored;
	private Object selectHosts;
	private Object selectItems;
	private Object selectTriggers;
	private Object selectGraphs;
	private Object selectMediatypes;
	private Object selectHostPrototypes;

	public Object getSelectMediatypes() {
		return selectMediatypes;
	}

	public void setSelectMediatypes(Object selectMediatypes) {
		this.selectMediatypes = selectMediatypes;
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

	public Long[] getInterfaceIds() {
		return interfaceIds;
	}

	public void setInterfaceIds(Long... interfaceIds) {
		this.interfaceIds = interfaceIds;
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

	public Object getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(Object selectItems) {
		this.selectItems = selectItems;
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

	public Object getSelectHostPrototypes() {
		return selectHostPrototypes;
	}

	public void setSelectHostPrototypes(Object selectHostPrototypes) {
		this.selectHostPrototypes = selectHostPrototypes;
	}
}