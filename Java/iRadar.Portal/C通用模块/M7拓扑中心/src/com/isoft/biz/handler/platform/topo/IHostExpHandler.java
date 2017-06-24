package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IHostExpHandler extends LogicHandler {
	
	BLHMethod doCategoryList = new BLHMethod("doCategoryList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
		
	BLHMethod doAssetsCategoryList = new BLHMethod("doAssetsCategoryList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
