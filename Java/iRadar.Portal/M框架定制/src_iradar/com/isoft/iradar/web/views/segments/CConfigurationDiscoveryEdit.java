package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.inArray;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strInArray;
import static com.isoft.iradar.inc.ActionsUtil.condition_operator2str;
import static com.isoft.iradar.inc.ActionsUtil.condition_type2str;
import static com.isoft.iradar.inc.ActionsUtil.count_operations_delay;
import static com.isoft.iradar.inc.ActionsUtil.get_condition_desc;
import static com.isoft.iradar.inc.ActionsUtil.get_opconditions_by_eventsource;
import static com.isoft.iradar.inc.ActionsUtil.get_operation_descr;
import static com.isoft.iradar.inc.ActionsUtil.get_operators_by_conditiontype;
import static com.isoft.iradar.inc.ActionsUtil.operation_type2str;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_OR;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_LIKE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DHOST_IP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DVALUE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_EVENT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_PROXY;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.LONG_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_GROUP_REMOVE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_SMALL_SIZE;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SHORT_DESCRIPTION;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.get_requests;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.num2letter;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_formatDomId;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_rksort;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CInput;
import com.isoft.iradar.tags.CMultiSelect;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRadioButtonList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationDiscoveryEdit extends CViewSegment {
	
	public static int ACTIONTABLECONDITIONVALUELENGTH = 39;
	public static int ACTIONTABLECONDITIONVALUELENGTHOFFSET = 4;

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		RadarContext.getContext().getRequest().setAttribute("data", data);
		includeSubView("js/configuration.discovery.edit.js");
		includeSubView("js/configuration.action.edit.js");

		CWidget discoveryWidget = new CWidget(null, "discovery_edit");

		
		if(isset(_REQUEST, "form_refresh")) {
			String form_refresh = Nest.value(_REQUEST, "form_refresh").asString();
			if(form_refresh.endsWith("_")) {
				form_refresh = form_refresh.substring(0, form_refresh.length()-1);
				Nest.value(data, "new_condition_conditiontype").$(true);
			}
		}
		
		// create form
		CForm discoveryForm = new CForm();
		discoveryForm.setName("discoveryForm");
		discoveryForm.addVar("form", Nest.value(data,"form").$());
		if (!empty(Nest.value(data,"druleid").$())) {
			discoveryForm.addVar("druleid", Nest.value(data,"druleid").$());
		}

		// create form list
		CFormList discoveryFormList = new CFormList("discoveryFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"drule","name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		CSpan cs =new CSpan(_("Name"));
		discoveryFormList.addRow(cs, nameTextBox);

		if(Feature.showProxy) {
			// append proxy to form list
			CComboBox proxyComboBox = new CComboBox("proxy_hostid", Nest.value(data,"drule","proxy_hostid").$());
			proxyComboBox.addItem(0, _("No proxy"));
			for(Map proxy:(CArray<Map>)Nest.value(data,"proxies").asCArray()) {
				proxyComboBox.addItem(Nest.value(proxy,"proxyid").$(), Nest.value(proxy,"host").asString());
			}
			discoveryFormList.addRow(_("Discovery by proxy"), proxyComboBox);
		}
		discoveryFormList.addRow(_("IP range"), new CTextBox("iprange", Nest.value(data,"drule","iprange").asString(), RDA_TEXTBOX_SMALL_SIZE));
		discoveryFormList.addRow(_("Delay (in sec)"), new CNumericBox("delay", Nest.value(data,"drule","delay").asString(), 8));

		// append checks to form list
		CTable checkTable = new CTable(null, "formElementTable");
		checkTable.addRow(new CRow(
			new CCol(
				new CButton("newCheck", _("New"), null, "link_menu new"),
				null,
				3
			),
			"cmd_row",
			"dcheckListFooter"
		));
		discoveryFormList.addRow(_("Checks"),
			new CDiv(checkTable, "objectgroup inlineblock border_dotted ui-corner-all", "dcheckList"));

		// append uniqueness criteria to form list
		CRadioButtonList uniquenessCriteriaRadio = new CRadioButtonList("uniqueness_criteria", Nest.value(data,"drule","uniqueness_criteria").asString());
		uniquenessCriteriaRadio.addValue(SPACE+_("IP address"), "-1", true, rda_formatDomId("uniqueness_criteria_ip"));
		discoveryFormList.addRow(_("Device uniqueness criteria"),
			new CDiv(uniquenessCriteriaRadio, "objectgroup inlineblock border_dotted ui-corner-all", "uniqList"));

		// append status to form list
		boolean status = (empty(Nest.value(data,"druleid").$()) && empty(Nest.value(data,"form_refresh").$()))
			? true
			: (Nest.value(data,"drule","status").asInteger() == DRULE_STATUS_ACTIVE);

		discoveryFormList.addRow(_("Enabled"), new CCheckBox("status", status, null, 1));
		CButton next=new CButton();
		next.setAttribute("onclick", "javascript:selectNext();");
		next.attr("value", _("nextStep"));
		if(empty(Nest.value(data,"druleid").$())){}else{}
		discoveryFormList.addRow(makeFormFooter(
			null,
			array(
				next,
				createCloneBtn(data),
				new CButtonCancel()
			)
		),null, false, null, "btns_li");
		// append tabs to form
		CTabView discoveryTabs = new CTabView();
		discoveryTabs.addTab("druleTab", _("Discovery rule"), discoveryFormList);
		//发现规则添加编辑跳转标识
		addActionTabs(idBean, executor, data, discoveryForm, discoveryTabs);
		discoveryForm.addItem(discoveryTabs);

		discoveryWidget.addItem(discoveryForm);
		
		JsUtil.rda_add_post_js("setTimeout(function(){$=jQuery;" +
				"$('#tabs LI').each(function(i){" +
					"if(i==0) return;" +
					"$(this).before('<li class=\"next\" />');" +
				"})" +
			"}, 0);");

		return discoveryWidget;
	}
	
	public static CSubmit createCloneBtn(Map data) {
		if(!"clone".equals(data.get("form"))) {
			CSubmit btn = new CSubmit("clone", _("Clone"));
			btn.setAttribute("onClick", "javascript:checkSubmit()");
			btn.setAttribute("id", "_clone_");
			return btn;
		}
		return null;
	}
	
	/**
	 * 添加发现动作的Tab页
	 * 
	 * @param executor
	 * @param data
	 * @param actionForm
	 * @return
	 */
	private void addActionTabs(IIdentityBean idBean, SQLExecutor executor, Map data, CForm actionForm, CTabView discoveryTabs) {
		int eventsource = EVENT_SOURCE_DISCOVERY;
		
		// Condition tab
		CFormList conditionFormList = new CFormList("conditionlist",IMonConsts.STYLE_CLASS_MULTLINE);
		
		// create condition table
		CTable conditionTable = new CTable(_("No conditions defined."), "formElementTable");
		conditionTable.attr("id", "conditionTable");
		conditionTable.attr("style", "min-width: 350px;");
		conditionTable.setHeader(array(_("Label"), _("Name"), _("Action operations")));
		int i = 0;
		CArray<Map> conditions=new CArray<Map>();
		CArray<Map> conditionss = Nest.value(data,"action","conditions").asCArray();
		if(conditionss.size()>0){
			for (Map cond : conditionss) {
				Integer type = Nest.value(cond, "conditiontype").asInteger();
				if (type != Defines.CONDITION_TYPE_DRULE) {
					conditions.push(cond);
				}
			}
			Nest.value(data,"action","conditions").$(conditions);
		}
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
			
			String value = Nest.value(condition,"value").asString();
			if(!empty(value) && value.length()>ACTIONTABLECONDITIONVALUELENGTH){
				value = value.substring(0, ACTIONTABLECONDITIONVALUELENGTH-ACTIONTABLECONDITIONVALUELENGTHOFFSET)+"...";
			}
			conditionTable.addRow(
				array(
					labelSpan,
					get_condition_desc(idBean, executor, 
						Nest.value(condition,"conditiontype").asInteger(), 
						Nest.value(condition,"operator").asInteger(), 
						value
					),
					array(
						new CButton("remove", _("Remove"), "javascript: removeCondition("+i+");", "link_menu icon remove"),
						new CVar("conditions["+i+"]", condition)
					)
				),
				null, 
				"conditions_"+i
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
		CComboBox conditionTypeComboBox = new CComboBox("new_condition[conditiontype]", Nest.value(data,"new_condition","conditiontype").asString(), actionForm.getName()+".form_refresh.value+='_';submit()");
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
			case CONDITION_TYPE_PROXY:
				conditionFormList.addItem(new CVar("new_condition[value]", "0"));
				condition = array(
					new CTextBox("proxy", "", RDA_TEXTBOX_STANDARD_SIZE, true),
					SPACE,
					new CButton("btn1", _("Select"),
						"return PopUp(\"popup.action?srctbl=proxies&srcfld1=hostid&srcfld2=host"+
							"&dstfrm="+actionForm.getName()+"&dstfld1=new_condition_value&dstfld2=proxy"+
							"\", 450, 450);",
						"link_menu select-popup"
					)
				);
				break;
	
			case CONDITION_TYPE_DHOST_IP:
				condition = new CTextBox("new_condition[value]", "192.168.0.1-127,192.168.2.1", RDA_TEXTBOX_STANDARD_SIZE);
				break;
	
			case CONDITION_TYPE_DVALUE:
				condition = new CTextBox("new_condition[value]", "", RDA_TEXTBOX_STANDARD_SIZE);
				break;
				
			default:
				condition = null;
		}
		
		conditionTable = new CTable(null, "newActionConditionTable");
		conditionTable.addRow(array(conditionTypeComboBox, conditionOperatorsComboBox, condition));
		conditionTable.addRow(new CRow(new CCol(new CSubmit("add_condition", _("Add"), null, "link_menu add"),null,3),"cmd_row"));
		
		conditionFormList.addRow(_("New condition"), new CDiv(conditionTable, "objectgroup inlineblock border_dotted ui-corner-all"));
		CButton next=new CButton();
		next.setAttribute("onclick", "javascript:selectNextTwo();");
		next.attr("value", _("nextStep"));
		CButton next1=new CButton();
		next1.setAttribute("onclick", "javascript:selectQTwo();");
		next1.attr("value", _("upStep"));
		conditionFormList.addRow(makeFormFooter(
			null,
			array(
				next1,
				next,
				createCloneBtn(data),
				new CButtonCancel()
			)
		),null, false, null, "btns_li");
		// Operation tab
		CFormList operationFormList = new CFormList("operationlist", IMonConsts.STYLE_CLASS_MULTLINE);
		if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
			operationFormList.addRow(_("Default operation step duration"), array(
				new CNumericBox("esc_period", Nest.value(data,"action","esc_period").asString(), 6, false),
				" ("+_("minimum 60 seconds")+")")
			);
		}

		CArray<Map> operations =new CArray<Map>();
		CArray<Map> operationss= Nest.value(data,"action","operations").asCArray();
		for (Entry<Object, Map> e : operationss.entrySet()) {
			Map opera = e.getValue();
			int operationtype = Nest.value(opera, "operationtype").asInteger();
			if (operationtype == Defines.CONDITION_TYPE_TRIGGER_SEVERITY) {
				operations.push(opera);
			}
		}
		CArray<Integer> delay = null;
		CTable operationsTable = new CTable(_("No operations defined."), "formElementTable");
		operationsTable.attr("style", "min-width: 600px;");
		if (eventsource == EVENT_SOURCE_TRIGGERS || eventsource == EVENT_SOURCE_INTERNAL) {
			operationsTable.setHeader(array(_("Steps"), _("Details"), _("Start in"), _("Duration (sec)"), _("Action operations")));
			delay = count_operations_delay(operations, Nest.value(data,"action","esc_period").asInteger());
		} else {
			operationsTable.setHeader(array(_("Details"), _("Action operations")));
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
						new CSubmit("edit_operationid["+operationid+"]", _("Edit"), null, "link_menu icon edit"),
						SPACE, SPACE, SPACE,
						array(
							new CButton("remove", _("Remove"), "javascript: removeOperation("+operationid+");", "link_menu icon remove"),
							new CVar("operations["+operationid+"]", operation)
						)
					)
				);
			} else {
				operationRow = array(
					details,
					array(
						new CSubmit("edit_operationid["+operationid+"]", _("Edit"), null, "link_menu icon edit"),
						SPACE, SPACE, SPACE,
						array(
							new CButton("remove", _("Remove"), "javascript: removeOperation("+operationid+");", "link_menu icon remove"),
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
		
		Object footer = array();
		if (empty(data.get("new_operation"))) {
			//footer.add(new CSubmit("new_operation", _("New"), null, "link_menu"));
		}
		
		operationFormList.addRow(_("Action operations"), new CDiv(array(operationsTable, footer), "objectgroup inlineblock border_dotted ui-corner-all"));
		
		// create new operation table
//		if(!empty(data.get("new_operation"))){
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
			} else {// if only one operation is available - show only the label
				int op = allowedOperations[0];
				newOperationsTable.addRow(array(
					_("Operation type"),
					array(operation_type2str(op), new CVar("new_operation[operationtype]", op))
				), "indent_both");
			}
			Nest.value(data, "new_operation","operationtype").$(4);
			Map new_operation = (Map)data.get("new_operation");
			int operationtype = Nest.value(new_operation, "operationtype").asInteger();
			switch (operationtype) {
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
				groupList.addRow(new CRow(new CCol(new CButton("add", _("Add"), "return addDiscoveryHostGroup();", "link_menu add"),null,3),"cmd_row"));

				// load host groups
				CArray groupIds = isset(Nest.value(data,"new_operation","opgroup").$())
					? rda_objectValues(Nest.value(data,"new_operation","opgroup").$(), "groupid")
					: array();

				if (!empty(groupIds)) {
					CHostGroupGet hgget = new CHostGroupGet();
					hgget.setGroupIds(groupIds.valuesAsLong());
					hgget.setOutput(new String[]{"groupid", "name"});
					CArray<Map> hostGroups = API.HostGroup(idBean, executor).get(hgget);
					order_result(hostGroups, "name");

					String jsInsert = "";
					jsInsert += "addPopupValues("+rda_jsvalue(map("object", "dsc_groupid", "values", hostGroups))+");";
					rda_add_post_js(jsInsert);
				}

				String caption = (OPERATION_TYPE_GROUP_ADD == Nest.value(data,"new_operation","operationtype").asInteger())
					? _("Add to host groups")
					: _("Remove from host groups");

				newOperationsTable.addRow(array(caption, new CDiv(groupList, "objectgroup inlineblock border_dotted ui-corner-all")));
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
				operationConditionsTable.setHeader(array(_("Label"), _("Name"), _("Action operations")));

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
								new CButton("remove", _("Remove"), "javascript: removeOperationCondition("+i+");", "link_menu remove"),
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
			
			boolean isUpdate = "update".equals(Nest.value(data,"new_operation","action").$());
			footer = new CDiv(array(
				new CSubmit("add_operations", (isUpdate) ? _("Update") : _("Add"), null, "link_menu "+(isUpdate? "update": "add")),
				SPACE+SPACE,
				new CSubmit("cancel_new_operation", _("Cancel"), null, "link_menu cancel")
			), "cmd_div");
			CButton next2=new CButton();
			next2.setAttribute("onclick", "javascript:selectQQTwo();");
			next2.attr("value", _("upStep"));
			operationFormList.addRow(_("Operation details"), new CDiv(array(newOperationsTable, footer), "objectgroup inlineblock border_dotted ui-corner-all"));
			operationFormList.addRow(makeFormFooter(
				null,	
				array(
					next2,
					new CSubmit("save",  _("Save")),
					createCloneBtn(data),
					new CButtonCancel()
				)
			), null, false, null, "btns_li");
//		}
	 
		// append tabs to form
		CTabView actionTabs = discoveryTabs;
		int selectTabIndex = 0;
		if (isset(_REQUEST, "add_condition") || isset(_REQUEST, "evaltype") || Nest.value(data, "new_condition_conditiontype").asBoolean()) {
			selectTabIndex = 1;
			//因多个Tab页都存在evaltype属性 故在此判断其他页面跳转标识
			if(isset(_REQUEST, "add_operations") || isset(_REQUEST, "edit_operationid") ||(isset(_REQUEST, "evaltype") && isset(_REQUEST, "cancel_new_operation"))){
				selectTabIndex = 2;
			}

		}else if(isset(_REQUEST, "add_operations") || isset(_REQUEST, "cancel_new_operation") || isset(_REQUEST, "edit_operationid")) {
			selectTabIndex = 2;
		} 
		//clone
		if(isset(_REQUEST, "clone")){
			selectTabIndex = Nest.value(_REQUEST, "cabIndexFlag").asInteger();
		}
		if(isset(_REQUEST, "saveFlag")){
			selectTabIndex = 0;
		}
		CInput cabIndexFlag = new CInput("hidden", "cabIndexFlag",String.valueOf(selectTabIndex));
		
		actionTabs.setSelected(String.valueOf(selectTabIndex));
		actionTabs.addTab("conditionTab", _("Conditions"), conditionFormList);
		actionTabs.addTab("operationTab", _("Operations"), operationFormList);
		actionTabs.addItem(cabIndexFlag);
		
	}

}
