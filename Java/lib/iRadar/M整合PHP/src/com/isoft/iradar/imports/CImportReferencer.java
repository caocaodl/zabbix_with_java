package com.isoft.iradar.imports;

import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CIconMapGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class that handles associations for zabbix elements unique fields and their database ids.
 * The purpose is to gather all elements that need ids from database and resolve them with one query.
 */
public class CImportReferencer {
	
	/**
	 * @var array with references to interfaceid (hostid -> reference_name -> interfaceid)
	 */
	public Map<Long, Map<String, Long>> interfacesCache = array();
	protected Map<String, String> processedHosts = array();
	protected CArray<String> groups = array();
	protected CArray<String> templates = array();
	protected CArray<String> hosts = array();
	protected Map<String, CArray<String>> applications = array();
	protected Map<String, CArray<String>> items = array();
	protected CArray<String> valueMaps = array();
	protected Map<String, CArray<String>> triggers = array();
	protected CArray<String> iconMaps = array();
	protected CArray<String> maps = array();
	protected CArray<String> screens = array();
	protected Map<String, CArray<String>> macros = array();
	protected CArray<String> proxies = array();
	protected Map<String, Map<String, CArray<String>>> hostPrototypes = array();
	protected Map<String, Long> groupsRefs;
	protected Map<String, Long> templatesRefs;
	protected Map<String, Long> hostsRefs;
	protected Map<Long, Map<String, Long>> applicationsRefs;
	protected Map<Long, Map<String, Long>> itemsRefs;
	protected Map<String, Long> valueMapsRefs;
	protected Map<String, Map<String, Long>> triggersRefs;
	protected Map<String, Long> iconMapsRefs;
	protected Map<String, Long> mapsRefs;
	protected Map<String, Long> screensRefs;
	protected Map<Long, Map<String, Long>> macrosRefs;
	protected Map<String, Long> proxiesRefs;
	protected Map<Long, Map<Long, Map<String, Long>>> hostPrototypeRefs;
	
	private SQLExecutor executor;
	
	public SQLExecutor getExecutor() {
		return executor;
	}

	/**
	 * Add host/template that has been updated or created, i.e. all items, discovery rules, etc,
	 * related to these hosts/templates should be created or updated too.
	 *
	 * @param host
	 */
	public void addProcessedHost(String host) {
		Nest.value(this.processedHosts, host).$(host);
	}

	/**
	 * Checks if host/template has been created or updated during the current import.
	 *
	 * @param host
	  * @return boolean
	 */
	public boolean isProcessedHost(String host) {
		return isset(this.processedHosts, host);
	}

	/**
	 * Get group id by name.
	 *
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveGroup(IIdentityBean idBean, String name) {
		if (this.groupsRefs == null) {
			selectGroups(idBean);
		}
		return isset(this.groupsRefs, name) ? this.groupsRefs.get(name) : null;
	}

	/**
	 * Get host id by host.
	 *
	 * @param String host
	 *
	 * @return Long
	 */
	public Long resolveHost(IIdentityBean idBean, String host) {
		if (this.hostsRefs == null) {
			selectHosts(idBean);
		}
		return isset(this.hostsRefs, host) ? this.hostsRefs.get(host) : null;
	}

	/**
	 * Get template id by host.
	 *
	 * @param String host
	 *
	 * @return Long
	 */
	public Long resolveTemplate(IIdentityBean idBean, String host) {
		if (this.templatesRefs == null) {
			selectTemplates(idBean);
		}
		return isset(this.templatesRefs, host) ? this.templatesRefs.get(host) : null;
	}

	/**
	 * Get host or template id by host.
	 *
	 * @param String host
	 *
	 * @return Long
	 */
	public Long resolveHostOrTemplate(IIdentityBean idBean, String host) {
		if (this.templatesRefs == null) {
			selectTemplates(idBean);
		}
		if (this.hostsRefs == null) {
			selectHosts(idBean);
		}
		if (isset(this.templatesRefs, host)) {
			return this.templatesRefs.get(host);
		} else if (isset(this.hostsRefs, host)) {
			return this.hostsRefs.get(host);
		} else {
			return null;
		}
	}

