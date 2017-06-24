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
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.ActionsUtil.get_conditions_by_eventsource;
import static com.isoft.iradar.inc.ActionsUtil.get_operations_by_eventsource;
import static com.isoft.iradar.inc.ActionsUtil.operation_type2str;
import static com.isoft.iradar.inc.ActionsUtil.sortOperations;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBselect;
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
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ACTION;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_EQUAL;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_LIKE;
import static com.isoft.iradar.inc.Defines.CONDITION_OPERATOR_NOT_IN;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_MAINTENANCE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_NAME;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER_VALUE;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ADD;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_DISABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_ENABLE;
import static com.isoft.iradar.inc.Defines.OPERATION_TYPE_HOST_REMOVE;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CActionDAO;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ActionconfAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of actions"));
		page("file", "actionconf.action");
		page("scripts", new String[] { "multiselect.js" });
		page("hist_arg", new String[] {});
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"actionid",							array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name",								array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Name")),
			"eventsource",						array(T_RDA_INT, O_OPT, null, IN(array(EVENT_SOURCE_TRIGGERS, EVENT_SOURCE_DISCOVERY, EVENT_SOURCE_AUTO_REGISTRATION, EVENT_SOURCE_INTERNAL)), null),
			"evaltype",							array(T_RDA_INT, O_OPT, null, IN(array(ACTION_EVAL_TYPE_AND_OR, ACTION_EVAL_TYPE_AND, ACTION_EVAL_TYPE_OR)), "isset({save})"),
			"esc_period",						array(T_RDA_INT, O_OPT, null,	BETWEEN(60, 999999), null, _("Default operation step duration")),
			"status",								array(T_RDA_INT, O_OPT, null,	IN(array(ACTION_STATUS_ENABLED, ACTION_STATUS_DISABLED)), null),
			"def_shortdata",					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"def_longdata",						array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"recovery_msg",					array(T_RDA_INT, O_OPT, null,	null,		null),
			"r_shortdata",						array(T_RDA_STR, O_OPT, null,	null,		"isset({recovery_msg})&&isset({save})", _("Recovery subject")),
			"r_longdata",						array(T_RDA_STR, O_OPT, null,	null,		"isset({recovery_msg})&&isset({save})", _("Recovery message")),
			"g_actionid",							array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"conditions",							array(null,		O_OPT,	null,	null,		null),
			"new_condition",					array(null,		O_OPT,	null,	null,		"isset({add_condition})"),
			"operations",						array(null,		O_OPT,	null,	null,		"isset({save})", _("Operations")),
			"edit_operationid",				array(null,		O_OPT,	P_ACT,	NOT_EMPTY,		null),
			"new_operation",					array(null,		O_OPT,	null,	null,		"isset({add_operation})"),
			"opconditions",						array(null,		O_OPT,	null,	null,		null),
			"new_opcondition",				array(null,		O_OPT,	null,	null,		"isset({add_opcondition})"),
			// actions
			"go",										array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_condition",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_condition", 		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"add_operation",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_operation", 		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"add_opcondition",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel_new_opcondition", 	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null, null),
			"save",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",								array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",									array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",						array(T_RDA_INT, O_OPT, null,	null,		null),
			// ajax
			"favobj",								array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",								array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})"),
			"favstate",							array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}")
		);

		boolean dataValid = check_fields(getIdentityBean(), fields);

		if (dataValid && hasRequest("eventsource") && !hasRequest("form")) {
			CProfile.update(getIdentityBean(), executor, "web.actionconf.eventsource", get_request("eventsource"), PROFILE_TYPE_INT);
		}

		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (isset(_REQUEST,"actionid")) {
			CActionGet aoptions = new CActionGet();
			aoptions.setActionIds(Nest.value(_REQUEST,"actionid").asLong());
			aoptions.setEditable(true);
			CArray<Map> actionPermissions = API.Action(getIdentityBean(), executor).get(aoptions);
			if (empty(actionPermissions)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.audit.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return false;
		}
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"actionid")) {
			unset(_REQUEST,"actionid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"cancel_new_operation")) {
			unset(_REQUEST,"new_operation");
		} else if (isset(_REQUEST,"cancel_new_opcondition")) {
			unset(_REQUEST,"new_opcondition");
		} else if (hasRequest("save")) {
			final Map action = map(
				"name", get_request("name"),
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

			DBstart(executor);
			boolean result;
			if (hasRequest("actionid")) {
				Nest.value(action,"actionid").$(get_request("actionid"));
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Action(getIdentityBean(), executor).update(array(action)));
					}
				});
				show_messages(result, _("Action updated"), _("Cannot update action"));
			} else {
				Nest.value(action,"eventsource").$(get_request("eventsource", CProfile.get(getIdentityBean(), executor, "web.actionconf.eventsource", EVENT_SOURCE_TRIGGERS)));
				result = !empty(Call(new Wrapper<CArray<Long[]>>() {
					@Override
					protected CArray<Long[]> doCall() throws Throwable {
						return API.Action(getIdentityBean(), executor).create(array(action));
					}
				}, null));
				show_messages(result, _("Action added"), _("Cannot add action"));
			}
			
			result = DBend(executor, result);
			if (result) {
				add_audit(getIdentityBean(), executor,
					hasRequest("actionid") ? AUDIT_ACTION_UPDATE : AUDIT_ACTION_ADD,
					AUDIT_RESOURCE_ACTION,
					_("Name")+NAME_DELIMITER+Nest.value(action,"name").asString()
				);

				unset(_REQUEST,"form");
			}

			clearCookies(result);
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"actionid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Action(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"actionid").asLong()));
				}
			});
			
			show_messages(result, _("Action deleted"), _("Cannot delete action"));

			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"actionid");
				clearCookies(result);
			}
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
								if (Cphp.equals(Nest.value(newCondition,"value").$(), Nest.value(condition,"value").$())) {
									Nest.value(newCondition,"value").$(null);
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

					Nest.value(_REQUEST,"conditions").$(validateConditions);
				}
			} catch (APIException e) {
				show_error_message(_("Cannot add action condition"));
				error(e.getMessage());
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
		} else if (isset(_REQUEST,"add_operation") && isset(_REQUEST,"new_operation")) {
			final Map new_operation = Nest.value(_REQUEST,"new_operation").asCArray();
			boolean result = true;
			if (Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return API.Action(getIdentityBean(), executor).validateOperations(array(new_operation));
				}
			})) {
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
			}
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
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_actionid")) {
			boolean result = true;
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? ACTION_STATUS_ENABLED : ACTION_STATUS_DISABLED;
			String statusName = enable ? "enabled" : "disabled";
			CArray actionIds = array();
			int updated = 0;

			DBstart(executor);
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbActions = DBselect(executor,
				"SELECT a.actionid"+
				" FROM actions a"+
				" WHERE "+sqlParts.dual.dbConditionInt("a.actionid", Nest.array(_REQUEST,"g_actionid").asLong()),
				sqlParts.getNamedParams()
			);
			for(Map row : dbActions) {
				result &= DBexecute(executor,
					"UPDATE actions"+
					" SET status="+status+
					" WHERE actionid="+Nest.value(row,"actionid").asLong()
				);
				if (result) {
					actionIds.add(Nest.value(row,"actionid").$());
				}
				updated++;
			}
			result = DBend(executor, result);
			
			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ACTION, " Actions ["+implode(",", actionIds)+"] "+statusName);
			}

			String messageSuccess = enable
				? _n("Action enabled", "Actions enabled", updated)
				: _n("Action disabled", "Actions disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable action", "Cannot enable actions", updated)
				: _n("Cannot disable action", "Cannot disable actions", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result);
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_actionid")) {
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Action(getIdentityBean(), executor).delete(Nest.array(_REQUEST,"g_actionid").asLong()));
				}
			});
			show_messages(goResult, _("Selected actions deleted"), _("Cannot delete selected actions"));
			clearCookies(goResult);
		}

		/* Display */
		show_messages();

		if (hasRequest("form")) {
			CArray data = map(
				"form", get_request("form"),
				"actionid", get_request("actionid"),
				"new_condition", get_request("new_condition", array()),
				"new_operation", get_request("new_operation", null)
			);

			if (!empty(Nest.value(data,"actionid").$())) {
				CActionGet aoptions = new CActionGet();
				aoptions.setActionIds(Nest.value(data,"actionid").asLong());
				aoptions.setSelectOperations(API_OUTPUT_EXTEND);
				aoptions.setSelectConditions(API_OUTPUT_EXTEND);
				aoptions.setOutput(API_OUTPUT_EXTEND);
				aoptions.setEditable(true);
				CArray<Map> dbactions = API.Action(getIdentityBean(), executor).get(aoptions);
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

			Nest.value(data,"allowedConditions").$(get_conditions_by_eventsource(Nest.value(data,"eventsource").asInteger()));
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

			// render view
			CView actionView = new CView("configuration.action.edit", data);
			actionView.render(getIdentityBean(), executor);
			actionView.show();
		} else {
			CArray data = map(
				"eventsource", get_request("eventsource", CProfile.get(getIdentityBean(), executor, "web.actionconf.eventsource", EVENT_SOURCE_TRIGGERS))
			);

			String sortfield = getPageSortField(getIdentityBean(), executor, "name");
			Map<String, Object> config = select_config(getIdentityBean(), executor);

			CActionGet aoptions = new CActionGet();
			aoptions.setOutput(API_OUTPUT_EXTEND);
			aoptions.setFilter("eventsource", Nest.value(data,"eventsource").asString());
			aoptions.setSelectConditions(API_OUTPUT_EXTEND);
			aoptions.setSelectOperations(API_OUTPUT_EXTEND);
			aoptions.setEditable(true);
			aoptions.setSortfield(sortfield);
			aoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> dbactions = API.Action(getIdentityBean(), executor).get(aoptions);
			Nest.value(data,"actions").$(dbactions);

			// sorting && paging
			order_result(dbactions, sortfield, getPageSortOrder(getIdentityBean(), executor));
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, Nest.value(data,"actions").asCArray(), array("actionid")));

			// render view
			CView actionView = new CView("configuration.action.list", data);
			actionView.render(getIdentityBean(), executor);
			actionView.show();
		}
	}	
	
}
