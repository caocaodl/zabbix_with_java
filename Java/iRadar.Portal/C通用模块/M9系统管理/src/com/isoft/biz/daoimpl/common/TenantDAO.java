package com.isoft.biz.daoimpl.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isoft.biz.dao.NameSpaceEnum;
import com.isoft.biz.dao.common.ITenantDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.DataPage;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.server.RunParams;
import com.isoft.util.ImonUtil;
import com.isoft.utils.EncryptionUtil;

public class TenantDAO extends BaseDAO implements ITenantDAO{

    public TenantDAO(SQLExecutor sqlExecutor) {
        super(sqlExecutor);
    }
    
    private static final String SQL_PAGE = "SQL_PAGE";
    public List doTenantPage(DataPage dataPage, Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_PAGE);
        Map sqlVO = getSqlVO(SQL_PAGE);
        return executor.executeNameParaQuery(dataPage, sql, paraMap, sqlVO);
    }
    
    private static final String SQL_VIEW = "SQL_VIEW";
    public List doTenantView(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_VIEW);
        Map sqlVO = getSqlVO(SQL_VIEW);
        return executor.executeNameParaQuery(sql, paraMap, sqlVO);
    }
    
    private final static String SQL_TENANT_DUPLICATE_CHECK = "SQL_TENANT_DUPLICATE_CHECK";
    private boolean doTenantDuplicateCheck(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_TENANT_DUPLICATE_CHECK);
        return executor.executeNameParaQuery(sql, paraMap, String.class).isEmpty();
    }
    
    private static final String SQL_ADD = "SQL_ADD";
    public String[] doTenantAdd(Map paraMap) {
    	String[] ret = new String[2];
		if(!doTenantDuplicateCheck(paraMap)){
			ret[0] = null;
			ret[1] = "租户名称重复";
		} else {
	        SQLExecutor executor = getSqlExecutor();
	        String sql = getSql(SQL_ADD);
	        String id = getFlowcode(NameSpaceEnum.SYS_TENANT);
	        paraMap.put("id", id);
	        if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
	        	ret[0] = id;
				ret[1] = null;
	        }
		}
		return ret;
    }
    
    private static final String SQL_EDIT = "SQL_EDIT";
    public String[] doTenantEdit(Map paraMap) {
		String[] ret = new String[2];
		String id = (String) paraMap.get("id");
		if (StringUtils.isNotEmpty(id)) {
			if (!doTenantDuplicateCheck(paraMap)) {
				ret[0] = null;
				ret[1] = "租户名称重复";
			} else {
		        SQLExecutor executor = getSqlExecutor();
		        String sql = getSql(SQL_EDIT);
		        if(executor.executeInsertDeleteUpdate(sql, paraMap) == 1){
		        	ret[0] = id;
					ret[1] = null;
		        } else {
	    			ret[0] = null;
					ret[1] = "租户不存在或不可编辑";
	    		}
			}
		}
		return ret;
    }
    
    private static final String SQL_DEL = "SQL_DEL";
    public boolean doTenantDel(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_DEL);
        String id = (String)paraMap.get("id");
        paraMap.put("id", id);
		return executor.executeInsertDeleteUpdate(sql, paraMap) == 1;
    }
    public boolean doSaveOsTenantId(IIdentityBean identityBean, Map paraMap) {
    	SQLExecutor executor = getSqlExecutor();
    	String setOsTenantId = getSql(SQL_SET_OS_TENANT_ID);
		Map tmap = new HashMap();
		tmap.put("id", paraMap.get("tenantId"));
		tmap.put("osTenantId", paraMap.get("osTenantId"));
		if(executor.executeInsertDeleteUpdate(setOsTenantId, tmap) == 1){
			return true;
		}
		return false;
	}
    
    private static final String SQL_ACTIVE = "SQL_ACTIVE";
    private static final String SQL_INACTIVE = "SQL_INACTIVE";
    private static final String SQL_INIT_ROOT = "SQL_INIT_ROOT";
    private static final String SQL_SET_OS_TENANT_ID = "SQL_SET_OS_TENANT_ID";
    private static final String SQL_GET_TENANT_EMAIL = "SQL_GET_TENANT_EMAIL";

	public String[] doTenantActive(String tenantId, boolean createRoot) {
		String osTenantId = null;
		String[] ret = new String[2];
		if (StringUtils.isNotEmpty(tenantId)) {
//			DatabaseClient dbClient = IaasUtils.getDbClient(null, this);
//			VLanVO vlan = dbClient.vlan_allocate(tenantId);
//			if (vlan == null) {
//				ret[0] = null;
//				ret[1] = "Vlan资源不足!";
//				return ret;
//			}
			SQLExecutor executor = getSqlExecutor();
			Map paraMap = new HashMap();
			paraMap.put("id", tenantId);
			// 随机密码设置
			String randomPswd = ImonUtil.getRandomPassword(8);
			String tenantEmail = null;
			String eMailBody = null;
			paraMap.put("password", EncryptionUtil.encrypt(randomPswd));
			String activeSql = getSql(SQL_ACTIVE);
			if (executor.executeInsertDeleteUpdate(activeSql, paraMap) == 1) {
				if (createRoot) {
					String getTenantEMail = getSql(SQL_GET_TENANT_EMAIL);
					tenantEmail = (String) executor.executeNameParaQuery(
							getTenantEMail, paraMap, String.class).get(0);
					eMailBody = ImonUtil.generateMailBody((String) paraMap
							.get("id"), "root", randomPswd, RunParams.PORTAL_URI);
					
					String uid = getFlowcode(NameSpaceEnum.SYS_USER);
					paraMap.put("uid", uid);
					paraMap.put("email", tenantEmail);
					String rootSql = getSql(SQL_INIT_ROOT);
					executor.executeInsertDeleteUpdate(rootSql, paraMap);

				}
				String getOsTenantId = getSql(SQL_GET_OS_TENANT_ID);
				osTenantId = (String) executor.executeNameParaQuery(
						getOsTenantId, paraMap, String.class).get(0);
				if (StringUtils.isEmpty(osTenantId)) {
					try {
						osTenantId = ImonUtil.createOpenstackTenant(this,
								tenantId);
						Map tmap = new HashMap();
						tmap.put("id", tenantId);
						tmap.put("osTenantId", osTenantId);
						String setOsTenantId = getSql(SQL_SET_OS_TENANT_ID);
						if (executor.executeInsertDeleteUpdate(setOsTenantId,tmap) == 1) {
							if (tenantEmail != null && tenantEmail.length() > 0
									&& eMailBody != null
									&& eMailBody.length() > 0) {
								boolean sentMail = false;
								try{
									sentMail = ImonUtil.sendMail(tenantEmail, "用户激活邮件",
										eMailBody);
								}finally{
									if(!sentMail){
										ret[1] = "租户邮件地址不可用,账号信息无法送达,请确认租户信息后再激活!";
										throw new Exception();
									}
								}
							}
						}
					} catch (Exception e) {
						String inactiveSql = getSql(SQL_INACTIVE);
						executor.executeInsertDeleteUpdate(inactiveSql, paraMap);
						ImonUtil.releaseOpenstackTenant(this, tenantId,
								osTenantId);
						ret[0] = null;
						if (ret[1] == null) {
							ret[1] = "租户资源分配失败!";
						}
						return ret;
					}
				}
				ret[0] = osTenantId;
				ret[1] = null;
				return ret;
			} else {
//				if (vlan != null) {
//					dbClient.vlan_release(tenantId);
//				}
				ret[0] = null;
				ret[1] = "租户当前状态不可激活!";
				return ret;
			}
		} else {
			ret[0] = null;
			ret[1] = "租户ID不可用!";
			return ret;
		}
	}
    
	
    private static final String SQL_FORBID = "SQL_FORBID";
    public boolean doTenantForbid(String tenantId) {
        if(StringUtils.isNotEmpty(tenantId)){
        	SQLExecutor executor = getSqlExecutor();
        	String sql = getSql(SQL_FORBID);
    		Map paraMap = new HashMap();
    		paraMap.put("id", tenantId);
    		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
    			return true;
    		}
    	}
        return false;
    }
    
    private static final String SQL_RESUME = "SQL_RESUME";
    public boolean doTenantResume(String tenantId) {
        if(StringUtils.isNotEmpty(tenantId)){
        	SQLExecutor executor = getSqlExecutor();
        	String sql = getSql(SQL_RESUME);
    		Map paraMap = new HashMap();
    		paraMap.put("id", tenantId);
    		if(executor.executeInsertDeleteUpdate(sql, paraMap)==1){
    			return true;
    		}
    	}
        return false;
    }
    
    private static final String SQL_RELEASE = "SQL_RELEASE";
    private static final String SQL_GET_RELEASE_TENANT = "SQL_GET_RELEASE_TENANT";
    @SuppressWarnings("unchecked")
	public boolean doTenantRelease(String tenantId) {
        if(StringUtils.isNotEmpty(tenantId)){
    		Map paraMap = new HashMap();
    		paraMap.put("id", tenantId);
            SQLExecutor executor = getSqlExecutor();
            String sql = getSql(SQL_RELEASE);
    		int ret = executor.executeInsertDeleteUpdate(sql, paraMap);
    		if(ret == 1){
    			String rsql = getSql(SQL_GET_RELEASE_TENANT);
    			List<String> osTenantIds = executor.executeNameParaQuery(rsql, paraMap, String.class);
				for (String osTenantId : osTenantIds) {
					try{
						return ImonUtil.releaseOpenstackTenant(this, tenantId, osTenantId);
					}catch(Exception e){
					}
				}
    		}
    	}
        return false;
    }
    
    private static final String SQL_GET_OS_TENANT_ID = "SQL_GET_OS_TENANT_ID";
	public List doTenantViewByTenantId(Map paraMap) {
        SQLExecutor executor = getSqlExecutor();
        String sql = getSql(SQL_GET_OS_TENANT_ID);
        return executor.executeNameParaQuery(sql, paraMap, String.class);
    }
    
    
}
