package com.isoft.biz.handler.common;

import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.method.BLHMethod;
import com.isoft.biz.method.Role;
import com.isoft.dictionary.FuncIdEnum;

public interface IWarnHandler extends LogicHandler {
	
    BLHMethod METHOD_DOPLATFORM_WARN_PAGE = new BLHMethod("doWarnPage",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
    
    BLHMethod METHOD_DOPLATFORM_WARN_EDIT = new BLHMethod("doWarnEdit",
            FuncIdEnum.DEFAULT_FUNID, Role.ANYONE);
}
