package com.isoft.biz.handler;

import com.isoft.biz.exception.BusinessException;
import com.isoft.framework.common.interfaces.IResponseEvent;

public interface LogicHandler {

    IResponseEvent processException(IResponseEvent response, BusinessException e);
    
}
