package com.isoft.iradar.web.action.moncenter;

public class CiscoLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return CISCO_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CISCO_LATEST_DATA_ACTION;
	}
	
}
