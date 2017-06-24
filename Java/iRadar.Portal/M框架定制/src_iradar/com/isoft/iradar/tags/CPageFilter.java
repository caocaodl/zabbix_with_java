package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_add;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.issets;
import static com.isoft.iradar.Cphp.key;
import static com.isoft.iradar.Cphp.natcasesort;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.RDA_DROPDOWN_FIRST_ALL;
import static com.isoft.iradar.inc.Defines.RDA_DROPDOWN_FIRST_NONE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CTenantGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObject;

/**
 * @property string tenantid
 * @property string groupid
 * @property string hostid
 * @property string triggerid
 * @property string graphid
 * @property string druleid
 * @property string severityMin
 * @property array  groups
 * @property array  hosts
 * @property array  graphs
 * @property array  triggers
 * @property array  drules
 * @property bool   groupsSelected
 * @property bool   groupsAll
 * @property bool   hostsSelected
 * @property bool   hostsAll
 * @property bool   graphsSelected
 * @property bool   triggersSelected
 * @property bool   drulesSelected
 * @property bool   drulesAll
 */
public class CPageFilter {
	
	public final static String KEY_GROUP_SHOW_TEMPLATE = "KEY_GROUP_SHOW_TEMPLATE";
	public final static String KEY_GROUP_SHOW_DISCOVERY = "KEY_GROUP_SHOW_DISCOVERY";
	public final static String KEY_GROUP_SHOW_ALL = "KEY_GROUP_SHOW_ALL";

	public final static String KEY_TENANT_SHOW_ALL = "KEY_TENANT_SHOW_ALL";
	
	/**
	 * Configuration options.
	 * @var array
	 */
	protected CArray config = map(
		// select the latest object viewed by the user on any page
		"select_latest", null,

		// reset the remembered values if the remember first dropdown entry function is disabled
		"DDReset", null,

		// if set to true selections will be remembered for each file separately,
		// if set to false - for each main menu section (monitoring, inventory, configuration etc.)
		"individual", null,

		// if set to true and the remembered object is missing from the selection, sets the filter to the first
		// available object. If set to false, the selection will remain empty.
		"popupDD", null,

		// Force the filter to select the given objects.
		"tenantid", null,
		// works only if the host given in "hostid" belongs to that group or "hostid" is not set
		"groupid", null,

		// works only if a host group is selected or the host group filter value is set to "all"
		"hostid", null,

		// works only if a host is selected or the host filter value is set to "all"
		"graphid", null,

		// works only if a specific host has been selected, will NOT work if the host filter is set to "all"
		"triggerid", null,
		"druleid", null,

		// API parameters to be used to retrieve filter objects
		"tenants", null,
		"groups", null,
		"hosts", null,
		"graphs", null,
		"triggers", null,
		"drules", null
	);

	/**
	 * Objects present in the filter.
	 * @var array
	 */
	protected CArray data = map(
		"tenants", null,
		"groups", null,
		"hosts", null,
		"graphs", null,
		"triggers", null,
		"drules", null
	);

	/**
	 * Selected objects IDs.
	 * @var array
	 */
	protected CArray ids = map(
		"tenantid", null,
		"groupid", null,
		"hostid", null,
		"triggerid", null,
		"graphid", null,
		"druleid", null,
		"severityMin", null
	);

	/**
	 * Contains information about the selected values.
	 * The "*Selected" value is set to true if a specific object is chosen or the corresponding filter is set to "All"
	 * and contains objects.
	 * The "*All" value is set to true if the corresponding filter is set to "All" and contains objects.
	 *
	 * @var array
	 */
	protected CArray isSelected = map(
		"tenantsSelected", false,
		"tenantsAll", false,
		"groupsSelected", null,
		"groupsAll", null,
		"hostsSelected", null,
		"hostsAll", null,
		"graphsSelected", null,
		"triggersSelected", null,
		"drulesSelected", null,
		"drulesAll", null
	);

	/**
	 * User profile keys to be used when remembering the selected values.
	 * @see the "individual" option for more info.
	 * @var array
	 */
	private CArray<String> _profileIdx = map(
		"tenantid", null,
		"groupid", null,
		"hostid", null,
		"triggerid", null,
		"graphid", null,
		"druleid", null,
		"severityMin", null
	);

	/**
	 * IDs of specific objects to be selected.
	 * @var array
	 */
	private CArray _profileIds = map(
		"tenantid", null,
		"groupid", null,
		"hostid", null,
		"triggerid", null,
		"graphid", null,
		"druleid", null,
		"severityMin", null
	);

	/**
	 * Request ids.
	 * @var array
	 */
	private CArray _requestIds = array();

	private String profileSection;
	
	/**
	 * Get value from data, ids or isSelected arrays.
	 * Search occurs in mentioned above order.
	 * @param string name
	 * @return mixed
	 */
	public TObject $(String name) {
		if (isset(data, name)) {
			return Nest.value(data, name);
		} else if (isset(ids, name)) {
			return Nest.value(ids, name);
		} else if (isset(isSelected, name)) {
			return Nest.value(isSelected, name);
		} else {
			if("tenantid".equals(name)){
				return Nest.value(data, name);
			} else {
				throw new IllegalArgumentException(_s("Try to read inaccessible property \"%s\".", this.getClass()+"."+name)+ "E_USER_WARNING");
			}
		}
	}
	
