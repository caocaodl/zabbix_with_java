package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphPrototypeGet;
import com.isoft.iradar.model.params.CItemPrototypeGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTriggerPrototypeGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CItemPrototypeDAO extends CItemGeneralDAO<CItemPrototypeGet> {

	public CItemPrototypeDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "items",  "i", new String[]{"itemid", "name", "key_", "delay", "history", "trends", "type", "status"});
		this.errorMessages = array_merge(errorMessages, map(
			ERROR_EXISTS_TEMPLATE, _("Item prototype \"%1$s\" already exists on \"%2$s\", inherited from another template."),
			ERROR_EXISTS, _("Item prototype \"%1$s\" already exists on \"%2$s\"."),
			ERROR_INVALID_KEY, _("Invalid key \"%1$s\" for item prototype \"%2$s\" on \"%3$s\": %4$s.")
		));
	}

	@Override
	public <T> T get(CItemPrototypeGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("items", "i.itemid");
		sqlParts.from.put("items", "items i");
		sqlParts.where.put("i.flags="+RDA_FLAG_DISCOVERY_PROTOTYPE);
		
		// templateIds
		if (!is_null(params.getTemplateIds())) {
			if (!is_null(params.getHostIds())) {
				params.setHostIds(array_merge(params.getHostIds(), params.getTemplateIds()));
			} else {
				params.setHostIds(params.getTemplateIds());
			}
		}
		
		// hostIds
		if (!is_null(params.getHostIds())) {
			if (!API_OUTPUT_EXTEND.equals(params.getOutput())) {
				sqlParts.select.put("hostid", "i.hostid");
			}

			sqlParts.where.dbConditionInt("hostid", "i.hostid", params.getHostIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("i","i.hostid");
			}
		}
		
		// itemIds
		if (!is_null(params.getItemIds())) {
			sqlParts.where.dbConditionInt("itemid", "i.itemid", params.getItemIds());
		}
		
		// discoveryids
		if (!is_null(params.getDiscoveryIds())) {
			sqlParts.select.put("discoveryid", "id.parent_itemid");
			sqlParts.from.put("item_discovery", "item_discovery id");
			sqlParts.where.dbConditionInt("id.parent_itemid", params.getDiscoveryIds());
			sqlParts.where.put("idi.tenantid","i.tenantid=id.tenantid");
			sqlParts.where.put("idi","i.itemid=id.itemid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("id","id.parent_itemid");
			}
		}

		// triggerIds
		if (!is_null(params.getTriggerIds())) {
			sqlParts.select.put("triggerid", "f.triggerid");
			sqlParts.from.put("functions","functions f");
			sqlParts.where.dbConditionInt("f.triggerid", params.getTriggerIds());
			sqlParts.where.put("if.tenantid","i.tenantid=f.tenantid");
			sqlParts.where.put("if","i.itemid=f.itemid");
		}

		// graphIds
		if (!is_null(params.getGraphIds())) {
			sqlParts.select.put("graphid", "gi.graphid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.where.dbConditionInt("gi.graphid", params.getGraphIds());
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
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
				sqlParts.where.put("h.status=" + HOST_STATUS_TEMPLATE);
			} else {
				sqlParts.where.put("h.status<>" + HOST_STATUS_TEMPLATE);
			}
		}

		// monitored
		if (!is_null(params.getMonitored())) {
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");

			if (params.getMonitored()) {
				sqlParts.where.put("h.status=" + HOST_STATUS_MONITORED);
				sqlParts.where.put("h.status=" + ITEM_STATUS_ACTIVE);
			} else {
				sqlParts.where.put("(h.status<>" + HOST_STATUS_MONITORED + " OR i.status<>" + ITEM_STATUS_ACTIVE + ")");
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
				sqlParts.where.dbConditionString("h", "h.host", TArray.as(params.getFilter().get("host")).asString());
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
		
		CArray<Long> itemids = array();
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
				itemids.add(id);
				
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}
				
				// hostids
				if (isset(row.get("hostid")) && is_null(params.getSelectHosts())) {
					if (!isset(result.get(id).get("hosts"))) {
						result.get(id).put("hosts", new CArray());
					}
					((CArray)result.get(id).get("hosts")).add(map("hostid", row.get("hostid")));
				}

				// triggerids
				if (isset(row.get("triggerid")) && is_null(params.getSelectTriggers())) {
					if (!isset(result.get(id).get("triggers"))) {
						result.get(id).put("triggers", new CArray());
					}
					((CArray)result.get(id).get("triggers")).add(map("triggerid", row.remove("triggerid")));
				}
				
				// graphids
				if (isset(row.get("graphid")) && is_null(params.getSelectGraphs())) {
					if (!isset(result.get(id).get("graphs"))) {
						result.get(id).put("graphs", new CArray());
					}
					((CArray)result.get(id).get("graphs")).add(map("graphid", row.remove("graphid")));
				}
				
				// discoveryids
				if (isset(row.get("discoveryids"))) {
					if (!isset(result.get(id).get("discovery"))) {
						result.get(id).put("discovery", new CArray());
					}
					((CArray)result.get(id).get("discovery")).add(map("ruleid", row.remove("item_parentid")));
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
		CItemPrototypeGet options = new CItemPrototypeGet();
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
	 * Check item prototype data and set flags field.
	 *
	 * @param array _items passed by reference
	 * @param bool  _update
	 *
	 * @return void
	 */
	@Override
	protected void checkInput(CArray<Map> items) {
		checkInput(items, false);
	}
	
	@Override
	protected void checkInput(CArray<Map> items, boolean update) {
		super.checkInput(Clone.deepcopy(items), update);
		// set proper flags to divide normal and discovered items in future processing
		for(Map item : items) {
			Nest.value(item,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);
		}
	}
	
	/**
	 * Create item prototype.
	 *
	 * @param array _items
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
	
	protected void createReal(CArray<Map> items) {
		if (items.isEmpty()) {
			return;
		}
		CArray<Long> itemids = insert("items", Clone.deepcopy(items));

		CArray itemApplications = array();
		CArray<Map> insertItemDiscovery = array();
		
		for (Entry<Object, Map> e : items.entrySet()) {
		    Object key = e.getKey();
		    Map item = e.getValue();
			Nest.value(items,key,"itemid").$(itemids.get(key));

			insertItemDiscovery.add(map(
				"itemid", Nest.value(items,key,"itemid").$(),
				"parent_itemid", Nest.value(item,"ruleid").$()
			));

			if (isset(item,"applications")) {
				for (Object appid : Nest.value(item,"applications").asCArray()) {
					if (Nest.as(appid).asInteger() == 0) continue;

					itemApplications.add(map(
						"applicationid", appid,
						"itemid" , Nest.value(items,key,"itemid").$()
					));
				}
			}
		}

		insert("item_discovery", insertItemDiscovery);

		if (!empty(itemApplications)) {
			insert("items_applications", itemApplications);
		}
	}
	
	protected void updateReal(CArray<Map> items) {
		if (items.isEmpty()) {
			return;
		}
		CArray<Map> data = array();
		for (Map item : items) {
			data.add(map("values", item, "where", map("itemid", Nest.value(item,"itemid").$())));
		}

		boolean result = update("items", data);
		if (!result) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
		}

		CArray itemids = array();
		CArray<Long> itemidsWithApplications = array();
		CArray<Map> itemApplications = array();
		for(Map item : items) {
			if (!isset(item,"applications")) {
				array_push(itemids, Nest.value(item,"itemid").$());
				continue;
			}

			itemidsWithApplications.add(Nest.value(item,"itemid").asLong());
			for (Object appid : Nest.value(item,"applications").asCArray()) {
				itemApplications.add(map(
					"applicationid", appid,
					"itemid", item.get("itemid")
				));
			}
		}

		if (!empty(itemidsWithApplications)) {
			delete("items_applications", (Map)map("itemid", itemidsWithApplications.valuesAsLong()));
			insert("items_applications", itemApplications);
		}
	}
	
	/**
	 * Update Itemprototype.
	 *
	 * @param array _items
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
	
	@Override
	public CArray<Long[]> delete(Long... ids) {
		return delete(false, ids);
	}

	/**
	 * Delete Item prototypes.
	 *
	 * @param int|string|array _prototypeids
	 * @param bool             _nopermissions
	 *
	 * @return array
	 */
	public CArray<Long[]> delete(boolean nopermissions, Long... prototypeIds) {
		if (empty(prototypeIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CArray<Long> delItemPrototypeIds = rda_toArray(prototypeIds);
		CArray<Long> prototypeids = rda_toHash(prototypeIds);

		CItemPrototypeGet options = new CItemPrototypeGet();
		options.setItemIds(prototypeids.valuesAsLong());
		options.setEditable(true);
		options.setPreserveKeys(true);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectHosts(new String[]{"name"});
		CArray<Map> delItemPrototypes = get(options);

		// TODO: remove _nopermissions hack
		if (!nopermissions) {
			for(Long prototypeid : prototypeids) {
				if (!isset(delItemPrototypes,prototypeid)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
				if (Nest.value(delItemPrototypes,prototypeid,"templateid").asInteger() != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete templated items"));
				}
			}
		}

		// first delete child items
		CArray<Long> parentItemids = Clone.deepcopy(prototypeids);
		CArray<Long> childPrototypeids = array();
		SqlBuilder sqlParts  = null;
		do {
			sqlParts = new SqlBuilder();
			CArray<Map> _dbItems = DBselect(getSqlExecutor(),
				"SELECT itemid FROM items WHERE "+sqlParts.dual.dbConditionInt("templateid", parentItemids.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			parentItemids = array();
			for (Map _dbItem : _dbItems) {
				Nest.value(parentItemids,_dbItem.get("itemid")).$(Nest.value(_dbItem,"itemid").$());
				Nest.value(childPrototypeids,_dbItem.get("itemid")).$(Nest.value(_dbItem,"itemid").$());
			}
		} while (!empty(parentItemids));

		options = new CItemPrototypeGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setItemIds(childPrototypeids.valuesAsLong());
		options.setNopermissions(true);
		options.setPreserveKeys(true);
		options.setSelectHosts(new String[]{"name"});
		CArray<Map> delItemPrototypesChilds = get(options);

		delItemPrototypes = array_merge(delItemPrototypes, delItemPrototypesChilds);
		prototypeids = array_merge(prototypeids, childPrototypeids);

		// delete graphs with this item prototype
		CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
		gpoptions.setItemIds(prototypeids.valuesAsLong());
		gpoptions.setOutput(new String[]{"graphid"});
		gpoptions.setNopermissions(true);
		gpoptions.setPreserveKeys(true);
		CArray<Map> delGraphPrototypes = API.GraphPrototype(this.idBean, this.getSqlExecutor()).get(gpoptions);
		if (!empty(delGraphPrototypes)) {
			CArray<Long[]> result = API.GraphPrototype(this.idBean, this.getSqlExecutor()).delete(rda_objectValues(delGraphPrototypes, "graphid"), true);
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete graph prototype"));
			}
		}

		// check if any graphs are referencing this item
		checkGraphReference(prototypeids.valuesAsLong());

		// CREATED ITEMS
		CArray<Long> createdItems = array();
		sqlParts = new SqlBuilder();
		String sql = "SELECT itemid FROM item_discovery WHERE "+sqlParts.dual.dbConditionInt("parent_itemid", prototypeids.valuesAsLong());
		CArray<Map> dbItems = DBselect(getSqlExecutor(),sql,sqlParts.getNamedParams());
		for (Map item : dbItems) {
			Nest.value(createdItems,item.get("itemid")).$(Nest.value(item,"itemid").$());
		}
		if (!empty(createdItems)) {
			CArray<Long[]> result = API.Item(this.idBean, this.getSqlExecutor()).delete(true, createdItems.valuesAsLong());
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete items created by low level discovery."));
			}
		}

		// TRIGGER PROTOTYPES
		CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
		tpoptions.setItemIds(prototypeids.valuesAsLong());
		tpoptions.setOutput(new String[]{"triggerid"});
		tpoptions.setNopermissions(true);
		tpoptions.setPreserveKeys(true);
		CArray<Map> delTriggerPrototypes = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).get(tpoptions);
		if (!empty(delTriggerPrototypes)) {
			CArray<Long[]>  result = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).delete(true, rda_objectValues(delTriggerPrototypes, "triggerid").valuesAsLong());
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete trigger prototype"));
			}
		}

		// ITEM PROTOTYPES
		delete("items", (Map)map("itemid", prototypeids.valuesAsLong()));

		// TODO: remove info from API
		for(Map item : delItemPrototypes) {
			Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
			info(_s("Deleted: Item prototype \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(host,"name").$()));
		}

		return map("prototypeids", delItemPrototypeIds.valuesAsLong());
	}
	
	public boolean syncTemplates(Map data) {
		CArray<String> selectFields = array();
		for (Entry<String,Map> e : this.fieldRules.entrySet()) {
			String key = e.getKey();
		    Map rules = e.getValue();
			if (!isset(rules,"system") && !isset(rules,"host")) {
				selectFields.add(key);
			}
		}

		CItemPrototypeGet options = new CItemPrototypeGet();
		options.setHostIds(Nest.array(data,"templateids").asLong());
		options.setPreserveKeys(true);
		options.setSelectApplications(API_OUTPUT_REFER);
		options.setOutput(selectFields.valuesAsString());
		CArray<Map> items = get(options);

		for (Entry<Object, Map> e : items.entrySet()) {
		    Object inum = e.getKey();
		    Map item = e.getValue();
		    Nest.value(items,inum,"applications").$(rda_objectValues(Nest.value(item,"applications").$(), "applicationid").valuesAsLong());
		}

		inherit(items, Nest.array(data,"hostids").asLong());
		return true;
	}

	@Override
	protected boolean inherit(CArray<Map> items, Long... hostids) {
		if (empty(items)) {
			return true;
		}

		SqlBuilder sqlParts = new SqlBuilder();
		// fetch the corresponding discovery rules for the child items
		CArray ruleids = array();
		CArray<Map> dbResult = DBselect(getSqlExecutor(),
			"SELECT i.itemid AS ruleid,id.itemid,i.hostid"+
			" FROM items i,item_discovery id"+
			" WHERE TRUE"+ //因租户的虚拟机会使用运营商的模型，所以删除
				" AND i.templateid=id.parent_itemid"+
				" AND "+sqlParts.dual.dbConditionInt("id.itemid", rda_objectValues(items, "itemid").valuesAsLong()),
			sqlParts.getNamedParams()
		);
		for (Map rule : dbResult) {
			if (!isset(ruleids,rule.get("itemid"))) {
				Nest.value(ruleids,rule.get("itemid")).$(array());
			}
			Nest.value(ruleids,rule.get("itemid"),rule.get("hostid")).$(Nest.value(rule,"ruleid").$());
		}

		// prepare the child items
		CArray<Map> newItems = prepareInheritedItems(items, hostids);
		if (empty(items)) {
			return true;
		}

		CArray<Map> insertItems = array();
		CArray<Map> updateItems = array();
		for(Map newItem : newItems) {
			if (isset(newItem,"itemid")) {
				unset(newItem,"ruleid");
				updateItems.add(newItem);
			} else {
				// set the corresponding discovery rule id for the new items
				Nest.value(newItem,"ruleid").$(Nest.value(ruleids,newItem.get("templateid"),newItem.get("hostid")).$());
				Nest.value(newItem,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);
				insertItems.add(newItem);
			}
		}
		
		// save the new items
		createReal(Clone.deepcopy(insertItems));
		updateReal(Clone.deepcopy(updateItems));

		// propagate the inheritance to the children
		inherit(array_merge(insertItems, updateItems));
		return true;
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (params.getCountOutput() == null) {
			if (Nest.value(params, "selectHosts").$() != null) {
				addQuerySelect("i.hostid", sqlParts);
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(CItemPrototypeGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		CArray itemids = array_keys(result);
		
		// adding applications
		if (!is_null(params.getSelectApplications()) && !API_OUTPUT_COUNT.equals(params.getSelectApplications())) {
			CRelationMap relationMap = createRelationMap(result, "itemid", "applicationid", "items_applications");
			CAppGet aoptions = new CAppGet();
			aoptions.setOutput(params.getSelectApplications());
			aoptions.setApplicationIds(relationMap.getRelatedLongIds());
			aoptions.setPreserveKeys(true);
			CArray<Map> applications = API.Application(this.idBean, this.getSqlExecutor()).get(aoptions);
			relationMap.mapMany(result, applications, "applications");
		}

		// adding triggers
		if (!is_null(params.getSelectTriggers())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectTriggers())) {
				CRelationMap relationMap = createRelationMap(result, "itemid", "triggerid", "functions");
				CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
				tpoptions.setOutput(params.getSelectTriggers());
				tpoptions.setTriggerIds(relationMap.getRelatedLongIds());
				tpoptions.setPreserveKeys(true);
				CArray<Map>triggers = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).get(tpoptions);

				if (!is_null(params.getLimitSelects())) {
					order_result(triggers, "description");
				}
				relationMap.mapMany(result, triggers, "triggers", params.getLimitSelects());
			} else {
				CTriggerPrototypeGet tpoptions = new CTriggerPrototypeGet();
				tpoptions.setCountOutput(true);
				tpoptions.setGroupCount(true);
				tpoptions.setItemIds(itemids.valuesAsLong());
				CArray<Map>triggers = API.TriggerPrototype(this.idBean, this.getSqlExecutor()).get(tpoptions);
				triggers = rda_toHash(triggers, "itemid");

				for (Entry<Object, Map> e : result.entrySet()) {
				    Object itemid = e.getKey();
					if (isset(triggers,itemid)) {
						Nest.value(result,itemid,"triggers").$(Nest.value(triggers,itemid,"rowscount").$());
					} else {
						Nest.value(result,itemid,"triggers").$(0);
					}
				}
			}
		}
		
		// adding graphs
		if (!is_null(params.getSelectGraphs())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectGraphs())) {
				CRelationMap relationMap = createRelationMap(result, "itemid", "graphid", "graphs_items");
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setOutput(params.getSelectGraphs());
				gpoptions.setGraphIds(relationMap.getRelatedLongIds());
				gpoptions.setPreserveKeys(true);
				CArray<Map> graphs = API.GraphPrototype(this.idBean, this.getSqlExecutor()).get(gpoptions);

				if (!is_null(params.getLimitSelects())) {
					order_result(graphs, "name");
				}
				relationMap.mapMany(result, graphs, "graphs", params.getLimitSelects());
			} else {
				CGraphPrototypeGet gpoptions = new CGraphPrototypeGet();
				gpoptions.setCountOutput(true);
				gpoptions.setGroupCount(true);
				gpoptions.setItemIds(itemids.valuesAsLong());
				CArray<Map> graphs = API.GraphPrototype(this.idBean, this.getSqlExecutor()).get(gpoptions);
				graphs = rda_toHash(graphs, "itemid");
				
				for (Entry<Object, Map> e : result.entrySet()) {
				    Object itemid = e.getKey();
					if (isset(graphs,itemid)) {
						Nest.value(result,itemid,"graphs").$(Nest.value(graphs,itemid,"rowscount").$());
					} else {
						Nest.value(result,itemid,"graphs").$(0);
					}
				}
			}
		}
		
		// adding discoveryrule
		if (!is_null(params.getSelectDiscoveryRule()) && !API_OUTPUT_COUNT.equals(params.getSelectDiscoveryRule())) {
			CRelationMap relationMap = createRelationMap(result, "itemid", "parent_itemid", "item_discovery");
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setOutput(params.getSelectDiscoveryRule());
			droptions.setItemIds(relationMap.getRelatedLongIds());
			droptions.setNopermissions(true);
			droptions.setPreserveKeys(true);
			CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
			relationMap.mapOne(result, discoveryRules, "discoveryRule");
		}
	}

}
