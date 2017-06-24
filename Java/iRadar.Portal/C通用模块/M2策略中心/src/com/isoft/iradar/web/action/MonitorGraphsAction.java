package com.isoft.iradar.web.action;

import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.web.action.core.GraphsAction;

public class MonitorGraphsAction extends GraphsAction {

	@Override
	protected String getAction() {
		return "monitor_graphs.action";
	}
	
	@Override
	protected void doInitPage() {
		super.doInitPage();
		page("module", IMonModule.monitor.ordinal());
	}

}
