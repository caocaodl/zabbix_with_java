package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IBizTopoHandler extends LogicHandler {
	
	BLHMethod doTBizXml = new BLHMethod("doTBizXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTBizSave = new BLHMethod("doTBizSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTBizNodeUpdateG = new BLHMethod("doTBizNodeUpdateG",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTBizNodeDel = new BLHMethod("doTBizNodeDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNodePage = new BLHMethod("doNodePage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTBizDel = new BLHMethod("doTBizDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTBizLineDel = new BLHMethod("doTBizLineDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetBizTopoData = new BLHMethod("doGetBizTopoData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoDataSave = new BLHMethod("doTopoDataSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoBizNodeDataSave = new BLHMethod("doTopoBizNodeDataSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoBizNodeDataEdit = new BLHMethod("doTopoBizNodeDataEdit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
		
	BLHMethod doTopoBizNodeDataDel = new BLHMethod("doTopoBizNodeDataDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoDataDel = new BLHMethod("doTopoDataDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoDataEdit = new BLHMethod("doTopoDataEdit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTopoBizDataGet = new BLHMethod("doTopoBizDataGet",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoBizHostDataGet = new BLHMethod("doTopoBizHostDataGet",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoBizTopoAndNodeDataSave = new BLHMethod("doTopoBizTopoAndNodeDataSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTopoBizTopoAndNodeDataEdit = new BLHMethod("doTopoBizTopoAndNodeDataEdit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetBizTopoAllData = new BLHMethod("doGetBizTopoAllData",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doTopoBizDataGetToAdmin = new BLHMethod("doTopoBizDataGetToAdmin",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
			
	BLHMethod doGetBizTopoAllDataToAdmin = new BLHMethod("doGetBizTopoAllDataToAdmin",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
