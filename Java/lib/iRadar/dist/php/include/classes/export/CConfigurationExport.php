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


class CConfigurationExport {

	/**
	 * @var CExportWriter
	 */
	protected $writer;

	/**
	 * @var CConfigurationExportBuilder
	 */
	protected $builder;

	/**
	 * Array with data that must be exported.
	 *
	 * @var array
	 */
	protected $data;

	/**
	 * Array with data fields that must be exported.
	 *
	 * @var array
	 */
	protected $dataFields;


	/**
	 * Constructor.
	 *
	 * @param array $options ids of elements that should be exported.
	 */
	public function __construct(array $options) {
		options = CArray.array(
			"hosts" => CArray.array(),
			"templates" => CArray.array(),
			"groups" => CArray.array(),
			"screens" => CArray.array(),
			"images" => CArray.array(),
			"maps" => CArray.array()
		);
		options = array_merge(options, $options);

		data = CArray.array(
			"groups" => CArray.array(),
			"templates" => CArray.array(),
			"hosts" => CArray.array(),
			"triggers" => CArray.array(),
			"triggerPrototypes" => CArray.array(),
			"graphs" => CArray.array(),
			"graphPrototypes" => CArray.array(),
			"screens" => CArray.array(),
			"images" => CArray.array(),
			"maps" => CArray.array()
		);

		dataFields = CArray.array(
			"item" => CArray.array("hostid", "multiplier", "type", "snmp_community", "snmp_oid", "name", "key_", "delay", "history",
				"trends", "status", "value_type", "trapper_hosts", "units", "delta", "snmpv3_contextname",
				"snmpv3_securityname", "snmpv3_securitylevel", "snmpv3_authprotocol", "snmpv3_authpassphrase",
				"snmpv3_privprotocol", "snmpv3_privpassphrase", "formula", "valuemapid", "delay_flex", "params",
				"ipmi_sensor", "data_type", "authtype", "username", "password", "publickey", "privatekey",
				"interfaceid", "port", "description", "inventory_link", "flags"
			),
			"drule" => CArray.array("itemid", "hostid", "type", "snmp_community", "snmp_oid", "name", "key_", "delay", "history",
				"trends", "status", "value_type", "trapper_hosts", "units", "delta", "snmpv3_contextname",
				"snmpv3_securityname", "snmpv3_securitylevel", "snmpv3_authprotocol", "snmpv3_authpassphrase",
				"snmpv3_privprotocol", "snmpv3_privpassphrase", "formula", "valuemapid", "delay_flex", "params",
				"ipmi_sensor", "data_type", "authtype", "username", "password", "publickey", "privatekey",
				"interfaceid", "port", "description", "inventory_link", "flags", "filter", "lifetime"
			),
			"discoveryrule" => CArray.array("hostid", "multiplier", "type", "snmp_community", "snmp_oid", "name", "key_",
				"delay", "history", "trends", "status", "value_type", "trapper_hosts", "units", "delta",
				"snmpv3_contextname", "snmpv3_securityname", "snmpv3_securitylevel", "snmpv3_authprotocol",
				"snmpv3_authpassphrase", "snmpv3_privprotocol", "snmpv3_privpassphrase", "formula", "valuemapid",
				"delay_flex", "params", "ipmi_sensor", "data_type", "authtype", "username", "password", "publickey",
				"privatekey", "interfaceid", "port", "description", "inventory_link", "flags"
			)
		);
	}

	/**
	 * Setter for $writer property.
	 *
	 * @param CExportWriter $writer
	 */
	public function setWriter(CExportWriter $writer) {
		writer = $writer;
	}

	/**
	 * Setter for builder property.
	 *
	 * @param CConfigurationExportBuilder $builder
	 */
	public function setBuilder(CConfigurationExportBuilder $builder) {
		builder = $builder;
	}

	/**
	 * Export elements whose ids were passed to constructor.
	 * The resulting export format depends on the export writer that was set,
	 * the export structure depends on the builder that was set.
	 *
	 * @return string
	 */
	public function export() {
		gatherData();

		if (Nest.value(data,"groups").$()) {
			builder->buildGroups(Nest.value(data,"groups").$());
		}
		if (Nest.value(data,"templates").$()) {
			builder->buildTemplates(Nest.value(data,"templates").$());
		}
		if (Nest.value(data,"hosts").$()) {
			builder->buildHosts(Nest.value(data,"hosts").$());
		}
		if (Nest.value(data,"triggers").$()) {
			builder->buildTriggers(Nest.value(data,"triggers").$());
		}
		if (Nest.value(data,"graphs").$()) {
			builder->buildGraphs(Nest.value(data,"graphs").$());
		}
		if (Nest.value(data,"screens").$()) {
			builder->buildScreens(Nest.value(data,"screens").$());
		}
		if (Nest.value(data,"images").$()) {
			builder->buildImages(Nest.value(data,"images").$());
		}
		if (Nest.value(data,"maps").$()) {
			builder->buildMaps(Nest.value(data,"maps").$());
		}
		return writer->write(builder->getExport());
	}

