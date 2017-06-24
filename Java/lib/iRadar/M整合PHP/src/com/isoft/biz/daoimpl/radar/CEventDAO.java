package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.inArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_AUTOREGHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_ITEM;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_LLDRULE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.EventsUtil.eventObject;
import static com.isoft.iradar.inc.EventsUtil.eventSource;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CAlertGet;
import com.isoft.iradar.model.params.CDHostGet;
import com.isoft.iradar.model.params.CDServiceGet;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CSetValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.event.CEventSourceObjectValidator;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with events.
 * @author benne
 */
@CodeConfirmed("benne.2.2.6")
public class CEventDAO extends CCoreLongKeyDAO<CEventGet> {

	/**
	 * Array of supported objects where keys are object IDs and values are translated object names.
	 *
	 * @var array
	 */
	protected CArray<String> objects;
	
	/**
	 * Array of supported sources where keys are source IDs and values are translated source names.
	 *
	 * @var array
	 */
	protected CArray<String> sources;
	
	public CEventDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "events", "e", new String[]{"eventid", "object", "objectid", "clock"});
		this.sources = eventSource();
		this.objects = eventObject();
	}
	
	@Override
	public <T> T get(CEventGet params) {
		int userType = CWebUser.getType();	
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("dchecks", this.fieldId("eventid"));
		sqlParts.from.put("events", "events e");
		
		checkDeprecatedParam(params, "selectTriggers");
		checkDeprecatedParam(params, "selectItems");
		checkDeprecatedParam(params, "sortfield", "object");
		params = convertDeprecatedParam(params, "triggerids", "objectids");
		validateGet(params);
		
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			// triggers
			if (params.getObject() == EVENT_OBJECT_TRIGGER) {
				// specific triggers
				if (params.getObjectIds() != null) {
					CTriggerGet toptions = new CTriggerGet();
					toptions.setTriggerIds(params.getObjectIds());
					toptions.setEditable(params.getEditable());
					CArray<Map> triggers = API.Trigger(this.idBean,getSqlExecutor()).get(toptions);
					params.setObjectIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
				}
				// all triggers
				else {
					int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;
					Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);
					sqlParts.where.put("EXISTS ("+
							"SELECT NULL"+
							" FROM functions f,items i,hosts_groups hgg"+
								" JOIN rights r"+
									" ON r.tenantid=hgg.tenantid"+ 
										" AND r.id=hgg.groupid"+
										" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
							" WHERE e.tenantid=f.tenantid"+
								" AND f.tenantid=i.tenantid"+
								" AND i.tenantid=hgg.tenantid"+
								" AND e.objectid=f.triggerid"+
								" AND f.itemid=i.itemid"+
								" AND i.hostid=hgg.hostid"+
							" GROUP BY f.triggerid"+
							" HAVING MIN(r.permission)>"+PERM_DENY+
								" AND MAX(r.permission)>="+permission+
							")");
				}
			}
			// items and LLD rules
			else if (params.getObject() == EVENT_OBJECT_ITEM || params.getObject() == EVENT_OBJECT_LLDRULE) {
				// specific items or LLD rules
				if (params.getObjectIds() != null) {
					if (params.getObject() == EVENT_OBJECT_ITEM) {
						CItemGet ioptions = new CItemGet();
						ioptions.setOutput(new String[]{"itemid"});
						ioptions.setItemIds(params.getObjectIds());
						ioptions.setEditable(params.getEditable());
						CArray<Map> items = API.Item(this.idBean, this.getSqlExecutor()).get(ioptions);
						params.setObjectIds(rda_objectValues(items, "itemid").valuesAsLong());
					}
					else if (params.getObject() == EVENT_OBJECT_LLDRULE) {
						CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
						droptions.setItemIds(params.getObjectIds());
						droptions.setEditable(params.getEditable());
						CArray<Map> items = API.DiscoveryRule(this.idBean, this.getSqlExecutor()).get(droptions);
						params.setObjectIds(rda_objectValues(items, "itemid").valuesAsLong());
					}
				}
				// all items and LLD rules
				else {
					int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;
					Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);
					sqlParts.where.put("EXISTS ("+
							"SELECT NULL"+
							" FROM items i,hosts_groups hgg"+
								" JOIN rights r"+
									" ON r.tenantid=hgg.tenantid"+ 
										" AND r.id=hgg.groupid"+
										" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
							" WHERE e.tenantid=i.tenantid"+
								" AND i.tenantid=hgg.tenantid"+
								" AND e.objectid=i.itemid"+
								" AND i.hostid=hgg.hostid"+
							" GROUP BY hgg.hostid"+
							" HAVING MIN(r.permission)>"+PERM_DENY+
								" AND MAX(r.permission)>="+permission+
							")");
				}
			}
		}
		
		// eventids
		if (!is_null(params.getEventIds())) {
			sqlParts.where.dbConditionInt("e.eventid", params.getEventIds());
		}

		// objectids
		if (!is_null(params.getObjectIds())
				&& inArray(params.getObject(), new Integer[] {EVENT_OBJECT_TRIGGER, EVENT_OBJECT_ITEM, EVENT_OBJECT_LLDRULE})) {
			sqlParts.where.dbConditionInt("e.objectid", params.getObjectIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("objectid","e.objectid");
			}
		}

		// groupids
		if (!is_null(params.getGroupIds())) {
			this.addQuerySelect("hg.groupid", sqlParts);

			// triggers
			if (params.getObject() == EVENT_OBJECT_TRIGGER) {
				sqlParts.from.put("functions","functions f");
				sqlParts.from.put("items","items i");
				sqlParts.from.put("hosts_groups","hosts_groups hg");
				sqlParts.where.dbConditionInt("hg","hg.groupid", params.getGroupIds());
				sqlParts.where.put("hgi.tenantid","hg.tenantid=i.tenantid");
				sqlParts.where.put("hgi","hg.hostid=i.hostid");
				sqlParts.where.put("fe.tenantid","f.tenantid=e.tenantid");
				sqlParts.where.put("fe","f.triggerid=e.objectid");
				sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
				sqlParts.where.put("fi","f.itemid=i.itemid");
			}
			// lld rules and items
			else if (params.getObject() == EVENT_OBJECT_LLDRULE || params.getObject() == EVENT_OBJECT_ITEM) {
				sqlParts.from.put("items","items i");
				sqlParts.from.put("hosts_groups","hosts_groups hg");
				sqlParts.where.dbConditionInt("hg","hg.groupid", params.getGroupIds());
				sqlParts.where.put("hgi.tenantid","hg.tenantid=i.tenantid");
				sqlParts.where.put("hgi","hg.hostid=i.hostid");
				sqlParts.where.put("fi.tenantid","e.tenantid=i.tenantid");
				sqlParts.where.put("fi","e.objectid=i.itemid");
			}
		}

		// hostids
		if (!is_null(params.getHostIds())) {
			this.addQuerySelect("i.hostid", sqlParts);

			// triggers
			if (params.getObject() == EVENT_OBJECT_TRIGGER) {
				sqlParts.from.put("functions","functions f");
				sqlParts.from.put("items","items i");
				sqlParts.where.dbConditionInt("i","i.hostid", params.getHostIds());
				sqlParts.where.put("ft.tenantid","f.tenantid=e.tenantid");
				sqlParts.where.put("ft","f.triggerid=e.objectid");
				sqlParts.where.put("fi.tenantid","f.tenantid=i.tenantid");
				sqlParts.where.put("fi","f.itemid=i.itemid");
			}
			// lld rules and items
			else if (params.getObject() == EVENT_OBJECT_LLDRULE || params.getObject() == EVENT_OBJECT_ITEM) {
				sqlParts.from.put("items","items i");
				sqlParts.where.dbConditionInt("i","i.hostid", params.getHostIds());
				sqlParts.where.put("fi.tenantid","e.tenantid=i.tenantid");
				sqlParts.where.put("fi","e.objectid=i.itemid");
			}
		}

		// object
		if (!is_null(params.getObject())) {
			sqlParts.where.put("o", "e.object="+sqlParts.marshalParam(params.getObject()));
		}

		// source
		if (!is_null(params.getSource())) {
			sqlParts.where.put("e.source="+sqlParts.marshalParam(params.getSource()));
		}

		// acknowledged
		if (!is_null(params.getAcknowledged())) {
			sqlParts.where.put("e.acknowledged="+(params.getAcknowledged() ? 1 : 0));
		}

		// time_from
		if (!is_null(params.getTimeFrom())) {
			sqlParts.where.put("e.clock>="+sqlParts.marshalParam(params.getTimeFrom()));
		}

		// time_till
		if (!is_null(params.getTimeTill())) {
			sqlParts.where.put("e.clock<="+sqlParts.marshalParam(params.getTimeTill()));
		}

		// eventid_from
		if (!is_null(params.getEventIdFrom())) {
			sqlParts.where.put("e.eventid>="+sqlParts.marshalParam(params.getEventIdFrom()));
		}

		// eventid_till
		if (!is_null(params.getEventIdTill())) {
			sqlParts.where.put("e.eventid<="+sqlParts.marshalParam(params.getEventIdTill()));
		}

		// value
		if (!is_null(params.getValue())) {
			sqlParts.where.dbConditionInt("e.value", TArray.as(params.getValue()).asLong());
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("events e", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("events e", params, sqlParts);
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
				Long id = (Long)row.get("eventid");
				if (!isset(result.get(id))) {
					result.put(id, new HashMap());
				}

				// hostids
				if (isset(row.get("hostid")) && is_null(params.getSelectHosts())) {
					if (!isset(result.get(id).get("hosts"))) {
						result.get(id).put("hosts", new CArray());
					}
					((CArray)result.get(id).get("hosts")).add(map("hostid", row.remove("hostid")));
				}

				result.get(id).putAll(row);
			}
		}
		
		if (!is_null(params.getCountOutput())) {
			return (T)ret;
		}
		
		if (!empty(result)) {
			addRelatedObjects(params, result);
			unsetExtraFields(result, new String[]{"object", "objectid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = FuncsUtil.rda_cleanHashes(result);
		}
		return (T)result;
	}
	
	/**
	 * Validates the input parameters for the get() method.
	 *
	 * @throws APIException     if the input is invalid
	 *
	 * @param array     options
	 *
	 * @return void
	 */
	protected void validateGet(CEventGet options) {
		CSetValidator sourceValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(eventSource())
		));
		if (!sourceValidator.validate(this.idBean, String.valueOf(options.getSource()))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect source value."));
		}

		CSetValidator objectValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(eventObject())
		));
		if (!objectValidator.validate(this.idBean, String.valueOf(options.getObject()))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect object value."));
		}

		CEventSourceObjectValidator sourceObjectValidator = CValidator.init(new CEventSourceObjectValidator(),map());
		if (!sourceObjectValidator.validate(this.idBean, map("source", options.getSource(), "object", options.getObject()))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, sourceObjectValidator.getError());
		}
	}
	
	/**
	 * Acknowledges the given events.
	 *
	 * Supported parameters:
	 * - eventids   - an event ID or an array of event IDs to acknowledge
	 * - message    - acknowledgment message
	 *
	 * @param array data
	 * @return 
	 *
	 * @return array
	 */
	public CArray<Long[]> acknowledge(Long[] eventIds, String message) {

		validateAcknowledge(eventIds);

		SqlBuilder sqlParts = new SqlBuilder();
		if (!DBexecute(getSqlExecutor(), "UPDATE events SET acknowledged=1 WHERE " + sqlParts.where.dbConditionLong("eventid", eventIds), sqlParts.getNamedParams())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, "DBerror");
		}

		long time = time();
		CArray<Map> dataInsert = array();

		for(long eventId:eventIds) {
			dataInsert.add(map(
				"userid", Nest.value(this.userData(),"userid").$(),
				"eventid", eventId,
				"clock", time,
				"message", message
			));
		}

		insert("acknowledges", dataInsert);

		return map("eventids", eventIds);
	}
	
	/**
	 * Validates the input parameters for the acknowledge() method.
	 *
	 * @throws APIException     if the input is invalid
	 *
	 * @param array     data
	 *
	 * @return void
	 */
	protected void validateAcknowledge(Long[] eventids) {
		checkCanBeAcknowledged(TArray.as(eventids).asLong());
	}
	
	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		if (is_null(params.getCountOutput())) {
			if (!is_null(Nest.value(params,"selectTriggers").$())
					|| !is_null(Nest.value(params,"selectRelatedObject").$())
					|| !is_null(Nest.value(params,"selectItems").$())
					|| !is_null(Nest.value(params,"selectHosts").$())) {
				this.addQuerySelect("e.object", sqlParts);
				this.addQuerySelect("e.objectid", sqlParts);
			}
		}
	}
	
	@Override
	protected void addRelatedObjects(CEventGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		
		Long[] eventIds = result.keysAsLong();
		
		// adding hosts
		if (!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())) {
			SqlBuilder sqlParts = new SqlBuilder();
			// trigger events
			if(params.getObject() == EVENT_OBJECT_TRIGGER) {
				sqlParts.select.put("e.eventid");
				sqlParts.select.put("i.hostid");
				sqlParts.from.put("events e");
				sqlParts.from.put("functions f");
				sqlParts.from.put("items i");
				applyQueryTenantOptions("events", "e", params, sqlParts);
				sqlParts.where.dbConditionInt("e.eventid", eventIds);
				sqlParts.where.put("e.tenantid=f.tenantid");
				sqlParts.where.put("e.objectid=f.triggerid");
				sqlParts.where.put("f.tenantid=i.tenantid");
				sqlParts.where.put("f.itemid=i.itemid");
				sqlParts.where.put("e.object="+sqlParts.marshalParam(params.getObject()));
				sqlParts.where.put("e.source="+sqlParts.marshalParam(params.getSource()));
			}// item and LLD rule events
			else if (params.getObject()==EVENT_OBJECT_ITEM || params.getObject()==EVENT_OBJECT_LLDRULE) {
				sqlParts.select.put("e.eventid");
				sqlParts.select.put("i.hostid");
				sqlParts.from.put("events e");
				sqlParts.from.put("items i");
				applyQueryTenantOptions("events", "e", params, sqlParts);
				sqlParts.where.dbConditionInt("e.eventid", eventIds);
				sqlParts.where.put("e.tenantid=i.tenantid");
				sqlParts.where.put("e.objectid=i.itemid");
				sqlParts.where.put("e.object="+sqlParts.marshalParam(params.getObject()));
				sqlParts.where.put("e.source="+sqlParts.marshalParam(params.getSource()));
			}
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "eventid", "hostid");
			
			CHostGet hparams = new CHostGet();
			hparams.setOutput(params.getSelectHosts());
			hparams.setHostIds(relationMap.getRelatedLongIds());
			hparams.setPreserveKeys(true);
			
			datas = API.Host(this.idBean, getSqlExecutor()).get(hparams);
			relationMap.mapMany(result, datas, "hosts", null);
		}

		// adding triggers
		if (!is_null(params.getSelectTriggers()) && !API_OUTPUT_COUNT.equals(params.getSelectTriggers())) {
			CRelationMap relationMap = new CRelationMap();
			for(Map event: result) {
				if(Integer.valueOf(EVENT_OBJECT_TRIGGER).equals(event.get("object"))) {
					relationMap.addRelation(event.get("eventid"), event.get("objectid"));
				}
			}
			
			CTriggerGet tparams = new CTriggerGet();
			tparams.setOutput(params.getSelectTriggers());
			tparams.setTriggerIds(relationMap.getRelatedLongIds());
			tparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.Trigger(this.idBean, getSqlExecutor()).get(tparams);
			relationMap.mapMany(result, datas, "triggers", null);
		}
		
		// adding items
		if (!is_null(params.getSelectItems()) && !API_OUTPUT_COUNT.equals(params.getSelectItems())) {
			// discovered items
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("e.eventid");
			sqlParts.select.put("f.itemid");
			sqlParts.from.put("events e");
			sqlParts.from.put("functions f");
			applyQueryTenantOptions("events", "e", params, sqlParts);
			sqlParts.where.dbConditionInt("e.eventid", eventIds);
			sqlParts.where.put("e.tenantid=f.tenantid");
			sqlParts.where.put("e.objectid=f.triggerid");
			sqlParts.where.put("e.object="+EVENT_OBJECT_TRIGGER);
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			CRelationMap relationMap = this.createRelationMap(datas, "eventid", "itemid");
			
			CItemGet iparams = new CItemGet();
			iparams.setOutput(params.getSelectItems());
			iparams.setItemIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			datas = API.Item(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapMany(result, datas, "items", null);
		}
		
		// adding the related object
		if (!is_null(params.getSelectRelatedObject()) && !API_OUTPUT_COUNT.equals(params.getSelectRelatedObject()) && params.getObject()!=EVENT_OBJECT_AUTOREGHOST) {
			CRelationMap relationMap = this.createRelationMap(result, "eventid", "objectid");
			
			CCoreLongKeyDAO idao = null;
			CParamGet cparams = null;
			switch (params.getObject()) {
				case EVENT_OBJECT_TRIGGER:
					idao = API.Trigger(this.idBean, getSqlExecutor());
					cparams = new CTriggerGet();
					((CTriggerGet)cparams).setTriggerIds(relationMap.getRelatedLongIds());
					break;
				case EVENT_OBJECT_DHOST:
					idao = API.DHost(this.idBean, getSqlExecutor());
					cparams = new CDHostGet();
					((CDHostGet)cparams).setDhostIds(relationMap.getRelatedLongIds());
					break;
				case EVENT_OBJECT_DSERVICE:
					idao = API.DService(this.idBean, getSqlExecutor());
					cparams = new CDServiceGet();
					((CDServiceGet)cparams).setDserviceIds(relationMap.getRelatedLongIds());
					break;
				case EVENT_OBJECT_ITEM:
					idao = API.Item(this.idBean, getSqlExecutor());
					cparams = new CItemGet();
					((CItemGet)cparams).setItemIds(relationMap.getRelatedLongIds());
					break;
				case EVENT_OBJECT_LLDRULE:
					idao = API.DiscoveryRule(this.idBean, getSqlExecutor());
					cparams = new CDiscoveryRuleGet();
					((CDiscoveryRuleGet)cparams).setItemIds(relationMap.getRelatedLongIds());
					break;
			}
			
			cparams.setOutput(params.getSelectRelatedObject());
			cparams.setPreserveKeys(true);
			
			CArray<Map> datas = (CArray)idao.get(cparams);
			relationMap.mapMany(result, datas, "relatedObject", null);
		}
		
		// adding alerts
		if (!is_null(params.getSelectAlerts()) && !API_OUTPUT_COUNT.equals(params.getSelectAlerts())) {
			CRelationMap relationMap = this.createRelationMap(result, "eventid", "alertid", "alerts");
			
			CAlertGet aparams = new CAlertGet();
			aparams.setOutput(params.getSelectAlerts());
			aparams.setSelectMediatypes(API_OUTPUT_EXTEND);
			aparams.setAlertIds(relationMap.getRelatedLongIds());
			aparams.setPreserveKeys(true);
			aparams.setSortfield(new String[] {"clock"});
			aparams.setSortorder(new String[] {RDA_SORT_DOWN});
			
			CArray<Map> datas = API.Alert(this.idBean, getSqlExecutor()).get(aparams);
			relationMap.mapMany(result, datas, "alerts", null);
		}
		
		// adding acknowledges
		if (!is_null(params.getSelectAcknowledges())) {
			if (!API_OUTPUT_COUNT.equals(params.getSelectAcknowledges())) {
				// create the base query
				CParamGet cparams = new CParamGet();
				cparams.setOutput(this.outputExtend("acknowledges", new String[] {"acknowledgeid", "eventid", "clock"}, params.getSelectAcknowledges()));
				cparams.setFilter("eventid", eventIds);
				cparams.setSortfield(new String[]{"a.clock"});
				cparams.setSortorder(new String[]{RDA_SORT_DOWN});
				
				SqlBuilder sqlParts = this.createSelectQueryParts("acknowledges", "a", cparams);
				
				// if the user data is requested via extended output or specified fields, join the users table
				String[] userFields = new String[]{"alias", "name", "surname"};
				CArray<String> requestUserData = array();
				for(String userField : userFields) {
					if (this.outputIsRequested(userField, params.getSelectAcknowledges())) {
						requestUserData.add(userField);
					}
				}
				if (!empty(requestUserData)) {
					for(String userField : requestUserData) {
						this.addQuerySelect("u."+userField, sqlParts);
					}
					sqlParts.from.put("users u");
					sqlParts.where.put("a.tenantid=u.tenantid");
					sqlParts.where.put("a.userid=u.userid");
				}
				
				CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
				CArray<Map> acknowledges = dbfetchArrayAssoc(datas, "acknowledgeid");
				
				CRelationMap relationMap = this.createRelationMap(acknowledges, "eventid", "acknowledgeid");
				this.unsetExtraFields(acknowledges, new String[] {"eventid", "acknowledgeid", "clock"}, params.getSelectAcknowledges());
				relationMap.mapMany(result, acknowledges, "acknowledges");
			} else {
				SqlBuilder sqlParts = new SqlBuilder();				
				CArray<Map> acknowledges = dbfetchArrayAssoc(DBselect(getSqlExecutor(),
						"SELECT COUNT(a.acknowledgeid) AS rowscount,a.eventid"+
							" FROM acknowledges a"+
							" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "acknowledges", "a", params)+
							" AND "+sqlParts.dual.dbConditionInt("a.eventid", eventIds)+
							" GROUP BY a.eventid",
						sqlParts.getNamedParams()
					), "eventid");
				
				for(Map event: result) {
					Object id = event.get("eventid");
					if(isset(acknowledges.get(id))) {
						event.put("acknowledges", acknowledges.get(id).get("rowscount"));
					}else {
						event.put("acknowledges", 0);
					}
				}
			}
		}		
	}	
	
	/**
	 * Checks if the given events exist, are accessible and can be acknowledged.
	 *
	 * @throws APIException     if an event does not exist, is not accessible or is not a trigger event
	 *
	 * @param array eventIds
	 *
	 * @return void
	 */
	protected void checkCanBeAcknowledged(Long[] eventIds) {
		CEventGet eparams = new CEventGet();
		eparams.setEventIds(eventIds);
		eparams.setOutput(API_OUTPUT_REFER);
		eparams.setPreserveKeys(true);
		CArray<Map> allowedEventsMap = this.get(eparams);
		
		for(Long eventId: eventIds) {
			if(!isset(allowedEventsMap.get(eventId))) {
				// check if an event actually exists but maybe belongs to a different source or object
				eparams = new CEventGet();
				eparams.setOutput(new String[] {"eventId", "source", "object"});
				eparams.setEventIds(eventId);
				eparams.setLimit(1);
				
				Map event = reset(this.select(this.tableName, eparams));
				
				// if the event exists, check if we have permissions to access it
				CArray<Map> events = null;
				if(isset(event)) {
					eparams = new CEventGet();
					eparams.setOutput(new String[] {"eventid"});
					eparams.setEventIds(Nest.value(event, "eventid").asLong());
					eparams.setSource((Integer)event.get("source"));
					eparams.setObject((Integer)event.get("object"));
					eparams.setLimit(1);
					
					events = this.get(eparams);
				}
				
				// the event exists, is accessible but belongs to a different object or source
				if (isset(events)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Only trigger events can be acknowledged."));
				} else { // the event either doesn't exist or is not accessible
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
			}
		}
	}
}
