package com.isoft.web.common;

import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.exception.ExceptionInterceptor;
import com.isoft.exception.IErrorCode;
import com.isoft.framework.common.DelegateFactory;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IDelegate;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;


public abstract class DelegateAware implements DelegateLock {
    
    protected abstract IdentityBean getIdentityBean();
    
    public IdentityBean getIdBean(){
        return getIdentityBean();
    }
    
    protected IResponseEvent delegator(RequestEvent request) {
        return delegator(request, null);
    }
    
    protected IResponseEvent delegator(RequestEvent request,
            ExceptionInterceptor ei) {
        IdentityBean identityBean = getIdentityBean();
        return delegator(identityBean, request, ei);
    }
    
    protected IResponseEvent delegator(IdentityBean identityBean,
            RequestEvent request) {
        return delegator(identityBean, request, null);
    }
    
    protected IResponseEvent delegator(IdentityBean identityBean,
            RequestEvent re, ExceptionInterceptor ei) {
        IDelegate delegate = DelegateFactory.newDelegateInstance();
        IResponseEvent responseEvent = delegate.delegate(identityBean, re);
        if (responseEvent.hasException()) {
            BusinessException e = responseEvent.getBusinessException();
            processException(re, e, ei);
        }
        return responseEvent;
    }
    
    protected void processException(IRequestEvent re, BusinessException e,
            ExceptionInterceptor ei) {
        IErrorCode ec = e.getErrorCode();
        if (ei != null) {
            IErrorCode[] eMask = ei.getErrorMask();
            if (eMask != null && eMask.length > 0) {
                for (IErrorCode ecm : eMask) {
                    if(ecm.equals(ec)){
                        ei.interceptException(e);
                        return;
                    }
                }
            }            
        }
        throw e;
    }
}
