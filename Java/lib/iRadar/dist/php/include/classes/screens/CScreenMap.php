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


class CScreenMap extends CScreenBase {

	/**
	 * Params for monitoring maps js.
	 *
	 * @var array
	 */
	private $data = CArray.array();

	/**
	 * Process screen.
	 *
	 * @return CDiv (screen inside container)
	 */
	public function get() {
		$image = new CImg("map.php?noedit=1&sysmapid=".screenitem["resourceid"]."&width=".screenitem["width"]
			."&height=".screenitem["height"]."&curtime=".time());
		$image.setAttribute("id", "map_".Nest.value(screenitem,"screenitemid").$());

		if (mode == SCREEN_MODE_PREVIEW) {
			$sysmap = API.Map().get(CArray.array(
				"sysmapids" => Nest.value(screenitem,"resourceid").$(),
				"output" => API_OUTPUT_EXTEND,
				"selectSelements" => API_OUTPUT_EXTEND,
				"selectLinks" => API_OUTPUT_EXTEND,
				"expandUrls" => true,
				"nopermissions" => true,
				"preservekeys" => true
			));
			$sysmap = reset($sysmap);

			$image.setSrc($image.getAttribute("src")."&severity_min=".Nest.value($sysmap,"severity_min").$());

			$actionMap = getActionMapBySysmap($sysmap, CArray.array("severity_min" => Nest.value($sysmap,"severity_min").$()));
			$image.setMap($actionMap.getName());

			$output = CArray.array($actionMap, $image);
		}
		elseif (mode == SCREEN_MODE_EDIT) {
			$output = CArray.array($image, BR(), new CLink(_("Change"), action));
		}
		else {
			$output = CArray.array($image);
		}

		insertFlickerfreeJs();

		$div = new CDiv($output, "map-container flickerfreescreen", getScreenId());
		$div.setAttribute("data-timestamp", timestamp);
		$div.addStyle("position: relative;");

		return $div;
	}
}
