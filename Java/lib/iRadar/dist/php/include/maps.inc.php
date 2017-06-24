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


function sysmap_element_types($type = null) {
	$types = CArray.array(
		SYSMAP_ELEMENT_TYPE_HOST => _("Host"),
		SYSMAP_ELEMENT_TYPE_HOST_GROUP => _("Host group"),
		SYSMAP_ELEMENT_TYPE_TRIGGER => _("Trigger"),
		SYSMAP_ELEMENT_TYPE_MAP => _("Map"),
		SYSMAP_ELEMENT_TYPE_IMAGE => _("Image")
	);

	if (is_null($type)) {
		natsort($types);
		return $types;
	}
	elseif (isset($types[$type])) {
		return $types[$type];
	}
	else {
		return _("Unknown");
	}
}

function sysmapElementLabel($label = null) {
	$labels = CArray.array(
		MAP_LABEL_TYPE_LABEL => _("Label"),
		MAP_LABEL_TYPE_IP => _("IP address"),
		MAP_LABEL_TYPE_NAME => _("Element name"),
		MAP_LABEL_TYPE_STATUS => _("Status only"),
		MAP_LABEL_TYPE_NOTHING => _("Nothing"),
		MAP_LABEL_TYPE_CUSTOM => _("Custom label")
	);

	if (is_null($label)) {
		return $labels;
	}
	elseif (isset($labels[$label])) {
		return $labels[$label];
	}
	else {
		return false;
	}
}

/**
 * Create map area with submenu for sysmap elements.
 * In submenu gathered information about urls, scripts and submaps.
 *
 * @param array $sysmap
 * @param array $options
 * @param int   $options["severity_min"]
 *
 * @return CAreaMap
 */
function getActionMapBySysmap($sysmap, array $options = CArray.array()) {
	Nest.value($sysmap,"selements").$() = zbx_toHash(Nest.value($sysmap,"selements").$(), "selementid");
	Nest.value($sysmap,"links").$() = zbx_toHash(Nest.value($sysmap,"links").$(), "linkid");

	$actionMap = new CAreaMap("links".Nest.value($sysmap,"sysmapid").$());

	$areas = populateFromMapAreas($sysmap);
	$mapInfo = getSelementsInfo($sysmap, $options);
	processAreasCoordinates($sysmap, $areas, $mapInfo);

	$hostIds = CArray.array();
	for(Nest.value($sysmap,"selements").$() as $id => &$selement) {
		if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST) {
			$hostIds[$selement["elementid"]] = Nest.value($selement,"elementid").$();

			// expanding host URL macros again as some hosts were added from hostgroup areas
			// and automatic expanding only happens for elements that are defined for map in db
			for(Nest.value($selement,"urls").$() as $urlId => $url) {
				$selement["urls"][$urlId]["url"] = str_replace("{HOST.ID}", Nest.value($selement,"elementid").$(), Nest.value($url,"url").$());
			}
		}

		if (Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
			unset($sysmap["selements"][$id]);
		}
	}
	unset($selement);

	$hostScripts = API.Script().getScriptsByHosts($hostIds);

	$hosts = API.Host().get(CArray.array(
		"nodeids" => get_current_nodeid(true),
		"hostids" => $hostIds,
		"output" => CArray.array("status"),
		"nopermissions" => true,
		"preservekeys" => true,
		"selectScreens" => API_OUTPUT_COUNT
	));

	for(Nest.value($sysmap,"selements").$() as $elem) {
		$back = get_png_by_selement($mapInfo[$elem["selementid"]]);
		$area = new CArea(
			CArray.array(
				Nest.value($elem,"x").$(),
				Nest.value($elem,"y").$(),
				Nest.value($elem,"x").$() + imagesx($back),
				Nest.value($elem,"y").$() + imagesy($back)
			),
			"", "", "rect"
		);
		$area.addClass("menu-map");

		$hostId = null;
		$scripts = null;
		$gotos = null;

		switch (Nest.value($elem,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_HOST:
				$host = $hosts[$elem["elementid"]];

				if ($hostScripts[$elem["elementid"]]) {
					$hostId = Nest.value($elem,"elementid").$();
					$scripts = $hostScripts[$elem["elementid"]];
				}
				if ($hosts[$elem["elementid"]]["status"] == HOST_STATUS_MONITORED) {
					Nest.value($gotos,"triggerStatus").$() = CArray.array(
						"hostid" => Nest.value($elem,"elementid").$(),
						"show_severity" => isset(Nest.value($options,"severity_min").$()) ? Nest.value($options,"severity_min").$() : null
					);
				}
				if (Nest.value($host,"screens").$()) {
					Nest.value($gotos,"screens").$() = CArray.array(
						"hostid" => $host["hostid"]
					);
				}
				Nest.value($gotos,"inventory").$() = CArray.array(
					"hostid" => $host["hostid"]
				);
				break;

			case SYSMAP_ELEMENT_TYPE_MAP:
				Nest.value($gotos,"submap").$() = CArray.array(
					"sysmapid" => Nest.value($elem,"elementid").$(),
					"severity_min" => isset(Nest.value($options,"severity_min").$()) ? Nest.value($options,"severity_min").$() : null
				);
				break;

			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				Nest.value($gotos,"events").$() = CArray.array(
					"triggerid" => Nest.value($elem,"elementid").$(),
					"stime" => date(TIMESTAMP_FORMAT, time() - SEC_PER_WEEK),
					"period" => SEC_PER_WEEK
				);
				break;

			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				Nest.value($gotos,"triggerStatus").$() = CArray.array(
					"groupid" => Nest.value($elem,"elementid").$(),
					"hostid" => 0,
					"show_severity" => isset(Nest.value($options,"severity_min").$()) ? Nest.value($options,"severity_min").$() : null
				);
				break;
		}

		order_result(Nest.value($elem,"urls").$(), "name");

		$area.setMenuPopup(getMenuPopupMap($hostId, $scripts, $gotos, Nest.value($elem,"urls").$()));

		$actionMap.addItem($area);
	}

	return $actionMap;
}