	/**
	 * Gathers data required for export from database depends on $options passed to constructor.
	 */
	protected function gatherData() {
		$options = filterOptions(options);

		if (Nest.value($options,"groups").$()) {
			gatherGroups(Nest.value($options,"groups").$());
		}

		if (Nest.value($options,"templates").$()) {
			gatherTemplates(Nest.value($options,"templates").$());
		}

		if (Nest.value($options,"hosts").$()) {
			gatherHosts(Nest.value($options,"hosts").$());
		}

		if (Nest.value($options,"templates").$() || Nest.value($options,"hosts").$()) {
			gatherGraphs(Nest.value($options,"hosts").$(), Nest.value($options,"templates").$());
			gathertriggers(Nest.value($options,"hosts").$(), Nest.value($options,"templates").$());
		}

		if (Nest.value($options,"screens").$()) {
			gatherScreens(Nest.value($options,"screens").$());
		}

		if (Nest.value($options,"maps").$()) {
			gatherMaps(Nest.value($options,"maps").$());
		}
	}

	/**
	 * Excludes objects that cannot be exported.
	 *
	 * @param array $options
	 *
	 * @return array
	 */
	protected function filterOptions(array $options) {
		if (Nest.value($options,"hosts").$()) {
			// exclude discovered hosts
			$hosts = API.Host().get(CArray.array(
				"output" => CArray.array("hostid"),
				"hostids" => Nest.value($options,"hosts").$(),
				"filter" => CArray.array("flags" => ZBX_FLAG_DISCOVERY_NORMAL)
			));
			Nest.value($options,"hosts").$() = zbx_objectValues($hosts, "hostid");
		}

		return $options;
	}

	/**
	 * Get groups for export from database.
	 *
	 * @param array $groupIds
	 */
	protected function gatherGroups(array $groupIds) {
		Nest.value(data,"groups").$() = API.HostGroup()->get(CArray.array(
			"groupids" => $groupIds,
			"preservekeys" => true,
			"output" => API_OUTPUT_EXTEND
		));
	}

	/**
	 * Get templates for export from database.
	 *
	 * @param array $templateIds
	 */
	protected function gatherTemplates(array $templateIds) {
		$templates = API.Template().get(CArray.array(
			"templateids" => $templateIds,
			"output" => CArray.array("host", "name"),
			"selectMacros" => API_OUTPUT_EXTEND,
			"selectGroups" => API_OUTPUT_EXTEND,
			"selectParentTemplates" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));

		// merge host groups with all groups
		$templateGroups = CArray.array();
		for($templates as &$template) {
			$templateGroups += zbx_toHash(Nest.value($template,"groups").$(), "groupid");

			Nest.value($template,"screens").$() = CArray.array();
			Nest.value($template,"applications").$() = CArray.array();
			Nest.value($template,"discoveryRules").$() = CArray.array();
			Nest.value($template,"items").$() = CArray.array();
		}
		unset($template);
		Nest.value(data,"groups").$() += $templateGroups;

		// applications
		$applications = API.Application().get(CArray.array(
			"hostids" => $templateIds,
			"output" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"preservekeys" => true
		));

		for($applications as $application) {
			if (!isset($templates[$application["hostid"]]["applications"])) {
				$templates[$application["hostid"]]["applications"] = CArray.array();
			}
			$templates[$application["hostid"]]["applications"][] = $application;
		}

		// screens
		$screens = API.TemplateScreen().get(CArray.array(
			"templateids" => $templateIds,
			"selectScreenItems" => API_OUTPUT_EXTEND,
			"noInheritance" => true,
			"output" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));
		prepareScreenExport($screens);

		for($screens as $screen) {
			if (!isset($templates[$screen["templateid"]]["screens"])) {
				$templates[$screen["templateid"]]["screens"] = CArray.array();
			}
			$templates[$screen["templateid"]]["screens"][] = $screen;
		}

		Nest.value(data,"templates").$() = $templates;

		gatherTemplateItems($templateIds);
		gatherTemplateDiscoveryRules($templateIds);
	}

