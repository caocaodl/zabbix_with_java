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


class CSetupWizard extends CForm {

	function __construct(&$ZBX_CONFIG) {
		DISABLE_NEXT_BUTTON = false;
		ZBX_CONFIG = &$ZBX_CONFIG;

		stage = CArray.array(
			0 => CArray.array(
				"title" => "1. Welcome",
				"fnc" => "stage1"
			),
			1 => CArray.array(
				"title" => "2. Check of pre-requisites",
				"fnc" => "stage2"
			),
			2 => CArray.array(
				"title" => "3. Configure DB connection",
				"fnc" => "stage3"
			),
			3 => CArray.array(
				"title" => "4. Zabbix server details",
				"fnc" => "stage4"
			),
			4 => CArray.array(
				"title" => "5. Pre-Installation summary",
				"fnc" => "stage5"
			),
			5 => CArray.array(
				"title" => "6. Install",
				"fnc" => "stage6"
			)
		);

		eventHandler();

		parent::__construct("post");
	}

	function getConfig($name, $default = null) {
		return isset(ZBX_CONFIG[$name]) ? ZBX_CONFIG[$name] : $default;
	}

	function setConfig($name, $value) {
		return (ZBX_CONFIG[$name] = $value);
	}

	function getStep() {
		return getConfig("step", 0);
	}

	function doNext() {
		if (isset(stage[getStep() + 1])) {
			ZBX_CONFIG["step"]++;

			return true;
		}

		return false;
	}

	function doBack() {
		if (isset(stage[getStep() - 1])) {
			ZBX_CONFIG["step"]--;

			return true;
		}

		return false;
	}

	function bodyToString($destroy = true) {
		$left = new CDiv(null, "left");
		$left.addItem(new CDiv(getList(), "left_menu"));

		$link1 = new CLink("www.zabbix.com", "http://www.zabbix.com/", null, null, true);
		$link1.setAttribute("target", "_blank");

		$link2 = new CLink("GPL v2", "http://www.zabbix.com/license.php", null, null, true);
		$link2.setAttribute("target", "_blank");

		$licence = new CDiv(CArray.array($link1, BR(), " Licensed under ", $link2), "setup_wizard_licence");
		$left.addItem($licence);

		$right = new CDiv(null, "right");
		if (getStep() == 0) {
			$right.addItem(new CDiv(null, "blank_title"));
			$right.addItem(new CDiv(getState(), "blank_under_title"));
			$container = new CDiv(CArray.array($left, $right), "setup_wizard setup_wizard_welcome");
		}
		else {
			$right.addItem(new CDiv(stage[getStep()]["title"], "setup_title"));
			$right.addItem(new CDiv(getState(), "under_title"));
			$container = new CDiv(CArray.array($left, $right), "setup_wizard");
		}

		if (isset(stage[getStep() + 1])) {
			$next = new CSubmit("next[".getStep()."]", _("Next").SPACE."&raquo;");
		}
		else {
			$next = new CSubmit("finish", _("Finish"));
		}

		if (isset(HIDE_CANCEL_BUTTON) && HIDE_CANCEL_BUTTON) {
			$cancel = null;
		}
		else {
			$cancel = new CDiv(new CSubmit("cancel", _("Cancel")), "footer_left");
		}

		if (DISABLE_NEXT_BUTTON) {
			$next.setEnabled(false);
		}

		// if the user is not logged in (first setup run) hide the \"previous\" button on the final step
		if (getStep()
				&& ((CWebUser::$data && CWebUser::getType() == USER_TYPE_SUPER_ADMIN) || getStep() < 5)) {
			$back = new CSubmit("back[".getStep()."]", "&laquo;".SPACE._("Previous"));
		}
		else {
			$back = null;
		}

		$footer = new CDiv(CArray.array($cancel, new CDiv(CArray.array($back, $next), "footer_right")), "footer");

		$container.addItem($footer);

		return parent::bodyToString($destroy).$container.ToString();
	}

	function getList() {
		$list = new CList();
		for(stage as $id => $data) {
			if ($id < getStep()) {
				$style = "completed";
			}
			elseif ($id == getStep() && getStep() != 0) {
				$style = "current";
			}
			else {
				$style = null;
			}

			$list.addItem(Nest.value($data,"title").$(), $style);
		}
		return $list;
	}

	function getState() {
		$fnc = stage[getStep()]["fnc"];
		return $fnc();
	}

	function stage1() {
		return null;
	}

