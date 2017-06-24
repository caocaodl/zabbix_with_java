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


require_once dirname(__FILE__)."/CAutoloader.php";

class ZBase {
	const EXEC_MODE_DEFAULT = "default";
	const EXEC_MODE_SETUP = "setup";
	const EXEC_MODE_API = "api";

	/**
	 * An instance of the current Z object.
	 *
	 * @var Z
	 */
	protected static $instance;

	/**
	 * The absolute path to the root directory.
	 *
	 * @var string
	 */
	protected $rootDir;

	/**
	 * Session object.
	 *
	 * @var CSession
	 */
	protected $session;

	/**
	 * @var array of config data from zabbix config file
	 */
	protected $config = CArray.array();

	/**
	 * Returns the current instance of Z.
	 *
	 * @static
	 *
	 * @return Z
	 */
	public static function getInstance() {
		if (self::$instance === null) {
			self::$instance = new Z();
		}

		return self::$instance;
	}

	/**
	 * Init modules required to run frontend.
	 */
	protected function init() {
		rootDir = findRootDir();
		registerAutoloader();

		// system includes
		require_once getRootDir()."/include/debug.inc.php";
		require_once getRootDir()."/include/gettextwrapper.inc.php";
		require_once getRootDir()."/include/defines.inc.php";
		require_once getRootDir()."/include/func.inc.php";
		require_once getRootDir()."/include/html.inc.php";
		require_once getRootDir()."/include/perm.inc.php";
		require_once getRootDir()."/include/audit.inc.php";
		require_once getRootDir()."/include/js.inc.php";
		require_once getRootDir()."/include/users.inc.php";
		require_once getRootDir()."/include/validate.inc.php";
		require_once getRootDir()."/include/profiles.inc.php";
		require_once getRootDir()."/include/locales.inc.php";
		require_once getRootDir()."/include/db.inc.php";
		require_once getRootDir()."/include/nodes.inc.php";

		// page specific includes
		require_once getRootDir()."/include/acknow.inc.php";
		require_once getRootDir()."/include/actions.inc.php";
		require_once getRootDir()."/include/discovery.inc.php";
		require_once getRootDir()."/include/draw.inc.php";
		require_once getRootDir()."/include/events.inc.php";
		require_once getRootDir()."/include/graphs.inc.php";
		require_once getRootDir()."/include/hosts.inc.php";
		require_once getRootDir()."/include/httptest.inc.php";
		require_once getRootDir()."/include/ident.inc.php";
		require_once getRootDir()."/include/images.inc.php";
		require_once getRootDir()."/include/items.inc.php";
		require_once getRootDir()."/include/maintenances.inc.php";
		require_once getRootDir()."/include/maps.inc.php";
		require_once getRootDir()."/include/media.inc.php";
		require_once getRootDir()."/include/services.inc.php";
		require_once getRootDir()."/include/sounds.inc.php";
		require_once getRootDir()."/include/triggers.inc.php";
		require_once getRootDir()."/include/valuemap.inc.php";
	}

	/**
	 * Initializes the application.
	 */
	public function run($mode = self::EXEC_MODE_DEFAULT) {
		init();

		setMaintenanceMode();
		setErrorHandler();

		switch ($mode) {
			case self::EXEC_MODE_DEFAULT:
				loadConfigFile();
				initDB();
				initNodes();
				authenticateUser();
				// init nodes after user is authenticated
				init_nodes();
				initLocales();
				break;
			case self::EXEC_MODE_API:
				loadConfigFile();
				initDB();
				initNodes();
				initLocales();
				break;
			case self::EXEC_MODE_SETUP:
				try {
					// try to load config file, if it exists we need to init db and authenticate user to check permissions
					loadConfigFile();
					initDB();
					initNodes();
					authenticateUser();
					// init nodes after user is authenticated
					init_nodes();
					initLocales();
					DBclose();
				}
				catch (ConfigFileException $e) {}
				break;
		}
	}

	/**
	 * Returns the absolute path to the root dir.
	 *
	 * @return string
	 */
	public static function getRootDir() {
		return self::getInstance()->rootDir;
	}

	/**
	 * Returns the path to the frontend's root dir.
	 *
	 * @return string
	 */
	private function findRootDir() {
		return realpath(dirname(__FILE__)."/../../..");
	}

	/**
	 * Register autoloader.
	 */
	private function registerAutoloader() {
		$autoloader = new CAutoloader(getIncludePaths());
		$autoloader.register();
	}

	/**
	 * An array of directories to add to the autoloader include paths.
	 *
	 * @return array
	 */
	private function getIncludePaths() {
		return CArray.array(
			rootDir."/include/classes",
			rootDir."/include/classes/core",
			rootDir."/include/classes/api",
			rootDir."/include/classes/db",
			rootDir."/include/classes/debug",
			rootDir."/include/classes/validators",
			rootDir."/include/classes/validators/schema",
			rootDir."/include/classes/validators/string",
			rootDir."/include/classes/validators/object",
			rootDir."/include/classes/validators/hostgroup",
			rootDir."/include/classes/validators/host",
			rootDir."/include/classes/validators/hostprototype",
			rootDir."/include/classes/validators/event",
			rootDir."/include/classes/export",
			rootDir."/include/classes/export/writers",
			rootDir."/include/classes/export/elements",
			rootDir."/include/classes/graphdraw",
			rootDir."/include/classes/import",
			rootDir."/include/classes/import/importers",
			rootDir."/include/classes/import/readers",
			rootDir."/include/classes/import/formatters",
			rootDir."/include/classes/items",
			rootDir."/include/classes/server",
			rootDir."/include/classes/screens",
			rootDir."/include/classes/sysmaps",
			rootDir."/include/classes/helpers",
			rootDir."/include/classes/helpers/trigger",
			rootDir."/include/classes/macros",
			rootDir."/include/classes/tree",
			rootDir."/include/classes/html",
			rootDir."/include/classes/parsers",
			rootDir."/api/classes",
			rootDir."/api/classes/managers",
			rootDir."/api/rpc"
		);
	}

