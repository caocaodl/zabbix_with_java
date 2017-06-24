package com.isoft.iradar.web.action.moncenter;

public class OracleSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return ORACLE_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return ORACLE_LATEST_DATA_ACTION;
	}

}