	/**
	 * Get Hosts for export from database.
	 *
	 * @param array $hostIds
	 */
	protected function gatherHosts(array $hostIds) {
		$hosts = API.Host().get(CArray.array(
			"hostids" => $hostIds,
			"output" => CArray.array("proxy_hostid", "host", "status", "ipmi_authtype", "ipmi_privilege", "ipmi_username",
				"ipmi_password", "name"),
			"selectInventory" => true,
			"selectInterfaces" => CArray.array("interfaceid", "main", "type", "useip", "ip", "dns", "port"),
			"selectMacros" => API_OUTPUT_EXTEND,
			"selectGroups" => API_OUTPUT_EXTEND,
			"selectParentTemplates" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));

		// merge host groups with all groups
		$hostGroups = CArray.array();
		for($hosts as &$host) {
			$hostGroups += zbx_toHash(Nest.value($host,"groups").$(), "groupid");
			Nest.value($host,"applications").$() = CArray.array();
			Nest.value($host,"discoveryRules").$() = CArray.array();
			Nest.value($host,"items").$() = CArray.array();
		}
		unset($host);
		Nest.value(data,"groups").$() += $hostGroups;

		// applications
		$applications = API.Application().get(CArray.array(
			"hostids" => $hostIds,
			"output" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"preservekeys" => true
		));
		for($applications as $application) {
			if (!isset($hosts[$application["hostid"]]["applications"])) {
				$hosts[$application["hostid"]]["applications"] = CArray.array();
			}
			$hosts[$application["hostid"]]["applications"][] = $application;
		}

		// proxies
		$dbProxies = DBselect(
			"SELECT h.hostid,h.host".
			" FROM hosts h".
			" WHERE ".dbConditionInt("h.hostid", zbx_objectValues($hosts, "proxy_hostid"))
		);
		$proxies = CArray.array();
		while ($proxy = DBfetch($dbProxies)) {
			$proxies[$proxy["hostid"]] = Nest.value($proxy,"host").$();
		}

		for($hosts as &$host) {
			Nest.value($host,"proxy").$() = Nest.value($host,"proxy_hostid").$() ? CArray.array("name" => $proxies[$host["proxy_hostid"]]) : null;
		}
		unset($host);

		Nest.value(data,"hosts").$() = $hosts;

		gatherHostItems($hostIds);
		gatherHostDiscoveryRules($hostIds);
	}

	/**
	 * Get hosts items from database.
	 *
	 * @param array $hostIds
	 */
	protected function gatherHostItems(array $hostIds) {
		$items = API.Item().get(CArray.array(
			"hostids" => $hostIds,
			"output" => Nest.value(dataFields,"item").$(),
			"selectApplications" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"filter" => CArray.array("flags" => CArray.array(ZBX_FLAG_DISCOVERY_NORMAL)),
			"preservekeys" => true
		));
		$items = prepareItems($items);
		for($items as $item) {
			if (!isset(data["hosts"][$item["hostid"]]["items"])) {
				data["hosts"][$item["hostid"]]["items"] = CArray.array();
			}

			data["hosts"][$item["hostid"]]["items"][] = $item;
		}
	}

	/**
	 * Get templates items from database.
	 *
	 * @param array $templateIds
	 */
	protected function gatherTemplateItems(array $templateIds) {
		$items = API.Item().get(CArray.array(
			"hostids" => $templateIds,
			"output" => Nest.value(dataFields,"item").$(),
			"selectApplications" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"filter" => CArray.array("flags" => CArray.array(ZBX_FLAG_DISCOVERY_NORMAL)),
			"preservekeys" => true
		));

		$items = prepareItems($items);

		for($items as $item) {
			if (!isset(data["templates"][$item["hostid"]]["items"])) {
				data["templates"][$item["hostid"]]["items"] = CArray.array();
			}

			data["templates"][$item["hostid"]]["items"][] = $item;
		}
	}

	/**
	 * Get items related objects data from database.
	 *
	 * @param array $items
	 *
	 * @return array
	 */
	protected function prepareItems(array $items) {
		// gather value maps
		$valueMapIds = zbx_objectValues($items, "valuemapid");
		$dbValueMaps = DBselect("SELECT vm.valuemapid, vm.name FROM valuemaps vm WHERE ".dbConditionInt("vm.valuemapid", $valueMapIds));
		$valueMapNames = CArray.array();
		while ($valueMap = DBfetch($dbValueMaps)) {
			$valueMapNames[$valueMap["valuemapid"]] = Nest.value($valueMap,"name").$();
		}

		for($items as &$item) {
			Nest.value($item,"valuemap").$() = CArray.array();
			if (Nest.value($item,"valuemapid").$()) {
				Nest.value($item,"valuemap").$() = CArray.array("name" => $valueMapNames[$item["valuemapid"]]);
			}
		}
		unset($item);

		return $items;
	}

	/**
	 * Get hosts discovery rules from database.
	 *
	 * @param array $hostIds
	 */
	protected function gatherHostDiscoveryRules(array $hostIds) {
		$items = API.DiscoveryRule().get(CArray.array(
			"hostids" => $hostIds,
			"output" => Nest.value(dataFields,"drule").$(),
			"inherited" => false,
			"preservekeys" => true
		));

		$items = prepareDiscoveryRules($items);

		for($items as $item) {
			if (!isset(data["hosts"][$item["hostid"]]["items"])) {
				data["hosts"][$item["hostid"]]["discoveryRules"] = CArray.array();
			}
			data["hosts"][$item["hostid"]]["discoveryRules"][] = $item;
		}
	}

	/**
	 * Get templates discovery rules from database.
	 *
	 * @param array $templateIds
	 */
	protected function gatherTemplateDiscoveryRules(array $templateIds) {
		$items = API.DiscoveryRule().get(CArray.array(
			"hostids" => $templateIds,
			"output" => Nest.value(dataFields,"drule").$(),
			"inherited" => false,
			"preservekeys" => true
		));

		$items = prepareDiscoveryRules($items);

		for($items as $item) {
			if (!isset(data["templates"][$item["hostid"]]["discoveryRules"])) {
				data["templates"][$item["hostid"]]["discoveryRules"] = CArray.array();
			}
			data["templates"][$item["hostid"]]["discoveryRules"][] = $item;
		}
	}