	/**
	 * Get application id by host id and application name.
	 *
	 * @param Long hostid
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveApplication(IIdentityBean idBean, Long hostid, String name) {
		if (this.applicationsRefs == null) {
			selectApplications(idBean);
		}
		return isset(Nest.value(this.applicationsRefs, hostid, name).$()) ? Nest.value(this.applicationsRefs, hostid, name).asLong() : null;
	}

	/**
	 * Get item id by host id and item key_.
	 *
	 * @param Long hostid
	 * @param String key
	 *
	 * @return Long
	 */
	public Long resolveItem(IIdentityBean idBean, Long hostid, String key) {
		if (this.itemsRefs == null) {
			selectItems(idBean);
		}
		return isset(Nest.value(this.itemsRefs, hostid, key).$()) ? Nest.value(this.itemsRefs, hostid, key).asLong() : null;
	}

	/**
	 * Get value map id by vale map name.
	 *
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveValueMap(String name) {
		if (this.valueMapsRefs == null) {
			selectValueMaps();
		}
		return isset(this.valueMapsRefs, name) ? this.valueMapsRefs.get(name) : null;
	}

	/**
	 * Get trigger id by trigger name and expression.
	 *
	 * @param string name
	 * @param string expression
	 *
	 * @return string|bool
	 */
	public Long resolveTrigger(IIdentityBean idBean, String name, String expression) {
		if (this.triggersRefs == null) {
			selectTriggers(idBean);
		}
		return isset(Nest.value(this.triggersRefs, name, expression).$()) ? Nest.value(this.triggersRefs, name, expression).asLong() : null;
	}

	/**
	 * Get icon map id by name.
	 *
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveIconMap(IIdentityBean idBean, String name) {
		if (this.iconMapsRefs == null) {
			selectIconMaps(idBean);
		}
		return isset(this.iconMapsRefs, name) ? this.iconMapsRefs.get(name) : null;
	}

	/**
	 * Get map id by name.
	 *
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveMap(IIdentityBean idBean, String name) {
		if (this.mapsRefs == null) {
			selectMaps(idBean);
		}
		return isset(this.mapsRefs, name) ? this.mapsRefs.get(name) : null;
	}

	/**
	 * Get screen id by name.
	 *
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveScreen(String name) {
		if (this.screensRefs == null) {
			selectScreens();
		}
		return isset(this.screensRefs, name) ? this.screensRefs.get(name) : null;
	}

	/**
	 * Get macro id by host id and macro name.
	 *
	 * @param Long hostid
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveMacro(IIdentityBean idBean, Long hostid, String name) {
		if (this.macrosRefs == null) {
			selectMacros(idBean);
		}
		return isset(Nest.value(this.macrosRefs, hostid, name).$()) ? Nest.value(this.macrosRefs, hostid, name).asLong() : null;
	}

	/**
	 * Get proxy id by name.
	 *
	 * @param String name
	 *
	 * @return Long
	 */
	public Long resolveProxy(IIdentityBean idBean, String name) {
		if (this.proxiesRefs == null) {
			selectProxyes(idBean);
		}
		return isset(this.proxiesRefs, name) ? this.proxiesRefs.get(name) : null;
	}

	/**
	 * Get proxy id by name.
	 *
	 * @param Long hostId
	 * @param Long discoveryRuleId
	 * @param string hostPrototype
	 *
	 * @return Long
	 */
	public Long resolveHostPrototype(IIdentityBean idBean, Long hostId, Long discoveryRuleId, String hostPrototype) {
		if (this.hostPrototypeRefs == null) {
			selectHostPrototypes(idBean);
		}
		if (isset(Nest.value(this.hostPrototypeRefs, hostId, discoveryRuleId, hostPrototype).$())) {
			return Nest.value(this.hostPrototypeRefs, hostId, discoveryRuleId, hostPrototype).asLong();
		} else {
			return null;
		}
	}

	/**
	 * Add group names that need association with a database group id.
	 *
	 * @param array groups
	 */
	public void addGroups(CArray<String> groups) {
		this.groups = array_unique(array_merge(this.groups, groups));
	}

	/**
	 * Add group name association with group id.
	 *
	 * @param String name
	 * @param Long id
	 */
	public void addGroupRef(String name, Long id) {
		Nest.value(this.groupsRefs, name).$(id);
	}

	/**
	 * Add templates names that need association with a database template id.
	 *
	 * @param array templates
	 */
	public void addTemplates(CArray<String> templates) {
		this.templates = array_unique(array_merge(this.templates, templates));
	}