	function stage2() {
		$table = new CTable(null, "requirements");
		$table.setAlign("center");

		$finalResult = FrontendSetup::CHECK_OK;

		$table.addRow(CArray.array(
			SPACE,
			new CCol(_("Current value"), "header"),
			new CCol(_("Required"), "header")
		));

		$frontendSetup = new FrontendSetup();
		$reqs = $frontendSetup.checkRequirements();
		for($reqs as $req) {
			$result = null;

			// OK
			if (Nest.value($req,"result").$() == FrontendSetup::CHECK_OK) {
				$rowClass = "";
				$result = new CSpan(_("OK"), "ok");
			}
			// warning
			elseif (Nest.value($req,"result").$() == FrontendSetup::CHECK_WARNING) {
				$rowClass = "notice";
				$result = new CSpan(_x("Warning", "setup"), "link_menu notice");
				$result.setHint(Nest.value($req,"error").$());
			}
			// fatal error
			else {
				$rowClass = "fail";
				$result = new CSpan(_("Fail"), "link_menu fail");
				$result.setHint(Nest.value($req,"error").$());
			}

			$table.addRow(CArray.array(
				new CCol(
					Nest.value($req,"name").$(), "header"),
					Nest.value($req,"current").$(),
					Nest.value($req,"required").$() ? Nest.value($req,"required").$() : SPACE,
					$result
				),
				$rowClass
			);

			$finalResult = max($finalResult, Nest.value($req,"result").$());
		}

		// fatal error
		if ($finalResult == FrontendSetup::CHECK_FATAL) {
			DISABLE_NEXT_BUTTON = true;

			$message = CArray.array(
				_("Please correct all issues and press \"Retry\" button"),
				BR(),
				new CSubmit("retry", _("Retry"))
			);
		}
		// OK or warning
		else {
			DISABLE_NEXT_BUTTON = false;
			$message = CArray.array(new CSpan(_("OK"), "ok"));

			// add a warning message
			if ($finalResult == FrontendSetup::CHECK_WARNING) {
				$message[] = BR();
				$message[] = _("(with warnings)");
			}
		}

		return CArray.array(
			new CDiv(CArray.array(BR(), $table, BR()), "table_wraper"),
			new CDiv($message, "info_bar")
		);
	}

