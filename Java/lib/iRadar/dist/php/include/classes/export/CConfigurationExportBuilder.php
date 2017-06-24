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


class CConfigurationExportBuilder {

	const EXPORT_VERSION = "2.0";

	/**
	 * @var array
	 */
	protected $data = CArray.array();

	public function __construct() {
		Nest.value(data,"version").$() = self::EXPORT_VERSION;
		Nest.value(data,"date").$() = date("Y-m-d\TH:i:s\Z", time() - date("Z"));
	}

	/**
	 * Get array with formatted export data.
	 *
	 * @return array
	 */
	public function getExport() {
		return CArray.array("zabbix_export" => data);
	}

	/**
	 * Format groups.
	 *
	 * @param array $groups
	 */
	public function buildGroups(array $groups) {
		Nest.value(data,"groups").$() = formatGroups($groups);
	}

	/**
	 * Format templates.
	 *
	 * @param array $templates
	 */
	public function buildTemplates(array $templates) {
		order_result($templates, "host");
		Nest.value(data,"templates").$() = CArray.array();

		for($templates as $template) {
			data["templates"][] = CArray.array(
				"template" => Nest.value($template,"host").$(),
				"name" => Nest.value($template,"name").$(),
				"groups" => formatGroups(Nest.value($template,"groups").$()),
				"applications" => formatApplications(Nest.value($template,"applications").$()),
				"items" => formatItems(Nest.value($template,"items").$()),
				"discovery_rules" => formatDiscoveryRules(Nest.value($template,"discoveryRules").$()),
				"macros" => formatMacros(Nest.value($template,"macros").$()),
				"templates" => formatTemplateLinkage(Nest.value($template,"parentTemplates").$()),
				"screens" => formatScreens(Nest.value($template,"screens").$())
			);
		}
	}

	/**
	 * Format hosts.
	 *
	 * @param $hosts
	 */
	public function buildHosts($hosts) {
		order_result($hosts, "host");
		Nest.value(data,"hosts").$() = CArray.array();

		for($hosts as $host) {
			$host = createInterfaceReferences($host);

			data["hosts"][] = CArray.array(
				"host" => Nest.value($host,"host").$(),
				"name" => Nest.value($host,"name").$(),
				"proxy" => Nest.value($host,"proxy").$(),
				"status" => Nest.value($host,"status").$(),
				"ipmi_authtype" => Nest.value($host,"ipmi_authtype").$(),
				"ipmi_privilege" => Nest.value($host,"ipmi_privilege").$(),
				"ipmi_username" => Nest.value($host,"ipmi_username").$(),
				"ipmi_password" => Nest.value($host,"ipmi_password").$(),
				"templates" => formatTemplateLinkage(Nest.value($host,"parentTemplates").$()),
				"groups" => formatGroups(Nest.value($host,"groups").$()),
				"interfaces" => formatHostInterfaces(Nest.value($host,"interfaces").$()),
				"applications" => formatApplications(Nest.value($host,"applications").$()),
				"items" => formatItems(Nest.value($host,"items").$()),
				"discovery_rules" => formatDiscoveryRules(Nest.value($host,"discoveryRules").$()),
				"macros" => formatMacros(Nest.value($host,"macros").$()),
				"inventory" => formatHostInventory(Nest.value($host,"inventory").$())
			);
		}
	}

	/**
	 * Format graphs.
	 *
	 * @param array $graphs
	 */
	public function buildGraphs(array $graphs) {
		Nest.value(data,"graphs").$() = formatGraphs($graphs);
	}

	/**
	 * Format triggers.
	 *
	 * @param array $triggers
	 */
	public function buildTriggers(array $triggers) {
		Nest.value(data,"triggers").$() = formatTriggers($triggers);
	}

	/**
	 * Format screens.
	 *
	 * @param array $screens
	 */
	public function buildScreens(array $screens) {
		Nest.value(data,"screens").$() = formatScreens($screens);
	}

