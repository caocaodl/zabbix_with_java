package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IThumbnailHandler extends LogicHandler {
	
	BLHMethod doThumbnailTree = new BLHMethod("doThumbnailTree",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailAdd = new BLHMethod("doThumbnailAdd",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailCheckOper = new BLHMethod("doThumbnailCheckOper",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailAllCheckOper = new BLHMethod("doThumbnailAllCheckOper",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod doThumbnailDel = new BLHMethod("doThumbnailDel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailXml = new BLHMethod("doThumbnailXml",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailSave = new BLHMethod("doThumbnailSave",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailNodeClear = new BLHMethod("doThumbnailNodeClear",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doThumbnailAllNodeClear = new BLHMethod("doThumbnailAllNodeClear",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doTNodeThumbnailUnchecked = new BLHMethod("doTNodeThumbnailUnchecked",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doNodeTypeList = new BLHMethod("doNodeTypeList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doLineAutoOper = new BLHMethod("doLineAutoOper",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
