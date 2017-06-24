package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.inc.ActionsUtil.get_condition_desc;
import static com.isoft.iradar.inc.ActionsUtil.get_operation_descr;
import static com.isoft.iradar.inc.ActionsUtil.sortOperations;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.SHORT_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
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
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationActionList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget actionWidget = new CWidget();

		// create new action button
		CForm createForm = new CForm("get");
		createForm.cleanItems();
		createForm.addVar("eventsource", Nest.value(data,"eventsource").$());
		createForm.addItem(new CSubmit("form", _("Create action")));
		actionWidget.addPageHeader(_("CONFIGURATION OF ACTIONS"), createForm);

		// create widget header
		CComboBox sourceComboBox = new CComboBox("eventsource", Nest.value(data,"eventsource").$(), "submit()");
		sourceComboBox.addItem(EVENT_SOURCE_TRIGGERS, _("Triggers"));
		sourceComboBox.addItem(EVENT_SOURCE_DISCOVERY, _("Discovery"));
		sourceComboBox.addItem(EVENT_SOURCE_AUTO_REGISTRATION, _("Auto registration"));
		sourceComboBox.addItem(EVENT_SOURCE_INTERNAL, _x("Internal", "event source"));
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Event source"), SPACE, sourceComboBox));

		actionWidget.addHeader(_("Actions"), filterForm);
		actionWidget.addHeaderRowNumber();

		// create form
		CForm actionForm = new CForm();
		actionForm.setName("actionForm");

		// create table
		CTableInfo actionTable = new CTableInfo(_("No actions found."));
		actionTable.setHeader(array(
			new CCheckBox("all_items", false, "checkAll(\""+actionForm.getName()+"\", \"all_items\", \"g_actionid\");"),
			make_sorting_header(_("Name"), "name"),
			_("Conditions"),
			_("Operations"),
			make_sorting_header(_("Status"), "status")
		));

		for(Map action : (CArray<Map>)Nest.value(data,"actions").asCArray()) {
			CArray conditions = array();
			order_result(Nest.value(action,"conditions").asCArray(), "conditiontype", RDA_SORT_DOWN);
			for(Map condition : (CArray<Map>)Nest.value(action,"conditions").asCArray()) {
				conditions.add(get_condition_desc(idBean, executor, Nest.value(condition,"conditiontype").asInteger(), Nest.value(condition,"operator").asInteger(), Nest.value(condition,"value").asString()));
				conditions.add(BR());
			}

			sortOperations(Nest.value(data,"eventsource").asInteger(), Nest.value(action,"operations").asCArray());
			CArray operations = array();
			for(Map operation : (CArray<Map>)Nest.value(action,"operations").asCArray()) {
				operations.add(get_operation_descr(idBean, executor, SHORT_DESCRIPTION, operation));
			}

			CLink status = null;
			if (Nest.value(action,"status").asInteger() == ACTION_STATUS_DISABLED) {
				status  = new CLink(_("Disabled"),
					"actionconf.action?go=activate&g_actionid"+SQUAREBRACKETS+"="+action.get("actionid")+url_param(idBean, "eventsource"),
					"disabled"
				);
			} else {
				status = new CLink(_("Enabled"),
					"actionconf.action?go=disable&g_actionid"+SQUAREBRACKETS+"="+action.get("actionid")+url_param(idBean, "eventsource"),
					"enabled"
				);
			}

			actionTable.addRow(array(
				new CCheckBox("g_actionid["+Nest.value(action,"actionid").$()+"]", false, null, Nest.value(action,"actionid").asInteger()),
				new CLink(Nest.value(action,"name").$(), "actionconf.action?form=update&actionid="+Nest.value(action,"actionid").$()),
				conditions,
				new CCol(operations, "wraptext"),
				status
			));
		}

		// create go buttons
		CComboBox goComboBox = new CComboBox("go");
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected actions?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected actions?"));
		goComboBox.addItem(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected actions?"));
		goComboBox.addItem(goOption);

		CSubmit goButton = new CSubmit("goButton", _("Go")+" (0)");
		goButton.setAttribute("id", "goButton");
		rda_add_post_js("chkbxRange.pageGoName = \"g_actionid\";");

		// append table to form
		actionForm.addItem(array(Nest.value(data,"paging").$(), actionTable, Nest.value(data,"paging").$(), get_table_header(array(goComboBox, goButton))));

		// append form to widget
		actionWidget.addItem(actionForm);

		return actionWidget;
	}

}
