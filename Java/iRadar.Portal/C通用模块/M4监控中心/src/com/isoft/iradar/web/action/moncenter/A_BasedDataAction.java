package com.isoft.iradar.web.action.moncenter;

import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;

public abstract class A_BasedDataAction extends RadarBaseAction  implements I_LatestDataAction {

	@Override
	public long getHostGroupId() {
		CArray configs = (CArray)CONFIGS.get(getSimpleAction());
		IMonGroup group = (IMonGroup)configs.get("group");
		return group.id();
	}

}
