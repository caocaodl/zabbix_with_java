package com.isoft.iradar.web.action.monitoring;

import com.isoft.iradar.web.action.monitoring.TEventsAction;

public class TActiveAlarmAction extends TAbstractEventAction {

	@Override
	protected String getPageFile() {
		return TEventsAction.TACTIVEALARM;
	}

	@Override
	protected String getPageName() {
		return "活动告警";
	}

	@Override
	protected boolean isShowErrors() {
		return false;
	}

	@Override
	protected boolean isShowHistory() {
		return false;
	}

}
