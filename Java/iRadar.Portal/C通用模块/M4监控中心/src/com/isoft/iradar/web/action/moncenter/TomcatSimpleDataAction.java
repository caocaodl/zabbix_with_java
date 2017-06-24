package com.isoft.iradar.web.action.moncenter;

public class TomcatSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return TOMCAT_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return TOMCAT_LATEST_DATA_ACTION;
	}

}
