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


class CHostsInfo extends CTable {

	public $style;

	public function __construct($groupid = 0, $style = STYLE_HORIZONTAL) {
		nodeid = id2nodeid($groupid);
		groupid = $groupid;
		style = null;

		parent::__construct(null, "hosts_info");
		setOrientation($style);
	}

	public function setOrientation($value) {
		if ($value != STYLE_HORIZONTAL && $value != STYLE_VERTICAL) {
			return error("Incorrect value for SetOrientation \"".$value."\".");
		}
		style = $value;
	}

	public function bodyToString() {
		cleanItems();

		$total = 0;

		// fetch accessible host ids
		$hosts = API.Host().get(CArray.array(
			"nodeids" => get_current_nodeid(true),
			"output" => CArray.array("hostid"),
			"preservekeys" => true
		));
		$hostIds = array_keys($hosts);

		if (groupid != 0) {
			$cond_from = ",hosts_groups hg";
			$cond_where = " AND hg.hostid=h.hostid AND hg.groupid=".zbx_dbstr(groupid);
		}
		else {
			$cond_from = "";
			$cond_where = andDbNode("h.hostid", nodeid);
		}

		$db_host_cnt = DBselect(
			"SELECT COUNT(DISTINCT h.hostid) AS cnt".
			" FROM hosts h".$cond_from.
			" WHERE h.available=".HOST_AVAILABLE_TRUE.
				" AND h.status IN (".HOST_STATUS_MONITORED.",".HOST_STATUS_NOT_MONITORED.")".
				" AND ".dbConditionInt("h.hostid", $hostIds).
				$cond_where
		);

		$host_cnt = DBfetch($db_host_cnt);
		$avail = Nest.value($host_cnt,"cnt").$();
		$total += Nest.value($host_cnt,"cnt").$();

		$db_host_cnt = DBselect(
			"SELECT COUNT(DISTINCT h.hostid) AS cnt".
			" FROM hosts h".$cond_from.
			" WHERE h.available=".HOST_AVAILABLE_FALSE.
				" AND h.status IN (".HOST_STATUS_MONITORED.",".HOST_STATUS_NOT_MONITORED.")".
				" AND ".dbConditionInt("h.hostid", $hostIds).
				$cond_where
		);

		$host_cnt = DBfetch($db_host_cnt);
		$notav = Nest.value($host_cnt,"cnt").$();
		$total += Nest.value($host_cnt,"cnt").$();

		$db_host_cnt = DBselect(
			"SELECT COUNT(DISTINCT h.hostid) AS cnt".
			" FROM hosts h".$cond_from.
			" WHERE h.available=".HOST_AVAILABLE_UNKNOWN.
				" AND h.status IN (".HOST_STATUS_MONITORED.",".HOST_STATUS_NOT_MONITORED.")".
				" AND ".dbConditionInt("h.hostid", $hostIds).
				$cond_where
		);

		$host_cnt = DBfetch($db_host_cnt);
		$uncn = Nest.value($host_cnt,"cnt").$();
		$total += Nest.value($host_cnt,"cnt").$();

		$node = get_node_by_nodeid(nodeid);
		$header_str = _("Hosts info").SPACE;

		if ($node > 0) {
			$header_str .= "(".$node["name"].")".SPACE;
		}

		if (groupid != 0) {
			$group = get_hostgroup_by_groupid(groupid);
			$header_str .= _("Group").SPACE."&quot;".$group["name"]."&quot;";
		}
		else {
			$header_str .= _("All groups");
		}

		$header = new CCol($header_str, "header");
		if (style == STYLE_HORIZONTAL) {
			$header.setColspan(4);
		}

		addRow($header);

		$avail = new CCol($avail."  "._("Available"), "avail");
		$notav = new CCol($notav."  "._("Not available"), "notav");
		$uncn = new CCol($uncn."  "._("Unknown"), "uncn");
		$total = new CCol($total."  "._("Total"), "total");

		if (style == STYLE_HORIZONTAL) {
			addRow(CArray.array($avail, $notav, $uncn, $total));
		}
		else {
			addRow($avail);
			addRow($notav);
			addRow($uncn);
			addRow($total);
		}

		return parent::bodyToString();
	}
}
