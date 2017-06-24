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


class CScreenChart extends CScreenBase {

	/**
	 * Graph id
	 *
	 * @var int
	 */
	public $graphid;

	/**
	 * Init screen data.
	 *
	 * @param array		$options
	 * @param int		$options["graphid"]
	 */
	public function __construct(array $options = CArray.array()) {
		parent::__construct($options);

		graphid = isset(Nest.value($options,"graphid").$()) ? Nest.value($options,"graphid").$() : null;
	}

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		dataId = "graph_full";
		$containerId = "graph_container";

		// time control
		$graphDims = getGraphDims(graphid);
		if (Nest.value($graphDims,"graphtype").$() == GRAPH_TYPE_PIE || Nest.value($graphDims,"graphtype").$() == GRAPH_TYPE_EXPLODED) {
			$loadSBox = 0;
			$src = "chart6.php";
		}
		else {
			$loadSBox = 1;
			$src = "chart2.php";
		}
		$src .= "?graphid=".graphid."&period=".timeline["period"]."&stime=".timeline["stimeNow"].getProfileUrlParams();

		Nest.value(timeline,"starttime").$() = date(TIMESTAMP_FORMAT, get_min_itemclock_by_graphid(graphid));

		$timeControlData = CArray.array(
			"id" => getDataId(),
			"containerid" => $containerId,
			"src" => $src,
			"objDims" => $graphDims,
			"loadSBox" => $loadSBox,
			"loadImage" => 1,
			"dynamic" => 1,
			"periodFixed" => CProfile::get(profileIdx.".timelinefixed", 1),
			"sliderMaximumTimePeriod" => ZBX_MAX_PERIOD
		);

		// output
		if (mode == SCREEN_MODE_JS) {
			Nest.value($timeControlData,"dynamic").$() = 0;
			Nest.value($timeControlData,"loadSBox").$() = 0;

			return "timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).")";
		}
		else {
			if (mode == SCREEN_MODE_SLIDESHOW) {
				insert_js("timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).");");
			}
			else {
				zbx_add_post_js("timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).");");
			}

			return getOutput(new CDiv(null, "center", $containerId), true, CArray.array("graphid" => graphid));
		}
	}
}
