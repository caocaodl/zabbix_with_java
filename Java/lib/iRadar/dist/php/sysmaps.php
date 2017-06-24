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
require_once dirname(__FILE__)."/include/ident.inc.php";
require_once dirname(__FILE__)."/include/forms.inc.php";

if (isset(Nest.value(_REQUEST,"go").$()) && Nest.value(_REQUEST,"go").$() == "export" && isset(Nest.value(_REQUEST,"maps").$())) {
	Nest.value($page,"file").$() = "zbx_export_maps.xml";
	Nest.value($page,"type").$() = detect_page_type(PAGE_TYPE_XML);

	$isExportData = true;
}
else {
	Nest.value($page,"title").$() = _("Configuration of network maps");
	Nest.value($page,"file").$() = "sysmaps.php";
	Nest.value($page,"type").$() = detect_page_type(PAGE_TYPE_HTML);
	Nest.value($page,"hist_arg").$() = CArray.array();

	$isExportData = false;
}

require_once dirname(__FILE__)."/include/page_header.php";

// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
$fields = CArray.array(
	"maps" =>					CArray.array(T_ZBX_INT, O_OPT, P_SYS,	DB_ID,			null),
	"sysmapid" =>				CArray.array(T_ZBX_INT, O_OPT, P_SYS,	DB_ID,			null),
	"name" =>					CArray.array(T_ZBX_STR, O_OPT, null,	NOT_EMPTY, "isset({save})", _("Name")),
	"width" =>					CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})", _("Width")),
	"height" =>					CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 65535), "isset({save})", _("Height")),
	"backgroundid" =>			CArray.array(T_ZBX_INT, O_OPT, null,	DB_ID,			"isset({save})"),
	"iconmapid" =>				CArray.array(T_ZBX_INT, O_OPT, null,	DB_ID,			"isset({save})"),
	"expandproblem" =>			CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 1),	null),
	"markelements" =>			CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 1),	null),
	"show_unack" =>				CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 2),	null),
	"highlight" =>				CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 1),	null),
	"label_format" =>			CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 1),	null),
	"label_type_host" =>		CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
	"label_type_hostgroup" =>	CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
	"label_type_trigger" =>		CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
	"label_type_map" =>			CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
	"label_type_image" =>		CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL, MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
	"label_string_host" =>		CArray.array(T_ZBX_STR, O_OPT, null,	null,			"isset({save})"),
	"label_string_hostgroup" =>	CArray.array(T_ZBX_STR, O_OPT, null,	null,			"isset({save})"),
	"label_string_trigger" =>	CArray.array(T_ZBX_STR, O_OPT, null,	null,			"isset({save})"),
	"label_string_map" =>		CArray.array(T_ZBX_STR, O_OPT, null,	null,			"isset({save})"),
	"label_string_image" =>		CArray.array(T_ZBX_STR, O_OPT, null,	null,			"isset({save})"),
	"label_type" =>				CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(MAP_LABEL_TYPE_LABEL,MAP_LABEL_TYPE_CUSTOM), "isset({save})"),
	"label_location" =>			CArray.array(T_ZBX_INT, O_OPT, null,	BETWEEN(0, 3),	"isset({save})"),
	"urls" =>					CArray.array(T_ZBX_STR, O_OPT, null,	null,			null),
	"severity_min" =>			CArray.array(T_ZBX_INT, O_OPT, null,	IN("0,1,2,3,4,5"), null),
	// actions
	"save" =>					CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT, null,		null),
	"delete" =>					CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT, null,		null),
	"cancel" =>					CArray.array(T_ZBX_STR, O_OPT, P_SYS,	null,			null),
	"go" =>						CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT, null,		null),
	// form
	"form" =>					CArray.array(T_ZBX_STR, O_OPT, P_SYS,	null,			null),
	"form_refresh" =>			CArray.array(T_ZBX_INT, O_OPT, null,	null,			null)
);
check_fields($fields);
validate_sort_and_sortorder("name", ZBX_SORT_UP);

/*
 * Permissions
 */
if (isset(Nest.value(_REQUEST,"sysmapid").$())) {
	$sysmap = API.Map().get(CArray.array(
		"sysmapids" => Nest.value(_REQUEST,"sysmapid").$(),
		"editable" => true,
		"output" => API_OUTPUT_EXTEND,
		"selectUrls" => API_OUTPUT_EXTEND
	));
	if (empty($sysmap)) {
		access_deny();
	}
	else {
		$sysmap = reset($sysmap);
	}
}
else {
	$sysmap = CArray.array();
}

