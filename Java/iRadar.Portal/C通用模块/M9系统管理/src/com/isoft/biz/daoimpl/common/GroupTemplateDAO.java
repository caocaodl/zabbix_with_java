package com.isoft.biz.daoimpl.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.common.IGroupTemplateDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;

public class GroupTemplateDAO extends BaseDAO implements IGroupTemplateDAO {

	public GroupTemplateDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_LIST = "SQL_LIST";

	public List doList(List par) {
		Map paraMap = new LinkedMap();
		List lis = new ArrayList();
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LIST);
		Map sqlVO = getSqlVO(SQL_LIST);
		if (par.size() > 0) {
			paraMap.put("idList", par);
			lis = executor.executeNameParaQuery(sql, paraMap, sqlVO);
		}
		return lis;
	}
}
