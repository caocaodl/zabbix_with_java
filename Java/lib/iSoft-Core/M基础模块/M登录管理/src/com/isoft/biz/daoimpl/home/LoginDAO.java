package com.isoft.biz.daoimpl.home;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.home.ILoginDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.model.PermItem;

public class LoginDAO extends BaseDAO implements ILoginDAO {

	public LoginDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_GET_USER = "SQL_GET_USER";

	public List getUser(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_USER);
		Map sqlVO = getSqlVO(SQL_GET_USER);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	private static final String SQL_GET_TENANT = "SQL_GET_TENANT";
	public List getTenant(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_TENANT);
		Map sqlVO = getSqlVO(SQL_GET_TENANT);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	private static final String SQL_UPDATE_LAST_LOGIN_AT = "SQL_UPDATE_LAST_LOGIN_AT";
	public void updateLastLoginAt(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_LAST_LOGIN_AT);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_GET_USER_PERMS = "SQL_GET_USER_PERMS";
	public List<PermItem> getUserPerms(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_USER_PERMS);
		return executor.executeNameParaQuery(sql, paraMap, PermItem.class);
	}

}
