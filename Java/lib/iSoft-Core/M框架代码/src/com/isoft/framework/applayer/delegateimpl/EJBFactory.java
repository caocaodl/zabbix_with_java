package com.isoft.framework.applayer.delegateimpl;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.applayer.ejb.IDelegateFacade;

public final class EJBFactory {
    private static final String IMPL_PREIFX = "com.isoft.framework.applayer.ejb.";
    private static final String IMPL_CLASS = "DelegateFacadeDummy";

    @SuppressWarnings("unchecked")
    private static Class delegateFacadeImplClass = IDelegateFacade.class;
    static {
        try {
            delegateFacadeImplClass = Class.forName(IMPL_PREIFX + IMPL_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private EJBFactory() {
    }
    
    public static IDelegateFacade getDelegateFacadeEJB() {
        try {
            return (IDelegateFacade) delegateFacadeImplClass.newInstance();
        } catch (Exception e) {
            throw BizError.createBizLogicException(ErrorCodeEnum.BIZLOGIC_UNKNOWN, e);
        }
    }
}
