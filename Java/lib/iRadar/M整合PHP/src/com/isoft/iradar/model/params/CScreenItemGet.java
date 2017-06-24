package com.isoft.iradar.model.params;

public class CScreenItemGet extends CParamGet {

	private static final long serialVersionUID = 0L;

	private Long[] screenitemIds;
	private Long[] screenIds;

	public Long[] getScreenitemIds() {
		return screenitemIds;
	}

	public void setScreenitemIds(Long... screenitemIds) {
		this.screenitemIds = screenitemIds;
	}

	public Long[] getScreenIds() {
		return screenIds;
	}

	public void setScreenIds(Long... screenIds) {
		this.screenIds = screenIds;
	}

}
