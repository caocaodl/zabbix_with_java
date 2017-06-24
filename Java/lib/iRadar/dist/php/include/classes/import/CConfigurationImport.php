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
 * Class for importing configuration data.
 */
class CConfigurationImport {

	/**
	 * @var CImportReader
	 */
	protected $reader;

	/**
	 * @var CImportFormatter
	 */
	protected $formatter;

	/**
	 * @var CImportReferencer
	 */
	protected $referencer;

	/**
	 * @var array
	 */
	protected $options;

	/**
	 * @var string with import data in one of supported formats
	 */
	protected $source;

	/**
	 * @var array with data read from source string
	 */
	protected $data;

	/**
	 * @var array with formatted data received from formatter
	 */
	protected $formattedData = CArray.array();

	/**
	 * Constructor.
	 * Source string must be suitable for reader class,
	 * i.e. if string contains json then reader should be able to read json.
	 *
	 * @param string $source
	 * @param array $options
	 */
	public function __construct($source, array $options = CArray.array()) {
		options = CArray.array(
			"groups" => CArray.array("createMissing" => false),
			"hosts" => CArray.array("updateExisting" => false, "createMissing" => false),
			"templates" => CArray.array("updateExisting" => false, "createMissing" => false),
			"templateScreens" => CArray.array("updateExisting" => false, "createMissing" => false),
			"applications" => CArray.array("updateExisting" => false, "createMissing" => false),
			"templateLinkage" => CArray.array("createMissing" => false),
			"items" => CArray.array("updateExisting" => false, "createMissing" => false),
			"discoveryRules" => CArray.array("updateExisting" => false, "createMissing" => false),
			"triggers" => CArray.array("updateExisting" => false, "createMissing" => false),
			"graphs" => CArray.array("updateExisting" => false, "createMissing" => false),
			"screens" => CArray.array("updateExisting" => false, "createMissing" => false),
			"maps" => CArray.array("updateExisting" => false, "createMissing" => false),
			"images" => CArray.array("updateExisting" => false, "createMissing" => false),
		);
		options = array_merge(options, $options);

		source = $source;
	}

	/**
	 * Set reader that is used to read data from source string that is passed to constructor.
	 *
	 * @param CImportReader $reader
	 */
	public function setReader(CImportReader $reader) {
		reader = $reader;
	}

	/**
	 * Import configuration data.
	 *
	 * @todo   for 1.8 version import old class CXmlImport18 is used
	 *
	 * @throws Exception
	 * @throws UnexpectedValueException
	 * @return bool
	 */
	public function import() {
		if (empty(reader)) {
			throw new UnexpectedValueException("Reader is not set.");
		}

		try {
			// hack to make api throw exceptions
			// this made to not check all api calls results for false return
			czbxrpc::$useExceptions = true;
			DBstart();

			data = reader->read(source);

			$version = getImportVersion();

			// if import version is 1.8 we use old class that support it.
			// old import class process hosts, maps and screens separately.
			if ($version == "1.8") {
				CXmlImport18::import(source);
				if (Nest.value(options,"maps","updateExisting").$() || Nest.value(options,"maps","createMissing").$()) {
					CXmlImport18::parseMap(options);
				}
				if (Nest.value(options,"screens","updateExisting").$() || Nest.value(options,"screens","createMissing").$()) {
					CXmlImport18::parseScreen(options);
				}
				if (options["hosts"]["updateExisting"]
						|| options["hosts"]["createMissing"]
						|| options["templates"]["updateExisting"]
						|| Nest.value(options,"templates","createMissing").$()) {
					CXmlImport18::parseMain(options);
				}
			}
			else {
				formatter = getFormatter($version);

				// pass data to formatter
				// export has root key \"zabbix_export\" which is not passed
				formatter->setData(Nest.value(data,"zabbix_export").$());
				referencer = new CImportReferencer();

				// parse all import for references to resolve them all together with less sql count
				gatherReferences();
				processGroups();
				processTemplates();
				processHosts();
				processApplications();
				processItems();
				processDiscoveryRules();
				processTriggers();
				processGraphs();
				processImages();
				processMaps();

				// screens should be created after all other elements
				processTemplateScreens();
				processScreens();
			}

			// prevent api from throwing exception
			czbxrpc::$useExceptions = false;
			return DBend(true);
		}
		catch (Exception $e) {
			czbxrpc::$useExceptions = false;
			DBend(false);
			throw new Exception($e.getMessage(), $e.getCode());
		}
	}

