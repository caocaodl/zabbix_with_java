package com.isoft.iradar.model.params;

public class CAppGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Long[] itemIds;
	private Long[] applicationIds;
	private Boolean expandData;
	private Boolean templated;
	private Boolean inherited;
	private Object selectHosts;
	private Object selectItems;

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

	public Long[] getApplicationIds() {
		return applicationIds;
	}

	public void setApplicationIds(Long... applicationIds) {
		this.applicationIds = applicationIds;
	}

	public Boolean getExpandData() {
		return expandData;
	}

	public void setExpandData(Boolean expandData) {
		this.expandData = expandData;
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

}