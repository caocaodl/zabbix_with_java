package com.isoft.iradar.web.action.monitoring;

import com.isoft.iradar.web.action.core.EventsAction;

public abstract class AbstractEventAction extends EventsAction {

	@Override
	protected String getPageTitle() {
		return "事件中心";
	}

	@Override
	protected boolean isNeedDiscovery() {
		return false;
	}
	
	
}
