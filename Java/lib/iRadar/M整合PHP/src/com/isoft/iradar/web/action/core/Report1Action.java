package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.BlocksUtil.make_status_of_rda;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;

public class Report1Action extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Status of iRadar"));
		page("file", "report1.action");
		page("hist_arg", new String[] {});
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
		CWidget reportWidget = new CWidget();
		reportWidget.addPageHeader(_("STATUS OF IRADAR"));
		reportWidget.addItem(make_status_of_rda(getIdentityBean(), executor));
		reportWidget.show();
	}

}
