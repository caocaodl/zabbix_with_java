package com.isoft.iradar.web.action.moncenter;

public class HuaweiSwitchLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return HUAWEI_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return HUAWEI_LATEST_DATA_ACTION;
	}
	
}
