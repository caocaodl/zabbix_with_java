package com.isoft.iradar.web.action.moncenter;

public class StorageLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return STORAGE_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return STORAGE_LATEST_DATA_ACTION;
	}
	
}
