package com.isoft.biz.daoimpl.radar;

import static com.isoft.iaas.openstack.OpsUtils.getOpenStackClientForAdmin;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.Defines.GROUP_DEBUG_MODE_ENABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_ATTEMPTS;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_BLOCK;
import static com.isoft.iradar.inc.Defines.RDA_SESSION_ACTIVE;
import static com.isoft.iradar.inc.MD5Util.MD5;
import static com.isoft.iradar.inc.PermUtil.check_perm2system;
import static com.isoft.types.CArray.map;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iaas.openstack.Configuration;
import com.isoft.iaas.openstack.IaaSClient;
import com.isoft.iaas.openstack.base.client.OpenStackResponseException;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Access.User.Role;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.Token;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.data.DataDriver;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.model.schema.DBSchema;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class CUserDAO extends CUserGeneralDAO {
	
	public final static String KEY_LOGIN = "com.isoft.biz.daoimpl.radar.CUserDAO.KEY_LOGIN";
	public final static int VALUE_LOGIN_UNROLE = -10;
	
	private static CArray<User> OS_USERS = new CArray();

	public CUserDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
	}
	
	@Override
	protected boolean isStickTenantResource(){
		return true;
	}
	
	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		String pkFieldId = this.fieldId("userid", tableAlias);
		// count
		if (!is_null(params.getCountOutput())) {
			sqlParts.select.clear();
			sqlParts.select.put("COUNT(DISTINCT "+pkFieldId+") AS rowscount");
			// select columns used by group count
			if (!is_null(params.getGroupCount())) {
				Set<Entry<String, String>> set = sqlParts.group.namedMap.entrySet();
				for(Entry<String, String> entry : set){
					sqlParts.select.put(entry.getKey(),entry.getValue());
				}
			}
		}
		// custom output
		else if(isArray(params.getOutput())){
			// the pk field must always be included for the API to work properly
			sqlParts.select.clear();
			sqlParts.select.put(pkFieldId);
			
			String[] fields = CArray.valueOf(params.getOutput()).valuesAsString();
			if (fields.length > 0) {
				DBSchema schema = getSqlExecutor().executeSchemaQuery(tableName);
				for (String field : fields) {
					if(schema.containsField(field)){
						sqlParts.select.put(this.fieldId(field, tableAlias));
					}
				}
			}
			sqlParts.select.arrayUnique();
		}
		// extended output
		else if(API_OUTPUT_EXTEND.equals(params.getOutput())){
			// TODO: API_OUTPUT_EXTEND must return ONLY the fields from the base table
			this.addQuerySelect(this.fieldId("*", tableAlias), sqlParts);
		}
	}
	
	/**
	 * 给设备类型添加默认的权限（所有用户都有）
	 * 
	 * @param executor
	 * @param rights 权限信息，需要以下字段
	 * 		tenantid 	租户ID
	 * 		groupid		设备类型ID
	 * @return
	 */
	public static boolean addDefaultRights(SQLExecutor executor, CArray<Map> rights) {
		Long id = DBUtil.get_dbid(null, executor, "rights", "rightid", rights.size());
		for(Map right: rights) {
			int result = executor.executeInsertDeleteUpdate(
				"INSERT INTO `rights` (tenantid, rightid, groupid, permission, id) VALUES (" +
					"#{tenantid}, #{rightid}, #{usergroupid}, #{permission}, #{groupid}"+
				")", 
				map(
					"tenantid", Nest.value(right, "tenantid").$(),
					"rightid", id++,
					"usergroupid", Feature.defaultOsUserGroup,
					"permission", Defines.PERM_READ_WRITE,
					"groupid", Nest.value(right, "groupid").$()
				)
			);
			if(result == 0) {
				return false;
			}
		}
		return true;
	}
	public static boolean removeDefaultRights(SQLExecutor executor, CArray<Long> groupids) {
		executor.executeInsertDeleteUpdate(
			"delete from rights where id IN(#foreach($var in $list)$var #if($velocityCount<$list.size()),#end #end)", 
			map(
				"list", groupids.toList()
			)
		);
		return true;
	}
	
	private final static String KEY_NEED_RELOAD_USERS = "KEY_RELOAD_USERS";
	@Override
	public Object login(Map<String, Object> user) {
		String username = Nest.value(user,"user").asString(true);
		String password = Nest.value(user,"password").asString(true);
		String tenantId = null;
		boolean loginSuccess = false;
		Tenant osTenant  = null;
		String osTokenId = null;
		String osUserId = null;
		String osUserName = null;
		String osUserUserName = null;
		
		if( !"C".equals(Feature.codeLogin)) {
			loginSuccess = true;
			osTokenId = ""+System.currentTimeMillis();
			
			if(Feature.codeLogin.equals("T")) {
				tenantId = "4d33a2df8e2541b2a5057ec620fe8e82";
				osUserId = "41ff8a66b7b64f40914e2e15fe6c33aa";
				username = osUserName = osUserUserName = "user1";
			}else {
				tenantId = Feature.defaultTenantId;
				osUserId = "1";
				username = osUserName = osUserUserName = Feature.defaultUser;
			}
		}else {
			int retryCount = Nest.value(user, KEY_NEED_RELOAD_USERS).asInteger();
			try {
				tenantId = getOsTenantIdByUsername(username, retryCount==1);
				if (!isEmpty(tenantId)) {
					retryCount++;
					
					Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
					Access access = keystone.tokens()
							.authenticate(new UsernamePassword(username, password))
							.withTenantId(tenantId).execute();
					Token osToken = access.getToken();
					osTenant = osToken.getTenant();
					Access.User osUser = access.getUser();
					
					List<Role> roles = osUser.getRoles();
					if(roles != null) {
						for(Role role: roles) {
							String name = role.getName();
							if(IMonConsts.ROLE_LESSOR_MANAGER.equals(name) ||
									IMonConsts.ROLE_TENANT_MANAGER.equals(name)
								) {
								loginSuccess = true;
								break;
							}
						}
					}
					
					if(loginSuccess) {
						osUserId = osUser.getId();
						osUserName = osUser.getName();
						osUserUserName = osUser.getUsername();
						osTokenId = osToken.getId();
						
						loginSuccess = true;
					}else {
						RadarContext.getContext().define(KEY_LOGIN, VALUE_LOGIN_UNROLE);
						loginSuccess = false;
					}
				} else {
					loginSuccess = false;
				}
			} catch (OpenStackResponseException e) {
				loginSuccess = false;
				if(retryCount == 1) {
					user.put(KEY_NEED_RELOAD_USERS, retryCount);
					return login(user);
				}
			}
		}
		
		
		//FIXME: 修复tenantId不随用户改变的BUG
//		tenantId = this.idBean.getTenantId();
		tenantId = DataDriver.transformTentantId(tenantId, username);
		
		Map params = new HashMap();
		params.put("tenantid", tenantId);
		params.put("alias", username);
		
		SQLExecutor executor = getSqlExecutor();
		List<Map> userInfos = executor.executeNameParaQuery(
				"SELECT u.tenantid,u.userid,u.attempt_failed,u.attempt_clock,u.attempt_ip "+
				" FROM users u "+
				" WHERE u.tenantid=#{tenantid}"+
				    " AND u.alias=#{alias}", 
				params);
		if (userInfos.isEmpty()) {
			if (loginSuccess) {
				addUser(executor, username, tenantId, osUserId, osUserName, true);
				userInfos.add(map(
						"tenantid", tenantId,
						"userid",osUserId,
						"attempt_failed",0,
						"attempt_ip","",
						"attempt_clock",0
				));
			} else {
				throw new RuntimeException(_("Login name or password is incorrect."));
			}
		}
		
		Map userInfo = userInfos.get(0);
		long time = time();
		// check if user is blocked
		int attempt_failed = (Integer) userInfo.get("attempt_failed");
		if (attempt_failed >= RDA_LOGIN_ATTEMPTS) {
			int attempt_clock = (Integer) userInfo.get("attempt_clock");
			if ((time - attempt_clock) < RDA_LOGIN_BLOCK) {
				throw new RuntimeException(_s("Account is blocked for %s seconds", RDA_LOGIN_BLOCK - ( time - attempt_clock)));
			}
			params.put("time", time);
			executor.executeInsertDeleteUpdate(
					"UPDATE users SET attempt_clock=#{time}"+
					" WHERE tenantid=#{tenantid}"+
					    " AND alias=#{alias}", 
					params);
		}
		
		// check system permissions
		String userId = Nest.value(userInfo, "userid").asString();
		if (!check_perm2system(this.idBean, executor, userId)) {
			throw new RuntimeException(_("No permissions for system access."));
		}
		
		params.put("userId", userId);
		List<Map> datas = executor.executeNameParaQuery(
				"SELECT MAX(g.gui_access) AS gui_access "+
				" FROM usrgrp g,users_groups ug "+
				" WHERE g.tenantid=#{tenantid} "+
				    " AND ug.userid=#{userId} "+
				   " AND g.tenantid=ug.tenantid "+
				   " AND g.usrgrpid=ug.usrgrpid", 
				params);
		Map dbAccess = !datas.isEmpty() ? datas.get(0) : new IMap();
		Integer guiAccess = (Integer)dbAccess.get("gui_access");
		if(empty(guiAccess)){
			guiAccess = GROUP_GUI_ACCESS_SYSTEM;
		}
		
		if (!loginSuccess) {
			HttpServletRequest request = RadarContext.request();
			String ip = !empty(request.getHeader("X-Real-IP")) 
					? request.getHeader("X-Real-IP") 
					: request.getRemoteAddr();
			userInfo.put("attempt_failed", attempt_failed+1);
			params.put("attempt_failed", userInfo.get("attempt_failed"));
			params.put("attempt_clock", time());
			params.put("attempt_ip", ip);
			executor.executeInsertDeleteUpdate(
					"UPDATE users "+
					" SET attempt_failed=#{attempt_failed}, attempt_clock=#{attempt_clock}, attempt_ip=#{attempt_ip} "+
					" WHERE tenantid=#{tenantid}"+
					    " AND userid=#{userId}", 
					params);
			
			add_audit(this.idBean, getSqlExecutor(), AUDIT_ACTION_LOGIN,
					AUDIT_RESOURCE_USER, _s("Login failed \"%s\".", username));
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Login name or password is incorrect."));
		}
		// start session
		String sessionId = MD5(osTokenId);
		params.put("sessionId", sessionId);
		params.put("lastaccess", time);
		params.put("status", RDA_SESSION_ACTIVE);
		executor.executeInsertDeleteUpdate(
				"INSERT INTO sessions (tenantid,sessionid,userid,lastaccess,status)"+ 
				"VALUES (#{tenantid},#{sessionId},#{userId},#{lastaccess},#{status})", 
				params);
		
		add_audit(this.idBean, getSqlExecutor(), AUDIT_ACTION_LOGIN, AUDIT_RESOURCE_USER, _s("Correct login \"%s\".", username));
		
		Map<String, Object> udata = getUserData(userId);
		udata.put("sessionid", sessionId);
		udata.put("gui_access", guiAccess);
		udata.put("userid", userId);
		udata.put("tenantid", tenantId);
		udata.put("name", osUserUserName);
//		udata.put("osUser", osUser);	//此功能会在IaaS项目中用到，一体化监控项目暂时不需要，所以关掉
		udata.put("osTenant", osTenant);
		//强制添加SUPER_ADMIN权限
		udata.put("type", Defines.USER_TYPE_SUPER_ADMIN);
		udata.put("autologin", true);
		
		if (attempt_failed>0) {
			executor.executeInsertDeleteUpdate(
					"UPDATE users SET attempt_failed=0 "+
					" WHERE tenantid=#{tenantid}"+
			            " AND userid=#{userId}", 
					params);
		}
		
		CWebUser.data().clear();
		CWebUser.set(udata);
		
		return isset(user, "userData") ? userData() : sessionId;
	}

	@Override
	protected Map<String,Object> getUserData(String userid) {
		Map params = new HashMap();
		params.put("userId", userid);	
		SQLExecutor executor = getSqlExecutor();
		List<Map> datas = executor.executeNameParaQuery(
				"SELECT u.tenantid,u.userid,u.alias,u.name,u.surname,u.url,u.autologin,u.autologout,u.lang,u.refresh,u.type,u.theme,u.attempt_failed,u.attempt_ip,u.attempt_clock,u.rows_per_page "+
				" FROM users u "+
				" WHERE u.userid=#{userId}", 
				params);
		Map<String,Object> userData = datas.isEmpty() ? new HashMap() : datas.get(0);
		
		params.put("debugMode", GROUP_DEBUG_MODE_ENABLED);
		datas = executor.executeNameParaQuery(
				 "SELECT ug.userid "+
				 " FROM usrgrp g,users_groups ug "+
				 " WHERE  ug.userid=#{userId} "+
				     " AND g.usrgrpid=ug.usrgrpid "+
				     " AND g.debug_mode=#{debugMode}", 
				params);
		userData.put("debug_mode", !datas.isEmpty());
		
		HttpServletRequest request = RadarContext.request();
		String ip = !empty(request.getHeader("X-Real-IP")) 
				? request.getHeader("X-Real-IP") 
				: request.getRemoteAddr();
		userData.put("userip", ip);
		return userData;
	}
	
	/**
	 * 添加云平台的用户到系统数据库中
	 * 
	 * @param executor
	 * @param alias 	别名
	 * @param tenantId	租户ID
	 * @param userid	用户ID
	 * @param name		用户名称
	 */
	public static void addUser(SQLExecutor executor, String alias, String tenantId, String userid, String name, boolean enabled) {
		executor.executeInsertDeleteUpdate(
			"insert into users(tenantid,userid,alias,name,surname,passwd,url,autologin,autologout,lang,refresh,type,theme,attempt_failed,attempt_ip,attempt_clock,rows_per_page)"+
			"values(#{tenantid},#{userid},#{alias},#{name},#{surname},#{passwd},#{url},#{autologin},#{autologout},#{lang},#{refresh},#{type},#{theme},#{attempt_failed},#{attempt_ip},#{attempt_clock},#{rows_per_page})", 
			map(
					"tenantid", tenantId,
					"userid",userid,
					"alias",alias,
					"name",name,
					"surname","",
					"passwd","",
					"url","",
					"autologin",1,
					"autologout",0,
					"lang","en_US",
					"refresh",3000,
					"type",3,
					"theme","default",
					"attempt_failed",0,
					"attempt_ip","",
					"attempt_clock",0,
					"rows_per_page",50,
					"enabled",enabled?1:0
			)
		);
		executor.executeInsertDeleteUpdate(
			"INSERT INTO users_groups VALUES (#{tenantid},#{id},#{usrgrpid},#{userid})", 
			map(
					"tenantid", tenantId,
					"id", get_dbid(null, executor,"users_groups", "id"),
					"usrgrpid",Feature.defaultOsUserGroup,
					"userid",userid
			)
		);
	}

	private String getOsTenantIdByUsername(String username, boolean needReload) {
		boolean isInit = OS_USERS.isEmpty(); 
		if (needReload || isInit || !OS_USERS.containsKey(username) || Cphp.empty(OS_USERS.get(username).getTenantId())) {
			synchronized (OS_USERS) {
				IaaSClient osClient = getOpenStackClientForAdmin();
				Keystone keystone = osClient.getIdentityClient();
				Users echoUsers = keystone.users().list().execute();
				List<User> osUsers = echoUsers.getList();
				for (User u : osUsers) {
					User osUser = keystone.users().show(u.getId()).execute();
					OS_USERS.put(osUser.getUsername(), osUser);
				}
			}
		}
		if (OS_USERS.containsKey(username)) {
			return OS_USERS.get(username).getTenantId();
		}else {
			return null;
		}
	}

}
