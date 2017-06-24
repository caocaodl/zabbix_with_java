package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.EZ_TEXTING_LIMIT_CANADA;
import static com.isoft.iradar.inc.Defines.EZ_TEXTING_LIMIT_USA;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EMAIL;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EXEC;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EZ_TEXTING;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_JABBER;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_SMS;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPassBox;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationMediatypesEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget mediaTypeWidget = new CWidget();
		mediaTypeWidget.addPageHeader(_("CONFIGURATION OF MEDIA TYPES"));

		// create form
		CForm mediaTypeForm = new CForm();
		mediaTypeForm.setName("mediaTypeForm");
		mediaTypeForm.addVar("form", Nest.value(data,"form").$());
		mediaTypeForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		mediaTypeForm.addVar("mediatypeid", Nest.value(data,"mediatypeid").$());

		// create form list
		CFormList mediaTypeFormList = new CFormList("mediaTypeFormList");
		CTextBox nameTextBox = new CTextBox("description", Nest.value(data,"description").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 100);
		nameTextBox.attr("autofocus", "autofocus");
		mediaTypeFormList.addRow(_("Name"), nameTextBox);

		// append type to form list
		CComboBox cmbType = new CComboBox("type", Nest.value(data,"type").$(), "submit()");
		cmbType.addItems((CArray)map(
			MEDIA_TYPE_EMAIL, _("Email"),
			MEDIA_TYPE_EXEC, _("Script"),
			MEDIA_TYPE_SMS, _("SMS"),
			MEDIA_TYPE_JABBER, _("Jabber")
		));
		cmbType.addItemsInGroup(_("Commercial"), (CArray)map(MEDIA_TYPE_EZ_TEXTING, _("Ez Texting")));
		CArray cmbTypeRow = array(cmbType);
		if (Nest.value(data,"type").asInteger() == MEDIA_TYPE_EZ_TEXTING) {
			CLink ez_texting_link = new CLink("https://app.eztexting.com", "https://app.eztexting.com/", null, null, "nosid");
			ez_texting_link.setTarget("_blank");
			cmbTypeRow.add(ez_texting_link);
		}
		mediaTypeFormList.addRow(_("Type"), cmbTypeRow);

		// append others fields to form list
		if (Nest.value(data,"type").asInteger() == MEDIA_TYPE_EMAIL) {
			mediaTypeFormList.addRow(_("SMTP server"), new CTextBox("smtp_server", Nest.value(data,"smtp_server").asString(), RDA_TEXTBOX_STANDARD_SIZE));
			mediaTypeFormList.addRow(_("SMTP helo"), new CTextBox("smtp_helo", Nest.value(data,"smtp_helo").asString(), RDA_TEXTBOX_STANDARD_SIZE));
			mediaTypeFormList.addRow(_("SMTP email"), new CTextBox("smtp_email", Nest.value(data,"smtp_email").asString(), RDA_TEXTBOX_STANDARD_SIZE));
		} else if (Nest.value(data,"type").asInteger() == MEDIA_TYPE_SMS) {
			mediaTypeFormList.addRow(_("GSM modem"), new CTextBox("gsm_modem", Nest.value(data,"gsm_modem").asString(), RDA_TEXTBOX_STANDARD_SIZE));
		} else if (Nest.value(data,"type").asInteger() == MEDIA_TYPE_EXEC) {
			mediaTypeFormList.addRow(_("Script name"), new CTextBox("exec_path", Nest.value(data,"exec_path").asString(), RDA_TEXTBOX_STANDARD_SIZE));
		} else if (Nest.value(data,"type").asInteger() == MEDIA_TYPE_JABBER || Nest.value(data,"type").asInteger() == MEDIA_TYPE_EZ_TEXTING) {
			// create password field
			Object passwordField = null;
			if (!empty(Nest.value(data,"password").$())) {
				CButton passwordButton = new CButton("chPass_btn", _("Change password"), "this.style.display=\"none\"; $(\"password\").enable().show().focus();");
				CPassBox passwordBox = new CPassBox("password", Nest.value(data,"password").asString(), RDA_TEXTBOX_SMALL_SIZE);
				passwordBox.addStyle("display: none;");
				passwordField  = array(passwordButton, passwordBox);
			} else {
				passwordField = new CPassBox("password", "", RDA_TEXTBOX_SMALL_SIZE);
			}

			// append password field to form list
			if (Nest.value(data,"type").asInteger() == MEDIA_TYPE_JABBER) {
				mediaTypeFormList.addRow(_("Jabber identifier"), new CTextBox("username", Nest.value(data,"username").asString(), RDA_TEXTBOX_STANDARD_SIZE));
				mediaTypeFormList.addRow(_("Password"), passwordField);
			} else {
				mediaTypeFormList.addRow(_("Username"), new CTextBox("username", Nest.value(data,"username").asString(), RDA_TEXTBOX_STANDARD_SIZE));
				mediaTypeFormList.addRow(_("Password"), passwordField);
				CComboBox limitCb = new CComboBox("exec_path", Nest.value(data,"exec_path").$());
				limitCb.addItems((CArray)map(
					EZ_TEXTING_LIMIT_USA, _("USA (160 characters)"),
					EZ_TEXTING_LIMIT_CANADA, _("Canada (136 characters)")
				));
				mediaTypeFormList.addRow(_("Message text limit"), limitCb);
			}
		}

		mediaTypeFormList.addRow(_("Enabled"), new CCheckBox("status", MEDIA_TYPE_STATUS_ACTIVE == Nest.value(data,"status").asInteger(), null, MEDIA_TYPE_STATUS_ACTIVE));

		// append form list to tab
		CTabView mediaTypeTab = new CTabView();
		mediaTypeTab.addTab("mediaTypeTab", _("Media type"), mediaTypeFormList);

		// append tab to form
		mediaTypeForm.addItem(mediaTypeTab);

		// append buttons to form
		if (empty(Nest.value(data,"mediatypeid").$())) {
			mediaTypeForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), array(new CButtonCancel(url_param(idBean, "config")))));
		} else {
			mediaTypeForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), array(new CButtonDelete(_("Delete selected media type?"), url_param(idBean, "form")+url_param(idBean, "mediatypeid")+url_param(idBean, "config")), new CButtonCancel(url_param(idBean, "config")))));
		}

		// append form to widget
		mediaTypeWidget.addItem(mediaTypeForm);

		return mediaTypeWidget;
	}

}
