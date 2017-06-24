package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.TriggersUtil.getExpressionItems;
import static com.isoft.iradar.inc.TriggersUtil.implode_exp;
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
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTriggerPrototypeGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CTriggerPrototypeDAO extends CTriggerGeneralDAO<CTriggerPrototypeGet> {

	public CTriggerPrototypeDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "triggers", "t", new String[]{"triggerid", "description", "status", "priority"});
	}
	
	@Override
	public <T> T get(CTriggerPrototypeGet params) {
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("triggers", "t.triggerid");
		sqlParts.from.put("t", "triggers t");
		sqlParts.where.put("t.flags="+RDA_FLAG_DISCOVERY_PROTOTYPE);
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("hgi.tenantid","hg.tenantid=i.tenantid");
			sqlParts.where.put("hgi","hg.hostid=i.hostid");
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
			sqlParts.where.dbConditionInt("groupid","hg.groupid",params.getGroupIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hg","hg.groupid");
			}
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
			sqlParts.select.put("hostid","i.hostid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.where.dbConditionInt("hostid","i.hostid",params.getHostIds());
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("i","i.hostid");
			}
		}
		
		// triggerids
		if (!is_null(params.getTriggerIds())) {
			sqlParts.where.dbConditionInt("triggerid","t.triggerid",params.getTriggerIds());
		}
		
		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","f.itemid");
			sqlParts.from.put("functions","functions f");
			sqlParts.where.dbConditionInt("itemid","f.itemid",params.getItemIds());
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("f","f.itemid");
			}
		}
		
		// applicationids
		if (!is_null(params.getApplicationIds())) {
			sqlParts.select.put("applicationid","a.applicationid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("applications","applications a");
			sqlParts.where.dbConditionInt("a","a.applicationid",params.getApplicationIds());
			sqlParts.where.put("ia.tenantid","i.tenantid=a.tenantid");
			sqlParts.where.put("ia","i.hostid=a.hostid");
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=ia.tenantid");
			sqlParts.where.put("fi","f.itemid=ia.itemid");
		}
		
		// discoveryids
		if (!is_null(params.getDiscoveryIds())) {
			sqlParts.select.put("itemid","id.parent_itemid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("item_discovery","item_discovery id");
			sqlParts.where.put("fid.tenantid","f.tenantid=id.tenantid");
			sqlParts.where.put("fid","f.itemid=id.itemid");
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.dbConditionInt("id.parent_itemid",params.getDiscoveryIds());			

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("id","id.parent_itemid");
			}
		}
		
		// functions
		if (!is_null(params.getFunctions())) {
			sqlParts.from.put("functions","functions f");
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.dbConditionString("f.function",params.getFunctions());
		}
		
		// monitored
		if (!is_null(params.getMonitored())) {
			sqlParts.where.put("monitored"," NOT EXISTS ("+
					" SELECT NULL"+
					" FROM functions ff"+
					" WHERE ff.tenantid=t.tenantid"+
						" AND ff.triggerid=t.triggerid"+
						" AND EXISTS ("+
								" SELECT NULL"+
								" FROM items ii,hosts hh"+
								" WHERE ff.tenantid=ii.tenantid"+
									" AND hh.tenantid=ii.tenantid"+
									" AND ff.itemid=ii.itemid"+
									" AND hh.hostid=ii.hostid"+
									" AND ("+
										" ii.status<>"+ITEM_STATUS_ACTIVE+
										" OR hh.status<>"+HOST_STATUS_MONITORED+
									" )"+
						" )"+
				" )");
			sqlParts.where.put("status","t.status="+TRIGGER_STATUS_ENABLED);
		}
		
		// active
		if (!is_null(params.getActive())) {
			sqlParts.where.put("active"," NOT EXISTS ("+
					" SELECT NULL"+
					" FROM functions ff"+
					" WHERE ff.tenantid=t.tenantid"+
						" AND ff.triggerid=t.triggerid"+
						" AND EXISTS ("+
							" SELECT NULL"+
							" FROM items ii,hosts hh"+
							" WHERE ff.tenantid=ii.tenantid"+
								" AND hh.tenantid=ii.tenantid"+
								" AND ff.itemid=ii.itemid"+
								" AND hh.hostid=ii.hostid"+
								" AND  hh.status<>"+HOST_STATUS_MONITORED+
						" )"+
				" )");
			sqlParts.where.put("status","t.status="+TRIGGER_STATUS_ENABLED);
		}
		
		// maintenance
		if (!is_null(params.getMaintenance())) {
			sqlParts.where.put(((params.getMaintenance()==null || !params.getMaintenance())? "NOT " : "")+
					" EXISTS ("+
					" SELECT NULL"+
					" FROM functions ff"+
					" WHERE ff.tenantid=t.tenantid"+ 
						" AND ff.triggerid=t.triggerid"+
						" AND EXISTS ("+
								" SELECT NULL"+
								" FROM items ii,hosts hh"+
								" WHERE ff.tenantid=ii.tenantid"+
									" AND hh.tenantid=ii.tenantid"+
									" AND ff.itemid=ii.itemid"+
									" AND hh.hostid=ii.hostid"+
									" AND hh.maintenance_status=1"+
						" )"+
				" )");
			sqlParts.where.put("t.status="+TRIGGER_STATUS_ENABLED);
		}
		
		// templated
		if (!is_null(params.getTemplated())) {
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");

			if (params.getTemplated()) {
				sqlParts.where.put("h.status="+HOST_STATUS_TEMPLATE);
			} else {
				sqlParts.where.put("h.status<>"+HOST_STATUS_TEMPLATE);
			}
		}
		
		// inherited
		if (!is_null(params.getInherited())) {
			if (params.getInherited()) {
				sqlParts.where.put("t.templateid IS NOT NULL");
			} else {
				sqlParts.where.put("t.templateid IS NULL");
			}
		}
		
		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("triggers t", params, sqlParts);
		}
		
		// filter
		if (is_null(params.getFilter())) {
			params.setFilter(new HashMap());
		}
		
		if (params.getFilter() != null) {
			dbFilter("triggers t", params, sqlParts);
		
			if (isset(params.getFilter().get("host")) && !is_null(params.getFilter().get("host"))) {
				sqlParts.from.put("functions","functions f");
				sqlParts.from.put("items","items i");
				sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
				sqlParts.where.put("ft","f.triggerid=t.triggerid");
				sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
				sqlParts.where.put("fi","f.itemid=i.itemid");
				sqlParts.from.put("hosts","hosts h");
				sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
				sqlParts.where.put("hi","h.hostid=i.hostid");
				sqlParts.where.dbConditionString("host","h.host",TArray.as(params.getFilter().get("host")).asString());
			}

			if (isset(params.getFilter().get("hostid")) && !is_null(params.getFilter().get("hostid"))) {
				sqlParts.from.put("functions","functions f");
				sqlParts.from.put("items","items i");
				sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
				sqlParts.where.put("ft","f.triggerid=t.triggerid");
				sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
				sqlParts.where.put("fi","f.itemid=i.itemid");
				sqlParts.where.dbConditionInt("hostid","i.hostid",TArray.as(params.getFilter().get("hostid")).asLong());
			}
		}
		
		// group
		if (!is_null(params.getGroup())) {
			sqlParts.select.put("name","g.name");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.from.put("groups","groups g");
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
			sqlParts.where.put("hgi.tenantid","hg.tenantid=i.tenantid");
			sqlParts.where.put("hgi","hg.hostid=i.hostid");
			sqlParts.where.put("ghg.tenantid","g.tenantid = hg.tenantid");
			sqlParts.where.put("ghg","g.groupid = hg.groupid");
			sqlParts.where.put("group"," g.name="+sqlParts.marshalParam(params.getGroup()));
		}
		
		// host
		if (!is_null(params.getHost())) {
			sqlParts.select.put("host","h.host");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items","items i");
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.dbConditionInt("i","i.hostid",params.getHostIds());
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
			sqlParts.where.put("fi","f.itemid=i.itemid");
			sqlParts.where.put("hi.tenantid","h.tenantid=i.tenantid");
			sqlParts.where.put("hi","h.hostid=i.hostid");
			sqlParts.where.put("host"," h.host="+sqlParts.marshalParam(params.getHost()));
		}
		
		// min_severity
		if (!is_null(params.getMinSeverity())) {
			sqlParts.where.put("t.priority>="+sqlParts.marshalParam(params.getMinSeverity()));
		}
		
		// limit
		if (params.getLimit()!=null) {
			sqlParts.limit = params.getLimit();
		}
		
		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		CArray<Map> datas = DBselect(getSqlExecutor(),sqlParts);
		
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
				Long id = (Long)row.get("triggerid");
				
				Map resultRow = result.get(id);
				if (!isset(resultRow)) {
					resultRow = new IMap();
					result.put(id, resultRow);
				}
				
				// groups
				if (isset(row.get("groupid")) && is_null(params.getSelectGroups())) {
					if (!isset(resultRow,"groups")) {
						resultRow.put("groups", new CArray());
					}
					((CArray)resultRow.get("groups")).add(map("groupid", row.remove("groupid")));
				}
				
				// hostids
				if (isset(row.get("hostid")) && is_null(params.getSelectHosts())) {
					if (!isset(resultRow,"hosts")) {
						resultRow.put("hosts", new CArray());
					}
					((CArray)resultRow.get("hosts")).add(map("hostid", row.get("hostid")));
					if (is_null(params.getExpandData())) {
						row.remove("hostid");
					}
				}
				
				// itemids
				if (isset(row.get("itemid")) && is_null(params.getSelectItems())) {
					if (!isset(resultRow,"items")) {
						resultRow.put("items", new CArray());
					}
					((CArray)resultRow.get("items")).add(map("itemid", row.remove("itemid")));
				}
				
				// expand expression
				if (params.getExpandExpression() != null && isset(row,"expression")) {
					Nest.value(row,"expression").$(explode_exp(idBean, getSqlExecutor(), Nest.value(row,"expression").asString(), false, true));
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
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Create triggers.
	 *
	 * @param array  _triggers
	 * @param string _triggers['expression']
	 * @param string _triggers['description']
	 * @param int    _triggers['type']
	 * @param int    _triggers['priority']
	 * @param int    _triggers['status']
	 * @param string _triggers['comments']
	 * @param string _triggers['url']
	 * @param string _triggers['flags']
	 * @param int    _triggers['templateid']
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> triggers) {
		for(Map trigger : triggers) {
			CArray triggerDbFields = map(
				"description", null,
				"expression", null,
				"error", _("Trigger just added. No status update so far.")
			);
			if (!check_db_fields(triggerDbFields, trigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for trigger."));
			}

			// check for \"templateid\", because it is not allowed
			if (array_key_exists("templateid", trigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Cannot set \"templateid\" for trigger prototype \"%1$s\".", Nest.value(trigger,"description").asString()));
			}

			CTriggerExpression triggerExpression = new CTriggerExpression();
			if (!triggerExpression.parse(Nest.value(trigger,"expression").asString())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, triggerExpression.error);
			}

			checkIfExistsOnHost(trigger);

			// check item prototypes
			CArray<Map> items = getExpressionItems(this.idBean, getSqlExecutor(),triggerExpression);
			checkDiscoveryRuleCount(trigger, items);
		}

		createReal(triggers);

		for(Map trigger : triggers) {
			inherit(trigger);
		}

		return map("triggerids", rda_objectValues(triggers, "triggerid").valuesAsLong());
	}

	@Override
	public CArray<Long[]> update(CArray<Map> triggers) {
		CArray triggerIds = rda_objectValues(triggers, "triggerid");
		
		CTriggerPrototypeGet options = new CTriggerPrototypeGet();
		options.setTriggerIds(triggerIds.valuesAsLong());
		options.setEditable(true);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		CArray<Map> dbTriggers = get(options);

		triggers = extendObjects(tableName(), triggers, new String[]{"description"});

		for (Entry<Object, Map> e : triggers.entrySet()) {
            Object key = e.getKey();
            Map trigger = e.getValue();
			if (!isset(dbTriggers,trigger.get("triggerid"))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
			}

			if (!isset(trigger,"triggerid")) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Wrong fields for trigger."));
			}

			// check for \"templateid\", because it is not allowed
			if (array_key_exists("templateid", trigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
					_s("Cannot update \"templateid\" for trigger prototype \"%1$s\".", Nest.value(trigger,"description").$()));
			}

			Map dbTrigger = dbTriggers.get(trigger.get("triggerid"));

			if (isset(trigger,"expression")) {
				String expressionFull = Nest.as(explode_exp(idBean, getSqlExecutor(),Nest.value(dbTrigger,"expression").asString())).asString();
				if (strcmp(Nest.value(trigger,"expression").asString(), expressionFull) == 0) {
					unset(triggers,key,"expression");
				}

				// check item prototypes
				CTriggerExpression triggerExpression = new CTriggerExpression();
				if (!triggerExpression.parse(Nest.value(trigger,"expression").asString())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, triggerExpression.error);
				}

				// check item prototypes
				CArray items = getExpressionItems(this.idBean, getSqlExecutor(),triggerExpression);
				checkDiscoveryRuleCount(trigger, items);
			}

			if (isset(trigger,"description") && strcmp(Nest.value(trigger,"description").asString(), Nest.value(dbTrigger,"comments").asString()) == 0) {
				unset(triggers,key,"description");
			}
			if (isset(trigger,"priority") && Nest.value(trigger,"priority").asInteger() == Nest.value(dbTrigger,"priority").asInteger()) {
				unset(triggers,key,"priority");
			}
			if (isset(trigger,"type") && Nest.value(trigger,"type").asInteger() == Nest.value(dbTrigger,"type").asInteger()) {
				unset(triggers,key,"type");
			}
			if (isset(trigger,"comments") && strcmp(Nest.value(trigger,"comments").asString(), Nest.value(dbTrigger,"comments").asString()) == 0) {
				unset(triggers,key,"comments");
			}
			if (isset(trigger,"url") && strcmp(Nest.value(trigger,"url").asString(), Nest.value(dbTrigger,"url").asString()) == 0) {
				unset(triggers,key,"url");
			}
			if (isset(trigger,"status") && Nest.value(trigger,"status").asInteger() == Nest.value(dbTrigger,"status").asInteger()) {
				unset(triggers,key,"status");
			}

			checkIfExistsOnHost(trigger);
		}

		updateReal(triggers);

		for(Map trigger : triggers) {
			Nest.value(trigger,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);
			inherit(trigger);
		}

		return map("triggerids", triggerIds.valuesAsLong());
	}

	@Override
	public CArray<Long[]> delete(Long... triggerIds) {
		return delete(false,triggerIds);
	}
	
	public CArray<Long[]> delete(boolean nopermissions, Long... triggerIds) {
		if (empty(triggerIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		Long[] triggerPrototypeIds = Clone.deepcopy(triggerIds);

		CTriggerPrototypeGet options = new CTriggerPrototypeGet();
		options.setTriggerIds(TArray.as(triggerIds).asLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> delTriggers = get(options);

		// TODO: remove _nopermissions hack
		if (!nopermissions) {
			for(Long triggerId : triggerIds) {
				if (!isset(delTriggers,triggerId)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				if (Nest.value(delTriggers,triggerId,"templateid").asInteger() != 0) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Cannot delete templated trigger \"%1$s:%2$s\".",
							Nest.value(delTriggers,triggerId,"description").asString(),
							explode_exp(idBean, getSqlExecutor(),Nest.value(delTriggers,triggerId,"expression").asString()))
					);
				}
			}
		}

		// get child triggers
		CArray<Long> parentTriggerids = CArray.valueOf(Clone.deepcopy(triggerIds));
		CArray<Long> ctriggerIds = CArray.valueOf(triggerIds);
		do {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbItems = DBselect(getSqlExecutor(),"SELECT triggerid FROM triggers WHERE "+sqlParts.dual.dbConditionInt("templateid", parentTriggerids.valuesAsLong()),sqlParts.getNamedParams());
			parentTriggerids = array();
			for(Map dbTrigger : dbItems) {
				parentTriggerids.add(Nest.value(dbTrigger,"triggerid").asLong());
				Nest.value(ctriggerIds,dbTrigger.get("triggerid")).$(Nest.value(dbTrigger,"triggerid").$());
			}
		} while (!empty(parentTriggerids));
		triggerIds = ctriggerIds.valuesAsLong();

		// select all triggers which are deleted (include childs)
		options = new CTriggerPrototypeGet();
		options.setTriggerIds(TArray.as(triggerIds).asLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setEditable(true);
		options.setPreserveKeys(true);
		options.setSelectHosts(new String[]{"name"});
		delTriggers = get(options);

		// created triggers
		CArray createdTriggers = array();
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT triggerid FROM trigger_discovery WHERE "+sqlParts.dual.dbConditionInt("parent_triggerid", triggerIds);
		CArray<Map> dbTriggers = DBselect(getSqlExecutor(),sql, sqlParts.getNamedParams());
		for(Map trigger : dbTriggers) {
			Nest.value(createdTriggers,trigger.get("triggerid")).$(Nest.value(trigger,"triggerid").$());
		}
		if (!empty(createdTriggers)) {
			Object result = API.Trigger(this.idBean, this.getSqlExecutor()).delete(true,createdTriggers.valuesAsLong());
			if (empty(result)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete triggers created by low level discovery."));
			}
		}

		for(Map trigger : delTriggers) {
			add_audit_ext(this.idBean, getSqlExecutor(),AUDIT_ACTION_DELETE, AUDIT_RESOURCE_TRIGGER_PROTOTYPE, Nest.value(trigger,"triggerid").asLong(),
					Nest.value(trigger,"description").asString()+":"+Nest.value(trigger,"expression").$(), null, null, null);
		}

		delete("triggers", (Map)map("triggerid", triggerIds));

		return map("triggerids", triggerPrototypeIds);
	}

	@Override
	protected void createReal(CArray<Map> triggers) {
		if(empty(triggers)){
			return;
		}
		for (Entry<Object, Map> e : triggers.entrySet()) {
            Object key = e.getKey();
            Nest.value(triggers,key,"flags").$(RDA_FLAG_DISCOVERY_PROTOTYPE);
		}
		
		// insert triggers without expression
		CArray<Map> triggersCopy = Clone.deepcopy(triggers);
		for (int i = 0, size = count(triggersCopy); i < size; i++) {
			unset(triggersCopy,i,"expression");
		}

		CArray<Long> triggerIds = insert("triggers", triggersCopy);
		triggersCopy = null;

		for (Entry<Object, Map> e : triggers.entrySet()) {
            Object key = e.getKey();
            Map trigger = e.getValue();
            Long triggerId = triggerIds.get(key);
			Nest.value(triggers,key,"triggerid").$(triggerId);
			CArray hosts = array();

			String expression = null;
			try {
				expression  = implode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString(true), triggerId, hosts);
			} catch (Exception error) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Cannot implode expression \"%s\".", Nest.value(trigger,"expression").asString())+" "+error.getMessage());
			}

			update("triggers", array((Map)map(
				"values", map("expression", expression),
				"where", map("triggerid", triggerId)
			)));

			info(_s("Created: Trigger prototype \"%1$s\" on \"%2$s\".", Nest.value(trigger,"description").asString(), implode(", ", hosts)));
		}
	}

	@Override
	protected void updateReal(CArray<Map> triggers) {
		if(empty(triggers)){
			return;
		}
		CTriggerPrototypeGet options = new CTriggerPrototypeGet();
		options.setTriggerIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectHosts(new String[]{"name"});
		options.setPreserveKeys(true);
		options.setNopermissions(true);
		CArray<Map> dbTriggers = get(options);

		boolean descriptionChanged = false;
		boolean expressionChanged = false;
		for(Map trigger : triggers) {
			Map dbTrigger = dbTriggers.get(trigger.get("triggerid"));
			CArray<String> hosts = rda_objectValues(Nest.value(dbTrigger,"hosts").asCArray(), "name");

			if (isset(trigger,"description") && strcmp(Nest.value(dbTrigger,"description").asString(), Nest.value(trigger,"description").asString()) != 0) {
				descriptionChanged = true;
			}

			String expressionFull = Nest.as(explode_exp(idBean, getSqlExecutor(),Nest.value(dbTrigger,"expression").asString())).asString();
			if (isset(trigger,"expression") && strcmp(expressionFull, Nest.value(trigger,"expression").asString()) != 0) {
				expressionChanged = true;
				expressionFull = Nest.value(trigger,"expression").asString();
			}

			if (descriptionChanged || expressionChanged) {
				CTriggerExpression expressionData = new CTriggerExpression();
				if (!expressionData.parse(expressionFull)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, expressionData.error);
				}

				if (expressionData.expressions.size()==0 || !isset(expressionData.expressions.get(0))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_("Trigger expression must contain at least one host:key reference."));
				}
			}

			if (expressionChanged) {
				delete("functions", (Map)map("triggerid", Nest.value(trigger,"triggerid").$()));

				try {
					Nest.value(trigger,"expression").$(implode_exp(idBean, getSqlExecutor(),expressionFull, Nest.value(trigger,"triggerid").asLong(), hosts));
				} catch (Exception e) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Cannot implode expression \"%s\".", expressionFull)+" "+e.getMessage());
				}
			}

			Map triggerUpdate = new HashMap();
			triggerUpdate.putAll(trigger);			
			if (!descriptionChanged) {
				unset(triggerUpdate,"description");
			}
			if (!expressionChanged) {
				unset(triggerUpdate,"expression");
			}

			// skip updating read only values
			unset(triggerUpdate,"state");
			unset(triggerUpdate,"value");
			unset(triggerUpdate,"lastchange");
			unset(triggerUpdate,"error");

			update("triggers", array((Map)map(
				"values", triggerUpdate,
				"where", map("triggerid", Nest.value(trigger,"triggerid").$())
			)));

			String description = isset(trigger,"description") ? Nest.value(trigger,"description").asString() : Nest.value(dbTrigger,"description").asString();

			info(_s("Updated: Trigger prototype \"%1$s\" on \"%2$s\".", description, implode(", ", hosts)));
		}
	}

	public boolean syncTemplates(Map data) {
		CTriggerPrototypeGet options = new CTriggerPrototypeGet();
		options.setHostIds(Nest.array(data,"templateids").asLong());
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"triggerid", "expression", "description", "url", "status", "priority", "comments", "type"});
		CArray<Map> triggers = get(options);
		for(Map trigger : triggers) {
			Nest.value(trigger,"expression").$(explode_exp(idBean, getSqlExecutor(), Nest.value(trigger,"expression").asString()));
			inherit(trigger, Nest.array(data,"hostids").asLong());
		}
		return true;
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (is_null(params.getCountOutput())) {
			// expandData
			if (!is_null(Nest.value(params,"expandData").$())) {
				sqlParts.select.put("host", "h.host");
				sqlParts.select.put("hostid", "h.hostid");
				sqlParts.from.put("functions", "functions f");
				sqlParts.from.put("items", "items i");
				sqlParts.from.put("hosts", "hosts h");
				sqlParts.where.put("ft.tenantid", "f.tenantid=t.tenantid");
				sqlParts.where.put("ft", "f.triggerid=t.triggerid");
				sqlParts.where.put("fi.tenantid", "f.tenantid=i.tenantid");
				sqlParts.where.put("fi", "f.itemid=i.itemid");
				sqlParts.where.put("hi.tenantid", "h.tenantid=i.tenantid");
				sqlParts.where.put("hi", "h.hostid=i.hostid");
			}
		}
	}

	@Override
	protected void addRelatedObjects(CTriggerPrototypeGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] triggerids = result.keysAsLong();
		
		// adding items
		if (!is_null(params.getSelectItems()) && !API_OUTPUT_COUNT.equals(params.getSelectItems())) {
			CRelationMap relationMap = createRelationMap(result, "triggerid", "itemid", "functions");
			CItemGet options = new CItemGet();
			options.setOutput(params.getSelectItems());
			options.setItemIds(relationMap.getRelatedLongIds());
			options.setWebItems(true);
			options.setNopermissions(true);
			options.setPreserveKeys(true);
			options.setFilter("flags");
			CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(options);
			relationMap.mapMany(result, items, "items");
		}
		
		// adding discoveryrule
		if (!is_null(params.getSelectDiscoveryRule()) && !API_OUTPUT_COUNT.equals(params.getSelectDiscoveryRule())) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(
				getSqlExecutor(),
				"SELECT id.parent_itemid,f.triggerid"+
					" FROM item_discovery id,functions f"+
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "functions", "f", params)+
					" AND "+sqlParts.dual.dbConditionInt("f.triggerid", triggerids)+
					" AND f.tenantid=id.tenantid"+
					" AND f.itemid=id.itemid",
				sqlParts.getNamedParams()
			);
			CRelationMap relationMap = new CRelationMap();
			for(Map rule : dbRules) {
				relationMap.addRelation(Nest.value(rule,"triggerid").$(), Nest.value(rule,"parent_itemid").$());
			}
			
			CDiscoveryRuleGet options = new CDiscoveryRuleGet();
			options.setOutput(params.getSelectDiscoveryRule());
			options.setItemIds(relationMap.getRelatedLongIds());
			options.setNopermissions(true);
			options.setPreserveKeys(true);
			CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(options);
			relationMap.mapMany(result, discoveryRules, "discoveryRule");
		}
	}

	/**
	 * Check if trigger prototype has at least one item prototype and belongs to one discovery rule.
	 *
	 * @throws APIException if trigger prototype has no item prototype or items belong to multiple discovery rules.
	 *
	 * @param array  _trigger						array of trigger data
	 * @param string _trigger["description"]		trigger description
	 * @param array  _items							array of trigger items
	 */
	protected void checkDiscoveryRuleCount(Map trigger, CArray<Map> items) {
		if (!empty(items)) {
			CParamGet params = new CParamGet();
			params.setOutput(new String[]{"parent_itemid"});
			params.setFilter("itemid", rda_objectValues(items, "itemid").valuesAsString());
			CArray<Map> itemDiscoveries = select("item_discovery", params);

			CArray<Long> itemDiscoveryIds = array_flip(rda_objectValues(itemDiscoveries, "parent_itemid"));

			if (count(itemDiscoveryIds) > 1) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Trigger prototype \"%1$s\" contains item prototypes from multiple discovery rules.",
					Nest.value(trigger,"description").asString()
				));
			} else if (empty(itemDiscoveryIds)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s(
					"Trigger prototype \"%1$s\" must contain at least one item prototype.",
					Nest.value(trigger,"description").asString()
				));
			}
		}
	}
}
