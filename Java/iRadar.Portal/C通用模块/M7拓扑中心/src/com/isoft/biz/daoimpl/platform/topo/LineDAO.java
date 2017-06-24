package com.isoft.biz.daoimpl.platform.topo;

import java.util.List;
import java.util.Map;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ILineDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.LineVo;
import com.isoft.framework.persistlayer.SQLExecutor;

public class LineDAO extends BaseDAO implements ILineDAO{

	public LineDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	private static String SQL_TOPO_LINE_LIST = "SQL_TOPO_LINE_LIST";
	@SuppressWarnings("unchecked")
	public List<LineVo> doLineList(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_LINE_LIST);
		return (List<LineVo>) executor.executeNameParaQuery(sql, paraMap, LineVo.class);
	}
	
	private static String SQL_TOPO_TARGET_NODE_ID_BY_NODE_ID = "SQL_TOPO_TARGET_NODE_ID_BY_NODE_ID";
	@SuppressWarnings("unchecked")
	public List<String> doTargetNodeIdByNodeId(Map<String,Object> param){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_TARGET_NODE_ID_BY_NODE_ID);
		return (List<String>) executor.executeNameParaQuery(sql, param, String.class);
	}
	
	private static final String SQL_TOPO_LINE_ADD = "SQL_TOPO_LINE_ADD";	
	public String doLineAdd(Map<String,Object> paraMap){
		if(doTNodeDuplicateCheck(paraMap)==0){
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_TOPO_LINE_ADD);
			String lineId = getFlowcode(NameSpaceEnum.T_LINE);
			paraMap.put("lineId", lineId);
			String tagName = (String)paraMap.get("tagName");
			if(tagName==null||"".equals(tagName)){
				paraMap.put("tagName", "line");
			}
			String strokeWeight = (String)paraMap.get("strokeWeight");
			if(strokeWeight==null||"".equals(strokeWeight)){
				paraMap.put("strokeWeight", "1");
			}
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				return lineId;
			}
		}
		return null;
	}
	
	private static String SQL_TOPO_LINE_DUPLICATE_CHECK = "SQL_TOPO_LINE_DUPLICATE_CHECK";
	public int doTNodeDuplicateCheck(Map<String,Object> paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_LINE_DUPLICATE_CHECK);
		return executor.executeNameParaQuery(sql, paraMap, LineVo.class).size();
	}
	
    private static final String SQL_TOPO_LINE_UPDATE_ATTR = "SQL_TOPO_LINE_UPDATE_ATTR";	
	@SuppressWarnings("rawtypes")
	public boolean doLineUpdateAttr(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_LINE_UPDATE_ATTR);
		return executor.executeInsertDeleteUpdate(sql, paraMap)!=0?true:false;
	}
	
	private static final String SQL_TOPO_LINE_UPDATE_BY_ID = "SQL_TOPO_LINE_UPDATE_BY_ID";	
	@SuppressWarnings("rawtypes")
	public boolean doLineUpdate(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_LINE_UPDATE_BY_ID);
		return executor.executeInsertDeleteUpdate(sql, paraMap)!=0?true:false;
	}
	
	private static final String SQL_TOPO_DEL_LINE_BY_NODEID_OR_TONODE = "SQL_TOPO_DEL_LINE_BY_NODEID_OR_TONODE";	
	/**
	 * 主要提供于缩略图删除线
	 * @param paraMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public int doLineDelByNodeIdOrToNode(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DEL_LINE_BY_NODEID_OR_TONODE);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_TOPO_DEL_LINE_BY_ID = "SQL_TOPO_DEL_LINE_BY_ID";	
	/**
	 * 根据ID删除line
	 * @param paraMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public int doLineDelByLineId(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DEL_LINE_BY_ID);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_TOPO_DEL_LINE_BY_TOPO_ID = "SQL_TOPO_DEL_LINE_BY_TOPO_ID";	
	@SuppressWarnings("rawtypes")
	public int doLineDelByTopoId(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DEL_LINE_BY_TOPO_ID);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
	private static final String SQL_TOPO_DEL_EXCLUDE_LINE_OF_THUMBNAIL = "SQL_TOPO_DEL_EXCLUDE_LINE_OF_THUMBNAIL";	
	@SuppressWarnings("rawtypes")
	public int doDelExcludeLineOfThumbnail(Map paraMap){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DEL_EXCLUDE_LINE_OF_THUMBNAIL);
		return executor.executeInsertDeleteUpdate(sql, paraMap);
	}
}
