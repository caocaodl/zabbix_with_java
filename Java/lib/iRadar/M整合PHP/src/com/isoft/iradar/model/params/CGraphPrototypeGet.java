package com.isoft.iradar.model.params;

public class CGraphPrototypeGet extends CGraphGeneralGet {
	
	private static final long serialVersionUID = 1L;
	
	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Long[] graphIds;
	private Long[] itemIds;
	private Long[] discoveryIds;
	private Boolean templated;
	private Boolean inherited;
	private Object selectItems;
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

	public Long[] getDiscoveryIds() {
		return discoveryIds;
	}

	public void setDiscoveryIds(Long... discoveryIds) {
		this.discoveryIds = discoveryIds;
	}

	public Boolean getTemplated() {
		return templated;
	}

	public void setTemplated(Boolean templated) {
		this.templated = templated;
	}

	public Boolean getInherited() {
		return inherited;
	}

	public void setInherited(Boolean inherited) {
		this.inherited = inherited;
	}

	public Object getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(Object selectItems) {
		this.selectItems = selectItems;
	}

	public Object getSelectDiscoveryRule() {
		return selectDiscoveryRule;
	}

	public void setSelectDiscoveryRule(Object selectDiscoveryRule) {
		this.selectDiscoveryRule = selectDiscoveryRule;
	}

}