function get_icon_center_by_selement($element, $info, $map) {
	$x = Nest.value($element,"x").$();
	$y = Nest.value($element,"y").$();

	if (isset(Nest.value($element,"elementsubtype").$()) && Nest.value($element,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
		if (Nest.value($element,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
			$w = Nest.value($element,"width").$();
			$h = Nest.value($element,"height").$();
		}
		else {
			$w = Nest.value($map,"width").$();
			$h = Nest.value($map,"height").$();
		}
	}
	else {
		$image = get_png_by_selement($info);
		$w = imagesx($image);
		$h = imagesy($image);
	}

	$x += $w / 2;
	$y += $h / 2;

	return CArray.array($x, $y);
}

function myDrawLine($image, $x1, $y1, $x2, $y2, $color, $drawtype) {
	if ($drawtype == MAP_LINK_DRAWTYPE_BOLD_LINE) {
		zbx_imagealine($image, $x1, $y1, $x2, $y2, $color, LINE_TYPE_BOLD);
	}
	elseif ($drawtype == MAP_LINK_DRAWTYPE_DASHED_LINE) {
		if (function_exists("imagesetstyle")) {
			// use imagesetstyle + imageline instead of bugged ImageDashedLine
			$style = CArray.array(
				$color, $color, $color, $color,
				IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT
			);
			imagesetstyle($image, $style);
			zbx_imageline($image, $x1, $y1, $x2, $y2, IMG_COLOR_STYLED);
		}
		else {
			imagedashedline($image, $x1, $y1, $x2, $y2, $color);
		}
	}
	elseif ($drawtype == MAP_LINK_DRAWTYPE_DOT && function_exists("imagesetstyle")) {
		$style = CArray.array($color, IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT);
		imagesetstyle($image, $style);
		zbx_imageline($image, $x1, $y1, $x2, $y2, IMG_COLOR_STYLED);
	}
	else {
		zbx_imagealine($image, $x1, $y1, $x2, $y2, $color);
	}
}

function get_png_by_selement($info) {
	$image = get_image_by_imageid(Nest.value($info,"iconid").$());
	if (!$image) {
		return get_default_image();
	}
	return imagecreatefromstring(Nest.value($image,"image").$());
}

function convertColor($im, $color) {
	$RGB = CArray.array(
		hexdec("0x".substr($color, 0, 2)),
		hexdec("0x".substr($color, 2, 2)),
		hexdec("0x".substr($color, 4, 2))
	);
	return imagecolorallocate($im, $RGB[0], $RGB[1], $RGB[2]);
}

/**
 * Resolve all kinds of macros in map labels
 *
 * @param array $selement
 * @param string Nest.value($selement,"label").$() label to expand
 * @param int Nest.value($selement,"elementtype").$() type of element: trigger, host, ...
 * @param int Nest.value($selement,"elementid").$() element id in DB
 * @param string Nest.value($selement,"elementExpressionTrigger").$() if type is trigger, then trigger expression
 *
 * @return string expanded label
 */
function resolveMapLabelMacrosAll(array $selement) {
	$label = Nest.value($selement,"label").$();
	// for host and trigger items expand macros if they exists
	if ((Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST
			|| Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_TRIGGER)
			&& (zbx_strpos($label, "HOST.NAME") !== false
					|| zbx_strpos($label, "HOSTNAME") !== false /* deprecated */
					|| zbx_strpos($label, "HOST.HOST") !== false
					|| zbx_strpos($label, "HOST.DNS") !== false
					|| zbx_strpos($label, "HOST.IP") !== false
					|| zbx_strpos($label, "IPADDRESS") !== false /* deprecated */
					|| zbx_strpos($label, "HOST.CONN") !== false)) {

		// priorities of interface types doesn't match interface type ids in DB
		$priorities = CArray.array(
			INTERFACE_TYPE_AGENT => 4,
			INTERFACE_TYPE_SNMP => 3,
			INTERFACE_TYPE_JMX => 2,
			INTERFACE_TYPE_IPMI => 1
		);

		// get host data if element is host
		if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST) {
			$res = DBselect("SELECT hi.ip,hi.dns,hi.useip,h.host,h.name,hi.type AS interfacetype".
					" FROM interface hi,hosts h".
					" WHERE hi.hostid=h.hostid".
						" AND hi.main=1 AND hi.hostid=".zbx_dbstr(Nest.value($selement,"elementid").$()));

			// process interface priorities
			$tmpPriority = 0;
			while ($dbHost = DBfetch($res)) {
				if  ($priorities[$dbHost["interfacetype"]] > $tmpPriority) {
					$resHost = $dbHost;
					$tmpPriority = $priorities[$dbHost["interfacetype"]];
				}
			}

			$hostsByNr[""] = $resHost;
		}
		// get trigger host list if element is trigger
		else {
			$res = DBselect("SELECT hi.ip,hi.dns,hi.useip,h.host,h.name,f.functionid,hi.type AS interfacetype" .
					" FROM interface hi,items i,functions f,hosts h".
					" WHERE h.hostid=hi.hostid".
						" AND hi.hostid=i.hostid".
						" AND i.itemid=f.itemid".
						" AND hi.main=1 AND f.triggerid=".zbx_dbstr(Nest.value($selement,"elementid").$()).
					" ORDER BY f.functionid");

			// process interface priorities, build $hostsByFunctionId array
			$tmpFunctionId = -1;
			while ($dbHost = DBfetch($res)) {
				if (Nest.value($dbHost,"functionid").$() != $tmpFunctionId) {
					$tmpPriority = 0;
					$tmpFunctionId = Nest.value($dbHost,"functionid").$();
				}
				if  ($priorities[$dbHost["interfacetype"]] > $tmpPriority) {
					$hostsByFunctionId[$dbHost["functionid"]] = $dbHost;
					$tmpPriority = $priorities[$dbHost["interfacetype"]];
				}
			}

			// get all function ids from expression and link host data against position in expression
			preg_match_all("/\{([0-9]+)\}/", Nest.value($selement,"elementExpressionTrigger").$(), $matches);
			$hostsByNr = CArray.array();
			for($matches[1] as $i => $functionid) {
				if (isset($hostsByFunctionId[$functionid])) {
					$hostsByNr[$i + 1] = $hostsByFunctionId[$functionid];
				}
			}

			// for macro without numeric index
			if (isset($hostsByNr[1])) {
				$hostsByNr[""] = $hostsByNr[1];
			}
		}

		// resolve functional macros like: {{HOST.HOST}:log[{HOST.HOST}.log].last(0)}
		$label = resolveMapLabelMacros($label, $hostsByNr);

		// resolves basic macros
		// $hostsByNr possible keys: "" and 1-9
		for($hostsByNr as $i => $host) {
			$replace = CArray.array(
				"{HOST.NAME".$i."}" => Nest.value($host,"name").$(),
				"{HOSTNAME".$i."}" => Nest.value($host,"host").$(),
				"{HOST.HOST".$i."}" => Nest.value($host,"host").$(),
				"{HOST.DNS".$i."}" => Nest.value($host,"dns").$(),
				"{HOST.IP".$i."}" => Nest.value($host,"ip").$(),
				"{IPADDRESS".$i."}" => Nest.value($host,"ip").$(),
				"{HOST.CONN".$i."}" => Nest.value($host,"useip").$() ? Nest.value($host,"ip").$() : Nest.value($host,"dns").$(),
			);
			$label = str_replace(array_keys($replace), $replace, $label);
		}
	}
	else {
		// resolve functional macros like: {sampleHostName:log[{HOST.HOST}.log].last(0)}, if no host provided
		$label = resolveMapLabelMacros($label);
	}

	// resolve map specific processing consuming macros
	switch (Nest.value($selement,"elementtype").$()) {
		case SYSMAP_ELEMENT_TYPE_HOST:
		case SYSMAP_ELEMENT_TYPE_MAP:
		case SYSMAP_ELEMENT_TYPE_TRIGGER:
		case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
			if (zbx_strpos($label, "{TRIGGERS.UNACK}") !== false) {
				$label = str_replace("{TRIGGERS.UNACK}", get_triggers_unacknowledged($selement), $label);
			}
			if (zbx_strpos($label, "{TRIGGERS.PROBLEM.UNACK}") !== false) {
				$label = str_replace("{TRIGGERS.PROBLEM.UNACK}", get_triggers_unacknowledged($selement, true), $label);
			}
			if (zbx_strpos($label, "{TRIGGER.EVENTS.UNACK}") !== false) {
				$label = str_replace("{TRIGGER.EVENTS.UNACK}", get_events_unacknowledged($selement), $label);
			}
			if (zbx_strpos($label, "{TRIGGER.EVENTS.PROBLEM.UNACK}") !== false) {
				$label = str_replace("{TRIGGER.EVENTS.PROBLEM.UNACK}", get_events_unacknowledged($selement, null, TRIGGER_VALUE_TRUE), $label);
			}
			if (zbx_strpos($label, "{TRIGGER.PROBLEM.EVENTS.PROBLEM.UNACK}") !== false) {
				$label = str_replace("{TRIGGER.PROBLEM.EVENTS.PROBLEM.UNACK}", get_events_unacknowledged($selement, TRIGGER_VALUE_TRUE, TRIGGER_VALUE_TRUE), $label);
			}
			if (zbx_strpos($label, "{TRIGGERS.ACK}") !== false) {
				$label = str_replace("{TRIGGERS.ACK}", get_triggers_unacknowledged($selement, null, true), $label);
			}
			if (zbx_strpos($label, "{TRIGGERS.PROBLEM.ACK}") !== false) {
				$label = str_replace("{TRIGGERS.PROBLEM.ACK}", get_triggers_unacknowledged($selement, true, true), $label);
			}
			if (zbx_strpos($label, "{TRIGGER.EVENTS.ACK}") !== false) {
				$label = str_replace("{TRIGGER.EVENTS.ACK}", get_events_unacknowledged($selement, null, null, true), $label);
			}
			if (zbx_strpos($label, "{TRIGGER.EVENTS.PROBLEM.ACK}") !== false) {
				$label = str_replace("{TRIGGER.EVENTS.PROBLEM.ACK}", get_events_unacknowledged($selement, null, TRIGGER_VALUE_TRUE, true), $label);
			}
			if (zbx_strpos($label, "{TRIGGER.PROBLEM.EVENTS.PROBLEM.ACK}") !== false) {
				$label = str_replace("{TRIGGER.PROBLEM.EVENTS.PROBLEM.ACK}", get_events_unacknowledged($selement, TRIGGER_VALUE_TRUE, TRIGGER_VALUE_TRUE, true), $label);
			}
			break;
	}

	return $label;
}

/**
 * Expand functional macros in given map label.
 *
 * @param string $label label to expand
 * @param array $replaceHosts list of hosts in order which they appear in trigger expression if trigger label is given,
 * or single host when host label is given
 *
 * @return string expanded label
 */
function resolveMapLabelMacros($label, $replaceHosts = null) {
	$functionsPattern = "(last|max|min|avg)\(([0-9]+[".ZBX_TIME_SUFFIXES."]?)?\)";

	// find functional macro pattern
	$pattern = (null === $replaceHosts)
		? "/{".ZBX_PREG_HOST_FORMAT.":.+\.".$functionsPattern."}/Uu"
		: "/{(".ZBX_PREG_HOST_FORMAT."|{HOSTNAME[0-9]?}|{HOST\.HOST[0-9]?}):.+\.".$functionsPattern."}/Uu";

	preg_match_all($pattern, $label, $matches);

	// for each functional macro
	for($matches[0] as $expr) {
		$macro = $expr;
		if ($replaceHosts !== null) {
			// search for macros with all possible indices
			for($replaceHosts as $i => $host) {
				$macroTmp = $macro;
				// replace only macro in first position
				$macro = preg_replace("/{({HOSTNAME".$i."}|{HOST\.HOST".$i."}):(.*)}/U", "{".$host["host"].":$2}", $macro);
				// only one simple macro possible inside functional macro
				if ($macro != $macroTmp) {
					break;
				}
			}
		}

		// try to create valid expression
		$expressionData = new CTriggerExpression();
		if (!$expressionData.parse($macro) || !isset($expressionData.expressions[0])) {
			continue;
		}

		// look in DB for corresponding item
		$itemHost = $expressionData.expressions[0]["host"];
		$key = $expressionData.expressions[0]["item"];
		$function = $expressionData.expressions[0]["functionName"];

		$item = API.Item().get(CArray.array(
			"webitems" => true,
			"filter" => CArray.array(
				"host" => $itemHost,
				"key_" => $key
			),
			"output" => CArray.array("itemid", "lastclock", "value_type", "lastvalue", "units", "valuemapid")
		));

		$item = reset($item);

		// if no corresponding item found with functional macro key and host
		if (!$item) {
			$label = str_replace($expr, UNRESOLVED_MACRO_STRING, $label);
			continue;
		}

		// do function type (last, min, max, avg) related actions
		if ($function == "last") {
			$value = (Nest.value($item,"lastclock").$()) ? formatHistoryValue(Nest.value($item,"lastvalue").$(), $item) : UNRESOLVED_MACRO_STRING;
		}
		else {
			$value = getItemFunctionalValue($item, $function, $expressionData.expressions[0]["functionParamList"][0]);
		}

		if (isset($value)) {
			$label = str_replace($expr, $value, $label);
		}
	}

	return $label;
}

function get_map_elements($db_element, &$elements) {
	switch (Nest.value($db_element,"elementtype").$()) {
		case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
			$elements["hosts_groups"][] = Nest.value($db_element,"elementid").$();
			break;
		case SYSMAP_ELEMENT_TYPE_HOST:
			$elements["hosts"][] = Nest.value($db_element,"elementid").$();
			break;
		case SYSMAP_ELEMENT_TYPE_TRIGGER:
			$elements["triggers"][] = Nest.value($db_element,"elementid").$();
			break;
		case SYSMAP_ELEMENT_TYPE_MAP:
			$db_mapselements = DBselect(
				"SELECT DISTINCT se.elementtype,se.elementid".
				" FROM sysmaps_elements se".
				" WHERE se.sysmapid=".zbx_dbstr(Nest.value($db_element,"elementid").$())
			);
			while ($db_mapelement = DBfetch($db_mapselements)) {
				get_map_elements($db_mapelement, $elements);
			}
			break;
	}
}

/**
 * Adds names to elements. Adds expression for SYSMAP_ELEMENT_TYPE_TRIGGER elements.
 *
 * @param type $selements
 */
function add_elementNames(&$selements) {
	$hostids = CArray.array();
	$triggerids = CArray.array();
	$mapids = CArray.array();
	$hostgroupids = CArray.array();
	$imageids = CArray.array();

	for($selements as $selement) {
		switch (Nest.value($selement,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_HOST:
				$hostids[$selement["elementid"]] = Nest.value($selement,"elementid").$();
				break;
			case SYSMAP_ELEMENT_TYPE_MAP:
				$mapids[$selement["elementid"]] = Nest.value($selement,"elementid").$();
				break;
			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				$triggerids[$selement["elementid"]] = Nest.value($selement,"elementid").$();
				break;
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				$hostgroupids[$selement["elementid"]] = Nest.value($selement,"elementid").$();
				break;
			case SYSMAP_ELEMENT_TYPE_IMAGE:
				$imageids[$selement["iconid_off"]] = Nest.value($selement,"iconid_off").$();
				break;
		}
	}

	$hosts = API.Host().get(CArray.array(
		"hostids" => $hostids,
		"output" => CArray.array("name"),
		"nopermissions" => true,
		"nodeids" => get_current_nodeid(true),
		"preservekeys" => true
	));

	$maps = API.Map().get(CArray.array(
		"mapids" => $mapids,
		"output" => CArray.array("name"),
		"nopermissions" => true,
		"nodeids" => get_current_nodeid(true),
		"preservekeys" => true
	));

	$triggers = API.Trigger().get(CArray.array(
		"triggerids" => $triggerids,
		"output" => API_OUTPUT_EXTEND,
		"selectHosts" => CArray.array("hostid", "name"),
		"nopermissions" => true,
		"nodeids" => get_current_nodeid(true),
		"preservekeys" => true
	));

	$hostgroups = API.HostGroup().get(CArray.array(
		"hostgroupids" => $hostgroupids,
		"output" => CArray.array("name"),
		"nopermissions" => true,
		"nodeids" => get_current_nodeid(true),
		"preservekeys" => true
	));

	$images = API.image().get(CArray.array(
		"imageids" => $imageids,
		"output" => API_OUTPUT_EXTEND,
		"nopermissions" => true,
		"nodeids" => get_current_nodeid(true),
		"preservekeys" => true
	));

	for($selements as $snum => $selement) {
		switch (Nest.value($selement,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_HOST:
				$selements[$snum]["elementName"] = $hosts[$selement["elementid"]]["name"];
				break;
			case SYSMAP_ELEMENT_TYPE_MAP:
				$selements[$snum]["elementName"] = $maps[$selement["elementid"]]["name"];
				break;
			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				$hostname = reset($triggers[$selement["elementid"]]["hosts"]);
				$selements[$snum]["elementName"] = $hostname["name"].NAME_DELIMITER.
					CMacrosResolverHelper::resolveTriggerName($triggers[$selement["elementid"]]);
				break;
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				$selements[$snum]["elementName"] = $hostgroups[$selement["elementid"]]["name"];
				break;
			case SYSMAP_ELEMENT_TYPE_IMAGE:
				$selements[$snum]["elementName"] = $images[$selement["iconid_off"]]["name"];
				break;
		}
	}

	if (!empty($triggers)) {
		add_triggerExpressions($selements, $triggers);
	}
}

function add_triggerExpressions(&$selements, $triggers = CArray.array()) {
	if (empty($triggers)) {
		$triggerIds = CArray.array();

		for($selements as $selement) {
			if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_TRIGGER) {
				$triggerIds[] = Nest.value($selement,"elementid").$();
			}
		}

		$triggers = API.Trigger().get(CArray.array(
			"triggerids" => $triggerIds,
			"output" => API_OUTPUT_EXTEND,
			"selectHosts" => CArray.array("name"),
			"nopermissions" => true,
			"nodeids" => get_current_nodeid(true),
			"preservekeys" => true
		));
	}

	for($selements as $snum => $selement) {
		if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_TRIGGER) {
			$selements[$snum]["elementExpressionTrigger"] = $triggers[$selement["elementid"]]["expression"];
		}
	}
}

/**
 * Returns trigger element icon rendering parameters.
 *
 * @param $selement
 * @param $i
 * @param $showUnack    map \"problem display\" parameter
 *
 * @return array
 */
function getTriggersInfo($selement, $i, $showUnack) {
	global $colors;

	$info = CArray.array(
		"latelyChanged" => Nest.value($i,"latelyChanged").$(),
		"ack" => Nest.value($i,"ack").$(),
		"priority" => Nest.value($i,"priority").$(),
		"info" => CArray.array(),
		"iconid" => $selement["iconid_off"]
	);

	if (Nest.value($i,"problem").$() && (Nest.value($i,"problem_unack").$() && $showUnack == EXTACK_OPTION_UNACK
			|| in_CArray.array($showUnack, CArray.array(EXTACK_OPTION_ALL, EXTACK_OPTION_BOTH)))) {
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_on").$();
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_ON;
		Nest.value($info,"info","unack").$() = CArray.array(
			"msg" => _("PROBLEM"),
			"color" => (Nest.value($i,"priority").$() > 3) ? Nest.value($colors,"Red").$() : $colors["Dark Red"]
		);
	}
	elseif (Nest.value($i,"trigger_disabled").$()) {
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_disabled").$();
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_DISABLED;
		Nest.value($info,"info","status").$() = CArray.array(
			"msg" => _("DISABLED"),
			"color" => $colors["Dark Red"]
		);
	}
	else {
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_off").$();
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_OFF;
		Nest.value($info,"info","ok").$() = CArray.array(
			"msg" => _("OK"),
			"color" => $colors["Dark Green"]
		);
	}

	return $info;
}

/**
 * Returns host element icon rendering parameters.
 *
 * @param $selement
 * @param $i
 * @param $show_unack    map \"problem display\" parameter
 *
 * @return array
 */
function getHostsInfo($selement, $i, $show_unack) {
	global $colors;

	$info = CArray.array(
		"latelyChanged" => Nest.value($i,"latelyChanged").$(),
		"ack" => Nest.value($i,"ack").$(),
		"priority" => Nest.value($i,"priority").$(),
		"info" => CArray.array(),
		"iconid" => $selement["iconid_off"]
	);
	$hasProblem = false;

	if (Nest.value($i,"problem").$()) {
		if (in_CArray.array($show_unack, CArray.array(EXTACK_OPTION_ALL, EXTACK_OPTION_BOTH))) {
			if (Nest.value($i,"problem").$() > 1) {
				$msg = $i["problem"]." "._("Problems");
			}
			elseif (isset(Nest.value($i,"problem_title").$())) {
				$msg = Nest.value($i,"problem_title").$();
			}
			else {
				$msg = "1 "._("Problem");
			}

			Nest.value($info,"info","problem").$() = CArray.array(
				"msg" => $msg,
				"color" => (Nest.value($i,"priority").$() > 3) ? Nest.value($colors,"Red").$() : $colors["Dark Red"]
			);
		}

		if (in_CArray.array($show_unack, CArray.array(EXTACK_OPTION_UNACK, EXTACK_OPTION_BOTH)) && Nest.value($i,"problem_unack").$()) {
			Nest.value($info,"info","unack").$() = CArray.array(
				"msg" => $i["problem_unack"]." "._("Unacknowledged"),
				"color" => $colors["Dark Red"]
			);
		}

		// set element to problem state if it has problem events
		if (Nest.value($info,"info").$()) {
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_on").$();
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_ON;
			$hasProblem = true;
		}
	}

	if (Nest.value($i,"maintenance").$()) {
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_maintenance").$();
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_MAINTENANCE;
		Nest.value($info,"info","maintenance").$() = CArray.array(
			"msg" => _("MAINTENANCE")." (".$i["maintenance_title"].")",
			"color" => $colors["Orange"]
		);
	}
	elseif (Nest.value($i,"disabled").$()) {
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_disabled").$();
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_DISABLED;
		Nest.value($info,"info","status").$() = CArray.array(
			"msg" => _("DISABLED"),
			"color" => $colors["Dark Red"]
		);
	}
	elseif (!$hasProblem) {
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_off").$();
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_OFF;
		Nest.value($info,"info","ok").$() = CArray.array(
			"msg" => _("OK"),
			"color" => $colors["Dark Green"]
		);
	}

	return $info;
}

/**
 * Returns host groups element icon rendering parameters.
 *
 * @param $selement
 * @param $i
 * @param $show_unack    map \"problem display\" parameter
 *
 * @return array
 */
function getHostGroupsInfo($selement, $i, $show_unack) {
	global $colors;

	$info = CArray.array(
		"latelyChanged" => Nest.value($i,"latelyChanged").$(),
		"ack" => Nest.value($i,"ack").$(),
		"priority" => Nest.value($i,"priority").$(),
		"info" => CArray.array(),
		"iconid" => $selement["iconid_off"]
	);
	$hasProblem = false;
	$hasStatus = false;

	if (Nest.value($i,"problem").$()) {
		if (in_CArray.array($show_unack, CArray.array(EXTACK_OPTION_ALL, EXTACK_OPTION_BOTH))) {
			if (Nest.value($i,"problem").$() > 1) {
				$msg = $i["problem"]." "._("Problems");
			}
			elseif (isset(Nest.value($i,"problem_title").$())) {
				$msg = Nest.value($i,"problem_title").$();
			}
			else {
				$msg = "1 "._("Problem");
			}

			Nest.value($info,"info","problem").$() = CArray.array(
				"msg" => $msg,
				"color" => (Nest.value($i,"priority").$() > 3) ? Nest.value($colors,"Red").$() : $colors["Dark Red"]
			);
		}

		if (in_CArray.array($show_unack, CArray.array(EXTACK_OPTION_UNACK, EXTACK_OPTION_BOTH)) && Nest.value($i,"problem_unack").$()) {
			Nest.value($info,"info","unack").$() = CArray.array(
				"msg" => $i["problem_unack"]." "._("Unacknowledged"),
				"color" => $colors["Dark Red"]
			);
		}

		// set element to problem state if it has problem events
		if (Nest.value($info,"info").$()) {
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_on").$();
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_ON;
			$hasProblem = true;
		}
	}

	if (Nest.value($i,"maintenance").$()) {
		if (!$hasProblem) {
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_maintenance").$();
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_MAINTENANCE;
		}
		Nest.value($info,"info","maintenance").$() = CArray.array(
			"msg" => $i["maintenance"]." "._("Maintenance"),
			"color" => $colors["Orange"]
		);
		$hasStatus = true;
	}
	elseif (Nest.value($i,"disabled").$()) {
		if (!$hasProblem) {
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_DISABLED;
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_disabled").$();
		}
		Nest.value($info,"info","disabled").$() = CArray.array(
			"msg" => _("DISABLED"),
			"color" => $colors["Dark Red"]
		);
		$hasStatus = true;
	}

	if (!$hasStatus && !$hasProblem) {
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_OFF;
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_off").$();
		Nest.value($info,"info","ok").$() = CArray.array(
			"msg" => _("OK"),
			"color" => $colors["Dark Green"]
		);
	}

	return $info;
}

/**
 * Returns maps groups element icon rendering parameters.
 *
 * @param $selement
 * @param $i
 * @param $show_unack    map \"problem display\" parameter
 *
 * @return array
 */
function getMapsInfo($selement, $i, $show_unack) {
	global $colors;

	$info = CArray.array(
		"latelyChanged" => Nest.value($i,"latelyChanged").$(),
		"ack" => Nest.value($i,"ack").$(),
		"priority" => Nest.value($i,"priority").$(),
		"info" => CArray.array(),
		"iconid" => $selement["iconid_off"]
	);

	$hasProblem = false;
	$hasStatus = false;

	if (Nest.value($i,"problem").$()) {
		if (in_CArray.array($show_unack, CArray.array(EXTACK_OPTION_ALL, EXTACK_OPTION_BOTH))) {
			if (Nest.value($i,"problem").$() > 1) {
				$msg = $i["problem"]." "._("Problems");
			}
			elseif (isset(Nest.value($i,"problem_title").$())) {
				$msg = Nest.value($i,"problem_title").$();
			}
			else {
				$msg = "1 "._("Problem");
			}

			Nest.value($info,"info","problem").$() = CArray.array(
				"msg" => $msg,
				"color" => (Nest.value($i,"priority").$() > 3) ? Nest.value($colors,"Red").$() : $colors["Dark Red"]
			);
		}

		if (in_CArray.array($show_unack, CArray.array(EXTACK_OPTION_UNACK, EXTACK_OPTION_BOTH)) && Nest.value($i,"problem_unack").$()) {
			Nest.value($info,"info","unack").$() = CArray.array(
				"msg" => $i["problem_unack"]." "._("Unacknowledged"),
				"color" => $colors["Dark Red"]
			);
		}

		if (Nest.value($info,"info").$()) {
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_on").$();
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_ON;
			$hasProblem = true;
		}
	}

	if (Nest.value($i,"maintenance").$()) {
		if (!$hasProblem) {
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_maintenance").$();
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_MAINTENANCE;
		}
		Nest.value($info,"info","maintenance").$() = CArray.array(
			"msg" => $i["maintenance"]." "._("Maintenance"),
			"color" => $colors["Orange"]
		);
		$hasStatus = true;
	}
	elseif (Nest.value($i,"disabled").$()) {
		if (!$hasProblem) {
			Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_DISABLED;
			Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_disabled").$();
		}
		Nest.value($info,"info","disabled").$() = CArray.array(
			"msg" => _("DISABLED"),
			"color" => $colors["Dark Red"]
		);
		$hasStatus = true;
	}

	if (!$hasStatus && !$hasProblem) {
		Nest.value($info,"icon_type").$() = SYSMAP_ELEMENT_ICON_OFF;
		Nest.value($info,"iconid").$() = Nest.value($selement,"iconid_off").$();
		Nest.value($info,"info","ok").$() = CArray.array(
			"msg" => _("OK"),
			"color" => $colors["Dark Green"]
		);
	}

	return $info;
}

function getImagesInfo($selement) {
	return CArray.array(
		"iconid" => Nest.value($selement,"iconid_off").$(),
		"icon_type" => SYSMAP_ELEMENT_ICON_OFF,
		"name" => _("Image"),
		"latelyChanged" => false
	);
}

/**
 * Prepare map elements data.
 * Calculate problem triggers and priorities. Populate map elements with automatic icon mapping, acknowledging and
 * recent change markers.
 *
 * @param array $sysmap
 * @param int   $options
 * @param int   Nest.value($options,"severity_min").$() Minimum trigger severity, default value is maximal (Disaster)
 *
 * @return array
 */
function getSelementsInfo($sysmap, array $options = CArray.array()) {
	if (!isset(Nest.value($options,"severity_min").$())) {
		Nest.value($options,"severity_min").$() = TRIGGER_SEVERITY_NOT_CLASSIFIED;
	}

	$config = select_config();
	$show_unack = Nest.value($config,"event_ack_enable").$() ? Nest.value($sysmap,"show_unack").$() : EXTACK_OPTION_ALL;

	$triggers_map = CArray.array();
	$triggers_map_submaps = CArray.array();
	$hostgroups_map = CArray.array();
	$hosts_map = CArray.array();

	if (Nest.value($sysmap,"sysmapid").$()) {
		$iconMap = API.IconMap().get(CArray.array(
			"sysmapids" => Nest.value($sysmap,"sysmapid").$(),
			"selectMappings" => API_OUTPUT_EXTEND,
			"output" => API_OUTPUT_EXTEND
		));
		$iconMap = reset($iconMap);

		$hostsToGetInventories = CArray.array();
	}

	$selements = Nest.value($sysmap,"selements").$();
	for($selements as $selementId => $selement) {
		$selements[$selementId]["hosts"] = CArray.array();
		$selements[$selementId]["triggers"] = CArray.array();

		switch (Nest.value($selement,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_MAP:
				$mapids = CArray.array(Nest.value($selement,"elementid").$());

				while (!empty($mapids)) {
					$maps = API.Map().get(CArray.array(
						"sysmapids" => $mapids,
						"output" => API_OUTPUT_REFER,
						"selectSelements" => API_OUTPUT_EXTEND,
						"nopermissions" => true,
						"nodeids" => get_current_nodeid(true)
					));

					$mapids = CArray.array();
					for($maps as $map) {
						for(Nest.value($map,"selements").$() as $sel) {
							switch (Nest.value($sel,"elementtype").$()) {
								case SYSMAP_ELEMENT_TYPE_MAP:
									$mapids[] = Nest.value($sel,"elementid").$();
									break;
								case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
									$hostgroups_map[$sel["elementid"]][$selementId] = $selementId;
									break;
								case SYSMAP_ELEMENT_TYPE_HOST:
									$hosts_map[$sel["elementid"]][$selementId] = $selementId;
									break;
								case SYSMAP_ELEMENT_TYPE_TRIGGER:
									$triggers_map_submaps[$sel["elementid"]][$selementId] = $selementId;
									break;
							}
						}
					}
				}
				break;
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				$hostgroups_map[$selement["elementid"]][$selement["selementid"]] = Nest.value($selement,"selementid").$();
				break;
			case SYSMAP_ELEMENT_TYPE_HOST:
				$hosts_map[$selement["elementid"]][$selement["selementid"]] = Nest.value($selement,"selementid").$();

				// if we have icon map applied, we need to get inventories for all hosts,
				// where automatic icon selection is enabled.
				if (Nest.value($sysmap,"iconmapid").$() && Nest.value($selement,"use_iconmap").$()) {
					$hostsToGetInventories[] = Nest.value($selement,"elementid").$();
				}
				break;
			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				$triggers_map[$selement["elementid"]][$selement["selementid"]] = Nest.value($selement,"selementid").$();
				break;
		}
	}

	// get host inventories
	if (Nest.value($sysmap,"iconmapid").$()) {
		$hostInventories = API.Host().get(CArray.array(
			"hostids" => $hostsToGetInventories,
			"output" => CArray.array("hostid"),
			"nopermissions" => true,
			"preservekeys" => true,
			"selectInventory" => API_OUTPUT_EXTEND
		));
	}

	$all_hosts = CArray.array();
	if (!empty($hosts_map)) {
		$hosts = API.Host().get(CArray.array(
			"hostids" => array_keys($hosts_map),
			"output" => CArray.array("name", "status", "maintenance_status", "maintenanceid"),
			"nopermissions" => true,
			"nodeids" => get_current_nodeid(true)
		));
		$all_hosts = array_merge($all_hosts, $hosts);
		for($hosts as $host) {
			for($hosts_map[$host["hostid"]] as $belongs_to_sel) {
				$selements[$belongs_to_sel]["hosts"][$host["hostid"]] = Nest.value($host,"hostid").$();
			}
		}
	}

	if (!empty($hostgroups_map)) {
		$hosts = API.Host().get(CArray.array(
			"groupids" => array_keys($hostgroups_map),
			"output" => CArray.array("name", "status", "maintenance_status", "maintenanceid"),
			"selectGroups" => CArray.array("groupid"),
			"nopermissions" => true,
			"nodeids" => get_current_nodeid(true)
		));
		$all_hosts = array_merge($all_hosts, $hosts);
		for($hosts as $host) {
			for(Nest.value($host,"groups").$() as $group) {
				if (isset($hostgroups_map[$group["groupid"]])) {
					for($hostgroups_map[$group["groupid"]] as $belongs_to_sel) {
						$selements[$belongs_to_sel]["hosts"][$host["hostid"]] = Nest.value($host,"hostid").$();

						// add hosts to hosts_map for trigger selection;
						if (!isset($hosts_map[$host["hostid"]])) {
							$hosts_map[$host["hostid"]] = CArray.array();
						}
						$hosts_map[$host["hostid"]][$belongs_to_sel] = $belongs_to_sel;
					}
				}
			}
		}
	}
	$all_hosts = zbx_toHash($all_hosts, "hostid");

	$monitored_hostids = CArray.array();
	for($all_hosts as $hostid => $host) {
		if (Nest.value($host,"status").$() == HOST_STATUS_MONITORED) {
			$monitored_hostids[$hostid] = $hostid;
		}
	}

	// get triggers data, triggers from current map, select all
	$all_triggers = CArray.array();

	if (!empty($triggers_map)) {
		$triggers = API.Trigger().get(CArray.array(
			"nodeids" => get_current_nodeid(true),
			"triggerids" => array_keys($triggers_map),
			"filter" => CArray.array("state" => null),
			"output" => API_OUTPUT_EXTEND,
			"nopermissions" => true
		));
		$all_triggers = array_merge($all_triggers, $triggers);

		for($triggers as $trigger) {
			for($triggers_map[$trigger["triggerid"]] as $belongs_to_sel) {
				$selements[$belongs_to_sel]["triggers"][$trigger["triggerid"]] = Nest.value($trigger,"triggerid").$();
			}
		}
	}

	// triggers from submaps, skip dependent
	if (!empty($triggers_map_submaps)) {
		$triggers = API.Trigger().get(CArray.array(
			"nodeids" => get_current_nodeid(true),
			"triggerids" => array_keys($triggers_map_submaps),
			"filter" => CArray.array("state" => null),
			"skipDependent" => true,
			"output" => API_OUTPUT_EXTEND,
			"nopermissions" => true
		));
		$all_triggers = array_merge($all_triggers, $triggers);

		for($triggers as $trigger) {
			for($triggers_map_submaps[$trigger["triggerid"]] as $belongs_to_sel) {
				$selements[$belongs_to_sel]["triggers"][$trigger["triggerid"]] = Nest.value($trigger,"triggerid").$();
			}
		}
	}

	// triggers from all hosts/hostgroups, skip dependent
	if (!empty($monitored_hostids)) {
		$triggers = API.Trigger().get(CArray.array(
			"hostids" => $monitored_hostids,
			"output" => CArray.array("status", "value", "priority", "lastchange", "description", "expression"),
			"selectHosts" => CArray.array("hostid"),
			"nopermissions" => true,
			"filter" => CArray.array("state" => null),
			"nodeids" => get_current_nodeid(true),
			"monitored" => true,
			"skipDependent" => true
		));
		$all_triggers = array_merge($all_triggers, $triggers);

		for($triggers as $trigger) {
			for(Nest.value($trigger,"hosts").$() as $host) {
				if (isset($hosts_map[$host["hostid"]])) {
					for($hosts_map[$host["hostid"]] as $belongs_to_sel) {
						$selements[$belongs_to_sel]["triggers"][$trigger["triggerid"]] = Nest.value($trigger,"triggerid").$();
					}
				}
			}
		}
	}
	$all_triggers = zbx_toHash($all_triggers, "triggerid");

	$unackTriggerIds = API.Trigger().get(CArray.array(
		"triggerids" => array_keys($all_triggers),
		"withLastEventUnacknowledged" => true,
		"output" => CArray.array("triggerid"),
		"nodeids" => get_current_nodeid(true),
		"nopermissions" => true,
		"monitored" => true,
		"filter" => CArray.array("value" => TRIGGER_VALUE_TRUE, "state" => null)
	));
	$unackTriggerIds = zbx_toHash($unackTriggerIds, "triggerid");

	$info = CArray.array();
	for($selements as $selementId => $selement) {
		$i = CArray.array(
			"disabled" => 0,
			"maintenance" => 0,
			"problem" => 0,
			"problem_unack" => 0,
			"priority" => 0,
			"trigger_disabled" => 0,
			"latelyChanged" => false,
			"ack" => true
		);

		for(Nest.value($selement,"hosts").$() as $hostid) {
			$host = $all_hosts[$hostid];
			$last_hostid = $hostid;

			if (Nest.value($host,"status").$() == HOST_STATUS_NOT_MONITORED) {
				$i["disabled"]++;
			}
			elseif (Nest.value($host,"maintenance_status").$() == HOST_MAINTENANCE_STATUS_ON) {
				$i["maintenance"]++;
			}
		}

		for(Nest.value($selement,"triggers").$() as $triggerId) {
			$trigger = $all_triggers[$triggerId];

			if (Nest.value($options,"severity_min").$() <= Nest.value($trigger,"priority").$()) {
				if (Nest.value($trigger,"status").$() == TRIGGER_STATUS_DISABLED) {
					$i["trigger_disabled"]++;
				}
				else {
					if (Nest.value($trigger,"value").$() == TRIGGER_VALUE_TRUE) {
						$i["problem"]++;
						$lastProblemId = $triggerId;

						if (Nest.value($i,"priority").$() < Nest.value($trigger,"priority").$()) {
							Nest.value($i,"priority").$() = Nest.value($trigger,"priority").$();
						}
					}

					if (isset($unackTriggerIds[$triggerId])) {
						$i["problem_unack"]++;
					}

					$config = select_config();

					Nest.value($i,"latelyChanged").$() |= ((time() - Nest.value($trigger,"lastchange").$()) < Nest.value($config,"blink_period").$());
				}
			}
		}

		Nest.value($i,"ack").$() = (bool) !(Nest.value($i,"problem_unack").$());

		if (Nest.value($sysmap,"expandproblem").$() && Nest.value($i,"problem").$() == 1) {
			if (!isset($lastProblemId)) {
				$lastProblemId = null;
			}

			Nest.value($i,"problem_title").$() = CMacrosResolverHelper::resolveTriggerName($all_triggers[$lastProblemId]);
		}

		if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST && Nest.value($i,"maintenance").$() == 1) {
			$mnt = get_maintenance_by_maintenanceid($all_hosts[$last_hostid]["maintenanceid"]);
			Nest.value($i,"maintenance_title").$() = Nest.value($mnt,"name").$();
		}

		// replace default icons
		if (!Nest.value($selement,"iconid_on").$()) {
			Nest.value($selement,"iconid_on").$() = Nest.value($selement,"iconid_off").$();
		}
		if (!Nest.value($selement,"iconid_maintenance").$()) {
			Nest.value($selement,"iconid_maintenance").$() = Nest.value($selement,"iconid_off").$();
		}
		if (!Nest.value($selement,"iconid_disabled").$()) {
			Nest.value($selement,"iconid_disabled").$() = Nest.value($selement,"iconid_off").$();
		}

		switch (Nest.value($selement,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_MAP:
				$info[$selementId] = getMapsInfo($selement, $i, $show_unack);
				break;

			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				$info[$selementId] = getHostGroupsInfo($selement, $i, $show_unack);
				break;

			case SYSMAP_ELEMENT_TYPE_HOST:
				$info[$selementId] = getHostsInfo($selement, $i, $show_unack);
				if (Nest.value($sysmap,"iconmapid").$() && Nest.value($selement,"use_iconmap").$()) {
					$info[$selementId]["iconid"] = getIconByMapping($iconMap, $hostInventories[$selement["elementid"]]);
				}
				break;

			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				$info[$selementId] = getTriggersInfo($selement, $i, $show_unack);
				break;

			case SYSMAP_ELEMENT_TYPE_IMAGE:
				$info[$selementId] = getImagesInfo($selement);
				break;
		}
	}

	if (Nest.value($sysmap,"label_format").$() == SYSMAP_LABEL_ADVANCED_OFF) {
		$hlabel = $hglabel = $tlabel = $mlabel = (Nest.value($sysmap,"label_type").$() == MAP_LABEL_TYPE_NAME);
	}
	else {
		$hlabel = (Nest.value($sysmap,"label_type_host").$() == MAP_LABEL_TYPE_NAME);
		$hglabel = (Nest.value($sysmap,"label_type_hostgroup").$() == MAP_LABEL_TYPE_NAME);
		$tlabel = (Nest.value($sysmap,"label_type_trigger").$() == MAP_LABEL_TYPE_NAME);
		$mlabel = (Nest.value($sysmap,"label_type_map").$() == MAP_LABEL_TYPE_NAME);
	}

	// get names if needed
	$elems = separateMapElements($sysmap);
	if (!empty(Nest.value($elems,"sysmaps").$()) && $mlabel) {
		$maps = API.Map().get(CArray.array(
			"sysmapids" => zbx_objectValues(Nest.value($elems,"sysmaps").$(), "elementid"),
			"nopermissions" => true,
			"output" => CArray.array("name")
		));
		$maps = zbx_toHash($maps, "sysmapid");

		for(Nest.value($elems,"sysmaps").$() as $elem) {
			$info[$elem["selementid"]]["name"] = $maps[$elem["elementid"]]["name"];
		}
	}
	if (!empty(Nest.value($elems,"hostgroups").$()) && $hglabel) {
		$hostgroups = API.HostGroup().get(CArray.array(
			"groupids" => zbx_objectValues(Nest.value($elems,"hostgroups").$(), "elementid"),
			"nopermissions" => true,
			"output" => CArray.array("name")
		));
		$hostgroups = zbx_toHash($hostgroups, "groupid");

		for(Nest.value($elems,"hostgroups").$() as $elem) {
			$info[$elem["selementid"]]["name"] = $hostgroups[$elem["elementid"]]["name"];
		}
	}

	if (!empty(Nest.value($elems,"triggers").$()) && $tlabel) {
		for(Nest.value($elems,"triggers").$() as $elem) {
			$info[$elem["selementid"]]["name"] = CMacrosResolverHelper::resolveTriggerName($all_triggers[$elem["elementid"]]);
		}
	}
	if (!empty(Nest.value($elems,"hosts").$()) && $hlabel) {
		for(Nest.value($elems,"hosts").$() as $elem) {
			$info[$elem["selementid"]]["name"] = $all_hosts[$elem["elementid"]]["name"];
		}
	}

	return $info;
}

function separateMapElements($sysmap) {
	$elements = CArray.array(
		"sysmaps" => CArray.array(),
		"hostgroups" => CArray.array(),
		"hosts" => CArray.array(),
		"triggers" => CArray.array(),
		"images" => CArray.array()
	);

	for(Nest.value($sysmap,"selements").$() as $selement) {
		switch (Nest.value($selement,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_MAP:
				$elements["sysmaps"][$selement["selementid"]] = $selement;
				break;
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				$elements["hostgroups"][$selement["selementid"]] = $selement;
				break;
			case SYSMAP_ELEMENT_TYPE_HOST:
				$elements["hosts"][$selement["selementid"]] = $selement;
				break;
			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				$elements["triggers"][$selement["selementid"]] = $selement;
				break;
			case SYSMAP_ELEMENT_TYPE_IMAGE:
			default:
				$elements["images"][$selement["selementid"]] = $selement;
		}
	}
	return $elements;
}

function drawMapConnectors(&$im, $map, $mapInfo, $drawAll = false) {
	$selements = Nest.value($map,"selements").$();

	for(Nest.value($map,"links").$() as $link) {
		$selement1 = $selements[$link["selementid1"]];
		$selement2 = $selements[$link["selementid2"]];

		list($x1, $y1) = get_icon_center_by_selement($selement1, $mapInfo[$link["selementid1"]], $map);
		list($x2, $y2) = get_icon_center_by_selement($selement2, $mapInfo[$link["selementid2"]], $map);

		if (isset(Nest.value($selement1,"elementsubtype").$()) && Nest.value($selement1,"elementsubtype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
			if (!$drawAll && (Nest.value($selement2,"elementsubtype").$() != SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS)) {
				continue;
			}

			if (Nest.value($selement1,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
				$w = Nest.value($selement1,"width").$();
				$h = Nest.value($selement1,"height").$();
			}
			else {
				$w = Nest.value($map,"width").$();
				$h = Nest.value($map,"height").$();
			}
			list($x1, $y1) = calculateMapAreaLinkCoord($x1, $y1, $w, $h, $x2, $y2);
		}

		if (isset(Nest.value($selement2,"elementsubtype").$()) && Nest.value($selement2,"elementsubtype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
			if (!$drawAll && (Nest.value($selement1,"elementsubtype").$() != SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS)) {
				continue;
			}

			if (Nest.value($selement2,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
				$w = Nest.value($selement2,"width").$();
				$h = Nest.value($selement2,"height").$();
			}
			else {
				$w = Nest.value($map,"width").$();
				$h = Nest.value($map,"height").$();
			}
			list($x2, $y2) = calculateMapAreaLinkCoord($x2, $y2, $w,$h, $x1, $y1);
		}

		$drawtype = Nest.value($link,"drawtype").$();
		$color = convertColor($im, Nest.value($link,"color").$());

		$linktriggers = Nest.value($link,"linktriggers").$();
		order_result($linktriggers, "triggerid");

		if (!empty($linktriggers)) {
			$max_severity = 0;

			$triggers = CArray.array();
			for($linktriggers as $link_trigger) {
				if (Nest.value($link_trigger,"triggerid").$() == 0) {
					continue;
				}

				$id = Nest.value($link_trigger,"linktriggerid").$();

				$triggers[$id] = zbx_array_merge($link_trigger, get_trigger_by_triggerid(Nest.value($link_trigger,"triggerid").$()));
				if ($triggers[$id]["status"] == TRIGGER_STATUS_ENABLED && $triggers[$id]["value"] == TRIGGER_VALUE_TRUE) {
					if ($triggers[$id]["priority"] >= $max_severity) {
						$drawtype = $triggers[$id]["drawtype"];
						$color = convertColor($im, $triggers[$id]["color"]);
						$max_severity = $triggers[$id]["priority"];
					}
				}
			}
		}

		myDrawLine($im, $x1, $y1, $x2, $y2, $color, $drawtype);
	}
}

function drawMapSelements(&$im, $map, $mapInfo) {
	$selements = Nest.value($map,"selements").$();

	for($selements as $selementId => $selement) {
		if (isset(Nest.value($selement,"elementsubtype").$()) && Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
			continue;
		}

		$elementInfo = $mapInfo[$selementId];
		$img = get_png_by_selement($elementInfo);

		$iconX = imagesx($img);
		$iconY = imagesy($img);

		imagecopy($im, $img, Nest.value($selement,"x").$(), Nest.value($selement,"y").$(), 0, 0, $iconX, $iconY);
	}
}

function drawMapHighligts(&$im, $map, $mapInfo) {
	$selements = Nest.value($map,"selements").$();

	for($selements as $selementId => $selement) {
		if (isset(Nest.value($selement,"elementsubtype").$()) && Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
			continue;
		}

		$elementInfo = $mapInfo[$selementId];
		$img = get_png_by_selement($elementInfo);

		$iconX = imagesx($img);
		$iconY = imagesy($img);

		if ((Nest.value($map,"highlight").$() % 2) == SYSMAP_HIGHLIGHT_ON) {
			$hl_color = null;
			$st_color = null;

			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_ON) {
				$hl_color = hex2rgb(getSeverityColor(Nest.value($elementInfo,"priority").$()));
			}

			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_MAINTENANCE) {
				$st_color = hex2rgb("FF9933");
			}
			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_DISABLED) {
				$st_color = hex2rgb("EEEEEE");
			}

			$mainProblems = CArray.array(
				SYSMAP_ELEMENT_TYPE_HOST_GROUP => 1,
				SYSMAP_ELEMENT_TYPE_MAP => 1
			);

			if (isset($mainProblems[$selement["elementtype"]])) {
				if (!is_null($hl_color)) {
					$st_color = null;
				}
			}
			elseif (!is_null($st_color)) {
				$hl_color = null;
			}

			if (!is_null($st_color)) {
				$r = $st_color[0];
				$g = $st_color[1];
				$b = $st_color[2];

				imagefilledrectangle($im,
					Nest.value($selement,"x").$() - 2,
					Nest.value($selement,"y").$() - 2,
					Nest.value($selement,"x").$() + $iconX + 2,
					Nest.value($selement,"y").$() + $iconY + 2,
					imagecolorallocatealpha($im, $r, $g, $b, 0)
				);

				// shadow
				imagerectangle($im,
					Nest.value($selement,"x").$() - 2 - 1,
					Nest.value($selement,"y").$() - 2 - 1,
					Nest.value($selement,"x").$() + $iconX + 2 + 1,
					Nest.value($selement,"y").$() + $iconY + 2 + 1,
					imagecolorallocate($im, 120, 120, 120)
				);

				imagerectangle($im,
					Nest.value($selement,"x").$() - 2 - 2,
					Nest.value($selement,"y").$() - 2 - 2,
					Nest.value($selement,"x").$() + $iconX + 2 + 2,
					Nest.value($selement,"y").$() + $iconY + 2 + 2,
					imagecolorallocate($im, 220, 220, 220)
				);
			}

			if (!is_null($hl_color)) {
				$r = $hl_color[0];
				$g = $hl_color[1];
				$b = $hl_color[2];

				imagefilledellipse($im,
					Nest.value($selement,"x").$() + ($iconX / 2),
					Nest.value($selement,"y").$() + ($iconY / 2),
					$iconX + 20,
					$iconX + 20,
					imagecolorallocatealpha($im, $r, $g, $b, 0)
				);

				imageellipse($im,
					Nest.value($selement,"x").$() + ($iconX / 2),
					Nest.value($selement,"y").$() + ($iconY / 2),
					$iconX + 20 + 1,
					$iconX + 20 + 1,
					imagecolorallocate($im, 120, 120, 120)
				);

				$config = select_config();
				if (isset(Nest.value($elementInfo,"ack").$()) && Nest.value($elementInfo,"ack").$() && Nest.value($config,"event_ack_enable").$()) {
					imagesetthickness($im, 5);
					imagearc($im,
						Nest.value($selement,"x").$() + ($iconX / 2),
						Nest.value($selement,"y").$() + ($iconY / 2),
						$iconX + 20 - 3,
						$iconX + 20 - 3,
						0,
						359,
						imagecolorallocate($im, 50, 150, 50)
					);
					imagesetthickness($im, 1);
				}
			}
		}
	}
}

function drawMapSelementsMarks(&$im, $map, $mapInfo) {
	global $colors;

	$selements = Nest.value($map,"selements").$();

	for($selements as $selementId => $selement) {
		if (empty($selement)) {
			continue;
		}

		$elementInfo = $mapInfo[$selementId];
		if (!Nest.value($elementInfo,"latelyChanged").$()) {
			continue;
		}

		// skip host group element containers
		if (Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
			continue;
		}

		$img = get_png_by_selement($elementInfo);

		$iconX = imagesx($img);
		$iconY = imagesy($img);

		$hl_color = null;
		$st_color = null;
		if (!isset(Nest.value(_REQUEST,"noselements").$()) && ((Nest.value($map,"highlight").$() % 2) == SYSMAP_HIGHLIGHT_ON)) {
			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_ON) {
				$hl_color = true;
			}
			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_MAINTENANCE
					|| Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_DISABLED) {
				$st_color = true;
			}
		}

		$mainProblems = CArray.array(
			SYSMAP_ELEMENT_TYPE_HOST_GROUP => 1,
			SYSMAP_ELEMENT_TYPE_MAP => 1
		);

		if (isset($mainProblems[$selement["elementtype"]])) {
			if (!is_null($hl_color)) {
				$st_color = null;
			}
			elseif (!is_null($st_color)) {
				$hl_color = null;
			}
		}

		$markSize = $iconX / 2;
		if ($hl_color) {
			$markSize += 12;
		}
		elseif ($st_color) {
			$markSize += 8;
		}
		else {
			$markSize += 3;
		}

		if (Nest.value($map,"label_type").$() != MAP_LABEL_TYPE_NOTHING) {
			$labelLocation = Nest.value($selement,"label_location").$();
			if (is_null($labelLocation) || ($labelLocation < 0)) {
				$labelLocation = Nest.value($map,"label_location").$();
			}

			switch ($labelLocation) {
				case MAP_LABEL_LOC_TOP:
					$marks = "rbl";
					break;
				case MAP_LABEL_LOC_LEFT:
					$marks = "trb";
					break;
				case MAP_LABEL_LOC_RIGHT:
					$marks = "tbl";
					break;
				case MAP_LABEL_LOC_BOTTOM:
				default:
					$marks = "trl";
			}
		}
		else {
			$marks = "trbl";
		}

		imageVerticalMarks($im, Nest.value($selement,"x").$() + ($iconX / 2), Nest.value($selement,"y").$() + ($iconY / 2), $markSize, Nest.value($colors,"Red").$(), $marks);
	}
}

function drawMapLinkLabels(&$im, $map, $mapInfo, $resolveMacros = true) {
	global $colors;

	$links = Nest.value($map,"links").$();
	$selements = Nest.value($map,"selements").$();

	for($links as $link) {
		if (empty(Nest.value($link,"label").$())) {
			continue;
		}

		$selement1 = $selements[$link["selementid1"]];
		list($x1, $y1) = get_icon_center_by_selement($selement1, $mapInfo[$link["selementid1"]], $map);

		$selement2 = $selements[$link["selementid2"]];
		list($x2, $y2) = get_icon_center_by_selement($selement2, $mapInfo[$link["selementid2"]], $map);

		if (isset(Nest.value($selement1,"elementsubtype").$()) && Nest.value($selement1,"elementsubtype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
			if (Nest.value($selement1,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
				$w = Nest.value($selement1,"width").$();
				$h = Nest.value($selement1,"height").$();
			}
			else {
				$w = Nest.value($map,"width").$();
				$h = Nest.value($map,"height").$();
			}
			list($x1, $y1) = calculateMapAreaLinkCoord($x1, $y1, $w, $h, $x2, $y2);
		}

		if (isset(Nest.value($selement2,"elementsubtype").$()) && Nest.value($selement2,"elementsubtype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
			if (Nest.value($selement2,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
				$w = Nest.value($selement2,"width").$();
				$h = Nest.value($selement2,"height").$();
			}
			else {
				$w = Nest.value($map,"width").$();
				$h = Nest.value($map,"height").$();
			}
			list($x2, $y2) = calculateMapAreaLinkCoord($x2, $y2, $w, $h, $x1, $y1);
		}

		$drawtype = Nest.value($link,"drawtype").$();
		$color = convertColor($im, Nest.value($link,"color").$());

		$linktriggers = Nest.value($link,"linktriggers").$();
		order_result($linktriggers, "triggerid");

		if (!empty($linktriggers)) {
			$max_severity = 0;

			$triggers = CArray.array();
			for($linktriggers as $link_trigger) {
				if (Nest.value($link_trigger,"triggerid").$() == 0) {
					continue;
				}
				$id = Nest.value($link_trigger,"linktriggerid").$();

				$triggers[$id] = zbx_array_merge($link_trigger, get_trigger_by_triggerid(Nest.value($link_trigger,"triggerid").$()));
				if ($triggers[$id]["status"] == TRIGGER_STATUS_ENABLED && $triggers[$id]["value"] == TRIGGER_VALUE_TRUE) {
					if ($triggers[$id]["priority"] >= $max_severity) {
						$drawtype = $triggers[$id]["drawtype"];
						$color = convertColor($im, $triggers[$id]["color"]);
						$max_severity = $triggers[$id]["priority"];
					}
				}
			}
		}

		$label = Nest.value($link,"label").$();

		$label = str_replace(\"\r\", "", $label);
		$strings = explode(\"\n\", $label);

		$box_width = 0;
		$box_height = 0;

		for($strings as $snum => $str) {
			$strings[$snum] = $resolveMacros ? resolveMapLabelMacros($str) : $str;
		}

		for($strings as $str) {
			$dims = imageTextSize(8, 0, $str);

			$box_width = ($box_width > Nest.value($dims,"width").$()) ? $box_width : Nest.value($dims,"width").$();
			$box_height += Nest.value($dims,"height").$() + 2;
		}

		$boxX_left = round(($x1 + $x2) / 2 - ($box_width / 2) - 6);
		$boxX_right = round(($x1 + $x2) / 2 + ($box_width / 2) + 6);

		$boxY_top = round(($y1 + $y2) / 2 - ($box_height / 2) - 4);
		$boxY_bottom = round(($y1 + $y2) / 2 + ($box_height / 2) + 2);

		switch ($drawtype) {
			case MAP_LINK_DRAWTYPE_DASHED_LINE:
			case MAP_LINK_DRAWTYPE_DOT:
				dashedRectangle($im, $boxX_left, $boxY_top, $boxX_right, $boxY_bottom, $color);
				break;
			case MAP_LINK_DRAWTYPE_BOLD_LINE:
				imagerectangle($im, $boxX_left - 1, $boxY_top - 1, $boxX_right + 1, $boxY_bottom + 1, $color);
				// break; is not ne
			case MAP_LINK_DRAWTYPE_LINE:
			default:
				imagerectangle($im, $boxX_left, $boxY_top, $boxX_right, $boxY_bottom, $color);
		}

		imagefilledrectangle($im, $boxX_left + 1, $boxY_top + 1, $boxX_right - 1, $boxY_bottom - 1, Nest.value($colors,"White").$());

		$increasey = 4;
		for($strings as $str) {
			$dims = imageTextSize(8, 0, $str);

			$labelx = ($x1 + $x2) / 2 - (Nest.value($dims,"width").$() / 2);
			$labely = $boxY_top + $increasey;

			imagetext($im, 8, 0, $labelx, $labely + Nest.value($dims,"height").$(), Nest.value($colors,"Black").$(), $str);

			$increasey += Nest.value($dims,"height").$() + 2;
		}
	}
}

function drawMapLabels(&$im, $map, $mapInfo, $resolveMacros = true) {
	global $colors;

	if (Nest.value($map,"label_type").$() == MAP_LABEL_TYPE_NOTHING && Nest.value($map,"label_format").$() == SYSMAP_LABEL_ADVANCED_OFF) {
		return;
	}

	$selements = Nest.value($map,"selements").$();
	$allStrings = "";
	$labelLines = CArray.array();
	$statusLines = CArray.array();

	for($selements as $sid => $selement) {
		if (isset(Nest.value($selement,"elementsubtype").$()) && Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
			unset($selements[$sid]);
		}
	}

	// set label type and custom label text for all selements
	for($selements as $selementId => $selement) {
		$selements[$selementId]["label_type"] = Nest.value($map,"label_type").$();

		if (Nest.value($map,"label_format").$() == SYSMAP_LABEL_ADVANCED_OFF) {
			continue;
		}

		switch (Nest.value($selement,"elementtype").$()) {
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				$selements[$selementId]["label_type"] = Nest.value($map,"label_type_hostgroup").$();
				if (Nest.value($map,"label_type_hostgroup").$() == MAP_LABEL_TYPE_CUSTOM) {
					$selements[$selementId]["label"] = Nest.value($map,"label_string_hostgroup").$();
				}
				break;

			case SYSMAP_ELEMENT_TYPE_HOST:
				$selements[$selementId]["label_type"] = Nest.value($map,"label_type_host").$();
				if (Nest.value($map,"label_type_host").$() == MAP_LABEL_TYPE_CUSTOM) {
					$selements[$selementId]["label"] = Nest.value($map,"label_string_host").$();
				}
				break;

			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				$selements[$selementId]["label_type"] = Nest.value($map,"label_type_trigger").$();
				if (Nest.value($map,"label_type_trigger").$() == MAP_LABEL_TYPE_CUSTOM) {
					$selements[$selementId]["label"] = Nest.value($map,"label_string_trigger").$();
				}
				break;

			case SYSMAP_ELEMENT_TYPE_MAP:
				$selements[$selementId]["label_type"] = Nest.value($map,"label_type_map").$();
				if (Nest.value($map,"label_type_map").$() == MAP_LABEL_TYPE_CUSTOM) {
					$selements[$selementId]["label"] = Nest.value($map,"label_string_map").$();
				}
				break;

			case SYSMAP_ELEMENT_TYPE_IMAGE:
				$selements[$selementId]["label_type"] = Nest.value($map,"label_type_image").$();
				if (Nest.value($map,"label_type_image").$() == MAP_LABEL_TYPE_CUSTOM) {
					$selements[$selementId]["label"] = Nest.value($map,"label_string_image").$();
				}
				break;
		}
	}

	for($selements as $selementId => $selement) {
		if (!isset($labelLines[$selementId])) {
			$labelLines[$selementId] = CArray.array();
		}
		if (!isset($statusLines[$selementId])) {
			$statusLines[$selementId] = CArray.array();
		}

		$msg = $resolveMacros ? resolveMapLabelMacrosAll($selement) : Nest.value($selement,"label").$();

		$allStrings .= $msg;
		$msgs = explode(\"\n\", $msg);
		for($msgs as $msg) {
			$labelLines[$selementId][] = CArray.array("msg" => $msg);
		}

		$elementInfo = $mapInfo[$selementId];

		for(CArray.array("problem", "unack", "maintenance", "ok", "status") as $caption) {
			if (!isset($elementInfo["info"][$caption]) || zbx_empty($elementInfo["info"][$caption]["msg"])) {
				continue;
			}

			$statusLines[$selementId][] = CArray.array(
				"msg" => $elementInfo["info"][$caption]["msg"],
				"color" => $elementInfo["info"][$caption]["color"]
			);

			$allStrings .= $elementInfo["info"][$caption]["msg"];
		}
	}

	$allLabelsSize = imageTextSize(8, 0, str_replace(\"\r\", "", str_replace(\"\n\", "", $allStrings)));
	$labelFontHeight = Nest.value($allLabelsSize,"height").$();
	$labelFontBaseline = Nest.value($allLabelsSize,"baseline").$();

	$elementsHostIds = CArray.array();
	for($selements as $selement) {
		if (Nest.value($selement,"label_type").$() != MAP_LABEL_TYPE_IP) {
			continue;
		}
		if (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST) {
			$elementsHostIds[] = Nest.value($selement,"elementid").$();
		}
	}

	if (!empty($elementsHostIds)) {
		$mapHosts = API.Host().get(CArray.array(
			"hostids" => $elementsHostIds,
			"output" => CArray.array("hostid"),
			"selectInterfaces" => API_OUTPUT_EXTEND
		));
		$mapHosts = zbx_toHash($mapHosts, "hostid");
	}

	// draw
	for($selements as $selementId => $selement) {
		if (empty($selement) || Nest.value($selement,"label_type").$() == MAP_LABEL_TYPE_NOTHING) {
			continue;
		}

		$elementInfo = $mapInfo[$selementId];

		$hl_color = null;
		$st_color = null;

		if (!isset(Nest.value(_REQUEST,"noselements").$()) && (Nest.value($map,"highlight").$() % 2) == SYSMAP_HIGHLIGHT_ON) {
			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_ON) {
				$hl_color = true;
			}
			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_MAINTENANCE) {
				$st_color = true;
			}
			if (Nest.value($elementInfo,"icon_type").$() == SYSMAP_ELEMENT_ICON_DISABLED) {
				$st_color = true;
			}
		}

		if (in_CArray.array(Nest.value($selement,"elementtype").$(), CArray.array(SYSMAP_ELEMENT_TYPE_HOST_GROUP, SYSMAP_ELEMENT_TYPE_MAP))
				&& !is_null($hl_color)) {
			$st_color = null;
		}
		elseif (!is_null($st_color)) {
			$hl_color = null;
		}

		$labelLocation = (is_null(Nest.value($selement,"label_location").$()) || Nest.value($selement,"label_location").$() < 0)
			? $map["label_location"]
			: Nest.value($selement,"label_location").$();

		$label = CArray.array();

		if (Nest.value($selement,"label_type").$() == MAP_LABEL_TYPE_IP && Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST) {
			$interface = reset($mapHosts[$selement["elementid"]]["interfaces"]);

			$label[] = CArray.array("msg" => Nest.value($interface,"ip").$());
			$label = array_merge($label, $statusLines[$selementId]);
		}
		elseif (Nest.value($selement,"label_type").$() == MAP_LABEL_TYPE_STATUS) {
			$label = $statusLines[$selementId];
		}
		elseif (Nest.value($selement,"label_type").$() == MAP_LABEL_TYPE_NAME) {
			$label[] = CArray.array("msg" => Nest.value($elementInfo,"name").$());
			$label = array_merge($label, $statusLines[$selementId]);
		}
		else {
			$label = array_merge($labelLines[$selementId], $statusLines[$selementId]);
		}

		if (empty($label)) {
			continue;
		}

		$w = 0;
		for($label as $str) {
			$dims = imageTextSize(8, 0, Nest.value($str,"msg").$());
			$w = max($w, Nest.value($dims,"width").$());
		}

		$h = count($label) * $labelFontHeight;
		$x = Nest.value($selement,"x").$();
		$y = Nest.value($selement,"y").$();

		$image = get_png_by_selement($elementInfo);
		$iconX = imagesx($image);
		$iconY = imagesy($image);

		if (!is_null($hl_color)) {
			$icon_hl = 14;
		}
		elseif (!is_null($st_color)) {
			$icon_hl = 6;
		}
		else {
			$icon_hl = 2;
		}

		switch ($labelLocation) {
			case MAP_LABEL_LOC_TOP:
				$y_rec = $y - $icon_hl - $h - 6;
				$x_rec = $x + $iconX / 2 - $w / 2;
				break;

			case MAP_LABEL_LOC_LEFT:
				$y_rec = $y - $h / 2 + $iconY / 2;
				$x_rec = $x - $icon_hl - $w;
				break;

			case MAP_LABEL_LOC_RIGHT:
				$y_rec = $y - $h / 2 + $iconY / 2;
				$x_rec = $x + $iconX + $icon_hl;
				break;

			case MAP_LABEL_LOC_BOTTOM:
			default:
				$y_rec = $y + $iconY + $icon_hl;
				$x_rec = $x + $iconX / 2 - $w / 2;
		}

		$increasey = 12;
		for($label as $line) {
			if (zbx_empty(Nest.value($line,"msg").$())) {
				continue;
			}

			$str = str_replace(\"\r\", "", Nest.value($line,"msg").$());
			$color = isset(Nest.value($line,"color").$()) ? Nest.value($line,"color").$() : Nest.value($colors,"Black").$();

			$dims = imageTextSize(8, 0, $str);

			if ($labelLocation == MAP_LABEL_LOC_TOP || $labelLocation == MAP_LABEL_LOC_BOTTOM) {
				$x_label = $x + ceil($iconX / 2) - ceil(Nest.value($dims,"width").$() / 2);
			}
			elseif ($labelLocation == MAP_LABEL_LOC_LEFT) {
				$x_label = $x_rec + $w - Nest.value($dims,"width").$();
			}
			else {
				$x_label = $x_rec;
			}

			imagefilledrectangle(
				$im,
				$x_label - 1, $y_rec + $increasey - $labelFontHeight + $labelFontBaseline,
				$x_label + Nest.value($dims,"width").$() + 1, $y_rec + $increasey + $labelFontBaseline,
				$colors["White"]
			);
			imagetext($im, 8, 0, $x_label, $y_rec + $increasey, $color, $str);

			$increasey += $labelFontHeight + 1;
		}
	}
}

/**
 * For each host group which is area for hosts virtual elements as hosts from that host group are created
 *
 * @param array $map
 * @return array areas with area coordinates and selementids
 */
function populateFromMapAreas(array &$map) {
	$areas = CArray.array();

	for(Nest.value($map,"selements").$() as $selement) {
		if (Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
			$area = CArray.array("selementids" => CArray.array());

			$origSelement = $selement;

			$hosts = API.host().get(CArray.array(
				"groupids" => Nest.value($selement,"elementid").$(),
				"sortfield" => "name",
				"output" => CArray.array("hostid"),
				"nopermissions" => true,
				"preservekeys" => true
			));
			$hostsCount = count($hosts);

			if ($hostsCount == 0) {
				continue;
			}

			if (Nest.value($selement,"areatype").$() == SYSMAP_ELEMENT_AREA_TYPE_CUSTOM) {
				Nest.value($area,"width").$() = Nest.value($selement,"width").$();
				Nest.value($area,"height").$() = Nest.value($selement,"height").$();
				Nest.value($area,"x").$() = Nest.value($selement,"x").$();
				Nest.value($area,"y").$() = Nest.value($selement,"y").$();
			}
			else {
				Nest.value($area,"width").$() = Nest.value($map,"width").$();
				Nest.value($area,"height").$() = Nest.value($map,"height").$();
				Nest.value($area,"x").$() = 0;
				Nest.value($area,"y").$() = 0;
			}

			for($hosts as $host) {
				Nest.value($selement,"elementtype").$() = SYSMAP_ELEMENT_TYPE_HOST;
				Nest.value($selement,"elementsubtype").$() = SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP;
				Nest.value($selement,"elementid").$() = Nest.value($host,"hostid").$();

				$newSelementid = rand(1, 9999999);
				while (isset($map["selements"][$newSelementid])) {
					$newSelementid += 1;
				};
				Nest.value($selement,"selementid").$() = $newSelementid;

				$area["selementids"][$newSelementid] = $newSelementid;
				$map["selements"][$newSelementid] = $selement;
			}

			$areas[] = $area;

			for(Nest.value($map,"links").$() as $link) {
				// do not multiply links between two areas
				if ($map["selements"][$link["selementid1"]]["elementsubtype"] == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS
						&& $map["selements"][$link["selementid2"]]["elementsubtype"] == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS) {
					continue;
				}

				$idNumber = null;
				if (Nest.value($link,"selementid1").$() == Nest.value($origSelement,"selementid").$()) {
					$idNumber = "selementid1";
				}
				elseif (Nest.value($link,"selementid2").$() == Nest.value($origSelement,"selementid").$()) {
					$idNumber = "selementid2";
				}

				if ($idNumber) {
					for(Nest.value($area,"selementids").$() as $newSelementid) {
						$newLinkid = rand(1, 9999999);
						while (isset($map["links"][$newLinkid])) {
							$newLinkid += 1;
						};

						Nest.value($link,"linkid").$() = $newLinkid;
						$link[$idNumber] = $newSelementid;
						$map["links"][$newLinkid] = $link;
					}
				}
			}
		}
	}

	return $areas;
}

/**
 * Calculates coordinates from elements inside areas
 *
 * @param array $map
 * @param array $areas
 * @param array $mapInfo
 *
 * @return void
 */
function processAreasCoordinates(array &$map, array $areas, array $mapInfo) {
	for($areas as $area) {
		$rowPlaceCount = ceil(sqrt(count(Nest.value($area,"selementids").$())));

		// offset from area borders
		Nest.value($area,"x").$() += 5;
		Nest.value($area,"y").$() += 5;
		Nest.value($area,"width").$() -= 5;
		Nest.value($area,"height").$() -= 5;

		$xOffset = floor(Nest.value($area,"width").$() / $rowPlaceCount);
		$yOffset = floor(Nest.value($area,"height").$() / $rowPlaceCount);

		$colNum = 0;
		$rowNum = 0;
		// some offset is required so that icon highlights are not drawn outside area
		$borderOffset = 20;
		for(Nest.value($area,"selementids").$() as $selementId) {
			$selement = $map["selements"][$selementId];

			$image = get_png_by_selement($mapInfo[$selementId]);
			$iconX = imagesx($image);
			$iconY = imagesy($image);

			$labelLocation = (is_null(Nest.value($selement,"label_location").$()) || (Nest.value($selement,"label_location").$() < 0))
				? Nest.value($map,"label_location").$() : Nest.value($selement,"label_location").$();
			switch ($labelLocation) {
				case MAP_LABEL_LOC_TOP:
					$newX = Nest.value($area,"x").$() + ($xOffset / 2) - ($iconX / 2);
					$newY = Nest.value($area,"y").$() + $yOffset - $iconY - ($iconY >= $iconX ? 0 : abs($iconX - $iconY) / 2) - $borderOffset;
					break;
				case MAP_LABEL_LOC_LEFT:
					$newX = Nest.value($area,"x").$() + $xOffset - $iconX - $borderOffset;
					$newY = Nest.value($area,"y").$() + ($yOffset / 2) - ($iconY / 2);
					break;
				case MAP_LABEL_LOC_RIGHT:
					$newX = Nest.value($area,"x").$() + $borderOffset;
					$newY = Nest.value($area,"y").$() + ($yOffset / 2) - ($iconY / 2);
					break;
				case MAP_LABEL_LOC_BOTTOM:
					$newX = Nest.value($area,"x").$() + ($xOffset / 2) - ($iconX / 2);
					$newY = Nest.value($area,"y").$() + abs($iconX - $iconY) / 2 + $borderOffset;
					break;
			}

			$map["selements"][$selementId]["x"] = $newX + ($colNum * $xOffset);
			$map["selements"][$selementId]["y"] = $newY + ($rowNum * $yOffset);

			$colNum++;
			if ($colNum == $rowPlaceCount) {
				$colNum = 0;
				$rowNum++;
			}
		}
	}
}

/**
 * Calculates area connector point on area perimeter
 *
 * @param int $ax      x area coordinate
 * @param int $ay      y area coordinate
 * @param int $aWidth  area width
 * @param int $aHeight area height
 * @param int $x2      x coordinate of connector second element
 * @param int $y2      y coordinate of connector second element
 *
 * @return array contains two values, x and y coordinates of new area connector point
 */
function calculateMapAreaLinkCoord($ax, $ay, $aWidth, $aHeight, $x2, $y2) {
	$dY = abs($y2 - $ay);
	$dX = abs($x2 - $ax);

	$halfHeight = $aHeight / 2;
	$halfWidth = $aWidth / 2;

	if ($dY == 0) {
		$ay = $y2;
		$ax = ($x2 < $ax) ? $ax - $halfWidth : $ax + $halfWidth;
	}
	elseif ($dX == 0) {
		$ay = ($y2 > $ay) ? $ay + $halfHeight : $ay - $halfHeight;
		$ax = $x2;
	}
	else {
		$koef = $halfHeight / $dY;

		$c = $dX * $koef;

		// if point is further than area diagonal, we should use calculations with width instead of height
		if (($halfHeight / $c) > ($halfHeight / $halfWidth)) {
			$ay = ($y2 > $ay) ? $ay + $halfHeight : $ay - $halfHeight;
			$ax = ($x2 < $ax) ? $ax - $c : $ax + $c;
		}
		else {
			$koef = $halfWidth / $dX;

			$c = $dY * $koef;

			$ay = ($y2 > $ay) ? $ay + $c : $ay - $c;
			$ax = ($x2 < $ax) ? $ax - $halfWidth : $ax + $halfWidth;
		}
	}

	return CArray.array($ax, $ay);
}

/**
 * Get icon id by mapping.
 *
 * @param array $iconMap
 * @param array $inventory
 *
 * @return int
 */
function getIconByMapping($iconMap, $inventory) {
	if (!empty(Nest.value($inventory,"inventory").$())) {
		$inventories = getHostInventories();

		for(Nest.value($iconMap,"mappings").$() as $mapping) {
			try {
				$expr = new GlobalRegExp(Nest.value($mapping,"expression").$());
				if ($expr.match($inventory["inventory"][$inventories[$mapping["inventory_link"]]["db_field"]])) {
					return Nest.value($mapping,"iconid").$();
				}
			}
			catch(Exception $e) {
				continue;
			}
		}
	}

	return Nest.value($iconMap,"default_iconid").$();
}

/**
 * Get parent maps for current map.
 *
 * @param int $mapId
 *
 * @return array
 */
function getParentMaps($mapId) {
	$parentMaps = DBfetchArrayAssoc(DBselect(
		"SELECT s.sysmapid,s.name".
			" FROM sysmaps s".
				" JOIN sysmaps_elements se ON se.sysmapid=s.sysmapid".
			" WHERE se.elementtype=".SYSMAP_ELEMENT_TYPE_MAP.
				" AND se.elementid=".zbx_dbstr($mapId)
	), "sysmapid");

	CArrayHelper::sort($parentMaps, CArray.array("name"));

	return $parentMaps;
}
