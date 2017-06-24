package com.isoft.iradar.web.action.moncenter;

public class CloudCephSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return CLOUDCEPH_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLOUDCEPH_LATEST_DATA_ACTION;
	}

}
