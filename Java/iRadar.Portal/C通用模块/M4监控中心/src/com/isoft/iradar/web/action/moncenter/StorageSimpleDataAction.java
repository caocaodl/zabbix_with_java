package com.isoft.iradar.web.action.moncenter;

import static com.isoft.iradar.inc.FuncsUtil.get_request;

import com.isoft.iradar.common.util.IMonConsts;

public class StorageSimpleDataAction extends A_LatestDataAction {

	@Override
	public long getHostGroupId() {
		/**
		 * 系统屏蔽默认的存储设备类型
		 */
//		return get_request("groupid",IMonConsts.MON_STORAGE);
		return get_request("groupid",0);
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
