package com.isoft.iradar.web.action.moncenter;

public class MysqlLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return MYSQL_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return MYSQL_LATEST_DATA_ACTION;
	}
	
}
