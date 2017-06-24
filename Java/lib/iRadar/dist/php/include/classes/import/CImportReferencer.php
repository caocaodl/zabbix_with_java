<?php
/*
** Zabbix
** Copyright (C) 2001-2014 Zabbix SIA
**
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software
** Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
**/


/**
 * Class that handles associations for zabbix elements unique fields and their database ids.
 * The purpose is to gather all elements that need ids from database and resolve them with one query.
 */
class CImportReferencer {

	/**
	 * @var array with references to interfaceid (hostid -> reference_name -> interfaceid)
	 */
	public $interfacesCache = CArray.array();
	protected $processedHosts = CArray.array();
	protected $groups = CArray.array();
	protected $templates = CArray.array();
	protected $hosts = CArray.array();
	protected $applications = CArray.array();
	protected $items = CArray.array();
	protected $valueMaps = CArray.array();
	protected $triggers = CArray.array();
	protected $iconMaps = CArray.array();
	protected $maps = CArray.array();
	protected $screens = CArray.array();
	protected $macros = CArray.array();
	protected $proxies = CArray.array();
	protected $hostPrototypes = CArray.array();
	protected $groupsRefs;
	protected $templatesRefs;
	protected $hostsRefs;
	protected $applicationsRefs;
	protected $itemsRefs;
	protected $valueMapsRefs;
	protected $triggersRefs;
	protected $iconMapsRefs;
	protected $mapsRefs;
	protected $screensRefs;
	protected $macrosRefs;
	protected $proxiesRefs;
	protected $hostPrototypeRefs;


	/**
	 * Add host/template that has been updated or created, i.e. all items, discovery rules, etc,
	 * related to these hosts/templates should be created or updated too.
	 *
	 * @param $host
	 */
	public function addProcessedHost($host) {
		processedHosts[$host] = $host;
	}

	/**
	 * Checks if host/template has been created or updated during the current import.
	 *
	 * @param $host
	 *
	 * @return bool
	 */
	public function isProcessedHost($host) {
		return isset(processedHosts[$host]);
	}

	/**
	 * Get group id by name.
	 *
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveGroup($name) {
		if (groupsRefs === null) {
			selectGroups();
		}

		return isset(groupsRefs[$name]) ? groupsRefs[$name] : false;
	}

	/**
	 * Get host id by host.
	 *
	 * @param string $host
	 *
	 * @return string|bool
	 */
	public function resolveHost($host) {
		if (hostsRefs === null) {
			selectHosts();
		}

		return isset(hostsRefs[$host]) ? hostsRefs[$host] : false;
	}

	/**
	 * Get template id by host.
	 *
	 * @param string $host
	 *
	 * @return string|bool
	 */
	public function resolveTemplate($host) {
		if (templatesRefs === null) {
			selectTemplates();
		}
		return isset(templatesRefs[$host]) ? templatesRefs[$host] : false;
	}

	/**
	 * Get host or template id by host.
	 *
	 * @param string $host
	 *
	 * @return string|bool
	 */
	public function resolveHostOrTemplate($host) {
		if (templatesRefs === null) {
			selectTemplates();
		}
		if (hostsRefs === null) {
			selectHosts();
		}

		if (isset(templatesRefs[$host])) {
			return templatesRefs[$host];
		}
		elseif (isset(hostsRefs[$host])) {
			return hostsRefs[$host];
		}
		else {
			return false;
		}
	}

	/**
	 * Get application id by host id and application name.
	 *
	 * @param string $hostid
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveApplication($hostid, $name) {
		if (applicationsRefs === null) {
			selectApplications();
		}

		return isset(applicationsRefs[$hostid][$name]) ? applicationsRefs[$hostid][$name] : false;
	}

	/**
	 * Get item id by host id and item key_.
	 *
	 * @param string $hostid
	 * @param string $key
	 *
	 * @return string|bool
	 */
	public function resolveItem($hostid, $key) {
		if (itemsRefs === null) {
			selectItems();
		}

		return isset(itemsRefs[$hostid][$key]) ? itemsRefs[$hostid][$key] : false;
	}

