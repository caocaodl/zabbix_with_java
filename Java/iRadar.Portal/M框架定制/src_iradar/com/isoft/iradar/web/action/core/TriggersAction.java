package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_diff_assoc;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
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
import static com.isoft.iradar.inc.FormsUtil.getCopyElementsFormData;
import static com.isoft.iradar.inc.FormsUtil.getTriggerFormData;
import static com.isoft.iradar.inc.FormsUtil.getTriggerMassupdateFormData;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.copyTriggersToHosts;
import static com.isoft.iradar.inc.TriggersUtil.getParentHostsByTriggers;
import static com.isoft.iradar.inc.TriggersUtil.orderTriggersByStatus;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TriggersAction extends RadarBaseAction {
	
	public static String CREATE = "create";
	
	@Override
	protected void doInitPage() {
		page("title", _("Trigger"));
		page("file", "triggers.action");
		page("hist_arg", new String[] {"hostid", "groupid"});
		page("js", new String[] {"jquery/jquery.js","jquery/jquery-ui.js"});	
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变阀值状态所需js
		page("css", new String[] {"lessor/strategy/triggers.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",			    array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",			    array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggerid",		    array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"(isset({form})&&({form}==\"update\"))"),
			"copy_type",		    array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	"isset({copy})"),
			"copy_mode",		    array(T_RDA_INT, O_OPT, P_SYS,	IN("0"),	null),
			"type",				    array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null),
			"description",		    array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Name")),
			"expression",		    array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})", _("Expression")),
			"priority",			    array(T_RDA_INT, O_OPT, null,	IN("0,1,2,3,4,5"), "isset({save})",_("Priority")),
			"comments",			    array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"url",				    array(T_RDA_STR, O_OPT, null,	null,		"isset({save})"),
			"status",			    array(T_RDA_STR, O_OPT, null,	null,		null),
			"input_method",		    array(T_RDA_INT, O_OPT, null,	NOT_EMPTY,	"isset({toggle_input_method})"),
			"expr_temp",			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"(isset({add_expression})||isset({and_expression})||isset({or_expression})||isset({replace_expression}))", _("Expression")),
			"expr_target_single", 	array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"(isset({and_expression})||isset({or_expression})||isset({replace_expression}))"),
			"dependencies",			array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"new_dependency",		array(T_RDA_INT, O_OPT, null,	DB_ID+"{}>0", "isset({add_dependency})"),
			"g_triggerid",			array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"copy_targetid",		array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"filter_groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({copy})&&(isset({copy_type})&&({copy_type}==0))"),
			"showdisabled",			array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"massupdate",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"visible",				array(T_RDA_STR, O_OPT, null,	null,		null),
			// actions
			"go",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"toggle_input_method",  array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_expression",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"and_expression",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"or_expression",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"replace_expression",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"remove_expression",	array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"test_expression",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"add_dependency",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_enable",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_disable",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"group_delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"copy",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"mass_save",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",					array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"actionType",			array(T_RDA_STR, O_OPT, null,	null,		null)
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

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions  */
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
		} else if (isset(_REQUEST,"save")) {
			final Map trigger = map(
				"expression", Nest.value(_REQUEST,"expression").$(),
				"description", Nest.value(_REQUEST,"description").$(),
				"priority", Nest.value(_REQUEST,"priority").$(),
				"status", Nest.value(_REQUEST,"status").$(),
				"type", Nest.value(_REQUEST,"type").$(),
				"comments", Nest.value(_REQUEST,"comments").$(),
				"url", Nest.value(_REQUEST,"url").$(),
				"dependencies", rda_toObject(get_request("dependencies", array()), "triggerid")
			);

			boolean result;
			if (!empty(get_request("triggerid"))) {
				// update only changed fields
				CTriggerGet toptions = new CTriggerGet();
				toptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
				toptions.setOutput(API_OUTPUT_EXTEND);
				toptions.setSelectDependencies(new String[]{"triggerid"});
				CArray<Map> oldTriggers = API.Trigger(getIdentityBean(), executor).get(toptions);
				if (empty(oldTriggers)) {
					access_deny();
				}

				Map oldTrigger = reset(oldTriggers);
				Nest.value(oldTrigger,"dependencies").$(rda_toHash(rda_objectValues(Nest.value(oldTrigger,"dependencies").$(), "triggerid")));

				CArray<Map> newDependencies = Nest.value(trigger,"dependencies").asCArray();
				CArray<Map> oldDependencies = Nest.value(oldTrigger,"dependencies").asCArray();
				unset(trigger,"dependencies");
				unset(oldTrigger,"dependencies");

				final Map triggerToUpdate = array_diff_assoc(trigger, oldTrigger);
				Nest.value(triggerToUpdate,"triggerid").$(Nest.value(_REQUEST,"triggerid").$());

				// dependencies
				boolean updateDepencencies = false;
				if (count(newDependencies) != count(oldDependencies)) {
					updateDepencencies = true;
				} else {
					for(Map dependency : newDependencies) {
						if (!isset(oldDependencies, dependency.get("triggerid"))) {
							updateDepencencies = true;
						}
					}
				}
				if (updateDepencencies) {
					Nest.value(triggerToUpdate,"dependencies").$(newDependencies);
				}
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Trigger(getIdentityBean(), executor).update(array(triggerToUpdate)));
					}
				});
				show_messages(result, _("Trigger updated"), _("Cannot update trigger"));
			} else {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.Trigger(getIdentityBean(), executor).create(array(trigger)));
					}
				});
				show_messages(result, _("Trigger added"), _("Cannot add trigger"));
			}

			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"triggerid")) {
			DBstart(executor);
			
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Trigger(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"triggerid").asLong()));
				}
			});
			
			result = DBend(executor, result);
			
			show_messages(result, _("Trigger deleted"), _("Cannot delete trigger"));
			clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
			if (result) {
				unset(_REQUEST,"form");
				unset(_REQUEST,"triggerid");
			}
		} else if (isset(_REQUEST,"add_dependency") && isset(_REQUEST,"new_dependency")) {
			if (!isset(_REQUEST,"dependencies")) {
				Nest.value(_REQUEST,"dependencies").$(array());
			}
			CArray<String> new_dependency = Nest.value(_REQUEST,"new_dependency").asCArray();
			CArray<String> dependencies = Nest.value(_REQUEST,"dependencies").asCArray();
			for(String triggerid:new_dependency) {
				if (!uint_in_array(triggerid, dependencies)) {
					array_push(dependencies, triggerid);
				}
			}
		} else if ("massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"mass_save") && isset(_REQUEST,"g_triggerid")) {
			Map visible = get_request("visible", array());

			// update triggers
			final CArray<Map> triggersToUpdate = array();
			for(String triggerid : (CArray<String>)Nest.value(_REQUEST,"g_triggerid").asCArray()) {
				Map trigger = map("triggerid", triggerid);

				if (isset(visible,"priority")) {
					Nest.value(trigger,"priority").$(get_request("priority"));
				}
				if (isset(visible,"dependencies")) {
					Nest.value(trigger,"dependencies").$(rda_toObject(get_request("dependencies", array()), "triggerid"));
				}

				triggersToUpdate.add(trigger);
			}

			DBstart(executor);
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Trigger(getIdentityBean(), executor).update(triggersToUpdate));
				}
			});
			result = DBend(executor, result);

			show_messages(result, _("Trigger updated"), _("Cannot update trigger"));
			clearCookies(result, Nest.value(_REQUEST,"hostid").asString());

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
				? _n("规则已启用", "规则已启用", updated)
				: _n("规则已停用", "规则已停用", updated);
			String messageFailed = enable
				? _n("规则启用失败", "规则启用失败", updated)
				: _n("规则停用失败", "规则停用失败", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("hostid"));
		} else if ("copy_to".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"copy") && isset(_REQUEST,"g_triggerid")) {
			if (isset(_REQUEST,"copy_targetid") && !empty(Nest.value(_REQUEST,"copy_targetid").$()) && isset(_REQUEST,"copy_type")) {
				CArray hosts_ids = null;
				if (Nest.value(_REQUEST,"copy_type").asInteger() == 0) { // hosts
					hosts_ids = Nest.value(_REQUEST,"copy_targetid").asCArray();
				} else { // groups
					hosts_ids = array();
					CArray group_ids = Nest.value(_REQUEST,"copy_targetid").asCArray();

					SqlBuilder sqlParts = new SqlBuilder();
					CArray<Map> db_hosts = DBselect(executor,
						"SELECT DISTINCT h.hostid"+
						" FROM hosts h,hosts_groups hg"+
						" WHERE h.hostid=hg.hostid"+
							" AND "+sqlParts.dual.dbConditionInt("hg.groupid", group_ids.valuesAsLong()),
						sqlParts.getNamedParams()
					);
					
					CArray<Long> hostids = getHostIdsByG_Triggerid(executor);
					for(Map db_host :db_hosts) {
						if(!Nest.value(_REQUEST,"hostid").asString().equals(Nest.value(db_host, "hostid").asString())&&!hostids.containsValue(Nest.value(db_host, "hostid").asLong()))
							hosts_ids.add(Nest.value(db_host,"hostid").$());
					}
				}

				DBstart(executor);
				final Long[] fhosts_ids = hosts_ids.valuesAsLong();
				boolean goResult = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return copyTriggersToHosts(getIdentityBean(), executor, Nest.array(_REQUEST,"g_triggerid").asLong(), fhosts_ids, Nest.as(get_request("hostid")).asLong());
					}
				});
				goResult = DBend(executor, goResult);

				show_messages(goResult, _("Trigger added"), _("Cannot add trigger"));
				clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());

				Nest.value(_REQUEST,"go").$("none2");
			} else {
				show_error_message(_("No target selected"));
			}
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_triggerid")) {
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.Trigger(getIdentityBean(), executor).delete(Nest.array(_REQUEST,"g_triggerid").asLong()));
				}
			});
			show_messages(goResult, _("Triggers deleted"), _("Cannot delete triggers"));
			clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			if(CREATE.equals(Nest.value(_REQUEST,"actionType").asString())&&empty(get_request("hostid", 0))){
				show_messages(false,"",_("select host first"));
				unset(_REQUEST,"form");
			}
		}
		
		if ("massupdate".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_triggerid")) {
			CView triggersView = new CView("configuration.triggers.massupdate", getTriggerMassupdateFormData(getIdentityBean(), executor));
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		} else if (isset(_REQUEST,"form")) {
			CView triggersView = new CView("configuration.triggers.edit", getTriggerFormData(getIdentityBean(), executor));
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		} else if ("copy_to".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"g_triggerid")) {
			Map data = getCopyElementsFormData(getIdentityBean(), executor, "g_triggerid", _("CONFIGURATION OF TRIGGERS"));
			if(!empty(Nest.value(data, "hosts").$())){
				CArray<Long> hostids = getHostIdsByG_Triggerid(executor);
				Map<Long,Map> hosts = Nest.value(data, "hosts").asCArray();
				Map<Long,Map> copyHosts = Clone.deepcopy(hosts);
				for(Entry<Long,Map> e:hosts.entrySet()){
					Long key = e.getKey();
					Map host = e.getValue();
					if(Nest.value(_REQUEST, "hostid").asString().equals(Nest.value(host, "hostid").asString())||hostids.containsValue(Nest.value(host, "hostid").asLong())){
						copyHosts.remove(key);
						break;
					}
				}
				Nest.value(data, "hosts").$(copyHosts);
			}
			CView triggersView = new CView("configuration.copy.elements", data);
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		} else {
			CArray data = map(
				"showdisabled", get_request("showdisabled", 1),
				"parent_discoveryid", null,
				"triggers", array()
			);
			CProfile.update(getIdentityBean(), executor,"web.triggers.showdisabled", Nest.value(data,"showdisabled").$(), PROFILE_TYPE_INT);

			CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups", map("not_proxy_hosts", true, "editable", true, CPageFilter.KEY_GROUP_SHOW_TEMPLATE, true),
				"hosts", map("templated_hosts", true, "editable", true),
				"triggers", map("editable", true),
				"groupid", get_request("groupid", null),
				"hostid", get_request("hostid", null),
				"triggerid", get_request("triggerid", null)
			));
			Nest.value(data,"pageFilter").$(pageFilter);
			if (pageFilter.$("triggerid").asInteger() > 0) {
				Nest.value(data,"triggerid").$(pageFilter.$("triggerid").$());
			}
			Nest.value(data,"groupid").$(pageFilter.$("groupid").$());
			Nest.value(data,"hostid").$(pageFilter.$("hostid").$());

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			// get triggers
			String sortfield = getPageSortField(getIdentityBean(), executor, "description");
			String sortOrder = getPageSortOrder(getIdentityBean(), executor, Defines.RDA_SORT_UP);
			if (pageFilter.$("hostsSelected").asBoolean()) {
				CTriggerGet toptions = new CTriggerGet();
				toptions.setEditable(true);
				toptions.setOutput(new String[]{"triggerid"});
				toptions.setSortfield(sortfield);
				toptions.setSortorder(sortOrder);
				toptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				if (empty(Nest.value(data,"showdisabled").$())) {
					toptions.setFilter("status", Nest.as(TRIGGER_STATUS_ENABLED).asString());
				}
				if (pageFilter.$("hostid").asInteger() > 0) {
					toptions.setHostIds(pageFilter.$("hostid").asLong());
				} else if (pageFilter.$("groupid").asInteger() > 0) {
					toptions.setGroupIds(pageFilter.$("groupid").asLong());
				}
				Nest.value(data,"triggers").$(API.Trigger(getIdentityBean(), executor).get(toptions));
			}

			Nest.value(_REQUEST,"hostid").$(get_request("hostid", pageFilter.$("hostid").$()));

			// paging
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,Nest.value(data,"triggers").asCArray(), array("triggerid"), map("hostid", Nest.value(_REQUEST,"hostid").$())));

			CTriggerGet toptions = new CTriggerGet();
			toptions.setTriggerIds(rda_objectValues(Nest.value(data,"triggers").asCArray(), "triggerid").valuesAsLong());
			toptions.setOutput(API_OUTPUT_EXTEND);
			toptions.setSortfield(sortfield);
			toptions.setSortorder(sortOrder);
			toptions.setSelectHosts(API_OUTPUT_EXTEND);
			toptions.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
			toptions.setSelectFunctions(API_OUTPUT_EXTEND);
			toptions.setSelectDependencies(API_OUTPUT_EXTEND);
			toptions.setSelectDiscoveryRule(API_OUTPUT_EXTEND);
			CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
			Nest.value(data,"triggers").$(triggers);

