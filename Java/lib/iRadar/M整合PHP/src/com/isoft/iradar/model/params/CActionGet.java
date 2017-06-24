package com.isoft.iradar.model.params;

public class CActionGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] actionIds;
	private Long[] triggerIds;
	private Long[] mediaTypeIds;
	private Long[] usrgrpIds;
	private Long[] userIds;
	private Long[] scriptIds;
	private Object selectConditions;
	private Object selectOperations;

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

	public Long[] getMediaTypeIds() {
		return mediaTypeIds;
	}

	public void setMediaTypeIds(Long... mediaTypeIds) {
		this.mediaTypeIds = mediaTypeIds;
	}

	public Long[] getUsrgrpIds() {
		return usrgrpIds;
	}

	public void setUsrgrpIds(Long... usrgrpIds) {
		this.usrgrpIds = usrgrpIds;
	}

	public Long[] getUserIds() {
		return userIds;
	}

	public void setUserIds(Long... userIds) {
		this.userIds = userIds;
	}

	public Long[] getScriptIds() {
		return scriptIds;
	}

	public void setScriptIds(Long... scriptIds) {
		this.scriptIds = scriptIds;
	}

	public Object getSelectConditions() {
		return selectConditions;
	}

	public void setSelectConditions(Object selectConditions) {
		this.selectConditions = selectConditions;
	}

	public Object getSelectOperations() {
		return selectOperations;
	}

	public void setSelectOperations(Object selectOperations) {
		this.selectOperations = selectOperations;
	}

}
