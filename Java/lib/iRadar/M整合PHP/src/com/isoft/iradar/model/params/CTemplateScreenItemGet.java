package com.isoft.iradar.model.params;

public class CTemplateScreenItemGet extends CParamGet {

	private static final long serialVersionUID = 0;

	private Long[] screenItemIds;
	private Long[] screenIds;
	private Long[] hostIds;

	public Long[] getScreenItemIds() {
		return screenItemIds;
	}

	public void setScreenItemIds(Long... screenItemIds) {
		this.screenItemIds = screenItemIds;
	}

	public Long[] getScreenIds() {
		return screenIds;
	}

	public void setScreenIds(Long... screenIds) {
		this.screenIds = screenIds;
	}

	public Long[] getHostIds() {
		return hostIds;
	}

	public void setHostIds(Long... hostIds) {
		this.hostIds = hostIds;
	}

}
