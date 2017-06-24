package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
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
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER_GROUP;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.THEME_DEFAULT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FormsUtil.getUserFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.PermUtil.authentication2str;
import static com.isoft.iradar.inc.PermUtil.getGroupAuthenticationType;
import static com.isoft.iradar.inc.PermUtil.getUserAuthenticationType;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.UsersUtil.add_user_to_group;
import static com.isoft.iradar.inc.UsersUtil.remove_user_from_group;
import static com.isoft.iradar.inc.UsersUtil.unblock_user_login;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.core.RBase;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.operator.COperator;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;

public class UsersAction extends RadarBaseAction {
	
	private CArray<Map> users;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of users"));
		page("file", "users.action");
		page("hist_arg", new String[] {});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray themes = array_keys(RBase.getThemes());
		themes.add(THEME_DEFAULT);
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			// users
			"userid",						array(T_RDA_INT, O_NO,	P_SYS,	NOT_EMPTY,		"isset({form})&&{form}==\"update\""),
			"group_userid",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"filter_usrgrpid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"alias",							array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Alias")),
			"name",						array(T_RDA_STR, O_OPT, null,	null,		null, _("Name")),
			"surname",					array(T_RDA_STR, O_OPT, null,	null,		null, _("Surname")),
			"password1",				array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({form})&&{form}!=\"update\"&&isset({change_password})"),
			"password2",				array(T_RDA_STR, O_OPT, null,	null,		"isset({save})&&isset({form})&&{form}!=\"update\"&&isset({change_password})"),
			"user_type",					array(T_RDA_INT, O_OPT, null,	IN("1,2,3"),"isset({save})"),
			"user_groups",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null),
			"user_groups_to_del",	array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"user_medias",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	null),
			"user_medias_to_del",	array(T_RDA_STR, O_OPT, null,	DB_ID,		null),
			"new_groups",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"new_media",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"enable_media",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"disable_media",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"lang",							array(T_RDA_STR, O_OPT, null,	null,		null),
			"theme",						array(T_RDA_STR, O_OPT, null,	IN("\""+implode("\",\"", themes)+"\""), "isset({save})"),
			"autologin",					array(T_RDA_INT, O_OPT, null,	IN("1"),	null),
			"autologout", 				array(T_RDA_INT, O_OPT, null,	BETWEEN(90, 10000), null, _("Auto-logout (min 90 seconds)")),
			"url",								array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"refresh",						array(T_RDA_INT, O_OPT, null,	BETWEEN(0, SEC_PER_HOUR), "isset({save})", _("Refresh (in seconds)")),
			"rows_per_page",			array(T_RDA_INT, O_OPT, null,	BETWEEN(1, 999999),"isset({save})", _("Rows per page")),
			// actions
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"register",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	IN("\"add permission\",\"delete permission\""), null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete_selected",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"del_user_group",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"del_user_media",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"del_group_user",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"change_password",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			// form
			"form",							array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			"form_refresh",				array(T_RDA_STR, O_OPT, null,			null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "alias", RDA_SORT_UP);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST,"userid")) {
			CUserGet uoptions = new CUserGet();
			uoptions.setUserIds(get_request("userid"));
			uoptions.setOutput(API_OUTPUT_EXTEND);
			uoptions.setEditable(true);
			users = API.User(getIdentityBean(), executor).get(uoptions);
			if (empty(users)) {
				access_deny();
			}
		}
		if (!empty(get_request("filter_usrgrpid")) && !API.UserGroup(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"filter_usrgrpid").asLong())) {
			access_deny();
		}

		if (isset(_REQUEST,"go")) {
			if (!isset(_REQUEST,"group_userid") || !isArray(Nest.value(_REQUEST,"group_userid").$())) {
				access_deny();
			} else {
				CUserGet uoptions = new CUserGet();
				uoptions.setUserIds(Nest.array(_REQUEST,"group_userid").asString());
				uoptions.setCountOutput(true);
				uoptions.setEditable(true);
				long usersChk = API.User(getIdentityBean(), executor).get(uoptions);
				if (usersChk != count(Nest.value(_REQUEST,"group_userid").$())) {
					access_deny();
				}
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));

		if (isset(_REQUEST,"new_groups")) {
			Nest.value(_REQUEST,"new_groups").$(get_request("new_groups", array()));
			Nest.value(_REQUEST,"user_groups").$(get_request("user_groups", array()));
			Nest.value(_REQUEST,"user_groups").$(
					COperator.CMapOperator.add(
						Nest.value(_REQUEST,"user_groups").asCArray(), 
						Nest.value(_REQUEST,"new_groups").asCArray()
					)
			);

			unset(_REQUEST,"new_groups");
		} else if (isset(_REQUEST,"new_media")) {
			Nest.value(_REQUEST,"user_medias").$(get_request("user_medias", array()));

			CArray user_medias = Nest.value(_REQUEST,"user_medias").asCArray();
			array_push(user_medias, Nest.value(_REQUEST,"new_media").$());
			Nest.value(_REQUEST,"user_medias").$(user_medias);
		}
		else if (isset(_REQUEST,"user_medias") && isset(_REQUEST,"enable_media")) {
			if (isset(Nest.value(_REQUEST,"user_medias",_REQUEST.get("enable_media")).$())) {
				Nest.value(_REQUEST,"user_medias",_REQUEST.get("enable_media"),"active").$(0);
			}
		} else if (isset(_REQUEST,"user_medias") && isset(_REQUEST,"disable_media")) {
			if (isset(Nest.value(_REQUEST,"user_medias",_REQUEST.get("disable_media")).$())) {
				Nest.value(_REQUEST,"user_medias",_REQUEST.get("disable_media"),"active").$(1);
			}
		} else if (isset(_REQUEST,"save")) {
			Map<String, Object> config = select_config(getIdentityBean(), executor);

			boolean isValid = true;

			CArray usrgrps = get_request("user_groups", array());

			// authentication type
			int authType;
			if (!empty(usrgrps)) {
				authType = getGroupAuthenticationType(getIdentityBean(), executor,usrgrps.valuesAsLong(), GROUP_GUI_ACCESS_INTERNAL);
			} else {
				authType = hasRequest("userid")
					? getUserAuthenticationType(getIdentityBean(), executor, Nest.as(get_request("userid")).asString(), GROUP_GUI_ACCESS_INTERNAL)
					: Nest.value(config,"authentication_type").asInteger();
			}

			// password validation
			if (authType != RDA_AUTH_INTERNAL) {
				if (hasRequest("password1")) {
					show_error_message(_s("Password is unavailable for users with %1$s.", authentication2str(authType)));
					isValid = false;
				} else {
					if (hasRequest("userid")) {
						Nest.value(_REQUEST,"password1").$(null);
						Nest.value(_REQUEST,"password2").$(null);
					} else {
						Nest.value(_REQUEST,"password1").$("iradar");
						Nest.value(_REQUEST,"password2").$("iradar");
					}
				}
			} else {
				Nest.value(_REQUEST,"password1").$(get_request("password1", null));
				Nest.value(_REQUEST,"password2").$(get_request("password2", null));
			}

			if (!StringUtils.equals(Nest.value(_REQUEST,"password1").asString(), Nest.value(_REQUEST,"password2").asString())) {
				if (isset(_REQUEST,"userid")) {
					show_error_message(_("Cannot update user. Both passwords must be equal."));
				} else {
					show_error_message(_("Cannot add user. Both passwords must be equal."));
				}
				isValid = false;
			} else if (isset(_REQUEST,"password1") && RDA_GUEST_USER.equals(Nest.value(_REQUEST,"alias").asString()) && !rda_empty(Nest.value(_REQUEST,"password1").$())) {
				show_error_message(_("For guest, password must be empty"));
				isValid = false;
			} else if (isset(_REQUEST,"password1") && (!RDA_GUEST_USER.equals(Nest.value(_REQUEST,"alias").asString())) && rda_empty(Nest.value(_REQUEST,"password1").$())) {
				show_error_message(_("Password should not be empty"));
				isValid = false;
			}

			if (isValid) {
				final Map user = map();
				Nest.value(user,"alias").$(get_request("alias"));
				Nest.value(user,"name").$(get_request("name"));
				Nest.value(user,"surname").$(get_request("surname"));
				Nest.value(user,"passwd").$(get_request("password1"));
				Nest.value(user,"url").$(get_request("url"));
				Nest.value(user,"autologin").$(get_request("autologin", 0));
				Nest.value(user,"autologout").$(get_request("autologout", 0));
				Nest.value(user,"theme").$(get_request("theme"));
				Nest.value(user,"refresh").$(get_request("refresh"));
				Nest.value(user,"rows_per_page").$(get_request("rows_per_page"));
				Nest.value(user,"type").$(get_request("user_type"));
				Nest.value(user,"user_medias").$(get_request("user_medias", array()));

				if (hasRequest("lang")) {
					Nest.value(user,"lang").$(get_request("lang"));
				}

				usrgrps = rda_toObject(usrgrps, "usrgrpid");
				Nest.value(user,"usrgrps").$(usrgrps);

				boolean result;
				int action;
				if (isset(_REQUEST,"userid")) {
					action = AUDIT_ACTION_UPDATE;
					Nest.value(user,"userid").$(Nest.value(_REQUEST,"userid").$());
					
					DBstart(executor);

					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.User(getIdentityBean(), executor).update(array(user)));
						}
					});
					if (result) {
						result = Call(new Wrapper<Boolean>() {
							@Override
							protected Boolean doCall() throws Throwable {
								return !empty(API.User(getIdentityBean(), executor).updateMedia(map(
									"users", user,
									"medias", Nest.value(user,"user_medias").$()
								)));
							}
						});
					}
					result = DBend(executor, result);
					
					show_messages(result, _("User updated"), _("Cannot update user"));
				} else {
					DBstart(executor);
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.User(getIdentityBean(), executor).create(array(user)));
						}
					});
					result = DBend(executor, result);
					action = AUDIT_ACTION_ADD;
					show_messages(result, _("User added"), _("Cannot add user"));
				}

				if (result) {
					add_audit(getIdentityBean(), executor,action, AUDIT_RESOURCE_USER, "User alias ["+Nest.value(_REQUEST,"alias").asString()+"] name ["+Nest.value(_REQUEST,"name").asString()+"] surname ["+Nest.value(_REQUEST,"surname").asString()+"]");
					unset(_REQUEST,"form");
					clearCookies(result);
				}
			}
		} else if (isset(_REQUEST,"del_user_media")) {
			for(Object mediaId : get_request("user_medias_to_del", array())) {
				if (isset(Nest.value(_REQUEST,"user_medias",mediaId).$())) {
					unset(Nest.value(_REQUEST,"user_medias").asCArray(),mediaId);
				}
			}
		} else if (isset(_REQUEST,"del_user_group")) {
			for(Object groupId : get_request("user_groups_to_del", array())) {
				if (isset(Nest.value(_REQUEST,"user_groups",groupId).$())) {
					unset(Nest.value(_REQUEST,"user_groups").asCArray(),groupId);
				}
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"userid")) {
			final Map user = reset(users);

			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.User(getIdentityBean(), executor).delete(Nest.value(user,"userid").asString()));
				}
			});

			show_messages(result, _("User deleted"), _("Cannot delete user"));
			clearCookies(result);

			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_USER, "User alias ["+Nest.value(user,"alias").$()+"] name ["+Nest.value(user,"name").$()+"] surname ["+Nest.value(user,"surname").$()+"]");
				unset(_REQUEST,"userid");
				unset(_REQUEST,"form");
			}
		} else if (isset(_REQUEST,"grpaction") && isset(_REQUEST,"usrgrpid") && isset(_REQUEST,"userid") && Nest.value(_REQUEST,"grpaction").asInteger() == 1) {
			Map user = reset(users);

			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setUsrgrpIds(Nest.value(_REQUEST,"usrgrpid").asLong());
			ugoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> groups = API.UserGroup(getIdentityBean(), executor).get(ugoptions);
			Map group = reset(groups);
			
			DBstart(executor);			
			boolean result =!empty(group);
			result &= Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return add_user_to_group(getIdentityBean(), executor,Nest.value(_REQUEST,"userid").asLong(), Nest.value(_REQUEST,"usrgrpid").asLong());
				}
			});
			result = DBend(executor, result);

			show_messages(result, _("User updated"), _("Cannot update user"));
			clearCookies(result);

			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_ADD, AUDIT_RESOURCE_USER_GROUP, "User alias ["+Nest.value(user,"alias").$()+"] name ["+Nest.value(user,"name").$()+"] surname ["+Nest.value(user,"surname").$()+"]");
				unset(_REQUEST,"usrgrpid");
				unset(_REQUEST,"userid");
			}

			unset(_REQUEST,"grpaction");
			unset(_REQUEST,"form");
		} else if (isset(_REQUEST,"grpaction") && isset(_REQUEST,"usrgrpid") && isset(_REQUEST,"userid") && Nest.value(_REQUEST,"grpaction").asInteger() == 0) {
			Map user = reset(users);

			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setUsrgrpIds(Nest.value(_REQUEST,"usrgrpid").asLong());
			ugoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> groups = API.UserGroup(getIdentityBean(), executor).get(ugoptions);
			Map group = reset(groups);

			DBstart(executor);
			boolean result = !empty(group);
			result &= Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return remove_user_from_group(getIdentityBean(), executor, Nest.value(_REQUEST,"userid").asLong(), Nest.value(_REQUEST,"usrgrpid").asLong());
				}
			});
			result = DBend(executor, result);

			show_messages(result, _("User updated"), _("Cannot update user"));
			clearCookies(result);

			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_USER_GROUP, "User alias ["+Nest.value(user,"alias").$()+"] name ["+Nest.value(user,"name").$()+"] surname ["+Nest.value(user,"surname").$()+"]");
				unset(_REQUEST,"usrgrpid");
				unset(_REQUEST,"userid");
			}

			unset(_REQUEST,"grpaction");
			unset(_REQUEST,"form");
		} else if ("unblock".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_userid")) {
			CArray groupUserId = get_request("group_userid", array());

			DBstart(executor);
			
			boolean goResult = unblock_user_login(getIdentityBean(), executor,groupUserId.valuesAsLong());

			if (goResult) {
				CUserGet uoptions = new CUserGet();
				uoptions.setUserIds(groupUserId.valuesAsString());
				uoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> users = API.User(getIdentityBean(), executor).get(uoptions);

				for(Map user : users) {
					info("User "+Nest.value(user,"alias").$()+" unblocked");
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_USER, "Unblocked user alias ["+Nest.value(user,"alias").$()+"] name ["+Nest.value(user,"name").$()+"] surname ["+Nest.value(user,"surname").$()+"]");
				}
			}
			
			goResult = DBend(executor, goResult);

			show_messages(goResult, _("Users unblocked"), _("Cannot unblock users"));
			clearCookies(goResult);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_userid")) {
			boolean goResult = false;

			CArray groupUserId = get_request("group_userid", array());

			CUserGet uoptions = new CUserGet();
			uoptions.setUserIds(groupUserId.valuesAsString());
			uoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> dbUsers = API.User(getIdentityBean(), executor).get(uoptions);
			dbUsers = rda_toHash(dbUsers, "userid");
			
			DBstart(executor);

			for(final Object userId : groupUserId) {
				if (!isset(dbUsers,userId)) {
					continue;
				}

				Map userData = dbUsers.get(userId);

				goResult |= Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.User(getIdentityBean(), executor).delete(TObj.as(userId).asString()));
					}
				});

				if (goResult) {
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_USER, "User alias ["+Nest.value(userData,"alias").$()+"] name ["+Nest.value(userData,"name").$()+"] surname ["+Nest.value(userData,"surname").$()+"]");
				}
			}
			
			goResult = DBend(executor, goResult);

			show_messages(goResult, _("User deleted"), _("Cannot delete user"));
			clearCookies(goResult);
		}

		/* Display */
		Nest.value(_REQUEST,"filter_usrgrpid").$(get_request("filter_usrgrpid", CProfile.get(getIdentityBean(), executor,"web.users.filter.usrgrpid", 0)));
		CProfile.update(getIdentityBean(), executor,"web.users.filter.usrgrpid", Nest.value(_REQUEST,"filter_usrgrpid").$(), PROFILE_TYPE_ID);

		Map data;
		if (!empty(Nest.value(_REQUEST,"form").$())) {
			String userId = Nest.as(get_request("userid")).asString();

			data = getUserFormData(this.getIdentityBean(), executor,userId);

			Nest.value(data,"userid").$(userId);
			Nest.value(data,"form").$(get_request("form"));
			Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

			// render view
			CView usersView = new CView("administration.users.edit", data);
			usersView.render(getIdentityBean(), executor);
			usersView.show();
		} else {
			data = map();

			// get user groups
			CUserGroupGet ugoptions = new CUserGroupGet();
			ugoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> userGroups = API.UserGroup(getIdentityBean(), executor).get(ugoptions);
			Nest.value(data,"userGroups").$(userGroups);
			order_result(userGroups, "name");

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			
			// get users
			CUserGet uoptions = new CUserGet();
			if(Nest.value(_REQUEST,"filter_usrgrpid").asInteger() > 0){
				uoptions.setUsrgrpIds(Nest.value(_REQUEST,"filter_usrgrpid").asLong());
			}
			uoptions.setOutput(API_OUTPUT_EXTEND);
			uoptions.setSelectUsrgrps(API_OUTPUT_EXTEND);
			uoptions.setAccess(1);
			uoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> users = API.User(getIdentityBean(), executor).get(uoptions);
			Nest.value(data,"users").$(users);

			// sorting & paging
			order_result(users, getPageSortField(getIdentityBean(), executor,"alias"), getPageSortOrder(getIdentityBean(), executor));
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, users, array("userid")));

			for (Entry<Object, Map> e : users.entrySet()) {
			    Map user = e.getValue();
				// set default lastaccess time to 0
				Nest.value(data,"usersSessions",user.get("userid")).$(map("lastaccess", 0));
			}

			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbSessions = DBselect(executor,
				"SELECT s.userid,MAX(s.lastaccess) AS lastaccess,s.status"+
				" FROM sessions s"+
				" WHERE "+sqlParts.dual.dbConditionInt("s.userid", rda_objectValues(users, "userid").valuesAsLong())+
				" GROUP BY s.userid,s.status",
				sqlParts.getNamedParams()
			);
			for(Map session : dbSessions) {
				if (Nest.value(data,"usersSessions",session.get("userid"),"lastaccess").asLong() < Nest.value(session,"lastaccess").asLong()) {
					Nest.value(data,"usersSessions",session.get("userid")).$(session);
				}
			}

			// render view
			CView usersView = new CView("administration.users.list", data);
			usersView.render(getIdentityBean(), executor);
			usersView.show();
		}
	}

}
