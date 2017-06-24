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
 * Import formatter for version 2.0
 */
class C20ImportFormatter extends CImportFormatter {

	public function getGroups() {
		if (!isset(Nest.value(data,"groups").$())) {
			return CArray.array();
		}
		return array_values(Nest.value(data,"groups").$());
	}

	public function getTemplates() {
		$templatesData = CArray.array();

		if (!empty(Nest.value(data,"templates").$())) {
			for(Nest.value(data,"templates").$() as $template) {
				$template = renameData($template, CArray.array("template" => "host"));

				CArrayHelper::convertFieldToArray($template, "templates");
				if (empty(Nest.value($template,"templates").$())) {
					unset(Nest.value($template,"templates").$());
				}
				CArrayHelper::convertFieldToArray($template, "macros");
				CArrayHelper::convertFieldToArray($template, "groups");

				CArrayHelper::convertFieldToArray($template, "screens");
				if (!empty(Nest.value($template,"screens").$())) {
					for(Nest.value($template,"screens").$() as &$screen) {
						$screen = renameData($screen, CArray.array("screen_items" => "screenitems"));
					}
					unset($screen);
				}


				$templatesData[] = CArrayHelper::getByKeys($template, CArray.array(
					"groups", "macros", "screens", "templates", "host", "status", "name"
				));
			}
		}

		return $templatesData;
	}

	public function getHosts() {
		$hostsData = CArray.array();

		if (!empty(Nest.value(data,"hosts").$())) {
			for(Nest.value(data,"hosts").$() as $host) {
				$host = renameData($host, CArray.array("proxyid" => "proxy_hostid"));

				CArrayHelper::convertFieldToArray($host, "interfaces");
				if (!empty(Nest.value($host,"interfaces").$())) {
					for(Nest.value($host,"interfaces").$() as $inum => $interface) {
						$host["interfaces"][$inum] = renameData($interface, CArray.array("default" => "main"));
					}
				}

				CArrayHelper::convertFieldToArray($host, "templates");
				if (empty(Nest.value($host,"templates").$())) {
					unset(Nest.value($host,"templates").$());
				}
				CArrayHelper::convertFieldToArray($host, "macros");
				CArrayHelper::convertFieldToArray($host, "groups");

				if (!empty(Nest.value($host,"inventory").$()) && isset(Nest.value($host,"inventory","inventory_mode").$())) {
					Nest.value($host,"inventory_mode").$() = Nest.value($host,"inventory","inventory_mode").$();
					unset(Nest.value($host,"inventory","inventory_mode").$());
				}
				else {
					Nest.value($host,"inventory_mode").$() = HOST_INVENTORY_DISABLED;
				}

				$hostsData[] = CArrayHelper::getByKeys($host, CArray.array(
					"inventory", "proxy", "groups", "templates", "macros", "interfaces", "host", "status",
					"ipmi_authtype", "ipmi_privilege", "ipmi_username", "ipmi_password", "name", "inventory_mode"
				));
			}
		}

		return $hostsData;
	}

	public function getApplications() {
		$applicationsData = CArray.array();

		if (isset(Nest.value(data,"hosts").$())) {
			for(Nest.value(data,"hosts").$() as $host) {
				if (!empty(Nest.value($host,"applications").$())) {
					for(Nest.value($host,"applications").$() as $application) {
						$applicationsData[$host["host"]][$application["name"]] = $application;
					}
				}
			}
		}
		if (isset(Nest.value(data,"templates").$())) {
			for(Nest.value(data,"templates").$() as $template) {
				if (!empty(Nest.value($template,"applications").$())) {
					for(Nest.value($template,"applications").$() as $application) {
						$applicationsData[$template["template"]][$application["name"]] = $application;
					}
				}
			}
		}

		return $applicationsData;
	}

