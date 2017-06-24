package com.isoft.iradar.web.action.moncenter;

public class LinuxLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return LINUX_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return LINUX_LATEST_DATA_ACTION;
	}
	
}