	/**
	 * Add template name association with template id.
	 *
	 * @param String name
	 * @param Long id
	 */
	public void addTemplateRef(String name, Long id) {
		Nest.value(this.templatesRefs, name).$(id);
	}

	/**
	 * Add hosts names that need association with a database host id.
	 *
	 * @param array hosts
	 */
	public void addHosts(CArray<String> hosts) {
		this.hosts = array_unique(array_merge(this.hosts, hosts));
	}

	/**
	 * Add host name association with host id.
	 *
	 * @param String host
	 * @param Long id
	 */
	public void addHostRef(String host, Long id) {
		Nest.value(this.hostsRefs, host).$(id);
	}

	/**
	 * Add application names that need association with a database application id.
	 * Input array has format:
	 * array("hostname1" => array("appname1", "appname2"), "hostname2" => array("appname1"), ...)
	 *
	 * @param array applications
	 */
	public void addApplications(Map<String, CArray<String>> applications) {
		for (Entry<String, CArray<String>> e : applications.entrySet()) {
			String host = e.getKey();
			CArray<String> apps = e.getValue();
			if (!isset(this.applications, host)) {
				Nest.value(this.applications, host).$(array());
			}
			Nest.value(this.applications, host).$(array_unique(array_merge(this.applications.get(host), apps)));
		}
	}

	/**
	 * Add application name association with application id.
	 *
	 * @param Long hostId
	 * @param String name
	 * @param Long appId
	 */
	public void addApplicationRef(Long hostId, String name, Long appId) {
		Nest.value(this.applicationsRefs, hostId, name).$(appId);
	}

	/**
	 * Add item keys that need association with a database item id.
	 * Input array has format:
	 * array("hostname1" => array("itemkey1", "itemkey2"), "hostname2" => array("itemkey1"), ...)
	 *
	 * @param array items
	 */
	public void addItems(Map<String, CArray<String>> items) {
		for (Entry<String, CArray<String>> e : items.entrySet()) {
		    String host = e.getKey();
			CArray<String> keys = e.getValue();
			if (!isset(this.items, host)) {
				Nest.value(this.items, host).$(array());
			}
			Nest.value(this.items,host).$(array_unique(array_merge(this.items.get(host), keys)));
		}
	}

	/**
	 * Add item key association with item id.
	 *
	 * @param Long hostId
	 * @param String key
	 * @param Long itemId
	 */
	public void addItemRef(Long hostId, String key, Long itemId) {
		Nest.value(this.itemsRefs, hostId, key).$(itemId);
	}

	/**
	 * Add value map names that need association with a database value map id.
	 *
	 * @param array valueMaps
	 */
	public void addValueMaps(CArray<String> valueMaps) {
		this.valueMaps = array_unique(array_merge(this.valueMaps, valueMaps));
	}

	/**
	 * Add trigger names/expressions that need association with a database trigger id.
	 * Input array has format:
	 * array("triggername1" => array("expr1", "expr2"), "triggername2" => array("expr1"), ...)
	 *
	 * @param array triggers
	 */
	public void addTriggers(Map<String, CArray<String>> triggers) {
		for (Entry<String, CArray<String>> e : triggers.entrySet()) {
			String name = e.getKey();
			CArray<String> expressions = e.getValue();
			if (!isset(this.triggers, name)) {
				Nest.value(this.triggers, name).$(array());
			}
			Nest.value(this.triggers,name).$(array_unique(array_merge(this.triggers.get(name), expressions)));
		}
	}

	/**
	 * Add trigger name/expression association with trigger id.
	 *
	 * @param String name
	 * @param String expression
	 * @param Long triggerId
	 */
	public void addTriggerRef(String name, String expression, Long triggerId) {
		Nest.value(this.triggersRefs, name, expression).$(triggerId);
	}

	/**
	 * Add icon map names that need association with a database icon map id.
	 *
	 * @param array iconMaps
	 */
	public void addIconMaps(CArray<String> iconMaps) {
		this.iconMaps = array_unique(array_merge(this.iconMaps, iconMaps));
	}

	/**
	 * Add map names that need association with a database map id.
	 *
	 * @param array maps
	 */
	public void addMaps(CArray<String> maps) {
		this.maps = array_unique(array_merge(this.maps, maps));
	}

	/**
	 * Add map name association with map id.
	 *
	 * @param String name
	 * @param Long mapId
	 */
	public void addMapRef(String name, Long mapId) {
		Nest.value(this.mapsRefs, name).$(mapId);
	}

