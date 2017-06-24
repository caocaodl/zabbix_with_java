package com.isoft.biz.daoimpl.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.common.IHostTypeDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.types.Mapper.Nest;

public class HostTypeDAO extends BaseDAO implements IHostTypeDAO{

	public HostTypeDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_ADD = "SQL_ADD";
	public Object[] doAdd(Map paraMap) {
		Object[] ret = new Object[2];
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ADD);
		String id =  (String) paraMap.get("id");
		paraMap.put("seqNo", getSeqNo(paraMap));
		if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
			ret[0] = id;
			Map pm = new HashMap();
			pm.put("id", id);
			ret[1] = doId(pm);
			
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

	private static final String SQL_DELETE = "SQL_DELETE";
	public void doDelete(Map paraMap) {
		List idList = (List) paraMap.get("idList");
		if (idList.size()>0) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_DELETE);
			executor.executeInsertDeleteUpdate(sql, paraMap);
		}
	}
	
	/**
	 * 获得系统管理子菜单下最大序列号
	 */
	private static final String SQL_SELECT_MAX_SEQNO = "SQL_SELECT_MAX_SEQNO";
	public List doMaxSeqNo(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_SELECT_MAX_SEQNO);
		Map sqlVO = getSqlVO(SQL_SELECT_MAX_SEQNO);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	/**
	 * 设置菜单序列号
	 */
	public String getSeqNo(Map paraMap){
		List dataList = doMaxSeqNo(paraMap);
		if(dataList.size()>0){
			Map data = (Map) dataList.get(0);
			if(!Cphp.empty(Nest.value(data, "seqNo").$())){
				Integer i = Integer.parseInt(data.get("seqNo").toString())+1;
				return i.toString();
			}else
				return "1000001";
		}else{
			return "1000001";
		}
	}
	
	/**
	 * 根据id查询记录
	 */
	private static final String SQL_ID = "SQL_ID";
	public List doId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ID);
		Map sqlVO = getSqlVO(SQL_ID);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}	
	
	/**
	 * 根据设备类型的id查询对应的默认监控模型
	 */	
	private static final String SQL_GROUPID = "SQL_GROUPID";
	public List<Map> doGroupId(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_GROUPID);
		Map sqlVO = getSqlVO(SQL_GROUPID);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	/**
	 * 增加设备类型对应的监控模型
	 */
	private static final String SQL_ADD_GROUP_TEMPLATES = "SQL_ADD_GROUP_TEMPLATES";
	public void doAddGroupTemplates(List<Map> gtList) {
		Object[] ret = new Object[2];
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ADD_GROUP_TEMPLATES);
		for(Map paraMap : gtList){
			executor.executeInsertDeleteUpdate(sql, paraMap);
		}
	}	
	
	/**
	 * 修改设备类型对应的监控模型
	 */
	public void doUpdateGroupTemplates(Long groupid, List<Map> gtList){
		List groupidList = new ArrayList();
		Map paraMap = new HashMap();
		groupidList.add(groupid);
		paraMap.put("groupidList", groupidList);
		//删除
		doDeleteGroupTemplates(paraMap);
		//插入
		doAddGroupTemplates(gtList);
	}
	
	
	/**
	 * 删除设备类型对应的监控模型
	 */
	private static final String SQL_DELETE_GROUP_TEMPLATES = "SQL_DELETE_GROUP_TEMPLATES";
	public void doDeleteGroupTemplates(Map paraMap) {
		List idList = (List) paraMap.get("groupidList");
		if (idList.size()>0) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_DELETE_GROUP_TEMPLATES);
			executor.executeInsertDeleteUpdate(sql, paraMap);
		}
	}	
	
}
