package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_count_values;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.g;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class ValuemapUtil {
	
	private ValuemapUtil() {
	}

	/**
	 * Add value map with mappings.
	 *
	 * @param array valueMap
	 * @param array mappings
	 *
	 * @throws Exception
	 */
	public static void addValueMap(IIdentityBean idBean, SQLExecutor executor, Map valueMap, CArray<Map> mappings) throws Exception {
		mappings = cleanValueMapMappings(mappings);
		checkValueMapMappings(mappings);

		// check duplicate name
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT v.valuemapid"+
				" FROM valuemaps v"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "valuemaps", "v")+
				" AND v.name="+sqlParts.marshalParam(Nest.value(valueMap,"name").$());
		if (!empty(DBfetch(DBselect(executor, sql, sqlParts.getNamedParams())))) {
			throw new Exception(_s("Value map \"%1$s\" already exists.", Nest.value(valueMap,"name").$()));
		}

		CArray<Long> valueMapIds = CDB.insert(idBean, executor, "valuemaps", array(valueMap));
		long valueMapId = reset(valueMapIds);

		addValueMapMappings(idBean, executor, valueMapId, mappings);
	}

	/**
	 * Update value map and rewrite mappings.
	 *
	 * @param array valueMap
	 * @param array mappings
	 *
	 * @throws Exception
	 */
	public static void updateValueMap(IIdentityBean idBean, SQLExecutor executor, Map valueMap, CArray<Map> mappings) throws Exception {
		mappings = cleanValueMapMappings(mappings);
		checkValueMapMappings(mappings);

		long valueMapId = Nest.value(valueMap,"valuemapid").asLong();
		unset(valueMap,"valuemapid");

		// check existence
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT v.valuemapid FROM valuemaps v"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "valuemaps", "v")+
				" AND v.valuemapid="+sqlParts.marshalParam(valueMapId);
		if (empty(DBfetch(DBselect(executor, sql, sqlParts.getNamedParams())))) {
			throw new Exception(_s("Value map with valuemapid \"%1$s\" does not exist.", valueMapId));
		}

		// check duplicate name
		Map dbValueMap = DBfetch(DBselect(executor, 
			"SELECT v.valuemapid"+
			" FROM valuemaps v"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "valuemaps", "v")+
			" AND v.name="+sqlParts.marshalParam(Nest.value(valueMap,"name").$()),
			sqlParts.getNamedParams()
		));
		if (!empty(dbValueMap) && bccomp(valueMapId, Nest.value(dbValueMap,"valuemapid").$()) != 0) {
			throw new Exception(_s("Value map \"%1$s\" already exists.", Nest.value(valueMap,"name").$()));
		}

		rewriteValueMapMappings(idBean, executor, valueMapId, mappings);

		CDB.update(idBean, executor, "valuemaps", array((Map)map(
			"values", valueMap,
			"where", map("valuemapid", valueMapId)
		)));
	}

	public static void deleteValueMap(IIdentityBean idBean, SQLExecutor executor, long valueMapId) {
		CDB.update(idBean, executor, "items", array((Map)map(
			"values", map("valuemapid", 0),
			"where", map("valuemapid", valueMapId)
		)));
		CDB.delete(idBean, executor, "valuemaps", (Map)map("valuemapid", valueMapId));
	}

	/**
	 * Clean value map mappings array from empty records when both value and newvalue are empty strings.
	 *
	 * @param array mappings
	 *
	 * @return array
	 */
	public static CArray<Map> cleanValueMapMappings(CArray<Map> mappings) {
		CArray<Map> cleanedMappings = Clone.deepcopy(mappings);
		for (Entry<Object, Map> e : cleanedMappings.entrySet()) {
		    Object key = e.getKey();
		    Map mapping = e.getValue();
			if (rda_empty(Nest.value(mapping,"value").$()) && rda_empty(Nest.value(mapping,"newvalue").$())) {
				unset(cleanedMappings,key);
			}
		}
		return cleanedMappings;
	}

	/**
	 * Check value map mappings.
	 * 1. check if at least one is defined
	 * 2. check if value is numeric
	 * 3. check if mappend value is not empty string
	 * 4. check for duplicate values
	 *
	 * @param array mappings
	 *
	 * @throws Exception
	 */
	public static void checkValueMapMappings(CArray<Map> mappings) throws Exception {
		if (empty(mappings)) {
			throw new Exception(_("Value mapping must have at least one mapping."));
		}

		for(Map mapping : mappings) {
			if (rda_empty(Nest.value(mapping,"newvalue").$())) {
				throw new Exception(_("Value cannot be mapped to empty string."));
			}
		}

		CArray<Integer> valueCount = array_count_values(rda_objectValues(mappings, "value"));
		for (Entry<Object, Integer> e : valueCount.entrySet()) {
		    Object value = e.getKey();
		    Integer count = e.getValue();
			if (count > 1) {
				throw new Exception(_s("Mapping value \"%1$s\" is not unique.", value));
			}
		}
	}

	/**
	 * Rewrite value map mappings.
	 *
	 * @param int   valueMapId
	 * @param array mappings
	 */
	public static void rewriteValueMapMappings(IIdentityBean idBean, SQLExecutor executor, long valueMapId, CArray<Map> mappings) {
		CArray<Map> dbValueMaps = getValueMapMappings(idBean, executor, valueMapId);

		CArray<Map> mappingsToAdd = array();
		CArray<Map> mappingsToUpdate = array();
		for(Map mapping : mappings) {
			if (!isset(mapping,"mappingid")) {
				mappingsToAdd.add(mapping);
			} else if (isset(dbValueMaps, mapping.get("mappingid"))) {
				mappingsToUpdate.add(mapping);
				unset(dbValueMaps, mapping.get("mappingid"));
			}
		}

		if (!empty(dbValueMaps)) {
			CArray dbMappingIds = rda_objectValues(dbValueMaps, "mappingid");
			deleteValueMapMappings(idBean, executor, dbMappingIds.valuesAsLong());
		}

		if (!empty(mappingsToAdd)) {
			addValueMapMappings(idBean, executor, valueMapId, mappingsToAdd);
		}

		if (!empty(mappingsToUpdate)) {
			updateValueMapMappings(idBean, executor, mappingsToUpdate);
		}
	}

	/**
	 * Add new mappings to value map.
	 *
	 * @param int   valueMapId
	 * @param array mappings
	 */
	public static void addValueMapMappings(IIdentityBean idBean, SQLExecutor executor, long valueMapId, CArray<Map> mappings) {
		for(Map mapping : mappings) {
			Nest.value(mapping,"valuemapid").$(valueMapId);
		}
		CDB.insert(idBean, executor, "mappings", mappings);
	}

	/**
	 * Update value map mappings.
	 *
	 * @param array mappings
	 */
	public static void updateValueMapMappings(IIdentityBean idBean, SQLExecutor executor, CArray<Map> mappings) {
		for(Map mapping : mappings) {
			Object mappingid = Nest.value(mapping,"mappingid").$();
			unset(mapping,"mappingid");

			CDB.update(idBean, executor, "mappings", array((Map)map(
				"values", mapping,
				"where", map("mappingid", mappingid)
			)));
		}
	}

	/**
	 * Delete value map mappings.
	 *
	 * @param array mappingIds
	 */
	public static void deleteValueMapMappings(IIdentityBean idBean, SQLExecutor executor, Long[] mappingIds) {
		CDB.delete(idBean, executor, "mappings", (Map)map("mappingid", mappingIds));
	}

	/**
	 * Get all value map mappings.
	 *
	 * @param int valueMapId
	 *
	 * @return array
	 */
	public static CArray<Map> getValueMapMappings(IIdentityBean idBean, SQLExecutor executor, long valueMapId) {
		CArray<Map> mappings = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbMappings = DBselect(executor,
			"SELECT m.mappingid,m.value,m.newvalue"+
			" FROM mappings m"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "mappings", "m")+
			" AND m.valuemapid="+sqlParts.marshalParam(valueMapId),
			sqlParts.getNamedParams()
		);
		for (Map mapping : dbMappings) {
			mappings.put(mapping.get("mappingid"),mapping);
		}
		return mappings;
	}

	/**
	 * Get mapping for value.
	 * If there is no mapping return false.
	 *
	 * @param string value			value that mapping should be applied to
	 * @param int    valueMapId	value map id which should be used
	 *
	 * @return string|bool
	 */
	public static String getMappedValue(IIdentityBean idBean, SQLExecutor executor, String value, long valueMapId) {
		CArray valueMaps = g.valueMaps.$();
		if (valueMapId < 1) {
			return null;
		}
		if (isset(Nest.value(valueMaps,valueMapId,value).$())) {
			return Nest.value(valueMaps,valueMapId,value).asString();
		}
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbMappings = DBselect(executor,
			"SELECT m.newvalue"+
			" FROM mappings m"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "mappings", "m")+
			    " AND m.valuemapid="+sqlParts.marshalParam(valueMapId)+
				" AND m.value="+sqlParts.marshalParam(value),
			sqlParts.getNamedParams()
		);
		Map mapping = DBfetch(dbMappings);
		if (!empty(mapping)) {
			String newvalue = Nest.value(mapping,"newvalue").asString();
			Nest.value(valueMaps,valueMapId,value).$(newvalue);
			return newvalue;
		}
		return null;
	}

	/**
	 * Apply value mapping to value.
	 * If value map or mapping is not found unchanged value returned,
	 * otherwise mapped value returned in format: \"<mapped_value> (<initial_value>)\".
	 *
	 * @param string value			value that mapping should be applied to
	 * @param int    valueMapId	value map id which should be used
	 *
	 * @return string
	 */
	public static String applyValueMap(IIdentityBean idBean, SQLExecutor executor, String value, long valueMapId) {
		String mapping = getMappedValue(idBean, executor, value, valueMapId);
		return (empty(mapping)) ? value : mapping+" ("+value+")";
	}
	
}
