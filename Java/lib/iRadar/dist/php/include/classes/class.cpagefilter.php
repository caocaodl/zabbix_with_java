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
 * @property string $groupid
 * @property string $hostid
 * @property string $triggerid
 * @property string $graphid
 * @property string $druleid
 * @property string $severityMin
 * @property array  $groups
 * @property array  $hosts
 * @property array  $graphs
 * @property array  $triggers
 * @property array  $drules
 * @property bool   $groupsSelected
 * @property bool   $groupsAll
 * @property bool   $hostsSelected
 * @property bool   $hostsAll
 * @property bool   $graphsSelected
 * @property bool   $triggersSelected
 * @property bool   $drulesSelected
 * @property bool   $drulesAll
 */
class CPageFilter {

	const GROUP_LATEST_IDX = "web.latest.groupid";
	const HOST_LATEST_IDX = "web.latest.hostid";
	const GRAPH_LATEST_IDX = "web.latest.graphid";
	const TRIGGER_LATEST_IDX = "web.latest.triggerid";
	const DRULE_LATEST_IDX = "web.latest.druleid";

	/**
	 * Configuration options.
	 *
	 * @var array
	 */
	protected $config = CArray.array(
		// whether to allow all nodes
		"all_nodes" => null,

		// select the latest object viewed by the user on any page
		"select_latest" => null,

		// reset the remembered values if the remember first dropdown entry function is disabled
		"DDReset" => null,

		// if set to true selections will be remembered for each file separately,
		// if set to false - for each main menu section (monitoring, inventory, configuration etc.)
		"individual" => null,

		// if set to true and the remembered object is missing from the selection, sets the filter to the first
		// available object. If set to false, the selection will remain empty.
		"popupDD" => null,

		// Force the filter to select the given objects.
		// works only if the host given in "hostid" belongs to that group or "hostid" is not set
		"groupid" => null,

		// works only if a host group is selected or the host group filter value is set to "all"
		"hostid" => null,

		// works only if a host is selected or the host filter value is set to "all"
		"graphid" => null,

		// works only if a specific host has been selected, will NOT work if the host filter is set to "all"
		"triggerid" => null,
		"druleid" => null,

		// API parameters to be used to retrieve filter objects
		"groups" => null,
		"hosts" => null,
		"graphs" => null,
		"triggers" => null,
		"drules" => null
	);

	/**
	 * Objects present in the filter.
	 *
	 * @var array
	 */
	protected $data = CArray.array(
		"groups" => null,
		"hosts" => null,
		"graphs" => null,
		"triggers" => null,
		"drules" => null
	);

	/**
	 * Selected objects IDs.
	 *
	 * @var array
	 */
	protected $ids = CArray.array(
		"groupid" => null,
		"hostid" => null,
		"triggerid" => null,
		"graphid" => null,
		"druleid" => null,
		"severityMin" => null
	);

	/**
	 * Contains information about the selected values.
	 * The "*Selected" value is set to true if a specific object is chosen or the corresponding filter is set to "All"
	 * and contains objects.
	 * The "*All" value is set to true if the corresponding filter is set to "All" and contains objects.
	 *
	 * @var array
	 */
	protected $isSelected = CArray.array(
		"groupsSelected" => null,
		"groupsAll" => null,
		"hostsSelected" => null,
		"hostsAll" => null,
		"graphsSelected" => null,
		"triggersSelected" => null,
		"drulesSelected" => null,
		"drulesAll" => null
	);

	/**
	 * User profile keys to be used when remembering the selected values.
	 *
	 * @see the "individual" option for more info.
	 *
	 * @var array
	 */
	private $_profileIdx = CArray.array(
		"groupid" => null,
		"hostid" => null,
		"triggerid" => null,
		"graphid" => null,
		"druleid" => null,
		"severityMin" => null
	);

	/**
	 * IDs of specific objects to be selected.
	 *
	 * @var array
	 */
	private $_profileIds = CArray.array(
		"groupid" => null,
		"hostid" => null,
		"triggerid" => null,
		"graphid" => null,
		"druleid" => null,
		"severityMin" => null
	);

