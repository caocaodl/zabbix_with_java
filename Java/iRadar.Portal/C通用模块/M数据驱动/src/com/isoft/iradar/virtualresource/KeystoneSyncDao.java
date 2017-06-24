package com.isoft.iradar.virtualresource;

import static com.isoft.biz.daoimpl.radar.CUserDAO.addDefaultRights;
import static com.isoft.biz.daoimpl.radar.CUserDAO.addUser;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.types.CArray.map;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.Feature;
import com.isoft.biz.Delegator;
import com.isoft.biz.method.Role;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.util.StringUtil;
import com.isoft.web.CDelegator;

public class KeystoneSyncDao {
	
	private <T> T delegate(Delegator<T> d) {
		return delegate(Feature.defaultTenantId, d);
	}
	
	private <T> T delegate(String tenantId, Delegator<T> d) {
		try {
			if(RadarContext.getContext() == null) {
	    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
	    		RadarContext.setContext(ctx);
	    		
	    		Map userdata = EasyMap.build(
					"userid", 0L, 
					"type", Defines.USER_TYPE_SUPER_ADMIN
				);
	    		CWebUser.set(userdata);
	    	}
			
			Map uinfo = new HashMap();
			uinfo.put("tenantId", tenantId);
			uinfo.put("osTenantId", "0");
			uinfo.put("tenantRole", Role.LESSOR.magic());
			uinfo.put("userId", Feature.defaultUser);
			uinfo.put("userName", Feature.defaultUser);
			uinfo.put("admin", "Y");
			uinfo.put("osUser", null);
			
			IdentityBean idBean = new IdentityBean();
			idBean.init(uinfo);
	    	return CDelegator.doDelegate(idBean, d);
		} finally {
			RadarContext.releaseContext();
		}
	}
	
	private final static String SQL_LIST_TENANTS = "select id,tenantid,name,parent,status,proxy_hostid,loadfactor,enabled from tenants where tenantid<>'-'";
    public List<Map> listTenants() {
    	return delegate(new Delegator<List<Map>>() {
			@Override public List<Map> doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map propMap = new HashMap();
				propMap.put("id", BigInteger.class);
				propMap.put("tenantid", String.class);
				propMap.put("name", String.class);
				propMap.put("parent", String.class);
				propMap.put("status", Integer.class);
				propMap.put("proxy_hostid", BigInteger.class);
				propMap.put("loadfactor", Integer.class);
				propMap.put("enabled", Integer.class);
				return  executor.executeNameParaQuery(SQL_LIST_TENANTS, new HashMap(), propMap);
			}
		});
    }
    
    private final static String SQL_UPDATE_TENANT = "UPDATE tenants SET name=#{name},enabled=#{enabled} WHERE tenantid=#{tenantid}";
    private final static String SQL_CREATE_TENANT = "insert into tenants(id,tenantid,name,parent,status,proxy_hostid,loadfactor,enabled) values(#{id},#{tenantid},#{name},#{parent},#{status},#{proxy_hostid},#{loadfactor},#{enabled})";
    private final static String SQL_GET_TENANTID = "SELECT max(id) FROM tenants";
	public boolean syncTenants(final List<Map> creates, final  List<Map> updates) {
    	return delegate(new Delegator<Boolean>() {
			@Override public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				for(Map tenant : updates){
					executor.executeInsertDeleteUpdate(SQL_UPDATE_TENANT, tenant);
				}
				
				if (creates != null && !creates.isEmpty()) {
					long tenantid = 0;
					List<Long> ids = executor.executeNameParaQuery(SQL_GET_TENANTID, new HashMap(), Long.class);
					if (!ids.isEmpty()) {
						tenantid = ids.get(0);
					}
					for(Map tenant : creates){
						tenant.put("id", ++tenantid);
						executor.executeInsertDeleteUpdate(SQL_CREATE_TENANT, tenant);
						
						//添加rights表（同时，在添加新的设备类型时，也是添加运营商的权限）
						CArray rights = DBselect(executor, "select 1 from rights where tenantid = #{tenantid}", map("tenantid", tenantid));
						if(empty(rights)) {
							CArray<Map> groups = DBselect(executor, "select groupid from groups");
							rights = CArray.array();
							for(Map group: groups) {
								rights.add(map(
									"tenantid", tenantid,
									"groupid", Nest.value(group, "groupid").$()
								));
							}
							addDefaultRights(executor, rights);
						}
					}
				}
				return true;
			}
		});
    }
	
	private final static String SQL_LIST_USERS = "select tenantid,userid,alias,name,enabled from users";
    public List<Map> listUsers() {
    	return delegate(new Delegator<List<Map>>() {
			@Override public List<Map> doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				Map propMap = new HashMap();
				propMap.put("tenantid", String.class);
				propMap.put("userid", String.class);
				propMap.put("name", String.class);
				propMap.put("alias", String.class);
				propMap.put("parent", String.class);
				propMap.put("enabled", Integer.class);
				return  executor.executeNameParaQuery(SQL_LIST_USERS, new HashMap(), propMap);
			}
		});
    }
    
    private final static String SQL_UPDATE_USER = "UPDATE users SET alias=#{alias},name=#{alias},enabled=#{enabled} WHERE tenantid=#{tenantid} AND userid=#{userid}";
	public boolean syncUsers(final List<Map> creates, final  List<Map> updates) {
    	return delegate(new Delegator<Boolean>() {
			@Override public Boolean doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception {
				for(Map user : updates){
					executor.executeInsertDeleteUpdate(SQL_UPDATE_USER, user);
				}
				for(Map user : creates){
					String name = Nest.value(user, "name").asString();
					String tenantid = Nest.value(user, "tenantid").asString();
					String userid = Nest.value(user, "userid").asString();
					if(Feature.defaultUser.equals(name)) {
						continue;
					}
					if(StringUtil.isEmpty(tenantid) || StringUtil.isEmpty(userid)) {
						continue;
					}
					addUser(executor, 
						Nest.value(user, "alias").asString(),
						tenantid,
						userid,
						name,
						Nest.value(user, "enabled").asInteger()==1
					);
				}
				return true;
			}
		});
    }
    
}
