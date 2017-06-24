package com.isoft.iradar.web.action.moncenter;

public class OracleLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return ORACLE_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return ORACLE_LATEST_DATA_ACTION;
	}
	
}
