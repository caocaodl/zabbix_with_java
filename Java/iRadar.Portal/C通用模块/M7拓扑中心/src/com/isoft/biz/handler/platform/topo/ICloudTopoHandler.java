package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ICloudTopoHandler extends LogicHandler {

	BLHMethod doColudTopoMenuJson = new BLHMethod("doColudTopoMenuJson",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doColudTopoXml = new BLHMethod("doColudTopoXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetCloudTopoData = new BLHMethod("doGetCloudTopoData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
