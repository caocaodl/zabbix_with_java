package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDCheckGet;
import com.isoft.iradar.model.params.CDHostGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with discovery services.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CDServiceDAO extends CCoreLongKeyDAO<CDServiceGet> {

	public CDServiceDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "dservices", "ds", new String[]{"dserviceid", "dhostid", "ip"});
	}

	/**
	 * Get discovery service data.
	 *
	 * @param array  options
	 * @param array  options["groupids"]				ServiceGroup IDs
	 * @param array  options["hostids"]				Service IDs
	 * @param bool   options["monitored_hosts"]		only monitored Services
	 * @param bool   options["templated_hosts"]		include templates in result
	 * @param bool   options["with_items"]				only with items
	 * @param bool   options["with_triggers"]			only with triggers
	 * @param bool   options["with_httptests"]			only with http tests
	 * @param bool   options["with_graphs"]			only with graphs
	 * @param bool   options["editable"]				only with read-write permission. Ignored for SuperAdmins
	 * @param bool   options["selectGroups"]			select ServiceGroups
	 * @param bool   options["selectTemplates"]		select Templates
	 * @param bool   options["selectItems"]			select Items
	 * @param bool   options["selectTriggers"]			select Triggers
	 * @param bool   options["selectGraphs"]			select Graphs
	 * @param int    options["count"]					count Services, returned column name is rowscount
	 * @param string options["pattern"]				search hosts by pattern in Service name
	 * @param string options["extendPattern"]			search hosts by pattern in Service name, ip and DNS
	 * @param int    options["limit"]					limit selection
	 * @param string options["sortfield"]				field to sort by
	 * @param string options["sortorder"]				sort order
	 *
	 * @return array									service data as array or false if error
	 */
	@Override
	public <T> T get(CDServiceGet params) {
		int userType = CWebUser.getType();	
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("dservices", "ds.dserviceid");
		sqlParts.from.put("dservices", "dservices ds");
		
		// editable + PERMISSION CHECK
		if (USER_TYPE_SUPER_ADMIN == userType) {
		} else if (is_null(params.getEditable()) && (userType == USER_TYPE_IRADAR_ADMIN)) {
		} else if (!is_null(params.getEditable()) && (userType!=USER_TYPE_SUPER_ADMIN)) {
			return (T)array();
		}
		
		// dserviceids
		if (!is_null(params.getDserviceIds())) {
			sqlParts.where.dbConditionInt("dserviceid","ds.dserviceid",params.getDserviceIds());
		}

		// dhostids
		if (!is_null(params.getDhostIds())) {
			sqlParts.select.put("dhostid","ds.dhostid");
			sqlParts.where.dbConditionInt("ds.dhostid",params.getDhostIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dhostid","ds.dhostid");
			}
		}

		// dcheckids
		if (!is_null(params.getDcheckIds())) {
			sqlParts.select.put("dcheckid","dc.dcheckid");
			sqlParts.from.put("dhosts","dhosts dh");
			sqlParts.from.put("dchecks","dchecks dc");

			sqlParts.where.dbConditionInt("dc.dcheckid",params.getDcheckIds());
			sqlParts.where.put("dhds.tenantid","dh.tenantid=ds.tenantid");
			sqlParts.where.put("dhds","dh.hostid=ds.hostid");
			sqlParts.where.put("dcdh.tenantid","dc.tenantid=dh.tenantid");
			sqlParts.where.put("dcdh","dc.druleid=dh.druleid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dcheckid","dc.dcheckid");
			}
		}

		// druleids
		if (!is_null(params.getDruleIds())) {
			sqlParts.select.put("druleid","dh.druleid");
			sqlParts.from.put("dhosts","dhosts dh");

			sqlParts.where.dbConditionInt("druleid","dh.druleid",params.getDruleIds());
			sqlParts.where.put("dhds.tenantid","dh.tenantid=ds.tenantid");
			sqlParts.where.put("dhds","dh.dhostid=ds.dhostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("druleid","dh.druleid");
			}
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("dservices ds", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("dservices ds", params, sqlParts);
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
				Long id = (Long)row.get("dserviceid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				// druleids
				if (isset(row.get("druleid")) && is_null(params.getSelectDRules())) {
					if (!isset(result.get(id).get("drules"))) {
						result.get(id).put("drules", new CArray());
					}
					((CArray)result.get(id).get("drules")).add(map("druleid", row.get("druleid")));
				}
				// dhostids
				if (isset(row.get("dhostid")) && is_null(params.getSelectDHosts())) {
					if (!isset(result.get(id).get("dhosts"))) {
						result.get(id).put("dhosts", new CArray());
					}
					((CArray)result.get(id).get("dhosts")).add(map("dhostid", row.get("dhostid")));
				}

				result.get(id).putAll(row);
			}
		}

		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[]{"dhostid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("dserviceid"));

		CDServiceGet options = new CDServiceGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"dserviceid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias,
			CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (is_null(params.getCountOutput())) {
			if (!is_null(Nest.value(params,"selectDHosts").$())) {
				addQuerySelect("ds.dhostid", sqlParts);
			}
			
			
			//#非官�? 为拓扑发现补充此代码
			if(!is_null(Nest.value(params,"selectDChecks").$())) {
				addQuerySelect("ds.dcheckid", sqlParts);
			}
		}
	}

	@Override
	protected void addRelatedObjects(CDServiceGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] dserviceIds = result.keysAsLong();
		
		// select_drules
		if (!is_null(params.getSelectDRules()) && !API_OUTPUT_COUNT.equals(params.getSelectDRules())) {
			CRelationMap relationMap = new CRelationMap();
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(
				getSqlExecutor(),
				"SELECT ds.dserviceid,dh.druleid"+
					" FROM dservices ds,dhosts dh"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "dservices", "ds", params)+
					" AND "+sqlParts.dual.dbConditionInt("ds.dserviceid", dserviceIds)+
					" AND ds.tenantid=dh.tenantid"+
					" AND ds.dhostid=dh.dhostid",
				sqlParts.getNamedParams()
			);
			for(Map rule : dbRules) {
				relationMap.addRelation(Nest.value(rule,"dserviceid").$(), Nest.value(rule,"druleid").$());
			}
			
			CDRuleGet droptions = new CDRuleGet();
			droptions.setOutput(params.getSelectDRules());
			droptions.setDruleIds(relationMap.getRelatedLongIds());
			droptions.setPreserveKeys(true);
			
			CArray<Map> drules = API.DRule(this.idBean, this.getSqlExecutor()).get(droptions);
			if (!is_null(params.getLimitSelects())) {
				order_result(drules, "name");
			}
			relationMap.mapMany(result, drules, "drules");
		}
		
		// selectDHosts
		if (!is_null(params.getSelectDHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectDHosts())) {
			CRelationMap relationMap = createRelationMap(result, "dserviceid", "dhostid");
			
			CDHostGet dhparams = new CDHostGet();
			dhparams.setOutput(params.getSelectDHosts());
			dhparams.setDhostIds(relationMap.getRelatedLongIds());
			dhparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.DHost(this.idBean, this.getSqlExecutor()).get(dhparams);
			if (!is_null(params.getLimitSelects())) {
				order_result(datas, "dhostid");
			}
			relationMap.mapMany(result, datas, "dhosts", params.getLimitSelects());
		}
		
		// selectHosts
		if (!is_null(params.getSelectHosts())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectHosts())){
				SqlBuilder sqlParts = new SqlBuilder();		
				sqlParts.select.put("ds.dserviceid");
				sqlParts.select.put("i.hostid");
				sqlParts.from.put("dservices ds");
				sqlParts.from.put("interface i");
				applyQueryTenantOptions("dservices", "ds", params, sqlParts);
				sqlParts.where.dbConditionInt("ds.dserviceid", dserviceIds);
				sqlParts.where.put("ds.tenantid=i.tenantid");
				sqlParts.where.put("ds.ip=i.ip");
				
				CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
				CRelationMap relationMap = this.createRelationMap(datas, "dserviceid", "hostid");
				
				CHostGet hparams = new CHostGet();
				hparams.setOutput(params.getSelectHosts());
				hparams.setHostIds(relationMap.getRelatedLongIds());
				hparams.setPreserveKeys(true);
				hparams.setSortfield(new String[]{"status"});
				datas = API.Host(this.idBean, getSqlExecutor()).get(hparams);
				if(!is_null(params.getLimitSelects())){
					order_result(datas, "hostid");
				}
				relationMap.mapMany(result, datas, "hosts", params.getLimitSelects());
			} else {
				CHostGet hparams = new CHostGet();
				hparams.setDserviceIds(dserviceIds);
				hparams.setCountOutput(true);
				hparams.setGroupCount(true);
				CArray<Map> hosts = API.Host(this.idBean, getSqlExecutor()).get(hparams);
				for(Entry<Object, Map> e:result.entrySet()){
					Object dserviceid = e.getKey();
					Map dservice = e.getValue();
					if(hosts.containsKey(dserviceid)){
						dservice.put("hosts", dservice.get("rowscount"));
					} else {
						dservice.put("hosts", 0);
					}
				}
			}
		}
		
		// selectDChecks  //#非官�? 为拓扑发现补充此代码
		if (!is_null(params.getSelectDChecks()) && !API_OUTPUT_COUNT.equals(params.getSelectDChecks())) {
			CRelationMap relationMap = createRelationMap(result, "dserviceid", "dcheckid");
			
			CDCheckGet dhparams = new CDCheckGet();
			dhparams.setOutput(params.getSelectDChecks());
			dhparams.setDcheckIds(relationMap.getRelatedLongIds());
			dhparams.setPreserveKeys(true);
			CArray<Map> datas = API.DCheck(this.idBean, getSqlExecutor()).get(dhparams);
			if (!is_null(params.getLimitSelects())) {
				order_result(datas, "dcheckid");
			}
			relationMap.mapMany(result, datas, "dchecks", params.getLimitSelects());
		}
	}	
}