	/**
	 * Request ids.
	 *
	 * @var array
	 */
	private $_requestIds = CArray.array();

	/**
	 * Get value from $data, $ids or $isSelected arrays.
	 * Search occurs in mentioned above order.
	 *
	 * @param string $name
	 *
	 * @return mixed
	 */
	public function __get($name) {
		if (isset(data[$name])) {
			return data[$name];
		}
		elseif (isset(ids[$name])) {
			return ids[$name];
		}
		elseif (isset(isSelected[$name])) {
			return isSelected[$name];
		}
		else {
			trigger_error(_s("Try to read inaccessible property \"%s\".", get_class($this).".".$name), E_USER_WARNING);

			return false;
		}
	}

	/**
	 * Initialize filter features.
	 * Supported: Host groups, Hosts, Triggers, Graphs, Applications, Discovery rules, Minimum trigger severities.
	 *
	 * @param array  $options
	 * @param array  $options["config"]
	 * @param bool   $options["config"]["select_latest"]
	 * @param bool   $options["config"]["popupDD"]
	 * @param bool   $options["config"]["individual"]
	 * @param bool   $options["config"]["allow_all"]
	 * @param bool   $options["config"]["deny_all"]
	 * @param array  $options["config"]["DDFirstLabels"]
	 * @param array  $options["hosts"]
	 * @param string $options["hostid"]
	 * @param array  $options["groups"]
	 * @param string $options["groupid"]
	 * @param array  $options["graphs"]
	 * @param string $options["graphid"]
	 * @param array  $options["triggers"]
	 * @param string $options["triggerid"]
	 * @param array  $options["drules"]
	 * @param string $options["druleid"]
	 * @param array  $options["applications"]
	 * @param string $options["application"]
	 * @param array  $options["severitiesMin"]
	 * @param int    $options["severitiesMin"]["default"]
	 * @param string $options["severitiesMin"]["mapId"]
	 * @param string $options["severityMin"]
	 */
	public function __construct(array $options = CArray.array()) {
		global $ZBX_WITH_ALL_NODES;

		Nest.value(config,"all_nodes").$() = $ZBX_WITH_ALL_NODES;
		Nest.value(config,"select_latest").$() = isset(Nest.value($options,"config","select_latest").$());
		Nest.value(config,"DDReset").$() = get_request("ddreset", null);
		Nest.value(config,"popupDD").$() = isset(Nest.value($options,"config","popupDD").$());

		$config = select_config();

		// individual remember selections per page (not for menu)
		Nest.value(config,"individual").$() = false;
		if (isset(Nest.value($options,"config","individual").$()) && !is_null(Nest.value($options,"config","individual").$())) {
			Nest.value(config,"individual").$() = true;
		}

		// dropdown
		Nest.value(config,"DDRemember").$() = Nest.value($config,"dropdown_first_remember").$();
		if (isset(Nest.value($options,"config","allow_all").$())) {
			Nest.value(config,"DDFirst").$() = ZBX_DROPDOWN_FIRST_ALL;
		}
		elseif (isset(Nest.value($options,"config","deny_all").$())) {
			Nest.value(config,"DDFirst").$() = ZBX_DROPDOWN_FIRST_NONE;
		}
		else {
			Nest.value(config,"DDFirst").$() = Nest.value($config,"dropdown_first_entry").$();
		}

		// profiles
		_getProfiles($options);

		if (!isset(Nest.value($options,"groupid").$(), Nest.value($options,"hostid").$())) {
			if (isset(Nest.value($options,"graphid").$())) {
				_updateByGraph($options);
			}
		}

		// groups
		if (isset(Nest.value($options,"groups").$())) {
			_initGroups(Nest.value($options,"groupid").$(), Nest.value($options,"groups").$(), isset(Nest.value($options,"hostid").$()) ? Nest.value($options,"hostid").$() : null);
		}

		// hosts
		if (isset(Nest.value($options,"hosts").$())) {
			_initHosts(Nest.value($options,"hostid").$(), Nest.value($options,"hosts").$());
		}

		// graphs
		if (isset(Nest.value($options,"graphs").$())) {
			_initGraphs(Nest.value($options,"graphid").$(), Nest.value($options,"graphs").$());
		}

		// triggers
		if (isset(Nest.value($options,"triggers").$())) {
			_initTriggers(Nest.value($options,"triggerid").$(), Nest.value($options,"triggers").$());
		}

		// drules
		if (isset(Nest.value($options,"drules").$())) {
			_initDiscoveries(Nest.value($options,"druleid").$(), Nest.value($options,"drules").$());
		}

		// applications
		if (isset(Nest.value($options,"applications").$())) {
			_initApplications(Nest.value($options,"application").$(), Nest.value($options,"applications").$());
		}

		// severities min
		if (isset(Nest.value($options,"severitiesMin").$())) {
			_initSeveritiesMin(Nest.value($options,"severityMin").$(), Nest.value($options,"severitiesMin").$());
		}
	}

