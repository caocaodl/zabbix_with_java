package com.isoft.iradar.model.params;

public class CHostPrototypeGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] discoveryIds;
	private Long[] hostIds;
	private Boolean inherited;
	private Object selectDiscoveryRule;
	private Object selectGroupLinks;
	private Object selectGroupPrototypes;
	private Object selectParentHost;
	private Object selectTemplates;
	private Object selectInventory;
	private Object groupOutput;

	public Long[] getDiscoveryIds() {
		return discoveryIds;
	}

	public void setDiscoveryIds(Long... discoveryIds) {
		this.discoveryIds = discoveryIds;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Boolean getInherited() {
		return inherited;
	}

	public void setInherited(Boolean inherited) {
		this.inherited = inherited;
	}

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

	public Object getSelectGroupLinks() {
		return selectGroupLinks;
	}

	public void setSelectGroupLinks(Object selectGroupLinks) {
		this.selectGroupLinks = selectGroupLinks;
	}

	public Object getSelectGroupPrototypes() {
		return selectGroupPrototypes;
	}

	public void setSelectGroupPrototypes(Object selectGroupPrototypes) {
		this.selectGroupPrototypes = selectGroupPrototypes;
	}

	public Object getSelectParentHost() {
		return selectParentHost;
	}

	public void setSelectParentHost(Object selectParentHost) {
		this.selectParentHost = selectParentHost;
	}

	public Object getSelectTemplates() {
		return selectTemplates;
	}

	public void setSelectTemplates(Object selectTemplates) {
		this.selectTemplates = selectTemplates;
	}

	public Object getSelectInventory() {
		return selectInventory;
	}

	public void setSelectInventory(Object selectInventory) {
		this.selectInventory = selectInventory;
	}

	public Object getGroupOutput() {
		return groupOutput;
	}

	public void setGroupOutput(Object groupOutput) {
		this.groupOutput = groupOutput;
	}
}
