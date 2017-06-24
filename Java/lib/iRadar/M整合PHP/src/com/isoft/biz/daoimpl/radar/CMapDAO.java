package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_CUSTOM;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_IP;
import static com.isoft.iradar.inc.Defines.MAP_LABEL_TYPE_STATUS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_MAP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_MAP;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.SYSMAP_GRID_ALIGN_OFF;
import static com.isoft.iradar.inc.Defines.SYSMAP_GRID_ALIGN_ON;
import static com.isoft.iradar.inc.Defines.SYSMAP_GRID_SHOW_OFF;
import static com.isoft.iradar.inc.Defines.SYSMAP_GRID_SHOW_ON;
import static com.isoft.iradar.inc.Defines.SYSMAP_LABEL_ADVANCED_ON;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_diff;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_subarray_push;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.MapsUtil.sysmapElementLabel;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CIconMapGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class CMapDAO extends CMapElementDAO<CMapGet> {

	public CMapDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "sysmaps", "s", new String[]{"name", "width", "height"});
	}
	
	/**
	 * Get map data.
	 *
	 * @param array  options
	 * @param array  options['groupids']					HostGroup IDs
	 * @param array  options['hostids']					Host IDs
	 * @param bool   options['monitored_hosts']			only monitored Hosts
	 * @param bool   options['templated_hosts']			include templates in result
	 * @param bool   options['with_items']					only with items
	 * @param bool   options['with_monitored_items']		only with monitored items
	 * @param bool   options['with_triggers'] only with	triggers
	 * @param bool   options['with_monitored_triggers']	only with monitored triggers
	 * @param bool   options['with_httptests'] only with	http tests
	 * @param bool   options['with_monitored_httptests']	only with monitored http tests
	 * @param bool   options['with_graphs']				only with graphs
	 * @param bool   options['editable']					only with read-write permission. Ignored for SuperAdmins
	 * @param int    options['count']						count Hosts, returned column name is rowscount
	 * @param string options['pattern']					search hosts by pattern in host names
	 * @param int    options['limit']						limit selection
	 * @param string options['sortorder']
	 * @param string options['sortfield']
	 *
	 * @return array|boolean Host data as array or false if error
	 */
	@Override
	public <T> T get(CMapGet params) {
		int userType = CWebUser.getType();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("sysmaps", "s.sysmapid");
		sqlParts.from.put("sysmaps", "sysmaps s");
		
		// sysmapids
		if (!is_null(params.getSysmapIds())) {
			sqlParts.where.dbConditionInt("sysmapid","s.sysmapid",params.getSysmapIds());
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("sysmaps s", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("sysmaps s", params, sqlParts);
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
		CArray sysmapids = array();
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
				ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("sysmapid");
				
				Nest.value(sysmapids,id).$(id);
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				result.get(id).putAll(row);
			}
		}
		
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			if (!empty(result)) {
				CArray<Long> linkTriggers = array();

				sqlParts = new SqlBuilder();
				CArray<Map> dbLinkTriggers = DBselect(getSqlExecutor(),
					"SELECT slt.triggerid,sl.sysmapid"+
					" FROM sysmaps_link_triggers slt,sysmaps_links sl"+
					" WHERE "+sqlParts.dual.dbConditionInt("sl.sysmapid", sysmapids.valuesAsLong())+
						" AND sl.tenantid=slt.tenantid"+
						" AND sl.linkid=slt.linkid",
					sqlParts.getNamedParams()
				);
				for(Map linkTrigger : dbLinkTriggers) {
					Nest.value(linkTriggers,linkTrigger.get("sysmapid")).$(Nest.value(linkTrigger,"triggerid").$());
				}

				if (!empty(linkTriggers)) {
					CTriggerGet toptions = new CTriggerGet();
					toptions.setTriggerIds(linkTriggers.valuesAsLong());
					toptions.setEditable(params.getEditable());
					toptions.setOutput(new String[]{"triggerid"});
					toptions.setPreserveKeys(true);
					CArray<Map> allTriggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(toptions);
					for (Entry<Object, Long> e : linkTriggers.entrySet()) {
					    Object id = e.getKey();
					    Long triggerid = e.getValue();
						if (!isset(allTriggers,triggerid)) {
							unset(result,id);
							unset(sysmapids,id);
						}
					}
				}

				CArray hostsToCheck = array();
				CArray mapsToCheck = array();
				CArray triggersToCheck = array();
				CArray hostGroupsToCheck = array();

				CArray<Map> selements = array();
				sqlParts = new SqlBuilder();
				CArray<Map> dbSelements = DBselect(getSqlExecutor(),
						"SELECT se.* FROM sysmaps_elements se WHERE "+sqlParts.dual.dbConditionInt("se.sysmapid", sysmapids.valuesAsLong()),
						sqlParts.getNamedParams());
				for(Map selement : dbSelements) {
					Nest.value(selements,selement.get("selementid")).$(selement);

					switch (Nest.value(selement,"elementtype").asInteger()) {
						case SYSMAP_ELEMENT_TYPE_HOST:
							Nest.value(hostsToCheck,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
							break;
						case SYSMAP_ELEMENT_TYPE_MAP:
							Nest.value(mapsToCheck,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
							break;
						case SYSMAP_ELEMENT_TYPE_TRIGGER:
							Nest.value(triggersToCheck,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
							break;
						case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
							Nest.value(hostGroupsToCheck,selement.get("elementid")).$(Nest.value(selement,"elementid").$());
							break;
					}
				}

				if (!empty(hostsToCheck)) {
					CHostGet hoptions = new CHostGet();
					hoptions.setHostIds(hostsToCheck.valuesAsLong());
					hoptions.setEditable(params.getEditable());
					hoptions.setPreserveKeys(true);
					hoptions.setOutput(new String[]{"hostid"});
					CArray<Map> allowedHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

					for(Object elementid : hostsToCheck) {
						if (!isset(allowedHosts,elementid)) {
							for (Entry<Object, Map> e : selements.entrySet()) {
							    Object selementid = e.getKey();
							    Map selement = e.getValue();
								if (Nest.value(selement,"elementtype").asInteger() == SYSMAP_ELEMENT_TYPE_HOST
										&& bccomp(Nest.value(selement,"elementid").$(), elementid) == 0) {
									unset(result,selement.get("sysmapid"));
									unset(selements,selementid);
								}
							}
						}
					}
				}

				if (!empty(mapsToCheck)) {
					CMapGet moptions = new CMapGet();
					moptions.setSysmapIds(mapsToCheck.valuesAsLong());
					moptions.setEditable(params.getEditable());
					moptions.setPreserveKeys(true);
					moptions.setOutput(new String[]{"sysmapid"});
					CArray<Map> allowedMaps = get(moptions);

					for(Object elementid : mapsToCheck) {
						if (!isset(allowedMaps,elementid)) {
							for (Entry<Object, Map> e : selements.entrySet()) {
							    Object selementid = e.getKey();
							    Map selement = e.getValue();
								if (Nest.value(selement,"elementtype").asInteger() == SYSMAP_ELEMENT_TYPE_MAP
										&& bccomp(Nest.value(selement,"elementid").$(), elementid) == 0) {
									unset(result,selement.get("sysmapid"));
									unset(selements,selementid);
								}
							}
						}
					}
				}

				if (!empty(triggersToCheck)) {
					CTriggerGet toptions = new CTriggerGet();
					toptions.setTriggerIds(triggersToCheck.valuesAsLong());
					toptions.setEditable(params.getEditable());
					toptions.setPreserveKeys(true);
					toptions.setOutput(new String[]{"triggerid"});
					CArray<Map> allowedTriggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(toptions);

					for(Object elementid : triggersToCheck) {
						if (!isset(allowedTriggers,elementid)) {
							for (Entry<Object, Map> e : selements.entrySet()) {
							    Object selementid = e.getKey();
							    Map selement = e.getValue();
								if (Nest.value(selement,"elementtype").asInteger() == SYSMAP_ELEMENT_TYPE_TRIGGER
										&& bccomp(Nest.value(selement,"elementid").$(), elementid) == 0) {
									unset(result,selement.get("sysmapid"));
									unset(selements,selementid);
								}
							}
						}
					}
				}

				if (!empty(hostGroupsToCheck)) {
					CHostGroupGet hgoptions = new CHostGroupGet();
					hgoptions.setTriggerIds(hostGroupsToCheck.valuesAsLong());
					hgoptions.setEditable(params.getEditable());
					hgoptions.setPreserveKeys(true);
					hgoptions.setOutput(new String[]{"groupid"});
					CArray<Map> allowedHostGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgoptions);

					for(Object elementid : hostGroupsToCheck) {
						if (!isset(allowedHostGroups,elementid)) {
							for (Entry<Object, Map> e : Clone.deepcopy(selements).entrySet()) {
							    Object selementid = e.getKey();
							    Map selement = e.getValue();
								if (Nest.value(selement,"elementtype").asInteger() == SYSMAP_ELEMENT_TYPE_HOST_GROUP
										&& bccomp(Nest.value(selement,"elementid").$(), elementid) == 0) {
									unset(result,selement.get("sysmapid"));
									unset(selements,selementid);
								}
							}
						}
					}
				}
			}
		}		
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
		}
	
		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Get Sysmap IDs by Sysmap params.
	 *
	 * @param array sysmapData
	 * @param array sysmapData["name"]
	 * @param array sysmapData["sysmapid"]
	 *
	 * @return string sysmapid
	 */
	@Override
	public CArray<Map> getObjects(Map<String, Object[]> sysmapData) {
		CMapGet options = new CMapGet();
		options.setFilter(sysmapData);
		options.setOutput(API_OUTPUT_EXTEND);
		return get(options);
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("sysmapid", "name"));
		CMapGet options = new CMapGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"sysmapid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	public CArray<Map> checkInput(CArray<Map> maps, String method) {
		boolean create = ("create".equals(method));
		boolean update = ("update".equals(method));
		boolean delete = ("delete".equals(method));

		// permissions
		CArray mapDbFields = null;
		CArray<Map> dbMaps = null;
		if (update || delete) {
			mapDbFields = map("sysmapid", null);
			CMapGet moptions = new CMapGet();
			moptions.setSysmapIds(rda_objectValues(maps, "sysmapid").valuesAsLong());
			moptions.setOutput(API_OUTPUT_EXTEND);
			moptions.setEditable(true);
			moptions.setPreserveKeys(true);
			moptions.setSelectLinks(API_OUTPUT_EXTEND);
			moptions.setSelectSelements(API_OUTPUT_EXTEND);
			moptions.setSelectUrls(API_OUTPUT_EXTEND);
			dbMaps = get(moptions);
		} else {
			mapDbFields = map(
				"name", null,
				"width", null,
				"height", null,
				"urls", array(),
				"selements", array(),
				"links", array()
			);
		}

		CArray mapNames = array();
		Map dbMap = null;
		for(Map map : maps) {
			if (!check_db_fields(mapDbFields, map)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect fields for sysmap."));
			}

			if (update || delete) {
				if (!isset(dbMaps,map.get("sysmapid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				dbMap = array_merge(dbMaps.get(map.get("sysmapid")), map);
			} else {
				dbMap = Clone.deepcopy(map);
			}

			if (isset(map,"name")) {
				if (isset(mapNames,map.get("name"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Duplicate map name for map \"%s\".", Nest.value(dbMap,"name").$()));
				} else {
					Nest.value(mapNames,map.get("name")).$(update ? Nest.value(map,"sysmapid").$() : 1);
				}
			}

			if (isset(map,"width") && (Nest.value(map,"width").asInteger() > 65535 || Nest.value(map,"width").asInteger() < 1)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect map width value for map \"%s\".", Nest.value(dbMap,"name").$()));
			}

			if (isset(map,"height") && (Nest.value(map,"height").asInteger() > 65535 || Nest.value(map,"height").asInteger() < 1)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect map height value for map \"%s\".", Nest.value(dbMap,"name").$()));
			}

			// labels
			CArray<Map> mapLabels = map("label_type", map("typeName", _("icon")));

			if (Nest.value(dbMap,"label_format").asInteger() == SYSMAP_LABEL_ADVANCED_ON) {
				Nest.value(mapLabels,"label_type_hostgroup").$(map("string", "label_string_hostgroup", "typeName", _("host group")));
				Nest.value(mapLabels,"label_type_host").$(map("string", "label_string_host", "typeName", _("host")));
				Nest.value(mapLabels,"label_type_trigger").$(map("string", "label_string_trigger", "typeName", _("trigger")));
				Nest.value(mapLabels,"label_type_map").$(map("string", "label_string_map", "typeName", _("map")));
				Nest.value(mapLabels,"label_type_image").$(map("string", "label_string_image", "typeName", _("image")));
			}

			for (Entry<Object, Map> e : mapLabels.entrySet()) {
			    String labelName = Nest.as(e.getKey()).asString();
			    Map labelData = e.getValue();
				if (!isset(map,labelName)) {
					continue;
				}
				
				if (empty(sysmapElementLabel(Nest.value(map,labelName).asInteger()))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect %1$s label type value for map \"%2$s\".", Nest.value(labelData,"typeName").$(), Nest.value(dbMap,"name").$()));
				}

				if (Nest.value(map,labelName).asInteger() == MAP_LABEL_TYPE_CUSTOM) {
					if (!isset(labelData,"string")) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect %1$s label type value for map \"%2$s\".", Nest.value(labelData,"typeName").$(), Nest.value(dbMap,"name").$()));
					}

					if (!isset(map,labelData.get("string")) || rda_empty(Nest.value(map,labelData.get("string")).$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Custom label for map \"%2$s\" elements of type \"%1$s\" may not be empty.", Nest.value(labelData,"typeName").$(), Nest.value(dbMap,"name").$()));
					}
				}

				if ("label_type_image".equals(labelName) && Nest.value(map,labelName).asInteger() == MAP_LABEL_TYPE_STATUS) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect %1$s label type value for map \"%2$s\".", Nest.value(labelData,"typeName").$(), Nest.value(dbMap,"name").$()));
				}

				if (labelName == "label_type" || labelName == "label_type_host") {
					continue;
				}

				if (Nest.value(map,labelName).asInteger() == MAP_LABEL_TYPE_IP) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect %1$s label type value for map \"%2$s\".", Nest.value(labelData,"typeName").$(), Nest.value(dbMap,"name").$()));
				}
			}

			// validating grid options
			CArray<Integer> possibleGridSizes = array(20, 40, 50, 75, 100);

			if (update || create) {
				// grid size
				if (isset(map,"grid_size") && !in_array(Nest.value(map,"grid_size").asInteger(), possibleGridSizes)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Value \"%1$s\" is invalid for parameter \"grid_show\". Choices are: \"%2$s\".", Nest.value(map,"grid_size").$(), implode("\", \"", possibleGridSizes)));
				}

				// grid auto align
				if (isset(map,"grid_align") && Nest.value(map,"grid_align").asInteger() != SYSMAP_GRID_ALIGN_ON && Nest.value(map,"grid_align").asInteger() != SYSMAP_GRID_ALIGN_OFF) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Value \"%1$s\" is invalid for parameter \"grid_align\". Choices are: \"%2$s\" and \"%3$s\"", Nest.value(map,"grid_align").$(), SYSMAP_GRID_ALIGN_ON, SYSMAP_GRID_ALIGN_OFF));
				}

				// grid show
				if (isset(map,"grid_show") && Nest.value(map,"grid_show").asInteger() != SYSMAP_GRID_SHOW_ON && Nest.value(map,"grid_show").asInteger() != SYSMAP_GRID_SHOW_OFF) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Value \"%1$s\" is invalid for parameter \"grid_show\". Choices are: \"%2$s\" and \"%3$s\".", Nest.value(map,"grid_show").$(), SYSMAP_GRID_SHOW_ON, SYSMAP_GRID_SHOW_OFF));
				}
			}

			// urls
			if (isset(map,"urls") && !empty(Nest.value(map,"urls").$())) {
				CArray urlNames = rda_toHash(Nest.value(map,"urls").$(), "name");

				for(Map url : (CArray<Map>)Nest.value(map,"urls").asCArray()) {
					if (StringUtils.isEmpty(Nest.value(url,"name").asString()) || StringUtils.isEmpty(Nest.value(url,"url").asString())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Link should have both \"name\" and \"url\" fields for map \"%s\".", Nest.value(dbMap,"name").$()));
					}

					if (!isset(urlNames,url.get("name"))) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Link name should be unique for map \"%s\".", Nest.value(dbMap,"name").$()));
					}

					unset(urlNames,url.get("name"));
				}
			}

			// map selement links
			if (!empty(Nest.value(map,"links").$())) {
				CArray selementIds = rda_objectValues(Nest.value(dbMap,"selements").$(), "selementid");

				for(Map link : (CArray<Map>)Nest.value(map,"links").asCArray()) {
					if (!in_array(Nest.value(link,"selementid1").$(), selementIds)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("Link selementid1 field is pointing to a nonexistent map selement ID \"%1$s\" for map \"%2$s\".",
								Nest.value(link,"selementid1").$(), Nest.value(dbMap,"name").asString()
						));
					}

					if (!in_array(Nest.value(link,"selementid2").$(), selementIds)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("Link selementid2 field is pointing to a nonexistent map selement ID \"%1$s\" for map \"%2$s\".",
								Nest.value(link,"selementid2").$(), Nest.value(dbMap,"name").asString()
						));
					}
				}
			}
		}

		// exists
		if ((create || update) && !empty(mapNames)) {
			CMapGet moptions = new CMapGet();
			moptions.setFilter("name", array_keys(mapNames).valuesAsString());
			moptions.setOutput(new String[]{"sysmapid", "name"});
			moptions.setNopermissions(true);
			CArray<Map> existDbMaps = get(moptions);
			for(Map cdbMap : existDbMaps) {
				if (create || bccomp(Nest.value(mapNames,cdbMap.get("name")).$(), Nest.value(cdbMap,"sysmapid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Map with name \"%s\" already exists.", Nest.value(cdbMap,"name").$()));
				}
			}
		}

		return dbMaps;
	}
	
	/**
	 * Add map.
	 *
	 * @param array  maps
	 * @param string maps["name"]
	 * @param array  maps["width"]
	 * @param int    maps["height"]
	 * @param string maps["backgroundid"]
	 * @param string maps["highlight"]
	 * @param array  maps["label_type"]
	 * @param int    maps["label_location"]
	 * @param int    maps["grid_size"]			size of one grid cell. 100 refers to 100x100 and so on.
	 * @param int    maps["grid_show"]			does grid need to be shown. Constants: SYSMAP_GRID_SHOW_ON / SYSMAP_GRID_SHOW_OFF
	 * @param int    maps["grid_align"]		do elements need to be aligned to the grid. Constants: SYSMAP_GRID_ALIGN_ON / SYSMAP_GRID_ALIGN_OFF
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> maps) {
		checkInput(maps, "create");

		CArray<Long> sysmapIds = insert("sysmaps", maps);

		CArray<Map> newUrls = array();
		CArray newSelements = array();
		CArray<Map> newLinks = array();

		for (Entry<Object, Long> e : sysmapIds.entrySet()) {
		    Object key = e.getKey();
		    Long sysmapId = e.getValue();
			for(Map url : (CArray<Map>)Nest.value(maps,key,"urls").asCArray()) {
				Nest.value(url,"sysmapid").$(sysmapId);
				newUrls.add(url);
			}

			for (Entry<Object, Map> ee : ((CArray<Map>)Nest.value(maps,key,"selements").asCArray()).entrySet()) {
			    Object snum = ee.getKey();
			    //Map selement = ee.getValue();
				Nest.value(maps,key,"selements",snum,"sysmapid").$(sysmapId);
			}

			newSelements = array_merge(newSelements, Nest.value(maps,key,"selements").asCArray());

			for (Entry<Object, Map> ee : ((CArray<Map>)Nest.value(maps,key,"links").asCArray()).entrySet()) {
			    Object lnum = ee.getKey();
			    //Map link = ee.getValue();
				Nest.value(maps,key,"links",lnum,"sysmapid").$(sysmapId);
			}

			newLinks = array_merge(newLinks, Nest.value(maps,key,"links").asCArray());
		}

		insert("sysmap_url", newUrls);

		if (!empty(newSelements)) {
			CArray<Long[]> selementids = createSelements(newSelements);

			if (!empty(newLinks)) {
				// links
				CArray mapVirtSelements = array();
				for (Entry<Object, Long> e : ((CArray<Long>)Nest.value(selementids,"selementids").asCArray()).entrySet()) {
				    Object key = e.getKey();
				    Long selementid = e.getValue();
					Nest.value(mapVirtSelements,Nest.value(newSelements,key,"selementid").$()).$(selementid);
				}

				for (Entry<Object, Map> e : newLinks.entrySet()) {
				    Object key = e.getKey();
				    Map link = e.getValue();
					Nest.value(newLinks,key,"selementid1").$(Nest.value(mapVirtSelements,link.get("selementid1")).$());
					Nest.value(newLinks,key,"selementid2").$(Nest.value(mapVirtSelements,link.get("selementid2")).$());
				}

				CArray<Long[]> linkIds = createLinks(newLinks);

				// linkTriggers
				CArray<Map> newLinkTriggers = array();
				for (Entry<Object, Long> e : ((CArray<Long>)Nest.value(linkIds,"linkids").asCArray()).entrySet()) {
				    Object key = e.getKey();
				    Long linkId = e.getValue();
					if (!isset(Nest.value(newLinks,key,"linktriggers").$())) {
						continue;
					}

					for(Map linktrigger : (CArray<Map>)Nest.value(newLinks,key,"linktriggers").asCArray()) {
						Nest.value(linktrigger,"linkid").$(linkId);
						newLinkTriggers.add(linktrigger);
					}
				}

				if (!empty(newLinkTriggers)) {
					createLinkTriggers(newLinkTriggers);
				}
			}
		}

		return map("sysmapids", sysmapIds.valuesAsLong());
	}
	
	/**
	 * Update map.
	 *
	 * @param array  maps						multidimensional array with Hosts data
	 * @param string maps["sysmapid"]
	 * @param string maps["name"]
	 * @param array  maps["width"]
	 * @param int    maps["height"]
	 * @param string maps["backgroundid"]
	 * @param array  maps["label_type"]
	 * @param int    maps["label_location"]
	 * @param int    maps["grid_size"]			size of one grid cell. 100 refers to 100x100 and so on.
	 * @param int    maps["grid_show"]			does grid need to be shown. Constants: SYSMAP_GRID_SHOW_ON / SYSMAP_GRID_SHOW_OFF
	 * @param int    maps["grid_align"]		do elements need to be aligned to the grid. Constants: SYSMAP_GRID_ALIGN_ON / SYSMAP_GRID_ALIGN_OFF
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> maps) {
		CArray sysmapIds = rda_objectValues(maps, "sysmapid");

		CArray<Map> dbMaps = checkInput(maps, "update");

		CArray<Map> updateMaps = array();
		CArray urlIdsToDelete = array();
		CArray<Map> urlsToUpdate = array();
		CArray<Map> urlsToAdd = array();
		CArray selementsToDelete = array();
		CArray<Map> selementsToUpdate = array();
		CArray<Map> selementsToAdd = array();
		CArray linksToDelete = array();
		CArray<Map> linksToUpdate = array();
		CArray<Map> linksToAdd = array();

		for(Map map : maps) {
			updateMaps.add(map(
				"values", map,
				"where", map("sysmapid", Nest.value(map,"sysmapid").$())
			));

			Map dbMap = dbMaps.get(map.get("sysmapid"));

			// urls
			if (isset(map,"urls")) {
				CArray urlDiff = rda_array_diff(Nest.value(map,"urls").asCArray(), Nest.value(dbMap,"urls").asCArray(), "name");

				for(Map updateUrl : (CArray<Map>)Nest.value(urlDiff,"both").asCArray()) {
					urlsToUpdate.add(map(
						"values", updateUrl,
						"where", map("name", Nest.value(updateUrl,"name").$(), "sysmapid", Nest.value(map,"sysmapid").$())
					));
				}

				for(Map newUrl : (CArray<Map>)Nest.value(urlDiff,"first").asCArray()) {
					Nest.value(newUrl,"sysmapid").$(Nest.value(map,"sysmapid").$());
					urlsToAdd.add(newUrl);
				}

				urlIdsToDelete = array_merge(urlIdsToDelete, rda_objectValues(Nest.value(urlDiff,"second").$(), "sysmapurlid"));
			}

			// elements
			if (isset(map,"selements")) {
				CArray selementDiff = rda_array_diff(Nest.value(map,"selements").asCArray(), Nest.value(dbMap,"selements").asCArray(), "selementid");

				// we need sysmapid for add operations
				for(Map newSelement : (CArray<Map>)Nest.value(selementDiff,"first").asCArray()) {
					Nest.value(newSelement,"sysmapid").$(Nest.value(map,"sysmapid").$());
					selementsToAdd.add(newSelement);
				}

				selementsToUpdate = array_merge(selementsToUpdate, Nest.value(selementDiff,"both").asCArray());
				selementsToDelete = array_merge(selementsToDelete, Nest.value(selementDiff,"second").asCArray());
			}

			// links
			if (isset(map,"links")) {
				CArray linkDiff = rda_array_diff(Nest.value(map,"links").asCArray(), Nest.value(dbMap,"links").asCArray(), "linkid");

				// we need sysmapId for add operations
				for(Map newLink : (CArray<Map>)Nest.value(linkDiff,"first").asCArray()) {
					Nest.value(newLink,"sysmapid").$(Nest.value(map,"sysmapid").$());
					linksToAdd.add(newLink);
				}

				linksToUpdate = array_merge(linksToUpdate, Nest.value(linkDiff,"both").asCArray());
				linksToDelete = array_merge(linksToDelete, Nest.value(linkDiff,"second").asCArray());
			}
		}

		update("sysmaps", updateMaps);

		// urls
		insert("sysmap_url", urlsToAdd);
		update("sysmap_url", urlsToUpdate);

		if (!empty(urlIdsToDelete)) {
			delete("sysmap_url", (CArray)map("sysmapurlid", urlIdsToDelete.valuesAsLong()));
		}

		// selements
		CArray<Long[]> newSelementIds = map("selementids", array());
		if (!empty(selementsToAdd)) {
			newSelementIds = createSelements(selementsToAdd);
		}

		if (!empty(selementsToUpdate)) {
			updateSelements(selementsToUpdate);
		}

		if (!empty(selementsToDelete)) {
			deleteSelements(selementsToDelete);
		}

		// links
		if (!empty(linksToAdd) || !empty(linksToUpdate)) {
			CArray selementsNames = array();
			for (Entry<Object, Object> e : ((CArray<Object>)Nest.value(newSelementIds,"selementids").asCArray()).entrySet()) {
			    Object key = e.getKey();
			    Object selementId = e.getValue();
				Nest.value(selementsNames,Nest.value(selementsToAdd,key,"selementid").$()).$(selementId);
			}

			for(Map selement : selementsToUpdate) {
				Nest.value(selementsNames,selement.get("selementid")).$(Nest.value(selement,"selementid").$());
			}

			for (Entry<Object, Map> e : linksToAdd.entrySet()) {
			    Object key = e.getKey();
			    Map link = e.getValue();
				if (isset(selementsNames,link.get("selementid1"))) {
					Nest.value(linksToAdd,key,"selementid1").$(Nest.value(selementsNames,link.get("selementid1")).$());
				}
				if (isset(selementsNames,link.get("selementid2"))) {
					Nest.value(linksToAdd,key,"selementid2").$(Nest.value(selementsNames,link.get("selementid2")).$());
				}
			}
			
			for (Entry<Object, Map> e : linksToUpdate.entrySet()) {
			    Object key = e.getKey();
			    Map link = e.getValue();
				if (isset(selementsNames,link.get("selementid1"))) {
					Nest.value(linksToUpdate,key,"selementid1").$(Nest.value(selementsNames,link.get("selementid1")).$());
				}
				if (isset(selementsNames,link.get("selementid2"))) {
					Nest.value(linksToUpdate,key,"selementid2").$(Nest.value(selementsNames,link.get("selementid2")).$());
				}
			}
		}

		Map newLinkIds = map("linkids", array());
		Map updateLinkIds = map("linkids", array());
		
		if (!empty(linksToAdd)) {
			newLinkIds = createLinks(linksToAdd);
		}

		if (!empty(linksToUpdate)) {
			updateLinkIds = updateLinks(linksToUpdate);
		}

		if (!empty(linksToDelete)) {
			deleteLinks(linksToDelete);
		}

		// link triggers
		CArray linkTriggersToDelete = array();
		CArray linkTriggersToUpdate = array();
		CArray<Map> linkTriggersToAdd = array();

		for (Entry<Object, Object> e : ((CArray<Object>)Nest.value(newLinkIds,"linkids").asCArray()).entrySet()) {
		    Object key = e.getKey();
		    Object linkId = e.getValue();
			if (!isset(Nest.value(linksToAdd,key,"linktriggers").$())) {
				continue;
			}

			for(Map linkTrigger : (CArray<Map>)Nest.value(linksToAdd,key,"linktriggers").asCArray()) {
				Nest.value(linkTrigger,"linkid").$(linkId);
				linkTriggersToAdd.add(linkTrigger);
			}
		}

		CArray<CArray> dbLinks = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> linkTriggerResource = DBselect(getSqlExecutor(),
			"SELECT slt.* FROM sysmaps_link_triggers slt WHERE "+sqlParts.dual.dbConditionInt("slt.linkid", Nest.array(updateLinkIds,"linkids").asLong()),
			sqlParts.getNamedParams()
		);
		for(Map dbLinkTrigger : linkTriggerResource) {
			rda_subarray_push(dbLinks, Nest.value(dbLinkTrigger,"linkid").asInteger(), dbLinkTrigger);
		}

		for (Entry<Object, Object> e : ((CArray<Object>)Nest.value(updateLinkIds,"linkids").asCArray()).entrySet()) {
		    Object key = e.getKey();
		    Object linkId = e.getValue();
			if (!isset(Nest.value(linksToUpdate,key,"linktriggers").$())) {
				continue;
			}

			CArray dbLinkTriggers = isset(dbLinks,linkId) ? dbLinks.get(linkId) : array();
			CArray dbLinkTriggersDiff = rda_array_diff(Nest.value(linksToUpdate,key,"linktriggers").asCArray(), dbLinkTriggers, "linktriggerid");

			for(Map newLinkTrigger : (CArray<Map>)Nest.value(dbLinkTriggersDiff,"first").asCArray()) {
				Nest.value(newLinkTrigger,"linkid").$(linkId);
				linkTriggersToAdd.add(newLinkTrigger);
			}

			linkTriggersToUpdate = array_merge(linkTriggersToUpdate, Nest.value(dbLinkTriggersDiff,"both").asCArray());
			linkTriggersToDelete = array_merge(linkTriggersToDelete, Nest.value(dbLinkTriggersDiff,"second").asCArray());
		}

		if (!empty(linkTriggersToDelete)) {
			deleteLinkTriggers(linkTriggersToDelete);
		}

		if (!empty(linkTriggersToAdd)) {
			createLinkTriggers(linkTriggersToAdd);
		}

		if (!empty(linkTriggersToUpdate)) {
			updateLinkTriggers(linkTriggersToUpdate);
		}

		return map("sysmapids", sysmapIds.valuesAsLong());
	}
	
	/**
	 * Delete Map.
	 *
	 * @param array sysmapIds
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... sysmapIds) {
		CArray maps = rda_toObject(CArray.valueOf(sysmapIds), "sysmapid");
		checkInput(maps, "delete");

		delete("sysmaps_elements", (CArray)map(
			"elementid", sysmapIds,
			"elementtype", SYSMAP_ELEMENT_TYPE_MAP
		));
		delete("screens_items", (CArray)map(
			"resourceid", sysmapIds,
			"resourcetype", SCREEN_RESOURCE_MAP
		));
		delete("profiles", (CArray)map(
			"idx", "web.maps.sysmapid",
			"value_id", sysmapIds
		));
		delete("profiles", (CArray)map(
			"idx", "web.favorite.sysmapids",
			"source", "sysmapid",
			"value_id", sysmapIds
		));
		delete("sysmaps", (CArray)map("sysmapid" , sysmapIds));

		return map("sysmapids", sysmapIds);
	}
	
	private Map expandUrlMacro(Map url, Map selement) {
		String macro = null;
		switch (Nest.value(selement,"elementtype").asInteger()) {
			case SYSMAP_ELEMENT_TYPE_HOST_GROUP:
				macro = "{HOSTGROUP.ID}";
				break;

			case SYSMAP_ELEMENT_TYPE_TRIGGER:
				macro = "{TRIGGER.ID}";
				break;

			case SYSMAP_ELEMENT_TYPE_MAP:
				macro = "{MAP.ID}";
				break;

			case SYSMAP_ELEMENT_TYPE_HOST:
				macro = "{HOST.ID}";
				break;

			default:
				macro = null;
		}

		if (!empty(macro)) {
			Nest.value(url,"url").$(str_replace(macro, Nest.value(selement,"elementid").$(), Nest.value(url,"url").asString()));
		}

		return url;
	}

	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CMapGet options = new CMapGet();
		options.setSysmapIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CMapGet options = new CMapGet();
		options.setSysmapIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void addRelatedObjects(CMapGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);

		CArray sysmapIds = array_keys(result);

		// adding elements
		if (!is_null(params.getSelectSelements()) && !API_OUTPUT_COUNT.equals(params.getSelectSelements())) {
			CMapGet moptions = new CMapGet();
			moptions.setOutput(outputExtend("sysmaps_elements", new String[]{"selementid", "sysmapid"}, params.getSelectSelements()));
			moptions.setFilter("sysmapid", sysmapIds.valuesAsString());
			moptions.setPreserveKeys(true);
			CArray<Map> selements = select("sysmaps_elements", moptions);
			CRelationMap relationMap = createRelationMap(selements, "sysmapid", "selementid");

			// add selement URLs
			if (outputIsRequested("urls", params.getSelectSelements())) {
				for(Map selement : selements) {
					Nest.value(selement,"urls").$(array());
				}

				SqlBuilder sqlParts = new SqlBuilder();
				if (!is_null(params.getExpandUrls())) {
					CArray<Map> dbMapUrls = DBselect(getSqlExecutor(),
						"SELECT su.sysmapurlid,su.sysmapid,su.name,su.url,su.elementtype"+
						" FROM sysmap_url su"+
						" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "sysmap_url", "su", params)+
						" AND "+sqlParts.dual.dbConditionInt("su.sysmapid", sysmapIds.valuesAsLong()),
						sqlParts.getNamedParams()
					);
					for(Map mapUrl : dbMapUrls) {
						for (Entry<Object, Map> e : selements.entrySet()) {
						    Object snum = e.getKey();
						    Map selement = e.getValue();
							if (bccomp(Nest.value(selement,"sysmapid").$(), Nest.value(mapUrl,"sysmapid").$()) == 0
									&& ((Nest.value(selement,"elementtype").asInteger() == Nest.value(mapUrl,"elementtype").asInteger()
											&& Nest.value(selement,"elementsubtype").asInteger() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP
											)
											|| (Nest.value(selement,"elementsubtype").asInteger() == SYSMAP_ELEMENT_SUBTYPE_HOST_GROUP_ELEMENTS
													&& Nest.value(mapUrl,"elementtype").asInteger() == SYSMAP_ELEMENT_TYPE_HOST)
										)) {
								Nest.value(selements,snum,"urls").asCArray().add(expandUrlMacro(mapUrl, selement));
							}
						}
					}
				}

				sqlParts = new SqlBuilder();
				CArray<Map> dbSelementUrls = DBselect(getSqlExecutor(),
					"SELECT seu.sysmapelementurlid,seu.selementid,seu.name,seu.url"+
					" FROM sysmap_element_url seu"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "sysmap_element_url", "seu", params)+
					" AND "+sqlParts.dual.dbConditionInt("seu.selementid", array_keys(selements).valuesAsLong()),
					sqlParts.getNamedParams()
				);
				for(Map selementUrl : dbSelementUrls) {
					Nest.value(selements,selementUrl.get("selementid"),"urls").asCArray().add(is_null(Nest.value(params,"expandUrls").$())
						? selementUrl
						: expandUrlMacro(selementUrl, selements.get(selementUrl.get("selementid"))));
				}
			}

			unsetExtraFields(selements, new String[]{"sysmapid", "selementid"}, params.getSelectSelements());
			relationMap.mapMany(result, selements, "selements");
		}

		// adding icon maps
		if (!is_null(params.getSelectIconMap()) && !API_OUTPUT_COUNT.equals(params.getSelectIconMap())) {
			CIconMapGet imoptions = new CIconMapGet();
			imoptions.setOutput(outputExtend("icon_map", new String[]{"sysmapid", "iconmapid"}, params.getSelectIconMap()));
			imoptions.setSysmapIds(sysmapIds.valuesAsLong());
			imoptions.setPreserveKeys(true);
			imoptions.setNopermissions(true);
			CArray<Map> iconMaps = API.IconMap(this.idBean, this.getSqlExecutor()).get(imoptions);
			CRelationMap relationMap = createRelationMap(iconMaps, "sysmapid", "iconmapid");

			unsetExtraFields(iconMaps, new String[]{"sysmapid", "iconmapid"}, params.getSelectIconMap());
			relationMap.mapOne(result, iconMaps, "iconmap");
		}

		// adding links
		if (!is_null(params.getSelectLinks()) && !API_OUTPUT_COUNT.equals(params.getSelectLinks())) {
			CMapGet moptions = new CMapGet();
			moptions.setOutput(outputExtend("sysmaps_links", new String[]{"sysmapid", "linkid"}, params.getSelectLinks()));
			moptions.setFilter("sysmapid", sysmapIds.valuesAsString());
			moptions.setPreserveKeys(true);
			CArray<Map> links = select("sysmaps_links", moptions);
			CRelationMap relationMap = createRelationMap(links, "sysmapid", "linkid");

			// add link triggers
			if (outputIsRequested("linktriggers", params.getSelectLinks())) {
				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> linkTriggers = dbfetchArrayAssoc(DBselect(
					getSqlExecutor(),
					"SELECT DISTINCT slt.*"+
					" FROM sysmaps_link_triggers slt"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "sysmaps_link_triggers", "slt", params)+
					" AND "+sqlParts.dual.dbConditionInt("slt.linkid", relationMap.getRelatedLongIds()),
					sqlParts.getNamedParams()
				), "linktriggerid");
				CRelationMap linkTriggerRelationMap = createRelationMap(linkTriggers, "linkid", "linktriggerid");
				linkTriggerRelationMap.mapMany(links, linkTriggers, "linktriggers");
			}

			unsetExtraFields(links, new String[]{"sysmapid", "linkid"}, params.getSelectLinks());
			relationMap.mapMany(result, links, "links");
		}

		// adding urls
		if (!is_null(params.getSelectUrls()) && !API_OUTPUT_COUNT.equals(params.getSelectUrls())) {
			CMapGet moptions = new CMapGet();
			moptions.setOutput(outputExtend("sysmap_url", new String[]{"sysmapid", "sysmapurlid"}, params.getSelectUrls()));
			moptions.setFilter("sysmapid", sysmapIds.valuesAsString());
			moptions.setPreserveKeys(true);
			CArray<Map> links = select("sysmap_url", moptions);
			CRelationMap relationMap = createRelationMap(links, "sysmapid", "sysmapurlid");

			unsetExtraFields(links, new String[]{"sysmapid", "sysmapurlid"}, params.getSelectUrls());
			relationMap.mapMany(result, links, "urls");
		}
	}
}
