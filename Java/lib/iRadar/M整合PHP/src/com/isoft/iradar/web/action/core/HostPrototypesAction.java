package com.isoft.iradar.web.action.core;

import static com.isoft.biz.daoimpl.radar.CDB.dbfetchArrayAssoc;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
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
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_AUTOMATIC;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_MANUAL;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.getHostPrototypeSourceParentIds;
import static com.isoft.iradar.inc.ItemsUtil.get_realrule_by_itemid_and_hostid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHostPrototypeGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostPrototypesAction extends RadarBaseAction {
	
	private Map discoveryRule;
	private Map hostPrototype;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of host prototypes"));
		page("file", "host_prototypes.action");
		page("scripts", new String[] {"effects.js", "class.cviewswitcher.js", "multiselect.js"});
		page("hist_arg", new String[] {"parent_discoveryid"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hostid",						array(T_RDA_INT, O_NO,	P_SYS,	DB_ID,		"(isset({form})&&({form}==\"update\"))"),
			"parent_discoveryid",	array(T_RDA_INT, O_MAND, P_SYS,	DB_ID, null),
			"host",		        			array(T_RDA_STR, O_OPT, null,		NOT_EMPTY,	"isset({save})", _("Host name")),
			"name",	            		array(T_RDA_STR, O_OPT, null,		null,		"isset({save})"),
			"status",		        		array(T_RDA_INT, O_OPT, null,		        IN(array(HOST_STATUS_NOT_MONITORED, HOST_STATUS_MONITORED)), "isset({save})"),
			"inventory_mode",		array(T_RDA_INT, O_OPT, null, IN(array(HOST_INVENTORY_DISABLED, HOST_INVENTORY_MANUAL, HOST_INVENTORY_AUTOMATIC)), null),
			"templates",		    		array(T_RDA_STR, O_OPT, null, NOT_EMPTY,	null),
			"add_template",			array(T_RDA_STR, O_OPT, null,		null,	null),
			"add_templates",		    array(T_RDA_STR, O_OPT, null, NOT_EMPTY,	null),
			"group_links",				array(T_RDA_STR, O_OPT, null, NOT_EMPTY,	null),
			"group_prototypes",		array(T_RDA_STR, O_OPT, null, NOT_EMPTY,	null),
			"unlink",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,		null),
			"group_hostid",			array(T_RDA_INT, O_OPT, null,	DB_ID,		null),
			"go",								array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"save",							array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"clone",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"update",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"delete",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"cancel",						array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form",							array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"form_refresh",				array(T_RDA_INT, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		Nest.value(_REQUEST,"go").$(get_request("go", "none"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions*/
		if (!empty(get_request("parent_discoveryid"))) {
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setItemIds(Nest.value(_REQUEST,"parent_discoveryid").asLong());
			droptions.setOutput(API_OUTPUT_EXTEND);
			droptions.setSelectHosts(new String[]{"flags"});
			droptions.setEditable(true);
			CArray<Map> discoveryRules = API.DiscoveryRule(getIdentityBean(), executor).get(droptions);
			discoveryRule = reset(discoveryRules);
			if (empty(discoveryRule) || Nest.value(discoveryRule,"hosts",0,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED) {
				access_deny();
			}

			if (!empty(get_request("hostid"))) {
				CHostPrototypeGet hpoptions = new CHostPrototypeGet();
				hpoptions.setHostIds(get_request_asLong("hostid"));
				hpoptions.setOutput(API_OUTPUT_EXTEND);
				hpoptions.setSelectGroupLinks(API_OUTPUT_EXTEND);
				hpoptions.setSelectGroupPrototypes(API_OUTPUT_EXTEND);
				hpoptions.setSelectTemplates(new String[]{"templateid", "name"});
				hpoptions.setSelectParentHost(new String[]{"hostid"});
				hpoptions.setSelectInventory(API_OUTPUT_EXTEND);
				hpoptions.setEditable(true);
				CArray<Map> hostPrototypes = API.HostPrototype(getIdentityBean(), executor).get(hpoptions);
				hostPrototype = reset(hostPrototypes);
				if (empty(hostPrototype)) {
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
		/** Actions*/
		// add templates to the list
		if (!empty(get_request("add_template"))) {
			for(Object templateId : get_request("add_templates", array())) {
				Nest.value(_REQUEST,"templates",templateId).$(templateId);
			}
		} else if (!empty(get_request("unlink"))) {// unlink templates
			for(Object templateId : get_request("unlink",array()).keySet()) {
				unset(Nest.value(_REQUEST,"templates").asCArray(),templateId);
			}
		} else if (isset(_REQUEST,"delete") && isset(_REQUEST,"hostid")) {
			DBstart(executor);
			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HostPrototype(getIdentityBean(), executor).delete(Nest.as(get_request("hostid")).asLong()));
				}
			});

			show_messages(result, _("Host prototype deleted"), _("Cannot delete host prototypes"));

			unset(_REQUEST,"hostid");
			unset(_REQUEST,"form");
			
			DBend(executor, result);
			clearCookies(result, Nest.value(discoveryRule,"itemid").asString());
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"hostid")) {
			unset(_REQUEST,"hostid");
			for(Map groupPrototype : (CArray<Map>)Nest.value(_REQUEST,"group_prototypes").asCArray()) {
				unset(groupPrototype,"group_prototypeid");
			}
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			DBstart(executor);
			
			final Map newHostPrototype = map(
				"host", get_request("host"),
				"name", get_request("name"),
				"status", get_request("status"),
				"groupLinks", array(),
				"groupPrototypes", array(),
				"templates", get_request("templates", array()),
				"status", get_request("status"),
				"inventory", map(
					"inventory_mode", get_request("inventory_mode")
				)
			);

			// add custom group prototypes
			for(Map groupPrototype : get_request("group_prototypes", new CArray<Map>())) {
				if (empty(Nest.value(groupPrototype,"group_prototypeid").$())) {
					unset(groupPrototype,"group_prototypeid");
				}

				if (!rda_empty(Nest.value(groupPrototype,"name").$())) {
					Nest.value(newHostPrototype,"groupPrototypes").asCArray().add(groupPrototype);
				}
			}

			boolean result;
			if (!empty(get_request("hostid"))) {
				Nest.value(newHostPrototype,"hostid").$(get_request("hostid"));

				if (empty(Nest.value(hostPrototype,"templateid").$())) {
					// add group prototypes based on existing host groups
					CArray groupPrototypesByGroupId = rda_toHash(Nest.value(hostPrototype,"groupLinks").asCArray(), "groupid");
					unset(groupPrototypesByGroupId,0);
					for(Object groupId : get_request("group_links", array())) {
						if (isset(groupPrototypesByGroupId,groupId)) {
							Nest.value(newHostPrototype,"groupLinks").asCArray().add(map(
								"groupid", Nest.value(groupPrototypesByGroupId,groupId,"groupid").$(),
								"group_prototypeid", Nest.value(groupPrototypesByGroupId,groupId,"group_prototypeid").$()
							));
						} else {
							Nest.value(newHostPrototype,"groupLinks").asCArray().add(map(
								"groupid", groupId
							));
						}
					}
				} else {
					unset(newHostPrototype,"groupPrototypes");
					unset(newHostPrototype,"groupLinks");
				}

				final Map fnewHostPrototype = CArrayHelper.unsetEqualValues(newHostPrototype, hostPrototype, array("hostid"));
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.HostPrototype(getIdentityBean(), executor).update(array(fnewHostPrototype)));
					}
				});
				show_messages(result, _("Host prototype updated"), _("Cannot update host prototype"));
			} else {
				Nest.value(newHostPrototype,"ruleid").$(get_request("parent_discoveryid"));

				// add group prototypes based on existing host groups
				for(Object groupId : get_request("group_links", array())) {
					Nest.value(newHostPrototype,"groupLinks").asCArray().add(map(
						"groupid", groupId
					));
				}

				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.HostPrototype(getIdentityBean(), executor).create(array(newHostPrototype)));
					}
				});
				show_messages(result, _("Host prototype added"), _("Cannot add host prototype"));
			}

			if (result) {
				unset(_REQUEST,"itemid");
				unset(_REQUEST,"form");
			}

			DBend(executor, result);
			clearCookies(result, Nest.value(discoveryRule,"itemid").asString());
		}
		// GO
		else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("group_hostid")) {
			boolean enable = ("activate".equals(get_request("go")));
			int status = enable ? HOST_STATUS_MONITORED : HOST_STATUS_NOT_MONITORED;
			final CArray<Map> update = array();

			DBstart(executor);
			for(Object hostPrototypeId : get_request("group_hostid",array())) {
				update.add(map(
					"hostid", hostPrototypeId,
					"status", status
				));
			}

			boolean result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HostPrototype(getIdentityBean(), executor).update(update));
				}
			});
			DBend(executor, result);
			int updated = count(update);

			String messageSuccess = enable
				? _n("Host prototype enabled", "Host prototypes enabled", updated)
				: _n("Host prototype disabled", "Host prototypes disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable host prototype", "Cannot enable host prototypes", updated)
				: _n("Cannot disable host prototype", "Cannot disable host prototypes", updated);

			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, Nest.value(discoveryRule,"itemid").asString());
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_hostid")) {
			DBstart(executor);
			boolean go_result = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HostPrototype(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"group_hostid").asLong()));
				}
			});
			show_messages(go_result, _("Host prototypes deleted"), _("Cannot delete host prototypes"));
			DBend(executor, go_result);
			clearCookies(go_result, Nest.value(discoveryRule,"itemid").asString());
		}

		/* Display */
		if (isset(_REQUEST,"form")) {
			Map data = map(
				"discovery_rule", discoveryRule,
				"host_prototype", map(
					"hostid", get_request("hostid"),
					"templateid", get_request("templateid"),
					"host", get_request("host"),
					"name", get_request("name"),
					"status", get_request("status", HOST_STATUS_MONITORED),
					"templates", array(),
					"inventory", map(
						"inventory_mode", get_request("inventory_mode", HOST_INVENTORY_DISABLED)
					),
					"groupPrototypes", get_request("group_prototypes", array())
				),
				"groups", array()
			);

			// add already linked and new templates
			CTemplateGet toptions = new CTemplateGet();
			toptions.setOutput(new String[]{"templateid", "name"});
			toptions.setTemplateIds(get_request("templates", array()).valuesAsLong());
			Nest.value(data,"host_prototype","templates").$(API.Template(getIdentityBean(), executor).get(toptions));

			// add parent host
			CHostGet hoptions = new CHostGet();
			hoptions.setOutput(API_OUTPUT_EXTEND);
			hoptions.setSelectGroups(new String[]{"groupid", "name"});
			hoptions.setSelectInterfaces(API_OUTPUT_EXTEND);
			hoptions.setSelectMacros(API_OUTPUT_EXTEND);
			hoptions.setHostIds(Nest.value(discoveryRule,"hostid").asLong());
			hoptions.setTemplatedHosts(true);
			CArray<Map> parentHosts = API.Host(getIdentityBean(), executor).get(hoptions);
			Map parentHost = reset(parentHosts);
			Nest.value(data,"parent_host").$(parentHost);

			if (!empty(get_request("group_links"))) {
				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setOutput(API_OUTPUT_EXTEND);
				hgoptions.setGroupIds(get_request_asLong("group_links"));
				hgoptions.setEditable(true);
				hgoptions.setPreserveKeys(true);
				Nest.value(data,"groups").$(API.HostGroup(getIdentityBean(), executor).get(hgoptions));
			}

			if (!empty(Nest.value(parentHost,"proxy_hostid").$())) {
				CProxyGet poptions = new CProxyGet();
				poptions.setOutput(new String[]{"host", "proxyid"});
				poptions.setProxyIds(Nest.value(parentHost,"proxy_hostid").asLong());
				poptions.setLimit(1);
				CArray<Map> proxies = API.Proxy(getIdentityBean(), executor).get(poptions);
				Nest.value(data,"proxy").$(reset(proxies));
			}

			// host prototype edit form
			if (!empty(get_request("hostid")) && empty(get_request("form_refresh"))) {
				Nest.value(data,"host_prototype").$(array_merge(Nest.value(data,"host_prototype").asCArray(), hostPrototype));

				CHostGroupGet hgoptions = new CHostGroupGet();
				hgoptions.setOutput(API_OUTPUT_EXTEND);
				hgoptions.setGroupIds(rda_objectValues(Nest.value(data,"host_prototype","groupLinks").$(), "groupid").valuesAsLong());
				hgoptions.setEditable(true);
				hgoptions.setPreserveKeys(true);
				Nest.value(data,"groups").$(API.HostGroup(getIdentityBean(), executor).get(hgoptions));

				// add parent templates
				if (!empty(Nest.value(hostPrototype,"templateid").$())) {
					Nest.value(data,"parents").$(array());
					Long hostPrototypeId = Nest.value(hostPrototype,"templateid").asLong();
					while (!empty(hostPrototypeId)) {
						CHostPrototypeGet hpoptions = new CHostPrototypeGet();
						hpoptions.setOutput(new String[]{"itemid", "templateid"});
						hpoptions.setSelectParentHost(new String[]{"hostid", "name"});
						hpoptions.setSelectDiscoveryRule(new String[]{"itemid"});
						hpoptions.setHostIds(hostPrototypeId);
						CArray<Map> parentHostPrototypes = API.HostPrototype(getIdentityBean(), executor).get(hpoptions);
						Map parentHostPrototype = reset(parentHostPrototypes);
						hostPrototypeId = null;

						if (!empty(parentHostPrototype)) {
							Nest.value(data,"parents").asCArray().add(parentHostPrototype);
							hostPrototypeId = Nest.value(parentHostPrototype,"templateid").asLong();
						}
					}
				}
			}

			// order linked templates
			CArrayHelper.sort(Nest.value(data,"host_prototype","templates").asCArray(), array("name"));

			// render view
			CView itemView = new CView("configuration.host.prototype.edit", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		} else {
			Map data = map(
				"form", get_request("form", null),
				"parent_discoveryid", get_request("parent_discoveryid", null),
				"discovery_rule", discoveryRule
			);

			Map<String, Object> config = select_config(getIdentityBean(), executor);
			
			// get items
			String sortfield = getPageSortField(getIdentityBean(), executor, "name");
			CHostPrototypeGet hpoptions = new CHostPrototypeGet();
			hpoptions.setDiscoveryIds(Nest.value(data,"parent_discoveryid").asLong());
			hpoptions.setOutput(API_OUTPUT_EXTEND);
			hpoptions.setSelectTemplates(new String[]{"templateid", "name"});
			hpoptions.setEditable(true);
			hpoptions.setSortfield(sortfield);
			hpoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
			Nest.value(data,"hostPrototypes").$(API.HostPrototype(getIdentityBean(), executor).get(hpoptions));

			if (!empty(Nest.value(data,"hostPrototypes").$())) {
				order_result(Nest.value(data,"hostPrototypes").asCArray(), sortfield, getPageSortOrder(getIdentityBean(), executor));
			}

			Nest.value(data,"paging").$(getPagingLine(
				getIdentityBean(), 
				executor,
				Nest.value(data,"hostPrototypes").asCArray(),
				array("hostid"),
				map("parent_discoveryid" , get_request("parent_discoveryid"))
			));

			// fetch templates linked to the prototypes
			CArray templateIds = array();
			for(Map hostPrototype : (CArray<Map>)Nest.value(data,"hostPrototypes").asCArray()) {
				templateIds = array_merge(templateIds, rda_objectValues(Nest.value(hostPrototype,"templates").$(), "templateid"));
			}
			templateIds = array_unique(templateIds);

			CTemplateGet toptions = new CTemplateGet();
			toptions.setTemplateIds(templateIds.valuesAsLong());
			toptions.setSelectParentTemplates(new String[]{"hostid", "name"});
			CArray<Map> linkedTemplates = API.Template(getIdentityBean(), executor).get(toptions);
			Nest.value(data,"linkedTemplates").$(rda_toHash(linkedTemplates, "templateid"));

			// fetch source templates and LLD rules
			CArray<Long> hostPrototypeSourceIds = getHostPrototypeSourceParentIds(getIdentityBean(), executor,rda_objectValues(Nest.value(data,"hostPrototypes").$(), "hostid").valuesAsLong());
			if (!empty(hostPrototypeSourceIds)) {
				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> hostPrototypeSourceTemplates = dbfetchArrayAssoc(DBselect(
					executor,
					"SELECT h.hostid,h2.name,h2.hostid AS parent_hostid"+
					" FROM hosts h,host_discovery hd,items i,hosts h2"+
					" WHERE h.hostid=hd.hostid"+
						" AND hd.parent_itemid=i.itemid"+
						" AND i.hostid=h2.hostid"+
						" AND "+sqlParts.dual.dbConditionInt("h.hostid", hostPrototypeSourceIds.valuesAsLong()),
					sqlParts.getNamedParams()
				), "hostid");
				for(Map hostPrototype : (CArray<Map>)Nest.value(data,"hostPrototypes").asCArray()) {
					if (!empty(Nest.value(hostPrototype,"templateid").$())) {
						Map sourceTemplate = hostPrototypeSourceTemplates.get(hostPrototypeSourceIds.get(hostPrototype.get("hostid")));
						Nest.value(hostPrototype,"sourceTemplate").$(map(
							"hostid", Nest.value(sourceTemplate,"parent_hostid").$(),
							"name", Nest.value(sourceTemplate,"name").$()
						));
						String sourceDiscoveryRuleId = get_realrule_by_itemid_and_hostid(executor,Nest.value(discoveryRule,"itemid").asString(), Nest.value(sourceTemplate,"hostid").asString());
						Nest.value(hostPrototype,"sourceDiscoveryRuleId").$(sourceDiscoveryRuleId);
					}
				}
			}

			// render view
			CView itemView = new CView("configuration.host.prototype.list", data);
			itemView.render(getIdentityBean(), executor);
			itemView.show();
		}
	}
}
