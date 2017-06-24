package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IProfHandler extends LogicHandler {

	BLHMethod doProfView = new BLHMethod("doProfView",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doProfEdit = new BLHMethod("doProfEdit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doProfPswd = new BLHMethod("doProfPswd",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTenantView = new BLHMethod("doTenantView",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doTenantEdit = new BLHMethod("doTenantEdit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doPwdReset = new BLHMethod("doPwdReset",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

}