	/**
	 * Get discovery rules related objects from database.
	 *
	 * @param array $items
	 *
	 * @return array
	 */
	protected function prepareDiscoveryRules(array $items) {
		for($items as &$item) {
			Nest.value($item,"itemPrototypes").$() = CArray.array();
			Nest.value($item,"graphPrototypes").$() = CArray.array();
			Nest.value($item,"triggerPrototypes").$() = CArray.array();
			Nest.value($item,"hostPrototypes").$() = CArray.array();
		}
		unset($item);

		// gather item prototypes
		$prototypes = API.ItemPrototype().get(CArray.array(
			"discoveryids" => zbx_objectValues($items, "itemid"),
			"output" => Nest.value(dataFields,"discoveryrule").$(),
			"selectApplications" => API_OUTPUT_EXTEND,
			"selectDiscoveryRule" => CArray.array("itemid"),
			"inherited" => false,
			"preservekeys" => true
		));

		// gather value maps
		$valueMapIds = zbx_objectValues($prototypes, "valuemapid");
		$DbValueMaps = DBselect("SELECT vm.valuemapid, vm.name FROM valuemaps vm WHERE ".dbConditionInt("vm.valuemapid", $valueMapIds));
		$valueMaps = CArray.array();
		while ($valueMap = DBfetch($DbValueMaps)) {
			$valueMaps[$valueMap["valuemapid"]] = Nest.value($valueMap,"name").$();
		}

		for($prototypes as $prototype) {
			Nest.value($prototype,"valuemap").$() = CArray.array();
			if (Nest.value($prototype,"valuemapid").$()) {
				Nest.value($prototype,"valuemap","name").$() = $valueMaps[$prototype["valuemapid"]];
			}

			$items[$prototype["discoveryRule"]["itemid"]]["itemPrototypes"][] = $prototype;
		}

		// gather graph prototypes
		$graphs = API.GraphPrototype().get(CArray.array(
			"discoveryids" => zbx_objectValues($items, "itemid"),
			"selectDiscoveryRule" => API_OUTPUT_EXTEND,
			"selectGraphItems" => API_OUTPUT_EXTEND,
			"output" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"preservekeys" => true
		));
		$graphs = prepareGraphs($graphs);
		for($graphs as $graph) {
			$items[$graph["discoveryRule"]["itemid"]]["graphPrototypes"][] = $graph;
		}

		// gather trigger prototypes
		$triggers = API.TriggerPrototype().get(CArray.array(
			"discoveryids" => zbx_objectValues($items, "itemid"),
			"output" => API_OUTPUT_EXTEND,
			"selectDiscoveryRule" => API_OUTPUT_EXTEND,
			"selectItems" => CArray.array("flags", "type"),
			"inherited" => false,
			"preservekeys" => true,
			"expandData" => true
		));

		for($triggers as $trigger){
			for(Nest.value($trigger,"items").$() as $item) {
				if (Nest.value($item,"flags").$() == ZBX_FLAG_DISCOVERY_CREATED || Nest.value($item,"type").$() == ITEM_TYPE_HTTPTEST) {
					continue 2;
				}
			}

			Nest.value($trigger,"expression").$() = explode_exp(Nest.value($trigger,"expression").$());
			$items[$trigger["discoveryRule"]["itemid"]]["triggerPrototypes"][] = $trigger;
		}

		// gather host prototypes
		$hostPrototypes = API.HostPrototype().get(CArray.array(
			"discoveryids" => zbx_objectValues($items, "itemid"),
			"output" => API_OUTPUT_EXTEND,
			"selectGroupLinks" => API_OUTPUT_EXTEND,
			"selectGroupPrototypes" => API_OUTPUT_EXTEND,
			"selectDiscoveryRule" => API_OUTPUT_EXTEND,
			"selectTemplates" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"preservekeys" => true
		));

		// replace group prototype group IDs with references
		$groupIds = CArray.array();
		for($hostPrototypes as $hostPrototype) {
			for(Nest.value($hostPrototype,"groupLinks").$() as $groupLink) {
				$groupIds[$groupLink["groupid"]] = Nest.value($groupLink,"groupid").$();
			}
		}
		$groups = getGroupsReferences($groupIds);

		// export the groups used in group prototypes
		Nest.value(data,"groups").$() += $groups;

		for($hostPrototypes as $hostPrototype) {
			for(Nest.value($hostPrototype,"groupLinks").$() as &$groupLink) {
				Nest.value($groupLink,"groupid").$() = $groups[$groupLink["groupid"]];
			}
			unset($groupLink);
			$items[$hostPrototype["discoveryRule"]["itemid"]]["hostPrototypes"][] = $hostPrototype;
		}

		return $items;
	}

