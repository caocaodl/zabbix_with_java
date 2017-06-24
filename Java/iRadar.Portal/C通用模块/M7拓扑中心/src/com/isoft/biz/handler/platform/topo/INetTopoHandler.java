package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface INetTopoHandler extends LogicHandler {

	BLHMethod doNodePage = new BLHMethod("doNodePage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNetTopoXml = new BLHMethod("doNetTopoXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNetTopoSave = new BLHMethod("doNetTopoSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNetTopoUpdateG = new BLHMethod("doNetTopoUpdateG",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNetTopoUpdateLineAttr = new BLHMethod("doNetTopoUpdateLineAttr",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doLineAutoOper = new BLHMethod("doLineAutoOper",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNodeDel = new BLHMethod("doNodeDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNetTopoDel = new BLHMethod("doNetTopoDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doCircleLayout = new BLHMethod("doCircleLayout",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTbnailPage = new BLHMethod("doTbnailPage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doGetPhyLinkTopoData = new BLHMethod("doGetPhyLinkTopoData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);	
}
