package com.isoft.biz.daoimpl.common;


import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.IAnnouncementDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;

public class AnnouncementDAO extends BaseDAO implements IAnnouncementDAO{

	public AnnouncementDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
		// TODO Auto-generated constructor stub
	}
	private static final String SQL_CONFIG = "SQL_CONFIG";
	public List doconfig() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CONFIG);
		Map sqlVO = getSqlVO(SQL_CONFIG);
		Map paraMap = new LinkedMap();
		List list= executor.executeNameParaQuery( sql,paraMap,sqlVO);
		return list;
	}
	
	private static final String SQL_LIST = "SQL_LIST";
	public List doList(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LIST);
		Map sqlVO = getSqlVO(SQL_LIST);
		//Map paraMap = new LinkedMap();
		List list= executor.executeNameParaQuery( sql,paraMap,sqlVO);
		return list;
	}
	
	
	/**  只显示已生效公告
	 * @return
	 */
	private static final String SQL_EFFECTIVE_LIST = "SQL_EFFECTIVE_LIST";
	public List doeffectiveList() {
		Map paraMap = new LinkedMap();
		return doeffectiveList(paraMap);
	}
	
	public List doeffectiveList(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_EFFECTIVE_LIST);
		Map sqlVO = getSqlVO(SQL_EFFECTIVE_LIST);
		if(paraMap.isEmpty()){
			paraMap.put("search_limit", 6);
		}
		List list= executor.executeNameParaQuery(sql,paraMap,sqlVO);
		return list;
	}
	
	public List doListAll() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LIST);
		Map sqlVO = getSqlVO(SQL_LIST);
		Map paraMap = new LinkedMap();
		return executor.executeNameParaQuery( sql,paraMap,sqlVO);
		 
	}
	private static final String SQL_START = "SQL_START";
	public boolean doStart(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_START);
		return executor.executeInsertDeleteUpdate( sql,paraMap)>0;
		
	}
	private static final String SQL_END = "SQL_END";
	public boolean doEnd(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_END);
		return executor.executeInsertDeleteUpdate( sql,paraMap)>0;
		 
	}
	private static final String SQL_LIST_ONE = "SQL_LIST_ONE";
	public List doListOne(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_LIST_ONE);
		Map sqlVO = getSqlVO(SQL_LIST_ONE);
		List list=  executor.executeNameParaQuery( sql,paraMap,sqlVO);
		return list;
	}
	private static final String SQL_UPDATA = "SQL_UPDATA";
	public boolean doUpdata(Map paraMap) {
		boolean result=true;
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_UPDATA);
		result= executor.executeInsertDeleteUpdate( sql,paraMap)>0;
		return result;
	}
	private static final String SQL_CREATE = "SQL_CREATE";
	public boolean doCreate(Map paraMap) {
		boolean result=true;
		SQLExecutor executor = getSqlExecutor();
		String id = getFlowcode(NameSpaceEnum.SYS_USER_ROLE);
		paraMap.put("announcementid", id);
		String sql = getSql(SQL_CREATE);
		result= executor.executeInsertDeleteUpdate( sql,paraMap)>0;
		return result;
	}
	private static final String SQL_DELETE = "SQL_DELETE";
	public boolean doDelete(List param) { 
		boolean result=true;
		Map paraMap =new LinkedMap();
		if (param.size()>0) {
			paraMap.put("idList", param);
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_DELETE);
			 result=executor.executeInsertDeleteUpdate(sql, paraMap)>0;
		}
		return result;
	}
	private static final String SQL_CEASE = "SQL_CEASE";
	public boolean doCease(List param) { 
		boolean result=true;
		Map paraMap =new LinkedMap();
		if (param.size()>0) {
			paraMap.put("idList", param);
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_CEASE);
			 result=executor.executeInsertDeleteUpdate(sql, paraMap)>0;
		}
		return result;
	}
	
	private static final String SQL_ENABLE = "SQL_ENABLE";
	public boolean doEnable(List param) { 
		boolean result=true;
		Map paraMap =new LinkedMap();
		if (param.size()>0) {
			paraMap.put("idList", param);
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ENABLE);
			 result=executor.executeInsertDeleteUpdate(sql, paraMap)>0;
		}
		return result;
	}
	
	private static final String SQL_CONFLICT_NAME_CHECK = "SQL_CONFLICT_NAME_CHECK";
	public boolean doConflictNameCheck(Map param) { 
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_CONFLICT_NAME_CHECK);
		Map sqlVO = getSqlVO(SQL_CONFLICT_NAME_CHECK);
		List<Map> data = executor.executeNameParaQuery(sql, param, sqlVO);
		return (!Cphp.empty(data))&&data.size()>0?true:false;
	}
}
