package com.isoft.biz.daoimpl.reportForms;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;


import com.isoft.biz.dao.common.IRoleDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.handler.reportForms.IReportFormshandler;


import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;

public class ReportFormsDAO  extends BaseDAO implements IReportFormshandler {


	public ReportFormsDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
		// TODO Auto-generated constructor stub
	}
	private static final String SQL_UPDATE_COUNT = "SQL_UPDATE_COUNT";
	private static final String SQL_UPDATE_ZU = "SQL_UPDATE_ZU";
	private static final String SQL_UPDATE_COUNT_FEN_WAI = "SQL_UPDATE_COUNT_FEN_WAI";
	private static final String SQL_UPDATE_COUNT_FEN = "SQL_UPDATE_COUNT_FEN";
	private static final String SQL_UPDATE_OS = "SQL_UPDATE_OS";
	private static final String SQL_UPDATE_OS_TU = "SQL_UPDATE_OS_TU";
	private static final String SQL_UPDATE_COUNT_FEN_OS = "SQL_UPDATE_COUNT_FEN_OS";
	private static final String SQL_UPDATE_COUNT_FEN_OS_WAI = "SQL_UPDATE_COUNT_FEN_OS_WAI";
	public List doForm() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doFormOs() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_OS_TU);
		Map sqlVO = getSqlVO(SQL_UPDATE_OS_TU);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doGroup() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_ZU);
		Map sqlVO = getSqlVO(SQL_UPDATE_ZU);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doOs() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_OS);
		Map sqlVO = getSqlVO(SQL_UPDATE_OS);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doSingleGroup(String groupid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT_FEN);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT_FEN);
		Map paraMap = new LinkedMap();
		paraMap.put("groupid", groupid);
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List dogroup(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT_FEN);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT_FEN);
//		Map paraMap = new LinkedMap();
//		paraMap.put("groupid", groupid);
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doSingleGroupOs(String hostid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT_FEN_OS);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT_FEN_OS);
		Map paraMap = new LinkedMap();
		paraMap.put("hostid", hostid);
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doEquipmentSingleElse(String groupid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT_FEN_WAI);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT_FEN_WAI);
		Map paraMap = new LinkedMap();
		paraMap.put("groupid", groupid);
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List doOsSingleElse(String hostid) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT_FEN_OS_WAI);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT_FEN_OS_WAI);
		Map paraMap = new LinkedMap();
		paraMap.put("hostid", hostid);
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	@Override
	public IResponseEvent processException(IResponseEvent arg0,
			BusinessException arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
