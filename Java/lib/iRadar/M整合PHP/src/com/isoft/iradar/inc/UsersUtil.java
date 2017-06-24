package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_THEME;
import static com.isoft.iradar.inc.Defines.THEME_DEFAULT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.PermUtil.getUserGuiAccess;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CUserGroupGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.model.sql.SqlBuilder.Segment;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.4")
public class UsersUtil {

	/**
	 * Find user theme or get default theme.
	 *
	 * @param array _userData
	 *
	 * @return string
	 */
	@CodeConfirmed("benne.2.2.4")
	public static String getUserTheme(IIdentityBean idBean, SQLExecutor executor, Map userData) {
		Map<String, Object> config = select_config(idBean, executor);
		String css = null;
		if (isset(config,"default_theme")) {
			css  = Nest.value(config,"default_theme").asString();
		}
		if (isset(userData,"theme") &&  !THEME_DEFAULT.equals(Nest.value(userData,"theme").asString())) {
			css = Nest.value(userData,"theme").asString();
		}
		if (!isset(css)) {
			css = RDA_DEFAULT_THEME;
		}
		return css;
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static CArray<String> user_type2str() {
		CArray<String> userTypes = map(
			USER_TYPE_IRADAR_USER, _("iRadar User"),
			USER_TYPE_IRADAR_ADMIN, _("iRadar Admin"),
			USER_TYPE_SUPER_ADMIN, _("iRadar Super Admin")
		);
		return userTypes;
	}
	
	/**
	 * Get user type name.
	 *
	 * @param int _userType
	 *
	 * @return string
	 */
	@CodeConfirmed("benne.2.2.4")
	public static String user_type2str(int userType) {
		CArray<String> userTypes = map(
			USER_TYPE_IRADAR_USER, _("iRadar User"),
			USER_TYPE_IRADAR_ADMIN, _("iRadar Admin"),
			USER_TYPE_SUPER_ADMIN, _("iRadar Super Admin")
		);
		if (userTypes.containsKey(userType)) {
			return userTypes.get(userType);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Get user authentication name.
	 *
	 * @param int _authType
	 *
	 * @return string
	 */
	@CodeConfirmed("benne.2.2.4")
	public static String user_auth_type2str(IIdentityBean idBean, SQLExecutor executor, Integer authType) {
		if (authType == null) {
			authType = getUserGuiAccess(idBean, executor, Nest.as(CWebUser.get("userid")).asString());
		}
		CArray<String> authUserType = map(
			GROUP_GUI_ACCESS_SYSTEM, _("System default"),
			GROUP_GUI_ACCESS_INTERNAL, _x("Internal", "user type"),
			GROUP_GUI_ACCESS_DISABLED, _("Disabled")
		);
		return authUserType.containsKey(authType) ? authUserType.get(authType) : _("Unknown");
	}
	
	/**
	 * Unblock user account.
	 *
	 * @param array _userIds
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean unblock_user_login(IIdentityBean idBean, SQLExecutor executor, Long[] userids) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.dbConditionInt("userid", userids);
		Map params = sqlParts.getNamedParams();
		String segmentSql = sqlParts.createSegmentSql(Segment.where);
		String sql =  "UPDATE users SET attempt_failed=0 WHERE tenantid="+idBean.getTenantId()+" AND " + segmentSql;
		return executor.executeInsertDeleteUpdate(sql, params)>0;
	}
	
	/**
	 * Get users ids by groups ids.
	 *
	 * @param array _userGroupIds
	 *
	 * @return array
	 */
	@CodeConfirmed("benne.2.2.4")
	public static CArray<Long> get_userid_by_usrgrpid(IIdentityBean idBean, SQLExecutor executor, Long[] userGroupIds) {
		CArray<Long> userIds = array();		
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbUsers = DBselect(executor,
			"SELECT DISTINCT u.userid"+
			" FROM users u,users_groups ug"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "users", "u")+
				" AND u.tenantid=ug.tenantid"+
			    " AND u.userid=ug.userid"+
				" AND "+sqlParts.dual.dbConditionInt("ug.usrgrpid", userGroupIds),
				sqlParts.getNamedParams()
		);
		for (Map user : dbUsers) {
			Nest.value(userIds, Nest.value(user,"userid").asLong()).$(Nest.value(user,"userid").asLong());
		}
		return userIds;
	}
	
	/**
	 * Append user to group.
	 *
	 * @param string _userId
	 * @param string _userGroupId
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean add_user_to_group(IIdentityBean idBean, SQLExecutor executor, Long userId, Long userGroupId) {
		if (granted2move_user(idBean, executor, userId, userGroupId)) {
			Map params = new IMap();
			params.put("tenantid", idBean.getTenantId());
			params.put("userid", userId);
			params.put("usrgrpid", userGroupId);
			String sql = "DELETE FROM users_groups WHERE tenantid=#{tenantid} AND userid=#{userid} AND usrgrpid=#{usrgrpid}";
			executor.executeInsertDeleteUpdate(sql, params);

			Long usersGroupsId = get_dbid(idBean, executor, "users_groups", "id");
			params.put("id", usersGroupsId.longValue());
			
			sql = "INSERT INTO users_groups (tenantid,id,usrgrpid,userid) VALUES (#{tenantid},#{id},#{usrgrpid},#{userid})";
			return executor.executeInsertDeleteUpdate(sql, params)>0;
		} else {
			error(_("User cannot change status of himself."));
		}
		return false;
	}
	
	/**
	 * Remove user from group.
	 *
	 * @param string _userId
	 * @param string _userGroupId
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean remove_user_from_group(IIdentityBean idBean, SQLExecutor executor, Long userId, Long userGroupId) {
		if (granted2move_user(idBean, executor, userId, userGroupId)) {
			Map params = new IMap();
			params.put("tenantid", idBean.getTenantId());
			params.put("userid", userId);
			params.put("usrgrpid", userGroupId);
			String sql = "DELETE FROM users_groups WHERE tenantid=#{tenantid} AND userid=#{userid} AND usrgrpid=#{usrgrpid}";
			return executor.executeInsertDeleteUpdate(sql, params)>0;
		} else {
			error(_("User cannot change status of himself."));
		}
		return false;
	}
	
	/**
	 * Check if group has permissions for update.
	 *
	 * @param array _userGroupIds
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean granted2update_group(IIdentityBean idBean, SQLExecutor executor, Long[] userGroupIds) {
		CArray<Long> users = get_userid_by_usrgrpid(idBean, executor, userGroupIds);
		users.get(CWebUser.get("userid"));
		return !isset(users.get(CWebUser.get("userid")));
	}
	
	/**
	 * Check if user can be appended to group.
	 *
	 * @param string _userId
	 * @param string _userGroupId
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean granted2move_user(IIdentityBean idBean, SQLExecutor executor, Long userId, Long userGroupId) {
		CUserGroupGet ugget = new CUserGroupGet();
		ugget.setUsrgrpIds(userGroupId);
		ugget.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> groups = API.UserGroup(idBean, executor).get(ugget);
		Map group = reset(groups);		
		if (Nest.value(group,"gui_access").asInteger() == GROUP_GUI_ACCESS_DISABLED || Nest.value(group,"users_status").asInteger() == GROUP_STATUS_DISABLED) {
			return userId.equals(Nest.as(CWebUser.get("userid")).asLong());
		}
		return true;
	}

	/**
	 * Change group status.
	 *
	 * @param array _userGroupIds
	 * @param int   _usersStatus
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean change_group_status(IIdentityBean idBean, SQLExecutor executor, Long[] userGroupIds, int usersStatus) {
		boolean grant = (usersStatus == GROUP_STATUS_DISABLED) ? granted2update_group(idBean, executor, userGroupIds) : true;
		if (grant) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.where.dbConditionInt("usrgrpid", userGroupIds);
			Map params = sqlParts.getNamedParams();
			params.put("tenantid", idBean.getTenantId());
			params.put("status", usersStatus);
			String segmentSql = sqlParts.createSegmentSql(Segment.where);
			String sql =  "UPDATE usrgrp SET users_status=#{status} WHERE tenantid=#{tenantid} AND "+ segmentSql;
			return executor.executeInsertDeleteUpdate(sql, params)>0;
		} else {
			error(_("User cannot change status of himself."));
		}
		return false;
	}

	/**
	 * Change gui access for group.
	 *
	 * @param array _userGroupIds
	 * @param int   _guiAccess
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean change_group_gui_access(IIdentityBean idBean, SQLExecutor executor, Long[] userGroupIds, int guiAccess) {
		boolean grant = (guiAccess == GROUP_GUI_ACCESS_DISABLED) ? granted2update_group(idBean, executor, userGroupIds) : true;
		if (grant) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.where.dbConditionInt("usrgrpid", userGroupIds);
			Map params = sqlParts.getNamedParams();
			params.put("access", guiAccess);
			params.put("tenantid", idBean.getTenantId());
			String segmentSql = sqlParts.createSegmentSql(Segment.where);
			String sql =  "UPDATE usrgrp SET gui_access=#{access} WHERE tenantid=#{tenantid} AND "+ segmentSql;
			return executor.executeInsertDeleteUpdate(sql, params)>0;
		} else {
			error(_("User cannot change GUI access for himself."));
		}
		return false;
	}

	/**
	 * Change debug mode for group.
	 *
	 * @param array _userGroupIds
	 * @param int   _debugMode
	 *
	 * @return bool
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean change_group_debug_mode(IIdentityBean idBean, SQLExecutor executor, Long[] userGroupIds, int debugMode) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.dbConditionInt("usrgrpid", userGroupIds);
		Map params = sqlParts.getNamedParams();
		params.put("mode", debugMode);
		params.put("tenantid", idBean.getTenantId());
		String segmentSql = sqlParts.createSegmentSql(Segment.where);
		String sql =  "UPDATE usrgrp SET debug_mode=#{mode} WHERE tenantid=#{tenantid} AND "+ segmentSql;
		return executor.executeInsertDeleteUpdate(sql, params)>0;
	}

	/**
	 * Gets user full name in format \"alias (name surname)\". If both name and surname exist, returns translated string.
	 *
	 * @param array _userData
	 *
	 * @return string
	 */
	@CodeConfirmed("benne.2.2.4")
	public static String getUserFullname(Map userData) {
		return Nest.value(userData, "alias").asString();
	}

}