	/**
	 * Get value map id by vale map name.
	 *
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveValueMap($name) {
		if (valueMapsRefs === null) {
			selectValueMaps();
		}

		return isset(valueMapsRefs[$name]) ? valueMapsRefs[$name] : false;
	}

	/**
	 * Get trigger id by trigger name and expression.
	 *
	 * @param string $name
	 * @param string $expression
	 *
	 * @return string|bool
	 */
	public function resolveTrigger($name, $expression) {
		if (triggersRefs === null) {
			selectTriggers();
		}

		return isset(triggersRefs[$name][$expression]) ? triggersRefs[$name][$expression] : false;
	}

	/**
	 * Get icon map id by name.
	 *
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveIconMap($name) {
		if (iconMapsRefs === null) {
			selectIconMaps();
		}

		return isset(iconMapsRefs[$name]) ? iconMapsRefs[$name] : false;
	}

	/**
	 * Get map id by name.
	 *
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveMap($name) {
		if (mapsRefs === null) {
			selectMaps();
		}

		return isset(mapsRefs[$name]) ? mapsRefs[$name] : false;
	}

	/**
	 * Get screen id by name.
	 *
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveScreen($name) {
		if (screensRefs === null) {
			selectScreens();
		}

		return isset(screensRefs[$name]) ? screensRefs[$name] : false;
	}

	/**
	 * Get macro id by host id and macro name.
	 *
	 * @param string $hostid
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveMacro($hostid, $name) {
		if (macrosRefs === null) {
			selectMacros();
		}

		return isset(macrosRefs[$hostid][$name]) ? macrosRefs[$hostid][$name] : false;
	}

	/**
	 * Get proxy id by name.
	 *
	 * @param string $name
	 *
	 * @return string|bool
	 */
	public function resolveProxy($name) {
		if (proxiesRefs === null) {
			selectProxyes();
		}

		return isset(proxiesRefs[$name]) ? proxiesRefs[$name] : false;
	}

	/**
	 * Get proxy id by name.
	 *
	 * @param string $hostId
	 * @param string $discoveryRuleId
	 * @param string $hostPrototype
	 *
	 * @return string|bool
	 */
	public function resolveHostPrototype($hostId, $discoveryRuleId, $hostPrototype) {
		if (hostPrototypeRefs === null) {
			selectHostPrototypes();
		}

		if (isset(hostPrototypeRefs[$hostId][$discoveryRuleId][$hostPrototype])) {
			return hostPrototypeRefs[$hostId][$discoveryRuleId][$hostPrototype];
		}
		else {
			return false;
		}
	}

	/**
	 * Add group names that need association with a database group id.
	 *
	 * @param array $groups
	 */
	public function addGroups(array $groups) {
		groups = array_unique(array_merge(groups, $groups));
	}

	/**
	 * Add group name association with group id.
	 *
	 * @param string $name
	 * @param string $id
	 */
	public function addGroupRef($name, $id) {
		groupsRefs[$name] = $id;
	}

	/**
	 * Add templates names that need association with a database template id.
	 *
	 * @param array $templates
	 */
	public function addTemplates(array $templates) {
		templates = array_unique(array_merge(templates, $templates));
	}

	/**
	 * Add template name association with template id.
	 *
	 * @param string $name
	 * @param string $id
	 */
	public function addTemplateRef($name, $id) {
		templatesRefs[$name] = $id;
	}

	/**
	 * Add hosts names that need association with a database host id.
	 *
	 * @param array $hosts
	 */
	public function addHosts(array $hosts) {
		hosts = array_unique(array_merge(hosts, $hosts));
	}

	/**
	 * Add host name association with host id.
	 *
	 * @param string $host
	 * @param string $id
	 */
	public function addHostRef($host, $id) {
		hostsRefs[$host] = $id;
	}

	/**
	 * Add application names that need association with a database application id.
	 * Input array has format:
	 * CArray.array("hostname1" => CArray.array("appname1", "appname2"), "hostname2" => CArray.array("appname1"), ...)
	 *
	 * @param array $applications
	 */
	public function addApplications(array $applications) {
		for($applications as $host => $apps) {
			if (!isset(applications[$host])) {
				applications[$host] = CArray.array();
			}
			applications[$host] = array_unique(array_merge(applications[$host], $apps));
		}
	}

