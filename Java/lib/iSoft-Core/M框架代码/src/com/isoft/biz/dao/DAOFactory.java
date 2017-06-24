package com.isoft.biz.dao;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.persistlayer.SQLExecutor;

public class DAOFactory {
    private static Log logger = LogFactory.getLog(DAOFactory.class);
    
    //dao实现类包名
    private static final String IMPLEMENTATION_PACKAGE = "com.isoft.biz.daoimpl.";
    
    //获得数据库检索dao实现
    public static IDAO newDAOImplInstance(String moduleName,
            String interfaceName, SQLExecutor executor) {
        //获得dao实现类的类名
        int dotIndex = interfaceName.lastIndexOf(".");
        if (dotIndex != -1) {
            interfaceName = interfaceName.substring(dotIndex + 2);
        }
        String handlerImplName = moduleName + "." + interfaceName;
        try {
            //实例化dao
            Class<?> daoClass = Class.forName(IMPLEMENTATION_PACKAGE
                    + handlerImplName);
            Constructor<?> constructor = daoClass
                    .getDeclaredConstructor(new Class[] { SQLExecutor.class });
            return (IDAO) constructor.newInstance(new Object[] { executor });

        } catch (Exception e) {
            logger.error("Initiate handler error", e);
            throw BizError.createCodingException(
                    ErrorCodeEnum.CODING_BLHMETHOD_IMPLEMENT_ERROR, e);
        }
    }
}
