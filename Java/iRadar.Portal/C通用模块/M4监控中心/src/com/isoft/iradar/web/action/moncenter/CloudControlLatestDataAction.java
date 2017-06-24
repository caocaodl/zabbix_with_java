package com.isoft.iradar.web.action.moncenter;

public class CloudControlLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return CLOUDCONTROL_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLOUDCONTROL_LATEST_DATA_ACTION;
	}
	
}
