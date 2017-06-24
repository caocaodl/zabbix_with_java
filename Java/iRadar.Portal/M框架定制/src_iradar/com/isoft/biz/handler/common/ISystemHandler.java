package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ISystemHandler extends  LogicHandler{
	
		BLHMethod doSystem = new BLHMethod("doSystem",
	            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
		
		BLHMethod doAdd = new BLHMethod("doAdd",
	            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
		
		BLHMethod doUpdate = new BLHMethod("doUpdate",
	            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
		
		BLHMethod doDelete = new BLHMethod("doDelete",
	            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	          
		BLHMethod doSysRelationGet = new BLHMethod("doSysRelationGet",
	            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
