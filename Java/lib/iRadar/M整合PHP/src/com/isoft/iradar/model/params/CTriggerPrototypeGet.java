package com.isoft.iradar.model.params;

public class CTriggerPrototypeGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Long[] triggerIds;
	private Long[] itemIds;
	private Long[] applicationIds;
	private Long[] discoveryIds;
	private String[] functions;
	private Boolean inherited;
	private Boolean templated;
	private Boolean monitored;
	private Boolean active;
	private Boolean maintenance;

	private String group;
	private String host;
	private Integer minSeverity;

	private Boolean expandExpression;
	private Boolean expandData;
	private Object selectGroups;
	private Object selectHosts;
	private Object selectItems;
	private Object selectFunctions;
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

	public Long[] getTriggerIds() {
		return triggerIds;
	}

	public void setTriggerIds(Long... triggerIds) {
		this.triggerIds = triggerIds;
	}

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public Long[] getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(Long... applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Long[] getDiscoveryIds() {
		return discoveryIds;
	}

	public void setDiscoveryIds(Long... discoveryIds) {
		this.discoveryIds = discoveryIds;
	}

	public String[] getFunctions() {
		return functions;
	}

	public void setFunctions(String... functions) {
		this.functions = functions;
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(Boolean maintenance) {
		this.maintenance = maintenance;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getMinSeverity() {
		return minSeverity;
	}

	public void setMinSeverity(Integer minSeverity) {
		this.minSeverity = minSeverity;
	}

	public Boolean getExpandExpression() {
		return expandExpression;
	}

	public void setExpandExpression(Boolean expandExpression) {
		this.expandExpression = expandExpression;
	}

	public Boolean getExpandData() {
		return expandData;
	}

	public void setExpandData(Boolean expandData) {
		this.expandData = expandData;
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

	public Object getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(Object selectItems) {
		this.selectItems = selectItems;
	}

	public Object getSelectFunctions() {
		return selectFunctions;
	}

	public void setSelectFunctions(Object selectFunctions) {
		this.selectFunctions = selectFunctions;
	}

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

}
