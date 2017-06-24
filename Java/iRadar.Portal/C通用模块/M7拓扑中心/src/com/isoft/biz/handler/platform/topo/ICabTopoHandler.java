package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ICabTopoHandler extends LogicHandler {

	BLHMethod doPicList = new BLHMethod("doPicList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doCabTopoXml = new BLHMethod("doCabTopoXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTCabTopoNodeSave = new BLHMethod("doTCabTopoNodeSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doCabTopoUpdateG = new BLHMethod("doCabTopoUpdateG",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNodePage = new BLHMethod("doNodePage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTCabTopoDel = new BLHMethod("doTCabTopoDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNodeDel = new BLHMethod("doNodeDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetRoomData = new BLHMethod("doGetRoomData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetCabData = new BLHMethod("doGetCabData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doGetCabTopoData = new BLHMethod("doGetCabTopoData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
