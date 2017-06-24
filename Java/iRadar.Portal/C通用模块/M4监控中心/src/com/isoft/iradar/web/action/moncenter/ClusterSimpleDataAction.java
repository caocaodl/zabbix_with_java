package com.isoft.iradar.web.action.moncenter;

public class ClusterSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return CLUSTER_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return CLUSTER_LATEST_DATA_ACTION;
	}

}
