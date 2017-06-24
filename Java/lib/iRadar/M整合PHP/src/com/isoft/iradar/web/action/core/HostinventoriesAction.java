package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.FuncsUtil.rda_strpos;
import static com.isoft.iradar.inc.FuncsUtil.rda_strtolower;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostinventoriesAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Host inventory"));
		page("file", "hostinventories.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
			"hostid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
			// filter
			"filter_set",				array(T_RDA_STR, O_OPT,	P_SYS,	null,		null),
			"filter_field",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_field_value",	array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_exact",        	array(T_RDA_INT, O_OPT, null,	"IN(0,1)",	null),
			//ajax
			"favobj",					array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",					array(T_RDA_STR, O_OPT, P_ACT,  NOT_EMPTY,	"isset({favobj})"),
			"favstate",				array(T_RDA_INT, O_OPT, P_ACT,  NOT_EMPTY,	"isset({favobj})&&(\"filter\"=={favobj})")
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		
		if (hasRequest("favobj")) {
			if("filter".equals(Nest.value(_REQUEST,"favobj").asString())){
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter.state", Nest.as(get_request("favstate")).asString(), PROFILE_TYPE_INT);
			}
		}

		if ((PAGE_TYPE_JS == Nest.value(page,"type").asInteger()) || (PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger())) {
			return false;
		}
		
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		Long hostid = get_request("hostid", 0L);
		CArray data = array();
		
		/* Display */
		if (hostid > 0) {
			// host scripts
			Nest.value(data,"hostScripts").$(API.Script(getIdentityBean(), executor).getScriptsByHosts(hostid));
			
			// inventory info
			Nest.value(data,"tableTitles").$(getHostInventories());
			Nest.value(data,"tableTitles").$(rda_toHash(Nest.value(data,"tableTitles").$(), "db_field"));
			CArray inventoryFields = array_keys(Nest.value(data,"tableTitles").asCArray());
			
			// overview tab
			CHostGet params = new CHostGet();
			params.setHostIds(hostid);
			params.setOutput(new String[]{"hostid", "host", "name", "maintenance_status"});
			params.setSelectInterfaces(API_OUTPUT_EXTEND);
			params.setSelectItems(API_OUTPUT_COUNT);
			params.setSelectTriggers(API_OUTPUT_COUNT);
			params.setSelectScreens(API_OUTPUT_COUNT);
			params.setSelectInventory(inventoryFields.valuesAsString());
			params.setSelectGraphs(API_OUTPUT_COUNT);
			params.setSelectApplications(API_OUTPUT_COUNT);
			params.setSelectDiscoveries(API_OUTPUT_COUNT);
			params.setSelectHttpTests(API_OUTPUT_COUNT);
			params.setPreserveKeys(true);
			Nest.value(data,"host").$(reset((CArray<Map>)API.Host(getIdentityBean(), executor).get(params)));
			unset(data,"host","inventory","hostid");
			
			// resolve macros
			Nest.value(data,"host","interfaces").$(CMacrosResolverHelper.resolveHostInterfaces(this.getIdentityBean(), executor, Nest.value(data,"host","interfaces").asCArray().entryValueFromMap2CArray()));
			
			// get permissions
			int userType = CWebUser.getType();
			if (userType == USER_TYPE_SUPER_ADMIN) {
				Nest.value(data,"rwHost").$(true);
			} else if (userType == USER_TYPE_IRADAR_ADMIN) {
				params = new CHostGet();
				params.setHostIds(hostid);
				params.setEditable(true);
				CArray<Map> rwHost = API.Host(getIdentityBean(), executor).get(params);
				Nest.value(data,"rwHost").$( (!empty(rwHost)) ? true : false);
			} else {
				Nest.value(data,"rwHost").$(false);
			}

			// view generation
			CView hostinventoriesView = new CView("inventory.host.view", data);
			hostinventoriesView.render(getIdentityBean(), executor);
			hostinventoriesView.show();
		} else {
			Nest.value(data,"config").$(select_config(getIdentityBean(), executor));
			CArray params = map(
				"groups", map(
					"real_hosts", 1
				),
				"groupid", get_request("groupid", null)
			);
			CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, params);
			Nest.value(data,"pageFilter").$(pageFilter);
			
			// host inventory filter
			if (hasRequest("filter_set")) {
				Nest.value(data,"filterField").$(get_request("filter_field"));
				Nest.value(data,"filterFieldValue").$(get_request("filter_field_value"));
				Nest.value(data,"filterExact").$(get_request("filter_exact"));
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter_field", Nest.value(data,"filterField").$(), PROFILE_TYPE_STR);
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter_field_value", Nest.value(data,"filterFieldValue").$(), PROFILE_TYPE_STR);
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter_exact", Nest.value(data,"filterExact").$(), PROFILE_TYPE_INT);
			} else {
				Nest.value(data,"filterField").$(CProfile.get(getIdentityBean(), executor, "web.hostinventories.filter_field"));
				Nest.value(data,"filterFieldValue").$(CProfile.get(getIdentityBean(), executor, "web.hostinventories.filter_field_value"));
				Nest.value(data,"filterExact").$(CProfile.get(getIdentityBean(), executor, "web.hostinventories.filter_exact"));
			}
			
			Nest.value(data,"hosts").$(array());
			
			if (!empty(pageFilter.$("groupsSelected").$())) {
				// which inventory fields we will need for displaying
				CArray requiredInventoryFields = array(
					"name",
					"type",
					"os",
					"serialno_a",
					"tag",
					"macaddress_a"
				);
				
				// checking if correct inventory field is specified for filter
				CArray<Map> possibleInventoryFields = getHostInventories();
				possibleInventoryFields = rda_toHash(possibleInventoryFields, "db_field");
				if (!empty(Nest.value(data,"filterField").$())
						&& !empty(Nest.value(data,"filterFieldValue").$())
						&& !isset(possibleInventoryFields.get(data.get("filterField")))) {
					error(_s("Impossible to filter by inventory field \"%s\", which does not exist.", Nest.value(data,"filterField").$()));
				} else {
					// if we are filtering by field, this field is also required
					if (!empty(Nest.value(data,"filterField").$()) && !empty(Nest.value(data,"filterFieldValue").$())) {
						requiredInventoryFields.add(Nest.value(data,"filterField").$());
					}
					
					CHostGet options = new CHostGet();
					options.setOutput(new String[]{"hostid", "name", "status"});
					options.setSelectInventory(requiredInventoryFields.valuesAsString());
					options.setWithInventory(true);
					options.setSelectGroups(API_OUTPUT_EXTEND);
					options.setLimit((Nest.value(data,"config","search_limit").asInteger() + 1));
					if (pageFilter.$("groupid").asInteger() > 0) {
						options.setGroupIds(pageFilter.$("groupid").asLong());
					}
					
					CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(options);
					Nest.value(data,"hosts").$(hosts);
					
					// copy some inventory fields to the uppers array level for sorting
					// and filter out hosts if we are using filter
					CArray<Object> waitClearIds = new CArray();
		            for (Entry<Object, Map> e : hosts.entrySet()) {
		                Object num = e.getKey();
		                Map host = e.getValue();

		                Nest.value(data,"hosts",num,"pr_name").$(Nest.value(host,"inventory","name").$());
		                Nest.value(data,"hosts",num,"pr_type").$(Nest.value(host,"inventory","type").$());
						Nest.value(data,"hosts",num,"pr_os").$(Nest.value(host,"inventory","os").$());
		                Nest.value(data,"hosts",num,"pr_serialno_a").$(Nest.value(host,"inventory","serialno_a").$());
		                Nest.value(data,"hosts",num,"pr_tag").$(Nest.value(host,"inventory","tag").$());
		                Nest.value(data,"hosts",num,"pr_macaddress_a").$(Nest.value(host,"inventory","macaddress_a").$());
						// if we are filtering by inventory field
						if(!empty(Nest.value(data,"filterField").$()) && !empty(Nest.value(data,"filterFieldValue").$())) {
							// must we filter exactly or using a substring (both are case insensitive)
							String filterField = Nest.value(hosts, num, "inventory",data.get("filterField")).asString();
							String filterFieldValue = Nest.value(data,"filterFieldValue").asString();
							Object filterExact = Nest.value(data,"filterExact").$();
							boolean match = (!empty(filterExact))
								? rda_strtolower(filterField).equals(rda_strtolower(filterFieldValue))
								: rda_strpos(rda_strtolower(filterField),rda_strtolower(filterFieldValue)) >-1;
							if (!match) {
								waitClearIds.add(num);
							}
						}
					}
		            
					for (Object num : waitClearIds) {
						hosts.remove(num);
					}
					
					order_result(hosts, getPageSortField(getIdentityBean(), executor,"name"), getPageSortOrder(getIdentityBean(), executor));
				}
			}
			
			Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor, Nest.value(data,"hosts").asCArray()));

			CView hostinventoriesView = new CView("inventory.host.list", data);
			hostinventoriesView.render(getIdentityBean(), executor);
			hostinventoriesView.show();
		}
	}

}
