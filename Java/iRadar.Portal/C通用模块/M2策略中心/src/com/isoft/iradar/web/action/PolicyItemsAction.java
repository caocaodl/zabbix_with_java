package com.isoft.iradar.web.action;

import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.web.action.core.ItemsAction;

public class PolicyItemsAction extends ItemsAction {

	@Override
	protected String getAction() {
		return "policy_items.action";
	}

	@Override
	protected void doInitPage() {
		super.doInitPage();
		page("module", IMonModule.policy.ordinal());
	}

}