	/**
	 * Parse all import data and collect references to objects.
	 * For host objects it collects host names, for items - host name and item key, etc.
	 * Collected references are added and resolved via the referencer object.
	 *
	 * @see CImportReferencer
	 */
	protected function gatherReferences() {
		$groupsRefs = CArray.array();
		$templatesRefs = CArray.array();
		$hostsRefs = CArray.array();
		$applicationsRefs = CArray.array();
		$itemsRefs = CArray.array();
		$valueMapsRefs = CArray.array();
		$triggersRefs = CArray.array();
		$iconMapsRefs = CArray.array();
		$mapsRefs = CArray.array();
		$screensRefs = CArray.array();
		$macrosRefs = CArray.array();
		$proxyRefs = CArray.array();
		$hostPrototypeRefs = CArray.array();

		for(getFormattedGroups() as $group) {
			$groupsRefs[$group["name"]] = Nest.value($group,"name").$();
		}

		for(getFormattedTemplates() as $template) {
			$templatesRefs[$template["host"]] = Nest.value($template,"host").$();

			for(Nest.value($template,"groups").$() as $group) {
				$groupsRefs[$group["name"]] = Nest.value($group,"name").$();
			}

			for(Nest.value($template,"macros").$() as $macro) {
				$macrosRefs[$template["host"]][$macro["macro"]] = Nest.value($macro,"macro").$();
			}

			if (!empty(Nest.value($template,"templates").$())) {
				for(Nest.value($template,"templates").$() as $linkedTemplate) {
					$templatesRefs[$linkedTemplate["name"]] = Nest.value($linkedTemplate,"name").$();
				}
			}
		}

		for(getFormattedHosts() as $host) {
			$hostsRefs[$host["host"]] = Nest.value($host,"host").$();

			for(Nest.value($host,"groups").$() as $group) {
				$groupsRefs[$group["name"]] = Nest.value($group,"name").$();
			}

			for(Nest.value($host,"macros").$() as $macro) {
				$macrosRefs[$host["host"]][$macro["macro"]] = Nest.value($macro,"macro").$();
			}

			if (!empty(Nest.value($host,"templates").$())) {
				for(Nest.value($host,"templates").$() as $linkedTemplate) {
					$templatesRefs[$linkedTemplate["name"]] = Nest.value($linkedTemplate,"name").$();
				}
			}
			if (!empty(Nest.value($host,"proxy").$())) {
				$proxyRefs[$host["proxy"]["name"]] = Nest.value($host,"proxy","name").$();
			}
		}

		for(getFormattedApplications() as $host => $applications) {
			for($applications as $app) {
				$applicationsRefs[$host][$app["name"]] = Nest.value($app,"name").$();
			}
		}

		for(getFormattedItems() as $host => $items) {
			for($items as $item) {
				$itemsRefs[$host][$item["key_"]] = Nest.value($item,"key_").$();

				for(Nest.value($item,"applications").$() as $app) {
					$applicationsRefs[$host][$app["name"]] = Nest.value($app,"name").$();
				}

				if (!empty(Nest.value($item,"valuemap").$())) {
					$valueMapsRefs[$item["valuemap"]["name"]] = Nest.value($item,"valuemap","name").$();
				}
			}
		}

		for(getFormattedDiscoveryRules() as $host => $discoveryRules) {
			for($discoveryRules as $discoveryRule) {
				$itemsRefs[$host][$discoveryRule["key_"]] = Nest.value($discoveryRule,"key_").$();

				for(Nest.value($discoveryRule,"item_prototypes").$() as $itemp) {
					$itemsRefs[$host][$itemp["key_"]] = Nest.value($itemp,"key_").$();

					for(Nest.value($itemp,"applications").$() as $app) {
						$applicationsRefs[$host][$app["name"]] = Nest.value($app,"name").$();
					}

					if (!empty(Nest.value($itemp,"valuemap").$())) {
						$valueMapsRefs[$itemp["valuemap"]["name"]] = Nest.value($itemp,"valuemap","name").$();
					}
				}
				for(Nest.value($discoveryRule,"trigger_prototypes").$() as $trigerp) {
					$triggersRefs[$trigerp["description"]][$trigerp["expression"]] = Nest.value($trigerp,"expression").$();
				}

				for(Nest.value($discoveryRule,"graph_prototypes").$() as $graph) {
					if (Nest.value($graph,"ymin_item_1").$()) {
						$yMinItem = Nest.value($graph,"ymin_item_1").$();
						$itemsRefs[$yMinItem["host"]][$yMinItem["key"]] = Nest.value($yMinItem,"key").$();
					}
					if (Nest.value($graph,"ymax_item_1").$()) {
						$yMaxItem = Nest.value($graph,"ymax_item_1").$();
						$itemsRefs[$yMaxItem["host"]][$yMaxItem["key"]] = Nest.value($yMaxItem,"key").$();
					}
					for(Nest.value($graph,"gitems").$() as $gitem) {
						$gitemItem = Nest.value($gitem,"item").$();
						$itemsRefs[$gitemItem["host"]][$gitemItem["key"]] = Nest.value($gitemItem,"key").$();
					}
				}

				for(Nest.value($discoveryRule,"host_prototypes").$() as $hostPrototype) {
					$hostPrototypeRefs[$host][$discoveryRule["key_"]][$hostPrototype["host"]] = Nest.value($hostPrototype,"host").$();

					for(Nest.value($hostPrototype,"group_prototypes").$() as $groupPrototype) {
						if (isset(Nest.value($groupPrototype,"group").$())) {
							$groupsRefs[$groupPrototype["group"]["name"]] = Nest.value($groupPrototype,"group","name").$();
						}
					}

					for(Nest.value($hostPrototype,"templates").$() as $template) {
						$templatesRefs[$template["name"]] = Nest.value($template,"name").$();
					}
				}
			}
		}

		for(getFormattedGraphs() as $graph) {
			if (Nest.value($graph,"ymin_item_1").$()) {
				$yMinItem = Nest.value($graph,"ymin_item_1").$();
				$hostsRefs[$yMinItem["host"]] = Nest.value($yMinItem,"host").$();
				$itemsRefs[$yMinItem["host"]][$yMinItem["key"]] = Nest.value($yMinItem,"key").$();
			}

			if (Nest.value($graph,"ymax_item_1").$()) {
				$yMaxItem = Nest.value($graph,"ymax_item_1").$();
				$hostsRefs[$yMaxItem["host"]] = Nest.value($yMaxItem,"host").$();
				$itemsRefs[$yMaxItem["host"]][$yMaxItem["key"]] = Nest.value($yMaxItem,"key").$();
			}

			if (isset(Nest.value($graph,"gitems").$()) && Nest.value($graph,"gitems").$()) {
				for(Nest.value($graph,"gitems").$() as $gitem) {
					$gitemItem = Nest.value($gitem,"item").$();
					$hostsRefs[$gitemItem["host"]] = Nest.value($gitemItem,"host").$();
					$itemsRefs[$gitemItem["host"]][$gitemItem["key"]] = Nest.value($gitemItem,"key").$();
				}
			}
		}

		for(getFormattedTriggers() as $trigger) {
			$triggersRefs[$trigger["description"]][$trigger["expression"]] = Nest.value($trigger,"expression").$();

			for(Nest.value($trigger,"dependencies").$() as $dependency) {
				$triggersRefs[$dependency["name"]][$dependency["expression"]] = Nest.value($dependency,"expression").$();
			}
		}

		for(getFormattedMaps() as $map) {
			$mapsRefs[$map["name"]] = Nest.value($map,"name").$();

			if (!empty(Nest.value($map,"iconmap").$())) {
				$iconMapsRefs[$map["iconmap"]["name"]] = Nest.value($map,"iconmap","name").$();
			}

			if (isset(Nest.value($map,"selements").$())) {
				for(Nest.value($map,"selements").$() as $selement) {
					switch (Nest.value($selement,"elementtype").$()) {
						case SYSMAP_ELEMENT_TYPE_MAP:
							$mapsRefs[$selement["element"]["name"]] = Nest.value($selement,"element","name").$();
							break;

						case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
							$groupsRefs[$selement["element"]["name"]] = Nest.value($selement,"element","name").$();
							break;

						case SYSMAP_ELEMENT_TYPE_HOST:
							$hostsRefs[$selement["element"]["host"]] = Nest.value($selement,"element","host").$();
							break;

						case SYSMAP_ELEMENT_TYPE_TRIGGER:
							$el = Nest.value($selement,"element").$();
							$triggersRefs[$el["description"]][$el["expression"]] = Nest.value($el,"expression").$();
							break;
					}
				}
			}

			if (isset(Nest.value($map,"links").$())) {
				for(Nest.value($map,"links").$() as $link) {
					if (isset(Nest.value($link,"linktriggers").$())) {
						for(Nest.value($link,"linktriggers").$() as $linkTrigger) {
							$t = Nest.value($linkTrigger,"trigger").$();
							$triggersRefs[$t["description"]][$t["expression"]] = Nest.value($t,"expression").$();
						}
					}
				}
			}
		}

		for(getFormattedScreens() as $screen) {
			$screensRefs[$screen["name"]] = Nest.value($screen,"name").$();

			if (!empty(Nest.value($screen,"screenitems").$())) {
				for(Nest.value($screen,"screenitems").$() as $screenItem) {
					$resource = Nest.value($screenItem,"resource").$();
					if (empty($resource)) {
						continue;
					}

					switch (Nest.value($screenItem,"resourcetype").$()) {
						case SCREEN_RESOURCE_HOSTS_INFO:
						case SCREEN_RESOURCE_TRIGGERS_INFO:
						case SCREEN_RESOURCE_TRIGGERS_OVERVIEW:
						case SCREEN_RESOURCE_DATA_OVERVIEW:
						case SCREEN_RESOURCE_HOSTGROUP_TRIGGERS:
							$groupsRefs[$resource["name"]] = Nest.value($resource,"name").$();
							break;

						case SCREEN_RESOURCE_HOST_TRIGGERS:
							$hostsRefs[$resource["host"]] = Nest.value($resource,"host").$();
							break;

						case SCREEN_RESOURCE_GRAPH:
							// TODO: gather graphs too
							break;

						case SCREEN_RESOURCE_SIMPLE_GRAPH:
						case SCREEN_RESOURCE_PLAIN_TEXT:
							$hostsRefs[$resource["host"]] = Nest.value($resource,"host").$();
							$itemsRefs[$resource["host"]][$resource["key"]] = Nest.value($resource,"key").$();
							break;

						case SCREEN_RESOURCE_MAP:
							$mapsRefs[$resource["name"]] = Nest.value($resource,"name").$();
							break;

						case SCREEN_RESOURCE_SCREEN:
							$screensRefs[$resource["name"]] = Nest.value($resource,"name").$();
							break;
					}
				}
			}
		}

		for(getFormattedTemplateScreens() as $screens) {
			for($screens as $screen) {
				if (!empty(Nest.value($screen,"screenitems").$())) {
					for(Nest.value($screen,"screenitems").$() as $screenItem) {
						$resource = Nest.value($screenItem,"resource").$();

						switch (Nest.value($screenItem,"resourcetype").$()) {
							case SCREEN_RESOURCE_GRAPH:
								// TODO: gather graphs too
								break;

							case SCREEN_RESOURCE_SIMPLE_GRAPH:
							case SCREEN_RESOURCE_PLAIN_TEXT:
								$hostsRefs[$resource["host"]] = Nest.value($resource,"host").$();
								$itemsRefs[$resource["host"]][$resource["key"]] = Nest.value($resource,"key").$();
								break;
						}
					}
				}
			}
		}

		referencer->addGroups($groupsRefs);
		referencer->addTemplates($templatesRefs);
		referencer->addHosts($hostsRefs);
		referencer->addApplications($applicationsRefs);
		referencer->addItems($itemsRefs);
		referencer->addValueMaps($valueMapsRefs);
		referencer->addTriggers($triggersRefs);
		referencer->addIconMaps($iconMapsRefs);
		referencer->addMaps($mapsRefs);
		referencer->addScreens($screensRefs);
		referencer->addMacros($macrosRefs);
		referencer->addProxies($proxyRefs);
		referencer->addHostPrototypes($hostPrototypeRefs);
	}

