package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.web.action.RadarBaseAction;

//TODO 尚未实现完
public class AuthenticationAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of authentication"));
		page("file", "authentication.action");
		page("hist_arg", new String[] { "config" });
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
