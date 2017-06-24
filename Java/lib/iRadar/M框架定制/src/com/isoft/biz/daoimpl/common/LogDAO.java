package com.isoft.biz.daoimpl.common;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.ILogDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;

public class LogDAO extends BaseDAO implements ILogDAO {

	public LogDAO(SQLExecutor executor) {
		super(executor);
	}

	private static final String SQL_LOG_PAGE = "SQL_LOG_PAGE";

	public List doLogPage(DataPage dataPage, Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LOG_PAGE);
		Map sqlVO = getSqlVO(SQL_LOG_PAGE);
		return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
	}

	private static final String SQL_LOG_ADD = "SQL_LOG_ADD";

	public String doLogAdd(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LOG_ADD);
		String id = getFlowcode(NameSpaceEnum.SYS_LOG);
		paraMap.put("id", id);
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			return id;
		} else {
			return null;
		}
	}
	
	private final static String SQL_GET_FUNC_REQ = "SQL_GET_FUNC_REQ";
	private final static String SQL_ADD_FUNC_REQ = "SQL_ADD_FUNC_REQ";
	private final static String SQL_UPDATE_FUNC_REQ = "SQL_UPDATE_FUNC_REQ";
	public void doLogRequest(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GET_FUNC_REQ);
		List<String> data = executor.executeNameParaQuery(sql, paraMap, String.class);
		if(data.isEmpty()){
			sql = getSql(SQL_ADD_FUNC_REQ);
			executor.executeInsertDeleteUpdate(sql, paraMap);
		} else {
			String id = data.get(0);
			sql = getSql(SQL_UPDATE_FUNC_REQ);
			paraMap.put("id", id);
			executor.executeInsertDeleteUpdate(sql, paraMap);
		}
	}
}
