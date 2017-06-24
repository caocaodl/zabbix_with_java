package com.isoft.biz.daoimpl.reportForms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.Data;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.exception.BusinessException;
import com.isoft.biz.handler.reportForms.IReportFormshandler;
import com.isoft.biz.handler.reportForms.IReportTopnUsehandler;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.framework.persistlayer.SQLExecutor;

public class ReportTopnUseDAO  extends BaseDAO implements IReportTopnUsehandler{
	
	private static final String SQL_UPDATE_OS = "SQL_UPDATE_OS";
	private static final String SQL_UPDATE_COUNT_FEN = "SQL_UPDATE_COUNT_FEN";
	
	public ReportTopnUseDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	
	}
// HOST  设备   1 CPU  2 内存  3 磁盘  4 带宽
	public List getChars(String host, String  timeUp,String timeDown) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATE_COUNT_FEN);
		Map sqlVO = getSqlVO(SQL_UPDATE_COUNT_FEN);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
	}
	public List numbuerLimit(String host,String limt) {
//		SQLExecutor executor = getSqlExecutor();
//		String sql = getSql(SQL_UPDATE_OS);
//		Map sqlVO = getSqlVO(SQL_UPDATE_OS);
//		Map paraMap = new LinkedMap();
		List list=new ArrayList();
		Map map=new HashMap();
		map.put("Hostid", "temlite");
		map.put("Uselv", "70%");
		list.add(map);
		Map map1=new HashMap();
		map1.put("Hostid", "hostserve");
		map1.put("Uselv", "50%");
		list.add(map1);
//		Use use=new Use();
//		use.setHostid("temlite");
//		use.setUselv("70%");
//		Use use1=new Use();
//		use1.setHostid("hostserve");
//		use1.setUselv("50%");
//		list.add(use1);
//		list.add(use);
		return list;
		
	}
	
	@Override
	public IResponseEvent processException(IResponseEvent arg0,
			BusinessException arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