	/**
	 * Get graphs for export from database.
	 *
	 * @param array $hostIds
	 * @param array $templateIds
	 */
	protected function gatherGraphs(array $hostIds, array $templateIds) {
		$hostIds = array_merge($hostIds, $templateIds);

		$graphs = API.Graph().get(CArray.array(
			"hostids" => $hostIds,
			"filter" => CArray.array("flags" => CArray.array(ZBX_FLAG_DISCOVERY_NORMAL)),
			"selectGraphItems" => API_OUTPUT_EXTEND,
			"inherited" => false,
			"output" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));

		Nest.value(data,"graphs").$() = prepareGraphs($graphs);
	}

	/**
	 * Unset graphs that have lld created items or web items.
	 *
	 * @param array $graphs
	 *
	 * @return array
	 */
	protected function prepareGraphs(array $graphs) {
		// get item axis items info
		$graphItemIds = CArray.array();
		for($graphs as $graph) {
			for(Nest.value($graph,"gitems").$() as $gItem) {
				$graphItemIds[$gItem["itemid"]] = Nest.value($gItem,"itemid").$();
			}
			if (Nest.value($graph,"ymin_itemid").$()) {
				$graphItemIds[$graph["ymin_itemid"]] = Nest.value($graph,"ymin_itemid").$();
			}
			if (Nest.value($graph,"ymax_itemid").$()) {
				$graphItemIds[$graph["ymax_itemid"]] = Nest.value($graph,"ymax_itemid").$();
			}
		}

		$graphItems = API.Item().get(CArray.array(
			"itemids" => $graphItemIds,
			"output" => CArray.array("key_", "flags", "type"),
			"webitems" => true,
			"selectHosts" => CArray.array("host"),
			"preservekeys" => true,
			"filter" => CArray.array("flags" => null)
		));

		for($graphs as $gnum => $graph) {
			if (Nest.value($graph,"ymin_itemid").$() && isset($graphItems[$graph["ymin_itemid"]])) {
				$axisItem = $graphItems[$graph["ymin_itemid"]];
				// unset lld and web graphs
				if (Nest.value($axisItem,"flags").$() == ZBX_FLAG_DISCOVERY_CREATED || Nest.value($axisItem,"type").$() == ITEM_TYPE_HTTPTEST) {
					unset($graphs[$gnum]);
					continue;
				}

				$axisItemHost = reset(Nest.value($axisItem,"hosts").$());
				$graphs[$gnum]["ymin_itemid"] = CArray.array(
					"host" => Nest.value($axisItemHost,"host").$(),
					"key" => $axisItem["key_"]
				);
			}
			if (Nest.value($graph,"ymax_itemid").$() && isset($graphItems[$graph["ymax_itemid"]])) {
				$axisItem = $graphItems[$graph["ymax_itemid"]];
				// unset lld and web graphs
				if (Nest.value($axisItem,"flags").$() == ZBX_FLAG_DISCOVERY_CREATED || Nest.value($axisItem,"type").$() == ITEM_TYPE_HTTPTEST) {
					unset($graphs[$gnum]);
					continue;
				}
				$axisItemHost = reset(Nest.value($axisItem,"hosts").$());
				$graphs[$gnum]["ymax_itemid"] = CArray.array(
					"host" => Nest.value($axisItemHost,"host").$(),
					"key" => $axisItem["key_"]
				);
			}

			for(Nest.value($graph,"gitems").$() as $ginum => $gItem) {
				$item = $graphItems[$gItem["itemid"]];

				// unset lld and web graphs
				if (Nest.value($item,"flags").$() == ZBX_FLAG_DISCOVERY_CREATED || Nest.value($item,"type").$() == ITEM_TYPE_HTTPTEST) {
					unset($graphs[$gnum]);
					continue 2;
				}
				$itemHost = reset(Nest.value($item,"hosts").$());
				$graphs[$gnum]["gitems"][$ginum]["itemid"] = CArray.array(
					"host" => Nest.value($itemHost,"host").$(),
					"key" => $item["key_"]
				);
			}
		}

		return $graphs;
	}

	/**
	 * Get triggers for export from database.
	 *
	 * @param array $hostIds
	 * @param array $templateIds
	 */
	protected function gatherTriggers(array $hostIds, array $templateIds) {
		$hostIds = array_merge($hostIds, $templateIds);

		$triggers = API.Trigger().get(CArray.array(
			"hostids" => $hostIds,
			"output" => API_OUTPUT_EXTEND,
			"filter" => CArray.array("flags" => CArray.array(ZBX_FLAG_DISCOVERY_NORMAL)),
			"selectDependencies" => API_OUTPUT_EXTEND,
			"selectItems" => CArray.array("flags", "type"),
			"inherited" => false,
			"preservekeys" => true,
			"expandData" => true
		));

		for($triggers as $trigger){
			for(Nest.value($trigger,"items").$() as $item) {
				if (Nest.value($item,"flags").$() == ZBX_FLAG_DISCOVERY_CREATED || Nest.value($item,"type").$() == ITEM_TYPE_HTTPTEST) {
					continue 2;
				}
			}

			Nest.value($trigger,"expression").$() = explode_exp(Nest.value($trigger,"expression").$());

			for(Nest.value($trigger,"dependencies").$() as &$dependency) {
				Nest.value($dependency,"expression").$() = explode_exp(Nest.value($dependency,"expression").$());
			}
			unset($dependency);

			data["triggers"][] = $trigger;
		}
	}

