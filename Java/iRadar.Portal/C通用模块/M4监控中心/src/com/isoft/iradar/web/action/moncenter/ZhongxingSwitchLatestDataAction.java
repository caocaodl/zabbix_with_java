package com.isoft.iradar.web.action.moncenter;

import com.isoft.iradar.common.util.IMonConsts;

public class ZhongxingSwitchLatestDataAction extends A_LatestDataAction {
	
	@Override
	public long getHostGroupId() {
		return IMonConsts.MON_NET_ZHONGXING_SWITCH;
	}
	
	@Override
	public String getSimpleAction() {
//		return ZHONGXING_SIMPLE_DATA_ACTION;
		return null;
	}

	@Override
	public String getLatestAction() {
		return ZHONGXING_LATEST_DATA_ACTION;
	}
	
}