	/**
	 * Import groups.
	 */
	protected function processGroups() {
		$groups = getFormattedGroups();
		if (empty($groups)) {
			return;
		}

		// skip the groups that already exist
		for($groups as $gnum => $group) {
			if (referencer->resolveGroup(Nest.value($group,"name").$())) {
				unset($groups[$gnum]);
			}
		}

		if ($groups) {
			// reset indexing because ids from api does not preserve input array keys
			$groups = array_values($groups);
			$newGroups = API.HostGroup().create($groups);
			for(Nest.value($newGroups,"groupids").$() as $gnum => $groupid) {
				referencer->addGroupRef($groups[$gnum]["name"], $groupid);
			}
		}
	}

	/**
	 * Import templates.
	 *
	 * @throws Exception
	 */
	protected function processTemplates() {
		if ($templates = getFormattedTemplates()) {
			$templateImporter = new CTemplateImporter(options, referencer);
			$templateImporter.import($templates);
		}
	}

	/**
	 * Import hosts.
	 *
	 * @throws Exception
	 */
	protected function processHosts() {
		if ($hosts = getFormattedHosts()) {
			$hostImporter = new CHostImporter(options, referencer);
			$hostImporter.import($hosts);
		}
	}

	/**
	 * Import applications.
	 */
	protected function processApplications() {
		$allApplciations = getFormattedApplications();
		if (empty($allApplciations)) {
			return;
		}

		$applicationsToCreate = CArray.array();
		for($allApplciations as $host => $applications) {
			if (!referencer->isProcessedHost($host)) {
				continue;
			}

			$hostid = referencer->resolveHostOrTemplate($host);
			for($applications as $application) {
				Nest.value($application,"hostid").$() = $hostid;
				$appId = referencer->resolveApplication($hostid, Nest.value($application,"name").$());
				if (!$appId) {
					$applicationsToCreate[] = $application;
				}
			}
		}

		// create the applications and create a hash hostid->name->applicationid
		if (!empty($applicationsToCreate)) {
			$newApplicationsIds = API.Application().create($applicationsToCreate);
			for(Nest.value($newApplicationsIds,"applicationids").$() as $anum => $applicationId) {
				$application = $applicationsToCreate[$anum];
				referencer->addApplicationRef(Nest.value($application,"hostid").$(), Nest.value($application,"name").$(), $applicationId);
			}
		}

		// refresh applications because templated ones can be inherited to host and used in items
		referencer->refreshApplications();
	}

