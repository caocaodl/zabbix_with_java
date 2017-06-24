package com.isoft.iradar.model.params;

public class CUserMacroGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	private Long[] hostIds;
	private Long[] hostMacroIds;
	private Long[] globalMacroIds;
	private Long[] templateIds;
	private Long[] triggerIds;
	private Long[] itemIds;
	private Boolean globalMacro;
	private Object selectGroups;
	private Object selectHosts;
	private Object selectTemplates;

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

	public Long[] getHostMacroIds() {
		return hostMacroIds;
	}

	public void setHostMacroIds(Long... hostMacroIds) {
		this.hostMacroIds = hostMacroIds;
	}

	public Long[] getGlobalMacroIds() {
		return globalMacroIds;
	}

	public void setGlobalMacroIds(Long... globalMacroIds) {
		this.globalMacroIds = globalMacroIds;
	}

	public Long[] getTemplateIds() {
		return templateIds;
	}

	public void setTemplateIds(Long... templateIds) {
		this.templateIds = templateIds;
	}

	public Long[] getTriggerIds() {
		return triggerIds;
	}

	public void setTriggerIds(Long... triggerIds) {
		this.triggerIds = triggerIds;
	}

	public Long[] getItemIds() {
		return itemIds;
	}

	public void setItemIds(Long... itemIds) {
		this.itemIds = itemIds;
	}

	public Boolean getGlobalMacro() {
		return globalMacro;
	}

	public void setGlobalMacro(Boolean globalMacro) {
		this.globalMacro = globalMacro;
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

	public Object getSelectTemplates() {
		return selectTemplates;
	}

	public void setSelectTemplates(Object selectTemplates) {
		this.selectTemplates = selectTemplates;
	}
}