	function stage3() {
		$table = new CTable(null, "requirements");
		$table.setAlign("center");

		Nest.value($DB,"TYPE").$() = getConfig("DB_TYPE");

		$cmbType = new CComboBox("type", Nest.value($DB,"TYPE").$(), "this.form.submit();");

		$frontendSetup = new FrontendSetup();
		$databases = $frontendSetup.getSupportedDatabases();

		for($databases as $id => $name) {
			$cmbType.addItem($id, $name);
		}
		$table.addRow(CArray.array(new CCol(_("Database type"), "header"), $cmbType));

		switch (Nest.value($DB,"TYPE").$()) {
			case ZBX_DB_SQLITE3:
				$database = new CTextBox("database", getConfig("DB_DATABASE", "zabbix"));
				$database.attr("onchange", \"disableSetupStepButton("#next_2")\");
				$table.addRow(CArray.array(
					new CCol(_("Database file"), "header"),
					$database
				));
			break;
			default:
				$server = new CTextBox("server", getConfig("DB_SERVER", "localhost"));
				$server.attr("onchange", \"disableSetupStepButton("#next_2")\");
				$table.addRow(CArray.array(
					new CCol(_("Database host"), "header"),
					$server
				));

				$port = new CNumericBox("port", getConfig("DB_PORT", "0"), 5, "no", false, false);
				$port.attr("style", "");
				$port.attr(
					"onchange",
					\"disableSetupStepButton("#next_2"); validateNumericBox(this, "false", "false");\"
				);

				$table.addRow(CArray.array(
					new CCol(_("Database port"), "header"),
					CArray.array($port, " 0 - use default port")
				));

				$database = new CTextBox("database", getConfig("DB_DATABASE", "zabbix"));
				$database.attr("onchange", \"disableSetupStepButton("#next_2")\");

				$table.addRow(CArray.array(
					new CCol(_("Database name"), "header"),
					$database
				));

				if (Nest.value($DB,"TYPE").$() == ZBX_DB_DB2) {
					$schema = new CTextBox("schema", getConfig("DB_SCHEMA", ""));
					$schema.attr("onchange", \"disableSetupStepButton("#next_2")\");
					$table.addRow(CArray.array(
						new CCol(_("Database schema"), "header"),
						$schema
					));
				}

				$user = new CTextBox("user", getConfig("DB_USER", "root"));
				$user.attr("onchange", \"disableSetupStepButton("#next_2")\");
				$table.addRow(CArray.array(
					new CCol(_("User"), "header"),
					$user
				));

				$password = new CPassBox("password", getConfig("DB_PASSWORD", ""));
				$password.attr("onchange", \"disableSetupStepButton("#next_2")\");
				$table.addRow(CArray.array(
					new CCol(_("Password"), "header"),
					$password
				));
			break;
		}

		global $ZBX_MESSAGES;
		if (!empty($ZBX_MESSAGES)) {
			$lst_error = new CList(null, "messages");
			for($ZBX_MESSAGES as $msg) {
				$lst_error.addItem(Nest.value($msg,"message").$(), Nest.value($msg,"type").$());
			}

			$table = CArray.array($table, $lst_error);
		}

		return CArray.array(
			new CDiv(new CDiv(CArray.array(
				"Please create database manually, and set the configuration parameters for connection to this database.", BR(), BR(),
				"Press \"Test connection\" button when done.", BR(),
				$table
			), "vertical_center"), "table_wraper"),

			new CDiv(CArray.array(
				isset(Nest.value(_REQUEST,"retry").$()) ? !DISABLE_NEXT_BUTTON ?
					new CSpan(CArray.array(_("OK"), BR()), "ok")
					: new CSpan(CArray.array(_("Fail"), BR()), "fail")
					: null,
				new  CSubmit("retry", "Test connection")
			), "info_bar")

		);
	}

	function stage4() {
		$table = new CTable(null, "requirements");
		$table.setAlign("center");

		$table.addRow(CArray.array(
			new CCol(_("Host"), "header"),
			new CTextBox("zbx_server", getConfig("ZBX_SERVER", "localhost"))
		));

		$port = new CNumericBox(
			"zbx_server_port",
			getConfig("ZBX_SERVER_PORT", "10051"),
			20,
			"no",
			false,
			false
		);
		$port.attr("style", "");
		$table.addRow(CArray.array(
			new CCol(_("Port"), "header"),
			$port
		));

		$table.addRow(CArray.array(
			new CCol(_("Name"), "header"),
			new CTextBox("zbx_server_name", getConfig("ZBX_SERVER_NAME", ""))
		));

		return CArray.array(
			"Please enter host name or host IP address", BR(),
			"and port number of Zabbix server,", BR(),
			"as well as the name of the installation (optional).", BR(), BR(),
			$table,
		);
	}

	function stage5() {
		$dbType = getConfig("DB_TYPE");
		$frontendSetup = new FrontendSetup();
		$databases = $frontendSetup.getSupportedDatabases();

		$table = new CTable(null, "requirements");
		$table.setAlign("center");
		$table.addRow(CArray.array(
			new CCol(_("Database type"), "header"),
			$databases[$dbType]
		));

		switch ($dbType) {
			case ZBX_DB_SQLITE3:
				$table.addRow(CArray.array(
					new CCol(_("Database file"), "header"),
					getConfig("DB_DATABASE")
				));
				break;
			default:
				$table.addRow(CArray.array(new CCol(_("Database server"), "header"), getConfig("DB_SERVER")));
				$dbPort = getConfig("DB_PORT");
				$table.addRow(CArray.array(
					new CCol(_("Database port"), "header"),
					($dbPort == 0) ? _("default") : $dbPort
				));
				$table.addRow(CArray.array(new CCol(_("Database name"), "header"), getConfig("DB_DATABASE")));
				$table.addRow(CArray.array(new CCol(_("Database user"), "header"), getConfig("DB_USER")));
				$table.addRow(CArray.array(new CCol(_("Database password"), "header"), preg_replace("/./", "*", getConfig("DB_PASSWORD"))));
				if ($dbType == ZBX_DB_DB2) {
					$table.addRow(CArray.array(new CCol(_("Database schema"), "header"), getConfig("DB_SCHEMA")));
				}
				break;
		}

		$table.addRow(BR());
		$table.addRow(CArray.array(new CCol(_("Zabbix server"), "header"), getConfig("ZBX_SERVER")));
		$table.addRow(CArray.array(new CCol(_("Zabbix server port"), "header"), getConfig("ZBX_SERVER_PORT")));
		$table.addRow(CArray.array(new CCol(_("Zabbix server name"), "header"), getConfig("ZBX_SERVER_NAME")));

		return CArray.array(
			"Please check configuration parameters.", BR(),
			"If all is correct, press \"Next\" button, or \"Previous\" button to change configuration parameters.", BR(), BR(),
			$table
		);
	}

	function stage6() {
		setConfig("ZBX_CONFIG_FILE_CORRECT", true);

		$config = new CConfigFile(Z::getInstance().getRootDir().CConfigFile::CONFIG_FILE_PATH);
		$config.config = CArray.array(
			"DB" => CArray.array(
				"TYPE" => getConfig("DB_TYPE"),
				"SERVER" => getConfig("DB_SERVER"),
				"PORT" => getConfig("DB_PORT"),
				"DATABASE" => getConfig("DB_DATABASE"),
				"USER" => getConfig("DB_USER"),
				"PASSWORD" => getConfig("DB_PASSWORD"),
				"SCHEMA" => getConfig("DB_SCHEMA")
			),
			"ZBX_SERVER" => getConfig("ZBX_SERVER"),
			"ZBX_SERVER_PORT" => getConfig("ZBX_SERVER_PORT"),
			"ZBX_SERVER_NAME" => getConfig("ZBX_SERVER_NAME")
		);
		$config.save();

		try {
			$error = false;
			$config.load();

			if ($config.Nest.value(config,"DB","TYPE").$() != getConfig("DB_TYPE")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"DB","SERVER").$() != getConfig("DB_SERVER")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"DB","PORT").$() != getConfig("DB_PORT")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"DB","DATABASE").$() != getConfig("DB_DATABASE")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"DB","USER").$() != getConfig("DB_USER")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"DB","PASSWORD").$() != getConfig("DB_PASSWORD")) {
				$error = true;
			}
			elseif (getConfig("DB_TYPE") == ZBX_DB_DB2 && $config.Nest.value(config,"DB","SCHEMA").$() != getConfig("DB_SCHEMA")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"ZBX_SERVER").$() != getConfig("ZBX_SERVER")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"ZBX_SERVER_PORT").$() != getConfig("ZBX_SERVER_PORT")) {
				$error = true;
			}
			elseif ($config.Nest.value(config,"ZBX_SERVER_NAME").$() != getConfig("ZBX_SERVER_NAME")) {
				$error = true;
			}
			$error_text = "Unable to overwrite the existing configuration file. ";
		}
		catch (ConfigFileException $e) {
			$error = true;
			$error_text = "Unable to create the configuration file. ";
		}

