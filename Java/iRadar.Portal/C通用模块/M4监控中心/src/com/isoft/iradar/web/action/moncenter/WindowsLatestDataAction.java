package com.isoft.iradar.web.action.moncenter;

public class WindowsLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return WINDOWS_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return WINDOWS_LATEST_DATA_ACTION;
	}
	
}
