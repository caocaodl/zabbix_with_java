package com.isoft.biz.handler.subscribeStatistical;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ISubscribeStatisticalHandler extends LogicHandler {

	BLHMethod doSubscribeStatisticalPage = new BLHMethod("doSubscribeStatisticalPage",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	
}
