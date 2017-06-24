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

Nest.value($page,"title").$() = _("Configuration of authentication");
Nest.value($page,"file").$() = "authentication.php";
Nest.value($page,"hist_arg").$() = CArray.array("config");

require_once dirname(__FILE__)."/include/page_header.php";

//	VAR						TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
$fields = CArray.array(
	"config" =>			CArray.array(T_ZBX_INT, O_OPT, null, IN(ZBX_AUTH_INTERNAL.",".ZBX_AUTH_LDAP.",".ZBX_AUTH_HTTP), null),
	"form_refresh" =>	CArray.array(T_ZBX_INT, O_OPT, null,			null, null),
	"save" =>			CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT,	null, null),
	"test" =>			CArray.array(T_ZBX_STR, O_OPT, P_SYS|P_ACT,	null, null),
	// LDAP
	"ldap_host" =>		CArray.array(T_ZBX_STR, O_OPT, null,			NOT_EMPTY,
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))",	_("LDAP host")),
	"ldap_port" =>		CArray.array(T_ZBX_INT, O_OPT, null,			BETWEEN(0, 65535),
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))",	_("Port")),
	"ldap_base_dn" =>	CArray.array(T_ZBX_STR, O_OPT, null,			NOT_EMPTY,
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))",	_("Base DN")),
	"ldap_bind_dn" =>	CArray.array(T_ZBX_STR, O_OPT, null,			null,
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))"),
	"ldap_bind_password" => CArray.array(T_ZBX_STR, O_OPT, null,		null, null,				_("Bind password")),
	"ldap_search_attribute" => CArray.array(T_ZBX_STR, O_OPT, null,	NOT_EMPTY,
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))",	_("Search attribute")),
	"user" =>			CArray.array(T_ZBX_STR, O_OPT, null,			NOT_EMPTY,
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))"),
	"user_password" =>	CArray.array(T_ZBX_STR, O_OPT, null,			NOT_EMPTY,
		"isset({config})&&{config}==".ZBX_AUTH_LDAP."&&(isset({save})||isset({test}))",	_("User password")),
	"change_bind_password" => CArray.array(T_ZBX_STR, O_OPT, null, null,	null)
);
check_fields($fields);

$config = select_config();

if (isset(Nest.value(_REQUEST,"config").$())) {
	$isAuthenticationTypeChanged = (Nest.value($config,"authentication_type").$() != Nest.value(_REQUEST,"config").$());
	Nest.value($config,"authentication_type").$() = Nest.value(_REQUEST,"config").$();
}
else {
	$isAuthenticationTypeChanged = false;
}

for($config as $name => $value) {
	if (array_key_exists($name, _REQUEST)) {
		$config[$name] = _REQUEST[$name];
	}
}

/*
 * Actions
 */
