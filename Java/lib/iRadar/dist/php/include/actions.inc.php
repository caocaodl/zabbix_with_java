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


function condition_operator2str($operator) {
	switch ($operator) {
		case CONDITION_OPERATOR_EQUAL:
			return "=";
		case CONDITION_OPERATOR_NOT_EQUAL:
			return "<>";
		case CONDITION_OPERATOR_LIKE:
			return _("like");
		case CONDITION_OPERATOR_NOT_LIKE:
			return _("not like");
		case CONDITION_OPERATOR_IN:
			return _("in");
		case CONDITION_OPERATOR_MORE_EQUAL:
			return ">=";
		case CONDITION_OPERATOR_LESS_EQUAL:
			return "<=";
		case CONDITION_OPERATOR_NOT_IN:
			return _("not in");
		default:
			return _("Unknown");
	}
}

function condition_type2str($conditionType) {
	switch ($conditionType) {
		case CONDITION_TYPE_TRIGGER_VALUE:
			return _("Trigger value");
		case CONDITION_TYPE_MAINTENANCE:
			return _("Maintenance status");
		case CONDITION_TYPE_TRIGGER_NAME:
			return _("Trigger name");
		case CONDITION_TYPE_TRIGGER_SEVERITY:
			return _("Trigger severity");
		case CONDITION_TYPE_TRIGGER:
			return _("Trigger");
		case CONDITION_TYPE_HOST_NAME:
			return _("Host name");
		case CONDITION_TYPE_HOST_GROUP:
			return _("Host group");
		case CONDITION_TYPE_TEMPLATE:
			return _("Template");
		case CONDITION_TYPE_HOST:
			return _("Host");
		case CONDITION_TYPE_TIME_PERIOD:
			return _("Time period");
		case CONDITION_TYPE_NODE:
			return _("Node");
		case CONDITION_TYPE_DRULE:
			return _("Discovery rule");
		case CONDITION_TYPE_DCHECK:
			return _("Discovery check");
		case CONDITION_TYPE_DOBJECT:
			return _("Discovery object");
		case CONDITION_TYPE_DHOST_IP:
			return _("Host IP");
		case CONDITION_TYPE_DSERVICE_TYPE:
			return _("Service type");
		case CONDITION_TYPE_DSERVICE_PORT:
			return _("Service port");
		case CONDITION_TYPE_DSTATUS:
			return _("Discovery status");
		case CONDITION_TYPE_DUPTIME:
			return _("Uptime/Downtime");
		case CONDITION_TYPE_DVALUE:
			return _("Received value");
		case CONDITION_TYPE_EVENT_ACKNOWLEDGED:
			return _("Event acknowledged");
		case CONDITION_TYPE_APPLICATION:
			return _("Application");
		case CONDITION_TYPE_PROXY:
			return _("Proxy");
		case CONDITION_TYPE_EVENT_TYPE:
			return _("Event type");
		case CONDITION_TYPE_HOST_METADATA:
			return _("Host metadata");
		default:
			return _("Unknown");
	}
}

function discovery_object2str($object = null) {
	$discoveryObjects = CArray.array(
		EVENT_OBJECT_DHOST => _("Device"),
		EVENT_OBJECT_DSERVICE => _("Service")
	);

	if ($object === null) {
		return $discoveryObjects;
	}
	elseif (isset($discoveryObjects[$object])) {
		return $discoveryObjects[$object];
	}
	else {
		return _("Unknown");
	}
}

