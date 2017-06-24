package com.isoft.biz.handler.portserves;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IItemDataInterfaceHandler extends LogicHandler{
	BLHMethod doList = new BLHMethod("doList",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

}
