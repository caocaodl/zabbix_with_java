package com.isoft.iradar.web.action.moncenter;

public class VmSimpleDataAction extends A_SimpleDataAction {

	@Override
	public String getSimpleAction() {
		return VM_SIMPLE_DATA_ACTION;
	}

	@Override
	public String getLatestAction() {
		return VM_LATEST_DATA_ACTION;
	}

}