	/**
	 * An array of available themes.
	 *
	 * @return array
	 */
	public static function getThemes() {
		return CArray.array(
			"classic" => _("Classic"),
			"originalblue" => _("Original blue"),
			"darkblue" => _("Black & Blue"),
			"darkorange" => _("Dark orange")
		);
	}

	/**
	 * Return session object.
	 *
	 * @return CSession
	 */
	public function getSession() {
		if (session === null) {
			session = new CSession();
		}

		return session;
	}

	/**
	 * Set custom error handler for PHP errors.
	 */
	protected function setErrorHandler() {
		function zbx_err_handler($errno, $errstr, $errfile, $errline) {
			// necessary to surpress errors when calling with error control operator like @function_name()
			if (error_reporting() === 0) {
				return true;
			}

			// don't show the call to this handler function
			error($errstr." [".CProfiler::getInstance().formatCallStack()."]");
		}

		set_error_handler("zbx_err_handler");
	}

	/**
	 * Check if maintenance mode is enabled.
	 *
	 * @throws Exception
	 */
	protected function setMaintenanceMode() {
		require_once getRootDir()."/conf/maintenance.inc.php";

		if (defined("ZBX_DENY_GUI_ACCESS")) {
			$user_ip = (isset(Nest.value($_SERVER,"HTTP_X_FORWARDED_FOR").$()) && !empty(Nest.value($_SERVER,"HTTP_X_FORWARDED_FOR").$()))
					? $_SERVER["HTTP_X_FORWARDED_FOR"]
					: Nest.value($_SERVER,"REMOTE_ADDR").$();
			if (!isset($ZBX_GUI_ACCESS_IP_RANGE) || !in_CArray.array($user_ip, $ZBX_GUI_ACCESS_IP_RANGE)) {
				throw new Exception(Nest.value(_REQUEST,"warning_msg").$());
			}
		}
	}

	/**
	 * Load zabbix config file.
	 */
	protected function loadConfigFile() {
		$configFile = getRootDir().CConfigFile::CONFIG_FILE_PATH;
		$config = new CConfigFile($configFile);
		config = $config.load();
	}

	/**
	 * Check if frontend can connect to DB.
	 * @throws DBException
	 */
	protected function initDB() {
		$error = null;
		if (!DBconnect($error)) {
			throw new DBException($error);
		}
	}

	/**
	 * Check if distributed monitoring is enabled.
	 */
	protected function initNodes() {
		global $ZBX_LOCALNODEID, $ZBX_LOCMASTERID, $ZBX_NODES;

		if ($local_node_data = DBfetch(DBselect("SELECT n.* FROM nodes n WHERE n.nodetype=1 ORDER BY n.nodeid"))) {
			$ZBX_LOCALNODEID = Nest.value($local_node_data,"nodeid").$();
			$ZBX_LOCMASTERID = Nest.value($local_node_data,"masterid").$();
			$ZBX_NODES[$local_node_data["nodeid"]] = $local_node_data;
			define("ZBX_DISTRIBUTED", true);
		}
		else {
			define("ZBX_DISTRIBUTED", false);
		}
	}

	/**
	 * Initialize translations.
	 */
	protected function initLocales() {
		init_mbstrings();

		$defaultLocales = CArray.array(
			"C", "POSIX", "en", "en_US", "en_US.UTF-8", "English_United States.1252", "en_GB", "en_GB.UTF-8"
		);

		if (function_exists("bindtextdomain")) {
			// initializing gettext translations depending on language selected by user
			$locales = zbx_locale_variants(CWebUser::Nest.value($data,"lang").$());
			$locale_found = false;
			for($locales as $locale) {
				// since LC_MESSAGES may be unavailable on some systems, try to set all of the locales
				// and then revert some of them back
				putenv("LC_ALL=".$locale);
				putenv("LANG=".$locale);
				putenv("LANGUAGE=".$locale);
				setlocale(LC_TIME, $locale);

				if (setlocale(LC_ALL, $locale)) {
					$locale_found = true;
					CWebUser::Nest.value($data,"locale").$() = $locale;
					break;
				}
			}

			// reset the LC_CTYPE locale so that case transformation functions would work correctly
			// it is also required for PHP to work with the Turkish locale (https://bugs.php.net/bug.php?id=18556)
			// WARNING: this must be done before executing any other code, otherwise code execution could fail!
			// this will be unnecessary in PHP 5.5
			setlocale(LC_CTYPE, $defaultLocales);

			if (!$locale_found && CWebUser::Nest.value($data,"lang").$() != "en_GB" && CWebUser::Nest.value($data,"lang").$() != "en_gb") {
				error("Locale for language \"".CWebUser::$data["lang"]."\" is not found on the web server. Tried to set: ".implode(", ", $locales).". Unable to translate Zabbix interface.");
			}
			bindtextdomain("frontend", "locale");
			bind_textdomain_codeset("frontend", "UTF-8");
			textdomain("frontend");
		}

		// reset the LC_NUMERIC locale so that PHP would always use a point instead of a comma for decimal numbers
		setlocale(LC_NUMERIC, $defaultLocales);

		// should be after locale initialization
		require_once getRootDir()."/include/translateDefines.inc.php";
	}

	/**
	 * Authenticate user.
	 */
	protected function authenticateUser() {
		if (!CWebUser::checkAuthentication(get_cookie("zbx_sessionid"))) {
			CWebUser::setDefault();
		}
	}
}