	/**
	 * Retrieve objects stored in the user profile.
	 * If the "select_latest" option is used, the IDs will be loaded from the web.latest.objectid profile values,
	 * otherwise - from the web.*.objectid field, depending on the use of the "individial" option.
	 * If the "DDReset" option is used, IDs will be reset to zeroes.
	 * The method also sets the scope for remembering the selected values, see the "individual" option for more info.
	 *
	 * @param array $options
	 */
	private function _getProfiles(array $options) {
		global $page;

		$profileSection = Nest.value(config,"individual").$() ? Nest.value($page,"file").$() : Nest.value($page,"menu").$();

		Nest.value(_profileIdx,"groups").$() = "web.".$profileSection.".groupid";
		Nest.value(_profileIdx,"hosts").$() = "web.".$profileSection.".hostid";
		Nest.value(_profileIdx,"graphs").$() = "web.".$profileSection.".graphid";
		Nest.value(_profileIdx,"triggers").$() = "web.".$profileSection.".triggerid";
		Nest.value(_profileIdx,"drules").$() = "web.".$profileSection.".druleid";
		Nest.value(_profileIdx,"application").$() = "web.".$profileSection.".application";
		Nest.value(_profileIdx,"severityMin").$() = "web.maps.severity_min";

		if (Nest.value(config,"select_latest").$()) {
			Nest.value(_profileIds,"groupid").$() = CProfile::get(self::GROUP_LATEST_IDX);
			Nest.value(_profileIds,"hostid").$() = CProfile::get(self::HOST_LATEST_IDX);
			Nest.value(_profileIds,"graphid").$() = CProfile::get(self::GRAPH_LATEST_IDX);
			Nest.value(_profileIds,"triggerid").$() = null;
			Nest.value(_profileIds,"druleid").$() = CProfile::get(self::DRULE_LATEST_IDX);
			Nest.value(_profileIds,"application").$() = "";
			Nest.value(_profileIds,"severityMin").$() = null;
		}
		elseif (Nest.value(config,"DDReset").$() && !Nest.value(config,"DDRemember").$()) {
			Nest.value(_profileIds,"groupid").$() = 0;
			Nest.value(_profileIds,"hostid").$() = 0;
			Nest.value(_profileIds,"graphid").$() = 0;
			Nest.value(_profileIds,"triggerid").$() = 0;
			Nest.value(_profileIds,"druleid").$() = 0;
			Nest.value(_profileIds,"application").$() = "";
			Nest.value(_profileIds,"severityMin").$() = null;
		}
		else {
			Nest.value(_profileIds,"groupid").$() = CProfile::get(Nest.value(_profileIdx,"groups").$());
			Nest.value(_profileIds,"hostid").$() = CProfile::get(Nest.value(_profileIdx,"hosts").$());
			Nest.value(_profileIds,"graphid").$() = CProfile::get(Nest.value(_profileIdx,"graphs").$());
			Nest.value(_profileIds,"triggerid").$() = null;
			Nest.value(_profileIds,"druleid").$() = CProfile::get(Nest.value(_profileIdx,"drules").$());
			Nest.value(_profileIds,"application").$() = CProfile::get(Nest.value(_profileIdx,"application").$());

			// minimum severity
			$mapId = isset(Nest.value($options,"severitiesMin","mapId").$()) ? Nest.value($options,"severitiesMin","mapId").$() : null;
			Nest.value(_profileIds,"severityMin").$() = CProfile::get(Nest.value(_profileIdx,"severityMin").$(), null, $mapId);
		}

		Nest.value(_requestIds,"groupid").$() = isset(Nest.value($options,"groupid").$()) ? Nest.value($options,"groupid").$() : null;
		Nest.value(_requestIds,"hostid").$() = isset(Nest.value($options,"hostid").$()) ? Nest.value($options,"hostid").$() : null;
		Nest.value(_requestIds,"graphid").$() = isset(Nest.value($options,"graphid").$()) ? Nest.value($options,"graphid").$() : null;
		Nest.value(_requestIds,"triggerid").$() = isset(Nest.value($options,"triggerid").$()) ? Nest.value($options,"triggerid").$() : null;
		Nest.value(_requestIds,"druleid").$() = isset(Nest.value($options,"druleid").$()) ? Nest.value($options,"druleid").$() : null;
		Nest.value(_requestIds,"application").$() = isset(Nest.value($options,"application").$()) ? Nest.value($options,"application").$() : null;
		Nest.value(_requestIds,"severityMin").$() = isset(Nest.value($options,"severityMin").$()) ? Nest.value($options,"severityMin").$() : null;
	}

