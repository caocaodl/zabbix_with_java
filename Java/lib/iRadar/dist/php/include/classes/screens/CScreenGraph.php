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


class CScreenGraph extends CScreenBase {

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		dataId = "graph_".screenitem["screenitemid"]."_".Nest.value(screenitem,"screenid").$();
		$resourceid = !empty(Nest.value(screenitem,"real_resourceid").$()) ? Nest.value(screenitem,"real_resourceid").$() : Nest.value(screenitem,"resourceid").$();
		$containerid = "graph_container_".screenitem["screenitemid"]."_".Nest.value(screenitem,"screenid").$();
		$graphDims = getGraphDims($resourceid);
		Nest.value($graphDims,"graphHeight").$() = Nest.value(screenitem,"height").$();
		Nest.value($graphDims,"width").$() = Nest.value(screenitem,"width").$();
		$graph = getGraphByGraphId($resourceid);
		$graphid = Nest.value($graph,"graphid").$();
		$legend = Nest.value($graph,"show_legend").$();
		$graph3d = Nest.value($graph,"show_3d").$();

		if (Nest.value(screenitem,"dynamic").$() == SCREEN_DYNAMIC_ITEM && !empty(hostid)) {
			// get host
			$hosts = API.Host().get(CArray.array(
				"hostids" => hostid,
				"output" => CArray.array("hostid", "name")
			));
			$host = reset($hosts);

			// get graph
			$graph = API.Graph().get(CArray.array(
				"graphids" => $resourceid,
				"output" => API_OUTPUT_EXTEND,
				"selectHosts" => API_OUTPUT_REFER,
				"selectGraphItems" => API_OUTPUT_EXTEND
			));
			$graph = reset($graph);

			// if items from one host we change them, or set calculated if not exist on that host
			if (count(Nest.value($graph,"hosts").$()) == 1) {
				if (Nest.value($graph,"ymax_type").$() == GRAPH_YAXIS_TYPE_ITEM_VALUE && Nest.value($graph,"ymax_itemid").$()) {
					$newDinamic = getSameGraphItemsForHost(
						CArray.array(CArray.array("itemid" => Nest.value($graph,"ymax_itemid").$())),
						hostid,
						false
					);
					$newDinamic = reset($newDinamic);

					if (isset(Nest.value($newDinamic,"itemid").$()) && Nest.value($newDinamic,"itemid").$() > 0) {
						Nest.value($graph,"ymax_itemid").$() = Nest.value($newDinamic,"itemid").$();
					}
					else {
						Nest.value($graph,"ymax_type").$() = GRAPH_YAXIS_TYPE_CALCULATED;
					}
				}

				if (Nest.value($graph,"ymin_type").$() == GRAPH_YAXIS_TYPE_ITEM_VALUE && Nest.value($graph,"ymin_itemid").$()) {
					$newDinamic = getSameGraphItemsForHost(
						CArray.array(CArray.array("itemid" => Nest.value($graph,"ymin_itemid").$())),
						hostid,
						false
					);
					$newDinamic = reset($newDinamic);

					if (isset(Nest.value($newDinamic,"itemid").$()) && Nest.value($newDinamic,"itemid").$() > 0) {
						Nest.value($graph,"ymin_itemid").$() = Nest.value($newDinamic,"itemid").$();
					}
					else {
						Nest.value($graph,"ymin_type").$() = GRAPH_YAXIS_TYPE_CALCULATED;
					}
				}
			}

			// get url
			Nest.value(screenitem,"url").$() = (Nest.value($graph,"graphtype").$() == GRAPH_TYPE_PIE || Nest.value($graph,"graphtype").$() == GRAPH_TYPE_EXPLODED)
				? "chart7.php"
				: "chart3.php";
			Nest.value(screenitem,"url").$() = new CUrl(Nest.value(screenitem,"url").$());

			for($graph as $name => $value) {
				if ($name == "width" || $name == "height") {
					continue;
				}
				screenitem["url"]->setArgument($name, $value);
			}

			$newGraphItems = getSameGraphItemsForHost(Nest.value($graph,"gitems").$(), hostid, false);
			for($newGraphItems as $newGraphItem) {
				unset(Nest.value($newGraphItem,"gitemid").$(), Nest.value($newGraphItem,"graphid").$());

				for($newGraphItem as $name => $value) {
					screenitem["url"]->setArgument("items[".$newGraphItem["itemid"]."][".$name."]", $value);
				}
			}

			screenitem["url"]->setArgument("name", $host["name"].NAME_DELIMITER.Nest.value($graph,"name").$());
			Nest.value(screenitem,"url").$() = screenitem["url"]->getUrl();
		}

