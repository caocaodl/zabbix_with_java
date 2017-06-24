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
?>
<?php

class CServerInfo extends CTable {

	public function __construct() {
		parent::__construct(null, "server_info");
	}

	public function bodyToString() {
		cleanItems();

		$status = get_status();
		$server = (Nest.value($status,"zabbix_server").$() == _("Yes"))
			? new CSpan(_("running"), "off")
			: new CSpan(_("not running"), "on");
		$serverLink = (CWebUser::Nest.value($data,"type").$() == USER_TYPE_SUPER_ADMIN)
			? new CLink(_("Zabbix server"), "report1.php")
			: _("Zabbix server");

		addRow(new CCol(_("Zabbix server info"), "nowrap ui-corner-all ui-widget-header"));
		addRow(_("Updated").NAME_DELIMITER.zbx_date2str(SERVER_INFO_DATE_FORMAT, time()));
		addRow(_("Users (online)").NAME_DELIMITER.$status["users_count"]."(".$status["users_online"].")");
		addRow(new CCol(CArray.array(_("Logged in as").SPACE, new CLink(CWebUser::Nest.value($data,"alias").$(), "profile.php"))));
		addRow(new CCol(CArray.array($serverLink, SPACE._("is").SPACE, $server)), "status");
		addRow(new CCol(CArray.array(
			_("Hosts (m/n/t)").NAME_DELIMITER.$status["hosts_count"]."(",
			new CSpan(Nest.value($status,"hosts_count_monitored").$(), "off"),
			"/",
			new CSpan(Nest.value($status,"hosts_count_not_monitored").$(), "on"),
			"/",
			new CSpan(Nest.value($status,"hosts_count_template").$(), "unknown"),
			")"
		)));
		addRow(new CCol(CArray.array(
			_("Items (m/d/n)").NAME_DELIMITER.$status["items_count"]."(",
			new CSpan(Nest.value($status,"items_count_monitored").$(), "off"),
			"/",
			new CSpan(Nest.value($status,"items_count_disabled").$(), "on"),
			"/",
			new CSpan(Nest.value($status,"items_count_not_supported").$(), "unknown"),
			")"
		)));
		addRow(new CCol(CArray.array(
			_("Triggers (e/d)[p/o]").NAME_DELIMITER.$status["triggers_count"].
			"(".$status["triggers_count_enabled"]."/".$status["triggers_count_disabled"].")[",
			new CSpan(Nest.value($status,"triggers_count_on").$(), "on"),
			"/",
			new CSpan(Nest.value($status,"triggers_count_off").$(), "off"),
			"]"
		)));

		return parent::bodyToString();
	}
}
?>