	/**
	 * Add application name association with application id.
	 *
	 * @param string $hostId
	 * @param string $name
	 * @param string $appId
	 */
	public function addApplicationRef($hostId, $name, $appId) {
		applicationsRefs[$hostId][$name] = $appId;
	}

	/**
	 * Add item keys that need association with a database item id.
	 * Input array has format:
	 * CArray.array("hostname1" => CArray.array("itemkey1", "itemkey2"), "hostname2" => CArray.array("itemkey1"), ...)
	 *
	 * @param array $items
	 */
	public function addItems(array $items) {
		for($items as $host => $keys) {
			if (!isset(items[$host])) {
				items[$host] = CArray.array();
			}
			items[$host] = array_unique(array_merge(items[$host], $keys));
		}
	}

	/**
	 * Add item key association with item id.
	 *
	 * @param string $hostId
	 * @param string $key
	 * @param string $itemId
	 */
	public function addItemRef($hostId, $key, $itemId) {
		itemsRefs[$hostId][$key] = $itemId;
	}

	/**
	 * Add value map names that need association with a database value map id.
	 *
	 * @param array $valueMaps
	 */
	public function addValueMaps(array $valueMaps) {
		valueMaps = array_unique(array_merge(valueMaps, $valueMaps));
	}

	/**
	 * Add trigger names/expressions that need association with a database trigger id.
	 * Input array has format:
	 * CArray.array("triggername1" => CArray.array("expr1", "expr2"), "triggername2" => CArray.array("expr1"), ...)
	 *
	 * @param array $triggers
	 */
	public function addTriggers(array $triggers) {
		for($triggers as $name => $expressions) {
			if (!isset(triggers[$name])) {
				triggers[$name] = CArray.array();
			}
			triggers[$name] = array_unique(array_merge(triggers[$name], $expressions));
		}
	}

	/**
	 * Add trigger name/expression association with trigger id.
	 *
	 * @param string $name
	 * @param string $expression
	 * @param string $triggerId
	 */
	public function addTriggerRef($name, $expression, $triggerId) {
		triggersRefs[$name][$expression] = $triggerId;
	}

	/**
	 * Add icon map names that need association with a database icon map id.
	 *
	 * @param array $iconMaps
	 */
	public function addIconMaps(array $iconMaps) {
		iconMaps = array_unique(array_merge(iconMaps, $iconMaps));
	}

	/**
	 * Add map names that need association with a database map id.
	 *
	 * @param array $maps
	 */
	public function addMaps(array $maps) {
		maps = array_unique(array_merge(maps, $maps));
	}

	/**
	 * Add map name association with map id.
	 *
	 * @param string $name
	 * @param string $mapId
	 */
	public function addMapRef($name, $mapId) {
		mapsRefs[$name] = $mapId;
	}

	/**
	 * Add screens names that need association with a database screen id.
	 *
	 * @param array $screens
	 */
	public function addScreens(array $screens) {
		screens = array_unique(array_merge(screens, $screens));
	}

	/**
	 * Add screen name association with screen id.
	 *
	 * @param string $name
	 * @param string $screenId
	 */
	public function addScreenRef($name, $screenId) {
		screensRefs[$name] = $screenId;
	}

	/**
	 * Add macros names that need association with a database macro id.
	 *
	 * @param array $macros
	 */
	public function addMacros(array $macros) {
		for($macros as $host => $ms) {
			if (!isset(macros[$host])) {
				macros[$host] = CArray.array();
			}
			macros[$host] = array_unique(array_merge(macros[$host], $ms));
		}
	}

	/**
	 * Add macro name association with macro id.
	 *
	 * @param string $hostId
	 * @param string $macro
	 * @param string $macroId
	 */
	public function addMacroRef($hostId, $macro, $macroId) {
		macrosRefs[$hostId][$macro] = $macroId;
	}

	/**
	 * Add proxy names that need association with a database proxy id.
	 *
	 * @param array $proxies
	 */
	public function addProxies(array $proxies) {
		proxies = array_unique(array_merge(proxies, $proxies));
	}