	public function getItems() {
		$itemsData = CArray.array();

		if (isset(Nest.value(data,"hosts").$())) {
			for(Nest.value(data,"hosts").$() as $host) {
				if (!empty(Nest.value($host,"items").$())) {
					for(Nest.value($host,"items").$() as $item) {
						// if a host item has the \"Not supported\" status, convert it to \"Active\"
						if (Nest.value($item,"status").$() == ITEM_STATUS_NOTSUPPORTED) {
							Nest.value($item,"status").$() = ITEM_STATUS_ACTIVE;
						}

						$item = formatItem($item);
						$itemsData[$host["host"]][$item["key_"]] = $item;
					}
				}
			}
		}
		if (isset(Nest.value(data,"templates").$())) {
			for(Nest.value(data,"templates").$() as $template) {
				if (!empty(Nest.value($template,"items").$())) {
					for(Nest.value($template,"items").$() as $item) {
						$item = formatItem($item);
						$itemsData[$template["template"]][$item["key_"]] = $item;
					}
				}
			}
		}

		return $itemsData;
	}

	public function getDiscoveryRules() {
		$discoveryRulesData = CArray.array();

		if (isset(Nest.value(data,"hosts").$())) {
			for(Nest.value(data,"hosts").$() as $host) {
				if (!empty(Nest.value($host,"discovery_rules").$())) {
					for(Nest.value($host,"discovery_rules").$() as $item) {
						// if a discovery rule has the \"Not supported\" status, convert it to \"Active\"
						if (Nest.value($item,"status").$() == ITEM_STATUS_NOTSUPPORTED) {
							Nest.value($item,"status").$() = ITEM_STATUS_ACTIVE;
						}

						$item = formatDiscoveryRule($item);

						$discoveryRulesData[$host["host"]][$item["key_"]] = $item;
					}
				}
			}
		}

		if (isset(Nest.value(data,"templates").$())) {
			for(Nest.value(data,"templates").$() as $template) {
				if (!empty(Nest.value($template,"discovery_rules").$())) {
					for(Nest.value($template,"discovery_rules").$() as $item) {
						$item = formatDiscoveryRule($item);

						$discoveryRulesData[$template["template"]][$item["key_"]] = $item;
					}
				}
			}
		}

		return $discoveryRulesData;
	}

	public function getGraphs() {
		$graphsData = CArray.array();

		if (isset(Nest.value(data,"graphs").$()) && Nest.value(data,"graphs").$()) {
			for(Nest.value(data,"graphs").$() as $graph) {
				$graph = renameGraphFields($graph);

				if (isset(Nest.value($graph,"gitems").$()) && Nest.value($graph,"gitems").$()) {
					Nest.value($graph,"gitems").$() = array_values(Nest.value($graph,"gitems").$());
				}

				$graphsData[] = $graph;
			}
		}

		return $graphsData;
	}

	public function getTriggers() {
		$triggersData = CArray.array();

		if (!empty(Nest.value(data,"triggers").$())) {
			for(Nest.value(data,"triggers").$() as $trigger) {
				CArrayHelper::convertFieldToArray($trigger, "dependencies");
				$triggersData[] = renameTriggerFields($trigger);

			}
		}

		return $triggersData;
	}

	public function getImages() {
		$imagesData = CArray.array();

		if (!empty(Nest.value(data,"images").$())) {
			for(Nest.value(data,"images").$() as $image) {
				$imagesData[] = renameData($image, CArray.array("encodedImage" => "image"));
			}
		}

		return $imagesData;
	}

	public function getMaps() {
		$mapsData = CArray.array();

		if (!empty(Nest.value(data,"maps").$())) {
			for(Nest.value(data,"maps").$() as $map) {
				CArrayHelper::convertFieldToArray($map, "selements");
				for(Nest.value($map,"selements").$() as &$selement) {
					CArrayHelper::convertFieldToArray($selement, "urls");
				}
				unset($selement);

				CArrayHelper::convertFieldToArray($map, "links");
				for(Nest.value($map,"links").$() as &$link) {
					CArrayHelper::convertFieldToArray($link, "linktriggers");
				}
				unset($link);

				CArrayHelper::convertFieldToArray($map, "urls");

				$mapsData[] = $map;
			}
		}

		return $mapsData;
	}

