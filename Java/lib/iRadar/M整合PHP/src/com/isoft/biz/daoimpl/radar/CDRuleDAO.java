package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DCHECK;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_DRULE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_MD5;
import static com.isoft.iradar.inc.Defines.ITEM_AUTHPROTOCOL_SHA;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_AES;
import static com.isoft.iradar.inc.Defines.ITEM_PRIVPROTOCOL_DES;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV;
import static com.isoft.iradar.inc.Defines.ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.SVC_AGENT;
import static com.isoft.iradar.inc.Defines.SVC_ICMPPING;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv1;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv2c;
import static com.isoft.iradar.inc.Defines.SVC_SNMPv3;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.ValidateUtil.validate_ip_range;
import static com.isoft.iradar.inc.ValidateUtil.validate_port_list;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDCheckGet;
import com.isoft.iradar.model.params.CDHostGet;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CProxyGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * Class containing methods for operations with discovery rules.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CDRuleDAO extends CCoreLongKeyDAO<CDRuleGet> {

	public CDRuleDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "drules", "dr", new String[]{"druleid", "name"});
	}

	@Override
	public <T> T get(CDRuleGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("drules", "dr.druleid");
		sqlParts.from.put("drules", "drules dr");
		
		if (CWebUser.getType() < USER_TYPE_IRADAR_ADMIN) {
			return (T)array();
		}
		
		// druleids
		if (!is_null(params.getDruleIds())) {
			sqlParts.where.dbConditionInt("druleid","dr.druleid",params.getDruleIds());
		}

		// dhostids
		if (!is_null(params.getDhostIds())) {
			sqlParts.select.put("dhostid","dh.dhostid");
			sqlParts.from.put("dhosts","dhosts dh");
			sqlParts.where.dbConditionInt("dhostid","dh.dhostid",params.getDhostIds());
			sqlParts.where.put("dhdr.tenantid","dh.tenantid=dr.tenantid");
			sqlParts.where.put("dhdr","dh.druleid=dr.druleid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dhostid","dh.dhostid");
			}
		}

		// dserviceids
		if (!is_null(params.getDserviceIds())) {
			sqlParts.select.put("dserviceid","ds.dserviceid");
			sqlParts.from.put("dhosts","dhosts dh");
			sqlParts.from.put("dservices","dservices ds");

			sqlParts.where.dbConditionInt("dserviceid","ds.dserviceid",params.getDserviceIds());
			sqlParts.where.put("dhdr.tenantid","dh.tenantid=dr.tenantid");
			sqlParts.where.put("dhdr","dh.druleid=dr.druleid");
			sqlParts.where.put("dhds.tenantid","dh.tenantid=ds.tenantid");
			sqlParts.where.put("dhds","dh.dhostid=ds.dhostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("dserviceid","ds.dserviceid");
			}
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("drules dr", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("drules dr", params, sqlParts);
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
				Long id = (Long)row.get("druleid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				// dhostids
				if (isset(row.get("dhostid")) && is_null(params.getSelectDHosts())) {
					if (!isset(result.get(id).get("dhosts"))) {
						result.get(id).put("dhosts", new CArray());
					}
					((CArray)result.get(id).get("dhosts")).add(map("dhostid", row.remove("dhostid")));
				}
				// dchecks
				if (isset(row.get("dcheckid")) && is_null(params.getSelectDChecks())) {
					if (!isset(result.get(id).get("dchecks"))) {
						result.get(id).put("dchecks", new CArray());
					}
					((CArray)result.get(id).get("dchecks")).add(map("dcheckid", row.remove("dcheckid")));
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
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	@Override
	public boolean exists(CArray object) {
		String name = null;
		Long druleid = null;
		Long[] druleids = null;
		
		CDRuleGet options = new CDRuleGet();
		options.setOutput(new String[]{"druleid", "name"});
		options.setNopermissions(true);
		options.setLimit(2);
		
		if (isset(object, "name")) {
			name = Nest.value(object, "name").asString();
			druleid = Nest.value(object, "druleid").asLong();//只在更新或创建操作判断名称的时候起作用
			options.setFilter("name", name);
		}
		if (isset(object, "druleids")) {
			druleids = Nest.array(object,"druleids").asLong();
			options.setDruleIds(druleids);
		}
		
		CArray<Map> objs = get(options);
		
		boolean exists = !empty(objs); 
		
		//解决因数据库不区分大小写，造成有名称为a时， A也不能创建的BUG
		if(exists && druleids==null) { //当做更新或创建操作时，不会传druleids参数
			exists = false;
			for(Map obj: objs) {
				String oName = Nest.value(obj, "name").asString();
				Long oDruleid = Nest.value(obj, "druleid").asLong();
				
				boolean nameEquals = (Cphp.bccomp(oName, name) == 0);
				if(druleid == null) {//创建
					if(nameEquals) {
						return true; //只要是完全相等的，就是存在
					}
				}else {//更新
					if(druleid==oDruleid) {
						if(nameEquals) {
							return false; //更新自己不能是已存在
						}
					}else {
						if(nameEquals) {
							return true; //与别人名字相同就是存在
						}
					}
				}
			}
		}
		return exists;
	}
	
	public void checkInput(CArray<Map> dRules) {
		if (empty(dRules)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input."));
		}

		if (CWebUser.getType() < USER_TYPE_IRADAR_ADMIN) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
		}

		CArray proxies = array();
		for(Map dRule : dRules) {
			if (!isset(dRule,"iprange")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("IP range cannot be empty."));
			} else if (!validate_ip_range(Nest.value(dRule,"iprange").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect IP range \"%s\".", Nest.value(dRule,"iprange").$()));
			}

			if (isset(dRule,"delay") && Nest.value(dRule,"delay").asInteger() < 0) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect delay."));
			}

			if (isset(dRule,"status") && ((Nest.value(dRule,"status").asInteger() != DRULE_STATUS_DISABLED) && (Nest.value(dRule,"status").asInteger() != DRULE_STATUS_ACTIVE))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect status."));
			}

			if (empty(Nest.value(dRule,"dchecks").$())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot save discovery rule without checks."));
			}

			validateDChecks(Clone.deepcopy(Nest.value(dRule,"dchecks").asCArray()));

			if (isset(dRule,"proxy_hostid") && !empty(Nest.value(dRule,"proxy_hostid").$())) {
				proxies.add(Nest.value(dRule,"proxy_hostid").$());
			}
		}

		if (!empty(proxies)) {
			CProxyGet options = new CProxyGet();
			options.setProxyIds(proxies.valuesAsLong());
			options.setOutput(new String[]{"proxyid"});
			options.setPreserveKeys(true);
			CArray<Map> proxiesDB = API.Proxy(this.idBean, this.getSqlExecutor()).get(options);
			for(Object proxy : proxies) {
				if (!isset(proxiesDB,proxy)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect proxyid."));
				}
			}
		}
	}
	
	protected void validateDChecks(CArray<Map> dChecks) {
		int uniq = 0;
		for (Entry<Object, Map> e : dChecks.entrySet()) {
		    Object dcnum = e.getKey();
		    Map dCheck = e.getValue();
			if (isset(dCheck,"uniq") && (Nest.value(dCheck,"uniq").asInteger() == 1)) uniq++;

			if (isset(dCheck,"ports") && !validate_port_list(Nest.value(dCheck,"ports").asString())) {
				if (!(Nest.value(dCheck, "type").asInteger() == SVC_ICMPPING 
						&& Nest.value(dCheck, "ports").asInteger() == 0)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect port range."));
				}				
			}

			switch (Nest.value(dCheck,"type").asInteger()) {
				case SVC_AGENT:
					if (!isset(dCheck,"key_")) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect key."));
					}

					CItemKey itemKey  = new CItemKey(Nest.value(dCheck,"key_").asString());
					if (!itemKey.isValid()) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Invalid key \"%1$s\": %2$s.",
							Nest.value(dCheck,"key_").$(),
							itemKey.getError()
						));
					}
					break;
				case SVC_SNMPv1:
				case SVC_SNMPv2c:
					if (!isset(dCheck,"snmp_community") || rda_empty(Nest.value(dCheck,"snmp_community").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect SNMP community."));
					}
				case SVC_SNMPv3:
					if (!isset(dCheck,"key_") || rda_empty(Nest.value(dCheck,"key_").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect SNMP OID."));
					}
					break;
			}

			// set default values for snmpv3 fields
			if (!isset(dCheck,"snmpv3_securitylevel")) {
				Nest.value(dCheck,"snmpv3_securitylevel").$(ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV);
			}

			switch (Nest.value(dCheck,"snmpv3_securitylevel").asInteger()) {
				case ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV:
					Nest.value(dChecks, dcnum, "snmpv3_authprotocol").$(ITEM_AUTHPROTOCOL_MD5);
					Nest.value(dChecks, dcnum, "snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
					Nest.value(dChecks, dcnum, "snmpv3_authpassphrase").$("");
					Nest.value(dChecks, dcnum, "snmpv3_privpassphrase").$("");
					break;
				case ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV:
					Nest.value(dChecks, dcnum, "snmpv3_privprotocol").$(ITEM_PRIVPROTOCOL_DES);
					Nest.value(dChecks, dcnum, "snmpv3_privpassphrase").$("");
					break;
			}

			// validate snmpv3 fields
			if (isset(dCheck,"snmpv3_securitylevel") && Nest.value(dCheck,"snmpv3_securitylevel").asInteger() != ITEM_SNMPV3_SECURITYLEVEL_NOAUTHNOPRIV) {
				// snmpv3 authprotocol
				if (str_in_array(Nest.value(dCheck,"snmpv3_securitylevel").$(), array(ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV, ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV))) {
					if (rda_empty(Nest.value(dCheck,"snmpv3_authprotocol").$())
							|| (isset(dCheck,"snmpv3_authprotocol")
									&& !str_in_array(Nest.value(dCheck,"snmpv3_authprotocol").$(), array(ITEM_AUTHPROTOCOL_MD5, ITEM_AUTHPROTOCOL_SHA)))) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect authentication protocol for discovery rule \"%1$s\".", Nest.value(dCheck,"name").$()));
					}
				}

				// snmpv3 privprotocol
				if (Nest.value(dCheck,"snmpv3_securitylevel").asInteger() == ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV) {
					if (rda_empty(Nest.value(dCheck,"snmpv3_privprotocol").$())
							|| (isset(dCheck,"snmpv3_privprotocol")
									&& !str_in_array(Nest.value(dCheck,"snmpv3_privprotocol").$(), array(ITEM_PRIVPROTOCOL_DES, ITEM_PRIVPROTOCOL_AES)))) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect privacy protocol for discovery rule \"%1$s\".", Nest.value(dCheck,"name").$()));
					}
				}
			}

			validateDuplicateChecks(Clone.deepcopy(dChecks));
		}

		if (uniq > 1) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Only one check can be unique."));
		}
	}
	
	protected void validateRequiredFields(CArray<Map>dRules, String on) {
		if ("update".equals(on)) {
			for(Map dRule : dRules) {
				if (!isset(dRule,"druleid") || rda_empty(Nest.value(dRule,"druleid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Field \"druleid\" is required."));
				}
			}
		} else {
			for(Map dRule : dRules) {
				if (!isset(dRule,"name") || rda_empty(Nest.value(dRule,"name").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Field \"name\" is required."));
				}
			}
		}
	}
	
	protected void validateDuplicateChecks(CArray<Map> dChecks) {
		Map<String, Object> defaultValues = CDB.getDefaults("dchecks");
		for(Map dCheck : dChecks) {
			for(Entry<String, Object> e:defaultValues.entrySet()){
				if(dCheck.containsKey(e.getKey())){
					dCheck.put(e.getKey(), e.getValue());
				}
			}
			unset(dCheck,"uniq");
		}

		Map current = null;
		while (!empty(current  = array_pop(dChecks))) {
			for(Map<Object, Object> dCheck : dChecks) {
				boolean equal = true;
				for (Entry<Object, Object> e2 : dCheck.entrySet()) {
				    Object fieldName = e2.getKey();
				    Object dCheckField = e2.getValue();
					if (isset(current,fieldName) && (strcmp(Nest.as(dCheckField).asString(), Nest.as(current.get(fieldName)).asString()) != 0)) {
						equal = false;
						break;
					}
				}
				if (equal) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Checks should be unique."));
				}
			}
		}
	}

	/**
	 * Create new discovery rules.
	 *
	 * @param array(
	 *  name => string,
	 *  proxy_hostid => int,
	 *  iprange => string,
	 *  delay => string,
	 *  status => int,
	 *  dchecks => array(
	 *  	array(
	 *  		type => int,
	 *  		ports => string,
	 *  		key_ => string,
	 *  		snmp_community => string,
	 *  		snmpv3_securityname => string,
	 *  		snmpv3_securitylevel => int,
	 *  		snmpv3_authpassphrase => string,
	 *  		snmpv3_privpassphrase => string,
	 *  		uniq => int,
	 *  	), ...
	 *  )
	 * ) drules
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> dRules) {
		checkInput(dRules);
		validateRequiredFields(dRules, "create");

		// checking to the duplicate names
		for(Map dRule : dRules) {
			if (exists(CArray.valueOf(dRule))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Discovery rule \"%s\" already exists.", Nest.value(dRule,"name").$()));
			}
		}

		CArray<Long> druleids = insert("drules", Clone.deepcopy(dRules));

		CArray dChecksCreate = array();
		for (Entry<Object, Map> e : dRules.entrySet()) {
		    Object dNum = e.getKey();
		    Map dRule = e.getValue();
			for(Map dCheck : (CArray<Map>)Nest.value(dRule,"dchecks").asCArray()) {
				Nest.value(dCheck,"druleid").$(druleids.get(dNum));
				dChecksCreate.add(dCheck);
			}
		}

		insert("dchecks", dChecksCreate);

		return map("druleids", druleids.valuesAsLong());
	}
	
	/**
	 * Update existing drules.
	 *
	 * @param array(
	 * 	druleid => int,
	 *  name => string,
	 *  proxy_hostid => int,
	 *  iprange => string,
	 *  delay => string,
	 *  status => int,
	 *  dchecks => array(
	 *  	array(
	 * 			dcheckid => int,
	 *  		type => int,
	 *  		ports => string,
	 *  		key_ => string,
	 *  		snmp_community => string,
	 *  		snmpv3_securityname => string,
	 *  		snmpv3_securitylevel => int,
	 *  		snmpv3_authpassphrase => string,
	 *  		snmpv3_privpassphrase => string,
	 *  		uniq => int,
	 *  	), ...
	 *  )
	 * ) dRules
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> dRules) {
		checkInput(dRules);
		validateRequiredFields(dRules, "update");

		CArray dRuleIds = rda_objectValues(dRules, "druleid");

		CDRuleGet droptions = new CDRuleGet();
		droptions.setDruleIds(dRuleIds.valuesAsLong());
		droptions.setOutput(API_OUTPUT_EXTEND);
		droptions.setSelectDChecks(API_OUTPUT_EXTEND);
		droptions.setEditable(true);
		droptions.setPreserveKeys(true);
		CArray<Map> dRulesDb = API.DRule(this.idBean, this.getSqlExecutor()).get(droptions);

		Map<String, Object> defaultValues = CDB.getDefaults("dchecks");

		CArray<Map> dRulesUpdate = array();

		for(Map dRule : dRules) {
			// validate drule duplicate names
			if (strcmp(Nest.value(dRulesDb,dRule.get("druleid"),"name").asString(), Nest.value(dRule,"name").asString()) != 0) {
				if (exists(CArray.valueOf(dRule))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Discovery rule \"%s\" already exists.", Nest.value(dRule,"name").$()));
				}
			}

			dRulesUpdate.add(map(
				"values", dRule,
				"where", map("druleid", Nest.value(dRule,"druleid").$())
			));

			// update dchecks
			CArray dbChecks = Nest.value(dRulesDb,dRule.get("druleid"),"dchecks").asCArray();

			CArray newChecks = array();

			CArray<Map> dchecks = Clone.deepcopy(Nest.value(dRule,"dchecks").asCArray());
			for (Entry<Object, Map> e : dchecks.entrySet()) {
			    Object cnum = e.getKey();
			    Map check = e.getValue();
				if (!isset(check,"druleid")) {
					Nest.value(check,"druleid").$(Nest.value(dRule,"druleid").$());
					unset(check,"dcheckid");
					newChecks.add(array_merge(defaultValues, check));
					unset(Nest.value(dRule,"dchecks").asCArray(),cnum);
				}
			}

			CArray delDCheckIds = array_diff(
				rda_objectValues(dbChecks, "dcheckid"),
				rda_objectValues(Nest.value(dRule,"dchecks").$(), "dcheckid")
			);

			if (!empty(delDCheckIds)) {
				deleteActionConditions(delDCheckIds.valuesAsLong());
			}

			replace("dchecks", dbChecks, array_merge(Nest.value(dRule,"dchecks").asCArray(), newChecks));
		}

		update("drules", dRulesUpdate);

		return map("druleids", dRuleIds.valuesAsLong());
	}

	/**
	 * Delete drules.
	 *
	 * @param array druleIds
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> delete(Long... druleIds) {
		validateDelete(druleIds);
		CArray actionIds = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbActions = DBselect(getSqlExecutor(),
			"SELECT DISTINCT actionid"+
			" FROM conditions"+
			" WHERE conditiontype="+CONDITION_TYPE_DRULE+
				" AND "+sqlParts.dual.dbConditionInt("value", druleIds)+
			" ORDER BY actionid",
			sqlParts.getNamedParams()
		);
		for (Map dbAction : dbActions) {
			actionIds.add(Nest.value(dbAction,"actionid").$());
		}

		if (!empty(actionIds)) {
			update("actions", array((Map)map(
				"values", map("status", ACTION_STATUS_DISABLED),
				"where", map("actionid", actionIds.valuesAsLong())
			)));

			delete("conditions", (Map)map(
				"conditiontype" , CONDITION_TYPE_DRULE,
				"value", druleIds
			));
		}

		boolean result = delete("drules", (Map)map("druleid", druleIds));
		if (result) {
			for(Long druleId : druleIds) {
				add_audit(this.idBean, getSqlExecutor(),AUDIT_ACTION_DELETE, AUDIT_RESOURCE_DISCOVERY_RULE, "["+druleId+"]");
			}
		}

		return map("druleids", druleIds);
	}

	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array druleIds
	 *
	 * @return void
	 */
	protected void validateDelete(Long...  druleIds) {
		if (empty(druleIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}
		checkDrulePermissions(druleIds);
	}
	
	/**
	 * Delete related action conditions.
	 *
	 * @param array dCheckIds
	 */
	protected void deleteActionConditions(Long...  dCheckIds) {
		CArray actionIds = array();
		
		// conditions
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbActions = DBselect(getSqlExecutor(),
			"SELECT DISTINCT c.actionid"+
			" FROM conditions c"+
			" WHERE c.conditiontype="+CONDITION_TYPE_DCHECK+
				" AND "+sqlParts.dual.dbConditionInt("c.value", dCheckIds)+
			" ORDER BY c.actionid",
			sqlParts.getNamedParams()
		);
		for (Map dbAction : dbActions) {
			actionIds.add(Nest.value(dbAction,"actionid").$());
		}

		// disabling actions with deleted conditions
		if (!empty(actionIds)) {
			update("actions", array((Map)map(
				"values", map("status", ACTION_STATUS_DISABLED),
				"where", map("actionid", actionIds.valuesAsLong())
			)));

			delete("conditions", (Map)map(
				"conditiontype", CONDITION_TYPE_DCHECK,
				"value", dCheckIds
			));
		}
	}
	
	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CDRuleGet options = new CDRuleGet();
		options.setDruleIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CDRuleGet options = new CDRuleGet();
		options.setDruleIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void addRelatedObjects(CDRuleGet params, CArray<Map> result) {
		Long[] druleids = result.keysAsLong();
		
		// Adding Discovery Checks
		if (!is_null(params.getSelectDChecks())) {
			CDCheckGet dcheckParams = new CDCheckGet();
			if(!API_OUTPUT_COUNT.equals(params.getSelectDChecks())){
				CRelationMap relationMap = createRelationMap(result, "druleid", "dcheckid", "dchecks");
				dcheckParams.setOutput(params.getSelectDChecks());
				dcheckParams.setDcheckIds(relationMap.getRelatedLongIds());
				dcheckParams.setNopermissions(true);
				dcheckParams.setPreserveKeys(true);
				CArray<Map> dchecks = API.DCheck(this.idBean, getSqlExecutor()).get(dcheckParams);
				if (!is_null(params.getLimitSelects())) {
					order_result(dchecks, "dcheckid");
				}
				relationMap.mapMany(result, dchecks, "dchecks", params.getLimitSelects());
			} else {
				dcheckParams.setDruleIds(druleids);
				dcheckParams.setNopermissions(true);
				dcheckParams.setCountOutput(true);
				dcheckParams.setGroupCount(true);
				CArray<Map> dchecks = API.DCheck(this.idBean, getSqlExecutor()).get(dcheckParams);
				for(Entry<Object, Map> e: result.entrySet()){
					Object druleid = e.getKey();
					Map drule = e.getValue();
					if(dchecks.containsKey(druleid)){
						drule.put("dchecks", drule.get("rowscount"));
					} else {
						drule.put("dchecks", 0);
					}
				}
			}
		}
		
		// Adding Discovery Hosts
		if (!is_null(params.getSelectDHosts())) {
			CDHostGet dhostParams = new CDHostGet();
			if(!API_OUTPUT_COUNT.equals(params.getSelectDHosts())){
				CRelationMap relationMap = createRelationMap(result, "druleid", "dhostid", "dhosts");
				dhostParams.setOutput(params.getSelectDHosts());
				dhostParams.setDhostIds(relationMap.getRelatedLongIds());
				dhostParams.setPreserveKeys(true);
				CArray<Map> dhosts = API.DHost(this.idBean, getSqlExecutor()).get(dhostParams);
				if (!is_null(params.getLimitSelects())) {
					order_result(dhosts, "dhostid");
				}
				relationMap.mapMany(result, dhosts, "dhosts", params.getLimitSelects());
			} else {
				dhostParams.setDruleIds(druleids);
				dhostParams.setCountOutput(true);
				dhostParams.setGroupCount(true);				
				CArray<Map> dhosts = API.DHost(this.idBean, getSqlExecutor()).get(dhostParams);
				for(Entry<Object, Map> e: result.entrySet()){
					Object druleid = e.getKey();
					Map drule = e.getValue();
					if(dhosts.containsKey(druleid)){
						drule.put("dhosts", drule.get("rowscount"));
					} else {
						drule.put("dhosts", 0);
					}
				}
			}
		}
	}

	/**
	 * Checks if the current user has access to given discovery rules.
	 *
	 * @throws APIException if the user doesn't have write permissions for discovery rules.
	 *
	 * @param array druleIds
	 *
	 * @return void
	 */
	protected void checkDrulePermissions(Long... druleIds) {
		if (!isWritable(druleIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
}
