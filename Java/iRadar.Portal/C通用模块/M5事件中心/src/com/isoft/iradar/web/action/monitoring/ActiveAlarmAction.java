package com.isoft.iradar.web.action.monitoring;

import com.isoft.iradar.web.action.core.EventsAction;


/**
 * 活动告警事件Action
 * @author Administrator
 *
 */
public class ActiveAlarmAction extends AbstractEventAction {

	@Override
	protected String getPageFile() {
		return EventsAction.ACTIVEALARM;
	}

	@Override
	protected String getPageName() {
		return "活动告警";
	}

	@Override
	protected boolean isShowErrors() {
		return true;
	}

	@Override
	protected boolean isShowHistory() {
		return false;
	}
}
