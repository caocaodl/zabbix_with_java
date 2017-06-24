package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PERMISSIONS;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_mintersect;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CAppManager;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

/**
 * Class containing methods for operations with applications.
 * @author benne
  */
@CodeConfirmed("benne.2.2.6")
public class CAppDAO extends CCoreLongKeyDAO<CAppGet> {

	public CAppDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "applications", "a", new String[]{"applicationid", "name"});
	}
	
	/**
	 * Get applications data.
	 *
	 * @param array  options
	 * @param array  options["itemids"]
	 * @param array  options["hostids"]
	 * @param array  options["groupids"]
	 * @param array  options["triggerids"]
	 * @param array  options["applicationids"]
	 * @param bool   options["status"]
	 * @param bool   options["editable"]
	 * @param bool   options["count"]
	 * @param string options["pattern"]
	 * @param int    options["limit"]
	 * @param string options["order"]
	 *
	 * @return array	item data as array or false if error
	 */
	@Override
	public <T> T get(CAppGet params) {
		int userType = CWebUser.getType();
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("apps", "a.applicationid");
		sqlParts.from.put("applications", "applications a");
		
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
				" WHERE a.tenantid=hgg.tenantid"+ 
					" AND a.hostid=hgg.hostid"+
				" GROUP BY hgg.hostid"+
				" HAVING MIN(r.permission)>"+PERM_DENY+
					" AND MAX(r.permission)>="+permission+
				")");
		}
		
		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.put("ahg","a.hostid=hg.hostid");
			sqlParts.where.dbConditionInt("hg.groupid",params.getGroupIds());

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
			sqlParts.select.put("hostid","a.hostid");
			sqlParts.where.dbConditionInt("hostid","a.hostid",params.getHostIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hostid","a.hostid");
			}
		}

		// itemids
		if (!is_null(params.getItemIds())) {
			sqlParts.select.put("itemid","ia.itemid");
			sqlParts.from.put("items_applications","items_applications ia");
			sqlParts.where.dbConditionInt("ia.itemid",params.getItemIds());
			sqlParts.where.put("aia.tenantid","a.tenantid=ia.tenantid");
			sqlParts.where.put("aia","a.applicationid=ia.applicationid");
		}

		// applicationids
		if (!is_null(params.getApplicationIds())) {
			sqlParts.select.put("applicationid","a.applicationid");
			sqlParts.where.dbConditionInt("a.applicationid",params.getApplicationIds());
		}

		// templated
		if (!is_null(params.getTemplated())) {
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("ah.tenantid","a.tenantid=h.tenantid");
			sqlParts.where.put("ah","a.hostid=h.hostid");

			if (params.getTemplated()) {
				sqlParts.where.put("h.status="+HOST_STATUS_TEMPLATE);
			} else {
				sqlParts.where.put("h.status<>"+HOST_STATUS_TEMPLATE);
			}
		}

		// inherited
		if (!is_null(params.getInherited())) {
			sqlParts.where.put((params.getInherited()?"":"NOT")+" EXISTS ("+
					"SELECT NULL"+
					" FROM application_template at"+
					" WHERE a.tenantid=at.tenantid"+ 
					" AND a.applicationid=at.applicationid"+
					")"
			);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("applications a", params, sqlParts);
		}

		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("applications a", params, sqlParts);
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
				Long id = (Long)row.get("applicationid");
				
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
				// itemids
				if (isset(row.get("itemid")) && is_null(params.getSelectItems())) {
					if (!isset(result.get(id).get("items"))) {
						result.get(id).put("items", new CArray());
					}
					((CArray)result.get(id).get("items")).add(map("itemid", row.get("itemid")));
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
	
	@Override
	public boolean exists(CArray object) {
		CArray keyFields = array(array("hostid", "host"), "name");
		CAppGet options = new CAppGet();
		options.setFilter(rda_array_mintersect(keyFields, object));
		options.setOutput(new String[]{"applicationid"});
		options.setNopermissions(true);
		options.setLimit(1);
		CArray<Map> objs = get(options);
		return !empty(objs);
	}
	
	public void checkInput(CArray<Map> applications, String method) {
		if (empty(applications)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameter."));
		}

		boolean create = ("create".equals(method));
		boolean update = ("update".equals(method));
		boolean delete = ("delete".equals(method));

		// permissions
		CArray itemDbFields = null;
		CArray<Map> dbApplications = null;
		CArray<Map> dbHosts = null;
		if (update || delete) {
			itemDbFields = map("applicationid", null);
			CAppGet params = new CAppGet();
			params.setOutput(API_OUTPUT_EXTEND);
			params.setApplicationIds(rda_objectValues(applications, "applicationid").valuesAsLong());
			params.setEditable(true);
			params.setPreserveKeys(true);
			dbApplications = get(params);
		} else {
			itemDbFields = map("name", null, "hostid", null);
			CHostGet params = new CHostGet();
			params.setOutput(new String[]{"hostid", "host", "status"});
			params.setHostIds(rda_objectValues(applications, "hostid").valuesAsLong());
			params.setTemplatedHosts(true);
			params.setEditable(true);
			params.setPreserveKeys(true);
			dbHosts = API.Host(this.idBean, this.getSqlExecutor()).get(params);
		}

		if (update){
			applications = extendObjects(tableName(), applications, new String[]{"name"});
		}

		for(Map application : applications) {
			if (!check_db_fields(itemDbFields, Clone.deepcopy(application))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect arguments passed to function."));
			}

			// check permissions by hostid
			if (create) {
				if (!isset(dbHosts,application.get("hostid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}
			}

			// check permissions by applicationid
			if (delete || update) {
				if (!isset(dbApplications,application.get("applicationid"))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}
			}

			// check for \"templateid\", because it is not allowed
			if (array_key_exists("templateid", application)) {
				String error = null;
				if (update) {
					error  = _s("Cannot update \"templateid\" for application \"%1$s\".", Nest.value(application,"name").$());
				} else {
					error = _s("Cannot set \"templateid\" for application \"%1$s\".", Nest.value(application,"name").$());
				}
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, error);
			}

			// check on operating with templated applications
			if (delete || update) {
				if (!empty(Nest.value(dbApplications,application.get("applicationid"),"templateids").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot update templated applications."));
				}
			}

			if (update) {
				if (!isset(application,"hostid")) {
					Nest.value(application,"hostid").$(Nest.value(dbApplications,application.get("applicationid"),"hostid").$());
				}
			}

			// check existence
			if (update || create) {
				CAppGet params = new CAppGet();
				params.setOutput(API_OUTPUT_EXTEND);
				params.setFilter("hostid", Nest.value(application,"hostid").asString());
				params.setFilter("name", Nest.value(application,"name").asString());
				params.setNopermissions(true);
				CArray<Map> applicationsExists = get(params);
				for(Map applicationExists : applicationsExists) {
					if (!update || (bccomp(Nest.value(applicationExists,"applicationid").$(), Nest.value(application,"applicationid").$()) != 0)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Application \"%1$s\" already exists.", Nest.value(application,"name").$()));
					}
				}
			}
		}
	}

	/**
	 * Create new applications.
	 *
	 * @param array applications
	
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> applications) {
		checkInput(applications, "create");
		CAppManager appManager = Manager.Application(idBean, getSqlExecutor());
		applications = appManager.create(applications);
		appManager.inherit(applications);
		return map("applicationids", rda_objectValues(applications, "applicationid").valuesAsLong());
	}
	
	/**
	 * Update applications.
	 *
	 * @param array applications
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> applications) {
		checkInput(applications, "update");
		CAppManager appManager = Manager.Application(idBean, getSqlExecutor());
		appManager.update(applications);
		appManager.inherit(applications);
		return map("applicationids", rda_objectValues(applications, "applicationid").valuesAsLong());
	}
	
	/**
	 * Delete Applications
	 *
	 * @param array applicationids
	 * @return array
	 */
	@Override
	public CArray<Long[]> delete(Long... applicationids) {
		return delete(false, applicationids);
	}
	
	public CArray<Long[]> delete(boolean nopermissions, Long... applicationids) {
		Long[] delApplicationIds = Arrays.copyOf(applicationids, applicationids.length);
		CAppGet options = new CAppGet();
		options.setApplicationIds(TArray.as(applicationids).asLong());
		options.setEditable(true);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setPreserveKeys(true);
		options.setSelectHosts(new String[]{"name", "hostid"});
		CArray<Map> delApplications = get(options);

		if (!nopermissions) {
			for(Long applicationid : applicationids) {
				if (!isset(delApplications,applicationid)) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
				}
				if (!empty(Nest.value(delApplications,applicationid,"templateids").$())) {
					throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Cannot delete templated application."));
				}
			}
		}

		CAppManager appManager = Manager.Application(idBean, getSqlExecutor());

		// fetch application children
		CArray childApplicationIds = array();
		Long[] parentApplicationIds = Arrays.copyOf(applicationids, applicationids.length);
		while (!empty(parentApplicationIds)) {
			parentApplicationIds = appManager.fetchExclusiveChildIds(parentApplicationIds).valuesAsLong();
			for(Long appId : parentApplicationIds) {
				Nest.value(childApplicationIds,appId).$(appId);
			}
		}

		// filter children that can be deleted
		if (!empty(childApplicationIds)) {
			childApplicationIds = appManager.fetchEmptyIds(childApplicationIds.valuesAsLong());
		}

		options = new CAppGet();
		options.setApplicationIds(childApplicationIds.valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setNopermissions(true);
		options.setPreserveKeys(true);
		options.setSelectHosts(new String[]{"name", "hostid"});
		CArray<Map> childApplications = get(options);

		appManager.delete(array_merge(TArray.as(applicationids).asString(), childApplicationIds.valuesAsString()));

		return map("applicationids", delApplicationIds);
	}
	
	/**
	 * Add Items to applications.
	 *
	 * @param array data
	 * @param array data["applications"]
	 * @param array data["items"]
	 *
	 * @return array
	 */
	public Object massAdd(CArray<CArray<Map>> data) {
		if (empty(Nest.value(data,"applications").$()) || empty(Nest.value(data,"items").$())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Empty input parameters."));
		}

		CArray<Map> applications = Nest.value(data,"applications").asCArray();
		Long[] applicationIds = rda_objectValues(applications, "applicationid").valuesAsLong();
		CArray<Map> items = Nest.value(data,"items").asCArray();
		Long[] itemIds = rda_objectValues(items, "itemid").valuesAsLong();

		// validate permissions
		CAppGet options = new CAppGet();
		options.setApplicationIds(TArray.as(applicationIds).asLong());
		options.setOutput(new String[]{"applicationid", "hostid", "name"});
		options.setSelectHosts(new String[]{"hostid", "name"});
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> allowedApplications = get(options);
		for(Map application : applications) {
			if (!isset(allowedApplications,application.get("applicationid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		CItemGet params = new CItemGet();
		params.setItemIds(TArray.as(itemIds).asLong());
		params.setSelectHosts(new String[]{"name"});
		params.setOutput(new String[]{"itemid", "hostid", "name"});
		params.setEditable(true);
		params.setPreserveKeys(true);
		CArray<Map> allowedItems = API.Item(this.idBean, this.getSqlExecutor()).get(params);
		for(Map item : items) {
			if (!isset(allowedItems,item.get("itemid"))) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("No permissions to referred object or it does not exist!"));
			}
		}

		// validate hosts
		Map dbApplication = reset(allowedApplications);
		Map dbApplicationHost = reset(Nest.value(dbApplication,"hosts").asCArray());
		for(Map application : applications) {
			if (Nest.value(dbApplicationHost,"hostid").asLong() != Nest.value(allowedApplications,application.get("applicationid"),"hostid").asLong()) {
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS, _("Cannot process applications from different hosts or templates."));
			}
		}

		for(Map item : items) {
			Map dbItem = Nest.value(allowedItems,item.get("itemid")).asCArray();

			if (Nest.value(dbItem,"hostid").asLong() != Nest.value(dbApplicationHost,"hostid").asLong()) {
				Nest.value(dbItem,"host").$(reset(Nest.value(dbItem,"hosts").asCArray()));
				throw CDB.exception(RDA_API_ERROR_PERMISSIONS,
					_s("Cannot add item \"%1$s\" from \"%2$s\" to application \"%3$s\" from \"%4$s\".",
						Nest.value(dbItem,"name").$(), Nest.value(dbItem,"host","name").$(), Nest.value(dbApplication,"name").$(), Nest.value(dbApplicationHost,"name").$()));
			}
		}

		// link application with item
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> linkedDb = DBselect(getSqlExecutor(), 
			"SELECT ia.itemid,ia.applicationid"+
			" FROM items_applications ia"+
			" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "items_applications", "ia", params)+
			    " AND "+sqlParts.dual.dbConditionInt("ia.itemid", itemIds)+
				" AND "+sqlParts.dual.dbConditionInt("ia.applicationid", applicationIds),
			sqlParts.getNamedParams()
		);
		CArray linked = array();
		for(Map pair : linkedDb) {
			Nest.value(linked,pair.get("applicationid"),pair.get("itemid")).$(true);
		}

		CArray<Map> createApplications = array();

		for(Long applicationId : applicationIds) {
			for(Long itemId : itemIds) {
				if (isset(linked,applicationId) && isset(Nest.value(linked,applicationId,itemId).$())) {
					continue;
				}

				createApplications.add(map(
					"itemid", itemId,
					"applicationid", applicationId
				));
			}
		}

		insert("items_applications", createApplications);

		// mass add applications for children
		Map sqlParams = new HashMap();
		for(Long itemId : itemIds) {
			sqlParams.put("itemId", itemId);
			CArray<Map> dbChilds = DBselect(getSqlExecutor(), "SELECT i.itemid,i.hostid FROM items i WHERE "+
						sqlParts.dual.dbConditionTenants(this.idBean, "items", "i", params)+
						" AND i.templateid=#{itemId}", sqlParams);

			for(Map child : dbChilds) {
				sqlParts = new SqlBuilder();
				CArray<Map> dbApplications = DBselect(getSqlExecutor(), 
					"SELECT a1.applicationid" +
					" FROM applications a1,applications a2" +
					" WHERE "+sqlParts.dual.dbConditionTenants(this.idBean, "applications", "a1", params)+
						" AND a1.tenantid=a2.tenantid"+
						" AND a1.name=a2.name" +
						" AND a1.hostid="+ Nest.value(child,"hostid").asString()+
						" AND "+sqlParts.dual.dbConditionInt("a2.applicationid", applicationIds),
					sqlParts.getNamedParams()
				);

				CArray<Map> childApplications = array();

				for(Map dbApp : dbApplications) {
					childApplications.add(dbApp);
				}

				if (empty(massAdd((CArray)map("items", child, "applications", childApplications)))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Cannot add items."));
				}
			}
		}
		return map("applicationids", applicationIds);
	}


	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		
		if(is_null(params.getCountOutput())){
			if(!is_null(params.get("expandData"))){
				sqlParts.select.put("host", "host");
				sqlParts.from.put("hosts","hosts h");
				sqlParts.where.put("ah.tenantid","a.tenantid=h.tenantid");
				sqlParts.where.put("ah","a.hostid=h.hostid");
			}
			if(!is_null(params.get("selectHosts"))){
				addQuerySelect("a.hostid", sqlParts);
			}
		}
	}

	@Override
	protected void addRelatedObjects(CAppGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] appIds = result.keysAsLong();
		// add application templates
		if (outputIsRequested("templateids",params.getOutput())) {
			SqlBuilder sqlParts = new SqlBuilder();		
			sqlParts.select.put("at.application_templateid");
			sqlParts.select.put("at.applicationid");
			sqlParts.select.put("at.templateid");
			sqlParts.from.put("application_template at");
			applyQueryTenantOptions("application_template", "at", params, sqlParts);
			sqlParts.where.dbConditionInt("at.applicationid", appIds);
			
			CArray<Map> datas = DBselect(getSqlExecutor(), sqlParts);
			
			CRelationMap relationMap = new CRelationMap();
			CArray<Map> mdatas = new CArray<Map>();
			for(Map row : datas){
				relationMap.addRelation(row.get("applicationid"), row.get("application_templateid"));
				mdatas.put(row.get("application_templateid"), row.get("templateid"));
			}
			relationMap.mapMany(result, mdatas, "templateids", params.getLimitSelects());
		}
		
		// adding hosts
		if (!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())) {
			CRelationMap relationMap = createRelationMap(result, "applicationid", "hostid");
			CHostGet hparams = new CHostGet();
			hparams.setOutput(params.getSelectHosts());
			hparams.setHostIds(relationMap.getRelatedLongIds());
			hparams.setTemplatedHosts(true);
			hparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.Host(this.idBean, getSqlExecutor()).get(hparams);
			relationMap.mapMany(result, datas, "hosts", null);
		}
		
		// adding items
		if (!is_null(params.getSelectItems()) && !API_OUTPUT_COUNT.equals(params.getSelectItems())) {
			CRelationMap relationMap = createRelationMap(result, "applicationid", "itemid", "items_applications");
			CItemGet iparams = new CItemGet();
			iparams.setOutput(params.getSelectItems());
			iparams.setItemIds(relationMap.getRelatedLongIds());
			iparams.setPreserveKeys(true);
			
			CArray<Map> datas = API.Item(this.idBean, getSqlExecutor()).get(iparams);
			relationMap.mapMany(result, datas, "items", null);
		}
	}

}
