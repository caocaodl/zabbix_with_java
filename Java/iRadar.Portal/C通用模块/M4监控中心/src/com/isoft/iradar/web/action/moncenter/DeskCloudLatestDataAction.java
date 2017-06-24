package com.isoft.iradar.web.action.moncenter;

public class DeskCloudLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return DESKCLOUD_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return DESKCLOUD_LATEST_DATA_ACTION;
	}
	
}
