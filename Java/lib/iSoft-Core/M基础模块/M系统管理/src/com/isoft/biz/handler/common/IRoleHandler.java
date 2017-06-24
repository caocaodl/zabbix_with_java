package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IRoleHandler extends LogicHandler {

	BLHMethod doRolePage = new BLHMethod("doRolePage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doRoleAdd = new BLHMethod("doRoleAdd",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doRoleEdit = new BLHMethod("doRoleEdit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doRoleDel = new BLHMethod("doRoleDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod getAllFuncSet = new BLHMethod(
			"getAllFuncSet", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod getFuncs = new BLHMethod(
			"getFuncs", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doRoleGrantFuncs = new BLHMethod(
			"doRoleGrantFuncs", FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
	
}
