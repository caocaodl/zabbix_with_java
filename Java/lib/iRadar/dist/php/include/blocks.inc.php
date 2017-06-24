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


require_once dirname(__FILE__)."/graphs.inc.php";
require_once dirname(__FILE__)."/screens.inc.php";
require_once dirname(__FILE__)."/maps.inc.php";
require_once dirname(__FILE__)."/users.inc.php";

function make_favorite_graphs() {
	$favList = new CList(null, "favorites", _("No graphs added."));
	$graphids = CArray.array();
	$itemids = CArray.array();

	$fav_graphs = CFavorite::get("web.favorite.graphids");

	if (!$fav_graphs) {
		return $favList;
	}

	for($fav_graphs as $favorite) {
		if ("itemid" == Nest.value($favorite,"source").$()) {
			$itemids[$favorite["value"]] = Nest.value($favorite,"value").$();
		}
		else {
			$graphids[$favorite["value"]] = Nest.value($favorite,"value").$();
		}
	}

	if ($graphids) {
		$options = CArray.array(
			"graphids" => $graphids,
			"selectHosts" => CArray.array("hostid", "name"),
			"output" => CArray.array("graphid", "name"),
			"expandName" => true
		);
		$graphs = API.Graph().get($options);
		$graphs = zbx_toHash($graphs, "graphid");
	}

	if ($itemids) {
		$items = API.Item().get(CArray.array(
			"itemids" => $itemids,
			"selectHosts" => CArray.array("hostid", "name"),
			"output" => CArray.array("itemid", "hostid", "name", "key_"),
			"webitems" => true
		));
		$items = zbx_toHash($items, "itemid");

		$items = CMacrosResolverHelper::resolveItemNames($items);
	}

	for($fav_graphs as $favorite) {
		$sourceid = Nest.value($favorite,"value").$();

		if (Nest.value($favorite,"source").$() == "itemid") {
			if (!isset($items[$sourceid])) {
				continue;
			}

			$item = $items[$sourceid];
			$host = reset(Nest.value($item,"hosts").$());

			$link = new CLink(
				get_node_name_by_elid($sourceid, null, NAME_DELIMITER).$host["name"].NAME_DELIMITER.Nest.value($item,"name_expanded").$(),
				"history.php?action=showgraph&itemid=".$sourceid
			);
			$link.setTarget("blank");
		}
		else {
			if (!isset($graphs[$sourceid])) {
				continue;
			}

			$graph = $graphs[$sourceid];
			$ghost = reset(Nest.value($graph,"hosts").$());

			$link = new CLink(
				get_node_name_by_elid($sourceid, null, NAME_DELIMITER).$ghost["name"].NAME_DELIMITER.Nest.value($graph,"name").$(),
				"charts.php?graphid=".$sourceid
			);
			$link.setTarget("blank");
		}

		$favList.addItem($link, "nowrap");
	}

	return $favList;
}

function make_favorite_screens() {
	$favList = new CList(null, "favorites", _("No screens added."));
	$fav_screens = CFavorite::get("web.favorite.screenids");

	if (!$fav_screens) {
		return $favList;
	}

	$screenids = CArray.array();
	for($fav_screens as $favorite) {
		if ("screenid" == Nest.value($favorite,"source").$()) {
			$screenids[$favorite["value"]] = Nest.value($favorite,"value").$();
		}
	}

	$options = CArray.array(
		"screenids" => $screenids,
		"output" => CArray.array("screenid", "name")
	);
	$screens = API.Screen().get($options);
	$screens = zbx_toHash($screens, "screenid");

	for($fav_screens as $favorite) {
		$source = Nest.value($favorite,"source").$();
		$sourceid = Nest.value($favorite,"value").$();

		if ("slideshowid" == $source) {
			if (!slideshow_accessible($sourceid, PERM_READ)) {
				continue;
			}
			if (!$slide = get_slideshow_by_slideshowid($sourceid)) {
				continue;
			}

			$link = new CLink(get_node_name_by_elid($sourceid, null, NAME_DELIMITER).Nest.value($slide,"name").$(), "slides.php?elementid=".$sourceid);
			$link.setTarget("blank");
		}
		else {
			if (!isset($screens[$sourceid])) {
				continue;
			}
			$screen = $screens[$sourceid];

			$link = new CLink(get_node_name_by_elid($sourceid, null, NAME_DELIMITER).Nest.value($screen,"name").$(), "screens.php?elementid=".$sourceid);
			$link.setTarget("blank");
		}
		$favList.addItem($link, "nowrap");
	}
	return $favList;
}

function make_favorite_maps() {
	$favList = new CList(null, "favorites", _("No maps added."));
	$fav_sysmaps = CFavorite::get("web.favorite.sysmapids");

	if (!$fav_sysmaps) {
		return $favList;
	}

	$sysmapids = CArray.array();
	for($fav_sysmaps as $favorite) {
		$sysmapids[$favorite["value"]] = Nest.value($favorite,"value").$();
	}

	$sysmaps = API.Map().get(CArray.array(
		"sysmapids" => $sysmapids,
		"output" => CArray.array("sysmapid", "name")
	));
	for($sysmaps as $sysmap) {
		$sysmapid = Nest.value($sysmap,"sysmapid").$();

		$link = new CLink(get_node_name_by_elid($sysmapid, null, NAME_DELIMITER).Nest.value($sysmap,"name").$(), "maps.php?sysmapid=".$sysmapid);
		$link.setTarget("blank");

		$favList.addItem($link, "nowrap");
	}
	return $favList;
}