	public CPageFilter(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, array());
	}

	/**
	 * Initialize filter features.
	 * Supported: Host groups, Hosts, Triggers, Graphs, Applications, Discovery rules, Minimum trigger severities.
	 *
	 * @param array  options
	 * @param array  options["config"]
	 * @param bool   options["config"]["select_latest"]
	 * @param bool   options["config"]["popupDD"]
	 * @param bool   options["config"]["individual"]
	 * @param bool   options["config"]["allow_all"]
	 * @param bool   options["config"]["deny_all"]
	 * @param array  options["config"]["DDFirstLabels"]
	 * @param array  options["hosts"]
	 * @param string options["hostid"]
	 * @param array  options["groups"]
	 * @param string options["groupid"]
	 * @param array  options["graphs"]
	 * @param string options["graphid"]
	 * @param array  options["triggers"]
	 * @param string options["triggerid"]
	 * @param array  options["drules"]
	 * @param string options["druleid"]
	 * @param array  options["applications"]
	 * @param string options["application"]
	 * @param array  options["severitiesMin"]
	 * @param int      options["severitiesMin"]["default"]
	 * @param string options["severitiesMin"]["mapId"]
	 * @param string options["severityMin"]
	 */
	public CPageFilter(IIdentityBean idBean, SQLExecutor executor, CArray options) {
		Nest.value(config,"select_latest").$(isset(Nest.value(options,"config","select_latest").$()));
		Nest.value(config,"DDReset").$(get_request("ddreset", null));
		Nest.value(config,"popupDD").$(isset(Nest.value(options,"config","popupDD").$()));

		Map _config = select_config(idBean, executor);

		// individual remember selections per page (not for menu)
		Nest.value(config,"individual").$(true);
		if (isset(Nest.value(options,"config","individual").$()) && !is_null(Nest.value(options,"config","individual").$())) {
			Nest.value(config,"individual").$(true);
		}
		
		Map page = RadarContext.page();
		profileSection = Nest.value(config,"individual").asBoolean() ? Nest.value(page,"file").asString() : Nest.value(page,"menu").asString();

		// dropdown
		Nest.value(config,"DDRemember").$(Nest.value(_config,"dropdown_first_remember").$());
		if (isset(Nest.value(options,"config","allow_all").$())) {
			Nest.value(config,"DDFirst").$(RDA_DROPDOWN_FIRST_ALL);
		} else if (isset(Nest.value(options,"config","deny_all").$())) {
			Nest.value(config,"DDFirst").$(RDA_DROPDOWN_FIRST_NONE);
		} else {
			Nest.value(config,"DDFirst").$(Nest.value(_config,"dropdown_first_entry").$());
		}

		// profiles
		_getProfiles(idBean, executor, options);

		if (!issets(Nest.value(options,"groupid").$(), Nest.value(options,"hostid").$())) {
			if (isset(options,"graphid")) {
				_updateByGraph(idBean, executor, options);
			}
		}
		
		// tenants
		if (isset(options,"tenants")) {
			_initTenants(idBean, executor, Nest.value(options,"tenantid").asString(true),
					Nest.value(options,"tenants").asCArray(), 
					isset(options,"hostid") ? Nest.value(options,"hostid").asLong() : null);
		}

		// groups
		if (isset(options,"groups")) {
			_initGroups(idBean, executor, Nest.value(options,"groupid").asLong(true),
					Nest.value(options,"groups").asCArray(), 
					isset(options,"hostid") ? Nest.value(options,"hostid").asLong() : null);
		}

		// hosts
		if (isset(options,"hosts")) {
			_initHosts(idBean, executor, Nest.value(options,"hostid").asLong(true), Nest.value(options,"hosts").asCArray());
		}

		// graphs
		if (isset(options,"graphs")) {
			_initGraphs(idBean, executor, Nest.value(options,"graphid").asLong(true), Nest.value(options,"graphs").asCArray());
		}

		// triggers
		if (isset(options,"triggers")) {
			_initTriggers(idBean, executor, Nest.value(options,"triggerid").asLong(true), Nest.value(options,"triggers").asCArray());
		}

		// drules
		if (isset(options,"drules")) {
			_initDiscoveries(idBean, executor, Nest.value(options,"druleid").asLong(true), Nest.value(options,"drules").asCArray());
		}

		// applications
		if (isset(options,"applications")) {
			_initApplications(idBean, executor, Nest.value(options,"application").$(), Nest.value(options,"applications").asCArray());
		}

		// severities min
		if (isset(options,"severitiesMin")) {
			_initSeveritiesMin(idBean, executor, Nest.value(options,"severityMin").asString(), Nest.value(options,"severitiesMin").asCArray());
		}
	}
	
	/**
	 * Retrieve objects stored in the user profile.
	 * If the "select_latest" option is used, the IDs will be loaded from the web.latest.objectid profile values,
	 * otherwise - from the web.*.objectid field, depending on the use of the "individial" option.
	 * If the "DDReset" option is used, IDs will be reset to zeroes.
	 * The method also sets the scope for remembering the selected values, see the "individual" option for more info.
	 *
	 * @param array options
	 */
	private void _getProfiles(IIdentityBean idBean, SQLExecutor executor, CArray options) {
		Nest.value(_profileIdx,"tenants").$("web."+profileSection+".tenantid");
		Nest.value(_profileIdx,"groups").$("web."+profileSection+".groupid");
		Nest.value(_profileIdx,"hosts").$("web."+profileSection+".hostid");
		Nest.value(_profileIdx,"graphs").$("web."+profileSection+".graphid");
		Nest.value(_profileIdx,"triggers").$("web."+profileSection+".triggerid");
		Nest.value(_profileIdx,"drules").$("web."+profileSection+".druleid");
		Nest.value(_profileIdx,"application").$("web."+profileSection+".application");
		Nest.value(_profileIdx,"severityMin").$("web.maps.severity_min");

		if (Nest.value(config,"select_latest").asBoolean()) {
			Nest.value(_profileIds,"tenantid").$(CProfile.get(idBean, executor, "web."+profileSection+".tenantid"));
			Nest.value(_profileIds,"groupid").$(CProfile.get(idBean, executor, "web."+profileSection+".groupid"));
			Nest.value(_profileIds,"hostid").$(CProfile.get(idBean, executor, "web."+profileSection+".hostid"));
			Nest.value(_profileIds,"graphid").$(CProfile.get(idBean, executor, "web."+profileSection+".graphid"));
			Nest.value(_profileIds,"triggerid").$(null);
			Nest.value(_profileIds,"druleid").$(CProfile.get(idBean, executor, "web."+profileSection+".druleid"));
			Nest.value(_profileIds,"application").$("");
			Nest.value(_profileIds,"severityMin").$(null);
		} else if (Nest.value(config,"DDReset").asBoolean() && !Nest.value(config,"DDRemember").asBoolean()) {
			Nest.value(_profileIds,"tenantid").$("0");
			Nest.value(_profileIds,"groupid").$(0L);
			Nest.value(_profileIds,"hostid").$(0L);
			Nest.value(_profileIds,"graphid").$(0L);
			Nest.value(_profileIds,"triggerid").$(0L);
			Nest.value(_profileIds,"druleid").$(0L);
			Nest.value(_profileIds,"application").$("");
			Nest.value(_profileIds,"severityMin").$(null);
		} else {
			Nest.value(_profileIds,"tenantid").$(CProfile.get(idBean, executor, _profileIdx.get("tenants")));
			Nest.value(_profileIds,"groupid").$(CProfile.get(idBean, executor, _profileIdx.get("groups")));
			Nest.value(_profileIds,"hostid").$(CProfile.get(idBean, executor, _profileIdx.get("hosts")));
			Nest.value(_profileIds,"graphid").$(CProfile.get(idBean, executor, _profileIdx.get("graphs")));
			Nest.value(_profileIds,"triggerid").$(null);
			Nest.value(_profileIds,"druleid").$(CProfile.get(idBean, executor, _profileIdx.get("drules")));
			Nest.value(_profileIds,"application").$(CProfile.get(idBean, executor, _profileIdx.get("application")));

			// minimum severity
			Long mapId = (isset(Nest.value(options,"severitiesMin","mapId").$()) ? Nest.value(options,"severitiesMin","mapId").asLong() : null);
			Nest.value(_profileIds,"severityMin").$(CProfile.get(idBean, executor, _profileIdx.get("severityMin"), null, mapId));
		}

		Nest.value(_requestIds,"tenantid").$(isset(Nest.value(options,"tenantid").$()) ? Nest.value(options,"tenantid").$() : null);
		Nest.value(_requestIds,"groupid").$(isset(Nest.value(options,"groupid").$()) ? Nest.value(options,"groupid").$() : null);
		Nest.value(_requestIds,"hostid").$(isset(Nest.value(options,"hostid").$()) ? Nest.value(options,"hostid").$() : null);
		Nest.value(_requestIds,"graphid").$(isset(Nest.value(options,"graphid").$()) ? Nest.value(options,"graphid").$() : null);
		Nest.value(_requestIds,"triggerid").$(isset(Nest.value(options,"triggerid").$()) ? Nest.value(options,"triggerid").$() : null);
		Nest.value(_requestIds,"druleid").$(isset(Nest.value(options,"druleid").$()) ? Nest.value(options,"druleid").$() : null);
		Nest.value(_requestIds,"application").$(isset(Nest.value(options,"application").$()) ? Nest.value(options,"application").$() : null);
		Nest.value(_requestIds,"severityMin").$(isset(Nest.value(options,"severityMin").$()) ? Nest.value(options,"severityMin").$() : null);
	}

	private void _updateByGraph(IIdentityBean idBean, SQLExecutor executor, Map options) {
		CGraphGet goptions = new CGraphGet();
		goptions.setGraphIds(Nest.value(options,"graphid").asLong());
		goptions.setOutput(API_OUTPUT_EXTEND);
		goptions.setSelectHosts(API_OUTPUT_REFER);
		goptions.setSelectTemplates(API_OUTPUT_REFER);
		goptions.setSelectGroups(API_OUTPUT_REFER);
		CArray<Map> graphs = API.Graph(idBean, executor).get(goptions);

		Map graph = reset(graphs);
		if (!empty(graph)) {
			CArray<Map> groups = rda_toHash(Nest.value(graph,"groups").$(), "groupid");
			CArray<Map> hosts = rda_toHash(Nest.value(graph,"hosts").$(), "hostid");
			CArray<Map> templates = rda_toHash(Nest.value(graph,"templates").$(), "templateid");

			if (isset(groups,_profileIds.get("groupid"))) {
				Nest.value(options,"groupid").$(Nest.value(_profileIds,"groupid").$());
			} else {
				CArray groupids = array_keys(groups);
				Nest.value(options,"groupid").$(reset(groupids));
			}

			if (isset(hosts,_profileIds.get("hostid"))) {
				Nest.value(options,"hostid").$(Nest.value(_profileIds,"hostid").$());
			} else {
				CArray hostids = array_keys(hosts);
				Nest.value(options,"hostid").$(reset(hostids));
			}

			if (is_null(Nest.value(options,"hostid").$())) {
				if (isset(templates,_profileIds.get("hostid"))) {
					Nest.value(options,"hostid").$(Nest.value(_profileIds,"hostid").$());
				} else {
					CArray templateids = array_keys(templates);
					Nest.value(options,"hostid").$(reset(templateids));
				}
			}
		}
	}
	
	/**
	 * Load available tenants, choose the selected tenant and remember the selection.
	 * If the host given in the "hostid" option does not belong to the selected tenant, the selected tenant
	 * will be reset to empty.
	 *
	 * @param int   tenantid
	 * @param array options
	 * @param int  hostid
	 */
	private void _initTenants(IIdentityBean idBean, SQLExecutor executor, String tenantid, CArray options, Long hostid) {
		CArray def_options = map(
			"output", new String[]{"tenantid", "name"}
		);
		options = rda_array_merge(def_options, options);
		
		//增加组这块的显示
		boolean showAll = Nest.value(options, KEY_TENANT_SHOW_ALL).asBoolean();
		Nest.value(data,KEY_TENANT_SHOW_ALL).$(showAll);
		
		CTenantGet toptions = new CTenantGet();
		toptions.putAll(options);
		CArray<Map> tenants = API.Tenant(idBean, executor).get(toptions);
		
		order_result(tenants, "name");

		Nest.value(data,"tenants").$(array());
		for(Map tenant: tenants) {
			String id = Nest.value(tenant, "tenantid").asString();
			Nest.value(data,"tenants", id).$(tenant);
		}

		// select remebered selection
		if (is_null(tenantid) && !empty(Nest.value(_profileIds,"tenantid").$())) {
			CArray<Map> host = null;
			
			// set group only if host is in group or hostid is not set
			if (!empty(hostid)) {
				CHostGet hoptions = new CHostGet();
				hoptions.setOutput(new String[]{"hostid"});
				hoptions.setHostIds(hostid);
				hoptions.setGroupIds(Nest.array(_profileIds,"groupid").asLong());
				if(tenantid != null && tenantid.length()>1){
					hoptions.setFilter("tenantid",tenantid);
				}
				host = API.Host(idBean, executor).get(hoptions);
			}
			if (empty(hostid) || !empty(host)) {
				tenantid = Nest.value(_profileIds,"tenantid").asString();
			}
		}

		// nonexisting or unset _groupid
		if (is_null(tenantid) || (!isset(Nest.value(data,"tenants", tenantid).$()) && tenantid.length()>1)) {
			// for popup select first group in the list
			if (Nest.value(config,"popupDD").asBoolean() && !empty(Nest.value(data,"tenants").$())) {
				tenantid = key(Nest.value(data,"tenants").asCArray());
			}
			// otherwise groupid = 0 for "Dropdown first entry" option ALL or NONE
			else {
				tenantid = "0";
			}
		}

		CProfile.update(idBean, executor, _profileIdx.get("tenants"), tenantid, PROFILE_TYPE_ID);

		Nest.value(isSelected,"tenantsSelected").$((Nest.value(config,"DDFirst").asInteger()==RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"tenants").$())) || tenantid.length()>0);
		Nest.value(isSelected,"tenantsAll").$(Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"tenants").$()) && tenantid.length()==0);
		Nest.value(ids,"tenantid").$(tenantid);
	}

	/**
	 * Load available host groups, choose the selected host group and remember the selection.
	 * If the host given in the "hostid" option does not belong to the selected host group, the selected host group
	 * will be reset to 0.
	 *
	 * @param int   groupid
	 * @param array options
	 * @param int  hostid
	 */
	private void _initGroups(IIdentityBean idBean, SQLExecutor executor, Long groupid, CArray options, Long hostid) {
		CArray def_options = map(
			"output", new String[]{"groupid", "name"}
		);
		options = rda_array_merge(def_options, options);
		
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.putAll(options);
		hgoptions.setFilter("internal", 0);
		CArray<Map> groups = API.HostGroup(idBean, executor).get(hgoptions);
		order_result(groups, "name");

		Nest.value(data,"groups").$(array());
		for(Map group: groups) {
			Long id = Nest.value(group, "groupid").asLong();
			Nest.value(data,"groups", id).$(group);
		}

		// select remebered selection
		if (is_null(groupid) && !empty(Nest.value(_profileIds,"groupid").$())) {
			CArray<Map> host = null;
			
			// set group only if host is in group or hostid is not set
			if (!empty(hostid)) {
				CHostGet hoptions = new CHostGet();
				hoptions.setOutput(new String[]{"hostid"});
				hoptions.setHostIds(hostid);
				hoptions.setGroupIds(Nest.array(_profileIds,"groupid").asLong());
				host = API.Host(idBean, executor).get(hoptions);
			}
			if (empty(hostid) || !empty(host)) {
				groupid = Nest.value(_profileIds,"groupid").asLong();
			}
		}

		// nonexisting or unset _groupid
		if (is_null(groupid) || (!isset(Nest.value(data,"groups", groupid).$()) && groupid > 0L)) {
			// for popup select first group in the list
			if (Nest.value(config,"popupDD").asBoolean() && !empty(Nest.value(data,"groups").$())) {
				groupid = key(Nest.value(data,"groups").asCArray());
			}
			// otherwise groupid = 0 for "Dropdown first entry" option ALL or NONE
			else {
				groupid = 0L;
			}
		}

		CProfile.update(idBean, executor, _profileIdx.get("groups"), groupid, PROFILE_TYPE_ID);
		
		Nest.value(isSelected,"groupsSelected").$((Nest.value(config,"DDFirst").asInteger()==RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"groups").$())) || groupid > 0L);
		Nest.value(isSelected,"groupsAll").$(Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"groups").$()) && groupid == 0L);
		Nest.value(ids,"groupid").$(groupid);
	}

	/**
	 * Load available hosts, choose the selected host and remember the selection.
	 * If no host group is selected, reset the selected host to 0.
	 *
	 * @param int      hostId
	 * @param array  options
	 * @param string options["DDFirstLabel"]
	 */
	private void _initHosts(IIdentityBean idBean, SQLExecutor executor, Long hostId, CArray options) {
		Nest.value(data,"hosts").$(array());

		if (isset(options,"DDFirstLabel")) {
			Nest.value(config,"DDFirstLabels","hosts").$(Nest.value(options,"DDFirstLabel").$());
			unset(options,"DDFirstLabel");
		}

		if (!this.$("groupsSelected").asBoolean() && !this.$("tenantsSelected").asBoolean()) {
			hostId = 0L;
		} else {
			CArray defaultOptions = map(
				"output", new String[]{"hostid", "name", "status"},
				"editable", true, //加此条件，运营商只能看到运营商的设备
				"groupids", this.$("groupid").asLong() > 0L ? this.$("groupid").$() : null
			);
			
			CHostGet hostGet = new CHostGet();
			hostGet.putAll(rda_array_merge(defaultOptions, options));
			if(!empty(this.$("tenantid").asString()) && this.$("tenantid").asString().length()>1){
				hostGet.setFilter("tenantid",this.$("tenantid").asString());
			}
			CArray<Map> hosts = API.Host(idBean, executor).get(hostGet);

			if (!empty(hosts)) {
				order_result(hosts, "name");
				for(Map host: hosts) {
					data.put("hosts", host.get("hostid"), host);
				}
			}

			// select remebered selection
			if (is_null(hostId) && Nest.value(_profileIds,"hostid").asBoolean()) {
				hostId = Nest.value(_profileIds,"hostid").asLong();
			}

			// nonexisting or unset hostid
			if (is_null(hostId) || (!isset(data.getNested("hosts", hostId)) && hostId > 0L)) {
				// for popup select first host in the list
				if (Nest.value(config,"popupDD").asBoolean() && !empty(Nest.value(data,"hosts").$())) {
					hostId = key(Nest.value(data,"hosts").asCArray());
				}
				// otherwise hostid = 0 for "Dropdown first entry" option ALL or NONE
				else {
					hostId = 0L;
				}
			}
		}

		if (!is_null(Nest.value(_requestIds,"hostid").$())) {
			CProfile.update(idBean, executor, _profileIdx.get("hosts"), hostId, PROFILE_TYPE_ID);
		}

		Nest.value(isSelected,"hostsSelected").$(((Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"hosts").$())) || hostId > 0L));
		Nest.value(isSelected,"hostsAll").$((Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"hosts").$()) && hostId == 0L));
		Nest.value(ids,"hostid").$(hostId);
	}

	/**
	 * Load available graphs, choose the selected graph and remember the selection.
	 * If no host is selected, reset the selected graph to 0.
	 *
	 * @param int     graphid
	 * @param array options
	 */
	private void _initGraphs(IIdentityBean idBean, SQLExecutor executor, Long graphid, CArray options) {
		Nest.value(data,"graphs").$(array());

		if (!this.$("hostsSelected").asBoolean()) {
			graphid = 0L;
		} else {
			CArray def_options = map(
				"output", new String[]{"graphid", "name"},
				"groupids", (this.$("groupid").asLong() > 0L && this.$("hostid").asLong() == 0L) ? this.$("groupid").$() : null,
				"hostids", (this.$("hostid").asLong() > 0) ? this.$("hostid").$() : null,
				"expandName", true
			);
			options = rda_array_merge(def_options, options);
			CGraphGet goptions = new CGraphGet();
			goptions.putAll(options);
			CArray<Map> graphs = API.Graph(idBean, executor).get(goptions);
			order_result(graphs, "name");

			for(Map graph: graphs){
				data.put("graphs", graph.get("graphid"), graph);
			}

			// no graphid provided
			if (is_null(graphid)) {
				// if there is one saved in profile, let's take it from there
				graphid = is_null(Nest.value(_profileIds,"graphid").$()) ? 0L : Nest.value(_profileIds,"graphid").asLong();
			}

			// if there is no graph with given id in selected host
			if (graphid > 0L && !isset(data.getNested("graphs", graphid))) {
				// then let's take a look how the desired graph is named
				options = map(
					"output", new String[]{"name"},
					"graphids", new Long[]{graphid}
				);
				goptions = new CGraphGet();
				goptions.putAll(options);
				CArray<Map> selectedGraphInfos = API.Graph(idBean, executor).get(goptions);
				Map selectedGraphInfo = reset(selectedGraphInfos);
				graphid = 0L;

				// if there is a graph with the same name on new host, why not show it then?
				for(Entry<Object, Map> entry: ((CArray<Map>)data.get("graphs")).entrySet()) {
					Object gid = entry.getKey();
					Map graph = entry.getValue();
					if (Cphp.equals(Nest.value(graph,"name").$(), Nest.value(selectedGraphInfo,"name").$())) {
						graphid = Nest.as(gid).asLong();
						break;
					}
				}
			}
		}

		if (!is_null(Nest.value(_requestIds,"graphid").$())) {
			CProfile.update(idBean, executor, _profileIdx.get("graphs"), graphid, PROFILE_TYPE_ID);
		}
		Nest.value(isSelected,"graphsSelected").$(graphid > 0L);
		Nest.value(ids,"graphid").$(graphid);
	}

	/**
	 * Load available triggers, choose the selected trigger and remember the selection.
	 * If no host is elected, or the host selection is set to "All", reset the selected trigger to 0.
	 *
	 * @param int    triggerid
	 * @param array options
	 */
	private void _initTriggers(IIdentityBean idBean, SQLExecutor executor, Long triggerid, CArray options) {
		Nest.value(data,"triggers").$(array());

		if (!this.$("hostsSelected").asBoolean() || this.$("hostsAll").asBoolean()) {
			triggerid = 0L;
		} else {
			CArray def_options = map(
				"output", new String[]{"triggerid", "description"},
				"groupids", (this.$("groupid").asLong() > 0L && this.$("hostid").asLong() == 0L) ? this.$("groupid").$() : null,
				"hostids", (this.$("hostid").asLong() > 0L) ? this.$("hostid").$() : null
			);
			options = rda_array_merge(def_options, options);
			CTriggerGet toptions = new CTriggerGet();
			toptions.putAll(options);
			CArray<Map> triggers = API.Trigger(idBean, executor).get(toptions);
			order_result(triggers, "description");

			for(Map trigger: triggers) {
				data.put("triggers", trigger.get("triggerid"), trigger);
			}

			if (is_null(triggerid)) {
				triggerid = Nest.value(_profileIds,"triggerid").asLong();
			}
			triggerid = isset(Nest.value(data, "triggers", triggerid).$()) ? triggerid : 0L;
		}

		Nest.value(isSelected,"triggersSelected").$(triggerid > 0L);
		Nest.value(ids,"triggerid").$(triggerid);
	}

	/**
	 * Load the available network discovery rules, choose the selected rule and remember the selection.
	 *
	 * @param int     druleid
	 * @param array options
	 */
	private void _initDiscoveries(IIdentityBean idBean, SQLExecutor executor, Long druleid, CArray options) {
		CArray def_options = map(
			"output", API_OUTPUT_EXTEND
		);
		options = rda_array_merge(def_options, options);
		CDRuleGet droptions = new CDRuleGet();
		droptions.putAll(options);
		CArray<Map> drules = API.DRule(idBean, executor).get(droptions);
		order_result(drules, "name");

		Nest.value(data,"drules").$(array());
		for(Map drule: drules) {
			data.put("drules", drule.get("druleid"), drule);
		}

		if (is_null(druleid)) {
			druleid = Nest.value(_profileIds,"druleid").asLong();
		}

		if (is_null(druleid) || (!isset(data.getNested("drules", druleid)) && druleid > 0L)) {
			if (Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_NONE) {
				druleid = 0L;
			} else if (is_null(Nest.value(_requestIds,"druleid").$()) || Nest.value(_requestIds,"druleid").asLong() > 0L) {
				CArray<Long> druleids = array_keys(Nest.value(data,"drules").asCArray());
				druleid = empty(druleids) ? 0L : reset(druleids);
			}
		}

		CProfile.update(idBean, executor, _profileIdx.get("drules"), druleid, PROFILE_TYPE_ID);

		Nest.value(isSelected,"drulesSelected").$((Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"drules").$())) || druleid > 0L);
		Nest.value(isSelected,"drulesAll").$(Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"drules").$()) && druleid == 0L);
		Nest.value(ids,"druleid").$(druleid);
	}

	/**
	 * Set applications related variables.
	 *  - applications: all applications available for dropdown on page
	 *  - application: application curently selected, can be "" for "all" or "not selected"
	 *  - applicationsSelected: if an application selected, i.e. not "not selected"
	 * Applications are dependent on groups.
	 *
	 * @param int    application
	 * @param array options
	 */
	private void _initApplications(IIdentityBean idBean, SQLExecutor executor, Object application, CArray options) {
		Nest.value(data,"applications").$(array());

		if (!this.$("groupsSelected").asBoolean()) {
			application = "";
		} else {
			CArray def_options = map(
				"output", new String[]{"name"},
				"groupids", (this.$("groupid").asLong() > 0L) ? this.$("groupid").asLong() : (Long[])null
			);
			options = rda_array_merge(def_options, options);
			
			CAppGet appget = new CAppGet();
			appget.putAll(options);
			CArray<Map> applications = API.Application(idBean, executor).get(appget);

			for(Map app: applications) {
				data.put("applications", app.get("name"), app);
			}

			// select remembered selection
			if (is_null(application) && Nest.value(_profileIds,"application").asBoolean()) {
				application = Nest.value(_profileIds,"application").$();
			}

			// nonexisting or unset application
			if (is_null(application) || (!isset(data.getNested("applications", application)) && !"".equals(application))) {
				application = "";
			}
		}

		if (!is_null(Nest.value(_requestIds,"application").$())) {
			CProfile.update(idBean, executor, _profileIdx.get("application"), application, PROFILE_TYPE_STR);
		}
		Nest.value(isSelected,"applicationsSelected").$((Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"applications").$())) || !"".equals(application));
		Nest.value(isSelected,"applicationsAll").$(Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"applications").$()) && "".equals(application));
		Nest.value(ids,"application").$(application);
	}

	/**
	 * Initialize minimum trigger severities.
	 *
	 * @param string severityMin
	 */
	public void _initSeveritiesMin(IIdentityBean idBean, SQLExecutor executor, String severityMin) {
		_initSeveritiesMin(idBean, executor, severityMin, array());
	}

	/**
	 * Initialize minimum trigger severities.
	 *
	 * @param string severityMin
	 * @param array  options
	 * @param int      options["default"]
	 * @param string options["mapId"]
	 */
	private void _initSeveritiesMin(IIdentityBean idBean, SQLExecutor executor, String severityMin, CArray options) {
		int defval = isset(options,"default") ? Nest.value(options,"default").asInteger() : TRIGGER_SEVERITY_NOT_CLASSIFIED;
		long mapId = isset(options,"mapId") ? Nest.value(options,"mapId").asLong() : 0L;
		String severityMinProfile = isset(_profileIds,"severityMin") ? Nest.value(_profileIds,"severityMin").asString() : null;

		if (severityMin == null && severityMinProfile != null) {
			severityMin = severityMinProfile;
		}

		if (severityMin != null) {
			if (Cphp.equals(severityMin, defval)) {
				CProfile.delete(idBean, executor, _profileIdx.get("severityMin"), mapId);
			} else {
				CProfile.update(idBean, executor, _profileIdx.get("severityMin"), severityMin, PROFILE_TYPE_INT, mapId);
			}
		}

		Nest.value(data,"severitiesMin").$(getSeverityCaption(idBean, executor));
		data.put("severitiesMin", defval, data.getNested("severitiesMin", defval)+SPACE+"("+_("default")+")");
		Nest.value(ids,"severityMin").$((severityMin == null) ? defval : severityMin);
	}
	
	/**
	 * Get tenants combobox with selected item.
	 * @return CComboBox
	 */
	public CComboBox getTenantsCB() {
		CArray items = array();
		for(Entry<Object, Map> entry: ((CArray<Map>)this.$("tenants").$s()).entrySet()) {
			Object id = entry.getKey();
			Map tenant = entry.getValue();			
			items.put(id, Nest.value(tenant,"name").$());
		}
		return _getCB("tenantid", this.$("tenantid").asString(), items, map("objectName", "tenants"));
	}
	
	/**
	 * Get hosts combobox with selected item.
	 * @return CComboBox
	 */
	public CComboBox getHostsCB() {
		CArray items = array(); 
		CArray classes = array();
		for(Entry<Object, Map> entry: ((CArray<Map>)this.$("hosts").$s()).entrySet()) {
			Object id = entry.getKey();
			Map host = entry.getValue();
			items.put(id, Nest.value(host,"name").$());
			classes.put(id, (Nest.value(host,"status").asInteger() == HOST_STATUS_NOT_MONITORED) ? "not-monitored" : null);
		}
		CArray options = map("objectName", "hosts", "classes", classes);

		return _getCB("hostid", this.$("hostid").asString(), items, options);
	}

	/**
	 * Get host groups combobox with selected item.
	 * @return CComboBox
	 */
	public CComboBox getGroupsCB() {
		CArray items = array();
		Long groupid = this.$("groupid").asLong();
		if(IMonGroup.systemGroups().containsKey(groupid)) {
			CArray groups = this.$("groups").$s();
			CArray group = Nest.value(groups, groupid).asCArray();
			items.put(groupid, Nest.value(group,"name").$());
		}
		
		for(Entry<Object, Map> entry: ((CArray<Map>)this.$("groups").$s()).entrySet()) {
			Object id = entry.getKey();
			Map group = entry.getValue();
			items.put(id, Nest.value(group,"name").$());
		}
		return _getCB("groupid", this.$("groupid").asString(), items, map("objectName", "groups"));
	}

	/**
	 * Get graphs combobox with selected item.
	 * @return CComboBox
	 */
	public CComboBox getGraphsCB() {
		CArray<Object> graphs = Clone.deepcopy(this.$("graphs").$s(true));
		natcasesort(graphs);
		graphs = array_add(
				map(0, map(
							"graphid",0,
							"name", _("not selected"))
				),graphs);

		CComboBox graphComboBox = new CComboBox("graphid", this.$("graphid").asString(), "javascript: submit();");
		for(Entry<Object, Map> entry: ((Map<Object,Map>)(Map)graphs).entrySet()) {
			Object id = entry.getKey();
			String name = Nest.value(entry.getValue(),"name").asString();
			graphComboBox.addItem(id, name);
		}

		return graphComboBox;
	}

	/**
	 * Get discovery rules combobox with selected item.
	 * @return CComboBox
	 */
	public CComboBox getDiscoveryCB() {
		CArray items = array();
		for(Entry<Object, Map> entry: ((CArray<Map>)this.$("drules").$s()).entrySet()) {
			Object id = entry.getKey();
			Map drule = entry.getValue();
			items.put(id, Nest.value(drule,"name").$());
		}
		return _getCB("druleid", this.$("druleid").asString(), items, map("objectName", "discovery"));
	}

	/**
	 * Get applications combobox with selected item.
	 * @return CComboBox
	 */
	public CComboBox getApplicationsCB() {
		CArray items = array();
		for(Entry<Object, Map> entry: ((CArray<Map>)this.$("applications").$s()).entrySet()) {
			Object id = entry.getKey();
			Map application = entry.getValue();
			items.put(id, Nest.value(application,"name").$());
		}
		return _getCB("application", this.$("application").asString(), items, map(
			"objectName", "applications"
		));
	}
	
	/**
	 * Get minimum trigger severities combobox with selected item.
	 *
	 * @return CComboBox
	 */
	public CComboBox getSeveritiesMinCB() {
		return new CComboBox("severity_min", this.$("severityMin").asString(), "javascript: submit();", this.$("severitiesMin").asCArray());
	}
	
	/**
	 * Create combobox with available data.
	 * Preselect active item. Display nodes. Add addition "not selected" or "all" item to top adjusted by configuration.
	 *
	 * @param string name
	 * @param string selectedId
	 * @param array  items
	 * @param bool   withNode
	 * @return CComboBox
	 */
	public CComboBox getCB(String name, String selectedId, CArray items) {
		return _getCB(name, selectedId, items, array());
	}

	/**
	 * Create combobox with available data.
	 * Preselect active item. Add addition "not selected" or "all" item to top adjusted by configuration.
	 *
	 * @param string name
	 * @param string selectedId
	 * @param array  items
	 * @param int      allValue
	 * @param array  options
	 * @param string options["objectName"]
	 * @param array  options["classes"]	array of class names for the combobox options with item IDs as keys
	 *
	 * @return CComboBox
	 */
	private CComboBox _getCB(String name, String selectedId, CArray<String> items, CArray options) {
		CComboBox comboBox = new CComboBox(name, selectedId, "javascript: submit();");

		natcasesort(items);

		// add drop down first item
		Object firstLabel = null;
		if (!Nest.value(config,"popupDD").asBoolean()) {
			if (isset(config.getNested("DDFirstLabels", options.get("objectName")))) {
				firstLabel = config.getNested("DDFirstLabels", options.get("objectName"));
			} else {
				firstLabel = (Nest.value(config,"DDFirst").asInteger() == RDA_DROPDOWN_FIRST_NONE) ? _("not selected") : _("all");
			}

			if ("application".equals(name)) {
				items = array_add(map("", firstLabel), items);
			} else {
				items = array_add(array(firstLabel), items);
			}
		}

		for(Entry<Object, String> entry: items.entrySet()) {
			Object id = entry.getKey();
			String value = entry.getValue();
			comboBox.addItem(id, value, null, true, isset(options.getNested("classes",id)) ? (String)options.getNested("classes",id) : null);
		}
		comboBox.addClass("autocomplete");
		return comboBox;
	}

}
