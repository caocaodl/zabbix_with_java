package com.isoft.biz.handler.platform.topo;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IBackGPicHandler extends LogicHandler {
	
	BLHMethod doBackGPicList = new BLHMethod("doBackGPicList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doBackGPicChange = new BLHMethod("doBackGPicChange",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
	BLHMethod doBackGPicInit = new BLHMethod("doBackGPicInit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
