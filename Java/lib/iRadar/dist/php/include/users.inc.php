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


/**
 * Find user theme or get default theme.
 *
 * @param array $userData
 *
 * @return string
 */
function getUserTheme($userData) {
	$config = select_config();

	if (isset(Nest.value($config,"default_theme").$())) {
		$css = Nest.value($config,"default_theme").$();
	}
	if (isset(Nest.value($userData,"theme").$()) && Nest.value($userData,"theme").$() != THEME_DEFAULT) {
		$css = Nest.value($userData,"theme").$();
	}
	if (!isset($css)) {
		$css = ZBX_DEFAULT_THEME;
	}

	return $css;
}

/**
 * Get user type name.
 *
 * @param int $userType
 *
 * @return string
 */
function user_type2str($userType = null) {
	$userTypes = CArray.array(
		USER_TYPE_ZABBIX_USER => _("Zabbix User"),
		USER_TYPE_ZABBIX_ADMIN => _("Zabbix Admin"),
		USER_TYPE_SUPER_ADMIN => _("Zabbix Super Admin")
	);

	if ($userType === null) {
		return $userTypes;
	}
	elseif (isset($userTypes[$userType])) {
		return $userTypes[$userType];
	}
	else {
		return _("Unknown");
	}
}

/**
 * Get user authentication name.
 *
 * @param int $authType
 *
 * @return string
 */
function user_auth_type2str($authType) {
	if ($authType === null) {
		$authType = getUserGuiAccess(CWebUser::Nest.value($data,"userid").$());
	}

	$authUserType = CArray.array(
		GROUP_GUI_ACCESS_SYSTEM => _("System default"),
		GROUP_GUI_ACCESS_INTERNAL => _x("Internal", "user type"),
		GROUP_GUI_ACCESS_DISABLED => _("Disabled")
	);

	return isset($authUserType[$authType]) ? $authUserType[$authType] : _("Unknown");
}

/**
 * Unblock user account.
 *
 * @param array $userIds
 *
 * @return bool
 */
function unblock_user_login($userIds) {
	zbx_value2CArray.array($userIds);

	return DBexecute("UPDATE users SET attempt_failed=0 WHERE ".dbConditionInt("userid", $userIds));
}

/**
 * Get users ids by groups ids.
 *
 * @param array $userGroupIds
 *
 * @return array
 */
function get_userid_by_usrgrpid($userGroupIds) {
	zbx_value2CArray.array($userGroupIds);

	$userIds = CArray.array();

	$dbUsers = DBselect(
		"SELECT DISTINCT u.userid".
		" FROM users u,users_groups ug".
		" WHERE u.userid=ug.userid".
			" AND ".dbConditionInt("ug.usrgrpid", $userGroupIds).
			andDbNode("ug.usrgrpid", false)
	);
	while ($user = DBFetch($dbUsers)) {
		$userIds[$user["userid"]] = Nest.value($user,"userid").$();
	}

	return $userIds;
}

/**
 * Append user to group.
 *
 * @param string $userId
 * @param string $userGroupId
 *
 * @return bool
 */
function add_user_to_group($userId, $userGroupId) {
	if (granted2move_user($userId, $userGroupId)) {
		DBexecute("DELETE FROM users_groups WHERE userid=".zbx_dbstr($userId)." AND usrgrpid=".zbx_dbstr($userGroupId));

		$usersGroupsId = get_dbid("users_groups", "id");

		return DBexecute(
			"INSERT INTO users_groups (id,usrgrpid,userid) VALUES (".zbx_dbstr($usersGroupsId).",".zbx_dbstr($userGroupId).",".zbx_dbstr($userId).")"
		);
	}
	else {
		error(_("User cannot change status of himself."));
	}

	return false;
}

/**
 * Remove user from group.
 *
 * @param string $userId
 * @param string $userGroupId
 *
 * @return bool
 */
