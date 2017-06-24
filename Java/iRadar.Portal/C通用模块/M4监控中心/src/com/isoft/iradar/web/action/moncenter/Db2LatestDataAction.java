package com.isoft.iradar.web.action.moncenter;

public class Db2LatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return DB2_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return DB2_LATEST_DATA_ACTION;
	}
	
}
