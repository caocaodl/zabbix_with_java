package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_fill_keys;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit_ext;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArray;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.ACTION_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER;
import static com.isoft.iradar.inc.Defines.CONDITION_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_NOT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_ON;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.SERVICE_SHOW_SLA_OFF;
import static com.isoft.iradar.inc.Defines.SYSMAP_ELEMENT_TYPE_TRIGGER;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ServicesUtil.updateItServices;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.TriggersUtil.get_hosts_by_triggerid;
import static com.isoft.iradar.inc.TriggersUtil.implode_exp;
import static com.isoft.iradar.inc.TriggersUtil.replace_template_dependencies;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CDbConfig;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.operator.COperator.CMapOperator;
import com.isoft.iradar.parsers.CTriggerExpression;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.object.CUpdateDiscoveredValidator;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

public class CTriggerDAO extends CTriggerGeneralDAO<CTriggerGet> {

	public CTriggerDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "triggers", "t", new String[]{"triggerid", "description", "status", "priority", "lastchange", "hostname"});
	}
	
	@Override
	public <T> T get(CTriggerGet params) {
		SqlBuilder sqlParts = new SqlBuilder();		
		sqlParts.select.put("triggers", "t.triggerid");
		sqlParts.from.put("t", "triggers t");
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			Arrays.sort(params.getGroupIds());
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
			sqlParts.select.put("applicationid","ia.applicationid");
			sqlParts.from.put("functions","functions f");
			sqlParts.from.put("items_applications","items_applications ia");
			sqlParts.where.dbConditionInt("a","ia.applicationid",params.getApplicationIds());
			sqlParts.where.put("ft.tenantid","f.tenantid=t.tenantid");
			sqlParts.where.put("ft","f.triggerid=t.triggerid");
			sqlParts.where.put("fi.tenantid","f.tenantid=ia.tenantid");
			sqlParts.where.put("fi","f.itemid=ia.itemid");
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
			sqlParts.where.put("monitored","NOT EXISTS ("+
					"SELECT NULL"+
					" FROM functions f,items i,hosts h"+
					" WHERE t.tenantid=f.tenantid"+
					" AND f.tenantid=i.tenantid"+
					" AND i.tenantid=h.tenantid"+
					" AND t.triggerid=f.triggerid"+
					" AND f.itemid=i.itemid"+
					" AND i.hostid=h.hostid"+
					" AND ("+
						"i.status<>"+ITEM_STATUS_ACTIVE+
						" OR h.status<>"+HOST_STATUS_MONITORED+
						")"+
					")");
			sqlParts.where.put("status","t.status="+TRIGGER_STATUS_ENABLED);
		}

		// active
		if (!is_null(params.getActive())) {
			sqlParts.where.put("active","NOT EXISTS ("+
					"SELECT NULL"+
					" FROM functions f,items i,hosts h"+
					" WHERE t.tenantid=f.tenantid"+
					" AND f.tenantid=i.tenantid"+
					" AND i.tenantid=h.tenantid"+
					" AND t.triggerid=f.triggerid"+
					" AND f.itemid=i.itemid"+
					" AND i.hostid=h.hostid"+
					" AND h.status<>"+HOST_STATUS_MONITORED+
					")");
			sqlParts.where.put("status","t.status="+TRIGGER_STATUS_ENABLED);
		}

		// maintenance
		if (!is_null(params.getMaintenance())) {
			sqlParts.where.put(((params.getMaintenance()==null || !params.getMaintenance())? "NOT " : "")+"EXISTS ("+
					"SELECT NULL"+
					" FROM functions f,items i,hosts h"+
					" WHERE t.tenantid=f.tenantid"+
					" AND f.tenantid=i.tenantid"+
					" AND i.tenantid=h.tenantid"+
					" AND t.triggerid=f.triggerid"+
					" AND f.itemid=i.itemid"+
					" AND i.hostid=h.hostid"+
					" AND h.maintenance_status="+HOST_MAINTENANCE_STATUS_ON+
					")");
			sqlParts.where.put("t.status="+TRIGGER_STATUS_ENABLED);
		}

		// lastChangeSince
		if (!is_null(params.getLastChangeSince())) {
			sqlParts.where.put("lastchangesince","t.lastchange>"+sqlParts.marshalParam(params.getLastChangeSince()));
		}

		// lastChangeTill
		if (!is_null(params.getLastChangeTill())) {
			sqlParts.where.put("lastchangetill","t.lastchange<"+sqlParts.marshalParam(params.getLastChangeTill()));
		}

		// withUnacknowledgedEvents
		if (!is_null(params.getWithUnacknowledgedEvents())) {
			sqlParts.where.put("unack","EXISTS ("+
					"SELECT NULL"+
					" FROM events e"+
					" WHERE t.tenantid=e.tenantid"+
					" AND t.triggerid=e.objectid"+
					" AND e.source="+EVENT_SOURCE_TRIGGERS+
					" AND e.object="+EVENT_OBJECT_TRIGGER+
					" AND e.value="+TRIGGER_VALUE_TRUE+
					" AND e.acknowledged="+EVENT_NOT_ACKNOWLEDGED+
					")");
		}
		// withAcknowledgedEvents
		if (!is_null(params.getWithAcknowledgedEvents())) {
			sqlParts.where.put("ack","NOT EXISTS ("+
					"SELECT NULL"+
					" FROM events e"+
					" WHERE e.tenantid=t.tenantid"+
					" AND e.objectid=t.triggerid"+
					" AND e.source="+EVENT_SOURCE_TRIGGERS+
					" AND e.object="+EVENT_OBJECT_TRIGGER+
					" AND e.value="+TRIGGER_VALUE_TRUE+
					" AND e.acknowledged="+EVENT_NOT_ACKNOWLEDGED+
					")");
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
			if (!params.getFilter().containsKey("flags")) {
				params.getFilter().put("flags", new String[]{String.valueOf(RDA_FLAG_DISCOVERY_NORMAL), String.valueOf(RDA_FLAG_DISCOVERY_CREATED)});
			}
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
		
		// only_true
		if (!is_null(params.getOnly_true())) {
			CDbConfig config = this.getDbConfig();
			sqlParts.where.put("ot","((t.value="+TRIGGER_VALUE_TRUE+")"+
					" OR "+
					"((t.value="+TRIGGER_VALUE_FALSE+") AND (t.lastchange>"+(System.currentTimeMillis()/1000 - config.getOk_period())+")))");
			
		}

		// min_severity
		if (!is_null(params.getMinSeverity())) {
			sqlParts.where.put("t.priority>="+sqlParts.marshalParam(params.getMinSeverity()));
		}
		
		// limit
		if (!empty(params.getLimit())) {
			sqlParts.limit = null;
		}

		applyQueryOutputOptions(tableName(), tableAlias(), params, sqlParts);
		applyQuerySortOptions(tableName(), tableAlias(), params, sqlParts);
		applyQueryTenantOptions(tableName(), tableAlias(), params, sqlParts);
		
		if (!is_null(params.getCountOutput()) && !requiresPostSqlFiltering(params)) {
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CArray<Map> result = new CArray<Map>();
			Object ret = result;
			for(Map row : datas){
				if (params.getGroupCount() != null) {
					result.add(row);
				} else {
					ret = row.get("rowscount");
				}
			}
			return (T)ret;
		}
		
		CArray<Map> result = rda_toHash(customFetch(sqlParts.createSelectQueryFromParts(), sqlParts.getNamedParams(), params), "triggerid");
		
		if (!is_null(params.getCountOutput())) {
			return (T)Integer.valueOf(result.size());
		}
		
		if (!is_null(params.getGroupIds()) && is_null(params.getSelectGroups())) {
			params.setSelectGroups(API_OUTPUT_REFER);
		}
		
		if (!is_null(params.getHostIds()) && is_null(params.getSelectHosts())) {
			params.setSelectHosts(API_OUTPUT_REFER);
		}

		if (!is_null(params.getItemIds()) && is_null(params.getSelectItems())) {
			params.setSelectItems(new String[]{"itemid"});
		}
		
		addRelatedObjects(params, result);
		
		// expandDescription
		if (!is_null(params.getExpandDescription()) && !empty(result) && reset(result).containsKey("description")) {
			CMacrosResolverHelper.resolveTriggerNames(this.idBean, this.getSqlExecutor(),result);
		}
		
		// expandComment
		if (!is_null(params.getExpandComment()) && !empty(result) && reset(result).containsKey("comment")) {
			CMacrosResolverHelper.resolveTriggerDescriptions(this.idBean, this.getSqlExecutor(),result);
		}
		
		// deprecated fields
		handleDeprecatedOutput(result, "value_flag", "state", params.getOutput());
		
		// unset extra fields
		List<String> extraFields = new ArrayList();
		extraFields.add("state");
		extraFields.add("expression");
		if (is_null(params.getExpandData())) {
			extraFields.add("hostname");
		}
		unsetExtraFields(result, extraFields.toArray(new String[0]), params.getOutput());
		return (T)result;
	}
	
	/**
	 * Get triggerid by host.host and trigger.expression.
	 *
	 * @param array _triggerData multidimensional array with trigger objects
	 * @param array _triggerData[0,...]["expression"]
	 * @param array _triggerData[0,...]["host"]
	 * @param array _triggerData[0,...]["hostid"] OPTIONAL
	 * @param array _triggerData[0,...]["description"] OPTIONAL
	 * @return 
	 *
	 * @return array|int
	 */
	public CArray<Map> getObjects(CArray triggerData) {
		CTriggerGet options = new CTriggerGet();
		options.setOutput(API_OUTPUT_EXTEND);
		options.setFilter(triggerData);

		// expression is checked later
		unset(options.getFilter(),"expression");
		CArray<Map> result = get(options);
		if (isset(triggerData,"expression")) {
			for (Entry<Object, Map> e : result.entrySet()) {
                Object tnum = e.getKey();
                Map trigger = e.getValue();
				String tmpExp = Nest.as(explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString())).asString();
				if (strcmp(trim(tmpExp, ' '), trim(Nest.value(triggerData,"expression").asString(), ' ')) != 0) {
					unset(result,tnum);
				}
			}
		}

		return result;
	}
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("hostid", "host"), "description");
		
		if (!isset(object,"hostid") && !isset(object,"host")) {
			CTriggerExpression expressionData = new CTriggerExpression();
			if (!expressionData.parse(Nest.value(object,"expression").asString())) {
				return false;
			}
			CArray<String> expressionHosts = expressionData.getHosts();
			Nest.value(object,"host").$(reset(expressionHosts));
		}
		
		CTriggerGet options = new CTriggerGet();
		options.setFilter(array_merge(rda_array_mintersect(keyFields, object),map("flags", null)));
		options.setOutput(API_OUTPUT_EXTEND);
		options.setNopermissions(true);
		CArray<Map> triggers = get(options);
		boolean result = false;
		for(Map trigger : triggers) {
			String tmpExp = Nest.as(explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString())).asString();
			if (strcmp(tmpExp, Nest.value(object,"expression").asString()) == 0) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Check input.
	 *
	 * @param array  _triggers
	 * @param string _method
	 */
	public void checkInput(CArray<Map> triggers, String method) {
		boolean update = ("update".equals(method));

		CArray triggerDbFields = null;
		CArray<Map> dbTriggers = null;
		// permissions
		if (update) {
			triggerDbFields  = map("triggerid", null);

			CTriggerGet options = new CTriggerGet();
			options.setTriggerIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
			options.setOutput(API_OUTPUT_EXTEND);
			options.setEditable(true);
			options.setPreserveKeys(true);
			options.setSelectDependencies(API_OUTPUT_REFER);
			dbTriggers  = get(options);

			CUpdateDiscoveredValidator _updateDiscoveredValidator = CValidator.init(new CUpdateDiscoveredValidator(),map(
				"allowed", array("triggerid", "status"),
				"messageAllowedField", _("Cannot update \"%1$s\" for a discovered trigger.")
			));
			for(Map trigger : triggers) {
				// check permissions
				if (!isset(dbTriggers, trigger.get("triggerid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}

				// discovered fields, except status, cannot be updated
				checkPartialValidator(trigger, _updateDiscoveredValidator, dbTriggers.get(trigger.get("triggerid")));
			}

			triggers = extendObjects(tableName(), triggers, new String[]{"description"});
		} else {
			triggerDbFields = map(
				"description", null,
				"expression", null,
				"value", TRIGGER_VALUE_FALSE
			);
		}

		for (Entry<Object, Map> e : triggers.entrySet()) {
            Object tnum = e.getKey();
            Map trigger = e.getValue();
			Map currentTrigger = Clone.deepcopy(triggers.get(tnum));

			checkNoParameters(
				trigger,
				new String[]{"templateid", "state", "value", "value_flags"},
				(update ? _("Cannot update \"%1$s\" for trigger \"%2$s\".") : _("Cannot set \"%1$s\" for trigger \"%2$s\".")),
				Nest.value(trigger,"description").asString()
			);

			if (!check_db_fields(triggerDbFields, trigger)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect fields for trigger."));
			}

			boolean expressionChanged = true;
			if (update) {
				Map dbTrigger = dbTriggers.get(trigger.get("triggerid"));
				if (isset(trigger,"expression")) {
					String expressionFull = Nest.as(explode_exp(idBean, getSqlExecutor(),Nest.value(dbTrigger,"expression").asString())).asString();
					if (strcmp(Nest.value(trigger,"expression").asString(), expressionFull) == 0) {
						expressionChanged = false;
					}
				}
				if (isset(trigger,"description") && strcmp(Nest.value(trigger,"description").asString(), Nest.value(dbTrigger,"description").asString()) == 0) {
					unset(trigger,"description");
				}
			}

			// if some of the properties are unchanged, no need to update them in DB
			// validating trigger expression
			if (isset(trigger,"expression") && expressionChanged) {
				// expression permissions
				CTriggerExpression expressionData = new CTriggerExpression(map("lldmacros", false));
				if (!expressionData.parse(Nest.value(trigger,"expression").asString())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, expressionData.error);
				}

				if (expressionData.expressions == null || expressionData.expressions.size()==0 || expressionData.expressions.get(0)==null) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_("Trigger expression must contain at least one host:key reference."));
				}

				CArray<String> expressionHosts = expressionData.getHosts();

				CHostGet params = new CHostGet();
				params.setFilter("host", expressionHosts.valuesAsString());
				params.setEditable(true);
				params.setOutput(new String[]{"hostid","host","status"});
				params.setTemplatedHosts(true);
				params.setPreserveKeys(true);
				CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(params);
				hosts = rda_toHash(hosts, "host");
				int hostsStatusFlags = 0x0;
				for(String host : expressionHosts) {
					if (!isset(hosts,host)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Incorrect trigger expression. Host \"%s\" does not exist or you have no access to this host.", host));
					}

					// find out if both templates and hosts are referenced in expression
					hostsStatusFlags |= (Nest.value(hosts,host,"status").asInteger() == HOST_STATUS_TEMPLATE) ? 0x1 : 0x2;
					if (hostsStatusFlags == 0x3) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect trigger expression. Trigger expression elements should not belong to a template and a host simultaneously."));
					}
				}

				for(Map exprPart : expressionData.expressions) {
					SqlBuilder sqlParts = new SqlBuilder();
					String sql = "SELECT i.itemid,i.value_type"+
							" FROM items i,hosts h"+
							" WHERE i.key_=" + sqlParts.marshalParam("key_", Nest.value(exprPart,"item").$()) +
								" AND "+ sqlParts.where.dbConditionInt("i.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED})+
								" AND h.host=" + sqlParts.marshalParam("host", Nest.value(exprPart,"host").$()) +
								" AND h.tenantid=i.tenantid"+
								" AND h.hostid=i.hostid";
					if (empty(DBselect(getSqlExecutor(), sql, sqlParts.getNamedParams()))) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("Incorrect item key \"%1$s\" provided for trigger expression on \"%2$s\".", Nest.value(exprPart,"item").$(), Nest.value(exprPart,"host").$()));
					}
				}
			}

			// check existing
			checkIfExistsOnHost(currentTrigger);
		}
	}
	
	/**
	 * Add triggers
	 *
	 * Trigger params: expression, description, type, priority, status, comments, url, templateid
	 *
	 * @param array _triggers
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> triggers) {
		checkInput(triggers, "create");
		createReal(triggers);

		for(Map trigger : triggers) {
			inherit(trigger);
		}

		// clear all dependencies on inherited triggers
		deleteDependencies(triggers);

		// add new dependencies
		for(Map trigger : triggers) {
			if (!empty(Nest.value(trigger,"dependencies").$())) {
				CArray<Map> newDeps = array();
				for(Map depTrigger : (CArray<Map>)Nest.value(trigger,"dependencies").asCArray()) {
					newDeps.add(map(
						"triggerid", Nest.value(trigger,"triggerid").$(),
						"dependsOnTriggerid", Nest.value(depTrigger,"triggerid").$()
					));
				}
				addDependencies(newDeps);
			}
		}

		return map("triggerids", rda_objectValues(triggers, "triggerid").valuesAsLong());
	}

	/**
	 * Update triggers.
	 *
	 * If a trigger expression is passed in any of the triggers, it must be in it's exploded form.
	 *
	 * @param array _triggers
	 *
	 * @return boolean
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> triggers) {
		CArray triggerids = rda_objectValues(triggers, "triggerid");

		checkInput(triggers, "update");
		updateReal(triggers);

		for(Map trigger : triggers) {
			inherit(trigger);

			// replace dependencies
			if (isset(trigger,"dependencies")) {
				deleteDependencies(array(trigger));

				if (!empty(Nest.value(trigger,"dependencies").$())) {
					CArray<Map> newDeps = array();
					for(Map depTrigger : (CArray<Map>)Nest.value(trigger,"dependencies").asCArray()) {
						newDeps.add(map(
							"triggerid", Nest.value(trigger,"triggerid").$(),
							"dependsOnTriggerid", Nest.value(depTrigger,"triggerid").$()
						));
					}
					addDependencies(newDeps);
				}
			}
		}

		return map("triggerids", triggerids.valuesAsLong());
	}
	
	/**
	 * Delete triggers.
	 *
	 * @param array _triggerIds
	 * @param bool  _nopermissions
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... triggerIds) {
		return delete(false, triggerIds);
	}
	
	public CArray<Long[]> delete(boolean nopermissions, Long... triggerIds) {
		validateDelete(nopermissions, triggerIds);

		CArray<Long> ctriggerIds = array(triggerIds);
		// get child triggers
		CArray<Long> parentTriggerIds = array(triggerIds);

		do {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbItems = DBselect(getSqlExecutor(), "SELECT triggerid FROM triggers WHERE "+sqlParts.dual.dbConditionInt("templateid", parentTriggerIds.valuesAsLong()),sqlParts.getNamedParams());
			parentTriggerIds = array();

			for(Map dbTrigger : dbItems) {
				parentTriggerIds.add(Nest.value(dbTrigger,"triggerid").asLong());
				ctriggerIds.add(Nest.value(dbTrigger,"triggerid").asLong());
			}
		} while (!empty(parentTriggerIds));

		CTriggerGet options = new CTriggerGet();
		options.setTriggerIds(ctriggerIds.valuesAsLong());
		options.setOutput(new String[]{"triggerid", "description", "expression"});
		options.setNopermissions(true);
		options.setSelectHosts(new String[]{"name"});
		// select all triggers which are deleted (including children)
		CArray<Map> delTriggers = get(options);

		for(Map trigger : delTriggers) {
			add_audit_ext(this.idBean, getSqlExecutor(), AUDIT_ACTION_DELETE, AUDIT_RESOURCE_TRIGGER, Nest.value(trigger,"triggerid").asLong(),
					Nest.value(trigger,"description").asString(), null, null, null);
		}

		// execute delete
		deleteByIds(ctriggerIds);

		return map("triggerids", ctriggerIds.valuesAsLong());
	}
	
	
	
	/**
	 * Validates the input parameters for the delete() method.
	 *
	 * @throws APIException if the input is invalid
	 *
	 * @param array     _triggerIds
	 * @param boolean   _nopermissions  if set to true permissions will not be checked
	 *
	 * @return void
	 */
	protected void validateDelete(boolean nopermissions, Long... triggerIds) {
		if (empty(triggerIds)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		if (!nopermissions) {
			checkPermissions(triggerIds);
			checkNotInherited(triggerIds);
		}
	}
	
	/**
	 * Delete trigger by ids.
	 *
	 * @param array _triggerIds
	 */
	@Override
	protected void deleteByIds(CArray<Long> triggerIds) {
		// others idx should be deleted as well if they arise at some point
		delete("profiles", (Map)map(
			"idx", "web.events.filter.triggerid",
			"value_id", triggerIds.valuesAsLong()
		));

		delete("sysmaps_elements", (Map)map(
			"elementid", triggerIds.valuesAsLong(),
			"elementtype", SYSMAP_ELEMENT_TYPE_TRIGGER
		));

		// disable actions
		CArray<Long> actionIds = array();

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbActions = DBselect(getSqlExecutor(),
			"SELECT DISTINCT actionid"+
			" FROM conditions"+
			" WHERE conditiontype="+CONDITION_TYPE_TRIGGER+
				" AND "+sqlParts.dual.dbConditionString("value", triggerIds.valuesAsString()),
			sqlParts.getNamedParams()
		);
		for(Map dbAction : dbActions) {
			Nest.value(actionIds,dbAction.get("actionid")).$(Nest.value(dbAction,"actionid").$());
		}

		sqlParts = new SqlBuilder();
		DBexecute(getSqlExecutor(),"UPDATE actions SET status="+ACTION_STATUS_DISABLED+" WHERE "+sqlParts.dual.dbConditionInt("actionid", actionIds.valuesAsLong()),sqlParts.getNamedParams());

		// delete action conditions
		delete("conditions", (Map)map(
			"conditiontype", CONDITION_TYPE_TRIGGER,
			"value", triggerIds.valuesAsLong()
		));

		if (usedInItServices(triggerIds)) {
			update("services", array((Map)map(
				"values", map(
					"triggerid", null,
					"showsla", SERVICE_SHOW_SLA_OFF
				),
				"where", map(
					"triggerid", triggerIds.valuesAsLong()
				)
			)));
			updateItServices(this.idBean, getSqlExecutor());
		}

		super.deleteByIds(triggerIds);
	}

	/**
	 * Validates the input for the addDependencies() method.
	 *
	 * @throws APIException if the given dependencies are invalid
	 *
	 * @param array _triggersData
	 */
	protected void validateAddDependencies(CArray<Map> triggersData) {
		if (empty(triggersData)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		CArray depTtriggerIds = array();
		CArray<Map> triggers = array();
		for(Map dep : triggersData) {
			Object triggerId = Nest.value(dep,"triggerid").$();

			if (!isset(triggers,triggerId)) {
				Nest.value(triggers,triggerId).$(map(
					"triggerid", triggerId,
					"dependencies",array()
				));
			}
			Nest.value(triggers,triggerId,"dependencies").asCArray().add(Nest.value(dep,"dependsOnTriggerid").$());
			Nest.value(depTtriggerIds,dep.get("dependsOnTriggerid")).$(Nest.value(dep,"dependsOnTriggerid").$());
		}

		if (!isReadable(depTtriggerIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		checkDependencies(triggers);
		checkDependencyParents(triggers);
		checkDependencyDuplicates(triggers);
	}
	
	/**
	 * Add the given dependencies and inherit them on all child triggers.
	 *
	 * @param array _triggersData   an array of trigger dependency pairs, each pair in the form of
	 *                              array("triggerid" => 1, "dependsOnTriggerid" => 2)
	 * @return 
	 *
	 * @return array
	 */
	public CArray<Long> addDependencies(CArray<Map> triggersData) {
		CArray<Long> triggerIds = array_unique(rda_objectValues(triggersData, "triggerid"));
		if (!isWritable(triggerIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}

		validateAddDependencies(triggersData);

		for(Map dep : triggersData) {
			String triggerId = Nest.value(dep,"triggerid").asString();
			Object depTriggerId = Nest.value(dep,"dependsOnTriggerid").$();

			insert("trigger_depends", (CArray)array(map(
				"triggerid_down", triggerId,
				"triggerid_up", depTriggerId
			)));

			// propagate the dependencies to the child triggers
			CParamGet params = new CParamGet();
			params.setOutput(new String[]{"triggerid"});
			params.setFilter("templateid", triggerId);
			CArray<Map> childTriggers = select(tableName(), params);
			if (!empty(childTriggers)) {
				for(Map childTrigger : childTriggers) {
					CArray<Map> childHostsQuery = get_hosts_by_triggerid(this.idBean, getSqlExecutor(),Nest.value(childTrigger,"triggerid").asLong());
					for(Map childHost : childHostsQuery) {
						CArray<Object> newDep = map(Nest.value(childTrigger,"triggerid").$(), depTriggerId);
						newDep = replace_template_dependencies(this.idBean, getSqlExecutor(), newDep, Nest.value(childHost,"hostid").asString());

						addDependencies(array((Map)map(
							"triggerid", Nest.value(childTrigger,"triggerid").$(),
							"dependsOnTriggerid", newDep.get(childTrigger.get("triggerid"))
						)));
					}
				}
			}
		}

		return map("triggerids", triggerIds.valuesAsLong());
	}
	
	/**
	 * Validates the input for the deleteDependencies() method.
	 *
	 * @throws APIException if the given input is invalid
	 *
	 * @param array _triggers
	 */
	protected void validateDeleteDependencies(CArray<Map> triggers) {
		if (empty(triggers)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}
	}
	
	/**
	 * Deletes all trigger dependencies from the given triggers and their children.
	 *
	 * @param array _triggers   an array of triggers with the "triggerid" field defined
	 * @return 
	 *
	 * @return boolean
	 */
	public CArray<Long> deleteDependencies(CArray<Map> triggers) {
		validateDeleteDependencies(triggers);

		CArray<Long> triggerids = rda_objectValues(triggers, "triggerid");

		try {
			// delete the dependencies from the child triggers
			CParamGet options = new CParamGet();
			options.setOutput(new String[]{"triggerid"});
			options.setFilter("templateid", triggerids.valuesAsString());
			CArray<Map> childTriggers = select(tableName(), options);
			if (!empty(childTriggers)) {
				deleteDependencies(childTriggers);
			}

			delete("trigger_depends", (Map)map(
				"triggerid_down", triggerids.valuesAsLong()
			));
		} catch (APIException e) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot delete dependency"));
		}

		return map("triggerids", triggerids.valuesAsLong());
	}
	
	@Override
	protected void createReal(CArray<Map> triggers) {
		if(empty(triggers)){
			return;
		}
		// insert triggers without expression
		CArray<Map> triggersCopy = Clone.deepcopy(triggers);
		for (int i = 0, size = count(triggersCopy); i < size; i++) {
			unset(triggersCopy.get(i),"expression");
		}
		CArray<Long> triggerIds = insert("triggers", triggersCopy);
		unset(triggersCopy);

		CArray<String> allHosts = array();
		CArray<String> allowedHosts = array();
		CArray<CArray<String>> triggersAndHosts = array();
		CArray<String> triggerExpression = array();
		for (Entry<Object, Map> e : triggers.entrySet()) {
            Object tnum = e.getKey();
            Map trigger = e.getValue();
            Long triggerId = Nest.value(triggerIds,tnum).asLong();
            Nest.value(triggers,tnum,"triggerid").$(triggerId);
            CArray<String> hosts = array();

			String expression = null;
			try {
				expression  = implode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString(), triggerId, hosts);
			} catch (Exception ex) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS,
						_s("Cannot implode expression \"%s\".", Nest.value(trigger,"expression").$())+" "+ex.getMessage());
			}

			validateItems(trigger);

			for(String host : hosts) {
				allHosts.add(host);
			}

			Nest.value(triggersAndHosts,triggerId).$(hosts);
			Nest.value(triggerExpression,triggerId).$(expression);
		}

		allHosts = array_unique(allHosts);
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbHostsStatuses = DBselect(getSqlExecutor(),
			"SELECT h.host,h.status"+
			" FROM hosts h"+
			" WHERE "+sqlParts.dual.dbConditionString("h.host", allHosts.valuesAsString()),
			sqlParts.getNamedParams()
		);
		for(Map allowedHostsData : dbHostsStatuses) {
			if (Nest.value(allowedHostsData,"status").asInteger() != HOST_STATUS_TEMPLATE) {
				allowedHosts.add(Nest.value(allowedHostsData,"host").asString());
			}
		}

		// update triggers expression
		for (Entry<Object, Map> e : triggers.entrySet()) {
            Object tnum = e.getKey();
            Map trigger = e.getValue();
			Long triggerId = Nest.value(triggerIds,tnum).asLong();
            Nest.value(triggers,tnum,"triggerid").$(triggerId);

			update("triggers", array((Map)map(
				"values", map("expression", triggerExpression.get(triggerId)),
				"where", map("triggerid", triggerId)
			)));

			info(_s("Created: Trigger \"%1$s\" on \"%2$s\".", getDescription((String)Nest.value(trigger,"description").$()), implode(", ", triggersAndHosts.get(triggerId))));
			add_audit_ext(this.idBean, getSqlExecutor(), AUDIT_ACTION_ADD, AUDIT_RESOURCE_TRIGGER, Nest.as(triggerId).asLong(),
					Nest.value(trigger,"description").asString(), null, null, null);
		}
	}
	
    protected  String getDescription(String description){// remove "=" of the description
	if(description!=null){
    		if(description.contains("=")){
    			String[] des=description.split("=");
                if(des.length==2){
                	return des[1];
                }else{
                	return description;
                }
    		}else{
    			return description;
    		}
    	}
    	return null;
    }

	@Override
	protected void updateReal(CArray<Map> triggers) {
		if(empty(triggers)){
			return;
		}
		CArray<String> infos = array();

		CArray triggerIds = rda_objectValues(triggers, "triggerid");

		CTriggerGet options = new CTriggerGet();
		options.setTriggerIds(triggerIds.valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectHosts(new String[]{"name"});
		options.setSelectDependencies(API_OUTPUT_REFER);
		options.setPreserveKeys(true);
		options.setNopermissions(true);
		CArray<Map> dbTriggers = get(options);

		boolean descriptionChanged = false;
		boolean expressionChanged = false;
		CArray<Long> changedPriorityTriggerIds = array();

		for(Map trigger : triggers) {
			Map dbTrigger = dbTriggers.get(trigger.get("triggerid"));
			CArray<String> hosts = rda_objectValues(Nest.value(dbTrigger,"hosts").asCArray(), "name");

			if (isset(trigger,"description") && strcmp(Nest.value(dbTrigger,"description").asString(), Nest.value(trigger,"description").asString()) != 0) {
				descriptionChanged = true;
			} else {
				Nest.value(trigger,"description").$(Nest.value(dbTrigger,"description").$());
			}

			String oldExpression = Nest.as(explode_exp(idBean, getSqlExecutor(),Nest.value(dbTrigger,"expression").asString())).asString();
			String expressionFull = null;
			if (isset(trigger,"expression") && strcmp(oldExpression, Nest.value(trigger,"expression").asString()) != 0) {
				validateItems(trigger);

				expressionChanged = true;
				expressionFull  = Nest.value(trigger,"expression").asString();
			}

			if (expressionChanged) {
				// check the expression
				CTriggerExpression expressionData = new CTriggerExpression();
				if (!expressionData.parse(expressionFull)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, expressionData.error);
				}

				// remove triggers if expression is changed in a way that trigger will not appear in current host
				CTriggerExpression oldExpressionData = new CTriggerExpression();
				oldExpressionData.parse(oldExpression);
				// check if at least one template has stayed in expression, this means that child trigger will stay in host
				CArray<String> oldTemplates = oldExpressionData.getHosts();
				CArray newTemplates = rda_toHash(expressionData.getHosts());
				boolean proceed = true;
				for(String oldTemplate : oldTemplates) {
					if (isset(newTemplates,oldTemplate)) {
						proceed = false;
						break;
					}
				}
				// proceed if there is possibility that child triggers should be deleted
				if (proceed) {
					SqlBuilder sqlParts = new SqlBuilder();
					String sql = "SELECT t.triggerid"+
							" FROM triggers t"+
							" WHERE t.templateid="+sqlParts.marshalParam("templateid", Nest.value(trigger,"triggerid").asLong());
					CArray<Map> cTrigCursor = DBselect(getSqlExecutor(), sql, sqlParts.getNamedParams());
					CArray<Long> cTrigIds = array();
					for(Map cTrig : cTrigCursor) {
						// get templates linked to templated trigger host
						sqlParts = new SqlBuilder();
						CArray<Map> templateNames = DBfetchArrayAssoc(DBselect(getSqlExecutor(), 
							"SELECT h.name"+
							" FROM hosts h, hosts_templates ht, items i, functions f"+
							" WHERE h.tenantid = ht.tenantid AND ht.tenantid = i.tenantid AND i.tenantid = f.tenantid "+
							" AND h.hostid = ht.templateid AND ht.hostid = i.hostid AND i.itemid = f.itemid AND"+
							" f.triggerid="+sqlParts.marshalParam("triggerid", Nest.value(cTrig,"triggerid").asLong())), "name");

						// if we have at least one template linked to trigger host inside trigger expression,
						// then we don't delete this trigger
						boolean gocontinue = false;
						CArray<String> expressionHosts = expressionData.getHosts();
						for(String templateName : expressionHosts) {
							if (isset(templateNames, templateName)) {
								//continue 2;
								gocontinue = true;
								continue;
							}
						}
						if(gocontinue){
							continue;
						}
						cTrigIds.add(Nest.value(cTrig,"triggerid").asLong());
					}
					deleteByIds(cTrigIds);
				}

				delete("functions", (Map)map("triggerid", Nest.value(trigger,"triggerid").asLong()));

				try {
					Nest.value(trigger,"expression").$(implode_exp(idBean, getSqlExecutor(),expressionFull, Nest.value(trigger,"triggerid").asLong(), hosts));
				} catch (Exception e) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("Cannot implode expression \"%s\".", expressionFull)+" "+e.getMessage());
				}

				// if the expression has changed, we must revalidate the existing dependencies
				if (!isset(trigger,"dependencies")) {
					CArray<Long> dependencies = rda_objectValues(Nest.value(dbTrigger,"dependencies").$(), "triggerid");
					CArray<Map> dependenciesCA = array();
					for(Long dependencyTriggerId : dependencies){
						dependenciesCA.add(map("triggerid",dependencyTriggerId));
					}
					Nest.value(trigger,"dependencies").$(dependenciesCA);
				}
			}

			Map triggerUpdate = Clone.deepcopy(trigger);
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
				"where", map("triggerid", Nest.value(trigger,"triggerid").asLong())
			)));

			// update service status
			if (isset(trigger,"priority") && Nest.value(trigger,"priority").asInteger() != Nest.value(dbTrigger,"priority").asInteger()) {
				changedPriorityTriggerIds.add(Nest.value(trigger,"triggerid").asLong());
			}

			// restore the full expression to properly validate dependencies
			Nest.value(trigger,"expression").$(expressionChanged ? explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString()) : oldExpression);

			infos.add( _s("Updated: Trigger \"%1$s\" on \"%2$s\".", getDescription((String)Nest.value(trigger,"description").$()), implode(", ", hosts)));
			add_audit_ext(this.idBean, getSqlExecutor(), AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_TRIGGER, Nest.value(dbTrigger,"triggerid").asLong(),
					Nest.value(dbTrigger,"description").asString(), null, dbTrigger, triggerUpdate);
		}

		if (!empty(changedPriorityTriggerIds) && usedInItServices(changedPriorityTriggerIds)) {
			updateItServices(this.idBean, getSqlExecutor());
		}

		for(String info : infos) {
			info(info);
		}
	}

	public boolean syncTemplates(CArray<Map> data) {
		Nest.value(data,"templateids").$(rda_toArray(Nest.value(data,"templateids").$()));
		Nest.value(data,"hostids").$(rda_toArray(Nest.value(data,"hostids").$()));

		CTriggerGet options = new CTriggerGet();
		options.setHostIds(Nest.array(data,"templateids").asLong());
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"triggerid", "expression", "description", "url", "status", "priority", "comments", "type"});
		CArray<Map> triggers = get(options);

		for(Map trigger : triggers) {
			Nest.value(trigger,"expression").$(explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString()));
			inherit(trigger, Nest.array(data,"hostids").asLong());
		}

		return true;
	}

	/**
	 * Synchronizes the templated trigger dependencies on the given hosts inherited from the given
	 * templates.
	 * Update dependencies, do it after all triggers that can be dependent were created/updated on
	 * all child hosts/templates. Starting from highest level template triggers select triggers from
	 * one level lower, then for each lower trigger look if it's parent has dependencies, if so
	 * find this dependency trigger child on dependent trigger host and add new dependency.
	 *
	 * @param array _data
	 *
	 * @return void
	 */
	public void syncTemplateDependencies(CArray<Object> _data) {
		CArray<Long> _templateIds = rda_toArray(Nest.value(_data,"templateids").$());
		CArray<Long> _hostIds = rda_toArray(Nest.value(_data,"hostids").$());

		CTriggerGet options = new CTriggerGet();
		options.setHostIds(_templateIds.valuesAsLong());
		options.setPreserveKeys(true);
		options.setOutput(new String[]{"triggerid"});
		options.setSelectDependencies(API_OUTPUT_REFER);
		CArray<Map> _parentTriggers = get(options);

		if (!empty(_parentTriggers)) {
			options = new CTriggerGet();
			if(!empty(_hostIds)){
				options.setHostIds(_hostIds.valuesAsLong());
				options.setFilter("templateid", array_keys(_parentTriggers).valuesAsString());
				options.setNopermissions(true);
				options.setPreserveKeys(true);
				options.setOutput(new String[]{"triggerid", "templateid"});
				options.setSelectDependencies(API_OUTPUT_REFER);
				options.setSelectHosts(new String[]{"hostid"});
			}
			CArray<Map> _childTriggers = get(options);

			if (!empty(_childTriggers)) {
				CArray<Map> _newDependencies = array();
				for(Map _childTrigger : _childTriggers) {
					CArray<Map> _parentDependencies = Nest.value(_parentTriggers,_childTrigger.get("templateid"),"dependencies").asCArray();
					if (!empty(_parentDependencies)) {
						CArray<Long> _dependencies = array();
						for(Map _depTrigger : _parentDependencies) {
							_dependencies.add(Nest.value(_depTrigger,"triggerid").asLong());
						}
						Map _host = reset((CArray<Map>)Nest.value(_childTrigger,"hosts").asCArray());
						_dependencies = replace_template_dependencies(this.idBean, getSqlExecutor(),(CArray)_dependencies, Nest.value(_host,"hostid").asString());
						for (Entry<Object, Long> e : _dependencies.entrySet()) {
			                Long _depTriggerId = e.getValue();
							_newDependencies.add(map(
								"triggerid", Nest.value(_childTrigger,"triggerid").$(),
								"dependsOnTriggerid", _depTriggerId
							));
						}
					}
				}
				deleteDependencies(_childTriggers);

				if (!empty(_newDependencies)) {
					addDependencies(_newDependencies);
				}
			}
		}
	}
	
	/**
	 * Validates the dependencies of the given triggers.
	 *
	 * @param array _triggers list of triggers and corresponding dependencies
	 * @param int _triggers[]["triggerid"] trigger id
	 * @param array _triggers[]["dependencies"] list of trigger ids on which depends given trigger
	 *
	 * @trows APIException if any of the dependencies is invalid
	 */
	protected void checkDependencies(CArray<Map> triggers) {
		for(Map trigger : triggers) {
			if (empty(Nest.value(trigger,"dependencies").$())) {
				continue;
			}

			// trigger templates
			CTemplateGet options = new CTemplateGet();
			options.setOutput(new String[]{"status", "hostid"});
			options.setTriggerIds(Nest.value(trigger,"triggerid").asLong());
			options.setNopermissions(true);
			CArray<Map> triggerTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(options);

			// forbid dependencies from hosts to templates
			if (empty(triggerTemplates)) {
				options = new CTemplateGet();
				options.setTriggerIds(Nest.array(trigger,"dependencies").asLong());
				options.setOutput(new String[]{"templateid"});
				options.setNopermissions(true);
				options.setLimit(1);
				CArray<Map> triggerDependencyTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(options);
				if (!empty(triggerDependencyTemplates)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot add dependency from a host to a template."));
				}
			}

			// the trigger can't depend on itself
			if (in_array(Nest.value(trigger,"triggerid").$(), Nest.value(trigger,"dependencies").asCArray())) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot create dependency on trigger itself."));
			}

			// check circular dependency
			CArray<Long> downTriggerIds = array(Nest.value(trigger,"triggerid").asLong());
			SqlBuilder sqlParts = new SqlBuilder();
			do {
				// triggerid_down depends on triggerid_up
				CArray<Map> res = DBselect(getSqlExecutor(),
					"SELECT td.triggerid_up"+
					" FROM trigger_depends td"+
					" WHERE "+ sqlParts.where.dbConditionInt("td.triggerid_down", downTriggerIds.valuesAsLong()),
					sqlParts.getNamedParams()
				);

				// combine db dependencies with those to be added
				CArray<Long> upTriggersIds = array();
				for(Map row : res) {
					upTriggersIds.add(Nest.value(row,"triggerid_up").asLong());
				}
				for(Long id : downTriggerIds) {
					if (isset(triggers,id) && isset(triggers.get(id),"dependencies")) {
						upTriggersIds = array_merge(upTriggersIds, Nest.value(triggers,id,"dependencies").asCArray());
					}
				}

				// if found trigger id is in dependent triggerids, there is a dependency loop
				downTriggerIds = array();
				for(Object id : upTriggersIds) {
					if (bccomp(id, Nest.value(trigger,"triggerid").$()) == 0) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot create circular dependencies."));
					}
					downTriggerIds.add(Nest.as(id).asLong());
				}
			} while (!empty(downTriggerIds));

			// fetch all templates that are used in dependencies
			options = new CTemplateGet();
			options.setTriggerIds(Nest.array(trigger,"dependencies").asLong());
			options.setOutput(new String[]{"templateid"});
			options.setNopermissions(true);
			CArray<Map> triggerDependencyTemplates = API.Template(this.idBean, this.getSqlExecutor()).get(options);
			CArray<Long> depTemplateIds = rda_toHash(rda_objectValues(triggerDependencyTemplates, "templateid"));

			// run the check only if a templated trigger has dependencies on other templates
			CArray<Long> triggerTemplateIds = rda_toHash(rda_objectValues(triggerTemplates, "hostid"));
			CArray<Long> tdiff = array_diff(depTemplateIds, triggerTemplateIds);
			if (!empty(triggerTemplateIds) && !empty(depTemplateIds) && !empty(tdiff)) {
				CArray<Long> affectedTemplateIds = rda_array_merge(triggerTemplateIds, depTemplateIds);

				// create a list of all hosts, that are children of the affected templates
				sqlParts = new SqlBuilder();
				CArray<Map> dbLowlvltpl = DBselect(getSqlExecutor(),
					"SELECT DISTINCT ht.templateid,ht.hostid,h.host"+
					" FROM hosts_templates ht,hosts h"+
					" WHERE h.tenantid=ht.tenantid"+
						" AND h.hostid=ht.hostid"+
						" AND "+sqlParts.dual.dbConditionInt("ht.templateid", affectedTemplateIds.valuesAsLong()),
					sqlParts.getNamedParams()
				);
				CArray<Map> map = array();
				for(Map lowlvltpl : dbLowlvltpl) {
					if (!isset(map,lowlvltpl.get("hostid"))) {
						Nest.value(map,lowlvltpl.get("hostid")).$(array());
					}
					Nest.value(map,lowlvltpl.get("hostid"),lowlvltpl.get("templateid")).$(Nest.value(lowlvltpl,"host").$());
				}

				// check that if some host is linked to the template, that the trigger belongs to,
				// the host must also be linked to all of the templates, that trigger dependencies point to
				for(Map templates : map) {
					for(Long triggerTemplateId : triggerTemplateIds) {
						// is the host linked to one of the trigger templates?
						if (isset(templates,triggerTemplateId)) {
							// then make sure all of the dependency templates are also linked
							for(Long depTemplateId : depTemplateIds) {
								if (!isset(templates,depTemplateId)) {
									throw CDB.exception(RDA_API_ERROR_PARAMETERS,
										_s("Not all templates are linked to \"%s\".", reset(templates))
									);
								}
							}
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Check that none of the triggers have dependencies on their children. Checks only one level of inheritance, but
	 * since it is called on each inheritance step, also works for multiple inheritance levels.
	 *
	 * @throws APIException     if at least one trigger is dependent on its child
	 *
	 * @param array _triggers
	 */
	protected void checkDependencyParents(CArray<Map> triggers) {
		// fetch all templated dependency trigger parents
		CArray<Long> depTriggerIds = array();
		for(Map trigger : triggers) {
			for(Object depTriggerId:Nest.value(trigger,"dependencies").asCArray()) {
				Nest.value(depTriggerIds,depTriggerId).$(depTriggerId);
			}
		}
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> parentDepTriggers = DBfetchArray(DBselect(getSqlExecutor(),
			"SELECT templateid,triggerid"+
			" FROM triggers"+
			" WHERE templateid>0"+
				" AND "+ sqlParts.where.dbConditionInt("triggerid", depTriggerIds.valuesAsLong()),
			sqlParts.getNamedParams()
		));
		if (!empty(parentDepTriggers)) {
			parentDepTriggers = rda_toHash(parentDepTriggers, "triggerid");
			for(Map trigger : triggers) {
				for(Long depTriggerId : Nest.array(trigger,"dependencies").asLong()) {
					// check if the current trigger is the parent of the dependency trigger
					if (isset(parentDepTriggers,depTriggerId)
							&& Nest.value(parentDepTriggers,depTriggerId,"templateid").asLong() == Nest.value(trigger,"triggerid").asLong()) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("Trigger cannot be dependent on a trigger that is inherited from it.")
						);
					}
				}
			}
		}
	}

	/**
	 * Checks if the given dependencies contain duplicates.
	 *
	 * @throws APIException if the given dependencies contain duplicates
	 *
	 * @param array _triggers
	 */
	protected void checkDependencyDuplicates(CArray<Map> triggers) {
		// check duplicates in array
		CArray uniqueTriggers = array();
		Long duplicateTriggerId = null;
		for(Map trigger : triggers) {
			boolean gobreak =false;
			for(Object dep : Nest.value(trigger,"dependencies").asCArray()) {
				if (isset(Nest.value(uniqueTriggers,trigger.get("triggerid"),dep).$())) {
					duplicateTriggerId = Nest.value(trigger,"triggerid").asLong();
					gobreak = true;
					break;
				} else {
					Nest.value(uniqueTriggers,trigger.get("triggerid"),dep).$(1);
				}
			}
			if(gobreak){
				break;
			}
		}

		if (duplicateTriggerId == null) {
			// check if dependency already exists in DB
			SqlBuilder sqlParts = null;
			for(Map trigger : triggers) {
				sqlParts = new SqlBuilder();
				CArray<Map> dbUpTriggers = DBselect(getSqlExecutor(),
					"SELECT td.triggerid_up"+
					" FROM trigger_depends td"+
					" WHERE "+sqlParts.dual.dbConditionInt("td.triggerid_up", Nest.array(trigger,"dependencies").asLong())+
					" AND td.triggerid_down="+sqlParts.marshalParam("triggerid_down",Nest.value(trigger,"triggerid").asLong()),
					1,
					sqlParts.getNamedParams());
				if (!empty(dbUpTriggers)) {
					duplicateTriggerId = Nest.value(trigger,"triggerid").asLong();
					break;
				}
			}
		}

		if (!empty(duplicateTriggerId)) {
			Map params = new HashMap();
			params.put("triggerid", duplicateTriggerId);
			Map dplTrigger = DBfetch(DBselect(getSqlExecutor(), 
				"SELECT t.description"+
				" FROM triggers t"+
				" WHERE t.triggerid=#{triggerid}",
				params
			));
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Duplicate dependencies in trigger \"%1$s\".", Nest.value(dplTrigger,"description").$())
			);
		}
	}
	
	/**
	 * Check if all templates trigger belongs to are linked to same hosts.
	 *
	 * @throws APIException
	 *
	 * @param trigger
	 *
	 * @return bool
	 */
	protected boolean validateItems(Map trigger) {
		CTriggerExpression expressionData = new CTriggerExpression();
		expressionData.parse(Nest.value(trigger,"expression").asString());

		CTemplateGet options = new CTemplateGet();
		options.setOutput(API_OUTPUT_REFER);
		options.setSelectHosts(API_OUTPUT_REFER);
		options.setSelectTemplates(API_OUTPUT_REFER);
		options.setFilter("host", expressionData.getHosts().valuesAsString());
		options.setPreserveKeys(true);
		CArray<Map> templatesData = API.Template(this.idBean, this.getSqlExecutor()).get(options);
		Map firstTemplate = array_pop(templatesData);
		if (!empty(firstTemplate)) {
			CArray compareLinks = array_merge(
				rda_objectValues(Nest.value(firstTemplate,"hosts").$(), "hostid"),
				rda_objectValues(Nest.value(firstTemplate,"templates").$(), "templateid")
			);

			for(Map data : templatesData) {
				CArray linkedTo = array_merge(
					rda_objectValues(Nest.value(data,"hosts").$(), "hostid"),
					rda_objectValues(Nest.value(data,"templates").$(), "templateid")
				);

				if (!empty(array_diff(compareLinks, linkedTo)) || !empty(array_diff(linkedTo, compareLinks))) {
					throw CDB.exception(
						RDA_API_ERROR_PARAMETERS,
						_s("Trigger \"%s\" belongs to templates with different linkages.", Nest.value(trigger,"description").$())
					);
				}
			}
		}

		return true;
	}
	
	@Override
	public boolean isReadable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);

		CTriggerGet options = new CTriggerGet();
		options.setTriggerIds(TArray.as(ids).asLong());
		options.setCountOutput(true);
		long count = get(options);

		return count(ids) == count;
	}

	@Override
	public boolean isWritable(Long... ids) {
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);

		CTriggerGet options = new CTriggerGet();
		options.setTriggerIds(TArray.as(ids).asLong());
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);

		return count(ids) == count;
	}
	
	/**
	 * Returns true if the given expression contains templates.
	 *
	 * @param CTriggerExpression _exp
	 *
	 * @return bool
	 */
	protected boolean expressionHasTemplates(CTriggerExpression expressionData) {
		CHostGet options = new CHostGet();
		options.setOutput(new String[]{"status"});
		options.setFilter("name", expressionData.getHosts().valuesAsString());
		options.setTemplatedHosts(true);
		CArray<Map> hosts = API.Host(this.idBean, this.getSqlExecutor()).get(options);

		for(Map host: hosts) {
			if (Nest.value(host,"status").asInteger() == HOST_STATUS_TEMPLATE) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		
		if (params.getCountOutput() == null) {
			// expandData
			if (!is_null(Nest.value(params,"expandData").$())) {
				sqlParts.select.put("hostname", "h.name AS hostname");
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

			if (Nest.value(params,"expandDescription").$() != null) {
				addQuerySelect(fieldId("expression"), sqlParts);
			}

			// select the state field to be able to return the deprecated value_flags property
			if (outputIsRequested("value_flags", params.getOutput())) {
				addQuerySelect(fieldId("state"), sqlParts);
			}
		}
	}
	
	protected void addRelatedObjects(CTriggerGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] triggerids = array_keys(result).valuesAsLong();

		// adding trigger dependencies
		if (params.getSelectDependencies() != null && !API_OUTPUT_COUNT.equals(params.getSelectDependencies())) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> res = DBselect(getSqlExecutor(),
				"SELECT td.triggerid_up,td.triggerid_down"+
				" FROM trigger_depends td"+
				" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "trigger_depends", "td", params)+
				" AND "+sqlParts.dual.dbConditionInt("td.triggerid_down", triggerids),
				sqlParts.getNamedParams()
			);
			CRelationMap relationMap = new CRelationMap();
			for (Map relation : res) {
				relationMap.addRelation(Nest.value(relation,"triggerid_down").$(), Nest.value(relation,"triggerid_up").$());
			}

			CTriggerGet options = new CTriggerGet();
			options.setTriggerIds(relationMap.getRelatedLongIds());
			options.setOutput(params.getSelectDependencies());
			options.setExpandData(true);
			options.setPreserveKeys(true);
			CArray<Map> dependencies = get(options);
			relationMap.mapMany(result, dependencies, "dependencies");
		}

		// adding items
		if (params.getSelectItems() != null && !API_OUTPUT_COUNT.equals(params.getSelectItems())) {
			CRelationMap relationMap = createRelationMap(result, "triggerid", "itemid", "functions");
			CItemGet tparams = new CItemGet();
			tparams.setOutput(params.getSelectItems());
			tparams.setItemIds(relationMap.getRelatedLongIds());
			tparams.setWebItems(true);
			tparams.setNopermissions(true);
			tparams.setPreserveKeys(true);
			CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(tparams);
			relationMap.mapMany(result, items, "items");
		}

		// adding discoveryrule
		if (params.getSelectDiscoveryRule() != null && !API_OUTPUT_COUNT.equals(params.getSelectDiscoveryRule())) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbRules = DBselect(getSqlExecutor(),
				"SELECT id.parent_itemid,td.triggerid"+
				" FROM trigger_discovery td,item_discovery id,functions f"+
				" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "trigger_discovery", "td", params)+
					" AND "+sqlParts.dual.dbConditionInt("td.triggerid", triggerids)+
					" AND td.tenantid=f.tenantid"+
					" AND f.tenantid=id.tenantid"+
					" AND td.parent_triggerid=f.triggerid"+
					" AND f.itemid=id.itemid",
				sqlParts.getNamedParams()
			);
			CRelationMap relationMap = new CRelationMap();
			for (Map rule : dbRules) {
				relationMap.addRelation(Nest.value(rule,"triggerid").$(), Nest.value(rule,"parent_itemid").$());
			}

			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setOutput(params.getSelectDiscoveryRule());
			droptions.setItemIds(relationMap.getRelatedLongIds());
			droptions.setNopermissions(true);
			droptions.setPreserveKeys(true);
			CArray<Map> discoveryRules = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
			relationMap.mapOne(result, discoveryRules, "discoveryRule");
		}

		// adding last event
		if (params.getSelectLastEvent() != null) {
			 for (Entry<Object, Map> e : result.entrySet()) {
				Object triggerId = e.getKey();
				Nest.value(result,triggerId,"lastEvent").$(array());
			}

			String outputFields = null;
			if (isArray(params.getSelectLastEvent())) {
				String pkFieldId = pk("events");
				CArray coutputFields = map(
					"objectid", fieldId("objectid", "e"),
					"ns", fieldId("ns", "e"),
					pkFieldId, fieldId(pkFieldId, "e")
				);

				for(String field: (String[])params.getSelectLastEvent()) {
					if (hasField("events",field)) {
						Nest.value(coutputFields,field).$(fieldId(field, "e"));
					}
				}

				outputFields = implode(",", coutputFields);
			} else {
				outputFields = "e.*";
			}

			// due to performance issues, avoid using "ORDER BY" for outter SELECT
			SqlBuilder sqlParts = new SqlBuilder();
			/* To be confirmed */
			CArray<Map> dbEvents = DBselect(getSqlExecutor(),
				"SELECT "+outputFields+
				" FROM events e"+
					" JOIN ("+
						"SELECT e2.tenantid,e2.source,e2.object,e2.objectid,MAX(clock) AS clock"+
						" FROM events e2"+
						" WHERE e2.source="+EVENT_SOURCE_TRIGGERS+
							" AND e2.object="+EVENT_OBJECT_TRIGGER+
							" AND "+sqlParts.dual.dbConditionTenants(this.idBean, "events", "e2", params)+
							" AND "+sqlParts.dual.dbConditionInt("e2.objectid", triggerids)+
						" GROUP BY e2.source,e2.object,e2.objectid"+
					") e3 ON e3.tenantid=e.tenantid"+
						" AND e3.source=e.source"+
						" AND e3.object=e.object"+
						" AND e3.objectid=e.objectid"+
						" AND e3.clock=e.clock",
				sqlParts.getNamedParams()
			);

			// in case there are multiple records with same "clock" for one trigger, we"ll get different "ns'
			CArray<CArray<Map>> lastEvents = array();

			for (Map dbEvent : dbEvents) {
				Object triggerId = Nest.value(dbEvent,"objectid").$();
				Object ns = Nest.value(dbEvent,"ns").$();

				// unset fields, that were not requested
				if (isArray(params.getSelectLastEvent())) {
					if (!in_array("objectid", (String[])params.getSelectLastEvent())) {
						unset(dbEvent,"objectid");
					}
					if (!in_array("ns", (String[])params.getSelectLastEvent())) {
						unset(dbEvent,"ns");
					}
				}

				Nest.value(lastEvents,triggerId,ns).$(dbEvent);
			}

			for (Entry<Object, CArray<Map>> e : lastEvents.entrySet()) {
                Object triggerId = e.getKey();
                CArray<Map> events = e.getValue();
				// find max "ns" for each trigger and that will be the "lastEvent"
				Long maxNs = max(array_keys(events).valuesAsLong());
				Nest.value(result,triggerId,"lastEvent").$(Nest.value(events,maxNs).$());
			}
		}
	}

	@Override
	protected void applyQuerySortOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQuerySortOptions(tableName, tableAlias, params, sqlParts);
		
		if (!rda_empty(params.getSortfield())) {
			// if the parent method call adds a hostname column to the select clause, replace it with \"h.name\"
			// since column \"t.hostname\" doesn't exist
			if (isset(sqlParts.select.namedMap, "hostname")) {
				sqlParts.select.put("hostname", "h.name as hostname");
			}
		}
	}
	
	@Override
	protected void applyQuerySortField(String sortfield, String sortorder, String alias, SqlBuilder sqlParts) {
		if ("hostname".equals(sortfield)) {
			sqlParts.from.put("functions", "functions f");
			sqlParts.from.put("items", "items i");
			sqlParts.from.put("hosts", "hosts h");
			sqlParts.where.put("t.tenantid = f.tenantid");
			sqlParts.where.put("t.triggerid = f.triggerid");
			sqlParts.where.put("f.tenantid = i.tenantid");
			sqlParts.where.put("f.itemid = i.itemid");
			sqlParts.where.put("i.tenantid = h.tenantid");
			sqlParts.where.put("i.hostid = h.hostid");
			sqlParts.order.put("h.name " + sortorder);
		} else {
			super.applyQuerySortField(sortfield, sortorder, alias, sqlParts);
		}
	}
	
	/**
	 * Checks if all of the given triggers are available for writing.
	 *
	 * @throws APIException     if a trigger is not writable or does not exist
	 *
	 * @param array _triggerIds
	 */
	protected void checkPermissions(Long... triggerIds) {
		if (!isWritable(triggerIds)) {
			throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
		}
	}
	
	/**
	 * Checks that none of the given triggers is inherited from a template.
	 *
	 * @throws APIException     if one of the triggers is inherited
	 *
	 * @param array _triggerIds
	 */
	protected void checkNotInherited(Long... triggerIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map trigger = DBfetch(DBselect(getSqlExecutor(),
			"SELECT t.triggerid,t.description,t.expression"+
				" FROM triggers t"+
				" WHERE "+sqlParts.dual.dbConditionInt("t.triggerid", triggerIds)+
				"AND t.templateid IS NOT NULL",
				1,
				sqlParts.getNamedParams()
		));
		if (!empty(trigger)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS,
				_s("Cannot delete templated trigger \"%1$s:%2$s\".", Nest.value(trigger,"description").$(),	explode_exp(idBean, getSqlExecutor(),Nest.value(trigger,"expression").asString()))
			);
		}
	}
	
	@Override
	protected boolean requiresPostSqlFiltering(CTriggerGet params) {
		return params.getSkipDependent() != null || params.getWithLastEventUnacknowledged() != null;
	}

	@Override
	protected CArray applyPostSqlFiltering(CArray elements, CTriggerGet options){
		CArray<Object> triggers = rda_toHash(elements, "triggerid");

		// unset triggers which are dependant on at least one problem trigger upstream into dependency tree
		if (!is_null(options.getSkipDependent())) {
			// Result trigger IDs of all triggers in results.
			CArray resultTriggerIds = rda_objectValues(triggers, "triggerid");

			// Will contain IDs of all triggers on which some other trigger depends.
			CArray allUpTriggerIds = array();

			// Trigger dependency map.
			CArray<CArray> downToUpTriggerIds = array();

			// Values (state) of each \"up\" trigger ID is stored in here.
			CArray upTriggerValues = array();

			// Will contain IDs of all triggers either disabled directly, or by having disabled item or disabled host.
			CArray disabledTriggerIds = array();

			// First loop uses result trigger IDs.
			CArray triggerIds = Clone.deepcopy(resultTriggerIds);
			
			SqlBuilder sqlParts = null;
			CArray<Map> dbResult = null;
			Object downTriggerId = null;
			CArray upTriggerIds = null;
			do {
				// Fetch all dependency records where \"down\" trigger IDs are in current iteration trigger IDs.
				sqlParts = new SqlBuilder();
				dbResult = DBselect(getSqlExecutor(),
					"SELECT d.triggerid_down,d.triggerid_up,t.value"+
					" FROM trigger_depends d,triggers t"+
					" WHERE d.tenantid=t.tenantid"+
						" AND d.triggerid_up=t.triggerid"+
						" AND "+sqlParts.dual.dbConditionInt("d.triggerid_down", triggerIds.valuesAsLong()),
					sqlParts.getNamedParams()
				);

				// Add trigger IDs as keys and empty arrays as values.
				downToUpTriggerIds = CMapOperator.add(downToUpTriggerIds, array_fill_keys(triggerIds, array()));

				triggerIds = array();
				for(Map dependency : dbResult) {
					// Trigger ID for \"down\" trigger, which has dependencies.
					downTriggerId = Nest.value(dependency,"triggerid_down").$();

					// Trigger ID for \"up\" trigger, on which the other (\"up\") trigger depends.
					Object upTriggerId = Nest.value(dependency,"triggerid_up").$();

					// Add \"up\" trigger ID to mapping. We also index by _upTrigger because later these arrays
					// are combined with + and this way indexes and values do not break.
					Nest.value(downToUpTriggerIds,downTriggerId,upTriggerId).$(upTriggerId);

					// Add ID of this \"up\" trigger to all known \"up\" triggers.
					allUpTriggerIds.add(upTriggerId);

					// Remember value of this \"up\" trigger.
					Nest.value(upTriggerValues,upTriggerId).$(Nest.value(dependency,"value").$());

					// Add ID of this \"up\" trigger to the list of trigger IDs which should be mapped.
					triggerIds.add(upTriggerId);
				}
			} while (!empty(triggerIds));

			// Fetch trigger IDs for triggers that are disabled, have disabled items or disabled item hosts.
			sqlParts = new SqlBuilder();
			dbResult = DBselect(getSqlExecutor(),
				"SELECT t.triggerid"+
				" FROM triggers t,functions f,items i,hosts h"+
				" WHERE t.tenantid=f.tenantid"+
					" AND f.tenantid=i.tenantid"+
					" AND i.tenantid=h.tenantid"+
					" AND t.triggerid=f.triggerid"+
					" AND f.itemid=i.itemid"+
					" AND i.hostid=h.hostid"+
					" AND ("+
						"i.status="+ITEM_STATUS_DISABLED+
						" OR h.status="+HOST_STATUS_NOT_MONITORED+
						" OR t.status="+TRIGGER_STATUS_DISABLED+
					")"+
					" AND "+sqlParts.dual.dbConditionInt("t.triggerid", allUpTriggerIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			
			for(Map row : dbResult) {
				Object resultTriggerId = Nest.value(row,"triggerid").$();
				Nest.value(disabledTriggerIds,resultTriggerId).$(resultTriggerId);
			}

			// Now process all mapped dependencies and unset any disabled \"up\" triggers so they do not participate in
			// decisions regarding nesting resolution in next step.
			for (Entry<Object, CArray> e : downToUpTriggerIds.entrySet()) {
			    downTriggerId = e.getKey();
			    upTriggerIds = e.getValue();
			    
			    Iterator<Entry<Object, Object>> iterator = upTriggerIds.entrySet().iterator();
			    while(iterator.hasNext()) {
			    	Object upTriggerId = iterator.next().getValue();
			    	if (isset(disabledTriggerIds,upTriggerId)) {
			    		iterator.remove();
			    	}
			    }
			}

			// Resolve dependencies for all result set triggers.
			for(Object resultTriggerId : resultTriggerIds) {
				// We start with result trigger.
				triggerIds = array(resultTriggerId);

				// This also is unrolled recursive function and is repeated until there are no more trigger IDs to
				// check, add and resolve.
				CArray nextTriggerIds = null;
				do {
					nextTriggerIds = array();
					for(Object triggerId : triggerIds) {
						// Loop through all \"up\" triggers.
						for(Object upTriggerId : downToUpTriggerIds.get(triggerId)) {
							if (!empty(downToUpTriggerIds.get(upTriggerId))) {
								// If there this \"up\" trigger has \"up\" triggers of it's own, merge them and proceed with recursion.
								Nest.value(downToUpTriggerIds,resultTriggerId).$(CMapOperator.add(downToUpTriggerIds.get(resultTriggerId), downToUpTriggerIds.get(upTriggerId)));
								// Add trigger ID to be processed in next loop iteration.
								nextTriggerIds.add(upTriggerId);
							}
						}
					}
					triggerIds = Clone.deepcopy(nextTriggerIds);
				} while (!empty(triggerIds));
			}

			// Clean result set.
			for(Object resultTriggerId : resultTriggerIds) {
				for(Object upTriggerId : downToUpTriggerIds.get(resultTriggerId)) {
					// If \"up\" trigger is in problem state, dependent trigger should not be returned and is removed
					// from results.
					if (Nest.value(upTriggerValues,upTriggerId).asInteger() == TRIGGER_VALUE_TRUE) {
						unset(triggers,resultTriggerId);
					}
				}

				// Check if result trigger is disabled and if so, remove from results.
				if (isset(disabledTriggerIds,resultTriggerId)) {
					unset(triggers,resultTriggerId);
				}
			}
		}

		// withLastEventUnacknowledged
		if (!is_null(Nest.value(options,"withLastEventUnacknowledged").$())) {
			CArray triggerIds = rda_objectValues(triggers, "triggerid");
			CArray eventIds = array();
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("MAX(e.eventid) AS eventid,e.objectid");
			sqlParts.from.put("events e");
			sqlParts.where.put("e.object="+EVENT_OBJECT_TRIGGER);
			sqlParts.where.put("e.source="+EVENT_SOURCE_TRIGGERS);
			sqlParts.where.dbConditionInt("e.objectid", triggerIds.valuesAsLong());
			sqlParts.where.dbConditionInt("e.value", array(TRIGGER_VALUE_TRUE).valuesAsLong());
			sqlParts.group.put("e.objectid");
			
			CArray<Map> eventsDb = DBselect(getSqlExecutor(), sqlParts);
			for (Map event: eventsDb) {
				eventIds.add( Nest.value(event,"eventid").$() );
			}

			sqlParts = new SqlBuilder();
			sqlParts.select.put("e.objectid");
			sqlParts.from.put("events e");
			sqlParts.where.dbConditionInt("e.eventid", eventIds.valuesAsLong());
			sqlParts.where.put("e.acknowledged=0");
			
			CArray<Map> correctTriggerIds = DBfetchArrayAssoc(DBselect(getSqlExecutor(), sqlParts), "objectid");

			Iterator<Entry<Object, Object>> triggerIterator = triggers.entrySet().iterator();
			while(triggerIterator.hasNext()) {
				Entry<Object, Object> entry = triggerIterator.next();
				Object triggerId = entry.getKey();
				
				if (!isset(correctTriggerIds.get(triggerId))) {
//					unset(triggers, triggerId);
					triggerIterator.remove();
				}
			}
		}

		return triggers;
	}

	/**
	 * Returns true if at least one of the given triggers is used in IT services.
	 *
	 * @param array _triggerIds
	 *
	 * @return bool
	 */
	protected boolean usedInItServices(CArray<Long> triggerIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> query = DBselect( getSqlExecutor(),
				"SELECT serviceid FROM services WHERE "+sqlParts.dual.dbConditionInt("triggerid", triggerIds.valuesAsLong()), 
				1, 
				sqlParts.getNamedParams());
		return !empty(query);
	}
}
