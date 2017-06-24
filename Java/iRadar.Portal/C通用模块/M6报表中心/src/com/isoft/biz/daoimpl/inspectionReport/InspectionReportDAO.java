package com.isoft.biz.daoimpl.inspectionReport;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.views.freemarker.tags.ParamModel;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.inspectionReport.IInspectionReportDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;

public class InspectionReportDAO extends BaseDAO implements
		IInspectionReportDAO {

	public InspectionReportDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_PAGE = "SQL_PAGE";
	public List doPage(DataPage dataPage, Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PAGE);
		Map sqlVO = getSqlVO(SQL_PAGE);
		return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
	}
	
	private static final String SQL_ADD = "SQL_ADD";
	public String[] doAdd(Map paraMap) {
		String[] ret = new String[2];
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ADD);
		String id = getFlowcode(NameSpaceEnum.I_INSPECTION_REPORT);
		paraMap.put("id", id);
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			ret[0] = id;
			ret[1] = null;
		}
		return ret;
	}
	
	private static final String SQL_UPDATE = "SQL_UPDATE";
	public String[] doUpdate(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_UPDATE);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				ret[0] = id;
				ret[1] = null;
			} else {
				ret[0] = null;
				ret[1] = "2";
			}
		}
		return ret;
	}	
	
	private static final String SQL_UPDATE_STATUS = "SQL_UPDATE_STATUS";
	public String[] doUpdateStatus(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_UPDATE_STATUS);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				ret[0] = id;
				ret[1] = null;
			} else {
				ret[0] = null;
				ret[1] = "2";
			}
		}
		return ret;
	}
	
	private final static String SQL_CHECK_NAME = "SQL_CHECK_NAME";
	public boolean doCheckName(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CHECK_NAME);
		List data = executor.executeNameParaQuery(sql, paraMap, String.class);
		return data.isEmpty();
	}

	private final static String SQL_MONITOR_HOST = "SQL_MONITOR_HOST";
	public List doMonitorHost(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_MONITOR_HOST);
		Map sqlVO = getSqlVO(SQL_MONITOR_HOST);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	private final static String SQL_MONITOR_APPLICATION = "SQL_MONITOR_APPLICATION";
	public List doMonitorApplication(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_MONITOR_APPLICATION);
		Map sqlVO = getSqlVO(SQL_MONITOR_APPLICATION);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	private static final String SQL_ADD_INSPECTION_HOST = "SQL_ADD_INSPECTION_HOST";
	public String[] doAddInspectionHost(Map paraMap) {
		String[] ret = new String[2];
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ADD_INSPECTION_HOST);
		String id = getFlowcode(NameSpaceEnum.I_INSPECTION_HOST);
		paraMap.put("id", id);
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			ret[0] = id;
			ret[1] = null;
		}
		return ret;
	}
	
	private final static String SQL_INSPECTION_HOST_APPLICATION = "SQL_INSPECTION_HOST_APPLICATION";
	public List doInspectionHostApplication(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_INSPECTION_HOST_APPLICATION);
		Map sqlVO = getSqlVO(SQL_INSPECTION_HOST_APPLICATION);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}

	private static final String SQL_DELETE_INSPECTION_HOST = "SQL_DELETE_INSPECTION_HOST";
	public String[] doDeleteInspectionHost(Map paraMap) {
		String[] ret = new String[2];
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_DELETE_INSPECTION_HOST);
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			ret[0] = paraMap.get("inspectionId").toString();
			ret[1] = null;
		}
		return ret;
	}

	private static final String SQL_INSPECTION_HISTORY_PAGE = "SQL_INSPECTION_HISTORY_PAGE";
	public List doInspectionHistoryPage(DataPage dataPage, Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_INSPECTION_HISTORY_PAGE);
		Map sqlVO = getSqlVO(SQL_INSPECTION_HISTORY_PAGE);
		return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
	}
	
	private static final String SQL_INSPECTION_HISTORY_INFO = "SQL_INSPECTION_HISTORY_INFO";
	public List doInspectionHistoryInfo(DataPage dataPage, Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_INSPECTION_HISTORY_INFO);
		Map sqlVO = getSqlVO(SQL_INSPECTION_HISTORY_INFO);
		return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
	}
	
	private static final String SQL_INSPECTION_TIMERULE = "SQL_INSPECTION_TIMERULE";
	public List doInspectionTimeRuleList(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_INSPECTION_TIMERULE);
		Map sqlVO = getSqlVO(SQL_INSPECTION_TIMERULE);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
}
