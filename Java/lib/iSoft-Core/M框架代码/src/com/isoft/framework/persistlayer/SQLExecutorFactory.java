package com.isoft.framework.persistlayer;

import java.sql.Connection;

import com.isoft.framework.common.interfaces.IIdentityBean;

public class SQLExecutorFactory {

    public static SQLExecutor newSQLExecutor(Connection conn,IIdentityBean idBean){
        return new SQLExecutor(conn, idBean);
    }
}
