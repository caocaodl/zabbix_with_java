package com.isoft.framework.applayer.ejb;

import java.rmi.RemoteException;

import com.isoft.biz.exception.BusinessException;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;

public interface IDelegateFacade {
    IResponseEvent delegate(IIdentityBean identityBean,
            IRequestEvent requestEvent) throws RemoteException;
    
    IResponseEvent processException(IResponseEvent response, BusinessException e);
}