	/**
	 * Get maps for export from database.
	 *
	 * @param array $mapIds
	 */
	protected function gatherMaps(array $mapIds) {
		$sysmaps = API.Map().get(CArray.array(
			"sysmapids" => $mapIds,
			"selectSelements" => API_OUTPUT_EXTEND,
			"selectLinks" => API_OUTPUT_EXTEND,
			"selectIconMap" => API_OUTPUT_EXTEND,
			"selectUrls" => API_OUTPUT_EXTEND,
			"output" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));
		prepareMapExport($sysmaps);
		Nest.value(data,"maps").$() = $sysmaps;

		$images = API.Image().get(CArray.array(
			"output" => CArray.array("imageid", "name", "imagetype"),
			"sysmapids" => zbx_objectValues($sysmaps, "sysmapid"),
			"select_image" => true,
			"preservekeys" => true
		));

		for($images as &$image) {
			$image = CArray.array(
				"name" => Nest.value($image,"name").$(),
				"imagetype" => Nest.value($image,"imagetype").$(),
				"encodedImage" => Nest.value($image,"image").$(),
			);
		}
		unset($image);

		Nest.value(data,"images").$() = $images;
	}

	/**
	 * Get screens for export from database.
	 *
	 * @param array $screenIds
	 */
	protected function gatherScreens(array $screenIds) {
		$screens = API.Screen().get(CArray.array(
			"screenids" => $screenIds,
			"selectScreenItems" => API_OUTPUT_EXTEND,
			"output" => API_OUTPUT_EXTEND
		));

		prepareScreenExport($screens);
		Nest.value(data,"screens").$() = $screens;
	}

	/**
	 * Change screen elements real database resource id to unique field references.
	 *
	 * @param array $exportScreens
	 */
	protected function prepareScreenExport(array &$exportScreens) {
		$screenIds = CArray.array();
		$sysmapIds = CArray.array();
		$groupIds = CArray.array();
		$hostIds = CArray.array();
		$graphIds = CArray.array();
		$itemIds = CArray.array();

		// gather element ids that must be substituted
		for($exportScreens as $screen) {
			for(Nest.value($screen,"screenitems").$() as $screenItem) {
				if (Nest.value($screenItem,"resourceid").$() != 0) {
					switch (Nest.value($screenItem,"resourcetype").$()) {
						case SCREEN_RESOURCE_HOSTS_INFO:
							// fall through
						case SCREEN_RESOURCE_TRIGGERS_INFO:
							// fall through
						case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
							// fall through
						case SCREEN_RESOURCE_DATA_OVERVIEW:
							// fall through
						case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
							$groupIds[$screenItem["resourceid"]] = Nest.value($screenItem,"resourceid").$();
							break;

						case SCREEN_RESOURCE_HOST_TRIGGERS:
							$hostIds[$screenItem["resourceid"]] = Nest.value($screenItem,"resourceid").$();
							break;

						case SCREEN_RESOURCE_GRAPH:
							$graphIds[$screenItem["resourceid"]] = Nest.value($screenItem,"resourceid").$();
							break;

						case SCREEN_RESOURCE_SIMPLE_GRAPH:
							// fall through
						case SCREEN_RESOURCE_PLAIN_TEXT:
							$itemIds[$screenItem["resourceid"]] = Nest.value($screenItem,"resourceid").$();
							break;

						case SCREEN_RESOURCE_MAP:
							$sysmapIds[$screenItem["resourceid"]] = Nest.value($screenItem,"resourceid").$();
							break;

						case SCREEN_RESOURCE_SCREEN:
							$screenIds[$screenItem["resourceid"]] = Nest.value($screenItem,"resourceid").$();
							break;
					}
				}
			}
		}

		$screens = getScreensReferences($screenIds);
		$sysmaps = getMapsReferences($sysmapIds);
		$groups = getGroupsReferences($groupIds);
		$hosts = getHostsReferences($hostIds);
		$graphs = getGraphsReferences($graphIds);
		$items = getItemsReferences($itemIds);

		for($exportScreens as &$screen) {
			unset(Nest.value($screen,"screenid").$());

			foreach	(Nest.value($screen,"screenitems").$() as &$screenItem) {
				if (Nest.value($screenItem,"resourceid").$() != 0) {
					switch (Nest.value($screenItem,"resourcetype").$()) {
						case SCREEN_RESOURCE_HOSTS_INFO:
							// fall through
						case SCREEN_RESOURCE_TRIGGERS_INFO:
							// fall through
						case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
							// fall through
						case SCREEN_RESOURCE_DATA_OVERVIEW:
							// fall through
						case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
							Nest.value($screenItem,"resourceid").$() = $groups[$screenItem["resourceid"]];
							break;

						case SCREEN_RESOURCE_HOST_TRIGGERS:
							Nest.value($screenItem,"resourceid").$() = $hosts[$screenItem["resourceid"]];
							break;

						case SCREEN_RESOURCE_GRAPH:
							Nest.value($screenItem,"resourceid").$() = $graphs[$screenItem["resourceid"]];
							break;

						case SCREEN_RESOURCE_SIMPLE_GRAPH:
							// fall through
						case SCREEN_RESOURCE_PLAIN_TEXT:
							Nest.value($screenItem,"resourceid").$() = $items[$screenItem["resourceid"]];
							break;

						case SCREEN_RESOURCE_MAP:
							Nest.value($screenItem,"resourceid").$() = $sysmaps[$screenItem["resourceid"]];
							break;

						case SCREEN_RESOURCE_SCREEN:
							Nest.value($screenItem,"resourceid").$() = $screens[$screenItem["resourceid"]];
							break;
					}
				}
			}
			unset($screenItem);
		}
		unset($screen);
	}