if (Nest.value($config,"authentication_type").$() == ZBX_AUTH_INTERNAL) {
	if (isset(Nest.value(_REQUEST,"save").$())) {
		if (update_config($config)) {
			// reset all sessions
			if ($isAuthenticationTypeChanged) {
				DBexecute(
					"UPDATE sessions SET status=".ZBX_SESSION_PASSIVE.
					" WHERE sessionid<>".zbx_dbstr(CWebUser::Nest.value($data,"sessionid").$())
				);
			}

			$isAuthenticationTypeChanged = false;

			add_audit(AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ZABBIX_CONFIG,
				_("Authentication method changed to Zabbix internal")
			);

			show_message(_("Authentication method changed to Zabbix internal"));
		}
		else {
			show_error_message(_("Cannot change authentication method to Zabbix internal"));
		}
	}
}
elseif (Nest.value($config,"authentication_type").$() == ZBX_AUTH_LDAP) {
	if (isset(Nest.value(_REQUEST,"save").$()) || isset(Nest.value(_REQUEST,"test").$())) {
		// check LDAP login/password
		$ldapValidator = new CLdapAuthValidator(CArray.array(
			"conf" => CArray.array(
				"host" => Nest.value($config,"ldap_host").$(),
				"port" => Nest.value($config,"ldap_port").$(),
				"base_dn" => Nest.value($config,"ldap_base_dn").$(),
				"bind_dn" => Nest.value($config,"ldap_bind_dn").$(),
				"bind_password" => Nest.value($config,"ldap_bind_password").$(),
				"search_attribute" => $config["ldap_search_attribute"]
			)
		));

		$login = $ldapValidator.validate(CArray.array(
			"user" => get_request("user", CWebUser::Nest.value($data,"alias").$()),
			"password" => get_request("user_password", "")
		));

		if (!$login) {
			error(_("Login name or password is incorrect!"));
		}

		if (isset(Nest.value(_REQUEST,"save").$())) {
			if (!$login) {
				show_error_message(_("Cannot change authentication method to LDAP"));
			}
			else {
				if (update_config($config)) {
					unset(Nest.value(_REQUEST,"change_bind_password").$());

					// reset all sessions
					if ($isAuthenticationTypeChanged) {
						DBexecute(
							"UPDATE sessions SET status=".ZBX_SESSION_PASSIVE.
							" WHERE sessionid<>".zbx_dbstr(CWebUser::Nest.value($data,"sessionid").$())
						);
					}

					$msg = $isAuthenticationTypeChanged
						? _("Authentication method changed to LDAP")
						: _("LDAP authentication changed");

					$isAuthenticationTypeChanged = false;

					add_audit(AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ZABBIX_CONFIG, $msg);

					show_message($msg);
				}
				else {
					show_error_message(
						$isAuthenticationTypeChanged
							? _("Cannot change authentication method to LDAP")
							: _("Cannot change authentication")
					);
				}
			}
		}
	}
	elseif (isset(Nest.value(_REQUEST,"test").$())) {
		show_messages($login, _("LDAP login successful"), _("LDAP login was not successful"));
	}
}
elseif (Nest.value($config,"authentication_type").$() == ZBX_AUTH_HTTP) {
	if (isset(Nest.value(_REQUEST,"save").$())) {
		// get groups that use this authentication method
		$result = DBfetch(DBselect(
			"SELECT COUNT(g.usrgrpid) AS cnt_usrgrp FROM usrgrp g WHERE g.gui_access=".GROUP_GUI_ACCESS_INTERNAL
		));

		if (Nest.value($result,"cnt_usrgrp").$() > 0) {
			info(_n(
				"There is \"%1$d\" group with Internal GUI access.",
				"There are \"%1$d\" groups with Internal GUI access.",
				$result["cnt_usrgrp"]
			));
		}

		if (update_config($config)) {
			// reset all sessions
			if ($isAuthenticationTypeChanged) {
				DBexecute(
					"UPDATE sessions SET status=".ZBX_SESSION_PASSIVE.
					" WHERE sessionid<>".zbx_dbstr(CWebUser::Nest.value($data,"sessionid").$())
				);
			}

			$isAuthenticationTypeChanged = false;

			add_audit(AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_ZABBIX_CONFIG, _("Authentication method changed to HTTP"));

			show_message(_("Authentication method changed to HTTP"));
		}
		else {
			show_error_message(_("Cannot change authentication method to HTTP"));
		}
	}
}

show_messages();

/*
 * Display
 */
$data = CArray.array(
	"form_refresh" => get_request("form_refresh"),
	"config" => $config,
	"is_authentication_type_changed" => $isAuthenticationTypeChanged,
	"user" => get_request("user", CWebUser::Nest.value($data,"alias").$()),
	"user_password" => get_request("user_password", ""),
	"user_list" => null,
	"change_bind_password" => get_request("change_bind_password")
);

// get tab title
Nest.value($data,"title").$() = authentication2str(Nest.value($config,"authentication_type").$());

// get user list
if (getUserGuiAccess(CWebUser::Nest.value($data,"userid").$()) == GROUP_GUI_ACCESS_INTERNAL) {
	Nest.value($data,"user_list").$() = DBfetchArray(DBselect(
		"SELECT u.alias,u.userid".
		" FROM users u".
		whereDbNode("u.userid").
		" ORDER BY u.alias"
	));
}

// render view
$authenticationView = new CView("administration.authentication.edit", $data);
$authenticationView.render();
$authenticationView.show();

require_once dirname(__FILE__)."/include/page_footer.php";
