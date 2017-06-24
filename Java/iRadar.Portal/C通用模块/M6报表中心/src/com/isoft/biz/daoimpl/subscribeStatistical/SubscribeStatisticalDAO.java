package com.isoft.biz.daoimpl.subscribeStatistical;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.LinkedMap;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.biz.dao.subscribeStatistical.ISubscribeStatisticalDAO;

public class SubscribeStatisticalDAO extends BaseDAO implements ISubscribeStatisticalDAO {

	public SubscribeStatisticalDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_SUBSCRIBESTATISTICAL_PAGE = "SQL_SUBSCRIBESTATISTICAL_PAGE";
	
	public List doSubscribeStatisticalPage() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_SUBSCRIBESTATISTICAL_PAGE);
		Map sqlVO = getSqlVO(SQL_SUBSCRIBESTATISTICAL_PAGE);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}

}
