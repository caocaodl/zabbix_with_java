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


class CMacrosResolver extends CMacrosResolverGeneral {

	/**
	 * Supported macros resolving scenarios.
	 *
	 * @var array
	 */
	protected $configs = CArray.array(
		"scriptConfirmation" => CArray.array(
			"types" => CArray.array("host", "interfaceWithoutPort", "user"),
			"method" => "resolveTexts"
		),
		"httpTestName" => CArray.array(
			"types" => CArray.array("host", "interfaceWithoutPort", "user"),
			"method" => "resolveTexts"
		),
		"hostInterfaceIpDns" => CArray.array(
			"types" => CArray.array("host", "agentInterface", "user"),
			"method" => "resolveTexts"
		),
		"hostInterfaceIpDnsAgentPrimary" => CArray.array(
			"types" => CArray.array("host", "user"),
			"method" => "resolveTexts"
		),
		"hostInterfacePort" => CArray.array(
			"types" => CArray.array("user"),
			"method" => "resolveTexts"
		),
		"triggerName" => CArray.array(
			"types" => CArray.array("host", "interface", "user", "item", "reference"),
			"source" => "description",
			"method" => "resolveTrigger"
		),
		"triggerDescription" => CArray.array(
			"types" => CArray.array("host", "interface", "user", "item"),
			"source" => "comments",
			"method" => "resolveTrigger"
		),
		"triggerExpressionUser" => CArray.array(
			"types" => CArray.array("user"),
			"source" => "expression",
			"method" => "resolveTrigger"
		),
		"eventDescription" => CArray.array(
			"types" => CArray.array("host", "interface", "user", "item", "reference"),
			"source" => "description",
			"method" => "resolveTrigger"
		),
		"graphName" => CArray.array(
			"types" => CArray.array("graphFunctionalItem"),
			"source" => "name",
			"method" => "resolveGraph"
		)
	);

	/**
	 * Resolve macros.
	 *
	 * Macros examples:
	 * reference: $1, $2, $3, ...
	 * user: {$MACRO1}, {$MACRO2}, ...
	 * host: {HOSTNAME}, {HOST.HOST}, {HOST.NAME}
	 * ip: {IPADDRESS}, {HOST.IP}, {HOST.DNS}, {HOST.CONN}
	 * item: {ITEM.LASTVALUE}, {ITEM.VALUE}
	 *
	 * @param array  $options
	 * @param string $options["config"]
	 * @param array  $options["data"]
	 *
	 * @return array
	 */
	public function resolve(array $options) {
		if (empty(Nest.value($options,"data").$())) {
			return CArray.array();
		}

		config = Nest.value($options,"config").$();

		// call method
		$method = configs[config]["method"];

		return $method(Nest.value($options,"data").$());
	}

