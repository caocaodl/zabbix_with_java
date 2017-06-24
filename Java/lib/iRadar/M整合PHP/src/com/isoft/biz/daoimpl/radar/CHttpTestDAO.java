package com.isoft.biz.daoimpl.radar;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_key_exists;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.preg_grep;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strInArray;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.check_db_fields;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCENARIO;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.PERM_DENY;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PERM_READ_WRITE;
import static com.isoft.iradar.inc.Defines.RDA_API_ERROR_PARAMETERS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_EXPRESSION_USER_MACROS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_PARAMS;
import static com.isoft.iradar.inc.Defines.RDA_PREG_PRINT;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.FuncsUtil.checkRequiredKeys;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_cleanHashes;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HttpTestUtil.resolveHttpTestMacros;
import static com.isoft.iradar.inc.PermUtil.getUserGroupsByUserId;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.CRelationMap;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;

@CodeConfirmed("benne.2.2.6")
public class CHttpTestDAO extends CCoreLongKeyDAO<CHttpTestGet> {

	public CHttpTestDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "httptest", "ht", new String[] { "httptestid", "name" });
	}

	/**
	 * Get data about web scenarios.
	 *
	 * @param array options
	 *
	 * @return array
	 */
	@Override
	public <T> T get(CHttpTestGet params) {
		int userType = CWebUser.getType();;
		String userid = Nest.value(userData(), "userid").asString();
		
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("httptests", "ht.httptestid");
		sqlParts.from.put("httptest", "httptest ht");
	
		checkDeprecatedParam(params, "output", "macros");
		checkDeprecatedParam(params, "selectSteps", "webstepid");
		
		// editable + PERMISSION CHECK
		if (userType != USER_TYPE_SUPER_ADMIN && !params.getNopermissions()) {
			int permission = params.getEditable() ? PERM_READ_WRITE : PERM_READ;
			List<Long> userGroups = getUserGroupsByUserId(this.idBean, getSqlExecutor(),userid);
			sqlParts.where.put("EXISTS ("+
				"SELECT NULL"+
				" FROM hosts_groups hgg"+
					" JOIN rights r"+
						" ON r.tenantid=hgg.tenantid"+ 
						" AND r.id=hgg.groupid"+
							" AND "+sqlParts.dual.dbConditionInt("r.groupid", userGroups.toArray(new Long[0]))+
				" WHERE ht.tenantid=hgg.tenantid"+
				" AND ht.hostid=hgg.hostid"+
				" GROUP BY hgg.hostid"+
				" HAVING MIN(r.permission)>"+PERM_DENY+
					" AND MAX(r.permission)>="+permission+
				")");
		}
		
		// httptestids
		if (!is_null(params.getHttptestIds())) {
			sqlParts.select.put("httptestid","ht.httptestid");
			sqlParts.where.dbConditionInt("httptestid", "ht.httptestid", params.getHttptestIds());
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
			sqlParts.where.dbConditionInt("hostid", "ht.hostid", params.getHostIds());

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hostid","ht.hostid");
			}
		}

		// groupids
		if (!is_null(params.getGroupIds())) {
			sqlParts.select.put("groupid","hg.groupid");
			sqlParts.from.put("hosts_groups","hosts_groups hg");
			sqlParts.where.dbConditionInt("hg.groupid", params.getGroupIds());
			sqlParts.where.put("hg.tenantid=ht.tenantid");
			sqlParts.where.put("hg.hostid=ht.hostid");

			if (!is_null(params.getGroupCount())) {
				sqlParts.group.put("hg","hg.groupid");
			}
		}

		// applicationids
		if (!is_null(params.getApplicationIds())) {
			if (!API_OUTPUT_EXTEND.equals(params.getOutput())) {
				sqlParts.select.put("applicationid","a.applicationid");
			}
			sqlParts.where.dbConditionInt("ht.applicationid", params.getApplicationIds());
		}

		// inherited
		if (isset(params.getInherited())) {
			sqlParts.where.put(params.getInherited()? "ht.templateid IS NOT NULL": "ht.templateid IS NULL");
		}

		// templated
		if (isset(params.getTemplated())) {
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("ha.tenantid","h.tenantid=ht.tenantid");
			sqlParts.where.put("ha","h.hostid=ht.hostid");
			if (params.getTemplated()) {
				sqlParts.where.put("h.status="+HOST_STATUS_TEMPLATE);
			} else {
				sqlParts.where.put("h.status<>"+HOST_STATUS_TEMPLATE);
			}
		}

		// monitored
		if (!is_null(params.getMonitored())) {
			sqlParts.from.put("hosts","hosts h");
			sqlParts.where.put("hht.tenantid","h.tenantid=ht.tenantid");
			sqlParts.where.put("hht","h.hostid=ht.hostid");

			if (params.getMonitored()) {
				sqlParts.where.put("h.status="+HOST_STATUS_MONITORED);
				sqlParts.where.put("h.status="+ITEM_STATUS_ACTIVE);
			} else {
				sqlParts.where.put("(h.status<>"+HOST_STATUS_MONITORED+" OR ht.status<>"+ITEM_STATUS_ACTIVE+")");
			}
		}
		
		// filter
		if (params.getFilter()!=null && !params.getFilter().isEmpty()) {
			dbFilter("httptest ht", params, sqlParts);
		}

		// search
		if (params.getSearch()!=null && !params.getSearch().isEmpty()) {
			dbSearch("httptest ht", params, sqlParts);
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
				Long id = (Long)row.get("httptestid");
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

			// expandName
			boolean nameRequested = (isArray(params.getOutput()) && strInArray("name", (String[])params.getOutput())) || API_OUTPUT_EXTEND.equals(params.getOutput());
			boolean expandName = params.getExpandName()!=null && nameRequested;

			// expandStepName
			boolean stepNameRequested = API_OUTPUT_EXTEND.equals(params.getSelectSteps()) || (isArray(params.getSelectSteps()) && strInArray("name", (String[])params.getSelectSteps()));
			boolean expandStepName = params.getExpandStepName()!=null && stepNameRequested;

			if (expandName || expandStepName) {
				resolveHttpTestMacros(this.idBean, this.getSqlExecutor(), result, expandName, expandStepName);
			}
			
			unsetExtraFields(result, new String[]{"hostid"}, params.getOutput());
		}

		// removing keys (hash -> array)
		if (is_null(params.getPreserveKeys()) || !params.getPreserveKeys()) {
			result = rda_cleanHashes(result);
		}
		
		// deprecated fields
		this.handleDeprecatedOutput(result, "macros", "variables", params.getOutput());
		
		return (T)result;
	}
	
	/**
	 * Create web scenario.
	 *
	 * @param httpTests
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> create(CArray<Map> httpTests) {
		// find hostid by applicationid
		Map params = new HashMap();
		for (Entry<Object, Map> e : httpTests.entrySet()) {
		    Map httpTest = e.getValue();
			unset(httpTest,"templateid");

			// convert deprecated params
			convertDeprecatedParam(httpTest, "macros", "variables");

			if (empty(Nest.value(httpTest,"hostid").$()) && !empty(Nest.value(httpTest,"applicationid").$())) {
				params.put("applicationid", Nest.value(httpTest,"applicationid").$());
				Map dbHostId = DBfetch(DBselect(getSqlExecutor(),
						"SELECT a.hostid"+
						" FROM applications a"+
						" WHERE a.applicationid=#{applicationid}",params));
				httpTest.put("hostid", Nest.value(dbHostId,"hostid").$());
			}
		}

		validateCreate(httpTests);

		httpTests = Manager.HttpTest(idBean, getSqlExecutor()).persist(httpTests);

		return map("httptestids", rda_objectValues(httpTests, "httptestid").valuesAsLong());
	}
	
	/**
	 * Update web scenario.
	 *
	 * @param httpTests
	 *
	 * @return array
	 */
	@Override
	public CArray<Long[]> update(CArray<Map> httpTests) {
		// TODO Auto-generated method stub
		httpTests = rda_toHash(httpTests, "httptestid");
		for (Map httpTest : httpTests) {
			unset(httpTest,"templateid");

			// convert deprecated parameters
			convertDeprecatedParam(httpTest, "macros", "variables");
			if (isset(httpTest,"steps")) {
				for(Map step : (CArray<Map>)Nest.value(httpTest,"steps").asCArray()) {
					convertDeprecatedParam(step, "webstepid", "httpstepid");
				}
			}
		}

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbHttpTests = array();
		CArray<Map> dbCursor = DBselect(getSqlExecutor(),
				"SELECT ht.httptestid,ht.hostid,ht.templateid,ht.name"+
				" FROM httptest ht"+
				" WHERE "+sqlParts.dual.dbConditionInt("ht.httptestid", array_keys(httpTests).valuesAsLong()),
				sqlParts.getNamedParams());
		for(Map dbHttpTest : dbCursor) {
			Nest.value(dbHttpTests,dbHttpTest.get("httptestid")).$(dbHttpTest);
		}
		
		sqlParts = new SqlBuilder();
		dbCursor = DBselect(getSqlExecutor(),
				"SELECT hs.httpstepid,hs.httptestid,hs.name"+
				" FROM httpstep hs"+
				" WHERE "+sqlParts.dual.dbConditionInt("hs.httptestid", array_keys(dbHttpTests).valuesAsLong()),
				sqlParts.getNamedParams());
		for(Map dbHttpStep : dbCursor) {
			Nest.value(dbHttpTests,dbHttpStep.get("httptestid"),"steps",dbHttpStep.get("httpstepid")).$(dbHttpStep);
		}

		// add hostid if missing
		// add test name and steps names if it's empty or test is templated
		// unset steps no for templated tests
		for(Map httpTest : httpTests) {
			Map dbTest = dbHttpTests.get(httpTest.get("httptestid"));
			Nest.value(httpTest,"hostid").$(Nest.value(dbTest,"hostid").$());

			if (!isset(httpTest,"name") || rda_empty(Nest.value(httpTest,"name").$()) || !empty(Nest.value(dbTest,"templateid").$())) {
				Nest.value(httpTest,"name").$(Nest.value(dbTest,"name").$());
			}

			if (array_key_exists("steps", httpTest) && isArray(Nest.value(httpTest,"steps").$())) {
				for(Map step : (CArray<Map>)Nest.value(httpTest,"steps").asCArray()) {
					if (isset(step,"httpstepid")
							&& (!empty(Nest.value(dbTest,"templateid").$()) || !array_key_exists("name", step))) {
						Nest.value(step,"name").$(Nest.value(dbTest,"steps",step.get("httpstepid"),"name").$());
					}
					if (!empty(Nest.value(dbTest,"templateid").$())) {
						unset(step,"no");
					}
				}
			}
		}

		validateUpdate(httpTests, dbHttpTests);

		Manager.HttpTest(idBean, getSqlExecutor()).persist(httpTests);

		return map("httptestids", rda_objectValues(httpTests, "httptestid").valuesAsLong());
	}
	
	/**
	 * Delete web scenario.
	 *
	 * @param httpTestIds
	 *
	 * @return array|bool
	 */
	@Override
	public CArray<Long[]> delete(Long... httpTestIds) {
		return delete(false, httpTestIds);
	}
	
	public CArray<Long[]> delete(boolean nopermissions, Long... httpTestIds) {
		CHttpTestGet htoptions = new CHttpTestGet();
		htoptions.setHttptestIds(TArray.as(httpTestIds).asLong());
		htoptions.setOutput(API_OUTPUT_EXTEND);
		htoptions.setEditable(true);
		htoptions.setSelectHosts(API_OUTPUT_EXTEND);
		htoptions.setPreserveKeys(true);
		CArray<Map> delHttpTests = get(htoptions);
		if (!nopermissions) {
			for(Long httpTestId : httpTestIds) {
				if (!empty(Nest.value(delHttpTests,httpTestId,"templateid").$())) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Cannot delete templated web scenario \"%1$s\".", Nest.value(delHttpTests,httpTestId,"name").$()));
				}
				if (!isset(delHttpTests,httpTestId)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}
			}
		}

		CArray parentHttpTestIds = CArray.valueOf(httpTestIds);
		CArray childHttpTestIds = array();
		SqlBuilder sqlParts = null;
		CArray<Map> dbTests = null;
		do {
			sqlParts = new SqlBuilder();
			dbTests = DBselect(
					getSqlExecutor(),
					"SELECT ht.httptestid FROM httptest ht WHERE "+sqlParts.dual.dbConditionInt("ht.templateid", parentHttpTestIds.valuesAsLong()),
					sqlParts.getNamedParams()
			);
			parentHttpTestIds = array();
			for(Map dbTest : dbTests) {
				parentHttpTestIds.add(Nest.value(dbTest,"httptestid").$());
				Nest.value(childHttpTestIds,dbTest.get("httptestid")).$(Nest.value(dbTest,"httptestid").$());
			}
		} while (!empty(parentHttpTestIds));

		htoptions = new CHttpTestGet();
		htoptions.setHttptestIds(childHttpTestIds.valuesAsLong());
		htoptions.setOutput(API_OUTPUT_EXTEND);
		htoptions.setNopermissions(true);
		htoptions.setPreserveKeys(true);
		htoptions.setSelectHosts(API_OUTPUT_EXTEND);
		CArray<Map> delHttpTestChilds = get(htoptions);
		delHttpTests = rda_array_merge(delHttpTests, delHttpTestChilds);
		httpTestIds = array_merge(CArray.valueOf(httpTestIds), childHttpTestIds).valuesAsLong();

		CArray itemidsDel = array();
		sqlParts = new SqlBuilder();
		CArray<Map> dbTestItems = DBselect(
			getSqlExecutor(),
			"SELECT hsi.itemid"+
			" FROM httptestitem hsi"+
			" WHERE "+sqlParts.dual.dbConditionInt("hsi.httptestid", httpTestIds),
			sqlParts.getNamedParams()
		);
		for(Map testitem : dbTestItems) {
			itemidsDel.add(Nest.value(testitem,"itemid").$());
		}

		sqlParts = new SqlBuilder();
		CArray<Map> dbStepItems = DBselect(
			getSqlExecutor(),
			"SELECT DISTINCT hsi.itemid"+
			" FROM httpstepitem hsi,httpstep hs"+
			" WHERE "+sqlParts.dual.dbConditionInt("hs.httptestid", httpTestIds)+
				" AND hs.tenantid=hsi.tenantid"+	
				" AND hs.httpstepid=hsi.httpstepid",
			sqlParts.getNamedParams()
		);
		for(Map stepitem : dbStepItems) {
			itemidsDel.add(Nest.value(stepitem,"itemid").$());
		}

		if (!empty(itemidsDel)) {
			API.Item(this.idBean, this.getSqlExecutor()).delete(true,itemidsDel.valuesAsLong());
		}

		delete("httptest", (CArray)map("httptestid", httpTestIds));

		// TODO: REMOVE
		for(Map httpTest : delHttpTests) {
			Map host = reset((CArray<Map>)Nest.value(httpTest,"hosts").asCArray());

			info(_s("Deleted: Web scenario \"%1$s\" on \"%2$s\".", Nest.value(httpTest,"name").$(), Nest.value(host,"host").$()));
			add_audit(this.idBean, getSqlExecutor(),AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCENARIO,
				"Web scenario \""+Nest.value(httpTest,"name").asString()+"\" \""+Nest.value(httpTest,"httptestid").asString()+"\" host \""+Nest.value(host,"host").asString()+"\".");
		}

		return map("httptestids", httpTestIds);
	}

	/**
	 * Validate web scenario parameters for create method.
	 *  - check if web scenario with same name already exists
	 *  - check if web scenario has at least one step
	 *
	 * @param array httpTests
	 */
	protected void validateCreate(CArray<Map> httpTests) {
		checkNames(httpTests);

		Map params = new HashMap();
		for(Map httpTest : httpTests) {
			CArray missingKeys = checkRequiredKeys(httpTest, array("name", "hostid", "steps"));
			if (!empty(missingKeys)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Web scenario missing parameters: %1$s", implode(", ", missingKeys)));
			}

			params.put("name", Nest.value(httpTest,"name").$());
			params.put("hostid", Nest.value(httpTest,"hostid").$());
			Map nameExists = DBfetch(DBselect(getSqlExecutor(),
				"SELECT ht.name FROM httptest ht"+
				" WHERE ht.name=#{name}"+
					" AND ht.hostid=#{hostid}", 1, params));
			if (!empty(nameExists)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Web scenario \"%1$s\" already exists.", Nest.value(nameExists,"name").$()));
			}

			checkSteps(httpTest);
			checkDuplicateSteps(httpTest);
		}

		checkApplicationHost(httpTests);
	}
	
	/**
	 * Validate web scenario parameters for update method.
	 *  - check permissions
	 *  - check if web scenario with same name already exists
	 *  - check that each web scenario object has httptestid defined
	 *
	 * @param array httpTests
	 */
	protected void validateUpdate(CArray<Map> httpTests, CArray<Map> dbHttpTests) {
		CArray httpTestIds = rda_objectValues(httpTests, "httptestid");

		if (!isWritable(httpTestIds.valuesAsLong())) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("You do not have permission to perform this operation."));
		}

		checkNames(httpTests);

		Map params = new HashMap();
		Object hostId;
		for(Map httpTest : httpTests) {
			CArray missingKeys = checkRequiredKeys(httpTest, array("httptestid"));
			if (!empty(missingKeys)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Web scenario missing parameters: %1$s", implode(", ", missingKeys)));
			}

			if (isset(httpTest,"name")) {
				// get hostid from db if it's not provided
				if (isset(httpTest,"hostid")) {
					hostId = Nest.value(httpTest,"hostid").$();
				} else {
					params.put("httptestid", Nest.value(httpTest,"httptestid").$());
					Map dbhost = DBfetch(DBselect(getSqlExecutor(), "SELECT ht.hostid FROM httptest ht WHERE ht.httptestid=#{httptestid}",params));
					hostId = Nest.value(dbhost,"hostid").$();
				}

				params.put("hostid", hostId);
				params.put("name", Nest.value(httpTest,"name").$());
				params.put("httptestid", Nest.value(httpTest,"httptestid").$());
				Map nameExists = DBfetch(DBselect(getSqlExecutor(),
					"SELECT ht.name FROM httptest ht"+
					" WHERE ht.name=#{name}"+
						" AND ht.hostid=#{hostid}"+
						" AND ht.httptestid<>#{httptestid}", 1, params));
				if (!empty(nameExists)) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Web scenario \"%1$s\" already exists.", Nest.value(nameExists,"name").$()));
				}
			}

			if (!check_db_fields(map("httptestid", null), httpTest)) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Incorrect arguments passed to function."));
			}

			if (array_key_exists("steps", httpTest)) {
				Map dbHttpTest = isset(httpTest,"httptestid") ? dbHttpTests.get(httpTest.get("httptestid")) : null;
				checkSteps(httpTest, dbHttpTest);
				checkDuplicateSteps(httpTest);
			}
		}

		checkApplicationHost(httpTests);
	}
	
	/**
	 * Check that application belongs to http test host.
	 *
	 * @param array httpTests
	 */
	protected void checkApplicationHost(CArray<Map> httpTests) {
		CArray appIds = rda_objectValues(httpTests, "applicationid");
		appIds = rda_toHash(appIds);
		unset(appIds,"0");

		if (!empty(appIds)) {
			CArray appHostIds = array();
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbCursor = DBselect(getSqlExecutor(),
				"SELECT a.hostid,a.applicationid"+
				" FROM applications a"+
				" WHERE "+sqlParts.dual.dbConditionInt("a.applicationid", appIds.valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map dbApp : dbCursor) {
				Nest.value(appHostIds,dbApp.get("applicationid")).$(Nest.value(dbApp,"hostid").$());
			}

			for(Map httpTest : httpTests) {
				if (isset(httpTest,"applicationid")) {
					if (!idcmp(Nest.value(appHostIds,httpTest.get("applicationid")).$(), Nest.value(httpTest,"hostid").$())) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("The web scenario application belongs to a different host than the web scenario host."));
					}
				}
			}
		}
	}
	
	/**
	 * Check web scenario steps.
	 *  - check status_codes field
	 *  - check name characters
	 *  - check if httpstepid values are from current web scenario
	 *  - check if name is valid
	 *  - check if url is valid
	 *
	 * @param array httpTest
	 * @param array|null dbHttpTest
	 */
	protected void checkSteps(Map httpTest) {
		checkSteps(httpTest, array());
	}
	
	protected void checkSteps(Map httpTest, Map dbHttpTest) {
		if (array_key_exists("steps", httpTest)
				&& (!isArray(Nest.value(httpTest,"steps").$()) || (count(Nest.value(httpTest,"steps").$()) == 0))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Web scenario must have at least one step."));
		}

		CArray stepNames = rda_objectValues(Nest.value(httpTest,"steps").$(), "name");
		if (!empty(stepNames) && empty(preg_grep(RDA_PREG_PARAMS, stepNames))) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Web scenario step name should contain only printable characters."));
		}

		if (!empty(dbHttpTest)) {
			CArray httpTestStepIds = rda_objectValues(Nest.value(httpTest,"steps").$(), "httpstepid");

			if (!empty(httpTestStepIds)) {
				CArray dbHttpTestStepIds = rda_objectValues(Nest.value(dbHttpTest,"steps").$(), "httpstepid");

				if (!empty(array_diff(httpTestStepIds, dbHttpTestStepIds))) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("No permissions to referred object or it does not exist!"));
				}
			}
		}

		for(Map step : (CArray<Map>)Nest.value(httpTest,"steps").asCArray()) {
			if ((isset(step,"httpstepid") && array_key_exists("name", step) && rda_empty(Nest.value(step,"name").$()))
					|| (!isset(step,"httpstepid") && (!array_key_exists("name", step) || rda_empty(Nest.value(step,"name").$())))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Web scenario step name cannot be empty."));
			}

			if ((isset(step,"httpstepid") && array_key_exists("url", step) && rda_empty(Nest.value(step,"url").$()))
					|| (!isset(step,"httpstepid") && (!array_key_exists("url", step) || rda_empty(Nest.value(step,"url").$())))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Web scenario step URL cannot be empty."));
			}

			if (isset(step,"no") && Nest.value(step,"no").asInteger() <= 0) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Web scenario step number cannot be less than 1."));
			}

			if (isset(step,"status_codes")) {
				checkStatusCode(Nest.value(step,"status_codes").asString());
			}
		}
	}
	
	/**
	 * Check duplicate step names.
	 *
	 * @param array httpTest
	 */
	protected void checkDuplicateSteps(Map httpTest) {
		Map duplicate = CArrayHelper.findDuplicate(Nest.value(httpTest,"steps").asCArray(), "name");
		if (!empty(duplicate)) {
			throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Web scenario step \"%1$s\" already exists.", Nest.value(duplicate,"name").$()));
		}
	}
	
	/**
	 * Validate http response code range.
	 * Range can be empty string, can be set as user macro or be numeric and contain "," and "-".
	 *
	 * Examples: "100-199, 301, 404, 500-550" or "{$USER_MACRO123}"
	 *
	 * @throws APIException if the status code range is invalid.
	 *
	 * @param string statusCodeRange
	 *
	 * @return bool
	 */
	protected  boolean checkStatusCode(String statusCodeRange) {
		if (statusCodeRange==null || statusCodeRange.length()==0 || preg_match("^"+RDA_PREG_EXPRESSION_USER_MACROS+"$", statusCodeRange)==0) {
			return true;
		} else {
			String[] ranges = explode(",", statusCodeRange);
			for(String srange : ranges) {
				String[] range = explode("-", srange);
				if (count(range) > 2) {
					throw CDB.exception(RDA_API_ERROR_PARAMETERS, _s("Invalid response code \"%1$s\".", statusCodeRange));
				}

				for(String value : range) {
					if (!is_numeric(value)) {
						throw CDB.exception(RDA_API_ERROR_PARAMETERS,
							_s("Invalid response code \"%1$s\".", statusCodeRange)
						);
					}
				}
			}
		}

		return true;
	}
	
	/**
	 * Check web scenario names.
	 *
	 * @param array httpTests
	 *
	 * @return array|null
	 */
	protected void checkNames(CArray<Map> httpTests) {
		CArray<String> httpTestsNames = rda_objectValues(httpTests, "name");
		if (!empty(httpTestsNames)) {
			if (empty(preg_grep("^["+RDA_PREG_PRINT+"]+$", httpTestsNames))) {
				throw CDB.exception(RDA_API_ERROR_PARAMETERS, _("Only characters are allowed."));
			}
		}
	}
	
	/**
	 * Check if user has read permissions on http test with given ids.
	 *
	 * @param array ids
	 *
	 * @return bool
	 */
	@Override
	public boolean isReadable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CHttpTestGet options = new CHttpTestGet();
		options.setHttptestIds(ids);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	/**
	 * Check if user has write permissions on http test with given ids.
	 *
	 * @param array ids
	 *
	 * @return bool
	 */
	@Override
	public boolean isWritable(Long... ids) {
		if (!isArray(ids)) {
			return false;
		}
		if (empty(ids)) {
			return true;
		}
		ids = array_unique(ids);
		CHttpTestGet options = new CHttpTestGet();
		options.setHttptestIds(ids);
		options.setEditable(true);
		options.setCountOutput(true);
		long count = get(options);
		return (count(ids) == count);
	}

	@Override
	protected void applyQueryOutputOptions(String tableName, String tableAlias, CParamGet params, SqlBuilder sqlParts) {
		super.applyQueryOutputOptions(tableName, tableAlias, params, sqlParts);
		
		if (params.getCountOutput()==null) {
			if(params instanceof CHttpTestGet) {
				CHttpTestGet hparams = (CHttpTestGet)params;
				// make sure we request the hostid to be able to expand macros
				if (hparams.getExpandName() != null || hparams.getExpandStepName() != null || hparams.getSelectHosts() != null) {
					this.addQuerySelect(this.fieldId("hostid"), sqlParts);
				}
			}
			// select the state field to be able to return the deprecated value_flag property
			if(this.outputIsRequested("macros", params.getOutput())) {
				this.addQuerySelect(this.fieldId("variables"), sqlParts);
			}
		}
	}

	@Override
	protected void addRelatedObjects(CHttpTestGet params, CArray<Map> result) {
		super.addRelatedObjects(params, result);
		Long[] httpTestIds = result.keysAsLong();

		// adding hosts
		if (!is_null(params.getSelectHosts()) && !API_OUTPUT_COUNT.equals(params.getSelectHosts())) {
			CRelationMap relationMap = this.createRelationMap(result, "httptestid", "hostid");
			
			CHostGet options = new CHostGet();
			options.setOutput(params.getSelectHosts());
			options.setHostIds(relationMap.getRelatedLongIds());
			options.setPreserveKeys(true);
			options.setTemplatedHosts(true);
			
			CArray<Map> datas = API.Host(this.idBean, this.getSqlExecutor()).get(options);
			relationMap.mapMany(result, datas, "hosts");
		}
		
		// adding steps
		if(params.getSelectSteps() != null) {
			if(!API_OUTPUT_COUNT.equals(params.getSelectSteps())) {
				CParamGet options = new CParamGet();
				options.setOutput(outputExtend("httpstep", new String[] {"httptestid", "httpstepid"}, params.getSelectSteps()));
				options.setFilter("httptestid", httpTestIds);
				options.setPreserveKeys(true);
				
				CArray<Map> httpSteps = this.select("httpstep", options);
				CRelationMap relationMap = this.createRelationMap(httpSteps, "httptestid", "httpstepid");
				
				// add the deprecated webstepid parameter if it's requested
				this.handleDeprecatedOutput(httpSteps, "webstepid", "httpstepid", params.getSelectSteps());
				
				this.unsetExtraFields(httpSteps, new String[] {"httptestid", "httpstepid"}, params.getSelectSteps());
				relationMap.mapMany(result, httpSteps, "steps");
			} else {
				SqlBuilder sqlParts = new SqlBuilder();
				sqlParts.select.put("hs.httptestid");
				sqlParts.select.put("COUNT(hs.httpstepid) AS stepscnt");
				sqlParts.from.put("httpstep hs");
				applyQueryTenantOptions("httpstep", "hs", params, sqlParts);
				sqlParts.where.dbConditionInt("hs.httptestid", httpTestIds);
				sqlParts.group.put("hs.httptestid");
				
				CArray<Map> dbHttpSteps = DBselect(getSqlExecutor(), sqlParts);
				for(Map dbHttpStep: dbHttpSteps) {
					Long id = (Long)dbHttpStep.get("httptestid");
					result.get(id).put("steps", dbHttpStep.get("stepscnt"));
				}
			}
		}
	}
}
