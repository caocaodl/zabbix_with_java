package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CGraphItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;

public class CGraphItemDAO extends CGraphGeneralDAO<CGraphItemGet> {

	public CGraphItemDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "graphs_items", "gi", new String[] { "gitemid" });
	}
	
	@Override
	public <T> T get(CGraphItemGet params) {
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("gitems", "gi.gitemid");
		sqlParts.from.put("graphs_items", "graphs_items gi");
		
		// graphids
		if (!is_null(params.getGraphIds())) {
			sqlParts.select.put("graphid","gi.graphid");
			sqlParts.from.put("graphs","graphs g");
			sqlParts.where.put("gig.tenantid","gi.tenantid=g.tenantid");
			sqlParts.where.put("gig","gi.graphid=g.graphid");
			sqlParts.where.dbConditionInt("g.graphid",params.getGraphIds());
		}

		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","gi.itemid");
			sqlParts.where.dbConditionInt("gi.itemid",params.getItemIds());
		}

		// type
		if (!is_null(params.getType())) {
			sqlParts.where.put("gi.type="+sqlParts.marshalParam(params.getType()));
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
				Long id = (Long)row.get("gitemid");
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				// graphids
				if (isset(row.get("graphid")) && is_null(params.getSelectGraphs())) {
					if (!isset(result.get(id).get("graphs"))) {
						result.get(id).put("graphs", new CArray());
					}
					((CArray)result.get(id).get("graphs")).add(map("graphid", row.remove("graphid")));
				}
				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			this.unsetExtraFields(result, new String[]{"graphid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		CGraphItemGet giparams = (CGraphItemGet)params;
		if(!is_null(giparams.getExpandData())){
			sqlParts.select.put("i.key_");
			sqlParts.select.put("i.hostid");
			sqlParts.select.put("i.flags");
			sqlParts.select.put("h.host");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("gii.tenantid","gi.tenantid=i.tenantid");
			sqlParts.where.put("gii","gi.itemid=i.itemid");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
		}
		
		if(!is_null(giparams.getSelectGraphs())){
			addQuerySelect("graphid", sqlParts);
		}
	}

	@Override
	protected void addRelatedObjects(CGraphItemGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		if(!is_null(params.getSelectGraphs())){
			CRelationMap relationMap = createRelationMap(result, "gitemid", "graphid");
			CGraphGet gparams = new CGraphGet();
			gparams.setOutput(params.getSelectGraphs());
			gparams.setItemIds(relationMap.getRelatedLongIds());
			gparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.Graph(this.idBean, getSqlExecutor()).get(gparams);
			relationMap.mapMany(result, datas, "graphs");
		}
	}
	
}
