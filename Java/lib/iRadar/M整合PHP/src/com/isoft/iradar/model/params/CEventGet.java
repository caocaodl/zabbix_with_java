package com.isoft.iradar.model.params;

import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;

public class CEventGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] actionIds;
	private Long[] triggerIds;
	private Long[] objectIds;
	private Long[] eventIds;
	private Integer object = EVENT_OBJECT_TRIGGER;
	private Integer source = EVENT_SOURCE_TRIGGERS;
	private Boolean acknowledged;
	private String[] value;
	private Object eventIdFrom;
	private Object eventIdTill;
	private Object selectHosts;
	private Object selectItems;
	private Object selectTriggers;
	private Object selectRelatedObject;
	private Object selectAlerts;
	private Object selectAcknowledges;

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

	public Object getSelectRelatedObject() {
		return selectRelatedObject;
	}

	public void setSelectRelatedObject(Object selectRelatedObject) {
		this.selectRelatedObject = selectRelatedObject;
	}

	public Object getSelectAlerts() {
		return selectAlerts;
	}

	public void setSelectAlerts(Object selectAlerts) {
		this.selectAlerts = selectAlerts;
	}

	public Object getSelectAcknowledges() {
		return selectAcknowledges;
	}

	public void setSelectAcknowledges(Object selectAcknowledges) {
		this.selectAcknowledges = selectAcknowledges;
	}

	public Object getSelectHosts() {
		return selectHosts;
	}

	public void setSelectHosts(Object selectHosts) {
		this.selectHosts = selectHosts;
	}

	public Long[] getEventIds() {
		return eventIds;
	}

	public void setEventIds(Long... eventids) {
		this.eventIds = eventids;
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

	public Long[] getActionIds() {
		return actionIds;
	}

	public void setActionIds(Long... actionIds) {
		this.actionIds = actionIds;
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

	public void setObjectIds(Long... objectids) {
		this.objectIds = objectids;
	}

	public Integer getObject() {
		return object;
	}

	public void setObject(Integer object) {
		this.object = object;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Boolean getAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public String[] getValue() {
		return value;
	}

	public void setValue(String... value) {
		this.value = value;
	}

	public Object getEventIdFrom() {
		return eventIdFrom;
	}

	public void setEventIdFrom(Object eventIdFrom) {
		this.eventIdFrom = eventIdFrom;
	}

	public Object getEventIdTill() {
		return eventIdTill;
	}

	public void setEventIdTill(Object eventIdTill) {
		this.eventIdTill = eventIdTill;
	}
}
