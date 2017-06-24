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

Nest.value($page,"file").$() = "conf.import.php";
Nest.value($page,"title").$() = _("Configuration import");
Nest.value($page,"type").$() = detect_page_type(PAGE_TYPE_HTML);
Nest.value($page,"hist_arg").$() = CArray.array();

ob_start();

require_once dirname(__FILE__)."/include/page_header.php";

$fields = CArray.array(
	"rules" => CArray.array(T_ZBX_STR, O_OPT, null, null, null),
	"import" => CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT, null, null),
	"rules_preset" => CArray.array(T_ZBX_STR, O_OPT, null, null, null),
	"cancel" => CArray.array(T_ZBX_STR, O_OPT, P_SYS, null, null),
	"form_refresh" => CArray.array(T_ZBX_INT, O_OPT, null, null, null)
);
check_fields($fields);


if (isset(Nest.value(_REQUEST,"cancel").$())) {
	ob_end_clean();
	redirect(CWebUser::Nest.value($data,"last_page","url").$());
}
ob_end_flush();


Nest.value($data,"rules").$() = CArray.array(
	"groups" => CArray.array("createMissing" => false),
	"hosts" => CArray.array("updateExisting" => false, "createMissing" => false),
	"templates" => CArray.array("updateExisting" => false, "createMissing" => false),
	"templateScreens" => CArray.array("updateExisting" => false, "createMissing" => false),
	"templateLinkage" => CArray.array("createMissing" => false),
	"items" => CArray.array("updateExisting" => false, "createMissing" => false),
	"discoveryRules" => CArray.array("updateExisting" => false, "createMissing" => false),
	"triggers" => CArray.array("updateExisting" => false, "createMissing" => false),
	"graphs" => CArray.array("updateExisting" => false, "createMissing" => false),
	"screens" => CArray.array("updateExisting" => false, "createMissing" => false),
	"maps" => CArray.array("updateExisting" => false, "createMissing" => false),
	"images" => CArray.array("updateExisting" => false, "createMissing" => false)
);
// rules presets
if (isset(Nest.value(_REQUEST,"rules_preset").$()) && !isset(Nest.value(_REQUEST,"rules").$())) {
	switch (Nest.value(_REQUEST,"rules_preset").$()) {
		case "host":
			Nest.value($data,"rules","groups").$() = CArray.array("createMissing" => true);
			Nest.value($data,"rules","hosts").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","items").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","discoveryRules").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","triggers").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","graphs").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","templateLinkage").$() = CArray.array("createMissing" => true);
			break;

		case "template":
			Nest.value($data,"rules","groups").$() = CArray.array("createMissing" => true);
			Nest.value($data,"rules","templates").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","templateScreens").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","items").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","discoveryRules").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","triggers").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","graphs").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			Nest.value($data,"rules","templateLinkage").$() = CArray.array("createMissing" => true);
			break;

		case "map":
			Nest.value($data,"rules","maps").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			break;

		case "screen":
			Nest.value($data,"rules","screens").$() = CArray.array("updateExisting" => true, "createMissing" => true);
			break;

	}
}
if (isset(Nest.value(_REQUEST,"rules").$())) {
	$requestRules = get_request("rules", CArray.array());
	// if form was submitted with some checkboxes unchecked, those values are not submitted
	// so that we set missing values to false
	for(Nest.value($data,"rules").$() as $ruleName => $rule) {
		if (!isset($requestRules[$ruleName])) {
			if (isset(Nest.value($rule,"updateExisting").$())) {
				$requestRules[$ruleName]["updateExisting"] = false;
			}
			if (isset(Nest.value($rule,"createMissing").$())) {
				$requestRules[$ruleName]["createMissing"] = false;
			}
		}
		elseif (!isset($requestRules[$ruleName]["updateExisting"]) && isset(Nest.value($rule,"updateExisting").$())){
			$requestRules[$ruleName]["updateExisting"] = false;
		}
		elseif (!isset($requestRules[$ruleName]["createMissing"]) && isset(Nest.value($rule,"createMissing").$())){
			$requestRules[$ruleName]["createMissing"] = false;
		}
	}
	Nest.value($data,"rules").$() = $requestRules;
}

if (isset(Nest.value($_FILES,"import_file").$())) {
	try {
		$file = new CUploadFile(Nest.value($_FILES,"import_file").$());
		$importFormat = CImportReaderFactory::fileExt2ImportFormat($file.getExtension());
		$importReader = CImportReaderFactory::getReader($importFormat);

		$configurationImport = new CConfigurationImport($file.getContent(), Nest.value($data,"rules").$());
		$configurationImport.setReader($importReader);

		$configurationImport.import();
		show_messages(true, _("Imported successfully"));
	}
	catch (Exception $e) {
		error($e.getMessage());
		show_messages(false, null, _("Import failed"));
	}
}

$view = new CView("conf.import", $data);
$view.render();
$view.show();

require_once dirname(__FILE__)."/include/page_footer.php";
