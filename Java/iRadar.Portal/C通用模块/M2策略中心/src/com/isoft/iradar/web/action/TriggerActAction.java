package com.isoft.iradar.web.action;

import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.web.action.core.ActionconfAction;

public class TriggerActAction extends ActionconfAction {

	@Override
	protected void doInitPage() {
		super.doInitPage();
		_REQUEST("eventsource", Defines.EVENT_SOURCE_TRIGGERS);
		_REQUEST("isShowSourceComboBox", "false");
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变动作状态所需JS
		page("css", new String[] {"lessor/strategy/triggerAct.css"});
	}
}