if ($isExportData) {
	$export = new CConfigurationExport(CArray.array("maps" => get_request("maps", CArray.array())));
	$export.setBuilder(new CConfigurationExportBuilder());
	$export.setWriter(CExportWriterFactory::getWriter(CExportWriterFactory::XML));
	$exportData = $export.export();

	if (hasErrorMesssages()) {
		show_messages();
	}
	else {
		echo $exportData;
	}
	exit();
}

Nest.value(_REQUEST,"go").$() = get_request("go", "none");

/*
 * Actions
 */
if (isset(Nest.value(_REQUEST,"save").$())) {
	$map = CArray.array(
		"name" => Nest.value(_REQUEST,"name").$(),
		"width" => Nest.value(_REQUEST,"width").$(),
		"height" => Nest.value(_REQUEST,"height").$(),
		"backgroundid" => Nest.value(_REQUEST,"backgroundid").$(),
		"iconmapid" => Nest.value(_REQUEST,"iconmapid").$(),
		"highlight" => get_request("highlight", 0),
		"markelements" => get_request("markelements", 0),
		"expandproblem" => get_request("expandproblem", 0),
		"label_format" => get_request("label_format", 0),
		"label_type_host" => get_request("label_type_host", 2),
		"label_type_hostgroup" => get_request("label_type_hostgroup", 2),
		"label_type_trigger" => get_request("label_type_trigger", 2),
		"label_type_map" => get_request("label_type_map", 2),
		"label_type_image" => get_request("label_type_image", 2),
		"label_string_host" => get_request("label_string_host", ""),
		"label_string_hostgroup" => get_request("label_string_hostgroup", ""),
		"label_string_trigger" => get_request("label_string_trigger", ""),
		"label_string_map" => get_request("label_string_map", ""),
		"label_string_image" => get_request("label_string_image", ""),
		"label_type" => Nest.value(_REQUEST,"label_type").$(),
		"label_location" => Nest.value(_REQUEST,"label_location").$(),
		"show_unack" => get_request("show_unack", 0),
		"severity_min" => get_request("severity_min", TRIGGER_SEVERITY_NOT_CLASSIFIED),
		"urls" => get_request("urls", CArray.array())
	);

	for(Nest.value($map,"urls").$() as $unum => $url) {
		if (zbx_empty(Nest.value($url,"name").$()) && zbx_empty(Nest.value($url,"url").$())) {
			unset($map["urls"][$unum]);
		}
	}

	if (isset(Nest.value(_REQUEST,"sysmapid").$())) {
		// TODO check permission by new value.
		Nest.value($map,"sysmapid").$() = Nest.value(_REQUEST,"sysmapid").$();
		$result = API.Map().update($map);

		$auditAction = AUDIT_ACTION_UPDATE;
		show_messages($result, _("Network map updated"), _("Cannot update network map"));
	}
	else {
		$result = API.Map().create($map);

		$auditAction = AUDIT_ACTION_ADD;
		show_messages($result, _("Network map added"), _("Cannot add network map"));
	}

	if ($result) {
		add_audit($auditAction, AUDIT_RESOURCE_MAP, "Name ["._REQUEST["name"]."]");
		unset(Nest.value(_REQUEST,"form").$());
		clearCookies($result);
	}
}
elseif ((isset(Nest.value(_REQUEST,"delete").$()) && isset(Nest.value(_REQUEST,"sysmapid").$())) || Nest.value(_REQUEST,"go").$() == "delete") {
	$sysmapIds = get_request("maps", CArray.array());

	if (isset(Nest.value(_REQUEST,"sysmapid").$())) {
		$sysmapIds[] = Nest.value(_REQUEST,"sysmapid").$();
	}

	DBstart();

	$maps = API.Map().get(CArray.array(
		"sysmapids" => $sysmapIds,
		"output" => CArray.array("sysmapid", "name"),
		"editable" => true
	));

	$result = API.Map().delete($sysmapIds);

	if ($result) {
		unset(Nest.value(_REQUEST,"form").$());

		for($maps as $map) {
			add_audit_ext(AUDIT_ACTION_DELETE, AUDIT_RESOURCE_MAP, Nest.value($map,"sysmapid").$(), Nest.value($map,"name").$(), null, null, null);
		}
	}

	$result = DBend($result);

	show_messages($result, _("Network map deleted"), _("Cannot delete network map"));
	clearCookies($result);
}

/*
 * Display
 */
