package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.THEME_DEFAULT;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_subarray_push;
import static com.isoft.iradar.inc.FuncsUtil.rda_substr;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.getJsTemplate;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.UsersUtil.user_type2str;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.RBase;
import com.isoft.iradar.inc.FormsUtil;
import com.isoft.iradar.inc.PermUtil;
import com.isoft.iradar.inc.SoundsUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CListBox;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CPassBox;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationUsersEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.users.edit.js");

		CWidget userWidget = new CWidget();

		if (Nest.value(data,"is_profile").asBoolean()) {
			userWidget.addPageHeader(_("USER PROFILE")+NAME_DELIMITER+Nest.value(data,"name").asString()+" "+Nest.value(data,"surname").$());
		} else {
			userWidget.addPageHeader(_("CONFIGURATION OF USERS"));
		}

		// create form
		CForm userForm = new CForm();
		userForm.setName("userForm");
		userForm.addVar("config", get_request("config", 0));
		userForm.addVar("form", Nest.value(data,"form").$());
		userForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);

		if (isset(_REQUEST,"userid")) {
			userForm.addVar("userid", Nest.value(data,"userid").$());
		}

		/* User tab */
		CFormList userFormList = new CFormList("userFormList");

		if (!Nest.value(data,"is_profile").asBoolean()) {
			CTextBox nameTextBox = new CTextBox("alias", Nest.value(data,"alias").asString(), RDA_TEXTBOX_STANDARD_SIZE);
			nameTextBox.attr("autofocus", "autofocus");
			userFormList.addRow(_("Alias"), nameTextBox);
			userFormList.addRow(_("Name"), new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE));
			userFormList.addRow(_("Surname"), new CTextBox("surname", Nest.value(data,"surname").asString(), RDA_TEXTBOX_STANDARD_SIZE));
		}

		// append user groups to form list
		if (!Nest.value(data,"is_profile").asBoolean()) {
			userForm.addVar("user_groups", Nest.value(data,"user_groups").$());

			CListBox lstGroups = new CListBox("user_groups_to_del[]", null, 10);
			lstGroups.setAttribute("style","width: 320px");
			for(Map group : (CArray<Map>)Nest.value(data,"groups").asCArray()) {
				lstGroups.addItem(Nest.value(group,"usrgrpid").$(), Nest.value(group,"name").asString());
			}

			userFormList.addRow(_("Groups"),
				array(
					lstGroups,
					new CButton("add_group", _("Add"),
						"return PopUp(\"popup_usrgrp.action?dstfrm="+userForm.getName()+"&list_name=user_groups_to_del[]&var_name=user_groups\", 450, 450);", "formlist"),
					BR(),
					(count(Nest.value(data,"user_groups").$()) > 0)
						? new CSubmit("del_user_group", _("Delete selected"), null, "formlist")
						: null
				)
			);
		}

		// append password to form list
		if (Nest.value(data,"auth_type").asInteger() == RDA_AUTH_INTERNAL) {
			if (empty(Nest.value(data,"userid").$()) || isset(data,"change_password")) {
				userFormList.addRow(
					_("Password"),
					new CPassBox("password1", Nest.value(data,"password1").asString(), RDA_TEXTBOX_SMALL_SIZE)
				);
				userFormList.addRow(
					_("Password (once again)"),
					new CPassBox("password2", Nest.value(data,"password2").asString(), RDA_TEXTBOX_SMALL_SIZE)
				);

				if (isset(data,"change_password")) {
					userForm.addVar("change_password", Nest.value(data,"change_password").$());
				}
			} else {
				CSubmit passwdButton = new CSubmit("change_password", _("Change password"), null, "formlist");
				if (RDA_GUEST_USER.equals(Nest.value(data,"alias").$())) {
					passwdButton.setAttribute("disabled", "disabled");
				}
				userFormList.addRow(_("Password"), passwdButton);
			}
		} else {
			userFormList.addRow(_("Password"), new CSpan(
				_s("Unavailable for users with %1$s.", PermUtil.authentication2str(Nest.value(data,"auth_type").asInteger()))
			));
		}

		//TODO
