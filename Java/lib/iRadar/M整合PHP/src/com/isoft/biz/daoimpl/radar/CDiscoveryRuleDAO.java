package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetchColumn;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_RULE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ItemsUtil.get_same_applications_for_host;
import static com.isoft.iradar.inc.ItemsUtil.httpItemExists;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.ValidateUtil.validateNumber;
import static com.isoft.iradar.inc.ValidateUtil.validateUserMacro;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphPrototypeGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostPrototypeGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CItemPrototypeGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTriggerPrototypeGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with discovery rules.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CDiscoveryRuleDAO extends CItemGeneralDAO<CDiscoveryRuleGet> {
	
	protected static int MIN_LIFETIME = 0;
	protected static int MAX_LIFETIME = 3650;
	
	public CDiscoveryRuleDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "items",  "i", new String[]{"itemid", "name", "key_", "delay", "type", "status"});
		
		this.errorMessages = array_merge(errorMessages, map(
			ERROR_EXISTS_TEMPLATE, _("Discovery rule \"%1$s\" already exists on \"%2$s\", inherited from another template."),
			ERROR_EXISTS, _("Discovery rule \"%1$s\" already exists on \"%2$s\"."),
			ERROR_INVALID_KEY, _("Invalid key \"%1$s\" for discovery rule \"%2$s\" on \"%3$s\": %4$s.")
		));
	}

	@Override
	public <T> T get(CDiscoveryRuleGet params) {
		int userType = CWebUser.getType();
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("items", "i.itemid");
		sqlParts.from.put("items", "items i");
		sqlParts.where.put("i.flags="+RDA_FLAG_DISCOVERY_RULE);
		
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
					" WHERE i.tenantid=hgg.tenantid"+
					" AND i.hostid=hgg.hostid"+
					" GROUP BY hgg.hostid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+
						" AND MAX(r.permission)>="+permission+
					")");
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
			if(!API_OUTPUT_EXTEND.equals(params.getOutput())){
				sqlParts.select.put("hostid","i.hostid");
			}

			sqlParts.where.dbConditionInt("hostid","i.hostid",params.getHostIds());

			if (params.getGroupCount() != null) {
				sqlParts.group.put("i","i.hostid");
			}
		}

		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.where.dbConditionInt("itemid","i.itemid",params.getItemIds());
		}

		// interfaceids
		if (!is_null(params.getInterfaceIds())) {
			if(!API_OUTPUT_EXTEND.equals(params.getOutput())){
				sqlParts.select.put("interfaceid","i.interfaceid");
			}

			sqlParts.where.dbConditionInt("interfaceid","i.interfaceid",params.getInterfaceIds());

			if (params.getGroupCount() != null) {
				sqlParts.group.put("i","i.interfaceid");
			}
		}

		// inherited
		if (!is_null(params.getInherited())) {
			if (params.getInherited()) {
				sqlParts.where.put("i.templateid IS NOT NULL");
			} else {
				sqlParts.where.put("i.templateid IS NULL");
			}
		}

		// templated
		if (!is_null(params.getTemplated())) {
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");

			if (params.getTemplated()) {
				sqlParts.where.put("h.status="+HOST_STATUS_TEMPLATE);
			} else {
				sqlParts.where.put("h.status<>"+HOST_STATUS_TEMPLATE);
			}
		}

		// monitored
		if (!is_null(params.getMonitored())) {
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");

			if (params.getMonitored()) {
				sqlParts.where.put("h.status="+HOST_STATUS_MONITORED);
				sqlParts.where.put("i.status="+ITEM_STATUS_ACTIVE);
			} else {
				sqlParts.where.put("(h.status<>"+HOST_STATUS_MONITORED+" OR i.status<>"+ITEM_STATUS_ACTIVE+")");
			}
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("items i", params, sqlParts);
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("items i", params, sqlParts);
			if (isset(params.getFilter().get("host"))) {
				sqlParts.from.put("hosts","hosts h");
				sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
				sqlParts.where.put("hi","h.hostid=i.hostid");
				sqlParts.where.dbConditionInt("h","h.host",TArray.as(params.getFilter().get("host")).asLong());
			}
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
				Long id = (Long)row.get("itemid");
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				// hostids
				if (isset(row,"hostid") && is_null(params.getSelectHosts())) {
					if (!isset(Nest.value(result,id,"hosts").$())) {
						result.get(id).put("hosts", new CArray());
					}
					Nest.value(result,id,"hosts").asCArray().add(map("hostid", row.get("hostid")));
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
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}

	@Override
	public boolean exists(CArray object) {
		CDiscoveryRuleGet options = new CDiscoveryRuleGet();
		options.setFilter("key_", Nest.value(object,"key_").asString());
		options.setOutput(new String[]{"itemid"});
		options.setNopermissions(true);
		options.setLimit(1);
		
		if (isset(object, "hostid")) {
			options.setHostIds(Nest.value(object,"hostid").asLong());
		}
		if (isset(object, "host")) {
			options.setFilter("host", Nest.value(object,"host").asString());
		}
		
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	/**
	 * Add DiscoveryRule.
	 *
	 * @param array items
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> items) {
		checkInput(items);
		createReal(items);
		inherit(items);
		return map("itemids", rda_objectValues(items, "itemid").valuesAsLong());
	}
	
	/**
	 * Update DiscoveryRule.
	 *
	 * @param array items
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> items) {
		checkInput(items, true);
		updateReal(items);
		inherit(items);
		return map("itemids", rda_objectValues(items, "itemid").valuesAsLong());
	}
	
	/**
	 * Delete DiscoveryRules.
	 *
	 * @param array ruleids
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... ruleids) {
		return delete(false, ruleids);
	}

	public CArray<Long[]> delete(boolean nopermissions, Long... sruleids) {
		if (empty(sruleids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CArray delRuleIds = rda_toArray(sruleids);
		CArray<Long> ruleids = rda_toHash(sruleids);

		CDiscoveryRuleGet options = new CDiscoveryRuleGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setItemIds(ruleids.valuesAsLong());
		options.setEditable(true);
		options.setPreserveKeys(true);
		options.setSelectHosts(new String[]{"name"});
		CArray<Map> delRules = get(options);

		// TODO: remove nopermissions hack
		if (!nopermissions) {
			for(Long ruleid : ruleids) {
				if (!isset(delRules,ruleid)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
				if (Nest.value(delRules,ruleid,"templateid").asInteger() != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete templated items."));
				}
			}
		}

		// get child discovery rules
		CArray<Long> parentItemids = Clone.deepcopy(ruleids);
		CArray<Long> childTuleids = array();
		SqlBuilder sqlParts = null;
		CArray<Map> dbItems = null;
		do {
			sqlParts = new SqlBuilder();
			dbItems = DBselect(getSqlExecutor(),
					"SELECT i.itemid FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.templateid", parentItemids.valuesAsLong()),
					sqlParts.getNamedParams()
				);
			parentItemids = array();
			for (Map dbItem : dbItems) {
				Nest.value(parentItemids,dbItem.get("itemid")).$(Nest.value(dbItem,"itemid").$());
				Nest.value(childTuleids,dbItem.get("itemid")).$(Nest.value(dbItem,"itemid").$());
			}
		} while (!empty(parentItemids));

		options = new CDiscoveryRuleGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setItemIds(childTuleids.valuesAsLong());
		options.setNopermissions(true);
		options.setPreserveKeys(true);
		options.setSelectHosts(new String[]{"name"});
		CArray<Map> delRulesChilds = get(options);

		delRules = array_merge(delRules, delRulesChilds);
		ruleids = array_merge(ruleids, childTuleids);

		CArray iprototypeids = array();
		sqlParts = new SqlBuilder();
		dbItems = DBselect(getSqlExecutor(),
			"SELECT i.itemid"+
			" FROM item_discovery id,items i"+
			" WHERE i.tenantid=id.tenantid"+
				" AND i.itemid=id.itemid"+
				" AND "+sqlParts.dual.dbConditionInt("parent_itemid", ruleids.valuesAsLong()),
			sqlParts.getNamedParams()
		);
		for (Map item : dbItems) {
			Nest.value(iprototypeids,item.get("itemid")).$(Nest.value(item,"itemid").$());
		}
		if (!empty(iprototypeids)) {
			if (empty(API.ItemPrototype(this.idBean, this.getSqlExecutor()).delete(true, iprototypeids.valuesAsLong()))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete discovery rule"));
			}
		}

		// delete host prototypes
		sqlParts = new SqlBuilder();
		CArray hostPrototypeIds = DBfetchColumn(DBselect(getSqlExecutor(),
			"SELECT hd.hostid"+
			" FROM host_discovery hd"+
			" WHERE "+sqlParts.dual.dbConditionInt("hd.parent_itemid", ruleids.valuesAsLong()),
			sqlParts.getNamedParams()
		), "hostid");
		if (!empty(hostPrototypeIds)) {
			if (empty(API.HostPrototype(this.idBean, this.getSqlExecutor()).delete(true, hostPrototypeIds))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete host prototype."));
			}
		}

		// delete LLD rules
		delete("items", (Map)map("itemid", ruleids.valuesAsLong()));

		return map("ruleids", delRuleIds.valuesAsLong());
	}
	
	/**
	 * Copies the given discovery rules to the specified hosts.
	 *
	 * @throws APIException if no discovery rule IDs or host IDs are given or
	 * the user doesn't have the necessary permissions.
	 *
	 * @param array data
	 * @param array data["discoveryruleids"]	An array of item ids to be cloned
	 * @param array data["hostids"]			An array of host ids were the items should be cloned to
	 *
	 * @return bool
	 */
	public boolean copy(Map data) {
		// validate data
		if (!isset(data,"discoveryids") || empty(Nest.value(data,"discoveryids").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No discovery rule IDs given."));
		}
		if (!isset(data,"hostids") || empty(Nest.value(data,"hostids").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No host IDs given."));
		}

		// check if all hosts exist and are writable
		if (!API.Host(this.idBean, this.getSqlExecutor()).isWritable(Nest.array(data,"hostids").asLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		// check if the given discovery rules exist
		if (!isReadable(Nest.array(data,"discoveryids").asLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		// copy
		for(Long discoveryid : (CArray<Long>)Nest.value(data,"discoveryids").asCArray()) {
			for(Long hostid:(CArray<Long>)Nest.value(data,"hostids").asCArray()) {
				copyDiscoveryRule(discoveryid, hostid);
			}
		}

		return true;
	}
	
	public boolean syncTemplates(Map data) {
		CArray<String> selectFields = array();
		for (Entry<String, Map> e : fieldRules.entrySet()) {
			String key = e.getKey();
		    Map rules = e.getValue();
			if (!isset(rules,"system") && !isset(rules,"host")) {
				selectFields.add(key);
			}
		}

		CDiscoveryRuleGet options = new CDiscoveryRuleGet();
		options.setHostIds(Nest.array(data,"templateids").asLong());
		options.setPreserveKeys(true);
		options.setOutput(selectFields.valuesAsString());
		CArray<Map> items = get(options);

		inherit(items, Nest.array(data,"hostids").asLong());

		return true;
	}

	/**
	 * Returns true if the given discovery rules exists and are available for
	 * reading.
	 *
	 * @param array ids	An array if item IDs
	 *
	 * @return bool
	 */
	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CDiscoveryRuleGet options = new CDiscoveryRuleGet();
		options.setItemIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	/**
	 * Returns true if the given discovery rules exists and are available for
	 * writing.
	 *
	 * @param array ids	An array if item IDs
	 *
	 * @return bool
	 */
	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CDiscoveryRuleGet options = new CDiscoveryRuleGet();
		options.setItemIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}
	
	/**
	 * Copies all of the triggers from the source discovery to the target discovery rule.
	 *
	 * @throws APIException if trigger saving fails
	 *
	 * @param array srcDiscovery    The source discovery rule to copy from
	 * @param array dstDiscovery    The target discovery rule to copy to
	 * @param array srcHost         The host the source discovery belongs to
	 * @param array dstHost         The host the target discovery belongs to
	 * @return 
	 *
	 * @return array
	 */
	protected CArray<Long[]> copyTriggerPrototypes(Map srcDiscovery, Map dstDiscovery, Map srcHost, Map dstHost) {
		CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
		tpoptions.setDiscoveryIds(Nest.value(srcDiscovery,"itemid").asLong());
		tpoptions.setOutput(API_OUTPUT_EXTEND);
		tpoptions.setSelectHosts(API_OUTPUT_EXTEND);
		tpoptions.setSelectItems(new String[]{"itemid", "type"});
		tpoptions.setSelectDiscoveryRule(API_OUTPUT_EXTEND);
		tpoptions.setSelectFunctions(API_OUTPUT_EXTEND);
		tpoptions.setPreserveKeys(true);
		CArray<Map> srcTriggers = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).get(tpoptions);

		if (empty(srcTriggers)) {
			return array();
		}

		for (Entry<Object, Map> e : srcTriggers.entrySet()) {
		    Object id = e.getKey();
		    Map trigger = e.getValue();
			// skip triggers with web items
			if (httpItemExists(Nest.value(trigger,"items").asCArray())) {
				unset(srcTriggers,id);
				continue;
			}
		}

		// save new triggers
		CArray<Map> dstTriggers = Clone.deepcopy(srcTriggers);
		for (Entry<Object, Map> e : dstTriggers.entrySet()) {
		    Object id = e.getKey();
		    Map trigger = e.getValue();
			unset(dstTriggers.get(id),"templateid");
			unset(dstTriggers.get(id),"triggerid");

			// update expression
			Nest.value(dstTriggers,id,"expression").$(explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString(), false, false, Nest.value(srcHost,"host").asString(), Nest.value(dstHost,"host").asString()));
		}

		CArray<Long[]> rs = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).create(dstTriggers);
		if (empty(rs)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot clone trigger prototypes."));
		}

		return rs;
	}
	
	protected void createReal(CArray<Map> items) {
		if(empty(items)){
			return;
		}
		CArray<Long> itemids = insert("items", items);

		for(Object key : items.keySet()) {
			Nest.value(items,key,"itemid").$(itemids.get(key));
		}
	}
	
	protected void updateReal(CArray<Map> items) {
		if(empty(items)){
			return;
		}
		CArray data = array();
		for(Map item : items) {
			data.add(map("values", item, "where", map("itemid", Nest.value(item,"itemid").asLong())));
		}
		boolean result = update("items", data);
		if (!result){
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
		}

		CArray itemids = array();
		for(Map item : items) {
			itemids.add(Nest.value(item,"itemid").$());
		}
	}
	
	/**
	 * Check item data and set missing default values.
	 *
	 * @param array items passed by reference
	 * @param bool  update
	 */
	protected void checkInput(CArray<Map> items) {
		checkInput(items, false);
	}
	
	protected void checkInput(CArray<Map> items, boolean update) {
		// add the values that cannot be changed, but are required for further processing
		for(Map item : items) {
			Nest.value(item,"flags").$(RDA_FLAG_DISCOVERY_RULE);
			Nest.value(item,"value_type").$(ITEM_VALUE_TYPE_TEXT);
		}
		super.checkInput(items, update);
	}
	
	@Override
	protected boolean checkSpecificFields(Map item) {
		if (isset(item,"lifetime") && !validateLifetime(Nest.value(item,"lifetime").asString())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Discovery rule \"%1$s:%2$s\" has incorrect lifetime: \"%3$s\". (min: %4$d, max: %5$d, user macro allowed)",
					Nest.value(item,"name").$(), Nest.value(item,"key_").$(), Nest.value(item,"lifetime").$(), MIN_LIFETIME, MAX_LIFETIME)
			);
		}
		return true;
	}

	@Override
	protected boolean inherit(CArray<Map> items, Long... hostids) {
		if (empty(items)) {
			return true;
		}

		// prepare the child items
		CArray<Map> newItems = prepareInheritedItems(items, hostids);
		if (empty(newItems)) {
			return true;
		}

		CArray<Map> insertItems = array();
		CArray<Map> updateItems = array();
		for(Map newItem : newItems) {
			if (isset(newItem,"itemid")) {
				updateItems.add(newItem);
			} else {
				Nest.value(newItem,"flags").$(RDA_FLAG_DISCOVERY_RULE);
				insertItems.add(newItem);
			}
		}

		// save the new items
		createReal(insertItems);
		updateReal(updateItems);

		// propagate the inheritance to the children
		return inherit(array_merge(updateItems, insertItems));
	}
	
	/**
	 * Copies the given discovery rule to the specified host.
	 *
	 * @throws APIException if the discovery rule interfaces could not be mapped
	 * to the new host interfaces.
	 *
	 * @param string discoveryid  The ID of the discovery rule to be copied
	 * @param string hostid       Destination host id
	 *
	 * @return bool
	 */
	protected boolean copyDiscoveryRule(Long discoveryid, Long hostid) {
		// fetch discovery to clone
		CDiscoveryRuleGet options = new CDiscoveryRuleGet();
		options.setItemIds(discoveryid);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		CArray<Map> srcDiscoveries = get(options);
		Map srcDiscovery = reset(srcDiscoveries);

		// fetch source and destination hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setHostIds(new Long[]{Nest.value(srcDiscovery,"hostid").asLong(), hostid});
		hoptions.setOutput(API_OUTPUT_EXTEND);
		hoptions.setSelectInterfaces(API_OUTPUT_EXTEND);
		hoptions.setTemplatedHosts(true);
		hoptions.setPreserveKeys(true);
		CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(hoptions);
		Map srcHost = hosts.get(srcDiscovery.get("hostid"));
		Map dstHost = hosts.get(hostid);

		Map dstDiscovery = Clone.deepcopy(srcDiscovery);
		Nest.value(dstDiscovery,"hostid").$(hostid);
		unset(dstDiscovery,"templateid");
		unset(dstDiscovery,"state");

		// if this is a plain host, map discovery interfaces
		if (Nest.value(srcHost,"status").asInteger() != HOST_STATUS_TEMPLATE) {
			// find a matching interface
			Map iface = findInterfaceForItem(dstDiscovery, Nest.value(dstHost,"interfaces").asCArray());
			if (!empty(iface)) {
				Nest.value(dstDiscovery,"interfaceid").$(Nest.value(iface,"interfaceid").$());
			} else if (iface!=null) {// no matching interface found, throw an error
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot find host interface on \"%1$s\" for item key \"%2$s\".", Nest.value(dstHost,"name").$(), Nest.value(dstDiscovery,"key_").$()));
			}
		}

		// save new discovery
		CArray<Long[]> newDiscovery = create(array(dstDiscovery));
		Nest.value(dstDiscovery,"itemid").$(Nest.array(newDiscovery,"itemids").get(0));

		// copy prototypes
		CArray<Long[]> newPrototypesResult = copyItemPrototypes(srcDiscovery, dstDiscovery, dstHost);

		// if there were prototypes defined, clone everything else
		if (!empty(newPrototypesResult)) {
			// fetch new prototypes
			CItemPrototypeGet ipoptions = new CItemPrototypeGet();
			ipoptions.setItemIds(Nest.array(newPrototypesResult,"itemids").asLong());
			ipoptions.setOutput(API_OUTPUT_EXTEND);
			ipoptions.setPreserveKeys(true);
			CArray<Map> newPrototypes = API.ItemPrototype(this.idBean, this.getSqlExecutor()).get(ipoptions);

			for(Map newPrototype : newPrototypes) {
				unset(newPrototype, "templateid");
			}

			Nest.value(dstDiscovery,"items").$(newPrototypes);

			// copy graphs
			copyGraphPrototypes(srcDiscovery, dstDiscovery);

			// copy triggers
			copyTriggerPrototypes(srcDiscovery, dstDiscovery, srcHost, dstHost);
		}

		// copy host prototypes
		copyHostPrototypes(srcDiscovery, dstDiscovery);

		return true;
	}
	
	/**
	 * Copies all of the item prototypes from the source discovery to the target
	 * discovery rule.
	 *
	 * @throws APIException if prototype saving fails
	 *
	 * @param array srcDiscovery   The source discovery rule to copy from
	 * @param array dstDiscovery   The target discovery rule to copy to
	 * @param array dstHost        The target host to copy the deiscovery rule to
	 *
	 * @return array
	 */
	protected CArray<Long[]> copyItemPrototypes(Map srcDiscovery, Map dstDiscovery, Map dstHost) {
		CItemPrototypeGet ipoptions = new CItemPrototypeGet();
		ipoptions.setDiscoveryIds(Nest.value(srcDiscovery,"itemid").asLong());
		ipoptions.setSelectApplications(API_OUTPUT_EXTEND);
		ipoptions.setOutput(API_OUTPUT_EXTEND);
		ipoptions.setPreserveKeys(true);
		CArray<Map> prototypes = API.ItemPrototype(this.idBean, this.getSqlExecutor()).get(ipoptions);

		CArray<Long[]> rs = array();
		if (!empty(prototypes)) {
			for (Entry<Object, Map> e : prototypes.entrySet()) {
			    Object key = e.getKey();
			    Map prototype = e.getValue();
				Nest.value(prototype,"ruleid").$(Nest.value(dstDiscovery,"itemid").$());
				Nest.value(prototype,"hostid").$(Nest.value(dstDiscovery,"hostid").$());
				
				unset(prototype,"templateid");
				unset(prototype,"state");

				// map prototype interfaces
				if (Nest.value(dstHost,"status").asInteger() != HOST_STATUS_TEMPLATE) {
					// find a matching interface
					Map iface = findInterfaceForItem(prototype, Nest.value(dstHost,"interfaces").asCArray());
					if (!empty(iface)) {
						Nest.value(prototype,"interfaceid").$(Nest.value(iface,"interfaceid").$());
					} else if (iface!=null) {// no matching interface found, throw an error
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot find host interface on \"%1$s\" for item key \"%2$s\".", Nest.value(dstHost,"name").$(), Nest.value(prototype,"key_").$()));
					}
				}

				// add new applications
				Nest.value(prototype,"applications").$(get_same_applications_for_host(getSqlExecutor(),rda_objectValues(Nest.value(prototype,"applications").$(), "applicationid").valuesAsLong(), Nest.value(dstHost,"hostid").asLong()));

				prototypes.put(key,prototype);
			}

			rs = API.ItemPrototype(this.idBean, this.getSqlExecutor()).create(prototypes);
			if (empty(rs)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot clone item prototypes."));
			}
		}

		return rs;
	}
	
	/**
	 * Copies all of the graphs from the source discovery to the target discovery rule.
	 *
	 * @throws APIException if graph saving fails
	 *
	 * @param array srcDiscovery    The source discovery rule to copy from
	 * @param array dstDiscovery    The target discovery rule to copy to
	 *
	 * @return array
	 */
	protected CArray<Long[]> copyGraphPrototypes(Map srcDiscovery, Map dstDiscovery) {
		// fetch source graphs
		CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
		gpoptions.setDiscoveryIds(Nest.value(srcDiscovery,"itemid").asLong());
		gpoptions.setOutput(API_OUTPUT_EXTEND);
		gpoptions.setSelectGraphItems(API_OUTPUT_EXTEND);
		gpoptions.setSelectHosts(API_OUTPUT_REFER);
		gpoptions.setPreserveKeys(true);
		CArray<Map> srcGraphs = API.GraphPrototype(this.idBean, this.getSqlExecutor()).get(gpoptions);

		if (empty(srcGraphs)) {
			return array();
		}

		CArray srcItemIds = array();
		for (Entry<Object, Map> e : srcGraphs.entrySet()) {
		    Object key = e.getKey();
		    Map graph = e.getValue();
			// skip graphs with items from multiple hosts
			if (count(Nest.value(graph,"hosts").asCArray()) > 1) {
				unset(srcGraphs,key);
				continue;
			}

			// skip graphs with http items
			if (httpItemExists(Nest.value(graph,"gitems").asCArray())) {
				unset(srcGraphs,key);
				continue;
			}

			// save all used item ids to map them to the new items
			for(Map item:(CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
				Nest.value(srcItemIds,item.get("itemid")).$(Nest.value(item,"itemid").$());
			}
			if (!empty(Nest.value(graph,"ymin_itemid").$())) {
				Nest.value(srcItemIds,graph.get("ymin_itemid")).$(Nest.value(graph,"ymin_itemid").$());
			}
			if (!empty(Nest.value(graph,"ymax_itemid").$())) {
				Nest.value(srcItemIds,graph.get("ymax_itemid")).$(Nest.value(graph,"ymax_itemid").$());
			}
		}

		// fetch source items
		CItemGet ioptions = new CItemGet();
		ioptions.setItemIds(srcItemIds.valuesAsLong());
		ioptions.setOutput(new String[]{"itemid", "key_"});
		ioptions.setPreserveKeys(true);
		ioptions.setFilter("flags",(String[])null);
		CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

		CArray<Map> srcItems = array();
		CArray<String> itemKeys = array();
		for(Map item : items) {
			srcItems.put(item.get("itemid"),item);
			itemKeys.put(item.get("key_"),Nest.value(item,"key_").$());
		}

		// fetch newly cloned items
		ioptions = new CItemGet();
		ioptions.setHostIds(Nest.value(dstDiscovery,"hostid").asLong());
		ioptions.setFilter("key_",itemKeys.valuesAsString());
		ioptions.setFilter("flags",(String[])null);
		ioptions.setOutput(new String[]{"itemid", "key_"});
		ioptions.setPreserveKeys(true);		
		CArray<Map> newItems = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);

		items = array_merge(Nest.value(dstDiscovery,"items").asCArray(), newItems);
		CArray dstItems = array();
		for(Map item : items) {
			Nest.value(dstItems,item.get("key_")).$(item);
		}

		CArray<Map> dstGraphs = Clone.deepcopy(srcGraphs);
		for(Map graph : dstGraphs) {
			unset(graph,"graphid");
			unset(graph,"templateid");

			for(Map gitem : (CArray<Map>)Nest.value(graph,"gitems").asCArray()) {
				// replace the old item with the new one with the same key
				Map item = srcItems.get(gitem.get("itemid"));
				Nest.value(gitem,"itemid").$(Nest.value(dstItems,item.get("key_"),"itemid").$());

				unset(gitem,"gitemid");
				unset(gitem,"graphid");
			}

			// replace the old axis items with the new one with the same key
			if (!empty(Nest.value(graph,"ymin_itemid").$())) {
				Map yMinSrcItem = srcItems.get(graph.get("ymin_itemid"));
				Nest.value(graph,"ymin_itemid").$(Nest.value(dstItems,yMinSrcItem.get("key_"),"itemid").$());
			}
			if (!empty(Nest.value(graph,"ymax_itemid").$())) {
				Map yMaxSrcItem = srcItems.get(graph.get("ymax_itemid"));
				Nest.value(graph,"ymax_itemid").$(Nest.value(dstItems,yMaxSrcItem.get("key_"),"itemid").$());
			}
		}

		// save graphs
		CArray<Long[]> rs = API.GraphPrototype(this.idBean, this.getSqlExecutor()).create(dstGraphs);
		if (empty(rs)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot clone graph prototypes."));
		}
		return rs;
	}
	
	/**
	 * Copies all of the host prototypes from the source discovery to the target
	 * discovery rule.
	 *
	 * @throws APIException if prototype saving fails
	 *
	 * @param array srcDiscovery   The source discovery rule to copy from
	 * @param array dstDiscovery   The target discovery rule to copy to
	 *
	 * @return array
	 */
	protected CArray<Long[]> copyHostPrototypes(Map srcDiscovery, Map dstDiscovery) {
		CHostPrototypeGet hpoptions = new CHostPrototypeGet();
		hpoptions.setDiscoveryIds(Nest.value(srcDiscovery,"itemid").asLong());
		hpoptions.setOutput(new String[]{"host", "name", "status"});
		hpoptions.setSelectGroupLinks(new String[]{"groupid"});
		hpoptions.setSelectGroupPrototypes(new String[]{"name"});
		hpoptions.setSelectInventory(new String[]{"inventory_mode"});
		hpoptions.setSelectTemplates(new String[]{"templateid"});
		hpoptions.setPreserveKeys(true);
		CArray<Map> prototypes = API.HostPrototype(this.idBean, this.getSqlExecutor()).get(hpoptions);

		CArray<Long[]> rs = array();
		if (!empty(prototypes)) {
			for(Map prototype : prototypes) {
				Nest.value(prototype,"ruleid").$(Nest.value(dstDiscovery,"itemid").$());
				unset(prototype,"hostid");
				unset((Map)prototype.get("inventory"), "hostid");

				for(Map groupLinks : (CArray<Map>)Nest.value(prototype,"groupLinks").asCArray()) {
					unset(groupLinks,"group_prototypeid");
				}

				for(Map groupPrototype : (CArray<Map>)Nest.value(prototype,"groupPrototypes").asCArray()) {
					unset(groupPrototype,"group_prototypeid");
				}
			}

			rs = API.HostPrototype(this.idBean, this.getSqlExecutor()).create(prototypes);
			if (empty(rs)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot clone host prototypes."));
			}
		}
		return rs;
	}

	private boolean validateLifetime(String lifetime) {
		return (validateNumber(lifetime, MIN_LIFETIME, MAX_LIFETIME) || validateUserMacro(lifetime));
	}	
	
	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (is_null(params.getCountOutput())) {
			if(!is_null(params.get("selectHosts"))){
				addQuerySelect("i.hostid", sqlParts);
			}
		}
	}

	@Override
	protected void addRelatedObjects(CDiscoveryRuleGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);

		CArray itemIds = array_keys(result);

		// adding items
		if (!is_null(params.getSelectItems())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectItems())) {
				CRelationMap relationMap = createRelationMap(result, "parent_itemid", "itemid", "item_discovery");
				CItemPrototypeGet ipoptions = new CItemPrototypeGet();
				ipoptions.setOutput(params.getSelectItems());
				ipoptions.setItemIds(relationMap.getRelatedLongIds());
				ipoptions.setNopermissions(true);
				ipoptions.setPreserveKeys(true);
				CArray<Map> items = API.ItemPrototype(this.idBean, this.getSqlExecutor()).get(ipoptions);
				relationMap.mapMany(result, items, "items", params.getLimitSelects());
			} else {
				CItemPrototypeGet ipoptions = new CItemPrototypeGet();
				ipoptions.setDiscoveryIds(itemIds.valuesAsLong());
				ipoptions.setNopermissions(true);
				ipoptions.setCountOutput(true);
				ipoptions.setGroupCount(true);
				CArray<Map> items = API.ItemPrototype(this.idBean, this.getSqlExecutor()).get(ipoptions);
				items = rda_toHash(items, "parent_itemid");
				for(Object itemid: result.keySet()) {
					Nest.value(result, itemid, "items").$(isset(items.get(itemid)) ? Nest.value(items, itemid, "rowscount").asInteger() : 0);
				}
			}
		}

		// adding triggers
		if (!is_null(params.getSelectTriggers())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectTriggers())) {
				CRelationMap relationMap = new CRelationMap();
				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> res = DBselect(getSqlExecutor(),
					"SELECT id.parent_itemid,f.triggerid"+
					" FROM item_discovery id,items i,functions f"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "item_discovery", "id", params)+
					    " AND "+sqlParts.dual.dbConditionInt("id.parent_itemid", itemIds.valuesAsLong())+
						" AND id.tenantid=i.tenantid"+
						" AND i.tenantid=f.tenantid"+
						" AND id.itemid=i.itemid"+
						" AND i.itemid=f.itemid",
					sqlParts.getNamedParams()
				);
				for (Map relation : res) {
					relationMap.addRelation(Nest.value(relation,"parent_itemid").$(), Nest.value(relation,"triggerid").$());
				}

				CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
				tpoptions.setOutput(params.getSelectTriggers());
				tpoptions.setTriggerIds(relationMap.getRelatedLongIds());
				tpoptions.setPreserveKeys(true);
				CArray<Map> triggers = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).get(tpoptions);
				relationMap.mapMany(result, triggers, "triggers", params.getLimitSelects());
			} else {
				CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
				tpoptions.setDiscoveryIds(itemIds.valuesAsLong());
				tpoptions.setCountOutput(true);
				tpoptions.setGroupCount(true);
				CArray<Map> triggers = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).get(tpoptions);
				triggers = rda_toHash(triggers, "parent_itemid");
				for(Object itemid: result.keySet()) {
					Nest.value(result, itemid, "triggers").$(isset(triggers.get(itemid)) ? Nest.value(triggers, itemid, "rowscount").asInteger() : 0);
				}
			}
		}

		// adding graphs
		if (!is_null(params.getSelectGraphs())) {
			if (! API_OUTPUT_COUNT.equals(params.getSelectGraphs())) {
				CRelationMap relationMap = new CRelationMap();
				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> res = DBselect(getSqlExecutor(),
					"SELECT id.parent_itemid,gi.graphid"+
					" FROM item_discovery id,items i,graphs_items gi"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "item_discovery", "id", params)+
					    " AND "+sqlParts.dual.dbConditionInt("id.parent_itemid", itemIds.valuesAsLong())+
						" AND id.tenantid=i.tenantid"+
						" AND i.tenantid=gi.tenantid"+	
						" AND id.itemid=i.itemid"+
						" AND i.itemid=gi.itemid",
					sqlParts.getNamedParams()
				);
				for (Map relation : res) {
					relationMap.addRelation(Nest.value(relation,"parent_itemid").$(), Nest.value(relation,"graphid").$());
				}

				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setOutput(params.getSelectGraphs());
				gpoptions.setGraphIds(relationMap.getRelatedLongIds());
				gpoptions.setPreserveKeys(true);
				CArray<Map> graphs = API.GraphPrototype(this.idBean, this.getSqlExecutor()).get(gpoptions);
				relationMap.mapMany(result, graphs, "graphs", params.getLimitSelects());
			} else {
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setDiscoveryIds(itemIds.valuesAsLong());
				gpoptions.setCountOutput(true);
				gpoptions.setGroupCount(true);
				CArray<Map> graphs = API.GraphPrototype(this.idBean, this.getSqlExecutor()).get(gpoptions);
				graphs = rda_toHash(graphs, "parent_itemid");
				for(Object itemid : result.keySet()) {
					Nest.value(result, itemid, "graphs").$(isset(graphs.get(itemid)) ? Nest.value(graphs, itemid, "rowscount").asInteger() : 0);
				}
			}
		}

		// adding hosts
		if (params.getSelectHostPrototypes() != null) {
			if (! API_OUTPUT_COUNT.equals(params.getSelectHostPrototypes())) {
				CRelationMap relationMap = createRelationMap(result, "parent_itemid", "hostid", "host_discovery");
				CHostPrototypeGet hpoptions = new CHostPrototypeGet();
				hpoptions.setOutput(params.getSelectHostPrototypes());
				hpoptions.setHostIds(relationMap.getRelatedLongIds());
				hpoptions.setNopermissions(true);
				hpoptions.setPreserveKeys(true);
				CArray<Map> hostPrototypes = API.HostPrototype(this.idBean, this.getSqlExecutor()).get(hpoptions);
				relationMap.mapMany(result, hostPrototypes, "hostPrototypes", params.getLimitSelects());
			} else {
				CHostPrototypeGet hpoptions = new CHostPrototypeGet();
				hpoptions.setDiscoveryIds(itemIds.valuesAsLong());
				hpoptions.setNopermissions(true);
				hpoptions.setCountOutput(true);
				hpoptions.setGroupCount(true);
				CArray<Map> hostPrototypes = API.HostPrototype(this.idBean, this.getSqlExecutor()).get(hpoptions);
				hostPrototypes = rda_toHash(hostPrototypes, "parent_itemid");
				for(Object itemid : result.keySet()) {
					Nest.value(result, itemid, "hostPrototypes").$(isset(hostPrototypes.get(itemid)) ? Nest.value(hostPrototypes, itemid, "rowscount").asInteger() : 0);
				}
			}
		}
	}
}
