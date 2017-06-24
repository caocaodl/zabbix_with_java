package com.isoft.iradar.managers;

import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import static com.isoft.types.CArray.valueOf;

import java.util.List;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class to perform low level history related actions.
 * @author benne
 *
 */
@CodeConfirmed("benne.2.2.5")
public class CHistoryManager {
	
	private IIdentityBean idBean;
	private SQLExecutor executor;
	
	private CHistoryManager(IIdentityBean idBean, SQLExecutor executor){
		this.idBean = idBean;
		this.executor = executor;
	}

	/**
	 * Returns the last _limit history objects for the given items.
	 *
	 * @param array _items      an array of items with the 'itemid' and 'value_type' properties
	 * @param int   _limit
	 * @param int   _period     the maximum period to retrieve data for
	 *
	 * @return array    an array with items IDs as keys and arrays of history objects as values
	 */
	@CodeConfirmed("benne.2.2.5")
	public CArray<CArray<Map>> getLast(CArray<Map> items) {
		return getLast( items, 1);
	}
	
	@CodeConfirmed("benne.2.2.5")
	public CArray<CArray<Map>> getLast(CArray<Map> items, int limit) {
		return getLast(items, limit, null);
	}
	
	@CodeConfirmed("benne.2.2.5")
	public CArray<CArray<Map>> getLast(CArray<Map> items, int limit, Integer period) {
		CArray<CArray<Map>> rs = array();
		Map params = null;
		SqlBuilder sqlParts = null;
		for (Map item : items) {
			String table = getTableName(Nest.value(item,"value_type").asInteger());
			sqlParts = new SqlBuilder();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM ").append(table).append(" h WHERE h.itemid=#{itemId}");
			sql.append(" AND "+sqlParts.dual.dbConditionTenants(idBean, table, "h"));
			if (period != null && period > 0) {
				sql.append(" AND h.clock>"+(time() - period));
			}
			sql.append(" ORDER BY h.clock DESC LIMIT ");
			sql.append(limit);
			params = sqlParts.getNamedParams();
			params.put("itemId", item.get("itemid"));
			List<Map> values = executor.executeNameParaQuery(sql.toString(), params);
			if (values != null && !values.isEmpty()) {
				Nest.value(rs, item.get("itemid")).$(valueOf(values));
			}
		}
		return rs;
	}
	
	/**
	 * Return the name of the table where the data for the given value type is stored.
	 *
	 * @param int _valueType
	 *
	 * @return string
	 */
	@CodeConfirmed("benne.2.2.5")
	public static String getTableName(int valueType) {
		return TABLES.get(valueType);
	}
	
	@CodeConfirmed("benne.2.2.5")
	private static CArray<String> TABLES = map(
			ITEM_VALUE_TYPE_LOG, "history_log",
			ITEM_VALUE_TYPE_TEXT, "history_text",
			ITEM_VALUE_TYPE_STR, "history_str",
			ITEM_VALUE_TYPE_FLOAT, "history",
			ITEM_VALUE_TYPE_UINT64, "history_uint"
		);
	
}
