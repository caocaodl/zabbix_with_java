package com.isoft.iradar.web.action.moncenter;

public class MysqlSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return MYSQL_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return MYSQL_LATEST_DATA_ACTION;
	}

}