function remove_user_from_group($userId, $userGroupId) {
	if (granted2move_user($userId, $userGroupId)) {
		return DBexecute("DELETE FROM users_groups WHERE userid=".zbx_dbstr($userId)." AND usrgrpid=".zbx_dbstr($userGroupId));
	}
	else {
		error(_("User cannot change status of himself."));
	}

	return false;
}

/**
 * Check if group has permissions for update.
 *
 * @param array $userGroupIds
 *
 * @return bool
 */
function granted2update_group($userGroupIds) {
	zbx_value2CArray.array($userGroupIds);

	$users = get_userid_by_usrgrpid($userGroupIds);

	return !isset($users[CWebUser::$data["userid"]]);
}

/**
 * Check if user can be appended to group.
 *
 * @param string $userId
 * @param string $userGroupId
 *
 * @return bool
 */
function granted2move_user($userId, $userGroupId) {
	$group = API.UserGroup().get(CArray.array(
		"usrgrpids" => $userGroupId,
		"output" => API_OUTPUT_EXTEND
	));
	$group = reset($group);

	if (Nest.value($group,"gui_access").$() == GROUP_GUI_ACCESS_DISABLED || Nest.value($group,"users_status").$() == GROUP_STATUS_DISABLED) {
		return (bccomp(CWebUser::Nest.value($data,"userid").$(), $userId) != 0);
	}

	return true;
}

/**
 * Change group status.
 *
 * @param array $userGroupIds
 * @param int   $usersStatus
 *
 * @return bool
 */
function change_group_status($userGroupIds, $usersStatus) {
	zbx_value2CArray.array($userGroupIds);

	$grant = ($usersStatus == GROUP_STATUS_DISABLED) ? granted2update_group($userGroupIds) : true;

	if ($grant) {
		return DBexecute(
			"UPDATE usrgrp SET users_status=".$usersStatus." WHERE ".dbConditionInt("usrgrpid", $userGroupIds)
		);
	}
	else {
		error(_("User cannot change status of himself."));
	}

	return false;
}

/**
 * Change gui access for group.
 *
 * @param array $userGroupIds
 * @param int   $guiAccess
 *
 * @return bool
 */
function change_group_gui_access($userGroupIds, $guiAccess) {
	zbx_value2CArray.array($userGroupIds);

	$grant = ($guiAccess == GROUP_GUI_ACCESS_DISABLED) ? granted2update_group($userGroupIds) : true;

	if ($grant) {
		return DBexecute(
			"UPDATE usrgrp SET gui_access=".$guiAccess." WHERE ".dbConditionInt("usrgrpid", $userGroupIds)
		);
	}
	else {
		error(_("User cannot change GUI access for himself."));
	}

	return false;
}

/**
 * Change debug mode for group.
 *
 * @param array $userGroupIds
 * @param int   $debugMode
 *
 * @return bool
 */
function change_group_debug_mode($userGroupIds, $debugMode) {
	zbx_value2CArray.array($userGroupIds);

	return DBexecute(
		"UPDATE usrgrp SET debug_mode=".$debugMode." WHERE ".dbConditionInt("usrgrpid", $userGroupIds)
	);
}

/**
 * Gets user full name in format \"alias (name surname)\". If both name and surname exist, returns translated string.
 *
 * @param array $userData
 *
 * @return string
 */
function getUserFullname($userData) {
	$fullname = "";
	if (!zbx_empty(Nest.value($userData,"name").$())) {
		$fullname = Nest.value($userData,"name").$();
	}

	// return full name and surname
	if (!zbx_empty(Nest.value($userData,"surname").$())) {
		if (!zbx_empty(Nest.value($userData,"name").$())) {
			return $userData["alias"]." "._x("(%1$s %2$s)", "user fullname", Nest.value($userData,"name").$(), Nest.value($userData,"surname").$());
		}
		$fullname = Nest.value($userData,"surname").$();
	}

	// return alias with full name
	if (!zbx_empty($fullname)) {
		return $userData["alias"]." (".$fullname.")";
	}
	else {
		return Nest.value($userData,"alias").$();
	}
}
