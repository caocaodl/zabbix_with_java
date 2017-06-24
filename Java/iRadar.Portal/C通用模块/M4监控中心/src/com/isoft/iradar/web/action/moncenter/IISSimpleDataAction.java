package com.isoft.iradar.web.action.moncenter;

public class IISSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return IIS_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return IIS_LATEST_DATA_ACTION;
	}

}
