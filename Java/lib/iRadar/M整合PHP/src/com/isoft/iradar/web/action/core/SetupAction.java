package com.isoft.iradar.web.action.core;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.web.action.RadarBaseAction;

public class SetupAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
	}

}
