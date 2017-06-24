package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FormsUtil.getPermissionsFormList;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.UsersUtil.getUserFullname;
import static com.isoft.iradar.inc.UsersUtil.granted2update_group;
import static com.isoft.iradar.inc.UsersUtil.user_auth_type2str;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CListBox;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationUsergroupsEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget userGroupWidget = new CWidget();
		userGroupWidget.addPageHeader(_("CONFIGURATION OF USER GROUPS"));

		// create form
		CForm userGroupForm = new CForm();
		userGroupForm.setName("userGroupsForm");
		userGroupForm.addVar("form", Nest.value(data,"form").$());
		userGroupForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		userGroupForm.addVar("group_rights", Nest.value(data,"group_rights").$());
		if (isset(_REQUEST,"usrgrpid")) {
			userGroupForm.addVar("usrgrpid", Nest.value(data,"usrgrpid").$());
		}

		/*
		 * User group tab
		*/
		CFormList userGroupFormList = new CFormList("userGroupFormList");
		CTextBox nameTextBox = new CTextBox("gname", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		userGroupFormList.addRow(_("Group name"), nameTextBox);

		// append groups to form list
		CComboBox groupsComboBox = new CComboBox("selusrgrp", Nest.value(data,"selected_usrgrp").$(), "submit()");
		groupsComboBox.addItem(0, _("All"));
		for(Map group : (CArray<Map>)Nest.value(data,"usergroups").asCArray()) {
			groupsComboBox.addItem(Nest.value(group,"usrgrpid").$(), Nest.value(group,"name").asString());
		}

		// append user tweenbox to form list
		CTweenBox usersTweenBox = new CTweenBox(userGroupForm, "group_users", Nest.value(data,"group_users").$(), 10);
		for(Map user : (CArray<Map>)Nest.value(data,"users").asCArray()) {
			usersTweenBox.addItem(Nest.value(user,"userid").$(), getUserFullname(user));
		}
		userGroupFormList.addRow(_("Users"), usersTweenBox.get(_("In group"), array(_("Other groups"), SPACE, groupsComboBox)));

		// append frontend and user status to from list
		boolean isGranted = isset(Nest.value(_REQUEST,"usrgrpid").$()) ? granted2update_group(idBean, executor, Nest.array(_REQUEST,"usrgrpid").asLong()) : true;
		if (isGranted) {
			CComboBox frontendComboBox = new CComboBox("gui_access", Nest.value(data,"gui_access").$());
			frontendComboBox.addItem(GROUP_GUI_ACCESS_SYSTEM, user_auth_type2str(idBean, executor,GROUP_GUI_ACCESS_SYSTEM));
			frontendComboBox.addItem(GROUP_GUI_ACCESS_INTERNAL, user_auth_type2str(idBean, executor,GROUP_GUI_ACCESS_INTERNAL));
			frontendComboBox.addItem(GROUP_GUI_ACCESS_DISABLED, user_auth_type2str(idBean, executor,GROUP_GUI_ACCESS_DISABLED));
			userGroupFormList.addRow(_("Frontend access"), frontendComboBox);
			userGroupFormList.addRow(_("Enabled"), new CCheckBox("users_status", Nest.value(data,"users_status").asBoolean() ? (!isset(_REQUEST,"usrgrpid") ? true : false) : true, null, 1)); // invert user status 0 - enable, 1 - disable
		} else {
			userGroupForm.addVar("gui_access", Nest.value(data,"gui_access").$());
			userGroupForm.addVar("users_status", GROUP_STATUS_ENABLED);
			userGroupFormList.addRow(_("Frontend access"), new CSpan(user_auth_type2str(idBean, executor, Nest.value(data,"gui_access").asInteger()), "text-field green"));
			userGroupFormList.addRow(_("Enabled"), new CSpan(_("Enabled"), "text-field green"));
		}
		
		//关闭Debug mode功能
		//userGroupFormList.addRow(_("Debug mode"), new CCheckBox("debug_mode", Nest.value(data,"debug_mode").asBoolean(), null, 1));

		/* Permissions tab */
		CFormList permissionsFormList = new CFormList("permissionsFormList");

		// append permissions table to form list
		CTable permissionsTable = new CTable(null, "right_table");
		permissionsTable.setHeader(array(_("Read-write"), _("Read only"), _("Deny")), "header");

		CListBox lstWrite = new CListBox("right_to_del[read_write][]", null, 20);
		CListBox lstRead = new CListBox("right_to_del[read_only][]", null, 20);
		CListBox lstDeny = new CListBox("right_to_del[deny][]", null, 20);

		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"group_rights").asCArray()).entrySet()) {
		    Object id = e.getKey();
		    Map rights = e.getValue();
			if (Nest.value(rights,"permission").asInteger() == PERM_DENY) {
				lstDeny.addItem(id, Nest.value(rights,"name").asString());
			} else if (Nest.value(rights,"permission").asInteger() == PERM_READ) {
				lstRead.addItem(id, Nest.value(rights,"name").asString());
			} else if (Nest.value(rights,"permission").asInteger() == PERM_READ_WRITE) {
				lstWrite.addItem(id, Nest.value(rights,"name").asString());
			}
		}

		permissionsTable.addRow(array(
			new CCol(lstWrite, "read_write"),
			new CCol(lstRead, "read_only"),
			new CCol(lstDeny, "deny")
		));
		permissionsTable.addRow(array(
			array(
				new CButton("add_read_write", _("Add"), "return PopUp(\"popup_right.action?dstfrm="+userGroupForm.getName()+"&permission="+PERM_READ_WRITE+"\", 450, 450);", "formlist"),
				new CSubmit("del_read_write", _("Delete selected"), null, "formlist")
			),
			array(
				new CButton("add_read_only", _("Add"), "return PopUp(\"popup_right.action?dstfrm="+userGroupForm.getName()+"&permission="+PERM_READ+"\", 450, 450);", "formlist"),
				new CSubmit("del_read_only", _("Delete selected"), null, "formlist")
			),
			array(
				new CButton("add_deny", _("Add"), "return PopUp(\"popup_right.action?dstfrm="+userGroupForm.getName()+"&permission="+PERM_DENY+"\", 450, 450);", "formlist"),
				new CSubmit("del_deny", _("Delete selected"), null, "formlist")
			)
		));
		permissionsFormList.addRow(_("Composing permissions"), permissionsTable);
		permissionsFormList.addRow(_("Calculated permissions"), "");
		permissionsFormList = getPermissionsFormList(idBean, executor,(CArray)Nest.value(data,"group_rights").asCArray(), null, permissionsFormList);

		// append form lists to tab
		CTabView userGroupTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			userGroupTab.setSelected("0");
		}
		userGroupTab.addTab("userGroupTab", _("User group"), userGroupFormList);
		userGroupTab.addTab("permissionsTab", _("Permissions"), permissionsFormList);

		// append tab to form
		userGroupForm.addItem(userGroupTab);

		// append buttons to form
		if (empty(Nest.value(data,"usrgrpid").$())) {
			userGroupForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel(url_param(idBean, "config"))
			));
		} else {
			userGroupForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CButtonDelete(_("Delete selected group?"), url_param(idBean, "form")+url_param(idBean, "usrgrpid")+url_param(idBean, "config")),
					new CButtonCancel(url_param(idBean, "config"))
				)
			));
		}

		// append form to widget
		userGroupWidget.addItem(userGroupForm);
		return userGroupWidget;
	}

}
