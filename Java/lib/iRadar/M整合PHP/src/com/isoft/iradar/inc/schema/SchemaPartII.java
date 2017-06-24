package com.isoft.iradar.inc.schema;

import static com.isoft.biz.daoimpl.radar.CDB.FIELD_TYPE_BLOB;
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

public class SchemaPartII extends Schema {

	public final static Map<String, Map<String,Object>> SCHEMAS = (Map)map(
			"rights" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "rightid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"rightid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "usrgrp",
						"ref_field" , "usrgrpid"
					),
					"permission" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "groups",
						"ref_field" , "groupid"
					)
				)
			),
			"services" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "serviceid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"serviceid" , map(
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
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"algorithm" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"triggerid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"showsla" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"goodsla" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "99.9"
					),
					"sortorder" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"services_links" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "linkid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"linkid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"serviceupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "services",
						"ref_field" , "serviceid"
					),
					"servicedownid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "services",
						"ref_field" , "serviceid"
					),
					"soft" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"services_times" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "timeid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"timeid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"serviceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "services",
						"ref_field" , "serviceid"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ts_from" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ts_to" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"note" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"icon_map" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "iconmapid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"iconmapid" , map(
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
					"default_iconid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					)
				)
			),
			"icon_mapping" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "iconmappingid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"iconmappingid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"iconmapid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "icon_map",
						"ref_field" , "iconmapid"
					),
					"iconid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					),
					"inventory_link" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"expression" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"sortorder" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"sysmaps" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "sysmapid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"sysmapid" , map(
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
						"default" , "600"
					),
					"height" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "400"
					),
					"backgroundid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					),
					"label_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"label_location" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"highlight" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"expandproblem" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"markelements" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"show_unack" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"grid_size" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "50"
					),
					"grid_show" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"grid_align" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					),
					"label_format" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"label_type_host" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"label_type_hostgroup" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"label_type_trigger" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"label_type_map" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"label_type_image" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "2"
					),
					"label_string_host" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"label_string_hostgroup" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"label_string_trigger" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"label_string_map" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"label_string_image" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"iconmapid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "icon_map",
						"ref_field" , "iconmapid"
					),
					"expand_macros" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"severity_min" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"sysmaps_elements" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "selementid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"selementid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"sysmapid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps",
						"ref_field" , "sysmapid"
					),
					"elementid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"default" , "0"
					),
					"elementtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"iconid_off" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					),
					"iconid_on" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					),
					"label" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 2048,
						"default" , ""
					),
					"label_location" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "-1"
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
					"iconid_disabled" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					),
					"iconid_maintenance" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "images",
						"ref_field" , "imageid"
					),
					"elementsubtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"areatype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"width" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "200"
					),
					"height" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "200"
					),
					"viewtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"use_iconmap" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "1"
					)
				)
			),
			"sysmaps_links" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "linkid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"linkid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"sysmapid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps",
						"ref_field" , "sysmapid"
					),
					"selementid1" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps_elements",
						"ref_field" , "selementid"
					),
					"selementid2" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps_elements",
						"ref_field" , "selementid"
					),
					"drawtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "000000"
					),
					"label" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 2048,
						"default" , ""
					)
				)
			),
			"sysmaps_link_triggers" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "linktriggerid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"linktriggerid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"linkid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps_links",
						"ref_field" , "linkid"
					),
					"triggerid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"drawtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"color" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 6,
						"default" , "000000"
					)
				)
			),
			"sysmap_element_url" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "sysmapelementurlid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"sysmapelementurlid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"selementid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps_elements",
						"ref_field" , "selementid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255
					),
					"url" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"sysmap_url" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "sysmapurlid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"sysmapurlid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"sysmapid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "sysmaps",
						"ref_field" , "sysmapid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255
					),
					"url" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"elementtype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"maintenances_hosts" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "maintenance_hostid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"maintenance_hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"maintenanceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "maintenances",
						"ref_field" , "maintenanceid"
					),
					"hostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					)
				)
			),
			"maintenances_groups" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "maintenance_groupid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"maintenance_groupid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"maintenanceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "maintenances",
						"ref_field" , "maintenanceid"
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
			"timeperiods" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "timeperiodid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"timeperiodid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"timeperiod_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"every" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"month" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"dayofweek" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"day" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"start_time" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"period" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"start_date" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"maintenances_windows" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "maintenance_timeperiodid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"maintenance_timeperiodid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"maintenanceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "maintenances",
						"ref_field" , "maintenanceid"
					),
					"timeperiodid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "timeperiods",
						"ref_field" , "timeperiodid"
					)
				)
			),
			"regexps" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "regexpid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"regexpid" , map(
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
					"test_string" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					)
				)
			),
			"expressions" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "expressionid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"expressionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"regexpid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "regexps",
						"ref_field" , "regexpid"
					),
					"expression" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"expression_type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"exp_delimiter" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 1,
						"default" , ""
					),
					"case_sensitive" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"ids" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "table_name,field_name",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"table_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"field_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"nextid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					)
				)
			),
			"alerts" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "alertid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"alertid" , map(
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
					"eventid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "events",
						"ref_field" , "eventid"
					),
					"userid" , map(
						"null" , true,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"ref_table" , "users",
						"ref_field" , "userid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"mediatypeid" , map(
						"null" , true,
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
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"retries" , map(
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
					"esc_step" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"alerttype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0.0000"
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_sync" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0.0000"
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_uint" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20,
						"default" , "0"
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_uint_sync" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20,
						"default" , "0"
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_str" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
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
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_str_sync" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
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
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_log" , map(
				"type" , TABLE_TYPE_HISTORY,
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
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"timestamp" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"source" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"severity" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"logeventid" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"history_text" , map(
				"type" , TABLE_TYPE_HISTORY,
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
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"proxy_history" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"timestamp" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"source" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"severity" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"logeventid" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ns" , map(
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
					)
				)
			),
			"proxy_dhistory" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
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
					"ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"port" , map(
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
					"value" , map(
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
					"dcheckid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "dchecks",
						"ref_field" , "dcheckid"
					),
					"dns" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					)
				)
			),
			"events" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "eventid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"eventid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"source" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"object" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"objectid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"default" , "0"
					),
					"clock" , map(
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
					"acknowledged" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"ns" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"trends" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "itemid,clock",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"num" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value_min" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0.0000"
					),
					"value_avg" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0.0000"
					),
					"value_max" , map(
						"null" , false,
						"type" , FIELD_TYPE_FLOAT,
						"length" , 16,
						"default" , "0.0000"
					)
				)
			),
			"trends_uint" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "itemid,clock",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"num" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value_min" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20,
						"default" , "0"
					),
					"value_avg" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20,
						"default" , "0"
					),
					"value_max" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20,
						"default" , "0"
					)
				)
			),
			"acknowledges" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "acknowledgeid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"acknowledgeid" , map(
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
					"eventid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "events",
						"ref_field" , "eventid"
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"message" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"auditlog" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "auditid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"auditid" , map(
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
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"action" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"resourcetype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"details" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , "0"
					),
					"ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"resourceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"default" , "0"
					),
					"resourcename" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"auditlog_details" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "auditdetailid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"auditdetailid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"auditid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "auditlog",
						"ref_field" , "auditid"
					),
					"table_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"field_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"oldvalue" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"newvalue" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					)
				)
			),
			"service_alarms" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "servicealarmid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"servicealarmid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"serviceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "services",
						"ref_field" , "serviceid"
					),
					"clock" , map(
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
					)
				)
			),
			"autoreg_host" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "autoreg_hostid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"autoreg_hostid" , map(
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
					"listen_ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"listen_port" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"listen_dns" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"host_metadata" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"proxy_autoreg_host" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "id",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"id" , map(
						"null" , false,
						"type" , FIELD_TYPE_UINT,
						"length" , 20
					),
					"clock" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"host" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"listen_ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"listen_port" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"listen_dns" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"host_metadata" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"dhosts" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "dhostid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"dhostid" , map(
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
					"status" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"lastup" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"lastdown" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"dservices" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "dserviceid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"dserviceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"dhostid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "dhosts",
						"ref_field" , "dhostid"
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
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"port" , map(
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
					"lastup" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"lastdown" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"dcheckid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "dchecks",
						"ref_field" , "dcheckid"
					),
					"ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"dns" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					)
				)
			),
			"escalations" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "escalationid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"escalationid" , map(
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
					"triggerid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"eventid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "events",
						"ref_field" , "eventid"
					),
					"r_eventid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "events",
						"ref_field" , "eventid"
					),
					"nextcheck" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"esc_step" , map(
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
					"itemid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					)
				)
			),
			"globalvars" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "globalvarid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"globalvarid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"snmp_lastsize" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"graph_discovery" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "graphdiscoveryid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"graphdiscoveryid" , map(
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
					"parent_graphid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "graphs",
						"ref_field" , "graphid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					)
				)
			),
			"host_inventory" , map(
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
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"inventory_mode" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"type_full" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"alias" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"os" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"os_full" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"os_short" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"serialno_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"serialno_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"tag" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"asset_tag" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"macaddress_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"macaddress_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"hardware" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"hardware_full" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"software" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"software_full" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"software_app_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"software_app_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"software_app_c" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"software_app_d" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"software_app_e" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"contact" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"location" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"location_lat" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 16,
						"default" , ""
					),
					"location_lon" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 16,
						"default" , ""
					),
					"notes" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"chassis" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"model" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"hw_arch" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , ""
					),
					"vendor" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"contract_number" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"installer_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"deployment_status" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"url_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url_c" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"host_networks" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"host_netmask" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"host_router" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"oob_ip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"oob_netmask" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"oob_router" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 39,
						"default" , ""
					),
					"date_hw_purchase" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"date_hw_install" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"date_hw_expiry" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"date_hw_decomm" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"site_address_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"site_address_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"site_address_c" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"site_city" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"site_state" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"site_country" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"site_zip" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"site_rack" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"site_notes" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"poc_1_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"poc_1_email" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"poc_1_phone_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_1_phone_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_1_cell" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_1_screen" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_1_notes" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					),
					"poc_2_name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"poc_2_email" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 128,
						"default" , ""
					),
					"poc_2_phone_a" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_2_phone_b" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_2_cell" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_2_screen" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"poc_2_notes" , map(
						"null" , false,
						"type" , FIELD_TYPE_TEXT,
						"default" , ""
					)
				)
			),
			"housekeeper" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "housekeeperid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"housekeeperid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"tablename" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"field" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , ""
					),
					"value" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "value"
					)
				)
			),
			"images" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "imageid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"imageid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"imagetype" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"default" , "0"
					),
					"image" , map(
						"null" , false,
						"type" , FIELD_TYPE_BLOB,
						"length" , 2048,
						"default" , ""
					)
				)
			),
			"item_discovery" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "itemdiscoveryid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"itemdiscoveryid" , map(
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
					"parent_itemid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"key_" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
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
			"host_discovery" , map(
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
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"parent_hostid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "hosts",
						"ref_field" , "hostid"
					),
					"parent_itemid" , map(
						"null" , true,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "items",
						"ref_field" , "itemid"
					),
					"host" , map(
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
			"interface_discovery" , map(
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
						"length" , 20,
						"ref_table" , "interface",
						"ref_field" , "interfaceid"
					),
					"parent_interfaceid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "interface",
						"ref_field" , "interfaceid"
					)
				)
			),
			"profiles" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "profileid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"profileid" , map(
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
					"idx" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 96,
						"default" , ""
					),
					"idx2" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"default" , "0"
					),
					"value_id" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"default" , "0"
					),
					"value_int" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"value_str" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"source" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 96,
						"default" , ""
					),
					"type" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			),
			"sessions" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "sessionid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"sessionid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 32,
						"default" , ""
					),
					"userid" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 64,
						"ref_table" , "users",
						"ref_field" , "userid"
					),
					"lastaccess" , map(
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
			"trigger_discovery" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "triggerdiscoveryid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"triggerdiscoveryid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20
					),
					"triggerid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"parent_triggerid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "triggers",
						"ref_field" , "triggerid"
					),
					"name" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"user_history" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "userhistoryid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"userhistoryid" , map(
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
					"title1" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url1" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"title2" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url2" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"title3" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url3" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"title4" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url4" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"title5" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					),
					"url5" , map(
						"null" , false,
						"type" , FIELD_TYPE_CHAR,
						"length" , 255,
						"default" , ""
					)
				)
			),
			"application_template" , map(
				"type" , TABLE_TYPE_CONFIG,
				"key" , "application_templateid",
				"fields" , map(
					"tenantid" , map(
						"null" , TENANTID_NULLABLE,
						"type" , TENANTID_TYPE,
						"length" , TENANTID_LENGTH
					),
					"application_templateid" , map(
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
					"templateid" , map(
						"null" , false,
						"type" , FIELD_TYPE_ID,
						"length" , 20,
						"ref_table" , "applications",
						"ref_field" , "applicationid"
					)
				)
			),
			"dbversion" , map(
				"type" , TABLE_TYPE_HISTORY,
				"key" , "",
				"fields" , map(
					"mandatory" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					),
					"optional" , map(
						"null" , false,
						"type" , FIELD_TYPE_INT,
						"length" , 10,
						"default" , "0"
					)
				)
			)
		);
}
