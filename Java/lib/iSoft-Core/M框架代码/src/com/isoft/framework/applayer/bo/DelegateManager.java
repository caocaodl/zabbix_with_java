package com.isoft.framework.applayer.bo;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.isoft.biz.dao.DAOFactory;
import com.isoft.biz.dao.IDAO;
import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.handler.LogicHandler;
import com.isoft.biz.handler.LogicHandlerFactory;
import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.framework.persistlayer.SQLExecutorFactory;

public class DelegateManager {
    private Connection conn;

    public DelegateManager(Connection conn) {
        this.conn = conn;
    }

    @SuppressWarnings("unchecked")
    public IResponseEvent executeBizLogic(IIdentityBean identityBean, IRequestEvent requestEvent) {
        IResponseEvent response = null;
        BusinessException businessException = null;

        // 实例化逻辑处理器
        LogicHandler logicHandler = LogicHandlerFactory.newLogicHandlerInstance(requestEvent.getModuleName(), requestEvent.getCallHandlerIF());

        // 实例化SQL执行器
        SQLExecutor executor = SQLExecutorFactory.newSQLExecutor(conn, identityBean);

        // 实例化DAO
        IDAO dao = DAOFactory.newDAOImplInstance(requestEvent.getModuleName(), requestEvent.getCallDAOIF(), executor);
        
        try {
            Class[] params = new Class[] {IIdentityBean.class, IRequestEvent.class, IDAO.class };
            Method bizLogicMethod = logicHandler.getClass().getMethod(requestEvent.getCallHandlerMethod(), params);
            // 执行指定的方法
            response = (IResponseEvent) bizLogicMethod.invoke(logicHandler, new Object[] {identityBean, requestEvent, dao });
        } catch (BusinessException e) {// 异常处理
            businessException = e;
        } catch (Exception e) {
        	e.printStackTrace();
            Throwable cause = e.getCause();
            cause.printStackTrace(System.err);
            if (cause != null && cause instanceof BusinessException) {
                businessException = (BusinessException) cause;
            }else {
                businessException = BizError.createCodingException(
                        ErrorCodeEnum.CODING_BLHMETHOD_IMPLEMENT_ERROR, e);
            }            
        }

        // 处理异常
        return logicHandler.processException(response, businessException);
    }
}
