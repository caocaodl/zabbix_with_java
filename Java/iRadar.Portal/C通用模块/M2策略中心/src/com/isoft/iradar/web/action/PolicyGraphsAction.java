package com.isoft.iradar.web.action;

import com.isoft.iradar.common.util.IMonModule;
import com.isoft.iradar.web.action.core.GraphsAction;

public class PolicyGraphsAction extends GraphsAction {

	@Override
	protected String getAction() {
		return "policy_graphs.action";
	}

	@Override
	protected void doInitPage() {
		super.doInitPage();
		page("module", IMonModule.policy.ordinal());
	}

}
