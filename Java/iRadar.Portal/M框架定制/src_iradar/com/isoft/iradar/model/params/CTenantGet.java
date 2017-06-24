package com.isoft.iradar.model.params;

public class CTenantGet extends CParamGet {

	private static final long serialVersionUID = 1L;
	
	private String[] tenantIds;
	private Long[] hostIds;
	private Long[] proxyIds;
	private Boolean monitoredTenants;
	private Boolean withoutLessor;
	private Object selectHosts;
	private Boolean withVms;
	
	public String[] getTenantIds() {
		return tenantIds;
	}
	public void setTenantIds(String[] tenantIds) {
		this.tenantIds = tenantIds;
	}
	
	public Long[] getProxyIds() {
		return proxyIds;
	}

	public void setProxyIds(Long... proxyIds) {
		this.proxyIds = proxyIds;
	}
	
	public Boolean getMonitoredTenants() {
		return monitoredTenants;
	}

	public void setMonitoredTenants(Boolean monitoredTenants) {
		this.monitoredTenants = monitoredTenants;
	}
	
	public Boolean getWithVms() {
		return withVms;
	}

	public void setWithVms(Boolean withVms) {
		this.withVms = withVms;
	}
	public Boolean getWithoutLessor() {
		return withoutLessor;
	}

	public void setWithoutLessor(Boolean withoutLessor) {
		this.withoutLessor = withoutLessor;
	}
	
	/* -------------------------------------------------- */

	


	private Long[] groupIds;
	

	
	private Long[] templateIds;
	private Long[] interfaceIds;
	private Long[] itemIds;
	private Long[] triggerIds;
	private Long[] maintenanceIds;
	private Long[] graphIds;
	private Long[] applicationIds;
	private Long[] dhostIds;
	private Long[] dserviceIds;
	private Long[] httpTestIds;
	private String[] proxyHosts;

	
	private Boolean templatedHosts;

	private Boolean withItems;
	private Boolean withMonitoredItems;
	private Boolean withHistoricalItems;
	private Boolean withSimpleGraphItems;
	private Boolean withTriggers;
	private Boolean withMonitoredTriggers;
	private Boolean withHttpTests;
	private Boolean withMonitoredHttpTests;
	private Boolean withGraphs;
	private Boolean withApplications;
	private Boolean withInventor;

	private Integer hostType;
	
	private Object selectParentTemplates;
	private Object selectItems;
	private Object selectDiscoveries;
	private Object selectTriggers;
	private Object selectGraphs;
	private Object selectDHosts;
	private Object selectDServices;
	private Object selectApplications;
	private Object selectMacros;
	private Object selectScreens;
	private Object selectInterfaces;
	private Object selectInventory;
	private Boolean withInventory;
	private Object selectHttpTests;
	private Object selectDiscoveryRule;
	private Object selectHostDiscovery;

	public Object getSelectHttpTests() {
		return selectHttpTests;
	}

	public void setSelectHttpTests(Object selectHttpTests) {
		this.selectHttpTests = selectHttpTests;
	}

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

	public Object getSelectHostDiscovery() {
		return selectHostDiscovery;
	}

	public void setSelectHostDiscovery(Object selectHostDiscovery) {
		this.selectHostDiscovery = selectHostDiscovery;
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

	public Long[] getInterfaceIds() {
		return interfaceIds;
	}

	public void setInterfaceIds(Long... interfaceIds) {
		this.interfaceIds = interfaceIds;
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

	public Long[] getMaintenanceIds() {
		return maintenanceIds;
	}

	public void setMaintenanceIds(Long... maintenanceIds) {
		this.maintenanceIds = maintenanceIds;
	}

	public Long[] getGraphIds() {
		return graphIds;
	}

	public void setGraphIds(Long... graphIds) {
		this.graphIds = graphIds;
	}

	public Long[] getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(Long... applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Long[] getDhostIds() {
		return dhostIds;
	}

	public void setDhostIds(Long... dhostIds) {
		this.dhostIds = dhostIds;
	}

	public Long[] getDserviceIds() {
		return dserviceIds;
	}

	public void setDserviceIds(Long... dserviceIds) {
		this.dserviceIds = dserviceIds;
	}

	public Long[] getHttpTestIds() {
		return httpTestIds;
	}

	public void setHttpTestIds(Long... httpTestIds) {
		this.httpTestIds = httpTestIds;
	}

	public String[] getProxyHosts() {
		return proxyHosts;
	}

	public void setProxyHosts(String... proxyHosts) {
		this.proxyHosts = proxyHosts;
	}



	public Boolean getTemplatedHosts() {
		return templatedHosts;
	}

	public void setTemplatedHosts(Boolean templatedHosts) {
		this.templatedHosts = templatedHosts;
	}

	public Boolean getWithItems() {
		return withItems;
	}

	public void setWithItems(Boolean withItems) {
		this.withItems = withItems;
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

	public Boolean getWithSimpleGraphItems() {
		return withSimpleGraphItems;
	}

	public void setWithSimpleGraphItems(Boolean withSimpleGraphItems) {
		this.withSimpleGraphItems = withSimpleGraphItems;
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

	public Boolean getWithInventor() {
		return withInventor;
	}

	public void setWithInventor(Boolean withInventor) {
		this.withInventor = withInventor;
	}

	public Integer getHostType() {
		return hostType;
	}

	public void setHostType(Integer hostType) {
		this.hostType = hostType;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
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

	public Object getSelectDHosts() {
		return selectDHosts;
	}

	public void setSelectDHosts(Object selectDHosts) {
		this.selectDHosts = selectDHosts;
	}

	public Object getSelectDServices() {
		return selectDServices;
	}

	public void setSelectDServices(Object selectDServices) {
		this.selectDServices = selectDServices;
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

	public Object getSelectInterfaces() {
		return selectInterfaces;
	}

	public void setSelectInterfaces(Object selectInterfaces) {
		this.selectInterfaces = selectInterfaces;
	}

	public Object getSelectInventory() {
		return selectInventory;
	}

	public void setSelectInventory(Object selectInventory) {
		this.selectInventory = selectInventory;
	}

	public Boolean getWithInventory() {
		return withInventory;
	}

	public void setWithInventory(Boolean withInventory) {
		this.withInventory = withInventory;
	}

}
