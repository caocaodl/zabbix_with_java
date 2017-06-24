package com.isoft.iradar.web.action.moncenter;

public class WebLogicSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return WEBLOGIC_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return WEBLOGIC_LATEST_DATA_ACTION;
	}

}
