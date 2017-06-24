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


class CScreenSimpleGraph extends CScreenBase {

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		dataId = "graph_".screenitem["screenitemid"]."_".Nest.value(screenitem,"screenid").$();
		$resourceid = !empty(Nest.value(screenitem,"real_resourceid").$()) ? Nest.value(screenitem,"real_resourceid").$() : Nest.value(screenitem,"resourceid").$();
		$containerid = "graph_container_".screenitem["screenitemid"]."_".Nest.value(screenitem,"screenid").$();
		$graphDims = getGraphDims();
		Nest.value($graphDims,"graphHeight").$() = Nest.value(screenitem,"height").$();
		Nest.value($graphDims,"width").$() = Nest.value(screenitem,"width").$();

		// get time control
		$timeControlData = CArray.array(
			"id" => getDataId(),
			"containerid" => $containerid,
			"objDims" => $graphDims,
			"loadImage" => 1,
			"periodFixed" => CProfile::get("web.screens.timelinefixed", 1),
			"sliderMaximumTimePeriod" => ZBX_MAX_PERIOD
		);

		// host feature
		if (Nest.value(screenitem,"dynamic").$() == SCREEN_DYNAMIC_ITEM && !empty(hostid)) {
			$newitemid = get_same_item_for_host($resourceid, hostid);
			$resourceid = !empty($newitemid) ? $newitemid : "";
		}

		if (mode == SCREEN_MODE_PREVIEW && !empty($resourceid)) {
			action = "history.php?action=showgraph&itemid=".$resourceid."&period=".timeline["period"].
					"&stime=".timeline["stimeNow"].getProfileUrlParams();
		}

		if (!zbx_empty($resourceid) && mode != SCREEN_MODE_EDIT) {
			if (mode == SCREEN_MODE_PREVIEW) {
				Nest.value($timeControlData,"loadSBox").$() = 1;
			}
		}

		Nest.value($timeControlData,"src").$() = zbx_empty($resourceid)
			? "chart3.php?"
			: "chart.php?itemid=".$resourceid."&".screenitem["url"]."&width=".screenitem["width"]."&height=".Nest.value(screenitem,"height").$();

		Nest.value($timeControlData,"src").$() .= (mode == SCREEN_MODE_EDIT)
			? "&period=3600&stime=".date(TIMESTAMP_FORMAT, time())
			: "&period=".timeline["period"]."&stime=".Nest.value(timeline,"stimeNow").$();

		Nest.value($timeControlData,"src").$() .= getProfileUrlParams();

		// output
		if (mode == SCREEN_MODE_JS) {
			return "timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).")";
		}
		else {
			if (mode == SCREEN_MODE_SLIDESHOW) {
				insert_js("timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).");");
			}
			else {
				zbx_add_post_js("timeControl.addObject(\"".getDataId()."\", ".zbx_jsvalue(timeline).", ".zbx_jsvalue($timeControlData).");");
			}

			if (mode == SCREEN_MODE_EDIT || mode == SCREEN_MODE_SLIDESHOW) {
				$item = new CDiv();
			}
			elseif (mode == SCREEN_MODE_PREVIEW) {
				$item = new CLink(null, "history.php?action=showgraph&itemid=".$resourceid."&period=".timeline["period"].
						"&stime=".Nest.value(timeline,"stimeNow").$());
			}
			$item.setAttribute("id", $containerid);

			return getOutput($item);
		}
	}
}
