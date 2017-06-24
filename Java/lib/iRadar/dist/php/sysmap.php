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
require_once dirname(__FILE__)."/include/forms.inc.php";

Nest.value($page,"title").$() = _("Configuration of network maps");
Nest.value($page,"file").$() = "sysmap.php";
Nest.value($page,"hist_arg").$() = CArray.array("sysmapid");
Nest.value($page,"scripts").$() = CArray.array("class.cmap.js", "class.cviewswitcher.js", "multiselect.js");
Nest.value($page,"type").$() = detect_page_type();

require_once dirname(__FILE__)."/include/page_header.php";

// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
$fields = CArray.array(
	"sysmapid" =>	CArray.array(T_ZBX_INT, O_MAND, P_SYS,	DB_ID,		null),
	"selementid" =>	CArray.array(T_ZBX_INT, O_OPT, P_SYS,	DB_ID,		null),
	"sysmap" =>		CArray.array(T_ZBX_STR, O_OPT, null,	NOT_EMPTY,	"isset({save})"),
	"selements" =>	CArray.array(T_ZBX_STR, O_OPT, P_SYS,	DB_ID,		null),
	"links" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS,	DB_ID,		null),
	// actions
	"action" =>		CArray.array(T_ZBX_STR, O_OPT, P_ACT,	NOT_EMPTY,	null),
	"save" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT, null,	null),
	"delete" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT, null,	null),
	"cancel" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS,	null,		null),
	"form" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS,	null,		null),
	"form_refresh" => CArray.array(T_ZBX_INT, O_OPT, null,	null,		null),
	// ajax
	"favobj" =>		CArray.array(T_ZBX_STR, O_OPT, P_ACT,	null,		null),
	"favid" =>		CArray.array(T_ZBX_STR, O_OPT, P_ACT,	null,		null),
	"favcnt" =>		CArray.array(T_ZBX_INT, O_OPT, null,	null,		null)
);
check_fields($fields);

/*
 * Ajax
 */
if (isset(Nest.value(_REQUEST,"favobj").$())) {
	$json = new CJSON();

	if (Nest.value(_REQUEST,"favobj").$() == "sysmap" && Nest.value(_REQUEST,"action").$() == "save") {
		$sysmapid = get_request("sysmapid", 0);

		@ob_start();

		try {
			DBstart();

			$sysmap = API.Map().get(CArray.array(
				"sysmapids" => $sysmapid,
				"editable" => true,
				"output" => CArray.array("sysmapid")
			));
			$sysmap = reset($sysmap);

			if ($sysmap === false) {
				throw new Exception(_("Access denied!").\"\n\r\");
			}

			$sysmapUpdate = $json.decode(Nest.value(_REQUEST,"sysmap").$(), true);
			Nest.value($sysmapUpdate,"sysmapid").$() = $sysmapid;

			$result = API.Map().update($sysmapUpdate);

			if ($result !== false) {
				echo "if (Confirm(\""._("Map is saved! Return?")."\")) { location.href = \"sysmaps.php\"; }";
			}
			else {
				throw new Exception(_("Map save operation failed.").\"\n\r\");
			}

			DBend(true);
		}
		catch (Exception $e) {
			DBend(false);
			$msg = CArray.array($e.getMessage());

			for(clear_messages() as $errMsg) {
				$msg[] = $errMsg["type"].": ".Nest.value($errMsg,"message").$();
			}

			ob_clean();

			echo "alert(".zbx_jsvalue(implode(\"\n\r\", $msg)).");";
		}

		@ob_flush();
		exit();

	}
}

if (PAGE_TYPE_HTML != Nest.value($page,"type").$()) {
	require_once dirname(__FILE__)."/include/page_footer.php";
	exit();
}

/*
 * Permissions
 */
if (isset(Nest.value(_REQUEST,"sysmapid").$())) {
	$sysmap = API.Map().get(CArray.array(
		"sysmapids" => Nest.value(_REQUEST,"sysmapid").$(),
		"editable" => true,
		"output" => API_OUTPUT_EXTEND,
		"selectSelements" => API_OUTPUT_EXTEND,
		"selectLinks" => API_OUTPUT_EXTEND,
		"preservekeys" => true
	));
	if (empty($sysmap)) {
		access_deny();
	}
	else {
		$sysmap = reset($sysmap);
	}
}

/*
 * Display
 */
$data = CArray.array(
	"sysmap" => $sysmap,
	"iconList" => CArray.array(),
	"defaultAutoIconId" => null,
	"defaultIconId" => null,
	"defaultIconName" => null
);

// get selements
add_elementNames(Nest.value($data,"sysmap","selements").$());

Nest.value($data,"sysmap","selements").$() = zbx_toHash(Nest.value($data,"sysmap","selements").$(), "selementid");
Nest.value($data,"sysmap","links").$() = zbx_toHash(Nest.value($data,"sysmap","links").$(), "linkid");

// get links
for(Nest.value($data,"sysmap","links").$() as &$link) {
	for(Nest.value($link,"linktriggers").$() as $lnum => $linkTrigger) {
		$dbTrigger = API.Trigger().get(CArray.array(
			"triggerids" => Nest.value($linkTrigger,"triggerid").$(),
			"output" => CArray.array("description", "expression"),
			"selectHosts" => API_OUTPUT_EXTEND,
			"preservekeys" => true,
			"expandDescription" => true
		));
		$dbTrigger = reset($dbTrigger);
		$host = reset(Nest.value($dbTrigger,"hosts").$());

		$link["linktriggers"][$lnum]["desc_exp"] = $host["name"].NAME_DELIMITER.Nest.value($dbTrigger,"description").$();
	}
	order_result(Nest.value($link,"linktriggers").$(), "desc_exp");
}
unset($link);

// get iconmapping
if (Nest.value($data,"sysmap","iconmapid").$()) {
	$iconMap = API.IconMap().get(CArray.array(
		"iconmapids" => Nest.value($data,"sysmap","iconmapid").$(),
		"output" => CArray.array("default_iconid"),
		"preservekeys" => true
	));
	$iconMap = reset($iconMap);
	Nest.value($data,"defaultAutoIconId").$() = Nest.value($iconMap,"default_iconid").$();
}

// get icon list
$icons = DBselect(
	"SELECT i.imageid,i.name".
	" FROM images i".
	" WHERE i.imagetype=".IMAGE_TYPE_ICON.
		andDbNode("i.imageid")
);
while ($icon = DBfetch($icons)) {
	$data["iconList"][] = CArray.array(
		"imageid" => Nest.value($icon,"imageid").$(),
		"name" => $icon["name"]
	);

	if (Nest.value($icon,"name").$() == MAP_DEFAULT_ICON || !isset(Nest.value($data,"defaultIconId").$())) {
		Nest.value($data,"defaultIconId").$() = Nest.value($icon,"imageid").$();
		Nest.value($data,"defaultIconName").$() = Nest.value($icon,"name").$();
	}
}
if (Nest.value($data,"iconList").$()) {
	CArrayHelper::sort(Nest.value($data,"iconList").$(), CArray.array("name"));
	Nest.value($data,"iconList").$() = array_values(Nest.value($data,"iconList").$());
}

// render view
$sysmapView = new CView("configuration.sysmap.constructor", $data);
$sysmapView.render();
$sysmapView.show();

require_once dirname(__FILE__)."/include/page_footer.php";
