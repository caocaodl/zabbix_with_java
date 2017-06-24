package com.isoft.iradar.web.action.monitoring;

import com.isoft.iradar.web.action.core.EventsAction;


/**
 * 历史告警事件Action
 * @author Administrator
 *
 */
public class HistoryAlarmAction extends AbstractEventAction {

	@Override
	protected String getPageFile() {
		return null;
	}

	@Override
	protected String getPageName() {
		return "历史告警";
	}

	@Override
	protected boolean isShowErrors() {
		return true;
	}

	@Override
	protected boolean isShowActive() {
		return false;
	}
}
