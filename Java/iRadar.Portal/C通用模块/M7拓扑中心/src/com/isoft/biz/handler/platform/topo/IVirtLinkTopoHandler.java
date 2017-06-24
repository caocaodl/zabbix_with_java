package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IVirtLinkTopoHandler extends LogicHandler {
	
	BLHMethod doVirtLinkTopoXml = new BLHMethod("doVirtLinkTopoXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doGetVirtLinkTopoData = new BLHMethod("doGetVirtLinkTopoData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetVirtLinkVMData = new BLHMethod("doGetVirtLinkVMData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetTenantData = new BLHMethod("doGetTenantData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
