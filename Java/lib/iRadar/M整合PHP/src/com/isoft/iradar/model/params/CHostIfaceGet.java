package com.isoft.iradar.model.params;

public class CHostIfaceGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] interfaceIds;
	private Long[] itemIds;
	private Long[] triggerIds;
	private Object selectHosts;
	private Object selectItems;

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
