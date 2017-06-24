package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_SCENARIO;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.HTTPTEST_AUTH_BASIC;
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
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HttpTestUtil.get_httptest_by_httptestid;
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
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ChangeHttpConfStatus extends RadarBaseAction{

	private int showDisabled;

	@Override
	protected void doInitPage() {
		page("title", _("Configuration of web monitoring"));
		page("file", "changeHttpConfStatus.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("js", new String[] {"imon/changeThresholdStatus.js"});	//引入改变监控状态所需JS
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid", 				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,				null),
			"new_httpstep", 		array(T_RDA_STR, O_OPT, null,	null,				null),
			"sel_step", 				array(T_RDA_INT, O_OPT, null,	BETWEEN(0, 65534),	null),
			"statusflag",	array(T_RDA_INT, O_OPT, null,	DB_ID,				null),//本参数为状态改变作判断是否链接设备条件用
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
		CHostGroupGet ch = new CHostGroupGet();
		ch.setOutput(new String[] { "groupid" });
		ch.setSelectHosts(new String[] { "hostid", "name" });
		CArray<Map> hosts = API.HostGroup(getIdentityBean(), executor).get(ch);
		CArray<Map> host = new CArray<Map>();
		for (Entry<Object, Map> e : hosts.entrySet()) {
			Map group = e.getValue();
			CArray<Map> hos = Nest.value(group, "hosts").asCArray();
			if (!empty(hos)) {
				for (Entry<Object, Map> f : hos.entrySet()) {
					Map hsot = f.getValue();

					if (Nest.value(hsot, "name").asString().equals("iRadar server")) {
						host.put("groupid", Nest.value(group, "groupid").asLong());
						host.put("hostid", Nest.value(hsot, "hostid").asLong());
					}
				}
			}
		}
		
		Nest.value(_REQUEST, "groupid").$(Nest.value(host,"groupid").$());
		Nest.value(_REQUEST, "hostid").$(Nest.value(host,"hostid").$());
		Nest.value(_REQUEST, " form_refresh").$(01);
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
	 if (str_in_array(get_request("go"), array("activate", "disable")) && hasRequest("group_httptestid")) {
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
							_("WEB PRIFEREWS")+" ["+Nest.value(httpTestData,"name").$()+"] ["+id+"] 设备 ["+Nest.value(host,"host").$()+"] "+statusName
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
		}
	}
}