	/**
	 * Batch resolving macros in text using host id.
	 *
	 * @param array $data	(as $hostId => CArray.array(texts))
	 *
	 * @return array		(as $hostId => CArray.array(texts))
	 */
	private function resolveTexts(array $data) {
		$hostIds = array_keys($data);

		$macros = CArray.array();

		$hostMacrosAvailable = $agentInterfaceAvailable = $interfaceWithoutPortMacrosAvailable = false;

		if (isTypeAvailable("host")) {
			for($data as $hostId => $texts) {
				if ($hostMacros = findMacros(self::PATTERN_HOST, $texts)) {
					for($hostMacros as $hostMacro) {
						$macros[$hostId][$hostMacro] = UNRESOLVED_MACRO_STRING;
					}

					$hostMacrosAvailable = true;
				}
			}
		}

		if (isTypeAvailable("agentInterface")) {
			for($data as $hostId => $texts) {
				if ($interfaceMacros = findMacros(self::PATTERN_INTERFACE, $texts)) {
					for($interfaceMacros as $interfaceMacro) {
						$macros[$hostId][$interfaceMacro] = UNRESOLVED_MACRO_STRING;
					}

					$agentInterfaceAvailable = true;
				}
			}
		}

		if (isTypeAvailable("interfaceWithoutPort")) {
			for($data as $hostId => $texts) {
				if ($interfaceMacros = findMacros(self::PATTERN_INTERFACE, $texts)) {
					for($interfaceMacros as $interfaceMacro) {
						$macros[$hostId][$interfaceMacro] = UNRESOLVED_MACRO_STRING;
					}

					$interfaceWithoutPortMacrosAvailable = true;
				}
			}
		}

		// host macros
		if ($hostMacrosAvailable) {
			$dbHosts = DBselect("SELECT h.hostid,h.name,h.host FROM hosts h WHERE ".dbConditionInt("h.hostid", $hostIds));

			while ($dbHost = DBfetch($dbHosts)) {
				$hostId = Nest.value($dbHost,"hostid").$();

				if ($hostMacros = findMacros(self::PATTERN_HOST, $data[$hostId])) {
					for($hostMacros as $hostMacro) {
						switch ($hostMacro) {
							case "{HOSTNAME}":
							case "{HOST.HOST}":
								$macros[$hostId][$hostMacro] = Nest.value($dbHost,"host").$();
								break;
							case "{HOST.NAME}":
								$macros[$hostId][$hostMacro] = Nest.value($dbHost,"name").$();
								break;
						}
					}
				}
			}
		}

		// interface macros, macro should be resolved to main agent interface
		if ($agentInterfaceAvailable) {
			for($data as $hostId => $texts) {
				if ($interfaceMacros = findMacros(self::PATTERN_INTERFACE, $texts)) {
					$dbInterface = DBfetch(DBselect(
						"SELECT i.hostid,i.ip,i.dns,i.useip".
						" FROM interface i".
						" WHERE i.main=".INTERFACE_PRIMARY.
							" AND i.type=".INTERFACE_TYPE_AGENT.
							" AND i.hostid=".zbx_dbstr($hostId)
					));

					$dbInterfaceTexts = CArray.array(Nest.value($dbInterface,"ip").$(), Nest.value($dbInterface,"dns").$());

					if (findMacros(self::PATTERN_HOST, $dbInterfaceTexts)
							|| findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, $dbInterfaceTexts)) {
						$saveCurrentConfig = config;

						$dbInterfaceMacros = resolve(CArray.array(
							"config" => "hostInterfaceIpDnsAgentPrimary",
							"data" => CArray.array($hostId => $dbInterfaceTexts)
						));

						$dbInterfaceMacros = reset($dbInterfaceMacros);
						Nest.value($dbInterface,"ip").$() = $dbInterfaceMacros[0];
						Nest.value($dbInterface,"dns").$() = $dbInterfaceMacros[1];

						config = $saveCurrentConfig;
					}

					for($interfaceMacros as $interfaceMacro) {
						switch ($interfaceMacro) {
							case "{IPADDRESS}":
							case "{HOST.IP}":
								$macros[$hostId][$interfaceMacro] = Nest.value($dbInterface,"ip").$();
								break;
							case "{HOST.DNS}":
								$macros[$hostId][$interfaceMacro] = Nest.value($dbInterface,"dns").$();
								break;
							case "{HOST.CONN}":
								$macros[$hostId][$interfaceMacro] = Nest.value($dbInterface,"useip").$() ? Nest.value($dbInterface,"ip").$() : Nest.value($dbInterface,"dns").$();
								break;
						}
					}
				}
			}
		}

