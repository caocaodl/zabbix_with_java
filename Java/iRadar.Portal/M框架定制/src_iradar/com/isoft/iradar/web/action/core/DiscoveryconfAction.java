package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.ActionsUtil.get_operations_by_eventsource;
import static com.isoft.iradar.inc.ActionsUtil.operation_type2str;
import static com.isoft.iradar.inc.ActionsUtil.sortOperations;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_MSG_AUTOREG;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_MSG_DISCOVERY;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_MSG_TRIGGER;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_SUBJ_AUTOREG;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_SUBJ_DISCOVERY;
import static com.isoft.iradar.inc.Defines.ACTION_DEFAULT_SUBJ_TRIGGER;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ACTION;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_LIKE;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_NOT_IN;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_VALUE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_DISABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ENABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_REMOVE;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_WEEK;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.get_discovery_rule_by_druleid;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ValidateUtil.validate_port_list;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.isoft.Feature;
import com.isoft.biz.daoimpl.common.GroupTemplateDAO;
import com.isoft.biz.daoimpl.radar.CActionDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.inc.ProfilesUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class DiscoveryconfAction extends RadarBaseAction {
	private final static int ACTION_EVENT_SOURCE = EVENT_SOURCE_DISCOVERY;
	
	CArray<Map> dbDRule;
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of discovery rules"));
		page("file", "discoveryconf.action");
		page("scripts", new String[] { "multiselect.js" });
		page("hist_arg", new String[] {});
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变发现规则状态所需JS
		page("style", "ul.formlist li.btns_li .dd{margin-left: -10em;}");
		page("css", new String[] { "lessor/strategy/discoveryconf.css" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//设置请求参数中的数据源
		Nest.value(_REQUEST, "eventsource").$(ACTION_EVENT_SOURCE);
		if(isset(_REQUEST, "save")){
			Nest.value(_REQUEST, "saveFlag").$("1");
		}
		
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"druleid" ,					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name" ,					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})","名称"),
			"proxy_hostid" ,			array(T_RDA_INT, O_OPT, null,	DB_ID,		Feature.showProxy?"isset({save})":null),
			"iprange" ,					array(Defines.T_RDA_IP_RANGE, O_OPT, null,	null,		"isset({save})",_("IP range")),
			"delay" ,					array(T_RDA_INT, O_OPT, null,	BETWEEN(1, SEC_PER_WEEK), "isset({save})",_("Delay")),
			"status" ,					array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"uniqueness_criteria" , 	array(T_RDA_STR, O_OPT, null, null,	"isset({save})", _("Device uniqueness criteria")),
			"g_druleid" ,				array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"dchecks" ,					array(null, 	 O_OPT, null,		null,		null),
			"ports" ,					array(T_RDA_INT, O_OPT, null,		null,		null),
			// actions
			"go" ,						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete" ,					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form" ,					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh" ,			array(T_RDA_STR, O_OPT, null,	null,		null),
			"output" ,					array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"ajaxaction" ,				array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"ajaxdata" ,				array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			//发现动作
			"evaltype",					array(T_RDA_INT, O_OPT, null, IN(array(ACTION_EVAL_TYPE_AND_OR, ACTION_EVAL_TYPE_AND, ACTION_EVAL_TYPE_OR)), "isset({save})"),
			"esc_period",				array(T_RDA_INT, O_OPT, null,	BETWEEN(60, 999999), null, _("Default operation step duration")),
			"status",					array(T_RDA_INT, O_OPT, null,	IN(array(ACTION_STATUS_ENABLED, ACTION_STATUS_DISABLED)), null),
//			"def_shortdata",			array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
//			"def_longdata",				array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"recovery_msg",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"r_shortdata",				array(T_RDA_STR, O_OPT, null,	null,		"isset({recovery_msg})&&isset({save})", _("Recovery subject")),
			"r_longdata",				array(T_RDA_STR, O_OPT, null,	null,		"isset({recovery_msg})&&isset({save})", _("Recovery message")),
			"g_actionid",				array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"conditions",				array(null,		 O_OPT,	null,	null,		null),
			"new_condition",			array(null,		 O_OPT,	null,	null,		"isset({add_condition})"),
			"operations",				array(null,		 O_OPT,	null,	null,		"isset({save})","发现中的操作"),
			"edit_operationid",			array(null,		 O_OPT,	P_ACT,	NOT_EMPTY,		null),
			"new_operation",			array(null,		 O_OPT,	null,	null,		"isset({add_operations})", _("new_operation")),
			"opconditions",				array(null,		 O_OPT,	null,	null,		null),
			"new_opcondition",			array(null,		 O_OPT,	null,	null,		"isset({add_opcondition})"),
			// actions
			"add_condition",			array(T_RDA_STR, O_OPT, null, null,	null),
			"evaltype",					array(T_RDA_STR, O_OPT, null, null,	null),
			"cancel_new_condition", 	array(T_RDA_STR, O_OPT, null, null, null),
			"add_operations",			array(T_RDA_STR, O_OPT, null, null,	null),
			"cancel_new_operation", 	array(T_RDA_STR, O_OPT, null, null, null),
			"add_opcondition",			array(T_RDA_STR, O_OPT, null, null,	null),
			"cancel_new_opcondition", 	array(T_RDA_STR, O_OPT, null, null, null),
			"cabIndexFlag", 			array(T_RDA_STR, O_OPT, null, null, null)
		);
		check_fields(getIdentityBean(), fields);		
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);

		Nest.value(_REQUEST,"status").$(isset(_REQUEST,"status") ? DRULE_STATUS_ACTIVE : DRULE_STATUS_DISABLED);
		Nest.value(_REQUEST,"dchecks").$(get_request("dchecks", array()));
	}

	@Override
	protected void doPermissions(final SQLExecutor executor) {		
		/** Permissions */
		if (isset(_REQUEST,"druleid")) {
			final CDRuleGet option = new CDRuleGet();
			option.setDruleIds(get_request_asLong("druleid"));
			option.setOutput(new String[]{"name", "proxy_hostid", "iprange", "delay", "status"});
			option.setSelectDChecks(new String[]{"type", "key_", "snmp_community", "ports", "snmpv3_securityname", "snmpv3_securitylevel",
					"snmpv3_authpassphrase", "snmpv3_privpassphrase", "uniq", "snmpv3_authprotocol", "snmpv3_privprotocol",
					"snmpv3_contextname"});
			option.setEditable(true);
			dbDRule = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.DRule(getIdentityBean(), executor).get(option);
				}
			}, array());
			if (empty(dbDRule)) {
				access_deny();
			}
		}
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		// ajax
		if (isset(_REQUEST,"output") && "ajax".equals(Nest.value(_REQUEST,"output").asString())) {
			AjaxResponse ajaxResponse = new AjaxResponse();
			if (isset(_REQUEST,"ajaxaction") && "validate".equals(Nest.value(_REQUEST,"ajaxaction").asString())) {
				CArray<Map> ajaxData = get_request("ajaxdata", array());
				for(Map check : ajaxData) {
					String field = Nest.value(check,"field").asString();
					if("port".equals(field)){
						if (!validate_port_list(Nest.value(check,"value").asString())) {
							ajaxResponse.error(_("Incorrect port range."));
						}
					} else if("itemKey".equals(field)){
						CItemKey itemKey = new CItemKey(Nest.value(check,"value").asString());
						if (!itemKey.isValid()) {
							ajaxResponse.error(_s("Invalid key \"%1$s\": %2$s.", Nest.value(check,"value").$(), itemKey.getError()));
						}
					}
				}
			}
			ajaxResponse.send();
			return true;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/** Action */
		if (isset(_REQUEST,"save")) {
			doSaveAction(executor);
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"druleid")) {
			doFromDeleteAction(executor);
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_druleid")) {	
			doListActivateAction(executor);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_druleid")) {
			doListDeleteAction(executor);
		}
		
		
		//发现动作Action操作
		/* Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"druleid")) {
			unset(_REQUEST,"druleid");
			unset(_REQUEST,"actionid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"cancel_new_operation")) {
			unset(_REQUEST,"new_operation");
		} else if (isset(_REQUEST,"cancel_new_opcondition")) {
			unset(_REQUEST,"new_opcondition");
		} else if (isset(_REQUEST,"add_condition") && isset(_REQUEST,"new_condition")) {
			try {
				Map newCondition = get_request("new_condition", new CArray());

				if (!empty(newCondition)) {
					CArray<Map> conditions = get_request("conditions", array());
					// when adding new maintenance, in order to check for an existing maintenance, it must have a not null value
					if (Nest.value(newCondition,"conditiontype").asInteger() == CONDITION_TYPE_MAINTENANCE) {
						Nest.value(newCondition,"value").$("");
					}

					// check existing conditions and remove duplicate condition values
					for(Map condition : conditions) {
						if (Nest.value(newCondition,"conditiontype").asInteger() == Nest.value(condition,"conditiontype").asInteger()) {
							if (isArray(Nest.value(newCondition,"value").$())) {
								for (Entry<Object,Object> e : ((CArray<Object>)Nest.value(newCondition,"value").asCArray()).entrySet()) {
								    Object key = e.getKey();
								    Object newValue = e.getValue();
									if (Nest.value(condition,"value").$().equals(newValue)) {
										unset(Nest.value(newCondition,"value").asCArray(),key);
									}
								}
							} else {
								if (Nest.value(newCondition,"value").$().equals(Nest.value(condition,"value").$())) {
									Nest.value(newCondition,"value").$(null);
									break;
								}
							}
						}
					}

					CArray<Map> validateConditions = Clone.deepcopy(conditions);

					if (isset(newCondition,"value")) {
						CArray newConditionValues = rda_toArray(Nest.value(newCondition,"value").$());
						for(Object newValue : newConditionValues) {
							Map condition = Clone.deepcopy(newCondition);
							Nest.value(condition,"value").$(newValue);
							validateConditions.add(condition);
						}
					}

					if (!empty(validateConditions)) {
						CActionDAO.validateConditions(getIdentityBean(), executor, validateConditions);
					}
					
			//		validateConditions.push(con);
					Nest.value(_REQUEST,"conditions").$(validateConditions);
					
				}
			} catch (APIException e) {
				error(e.getMessage());
				show_error_message(_("Cannot add action condition"));
			}
		} else if (isset(_REQUEST,"add_opcondition") && isset(_REQUEST,"new_opcondition")) {
			Map new_opcondition = Nest.value(_REQUEST,"new_opcondition").asCArray();

			try {
				CActionDAO.validateOperationConditions(array(new_opcondition));
				Map new_operation = get_request("new_operation", array());

				if (!isset(new_operation,"opconditions")) {
					Nest.value(new_operation,"opconditions").$(array());
				}
				if (!str_in_array(new_opcondition, Nest.value(new_operation,"opconditions").asCArray())) {
					array_push(Nest.value(new_operation,"opconditions").asCArray(), new_opcondition);
				}

				Nest.value(_REQUEST,"new_operation").$(new_operation);

				unset(_REQUEST,"new_opcondition");
			} catch (APIException e) {
				error(e.getMessage());
			}
		} else if (isset(_REQUEST,"add_operations") && isset(_REQUEST,"new_operation")) {
			final Map new_operation = Nest.value(_REQUEST,"new_operation").asCArray();
			new_operation.put("operationtype", Defines.OPERATION_TYPE_GROUP_ADD);
			boolean result = true;
			boolean isRightOperation = API.Call(new Wrapper<Boolean>() {
				@Override protected Boolean doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).validateOperations(array(new_operation));
				}
			});
			if (isRightOperation) {
				Nest.value(_REQUEST,"operations").$(get_request("operations", array()));

				CArray uniqOperations = map(
					OPERATION_TYPE_HOST_ADD, 0,
					OPERATION_TYPE_HOST_REMOVE, 0,
					OPERATION_TYPE_HOST_ENABLE, 0,
					OPERATION_TYPE_HOST_DISABLE, 0
				);
				if (isset(uniqOperations,Nest.value(new_operation,"operationtype").asInteger())) {
					for(Map operation : (CArray<Map>)Nest.value(_REQUEST,"operations").asCArray()) {
						if (isset(uniqOperations,Nest.value(operation,"operationtype").asInteger())) {
							Nest.value(uniqOperations,Nest.value(operation,"operationtype").asInteger()).$(
									Nest.value(uniqOperations,Nest.value(operation,"operationtype").asInteger()).asInteger()+1
							);
						}
					}
					if (!empty(Nest.value(uniqOperations,Nest.value(new_operation,"operationtype").asInteger()).$())) {
						result = false;
						info(_s("Operation \"%s\" already exists.", operation_type2str(Nest.value(new_operation,"operationtype").asInteger())));
						show_messages();
					}
				}

				if (result) {
					if (isset(new_operation,"id")) {
						Nest.value(_REQUEST,"operations",new_operation.get("id")).$(new_operation);
					} else {
						Nest.value(_REQUEST, "operations").asCArray().add(new_operation);
						int eventsource = get_request("eventsource",
							Nest.as(CProfile.get(getIdentityBean(), executor, "web.actionconf.eventsource", EVENT_SOURCE_TRIGGERS)).asInteger()
						);
						sortOperations(eventsource, Nest.value(_REQUEST,"operations").asCArray());
					}
				}

				unset(_REQUEST,"new_operation");
			}else
				FuncsUtil.show_error_message(_("the action of discovery is failure"));
		} else if (isset(_REQUEST,"edit_operationid")) {
			Nest.value(_REQUEST,"edit_operationid").$(array_keys(Nest.value(_REQUEST,"edit_operationid").asCArray()));
			Object edit_operationid = array_pop(Nest.value(_REQUEST,"edit_operationid").asCArray());
			Nest.value(_REQUEST,"edit_operationid").$(edit_operationid);
			Nest.value(_REQUEST,"operations").$(get_request("operations", array()));

			if (isset(Nest.value(_REQUEST,"operations",edit_operationid).$())) {
				Nest.value(_REQUEST,"new_operation").$(Nest.value(_REQUEST,"operations",edit_operationid).$());
				Nest.value(_REQUEST,"new_operation","id").$(edit_operationid);
				Nest.value(_REQUEST,"new_operation","action").$("update");
			}
		}

		/* Display */
		if (isset(_REQUEST, "form")) {
			displayForm(executor);
		} else {
			displayList(executor);
		}
	}

	/**
	 * 列表页面启用禁用操作
	 * 
	 * @param executor
	 */
	private void doListActivateAction(SQLExecutor executor) {
		boolean result = true;
		boolean enable = ("activate".equals(get_request("go")));
		int status = enable ? DRULE_STATUS_ACTIVE : DRULE_STATUS_DISABLED;
		int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;
		int updated = 0;
		CArray actionIds = array();
		
		DBstart(executor);
		
		Map params = new HashMap();
	
		params.put("status", status);
		for(Object druleId : get_request("g_druleid", array())) {
			params.put("druleid", druleId);
			result &= DBexecute(executor,"UPDATE drules SET status=#{status} WHERE druleid=#{druleid}",params);

			if(result) {
				status = enable ? ACTION_STATUS_ENABLED : ACTION_STATUS_DISABLED;
				Long actionid = getActionIdByDiscoveryRuleId(executor, druleId);
				result &= DBexecute(executor,
					"UPDATE actions"+
					" SET status=#{status}"+
					" WHERE actionid=#{actionid}"
				,EasyMap.build(
					"status", status,
					"actionid", actionid
				));
				if (result) {
					actionIds.add(actionid);
				}
			}
			
			if (result) {
				Map druleData = get_discovery_rule_by_druleid(getIdentityBean(), executor,Nest.as(druleId).asString());
				add_audit(getIdentityBean(), executor,auditAction, AUDIT_RESOURCE_DISCOVERY_RULE, "["+druleId+"] "+Nest.value(druleData,"name").$());
				
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ACTION, " 操作： ["+implode(",", actionIds)+"] "+Nest.value(druleData,"name").$());
			}
			updated++;
		}

		String messageSuccess = enable
			? _n(_("dosuccess"), _("dosuccess"), updated)
			: _n(_("dosuccess"), _("dosuccess"), updated);
		String messageFailed = enable
			? _n(_("donot"), _("donot"), updated)
			: _n(_("donot"), _("donot"), updated);

		result = DBend(executor, result);
		show_messages(result, messageSuccess, messageFailed);
		clearCookies(result);
	}

	/**
	 * 列表页面的删除操作
	 * 
	 * @param executor
	 */
	private void doListDeleteAction(final SQLExecutor executor) {
		boolean result = DBstart(executor);
		final Long[] druleids = Nest.array(_REQUEST,"g_druleid").asLong();
		result = API.Call(new Wrapper<Boolean>() {
			@Override protected Boolean doCall() throws Throwable {
				return !empty(API.DRule(getIdentityBean(), executor).delete(druleids));
			}
		});
		if(result) {
			final Long[] actionids = getActionIdByDiscoveryRuleIds(executor, druleids);
			result = !empty(actionids);
			if(result) {
				result = API.Call(new Wrapper<Boolean>() {
					@Override protected Boolean doCall() throws Throwable {
						return !empty(API.Action(getIdentityBean(), executor).delete(actionids));
					}
				});
				
			}
		}
		result = DBend(executor);
		
		show_messages(result, _("Discovery rules deleted"), _("Cannot delete discovery rules"));
		clearCookies(result);
	}

	/**
	 * 编辑页面的删除操作
	 * 
	 * @param executor
	 */
	private void doFromDeleteAction(final SQLExecutor executor) {
		boolean result = DBstart(executor);
		result = API.Call(new Wrapper<Boolean>() {
			@Override protected Boolean doCall() throws Throwable {
				return !empty(API.DRule(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"druleid").asLong()));
			}
		});
		
		
		String msgOk = _("Discovery rule deleted");
		String msgFail =  _("Cannot delete discovery rule");
		
		if(result) {
			Object druleid = Nest.value(_REQUEST,"druleid").asLong();
			final Long actionId = getActionIdByDiscoveryRuleId(executor, druleid);
			result = !empty(actionId);
			if(result) {
				result = API.Call(new Wrapper<Boolean>() {
					@Override protected Boolean doCall() throws Throwable {
						return !empty(API.Action(getIdentityBean(), executor).delete(actionId));
					}
				});
				
			}
			msgFail =  _("Cannot delete action");
//				show_messages(result, _("Action deleted"), _("Cannot delete action"));
		}
		result = DBend(executor, result);
		 
		show_messages(result, msgOk, msgFail);
		if (result) {
			unset(_REQUEST,"form");
			unset(_REQUEST,"druleid");
			clearCookies(result);
		}
	}
	
	/**
	 * 保存操作
	 * 
	 * @param executor
	 */
	private void doSaveAction(final SQLExecutor executor) {
		//准备数据——发现规则
		CArray<Map> dChecks = get_request("dchecks", array());
		String uniq = get_request("uniqueness_criteria", "0");

		for (Entry<Object, Map> e : dChecks.entrySet()) {
		    Object dcnum = e.getKey();
		    //Map check = e.getValue();
			Nest.value(dChecks,dcnum,"uniq").$((uniq.equals(dcnum.toString())) ? 1 : 0);
		}

		final Map discoveryRule = map(
			"name", get_request("name"),
			"proxy_hostid", get_request("proxy_hostid"),
			"iprange", get_request("iprange"),
			"delay", get_request("delay"),
			"status", get_request("status"),
			"dchecks", dChecks
		);
		
		boolean result = false;
		String msgOk,msgFail;
		if (isset(_REQUEST,"druleid")) {
			msgOk = _("Discovery rule updated");
			msgFail = _("Cannot update discovery rule");
		} else {
			msgOk = _("Discovery rule created");
			msgFail = _("Cannot create discovery rule");
		}
		if(!Pattern.compile(IMonConsts.RDA_PREG_ZIFU).matcher(Nest.value(discoveryRule, "name").asString()).find()){
			//准备数据——发现动作
			final Map action = map(
					"actionid","",
			//		"name", "",	//动作的name用来存储对应规则的ID，方便关联
				"evaltype", get_request("evaltype", 0),
				"status", get_request("status", ACTION_STATUS_DISABLED),
				"esc_period", get_request("esc_period", 0),
				"def_shortdata", get_request("def_shortdata", ""),
				"def_longdata", get_request("def_longdata", ""),
				"recovery_msg", get_request("recovery_msg", 0),
				"r_shortdata", get_request("r_shortdata", ""),
				"r_longdata", get_request("r_longdata", ""),
				"conditions", get_request("conditions", array()),
				"operations", get_request("operations", array())
			);
	   
			for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(action,"operations").asCArray()).entrySet()) {
			    Object num = e.getKey();
			    Map operation = e.getValue();
				if (isset(operation,"opmessage") && !isset(Nest.value(operation,"opmessage","default_msg").$())) {
					Nest.value(action,"operations",num,"opmessage","default_msg").$(0);
				}
			}

			//执行数据库操作——开始
			DBstart(executor);
			
			CArray<Long[]> resultIds;
			//保存数据——发现规则
			if (isset(_REQUEST,"druleid")) {
				Nest.value(discoveryRule,"druleid").$(get_request("druleid"));
				resultIds = API.Call(new Wrapper<CArray>() {
					@Override protected CArray doCall() throws Throwable {
						return API.DRule(getIdentityBean(), executor).update(array(discoveryRule));
					}
				}, array());
			} else {
				resultIds = API.Call(new Wrapper<CArray>() {
					@Override protected CArray doCall() throws Throwable {
						return API.DRule(getIdentityBean(), executor).create(array(discoveryRule));
					}
				}, array());
			}
			
			
			
			//保存数据——发现动作
			result = !empty(resultIds);
			GroupTemplateDAO groupDefaultTemplateDao=new GroupTemplateDAO(executor);
			if(result) {
				Long discoveryRuleId = reset(resultIds)[0];
				if (isset(_REQUEST,"druleid")) {
					Long actionId = getActionIdByDiscoveryRuleId(executor, discoveryRuleId);
					result = !empty(actionId);
					if(result) {
						Nest.value(action,"actionid").$(actionId);
						
						//条件
						CArray<Map> conditions =Nest.value(action,"conditions").asCArray();
						conditions.push(map(
							"conditiontype", Defines.CONDITION_TYPE_DRULE,
							"operator", Defines.CONDITION_OPERATOR_EQUAL,
							"value", discoveryRuleId
						));
						Nest.value(action,"conditions").$(conditions);
						
						//获取原来 各操作的opid
						long  opid_tmpl_add=0L;
						long  opid_host_enable=0L;
						long  opid_host_add=0L;
						final CActionGet cadao = new CActionGet();
						cadao.setOutput(API_OUTPUT_EXTEND);
						cadao.setActionIds(actionId);
						cadao.setSelectOperations(new String[] { "operationid", "operationtype" });
						CArray<Map> operationids = API.Call(new Wrapper<CArray>() {
							@Override protected CArray doCall() throws Throwable {
								return API.Action(getIdentityBean(), executor).get(cadao);
							}
						}, array());
						CArray<Map> operationidsTwo = Nest.value(operationids, "0", "operations").asCArray();
						for (Entry<Object, Map> e : operationidsTwo.entrySet()) {
							Map ids = e.getValue();
							Long id = Nest.value(ids, "operationtype").asLong();
							if (id == Defines.OPERATION_TYPE_HOST_ADD) {
								opid_host_add = Nest.value(ids, "operationid").asLong();
							}
							if (id == Defines.OPERATION_TYPE_TEMPLATE_ADD) {
								opid_tmpl_add = Nest.value(ids, "operationid").asLong();
							}
						}
						
						
						CArray<Map> operations =Nest.value(action,"operations").asCArray();
						
						// 获取模板ID
						List gro= operations.listNested("opgroup", "groupid");
						List<Map> temids = groupDefaultTemplateDao.doList(gro);
						if(!empty(temids)){
							//添加模板
							CArray<Map> operation = map("operationtype", Defines.OPERATION_TYPE_TEMPLATE_ADD);
							if (opid_tmpl_add == 0) {
							} else {
								operation.put("operationid", opid_tmpl_add);
							}
							
							CArray<Map> optemplate = new CArray<Map>();
							CArray<Map> template = null;
							for (Map object : temids) {
								template = new CArray<Map>();
								template.put("templateid", object.get("templateid"));
								optemplate.put(object.get("templateid"), template);
							}
					    	operations.push(map(
				    			"operationtype", Defines.OPERATION_TYPE_TEMPLATE_ADD, 
					    		"optemplate", optemplate
				    		));
					    }
					    
					    //添加设备
					    operations.push(map(
					    	"operationtype", Defines.OPERATION_TYPE_HOST_ADD,
					    	"operationid", opid_host_add
					    ));
					    
					    Nest.value(action,"operations").$(operations);
						
						result = API.Call(new Wrapper<Boolean>() {
							@Override protected Boolean doCall() throws Throwable {
								return !empty(API.Action(getIdentityBean(), executor).update(array(action)));
							}
						});
					}
					msgFail = _("Cannot update action");
//					show_messages(result, _("Action updated"), _("Cannot update action"));
				}else {
					//条件
					CArray<Map> conditions = Nest.value(action,"conditions").asCArray();
					conditions.push(map(//条件中增加只能是本规则的BUG
						"conditiontype", Defines.CONDITION_TYPE_DRULE,
						"operator", Defines.CONDITION_OPERATOR_EQUAL,
						"value", discoveryRuleId
					));
					Nest.value(action,"conditions").$(conditions);
					
					//操作
					CArray<Map> operations = Nest.value(action,"operations").asCArray();
					List gro=operations.listNested("opgroup", "groupid");
					List<Map> tmplIds = groupDefaultTemplateDao.doList(gro);
					if(!empty(tmplIds)){
						CArray<Map> optemplate=new CArray<Map>();
						for (Map object : tmplIds) {
							Object templateid = object.get("templateid");
							optemplate.put(templateid, map("templateid", templateid));
						}
					    
					    operations.push(map(
				    		"operationtype", Defines.OPERATION_TYPE_TEMPLATE_ADD, 
				    		"optemplate", optemplate
				    	));
					}
				    operations.push(map("operationtype", Defines.OPERATION_TYPE_HOST_ADD));
				    Nest.value(action,"operations").$(operations);
				    
				    //其他
					Nest.value(action,"name").$(discoveryRuleId);//name关联规则ID
					Nest.value(action,"eventsource").$(ACTION_EVENT_SOURCE);//固定事件源为发现
					result = API.Call(new Wrapper<Boolean>() {
						@Override protected Boolean doCall() throws Throwable {
							return !empty(API.Action(getIdentityBean(), executor).create(array(action)));
						}
					});
//					show_messages(result, _("Action added"), _("Cannot add action"));
				}
			}
			//执行数据库操作——结束
			result = DBend(executor, result);
			//显示操作结果提示
			show_messages(result, msgOk, msgFail);
			
			if (!empty(resultIds)) {
				Long druleid = reset(Nest.array(resultIds,"druleids").asLong());
				add_audit(getIdentityBean(), executor, isset(discoveryRule,"druleid") ? AUDIT_ACTION_UPDATE : AUDIT_ACTION_ADD,
					AUDIT_RESOURCE_DISCOVERY_RULE,
					"["+druleid+"] "+Nest.value(discoveryRule,"name").asString()
				);
				unset(_REQUEST,"form");
				clearCookies(!empty(resultIds));
			}

			if (result) {
				add_audit(getIdentityBean(), executor,
					hasRequest("actionid") ? AUDIT_ACTION_UPDATE : AUDIT_ACTION_ADD,
					AUDIT_RESOURCE_ACTION,
					_("Name")+NAME_DELIMITER+Nest.value(action,"name").asString()
				);

				unset(_REQUEST,"form");
			}
		}else{
			info(_s("the name contains special char"));
			show_messages(false, msgOk, msgFail);
		}

		clearCookies(result);
	}

	/**
	 * 展现编辑页面
	 * 
	 * @param executor
	 */
	private void displayForm(final SQLExecutor executor) {
		Map data = map(
			"druleid" , get_request("druleid"),
			"drule" , array(),
			"form" , get_request("form"),
			"form_refresh" , get_request("form_refresh", 0)
		);

		// get drule
		if (isset(data,"druleid") && !isset(_REQUEST,"form_refresh")) {
			Nest.value(data,"drule").$(reset(dbDRule));
			Nest.value(data,"drule","uniqueness_criteria").$(-1);

			if (!empty(Nest.value(data,"drule","dchecks").$())) {
				CArray<Map> dchecks = Nest.value(data,"drule","dchecks").asCArray();
				for (Map dcheck  : dchecks) {
					if (!empty(Nest.value(dcheck,"uniq").$())) {
						Nest.value(data,"drule","uniqueness_criteria").$(Nest.value(dcheck,"dcheckid").$());
					}
				}
			}
		} else {
			Nest.value(data,"drule","proxy_hostid").$(get_request("proxy_hostid", 0));
			Nest.value(data,"drule","name").$(get_request("name", ""));
			Nest.value(data,"drule","iprange").$(get_request("iprange", "192.168.0.1-254"));
			Nest.value(data,"drule","delay").$(get_request("delay", SEC_PER_HOUR));
			Nest.value(data,"drule","status").$(get_request("status", DRULE_STATUS_ACTIVE));
			Nest.value(data,"drule","dchecks").$(get_request("dchecks", array()));
			Nest.value(data,"drule","nextcheck").$(get_request("nextcheck", 0));
			Nest.value(data,"drule","uniqueness_criteria").$(get_request("uniqueness_criteria", "-1"));
		} 

		if (!empty(Nest.value(data,"drule","dchecks").$())) {
			CArray<Map> dchecks = Nest.value(data,"drule","dchecks").asCArray();
			for (Entry<Object, Map> e : dchecks.entrySet()) {
		        Object id = e.getKey();
		        Map dcheck = e.getValue();
				Nest.value(data,"drule","dchecks",id,"name").$(discovery_check2str(
					Nest.value(dcheck,"type").asInteger(),
					isset(dcheck,"key_") ? Nest.value(dcheck,"key_").asString() : "",
					isset(dcheck,"ports") ? Nest.value(dcheck,"ports").asString() : ""
				));
			}
			order_result(dchecks, "name");
		}

		// get proxies
		if(Feature.showProxy) {
			final CProxyGet options = new CProxyGet();
			options.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> proxies = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.Proxy(getIdentityBean(), executor).get(options);
				}
			}, array());
			Nest.value(data,"proxies").$(proxies);
			order_result(proxies, "host");
		}
			
		//添加动作的展示数据
		CArray actionEditData = getActionEditData(executor);
		data = Cphp.array_merge(data, actionEditData);
		
		// render view
		CView discoveryView = new CView("configuration.discovery.edit", data);
		discoveryView.render(getIdentityBean(), executor);
		discoveryView.show();
	}
	
	/**
	 * 展现列表页面
	 * 
	 * @param executor
	 */
	private void displayList(final SQLExecutor executor) {
		Map data = array();
		Map<String, Object> config = ProfilesUtil.select_config(getIdentityBean(), executor);
		// get drules
		final CDRuleGet options = new CDRuleGet();
		options.setOutput(new String[]{"proxy_hostid", "name", "status", "iprange", "delay", "nextcheck"});//增加对nextcheck的获取
		options.setSelectDChecks(new String[]{"type"});
		options.setSelectDHosts(new String[] {"status", "dhostid"});//添加对发现设备的数据获取，当前只需要获取状态就可以了
		options.setEditable(true);
		options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
 		CArray<Map> drules = API.Call(new Wrapper<CArray>() {
			@Override protected CArray doCall() throws Throwable {
				return API.DRule(getIdentityBean(), executor).get(options);
			}
		}, array());
		Nest.value(data,"drules").$(drules);
		
		final CDServiceGet serviceGet = new CDServiceGet();
		serviceGet.setOutput(new String[]{"dhostid"});
		CArray<Map> dservices = API.Call(new Wrapper<CArray>() {
			@Override protected CArray doCall() throws Throwable {
				return API.DService(getIdentityBean(), executor).get(serviceGet);
			}
		}, array());
		Nest.value(data,"dservices").$(dservices);

		if (!empty(drules)) {
			for (Entry<Object, Map> e : drules.entrySet()) {
		        Object key = e.getKey();
		        Map drule = e.getValue();
		        
				// checks
		        CArray checks = array();	            
		        CArray<Map> dchecks = Nest.value(drule,"dchecks").asCArray();
				for (Map check : dchecks) {
					Nest.value(checks,check.get("type")).$(discovery_check_type2str(Nest.value(check,"type").asInteger()));
				}
				order_result(checks);
				Nest.value(drules,key,"checks").$(checks);

				// description
				Nest.value(drules,key,"description").$(array());
				if (!empty(Nest.value(drule,"proxy_hostid").$())) {
					Map proxy = get_host_by_hostid(getIdentityBean(), executor,Nest.value(drule,"proxy_hostid").asLong());
					array_push(Nest.value(drules,key,"description").asCArray(), Nest.value(proxy,"host").asString()+NAME_DELIMITER);
				}
			}

			order_result(drules, getPageSortField(getIdentityBean(), executor,"name"), getPageSortOrder(getIdentityBean(), executor));
		}

		// get paging
		Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,drules, array("druleid")));

		// render view
		CView discoveryView = new CView("configuration.discovery.list", data);
		discoveryView.render(getIdentityBean(), executor);
		discoveryView.show();
	}
	
	/**
	 * 获取发现动作编辑数据
	 * 
	 * @param executor
	 */
	private CArray getActionEditData(final SQLExecutor executor) {
		Object druleid = get_request("druleid");
		CArray data = map(
			"form", get_request("form"),
			"actionid", getActionIdByDiscoveryRuleId(executor, druleid),
			"new_condition", get_request("new_condition", array()),
			"new_operation", get_request("new_operation", null)
		);

		if (!empty(Nest.value(data,"actionid").$())) {
			final CActionGet aoptions = new CActionGet();
			aoptions.setActionIds(Nest.value(data,"actionid").asLong());
			aoptions.setSelectOperations(API_OUTPUT_EXTEND);
			aoptions.setSelectConditions(API_OUTPUT_EXTEND);
			aoptions.setOutput(API_OUTPUT_EXTEND);
			aoptions.setEditable(true);
			CArray<Map> dbactions = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).get(aoptions);
				}
			}, array());
			
			Nest.value(data,"action").$(reset(dbactions));
			Nest.value(data,"eventsource").$(Nest.value(data,"action","eventsource").$());
		} else {
			Nest.value(data,"eventsource").$(get_request("eventsource",
				CProfile.get(getIdentityBean(), executor, "web.actionconf.eventsource", EVENT_SOURCE_TRIGGERS)
			));
			Nest.value(data,"evaltype").$(get_request("evaltype"));
			Nest.value(data,"esc_period").$(get_request("esc_period"));
		}

		if (isset(Nest.value(data,"action","actionid").$()) && !hasRequest("form_refresh")) {
			sortOperations(Nest.value(data,"eventsource").asInteger(), Nest.value(data,"action","operations").asCArray());
		} else {
			Nest.value(data,"action","name").$(get_request("name"));
			Nest.value(data,"action","evaltype").$(get_request("evaltype", 0));
			Nest.value(data,"action","esc_period").$(get_request("esc_period", SEC_PER_HOUR));
			Nest.value(data,"action","status").$(get_request("status", hasRequest("form_refresh") ? 1 : 0));
			Nest.value(data,"action","recovery_msg").$(get_request("recovery_msg", 0));
			Nest.value(data,"action","conditions").$(get_request("conditions", array()));
			Nest.value(data,"action","operations").$(get_request("operations", array()));

			sortOperations(Nest.value(data,"eventsource").asInteger(), Nest.value(data,"action","operations").asCArray());

			if (!empty(Nest.value(data,"actionid").$()) && hasRequest("form_refresh")) {
				Nest.value(data,"action","def_shortdata").$(get_request("def_shortdata"));
				Nest.value(data,"action","def_longdata").$(get_request("def_longdata"));
			} else {
				if (Nest.value(data,"eventsource").asInteger() == EVENT_SOURCE_TRIGGERS) {
					Nest.value(data,"action","def_shortdata").$(get_request("def_shortdata", ACTION_DEFAULT_SUBJ_TRIGGER));
					Nest.value(data,"action","def_longdata").$(get_request("def_longdata", ACTION_DEFAULT_MSG_TRIGGER));
					Nest.value(data,"action","r_shortdata").$(get_request("r_shortdata", ACTION_DEFAULT_SUBJ_TRIGGER));
					Nest.value(data,"action","r_longdata").$(get_request("r_longdata", ACTION_DEFAULT_MSG_TRIGGER));
				} else if (Nest.value(data,"eventsource").asInteger() == EVENT_SOURCE_DISCOVERY) {
					Nest.value(data,"action","def_shortdata").$(get_request("def_shortdata", ACTION_DEFAULT_SUBJ_DISCOVERY));
					Nest.value(data,"action","def_longdata").$(get_request("def_longdata", ACTION_DEFAULT_MSG_DISCOVERY));
				} else if (Nest.value(data,"eventsource").asInteger() == EVENT_SOURCE_AUTO_REGISTRATION) {
					Nest.value(data,"action","def_shortdata").$(get_request("def_shortdata", ACTION_DEFAULT_SUBJ_AUTOREG));
					Nest.value(data,"action","def_longdata").$(get_request("def_longdata", ACTION_DEFAULT_MSG_AUTOREG));
				} else {
					Nest.value(data,"action","def_shortdata").$(get_request("def_shortdata"));
					Nest.value(data,"action","def_longdata").$(get_request("def_longdata"));
					Nest.value(data,"action","r_shortdata").$(get_request("r_shortdata"));
					Nest.value(data,"action","r_longdata").$(get_request("r_longdata"));
				}
			}
		}

		if (empty(Nest.value(data,"actionid").$()) && !hasRequest("form_refresh") && Nest.value(data,"eventsource").asInteger() == EVENT_SOURCE_TRIGGERS) {
			Nest.value(data,"action","conditions").$(array(
				map(
					"conditiontype", CONDITION_TYPE_TRIGGER_VALUE,
					"operator", CONDITION_OPERATOR_EQUAL,
					"value", TRIGGER_VALUE_TRUE
				),
				map(
					"conditiontype", CONDITION_TYPE_MAINTENANCE,
					"operator", CONDITION_OPERATOR_NOT_IN,
					"value", ""
				)
			));
		}

		Nest.value(data,"allowedConditions").$(_get_conditions_by_eventsource(Nest.value(data,"eventsource").asInteger()));
		Nest.value(data,"allowedOperations").$(get_operations_by_eventsource(Nest.value(data,"eventsource").asInteger()));

		// sort conditions
		CArray sortFields = array(
			map("field" , "conditiontype", "order" , RDA_SORT_DOWN),
			map("field" , "operator", "order" , RDA_SORT_DOWN),
			map("field" , "value", "order" , RDA_SORT_DOWN)
		);
		CArrayHelper.sort(Nest.value(data,"action","conditions").asCArray(), sortFields);

		// new condition
		Nest.value(data,"new_condition").$(map(
			"conditiontype", isset(Nest.value(data,"new_condition","conditiontype").$()) ? Nest.value(data,"new_condition","conditiontype").$() : CONDITION_TYPE_TRIGGER_NAME,
			"operator", isset(Nest.value(data,"new_condition","operator").$()) ? Nest.value(data,"new_condition","operator").$() : CONDITION_OPERATOR_LIKE,
			"value", isset(Nest.value(data,"new_condition","value").$()) ? Nest.value(data,"new_condition","value").$() : ""
		));

		if (!str_in_array(Nest.value(data,"new_condition","conditiontype").$(), Nest.value(data,"allowedConditions").asCArray())) {
			Nest.value(data,"new_condition","conditiontype").$(Nest.value(data,"allowedConditions",0).$());
		}

		// new operation
		if (!empty(Nest.value(data,"new_operation").$())) {
			if (!isArray(Nest.value(data,"new_operation").$())) {
				Nest.value(data,"new_operation").$(map(
					"action", "create",
					"operationtype", 0,
					"esc_period", 0,
					"esc_step_from", 1,
					"esc_step_to", 1,
					"evaltype", 0
				));
			}
		}
		return data;
	}
	
	private CArray<Integer> _get_conditions_by_eventsource(int eventSource){
//		return get_conditions_by_eventsource(eventSource);
		CArray result = CArray.array(
			Defines.CONDITION_TYPE_DVALUE, 	//接收到的值
			Defines.CONDITION_TYPE_DHOST_IP	//设备IP
		);
		
		if(Feature.showProxy) {
			result.push(Defines.CONDITION_TYPE_PROXY);
		}
		
		return result;
	}

	
	/**
	 * 通过发现规则ID，获取动作ID
	 * 
	 * @param executor
	 * @param discoveryRuleId
	 * @return 不存在时，将返回null
	 */
	private Long getActionIdByDiscoveryRuleId(final SQLExecutor executor, Object discoveryRuleId) {
		Long actionId = null;
		if(!empty(discoveryRuleId)) {
			//通过name中关联的规则ID，获取action的ID
			final CActionGet actionGetParams = new CActionGet();
			actionGetParams.setOutput(new String[] {"actionid"});
			actionGetParams.setLimit(1);
			actionGetParams.setFilter("name", discoveryRuleId);
			CArray<Map> actions = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).get(actionGetParams);
				}
			}, array());
			if(!empty(actions)) {
				actionId = Nest.value(reset(actions), "actionid").asLong();
			}
		}
		return actionId;
	}
	
	/**
	 * 通过发现规则ID，获取动作ID
	 * 
	 * @param executor
	 * @param discoveryRuleId
	 * @return 不存在时，将返回null
	 */
	private Long[] getActionIdByDiscoveryRuleIds(final SQLExecutor executor, Long[] discoveryRuleIds) {
		Long[] actionIds = null;
		if(!empty(discoveryRuleIds)) {
			//通过name中关联的规则ID，获取action的ID
			final CActionGet actionGetParams = new CActionGet();
			actionGetParams.setOutput(new String[] {"actionid"});
			actionGetParams.setFilter("name", discoveryRuleIds);
			CArray<Map> actions = API.Call(new Wrapper<CArray>() {
				@Override protected CArray doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).get(actionGetParams);
				}
			}, array());
			if(!empty(actions)) {
				CArray<Long> ids = array(); 
				for(Map action: actions) {
					ids.add(Nest.value(action, "actionid").asLong());
				}
				actionIds = ids.valuesAsLong();
			}
		}
		return actionIds;
	}
}
