package com.isoft.iradar.web.action;

import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.web.action.core.ApplicationsAction;

public class MonitorAppsAction extends ApplicationsAction {

	@Override
	protected String getAction() {
		return "monitor_apps.action";
	}
	
	@Override
	protected void doInitPage() {
		super.doInitPage();
		page("module", IMonModule.monitor.ordinal());
	}

}
