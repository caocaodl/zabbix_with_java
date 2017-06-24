package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER_GROUP;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GROUP_DEBUG_MODE_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.UsersUtil.change_group_gui_access;
import static com.isoft.iradar.inc.UsersUtil.change_group_status;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class UsergrpsAction extends RadarBaseAction {
	
	private CArray<Map> dbUserGroup;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of user groups"));
		page("file", "usergrps.action");
		page("hist_arg", new String[] { "config" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"grpaction",					array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			// group
			"usrgrpid",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({grpaction})&&isset({form})&&{form}==\"update\""),
			"group_groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"selusrgrp",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"gname",						array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})"),
			"users",							array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"gui_access",				array(T_RDA_INT, O_OPT, null,	IN("0,1,2"),"isset({save})"),
			"users_status",				array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"debug_mode",				array(T_RDA_INT, O_OPT, null,	IN("1"),	null),
			"new_right",					array(T_RDA_STR, O_OPT, null,	null,		null),
			"right_to_del",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"group_users_to_del",	array(T_RDA_STR, O_OPT, null,	null,		null),
			"group_users",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"group_rights",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"set_users_status",		array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"set_gui_access",			array(T_RDA_INT, O_OPT, null,	IN("0,1,2"),null),
			"set_debug_mode",		array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			// actions
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"register",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, IN("\"add permission\",\"delete permission\""), null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete_selected",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_user_group",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_user_media",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_read_only",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_read_write",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_deny",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_group_user",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_read_only",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_read_write",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_deny",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"change_password",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS,		 null,	null),
			// form
			"form",							array(T_RDA_STR, O_OPT, P_SYS,		 null,	null),
			"form_refresh",				array(T_RDA_STR, O_OPT, null,		 null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);

		Nest.value(_REQUEST,"users_status").$(isset(Nest.value(_REQUEST,"users_status").$()) ? 0 : 1);
		Nest.value(_REQUEST,"debug_mode").$(get_request("debug_mode", 0));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST,"usrgrpid")) {
			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setUsrgrpIds(Nest.value(_REQUEST,"usrgrpid").asLong());
			ugoptions.setOutput(API_OUTPUT_EXTEND);
			dbUserGroup = API.UserGroup(getIdentityBean(), executor).get(ugoptions);
			if (empty(dbUserGroup)) {
				access_deny();
			}
		} else if (isset(_REQUEST,"go")) {
			if (!isset(_REQUEST,"group_groupid") || !isArray(Nest.value(_REQUEST,"group_groupid").$())) {
				access_deny();
			} else {
				CUserGroupGet ugoptions = new CUserGroupGet();
				ugoptions.setUsrgrpIds(Nest.array(_REQUEST,"group_groupid").asLong());
				ugoptions.setCountOutput(true);
				long dbUserGroupCount = API.UserGroup(getIdentityBean(), executor).get(ugoptions);
				if (dbUserGroupCount != count(Nest.value(_REQUEST,"group_groupid").$())) {
					access_deny();
				}
			}
		}
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"del_deny") && isset(Nest.value(_REQUEST,"right_to_del","deny").$())) {
			Nest.value(_REQUEST,"group_rights").$(get_request("group_rights", array()));

			for(Object name : Nest.value(_REQUEST,"right_to_del","deny").asCArray()) {
				if (!isset(Nest.value(_REQUEST,"group_rights",name).$())) {
					continue;
				}

				if (Nest.value(_REQUEST,"group_rights",name,"permission").asInteger() == PERM_DENY) {
					unset(Nest.value(_REQUEST,"group_rights").asCArray(),name);
				}
			}
		} else if (isset(_REQUEST,"del_read_only") && isset(Nest.value(_REQUEST,"right_to_del","read_only").$())) {
			Nest.value(_REQUEST,"group_rights").$(get_request("group_rights", array()));

			for(Object name : Nest.value(_REQUEST,"right_to_del","read_only").asCArray()) {
				if (!isset(Nest.value(_REQUEST,"group_rights",name).$())) {
					continue;
				}

				if (Nest.value(_REQUEST,"group_rights",name,"permission").asInteger() == PERM_READ) {
					unset(Nest.value(_REQUEST,"group_rights").asCArray(),name);
				}
			}
		} else if (isset(_REQUEST,"del_read_write") && isset(Nest.value(_REQUEST,"right_to_del","read_write").$())) {
			Nest.value(_REQUEST,"group_rights").$(get_request("group_rights", array()));

			for(Object name : Nest.value(_REQUEST,"right_to_del","read_write").asCArray()) {
				if (!isset(Nest.value(_REQUEST,"group_rights",name).$())) {
					continue;
				}

				if (Nest.value(_REQUEST,"group_rights",name,"permission").asInteger() == PERM_READ_WRITE) {
					unset(Nest.value(_REQUEST,"group_rights").asCArray(),name);
				}
			}
		} else if (isset(_REQUEST,"new_right")) {
			Nest.value(_REQUEST,"group_rights").$(get_request("group_rights", array()));

			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(_REQUEST,"new_right").asCArray()).entrySet()) {
			    Object id = e.getKey();
			    Map right = e.getValue();
				Nest.value(_REQUEST,"group_rights",id).$(map(
					"name", Nest.value(right,"name").$(),
					"permission", Nest.value(right,"permission").$(),
					"id", id
				));
			}
		} else if (isset(_REQUEST,"save")) {
			final Map userGroup = map(
				"name", Nest.value(_REQUEST,"gname").$(),
				"users_status", Nest.value(_REQUEST,"users_status").$(),
				"gui_access", Nest.value(_REQUEST,"gui_access").$(),
				"debug_mode", Nest.value(_REQUEST,"debug_mode").$(),
				"userids", get_request("group_users", array()),
				"rights", array_values(get_request("group_rights", array()))
			);

			boolean result;
			int action;
			if (isset(_REQUEST,"usrgrpid")) {
				Nest.value(userGroup,"usrgrpid").$(Nest.value(_REQUEST,"usrgrpid").$());
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.UserGroup(getIdentityBean(), executor).update(array(userGroup)));
					}
				});
				action = AUDIT_ACTION_UPDATE;
				show_messages(result, _("Group updated"), _("Cannot update group"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.UserGroup(getIdentityBean(), executor).create(array(userGroup)));
					}
				});
				action = AUDIT_ACTION_ADD;
				show_messages(result, _("Group added"), _("Cannot add group"));
			}

			if (result) {
				add_audit(getIdentityBean(), executor,action, AUDIT_RESOURCE_USER_GROUP, "Group name ["+Nest.value(_REQUEST,"gname").asString()+"]");
				unset(_REQUEST,"form");
				clearCookies(result);
			}
		} else if (isset(_REQUEST,"delete")) {
			DBstart(executor);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.UserGroup(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"usrgrpid").asLong()));
				}
			});
			
			result = DBend(executor, result);
			
			show_messages(result, _("Group deleted"), _("Cannot delete group"));
			clearCookies(result);

			if (result) {
				Map group = reset(dbUserGroup);

				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_USER_GROUP, "Group name ["+Nest.value(group,"name").asString()+"]");
				unset(_REQUEST,"usrgrpid");
				unset(_REQUEST,"form");
			}
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			final CArray groupIds = get_request("group_groupid", array());
			CArray<Map> groups = array();

			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbGroups = DBselect(executor,
				"SELECT ug.usrgrpid,ug.name"+
				" FROM usrgrp ug"+
				" WHERE "+sqlParts.dual.dbConditionInt("ug.usrgrpid", groupIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map group : dbGroups) {
				Nest.value(groups,group.get("usrgrpid")).$(group);
			}

			if (!empty(groups)) {
				DBstart(executor);
				
				boolean goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.UserGroup(getIdentityBean(), executor).delete(groupIds.valuesAsLong()));
					}
				});

				if (goResult) {
					for (Map group : groups) {
						add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_USER_GROUP, "Group name ["+Nest.value(group,"name").asString()+"]");
					}
				}
				
				goResult = DBend(executor, goResult);
				
				show_messages(goResult, _("Group deleted"), _("Cannot delete group"));
				clearCookies(goResult);
			}
		} else if ("set_gui_access".equals(Nest.value(_REQUEST,"go").asString())) {
			final CArray groupIds = get_request("group_groupid", get_request("usrgrpid",array()));

			CArray<Map> groups = array();
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbGroups = DBselect(executor,
				"SELECT ug.usrgrpid,ug.name"+
				" FROM usrgrp ug"+
				" WHERE "+sqlParts.dual.dbConditionInt("ug.usrgrpid", groupIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map group : dbGroups) {
				Nest.value(groups,group.get("usrgrpid")).$(group);
			}

			if (!empty(groups)) {
				DBstart(executor);
				
				boolean goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return change_group_gui_access(getIdentityBean(), executor, groupIds.valuesAsLong(), Nest.value(_REQUEST,"set_gui_access").asInteger());
					}
				});

				if (goResult) {
					int auditAction = (Nest.value(_REQUEST,"set_gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED) ? AUDIT_ACTION_DISABLE : AUDIT_ACTION_ENABLE;
					for (Map group : groups) {
						add_audit(getIdentityBean(), executor,auditAction, AUDIT_RESOURCE_USER_GROUP, "GUI access for group name ["+Nest.value(group,"name").$()+"]");
					}
				}
				
				goResult = DBend(executor, goResult);

				show_messages(goResult, _("Frontend access updated"), _("Cannot update frontend access"));
				clearCookies(goResult);
			}
		/*} else if (false && str_in_array(Nest.value(_REQUEST,"go").$(), array("enable_debug", "disable_debug"))) {//关闭Debug mode功能
			final CArray groupIds = get_request("group_groupid", get_request("usrgrpid",array()));

			final int setDebugMode = ("enable_debug".equals(Nest.value(_REQUEST,"go").asString())) ? GROUP_DEBUG_MODE_ENABLED : GROUP_DEBUG_MODE_DISABLED;

			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> groups = array();
			CArray<Map> dbGroup = DBselect(executor,
				"SELECT ug.usrgrpid,ug.name"+
				" FROM usrgrp ug"+
				" WHERE "+sqlParts.dual.dbConditionInt("ug.usrgrpid", groupIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map group : dbGroup) {
				Nest.value(groups,group.get("usrgrpid")).$(group);
			}

			if (!empty(groups)) {
				DBstart(executor);
				
				boolean goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return change_group_debug_mode(executor,groupIds.valuesAsLong(), setDebugMode);
					}
				});

				if (goResult) {
					int auditAction = (setDebugMode == GROUP_DEBUG_MODE_DISABLED) ? AUDIT_ACTION_DISABLE : AUDIT_ACTION_ENABLE;

					for(Map group : groups) {
						add_audit(getIdentityBean(), executor,auditAction, AUDIT_RESOURCE_USER_GROUP, "Debug mode for group name ["+Nest.value(group,"name").asString()+"]");
					}
				}
				
				goResult = DBend(executor, goResult);

				show_messages(goResult, _("Debug mode updated"), _("Cannot update debug mode"));
				clearCookies(goResult);
			}*/
		} else if (str_in_array(get_request("go"), array("enable_status", "disable_status"))) {
			final CArray groupIds = get_request("group_groupid", get_request("usrgrpid",array()));

			boolean enable = ("enable_status".equals(get_request("go")));
			final int status = enable ? GROUP_STATUS_ENABLED : GROUP_STATUS_DISABLED;
			int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;
			CArray<Map> groups = array();

			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbGroups = DBselect(executor,
				"SELECT ug.usrgrpid,ug.name"+
				" FROM usrgrp ug"+
				" WHERE "+sqlParts.dual.dbConditionInt("ug.usrgrpid", groupIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map group : dbGroups) {
				Nest.value(groups,group.get("usrgrpid")).$(group);
			}
			int updated = count(groups);

			if (!empty(groups)) {
				DBstart(executor);
				
				boolean result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return change_group_status(getIdentityBean(), executor,groupIds.valuesAsLong(), status);
					}
				});

				if (result) {
					for(Map group : groups) {
						add_audit(getIdentityBean(), executor,auditAction, AUDIT_RESOURCE_USER_GROUP, "User status for group name ["+Nest.value(group,"name").asString()+"]");
					}
				}

				String messageSuccess = enable
					? _n("User group enabled", "User groups enabled", updated)
					: _n("User group disabled", "User groups disabled", updated);
				String messageFailed = enable
					? _n("Cannot enable user group", "Cannot enable user groups", updated)
					: _n("Cannot disable user group", "Cannot disable user groups", updated);

				result = DBend(executor, result);
					
				show_messages(result, messageSuccess, messageFailed);
				clearCookies(result);
			}
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"usrgrpid", get_request("usrgrpid"),
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh", 0)
			);

			if (isset(_REQUEST,"usrgrpid")) {
				Nest.value(data,"usrgrp").$(reset(dbUserGroup));
			}

			if (isset(_REQUEST,"usrgrpid") && !isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"name").$(Nest.value(data,"usrgrp","name").$());
				Nest.value(data,"users_status").$(Nest.value(data,"usrgrp","users_status").$());
				Nest.value(data,"gui_access").$(Nest.value(data,"usrgrp","gui_access").$());
				Nest.value(data,"debug_mode").$(Nest.value(data,"usrgrp","debug_mode").$());

				// group users
				Nest.value(data,"group_users").$(array());

				Map params = new HashMap();
				params.put("usrgrpid", Nest.value(data,"usrgrpid").$());
				CArray<Map> dbUsers = DBselect(executor,
					"SELECT DISTINCT u.userid "+
					" FROM users u,users_groups ug "+
					" WHERE u.userid=ug.userid "+
						" AND ug.usrgrpid=#{usrgrpid}",
					params
				);
				for(Map dbUser : dbUsers) {
					Nest.value(data,"group_users",dbUser.get("userid")).$(Nest.value(dbUser,"userid").$());
				}

				// group rights
				Nest.value(data,"group_rights").$(array());

				params.put("groupid", Nest.value(data,"usrgrpid").$());
				CArray<Map> dbRights = DBselect(executor,
					"SELECT r.*,g.name AS name"+
					" FROM groups g"+
						" LEFT JOIN rights r ON r.id=g.groupid"+
					" WHERE r.groupid=#{groupid}",
					params
				);
				for(Map dbRight : dbRights) {
					Nest.value(data,"group_rights",dbRight.get("id")).$(map(
						"permission", Nest.value(dbRight,"permission").$(),
						"name", Nest.value(dbRight,"name").$(),
						"id", Nest.value(dbRight,"id").$()
					));
				}
			} else {
				Nest.value(data,"name").$(get_request("gname", ""));
				Nest.value(data,"users_status").$(get_request("users_status", GROUP_STATUS_ENABLED));
				Nest.value(data,"gui_access").$(get_request("gui_access", GROUP_GUI_ACCESS_SYSTEM));
				Nest.value(data,"debug_mode").$(get_request("debug_mode", GROUP_DEBUG_MODE_DISABLED));
				Nest.value(data,"group_users").$(get_request("group_users", array()));
				Nest.value(data,"group_rights").$(get_request("group_rights", array()));
			}

			Nest.value(data,"selected_usrgrp").$(get_request("selusrgrp", 0));

			// sort group rights
			order_result(Nest.value(data,"group_rights").asCArray(), "name");

			// get users
			SqlBuilder sqlParts = new SqlBuilder();
			String sqlFrom;
			String sqlWhere;
			if (Nest.value(data,"selected_usrgrp").asInteger() > 0) {
				sqlFrom = ",users_groups g";
				sqlWhere =
					" WHERE "+sqlParts.dual.dbConditionInt("u.userid", Nest.array(data,"group_users").asLong())+
						" OR (u.userid=g.userid AND g.usrgrpid="+sqlParts.marshalParam(Nest.value(data,"selected_usrgrp").asLong())+")";
			} else {
				sqlFrom = "";
				sqlWhere = "";
			}

			Nest.value(data,"users").$(DBselect(executor,
				"SELECT DISTINCT u.userid,u.alias,u.name,u.surname"+
				" FROM users u"+sqlFrom+
					sqlWhere,
				sqlParts.getNamedParams()
			));
			order_result(Nest.value(data,"users").asCArray(), "alias");

			// get user groups
			Nest.value(data,"usergroups").$(DBselect(executor,
				"SELECT ug.usrgrpid,ug.name"+
				" FROM usrgrp ug"
			));
			order_result(Nest.value(data,"usergroups").asCArray(), "name");

			// render view
			CView userGroupsView = new CView("administration.usergroups.edit", data);
			userGroupsView.render(getIdentityBean(), executor);
			userGroupsView.show();
		} else {
			CArray data = map();

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			
			String sortfield = getPageSortField(getIdentityBean(), executor,"name");

			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setOutput(API_OUTPUT_EXTEND);
			ugoptions.setSelectUsers(API_OUTPUT_EXTEND);
			ugoptions.setSortfield(sortfield);
			ugoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> usergroups = API.UserGroup(getIdentityBean(), executor).get(ugoptions);
			Nest.value(data,"usergroups").$(usergroups);

			// sorting & paging
			order_result(usergroups, sortfield, getPageSortOrder(getIdentityBean(), executor));
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, usergroups, array("usrgrpid")));

			// render view
			CView userGroupsView = new CView("administration.usergroups.list", data);
			userGroupsView.render(getIdentityBean(), executor);
			userGroupsView.show();
		}
	}

}