	/**
	 * Add proxy name association with proxy id.
	 *
	 * @param string $name
	 * @param string $proxyId
	 */
	public function addProxyRef($name, $proxyId) {
		proxiesRefs[$name] = $proxyId;
	}

	/**
	 * Add host prototypes that need association with a database host prototype id.
	 *
	 * @param array $hostPrototypes
	 */
	public function addHostPrototypes(array $hostPrototypes) {
		for($hostPrototypes as $host => $discoveryRule) {
			if (!isset(hostPrototypes[$host])) {
				hostPrototypes[$host] = CArray.array();
			}
			for($discoveryRule as $discoveryRuleKey => $hostPrototypes) {
				if (!isset(hostPrototypes[$host][$discoveryRuleKey])) {
					hostPrototypes[$host][$discoveryRuleKey] = CArray.array();
				}
				hostPrototypes[$host][$discoveryRuleKey] = array_unique(
					array_merge(hostPrototypes[$host][$discoveryRuleKey], $hostPrototypes)
				);
			}
		}
	}

	/**
	 * Add host prototype host association with host id.
	 *
	 * @param string $host
	 * @param string $hostPrototypeId
	 */
	public function addHostPrototypeRef($host, $hostPrototypeId) {
		hostPrototypes[$host] = $hostPrototypeId;
	}

	/**
	 * Select group ids for previously added group names.
	 */
	protected function selectGroups() {
		if (!empty(groups)) {
			groupsRefs = CArray.array();
			$dbGroups = API.HostGroup().get(CArray.array(
				"filter" => CArray.array("name" => groups),
				"output" => CArray.array("groupid", "name"),
				"preservekeys" => true,
				"editable" => true
			));
			for($dbGroups as $group) {
				groupsRefs[$group["name"]] = Nest.value($group,"groupid").$();
			}

			groups = CArray.array();
		}
	}

	/**
	 * Select template ids for previously added template names.
	 */
	protected function selectTemplates() {
		if (!empty(templates)) {
			templatesRefs = CArray.array();
			$dbTemplates = API.Template().get(CArray.array(
				"filter" => CArray.array("host" => templates),
				"output" => CArray.array("hostid", "host"),
				"preservekeys" => true,
				"editable" => true
			));
			for($dbTemplates as $template) {
				templatesRefs[$template["host"]] = Nest.value($template,"templateid").$();
			}

			templates = CArray.array();
		}
	}

	/**
	 * Select host ids for previously added host names.
	 */
	protected function selectHosts() {
		if (!empty(hosts)) {
			hostsRefs = CArray.array();
			// fetch only normal hosts, discovered hosts must not be imported
			$dbHosts = API.Host().get(CArray.array(
				"filter" => CArray.array("host" => hosts, "flags" => ZBX_FLAG_DISCOVERY_NORMAL),
				"output" => CArray.array("hostid", "host"),
				"preservekeys" => true,
				"templated_hosts" => true,
				"editable" => true
			));
			for($dbHosts as $host) {
				hostsRefs[$host["host"]] = Nest.value($host,"hostid").$();
			}

			hosts = CArray.array();
		}
	}

	/**
	 * Select application ids for previously added application names.
	 */
	protected function selectApplications() {
		if (!empty(applications)) {
			applicationsRefs = CArray.array();
			$sqlWhere = CArray.array();
			for(applications as $host => $applications) {
				$hostId = resolveHostOrTemplate($host);
				if ($hostId) {
					$sqlWhere[] = "(hostid=".zbx_dbstr($hostId)." AND ".dbConditionString("name", $applications).")";
				}
			}

			if ($sqlWhere) {
				$dbApplications = DBselect("SELECT applicationid,hostid,name FROM applications WHERE ".implode(" OR ", $sqlWhere));
				while ($dbApplication = DBfetch($dbApplications)) {
					applicationsRefs[$dbApplication["hostid"]][$dbApplication["name"]] = Nest.value($dbApplication,"applicationid").$();
				}
			}
		}
	}