	/**
	 * Import items.
	 */
	protected function processItems() {
		$allItems = getFormattedItems();
		if (empty($allItems)) {
			return;
		}

		$itemsToCreate = CArray.array();
		$itemsToUpdate = CArray.array();
		for($allItems as $host => $items) {
			if (!referencer->isProcessedHost($host)) {
				continue;
			}

			$hostid = referencer->resolveHostOrTemplate($host);
			for($items as $item) {
				Nest.value($item,"hostid").$() = $hostid;

				if (isset(Nest.value($item,"applications").$()) && Nest.value($item,"applications").$()) {
					$applicationsIds = CArray.array();
					for(Nest.value($item,"applications").$() as $application) {
						if ($applicationId = referencer->resolveApplication($hostid, Nest.value($application,"name").$())) {
							$applicationsIds[] = $applicationId;
						}
						else {
							throw new Exception(_s("Item \"%1$s\" on \"%2$s\": application \"%3$s\" does not exist.",
								Nest.value($item,"name").$(), $host, Nest.value($application,"name").$()));
						}
					}
					Nest.value($item,"applications").$() = $applicationsIds;
				}

				if (isset(Nest.value($item,"interface_ref").$()) && Nest.value($item,"interface_ref").$()) {
					Nest.value($item,"interfaceid").$() = referencer->interfacesCache[$hostid][$item["interface_ref"]];
				}

				if (isset(Nest.value($item,"valuemap").$()) && Nest.value($item,"valuemap").$()) {
					$valueMapId = referencer->resolveValueMap(Nest.value($item,"valuemap","name").$());
					if (!$valueMapId) {
						throw new Exception(_s(
							"Cannot find value map \"%1$s\" used for item \"%2$s\" on \"%3$s\".",
							Nest.value($item,"valuemap","name").$(),
							Nest.value($item,"name").$(),
							$host
						));
					}
					Nest.value($item,"valuemapid").$() = $valueMapId;
				}

				$itemsId = referencer->resolveItem($hostid, Nest.value($item,"key_").$());

				if ($itemsId) {
					Nest.value($item,"itemid").$() = $itemsId;
					$itemsToUpdate[] = $item;
				}
				else {
					$itemsToCreate[] = $item;
				}
			}
		}

		// create/update the items and create a hash hostid->key_->itemid
		if (Nest.value(options,"items","createMissing").$() && $itemsToCreate) {
			$newItemsIds = API.Item().create($itemsToCreate);
			for(Nest.value($newItemsIds,"itemids").$() as $inum => $itemid) {
				$item = $itemsToCreate[$inum];
				referencer->addItemRef(Nest.value($item,"hostid").$(), Nest.value($item,"key_").$(), $itemid);
			}
		}
		if (Nest.value(options,"items","updateExisting").$() && $itemsToUpdate) {
			API.Item()->update($itemsToUpdate);
		}

		// refresh items because templated ones can be inherited to host and used in triggers, grahs, etc.
		referencer->refreshItems();
	}

