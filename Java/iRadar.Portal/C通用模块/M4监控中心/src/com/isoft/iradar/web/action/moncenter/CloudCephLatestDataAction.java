package com.isoft.iradar.web.action.moncenter;

public class CloudCephLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return CLOUDCEPH_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLOUDCEPH_LATEST_DATA_ACTION;
	}
	
}