		clear_messages();
		if ($error) {
			setConfig("ZBX_CONFIG_FILE_CORRECT", false);
		}

		DISABLE_NEXT_BUTTON = !getConfig("ZBX_CONFIG_FILE_CORRECT", false);
		HIDE_CANCEL_BUTTON = !DISABLE_NEXT_BUTTON;


		$table = CArray.array("Configuration file", BR(), "\"".Z::getInstance().getRootDir().CConfigFile::CONFIG_FILE_PATH."\"",
			BR(), "created: ", getConfig("ZBX_CONFIG_FILE_CORRECT", false)
			? new CSpan(_("OK"), "ok")
			: new CSpan(_("Fail"), "fail")
		);

		return CArray.array(
			$table, BR(), BR(),
			DISABLE_NEXT_BUTTON ? CArray.array(new CSubmit("retry", _("Retry")), BR(), BR()) : null,
			!getConfig("ZBX_CONFIG_FILE_CORRECT", false)
				? CArray.array($error_text, BR(), "Please install it manually, or fix permissions on the conf directory.", BR(), BR(),
					"Press the \"Download configuration file\" button, download the configuration file ",
					"and save it as ", BR(),
					"\"".Z::getInstance()->getRootDir().CConfigFile::CONFIG_FILE_PATH."\"", BR(), BR(),
					new CSubmit("save_config", "Download configuration file"),
					BR(), BR()
				)
				: CArray.array(
					"Congratulations on successful installation of Zabbix frontend.", BR(), BR()
				),
			"When done, press the ".(DISABLE_NEXT_BUTTON ? "\"Retry\"" : "\"Finish\"")." button"
		);
	}

	function checkConnection() {
		global $DB;

		if (!getConfig("check_fields_result")) {
			return false;
		}

		Nest.value($DB,"TYPE").$() = getConfig("DB_TYPE");
		if (is_null(Nest.value($DB,"TYPE").$())) {
			return false;
		}

		Nest.value($DB,"SERVER").$() = getConfig("DB_SERVER", "localhost");
		Nest.value($DB,"PORT").$() = getConfig("DB_PORT", "0");
		Nest.value($DB,"DATABASE").$() = getConfig("DB_DATABASE", "zabbix");
		Nest.value($DB,"USER").$() = getConfig("DB_USER", "root");
		Nest.value($DB,"PASSWORD").$() = getConfig("DB_PASSWORD", "");
		Nest.value($DB,"SCHEMA").$() = getConfig("DB_SCHEMA", "");

		$error = "";

		// during setup set debug to false to avoid displaying unwanted PHP errors in messages
		if (!$result = DBconnect($error)) {
			error($error);
		}
		else {
			$result = true;
			if (!zbx_empty(Nest.value($DB,"SCHEMA").$()) && Nest.value($DB,"TYPE").$() == ZBX_DB_DB2) {
				$db_schema = DBselect("SELECT schemaname FROM syscat.schemata WHERE schemaname=\"".db2_escape_string(Nest.value($DB,"SCHEMA").$())."\"");
				$result = DBfetch($db_schema);
			}

			if ($result) {
				$result = DBexecute("CREATE TABLE zabbix_installation_test (test_row INTEGER)");
				$result &= DBexecute("DROP TABLE zabbix_installation_test");
			}
		}

		DBclose();

		$DB = null;
		return $result;
	}

	function eventHandler() {
		if (isset(_REQUEST["back"][getStep()])) {
			doBack();
		}

		if (getStep() == 1) {
			if (isset(_REQUEST["next"][getStep()])) {
				doNext();
			}
			DISABLE_NEXT_BUTTON = true;
		}
		elseif (getStep() == 2) {
			setConfig("DB_TYPE", get_request("type", getConfig("DB_TYPE")));
			setConfig("DB_SERVER", get_request("server", getConfig("DB_SERVER", "localhost")));
			setConfig("DB_PORT", get_request("port", getConfig("DB_PORT", "0")));
			setConfig("DB_DATABASE", get_request("database", getConfig("DB_DATABASE", "zabbix")));
			setConfig("DB_USER", get_request("user", getConfig("DB_USER", "root")));
			setConfig("DB_PASSWORD", get_request("password", getConfig("DB_PASSWORD", "")));
			setConfig("DB_SCHEMA", get_request("schema", getConfig("DB_SCHEMA", "")));

			if (isset(Nest.value(_REQUEST,"retry").$())) {
				if (!checkConnection()) {
					DISABLE_NEXT_BUTTON = true;
					unset(Nest.value(_REQUEST,"next").$());
				}
			}
			elseif (!isset(_REQUEST["next"][getStep()])) {
				DISABLE_NEXT_BUTTON = true;
				unset(Nest.value(_REQUEST,"next").$());
			}

			if (isset(_REQUEST["next"][getStep()])) {
				doNext();
			}
		}
		elseif (getStep() == 3) {
			setConfig("ZBX_SERVER", get_request("zbx_server", getConfig("ZBX_SERVER", "localhost")));
			setConfig("ZBX_SERVER_PORT", get_request("zbx_server_port", getConfig("ZBX_SERVER_PORT", "10051")));
			setConfig("ZBX_SERVER_NAME", get_request("zbx_server_name", getConfig("ZBX_SERVER_NAME", "")));
			if (isset(_REQUEST["next"][getStep()])) {
				doNext();
			}
		}
		elseif (getStep() == 4 && isset(_REQUEST["next"][getStep()])) {
			doNext();
		}
		elseif (getStep() == 5) {
			if (isset(Nest.value(_REQUEST,"save_config").$())) {
				// make zabbix.conf.php downloadable
				header("Content-Type: application/x-httpd-php");
				header("Content-Disposition: attachment; filename=\"".basename(CConfigFile::CONFIG_FILE_PATH)."\"");
				$config = new CConfigFile(Z::getInstance().getRootDir().CConfigFile::CONFIG_FILE_PATH);
				$config.config = CArray.array(
					"DB" => CArray.array(
						"TYPE" => getConfig("DB_TYPE"),
						"SERVER" => getConfig("DB_SERVER"),
						"PORT" => getConfig("DB_PORT"),
						"DATABASE" => getConfig("DB_DATABASE"),
						"USER" => getConfig("DB_USER"),
						"PASSWORD" => getConfig("DB_PASSWORD"),
						"SCHEMA" => getConfig("DB_SCHEMA")
					),
					"ZBX_SERVER" => getConfig("ZBX_SERVER"),
					"ZBX_SERVER_PORT" => getConfig("ZBX_SERVER_PORT"),
					"ZBX_SERVER_NAME" => getConfig("ZBX_SERVER_NAME")
				);
				die($config.getString());
			}
		}

		if (isset(_REQUEST["next"][getStep()])) {
			doNext();
		}
	}
}