//		// append languages to form list
//		CComboBox _languageComboBox = new CComboBox("lang", Nest.value(data,"lang").$());
//
//		boolean _allLocalesAvailable = true;
//		for(getLocales() as _localeId => _locale) {
//			if (Nest.value(_locale,"display").$()) {
//				// checking if this locale exists in the system. The only way of doing it is to try and set one
//				// trying to set only the LC_MONETARY locale to avoid changing LC_NUMERIC
//				_localeExists = (setlocale(LC_MONETARY , rda_locale_variants(_localeId)) || _localeId == "en_GB");
//
//				_languageComboBox.addItem(
//					_localeId,
//					Nest.value(_locale,"name").$(),
//					(_localeId == Nest.value(data,"lang").$()) ? true : null,
//					_localeExists
//				);
//
//				_allLocalesAvailable &= _localeExists;
//			}
//		}
//
//		// restoring original locale
//		setlocale(LC_MONETARY, rda_locale_variants(CWebUser::Nest.value(_data,"lang").$()));
//
//		_languageError = "";
//		if (!function_exists("bindtextdomain")) {
//			_languageError = "Translations are unavailable because the PHP gettext module is missing.";
//			_languageComboBox.attr("disabled", "disabled");
//		}
//		elseif (!_allLocalesAvailable) {
//			_languageError = _("You are not able to choose some of the languages, because locales for them are not installed on the web server.");
//		}
//
//		_userFormList.addRow(
//			_("Language"),
//			_languageError ? array(_languageComboBox, SPACE, new CSpan(_languageError, "red wrap")) : _languageComboBox
//		);

		// append themes to form list
		CArray themes = array_merge(map(THEME_DEFAULT, _("System default")), RBase.getThemes());
		CComboBox themeComboBox = new CComboBox("theme", Nest.value(data,"theme").$(), null, themes);
		userFormList.addRow(_("Theme"), themeComboBox);

		// append auto-login & auto-logout to form list
		CCheckBox autologoutCheckBox = new CCheckBox("autologout_visible", (Nest.value(data,"autologout").asInteger() == 0) ? false : true);
		CNumericBox autologoutTextBox = new CNumericBox("autologout", (Nest.value(data,"autologout").asInteger() == 0) ? "900" : Nest.value(data,"autologout").asString(), 4);
		if (!Nest.value(data,"autologout").asBoolean()) {
			autologoutTextBox.setAttribute("disabled", "disabled");
		}
		userFormList.addRow(_("Auto-login"), new CCheckBox("autologin", Nest.value(data,"autologin").asBoolean(), null, 1));
		userFormList.addRow(_("Auto-logout (min 90 seconds)"), array(autologoutCheckBox, autologoutTextBox));
		userFormList.addRow(_("Refresh (in seconds)"), new CNumericBox("refresh", Nest.value(data,"refresh").asString(), 4));
		userFormList.addRow(_("Rows per page"), new CNumericBox("rows_per_page", Nest.value(data,"rows_per_page").asString(), 6));
		userFormList.addRow(_("URL (after login)"), new CTextBox("url", Nest.value(data,"url").asString(), RDA_TEXTBOX_STANDARD_SIZE));

		/* Media tab */
		CFormList userMediaFormList = null;
		if (uint_in_array(CWebUser.getType(), array(USER_TYPE_IRADAR_ADMIN, USER_TYPE_SUPER_ADMIN))) {
			userMediaFormList = new CFormList("userMediaFormList");
			userForm.addVar("user_medias", Nest.value(data,"user_medias").$());

			CTableInfo mediaTableInfo = new CTableInfo(_("No media found."));

			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"user_medias").asCArray()).entrySet()) {
			    Object id = e.getKey();
			    Map media = e.getValue();
				CLink status = null;
				if (!isset(media,"active") || !Nest.value(media,"active").asBoolean()) {
					status = new CLink(_("Enabled"), "#", "enabled");
					status.onClick("return create_var(\""+userForm.getName()+"\",\"disable_media\","+id+", true);");
				} else {
					status = new CLink(_("Disabled"), "#", "disabled");
					status.onClick("return create_var(\""+userForm.getName()+"\",\"enable_media\","+id+", true);");
				}

				String mediaUrl = "?dstfrm="+userForm.getName()+
								"&media="+id+
								"&mediatypeid="+Nest.value(media,"mediatypeid").$()+
								"&sendto="+urlencode(Nest.value(media,"sendto").asString())+
								"&period="+Nest.value(media,"period").$()+
								"&severity="+Nest.value(media,"severity").$()+
								"&active="+Nest.value(media,"active").$();

				CArray<CSpan> mediaSeverity = array();
				for (Entry<Object, String> sc : getSeverityCaption(idBean, executor).entrySet()) {
				    int key = Nest.as(sc.getKey()).asInteger();
				    String caption = sc.getValue();
					int mediaActive = (Nest.value(media,"severity").asInteger() & (1 << key));

					CSpan cspan = new CSpan(rda_substr(caption, 0, 1), mediaActive>0 ? "enabled" : null);
					cspan.setHint(caption+(mediaActive>0 ? " (on)" : " (off)"));
					Nest.value(mediaSeverity,key).$(cspan);
				}

				mediaTableInfo.addRow(array(
					new CCheckBox("user_medias_to_del["+id+"]", false, null, Nest.as(id).asInteger()),
					new CSpan(Nest.value(media,"description").$(), "nowrap"),
					new CSpan(Nest.value(media,"sendto").$(), "nowrap"),
					new CSpan(Nest.value(media,"period").$(), "nowrap"),
					mediaSeverity,
					status,
					new CButton("edit_media", _("Edit"), "return PopUp(\"popup_media.action"+mediaUrl+"\", 550, 400);", "link_menu"))
				);
			}

			userMediaFormList.addRow(_("Media"), array(mediaTableInfo,
				new CButton("add_media", _("Add"), "return PopUp(\"popup_media.action?dstfrm="+userForm.getName()+"\", 550, 400);", "link_menu"),
				SPACE,
				SPACE,
				(count(Nest.value(data,"user_medias").$()) > 0) ? new CSubmit("del_user_media", _("Delete selected"), null, "link_menu") : null
			));
		}

		/* Profile fields */
		CFormList userMessagingFormList = null;
		if (Nest.value(data,"is_profile").asBoolean()) {
			CArray<String> rdaSounds = SoundsUtil.getSounds();

			userMessagingFormList = new CFormList("userMessagingFormList");
			userMessagingFormList.addRow(_("Frontend messaging"), new CCheckBox("messages[enabled]", Nest.value(data,"messages","enabled").asBoolean(), null, 1));
			userMessagingFormList.addRow(_("Message timeout (seconds)"), new CNumericBox("messages[timeout]", Nest.value(data,"messages","timeout").asString(), 5), false, "timeout_row");

			CComboBox repeatSound = new CComboBox("messages[sounds.repeat]", Nest.value(data,"messages","sounds.repeat").$(), "javascript: if (IE) { submit(); }");
			repeatSound.addItem(1, _("Once"));
			repeatSound.addItem(10, "10 "+_("Seconds"));
			repeatSound.addItem(-1, _("Message timeout"));
			userMessagingFormList.addRow(_("Play sound"), repeatSound, false, "repeat_row");

			CComboBox soundList = new CComboBox("messages[sounds.recovery]", Nest.value(data,"messages","sounds.recovery").$());
			for (Entry<Object, String> e : rdaSounds.entrySet()) {
				String filename = Nest.as(e.getKey()).asString();
			    String file = e.getValue();
				soundList.addItem(file, filename);
			}

			CArray resolved = array(
				new CCheckBox("messages[triggers.recovery]", Nest.value(data,"messages","triggers.recovery").asBoolean(), null, 1),
				_("Recovery"),
				SPACE,
				soundList,
				new CButton("start", _("Play"), "javascript: testUserSound(\"messages_sounds.recovery\");", "formlist"),
				new CButton("stop", _("Stop"), "javascript: AudioList.stopAll();", "formlist")
			);

			CTable triggersTable = new CTable("", "invisible");
			triggersTable.addRow(resolved);

			CArray msgVisibility = map("1" , array(
				"messages[timeout]",
				"messages[sounds.repeat]",
				"messages[sounds.recovery]",
				"messages[triggers.recovery]",
				"timeout_row",
				"repeat_row",
				"triggers_row"
			));

			// trigger sounds
			CArray<Integer> severities = array(
				TRIGGER_SEVERITY_NOT_CLASSIFIED,
				TRIGGER_SEVERITY_INFORMATION,
				TRIGGER_SEVERITY_WARNING,
				TRIGGER_SEVERITY_AVERAGE,
				TRIGGER_SEVERITY_HIGH,
				TRIGGER_SEVERITY_DISASTER
			);
			for(Integer severity : severities) {
				soundList = new CComboBox("messages[sounds."+severity+"]", Nest.value(data,"messages","sounds."+severity).asString());
				for (Entry<Object, String> e : rdaSounds.entrySet()) {
					String filename = Nest.as(e.getKey()).asString();
				    String file = e.getValue();
					soundList.addItem(file, filename);
				}

				triggersTable.addRow(array(
					new CCheckBox("messages[triggers.severities]["+severity+"]", isset(Nest.value(data,"messages","triggers.severities", severity).$()), null, 1),
					getSeverityCaption(idBean, executor, severity),
					SPACE,
					soundList,
					new CButton("start", _("Play"), "javascript: testUserSound(\"messages_sounds."+severity+"\");", "formlist"),
					new CButton("stop", _("Stop"), "javascript: AudioList.stopAll();", "formlist")
				));

				rda_subarray_push(msgVisibility, 1, "messages[triggers.severities]["+severity+"]");
				rda_subarray_push(msgVisibility, 1, "messages[sounds."+severity+"]");
			}

			userMessagingFormList.addRow(_("Trigger severity"), triggersTable, false, "triggers_row");

			rda_add_post_js(getJsTemplate("administration.users.edit"));
		}

		// append form lists to tab
		CTabView userTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			userTab.setSelected("0");
		}
		userTab.addTab("userTab", _("User"), userFormList);
		if (isset(userMediaFormList)) {
			userTab.addTab("mediaTab", _("Media"), userMediaFormList);
		}

		if (!Nest.value(data,"is_profile").asBoolean()) {
			/* Permissions tab */
			CFormList permissionsFormList = new CFormList("permissionsFormList");

			CComboBox userTypeComboBox = new CComboBox("user_type", Nest.value(data,"user_type").$(), "submit();");
			userTypeComboBox.addItem(USER_TYPE_IRADAR_USER, user_type2str(USER_TYPE_IRADAR_USER));
			userTypeComboBox.addItem(USER_TYPE_IRADAR_ADMIN, user_type2str(USER_TYPE_IRADAR_ADMIN));
			userTypeComboBox.addItem(USER_TYPE_SUPER_ADMIN, user_type2str(USER_TYPE_SUPER_ADMIN));

			if (isset(data,"userid") && bccomp(CWebUser.get("userid"), Nest.value(data,"userid").$()) == 0) {
				userTypeComboBox.setEnabled(false);
				permissionsFormList.addRow(_("User type"), array(userTypeComboBox, SPACE, new CSpan(_("User can\'t change type for himself"))));
				userForm.addVar("user_type", Nest.value(data,"user_type").$());
			} else {
				permissionsFormList.addRow(_("User type"), userTypeComboBox);
			}

			permissionsFormList = FormsUtil.getPermissionsFormList(idBean, executor,Nest.value(data,"user_rights").asCArray(), Nest.value(data,"user_type").asInteger(), permissionsFormList);
			permissionsFormList.addInfo(_("Permissions can be assigned for user groups only."));

			userTab.addTab("permissionsTab", _("Permissions"), permissionsFormList);
		}
		if (isset(userMessagingFormList)) {
			userTab.addTab("messagingTab", _("Messaging"), userMessagingFormList);
		}

		// append tab to form
		userForm.addItem(userTab);

		// append buttons to form
		if (empty(Nest.value(data,"userid").$())) {
			userForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButtonCancel(url_param(idBean, "config"))));
		} else {
			if (Nest.value(data,"is_profile").asBoolean()) {
				userForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButtonCancel(url_param(idBean, "config"))));
			} else {
				CButtonDelete deleteButton = new CButtonDelete(_("Delete selected user?"), url_param(idBean, "form")+url_param(idBean, "userid")+url_param(idBean, "config"));

				if (bccomp(CWebUser.get("userid"), Nest.value(data,"userid").$()) == 0) {
					deleteButton.setAttribute("disabled", "disabled");
				}

				userForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					array(
						deleteButton,
						new CButtonCancel(url_param(idBean, "config"))
					)
				));
			}
		}

		// append form to widget
		userWidget.addItem(userForm);

		return userWidget;
	}

}