if (isset(Nest.value(_REQUEST,"form").$())) {
	if (!isset(Nest.value(_REQUEST,"sysmapid").$()) || isset(Nest.value(_REQUEST,"form_refresh").$())) {
		$data = CArray.array(
			"sysmap" => CArray.array(
				"sysmapid" => getRequest("sysmapid"),
				"name" => get_request("name", ""),
				"width" => get_request("width", 800),
				"height" => get_request("height", 600),
				"backgroundid" => get_request("backgroundid", 0),
				"iconmapid" => get_request("iconmapid", 0),
				"label_format" => get_request("label_format", 0),
				"label_type_host" => get_request("label_type_host", 2),
				"label_type_hostgroup" => get_request("label_type_hostgroup", 2),
				"label_type_trigger" => get_request("label_type_trigger", 2),
				"label_type_map" => get_request("label_type_map", 2),
				"label_type_image" => get_request("label_type_image", 2),
				"label_string_host" => get_request("label_string_host", ""),
				"label_string_hostgroup" => get_request("label_string_hostgroup", ""),
				"label_string_trigger" => get_request("label_string_trigger", ""),
				"label_string_map" => get_request("label_string_map", ""),
				"label_string_image" => get_request("label_string_image", ""),
				"label_type" => get_request("label_type", 0),
				"label_location" => get_request("label_location", 0),
				"highlight" => get_request("highlight", 0),
				"markelements" => get_request("markelements", 0),
				"expandproblem" => get_request("expandproblem", 0),
				"show_unack" => get_request("show_unack", 0),
				"severity_min" => get_request("severity_min", TRIGGER_SEVERITY_NOT_CLASSIFIED),
				"urls" => get_request("urls", CArray.array())
			)
		);
	}
	else {
		$data = CArray.array("sysmap" => $sysmap);
	}

	// config
	Nest.value($data,"config").$() = select_config();

	// advanced labels
	Nest.value($data,"labelTypes").$() = sysmapElementLabel();
	Nest.value($data,"labelTypesLimited").$() = Nest.value($data,"labelTypes").$();
	unset($data["labelTypesLimited"][MAP_LABEL_TYPE_IP]);
	Nest.value($data,"labelTypesImage").$() = Nest.value($data,"labelTypesLimited").$();
	unset($data["labelTypesImage"][MAP_LABEL_TYPE_STATUS]);

	// images
	Nest.value($data,"images").$() = API.Image().get(CArray.array(
		"output" => CArray.array("imageid", "name"),
		"filter" => CArray.array("imagetype" => IMAGE_TYPE_BACKGROUND)
	));
	order_result(Nest.value($data,"images").$(), "name");

	for(Nest.value($data,"images").$() as $num => $image) {
		$data["images"][$num]["name"] = get_node_name_by_elid(Nest.value($image,"imageid").$(), null, NAME_DELIMITER).Nest.value($image,"name").$();
	}

	// icon maps
	Nest.value($data,"iconMaps").$() = API.IconMap().get(CArray.array(
		"output" => CArray.array("iconmapid", "name"),
		"preservekeys" => true
	));
	order_result(Nest.value($data,"iconMaps").$(), "name");

	// render view
	$mapView = new CView("configuration.sysmap.edit", $data);
	$mapView.render();
	$mapView.show();
}
else {
	$data = CArray.array();

	// get maps
	$sortField = getPageSortField("name");
	$sortOrder = getPageSortOrder();

	Nest.value($data,"maps").$() = API.Map().get(CArray.array(
		"editable" => true,
		"output" => CArray.array("sysmapid", "name", "width", "height"),
		"sortfield" => $sortField,
		"sortorder" => $sortOrder,
		"limit" => Nest.value($config,"search_limit").$() + 1
	));
	order_result(Nest.value($data,"maps").$(), $sortField, $sortOrder);

	// paging
	Nest.value($data,"paging").$() = getPagingLine(Nest.value($data,"maps").$(), CArray.array("sysmapid"));

	// nodes
	if (Nest.value($data,"displayNodes").$() = is_array(get_current_nodeid())) {
		for(Nest.value($data,"maps").$() as &$map) {
			Nest.value($map,"nodename").$() = get_node_name_by_elid(Nest.value($map,"sysmapid").$(), true);
		}
		unset($map);
	}

	// render view
	$mapView = new CView("configuration.sysmap.list", $data);
	$mapView.render();
	$mapView.show();
}

require_once dirname(__FILE__)."/include/page_footer.php";
