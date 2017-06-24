package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IAnnouncementHandler extends LogicHandler{
      
	BLHMethod doList = new BLHMethod("doList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doUpdata = new BLHMethod("doUpdata",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doCreate = new BLHMethod("doCreate",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doListOne = new BLHMethod("doListOne",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doDelete = new BLHMethod("doDelete",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doCease = new BLHMethod("doCease",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doStart = new BLHMethod("doStart",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doEnd = new BLHMethod("doEnd",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doListAll = new BLHMethod("doListAll",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
