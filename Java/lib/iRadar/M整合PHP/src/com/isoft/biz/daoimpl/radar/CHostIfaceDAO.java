package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.ltrim;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.INTERFACE_PRIMARY;
import static com.isoft.iradar.inc.Defines.INTERFACE_TYPE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.INTERFACE_USE_DNS;
import static com.isoft.iradar.inc.Defines.INTERFACE_USE_IP;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_DNS_FORMAT;
import static com.isoft.iradar.inc.Defines.RDA_PREG_EXPRESSION_USER_MACROS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_MACRO_NAME_FORMAT;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.HostsUtil.hostInterfaceTypeNumToName;
import static com.isoft.iradar.inc.ValidateUtil.validatePortNumberOrMacro;
import static com.isoft.iradar.inc.ValidateUtil.validate_ip;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.host.CHostNormalValidator;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CHostIfaceDAO extends CCoreLongKeyDAO<CHostIfaceGet> {
	
	public CHostIfaceDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "interface", "hi", new String[]{"interfaceid","dns","ip"});
	}
	
	@Override
	public <T> T get(CHostIfaceGet params) {
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("interface", "hi.interfaceid");
		sqlParts.from.put("interface", "interface hi");
		
		// interfaceids
		if (!is_null(params.getInterfaceIds())) {
			sqlParts.where.dbConditionInt("interfaceid","hi.interfaceid",params.getInterfaceIds());
		}

		// hostids
		if (!is_null(params.getHostIds())) {
			sqlParts.select.put("hostid","hi.hostid");
			sqlParts.where.dbConditionInt("hostid","hi.hostid",params.getHostIds());
		}

		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","i.itemid");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("i.itemid",params.getItemIds());
			sqlParts.where.put("hi.tenantid","hi.tenantid=i.tenantid");
			sqlParts.where.put("hi","hi.interfaceid=i.interfaceid");
		}
		
		// triggerids
		if (!is_null(params.getTriggerIds())) {
			sqlParts.select.put("triggerid","f.triggerid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("f.triggerid",params.getTriggerIds());
			sqlParts.where.put("hi.tenantid","hi.tenantid=i.tenantid");
			sqlParts.where.put("hi","hi.hostid=i.hostid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
		}
		
		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("interface hi", params, sqlParts);
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("interface hi", params, sqlParts);
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
				Long id = (Long)row.get("interfaceid");
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// itemids
				if (isset(row.get("itemid")) && is_null(params.getSelectItems())) {
					if (!isset(result.get(id).get("items"))) {
						result.get(id).put("items", new CArray());
					}
					((CArray)result.get(id).get("items")).add(map("itemid", row.remove("itemid")));
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
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array("interfaceid", "hostid", "ip", "dns");
		CHostIfaceGet options = new CHostIfaceGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"interfaceid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	/**
	 * Check interfaces input.
	 *
	 * @param array  ifaces
	 * @param string _method
	 */
	public void checkInput(CArray<Map> interfaces, String method) {
		boolean update = ("update".equals(method));

		CArray interfaceDBfields = null;
		CArray<Map> dbInterfaces = null;
		// permissions
		if (update) {
			interfaceDBfields = map("interfaceid", null);
			CHostIfaceGet options = new CHostIfaceGet();
			options.setOutput(API_OUTPUT_EXTEND);
			options.setInterfaceIds(rda_objectValues(interfaces, "interfaceid").valuesAsLong());
			options.setEditable(true);
			options.setPreserveKeys(true);
			dbInterfaces = get(options);
		} else {
			interfaceDBfields = map(
				"hostid", null,
				"ip", null,
				"dns", null,
				"useip", null,
				"port", null,
				"main", null
			);
		}

		CHostGet hoptions = new CHostGet();
		hoptions.setOutput(new String[]{"host"});
		hoptions.setHostIds(rda_objectValues(interfaces, "hostid").valuesAsLong());
		hoptions.setEditable(true);
		hoptions.setPreserveKeys(true);
		CArray<Map> dbHosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);

		CProxyGet poptions = new CProxyGet();
		poptions.setOutput(new String[]{"host"});
		poptions.setProxyIds(rda_objectValues(interfaces, "hostid").valuesAsLong());
		poptions.setEditable(true);
		poptions.setPreserveKeys(true);
		CArray<Map> dbProxies = API.Proxy(this.idBean, this.getSqlExecutor()).get(poptions);

		for(Map iface : interfaces) {
			if (!check_db_fields(interfaceDBfields, iface)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect arguments passed to function."));
			}

			Map updInterface = null;
			if (update) {
				if (!isset(dbInterfaces,iface.get("interfaceid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				Map dbInterface = dbInterfaces.get(iface.get("interfaceid"));
				if (isset(iface,"hostid") && bccomp(Nest.value(dbInterface,"hostid").$(), Nest.value(iface,"hostid").$()) != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot switch host for interface."));
				}

				Nest.value(iface,"hostid").$(Nest.value(dbInterface,"hostid").$());

				// we check all fields on \"updated\" interface
				updInterface = Clone.deepcopy(iface);
				Nest.merge(dbInterface, iface).into(iface);
			} else {
				if (!isset(dbHosts,iface.get("hostid")) && !isset(dbProxies,iface.get("hostid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				if (isset(dbProxies,iface.get("hostid"))) {
					Nest.value(iface,"type").$(INTERFACE_TYPE_UNKNOWN);
				} else if (!isset(iface, "type")) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect arguments passed to method."));
				}
			}

			if (rda_empty(Nest.value(iface,"ip").$()) && rda_empty(Nest.value(iface,"dns").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("IP and DNS cannot be empty for host interface."));
			}

			if (Nest.value(iface,"useip").asInteger() == INTERFACE_USE_IP && rda_empty(Nest.value(iface,"ip").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Interface with DNS \"%1$s\" cannot have empty IP address.", Nest.value(iface,"dns").$()));
			}

			if (Nest.value(iface,"useip").asInteger() == INTERFACE_USE_DNS && rda_empty(Nest.value(iface,"dns").$())) {
				if (!empty(dbHosts) && !empty(Nest.value(dbHosts,iface.get("hostid"),"host").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Interface with IP \"%1$s\" cannot have empty DNS name while having \"Use DNS\" property on \"%2$s\".",
							Nest.value(iface,"ip").$(),
							Nest.value(dbHosts,iface.get("hostid"),"host").$()
					));
				} else if (!empty(dbProxies) && !empty(Nest.value(dbProxies,iface.get("hostid"),"host").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Interface with IP \"%1$s\" cannot have empty DNS name while having \"Use DNS\" property on \"%2$s\".",
							Nest.value(iface,"ip").$(),
							Nest.value(dbProxies,iface.get("hostid"),"host").$()
					));
				} else {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Interface with IP \"%1$s\" cannot have empty DNS name.", Nest.value(iface,"ip").$()));
				}
			}

			if (isset(iface,"dns")) {
				checkDns(iface);
			}
			if (isset(iface,"ip")) {
				checkIp(iface);
			}
			if (isset(iface,"port")) {
				checkPort(iface);
			}

			if (update) {
				iface.clear();
				iface.putAll(updInterface);
			}
		}

		// check if any of the affected hosts are discovered
		if (update) {
			interfaces = extendObjects("interface", interfaces, new String[]{"hostid"});
		}
		checkValidator(rda_objectValues(interfaces, "hostid").valuesAsLong(), CValidator.init(new CHostNormalValidator(getSqlExecutor()),map(
			"message", _("Cannot update interface for discovered host \"%1$s\".")
		)));
	}
	
	/**
	 * Add interfaces.
	 *
	 * @param array ifaces multidimensional array with Interfaces data
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> interfaces) {
		checkInput(interfaces, "create");
		checkMainInterfacesOnCreate(interfaces);
		CArray<Long> interfaceIds = insert("interface", interfaces);
		return map("interfaceids", interfaceIds.valuesAsLong());
	}
	
	/**
	 * Update interfaces.
	 *
	 * @param array ifaces multidimensional array with Interfaces data
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> interfaces) {
		checkInput(interfaces, "update");
		checkMainInterfacesOnUpdate(interfaces);

		CArray<Map> data = array();
		for(Map iface : interfaces) {
			data.add(map(
				"values", iface,
				"where" , map("interfaceid", Nest.value(iface,"interfaceid").$())
			));
		}
		update("interface", data);

		return map("interfaceids", rda_objectValues(interfaces, "interfaceid").valuesAsLong());
	}
	
	protected Object clearValues(Map iface) {
		if (isset(iface,"port") && !"".equals(Nest.value(iface,"port").asString())) {
			Nest.value(iface,"port").$(ltrim(Nest.value(iface,"port").asString(), "0"));

			if ("".equals(Nest.value(iface,"port").asString())) {
				Nest.value(iface,"port").$(0);
			}
		}
		return iface;
	}
	
	/**
	 * Delete interfaces.
	 * Interface cannot be deleted if it's main interface and exists other interface of same type on same host.
	 * Interface cannot be deleted if it is used in items.
	 *
	 * @param array ifaceids
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... interfaceids) {
		if (empty(interfaceids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CHostIfaceGet options = new CHostIfaceGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setInterfaceIds(TArray.as(interfaceids).asLong());
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> dbInterfaces = get(options);
		for(Long interfaceId : interfaceids) {
			if (!isset(dbInterfaces,interfaceId)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
			}
		}

		checkMainInterfacesOnDelete(interfaceids);

		delete("interface", (Map)map("interfaceid", interfaceids));

		return map("interfaceids", interfaceids);
	}
	
	public CArray<Long[]> massAdd(CArray data) {
		CArray<Map> interfaces = Nest.value(data,"interfaces").asCArray();
		CArray<Map> hosts = Nest.value(data,"hosts").asCArray();
		CArray<Map> insertData = array();
		for(Map iface : interfaces) {
			for(Map host : hosts) {
				Map newInterface = Clone.deepcopy(iface);
				Nest.value(newInterface,"hostid").$(Nest.value(host,"hostid").$());
				insertData.add(newInterface);
			}
		}
		CArray<Long[]> ifaceIds = create(insertData);
		return map("interfaceids", ifaceIds.valuesAsLong());
	}
	
	protected void validateMassRemove(CArray data) {
		// check permissions
		checkHostPermissions(Nest.array(data,"hostids").asLong());

		// check interfaces
		for(Map iface : (CArray<Map>)Nest.value(data,"interfaces").asCArray()) {
			if (!isset(iface,"dns") || !isset(iface,"ip") || !isset(iface,"port")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect arguments passed to function."));
			}

			checkDns(iface);
			checkIp(iface);
			checkPort(iface);

			// check main interfaces
			CParamGet options = new CParamGet();
			options.setOutput(new String[]{"interfaceid"});
			options.setFilter("hostid", Nest.array(data,"hostids").asString());
			options.setFilter("ip", Nest.value(iface,"ip").asString());
			options.setFilter("dns", Nest.value(iface,"dns").asString());
			options.setFilter("port", Nest.value(iface,"port").asString());
			CArray<Map> interfacesToRemove = select(tableName(), options);
			if (!empty(interfacesToRemove)) {
				checkMainInterfacesOnDelete(rda_objectValues(interfacesToRemove, "interfaceid").valuesAsLong());
			}
		}
	}

	/**
	 * Remove hosts from interfaces.
	 *
	 * @param array _data
	 * @param array _data["interfaceids"]
	 * @param array _data["hostids"]
	 * @param array _data["templateids"]
	 * @return 
	 *
	 * @return array
	 */
	public CArray<Long[]> massRemove(CArray data) {
		validateMassRemove(data);

		for(Map iface : (CArray<Map>)Nest.value(data,"interfaces").asCArray()) {
			delete("interface", (Map)map(
				"hostid", Nest.array(data,"hostids").asLong(),
				"ip", Nest.value(iface,"ip").asString(),
				"dns", Nest.value(iface,"dns").asString(),
				"port", Nest.value(iface,"port").asString()
			));
		}

		return map("interfaceids", rda_objectValues(Nest.value(data,"interfaces").$(), "interfaceid").valuesAsLong());
	}

	/**
	 * Replace existing interfaces with input interfaces.
	 *
	 * @param host
	 */
	public void replaceHostInterfaces(Map host) {
		if (isset(host,"interfaces") && !is_null(Nest.value(host,"interfaces").$())) {
			checkHostInterfaces(Nest.value(host,"interfaces").asCArray(), Nest.value(host,"hostid").asString());

			CHostIfaceGet options = new CHostIfaceGet();
			options.setHostIds(Nest.value(host,"hostid").asLong());
			options.setOutput(API_OUTPUT_EXTEND);
			options.setPreserveKeys(true);
			options.setNopermissions(true);
			CArray<Map> interfacesToDelete = get(options);

			CArray<Map> interfacesToAdd = array();
			CArray<Map> interfacesToUpdate = array();

			for(Map iface : (CArray<Map>)Nest.value(host,"interfaces").asCArray()) {
				Nest.value(iface,"hostid").$(Nest.value(host,"hostid").$());
				if (!isset(iface,"interfaceid")) {
					interfacesToAdd.add(iface);
				} else if (isset(interfacesToDelete,iface.get("interfaceid"))) {
					interfacesToUpdate.add(iface);
					unset(interfacesToDelete,iface.get("interfaceid"));
				}
			}

			if (!empty(interfacesToUpdate)) {
				checkInput(interfacesToUpdate, "update");

				CArray data = array();
				for(Map iface : interfacesToUpdate) {
					data.add(map(
						"values", iface,
						"where", map("interfaceid", Nest.value(iface,"interfaceid").asString())
					));
				}
				update("interface", data);
			}

			if (!empty(interfacesToAdd)) {
				checkInput(interfacesToAdd, "create");
				insert("interface", interfacesToAdd);
			}

			if (!empty(interfacesToDelete)) {
				delete(rda_objectValues(interfacesToDelete, "interfaceid").valuesAsLong());
			}
		}
	}
	
	/**
	 * Validates the \"dns\" field.
	 *
	 * @throws APIException if the field is invalid.
	 *
	 * @param array iface
	 */
	protected void checkDns(Map iface) {
		if (rda_strlen(Nest.value(iface,"dns").asString()) > 64) {
			throw CDB.exception(
				RDA_API_ERROR_PARAMETERS,
				_n(
					"Maximum DNS name length is %1$d characters, \"%2$s\" is %3$d character.",
					"Maximum DNS name length is %1$d characters, \"%2$s\" is %3$d characters.",
					64,
					Nest.value(iface,"dns").asString(),
					rda_strlen(Nest.value(iface,"dns").asString())
				)
			);
		}

		if (!empty(Nest.value(iface,"dns").$()) && preg_match("^"+RDA_PREG_DNS_FORMAT+"$", Nest.value(iface,"dns").asString())==0) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect interface DNS parameter \"%s\" provided.", Nest.value(iface,"dns").$()));
		}
	}
	
	/**
	 * Validates the \"ip\" field.
	 *
	 * @throws APIException if the field is invalid.
	 *
	 * @param array iface
	 */
	protected void checkIp(Map iface) {
		CArray arr = new CArray();
		if (!rda_empty(Nest.value(iface,"ip").$()) && !validate_ip(Nest.value(iface,"ip").asString(), arr)
				&& preg_match("^"+RDA_PREG_MACRO_NAME_FORMAT+"$", Nest.value(iface,"ip").asString())==0
				&& preg_match("^"+RDA_PREG_EXPRESSION_USER_MACROS+"$", Nest.value(iface,"ip").asString())==0) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect interface IP parameter \"%s\" provided.", Nest.value(iface,"ip").$()));
		}
	}
	
	/**
	 * Validates the \"port\" field.
	 *
	 * @throws APIException if the field is empty or invalid.
	 *
	 * @param array iface
	 */
	protected void checkPort(Map iface) {
		if (!isset(iface,"port") || rda_empty(Nest.value(iface,"port").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Port cannot be empty for host interface."));
		}
		if (!validatePortNumberOrMacro(Nest.value(iface,"port").asString())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect interface port \"%s\" provided.", Nest.value(iface,"port").$()));
		}
	}
	
	/**
	 * Checks if the current user has access to the given hosts. Assumes the \"hostid\" field is valid.
	 *
	 * @throws APIException if the user doesn't have write permissions for the given hosts
	 *
	 * @param array _hostIds	an array of host IDs
	 */
	protected void checkHostPermissions(Long... hostIds) {
		if (!API.Host(this.idBean, this.getSqlExecutor()).isWritable(hostIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	private void checkHostInterfaces(CArray<Map> interfaces, String hostid) {
		CArray interfacesWithMissingData = array();

		for(Map iface : interfaces) {
			if (!isset(iface,"type") && !isset(iface,"main")) {
				interfacesWithMissingData.add(Nest.value(iface,"interfaceid").$());
			}
		}

		CArray<Map> dbInterfaces = null;
		if (!empty(interfacesWithMissingData)) {
			CHostIfaceGet options = new CHostIfaceGet();
			options.setInterfaceIds(interfacesWithMissingData.valuesAsLong());
			options.setOutput(new String[]{"main", "type"});
			options.setPreserveKeys(true);
			options.setNopermissions(true);
			dbInterfaces = get(options);
		}

		for (Entry<Object, Map> e : interfaces.entrySet()) {
		    Object id = e.getKey();
		    Map iface = e.getValue();
			if (isset(iface,"interfaceid") && isset(dbInterfaces,iface.get("interfaceid"))) {
				Nest.merge(iface, dbInterfaces.get(iface.get("interfaceid"))).into(interfaces.get(id));
			}
			Nest.value(interfaces,id,"hostid").$(hostid);
		}

		checkMainInterfaces(interfaces);
	}
	
	private void checkMainInterfacesOnCreate(CArray<Map> interfaces) {
		CArray hostIds = array();
		for(Map iface : interfaces) {
			Nest.value(hostIds,iface.get("hostid")).$(Nest.value(iface,"hostid").$());
		}

		CHostIfaceGet options = new CHostIfaceGet();
		options.setHostIds(hostIds.valuesAsLong());
		options.setOutput(new String[]{"hostid", "main", "type"});
		options.setPreserveKeys(true);
		options.setNopermissions(true);
		CArray<Map> dbInterfaces = get(options);
		interfaces = array_merge(dbInterfaces, interfaces);
		checkMainInterfaces(interfaces);
	}
	
	private void checkMainInterfacesOnUpdate(CArray<Map> interfaces) {
		CArray interfaceidsWithoutHostIds = array();
		CArray hostIds = array();
		// gather all hostids where interfaces should be checked
		for(Map iface : interfaces) {
			if (isset(iface,"type") || isset(iface,"main")) {
				if (isset(iface,"hostid")) {
					Nest.value(hostIds,iface.get("hostid")).$(Nest.value(iface,"hostid").$());
				} else {
					interfaceidsWithoutHostIds.add(Nest.value(iface,"interfaceid").$());
				}
			}
		}

		// gather missing host ids
		hostIds = array();
		if (!empty(interfaceidsWithoutHostIds)) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbResult = DBselect(getSqlExecutor(),
					"SELECT DISTINCT i.hostid FROM interface i WHERE "+ sqlParts.where.dbConditionInt("i.interfaceid", interfaceidsWithoutHostIds.valuesAsLong()),
					sqlParts.getNamedParams()
					);
			for(Map hostData : dbResult) {
				Nest.value(hostIds,hostData.get("hostid")).$(Nest.value(hostData,"hostid").$());
			}
		}

		CHostIfaceGet options = new CHostIfaceGet();
		options.setHostIds(hostIds.valuesAsLong());
		options.setOutput(new String[]{"hostid", "main", "type"});
		options.setPreserveKeys(true);
		options.setNopermissions(true);
		CArray<Map> dbInterfaces = get(options);

		// update interfaces from DB with data that will be updated.
		for(Map iface : interfaces) {
			if (isset(dbInterfaces,iface.get("interfaceid"))) {
				Nest.merge(dbInterfaces.get(iface.get("interfaceid")),interfaces.get(iface.get("interfaceid"))).into(dbInterfaces.get(iface.get("interfaceid")));
			}
		}

		checkMainInterfaces(dbInterfaces);
	}
	
	private void checkMainInterfacesOnDelete(Long... interfaceIds) {
		checkIfInterfaceHasItems(interfaceIds);

		CArray hostids = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbResult = DBselect(getSqlExecutor(),
				"SELECT DISTINCT i.hostid FROM interface i WHERE "+sqlParts.dual.dbConditionInt("i.interfaceid", interfaceIds),
				sqlParts.getNamedParams());
		for (Map hostData : dbResult) {
			Nest.value(hostids,hostData.get("hostid")).$(Nest.value(hostData,"hostid").$());
		}

		CHostIfaceGet options = new CHostIfaceGet();
		options.setHostIds(hostids.valuesAsLong());
		options.setOutput(new String[]{"hostid", "main", "type"});
		options.setPreserveKeys(true);
		options.setNopermissions(true);
		CArray<Map> dbInterfaces = get(options);

		for(Long interfaceId : interfaceIds) {
			unset(dbInterfaces, interfaceId);
		}

		checkMainInterfaces(dbInterfaces);
	}
	
	/**
	 * Check if main interfaces are correctly set for every interface type.
	 * Each host must either have only one main interface for each interface type, or have no interface of that type at all.
	 *
	 * @param array ifaces
	 */
	private void checkMainInterfaces(CArray<Map> interfaces) {
		CArray<CArray<Map>> interfaceTypes = array();
		for(Map iface : interfaces) {
			if (!isset(interfaceTypes,iface.get("hostid"))) {
				Nest.value(interfaceTypes,iface.get("hostid")).$(array());
			}

			if (!isset(Nest.value(interfaceTypes,iface.get("hostid"),iface.get("type")).$())) {
				Nest.value(interfaceTypes,iface.get("hostid"),iface.get("type")).$(map("main", 0, "all", 0));
			}

			if (Nest.value(iface,"main").asInteger() == INTERFACE_PRIMARY) {
				Nest.value(interfaceTypes,iface.get("hostid"),iface.get("type"),"main").$(
						Nest.value(interfaceTypes,iface.get("hostid"),iface.get("type"),"main").asInteger()+1
				);
			} else {
				Nest.value(interfaceTypes,iface.get("hostid"),iface.get("type"),"all").$(
						Nest.value(interfaceTypes,iface.get("hostid"),iface.get("type"),"all").asInteger()+1
				);
			}
		}

		for (Entry<Object, CArray<Map>> e : interfaceTypes.entrySet()) {
		    Object interfaceHostId = e.getKey();
		    CArray<Map> interfaceType = e.getValue();
		    for (Entry<Object, Map> eit : interfaceType.entrySet()) {
		        int type = Nest.as(eit.getKey()).asInteger();
		        Map counters = eit.getValue();
				if (!empty(Nest.value(counters,"all").$()) && Nest.value(counters,"main").asInteger()==0) {
					CHostGet hoptions = new CHostGet();
					hoptions.setHostIds(Nest.as(interfaceHostId).asLong());
					hoptions.setOutput(new String[]{"name"});
					hoptions.setPreserveKeys(true);
					hoptions.setNopermissions(true);
					CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
					Map host = reset(hosts);
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("No default interface for \"%1$s\" type on \"%2$s\".", hostInterfaceTypeNumToName(type), Nest.value(host,"name").$()));
				}

				if (Nest.value(counters,"main").asInteger() > 1) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Host cannot have more than one default interface of the same type."));
				}
			}
		}
	}

	private void checkIfInterfaceHasItems(Long... interfaceIds) {
		CItemGet options = new CItemGet();
		options.setOutput(new String[]{"name"});
		options.setSelectHosts(new String[]{"name"});
		options.setInterfaceIds(interfaceIds);
		options.setPreserveKeys(true);
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(options);
		for(Map item : items) {
			Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Interface is linked to item \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(host,"name").$()));
		}
	}
	
	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		
		if (params.getCountOutput() == null && Nest.value(params,"selectHosts").$() != null) {
			addQuerySelect("hi.hostid", sqlParts);
		}
	}

	@Override
	protected void addRelatedObjects(CHostIfaceGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] interfaceIds = result.keysAsLong();	
		// adding hosts
		if (!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())) {
			CRelationMap relationMap = createRelationMap(result, "interfaceid", "hostid");
			CHostGet hostParams = new CHostGet();
			hostParams.setOutput(params.getSelectHosts());
			hostParams.setHostIds(relationMap.getRelatedLongIds());
			hostParams.setPreserveKeys(true);
			CArray<Map> datas = API.Host(this.idBean, this.getSqlExecutor()).get(hostParams);
			relationMap.mapMany(result, datas, "hosts", params.getLimitSelects());
		}
		
		// adding items
		if (!is_null(params.getSelectItems())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectItems())) {
				CItemGet itemParams = new CItemGet();
				itemParams.setOutput(outputExtend("items", new String[]{"itemid", "interfaceid"}, params.getSelectItems()));
				itemParams.setInterfaceIds(interfaceIds);
				itemParams.setPreserveKeys(true);
				itemParams.setFilter("flags");
				CArray<Map> datas = API.Item(this.idBean, this.getSqlExecutor()).get(itemParams);
	
				CRelationMap relationMap = createRelationMap(datas, "interfaceid", "itemid");
				unsetExtraFields(datas, new String[] {"interfaceid", "itemid"}, params.getSelectItems());
				relationMap.mapMany(result, datas, "items", params.getLimitSelects());
			} else {
				CItemGet itemParams = new CItemGet();
				itemParams.setInterfaceIds(interfaceIds);
				itemParams.setFilter("flags");
				itemParams.setCountOutput(true);
				itemParams.setGroupCount(true);
				
				CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(itemParams);
				for(Entry<Object, Map> e: result.entrySet()){
					Object itemId = e.getKey();
					Map item = (Map)items.get(itemId);
					e.getValue().put("items", isset(item)? item.get("rowscount"): 0);
				}
			}
		}
	}
	
}
