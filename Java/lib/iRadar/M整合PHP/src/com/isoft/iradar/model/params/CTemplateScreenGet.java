package com.isoft.iradar.model.params;

public class CTemplateScreenGet extends CScreenGet {

	private static final long serialVersionUID = 0L;

	private Long[] screenIds;
	private Long[] screenItemIds;
	private Long[] templateIds;
	private Long[] hostIds;
	private Boolean noInheritance;
	private Object selectScreenItems;

	public Long[] getScreenIds() {
		return screenIds;
	}

	public void setScreenIds(Long... screenIds) {
		this.screenIds = screenIds;
	}

	public Long[] getScreenItemIds() {
		return screenItemIds;
	}

	public void setScreenItemIds(Long... screenItemIds) {
		this.screenItemIds = screenItemIds;
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

	public Boolean getNoInheritance() {
		return noInheritance;
	}

	public void setNoInheritance(Boolean noInheritance) {
		this.noInheritance = noInheritance;
	}

	public Object getSelectScreenItems() {
		return selectScreenItems;
	}

	public void setSelectScreenItems(Object selectScreenItems) {
		this.selectScreenItems = selectScreenItems;
	}

}