	/**
	 * Import discovery rules.
	 *
	 * @throws Exception
	 */
	protected function processDiscoveryRules() {
		$allDiscoveryRules = getFormattedDiscoveryRules();
		if (empty($allDiscoveryRules)) {
			return;
		}

		// unset rules that are related to hosts we did not process
		for($allDiscoveryRules as $host => $discoveryRules) {
			if (!referencer->isProcessedHost($host)) {
				unset($allDiscoveryRules[$host]);
			}
		}

		$itemsToCreate = CArray.array();
		$itemsToUpdate = CArray.array();
		for($allDiscoveryRules as $host => $discoveryRules) {
			$hostid = referencer->resolveHostOrTemplate($host);
			for($discoveryRules as $item) {
				Nest.value($item,"hostid").$() = $hostid;

				if (isset(Nest.value($item,"interface_ref").$())) {
					Nest.value($item,"interfaceid").$() = referencer->interfacesCache[$hostid][$item["interface_ref"]];
				}
				unset(Nest.value($item,"item_prototypes").$());
				unset(Nest.value($item,"trigger_prototypes").$());
				unset(Nest.value($item,"graph_prototypes").$());
				unset(Nest.value($item,"host_prototypes").$());

				$itemId = referencer->resolveItem($hostid, Nest.value($item,"key_").$());
				if ($itemId) {
					Nest.value($item,"itemid").$() = $itemId;
					$itemsToUpdate[] = $item;
				}
				else {
					$itemsToCreate[] = $item;
				}
			}
		}

		// create/update discovery rules and add processed rules to array $processedRules
		$processedRules = CArray.array();
		if (Nest.value(options,"discoveryRules","createMissing").$() && $itemsToCreate) {
			$newItemsIds = API.DiscoveryRule().create($itemsToCreate);
			for(Nest.value($newItemsIds,"itemids").$() as $inum => $itemid) {
				$item = $itemsToCreate[$inum];
				referencer->addItemRef(Nest.value($item,"hostid").$(), Nest.value($item,"key_").$(), $itemid);
			}
			for($itemsToCreate as $item) {
				$processedRules[$item["hostid"]][$item["key_"]] = 1;
			}

		}
		if (Nest.value(options,"discoveryRules","updateExisting").$() && $itemsToUpdate) {
			API.DiscoveryRule()->update($itemsToUpdate);
			for($itemsToUpdate as $item) {
				$processedRules[$item["hostid"]][$item["key_"]] = 1;
			}
		}

		// refresh discovery rules because templated ones can be inherited to host and used for prototypes
		referencer->refreshItems();

		// process prototypes
		$prototypesToUpdate = CArray.array();
		$prototypesToCreate = CArray.array();
		$hostPrototypesToUpdate = CArray.array();
		$hostPrototypesToCreate = CArray.array();
		for($allDiscoveryRules as $host => $discoveryRules) {
			$hostid = referencer->resolveHostOrTemplate($host);
			for($discoveryRules as $item) {
				// if rule was not processed we should not create/update any of its prototypes
				if (!isset($processedRules[$hostid][$item["key_"]])) {
					continue;
				}

				Nest.value($item,"hostid").$() = $hostid;
				$itemId = referencer->resolveItem($hostid, Nest.value($item,"key_").$());

				// prototypes
				for(Nest.value($item,"item_prototypes").$() as $prototype) {
					Nest.value($prototype,"hostid").$() = $hostid;

					$applicationsIds = CArray.array();
					for(Nest.value($prototype,"applications").$() as $application) {
						$applicationsIds[] = referencer->resolveApplication($hostid, Nest.value($application,"name").$());
					}
					Nest.value($prototype,"applications").$() = $applicationsIds;

					if (isset(Nest.value($prototype,"interface_ref").$())) {
						Nest.value($prototype,"interfaceid").$() = referencer->interfacesCache[$hostid][$prototype["interface_ref"]];
					}

					if (Nest.value($prototype,"valuemap").$()) {
						$valueMapId = referencer->resolveValueMap(Nest.value($prototype,"valuemap","name").$());
						if (!$valueMapId) {
							throw new Exception(_s(
								"Cannot find value map \"%1$s\" used for item prototype \"%2$s\" of discovery rule \"%3$s\" on \"%4$s\".",
								Nest.value($prototype,"valuemap","name").$(),
								Nest.value($prototype,"name").$(),
								Nest.value($item,"name").$(),
								$host
							));
						}
						Nest.value($prototype,"valuemapid").$() = $valueMapId;
					}

					$prototypeId = referencer->resolveItem($hostid, Nest.value($prototype,"key_").$());
					Nest.value($prototype,"rule").$() = CArray.array("hostid" => $hostid, "key" => Nest.value($item,"key_").$());
					if ($prototypeId) {
						Nest.value($prototype,"itemid").$() = $prototypeId;
						$prototypesToUpdate[] = $prototype;
					}
					else {
						$prototypesToCreate[] = $prototype;
					}
				}

				// host prototype
				for(Nest.value($item,"host_prototypes").$() as $hostPrototype) {
					// resolve group prototypes
					$groupLinks = CArray.array();
					for(Nest.value($hostPrototype,"group_links").$() as $groupLink) {
						$groupId = referencer->resolveGroup(Nest.value($groupLink,"group","name").$());
						if (!$groupId) {
							throw new Exception(_s(
								"Cannot find host group \"%1$s\" for host prototype \"%2$s\" of discovery rule \"%3$s\" on \"%4$s\".",
								Nest.value($groupLink,"group","name").$(),
								Nest.value($hostPrototype,"name").$(),
								Nest.value($item,"name").$(),
								$host
							));
						}
						$groupLinks[] = CArray.array("groupid" => $groupId);
					}
					Nest.value($hostPrototype,"groupLinks").$() = $groupLinks;
					Nest.value($hostPrototype,"groupPrototypes").$() = Nest.value($hostPrototype,"group_prototypes").$();
					unset(Nest.value($hostPrototype,"group_links").$(), Nest.value($hostPrototype,"group_prototypes").$());

					// resolve templates
					$templates = CArray.array();
					for(Nest.value($hostPrototype,"templates").$() as $template) {
						$templateId = referencer->resolveTemplate(Nest.value($template,"name").$());
						if (!$templateId) {
							throw new Exception(_s(
								"Cannot find template \"%1$s\" for host prototype \"%2$s\" of discovery rule \"%3$s\" on \"%4$s\".",
								Nest.value($template,"name").$(),
								Nest.value($hostPrototype,"name").$(),
								Nest.value($item,"name").$(),
								$host
							));
						}
						$templates[] = CArray.array("templateid" => $templateId);
					}
					Nest.value($hostPrototype,"templates").$() = $templates;

					$hostPrototypeId = referencer->resolveHostPrototype($hostid, $itemId, Nest.value($hostPrototype,"host").$());
					if ($hostPrototypeId) {
						Nest.value($hostPrototype,"hostid").$() = $hostPrototypeId;
						$hostPrototypesToUpdate[] = $hostPrototype;
					}
					else {
						Nest.value($hostPrototype,"ruleid").$() = $itemId;
						$hostPrototypesToCreate[] = $hostPrototype;
					}
				}

				if (isset(Nest.value($item,"interface_ref").$())) {
					Nest.value($item,"interfaceid").$() = referencer->interfacesCache[$hostid][$item["interface_ref"]];
				}
				unset(Nest.value($item,"item_prototypes").$());
				unset(Nest.value($item,"trigger_prototypes").$());
				unset(Nest.value($item,"graph_prototypes").$());
				unset(Nest.value($item,"host_prototypes").$());

				$itemsId = referencer->resolveItem($hostid, Nest.value($item,"key_").$());
				if ($itemsId) {
					Nest.value($item,"itemid").$() = $itemsId;
					$itemsToUpdate[] = $item;
				}
				else {
					$itemsToCreate[] = $item;
				}
			}
		}

		if ($prototypesToCreate) {
			for($prototypesToCreate as &$prototype) {
				Nest.value($prototype,"ruleid").$() = referencer->resolveItem(Nest.value($prototype,"rule","hostid").$(), Nest.value($prototype,"rule","key").$());
			}
			unset($prototype);
			$newPrototypeIds = API.ItemPrototype().create($prototypesToCreate);
			for(Nest.value($newPrototypeIds,"itemids").$() as $inum => $itemid) {
				$item = $prototypesToCreate[$inum];
				referencer->addItemRef(Nest.value($item,"hostid").$(), Nest.value($item,"key_").$(), $itemid);
			}
		}
		if ($prototypesToUpdate) {
			for($prototypesToCreate as &$prototype) {
				Nest.value($prototype,"ruleid").$() = referencer->resolveItem(Nest.value($prototype,"rule","hostid").$(), Nest.value($prototype,"rule","key").$());
			}
			unset($prototype);

			API.ItemPrototype()->update($prototypesToUpdate);
		}

		if ($hostPrototypesToCreate) {
			API.HostPrototype()->create($hostPrototypesToCreate);
		}
		if ($hostPrototypesToUpdate) {
			API.HostPrototype()->update($hostPrototypesToUpdate);
		}

		// refresh prototypes because templated ones can be inherited to host and used in triggers prototypes or graph prototypes
		referencer->refreshItems();

		// first we need to create item prototypes and only then graph prototypes
		$triggersToCreate = CArray.array();
		$triggersToUpdate = CArray.array();
		$graphsToCreate = CArray.array();
		$graphsToUpdate = CArray.array();
		for($allDiscoveryRules as $host => $discoveryRules) {
			$hostid = referencer->resolveHostOrTemplate($host);
			for($discoveryRules as $item) {
				// if rule was not processed we should not create/update any of its prototypes
				if (!isset($processedRules[$hostid][$item["key_"]])) {
					continue;
				}

				// trigger prototypes
				for(Nest.value($item,"trigger_prototypes").$() as $trigger) {
					$triggerId = referencer->resolveTrigger(Nest.value($trigger,"description").$(), Nest.value($trigger,"expression").$());

					if ($triggerId) {
						Nest.value($trigger,"triggerid").$() = $triggerId;
						$triggersToUpdate[] = $trigger;
					}
					else {
						$triggersToCreate[] = $trigger;
					}
				}

				// graph prototypes
				for(Nest.value($item,"graph_prototypes").$() as $graph) {
					$graphHostIds = CArray.array();

					if (Nest.value($graph,"ymin_item_1").$()) {
						$hostId = referencer->resolveHostOrTemplate(Nest.value($graph,"ymin_item_1","host").$());
						$itemId = ($hostId)
							? referencer->resolveItem($hostId, Nest.value($graph,"ymin_item_1","key").$())
							: false;

						if (!$itemId) {
							throw new Exception(_s(
								"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MIN value for graph prototype \"%3$s\" of discovery rule \"%4$s\" on \"%5$s\".",
								Nest.value($graph,"ymin_item_1","key").$(),
								Nest.value($graph,"ymin_item_1","host").$(),
								Nest.value($graph,"name").$(),
								Nest.value($item,"name").$(),
								$host
							));
						}

						Nest.value($graph,"ymin_itemid").$() = $itemId;
					}

					if (Nest.value($graph,"ymax_item_1").$()) {
						$hostId = referencer->resolveHostOrTemplate(Nest.value($graph,"ymax_item_1","host").$());
						$itemId = ($hostId)
							? referencer->resolveItem($hostId, Nest.value($graph,"ymax_item_1","key").$())
							: false;

						if (!$itemId) {
							throw new Exception(_s(
								"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MAX value for graph prototype \"%3$s\" of discovery rule \"%4$s\" on \"%5$s\".",
								Nest.value($graph,"ymax_item_1","key").$(),
								Nest.value($graph,"ymax_item_1","host").$(),
								Nest.value($graph,"name").$(),
								Nest.value($item,"name").$(),
								$host
							));
						}

						Nest.value($graph,"ymax_itemid").$() = $itemId;
					}


					for(Nest.value($graph,"gitems").$() as &$gitem) {
						if (!$gitemHostId = referencer->resolveHostOrTemplate(Nest.value($gitem,"item","host").$())) {
							throw new Exception(_s("Cannot find host or template \"%1$s\" used in graph \"%2$s\".",
								Nest.value($gitem,"item","host").$(), Nest.value($graph,"name").$()));
						}

						Nest.value($gitem,"itemid").$() = referencer->resolveItem($gitemHostId, Nest.value($gitem,"item","key").$());

						$graphHostIds[$gitemHostId] = $gitemHostId;
					}
					unset($gitem);


					// TODO: do this for all graphs at once
					$sql = "SELECT g.graphid".
							" FROM graphs g,graphs_items gi,items i".
							" WHERE g.graphid=gi.graphid".
								" AND gi.itemid=i.itemid".
								" AND g.name=".zbx_dbstr(Nest.value($graph,"name").$()).
								" AND ".dbConditionInt("i.hostid", $graphHostIds);
					$graphExists = DBfetch(DBselect($sql));

					if ($graphExists) {
						$dbGraph = API.GraphPrototype().get(CArray.array(
							"graphids" => Nest.value($graphExists,"graphid").$(),
							"output" => CArray.array("graphid"),
							"editable" => true
						));
						if (empty($dbGraph)) {
							throw new Exception(_s("No permission for graph \"%1$s\".", Nest.value($graph,"name").$()));
						}
						Nest.value($graph,"graphid").$() = Nest.value($graphExists,"graphid").$();
						$graphsToUpdate[] = $graph;
					}
					else {
						$graphsToCreate[] = $graph;
					}
				}
			}
		}

		if ($triggersToCreate) {
			API.TriggerPrototype()->create($triggersToCreate);
		}
		if ($triggersToUpdate) {
			API.TriggerPrototype()->update($triggersToUpdate);
		}

		if ($graphsToCreate) {
			API.GraphPrototype()->create($graphsToCreate);
		}
		if ($graphsToUpdate) {
			API.GraphPrototype()->update($graphsToUpdate);
		}
	}

