package com.isoft.iradar.managers;

import static com.isoft.biz.daoimpl.radar.CDB.delete;
import static com.isoft.biz.daoimpl.radar.CDB.insert;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBfetchArrayAssoc;
import static com.isoft.iradar.inc.DBUtil.DBfetchColumn;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_IN;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_LASTERROR;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_LASTSTEP;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_RSPCODE;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_TIME;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_DATA_TYPE_DECIMAL;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.ItemsUtil.quoteItemKeyParam;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.exception.APIException;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.model.CItemKey;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class CHttpTestManager {
	
	private IIdentityBean idBean;
	private SQLExecutor executor;
	
	private final static int ITEM_HISTORY = 30;
	private final static int ITEM_TRENDS = 90;
	
	/**
	 * Changed steps names.
	 * array(
	 *   testid1 => array(nameold1 => namenew1, nameold2 => namenew2),
	 *   ...
	 * )
	 *
	 * @var array
	 */
	protected CArray<Map> changedSteps = array();
	
	/**
	 * Map of parent http test id to child http test id.
	 *
	 * @var array
	 */
	protected CArray<Long> httpTestParents = array();
	
	private CHttpTestManager(IIdentityBean idBean, SQLExecutor executor){
		this.idBean = idBean;
		this.executor = executor;
	}
	
	/**
	 * Save http test to db.
	 *
	 * @param array httpTests
	 *
	 * @return array
	 */
	public CArray<Map> persist(CArray<Map> httpTests) {
		changedSteps = findChangedStepNames(httpTests);

		httpTests = save(httpTests);
		inherit(httpTests);

		return httpTests;
	}

	/**
	 * Find steps where name was changed.
	 *
	 * @return array
	 */
	protected CArray<Map> findChangedStepNames(CArray<Map> httpTests) {
		CArray httpSteps = array();
		CArray<Map> result = array();
		for(Map httpTest : httpTests) {
			if (isset(httpTest,"httptestid") && isset(httpTest,"steps")) {
				for(Map step : (CArray<Map>)Nest.value(httpTest,"steps").asCArray()) {
					if (isset(step,"httpstepid") && isset(step,"name")) {
						Nest.value(httpSteps,step.get("httpstepid")).$(Nest.value(step,"name").$());
					}
				}
			}
		}

		if (!empty(httpSteps)) {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray<Map> dbCursor = DBselect(
				executor,
				"SELECT hs.httpstepid,hs.httptestid,hs.name"+
				" FROM httpstep hs"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httpstep", "hs")+
				" AND "+sqlParts.dual.dbConditionInt("hs.httpstepid", array_keys(httpSteps).valuesAsLong()),
				sqlParts.getNamedParams()
			);
			for(Map dbStep : dbCursor) {
				if (!Nest.value(httpSteps,dbStep.get("httpstepid")).asString().equals(Nest.value(dbStep,"name").asString())) {
					Nest.value(result,dbStep.get("httptestid"),httpSteps.get(dbStep.get("httpstepid"))).$(Nest.value(dbStep,"name").$());
				}
			}
		}

		return result;
	}
	
	/**
	 * Create new http tests.
	 *
	 * @param array httpTests
	 *
	 * @return array
	 */
	public CArray<Map> create(CArray<Map> httpTests) {
		CArray<Long> httpTestIds = insert(this.idBean, this.executor, "httptest", Clone.deepcopy(httpTests));

		for (Entry<Object, Map> e : httpTests.entrySet()) {
		    Object hnum = e.getKey();
		    Map httpTest = e.getValue();
			Nest.value(httpTests,hnum,"httptestid").$(httpTestIds.get(hnum));

			Nest.value(httpTest,"httptestid").$(httpTestIds.get(hnum));
			createHttpTestItems(httpTest);
			createStepsReal(httpTest, Nest.value(httpTest,"steps").asCArray());
		}

		return httpTests;
	}

	/**
	 * Update http tests.
	 *
	 * @param array httpTests
	 *
	 * @return array
	 */
	public CArray<Map> update(CArray<Map> httpTests) {
		CArray httpTestIds = rda_objectValues(httpTests, "httptestid");
		CHttpTestGet htoptions = new CHttpTestGet();
		htoptions.setOutput(API_OUTPUT_EXTEND);
		htoptions.setHttptestIds(httpTestIds.valuesAsLong());
		htoptions.setSelectSteps(API_OUTPUT_EXTEND);
		htoptions.setEditable(true);
		htoptions.setPreserveKeys(true);
		CArray<Map> dbHttpTest = API.HttpTest(this.idBean, executor).get(htoptions);

		CArray deleteStepItemIds = array();

		SqlBuilder sqlParts = null;
		for(Map httpTest : httpTests) {
			CDB.update(idBean, executor, "httptest", (CArray)array(map(
				"values", httpTest,
				"where", map("httptestid", Nest.value(httpTest,"httptestid").$())
			)));

			CArray<Map> checkItemsUpdate = array();
			CArray itemids = array();
			sqlParts = new SqlBuilder();
			CArray<Map> dbCheckItems = DBselect(
				executor,
				"SELECT i.itemid,hi.type"+
				" FROM items i,httptestitem hi"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httptestitem", "hi")+
				    " AND hi.httptestid="+sqlParts.marshalParam(Nest.value(httpTest,"httptestid").$())+
					" AND hi.tenantid=i.tenantid"+
					" AND hi.itemid=i.itemid",
				sqlParts.getNamedParams()
			);
			for(Map checkitem : dbCheckItems) {
				itemids.add(Nest.value(checkitem,"itemid").$());
				
				Map updateFields = array();
				if (isset(httpTest,"name")) {
					Nest.value(updateFields,"key_").$(getTestKey(Nest.value(checkitem,"type").asInteger(), Nest.value(httpTest,"name").asString()));
				}

				if (isset(httpTest,"status")) {
					Nest.value(updateFields,"status").$((HTTPTEST_STATUS_ACTIVE == Nest.value(httpTest,"status").asInteger()) ? ITEM_STATUS_ACTIVE : ITEM_STATUS_DISABLED);
				}
				if (isset(httpTest,"delay")) {
					Nest.value(updateFields,"delay").$(Nest.value(httpTest,"delay").$());
				}
				if (!empty(updateFields)) {
					checkItemsUpdate.add(map(
						"values", updateFields,
						"where", map("itemid", Nest.value(checkitem,"itemid").$())
					));
				}
			}
			CDB.update(idBean, executor, "items", checkItemsUpdate);

			if (isset(httpTest,"applicationid")) {
				updateItemsApplications(itemids, Nest.value(httpTest,"applicationid").asLong());
			}

			// update steps
			if (isset(httpTest,"steps")) {
				CArray stepsCreate = array();
				CArray stepsUpdate = array();
				CArray dbSteps = rda_toHash(Nest.value(dbHttpTest,httpTest.get("httptestid"),"steps").asCArray(), "httpstepid");
				for(Map webstep : (CArray<Map>)Nest.value(httpTest,"steps").asCArray()) {
					if (isset(webstep,"httpstepid") && isset(dbSteps,webstep.get("httpstepid"))) {
						stepsUpdate.add(webstep);
						unset(dbSteps,webstep.get("httpstepid"));
					} else if (!isset(webstep,"httpstepid")) {
						stepsCreate.add(webstep);
					}
				}
				CArray stepidsDelete = array_keys(dbSteps);

				if (!empty(stepidsDelete)) {
					sqlParts = new SqlBuilder();
					CArray<Map> result = DBselect(
						executor,
						"SELECT hi.itemid FROM httpstepitem hi"+
						" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httpstepitem", "hi")+
						" AND "+sqlParts.dual.dbConditionInt("hi.httpstepid", stepidsDelete.valuesAsLong()),
						sqlParts.getNamedParams()
					);

					for(Object itemId : DBfetchColumn(result, "itemid")) {
						deleteStepItemIds.add(itemId);
					}

					delete(idBean, executor, "httpstep", (Map)map("httpstepid", stepidsDelete.valuesAsLong()));
				}
				if (!empty(stepsUpdate)) {
					updateStepsReal(httpTest, stepsUpdate);
				}
				if (!empty(stepsCreate)) {
					createStepsReal(httpTest, stepsCreate);
				}
			} else {
				if (isset(httpTest,"applicationid")) {
					sqlParts = new SqlBuilder();
					CArray dbStepIds = DBfetchColumn(DBselect(
						executor,
						"SELECT i.itemid"+
						" FROM items i"+
							" INNER JOIN httpstepitem hi ON hi.tenantid=i.tenantid AND hi.itemid=i.itemid"+
						" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "items", "i")+
						" AND "+sqlParts.dual.dbConditionInt("hi.httpstepid", rda_objectValues(Nest.value(dbHttpTest,httpTest.get("httptestid"),"steps").asCArray(), "httpstepid").valuesAsLong()),
						sqlParts.getNamedParams())
						, "itemid"
					);
					updateItemsApplications(dbStepIds, Nest.value(httpTest,"applicationid").asLong());
				}

				if (isset(httpTest,"status")) {
					int status = (Nest.value(httpTest,"status").asInteger() == HTTPTEST_STATUS_ACTIVE) ? ITEM_STATUS_ACTIVE : ITEM_STATUS_DISABLED;
					sqlParts = new SqlBuilder();
					CArray itemIds = DBfetchColumn(DBselect(
						executor,
						"SELECT hsi.itemid"+
							" FROM httpstep hs,httpstepitem hsi"+
							" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httpstep", "hs")+
								" AND hs.tenantid=hsi.tenantid"+
								" AND hs.httpstepid=hsi.httpstepid"+
								" AND hs.httptestid="+sqlParts.marshalParam(Nest.value(httpTest,"httptestid").$()),
						sqlParts.getNamedParams()
					), "itemid");

					CDB.update(idBean, executor, "items", array((Map)map(
						"values", map("status", status),
						"where", map("itemid", itemIds.valuesAsLong())
					)));
				}
			}
		}

		if (!empty(deleteStepItemIds)) {
			API.Item(this.idBean, executor).delete(true,deleteStepItemIds.valuesAsLong());
		}

		return httpTests;
	}

	/**
	 * Link http tests in template to hosts.
	 *
	 * @param templateId
	 * @param hostIds
	 */
	public void link(Long templateId, Long... hostIds) {
		CArray httpTests = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbCursor = DBselect(
			executor,
			"SELECT ht.httptestid,ht.name,ht.applicationid,ht.delay,ht.status,ht.variables,ht.agent,"+
				"ht.authentication,ht.http_user,ht.http_password,ht.hostid,ht.templateid,ht.http_proxy,ht.retries"+
			" FROM httptest ht"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httptest", "ht")+
			" AND ht.hostid="+sqlParts.marshalParam(templateId),
			sqlParts.getNamedParams()
		);
		for(Map dbHttpTest : dbCursor) {
			Nest.value(httpTests,dbHttpTest.get("httptestid")).$(dbHttpTest);
		}

		sqlParts = new SqlBuilder();
		dbCursor = DBselect(
			executor,
			"SELECT hs.httpstepid,hs.httptestid,hs.name,hs.no,hs.url,hs.timeout,hs.posts,hs.variables,hs.required,hs.status_codes"+
			" FROM httpstep hs"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httpstep", "hs")+
			" AND "+sqlParts.dual.dbConditionInt("hs.httptestid", array_keys(httpTests).valuesAsLong()),
			sqlParts.getNamedParams()
		);
		for(Map dbHttpStep : dbCursor) {
			if(!isset(Nest.value(httpTests,dbHttpStep.get("httptestid"),"steps").$())){
				Nest.value(httpTests,dbHttpStep.get("httptestid"),"steps").$(new CArray());
			}
			Nest.value(httpTests,dbHttpStep.get("httptestid"),"steps").asCArray().add(dbHttpStep);
		}

		inherit(httpTests, CArray.valueOf(hostIds));
	}
	
	/**
	 * Inherit passed http tests to hosts.
	 * If hostIds is empty that means that we need to inherit all httpTests to hosts which are linked to templates
	 * where httpTests belong.
	 *	 *
	 * @param array httpTests
	 * @param array hostIds
	 *
	 * @return bool
	 */
	public boolean inherit(CArray<Map> httpTests) {
		return inherit(httpTests, array());
	}
	public boolean inherit(CArray<Map> httpTests, CArray hostIds) {
		CArray<Map> hostsTemplatesMap = getChildHostsFromHttpTests(httpTests, hostIds);
		if (empty(hostsTemplatesMap)) {
			return true;
		}

		CArray<Map> preparedHttpTests = prepareInheritedHttpTests(httpTests, hostsTemplatesMap);
		CArray<Map> inheritedHttpTests = save(preparedHttpTests);
		inherit(inheritedHttpTests);

		return true;
	}
	
	/**
	 * Get array with hosts that are linked with templates which passed http tests belong to as key and templateid that host
	 * is linked to as value.
	 * If second parameter hostIds is not empty, result should contain only passed host ids.
	 *
	 * @param array httpTests
	 * @param array hostIds
	 *
	 * @return array
	 */
	protected CArray getChildHostsFromHttpTests(CArray<Map> httpTests) {
		return getChildHostsFromHttpTests(httpTests, array());
	}
	
	protected CArray getChildHostsFromHttpTests(CArray<Map> httpTests, CArray hostIds) {
		CArray hostsTemplatesMap = array();

		SqlBuilder sqlParts = new SqlBuilder();
		String sqlWhere = !empty(hostIds) ? " AND "+sqlParts.dual.dbConditionInt("ht.hostid", hostIds.valuesAsLong()) : "";
		CArray<Map> dbCursor = DBselect(
			executor,
			"SELECT ht.templateid,ht.hostid"+
			" FROM hosts_templates ht"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts_templates", "ht")+
			" AND "+sqlParts.dual.dbConditionInt("ht.templateid", rda_objectValues(httpTests, "hostid").valuesAsLong())+
				sqlWhere,
			sqlParts.getNamedParams()
		);
		for(Map dbHost : dbCursor) {
			Nest.value(hostsTemplatesMap,dbHost.get("hostid")).$(Nest.value(dbHost,"templateid").$());
		}

		return hostsTemplatesMap;
	}
	
	/**
	 * Generate http tests data for inheritance.
	 * Using passed parameters decide if new http tests must be created on host or existing ones must be updated.
	 *
	 * @param array httpTests which we need to inherit
	 * @param array hostsTemplatesMap
	 *
	 * @throws Exception
	 * @return array with http tests, existing apps have "httptestid" key.
	 */
	protected CArray<Map> prepareInheritedHttpTests(CArray<Map> httpTests, CArray<Map> hostsTemplatesMap) {
		CArray<Map> hostHttpTests = getHttpTestsMapsByHostIds(array_keys(hostsTemplatesMap));

		CArray<Map> result = array();
		SqlBuilder sqlParts = null;
		for(Map httpTest : httpTests) {
			Long httpTestId = Nest.value(httpTest,"httptestid").asLong();
			for (Entry<Object, Map> e : hostHttpTests.entrySet()) {
				Long hostId = Nest.as(e.getKey()).asLong();
			    Map hostHttpTest = e.getValue();
				// if http test template is not linked to host we skip it
				if (Nest.value(hostsTemplatesMap,hostId).asLong() != Nest.value(httpTest,"hostid").asLong()) {
					continue;
				}

				Map exHttpTest = null;
				// update by templateid
				if (isset(Nest.value(hostHttpTest,"byTemplateId",httpTestId).$())) {
					exHttpTest = (Map)Nest.value(hostHttpTest,"byTemplateId",httpTestId).$();

					// need to check templateid here too in case we update linked http test to name that already exists on linked host
					if (isset(httpTest,"name") && isset(Nest.value(hostHttpTest,"byName",httpTest.get("name")).$())
							&& !idcmp(Nest.value(exHttpTest,"templateid").$(), Nest.value(hostHttpTest,"byName",httpTest.get("name"),"templateid").$())) {
						sqlParts = new SqlBuilder();
						Map host = DBfetch(DBselect(executor,
								"SELECT h.name FROM hosts h"+
								" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
								" AND h.hostid="+sqlParts.marshalParam(hostId),
								sqlParts.getNamedParams()));
						throw new APIException(_s("Web scenario \"%1$s\" already exists on host \"%2$s\".", Nest.value(exHttpTest,"name").$(), Nest.value(host,"name").$()));
					}
				}
				// update by name
				else if (isset(Nest.value(hostHttpTest,"byName",httpTest.get("name")).$())) {
					exHttpTest = (Map)Nest.value(hostHttpTest,"byName",httpTest.get("name")).$s();
					if (Nest.value(exHttpTest,"templateid").asLong() > 0 || !compareHttpSteps(httpTest, exHttpTest)) {
						sqlParts = new SqlBuilder();
						Map host = DBfetch(DBselect(executor,
								"SELECT h.name FROM hosts h"+
								" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
								" AND h.hostid="+sqlParts.marshalParam(hostId),
								sqlParts.getNamedParams()));
						throw new APIException(_s("Web scenario \"%1$s\" already exists on host \"%2$s\".", Nest.value(exHttpTest,"name").$(), Nest.value(host,"name").$()));
					}

					createLinkageBetweenHttpTests(httpTestId, Nest.value(exHttpTest,"httptestid").asLong());
					continue;
				}

				Map newHttpTest = Clone.deepcopy(httpTest);
				Nest.value(newHttpTest,"hostid").$(hostId);
				Nest.value(newHttpTest,"templateid").$(httpTestId);
				if (!empty(exHttpTest)) {
					Nest.value(newHttpTest,"httptestid").$(Nest.value(exHttpTest,"httptestid").$());

					setHttpTestParent(Nest.value(exHttpTest,"httptestid").asLong(), httpTestId);

					if (isset(newHttpTest,"steps")) {
						Nest.value(newHttpTest,"steps").$(prepareHttpSteps(Nest.value(httpTest,"steps").asCArray(), Nest.value(exHttpTest,"httptestid").asLong()));
					}
				} else {
					unset(newHttpTest,"httptestid");
				}

				if (!empty(Nest.value(newHttpTest,"applicationid").$())) {
					Nest.value(newHttpTest,"applicationid").$(findChildApplication(Nest.value(newHttpTest,"applicationid").asLong(), hostId));
				}

				result.add(newHttpTest);
			}
		}

		return result;
	}
	
	/**
	 * Create linkage between two http tests.
	 * If we found existing http test by name and steps, we only add linkage, i.e. change templateid
	 *
	 * @param parentId
	 * @param childId
	 */
	protected void createLinkageBetweenHttpTests(Long parentId, Long childId) {
		CDB cdb = new CDB(this.idBean, this.executor);
		cdb.update("httptest", array((Map)map(
			"values", map("templateid", parentId),
			"where", map("httptestid", childId)
		)));

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbCursor = DBselect(
			executor,
			"SELECT i1.itemid AS parentid,i2.itemid AS childid"+
			" FROM httptestitem hti1,httptestitem hti2,items i1,items i2"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httptestitem", "hti1")+
			     " AND hti1.httptestid="+sqlParts.marshalParam(parentId)+
				" AND hti2.httptestid="+sqlParts.marshalParam(childId)+
				" AND hti1.tenantid=i1.tenantid"+
				" AND hti1.itemid=i1.itemid"+
				" AND hti2.tenantid=i2.tenantid"+
				" AND hti2.itemid=i2.itemid"+
				" AND i1.key_=i2.key_",
			sqlParts.getNamedParams()
		);
		for(Map dbItems : dbCursor) {
			cdb.update("items", array((Map)map(
				"values", map("templateid", Nest.value(dbItems,"parentid").$()),
				"where", map("itemid", Nest.value(dbItems,"childid").$())
			)));
		}

		sqlParts = new SqlBuilder();
		dbCursor = DBselect(
			executor,
			"SELECT i1.itemid AS parentid,i2.itemid AS childid"+
			" FROM httpstepitem hsi1,httpstepitem hsi2,httpstep hs1,httpstep hs2,items i1,items i2"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httpstep", "hs1")+
			     " AND hs1.httptestid="+sqlParts.marshalParam(parentId)+
				" AND hs2.httptestid="+sqlParts.marshalParam(childId)+
				" AND hsi1.tenantid=i1.tenantid"+
				" AND hsi1.itemid=i1.itemid"+
				" AND hsi2.tenantid=i2.tenantid"+
				" AND hsi2.itemid=i2.itemid"+
				" AND hs1.tenantid=hsi1.tenantid"+
				" AND hs1.httpstepid=hsi1.httpstepid"+
				" AND hs2.tenantid=hsi2.tenantid"+
				" AND hs2.httpstepid=hsi2.httpstepid"+
				" AND i1.tenantid=i2.tenantid"+
				" AND i1.key_=i2.key_",
			sqlParts.getNamedParams()
		);
		for(Map dbItems : dbCursor) {
			cdb.update("items", array((Map)map(
				"values", map("templateid", Nest.value(dbItems,"parentid").$()),
				"where", map("itemid", Nest.value(dbItems,"childid").$())
			)));
		}
	}
	
	/**
	 * Find application with same name on given host.
	 *
	 * @param parentAppId
	 * @param childHostId
	 *
	 * @return string
	 */
	protected Long findChildApplication(Long parentAppId, Long childHostId) {
		Map params = new HashMap();
		params.put("applicationid", childHostId);
		params.put("hostid", parentAppId);
		Map childAppId = DBfetch(DBselect(
			executor,
			"SELECT a2.applicationid"+
			" FROM applications a1"+
				" INNER JOIN applications a2 ON a1.name=a2.name"+
			" WHERE a1.applicationid=#{applicationid}"+
				" AND a2.hostid=#{hostid}",
			params
		));

		return Nest.value(childAppId,"applicationid").asLong();
	}
	
	/**
	 * Find and set first parent id for http test.
	 *
	 * @param id
	 * @param parentId
	 */
	protected void setHttpTestParent(Long id, Long parentId) {
		while (isset(httpTestParents,parentId)) {
			parentId = Nest.value(httpTestParents,parentId).asLong();
		}
		Nest.value(httpTestParents,id).$(parentId);
	}
	
	/**
	 * Get hosts http tests for each passed hosts.
	 * Each host has two hashes with http tests, one with name keys other with templateid keys.
	 *
	 * Resulting structure is:
	 * array(
	 *     "hostid1" => array(
	 *         "byName" => array(ht1data, ht2data, ...),
	 *         "nyTemplateId" => array(ht1data, ht2data, ...)
	 *     ), ...
	 * );
	 *
	 * @param array hostIds
	 *
	 * @return array
	 */
	protected CArray<Map> getHttpTestsMapsByHostIds(CArray hostIds) {
		CArray<Map> hostHttpTests = array();
		for(Object hostid : hostIds) {
			Nest.value(hostHttpTests,hostid).$(map("byName", array(), "byTemplateId", array()));
		}

		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbCursor = DBselect(
			executor,
			"SELECT ht.httptestid,ht.name,ht.hostid,ht.templateid"+
			" FROM httptest ht"+
			" WHERE "+sqlParts.dual.dbConditionInt("ht.hostid", hostIds.valuesAsLong()),
			sqlParts.getNamedParams()
		);
		for(Map dbHttpTest : dbCursor) {
			Nest.value(hostHttpTests,dbHttpTest.get("hostid"),"byName",dbHttpTest.get("name")).$(dbHttpTest);
			if (!empty(Nest.value(dbHttpTest,"templateid").$())) {
				Nest.value(hostHttpTests,dbHttpTest.get("hostid"),"byTemplateId",dbHttpTest.get("templateid")).$(dbHttpTest);
			}
		}
		return hostHttpTests;
	}
	
	/**
	 * Compare steps for http tests.
	 *
	 * @param array httpTest steps must be included under "steps"
	 * @param array exHttpTest
	 *
	 * @return bool
	 */
	protected boolean compareHttpSteps(Map httpTest, Map exHttpTest) {
		String firstHash = "";
		String secondHash = "";

		CArrayHelper.sort(Nest.value(httpTest,"steps").asCArray(), array("no"));
		for(Map step : (CArray<Map>)Nest.value(httpTest,"steps").asCArray()) {
			firstHash += Nest.value(step,"no").asString()+Nest.value(step,"name").asString();
		}

		Map params = new HashMap();
		params.put("httptestid", Nest.value(exHttpTest,"httptestid").$());
		CArray<Map> dbHttpTestSteps = DBselect(
			executor,
			"SELECT hs.name,hs.no"+
			" FROM httpstep hs"+
			" WHERE hs.httptestid=#{httptestid}",
			params
		);
		
		CArrayHelper.sort(dbHttpTestSteps, array("no"));
		for(Map dbHttpStep : dbHttpTestSteps) {
			secondHash += Nest.value(dbHttpStep,"no").asString()+Nest.value(dbHttpStep,"name").asString();
		}

		return firstHash.equals(secondHash);
	}
	
	/**
	 * Save http tests. If http test has httptestid it gets updated otherwise a new one is created.
	 *
	 * @param array httpTests
	 *
	 * @return array
	 */
	protected CArray<Map> save(CArray<Map> httpTests) {
		CArray<Map> httpTestsCreate = array();
		CArray<Map> httpTestsUpdate = array();

		for(Map httpTest : httpTests) {
			if (isset(httpTest,"httptestid")) {
				httpTestsUpdate.add(httpTest);
			} else {
				httpTestsCreate.add(httpTest);
			}
		}

		if (!empty(httpTestsCreate)) {
			CArray<Map> newHttpTests = create(httpTestsCreate);
			for (Entry<Object, Map> e : newHttpTests.entrySet()) {
			    Object num = e.getKey();
			    Map newHttpTest = e.getValue();
				Nest.value(httpTests,num,"httptestid").$(Nest.value(newHttpTest,"httptestid").$());
			}
		}
		if (!empty(httpTestsUpdate)) {
			update(httpTestsUpdate);
		}

		return httpTests;
	}
	
	/**
	 * @param array steps
	 * @param exHttpTestId
	 *
	 * @return array
	 */
	protected CArray<Map> prepareHttpSteps(CArray<Map> steps, Long exHttpTestId) {
		Map params = new HashMap();
		params.put("httptestid", exHttpTestId);
		CArray exSteps = array();
		CArray<Map> dbCursor = DBselect(
			executor,
			"SELECT hs.httpstepid,hs.name"+
			" FROM httpstep hs"+
			" WHERE hs.httptestid=#{httptestid}",
			params
		);
		for(Map dbHttpStep : dbCursor) {
			Nest.value(exSteps,dbHttpStep.get("name")).$(Nest.value(dbHttpStep,"httpstepid").$());
		}

		CArray<Map> result = array();
		Object stepName = null;
		for(Map step : steps) {
			Long parentTestId = httpTestParents.get(exHttpTestId);
			if (isset(Nest.value(changedSteps,parentTestId,step.get("name")).$())) {
				stepName = Nest.value(changedSteps,parentTestId,step.get("name")).$();
			} else {
				stepName = Nest.value(step,"name").$();
			}

			if (isset(exSteps,stepName)) {
				Nest.value(step,"httpstepid").$(exSteps.get(stepName));
				Nest.value(step,"httptestid").$(exHttpTestId);
			}

			result.add(step);
		}

		return result;
	}
	
	/**
	 * Create items required for web scenario.
	 *
	 * @param array httpTest
	 *
	 * @throws Exception
	 */
	protected void createHttpTestItems(Map httpTest) {
		CArray<Map> checkitems = array(
			(Map)map(
				"name"				, Cphp._("Download speed for scenario \"$1\"."),
				"key_"				, getTestKey(HTTPSTEP_ITEM_TYPE_IN, Nest.value(httpTest,"name").asString()),
				"value_type"		, ITEM_VALUE_TYPE_FLOAT,
				"units"				, "Bps",
				"httptestitemtype"	, HTTPSTEP_ITEM_TYPE_IN
			),
			(Map)map(
				"name"				, Cphp._("Failed step of scenario \"$1\"."),
				"key_"				, getTestKey(HTTPSTEP_ITEM_TYPE_LASTSTEP, Nest.value(httpTest,"name").asString()),
				"value_type"		, ITEM_VALUE_TYPE_UINT64,
				"units"				, "",
				"httptestitemtype"	, HTTPSTEP_ITEM_TYPE_LASTSTEP
			),
			(Map)map(
				"name"				, Cphp._("Last error message of scenario \"$1\"."),
				"key_"				, getTestKey(HTTPSTEP_ITEM_TYPE_LASTERROR, Nest.value(httpTest,"name").asString()),
				"value_type"		, ITEM_VALUE_TYPE_STR,
				"units"				, "",
				"httptestitemtype"	, HTTPSTEP_ITEM_TYPE_LASTERROR
			)
		);

		Map params = new HashMap();
		params.put("httptestid", Nest.value(httpTest,"templateid").$());
		// if this is a template scenario, fetch the parent http items to link inherited items to them
		CArray parentItems = array();
		if (isset(httpTest,"templateid") && !empty(Nest.value(httpTest,"templateid").$())) {
			parentItems = DBfetchArrayAssoc(DBselect(
				executor,
				"SELECT i.itemid,i.key_"+
					" FROM items i,httptestitem hti"+
					" WHERE i.itemid=hti.itemid"+
					" AND hti.httptestid=#{httptestid}",
				params
			), "key_");
		}

		CArray<Map> insertItems = array();
		CArray<Map> updateItems = array();
		CArray testItemIds = array();
		for(Map item : checkitems) {
			params.put("key_", Nest.value(item,"key_").$());
			params.put("hostid", Nest.value(httpTest,"hostid").$());
			Map dbItem = DBfetch(DBselect(
				executor,
				"SELECT i.itemid,i.templateid"+
				" FROM items i"+
				" WHERE i.key_=#{key_}"+
					" AND i.hostid=#{hostid}",
				params
			));

			Nest.value(item,"data_type").$(ITEM_DATA_TYPE_DECIMAL);
			Nest.value(item,"hostid").$(Nest.value(httpTest,"hostid").$());
			Nest.value(item,"delay").$(Nest.value(httpTest,"delay").$());
			Nest.value(item,"type").$(ITEM_TYPE_HTTPTEST);
			Nest.value(item,"history").$(ITEM_HISTORY);
			Nest.value(item,"trends").$(ITEM_TRENDS);
			Nest.value(item,"status").$((HTTPTEST_STATUS_ACTIVE == Nest.value(httpTest,"status").asInteger()) ? ITEM_STATUS_ACTIVE : ITEM_STATUS_DISABLED);

			if (isset(parentItems,item.get("key_"))) {
				Nest.value(item,"templateid").$(Nest.value(parentItems,item.get("key_"),"itemid").$());
			}

			if (!empty(dbItem)) {
				if (!empty(Nest.value(dbItem,"templateid").$())) {
					throw new APIException(_s("Item with key \"%1$s\" already exists.", Nest.value(item,"key_").$()));
				}
				testItemIds.add(Nest.value(dbItem,"itemid").$());
				updateItems.add(map("values", item, "where", map("itemid", Nest.value(dbItem,"itemid").$())));
			} else {
				insertItems.add(item);
			}
		}

		CDB cdb = new CDB(this.idBean, this.executor);
		if (!empty(insertItems)) {
			CArray<Long> newTestItemIds = cdb.insert("items", Clone.deepcopy(insertItems));
			testItemIds = array_merge(testItemIds, newTestItemIds);
		}
		if (!empty(updateItems)) {
			cdb.update("items", Clone.deepcopy(updateItems));
		}

		CArray<Map> itemApplications = array();
		for(Object itemid : testItemIds) {
			if (!empty(Nest.value(httpTest,"applicationid").$())) {
				itemApplications.add(map(
					"applicationid", Nest.value(httpTest,"applicationid").$(),
					"itemid", itemid
				));
			}
		}
		if (!empty(itemApplications)) {
			cdb.insert("items_applications", itemApplications);
		}


		CArray<Map> httpTestItems = array();
		for (Entry<Object, Map> e : checkitems.entrySet()) {
		    Object inum = e.getKey();
		    Map item = e.getValue();
			httpTestItems.add(map(
				"httptestid", Nest.value(httpTest,"httptestid").$(),
				"itemid", testItemIds.get(inum),
				"type", item.get("httptestitemtype")
			));
		}
		cdb.insert("httptestitem", httpTestItems);
	}
	
	/**
	 * Create web scenario steps with items.
	 *
	 * @param httpTest
	 * @param websteps
	 *
	 * @throws Exception
	 */
	protected void createStepsReal(Map httpTest, CArray<Map> websteps) {
		for (Entry<Object, Map> e : websteps.entrySet()) {
		    Object snum = e.getKey();
		    //Map webstep = e.getValue();
			Nest.value(websteps,snum,"httptestid").$(Nest.value(httpTest,"httptestid").$());
		}
		Map params = new HashMap();
		CArray<Long> webstepids = CDB.insert(this.idBean, this.executor, "httpstep", websteps);

		// if this is a template scenario, fetch the parent http items to link inherited items to them
		CArray parentStepItems = array();
		if (isset(httpTest,"templateid") && !empty(Nest.value(httpTest,"templateid").$())) {
			params.put("httptestid", Nest.value(httpTest,"templateid").$());
			parentStepItems = DBfetchArrayAssoc(DBselect(
				executor,
				"SELECT i.itemid,i.key_,hsi.httpstepid"+
				" FROM items i,httpstepitem hsi,httpstep hs"+
				" WHERE i.itemid=hsi.itemid"+
					" AND hsi.httpstepid=hs.httpstepid"+
					" AND hs.httptestid=#{httptestid}",
				params
			), "key_");
		}

		for (Entry<Object, Map> e : websteps.entrySet()) {
		    Object snum = e.getKey();
		    Map webstep = e.getValue();
			Object webstepid = Nest.value(webstepids,snum).$();

			CArray<Map> stepitems = array(
				(Map)map(
					"name" , Cphp._("Download speed for step \"$2\" of scenario \"$1\"."),
					"key_" , getStepKey(HTTPSTEP_ITEM_TYPE_IN, Nest.value(httpTest,"name").asString(), Nest.value(webstep,"name").asString()),
					"value_type" , ITEM_VALUE_TYPE_FLOAT,
					"units" , "Bps",
					"httpstepitemtype" , HTTPSTEP_ITEM_TYPE_IN
				),
				(Map)map(
					"name" , Cphp._("Response time for step \"$2\" of scenario \"$1\"."),
					"key_" , getStepKey(HTTPSTEP_ITEM_TYPE_TIME, Nest.value(httpTest,"name").asString(), Nest.value(webstep,"name").asString()),
					"value_type" , ITEM_VALUE_TYPE_FLOAT,
					"units" , "s",
					"httpstepitemtype" , HTTPSTEP_ITEM_TYPE_TIME
				),
				(Map)map(
					"name" , Cphp._("Response code for step \"$2\" of scenario \"$1\"."),
					"key_" , getStepKey(HTTPSTEP_ITEM_TYPE_RSPCODE, Nest.value(httpTest,"name").asString(), Nest.value(webstep,"name").asString()),
					"value_type" , ITEM_VALUE_TYPE_UINT64,
					"units" , "",
					"httpstepitemtype" , HTTPSTEP_ITEM_TYPE_RSPCODE
				)
			);

			long delay;
			int status;
			if (!isset(httpTest,"delay") || !isset(httpTest,"status")) {
				params.put("httptestid", Nest.value(httpTest,"httptestid").$());
				Map dbTest = DBfetch(DBselect(executor,"SELECT ht.delay,ht.status FROM httptest ht WHERE ht.httptestid=#{httptestid}",params));
				delay = Nest.value(dbTest,"delay").asLong();
				status = Nest.value(dbTest,"status").asInteger();
			} else {
				delay = Nest.value(httpTest,"delay").asLong();
				status = Nest.value(httpTest,"status").asInteger();
			}

			CArray<Map> insertItems = array();
			CArray<Map> updateItems = array();
			CArray stepItemids = array();
			for(Map item : stepitems) {
				params.put("key_", Nest.value(item,"key_").$());
				params.put("hostid", Nest.value(httpTest,"hostid").$());
				Map dbItem = DBfetch(DBselect(
					executor,
					"SELECT i.itemid,i.templateid"+
					" FROM items i"+
					" WHERE i.key_=#{key_}"+
						" AND i.hostid=#{hostid}",
					params
				));

				Nest.value(item,"hostid").$(Nest.value(httpTest,"hostid").$());
				Nest.value(item,"delay").$(delay);
				Nest.value(item,"type").$(ITEM_TYPE_HTTPTEST);
				Nest.value(item,"data_type").$(ITEM_DATA_TYPE_DECIMAL);
				Nest.value(item,"history").$(ITEM_HISTORY);
				Nest.value(item,"trends").$(ITEM_TRENDS);
				Nest.value(item,"status").$((HTTPTEST_STATUS_ACTIVE == status) ? ITEM_STATUS_ACTIVE : ITEM_STATUS_DISABLED);

				if (isset(parentStepItems,item.get("key_"))) {
					Nest.value(item,"templateid").$(Nest.value(parentStepItems,item.get("key_"),"itemid").$());
				}

				if (!empty(dbItem)) {
					if (!empty(Nest.value(dbItem,"templateid").$())) {
						throw new APIException(_s("Item with key \"%1$s\" already exists.", Nest.value(item,"key_").$()));
					}
					stepItemids.add(Nest.value(dbItem,"itemid").$());
					updateItems.add(map("values", item, "where", map("itemid", Nest.value(dbItem,"itemid").$())));
				} else {
					insertItems.add(item);
				}
			}

			if (!empty(insertItems)) {
				CArray<Long> newStepItemIds = insert(this.idBean, this.executor,"items", insertItems);
				stepItemids = array_merge(stepItemids, newStepItemIds);
			}
			if (!empty(updateItems)) {
				CDB.update(idBean, executor, "items", updateItems);
			}

			CArray<Map> itemApplications = array();
			for(Object itemid : stepItemids) {
				if (!empty(Nest.value(httpTest,"applicationid").$())) {
					itemApplications.add(map(
						"applicationid", Nest.value(httpTest,"applicationid").$(),
						"itemid", itemid
					));
				}
			}
			if (!empty(itemApplications)) {
				insert(this.idBean, this.executor, "items_applications", itemApplications);
			}

			CArray<Map> webstepitems = array();
			for (Entry<Object, Map> ee : stepitems.entrySet()) {
			    Object inum = ee.getKey();
			    Map item = ee.getValue();
				webstepitems.add(map(
					"httpstepid", webstepid,
					"itemid", stepItemids.get(inum),
					"type", item.get("httpstepitemtype")
				));
			}
			insert(this.idBean, this.executor, "httpstepitem", webstepitems);
		}
	}
	
	/**
	 * Update web scenario steps.
	 *
	 * @param httpTest
	 * @param websteps
	 *
	 * @throws Exception
	 */
	protected void updateStepsReal(Map httpTest, CArray<Map> websteps) {
		SqlBuilder sqlParts = new SqlBuilder();
		// get all used keys
		CArray webstepids = rda_objectValues(websteps, "httpstepid");
		CArray<Map> dbKeys = DBfetchArrayAssoc(DBselect(
			executor,
			"SELECT i.key_"+
			" FROM items i,httpstepitem hi"+
			" WHERE "+sqlParts.dual.dbConditionInt("hi.httpstepid", webstepids.valuesAsLong())+
				" AND hi.itemid=i.itemid",sqlParts.getNamedParams())
			, "key_"
		);

		Map sqlParams = new HashMap();
		CDB cdb = new CDB(this.idBean, this.executor);
		for(Map webstep : websteps) {
			cdb.update("httpstep", array((Map)map(
				"values", webstep,
				"where", map("httpstepid", Nest.value(webstep,"httpstepid").$())
			)));

			// update item keys
			CArray itemids = array();
			CArray<Map> stepitemsUpdate = array();
			sqlParams.put("httpstepid", Nest.value(webstep,"httpstepid").$());
			CArray<Map> dbStepItems = DBselect(
				executor,
				"SELECT i.itemid,i.key_,hi.type"+
				" FROM items i,httpstepitem hi"+
				" WHERE hi.httpstepid=#{httpstepid}"+
					" AND hi.itemid=i.itemid",
				sqlParams
			);
			for(Map stepitem : dbStepItems) {
				itemids.add(Nest.value(stepitem,"itemid").$());

				CArray updateFields = array();
				
				if (isset(httpTest,"name") || isset(webstep,"name")) {
					if (!isset(httpTest,"name") || !isset(webstep,"name")) {
						CItemKey key = new CItemKey(Nest.value(stepitem,"key_").asString());
						CArray<String> params = key.getParameters();
						if (!isset(httpTest,"name")) {
							Nest.value(httpTest,"name").$(params.get(0));
						}
						if (!isset(webstep,"name")) {
							Nest.value(webstep,"name").$(params.get(1));
						}
					}

					Nest.value(updateFields,"key_").$(getStepKey(Nest.value(stepitem,"type").asInteger(), Nest.value(httpTest,"name").asString(), Nest.value(webstep,"name").asString()));
				}
				if (isset(dbKeys,updateFields.get("key_"))) {
					unset(updateFields,"key_");
				}
				if (isset(httpTest,"status")) {
					Nest.value(updateFields,"status").$((HTTPTEST_STATUS_ACTIVE == Nest.value(httpTest,"status").asInteger()) ? ITEM_STATUS_ACTIVE : ITEM_STATUS_DISABLED);
				}
				if (isset(httpTest,"delay")) {
					Nest.value(updateFields,"delay").$(Nest.value(httpTest,"delay").$());
				}
				if (!empty(updateFields)) {
					stepitemsUpdate.add(map(
						"values", updateFields,
						"where", map("itemid", Nest.value(stepitem,"itemid").$())
					));
				}
			}
			cdb.update("items", stepitemsUpdate);

			if (isset(httpTest,"applicationid")) {
				updateItemsApplications(itemids, Nest.value(httpTest,"applicationid").asLong());
			}
		}
	}
	
	/**
	 * Update web item application linkage.
	 *
	 * @param array  itemIds
	 * @param string appId
	 */
	protected void updateItemsApplications(CArray itemIds, Long appId) {
		CDB cdb = new CDB(this.idBean, this.executor);
		if (empty(appId)) {
			cdb.delete("items_applications", (CArray)map("itemid", itemIds.valuesAsLong()));
		} else {
			SqlBuilder sqlParts = new SqlBuilder();
			CArray linkedItemIds = DBfetchColumn(
				DBselect(
						executor,
						"SELECT ia.itemid FROM items_applications ia WHERE "+sqlParts.dual.dbConditionInt("ia.itemid", itemIds.valuesAsLong()),
						sqlParts.getNamedParams()
				),
				"itemid"
			);

			if (!empty(linkedItemIds)) {
				cdb.update("items_applications", array((Map)map(
					"values", map("applicationid", appId),
					"where", map("itemid", linkedItemIds.valuesAsLong())
				)));
			}

			CArray notLinkedItemIds = array_diff(itemIds, linkedItemIds);
			if (!empty(notLinkedItemIds)) {
				CArray<Map> insert = array();
				for(Object itemId : notLinkedItemIds) {
					insert.add(map("itemid", itemId, "applicationid", appId));
				}
				cdb.insert("items_applications", insert);
			}
		}
	}
	
	/**
	 * Get item key for test item.
	 *
	 * @param int    type
	 * @param string testName
	 *
	 * @return bool|string
	 */
	protected String getTestKey(int type, String testName) {
		switch (type) {
			case HTTPSTEP_ITEM_TYPE_IN:
				return "web.test.in["+quoteItemKeyParam(testName)+",,bps]";
			case HTTPSTEP_ITEM_TYPE_LASTSTEP:
				return "web.test.fail["+quoteItemKeyParam(testName)+"]";
			case HTTPSTEP_ITEM_TYPE_LASTERROR:
				return "web.test.error["+quoteItemKeyParam(testName)+"]";
		}
		return null;
	}
	
	/**
	 * Get item key for step item.
	 *
	 * @param int    type
	 * @param string testName
	 * @param string stepName
	 *
	 * @return bool|string
	 */
	protected String getStepKey(int type, String testName, String stepName) {
		switch (type) {
			case HTTPSTEP_ITEM_TYPE_IN:
				return "web.test.in["+quoteItemKeyParam(testName)+","+quoteItemKeyParam(stepName)+",bps]";
			case HTTPSTEP_ITEM_TYPE_TIME:
				return "web.test.time["+quoteItemKeyParam(testName)+","+quoteItemKeyParam(stepName)+",resp]";
			case HTTPSTEP_ITEM_TYPE_RSPCODE:
				return "web.test.rspcode["+quoteItemKeyParam(testName)+","+quoteItemKeyParam(stepName)+"]";
		}
		return null;
	}
	
	/**
	 * Returns the data about the last execution of the given HTTP tests.
	 *
	 * The following values will be returned for each executed HTTP test:
	 * - lastcheck      - time when the test has been executed last
	 * - lastfailedstep - number of the last failed step
	 * - error          - error message
	 *
	 * If a HTTP test has never been executed, no value will be returned.
	 *
	 * @param array httpTestIds
	 *
	 * @return array    an array with HTTP test IDs as keys and arrays of data as values
	 */
	public CArray<Map> getLastData(Long... httpTestIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT hti.httptestid,hti.type,i.itemid,i.value_type" +
				" FROM httptestitem hti,items i" +
				" WHERE hti.itemid=i.itemid" +
					" AND hti.type IN ("+HTTPSTEP_ITEM_TYPE_LASTSTEP+","+HTTPSTEP_ITEM_TYPE_LASTERROR+")"+
					" AND "+sqlParts.dual.dbConditionInt("hti.httptestid", httpTestIds);
		Map<String, Object> params = sqlParts.getNamedParams();
		
		CArray<Map> httpItems = DBselect(executor, sql, params);		
		CArray<CArray<Map>> history = Manager.History(idBean, executor).getLast(CArray.valueOf(httpItems));		
		CArray<Map> data = new CArray();		
		for (Map httpItem : httpItems) {
			if (isset(history.get(httpItem.get("itemid")))) {
				if (!isset(data.get(httpItem.get("httptestid")))) {
					Nest.value(data, httpItem.get("httptestid")).$(map(
						"lastcheck", null,
						"lastfailedstep", null,
						"error", null
					));
				}
				Map itemHistory = (Map)Nest.value(history, httpItem.get("itemid"), 0).$();
				if (Nest.value(httpItem,"type").asInteger() == HTTPSTEP_ITEM_TYPE_LASTSTEP) {
					Nest.value(data, httpItem.get("httptestid"), "lastcheck").$(Nest.value(itemHistory,"clock").$());
					Nest.value(data, httpItem.get("httptestid"), "lastfailedstep").$(Nest.value(itemHistory,"value").$());
				} else {
					Nest.value(data, httpItem.get("httptestid"), "error").$(Nest.value(itemHistory,"value").$());
				}
			}
		}		
		return data;
	}

}
