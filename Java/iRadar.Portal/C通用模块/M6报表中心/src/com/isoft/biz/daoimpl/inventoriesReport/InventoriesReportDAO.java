package com.isoft.biz.daoimpl.inventoriesReport;


import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.biz.dao.inventoriesReport.IInventoriesReportDAO;



public class InventoriesReportDAO extends BaseDAO implements IInventoriesReportDAO {

	public InventoriesReportDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	
	private static final String SQL_INVENTORIESREPORT_PAGE = "SQL_INVENTORIESREPORT_PAGE";
	
	public List doInventoriesReportPage(DataPage dataPage, Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_INVENTORIESREPORT_PAGE);
		Map sqlVO = getSqlVO(SQL_INVENTORIESREPORT_PAGE);
		return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
	}
	
	
	public List doInventoriesCSV(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_INVENTORIESREPORT_PAGE);
		Map sqlVO = getSqlVO(SQL_INVENTORIESREPORT_PAGE);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
}
