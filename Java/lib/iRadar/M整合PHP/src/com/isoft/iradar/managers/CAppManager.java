package com.isoft.iradar.managers;

import static com.isoft.biz.daoimpl.radar.CDB.dbfetchArrayAssoc;
import static com.isoft.biz.daoimpl.radar.CDB.insert;
import static com.isoft.biz.daoimpl.radar.CDB.insertBatch;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_fill_keys;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetchColumn;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toArray;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CAppManager {
	
	private IIdentityBean idBean;
	private SQLExecutor executor;
	
	private CAppManager(IIdentityBean idBean, SQLExecutor executor){
		this.idBean = idBean;
		this.executor = executor;
	}
	
	/**
	 * Create new application.
	 * If batch is true it performs batch insert, in this case all applications must have same fields in same order.
	 *
	 * @param array applications
	 * @param bool  batch
	 *
	 * @return array
	 */
	public CArray create(CArray<Map> applications) {
		return create(applications, false);
	}
	
	public CArray<Map> create(CArray<Map> applications, boolean batch) {
		CArray<Map> insertApplications = Clone.deepcopy(applications);
		for(Map app : insertApplications) {
			unset(app,"applicationTemplates");
		}

		CArray<Long> applicationids = null;
		if (batch) {
			applicationids = insertBatch(this.idBean, this.executor, "applications", insertApplications);
		} else {
			applicationids = insert(this.idBean, this.executor, "applications", insertApplications);
		}

		CArray<Map> applicationTemplates = array();
		for (Entry<Object, Map> e : applications.entrySet()) {
		    Object anum = e.getKey();
		    Map application = e.getValue();
			Nest.value(application,"applicationid").$(applicationids.get(anum));

			if (isset(application,"applicationTemplates")) {
				for(Map applicationTemplate : (CArray<Map>)Nest.value(application,"applicationTemplates").asCArray()) {
					applicationTemplates.add(map(
						"applicationid", Nest.value(application,"applicationid").$(),
						"templateid", Nest.value(applicationTemplate,"templateid").$()
					));
				}
			}
		}

		// link inherited apps
		insertBatch(this.idBean, this.executor, "application_template", applicationTemplates);

		return applications;
	}
	
	/**
	 * Update applications.
	 *
	 * @param array applications
	 *
	 * @return array
	 */
	public CArray<Map> update(CArray<Map> applications) {
		applications = Clone.deepcopy(applications);
		CArray<Map> update = array();
		CArray applicationTemplates = array();
		for(Map application : applications) {
			if (isset(application,"applicationTemplates")) {
				for(Map applicationTemplate : (CArray<Map>)Nest.value(application,"applicationTemplates").asCArray()) {
					applicationTemplates.add(applicationTemplate);
				}
				unset(application,"applicationTemplates");
			}

			update.add(map(
				"values", application,
				"where", map("applicationid", Nest.value(application,"applicationid").$())
			));
		}
		CDB.update(idBean, executor,"applications", update);
		SqlBuilder sqlParts = new SqlBuilder();
		// replace existing application templates
		if (!empty(applicationTemplates)) {
			CArray<Map> dbApplicationTemplates = DBselect(
				executor,
				"SELECT * "+
				" FROM application_template at"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "application_template", "at")+
				" AND "+sqlParts.dual.dbConditionInt("at.applicationid", rda_objectValues(applications, "applicationid").valuesAsLong()),
				sqlParts.getNamedParams()
			);
			(new CDB(this.idBean, this.executor)).replace("application_template", dbApplicationTemplates, applicationTemplates);
		}

		return applications;
	}

	/**
	 * Link applications in template to hosts.
	 *
	 * @param templateId
	 * @param hostIds
	 *
	 * @return bool
	 */
	public boolean link(String templateId, CArray hostIds) {
		hostIds = rda_toArray(hostIds);
	
		// fetch template applications
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> applications = DBselect(executor,
			"SELECT a.applicationid,a.name,a.hostid"+
			" FROM applications a"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "applications", "a")+
			" AND a.hostid="+sqlParts.marshalParam(templateId),
			sqlParts.getNamedParams()
		);
	
		this.inherit(applications, hostIds);
	
		return true;
	}

	/**
	 * Inherit passed applications to hosts.
	 * If hostIds is empty that means that we need to inherit all applications to hosts which are linked to templates
	 * where applications belong.
	 *
	 * Usual use case is:
	 *   inherit is called with some hostIds passed
	 *   new applications are created/updated
	 *   inherit is called again with created/updated applications but empty hostIds
	 *   if any of new applications belongs to template, inherit it to all hosts linked to that template
	 *
	 * @param array applications
	 * @param array hostIds
	 *
	 * @return bool
	 */
	public boolean inherit(CArray<Map> applications) {
		return inherit(applications, array());
	}
	
	public boolean inherit(CArray<Map> applications, CArray hostIds) {
		applications = Clone.deepcopy(applications);
		hostIds = Clone.deepcopy(hostIds);
		CArray hostTemplateMap = getChildHostsFromApplications(applications, hostIds);
		if (empty(hostTemplateMap)) {
			return true;
		}

		CArray<Map> hostApps = getApplicationMapsByHostIds(array_keys(hostTemplateMap));
		CArray<Map> preparedApps = prepareInheritedApps(applications, hostTemplateMap, hostApps);
		CArray<Map> inheritedApps = save(preparedApps);

		applications = rda_toHash(applications, "applicationid");

		// update application linkage
		CArray oldApplicationTemplateIds = array();
		CArray movedAppTemplateIds = array();
		CArray childAppIdsPairs = array();
		CArray<Map> oldChildApps = array();
		Map oldChildAppsByTemplateId = null;
		Map oldChildApp = null;
		CArray oldApplicationTemplates = null;
		for(Map newChildApp : inheritedApps) {
			oldChildAppsByTemplateId = (Map)Nest.value(hostApps,newChildApp.get("hostid"),"byTemplateId").$();

			for(Map applicationTemplate : (CArray<Map>)Nest.value(newChildApp,"applicationTemplates").asCArray()) {
				// check if the parent of this application had a different child on the same host
				if (isset(oldChildAppsByTemplateId,applicationTemplate.get("templateid"))
						&& Nest.value(oldChildAppsByTemplateId,applicationTemplate.get("templateid"),"applicationid").asLong() != Nest.value(newChildApp,"applicationid").asLong()) {

					// if a different child existed, find the template-application link and remove it later
					oldChildApp = (Map)Nest.value(oldChildAppsByTemplateId,applicationTemplate.get("templateid")).$();
					oldApplicationTemplates = rda_toHash(Nest.value(oldChildApp,"applicationTemplates").$(), "templateid");
					oldApplicationTemplateIds.add(Nest.value(oldApplicationTemplates,applicationTemplate.get("templateid"),"application_templateid").$());

					// save the IDs of the affected templates and old
					if (isset(applications,applicationTemplate.get("templateid"))) {
						movedAppTemplateIds.add(Nest.value(applications,applicationTemplate.get("templateid"),"hostid").$());
						Nest.value(childAppIdsPairs,oldChildApp.get("applicationid")).$(Nest.value(newChildApp,"applicationid").$());
					}

					oldChildApps.add(oldChildApp);
				}
			}
		}

		// move all items and web scenarios from the old app to the new
		if (!empty(childAppIdsPairs)) {
			moveInheritedItems(movedAppTemplateIds, childAppIdsPairs);
			moveInheritedHttpTests(movedAppTemplateIds, childAppIdsPairs);
		}

		// delete old application links
		CDB cdb = new CDB(this.idBean, this.executor);
		if (!empty(oldApplicationTemplateIds)) {
			cdb.delete("application_template", (CArray)map(
				"application_templateid", oldApplicationTemplateIds.valuesAsLong()
			));
		}

		// delete old children that have only one parent
		CArray delAppIds = array();
		for(Map app : oldChildApps) {
			if (count(Nest.value(app,"applicationTemplates").asInteger()) == 1) {
				delAppIds.add(Nest.value(app,"applicationid").$());
			}
		}
		CArray emptyIds = null;
		if (!empty(delAppIds) && !empty(emptyIds = fetchEmptyIds(delAppIds.valuesAsLong()))) {
			delete(emptyIds.valuesAsString());
		}

		inherit(inheritedApps);

		return true;
	}
	
	/**
	 * Replaces applications for all items inherited from templates templateIds according to the map given in
	 * appIdPairs.
	 *
	 * @param array templateIds
	 * @param array appIdPairs		an array of source application ID - target application ID pairs
	 *
	 * @return void
	 */
	protected void moveInheritedItems(CArray templateIds, CArray<Map> appIdPairs) {
		SqlBuilder sqlParts = new SqlBuilder();
		// fetch existing item application links for all items inherited from template templateIds
		CArray<Map> itemApps = DBselect(
			executor,
			"SELECT ia2.itemappid,ia2.applicationid,ia2.itemid"+
			" FROM items i,items i2,items_applications ia2"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "items", "i")+
			    " AND i.tenantid=i2.tenantid"+
			    " AND i.itemid=i2.templateid"+
				" AND i2.tenantid=ia2.tenantid"+
				" AND i2.itemid=ia2.itemid"+
				" AND "+sqlParts.dual.dbConditionInt("i.hostid", templateIds.valuesAsLong())+
				" AND "+sqlParts.dual.dbConditionInt("ia2.applicationid", array_keys(appIdPairs).valuesAsLong()),
			sqlParts.getNamedParams()
		);

		// find item application links to target applications that may already exist
		sqlParts = new SqlBuilder();
		CArray<Map> query = DBselect(
			executor,
			"SELECT ia.itemid,ia.applicationid"+
			" FROM items_applications ia"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "items_applications", "ia")+
			    " AND "+sqlParts.dual.dbConditionInt("ia.applicationid", appIdPairs.valuesAsLong())+
				" AND "+sqlParts.dual.dbConditionInt("ia.itemid", rda_objectValues(itemApps, "itemid").valuesAsLong()),
			sqlParts.getNamedParams()
		);
		CArray exItemAppIds = array();
		for(Map row : query) {
			Nest.value(exItemAppIds,row.get("itemid"),row.get("applicationid")).$(Nest.value(row,"applicationid").$());
		}

		CArray<Object> newAppItems = array();
		CArray delAppItemIds = array();
		for(Map itemApp : itemApps) {
			// if no link to the target app exists, add a new one
			if (!isset(Nest.value(exItemAppIds,itemApp.get("itemid"),appIdPairs.get(itemApp.get("applicationid"))).$())) {
				Nest.value(newAppItems,appIdPairs.get(itemApp.get("applicationid"))).asCArray().add(Nest.value(itemApp,"itemappid").$());
			}
			// if the link to the target app already exists, delete the link to the old app
			else {
				delAppItemIds.add(Nest.value(itemApp,"itemappid").$());
			}
		}

		// link the items to the new apps
		CDB cdb = new CDB(this.idBean, this.executor);
		for (Entry<Object, Object> e : newAppItems.entrySet()) {
		    Object targetAppId = e.getKey();
		    Object itemAppIds = e.getValue();
		    cdb.updateByPk("items_applications", itemAppIds, map(
				"applicationid", targetAppId
			));
		}

		// delete old item application links
		if (!empty(delAppItemIds)) {
			cdb.delete("items_applications", (CArray)map("itemappid", delAppItemIds.valuesAsLong()));
		}
	}
	
	/**
	 * Return IDs of applications that are not used by items or HTTP tests.
	 *
	 * @param array applicationIds
	 * @return 
	 *
	 * @return array
	 */
	public CArray fetchEmptyIds(Long... applicationIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetchColumn(DBselect(
			executor,
			"SELECT a.applicationid "+
			" FROM applications a"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "applications", "a")+
			    " AND "+sqlParts.dual.dbConditionInt("a.applicationid", applicationIds)+
				" AND NOT EXISTS (SELECT NULL FROM items_applications ia WHERE a.tenantid=ia.tenantid AND a.applicationid=ia.applicationid)"+
				" AND NOT EXISTS (SELECT NULL FROM httptest ht WHERE a.tenantid=ht.tenantid AND a.applicationid=ht.applicationid)",
			sqlParts.getNamedParams()
		), "applicationid");
	}

	/**
	 * Return IDs of applications that are children only (!) of the given parents.
	 *
	 * @param array parentApplicationIds
	 *
	 * @return array
	 */
	public CArray fetchExclusiveChildIds(Long... parentApplicationIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBfetchColumn(DBselect(
			executor,
			"SELECT at.applicationid "+
			" FROM application_template at"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "application_template", "at")+
			" AND "+sqlParts.dual.dbConditionInt("at.templateid", parentApplicationIds)+
				" AND NOT EXISTS (SELECT NULL FROM application_template at2 WHERE at.tenantid=at2.tenantid"+
					" AND at.applicationid=at2.applicationid"+
					" AND "+sqlParts.dual.dbConditionInt("at2.templateid", parentApplicationIds, true)+
				")",
			sqlParts.getNamedParams()
		), "applicationid");
	}

	/**
	 * Delete applications.
	 *
	 * @param array applicationIds
	 */
	public void delete(String... applicationIds) {
		// unset applications from http tests
		CDB.update(idBean, executor, "httptest", array((Map)map(
			"values", map("applicationid", null),
			"where", map("applicationid", applicationIds)
		)));

		// remove Monitoring > Latest data toggle profile values related to given applications
		CProfile.delete(idBean, executor, "web.latest.toggle", applicationIds);

		CDB.delete(idBean, executor, "applications", (CArray)map("applicationid", applicationIds));
	}
	
	/**
	 * Replaces the applications for all http tests inherited from templates templateIds according to the map given in
	 * appIdPairs.
	 *
	 * @param array templateIds
	 * @param array appIdPairs		an array of source application ID - target application ID pairs
	 *
	 * @return void
	 */
	protected void moveInheritedHttpTests(CArray templateIds, CArray<Map> appIdPairs) {
		SqlBuilder sqlParts = new SqlBuilder();
		// find all http tests inherited from the given templates and linked to the given applications
		CArray<Map> query = DBselect(
			executor,
			"SELECT ht2.applicationid,ht2.httptestid"+
			" FROM httptest ht,httptest ht2"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httptest", "ht")+
			    " AND ht.tenantid=ht2.tenantid"+
			    " AND ht.httptestid=ht2.templateid"+
				" AND "+sqlParts.dual.dbConditionInt("ht.hostid", templateIds.valuesAsLong())+
				" AND "+sqlParts.dual.dbConditionInt("ht2.applicationid", array_keys(appIdPairs).valuesAsLong()),
			sqlParts.getNamedParams()
		);
		CArray<Object> targetAppHttpTestIds = array();
		for(Map row : query) {
			Nest.value(targetAppHttpTestIds,appIdPairs.get(row.get("applicationid"))).asCArray().add(Nest.value(row,"httptestid").$());
		}

		// link the http test to the new apps
		CDB cdb = new CDB(this.idBean, this.executor);
		for (Entry<Object, Object> e : targetAppHttpTestIds.entrySet()) {
		    Object targetAppId = e.getKey();
		    Object httpTestIds = e.getValue();
		    cdb.updateByPk("httptest", httpTestIds, map(
				"applicationid", targetAppId
			));
		}
	}
	
	/**
	 * Get array with hosts that are linked with templates which passed applications belongs to as key and
	 * templateid that host is linked to as value. If second parameter $hostIds is not empty, result should contain
	 * only passed host IDs.
	 *
	 * Example:
	 * We have template T1 with application A1 and template T1 with application A2 both linked to hosts H1 and H2.
	 * When we pass A1 to this function it should return array like:
	 *     array(H1_id => array(T1_id, T2_id), H2_id => array(T1_id, T2_id));
	 *
	 * @param array applications
	 * @param array hostIds
	 *
	 * @return array
	 */
	protected CArray getChildHostsFromApplications(CArray<Map> applications) {
		return getChildHostsFromApplications(applications, array());
	}
	
	protected CArray getChildHostsFromApplications(CArray<Map> applications, CArray hostIds) {
		SqlBuilder sqlParts = new SqlBuilder();
		CArray hostsTemplatesMap = array();

		CArray<Map> dbCursor = DBselect(executor,
			"SELECT ht.templateid,ht.hostid"+
			" FROM hosts_templates ht"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts_templates", "ht")+
			" AND "+sqlParts.dual.dbConditionInt("ht.templateid", rda_objectValues(applications, "hostid").valuesAsLong())+
				(!empty(hostIds) ? " AND "+sqlParts.dual.dbConditionInt("ht.hostid", hostIds.valuesAsLong()) : ""),
			sqlParts.getNamedParams()
		);
		for(Map dbHost : dbCursor) {
			Object hostId = Nest.value(dbHost,"hostid").$();
			Object templateId = Nest.value(dbHost,"templateid").$();

			if (!isset(hostsTemplatesMap, hostId)) {
				Nest.value(hostsTemplatesMap,hostId).$(array());
			}
			Nest.value(hostsTemplatesMap,hostId,templateId).$(templateId);
		}

		return hostsTemplatesMap;
	}
	
	/**
	 * Generate application data for inheritance. Using passed parameters, decide if new application must be
	 * created on host or existing application must be updated.
	 *
	 * @param array applications 		        applications to prepare for inheritance
	 * @param array hostsTemplatesMap	map of host IDs to templates they are linked to
	 * @param array hostApplications	    array of existing applications on the child host returned by
	 * 									                        self::getApplicationMapsByHostIds()
	 *
	 * @return array					                Return array with applications. Existing applications have \"applicationid\" key.
	 */
	protected CArray<Map> prepareInheritedApps(CArray<Map> applications, Map hostsTemplatesMap, CArray<Map> hostApplications) {
		applications = Clone.deepcopy(applications);
		hostsTemplatesMap = Clone.deepcopy(hostsTemplatesMap);
		hostApplications = Clone.deepcopy(hostApplications);
		
		/*
		 * This variable holds array of working copies of results, indexed first by host ID (hence pre-filling
		 * with host IDs from $hostApplications as keys and empty arrays as values), and then by application name.
		 * For each host ID / application name pair, there is only one array with application data
		 * with key \"applicationTemplates\" which is updated, if application with same name is inherited from
		 * more than one template. In the end this variable gets looped through and plain result array is constructed.
		 */
		CArray<CArray<Map>> newApplications = array_fill_keys(array_keys(hostApplications), array());
		
		Object applicationId = null;
		Map existingApplication = null;
		Map newApplication = null;
		for(Map application : applications) {
			applicationId = Nest.value(application,"applicationid").$();
			
			for (Entry<Object, Map> e : hostApplications.entrySet()) {
			    Object hostId = e.getKey();
			    Map hostApplication = e.getValue();
			    // If application template is not linked to host, skip it.
			    if (!isset(Nest.value(hostsTemplatesMap,hostId,application.get("hostid")).$())) {
					continue;
				}
			    
			    if (!isset(Nest.value(newApplications,hostId,application.get("name")).$())) {
					newApplication = map(
						"name", Nest.value(application,"name").$(),
						"hostid", hostId,
						"applicationTemplates", array()
					);
				}
				else {
					newApplication = Nest.value(newApplications,hostId,application.get("name")).asCArray();
				}

				existingApplication = null;

				/*
				 * Look for an application with the same name, if one exists - link the parent application to it.
				 * If no application with the same name exists, look for a child application via \"templateid\".
				 * Use it only if it has only one parent. Otherwise a new application must be created.
				 */
				if (isset(Nest.value(hostApplication,"byName",application.get("name")).$())) {
					existingApplication = Nest.value(hostApplication,"byName",application.get("name")).asCArray();
				} else if (isset(Nest.value(hostApplication,"byTemplateId",applicationId).asCArray())
						&& count(Nest.value(hostApplication,"byTemplateId",applicationId,"applicationTemplates").asCArray()) == 1) {
					existingApplication = Nest.value(hostApplication,"byTemplateId",applicationId).asCArray();
				}

				if (!empty(existingApplication)) {
					Nest.value(newApplication,"applicationid").$(Nest.value(existingApplication,"applicationid").$());

					// Add the new template link to an existing child application if it's not present yet.
					Nest.value(newApplication,"applicationTemplates").$(isset(existingApplication,"applicationTemplates")
						? Nest.value(existingApplication,"applicationTemplates").asCArray()
						: array());

					CArray applicationTemplateIds = rda_objectValues(Nest.value(newApplication,"applicationTemplates").asCArray(), "templateid");

					if (!in_array(applicationId, applicationTemplateIds)) {
						Nest.value(newApplication,"applicationTemplates").asCArray().add(map(
							"applicationid", Nest.value(newApplication,"applicationid").$(),
							"templateid", applicationId
						));
					}
				}
				else {
					// If no matching child application exists, add a new one.
					Nest.value(newApplication,"applicationTemplates").asCArray().add(map("templateid", applicationId));
				}
				
				// Store new or updated application data so it can be reused.
				Nest.value(newApplications,hostId,application.get("name")).$(newApplication);
			}
		}
		
		CArray<Map> result = array();
		for(CArray<Map> newApplicationsPerHost : newApplications) {
			for(Map _newApplication : newApplicationsPerHost) {
				result.add(_newApplication);
			}
		}

		return result;
	}
	
	/**
	 * Get host applications for each passed host.
	 * Each host has two hashes with applications, one with name keys other with templateid keys.
	 *
	 * Resulting structure is:
	 * array(
	 *     "hostid1" => array(
	 *         "byName" => array(app1data, app2data, ...),
	 *         "nyTemplateId" => array(app1data, app2data, ...)
	 *     ), ...
	 * );
	 *
	 * @param array hostIds
	 *
	 * @return array
	 */
	protected CArray<Map> getApplicationMapsByHostIds(CArray hostIds) {
		CArray<Map> hostApps = array();
		for(Object hostid : hostIds) {
			Nest.value(hostApps,hostid).$(map("byName",array(), "byTemplateId",array()));
		}

		// fetch applications
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> applications = dbfetchArrayAssoc(DBselect(
			executor,
			"SELECT a.applicationid,a.name,a.hostid"+
				" FROM applications a"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "applications", "a")+
				" AND "+sqlParts.dual.dbConditionInt("a.hostid", hostIds.valuesAsLong()),
			sqlParts.getNamedParams()
		), "applicationid");
		
		sqlParts = new SqlBuilder();
		CArray<Map> query = DBselect(
			executor,
			"SELECT *"+
				" FROM application_template at"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "application_template", "at")+
				" AND "+sqlParts.dual.dbConditionInt("at.applicationid", array_keys(applications).valuesAsLong()),
			sqlParts.getNamedParams()
		);
		for(Map applicationTemplate : query) {
			if(!isset(applications.get(applicationTemplate.get("applicationid")),"applicationTemplates")){
				Nest.value(applications,applicationTemplate.get("applicationid"),"applicationTemplates").$(array());
			}
			Nest.value(applications,applicationTemplate.get("applicationid"),"applicationTemplates").asCArray().add(applicationTemplate);
		}

		for(Map app : applications) {
			Nest.value(hostApps,app.get("hostid"),"byName",app.get("name")).$(app);
			if (isset(app,"applicationTemplates")) {
				for(Map applicationTemplate : (CArray<Map>)Nest.value(app,"applicationTemplates").asCArray()) {
					Nest.value(hostApps,app.get("hostid"),"byTemplateId",applicationTemplate.get("templateid")).$(app);
				}
			}
		}

		return hostApps;
	}
	
	/**
	 * Save applications. If application has applicationid it gets updated otherwise a new one is created.
	 *
	 * @param array applications
	 *
	 * @return array
	 */
	protected CArray<Map> save(CArray<Map> applications) {
		applications = Clone.deepcopy(applications);
		CArray<Map> appsCreate = array();
		CArray<Map> appsUpdate = array();

		for (Entry<Object, Map> e : applications.entrySet()) {
		    Object key = e.getKey();
		    Map app = e.getValue();
			if (isset(app,"applicationid")) {
				appsUpdate.add(app);
			} else {
				Nest.value(appsCreate,key).$(app);
			}
		}

		if (!empty(appsCreate)) {
			CArray<Map> newApps = create(appsCreate, true);
			for (Entry<Object, Map> e : newApps.entrySet()) {
			    Object key = e.getKey();
			    Map newApp = e.getValue();
			    Nest.value(applications,key,"applicationid").$(Nest.value(newApp,"applicationid").$());
			}
		}
		if (!empty(appsUpdate)) {
			update(appsUpdate);
		}

		return applications;
	}
	
}
