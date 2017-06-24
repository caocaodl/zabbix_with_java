package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_ONETIME;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_diff;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.ValidateUtil.validateUnixTime;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CMaintenanceDAO extends CCoreLongKeyDAO<CMaintenanceGet> {

	public CMaintenanceDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "maintenances", "m", new String[]{"maintenanceid", "name", "maintenance_type"});
	}

	@Override
	public <T> T get(CMaintenanceGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("maintenance", "m.maintenanceid");
		sqlParts.from.put("maintenances", "maintenances m");
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			params.setSelectGroups(true);
		}

		// hostids
		if (!is_null(params.getHostIds())) {
			params.setSelectHosts(true);
		}

		// maintenanceids
		if (!is_null(params.getMaintenanceIds())) {
			sqlParts.where.dbConditionInt("m.maintenanceid", params.getMaintenanceIds());
		}
		
		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("maintenances m", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("maintenances m", params, sqlParts);
		}
		
		// limit
		if (params.getLimit() != null) {
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
				Long id = (Long)row.get("maintenanceid");
			
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// groupids
				if (isset(row.get("groupid")) && is_null(params.getSelectGroups())) {
					if(!result.get(id).containsKey("groups")){
						result.get(id).put("groups", new ArrayList(0));
					}
					((CArray)result.get(id).get("groups")).add(map("groupid",row.remove("groupid")));
				}
				
				// hostids
				if (isset(row.get("hostid")) && is_null(params.getSelectHosts())) {
					if(!result.get(id).containsKey("hosts")){
						result.get(id).put("hosts", new ArrayList(0));
					}
					((CArray)result.get(id).get("hosts")).add(map("hostid",row.remove("hostid")));
				}

				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Determine, whether an object already exists
	 *
	 * @param array _object
	 * @return bool
	 */
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("maintenanceid", "name"));
		CMaintenanceGet options = new CMaintenanceGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"maintenanceid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	@Override
	public CArray<Long[]> create(CArray<Map> maintenances) {
		if (Nest.value(this.userData(),"type").asInteger() == USER_TYPE_IRADAR_USER) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
		
		Long[] hostids = new Long[0];
		Long[] groupids = new Long[0];
		for(Map maintenance : maintenances) {
			hostids = array_merge(hostids, Nest.array(maintenance,"hostids").asLong());
			groupids = array_merge(groupids, Nest.array(maintenance,"groupids").asLong());
		}
		
		// validate hosts & groups
		if (empty(hostids) && empty(groupids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("At least one host or group should be selected."));
		}
		
		// hosts permissions
		CHostGet options = new CHostGet();
		options.setHostIds(hostids);
		options.setEditable(true);
		options.setOutput(new String[]{"hostid"});
		options.setPreserveKeys(true);
		CArray<Map> updHosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
		for(Long hostid : hostids) {
			if (!isset(updHosts,hostid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}
		
		// groups permissions
		CHostGroupGet hgOptions = new CHostGroupGet();
		hgOptions.setGroupIds(groupids);
		hgOptions.setEditable(true);
		hgOptions.setOutput(new String[]{"groupid"});
		hgOptions.setPreserveKeys(true);
		CArray<Map> updGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgOptions);
		for(Long groupid : groupids) {
			if (!isset(updGroups, groupid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}
		
		removeSecondsFromTimes(maintenances);
		
		int tid = 0;
		CArray insert = array();
		CArray<Object> timeperiods = array();
		CArray insertTimeperiods = array();
		long now = time();
		now -= now % SEC_PER_MIN;
		for (Entry<Object, Map> e : maintenances.entrySet()) {
            Object mnum = e.getKey();
            Map maintenance = e.getValue();
            CArray dbFields = map(
				"name", null,
				"active_since", now,
				"active_till", now + SEC_PER_DAY
			);
			if (!check_db_fields(dbFields, maintenance)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect parameters for maintenance."));
			}

			// validate if maintenance name already exists
			if (exists(map("name", Nest.value(maintenance,"name").asString()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Maintenance \"%s\" already exists.", Nest.value(maintenance,"name").$()));
			}

			// validate maintenance active since
			if (!validateUnixTime(Nest.value(maintenance,"active_since").asLong())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active since")));
			}

			// validate maintenance active till
			if (!validateUnixTime(Nest.value(maintenance,"active_till").asLong())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active till")));
			}

			// validate maintenance active interval
			if (Nest.value(maintenance,"active_since").asLong() > Nest.value(maintenance,"active_till").asLong()) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Maintenance \"Active since\" value cannot be bigger than \"Active till\"."));
			}

			// validate timeperiods
			if (empty(Nest.value(maintenance,"timeperiods").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("At least one maintenance period must be created."));
			}

			Nest.value(insert,mnum).$(maintenance);

			CArray<Map> _timeperiods = Nest.value(maintenance,"timeperiods").asCArray();
			for(Map timeperiod : _timeperiods) {
				dbFields = map(
					"timeperiod_type", TIMEPERIOD_TYPE_ONETIME,
					"period", SEC_PER_HOUR,
					"start_date",	now
				);
				check_db_fields(dbFields, timeperiod);

				tid++;
				Nest.value(insertTimeperiods,tid).$(timeperiod);
				Nest.value(timeperiods,tid).$(mnum);
			}
		}
		CArray<Long> maintenanceids = insert("maintenances", insert);
		CArray<Long> timeperiodids = insert("timeperiods", insertTimeperiods);

		CArray insertWindows = array();
		for (Entry e : timeperiods.entrySet()) {
            Object key = e.getKey();
            Object mnum = e.getValue();
			insertWindows.add(map(
				"timeperiodid", Nest.value(timeperiodids,key).$(),
				"maintenanceid", Nest.value(maintenanceids,mnum).$()
			));
		}
		insert("maintenances_windows", insertWindows);

		CArray<Map> insertHosts = array();
		CArray<Map> insertGroups = array();
		for (Entry<Object, Map> e : maintenances.entrySet()) {
            Object mnum = e.getKey();
            Map maintenance = e.getValue();
            hostids = Nest.array(maintenance,"hostids").asLong();
			for(Long hostid : hostids) {
				insertHosts.add(map(
					"hostid", hostid,
					"maintenanceid", Nest.value(maintenanceids,mnum).$()
				));
			}
			groupids = Nest.array(maintenance,"groupids").asLong();
			for(Long groupid : groupids) {
				insertGroups.add(map(
					"groupid", groupid,
					"maintenanceid", Nest.value(maintenanceids,mnum).$()
				));
			}
		}
		insert("maintenances_hosts", insertHosts);
		insert("maintenances_groups", insertGroups);

		return map("maintenanceids", maintenanceids);
	}

	@Override
	public CArray<Long[]> update(CArray<Map> maintenances) {
		CArray maintenanceids = rda_objectValues(maintenances, "maintenanceid");

		// validate maintenance permissions
		if (Nest.value(this.userData(),"type").asInteger() == USER_TYPE_IRADAR_USER) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		Long[] hostids = new Long[0];
		Long[] groupids = new Long[0];
		CMaintenanceGet params = new CMaintenanceGet();
		params.setMaintenanceIds(rda_objectValues(maintenances, "maintenanceid").valuesAsLong());
		params.setEditable(true);
		params.setOutput(API_OUTPUT_EXTEND);
		params.setSelectGroups(API_OUTPUT_REFER);
		params.setSelectHosts(API_OUTPUT_REFER);
		params.setSelectTimeperiods(API_OUTPUT_EXTEND);
		params.setPreserveKeys(true);
		CArray<Map> updMaintenances = get(params);

		for(Map maintenance : maintenances) {
			if (!isset(updMaintenances,Nest.value(maintenance,"maintenanceid").$())) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}

			// Checking whether a maintenance with this name already exists. First, getting all maintenances with the same name as this
			CMaintenanceGet options = new CMaintenanceGet();
			options.setFilter("name", Nest.value(maintenance,"name").asString());
			CArray<Map> receivedMaintenances = API.Maintenance(this.idBean, this.getSqlExecutor()).get(options);

			// validate if maintenance name already exists
			for(Map rMaintenance : receivedMaintenances) {
				if (bccomp(Nest.value(rMaintenance,"maintenanceid").$(), Nest.value(maintenance,"maintenanceid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Maintenance \"%s\" already exists.", Nest.value(maintenance,"name").$()));
				}
			}

			// validate maintenance active since
			if (!validateUnixTime(Nest.value(maintenance,"active_since").asLong())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active since")));
			}

			// validate maintenance active till
			if (!validateUnixTime(Nest.value(maintenance,"active_till").asLong())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("\"%s\" must be between 2010.01.01 and 2038.01.01.", _("Active till")));
			}

			// validate maintenance active interval
			if (Nest.value(maintenance,"active_since").asLong() > Nest.value(maintenance,"active_till").asLong()) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Maintenance \"Active since\" value cannot be bigger than \"Active till\"."));
			}

			// validate timeperiods
			if (empty(Nest.value(maintenance,"timeperiods").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("At least one maintenance period must be created."));
			}

			hostids = array_merge(hostids, Nest.array(maintenance,"hostids").asLong());
			groupids = array_merge(groupids, Nest.array(maintenance,"groupids").asLong());
		}

		// validate hosts & groups
		if (empty(hostids) && empty(groupids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("At least one host or group should be selected."));
		}

		// validate hosts permissions
		CHostGet options = new CHostGet();
		options.setHostIds(hostids);
		options.setEditable(true);
		options.setOutput(new String[]{"hostid"});
		options.setPreserveKeys(true);
		CArray<Map> updHosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
		for(Long hostid : hostids) {
			if (!isset(updHosts, hostid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
			}
		}
		// validate groups permissions
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setGroupIds(groupids);
		hgoptions.setEditable(true);
		hgoptions.setOutput(new String[]{"groupid"});
		hgoptions.setPreserveKeys(true);
		CArray<Map> updGroups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(hgoptions);
		for(Long groupid : groupids) {
			if (!isset(updGroups,groupid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		removeSecondsFromTimes(maintenances);

		CArray<Map> update = array();
		for (Entry<Object, Map> e : maintenances.entrySet()) {
            Object mnum = e.getKey();
            Map maintenance = e.getValue();
            CArray dbFields = map(
				"maintenanceid", null
			);

			// validate fields
			if (!check_db_fields(dbFields, maintenance)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect parameters for maintenance."));
			}

			Nest.value(update,mnum).$(map(
				"values", maintenance,
				"where", map("maintenanceid", Nest.value(maintenance,"maintenanceid").$())
			));

			// update time periods
			replaceTimePeriods(Nest.value(updMaintenances,maintenance.get("maintenanceid")).asCArray(), maintenance);
		}
		update("maintenances", Clone.deepcopy(update));

		// some of the hosts and groups bound to maintenance must be deleted, other inserted and others left alone
		CArray<Map> insertHosts = array();
		CArray<Map> insertGroups = array();

		for(Map maintenance : maintenances) {
			// putting apart those host<->maintenance connections that should be inserted, deleted and not changed
			// Nest.value(_hostDiff,"first").$() - new hosts, that should be inserted
			// Nest.value(_hostDiff,"second").$() - hosts, that should be deleted
			// Nest.value(_hostDiff,"both").$() - hosts, that should not be touched
			CArray hostDiff = rda_array_diff(
				rda_toObject(Nest.value(maintenance,"hostids").asCArray(), "hostid"),
				Nest.value(updMaintenances,maintenance.get("maintenanceid"),"hosts").asCArray(),
				"hostid"
			);

			for(Map host : (CArray<Map>)Nest.value(hostDiff,"first").asCArray()) {
				insertHosts.add(map(
					"hostid", Nest.value(host,"hostid").$(),
					"maintenanceid", Nest.value(maintenance,"maintenanceid").$()
				));
			}
			for(Map host : (CArray<Map>)Nest.value(hostDiff,"second").asCArray()) {
				CArray deleteHosts = map(
					"hostid", Nest.value(host,"hostid").$(),
					"maintenanceid", Nest.value(maintenance,"maintenanceid").$()
				);
				delete("maintenances_hosts", deleteHosts);
			}

			// now the same with the groups
			CArray groupDiff = rda_array_diff(
				rda_toObject(Nest.value(maintenance,"groupids").asCArray(), "groupid"),
				Nest.value(updMaintenances,maintenance.get("maintenanceid"),"groups").asCArray(),
				"groupid"
			);

			for(Map group : (CArray<Map>)Nest.value(groupDiff,"first").asCArray()) {
				insertGroups.add(map(
					"groupid", Nest.value(group,"groupid").$(),
					"maintenanceid", Nest.value(maintenance,"maintenanceid").$()
				));
			}
			for(Map group : (CArray<Map>)Nest.value(groupDiff,"second").asCArray()) {
				CArray _deleteGroups = map(
					"groupid", Nest.value(group,"groupid").$(),
					"maintenanceid", Nest.value(maintenance,"maintenanceid").$()
				);
				delete("maintenances_groups", _deleteGroups);
			}
		}

		insert("maintenances_hosts", insertHosts);
		insert("maintenances_groups", insertGroups);

		return map("maintenanceids", maintenanceids);
	}
	
	@Override
	public CArray<Long[]> delete(Long... maintenanceids) {
		if (Nest.value(this.userData(),"type").asInteger() == USER_TYPE_IRADAR_USER) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
		}

		CMaintenanceGet options = new CMaintenanceGet();
		options.setMaintenanceIds(TArray.as(maintenanceids).asLong());
		options.setEditable(true);
		options.setOutput(new String[]{"maintenanceid"});
		options.setPreserveKeys(true);
		CArray<Map> maintenances = get(options);
		for(Long maintenanceid : maintenanceids) {
			if (!isset(maintenances, maintenanceid)) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("You do not have permission to perform this operation."));
			}
		}

		CArray timeperiodids = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbTimeperiods = DBselect(getSqlExecutor(),
			"SELECT DISTINCT tp.timeperiodid"+
			" FROM timeperiods tp,maintenances_windows mw"+
			" WHERE "+sqlParts.dual.dbConditionInt("mw.maintenanceid", maintenanceids)+
				" AND tp.tenantid=mw.tenantid"+
				" AND tp.timeperiodid=mw.timeperiodid",
				sqlParts.getNamedParams()
		);
		for(Map timeperiod : dbTimeperiods) {
			timeperiodids.add(Nest.value(timeperiod,"timeperiodid").$());
		}

		CArray midCond = map("maintenanceid", maintenanceids);

		// remove maintenanceid from hosts table
		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"hostid"});
		hoptions.setFilter("maintenanceid", TArray.as(maintenanceids).asString());
		hoptions.put("real_hosts", true);
		CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
		if (!empty(hosts)) {
			update("hosts", array((Map)map(
				"values", map("maintenanceid", 0),
				"where", map("hostid", rda_objectValues(hosts, "hostid").valuesAsLong())
			)));
		}

		delete("timeperiods", (Map)map("timeperiodid", timeperiodids.valuesAsLong()));
		delete("maintenances_windows", midCond);
		delete("maintenances_hosts", midCond);
		delete("maintenances_groups", midCond);
		delete("maintenances", midCond);
		return map("maintenanceids", maintenanceids);
	}
	
	/**
	 * Reset seconds to zero in maintenace time values.
	 *
	 * @param array _maintenances passed by reference
	 */
	protected void removeSecondsFromTimes(CArray<Map> maintenances) {
		for(Map maintenance : maintenances) {
			if (isset(maintenance,"active_since")) {
				Nest.value(maintenance,"active_since").$(Nest.value(maintenance,"active_since").asLong()-(Nest.value(maintenance,"active_since").asLong() % SEC_PER_MIN));
			}

			if (isset(maintenance,"active_till")) {
				Nest.value(maintenance,"active_till").$(Nest.value(maintenance,"active_till").asLong()-(Nest.value(maintenance,"active_till").asLong() % SEC_PER_MIN));
			}


			if (isset(maintenance,"timeperiods")) {
				CArray<Map> timeperiods = Nest.value(maintenance,"timeperiods").asCArray();
				for (Map timeperiod : timeperiods) {
					if (isset(timeperiod,"start_date")) {
						Nest.value(timeperiod,"start_date").$(Nest.value(timeperiod,"start_date").asLong() - (Nest.value(timeperiod,"start_date").asLong() % SEC_PER_MIN));
					}
				}
			}
		}
	}
	
	/**
	 * Updates maintenance time periods.
	 *
	 * @param array _maintenance
	 * @param array _oldMaintenance
	 */
	protected void replaceTimePeriods(Map oldMaintenance, Map maintenance) {
		// replace time periods
		CArray<Map> timePeriods = replace("timeperiods", Nest.value(oldMaintenance,"timeperiods").asCArray(), Nest.value(maintenance,"timeperiods").asCArray());

		// link new time periods to maintenance
		CArray<Map> oldTimePeriods = rda_toHash(Nest.value(oldMaintenance,"timeperiods").$(), "timeperiodid");
		CArray<Map> newMaintenanceWindows = array();
		for(Map tp : timePeriods) {
			if (!isset(oldTimePeriods,Nest.value(tp,"timeperiodid").$())) {
				newMaintenanceWindows.add(map(
					"maintenanceid", Nest.value(maintenance,"maintenanceid").$(),
					"timeperiodid", Nest.value(tp,"timeperiodid").$()
				));
			}
		}
		insert("maintenances_windows", newMaintenanceWindows);
	}

	@Override
	protected void addRelatedObjects(CMaintenanceGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		// selectGroups
		if(!is_null(params.getSelectGroups()) && !API_OUTPUT_COUNT.equals(params.getSelectGroups())){
			CRelationMap relationMap = createRelationMap(result, "maintenanceid", "groupid", "maintenances_groups");
			CHostGroupGet options = new CHostGroupGet();
			options.setOutput(params.getSelectGroups());
			options.setGroupIds(relationMap.getRelatedLongIds());
			options.setPreserveKeys(true);
			CArray<Map> groups = API.HostGroup(this.idBean, this.getSqlExecutor()).get(options);
			relationMap.mapMany(result, groups, "groups");
		}
		
		// selectHosts
		if(!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())){
			CRelationMap relationMap = createRelationMap(result, "maintenanceid", "hostid", "maintenances_hosts");
			CHostGet options = new CHostGet();
			options.setOutput(params.getSelectHosts());
			options.setHostIds(relationMap.getRelatedLongIds());
			options.setPreserveKeys(true);
			CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);
			relationMap.mapMany(result, hosts, "hosts");
		}
		
		// selectTimeperiods
		if(!is_null(params.getSelectTimeperiods()) && !API_OUTPUT_COUNT.equals(params.getSelectTimeperiods())){
			CRelationMap relationMap = createRelationMap(result, "maintenanceid", "timeperiodid", "maintenances_windows");
			CParamGet options = new CParamGet();
			options.setOutput(params.getSelectTimeperiods());
			options.setFilter("timeperiodid", relationMap.getRelatedLongIds());
			options.setPreserveKeys(true);
			CArray<Map> timeperiods = this.select("timeperiods", options);			
			relationMap.mapMany(result, timeperiods, "timeperiods");
		}
	}
}
