package com.isoft.iradar.web.action;

import com.isoft.iradar.web.action.core.HttpmonAction;

public class WebServiceHttpmonAction extends HttpmonAction {

	@Override
	protected String getAction() {
		return "mon_web.action";
	}

	protected boolean showGroupFilter() {
		return false;
	}

	@Override
	protected Object getHeader() {
		return "Web服务监察";
	}
}
