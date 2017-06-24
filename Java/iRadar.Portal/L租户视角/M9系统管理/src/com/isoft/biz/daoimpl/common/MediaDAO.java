package com.isoft.biz.daoimpl.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.IMediaDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

public class MediaDAO extends BaseDAO implements IMediaDAO{

	public MediaDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_LIST_USERS_BY_TENANTID = "SQL_LIST_USERS_BY_TENANTID";
	public List doList(String tenantId) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LIST_USERS_BY_TENANTID);
		Map sqlVO = getSqlVO(SQL_LIST_USERS_BY_TENANTID);
		Map paraMap = new LinkedMap();
		paraMap.put("tenantid", tenantId);
		return executor.executeNameParaQuery(sql,paraMap,sqlVO);
	}
	
	private static final String SQL_GET_MEDIA_TYPES = "SQL_GET_MEDIA_TYPES";
	public List doGetMediaTypes() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_MEDIA_TYPES);
		Map sqlVO = getSqlVO(SQL_GET_MEDIA_TYPES);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	
	private static final String SQL_GET_MEDIABY_USERID = "SQL_GET_MEDIABY_USERID";
	public List doGetMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_MEDIABY_USERID);
		Map sqlVO = getSqlVO(SQL_GET_MEDIABY_USERID);
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	
	private static final String SQL_DELETE_MEDIA_BY_USERID = "SQL_DELETE_MEDIA_BY_USERID";
	public boolean doDeleteMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_DELETE_MEDIA_BY_USERID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)>0;
	}
	
	private static final String SQL_ADD_MEDIA_BY_USERID = "SQL_ADD_MEDIA_BY_USERID";
	public boolean doAddMediaByUserId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String id = getFlowcode(NameSpaceEnum.OPERATION_SYSTEM);
		paraMap.put("mediaid", id);
		String sql = getSql(SQL_ADD_MEDIA_BY_USERID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)>0;
	}
	
}
