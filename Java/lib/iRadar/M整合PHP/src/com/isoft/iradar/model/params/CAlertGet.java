package com.isoft.iradar.model.params;

import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;

public class CAlertGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;
	
	private int eventSource = EVENT_SOURCE_TRIGGERS;
	private int eventObject = EVENT_SOURCE_TRIGGERS;
	
	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] alertIds;
	private Long[] triggerIds;
	private Long[] objectIds;
	private Long[] eventIds;
	private Long[] actionIds;
	private Long[] mediaTypeIds;
	private String[] userIds;
	private Object selectMediatypes;
	private Object selectUsers;
	private Object selectHosts;

	public int getEventSource() {
		return eventSource;
	}

	public void setEventSource(int eventSource) {
		this.eventSource = eventSource;
	}

	public int getEventObject() {
		return eventObject;
	}

	public void setEventObject(int eventObject) {
		this.eventObject = eventObject;
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

	public Long[] getAlertIds() {
		return alertIds;
	}

	public void setAlertIds(Long... alertIds) {
		this.alertIds = alertIds;
	}

	public Long[] getTriggerIds() {
		return triggerIds;
	}

	public void setTriggerIds(Long... triggerIds) {
		this.triggerIds = triggerIds;
	}

	public Long[] getObjectIds() {
		return objectIds;
	}

	public void setObjectIds(Long... objectIds) {
		this.objectIds = objectIds;
	}

	public Long[] getEventIds() {
		return eventIds;
	}

	public void setEventIds(Long... eventIds) {
		this.eventIds = eventIds;
	}

	public Long[] getActionIds() {
		return actionIds;
	}

	public void setActionIds(Long... actionIds) {
		this.actionIds = actionIds;
	}

	public Long[] getMediaTypeIds() {
		return mediaTypeIds;
	}

	public void setMediaTypeIds(Long... mediaTypeIds) {
		this.mediaTypeIds = mediaTypeIds;
	}

	public String[] getUserIds() {
		return userIds;
	}

	public void setUserIds(String... userIds) {
		this.userIds = userIds;
	}

	public Object getSelectMediatypes() {
		return selectMediatypes;
	}

	public void setSelectMediatypes(Object selectMediatypes) {
		this.selectMediatypes = selectMediatypes;
	}

	public Object getSelectUsers() {
		return selectUsers;
	}

	public void setSelectUsers(Object selectUsers) {
		this.selectUsers = selectUsers;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

}
