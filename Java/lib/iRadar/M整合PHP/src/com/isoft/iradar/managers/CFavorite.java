package com.isoft.iradar.managers;

import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.DBUtil.get_dbid;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;


/**
 * Container class for favorite value management.
 * Uses caching.
 */
public class CFavorite {

	/**
	 * Cache for favorite values.
	 *
	 * cache[idx][]["value"]
	 * cache[idx][]["source"]
	 */
	private static CArray<CArray> cache = array();

	/**
	 * Returns favorite values from db. Uses caching for performance.
	 *
	 * @param string idx identifier of favorite value group
	 *
	 * @return array list of favorite values corresponding to idx
	 */
	public static CArray get(IIdentityBean idBean, SQLExecutor executor, String idx) {
		// return values if cached
		if (isset(cache.get(idx))) {
			return (CArray)cache.get(idx);
		}

		CArray result = array();
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("p.value_id,p.source");
		sqlParts.from.put("profiles p");
		sqlParts.where.dbConditionTenants(idBean, "profiles", "p");
		sqlParts.where.put("p.userid="+sqlParts.marshalParam(CWebUser.data().get("userid")));
		sqlParts.where.put("p.idx="+sqlParts.marshalParam(idx));
		sqlParts.order.put("p.profileid");
		
		CArray<Map> db_profiles = DBselect(executor, sqlParts);
		for (Map profile: db_profiles) {
			result.add(map("value", Nest.value(profile,"value_id").$(), "source", Nest.value(profile,"source").$()));
		}

		// store db values in cache
		cache.put(idx, result);

		return result;
	}
	
	public static boolean add(IIdentityBean idBean, SQLExecutor executor, String idx, long favid) {
		return add(idBean, executor, idx, favid, null);
	}

	/**
	 * Adds favorite value to DB.
	 *
	 * @param string idx    identifier of favorite value group
	 * @param int    favid  value id
	 * @param string favobj source object
	 *
	 * @return bool did SQL INSERT succeeded
	 */
	public static boolean add(IIdentityBean idBean, SQLExecutor executor, String idx, long favid, String favobj) {
		if (exists(idBean, executor, idx, favid, favobj)) {
			return true;
		}

		// add to cache only if cache is created
		if (isset(cache.get(idx))) {
			cache.get(idx).add( map(
				"value", favid,
				"source", favobj
			));
		}

		DBstart(executor);
		CArray _values = map(
			"tenantid", idBean.getTenantId(),
			"profileid", get_dbid(idBean, executor, "profiles", "profileid"),
			"userid", CWebUser.data().get("userid"),
			"idx", idx,
			"value_id", favid,
			"type", PROFILE_TYPE_ID
		);
		if (!is_null(favobj)) {
			Nest.value(_values,"source").$(favobj);
		}
		
		String sql = "INSERT INTO profiles ("+implode(", ", array_keys(_values))+") VALUES (#{"+implode("}, #{", array_keys(_values))+"})";
		int result = executor.executeInsertDeleteUpdate(sql, _values);
		return DBend(executor, result > 0);
	}
	
	public static boolean remove(IIdentityBean idBean, SQLExecutor executor, String idx) {
		return remove(idBean, executor, idx, 0L);
	}

	public static boolean remove(IIdentityBean idBean, SQLExecutor executor, String idx, long favid) {
		return remove(idBean, executor, idx, favid, null);
	}

	/**
	 * Removes favorite from DB. Clears cache by _idx.
	 *
	 * @param string _idx    identifier of favorite value group
	 * @param int    _favid  value id
	 * @param string _favobj source object
	 *
	 * @return boolean did SQL DELETE succeeded
	 */
	public static boolean remove(IIdentityBean idBean, SQLExecutor executor, String idx, long favid, String favobj) {
		// empty cache, we know that all _idx values will be removed in DELETE
		if (favid == 0 && favobj == null) {
			cache.put(idx, array());
		}
		// remove from cache, cache will be rebuilt upon get()
		else {
			cache.put(idx, null);
		}
		
		Map param = EasyMap.build("idx", idx, "favid", favid, "favobj", favobj);
		param.put("tenantid", idBean.getTenantId());
		param.put("userid", CWebUser.data().get("userid"));
		
		String sql = "DELETE FROM profiles"+
			" WHERE tenantid=#{tenantid}"+
				"AND userid=#{userid}"+
				" AND idx=#{idx}"+
				(favid > 0 ? " AND value_id=#{favid}": "")+
				(is_null(favobj) ? "" : " AND source=#{favobj}");

		return executor.executeInsertDeleteUpdate(sql, param) > 0;
	}
	
	public static boolean exists(IIdentityBean idBean, SQLExecutor executor, String idx, long favid) {
		return exists(idBean, executor, idx, favid, null);
	}
	
	/**
	 * Checks whether favorite value exists.
	 *
	 * @param string _idx    identifier of favorite value group
	 * @param int    _favid  value id
	 * @param string _favobj source object
	 *
	 * @return boolean
	 */
	public static boolean exists(IIdentityBean idBean, SQLExecutor executor, String idx, long favid, String favobj) {
		CArray<Map> favorites = get(idBean, executor, idx);
		for(Map favorite: favorites) {
			if (idcmp(favid, Nest.value(favorite,"value").asLong()) && Nest.value(favorite,"source").asString().equals(favobj)) {
				return true;
			}
		}
		return false;
	}
	
}
