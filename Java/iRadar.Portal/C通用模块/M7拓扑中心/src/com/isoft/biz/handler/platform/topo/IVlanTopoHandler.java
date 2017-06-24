package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IVlanTopoHandler extends LogicHandler {

	BLHMethod doVlanTopoMenuJson = new BLHMethod("doVlanTopoMenuJson",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doVlanTopoXml = new BLHMethod("doVlanTopoXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
