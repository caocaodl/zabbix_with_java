package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.ActionsUtil.get_condition_desc;
import static com.isoft.iradar.inc.ActionsUtil.get_operation_descr;
import static com.isoft.iradar.inc.ActionsUtil.sortOperations;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.SHORT_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.SQUAREBRACKETS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
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

public class CConfigurationActionList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget actionWidget = new CWidget();
		
		CForm actionForm = new CForm();
		actionForm.setName("actionForm");
		actionForm.addVar("eventsource", Nest.value(data,"eventsource").$());
		
		CToolBar tb = new CToolBar(actionForm);
		tb.addSubmit("form", _("Create action"),"","orange create");
		
		CArray<CComboItem> goComboBox = array();
		CComboItem goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected actions?"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);

		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected actions?"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);

		goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected actions?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"g_actionid\";");
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		actionWidget.addItem(headerActions);
		
		// create widget header
/*		CComboBox sourceComboBox = new CComboBox("eventsource", Nest.value(data,"eventsource").$(), "submit()");
		sourceComboBox.addItem(EVENT_SOURCE_TRIGGERS, _("Triggers"));
		sourceComboBox.addItem(EVENT_SOURCE_DISCOVERY, _("Discovery"));
		sourceComboBox.addItem(EVENT_SOURCE_AUTO_REGISTRATION, _("Auto registration"));
		sourceComboBox.addItem(EVENT_SOURCE_INTERNAL, _x("Internal", "event source"));
		CForm filterForm = new CForm("get");
		filterForm.addItem(array(_("Event source"), SPACE, sourceComboBox));
		actionWidget.addHeader(SPACE, filterForm);*/

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
				if (!conditions.isEmpty()) {
					conditions.add(BR());
				}
				conditions.add(get_condition_desc(idBean, executor, Nest.value(condition,"conditiontype").asInteger(), Nest.value(condition,"operator").asInteger(), Nest.value(condition,"value").asString()));
			}

			sortOperations(Nest.value(data,"eventsource").asInteger(), Nest.value(action,"operations").asCArray());
			CArray operations = array();
			for(Map operation : (CArray<Map>)Nest.value(action,"operations").asCArray()) {
				operations.add(get_operation_descr(idBean, executor, SHORT_DESCRIPTION, operation));
			}
			
			//自定义属性 用于告警响应页面Ajax动作状态修改
			Object _sid = "";
			Object _go = "";
			Object _g_actionid = "";
			Object _eventsource = "";
			
			CLink status = null;
			if (Nest.value(action,"status").asInteger() == ACTION_STATUS_DISABLED) {
				status  = new CLink(_("Disabled"),
					"actionconf.action?go=activate&g_actionid"+SQUAREBRACKETS+"="+action.get("actionid")+url_param(idBean, "eventsource"),
					"disabled"
				);
				_go = "activate";
			
			} else {
				status = new CLink(_("Enabled"),
					"actionconf.action?go=disable&g_actionid"+SQUAREBRACKETS+"="+action.get("actionid")+url_param(idBean, "eventsource"),
					"enabled"
				);
				_go = "disable";
			}
			//自定义属性赋值
			String[] ids = status.getUrl().split("=");
			_sid = ids[ids.length-1];
			_g_actionid = action.get("actionid");
			_eventsource = url_param(idBean, "eventsource");
			
			status.setAttribute("go", _go);
			status.setAttribute("sid", _sid);
			status.setAttribute("eventsource", _eventsource);
			status.setAttribute("g_actionid", _g_actionid);
			status.setAttribute("onclick", "changeActionStatus(this)");
			status.setAttribute("href", "javascript:void(0)");
			
			
			CCol cstatus = new CCol(new CDiv(status, "switch"));
			
			actionTable.addRow(array(
				new CCheckBox("g_actionid["+Nest.value(action,"actionid").$()+"]", false, null, Nest.value(action,"actionid").asInteger()),
				new CLink(Nest.value(action,"name").$(), "actionconf.action?form=update&actionid="+Nest.value(action,"actionid").$()),
				conditions,
				new CCol(operations, "wraptext"),
				cstatus
			));
		}


		// append table to form
		actionForm.addItem(array(actionTable, Nest.value(data,"paging").$()));

		// append form to widget
		actionWidget.addItem(actionForm);

		return actionWidget;
	}

}
