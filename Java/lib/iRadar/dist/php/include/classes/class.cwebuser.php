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


class CWebUser {

	public static $data = null;

	public static function login($login, $password) {
		try {
			self::setDefault();

			self::$data = API.User().login(CArray.array(
				"user" => $login,
				"password" => $password,
				"userData" => true
			));

			if (!self::$data) {
				throw new Exception();
			}

			if (self::Nest.value($data,"gui_access").$() == GROUP_GUI_ACCESS_DISABLED) {
				error(_("GUI access disabled."));
				throw new Exception();
			}

			if (empty(self::Nest.value($data,"url").$())) {
				self::Nest.value($data,"url").$() = CProfile::get("web.menu.view.last", "index.php");
			}

			if (isset(self::Nest.value($data,"attempt_failed").$()) && self::Nest.value($data,"attempt_failed").$()) {
				CProfile::init();
				CProfile::update("web.login.attempt.failed", self::Nest.value($data,"attempt_failed").$(), PROFILE_TYPE_INT);
				CProfile::update("web.login.attempt.ip", self::Nest.value($data,"attempt_ip").$(), PROFILE_TYPE_STR);
				CProfile::update("web.login.attempt.clock", self::Nest.value($data,"attempt_clock").$(), PROFILE_TYPE_INT);
				CProfile::flush();
			}

			// remove guest session after successful login
			DBexecute("DELETE FROM sessions WHERE sessionid=".zbx_dbstr(get_cookie("zbx_sessionid")));

			zbx_setcookie("zbx_sessionid", self::Nest.value($data,"sessionid").$(), self::Nest.value($data,"autologin").$() ? time() + SEC_PER_DAY * 31 : 0);

			return true;
		}
		catch (Exception $e) {
			self::setDefault();
			return false;
		}
	}

	public static function logout() {
		self::Nest.value($data,"sessionid").$() = get_cookie("zbx_sessionid");
		self::$data = API.User().logout();
		zbx_unsetcookie("zbx_sessionid");
	}

	public static function checkAuthentication($sessionid) {
		try {
			if ($sessionid !== null) {
				self::$data = API.User().checkAuthentication($sessionid);
			}

			if ($sessionid === null || empty(self::$data)) {
				self::setDefault();
				self::$data = API.User().login(CArray.array(
					"user" => ZBX_GUEST_USER,
					"password" => "",
					"userData" => true
				));

				if (empty(self::$data)) {
					clear_messages(1);
					throw new Exception();
				}
				$sessionid = self::Nest.value($data,"sessionid").$();
			}

			if (self::Nest.value($data,"gui_access").$() == GROUP_GUI_ACCESS_DISABLED) {
				error(_("GUI access disabled."));
				throw new Exception();
			}

			zbx_setcookie("zbx_sessionid", $sessionid, self::Nest.value($data,"autologin").$() ? time() + SEC_PER_DAY * 31 : 0);

			return true;
		}
		catch (Exception $e) {
			self::setDefault();
			return false;
		}
	}

	public static function setDefault() {
		self::$data = CArray.array(
			"alias" => ZBX_GUEST_USER,
			"userid" => 0,
			"lang" => "en_gb",
			"type" => "0",
			"node" => CArray.array("name" => "- unknown -", "nodeid" => 0)
		);
	}

	/**
	 * Returns the type of the current user.
	 *
	 * @static
	 *
	 * @return int
	 */
	public static function getType() {
		return self::Nest.value($data,"type").$();
	}

	/**
	 * Returns true if the current user is logged in.
	 *
	 * @return bool
	 */
	public static function isLoggedIn() {
		return (self::Nest.value($data,"userid").$());
	}

	/**
	 * Returns true if the user is not logged in or logged in as Guest.
	 *
	 * @return bool
	 */
	public static function isGuest() {
		return (self::Nest.value($data,"alias").$() == ZBX_GUEST_USER);
	}
}
