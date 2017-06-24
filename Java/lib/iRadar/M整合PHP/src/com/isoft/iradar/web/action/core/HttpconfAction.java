package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_reverse;
import static com.isoft.iradar.Cphp.array_shift;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBexecute;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.DBUtil.idcmp;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCENARIO;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_BASIC;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_NONE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_NTLM;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.NOT_ZERO;
import static com.isoft.iradar.inc.Defines.O_NO;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RARR;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.HttpTestUtil.delete_history_by_httptestid;
import static com.isoft.iradar.inc.HttpTestUtil.getHttpTestsParentTemplates;
import static com.isoft.iradar.inc.HttpTestUtil.get_httptest_by_httptestid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HttpconfAction extends RadarBaseAction {

	private int showDisabled;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of web monitoring"));
		page("file", "httpconf.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid", 				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,				null),
			"new_httpstep", 		array(T_RDA_STR, O_OPT, null,	null,				null),
			"sel_step", 				array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65534),	null),
			"group_httptestid",	array(T_RDA_INT, O_OPT, null,	DB_ID,				null),
			"showdisabled", 		array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),			null),
			// form
			"hostid", 					array(T_RDA_INT, O_OPT, P_SYS, DB_ID+NOT_ZERO,          "isset({form})||isset({save})"),
			"applicationid", 		array(T_RDA_INT, O_OPT, null,  DB_ID,                   null, _("Application")),
			"httptestid", 				array(T_RDA_INT, O_NO,  P_SYS, DB_ID,                   "(isset({form})&&({form}==\"update\"))"),
			"name", 					array(T_RDA_STR, O_OPT, null,  NOT_EMPTY,               "isset({save})", _("Name")),
			"delay", 					array(T_RDA_INT, O_OPT, null,  BETWEEN(1, SEC_PER_DAY), "isset({save})", _("Update interval (in sec)")),
			"retries", 					array(T_RDA_INT, O_OPT, null,  BETWEEN(1, 10),          "isset({save})", _("Retries")),
			"status", 					array(T_RDA_STR, O_OPT, null,  null,                    null),
			"agent", 					array(T_RDA_STR, O_OPT, null,  null,                    "isset({save})"),
			"variables", 				array(T_RDA_STR, O_OPT, null,  null,                    "isset({save})"),
			"steps", 					array(T_RDA_STR, O_OPT, null,  null,                    "isset({save})", _("Steps")),
			"authentication", 		array(T_RDA_INT, O_OPT, null,  IN("0,1,2"),             "isset({save})"),
			"http_user", 				array(T_RDA_STR, O_OPT, null,  NOT_EMPTY,               "isset({save})&&isset({authentication})&&({authentication}=="+HTTPTEST_AUTH_BASIC+"||{authentication}=="+HTTPTEST_AUTH_NTLM+")", _("User")),
			"http_password", 		array(T_RDA_STR, O_OPT, null,	NOT_EMPTY,			"isset({save})&&isset({authentication})&&({authentication}=="+HTTPTEST_AUTH_BASIC+"||{authentication}=="+HTTPTEST_AUTH_NTLM+")", _("Password")),
			"http_proxy", 			array(T_RDA_STR, O_OPT, null,	null,				"isset({save})"),
			"new_application", 	array(T_RDA_STR, O_OPT, null,	null,				null),
			"hostname", 			array(T_RDA_STR, O_OPT, null,	null,				null),
			"templated", 			array(T_RDA_STR, O_OPT, null,	null,				null),
			// actions
			"go", 						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"clone", 					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"save", 						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"delete", 					array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,			null),
			"cancel", 					array(T_RDA_STR, O_OPT, P_SYS,	null,				null),
			"form", 					array(T_RDA_STR, O_OPT, P_SYS,	null,				null),
			"form_refresh", 		array(T_RDA_INT, O_OPT, null,	null,				null)
		);
		Nest.value(_REQUEST,"showdisabled").$(get_request("showdisabled", CProfile.get(getIdentityBean(), executor,"web.httpconf.showdisabled", 1)));

		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor,"name", RDA_SORT_UP);

		showDisabled = get_request("showdisabled", 1);
		CProfile.update(getIdentityBean(), executor,"web.httpconf.showdisabled", showDisabled, PROFILE_TYPE_INT);

		if (!empty(Nest.value(_REQUEST,"steps").$())) {
			order_result(Nest.value(_REQUEST,"steps").asCArray(), "no");
		}
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (isset(_REQUEST, "httptestid") || !empty(Nest.value(_REQUEST, "group_httptestid").$())) {
			CArray testIds = array();
			if (isset(_REQUEST, "httptestid")) {
				testIds = Nest.value(_REQUEST, "httptestid").asCArray();
			}
			if (!empty(Nest.value(_REQUEST, "group_httptestid").$())) {
				testIds = array_merge(testIds,Nest.value(_REQUEST, "group_httptestid").asCArray());
			}
			if (!API.HttpTest(getIdentityBean(), executor).isWritable(testIds.valuesAsLong())) {
				access_deny();
			}
		}
		Nest.value(_REQUEST, "go").$(get_request("go", "none"));
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		// add new steps
		if (isset(_REQUEST,"new_httpstep")) {
			Nest.value(_REQUEST,"steps").$(get_request("steps", array()));
			Nest.value(_REQUEST,"new_httpstep","no").$(count(Nest.value(_REQUEST,"steps").$()) + 1);
			array_push(Nest.value(_REQUEST,"steps").asCArray(), Nest.value(_REQUEST,"new_httpstep").$());
			unset(_REQUEST,"new_httpstep");
		}

		if (isset(_REQUEST,"delete") && isset(_REQUEST,"httptestid")) {
			boolean result = false;

			Map params = new HashMap();
			params.put("httptestid", Nest.value(_REQUEST,"httptestid").$());
			Map host = DBfetch(DBselect(executor,
				"SELECT h.host FROM hosts h,httptest ht WHERE ht.hostid=h.hostid AND ht.httptestid=#{httptestid}",params));

			Map httptest_data = get_httptest_by_httptestid(executor,Nest.value(_REQUEST,"httptestid").asLong());
			if (!empty(httptest_data)) {
				result = Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.HttpTest(getIdentityBean(), executor).delete(Nest.value(_REQUEST,"httptestid").asLong()));
					}
				});
			}

			show_messages(result, _("Web scenario deleted"), _("Cannot delete web scenario"));
			if (result) {
				add_audit(getIdentityBean(), executor,AUDIT_ACTION_DELETE, AUDIT_RESOURCE_SCENARIO, "Web scenario ["+Nest.value(httptest_data,"name").$()+"] ["+
					Nest.value(_REQUEST,"httptestid").asString()+"] Host ["+Nest.value(host,"host").$()+"]");
			}
			unset(_REQUEST,"httptestid");
			unset(_REQUEST,"form");
		} else if (isset(_REQUEST,"clone") && isset(_REQUEST,"httptestid")) {
			unset(_REQUEST,"httptestid");
			unset(_REQUEST,"templated");
			Nest.value(_REQUEST,"form").$("clone");
		} else if (isset(_REQUEST,"save")) {
			int action;
			String message_true,message_false = null;
			try {
				DBstart(executor);
				
				if (isset(_REQUEST,"httptestid")) {
					action = AUDIT_ACTION_UPDATE;
					message_true = _("Scenario updated");
					message_false = _("Cannot update web scenario");
				} else {
					action = AUDIT_ACTION_ADD;
					message_true = _("Scenario added");
					message_false = _("Cannot add web scenario");
				}

				if (!empty(Nest.value(_REQUEST,"applicationid").$()) && !empty(Nest.value(_REQUEST,"new_application").$())) {
					throw new Exception(_("Cannot create new application, web scenario is already assigned to application."));
				}

				CArray<Map> steps = get_request("steps", array());
				if (!empty(steps)) {
					int i = 1;
					for (Entry<Object, Map> e : steps.entrySet()) {
					    Object snum = e.getKey();
					    //Map step = e.getValue();
						Nest.value(steps,snum,"no").$(i++);
					}
				}

				Map httpTest = map(
					"hostid", Nest.value(_REQUEST,"hostid").$(),
					"name", Nest.value(_REQUEST,"name").$(),
					"authentication", Nest.value(_REQUEST,"authentication").$(),
					"applicationid", get_request("applicationid"),
					"delay", Nest.value(_REQUEST,"delay").$(),
					"retries", Nest.value(_REQUEST,"retries").$(),
					"status", isset(_REQUEST,"status") ? 0 : 1,
					"agent", Nest.value(_REQUEST,"agent").$(),
					"variables", Nest.value(_REQUEST,"variables").$(),
					"http_proxy", Nest.value(_REQUEST,"http_proxy").$(),
					"steps", steps
				);

				if (!empty(Nest.value(_REQUEST,"new_application").$())) {
					CAppGet aoptions = new CAppGet();
					aoptions.setOutput(new String[]{"applicationid"});
					aoptions.setHostIds(Nest.value(_REQUEST,"hostid").asLong());
					aoptions.setFilter("name", Nest.value(_REQUEST,"new_application").asString());
					CArray<Map> exApp = API.Application(getIdentityBean(), executor).get(aoptions);
					if (!empty(exApp)) {
						Nest.value(httpTest,"applicationid").$(Nest.value(exApp,0,"applicationid").$());
					} else {
						CArray<Long[]> result = API.Application(getIdentityBean(), executor).create(array((Map)map(
							"name", Nest.value(_REQUEST,"new_application").$(),
							"hostid", Nest.value(_REQUEST,"hostid").$()
						)));
						if (!empty(result)) {
							Nest.value(httpTest,"applicationid").$(reset(result.get("applicationids")));
						} else {
							throw new Exception(_s("Cannot add new application \"%1$s\".", Nest.value(_REQUEST,"new_application").$()));
						}
					}
				}

				if (Nest.value(_REQUEST,"authentication").asInteger() != HTTPTEST_AUTH_NONE) {
					Nest.value(httpTest,"http_user").$(Nest.value(_REQUEST,"http_user").$());
					Nest.value(httpTest,"http_password").$(Nest.value(_REQUEST,"http_password").$());
				} else {
					Nest.value(httpTest,"http_user").$("");
					Nest.value(httpTest,"http_password").$("");
				}

				Long httptestid;
				if (isset(_REQUEST,"httptestid")) {
					// unset fields that did not change
					CHttpTestGet htoptions = new CHttpTestGet();
					htoptions.setHttptestIds(Nest.value(_REQUEST,"httptestid").asLong());
					htoptions.setOutput(API_OUTPUT_EXTEND);
					htoptions.setSelectSteps(API_OUTPUT_EXTEND);
					CArray<Map> dbHttpTests = API.HttpTest(getIdentityBean(), executor).get(htoptions);
					Map dbHttpTest = reset(dbHttpTests);
					CArray<Map> dbHttpSteps = rda_toHash(Nest.value(dbHttpTest,"steps").$(), "httpstepid");

					httpTest = CArrayHelper.unsetEqualValues(httpTest, dbHttpTest, array("applicationid"));
					for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(httpTest,"steps").asCArray()).entrySet()) {
					    Object snum = e.getKey();
					    Map step = e.getValue();
						if (isset(step,"httpstepid")) {
							Map newStep = CArrayHelper.unsetEqualValues(step, dbHttpSteps.get(step.get("httpstepid")), array("httpstepid"));
							Nest.value(httpTest,"steps",snum).$(newStep);
						}
					}

					httptestid = Nest.value(_REQUEST,"httptestid").asLong();
					Nest.value(httpTest,"httptestid").$(httptestid);
					boolean result = !empty(API.HttpTest(getIdentityBean(), executor).update(array(httpTest)));
					if (!result) {
						throw new Exception();
					} else {
						clearCookies(result, Nest.value(_REQUEST,"hostid").asString());
					}
				} else {
					CArray<Long[]> result = API.HttpTest(getIdentityBean(), executor).create(array(httpTest));
					if (empty(result)) {
						throw new Exception();
					} else {
						clearCookies(!empty(result), Nest.value(_REQUEST,"hostid").asString());
					}
					httptestid = reset(result.get("httptestids"));
				}

				Map host = get_host_by_hostid(getIdentityBean(), executor,Nest.value(_REQUEST,"hostid").asLong());
				add_audit(getIdentityBean(), executor,action, AUDIT_RESOURCE_SCENARIO, "Scenario ["+Nest.value(_REQUEST,"name").asString()+"] ["+httptestid+"] Host ["+Nest.value(host,"host").$()+"]");

				unset(_REQUEST,"httptestid");
				unset(_REQUEST,"form");
				show_messages(true, message_true);
				DBend(executor, true);
			} catch (Exception e) {
				DBend(executor, false);
				String msg = e.getMessage();
				if (!empty(msg)) {
					error(msg);
				}
				show_messages(false, null, message_false);
			}
		} else if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("group_httptestid")) {
			boolean result = true;
			CArray groupHttpTestId = get_request("group_httptestid",array());
			boolean enable = ("activate".equals(get_request("go")));
			final int status = enable ? HTTPTEST_STATUS_ACTIVE : HTTPTEST_STATUS_DISABLED;
			String statusName = enable ? "enabled" : "disabled";
			int auditAction = enable ? AUDIT_ACTION_ENABLE : AUDIT_ACTION_DISABLE;
			int updated = 0;
			
			DBstart(executor);

			Map params = new HashMap();
			for(final Object id : groupHttpTestId) {
				Map httpTestData = get_httptest_by_httptestid(executor,Nest.as(id).asLong());
				if (empty(httpTestData)) {
					continue;
				}
				result &= Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return !empty(API.HttpTest(getIdentityBean(), executor).update(array((Map)map(
								"httptestid", id,
								"status", status
							))));
					}
				});

				if (result) {
					params.put("httptestid", id);
					Map host = DBfetch(DBselect(executor,
						"SELECT h.host FROM hosts h,httptest ht WHERE ht.hostid=h.hostid AND ht.httptestid=#{httptestid}",
						params
					));
					add_audit(getIdentityBean(), executor, auditAction, AUDIT_RESOURCE_SCENARIO,
						"Scenario ["+Nest.value(httpTestData,"name").$()+"] ["+id+"] Host ["+Nest.value(host,"host").$()+"] "+statusName
					);
				}
				updated++;
			}

			String messageSuccess = enable
				? _n("Web scenario enabled", "Web scenarios enabled", updated)
				: _n("Web scenario disabled", "Web scenarios disabled", updated);
			String messageFailed = enable
				? _n("Cannot enable web scenario", "Cannot enable web scenarios", updated)
				: _n("Cannot disable web scenario", "Cannot disable web scenarios", updated);
			
			result = DBend(executor, result);
			
			show_messages(result, messageSuccess, messageFailed);
			clearCookies(result, get_request("hostid"));
		} else if ("clean_history".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_httptestid")) {
			boolean goResult = false;
			CArray group_httptestid = Nest.value(_REQUEST,"group_httptestid").asCArray();

			DBstart(executor);
			
			Map params = new HashMap();
			for(final Object id : group_httptestid) {
				Map httptest_data = get_httptest_by_httptestid(executor,Nest.as(id).asLong());
				if (empty(httptest_data)) {
					continue;
				}
				if (Call(new Wrapper<Boolean>() {
					@Override
					protected Boolean doCall() throws Throwable {
						return delete_history_by_httptestid(executor,Nest.as(id).asLong());
					}
				})) {
					goResult = true;
					params.put("httptestid", id);
					DBexecute(executor,"UPDATE httptest SET nextcheck=0 WHERE httptestid=#{httptestid}",params);
					Map host = DBfetch(DBselect(executor,
						"SELECT h.host FROM hosts h,httptest ht WHERE ht.hostid=h.hostid AND ht.httptestid=#{httptestid}",params));

					add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_SCENARIO, "Scenario ["+Nest.value(httptest_data,"name").$()+"] ["+id+
						"] Host ["+Nest.value(host,"host").$()+"] history cleared");
				}
			}
			
			goResult = DBend(executor, goResult);
			
			show_messages(goResult, _("History cleared"), null);
			clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());
		} else if ("delete".equals(Nest.value(_REQUEST,"go").asString()) && isset(_REQUEST,"group_httptestid")) {
			boolean goResult = Call(new Wrapper<Boolean>() {
				@Override
				protected Boolean doCall() throws Throwable {
					return !empty(API.HttpTest(getIdentityBean(), executor).delete(Nest.array(_REQUEST,"group_httptestid").asLong()));
				}
			});
			show_messages(goResult, _("Web scenario deleted"), _("Cannot delete web scenario"));
			clearCookies(goResult, Nest.value(_REQUEST,"hostid").asString());
		}

		show_messages();
		
		/* Display */
		if (isset(_REQUEST, "form")) {
			Map data = map(
				"hostid", get_request("hostid", 0), 
				"httptestid",get_request("httptestid", null), 
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh"), 
				"templates",array()
			);

			Map params = new HashMap();
			if (isset(data, "httptestid")) {
				// get templates
				String httpTestId = Nest.value(data, "httptestid").asString();
				while (!empty(httpTestId)) {
					params.put("httptestid", httpTestId);
					Map dbTest = DBfetch(DBselect(executor,
						"SELECT h.hostid,h.name,ht.httptestid,ht.templateid"+
							" FROM hosts h,httptest ht"+
							" WHERE ht.hostid=h.hostid"+
							" AND ht.httptestid=#{httptestid}",
						params
					));
					httpTestId = null;

					if (!empty(dbTest)) {
						if (!idcmp(Nest.value(data, "httptestid").$(),Nest.value(dbTest, "httptestid").$())) {
							Nest.value(data, "templates").asCArray().add(new CLink(
								Nest.value(dbTest,"name").$(),
								"httpconf.action?form=update&httptestid="+Nest.value(dbTest,"httptestid").asString()+"&hostid="+Nest.value(dbTest,"hostid").$(),
								"highlight underline weight_normal"
							));
							Nest.value(data, "templates").asCArray().add(SPACE + RARR + SPACE);
						}
						httpTestId = Nest.value(dbTest, "templateid").asString();
					}
				}
				Nest.value(data, "templates").$(array_reverse(Nest.value(data, "templates").asCArray()));

				array_shift(Nest.value(data,"templates").asCArray());
			}

			if ((isset(_REQUEST, "httptestid") && !isset(_REQUEST, "form_refresh"))) {
				params.put("httptestid", Nest.value(_REQUEST,"httptestid").$());
				Map dbHttpTest = DBfetch(DBselect(executor,
					"SELECT ht.*"+
					" FROM httptest ht"+
					" WHERE ht.httptestid=#{httptestid}",
					params
				));

				Nest.value(data, "name").$(Nest.value(dbHttpTest, "name").$());
				Nest.value(data, "applicationid").$(Nest.value(dbHttpTest, "applicationid").$());
				Nest.value(data, "new_application").$("");
				Nest.value(data, "delay").$(Nest.value(dbHttpTest, "delay").$());
				Nest.value(data, "retries").$(Nest.value(dbHttpTest, "retries").$());
				Nest.value(data, "status").$(Nest.value(dbHttpTest, "status").$());
				Nest.value(data, "agent").$(Nest.value(dbHttpTest, "agent").$());
				Nest.value(data, "variables").$(Nest.value(dbHttpTest, "variables").$());
				Nest.value(data, "authentication").$(Nest.value(dbHttpTest, "authentication").$());
				Nest.value(data, "http_user").$(Nest.value(dbHttpTest, "http_user").$());
				Nest.value(data, "http_password").$(Nest.value(dbHttpTest, "http_password").$());
				Nest.value(data, "http_proxy").$(Nest.value(dbHttpTest, "http_proxy").$());
				Nest.value(data, "templated").$(Nest.value(dbHttpTest, "templateid").$());
				Nest.value(data,"steps").$(DBselect(executor,"SELECT h.* FROM httpstep h WHERE h.httptestid=#{httptestid} ORDER BY h.no", params));
			} else {
				if (isset(_REQUEST, "form_refresh")) {
					Nest.value(data,"status").$(isset(_REQUEST,"status") ? HTTPTEST_STATUS_ACTIVE : HTTPTEST_STATUS_DISABLED);
				} else {
					Nest.value(data, "status").$(HTTPTEST_STATUS_ACTIVE);
				}

				Nest.value(data, "name").$(get_request("name", ""));
				Nest.value(data, "applicationid").$(get_request("applicationid"));
				Nest.value(data, "new_application").$(get_request("new_application", ""));
				Nest.value(data, "delay").$(get_request("delay", 60));
				Nest.value(data, "retries").$(get_request("retries", 1));
				Nest.value(data, "agent").$(get_request("agent", ""));
				Nest.value(data, "variables").$(get_request("variables", ""));
				Nest.value(data, "authentication").$(get_request("authentication", HTTPTEST_AUTH_NONE));
				Nest.value(data, "http_user").$(get_request("http_user", ""));
				Nest.value(data, "http_password").$(get_request("http_password", ""));
				Nest.value(data, "http_proxy").$(get_request("http_proxy", ""));
				Nest.value(data, "templated").$(get_request("templated"));
				Nest.value(data, "steps").$(get_request("steps", array()));
			}

			Nest.value(data, "application_list").$(array());
			if (!empty(Nest.value(data, "hostid").$())) {
				params.put("hostid", Nest.value(data,"hostid").$());
				CArray<Map> dbApps = DBselect(executor,"SELECT a.applicationid,a.name FROM applications a WHERE a.hostid=#{hostid}",params);
				for(Map dbApp : dbApps) {
					Nest.value(data,"application_list",dbApp.get("applicationid")).$(Nest.value(dbApp,"name").$());
				}
			}

			// render view
			CView httpView = new CView("configuration.httpconf.edit", data);
			httpView.render(getIdentityBean(), executor);
			httpView.show();
		} else {
			CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups", map(
					"editable", true
				),
				"hosts", map(
					"editable", true,
					"templated_hosts", true
				),
				"hostid", get_request("hostid"),
				"groupid", get_request("groupid")
			));

			CArray data = map(
				"hostid",pageFilter.$("hostid").$(),
				"pageFilter",pageFilter,
				"showDisabled",showDisabled,
				"httpTests",array(),
				"paging",null
			);
			if (pageFilter.$("hostsSelected").asBoolean()) {
				Map<String, Object> config = select_config(getIdentityBean(), executor);
				String sortfield = getPageSortField(getIdentityBean(), executor, "hostname");

				CHttpTestGet options = new CHttpTestGet();
				options.setEditable(true);
				options.setOutput(new String[] { "httptestid" });
				options.setLimit(Nest.value(config, "search_limit").asInteger() + 1);

				if (empty(Nest.value(data, "showDisabled").$())) {
					options.setFilter("status", Nest.as(HTTPTEST_STATUS_ACTIVE).asString());
				}
				if (pageFilter.$("hostid").asInteger() > 0) {
					options.setHostIds(pageFilter.$("hostid").asLong());
				} else if (pageFilter.$("groupid").asInteger() > 0) {
					options.setGroupIds(pageFilter.$("groupid").asLong());
				}

				CArray<Map> httpTests = API.HttpTest(getIdentityBean(), executor).get(options);

				order_result(httpTests, sortfield, getPageSortOrder(getIdentityBean(), executor));

				Nest.value(data, "paging").$(getPagingLine(getIdentityBean(), executor, httpTests, array("httptestid")));

				SqlBuilder sqlParts = new SqlBuilder();
				CArray<Map> dbHttpTests = DBselect(executor,
					"SELECT ht.httptestid,ht.name,ht.delay,ht.status,ht.hostid,ht.templateid,h.name AS hostname"+
						" FROM httptest ht"+
						" INNER JOIN hosts h ON h.hostid=ht.hostid"+
						" WHERE "+sqlParts.dual.dbConditionInt("ht.httptestid", rda_objectValues(httpTests, "httptestid").valuesAsLong()),
					sqlParts.getNamedParams()
				);
				httpTests = array();
				for(Map dbHttpTest : dbHttpTests) {
					Nest.value(httpTests,dbHttpTest.get("httptestid")).$(dbHttpTest);
				}

				sqlParts = new SqlBuilder();
				CArray<Map> dbHttpSteps = DBselect(executor,
					"SELECT hs.httptestid,COUNT(*) AS stepscnt"+
						" FROM httpstep hs"+
						" WHERE "+sqlParts.dual.dbConditionInt("hs.httptestid", rda_objectValues(httpTests, "httptestid").valuesAsLong())+
						" GROUP BY hs.httptestid",
					sqlParts.getNamedParams()
				);
				for(Map dbHttpStep : dbHttpSteps) {
					Nest.value(httpTests,dbHttpStep.get("httptestid"),"stepscnt").$(Nest.value(dbHttpStep,"stepscnt").$());
				}

				order_result(httpTests, sortfield, getPageSortOrder(getIdentityBean(), executor));

				Nest.value(data,"parentTemplates").$(getHttpTestsParentTemplates(executor,httpTests));

				Nest.value(data, "httpTests").$(httpTests);
			}

			// render view
			CView httpView = new CView("configuration.httpconf.list", data);
			httpView.render(getIdentityBean(), executor);
			httpView.show();
		}
	}
}
