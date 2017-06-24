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
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAdministrationScriptList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget scriptsWidget = new CWidget();

		CForm scriptsForm = new CForm();
		scriptsForm.setName("scriptsForm");
		scriptsForm.setAttribute("id", "scripts");
		
		CToolBar tb = new CToolBar(scriptsForm);
		tb.addSubmit("form", _("Create script"),"","orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected scripts?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"scripts\";");
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		scriptsWidget.addItem(headerActions);
		
		CTableInfo scriptsTable = new CTableInfo(_("No scripts found."));
		scriptsTable.setHeader(array(
			new CCheckBox("all_scripts", false, "checkAll(\""+scriptsForm.getName()+"\",\"all_scripts\", \"scripts\");"),
			make_sorting_header(_("Name"), "name"),
			_("Type"),
			_("Execute on"),
			make_sorting_header(_("Commands"), "command"),
			//_("User group"),
			_("Host group")
			//_("Host access")
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
				//!empty(Nest.value(script,"userGroupName").$()) ? Nest.value(script,"userGroupName").$() : _("All"),
				!empty(Nest.value(script,"hostGroupName").$()) ? Nest.value(script,"hostGroupName").$() : _("All")
				//(Nest.value(script,"host_access").asInteger() == PERM_READ_WRITE) ? _("Write") : _("Read")
			));
		}

		// append table to form
		scriptsForm.addItem(array(scriptsTable, Nest.value(data,"paging").$()));

		// append form to widget
		scriptsWidget.addItem(scriptsForm);

		return scriptsWidget;
	}

}
