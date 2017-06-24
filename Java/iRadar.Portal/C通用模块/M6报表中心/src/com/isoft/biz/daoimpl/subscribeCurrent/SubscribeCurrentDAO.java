package com.isoft.biz.daoimpl.subscribeCurrent;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.LinkedMap;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.biz.dao.subscribeCurrent.ISubscribeCurrentDAO;

public class SubscribeCurrentDAO extends BaseDAO implements ISubscribeCurrentDAO {

	public SubscribeCurrentDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_SUBSCRIBECURRENT_PAGE = "SQL_SUBSCRIBECURRENT_PAGE";
	
	public List doSubscribeCurrentPage(String startTime,String endTime) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_SUBSCRIBECURRENT_PAGE);
		Map sqlVO = getSqlVO(SQL_SUBSCRIBECURRENT_PAGE);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}

}