	/**
	 * Import graphs.
	 *
	 * @throws Exception
	 */
	protected function processGraphs() {
		$allGraphs = getFormattedGraphs();
		if (empty($allGraphs)) {
			return;
		}

		$graphsToCreate = CArray.array();
		$graphsToUpdate = CArray.array();
		for($allGraphs as $graph) {
			$graphHostIds = CArray.array();

			if (Nest.value($graph,"ymin_item_1").$()) {
				$hostId = referencer->resolveHostOrTemplate(Nest.value($graph,"ymin_item_1","host").$());
				$itemId = ($hostId)
					? referencer->resolveItem($hostId, Nest.value($graph,"ymin_item_1","key").$())
					: false;

				if (!$itemId) {
					throw new Exception(_s(
						"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MIN value for graph \"%3$s\".",
						Nest.value($graph,"ymin_item_1","key").$(),
						Nest.value($graph,"ymin_item_1","host").$(),
						$graph["name"]
					));
				}

				Nest.value($graph,"ymin_itemid").$() = $itemId;
			}

			if (Nest.value($graph,"ymax_item_1").$()) {
				$hostId = referencer->resolveHostOrTemplate(Nest.value($graph,"ymax_item_1","host").$());
				$itemId = ($hostId)
					? referencer->resolveItem($hostId, Nest.value($graph,"ymax_item_1","key").$())
					: false;

				if (!$itemId) {
					throw new Exception(_s(
						"Cannot find item \"%1$s\" on \"%2$s\" used as the Y axis MAX value for graph \"%3$s\".",
						Nest.value($graph,"ymax_item_1","key").$(),
						Nest.value($graph,"ymax_item_1","host").$(),
						$graph["name"]
					));
				}

				Nest.value($graph,"ymax_itemid").$() = $itemId;
			}

			if (isset(Nest.value($graph,"gitems").$()) && Nest.value($graph,"gitems").$()) {
				for(Nest.value($graph,"gitems").$() as &$gitem) {
					$gitemHostId = referencer->resolveHostOrTemplate(Nest.value($gitem,"item","host").$());

					if (!$gitemHostId) {
						throw new Exception(_s(
							"Cannot find host or template \"%1$s\" used in graph \"%2$s\".",
							Nest.value($gitem,"item","host").$(),
							$graph["name"]
						));
					}

					Nest.value($gitem,"itemid").$() = referencer->resolveItem($gitemHostId, Nest.value($gitem,"item","key").$());

					$graphHostIds[$gitemHostId] = $gitemHostId;
				}
				unset($gitem);
			}

			// TODO: do this for all graphs at once
			$sql = "SELECT g.graphid".
					" FROM graphs g,graphs_items gi,items i".
					" WHERE g.graphid=gi.graphid".
						" AND gi.itemid=i.itemid".
						" AND g.name=".zbx_dbstr(Nest.value($graph,"name").$()).
						" AND ".dbConditionInt("i.hostid", $graphHostIds);
			$graphExists = DBfetch(DBselect($sql));

			if ($graphExists) {
				$dbGraph = API.Graph().get(CArray.array(
					"graphids" => Nest.value($graphExists,"graphid").$(),
					"output" => CArray.array("graphid"),
					"editable" => true
				));
				if (empty($dbGraph)) {
					throw new Exception(_s("No permission for graph \"%1$s\".", Nest.value($graph,"name").$()));
				}
				Nest.value($graph,"graphid").$() = Nest.value($graphExists,"graphid").$();
				$graphsToUpdate[] = $graph;
			}
			else {
				$graphsToCreate[] = $graph;
			}
		}

		if (Nest.value(options,"graphs","createMissing").$() && $graphsToCreate) {
			API.Graph()->create($graphsToCreate);
		}
		if (Nest.value(options,"graphs","updateExisting").$() && $graphsToUpdate) {
			API.Graph()->update($graphsToUpdate);
		}
	}