function condition_value2str($conditiontype, $value) {
	switch ($conditiontype) {
		case CONDITION_TYPE_HOST_GROUP:
			$groups = API.HostGroup().get(CArray.array(
				"groupids" => $value,
				"output" => CArray.array("name"),
				"nodeids" => get_current_nodeid(true),
				"limit" => 1
			));

			if ($groups) {
				$group = reset($groups);

				$str_val = "";
				if (id2nodeid($value) != get_current_nodeid()) {
					$str_val = get_node_name_by_elid($value, true, NAME_DELIMITER);
				}
				$str_val .= Nest.value($group,"name").$();
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_TRIGGER:
			$trigs = API.Trigger().get(CArray.array(
				"triggerids" => $value,
				"expandDescription" => true,
				"output" => CArray.array("description"),
				"selectHosts" => CArray.array("name"),
				"nodeids" => get_current_nodeid(true),
				"limit" => 1
			));

			if ($trigs) {
				$trig = reset($trigs);
				$host = reset(Nest.value($trig,"hosts").$());

				$str_val = "";
				if (id2nodeid($value) != get_current_nodeid()) {
					$str_val = get_node_name_by_elid($value, true, NAME_DELIMITER);
				}
				$str_val .= $host["name"].NAME_DELIMITER.Nest.value($trig,"description").$();
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_HOST:
		case CONDITION_TYPE_TEMPLATE:
			if ($host = get_host_by_hostid($value)) {
				$str_val = "";
				if (id2nodeid($value) != get_current_nodeid()) {
					$str_val = get_node_name_by_elid($value, true, NAME_DELIMITER);
				}
				$str_val .= Nest.value($host,"name").$();
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_TRIGGER_NAME:
		case CONDITION_TYPE_HOST_METADATA:
		case CONDITION_TYPE_HOST_NAME:
			$str_val = $value;
			break;
		case CONDITION_TYPE_TRIGGER_VALUE:
			$str_val = trigger_value2str($value);
			break;
		case CONDITION_TYPE_TRIGGER_SEVERITY:
			$str_val = getSeverityCaption($value);
			break;
		case CONDITION_TYPE_TIME_PERIOD:
			$str_val = $value;
			break;
		case CONDITION_TYPE_MAINTENANCE:
			$str_val = _("maintenance");
			break;
		case CONDITION_TYPE_NODE:
			if ($node = get_node_by_nodeid($value)) {
				$str_val = Nest.value($node,"name").$();
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_DRULE:
			if ($drule = get_discovery_rule_by_druleid($value)) {
				$str_val = Nest.value($drule,"name").$();
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_DCHECK:
			$row = DBfetch(DBselect(
					"SELECT dr.name,c.dcheckid,c.type,c.key_,c.ports".
					" FROM drules dr,dchecks c".
					" WHERE dr.druleid=c.druleid".
						" AND c.dcheckid=".zbx_dbstr($value)
			));
			if ($row) {
				$str_val = $row["name"].NAME_DELIMITER.discovery_check2str(Nest.value($row,"type").$(), Nest.value($row,"key_").$(), Nest.value($row,"ports").$());
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_DOBJECT:
			$str_val = discovery_object2str($value);
			break;
		case CONDITION_TYPE_PROXY:
			if ($host = get_host_by_hostid($value)) {
				$str_val = Nest.value($host,"host").$();
			}
			else {
				return _("Unknown");
			}
			break;
		case CONDITION_TYPE_DHOST_IP:
			$str_val = $value;
			break;
		case CONDITION_TYPE_DSERVICE_TYPE:
			$str_val = discovery_check_type2str($value);
			break;
		case CONDITION_TYPE_DSERVICE_PORT:
			$str_val = $value;
			break;
		case CONDITION_TYPE_DSTATUS:
			$str_val = discovery_object_status2str($value);
			break;
		case CONDITION_TYPE_DUPTIME:
			$str_val = $value;
			break;
		case CONDITION_TYPE_DVALUE:
			$str_val = $value;
			break;
		case CONDITION_TYPE_EVENT_ACKNOWLEDGED:
			$str_val = ($value) ? _("Ack") : _("Not Ack");
			break;
		case CONDITION_TYPE_APPLICATION:
			$str_val = $value;
			break;
		case CONDITION_TYPE_EVENT_TYPE:
			$str_val = eventType($value);
			break;
		default:
			return _("Unknown");
	}

	return $str_val;
}

/**
 * Returns the HTML representation of an action condition.
 *
 * @param $conditiontype
 * @param $operator
 * @param $value
 *
 * @return array
 */
function get_condition_desc($conditiontype, $operator, $value) {
	return CArray.array(
		condition_type2str($conditiontype),
		SPACE,
		condition_operator2str($operator),
		SPACE,
		italic(CHtml::encode(condition_value2str($conditiontype, $value)))
	);
}

/**
 * Generates array with HTML items representing operation with description
 *
 * @param int $type short or long description, use const. SHORT_DESCRIPTION and LONG_DESCRIPTION
 * @param array $data
 * @param int Nest.value($data,"operationtype").$() type of operation: OPERATION_TYPE_MESSAGE, OPERATION_TYPE_COMMAND, ...
 * @param int Nest.value($data,"opmessage","mediatypeid").$() type id of message media
 * @param bool Nest.value($data,"opmessage","default_msg").$() should default message be used
 * @param bool Nest.value($data,"opmessage","operationid").$() if true Nest.value($data,"operationid").$() will be used to retrieve default messages from DB
 * @param string Nest.value($data,"opmessage","subject").$() subject of message
 * @param string Nest.value($data,"opmessage","message").$() message it self
 * @param array Nest.value($data,"opmessage_usr").$() list of user ids if OPERATION_TYPE_MESSAGE
 * @param array Nest.value($data,"opmessage_grp").$() list of group ids if OPERATION_TYPE_MESSAGE
 * @param array Nest.value($data,"opcommand_grp").$() list of group ids if OPERATION_TYPE_COMMAND
 * @param array Nest.value($data,"opcommand_hst").$() list of host ids if OPERATION_TYPE_COMMAND
 * @param array Nest.value($data,"opgroup").$() list of group ids if OPERATION_TYPE_GROUP_ADD or OPERATION_TYPE_GROUP_REMOVE
 * @param array Nest.value($data,"optemplate").$() list of template ids if OPERATION_TYPE_TEMPLATE_ADD or OPERATION_TYPE_TEMPLATE_REMOVE
 * @param int Nest.value($data,"operationid").$() id of operation
 * @param int Nest.value($data,"opcommand","type").$() type of command: ZBX_SCRIPT_TYPE_IPMI, ZBX_SCRIPT_TYPE_SSH, ...
 * @param string Nest.value($data,"opcommand","command").$() actual command
 * @param int Nest.value($data,"opcommand","scriptid").$() script id used if Nest.value($data,"opcommand","type").$() is ZBX_SCRIPT_TYPE_GLOBAL_SCRIPT
 *
 * @return array
 */
function get_operation_descr($type, $data) {
	$result = CArray.array();

	if ($type == SHORT_DESCRIPTION) {
		switch (Nest.value($data,"operationtype").$()) {
			case OPERATION_TYPE_MESSAGE:
				$mediaTypes = API.Mediatype().get(CArray.array(
					"mediatypeids" => Nest.value($data,"opmessage","mediatypeid").$(),
					"output" => CArray.array("description")
				));
				if (empty($mediaTypes)) {
					$mediatype = _("all media");
				}
				else {
					$mediatype = reset($mediaTypes);
					$mediatype = Nest.value($mediatype,"description").$();
				}


				if (!empty(Nest.value($data,"opmessage_usr").$())) {
					$users = API.User().get(CArray.array(
						"userids" => zbx_objectValues(Nest.value($data,"opmessage_usr").$(), "userid"),
						"output" => CArray.array("userid", "alias", "name", "surname")
					));
					order_result($users, "alias");

					for($users as $user) {
						$fullnames[] = getUserFullname($user);
					}

					$result[] = bold(_("Send message to users").NAME_DELIMITER);
					$result[] = CArray.array(implode(", ", $fullnames), SPACE, _("via"), SPACE, $mediatype);
					$result[] = BR();
				}

				if (!empty(Nest.value($data,"opmessage_grp").$())) {
					$usrgrps = API.UserGroup().get(CArray.array(
						"usrgrpids" => zbx_objectValues(Nest.value($data,"opmessage_grp").$(), "usrgrpid"),
						"output" => API_OUTPUT_EXTEND
					));
					order_result($usrgrps, "name");

					$result[] = bold(_("Send message to user groups").NAME_DELIMITER);
					$result[] = CArray.array(implode(", ", zbx_objectValues($usrgrps, "name")), SPACE, _("via"), SPACE, $mediatype);
					$result[] = BR();
				}
				break;
			case OPERATION_TYPE_COMMAND:
				if (!isset(Nest.value($data,"opcommand_grp").$())) {
					Nest.value($data,"opcommand_grp").$() = CArray.array();
				}
				if (!isset(Nest.value($data,"opcommand_hst").$())) {
					Nest.value($data,"opcommand_hst").$() = CArray.array();
				}

				$hosts = API.Host().get(CArray.array(
					"hostids" => zbx_objectValues(Nest.value($data,"opcommand_hst").$(), "hostid"),
					"output" => CArray.array("hostid", "name")
				));

				for(Nest.value($data,"opcommand_hst").$() as $cmd) {
					if (Nest.value($cmd,"hostid").$() != 0) {
						continue;
					}

					$result[] = CArray.array(bold(_("Run remote commands on current host")), BR());
					break;
				}

				if (!empty($hosts)) {
					order_result($hosts, "name");

					$result[] = bold(_("Run remote commands on hosts").NAME_DELIMITER);
					$result[] = CArray.array(implode(", ", zbx_objectValues($hosts, "name")), BR());
				}

				$groups = API.HostGroup().get(CArray.array(
					"groupids" => zbx_objectValues(Nest.value($data,"opcommand_grp").$(), "groupid"),
					"output" => CArray.array("groupid", "name")
				));

				if (!empty($groups)) {
					order_result($groups, "name");

					$result[] = bold(_("Run remote commands on host groups").NAME_DELIMITER);
					$result[] = CArray.array(implode(", ", zbx_objectValues($groups, "name")), BR());
				}
				break;
			case OPERATION_TYPE_HOST_ADD:
				$result[] = CArray.array(bold(_("Add host")), BR());
				break;
			case OPERATION_TYPE_HOST_REMOVE:
				$result[] = CArray.array(bold(_("Remove host")), BR());
				break;
			case OPERATION_TYPE_HOST_ENABLE:
				$result[] = CArray.array(bold(_("Enable host")), BR());
				break;
			case OPERATION_TYPE_HOST_DISABLE:
				$result[] = CArray.array(bold(_("Disable host")), BR());
				break;
			case OPERATION_TYPE_GROUP_ADD:
			case OPERATION_TYPE_GROUP_REMOVE:
				if (!isset(Nest.value($data,"opgroup").$())) {
					Nest.value($data,"opgroup").$() = CArray.array();
				}

				$groups = API.HostGroup().get(CArray.array(
					"groupids" => zbx_objectValues(Nest.value($data,"opgroup").$(), "groupid"),
					"output" => CArray.array("groupid", "name")
				));

				if (!empty($groups)) {
					order_result($groups, "name");

					if (OPERATION_TYPE_GROUP_ADD == Nest.value($data,"operationtype").$()) {
						$result[] = bold(_("Add to host groups").NAME_DELIMITER);
					}
					else {
						$result[] = bold(_("Remove from host groups").NAME_DELIMITER);
					}

					$result[] = CArray.array(implode(", ", zbx_objectValues($groups, "name")), BR());
				}
				break;
			case OPERATION_TYPE_TEMPLATE_ADD:
			case OPERATION_TYPE_TEMPLATE_REMOVE:
				if (!isset(Nest.value($data,"optemplate").$())) {
					Nest.value($data,"optemplate").$() = CArray.array();
				}

				$templates = API.Template().get(CArray.array(
					"templateids" => zbx_objectValues(Nest.value($data,"optemplate").$(), "templateid"),
					"output" => CArray.array("hostid", "name")
				));

				if (!empty($templates)) {
					order_result($templates, "name");

					if (OPERATION_TYPE_TEMPLATE_ADD == Nest.value($data,"operationtype").$()) {
						$result[] = bold(_("Link to templates").NAME_DELIMITER);
					}
					else {
						$result[] = bold(_("Unlink from templates").NAME_DELIMITER);
					}

					$result[] = CArray.array(implode(", ", zbx_objectValues($templates, "name")), BR());
				}
				break;
			default:
		}
	}
	else {
		switch (Nest.value($data,"operationtype").$()) {
			case OPERATION_TYPE_MESSAGE:
				if (isset(Nest.value($data,"opmessage","default_msg").$()) && !empty(Nest.value($data,"opmessage","default_msg").$())) {
					if (isset(Nest.value(_REQUEST,"def_shortdata").$()) && isset(Nest.value(_REQUEST,"def_longdata").$())) {
						$result[] = CArray.array(bold(_("Subject").NAME_DELIMITER), BR(), zbx_nl2br(Nest.value(_REQUEST,"def_shortdata").$()));
						$result[] = CArray.array(bold(_("Message").NAME_DELIMITER), BR(), zbx_nl2br(Nest.value(_REQUEST,"def_longdata").$()));
					}
					elseif (isset(Nest.value($data,"opmessage","operationid").$())) {
						$sql = "SELECT a.def_shortdata,a.def_longdata ".
								" FROM actions a,operations o ".
								" WHERE a.actionid=o.actionid ".
									" AND o.operationid=".zbx_dbstr(Nest.value($data,"operationid").$());
						if ($rows = DBfetch(DBselect($sql, 1))) {
							$result[] = CArray.array(bold(_("Subject").NAME_DELIMITER), BR(), zbx_nl2br(Nest.value($rows,"def_shortdata").$()));
							$result[] = CArray.array(bold(_("Message").NAME_DELIMITER), BR(), zbx_nl2br(Nest.value($rows,"def_longdata").$()));
						}
					}
				}
				else {
					$result[] = CArray.array(bold(_("Subject").NAME_DELIMITER), BR(), zbx_nl2br(Nest.value($data,"opmessage","subject").$()));
					$result[] = CArray.array(bold(_("Message").NAME_DELIMITER), BR(), zbx_nl2br(Nest.value($data,"opmessage","message").$()));
				}

				break;
			case OPERATION_TYPE_COMMAND:
				switch (Nest.value($data,"opcommand","type").$()) {
					case ZBX_SCRIPT_TYPE_IPMI:
						$result[] = CArray.array(bold(_("Run IPMI command").NAME_DELIMITER), BR(), italic(zbx_nl2br(Nest.value($data,"opcommand","command").$())));
						break;
					case ZBX_SCRIPT_TYPE_SSH:
						$result[] = CArray.array(bold(_("Run SSH commands").NAME_DELIMITER), BR(), italic(zbx_nl2br(Nest.value($data,"opcommand","command").$())));
						break;
					case ZBX_SCRIPT_TYPE_TELNET:
						$result[] = CArray.array(bold(_("Run TELNET commands").NAME_DELIMITER), BR(), italic(zbx_nl2br(Nest.value($data,"opcommand","command").$())));
						break;
					case ZBX_SCRIPT_TYPE_CUSTOM_SCRIPT:
						if (Nest.value($data,"opcommand","execute_on").$() == ZBX_SCRIPT_EXECUTE_ON_AGENT) {
							$result[] = CArray.array(bold(_("Run custom commands on Zabbix agent").NAME_DELIMITER), BR(), italic(zbx_nl2br(Nest.value($data,"opcommand","command").$())));
						}
						else {
							$result[] = CArray.array(bold(_("Run custom commands on Zabbix server").NAME_DELIMITER), BR(), italic(zbx_nl2br(Nest.value($data,"opcommand","command").$())));
						}
						break;
					case ZBX_SCRIPT_TYPE_GLOBAL_SCRIPT:
						$userScripts = API.Script().get(CArray.array(
							"scriptids" => Nest.value($data,"opcommand","scriptid").$(),
							"output" => API_OUTPUT_EXTEND
						));
						$userScript = reset($userScripts);

						$result[] = CArray.array(bold(_("Run global script").NAME_DELIMITER), italic(Nest.value($userScript,"name").$()));
						break;
					default:
						$result[] = CArray.array(bold(_("Run commands").NAME_DELIMITER), BR(), italic(zbx_nl2br(Nest.value($data,"opcommand","command").$())));
				}
				break;
			default:
		}
	}

	return $result;
}

/**
 * Return an array of action conditions supported by the given event source.
 *
 * @param int $eventsource
 *
 * @return mixed
 */
function get_conditions_by_eventsource($eventsource) {
	$conditions[EVENT_SOURCE_TRIGGERS] = CArray.array(
		CONDITION_TYPE_APPLICATION,
		CONDITION_TYPE_HOST_GROUP,
		CONDITION_TYPE_TEMPLATE,
		CONDITION_TYPE_HOST,
		CONDITION_TYPE_TRIGGER,
		CONDITION_TYPE_TRIGGER_NAME,
		CONDITION_TYPE_TRIGGER_SEVERITY,
		CONDITION_TYPE_TRIGGER_VALUE,
		CONDITION_TYPE_TIME_PERIOD,
		CONDITION_TYPE_MAINTENANCE
	);
	$conditions[EVENT_SOURCE_DISCOVERY] = CArray.array(
		CONDITION_TYPE_DHOST_IP,
		CONDITION_TYPE_DSERVICE_TYPE,
		CONDITION_TYPE_DSERVICE_PORT,
		CONDITION_TYPE_DRULE,
		CONDITION_TYPE_DCHECK,
		CONDITION_TYPE_DOBJECT,
		CONDITION_TYPE_DSTATUS,
		CONDITION_TYPE_DUPTIME,
		CONDITION_TYPE_DVALUE,
		CONDITION_TYPE_PROXY
	);
	$conditions[EVENT_SOURCE_AUTO_REGISTRATION] = CArray.array(
		CONDITION_TYPE_HOST_NAME,
		CONDITION_TYPE_PROXY,
		CONDITION_TYPE_HOST_METADATA
	);
	$conditions[EVENT_SOURCE_INTERNAL] = CArray.array(
		CONDITION_TYPE_APPLICATION,
		CONDITION_TYPE_EVENT_TYPE,
		CONDITION_TYPE_HOST_GROUP,
		CONDITION_TYPE_TEMPLATE,
		CONDITION_TYPE_HOST
	);

	if (ZBX_DISTRIBUTED) {
		array_push($conditions[EVENT_SOURCE_TRIGGERS], CONDITION_TYPE_NODE);
		array_push($conditions[EVENT_SOURCE_INTERNAL], CONDITION_TYPE_NODE);
	}

	if (isset($conditions[$eventsource])) {
		return $conditions[$eventsource];
	}

	return $conditions[EVENT_SOURCE_TRIGGERS];
}

function get_opconditions_by_eventsource($eventsource) {
	$conditions = CArray.array(
		EVENT_SOURCE_TRIGGERS => CArray.array(CONDITION_TYPE_EVENT_ACKNOWLEDGED),
		EVENT_SOURCE_DISCOVERY => CArray.array(),
	);

	if (isset($conditions[$eventsource])) {
		return $conditions[$eventsource];
	}
}

function get_operations_by_eventsource($eventsource) {
	$operations[EVENT_SOURCE_TRIGGERS] = CArray.array(
		OPERATION_TYPE_MESSAGE,
		OPERATION_TYPE_COMMAND
	);
	$operations[EVENT_SOURCE_DISCOVERY] = CArray.array(
		OPERATION_TYPE_MESSAGE,
		OPERATION_TYPE_COMMAND,
		OPERATION_TYPE_HOST_ADD,
		OPERATION_TYPE_HOST_REMOVE,
		OPERATION_TYPE_GROUP_ADD,
		OPERATION_TYPE_GROUP_REMOVE,
		OPERATION_TYPE_TEMPLATE_ADD,
		OPERATION_TYPE_TEMPLATE_REMOVE,
		OPERATION_TYPE_HOST_ENABLE,
		OPERATION_TYPE_HOST_DISABLE
	);
	$operations[EVENT_SOURCE_AUTO_REGISTRATION] = CArray.array(
		OPERATION_TYPE_MESSAGE,
		OPERATION_TYPE_COMMAND,
		OPERATION_TYPE_HOST_ADD,
		OPERATION_TYPE_GROUP_ADD,
		OPERATION_TYPE_TEMPLATE_ADD,
		OPERATION_TYPE_HOST_DISABLE
	);
	$operations[EVENT_SOURCE_INTERNAL] = CArray.array(
		OPERATION_TYPE_MESSAGE
	);

	if (isset($operations[$eventsource])) {
		return $operations[$eventsource];
	}

	return $operations[EVENT_SOURCE_TRIGGERS];
}

function operation_type2str($type = null) {
	$types = CArray.array(
		OPERATION_TYPE_MESSAGE => _("Send message"),
		OPERATION_TYPE_COMMAND => _("Remote command"),
		OPERATION_TYPE_HOST_ADD => _("Add host"),
		OPERATION_TYPE_HOST_REMOVE => _("Remove host"),
		OPERATION_TYPE_HOST_ENABLE => _("Enable host"),
		OPERATION_TYPE_HOST_DISABLE => _("Disable host"),
		OPERATION_TYPE_GROUP_ADD => _("Add to host group"),
		OPERATION_TYPE_GROUP_REMOVE => _("Remove from host group"),
		OPERATION_TYPE_TEMPLATE_ADD => _("Link to template"),
		OPERATION_TYPE_TEMPLATE_REMOVE => _("Unlink from template")
	);

	if (is_null($type)) {
		return order_result($types);
	}
	elseif (isset($types[$type])) {
		return $types[$type];
	}
	else {
		return _("Unknown");
	}
}

function sortOperations($eventsource, &$operations) {
	if ($eventsource == EVENT_SOURCE_TRIGGERS || $eventsource == EVENT_SOURCE_INTERNAL) {
		$esc_step_from = CArray.array();
		$esc_step_to = CArray.array();
		$esc_period = CArray.array();
		$operationTypes = CArray.array();

		for($operations as $key => $operation) {
			$esc_step_from[$key] = Nest.value($operation,"esc_step_from").$();
			$esc_step_to[$key] = Nest.value($operation,"esc_step_to").$();
			$esc_period[$key] = Nest.value($operation,"esc_period").$();
			$operationTypes[$key] = Nest.value($operation,"operationtype").$();
		}
		array_multisort($esc_step_from, SORT_ASC, $esc_step_to, SORT_ASC, $esc_period, SORT_ASC, $operationTypes, SORT_ASC, $operations);
	}
	else {
		CArrayHelper::sort($operations, CArray.array("operationtype"));
	}
}

/**
 * Return an array of operators supported by the given action condition.
 *
 * @param int $conditiontype
 *
 * @return array
 */
function get_operators_by_conditiontype($conditiontype) {
	$operators[CONDITION_TYPE_HOST_GROUP] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_TEMPLATE] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_HOST] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_TRIGGER] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_TRIGGER_NAME] = CArray.array(
		CONDITION_OPERATOR_LIKE,
		CONDITION_OPERATOR_NOT_LIKE
	);
	$operators[CONDITION_TYPE_TRIGGER_SEVERITY] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL,
		CONDITION_OPERATOR_MORE_EQUAL,
		CONDITION_OPERATOR_LESS_EQUAL
	);
	$operators[CONDITION_TYPE_TRIGGER_VALUE] = CArray.array(
		CONDITION_OPERATOR_EQUAL
	);
	$operators[CONDITION_TYPE_TIME_PERIOD] = CArray.array(
		CONDITION_OPERATOR_IN,
		CONDITION_OPERATOR_NOT_IN
	);
	$operators[CONDITION_TYPE_MAINTENANCE] = CArray.array(
		CONDITION_OPERATOR_IN,
		CONDITION_OPERATOR_NOT_IN
	);
	$operators[CONDITION_TYPE_NODE] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DRULE] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DCHECK] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DOBJECT] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
	);
	$operators[CONDITION_TYPE_PROXY] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DHOST_IP] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DSERVICE_TYPE] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DSERVICE_PORT] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL
	);
	$operators[CONDITION_TYPE_DSTATUS] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
	);
	$operators[CONDITION_TYPE_DUPTIME] = CArray.array(
		CONDITION_OPERATOR_MORE_EQUAL,
		CONDITION_OPERATOR_LESS_EQUAL
	);
	$operators[CONDITION_TYPE_DVALUE] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_NOT_EQUAL,
		CONDITION_OPERATOR_MORE_EQUAL,
		CONDITION_OPERATOR_LESS_EQUAL,
		CONDITION_OPERATOR_LIKE,
		CONDITION_OPERATOR_NOT_LIKE
	);
	$operators[CONDITION_TYPE_EVENT_ACKNOWLEDGED] = CArray.array(
		CONDITION_OPERATOR_EQUAL
	);
	$operators[CONDITION_TYPE_APPLICATION] = CArray.array(
		CONDITION_OPERATOR_EQUAL,
		CONDITION_OPERATOR_LIKE,
		CONDITION_OPERATOR_NOT_LIKE
	);
	$operators[CONDITION_TYPE_HOST_NAME] = CArray.array(
		CONDITION_OPERATOR_LIKE,
		CONDITION_OPERATOR_NOT_LIKE
	);
	$operators[CONDITION_TYPE_EVENT_TYPE] = CArray.array(
		CONDITION_OPERATOR_EQUAL
	);
	$operators[CONDITION_TYPE_HOST_METADATA] = CArray.array(
		CONDITION_OPERATOR_LIKE,
		CONDITION_OPERATOR_NOT_LIKE
	);

	if (isset($operators[$conditiontype])) {
		return $operators[$conditiontype];
	}

	return CArray.array();
}

