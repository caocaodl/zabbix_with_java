package com.isoft.iradar.web.action.moncenter;

import static com.isoft.iradar.inc.FuncsUtil.get_request;

public class OthersLatestDataAction extends A_LatestDataAction {
	
	@Override
	public long getHostGroupId() {
		return get_request("groupid",0L);
	}

	@Override
	public String getSimpleAction() {
		return null;
	}

	@Override
	public String getLatestAction() {
		return OTHERS_LATEST_DATA_ACTION;
	}
	
}
