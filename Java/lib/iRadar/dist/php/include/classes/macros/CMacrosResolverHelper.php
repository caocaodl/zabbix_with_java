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
 * Helper class that simplifies working with CMacrosResolver class.
 */
class CMacrosResolverHelper {

	/**
	 * @var CMacrosResolver
	 */
	private static $macrosResolver;

	/**
	 * Resolve macros.
	 *
	 * @static
	 *
	 * @param array $options
	 *
	 * @return array
	 */
	public static function resolve(array $options) {
		self::init();

		return self::$macrosResolver.resolve($options);
	}

	/**
	 * Resolve macros in http test name.
	 *
	 * @static
	 *
	 * @param int    $hostId
	 * @param string $name
	 *
	 * @return string
	 */
	public static function resolveHttpTestName($hostId, $name) {
		self::init();

		$macros = self::$macrosResolver.resolve(CArray.array(
			"config" => "httpTestName",
			"data" => CArray.array($hostId => CArray.array($name))
		));

		return $macros[$hostId][0];
	}

	/**
	 * Resolve macros in host interfaces.
	 *
	 * @static
	 *
	 * @param array  $interfaces
	 * @param string $interfaces[n]["hostid"]
	 * @param string $interfaces[n]["type"]
	 * @param string $interfaces[n]["main"]
	 * @param string $interfaces[n]["ip"]
	 * @param string $interfaces[n]["dns"]
	 *
	 * @return array
	 */
	public static function resolveHostInterfaces(array $interfaces) {
		self::init();

		// agent primary ip and dns
		$data = CArray.array();
		for($interfaces as $interface) {
			if (Nest.value($interface,"type").$() == INTERFACE_TYPE_AGENT && Nest.value($interface,"main").$() == INTERFACE_PRIMARY) {
				$data[$interface["hostid"]][] = Nest.value($interface,"ip").$();
				$data[$interface["hostid"]][] = Nest.value($interface,"dns").$();
			}
		}

		$resolvedData = self::$macrosResolver.resolve(CArray.array(
			"config" => "hostInterfaceIpDnsAgentPrimary",
			"data" => $data
		));

		for($resolvedData as $hostId => $texts) {
			$n = 0;

			for($interfaces as &$interface) {
				if (Nest.value($interface,"type").$() == INTERFACE_TYPE_AGENT && Nest.value($interface,"main").$() == INTERFACE_PRIMARY
						&& Nest.value($interface,"hostid").$() == $hostId) {
					Nest.value($interface,"ip").$() = $texts[$n];
					$n++;
					Nest.value($interface,"dns").$() = $texts[$n];
					$n++;
				}
			}
			unset($interface);
		}

		// others ip and dns
		$data = CArray.array();
		for($interfaces as $interface) {
			if (!(Nest.value($interface,"type").$() == INTERFACE_TYPE_AGENT && Nest.value($interface,"main").$() == INTERFACE_PRIMARY)) {
				$data[$interface["hostid"]][] = Nest.value($interface,"ip").$();
				$data[$interface["hostid"]][] = Nest.value($interface,"dns").$();
			}
		}

		$resolvedData = self::$macrosResolver.resolve(CArray.array(
			"config" => "hostInterfaceIpDns",
			"data" => $data
		));

		for($resolvedData as $hostId => $texts) {
			$n = 0;

			for($interfaces as &$interface) {
				if (!(Nest.value($interface,"type").$() == INTERFACE_TYPE_AGENT && Nest.value($interface,"main").$() == INTERFACE_PRIMARY)
						&& Nest.value($interface,"hostid").$() == $hostId) {
					Nest.value($interface,"ip").$() = $texts[$n];
					$n++;
					Nest.value($interface,"dns").$() = $texts[$n];
					$n++;
				}
			}
			unset($interface);
		}

		// port
		$data = CArray.array();
		for($interfaces as $interface) {
			$data[$interface["hostid"]][] = Nest.value($interface,"port").$();
		}

		$resolvedData = self::$macrosResolver.resolve(CArray.array(
			"config" => "hostInterfacePort",
			"data" => $data
		));

		for($resolvedData as $hostId => $texts) {
			$n = 0;

			for($interfaces as &$interface) {
				if (Nest.value($interface,"hostid").$() == $hostId) {
					Nest.value($interface,"port").$() = $texts[$n];
					$n++;
				}
			}
			unset($interface);
		}

		return $interfaces;
	}

