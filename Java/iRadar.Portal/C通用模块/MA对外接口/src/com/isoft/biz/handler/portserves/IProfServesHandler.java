package com.isoft.biz.handler.portserves;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IProfServesHandler extends LogicHandler {

	BLHMethod doProtServer = new BLHMethod("doProtServer",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doInterfaceServer = new BLHMethod("doInterfaceServer",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doInterfaceServerDelect = new BLHMethod("doInterfaceServerDelect",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod doFind = new BLHMethod("doFind",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);


	

}
