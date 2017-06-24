package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.model.params.CTenantGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.IMap;

/**
 * Class containing methods for operations with tenants.
 * @author benne
  */
public class CTenantDAO extends CCoreStringKeyDAO<CTenantGet> {

	public CTenantDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "tenants", "t", new String[]{"tenantid", "name", "proxy_hostid", "status"});
	}

	/**
	 * Get tenant data.
	 *
	 * @param array         options
	 * @param array         options['groupids']                 HostGroup IDs
	 * @param array         options['hostids']                  Host IDs
	 * @param boolean       options['monitored_hosts']          only monitored Hosts
	 * @param boolean       options['templated_hosts']          include templates in result
	 * @param boolean       options['with_items']               only with items
	 * @param boolean       options['with_monitored_items']     only with monitored items
	 * @param boolean       options['with_triggers']            only with triggers
	 * @param boolean       options['with_monitored_triggers']  only with monitored triggers
	 * @param boolean       options['with_httptests']           only with http tests
	 * @param boolean       options['with_monitored_httptests'] only with monitored http tests
	 * @param boolean       options['with_graphs']              only with graphs
	 * @param boolean       options['editable']                 only with read-write permission. Ignored for SuperAdmins
	 * @param boolean       options['selectGroups']             select HostGroups
	 * @param boolean       options['selectHosts']              select Items
	 * @param boolean       options['selectTriggers']           select Triggers
	 * @param boolean       options['selectGraphs']             select Graphs
	 * @param boolean       options['selectApplications']       select Applications
	 * @param boolean       options['selectMacros']             select Macros
	 * @param boolean|array options['selectInventory']          select Inventory
	 * @param boolean       options['withInventory']            select only hosts with inventory
	 * @param int           options['count']                    count Hosts, returned column name is rowscount
	 * @param string        options['pattern']                  search hosts by pattern in Host name
	 * @param string        options['extendPattern']            search hosts by pattern in Host name, ip and DNS
	 * @param int           options['limit']                    limit selection
	 * @param string        options['sortfield']                field to sort by
	 * @param string        options['sortorder']                sort order
	 *
	 * @return array|boolean Host data as array or false if error
	 */
	@Override
	public <T> T get(CTenantGet params) {
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("tenants", "t.tenantid");
		sqlParts.from.put("tenants", "tenants t");
		
		sqlParts.where.put("proxy", "t.tenantid<>'-'");
		sqlParts.where.put("enabled", "t.enabled=1");
		
		// tenantids
		if (!is_null(params.getTenantIds())) {
			sqlParts.where.dbConditionString("tenantid","t.tenantid", params.getTenantIds());
		}
		
		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("hostid","h.hostid");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.dbConditionInt("h.hostid", params.getHostIds());
			sqlParts.where.put("th.tenantid","t.tenantid=h.tenantid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hostid","h.hostid");
			}
		}
		
		// proxyids
		if (!is_null(params.getProxyIds())) {
			sqlParts.select.put("proxy_hostid","t.proxy_hostid");
			sqlParts.where.dbConditionInt("t.proxy_hostid", params.getProxyIds());
		}

		// monitored tenants
		if (!is_null(params.getMonitoredTenants()) && params.getMonitoredTenants()) {
			sqlParts.where.put("status", "t.status="+HOST_STATUS_MONITORED);
		} else if (!is_null(params.getMonitoredTenants()) && !params.getMonitoredTenants()) {
			sqlParts.where.put("status", "t.status="+HOST_STATUS_NOT_MONITORED);
		}
		
		// monitored tenants
		if (!is_null(params.getWithoutLessor()) && params.getWithoutLessor()) {
			sqlParts.where.put("lessor", "t.tenantid<>'"+Feature.defaultTenantId+"'");
		}
		
		if (!is_null(params.getWithVms())) {
			sqlParts.where.put("EXISTS ("+
					" SELECT 1"+ 
					" FROM hosts h"+ 
					" INNER JOIN hosts_groups hg ON h.tenantid = hg.tenantid AND hg.hostid = h.hostid AND hg.groupid = "+IMonGroup.MON_VM.id()+ 
					" WHERE h.tenantid = t.tenantid AND h.hostid_os IS NOT NULL"+ 
					")");
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("tenants t", params, sqlParts);
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("tenants t", params, sqlParts);
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
				String id = (String)row.get("tenantid");
				
				Map resultRow = result.get(id);
				if (!isset(resultRow)) {
					resultRow = new IMap();
					result.put(id, resultRow);
				}
				
				// hostids
				if (isset(row.get("hostid")) && is_null(params.getSelectHosts())) {
					if (!isset(resultRow.get("hosts"))) {
						resultRow.put("hosts", new CArray());
					}
					((CArray)resultRow.get("hosts")).add(map("hostid", row.remove("hostid")));
				}
				
				resultRow.putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Get Tenant ID by Tenant name
	 * @param array tenant_data
	 * @param string tennat_data["name"]
	 * @return int|boolean
	 */
	public CArray<Map> getObjects(CArray tenantData) {
		CTenantGet options = new CTenantGet();
		options.setFilter(tenantData);
		options.setOutput(API_OUTPUT_EXTEND);
		return get(options);
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("tenantid", "name"));
		CTenantGet options = new CTenantGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"tenantid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
}
