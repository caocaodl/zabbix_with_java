package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDCheckGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with discovery checks.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CDCheckDAO extends CCoreLongKeyDAO<CDCheckGet> {

	public CDCheckDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "dchecks", "dc", new String[]{"dcheckid", "druleid"});
	}

	@Override
	public <T> T get(CDCheckGet params) {
		int userType = CWebUser.getType();	
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("dchecks", "dc.dcheckid");
		sqlParts.from.put("dchecks", "dchecks dc");
		
		// editable + PERMISSION CHECK
		if (USER_TYPE_SUPER_ADMIN == userType) {
		} else if (is_null(params.getEditable()) && (userType == USER_TYPE_IRADAR_ADMIN)) {
		} else if (!is_null(params.getEditable()) && (userType!=USER_TYPE_SUPER_ADMIN)) {
			return (T)array();
		}

		// dcheckids
		if (!is_null(params.getDcheckIds())) {
			sqlParts.where.dbConditionInt("dcheckid","dc.dcheckid",params.getDcheckIds());
		}

		// druleids
		if (!is_null(params.getDruleIds())) {
			sqlParts.select.put("druleid","dc.druleid");
			sqlParts.where.dbConditionInt("dc.druleid",params.getDruleIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("druleid","dc.druleid");
			}
		}

		// dserviceids
		if (!is_null(params.getDserviceIds())) {
			sqlParts.select.put("dserviceid","ds.dserviceid");
			sqlParts.from.put("dhosts","dhosts dh");
			sqlParts.from.put("dservices","dservices ds");

			sqlParts.where.dbConditionInt("ds","ds.dserviceid",params.getDserviceIds());
			sqlParts.where.put("dcdh.tenantid","dc.tenantid=dh.tenantid");
			sqlParts.where.put("dcdh","dc.druleid=dh.druleid");
			sqlParts.where.put("dhds.tenantid","dh.tenantid=ds.tenantid");
			sqlParts.where.put("dhds","dh.hostid=ds.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dserviceid","ds.dserviceid");
			}
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("dchecks dc", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("dchecks dc", params, sqlParts);
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
				if (params.getGroupCount() != null) {
					result.add(row);
				} else {
					ret = row.get("rowscount");
				}
			} else {
				Long id = (Long)row.get("dcheckid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				// druleids
				if (isset(row.get("druleid")) && is_null(params.getSelectDRules())) {
					if (!isset(result.get(id).get("drules"))) {
						result.get(id).put("drules", new CArray());
					}
					((CArray)result.get(id).get("drules")).add(map("druleid", row.remove("druleid")));
				}

				result.get(id).putAll(row);
			}
		}
		
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[]{"druleid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	/**
	 * Check if user has read permissions for discovery checks.
	 *
	 * @param array ids
	 * @return bool
	 */
	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CDCheckGet params = new CDCheckGet();
		params.setDcheckIds(ids);
		params.setCountOutput(true);
		long count = get(params);
		return (count(ids) == count);
	}

	/**
	 * Check if user has write permissions for discovery checks.
	 *
	 * @param array ids
	 * @return bool
	 */
	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CDCheckGet params = new CDCheckGet();
		params.setDcheckIds(ids);
		params.setEditable(true);
		params.setCountOutput(true);
		long count = get(params);
		return (count(ids) == count);
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (is_null(params.getCountOutput())) {
			if (!is_null(Nest.value(params,"selectDRules").$())) {
				this.addQuerySelect("dc.druleid", sqlParts);
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(CDCheckGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		// select_drules
		if (!is_null(params.getSelectDRules()) && !API_OUTPUT_COUNT.equals(params.getSelectDRules())) {
			CRelationMap relationMap = createRelationMap(result, "dcheckid", "druleid");
			CDRuleGet druleParams = new CDRuleGet();
			druleParams.setOutput(params.getSelectDRules());
			druleParams.setDruleIds(relationMap.getRelatedLongIds());
			druleParams.setPreserveKeys(true);
			druleParams.setSortfield(new String[]{"name"});
			druleParams.setLimit(params.getLimitSelects());
			
			CArray<Map> drules = API.DRule(this.idBean, getSqlExecutor()).get(druleParams);
			relationMap.mapMany(result, drules, "drules", params.getLimitSelects());
		}
	}
	
}
