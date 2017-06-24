package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_LOGIN_ATTEMPTS;
import static com.isoft.iradar.inc.Defines.RDA_USER_ONLINE_TIME;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.UsersUtil.user_auth_type2str;
import static com.isoft.iradar.inc.UsersUtil.user_type2str;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationUsersList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget usersWidget = new CWidget();

		// append page header to widget
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		CComboBox configurationComboBox = new CComboBox("config", "users.action", "javascript: redirect(this.options[this.selectedIndex].value);");
		configurationComboBox.addItem("usergrps.action", _("User groups"));
		configurationComboBox.addItem("users.action", _("Users"));
		createForm.addItem(array(configurationComboBox, new CSubmit("form", _("Create user"))));
		usersWidget.addPageHeader(_("CONFIGURATION OF USERS"), createForm);

		// append form header to widget
		CForm userGroupListForm = new CForm("get");
		CComboBox userGroupComboBox = new CComboBox("filter_usrgrpid", Nest.value(_REQUEST,"filter_usrgrpid").$(), "submit()");
		userGroupComboBox.addItem(0, _("All"));

		for(Map userGroup : (CArray<Map>)Nest.value(data,"userGroups").asCArray()) {
			userGroupComboBox.addItem(Nest.value(userGroup,"usrgrpid").$(), Nest.value(userGroup,"name").asString());
		}
		userGroupListForm.addItem(array(_("User group")+SPACE, userGroupComboBox));

		usersWidget.addHeader(_("Users"), userGroupListForm);
		usersWidget.addHeaderRowNumber();

		// create form
		CForm usersForm = new CForm();
		usersForm.setName("userForm");

		// create users table
		CTableInfo usersTable = new CTableInfo(_("No users found."));
		usersTable.setHeader(array(
			new CCheckBox("all_users", false, "checkAll(\""+usersForm.getName()+"\", \"all_users\", \"group_userid\");"),
			make_sorting_header(_("Alias"), "alias"),
			make_sorting_header(_("Name"), "name"),
			make_sorting_header(_("Surname"), "surname"),
			make_sorting_header(_("User type"), "type"),
			_("Groups"),
			_("Is online?"),
			_("Login"),
			_("Frontend access"),
			//关闭Debug mode功能
			//_("Debug mode"),
			_("Status")
		));

		for(Map user : (CArray<Map>)Nest.value(data,"users").asCArray()) {
			Object userId = Nest.value(user,"userid").$();
			Map session = Nest.value(data,"usersSessions",userId).asCArray();

			// online time
			CCol online = null;
			if (!empty(Nest.value(session,"lastaccess").$())) {
				int onlineTime = (Nest.value(user,"autologout").asInteger() == 0 || RDA_USER_ONLINE_TIME < Nest.value(user,"autologout").asInteger()) ? RDA_USER_ONLINE_TIME : Nest.value(user,"autologout").asInteger();

				online  = ((Nest.value(session,"lastaccess").asInteger() + onlineTime) >= time())
					? new CCol(_("Yes")+" ("+date("r", Nest.value(session,"lastaccess").asLong())+")", "enabled")
					: new CCol(_("No")+" ("+date("r", Nest.value(session,"lastaccess").asLong())+")", "disabled");
			} else {
				online = new CCol(_("No"), "disabled");
			}

			// blocked
			CTag blocked = (Nest.value(user,"attempt_failed").asInteger() >= RDA_LOGIN_ATTEMPTS)
				? new CLink(_("Blocked"), "users.action?go=unblock&group_userid"+SQUAREBRACKETS+"="+userId, "on")
				: new CSpan(_("Ok"), "green");

			// user groups
			order_result(Nest.value(user,"usrgrps").asCArray(), "name");

			CArray usersGroups = array();
			for(Map userGroup : (CArray<Map>)Nest.value(user,"usrgrps").asCArray()) {
				usersGroups.add(new CLink(Nest.value(userGroup,"name").$(), "usergrps.action?form=update&usrgrpid="+Nest.value(userGroup,"usrgrpid").$()));
				usersGroups.add(BR());
			}
			array_pop(usersGroups);

			// user type style
			@SuppressWarnings("unused")
			String userTypeStyle = "enabled";
			if (Nest.value(user,"type").asInteger() == USER_TYPE_IRADAR_ADMIN) {
				userTypeStyle = "orange";
			}
			if (Nest.value(user,"type").asInteger() == USER_TYPE_SUPER_ADMIN) {
				userTypeStyle = "disabled";
			}

			// gui access style
			String guiAccessStyle = "green";
			if (Nest.value(user,"gui_access").asInteger() == GROUP_GUI_ACCESS_INTERNAL) {
				guiAccessStyle = "orange";
			}
			if (Nest.value(user,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED) {
				guiAccessStyle = "disabled";
			}

			// append user to table
			usersTable.addRow(array(
				new CCheckBox("group_userid["+userId+"]", false, null, Nest.as(userId).asString()),
				new CLink(Nest.value(user,"alias").$(), "users.action?form=update&userid="+userId),
				Nest.value(user,"name").$(),
				Nest.value(user,"surname").$(),
				user_type2str(Nest.value(user,"type").asInteger()),
				usersGroups,
				online,
				blocked,
				new CSpan(user_auth_type2str(idBean, executor, Nest.value(user,"gui_access").asInteger()), guiAccessStyle),
				//关闭Debug mode功能
				//(Nest.value(user,"debug_mode").asInteger() == GROUP_DEBUG_MODE_ENABLED) ? new CSpan(_("Enabled"), "orange") : new CSpan(_("Disabled"), "green"),
				(Nest.value(user,"users_status").asInteger() == 1) ? new CSpan(_("Disabled"), "red") : new CSpan(_("Enabled"), "green")
			));
		}

		// append Go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("unblock", _("Unblock selected"));
		goOption.setAttribute("confirm", _("Unblock selected users?"));
		goComboBox.addItem(goOption);
		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected users?"));
		goComboBox.addItem(goOption);
		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"group_userid\";");

		// append table to form
		usersForm.addItem(array(Nest.value(data,"paging").$(), usersTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		usersWidget.addItem(usersForm);

		return usersWidget;
	}

}
