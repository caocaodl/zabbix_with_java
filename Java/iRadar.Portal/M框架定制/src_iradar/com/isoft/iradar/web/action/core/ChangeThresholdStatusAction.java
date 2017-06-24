package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_MULT_EVENT_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ChangeThresholdStatusAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of IT services"));
		page("file", "changeStatus.action");
		page("hist_arg", new String[] {"hostid", "groupid"});
		page("js", new String[] {"jquery/jquery.js","jquery/jquery-ui.js"});	
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变阀值状态所需js
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",						array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggerid",					array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"(isset({form})&&({form}==\"update\"))"),
			"copy_type",					array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	"isset({copy})"),
			"copy_mode",				array(T_RDA_INT, O_OPT, P_SYS,	IN("0"),	null),
			"type",							array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"description",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Name")),
			"expression",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Expression")),
			"priority",						array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3,4,5"), "isset({save})"),
			"comments",				array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"url",								array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"status",						array(T_RDA_STR, O_OPT, null,	null,		null),
			"input_method",			array(T_RDA_INT, O_OPT, null,	NOT_EMPTY,	"isset({toggle_input_method})"),
			"expr_temp",				array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"(isset({add_expression})||isset({and_expression})||isset({or_expression})||isset({replace_expression}))", _("Expression")),
			"expr_target_single", 	array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"(isset({and_expression})||isset({or_expression})||isset({replace_expression}))"),
			"dependencies",			array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"new_dependency",		array(T_RDA_INT, O_OPT, null,	DB_ID+"{}>0", "isset({add_dependency})"),
			"g_triggerid",				array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"copy_targetid",			array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"filter_groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({copy})&&(isset({copy_type})&&({copy_type}==0))"),
			"showdisabled",			array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"massupdate",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"visible",						array(T_RDA_STR, O_OPT, null,	null,		null),
			// actions
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"toggle_input_method",array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_expression",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"and_expression",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"or_expression",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"replace_expression",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"remove_expression",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"test_expression",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_dependency",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_enable",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_disable",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_delete",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"copy",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"mass_save",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",							array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",				array(T_RDA_INT, O_OPT, null,	null,		null)
		);
		Nest.value(_REQUEST,"showdisabled").$(get_request("showdisabled", CProfile.get(getIdentityBean(), executor,"web.triggers.showdisabled", 1)));

		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"description", RDA_SORT_UP);

		Nest.value(_REQUEST,"status").$(isset(Nest.value(_REQUEST,"status").$()) ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED);
		Nest.value(_REQUEST,"type").$(isset(Nest.value(_REQUEST,"type").$()) ? TRIGGER_MULT_EVENT_ENABLED : TRIGGER_MULT_EVENT_DISABLED);
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// validate permissions
		if (!empty(get_request("triggerid"))) {
			CTriggerGet toptions = new CTriggerGet();
			toptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
			toptions.setOutput(new String[]{"triggerid"});
			toptions.setPreserveKeys(true);
			toptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			toptions.setEditable(true);
			CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
			if (empty(triggers)) {
				access_deny();
			}
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	/**
	 * 修改阀值规则中的状态
	 */
	@Override
	public void doAction(final SQLExecutor executor) {
		
		if(str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_triggerid")){
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? TRIGGER_STATUS_ENABLED : TRIGGER_STATUS_DISABLED;
			final CArray<Map> update = array();

			// get requested triggers with permission check
			CTriggerGet toptions = new CTriggerGet();
			toptions.setOutput(new String[]{"triggerid", "status"});
			toptions.setTriggerIds(Nest.array(_REQUEST, "g_triggerid").asLong());
			toptions.setEditable(true);
			CArray<Map> dbTriggers = API.Trigger(getIdentityBean(), executor).get(toptions);

			boolean result;
			if (!empty(dbTriggers)) {
				for(Map dbTrigger : dbTriggers) {
					update.add(map(
						"triggerid", Nest.value(dbTrigger,"triggerid").$(),
						"status", status
					));
				}

				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Trigger(getIdentityBean(), executor).update(update));
					}
				});
			} else {
				result = true;
			}

			int updated = count(update);
			String messageSuccess = enable
				? _n("Trigger enabled", "Triggers enabled", updated)
				: _n("Trigger disabled", "Triggers disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable trigger", "Cannot enable triggers", updated)
				: _n("Cannot disable trigger", "Cannot disable triggers", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("hostid"));
		}
	}
}
