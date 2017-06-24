package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.inArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_ITEM;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_LLDRULE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.EventsUtil.eventObject;
import static com.isoft.iradar.inc.EventsUtil.eventSource;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CAlertGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.validators.CSetValidator;
import com.isoft.iradar.validators.CValidator;
import com.isoft.iradar.validators.event.CEventSourceObjectValidator;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with alerts.
 * @author benne
 */
@CodeConfirmed("benne.2.2.6")
public class CAlertDAO extends CCoreLongKeyDAO<CAlertGet> {

	public CAlertDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "alerts", "a", new String[]{"alertid", "clock", "eventid", "status"});
	}

	/**
	 * Get alerts data.
	 *
	 * @param array options
	 * @param array options['itemids']
	 * @param array options['hostids']
	 * @param array options['groupids']
	 * @param array options['alertids']
	 * @param array options['applicationids']
	 * @param array options['status']
	 * @param array options['editable']
	 * @param array options['extendoutput']
	 * @param array options['count']
	 * @param array options['pattern']
	 * @param array options['limit']
	 * @param array options['order']
	 *
	 * @return array|int item data as array or false if error
	 */
	@Override
	public <T> T get(CAlertGet params) {		
		int userType = CWebUser.getType();
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("alerts", "a.alertid");
		sqlParts.from.put("alerts", "alerts a");

		params = convertDeprecatedParam(params, "triggerids", "objectids");
		validateGet(params);
		
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			// triggers
			if (params.getEventObject() == EVENT_OBJECT_TRIGGER) {
				int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;

				// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
				Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);
				sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM events e,functions f,items i,hosts_groups hgg"+
					" JOIN rights r"+
						" ON r.tenantid=hgg.tenantid"+ 
						" AND r.id=hgg.groupid"+
						" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
					" WHERE a.tenantid=e.tenantid"+
						" AND e.tenantid=f.tenantid"+
						" AND f.tenantid=i.tenantid"+
						" AND i.tenantid=hgg.tenantid"+
						" AND a.eventid=e.eventid"+
						" AND e.objectid=f.triggerid"+
						" AND f.itemid=i.itemid"+
						" AND i.hostid=hgg.hostid"+
					" GROUP BY f.triggerid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+
					" AND MAX(r.permission)>="+permission+
				")");
			}
			// items and LLD rules
			else if (params.getEventObject() == EVENT_OBJECT_ITEM || params.getEventObject() == EVENT_OBJECT_LLDRULE) {
				int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;

				Long[] userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid).toArray(new Long[0]);
				// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
				sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM events e,items i,hosts_groups hgg"+
					" JOIN rights r"+
						" ON r.tenantid=hgg.tenantid"+ 
						" AND r.id=hgg.groupid"+
						" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups)+
					" WHERE a.tenantid=e.tenantid"+
						" AND e.tenantid=i.tenantid"+
						" AND i.tenantid=hgg.tenantid"+
						" AND a.eventid=e.eventid"+
						" AND e.objectid=i.itemid"+
						" AND i.hostid=hgg.hostid"+
					" GROUP BY hgg.hostid"+
					" HAVING MIN(r.permission)>"+PERM_DENY+
					" AND MAX(r.permission)>="+permission+
				")");
			}
		}
		
		// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
		sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM events e"+
				" WHERE a.tenantid=e.tenantid"+ 
				" AND a.eventid=e.eventid"+
				" AND e.source="+sqlParts.marshalParam(params.getEventSource())+
				" AND e.object="+sqlParts.marshalParam(params.getEventObject())+
				")");

		// groupids
		if (!is_null(params.getGroupIds())) {
			if(params.getEventObject() == EVENT_OBJECT_TRIGGER){
				// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
				sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM events e,functions f,items i,hosts_groups hg"+
					" WHERE a.tenantid=e.tenantid"+
						" AND e.tenantid=f.tenantid"+
						" AND f.tenantid=i.tenantid"+
						" AND i.tenantid=hg.tenantid"+
						" AND a.eventid=e.eventid"+
						" AND e.objectid=f.triggerid"+
						" AND f.itemid=i.itemid"+
						" AND i.hostid=hg.hostid"+
						" AND "+sqlParts.dual.dbConditionInt("hg.groupid", params.getGroupIds())+
				")");
			} else if(params.getEventObject() == EVENT_OBJECT_LLDRULE || params.getEventObject() == EVENT_OBJECT_ITEM){
				// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
				sqlParts.where.put("EXISTS ("+
					"SELECT NULL"+
					" FROM events e,items i,hosts_groups hg"+
					" WHERE a.tenantid=e.tenantid"+
						" AND e.tenantid=i.tenantid"+
						" AND i.tenantid=hg.tenantid"+
						" AND a.eventid=e.eventid"+
						" AND e.objectid=i.itemid"+
						" AND i.hostid=hg.hostid"+
						" AND "+sqlParts.dual.dbConditionInt("hg.groupid", params.getGroupIds())+
				")");
			}
		}
		
		// hostids
		if (!is_null(params.getHostIds())) {
			if(params.getEventObject() == EVENT_OBJECT_TRIGGER){
				// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
				sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM events e,functions f,items i"+
				" WHERE a.tenantid=e.tenantid"+
					" AND e.tenantid=f.tenantid"+
					" AND f.tenantid=i.tenantid"+
					" AND a.eventid=e.eventid"+
					" AND e.objectid=f.triggerid"+
					" AND f.itemid=i.itemid"+
					" AND "+sqlParts.dual.dbConditionInt("i.hostid", params.getHostIds())+
				")");
			} else if(params.getEventObject() == EVENT_OBJECT_LLDRULE || params.getEventObject() == EVENT_OBJECT_ITEM){
				// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
				sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM events e,items i"+
				" WHERE a.tenantid=e.tenantid"+
					" AND e.tenantid=i.tenantid"+
					" AND a.eventid=e.eventid"+
					" AND e.objectid=i.itemid"+
					" AND "+sqlParts.dual.dbConditionInt("i.hostid", params.getHostIds())+
				")");
			}
		}
		
		// alertids
		if (!is_null(params.getAlertIds())) {
			sqlParts.where.dbConditionInt("a.alertid", params.getAlertIds());
		}
		
		// objectids
		if (!is_null(params.getObjectIds())
				&& inArray(params.getEventObject(), new Integer[] {EVENT_OBJECT_TRIGGER, EVENT_OBJECT_ITEM,EVENT_OBJECT_LLDRULE })) {
			// Oracle does not support using distinct with nclob fields, so we must use exists instead of joins
			sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM events e"+
				" WHERE a.tenantid=e.tenantid"+
					" AND a.eventid=e.eventid"+
					" AND "+sqlParts.dual.dbConditionInt("e.objectid", params.getObjectIds())+
			")");
		}
		
		// eventids
		if (!is_null(params.getEventIds())) {
			sqlParts.where.dbConditionInt("a.eventid", params.getEventIds());
		}
		
		// actionids
		if (!is_null(params.getActionIds())) {
			sqlParts.select.put("actionid","a.actionid");
			sqlParts.where.dbConditionInt("a.actionid", params.getActionIds());
		}
		
		// userids
		if (!is_null(params.getUserIds())) {
			String field = "a.userid";
			if (!is_null(params.getTimeFrom()) || !is_null(params.getTimeTill())) {
				field = "(a.userid+0)";
			}
			sqlParts.where.dbConditionString(field, params.getUserIds());
		}
		
		// mediatypeids
		if (!is_null(params.getMediaTypeIds())) {
			sqlParts.select.put("mediatypeid","a.mediatypeid");
			sqlParts.where.dbConditionInt("a.mediatypeid", params.getMediaTypeIds());
		}
		
		// filter
		if (params.getFilter() != null && !params.getFilter().isEmpty()) {
			dbFilter("alerts a", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("alerts a", params, sqlParts);
		}
		
		// time_from
		if (!is_null(params.getTimeFrom())) {
			sqlParts.where.put("a.clock>"+params.getTimeFrom());
		}

		// time_till
		if (!is_null(params.getTimeTill())) {
			sqlParts.where.put("a.clock<"+params.getTimeTill());
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
					ret = row.get("rowscount");
			} else {
				Long id = (Long)row.get("alertid");
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
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
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
	protected void validateGet(CAlertGet options) {
		CSetValidator sourceValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(eventSource())
		));
		if (!sourceValidator.validate(this.idBean, Nest.as(options.getEventSource()).asString())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect eventsource value."));
		}

		CSetValidator objectValidator = CValidator.init(new CSetValidator(),map(
			"values", array_keys(eventObject())
		));
		if (!objectValidator.validate(this.idBean, Nest.as(options.getEventObject()).asString())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect eventobject value."));
		}

		CEventSourceObjectValidator sourceObjectValidator = CValidator.init(new CEventSourceObjectValidator(),map());
		if (!sourceObjectValidator.validate(this.idBean, map("source", options.getEventSource(), "object", options.getEventObject()))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, sourceObjectValidator.getError());
		}
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		
		if(is_null(params.getCountOutput())){
			if(!is_null(Nest.value(params,"selectUsers").$())){
				addQuerySelect(fieldId("userid"),sqlParts);
			}
			if(!is_null(Nest.value(params,"selectMediatypes").$())){
				addQuerySelect(fieldId("mediatypeid"),sqlParts);
			}
		}
	}

	@Override
	protected void addRelatedObjects(CAlertGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] alertIds = TArray.as(result.keys()).asLong();
		// adding hosts
		if(!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())){
			CArray<Map> datas = null;
			if(params.getEventObject() == EVENT_OBJECT_TRIGGER){
				SqlBuilder sqlParts = new SqlBuilder();
				datas = DBselect(
						getSqlExecutor(),
						"SELECT a.alertid,i.hostid"+
							" FROM alerts a,events e,functions f,items i"+
							" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "alerts", "a", params)+
							" AND "+sqlParts.dual.dbConditionInt("a.alertid", alertIds)+
							" AND a.tenantid=e.tenantid"+
							" AND e.tenantid=f.tenantid"+
							" AND f.tenantid=i.tenantid"+
							" AND a.eventid=e.eventid"+
							" AND e.objectid=f.triggerid"+
							" AND f.itemid=i.itemid"+
							" AND e.object="+sqlParts.marshalParam(params.getEventObject())+
							" AND e.source="+sqlParts.marshalParam(params.getEventSource()),
						sqlParts.getNamedParams()
					);
			} else if(params.getEventObject() == EVENT_OBJECT_ITEM || params.getEventObject() == EVENT_OBJECT_LLDRULE){
				SqlBuilder sqlParts = new SqlBuilder();
				datas = DBselect(
						getSqlExecutor(),
						"SELECT a.alertid,i.hostid"+
							" FROM alerts a,events e,items i"+
							" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "alerts", "a", params)+
							" AND "+sqlParts.dual.dbConditionInt("a.alertid", alertIds)+
							" AND a.tenantid=e.tenantid"+
							" AND e.tenantid=i.tenantid"+
							" AND a.eventid=e.eventid"+
							" AND e.objectid=i.itemid"+
							" AND e.object="+sqlParts.marshalParam(params.getEventObject())+
							" AND e.source="+sqlParts.marshalParam(params.getEventSource()),
						sqlParts.getNamedParams()
					);
			}
			CRelationMap relationMap = new CRelationMap();
			for(Map relation : datas) {
				relationMap.addRelation(Nest.value(relation,"alertid").$(), Nest.value(relation,"hostid").$());
			}
			
			CHostGet hparams = new CHostGet();
			hparams.setOutput(params.getSelectHosts());
			hparams.setHostIds(relationMap.getRelatedLongIds());
			hparams.setPreserveKeys(true);
			
			datas = API.Host(this.idBean, getSqlExecutor()).get(hparams);
			relationMap.mapMany(result, datas, "hosts", null);
		}
		
		// adding users
		if(!is_null(params.getSelectUsers()) && !API_OUTPUT_COUNT.equals(params.getSelectUsers())){
			CRelationMap relationMap = createRelationMap(result, "alertid", "userid");
			CUserGet uparams = new CUserGet();
			uparams.setOutput(params.getSelectUsers());
			uparams.setUserIds(relationMap.getRelatedStringIds());
			uparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.User(this.idBean, getSqlExecutor()).get(uparams);
			relationMap.mapMany(result, datas, "users", null);
		}
		
		// adding media types
		if(!is_null(params.getSelectMediatypes()) && !API_OUTPUT_COUNT.equals(params.getSelectMediatypes())){
			CRelationMap relationMap = createRelationMap(result, "alertid", "mediatypeid");
			CParamGet options = new CParamGet();
			options.setOutput(params.getSelectMediatypes());
			options.setFilter("mediatypeid", relationMap.getRelatedLongIds());
			options.setPreserveKeys(true);
			
			CArray<Map> mediatypes = select("media_type", options);
			relationMap.mapMany(result, mediatypes, "mediatypes", null);
		}
	}	
}