		// interface macros, macro should be resolved to interface with highest priority
		if ($interfaceWithoutPortMacrosAvailable) {
			$interfaces = CArray.array();

			$dbInterfaces = DBselect(
				"SELECT i.hostid,i.ip,i.dns,i.useip,i.type".
				" FROM interface i".
				" WHERE i.main=".INTERFACE_PRIMARY.
					" AND ".dbConditionInt("i.hostid", $hostIds).
					" AND ".dbConditionInt("i.type", interfacePriorities)
			);

			while ($dbInterface = DBfetch($dbInterfaces)) {
				$hostId = Nest.value($dbInterface,"hostid").$();

				if (isset($interfaces[$hostId])) {
					$dbPriority = interfacePriorities[$dbInterface["type"]];
					$existPriority = interfacePriorities[$interfaces[$hostId]["type"]];

					if ($dbPriority > $existPriority) {
						$interfaces[$hostId] = $dbInterface;
					}
				}
				else {
					$interfaces[$hostId] = $dbInterface;
				}
			}

			if ($interfaces) {
				for($interfaces as $hostId => $interface) {
					if ($interfaceMacros = findMacros(self::PATTERN_INTERFACE, $data[$hostId])) {
						for($interfaceMacros as $interfaceMacro) {
							switch ($interfaceMacro) {
								case "{IPADDRESS}":
								case "{HOST.IP}":
									$macros[$hostId][$interfaceMacro] = Nest.value($interface,"ip").$();
									break;
								case "{HOST.DNS}":
									$macros[$hostId][$interfaceMacro] = Nest.value($interface,"dns").$();
									break;
								case "{HOST.CONN}":
									$macros[$hostId][$interfaceMacro] = Nest.value($interface,"useip").$() ? Nest.value($interface,"ip").$() : Nest.value($interface,"dns").$();
									break;
							}

							// Resolving macros to AGENT main interface. If interface is AGENT macros stay unresolved.
							if (Nest.value($interface,"type").$() != INTERFACE_TYPE_AGENT) {
								if (findMacros(self::PATTERN_HOST, CArray.array($macros[$hostId][$interfaceMacro]))
										|| findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, CArray.array($macros[$hostId][$interfaceMacro]))) {
									// attention recursion!
									$macrosInMacros = resolveTexts(CArray.array($hostId => CArray.array($macros[$hostId][$interfaceMacro])));
									$macros[$hostId][$interfaceMacro] = $macrosInMacros[$hostId][0];
								}
								elseif (findMacros(self::PATTERN_INTERFACE, CArray.array($macros[$hostId][$interfaceMacro]))) {
									$macros[$hostId][$interfaceMacro] = UNRESOLVED_MACRO_STRING;
								}
							}
						}
					}
				}
			}
		}

		// get user macros
		if (isTypeAvailable("user")) {
			$userMacrosData = CArray.array();

			for($data as $hostId => $texts) {
				$userMacros = findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, $texts);

				for($userMacros as $userMacro) {
					if (!isset($userMacrosData[$hostId])) {
						$userMacrosData[$hostId] = CArray.array(
							"hostids" => CArray.array($hostId),
							"macros" => CArray.array()
						);
					}

					$userMacrosData[$hostId]["macros"][$userMacro] = null;
				}
			}

			$userMacros = getUserMacros($userMacrosData);

			for($userMacros as $hostId => $userMacro) {
				$macros[$hostId] = isset($macros[$hostId])
					? array_merge($macros[$hostId], Nest.value($userMacro,"macros").$())
					: Nest.value($userMacro,"macros").$();
			}
		}

		// replace macros to value
		if ($macros) {
			for($data as $hostId => $texts) {
				if (isset($macros[$hostId])) {
					for($texts as $tnum => $text) {
						preg_match_all("/".self::PATTERN_HOST."|".self::PATTERN_INTERFACE."|".ZBX_PREG_EXPRESSION_USER_MACROS."/", $text, $matches, PREG_OFFSET_CAPTURE);

						for ($i = count($matches[0]) - 1; $i >= 0; $i--) {
							$matche = $matches[0][$i];

							$macrosValue = isset($macros[$hostId][$matche[0]]) ? $macros[$hostId][$matche[0]] : $matche[0];
							$text = substr_replace($text, $macrosValue, $matche[1], strlen($matche[0]));
						}

						$data[$hostId][$tnum] = $text;
					}
				}
			}
		}

		return $data;
	}

	/**
	 * Resolve macros in trigger.
	 *
	 * @param string $triggers[$triggerId]["expression"]
	 * @param string $triggers[$triggerId]["description"]			depend from config
	 * @param string $triggers[$triggerId]["comments"]				depend from config
	 *
	 * @return array
	 */
	private function resolveTrigger(array $triggers) {
		$macros = CArray.array(
			"host" => CArray.array(),
			"interfaceWithoutPort" => CArray.array(),
			"interface" => CArray.array(),
			"item" => CArray.array()
		);
		$macroValues = $userMacrosData = CArray.array();

		// get source field
		$source = getSource();

		// get available functions
		$hostMacrosAvailable = isTypeAvailable("host");
		$interfaceWithoutPortMacrosAvailable = isTypeAvailable("interfaceWithoutPort");
		$interfaceMacrosAvailable = isTypeAvailable("interface");
		$itemMacrosAvailable = isTypeAvailable("item");
		$userMacrosAvailable = isTypeAvailable("user");
		$referenceMacrosAvailable = isTypeAvailable("reference");

		// find macros
		for($triggers as $triggerId => $trigger) {
			if ($userMacrosAvailable) {
				$userMacros = findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, CArray.array($trigger[$source]));

				if ($userMacros) {
					if (!isset($userMacrosData[$triggerId])) {
						$userMacrosData[$triggerId] = CArray.array("macros" => CArray.array(), "hostids" => CArray.array());
					}

					for($userMacros as $userMacro) {
						$userMacrosData[$triggerId]["macros"][$userMacro] = null;
					}
				}
			}

			$functions = findFunctions(Nest.value($trigger,"expression").$());

			if ($hostMacrosAvailable) {
				for(findFunctionMacros(self::PATTERN_HOST_FUNCTION, $trigger[$source]) as $macro => $fNums) {
					for($fNums as $fNum) {
						$macroValues[$triggerId][getFunctionMacroName($macro, $fNum)] = UNRESOLVED_MACRO_STRING;

						if (isset($functions[$fNum])) {
							$macros["host"][$functions[$fNum]][$macro][] = $fNum;
						}
					}
				}
			}

			if ($interfaceWithoutPortMacrosAvailable) {
				for(findFunctionMacros(self::PATTERN_INTERFACE_FUNCTION_WITHOUT_PORT, $trigger[$source]) as $macro => $fNums) {
					for($fNums as $fNum) {
						$macroValues[$triggerId][getFunctionMacroName($macro, $fNum)] = UNRESOLVED_MACRO_STRING;

						if (isset($functions[$fNum])) {
							$macros["interfaceWithoutPort"][$functions[$fNum]][$macro][] = $fNum;
						}
					}
				}
			}

			if ($interfaceMacrosAvailable) {
				for(findFunctionMacros(self::PATTERN_INTERFACE_FUNCTION, $trigger[$source]) as $macro => $fNums) {
					for($fNums as $fNum) {
						$macroValues[$triggerId][getFunctionMacroName($macro, $fNum)] = UNRESOLVED_MACRO_STRING;

						if (isset($functions[$fNum])) {
							$macros["interface"][$functions[$fNum]][$macro][] = $fNum;
						}
					}
				}
			}

			if ($itemMacrosAvailable) {
				for(findFunctionMacros(self::PATTERN_ITEM_FUNCTION, $trigger[$source]) as $macro => $fNums) {
					for($fNums as $fNum) {
						$macroValues[$triggerId][getFunctionMacroName($macro, $fNum)] = UNRESOLVED_MACRO_STRING;

						if (isset($functions[$fNum])) {
							$macros["item"][$functions[$fNum]][$macro][] = $fNum;
						}
					}
				}
			}

			if ($referenceMacrosAvailable) {
				for(getTriggerReference(Nest.value($trigger,"expression").$(), $trigger[$source]) as $macro => $value) {
					$macroValues[$triggerId][$macro] = $value;
				}
			}
		}

		// get macro value
		if ($hostMacrosAvailable) {
			$macroValues = getHostMacros(Nest.value($macros,"host").$(), $macroValues);
		}
		if ($interfaceWithoutPortMacrosAvailable) {
			$macroValues = getIpMacros(Nest.value($macros,"interfaceWithoutPort").$(), $macroValues, false);
		}
		if ($interfaceMacrosAvailable) {
			$macroValues = getIpMacros(Nest.value($macros,"interface").$(), $macroValues, true);
			$patternInterfaceFunction = self::PATTERN_INTERFACE_FUNCTION;
		}
		else {
			$patternInterfaceFunction = self::PATTERN_INTERFACE_FUNCTION_WITHOUT_PORT;
		}
		if ($itemMacrosAvailable) {
			$macroValues = getItemMacros(Nest.value($macros,"item").$(), $triggers, $macroValues);
		}
		if ($userMacrosData) {
			// get hosts for triggers
			$dbTriggers = API.Trigger().get(CArray.array(
				"output" => CArray.array("triggerid"),
				"selectHosts" => CArray.array("hostid"),
				"triggerids" => array_keys($userMacrosData),
				"preservekeys" => true
			));

			for($userMacrosData as $triggerId => $userMacro) {
				if (isset($dbTriggers[$triggerId])) {
					$userMacrosData[$triggerId]["hostids"] =
						zbx_objectValues($dbTriggers[$triggerId]["hosts"], "hostid");
				}
			}

			// get user macros values
			$userMacros = getUserMacros($userMacrosData);

			for($userMacros as $triggerId => $userMacro) {
				$macroValues[$triggerId] = isset($macroValues[$triggerId])
					? array_merge($macroValues[$triggerId], Nest.value($userMacro,"macros").$())
					: Nest.value($userMacro,"macros").$();
			}
		}

		// replace macros to value
		for($triggers as $triggerId => $trigger) {
			preg_match_all("/".self::PATTERN_HOST_FUNCTION.
								"|".$patternInterfaceFunction.
								"|".self::PATTERN_ITEM_FUNCTION.
								"|".ZBX_PREG_EXPRESSION_USER_MACROS.
								"|\$([1-9])/", $trigger[$source], $matches, PREG_OFFSET_CAPTURE);

			for ($i = count($matches[0]) - 1; $i >= 0; $i--) {
				$matche = $matches[0][$i];

				$macrosValue = isset($macroValues[$triggerId][$matche[0]]) ? $macroValues[$triggerId][$matche[0]] : $matche[0];
				$trigger[$source] = substr_replace($trigger[$source], $macrosValue, $matche[1], strlen($matche[0]));
			}

			$triggers[$triggerId][$source] = $trigger[$source];
		}

		return $triggers;
	}

	/**
	 * Expand reference macros for trigger.
	 * If macro reference non existing value it expands to empty string.
	 *
	 * @param string $expression
	 * @param string $text
	 *
	 * @return string
	 */
	public function resolveTriggerReference($expression, $text) {
		for(getTriggerReference($expression, $text) as $key => $value) {
			$text = str_replace($key, $value, $text);
		}

		return $text;
	}

	/**
	 * Resolve functional item macros, for example, {{HOST.HOST1}:key.func(param)}.
	 *
	 * @param array  $data							list or hashmap of graphs
	 * @param type   $data[]["name"]				string in which macros should be resolved
	 * @param array  $data[]["items"]				list of graph items
	 * @param int    $data[]["items"][n]["hostid"]	graph n-th item corresponding host ID
	 * @param string $data[]["items"][n]["host"]	graph n-th item corresponding host name
	 *
	 * @return string	inputted data with resolved source field
	 */
	private function resolveGraph($data) {
		if (isTypeAvailable("graphFunctionalItem")) {
			$source = getSource();

			$strList = CArray.array();
			$itemsList = CArray.array();

			for($data as $graph) {
				$strList[] = $graph[$source];
				$itemsList[] = Nest.value($graph,"items").$();
			}

			$resolvedStrList = resolveGraphsFunctionalItemMacros($strList, $itemsList);
			$resolvedStr = reset($resolvedStrList);

			for($data as &$graph) {
				$graph[$source] = $resolvedStr;
				$resolvedStr = next($resolvedStrList);
			}
			unset($graph);
		}

		return $data;
	}

	/**
	 * Resolve functional macros, like {hostname:key.function(param)}.
	 * If macro can not be resolved it is replaced with UNRESOLVED_MACRO_STRING string i.e. \"*UNKNOWN*\".
	 *
	 * Supports function \"last\", \"min\", \"max\" and \"avg\".
	 * Supports seconds as parameters, except \"last\" function.
	 * Second parameter like {hostname:key.last(0,86400) and offsets like {hostname:key.last(#1)} are not supported.
	 * Supports postfixes s,m,h,d and w for parameter.
	 *
	 * @param array  $strList				list of string in which macros should be resolved
	 * @param array  $itemsList				list of	lists of graph items
	 * @param int    $items[n][m]["hostid"]	n-th graph m-th item corresponding host Id
	 * @param string $items[n][m]["host"]	n-th graph m-th item corresponding host name
	 *
	 * @return array	list of strings with macros replaced with corresponding values
	 */
	private function resolveGraphsFunctionalItemMacros($strList, $itemsList) {
		// retrieve all string macros and all host-key pairs
		$hostKeyPairs = CArray.array();
		$matchesList = CArray.array();
		$items = reset($itemsList);

		for($strList as $str) {
			// extract all macros into $matches - keys: macros, hosts, keys, functions and parameters are used
			// searches for macros, for example, \"{somehost:somekey[\"param[123]\"].min(10m)}\"
			preg_match_all("/(?P<macros>{".
				"(?P<hosts>(".ZBX_PREG_HOST_FORMAT."|({(".self::PATTERN_HOST_INTERNAL.")".self::PATTERN_MACRO_PARAM."}))):".
				"(?P<keys>".ZBX_PREG_ITEM_KEY_FORMAT.")\.".
				"(?P<functions>(last|max|min|avg))\(".
				"(?P<parameters>([0-9]+[".ZBX_TIME_SUFFIXES."]?)?)".
				"\)}{1})/Uux", $str, $matches, PREG_OFFSET_CAPTURE);

			if (!empty(Nest.value($matches,"hosts").$())) {
				for(Nest.value($matches,"hosts").$() as $i => $host) {
					$matches["hosts"][$i][0] = resolveGraphPositionalMacros($host[0], $items);

					if ($matches["hosts"][$i][0] !== UNRESOLVED_MACRO_STRING) {
						if (!isset($hostKeyPairs[$matches["hosts"][$i][0]])) {
							$hostKeyPairs[$matches["hosts"][$i][0]] = CArray.array();
						}

						$hostKeyPairs[$matches["hosts"][$i][0]][$matches["keys"][$i][0]] = 1;
					}
				}

				$matchesList[] = $matches;
				$items = next($itemsList);
			}
		}

		// stop, if no macros found
		if (empty($matchesList)) {
			return $strList;
		}

		// build item retrieval query from host-key pairs
		$query = "SELECT h.host,i.key_,i.itemid,i.value_type,i.units,i.valuemapid".
					" FROM items i, hosts h".
					" WHERE i.hostid=h.hostid AND (";
		for($hostKeyPairs as $host => $keys) {
			$query .= "(h.host=".zbx_dbstr($host)." AND i.key_ IN(";
			for($keys as $key => $val) {
				$query .= zbx_dbstr($key).",";
			}
			$query = substr($query, 0, -1).")) OR ";
		}
		$query = substr($query, 0, -4).")";

		// get necessary items for all graph strings
		$items = DBfetchArrayAssoc(DBselect($query), "itemid");

		$allowedItems = API.Item().get(CArray.array(
			"itemids" => array_keys($items),
			"webitems" => true,
			"output" => CArray.array("itemid", "value_type"),
			"preservekeys" => true
		));

		// map item data only for allowed items
		for($items as $item) {
			if (isset($allowedItems[$item["itemid"]])) {
				$hostKeyPairs[$item["host"]][$item["key_"]] = $item;
			}
		}

		// fetch history
		$history = Manager::History().getLast($items);

		// replace macros with their corresponding values in graph strings
		$matches = reset($matchesList);

		for($strList as &$str) {
			// iterate array backwards!
			$i = count(Nest.value($matches,"macros").$());

			while ($i--) {
				$host = $matches["hosts"][$i][0];
				$key = $matches["keys"][$i][0];
				$function = $matches["functions"][$i][0];
				$parameter = $matches["parameters"][$i][0];

				// host is real and item exists and has permissions
				if ($host !== UNRESOLVED_MACRO_STRING && is_array($hostKeyPairs[$host][$key])) {
					$item = $hostKeyPairs[$host][$key];

					// macro function is \"last\"
					if ($function == "last") {
						$value = isset($history[$item["itemid"]])
							? formatHistoryValue($history[$item["itemid"]][0]["value"], $item)
							: UNRESOLVED_MACRO_STRING;
					}
					// macro function is \"max\", \"min\" or \"avg\"
					else {
						$value = getItemFunctionalValue($item, $function, $parameter);
					}
				}
				// there is no item with given key in given host, or there is no permissions to that item
				else {
					$value = UNRESOLVED_MACRO_STRING;
				}

				$str = substr_replace($str, $value, $matches["macros"][$i][1], strlen($matches["macros"][$i][0]));
			}

			$matches = next($matchesList);
		}
		unset($str);

		return $strList;
	}

	/**
	 * Resolve positional macros, like {HOST.HOST2}.
	 * If macro can not be resolved it is replaced with UNRESOLVED_MACRO_STRING string i.e. \"*UNKNOWN*\"
	 * Supports HOST.HOST<1..9> macros.
	 *
	 * @param string	$str				string in which macros should be resolved
	 * @param array		$items				list of graph items
	 * @param int 		$items[n]["hostid"] graph n-th item corresponding host Id
	 * @param string	$items[n]["host"]   graph n-th item corresponding host name
	 *
	 * @return string	string with macros replaces with corresponding values
	 */
	private function resolveGraphPositionalMacros($str, $items) {
		// extract all macros into $matches
		preg_match_all("/{((".self::PATTERN_HOST_INTERNAL.")(".self::PATTERN_MACRO_PARAM."))\}/", $str, $matches);

		// match found groups if ever regexp should change
		Nest.value($matches,"macroType").$() = $matches[2];
		Nest.value($matches,"position").$() = $matches[3];

		// build structure of macros: $macroList["HOST.HOST"][2] = "host name";
		$macroList = CArray.array();

		// $matches[3] contains positions, e.g., "",1,2,2,3,...
		for(Nest.value($matches,"position").$() as $i => $position) {
			// take care of macro without positional index
			$posInItemList = ($position === "") ? 0 : $position - 1;

			// init array
			if (!isset($macroList[$matches["macroType"][$i]])) {
				$macroList[$matches["macroType"][$i]] = CArray.array();
			}

			// skip computing for duplicate macros
			if (isset($macroList[$matches["macroType"][$i]][$position])) {
				continue;
			}

			// positional index larger than item count, resolve to UNKNOWN
			if (!isset($items[$posInItemList])) {
				$macroList[$matches["macroType"][$i]][$position] = UNRESOLVED_MACRO_STRING;
				continue;
			}

			// retrieve macro replacement data
			switch ($matches["macroType"][$i]) {
				case "HOSTNAME":
				case "HOST.HOST":
					$macroList[$matches["macroType"][$i]][$position] = $items[$posInItemList]["host"];
					break;
			}
		}

		// replace macros with values in $str
		for($macroList as $macroType => $positions) {
			for($positions as $position => $replacement) {
				$str = str_replace("{".$macroType.$position."}", $replacement, $str);
			}
		}

		return $str;
	}

	/**
	 * Resolve item name macros to \"name_expanded\" field.
	 *
	 * @param array  $items
	 * @param string $items[n]["itemid"]
	 * @param string $items[n]["hostid"]
	 * @param string $items[n]["name"]
	 * @param string $items[n]["key_"]				item key (optional)
	 *												but is (mandatory) if macros exist and \"key_expanded\" is not present
	 * @param string $items[n]["key_expanded"]		expanded item key (optional)
	 *
	 * @return array
	 */
	public function resolveItemNames(array $items) {
		// define resolving fields
		for($items as &$item) {
			Nest.value($item,"name_expanded").$() = Nest.value($item,"name").$();
		}
		unset($item);

		$macros = $itemsWithReferenceMacros = $itemsWithUnResolvedKeys = CArray.array();

		// reference macros - $1..$9
		for($items as $key => $item) {
			$matchedMacros = findMacros(self::PATTERN_ITEM_NUMBER, CArray.array(Nest.value($item,"name_expanded").$()));

			if ($matchedMacros) {
				$macros[$key] = CArray.array("macros" => CArray.array());

				for($matchedMacros as $macro) {
					$macros[$key]["macros"][$macro] = null;
				}

				$itemsWithReferenceMacros[$key] = $item;
			}
		}

		if ($itemsWithReferenceMacros) {
			// resolve macros in item key
			for($itemsWithReferenceMacros as $key => $item) {
				if (!isset(Nest.value($item,"key_expanded").$())) {
					$itemsWithUnResolvedKeys[$key] = $item;
				}
			}

			if ($itemsWithUnResolvedKeys) {
				$itemsWithUnResolvedKeys = resolveItemKeys($itemsWithUnResolvedKeys);

				for($itemsWithUnResolvedKeys as $key => $item) {
					$itemsWithReferenceMacros[$key] = $item;
				}
			}

			// reference macros - $1..$9
			for($itemsWithReferenceMacros as $key => $item) {
				$itemKey = new CItemKey(Nest.value($item,"key_expanded").$());

				if ($itemKey.isValid()) {
					for($itemKey.getParameters() as $n => $keyParameter) {
						$paramNum = "$".++$n;

						if (array_key_exists($paramNum, $macros[$key]["macros"])) {
							$macros[$key]["macros"][$paramNum] = $keyParameter;
						}
					}
				}
			}
		}

		// user macros
		$userMacros = CArray.array();

		for($items as $item) {
			$matchedMacros = findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, CArray.array(Nest.value($item,"name_expanded").$()));

			if ($matchedMacros) {
				for($matchedMacros as $macro) {
					if (!isset($userMacros[$item["hostid"]])) {
						$userMacros[$item["hostid"]] = CArray.array(
							"hostids" => CArray.array(Nest.value($item,"hostid").$()),
							"macros" => CArray.array()
						);
					}

					$userMacros[$item["hostid"]]["macros"][$macro] = null;
				}
			}
		}

		if ($userMacros) {
			$userMacros = getUserMacros($userMacros);

			for($items as $key => $item) {
				if (isset($userMacros[$item["hostid"]])) {
					$macros[$key]["macros"] = isset($macros[$key])
						? zbx_array_merge($macros[$key]["macros"], $userMacros[$item["hostid"]]["macros"])
						: $userMacros[$item["hostid"]]["macros"];
				}
			}
		}

		// replace macros to value
		if ($macros) {
			for($macros as $key => $macroData) {
				$items[$key]["name_expanded"] = str_replace(
					array_keys(Nest.value($macroData,"macros").$()),
					array_values(Nest.value($macroData,"macros").$()),
					$items[$key]["name_expanded"]
				);
			}
		}

		return $items;
	}

	/**
	 * Resolve item key macros to \"key_expanded\" field.
	 *
	 * @param array  $items
	 * @param string $items[n]["itemid"]
	 * @param string $items[n]["hostid"]
	 * @param string $items[n]["key_"]
	 *
	 * @return array
	 */
	public function resolveItemKeys(array $items) {
		// define resolving field
		for($items as &$item) {
			Nest.value($item,"key_expanded").$() = Nest.value($item,"key_").$();
		}
		unset($item);

		$macros = $itemIds = CArray.array();

		// host, ip macros
		for($items as $key => $item) {
			$matchedMacros = findMacros(self::PATTERN_ITEM_MACROS, CArray.array(Nest.value($item,"key_expanded").$()));

			if ($matchedMacros) {
				$itemIds[$item["itemid"]] = Nest.value($item,"itemid").$();

				$macros[$key] = CArray.array(
					"itemid" => Nest.value($item,"itemid").$(),
					"macros" => CArray.array()
				);

				for($matchedMacros as $macro) {
					$macros[$key]["macros"][$macro] = null;
				}
			}
		}

		if ($macros) {
			$dbItems = API.Item().get(CArray.array(
				"itemids" => $itemIds,
				"selectInterfaces" => CArray.array("ip", "dns", "useip"),
				"selectHosts" => CArray.array("host", "name"),
				"webitems" => true,
				"output" => CArray.array("itemid"),
				"filter" => CArray.array("flags" => null),
				"preservekeys" => true
			));

			for($macros as $key => $macroData) {
				if (isset($dbItems[$macroData["itemid"]])) {
					$host = reset($dbItems[$macroData["itemid"]]["hosts"]);
					$interface = reset($dbItems[$macroData["itemid"]]["interfaces"]);

					// if item without interface or template item, resolve interface related macros to *UNKNOWN*
					if (!$interface) {
						$interface = CArray.array(
							"ip" => UNRESOLVED_MACRO_STRING,
							"dns" => UNRESOLVED_MACRO_STRING,
							"useip" => false
						);
					}

					for(Nest.value($macroData,"macros").$() as $macro => $value) {
						switch ($macro) {
							case "{HOST.NAME}":
								$macros[$key]["macros"][$macro] = Nest.value($host,"name").$();
								break;

							case "{HOST.HOST}":
							case "{HOSTNAME}": // deprecated
								$macros[$key]["macros"][$macro] = Nest.value($host,"host").$();
								break;

							case "{HOST.IP}":
							case "{IPADDRESS}": // deprecated
								$macros[$key]["macros"][$macro] = Nest.value($interface,"ip").$();
								break;

							case "{HOST.DNS}":
								$macros[$key]["macros"][$macro] = Nest.value($interface,"dns").$();
								break;

							case "{HOST.CONN}":
								$macros[$key]["macros"][$macro] = Nest.value($interface,"useip").$() ? Nest.value($interface,"ip").$() : Nest.value($interface,"dns").$();
								break;
						}
					}
				}

				unset($macros[$key]["itemid"]);
			}
		}

		// user macros
		$userMacros = CArray.array();

		for($items as $item) {
			$matchedMacros = findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, CArray.array(Nest.value($item,"key_expanded").$()));

			if ($matchedMacros) {
				for($matchedMacros as $macro) {
					if (!isset($userMacros[$item["hostid"]])) {
						$userMacros[$item["hostid"]] = CArray.array(
							"hostids" => CArray.array(Nest.value($item,"hostid").$()),
							"macros" => CArray.array()
						);
					}

					$userMacros[$item["hostid"]]["macros"][$macro] = null;
				}
			}
		}

		if ($userMacros) {
			$userMacros = getUserMacros($userMacros);

			for($items as $key => $item) {
				if (isset($userMacros[$item["hostid"]])) {
					$macros[$key]["macros"] = isset($macros[$key])
						? zbx_array_merge($macros[$key]["macros"], $userMacros[$item["hostid"]]["macros"])
						: $userMacros[$item["hostid"]]["macros"];
				}
			}
		}

		// replace macros to value
		if ($macros) {
			for($macros as $key => $macroData) {
				$items[$key]["key_expanded"] = str_replace(
					array_keys(Nest.value($macroData,"macros").$()),
					array_values(Nest.value($macroData,"macros").$()),
					$items[$key]["key_expanded"]
				);
			}
		}

		return $items;
	}

	/**
	 * Resolve function parameter macros to \"parameter_expanded\" field.
	 *
	 * @param array  $data
	 * @param string $data[n]["hostid"]
	 * @param string $data[n]["parameter"]
	 *
	 * @return array
	 */
	public function resolveFunctionParameters(array $data) {
		// define resolving field
		for($data as &$function) {
			Nest.value($function,"parameter_expanded").$() = Nest.value($function,"parameter").$();
		}
		unset($function);

		$macros = CArray.array();

		// user macros
		$userMacros = CArray.array();

		for($data as $function) {
			$matchedMacros = findMacros(ZBX_PREG_EXPRESSION_USER_MACROS, CArray.array(Nest.value($function,"parameter_expanded").$()));

			if ($matchedMacros) {
				for($matchedMacros as $macro) {
					if (!isset($userMacros[$function["hostid"]])) {
						$userMacros[$function["hostid"]] = CArray.array(
							"hostids" => CArray.array(Nest.value($function,"hostid").$()),
							"macros" => CArray.array()
						);
					}

					$userMacros[$function["hostid"]]["macros"][$macro] = null;
				}
			}
		}

		if ($userMacros) {
			$userMacros = getUserMacros($userMacros);

			for($data as $key => $function) {
				if (isset($userMacros[$function["hostid"]])) {
					$macros[$key]["macros"] = isset($macros[$key])
						? zbx_array_merge($macros[$key]["macros"], $userMacros[$function["hostid"]]["macros"])
						: $userMacros[$function["hostid"]]["macros"];
				}
			}
		}

		// replace macros to value
		if ($macros) {
			for($macros as $key => $macroData) {
				$data[$key]["parameter_expanded"] = str_replace(
					array_keys(Nest.value($macroData,"macros").$()),
					array_values(Nest.value($macroData,"macros").$()),
					$data[$key]["parameter_expanded"]
				);
			}
		}

		return $data;
	}
}
