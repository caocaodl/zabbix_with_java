package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_USER;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.THEME_DEFAULT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.FormsUtil.getUserFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.PermUtil.getUserAuthenticationType;
import static com.isoft.iradar.inc.SoundsUtil.updateMessageSettings;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ViewsUtil.redirect;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.core.RBase;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ProfileAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("User profile"));
		page("file", "profile.action");
		page("hist_arg", new String[] {});
		page("scripts", new String[] {"class.cviewswitcher.js"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray themes = array_keys(RBase.getThemes());
		themes.add(THEME_DEFAULT);

		//			VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"password1",				array(T_RDA_STR, O_OPT, null, null, "isset({save})&&isset({form})&&({form}!=\"update\")&&isset({change_password})"),
			"password2",				array(T_RDA_STR, O_OPT, null, null, "isset({save})&&isset({form})&&({form}!=\"update\")&&isset({change_password})"),
			"lang",							array(T_RDA_STR, O_OPT, null, null, null),
			"theme",						array(T_RDA_STR, O_OPT, null, IN("\""+implode("\",\"", themes)+"\""), "isset({save})"),
			"autologin",					array(T_RDA_INT, O_OPT, null, IN("1"), null),
			"autologout",				array(T_RDA_INT, O_OPT, null, BETWEEN(90, 10000), null, _("Auto-logout (min 90 seconds)")),
			"url",								array(T_RDA_STR, O_OPT, null, null, "isset({save})"),
			"refresh", 						array(T_RDA_INT, O_OPT, null, BETWEEN(0, SEC_PER_HOUR), "isset({save})", _("Refresh (in seconds)")),
			"rows_per_page", 			array(T_RDA_INT, O_OPT, null, BETWEEN(1, 999999), "isset({save})", _("Rows per page")),
			"change_password",		array(T_RDA_STR, O_OPT, null, null, null),
			"user_medias",				array(T_RDA_STR, O_OPT, null, NOT_EMPTY, null),
			"user_medias_to_del",	array(T_RDA_STR, O_OPT, null, DB_ID, null),
			"new_media",				array(T_RDA_STR, O_OPT, null, null, null),
			"enable_media",			array(T_RDA_INT, O_OPT, null, null, null),
			"disable_media",			array(T_RDA_INT, O_OPT, null, null, null),
			"messages",					array(T_RDA_STR, O_OPT, null, null, null),
			// actions
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS, null, null),
			"del_user_media",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			// form
			"form",							array(T_RDA_STR, O_OPT, P_SYS, null, null),
			"form_refresh",				array(T_RDA_STR, O_OPT, null, null, null)
		);
		check_fields(getIdentityBean(), fields);

		Nest.value(_REQUEST,"autologin").$(get_request("autologin", 0));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (RDA_GUEST_USER.equals(Nest.value(CWebUser.data(),"alias").asString())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		// secondary actions
		if (isset(_REQUEST,"new_media")) {
			Nest.value(_REQUEST,"user_medias").$(get_request("user_medias", array()));
			array_push(Nest.value(_REQUEST,"user_medias").asCArray(), Nest.value(_REQUEST,"new_media").$());
		} else if (isset(_REQUEST,"user_medias") && isset(_REQUEST,"enable_media")) {
			if (isset(Nest.value(_REQUEST,"user_medias",_REQUEST.get("enable_media")).$())) {
				Nest.value(_REQUEST,"user_medias",_REQUEST.get("enable_media"),"active").$(0);
			}
		} else if (isset(_REQUEST,"user_medias") && isset(_REQUEST,"disable_media")) {
			if (isset(Nest.value(_REQUEST,"user_medias",_REQUEST.get("disable_media")).$())) {
				Nest.value(_REQUEST,"user_medias",_REQUEST.get("disable_media"),"active").$(1);
			}
		} else if (isset(_REQUEST,"del_user_media")) {
			CArray user_medias_to_del = get_request("user_medias_to_del", array());
			for(Object mediaid : user_medias_to_del) {
				if (isset(Nest.value(_REQUEST,"user_medias",mediaid).$())) {
					unset(Nest.value(_REQUEST,"user_medias").asCArray(),mediaid);
				}
			}
		}
		// primary actions
		else if (isset(_REQUEST,"cancel")) {
			redirect(Nest.value(CWebUser.data(),"last_page","url").asString());
		} else if (isset(_REQUEST,"save")) {
			int auth_type = getUserAuthenticationType(getIdentityBean(), executor,Nest.value(CWebUser.data(),"userid").asString());

			if (auth_type != RDA_AUTH_INTERNAL) {
				Nest.value(_REQUEST,"password1").$(null);
				Nest.value(_REQUEST,"password2").$(null);
			} else {
				Nest.value(_REQUEST,"password1").$(get_request("password1", null));
				Nest.value(_REQUEST,"password2").$(get_request("password2", null));
			}
			
			if (isset(_REQUEST,"password1") && !Nest.value(_REQUEST,"password1").asString().equals(Nest.value(_REQUEST,"password2").asString())) {
				show_error_message(_("Cannot update user. Both passwords must be equal."));
			} else if (isset(_REQUEST,"password1") && RDA_GUEST_USER.equals(Nest.value(CWebUser.data(),"alias").asString()) && !rda_empty(Nest.value(_REQUEST,"password1").$())) {
				show_error_message(_("For guest, password must be empty"));
			} else if (isset(_REQUEST,"password1") && !RDA_GUEST_USER.equals(Nest.value(CWebUser.data(),"alias").asString()) && rda_empty(Nest.value(_REQUEST,"password1").$())) {
				show_error_message(_("Password should not be empty"));
			} else {
				final Map user = map();
				Nest.value(user,"userid").$(Nest.value(CWebUser.data(),"userid").$());
				Nest.value(user,"alias").$(Nest.value(CWebUser.data(),"alias").$());
				Nest.value(user,"passwd").$(get_request("password1"));
				Nest.value(user,"url").$(get_request("url"));
				Nest.value(user,"autologin").$(get_request("autologin", 0));
				Nest.value(user,"autologout").$(get_request("autologout", 0));
				Nest.value(user,"theme").$(get_request("theme"));
				Nest.value(user,"refresh").$(get_request("refresh"));
				Nest.value(user,"rows_per_page").$(get_request("rows_per_page"));
				Nest.value(user,"user_groups").$(null);
				Nest.value(user,"user_medias").$(get_request("user_medias", array()));

				if (hasRequest("lang")) {
					Nest.value(user,"lang").$(get_request("lang"));
				}

				Map messages = get_request("messages", array());
				if (!isset(messages,"enabled")) {
					Nest.value(messages,"enabled").$(0);
				}
				if (!isset(messages,"triggers.recovery")) {
					Nest.value(messages,"triggers.recovery").$(0);
				}
				if (!isset(messages,"triggers.severities")) {
					Nest.value(messages,"triggers.severities").$(array());
				}

				DBstart(executor);
				updateMessageSettings(getIdentityBean(), executor,messages);

				boolean result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.User(getIdentityBean(), executor).updateProfile(user));
					}
				});

				if (result && CWebUser.getType() > USER_TYPE_IRADAR_USER) {
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
				if (!result) {
					//TODO
					//error(API.User(getIdentityBean(), executor).resetErrors());
				}

				if (result) {
					Nest.value(CWebUser.data(),"autologin").$(Nest.value(user,"autologin").$());
					Nest.value(CWebUser.data(),"autologout").$(Nest.value(user,"autologout").$());
					Nest.value(CWebUser.data(),"theme").$(Nest.value(user,"theme").$());
					Nest.value(CWebUser.data(),"refresh").$(Nest.value(user,"refresh").$());
					Nest.value(CWebUser.data(),"rows_per_page").$(Nest.value(user,"rows_per_page").$());
					Nest.value(CWebUser.data(),"url").$(Nest.value(user,"url").$());
					
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_USER,
						"User alias ["+CWebUser.get("alias")+"] Name ["+CWebUser.get("name")+"]"+
						" Surname ["+CWebUser.get("surname")+"] profile id ["+CWebUser.get("userid")+"]");
					redirect(Nest.value(CWebUser.data(),"last_page","url").asString());
				} else {
					show_messages(result, _("User updated"), _("Cannot update user"));
				}
			}
		}
		
		/* Display */
		Map data = getUserFormData(this.getIdentityBean(), executor,Nest.value(CWebUser.data(),"userid").asString(), true);
		Nest.value(data,"userid").$(CWebUser.get("userid"));
		Nest.value(data,"form").$(get_request("form"));
		Nest.value(data,"form_refresh").$(get_request("form_refresh", 0));

		// render view
		CView usersView = new CView("administration.users.edit", data);
		usersView.render(getIdentityBean(), executor);
		usersView.show();
	}

}
