package com.isoft.biz.daoimpl.common;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;

import com.isoft.biz.dao.common.IProfDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.server.RunParams;
import com.isoft.utils.EncryptionUtil;
import com.isoft.utils.U4aUtil;

public class ProfDAO extends BaseDAO implements IProfDAO {

	public ProfDAO(SQLExecutor sqlExecutor) {
		super(sqlExecutor);
	}

	private static final String SQL_PROF_VIEW = "SQL_PROF_VIEW";

	public List doProfView() {
		SQLExecutor executor = getSqlExecutor();
		Map paraMap = new LinkedMap();
		String sql = getSql(SQL_PROF_VIEW);
		Map sqlVO = getSqlVO(SQL_PROF_VIEW);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}

	private static final String SQL_PROF_EDIT = "SQL_PROF_EDIT";

	public boolean doProfEdit(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PROF_EDIT);
		return executor.executeInsertDeleteUpdate(sql, paraMap) > 0;
	}

	private static final String SQL_PROF_GET_PSWD = "SQL_PROF_GET_PSWD";

	public String getProfPswd() {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PROF_GET_PSWD);
		Map paraMap = new LinkedMap();
		List dataList = executor.executeNameParaQuery(sql, paraMap,
				String.class);
		return dataList.isEmpty() ? null : (String) dataList.get(0);
	}

	private static final String SQL_PROF_CHANGE_PSWD = "SQL_PROF_CHANGE_PSWD";

	public boolean doProfChangePswd(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_PROF_CHANGE_PSWD);
		return executor.executeInsertDeleteUpdate(sql, paraMap) > 0;
	}
	
	private static final String SQL_TENANT_VIEW = "SQL_TENANT_VIEW";
	
	public List doTenantView() {
		SQLExecutor executor = getSqlExecutor();
		Map paraMap = new LinkedMap();
		String sql = getSql(SQL_TENANT_VIEW);
		Map sqlVO = getSqlVO(SQL_TENANT_VIEW);
		return executor.executeNameParaQuery(sql, paraMap, sqlVO);
	}

	private static final String SQL_TENANT_EDIT = "SQL_TENANT_EDIT";

	public boolean doTenantEdit(Map paraMap) {
		SQLExecutor executor = getSqlExecutor();
		String sql = getSql(SQL_TENANT_EDIT);
		return executor.executeInsertDeleteUpdate(sql, paraMap) > 0;
	}
	
	private static final String SQL_PROF_BY_TID_USERNAME = "SQL_PROF_BY_TID_USERNAME";
	public String[] doPwdReset(Map paraMap) {
		String[] result = new String[2];
    	result[0] = null;
    	result[1] = "";
		SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_PROF_BY_TID_USERNAME);
        Map sqlVO = getSqlVO(SQL_PROF_BY_TID_USERNAME);
        List<Map> userList = executor.executeNameParaQuery(sql, paraMap, sqlVO);
        if(userList.size() == 0){
        	result[1] = "企业账号或用户名称错误";
        }else if(userList.size() == 1){
        	String userEmail = (String) userList.get(0).get("email");
        	String inputEmail = (String) paraMap.get("email");
        	if(!userEmail.equals(inputEmail)){
            	result[1] = "邮箱地址错误";
        	}else if(!"Y".equals((String) userList.get(0).get("status"))){
            	result[1] = "用户未激活或已禁用";
        	}else{
        		paraMap.put("userId", (String) userList.get(0).get("id"));
    			String randomPswd = U4aUtil.getRandomPassword(8);
    			paraMap.put("pswd", EncryptionUtil.encrypt(randomPswd));
    			sql = getSql(SQL_PROF_CHANGE_PSWD);
    			if(executor.executeInsertDeleteUpdate(sql, paraMap) > 0){
					String emailBody = U4aUtil.generateMailBody((String) userList.get(0).get("tenantId"), 
							(String) userList.get(0).get("name"), randomPswd, RunParams.PORTAL_URI);
					try {
						U4aUtil.sendMail(userEmail, "密码找回邮件", emailBody);
				    	result[0] = (String) userList.get(0).get("id");
					} catch (Exception e) {
				    	result[0] = null;
					}
    			}
        	}
        }
		return result;
	}

}