	/**
	 * Add screens names that need association with a database screen id.
	 *
	 * @param array screens
	 */
	public void addScreens(CArray<String> screens) {
		this.screens = array_unique(array_merge(this.screens, screens));
	}

	/**
	 * Add screen name association with screen id.
	 *
	 * @param String name
	 * @param Long screenId
	 */
	public void addScreenRef(String name, Long screenId) {
		Nest.value(this.screensRefs, name).$(screenId);
	}

	/**
	 * Add macros names that need association with a database macro id.
	 *
	 * @param array macros
	 */
	public void addMacros(Map<String, CArray<String>> macros) {
		for (Entry<String, CArray<String>> e : macros.entrySet()) {
			String host = e.getKey();
			CArray<String> ms = e.getValue();
			if (!isset(this.macros, host)) {
				Nest.value(this.macros, host).$(array());
			}
			Nest.value(this.macros, host).$(array_unique(array_merge(this.macros.get(host), ms)));
		}
	}

	/**
	 * Add macro name association with macro id.
	 *
	 * @param Long hostId
	 * @param String macro
	 * @param Long macroId
	 */
	public void addMacroRef(Long hostId, String macro, Long macroId) {
		Nest.value(this.macrosRefs, hostId, macro).$(macroId);
	}

	/**
	 * Add proxy names that need association with a database proxy id.
	 *
	 * @param array proxies
	 */
	public void addProxies(CArray<String> proxies) {
		this.proxies = array_unique(array_merge(this.proxies, proxies));
	}

	/**
	 * Add proxy name association with proxy id.
	 *
	 * @param String name
	 * @param Long proxyId
	 */
	public void addProxyRef(String name, Long proxyId) {
		Nest.value(this.proxiesRefs, name).$(proxyId);
	}

	/**
	 * Add host prototypes that need association with a database host prototype id.
	 *
	 * @param array hostPrototypes
	 */
	public void addHostPrototypes(Map<String, Map<String, CArray<String>>> hostPrototypes) {
		for (Entry<String, Map<String, CArray<String>>> e : hostPrototypes.entrySet()) {
			String host = e.getKey();
		    Map<String, CArray<String>> discoveryRule = e.getValue();
			if (!isset(this.hostPrototypes, host)) {
				Nest.value(this.hostPrototypes,host).$(array());
			}
			for (Entry<String, CArray<String>> es : discoveryRule.entrySet()) {
				String discoveryRuleKey = es.getKey();
			    CArray<String> chostPrototypes = es.getValue();
				if (!isset(Nest.value(this.hostPrototypes,host,discoveryRuleKey).$())) {
					Nest.value(this.hostPrototypes,host,discoveryRuleKey).$(array());
				}
				Nest.value(this.hostPrototypes,host,discoveryRuleKey).$(array_unique(
					array_merge(Nest.value(this.hostPrototypes,host,discoveryRuleKey).asCArray(), chostPrototypes)
				));
			}
		}
	}

	/**
	 * Add host prototype host association with host id.
	 *
	 * @param String host
	 * @param Long hostPrototypeId
	 */
	@Deprecated
	public void addHostPrototypeRef(String host, Long hostPrototypeId) {
		Nest.value(this.hostPrototypes, host).$(hostPrototypeId);
	}

	/**
	 * Select group ids for previously added group names.
	 */
	protected void selectGroups(IIdentityBean idBean) {
		if (!empty(this.groups)) {
			this.groupsRefs = array();
			CHostGroupGet options = new CHostGroupGet();
			options.setFilter("name", this.groups.valuesAsString());
			options.setOutput(new String[]{"groupid", "name"});
			options.setPreserveKeys(true);
			options.setEditable(true);
			CArray<Map> dbGroups = API.HostGroup(idBean, this.executor).get(options);
			for(Map group : dbGroups) {
				Nest.value(this.groupsRefs,group.get("name")).$(Nest.value(group,"groupid").asLong());
			}
			this.groups = array();
		}
	}

	/**
	 * Select template ids for previously added template names.
	 */
	protected void selectTemplates(IIdentityBean idBean) {
		if (!empty(this.templates)) {
			this.templatesRefs = array();
			CTemplateGet options = new CTemplateGet();
			options.setFilter("host", this.templates.valuesAsString());
			options.setOutput(new String[]{"hostid", "host"});
			options.setPreserveKeys(true);
			options.setEditable(true);
			CArray<Map> dbTemplates = API.Template(idBean, this.executor).get(options);
			for(Map template : dbTemplates) {
				Nest.value(this.templatesRefs,template.get("host")).$(Nest.value(template,"templateid").asLong());
			}
			this.templates = array();
		}
	}

