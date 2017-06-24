package com.isoft.iradar.web.action.moncenter;

public class CloudWebLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return CLOUDWEB_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLOUDWEB_LATEST_DATA_ACTION;
	}
	
}
