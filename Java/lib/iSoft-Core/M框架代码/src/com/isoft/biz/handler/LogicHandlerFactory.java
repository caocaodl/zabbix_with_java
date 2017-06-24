package com.isoft.biz.handler;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;

public class LogicHandlerFactory {

    private static final String IMPLEMENTATION_PACKAGE = "com.isoft.biz.handlerimpl.";

    public static LogicHandler newLogicHandlerInstance(String modulePackage,String interfaceName) {
        int dotIndex = interfaceName.lastIndexOf(".");
        if (dotIndex != -1) {
            interfaceName = interfaceName.substring(dotIndex + 2);
        }
        String handlerImplName = modulePackage+"."+interfaceName;
        try {
            Class<?> handlerClass = Class.forName(IMPLEMENTATION_PACKAGE
                    + handlerImplName);
            return (LogicHandler) handlerClass.newInstance();
        } catch (Exception e) {
            throw BizError.createCodingException(
                    ErrorCodeEnum.CODING_BLHMETHOD_IMPLEMENT_ERROR, e);
        }
    }
}
