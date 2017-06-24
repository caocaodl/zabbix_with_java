package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_SERVER;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
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
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CRadioButtonList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationScriptEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.script.edit.js");

		CWidget scriptsWidget = new CWidget();
		scriptsWidget.addPageHeader(_("CONFIGURATION OF SCRIPTS"));

		CForm scriptForm = new CForm();
		scriptForm.setName("scripts");
		scriptForm.addVar("form", Nest.value(data,"form").$());
		scriptForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);

		if (!empty(Nest.value(data,"scriptid").$())) {
			scriptForm.addVar("scriptid", Nest.value(data,"scriptid").$());
		}

		CFormList scriptFormList = new CFormList("scriptsTab");

		// name
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		nameTextBox.attr("placeholder", _("<Sub-menu/Sub-menu.../>Script"));
		scriptFormList.addRow(_("Name"), nameTextBox);

		// type
		CComboBox typeComboBox = new CComboBox("type", Nest.value(data,"type").$());
		typeComboBox.addItem(RDA_SCRIPT_TYPE_IPMI, _("IPMI"));
		typeComboBox.addItem(RDA_SCRIPT_TYPE_CUSTOM_SCRIPT, _("Script"));
		scriptFormList.addRow(_("Type"), typeComboBox);

		// execute on
		CRadioButtonList typeRadioButton = new CRadioButtonList("execute_on", Nest.value(data,"execute_on").asString());
		typeRadioButton.makeVertical();
		typeRadioButton.addValue(_("iRadar agent"), String.valueOf(RDA_SCRIPT_EXECUTE_ON_AGENT));
		typeRadioButton.addValue(_("iRadar server"), String.valueOf(RDA_SCRIPT_EXECUTE_ON_SERVER));
		scriptFormList.addRow(
			_("Execute on"),
			new CDiv(typeRadioButton, "objectgroup inlineblock border_dotted ui-corner-all"),
			(Nest.value(data,"type").asInteger() == RDA_SCRIPT_TYPE_IPMI)
		);
		scriptFormList.addRow(
			_("Commands"),
			new CTextArea("command", Nest.value(data,"command").asString()),
			(Nest.value(data,"type").asInteger() == RDA_SCRIPT_TYPE_IPMI)
		);
		scriptFormList.addRow(
			_("Command"),
			new CTextBox("commandipmi", Nest.value(data,"commandipmi").asString(), RDA_TEXTBOX_STANDARD_SIZE),
			(Nest.value(data,"type").asInteger() == RDA_SCRIPT_TYPE_CUSTOM_SCRIPT)
		);
		scriptFormList.addRow(_("Description"), new CTextArea("description", Nest.value(data,"description").asString()));

		// user groups
		CComboBox userGroups = new CComboBox("usrgrpid", Nest.value(data,"usrgrpid").$());
		userGroups.addItem(0, _("All"));
		for(Map userGroup :(CArray<Map>)Nest.value(data,"usergroups").asCArray()){
			userGroups.addItem(Nest.value(userGroup,"usrgrpid").$(), Nest.value(userGroup,"name").asString());
		}
		scriptFormList.addRow(_("User group"), userGroups);

		// host groups
		CComboBox hostGroups = new CComboBox("hgstype", Nest.value(data,"hgstype").$());
		hostGroups.addItem(0, _("All"));
		hostGroups.addItem(1, _("Selected"));
		scriptFormList.addRow(_("Host group"), hostGroups);
		scriptFormList.addRow(null, new CMultiSelect(map(
			"name", "groupid",
			"selectedLimit", 1,
			"objectName", "hostGroup",
			"data", Nest.value(data,"hostGroup").$(),
			"popup", map(
				"parameters", "srctbl=host_groups&dstfrm="+scriptForm.getName()+"&dstfld1=groupid&srcfld1=groupid",
				"width", 450,
				"height", 450
			)
		)), false, "hostGroupSelection");

		// access
		CComboBox accessComboBox = new CComboBox("access", Nest.value(data,"access").$());
		accessComboBox.addItem(PERM_READ, _("Read"));
		accessComboBox.addItem(PERM_READ_WRITE, _("Write"));
		scriptFormList.addRow(_("Required host permissions"), accessComboBox);
		scriptFormList.addRow(new CLabel(_("Enable confirmation"), "enableConfirmation"),
			new CCheckBox("enableConfirmation", Nest.value(data,"enableConfirmation").asBoolean()));

		CLabel confirmationLabel = new CLabel(_("Confirmation text"), "confirmation");
		confirmationLabel.setAttribute("id", "confirmationLabel");
		scriptFormList.addRow(confirmationLabel, array(
			new CTextBox("confirmation", Nest.value(data,"confirmation").asString(), RDA_TEXTBOX_STANDARD_SIZE),
			SPACE,
			new CButton("testConfirmation", _("Test confirmation"), null, "link_menu")
		));

		CTabView scriptView = new CTabView();
		scriptView.addTab("scripts", _("Script"), scriptFormList);
		scriptForm.addItem(scriptView);

		// footer
		CArray others = array();
		if (isset(_REQUEST,"scriptid")) {
			others.add(new CButton("clone", _("Clone")));
			others.add(new CButtonDelete(_("Delete script?"), url_param(idBean, "form")+url_param(idBean, "scriptid")));
		}
		others.add(new CButtonCancel());
		scriptForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), others));
		scriptsWidget.addItem(scriptForm);

		return scriptsWidget;
	}

}
