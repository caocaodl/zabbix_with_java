package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_SERVER;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationScriptList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget scriptsWidget = new CWidget();

		CForm createForm = new CForm("get");
		createForm.addItem(new CSubmit("form", _("Create script")));

		scriptsWidget.addPageHeader(_("CONFIGURATION OF SCRIPTS"), createForm);
		scriptsWidget.addHeader(_("Scripts"));
		scriptsWidget.addHeaderRowNumber();

		CForm scriptsForm = new CForm();
		scriptsForm.setName("scriptsForm");
		scriptsForm.setAttribute("id", "scripts");

		CTableInfo scriptsTable = new CTableInfo(_("No scripts found."));
		scriptsTable.setHeader(array(
			new CCheckBox("all_scripts", false, "checkAll(\""+scriptsForm.getName()+"\",\"all_scripts\", \"scripts\");"),
			make_sorting_header(_("Name"), "name"),
			_("Type"),
			_("Execute on"),
			make_sorting_header(_("Commands"), "command"),
			_("User group"),
			_("Host group"),
			_("Host access")
		));

		for(Map script :(CArray<Map>)Nest.value(data,"scripts").asCArray()) {
			String scriptType = null;
			switch (Nest.value(script,"type").asInteger()) {
				case RDA_SCRIPT_TYPE_CUSTOM_SCRIPT:
					scriptType  = _("Script");
					break;
				case RDA_SCRIPT_TYPE_IPMI:
					scriptType = _("IPMI");
					break;
				default:
					scriptType = "";
					break;
			}

			String scriptExecuteOn = null;
			if (Nest.value(script,"type").asInteger() == RDA_SCRIPT_TYPE_CUSTOM_SCRIPT) {
				switch (Nest.value(script,"execute_on").asInteger()) {
					case RDA_SCRIPT_EXECUTE_ON_AGENT:
						scriptExecuteOn  = _("Agent");
						break;
					case RDA_SCRIPT_EXECUTE_ON_SERVER:
						scriptExecuteOn = _("Server");
						break;
				}
			} else {
				scriptExecuteOn = "";
			}

			scriptsTable.addRow(array(
				new CCheckBox("scripts["+Nest.value(script,"scriptid").$()+"]", false, null, Nest.value(script,"scriptid").asInteger()),
				new CLink(Nest.value(script,"name").$(), "scripts.action?form=1&scriptid="+Nest.value(script,"scriptid").$()),
				scriptType,
				scriptExecuteOn,
				rda_nl2br(htmlspecialchars(Nest.value(script,"command").asString())),
				!empty(Nest.value(script,"userGroupName").$()) ? Nest.value(script,"userGroupName").$() : _("All"),
				!empty(Nest.value(script,"hostGroupName").$()) ? Nest.value(script,"hostGroupName").$() : _("All"),
				(Nest.value(script,"host_access").asInteger() == PERM_READ_WRITE) ? _("Write") : _("Read")
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected scripts?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"scripts\";");

		// append table to form
		scriptsForm.addItem(array(Nest.value(data,"paging").$(), scriptsTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		scriptsWidget.addItem(scriptsForm);

		return scriptsWidget;
	}

}
