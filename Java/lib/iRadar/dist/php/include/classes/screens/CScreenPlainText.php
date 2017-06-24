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


class CScreenPlainText extends CScreenBase {

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		// if screen is defined in template, then "real_resourceid" is defined and should be used
		if (!empty(Nest.value(screenitem,"real_resourceid").$())) {
			Nest.value(screenitem,"resourceid").$() = Nest.value(screenitem,"real_resourceid").$();
		}

		if (Nest.value(screenitem,"dynamic").$() == SCREEN_DYNAMIC_ITEM && !empty(hostid)) {
			$newitemid = get_same_item_for_host(Nest.value(screenitem,"resourceid").$(), hostid);
			Nest.value(screenitem,"resourceid").$() = !empty($newitemid)? $newitemid : 0;
		}

		if (Nest.value(screenitem,"resourceid").$() == 0) {
			$table = new CTableInfo(_("No values found."));
			$table.setHeader(CArray.array(_("Timestamp"), _("Item")));

			return getOutput($table);
		}

		$items = CMacrosResolverHelper::resolveItemNames(CArray.array(get_item_by_itemid(Nest.value(screenitem,"resourceid").$())));
		$item = reset($items);

		switch (Nest.value($item,"value_type").$()) {
			case ITEM_VALUE_TYPE_TEXT:
			case ITEM_VALUE_TYPE_LOG:
				$orderField = "id";
				break;
			case ITEM_VALUE_TYPE_FLOAT:
			case ITEM_VALUE_TYPE_UINT64:
			default:
				$orderField = CArray.array("itemid", "clock");
		}

		$host = get_host_by_itemid(Nest.value(screenitem,"resourceid").$());

		$table = new CTableInfo(_("No values found."));
		$table.setHeader(CArray.array(_("Timestamp"), $host["name"].NAME_DELIMITER.Nest.value($item,"name_expanded").$()));

		$stime = zbxDateToTime(Nest.value(timeline,"stime").$());

		$histories = API.History().get(CArray.array(
			"history" => Nest.value($item,"value_type").$(),
			"itemids" => Nest.value(screenitem,"resourceid").$(),
			"output" => API_OUTPUT_EXTEND,
			"sortorder" => ZBX_SORT_DOWN,
			"sortfield" => $orderField,
			"limit" => Nest.value(screenitem,"elements").$(),
			"time_from" => $stime,
			"time_till" => $stime + timeline["period"]
		));
		for($histories as $history) {
			switch (Nest.value($item,"value_type").$()) {
				case ITEM_VALUE_TYPE_FLOAT:
					sscanf(Nest.value($history,"value").$(), "%f", $value);
					break;
				case ITEM_VALUE_TYPE_TEXT:
				case ITEM_VALUE_TYPE_STR:
				case ITEM_VALUE_TYPE_LOG:
					$value = Nest.value(screenitem,"style").$() ? new CJSscript(Nest.value($history,"value").$()) : Nest.value($history,"value").$();
					break;
				default:
					$value = Nest.value($history,"value").$();
					break;
			}

			if (Nest.value($item,"valuemapid").$() > 0) {
				$value = applyValueMap($value, Nest.value($item,"valuemapid").$());
			}

			$table.addRow(CArray.array(zbx_date2str(_("d M Y H:i:s"), Nest.value($history,"clock").$()), new CCol($value, "pre")));
		}

		return getOutput($table);
	}
}