	/**
	 * Format images.
	 *
	 * @param array $images
	 */
	public function buildImages(array $images) {
		Nest.value(data,"images").$() = CArray.array();

		for($images as $image) {
			data["images"][] = CArray.array(
				"name" => Nest.value($image,"name").$(),
				"imagetype" => Nest.value($image,"imagetype").$(),
				"encodedImage" => $image["encodedImage"]
			);
		}
	}

	/**
	 * Format maps.
	 *
	 * @param array $maps
	 */
	public function buildMaps(array $maps) {
		order_result($maps, "name");
		Nest.value(data,"maps").$() = CArray.array();

		for($maps as $map) {
			data["maps"][] = CArray.array(
				"name" => Nest.value($map,"name").$(),
				"width" => Nest.value($map,"width").$(),
				"height" => Nest.value($map,"height").$(),
				"label_type" => Nest.value($map,"label_type").$(),
				"label_location" => Nest.value($map,"label_location").$(),
				"highlight" => Nest.value($map,"highlight").$(),
				"expandproblem" => Nest.value($map,"expandproblem").$(),
				"markelements" => Nest.value($map,"markelements").$(),
				"show_unack" => Nest.value($map,"show_unack").$(),
				"severity_min" => Nest.value($map,"severity_min").$(),
				"grid_size" => Nest.value($map,"grid_size").$(),
				"grid_show" => Nest.value($map,"grid_show").$(),
				"grid_align" => Nest.value($map,"grid_align").$(),
				"label_format" => Nest.value($map,"label_format").$(),
				"label_type_host" => Nest.value($map,"label_type_host").$(),
				"label_type_hostgroup" => Nest.value($map,"label_type_hostgroup").$(),
				"label_type_trigger" => Nest.value($map,"label_type_trigger").$(),
				"label_type_map" => Nest.value($map,"label_type_map").$(),
				"label_type_image" => Nest.value($map,"label_type_image").$(),
				"label_string_host" => Nest.value($map,"label_string_host").$(),
				"label_string_hostgroup" => Nest.value($map,"label_string_hostgroup").$(),
				"label_string_trigger" => Nest.value($map,"label_string_trigger").$(),
				"label_string_map" => Nest.value($map,"label_string_map").$(),
				"label_string_image" => Nest.value($map,"label_string_image").$(),
				"expand_macros" => Nest.value($map,"expand_macros").$(),
				"background" => Nest.value($map,"backgroundid").$(),
				"iconmap" => Nest.value($map,"iconmap").$(),
				"urls" => formatMapUrls(Nest.value($map,"urls").$()),
				"selements" => formatMapElements(Nest.value($map,"selements").$()),
				"links" => formatMapLinks(Nest.value($map,"links").$())
			);
		}
	}

	/**
	 * For each host interface an unique reference must be created and then added for all items, discovery rules
	 * and item prototypes that use the interface.
	 *
	 * @param array $host
	 *
	 * @return array
	 */
	protected function createInterfaceReferences(array $host) {
		$references = CArray.array(
			"num" => 1,
			"refs" => CArray.array()
		);

		// create interface references
		for(Nest.value($host,"interfaces").$() as &$interface) {
			$refNum = $references["num"]++;
			$referenceKey = "if".$refNum;
			Nest.value($interface,"interface_ref").$() = $referenceKey;
			$references["refs"][$interface["interfaceid"]] = $referenceKey;
		}
		unset($interface);

		for(Nest.value($host,"items").$() as &$item) {
			if (Nest.value($item,"interfaceid").$()) {
				Nest.value($item,"interface_ref").$() = $references["refs"][$item["interfaceid"]];
			}
		}
		unset($item);

		for(Nest.value($host,"discoveryRules").$() as &$discoveryRule) {
			if (Nest.value($discoveryRule,"interfaceid").$()) {
				Nest.value($discoveryRule,"interface_ref").$() = $references["refs"][$discoveryRule["interfaceid"]];
			}

			for(Nest.value($discoveryRule,"itemPrototypes").$() as &$prototype) {
				if (Nest.value($prototype,"interfaceid").$()) {
					Nest.value($prototype,"interface_ref").$() = $references["refs"][$prototype["interfaceid"]];
				}
			}
			unset($prototype);
		}
		unset($discoveryRule);

		return $host;
	}

