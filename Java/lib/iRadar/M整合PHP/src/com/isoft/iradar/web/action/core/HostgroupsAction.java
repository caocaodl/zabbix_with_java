package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.getDeletableHostGroups;
import static com.isoft.iradar.inc.HostsUtil.get_hostgroup_by_groupid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostgroupsAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of host groups"));
		page("file", "hostgroups.action");
		page("hist_arg", new String[] {});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hosts",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groups",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostids",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"groupids",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// group
			"groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		"isset({form})&&{form}==\"update\""),
			"name",			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})"),
			"twb_groupid",	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			// actions
			"go",					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// other
			"form",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",	array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);

		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isWritable(Nest.value(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		boolean result;
		if (isset(_REQUEST,"clone") && isset(_REQUEST,"groupid")) {
			unset(_REQUEST,"groupid");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			CArray hostIds = get_request("hosts", array());

			CHostGet hoptions = new CHostGet();
			hoptions.setHostIds(hostIds.valuesAsLong());
			hoptions.setOutput(new String[]{"hostid"});
			hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			final CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);

			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(hostIds.valuesAsLong());
			toptions.setOutput(new String[]{"templateid"});
			final CArray<Map>  templates = API.Template(getIdentityBean(), executor).get(toptions);

			String msgOk,msgFail;
			if (!empty(Nest.value(_REQUEST,"groupid").$())) {
				DBstart(executor);

				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
				hgoptions.setOutput(API_OUTPUT_EXTEND);
				CArray<Map> oldGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
				Map oldGroup = reset(oldGroups);

				result = true;
				// don't try to update the name for a discovered host group
				if (Nest.value(oldGroup,"flags").asInteger() != RDA_FLAG_DISCOVERY_CREATED) {
					result = !empty(API.HostGroup(getIdentityBean(), executor).update(array((Map)map(
						"groupid", Nest.value(_REQUEST,"groupid").$(),
						"name", Nest.value(_REQUEST,"name").$()
					))));
				}
				
				result = DBend(executor, result);

				CArray<Map> groups = null;
				if (result) {
					hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
					hgoptions.setOutput(API_OUTPUT_EXTEND);
					final CArray groups_tmp = groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
					result = Call(new Wrapper<Boolean>() {
						@Override protected Boolean doCall() throws Throwable {
							return !empty(API.HostGroup(getIdentityBean(), executor).massUpdate(map(
								"hosts", hosts,
								"templates", templates,
								"groups", groups_tmp
							)));
						}
					});
				}

				if (result) {
					Map group = reset(groups);

					add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_HOST_GROUP, Nest.value(group,"groupid").asLong(), Nest.value(group,"name").asString(),
						"groups", (Map)map("name", Nest.value(oldGroup,"name").$()), (Map)map("name", Nest.value(group,"name").$()));
				}

				msgOk = _("Group updated");
				msgFail = _("Cannot update group");
			} else {
				DBstart(executor);
				
				CArray hostgroup = Call(new Wrapper<CArray>() {
					@Override
					protected CArray doCall() throws Throwable {
						return API.HostGroup(getIdentityBean(), executor).create(array((Map)map("name" , Nest.value(_REQUEST,"name").$())));
					}
				}, null);
				result = !empty(hostgroup);
				
				if (result) {
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setGroupIds(Nest.array(hostgroup,"groupids").asLong());
					hgoptions.setOutput(API_OUTPUT_EXTEND);
					final CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);

					hostgroup = Call(new Wrapper<CArray>() {
						@Override
						protected CArray doCall() throws Throwable {
							return API.HostGroup(getIdentityBean(), executor).massAdd(map(
									"hosts", hosts,
									"templates", templates,
									"groups", groups
								));
						}
					}, null);
					result = !empty(hostgroup);

					if (result) {
						Map group = reset(groups);
						add_audit_ext(getIdentityBean(), executor, AUDIT_ACTION_ADD, AUDIT_RESOURCE_HOST_GROUP, Nest.value(group,"groupid").asLong(), Nest.value(group,"name").asString(), null, null, null);
					}
				}
				
				result = DBend(executor, result);

				msgOk = _("Group added");
				msgFail = _("Cannot add group");
			}

			show_messages(result, msgOk, msgFail);

			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result);
			}
			unset(_REQUEST,"save");
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"groupid")) {
			result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HostGroup(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"groupid").asLong()));
				}
			});
			show_messages(result, _("Group deleted"), _("Cannot delete group"));
			if (result) {
				unset(_REQUEST,"form");
				clearCookies(result);
			}
			unset(_REQUEST,"delete");
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HostGroup(getIdentityBean(), executor).delete(get_request("groups", array()).valuesAsLong()));
				}
			});
			show_messages(goResult, _("Group deleted"), _("Cannot delete group"));
			clearCookies(goResult);
		} else if (str_in_array(get_request("go"), array("activate", "disable"))) {
			boolean enable = ("activate".equals(get_request("go")));
			final int status = enable ? HOST_STATUS_MONITORED : HOST_STATUS_NOT_MONITORED;
			int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;

			CArray groups = get_request("groups", array());

			if (!empty(groups)) {
				DBstart(executor);
				
				CHostGet hoptions = new CHostGet();
				hoptions.setGroupIds(groups.valuesAsLong());
				hoptions.setEditable(true);
				hoptions.setOutput(API_OUTPUT_EXTEND);
				final CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
				if (!empty(hosts)) {
					result = Call(new Wrapper<Boolean>() {
						@Override
						protected Boolean doCall() throws Throwable {
							return !empty(API.Host(getIdentityBean(), executor).massUpdate(map(
									"hosts", hosts,
									"status", status
								)));
						}
					});

					if (result) {
						for(Map host : hosts) {
							add_audit_ext(
								getIdentityBean(), 
								executor,
								auditAction,
								AUDIT_RESOURCE_HOST,
								Nest.value(host,"hostid").asLong(),
								Nest.value(host,"host").asString(),
								"hosts",
								(Map)map("status", Nest.value(host,"status").$()),
								(Map)map("status", status)
							);
						}
					}
				} else {
					result = true;
				}
				
				result = DBend(executor, result);

				int updated = count(hosts);

				String messageSuccess = enable
					? _n("Host enabled", "Hosts enabled", updated)
					: _n("Host disabled", "Hosts disabled", updated);
				String messageFailed = enable
					? _n("Cannot enable host", "Cannot enable hosts", updated)
					: _n("Cannot disable host", "Cannot disable hosts", updated);

				show_messages(result, messageSuccess, messageFailed);
				clearCookies(result);
			}
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"form", get_request("form"),
				"groupid", get_request("groupid", 0),
				"hosts", get_request("hosts", array()),
				"name", get_request("name", ""),
				"twb_groupid", get_request("twb_groupid", -1)
			);

			if (Nest.value(data,"groupid").asInteger() > 0) {
				Nest.value(data,"group").$(get_hostgroup_by_groupid(getIdentityBean(), executor, Nest.value(data,"groupid").asString()));

				// if first time select all hosts for group from db
				if (!isset(_REQUEST,"form_refresh")) {
					Nest.value(data,"name").$(Nest.value(data,"group","name").$());

					CHostGet hoptions = new CHostGet();
					hoptions.setGroupIds(Nest.value(data,"groupid").asLong());
					hoptions.setTemplatedHosts(true);
					hoptions.setOutput(new String[]{"hostid"});
					CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
					Nest.value(data,"hosts").$(hosts);

					Nest.value(data,"hosts").$(rda_toHash(rda_objectValues(hosts, "hostid"), "hostid"));
				}
			}

			// get all possible groups
			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setNotProxyHosts(true);
			hgoptions.setSortfield("name");
			hgoptions.setEditable(true);
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> db_groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"db_groups").$(db_groups);

			if (Nest.value(data,"twb_groupid").asInteger() == -1) {
				Map gr = reset(db_groups);
				Nest.value(data,"twb_groupid").$(Nest.value(gr,"groupid").$());
			}

			// get all possible hosts
			CHostGet hoptions = new CHostGet();
			if(!empty(Nest.value(data,"twb_groupid").$())){
				hoptions.setGroupIds(Nest.value(data,"twb_groupid").asLong());
			}
			hoptions.setTemplatedHosts(true);
			hoptions.setSortfield("name");
			hoptions.setEditable(true);
			hoptions.setOutput(API_OUTPUT_EXTEND);
			hoptions.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
			CArray<Map> db_hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Nest.value(data,"db_hosts").$(db_hosts);

			// get selected hosts
			hoptions = new CHostGet();
			hoptions.setHostIds(Nest.array(data,"hosts").asLong());
			hoptions.setTemplatedHosts(true);
			hoptions.setSortfield("name");
			hoptions.setOutput(API_OUTPUT_EXTEND);
			CArray<Map> r_hosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Nest.value(data,"r_hosts").$(rda_toHash(r_hosts, "hostid"));

			// deletable groups
			if (!empty(Nest.value(data,"groupid").$())) {
				Nest.value(data,"deletableHostGroups").$(getDeletableHostGroups(getIdentityBean(), executor, Nest.array(data,"groupid").asLong()));
			}

			// render view
			CView hostgroupView = new CView("configuration.hostgroups.edit", data);
			hostgroupView.render(getIdentityBean(), executor);
			hostgroupView.show();
		} else {
			Map<String, Object> config = select_config(getIdentityBean(), executor);
			CArray data = map(
				"config", config
			);

			String sortfield = getPageSortField(getIdentityBean(), executor,"name");
			String sortorder = getPageSortOrder(getIdentityBean(), executor);

			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setEditable(true);
			hgoptions.setOutput(new String[]{"groupid"});
			hgoptions.setSortfield(sortfield);
			hgoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);

			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, groups, array("groupid")));

			// get hosts and templates count
			hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(rda_objectValues(groups, "groupid").valuesAsLong());
			hgoptions.setSelectHosts(API_OUTPUT_COUNT);
			hgoptions.setSelectTemplates(API_OUTPUT_COUNT);
			hgoptions.setNopermissions(true);
			CArray<Map> groupCounts = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"groupCounts").$(rda_toHash(groupCounts, "groupid"));

			// get host groups
			hgoptions = new CHostGroupGet();
			hgoptions.setGroupIds(rda_objectValues(groups, "groupid").valuesAsLong());
			hgoptions.setSelectHosts(new String[]{"hostid", "name", "status"});
			hgoptions.setSelectTemplates(new String[]{"hostid", "name", "status"});
			hgoptions.setSelectGroupDiscovery(new String[]{"ts_delete"});
			hgoptions.setSelectDiscoveryRule(new String[]{"itemid", "name"});
			hgoptions.setOutput(API_OUTPUT_EXTEND);
			hgoptions.setLimitSelects(Nest.value(config,"max_in_table").asInteger() + 1);
			groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
			Nest.value(data,"groups").$(groups);
			order_result(groups, sortfield, sortorder);

			// render view
			CView hostgroupView = new CView("configuration.hostgroups.list", data);
			hostgroupView.render(getIdentityBean(), executor);
			hostgroupView.show();
		}
	}

}
