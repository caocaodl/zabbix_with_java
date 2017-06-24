package com.isoft.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoft.server.RunParams;

public final class DebugUtil {
    private static final Log LOGGER = LogFactory.getLog(DebugUtil.class);
    
    private DebugUtil() {
    }
    
    public static boolean isWarnEnabled() {
        return LOGGER.isWarnEnabled();
    }

    public static boolean isDebugEnabled() {
        return RunParams.DEBUG && LOGGER.isDebugEnabled();
    }
    
    public static boolean isErrorEnabled() {
        return LOGGER.isErrorEnabled();
    }
    
    public static boolean isFatalEnabled() {
        return LOGGER.isFatalEnabled();
    }
    
    public static void warn(Object obj) {
        LOGGER.warn(obj);
    }
    
    public static void debug(Object obj) {
        LOGGER.debug(obj);
    }
    
    public static void error(Object obj) {
        LOGGER.error(obj);
    }
    
    public static void fatal(Object obj) {
        LOGGER.fatal(obj);
    }

}