	/**
	 * Resolve macros in trigger name.
	 *
	 * @static
	 *
	 * @param array $trigger
	 *
	 * @return string
	 */
	public static function resolveTriggerName(array $trigger) {
		$macros = self::resolveTriggerNames(CArray.array($trigger));
		$macros = reset($macros);

		return Nest.value($macros,"description").$();
	}

	/**
	 * Resolve macros in trigger names.
	 *
	 * @static
	 *
	 * @param array $triggers
	 *
	 * @return array
	 */
	public static function resolveTriggerNames(array $triggers) {
		self::init();

		return self::$macrosResolver.resolve(CArray.array(
			"config" => "triggerName",
			"data" => zbx_toHash($triggers, "triggerid")
		));
	}

	/**
	 * Resolve macros in trigger description.
	 *
	 * @static
	 *
	 * @param array $trigger
	 *
	 * @return string
	 */
	public static function resolveTriggerDescription(array $trigger) {
		$macros = self::resolveTriggerDescriptions(CArray.array($trigger));
		$macros = reset($macros);

		return Nest.value($macros,"comments").$();
	}

	/**
	 * Resolve macros in trigger descriptions.
	 *
	 * @static
	 *
	 * @param array $triggers
	 *
	 * @return array
	 */
	public static function resolveTriggerDescriptions(array $triggers) {
		self::init();

		return self::$macrosResolver.resolve(CArray.array(
			"config" => "triggerDescription",
			"data" => zbx_toHash($triggers, "triggerid")
		));
	}

	/**
	 * Get trigger by id and resolve macros in trigger name.
	 *
	 * @static
	 *
	 * @param int $triggerId
	 *
	 * @return string
	 */
	public static function resolveTriggerNameById($triggerId) {
		$macros = self::resolveTriggerNameByIds(CArray.array($triggerId));
		$macros = reset($macros);

		return Nest.value($macros,"description").$();
	}

	/**
	 * Get triggers by ids and resolve macros in trigger names.
	 *
	 * @static
	 *
	 * @param array $triggerIds
	 *
	 * @return array
	 */
	public static function resolveTriggerNameByIds(array $triggerIds) {
		self::init();

		$triggers = DBfetchArray(DBselect(
			"SELECT DISTINCT t.description,t.expression,t.triggerid".
			" FROM triggers t".
			" WHERE ".dbConditionInt("t.triggerid", $triggerIds)
		));

		return self::$macrosResolver.resolve(CArray.array(
			"config" => "triggerName",
			"data" => zbx_toHash($triggers, "triggerid")
		));
	}

	/**
	 * Resolve macros in trigger reference.
	 *
	 * @static
	 *
	 * @param string $expression
	 * @param string $text
	 *
	 * @return string
	 */
	public static function resolveTriggerReference($expression, $text) {
		self::init();

		return self::$macrosResolver.resolveTriggerReference($expression, $text);
	}

	/**
	 * Resolve user macros in trigger expression.
	 *
	 * @static
	 *
	 * @param array $trigger
	 * @param array $trigger["triggerid"]
	 * @param array $trigger["expression"]
	 *
	 * @return string
	 */
	public static function resolveTriggerExpressionUserMacro(array $trigger) {
		if (zbx_empty(Nest.value($trigger,"expression").$())) {
			return Nest.value($trigger,"expression").$();
		}

		self::init();

		$triggers = self::$macrosResolver.resolve(CArray.array(
			"config" => "triggerExpressionUser",
			"data" => zbx_toHash(CArray.array($trigger), "triggerid")
		));
		$trigger = reset($triggers);

		return Nest.value($trigger,"expression").$();
	}

	/**
	 * Resolve macros in event description.
	 *
	 * @static
	 *
	 * @param array $event
	 *
	 * @return string
	 */
	public static function resolveEventDescription(array $event) {
		self::init();

		$macros = self::$macrosResolver.resolve(CArray.array(
			"config" => "eventDescription",
			"data" => CArray.array(Nest.value($event,"triggerid").$() => $event)
		));
		$macros = reset($macros);

		return Nest.value($macros,"description").$();
	}