function make_system_status($filter) {
	$showAllNodes = is_show_all_nodes();

	$ackParams = CArray.array();
	if (!empty(Nest.value($filter,"screenid").$())) {
		Nest.value($ackParams,"screenid").$() = Nest.value($filter,"screenid").$();
	}

	$table = new CTableInfo(_("No host groups found."));
	$table.setHeader(CArray.array(
		$showAllNodes ? _("Node") : null,
		_("Host group"),
		(is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_DISASTER])) ? getSeverityCaption(TRIGGER_SEVERITY_DISASTER) : null,
		(is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_HIGH])) ? getSeverityCaption(TRIGGER_SEVERITY_HIGH) : null,
		(is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_AVERAGE])) ? getSeverityCaption(TRIGGER_SEVERITY_AVERAGE) : null,
		(is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_WARNING])) ? getSeverityCaption(TRIGGER_SEVERITY_WARNING) : null,
		(is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_INFORMATION])) ? getSeverityCaption(TRIGGER_SEVERITY_INFORMATION) : null,
		(is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_NOT_CLASSIFIED])) ? getSeverityCaption(TRIGGER_SEVERITY_NOT_CLASSIFIED) : null
	));

	// get host groups
	$groups = API.HostGroup().get(CArray.array(
		"groupids" => Nest.value($filter,"groupids").$(),
		"hostids" => isset(Nest.value($filter,"hostids").$()) ? Nest.value($filter,"hostids").$() : null,
		"nodeids" => get_current_nodeid(),
		"monitored_hosts" => true,
		"output" => CArray.array("groupid", "name"),
		"preservekeys" => true
	));

	$sortOptions = CArray.array();
	if ($showAllNodes) {
		$groupNodeNames = getNodeNamesByElids(array_keys($groups));

		for($groups as &$group) {
			Nest.value($group,"nodename").$() = $groupNodeNames[$group["groupid"]];
		}
		unset($group);

		$sortOptions[] = CArray.array("field" => "nodename", "order" => ZBX_SORT_UP);
	}

	$sortOptions[] = CArray.array("field" => "name", "order" => ZBX_SORT_UP);

	CArrayHelper::sort($groups, $sortOptions);

	$groupIds = CArray.array();
	for($groups as $group) {
		$groupIds[$group["groupid"]] = Nest.value($group,"groupid").$();

		Nest.value($group,"tab_priority").$() = CArray.array(
			TRIGGER_SEVERITY_DISASTER => CArray.array("count" => 0, "triggers" => CArray.array(), "count_unack" => 0, "triggers_unack" => CArray.array()),
			TRIGGER_SEVERITY_HIGH => CArray.array("count" => 0, "triggers" => CArray.array(), "count_unack" => 0, "triggers_unack" => CArray.array()),
			TRIGGER_SEVERITY_AVERAGE => CArray.array("count" => 0, "triggers" => CArray.array(), "count_unack" => 0, "triggers_unack" => CArray.array()),
			TRIGGER_SEVERITY_WARNING => CArray.array("count" => 0, "triggers" => CArray.array(), "count_unack" => 0, "triggers_unack" => CArray.array()),
			TRIGGER_SEVERITY_INFORMATION => CArray.array("count" => 0, "triggers" => CArray.array(), "count_unack" => 0, "triggers_unack" => CArray.array()),
			TRIGGER_SEVERITY_NOT_CLASSIFIED => CArray.array("count" => 0, "triggers" => CArray.array(), "count_unack" => 0, "triggers_unack" => CArray.array())
		);
		$groups[$group["groupid"]] = $group;
	}

	// get triggers
	$triggers = API.Trigger().get(CArray.array(
		"nodeids" => get_current_nodeid(),
		"groupids" => $groupIds,
		"hostids" => isset(Nest.value($filter,"hostids").$()) ? Nest.value($filter,"hostids").$() : null,
		"monitored" => true,
		"maintenance" => Nest.value($filter,"maintenance").$(),
		"skipDependent" => true,
		"withLastEventUnacknowledged" => (Nest.value($filter,"extAck").$() == EXTACK_OPTION_UNACK) ? true : null,
		"selectLastEvent" => CArray.array("eventid", "acknowledged", "objectid"),
		"expandDescription" => true,
		"filter" => CArray.array(
			"priority" => Nest.value($filter,"severity").$(),
			"value" => TRIGGER_VALUE_TRUE
		),
		"sortfield" => "lastchange",
		"sortorder" => ZBX_SORT_DOWN,
		"output" => CArray.array("triggerid", "priority", "state", "description", "error", "value", "lastchange"),
		"selectHosts" => CArray.array("name"),
		"preservekeys" => true
	));

	$eventIds = CArray.array();

	for($triggers as $triggerId => $trigger) {
		if (Nest.value($trigger,"lastEvent").$()) {
			$eventIds[$trigger["lastEvent"]["eventid"]] = Nest.value($trigger,"lastEvent","eventid").$();
		}

		$triggers[$triggerId]["event"] = Nest.value($trigger,"lastEvent").$();
		unset($triggers[$triggerId]["lastEvent"]);
	}

	// get acknowledges
	if ($eventIds) {
		$eventAcknowledges = API.Event().get(CArray.array(
			"eventids" => $eventIds,
			"select_acknowledges" => CArray.array("eventid", "clock", "message", "alias", "name", "surname"),
			"preservekeys" => true
		));
	}

	// actions
	$actions = getEventActionsStatus($eventIds);

	// triggers
	for($triggers as $trigger) {
		// event
		if (Nest.value($trigger,"event").$()) {
			Nest.value($trigger,"event","acknowledges").$() = isset($eventAcknowledges[$trigger["event"]["eventid"]])
				? $eventAcknowledges[$trigger["event"]["eventid"]]["acknowledges"]
				: 0;
		}
		else {
			Nest.value($trigger,"event").$() = CArray.array(
				"acknowledged" => false,
				"clock" => Nest.value($trigger,"lastchange").$(),
				"value" => $trigger["value"]
			);
		}

		// groups
		for(Nest.value($trigger,"groups").$() as $group) {
			if (!isset($groups[$group["groupid"]])) {
				continue;
			}

			if (in_CArray.array(Nest.value($filter,"extAck").$(), CArray.array(EXTACK_OPTION_ALL, EXTACK_OPTION_BOTH))) {
				$groups[$group["groupid"]]["tab_priority"][$trigger["priority"]]["count"]++;

				if ($groups[$group["groupid"]]["tab_priority"][$trigger["priority"]]["count"] < 30) {
					$groups[$group["groupid"]]["tab_priority"][$trigger["priority"]]["triggers"][] = $trigger;
				}
			}

			if (in_CArray.array(Nest.value($filter,"extAck").$(), CArray.array(EXTACK_OPTION_UNACK, EXTACK_OPTION_BOTH))
					&& isset(Nest.value($trigger,"event").$()) && !Nest.value($trigger,"event","acknowledged").$()) {
				$groups[$group["groupid"]]["tab_priority"][$trigger["priority"]]["count_unack"]++;

				if ($groups[$group["groupid"]]["tab_priority"][$trigger["priority"]]["count_unack"] < 30) {
					$groups[$group["groupid"]]["tab_priority"][$trigger["priority"]]["triggers_unack"][] = $trigger;
				}
			}
		}
	}
	unset($triggers);

	$config = select_config();

	for($groups as $group) {
		$groupRow = new CRow();

		if ($showAllNodes) {
			$groupRow.addItem(Nest.value($group,"nodename").$());
		}

		$name = new CLink(Nest.value($group,"name").$(), "tr_status.php?groupid=".$group["groupid"]."&hostid=0&show_triggers=".TRIGGERS_OPTION_ONLYTRUE);
		$groupRow.addItem($name);

		for(Nest.value($group,"tab_priority").$() as $severity => $data) {
			if (!is_null(Nest.value($filter,"severity").$()) && !isset($filter["severity"][$severity])) {
				continue;
			}

			$allTriggersNum = Nest.value($data,"count").$();
			if ($allTriggersNum) {
				$allTriggersNum = new CSpan($allTriggersNum, "pointer");
				$allTriggersNum.setHint(makeTriggersPopup(Nest.value($data,"triggers").$(), $ackParams, $actions, $config));
			}

			$unackTriggersNum = Nest.value($data,"count_unack").$();
			if ($unackTriggersNum) {
				$unackTriggersNum = new CSpan($unackTriggersNum, "pointer red bold");
				$unackTriggersNum.setHint(makeTriggersPopup(Nest.value($data,"triggers_unack").$(), $ackParams, $actions, $config));
			}

			switch (Nest.value($filter,"extAck").$()) {
				case EXTACK_OPTION_ALL:
					$groupRow.addItem(getSeverityCell($severity, $allTriggersNum, !$allTriggersNum));
					break;

				case EXTACK_OPTION_UNACK:
					$groupRow.addItem(getSeverityCell($severity, $unackTriggersNum, !$unackTriggersNum));
					break;

				case EXTACK_OPTION_BOTH:
					if ($unackTriggersNum) {
						$span = new CSpan(SPACE._("of").SPACE);
						$unackTriggersNum = new CSpan($unackTriggersNum);
					}
					else {
						$span = null;
						$unackTriggersNum = null;
					}

					$groupRow.addItem(getSeverityCell($severity, CArray.array($unackTriggersNum, $span, $allTriggersNum), !$allTriggersNum));
					break;
			}
		}
		$table.addRow($groupRow);
	}

	$script = new CJSScript(get_js(\"jQuery("#hat_syssum_footer").html("\"._s("Updated: %s", zbx_date2str(_("H:i:s"))).\"")\"));

	return new CDiv(CArray.array($table, $script));
}

function make_hoststat_summary($filter) {
	$table = new CTableInfo(_("No host groups found."));
	$table.setHeader(CArray.array(
		is_show_all_nodes() ? _("Node") : null,
		_("Host group"),
		_("Without problems"),
		_("With problems"),
		_("Total")
	));

	// get host groups
	$groups = API.HostGroup().get(CArray.array(
		"nodeids" => get_current_nodeid(),
		"groupids" => Nest.value($filter,"groupids").$(),
		"monitored_hosts" => 1,
		"output" => CArray.array("groupid", "name")
	));
	$groups = zbx_toHash($groups, "groupid");

	for($groups as &$group) {
		Nest.value($group,"nodename").$() = get_node_name_by_elid(Nest.value($group,"groupid").$());
	}
	unset($group);

	CArrayHelper::sort($groups, CArray.array(
		CArray.array("field" => "nodename", "order" => ZBX_SORT_UP),
		CArray.array("field" => "name", "order" => ZBX_SORT_UP)
	));

	// get hosts
	$hosts = API.Host().get(CArray.array(
		"nodeids" => get_current_nodeid(),
		"groupids" => zbx_objectValues($groups, "groupid"),
		"hostids" => !empty(Nest.value($filter,"hostids").$()) ? Nest.value($filter,"hostids").$() : null,
		"monitored_hosts" => true,
		"filter" => CArray.array("maintenance_status" => Nest.value($filter,"maintenance").$()),
		"output" => CArray.array("hostid", "name"),
		"selectGroups" => CArray.array("groupid")
	));
	$hosts = zbx_toHash($hosts, "hostid");
	CArrayHelper::sort($hosts, CArray.array("name"));

	// get triggers
	$triggers = API.Trigger().get(CArray.array(
		"nodeids" => get_current_nodeid(),
		"monitored" => true,
		"maintenance" => Nest.value($filter,"maintenance").$(),
		"expandData" => true,
		"filter" => CArray.array(
			"priority" => Nest.value($filter,"severity").$(),
			"value" => TRIGGER_VALUE_TRUE
		),
		"output" => CArray.array("triggerid", "priority"),
		"selectHosts" => CArray.array("hostid")
	));

	if (Nest.value($filter,"extAck").$()) {
		$triggers_unack = API.Trigger().get(CArray.array(
			"nodeids" => get_current_nodeid(),
			"monitored" => true,
			"maintenance" => Nest.value($filter,"maintenance").$(),
			"withLastEventUnacknowledged" => true,
			"selectHosts" => API_OUTPUT_REFER,
			"filter" => CArray.array(
				"priority" => Nest.value($filter,"severity").$(),
				"value" => TRIGGER_VALUE_TRUE
			),
			"output" => API_OUTPUT_REFER
		));
		$triggers_unack = zbx_toHash($triggers_unack, "triggerid");
		for($triggers_unack as $tunack) {
			for(Nest.value($tunack,"hosts").$() as $unack_host) {
				$hosts_with_unack_triggers[$unack_host["hostid"]] = Nest.value($unack_host,"hostid").$();
			}
		}
	}

	$hosts_data = CArray.array();
	$problematic_host_list = CArray.array();
	$lastUnack_host_list = CArray.array();
	$highest_severity = CArray.array();
	$highest_severity2 = CArray.array();

	for($triggers as $trigger) {
		for(Nest.value($trigger,"hosts").$() as $trigger_host) {
			if (!isset($hosts[$trigger_host["hostid"]])) {
				continue;
			}
			else {
				$host = $hosts[$trigger_host["hostid"]];
			}

			if (Nest.value($filter,"extAck").$() && isset($hosts_with_unack_triggers[$host["hostid"]])) {
				if (!isset($lastUnack_host_list[$host["hostid"]])) {
					$lastUnack_host_list[$host["hostid"]] = CArray.array();
					$lastUnack_host_list[$host["hostid"]]["host"] = Nest.value($host,"name").$();
					$lastUnack_host_list[$host["hostid"]]["hostid"] = Nest.value($host,"hostid").$();
					$lastUnack_host_list[$host["hostid"]]["severities"] = CArray.array();
					$lastUnack_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_DISASTER] = 0;
					$lastUnack_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_HIGH] = 0;
					$lastUnack_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_AVERAGE] = 0;
					$lastUnack_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_WARNING] = 0;
					$lastUnack_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_INFORMATION] = 0;
					$lastUnack_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_NOT_CLASSIFIED] = 0;
				}
				if (isset($triggers_unack[$trigger["triggerid"]])) {
					$lastUnack_host_list[$host["hostid"]]["severities"][$trigger["priority"]]++;
				}

				for(Nest.value($host,"groups").$() as $gnum => $group) {
					if (!isset($highest_severity2[$group["groupid"]])) {
						$highest_severity2[$group["groupid"]] = 0;
					}

					if (Nest.value($trigger,"priority").$() > $highest_severity2[$group["groupid"]]) {
						$highest_severity2[$group["groupid"]] = Nest.value($trigger,"priority").$();
					}

					if (!isset($hosts_data[$group["groupid"]])) {
						$hosts_data[$group["groupid"]] = CArray.array(
							"problematic" => 0,
							"ok" => 0,
							"lastUnack" => 0,
							"hostids_all" => CArray.array(),
							"hostids_unack" => CArray.array()
						);
					}

					if (!isset($hosts_data[$group["groupid"]]["hostids_unack"][$host["hostid"]])) {
						$hosts_data[$group["groupid"]]["hostids_unack"][$host["hostid"]] = Nest.value($host,"hostid").$();
						$hosts_data[$group["groupid"]]["lastUnack"]++;
					}
				}
			}

			if (!isset($problematic_host_list[$host["hostid"]])) {
				$problematic_host_list[$host["hostid"]] = CArray.array();
				$problematic_host_list[$host["hostid"]]["host"] = Nest.value($host,"name").$();
				$problematic_host_list[$host["hostid"]]["hostid"] = Nest.value($host,"hostid").$();
				$problematic_host_list[$host["hostid"]]["severities"] = CArray.array();
				$problematic_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_DISASTER] = 0;
				$problematic_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_HIGH] = 0;
				$problematic_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_AVERAGE] = 0;
				$problematic_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_WARNING] = 0;
				$problematic_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_INFORMATION] = 0;
				$problematic_host_list[$host["hostid"]]["severities"][TRIGGER_SEVERITY_NOT_CLASSIFIED] = 0;
			}
			$problematic_host_list[$host["hostid"]]["severities"][$trigger["priority"]]++;

			for(Nest.value($host,"groups").$() as $gnum => $group) {
				if (!isset($highest_severity[$group["groupid"]])) {
					$highest_severity[$group["groupid"]] = 0;
				}

				if (Nest.value($trigger,"priority").$() > $highest_severity[$group["groupid"]]) {
					$highest_severity[$group["groupid"]] = Nest.value($trigger,"priority").$();
				}

				if (!isset($hosts_data[$group["groupid"]])) {
					$hosts_data[$group["groupid"]] = CArray.array(
						"problematic" => 0,
						"ok" => 0,
						"lastUnack" => 0,
						"hostids_all" => CArray.array(),
						"hostids_unack" => CArray.array()
					);
				}

				if (!isset($hosts_data[$group["groupid"]]["hostids_all"][$host["hostid"]])) {
					$hosts_data[$group["groupid"]]["hostids_all"][$host["hostid"]] = Nest.value($host,"hostid").$();
					$hosts_data[$group["groupid"]]["problematic"]++;
				}
			}
		}
	}

	for($hosts as $host) {
		for(Nest.value($host,"groups").$() as $group) {
			if (!isset($groups[$group["groupid"]])) {
				continue;
			}

			if (!isset($groups[$group["groupid"]]["hosts"])) {
				$groups[$group["groupid"]]["hosts"] = CArray.array();
			}
			$groups[$group["groupid"]]["hosts"][$host["hostid"]] = CArray.array("hostid" => Nest.value($host,"hostid").$());

			if (!isset($highest_severity[$group["groupid"]])) {
				$highest_severity[$group["groupid"]] = 0;
			}

			if (!isset($hosts_data[$group["groupid"]])) {
				$hosts_data[$group["groupid"]] = CArray.array("problematic" => 0, "ok" => 0, "lastUnack" => 0);
			}

			if (!isset($problematic_host_list[$host["hostid"]])) {
				$hosts_data[$group["groupid"]]["ok"]++;
			}
		}
	}

	for($groups as $group) {
		if (!isset($hosts_data[$group["groupid"]])) {
			continue;
		}

		$group_row = new CRow();
		if (is_show_all_nodes()) {
			$group_row.addItem(Nest.value($group,"nodename").$());
		}

		$name = new CLink(Nest.value($group,"name").$(), "tr_status.php?groupid=".$group["groupid"]."&hostid=0&show_triggers=".TRIGGERS_OPTION_ONLYTRUE);
		$group_row.addItem($name);
		$group_row.addItem(new CCol($hosts_data[$group["groupid"]]["ok"], "normal"));

		if (Nest.value($filter,"extAck").$()) {
			if ($hosts_data[$group["groupid"]]["lastUnack"]) {
				$table_inf = new CTableInfo();
				$table_inf.setAttribute("style", "width: 400px;");
				$table_inf.setHeader(CArray.array(
					_("Host"),
					is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_DISASTER]) ? getSeverityCaption(TRIGGER_SEVERITY_DISASTER) : null,
					is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_HIGH]) ? getSeverityCaption(TRIGGER_SEVERITY_HIGH) : null,
					is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_AVERAGE]) ? getSeverityCaption(TRIGGER_SEVERITY_AVERAGE) : null,
					is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_WARNING]) ? getSeverityCaption(TRIGGER_SEVERITY_WARNING) : null,
					is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_INFORMATION]) ? getSeverityCaption(TRIGGER_SEVERITY_INFORMATION) : null,
					is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_NOT_CLASSIFIED]) ? getSeverityCaption(TRIGGER_SEVERITY_NOT_CLASSIFIED) : null
				));
				$popup_rows = 0;

				for(Nest.value($group,"hosts").$() as $host) {
					$hostid = Nest.value($host,"hostid").$();
					if (!isset($lastUnack_host_list[$hostid])) {
						continue;
					}

					if ($popup_rows >= ZBX_WIDGET_ROWS) {
						break;
					}
					$popup_rows++;

					$host_data = $lastUnack_host_list[$hostid];

					$r = new CRow();
					$r.addItem(new CLink(Nest.value($host_data,"host").$(), "tr_status.php?groupid=".$group["groupid"]."&hostid=".$hostid."&show_triggers=".TRIGGERS_OPTION_ONLYTRUE));

					for($lastUnack_host_list[$host["hostid"]]["severities"] as $severity => $trigger_count) {
						if (!is_null(Nest.value($filter,"severity").$()) && !isset($filter["severity"][$severity])) {
							continue;
						}
						$r.addItem(new CCol($trigger_count, getSeverityStyle($severity, $trigger_count)));
					}
					$table_inf.addRow($r);
				}
				$lastUnack_count = new CSpan($hosts_data[$group["groupid"]]["lastUnack"], "pointer red bold");
				$lastUnack_count.setHint($table_inf);
			}
			else {
				$lastUnack_count = 0;
			}
		}

		// if hostgroup contains problematic hosts, hint should be built
		if ($hosts_data[$group["groupid"]]["problematic"]) {
			$table_inf = new CTableInfo();
			$table_inf.setAttribute("style", "width: 400px;");
			$table_inf.setHeader(CArray.array(
				_("Host"),
				is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_DISASTER]) ? getSeverityCaption(TRIGGER_SEVERITY_DISASTER) : null,
				is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_HIGH]) ? getSeverityCaption(TRIGGER_SEVERITY_HIGH) : null,
				is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_AVERAGE]) ? getSeverityCaption(TRIGGER_SEVERITY_AVERAGE) : null,
				is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_WARNING]) ? getSeverityCaption(TRIGGER_SEVERITY_WARNING) : null,
				is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_INFORMATION]) ? getSeverityCaption(TRIGGER_SEVERITY_INFORMATION) : null,
				is_null(Nest.value($filter,"severity").$()) || isset($filter["severity"][TRIGGER_SEVERITY_NOT_CLASSIFIED]) ? getSeverityCaption(TRIGGER_SEVERITY_NOT_CLASSIFIED) : null
			));
			$popup_rows = 0;

			for(Nest.value($group,"hosts").$() as $host) {
				$hostid = Nest.value($host,"hostid").$();
				if (!isset($problematic_host_list[$hostid])) {
					continue;
				}
				if ($popup_rows >= ZBX_WIDGET_ROWS) {
					break;
				}
				$popup_rows++;

				$host_data = $problematic_host_list[$hostid];

				$r = new CRow();
				$r.addItem(new CLink(Nest.value($host_data,"host").$(), "tr_status.php?groupid=".$group["groupid"]."&hostid=".$hostid."&show_triggers=".TRIGGERS_OPTION_ONLYTRUE));

				for($problematic_host_list[$host["hostid"]]["severities"] as $severity => $trigger_count) {
					if (!is_null(Nest.value($filter,"severity").$())&&!isset($filter["severity"][$severity])) {
						continue;
					}
					$r.addItem(new CCol($trigger_count, getSeverityStyle($severity, $trigger_count)));
				}
				$table_inf.addRow($r);
			}
			$problematic_count = new CSpan($hosts_data[$group["groupid"]]["problematic"], "pointer");
			$problematic_count.setHint($table_inf);
		}
		else {
			$problematic_count = 0;
		}

		switch (Nest.value($filter,"extAck").$()) {
			case EXTACK_OPTION_ALL:
				$group_row.addItem(new CCol(
					$problematic_count,
					getSeverityStyle($highest_severity[$group["groupid"]], $hosts_data[$group["groupid"]]["problematic"]))
				);
				$group_row.addItem($hosts_data[$group["groupid"]]["problematic"] + $hosts_data[$group["groupid"]]["ok"]);
				break;
			case EXTACK_OPTION_UNACK:
				$group_row.addItem(new CCol(
					$lastUnack_count,
					getSeverityStyle((isset($highest_severity2[$group["groupid"]]) ? $highest_severity2[$group["groupid"]] : 0),
						$hosts_data[$group["groupid"]]["lastUnack"]))
				);
				$group_row.addItem($hosts_data[$group["groupid"]]["lastUnack"] + $hosts_data[$group["groupid"]]["ok"]);
				break;
			case EXTACK_OPTION_BOTH:
				$unackspan = $lastUnack_count ? new CSpan(CArray.array($lastUnack_count, SPACE._("of").SPACE)) : null;
				$group_row.addItem(new CCol(CArray.array(
					$unackspan, $problematic_count),
					getSeverityStyle($highest_severity[$group["groupid"]], $hosts_data[$group["groupid"]]["problematic"]))
				);
				$group_row.addItem($hosts_data[$group["groupid"]]["problematic"] + $hosts_data[$group["groupid"]]["ok"]);
				break;
		}
		$table.addRow($group_row);
	}

	$script = new CJSScript(get_js(\"jQuery("#hat_hoststat_footer").html("\"._s("Updated: %s", zbx_date2str(_("H:i:s"))).\"")\"));

	return new CDiv(CArray.array($table, $script));
}

function make_status_of_zbx() {
	global $ZBX_SERVER, $ZBX_SERVER_PORT;

	$table = new CTableInfo();
	$table.setHeader(CArray.array(
		_("Parameter"),
		_("Value"),
		_("Details")
	));

	show_messages(); // because in function get_status(); function clear_messages() is called when fsockopen() fails.
	$status = get_status();

	$table.addRow(CArray.array(
		_("Zabbix server is running"),
		new CSpan(Nest.value($status,"zabbix_server").$(), (Nest.value($status,"zabbix_server").$() == _("Yes") ? "off" : "on")),
		isset($ZBX_SERVER, $ZBX_SERVER_PORT) ? $ZBX_SERVER.":".$ZBX_SERVER_PORT : _("Zabbix server IP or port is not set!")
	));
	$title = new CSpan(_("Number of hosts (monitored/not monitored/templates)"));
	$title.setAttribute("title", "asdad");
	$table.addRow(CArray.array(_("Number of hosts (monitored/not monitored/templates)"), Nest.value($status,"hosts_count").$(),
		CArray.array(
			new CSpan(Nest.value($status,"hosts_count_monitored").$(), "off"), " / ",
			new CSpan(Nest.value($status,"hosts_count_not_monitored").$(), "on"), " / ",
			new CSpan(Nest.value($status,"hosts_count_template").$(), "unknown")
		)
	));
	$title = new CSpan(_("Number of items (monitored/disabled/not supported)"));
	$title.setAttribute("title", _("Only items assigned to enabled hosts are counted"));
	$table.addRow(CArray.array($title, Nest.value($status,"items_count").$(),
		CArray.array(
			new CSpan(Nest.value($status,"items_count_monitored").$(), "off"), " / ",
			new CSpan(Nest.value($status,"items_count_disabled").$(), "on"), " / ",
			new CSpan(Nest.value($status,"items_count_not_supported").$(), "unknown")
		)
	));
	$title = new CSpan(_("Number of triggers (enabled/disabled) [problem/ok]"));
	$title.setAttribute("title", _("Only triggers assigned to enabled hosts and depending on enabled items are counted"));
	$table.addRow(CArray.array($title, Nest.value($status,"triggers_count").$(),
		CArray.array(
			Nest.value($status,"triggers_count_enabled").$(), " / ",
			Nest.value($status,"triggers_count_disabled").$(), " [",
			new CSpan(Nest.value($status,"triggers_count_on").$(), "on"), " / ",
			new CSpan(Nest.value($status,"triggers_count_off").$(), "off"), "]"
		)
	));
	$table.addRow(CArray.array(_("Number of users (online)"), Nest.value($status,"users_count").$(), new CSpan(Nest.value($status,"users_online").$(), "green")));
	$table.addRow(CArray.array(_("Required server performance, new values per second"), Nest.value($status,"qps_total").$(), " - "));

	// check requirements
	if (CWebUser::Nest.value($data,"type").$() == USER_TYPE_SUPER_ADMIN) {
		$frontendSetup = new FrontendSetup();
		$reqs = $frontendSetup.checkRequirements();
		for($reqs as $req) {
			if (Nest.value($req,"result").$() != FrontendSetup::CHECK_OK) {
				$class = (Nest.value($req,"result").$() == FrontendSetup::CHECK_WARNING) ? "notice" : "fail";
				$table.addRow(CArray.array(
					new CSpan(Nest.value($req,"name").$(), $class),
					new CSpan(Nest.value($req,"current").$(), $class),
					new CSpan(Nest.value($req,"error").$(), $class)
				));
			}
		}
	}
	$script = new CJSScript(get_js(\"jQuery("#hat_stszbx_footer").html("\"._s("Updated: %s", zbx_date2str(_("H:i:s"))).\"")\"));
	return new CDiv(CArray.array($table, $script));
}

/**
 * Create DIV with latest problem triggers.
 *
 * If no sortfield and sortorder are defined, the sort indicater in the column name will not be displayed.
 *
 * @param array  $filter["screenid"]
 * @param array  $filter["groupids"]
 * @param array  $filter["hostids"]
 * @param array  $filter["maintenance"]
 * @param int    $filter["extAck"]
 * @param int    $filter["severity"]
 * @param int    $filter["limit"]
 * @param string $filter["sortfield"]
 * @param string $filter["sortorder"]
 * @param string $filter["backUrl"]
 *
 * @return CDiv
 */
function make_latest_issues(array $filter = CArray.array()) {
	// hide the sort indicator if no sortfield and sortorder are given
	$showSortIndicator = isset(Nest.value($filter,"sortfield").$()) || isset(Nest.value($filter,"sortorder").$());

	if (isset(Nest.value($filter,"sortfield").$()) && Nest.value($filter,"sortfield").$() !== "lastchange") {
		$sortField = CArray.array(Nest.value($filter,"sortfield").$(), "lastchange");
		$sortOrder = CArray.array(Nest.value($filter,"sortorder").$(), ZBX_SORT_DOWN);
	}
	else {
		$sortField = CArray.array("lastchange");
		$sortOrder = CArray.array(ZBX_SORT_DOWN);
	}

	$options = CArray.array(
		"groupids" => Nest.value($filter,"groupids").$(),
		"hostids" => isset(Nest.value($filter,"hostids").$()) ? Nest.value($filter,"hostids").$() : null,
		"monitored" => true,
		"maintenance" => Nest.value($filter,"maintenance").$(),
		"filter" => CArray.array(
			"priority" => Nest.value($filter,"severity").$(),
			"value" => TRIGGER_VALUE_TRUE
		)
	);

	$triggers = API.Trigger().get(array_merge($options, CArray.array(
		"withLastEventUnacknowledged" => (isset(Nest.value($filter,"extAck").$()) && Nest.value($filter,"extAck").$() == EXTACK_OPTION_UNACK)
			? true
			: null,
		"skipDependent" => true,
		"output" => CArray.array("triggerid", "state", "error", "url", "expression", "description", "priority", "lastchange"),
		"selectHosts" => CArray.array("hostid", "name"),
		"selectLastEvent" => CArray.array("eventid", "acknowledged", "objectid", "clock", "ns"),
		"sortfield" => $sortField,
		"sortorder" => $sortOrder,
		"limit" => isset(Nest.value($filter,"limit").$()) ? Nest.value($filter,"limit").$() : DEFAULT_LATEST_ISSUES_CNT
	)));

	// don't use withLastEventUnacknowledged and skipDependent because of performance issues
	$triggersTotalCount = API.Trigger().get(array_merge($options, CArray.array(
		"countOutput" => true
	)));

	// get acknowledges
	$eventIds = CArray.array();
	for($triggers as $trigger) {
		if (Nest.value($trigger,"lastEvent").$()) {
			$eventIds[] = Nest.value($trigger,"lastEvent","eventid").$();
		}
	}
	if ($eventIds) {
		$eventAcknowledges = API.Event().get(CArray.array(
			"eventids" => $eventIds,
			"select_acknowledges" => API_OUTPUT_EXTEND,
			"preservekeys" => true
		));
	}

	for($triggers as $tnum => $trigger) {
		// if trigger is lost (broken expression) we skip it
		if (empty(Nest.value($trigger,"hosts").$())) {
			unset($triggers[$tnum]);
			continue;
		}

		$host = reset(Nest.value($trigger,"hosts").$());
		Nest.value($trigger,"hostid").$() = Nest.value($host,"hostid").$();
		Nest.value($trigger,"hostname").$() = Nest.value($host,"name").$();

		if (Nest.value($trigger,"lastEvent").$()) {
			Nest.value($trigger,"lastEvent","acknowledges").$() = isset($eventAcknowledges[$trigger["lastEvent"]["eventid"]])
				? $eventAcknowledges[$trigger["lastEvent"]["eventid"]]["acknowledges"]
				: null;
		}

		$triggers[$tnum] = $trigger;
	}

	$hostIds = zbx_objectValues($triggers, "hostid");

	// get hosts
	$hosts = API.Host().get(CArray.array(
		"hostids" => $hostIds,
		"output" => CArray.array("hostid", "name", "status", "maintenance_status", "maintenance_type", "maintenanceid"),
		"selectScreens" => API_OUTPUT_COUNT,
		"preservekeys" => true
	));

	// actions
	$actions = getEventActionsStatHints($eventIds);

	// ack params
	$ackParams = isset(Nest.value($filter,"screenid").$()) ? CArray.array("screenid" => Nest.value($filter,"screenid").$()) : CArray.array();

	$config = select_config();

	// indicator of sort field
	if ($showSortIndicator) {
		$sortDiv = new CDiv(SPACE, (Nest.value($filter,"sortorder").$() === ZBX_SORT_DOWN) ? "icon_sortdown default_cursor" : "icon_sortup default_cursor");
		$sortDiv.addStyle("float: left");
		$hostHeaderDiv = new CDiv(CArray.array(_("Host"), SPACE));
		$hostHeaderDiv.addStyle("float: left");
		$issueHeaderDiv = new CDiv(CArray.array(_("Issue"), SPACE));
		$issueHeaderDiv.addStyle("float: left");
		$lastChangeHeaderDiv = new CDiv(CArray.array(_("Time"), SPACE));
		$lastChangeHeaderDiv.addStyle("float: left");
	}

	$table = new CTableInfo(_("No events found."));
	$table.setHeader(CArray.array(
		is_show_all_nodes() ? _("Node") : null,
		($showSortIndicator && (Nest.value($filter,"sortfield").$() === "hostname")) ? CArray.array($hostHeaderDiv, $sortDiv) : _("Host"),
		($showSortIndicator && (Nest.value($filter,"sortfield").$() === "priority")) ? CArray.array($issueHeaderDiv, $sortDiv) : _("Issue"),
		($showSortIndicator && (Nest.value($filter,"sortfield").$() === "lastchange")) ? CArray.array($lastChangeHeaderDiv, $sortDiv) : _("Last change"),
		_("Age"),
		_("Info"),
		Nest.value($config,"event_ack_enable").$() ? _("Ack") : null,
		_("Actions")
	));

	$scripts = API.Script().getScriptsByHosts($hostIds);

	// triggers
	for($triggers as $trigger) {
		$host = $hosts[$trigger["hostid"]];

		$hostName = new CSpan(Nest.value($host,"name").$(), "link_menu");
		$hostName.setMenuPopup(getMenuPopupHost($host, $scripts[$host["hostid"]]));

		// add maintenance icon with hint if host is in maintenance
		$maintenanceIcon = null;

		if (Nest.value($host,"maintenance_status").$()) {
			$maintenanceIcon = new CDiv(null, "icon-maintenance-abs");

			// get maintenance
			$maintenances = API.Maintenance().get(CArray.array(
				"maintenanceids" => Nest.value($host,"maintenanceid").$(),
				"output" => API_OUTPUT_EXTEND,
				"limit" => 1
			));
			if ($maintenance = reset($maintenances)) {
				$hint = $maintenance["name"]." [".($host["maintenance_type"]
					? _("Maintenance without data collection")
					: _("Maintenance with data collection"))."]";

				if (isset(Nest.value($maintenance,"description").$())) {
					// double quotes mandatory
					$hint .= \"\n\".Nest.value($maintenance,"description").$();
				}

				$maintenanceIcon.setHint($hint);
				$maintenanceIcon.addClass("pointer");
			}

			$hostName.addClass("left-to-icon-maintenance-abs");
		}

		$hostDiv = new CDiv(CArray.array($hostName, $maintenanceIcon), "maintenance-abs-cont");

		// unknown triggers
		$unknown = SPACE;
		if (Nest.value($trigger,"state").$() == TRIGGER_STATE_UNKNOWN) {
			$unknown = new CDiv(SPACE, "status_icon iconunknown");
			$unknown.setHint(Nest.value($trigger,"error").$(), "", "on");
		}

		// trigger has events
		if (Nest.value($trigger,"lastEvent").$()) {
			// description
			$description = CMacrosResolverHelper::resolveEventDescription(zbx_array_merge($trigger, CArray.array(
				"clock" => Nest.value($trigger,"lastEvent","clock").$(),
				"ns" => $trigger["lastEvent"]["ns"]
			)));

			// ack
			$ack = getEventAckState(Nest.value($trigger,"lastEvent").$(), empty(Nest.value($filter,"backUrl").$()) ? true : Nest.value($filter,"backUrl").$(),
				true, $ackParams
			);
		}
		// trigger has no events
		else {
			// description
			$description = CMacrosResolverHelper::resolveEventDescription(zbx_array_merge($trigger, CArray.array(
				"clock" => Nest.value($trigger,"lastchange").$(),
				"ns" => "999999999"
			)));

			// ack
			$ack = new CSpan(_("No events"), "unknown");
		}

		// description
		if (!zbx_empty(Nest.value($trigger,"url").$())) {
			$description = new CLink($description, resolveTriggerUrl($trigger), null, null, true);
		}
		else {
			$description = new CSpan($description, "pointer");
		}
		$description = new CCol($description, getSeverityStyle(Nest.value($trigger,"priority").$()));
		if (Nest.value($trigger,"lastEvent").$()) {
			$description.setHint(
				make_popup_eventlist(Nest.value($trigger,"triggerid").$(), Nest.value($trigger,"lastEvent","eventid").$()),
				"", "", false
			);
		}

		// clock
		$clock = new CLink(zbx_date2str(_("d M Y H:i:s"), Nest.value($trigger,"lastchange").$()),
			"events.php?triggerid=".$trigger["triggerid"]."&source=".EVENT_SOURCE_TRIGGERS."&show_unknown=1".
				"&hostid=".$trigger["hostid"]."&stime=".date(TIMESTAMP_FORMAT, Nest.value($trigger,"lastchange").$()).
				"&period=".ZBX_PERIOD_DEFAULT
		);

		// actions
		$actionHint = (Nest.value($trigger,"lastEvent").$() && isset($actions[$trigger["lastEvent"]["eventid"]]))
			? $actions[$trigger["lastEvent"]["eventid"]]
			: SPACE;

		$table.addRow(CArray.array(
			get_node_name_by_elid(Nest.value($trigger,"triggerid").$()),
			$hostDiv,
			$description,
			$clock,
			zbx_date2age(Nest.value($trigger,"lastchange").$()),
			$unknown,
			$ack,
			$actionHint
		));
	}

	// initialize blinking
	zbx_add_post_js("jqBlink.blink();");

	$script = new CJSScript(get_js(\"jQuery("#hat_lastiss_footer").html("\"._s("Updated: %s", zbx_date2str(_("H:i:s"))).\"")\"));

	$infoDiv = new CDiv(_n("%1$d of %2$d issue is shown", "%1$d of %2$d issues are shown", count($triggers), $triggersTotalCount));
	$infoDiv.addStyle("text-align: right; padding-right: 3px;");

	return new CDiv(CArray.array($table, $infoDiv, $script));
}

/**
 * Create and return a DIV with web monitoring overview.
 *
 * @param array $filter
 * @param array $filter["groupids"]
 * @param bool  $filter["maintenance"]
 *
 * @return CDiv
 */
function make_webmon_overview($filter) {
	$groups = API.HostGroup().get(CArray.array(
		"groupids" => Nest.value($filter,"groupids").$(),
		"hostids" => isset(Nest.value($filter,"hostids").$()) ? Nest.value($filter,"hostids").$() : null,
		"monitored_hosts" => true,
		"with_monitored_httptests" => true,
		"output" => CArray.array("groupid", "name"),
		"preservekeys" => true
	));

	for($groups as &$group) {
		Nest.value($group,"nodename").$() = get_node_name_by_elid(Nest.value($group,"groupid").$());
	}
	unset($group);

	CArrayHelper::sort($groups, CArray.array(
		CArray.array("field" => "nodename", "order" => ZBX_SORT_UP),
		CArray.array("field" => "name", "order" => ZBX_SORT_UP)
	));

	$groupIds = array_keys($groups);

	$availableHosts = API.Host().get(CArray.array(
		"groupids" => $groupIds,
		"hostids" => isset(Nest.value($filter,"hostids").$()) ? Nest.value($filter,"hostids").$() : null,
		"monitored_hosts" => true,
		"filter" => CArray.array("maintenance_status" => Nest.value($filter,"maintenance").$()),
		"output" => CArray.array("hostid"),
		"preservekeys" => true
	));
	$availableHostIds = array_keys($availableHosts);

	$table = new CTableInfo(_("No web scenarios found."));
	$table.setHeader(CArray.array(
		is_show_all_nodes() ? _("Node") : null,
		_("Host group"),
		_("Ok"),
		_("Failed"),
		_("Unknown")
	));

	$data = CArray.array();

	// fetch links between HTTP tests and host groups
	$result = DbFetchArray(DBselect(
		"SELECT DISTINCT ht.httptestid,hg.groupid".
		" FROM httptest ht,hosts_groups hg".
		" WHERE ht.hostid=hg.hostid".
			" AND ".dbConditionInt("hg.hostid", $availableHostIds).
			" AND ".dbConditionInt("hg.groupid", $groupIds)
	));

	// fetch HTTP test execution data
	$httpTestData = Manager::HttpTest().getLastData(zbx_objectValues($result, "httptestid"));

	for($result as $row) {
		if (isset($httpTestData[$row["httptestid"]]) && $httpTestData[$row["httptestid"]]["lastfailedstep"] !== null) {
			if ($httpTestData[$row["httptestid"]]["lastfailedstep"] != 0) {
				$data[$row["groupid"]]["failed"] = isset($data[$row["groupid"]]["failed"])
					? ++$data[$row["groupid"]]["failed"]
					: 1;
			}
			else {
				$data[$row["groupid"]]["ok"] = isset($data[$row["groupid"]]["ok"])
					? ++$data[$row["groupid"]]["ok"]
					: 1;
			}
		}
		else {
			$data[$row["groupid"]]["unknown"] = isset($data[$row["groupid"]]["unknown"])
				? ++$data[$row["groupid"]]["unknown"]
				: 1;
		}
	}

	for($groups as $group) {
		if (!empty($data[$group["groupid"]])) {
			$table.addRow(CArray.array(
				is_show_all_nodes() ? Nest.value($group,"nodename").$() : null,
				Nest.value($group,"name").$(),
				new CSpan(empty($data[$group["groupid"]]["ok"]) ? 0 : $data[$group["groupid"]]["ok"], "off"),
				new CSpan(
					empty($data[$group["groupid"]]["failed"]) ? 0 : $data[$group["groupid"]]["failed"],
					empty($data[$group["groupid"]]["failed"]) ? "off" : "on"
				),
				new CSpan(empty($data[$group["groupid"]]["unknown"]) ? 0 : $data[$group["groupid"]]["unknown"], "unknown")
			));
		}
	}

	$script = new CJSScript(get_js(\"jQuery("#hat_webovr_footer").html("\"._s("Updated: %s", zbx_date2str(_("H:i:s"))).\"")\"));

	return new CDiv(CArray.array($table, $script));
}

function make_discovery_status() {
	$options = CArray.array(
		"filter" => CArray.array("status" => DHOST_STATUS_ACTIVE),
		"selectDHosts" => CArray.array("druleid", "dhostid", "status"),
		"output" => API_OUTPUT_EXTEND
	);
	$drules = API.DRule().get($options);

	for($drules as &$drule) {
		Nest.value($drule,"nodename").$() = get_node_name_by_elid(Nest.value($drule,"druleid").$());
	}
	unset($drule);

	// we need natural sort
	$sortFields = CArray.array(
		CArray.array("field" => "nodename", "order" => ZBX_SORT_UP),
		CArray.array("field" => "name", "order" => ZBX_SORT_UP)
	);
	CArrayHelper::sort($drules, $sortFields);


	for($drules as $drnum => $drule) {
		$drules[$drnum]["up"] = 0;
		$drules[$drnum]["down"] = 0;

		for(Nest.value($drule,"dhosts").$() as $dhost){
			if (DRULE_STATUS_DISABLED == Nest.value($dhost,"status").$()) {
				$drules[$drnum]["down"]++;
			}
			else {
				$drules[$drnum]["up"]++;
			}
		}
	}

	$header = CArray.array(
		is_show_all_nodes() ? new CCol(_("Node"), "center") : null,
		new CCol(_("Discovery rule"), "center"),
		new CCol(_x("Up", "discovery results in dashboard")),
		new CCol(_x("Down", "discovery results in dashboard"))
	);

	$table  = new CTableInfo();
	$table.setHeader($header,"header");

	for($drules as $drule) {
		$table.addRow(CArray.array(
			Nest.value($drule,"nodename").$(),
			new CLink($drule["nodename"].(Nest.value($drule,"nodename").$() ? NAME_DELIMITER : "").Nest.value($drule,"name").$(), "discovery.php?druleid=".Nest.value($drule,"druleid").$()),
			new CSpan(Nest.value($drule,"up").$(), "green"),
			new CSpan(Nest.value($drule,"down").$(), (Nest.value($drule,"down").$() > 0) ? "red" : "green")
		));
	}
	$script = new CJSScript(get_js(\"jQuery("#hat_dscvry_footer").html("\"._s("Updated: %s", zbx_date2str(_("H:i:s"))).\"")\"));
	return new CDiv(CArray.array($table, $script));
}

function make_graph_menu(&$menu, &$submenu) {
	$menu["menu_graphs"][] = CArray.array(
		_("Favourite graphs"),
		null,
		null,
		CArray.array("outer" => CArray.array("pum_oheader"), "inner" => CArray.array("pum_iheader"))
	);

	$menu["menu_graphs"][] = CArray.array(
		_("Add")." "._("Graph"),
		"javascript: PopUp(\"popup.php?srctbl=graphs&srcfld1=graphid&reference=graphid&multiselect=1&real_hosts=1\",800,450); void(0);",
		null,
		CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu"))
	);
	$menu["menu_graphs"][] = CArray.array(
		_("Add")." "._("Simple graph"),
		"javascript: PopUp(\"popup.php?srctbl=items&srcfld1=itemid&reference=itemid&real_hosts=1'.
			"&multiselect=1&numeric=1&templated=0&with_simple_graph_items=1\",800,450); void(0);',
		null,
		CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu"))
	);
	$menu["menu_graphs"][] = CArray.array(
		_("Remove"),
		null,
		null,
		CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu"))
	);
	Nest.value($submenu,"menu_graphs").$() = make_graph_submenu();
}

function make_graph_submenu() {
	$graphids = CArray.array();
	$itemids = CArray.array();
	$favGraphs = CArray.array();
	$fav_graphs = CFavorite::get("web.favorite.graphids");

	if (!$fav_graphs) {
		return $favGraphs;
	}

	for($fav_graphs as $favorite) {
		if ("itemid" == Nest.value($favorite,"source").$()) {
			$itemids[$favorite["value"]] = Nest.value($favorite,"value").$();
		}
		else {
			$graphids[$favorite["value"]] = Nest.value($favorite,"value").$();
		}
	}

	if ($graphids) {
		$options = CArray.array(
			"graphids" => $graphids,
			"selectHosts" => CArray.array("hostid", "host"),
			"output" => CArray.array("graphid", "name"),
			"expandName" => true
		);
		$graphs = API.Graph().get($options);
		$graphs = zbx_toHash($graphs, "graphid");
	}

	if ($itemids) {
		$items = API.Item().get(CArray.array(
			"output" => CArray.array("itemid", "hostid", "name", "key_"),
			"selectHosts" => CArray.array("hostid", "host"),
			"itemids" => $itemids,
			"webitems" => true,
			"preservekeys" => true
		));

		$items = CMacrosResolverHelper::resolveItemNames($items);
	}

	for($fav_graphs as $favorite) {
		$source = Nest.value($favorite,"source").$();
		$sourceid = Nest.value($favorite,"value").$();

		if ($source == "itemid") {
			if (!isset($items[$sourceid])) {
				continue;
			}

			$item_added = true;
			$item = $items[$sourceid];
			$host = reset(Nest.value($item,"hosts").$());

			$favGraphs[] = CArray.array(
				"name" => $host["host"].NAME_DELIMITER.Nest.value($item,"name_expanded").$(),
				"favobj" => "itemid",
				"favid" => $sourceid,
				"favaction" => "remove"
			);
		}
		else {
			if (!isset($graphs[$sourceid])) {
				continue;
			}

			$graph_added = true;
			$graph = $graphs[$sourceid];
			$ghost = reset(Nest.value($graph,"hosts").$());
			$favGraphs[] = CArray.array(
				"name" => $ghost["host"].NAME_DELIMITER.Nest.value($graph,"name").$(),
				"favobj" => "graphid",
				"favid" => $sourceid,
				"favaction" => "remove"
			);
		}
	}

	if (isset($graph_added)) {
		$favGraphs[] = CArray.array(
			"name" => _("Remove")." "._("All")." "._("Graphs"),
			"favobj" => "graphid",
			"favid" => 0,
			"favaction" => "remove"
		);
	}

	if (isset($item_added)) {
		$favGraphs[] = CArray.array(
			"name" => _("Remove")." "._("All")." "._("Simple graphs"),
			"favobj" => "itemid",
			"favid" => 0,
			"favaction" => "remove"
		);
	}

	return $favGraphs;
}

function make_sysmap_menu(&$menu, &$submenu) {
	$menu["menu_sysmaps"][] = CArray.array(_("Favourite maps"), null, null, CArray.array("outer" => CArray.array("pum_oheader"), "inner" => CArray.array("pum_iheader")));
	$menu["menu_sysmaps"][] = CArray.array(
		_("Add")." "._("Map"),
		"javascript: PopUp(\"popup.php?srctbl=sysmaps&srcfld1=sysmapid&reference=sysmapid&multiselect=1\",800,450); void(0);",
		null,
		CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu")
	));
	$menu["menu_sysmaps"][] = CArray.array(_("Remove"), null, null, CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu")));
	Nest.value($submenu,"menu_sysmaps").$() = make_sysmap_submenu();
}

function make_sysmap_submenu() {
	$fav_sysmaps = CFavorite::get("web.favorite.sysmapids");
	$favMaps = CArray.array();
	$sysmapids = CArray.array();
	for($fav_sysmaps as $favorite) {
		$sysmapids[$favorite["value"]] = Nest.value($favorite,"value").$();
	}

	$options = CArray.array(
		"sysmapids" => $sysmapids,
		"output" => CArray.array("sysmapid", "name")
	);
	$sysmaps = API.Map().get($options);
	for($sysmaps as $sysmap) {
		$favMaps[] = CArray.array(
			"name" => Nest.value($sysmap,"name").$(),
			"favobj" => "sysmapid",
			"favid" => Nest.value($sysmap,"sysmapid").$(),
			"favaction" => "remove"
		);
	}

	if (!empty($favMaps)) {
		$favMaps[] = CArray.array(
			"name" => _("Remove")." "._("All")." "._("Maps"),
			"favobj" => "sysmapid",
			"favid" => 0,
			"favaction" => "remove"
		);
	}
	return $favMaps;
}

function make_screen_menu(&$menu, &$submenu) {
	$menu["menu_screens"][] = CArray.array(_("Favourite screens"), null, null, CArray.array("outer" => CArray.array("pum_oheader"), "inner" => CArray.array("pum_iheader")));
	$menu["menu_screens"][] = CArray.array(
		_("Add")." "._("Screen"),
		"javascript: PopUp(\"popup.php?srctbl=screens&srcfld1=screenid&reference=screenid&multiselect=1\", 800, 450); void(0);",
		null,
		CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu")
	));
	$menu["menu_screens"][] = CArray.array(
		_("Add")." "._("Slide show"),
		"javascript: PopUp(\"popup.php?srctbl=slides&srcfld1=slideshowid&reference=slideshowid&multiselect=1\", 800, 450); void(0);",
		null,
		CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu")
	));
	$menu["menu_screens"][] = CArray.array(_("Remove"), null, null, CArray.array("outer" => "pum_o_submenu", "inner" => CArray.array("pum_i_submenu")));
	Nest.value($submenu,"menu_screens").$() = make_screen_submenu();
}

function make_screen_submenu() {
	$favScreens = CArray.array();
	$fav_screens = CFavorite::get("web.favorite.screenids");

	if (!$fav_screens) {
		return $favScreens;
	}

	$screenids = CArray.array();
	for($fav_screens as $favorite) {
		if ("screenid" == Nest.value($favorite,"source").$()) {
			$screenids[$favorite["value"]] = Nest.value($favorite,"value").$();
		}
	}

	$options = CArray.array(
		"screenids" => $screenids,
		"output" => CArray.array("screenid", "name")
	);
	$screens = API.Screen().get($options);
	$screens = zbx_toHash($screens, "screenid");

	for($fav_screens as $favorite) {
		$source = Nest.value($favorite,"source").$();
		$sourceid = Nest.value($favorite,"value").$();
		if ("slideshowid" == $source) {
			if (!slideshow_accessible($sourceid, PERM_READ)) {
				continue;
			}
			if (!$slide = get_slideshow_by_slideshowid($sourceid)) {
				continue;
			}
			$slide_added = true;
			$favScreens[] = CArray.array(
				"name" => Nest.value($slide,"name").$(),
				"favobj" => "slideshowid",
				"favid" => Nest.value($slide,"slideshowid").$(),
				"favaction" => "remove"
			);
		}
		else {
			if (!isset($screens[$sourceid])) {
				continue;
			}
			$screen = $screens[$sourceid];
			$screen_added = true;
			$favScreens[] = CArray.array(
				"name" => Nest.value($screen,"name").$(),
				"favobj" => "screenid",
				"favid" => Nest.value($screen,"screenid").$(),
				"favaction" => "remove"
			);
		}
	}

	if (isset($screen_added)) {
		$favScreens[] = CArray.array(
			"name" => _("Remove")." "._("All")." "._("Screens"),
			"favobj" => "screenid",
			"favid" => 0,
			"favaction" => "remove"
		);
	}

	if (isset($slide_added)) {
		$favScreens[] = CArray.array(
			"name" => _("Remove")." "._("All")." "._("Slides"),
			"favobj" => "slideshowid",
			"favid" => 0,
			"favaction" => "remove"
		);
	}
	return $favScreens;
}

/**
 * Generate table for dashboard triggers popup.
 *
 * @see make_system_status
 *
 * @param array $triggers
 * @param array $ackParams
 * @param array $actions
 * @param array $config
 *
 * @return CTableInfo
 */
function makeTriggersPopup(array $triggers, array $ackParams, array $actions, array $config) {
	$popupTable = new CTableInfo();
	$popupTable.setAttribute("style", "width: 400px;");
	$popupTable.setHeader(CArray.array(
		_("Host"),
		_("Issue"),
		_("Age"),
		_("Info"),
		Nest.value($config,"event_ack_enable").$() ? _("Ack") : null,
		_("Actions")
	));

	CArrayHelper::sort($triggers, CArray.array(CArray.array("field" => "lastchange", "order" => ZBX_SORT_DOWN)));

	for($triggers as $trigger) {
		// unknown triggers
		$unknown = SPACE;
		if (Nest.value($trigger,"state").$() == TRIGGER_STATE_UNKNOWN) {
			$unknown = new CDiv(SPACE, "status_icon iconunknown");
			$unknown.setHint(Nest.value($trigger,"error").$(), "", "on");
		}

		// ack
		if (Nest.value($config,"event_ack_enable").$()) {
			$ack = isset(Nest.value($trigger,"event","eventid").$())
				? getEventAckState(Nest.value($trigger,"event").$(), true, true, $ackParams)
				: _("No events");
		}
		else {
			$ack = null;
		}

		// action
		$action = (isset(Nest.value($trigger,"event","eventid").$()) && isset($actions[$trigger["event"]["eventid"]]))
			? $actions[$trigger["event"]["eventid"]]
			: _("-");

		$popupTable.addRow(CArray.array(
			Nest.array($trigger,"hosts").get(0)["name"],
			getSeverityCell(Nest.value($trigger,"priority").$(), Nest.value($trigger,"description").$()),
			zbx_date2age(Nest.value($trigger,"lastchange").$()),
			$unknown,
			$ack,
			$action
		));
	}

	return $popupTable;
}
