package com.isoft.biz.daoimpl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.model.FuncItem;
import com.isoft.model.PermItem;

public class PreLoaderDAO extends BaseDAO {

    public PreLoaderDAO(SQLExecutor sqlExecutor) {
        super(sqlExecutor);
    }
    
    private static final String SQL_LOAD_SYS_DICT = "SQL_LOAD_SYS_DICT";
    public List loadSysDicts() {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_LOAD_SYS_DICT);
        Map sqlVO = getSqlVO(SQL_LOAD_SYS_DICT);
        Map paraMap = new HashMap();
        return executor.executeNameParaQuery(sql, paraMap, sqlVO);
    }
    
    private static final String SQL_LOAD_SYS_FUNC = "SQL_LOAD_SYS_FUNC";
    public List loadSysFuncs() {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_LOAD_SYS_FUNC);
        Map paraMap = new HashMap();
        return executor.executeNameParaQuery(sql, paraMap, FuncItem.class);
    }
    
    private static final String SQL_LOAD_SYS_PERM = "SQL_LOAD_SYS_PERM";
    public List loadSysPerms() {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_LOAD_SYS_PERM);
        Map paraMap = new HashMap();
        return executor.executeNameParaQuery(sql, paraMap, PermItem.class);
    }
}