	/**
	 * Resolve positional macros and functional item macros, for example, {{HOST.HOST1}:key.func(param)}.
	 *
	 * @static
	 *
	 * @param type   $name					string in which macros should be resolved
	 * @param array  $items					list of graph items
	 * @param int    $items[n]["hostid"]	graph n-th item corresponding host Id
	 * @param string $items[n]["host"]		graph n-th item corresponding host name
	 *
	 * @return string	string with macros replaced with corresponding values
	 */
	public static function resolveGraphName($name, array $items) {
		self::init();

		$graph = self::$macrosResolver.resolve(CArray.array(
			"config" => "graphName",
			"data" => CArray.array(CArray.array("name" => $name, "items" => $items))
		));
		$graph = reset($graph);

		return Nest.value($graph,"name").$();
	}

	/**
	 * Resolve positional macros and functional item macros, for example, {{HOST.HOST1}:key.func(param)}.
	 * ! if same graph will be passed more than once only name for first entry will be resolved.
	 *
	 * @static
	 *
	 * @param array  $data					list or hashmap of graphs
	 * @param int    $data[n]["graphid"]	id of graph
	 * @param string $data[n]["name"]		name of graph
	 *
	 * @return array	inputted data with resolved names
	 */
	public static function resolveGraphNameByIds(array $data) {
		self::init();

		$graphIds = CArray.array();
		$graphMap = CArray.array();
		for($data as $graph) {
			// skip graphs without macros
			if (strpos(Nest.value($graph,"name").$(), "{") !== false) {
				$graphMap[$graph["graphid"]] = CArray.array(
					"graphid" => Nest.value($graph,"graphid").$(),
					"name" => Nest.value($graph,"name").$(),
					"items" => CArray.array()
				);
				$graphIds[$graph["graphid"]] = Nest.value($graph,"graphid").$();
			}
		}

		$items = DBfetchArray(DBselect(
			"SELECT i.hostid,gi.graphid,h.host".
			" FROM graphs_items gi,items i,hosts h".
			" WHERE gi.itemid=i.itemid".
				" AND i.hostid=h.hostid".
				" AND ".dbConditionInt("gi.graphid", $graphIds).
			" ORDER BY gi.sortorder"
		));

		for($items as $item) {
			$graphMap[$item["graphid"]]["items"][] = CArray.array("hostid" => Nest.value($item,"hostid").$(), "host" => Nest.value($item,"host").$());
		}

		$graphMap = self::$macrosResolver.resolve(CArray.array(
			"config" => "graphName",
			"data" => $graphMap
		));

		$resolvedGraph = reset($graphMap);
		for($data as &$graph) {
			if (Nest.value($graph,"graphid").$() === Nest.value($resolvedGraph,"graphid").$()) {
				Nest.value($graph,"name").$() = Nest.value($resolvedGraph,"name").$();
				$resolvedGraph = next($graphMap);
			}
		}
		unset($graph);

		return $data;
	}

	/**
	 * Resolve item name macros to \"name_expanded\" field.
	 *
	 * @static
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
	public static function resolveItemNames(array $items) {
		self::init();

		return self::$macrosResolver.resolveItemNames($items);
	}

	/**
	 * Resolve item key macros to \"key_expanded\" field.
	 *
	 * @static
	 *
	 * @param array  $items
	 * @param string $items[n]["itemid"]
	 * @param string $items[n]["hostid"]
	 * @param string $items[n]["key_"]
	 *
	 * @return array
	 */
	public static function resolveItemKeys(array $items) {
		self::init();

		return self::$macrosResolver.resolveItemKeys($items);
	}

	/**
	 * Resolve function parameter macros to \"parameter_expanded\" field.
	 *
	 * @static
	 *
	 * @param array  $data
	 * @param string $data[n]["hostid"]
	 * @param string $data[n]["parameter"]
	 *
	 * @return array
	 */
	public static function resolveFunctionParameters(array $data) {
		self::init();

		return self::$macrosResolver.resolveFunctionParameters($data);
	}

	/**
	 * Create CMacrosResolver object and store in static variable.
	 *
	 * @static
	 */
	private static function init() {
		if (self::$macrosResolver === null) {
			self::$macrosResolver = new CMacrosResolver();
		}
	}
}
