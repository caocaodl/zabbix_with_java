package com.isoft.iradar.web.action.moncenter;

public class CloudComputeLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return CLOUDCOMPUTE_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLOUDCOMPUTE_LATEST_DATA_ACTION;
	}
	
}