	/**
	 * Format discovery rules.
	 *
	 * @param array $discoveryRules
	 *
	 * @return array
	 */
	protected function formatDiscoveryRules(array $discoveryRules) {
		$result = CArray.array();
		order_result($discoveryRules, "name");

		for($discoveryRules as $discoveryRule) {
			$data = CArray.array(
				"name" => Nest.value($discoveryRule,"name").$(),
				"type" => Nest.value($discoveryRule,"type").$(),
				"snmp_community" => Nest.value($discoveryRule,"snmp_community").$(),
				"snmp_oid" => Nest.value($discoveryRule,"snmp_oid").$(),
				"key" => Nest.value($discoveryRule,"key_").$(),
				"delay" => Nest.value($discoveryRule,"delay").$(),
				"status" => Nest.value($discoveryRule,"status").$(),
				"allowed_hosts" => Nest.value($discoveryRule,"trapper_hosts").$(),
				"snmpv3_contextname" => Nest.value($discoveryRule,"snmpv3_contextname").$(),
				"snmpv3_securityname" => Nest.value($discoveryRule,"snmpv3_securityname").$(),
				"snmpv3_securitylevel" => Nest.value($discoveryRule,"snmpv3_securitylevel").$(),
				"snmpv3_authprotocol" => Nest.value($discoveryRule,"snmpv3_authprotocol").$(),
				"snmpv3_authpassphrase" => Nest.value($discoveryRule,"snmpv3_authpassphrase").$(),
				"snmpv3_privprotocol" => Nest.value($discoveryRule,"snmpv3_privprotocol").$(),
				"snmpv3_privpassphrase" => Nest.value($discoveryRule,"snmpv3_privpassphrase").$(),
				"delay_flex" => Nest.value($discoveryRule,"delay_flex").$(),
				"params" => Nest.value($discoveryRule,"params").$(),
				"ipmi_sensor" => Nest.value($discoveryRule,"ipmi_sensor").$(),
				"authtype" => Nest.value($discoveryRule,"authtype").$(),
				"username" => Nest.value($discoveryRule,"username").$(),
				"password" => Nest.value($discoveryRule,"password").$(),
				"publickey" => Nest.value($discoveryRule,"publickey").$(),
				"privatekey" => Nest.value($discoveryRule,"privatekey").$(),
				"port" => Nest.value($discoveryRule,"port").$(),
				"filter" => Nest.value($discoveryRule,"filter").$(),
				"lifetime" => Nest.value($discoveryRule,"lifetime").$(),
				"description" => Nest.value($discoveryRule,"description").$(),
				"item_prototypes" => formatItems(Nest.value($discoveryRule,"itemPrototypes").$()),
				"trigger_prototypes" => formatTriggers(Nest.value($discoveryRule,"triggerPrototypes").$()),
				"graph_prototypes" => formatGraphs(Nest.value($discoveryRule,"graphPrototypes").$()),
				"host_prototypes" => formatHostPrototypes(Nest.value($discoveryRule,"hostPrototypes").$())
			);
			if (isset(Nest.value($discoveryRule,"interface_ref").$())) {
				Nest.value($data,"interface_ref").$() = Nest.value($discoveryRule,"interface_ref").$();
			}
			$result[] = $data;
		}

		return $result;
	}

	/**
	 * Format host inventory.
	 *
	 * @param array $inventory
	 *
	 * @return array
	 */
	protected function formatHostInventory(array $inventory) {
		unset(Nest.value($inventory,"hostid").$());
		return $inventory;
	}