	/**
	 * Select host ids for previously added host names.
	 */
	protected void selectHosts(IIdentityBean idBean) {
		if (!empty(this.hosts)) {
			this.hostsRefs = array();
			// fetch only normal hosts, discovered hosts must not be imported
			CHostGet options = new CHostGet();
			options.setFilter("host", this.hosts.valuesAsString());
			options.setFilter("flags", RDA_FLAG_DISCOVERY_NORMAL);
			options.setOutput(new String[]{"hostid", "host"});
			options.setPreserveKeys(true);
			options.setTemplatedHosts(true);
			options.setEditable(true);
			CArray<Map> dbHosts = API.Host(idBean, this.executor).get(options);
			for(Map host : dbHosts) {
				Nest.value(this.hostsRefs,host.get("host")).$(Nest.value(host,"hostid").asLong());
			}
			this.hosts = array();
		}
	}

	/**
	 * Select application ids for previously added application names.
	 */
	protected void selectApplications(IIdentityBean idBean) {
		if (!empty(this.applications)) {
			this.applicationsRefs = array();
			CArray sqlWhere = array();
			SqlBuilder sqlParts = new SqlBuilder();
			for (Entry<String, CArray<String>> e : this.applications.entrySet()) {
			    String host = e.getKey();
			    CArray<String> applications = e.getValue();
				Long hostId = resolveHostOrTemplate(idBean, host);
				if (!empty(hostId)) {
					sqlWhere.add("(hostid="+sqlParts.marshalParam(hostId)+" AND "+sqlParts.dual.dbConditionString("name", applications.valuesAsString())+")");
				}
			}

			if (!empty(sqlWhere)) {
				CArray<Map> dbApplications = DBselect(this.executor,
						"SELECT applicationid,hostid,name FROM applications WHERE "+implode(" OR ", sqlWhere),
						sqlParts.getNamedParams());
				for(Map dbApplication : dbApplications) {
					Nest.value(this.applicationsRefs,dbApplication.get("hostid"),dbApplication.get("name")).$(Nest.value(dbApplication,"applicationid").asLong());
				}
			}
		}
	}

	/**
	 * Unset application refs to make referencer select them from db again.
	 */
	public void refreshApplications() {
		this.applicationsRefs = null;
	}

	/**
	 * Select item ids for previously added item keys.
	 */
	protected void selectItems(IIdentityBean idBean) {
		if (!empty(this.items)) {
			this.itemsRefs = array();
			CArray sqlWhere = array();
			SqlBuilder sqlParts = new SqlBuilder();
			for (Entry<String, CArray<String>> e : this.items.entrySet()) {
			    String host = e.getKey();
			    CArray<String> keys = e.getValue();
				Long hostId = resolveHostOrTemplate(idBean, host);
				if (!empty(hostId)) {
					sqlWhere.add("(i.hostid="+sqlParts.marshalParam(hostId)+" AND "+sqlParts.dual.dbConditionString("i.key_", keys.valuesAsString())+")");
				}
			}

			if (!empty(sqlWhere)) {
				CArray<Map> dbitems = DBselect(this.executor,
						"SELECT i.itemid,i.hostid,i.key_ FROM items i WHERE "+implode(" OR ", sqlWhere),
						sqlParts.getNamedParams());
				for(Map dbItem : dbitems) {
					Nest.value(this.itemsRefs,dbItem.get("hostid"),dbItem.get("key_")).$(Nest.value(dbItem,"itemid").asLong());
				}
			}
		}
	}

	/**
	 * Unset item refs to make referencer select them from db again.
	 */
	public void refreshItems() {
		this.itemsRefs = null;
	}

	/**
	 * Select value map ids for previously added value map names.
	 */
	protected void selectValueMaps() {
		if (!empty(this.valueMaps)) {
			this.valueMapsRefs = array();
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbitems = DBselect(this.executor,
					"SELECT v.name,v.valuemapid FROM valuemaps v WHERE "+sqlParts.dual.dbConditionString("v.name", this.valueMaps.valuesAsString()));
			for (Map dbItem : dbitems) {
				Nest.value(this.valueMapsRefs,dbItem.get("name")).$(Nest.value(dbItem,"valuemapid").asLong());
			}
			this.valueMaps = array();
		}
	}