		// get time control
		$timeControlData = CArray.array(
			"id" => getDataId(),
			"containerid" => $containerid,
			"objDims" => $graphDims,
			"loadSBox" => 0,
			"loadImage" => 1,
			"periodFixed" => CProfile::get("web.screens.timelinefixed", 1),
			"sliderMaximumTimePeriod" => ZBX_MAX_PERIOD
		);

		$isDefault = false;
		if (Nest.value($graphDims,"graphtype").$() == GRAPH_TYPE_PIE || Nest.value($graphDims,"graphtype").$() == GRAPH_TYPE_EXPLODED) {
			if (Nest.value(screenitem,"dynamic").$() == SCREEN_SIMPLE_ITEM || empty(Nest.value(screenitem,"url").$())) {
				Nest.value(screenitem,"url").$() = "chart6.php?graphid=".$resourceid."&screenid=".Nest.value(screenitem,"screenid").$();
				$isDefault = true;
			}

			Nest.value(timeline,"starttime").$() = date(TIMESTAMP_FORMAT, get_min_itemclock_by_graphid($resourceid));

			Nest.value($timeControlData,"src").$() = screenitem["url"]."&width=".screenitem["width"]."&height=".screenitem["height"]
				."&legend=".$legend."&graph3d=".$graph3d.getProfileUrlParams();
			Nest.value($timeControlData,"src").$() .= (mode == SCREEN_MODE_EDIT)
				? "&period=3600&stime=".date(TIMESTAMP_FORMAT, time())
				: "&period=".timeline["period"]."&stime=".Nest.value(timeline,"stimeNow").$();
		}
		else {
			if (Nest.value(screenitem,"dynamic").$() == SCREEN_SIMPLE_ITEM || empty(Nest.value(screenitem,"url").$())) {
				Nest.value(screenitem,"url").$() = "chart2.php?graphid=".$resourceid."&screenid=".Nest.value(screenitem,"screenid").$();
				$isDefault = true;
			}

			if (mode != SCREEN_MODE_EDIT && !empty($graphid)) {
				if (mode == SCREEN_MODE_PREVIEW) {
					Nest.value($timeControlData,"loadSBox").$() = 1;
				}
			}

			Nest.value($timeControlData,"src").$() = screenitem["url"]."&width=".screenitem["width"]."&height=".screenitem["height"]
				.getProfileUrlParams();
			Nest.value($timeControlData,"src").$() .= (mode == SCREEN_MODE_EDIT)
				? "&period=3600&stime=".date(TIMESTAMP_FORMAT, time())
				: "&period=".timeline["period"]."&stime=".Nest.value(timeline,"stimeNow").$();
		}

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

			if ((mode == SCREEN_MODE_EDIT || mode == SCREEN_MODE_SLIDESHOW) || !$isDefault) {
				$item = new CDiv();
			}
			elseif (mode == SCREEN_MODE_PREVIEW) {
				$item = new CLink(null, "charts.php?graphid=".$resourceid."&period=".timeline["period"].
						"&stime=".Nest.value(timeline,"stimeNow").$());
			}
			$item.setAttribute("id", $containerid);

			return getOutput($item);
		}
	}
}
