package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.managers.CHistoryManager;
import com.isoft.iradar.model.params.CHistoryGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;

public class CHistoryDAO extends CCoreLongKeyDAO<CHistoryGet> {

	public CHistoryDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "history", "h", new String[]{"itemid", "clock"});
	}

	/**
	 * Get history data.
	 *
	 * @param array _options
	 * @param array _options['itemids']
	 * @param boolean _options['editable']
	 * @param string _options['pattern']
	 * @param int _options['limit']
	 * @param string _options['order']
	 *
	 * @return array|int item data as array or false if error
	 */
	@Override
	public <T> T get(CHistoryGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("history", "h.itemid");
		
		if (empty(this.tableName = CHistoryManager.getTableName(params.getHistory()))) {
			this.tableName = "history";
		}
		sqlParts.from.put("history", this.tableName+" h");
		
		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.where.dbConditionInt("itemid", "h.itemid", params.getItemIds());
		}

		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("hostid","i.hostid");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("i", "i.hostid", params.getHostIds());
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.itemid=i.itemid");
		}

		// time_from
		if (!is_null(params.getTimeFrom())) {
			sqlParts.select.put("clock","h.clock");
			sqlParts.where.put("clock_from", "h.clock>="+sqlParts.marshalParam(params.getTimeFrom()));
		}

		// time_till
		if (!is_null(params.getTimeTill())) {
			sqlParts.select.put("clock","h.clock");
			sqlParts.where.put("clock_till", "h.clock<="+sqlParts.marshalParam(params.getTimeTill()));
		}
		
		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("history h", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("history h", params, sqlParts);
		}

		// output
		if (API_OUTPUT_EXTEND.equals(params.getOutput())) {
			sqlParts.select.clear("clock");
			sqlParts.select.put("history","h.*");
		}

		// countOutput
		if (!is_null(params.getCountOutput())) {
			params.put("sortfield", "");
			sqlParts.select.clear();
			sqlParts.select.put("count(DISTINCT h.hostid) as rowscount");

			// groupCount
			if (!is_null(params.getGroupCount())) {
				for(Entry<String, String> entry: sqlParts.group.namedMap.entrySet()) {
					sqlParts.select.put(entry.getKey(), entry.getValue());
				}
			}
		}

		// groupOutput
		boolean groupOutput = false;
		if (!is_null(params.getGroupOutput())) {
			if(sqlParts.select.namedList.contains("h."+params.getGroupOutput()) || sqlParts.select.namedList.contains("h.*")) {
				groupOutput = true;
			}
		}

		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
//		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		Map<Object, List<Map>> group = new IMap<Object, List<Map>>();
		Map itemids = new IMap();
		long count = 0;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
//				ret = row.get("rowscount");
				ret = row;
			} else {
				Long itemid = (Long)row.get("itemid");
				itemids.put(itemid, itemid);
				
				Long id = count++;
				Nest.value(result, id).$(array());

				// hostids
				if (isset(row.get("hostid"))) {
					if (!isset(result.get(id).get("hosts"))) {
						result.get(id).put("hosts", new CArray());
					}
					((CArray)result.get(id).get("hosts")).add(map("hostid", row.remove("hostid")));
				}
				
				// triggerids
				if (isset(row.get("triggerid"))) {
					if (!isset(result.get(id).get("triggers"))) {
						result.get(id).put("triggers", new CArray());
					}
					((CArray)result.get(id).get("triggers")).add(map("triggerid", row.remove("triggerid")));
				}
				result.get(id).putAll(row);

				// grouping
				if (groupOutput) {
					Object dataId = row.get(params.getGroupCount());
					if (!isset(group.get(dataId))) {
						group.put(dataId, new ArrayList<Map>());
					}
					group.get(dataId).add(result.get(id));
				}
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}

		// removing keys (hash -> array)
		if (!params.isPreserveKeys()) {
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}
	

	@Override
	protected void applyQuerySortOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		CHistoryGet hparams = (CHistoryGet)params;
		
		boolean isIdFieldUsed = false;

		if (hparams.getHistory()==ITEM_VALUE_TYPE_LOG || hparams.getHistory()==ITEM_VALUE_TYPE_TEXT) {
			sqlParts.order.put("id", "id");
			isIdFieldUsed = true;
		}

		super.applyQuerySortOptions(tableName, tableAlias, params, sqlParts);

		if (isIdFieldUsed) {
			sqlParts.order.namedMap.remove("id");
		}
	}
}