	/**
	 * Format graphs.
	 *
	 * @param array $graphs
	 *
	 * @return array
	 */
	protected function formatGraphs(array $graphs) {
		$result = CArray.array();
		order_result($graphs, "name");

		for($graphs as $graph) {
			$result[] = CArray.array(
				"name" => Nest.value($graph,"name").$(),
				"width" => Nest.value($graph,"width").$(),
				"height" => Nest.value($graph,"height").$(),
				"yaxismin" => Nest.value($graph,"yaxismin").$(),
				"yaxismax" => Nest.value($graph,"yaxismax").$(),
				"show_work_period" => Nest.value($graph,"show_work_period").$(),
				"show_triggers" => Nest.value($graph,"show_triggers").$(),
				"type" => Nest.value($graph,"graphtype").$(),
				"show_legend" => Nest.value($graph,"show_legend").$(),
				"show_3d" => Nest.value($graph,"show_3d").$(),
				"percent_left" => Nest.value($graph,"percent_left").$(),
				"percent_right" => Nest.value($graph,"percent_right").$(),
				"ymin_type_1" => Nest.value($graph,"ymin_type").$(),
				"ymax_type_1" => Nest.value($graph,"ymax_type").$(),
				"ymin_item_1" => Nest.value($graph,"ymin_itemid").$(),
				"ymax_item_1" => Nest.value($graph,"ymax_itemid").$(),
				"graph_items" => formatGraphItems(Nest.value($graph,"gitems").$())
			);
		}

		return $result;
	}

	/**
	 * Format host prototypes.
	 *
	 * @param array $hostPrototypes
	 *
	 * @return array
	 */
	protected function formatHostPrototypes(array $hostPrototypes) {
		$result = CArray.array();
		order_result($hostPrototypes, "host");

		for($hostPrototypes as $hostPrototype) {
			$result[] = CArray.array(
				"host" => Nest.value($hostPrototype,"host").$(),
				"name" => Nest.value($hostPrototype,"name").$(),
				"status" => Nest.value($hostPrototype,"status").$(),
				"group_links" => formatGroupLinks(Nest.value($hostPrototype,"groupLinks").$()),
				"group_prototypes" => formatGroupPrototypes(Nest.value($hostPrototype,"groupPrototypes").$()),
				"templates" => formatTemplateLinkage(Nest.value($hostPrototype,"templates").$())
			);
		}

		return $result;
	}

	/**
	 * Format group links.
	 *
	 * @param array $groupLinks
	 *
	 * @return array
	 */
	protected function formatGroupLinks(array $groupLinks) {
		$result = CArray.array();

		for($groupLinks as $groupLink) {
			$result[] = CArray.array(
				"group" => Nest.value($groupLink,"groupid").$(),
			);
		}

		return $result;
	}

	/**
	 * Format group prototypes.
	 *
	 * @param array $groupPrototypes
	 *
	 * @return array
	 */
	protected function formatGroupPrototypes(array $groupPrototypes) {
		$result = CArray.array();

		for($groupPrototypes as $groupPrototype) {
			$result[] = CArray.array(
				"name" => $groupPrototype["name"]
			);
		}

		return $result;
	}

	/**
	 * Format template linkage.
	 *
	 * @param array $templates
	 *
	 * @return array
	 */
	protected function formatTemplateLinkage(array $templates) {
		$result = CArray.array();
		order_result($templates, "host");

		for($templates as $template) {
			$result[] = CArray.array(
				"name" => $template["host"]
			);
		}

		return $result;
	}

