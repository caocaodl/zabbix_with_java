package com.isoft.biz.daoimpl.platform.topo;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ILineDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.TBizLine;
import com.isoft.framework.persistlayer.SQLExecutor;

public class BizLineDAO extends BaseDAO implements ILineDAO{

	public BizLineDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static String SQL_T_BIZ_LINE_LIST = "SQL_T_BIZ_LINE_LIST";
	@SuppressWarnings("unchecked")
	public List<TBizLine> doBizLineList(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_LINE_LIST);
		return (List<TBizLine>) executor.executeNameParaQuery(sql, paraMap, TBizLine.class);
	}
	
	private static final String SQL_T_BIZ_LINE_ADD = "SQL_T_BIZ_LINE_ADD";

	public String doLineAdd(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_LINE_ADD);
		String lineId = getFlowcode(NameSpaceEnum.T_BIZ_LINE);
		paraMap.put("lineId", lineId);
		String tagName = (String) paraMap.get("tagName");
		if (tagName == null || "".equals(tagName)) {
			paraMap.put("tagName", "line");
		}
		String strokeWeight = (String) paraMap.get("strokeWeight");
		if (strokeWeight == null || "".equals(strokeWeight)) {
			paraMap.put("strokeWeight", "1");
		}
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			return lineId;
		}
		return null;
	}
	
	private static final String SQL_T_BIZ_LINE_DEL_BY_ID = "SQL_T_BIZ_LINE_DEL_BY_ID";	
	@SuppressWarnings("rawtypes")
	public int doLineDelByLineId(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_LINE_DEL_BY_ID);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_T_BIZ_LINE_DEL_BY_TOPO_ID = "SQL_T_BIZ_LINE_DEL_BY_TOPO_ID";	
	@SuppressWarnings("rawtypes")
	public int doLineDelByTopoId(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_T_BIZ_LINE_DEL_BY_TOPO_ID);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}

}
