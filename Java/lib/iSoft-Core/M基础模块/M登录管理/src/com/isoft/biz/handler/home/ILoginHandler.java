package com.isoft.biz.handler.home;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ILoginHandler extends LogicHandler {

	BLHMethod METHOD_DOLOGIN = new BLHMethod("doLogin",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);

	BLHMethod METHOD_DOLOGIN_SSO = new BLHMethod("doLoginForSso",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