	/**
	 * Select trigger ids for previously added trigger names/expressions.
	 */
	protected void selectTriggers(IIdentityBean idBean) {
		if (!empty(this.triggers)) {
			this.triggersRefs = array();

			CArray triggerIds = array();
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT t.triggerid,t.expression,t.description"+
				" FROM triggers t"+
				" WHERE "+sqlParts.dual.dbConditionString("t.description", array_keys((CArray)this.triggers).valuesAsString());
			CArray<Map> dbTriggers = DBselect(this.executor, sql, sqlParts.getNamedParams());
			for(Map dbTrigger : dbTriggers) {
				String dbExpr = explode_exp(idBean, this.executor, Nest.value(dbTrigger,"expression").asString()).toString();
				for (Entry<String, CArray<String>> e : this.triggers.entrySet()) {
				    String name = e.getKey();
				    CArray<String> expressions = e.getValue();
					if (name.equals(Nest.value(dbTrigger,"description").asString())) {
						for(String expression : expressions) {
							if (expression.equals(dbExpr)) {
								Nest.value(this.triggersRefs,name,expression).$(Nest.value(dbTrigger,"triggerid").asLong());
								triggerIds.add(Nest.value(dbTrigger,"triggerid").asString());
							}
						}
					}
				}
			}

			CTriggerGet options = new CTriggerGet();
			options.setTriggerIds(triggerIds.valuesAsLong());
			options.setOutput(new String[]{"triggerid"});
			options.setFilter("flags", RDA_FLAG_DISCOVERY_NORMAL, 
												 RDA_FLAG_DISCOVERY_PROTOTYPE, 
												 RDA_FLAG_DISCOVERY_CREATED);
			options.setPreserveKeys(true);
			options.setEditable(true);
			CArray<Map> allowedTriggers = API.Trigger(idBean, this.executor).get(options);
			for (Entry<String, Map<String, Long>> e : this.triggersRefs.entrySet()) {
				String name = e.getKey();
				Map<String, Long> expressions = e.getValue();
				for (Entry<String, Long> es : expressions.entrySet()) {
					String expression = es.getKey();
					Long triggerId = es.getValue();
					if (!isset(allowedTriggers, triggerId)) {
						unset(this.triggersRefs.get(name), expression);
					}
				}
			}
		}
	}

	/**
	 * Unset trigger refs to make referencer select them from db again.
	 */
	public void refreshTriggers() {
		this.triggersRefs = null;
	}

	/**
	 * Select icon map ids for previously added icon maps names.
	 */
	protected void selectIconMaps(IIdentityBean idBean) {
		if (!empty(this.iconMaps)) {
			this.iconMapsRefs = array();
			CIconMapGet options = new CIconMapGet();
			options.setFilter("name", this.iconMaps.valuesAsString());
			options.setOutput(new String[] { "iconmapid", "name" });
			options.setPreserveKeys(true);
			CArray<Map> dbIconMaps = API.IconMap(idBean, this.executor).get(options);
			for (Map iconMap : dbIconMaps) {
				Nest.value(this.iconMapsRefs,iconMap.get("name")).$(Nest.value(iconMap,"iconmapid").asLong());
			}
			this.iconMaps = array();
		}
	}

	/**
	 * Select map ids for previously added maps names.
	 */
	protected void selectMaps(IIdentityBean idBean) {
		if (!empty(this.maps)) {
			this.mapsRefs = array();
			CMapGet options = new CMapGet();
			options.setFilter("name", this.maps.valuesAsString());
			options.setOutput(new String[] { "sysmapid", "name" });
			options.setPreserveKeys(true);
			CArray<Map> dbMaps = API.Map(idBean, this.executor).get(options);
			for(Map dbMap : dbMaps) {
				Nest.value(this.mapsRefs,dbMap.get("name")).$(Nest.value(dbMap,"sysmapid").asLong());
			}
			this.maps = array();
		}
	}