	/**
	 * Format triggers.
	 *
	 * @param array $triggers
	 *
	 * @return array
	 */
	protected function formatTriggers(array $triggers) {
		order_result($triggers, "description");

		$result = CArray.array();
		for($triggers as $trigger) {
			$tr = CArray.array(
				"expression" => Nest.value($trigger,"expression").$(),
				"name" => Nest.value($trigger,"description").$(),
				"url" => Nest.value($trigger,"url").$(),
				"status" => Nest.value($trigger,"status").$(),
				"priority" => Nest.value($trigger,"priority").$(),
				"description" => Nest.value($trigger,"comments").$(),
				"type" => $trigger["type"]
			);
			if (isset(Nest.value($trigger,"dependencies").$())) {
				Nest.value($tr,"dependencies").$() = formatDependencies(Nest.value($trigger,"dependencies").$());
			}

			$result[] = $tr;
		}

		return $result;
	}

	/**
	 * Format host interfaces.
	 *
	 * @param array $interfaces
	 *
	 * @return array
	 */
	protected function formatHostInterfaces(array $interfaces) {
		$result = CArray.array();
		order_result($interfaces, "ip");

		for($interfaces as $interface) {
			$result[] = CArray.array(
				"default" => Nest.value($interface,"main").$(),
				"type" => Nest.value($interface,"type").$(),
				"useip" => Nest.value($interface,"useip").$(),
				"ip" => Nest.value($interface,"ip").$(),
				"dns" => Nest.value($interface,"dns").$(),
				"port" => Nest.value($interface,"port").$(),
				"interface_ref" => $interface["interface_ref"]
			);
		}

		return $result;
	}

	/**
	 * Format groups.
	 *
	 * @param array $groups
	 *
	 * @return array
	 */
	protected function formatGroups(array $groups) {
		$result = CArray.array();
		order_result($groups, "name");

		for($groups as $group) {
			$result[] = CArray.array(
				"name" => $group["name"]
			);
		}

		return $result;
	}

	/**
	 * Format items.
	 *
	 * @param array $items
	 *
	 * @return array
	 */
	protected function formatItems(array $items) {
		$result = CArray.array();
		order_result($items, "name");

		for($items as $item) {
			$data = CArray.array(
				"name" => Nest.value($item,"name").$(),
				"type" => Nest.value($item,"type").$(),
				"snmp_community" => Nest.value($item,"snmp_community").$(),
				"multiplier" => Nest.value($item,"multiplier").$(),
				"snmp_oid" => Nest.value($item,"snmp_oid").$(),
				"key" => Nest.value($item,"key_").$(),
				"delay" => Nest.value($item,"delay").$(),
				"history" => Nest.value($item,"history").$(),
				"trends" => Nest.value($item,"trends").$(),
				"status" => Nest.value($item,"status").$(),
				"value_type" => Nest.value($item,"value_type").$(),
				"allowed_hosts" => Nest.value($item,"trapper_hosts").$(),
				"units" => Nest.value($item,"units").$(),
				"delta" => Nest.value($item,"delta").$(),
				"snmpv3_contextname" => Nest.value($item,"snmpv3_contextname").$(),
				"snmpv3_securityname" => Nest.value($item,"snmpv3_securityname").$(),
				"snmpv3_securitylevel" => Nest.value($item,"snmpv3_securitylevel").$(),
				"snmpv3_authprotocol" => Nest.value($item,"snmpv3_authprotocol").$(),
				"snmpv3_authpassphrase" => Nest.value($item,"snmpv3_authpassphrase").$(),
				"snmpv3_privprotocol" => Nest.value($item,"snmpv3_privprotocol").$(),
				"snmpv3_privpassphrase" => Nest.value($item,"snmpv3_privpassphrase").$(),
				"formula" => Nest.value($item,"formula").$(),
				"delay_flex" => Nest.value($item,"delay_flex").$(),
				"params" => Nest.value($item,"params").$(),
				"ipmi_sensor" => Nest.value($item,"ipmi_sensor").$(),
				"data_type" => Nest.value($item,"data_type").$(),
				"authtype" => Nest.value($item,"authtype").$(),
				"username" => Nest.value($item,"username").$(),
				"password" => Nest.value($item,"password").$(),
				"publickey" => Nest.value($item,"publickey").$(),
				"privatekey" => Nest.value($item,"privatekey").$(),
				"port" => Nest.value($item,"port").$(),
				"description" => Nest.value($item,"description").$(),
				"inventory_link" => Nest.value($item,"inventory_link").$(),
				"applications" => formatApplications(Nest.value($item,"applications").$()),
				"valuemap" => $item["valuemap"]
			);
			if (isset(Nest.value($item,"interface_ref").$())) {
				Nest.value($data,"interface_ref").$() = Nest.value($item,"interface_ref").$();
			}
			$result[] = $data;
		}

		return $result;
	}

