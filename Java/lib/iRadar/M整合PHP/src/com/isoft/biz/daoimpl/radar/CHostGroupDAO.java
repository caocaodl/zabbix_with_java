package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.AuditUtil.do_audit_off;
import static com.isoft.iradar.inc.AuditUtil.do_audit_on;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_HOST_GROUP;
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
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_INTERNAL_GROUP;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_DATA_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTGROUP_TRIGGERS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HOSTS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_INFO;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_TRIGGERS_OVERVIEW;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_HOST_GROUP;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.getDeletableHostGroups;
import static com.isoft.iradar.inc.HostsUtil.getUnlinkableHosts;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CScriptGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.host.CHostNormalValidator;
import com.isoft.iradar.validators.object.CUpdateDiscoveredValidator;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with host groups.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CHostGroupDAO extends CCoreLongKeyDAO<CHostGroupGet> {

	public CHostGroupDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "groups", "g", new String[]{"groupid", "name"});
	}

	@Override
	public <T> T get(CHostGroupGet params) {
		int userType = CWebUser.getType();
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("groups", "g.groupid");
		sqlParts.from.put("groups", "groups g");
		
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;
			Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);

			sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM rights r"+
				" WHERE g.tenantid=r.tenantid"+
					" AND g.groupid=r.id"+
					" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
				" GROUP BY r.id"+
				" HAVING MIN(r.permission)>"+PERM_DENY+
					" AND MAX(r.permission)>="+permission+
				")");
		}
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.where.dbConditionInt("groupid", "g.groupid", params.getGroupIds());
		}
				
		// templateids
		if (!is_null(params.getTemplateIds())) {
			if (!is_null(params.getHostIds())) {
				params.setHostIds(array_merge(params.getHostIds(), params.getTemplateIds()));
			} else {
				params.setHostIds(params.getTemplateIds());
			}
		}
		
		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("hostid", "hg.hostid");
			sqlParts.from.put("hosts_groups", "hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.hostid", params.getHostIds());
			sqlParts.where.put("hgg.tenantid", "hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg", "hg.groupid=g.groupid");
		}

		// triggerids
		if (!is_null(params.getTriggerIds())) {			
			sqlParts.select.put("triggerid", "f.triggerid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("f.triggerid", params.getTriggerIds());
			sqlParts.where.put("fi.tenantid", "f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
			sqlParts.where.put("hgi.tenantid", "hg.tenantid=i.tenantid");
			sqlParts.where.put("hgi","hg.hostid=i.hostid");
			sqlParts.where.put("hgg.tenantid", "hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg","hg.groupid=g.groupid");
		}
		
		// graphids
		if (!is_null(params.getGraphIds())) {
			sqlParts.select.put("graphid", "gi.graphid");
			sqlParts.from.put("gi","graphs_items gi");
			sqlParts.from.put("i","items i");
			sqlParts.from.put("hg","hosts_groups hg");
			sqlParts.where.dbConditionInt("gi.graphid", params.getGraphIds());
			sqlParts.where.put("hgg.tenantid", "hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg","hg.groupid=g.groupid");
			sqlParts.where.put("igi.tenantid", "i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
			sqlParts.where.put("hgi.tenantid", "hg.tenantid=i.tenantid");
			sqlParts.where.put("hgi","hg.hostid=i.hostid");
		}
		
		// maintenanceids
		if (!is_null(params.getMaintenanceIds())) {
			sqlParts.select.put("maintenanceid", "mg.maintenanceid");
			sqlParts.from.put("maintenances_groups","maintenances_groups mg");
			sqlParts.where.dbConditionInt("mg.maintenanceid", params.getMaintenanceIds());
			sqlParts.where.put("hmh.tenantid", "g.tenantid=mg.tenantid");
			sqlParts.where.put("hmh","g.groupid=mg.groupid");
		}
		
		// monitored_hosts, real_hosts, templated_hosts, not_proxy_hosts, with_hosts_and_templates
		if (!is_null(params.getMonitoredHosts())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM hosts h"+
					" WHERE hg.tenantid=h.tenantid"+
					" AND hg.hostid=h.hostid"+
					" AND h.status="+HOST_STATUS_MONITORED+
					")");
		} else if (!is_null(params.getRealHosts())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hgg.tenantid","hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg","hg.groupid=g.groupid");
			sqlParts.where.put("h.tenantid=hg.tenantid");
			sqlParts.where.put("h.hostid=hg.hostid");
			sqlParts.where.put("h.status IN("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+")");
		} else if (!is_null(params.getTemplatedHosts())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hgg.tenantid","hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg","hg.groupid=g.groupid");
			sqlParts.where.put("h.tenantid=hg.tenantid");
			sqlParts.where.put("h.hostid=hg.hostid");
			sqlParts.where.put("h.status="+HOST_STATUS_TEMPLATE);
		} else if (!is_null(params.getNotProxyHosts())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hgg.tenantid","hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg","hg.groupid=g.groupid");
			sqlParts.where.put("h.tenantid=hg.tenantid");
			sqlParts.where.put("h.hostid=hg.hostid");
			sqlParts.where.put("h.status NOT IN ("+HOST_STATUS_PROXY_ACTIVE+","+HOST_STATUS_PROXY_PASSIVE+")");
		} else if (!is_null(params.getWithHostsAndTemplates())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hgg.tenantid","hg.tenantid=g.tenantid");
			sqlParts.where.put("hgg","hg.groupid=g.groupid");
			sqlParts.where.put("h.tenantid=hg.tenantid");
			sqlParts.where.put("h.hostid=hg.hostid");
			sqlParts.where.put("h.status IN ("+HOST_STATUS_MONITORED+","+HOST_STATUS_NOT_MONITORED+","+HOST_STATUS_TEMPLATE+")");
		}
		
		// with_items, with_monitored_items, with_historical_items, with_simple_graph_items
		if (!is_null(params.getWithItems())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i"+
					" WHERE hg.tenantid=i.tenantid"+
					" AND hg.hostid=i.hostid"+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		} else if (!is_null(params.getWithMonitoredItems())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i,hosts h"+
					" WHERE hg.tenantid=i.tenantid"+
					" AND i.tenantid=h.tenantid"+
					" AND hg.hostid=i.hostid"+
					" AND i.hostid=h.hostid"+
					" AND h.status="+HOST_STATUS_MONITORED+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		} else if (!is_null(params.getWithSimpleGraphItems())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i"+
					" WHERE hg.tenantid=i.tenantid"+
					" AND hg.hostid=i.hostid"+
					" AND i.value_type IN ("+ITEM_VALUE_TYPE_FLOAT+","+ITEM_VALUE_TYPE_UINT64+")"+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}
		
		// with_triggers, with_monitored_triggers
		if (!is_null(params.getWithTriggers())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i,functions f,triggers t"+
					" WHERE hg.tenantid=i.tenantid"+
					" AND i.tenantid=f.tenantid"+
					" AND f.tenantid=t.tenantid"+
					" AND hg.hostid=i.hostid"+
					" AND i.itemid=f.itemid"+
					" AND f.triggerid=t.triggerid"+
					" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		} else if (!is_null(params.getWithMonitoredTriggers())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i,hosts h,functions f,triggers t"+
					" WHERE hg.tenantid=i.tenantid"+
					" AND i.tenantid=h.tenantid"+
					" AND i.tenantid=f.tenantid"+
					" AND f.tenantid=t.tenantid"+
					" AND hg.hostid=i.hostid"+
					" AND i.hostid=h.hostid"+
					" AND i.itemid=f.itemid"+
					" AND f.triggerid=t.triggerid"+
					" AND h.status="+HOST_STATUS_MONITORED+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND t.status="+TRIGGER_STATUS_ENABLED+
					" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}

		// with_httptests, with_monitored_httptests
		if (!is_null(params.getWithHttpTests())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM httptest ht"+
					" WHERE hg.tenantid=ht.tenantid"+
					" AND hg.hostid=ht.hostid"+
					")");
		} else if (!is_null(params.getWithMonitoredHttpTests())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM httptest ht"+
					" WHERE hg.tenantid=ht.tenantid"+
					" AND hg.hostid=ht.hostid"+
					" AND ht.status="+HTTPTEST_STATUS_ACTIVE+
					")");
		}

		// with_graphs
		if (!is_null(params.getWithGraphs())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM items i,graphs_items gi,graphs g"+
					" WHERE hg.tenantid=i.tenantid"+
					" AND i.tenantid=gi.tenantid"+
					" AND gi.tenantid=g.tenantid"+
					" AND hg.hostid=i.hostid"+
					" AND i.itemid=gi.itemid"+
					" AND gi.graphid=g.graphid"+
					" AND g.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
					")");
		}

		if (!is_null(params.getWithApplications())) {
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("applications","applications a");
			sqlParts.where.put("hgg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("hgg","g.groupid=hg.groupid");
			sqlParts.where.put("hg.tenantid=a.tenantid");
			sqlParts.where.put("hg.hostid=a.hostid");
		}
		
		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("groups g", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("groups g", params, sqlParts);
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
				Long id = (Long)row.get("groupid");
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
				// graphids
				if (isset(row.get("graphid"))) {
					if (!isset(resultRow.get("graphs"))) {
						resultRow.put("graphs", new CArray());
					}
					((CArray)resultRow.get("graphs")).add(map("graphid", row.remove("graphid")));
				}
				// maintenanceids
				if (isset(row.get("maintenanceid"))) {
					if (!isset(resultRow.get("maintenances"))) {
						resultRow.put("maintenances", new CArray());
					}
					((CArray)resultRow.get("maintenances")).add(map("maintenanceid", row.remove("maintenanceid")));
				}
				// triggerids
				if (isset(row.get("triggerid"))) {
					if (!isset(resultRow.get("triggers"))) {
						resultRow.put("triggers", new CArray());
					}
					((CArray)resultRow.get("triggers")).add(map("triggerid", row.remove("triggerid")));
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
		if (empty(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	@Override
	public CArray<Map> getObjects(Map<String,Object[]> filter) {
		CHostGroupGet params = new CHostGroupGet();
		params.setFilter(filter);
		params.setOutput(API_OUTPUT_EXTEND);
		return get(params);
	}

	@Override
	public boolean exists(CArray object) {
		CArray<String> keyFields = array("name", "groupid");

		CHostGroupGet options = new CHostGroupGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[] {"groupid"});
		options.setNopermissions(true);
		options.setLimit(1);
		
		CArray<Map> objs = this.get(options);
		return !empty(objs);
	}
	
	/**
	 * Create host groups.
	 *
	 * @param array groups array with host group names
	 * @param array groups["name"]
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> groups) {
		if (USER_TYPE_SUPER_ADMIN != CWebUser.getType()) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only Super Admins can create host groups."));
		}

		for(Map group: groups) {
			if (!isset(group,"name") || rda_empty(Nest.value(group,"name").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Host group name cannot be empty."));
			}
			if (this.exists(map("name", Nest.value(group,"name").$()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host group \"%1$s\" already exists.", Nest.value(group,"name").$()));
			}
			
			checkNoParameters(
				group,
				new String[]{"internal"},
				_("Cannot set \"%1$s\" for host group \"%2$s\"."),
				Nest.value(group,"name").asString()
			);
		}
		CArray<Long> groupids = this.insert("groups", groups);
		return map("groupids", groupids.valuesAsLong());
	}
	
	/**
	 * Update host groups.
	 *
	 * @param array groups
	 * @param array groups[0]['name'], ...
	 * @param array groups[0]['groupid'], ...
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> groups) {
		CArray groupids = rda_objectValues(groups, "groupid");

		if (empty(groups)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		// permissions
		CHostGroupGet params = new CHostGroupGet();
		params.setOutput(new String[] {"groupid", "flags", "name"});
		params.setGroupIds(groupids.valuesAsLong());
		params.setEditable(true);		
		params.setPreserveKeys(true);		
		
		CArray<Map> updGroups = get(params);
		for(Map group: groups) {
			if (!isset(updGroups,group.get("groupid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
			checkNoParameters(
				group,
				new String[]{"internal"},
				_("Cannot update \"%1$s\" for host group \"%2$s\"."),
				isset(group,"name") ? Nest.value(group,"name").asString() : Nest.value(updGroups,group.get("groupid"),"name").asString()
			);
		}

		// name duplicate check
		params = new CHostGroupGet();
		params.setFilter("name", rda_objectValues(groups, "name").valuesAsString());
		params.setOutput(new String[] {"groupid", "name"});
		params.setEditable(true);
		params.setNopermissions(true);
		CArray<Map> groupsNames = get(params);
		groupsNames = rda_toHash(groupsNames, "name");

		CUpdateDiscoveredValidator updateDiscoveredValidator = CValidator.init(new CUpdateDiscoveredValidator(),map(
			"messageAllowed", _("Cannot update a discovered host group.")
		));

		CArray<Map> update = array();
		for(Map group: groups) {
			if (isset(group,"name")) {
				if (rda_empty(group.get("name"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Host group name cannot be empty."));
				}

				// cannot update discovered host groups
				this.checkPartialValidator(group, updateDiscoveredValidator, updGroups.get(group.get("groupid")));
				
				String groupName = Nest.value(group,"name").asString();
				if (isset(groupsNames,groupName)
						&& !idcmp(groupsNames.getNested(groupName, "groupid"), group.get("groupid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host group \"%1$s\" already exists.", groupName));
				}

				update.add(map(
					"values", map("name", group.get("name")),
					"where", map("groupid", group.get("groupid"))
				));
			}

			// prevents updating several groups with same name
			groupsNames.put(group.get("name"), map("groupid", group.get("groupid")));
		}

		this.update("groups", update);

		return map("groupids", groupids.valuesAsLong());
	}
	
	/**
	 * Delete host groups.
	 *
	 * @param array groupids
	 * @param bool 	nopermissions
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> delete(Long... ids) {
		return this.delete(false, ids);
	}

	public CArray<Long[]> delete(boolean nopermissions, Long... ids) {
		if (empty(ids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}
		
		Arrays.sort(ids);
		
		do_audit_off();
		try {		
			CHostGroupGet params = new CHostGroupGet();
			params.setGroupIds(TArray.as(ids).asLong());
			params.setEditable(true);
			params.setOutput(new String[] {"groupid", "name", "internal"});
			params.setPreserveKeys(true);
			params.setNopermissions(true);
			
			CArray<Map> delGroups = this.get(params);
			
			for(Long groupid: ids) {
				if (!isset(delGroups,groupid)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
				if (Nest.value(delGroups,groupid, "internal").asInteger() == RDA_INTERNAL_GROUP) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Host group \"%1$s\" is internal and can not be deleted.", Nest.value(delGroups,groupid, "name").asString()));
				}
			}
	
			// check if a group is used in a group prototype
			SqlBuilder sqlParts = new SqlBuilder();
			Map groupPrototype = DBfetch(DBselect(getSqlExecutor(),
				"SELECT groupid"+
				" FROM group_prototype gp"+
				" WHERE "+sqlParts.dual.dbConditionInt("groupid", ids),
				1,
				sqlParts.getNamedParams()
			));
			
			if (groupPrototype != null) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Group \"%1$s\" cannot be deleted, because it is used by a host prototype.",
						Nest.value(delGroups,groupPrototype.get("groupid"), "name").asString()
					)
				);
			}
	
			CArray<Long> dltGroupids = getDeletableHostGroups(this.idBean, getSqlExecutor(), ids);
			if (count(ids) != count(dltGroupids)) {
				for (Long groupid: ids) {
					if (isset(dltGroupids,groupid)) {
						continue;
					}
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Host group \"%1$s\" cannot be deleted, because some hosts depend on it.",
							Nest.value(delGroups,groupid, "name").asString()
						)
					);
				}
			}
	
			CScriptGet sget = new CScriptGet();
			sget.setGroupIds(TArray.as(ids).asLong());
			sget.setOutput(new String[] {"scriptid", "groupid"});
			sget.setNopermissions(true);
			
			CArray<Map> dbScripts = API.Script(this.idBean, this.getSqlExecutor()).get(sget);
			if (!empty(dbScripts)) {
				for(Map script: dbScripts) {
					Object groupid = script.get("groupid");
					if (empty(groupid)) {
						continue;
					}
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Host group \"%1$s\" cannot be deleted, because it is used in a global script.",
							Nest.value(delGroups,groupid, "name").asString()
						)
					);
				}
			}
	
			// delete screens items
			int[] resources = {
				SCREEN_RESOURCE_HOSTGROUP_TRIGGERS,
				SCREEN_RESOURCE_HOSTS_INFO,
				SCREEN_RESOURCE_TRIGGERS_INFO,
				SCREEN_RESOURCE_TRIGGERS_OVERVIEW,
				SCREEN_RESOURCE_DATA_OVERVIEW
			};
			this.delete("screens_items", (Map)map(
				"resourceid", ids,
				"resourcetype", resources
			));
	
			// delete sysmap element
			if (!empty(ids)) {
				this.delete("sysmaps_elements", 
					(Map)map("elementtype", SYSMAP_ELEMENT_TYPE_HOST_GROUP, "elementid", ids)
				);
			}
	
			// disable actions
			// actions from conditions
			CArray actionids = array();
			
			sqlParts = new SqlBuilder();
			CArray<Map> dbActions = DBselect(getSqlExecutor(),
				"SELECT DISTINCT c.actionid"+
				" FROM conditions c"+
				" WHERE c.conditiontype="+CONDITION_TYPE_HOST_GROUP+
					" AND "+sqlParts.dual.dbConditionInt("c.value", ids),
				sqlParts.getNamedParams()
			);
			for(Map dbAction: dbActions) {
				actionids.put(dbAction.get("actionid"), dbAction.get("actionid"));
			}
	
			if (!empty(actionids)) {
				CArray<Map> update = array();
				update.add(map(
					"values", map("status", ACTION_STATUS_DISABLED),
					"where", map("actionid", actionids.valuesAsLong())
				));
				this.update("actions", update);
			}
	
			// delete action conditions
			this.delete("conditions", (Map)map(
				"conditiontype", CONDITION_TYPE_HOST_GROUP,
				"value", ids
			));
	
			// delete action operation commands
			CArray<String> operationids = array();
			
			sqlParts = new SqlBuilder();
			CArray<Map> dbOperations = DBselect(getSqlExecutor(),
				"SELECT DISTINCT og.operationid"+
				" FROM opgroup og"+
				" WHERE "+sqlParts.dual.dbConditionInt("og.groupid", ids),
				sqlParts.getNamedParams()
			);
			
			for(Map dbOperation: dbOperations) {
				operationids.put(dbOperation.get("actionid"), dbOperation.get("actionid"));
			}
			this.delete("opgroup", (Map)map("groupid", ids));
	
			// delete empty operations
			CArray delOperationids = array();
			
			sqlParts = new SqlBuilder();
			dbOperations = DBselect(getSqlExecutor(),
				"SELECT DISTINCT o.operationid"+
				" FROM operations o"+
				" WHERE "+sqlParts.dual.dbConditionInt("o.operationid", operationids.valuesAsLong())+
					" AND NOT EXISTS (SELECT NULL FROM opgroup og WHERE o.tenantid=og.tenantid"+
					" AND o.operationid=og.operationid)",
				sqlParts.getNamedParams()
			);
			
			for(Map dbOperation: dbOperations) {
				delOperationids.put(dbOperation.get("operationid"), dbOperation.get("operationid"));
			}
	
			this.delete("operations", (Map)map("operationid", delOperationids.valuesAsLong()));
	
			this.delete("groups", (Map)map("groupid", ids));
	
			this.delete("profiles", (Map)map(
				"idx", "web.dashconf.groups.groupids",
				"value_id", ids
			));
	
			this.delete("profiles", (Map)map(
				"idx", "web.dashconf.groups.hide.groupids",
				"value_id", ids
			));
			
			do_audit_on();
	
			//TODO: remove audit
			for(Long groupid: ids) {
				add_audit_ext(this.idBean, getSqlExecutor(), AUDIT_ACTION_DELETE, AUDIT_RESOURCE_HOST_GROUP, groupid, Nest.value(delGroups,groupid, "name").asString(), "groups", null, null);
			}
	
			return map("groupids", ids);
		} finally {
			do_audit_on();
		}
	}

	/**
	 * Add hosts to host groups. All hosts are added to all host groups.
	 *
	 * @param array data
	 * @param array data['groups']
	 * @param array data['hosts']
	 * @param array data['templates']
	 *
	 * @return boolean
	 */
	public CArray<Long[]> massAdd(CArray data) {
		CArray<Map> groups = rda_toArray(data.get("groups"));
		CArray<String> groupids = rda_objectValues(groups, "groupid");

		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setGroupIds(groupids.valuesAsLong());
		hgoptions.setEditable(true);
		hgoptions.setPreserveKeys(true);
		CArray<Map> updGroups = this.get(hgoptions);
		for(Map group: groups) {
			if (!isset(updGroups.get(group.get("groupid")))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		CArray hosts = isset(data.get("hosts")) ? rda_toArray(data.get("hosts")) : null;
		CArray hostids = is_null(hosts) ? array() : rda_objectValues(hosts, "hostid");
		CArray templates = isset(data.get("templates")) ? rda_toArray(data.get("templates")) : null;
		CArray templateids = is_null(templates) ? array() : rda_objectValues(templates, "templateid");
		CArray<String> objectids = array_merge(hostids, templateids);

		// check if any of the hosts are discovered
		checkValidator(hostids.valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()),map(
			"message", _("Cannot update groups for discovered host \"%1$s\".")
		)));

		CArray linked = array();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("hg.hostid,hg.groupid");
		sqlParts.from.put("hosts_groups hg");
		sqlParts.where.dbConditionInt("hg.hostid", objectids.valuesAsLong());
		sqlParts.where.dbConditionInt("hg.groupid", groupids.valuesAsLong());
		CArray<Map> linkedDb = DBselect(getSqlExecutor(), sqlParts);
		for(Map pair: linkedDb) {
			linked.put(pair.get("groupid"), pair.get("hostid"), 1);
		}
		
		CArray insert = array();
		for(Object groupid: groupids) {
			for(Object hostid: objectids) {
				if (isset(linked.getNested(groupid, hostid))) {
					continue;
				}
				insert.add(map("hostid", hostid, "groupid", groupid));
			}
		}
		this.insert("hosts_groups", insert);
		return map("groupids", groupids.valuesAsLong());
	}
	
	/**
	 * Remove hosts from host groups.
	 *
	 * @param array data
	 * @param array data["groupids"]
	 * @param array data["hostids"]
	 * @param array data["templateids"]
	 * @return 
	 *
	 * @return boolean
	 */
	public CArray<Long[]> massRemove(CArray<Object> data) {
		CArray groupids = rda_objectValues(Nest.value(data,"groupids").$(), "groupid");

		CHostGroupGet options = new CHostGroupGet();
		options.setGroupIds(groupids.valuesAsLong());
		options.setEditable(true);
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"groupid"});
		CArray<Map> updGroups = get(options);
		for(Object groupid : groupids) {
			if (!isset(updGroups,groupid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS,
					_("No permissions to referred object or it does not exist!")
				);
			}
		}
		CArray hostids = isset(data,"hostids") ? rda_toArray(Nest.value(data,"hostids").$()) : array();
		CArray templateids = isset(data,"templateids") ? rda_toArray(Nest.value(data,"templateids").$()) : array();

		// check if any of the hosts are discovered
		checkValidator(hostids.valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()),map(
			"message", _("Cannot update groups for discovered host \"%1$s\".")
		)));

		CArray objectidsToUnlink = array_merge(hostids, templateids);
		if (!empty(objectidsToUnlink)) {
			CArray<Long> unlinkable = getUnlinkableHosts(this.idBean, getSqlExecutor(),groupids.valuesAsLong(), objectidsToUnlink.valuesAsLong());
			if (count(objectidsToUnlink) != count(unlinkable)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("One of the objects is left without a host group."));
			}

			delete("hosts_groups", (Map)map(
				"hostid", objectidsToUnlink.valuesAsLong(),
				"groupid", groupids.valuesAsLong()
			));
		}
		return map("groupids", groupids.valuesAsLong());
	}

	/**
	 * Update host groups with new hosts (rewrite).
	 *
	 * @param array data
	 * @param array data['groups']
	 * @param array data['hosts']
	 * @param array data['templates']
	 *
	 * @return array
	 */
	public CArray<Long[]> massUpdate(CArray data) {
		CArray<Long> groupIds = array_unique(rda_objectValues(rda_toArray(data.get("groups")), "groupid"));
		CArray<Long> hostIds = array_unique(rda_objectValues(isset(data.get("hosts")) ? rda_toArray(data.get("hosts")) : null, "hostid"));
		CArray<Long> templateIds = array_unique(rda_objectValues(isset(data.get("templates")) ? rda_toArray(data.get("templates")) : null, "templateid"));

		CArray<Long> workHostIds = array();

		// validate permission
		CHostGroupGet hostGroupGetparams = new CHostGroupGet();
		hostGroupGetparams.setGroupIds(groupIds.valuesAsLong());
		hostGroupGetparams.setEditable(true);
		hostGroupGetparams.setPreserveKeys(true);
		CArray allowedGroups = this.get(hostGroupGetparams);
		for(Object groupId: groupIds) {
			if (!isset(allowedGroups.get(groupId))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		// validate allowed hosts
		if (!empty(hostIds)) {
			if (!API.Host(this.idBean, getSqlExecutor()).isWritable(hostIds.valuesAsLong())) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, ("No permissions to referred object or it does not exist!"));
			}

			// check if any of the hosts are discovered
			checkValidator(hostIds.valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()),map(
				"message", _("Cannot update groups for discovered host \"%1$s\".")
			)));

			workHostIds = rda_toHash(hostIds);
		}

		// validate allowed templates
		if (!empty(templateIds)) {
			CTemplateGet templdateGetParams = new CTemplateGet();
			templdateGetParams.setTemplateIds(templateIds.valuesAsLong());
			templdateGetParams.setEditable(true);
			templdateGetParams.setPreserveKeys(true);
			CArray allowedTemplates = API.Template(this.idBean, getSqlExecutor()).get(templdateGetParams);
			for(Object templateId: templateIds) {
				if (!isset(allowedTemplates.get(templateId))) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
				workHostIds.put(templateId, templateId);
			}
		}

		// get old records
		// skip discovered hosts
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("*");
		sqlParts.from.put("hosts_groups hg");
		sqlParts.from.put("hosts h");
		sqlParts.where.dbConditionInt("hg.groupid", groupIds.valuesAsLong());
		sqlParts.where.put("hg.tenantid=h.tenantid");
		sqlParts.where.put("hg.hostid=h.hostid");
		sqlParts.where.put("h.flags="+RDA_FLAG_DISCOVERY_NORMAL);
		CArray<Map> oldRecords = DBselect(getSqlExecutor(), sqlParts);

		// calculate new records
		CArray<Map> replaceRecords = array();
		CArray newRecords = array();
		CArray hostIdsToValidate = array();

		for (Object groupId: groupIds) {
			CArray<Map> groupRecords = array();
			for(Map oldRecord : oldRecords) {
				if (Cphp.equals(oldRecord.get("groupid"), groupId)) {
					groupRecords.add(oldRecord);
				}
			}

			// find records for replace
			for(Map groupRecord: groupRecords) {
				if (isset(workHostIds.get(groupRecord.get("hostid")))) {
					replaceRecords.add(groupRecord);
				}
			}

			// find records for create
			CArray groupHostIds = rda_toHash(rda_objectValues(groupRecords, "hostid"));
			CArray<Long> newHostIds = array_diff(workHostIds, groupHostIds);
			if (isset(newHostIds)) {
				for (Object newHostId: newHostIds) {
					newRecords.add(map(
						"groupid", groupId,
						"hostid", newHostId
					));
				}
			}

			// find records for delete
			CArray<String> deleteHostIds = array_diff(groupHostIds, workHostIds);
			if (isset(deleteHostIds)) {
				for(Object deleteHostId: deleteHostIds) {
					hostIdsToValidate.put(deleteHostId, deleteHostId);
				}
			}
		}

		// validate hosts without groups
		if (!empty(hostIdsToValidate)) {
			CArray unlinkable = getUnlinkableHosts(this.idBean, getSqlExecutor(), groupIds.valuesAsLong(), hostIdsToValidate.valuesAsLong());
			if (count(unlinkable) != count(hostIdsToValidate)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("One of the objects is left without a host group."));
			}
		}

		// save
		this.replace("hosts_groups", oldRecords, array_merge(replaceRecords, newRecords));

		return map("groupids", groupIds.valuesAsLong());
	}
	
	/**
	 * Check if user has read permissions for host groups.
	 *
	 * @param array ids
	 *
	 * @return bool
	 */
	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		
		CHostGroupGet params = new CHostGroupGet();
		params.setGroupIds(ids);
		params.setCountOutput(true);
		
		long count = get(params);
		return ids.length == count;
	}

	
	/**
	 * Check if user has write permissions for host groups.
	 *
	 * @param array ids
	 *
	 * @return bool
	 */
	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}

		CHostGroupGet params = this.getParamInstance();
		params.setGroupIds(ids);
		params.setCountOutput(true);
		params.setEditable(true);
		
		long count = get(params);
		return ids.length == count;
	}

	@Override
	protected void addRelatedObjects(CHostGroupGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] groupIds = array_keys(result).sort().valuesAsLong();
		
		// adding hosts
		if (!is_null(params.getSelectHosts())) {
			CHostGet hostParams = new CHostGet();
			if(!API_OUTPUT_COUNT.equals(params.getSelectHosts())){
				CRelationMap relationMap = createRelationMap(result, "groupid", "hostid", "hosts_groups");
				hostParams.setOutput(params.getSelectHosts());
				hostParams.setHostIds(relationMap.getRelatedLongIds());
				hostParams.setPreserveKeys(true);
				
				CArray<Map> hosts = API.Host(this.idBean, getSqlExecutor()).get(hostParams);
				if(!is_null(params.getLimitSelects())) {
					order_result(hosts, "host");
				}
				relationMap.mapMany(result, hosts, "hosts", params.getLimitSelects());
			} else {
				hostParams.setGroupIds(groupIds);
				hostParams.setCountOutput(true);
				hostParams.setGroupCount(true);
				
				CArray<Map> datas = API.Host(this.idBean, getSqlExecutor()).get(hostParams);
				Map hosts = rda_toHash(datas, "groupid");
				for(Entry<Object, Map> e:result.entrySet()){
					Object groupid = e.getKey();
					Map group = e.getValue();
					if(hosts.containsKey(groupid)){
						group.put("hosts", ((Map)hosts.get(groupid)).get("rowscount"));
					} else {
						group.put("hosts", 0);
					}
				}
			}
		}
		
		// adding templates
		if (!is_null(params.getSelectTemplates())) {
			CTemplateGet templateParams = new CTemplateGet();
			if(!API_OUTPUT_COUNT.equals(params.getSelectTemplates())){
				CRelationMap relationMap = createRelationMap(result, "groupid", "hostid", "hosts_groups");
				templateParams.setOutput(params.getSelectTemplates());
				templateParams.setTemplateIds(relationMap.getRelatedLongIds());
				templateParams.setPreserveKeys(true);
				
				if(!is_null(params.getLimitSelects())) {
					templateParams.setSortfield(new String[]{"host"});
				}
				
				CArray<Map> datas = API.Template(this.idBean, getSqlExecutor()).get(templateParams);
				relationMap.mapMany(result, datas, "templates", params.getLimitSelects());
			} else {
				templateParams.setGroupIds(groupIds);
				templateParams.setCountOutput(true);
				templateParams.setGroupCount(true);
				
				CArray<Map> datas = API.Template(this.idBean, getSqlExecutor()).get(templateParams);
				Map hosts = rda_toHash(datas, "groupid");
				for(Entry<Object, Map> e:result.entrySet()){
					Map host = (Map)hosts.get(e.getKey());
					Map group = e.getValue();
					if(isset(host)){
						group.put("templates", host.get("rowscount"));
					} else {
						group.put("templates", 0);
					}
				}
			}
		}
		
		// adding discovery rule
		if (!is_null(params.getSelectDiscoveryRule()) && !API_OUTPUT_COUNT.equals(params.getSelectDiscoveryRule())){
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("gd.groupid");
			sqlParts.select.put("hd.parent_itemid");
			sqlParts.from.put("group_discovery gd");
			sqlParts.from.put("group_prototype gp");
			sqlParts.from.put("host_discovery hd");
			applyQueryTenantOptions("group_discovery", "gd", params, sqlParts);
			sqlParts.where.dbConditionInt("gd.groupid", groupIds);
			sqlParts.where.put("gd.tenantid=gp.tenantid");
			sqlParts.where.put("gd.parent_group_prototypeid=gp.group_prototypeid");
			sqlParts.where.put("gp.tenantid=hd.tenantid");
			sqlParts.where.put("gp.hostid=hd.hostid");
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "groupid", "parent_itemid");
			
			CDiscoveryRuleGet iparams = new CDiscoveryRuleGet();
			iparams.setOutput(params.getSelectDiscoveryRule());
			iparams.setItemIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray mdatas = (CArray)API.DiscoveryRule(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapOne(result, mdatas, "discoveryRule");
		}
	
		// adding group discovery
		if (!is_null(params.getSelectGroupDiscovery())){
			CParamGet cparams = new CParamGet();
			cparams.setOutput(this.outputExtend("group_discovery", new String[]{"groupid"}, params.getSelectGroupDiscovery()));
			cparams.setFilter("groupid", groupIds);
			cparams.setPreserveKeys(true);
			
			CArray<Map> groupDiscoveries = this.select("group_discovery", cparams);
			CRelationMap relationMap = this.createRelationMap(groupDiscoveries, "groupid", "groupid");
			this.unsetExtraFields(groupDiscoveries, new String[] {"groupid"}, params.getSelectGroupDiscovery());
			relationMap.mapOne(result, groupDiscoveries, "groupDiscovery");
		}
	}
}
