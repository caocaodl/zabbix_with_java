package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CUserGroupDAO extends CCoreLongKeyDAO<CUserGroupGet> {

	public CUserGroupDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "usrgrp", "g", new String[]{"usrgrpid", "name"});
	}

	@Override
	public <T> T get(CUserGroupGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("usrgrp", "g.usrgrpid");
		sqlParts.from.put("usrgrp", "usrgrp g");
		
		// usrgrpids
		if (!is_null(params.getUsrgrpIds())) {
			sqlParts.where.dbConditionInt("g.usrgrpid", params.getUsrgrpIds());
		}

		// userids
		if (!is_null(params.getUserIds())) {
			sqlParts.select.put("userid","ug.userid");
			sqlParts.from.put("users_groups","users_groups ug");
			sqlParts.where.dbConditionString("ug.userid",params.getUserIds());
			sqlParts.where.put("gug.tenantid","g.tenantid=ug.tenantid");
			sqlParts.where.put("gug","g.usrgrpid=ug.usrgrpid");
		}

		// status
		if (!is_null(params.getStatus())) {
			sqlParts.where.put("g.users_status="+(params.getStatus()?1:0));
		}

		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("usrgrp g", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("usrgrp g", params, sqlParts);
		}
		
		// limit
		if (params.getLimit() != null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
					ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("usrgrpid");
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// usrids
				if (isset(row.get("userid")) && is_null(params.getSelectUsers())) {
					if(!result.get(id).containsKey("users")){
						result.get(id).put("users", new ArrayList(0));
					}
					((CArray)result.get(id).get("users")).add(row.remove("userid"));
				}
				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
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
	
	public CArray<Map> getObjects(CArray groupData) {
		CArray usrgrpids = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> groups = DBselect(getSqlExecutor(),
			"SELECT g.usrgrpid"+
			" FROM usrgrp g"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "usrgrp", "g")+
			    " AND g.name="+sqlParts.marshalParam(Nest.value(groupData,"name").asString()),
			 sqlParts.getNamedParams()
		);
		for (Map group : groups) {
			Nest.value(usrgrpids,group.get("usrgrpid")).$(Nest.value(group,"usrgrpid").$());
		}

		CArray<Map> result = null;
		if (!empty(usrgrpids)) {
			CUserGroupGet options = new CUserGroupGet();
			options.setUsrgrpIds(usrgrpids.valuesAsLong());
			options.setOutput(API_OUTPUT_EXTEND);
			result = get(options);
		} else {
			result = array();
		}
		return result;
	}
	
	@Override
	public boolean exists(CArray object) {
		CUserGroupGet options = new CUserGroupGet();
		options.setFilter("name",Nest.value(object,"name").asString());
		options.setOutput(new String[]{"usrgrpid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	@Override
	public CArray<Long[]> create(CArray<Map> usrgrps) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can delete user groups."));
		}

		CArray<Map> insert = array();

		for (Entry<Object, Map> e : usrgrps.entrySet()) {
            Object gnum = e.getKey();
            Map usrgrp = e.getValue();
            CArray usrgrpDbFields = map(
				"name", null
			);
			if (!check_db_fields(usrgrpDbFields, usrgrp)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect parameters for user group."));
			}

			if (exists(map("name", Nest.value(usrgrp,"name").$()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("User group")+" [ "+Nest.value(usrgrp,"name").$()+" ] "+_("already exists"));
			}
			Nest.value(insert, gnum).$(usrgrp);
		}
		CArray<Long> usrgrpids = insert("usrgrp", Clone.deepcopy(insert));

		for (Entry<Object, Map> e : usrgrps.entrySet()) {
            Object gnum = e.getKey();
            Map usrgrp = e.getValue();
            CArray massAdd = array();
			if (isset(usrgrp,"userids")) {
				Nest.value(massAdd,"userids").$(Nest.value(usrgrp,"userids").$());
			}
			if (isset(usrgrp,"rights")) {
				Nest.value(massAdd,"rights").$(Nest.value(usrgrp,"rights").$());
			}
			if (!empty(massAdd)) {
				Nest.value(massAdd,"usrgrpids").$(usrgrpids.get(gnum));
				if (empty(massAdd(massAdd)))
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot add users."));
			}
		}

		return map("usrgrpids", usrgrpids.valuesAsLong());
	}
	
	@Override
	public CArray<Long[]> update(CArray<Map> usrgrps) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can delete user groups."));
		}

		for(Map usrgrp : usrgrps) {
			// checks if usergroup id is present
			CArray groupDbFields = map("usrgrpid", null);
			if (!check_db_fields(groupDbFields, usrgrp)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect parameters for user group."));
			}

			Nest.value(usrgrp,"usrgrpids").$(Nest.value(usrgrp,"usrgrpid").$());
			unset(usrgrp,"usrgrpid");
			if (empty(massUpdate(usrgrp))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot update group."));
			}
		}

		return map("usrgrpids", rda_objectValues(usrgrps, "usrgrpid").valuesAsLong());
	}
	
	public CArray<Long[]> massAdd(Map data) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can delete user groups."));
		}

		CArray<Long> usrgrpids = array_keys(array_flip(Nest.value(data,"usrgrpids").asCArray()));
		CArray<Long> userids = (isset(data,"userids") && !is_null(Nest.value(data,"userids").$())) ? Nest.value(data,"userids").asCArray() : null;
		CArray<Map> rights = (isset(data,"rights") && !is_null(Nest.value(data,"rights").$())) ? Nest.value(data,"rights").asCArray() : null;

		if (!is_null(userids)) {
			CUserGroupGet options = new CUserGroupGet();
			options.setUsrgrpIds(usrgrpids.valuesAsLong());
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> usrgrps = get(options);
			for(Map usrgrp : usrgrps) {
				if (((Nest.value(usrgrp,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED)
					|| (Nest.value(usrgrp,"users_status").asInteger() == GROUP_STATUS_DISABLED))
					&& uint_in_array(Nest.value(this.userData(),"userid").asLong(), userids)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("User cannot change status of himself"));
				}
			}

			CArray linkedUsers = array();
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT usrgrpid, userid" +
				" FROM users_groups" +
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "users_groups", "ug")+
				" AND " + sqlParts.where.dbConditionInt("usrgrpid", usrgrpids.valuesAsLong())+
				" AND " + sqlParts.where.dbConditionInt("userid", userids.valuesAsLong());
			CArray<Map> linkedUsersDb = DBselect(getSqlExecutor(), sql, sqlParts.getNamedParams());
			for(Map link : linkedUsersDb) {
				if (!isset(linkedUsers, link.get("usrgrpid"))){ 
					Nest.value(linkedUsers,link.get("usrgrpid")).$(array());
				}
				Nest.value(linkedUsers,link.get("usrgrpid"),link.get("userid")).$(1);
			}

			CArray<Map> usersInsert = array();
			for(Long usrgrpid : usrgrpids.valuesAsLong()) {
				for(Long userid : userids.valuesAsLong()) {
					if (!isset(Nest.value(linkedUsers,usrgrpid,userid).$())) {
						usersInsert.add(map(
							"usrgrpid", usrgrpid,
							"userid", userid
						));
					}
				}
			}
			insert("users_groups", usersInsert);
		}

		if (!is_null(rights)) {
			CArray linkedRights = array();
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT groupid,id" +
				" FROM rights" +
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "rights", "r")+
				    " AND " + sqlParts.where.dbConditionInt("groupid", usrgrpids.valuesAsLong())+
					" AND " + sqlParts.where.dbConditionInt("id", rda_objectValues(rights, "id").valuesAsLong());
			CArray<Map> linkedRightsDb = DBselect(getSqlExecutor(), sql,sqlParts.getNamedParams());
			for(Map link : linkedRightsDb) {
				if (!isset(linkedRights, link.get("groupid"))){
					Nest.value(linkedRights,link.get("groupid")).$(array());
				}
				Nest.value(linkedRights,link.get("groupid"),link.get("id")).$(1);
			}

			CArray<Map> rightInsert = array();
			for(Long usrgrpid : usrgrpids.valuesAsLong()) {
				for(Map right : rights) {
					if (!isset(Nest.value(linkedRights,usrgrpid,right.get("id")).$())) {
						rightInsert.add(map(
							"groupid", usrgrpid,
							"permission", Nest.value(right,"permission").$(),
							"id", Nest.value(right,"id").$()
						));
					}
				}
			}
			insert("rights", rightInsert);
		}

		return map("usrgrpids", usrgrpids.valuesAsLong());
	}
	
	/**
	 * Mass update user group.
	 * Checks for permissions - only super admins can change user groups.
	 * Changes name to a group if name and one user group id is provided.
	 * Links/unlinks users to user groups.
	 * Links/unlinks rights to user groups.
	 *
	 * @param array _data
	 * @param int|int[] _data['usrgrpids'] id or ids of user groups to be updated.
	 * @param string _data['name'] name to be set to a user group. Only one host group id can be passed at a time!
	 * @param null|int|int[] _data['userids'] user ids to link to given user groups. Missing user ids will be unlinked from user groups.
	 * @param null|array _data['rights'] rights to link to given user groups. Missing rights will be unlinked from user groups.
	 * @param int _data['rights']['id'] id of right.
	 * @param int _data['rights']['permission'] permission level of right.
	 * @return 
	 *
	 * @return int[] array['usrgrpids'] returns passed user group ids
	 */
	public CArray<Long[]> massUpdate(Map data) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can delete user groups."));
		}

		CArray<Long> usrgrpids = Nest.value(data,"usrgrpids").asCArray();

		if (count(usrgrpids) == 0) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Missing parameter: usrgrpids."));
		}

		// _data['name'] parameter restrictions
		if (isset(data,"name")) {
			// same name can be set only to one hostgroup
			if (count(usrgrpids) > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Only one user group name can be changed at a time."));
			} else {
				// check if there already is hostgroup with this name, except current hostgroup
				CUserGroupGet options = new CUserGroupGet();
				options.setFilter("name", Nest.value(data,"name").asString());
				options.setOutput(new String[]{"usrgrpid"});
				options.setLimit(1);
				CArray<Map> groupExistses = get(options);
				Map groupExists = reset(groupExistses);
				if (!empty(groupExists) && (bccomp(Nest.value(groupExists,"usrgrpid").$(), usrgrpids.get(0)) != 0) ) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("User group \"%s\" already exists.", Nest.value(data,"name").$()));
				}
			}
		}

		// update usrgrp (user group) table if there is something to update
		CArray usrgrpTableUpdateData = Clone.deepcopy(CArray.valueOf(data));
		unset(usrgrpTableUpdateData,"usrgrpids");
		unset(usrgrpTableUpdateData,"userids");
		unset(usrgrpTableUpdateData,"rights");
		if (!empty(usrgrpTableUpdateData)) {
			for(Long usrgrpid : usrgrpids.valuesAsLong()) {
				update("usrgrp", array((Map)map(
					"values", usrgrpTableUpdateData,
					"where", map("usrgrpid", usrgrpid)
				)));
			}
		}

		// check that user do not add himself to a disabled user group
		// insert and delete user-userGroup links
		if (isset(data,"userids")) {
			CArray<String> userids = Nest.value(data,"userids").asCArray();

			// check whether user tries to add himself to a disabled user group
			CUserGroupGet options = new CUserGroupGet();
			options.setUsrgrpIds(usrgrpids.valuesAsLong());
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> usrgrps = get(options);
			if (uint_in_array(Nest.value(this.userData(),"userid").asString(), userids)) {
				for(Map usrgrp : usrgrps) {
					if ((Nest.value(usrgrp,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED)
						|| (Nest.value(usrgrp,"users_status").asInteger() == GROUP_STATUS_DISABLED)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("User cannot add himself to a disabled group or a group with disabled GUI access."));
					}
				}
			}

			// get already linked users
			CArray linkedUsers = array();
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT usrgrpid,userid" +
				" FROM users_groups" +
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "users_groups", "ug")+
				    " AND " + sqlParts.where.dbConditionInt("usrgrpid", usrgrpids.valuesAsLong());
			CArray<Map> linkedUsersDb = DBselect(getSqlExecutor(), sql, sqlParts.getNamedParams());
			for(Map link : linkedUsersDb) {
				if (!isset(linkedUsers,link.get("usrgrpid"))) {
					Nest.value(linkedUsers,link.get("usrgrpid")).$(array());
				}
				Nest.value(linkedUsers,link.get("usrgrpid"),link.get("userid")).$(1);
			}

			// get user-userGroup links to insert and get user ids to unlink
			CArray<Map> userUsergroupLinksToInsert = array();
			CArray<Long> userIdsToUnlink = array();
			for(Long usrgrpid : usrgrpids.valuesAsLong()) {
				for(Long userid : userids.valuesAsLong()) {
					if (!isset(Nest.value(linkedUsers,usrgrpid,userid).$())) {
						userUsergroupLinksToInsert.add(map(
							"usrgrpid", usrgrpid,
							"userid", userid
						));
					}
					unset(linkedUsers,usrgrpid,userid);
				}
				if (isset(linkedUsers,usrgrpid) && !empty(Nest.value(linkedUsers,usrgrpid).$())) {
					userIdsToUnlink = array_merge(userIdsToUnlink, array_keys(Nest.value(linkedUsers,usrgrpid).asCArray()));
				}
			}

			// link users to user groups
			if (!empty(userUsergroupLinksToInsert)) {
				insert("users_groups", userUsergroupLinksToInsert);
			}

			// unlink users from user groups
			if (!empty(userIdsToUnlink)) {
				delete("users_groups", (Map)map(
					"userid", userIdsToUnlink.valuesAsLong(),
					"usrgrpid", usrgrpids.valuesAsLong()
				));
			}
		}

		// link rights to user groups
		// update permissions to right-userGroup links
		// unlink rights from user groups (permissions)
		if (isset(data,"rights")) {
			CArray<Map> rights = Nest.value(data,"rights").asCArray();

			// get already linked rights
			CArray linkedRights = array();
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT groupid,permission,id" +
				" FROM rights" +
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "rights", "r")+
				    " AND "+sqlParts.dual.dbConditionInt("groupid", usrgrpids.valuesAsLong());
			CArray<Map> linkedRightsDb = DBselect(getSqlExecutor(), sql,sqlParts.getNamedParams());
			for (Map link : linkedRightsDb) {
				if (!isset(linkedRights,link.get("groupid"))) {
					Nest.value(linkedRights, link.get("groupid")).$(array());
				}
				Nest.value(linkedRights, link.get("groupid"), link.get("id")).$(Nest.value(link,"permission").$());
			}

			// get right-userGroup links to insert
			// get right-userGroup links to update permissions
			// get rightIds to unlink rights from user groups
			CArray<Map> rightUsergroupLinksToInsert = array();
			CArray<Map> rightUsergroupLinksToUpdate = array();
			CArray<Long> rightIdsToUnlink = array();
			for(Long usrgrpid : usrgrpids.valuesAsLong()) {
				for(Map right : rights) {
					if (!isset(Nest.value(linkedRights,usrgrpid,right.get("id")).$())) {
						rightUsergroupLinksToInsert.add(map(
							"groupid", usrgrpid,
							"id", Nest.value(right,"id").$(),
							"permission", Nest.value(right,"permission").$()
						));
					} else if (Nest.value(linkedRights,usrgrpid,right.get("id")).asInteger() != Nest.value(right,"permission").asInteger()) {
						rightUsergroupLinksToUpdate.add(map(
							"values", map("permission", Nest.value(right,"permission").$()),
							"where", map("groupid", usrgrpid, "id", Nest.value(right,"id").$())
						));
					}
					unset(linkedRights,usrgrpid,right.get("id"));
				}

				if (isset(linkedRights,usrgrpid) && !empty(Nest.value(linkedRights,usrgrpid).$())) {
					rightIdsToUnlink = array_merge(rightIdsToUnlink, array_keys(Nest.value(linkedRights,usrgrpid).asCArray()));
				}
			}

			// link rights to user groups
			if (!empty(rightUsergroupLinksToInsert)) {
				insert("rights", rightUsergroupLinksToInsert);
			}

			// unlink rights from user groups
			if (!empty(rightIdsToUnlink)) {
				delete("rights", (Map)map(
					"id", rightIdsToUnlink.valuesAsLong(),
					"groupid", usrgrpids.valuesAsLong()
				));
			}

			// update right-userGroup permissions
			if (!empty(rightUsergroupLinksToUpdate)) {
				update("rights", rightUsergroupLinksToUpdate);
			}
		}

		return map("usrgrpids", usrgrpids.valuesAsLong());
	}

	@Override
	public CArray<Long[]> delete(Long... userGroupIds) {
		validateDelete(userGroupIds);

		CArray operationIds = array();
		CArray delelteOperationIds = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbOperations = DBselect(getSqlExecutor(),
			"SELECT DISTINCT om.operationid" +
			" FROM opmessage_grp om" +
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "opmessage_grp", "om")+
			    " AND " + sqlParts.where.dbConditionInt("om.usrgrpid", userGroupIds),
			sqlParts.getNamedParams()
		);
		for (Map dbOperation : dbOperations) {
			Nest.value(operationIds,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
		}

		sqlParts = new SqlBuilder();
		dbOperations = DBselect(getSqlExecutor(),
			"SELECT DISTINCT o.operationid" +
			" FROM operations o" +
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "operations", "o")+
			   " AND " +sqlParts.dual.dbConditionInt("o.operationid", operationIds.valuesAsLong()) +
			   " AND NOT EXISTS(SELECT om.opmessage_grpid FROM opmessage_grp om WHERE om.tenantid=o.tenantid AND om.operationid=o.operationid)",
			   sqlParts.getNamedParams()
		);
		for(Map dbOperation : dbOperations) {
			Nest.value(delelteOperationIds,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
		}

		delete("opmessage_grp", (Map)map("usrgrpid", userGroupIds));
		delete("operations", (Map)map("operationid", delelteOperationIds.valuesAsLong()));
		delete("rights", (Map)map("groupid", userGroupIds));
		delete("users_groups", (Map)map("usrgrpid", userGroupIds));
		delete("usrgrp", (Map)map("usrgrpid", userGroupIds));

		return map("usrgrpids", userGroupIds);
	}
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException
	 *
	 * @param array _userGroupIds
	 */
	protected void validateDelete(Long... userGroupIds) {
		if (Nest.value(this.userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can delete user groups."));
		}

		if (empty(userGroupIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CUserGroupGet options = new CUserGroupGet();
		options.setOutput(new String[]{"usrgrpid", "name"});
		options.setUsrgrpIds(TArray.as(userGroupIds).asLong());
		options.setPreserveKeys(true);
		CArray<Map> dbUserGroups = get(options);

		// check if user group is used in scripts
		CScriptGet soptions = new CScriptGet();
		soptions.setOutput(new String[]{"scriptid", "name", "usrgrpid"});
		soptions.setUsrgrpIds(TArray.as(userGroupIds).asLong());
		soptions.setNopermissions(true);
		CArray<Map> dbScripts = API.Script(this.idBean, this.getSqlExecutor()).get(soptions);

		for(Map dbScript : dbScripts) {
			if (Nest.value(dbScript,"usrgrpid").asInteger() == 0) {
				continue;
			}

			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
				"User group \"%1$s\" is used in script \"%2$s\".",
				Nest.value(dbUserGroups,dbScript.get("usrgrpid"),"name").$(),
				Nest.value(dbScript,"name").$()
			));
		}

		// check if user group is used in config
		Map<String, Object> config = select_config(this.idBean, getSqlExecutor());

		if (isset(dbUserGroups,config.get("alert_usrgrpid"))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
				"User group \"%1$s\" is used in configuration for database down messages.",
				Nest.value(dbUserGroups,config.get("alert_usrgrpid"),"name").$()
			));
		}

		// check if user group is used in users with 1 user group
		CUserGet uoptions = new CUserGet();
		uoptions.setOutput(new String[]{"userid", "usrgrpid", "alias"});
		uoptions.setUsrgrpIds(TArray.as(userGroupIds).asLong());
		uoptions.setSelectUsrgrps(new String[]{"usrgrpid"});
		CArray<Map> dbUsers = API.User(this.idBean, this.getSqlExecutor()).get(uoptions);

		for(Map dbUser : dbUsers) {
			if (count(Nest.value(dbUser,"usrgrps").asCArray()) == 1) {
				Map dbGroup = reset(Nest.value(dbUser,"usrgrps").asCArray());
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"User group \"%1$s\" is the only group that user \"%2$s\" belongs to.",
					Nest.value(dbUserGroups,dbGroup.get("usrgrpid"),"name").$(),
					Nest.value(dbUser,"alias").$()
				));
			}
		}
	}

	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CUserGroupGet options = new CUserGroupGet();
		options.setUsrgrpIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CUserGroupGet options = new CUserGroupGet();
		options.setUsrgrpIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void addRelatedObjects(CUserGroupGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);

		if(!is_null(params.getSelectUsers()) && !API_OUTPUT_COUNT.equals(params.getSelectUsers())){
			CRelationMap relationMap = createRelationMap(result, "usrgrpid", "userid", "users_groups");
			CUserGet uparams = new CUserGet();
			uparams.setOutput(params.getSelectUsers());
			uparams.setUserIds(relationMap.getRelatedStringIds());
			if(API_OUTPUT_EXTEND.equals(params.getSelectUsers())){
				uparams.setAccess(true);
			}
			uparams.setPreserveKeys(true);
			CArray<Map> datas = API.User(this.idBean, getSqlExecutor()).get(uparams);
			relationMap.mapMany(result, datas, "users", params.getLimitSelects());
		}
	}

}