	/**
	 * Format applications.
	 *
	 * @param array $applications
	 *
	 * @return array
	 */
	protected function formatApplications(array $applications) {
		$result = CArray.array();
		order_result($applications, "name");

		for($applications as $application) {
			$result[] = CArray.array(
				"name" => $application["name"]
			);
		}

		return $result;
	}

	/**
	 * Format macros.
	 *
	 * @param array $macros
	 *
	 * @return array
	 */
	protected function formatMacros(array $macros) {
		$result = CArray.array();
		$macros = order_macros($macros, "macro");

		for($macros as $macro) {
			$result[] = CArray.array(
				"macro" => Nest.value($macro,"macro").$(),
				"value" => $macro["value"]
			);
		}

		return $result;
	}

	/**
	 * Format screens.
	 *
	 * @param array $screens
	 *
	 * @return array
	 */
	protected function formatScreens(array $screens) {
		$result = CArray.array();
		order_result($screens, "name");

		for($screens as $screen) {
			$result[] = CArray.array(
				"name" => Nest.value($screen,"name").$(),
				"hsize" => Nest.value($screen,"hsize").$(),
				"vsize" => Nest.value($screen,"vsize").$(),
				"screen_items" => formatScreenItems(Nest.value($screen,"screenitems").$())
			);
		}

		return $result;
	}

	/**
	 * Format trigger dependencies.
	 *
	 * @param array $dependencies
	 *
	 * @return array
	 */
	protected function formatDependencies(array $dependencies) {
		$result = CArray.array();

		for($dependencies as $dependency) {
			$result[] = CArray.array(
				"name" => Nest.value($dependency,"description").$(),
				"expression" => $dependency["expression"]
			);
		}

		return $result;
	}

	/**
	 * Format screen items.
	 *
	 * @param array $screenItems
	 *
	 * @return array
	 */
	protected function formatScreenItems(array $screenItems) {
		$result = CArray.array();

		for($screenItems as $screenItem) {
			$result[] = CArray.array(
				"resourcetype" => Nest.value($screenItem,"resourcetype").$(),
				"width" => Nest.value($screenItem,"width").$(),
				"height" => Nest.value($screenItem,"height").$(),
				"x" => Nest.value($screenItem,"x").$(),
				"y" => Nest.value($screenItem,"y").$(),
				"colspan" => Nest.value($screenItem,"colspan").$(),
				"rowspan" => Nest.value($screenItem,"rowspan").$(),
				"elements" => Nest.value($screenItem,"elements").$(),
				"valign" => Nest.value($screenItem,"valign").$(),
				"halign" => Nest.value($screenItem,"halign").$(),
				"style" => Nest.value($screenItem,"style").$(),
				"url" => Nest.value($screenItem,"url").$(),
				"dynamic" => Nest.value($screenItem,"dynamic").$(),
				"sort_triggers" => Nest.value($screenItem,"sort_triggers").$(),
				"resource" => Nest.value($screenItem,"resourceid").$(),
				"application" => $screenItem["application"]
			);
		}

		return $result;
	}

