package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FormsUtil.getTriggerFormData;
import static com.isoft.iradar.inc.FormsUtil.getTriggerMassupdateFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_requests;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.getParentHostsByTriggers;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CTriggerPrototypeGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TriggerPrototypesAction extends RadarBaseAction {
	
	private Map discovery_rule;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of trigger prototypes"));
		page("file", "trigger_prototypes.action");
		page("hist_arg", new String[] {"hostid", "parent_discoveryid"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"parent_discoveryid", 		array(T_RDA_INT, O_MAND, P_SYS,	DB_ID,		null),
			"hostid",							array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggerid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"(isset({form})&&({form}==\"update\"))"),
			"copy_type",						array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	"isset({copy})"),
			"copy_mode",					array(T_RDA_INT, O_OPT, P_SYS,	IN("0"),	null),
			"type",								array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"description",					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Name")),
			"expression",					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Expression")),
			"priority",							array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3,4,5"), "isset({save})"),
			"comments",					array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"url",									array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"status",							array(T_RDA_STR, O_OPT, null,	null,		null),
			"input_method",				array(T_RDA_INT, O_OPT, null,	NOT_EMPTY,	"isset({toggle_input_method})"),
			"expr_temp",					array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"(isset({add_expression})||isset({and_expression})||isset({or_expression})||isset({replace_expression}))"),
			"expr_target_single",			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"(isset({and_expression})||isset({or_expression})||isset({replace_expression}))"),
			"dependencies",				array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"new_dependence",			array(T_RDA_INT, O_OPT, null,	DB_ID+"{}>0", "isset({add_dependence})"),
			"rem_dependence",			array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"g_triggerid",					array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"copy_targetid",				array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"filter_groupid",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({copy})&&(isset({copy_type})&&({copy_type}==0))"),
			"showdisabled",				array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			// actions
			"massupdate",					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"visible",							array(T_RDA_STR, O_OPT, null,	null,		null),
			"go",									array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"toggle_input_method",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_expression", 			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"and_expression",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"or_expression",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"replace_expression",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"remove_expression",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"test_expression",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_dependence",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"del_dependence",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_enable",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_disable",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_delete",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"copy",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"mass_save",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",							array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",								array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",					array(T_RDA_INT, O_OPT, null,	null,		null)
		);
		Nest.value(_REQUEST,"showdisabled").$(get_request("showdisabled", CProfile.get(getIdentityBean(), executor,"web.triggers.showdisabled", 1)));

		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "description", RDA_SORT_UP);

		Nest.value(_REQUEST,"status").$(isset(_REQUEST,"status") ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED);
		Nest.value(_REQUEST,"type").$(isset(_REQUEST,"type") ? TRIGGER_MULT_EVENT_ENABLED : TRIGGER_MULT_EVENT_DISABLED);
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// validate permissions
		if (!empty(get_request("parent_discoveryid"))) {
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setItemIds(Nest.value(_REQUEST,"parent_discoveryid").asLong());
			droptions.setOutput(API_OUTPUT_EXTEND);
			droptions.setEditable(true);
			droptions.setPreserveKeys(true);
			CArray<Map> discovery_rules = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
			discovery_rule = reset(discovery_rules);
			if (empty(discovery_rule)) {
				access_deny();
			}

			if (isset(_REQUEST,"triggerid")) {
				CTriggerPrototypeGet troptions = new CTriggerPrototypeGet();
				troptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
				troptions.setOutput(new String[]{"triggerid"});
				troptions.setEditable(true);
				troptions.setPreserveKeys(true);
				CArray<Map> triggerPrototype = API.TriggerPrototype(getIdentityBean(), executor).get(troptions);
				if (empty(triggerPrototype)) {
					access_deny();
				}
			}
		} else {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		int showdisabled = get_request("showdisabled", 0);
		CProfile.update(getIdentityBean(), executor, "web.triggers.showdisabled", showdisabled, PROFILE_TYPE_INT);
		
		/* Actions */
		if (isset(_REQUEST,"add_expression")) {
			Nest.value(_REQUEST,"expression").$(Nest.value(_REQUEST,"expr_temp").$());
			Nest.value(_REQUEST,"expr_temp").$("");
		} else if (isset(_REQUEST,"and_expression")) {
			Nest.value(_REQUEST,"expr_action").$("&");
		} else if (isset(_REQUEST,"or_expression")) {
			Nest.value(_REQUEST,"expr_action").$("|");
		} else if (isset(_REQUEST,"replace_expression")) {
			Nest.value(_REQUEST,"expr_action").$("r");
		} else if (isset(_REQUEST,"remove_expression") && rda_strlen(Nest.value(_REQUEST,"remove_expression").asString())>0) {
			Nest.value(_REQUEST,"expr_action").$("R");
			Nest.value(_REQUEST,"expr_target_single").$(Nest.value(_REQUEST,"remove_expression").$());
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"triggerid")) {
			unset(_REQUEST,"triggerid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (hasRequest("save")) {
			final Map trigger = map(
				"expression", Nest.value(_REQUEST,"expression").$(),
				"description", Nest.value(_REQUEST,"description").$(),
				"type", Nest.value(_REQUEST,"type").$(),
				"priority", Nest.value(_REQUEST,"priority").$(),
				"status", Nest.value(_REQUEST,"status").$(),
				"comments", Nest.value(_REQUEST,"comments").$(),
				"url", Nest.value(_REQUEST,"url").$(),
				"flags", RDA_FLAG_DISCOVERY_PROTOTYPE
			);

			boolean result;
			if (hasRequest("triggerid")) {
				Nest.value(trigger,"triggerid").$(get_request("triggerid"));
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.TriggerPrototype(getIdentityBean(), executor).update(array(trigger)));
					}
				});
				show_messages(result, _("Trigger prototype updated"), _("Cannot update trigger prototype"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.TriggerPrototype(getIdentityBean(), executor).create(array(trigger)));
					}
				});
				show_messages(result, _("Trigger prototype added"), _("Cannot add trigger prototype"));
			}

			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result, get_request("parent_discoveryid"));
			}
			unset(_REQUEST,"save");
		} else if (hasRequest("delete") && hasRequest("triggerid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.TriggerPrototype(getIdentityBean(), executor).delete(Nest.as(get_request("triggerid")).asLong()));
				}
			});
			show_messages(result, _("Trigger prototype deleted"), _("Cannot delete trigger prototype"));
			clearCookies(result, get_request("parent_discoveryid"));

			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"triggerid");
			}
		} else if ("massupdate".equals(get_request("go")) && hasRequest("mass_save") && hasRequest("g_triggerid")) {
			CArray triggerIds = get_request("g_triggerid",array());
			CArray visible = get_request("visible",array());
			boolean result = false;
			if (isset(visible,"priority")) {
				final String priority = get_request("priority");

				for(final Object triggerId : triggerIds) {
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.TriggerPrototype(getIdentityBean(), executor).update(array((Map)map(
									"triggerid", triggerId,
									"priority", priority
								))));
						}
					});
					if (!result) {
						break;
					}
				}
			} else {
				result = true;
			}

			show_messages(result, _("Trigger prototypes updated"), _("Cannot update trigger prototypes"));
			clearCookies(result, get_request("parent_discoveryid"));

			if (result) {
				unset(_REQUEST,"massupdate");
				unset(_REQUEST,"form");
				unset(_REQUEST,"g_triggerid");
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_triggerid")) {
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED;
			final CArray<Map> update = array();

			// get requested triggers with permission check
			CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
			tpoptions.setOutput(new String[]{"triggerid", "status"});
			tpoptions.setTriggerIds(get_requests("g_triggerid").valuesAsLong());
			tpoptions.setEditable(true);
			CArray<Map> dbTriggerPrototypes = API.TriggerPrototype(getIdentityBean(), executor).get(tpoptions);

			boolean result;
			if (!empty(dbTriggerPrototypes)) {
				for(Map dbTriggerPrototype : dbTriggerPrototypes) {
					update.add(map(
						"triggerid", Nest.value(dbTriggerPrototype,"triggerid").$(),
						"status", status
					));
				}
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.TriggerPrototype(getIdentityBean(), executor).update(update));
					}
				});
			} else {
				result = true;
			}

			int updated = count(update);
			String messageSuccess = enable
				? _n("Trigger prototype enabled", "Trigger prototypes enabled", updated)
				: _n("Trigger prototype disabled", "Trigger prototypes disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable trigger prototype", "Cannot enable trigger prototypes", updated)
				: _n("Cannot disable trigger prototype", "Cannot disable trigger prototypes", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("parent_discoveryid"));
		} else if ("delete".equals(get_request("go")) && hasRequest("g_triggerid")) {
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.TriggerPrototype(getIdentityBean(), executor).delete(Nest.as(get_request("g_triggerid")).asLong()));
				}
			});
			show_messages(result, _("Trigger prototypes deleted"), _("Cannot delete trigger prototypes"));
			clearCookies(result, get_request("parent_discoveryid"));
		}

		/* Display */
		if ("massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_triggerid")) {
			CView triggersView = new CView("configuration.triggers.massupdate", getTriggerMassupdateFormData(this.getIdentityBean(), executor));
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		} else if (isset(_REQUEST,"form")) {
			CView triggersView = new CView("configuration.triggers.edit", getTriggerFormData(this.getIdentityBean(), executor));
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		} else {
			Map data = map(
				"parent_discoveryid", get_request("parent_discoveryid"),
				"showErrorColumn", false,
				"discovery_rule", discovery_rule,
				"hostid", get_request("hostid"),
				"showdisabled", get_request("showdisabled", 1),
				"triggers", array()
			);
			CProfile.update(getIdentityBean(), executor, "web.triggers.showdisabled", Nest.value(data,"showdisabled").$(), PROFILE_TYPE_INT);
			Map<String, Object> config = select_config(getIdentityBean(), executor);
			// get triggers
			String sortfield = getPageSortField(getIdentityBean(), executor, "description");
			CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
			tpoptions.setEditable(true);
			tpoptions.setOutput(new String[]{"triggerid"});
			tpoptions.setDiscoveryIds(Nest.value(data,"parent_discoveryid").asLong());
			tpoptions.setSortfield(sortfield);
			tpoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			if (empty(Nest.value(data,"showdisabled").$())) {
				tpoptions.setFilter("status", Nest.as(TRIGGER_STATUS_ENABLED).asString());
			}
			
			CArray<Map> triggers = API.TriggerPrototype(getIdentityBean(), executor).get(tpoptions);
			Nest.value(data,"triggers").$(triggers);

			// paging
			Nest.value(data,"paging").$(getPagingLine(
				getIdentityBean(), 
				executor,
				triggers,
				array("triggerid"),
				map(
					"hostid", get_request("hostid", Nest.value(data,"discovery_rule","hostid").$()),
					"parent_discoveryid", get_request("parent_discoveryid")
				)
			));

			tpoptions = new CTriggerPrototypeGet();
			tpoptions.setTriggerIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
			tpoptions.setOutput(API_OUTPUT_EXTEND);
			tpoptions.setSelectHosts(API_OUTPUT_EXTEND);
			tpoptions.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
			tpoptions.setSelectFunctions(API_OUTPUT_EXTEND);
			triggers = API.TriggerPrototype(getIdentityBean(), executor).get(tpoptions);
			Nest.value(data,"triggers").$(triggers);
			order_result(triggers, sortfield, getPageSortOrder(getIdentityBean(), executor));

			// get real hosts
			Nest.value(data,"realHosts").$(getParentHostsByTriggers(this.getIdentityBean(), executor, triggers));

			// render view
			CView triggersView = new CView("configuration.triggers.list", data);
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		}
	}

}