	/**
	 * Unset application refs to make referencer select them from db again.
	 */
	public function refreshApplications() {
		applicationsRefs = null;
	}

	/**
	 * Select item ids for previously added item keys.
	 */
	protected function selectItems() {
		if (!empty(items)) {
			itemsRefs = CArray.array();

			$sqlWhere = CArray.array();
			for(items as $host => $keys) {
				$hostId = resolveHostOrTemplate($host);
				if ($hostId) {
					$sqlWhere[] = "(i.hostid=".zbx_dbstr($hostId)." AND ".dbConditionString("i.key_", $keys).")";
				}
			}

			if ($sqlWhere) {
				$dbitems = DBselect("SELECT i.itemid,i.hostid,i.key_ FROM items i WHERE ".implode(" OR ", $sqlWhere));
				while ($dbItem = DBfetch($dbitems)) {
					itemsRefs[$dbItem["hostid"]][$dbItem["key_"]] = Nest.value($dbItem,"itemid").$();
				}
			}
		}
	}

	/**
	 * Unset item refs to make referencer select them from db again.
	 */
	public function refreshItems() {
		itemsRefs = null;
	}

	/**
	 * Select value map ids for previously added value map names.
	 */
	protected function selectValueMaps() {
		if (!empty(valueMaps)) {
			valueMapsRefs = CArray.array();

			$dbitems = DBselect("SELECT v.name,v.valuemapid FROM valuemaps v WHERE ".dbConditionString("v.name", valueMaps));
			while ($dbItem = DBfetch($dbitems)) {
				valueMapsRefs[$dbItem["name"]] = Nest.value($dbItem,"valuemapid").$();
			}

			valueMaps = CArray.array();
		}
	}

	/**
	 * Select trigger ids for previously added trigger names/expressions.
	 */
	protected function selectTriggers() {
		if (!empty(triggers)) {
			triggersRefs = CArray.array();

			$triggerIds = CArray.array();
			$sql = "SELECT t.triggerid,t.expression,t.description".
				" FROM triggers t".
				" WHERE ".dbConditionString("t.description", array_keys(triggers));
			$dbTriggers = DBselect($sql);
			while ($dbTrigger = DBfetch($dbTriggers)) {
				$dbExpr = explode_exp(Nest.value($dbTrigger,"expression").$());
				for(triggers as $name => $expressions) {
					if ($name == Nest.value($dbTrigger,"description").$()) {
						for($expressions as $expression) {
							if ($expression == $dbExpr) {
								triggersRefs[$name][$expression] = Nest.value($dbTrigger,"triggerid").$();
								$triggerIds[] = Nest.value($dbTrigger,"triggerid").$();
							}
						}
					}
				}
			}

			$allowedTriggers = API.Trigger().get(CArray.array(
				"triggerids" => $triggerIds,
				"output" => CArray.array("triggerid"),
				"filter" => CArray.array(
					"flags" => CArray.array(
						ZBX_FLAG_DISCOVERY_NORMAL,
						ZBX_FLAG_DISCOVERY_PROTOTYPE,
						ZBX_FLAG_DISCOVERY_CREATED
					)
				),
				"editable" => true,
				"preservekeys" => true
			));
			for(triggersRefs as $name => $expressions) {
				for($expressions as $expression => $triggerId) {
					if (!isset($allowedTriggers[$triggerId])) {
						unset(triggersRefs[$name][$expression]);
					}
				}
			}
		}
	}

	/**
	 * Unset trigger refs to make referencer select them from db again.
	 */
	public function refreshTriggers() {
		triggersRefs = null;
	}

	/**
	 * Select icon map ids for previously added icon maps names.
	 */
	protected function selectIconMaps() {
		if (!empty(iconMaps)) {
			iconMapsRefs = CArray.array();
			$dbIconMaps = API.IconMap().get(CArray.array(
				"filter" => CArray.array("name" => iconMaps),
				"output" => CArray.array("iconmapid", "name"),
				"preservekeys" => true,
			));
			for($dbIconMaps as $iconMap) {
				iconMapsRefs[$iconMap["name"]] = Nest.value($iconMap,"iconmapid").$();
			}

			iconMaps = CArray.array();
		}
	}

