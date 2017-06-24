package com.isoft.biz.handler.reportForms;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IReportTopnUsehandler  extends LogicHandler {
	BLHMethod numbuerLimit = new BLHMethod("numbuerLimit",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
	BLHMethod getChars = new BLHMethod("getChars",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

}
