package com.isoft.iradar.web.action.moncenter;

public class DmSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return DM_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return DM_LATEST_DATA_ACTION;
	}

}