	/**
	 * Select map ids for previously added maps names.
	 */
	protected function selectMaps() {
		if (!empty(maps)) {
			mapsRefs = CArray.array();
			$dbMaps = API.Map().get(CArray.array(
				"filter" => CArray.array("name" => maps),
				"output" => CArray.array("sysmapid", "name"),
				"preservekeys" => true,
			));
			for($dbMaps as $dbMap) {
				mapsRefs[$dbMap["name"]] = Nest.value($dbMap,"sysmapid").$();
			}

			maps = CArray.array();
		}
	}

	/**
	 * Select screen ids for previously added screen names.
	 */
	protected function selectScreens() {
		if (!empty(screens)) {
			screensRefs = CArray.array();

			$dbScreens = DBselect("SELECT s.screenid,s.name FROM screens s WHERE".
					" s.templateid IS NULL ".
					" AND ".dbConditionString("s.name", screens));
			while ($dbScreen = DBfetch($dbScreens)) {
				screensRefs[$dbScreen["name"]] = Nest.value($dbScreen,"screenid").$();
			}

			screens = CArray.array();
		}
	}

	/**
	 * Select macro ids for previously added macro names.
	 */
	protected function selectMacros() {
		if (!empty(macros)) {
			macrosRefs = CArray.array();
			$sqlWhere = CArray.array();
			for(macros as $host => $macros) {
				$hostId = resolveHostOrTemplate($host);
				if ($hostId) {
					$sqlWhere[] = "(hm.hostid=".zbx_dbstr($hostId)." AND ".dbConditionString("hm.macro", $macros).")";
				}
			}

			if ($sqlWhere) {
				$dbMacros = DBselect("SELECT hm.hostmacroid,hm.hostid,hm.macro FROM hostmacro hm WHERE ".implode(" OR ", $sqlWhere));
				while ($dbMacro = DBfetch($dbMacros)) {
					macrosRefs[$dbMacro["hostid"]][$dbMacro["macro"]] = Nest.value($dbMacro,"hostmacroid").$();
				}
			}

			macros = CArray.array();
		}
	}

	/**
	 * Select proxy ids for previously added proxy names.
	 */
	protected function selectProxyes() {
		if (!empty(proxies)) {
			proxiesRefs = CArray.array();
			$dbProxy = API.Proxy().get(CArray.array(
				"filter" => CArray.array("host" => proxies),
				"output" => CArray.array("hostid", "host"),
				"preservekeys" => true,
				"editable" => true
			));
			for($dbProxy as $proxy) {
				proxiesRefs[$proxy["host"]] = Nest.value($proxy,"proxyid").$();
			}

			proxies = CArray.array();
		}
	}

	/**
	 * Select host prototype ids for previously added host prototypes names.
	 */
	protected function selectHostPrototypes() {
		if (!empty(hostPrototypes)) {
			hostPrototypeRefs = CArray.array();
			$sqlWhere = CArray.array();
			for(hostPrototypes as $host => $discoveryRule) {
				$hostId = resolveHostOrTemplate($host);

				for($discoveryRule as $discoveryRuleKey => $hostPrototypes) {
					$discoveryRuleId = resolveItem($hostId, $discoveryRuleKey);
					if ($hostId) {
						$sqlWhere[] = "(hd.parent_itemid=".zbx_dbstr($discoveryRuleId)." AND ".dbConditionString("h.host", $hostPrototypes).")";
					}
				}
			}

			if ($sqlWhere) {
				$query = DBselect(
					"SELECT h.host,h.hostid,hd.parent_itemid,i.hostid AS parent_hostid ".
					" FROM hosts h,host_discovery hd,items i".
					" WHERE h.hostid=hd.hostid".
						" AND hd.parent_itemid=i.itemid".
						" AND ".implode(" OR ", $sqlWhere)
				);
				while ($data = DBfetch($query)) {
					hostPrototypeRefs[$data["parent_hostid"]][$data["parent_itemid"]][$data["host"]] = Nest.value($data,"hostid").$();
				}
			}
		}
	}
}
