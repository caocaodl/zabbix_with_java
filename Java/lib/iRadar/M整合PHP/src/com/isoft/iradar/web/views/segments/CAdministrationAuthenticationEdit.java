package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_HTTP;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_LDAP;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.PermUtil.check_perm2login;
import static com.isoft.iradar.inc.PermUtil.check_perm2system;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CPassBox;
import com.isoft.iradar.tags.CRadioButton;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationAuthenticationEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget authenticationWidget = new CWidget();
		authenticationWidget.addPageHeader(_("CONFIGURATION OF AUTHENTICATION"));

		// create form
		CForm authenticationForm = new CForm();
		authenticationForm.setName("authenticationForm");

		// create form list
		CFormList authenticationFormList = new CFormList("authenticationList");

		// append config radio buttons to form list
		CArray configTypeRadioButton = array(
			new CRadioButton("config", RDA_AUTH_INTERNAL, null, "config_"+RDA_AUTH_INTERNAL,
				(Nest.value(data,"config","authentication_type").asInteger() == RDA_AUTH_INTERNAL),
				"submit()"
			),
			new CLabel(_x("Internal", "authentication"), "config_"+RDA_AUTH_INTERNAL),
			new CRadioButton("config", RDA_AUTH_LDAP, null, "config_"+RDA_AUTH_LDAP,
				(Nest.value(data,"config","authentication_type").asInteger() == RDA_AUTH_LDAP),
				"submit()"
			),
			new CLabel(_("LDAP"), "config_"+RDA_AUTH_LDAP),
			new CRadioButton("config", RDA_AUTH_HTTP, null, "config_"+RDA_AUTH_HTTP,
				(Nest.value(data,"config","authentication_type").asInteger() == RDA_AUTH_HTTP),
				"submit()"
			),
			new CLabel(_("HTTP"), "config_"+RDA_AUTH_HTTP)
		);
		authenticationFormList.addRow(_("Default authentication"), new CDiv(configTypeRadioButton, "jqueryinputset"));

		// append LDAP fields to form list
		if (Nest.value(data,"config","authentication_type").asInteger() == RDA_AUTH_LDAP) {
			Object userComboBox = null;
			if (!empty(Nest.value(data,"user_list").$())) {
				userComboBox = new CComboBox("user", Nest.value(data,"user").$());
				for(Map user : (CArray<Map>)Nest.value(data,"user_list").asCArray()) {
					if (check_perm2login(idBean, executor, Nest.value(user,"userid").asString()) && check_perm2system(idBean, executor,Nest.value(user,"userid").asString())) {
						((CComboBox)userComboBox).addItem(Nest.value(user,"alias").$(), Nest.value(user,"alias").asString());
					}
				}
			} else {
				userComboBox = new CTextBox("user", Nest.value(data,"user").asString(), RDA_TEXTBOX_STANDARD_SIZE, true);
			}

			authenticationFormList.addRow(
				_("LDAP host"),
				new CTextBox("ldap_host", Nest.value(data,"config","ldap_host").asString(), RDA_TEXTBOX_STANDARD_SIZE)
			);
			authenticationFormList.addRow(
				_("Port"),
				new CNumericBox("ldap_port", Nest.value(data,"config","ldap_port").asString(), 5)
			);
			authenticationFormList.addRow(
				_("Base DN"),
				new CTextBox("ldap_base_dn", Nest.value(data,"config","ldap_base_dn").asString(), RDA_TEXTBOX_STANDARD_SIZE)
			);
			authenticationFormList.addRow(
				_("Search attribute"),
				new CTextBox(
					"ldap_search_attribute",
					(rda_empty(Nest.value(data,"config","ldap_search_attribute").$()) && Nest.value(data,"form_refresh").asInteger() == 0)
						? "uid"
						: Nest.value(data,"config","ldap_search_attribute").asString(),
					RDA_TEXTBOX_STANDARD_SIZE,
					false,
					128
				)
			);
			authenticationFormList.addRow(
				_("Bind DN"),
				new CTextBox("ldap_bind_dn", Nest.value(data,"config","ldap_bind_dn").asString(), RDA_TEXTBOX_STANDARD_SIZE)
			);

			// bind password
			if (isset(Nest.value(data,"change_bind_password").$()) || rda_empty(Nest.value(data,"config","ldap_bind_password").$())) {
				authenticationForm.addVar("change_bind_password", 1);
				authenticationFormList.addRow(
					_("Bind password"),
					new CPassBox("ldap_bind_password", null, RDA_TEXTBOX_SMALL_SIZE)
				);
			} else {
				authenticationFormList.addRow(
					_("Bind password"),
					new CSubmit("change_bind_password", _("Change password"), null, "formlist")
				);
			}

			authenticationFormList.addRow(_("Test authentication"), " ["+_("must be a valid LDAP user")+"]");
			authenticationFormList.addRow(_("Login"), userComboBox);
			authenticationFormList.addRow(_("User password"), new CPassBox("user_password", null, RDA_TEXTBOX_SMALL_SIZE));
		}

		// append form list to tab
		CTabView authenticationTab = new CTabView();
		authenticationTab.addTab("authenticationTab", Nest.value(data,"title").asString(), authenticationFormList);

		// append tab to form
		authenticationForm.addItem(authenticationTab);

		// create save button
		CSubmit saveButton = new CSubmit("save", _("Save"));
		if (!empty(Nest.value(data,"is_authentication_type_changed").$())) {
			saveButton.addAction("onclick", "javascript: "+
				"if (Confirm(\""+_("Switching authentication method will reset all except this session! Continue?")+"\")) {"+
					"jQuery(\"#authenticationForm\").submit(); return true; } else { return false; }"
			);
		} else if (Nest.value(data,"config","authentication_type").asInteger() != RDA_AUTH_LDAP) {
			saveButton.setAttribute("disabled", "true");
		}

		// append buttons to form
		if (Nest.value(data,"config","authentication_type").asInteger() == RDA_AUTH_LDAP) {
			authenticationForm.addItem(makeFormFooter(saveButton, new CSubmit("test", _("Test"))));
		} else {
			authenticationForm.addItem(makeFormFooter(saveButton));
		}

		// append form to widget
		authenticationWidget.addItem(authenticationForm);

		return authenticationWidget;
	}

}
