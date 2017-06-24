package com.isoft.framework.common;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.common.interfaces.IDelegate;

public final class DelegateFactory {
    private static final String IMPL_PREIFX = "com.isoft.framework.applayer.delegateimpl.";
    private static final String IMPL_CLASS = "ISoftDelegate";

    @SuppressWarnings("unchecked")
    private static Class delegateImplClass = IDelegate.class;
    static {
        try {
            delegateImplClass = Class.forName(IMPL_PREIFX + IMPL_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private DelegateFactory() {
    }
    
    public static IDelegate newDelegateInstance() {
        try {
            return (IDelegate) delegateImplClass.newInstance();
        } catch (Exception e) {
            throw BizError.createBizLogicException(ErrorCodeEnum.BIZLOGIC_UNKNOWN, e);
        }
    }
}
