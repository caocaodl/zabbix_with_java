package com.isoft.iradar.model.params;

public class CScreenGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;
	
	private Long[] screenIds;
	private Long[] screenItemIds;
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

	public Object getSelectScreenItems() {
		return selectScreenItems;
	}

	public void setSelectScreenItems(Object selectScreenItems) {
		this.selectScreenItems = selectScreenItems;
	}

}