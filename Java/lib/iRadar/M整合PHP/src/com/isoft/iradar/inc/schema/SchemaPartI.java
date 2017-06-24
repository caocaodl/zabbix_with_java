package com.isoft.iradar.inc.schema;

import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_CHAR;
import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_FLOAT;
import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_ID;
import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_INT;
import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_TEXT;
import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_UINT;
import static com.isoft.biz.daoimpl.radar.CDB.TABLE_TYPE_CONFIG;
import static com.isoft.biz.daoimpl.radar.CDB.TABLE_TYPE_HISTORY;
import static com.isoft.types.CArray.map;

import java.util.Map;

public class SchemaPartI extends Schema {

	public final static Map<String, Map<String,Object>> SCHEMAS = (Map)map(
			"maintenances" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "maintenanceid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"maintenanceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"maintenance_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"description" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"active_since" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"active_till" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"hosts" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "hostid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"proxy_hostid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"host" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"disable_until" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"error" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"available" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"errors_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"lastaccess" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ipmi_authtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ipmi_privilege" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"ipmi_username" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 16,
						"default" , ""
					),
					"ipmi_password" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 20,
						"default" , ""
					),
					"ipmi_disable_until" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ipmi_available" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmp_disable_until" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmp_available" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"maintenanceid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "maintenances",
						"ref_field" , "maintenanceid"
					),
					"maintenance_status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"maintenance_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"maintenance_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ipmi_errors_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmp_errors_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ipmi_error" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"snmp_error" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"jmx_disable_until" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"jmx_available" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"jmx_errors_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"jmx_error" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"flags" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					)
				)
			),
			"groups" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "groupid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"internal" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"flags" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"group_prototype" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "group_prototypeid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"group_prototypeid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"groupid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "group_prototype",
						"ref_field" , "group_prototypeid"
					)
				)
			),
			"group_discovery" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "groupid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					),
					"parent_group_prototypeid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "group_prototype",
						"ref_field" , "group_prototypeid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"lastcheck" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ts_delete" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"screens" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "screenid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"screenid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255
					),
					"hsize" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"vsize" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					)
				)
			),
			"screens_items" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "screenitemid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"screenitemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"screenid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "screens",
						"ref_field" , "screenid"
					),
					"resourcetype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"resourceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"default" , "0"
					),
					"width" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "320"
					),
					"height" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "200"
					),
					"x" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"y" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"colspan" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"rowspan" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"elements" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "25"
					),
					"valign" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"halign" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"style" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"url" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"dynamic" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"sort_triggers" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"application" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"slideshows" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "slideshowid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"slideshowid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"delay" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"slides" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "slideid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"slideid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"slideshowid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "slideshows",
						"ref_field" , "slideshowid"
					),
					"screenid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "screens",
						"ref_field" , "screenid"
					),
					"step" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"delay" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"drules" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "druleid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"druleid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"proxy_hostid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"iprange" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"delay" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "3600"
					),
					"nextcheck" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"dchecks" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "dcheckid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"dcheckid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"druleid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "drules",
						"ref_field" , "druleid"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"key_" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"snmp_community" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"ports" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , "0"
					),
					"snmpv3_securityname" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"snmpv3_securitylevel" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_authpassphrase" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"snmpv3_privpassphrase" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"uniq" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_authprotocol" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_privprotocol" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_contextname" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"applications" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "applicationid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"applicationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"httptest" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "httptestid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"httptestid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"applicationid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "applications",
						"ref_field" , "applicationid"
					),
					"nextcheck" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"delay" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "60"
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"variables" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"agent" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"authentication" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"http_user" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"http_password" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "httptest",
						"ref_field" , "httptestid"
					),
					"http_proxy" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"retries" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					)
				)
			),
			"httpstep" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "httpstepid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"httpstepid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"httptestid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "httptest",
						"ref_field" , "httptestid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"no" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"url" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"timeout" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "30"
					),
					"posts" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"required" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"status_codes" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"variables" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					)
				)
			),
			"interface" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "interfaceid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"interfaceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"main" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"useip" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , "127.0.0.1"
					),
					"dns" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"port" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , "10050"
					)
				)
			),
			"valuemaps" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "valuemapid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"valuemapid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					)
				)
			),
			"items" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "itemid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmp_community" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"snmp_oid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"key_" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"delay" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"history" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "90"
					),
					"trends" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"trapper_hosts" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"units" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"multiplier" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"delta" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_securityname" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"snmpv3_securitylevel" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_authpassphrase" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"snmpv3_privpassphrase" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"formula" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , "1"
					),
					"error" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"lastlogsize" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20,
						"default" , "0"
					),
					"logtimefmt" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"valuemapid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "valuemaps",
						"ref_field" , "valuemapid"
					),
					"delay_flex" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"params" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"ipmi_sensor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"data_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"authtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"username" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"password" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"publickey" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"privatekey" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"mtime" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"flags" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"filter" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"interfaceid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "interface",
						"ref_field" , "interfaceid"
					),
					"port" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"description" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"inventory_link" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"lifetime" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , "30"
					),
					"snmpv3_authprotocol" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_privprotocol" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"state" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"snmpv3_contextname" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"httpstepitem" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "httpstepitemid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"httpstepitemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"httpstepid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "httpstep",
						"ref_field" , "httpstepid"
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"httptestitem" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "httptestitemid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"httptestitemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"httptestid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "httptest",
						"ref_field" , "httptestid"
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"media_type" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "mediatypeid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"mediatypeid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"description" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , ""
					),
					"smtp_server" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"smtp_helo" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"smtp_email" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"exec_path" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"gsm_modem" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"username" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"passwd" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"users" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "userid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"userid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64
					),
					"alias" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , ""
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , ""
					),
					"surname" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , ""
					),
					"passwd" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , ""
					),
					"url" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"autologin" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"autologout" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "900"
					),
					"lang" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 5,
						"default" , "en_GB"
					),
					"refresh" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "30"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"theme" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , "default"
					),
					"attempt_failed" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10
					),
					"attempt_ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"attempt_clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10
					),
					"rows_per_page" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , 50
					)
				)
			),
			"usrgrp" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "usrgrpid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"usrgrpid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"gui_access" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"users_status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"debug_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"users_groups" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"usrgrpid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "usrgrp",
						"ref_field" , "usrgrpid"
					),
					"userid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"ref_table" , "users",
						"ref_field" , "userid"
					)
				)
			),
			"scripts" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "scriptid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"scriptid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"command" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"host_access" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"usrgrpid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "usrgrp",
						"ref_field" , "usrgrpid"
					),
					"groupid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					),
					"description" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"confirmation" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"execute_on" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					)
				)
			),
			"actions" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "actionid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"actionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"eventsource" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"evaltype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"esc_period" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"def_shortdata" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"def_longdata" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"recovery_msg" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"r_shortdata" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"r_longdata" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					)
				)
			),
			"operations" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "operationid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"actionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "actions",
						"ref_field" , "actionid"
					),
					"operationtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"esc_period" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"esc_step_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"esc_step_to" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"evaltype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"opmessage" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "operationid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"default_msg" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"subject" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"message" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"mediatypeid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "media_type",
						"ref_field" , "mediatypeid"
					)
				)
			),
			"opmessage_grp" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "opmessage_grpid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"opmessage_grpid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"usrgrpid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "usrgrp",
						"ref_field" , "usrgrpid"
					)
				)
			),
			"opmessage_usr" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "opmessage_usrid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"opmessage_usrid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"userid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"ref_table" , "users",
						"ref_field" , "userid"
					)
				)
			),
			"opcommand" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "operationid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"scriptid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "scripts",
						"ref_field" , "scriptid"
					),
					"execute_on" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"port" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"authtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"username" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"password" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"publickey" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"privatekey" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"command" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					)
				)
			),
			"opcommand_hst" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "opcommand_hstid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"opcommand_hstid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"hostid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					)
				)
			),
			"opcommand_grp" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "opcommand_grpid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"opcommand_grpid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					)
				)
			),
			"opgroup" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "opgroupid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"opgroupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					)
				)
			),
			"optemplate" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "optemplateid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"optemplateid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"templateid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					)
				)
			),
			"opconditions" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "opconditionid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"opconditionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"operationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "operations",
						"ref_field" , "operationid"
					),
					"conditiontype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"operator" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"conditions" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "conditionid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"conditionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"actionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "actions",
						"ref_field" , "actionid"
					),
					"conditiontype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"operator" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"config" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "configid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"configid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"refresh_unsupported" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"work_period" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , "1-5,00:00-24:00"
					),
					"alert_usrgrpid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "usrgrp",
						"ref_field" , "usrgrpid"
					),
					"event_ack_enable" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"event_expire" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "7"
					),
					"event_show_max" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "100"
					),
					"default_theme" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , "originalblue"
					),
					"authentication_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ldap_host" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"ldap_port" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , 389
					),
					"ldap_base_dn" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"ldap_bind_dn" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"ldap_bind_password" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"ldap_search_attribute" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"dropdown_first_entry" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"dropdown_first_remember" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"discovery_groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					),
					"max_in_table" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "50"
					),
					"search_limit" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1000"
					),
					"severity_color_0" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "DBDBDB"
					),
					"severity_color_1" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "D6F6FF"
					),
					"severity_color_2" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "FFF6A5"
					),
					"severity_color_3" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "FFB689"
					),
					"severity_color_4" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "FF9999"
					),
					"severity_color_5" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "FF3838"
					),
					"severity_name_0" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , "Not classified"
					),
					"severity_name_1" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , "Information"
					),
					"severity_name_2" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , "Warning"
					),
					"severity_name_3" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , "Average"
					),
					"severity_name_4" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , "High"
					),
					"severity_name_5" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , "Disaster"
					),
					"ok_period" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1800"
					),
					"blink_period" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1800"
					),
					"problem_unack_color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "DC0000"
					),
					"problem_ack_color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "DC0000"
					),
					"ok_unack_color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "00AA00"
					),
					"ok_ack_color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "00AA00"
					),
					"problem_unack_style" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"problem_ack_style" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"ok_unack_style" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"ok_ack_style" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"snmptrap_logging" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"server_check_interval" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "10"
					),
					"hk_events_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"hk_events_trigger" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_events_internal" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_events_discovery" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_events_autoreg" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_services_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"hk_services" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_audit_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"hk_audit" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_sessions_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"hk_sessions" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					),
					"hk_history_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"hk_history_global" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"hk_history" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "90"
					),
					"hk_trends_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"hk_trends_global" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"hk_trends" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "365"
					)
				)
			),
			"triggers" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "triggerid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"triggerid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"expression" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 2048,
						"default" , ""
					),
					"description" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"priority" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"lastchange" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"comments" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"error" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"state" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"flags" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"trigger_depends" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "triggerdepid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"triggerdepid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"triggerid_down" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"triggerid_up" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					)
				)
			),
			"functions" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "functionid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"functionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"triggerid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"function" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 12,
						"default" , ""
					),
					"parameter" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , "0"
					)
				)
			),
			"graphs" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "graphid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"graphid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"width" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "900"
					),
					"height" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "200"
					),
					"yaxismin" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0"
					),
					"yaxismax" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "100"
					),
					"templateid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "graphs",
						"ref_field" , "graphid"
					),
					"show_work_period" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"show_triggers" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"graphtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"show_legend" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"show_3d" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"percent_left" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0"
					),
					"percent_right" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0"
					),
					"ymin_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ymax_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ymin_itemid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"ymax_itemid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"flags" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"graphs_items" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "gitemid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"gitemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"graphid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "graphs",
						"ref_field" , "graphid"
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"drawtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"sortorder" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "009600"
					),
					"yaxisside" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"calc_fnc" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"graph_theme" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "graphthemeid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"graphthemeid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"description" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"theme" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"backgroundcolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "F0F0F0"
					),
					"graphcolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "FFFFFF"
					),
					"graphbordercolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "222222"
					),
					"gridcolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "CCCCCC"
					),
					"maingridcolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "AAAAAA"
					),
					"gridbordercolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "000000"
					),
					"textcolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "202020"
					),
					"highlightcolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "AA4444"
					),
					"leftpercentilecolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "11CC11"
					),
					"rightpercentilecolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "CC1111"
					),
					"nonworktimecolor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "CCCCCC"
					),
					"gridview" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , 1
					),
					"legendview" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , 1
					)
				)
			),
			"globalmacro" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "globalmacroid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"globalmacroid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"macro" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"hostmacro" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "hostmacroid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"hostmacroid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"macro" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"hosts_groups" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "hostgroupid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"hostgroupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					)
				)
			),
			"hosts_templates" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "hosttemplateid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"hosttemplateid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"templateid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					)
				)
			),
			"items_applications" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "itemappid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemappid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"applicationid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "applications",
						"ref_field" , "applicationid"
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					)
				)
			),
			"mappings" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "mappingid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"mappingid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"valuemapid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "valuemaps",
						"ref_field" , "valuemapid"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"newvalue" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					)
				)
			),
			"media" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "mediaid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"mediaid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"userid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"ref_table" , "users",
						"ref_field" , "userid"
					),
					"mediatypeid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "media_type",
						"ref_field" , "mediatypeid"
					),
					"sendto" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , ""
					),
					"active" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"severity" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "63"
					),
					"period" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 100,
						"default" , "1-7,00:00-24:00"
					)
				)
			)
		);
}
