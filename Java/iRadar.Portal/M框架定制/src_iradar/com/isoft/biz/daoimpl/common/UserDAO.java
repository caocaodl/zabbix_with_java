package com.isoft.biz.daoimpl.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.IUserDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

public class UserDAO extends BaseDAO implements IUserDAO{

	public UserDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_DELETE_MEDIA_BY_USERID = "SQL_DELETE_MEDIA_BY_USERID";
	public boolean doDeleteMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_DELETE_MEDIA_BY_USERID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)>0;
	}
	
	private static final String SQL_GET_MEDIA_BY_USERID = "SQL_GET_MEDIA_BY_USERID";
	public List doGetMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_MEDIA_BY_USERID);
		Map sqlVO = getSqlVO(SQL_GET_MEDIA_BY_USERID);
		return executor.executeNameParaQuery(sql,paraMap,sqlVO);
	}
	
	private static final String SQL_ENABLE_MEDIA_BY_USERID = "SQL_ENABLE_MEDIA_BY_USERID";
	public boolean doEnableMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ENABLE_MEDIA_BY_USERID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)>0;
	}
	
	private static final String SQL_DISABLE_MEDIA_BY_USERID = "SQL_DISABLE_MEDIA_BY_USERID";
	public boolean doDisableMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_DISABLE_MEDIA_BY_USERID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)>0;
	}
	
	private static final String SQL_ADD_MEDIA_BY_USERID = "SQL_ADD_MEDIA_BY_USERID";
	public boolean doAddMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String mediaId = getFlowcode(NameSpaceEnum.OPERATION_SYSTEM);
		paraMap.put("mediaid", mediaId);
		String sql = getSql(SQL_ADD_MEDIA_BY_USERID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)>0;
	}
	
}
