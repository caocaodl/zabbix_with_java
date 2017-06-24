package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_ENABLED;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.UsersUtil.getUserFullname;
import static com.isoft.iradar.inc.UsersUtil.granted2update_group;
import static com.isoft.iradar.inc.UsersUtil.user_auth_type2str;
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
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CAdministrationUsergroupsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget userGroupsWidget = new CWidget();

		// append page header to widget
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		CComboBox configurationComboBox = new CComboBox("config", "usergrps.action", "javascript: redirect(this.options[this.selectedIndex].value);");
		configurationComboBox.addItem("usergrps.action", _("User groups"));
		configurationComboBox.addItem("users.action", _("Users"));
		createForm.addItem(array(configurationComboBox, new CSubmit("form", _("Create user group"))));
		userGroupsWidget.addPageHeader(_("CONFIGURATION OF USER GROUPS"), createForm);

		// append header to widget
		userGroupsWidget.addHeader(_("User groups"));
		userGroupsWidget.addHeaderRowNumber();

		// create form
		CForm userGroupsForm = new CForm();
		userGroupsForm.setName("userGroupsForm");

		// create user group table
		CTableInfo userGroupTable = new CTableInfo(_("No user groups found."));
		userGroupTable.setHeader(array(
			new CCheckBox("all_groups", false, "checkAll(\""+userGroupsForm.getName()+"\",\"all_groups\",\"group_groupid\");"),
			make_sorting_header(_("Name"), "name"),
			"#",
			_("Members"),
			_("Status"),
			_("Frontend access")
			//关闭Debug mode功能
			//,_("Debug mode")
		));

		for(Map usrgrp : (CArray<Map>)Nest.value(data,"usergroups").asCArray()) {
			Object userGroupId = Nest.value(usrgrp,"usrgrpid").$();

			// gui access
			Object guiAccess = user_auth_type2str(idBean, executor, Nest.value(usrgrp,"gui_access").asInteger());
			String guiAccessStyle = "enabled";
			if (Nest.value(usrgrp,"gui_access").asInteger() == GROUP_GUI_ACCESS_INTERNAL) {
				guiAccessStyle = "orange";
			}
			if (Nest.value(usrgrp,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED) {
				guiAccessStyle = "disabled";
			}

			Object usersStatus = null;
			if (granted2update_group(idBean, executor, TArray.as(userGroupId).asLong())) {
				int nextGuiAuth = (Nest.value(usrgrp,"gui_access").asInteger() + 1 > GROUP_GUI_ACCESS_DISABLED)
					? GROUP_GUI_ACCESS_SYSTEM
					: Nest.value(usrgrp,"gui_access").asInteger() + 1;

				guiAccess = new CLink(
					guiAccess,
					"usergrps.action?go=set_gui_access&set_gui_access="+nextGuiAuth+"&usrgrpid="+userGroupId,
					guiAccessStyle
				);

				usersStatus = (Nest.value(usrgrp,"users_status").asInteger() == GROUP_STATUS_ENABLED)
					? new CLink(_("Enabled"), "usergrps.action?go=disable_status&usrgrpid="+userGroupId, "enabled")
					: new CLink(_("Disabled"), "usergrps.action?go=enable_status&usrgrpid="+userGroupId, "disabled");
			} else {
				guiAccess = new CSpan(guiAccess, guiAccessStyle);
				usersStatus = (Nest.value(usrgrp,"users_status").asInteger() == GROUP_STATUS_ENABLED) ? new CSpan(_("Enabled"), "enabled") : new CSpan(_("Disabled"), "disabled");
			}

			CArray users = null;
			if (isset(usrgrp,"users")) {
				CArray<Map> userGroupUsers = Nest.value(usrgrp,"users").asCArray();
				order_result(userGroupUsers, "alias");

				users = array();
				for(Map user : userGroupUsers) {
					String userStatusStyle = "enabled";
					if (Nest.value(user,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED) {
						userStatusStyle = "disabled";
					}
					if (Nest.value(user,"users_status").asInteger() == GROUP_STATUS_DISABLED) {
						userStatusStyle = "disabled";
					}

					users.add(new CLink(getUserFullname(user),
						"users.action?form=update&userid="+Nest.value(user,"userid").$(),
						userStatusStyle
					));
					users.add(", ");
				}
				array_pop(users);
			}

			userGroupTable.addRow(array(
				new CCheckBox("group_groupid["+userGroupId+"]", false, null, Nest.as(userGroupId).asInteger()),
				new CLink(Nest.value(usrgrp,"name").$(), "usergrps.action?form=update&usrgrpid="+userGroupId),
				array(new CLink(_("Users"), "users.action?filter_usrgrpid="+userGroupId), " (", count(Nest.value(usrgrp,"users").$()), ")"),
				new CCol(users, "wraptext"),
				usersStatus,
				guiAccess
				//关闭Debug mode功能
				//,debugMode
			));
		}

		// append GO buttons
		CComboBox goComboBox = new CComboBox("go");

		CComboItem goOption = new CComboItem("enable_status", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected groups?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable_status", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected groups?"));
		goComboBox.addItem(goOption);

		//关闭Debug mode功能
		//goOption = new CComboItem("enable_debug", _("Enable DEBUG"));
		//goOption.setAttribute("confirm", _("Enable debug mode in selected groups?"));
		//goComboBox.addItem(goOption);

		//关闭Debug mode功能
		//goOption = new CComboItem("disable_debug", _("Disable DEBUG"));
		//goOption.setAttribute("confirm", _("Disable debug mode in selected groups?"));
		//goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected groups?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"group_groupid\";");

		// append table to form
		userGroupsForm.addItem(array(Nest.value(data,"paging").$(), userGroupTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		userGroupsWidget.addItem(userGroupsForm);
		return userGroupsWidget;
	}

}