	private function _updateByGraph(array &$options) {
		$graphs = API.Graph().get(CArray.array(
			"graphids" => Nest.value($options,"graphid").$(),
			"output" => API_OUTPUT_EXTEND,
			"selectHosts" => API_OUTPUT_REFER,
			"selectTemplates" => API_OUTPUT_REFER,
			"selectGroups" => API_OUTPUT_REFER
		));

		if ($graph = reset($graphs)) {
			$groups = zbx_toHash(Nest.value($graph,"groups").$(), "groupid");
			$hosts = zbx_toHash(Nest.value($graph,"hosts").$(), "hostid");
			$templates = zbx_toHash(Nest.value($graph,"templates").$(), "templateid");

			if (isset($groups[_profileIds["groupid"]])) {
				Nest.value($options,"groupid").$() = Nest.value(_profileIds,"groupid").$();
			}
			else {
				$groupids = array_keys($groups);
				Nest.value($options,"groupid").$() = reset($groupids);
			}

			if (isset($hosts[_profileIds["hostid"]])) {
				Nest.value($options,"hostid").$() = Nest.value(_profileIds,"hostid").$();
			}
			else {
				$hostids = array_keys($hosts);
				Nest.value($options,"hostid").$() = reset($hostids);
			}

			if (is_null(Nest.value($options,"hostid").$())) {
				if (isset($templates[_profileIds["hostid"]])) {
					Nest.value($options,"hostid").$() = Nest.value(_profileIds,"hostid").$();
				}
				else {
					$templateids = array_keys($templates);
					Nest.value($options,"hostid").$() = reset($templateids);
				}
			}
		}
	}

