package com.isoft.biz.handler.portserves;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ISendMessageInterfaceHandler extends LogicHandler{
	
	BLHMethod doSendMessageTel = new BLHMethod("doSendMessageTel",
			FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);


}