	/**
	 * Import triggers.
	 */
	protected function processTriggers() {
		$allTriggers = getFormattedTriggers();
		if (empty($allTriggers)) {
			return;
		}

		$triggersToCreate = CArray.array();
		$triggersToUpdate = CArray.array();
		$triggersToCreateDependencies = CArray.array();
		for($allTriggers as $trigger) {
			$triggerId = referencer->resolveTrigger(Nest.value($trigger,"description").$(), Nest.value($trigger,"expression").$());

			if ($triggerId) {
				$deps = CArray.array();
				for(Nest.value($trigger,"dependencies").$() as $dependency) {
					$depTriggerId = referencer->resolveTrigger(Nest.value($dependency,"name").$(), Nest.value($dependency,"expression").$());
					if (!$depTriggerId) {
						throw new Exception(_s("Trigger \"%1$s\" depends on trigger \"%2$s\", which does not exist.", Nest.value($trigger,"description").$(), Nest.value($dependency,"name").$()));
					}
					$deps[] = CArray.array("triggerid" => $depTriggerId);
				}

				Nest.value($trigger,"dependencies").$() = $deps;
				Nest.value($trigger,"triggerid").$() = $triggerId;
				$triggersToUpdate[] = $trigger;
			}
			else {
				$triggersToCreateDependencies[] = Nest.value($trigger,"dependencies").$();
				unset(Nest.value($trigger,"dependencies").$());
				$triggersToCreate[] = $trigger;
			}
		}

		$triggerDependencies = CArray.array();
		$newTriggers = CArray.array();
		if (Nest.value(options,"triggers","createMissing").$() && $triggersToCreate) {
			$newTriggerIds = API.Trigger().create($triggersToCreate);
			for(Nest.value($newTriggerIds,"triggerids").$() as $tnum => $triggerId) {
				$trigger = $triggersToCreate[$tnum];
				referencer->addTriggerRef(Nest.value($trigger,"description").$(), Nest.value($trigger,"expression").$(), $triggerId);

				$newTriggers[$triggerId] = $trigger;
			}
		}

		// if we have new triggers with dependencies and they were created, create their dependencies
		if ($triggersToCreateDependencies && isset($newTriggerIds)) {
			for(Nest.value($newTriggerIds,"triggerids").$() as $tnum => $triggerId) {
				$deps = CArray.array();
				for($triggersToCreateDependencies[$tnum] as $dependency) {
					$depTriggerId = referencer->resolveTrigger(Nest.value($dependency,"name").$(), Nest.value($dependency,"expression").$());
					if (!$depTriggerId) {
						$trigger = $newTriggers[$triggerId];
						throw new Exception(_s("Trigger \"%1$s\" depends on trigger \"%2$s\", which does not exist.", Nest.value($trigger,"description").$(), Nest.value($dependency,"name").$()));
					}
					$deps[] = CArray.array("triggerid" => $depTriggerId);
				}

				if (!empty($deps)) {
					$triggerDependencies[] = CArray.array(
						"triggerid" => $triggerId,
						"dependencies" => $deps
					);
				}
			}
		}

		if (Nest.value(options,"triggers","updateExisting").$() && $triggersToUpdate) {
			API.Trigger()->update($triggersToUpdate);
		}

		if ($triggerDependencies) {
			API.Trigger()->update($triggerDependencies);
		}

		// refresh triggers because template triggers can be inherited to host and used in maps
		referencer->refreshTriggers();
	}

	/**
	 * Import images.
	 *
	 * @throws Exception
	 */
	protected function processImages() {
		$allImages = getFormattedImages();
		if (empty($allImages)) {
			return;
		}

		$imagesToUpdate = CArray.array();
		$allImages = zbx_toHash($allImages, "name");

		$dbImages = DBselect("SELECT i.imageid,i.name FROM images i WHERE ".dbConditionString("i.name", array_keys($allImages)));
		while ($dbImage = DBfetch($dbImages)) {
			Nest.value($dbImage,"image").$() = $allImages[$dbImage["name"]]["image"];
			$imagesToUpdate[] = $dbImage;
			unset($allImages[$dbImage["name"]]);
		}

		if (Nest.value(options,"images","createMissing").$()) {
			API.Image()->create(array_values($allImages));
		}

		if (Nest.value(options,"images","updateExisting").$()) {
			API.Image()->update($imagesToUpdate);
		}
	}

	/**
	 * Import maps.
	 */
	protected function processMaps() {
		if ($maps = getFormattedMaps()) {
			$mapImporter = new CMapImporter(options, referencer);
			$mapImporter.import($maps);
		}
	}

	/**
	 * Import screens.
	 */
	protected function processScreens() {
		if ($screens = getFormattedScreens()) {
			$screenImporter = new CScreenImporter(options, referencer);
			$screenImporter.import($screens);
		}
	}

	/**
	 * Import template screens.
	 */
	protected function processTemplateScreens() {
		if ($screens = getFormattedTemplateScreens()) {
			$screenImporter = new CTemplateScreenImporter(options, referencer);
			$screenImporter.import($screens);
		}
	}

	/**
	 * Method for creating an import formatter for the specified import version.
	 *
	 * @param string $version
	 *
	 * @return CImportFormatter
	 *
	 * @throws InvalidArgumentException
	 */
	protected function getFormatter($version) {
		switch ($version) {
			case "2.0":
				return new C20ImportFormatter;
			default:
				throw new InvalidArgumentException("Unknown import version.");
		}
	}

	/**
	 * Get configuration import version.
	 *
	 * @return string
	 */
	protected function getImportVersion() {
		if (isset(Nest.value(data,"zabbix_export","version").$())) {
			return Nest.value(data,"zabbix_export","version").$();
		}
		return "1.8";
	}

	/**
	 * Get formatted groups, if either \"createMissing\" groups option is true.
	 *
	 * @return array
	 */
	protected function getFormattedGroups() {
		if (!isset(Nest.value(formattedData,"groups").$())) {
			Nest.value(formattedData,"groups").$() = CArray.array();
			if (Nest.value(options,"groups","createMissing").$()) {
				Nest.value(formattedData,"groups").$() = formatter->getGroups();
			}
		}

		return Nest.value(formattedData,"groups").$();
	}