	public function getScreens() {
		$screensData = CArray.array();

		if (!empty(Nest.value(data,"screens").$())) {
			for(Nest.value(data,"screens").$() as $screen) {
				$screen = renameData($screen, CArray.array("screen_items" => "screenitems"));
				CArrayHelper::convertFieldToArray($screen, "screenitems");
				$screensData[] = $screen;
			}
		}

		return $screensData;
	}

	public function getTemplateScreens() {
		$screensData = CArray.array();

		if (isset(Nest.value(data,"templates").$())) {
			for(Nest.value(data,"templates").$() as $template) {
				if (!empty(Nest.value($template,"screens").$())) {
					for(Nest.value($template,"screens").$() as $screen) {
						$screen = renameData($screen, CArray.array("screen_items" => "screenitems"));
						CArrayHelper::convertFieldToArray($screen, "screenitems");
						$screensData[$template["template"]][$screen["name"]] = $screen;
					}
				}
			}
		}

		return $screensData;
	}

	/**
	 * Format item.
	 *
	 * @param array $item
	 *
	 * @return array
	 */
	protected function formatItem(array $item) {
		$item = renameItemFields($item);

		if (empty(Nest.value($item,"applications").$())) {
			Nest.value($item,"applications").$() = CArray.array();
		}

		return $item;
	}

	/**
	 * Format discovery rule.
	 *
	 * @param array $discoveryRule
	 *
	 * @return array
	 */
	protected function formatDiscoveryRule(array $discoveryRule) {
		$discoveryRule = renameItemFields($discoveryRule);

		if (!empty(Nest.value($discoveryRule,"item_prototypes").$())) {
			for(Nest.value($discoveryRule,"item_prototypes").$() as &$prototype) {
				$prototype = renameItemFields($prototype);
				CArrayHelper::convertFieldToArray($prototype, "applications");
			}
			unset($prototype);
		}
		else {
			Nest.value($discoveryRule,"item_prototypes").$() = CArray.array();
		}

		if (!empty(Nest.value($discoveryRule,"trigger_prototypes").$())) {
			for(Nest.value($discoveryRule,"trigger_prototypes").$() as &$trigger) {
				$trigger = renameTriggerFields($trigger);
			}
			unset($trigger);
		}
		else {
			Nest.value($discoveryRule,"trigger_prototypes").$() = CArray.array();
		}

		if (!empty(Nest.value($discoveryRule,"graph_prototypes").$())) {
			for(Nest.value($discoveryRule,"graph_prototypes").$() as &$graph) {
				$graph = renameGraphFields($graph);
			}
			unset($graph);
		}
		else {
			Nest.value($discoveryRule,"graph_prototypes").$() = CArray.array();
		}

		if (!empty(Nest.value($discoveryRule,"host_prototypes").$())) {
			for(Nest.value($discoveryRule,"host_prototypes").$() as &$hostPrototype) {
				CArrayHelper::convertFieldToArray($hostPrototype, "group_prototypes");
				CArrayHelper::convertFieldToArray($hostPrototype, "templates");
			}
			unset($hostPrototype);
		}
		else {
			Nest.value($discoveryRule,"host_prototypes").$() = CArray.array();
		}

		return $discoveryRule;
	}

	/**
	 * Rename items, discovery rules, item prototypes fields.
	 *
	 * @param array $item
	 *
	 * @return array
	 */
	protected function renameItemFields(array $item) {
		return renameData($item, CArray.array("key" => "key_", "allowed_hosts" => "trapper_hosts"));
	}

	/**
	 * Rename triggers, trigger prototypes fields.
	 *
	 * @param array $trigger
	 *
	 * @return array
	 */
	protected function renameTriggerFields(array $trigger) {
		$trigger = renameData($trigger, CArray.array("description" => "comments"));
		return renameData($trigger, CArray.array("name" => "description", "severity" => "priority"));
	}

	/**
	 * Rename graphs, graph prototypes fields.
	 *
	 * @param array $graph
	 *
	 * @return array
	 */
	protected function renameGraphFields(array $graph) {
		return renameData($graph, CArray.array(
			"type" => "graphtype",
			"ymin_type_1" => "ymin_type",
			"ymax_type_1" => "ymax_type",
			"graph_items" => "gitems"
		));
	}
}
