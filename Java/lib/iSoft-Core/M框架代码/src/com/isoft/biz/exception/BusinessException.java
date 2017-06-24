package com.isoft.biz.exception;

import com.isoft.exception.IErrorCode;
import com.isoft.exception.IErrorSeverity;

public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /** @serial */
    private IErrorCode errorCode;
    
    /** @serial */
    private IErrorSeverity errorSeverity;

    /** @serial */
    private Object[] errorParams;
    
    public BusinessException(IErrorSeverity errorSeverity, IErrorCode errorCode, Throwable cause, Object ... errorParams) {
        super(cause);
        this.errorParams = errorParams;
        this.errorCode = errorCode;
        this.errorSeverity = errorSeverity;
    }

    @Deprecated
    public BusinessException(String msg) {
        super(msg);
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }

    public IErrorSeverity getErrorSeverity() {
        return errorSeverity;
    }

    public Object[] getErrorParams() {
        return errorParams;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }    
    
}
