package com.isoft.iradar.model.params;

public class CScriptGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] scriptIds;
	private Long[] usrgrpIds;
	private Object selectGroups;
	private Object selectHosts;

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

	public Long[] getScriptIds() {
		return scriptIds;
	}

	public void setScriptIds(Long... scriptIds) {
		this.scriptIds = scriptIds;
	}

	public Long[] getUsrgrpIds() {
		return usrgrpIds;
	}

	public void setUsrgrpIds(Long... usrgrpIds) {
		this.usrgrpIds = usrgrpIds;
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
}
