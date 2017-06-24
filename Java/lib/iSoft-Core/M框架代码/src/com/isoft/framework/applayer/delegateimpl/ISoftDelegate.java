package com.isoft.framework.applayer.delegateimpl;

import java.rmi.RemoteException;

import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.applayer.ejb.IDelegateFacade;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IDelegate;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.server.RunParams;
import com.isoft.utils.DebugUtil;
import com.isoft.utils.ServerUtil;

public class ISoftDelegate implements IDelegate {

    private IDelegateFacade getDelegateFacade(){
        return EJBFactory.getDelegateFacadeEJB();
    }
    
    private boolean checkAuthorization(IdentityBean identityBean,
            IRequestEvent requestEvent) {
//        if (!Role.isPassable(identityBean.getRole(), requestEvent.getRole())) {
//            return false;
//        }
        return ServerUtil.checkAuthorization(identityBean, requestEvent
                .getFuncId());
    }
    
    public IResponseEvent delegate(IIdentityBean idBean,IRequestEvent requestEvent) {
        IResponseEvent response = null;
        BusinessException businessException = null;
        
        if(DebugUtil.isDebugEnabled()){
            DebugUtil.debug(requestEvent);
        }
        
        if (((RequestEvent) requestEvent).isCheckLogin()) {
            IdentityBean identityBean = (IdentityBean) idBean;
            if (!checkAuthorization(identityBean, requestEvent)) {
                businessException = BizError
                        .createFrameworkException(ErrorCodeEnum.BIZLOGIC_NO_PERMISSION);
                return processException(response, businessException);
            }
        }
        
        try {
            long begin = System.currentTimeMillis();
            IResponseEvent  responseEvent = getDelegateFacade().delegate(idBean, requestEvent);
            if(RunParams.REPORT_METRIC){
                long end = System.currentTimeMillis();
                long elapsedTime = end - begin; 
                System.out.println("elapsedTime="+elapsedTime);
            }
            return responseEvent;
        } catch (RemoteException e) {
            businessException = BizError
                    .createFrameworkException(ErrorCodeEnum.FRAMEWORK_APP_NO_SERVICE_AVAILABLE);
            return processException(response, businessException);
        }
    }

    public IResponseEvent processException(IResponseEvent response,
            BusinessException e) {
        if (response == null) {
            response = new ResponseEvent();
        }
        if (e != null) {
            response.setBusinessException(e);
            e.printStackTrace(System.err);
        }
        return response;
    }
}
