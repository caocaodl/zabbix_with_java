package com.isoft.iradar.inc;

import static com.isoft.biz.daoimpl.radar.CDB.update;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.uasort;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_AGENT;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_IPMI;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_JMX;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_SNMP;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_DEFAULT;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_MD2;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_MD5;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_NONE;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_OEM;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_RMCP_PLUS;
import static com.isoft.iradar.inc.Defines.IPMI_AUTHTYPE_STRAIGHT;
import static com.isoft.iradar.inc.Defines.IPMI_PRIVILEGE_ADMIN;
import static com.isoft.iradar.inc.Defines.IPMI_PRIVILEGE_CALLBACK;
import static com.isoft.iradar.inc.Defines.IPMI_PRIVILEGE_OEM;
import static com.isoft.iradar.inc.Defines.IPMI_PRIVILEGE_OPERATOR;
import static com.isoft.iradar.inc.Defines.IPMI_PRIVILEGE_USER;
import static com.isoft.iradar.inc.Defines.RDA_NOT_INTERNAL_GROUP;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

@CodeConfirmed("benne.2.2.6")
public class HostsUtil {
	
	private HostsUtil(){
	}
	
	public static boolean setHostGroupInternal(IIdentityBean idBean, SQLExecutor executor, Long[] groupids) {
		return setHostGroupInternal(idBean, executor, groupids, RDA_NOT_INTERNAL_GROUP);
	}
	
