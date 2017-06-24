package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IDelegateHandler extends LogicHandler {

	BLHMethod doDelegate = new BLHMethod("doDelegate",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

}