	/**
	 * Select screen ids for previously added screen names.
	 */
	protected void selectScreens() {
		if (!empty(this.screens)) {
			this.screensRefs = array();
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbScreens = DBselect(this.executor,
					"SELECT s.screenid,s.name FROM screens s WHERE"+
					" s.templateid IS NULL "+
					" AND "+sqlParts.dual.dbConditionString("s.name", this.screens.valuesAsString()),
					sqlParts.getNamedParams()
			);
			for(Map dbScreen : dbScreens) {
				Nest.value(this.screensRefs,dbScreen.get("name")).$(Nest.value(dbScreen,"screenid").asLong());
			}
			this.screens = array();
		}
	}

	/**
	 * Select macro ids for previously added macro names.
	 */
	protected void selectMacros(IIdentityBean idBean) {
		if (!empty(this.macros)) {
			this.macrosRefs = array();
			CArray sqlWhere = array();
			SqlBuilder sqlParts = new SqlBuilder();
			for (Entry<String, CArray<String>> e : this.macros.entrySet()) {
				String host = e.getKey();
			    CArray<String> macros = e.getValue();
				Long hostId = resolveHostOrTemplate(idBean, host);
				if (!empty(hostId)) {
					sqlWhere.add("(hm.hostid="+sqlParts.marshalParam(hostId)+" AND "+sqlParts.dual.dbConditionString("hm.macro", macros.valuesAsString())+")");
				}
			}
			if (!empty(sqlWhere)) {
				CArray<Map> dbMacros = DBselect(this.executor,
						"SELECT hm.hostmacroid,hm.hostid,hm.macro FROM hostmacro hm WHERE "+implode(" OR ", sqlWhere),
						sqlParts.getNamedParams()
				);
				for(Map dbMacro : dbMacros) {
					Nest.value(this.macrosRefs,dbMacro.get("hostid"),dbMacro.get("macro")).$(Nest.value(dbMacro,"hostmacroid").asLong());
				}
			}
			this.macros = array();
		}
	}

	/**
	 * Select proxy ids for previously added proxy names.
	 */
	protected void selectProxyes(IIdentityBean idBean) {
		if (!empty(this.proxies)) {
			this.proxiesRefs = array();
			CProxyGet options = new CProxyGet();
			options.setFilter("name", this.proxies.valuesAsString());
			options.setOutput(new String[] { "hostid", "host" });
			options.setPreserveKeys(true);
			options.setEditable(true);
			CArray<Map> dbProxy = API.Proxy(idBean, this.executor).get(options);
			for(Map proxy : dbProxy) {
				Nest.value(this.proxiesRefs,proxy.get("host")).$(Nest.value(proxy,"proxyid").asLong());
			}
			this.proxies = array();
		}
	}

	/**
	 * Select host prototype ids for previously added host prototypes names.
	 */
	protected void selectHostPrototypes(IIdentityBean idBean) {
		if (!empty(this.hostPrototypes)) {
			this.hostPrototypeRefs = array();
			CArray sqlWhere = array();
			SqlBuilder sqlParts = new SqlBuilder();
			for (Entry<String, Map<String, CArray<String>>> e : this.hostPrototypes.entrySet()) {
				String host = e.getKey();
				Map<String, CArray<String>> discoveryRule = e.getValue();
				Long hostId = resolveHostOrTemplate(idBean, host);

				for (Entry<String, CArray<String>> es : discoveryRule.entrySet()) {
					String discoveryRuleKey = es.getKey();
				    CArray<String> hostPrototypes = es.getValue();
					Long discoveryRuleId = resolveItem(idBean, hostId, discoveryRuleKey);
					if (!empty(hostId)) {
						sqlWhere.add("(hd.parent_itemid="+sqlParts.marshalParam(discoveryRuleId)+" AND "+sqlParts.dual.dbConditionString("h.host", hostPrototypes.valuesAsString())+")");
					}
				}
			}

			if (!empty(sqlWhere)) {
				CArray<Map >query = DBselect(this.executor,
					"SELECT h.host,h.hostid,hd.parent_itemid,i.hostid AS parent_hostid "+
					" FROM hosts h,host_discovery hd,items i"+
					" WHERE h.hostid=hd.hostid"+
						" AND hd.parent_itemid=i.itemid"+
						" AND "+implode(" OR ", sqlWhere),
					sqlParts.getNamedParams()
				);
				for(Map data : query) {
					Nest.value(this.hostPrototypeRefs,data.get("parent_hostid"),data.get("parent_itemid"),data.get("host")).$(Nest.value(data,"hostid").asLong());
				}
			}
		}
	}
	
}
