package com.isoft.framework.applayer.ejb;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.isoft.biz.daoimpl.DBIDGeneratorFactory;
import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.applayer.bo.DelegateManager;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.ResponseEvent;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.common.interfaces.IRequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.DataSourceManager;
import com.isoft.utils.DebugUtil;

public class DelegateFacadeDummy extends DataSourceManager implements IDelegateFacade{
    
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

    /**
     * 业务代理方法
     */
    public IResponseEvent delegate(IIdentityBean idBean, IRequestEvent requestEvent) {
        BusinessException businessException = null;
        
        Connection conn = null;
        IResponseEvent response = null;
        boolean commitStatusKeeper = false;
        
        try {
            DataSource dataSource = getDataSource(((RequestEvent)requestEvent));
            DBIDGeneratorFactory.initDataSource(dataSource);
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            businessException = BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_DB_NO_CONNECTION_AVAILABLE, e);
            return processException(response, businessException);
        } finally {
            // 数据库连接关闭操作
            if (businessException != null && conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        
        try {
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
                commitStatusKeeper = true;
            }
        } catch (SQLException e) {
            businessException = BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_DB_TURNON_TRANSACTION_FAIL, e);
            return processException(response, businessException);
        } finally {
            // 数据库连接关闭操作
            if (businessException != null && conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

        try {
            DelegateManager manager = new DelegateManager(conn);
            //执行业务逻辑方法
            response = manager.executeBizLogic(idBean,requestEvent);
            //处理业务异常
            if(!response.hasException()){
                try {
                    conn.commit();
                    if (DebugUtil.isDebugEnabled()) {
                        DebugUtil.debug("Database Commit .....");
                    }
                } catch (SQLException e) {
                    try {
                        conn.rollback();
                    } catch (SQLException re) {
                    }
                    businessException = BizError.createFrameworkException(
                            ErrorCodeEnum.FRAMEWORK_DB_TRANSACTION_COMMIT_FAIL,
                            e);
                    return processException(response, businessException);
                }
            }else{
                try {
                    conn.rollback();
                    if (DebugUtil.isDebugEnabled()) {
                        DebugUtil.debug("Database Roll back .....");
                    }
                } catch (SQLException e) {
                    businessException = BizError.createFrameworkException(
                            ErrorCodeEnum.FRAMEWORK_DB_TRANSACTION_ROLLBACK_FAIL,
                            e);
                    return processException(response, businessException);
                }
            }
        } catch (Exception e) {
            businessException = BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_UNKNOWN_ERROR,
                    e);
            return processException(response, businessException);
        } finally {
            //数据库连接关闭操作
            if(conn != null){
                try{
                    if (commitStatusKeeper) {
                        conn.setAutoCommit(commitStatusKeeper);
                    }
                }catch(SQLException e){
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return response;
    }

    private DataSource getDataSource(RequestEvent requestEvent) {
        return getDataSource(requestEvent.getDataSource());
    }
}
