package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_BASIC;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_NONE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_NTLM;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_DISABLED;
import static com.isoft.iradar.inc.ItemsUtil.delete_history_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_same_applications_for_host;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolver;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class HttpTestUtil {
	
	private HttpTestUtil() {
	}
	
	public static CArray<String> httptest_authentications() {
		CArray<String> authentication_types = map(
			HTTPTEST_AUTH_NONE, _("None"),
			HTTPTEST_AUTH_BASIC, _("Basic authentication"),
			HTTPTEST_AUTH_NTLM, _("NTLM authentication")
		);
		return authentication_types;
	}

	public static String httptest_authentications(int type) {
		CArray<String> authentication_types = map(
			HTTPTEST_AUTH_NONE, _("None"),
			HTTPTEST_AUTH_BASIC, _("Basic authentication"),
			HTTPTEST_AUTH_NTLM, _("NTLM authentication")
		);

		if (isset(authentication_types, type)) {
			return authentication_types.get(type);
		} else {
			return _("Unknown");
		}
	}
	
	public static CArray<String> httptest_status2str() {
		CArray<String> statuses = map(
			HTTPTEST_STATUS_ACTIVE, _("Enabled"),
			HTTPTEST_STATUS_DISABLED, _("Disabled")
		);
		return statuses;
	}

	public static String httptest_status2str(int status) {
		CArray<String> statuses = map(
			HTTPTEST_STATUS_ACTIVE, _("Enabled"),
			HTTPTEST_STATUS_DISABLED, _("Disabled")
		);

		if (isset(statuses, status)) {
			return statuses.get(status);
		} else {
			return _("Unknown");
		}
	}

	public static String httptest_status2style(int status) {
		CArray<String> statuses = map(
			HTTPTEST_STATUS_ACTIVE, "off",
			HTTPTEST_STATUS_DISABLED, "on"
		);

		if (isset(statuses,status)) {
			return statuses.get(status);
		} else {
			return "unknown";
		}
	}
	
	public static boolean delete_history_by_httptestid(SQLExecutor executor, long httptestid) {
		Map params = new HashMap();
		params.put("httptestid", httptestid);
		CArray<Map> db_items = DBselect(executor,
			"SELECT DISTINCT i.itemid"+
			" FROM items i,httpstepitem si,httpstep s"+
			" WHERE i.itemid=si.itemid"+
				" AND si.httpstepid=s.httpstepid"+
				" AND s.httptestid=#{httptestid}",
			params
		);
		for (Map item_data : db_items) {
			if (!delete_history_by_itemid(executor, Nest.value(item_data,"itemid").asLong())) {
				return false;
			}
		}
		return true;
	}

	public static Map get_httptest_by_httptestid(SQLExecutor executor, long httptestid) {
		Map params = new HashMap();
		params.put("httptestid", httptestid);
		return DBfetch(DBselect(executor, "SELECT ht.* FROM httptest ht WHERE ht.httptestid=#{httptestid}",params));
	}

	public static Map get_httpstep_by_no(SQLExecutor executor, long httptestid, int no) {
		Map params = new HashMap();
		params.put("httptestid", httptestid);
		params.put("no", no);
		return DBfetch(DBselect(executor, "SELECT hs.* FROM httpstep hs WHERE hs.httptestid=#{httptestid} AND hs.no=#{no}",params));
	}

	public static CArray<Map> get_httptests_by_hostid(SQLExecutor executor, Long... hostids) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor, "SELECT DISTINCT ht.* FROM httptest ht WHERE "+sqlParts.dual.dbConditionLong("ht.hostid", hostids), sqlParts.getNamedParams());
	}

	/**
	 * Return parent templates for http tests.
	 * Result structure:
	 * array(
	 *   "httptestid" => array(
	 *     "name" => <template name>,
	 *     "id" => <template id>
	 *   ), ...
	 * )
	 *
	 * @param array httpTests must have httptestid and templateid fields
	 *
	 * @return array
	 */
	public static CArray<Map> getHttpTestsParentTemplates(SQLExecutor executor, CArray<Map> httpTests) {
		CArray<Map> result = array();
		CArray template2testMap = array();

		for(Map httpTest : httpTests) {
			if (!empty(Nest.value(httpTest,"templateid").$())){
				Nest.value(result,httpTest.get("httptestid")).$(array());
				Nest.value(template2testMap,httpTest.get("templateid"),httpTest.get("httptestid")).$(Nest.value(httpTest,"httptestid").$());
			}
		}

		SqlBuilder sqlParts = null;
		CArray<Map> dbHttpTests = null;
		do {
			sqlParts = new SqlBuilder();
			dbHttpTests = DBselect(executor,
					"SELECT ht.httptestid,ht.templateid,ht.hostid,h.name"+
					" FROM httptest ht"+
					" INNER JOIN hosts h ON h.hostid=ht.hostid"+
					" WHERE "+sqlParts.dual.dbConditionLong("ht.httptestid", array_keys(template2testMap).valuesAsLong()),
					sqlParts.getNamedParams());
			for (Map dbHttpTest : dbHttpTests) {
				
				for(Object testId : Nest.value(template2testMap, dbHttpTest.get("httptestid")).asCArray().keySet()) {
					result.put(testId, map("name", Nest.value(dbHttpTest,"name").$(), "id", Nest.value(dbHttpTest,"hostid").$()));

					if (!empty(Nest.value(dbHttpTest,"templateid").$())) {
						Nest.value(template2testMap,dbHttpTest.get("templateid"),testId).$(testId);
					}
				}
				unset(template2testMap,dbHttpTest.get("httptestid"));
			}
		} while (!empty(template2testMap));

		return result;
	}

	/**
	 * Resolve http tests macros.
	 *
	 * @param array httpTests
	 * @param bool  resolveName
	 * @param bool  resolveStepName
	 *
	 * @return array
	 */
	public static CArray<Map> resolveHttpTestMacros(IIdentityBean idBean, SQLExecutor executor, CArray<Map> httpTests) {
		return resolveHttpTestMacros(idBean, executor, httpTests, true);
	}
	
	public static CArray<Map> resolveHttpTestMacros(IIdentityBean idBean, SQLExecutor executor, CArray<Map> httpTests, boolean resolveName) {
		return resolveHttpTestMacros(idBean, executor, httpTests, resolveName, true);
	}
	
	public static CArray<Map> resolveHttpTestMacros(IIdentityBean idBean, SQLExecutor executor, CArray<Map> httpTests, boolean resolveName, boolean resolveStepName) {
		CArray names = array();

		int i = 0;
		for(Map test : httpTests) {
			if (resolveName) {
				Nest.value(names,test.get("hostid"),i++).$(Nest.value(test,"name").$());
			}

			if (resolveStepName) {
				for(Map step : (CArray<Map>)Nest.value(test,"steps").asCArray()) {
					Nest.value(names,test.get("hostid"),i++).$(Nest.value(step,"name").$());
				}
			}
		}

		CMacrosResolver macrosResolver = new CMacrosResolver();
		names = macrosResolver.resolve(idBean, executor, map(
			"config", "httpTestName",
			"data", names
		));

		i = 0;
		for (Entry<Object, Map> e : httpTests.entrySet()) {
		    Object tnum = e.getKey();
		    Map test = e.getValue();
			if (resolveName) {
				Nest.value(httpTests,tnum,"name").$(Nest.value(names,test.get("hostid"),i++).$());
			}

			if (resolveStepName) {
				for (Object snum : Nest.value(httpTests,tnum,"steps").asCArray().keySet()) {
				    Nest.value(httpTests,tnum,"steps",snum,"name").$(Nest.value(names,test.get("hostid"),i++).$());
				}
			}
		}

		return httpTests;
	}

	/**
	 * Copies web scenarios from given host ID to destination host.
	 *
	 * @param string srcHostId		source host ID
	 * @param string dstHostId		destination host ID
	 *
	 * @return bool
	 */
	public static boolean copyHttpTests(IIdentityBean idBean, SQLExecutor executor, long srcHostId, long dstHostId) {
		CHttpTestGet options = new CHttpTestGet();
		options.setOutput(new String[]{"name", "applicationid", "delay", "status", "variables", "agent", "authentication", "http_user", "http_password", "http_proxy", "retries"});
		options.setHostIds(srcHostId);
		options.setSelectSteps(new String[]{"name", "no", "url", "timeout", "posts", "required", "status_codes", "variables"});
		options.setInherited(false);
		CArray<Map> httpTests = API.HttpTest(idBean, executor).get(options);

		if (empty(httpTests)) {
			return true;
		}

		// get destination application IDs
		CArray srcApplicationIds = array();
		for(Map httpTest : httpTests) {
			if (Nest.value(httpTest,"applicationid").asLong() != 0) {
				srcApplicationIds.add(Nest.value(httpTest,"applicationid").$());
			}
		}

		CArray<Long> dstApplicationIds = null;
		if (!empty(srcApplicationIds)) {
			dstApplicationIds = get_same_applications_for_host(executor, srcApplicationIds.valuesAsLong(), dstHostId);
		}

		for(Map httpTest : httpTests) {
			Nest.value(httpTest,"hostid").$(dstHostId);

			if (isset(dstApplicationIds,httpTest.get("applicationid"))) {
				Nest.value(httpTest,"applicationid").$(Nest.value(dstApplicationIds,httpTest.get("applicationid")).$());
			} else {
				unset(httpTest,"applicationid");
			}
			unset(httpTest,"httptestid");
		}
		return !empty(API.HttpTest(idBean, executor).create(httpTests));
	}
	
}
