package com.isoft.biz.daoimpl.portserves;

import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.LinkedMap;
import com.isoft.biz.dao.portserves.IProfServesDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

public class UserResourcesDAO extends BaseDAO implements IProfServesDAO {

	public UserResourcesDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static final String SQL_SELECT = "SQL_SELECT";
	
	public List doResourceUser() {
		SQLExecutor executor = getSqlExecutor();
		Map paraMap = new LinkedMap();
		String sql = getSql(SQL_SELECT);
		Map sqlVO = getSqlVO(SQL_SELECT);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
}
