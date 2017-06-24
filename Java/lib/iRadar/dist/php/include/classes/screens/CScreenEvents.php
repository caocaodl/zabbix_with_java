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


class CScreenEvents extends CScreenBase {

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		$options = CArray.array(
			"monitored" => true,
			"value" => CArray.array(TRIGGER_VALUE_TRUE, TRIGGER_VALUE_FALSE),
			"triggerLimit" => Nest.value(screenitem,"elements").$(),
			"eventLimit" => screenitem["elements"]
		);

		$item = new CTableInfo(_("No events found."));
		$item.setHeader(CArray.array(
			_("Time"),
			is_show_all_nodes() ? _("Node") : null,
			_("Host"),
			_("Description"),
			_("Value"),
			_("Severity")
		));

		$events = getLastEvents($options);
		for($events as $event) {
			$trigger = Nest.value($event,"trigger").$();
			$host = Nest.value($event,"host").$();

			$statusSpan = new CSpan(trigger_value2str(Nest.value($event,"value").$()));

			// add colors and blinking to span depending on configuration and trigger parameters
			addTriggerValueStyle($statusSpan, Nest.value($event,"value").$(), Nest.value($event,"clock").$(), Nest.value($event,"acknowledged").$());

			$item.addRow(CArray.array(
				zbx_date2str(_("d M Y H:i:s"), Nest.value($event,"clock").$()),
				get_node_name_by_elid(Nest.value($event,"objectid").$()),
				Nest.value($host,"name").$(),
				new CLink(
					Nest.value($trigger,"description").$(),
					"tr_events.php?triggerid=".$event["objectid"]."&eventid=".$event["eventid"]
				),
				$statusSpan,
				getSeverityCell(Nest.value($trigger,"priority").$())
			));
		}

		return getOutput($item);
	}
}
