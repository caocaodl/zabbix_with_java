package com.isoft.iradar.web.action.monitoring;


public class THistoryAlarmAction extends TAbstractEventAction {

	@Override
	protected String getPageFile() {
		return TEventsAction.THISTORYALARM;
	}

	@Override
	protected String getPageName() {
		return "历史告警";
	}

	@Override
	protected boolean isShowErrors() {
		return false;
	}

	@Override
	protected boolean isShowActive() {
		return false;
	}
}
