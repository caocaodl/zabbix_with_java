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


require_once dirname(__FILE__)."/include/config.inc.php";

$requestType = get_request("type", PAGE_TYPE_JSON);
if ($requestType == PAGE_TYPE_JSON) {
	$http_request = new CHTTP_request();
	$json = new CJSON();
	$data = $json.decode($http_request.body(), true);
}
else {
	$data = _REQUEST;
}

Nest.value($page,"title").$() = "RPC";
Nest.value($page,"file").$() = "jsrpc.php";
Nest.value($page,"hist_arg").$() = CArray.array();
Nest.value($page,"type").$() = detect_page_type($requestType);

require_once dirname(__FILE__)."/include/page_header.php";

if (!is_array($data) || !isset(Nest.value($data,"method").$())
		|| ($requestType == PAGE_TYPE_JSON && (!isset(Nest.value($data,"params").$()) || !is_array(Nest.value($data,"params").$())))) {
	fatal_error("Wrong RPC call to JS RPC!");
}

$result = CArray.array();
switch (Nest.value($data,"method").$()) {
	case "host.get":
		$result = API.Host().get(CArray.array(
			"startSearch" => true,
			"search" => Nest.value($data,"params","search").$(),
			"output" => CArray.array("hostid", "host", "name"),
			"sortfield" => "name",
			"limit" => 15
		));
		break;

	case "message.mute":
		$msgsettings = getMessageSettings();
		$msgsettings["sounds.mute"] = 1;
		updateMessageSettings($msgsettings);
		break;

	case "message.unmute":
		$msgsettings = getMessageSettings();
		$msgsettings["sounds.mute"] = 0;
		updateMessageSettings($msgsettings);
		break;

	case "message.settings":
		$result = getMessageSettings();
		break;

	case "message.get":
		$msgsettings = getMessageSettings();

		// if no severity is selected, show nothing
		if (empty($msgsettings["triggers.severities"])) {
			break;
		}

		// timeout
		$timeout = time() - Nest.value($msgsettings,"timeout").$();
		$lastMsgTime = 0;
		if (isset(Nest.value($data,"params","messageLast","events").$())) {
			$lastMsgTime = Nest.value($data,"params","messageLast","events","time").$();
		}

		$options = CArray.array(
			"nodeids" => get_current_nodeid(true),
			"lastChangeSince" => max(CArray.array($lastMsgTime, $msgsettings["last.clock"], $timeout)),
			"value" => CArray.array(TRIGGER_VALUE_TRUE, TRIGGER_VALUE_FALSE),
			"priority" => array_keys($msgsettings["triggers.severities"]),
			"triggerLimit" => 15
		);
		if (!$msgsettings["triggers.recovery"]) {
			Nest.value($options,"value").$() = CArray.array(TRIGGER_VALUE_TRUE);
		}
		$events = getLastEvents($options);

		$sortClock = CArray.array();
		$sortEvent = CArray.array();

		$usedTriggers = CArray.array();
		for($events as $number => $event) {
			if (count($usedTriggers) < 15) {
				if (!isset($usedTriggers[$event["objectid"]])) {
					$trigger = Nest.value($event,"trigger").$();
					$host = Nest.value($event,"host").$();

					if (Nest.value($event,"value").$() == TRIGGER_VALUE_FALSE) {
						$priority = 0;
						$title = _("Resolved");
						$sound = $msgsettings["sounds.recovery"];
					}
					else {
						$priority = Nest.value($trigger,"priority").$();
						$title = _("Problem on");
						$sound = $msgsettings["sounds.".$trigger["priority"]];
					}

					$url_tr_status = "tr_status.php?hostid=".Nest.value($host,"hostid").$();
					$url_events = "events.php?triggerid=".$event["objectid"]."&source=".EVENT_SOURCE_TRIGGERS;
					$url_tr_events = "tr_events.php?eventid=".$event["eventid"]."&triggerid=".Nest.value($event,"objectid").$();

					$result[$number] = CArray.array(
						"type" => 3,
						"caption" => "events",
						"sourceid" => Nest.value($event,"eventid").$(),
						"time" => Nest.value($event,"clock").$(),
						"priority" => $priority,
						"sound" => $sound,
						"color" => getSeverityColor(Nest.value($trigger,"priority").$(), Nest.value($event,"value").$()),
						"title" => $title." ".get_node_name_by_elid(Nest.value($host,"hostid").$(), null, NAME_DELIMITER)."[url=".$url_tr_status."]".$host["name"]."[/url]",
						"body" => CArray.array(
							_("Details").": [url=".$url_events."]".$trigger["description"]."[/url]",
							_("Date").": [b][url=".$url_tr_events."]".zbx_date2str(_("d M Y H:i:s"), Nest.value($event,"clock").$())."[/url][/b]",
						),
						"timeout" => $msgsettings["timeout"]
					);

					$sortClock[$number] = Nest.value($event,"clock").$();
					$sortEvent[$number] = Nest.value($event,"eventid").$();
					$usedTriggers[$event["objectid"]] = true;
				}
			}
			else {
				break;
			}
		}
		array_multisort($sortClock, SORT_ASC, $sortEvent, SORT_ASC, $result);
		break;

	case "message.closeAll":
		$msgsettings = getMessageSettings();
		switch (strtolower(Nest.value($data,"params","caption").$())) {
			case "events":
				$msgsettings["last.clock"] = (int) Nest.value($data,"params","time").$() + 1;
				updateMessageSettings($msgsettings);
				break;
		}
		break;

	case "zabbix.status":
		$session = Z::getInstance().getSession();
		if (!isset(Nest.value($session,"serverCheckResult").$()) || (Nest.value($session,"serverCheckTime").$() + SERVER_CHECK_INTERVAL) <= time()) {
			$zabbixServer = new CZabbixServer($ZBX_SERVER, $ZBX_SERVER_PORT, ZBX_SOCKET_TIMEOUT, 0);
			Nest.value($session,"serverCheckResult").$() = $zabbixServer.isRunning();
			Nest.value($session,"serverCheckTime").$() = time();
		}

		$result = CArray.array(
			"result" => (bool) Nest.value($session,"serverCheckResult").$(),
			"message" => Nest.value($session,"serverCheckResult").$() ? "" : _("Zabbix server is not running: the information displayed may not be current.")
		);
		break;

	case "screen.get":
		$options = CArray.array(
			"pageFile" => !empty(Nest.value($data,"pageFile").$()) ? Nest.value($data,"pageFile").$() : null,
			"mode" => !empty(Nest.value($data,"mode").$()) ? Nest.value($data,"mode").$() : null,
			"timestamp" => !empty(Nest.value($data,"timestamp").$()) ? Nest.value($data,"timestamp").$() : time(),
			"resourcetype" => !empty(Nest.value($data,"resourcetype").$()) ? Nest.value($data,"resourcetype").$() : null,
			"screenitemid" => !empty(Nest.value($data,"screenitemid").$()) ? Nest.value($data,"screenitemid").$() : null,
			"groupid" => !empty(Nest.value($data,"groupid").$()) ? Nest.value($data,"groupid").$() : null,
			"hostid" => !empty(Nest.value($data,"hostid").$()) ? Nest.value($data,"hostid").$() : null,
			"period" => !empty(Nest.value($data,"period").$()) ? Nest.value($data,"period").$() : null,
			"stime" => !empty(Nest.value($data,"stime").$()) ? Nest.value($data,"stime").$() : null,
			"profileIdx" => !empty(Nest.value($data,"profileIdx").$()) ? Nest.value($data,"profileIdx").$() : null,
			"profileIdx2" => !empty(Nest.value($data,"profileIdx2").$()) ? Nest.value($data,"profileIdx2").$() : null,
			"updateProfile" => isset(Nest.value($data,"updateProfile").$()) ? Nest.value($data,"updateProfile").$() : null
		);
		if (Nest.value($options,"resourcetype").$() == SCREEN_RESOURCE_HISTORY) {
			Nest.value($options,"itemids").$() = !empty(Nest.value($data,"itemids").$()) ? Nest.value($data,"itemids").$() : null;
			Nest.value($options,"action").$() = !empty(Nest.value($data,"action").$()) ? Nest.value($data,"action").$() : null;
			Nest.value($options,"filter").$() = !empty(Nest.value($data,"filter").$()) ? Nest.value($data,"filter").$() : null;
			Nest.value($options,"filter_task").$() = !empty(Nest.value($data,"filter_task").$()) ? Nest.value($data,"filter_task").$() : null;
			Nest.value($options,"mark_color").$() = !empty(Nest.value($data,"mark_color").$()) ? Nest.value($data,"mark_color").$() : null;
		}
		elseif (Nest.value($options,"resourcetype").$() == SCREEN_RESOURCE_CHART) {
			Nest.value($options,"graphid").$() = !empty(Nest.value($data,"graphid").$()) ? Nest.value($data,"graphid").$() : null;
			Nest.value($options,"profileIdx2").$() = Nest.value($options,"graphid").$();
		}

		$screenBase = CScreenBuilder::getScreen($options);
		if (!empty($screenBase)) {
			$screen = $screenBase.get();
		}

		if (!empty($screen)) {
			if (Nest.value($options,"mode").$() == SCREEN_MODE_JS) {
				$result = $screen;
			}
			else {
				if (is_object($screen)) {
					$result = $screen.toString();
				}
			}
		}
		else {
			$result = "";
		}
		break;

	/**
	 * Create multi select data.
	 * Supported objects: \"applications\", \"hosts\", \"hostGroup\", \"templates\", \"triggers\"
	 *
	 * @param string $data["objectName"]
	 * @param string $data["search"]
	 * @param int    $data["limit"]
	 *
	 * @return CArray.array(int => CArray.array("value" => int, "text" => string))
	 */
	case "multiselect.get":
		$config = select_config();
		$displayNodes = is_array(get_current_nodeid());
		$sortFields = $displayNodes ? CArray.array(CArray.array("field" => "nodename", "order" => ZBX_SORT_UP)) : CArray.array();

		switch (Nest.value($data,"objectName").$()) {
			case "hostGroup":
				$hostGroups = API.HostGroup().get(CArray.array(
					"editable" => isset(Nest.value($data,"editable").$()) ? Nest.value($data,"editable").$() : null,
					"output" => CArray.array("groupid", "name"),
					"search" => isset(Nest.value($data,"search").$()) ? CArray.array("name" => Nest.value($data,"search").$()) : null,
					"filter" => isset(Nest.value($data,"filter").$()) ? Nest.value($data,"filter").$() : null,
					"limit" => isset(Nest.value($data,"limit").$()) ? Nest.value($data,"limit").$() : null
				));

				if ($hostGroups) {
					if ($displayNodes) {
						for($hostGroups as &$hostGroup) {
							Nest.value($hostGroup,"nodename").$() = get_node_name_by_elid(Nest.value($hostGroup,"groupid").$(), true, NAME_DELIMITER);
						}
						unset($hostGroup);
					}

					$sortFields[] = CArray.array("field" => "name", "order" => ZBX_SORT_UP);
					CArrayHelper::sort($hostGroups, $sortFields);

					if (isset(Nest.value($data,"limit").$())) {
						$hostGroups = array_slice($hostGroups, 0, Nest.value($data,"limit").$());
					}

					for($hostGroups as $hostGroup) {
						$result[] = CArray.array(
							"id" => Nest.value($hostGroup,"groupid").$(),
							"prefix" => $displayNodes ? Nest.value($hostGroup,"nodename").$() : "",
							"name" => $hostGroup["name"]
						);
					}
				}
				break;

			case "hosts":
				$hosts = API.Host().get(CArray.array(
					"editable" => isset(Nest.value($data,"editable").$()) ? Nest.value($data,"editable").$() : null,
					"output" => CArray.array("hostid", "name"),
					"templated_hosts" => isset(Nest.value($data,"templated_hosts").$()) ? Nest.value($data,"templated_hosts").$() : null,
					"search" => isset(Nest.value($data,"search").$()) ? CArray.array("name" => Nest.value($data,"search").$()) : null,
					"limit" => $config["search_limit"]
				));

				if ($hosts) {
					if ($displayNodes) {
						for($hosts as &$host) {
							Nest.value($host,"nodename").$() = get_node_name_by_elid(Nest.value($host,"hostid").$(), true, NAME_DELIMITER);
						}
						unset($host);
					}

					$sortFields[] = CArray.array("field" => "name", "order" => ZBX_SORT_UP);
					CArrayHelper::sort($hosts, $sortFields);

					if (isset(Nest.value($data,"limit").$())) {
						$hosts = array_slice($hosts, 0, Nest.value($data,"limit").$());
					}

					for($hosts as $host) {
						$result[] = CArray.array(
							"id" => Nest.value($host,"hostid").$(),
							"prefix" => $displayNodes ? Nest.value($host,"nodename").$() : "",
							"name" => $host["name"]
						);
					}
				}
				break;

			case "templates":
				$templates = API.Template().get(CArray.array(
					"editable" => isset(Nest.value($data,"editable").$()) ? Nest.value($data,"editable").$() : null,
					"output" => CArray.array("templateid", "name"),
					"search" => isset(Nest.value($data,"search").$()) ? CArray.array("name" => Nest.value($data,"search").$()) : null,
					"limit" => $config["search_limit"]
				));

				if ($templates) {
					if ($displayNodes) {
						for($templates as &$template) {
							Nest.value($template,"nodename").$() = get_node_name_by_elid(Nest.value($template,"templateid").$(), true, NAME_DELIMITER);
						}
						unset($template);
					}

					$sortFields[] = CArray.array("field" => "name", "order" => ZBX_SORT_UP);
					CArrayHelper::sort($templates, $sortFields);

					if (isset(Nest.value($data,"limit").$())) {
						$templates = array_slice($templates, 0, Nest.value($data,"limit").$());
					}

					for($templates as $template) {
						$result[] = CArray.array(
							"id" => Nest.value($template,"templateid").$(),
							"prefix" => $displayNodes ? Nest.value($template,"nodename").$() : "",
							"name" => $template["name"]
						);
					}
				}
				break;

			case "applications":
				$applications = API.Application().get(CArray.array(
					"hostids" => zbx_toArray(Nest.value($data,"hostid").$()),
					"output" => CArray.array("applicationid", "name"),
					"search" => isset(Nest.value($data,"search").$()) ? CArray.array("name" => Nest.value($data,"search").$()) : null,
					"limit" => $config["search_limit"]
				));

				if ($applications) {
					if ($displayNodes) {
						for($applications as &$application) {
							Nest.value($application,"nodename").$() = get_node_name_by_elid(Nest.value($application,"applicationid").$(), true, NAME_DELIMITER);
						}
						unset($application);
					}

					$sortFields[] = CArray.array("field" => "name", "order" => ZBX_SORT_UP);
					CArrayHelper::sort($applications, $sortFields);

					if (isset(Nest.value($data,"limit").$())) {
						$applications = array_slice($applications, 0, Nest.value($data,"limit").$());
					}

					for($applications as $application) {
						$result[] = CArray.array(
							"id" => Nest.value($application,"applicationid").$(),
							"prefix" => $displayNodes ? Nest.value($application,"nodename").$() : "",
							"name" => $application["name"]
						);
					}
				}
				break;

			case "triggers":
				$triggers = API.Trigger().get(CArray.array(
					"editable" => isset(Nest.value($data,"editable").$()) ? Nest.value($data,"editable").$() : null,
					"output" => CArray.array("triggerid", "description"),
					"selectHosts" => CArray.array("name"),
					"search" => isset(Nest.value($data,"search").$()) ? CArray.array("description" => Nest.value($data,"search").$()) : null,
					"limit" => $config["search_limit"]
				));

				if ($triggers) {
					if ($displayNodes) {
						for($triggers as &$trigger) {
							Nest.value($trigger,"nodename").$() = get_node_name_by_elid(Nest.value($trigger,"triggerid").$(), true, NAME_DELIMITER);
						}
						unset($trigger);
					}

					$sortFields[] = CArray.array("field" => "description", "order" => ZBX_SORT_UP);
					CArrayHelper::sort($triggers, $sortFields);

					if (isset(Nest.value($data,"limit").$())) {
						$triggers = array_slice($triggers, 0, Nest.value($data,"limit").$());
					}

					for($triggers as $trigger) {
						$hostName = "";

						if (Nest.value($trigger,"hosts").$()) {
							Nest.value($trigger,"hosts").$() = reset(Nest.value($trigger,"hosts").$());

							$hostName = $trigger["hosts"]["name"].NAME_DELIMITER;
						}

						$result[] = CArray.array(
							"id" => Nest.value($trigger,"triggerid").$(),
							"prefix" => ($displayNodes ? Nest.value($trigger,"nodename").$() : "").$hostName,
							"name" => $trigger["description"]
						);
					}
				}
				break;
		}
		break;

	default:
		fatal_error("Wrong RPC call to JS RPC!");
}

if ($requestType == PAGE_TYPE_JSON) {
	if (isset(Nest.value($data,"id").$())) {
		echo $json.encode(CArray.array(
			"jsonrpc" => "2.0",
			"result" => $result,
			"id" => $data["id"]
		));
	}
}
elseif ($requestType == PAGE_TYPE_TEXT_RETURN_JSON) {
	$json = new CJSON();

	echo $json.encode(CArray.array(
		"jsonrpc" => "2.0",
		"result" => $result
	));
}
elseif ($requestType == PAGE_TYPE_TEXT || $requestType == PAGE_TYPE_JS) {
	echo $result;
}

require_once dirname(__FILE__)."/include/page_footer.php";
