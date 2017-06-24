package com.isoft.iradar.web.action.monitoring;

import com.isoft.iradar.web.action.core.EventsAction;


/**
 * 历史故障事件Action
 * @author Administrator
 *
 */
public class HistoryFaultAction extends AbstractEventAction {

	@Override
	protected String getPageFile() {
		return null;
	}
	
	@Override
	protected String getPageName() {
		return "历史故障";
	}

	@Override
	protected boolean isShowWarnings() {
		return false;
	}

	@Override
	protected boolean isShowActive() {
		return false;
	}

}