	/**
	 * Get formatted templates, if either \"createMissing\" or \"updateExisting\" templates option is true.
	 *
	 * @return array
	 */
	protected function getFormattedTemplates() {
		if (!isset(Nest.value(formattedData,"templates").$())) {
			Nest.value(formattedData,"templates").$() = CArray.array();
			if (Nest.value(options,"templates","updateExisting").$() || Nest.value(options,"templates","createMissing").$()) {
				Nest.value(formattedData,"templates").$() = formatter->getTemplates();
			}
		}

		return Nest.value(formattedData,"templates").$();
	}

	/**
	 * Get formatted hosts, if either \"createMissing\" or \"updateExisting\" hosts option is true.
	 *
	 * @return array
	 */
	protected function getFormattedHosts() {
		if (!isset(Nest.value(formattedData,"hosts").$())) {
			Nest.value(formattedData,"hosts").$() = CArray.array();
			if (Nest.value(options,"hosts","updateExisting").$() || Nest.value(options,"hosts","createMissing").$()) {
				Nest.value(formattedData,"hosts").$() = formatter->getHosts();
			}
		}

		return Nest.value(formattedData,"hosts").$();
	}

	/**
	 * Get formatted applications, if either \"createMissing\" or \"updateExisting\" applications option is true.
	 *
	 * @return array
	 */
	protected function getFormattedApplications() {
		if (!isset(Nest.value(formattedData,"applications").$())) {
			Nest.value(formattedData,"applications").$() = CArray.array();
			if (options["templates"]["updateExisting"]
					|| options["templates"]["createMissing"]
					|| options["hosts"]["updateExisting"]
					|| Nest.value(options,"hosts","createMissing").$()) {
				Nest.value(formattedData,"applications").$() = formatter->getApplications();
			}
		}

		return Nest.value(formattedData,"applications").$();
	}

	/**
	 * Get formatted items, if either \"createMissing\" or \"updateExisting\" items option is true.
	 *
	 * @return array
	 */
	protected function getFormattedItems() {
		if (!isset(Nest.value(formattedData,"items").$())) {
			Nest.value(formattedData,"items").$() = CArray.array();
			if (Nest.value(options,"items","updateExisting").$() || Nest.value(options,"items","createMissing").$()) {
				Nest.value(formattedData,"items").$() = formatter->getItems();
			}
		}

		return Nest.value(formattedData,"items").$();
	}

	/**
	 * Get formatted discovery rules, if either \"createMissing\" or \"updateExisting\" discovery rules option is true.
	 *
	 * @return array
	 */
	protected function getFormattedDiscoveryRules() {
		if (!isset(Nest.value(formattedData,"discoveryRules").$())) {
			Nest.value(formattedData,"discoveryRules").$() = CArray.array();
			if (Nest.value(options,"discoveryRules","updateExisting").$() || Nest.value(options,"discoveryRules","createMissing").$()) {
				Nest.value(formattedData,"discoveryRules").$() = formatter->getDiscoveryRules();
			}
		}

		return Nest.value(formattedData,"discoveryRules").$();
	}

	/**
	 * Get formatted triggers, if either \"createMissing\" or \"updateExisting\" triggers option is true.
	 *
	 * @return array
	 */
	protected function getFormattedTriggers() {
		if (!isset(Nest.value(formattedData,"triggers").$())) {
			Nest.value(formattedData,"triggers").$() = CArray.array();
			if (Nest.value(options,"triggers","updateExisting").$() || Nest.value(options,"triggers","createMissing").$()) {
				Nest.value(formattedData,"triggers").$() = formatter->getTriggers();
			}
		}

		return Nest.value(formattedData,"triggers").$();
	}

	/**
	 * Get formatted graphs, if either \"createMissing\" or \"updateExisting\" graphs option is true.
	 *
	 * @return array
	 */
	protected function getFormattedGraphs() {
		if (!isset(Nest.value(formattedData,"graphs").$())) {
			Nest.value(formattedData,"graphs").$() = CArray.array();
			if (Nest.value(options,"graphs","updateExisting").$() || Nest.value(options,"graphs","createMissing").$()) {
				Nest.value(formattedData,"graphs").$() = formatter->getGraphs();
			}
		}

		return Nest.value(formattedData,"graphs").$();
	}

	/**
	 * Get formatted images, if user is super admin and either \"createMissing\" or \"updateExisting\" images option is true.
	 *
	 * @return array
	 */
	protected function getFormattedImages() {
		if (!isset(Nest.value(formattedData,"images").$())) {
			Nest.value(formattedData,"images").$() = CArray.array();
			if (CWebUser::Nest.value($data,"type").$() == USER_TYPE_SUPER_ADMIN
					&& Nest.value(options,"images","updateExisting").$() || Nest.value(options,"images","createMissing").$()) {
				Nest.value(formattedData,"images").$() = formatter->getImages();
			}
		}

		return Nest.value(formattedData,"images").$();
	}

	/**
	 * Get formatted maps, if either \"createMissing\" or \"updateExisting\" maps option is true.
	 *
	 * @return array
	 */
	protected function getFormattedMaps() {
		if (!isset(Nest.value(formattedData,"maps").$())) {
			Nest.value(formattedData,"maps").$() = CArray.array();
			if (Nest.value(options,"maps","updateExisting").$() || Nest.value(options,"maps","createMissing").$()) {
				Nest.value(formattedData,"maps").$() = formatter->getMaps();
			}
		}

		return Nest.value(formattedData,"maps").$();
	}

	/**
	 * Get formatted screens, if either \"createMissing\" or \"updateExisting\" screens option is true.
	 *
	 * @return array
	 */
	protected function getFormattedScreens() {
		if (!isset(Nest.value(formattedData,"screens").$())) {
			Nest.value(formattedData,"screens").$() = CArray.array();
			if (Nest.value(options,"screens","updateExisting").$() || Nest.value(options,"screens","createMissing").$()) {
				Nest.value(formattedData,"screens").$() = formatter->getScreens();
			}
		}

		return Nest.value(formattedData,"screens").$();
	}

	/**
	 * Get formatted template screens, if either \"createMissing\" or \"updateExisting\" template screens option is true.
	 *
	 * @return array
	 */
	protected function getFormattedTemplateScreens() {
		if (!isset(Nest.value(formattedData,"templateScreens").$())) {
			Nest.value(formattedData,"templateScreens").$() = CArray.array();
			if (Nest.value(options,"templateScreens","updateExisting").$() || Nest.value(options,"templateScreens","createMissing").$()) {
				Nest.value(formattedData,"templateScreens").$() = formatter->getTemplateScreens();
			}
		}

		return Nest.value(formattedData,"templateScreens").$();
	}
}
