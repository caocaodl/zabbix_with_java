package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.inArray;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strInArray;
import static com.isoft.iradar.inc.ActionsUtil.condition_operator2str;
import static com.isoft.iradar.inc.ActionsUtil.condition_type2str;
import static com.isoft.iradar.inc.ActionsUtil.count_operations_delay;
import static com.isoft.iradar.inc.ActionsUtil.discovery_object2str;
import static com.isoft.iradar.inc.ActionsUtil.eventType;
import static com.isoft.iradar.inc.ActionsUtil.get_condition_desc;
import static com.isoft.iradar.inc.ActionsUtil.get_opconditions_by_eventsource;
import static com.isoft.iradar.inc.ActionsUtil.get_operation_descr;
import static com.isoft.iradar.inc.ActionsUtil.get_operators_by_conditiontype;
import static com.isoft.iradar.inc.ActionsUtil.operation_type2str;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_MSG_AUTOREG;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_MSG_DISCOVERY;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_MSG_TRIGGER;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_SUBJ_AUTOREG;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_SUBJ_DISCOVERY;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_SUBJ_TRIGGER;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_LIKE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_APPLICATION;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DCHECK;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DHOST_IP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DOBJECT;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DRULE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSERVICE_PORT;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSERVICE_TYPE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DSTATUS;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DUPTIME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DVALUE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_TYPE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_METADATA;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_PROXY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TEMPLATE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TIME_PERIOD;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_SEVERITY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_VALUE;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_DISCOVER;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_DOWN;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_LOST;
import static com.isoft.iradar.inc.Defines.DOBJECT_STATUS_UP;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PASSWORD;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHTYPE_PUBLICKEY;
import static com.isoft.iradar.inc.Defines.LONG_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_COMMAND;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_REMOVE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_DISABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ENABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_REMOVE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_MESSAGE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_TEMPLATE_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_TEMPLATE_REMOVE;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_INTERVAL;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_AGENT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_SERVER;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_GLOBAL_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_SSH;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_TELNET;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SHORT_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.SVC_AGENT;
import static com.isoft.iradar.inc.Defines.SVC_FTP;
import static com.isoft.iradar.inc.Defines.SVC_HTTP;
import static com.isoft.iradar.inc.Defines.SVC_HTTPS;
import static com.isoft.iradar.inc.Defines.SVC_ICMPPING;
import static com.isoft.iradar.inc.Defines.SVC_IMAP;
import static com.isoft.iradar.inc.Defines.SVC_LDAP;
import static com.isoft.iradar.inc.Defines.SVC_NNTP;
import static com.isoft.iradar.inc.Defines.SVC_POP;
import static com.isoft.iradar.inc.Defines.SVC_SMTP;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv1;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv2c;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv3;
import static com.isoft.iradar.inc.Defines.SVC_SSH;
import static com.isoft.iradar.inc.Defines.SVC_TCP;
import static com.isoft.iradar.inc.Defines.SVC_TELNET;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_object_status2str;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.get_requests;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.num2letter;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_rksort;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.UsersUtil.getUserFullname;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRadioButtonList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationActionEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.action.edit.js");
		CWidget actionWidget = new CWidget(null, "action-edit");
		actionWidget.addPageHeader(_("CONFIGURATION OF ACTIONS"));
		
		// create form
		CForm actionForm = new CForm();
		actionForm.setName("action.edit");
		actionForm.addVar("form", Nest.value(data,"form").asString());
		
		if (!empty(Nest.value(data,"actionid").$())) {
			actionForm.addVar("actionid", Nest.value(data,"actionid").asString());
		} else {
			actionForm.addVar("eventsource", Nest.value(data,"eventsource").asString());
		}
		
		// Action tab
		CFormList actionFormList = new CFormList("actionlist");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"action","name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		actionFormList.addRow(_("Name"), nameTextBox);
		
		actionFormList.addRow(_("Default subject"), new CTextBox("def_shortdata", Nest.value(data,"action","def_shortdata").asString(), RDA_TEXTBOX_STANDARD_SIZE));
		actionFormList.addRow(_("Default message"), new CTextArea("def_longdata", Nest.value(data,"action","def_longdata").asString()));
		
		int eventsource = Nest.value(data,"eventsource").asInteger();
		if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
			actionFormList.addRow(_("Recovery message"), new CCheckBox("recovery_msg", Nest.value(data,"action","recovery_msg").asBoolean(), "javascript: submit();", "1"));
			if (Nest.value(data,"action","recovery_msg").asBoolean()) {
				actionFormList.addRow(_("Recovery subject"), new CTextBox("r_shortdata", Nest.value(data,"action","r_shortdata").asString(), RDA_TEXTBOX_STANDARD_SIZE));
				actionFormList.addRow(_("Recovery message"), new CTextArea("r_longdata", Nest.value(data,"action","r_longdata").asString()));
			} else {
				actionForm.addVar("r_shortdata", Nest.value(data,"action","r_shortdata").asString());
				actionForm.addVar("r_longdata", Nest.value(data,"action","r_longdata").asString());
			}
		}
		actionFormList.addRow(_("Enabled"), new CCheckBox("status", !Nest.value(data,"action","status").asBoolean(), null, String.valueOf(ACTION_STATUS_ENABLED)));
		
		// Condition tab
		CFormList conditionFormList = new CFormList("conditionlist");
		
		// create condition table
		CTable conditionTable = new CTable(_("No conditions defined."), "formElementTable");
		conditionTable.attr("id", "conditionTable");
		conditionTable.attr("style", "min-width: 350px;");
		conditionTable.setHeader(array(_("Label"), _("Name"), _("Action")));
		
		int i = 0;
		CArray<Map> conditions = Nest.value(data,"action","conditions").asCArray();
		for (Map condition : conditions) {
			if (!isset(condition, "conditiontype")) {
				condition.put("conditiontype", 0);
			}
			if (!isset(condition, "operator")) {
				condition.put("operator", 0);
			}
			if (!isset(condition, "value")) {
				condition.put("value", "");
			}
			if(!inArray(condition.get("conditiontype"), Nest.array(data,"allowedConditions").asInteger())){
				continue;
			}
			String label = num2letter(i);
			CSpan labelSpan = new CSpan("("+label+")", "label");
			labelSpan.setAttribute("data-conditiontype", condition.get("conditiontype"));
			labelSpan.setAttribute("data-label", label);
			
			conditionTable.addRow(
					array(
						labelSpan,
						get_condition_desc(idBean, executor, Nest.value(condition,"conditiontype").asInteger(), Nest.value(condition,"operator").asInteger(), Nest.value(condition,"value").asString()),
						array(
							new CButton("remove", _("Remove"), "javascript: removeCondition("+i+");", "link_menu"),
							new CVar("conditions["+i+"]", condition)
						)
					),
					null, "conditions_"+i
				);
				i++;
		}
		
		CComboBox calculationTypeComboBox = new CComboBox("evaltype", Nest.value(data,"action","evaltype").asString(), "submit()");
		calculationTypeComboBox.addItem(ACTION_EVAL_TYPE_AND_OR, _("AND / OR"));
		calculationTypeComboBox.addItem(ACTION_EVAL_TYPE_AND, _("AND"));
		calculationTypeComboBox.addItem(ACTION_EVAL_TYPE_OR, _("OR"));
		conditionFormList.addRow(_("Type of calculation"), array(calculationTypeComboBox, new CSpan("", null, "conditionLabel")), false, "conditionRow");
		conditionFormList.addRow(_("Conditions"), new CDiv(conditionTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		
		// append new condition to form list
		CComboBox conditionTypeComboBox = new CComboBox("new_condition[conditiontype]", Nest.value(data,"new_condition","conditiontype").asString(), "submit()");
		Integer[] allowedConditions = Nest.array(data,"allowedConditions").asInteger();
		CArray<Map> callowedConditions = new CArray();
		for (int condition : allowedConditions) {
			callowedConditions.add(map(
					"name", condition_type2str(condition),
					"type" , condition
					));
		}
		data.put("allowedConditions", callowedConditions);
		order_result(callowedConditions, "name");
		for(Map condition:callowedConditions) {
			conditionTypeComboBox.addItem(condition.get("type"), (String)condition.get("name"));
		}
		
		CComboBox conditionOperatorsComboBox = new CComboBox("new_condition[operator]", Nest.value(data,"new_condition","operator").asString());
		int conditiontype = Nest.value(data,"new_condition","conditiontype").asInteger();
		for (int operator:get_operators_by_conditiontype(conditiontype)) {
			conditionOperatorsComboBox.addItem(operator, condition_operator2str(operator));
		}
		
		Object condition = null;
		switch (conditiontype) {
		case CONDITION_TYPE_HOST_GROUP:
			condition = new CMultiSelect(map(
				"name" , "new_condition[value][]",
				"objectName", "hostGroup",
				"objectOptions", map("editable" , true),
				"defaultValue", 0,
				"popup", map(
					"parameters" , "srctbl=host_groups&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value_"+"&srcfld1=groupid&writeonly=1&multiselect=1",
					"width", 450,
					"height", 450
				)
			));
			break;

		case CONDITION_TYPE_TEMPLATE:
			condition = new CMultiSelect(map(
				"name", "new_condition[value][]",
				"objectName", "templates",
				"objectOptions", map("editable" , true),
				"defaultValue", 0,
				"popup", map(
					"parameters", "srctbl=templates&srcfld1=hostid&srcfld2=host&dstfrm="+actionForm.getName()+
						"&dstfld1=new_condition_value_&templated_hosts=1&multiselect=1&writeonly=1",
					"width", 450,
					"height", 450
				)
			));
			break;

		case CONDITION_TYPE_HOST:
			condition = new CMultiSelect(map(
				"name", "new_condition[value][]",
				"objectName", "hosts",
				"objectOptions", map("editable", true),
				"defaultValue", 0,
				"popup", map(
					"parameters", "srctbl=hosts&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value_"+
						"&srcfld1=hostid&writeonly=1&multiselect=1",
					"width", 450,
					"height", 450
				)
			));
			break;

		case CONDITION_TYPE_TRIGGER:
			condition = new CMultiSelect(map(
				"name", "new_condition[value][]",
				"objectName", "triggers",
				"objectOptions", map("editable", true),
				"defaultValue", 0,
				"popup", map(
					"parameters", "srctbl=triggers&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value_"+
						"&srcfld1=triggerid&writeonly=1&multiselect=1&noempty=1",
					"width", 600,
					"height", 450
				)
			));
			break;

		case CONDITION_TYPE_TRIGGER_NAME:
			condition = new CTextBox("new_condition[value]", "", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_TRIGGER_VALUE:
			condition = new CComboBox("new_condition[value]");
			((CComboBox)condition).addItem(TRIGGER_VALUE_FALSE, trigger_value2str(TRIGGER_VALUE_FALSE));
			((CComboBox)condition).addItem(TRIGGER_VALUE_TRUE, trigger_value2str(TRIGGER_VALUE_TRUE));
			break;

		case CONDITION_TYPE_TIME_PERIOD:
			condition = new CTextBox("new_condition[value]", RDA_DEFAULT_INTERVAL, RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_TRIGGER_SEVERITY:
			condition = new CComboBox("new_condition[value]");
			((CComboBox)condition).addItems(getSeverityCaption(idBean, executor));
			break;

		case CONDITION_TYPE_MAINTENANCE:
			condition = new CCol(_("maintenance"));
			break;

		case CONDITION_TYPE_DRULE:
			conditionFormList.addItem(new CVar("new_condition[value]", "0"));
			condition = array(
				new CTextBox("drule", "", RDA_TEXTBOX_STANDARD_SIZE, true),
				SPACE,
				new CButton("btn1", _("Select"),
					"return PopUp(\"popup.action?srctbl=drules&srcfld1=druleid&srcfld2=name"+
						"&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value&dstfld2=drule\", 450, 450);",
					"link_menu"
				)
			);
			break;

		case CONDITION_TYPE_DCHECK:
			conditionFormList.addItem(new CVar("new_condition[value]", "0"));
			condition = array(
				new CTextBox("dcheck", "", RDA_TEXTBOX_STANDARD_SIZE, true),
				SPACE,
				new CButton("btn1", _("Select"),
					"return PopUp(\"popup.action?srctbl=dchecks&srcfld1=dcheckid&srcfld2=name"+
						"&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value&dstfld2=dcheck&writeonly=1\", 450, 450);",
					"link_menu"
				)
			);
			break;

		case CONDITION_TYPE_PROXY:
			conditionFormList.addItem(new CVar("new_condition[value]", "0"));
			condition = array(
				new CTextBox("proxy", "", RDA_TEXTBOX_STANDARD_SIZE, true),
				SPACE,
				new CButton("btn1", _("Select"),
					"return PopUp(\"popup.action?srctbl=proxies&srcfld1=hostid&srcfld2=host"+
						"&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value&dstfld2=proxy"+
						"\", 450, 450);",
					"link_menu"
				)
			);
			break;

		case CONDITION_TYPE_DHOST_IP:
			condition = new CTextBox("new_condition[value]", "192.168.0.1-127,192.168.2.1", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_DSERVICE_TYPE:
			condition = new CComboBox("new_condition[value]");
			for(int svc:new int[]{SVC_SSH, SVC_LDAP, SVC_SMTP, SVC_FTP, SVC_HTTP, SVC_HTTPS, SVC_POP, SVC_NNTP, SVC_IMAP, SVC_TCP, SVC_AGENT, SVC_SNMPv1, SVC_SNMPv2c, SVC_SNMPv3, SVC_ICMPPING, SVC_TELNET}) {
				((CComboBox)condition).addItem(svc,discovery_check_type2str(svc));
			}
			break;

		case CONDITION_TYPE_DSERVICE_PORT:
			condition = new CTextBox("new_condition[value]", "0-1023,1024-49151", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_DSTATUS:
			condition = new CComboBox("new_condition[value]");
			for(int stat:new int[]{DOBJECT_STATUS_UP, DOBJECT_STATUS_DOWN, DOBJECT_STATUS_DISCOVER, DOBJECT_STATUS_LOST}) {
				((CComboBox)condition).addItem(stat, discovery_object_status2str(stat));
			}
			break;

		case CONDITION_TYPE_DOBJECT:
			condition = new CComboBox("new_condition[value]");
			for(int object:new int[]{EVENT_OBJECT_DHOST, EVENT_OBJECT_DSERVICE}) {
				((CComboBox)condition).addItem(object, discovery_object2str(object));
			}
			break;

		case CONDITION_TYPE_DUPTIME:
			condition = new CNumericBox("new_condition[value]", String.valueOf(600), 15);
			break;

		case CONDITION_TYPE_DVALUE:
			condition = new CTextBox("new_condition[value]", "", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_APPLICATION:
			condition = new CTextBox("new_condition[value]", "", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_HOST_NAME:
			condition = new CTextBox("new_condition[value]", "", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		case CONDITION_TYPE_EVENT_TYPE:
			condition = new CComboBox("new_condition[value]", null, null, eventType());
			break;

		case CONDITION_TYPE_HOST_METADATA:
			condition = new CTextBox("new_condition[value]", "", RDA_TEXTBOX_STANDARD_SIZE);
			break;

		default:
			condition = null;
		}
		
		conditionTable = new CTable(null, "newActionConditionTable");
		conditionTable.addRow(array(conditionTypeComboBox, conditionOperatorsComboBox, condition));
		conditionTable.addRow(array(new CSubmit("add_condition", _("Add"), null, "link_menu"), SPACE, SPACE));

		conditionFormList.addRow(_("New condition"), new CDiv(conditionTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		
		// Operation tab
		CFormList operationFormList = new CFormList("operationlist");
		if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
			operationFormList.addRow(_("Default operation step duration"), array(
				new CNumericBox("esc_period", Nest.value(data,"action","esc_period").asString(), 6, false),
				" ("+_("minimum 60 seconds")+")")
			);
		}
		
		// create operation table
		CArray<Map> operations = Nest.value(data,"action","operations").asCArray();
		CArray<Integer> delay = null;
		CTable operationsTable = new CTable(_("No operations defined."), "formElementTable");
		operationsTable.attr("style", "min-width: 600px;");
		if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
			operationsTable.setHeader(array(_("Steps"), _("Details"), _("Start in"), _("Duration (sec)"), _("Action")));
			delay = count_operations_delay(operations, Nest.value(data,"action","esc_period").asInteger());
		} else {
			operationsTable.setHeader(array(_("Details"), _("Action")));
		}
		
		CArray operationRow = null;
		Map operation = null;
		Object operationid = null;
		for (Entry<Object, Map> e : operations.entrySet()) {
			operation = e.getValue();
			operationid = e.getKey();
			if(!inArray(Nest.value(operation, "operationtype").asInteger(), Nest.array(data, "allowedOperations").asInteger())){
				continue;
			}
			if (!isset(operation,"opconditions")) {
				operation.put("opconditions", array());
			}
			if (!isset(operation,"mediatypeid")) {
				operation.put("mediatypeid", 0);
			}

			CSpan details = new CSpan(get_operation_descr(idBean, executor, SHORT_DESCRIPTION, operation));
			details.setHint(get_operation_descr(idBean, executor, LONG_DESCRIPTION, operation));
			
			if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
				String esc_steps_txt = null;
				String esc_period_txt = null;
				String esc_delay_txt = null;
				if(Nest.value(operation, "esc_step_from").asInteger()<1){
					operation.put("esc_step_from", 1);
				}

				// display N-N as N
				esc_steps_txt = (operation.get("esc_step_from").equals(operation.get("esc_step_to")))
					? operation.get("esc_step_from").toString()
					: operation.get("esc_step_from")+" - "+operation.get("esc_step_to");

				esc_period_txt = Nest.value(operation,"esc_period").asInteger()>0 ? operation.get("esc_period").toString() : _("Default");
				esc_delay_txt = delay.get(Nest.value(operation, "esc_step_from").asInteger())>0
						? convert_units(map("value", delay.get(Nest.value(operation, "esc_step_from").asInteger()), "units", "uptime"))
						: _("Immediately");
				
				operationRow = array(
						esc_steps_txt,
						details,
						esc_delay_txt,
						esc_period_txt,
						array(
							new CSubmit("edit_operationid["+operationid+"]", _("Edit"), null, "link_menu"),
							SPACE, SPACE, SPACE,
							array(
								new CButton("remove", _("Remove"), "javascript: removeOperation("+operationid+");", "link_menu"),
								new CVar("operations["+operationid+"]", operation)
							)
						)
					);
			} else {
				operationRow = array(
						details,
						array(
							new CSubmit("edit_operationid["+operationid+"]", _("Edit"), null, "link_menu"),
							SPACE, SPACE, SPACE,
							array(
								new CButton("remove", _("Remove"), "javascript: removeOperation("+operationid+");", "link_menu"),
								new CVar("operations["+operationid+"]", operation)
							)
						)
					);
			}
			operationsTable.addRow(operationRow, null, "operations_"+operationid);
			
			operation.put("opmessage_grp", isset(operation, "opmessage_grp")?rda_toHash(operation.get("opmessage_grp"),"usrgrpid"):null);
			operation.put("opmessage_usr", isset(operation, "opmessage_usr")?rda_toHash(operation.get("opmessage_usr"),"userid"):null);
			operation.put("opcommand_grp", isset(operation, "opcommand_grp")?rda_toHash(operation.get("opcommand_grp"),"groupid"):null);
			operation.put("opcommand_hst", isset(operation, "opcommand_hst")?rda_toHash(operation.get("opcommand_hst"),"hostid"):null);
		}
		
		CArray footer = array();
		if (empty(data.get("new_operation"))) {
			footer.add(new CSubmit("new_operation", _("New"), null, "link_menu"));
		}
		
		operationFormList.addRow(_("Action operations"), new CDiv(array(operationsTable, footer), "objectgroup inlineblock border_dotted ui-corner-all"));
		
		// create new operation table
		if(!empty(data.get("new_operation"))){
			CTable newOperationsTable = new CTable(null, "formElementTable");
			newOperationsTable.addItem(new CVar("new_operation[action]", Nest.value(data,"new_operation", "action").asString()));

			if (isset(Nest.value(data,"new_operation", "id").$())) {
				newOperationsTable.addItem(new CVar("new_operation[id]", Nest.value(data,"new_operation", "id").asString()));
			}
			if (isset(Nest.value(data,"new_operation", "operationid").$())) {
				newOperationsTable.addItem(new CVar("new_operation[operationid]", Nest.value(data,"new_operation", "operationid").asString()));
			}
			
			if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
				CNumericBox stepFrom = new CNumericBox("new_operation[esc_step_from]", Nest.value(data,"new_operation","esc_step_from").asString(), 5);
				stepFrom.attr("size", 6);
				stepFrom.addAction(
					"onchange",
					"javascript:"+stepFrom.getAttribute("onchange")+" if (this.value == 0) this.value = 1;"
				);

				CNumericBox stepTo = new CNumericBox("new_operation[esc_step_to]", Nest.value(data,"new_operation","esc_step_to").asString(), 5);
				stepTo.attr("size", 6);

				CTable stepTable = new CTable();
				stepTable.addRow(array(_("From"), stepFrom), "indent_both");
				stepTable.addRow(
					array(
						_("To"),
						new CCol(array(stepTo, SPACE, _("(0 - infinitely)")))
					),
					"indent_both"
				);

				stepTable.addRow(
					array(
						_("Step duration"),
						new CCol(array(
							new CNumericBox("new_operation[esc_period]", Nest.value(data,"new_operation","esc_period").asString(), 6),
							SPACE,
							_("(minimum 60 seconds, 0 - use action default)")
						))
					),
					"indent_both"
				);

				newOperationsTable.addRow(array(_("Step"), stepTable));
			}
			
			// if multiple operation types are available, display a select
			Integer[] allowedOperations = Nest.array(data,"allowedOperations").asInteger();
			if (allowedOperations.length > 1) {
				CComboBox operationTypeComboBox = new CComboBox(
					"new_operation[operationtype]",
					Nest.value(data,"new_operation","operationtype").asString(), 
					"submit()"
				);
				for (int op:allowedOperations) {
					operationTypeComboBox.addItem(op, operation_type2str(op));
				}
				newOperationsTable.addRow(array(_("Operation type"), operationTypeComboBox), "indent_both");
			} else {// if only one operation is available - show only the label
				int op = allowedOperations[0];
				newOperationsTable.addRow(array(
					_("Operation type"),
					array(operation_type2str(op), new CVar("new_operation[operationtype]", op))
				), "indent_both");
			}
			
			Map new_operation = (Map)data.get("new_operation");
			int operationtype = Nest.value(new_operation, "operationtype").asInteger();
			CArray opmessage = null;
			switch (operationtype) {
			case OPERATION_TYPE_MESSAGE:
				if (!isset(new_operation,"opmessage")) {
					new_operation.put("opmessage_usr", array());
					new_operation.put("opmessage", (opmessage = map("default_msg" , 1, "mediatypeid", 0)));

					if (eventsource == EVENT_SOURCE_TRIGGERS) {
						opmessage.put("subject",ACTION_DEFAULT_SUBJ_TRIGGER);
						opmessage.put("message",ACTION_DEFAULT_MSG_TRIGGER);
					} else if (eventsource == EVENT_SOURCE_DISCOVERY) {
						opmessage.put("subject",ACTION_DEFAULT_SUBJ_DISCOVERY);
						opmessage.put("message",ACTION_DEFAULT_MSG_DISCOVERY);
					} else if (eventsource == EVENT_SOURCE_AUTO_REGISTRATION) {
						opmessage.put("subject",ACTION_DEFAULT_SUBJ_AUTOREG);
						opmessage.put("message",ACTION_DEFAULT_MSG_AUTOREG);
					} else {
						opmessage.put("subject","");
						opmessage.put("message","");
					}
				}
				
				if (!isset(Nest.value(data,"new_operation", "opmessage", "default_msg").$())) {
					Nest.value(data,"new_operation", "opmessage", "default_msg").$(0);
				}
				
				CTable usrgrpList = new CTable(null, "formElementTable");
				usrgrpList.setHeader(array(_("User group"), _("Action")));
				usrgrpList.attr("style", "min-width: 310px;");
				usrgrpList.setAttribute("id", "opmsgUsrgrpList");

				CButton addUsrgrpBtn = new CButton("add", _("Add"), "return PopUp(\"popup.action?dstfrm=action.edit&srctbl=usrgrp&srcfld1=usrgrpid&srcfld2=name&multiselect=1\", 450, 450)", "link_menu");
				addUsrgrpBtn.attr("id", "addusrgrpbtn");
				usrgrpList.addRow(new CRow(new CCol(addUsrgrpBtn, null, "2"), null, "opmsgUsrgrpListFooter"));

				CTable userList = new CTable(null, "formElementTable");
				userList.setHeader(array(_("User"), _("Action")));
				userList.attr("style", "min-width: 310px;");
				userList.setAttribute("id", "opmsgUserList");

				CButton addUserBtn = new CButton("add", _("Add"), "return PopUp(\"popup.action?dstfrm=action.edit&srctbl=users&srcfld1=userid&srcfld2=fullname&multiselect=1\", 450, 450)", "link_menu");
				addUserBtn.attr("id", "adduserbtn");
				userList.addRow(new CRow(new CCol(addUserBtn, null, "2"), null, "opmsgUserListFooter"));
				
				// add participations
				CArray usrgrpids = isset(Nest.value(data,"new_operation","opmessage_grp").$())
					? rda_objectValues(Nest.value(data,"new_operation","opmessage_grp").$(), "usrgrpid")
					: array();

				CArray userids = isset(Nest.value(data,"new_operation","opmessage_usr").$())
					? rda_objectValues(Nest.value(data,"new_operation","opmessage_usr").$(), "userid")
					: array();

				CUserGroupGet ugget = new CUserGroupGet();
				ugget.setUsrgrpIds(usrgrpids.valuesAsLong());
				ugget.setOutput(new String[]{"name"});
				ugget.setSortfield("name");
				
				CArray<Map> usrgrps = API.UserGroup(idBean, executor).get(ugget);
				
				CUserGet uget = new CUserGet();
				uget.setUserIds(userids.valuesAsString());
				uget.setOutput(new String[]{"alias", "name", "surname"});
				uget.setSortfield("alias");
				
				CArray<Map> users = API.User(idBean, executor).get(uget);
				
				for (Map user : users) {
					user.put("fullname", getUserFullname(user));
				}
				
				String jsInsert = "addPopupValues("+rda_jsvalue(map("object","usrgrpid", "values",usrgrps))+");";
				jsInsert += "addPopupValues("+rda_jsvalue(map("object","userid", "values" , users))+");";
				rda_add_post_js(jsInsert);
				
				newOperationsTable.addRow(array(_("Send to User groups"), new CDiv(usrgrpList, "objectgroup inlineblock border_dotted ui-corner-all")));
				newOperationsTable.addRow(array(_("Send to Users"), new CDiv(userList, "objectgroup inlineblock border_dotted ui-corner-all")));

				CComboBox mediaTypeComboBox = new CComboBox("new_operation[opmessage][mediatypeid]", Nest.value(data,"new_operation", "opmessage", "mediatypeid").asString());
				mediaTypeComboBox.addItem(0, "- "+_("All")+" -");
			
				String query = "SELECT mt.mediatypeid,mt.description FROM media_type mt where mt.tenantid='-'";
				CArray<Map> dbMediaTypes = DBselect(executor, query);
				
				if(dbMediaTypes!=null && !dbMediaTypes.isEmpty()){
					for(Map dbMediaType:dbMediaTypes){
						mediaTypeComboBox.addItem(dbMediaType.get("mediatypeid"), Nest.value(dbMediaType, "description").asString());
					}
				}
				
				newOperationsTable.addRow(array(_("Send only to"), mediaTypeComboBox));
				newOperationsTable.addRow(
					array(
						_("Default message"),
						new CCheckBox("new_operation[opmessage][default_msg]", Nest.value(data,"new_operation", "opmessage", "default_msg").asInteger()==1, "javascript: submit();", "1")
					),
					"indent_top"
				);

				if (Nest.value(data,"new_operation", "opmessage", "default_msg").asInteger()!=1) {
					newOperationsTable.addRow(array(
						_("Subject"),
						new CTextBox("new_operation[opmessage][subject]", Nest.value(data,"new_operation", "opmessage", "subject").asString()+ RDA_TEXTBOX_STANDARD_SIZE)
					));
					newOperationsTable.addRow(array(
						_("Message"),
						new CTextArea("new_operation[opmessage][message]", Nest.value(data,"new_operation", "opmessage", "message").asString())
					));
				} else {
					newOperationsTable.addItem(new CVar("new_operation[opmessage][subject]", Nest.value(data,"new_operation", "opmessage", "subject").asString()));
					newOperationsTable.addItem(new CVar("new_operation[opmessage][message]", Nest.value(data,"new_operation", "opmessage", "message").asString()));
				}
				break;
			case OPERATION_TYPE_COMMAND:
				if (!isset(Nest.value(data,"new_operation","opcommand").$())) {
					Nest.value(data,"new_operation","opcommand").$(array());
				}

				Nest.value(data,"new_operation","opcommand","type").$(isset(Nest.value(data,"new_operation","opcommand","type").$())
					? Nest.value(data,"new_operation","opcommand","type").$() : RDA_SCRIPT_TYPE_CUSTOM_SCRIPT);
				Nest.value(data,"new_operation","opcommand","scriptid").$(isset(Nest.value(data,"new_operation","opcommand","scriptid").$())
					? Nest.value(data,"new_operation","opcommand","scriptid").$() : "");
				Nest.value(data,"new_operation","opcommand","execute_on").$(isset(Nest.value(data,"new_operation","opcommand","execute_on").$())
					? Nest.value(data,"new_operation","opcommand","execute_on").$() : RDA_SCRIPT_EXECUTE_ON_AGENT);
				Nest.value(data,"new_operation","opcommand","publickey").$(isset(Nest.value(data,"new_operation","opcommand","publickey").$())
					? Nest.value(data,"new_operation","opcommand","publickey").$() : "");
				Nest.value(data,"new_operation","opcommand","privatekey").$(isset(Nest.value(data,"new_operation","opcommand","privatekey").$())
					? Nest.value(data,"new_operation","opcommand","privatekey").$() : "");
				Nest.value(data,"new_operation","opcommand","authtype").$(isset(Nest.value(data,"new_operation","opcommand","authtype").$())
					? Nest.value(data,"new_operation","opcommand","authtype").$() : ITEM_AUTHTYPE_PASSWORD);
				Nest.value(data,"new_operation","opcommand","username").$(isset(Nest.value(data,"new_operation","opcommand","username").$())
					? Nest.value(data,"new_operation","opcommand","username").$() : "");
				Nest.value(data,"new_operation","opcommand","password").$(isset(Nest.value(data,"new_operation","opcommand","password").$())
					? Nest.value(data,"new_operation","opcommand","password").$() : "");
				Nest.value(data,"new_operation","opcommand","port").$(isset(Nest.value(data,"new_operation","opcommand","port").$())
					? Nest.value(data,"new_operation","opcommand","port").$() : "");
				Nest.value(data,"new_operation","opcommand","command").$(isset(Nest.value(data,"new_operation","opcommand","command").$())
					? Nest.value(data,"new_operation","opcommand","command").$() : "");
				Nest.value(data,"new_operation","opcommand","script").$("");
				if (!rda_empty(Nest.value(data,"new_operation","opcommand","scriptid").$())) {
					CScriptGet sget = new CScriptGet();
					sget.setScriptIds(Nest.array(data,"new_operation","opcommand","scriptid").asLong());
					sget.setOutput(API_OUTPUT_EXTEND);
					CArray<Map> userScripts = API.Script(idBean, executor).get(sget);
					Map userScript = null;
					if ((userScript  = reset(userScripts))!=null) {
						Nest.value(data,"new_operation","opcommand","script").$(Nest.value(userScript,"name").$());
					}
				}

				CTable cmdList = new CTable(null, "formElementTable");
				cmdList.attr("style", "min-width: 310px;");
				cmdList.setHeader(array(_("Target"), _("Action")));

				CButton addCmdBtn = new CButton("add", _("New"), "javascript: showOpCmdForm(0, \"new\");", "link_menu");
				cmdList.addRow(new CRow(new CCol(addCmdBtn, null, "3"), null, "opCmdListFooter"));

				// add participations
				if (!isset(Nest.value(data,"new_operation","opcommand_grp").$())) {
					Nest.value(data,"new_operation","opcommand_grp").$(array());
				}
				if (!isset(Nest.value(data,"new_operation","opcommand_hst").$())) {
					Nest.value(data,"new_operation","opcommand_hst").$(array());
				}

				CHostGet hget = new CHostGet();
				hget.setHostIds(rda_objectValues(Nest.value(data,"new_operation","opcommand_hst").$(), "hostid").valuesAsLong());
				hget.setOutput(new String[]{"hostid", "name"});
				hget.setPreserveKeys(true);
				hget.setEditable(true);
				CArray<Map> hosts = API.Host(idBean, executor).get(hget);
				Nest.value(data,"new_operation","opcommand_hst").$(array_values(Nest.value(data,"new_operation","opcommand_hst").asCArray()));
				for(Entry<Object,Map> e:(Set<Map.Entry<Object,Map>>)Nest.value(data,"new_operation","opcommand_hst").asCArray().entrySet()) {
					Object ohnum = e.getKey();
					Map cmd = e.getValue();
					Nest.value(data,"new_operation","opcommand_hst", ohnum.toString(),"name").$((Nest.value(cmd,"hostid").asLong() > 0) ? Nest.value(hosts.get(cmd.get("hostid")),"name").asString() : "");
				}
				order_result(Nest.value(data,"new_operation","opcommand_hst").asCArray(), "name");
				
				CHostGroupGet hgget = new CHostGroupGet();
				hgget.setGroupIds(rda_objectValues(Nest.value(data,"new_operation","opcommand_grp").$(), "groupid").valuesAsLong());
				hgget.setOutput(new String[]{"groupid", "name"});
				hgget.setPreserveKeys(true);
				hgget.setEditable(true);
				CArray<Map> groups = API.HostGroup(idBean, executor).get(hgget);

				Nest.value(data,"new_operation","opcommand_grp").$(array_values(Nest.value(data,"new_operation","opcommand_grp").asCArray()));
				for(Entry<Object,Map> e:(Set<Map.Entry<Object,Map>>)Nest.value(data,"new_operation","opcommand_grp").asCArray().entrySet()) {
					Object ognum = e.getKey();
					Map cmd = e.getValue();
					Nest.value(data,"new_operation","opcommand_grp", ognum.toString(),"name").$(Nest.value(groups.get(cmd.get("groupid")),"name").asString());
				}
				order_result(Nest.value(data,"new_operation","opcommand_grp").asCArray(), "name");

				// js add commands
				jsInsert = "addPopupValues("+rda_jsvalue(map("object", "hostid", "values", Nest.value(data,"new_operation","opcommand_hst").$()))+");";
				jsInsert += "addPopupValues("+rda_jsvalue(map("object", "groupid", "values", Nest.value(data,"new_operation","opcommand_grp").$()))+");";
				rda_add_post_js(jsInsert);

				// target list
				CDiv cmdList1 = new CDiv(cmdList, "objectgroup border_dotted ui-corner-all inlineblock");
				cmdList1.setAttribute("id", "opCmdList");
				newOperationsTable.addRow(array(_("Target list"), cmdList1), "indent_top");

				// type
				CComboBox typeComboBox = new CComboBox("new_operation[opcommand][type]", Nest.value(data,"new_operation","opcommand","type").asString(), "javascript: showOpTypeForm();");
				typeComboBox.addItem(RDA_SCRIPT_TYPE_IPMI, _("IPMI"));
				typeComboBox.addItem(RDA_SCRIPT_TYPE_CUSTOM_SCRIPT, _("Custom script"));
				typeComboBox.addItem(RDA_SCRIPT_TYPE_SSH, _("SSH"));
				typeComboBox.addItem(RDA_SCRIPT_TYPE_TELNET, _("Telnet"));
				typeComboBox.addItem(RDA_SCRIPT_TYPE_GLOBAL_SCRIPT, _("Global script"));

				CVar userScriptId = new CVar("new_operation[opcommand][scriptid]", Nest.value(data,"new_operation","opcommand","scriptid").$());
				CTextBox userScriptName = new CTextBox("new_operation[opcommand][script]", Nest.value(data,"new_operation","opcommand","script").asString(), 32, true);
				CButton userScriptSelect = new CButton("select_opcommand_script", _("Select"), null, "link_menu");

				CDiv userScript = new CDiv(array(userScriptId, userScriptName, SPACE, userScriptSelect), "class_opcommand_userscript inlineblock hidden");

				newOperationsTable.addRow(array(_("Type"), array(typeComboBox, SPACE, userScript)), "indent_bottom");

				// script
				CRadioButtonList executeOnRadioButton = new CRadioButtonList("new_operation[opcommand][execute_on]", Nest.value(data,"new_operation","opcommand","execute_on").asString());
				executeOnRadioButton.makeVertical();
				executeOnRadioButton.addValue(SPACE+_("iRadar agent")+SPACE, String.valueOf(RDA_SCRIPT_EXECUTE_ON_AGENT));
				executeOnRadioButton.addValue(SPACE+_("iRadar server")+SPACE, String.valueOf(RDA_SCRIPT_EXECUTE_ON_SERVER));
				newOperationsTable.addRow(array(_("Execute on"), new CDiv(executeOnRadioButton, "objectgroup border_dotted ui-corner-all inlineblock")), "class_opcommand_execute_on hidden indent_both");

				// ssh
				CComboBox authTypeComboBox = new CComboBox("new_operation[opcommand][authtype]", Nest.value(data,"new_operation","opcommand","authtype").asString(), "javascript: showOpTypeAuth();");
				authTypeComboBox.addItem(ITEM_AUTHTYPE_PASSWORD, _("Password"));
				authTypeComboBox.addItem(ITEM_AUTHTYPE_PUBLICKEY, _("Public key"));

				newOperationsTable.addRow(
					array(
						_("Authentication method"),
						authTypeComboBox
					),
					"class_authentication_method hidden"
				);
				newOperationsTable.addRow(
					array(
						_("User name"),
						new CTextBox("new_operation[opcommand][username]", Nest.value(data,"new_operation","opcommand","username").asString(), RDA_TEXTBOX_SMALL_SIZE)
					),
					"class_authentication_username hidden indent_both"
				);
				newOperationsTable.addRow(
					array(
						_("Public key file"),
						new CTextBox("new_operation[opcommand][publickey]", Nest.value(data,"new_operation","opcommand","publickey").asString(), RDA_TEXTBOX_SMALL_SIZE)
					),
					"class_authentication_publickey hidden indent_both"
				);
				newOperationsTable.addRow(
					array(
						_("Private key file"),
						new CTextBox("new_operation[opcommand][privatekey]", Nest.value(data,"new_operation","opcommand","privatekey").asString(), RDA_TEXTBOX_SMALL_SIZE)
					),
					"class_authentication_privatekey hidden indent_both"
				);
				newOperationsTable.addRow(
					array(
						_("Password"),
						new CTextBox("new_operation[opcommand][password]", Nest.value(data,"new_operation","opcommand","password").asString(), RDA_TEXTBOX_SMALL_SIZE)
					),
					"class_authentication_password hidden indent_both"
				);

				// set custom id because otherwise they are set based on name (sick!) and produce duplicate ids
				CTextBox passphraseCB = new CTextBox("new_operation[opcommand][password]", Nest.value(data,"new_operation","opcommand","password").asString(), RDA_TEXTBOX_SMALL_SIZE);
				passphraseCB.attr("id", "new_operation_opcommand_passphrase");
				newOperationsTable.addRow(array(_("Key passphrase"), passphraseCB), "class_authentication_passphrase hidden");

				// ssh && telnet
				newOperationsTable.addRow(
					array(
						_("Port"),
						new CTextBox("new_operation[opcommand][port]", Nest.value(data,"new_operation","opcommand","port").asString(), RDA_TEXTBOX_SMALL_SIZE)
					),
					"class_opcommand_port hidden indent_both"
				);

				// command
				CTextArea commandTextArea = new CTextArea("new_operation[opcommand][command]", Nest.value(data,"new_operation","opcommand","command").asString());
				newOperationsTable.addRow(array(_("Commands"), commandTextArea), "class_opcommand_command hidden indent_both");

				CTextBox commandIpmiTextBox = new CTextBox("new_operation[opcommand][command]", Nest.value(data,"new_operation","opcommand","command").asString(), RDA_TEXTBOX_STANDARD_SIZE);
				commandIpmiTextBox.attr("id", "opcommand_command_ipmi");
				newOperationsTable.addRow(array(_("Commands"), commandIpmiTextBox), "class_opcommand_command_ipmi hidden indent_both");
				break;
			case OPERATION_TYPE_HOST_ADD:
			case OPERATION_TYPE_HOST_REMOVE:
			case OPERATION_TYPE_HOST_ENABLE:
			case OPERATION_TYPE_HOST_DISABLE:
				newOperationsTable.addItem(new CVar("new_operation[object]", 0));
				newOperationsTable.addItem(new CVar("new_operation[objectid]", 0));
				newOperationsTable.addItem(new CVar("new_operation[shortdata]", ""));
				newOperationsTable.addItem(new CVar("new_operation[longdata]", ""));
				break;
			case OPERATION_TYPE_GROUP_ADD:
			case OPERATION_TYPE_GROUP_REMOVE:
				if (!isset(Nest.value(data,"new_operation","opgroup").$())) {
					Nest.value(data,"new_operation","opgroup").$(array());
				}

				CTable groupList = new CTable();
				groupList.setAttribute("id", "opGroupList");
				groupList.addRow(new CRow(
					new CCol(
						new CMultiSelect(map(
							"name", "discoveryHostGroup",
							"objectName", "hostGroup",
							"objectOptions", map("editable", true),
							"popup", map(
								"parameters", "srctbl=host_groups&dstfrm="+actionForm.getName()+"&dstfld1=discoveryHostGroup&srcfld1=groupid&writeonly=1&multiselect=1",
								"width", 450,
								"height", 450)
						)),
						null, "2"
					),
					null,
					"opGroupListFooter"
				));
				groupList.addRow(new CCol(new CButton("add", _("Add"), "return addDiscoveryHostGroup();", "link_menu"), null, "2"));

				// load host groups
				CArray groupIds = isset(Nest.value(data,"new_operation","opgroup").$())
					? rda_objectValues(Nest.value(data,"new_operation","opgroup").$(), "groupid")
					: array();

				if (!empty(groupIds)) {
					hgget = new CHostGroupGet();
					hgget.setGroupIds(groupIds.valuesAsLong());
					hgget.setOutput(new String[]{"groupid", "name"});
					CArray<Map> hostGroups = API.HostGroup(idBean, executor).get(hgget);
					order_result(hostGroups, "name");

					jsInsert = "";
					jsInsert += "addPopupValues("+rda_jsvalue(map("object", "dsc_groupid", "values", hostGroups))+");";
					rda_add_post_js(jsInsert);
				}

				String caption = (OPERATION_TYPE_GROUP_ADD == Nest.value(data,"new_operation","operationtype").asInteger())
					? _("Add to host groups")
					: _("Remove from host groups");

				newOperationsTable.addRow(array(caption, new CDiv(groupList, "objectgroup inlineblock border_dotted ui-corner-all")));
				break;
			case OPERATION_TYPE_TEMPLATE_ADD:
			case OPERATION_TYPE_TEMPLATE_REMOVE:
				if (!isset(Nest.value(data,"new_operation","optemplate").$())) {
					Nest.value(data,"new_operation","optemplate").$(array());
				}

				CTable templateList = new CTable();
				templateList.setAttribute("id", "opTemplateList");
				templateList.addRow(new CRow(
					new CCol(
						new CMultiSelect(map(
							"name" , "discoveryTemplates",
							"objectName" , "templates",
							"objectOptions" , map("editable" , true),
							"popup" , map(
								"parameters" , "srctbl=templates&srcfld1=hostid&srcfld2=host&dstfrm="+actionForm.getName()+"&dstfld1=discoveryTemplates&templated_hosts=1&multiselect=1&writeonly=1",
								"width" , 450,
								"height" , 450
							)
						)),
						null, "2"
					),
					null,
					"opTemplateListFooter"
				));
				templateList.addRow(new CCol(new CButton("add", _("Add"), "return addDiscoveryTemplates();", "link_menu"), null, "2"));

				// load templates
				CArray templateIds = isset(Nest.value(data,"new_operation","optemplate").$())
					? rda_objectValues(Nest.value(data,"new_operation","optemplate").$(), "templateid")
					: array();

				if (!empty(templateIds)) {
					CTemplateGet tget = new CTemplateGet();
					tget.setTemplateIds(templateIds.valuesAsLong());
					tget.setOutput(new String[]{"templateid", "name"});
					CArray<Map> templates = API.Template(idBean, executor).get(tget);
					order_result(templates, "name");

					jsInsert = "";
					jsInsert += "addPopupValues("+rda_jsvalue(map("object" , "dsc_templateid", "values" , templates))+");";
					rda_add_post_js(jsInsert);
				}

				caption = (OPERATION_TYPE_TEMPLATE_ADD == Nest.value(data,"new_operation","operationtype").asInteger())
					? _("Link with templates")
					: _("Unlink from templates");

				newOperationsTable.addRow(array(caption, new CDiv(templateList, "objectgroup inlineblock border_dotted ui-corner-all")));
				break;
			}
			
			// append operation conditions to form list
			if (Nest.value(data,"eventsource").asInteger() == 0) {
				if (!isset(Nest.value(data,"new_operation","opconditions").$())) {
					Nest.value(data,"new_operation","opconditions").$(array());
				} else {
					rda_rksort(Nest.value(data,"new_operation","opconditions").asCArray());
				}

				CArray<Integer> allowed_opconditions = get_opconditions_by_eventsource(Nest.value(data,"eventsource").asInteger());
				CArray<CArray<String>> grouped_opconditions = array();

				CTable operationConditionsTable = new CTable(_("No conditions defined."), "formElementTable");
				operationConditionsTable.attr("style", "min-width: 310px;");
				operationConditionsTable.setHeader(array(_("Label"), _("Name"), _("Action")));

				i = 0;
				for(Map opcondition:(CArray<Map>)Nest.value(data,"new_operation","opconditions").asCArray()) {
					if (!isset(Nest.value(opcondition,"conditiontype").$())) {
						Nest.value(opcondition,"conditiontype").$(0);
					}
					if (!isset(Nest.value(opcondition,"operator").$())) {
						Nest.value(opcondition,"operator").$(0);
					}
					if (!isset(Nest.value(opcondition,"value").$())) {
						Nest.value(opcondition,"value").$(0);
					} 
					if (!strInArray(Nest.value(opcondition,"conditiontype").asString(), allowed_opconditions.valuesAsString())) {
						continue;
					}

					String label = num2letter(i);
					operationConditionsTable.addRow(
						array(
							"("+label+")",
							get_condition_desc(idBean, executor, Nest.value(opcondition,"conditiontype").asInteger(), Nest.value(opcondition,"operator").asInteger(), Nest.value(opcondition,"value").asString()),
							array(
								new CButton("remove", _("Remove"), "javascript: removeOperationCondition("+i+");", "link_menu"),
								new CVar("new_operation[opconditions]["+i+"][conditiontype]", Nest.value(opcondition,"conditiontype").$()),
								new CVar("new_operation[opconditions]["+i+"][operator]", Nest.value(opcondition,"operator").$()),
								new CVar("new_operation[opconditions]["+i+"][value]", Nest.value(opcondition,"value").$())
							)
						),
						null, "opconditions_"+i
					);
					if(!isset(grouped_opconditions,Nest.value(opcondition,"conditiontype").asInteger())){
						Nest.value(grouped_opconditions,Nest.value(opcondition,"conditiontype").asInteger()).$(array());
					}
					Nest.value(grouped_opconditions,Nest.value(opcondition,"conditiontype").asInteger()).asCArray().add(label);
					i++;
				}

				if (operationConditionsTable.itemsCount() > 1) {
					String glog_op = null;
					String group_op = null;
					switch (Nest.value(data,"new_operation","evaltype").asInteger()) {
						case ACTION_EVAL_TYPE_AND:
							group_op = glog_op = _("and");
							break;
						case ACTION_EVAL_TYPE_OR:
							group_op = glog_op = _("or");
							break;
						default:
							group_op = _("or");
							glog_op = _("and");
							break;
					}
					for(Entry<Object,CArray<String>> e:grouped_opconditions.entrySet()) {
						Object id = e.getKey(); 
						CArray tcondition = e.getValue();
						grouped_opconditions.put(id, "("+implode(" "+group_op+" ", tcondition)+")");
					}
					String tgrouped_opconditions = implode(" "+glog_op+" ", grouped_opconditions);

					CComboBox calcTypeComboBox = new CComboBox("new_operation[evaltype]", Nest.value(data,"new_operation","evaltype").asString(), "submit()");
					calcTypeComboBox.addItem(ACTION_EVAL_TYPE_AND_OR, _("AND / OR"));
					calcTypeComboBox.addItem(ACTION_EVAL_TYPE_AND, _("AND"));
					calcTypeComboBox.addItem(ACTION_EVAL_TYPE_OR, _("OR"));

					newOperationsTable.addRow(array(
						_("Type of calculation"),
						array(
							calcTypeComboBox,
							new CTextBox("preview", tgrouped_opconditions, RDA_TEXTBOX_STANDARD_SIZE, true)
						)
					));
				} else {
					operationConditionsTable.addItem(new CVar("new_operation[evaltype]", ACTION_EVAL_TYPE_AND_OR));
				}
				if (!isset(_REQUEST, "new_opcondition")) {
					operationConditionsTable.addRow(new CCol(new CSubmit("new_opcondition", _("New"), null, "link_menu")));
				}
				newOperationsTable.addRow(array(_("Conditions"), new CDiv(operationConditionsTable, "objectgroup inlineblock border_dotted ui-corner-all")), "indent_top");
			}
			
			// append new operation condition to form list
			if (isset(_REQUEST, "new_opcondition")) {
				CTable newOperationConditionTable = new CTable(null, "formElementTable");

				CArray<Integer> allowedOpConditions = get_opconditions_by_eventsource(Nest.value(data,"eventsource").asInteger());

				CArray<String> new_opcondition = get_requests("new_opcondition", array());
				if (!isArray(new_opcondition)) {
					new_opcondition = array();
				}

				if (empty(new_opcondition)) {
					Nest.value(new_opcondition,"conditiontype").$(CONDITION_TYPE_EVENT_ACKNOWLEDGED);
					Nest.value(new_opcondition,"operator").$(CONDITION_OPERATOR_LIKE);
					Nest.value(new_opcondition,"value").$(0);
				}

				if (!strInArray(Nest.value(new_opcondition,"conditiontype").asString(), allowedOpConditions.valuesAsString())) {
					Nest.value(new_opcondition,"conditiontype").$(allowedOpConditions.get(0));
				}

				CArray rowCondition = array();
				conditionTypeComboBox = new CComboBox("new_opcondition[conditiontype]", Nest.value(new_opcondition,"conditiontype").asString(), "submit()");
				
				for(int opcondition:allowedOpConditions) {
					conditionTypeComboBox.addItem(opcondition, condition_type2str(opcondition));
				}
				array_push(rowCondition, conditionTypeComboBox);

				CComboBox operationConditionComboBox = new CComboBox("new_opcondition[operator]");
				for(int operationCondition: get_operators_by_conditiontype(Nest.value(new_opcondition,"conditiontype").asInteger())) {
					operationConditionComboBox.addItem(operationCondition, condition_operator2str(operationCondition));
				}
				array_push(rowCondition, operationConditionComboBox);

				if (Nest.value(new_opcondition,"conditiontype").asInteger() == CONDITION_TYPE_EVENT_ACKNOWLEDGED) {
					CComboBox operationConditionValueComboBox = new CComboBox("new_opcondition[value]", Nest.value(new_opcondition,"value").asString());
					operationConditionValueComboBox.addItem(0, _("Not Ack"));
					operationConditionValueComboBox.addItem(1, _("Ack"));
					rowCondition.add(operationConditionValueComboBox);
				}
				newOperationConditionTable.addRow(rowCondition);

				CArray newOperationConditionFooter = array(
					new CSubmit("add_opcondition", _("Add"), null, "link_menu"),
					SPACE+SPACE,
					new CSubmit("cancel_new_opcondition", _("Cancel"), null, "link_menu")
				);

				newOperationsTable.addRow(array(_("Operation condition"), new CDiv(array(newOperationConditionTable, newOperationConditionFooter), "objectgroup inlineblock border_dotted ui-corner-all")));
			}
			
			footer = array(
					new CSubmit("add_operation", (Nest.value(data,"new_operation","action").$() == "update") ? _("Update") : _("Add"), null, "link_menu"),
					SPACE+SPACE,
					new CSubmit("cancel_new_operation", _("Cancel"), null, "link_menu")
				);
			operationFormList.addRow(_("Operation details"), new CDiv(array(newOperationsTable, footer), "objectgroup inlineblock border_dotted ui-corner-all"));
		}
		
		// append tabs to form
		CTabView actionTabs = new CTabView();
		if (!hasRequest("form_refresh")) {
			actionTabs.setSelected("0");
		}
		actionTabs.addTab("actionTab", _("Action"), actionFormList);
		actionTabs.addTab("conditionTab", _("Conditions"), conditionFormList);
		actionTabs.addTab("operationTab", _("Operations"), operationFormList);
		actionForm.addItem(actionTabs);
		
		// append buttons to form
		CArray others = array();
		if (!empty(Nest.value(data,"actionid").$())) {
			others.add(new CButton("clone", _("Clone")));
			others.add(new CButtonDelete(_("Delete current action?"), url_param(idBean, "form")+url_param(idBean, "eventsource")+url_param(idBean, "actionid")));
		}
		others.add(new CButtonCancel(url_param(idBean, "actiontype")));

		actionForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), others));
		// append form to widget
		actionWidget.addItem(actionForm);

		return actionWidget;
	}

}
