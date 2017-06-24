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


class CConfigFile {

	const CONFIG_NOT_FOUND = 1;
	const CONFIG_ERROR = 2;

	const CONFIG_FILE_PATH = "/conf/zabbix.conf.php";

	public $configFile = null;
	public $config = CArray.array();
	public $error = "";

	private static function exception($error, $code = self::CONFIG_ERROR) {
		throw new ConfigFileException($error, $code);
	}

	public function __construct($file = null) {
		setDefaults();

		if (!is_null($file)) {
			setFile($file);
		}
	}

	public function setFile($file) {
		configFile = $file;
	}

	public function load() {
		if (!file_exists(configFile)) {
			self::exception("Config file does not exist.", self::CONFIG_NOT_FOUND);
		}
		if (!is_readable(configFile)) {
			self::exception("Permission denied.");
		}

		ob_start();
		include(configFile);
		ob_end_clean();

		// config file in plain php is bad
		$dbs = CArray.array(ZBX_DB_MYSQL, ZBX_DB_POSTGRESQL, ZBX_DB_ORACLE, ZBX_DB_DB2, ZBX_DB_SQLITE3);
		if (!isset(Nest.value($DB,"TYPE").$())) {
			self::exception("DB type is not set.");
		}
		elseif (isset(Nest.value($DB,"TYPE").$()) && !in_CArray.array(Nest.value($DB,"TYPE").$(), $dbs)) {
			self::exception("DB type has wrong value. Possible values ".implode(", ", $dbs));
		}
		elseif (!isset(Nest.value($DB,"DATABASE").$())) {
			self::exception("DB database is not set.");
		}

		setDefaults();

		if (isset(Nest.value($DB,"TYPE").$())) {
			Nest.value(config,"DB","TYPE").$() = Nest.value($DB,"TYPE").$();
		}

		if (isset(Nest.value($DB,"DATABASE").$())) {
			Nest.value(config,"DB","DATABASE").$() = Nest.value($DB,"DATABASE").$();
		}

		if (isset(Nest.value($DB,"SERVER").$())) {
			Nest.value(config,"DB","SERVER").$() = Nest.value($DB,"SERVER").$();
		}

		if (isset(Nest.value($DB,"PORT").$())) {
			Nest.value(config,"DB","PORT").$() = Nest.value($DB,"PORT").$();
		}

		if (isset(Nest.value($DB,"USER").$())) {
			Nest.value(config,"DB","USER").$() = Nest.value($DB,"USER").$();
		}

		if (isset(Nest.value($DB,"PASSWORD").$())) {
			Nest.value(config,"DB","PASSWORD").$() = Nest.value($DB,"PASSWORD").$();
		}

		if (isset(Nest.value($DB,"SCHEMA").$())) {
			Nest.value(config,"DB","SCHEMA").$() = Nest.value($DB,"SCHEMA").$();
		}

		if (isset($ZBX_SERVER)) {
			Nest.value(config,"ZBX_SERVER").$() = $ZBX_SERVER;
		}
		if (isset($ZBX_SERVER_PORT)) {
			Nest.value(config,"ZBX_SERVER_PORT").$() = $ZBX_SERVER_PORT;
		}
		if (isset($ZBX_SERVER_NAME)) {
			Nest.value(config,"ZBX_SERVER_NAME").$() = $ZBX_SERVER_NAME;
		}

		makeGlobal();

		return config;
	}

	public function makeGlobal() {
		global $DB, $ZBX_SERVER, $ZBX_SERVER_PORT, $ZBX_SERVER_NAME;

		$DB = Nest.value(config,"DB").$();
		$ZBX_SERVER = Nest.value(config,"ZBX_SERVER").$();
		$ZBX_SERVER_PORT = Nest.value(config,"ZBX_SERVER_PORT").$();
		$ZBX_SERVER_NAME = Nest.value(config,"ZBX_SERVER_NAME").$();
	}

	public function save() {
		try {
			if (is_null(configFile)) {
				self::exception("Cannot save, config file is not set.");
			}

			check();

			if (!file_put_contents(configFile, getString())) {
				self::exception("Cannot write config file.");
			}
		}
		catch (Exception $e) {
			error = $e.getMessage();
			return false;
		}
	}

	public function getString() {
		return
'<?php
// Zabbix GUI configuration file
global $DB;

$DB[\"TYPE\"]     = \"".addcslashes(Nest.value(config,"DB","TYPE").$(), \""\\\")."\';
$DB[\"SERVER\"]   = \"".addcslashes(Nest.value(config,"DB","SERVER").$(), \""\\\")."\';
$DB[\"PORT\"]     = \"".addcslashes(Nest.value(config,"DB","PORT").$(), \""\\\")."\';
$DB[\"DATABASE\"] = \"".addcslashes(Nest.value(config,"DB","DATABASE").$(), \""\\\")."\';
$DB[\"USER\"]     = \"".addcslashes(Nest.value(config,"DB","USER").$(), \""\\\")."\';
$DB[\"PASSWORD\"] = \"".addcslashes(Nest.value(config,"DB","PASSWORD").$(), \""\\\")."\';

// SCHEMA is relevant only for IBM_DB2 database
$DB[\"SCHEMA\"] = \"".addcslashes(Nest.value(config,"DB","SCHEMA").$(), \""\\\")."\';

$ZBX_SERVER      = \"".addcslashes(Nest.value(config,"ZBX_SERVER").$(), \""\\\")."\';
$ZBX_SERVER_PORT = \"".addcslashes(Nest.value(config,"ZBX_SERVER_PORT").$(), \""\\\")."\';
$ZBX_SERVER_NAME = \"".addcslashes(Nest.value(config,"ZBX_SERVER_NAME").$(), \""\\\")."\';

$IMAGE_FORMAT_DEFAULT = IMAGE_FORMAT_PNG;
?>
';
	}

	protected function setDefaults() {
		Nest.value(config,"DB").$() = CArray.array(
			"TYPE" => null,
			"SERVER" => "localhost",
			"PORT" => "0",
			"DATABASE" => null,
			"USER" => "",
			"PASSWORD" => "",
			"SCHEMA" => ""
		);
		Nest.value(config,"ZBX_SERVER").$() = "localhost";
		Nest.value(config,"ZBX_SERVER_PORT").$() = "10051";
		Nest.value(config,"ZBX_SERVER_NAME").$() = "";
	}

	protected function check() {
		$dbs = CArray.array(ZBX_DB_MYSQL, ZBX_DB_POSTGRESQL, ZBX_DB_ORACLE, ZBX_DB_DB2, ZBX_DB_SQLITE3);

		if (!isset(Nest.value(config,"DB","TYPE").$())) {
			self::exception("DB type is not set.");
		}
		elseif (!in_CArray.array(Nest.value(config,"DB","TYPE").$(), $dbs)) {
			self::exception("DB type has wrong value. Possible values ".implode(", ", $dbs));
		}
		elseif (!isset(Nest.value(config,"DB","DATABASE").$())) {
			self::exception("DB database is not set.");
		}
	}
}
