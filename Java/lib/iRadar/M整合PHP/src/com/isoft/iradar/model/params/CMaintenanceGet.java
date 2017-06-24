package com.isoft.iradar.model.params;

public class CMaintenanceGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] itemIds;
	private Long[] hostIds;
	private Long[] groupIds;
	private Long[] triggerIds;
	private Long[] maintenanceIds;

	private Boolean status;
	private Object selectGroups;
	private Object selectHosts;
	private Object selectTimeperiods;

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long... groupIds) {
		this.groupIds = groupIds;
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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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

	public Object getSelectTimeperiods() {
		return selectTimeperiods;
	}

	public void setSelectTimeperiods(Object selectTimeperiods) {
		this.selectTimeperiods = selectTimeperiods;
	}
}