	/**
	 * Change map elements real database selement id and icons ids to unique field references.
	 *
	 * @param array $exportMaps
	 */
	protected function prepareMapExport(array &$exportMaps) {
		$sysmapIds = CArray.array();
		$groupIds = CArray.array();
		$hostIds = CArray.array();
		$triggerIds = CArray.array();
		$imageIds = CArray.array();

		// gather element ids that must be substituted
		for($exportMaps as $sysmap) {
			for(Nest.value($sysmap,"selements").$() as $selement) {
				switch (Nest.value($selement,"elementtype").$()) {
					case SYSMAP_ELEMENT_TYPE_MAP:
						$sysmapIds[$selement["elementid"]] = Nest.value($selement,"elementid").$();
						break;

					case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
						$groupIds[$selement["elementid"]] = Nest.value($selement,"elementid").$();
						break;

					case SYSMAP_ELEMENT_TYPE_HOST:
						$hostIds[$selement["elementid"]] = Nest.value($selement,"elementid").$();
						break;

					case SYSMAP_ELEMENT_TYPE_TRIGGER:
						$triggerIds[$selement["elementid"]] = Nest.value($selement,"elementid").$();
						break;
				}

				if (Nest.value($selement,"iconid_off").$() > 0) {
					$imageIds[$selement["iconid_off"]] = Nest.value($selement,"iconid_off").$();
				}
				if (Nest.value($selement,"iconid_on").$() > 0) {
					$imageIds[$selement["iconid_on"]] = Nest.value($selement,"iconid_on").$();
				}
				if (Nest.value($selement,"iconid_disabled").$() > 0) {
					$imageIds[$selement["iconid_disabled"]] = Nest.value($selement,"iconid_disabled").$();
				}
				if (Nest.value($selement,"iconid_maintenance").$() > 0) {
					$imageIds[$selement["iconid_maintenance"]] = Nest.value($selement,"iconid_maintenance").$();
				}
			}

			if (Nest.value($sysmap,"backgroundid").$() > 0) {
				$imageIds[$sysmap["backgroundid"]] = Nest.value($sysmap,"backgroundid").$();
			}

			for(Nest.value($sysmap,"links").$() as $link) {
				for(Nest.value($link,"linktriggers").$() as $linktrigger) {
					$triggerIds[$linktrigger["triggerid"]] = Nest.value($linktrigger,"triggerid").$();
				}
			}
		}

		$sysmaps = getMapsReferences($sysmapIds);
		$groups = getGroupsReferences($groupIds);
		$hosts = getHostsReferences($hostIds);
		$triggers = getTriggersReferences($triggerIds);
		$images = getImagesReferences($imageIds);

		for($exportMaps as &$sysmap) {
			if (!empty(Nest.value($sysmap,"iconmap").$())) {
				Nest.value($sysmap,"iconmap").$() = CArray.array("name" => Nest.value($sysmap,"iconmap","name").$());
			}

			for(Nest.value($sysmap,"urls").$() as $unum => $url) {
				unset($sysmap["urls"][$unum]["sysmapurlid"]);
			}

			Nest.value($sysmap,"backgroundid").$() = (Nest.value($sysmap,"backgroundid").$() > 0) ? $images[$sysmap["backgroundid"]] : CArray.array();

			for(Nest.value($sysmap,"selements").$() as &$selement) {
				switch (Nest.value($selement,"elementtype").$()) {
					case SYSMAP_ELEMENT_TYPE_MAP:
						Nest.value($selement,"elementid").$() = $sysmaps[$selement["elementid"]];
						break;
					case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
						Nest.value($selement,"elementid").$() = $groups[$selement["elementid"]];
						break;
					case SYSMAP_ELEMENT_TYPE_HOST:
						Nest.value($selement,"elementid").$() = $hosts[$selement["elementid"]];
						break;
					case SYSMAP_ELEMENT_TYPE_TRIGGER:
						Nest.value($selement,"elementid").$() = $triggers[$selement["elementid"]];
						break;
				}

				Nest.value($selement,"iconid_off").$() = Nest.value($selement,"iconid_off").$() > 0 ? $images[$selement["iconid_off"]] : "";
				Nest.value($selement,"iconid_on").$() = Nest.value($selement,"iconid_on").$() > 0 ? $images[$selement["iconid_on"]] : "";
				Nest.value($selement,"iconid_disabled").$() = Nest.value($selement,"iconid_disabled").$() > 0 ? $images[$selement["iconid_disabled"]] : "";
				Nest.value($selement,"iconid_maintenance").$() = Nest.value($selement,"iconid_maintenance").$() > 0 ? $images[$selement["iconid_maintenance"]] : "";
			}
			unset($selement);

			for(Nest.value($sysmap,"links").$() as &$link) {
				for(Nest.value($link,"linktriggers").$() as &$linktrigger) {
					Nest.value($linktrigger,"triggerid").$() = $triggers[$linktrigger["triggerid"]];
				}
				unset($linktrigger);
			}
			unset($link);
		}
		unset($sysmap);
	}

