package com.isoft.iradar.model.params;

public class CItemGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Long[] proxyIds;
	private Long[] itemIds;
	private Long[] interfaceIds;
	private Long[] graphIds;
	private Long[] triggerIds;
	private Long[] applicationIds;
	private Boolean webItems;
	private Boolean inherited;
	private Boolean templated;
	private Boolean monitored;
	private String group;
	private String host;
	private String application;
	private Boolean withTriggers;
	private Object selectHosts;
	private Object selectInterfaces;
	private Object selectTriggers;
	private Object selectGraphs;
	private Object selectApplications;
	private Object selectDiscoveryRule;
	private Object selectItemDiscovery;

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

	public Long[] getProxyIds() {
		return proxyIds;
	}

	public void setProxyIds(Long... proxyIds) {
		this.proxyIds = proxyIds;
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

	public Long[] getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(Long... applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Boolean getWebItems() {
		return webItems;
	}

	public void setWebItems(Boolean webItems) {
		this.webItems = webItems;
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

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Boolean getWithTriggers() {
		return withTriggers;
	}

	public void setWithTriggers(Boolean withTriggers) {
		this.withTriggers = withTriggers;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Object getSelectInterfaces() {
		return selectInterfaces;
	}

	public void setSelectInterfaces(Object selectInterfaces) {
		this.selectInterfaces = selectInterfaces;
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

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

	public Object getSelectItemDiscovery() {
		return selectItemDiscovery;
	}

	public void setSelectItemDiscovery(Object selectItemDiscovery) {
		this.selectItemDiscovery = selectItemDiscovery;
	}
}
