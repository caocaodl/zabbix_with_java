package com.isoft.biz.handler.subscribeCurrent;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ISubscribeCurrentHandler extends LogicHandler {

	BLHMethod doSubscribeCurrentPage = new BLHMethod("doSubscribeCurrentPage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