//			order_result(triggers,sortfield, sortOrder);
			// get real hosts
			Nest.value(data,"realHosts").$(getParentHostsByTriggers(getIdentityBean(), executor,triggers));

			// determine, show or not column of errors
			Nest.value(data,"showErrorColumn").$(true);
			if (Nest.value(data,"hostid").asInteger() > 0) {
				CHostGet hoptions = new CHostGet();
				hoptions.setHostIds(Nest.value(_REQUEST,"hostid").asLong());
				hoptions.setOutput(new String[]{"status"});
				hoptions.setTemplatedHosts(true);
				hoptions.setEditable(true);
				CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
				Map host = reset(hosts);
				Nest.value(data,"showErrorColumn").$((empty(host) || Nest.value(host,"status").asInteger() != HOST_STATUS_TEMPLATE));
			}

			// render view
			CView triggersView = new CView("configuration.triggers.list", data);
			triggersView.render(getIdentityBean(), executor);
			triggersView.show();
		}
	}
	
	public CArray<Long> getHostIdsByG_Triggerid(SQLExecutor executor){
		CHostGet option = new CHostGet();
		option.setOutput(new String[]{"hostid"});
		option.setTriggerIds(Nest.value(_REQUEST,"g_triggerid").asCArray().valuesAsLong());
		option.setEditable(false);
		CArray<Map> hostidsCA = API.Host(getIdentityBean(), executor).get(option);
		CArray<Long> hostids = FuncsUtil.rda_objectValues(hostidsCA, "hostid");
		return hostids;
	}

}
