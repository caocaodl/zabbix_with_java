package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.Defines.GROUP_DEBUG_MODE_ENABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_HTTP;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_LDAP;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_ATTEMPTS;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_BLOCK;
import static com.isoft.iradar.inc.Defines.RDA_SESSION_ACTIVE;
import static com.isoft.iradar.inc.Defines.THEME_DEFAULT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.MD5Util.MD5;
import static com.isoft.iradar.inc.PermUtil.check_perm2system;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.math.RandomUtils;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.RBase;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CMediaTypeGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.params.CUserMediaGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CSetValidator;
import com.isoft.iradar.validators.CTimePeriodValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

@CodeConfirmed("benne.2.2.6")
public class CUserGeneralDAO extends CCoreStringKeyDAO<CUserGet> {

	public CUserGeneralDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "users", "u", new String[]{"userid", "alias"});
	}

	@Override
	public <T> T get(CUserGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("users", "u.userid");
		sqlParts.from.put("users", "users u");
		
		// permission check
		if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
			if (!params.getEditable() && CWebUser.getType() == USER_TYPE_IRADAR_ADMIN) {
				sqlParts.from.put("users_groups", "users_groups ug");
				sqlParts.where.put("uug.tenantid", "u.tenantid=ug.tenantid");
				sqlParts.where.put("uug", "u.userid=ug.userid");

				sqlParts.where.put("ug.usrgrpid IN ("+
					" SELECT uug.usrgrpid"+
					" FROM users_groups uug"+
					" WHERE uug.tenantid=ug.tenantid"+
					" WHERE uug.userid="+sqlParts.marshalParam(CWebUser.get("userid"))+
				")");
			} else {
				sqlParts.where.put("u.userid="+sqlParts.marshalParam(CWebUser.get("userid")));
			}
		}

		// userids
		if (!is_null(params.getUserIds())) {
			sqlParts.where.dbConditionString("u.userid", params.getUserIds());
		}

		// usrgrpids
		if (!is_null(params.getUsrgrpIds())) {
			sqlParts.select.put("usrgrpid","ug.usrgrpid");
			sqlParts.from.put("users_groups","users_groups ug");
			sqlParts.where.dbConditionInt("ug.usrgrpid",params.getUsrgrpIds());
			sqlParts.where.put("uug.tenantid","u.tenantid=ug.tenantid");
			sqlParts.where.put("uug","u.userid=ug.userid");
		}

		// mediaids
		if (!is_null(params.getMediaIds())) {
			sqlParts.select.put("mediaid","m.mediaid");
			sqlParts.from.put("media","media m");
			sqlParts.where.dbConditionInt("m.mediaid",params.getMediaIds());
			sqlParts.where.put("mu.tenantid","m.tenantid=u.tenantid");
			sqlParts.where.put("mu","m.userid=u.userid");
		}

		// mediatypeids
		if (!is_null(params.getMediaTypeIds())) {
			sqlParts.select.put("mediatypeid","m.mediatypeid");
			sqlParts.from.put("media","media m");
			sqlParts.where.dbConditionInt("m.mediatypeid",params.getMediaTypeIds());
			sqlParts.where.put("mu.tenantid","m.tenantid=u.tenantid");
			sqlParts.where.put("mu","m.userid=u.userid");
		}
		
		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			if (isset(params.getFilter(),"passwd")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("It is not possible to filter by user password."));
			}
			dbFilter("users u", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			if (isset(params.getSearch(),"passwd")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("It is not possible to filter by user password."));
			}
			dbSearch("users u", params, sqlParts);
		}

		// limit
		if (params.getLimit() != null) {
			sqlParts.limit = params.getLimit();
		}
		
		CArray userIds = array();
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			unset(row,"passwd");
			if (params.getCountOutput()!=null) {
				ret = row.get("rowscount");
			} else {
				String id = (String)row.get("userid");
				userIds.add(id);
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// usrgrpids
				if (isset(row.get("usrgrpid")) && is_null(params.getSelectUsrgrps())) {
					if(!result.get(id).containsKey("usrgrps")){
						result.get(id).put("usrgrps", new ArrayList(0));
					}
					((CArray)result.get(id).get("usrgrps")).add(row.remove("usrgrpid"));
				}
				// mediaids
				if (isset(row.get("mediaid")) && is_null(params.getSelectMedias())) {
					if(!result.get(id).containsKey("medias")){
						result.get(id).put("medias", new ArrayList(0));
					}
					((CArray)result.get(id).get("medias")).add(row.remove("mediaid"));
				}
				// mediatypeids
				if (isset(row.get("mediatypeid")) && is_null(params.getSelectMediatypes())) {
					if(!result.get(id).containsKey("mediatypes")){
						result.get(id).put("mediatypes", new ArrayList(0));
					}
					((CArray)result.get(id).get("mediatypes")).add(row.remove("mediatypeid"));
				}
				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		/* Adding objects */
		if (params.getAccess() != null) {
			for(Map user : result) {
				for(String prop : new String[]{"gui_access", "debug_mode", "users_status"}){
					if(!user.containsKey(prop)){
						user.put(prop, 0);
					}
				}
			}

			sqlParts = new SqlBuilder();
			CArray<Map> access = DBselect(getSqlExecutor(),
				"SELECT ug.userid,MAX(g.gui_access) AS gui_access,"+
					" MAX(g.debug_mode) AS debug_mode,MAX(g.users_status) AS users_status"+
					" FROM usrgrp g,users_groups ug"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "usrgrp", "g")+
					    " AND "+sqlParts.dual.dbConditionInt("ug.userid", userIds.valuesAsLong())+
						" AND g.tenantid=ug.tenantid"+
						" AND g.usrgrpid=ug.usrgrpid"+
					" GROUP BY ug.userid",
				sqlParts.getNamedParams()
			);

			for(Map userAccess : access) {
				Nest.value(result,userAccess.get("userid")).$(rda_array_merge(result.get(userAccess.get("userid")), userAccess));
			}
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	protected void checkInput(CArray<Map> users, String method) {
		boolean create = ("create".equals(method));
		boolean update = ("update".equals(method));

		CArray userDBfields = null;
		CArray<Map> dbUsers = null;
		if (update) {
			userDBfields = map("userid", null);

			CUserGet uoptions = new CUserGet();
			uoptions.setOutput(new String[]{"userid", "alias", "autologin", "autologout"});
			uoptions.setUserIds(rda_objectValues(users, "userid").valuesAsString());
			uoptions.setEditable(true);
			uoptions.setPreserveKeys(true);
			dbUsers = get(uoptions);
		} else {
			userDBfields = map("alias", null, "passwd", null, "usrgrps", null, "user_medias", array());
		}

		CArray themes = array_keys(RBase.getThemes());
		themes.add(THEME_DEFAULT);
		CSetValidator themeValidator = CValidator.init(new CSetValidator(),map("values", themes));
		CArray alias = array();
		Map dbUser = null;
		for(Map user : users) {
			if (!check_db_fields(userDBfields, user)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Wrong fields for user \"%s\".", Nest.value(user,"alias").$()));
			}
			
			// permissions
			if (create) {
				if (CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("You do not have permissions to create users."));
				}

				dbUser = Clone.deepcopy(user);
			} else if (update) {
				if (!isset(dbUsers,user.get("userid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("You do not have permissions to update user or user does not exist."));
				}

				if (bccomp(Nest.value(CWebUser.data(),"userid").$(), Nest.value(user,"userid").$()) != 0 && CWebUser.getType() != USER_TYPE_SUPER_ADMIN) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("You do not have permissions to update other users."));
				}

				dbUser = dbUsers.get(user.get("userid"));
			}

			// check if user alias
			if (isset(user,"alias")) {
				// check if we change guest user
				if (RDA_GUEST_USER.equals(Nest.value(dbUser,"alias").asString()) && !RDA_GUEST_USER.equals(Nest.value(user,"alias").asString())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot rename guest user."));
				}

				if (!isset(alias,user.get("alias"))) {
					Nest.value(alias,user.get("alias")).$(update ? Nest.value(user,"userid").$() : 1);
				} else {
					if (create || bccomp(Nest.value(user,"userid").$(), alias.get(user.get("alias"))) != 0) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Duplicate user alias \"%s\".", Nest.value(user,"alias").$()));
					}
				}

				if (rda_strlen(Nest.value(user,"alias").asString()) > 64) {
					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_n(
							"Maximum alias length is %1$d characters, \"%2$s\" is %3$d character.",
							"Maximum alias length is %1$d characters, \"%2$s\" is %3$d characters.",
							64,
							Nest.value(user,"alias").$(),
							rda_strlen(Nest.value(user,"alias").asString())
						)
					);
				}
			}

			if (isset(user,"usrgrps")) {
				if (empty(Nest.value(user,"usrgrps").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("User \"%s\" cannot be without user group.", Nest.value(dbUser,"alias").$()));
				}

				// checking if user tries to disable himself (not allowed). No need to check this on creating a user.
				if (!create && bccomp(Nest.value(CWebUser.data(),"userid").$(), Nest.value(user,"userid").$()) == 0) {
					CUserGroupGet ugoptions = new CUserGroupGet();
					ugoptions.setUsrgrpIds(rda_objectValues(Nest.value(user,"usrgrps").$(), "usrgrpid").valuesAsLong());
					ugoptions.setOutput(API_OUTPUT_EXTEND);
					ugoptions.setPreserveKeys(true);
					ugoptions.setNopermissions(true);
					CArray<Map> usrgrps = API.UserGroup(this.idBean, this.getSqlExecutor()).get(ugoptions);
					for(Map group : usrgrps) {
						if (Nest.value(group,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("User may not modify GUI access for himself by becoming a member of user group \"%s\".", Nest.value(group,"name").$()));
						}

						if (Nest.value(group,"users_status").asInteger() == GROUP_STATUS_DISABLED) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("User may not modify system status for himself by becoming a member of user group \"%s\".", Nest.value(group,"name").$()));
						}
					}
				}
			}

			if (isset(user,"theme")) {
				themeValidator.messageInvalid = _s("Incorrect theme for user \"%1$s\".", Nest.value(dbUser,"alias").$());
				checkValidator(Nest.value(user,"theme").asString(), themeValidator);
			}

			if (isset(user,"type") && (USER_TYPE_SUPER_ADMIN != CWebUser.getType())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("You are not allowed to alter privileges for user \"%s\".", Nest.value(dbUser,"alias").$()));
			}

			if (isset(user,"autologin") && Nest.value(user,"autologin").asInteger() == 1 && Nest.value(dbUser,"autologout").asInteger() != 0) {
				Nest.value(user,"autologout").$(0);
			}

			if (isset(user,"autologout") && Nest.value(user,"autologout").asInteger() > 0 && Nest.value(dbUser,"autologin").asInteger() != 0) {
				Nest.value(user,"autologin").$(0);
			}

			if (array_key_exists("passwd", user)) {
				if (is_null(Nest.value(user,"passwd").$())) {
					unset(user,"passwd");
				} else {
					if (RDA_GUEST_USER.equals(Nest.value(dbUser,"alias").asString()) && !rda_empty(Nest.value(user,"passwd").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Not allowed to set password for user \"guest\"."));
					}

					Nest.value(user,"passwd").$(MD5(Nest.value(user,"passwd").asString()));
				}
			}

			if (isset(user,"alias")) {
				CUserGet uoptions = new CUserGet();
				uoptions.setFilter("alias", Nest.value(user,"alias").asString());
				uoptions.setNopermissions(true);
				CArray<Map> userExists = get(uoptions);
				Map exUser = reset(userExists);
				if (!empty(exUser)) {
					if (create || (bccomp(Nest.value(exUser,"userid").$(), Nest.value(user,"userid").$()) != 0)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("User with alias \"%s\" already exists.", Nest.value(user,"alias").$()));
					}
				}
			}
		}
	}
	
	/**
	 * Create user.
	 *
	 * @param array  users
	 * @param string users["name"]
	 * @param string users["surname"]
	 * @param array  users["alias"]
	 * @param string users["passwd"]
	 * @param string users["url"]
	 * @param int    users["autologin"]
	 * @param int    users["autologout"]
	 * @param string users["lang"]
	 * @param string users["theme"]
	 * @param int    users["refresh"]
	 * @param int    users["rows_per_page"]
	 * @param int    users["type"]
	 * @param array  users["user_medias"]
	 * @param string users["user_medias"]["mediatypeid"]
	 * @param string users["user_medias"]["address"]
	 * @param int    users["user_medias"]["severity"]
	 * @param int    users["user_medias"]["active"]
	 * @param string users["user_medias"]["period"]
	 *
	 * @return array
	 */
	@Override
	public CArray<String[]> create(CArray<Map> users) {
		checkInput(users, "create");

		CArray<Long> userids = insert("users", Clone.deepcopy(users));
		Map params = new HashMap();
		params.put("tenantid", this.idBean.getTenantId());
		String sql = "INSERT INTO users_groups (tenantid,id,usrgrpid,userid) VALUES (#{tenantid},#{id},#{usrgrpid},#{userid})";
		String sql1 = "INSERT INTO media (tenantid,mediaid,userid,mediatypeid,sendto,active,severity,period) VALUES (#{tenantid},#{mediaid},#{userid},#{mediatypeid},#{sendto},#{active},#{severity},#{period})";
		for (Entry<Object, Map> e : users.entrySet()) {
		    Object unum = e.getKey();
		    Map user = e.getValue();
			Long userid = userids.get(unum);

			CArray usrgrps = rda_objectValues(Nest.value(user,"usrgrps").asCArray(), "usrgrpid");
			if(usrgrps!=null && !usrgrps.isEmpty()){
				for(Object groupid : usrgrps) {
					Long usersGroupdId = get_dbid(this.idBean, getSqlExecutor(), "users_groups", "id");
					params.put("id", usersGroupdId);
					params.put("usrgrpid", groupid);
					params.put("userid", userid);
					if (!DBexecute(getSqlExecutor(),sql,params)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
					}
				}
			}

			CArray<Map> usermedias = Nest.value(user,"user_medias").asCArray();
			if(usermedias!=null && !usermedias.isEmpty()){
				for(Map mediaData : usermedias) {
					Long mediaid = get_dbid(this.idBean, getSqlExecutor(),"media", "mediaid");
					params.put("mediaid", mediaid);
					params.put("userid", userid);
					params.put("mediatypeid", Nest.value(mediaData,"mediatypeid").$());
					params.put("sendto", Nest.value(mediaData,"sendto").$());
					params.put("active", Nest.value(mediaData,"active").$());
					params.put("severity", Nest.value(mediaData,"severity").$());
					params.put("period", Nest.value(mediaData,"period").$());
					
					if (!DBexecute(getSqlExecutor(), sql1, params)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
					}
				}
			}
		}

		return map("userids", userids.valuesAsLong());
	}

	/**
	 * Update user.
	 *
	 * @param array  users
	 * @param string users["userid"]
	 * @param string users["name"]
	 * @param string users["surname"]
	 * @param array  users["alias"]
	 * @param string users["passwd"]
	 * @param string users["url"]
	 * @param int    users["autologin"]
	 * @param int    users["autologout"]
	 * @param string users["lang"]
	 * @param string users["theme"]
	 * @param int    users["refresh"]
	 * @param int    users["rows_per_page"]
	 * @param int    users["type"]
	 * @param array  users["user_medias"]
	 * @param string users["user_medias"]["mediatypeid"]
	 * @param string users["user_medias"]["address"]
	 * @param int    users["user_medias"]["severity"]
	 * @param int    users["user_medias"]["active"]
	 * @param string users["user_medias"]["period"]
	 *
	 * @return array
	 */
	@Override
	public CArray<String[]> update(CArray<Map> users) {
		users = Clone.deepcopy(users);
		CArray userids = rda_objectValues(users, "userid");

		checkInput(users, "update");
		
		Map params = new HashMap();
		params.put("tenantid", this.idBean.getTenantId());
		String sql = "INSERT INTO users_groups VALUES (#{tenantid},#{id},#{usrgrpid},#{userid})";
		for(Map user : users) {
			//boolean self = (bccomp(Nest.value(CWebUser.data(),"userid").$(), Nest.value(user,"userid").$()) == 0);

			boolean result = update("users", array((Map)map(
					"values", Clone.deepcopy(user),
					"where", map("userid", Nest.value(user,"userid").$())
				)
			));

			if (!result) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
			}

			if (isset(user,"usrgrps") && !is_null(Nest.value(user,"usrgrps").$())) {
				CArray newUsrgrpids = rda_objectValues(Nest.value(user,"usrgrps").asCArray(), "usrgrpid");

				// deleting all relations with groups, but not touching those, where user still must be after update
				SqlBuilder sqlParts = new SqlBuilder();
				DBexecute(
					getSqlExecutor(),
					"DELETE FROM users_groups"+
					" WHERE tenantid="+sqlParts.marshalParam(this.idBean.getTenantId())+
					    " AND userid="+sqlParts.marshalParam(Nest.value(user,"userid").asString())+
					    " AND "+sqlParts.dual.dbConditionInt("usrgrpid", newUsrgrpids.valuesAsLong(), true),
					sqlParts.getNamedParams());

				// getting the list of groups user is currently in
				sqlParts = new SqlBuilder();
				CArray<Map> dbGroupsUserIn = DBselect(
						getSqlExecutor(),
						"SELECT usrgrpid FROM users_groups"+
						" WHERE tenantid="+sqlParts.marshalParam(this.idBean.getTenantId())+
						    " AND userid="+sqlParts.marshalParam(Nest.value(user,"userid").asString()),
						sqlParts.getNamedParams());
				CArray groupsUserIn = array();
				for(Map grp : dbGroupsUserIn) {
					Nest.value(groupsUserIn,grp.get("usrgrpid")).$(Nest.value(grp,"usrgrpid").$());
				}

				CUserGroupGet ugoptions = new CUserGroupGet();
				ugoptions.setUsrgrpIds(rda_objectValues(Nest.value(user,"usrgrps").$(), "usrgrpid").valuesAsLong());
				ugoptions.setOutput(API_OUTPUT_EXTEND);
				ugoptions.setPreserveKeys(true);
				CArray<Map> usrgrps = API.UserGroup(this.idBean, this.getSqlExecutor()).get(ugoptions);
				for (Entry<Object, Map> e : usrgrps.entrySet()) {
				    Object groupid = e.getKey();
				    //Map group = e.getValue();
					// if user is not already in a given group
					if (isset(groupsUserIn,groupid)) {
						continue;
					}

					Long usersGroupdId = get_dbid(this.idBean, getSqlExecutor(),"users_groups", "id");
					params.put("id", usersGroupdId);
					params.put("usrgrpid", groupid);
					params.put("userid", Nest.value(user,"userid").$());
					if (!DBexecute(getSqlExecutor(), sql, params)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
					}
				}
			}
		}

		return map("userids", userids.valuesAsLong());
	}

	public CArray<String[]> updateProfile(Map user) {
		Nest.value(user,"userid").$(Nest.value(CWebUser.data(),"userid").$());
		return update(array(Clone.deepcopy(user)));
	}
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array userIds
	 */
	protected void validateDelete(String...  userIds) {
		if (empty(userIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		checkPermissions(userIds);
		checkDeleteCurrentUser(userIds);
		checkDeleteInternal(userIds);
	}
	
	/**
	 * Delete user.
	 *
	 * @param array userIds
	 *
	 * @return array
	 */
	@Override
	public CArray<String[]> delete(String... userIds) {
		validateDelete(userIds);

		// delete action operation msg
		CArray operationids = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbOperations = DBselect(
			getSqlExecutor(),
			"SELECT DISTINCT om.operationid"+
			" FROM opmessage_usr om"+
			" WHERE om.tenantid="+sqlParts.marshalParam(this.idBean.getTenantId())+
			   " AND "+sqlParts.dual.dbConditionString("om.userid", userIds),
			sqlParts.getNamedParams()
		);
		for(Map dbOperation : dbOperations) {
			Nest.value(operationids,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
		}

		delete("opmessage_usr", (CArray)map("userid", userIds));

		// delete empty operations
		CArray delOperationids = array();
		sqlParts = new SqlBuilder();
		dbOperations = DBselect(
			getSqlExecutor(),
			"SELECT DISTINCT o.operationid"+
			" FROM operations o"+
			" WHERE o.tenantid="+sqlParts.marshalParam(this.idBean.getTenantId())+
			    " AND "+sqlParts.dual.dbConditionInt("o.operationid", operationids.valuesAsLong())+
				" AND NOT EXISTS(SELECT om.opmessage_usrid FROM opmessage_usr om WHERE  om.tenantid=o.tenantid"+ 
				" AND om.operationid=o.operationid)",
			sqlParts.getNamedParams()
		);
		for(Map dbOperation : dbOperations) {
			Nest.value(delOperationids,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
		}

		delete("operations", (CArray)map("operationid", delOperationids.valuesAsLong()));
		delete("media", (CArray)map("userid", userIds));
		delete("profiles", (CArray)map("userid", userIds));
		delete("users_groups", (CArray)map("userid", userIds));
		delete("users", (CArray)map("userid", userIds));

		return map("userids", userIds);
	}

	/**
	 * Add user media.
	 *
	 * @param array  data["users"]
	 * @param string data["users"]["userid"]
	 * @param array  data["medias"]
	 * @param string data["medias"]["mediatypeid"]
	 * @param string data["medias"]["address"]
	 * @param int    data["medias"]["severity"]
	 * @param int    data["medias"]["active"]
	 * @param string data["medias"]["period"]
	 *
	 * @return array
	 */
	public CArray<Long[]> addMedia(Map data) {
		validateAddMedia(data);
		CArray<Long> mediaIds = addMediaReal(data);
		return map("mediaids", mediaIds.valuesAsLong());
	}
	
	/**
	 * Validate add user media.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array  data["users"]
	 * @param string data["users"]["userid"]
	 * @param array  data["medias"]
	 * @param string data["medias"]["mediatypeid"]
	 * @param string data["medias"]["address"]
	 * @param int    data["medias"]["severity"]
	 * @param int    data["medias"]["active"]
	 * @param string data["medias"]["period"]
	 */
	protected void validateAddMedia(Map data) {
		if (CWebUser.getType() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Only iRadar Admins can add user media."));
		}

		if (!isset(data,"users") || !isset(data,"medias")) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
		}

		CArray<Map> users = rda_toArray(Nest.value(data,"users").asCArray());
		CArray<Map> media = rda_toArray(Nest.value(data,"medias").asCArray());

		if (!isWritable(rda_objectValues(users, "userid").valuesAsString())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		CArray mediaDBfields = map(
			"period", null,
			"mediatypeid", null,
			"sendto", null,
			"active", null,
			"severity", null
		);

		for(Map mediaItem : media) {
			if (!check_db_fields(mediaDBfields, mediaItem)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}
		}

		CTimePeriodValidator timePeriodValidator = CValidator.init(new CTimePeriodValidator(),map());

		for(Map mediaItem : media) {
			if (!timePeriodValidator.validate(this.idBean, Nest.value(mediaItem,"period").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, timePeriodValidator.getError());
			}
		}
	}
	
	/**
	 * Create user media.
	 *
	 * @throws APIException if user media insert is fail.
	 *
	 * @param array  data["users"]
	 * @param string data["users"]["userid"]
	 * @param array  data["medias"]
	 * @param string data["medias"]["mediatypeid"]
	 * @param string data["medias"]["address"]
	 * @param int    data["medias"]["severity"]
	 * @param int    data["medias"]["active"]
	 * @param string data["medias"]["period"]
	 *
	 * @return array
	 */
	protected CArray<Long> addMediaReal(Map data) {
		CArray<Map> users = rda_toArray(Nest.value(data,"users").asCArray());
		CArray<Map> media = rda_toArray(Nest.value(data,"medias").asCArray());

		CArray<Long> mediaIds = array();
		String sql = "INSERT INTO media (tenantid,mediaid,userid,mediatypeid,sendto,active,severity,period) VALUES (#{tenantid},#{mediaid},#{userid},#{mediatypeid},#{sendto},#{active},#{severity},#{period})";
		Map params = new HashMap();
		params.put("tenantid", this.idBean.getTenantId());
		for(Map user : users) {
			for(Map mediaItem : media) {
				Long mediaId = get_dbid(this.idBean, getSqlExecutor(),"media", "mediaid");
				params.put("mediaid", mediaId);
				params.put("userid", Nest.value(user,"userid").$());
				params.put("mediatypeid", Nest.value(mediaItem,"mediatypeid").$());
				params.put("sendto", Nest.value(mediaItem,"sendto").$());
				params.put("active", Nest.value(mediaItem,"active").$());
				params.put("severity", Nest.value(mediaItem,"severity").$());
				params.put("period", Nest.value(mediaItem,"period").$());
				if (!DBexecute(getSqlExecutor(),sql,params)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot insert user media."));
				}
				mediaIds.add(mediaId);
			}
		}
		return mediaIds;
	}
	
	/**
	 * Update user media.
	 *
	 * @throws APIException if user media update is fail.
	 *
	 * @param array  data["users"]
	 * @param string data["users"]["userid"]
	 * @param array  data["medias"]
	 * @param string data["medias"]["mediatypeid"]
	 * @param string data["medias"]["address"]
	 * @param int    data["medias"]["severity"]
	 * @param int    data["medias"]["active"]
	 * @param string data["medias"]["period"]
	 *
	 * @return array
	 */
	public CArray<Long[]> updateMedia(Map data) {
		validateUpdateMedia(data);

		CArray<Map> users = rda_toArray(Nest.value(data,"users").asCArray());
		CArray<Map> media = rda_toArray(Nest.value(data,"medias").asCArray());

		CArray userIds = array_keys(array_flip((rda_objectValues(users, "userid"))));

		CUserMediaGet umoptions = new CUserMediaGet();
		umoptions.setOutput(new String[]{"mediaid"});
		umoptions.setUserIds(userIds.valuesAsLong());
		umoptions.setEditable(true);
		umoptions.setPreserveKeys(true);
		CArray<Map> dbMedia = API.UserMedia(this.idBean, this.getSqlExecutor()).get(umoptions);

		CArray<Map> mediaToCreate = array();
		CArray<Map> mediaToUpdate = array();
		CArray mediaToDelete = array();

		for(Map mediaItem : media) {
			if (isset(mediaItem,"mediaid")) {
				Nest.value(mediaToUpdate,mediaItem.get("mediaid")).$(mediaItem);
			} else {
				mediaToCreate.add(mediaItem);
			}
		}

		for(Map dbMediaItem : dbMedia) {
			if (!isset(mediaToUpdate,dbMediaItem.get("mediaid"))) {
				Nest.value(mediaToDelete,dbMediaItem.get("mediaid")).$(Nest.value(dbMediaItem,"mediaid").$());
			}
		}

		// create
		if (!empty(mediaToCreate)) {
			addMediaReal(map(
				"users", users,
				"medias", mediaToCreate
			));
		}

		// update
		if (!empty(mediaToUpdate)) {
			String sql = "UPDATE media SET mediatypeid=#{mediatypeid}, sendto=#{sendto}, active=#{active}, severity=#{severity}, period=#{period} WHERE tenantid=#{tenantid} AND mediaid=#{mediaid}";
			Map params = new HashMap();
			params.put("tenantid", this.idBean.getTenantId());
			for(Map cmedia : mediaToUpdate) {
				params.put("mediatypeid", Nest.value(cmedia,"mediatypeid").$());
				params.put("sendto", Nest.value(cmedia,"sendto").$());
				params.put("active", Nest.value(cmedia,"active").$());
				params.put("severity", Nest.value(cmedia,"severity").$());
				params.put("period", Nest.value(cmedia,"period").$());
				params.put("mediaid", Nest.value(cmedia,"mediaid").$());
				boolean result = DBexecute(getSqlExecutor(),sql,params);
				if (!result) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot update user media."));
				}
			}
		}

		// delete
		if (!empty(mediaToDelete)) {
			deleteMediaReal(mediaToDelete.valuesAsLong());
		}

		return map("userids", userIds.valuesAsLong());
	}
	
	/**
	 * Validate update user media.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array  data["users"]
	 * @param string data["users"]["userid"]
	 * @param array  data["medias"]
	 * @param string data["medias"]["mediatypeid"]
	 * @param string data["medias"]["address"]
	 * @param int    data["medias"]["severity"]
	 * @param int    data["medias"]["active"]
	 * @param string data["medias"]["period"]
	 */
	protected void validateUpdateMedia(Map data) {
		if (CWebUser.getType() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only iRadar Admins can change user media."));
		}

		if (!isset(data,"users") || !isset(data,"medias")) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
		}

		CArray<Map> users = rda_toArray(Nest.value(data,"users").asCArray());
		CArray<Map> media = rda_toArray(Nest.value(data,"medias").asCArray());

		// validate user permissions
		if (!isWritable(rda_objectValues(users, "userid").valuesAsString())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
		}

		// validate media permissions
		CArray mediaIds = array();

		for(Map mediaItem : media) {
			if (isset(mediaItem,"mediaid")) {
				Nest.value(mediaIds,mediaItem.get("mediaid")).$(Nest.value(mediaItem,"mediaid").$());
			}
		}

		if (!empty(mediaIds)) {
			CUserMediaGet umoptions = new CUserMediaGet();
			umoptions.setCountOutput(true);
			umoptions.setMediaIds(mediaIds.valuesAsLong());
			umoptions.setEditable(true);
			long dbUserMediaCount = API.UserMedia(this.idBean, this.getSqlExecutor()).get(umoptions);
			if (dbUserMediaCount != count(mediaIds)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
			}
		}

		// validate media parameters
		CArray mediaDBfields = map(
			"period", null,
			"mediatypeid", null,
			"sendto", null,
			"active", null,
			"severity", null
		);

		CTimePeriodValidator timePeriodValidator = CValidator.init(new CTimePeriodValidator(),map());

		for(Map mediaItem : media) {
			if (!check_db_fields(mediaDBfields, mediaItem)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Invalid method parameters."));
			}

			if (!timePeriodValidator.validate(this.idBean, Nest.value(mediaItem,"period").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, timePeriodValidator.getError());
			}
		}
	}
	
	/**
	 * Delete user media.
	 *
	 * @param array mediaIds
	 *
	 * @return array
	 */
	public CArray<Long[]> deleteMedia(Long... mediaIds) {
		validateDeleteMedia(mediaIds);
		deleteMediaReal(mediaIds);
		return map("mediaids", mediaIds);
	}
	
	/**
	 * Validate delete user media.
	 *
	 * @throws APIException if the input is invalid
	 */
	protected void validateDeleteMedia(Long... mediaIds) {
		if (CWebUser.getType() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Only iRadar Admins can remove user media."));
		}

		CUserMediaGet umoptions = new CUserMediaGet();
		umoptions.setCountOutput(true);
		umoptions.setMediaIds(TArray.as(mediaIds).asLong());
		umoptions.setEditable(true);
		long dbUserMediaCount = API.UserMedia(this.idBean, this.getSqlExecutor()).get(umoptions);

		if (count(mediaIds) != dbUserMediaCount) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	/**
	 * Delete user media.
	 *
	 * @throws APIException if delete is fail
	 */
	public void deleteMediaReal(Long... mediaIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		if (!DBexecute(getSqlExecutor(),
				"DELETE FROM media"+
				" WHERE tenantid="+sqlParts.marshalParam(this.idBean.getTenantId())+
				   " AND "+sqlParts.dual.dbConditionInt("mediaid", mediaIds),
				sqlParts.getNamedParams())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete user media."));
		}
	}
	
	protected boolean ldapLogin(Map user) {
		throw new NotImplementedException("ldapLogin");
	}	
	
	protected boolean dbLogin(Map user) {
		Map params = new IMap();
		params.put("tenantid", this.idBean.getTenantId());
		params.put("alias", user.get("user"));
		params.put("passwd", MD5((String)user.get("password")));
		
		SQLExecutor executor = getSqlExecutor();
		List datas = executor.executeNameParaQuery(
				"SELECT NULL "+
				" FROM users u "+
				" WHERE u.tenantid=#{tenantid} "+
				" AND u.alias=#{alias} "+
				" AND u.passwd=#{passwd}",
				params);
		if (!datas.isEmpty()) {
			return true;
		} else {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_("Login name or password is incorrect."));
		}
	}
	
	public boolean logout() {
		Map params = new HashMap();		
		String sessionId = Nest.as(CWebUser.get("sessionid")).asString();
		if(sessionId == null || sessionId.length()==0){
			sessionId = RadarContext.sessionId();
		}
		params.put("tenantid", this.idBean.getTenantId());
		params.put("sessionId", sessionId);
		params.put("status", RDA_SESSION_ACTIVE);
		
		SQLExecutor executor = getSqlExecutor();
		List<String> session = executor.executeNameParaQuery(
				"SELECT s.userid "+
				" FROM sessions s "+
                " WHERE s.tenantid=#{tenantid} "+
                " AND s.sessionid=#{sessionId} "+
                " AND s.status=#{status}", 
				params, String.class);
		if (!session.isEmpty()) {
			params.put("userId", CWebUser.get("userid"));
			executor.executeInsertDeleteUpdate(
					"DELETE FROM sessions WHERE tenantid=#{tenantid} AND status=#{status} AND userid=#{userId}", 
					params);
			executor.executeInsertDeleteUpdate(
					"UPDATE sessions SET status=#{status} WHERE tenantid=#{tenantid} AND sessionid=#{sessionId}", 
					params);
		} else {
			info( _("Cannot logout."));			
		}		
		return true;
	}
	
	/**
	 * @param user
	 * @return String|Map
	 */
	public Object login(Map<String,Object> user) {
		String name = Nest.value(user,"user").asString(true);
		String password = Nest.value(user,"password").asString(true);
		password = MD5(password);
		
		Map params = new HashMap();
		params.put("tenantid", this.idBean.getTenantId());
		params.put("alias", name);
		
		SQLExecutor executor = getSqlExecutor();
		List<Map> userInfos = executor.executeNameParaQuery(
				"SELECT u.tenantid,u.userid,u.attempt_failed,u.attempt_clock,u.attempt_ip "+
				" FROM users u "+
				" WHERE u.tenantid=#{tenantid}"+
				    " AND u.alias=#{alias}", 
				params);
		if (userInfos.isEmpty()) {
			throw new RuntimeException(_("Login name or password is incorrect."));
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
		
		Map<String, Object> config = select_config(this.idBean, executor);
		Integer authType = (Integer)config.get("authentication_type");
		switch (guiAccess) {
		case GROUP_GUI_ACCESS_INTERNAL:
			authType = (authType == RDA_AUTH_HTTP) 
					? RDA_AUTH_HTTP
					: RDA_AUTH_INTERNAL;
			break;
		case GROUP_GUI_ACCESS_DISABLED:
			/* fall through */
		case GROUP_GUI_ACCESS_SYSTEM:
			/* fall through */
		}
		
		try {
			switch (authType) {
			case RDA_AUTH_LDAP:
				ldapLogin(user);
				break;
			case RDA_AUTH_INTERNAL:
				dbLogin(user);
				break;
			case RDA_AUTH_HTTP:
			}
		} catch (APIException e) {
			HttpServletRequest request = RadarContext.request();
			String ip = !empty(request.getHeader("HTTP_X_FORWARDED_FOR")) 
					? request.getHeader("HTTP_X_FORWARDED_FOR") 
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
					AUDIT_RESOURCE_USER, _s("Login failed \"%s\".", name));
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, e.getMessage());
		}
		// start session
		String sessionId = MD5(time+password+name+(RandomUtils.nextInt(10000000)));
		params.put("sessionId", sessionId);
		params.put("lastaccess", time);
		params.put("status", RDA_SESSION_ACTIVE);
		executor.executeInsertDeleteUpdate(
				"INSERT INTO sessions (tenantid,sessionid,userid,lastaccess,status)"+ 
				"VALUES (#{tenantid},#{sessionId},#{userId},#{lastaccess},#{status})", 
				params);
		
		add_audit(this.idBean, getSqlExecutor(), AUDIT_ACTION_LOGIN, AUDIT_RESOURCE_USER, _s("Correct login \"%s\".", name));
		
		Map<String, Object> udata = getUserData(userId);
		udata.put("sessionid", sessionId);
		udata.put("gui_access", guiAccess);
		udata.put("userid", userId);
		
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
	
	public Map<String, Object> checkAuthentication(String sessionid) {
		// access DB only once per page load
		if (!empty(userData())) {
			return userData();
		}
		
		long time = time();
		Map params = new IMap();
		params.put("tenantid", this.idBean.getTenantId());
		params.put("sessionId", sessionid);
		params.put("status", RDA_SESSION_ACTIVE);
		params.put("time", time);
		SQLExecutor executor = getSqlExecutor();
		List<Map> userInfos = executor.executeNameParaQuery(
				"SELECT u.userid,u.autologout,s.lastaccess "+
				" FROM sessions s,users u "+
				" WHERE u.tenantid=#{tenantid} "+
				    " AND s.sessionid=#{sessionId} "+
					" AND s.status=#{status} "+
					" AND s.tenantid=u.tenantid "+
					" AND s.userid=u.userid "+
					" AND (s.lastaccess+u.autologout>#{time} OR u.autologout=0)", 
				params);
		if (userInfos.isEmpty()) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_("Session terminated, re-login, please."));
		}
		
		Map userInfo = userInfos.get(0);
		params.put("userId", userInfo.get("userid"));
		// don't check permissions on the same second
		if (time != (Integer) userInfo.get("lastaccess")) {
			if (!check_perm2system(this.idBean, executor, Nest.value(userInfo,"userid").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions for system access."));
			}
			
			if ((Integer) userInfo.get("autologout") > 0) {
				executor.executeInsertDeleteUpdate(
						"DELETE FROM sessions WHERE tenantid=#{tenantid} AND userid=#{userId} AND lastaccess<#{time}", 
						params);
			}
			
			executor.executeInsertDeleteUpdate(
					"UPDATE sessions SET lastaccess=#{time} WHERE tenantid=#{tenantid} AND userid=#{userId} AND sessionid=#{sessionId}", 
					params);
		}
		
		Integer guiAccess = null;
		List<Map> dbAccess = executor.executeNameParaQuery(
				 "SELECT MAX(g.gui_access) AS gui_access "+
				 " FROM usrgrp g,users_groups ug "+
				 " WHERE ug.tenantid=#{tenantid} "+
				     " AND ug.userid=#{userId} "+
				     " AND g.tenantid=ug.tenantid "+
				     " AND g.usrgrpid=ug.usrgrpid", 
				params);
		if (!dbAccess.isEmpty() && empty(dbAccess.get(0).get("gui_access"))) {
			guiAccess = (Integer)dbAccess.get(0).get("gui_access");
		} else {
			guiAccess = GROUP_GUI_ACCESS_SYSTEM;
		}
		
		Map<String, Object> userData = getUserData(Nest.value(userInfo,"userid").asString());
		userData.put("sessionid", sessionid);
		userData.put("gui_access", guiAccess);
		CWebUser.set(userData);
		return userData;
	}

	protected Map<String,Object> getUserData(String userid) {
		Map params = new HashMap();
		params.put("tenantid", this.idBean.getTenantId());
		params.put("userId", userid);	
		SQLExecutor executor = getSqlExecutor();
		List<Map> datas = executor.executeNameParaQuery(
				"SELECT u.tenantid,u.userid,u.alias,u.name,u.surname,u.url,u.autologin,u.autologout,u.lang,u.refresh,u.type,u.theme,u.attempt_failed,u.attempt_ip,u.attempt_clock,u.rows_per_page "+
				" FROM users u "+
				" WHERE u.tenantid=#{tenantid}"+
				" AND u.userid=#{userId}", 
				params);
		Map<String,Object> userData = datas.isEmpty() ? new HashMap() : datas.get(0);
		
		params.put("debugMode", GROUP_DEBUG_MODE_ENABLED);
		datas = executor.executeNameParaQuery(
				 "SELECT ug.userid "+
				 " FROM usrgrp g,users_groups ug "+
				 " WHERE g.tenantid=#{tenantid}"+
				     " AND ug.userid=#{userId} "+
				     " AND g.tenantid=ug.tenantid "+
				     " AND g.usrgrpid=ug.usrgrpid "+
				     " AND g.debug_mode=#{debugMode}", 
				params);
		userData.put("debug_mode", !datas.isEmpty());
		
		HttpServletRequest request = RadarContext.request();
		String ip = !empty(request.getHeader("HTTP_X_FORWARDED_FOR")) 
				? request.getHeader("HTTP_X_FORWARDED_FOR") 
				: request.getRemoteAddr();
		userData.put("userip", ip);
		return userData;
	}

	@Override
	public boolean isReadable(String... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CUserGet options = new CUserGet();
		options.setUserIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(String... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CUserGet options = new CUserGet();
		options.setUserIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void addRelatedObjects(CUserGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] userIds = result.keysAsLong();

		// adding usergroups
		if (params.getSelectUsrgrps()!=null && !API_OUTPUT_COUNT.equals(params.getSelectUsrgrps())) {
			CRelationMap relationMap = this.createRelationMap(result, "userid", "usrgrpid", "users_groups");
			
			CUserGroupGet aparams = new CUserGroupGet();
			aparams.setOutput(params.getSelectUsrgrps());
			aparams.setUsrgrpIds(relationMap.getRelatedLongIds());
			aparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.UserGroup(this.idBean, getSqlExecutor()).get(aparams);
			relationMap.mapMany(result, datas, "usrgrps");
		}

		// adding medias
		if (params.getSelectMedias()!=null && !API_OUTPUT_COUNT.equals(params.getSelectMedias())) {
			CUserMediaGet aparams = new CUserMediaGet();
			aparams.setOutput(this.outputExtend("media", new String[] {"userid", "mediaid"}, params.getSelectMedias()));
			aparams.setUserIds(userIds);
			aparams.setPreserveKeys(true);
			
			CArray<Map> userMedias = API.UserMedia(this.idBean, getSqlExecutor()).get(aparams);
			CRelationMap relationMap = this.createRelationMap(userMedias, "userid", "mediaid");
			this.unsetExtraFields(userMedias, new String[] {"userid", "mediaid"}, params.getSelectMedias());
			relationMap.mapMany(result, userMedias, "medias");
		}

		// adding media types
		if (params.getSelectMediatypes()!=null && !API_OUTPUT_COUNT.equals(params.getSelectMediatypes())) {
			CRelationMap relationMap = this.createRelationMap(result, "userid", "mediatypeid", "media");
			
			CMediaTypeGet aparams = new CMediaTypeGet();
			aparams.setOutput(params.getSelectMediatypes());
			aparams.setMediaTypeIds(relationMap.getRelatedLongIds());
			aparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.MediaType(this.idBean, getSqlExecutor()).get(aparams);
			relationMap.mapMany(result, datas, "mediatypes");
		}
	}
	
	/**
	 * Checks if the given users are editable.
	 *
	 * @param array userIds	user ids to check
	 *
	 * @throws APIException		if the user has no permissions to edit users or a user does not exist
	 */
	protected void checkPermissions(String... userIds) {
		if (!isWritable(userIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	/**
	 * Check if we're trying to delete the currently logged in user.
	 *
	 * @param array userIds	user ids to check
	 *
	 * @throws APIException		if we're deleting the current user
	 */
	protected void checkDeleteCurrentUser(String... userIds) {
		if (in_array(Nest.value(CWebUser.data(),"userid").asString(), userIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("User is not allowed to delete himself."));
		}
	}
	
	/**
	 * Check if we're trying to delete the guest user.
	 *
	 * @param array userIds	user ids to check
	 *
	 * @throws APIException		if we're deleting the guest user
	 */
	protected void checkDeleteInternal(String... userIds) {
		CUserGet options = new CUserGet();
		options.setOutput(new String[]{"userid"});
		options.setFilter("alias", RDA_GUEST_USER);
		CArray<Map> guests = get(options);
		Map guest = reset(guests);

		if (in_array(Nest.value(guest,"userid").asString(), userIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Cannot delete iRadar internal user \"%1$s\", try disabling that user.", RDA_GUEST_USER)
			);
		}
	}
	
}
