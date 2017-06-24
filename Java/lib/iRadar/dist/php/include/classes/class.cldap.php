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


class CLdap {

	public function __construct($arg = CArray.array()) {
		ds = false;
		info = CArray.array();
		cnf = CArray.array(
			"host" => "ldap://localhost",
			"port" => "389",
			"bind_dn" => "uid=admin,ou=system",
			"bind_password" => "",
			"base_dn" => "ou=users,ou=system",
			"search_attribute" => "uid",
			"userfilter" => "(%{attr}=%{user})",
			"groupkey" => "cn",
			"mapping" => CArray.array(
				"alias" => "uid",
				"userid" => "uidnumbera",
				"passwd" => "userpassword"
			),
			"referrals" => 0,
			"version" => 3,
			"starttls" => null,
			"deref" => null
		);

		if (is_array($arg)) {
			cnf = zbx_array_merge(cnf, $arg);
		}

		if (!function_exists("ldap_connect")) {
			error("LDAP lib error. Cannot find needed functions.");
			return false;
		}
	}

	public function connect() {
		// connection already established
		if (ds) {
			return true;
		}

		bound = 0;

		if (!ds = ldap_connect(Nest.value(cnf,"host").$(), Nest.value(cnf,"port").$())) {
			error("LDAP: couldn\"t connect to LDAP server.');

			return false;
		}

		// set protocol version and dependend options
		if (Nest.value(cnf,"version").$()) {
			if (!ldap_set_option(ds, LDAP_OPT_PROTOCOL_VERSION, Nest.value(cnf,"version").$())) {
				error("Setting LDAP Protocol version ".cnf["version"]." failed.");
			}
			else {
				// use TLS (needs version 3)
				if (isset(Nest.value(cnf,"starttls").$()) && !ldap_start_tls(ds)) {
					error("Starting TLS failed.");
				}

				// needs version 3
				if (!zbx_empty(Nest.value(cnf,"referrals").$())
						&& !ldap_set_option(ds, LDAP_OPT_REFERRALS, Nest.value(cnf,"referrals").$())) {
					error("Setting LDAP referrals to off failed.");
				}
			}
		}

		// set deref mode
		if (isset(Nest.value(cnf,"deref").$()) && !ldap_set_option(ds, LDAP_OPT_DEREF, Nest.value(cnf,"deref").$())) {
			error("Setting LDAP Deref mode ".cnf["deref"]." failed.");
		}

		return true;
	}

	public function checkPass($user, $pass) {
		if (!$pass) {
			return false;
		}

		if (!connect()) {
			return false;
		}

		$dn = null;

		// indirect user bind
		if (!empty(Nest.value(cnf,"bind_dn").$()) && !empty(Nest.value(cnf,"bind_password").$())) {
			// use superuser credentials
			if (!ldap_bind(ds, Nest.value(cnf,"bind_dn").$(), Nest.value(cnf,"bind_password").$())) {
				error("LDAP: cannot bind by given Bind DN.");

				return false;
			}

			bound = 2;
		}
		elseif (!empty(Nest.value(cnf,"bind_dn").$()) && !empty(Nest.value(cnf,"base_dn").$()) && !empty(Nest.value(cnf,"userfilter").$())) {
			// special bind string
			$dn = makeFilter(Nest.value(cnf,"bind_dn").$(), CArray.array("user" => $user, "host" => Nest.value(cnf,"host").$()));
		}
		elseif (zbx_strpos(Nest.value(cnf,"base_dn").$(), "%{user}")) {
			// direct user bind
			$dn = makeFilter(Nest.value(cnf,"base_dn").$(), CArray.array("user" => $user, "host" => Nest.value(cnf,"host").$()));
		}
		else {
			// anonymous bind
			if (!ldap_bind(ds)) {
				error("LDAP: can not bind anonymously.");

				return false;
			}
		}

		// try to bind to with the dn if we have one.
		if ($dn) {
			// user/password bind
			if (!ldap_bind(ds, $dn, $pass)) {
				return false;
			}

			bound = 1;

			return true;
		}
		else {
			// see if we can find the user
			info = getUserData($user);

			if (empty(Nest.value(info,"dn").$())) {
				return false;
			}
			else {
				$dn = Nest.value(info,"dn").$();
			}

			// try to bind with the dn provided
			if (!ldap_bind(ds, $dn, $pass)) {
				return false;
			}

			bound = 1;

			return true;
		}

		return false;
	}

	private function getUserData($user) {
		if (!connect()) {
			return false;
		}

		// force superuser bind if wanted and not bound as superuser yet
		if (!empty(Nest.value(cnf,"bind_dn").$()) && !empty(Nest.value(cnf,"bind_password").$()) && (bound < 2)) {
			if (!ldap_bind(ds, Nest.value(cnf,"bind_dn").$(), Nest.value(cnf,"bind_password").$())) {
				return false;
			}
			bound = 2;
		}

		// with no superuser creds we continue as user or anonymous here
		Nest.value($info,"user").$() = $user;
		Nest.value($info,"host").$() = Nest.value(cnf,"host").$();

		// get info for given user
		$base = makeFilter(Nest.value(cnf,"base_dn").$(), $info);

		if (isset(Nest.value(cnf,"userfilter").$()) && !empty(Nest.value(cnf,"userfilter").$())) {
			$filter = makeFilter(Nest.value(cnf,"userfilter").$(), $info);
		}
		else {
			$filter = "(ObjectClass=*)";
		}
		$sr = ldap_search(ds, $base, $filter);
		$result = ldap_get_entries(ds, $sr);

		// don't accept more or less than one response
		if (Nest.value($result,"count").$() != 1) {
			error("LDAP: User not found.");
			return false;
		}

		$user_result = $result[0];
		ldap_free_result($sr);

		// general user info
		Nest.value($info,"dn").$() = Nest.value($user_result,"dn").$();
		Nest.value($info,"name").$() = Nest.array($user_result,"cn").get(0);
		Nest.value($info,"grps").$() = CArray.array();

		// overwrite if other attribs are specified.
		if (is_array(Nest.value(cnf,"mapping").$())) {
			for(Nest.value(cnf,"mapping").$() as $localkey => $key) {
				$info[$localkey] = isset($user_result[$key])?$user_result[$key][0]:null;
			}
		}
		$user_result = zbx_array_merge($info,$user_result);

		// get groups for given user if grouptree is given
		if (isset(Nest.value(cnf,"grouptree").$()) && isset(Nest.value(cnf,"groupfilter").$())) {
			$base = makeFilter(Nest.value(cnf,"grouptree").$(), $user_result);
			$filter = makeFilter(Nest.value(cnf,"groupfilter").$(), $user_result);
			$sr = ldap_search(ds, $base, $filter, CArray.array(Nest.value(cnf,"groupkey").$()));

			if (!$sr) {
				error("LDAP: Reading group memberships failed.");
				return false;
			}

			$result = ldap_get_entries(ds, $sr);

			for($result as $grp) {
				if (!empty($grp[cnf["groupkey"]][0])) {
					$info["grps"][] = $grp[cnf["groupkey"]][0];
				}
			}
		}

		// always add the default group to the list of groups
		if (isset(Nest.value($conf,"defaultgroup").$()) && !str_in_array(Nest.value($conf,"defaultgroup").$(), Nest.value($info,"grps").$())) {
			$info["grps"][] = Nest.value($conf,"defaultgroup").$();
		}

		return $info;
	}

	private function makeFilter($filter, $placeholders) {
		Nest.value($placeholders,"attr").$() = Nest.value(cnf,"search_attribute").$();
		preg_match_all(\"/%{([^}]+)/\", $filter, $matches, PREG_PATTERN_ORDER);

		// replace each match
		for($matches[1] as $match) {
			// take first element if array
			if (is_array($placeholders[$match])) {
				$value = $placeholders[$match][0];
			}
			else {
				$value = $placeholders[$match];
			}
			$filter = str_replace("%{".$match."}", $value, $filter);
		}
		return $filter;
	}
}
