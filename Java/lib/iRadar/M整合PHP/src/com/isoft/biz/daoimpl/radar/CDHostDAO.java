package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDHostGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with discovery hosts.
 * @author benne
 */
@CodeConfirmed("benne.2.2.6")
public class CDHostDAO extends CCoreLongKeyDAO<CDHostGet> {

	public CDHostDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "dhosts", "dh", new String[]{"dhostid", "druleid"});
	}
	
	/**
	 * Get host data.
	 *
	 * @param array  options
	 * @param array  options["groupids"]				HostGroup IDs
	 * @param bool   options["monitored_hosts"]		only monitored Hosts
	 * @param bool   options["templated_hosts"]		include templates in result
	 * @param bool   options["with_items"]				only with items
	 * @param bool   options["with_triggers"]			only with triggers
	 * @param bool   options["with_httptests"]			only with http tests
	 * @param bool   options["with_graphs"]			only with graphs
	 * @param bool   options["editable"]				only with read-write permission. Ignored for SuperAdmins
	 * @param bool   options["selectTemplates"]		select Templates
	 * @param bool   options["selectItems"]			select Items
	 * @param bool   options["selectTriggers"]			select Triggers
	 * @param bool   options["selectGraphs"]			select Graphs
	 * @param int    options["count"]					count Hosts, returned column name is rowscount
	 * @param string options["pattern"]				search hosts by pattern in Host name
	 * @param string options["extendPattern"]			search hosts by pattern in Host name, ip and DNS
	 * @param int    options["limit"]					limit selection
	 * @param string options["sortfield"]				field to sort by
	 * @param string options["sortorder"]				sort order
	 *
	 * @return array									Host data as array or false if error
	 */
	@Override
	public <T> T get(CDHostGet params) {
		int userType = CWebUser.getType();	
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("dhosts", "dh.dhostid");
		sqlParts.from.put("dhosts", "dhosts dh");
		
		// editable + PERMISSION CHECK
		if (USER_TYPE_SUPER_ADMIN == userType) {
		} else if (is_null(params.getEditable()) && (userType == USER_TYPE_IRADAR_ADMIN)) {
		} else if (!is_null(params.getEditable()) && (userType!=USER_TYPE_SUPER_ADMIN)) {
			return (T)array();
		}
		
		// dhostids
		if (!is_null(params.getDhostIds())) {
			sqlParts.where.dbConditionInt("dhostid","dh.dhostid",params.getDhostIds());
		}

		// druleids
		if (!is_null(params.getDruleIds())) {
			sqlParts.select.put("druleid","dh.druleid");
			sqlParts.where.dbConditionInt("druleid","dh.druleid",params.getDruleIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("druleid","dh.druleid");
			}
		}

		// dserviceids
		if (!is_null(params.getDserviceIds())) {
			sqlParts.select.put("dserviceid","ds.dserviceid");
			sqlParts.from.put("dservices","dservices ds");

			sqlParts.where.dbConditionInt("ds","ds.dserviceid",params.getDserviceIds());
			sqlParts.where.put("dhds.tenantid","dh.tenantid=ds.tenantid");
			sqlParts.where.put("dhds","dh.hostid=ds.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dserviceid","ds.dserviceid");
			}
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("dhosts dh", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("dhosts dh", params, sqlParts);
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
				Long id = (Long)row.get("dhostid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
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
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("dhostid"));
		CDHostGet options = new CDHostGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"dhostid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (is_null(params.getCountOutput())) {
			if (!is_null(Nest.value(params,"selectDRules").$())) {
				this.addQuerySelect("dh.druleid", sqlParts);
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(CDHostGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] dhostIds = result.keysAsLong();
		// select_drules
		if (!is_null(params.getSelectDRules()) && !API_OUTPUT_COUNT.equals(params.getSelectDRules())) {
			CRelationMap relationMap = createRelationMap(result, "dhostid", "druleid");
			CDRuleGet druleParams = new CDRuleGet();
			druleParams.setOutput(params.getSelectDRules());
			druleParams.setDruleIds(relationMap.getRelatedLongIds());
			druleParams.setPreserveKeys(true);
			druleParams.setSortfield(new String[]{"name"});
			druleParams.setLimit(params.getLimitSelects());
			
			CArray<Map> drules = API.DRule(this.idBean, getSqlExecutor()).get(druleParams);
			relationMap.mapMany(result, drules, "drules", params.getLimitSelects());
		}
		
		// selectDServices
		if (!is_null(params.getSelectDServices())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectDServices())){
				CDServiceGet dsparams = new CDServiceGet();
				dsparams.setOutput(outputExtend("dservices", new String[]{"dserviceid","dhostid"},params.getSelectDServices()));
				dsparams.setDhostIds(dhostIds);
				dsparams.setPreserveKeys(true);
				dsparams.setSortfield(new String[]{"name"});
				
				CArray<Map> objects = API.DService(this.idBean, getSqlExecutor()).get(dsparams);
				
				CRelationMap relationMap = createRelationMap(objects, "dhostid", "dserviceid");
				unsetExtraFields(objects, new String[]{"dserviceid","dhostid"}, params.getSelectDServices());
				relationMap.mapMany(result, objects, "dservices", params.getLimitSelects());				
			} else {
				CDServiceGet dsparams = new CDServiceGet();
				dsparams.setOutput(params.getSelectDServices());
				dsparams.setDhostIds(dhostIds);
				dsparams.setCountOutput(true);
				dsparams.setGroupCount(true);
				
				CArray<Map> dhosts = API.DService(this.idBean, getSqlExecutor()).get(dsparams);
				for(Entry<Object, Map> e: result.entrySet()){
					Object dhostid = e.getKey();
					Map dhost = e.getValue();
					if(dhosts.containsKey(dhostid)){
						dhost.put("dservices", ((Map)dhost).get("rowscount"));
					} else {
						dhost.put("dservices", 0);
					}
				}
			}
		}
	}
	
}