	/**
	 * Format graph items.
	 *
	 * @param array $graphItems
	 *
	 * @return array
	 */
	protected function formatGraphItems(array $graphItems) {
		$result = CArray.array();

		for($graphItems as $graphItem) {
			$result[] = CArray.array(
				"sortorder"=> Nest.value($graphItem,"sortorder").$(),
				"drawtype"=> Nest.value($graphItem,"drawtype").$(),
				"color"=> Nest.value($graphItem,"color").$(),
				"yaxisside"=> Nest.value($graphItem,"yaxisside").$(),
				"calc_fnc"=> Nest.value($graphItem,"calc_fnc").$(),
				"type"=> Nest.value($graphItem,"type").$(),
				"item"=> $graphItem["itemid"]
			);
		}

		return $result;
	}

	/**
	 * Format map urls.
	 *
	 * @param array $urls
	 *
	 * @return array
	 */
	protected function formatMapUrls(array $urls) {
		$result = CArray.array();
		for($urls as $url) {
			$result[] = CArray.array(
				"name" => Nest.value($url,"name").$(),
				"url" => Nest.value($url,"url").$(),
				"elementtype" => $url["elementtype"]
			);
		}

		return $result;
	}

	/**
	 * Format map element urls.
	 *
	 * @param array $urls
	 *
	 * @return array
	 */
	protected function formatMapElementUrls(array $urls) {
		$result = CArray.array();

		for($urls as $url) {
			$result[] = CArray.array(
				"name" => Nest.value($url,"name").$(),
				"url" => $url["url"]
			);
		}

		return $result;
	}

	/**
	 * Format map links.
	 *
	 * @param array $links
	 *
	 * @return array
	 */
	protected function formatMapLinks(array $links) {
		$result = CArray.array();

		for($links as $link) {
			$result[] = CArray.array(
				"drawtype" => Nest.value($link,"drawtype").$(),
				"color" => Nest.value($link,"color").$(),
				"label" => Nest.value($link,"label").$(),
				"selementid1" => Nest.value($link,"selementid1").$(),
				"selementid2" => Nest.value($link,"selementid2").$(),
				"linktriggers" => formatMapLinkTriggers(Nest.value($link,"linktriggers").$())
			);
		}

		return $result;
	}

	/**
	 * Format map link triggers.
	 *
	 * @param array $linktriggers
	 *
	 * @return array
	 */
	protected function formatMapLinkTriggers(array $linktriggers) {
		$result = CArray.array();

		for($linktriggers as $linktrigger) {
			$result[] = CArray.array(
				"drawtype" => Nest.value($linktrigger,"drawtype").$(),
				"color" => Nest.value($linktrigger,"color").$(),
				"trigger" => $linktrigger["triggerid"]
			);
		}

		return $result;
	}

	/**
	 * Format map elements.
	 *
	 * @param array $elements
	 *
	 * @return array
	 */
	protected function formatMapElements(array $elements) {
		$result = CArray.array();
		for($elements as $element) {
			$result[] = CArray.array(
				"elementtype" => Nest.value($element,"elementtype").$(),
				"label" => Nest.value($element,"label").$(),
				"label_location" => Nest.value($element,"label_location").$(),
				"x" => Nest.value($element,"x").$(),
				"y" => Nest.value($element,"y").$(),
				"elementsubtype" => Nest.value($element,"elementsubtype").$(),
				"areatype" => Nest.value($element,"areatype").$(),
				"width" => Nest.value($element,"width").$(),
				"height" => Nest.value($element,"height").$(),
				"viewtype" => Nest.value($element,"viewtype").$(),
				"use_iconmap" => Nest.value($element,"use_iconmap").$(),
				"selementid" => Nest.value($element,"selementid").$(),
				"element" => Nest.value($element,"elementid").$(),
				"icon_off" => Nest.value($element,"iconid_off").$(),
				"icon_on" => Nest.value($element,"iconid_on").$(),
				"icon_disabled" => Nest.value($element,"iconid_disabled").$(),
				"icon_maintenance" => Nest.value($element,"iconid_maintenance").$(),
				"urls" => formatMapElementUrls(Nest.value($element,"urls").$())
			);
		}

		return $result;
	}
}
