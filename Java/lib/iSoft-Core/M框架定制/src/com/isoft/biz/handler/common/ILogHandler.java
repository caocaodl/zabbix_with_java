package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface ILogHandler extends LogicHandler {
	
    BLHMethod METHOD_DOPLATFORM_LOG_PAGE = new BLHMethod("doLogPage",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod METHOD_DOPLATFORM_LOG_ADD = new BLHMethod("doLogAdd",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doLogRequest = new BLHMethod("doLogRequest",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod doLogGetBT = new BLHMethod("doLogGetBT",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