	/**
	 * Load available host groups, choose the selected host group and remember the selection.
	 * If the host given in the "hostid" option does not belong to the selected host group, the selected host group
	 * will be reset to 0.
	 *
	 * @param int   $groupid
	 * @param array $options
	 * @param int   $hostid
	 */
	private function _initGroups($groupid, array $options, $hostid) {
		$def_options = CArray.array(
			"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
			"output" => CArray.array("groupid", "name")
		);
		$options = zbx_array_merge($def_options, $options);
		$groups = API.HostGroup().get($options);
		order_result($groups, "name");

		Nest.value(data,"groups").$() = CArray.array();
		for($groups as $group) {
			data["groups"][$group["groupid"]] = $group;
		}

		// select remembered selection
		if (is_null($groupid) && Nest.value(_profileIds,"groupid").$()) {
			// set group only if host is in group or hostid is not set
			if ($hostid) {
				$host = API.Host().get(CArray.array(
					"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
					"output" => CArray.array("hostid"),
					"hostids" => $hostid,
					"groupids" => _profileIds["groupid"]
				));
			}
			if (!$hostid || !empty($host)) {
				$groupid = Nest.value(_profileIds,"groupid").$();
			}
		}

		// nonexisting or unset $groupid
		if ((!isset(data["groups"][$groupid]) && $groupid > 0) || is_null($groupid)) {
			// for popup select first group in the list
			if (Nest.value(config,"popupDD").$() && !empty(Nest.value(data,"groups").$())) {
				reset(Nest.value(data,"groups").$());
				$groupid = key(Nest.value(data,"groups").$());
			}
			// otherwise groupid = 0 for "Dropdown first entry" option ALL or NONE
			else {
				$groupid = 0;
			}
		}

		CProfile::update(Nest.value(_profileIdx,"groups").$(), $groupid, PROFILE_TYPE_ID);
		CProfile::update(self::GROUP_LATEST_IDX, $groupid, PROFILE_TYPE_ID);

		Nest.value(isSelected,"groupsSelected").$() = (Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"groups").$())) || $groupid > 0;
		Nest.value(isSelected,"groupsAll").$() = Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"groups").$()) && $groupid == 0;
		Nest.value(ids,"groupid").$() = $groupid;
	}

	/**
	 * Load available hosts, choose the selected host and remember the selection.
	 * If no host group is selected, reset the selected host to 0.
	 *
	 * @param int    $hostId
	 * @param array  $options
	 * @param string $options["DDFirstLabel"]
	 */
	private function _initHosts($hostId, array $options) {
		Nest.value(data,"hosts").$() = CArray.array();

		if (isset(Nest.value($options,"DDFirstLabel").$())) {
			Nest.value(config,"DDFirstLabels","hosts").$() = Nest.value($options,"DDFirstLabel").$();

			unset(Nest.value($options,"DDFirstLabel").$());
		}

		if (!groupsSelected) {
			$hostId = 0;
		}
		else {
			$defaultOptions = CArray.array(
				"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
				"output" => CArray.array("hostid", "name", "status"),
				"groupids" => (groupid > 0) ? groupid : null
			);
			$hosts = API.Host().get(zbx_array_merge($defaultOptions, $options));

			if ($hosts) {
				order_result($hosts, "name");

				for($hosts as $host) {
					data["hosts"][$host["hostid"]] = $host;
				}
			}

			// select remembered selection
			if (is_null($hostId) && Nest.value(_profileIds,"hostid").$()) {
				$hostId = Nest.value(_profileIds,"hostid").$();
			}

			// nonexisting or unset $hostid
			if ((!isset(data["hosts"][$hostId]) && $hostId > 0) || is_null($hostId)) {
				// for popup select first host in the list
				if (Nest.value(config,"popupDD").$() && !empty(Nest.value(data,"hosts").$())) {
					reset(Nest.value(data,"hosts").$());
					$hostId = key(Nest.value(data,"hosts").$());
				}
				// otherwise hostid = 0 for "Dropdown first entry" option ALL or NONE
				else {
					$hostId = 0;
				}
			}
		}

		if (!is_null(Nest.value(_requestIds,"hostid").$())) {
			CProfile::update(Nest.value(_profileIdx,"hosts").$(), $hostId, PROFILE_TYPE_ID);
			CProfile::update(self::HOST_LATEST_IDX, $hostId, PROFILE_TYPE_ID);
		}

		Nest.value(isSelected,"hostsSelected").$() = ((Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"hosts").$())) || $hostId > 0);
		Nest.value(isSelected,"hostsAll").$() = (Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"hosts").$()) && $hostId == 0);
		Nest.value(ids,"hostid").$() = $hostId;
	}

	/**
	 * Load available graphs, choose the selected graph and remember the selection.
	 * If no host is selected, reset the selected graph to 0.
	 *
	 * @param int   $graphid
	 * @param array $options
	 */
	private function _initGraphs($graphid, array $options) {
		Nest.value(data,"graphs").$() = CArray.array();

		if (!hostsSelected) {
			$graphid = 0;
		}
		else {
			$def_ptions = CArray.array(
				"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
				"output" => CArray.array("graphid", "name"),
				"groupids" => (groupid > 0 && hostid == 0) ? groupid : null,
				"hostids" => (hostid > 0) ? hostid : null,
				"expandName" => true
			);
			$options = zbx_array_merge($def_ptions, $options);
			$graphs = API.Graph().get($options);
			order_result($graphs, "name");

			for($graphs as $graph) {
				data["graphs"][$graph["graphid"]] = $graph;
			}

			// no graphid provided
			if (is_null($graphid)) {
				// if there is one saved in profile, let's take it from there
				$graphid = is_null(Nest.value(_profileIds,"graphid").$()) ? 0 : Nest.value(_profileIds,"graphid").$();
			}

			// if there is no graph with given id in selected host
			if ($graphid > 0 && !isset(data["graphs"][$graphid])) {
				// then let's take a look how the desired graph is named
				$options = CArray.array(
					"output" => CArray.array("name"),
					"graphids" => CArray.array($graphid)
				);
				$selectedGraphInfo = API.Graph().get($options);
				$selectedGraphInfo = reset($selectedGraphInfo);
				$graphid = 0;

				// if there is a graph with the same name on new host, why not show it then?
				for(Nest.value(data,"graphs").$() as $gid => $graph) {
					if (Nest.value($graph,"name").$() === Nest.value($selectedGraphInfo,"name").$()) {
						$graphid = $gid;
						break;
					}
				}
			}
		}

		if (!is_null(Nest.value(_requestIds,"graphid").$())) {
			CProfile::update(Nest.value(_profileIdx,"graphs").$(), $graphid, PROFILE_TYPE_ID);
			CProfile::update(self::GRAPH_LATEST_IDX, $graphid, PROFILE_TYPE_ID);
		}
		Nest.value(isSelected,"graphsSelected").$() = $graphid > 0;
		Nest.value(ids,"graphid").$() = $graphid;
	}

	/**
	 * Load available triggers, choose the selected trigger and remember the selection.
	 * If no host is elected, or the host selection is set to "All", reset the selected trigger to 0.
	 *
	 * @param int   $triggerid
	 * @param array $options
	 */
	private function _initTriggers($triggerid, array $options) {
		Nest.value(data,"triggers").$() = CArray.array();

		if (!hostsSelected || hostsAll) {
			$triggerid = 0;
		}
		else {
			$def_ptions = CArray.array(
				"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
				"output" => CArray.array("triggerid", "description"),
				"groupids" => (groupid > 0 && hostid == 0) ? groupid : null,
				"hostids" => (hostid > 0) ? hostid : null
			);
			$options = zbx_array_merge($def_ptions, $options);
			$triggers = API.Trigger().get($options);
			order_result($triggers, "description");

			for($triggers as $trigger) {
				data["triggers"][$trigger["triggerid"]] = $trigger;
			}

			if (is_null($triggerid)) {
				$triggerid = Nest.value(_profileIds,"triggerid").$();
			}
			$triggerid = isset(data["triggers"][$triggerid]) ? $triggerid : 0;
		}

		Nest.value(isSelected,"triggersSelected").$() = $triggerid > 0;
		Nest.value(ids,"triggerid").$() = $triggerid;
	}

	/**
	 * Load the available network discovery rules, choose the selected rule and remember the selection.
	 *
	 * @param int   $druleid
	 * @param array $options
	 */
	private function _initDiscoveries($druleid, array $options) {
		$def_options = CArray.array(
			"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
			"output" => API_OUTPUT_EXTEND
		);
		$options = zbx_array_merge($def_options, $options);
		$drules = API.DRule().get($options);
		order_result($drules, "name");

		Nest.value(data,"drules").$() = CArray.array();
		for($drules as $drule) {
			data["drules"][$drule["druleid"]] = $drule;
		}

		if (is_null($druleid)) {
			$druleid = Nest.value(_profileIds,"druleid").$();
		}

		if ((!isset(data["drules"][$druleid]) && $druleid > 0) || is_null($druleid)) {
			if (Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_NONE) {
				$druleid = 0;
			}
			elseif (is_null(Nest.value(_requestIds,"druleid").$()) || Nest.value(_requestIds,"druleid").$() > 0) {
				$druleids = array_keys(Nest.value(data,"drules").$());
				$druleid = empty($druleids) ? 0 : reset($druleids);
			}
		}

		CProfile::update(Nest.value(_profileIdx,"drules").$(), $druleid, PROFILE_TYPE_ID);
		CProfile::update(self::DRULE_LATEST_IDX, $druleid, PROFILE_TYPE_ID);

		Nest.value(isSelected,"drulesSelected").$() = (Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"drules").$())) || $druleid > 0;
		Nest.value(isSelected,"drulesAll").$() = Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"drules").$()) && $druleid == 0;
		Nest.value(ids,"druleid").$() = $druleid;
	}

	/**
	 * Set applications related variables.
	 *  - applications: all applications available for dropdown on page
	 *  - application: application currently selected, can be "" for "all" or "not selected"
	 *  - applicationsSelected: if an application selected, i.e. not "not selected"
	 * Applications are dependent on groups.
	 *
	 * @param int   $application
	 * @param array $options
	 */
	private function _initApplications($application, array $options) {
		Nest.value(data,"applications").$() = CArray.array();

		if (!groupsSelected) {
			$application = "";
		}
		else {
			$def_options = CArray.array(
				"nodeids" => Nest.value(config,"all_nodes").$() ? get_current_nodeid() : null,
				"output" => CArray.array("name"),
				"groupids" => (groupid > 0) ? groupid : null
			);
			$options = zbx_array_merge($def_options, $options);
			$applications = API.Application().get($options);

			for($applications as $app) {
				data["applications"][$app["name"]] = $app;
			}

			// select remembered selection
			if (is_null($application) && Nest.value(_profileIds,"application").$()) {
				$application = Nest.value(_profileIds,"application").$();
			}

			// nonexisting or unset application
			if ((!isset(data["applications"][$application]) && $application !== "") || is_null($application)) {
				$application = "";
			}
		}

		if (!is_null(Nest.value(_requestIds,"application").$())) {
			CProfile::update(Nest.value(_profileIdx,"application").$(), $application, PROFILE_TYPE_STR);
		}
		Nest.value(isSelected,"applicationsSelected").$() = (Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"applications").$())) || $application !== "";
		Nest.value(isSelected,"applicationsAll").$() = Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_ALL && !empty(Nest.value(data,"applications").$()) && $application === "";
		Nest.value(ids,"application").$() = $application;
	}

	/**
	 * Initialize minimum trigger severities.
	 *
	 * @param string $severityMin
	 * @param array  $options
	 * @param int    $options["default"]
	 * @param string $options["mapId"]
	 */
	private function _initSeveritiesMin($severityMin, array $options = CArray.array()) {
		$default = isset(Nest.value($options,"default").$()) ? Nest.value($options,"default").$() : TRIGGER_SEVERITY_NOT_CLASSIFIED;
		$mapId = isset(Nest.value($options,"mapId").$()) ? Nest.value($options,"mapId").$() : 0;
		$severityMinProfile = isset(Nest.value(_profileIds,"severityMin").$()) ? Nest.value(_profileIds,"severityMin").$() : null;

		if ($severityMin === null && $severityMinProfile !== null) {
			$severityMin = $severityMinProfile;
		}

		if ($severityMin !== null) {
			if ($severityMin == $default) {
				CProfile::delete(Nest.value(_profileIdx,"severityMin").$(), $mapId);
			}
			else {
				CProfile::update(Nest.value(_profileIdx,"severityMin").$(), $severityMin, PROFILE_TYPE_INT, $mapId);
			}
		}

		Nest.value(data,"severitiesMin").$() = getSeverityCaption();
		data["severitiesMin"][$default] = data["severitiesMin"][$default].SPACE."("._("default").")";
		Nest.value(ids,"severityMin").$() = ($severityMin === null) ? $default : $severityMin;
	}

	/**
	 * Get hosts combobox with selected item.
	 *
	 * @param bool $withNode
	 *
	 * @return CComboBox
	 */
	public function getHostsCB($withNode = false) {
		$items = $classes = CArray.array();
		for(hosts as $id => $host) {
			$items[$id] = Nest.value($host,"name").$();
			$classes[$id] = (Nest.value($host,"status").$() == HOST_STATUS_NOT_MONITORED) ? "not-monitored" : null;
		}
		$options = CArray.array("objectName" => "hosts", "classes" => $classes);

		return _getCB("hostid", hostid, $items, $withNode, $options);
	}

	/**
	 * Get host groups combobox with selected item.
	 *
	 * @param bool $withNode
	 *
	 * @return CComboBox
	 */
	public function getGroupsCB($withNode = false) {
		$items = CArray.array();
		for(groups as $id => $group) {
			$items[$id] = Nest.value($group,"name").$();
		}
		return _getCB("groupid", groupid, $items, $withNode, CArray.array("objectName" => "groups"));
	}

	/**
	 * Get graphs combobox with selected item.
	 *
	 * @param bool $withNode
	 *
	 * @return CComboBox
	 */
	public function getGraphsCB($withNode = false) {
		$graphs = graphs;
		if ($withNode) {
			for($graphs as $id => $graph) {
				$graphs[$id] = get_node_name_by_elid($id, null, NAME_DELIMITER).Nest.value($graph,"name").$();
			}
		}

		natcasesort($graphs);
		$graphs = CArray.array(0 => _("not selected")) + $graphs;

		$graphComboBox = new CComboBox("graphid", graphid, "javascript: submit();");
		for($graphs as $id => $name) {
			$graphComboBox.addItem($id, $name);
		}

		return $graphComboBox;
	}

	/**
	 * Get discovery rules combobox with selected item.
	 *
	 * @param bool $withNode
	 *
	 * @return CComboBox
	 */
	public function getDiscoveryCB($withNode = false) {
		$items = CArray.array();
		for(drules as $id => $drule) {
			$items[$id] = Nest.value($drule,"name").$();
		}
		return _getCB("druleid", druleid, $items, $withNode, CArray.array("objectName" => "discovery"));
	}

	/**
	 * Get applications combobox with selected item.
	 *
	 * @param bool $withNode
	 *
	 * @return CComboBox
	 */
	public function getApplicationsCB($withNode = false) {
		$items = CArray.array();
		for(applications as $id => $application) {
			$items[$id] = Nest.value($application,"name").$();
		}
		return _getCB("application", application, $items, $withNode, CArray.array(
			"objectName" => "applications"
		));
	}

	/**
	 * Get minimum trigger severities combobox with selected item.
	 *
	 * @return CComboBox
	 */
	public function getSeveritiesMinCB() {
		return new CComboBox("severity_min", severityMin, "javascript: submit();", severitiesMin);
	}

	/**
	 * Create combobox with available data.
	 * Preselect active item. Display nodes. Add addition "not selected" or "all" item to top adjusted by configuration.
	 *
	 * @param string $name
	 * @param string $selectedId
	 * @param array  $items
	 * @param bool   $withNode
	 * @param int    $allValue
	 * @param array  $options
	 * @param string $options["objectName"]
	 * @param array  $options["classes"]	array of class names for the combobox options with item IDs as keys
	 *
	 * @return CComboBox
	 */
	private function _getCB($name, $selectedId, $items, $withNode, array $options = CArray.array()) {
		$comboBox = new CComboBox($name, $selectedId, "javascript: submit();");

		if ($withNode) {
			for($items as $id => $item) {
				$items[$id] = get_node_name_by_elid($id, null, NAME_DELIMITER).$item;
			}
		}

		natcasesort($items);

		// add drop down first item
		if (!Nest.value(config,"popupDD").$()) {
			if (isset(config["DDFirstLabels"][$options["objectName"]])) {
				$firstLabel = config["DDFirstLabels"][$options["objectName"]];
			}
			else {
				$firstLabel = (Nest.value(config,"DDFirst").$() == ZBX_DROPDOWN_FIRST_NONE) ? _("not selected") : _("all");
			}

			if ($name == "application") {
				$items = CArray.array("" => $firstLabel) + $items;
			}
			else {
				$items = CArray.array($firstLabel) + $items;
			}
		}

		for($items as $id => $name) {
			$comboBox.addItem($id, $name, null, "yes", isset($options["classes"][$id]) ? $options["classes"][$id] : null);
		}

		return $comboBox;
	}
}
