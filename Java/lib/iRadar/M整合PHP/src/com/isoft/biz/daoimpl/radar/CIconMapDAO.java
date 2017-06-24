package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CIconMapGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CIconMapDAO extends CCoreLongKeyDAO<CIconMapGet> {

	public CIconMapDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "icon_map", "im", new String[]{"iconmapid", "name"});
	}
	
	/**
	 * Get IconMap data.
	 * @param array _options
	 * @param array _options["iconmapids"]
	 * @param array _options["sysmapids"]
	 * @param array _options["editable"]
	 * @param array _options["count"]
	 * @param array _options["limit"]
	 * @param array _options["order"]
	 * @return array
	 */
	@Override
	public <T> T get(CIconMapGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("icon_map", "im.iconmapid");
		sqlParts.from.put("icon_map", "icon_map im");
		
		// iconmapids
		if (!is_null(params.getIconmapIds())) {
			sqlParts.where.dbConditionInt("im.iconmapid",params.getIconmapIds());
		}

		// sysmapids
		if (!is_null(params.getSysmapIds())) {
			sqlParts.select.put("sysmapids","s.sysmapid");
			sqlParts.from.put("sysmaps","sysmaps s");
			sqlParts.where.dbConditionInt("s.sysmapid",params.getSysmapIds());
			sqlParts.where.put("ims.tenantid","im.tenantid=s.tenantid");
			sqlParts.where.put("ims","im.iconmapid=s.iconmapid");
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("icon_map im", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("icon_map im", params, sqlParts);
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
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
				Long id = (Long)row.get("iconmapid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				if (isset(row.get("sysmapid"))) {
					if (!isset(result.get(id).get("sysmaps"))) {
						result.get(id).put("sysmaps", new CArray());
					}
					((CArray)result.get(id).get("sysmaps")).add(map("sysmapid", row.get("sysmapid")));
				}
				result.get(id).putAll(row);
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
	 * Add IconMap.
	 * @param array _iconMaps
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> iconMaps) {
		if (USER_TYPE_SUPER_ADMIN != Nest.value(userData(),"type").asInteger()) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can create icon maps."));
		}

		CArray iconMapRequiredFields = map("name", null);

		CArray<String> duplicates = array();
		for(Map iconMap : iconMaps) {
			if (!check_db_fields(iconMapRequiredFields, iconMap)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect parameter is used for icon map \"%s\".", Nest.value(iconMap,"name").$()));
			}
			if (rda_empty(Nest.value(iconMap,"name").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Icon map name cannot be empty."));
			}
			if (isset(duplicates,iconMap.get("name"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot create icon maps with identical name \"%s\".", Nest.value(iconMap,"name").$()));
			} else {
				Nest.value(duplicates,iconMap.get("name")).$(Nest.value(iconMap,"name").$());
			}
		}

		validateMappings(iconMaps);

		CIconMapGet options = new CIconMapGet();
		options.setFilter("name", duplicates.valuesAsString());
		options.setOutput(new String[]{"name"});
		options.setEditable(true);
		options.setNopermissions(true);
		CArray<Map> dbIconMaps = get(options);
		for(Map dbIconMap : dbIconMaps) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Icon map \"%s\" already exists.", Nest.value(dbIconMap,"name").$()));
		}

		CArray<Long> iconMapids = insert("icon_map", iconMaps);

		CArray<Map> mappings = array();
		for (Entry<Object, Map> e : iconMaps.entrySet()) {
            Object imnum = e.getKey();
            Map iconMap = e.getValue();
			for(Map mapping : (CArray<Map>)Nest.value(iconMap,"mappings").asCArray()) {
				Nest.value(mapping,"iconmapid").$(iconMapids.get(imnum));
				mappings.add(mapping);
			}
		}
		insert("icon_mapping", mappings);

		return map("iconmapids", iconMapids.valuesAsLong());
	}

	/**
	 * Update IconMap.
	 * @param array _iconMaps
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> iconMaps) {
		if (USER_TYPE_SUPER_ADMIN != Nest.value(userData(),"type").asInteger()) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can update icon maps."));
		}

		CArray iconMapids = rda_objectValues(iconMaps, "iconmapid");
		CArray<Map> updates = array();

		CArray duplicates = array();
		for(Map iconMap : iconMaps) {
			if (!check_db_fields(map("iconmapid", null), iconMap)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect parameters for icon map update method \"%s\".", Nest.value(iconMap,"name").$()));
			}

			if (isset(iconMap,"name")) {
				if (rda_empty(Nest.value(iconMap,"name").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Icon map name cannot be empty."));
				} else if (isset(duplicates,iconMap.get("name"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot create icon maps with identical name \"%s\".", Nest.value(iconMap,"name").$()));
				} else {
					Nest.value(duplicates,iconMap.get("name")).$(Nest.value(iconMap,"name").$());
				}
			}
		}

		validateMappings(iconMaps, false);

		CIconMapGet options = new CIconMapGet();
		options.setIconmapIds(iconMapids.valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		options.setSelectMappings(API_OUTPUT_EXTEND);
		CArray<Map> iconMapsUpd = API.IconMap(this.idBean, this.getSqlExecutor()).get(options);

		CArray<Map> oldIconMappings = array();
		CArray<Map> newIconMappings = array();
		for(Map iconMap : iconMaps) {
			if (!isset(iconMapsUpd,iconMap.get("iconmapid"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Icon map with iconmapid \"%s\" does not exist.", Nest.value(iconMap,"iconmapid").$()));
			}

			// Existence
			if (isset(iconMap,"name")) {
				options = new CIconMapGet();
				options.setFilter("name", Nest.value(iconMap,"name").asString());
				options.setOutput(new String[]{"iconmapid"});
				options.setEditable(true);
				options.setNopermissions(true);
				options.setPreserveKeys(true);
				CArray<Map> iconMapsExists = get(options);
				Map iconMapExists = reset(iconMapsExists);
				if ((!empty(iconMapExists)) && (bccomp(Nest.value(iconMapExists,"iconmapid").$(), Nest.value(iconMap,"iconmapid").$()) != 0)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Icon map \"%s\" already exists.", Nest.value(iconMap,"name").$()));
				}
			}

			if (isset(iconMap,"mappings")) {
				CArray<Map> mappingsDb = Nest.value(iconMapsUpd,iconMap.get("iconmapid"),"mappings").asCArray();
				for(Map mapping : mappingsDb) {
					oldIconMappings.add(mapping);
				}
				for(Map mapping : (CArray<Map>)Nest.value(iconMap,"mappings").asCArray()) {
					Nest.value(mapping,"iconmapid").$(Nest.value(iconMap,"iconmapid").$());
					newIconMappings.add(mapping);
				}
			}

			Long iconMapid = Nest.value(iconMap,"iconmapid").asLong();
			unset(iconMap,"iconmapid");
			if (!empty(iconMap)) {
				updates.add(map(
					"values", iconMap,
					"where", map("iconmapid", iconMapid)
				));
			}
		}

		save("icon_map", iconMaps);
		replace("icon_mapping", oldIconMappings, newIconMappings);

		return map("iconmapids", iconMapids.valuesAsLong());
	}

	/**
	 * Delete IconMap.
	 * @param array _iconmapids
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... iconmapids) {
		if (empty(iconmapids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}
		if (!isWritable(iconmapids)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT m.name AS mapname, im.name as iconmapname"+
			" FROM sysmaps m, icon_map im"+
			" WHERE m.tenantid=im.tenantid"+
			" AND m.iconmapid=im.iconmapid"+
			" AND "+sqlParts.dual.dbConditionInt("m.iconmapid", iconmapids);
		CArray<Map> res = DBselect(getSqlExecutor(),sql, sqlParts.getNamedParams());
		Map names = DBfetch(res);
		if (!empty(names)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Icon map \"%1$s\" cannot be deleted. Used in map \"%2$s\".", Nest.value(names,"iconmapname").$(), Nest.value(names,"mapname").$())
			);
		}

		delete("icon_map", (Map)map("iconmapid", iconmapids));

		return map("iconmapids", iconmapids);
	}

	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CIconMapGet options = new CIconMapGet();
		options.setIconmapIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CIconMapGet options = new CIconMapGet();
		options.setIconmapIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	/**
	 * Checks icon maps.
	 * @throws APIException
	 * @param iconMaps
	 * @param bool _mustExist if icon map should be checked against having at least one mapping
	 * @return void
	 */
	protected void validateMappings(CArray<Map> iconMaps) {
		validateMappings(iconMaps, true);
	}
	
	//TODO
	protected void validateMappings(CArray<Map> _iconMaps, boolean _mustExist) {
//		CArray<Map> _inventoryFields = getHostInventories();
//		_imageids = API.Image().get(array(
//			"output" => array("imageid"),
//			"preservekeys" => true,
//			"filter" => array("imagetype" => IMAGE_TYPE_ICON)
//		));
//
//		for(_iconMaps as _iconMap) {
//			if (isset(Nest.value(_iconMap,"mappings").$()) && empty(Nest.value(_iconMap,"mappings").$())) {
//				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//					_s("Icon map \"%s\" must have at least one mapping.", Nest.value(_iconMap,"name").$()));
//			}
//			elseif (!isset(Nest.value(_iconMap,"mappings").$())) {
//				if (_mustExist) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//						_s("Icon map \"%s\" must have at least one mapping.", Nest.value(_iconMap,"name").$()));
//				}
//				else {
//					continue;
//				}
//			}
//
//			_uniqField = array();
//			for(Nest.value(_iconMap,"mappings").$() as _mapping) {
//				if (!isset(Nest.value(_mapping,"expression").$())) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Required field \"expression\" is missing in icon mapping."));
//				}
//				elseif (!isset(Nest.value(_mapping,"inventory_link").$())) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Required field \"inventory_link\" is missing in icon mapping."));
//				}
//				elseif (!isset(Nest.value(_mapping,"iconid").$())) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Required field \"iconid\" is missing in icon mapping."));
//				}
//				elseif (!isset(_inventoryFields[_mapping["inventory_link"]])) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//						_s("Icon map \"%1$s\" has mapping with incorrect inventory link \"%2$s\".", Nest.value(_iconMap,"name").$(), Nest.value(_mapping,"inventory_link").$()));
//				}
//				elseif (!isset(_imageids[_mapping["iconid"]])) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//						_s("Icon map \"%1$s\" has mapping with incorrect iconid \"%2$s\".", Nest.value(_iconMap,"name").$(), Nest.value(_mapping,"iconid").$()));
//				}
//
//				try {
//					GlobalRegExp::isValid(Nest.value(_mapping,"expression").$());
//				}
//				catch (Exception $e) {
//					switch ($e.getCode()) {
//						case GlobalRegExp::ERROR_REGEXP_EMPTY:
//							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//								_s("Icon map \"%s\" cannot have mapping with empty expression.", Nest.value(_iconMap,"name").$()));
//							break;
//						case GlobalRegExp::ERROR_REGEXP_NOT_EXISTS:
//							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//								_s("Icon map \"%s\" cannot have mapping with global expression that does not exist.", Nest.value(_iconMap,"name").$()));
//							break;
//						default:
//							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//								_s("Icon map \"%s\" has incorrect expression.", Nest.value(_iconMap,"name").$()));
//					}
//				}
//
//				if (isset(_uniqField[_mapping["inventory_link"]._mapping["expression"]])) {
//					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
//						_s("Icon mapping entry \"%1$s\" against \"%2$s\" already exists.",
//							Nest.value(_mapping,"expression").$(),
//							_inventoryFields[_mapping["inventory_link"]]["title"])
//					);
//				}
//				_uniqField[_mapping["inventory_link"]._mapping["expression"]] = true;
//			}
//		}
	}

	@Override
	protected void addRelatedObjects(CIconMapGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);

		CArray _iconMapIds = array_keys(result);

		if (params.getSelectMappings() != null && !API_OUTPUT_COUNT.equals(params.getSelectMappings())) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("icon_mapping", new String[]{"iconmapid", "iconmappingid"}, params.getSelectMappings()));
			options.setFilter("iconmapid", _iconMapIds.valuesAsString());
			options.setPreserveKeys(true);
			CArray<Map> mappings = select("icon_mapping", options);
			CRelationMap relationMap = createRelationMap(mappings, "iconmapid", "iconmappingid");

			unsetExtraFields(mappings, new String[]{"iconmapid", "iconmappingid"}, params.getSelectMappings());
			relationMap.mapMany(result, mappings, "mappings");
		}
	}
	
	
}