function count_operations_delay($operations, $def_period = 0) {
	$delays = CArray.array(1 => 0);
	$periods = CArray.array();
	$max_step = 0;

	for($operations as $operation) {
		$step_to = Nest.value($operation,"esc_step_to").$() ? Nest.value($operation,"esc_step_to").$() : 9999;
		$esc_period = Nest.value($operation,"esc_period").$() ? Nest.value($operation,"esc_period").$() : $def_period;

		if ($max_step < Nest.value($operation,"esc_step_from").$()) {
			$max_step = Nest.value($operation,"esc_step_from").$();
		}

		for ($i = Nest.value($operation,"esc_step_from").$(); $i <= $step_to; $i++) {
			if (!isset($periods[$i]) || $periods[$i] > $esc_period) {
				$periods[$i] = $esc_period;
			}
		}
	}

	for ($i = 1; $i <= $max_step; $i++) {
		$esc_period = isset($periods[$i]) ? $periods[$i] : $def_period;
		$delays[$i+1] = $delays[$i] + $esc_period;
	}

	return $delays;
}

function get_action_msgs_for_event($event) {
	$table = new CTableInfo(_("No actions found."));
	$table.setHeader(CArray.array(
		is_show_all_nodes() ? _("Nodes") : null,
		_("Time"),
		_("Type"),
		_("Status"),
		_("Retries left"),
		_("Recipient(s)"),
		_("Message"),
		_("Error")
	));

	$alerts = Nest.value($event,"alerts").$();
	for($alerts as $alertid => $alert) {
		if (Nest.value($alert,"alerttype").$() != ALERT_TYPE_MESSAGE) {
			continue;
		}

		$mediatype = array_pop(Nest.value($alert,"mediatypes").$());

		$time = zbx_date2str(EVENT_ACTION_MESSAGES_DATE_FORMAT, Nest.value($alert,"clock").$());
		if (Nest.value($alert,"esc_step").$() > 0) {
			$time = CArray.array(
				bold(_("Step").NAME_DELIMITER),
				$alert[\"esc_step\"],
				br(),
				bold(_("Time").NAME_DELIMITER),
				br(),
				$time
			);
		}

		if (Nest.value($alert,"status").$() == ALERT_STATUS_SENT) {
			$status = new CSpan(_("sent"), "green");
			$retries = new CSpan(SPACE, "green");
		}
		elseif (Nest.value($alert,"status").$() == ALERT_STATUS_NOT_SENT) {
			$status = new CSpan(_("In progress"), "orange");
			$retries = new CSpan(ALERT_MAX_RETRIES - Nest.value($alert,"retries").$(), "orange");
		}
		else {
			$status = new CSpan(_("not sent"), "red");
			$retries = new CSpan(0, "red");
		}
		$sendto = Nest.value($alert,"sendto").$();

		$message = CArray.array(
			bold(_("Subject").NAME_DELIMITER),
			br(),
			Nest.value($alert,"subject").$(),
			br(),
			br(),
			bold(_("Message").NAME_DELIMITER)
		);
		array_push($message, BR(), zbx_nl2br(Nest.value($alert,"message").$()));

		if (empty(Nest.value($alert,"error").$())) {
			$error = new CSpan(SPACE, "off");
		}
		else {
			$error = new CSpan(Nest.value($alert,"error").$(), "on");
		}

		$table.addRow(CArray.array(
			get_node_name_by_elid(Nest.value($alert,"alertid").$()),
			new CCol($time, "top"),
			new CCol((!empty(Nest.value($mediatype,"description").$()) ? Nest.value($mediatype,"description").$() : ""), "top"),
			new CCol($status, "top"),
			new CCol($retries, "top"),
			new CCol($sendto, "top"),
			new CCol($message, "wraptext top"),
			new CCol($error, "wraptext top")
		));
	}

	return $table;
}

function get_action_cmds_for_event($event) {
	$table = new CTableInfo(_("No actions found."));
	$table.setHeader(CArray.array(
		is_show_all_nodes() ? _("Nodes") : null,
		_("Time"),
		_("Status"),
		_("Command"),
		_("Error")
	));

	$alerts = Nest.value($event,"alerts").$();
	for($alerts as $alert) {
		if (Nest.value($alert,"alerttype").$() != ALERT_TYPE_COMMAND) {
			continue;
		}

		$time = zbx_date2str(EVENT_ACTION_CMDS_DATE_FORMAT, Nest.value($alert,"clock").$());
		if (Nest.value($alert,"esc_step").$() > 0) {
			$time = CArray.array(
				bold(_("Step").NAME_DELIMITER),
				Nest.value($alert,"esc_step").$(),
				br(),
				bold(_("Time").NAME_DELIMITER),
				br(),
				$time
			);
		}

		switch (Nest.value($alert,"status").$()) {
			case ALERT_STATUS_SENT:
				$status = new CSpan(_("executed"), "green");
				break;
			case ALERT_STATUS_NOT_SENT:
				$status = new CSpan(_("In progress"), "orange");
				break;
			default:
				$status = new CSpan(_("not sent"), "red");
				break;
		}

		$message = CArray.array(bold(_("Command").NAME_DELIMITER));
		array_push($message, BR(), zbx_nl2br(Nest.value($alert,"message").$()));

		$error = empty(Nest.value($alert,"error").$()) ? new CSpan(SPACE, "off") : new CSpan(Nest.value($alert,"error").$(), "on");

		$table.addRow(CArray.array(
			get_node_name_by_elid(Nest.value($alert,"alertid").$()),
			new CCol($time, "top"),
			new CCol($status, "top"),
			new CCol($message, "wraptext top"),
			new CCol($error, "wraptext top")
		));
	}

	return $table;
}

function get_actions_hint_by_eventid($eventid, $status = null) {
	$tab_hint = new CTableInfo(_("No actions found."));
	$tab_hint.setAttribute("style", "width: 300px;");
	$tab_hint.setHeader(CArray.array(
		is_show_all_nodes() ? _("Nodes") : null,
		_("User"),
		_("Details"),
		_("Status")
	));

	$sql = "SELECT a.alertid,mt.description,u.alias,u.name,u.surname,a.subject,a.message,a.sendto,a.status,a.retries,a.alerttype".
			" FROM events e,alerts a".
				" LEFT JOIN users u ON u.userid=a.userid".
				" LEFT JOIN media_type mt ON mt.mediatypeid=a.mediatypeid".
			" WHERE a.eventid=".zbx_dbstr($eventid).
				(is_null($status)?"":" AND a.status=".$status).
				" AND e.eventid=a.eventid".
				" AND a.alerttype IN (".ALERT_TYPE_MESSAGE.",".ALERT_TYPE_COMMAND.")".
				andDbNode("a.alertid").
			" ORDER BY a.alertid";
	$result = DBselect($sql, 30);

	while ($row = DBfetch($result)) {
		if (Nest.value($row,"status").$() == ALERT_STATUS_SENT) {
			$status = new CSpan(_("Sent"), "green");
		}
		elseif (Nest.value($row,"status").$() == ALERT_STATUS_NOT_SENT) {
			$status = new CSpan(_("In progress"), "orange");
		}
		else {
			$status = new CSpan(_("not sent"), "red");
		}

		switch (Nest.value($row,"alerttype").$()) {
			case ALERT_TYPE_MESSAGE:
				$message = empty(Nest.value($row,"description").$()) ? "-" : Nest.value($row,"description").$();
				break;
			case ALERT_TYPE_COMMAND:
				$message = CArray.array(bold(_("Command").NAME_DELIMITER));
				$msg = explode(\"\n\", Nest.value($row,"message").$());
				for($msg as $m) {
					array_push($message, BR(), $m);
				}
				break;
			default:
				$message = "-";
		}

		if (!Nest.value($row,"alias").$()) {
			Nest.value($row,"alias").$() = " - ";
		}
		else {
			$fullname = "";
			if (Nest.value($row,"name").$()) {
				$fullname = Nest.value($row,"name").$();
			}
			if (Nest.value($row,"surname").$()) {
				$fullname .= $fullname ? " ".Nest.value($row,"surname").$() : Nest.value($row,"surname").$();
			}
			if ($fullname) {
				Nest.value($row,"alias").$() .= " (".$fullname.")";
			}
		}

		$tab_hint.addRow(CArray.array(
			get_node_name_by_elid(Nest.value($row,"alertid").$()),
			Nest.value($row,"alias").$(),
			$message,
			$status
		));
	}
	return $tab_hint;
}

function getEventActionsStatus($eventIds) {
	if (empty($eventIds)) {
		return CArray.array();
	}

	$actions = CArray.array();

	$alerts = DBselect(
		"SELECT a.eventid,a.status,COUNT(a.alertid) AS cnt".
		" FROM alerts a".
		" WHERE a.alerttype IN (".ALERT_TYPE_MESSAGE.",".ALERT_TYPE_COMMAND.")".
			" AND ".dbConditionInt("a.eventid", $eventIds).
		" GROUP BY eventid,status"
	);

	while ($alert = DBfetch($alerts)) {
		$actions[$alert["eventid"]][$alert["status"]] = Nest.value($alert,"cnt").$();
	}

	for($actions as $eventId => $action) {
		$sendCount = isset($action[ALERT_STATUS_SENT]) ? $action[ALERT_STATUS_SENT] : 0;
		$notSendCount = isset($action[ALERT_STATUS_NOT_SENT]) ? $action[ALERT_STATUS_NOT_SENT] : 0;
		$failedCount = isset($action[ALERT_STATUS_FAILED]) ? $action[ALERT_STATUS_FAILED] : 0;

		// calculate total
		$mixed = 0;
		if ($sendCount > 0) {
			$mixed += ALERT_STATUS_SENT;
		}
		if ($failedCount > 0) {
			$mixed += ALERT_STATUS_FAILED;
		}

		// display
		if ($notSendCount > 0) {
			$status = new CSpan(_("In progress"), "orange");
		}
		elseif ($mixed == ALERT_STATUS_SENT) {
			$status = new CSpan(_("Ok"), "green");
		}
		elseif ($mixed == ALERT_STATUS_FAILED) {
			$status = new CSpan(_("Failed"), "red");
		}
		else {
			$columnLeft = new CCol(($sendCount > 0) ? new CSpan($sendCount, "green") : SPACE);
			$columnLeft.setAttribute("width", "10");

			$columnRight = new CCol(($failedCount > 0) ? new CSpan($failedCount, "red") : SPACE);
			$columnRight.setAttribute("width", "10");

			$status = new CRow(CArray.array($columnLeft, $columnRight));
		}

		$actions[$eventId] = new CTable(" - ");
		$actions[$eventId].addRow($status);
	}

	return $actions;
}

function getEventActionsStatHints($eventIds) {
	if (empty($eventIds)) {
		return CArray.array();
	}

	$actions = CArray.array();

	$alerts = DBselect(
		"SELECT a.eventid,a.status,COUNT(a.alertid) AS cnt".
		" FROM alerts a".
		" WHERE a.alerttype IN (".ALERT_TYPE_MESSAGE.",".ALERT_TYPE_COMMAND.")".
			" AND ".dbConditionInt("a.eventid", $eventIds).
		" GROUP BY eventid,status"
	);

	while ($alert = DBfetch($alerts)) {
		if (Nest.value($alert,"cnt").$() > 0) {
			if (Nest.value($alert,"status").$() == ALERT_STATUS_SENT) {
				$color = "green";
			}
			elseif (Nest.value($alert,"status").$() == ALERT_STATUS_NOT_SENT) {
				$color = "orange";
			}
			else {
				$color = "red";
			}

			$hint = new CSpan(Nest.value($alert,"cnt").$(), $color);
			$hint.setHint(get_actions_hint_by_eventid(Nest.value($alert,"eventid").$(), Nest.value($alert,"status").$()));

			$actions[$alert["eventid"]][$alert["status"]] = $hint;
		}
	}

	for($actions as $eventId => $action) {
		$actions[$eventId] = new CDiv(null, "event-action-cont");
		$actions[$eventId].addItem(CArray.array(
			new CDiv(isset($action[ALERT_STATUS_SENT]) ? $action[ALERT_STATUS_SENT] : SPACE),
			new CDiv(isset($action[ALERT_STATUS_NOT_SENT]) ? $action[ALERT_STATUS_NOT_SENT] : SPACE),
			new CDiv(isset($action[ALERT_STATUS_FAILED]) ? $action[ALERT_STATUS_FAILED] : SPACE)
		));
	}

	return $actions;
}

/**
 * Returns the names of the \"Event type\" action condition values.
 *
 * If the $type parameter is passed, returns the name of the specific value, otherwise - returns an array of all
 * supported values.
 *
 * @param string $type
 *
 * @return array|string
 */
function eventType($type = null) {
	$types = CArray.array(
		EVENT_TYPE_ITEM_NOTSUPPORTED => _("Item in \"not supported\" state"),
		EVENT_TYPE_ITEM_NORMAL => _("Item in \"normal\" state"),
		EVENT_TYPE_LLDRULE_NOTSUPPORTED => _("Low-level discovery rule in \"not supported\" state"),
		EVENT_TYPE_LLDRULE_NORMAL => _("Low-level discovery rule in \"normal\" state"),
		EVENT_TYPE_TRIGGER_UNKNOWN => _("Trigger in \"unknown\" state"),
		EVENT_TYPE_TRIGGER_NORMAL => _("Trigger in \"normal\" state")
	);

	if (is_null($type)) {
		return $types;
	}
	elseif (isset($types[$type])) {
		return $types[$type];
	}
	else {
		return _("Unknown");
	}
}
