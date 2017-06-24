package com.isoft.iradar.web.action.moncenter;

public class NetdevSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return NETDEV_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return NETDEV_LATEST_DATA_ACTION;
	}

}
