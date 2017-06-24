package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sort;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.Cphp.unsets;
import static com.isoft.iradar.core.utils.EasyMap.getInteger;
import static com.isoft.iradar.core.utils.EasyMap.getString;
import static com.isoft.iradar.inc.AuditUtil.*;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_AUTOMATIC;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_DISABLED;
import static com.isoft.iradar.inc.Defines.HOST_INVENTORY_MANUAL;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_PREG_HOST_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOST_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.HostsUtil.updateHostStatus;
import static com.isoft.iradar.inc.HttpTestUtil.get_httptests_by_hostid;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.object.CUpdateDiscoveredValidator;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with hosts.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CHostDAO extends CHostGeneralDAO<CHostGet> {

	public CHostDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, new String[]{"hostid", "host", "name", "status"});
	}

	@Override
	public <T> T get(CHostGet params) {
		return get(params,false,0,0);
	}
	
	/**
	 * Get host data.
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
	 * @param boolean       options['selectItems']              select Items
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
	public <T> T get(CHostGet params,boolean flag,int start,int count) {
		int userType = CWebUser.getType();
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("hosts", "h.hostid");
		sqlParts.from.put("hosts", "hosts h");
		sqlParts.where.put("flags","h.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")");
		
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;
			Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);

			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM hosts_groups hgg"+
						" JOIN rights r"+
							" ON r.tenantid=hgg.tenantid"+ 
								" AND r.id=hgg.groupid"+
								" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
					" WHERE h.tenantid=hgg.tenantid"+ 
					" AND h.hostid=hgg.hostid"+
					" GROUP BY hgg.hostid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+
						" AND MAX(r.permission)>="+permission+
					")");
		}
		
		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.where.dbConditionInt("hostid","h.hostid", params.getHostIds());
		}
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.groupid", params.getGroupIds());
			sqlParts.where.put("hgh.tenantid","hg.tenantid=h.tenantid");
			sqlParts.where.put("hgh","hg.hostid=h.hostid");			

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("groupid","hg.groupid");
			}
		}
		
		// proxyids
		if (!is_null(params.getProxyIds())) {
			sqlParts.select.put("proxy_hostid","h.proxy_hostid");
			sqlParts.where.dbConditionInt("h.proxy_hostid", params.getProxyIds());
		}
		
		// templateids
		if (!is_null(params.getTemplateIds())) {
			sqlParts.select.put("templateid","ht.templateid");
			sqlParts.from.put("hosts_templates","hosts_templates ht");
			sqlParts.where.dbConditionInt("ht.templateid",params.getTemplateIds());
			sqlParts.where.put("hht.tenantid","h.tenantid=ht.tenantid");
			sqlParts.where.put("hht","h.hostid=ht.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("templateid","ht.templateid");
			}
		}
		
		// interfaceids
		if (!is_null(params.getInterfaceIds())) {
			sqlParts.select.put("interfaceid","hi.interfaceid");
			sqlParts.from.put("interface","interface hi");
			sqlParts.where.dbConditionInt("hi.interfaceid",params.getInterfaceIds());
			sqlParts.where.put("hhi.tenantid","h.tenantid=hi.tenantid");
			sqlParts.where.put("hhi","h.hostid=hi.hostid");
		}
		
		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","i.itemid");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("i.itemid",params.getItemIds());
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
		}

		// triggerids
		if (!is_null(params.getTriggerIds())) {
			sqlParts.select.put("triggerid","f.triggerid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("f.triggerid",params.getTriggerIds());
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
		}

		// httptestids
		if (!is_null(params.getHttpTestIds())) {
			sqlParts.select.put("httptestid","ht.httptestid");
			sqlParts.from.put("httptest","httptest ht");
			sqlParts.where.dbConditionInt("ht.httptestid",params.getHttpTestIds());
			sqlParts.where.put("aht.tenantid","ht.tenantid=h.tenantid");
			sqlParts.where.put("aht","ht.hostid=h.hostid");
		}

		// graphids
		if (!is_null(params.getGraphIds())) {
			sqlParts.select.put("graphid","gi.graphid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("gi.graphid",params.getGraphIds());
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
		}

		// applicationids
		if (!is_null(params.getApplicationIds())) {
			sqlParts.select.put("applicationid","a.applicationid");
			sqlParts.from.put("applications","applications a");
			sqlParts.where.dbConditionInt("a.applicationid",params.getApplicationIds());
			sqlParts.where.put("ah.tenantid","a.tenantid=h.tenantid");
			sqlParts.where.put("ah","a.hostid=h.hostid");
		}

		// dserviceids
		if (!is_null(params.getDserviceIds())) {
			sqlParts.select.put("dserviceid","ds.dserviceid");
			sqlParts.from.put("dservices","dservices ds");
			sqlParts.from.put("interface","interface i");
			sqlParts.where.dbConditionInt("ds.dserviceid",params.getDserviceIds());
			sqlParts.where.put("dsh.tenantid","ds.tenantid=i.tenantid");
			sqlParts.where.put("dsh","ds.ip=i.ip");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dserviceid","ds.dserviceid");
			}
		}

		// maintenanceids
		if (!is_null(params.getMaintenanceIds())) {
			sqlParts.select.put("maintenanceid","mh.maintenanceid");
			sqlParts.from.put("maintenances_hosts","maintenances_hosts mh");
			sqlParts.where.dbConditionInt("mh.maintenanceid",params.getMaintenanceIds());
			sqlParts.where.put("hmh.tenantid","h.tenantid=mh.tenantid");
			sqlParts.where.put("hmh","h.hostid=mh.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("maintenanceid","mh.maintenanceid");
			}
		}

		// monitored_hosts, templated_hosts
		if (!is_null(params.getMonitoredHosts())) {
			sqlParts.where.put("status", "h.status="+HOST_STATUS_MONITORED);
		} else if (!is_null(params.getTemplatedHosts())) {
			sqlParts.where.put("status","h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+","+HOST_STATUS_TEMPLATE+")");
		} else if (!is_null(params.getProxyHosts())) {
			sqlParts.where.put("status","h.status IN ("+HOST_STATUS_PROXY_ACTIVE+","+HOST_STATUS_PROXY_PASSIVE+")");
		} else {
			sqlParts.where.put("status","h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+")");
		}
		
		// with_items, with_monitored_items, with_historical_items, with_simple_graph_items
		if (!is_null(params.getWithItems())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i"+
					" WHERE h.tenantid=i.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		} else if (!is_null(params.getWithMonitoredItems())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i"+
					" WHERE h.tenantid=i.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		} else if (!is_null(params.getWithSimpleGraphItems())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i"+
					" WHERE h.tenantid=i.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND i.value_type IN ("+ITEM_VALUE_TYPE_FLOAT+","+ITEM_VALUE_TYPE_UINT64+")"+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}
		
		// with_triggers, with_monitored_triggers
		if (!is_null(params.getWithTriggers())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i,functions f,triggers t"+
					" WHERE h.tenantid=i.tenantid"+
					" AND i.tenantid=f.tenantid"+
					" AND f.tenantid=t.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND i.itemid=f.itemid"+
					" AND f.triggerid=t.triggerid"+
					" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		} else if (!is_null(params.getWithMonitoredTriggers())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i,functions f,triggers t"+
					" WHERE h.tenantid=i.tenantid"+
					" AND i.tenantid=f.tenantid"+
					" AND f.tenantid=t.tenantid"+
					" AND h.hostid=i.hostid"+
					" AND i.itemid=f.itemid"+
					" AND f.triggerid=t.triggerid"+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND t.status="+TRIGGER_STATUS_ENABLED+
					" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}
		
		// with_httptests, with_monitored_httptests
		if (!is_null(params.getWithHttpTests())) {
			sqlParts.where.put("EXISTS (SELECT NULL FROM httptest ht WHERE ht.hostid=h.hostid)");
		} else if (!is_null(params.getWithMonitoredHttpTests())) {
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM httptest ht"+
					" WHERE h.tenantid=ht.tenantid"+
					" AND h.hostid=ht.hostid"+
					" AND ht.status="+HTTPTEST_STATUS_ACTIVE+
					")");
		}
		
		// with_graphs
		if (!is_null(params.getWithGraphs())) {
			sqlParts.where.put("EXISTS ("+
					" SELECT 1"+
					" FROM items i,graphs_items gi,graphs g"+
					" WHERE i.tenantid=h.tenantid"+
					" AND i.tenantid=gi.tenantid"+
					" AND gi.tenantid=g.tenantid"+
					" AND i.hostid=h.hostid"+
					" AND i.itemid=gi.itemid"+
					" AND gi.graphid=g.graphid"+
					" AND g.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}

		// with applications
		if (!is_null(params.getWithApplications())) {
			sqlParts.from.put("applications","applications a");
			sqlParts.where.put("a.tenantid=h.tenantid");
			sqlParts.where.put("a.hostid=h.hostid");
		}

		// withInventory
		if (!is_null(params.getWithInventory()) && params.getWithInventory()) {
			sqlParts.where.put(" h.hostid IN ("+
					" SELECT hin.hostid"+
					" FROM host_inventory hin"+
					")");
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("hosts h", params, sqlParts);
			if(dbSearch("interface hi", params, sqlParts)){
				sqlParts.from.put("interface", "interface hi");
				sqlParts.where.put("hi.tenantid", "h.tenantid=hi.tenantid");
				sqlParts.where.put("hi", "h.hostid=hi.hostid");
			}
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("hosts h", params, sqlParts);
			if(dbFilter("interface hi", params, sqlParts)){
				sqlParts.from.put("interface", "interface hi");
				sqlParts.where.put("hi.tenantid", "h.tenantid=hi.tenantid");
				sqlParts.where.put("hi", "h.hostid=hi.hostid");
			}
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = null;
		if(flag){
			String sql = CCoreLongKeyDAO.createSelectQueryFromParts(sqlParts);
			Map paraMap = sqlParts.getNamedParams();
			datas = DBselect(getSqlExecutor(), sql, count,start ,paraMap);
		}else{
			datas = DBselect(getSqlExecutor(), sqlParts);
		}
		
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
				Long id = (Long)row.get("hostid");
				
				Map resultRow = result.get(id);
				if (!isset(resultRow)) {
					resultRow = new IMap();
					result.put(id, resultRow);
				}
				
				// groupids
				if (isset(row.get("groupid")) && is_null(params.getSelectGroups())) {
					if (!isset(resultRow.get("groups"))) {
						resultRow.put("groups", new CArray());
					}
					((CArray)resultRow.get("groups")).add(map("groupid", row.remove("groupid")));
				}
				// templateids
				if (isset(row.get("templateid"))) {
					if (!isset(resultRow.get("templates"))) {
						resultRow.put("templates", new CArray());
					}
					Map vo = new HashMap();
					vo.put("templateid", row.get("templateid"));
					vo.put("hostid", row.remove("templateid"));
					((CArray)resultRow.get("templates")).add(vo);
				}
				// triggerids
				if (isset(row.get("triggerid")) && is_null(params.getSelectTriggers())) {
					if (!isset(resultRow.get("triggers"))) {
						resultRow.put("triggers", new CArray());
					}
					((CArray)resultRow.get("triggers")).add(map("triggerid", row.remove("triggerid")));
				}
				// interfaceids
				if (isset(row.get("interfaceid")) && is_null(params.getSelectInterfaces())) {
					if (!isset(resultRow.get("interfaces"))) {
						resultRow.put("interfaces", new CArray());
					}
					((CArray)resultRow.get("interfaces")).add(map("interfaceid", row.remove("interfaceid")));
				}
				// itemids
				if (isset(row.get("itemid")) && is_null(params.getSelectItems())) {
					if (!isset(resultRow.get("items"))) {
						resultRow.put("items", new CArray());
					}
					((CArray)resultRow.get("items")).add(map("itemid", row.remove("itemid")));
				}
				// graphids
				if (isset(row.get("graphid")) && is_null(params.getSelectGraphs())) {
					if (!isset(resultRow.get("graphs"))) {
						resultRow.put("graphs", new CArray());
					}
					((CArray)resultRow.get("graphs")).add(map("graphid", row.remove("graphid")));
				}
				// applicationids
				if (isset(row.get("applicationid"))) {
					if (!isset(resultRow.get("applications"))) {
						resultRow.put("applications", new CArray());
					}
					((CArray)resultRow.get("applications")).add(map("applicationid", row.remove("applicationid")));
				}
				// httptestids
				if (isset(row.get("httptestid"))) {
					if (!isset(resultRow.get("httptests"))) {
						resultRow.put("httptests", new CArray());
					}
					((CArray)resultRow.get("httptests")).add(map("httptestid", row.remove("httptestid")));
				}
				// dhostids
				if (isset(row.get("dhostid"))&& is_null(params.getSelectDHosts())) {
					if (!isset(resultRow.get("dhosts"))) {
						resultRow.put("dhosts", new CArray());
					}
					((CArray)resultRow.get("dhosts")).add(map("dhostid", row.remove("dhostid")));
				}
				// dserviceids
				if (isset(row.get("dserviceid"))&& is_null(params.getSelectDServices())) {
					if (!isset(resultRow.get("dservices"))) {
						resultRow.put("dservices", new CArray());
					}
					((CArray)resultRow.get("dservices")).add(map("dserviceid", row.remove("dserviceid")));
				}
				// maintenanceids
				if (isset(row.get("maintenanceid"))) {
					if (!isset(resultRow.get("maintenances"))) {
						resultRow.put("maintenances", new CArray());
					}
					CArray maintenances = (CArray) resultRow.get("maintenances");
					Map mainMap = map("maintenanceid", row.remove("maintenanceid"));
					maintenances.add(mainMap);
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
	 * Get Host ID by Host name
	 *
	 * @param array host_data
	 * @param string host_data["host"]
	 *
	 * @return int|boolean
	 */
	public CArray<Map> getObjects(CArray hostData) {
		CHostGet options = new CHostGet();
		options.setFilter(hostData);
		options.setOutput(API_OUTPUT_EXTEND);

		CArray<Map> result = get(options);
		return result;
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("hostid", "host", "name"));

		CHostGet options = new CHostGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"hostid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	protected CArray<Map> checkInput(CArray<Map> hosts, String method) {
		boolean create = ("create".equals(method));
		boolean update = ("update".equals(method));

		// permissions
		CArray groupids = array();
		for(Map host : hosts) {
			if (!isset(host,"groups")) {
				continue;
			}
			groupids = array_merge(groupids, rda_objectValues(Nest.value(host,"groups").$(), "groupid"));
		}

		CArray hostDBfields = null;
		CArray<Map> dbHosts = null;
		if (update) {
			hostDBfields  = map("hostid", null);
			CHostGet options = new CHostGet();
			options.setOutput(new String[]{"hostid", "host", "flags"});
			options.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
			options.setEditable(true);
			options.setPreserveKeys(true);
			dbHosts  = get(options);
		} else {
			hostDBfields = map("host", null);
		}

		CArray<Map> dbGroups = null;
		if (!empty(groupids)) {
			CHostGroupGet goptions = new CHostGroupGet();
			goptions.setOutput(API_OUTPUT_EXTEND);
			goptions.setGroupIds(groupids.valuesAsLong());
			goptions.setEditable(true);
			goptions.setPreserveKeys(true);
			dbGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(goptions);
		}

		CArray inventoryFields = getHostInventories();
		inventoryFields = rda_objectValues(inventoryFields, "db_field");

		CArray hostNames = array();
		for(Map host : hosts) {
			if (!check_db_fields(hostDBfields, host)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Wrong fields for host \"%s\".", isset(Nest.value(host,"host").$()) ? Nest.value(host,"host").$() : ""));
			}

			if (isset(host,"inventory") && !empty(Nest.value(host,"inventory").$())) {

				if (isset(host,"inventory_mode") && Nest.value(host,"inventory_mode").asInteger() == HOST_INVENTORY_DISABLED) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot set inventory fields for disabled inventory."));
				}

				CArray<String> fields = array_keys(Nest.value(host,"inventory").asCArray());
				for(String field : fields) {
					if (!in_array(field, inventoryFields)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect inventory field \"%s\".", field));
					}
				}
			}

			CUpdateDiscoveredValidator updateDiscoveredValidator = CValidator.init(new CUpdateDiscoveredValidator(),map(
				"allowed", array("hostid", "status", "inventory"),
				"messageAllowedField", _("Cannot update \"%1$s\" for a discovered host.")
			));
			if (update) {
				if (!isset(dbHosts,host.get("hostid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				// cannot update certain fields for discovered hosts
				checkPartialValidator(host, updateDiscoveredValidator, dbHosts.get(host.get("hostid")));
			} else {
				// if visible name is not given or empty it should be set to host name
				if (!isset(host,"name") || rda_empty(trim(Nest.value(host,"name").asString()))) {
					Nest.value(host,"name").$(Nest.value(host,"host").$());
				}

				if (!isset(host,"groups")) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No groups for host \"%s\".", Nest.value(host,"host").$()));
				}

				if (!isset(host,"interfaces")) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No interfaces for host \"%s\".", Nest.value(host,"host").$()));
				}
			}

			if (isset(host,"groups")) {
				if (!isArray(Nest.value(host,"groups").$()) || empty(Nest.value(host,"groups").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No groups for host \"%s\".", Nest.value(host,"host").$()));
				}

				for(Map group:(CArray<Map>)Nest.value(host,"groups").asCArray()) {
					if (!isset(dbGroups,group.get("groupid"))) {
						throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
					}
				}
			}

			if (isset(host,"interfaces")) {
				if (!isArray(Nest.value(host,"interfaces").$()) || empty(Nest.value(host,"interfaces").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("No interfaces for host \"%s\".", Nest.value(host,"host").$()));
				}
			}

			if (isset(host,"host")) {
				// Check if host name isn't longer than 64 chars
				if (rda_strlen(Nest.value(host,"host").asString()) > 64) {
					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_n(
							"Maximum host name length is %1$d characters, \"%2$s\" is %3$d character.",
							"Maximum host name length is %1$d characters, \"%2$s\" is %3$d characters.",
							64,
							Nest.value(host,"host").asString(),
							rda_strlen(Nest.value(host,"host").asString())
						)
					);
				}

				if (preg_match("^"+RDA_PREG_HOST_FORMAT+"$", Nest.value(host,"host").asString())==0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect characters used for host name \"%s\".", Nest.value(host,"host").$()));
				}

				if (isset(Nest.value(hostNames,"host",host.get("host")).$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Duplicate host. Host with the same host name \"%s\" already exists in data.", Nest.value(host,"host").$()));
				}
				Nest.value(hostNames,"host",host.get("host")).$(update ? Nest.value(host,"hostid").$() : 1);
			}

			if (isset(host,"name")) {
				if (update) {
					// if visible name is empty replace it with host name
					if (rda_empty(trim(Nest.value(host,"name").asString()))) {
						if (!isset(host,"host")) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Visible name cannot be empty if host name is missing."));
						}
						Nest.value(host,"name").$(Nest.value(host,"host").$());
					}
				}

				// Check if visible name isn't longer than 64 chars
				if (rda_strlen(Nest.value(host,"name").asString()) > 64) {
					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_n(
							"Maximum visible host name length is %1$d characters, \"%2$s\" is %3$d character.",
							"Maximum visible host name length is %1$d characters, \"%2$s\" is %3$d characters.",
							64,
							Nest.value(host,"name").asString(),
							rda_strlen(Nest.value(host,"name").asString())
						)
					);
				}

				if (isset(Nest.value(hostNames,"name",host.get("name")).$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Duplicate host. Host with the same visible name \"%s\" already exists in data.", Nest.value(host,"name").$()));
				}
				Nest.value(hostNames,"name",host.get("name")).$(update ? Nest.value(host,"hostid").$() : 1);
			}
		}

		if (update || create) {
			if (isset(hostNames,"host") || isset(hostNames,"name")) {
				Map<String,Object[]> filter = new HashMap();
				if (isset(hostNames,"host")) {
					Nest.value(filter,"host").$(array_keys(Nest.value(hostNames,"host").asCArray()).valuesAsString());
				}
				if (isset(hostNames,"name")) {
					Nest.value(filter,"name").$(array_keys(Nest.value(hostNames,"name").asCArray()).valuesAsString());
				}

				CHostGet options = new CHostGet();
				options.setOutput(new String[]{"hostid", "host", "name"});
				options.setFilter(filter);
				options.setSearchByAny(true);
				options.setNopermissions(true);
				options.setPreserveKeys(true);
				options.setEditable(true);	//增加不同租户可以使用相同名称的功能

				CArray<Map> hostsExists = get(options);

				for(Map hostExists : hostsExists) {
					if (isset(Nest.value(hostNames,"host",hostExists.get("host")).$())) {
						if (!update || bccomp(Nest.value(hostExists,"hostid").$(), Nest.value(hostNames,"host",hostExists.get("host")).$()) != 0) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host with the same name \"%s\" already exists.", Nest.value(hostExists,"host").$()));
						}
					}

					if (isset(Nest.value(hostNames,"name",hostExists.get("name")).$())) {
						if (!update || bccomp(Nest.value(hostExists,"hostid").$(), Nest.value(hostNames,"name",hostExists.get("name")).$()) != 0) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host with the same visible name \"%s\" already exists.", Nest.value(hostExists,"name").$()));
						}
					}
				}

				CTemplateGet toptions = new CTemplateGet();
				toptions.setOutput(new String[]{"hostid", "host", "name"});
				toptions.setFilter(filter);
				toptions.setSearchByAny(true);
				toptions.setNopermissions(true);
				toptions.setPreserveKeys(true);
				toptions.setEditable(true);
				CArray<Map> templatesExists = API.Template(this.idBean, this.getSqlExecutor()).get(toptions);

				for(Map templateExists : templatesExists) {
					if (isset(Nest.value(hostNames,"host",templateExists.get("host")).$())) {
						if (!update || bccomp(Nest.value(templateExists,"templateid").$(), Nest.value(hostNames,"host",templateExists.get("host")).$()) != 0) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Template with the same name \"%s\" already exists.", Nest.value(templateExists,"host").$()));
						}
					}

					if (isset(Nest.value(hostNames,"name",templateExists.get("name")).$())) {
						if (!update || bccomp(Nest.value(templateExists,"templateid").$(), Nest.value(hostNames,"name",templateExists.get("name")).$()) != 0) {
							throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Template with the same visible name \"%s\" already exists.", Nest.value(templateExists,"name").$()));
						}
					}
				}
			}
		}

		return update ? dbHosts : hosts;
	}

	/**
	 * Add Host
	 *
	 * @param array  hosts multidimensional array with Hosts data
	 * @param string hosts ["host"] Host name.
	 * @param array  hosts ["groups"] array of HostGroup objects with IDs add Host to.
	 * @param int    hosts ["port"] Port. OPTIONAL
	 * @param int    hosts ["status"] Host Status. OPTIONAL
	 * @param int    hosts ["useip"] Use IP. OPTIONAL
	 * @param string hosts ["dns"] DNS. OPTIONAL
	 * @param string hosts ["ip"] IP. OPTIONAL
	 * @param int    hosts ["proxy_hostid"] Proxy Host ID. OPTIONAL
	 * @param int    hosts ["ipmi_authtype"] IPMI authentication type. OPTIONAL
	 * @param int    hosts ["ipmi_privilege"] IPMI privilege. OPTIONAL
	 * @param string hosts ["ipmi_username"] IPMI username. OPTIONAL
	 * @param string hosts ["ipmi_password"] IPMI password. OPTIONAL
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> hosts) {
		CArray<Long> hostids = array();

		checkInput(hosts, "create");

		for(Map host : hosts) {
			CArray<Long> hostsid = insert("hosts", array(Clone.deepcopy(host)));
			Long hostid = reset(hostsid);
			hostids.add(hostid);

			Nest.value(host,"hostid").$(hostid);

			// save groups
			// groups must be added before calling massAdd() for permission validation to work
			CArray<Map> groupsToAdd = array();
			for(Map group : (CArray<Map>)Nest.value(host,"groups").asCArray()) {
				groupsToAdd.add(map(
					"hostid", hostid,
					"groupid", Nest.value(group,"groupid").asLong()
				));
			}
			insert("hosts_groups", groupsToAdd);

			CArray options = array();
			Nest.value(options,"hosts").$(array(host));

			if (isset(host,"templates") && !is_null(Nest.value(host,"templates").$())) {
				Nest.value(options,"templates").$(Nest.value(host,"templates").$());
			}

			if (isset(host,"macros") && !is_null(Nest.value(host,"macros").$())) {
				Nest.value(options,"macros").$(Nest.value(host,"macros").$());
			}

			if (isset(host,"interfaces") && !is_null(Nest.value(host,"interfaces").$())) {
				Nest.value(options,"interfaces").$(Nest.value(host,"interfaces").$());
			}

			CArray result = API.Host(this.idBean, this.getSqlExecutor()).massAdd(options);
			if (empty(result)) {
				throw CDB.exception();
			}

			if (!empty(Nest.value(host,"inventory").$())) {
				Map inventory = Clone.deepcopy(Nest.value(host,"inventory").asCArray());
				inventory.put("hostid", hostid);
				Nest.value(inventory, "inventory_mode").$(isset(host,"inventory_mode") ? Nest.value(host,"inventory_mode").asInteger() : HOST_INVENTORY_MANUAL);
				this.insert("host_inventory", array(inventory), false);
			}
		}

		return map("hostids", hostids.valuesAsLong());
	}

	/**
	 * Update Host.
	 *
	 * @param array  hosts multidimensional array with Hosts data
	 * @param string hosts ["host"] Host name.
	 * @param int    hosts ["port"] Port. OPTIONAL
	 * @param int    hosts ["status"] Host Status. OPTIONAL
	 * @param int    hosts ["useip"] Use IP. OPTIONAL
	 * @param string hosts ["dns"] DNS. OPTIONAL
	 * @param string hosts ["ip"] IP. OPTIONAL
	 * @param int    hosts ["proxy_hostid"] Proxy Host ID. OPTIONAL
	 * @param int    hosts ["ipmi_authtype"] IPMI authentication type. OPTIONAL
	 * @param int    hosts ["ipmi_privilege"] IPMI privilege. OPTIONAL
	 * @param string hosts ["ipmi_username"] IPMI username. OPTIONAL
	 * @param string hosts ["ipmi_password"] IPMI password. OPTIONAL
	 * @param string hosts ["groups"] groups
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> hosts) {
		CArray hostids = rda_objectValues(hosts, "hostid");

		checkInput(hosts, "update");

		// fetch fields required to update host inventory
		CArray<Map> inventories = array();
		for(Map host : hosts) {
			Map inventory = Nest.value(host,"inventory").asCArray();
			Nest.value(inventory,"hostid").$(Nest.value(host,"hostid").$());
			inventories.add(inventory);
		}
		inventories = extendObjects("host_inventory", inventories, new String[]{"inventory_mode"});
		inventories = rda_toHash(inventories, "hostid");

		CArray<Map> macros = array();
		for(Map host : hosts) {
			// extend host inventory with the required data
			if (isset(host,"inventory") && !empty(Nest.value(host,"inventory").$())) {
				Map inventory = inventories.get(host.get("hostid"));

				// if no host inventory record exists in the DB, it's disabled
				if (!isset(inventory,"inventory_mode")) {
					Nest.value(inventory,"inventory_mode").$(HOST_INVENTORY_DISABLED);
				}

				Nest.value(host,"inventory").$(inventory);
			}

			API.HostInterface(this.idBean, this.getSqlExecutor()).replaceHostInterfaces(host);
			unset(host,"interfaces");

			if (isset(host,"macros")) {
				Nest.value(macros,host.get("hostid")).$(Nest.value(host,"macros").$());
				unset(host,"macros");
			}

			Map data = Clone.deepcopy(host);
			Nest.value(data,"hosts").$(array(host));
			CArray result = massUpdate(data);

			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Host update failed."));
			}
		}

		if (!empty(macros)) {
			API.UserMacro(this.idBean, this.getSqlExecutor()).replaceMacros(macros);
		}

		return map("hostids", hostids.valuesAsLong());
	}

	/**
	 * Additionally allows to create new interfaces on hosts.
	 *
	 * Checks write permissions for hosts.
	 *
	 * Additional supported data parameters are:
	 * - interfaces - an array of interfaces to create on the hosts
	 * - templates  - an array of templates to link to the hosts, overrides the CHostGeneral::massAdd()
	 *                'templates' parameter
	 *
	 * @param array data
	 *
	 * @return array
	 */
	@Override
	public CArray massAdd(CArray data) {
		CArray hosts = isset(data,"hosts") ? rda_toArray(Nest.value(data,"hosts").$()) : array();
		CArray hostIds = rda_objectValues(hosts, "hostid");

		// check permissions
		if (!isWritable(hostIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		// add new interfaces
		if (!empty(Nest.value(data,"interfaces").$())) {
			API.HostInterface(this.idBean, this.getSqlExecutor()).massAdd(map(
				"hosts", Nest.value(data,"hosts").$(),
				"interfaces", rda_toArray(Nest.value(data,"interfaces").$())
			));
		}

		// rename the \"templates\" parameter to the common \"templates_link\"
		if (isset(data,"templates")) {
			Nest.value(data,"templates_link").$(Nest.value(data,"templates").$());
			unset(data,"templates");
		}

		Nest.value(data,"templates").$(array());

		return super.massAdd(data);
	}

	/**
	 * Mass update hosts.
	 *
	 * @param array  hosts								multidimensional array with Hosts data
	 * @param array  hosts['hosts']					Array of Host objects to update
	 * @param string hosts['fields']['host']			Host name.
	 * @param array  hosts['fields']['groupids']		HostGroup IDs add Host to.
	 * @param int    hosts['fields']['port']			Port. OPTIONAL
	 * @param int    hosts['fields']['status']			Host Status. OPTIONAL
	 * @param int    hosts['fields']['useip']			Use IP. OPTIONAL
	 * @param string hosts['fields']['dns']			DNS. OPTIONAL
	 * @param string hosts['fields']['ip']				IP. OPTIONAL
	 * @param int    hosts['fields']['proxy_hostid']	Proxy Host ID. OPTIONAL
	 * @param int    hosts['fields']['ipmi_authtype']	IPMI authentication type. OPTIONAL
	 * @param int    hosts['fields']['ipmi_privilege']	IPMI privilege. OPTIONAL
	 * @param string hosts['fields']['ipmi_username']	IPMI username. OPTIONAL
	 * @param string hosts['fields']['ipmi_password']	IPMI password. OPTIONAL
	 *
	 * @return boolean
	 */
	
	public CArray massUpdate(Map data) {
		CArray<Map> hosts = rda_toArray(data.get("hosts"));
		CArray inputHostIds = rda_objectValues(hosts, "hostid");
		CArray<Long> hostids = array_unique(inputHostIds);

		sort(hostids);

		CHostGet hoptions = new CHostGet();
		hoptions.setHostIds(hostids.valuesAsLong());
		hoptions.setEditable(true);
		hoptions.setOutput(API_OUTPUT_EXTEND);
		hoptions.setPreserveKeys(true);
		CArray<Map> updHosts = this.get(hoptions);
		for(Map host: hosts) {
			if (!isset(updHosts.get(host.get("hostid")))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
			}
		}

		// check if hosts have at least 1 group
		if (isset(data.get("groups")) && empty(data.get("groups"))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No groups for hosts."));
		}

		/* Update hosts properties */
		if (isset(data.get("name"))) {
			if (count(hosts) > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot mass update visible host name."));
			}
		}

		if (isset(data.get("host"))) {
			if (0==preg_match("^"+RDA_PREG_HOST_FORMAT+"$", Nest.value(data, "host").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect characters used for host name \"%s\".", data.get("host")));
			}

			if (count(hosts) > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot mass update host name."));
			}

			Map curHost = reset(hosts);

			String curHostName = Nest.value(curHost,"host").asString();
			hoptions = new CHostGet();
			hoptions.setFilter("host", curHostName);
			hoptions.setOutput(new String[]{"hostid", "host"});
			hoptions.setEditable(true);
			hoptions.setNopermissions(true);
			CArray<Map> hostExists = this.get(hoptions);
			
			//此处是Zabbix的BUG，问题出在数据库不区分大小写上（a 和 A都会被查询出来）， 原来其是只凭第一个值进行判断，所以在更新的时候有的是正确的，有的是会报已经存在的错误
			//通过判断是否有此设备ID，来断定是否是更新自己，是自己就应该是正确的
			//但也存在与把A改a依然通过的状况，不过这种情况会在之前的checkInput方法里过滤掉，所以不用担心这个情况
			Iterator<Map> it = hostExists.iterator();
			while(it.hasNext()) {
				Map host = it.next();
				String name = Nest.value(host, "host").asString();
				if(!curHostName.equals(name)) {
					it.remove();
				}
			}
			
			boolean isUpdateSelf = false;
			if(empty(hostExists)) {
				isUpdateSelf = true;
			}else {
				for(Map hostExist: hostExists) {
					if (!empty(hostExist) && (bccomp(getString(hostExist, "hostid"), getString(curHost, "hostid")) == 0)) {
						isUpdateSelf = true;
					}
				}
			}
			
			if(!isUpdateSelf) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host \"%1$s\" already exists.", data.get("host")));
			}

			// can't add host with the same name as existing template
			if (API.Template(this.idBean, getSqlExecutor()).exists(map("host", curHost.get("host")))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Template \"%1$s\" already exists.", curHost.get("host")));
			}
		}

		CArray updateGroups = null;
		if (isset(data.get("groups"))) {
			updateGroups = (CArray)data.get("groups");
		}

		CArray updateInterfaces = null;
		if (isset(data.get("interfaces"))) {
			updateInterfaces = (CArray)data.get("interfaces");
		}

		CArray updateTemplatesClear = null;
		if (isset(data.get("templates_clear"))) {
			updateTemplatesClear = (CArray)rda_toArray(data.get("templates_clear"));
		}

		CArray updateTemplates = null;
		if (isset(data.get("templates"))) {
			updateTemplates = (CArray)data.get("templates");
		}

		CArray updateMacros = null;
		if (isset(data.get("macros"))) {
			updateMacros = (CArray)data.get("macros");
		}

		// second check is necessary, because import incorrectly inputs unset 'inventory' as empty string rather than null
		CArray<Object> updateInventory = null;
		if (isset(data.get("inventory")) && !empty(data.get("inventory"))) {
			updateInventory = (CArray)data.get("inventory");
			updateInventory.put("inventory_mode", null);

			if (isset(data.get("inventory_mode")) && getInteger(data, "inventory_mode") == HOST_INVENTORY_DISABLED) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot set inventory fields for disabled inventory."));
			}
		}

		if (isset(data.get("inventory_mode"))) {
			if (!isset(updateInventory)) {
				updateInventory = array();
			}
			updateInventory.put("inventory_mode", data.get("inventory_mode"));
		}

		Integer updateStatus = null;
		if (isset(data.get("status"))) {
			updateStatus = getInteger(data, "status");
		}

		unsets(data, "hosts", "groups", "interfaces", "templates_clear", "templates", "macros", "inventory", "inventory_mode", "status");

		if (!rda_empty(data)) {
			this.update("hosts", array((Map)map(
				"values", data,
				"where", map("hostid", hostids)
			)));
		}

		if (isset(updateStatus)) {
			updateHostStatus(this.idBean, getSqlExecutor(), hostids.valuesAsLong(), updateStatus);
		}

		Object result= null;
		/*
		 * Update hostgroups linkage
		 */
		if (isset(updateGroups)) {
			updateGroups = rda_toArray(updateGroups);

			CHostGroupGet hgoptions = new CHostGroupGet();
			hgoptions.setHostIds(hostids.valuesAsLong());
			CArray<Map> hostGroups = API.HostGroup(this.idBean, getSqlExecutor()).get(hgoptions);
			CArray hostGroupids = rda_objectValues(hostGroups, "groupid");
			CArray newGroupids = rda_objectValues(updateGroups, "groupid");

			result = this.massAdd(map(
				"hosts", hosts,
				"groups", updateGroups
			));
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot create host group."));
			}

			CArray groupidsToDel = array_diff(hostGroupids, newGroupids);

			if (!empty(groupidsToDel)) {
				result = this.massRemove(map(
					"hostids", hostids,
					"groupids", groupidsToDel
				));
				if (empty(result)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete host group."));
				}
			}
		}

		/*
		 * Update interfaces
		 */
		if (isset(updateInterfaces)) {
			CHostIfaceGet hioptions = new CHostIfaceGet();
			hioptions.setHostIds(hostids.valuesAsLong());
			hioptions.setOutput(API_OUTPUT_EXTEND);
			hioptions.setPreserveKeys(true);
			hioptions.setNopermissions(true);
			CArray hostInterfaces = API.HostInterface(this.idBean, getSqlExecutor()).get(hioptions);
			this.massRemove(map(
				"hostids", hostids,
				"interfaces", hostInterfaces
			));
			this.massAdd(map(
				"hosts", hosts,
				"interfaces", updateInterfaces
			));
		}

		CArray templateidsClear = null;
		if (isset(updateTemplatesClear)) {
			templateidsClear = rda_objectValues(updateTemplatesClear, "templateid");

			if (!empty(updateTemplatesClear)) {
				this.massRemove(map("hostids", hostids, "templateids_clear", templateidsClear));
			}
		} else {
			templateidsClear = array();
		}

		/*
		 * Update template linkage
		 */
		if (isset(updateTemplates)) {
			CTemplateGet toptions = new CTemplateGet();
			toptions.setHostIds(hostids.valuesAsLong());
			toptions.setOutput(new String[]{"templateid"});
			toptions.setPreserveKeys(true);
			CArray hostTemplates =API.Template(this.idBean, getSqlExecutor()).get(toptions);

			CArray hostTemplateids = array_keys(hostTemplates);
			CArray newTemplateids = rda_objectValues(updateTemplates, "templateid");

			CArray templatesToDel = array_diff(hostTemplateids, newTemplateids);
			templatesToDel = array_diff(templatesToDel, templateidsClear);

			if (!empty(templatesToDel)) {
				result = this.massRemove(map(
					"hostids", hostids,
					"templateids", templatesToDel
				));
				if (empty(result)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot unlink template"));
				}
			}

			result = this.massAdd(map(
				"hosts", hosts,
				"templates", updateTemplates
			));
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot link template"));
			}
		}

		// macros
		if (isset(updateMacros)) {
			this.delete("hostmacro", (Map)map("hostid", hostids));

			this.massAdd(map(
				"hosts", hosts,
				"macros", updateMacros
			));
		}

		/*
		 * Inventory
		 */
		if (isset(updateInventory)) {
			if (getInteger(updateInventory, "inventory_mode") == HOST_INVENTORY_DISABLED) {
				boolean success = this.delete("host_inventory", (Map)map("hostid", hostids));
				if (!success) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete inventory."));
				}
			}
			else {
				CArray<String> hostsWithInventories = array();
				SqlBuilder sqlPart = new SqlBuilder();
				sqlPart.select.put("hostid");
				sqlPart.from.put("host_inventory");
				sqlPart.where.dbConditionInt("hostid", hostids.valuesAsLong());
				
				CArray<Map> existingInventoriesDb = dbfetchArrayAssoc(DBselect(getSqlExecutor(), sqlPart), "hostid");

				// check for hosts with disabled inventory mode
				if (updateInventory.get("inventory_mode") == null && count(existingInventoriesDb) != count(hostids)) {
					for(Long hostId: hostids) {
						if (!isset(existingInventoriesDb.get(hostId))) {
							Map host = get_host_by_hostid(this.idBean, getSqlExecutor(),hostId);
							throw CDB.exception(RDA_API_ERROR_PARAMETERS,
								_s("Inventory disabled for host \"%s\".", host.get("host")));
						}
					}
				}
				for(Map existingInventory: existingInventoriesDb) {
					hostsWithInventories.add(getString(existingInventory, "hostid"));
				}

				CArray inventoriesToSave = null;
				// when hosts are being updated to use automatic mode for host inventories,
				// we must check if some items are set to populate inventory fields of every host.
				// if they do, mass update for those fields should be ignored
				if (getInteger(updateInventory, "inventory_mode") == HOST_INVENTORY_AUTOMATIC) {
					// getting all items on all affected hosts
					CItemGet ioptions = new CItemGet();
					ioptions.setOutput(new String[]{"inventory_link", "hostid"});
					ioptions.setFilter("hostid", hostids.valuesAsString());
					ioptions.setNopermissions(true);
					CArray<Map> itemsToInventories = API.Item(this.idBean, getSqlExecutor()).get(ioptions);

					// gathering links to array: 'hostid'=>array('inventory_name_1'=>true, 'inventory_name_2'=>true)
					CArray inventoryLinksOnHosts = array();
					CArray inventoryFields = getHostInventories();
					for(Map hinv: itemsToInventories) {
						if (getInteger(hinv, "inventory_link") != 0) { // 0 means 'no link'
							if (isset(inventoryLinksOnHosts.get(hinv.get("hostid")))) {
								inventoryLinksOnHosts.put(hinv.get("hostid"), inventoryFields.get(hinv.get("inventory_link")), "db_field", true);
							} else {
								inventoryLinksOnHosts.put(hinv.get("hostid"), map(inventoryFields.getNested(hinv.get("inventory_link"), "db_field"), true));
							}
						}
					}

					// now we have all info we need to determine, which inventory fields should be saved
					inventoriesToSave = array();
					for(Long hostid: hostids) {
						inventoriesToSave.put(hostid, updateInventory);
						inventoriesToSave.put(hostid, "hostid", hostid);
						for(String inventoryName : updateInventory.keys()) {
							if (isset(inventoryLinksOnHosts.getNested(hostid, inventoryName))) {
								unset(inventoriesToSave, hostid, inventoryName);
							}
						}
					}
				} else {
					// if mode is not automatic, all fields can be saved
					inventoriesToSave = array();
					for(Object hostid: hostids) {
						inventoriesToSave.put(hostid, updateInventory);
						inventoriesToSave.put(hostid, "hostid", hostid);
					}
				}

				CArray<Long> hostsWithoutInventory = array_diff(hostids, hostsWithInventories);

				// hosts that have no inventory yet, need it to be inserted
				for(Object hostid: hostsWithoutInventory) {
					this.insert("host_inventory", array((Map)inventoriesToSave.get(hostid)), false);
				}

				// those hosts that already have an inventory, need it to be updated
				for(String hostid: hostsWithInventories) {
					this.update("host_inventory", array((Map)map(
						"values", inventoriesToSave.get(hostid),
						"where", map("hostid", hostid)
					)));
				}
			}
		}

		return map("hostids", inputHostIds);
	}

	/**
	 * Additionally allows to remove interfaces from hosts.
	 *
	 * Checks write permissions for hosts.
	 *
	 * Additional supported data parameters are:
	 * - interfaces  - an array of interfaces to delete from the hosts
	 *
	 * @param array data
	 *
	 * @return array
	 */
	public CArray massRemove(CArray data) {
		CArray hostids = rda_toArray(data.get("hostids"));

		// check permissions
		if (!this.isWritable(hostids.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		if (isset(data.get("interfaces"))) {
			CArray options = map(
				"hostids", hostids,
				"interfaces", rda_toArray(data.get("interfaces"))
			);
			API.HostInterface(this.idBean, getSqlExecutor()).massRemove(options);
		}

		// rename the "templates" parameter to the common "templates_link"
		if (isset(data.get("templateids"))) {
			data.put("templateids_link", data.get("templateids"));
			unset(data, "templateids");
		}

		data.put("templateids", array());

		return super.massRemove(data);
	}
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array hostIds
	 * @param bool 	nopermissions
	 *
	 * @return void
	 */
	protected void validateDelete(Long... hostIds) {
		validateDelete(false, hostIds);
	}
	
	protected void validateDelete(boolean nopermissions, Long... hostIds) {
		if (empty(hostIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		if (!nopermissions) {
			checkPermissions(hostIds);
		}
	}
	
	/**
	 * Delete Host
	 *
	 * @param string|array 	hostIds
	 * @param bool			nopermissions
	 *
	 * @return array|boolean
	 */
	@Override
	public CArray<Long[]> delete(Long... hostIds) {
		return delete(false, hostIds);
	}
	
	/**
	 * Delete Host
	 *
	 * @param string|array 	hostIds
	 * @param bool			nopermissions
	 *
	 * @return array|boolean
	 */
	public CArray<Long[]> delete(boolean nopermissions, Long... hostIds) {
		validateDelete(nopermissions, TArray.as(hostIds).asLong());
		
		do_audit_off();
		try {
			// delete the discovery rules first
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setHostIds(hostIds);
			droptions.setNopermissions(true);
			droptions.setPreserveKeys(true);
			CArray<Map> delRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
			if (!empty(delRules)) {
				API.DiscoveryRule(this.idBean, this.getSqlExecutor()).delete(true, array_keys(delRules).valuesAsLong());
			}
	
			// delete the items
			CItemGet ioptions = new CItemGet();
			ioptions.setTemplateIds(TArray.as(hostIds).asLong());
			ioptions.setOutput(new String[]{"itemid"});
			ioptions.setNopermissions(true);
			ioptions.setPreserveKeys(true);
			CArray<Map> delItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);
			if (!empty(delItems)) {
				API.Item(this.idBean, this.getSqlExecutor()).delete(true, array_keys(delItems).valuesAsLong());
			}
	
			// delete web tests
			CArray delHttptests = array();
			CArray<Map> dbHttptests = get_httptests_by_hostid(getSqlExecutor(),TArray.as(hostIds).asLong());
			for(Map dbHttptest : dbHttptests) {
				Nest.value(delHttptests,dbHttptest.get("httptestid")).$(Nest.value(dbHttptest,"httptestid").$());
			}
			if (!empty(delHttptests)) {
				API.HttpTest(this.idBean, this.getSqlExecutor()).delete(true, delHttptests.valuesAsLong());
			}
	
	
			// delete screen items
			delete("screens_items", (Map)map(
				"resourceid", hostIds,
				"resourcetype", SCREEN_RESOURCE_HOST_TRIGGERS
			));
	
			// delete host from maps
			if (!empty(hostIds)) {
				delete("sysmaps_elements", (Map)map(
					"elementtype", SYSMAP_ELEMENT_TYPE_HOST,
					"elementid", hostIds
				));
			}
	
			// disable actions
			// actions from conditions
			CArray actionids = array();
			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT DISTINCT actionid"+
					" FROM conditions"+
					" WHERE conditiontype="+CONDITION_TYPE_HOST+
					" AND "+sqlParts.dual.dbConditionString("value", TArray.as(hostIds).asString());
			CArray<Map> dbActions = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbAction : dbActions) {
				Nest.value(actionids,dbAction.get("actionid")).$(Nest.value(dbAction,"actionid").$());
			}
	
			// actions from operations
			sqlParts = new SqlBuilder();
			sql = "SELECT DISTINCT o.actionid"+
					" FROM operations o, opcommand_hst oh"+
					" WHERE o.tenantid=oh.tenantid"+
					" AND o.operationid=oh.operationid"+
					" AND "+sqlParts.dual.dbConditionInt("oh.hostid", hostIds);
			dbActions = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbAction : dbActions) {
				Nest.value(actionids,dbAction.get("actionid")).$(Nest.value(dbAction,"actionid").$());
			}
	
			if (!empty(actionids)) {
				CArray<Map> update = array();
				update.add(map(
					"values", map("status", ACTION_STATUS_DISABLED),
					"where", map("actionid", actionids.valuesAsLong())
				));
				update("actions", update);
			}
	
			// delete action conditions
			delete("conditions", (CArray)map(
				"conditiontype", CONDITION_TYPE_HOST,
				"value", hostIds
			));
	
			// delete action operation commands
			CArray operationids = array();
			sqlParts = new SqlBuilder();
			sql = "SELECT DISTINCT oh.operationid"+
					" FROM opcommand_hst oh"+
					" WHERE "+sqlParts.dual.dbConditionInt("oh.hostid", hostIds);
			CArray<Map> dbOperations = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbOperation : dbOperations) {
				Nest.value(operationids,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
			}
	
			delete("opcommand_hst", (CArray)map(
				"hostid", hostIds
			));
	
			// delete empty operations
			CArray delOperationids = array();
			sqlParts = new SqlBuilder();
			sql = "SELECT DISTINCT o.operationid"+
					" FROM operations o"+
					" WHERE "+sqlParts.dual.dbConditionInt("o.operationid", operationids.valuesAsLong())+
					" AND NOT EXISTS(SELECT oh.opcommand_hstid FROM opcommand_hst oh WHERE oh.tenantid=o.tenantid"+
					" AND oh.operationid=o.operationid)";
			dbOperations = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
			for(Map dbOperation : dbOperations) {
				Nest.value(delOperationids,dbOperation.get("operationid")).$(Nest.value(dbOperation,"operationid").$());
			}
	
			delete("operations", (CArray)map(
				"operationid", delOperationids.valuesAsLong()
			));
	
			CHostGet hoptions = new CHostGet();
			hoptions.setOutput(new String[]{"hostid", "name"});
			hoptions.setHostIds(TArray.as(hostIds).asLong());
			hoptions.setNopermissions(true);
			CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
	
			// delete host inventory
			delete("host_inventory", (CArray)map("hostid", hostIds));
	
			// delete host applications
			delete("applications", (CArray)map("hostid", hostIds));
	
			// delete host
			delete("hosts", (CArray)map("hostid", hostIds));
			
			do_audit_on();
	
			// TODO: remove info from API
			for(Map host : hosts) {
				info(_s("Deleted: Host \"%1$s\".", Nest.value(host,"name").$()));
				add_audit_ext(this.idBean, getSqlExecutor(), AUDIT_ACTION_DELETE, AUDIT_RESOURCE_HOST, Nest.value(host,"hostid").asLong(), Nest.value(host,"name").asString(), "hosts", null, null);
			}
	
			// remove Monitoring > Latest data toggle profile values related to given hosts
			CProfile.delete(idBean, getSqlExecutor(),"web.latest.toggle_other", hostIds);
	
			return map("hostids", hostIds);
		} finally {
			do_audit_on();
		}
	}

	@Override
	public boolean isReadable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CHostGet options = new CHostGet();
		options.setHostIds(ids);
		options.setTemplatedHosts(true);
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
		CHostGet options = new CHostGet();
		options.setHostIds(ids);
		options.setEditable(true);
		options.setTemplatedHosts(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void addRelatedObjects(CHostGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] hostids = result.keysAsLong();
	
		// adding inventories
		if (!is_null(params.getSelectInventory())) {
			CRelationMap relationMap = createRelationMap(result, "hostid", "hostid");
			
			CParamGet options = new CParamGet();
			options.setOutput(params.getSelectInventory());
			options.setFilter("hostid", hostids);
			CArray<Map> inventory = select("host_inventory", options);
			relationMap.mapOne(result, rda_toHash(inventory, "hostid"), "inventory");
		}
		
		// adding hostinterfaces
		if (!is_null(params.getSelectInterfaces())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectInterfaces())) {
				CHostIfaceGet iparams = new CHostIfaceGet();
				iparams.setOutput(outputExtend("interface", new String[] {"hostid", "interfaceid"}, params.getSelectInterfaces()));
				iparams.setHostIds(hostids);
				iparams.setPreserveKeys(true);
				CArray<Map> interfaces = API.HostInterface(this.idBean, getSqlExecutor()).get(iparams);
				
				// we need to order interfaces for proper linkage and viewing
				order_result(interfaces, "interfaceid", RDA_SORT_UP);
	
				CRelationMap relationMap = createRelationMap(interfaces, "hostid", "interfaceid");
				unsetExtraFields(interfaces, new String[] {"hostid", "interfaceid"}, params.getSelectInterfaces());
				relationMap.mapMany(result, interfaces, "interfaces", params.getLimitSelects());
			} else {
				CHostIfaceGet iparams = new CHostIfaceGet();
				iparams.setHostIds(hostids);
				iparams.setCountOutput(true);
				iparams.setGroupCount(true);
				CArray<Map> interfaces = API.HostInterface(this.idBean, getSqlExecutor()).get(iparams);
				interfaces = rda_toHash(interfaces, "hostid");
				
				for(Entry<Object, Map> entry: result.entrySet()) {
					Object hostid = entry.getKey();
					Map m = entry.getValue();
					Map i = interfaces.get(hostid);
					m.put("interfaces", isset(i)? i.get("rowscount"): 0);
				}
			}
		}
		
		// adding screens
		if (!is_null(params.getSelectScreens())) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectScreens())) {
				CTemplateScreenGet tsoptions = new CTemplateScreenGet();
				tsoptions.setOutput(outputExtend("screens", new String[]{"hostid"}, params.getSelectScreens()));
				tsoptions.setHostIds(hostids);
				tsoptions.setNopermissions(true);
				CArray<Map> screens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(tsoptions);
				if (!is_null(params.getLimitSelects())) {
					order_result(screens, "name");
				}
	
				// inherited screens do not have a unique screenid, so we're building a map using array keys
				CRelationMap relationMap = new CRelationMap();
				for (Entry<Object, Map> e : screens.entrySet()) {
				    Object key = e.getKey();
				    Map screen = e.getValue();
					relationMap.addRelation(screen.get("hostid"), key);
				}
	
				unsetExtraFields(screens, new String[]{"hostid"}, params.getSelectScreens());
				relationMap.mapMany(result, screens, "screens", params.getLimitSelects());
			} else {
				CTemplateScreenGet tsoptions = new CTemplateScreenGet();
				tsoptions.setHostIds(hostids);
				tsoptions.setNopermissions(true);
				tsoptions.setCountOutput(true);
				tsoptions.setGroupCount(true);
				CArray<Map> screens = API.TemplateScreen(this.idBean, this.getSqlExecutor()).get(tsoptions);
				screens = rda_toHash(screens, "hostid");
	
				for (Object hostid : result.keySet()) {
					Nest.value(result,hostid,"screens").$(isset(screens,hostid) ? Nest.value(screens,hostid,"rowscount").asInteger() : 0);
				}
			}
		}
	
		// adding discovery rule
		if (!is_null(params.getSelectDiscoveryRule()) && !API_OUTPUT_COUNT.equals(params.getSelectDiscoveryRule())) {
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("hd.hostid");
			sqlParts.select.put("hd2.parent_itemid");
			sqlParts.from.put("host_discovery hd");
			sqlParts.from.put("host_discovery hd2");
			applyQueryTenantOptions("host_discovery", "hd", params, sqlParts);
			sqlParts.where.dbConditionInt("hd.hostid", hostids);
			sqlParts.where.put("hd.tenantid=hd2.tenantid");
			sqlParts.where.put("hd.parent_hostid=hd2.hostid");
			
			String sql = createSelectQueryFromParts(sqlParts);
			Map paraMap = sqlParts.getNamedParams();
			List<Map> datas = getSqlExecutor().executeNameParaQuery(sql, paraMap);
			CRelationMap relationMap = createRelationMap(datas, "hostid", "hostid", "parent_itemid");
			
			CDiscoveryRuleGet iparams = new CDiscoveryRuleGet();
			iparams.setOutput(params.getSelectDiscoveryRule());
			iparams.setItemIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapOne(result, discoveryRules, "discoveryRule");
		}
	
		// adding host discovery
		if (!is_null(params.getSelectHostDiscovery())) {
			CParamGet cparams = new CParamGet();
			cparams.setOutput(outputExtend("host_discovery", new String[] {"hostid"}, params.getSelectHostDiscovery()));
			cparams.setFilter("hostid", hostids);
			cparams.setPreserveKeys(true);
			
			CArray<Map> hostDiscoveries = select("host_discovery", cparams);
			CRelationMap relationMap = createRelationMap(hostDiscoveries, "hostid", "hostid");
			unsetExtraFields(hostDiscoveries, new String[] {"hostid"}, params.getSelectHostDiscovery());
			relationMap.mapMany(result, hostDiscoveries, "hostDiscovery");
		}
	}

	/**
	 * Checks if all of the given hosts are available for writing.
	 *
	 * @throws APIException     if a host is not writable or does not exist
	 *
	 * @param array hostIds
	 */
	protected void checkPermissions(Long... hostIds) {
		if (!isWritable(hostIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
}
