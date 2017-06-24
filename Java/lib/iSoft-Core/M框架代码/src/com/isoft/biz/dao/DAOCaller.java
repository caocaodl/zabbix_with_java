package com.isoft.biz.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.isoft.biz.daoimpl.DBIDGeneratorFactory;
import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.util.BizError;
import com.isoft.dictionary.ErrorCodeEnum;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.framework.persistlayer.SQLExecutorFactory;
import com.isoft.utils.DataSourceUtil;
import com.isoft.utils.DebugUtil;

public abstract class DAOCaller {
	
	public DAOCaller() {
		this.delegate();
	}

	private void processException(BusinessException e) {
		if (e != null) {
			e.printStackTrace(System.err);
		}
	}
    
    private void delegate() {
        BusinessException businessException = null;
        
        Connection conn = null;
        boolean commitStatusKeeper = false;
        
        try {
            DataSource dataSource = DataSourceUtil.getDefaultDataSource();
            DBIDGeneratorFactory.initDataSource(dataSource);
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            businessException = BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_DB_NO_CONNECTION_AVAILABLE, e);
            processException(businessException);
            return;
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
            processException(businessException);
            return;
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
        	SQLExecutor executor = SQLExecutorFactory.newSQLExecutor(conn, null);
            boolean hasException = false;
			//执行业务逻辑方法
			try {
				execute(executor);
			} catch (Throwable e) {
				hasException = true;
			}
            //处理业务异常
            if(!hasException ){
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
                    processException(businessException);
                    return;
                }
			} else {
                try {
                    conn.rollback();
                    if (DebugUtil.isDebugEnabled()) {
                        DebugUtil.debug("Database Roll back .....");
                    }
                } catch (SQLException e) {
                    businessException = BizError.createFrameworkException(
                            ErrorCodeEnum.FRAMEWORK_DB_TRANSACTION_ROLLBACK_FAIL,
                            e);
                    processException(businessException);
                    return;
                }
            }
        } catch (Exception e) {
            businessException = BizError.createFrameworkException(
                    ErrorCodeEnum.FRAMEWORK_UNKNOWN_ERROR,
                    e);
            processException(businessException);
            return;
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
    }

	protected abstract void execute(SQLExecutor executor);
}
