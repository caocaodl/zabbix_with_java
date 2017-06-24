package com.isoft.biz.daoimpl.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.IMonitorDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

public class MonitorDAO extends BaseDAO implements IMonitorDAO{

	public MonitorDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
		// TODO Auto-generated constructor stub
	}
	
	private static final String SQL_LIST = "SQL_LIST";
	public List doListMonitor(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LIST);
		Map sqlVO = getSqlVO(SQL_LIST);
		List list= executor.executeNameParaQuery( sql,paraMap,sqlVO);
		return list;
	}
	
}
