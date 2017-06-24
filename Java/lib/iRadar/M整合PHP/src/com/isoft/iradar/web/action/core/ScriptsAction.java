package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCRIPT;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_EXECUTE_ON_SERVER;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_CUSTOM_SCRIPT;
import static com.isoft.iradar.inc.Defines.RDA_SCRIPT_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ScriptsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Configuration of scripts"));
		page("file", "scripts.action");
		page("hist_arg", new String[] { "scriptid" });
		if (isset(_REQUEST("form"))) {
			page("scripts", new String[] { "multiselect.js" });
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"scriptid",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"scripts",						array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		null),
			"name",						array(T_RDA_STR, O_OPT, null,			NOT_EMPTY,	"isset({save})"),
			"type",							array(T_RDA_INT, O_OPT, null,			IN("0,1"),	"isset({save})"),
			"execute_on",				array(T_RDA_INT, O_OPT, null,			IN("0,1"),	"isset({save})&&{type}=="+RDA_SCRIPT_TYPE_CUSTOM_SCRIPT),
			"command",					array(T_RDA_STR, O_OPT, null,			null,		"isset({save})"),
			"commandipmi",			array(T_RDA_STR, O_OPT, null,			null,		"isset({save})"),
			"description",				array(T_RDA_STR, O_OPT, null,			null,		"isset({save})"),
			"access",						array(T_RDA_INT, O_OPT, null,			IN("0,1,2,3"), "isset({save})"),
			"groupid",						array(T_RDA_INT, O_OPT, null,			DB_ID,		"isset({save})&&{hgstype}!=0"),
			"usrgrpid",					array(T_RDA_INT, O_OPT, P_SYS,			DB_ID,		"isset({save})"),
			"hgstype",					array(T_RDA_INT, O_OPT, null,			null,		null),
			"confirmation",				array(T_RDA_STR, O_OPT, null,			null,		null),
			"enableConfirmation",	array(T_RDA_STR, O_OPT, null,			null,		null),
			// actions
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"action",						array(T_RDA_INT, O_OPT, P_ACT,			IN("0,1"),	null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"delete",						array(T_RDA_STR, O_OPT, P_ACT,			null,		null),
			"clone",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"form",							array(T_RDA_STR, O_OPT, null,			null,		null),
			"form_refresh",				array(T_RDA_INT, O_OPT, null,			null,		null)
		);
		check_fields(getIdentityBean(), fields);
		
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);		
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {		
		/* Permissions */
		Long scriptId = null;
		if (!empty(scriptId  = get_request_asLong("scriptid"))) {
			CScriptGet options = new CScriptGet();
			options.setScriptIds(scriptId);
			options.setOutput(new String[]{"scriptid"});
			CArray<Map> scripts = API.Script(getIdentityBean(), executor).get(options);
			if (empty(scripts)) {
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
		/* Actions */
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"scriptid")) {
			unset(_REQUEST,"scriptid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			String confirmation = get_request("confirmation", "");
			Boolean enableConfirmation = get_request("enableConfirmation", false);
			String command = (Nest.value(_REQUEST,"type").asInteger() == RDA_SCRIPT_TYPE_IPMI) ? Nest.value(_REQUEST,"commandipmi").asString() : Nest.value(_REQUEST,"command").asString();

			if (empty(Nest.value(_REQUEST,"hgstype").$())) {
				Nest.value(_REQUEST,"groupid").$(0);
			}

			if (enableConfirmation && rda_empty(confirmation)) {
				error(_("Please enter confirmation text."));
				show_messages(false, null, _("Cannot add script"));
			} else if (rda_empty(command)) {
				error(_("Command cannot be empty."));
				show_messages(false, null, _("Cannot add script"));
			} else {
				final Map script = map(
					"name", Nest.value(_REQUEST,"name").$(),
					"type", Nest.value(_REQUEST,"type").$(),
					"execute_on", Nest.value(_REQUEST,"execute_on").$(),
					"command", command,
					"description", Nest.value(_REQUEST,"description").$(),
					"usrgrpid", Nest.value(_REQUEST,"usrgrpid").$(),
					"groupid", Nest.value(_REQUEST,"groupid").$(),
					"host_access", Nest.value(_REQUEST,"access").$(),
					"confirmation", get_request("confirmation", "")
				);

				CArray<Long[]> result;
				int auditAction;
				if (isset(_REQUEST,"scriptid")) {
					Nest.value(script,"scriptid").$(Nest.value(_REQUEST,"scriptid").$());
					result = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Script(getIdentityBean(), executor).update(array(script));
						}
					}, null);
					show_messages(!empty(result), _("Script updated"), _("Cannot update script"));
					auditAction = AUDIT_ACTION_UPDATE;
				} else {
					result = Call(new Wrapper<CArray<Long[]>>() {
						@Override
						protected CArray<Long[]> doCall() throws Throwable {
							return API.Script(getIdentityBean(), executor).create(array(script));
						}
					}, null);
					show_messages(!empty(result), _("Script added"), _("Cannot add script"));
					auditAction = AUDIT_ACTION_ADD;
				}

				Long scriptId = isset(result,"scriptids") ? reset(result.get("scriptids")) : null;

				if (!empty(result)) {
					add_audit(getIdentityBean(), executor, auditAction, AUDIT_RESOURCE_SCRIPT, " Name ["+Nest.value(_REQUEST,"name").asString()+"] id ["+scriptId+"]");
					unset(_REQUEST,"action");
					unset(_REQUEST,"form");
					unset(_REQUEST,"scriptid");
					clearCookies(!empty(result));
				}
			}
		} else if (isset(_REQUEST,"delete")) {
			final Integer scriptId = get_request("scriptid", 0);
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Script(getIdentityBean(), executor).delete(Nest.as(scriptId).asLong()));
				}
			});
			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCRIPT, _("Script")+" ["+scriptId+"]");
			}
			show_messages(result, _("Script deleted"), _("Cannot delete script"));
			clearCookies(result);

			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"scriptid");
			}
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"scripts")) {
			final Long[] scriptIds = Nest.array(_REQUEST,"scripts").asLong();
			
			DBstart(executor);
			
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Script(getIdentityBean(), executor).delete(scriptIds));
				}
			});
			if (goResult) {
				for(Long scriptId : scriptIds) {
					add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCRIPT, _("Script")+" ["+scriptId+"]");
				}
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("Script deleted"), _("Cannot delete script"));
			clearCookies(goResult);
			if (goResult) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"scriptid");
			}
		}
		
		/* Display */
		if (isset(_REQUEST,"form")) {
			Map data = map(
				"form", get_request("form", "1"),
				"form_refresh", get_request("form_refresh", 0),
				"scriptid", get_request("scriptid")
			);

			if (empty(Nest.value(data,"scriptid").$()) || isset(_REQUEST,"form_refresh")) {
				Nest.value(data,"name").$(get_request("name", ""));
				Nest.value(data,"type").$(get_request("type", RDA_SCRIPT_TYPE_CUSTOM_SCRIPT));
				Nest.value(data,"execute_on").$(get_request("execute_on", RDA_SCRIPT_EXECUTE_ON_SERVER));
				Nest.value(data,"command").$(get_request("command", ""));
				Nest.value(data,"commandipmi").$(get_request("commandipmi", ""));
				Nest.value(data,"description").$(get_request("description", ""));
				Nest.value(data,"usrgrpid").$(get_request("usrgrpid", 0));
				Nest.value(data,"groupid").$(get_request("groupid", 0));
				Nest.value(data,"access").$(get_request("host_access", 0));
				Nest.value(data,"confirmation").$(get_request("confirmation", ""));
				Nest.value(data,"enableConfirmation").$(get_request("enableConfirmation", false));
				Nest.value(data,"hgstype").$(get_request("hgstype", 0));
			} else if (!empty(Nest.value(data,"scriptid").$())) {
				CScriptGet params = new CScriptGet();
				params.setScriptIds(Nest.value(data,"scriptid").asLong());
				params.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> scripts = API.Script(getIdentityBean(), executor).get(params);
				Map script = reset(scripts);

				Nest.value(data,"name").$(Nest.value(script,"name").$());
				Nest.value(data,"type").$(Nest.value(script,"type").$());
				Nest.value(data,"execute_on").$(Nest.value(script,"execute_on").$());
				Nest.value(data,"command").$(Nest.value(script,"command").$());
				Nest.value(data,"commandipmi").$(Nest.value(script,"command").$());
				Nest.value(data,"description").$(Nest.value(script,"description").$());
				Nest.value(data,"usrgrpid").$(Nest.value(script,"usrgrpid").$());
				Nest.value(data,"groupid").$(Nest.value(script,"groupid").$());
				Nest.value(data,"access").$(Nest.value(script,"host_access").$());
				Nest.value(data,"confirmation").$(Nest.value(script,"confirmation").$());
				Nest.value(data,"enableConfirmation").$(!rda_empty(Nest.value(script,"confirmation").$()));
				Nest.value(data,"hgstype").$(empty(Nest.value(data,"groupid").$()) ? 0 : 1);
			}
			
			// get host gruop
			CArray<Map>hostGroup = array();
			if (!empty(Nest.value(data,"groupid").$())) {
				CHostGroupGet params = new CHostGroupGet();
				params.setGroupIds(Nest.value(data,"groupid").asLong());
				params.setOutput(new String[]{"groupid", "name"});
				CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(params);
				Map group = reset(groups);

				hostGroup.add(map(
					"id", Nest.value(group,"groupid").$(),
					"name", Nest.value(group,"name").$()
				));
			}
			Nest.value(data,"hostGroup").$(hostGroup);

			// get list of user groups
			CUserGroupGet options = new CUserGroupGet();
			options.setOutput(new String[]{"usrgrpid", "name"});
			CArray<Map> usergroups = API.UserGroup(getIdentityBean(), executor).get(options);
			order_result(usergroups, "name");
			Nest.value(data,"usergroups").$(usergroups);

			// render view
			CView scriptView = new CView("administration.script.edit", data);
			scriptView.render(getIdentityBean(), executor);
			scriptView.show();
		} else {
			Map data = map();

			// list of scripts
			CScriptGet options = new CScriptGet();
			options.setOutput(new String[]{"scriptid", "name", "command", "host_access", "usrgrpid", "groupid", "type", "execute_on"});
			options.setEditable(true);
			options.setSelectGroups(API_OUTPUT_EXTEND);
			CArray<Map> scripts =API.Script(getIdentityBean(), executor).get(options);
			Nest.value(data,"scripts").$(scripts);

			// find script host group name and user group name. set to "" if all host/user groups used.
			for (Entry<Object, Map> e : scripts.entrySet()) {
			    Object key = e.getKey();
			    Map script = e.getValue();

				if (Nest.value(script,"usrgrpid").asInteger() > 0) {
					CUserGroupGet params = new CUserGroupGet();
					params.setUsrgrpIds(Nest.value(script,"usrgrpid").asLong());
					params.setOutput(API_OUTPUT_EXTEND);
					CArray<Map> userGroups = API.UserGroup(getIdentityBean(), executor).get(params);
					Map userGroup = reset(userGroups);
					Nest.value(scripts,key,"userGroupName").$(Nest.value(userGroup,"name").asString());
				} else {
					Nest.value(scripts,key,"userGroupName").$("");// all user groups
				}

				if (Nest.value(script,"groupid").asInteger() > 0) {
					Map group = array_pop(Nest.value(script,"groups").asCArray());
					Nest.value(scripts,key,"hostGroupName").$(Nest.value(group,"name").asString());
				} else {
					Nest.value(scripts,key,"hostGroupName").$(""); // all host groups
				}
			}

			// sorting & paging
			order_result(scripts, getPageSortField(getIdentityBean(), executor,"name"), getPageSortOrder(getIdentityBean(), executor));
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,scripts, array("scriptid")));

			// render view
			CView scriptView = new CView("administration.script.list", data);
			scriptView.render(getIdentityBean(), executor);
			scriptView.show();
		}
	}

}
