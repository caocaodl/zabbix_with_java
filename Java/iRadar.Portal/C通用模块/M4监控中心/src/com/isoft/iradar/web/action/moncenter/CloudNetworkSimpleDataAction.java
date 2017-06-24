package com.isoft.iradar.web.action.moncenter;

public class CloudNetworkSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return CLOUDNETWORK_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLOUDNETWORK_LATEST_DATA_ACTION;
	}

}
