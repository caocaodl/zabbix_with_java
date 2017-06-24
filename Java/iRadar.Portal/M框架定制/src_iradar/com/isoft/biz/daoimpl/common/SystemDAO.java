package com.isoft.biz.daoimpl.common;

import java.util.List;
import java.util.Map;
import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.ISystemDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.types.Mapper.Nest;

public class SystemDAO extends BaseDAO implements ISystemDAO{

	public SystemDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}
	
	/**
	 * 获取所有数据
	 */
	private static final String SQL_ALL_SYSTEM = "SQL_ALL_SYSTEM";
	public List doSystem(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ALL_SYSTEM);
		Map sqlVO = getSqlVO(SQL_ALL_SYSTEM);
		List list= executor.executeNameParaQuery(sql,paraMap,sqlVO);
		return list;
	}
	/**
	 * 根据机柜获取相关资产信息
	 */
	private static final String SQL_ALL_SYSTEM_INTERY = "SQL_ALL_SYSTEM_INTERY";
	public List existSystemhtintery(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ALL_SYSTEM_INTERY);
		Map sqlVO = getSqlVO(SQL_ALL_SYSTEM_INTERY);
		List list= executor.executeNameParaQuery(sql,paraMap,sqlVO);
		return list;
	}
	/**
	 * 添加数据
	 */
	private static final String SQL_ADD_SYSTEM = "SQL_ADD_SYSTEM";
	public Object[] doAdd(Map paraMap) {
		Object[] ret = new Object[2];
		if(!doSystemCheck(paraMap,"add")){
			ret[0] = null;
			ret[1] = "名称重复";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ADD_SYSTEM);
			String id = getFlowcode(NameSpaceEnum.OPERATION_DEPT);
			paraMap.put("id", id);
			paraMap.put("dkey", getFlowcode(NameSpaceEnum.OSKEY));
			if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
				ret[0] = id;
				ret[1] = "可以";
			}
		}
		return ret;
	}
	private static final String SQL_ADD_MOTOR_ROOM = "SQL_ADD_MOTOR_ROOM";
	public Object[] doAddRoom(Map paraMap) {
		Object[] ret = new Object[2];
		if(!doSystemCheck(paraMap,"add")){
			ret[0] = null;
			ret[1] = "名称重复";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ADD_MOTOR_ROOM);
			String id = getFlowcode(NameSpaceEnum.OPERATION_DEPT);
			String seq_no = getFlowcode(NameSpaceEnum.OPERATION_DEPT);
			paraMap.put("id", id);
			paraMap.put("seq_no", seq_no);
			paraMap.put("dkey", getFlowcode(NameSpaceEnum.OSKEY));
			if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
				ret[0] = id;
				ret[1] = "可以";
			}
		}
		return ret;
	}
	public Object[] doAddCatinet(Map paraMap) {
		Object[] ret = new Object[2];
		if(!doSystemCheckca(paraMap,"add")){
			ret[0] = null;
			ret[1] = "名称重复";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ADD_MOTOR_ROOM);
			String id = getFlowcode(NameSpaceEnum.OPERATION_DEPT);
			paraMap.put("id", id);
			paraMap.put("dkey", getFlowcode(NameSpaceEnum.OSKEY));
			if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
				ret[0] = id;
				ret[1] = "可以";
			}
		}
		return ret;
	}
	/**
	 * 修改数据
	 */
	private static final String SQL_UPDATE_SYSTEM = "SQL_UPDATE_SYSTEM";
	private static final String SQL_UPDATE_HOST_ROOM = "SQL_UPDATE_HOST_ROOM";
	public Object[] doUpdate(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (!doSystemCheckca(paraMap,"update")) {
			ret[0] = null;
			ret[1] = "名称重复";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_UPDATE_SYSTEM);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				if("CABINET".equals(Nest.value(paraMap, "type").asString())){
					sql = getSql(SQL_UPDATE_HOST_ROOM);
					executor.executeInsertDeleteUpdate(sql, paraMap);
				}
				ret[0] = id;
				ret[1] = "可以";
			}
		}
		return ret;
	}
	private static final String SQL_UPDATE_TWO = "SQL_UPDATE_TWO";
	public Object[] doUpdatetwo(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (!doSystemCheck(paraMap,"update")) {
			ret[0] = null;
			ret[1] = "名称重复";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_UPDATE_TWO);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {  
				ret[0] = id;
				ret[1] = "可以";
			}
		}
		return ret;
	}
	/**
	 * 删除数据
	 */
	private static final String SQL_DELETE_SYSTEM = "SQL_DELETE_SYSTEM";
	public boolean doDelete(Map paraMap) {
		boolean result = false;
		List idlist = (List) paraMap.get("oslist");
		if (idlist.size()>0) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_DELETE_SYSTEM);
			if(executor.executeInsertDeleteUpdate(sql, paraMap) != 0){
				result = true;
			}
		}
		return result;
	}

	/**
	 * 验证数据库机柜是否存在重复记录
	 */
	private final static String SQL_SYSTEM_CHECK_ADD_CA = "SQL_SYSTEM_CHECK_ADD_CA";
	private final static String SQL_SYSTEM_CHECK_UPDATE_CA = "SQL_SYSTEM_CHECK_UPDATE_CA";
    public boolean doSystemCheckca(Map paraMap,String mark) {
    	List data = null;
    	if(!mark.equals("add")){
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_SYSTEM_CHECK_UPDATE_CA);
			data = executor.executeNameParaQuery(sql, paraMap, String.class);
    	}
    	else {
    		SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_SYSTEM_CHECK_ADD_CA);
			data = executor.executeNameParaQuery(sql, paraMap, String.class);
    	}
        return data.isEmpty();
    }
	/**
	 * 验证数据库是否存在重复记录
	 */
	private final static String SQL_SYSTEM_CHECK_ADD = "SQL_SYSTEM_CHECK_ADD";
	private final static String SQL_SYSTEM_CHECK_UPDATE = "SQL_SYSTEM_CHECK_UPDATE";
    public boolean doSystemCheck(Map paraMap,String mark) {
    	List data = null;
    	if(!mark.equals("add")){
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_SYSTEM_CHECK_UPDATE);
			data = executor.executeNameParaQuery(sql, paraMap, String.class);
    	}
    	else {
    		SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_SYSTEM_CHECK_ADD);
			data = executor.executeNameParaQuery(sql, paraMap, String.class);
    	}
        return data.isEmpty();
    }
    
	private final static String SQL_SYSTEM_RELATION_GET = "SQL_SYSTEM_RELATION_GET";
    public boolean doSysRelationGet(Map paraMap) {
    	SQLExecutor executor = getSqlExecutor();
    	String sql = getSql(SQL_SYSTEM_RELATION_GET);
    	Map sqlVO = getSqlVO(SQL_SYSTEM_RELATION_GET);
    	List<Map> data = executor.executeNameParaQuery(sql, paraMap, sqlVO);
        if(data!=null && data.size()>0){
        	return true;
        }
        return false;
    }
}
