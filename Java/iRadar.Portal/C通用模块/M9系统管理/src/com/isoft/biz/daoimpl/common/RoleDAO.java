package com.isoft.biz.daoimpl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.IRoleDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;

public class RoleDAO extends BaseDAO implements IRoleDAO {

	public RoleDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_ROLE_PAGE = "SQL_ROLE_PAGE";
	public List doRolePage(DataPage dataPage, Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_PAGE);
		Map sqlVO = getSqlVO(SQL_ROLE_PAGE);
		return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
	}

	private static final String SQL_ROLE_ADD = "SQL_ROLE_ADD";
	public String[] doRoleAdd(Map paraMap) {
		String[] ret = new String[2];
		if(!doRoleDuplicateCheck(paraMap)){
			ret[0] = null;
			ret[1] = "角色名称重复";
		} else {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ROLE_ADD);
			String id = getFlowcode(NameSpaceEnum.SYS_USER_ROLE);
			paraMap.put("id", id);
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				ret[0] = id;
				ret[1] = null;
			}
		}
		return ret;
	}

	private static final String SQL_ROLE_EDIT = "SQL_ROLE_EDIT";
	public String[] doRoleEdit(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			if (!doRoleDuplicateCheck(paraMap)) {
				ret[0] = null;
				ret[1] = "角色名称重复";
			} else {
				SQLExecutor executor = getSqlExecutor();
				String sql = getSql(SQL_ROLE_EDIT);
				if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
					ret[0] = id;
					ret[1] = null;
				}
			}
		}
		return ret;
	}

	private static final String SQL_ROLE_USED_CNT = "SQL_ROLE_USED_CNT";
	private static final String SQL_ROLE_DEL = "SQL_ROLE_DEL";
	public String[] doRoleDel(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ROLE_USED_CNT);
			List<Integer> data = executor.executeNameParaQuery(sql, paraMap, Integer.class);
			if(!data.isEmpty() && data.get(0)>0){
				ret[0] = null;
				ret[1] = "角色被使用";
			} else {
				sql = getSql(SQL_ROLE_DEL);
				if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
					ret[0] = id;
					ret[1] = null;
				} else {
	    			ret[0] = null;
					ret[1] = "角色不存在或不可编辑";
	    		}
			}
		}
		return ret;
	}
	
	private final static String SQL_ROLE_DUPLICATE_CHECK = "SQL_ROLE_DUPLICATE_CHECK";
    public boolean doRoleDuplicateCheck(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_ROLE_DUPLICATE_CHECK);
        List data = executor.executeNameParaQuery(sql, paraMap, String.class);
        return data.isEmpty();
    }

	private static final String SQL_ROLE_GET_FUNCS = "SQL_ROLE_GET_FUNCS";
	public List<String> getFuncs(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_GET_FUNCS);
		return executor.executeNameParaQuery(sql, paraMap, String.class);
	}
	
	private static final String SQL_ROLE_GET_ALL_FUNC_SET = "SQL_ROLE_GET_ALL_FUNC_SET";
	public List getAllFuncSet(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_GET_ALL_FUNC_SET);
		Map sqlVO = getSqlVO(SQL_ROLE_GET_ALL_FUNC_SET);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
	
	private static final String SQL_ROLE_GRANT_FUNCS = "SQL_ROLE_GRANT_FUNCS";
	public void doGrantFuncs(Map paraMap) {
		String[] funcIds = (String[]) paraMap.get("funcIds");
		if (funcIds != null && funcIds.length > 0) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ROLE_GRANT_FUNCS);
			Map para = new HashMap(4);
			para.put("roleId", paraMap.get("roleId"));
			for (String funcId : funcIds) {
				String id = getFlowcode(NameSpaceEnum.SYS_USER_ROLE_FUNC);
				para.put("id", id);
				para.put("funcId", funcId);
				executor.executeInsertDeleteUpdate(sql, para);
			}
		}
	}
	
	private static final String SQL_ROLE_DELETE_FUNCS = "SQL_ROLE_DELETE_FUNCS";
	public void doDeleteFuncs(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_DELETE_FUNCS);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
}
