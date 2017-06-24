package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_DISABLED;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_INTERNAL;
import static com.isoft.iradar.inc.Defines.GROUP_GUI_ACCESS_SYSTEM;
import static com.isoft.iradar.inc.Defines.GROUP_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.PERM_RES_DATA_ARRAY;
import static com.isoft.iradar.inc.Defines.PERM_RES_IDS_ARRAY;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_HTTP;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_AUTH_LDAP;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.HtmlUtil.fatal_error;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.g;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.model.sql.SqlBuilder.Segment;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class PermUtil {
	
	private PermUtil() {
	}
	
	/**
	 * Get permission label.
	 * @param int permission
	 * @return string
	 */
	public static String permission2str(int permission) {
		CArray<String> permissions = map(
			PERM_READ_WRITE, _("Read-write"),
			PERM_READ , _("Read only"),
			PERM_DENY, _("Deny")
		);
		return isset(permissions, permission) ? permissions.get(permission) : _("Unknown");
	}
	
	/**
	 * Get authentication label.
	 * @param int type
	 * @return string
	 */
	public static String authentication2str(int type) {
		CArray<String> authentications = map(
			RDA_AUTH_INTERNAL, _("iRadar internal authentication"),
			RDA_AUTH_LDAP, _("LDAP authentication"),
			RDA_AUTH_HTTP, _("HTTP authentication")
		);
		return isset(authentications,type) ? authentications.get(type) : _("Unknown");
	}

	/**
	 * Checking user permissions to access system (affects server side: no notification will be sent)
	 * @param executor
	 * @param userid
	 * @return
	 */
	public static boolean check_perm2system(IIdentityBean idBean, SQLExecutor executor, String userid) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql =  "SELECT g.usrgrpid"+
							" FROM usrgrp g,users_groups ug"+
							" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "usrgrp", "g")+
							    " AND ug.userid="+sqlParts.marshalParam(userid)+
							    " AND g.tenantid=ug.tenantid"+
								" AND g.usrgrpid=ug.usrgrpid"+
								" AND g.users_status="+GROUP_STATUS_DISABLED;
		CArray<Map> datas = DBselect(executor, sql, 1, sqlParts.getNamedParams());
		return empty(datas);
	}
	
	/**
	 * Checking user permissions to Login in frontend
	 * @param executor
	 * @param userid
	 * @return
	 */
	public static boolean check_perm2login(IIdentityBean idBean, SQLExecutor executor,String userid) {
		return (getUserGuiAccess(idBean, executor, userid) != GROUP_GUI_ACCESS_DISABLED);
	}
	
	/**
	 * Get user gui access.
	 *
	 * @param string userId
	 * @param int    maxGuiAccess
	 *
	 * @return int
	 */
	public static int getUserGuiAccess(IIdentityBean idBean, SQLExecutor executor, String userId) {
		return getUserGuiAccess(idBean, executor, userId, null);
	}

	/**
	 * Get user gui access.
	 *
	 * @param string userId
	 * @param int    maxGuiAccess
	 *
	 * @return int
	 */
	public static int getUserGuiAccess(IIdentityBean idBean, SQLExecutor executor, String userId, Integer maxGuiAccess) {
		if (Cphp.equals(Nest.as(CWebUser.get("userid")).asString(), userId) && isset(CWebUser.get("gui_access"))) {
			return Nest.as(CWebUser.get("gui_access")).asInteger();
		}
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = 	"SELECT MAX(g.gui_access) AS gui_access" +
							" FROM usrgrp g,users_groups ug" +
							" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "usrgrp", "g")+
							    " AND ug.userid=#{userid}" +
								" AND g.tenantid=ug.tenantid" +
								" AND g.usrgrpid=ug.usrgrpid" +
								((maxGuiAccess == null) ? "" : " AND g.gui_access<=" +maxGuiAccess);
		Map params = sqlParts.getNamedParams();
		params.put("userid", userId);
		params.put("access", maxGuiAccess);
		List<Map> datas = executor.executeNameParaQuery(sql, params);
		Map guiAccess = datas.isEmpty() ? null : datas.get(0);
		return guiAccess!=null ? Nest.value(guiAccess,"gui_access").asInteger() : GROUP_GUI_ACCESS_SYSTEM;
	}
	
	/**
	 * Get user authentication type.
	 *
	 * @param string userId
	 * @param int    maxGuiAccess
	 *
	 * @return int
	 */
	public static int getUserAuthenticationType(IIdentityBean idBean, SQLExecutor executor, String userId) {
		return getUserAuthenticationType(idBean, executor, userId, null);
	}
	
	/**
	 * Get user authentication type.
	 *
	 * @param string userId
	 * @param int    maxGuiAccess
	 *
	 * @return int
	 */
	public static int getUserAuthenticationType(IIdentityBean idBean, SQLExecutor executor, String userId, Integer maxGuiAccess) {
		Map<String, Object> config = select_config(idBean, executor);
		switch (getUserGuiAccess(idBean, executor, userId, maxGuiAccess)) {
			case GROUP_GUI_ACCESS_SYSTEM:
				return Nest.value(config,"authentication_type").asInteger();
			case GROUP_GUI_ACCESS_INTERNAL:
				return (Nest.value(config,"authentication_type").asInteger() == RDA_AUTH_HTTP) ? RDA_AUTH_HTTP : RDA_AUTH_INTERNAL;
			default:
				return Nest.value(config,"authentication_type").asInteger();
		}
	}
	
	/**
	 * Get groups gui access.
	 *
	 * @param array groupIds
	 * @param int   maxGuiAccess
	 *
	 * @return int
	 */
	public static int getGroupsGuiAccess(IIdentityBean idBean, SQLExecutor executor, Long[] groupIds) {
		return getGroupsGuiAccess(idBean, executor, groupIds, null);
	}
	
	/**
	 * Get groups gui access.
	 *
	 * @param array groupIds
	 * @param int   maxGuiAccess
	 *
	 * @return int
	 */
	public static int getGroupsGuiAccess(IIdentityBean idBean, SQLExecutor executor, Long[] groupIds, Integer maxGuiAccess) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map guiAccess = DBfetch(DBselect(executor,
			"SELECT MAX(g.gui_access) AS gui_access"+
			" FROM usrgrp g"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "usrgrp", "g")+
			    " AND "+sqlParts.dual.dbConditionInt("g.usrgrpid", groupIds)+
				((maxGuiAccess == null) ? "" : " AND g.gui_access<="+maxGuiAccess),
			sqlParts.getNamedParams()
		));
		return !empty(guiAccess) ? Nest.value(guiAccess,"gui_access").asInteger() : GROUP_GUI_ACCESS_SYSTEM;
	}
	
	/**
	 * Get group authentication type.
	 *
	 * @param array groupIds
	 * @param int   maxGuiAccess
	 *
	 * @return int
	 */
	public static int getGroupAuthenticationType(IIdentityBean idBean, SQLExecutor executor, Long[] groupIds) {
		return getGroupAuthenticationType(idBean, executor, groupIds, null);
	}
	
	/**
	 * Get group authentication type.
	 *
	 * @param array groupIds
	 * @param int   maxGuiAccess
	 *
	 * @return int
	 */
	public static int getGroupAuthenticationType(IIdentityBean idBean, SQLExecutor executor, Long[] groupIds, Integer maxGuiAccess) {
		Map<String, Object> config = select_config(idBean, executor);
		switch (getGroupsGuiAccess(idBean, executor, groupIds, maxGuiAccess)) {
			case GROUP_GUI_ACCESS_SYSTEM:
				return Nest.value(config,"authentication_type").asInteger();
			case GROUP_GUI_ACCESS_INTERNAL:
				return (Nest.value(config,"authentication_type").asInteger() == RDA_AUTH_HTTP) ? RDA_AUTH_HTTP : RDA_AUTH_INTERNAL;
			default:
				return Nest.value(config,"authentication_type").asInteger();
		}
	}

	/**
	 * Returns the host groups that are accessible by the current user with the permission level given in perm.
	 *
	 * Can return results in different formats, based on the per_res parameter. Possible values are:
	 * - PERM_RES_IDS_ARRAY - return only host group ids;
	 * - PERM_RES_DATA_ARRAY - return an array of host groups.
	 * @param executor
	 * @param userData an array defined as array('userid' => userid, 'type' => type)
	 * @param perm requested permission level
	 * @param permRes result format
	 * @return
	 */
	public static CArray get_accessible_groups_by_user(IIdentityBean idBean, SQLExecutor executor, Map<String, Object> userData, Integer perm) {
		return get_accessible_groups_by_user(idBean, executor, userData, perm, PERM_RES_IDS_ARRAY);
	}

	/**
	 * Returns the host groups that are accessible by the current user with the permission level given in perm.
	 *
	 * Can return results in different formats, based on the per_res parameter. Possible values are:
	 * - PERM_RES_IDS_ARRAY - return only host group ids;
	 * - PERM_RES_DATA_ARRAY - return an array of host groups.
	 * @param executor
	 * @param userData an array defined as array('userid' => userid, 'type' => type)
	 * @param perm requested permission level
	 * @param permRes result format
	 * @return
	 */
	public static CArray get_accessible_groups_by_user(IIdentityBean idBean, SQLExecutor executor, Map<String, Object> userData, Integer perm, Integer permRes) {
		String userId =Nest.value(userData,"userid").asString();
		if (!isset(userId)) {
			fatal_error(idBean, _("Incorrect user data in \"get_accessible_groups_by_user\"."));
		}

		int userType =Nest.value(userData,"type").asInteger();
		CArray result = array();
		CArray processed = array();

		String sql = null;
		SqlBuilder sqlParts = new SqlBuilder();
		if (userType == USER_TYPE_SUPER_ADMIN) {
			sql = "SELECT hg.groupid,hg.name"+
					" FROM groups hg"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "groups", "hg")+
					" GROUP BY hg.groupid,hg.name";
		} else {
			sql = "SELECT hg.groupid,hg.name,MAX(r.permission) AS permission,MIN(r.permission) AS permission_deny,g.userid"+
					" FROM groups hg"+
						" LEFT JOIN rights r ON r.tenantid=hg.tenantid AND r.id=hg.groupid"+
						" LEFT JOIN users_groups g ON r.tenantid=g.tenantid AND r.groupid=g.usrgrpid"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "groups", "hg")+
					" AND g.userid="+sqlParts.marshalParam(userId)+
					" GROUP BY hg.groupid,hg.name,g.userid";
		}

		CArray<Map> dbGroups = DBselect(executor, sql, sqlParts.getNamedParams());
		for (Map groupData : dbGroups) {
			// calculate permissions
			if (userType == USER_TYPE_SUPER_ADMIN) {
				Nest.value(groupData,"permission").$(PERM_READ_WRITE);
			} else if (isset(processed,groupData.get("groupid"))) {
				if (Nest.value(groupData,"permission_deny").asInteger() == PERM_DENY) {
					unset(result,groupData.get("groupid"));
				} else if (Nest.value(processed,groupData.get("groupid")).asLong() > Nest.value(groupData,"permission").asLong()) {
					unset(processed,groupData.get("groupid"));
				} else {
					continue;
				}
			}

			Nest.value(processed,groupData.get("groupid")).$(Nest.value(groupData,"permission").$());
			if (Nest.value(groupData,"permission").asInteger() < perm) {
				continue;
			}

			switch (permRes) {
				case PERM_RES_DATA_ARRAY:
					Nest.value(result,groupData.get("groupid")).$(groupData);
					break;
				default:
					Nest.value(result,groupData.get("groupid")).$(Nest.value(groupData,"groupid").$());
					break;
			}
		}

		if (userType == USER_TYPE_SUPER_ADMIN) {
			CArrayHelper.sort(result, array(
				map("field", "name", "order", RDA_SORT_UP)
			));
		} else {
			CArrayHelper.sort(result, array(
				map("field", "name", "order", RDA_SORT_UP),
				map("field", "permission", "order", RDA_SORT_UP)
			));
		}

		return result;
	}
	
	/* NOTE: right structure is
	rights[i]["type"]	= type of resource
	rights[i]["permission"]= permission for resource
	rights[i]["id"]	= resource id
	 */
	public static CArray get_accessible_hosts_by_rights(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights, Integer user_type, Integer perm) {
		return get_accessible_hosts_by_rights(idBean, executor, rights, user_type, perm, null);
	}
	
	public static CArray get_accessible_hosts_by_rights(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights, Integer user_type, Integer perm, Integer perm_res) {
		CArray result = array();
		CArray res_perm = array();

		for (Map right : rights) {
			Nest.value(res_perm,right.get("id")).$(Nest.value(right,"permission").$());
		}

		CArray<Map> host_perm = array();
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.dbConditionTenants(idBean, "hosts", "h");
		sqlParts.where.put("h.status in ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+","+HOST_STATUS_TEMPLATE+")");
		sqlParts.where.dbConditionInt("h.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED});

		CArray<CArray<Long>> perm_by_host = array();

		CArray<Map> dbHosts = DBselect(executor,
			"SELECT hg.groupid AS groupid,h.hostid,h.host,h.name AS host_name,h.status"+
			" FROM hosts h"+
				" LEFT JOIN hosts_groups hg ON hg.tenantid=h.tenantid AND hg.hostid=h.hostid"+
				(count(sqlParts.where.valueList)>0 ? " WHERE "+sqlParts.createSegmentSql(Segment.where):""),
			sqlParts.getNamedParams()
		);
		for (Map dbHost : dbHosts) {
			if (isset(dbHost,"groupid") && isset(Nest.value(res_perm,dbHost.get("groupid")).$())) {
				if (!isset(perm_by_host,dbHost.get("hostid"))) {
					Nest.value(perm_by_host,dbHost.get("hostid")).$(array());
				}
				Nest.value(perm_by_host,dbHost.get("hostid")).asCArray().add(Nest.value(res_perm,dbHost.get("groupid")).asLong());
				Nest.value(host_perm,dbHost.get("hostid"),dbHost.get("groupid")).$(Nest.value(res_perm,dbHost.get("groupid")).$());
			}
			Nest.value(host_perm,dbHost.get("hostid"),"data").$(dbHost);
		}

		for (Entry<Object, Map> e : host_perm.entrySet()) {
		    Object hostid = e.getKey();
		    Map dbHost = e.getValue();
			dbHost = Nest.value(dbHost,"data").asCArray();

			// select min rights from groups
			if (USER_TYPE_SUPER_ADMIN == user_type) {
				Nest.value(dbHost,"permission").$(PERM_READ_WRITE);
			} else {
				if (isset(perm_by_host,hostid)) {
					Nest.value(dbHost,"permission").$( (min(perm_by_host.get(hostid)) == PERM_DENY)
						? PERM_DENY
						: max(perm_by_host.get(hostid)));
				} else {
					Nest.value(dbHost,"permission").$(PERM_DENY);
				}
			}

			if (Nest.value(dbHost,"permission").asInteger() < perm) {
				continue;
			}

			switch (perm_res) {
				case PERM_RES_DATA_ARRAY:
					Nest.value(result,dbHost.get("hostid")).$(dbHost);
					break;
				default:
					Nest.value(result,dbHost.get("hostid")).$(Nest.value(dbHost,"hostid").$());
			}
		}

		CArrayHelper.sort(result, array(
			map("field", "host_name", "order", RDA_SORT_UP)
		));

		return result;
	}

	public static CArray get_accessible_groups_by_rights(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights, Integer user_type, Integer perm) {
		return get_accessible_groups_by_rights(idBean, executor, rights, user_type, perm, null);
	}
	
	public static CArray get_accessible_groups_by_rights(IIdentityBean idBean, SQLExecutor executor, CArray<Map> rights, Integer user_type, Integer perm, Integer perm_res) {
		CArray result= array();
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.where.dbConditionTenants(idBean, "groups", "g");

		CArray group_perm = array();
		for(Map right : rights) {
			Nest.value(group_perm,right.get("id")).$(Nest.value(right,"permission").$());
		}

		CArray<Map> dbHostGroups = DBselect(executor,
			"SELECT g.*,"+PERM_DENY+" AS permission"+
			" FROM groups g"+
				(count(sqlParts.where.valueList)>0 ? " WHERE "+sqlParts.createSegmentSql(Segment.where):""),
			sqlParts.getNamedParams()
		);
		for (Map dbHostGroup : dbHostGroups) {
			if (USER_TYPE_SUPER_ADMIN == user_type) {
				Nest.value(dbHostGroup,"permission").$(PERM_READ_WRITE);
			} else {
				if (isset(group_perm,dbHostGroup.get("groupid"))) {
					Nest.value(dbHostGroup,"permission").$(Nest.value(group_perm,dbHostGroup.get("groupid")).$());
				} else {
					Nest.value(dbHostGroup,"permission").$(PERM_DENY);
				}
			}

			if (Nest.value(dbHostGroup,"permission").asInteger() < perm) {
				continue;
			}

			switch (perm_res) {
				case PERM_RES_DATA_ARRAY:
					Nest.value(result,dbHostGroup.get("groupid")).$(dbHostGroup);
					break;
				default:
					Nest.value(result,dbHostGroup.get("groupid")).$(Nest.value(dbHostGroup,"groupid").$());
			}
		}

		CArrayHelper.sort(result, array(
			map("field", "name", "order", RDA_SORT_UP)
		));

		return result;
	}
	
	/**
	 * Returns array of user groups by userId
	 * @param executor
	 * @param userId
	 * @return
	 */
	public static List<Long> getUserGroupsByUserId(IIdentityBean idBean, SQLExecutor executor, String userId) {
		Map<Long, List<Long>> userGroups = g.userGroups.$();
		if (!isset(userGroups,userId)) {
			Nest.value(userGroups,userId).$(new ArrayList());
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> result = DBselect(executor,
					"SELECT ug.usrgrpid FROM users_groups ug"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "users_groups", "ug")+
				     " AND ug.userid="+sqlParts.marshalParam(userId),
				 sqlParts.getNamedParams());
			for (Map row : result) {
				userGroups.get(userId).add(Nest.value(row,"usrgrpid").asLong());
			}
		}
		return userGroups.get(userId);
	}
}
