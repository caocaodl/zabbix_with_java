package com.isoft.biz.exception;

import com.isoft.exception.IErrorCode;

public final class ExceptionInterceptor {
    private BusinessException e;
    private IErrorCode[] errorMask;
    
    public ExceptionInterceptor(IErrorCode ... errorMask) {
        this.errorMask = errorMask;
    }

    public void interceptException(BusinessException e) {
        this.e = e;
    }

    public BusinessException getBusinessException() {
        return e;
    }
    
    public IErrorCode[] getErrorMask() {
        return errorMask;
    }

    public boolean find() {
        return this.e != null;
    }
}
