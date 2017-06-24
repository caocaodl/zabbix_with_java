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


define("ZBX_PAGE_NO_AUTHORIZATION", true);
define("ZBX_NOT_ALLOW_ALL_NODES", true);
define("ZBX_HIDE_NODE_SELECTION", true);

require_once dirname(__FILE__)."/include/config.inc.php";
require_once dirname(__FILE__)."/include/forms.inc.php";

Nest.value($page,"title").$() = _("ZABBIX");
Nest.value($page,"file").$() = "index.php";

// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
$fields = CArray.array(
	"name" =>		CArray.array(T_ZBX_STR, O_NO,	null,	NOT_EMPTY,		"isset({enter})", _("Username")),
	"password" =>	CArray.array(T_ZBX_STR, O_OPT, null,	null,			"isset({enter})"),
	"sessionid" =>	CArray.array(T_ZBX_STR, O_OPT, null,	null,			null),
	"reconnect" =>	CArray.array(T_ZBX_INT, O_OPT, P_SYS|P_ACT,	BETWEEN(0, 65535), null),
	"enter" =>		CArray.array(T_ZBX_STR, O_OPT, P_SYS,	null,			null),
	"autologin" =>	CArray.array(T_ZBX_INT, O_OPT, null,	null,			null),
	"request" =>	CArray.array(T_ZBX_STR, O_OPT, null,	null,			null)
);
check_fields($fields);

// logout
if (isset(Nest.value(_REQUEST,"reconnect").$())) {
	add_audit(AUDIT_ACTION_LOGOUT, AUDIT_RESOURCE_USER, _("Manual Logout"));
	CWebUser::logout();
	redirect("index.php");
}

$config = select_config();

if (Nest.value($config,"authentication_type").$() == ZBX_AUTH_HTTP) {
	if (!empty(Nest.value($_SERVER,"PHP_AUTH_USER").$())) {
		Nest.value(_REQUEST,"enter").$() = _("Sign in");
		Nest.value(_REQUEST,"name").$() = Nest.value($_SERVER,"PHP_AUTH_USER").$();
	}
	else {
		access_deny(ACCESS_DENY_PAGE);
	}
}

// login via form
if (isset(Nest.value(_REQUEST,"enter").$()) && Nest.value(_REQUEST,"enter").$() == _("Sign in")) {
	// try to login
	if (CWebUser::login(get_request("name", ""), get_request("password", ""))) {
		// save remember login preference
		$user = CArray.array("autologin" => get_request("autologin", 0));
		if (CWebUser::Nest.value($data,"autologin").$() != Nest.value($user,"autologin").$()) {
			$result = API.User().updateProfile($user);
		}
		add_audit_ext(AUDIT_ACTION_LOGIN, AUDIT_RESOURCE_USER, CWebUser::Nest.value($data,"userid").$(), "", null, null, null);

		$request = get_request("request");
		$url = zbx_empty($request) ? CWebUser::Nest.value($data,"url").$() : $request;
		if (zbx_empty($url) || $url == Nest.value($page,"file").$()) {
			$url = "dashboard.php";
		}
		redirect($url);
		exit();
	}
	// login failed, fall back to a guest account
	else {
		CWebUser::checkAuthentication(null);
	}
}
else {
	// login the user from the session, if the session id is empty - login as a guest
	CWebUser::checkAuthentication(get_cookie("zbx_sessionid"));
}

// the user is not logged in, display the login form
if (!CWebUser::Nest.value($data,"alias").$() || CWebUser::Nest.value($data,"alias").$() == ZBX_GUEST_USER) {
	switch (Nest.value($config,"authentication_type").$()) {
		case ZBX_AUTH_HTTP:
			echo _("User name does not match with DB");
			break;
		case ZBX_AUTH_LDAP:
		case ZBX_AUTH_INTERNAL:
			if (isset(Nest.value(_REQUEST,"enter").$())) {
				Nest.value(_REQUEST,"autologin").$() = get_request("autologin", 0);
			}

			if ($messages = clear_messages()) {
				$messages = array_pop($messages);
				Nest.value(_REQUEST,"message").$() = Nest.value($messages,"message").$();
			}
			$loginForm = new CView("general.login");
			$loginForm.render();
	}
}
else {
	redirect(zbx_empty(CWebUser::Nest.value($data,"url").$()) ? "dashboard.php" : CWebUser::Nest.value($data,"url").$());
}
