package com.isoft.iradar.web.action.monitoring;

import com.isoft.iradar.web.action.core.EventsAction;


/**
 * 活动故障事件Action
 * @author Administrator
 *
 */
public class ActiveFaultAction extends AbstractEventAction {

	@Override
	protected String getPageName() {
		return "活动故障";
	}

	@Override
	protected boolean isShowWarnings() {
		return true;
	}

	@Override
	protected boolean isShowHistory() {
		return true;
	}
}
