package com.isoft.biz.daoimpl.platform.topo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.platform.topo.ITopoDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.biz.vo.platform.topo.Topo;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TopoDAO extends BaseDAO implements ITopoDAO {

	public TopoDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private String SQL_TOPO_LIST = "SQL_TOPO_LIST";
	@SuppressWarnings("unchecked")
	public List<Topo> doTopoList(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_LIST);
		if(Cphp.isset(paraMap, "topoName")){
			String topoName = TopoUtil.doSpecialCharacters(Nest.as(paraMap.get("topoName")).asString());
			Nest.value(paraMap, "topoName").$(topoName);
		}
		return (List<Topo>) executor.executeNameParaQuery(sql, paraMap,Topo.class);
	}
	
	private String SQL_TOPO_DATA_SELECT = "SQL_TOPO_DATA_SELECT";
	@SuppressWarnings("unchecked")
	public Map doTopoDataSelect(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DATA_SELECT);
		Map sqlVO = getSqlVO(SQL_TOPO_DATA_SELECT);
		List<Map> dataList = executor.executeNameParaQuery(sql, paraMap,sqlVO);
		return dataList.get(0);
	}
	
	private static final String SQL_TOPO_ADD = "SQL_TOPO_ADD";
	public String[] doTopoAdd(Map<String, Object> paraMap) {
		String[] ret = new String[2];
		if(doTopoDuplicateCheck(paraMap)!= 0){
			ret[0] = null;
			ret[1] = "duplicateName";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_TOPO_ADD);
			String id = getFlowcode(NameSpaceEnum.T_TOPO);
			paraMap.put("id", id);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				ret[0] = id;
				ret[1] = null;
			}
		}
		return ret;
	}

	private static String SQL_TOPO_DUPLICATE_CHECK = "SQL_TOPO_DUPLICATE_CHECK";
	public int doTopoDuplicateCheck(Map<String, Object> paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DUPLICATE_CHECK);
		return executor.executeNameParaQuery(sql, paraMap, String.class).size();
	}
	
	
	private static String SQL_TOPO_QUERY_ISPUBLIC = "SQL_TOPO_QUERY_ISPUBLIC";
	/**
	 * 根据拓扑ID查看拓扑是否公开
	 * @param topoId
	 * @return
	 */
	public String doQueryIsPublic(String topoId){
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_QUERY_ISPUBLIC);
		Map paramap=new HashMap();
		paramap.put("topoId", topoId);
		List list = executor.executeNameParaQuery(sql,paramap,String.class);
		if(list != null && list.size() != 0){
			return list.get(0).toString();
		}else{
			return null;
		}
	}
		
	private static final String SQL_TOPO_DEL = "SQL_TOPO_DEL";
	@SuppressWarnings("rawtypes")
	public boolean doTopoDel(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_DEL);
		return executor.executeInsertDeleteUpdate(sql, param) != 0;
	}

	private static final String SQL_TOPO_UPDATE = "SQL_TOPO_UPDATE";
	public String[] doTopoUpdate(Map<String, Object> paraMap) {
		String[] ret = new String[2];
		int du = doTopoDuplicateCheck(paraMap);
		if(du!= 0){
			ret[0] = null;
			ret[1] = "duplicateName";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_TOPO_UPDATE);
			String id = (String) paraMap.get("id");
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				ret[0] = id;
				ret[1] = null;
			}
		}
		return ret;
	}
	
	private static final String SQL_TOPO_PUBLIC = "SQL_TOPO_PUBLIC";
	@SuppressWarnings("rawtypes")
	public boolean doTopoPublic(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_PUBLIC);
		return executor.executeInsertDeleteUpdate(sql, param) != 0;
	}
	
	private static final String SQL_TOPO_HIDE = "SQL_TOPO_HIDE";
	@SuppressWarnings("rawtypes")
	public boolean doTopoHide(Map param) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TOPO_HIDE);
		return executor.executeInsertDeleteUpdate(sql, param) != 0;
	}

}
