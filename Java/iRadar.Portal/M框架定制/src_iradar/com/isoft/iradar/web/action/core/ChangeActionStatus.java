package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_AND_OR;
import static com.isoft.iradar.inc.Defines.ACTION_EVAL_TYPE_OR;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_ACTION;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CActionGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * Ajax异步修改告警响应操作
 * @author HP Pro2000MT
 *
 */
public class ChangeActionStatus extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of actions"));
		page("file", "changeActionStatus.action");
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
			"operations",						array(null,		O_OPT,	null,	null,		"isset({save})"),
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
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		
		  if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("g_actionid")) {
			boolean result = true;
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? ACTION_STATUS_ENABLED : ACTION_STATUS_DISABLED;
			String statusName = enable ? "enabled" : "disabled";
			if(statusName.equals("enabled")){
				statusName = "已启用";
			}else{
				statusName = "已禁用";
			}
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
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ACTION, " 操作： ["+implode(",", actionIds)+"] "+statusName);
			}
	
			String messageSuccess = enable
				? _n("Action enabled", "Actions enabled", updated)
				: _n("Action disabled", "Actions disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable action", "Cannot enable actions", updated)
				: _n("Cannot disable action", "Cannot disable actions", updated);
	
			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result);
		}
	}	
}
