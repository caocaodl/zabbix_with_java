package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_PROXY;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_PROXY;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_ACTIVE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_PROXY_PASSIVE;
import static com.isoft.iradar.inc.Defines.INTERFACE_PRIMARY;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_INTERNAL;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_PREG_HOST_FORMAT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.host.CHostNormalValidator;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CProxyDAO extends CCoreLongKeyDAO<CProxyGet> {
	
	public CProxyDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "hosts", "h", new String[]{"hostid", "host", "status"});
	}

	/**
	 * Get proxy data.
	 *
	 * @param array  _options
	 * @param array  _options['proxyids']
	 * @param bool   _options['editable']	only with read-write permission. Ignored for SuperAdmins
	 * @param int    _options['count']		returns value in rowscount
	 * @param string _options['pattern']
	 * @param int    _options['limit']
	 * @param string _options['sortfield']
	 * @param string _options['sortorder']
	 *
	 * @return array
	 */
	@Override
	public <T> T get(CProxyGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("hostid", "h.hostid");
		sqlParts.from.put("hosts", "hosts h");
		sqlParts.where.put("h.status IN ("+HOST_STATUS_PROXY_ACTIVE+","+HOST_STATUS_PROXY_PASSIVE+")");
		
		// deprecated
		checkDeprecatedParam(params, "selectInterfaces");
		
		// proxyids
		if (!is_null(params.getProxyIds())) {
			sqlParts.where.dbConditionInt("h.hostid",params.getProxyIds());
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("hosts h", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("hosts h", params, sqlParts);
		}
		
		// output
		if (API_OUTPUT_EXTEND.equals(params.getOutput())) {
			sqlParts.select.put("hostid", "h.hostid");
			sqlParts.select.put("host", "h.host");
			sqlParts.select.put("status", "h.status");
			sqlParts.select.put("lastaccess", "h.lastaccess");
		}

		// countOutput
		if (!is_null(params.getCountOutput())) {
			params.setSortfield("");
			sqlParts.select.put("COUNT(DISTINCT h.hostid) AS rowscount");
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
//		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts); 
		
		CArray<Map> result = new CArray<Map>();
		Object ret = result;
		
		for(Map row : datas){
			if (params.getCountOutput()!=null) {
				ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("hostid");
				Nest.value(row, "proxyid").$(id);
				unset(row,"hostid");
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
			unsetExtraFields(result, new String[]{"hostid"}, params.getOutput());
		}
	
		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	protected void checkInput(CArray<Map> proxies, String method) {
		if (Nest.value(userData(),"type").asInteger() != USER_TYPE_SUPER_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS,
				_("No permissions to referred object or it does not exist!"));
		}

		boolean create = ("create".equals(method));
		boolean update = ("update".equals(method));

		CArray proxyIds = rda_objectValues(proxies, "proxyid");

		for(Map proxy : proxies) {
			if (isset(proxy,"proxyid")) {
				Nest.value(proxy,"hostid").$(Nest.value(proxy,"proxyid").$());
			} else if (isset(proxy,"hostid")) {
				Nest.value(proxy,"proxyid").$(Nest.value(proxy,"hostid").$());
			}
		}

		// permissions
		CArray proxyDBfields = null;
		CArray<Map> dbProxies = null;
		if (update) {
			proxyDBfields = map("proxyid", null);

			CProxyGet options = new CProxyGet();
			options.setOutput(new String[]{"proxyid", "hostid", "host", "status"});
			options.setProxyIds(proxyIds.valuesAsLong());
			options.setEditable(true);
			options.setPreserveKeys(true);
			dbProxies  = get(options);
		} else {
			proxyDBfields = map("host", null);
		}

		for(Map proxy : proxies) {
			if (!check_db_fields(proxyDBfields, proxy)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Wrong fields for proxy \"%1$s\".", Nest.value(proxy,"host").$()));
			}

			Integer status = null;
			if (update) {
				if (!isset(dbProxies,proxy.get("proxyid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_("No permissions to referred object or it does not exist!"));
				}

				if (isset(proxy,"status")
						&& (Nest.value(proxy,"status").asInteger() != HOST_STATUS_PROXY_ACTIVE
						&& Nest.value(proxy,"status").asInteger() != HOST_STATUS_PROXY_PASSIVE)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Incorrect value used for proxy status \"%1$s\".", Nest.value(proxy,"status").$()));
				}

				status  = isset(proxy,"status") ? Nest.value(proxy,"status").asInteger() : Nest.value(dbProxies,proxy.get("proxyid"),"status").asInteger();
			} else {
				if (!isset(proxy,"status")) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No status for proxy."));
				} else if (Nest.value(proxy,"status").asInteger() != HOST_STATUS_PROXY_ACTIVE && Nest.value(proxy,"status").asInteger() != HOST_STATUS_PROXY_PASSIVE) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Incorrect value used for proxy status \"%1$s\".", Nest.value(proxy,"status").$()));
				}

				status = Nest.value(proxy,"status").asInteger();
			}

			// host
			if (isset(proxy,"host")) {
				if (preg_match("^"+RDA_PREG_HOST_FORMAT+"$", Nest.value(proxy,"host").asString())==0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Incorrect characters used for proxy name \"%1$s\".", Nest.value(proxy,"host").$()));
				}

				CProxyGet options = new CProxyGet();
				options.setFilter("host", Nest.value(proxy,"host").asString());
				CArray<Map> proxiesExists = get(options);
				for(Map proxyExists : proxiesExists) {
					if (create || bccomp(Nest.value(proxyExists,"proxyid").$(), Nest.value(proxy,"proxyid").$()) != 0) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Proxy \"%s\" already exists.", Nest.value(proxy,"host").$()));
					}
				}
			}

			// interface
			if (status == HOST_STATUS_PROXY_PASSIVE) {
				if (create && empty(Nest.value(proxy,"interface").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("No interface provided for proxy \"%s\".", Nest.value(proxy,"host").$()));
				}

				if (isset(proxy,"interface")) {
					if (!isArray(Nest.value(proxy,"interface").$()) || empty(Nest.value(proxy,"interface").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("No interface provided for proxy \"%s\".", Nest.value(proxy,"host").$()));
					}

					// mark the interface as main to pass host interface validation
					Nest.value(proxy,"interface","main").$(INTERFACE_PRIMARY);
				}
			}

			// check if hosts exist
			if (!empty(Nest.value(proxy,"hosts").$())) {
				CArray hostIds = rda_objectValues(Nest.value(proxy,"hosts").$(), "hostid");

				CHostGet options = new CHostGet();
				options.setHostIds(hostIds.valuesAsLong());
				options.setEditable(false);
				options.setOutput(new String[]{"hostid", "proxy_hostid", "name"});
				options.setPreserveKeys(true);
				CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);

				if (empty(hosts)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_("No permissions to referred object or it does not exist!"));
				}
			}
		}

		// check if any of the affected hosts are discovered
		CArray hostIds = array();
		for(Map proxy : proxies) {
			if (isset(proxy,"hosts")) {
				hostIds = array_merge(hostIds, rda_objectValues(Nest.value(proxy,"hosts").$(), "hostid"));
			}
		}
		checkValidator(hostIds.valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()),map(
			"message", _("Cannot update proxy for discovered host \"%1$s\".")
		)));
	}

	@Override
	public CArray<Long[]> create(CArray<Map> proxies) {
		proxies = convertDeprecatedValues(proxies);

		checkInput(proxies, "create");

		this.idBean = IRadarContext.IDBEAN_PLATFORM;
		CArray<Long> proxyIds = insert("hosts", Clone.deepcopy(proxies));
		this.idBean = IRadarContext.getIdentityBean();
		
		CArray<Map> hostUpdate = array();
		for (Entry<Object, Map> e : proxies.entrySet()) {
            Object key = e.getKey();
            Map proxy = e.getValue();
			if (!empty(Nest.value(proxy,"hosts").$())) {
				hostUpdate.add(map(
					"values", map("proxy_hostid", proxyIds.get(key)),
					"where", map("hostid", rda_objectValues(Nest.value(proxy,"hosts").$(), "hostid").valuesAsLong())
				));
			}

			// create interface
			if (Nest.value(proxy,"status").asInteger() == HOST_STATUS_PROXY_PASSIVE) {
				Nest.value(proxy,"interface","hostid").$(proxyIds.get(key));

				if (empty(API.HostInterface(this.idBean, this.getSqlExecutor()).create(Nest.value(proxy,"interface").asCArray()))) {
					throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Proxy interface creation failed."));
				}
			}
		}

		IRadarContext.setIgnoreTenantForSql(true);
		update("hosts", hostUpdate);
		IRadarContext.setIgnoreTenantForSql(false);

		return map("proxyids", proxyIds.valuesAsLong());
	}

	@Override
	public CArray<Long[]> update(CArray<Map> proxies) {
		proxies = convertDeprecatedValues(proxies);

		checkInput(proxies, "update");

		CArray proxyIds = array();
		CArray<Map> proxyUpdate = array();
		CArray<Map> hostUpdate = array();

		for(Map proxy : proxies) {
			proxyIds.add(Nest.value(proxy,"proxyid").$());

			proxyUpdate.add(map(
				"values", proxy,
				"where", map("hostid", Nest.value(proxy,"proxyid").$())
			));

			if (isset(proxy,"hosts")) {
				// unset proxy for all hosts except for discovered hosts
				hostUpdate.add(map(
					"values", map("proxy_hostid", 0),
					"where", map(
						"proxy_hostid", Nest.value(proxy,"proxyid").$(),
						"flags", RDA_FLAG_DISCOVERY_NORMAL
					)
				));

				hostUpdate.add(map(
					"values", map("proxy_hostid", Nest.value(proxy,"proxyid").$()),
					"where", map("hostid", rda_objectValues(Nest.value(proxy,"hosts").$(), "hostid").valuesAsLong())
				));
			}

			// if this is an active proxy - delete it's interface;
			if (isset(proxy,"status") && Nest.value(proxy,"status").asInteger() == HOST_STATUS_PROXY_ACTIVE) {
				CHostIfaceGet hioptions = new CHostIfaceGet();
				hioptions.setHostIds(Nest.value(proxy,"hostid").asLong());
				hioptions.setOutput(new String[]{"interfaceid"});
				CArray<Map> interfaces = API.HostInterface(this.idBean, this.getSqlExecutor()).get(hioptions);
				CArray<Long> interfaceIds = rda_objectValues(interfaces, "interfaceid");

				if (!empty(interfaceIds)) {
					API.HostInterface(this.idBean, this.getSqlExecutor()).delete(interfaceIds.valuesAsLong());
				}
			} else if (isset(proxy,"interface") && isArray(Nest.value(proxy,"interface").$())) {// update the interface of a passive proxy
				Nest.value(proxy,"interface","hostid").$(Nest.value(proxy,"hostid").$());

				CArray<Long[]> result = isset(Nest.value(proxy,"interface","interfaceid").$())
					? API.HostInterface(this.idBean, this.getSqlExecutor()).update(Nest.value(proxy,"interface").asCArray())
					: API.HostInterface(this.idBean, this.getSqlExecutor()).create(Nest.value(proxy,"interface").asCArray());

				if (empty(result)) {
					throw CDB.exception(RDA_API_ERROR_INTERNAL, _("Proxy interface update failed."));
				}
			}
		}

		this.idBean = IRadarContext.IDBEAN_PLATFORM;
		update("hosts", proxyUpdate);
		this.idBean = IRadarContext.getIdentityBean();
		
		IRadarContext.setIgnoreTenantForSql(true);
		update("hosts", hostUpdate);
		IRadarContext.setIgnoreTenantForSql(false);

		return map("proxyids", proxyIds.valuesAsLong());
	}

	@Override
	public CArray<Long[]> delete(Long... proxyIds) {
		validateDelete(proxyIds);

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbProxies = DBselect(getSqlExecutor(),
			"SELECT h.hostid,h.host"+
			" FROM hosts h"+
			" WHERE "+sqlParts.dual.dbConditionInt("h.hostid", proxyIds),
			sqlParts.getNamedParams()
		);
		dbProxies = DBfetchArrayAssoc(dbProxies, "hostid");

		CArray actionIds = array();

		// get conditions
		sqlParts = new SqlBuilder();
		CArray<Map> dbActions = DBselect(getSqlExecutor(),
			"SELECT DISTINCT c.actionid"+
			" FROM conditions c"+
			" WHERE c.conditiontype="+CONDITION_TYPE_PROXY+
				" AND "+sqlParts.dual.dbConditionInt("c.value", proxyIds),
			sqlParts.getNamedParams()
		);
		for (Map dbAction : dbActions) {
			Nest.value(actionIds,dbAction.get("actionid")).$(Nest.value(dbAction,"actionid").$());
		}

		if (!empty(actionIds)) {
			update("actions", array((Map)map(
				"values", map("status", ACTION_STATUS_DISABLED),
				"where", map("actionid", actionIds.valuesAsLong())
			)));
		}

		// delete action conditions
		delete("conditions", (Map)map(
			"conditiontype", CONDITION_TYPE_PROXY,
			"value", proxyIds
		));

		// delete interface
		delete("interface", (Map)map("hostid", proxyIds));

		// delete host
		this.idBean = IRadarContext.IDBEAN_PLATFORM;
		delete("hosts", (Map)map("hostid", proxyIds));
		this.idBean = IRadarContext.getIdentityBean();
		
		// TODO: remove info from API
		for(Map proxy : dbProxies) {
			info(_s("Deleted: Proxy \"%1$s\".", Nest.value(proxy,"host").$()));
			add_audit(this.idBean, getSqlExecutor(),AUDIT_ACTION_DELETE, AUDIT_RESOURCE_PROXY, "["+proxy.get("host")+"] ["+proxy.get("hostid")+"]");
		}

		return map("proxyids", proxyIds);
	}
	
	/**
	 * Check if proxies can be deleted.
	 *  - only super admin can delete proxy
	 *  - cannot delete proxy if it is used to monitor host
	 *  - cannot delete proxy if it is used in discovery rule
	 *
	 * @param array _proxyIds
	 */
	protected void validateDelete(Long... proxyIds) {
		if (empty(proxyIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		checkPermissions(proxyIds);
		checkUsedInDiscoveryRule(proxyIds);
		checkUsedForMonitoring(proxyIds);
	}
	
	@Override
	public boolean isReadable(Long... proxyIds) {
		if (!isArray(proxyIds)) {
			return false;
		}
		if (empty(proxyIds)) {
			return true;
		}
		proxyIds = array_unique(proxyIds);
		CProxyGet options = new CProxyGet();
		options.setProxyIds(proxyIds);
		options.setCountOutput(true);
		long count = get(options);
		return (count(proxyIds) == count);
	}

	@Override
	public boolean isWritable(Long... proxyIds) {
		if (!isArray(proxyIds)) {
			return false;
		}
		if (empty(proxyIds)) {
			return true;
		}
		proxyIds = array_unique(proxyIds);
		CProxyGet options = new CProxyGet();
		options.setProxyIds(proxyIds);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(proxyIds) == count);
	}

	/**
	 * Checks if the given proxies are editable.
	 *
	 * @param array _proxyIds	proxy IDs to check
	 *
	 * @throws APIException		if the user has no permissions to edit proxies or a proxy does not exist
	 */
	protected void checkPermissions(Long... proxyIds) {
		if (!isWritable(proxyIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	/**
	 * Check if proxy is used in discovery rule.
	 *
	 * @param array _proxyIds
	 */
	protected void checkUsedInDiscoveryRule(Long... proxyIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map dRule = DBfetch(DBselect(getSqlExecutor(),
			"SELECT dr.druleid,dr.name,dr.proxy_hostid"+
			" FROM drules dr"+
			" WHERE "+sqlParts.dual.dbConditionInt("dr.proxy_hostid", proxyIds),
			1,
			sqlParts.getNamedParams()
		));
		if (!empty(dRule)) {
			Map proxy = DBfetch(DBselect(getSqlExecutor(),"SELECT h.host FROM hosts h WHERE h.hostid="+Nest.value(dRule,"proxy_hostid").$()));
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Proxy \"%1$s\" is used by discovery rule \"%2$s\".", Nest.value(proxy,"host").$(), Nest.value(dRule,"name").$()));
		}
	}
	
	/**
	 * Check if proxy is used to monitor hosts.
	 *
	 * @param array _proxyIds
	 */
	protected void checkUsedForMonitoring(Long... proxyIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map host = DBfetch(DBselect(getSqlExecutor(),
			"SELECT h.name,h.proxy_hostid"+
			" FROM hosts h"+
			" WHERE "+sqlParts.dual.dbConditionInt("h.proxy_hostid", proxyIds),
			1,
			sqlParts.getNamedParams()
		));
		if (!empty(host)) {
			Map proxy = DBfetch(DBselect(getSqlExecutor(),"SELECT h.host FROM hosts h WHERE h.hostid="+Nest.value(host,"proxy_hostid").$()));
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Host \"%1$s\" is monitored with proxy \"%2$s\".", Nest.value(host,"name").$(), Nest.value(proxy,"host").$()));
		}
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (Nest.value(params,"countOutput").$() == null && Nest.value(params,"selectInterface").$() != null) {
			addQuerySelect("h.hostid", sqlParts);
		}
	}

	@Override
	protected void addRelatedObjects(CProxyGet options, CArray<Map> result) {
		super.addRelatedObjects(options, result);

		CArray proxyIds = array_keys(result);
		// selectHosts
		if (options.getSelectHosts() != null &&  !API_OUTPUT_COUNT.equals(options.getSelectHosts())) {
			CHostGet hoptions = new CHostGet();
			hoptions.setOutput(outputExtend("hosts", new String[]{"hostid", "proxy_hostid"}, options.getSelectHosts()));
			hoptions.setProxyIds(proxyIds.valuesAsLong());
			hoptions.setPreserveKeys(true);
			CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

			CRelationMap relationMap = createRelationMap(hosts, "proxy_hostid", "hostid");
			unsetExtraFields(hosts, new String[]{"proxy_hostid", "hostid"}, options.getSelectHosts());
			relationMap.mapMany(result, hosts, "hosts");
		}

		// adding host interface
		if (options.getSelectInterface() != null && !API_OUTPUT_COUNT.equals(options.getSelectInterface())) {
			CHostIfaceGet hioptions = new CHostIfaceGet();
			hioptions.setOutput(outputExtend("interface", new String[]{"interfaceid", "hostid"}, options.getSelectInterface()));
			hioptions.setHostIds(proxyIds.valuesAsLong());
			hioptions.setNopermissions(true);
			hioptions.setPreserveKeys(true);
			CArray<Map> interfaces = API.HostInterface(this.idBean, this.getSqlExecutor()).get(hioptions);

			CRelationMap relationMap = createRelationMap(interfaces, "hostid", "interfaceid");
			unsetExtraFields(interfaces, new String[]{"hostid", "interfaceid"}, options.getSelectInterface());
			relationMap.mapOne(result, interfaces, "interface");

			for (Entry<Object, Map> e : result.entrySet()) {
                Object key = e.getKey();
                Map proxy = e.getValue();
				if (!empty(Nest.value(proxy,"interface").$())) {
					Nest.value(result, key, "interface").$(Nest.value(proxy,"interface").$());
				}
			}
		}

		// adding host interfaces (deprecated)
		if (options.getSelectInterfaces() != null &&  !API_OUTPUT_COUNT.equals(options.getSelectInterfaces())) {
			CHostIfaceGet hioptions = new CHostIfaceGet();
			hioptions.setOutput(outputExtend("interface", new String[]{"interfaceid", "hostid"}, options.getSelectInterfaces()));
			hioptions.setHostIds(proxyIds.valuesAsLong());
			hioptions.setNopermissions(true);
			hioptions.setPreserveKeys(true);
			CArray<Map> interfaces = API.HostInterface(this.idBean, this.getSqlExecutor()).get(hioptions);

			CRelationMap relationMap = createRelationMap(interfaces, "hostid", "interfaceid");
			unsetExtraFields(interfaces, new String[]{"hostid", "interfaceid"}, options.getSelectInterfaces());
			relationMap.mapOne(result, interfaces, "interfaces");

			for (Entry<Object, Map> e : result.entrySet()) {
                Object key = e.getKey();
                Map proxy = e.getValue();
				if (!empty(Nest.value(proxy,"interfaces").$())) {
					Nest.value(result, key, "interfaces").$(Nest.value(proxy,"interfaces").$());
				}
			}
		}
	}
	
	/**
	 * Convert deprecated \"interfaces\" to \"interface\".
	 *
	 * @param array _proxies
	 *
	 * @return array
	 */
	protected CArray<Map> convertDeprecatedValues(CArray<Map> proxies) {
		for (Entry<Object, Map> e : proxies.entrySet()) {
            Object key = e.getKey();
            Map proxy = e.getValue();
			if (isset(proxy,"interfaces")) {
				deprecated("Array of \"interfaces\" is deprecated, use single \"interface\" instead.");
				Nest.value(proxy,"interface").$(reset(Nest.value(proxy,"interfaces").asCArray()));
				unset(proxy,"interfaces");
				Nest.value(proxies,key).$(proxy);
			}
		}
		return proxies;
	}
}
