package com.isoft.iradar.web.action.moncenter;

public class MssqlLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return MSSQL_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return MSSQL_LATEST_DATA_ACTION;
	}
	
}
