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


require_once dirname(__FILE__)."/include/config.inc.php";
require_once dirname(__FILE__)."/include/maps.inc.php";

Nest.value($page,"title").$() = _("Map");
Nest.value($page,"file").$() = "map.php";
Nest.value($page,"type").$() = detect_page_type(PAGE_TYPE_IMAGE);

require_once dirname(__FILE__)."/include/page_header.php";

// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION		EXCEPTION
$fields = CArray.array(
	"sysmapid" =>		CArray.array(T_ZBX_INT, O_MAND, P_SYS,	DB_ID,				null),
	"selements" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS,	DB_ID,				null),
	"links" =>			CArray.array(T_ZBX_STR, O_OPT, P_SYS,	DB_ID,				null),
	"noselements" =>	CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1"),			null),
	"nolinks" =>		CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1"),			null),
	"nocalculations" =>	CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1"),			null),
	"expand_macros" =>	CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1"),			null),
	"show_triggers" =>	CArray.array(T_ZBX_INT, O_OPT, P_SYS,	IN("0,1,2,3"),		null),
	"severity_min" =>	CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1,2,3,4,5"),	null),
	"grid" =>			CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 500),	null),
	"base64image" =>	CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1"),			null)
);
check_fields($fields);

$maps = API.Map().get(CArray.array(
	"sysmapids" => Nest.value(_REQUEST,"sysmapid").$(),
	"selectSelements" => API_OUTPUT_EXTEND,
	"selectLinks" => API_OUTPUT_EXTEND,
	"output" => API_OUTPUT_EXTEND,
	"preservekeys" => true
));
$map = reset($maps);
if (empty($map)) {
	access_deny();
}

$mapPainter = new CMapPainter($map, CArray.array(
	"map" => CArray.array(
		"drawAreas" => (!isset(Nest.value(_REQUEST,"selements").$()) && !isset(Nest.value(_REQUEST,"noselements").$()))
	),
	"grid" => CArray.array(
		"size" => get_request("grid", 0)
	)
));

$im = $mapPainter.paint();

Nest.value($colors,"Red").$() = imagecolorallocate($im, 255, 0, 0);
$colors["Dark Red"] = imagecolorallocate($im, 150, 0, 0);
Nest.value($colors,"Green").$() = imagecolorallocate($im, 0, 255, 0);
$colors["Dark Green"] = imagecolorallocate($im, 0, 150, 0);
Nest.value($colors,"Blue").$() = imagecolorallocate($im, 0, 0, 255);
$colors["Dark Blue"] = imagecolorallocate($im, 0, 0, 150);
Nest.value($colors,"Yellow").$() = imagecolorallocate($im, 255, 255, 0);
$colors["Dark Yellow"] = imagecolorallocate($im, 150, 150, 0);
Nest.value($colors,"Cyan").$() = imagecolorallocate($im, 0, 255, 255);
Nest.value($colors,"Black").$() = imagecolorallocate($im, 0, 0, 0);
Nest.value($colors,"Gray").$() = imagecolorallocate($im, 150, 150, 150);
Nest.value($colors,"White").$() = imagecolorallocate($im, 255, 255, 255);
Nest.value($colors,"Orange").$() = imagecolorallocate($im, 238, 96, 0);

$x = imagesx($im);
$y = imagesy($im);

/*
 * Actions
 */
$json = new CJSON();

if (isset(Nest.value(_REQUEST,"selements").$()) || isset(Nest.value(_REQUEST,"noselements").$())) {
	Nest.value($map,"selements").$() = get_request("selements", "[]");
	Nest.value($map,"selements").$() = $json.decode(Nest.value($map,"selements").$(), true);
}
else {
	add_elementNames(Nest.value($map,"selements").$());
}

if (isset(Nest.value(_REQUEST,"links").$()) || isset(Nest.value(_REQUEST,"nolinks").$())) {
	Nest.value($map,"links").$() = get_request("links", "[]");
	Nest.value($map,"links").$() = $json.decode(Nest.value($map,"links").$(), true);
}

if (get_request("nocalculations", false)) {
	for(Nest.value($map,"selements").$() as $selement) {
		if (Nest.value($selement,"elementtype").$() != SYSMAP_ELEMENT_TYPE_IMAGE) {
			add_elementNames(Nest.value($map,"selements").$());
			break;
		}
	}

	// get default iconmap id to use for elements that use icon map
	if (Nest.value($map,"iconmapid").$()) {
		$iconMaps = API.IconMap().get(CArray.array(
			"iconmapids" => Nest.value($map,"iconmapid").$(),
			"output" => CArray.array("default_iconid"),
			"preservekeys" => true
		));
		$iconMap = reset($iconMaps);

		$defaultAutoIconId = Nest.value($iconMap,"default_iconid").$();
	}

	$mapInfo = CArray.array();
	for(Nest.value($map,"selements").$() as $selement) {
		// if element use icon map and icon map is set for map, and is host like element, we use default icon map icon
		if (Nest.value($map,"iconmapid").$() && $selement["use_iconmap"]
				&& (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_HOST
					|| (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP
						&& Nest.value($selement,"elementsubtype").$() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS))) {
			$iconid = $defaultAutoIconId;
		}
		else {
			$iconid = Nest.value($selement,"iconid_off").$();
		}

		$mapInfo[$selement["selementid"]] = CArray.array(
			"iconid" => $iconid,
			"icon_type" => SYSMAP_ELEMENT_ICON_OFF
		);

		$mapInfo[$selement["selementid"]]["name"] = (Nest.value($selement,"elementtype").$() == SYSMAP_ELEMENT_TYPE_IMAGE)
			? _("Image")
			: Nest.value($selement,"elementName").$();
	}

	$allLinks = true;
}
else {
	// we need selements to be a hash for further processing
	Nest.value($map,"selements").$() = zbx_toHash(Nest.value($map,"selements").$(), "selementid");

	add_triggerExpressions(Nest.value($map,"selements").$());

	$areas = populateFromMapAreas($map);
	$mapInfo = getSelementsInfo($map, CArray.array("severity_min" => get_request("severity_min")));
	processAreasCoordinates($map, $areas, $mapInfo);
	$allLinks = false;
}

/*
 * Draw map
 */
drawMapConnectors($im, $map, $mapInfo, $allLinks);

if (!isset(Nest.value(_REQUEST,"noselements").$())) {
	drawMapHighligts($im, $map, $mapInfo);
	drawMapSelements($im, $map, $mapInfo);
}

$expandMacros = get_request("expand_macros", true);
drawMapLabels($im, $map, $mapInfo, $expandMacros);
drawMapLinkLabels($im, $map, $mapInfo, $expandMacros);

if (!isset(Nest.value(_REQUEST,"noselements").$()) && Nest.value($map,"markelements").$() == 1) {
	drawMapSelementsMarks($im, $map, $mapInfo);
}

show_messages();

if (get_request("base64image")) {
	ob_start();
	imagepng($im);
	$imageSource = ob_get_contents();
	ob_end_clean();
	$json = new CJSON();
	echo $json.encode(CArray.array("result" => base64_encode($imageSource)));
	imagedestroy($im);
}
else {
	imageOut($im);
}

require_once dirname(__FILE__)."/include/page_footer.php";
