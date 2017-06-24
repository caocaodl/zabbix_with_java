package com.isoft.iradar.web.action.moncenter;

public class WebLogicLatestDataAction extends A_LatestDataAction {
	
	@Override
	public String getSimpleAction() {
		return WEBLOGIC_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return WEBLOGIC_LATEST_DATA_ACTION;
	}
	
}
