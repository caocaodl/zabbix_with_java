package com.isoft.biz.daoimpl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.IUserDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.server.RunParams;
import com.isoft.utils.EncryptionUtil;

public class UserDAO extends BaseDAO implements IUserDAO{

    public UserDAO(SQLExecutor sqlExecutor) {
        super(sqlExecutor);
    }
    
    private static final String SQL_PAGE = "SQL_USER_PAGE";
    public List doUserPage(DataPage dataPage, Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_PAGE);
        Map sqlVO = getSqlVO(SQL_PAGE);
        return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
    }
    
    private static final String SQL_VIEW = "SQL_USER_VIEW";
    public List doUserView(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_VIEW);
        Map sqlVO = getSqlVO(SQL_VIEW);
        return executor.executeNameParaQuery(sql, paraMap, sqlVO);
    }
    
    private static final String SQL_ADD = "SQL_USER_ADD";
    public String[] doUserAdd(Map paraMap) {
    	String[] ret = new String[2];
		if(!doUserDuplicateCheck(paraMap)){
			ret[0] = null;
			ret[1] = "用户名称重复";
		} else {
	        SQLExecutor executor = getSqlExecutor();
	        String sql = getSql(SQL_ADD);
	        String id = getFlowcode(NameSpaceEnum.SYS_USER);
	        paraMap.put("id", id);
	        if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
	        	ret[0] = id;
				ret[1] = null;
	        }
		}
		return ret;
    }
    
    private static final String SQL_EDIT = "SQL_USER_EDIT";
    public String[] doUserEdit(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			if (!doUserDuplicateCheck(paraMap)) {
				ret[0] = null;
				ret[1] = "用户名称重复";
			} else {
		        SQLExecutor executor = getSqlExecutor();
		        String sql = getSql(SQL_EDIT);
		        if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
		        	ret[0] = id;
					ret[1] = null;
		        } else {
	    			ret[0] = null;
					ret[1] = "用户不存在或不可编辑";
	    		}
			}
		}
		return ret;
    }
    
    private static final String SQL_DEL = "SQL_USER_DEL";
    private static final String SQL_DEL_ROLE_BY_USER = "SQL_DEL_ROLE_BY_USER";
    public String[] doUserDel(Map paraMap) {
    	String[] ret = new String[2];
    	String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
	        String sql = getSql(SQL_DEL);
	        if(StringUtils.isNotEmpty(id)){
	    		paraMap.put("id", id);
	    		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
	    			sql = getSql(SQL_DEL_ROLE_BY_USER);
	    			executor.executeInsertDeleteUpdate(sql, paraMap);
	    			ret[0] = id;
					ret[1] = null;
	    		} else {
	    			ret[0] = null;
					ret[1] = "用户不存在或不可删除";
	    		}
	    	}
		}
        return ret;
    }
    
    private static final String SQL_ACTIVE = "SQL_USER_ACTIVE";
    public String[] doUserActive(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");

		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
			String sql = getSql(SQL_ACTIVE);
			String randomPswd = "abc123";
			paraMap.put("password", EncryptionUtil.encrypt(randomPswd));
			if (executor.executeInsertDeleteUpdate(sql, paraMap) == 1) {
				ret[0] = id;
				ret[1] = "用户激活成功！";
			} else {
				ret[0] = null;
				ret[1] = "用户不存在或不可激活";
			}
		}
        return ret;
    }
    
    private static final String SQL_FORBID = "SQL_USER_FORBID";
    public String[] doUserForbid(Map paraMap) {
    	String[] ret = new String[2];
    	String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
	        String sql = getSql(SQL_FORBID);
	        if(StringUtils.isNotEmpty(id)){
	    		paraMap.put("id", id);
	    		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
	    			ret[0] = id;
					ret[1] = null;
	    		} else {
	    			ret[0] = null;
					ret[1] = "用户不存在或不可禁用";
	    		}
	    	}
		}
        return ret;
    }
    
    private static final String SQL_RESUME = "SQL_USER_RESUME";
    public String[] doUserResume(Map paraMap) {
    	String[] ret = new String[2];
    	String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			SQLExecutor executor = getSqlExecutor();
	        String sql = getSql(SQL_RESUME);
	        if(StringUtils.isNotEmpty(id)){
	    		paraMap.put("id", id);
	    		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
	    			ret[0] = id;
					ret[1] = null;
	    		} else {
	    			ret[0] = null;
					ret[1] = "用户不存在或不可启用";
	    		}
	    	}
		}
        return ret;
    }
    
    private final static String SQL_USER_DUPLICATE_CHECK = "SQL_USER_DUPLICATE_CHECK";
    private boolean doUserDuplicateCheck(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_USER_DUPLICATE_CHECK);
        List data = executor.executeNameParaQuery(sql, paraMap, String.class);
        return data.isEmpty();
    }
    
	private static final String SQL_ROLE_GET_ROLES = "SQL_ROLE_GET_ROLES";
	public List<String> getRoles(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_GET_ROLES);
		return executor.executeNameParaQuery(sql, paraMap, String.class);
	}
    
    private static final String SQL_ROLE_GET_ALL_ROLE_SET = "SQL_ROLE_GET_ALL_ROLE_SET";
    public List getAllRoleSet(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_GET_ALL_ROLE_SET);
		Map sqlVO = getSqlVO(SQL_ROLE_GET_ALL_ROLE_SET);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}
    
	private static final String SQL_ROLE_GRANT_ROLES = "SQL_ROLE_GRANT_ROLES";
	public void doGrantRoles(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_GRANT_ROLES);
		String[] roleIds = (String[]) paraMap.get("roleIds");
		if (roleIds != null && roleIds.length > 0) {
			Map para = new HashMap(4);
			para.put("uid", paraMap.get("uid"));
			for (String roleId : roleIds) {
				String id = getFlowcode(NameSpaceEnum.SYS_USER_ROLE);
				para.put("id", id);
				para.put("roleId", roleId);
				executor.executeInsertDeleteUpdate(sql, para);
			}
		}
	}
	
	private static final String SQL_ROLE_DELETE_ROLES = "SQL_ROLE_DELETE_ROLES";
	public void doDeleteRoles(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_ROLE_DELETE_ROLES);
		executor.executeInsertDeleteUpdate(sql, paraMap);
	}
	
    private static final String SQL_VIEW_BY_USERNAME = "SQL_USER_VIEW_BY_USERNAME";
	public List doUserViewForUserName(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_VIEW_BY_USERNAME);
        Map sqlVO = getSqlVO(SQL_VIEW_BY_USERNAME);
        return executor.executeNameParaQuery(sql, paraMap, sqlVO);
    }
    
}