	/**
	 * Get groups references by group ids.
	 *
	 * @param array $groupIds
	 *
	 * @return array
	 */
	protected function getGroupsReferences(array $groupIds) {
		$idents = CArray.array();
		$groups = API.HostGroup().get(CArray.array(
			"groupids" => $groupIds,
			"output" => CArray.array("name"),
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($groups as $id => $group) {
			$idents[$id] = CArray.array("name" => Nest.value($group,"name").$());
		}

		return $idents;
	}

	/**
	 * Get hosts references by host ids.
	 *
	 * @param array $hostIds
	 *
	 * @return array
	 */
	protected function getHostsReferences(array $hostIds) {
		$idents = CArray.array();
		$hosts = API.Host().get(CArray.array(
			"hostids" => $hostIds,
			"output" => CArray.array("host"),
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($hosts as $id => $host) {
			$idents[$id] = CArray.array("host" => Nest.value($host,"host").$());
		}

		return $idents;
	}

	/**
	 * Get screens references by screen ids.
	 *
	 * @param array $screenIds
	 *
	 * @return array
	 */
	protected function getScreensReferences(array $screenIds) {
		$idents = CArray.array();
		$screens = API.Screen().get(CArray.array(
			"screenids" => $screenIds,
			"output" => API_OUTPUT_EXTEND,
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($screens as $id => $screen) {
			$idents[$id] = CArray.array("name" => Nest.value($screen,"name").$());
		}

		return $idents;
	}

	/**
	 * Get maps references by map ids.
	 *
	 * @param array $mapIds
	 *
	 * @return array
	 */
	protected function getMapsReferences(array $mapIds) {
		$idents = CArray.array();
		$maps = API.Map().get(CArray.array(
			"sysmapids" => $mapIds,
			"output" => CArray.array("name"),
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($maps as $id => $map) {
			$idents[$id] = CArray.array("name" => Nest.value($map,"name").$());
		}

		return $idents;
	}

	/**
	 * Get graphs references by graph ids.
	 *
	 * @param array $graphIds
	 *
	 * @return array
	 */
	protected function getGraphsReferences(array $graphIds) {
		$idents = CArray.array();
		$graphs = API.Graph().get(CArray.array(
			"graphids" => $graphIds,
			"selectHosts" => CArray.array("host"),
			"output" => CArray.array("name"),
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($graphs as $id => $graph) {
			$host = reset(Nest.value($graph,"hosts").$());
			$idents[$id] = CArray.array(
				"name" => Nest.value($graph,"name").$(),
				"host" => $host["host"]
			);
		}

		return $idents;
	}

	/**
	 * Get items references by item ids.
	 *
	 * @param array $itemIds
	 *
	 * @return array
	 */
	protected function getItemsReferences(array $itemIds) {
		$idents = CArray.array();
		$items = API.Item().get(CArray.array(
			"itemids" => $itemIds,
			"output" => CArray.array("key_"),
			"selectHosts" => CArray.array("host"),
			"nodeids" => get_current_nodeid(true),
			"webitems" => true,
			"preservekeys" => true,
			"filter" => CArray.array("flags" => null)
		));
		for($items as $id => $item) {
			$host = reset(Nest.value($item,"hosts").$());
			$idents[$id] = CArray.array(
				"key" => Nest.value($item,"key_").$(),
				"host" => $host["host"]
			);
		}

		return $idents;
	}

	/**
	 * Get triggers references by trigger ids.
	 *
	 * @param array $triggerIds
	 *
	 * @return array
	 */
	protected function getTriggersReferences(array $triggerIds) {
		$idents = CArray.array();
		$triggers = API.Trigger().get(CArray.array(
			"triggerids" => $triggerIds,
			"output" => CArray.array("description", "expression"),
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($triggers as $id => $trigger) {
			$idents[$id] = CArray.array(
				"description" => Nest.value($trigger,"description").$(),
				"expression" => explode_exp(Nest.value($trigger,"expression").$())
			);
		}

		return $idents;
	}

	/**
	 * Get images references by image ids.
	 *
	 * @param array $imageIds
	 *
	 * @return array
	 */
	protected function getImagesReferences(array $imageIds) {
		$idents = CArray.array();
		$images = API.Image().get(CArray.array(
			"output" => CArray.array("imageid", "name"),
			"imageids" => $imageIds,
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
		for($images as $id => $image) {
			$idents[$id] = CArray.array("name" => Nest.value($image,"name").$());
		}

		return $idents;
	}
}
