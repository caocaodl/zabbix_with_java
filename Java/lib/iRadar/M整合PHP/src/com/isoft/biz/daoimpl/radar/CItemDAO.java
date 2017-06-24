package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_count_values;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_HISTORY_PERIOD;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_PLAIN_TEXT;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_SIMPLE_GRAPH;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostIfaceGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CItemDAO extends CItemGeneralDAO<CItemGet> {

	public CItemDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "items", "i", new String[]{"itemid", "name", "key_", "delay", "history", "trends", "type", "status"});
		this.errorMessages = array_merge(errorMessages, map(
			ERROR_EXISTS_TEMPLATE, _("Item \"%1$s\" already exists on \"%2$s\", inherited from another template."),
			ERROR_EXISTS, _("Item \"%1$s\" already exists on \"%2$s\"."),
			ERROR_INVALID_KEY, _("Invalid key \"%1$s\" for item \"%2$s\" on \"%3$s\": %4$s.")
		));
	}

	@Override
	public <T> T get(CItemGet params) {
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("items", "i.itemid");
		sqlParts.from.put("items", "items i");
		sqlParts.where.put("webtype", "i.type<>" + ITEM_TYPE_HTTPTEST);
		sqlParts.where.put("flags", "i.flags IN (" + RDA_FLAG_DISCOVERY_NORMAL + "," + RDA_FLAG_DISCOVERY_CREATED + ")");
		
		// itemIds
		if (!is_null(params.getItemIds())) {
			sqlParts.where.dbConditionInt("itemid", "i.itemid", params.getItemIds());
		}
		
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

		// interfaceids
		if (!is_null(params.getInterfaceIds())) {
			if (!API_OUTPUT_EXTEND.equals(params.getOutput())) {
				sqlParts.select.put("interfaceid", "i.interfaceid");
			}

			sqlParts.where.dbConditionInt("interfaceid", "i.interfaceid", params.getInterfaceIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("i","i.interfaceid");
			}
		}

		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid", "hg.groupid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.groupid", params.getGroupIds());
			sqlParts.where.put("hg.tenantid=i.tenantid");
			sqlParts.where.put("hg.hostid=i.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hg","hg.groupid");
			}
		}

		// proxyids
		if (!is_null(params.getProxyIds())) {
			if (!API_OUTPUT_EXTEND.equals(params.getOutput())) {
				sqlParts.select.put("proxyid","h.proxy_hostid");
			}

			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.dbConditionInt("h.proxy_hostid", params.getProxyIds());
			sqlParts.where.put("h.tenantid=i.tenantid");
			sqlParts.where.put("h.hostid=i.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("h","h.proxy_hostid");
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
		
		// applicationids
		if (!is_null(params.getApplicationIds())) {
			sqlParts.select.put("applicationid","ia.applicationid");
			sqlParts.from.put("items_applications","items_applications ia");
			sqlParts.where.dbConditionInt("ia.applicationid", params.getApplicationIds());
			sqlParts.where.put("ia.tenantid","ia.tenantid=i.tenantid");
			sqlParts.where.put("ia","ia.itemid=i.itemid");
		}

		// graphIds
		if (!is_null(params.getGraphIds())) {
			sqlParts.select.put("graphid", "gi.graphid");
			sqlParts.from.put("graphs_items","graphs_items gi");
			sqlParts.where.dbConditionInt("gi.graphid", params.getGraphIds());
			sqlParts.where.put("igi.tenantid","i.tenantid=gi.tenantid");
			sqlParts.where.put("igi","i.itemid=gi.itemid");
		}
		
		// webitems
		if (!is_null(params.getWebItems())) {
			sqlParts.where.clear("webtype");
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
				sqlParts.where.dbConditionString("h", "h.host", (String[])params.getFilter().get("host"), false);
			}

			if ( params.getFilter().containsKey("flags") && is_null(params.getFilter().get("flags"))) {
				sqlParts.where.clear("flags");
			}
		}

		// group
		if (!is_null(params.getGroup())) {
			sqlParts.from.put("groups","groups g");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("ghg.tenantid","g.tenantid=hg.tenantid");
			sqlParts.where.put("ghg","g.groupid=hg.groupid");
			sqlParts.where.put("hgi.tenantid","hg.tenantid=i.tenantid");
			sqlParts.where.put("hgi","hg.hostid=i.hostid");
			sqlParts.where.put("g.name="+sqlParts.marshalParam(params.getGroup()));
		}

		// host
		if (!is_null(params.getHost())) {
			sqlParts.select.put("host","h.host");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
			sqlParts.where.put("h.host="+sqlParts.marshalParam(params.getHost()));
		}

		// application
		if (!is_null(params.getApplication())) {
			sqlParts.select.put("application","a.name as application");
			sqlParts.from.put("applications","applications a");
			sqlParts.from.put("items_applications","items_applications ia");
			sqlParts.where.put("aia.tenantid","a.tenantid = ia.tenantid");
			sqlParts.where.put("aia","a.applicationid = ia.applicationid");
			sqlParts.where.put("iai.tenantid","ia.tenantid=i.tenantid");
			sqlParts.where.put("iai","ia.itemid=i.itemid");
			sqlParts.where.put(" a.name="+sqlParts.marshalParam(params.getApplication()));
		}

		// with_triggers
		if (!is_null(params.getWithTriggers())) {
			if (params.getWithTriggers()) {
				sqlParts.where.put("EXISTS ("+
						"SELECT NULL"+
						" FROM functions ff,triggers t"+
						" WHERE i.tenantid=ff.tenantid"+
						" AND ff.tenantid=t.tenantid"+
						" AND i.itemid=ff.itemid"+
						" AND ff.triggerid=t.triggerid"+
						" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
						")");
			} else {
				sqlParts.where.put("NOT EXISTS ("+
						"SELECT NULL"+
						" FROM functions ff,triggers t"+
						" WHERE i.tenantid=ff.tenantid"+
						" AND ff.tenantid=t.tenantid"+
						" AND i.itemid=ff.itemid"+
						" AND ff.triggerid=t.triggerid"+
						" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
						")");
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

				// triggerids
				if (isset(row.get("triggerid")) && is_null(params.getSelectTriggers())) {
					if (!isset(result.get(id).get("triggers"))) {
						result.get(id).put("triggers", new CArray());
					}
					((CArray)result.get(id).get("triggers")).add(map("triggerid", row.get("triggerid")));
				}
				// graphids
				if (isset(row.get("graphid")) && is_null(params.getSelectGraphs())) {
					if (!isset(result.get(id).get("graphs"))) {
						result.get(id).put("graphs", new CArray());
					}
					((CArray)result.get(id).get("graphs")).add(map("graphid", row.get("graphid")));
				}
				// applicationids
				if (isset(row.get("applicationid")) && is_null(params.getSelectApplications())) {
					if (!isset(result.get(id).get("applications"))) {
						result.get(id).put("applications", new CArray());
					}
					((CArray)result.get(id).get("applications")).add(map("applicationid", row.get("applicationid")));
				}
				
				result.get(id).putAll(row);
			}
		}		
		
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[]{"hostid", "interfaceid", "value_type"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys())) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Get itemid by host.name and item.key.
	 *
	 * @param array _itemData
	 * @param array _itemData["key_"]
	 * @param array _itemData["hostid"]
	 *
	 * @return array
	 */
	public CArray<Map> getObjects(CArray itemData) {
		CItemGet options = new CItemGet();
		options.setFilter(itemData);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setWebItems(true);
		CArray<Map> result = get(options);
		return result;
	}
	
	/**
	 * Check if item exists.
	 *
	 * @param array _object
	 *
	 * @return bool
	 */
	@Override
	public boolean exists(CArray object) {
		CItemGet options = new CItemGet();
		options.setFilter("key_", Nest.value(object,"key_").asString());
		options.setWebItems(true);
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
	 * Check item data and set flags field.
	 *
	 * @param array _items
	 * @param bool  _update
	 *
	 * @return void
	 */
	protected void checkInput(CArray items) {
		checkInput(items, false);
	}
	
	protected void checkInput(CArray<Map> items, boolean update) {
		super.checkInput(items, update);
		validateInventoryLinks(this.idBean, this.getSqlExecutor(), Clone.deepcopy(items), update);

		// set proper flags to divide normal and discovered items in future processing
		if (update) {
			CItemGet options = new CItemGet();
			options.setItemIds(rda_objectValues(items, "itemid").valuesAsLong());
			options.setOutput(new String[]{"itemid", "flags"});
			options.setEditable(true);
			options.setPreserveKeys(true);
			CArray<Map> dbItems = get(options);
			for(Map item : items) {
				Nest.value(item,"flags").$(Nest.value(dbItems,item.get("itemid"),"flags").$());
			}
		} else {
			for(Map item : items) {
				Nest.value(item,"flags").$(RDA_FLAG_DISCOVERY_NORMAL);;
			}
		}
	}
	
	/**
	 * Create item.
	 *
	 * @param items
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> items) {
		checkInput(Clone.deepcopy(items));
		createReal(items);
		inherit(items);
		return map("itemids", rda_objectValues(items, "itemid").valuesAsLong());
	}
	
	/**
	 * Create host item.
	 *
	 * @param array _items
	 */
	protected void createReal(CArray<Map> items) {
		if(empty(items)){
			return;
		}
		CArray<Long> itemids = insert("items", Clone.deepcopy(items));

		CArray<Map> itemApplications = array();
		for (Entry<Object, Map> e : items.entrySet()) {
		    Object key = e.getKey();
		    Map item = e.getValue();
		    Nest.value(items,key,"itemid").$(itemids.get(key));

			if (!isset(item,"applications")) {
				continue;
			}

			for(Object appid : Nest.value(item,"applications").asCArray()) {
				if (Nest.as(appid).asInteger() == 0) {
					continue;
				}

				itemApplications.add(map(
					"applicationid", appid,
					"itemid", Nest.value(items,key,"itemid").$()
				));
			}
		}

		if (!empty(itemApplications)) {
			insert("items_applications", itemApplications);
		}

		CItemGet options = new CItemGet();
		options.setItemIds(itemids.valuesAsLong());
		options.setOutput(new String[]{"name"});
		options.setSelectHosts(new String[]{"name"});
		options.setNopermissions(true);
		CArray<Map> itemHosts = get(options);
		for(Map item : itemHosts) {
			Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
			info(_s("Created: Item \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(host,"name").$()));
		}
	}
	
	/**
	 * Update host items.
	 *
	 * @param array _items
	 *
	 * @return void
	 */
	protected void updateReal(CArray<Map> items) {
		if(empty(items)){
			return;
		}
		CArray itemids = array();
		CArray data = array();
		for(Map item : Clone.deepcopy(items)) {
			unset(item,"flags"); // flags cannot be changed
			data.add(map("values", item, "where", map("itemid", Nest.value(item,"itemid").asLong())));
			itemids.add(Nest.value(item,"itemid").$());
		}
		update("items", data);

		CArray<Map> itemApplications = array();
		CArray applicationids = array();
		for(Map item : Clone.deepcopy(items)) {
			if (!isset(item,"applications")) {
				continue;
			}
			applicationids.add(Nest.value(item,"itemid").$());

			for(Object appid : Nest.value(item,"applications").asCArray()) {
				itemApplications.add(map(
					"applicationid", appid,
					"itemid", Nest.value(item,"itemid").$()
				));
			}
		}

		if (!empty(applicationids)) {
			delete("items_applications", (Map)map("itemid", applicationids.valuesAsLong()));
			insert("items_applications", itemApplications);
		}

		CItemGet options = new CItemGet();
		options.setItemIds(itemids.valuesAsLong());
		options.setOutput(new String[]{"name"});
		options.setSelectHosts(new String[]{"name"});
		options.setNopermissions(true);
		CArray<Map> itemHosts = get(options);
		for(Map item : itemHosts) {
			Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
			info(_s("Updated: Item \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(host,"name").$()));
		}
	}
	
	/**
	 * Update item.
	 *
	 * @param array _items
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> items) {
		checkInput(items, true);
		updateReal(items);
		inherit(items);
		return map("itemids", rda_objectValues(items, "itemid").valuesAsLong());
	}

	/**
	 * Delete items.
	 *
	 * @param array _itemids
	 */
	@Override
	public CArray<Long[]> delete(Long... temids) {
		return delete(false, temids);
	}

	public CArray<Long[]> delete(boolean nopermissions, Long[] fitemids) {
		if (empty(fitemids)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CArray delItemIds = rda_toArray(fitemids);
		CArray<Long> itemids = rda_toHash(fitemids);

		CItemGet options = new CItemGet();
		options.setItemIds(itemids.valuesAsLong());
		options.setEditable(true);
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"name", "templateid"});
		options.setSelectHosts(new String[]{"name"});
		CArray<Map> delItems = get(options);

		// TODO: remove _nopermissions hack
		if (!nopermissions) {
			for(Long itemid : itemids) {
				if (!isset(delItems,itemid)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
				if (Nest.value(delItems,itemid,"templateid").asLong() != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete templated item."));
				}
			}
		}

		// first delete child items
		CArray<Long> parentItemids = Clone.deepcopy(itemids);
		CArray<Map> dbItems = null;
		SqlBuilder sqlParts = null;
		do {
			sqlParts = new SqlBuilder();
			dbItems = DBselect(getSqlExecutor(),
					"SELECT i.itemid FROM items i WHERE "+sqlParts.dual.dbConditionInt("i.templateid", parentItemids.valuesAsLong()),
					sqlParts.getNamedParams());
			parentItemids = array();
			for (Map dbItem : dbItems) {
				parentItemids.add(Nest.value(dbItem,"itemid").asLong());
				Nest.value(itemids,dbItem.get("itemid")).$(Nest.value(dbItem,"itemid").$());
			}
		} while (!empty(parentItemids));

		// delete graphs, leave if graph still have item
		CArray delGraphs = array();
		sqlParts = new SqlBuilder();
		CArray<Map> dbGraphs = DBselect(getSqlExecutor(),
			"SELECT gi.graphid"+
			" FROM graphs_items gi"+
			" WHERE "+sqlParts.dual.dbConditionInt("gi.itemid", itemids.valuesAsLong())+
				" AND NOT EXISTS ("+
					"SELECT NULL"+
					" FROM graphs_items gii"+
					" WHERE gii.tenantid=gi.tenantid"+
						" AND gii.graphid=gi.graphid"+
						" AND "+sqlParts.dual.dbConditionLong("gii.itemid", itemids.valuesAsLong(), true)+
				")",
			sqlParts.getNamedParams()
		);
		for (Map dbGraph : dbGraphs) {
			Nest.value(delGraphs,dbGraph.get("graphid")).$(Nest.value(dbGraph,"graphid").$());
		}

		if (!empty(delGraphs)) {
			CArray<Long[]> result = API.Graph(this.idBean, this.getSqlExecutor()).delete(delGraphs, true);
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete graph."));
			}
		}

		// check if any graphs are referencing this item
		checkGraphReference(itemids.valuesAsLong());

		CTriggerGet toptions = new CTriggerGet();
		toptions.setItemIds(itemids.valuesAsLong());
		toptions.setOutput(new String[]{"triggerid"});
		toptions.setNopermissions(true);
		toptions.setPreserveKeys(true);
		CArray<Map> triggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(toptions);
		if (!empty(triggers)) {
			CArray<Long[]> result = API.Trigger(this.idBean, this.getSqlExecutor()).delete(true, array_keys(triggers).valuesAsLong());
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete trigger."));
			}
		}

		delete("screens_items", (Map)map(
			"resourceid", itemids.valuesAsLong(),
			"resourcetype", new Integer[]{SCREEN_RESOURCE_SIMPLE_GRAPH, SCREEN_RESOURCE_PLAIN_TEXT}
		));
		delete("items", (Map)map("itemid", itemids.valuesAsLong()));
		delete("profiles", (Map)map(
			"idx", "web.favorite.graphids",
			"source", "itemid",
			"value_id", itemids.valuesAsLong()
		));

		String[] itemDataTables = new String[]{
			"trends",
			"trends_uint",
			"history_text",
			"history_log",
			"history_uint",
			"history_str",
			"history"
		};
		CArray<Map> insert = array();
		for(Long itemid : itemids) {
			for(String table : itemDataTables) {
				insert.add(map(
					"tablename", table,
					"field", "itemid",
					"value", itemid
				));
			}
		}
		insert("housekeeper", insert);

		// TODO: remove info from API
		for(Map item : delItems) {
			Map host = reset((CArray<Map>)Nest.value(item,"hosts").asCArray());
			info(_s("Deleted: Item \"%1$s\" on \"%2$s\".", Nest.value(item,"name").$(), Nest.value(host,"name").$()));
		}

		return map("itemids", delItemIds.valuesAsLong());
	}
	
	public boolean syncTemplates(CArray data) {
		CArray<String> selectFields = array();
		for (Entry<String, Map> e : fieldRules.entrySet()) {
			String key = e.getKey();
		    Map rules = e.getValue();
			if (!isset(rules,"system") && !isset(rules,"host")) {
				selectFields.add(key);
			}
		}

		CItemGet options = new CItemGet();
		options.setHostIds(Nest.array(data,"templateids").asLong());
		options.setPreserveKeys(true);
		options.setSelectApplications(API_OUTPUT_REFER);
		options.setOutput(selectFields.valuesAsString());
		options.setFilter("flags", Nest.as(RDA_FLAG_DISCOVERY_NORMAL).asString());
		CArray<Map> items = get(options);

		for (Entry<Object, Map> e : items.entrySet()) {
		    Object inum = e.getKey();
		    Map item = e.getValue();
			Nest.value(items,inum,"applications").$(rda_objectValues(Nest.value(item,"applications").$(), "applicationid"));
		}

		inherit(items, Nest.array(data,"hostids").asLong());

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
				Nest.value(newItem,"flags").$(RDA_FLAG_DISCOVERY_NORMAL);
				insertItems.add(newItem);
			}
		}

		// save the new items
		if (!rda_empty(insertItems)) {
			validateInventoryLinks(this.idBean, this.getSqlExecutor(),Clone.deepcopy(insertItems), false); // false means "create"
			createReal(insertItems);
		}

		if (!rda_empty(updateItems)) {
			validateInventoryLinks(this.idBean, this.getSqlExecutor(),Clone.deepcopy(updateItems), true); // true means "update"
			updateReal(updateItems);
		}

		// propagate the inheritance to the children
		return inherit(array_merge(updateItems, insertItems));
	}
	
	/**
	 * Check, if items that are about to be inserted or updated violate the rule:
	 * only one item can be linked to a inventory filed.
	 * If everything is ok, function return true or throws Exception otherwise
	 *
	 * @static
	 *
	 * @param array _items
	 * @param bool _update whether this is update operation
	 *
	 * @return bool
	 */
	public static boolean validateInventoryLinks(IIdentityBean idBean, SQLExecutor executor, CArray<Map> items) {
		return validateInventoryLinks(idBean, executor, items, false);
	}
	
	public static boolean validateInventoryLinks(IIdentityBean idBean, SQLExecutor executor, CArray<Map> items, boolean update) {
		// inventory link field is not being updated, or being updated to 0, no need to validate anything then
		Iterator<Entry<Object, Map>> itemsEntrys = items.entrySet().iterator();
		while(itemsEntrys.hasNext()) {
			Entry<Object, Map> e = itemsEntrys.next();
//			Object i = e.getKey();
		    Map item = e.getValue();
			if (!isset(item,"inventory_link") || Nest.value(item,"inventory_link").asInteger() == 0) {
//				unset(items,i);
				itemsEntrys.remove();
			}
		}

		if (rda_empty(items)) {
			return true;
		}

		CArray<Map> possibleHostInventories = getHostInventories();
		if (update) {
			// for successful validation we need three fields for each item: inventory_link, hostid and key_
			// problem is, that when we are updating an item, we might not have them, because they are not changed
			// so, we need to find out what is missing and use API to get the lacking info
			CArray itemsWithNoHostId = array();
			CArray itemsWithNoInventoryLink = array();
			CArray itemsWithNoKeys = array();
			for(Map item : items) {
				if (!isset(item,"inventory_link")) {
					Nest.value(itemsWithNoInventoryLink,item.get("itemid")).$(Nest.value(item,"itemid").$());
				}
				if (!isset(Nest.value(item,"hostid").$())) {
					Nest.value(itemsWithNoHostId,item.get("itemid")).$(Nest.value(item,"itemid").$());
				}
				if (!isset(Nest.value(item,"key_").$())) {
					Nest.value(itemsWithNoKeys,item.get("itemid")).$(Nest.value(item,"itemid").$());
				}
			}
			CArray itemsToFind = array_merge(itemsWithNoHostId, itemsWithNoInventoryLink, itemsWithNoKeys);

			// are there any items with lacking info?
			if (!rda_empty(itemsToFind)) {
				CItemGet options = new CItemGet();
				options.setOutput(new String[]{"hostid", "inventory_link", "key_"});
				options.setFilter("itemid", itemsToFind.valuesAsString());
				options.setNopermissions(true);
				CArray<Map> missingInfo = API.Item(idBean, executor).get(options);
				missingInfo = rda_toHash(missingInfo, "itemid");

				// appending host ids, inventory_links and keys where they are needed
				for (Entry<Object, Map> e : items.entrySet()) {
				    Object i = e.getKey();
				    Map item = e.getValue();
					if (isset(missingInfo,item.get("itemid"))) {
						if (!isset(Nest.value(items,i,"hostid").$())) {
							Nest.value(items,i,"hostid").$(Nest.value(missingInfo,item.get("itemid"),"hostid").$());
						}
						if (!isset(Nest.value(items,i,"inventory_link").$())) {
							Nest.value(items,i,"inventory_link").$(Nest.value(missingInfo,item.get("itemid"),"inventory_link").$());
						}
						if (!isset(Nest.value(items,i,"key_").$())) {
							Nest.value(items,i,"key_").$(Nest.value(missingInfo,item.get("itemid"),"key_").$());
						}
					}
				}
			}
		}

		CArray hostids = rda_objectValues(items, "hostid");

		// getting all inventory links on every affected host
		CItemGet options = new CItemGet();
		options.setOutput(new String[]{"key_", "inventory_link", "hostid"});
		options.setFilter("hostid", hostids.valuesAsString());
		options.setNopermissions(true);
		CArray<Map> itemsOnHostsInfo = API.Item(idBean, executor).get(options);

		// now, changing array to: "hostid" => array("key_"=>"inventory_link")
		CArray<Map> linksOnHostsCurr = array();
		for(Map info : itemsOnHostsInfo) {
			// 0 means no link - we are not interested in those ones
			if (Nest.value(info,"inventory_link").asInteger() != 0) {
				if (!isset(linksOnHostsCurr,info.get("hostid"))) {
					Nest.value(linksOnHostsCurr,info.get("hostid")).$(map(Nest.value(info,"key_").$(), Nest.value(info,"inventory_link").$()));
				} else {
					Nest.value(linksOnHostsCurr,info.get("hostid"),info.get("key_")).$(Nest.value(info,"inventory_link").$());
				}
			}
		}

		CArray<Map> linksOnHostsFuture = array();

		for(Map item : items) {
			// checking if inventory_link value is a valid number
			if (update || Nest.value(item,"value_type").asInteger() != ITEM_VALUE_TYPE_LOG) {
				// does inventory field with provided number exists?
				if (!isset(possibleHostInventories,item.get("inventory_link"))) {
					long maxVar = max(array_keys(possibleHostInventories).valuesAsLong());
					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_s("Item \"%1$s\" cannot populate a missing host inventory field number \"%2$d\". Choices are: from 0 (do not populate) to %3$d.", Nest.value(item,"name").$(), Nest.value(item,"inventory_link").$(), maxVar)
					);
				}
			}

			if (!isset(linksOnHostsFuture,item.get("hostid"))) {
				Nest.value(linksOnHostsFuture,item.get("hostid")).$(map(Nest.value(item,"key_").$(), Nest.value(item,"inventory_link").$()));
			} else {
				Nest.value(linksOnHostsFuture,item.get("hostid"),item.get("key_")).$(Nest.value(item,"inventory_link").$());
			}
		}

		for (Entry<Object, Map> e : linksOnHostsFuture.entrySet()) {
		    Object hostId = e.getKey();
		    //Map linkFuture = e.getValue();
			Map futureSituation = null;
			if (isset(linksOnHostsCurr,hostId)) {
				futureSituation  = array_merge(linksOnHostsCurr.get(hostId), linksOnHostsFuture.get(hostId));
			} else {
				futureSituation = linksOnHostsFuture.get(hostId);
			}
			CArray<Integer> valuesCount = array_count_values(futureSituation);

			// if we have a duplicate inventory links after merging - we are in trouble
			if (max(valuesCount) > 1) {
				// what inventory field caused this conflict?
				CArray conflictedLinks = array_keys(valuesCount, 2);
				Object conflictedLink = reset(conflictedLinks);

				// which of updated items populates this link?
				String beingSavedItemName = "";
				for(Map item : items) {
					if (conflictedLink.equals(Nest.value(item,"inventory_link").$())) {
						if (isset(item,"name")) {
							beingSavedItemName = Nest.value(item,"name").asString();
						} else {
							options = new CItemGet();
							options.setOutput(new String[]{"name"});
							options.setFilter("itemid", Nest.value(item,"itemid").asString());
							options.setNopermissions(true);
							CArray<Map> thisItem = API.Item(idBean, executor).get(options);
							beingSavedItemName = Nest.value(thisItem,0,"name").asString();
						}
						break;
					}
				}

				// name of the original item that already populates the field
				options = new CItemGet();
				options.setOutput(new String[]{"name"});
				options.setFilter("hostid", Nest.as(hostId).asString());
				options.setFilter("inventory_link", Nest.as(conflictedLink).asString());
				options.setNopermissions(true);
				CArray<Map> originalItem = API.Item(idBean, executor).get(options);
				String originalItemName = Nest.value(originalItem,0,"name").asString();

				throw CDB.exception(
					RDA_API_ERROR_PARAMETERS,
					_s(
						"Two items (\"%1$s\" and \"%2$s\") cannot populate one host inventory field \"%3$s\", this would lead to a conflict.",
						beingSavedItemName,
						originalItemName,
						Nest.value(possibleHostInventories,conflictedLink,"title").$()
					)
				);
			}
		}

		return true;
	}

	@Override
	protected void addRelatedObjects(CItemGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		CArray itemids = array_keys(result);
		
		// adding applications
		if (!is_null(params.getSelectApplications()) && !API_OUTPUT_COUNT.equals(params.getSelectApplications())) {
			CRelationMap relationMap = this.createRelationMap(result, "itemid", "applicationid", "items_applications");
			CAppGet appGet = new CAppGet();
			appGet.setOutput(params.getSelectApplications());
			appGet.setApplicationIds(relationMap.getRelatedLongIds());
			appGet.setPreserveKeys(true);
			CArray<Map> applications = API.Application(this.idBean, this.getSqlExecutor()).get(appGet);
			relationMap.mapMany(result, applications, "applications");
		}

		// adding interfaces
		if (!is_null(params.getSelectInterfaces()) && !API_OUTPUT_COUNT.equals(params.getSelectInterfaces())) {
			CRelationMap relationMap = createRelationMap(result, "itemid", "interfaceid");
			CHostIfaceGet hioptions = new CHostIfaceGet();
			hioptions.setOutput(params.getSelectInterfaces());
			hioptions.setInterfaceIds(relationMap.getRelatedLongIds());
			hioptions.setNopermissions(true);
			hioptions.setPreserveKeys(true);
			CArray<Map> interfaces = API.HostInterface(this.idBean, this.getSqlExecutor()).get(hioptions);
			relationMap.mapMany(result, interfaces, "interfaces");
		}
		
		// adding triggers
		if (!is_null(params.getSelectTriggers())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectTriggers())) {
				CRelationMap relationMap = createRelationMap(result, "itemid", "triggerid", "functions");
				CTriggerGet toptions = new CTriggerGet();
				toptions.setOutput(params.getSelectTriggers());
				toptions.setTriggerIds(relationMap.getRelatedLongIds());
				toptions.setPreserveKeys(true);
				CArray<Map> triggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(toptions);
				if (!is_null(params.getLimitSelects())) {
					order_result(triggers, "description");
				}
				relationMap.mapMany(result, triggers, "triggers", params.getLimitSelects());
			} else {
				CTriggerGet toptions = new CTriggerGet();
				toptions.setCountOutput(true);
				toptions.setGroupCount(true);
				toptions.setItemIds(itemids.valuesAsLong());
				CArray<Map> triggers = API.Trigger(this.idBean, this.getSqlExecutor()).get(toptions);
				triggers = rda_toHash(triggers, "itemid");

				for (Entry<Object, Map> e : result.entrySet()) {
				    Object _itemid = e.getKey();
					if (isset(triggers,_itemid)) {
						Nest.value(result,_itemid,"triggers").$(Nest.value(triggers,_itemid,"rowscount").$());
					} else {
						Nest.value(result,_itemid,"triggers").$(0);
					}
				}
			}
		}
		
		// adding graphs
		if (!is_null(params.getSelectGraphs())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectGraphs())) {
				CRelationMap relationMap = createRelationMap(result, "itemid", "graphid", "graphs_items");
				CGraphGet goptions = new CGraphGet();
				goptions.setOutput(params.getSelectGraphs());
				goptions.setGraphIds(relationMap.getRelatedLongIds());
				goptions.setPreserveKeys(true);
				CArray<Map> graphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);
				if (!is_null(params.getLimitSelects())) {
					order_result(graphs, "name");
				}
				relationMap.mapMany(result, graphs, "graphs", params.getLimitSelects());
			} else {
				CGraphGet goptions = new CGraphGet();
				goptions.setCountOutput(true);
				goptions.setGroupCount(true);
				goptions.setItemIds(itemids.valuesAsLong());
				CArray<Map> graphs = API.Graph(this.idBean, this.getSqlExecutor()).get(goptions);
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
			CRelationMap relationMap = new CRelationMap();
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(getSqlExecutor(),
				"SELECT id1.itemid,id2.parent_itemid"+
					" FROM item_discovery id1,item_discovery id2,items i"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "item_discovery", "id1", params)+
					" AND "+sqlParts.dual.dbConditionInt("id1.itemid", itemids.valuesAsLong())+
					" AND id1.tenantid=id2.tenantid"+
					" AND id1.parent_itemid=id2.itemid"+
					" AND i.tenantid=id1.tenantid"+
					" AND i.itemid=id1.itemid"+
					" AND i.flags="+RDA_FLAG_DISCOVERY_CREATED,
					sqlParts.getNamedParams()
			);
			for (Map rule : dbRules) {
				relationMap.addRelation(Nest.value(rule,"itemid").$(), Nest.value(rule,"parent_itemid").$());
			}

			// item prototypes
			// TODO: this should not be in the item API
			sqlParts = new SqlBuilder();
			dbRules = DBselect(getSqlExecutor(),
				"SELECT id.parent_itemid,id.itemid"+
					" FROM item_discovery id,items i"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "item_discovery", "id", params)+
					" AND "+sqlParts.dual.dbConditionInt("id.itemid", itemids.valuesAsLong())+
					" AND i.tenantid=id.tenantid"+
					" AND i.itemid=id.itemid"+
					" AND i.flags="+RDA_FLAG_DISCOVERY_PROTOTYPE,
				sqlParts.getNamedParams()
			);
			for (Map rule : dbRules) {
				relationMap.addRelation(Nest.value(rule,"itemid").$(), Nest.value(rule,"parent_itemid").$());
			}

			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setOutput(params.getSelectDiscoveryRule());
			droptions.setItemIds(relationMap.getRelatedLongIds());
			droptions.setNopermissions(true);
			droptions.setPreserveKeys(true);
			CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
			relationMap.mapOne(result, discoveryRules, "discoveryRule");
		}

		// adding item discovery
		if (params.getSelectItemDiscovery() != null) {
			CParamGet options = new CParamGet();
			options.setOutput(outputExtend("item_discovery", new String[]{"itemdiscoveryid", "itemid"}, params.getSelectItemDiscovery()));
			options.setFilter("itemid", array_keys(result).valuesAsString());
			options.setPreserveKeys(true);
			CArray<Map> itemDiscoveries = select("item_discovery", options);
			CRelationMap relationMap = createRelationMap(itemDiscoveries, "itemid", "itemdiscoveryid");

			unsetExtraFields(itemDiscoveries, new String[]{"itemid", "itemdiscoveryid"}, params.getSelectItemDiscovery());
			relationMap.mapOne(result, itemDiscoveries, "itemDiscovery");
		}

		// adding history data
		CArray requestedOutput = array();
		if (outputIsRequested("lastclock", params.getOutput())) {
			Nest.value(requestedOutput,"lastclock").$(true) ;
		}
		if (outputIsRequested("lastns", params.getOutput())) {
			Nest.value(requestedOutput,"lastns").$(true);
		}
		if (outputIsRequested("lastvalue", params.getOutput())) {
			Nest.value(requestedOutput,"lastvalue").$(true);
		}
		if (outputIsRequested("prevvalue", params.getOutput())) {
			Nest.value(requestedOutput,"prevvalue").$(true);
		}
		if (!empty(requestedOutput)) {
			CArray<CArray<Map>> history = Manager.History(idBean, getSqlExecutor()).getLast(result, 2, RDA_HISTORY_PERIOD);
			for(Map item : result) {
				Map lastHistory = isset(Nest.value(history,item.get("itemid"),0).$()) ? Nest.value(history,item.get("itemid"),0).asCArray() : null;
				Map prevHistory = isset(Nest.value(history,item.get("itemid"),1).$()) ? Nest.value(history,item.get("itemid"),1).asCArray() : null;

				if (isset(requestedOutput,"lastclock")) {
					Nest.value(item,"lastclock").$(!empty(lastHistory) ? Nest.value(lastHistory,"clock").$() : "0");
				}
				if (isset(requestedOutput,"lastns")) {
					Nest.value(item,"lastns").$(!empty(lastHistory) ? Nest.value(lastHistory,"ns").$() : "0");
				}
				if (isset(requestedOutput,"lastvalue")) {
					Nest.value(item,"lastvalue").$( !empty(lastHistory) ? Nest.value(lastHistory,"value").$() : "0");
				}
				if (isset(requestedOutput,"prevvalue")) {
					Nest.value(item,"prevvalue").$(!empty(prevHistory) ? Nest.value(prevHistory,"value").$() : "0");
				}
			}
		}
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);

		if(is_null(params.getCountOutput())){
			if(!is_null(params.get("selectHosts"))){
				addQuerySelect("i.hostid", sqlParts);
			}
			if(!is_null(params.get("selectInterfaces"))){
				addQuerySelect("i.interfaceid", sqlParts);
			}
			
			if (outputIsRequested("lastclock", params.getOutput())
					|| outputIsRequested("lastns", params.getOutput())
					|| outputIsRequested("lastvalue", params.getOutput())
					|| outputIsRequested("prevvalue", params.getOutput())) {
				addQuerySelect("i.value_type", sqlParts);
			}
		}
	}
	
}