	public static boolean setHostGroupInternal(IIdentityBean idBean, SQLExecutor executor, Long[] groupids, int internal ) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBexecute(executor,
				"UPDATE groups SET internal="+sqlParts.marshalParam(internal)+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean)+
				    " AND "+sqlParts.dual.dbConditionInt("groupid", groupids),
				sqlParts.getNamedParams());
	}
	
	/**
	 * Get ipmi auth type label by it's number.
	 *
	 * @param null|int type
	 *
	 * @return array|string
	 */
	public static CArray<String> ipmiAuthTypes() {
		CArray<String> types = map(
			IPMI_AUTHTYPE_DEFAULT, _("Default"),
			IPMI_AUTHTYPE_NONE, _("None"),
			IPMI_AUTHTYPE_MD2, _("MD2"),
			IPMI_AUTHTYPE_MD5, _("MD5"),
			IPMI_AUTHTYPE_STRAIGHT, _("Straight"),
			IPMI_AUTHTYPE_OEM, _("OEM"),
			IPMI_AUTHTYPE_RMCP_PLUS, _("RMCP+"));
		return types;
	}
	
	/**
	 * Get ipmi auth type label by it's number.
	 *
	 * @param null|int type
	 *
	 * @return array|string
	 */
	public static String ipmiAuthTypes(int type) {
		CArray<String> types = map(
			IPMI_AUTHTYPE_DEFAULT, _("Default"),
			IPMI_AUTHTYPE_NONE, _("None"),
			IPMI_AUTHTYPE_MD2, _("MD2"),
			IPMI_AUTHTYPE_MD5, _("MD5"),
			IPMI_AUTHTYPE_STRAIGHT, _("Straight"),
			IPMI_AUTHTYPE_OEM, _("OEM"),
			IPMI_AUTHTYPE_RMCP_PLUS, _("RMCP+"));
		if (isset(types,type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Get ipmi auth privilege label by it's number.
	 *
	 * @param null|int type
	 *
	 * @return array|string
	 */
	public static CArray<String> ipmiPrivileges() {
		CArray<String> types = map(
			IPMI_PRIVILEGE_CALLBACK, _("Callback"),
			IPMI_PRIVILEGE_USER, _("User"),
			IPMI_PRIVILEGE_OPERATOR, _("Operator"),
			IPMI_PRIVILEGE_ADMIN, _("Admin"),
			IPMI_PRIVILEGE_OEM, _("OEM"));
		return types;
	}
	
	/**
	 * Get ipmi auth privilege label by it's number.
	 *
	 * @param null|int type
	 *
	 * @return array|string
	 */
	public static String ipmiPrivileges(int type) {
		CArray<String> types = map(
			IPMI_PRIVILEGE_CALLBACK, _("Callback"),
			IPMI_PRIVILEGE_USER, _("User"),
			IPMI_PRIVILEGE_OPERATOR, _("Operator"),
			IPMI_PRIVILEGE_ADMIN, _("Admin"),
			IPMI_PRIVILEGE_OEM, _("OEM"));
		if (isset(types,type)) {
			return types.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Get info about what host inventory fields we have, their numbers and names.
	 * Example of usage:
	 *      inventories = getHostInventories();
	 *      echo inventories[1]['db_field']; // host_networks
	 *      echo inventories[1]['title']; // Host networks
	 *      echo inventories[1]['nr']; // 1
	 *
	 * @param bool orderedByTitle	whether an array should be ordered by field title, not by number
	 *
	 * @return array
	 */
	public static CArray<Map> getHostInventories() {
		return getHostInventories(false);
	}
	
	/**
	 * Get info about what host inventory fields we have, their numbers and names.
	 * Example of usage:
	 *      inventories = getHostInventories();
	 *      echo inventories[1]["db_field"]; // host_networks
	 *      echo inventories[1]["title"]; // Host networks
	 *      echo inventories[1]["nr"]; // 1
	 *
	 * @param bool orderedByTitle	whether an array should be ordered by field title, not by number
	 *
	 * @return array
	 */
	public static CArray<Map> getHostInventories(boolean orderedByTitle) {
		/*
		 * WARNING! Before modifying this array, make sure changes are synced with C
		 * C analog is located in function DBget_inventory_field() in src/libs/rdadbhigh/db.c
		 */
		CArray<Map> inventoryFields = map(
			1, map(
				"nr", 1,
				"db_field", "type",
				"title", _("Type")),
			2, map(
				"nr", 2,
				"db_field", "type_full",
				"title", _("Type (Full details)") ),
			3, map(
				"nr", 3,
				"db_field", "name",
				"title", _("Name") ),
			4, map(
				"nr", 4,
				"db_field", "alias",
				"title", _("Alias")),
			5, map(
				"nr", 5,
				"db_field", "os",
				"title", _("OS")),
			6, map(
				"nr", 6,
				"db_field", "os_full",
				"title", _("OS (Full details)")),
			7, map(
				"nr", 7,
				"db_field", "os_short",
				"title", _("OS (Short)")),
			8, map(
				"nr", 8,
				"db_field", "serialno_a",
				"title", _("Serial number A")),
			9, map(
				"nr", 9,
				"db_field", "serialno_b",
				"title", _("Serial number B")),
			10, map(
				"nr", 10,
				"db_field", "tag",
				"title", _("Tag")),
			11, map(
				"nr", 11,
				"db_field", "asset_tag",
				"title", _("Asset tag")),
			12, map(
				"nr", 12,
				"db_field", "macaddress_a",
				"title", _("MAC address A")),
			13, map(
				"nr", 13,
				"db_field", "macaddress_b",
				"title", _("MAC address B")),
			14, map(
				"nr", 14,
				"db_field", "hardware",
				"title", _("Hardware")),
			15, map(
				"nr", 15,
				"db_field", "hardware_full",
				"title", _("Hardware (Full details)")),
			16, map(
				"nr", 16,
				"db_field", "software",
				"title", _("Software")),
			17, map(
				"nr", 17,
				"db_field", "software_full",
				"title", _("Software (Full details)")),
			18, map(
				"nr", 18,
				"db_field", "software_app_a",
				"title", _("Software application A")),
			19, map(
				"nr", 19,
				"db_field", "software_app_b",
				"title", _("Software application B")),
			20, map(
				"nr", 20,
				"db_field", "software_app_c",
				"title", _("Software application C")),
			21, map(
				"nr", 21,
				"db_field", "software_app_d",
				"title", _("Software application D")),
			22, map(
				"nr", 22,
				"db_field", "software_app_e",
				"title", _("Software application E")),
			23, map(
				"nr", 23,
				"db_field", "contact",
				"title", _("Contact")),
			24, map(
				"nr", 24,
				"db_field", "location",
				"title", _("Location")),
			25, map(
				"nr", 25,
				"db_field", "location_lat",
				"title", _("Location latitude")),
			26, map(
				"nr", 26,
				"db_field", "location_lon",
				"title", _("Location longitude")),
			27, map(
				"nr", 27,
				"db_field", "notes",
				"title", _("Notes")),
			28, map(
				"nr", 28,
				"db_field", "chassis",
				"title", _("Chassis")),
			29, map(
				"nr", 29,
				"db_field", "model",
				"title", _("Model")),
			30, map(
				"nr", 30,
				"db_field", "hw_arch",
				"title", _("HW architecture")),
			31, map(
				"nr", 31,
				"db_field", "vendor",
				"title", _("Vendor")),
			32, map(
				"nr", 32,
				"db_field", "contract_number",
				"title", _("Contract number")),
			33, map(
				"nr", 33,
				"db_field", "installer_name",
				"title", _("Installer name")),
			34, map(
				"nr", 34,
				"db_field", "deployment_status",
				"title", _("Deployment status")),
			35, map(
				"nr", 35,
				"db_field", "url_a",
				"title", _("URL A")),
			36, map(
				"nr", 36,
				"db_field", "url_b",
				"title", _("URL B")),
			37, map(
				"nr", 37,
				"db_field", "url_c",
				"title", _("URL C")),
			38, map(
				"nr", 38,
				"db_field", "host_networks",
				"title", _("Host networks")),
			39, map(
				"nr", 39,
				"db_field", "host_netmask",
				"title", _("Host subnet mask")),
			40, map(
				"nr", 40,
				"db_field", "host_router",
				"title", _("Host router")),
			41, map(
				"nr", 41,
				"db_field", "oob_ip",
				"title", _("OOB IP address")),
			42, map(
				"nr", 42,
				"db_field", "oob_netmask",
				"title", _("OOB subnet mask")),
			43, map(
				"nr", 43,
				"db_field", "oob_router",
				"title", _("OOB router")),
			44, map(
				"nr", 44,
				"db_field", "date_hw_purchase",
				"title", _("Date HW purchased")),
			45, map(
				"nr", 45,
				"db_field", "date_hw_install",
				"title", _("Date HW installed")),
			46, map(
				"nr", 46,
				"db_field", "date_hw_expiry",
				"title", _("Date HW maintenance expires")),
			47, map(
				"nr", 47,
				"db_field", "date_hw_decomm",
				"title", _("Date HW decommissioned")),
			48, map(
				"nr", 48,
				"db_field", "site_address_a",
				"title", _("Site address A")),
			49, map(
				"nr", 49,
				"db_field", "site_address_b",
				"title", _("Site address B")),
			50, map(
				"nr", 50,
				"db_field", "site_address_c",
				"title", _("Site address C")),
			51, map(
				"nr", 51,
				"db_field", "site_city",
				"title", _("Site city")),
			52, map(
				"nr", 52,
				"db_field", "site_state",
				"title", _("Site state / province")),
			53, map(
				"nr", 53,
				"db_field", "site_country",
				"title", _("Site country")),
			54, map(
				"nr", 54,
				"db_field", "site_zip",
				"title", _("Site ZIP / postal")),
			55, map(
				"nr", 55,
				"db_field", "site_rack",
				"title", _("Site rack location")),
			56, map(
				"nr", 56,
				"db_field", "site_notes",
				"title", _("Site notes")),
			57, map(
				"nr", 57,
				"db_field", "poc_1_name",
				"title", _("Primary POC name")),
			58, map(
				"nr", 58,
				"db_field", "poc_1_email",
				"title", _("Primary POC email")),
			59, map(
				"nr", 59,
				"db_field", "poc_1_phone_a",
				"title", _("Primary POC phone A")),
			60, map(
				"nr", 60,
				"db_field", "poc_1_phone_b",
				"title", _("Primary POC phone B")),
			61, map(
				"nr", 61,
				"db_field", "poc_1_cell",
				"title", _("Primary POC cell")),
			62, map(
				"nr", 62,
				"db_field", "poc_1_screen",
				"title", _("Primary POC screen name")),
			63, map(
				"nr", 63,
				"db_field", "poc_1_notes",
				"title", _("Primary POC notes")),
			64, map(
				"nr", 64,
				"db_field", "poc_2_name",
				"title", _("Secondary POC name")),
			65, map(
				"nr", 65,
				"db_field", "poc_2_email",
				"title", _("Secondary POC email")),
			66, map(
				"nr", 66,
				"db_field", "poc_2_phone_a",
				"title", _("Secondary POC phone A")),
			67, map(
				"nr", 67,
				"db_field", "poc_2_phone_b",
				"title", _("Secondary POC phone B")),
			68, map(
				"nr", 68,
				"db_field", "poc_2_cell",
				"title", _("Secondary POC cell")),
			69, map(
				"nr", 69,
				"db_field", "poc_2_screen",
				"title", _("Secondary POC screen name")),
			70, map(
				"nr", 70,
				"db_field", "poc_2_notes",
				"title", _("Secondary POC notes"))	);

		// array is ordered by number by default, should we change that and order by title?
		if (orderedByTitle) {
			uasort(inventoryFields, new Comparator<Map>() {
				@Override 
				public int compare(Map a, Map b) {
					return strcmp(Nest.value(a, "title").asString(), Nest.value(b, "title").asString());
				}
			});
		}
		return inventoryFields;
	}

	public static String hostInterfaceTypeNumToName(int type) {
		String name = null;
		switch (type) {
			case INTERFACE_TYPE_AGENT:
				name  = _("agent");
				break;
			case INTERFACE_TYPE_SNMP:
				name = _("SNMP");
				break;
			case INTERFACE_TYPE_JMX:
				name = _("JMX");
				break;
			case INTERFACE_TYPE_IPMI:
				name = _("IPMI");
				break;
			default:
				throw new IllegalArgumentException(_("Unknown interface type."));
		}
		return name;
	}
	
	public static Map get_hostgroup_by_groupid(IIdentityBean idBean, SQLExecutor executor, String groupid) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map groups = DBfetch(DBselect(executor,
				"SELECT g.* FROM groups g"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "groups", "g")+
				    " AND g.groupid="+sqlParts.marshalParam(groupid),
				sqlParts.getNamedParams()));
		if(!empty(groups)){
			return groups;
		}
		error(_s("No host groups with groupid \"%s\".", groupid));
		return null;
	}
	
	public static CArray<Map> get_host_by_itemid(IIdentityBean idBean, SQLExecutor executor, String[] itemids) {
		boolean result = false;
		CArray hosts = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_hostsItems = DBselect(executor,
			"SELECT i.itemid,h.*"+
			" FROM hosts h,items i"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
				" AND i.tenantid=h.tenantid"+
			    " AND i.hostid=h.hostid"+
				" AND "+sqlParts.dual.dbConditionInt("i.itemid", TArray.as(itemids).asLong()),
			sqlParts.getNamedParams()
		);
		for(Map hostItem : db_hostsItems) {
			result = true;
			Nest.value(hosts,hostItem.get("itemid")).$(hostItem);
		}
		if (result) {
			return hosts;
		}
		return null;
	}
	
	public static Map get_host_by_itemid(IIdentityBean idBean, SQLExecutor executor, String itemid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetch(DBselect(executor,
			"SELECT i.itemid,h.*"+
			" FROM hosts h,items i"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
				" AND i.tenantid=h.tenantid"+
			    " AND i.hostid=h.hostid"+
				" AND i.itemid="+sqlParts.marshalParam(itemid),
			sqlParts.getNamedParams()
		));
	}
	
	public static Map get_host_by_hostid(IIdentityBean idBean, SQLExecutor executor, Long hostid) {
		return get_host_by_hostid(idBean, executor, hostid, 0);
	}
	
	/**
	 * @param executor
	 * @param hostid  String或Integer类型
	 * @return
	 */
	public static Map get_host_by_hostid(IIdentityBean idBean, SQLExecutor executor, Long hostid, int no_error_message) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map row = DBfetch(DBselect(executor,
				"SELECT h.* FROM hosts h "+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
				" AND h.hostid="+sqlParts.marshalParam(hostid),
				sqlParts.getNamedParams()));
		if (!empty(row)) {
			return row;
		}
		if (no_error_message == 0) {
			error(_s("No host with hostid \"%s\".", hostid));
		}
		return null;
	}
	
	public static CArray<Map> get_hosts_by_templateid(IIdentityBean idBean, SQLExecutor executor, Long[] templateids) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor,
				"SELECT h.*"+
				" FROM hosts h,hosts_templates ht"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
				    " AND h.tenantid=ht.tenantid"+
				    " AND h.hostid=ht.hostid"+
					" AND "+sqlParts.dual.dbConditionInt("ht.templateid", templateids),
				sqlParts.getNamedParams()
			);
	}
	
	public static boolean updateHostStatus(IIdentityBean idBean, SQLExecutor executor, Long[] hostids, int status) {
		CArray hostIds = array();
		int oldStatus = (status == HOST_STATUS_MONITORED ? HOST_STATUS_NOT_MONITORED : HOST_STATUS_MONITORED);
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> db_hosts = DBselect(executor,
			"SELECT h.hostid,h.host,h.status"+
			" FROM hosts h"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
			    " AND "+sqlParts.dual.dbConditionInt("h.hostid", hostids)+
				" AND h.status="+oldStatus,
			sqlParts.getNamedParams()
		);
		for(Map host : db_hosts) {
			hostIds.add(Nest.value(host,"hostid").asLong());
			Map host_new = Clone.deepcopy(host);
			Nest.value(host_new,"status").$(status);
			add_audit_ext(idBean, executor, AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_HOST, Nest.value(host,"hostid").asLong(), Nest.value(host,"host").asString(), "hosts", host, host_new);
			info(_("Updated status of host")+" \""+Nest.value(host,"host").asString()+"\"");
		}

		return update(idBean, executor, "hosts", array((Map)map(
			"values", map("status", status),
			"where", map("hostid", hostIds.valuesAsLong())
		)));
	}
	
	public static Map get_application_by_applicationid(IIdentityBean idBean, SQLExecutor executor, String applicationid) {
		return get_application_by_applicationid(idBean, executor, applicationid, 0);
	}
	
	public static Map get_application_by_applicationid(IIdentityBean idBean, SQLExecutor executor, String applicationid, int no_error_message) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map row = DBfetch(DBselect(executor,
				"SELECT a.* FROM applications a"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "applications", "a")+
				    " AND a.applicationid="+sqlParts.marshalParam(applicationid),
				sqlParts.getNamedParams()));
		if (!empty(row)) {
			return row;
		}
		if (no_error_message == 0) {
			error(_s("No application with ID \"%1$s\".", applicationid));
		}
		return null;
	}
	
	/**
	 * Returns the farthest application ancestor for each given application.
	 *
	 * @param array applicationIds
	 * @param array templateApplicationIds		array with parent application IDs as keys and arrays of child application
	 * 											IDs as values
	 *
	 * @return array	an array with child IDs as keys and arrays of ancestor IDs as values
	 */
	public static CArray<CArray<Long>> getApplicationSourceParentIds(IIdentityBean idBean, SQLExecutor executor, Long[] applicationIds) {
		return getApplicationSourceParentIds(idBean, executor, applicationIds, new CArray());
	}

	
	/**
	 * Returns the farthest application ancestor for each given application.
	 *
	 * @param array applicationIds
	 * @param array templateApplicationIds		array with parent application IDs as keys and arrays of child application
	 * 											IDs as values
	 *
	 * @return array	an array with child IDs as keys and arrays of ancestor IDs as values
	 */
	public static CArray<CArray<Long>> getApplicationSourceParentIds(IIdentityBean idBean, SQLExecutor executor, Long[] applicationIds, CArray<CArray<Long>> templateApplicationIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> query = DBselect(executor,
			"SELECT at.applicationid,at.templateid"+
			" FROM application_template at"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "application_template", "at")+
			    " AND "+sqlParts.dual.dbConditionInt("at.applicationid", applicationIds),
			sqlParts.getNamedParams()
		);

		CArray<Long> capplicationIds = array();
		CArray<Long> unsetApplicationIds = array();
		for(Map applicationTemplate : query) {
			// check if we already have an application inherited from the current application
			// if we do - copy all of its child applications to the parent template
			if (isset(templateApplicationIds,Nest.value(applicationTemplate,"applicationid").asLong())) {
				
				Nest.value(templateApplicationIds,Nest.value(applicationTemplate, "templateid").asLong()).$(
						Nest.value(templateApplicationIds,Nest.value(applicationTemplate, "applicationid").asLong()).$()
				);
				Nest.value(unsetApplicationIds,Nest.value(applicationTemplate, "applicationid").asLong()).$(
						Nest.value(applicationTemplate,"applicationid").asLong()
				);
			}
			// if no - just add the application
			else {
				if(!isset(templateApplicationIds,Nest.value(applicationTemplate,"templateid").asLong())){
					Nest.value(templateApplicationIds,Nest.value(applicationTemplate,"templateid").asLong()).$(array());
				}
				Nest.value(templateApplicationIds,Nest.value(applicationTemplate,"templateid").asLong()).asCArray().add(Nest.value(applicationTemplate,"applicationid").asLong());
			}
			Nest.value(capplicationIds,Nest.value(applicationTemplate,"applicationid").asLong()).$(Nest.value(applicationTemplate,"templateid").asLong());
		}

		// unset children of all applications that we found a new parent for
		for(Long applicationId : unsetApplicationIds) {
			unset(templateApplicationIds,applicationId);
		}

		// continue while we still have new applications to check
		if (!empty(capplicationIds)) {
			return getApplicationSourceParentIds(idBean, executor, capplicationIds.valuesAsLong(), templateApplicationIds);
		} else {
			// return an inverse hash with application IDs as keys and arrays of parent application IDs as values
			CArray<CArray<Long>> result = array();
			for (Entry<Object, CArray<Long>> e : templateApplicationIds.entrySet()) {
				Long templateId = Nest.as(e.getKey()).asLong();
			    capplicationIds = e.getValue();
				for(Long applicationId : capplicationIds) {
					if(!isset(result,applicationId)){
						Nest.value(result, applicationId).$(array());
					}
					result.get(applicationId).add(templateId);
				}
			}
			return result;
		}
	}
	
	/**
	 * Returns the farthest host prototype ancestor for each given host prototype.
	 *
	 * @param array hostPrototypeIds
	 * @param array templateHostPrototypeIds	array with parent host prototype IDs as keys and arrays of child host
	 * 											prototype IDs as values
	 *
	 * @return array	an array of child ID - ancestor ID pairs
	 */
	public static CArray<Long> getHostPrototypeSourceParentIds(IIdentityBean idBean, SQLExecutor executor, Long[] hostPrototypeIds) {
		return getHostPrototypeSourceParentIds(idBean, executor, hostPrototypeIds, array());
	}
	
	/**
	 * Returns the farthest host prototype ancestor for each given host prototype.
	 *
	 * @param array hostPrototypeIds
	 * @param array templateHostPrototypeIds	array with parent host prototype IDs as keys and arrays of child host
	 * 											prototype IDs as values
	 *
	 * @return array	an array of child ID - ancestor ID pairs
	 */
	public static CArray<Long> getHostPrototypeSourceParentIds(IIdentityBean idBean, SQLExecutor executor, Long[] hostPrototypeIds, CArray<CArray<Long>> templateHostPrototypeIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> query = DBselect(executor,
			"SELECT h.hostid,h.templateid"+
			" FROM hosts h"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
			    " AND "+sqlParts.dual.dbConditionInt("h.hostid", hostPrototypeIds)+
				" AND h.templateid>0",
			sqlParts.getNamedParams()
		);

		CArray<Long> chostPrototypeIds = array();
		for (Map hostPrototype : query) {
			// check if we already have host prototype inherited from the current host prototype
			// if we do - move all of its child prototypes to the parent template
			if (isset(templateHostPrototypeIds,Nest.value(hostPrototype,"hostid").asLong())) {
				
				Nest.value(templateHostPrototypeIds,Nest.value(hostPrototype, "templateid").asLong()).$(
						Nest.value(templateHostPrototypeIds,Nest.value(hostPrototype, "hostid").asLong()).$()
				);
				unset(templateHostPrototypeIds,Nest.value(hostPrototype, "hostid").asLong());
			}
			// if no - just add the prototype
			else {
				if(!isset(templateHostPrototypeIds,Nest.value(hostPrototype,"templateid").asLong())){
					Nest.value(templateHostPrototypeIds,Nest.value(hostPrototype,"templateid").asLong()).$(array());
				}
				Nest.value(templateHostPrototypeIds,Nest.value(hostPrototype,"templateid").asLong()).asCArray().add(Nest.value(hostPrototype,"hostid").asLong());
				chostPrototypeIds.add(Nest.value(hostPrototype,"templateid").asLong());
			}
		}

		// continue while we still have new host prototypes to check
		if (!empty(chostPrototypeIds)) {
			return getHostPrototypeSourceParentIds(idBean, executor,chostPrototypeIds.valuesAsLong(), templateHostPrototypeIds);
		} else {
			// return an inverse hash with prototype IDs as keys and parent prototype IDs as values
			CArray<Long> result = array();
			for (Entry<Object, CArray<Long>> e : templateHostPrototypeIds.entrySet()) {
				Long templateId = Nest.as(e.getKey()).asLong();
			    CArray<Long> hostIds = e.getValue();
				for(Long hostId : hostIds) {
					Nest.value(result,hostId).$(templateId);
				}
			}
			return result;
		}
	}
	
	/**
	 * Get host ids of hosts which groupids can be unlinked from.
	 * if hostids is passed, function will check only these hosts.
	 *
	 * @param array groupids
	 * @param array hostids
	 *
	 * @return array
	 */
	public static CArray<Long> getUnlinkableHosts(IIdentityBean idBean, SQLExecutor executor, Long[] groupids) {
		return getUnlinkableHosts(idBean, executor, groupids, null);
	}
	
	/**
	 * Get host ids of hosts which groupids can be unlinked from.
	 * if hostids is passed, function will check only these hosts.
	 *
	 * @param array groupids
	 * @param array hostids
	 *
	 * @return array
	 */
	public static CArray<Long> getUnlinkableHosts(IIdentityBean idBean, SQLExecutor executor, Long[] groupids, Long[] hostids) {
		CArray<Long> unlinkableHostIds = array();
		SqlBuilder sqlParts = new SqlBuilder();
		String sql_where = "";
		if (hostids != null) {
			sql_where = " AND "+sqlParts.dual.dbConditionInt("hg.hostid", hostids);
		}
		CArray<Map> result = DBselect(executor,
			"SELECT hg.hostid,COUNT(hg.groupid) AS grp_count"+
			" FROM hosts_groups hg"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts_groups", "hg")+
			" AND "+sqlParts.dual.dbConditionInt("hg.groupid", groupids, true)+
			sql_where+
			" GROUP BY hg.hostid"+
			" HAVING COUNT(hg.groupid)>0",
			sqlParts.getNamedParams()
		);
		for(Map row : result) {
			unlinkableHostIds.add(Nest.value(row,"hostid").asLong());
		}
		return unlinkableHostIds;
	}
	
	public static CArray<Long> getDeletableHostGroups(IIdentityBean idBean, SQLExecutor executor) {
		return getDeletableHostGroups(idBean, executor, null);
	}
	
	public static CArray<Long> getDeletableHostGroups(IIdentityBean idBean, SQLExecutor executor, Long[] groupids) {
		CArray<Long> deletable_groupids = array();
		CArray<Long> hostids = getUnlinkableHosts(idBean, executor, groupids);
		SqlBuilder sqlParts = new SqlBuilder();
		String sql_where = "";
		if (!is_null(groupids)) {
			sql_where = " AND "+sqlParts.dual.dbConditionInt("g.groupid", groupids);
		}
		CArray<Map> db_groups = DBselect(executor,
			"SELECT DISTINCT g.groupid"+
			" FROM groups g"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "groups", "g")+
			" AND g.internal="+RDA_NOT_INTERNAL_GROUP+
				sql_where+
				" AND NOT EXISTS ("+
					"SELECT NULL"+
					" FROM hosts_groups hg"+
					" WHERE g.tenantid=hg.tenantid"+
					   " AND g.groupid=hg.groupid"+
						(!empty(hostids) ? " AND "+sqlParts.dual.dbConditionInt("hg.hostid", hostids.valuesAsLong(), true) : "")+
				")",
			sqlParts.getNamedParams()
		);
		for (Map group : db_groups) {
			Nest.value(deletable_groupids, Nest.value(group,"groupid").asLong()).$(Nest.value(group,"groupid").asLong());
		}
		return deletable_groupids;
	}
	
	public static boolean isTemplate(IIdentityBean idBean, SQLExecutor executor, String hostId) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map dbHost = DBfetch(DBselect(executor,
				"SELECT h.status FROM hosts h"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
			        " AND h.hostid="+sqlParts.marshalParam(hostId),
				sqlParts.getNamedParams()));
		return (!empty(dbHost) && Nest.value(dbHost,"status").asInteger() == HOST_STATUS_TEMPLATE);
	}
	
	public static boolean isTemplateInHost(CArray<Map> hosts) {
		if (!empty(hosts)) {
			for(Map host : hosts) {
				if (!empty(Nest.value(host,"templateid").$())) {
					return true;
				}
			}
		}
		return false;
	}
	
}
