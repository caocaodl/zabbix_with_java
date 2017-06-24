package com.isoft.iradar.web.action.core;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.AuditUtil.audit_resource2str;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ADD;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DELETE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_DISABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_ENABLE;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGIN;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_LOGOUT;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AuditlogsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Audit logs"));
		page("file", "auditlogs.action");
		page("hist_arg", new String[] {});
		page("scripts", new String[] { "class.calendar.js", "gtlc.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"action",			array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(-1, 6), null),
			"resourcetype",	array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(-1, 31), null),
			"filter_rst",		array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"filter_set",		array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"alias",				array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"period",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"dec",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"inc",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"left",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"right",				array(T_RDA_INT, O_OPT, null,	null,		null),
			"stime",			array(T_RDA_STR, O_OPT, null,	null,		null),
			// ajax
			"favobj",			array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",			array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favstate",		array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favid",				array(T_RDA_INT, O_OPT, P_ACT,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor,"web.auditlogs.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor,"web.auditlogs.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
				}
			}
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Filter */
		if (isset(_REQUEST,"filter_rst")) {
			Nest.value(_REQUEST,"alias").$("");
			Nest.value(_REQUEST,"action").$(-1);
			Nest.value(_REQUEST,"resourcetype").$(-1);
		} else {
			Nest.value(_REQUEST,"alias").$(get_request("alias", CProfile.get(getIdentityBean(), executor,"web.auditlogs.filter.alias", "")));
			Nest.value(_REQUEST,"action").$(get_request("action", CProfile.get(getIdentityBean(), executor,"web.auditlogs.filter.action", -1)));
			Nest.value(_REQUEST,"resourcetype").$(get_request("resourcetype", CProfile.get(getIdentityBean(), executor,"web.auditlogs.filter.resourcetype", -1)));
		}

		if (isset(_REQUEST,"filter_set") || isset(_REQUEST,"filter_rst")) {
			CProfile.update(getIdentityBean(), executor,"web.auditlogs.filter.alias", Nest.value(_REQUEST,"alias").$(), PROFILE_TYPE_STR);
			CProfile.update(getIdentityBean(), executor,"web.auditlogs.filter.action", Nest.value(_REQUEST,"action").$(), PROFILE_TYPE_INT);
			CProfile.update(getIdentityBean(), executor,"web.auditlogs.filter.resourcetype", Nest.value(_REQUEST,"resourcetype").$(), PROFILE_TYPE_INT);
		}

		/* Display */
		int effectivePeriod = navigation_bar_calc(getIdentityBean(), executor,"web.auditlogs.timeline", 0L, true);
		Map data = map(
			"stime", get_request("stime"),
			"actions", array(),
			"action", get_request("action"),
			"resourcetype", get_request("resourcetype"),
			"alias", get_request("alias")
		);

		long from = rdaDateToTime(Nest.value(data,"stime").asString());
		long till = from + effectivePeriod;

		// get audit
		Map params = new HashMap();
		params.put("alias", Nest.value(data,"alias").$());
		params.put("action", Nest.value(data,"action").$());
		params.put("resourcetype", Nest.value(data,"resourcetype").$());
		params.put("from", from);
		params.put("till", till);
		CArray sqlWhere = array();
		if (!empty(Nest.value(data,"alias").$())) {
			Nest.value(sqlWhere,"alias").$(" AND u.alias=#{alias}");
		}
		if (Nest.value(data,"action").asLong() > -1) {
			Nest.value(sqlWhere,"action").$(" AND a.action=#{action}");
		}
		if (Nest.value(data,"resourcetype").asInteger() > -1) {
			Nest.value(sqlWhere,"resourcetype").$(" AND a.resourcetype=#{resourcetype}");
		}
		Nest.value(sqlWhere,"from").$(" AND a.clock>#{from}");
		Nest.value(sqlWhere,"till").$(" AND a.clock<#{till}");

		String sql = "SELECT a.auditid,a.clock,u.alias,a.ip,a.resourcetype,a.action,a.resourceid,a.resourcename,a.details"+
				" FROM auditlog a,users u"+
				" WHERE a.userid=u.userid"+
					implode("", sqlWhere)+
				" ORDER BY a.clock DESC";
		Map<String, Object> config = select_config(getIdentityBean(), executor);
		CArray<Map> dbAudit = DBselect(executor, sql, Nest.value(config,"search_limit").asInteger() + 1, params);
		for(Map audit : dbAudit) {
			String action;
			switch (Nest.value(audit,"action").asInteger()) {
				case AUDIT_ACTION_ADD:
					action = _("Added");
					break;
				case AUDIT_ACTION_UPDATE:
					action = _("Updated");
					break;
				case AUDIT_ACTION_DELETE:
					action = _("Deleted");
					break;
				case AUDIT_ACTION_LOGIN:
					action = _("Login");
					break;
				case AUDIT_ACTION_LOGOUT:
					action = _("Logout");
					break;
				case AUDIT_ACTION_ENABLE:
					action = _("Enabled");
					break;
				case AUDIT_ACTION_DISABLE:
					action = _("Disabled");
					break;
				default:
					action = _("Unknown action");
			}
			Nest.value(audit,"action").$(action);
			Nest.value(audit,"resourcetype").$(audit_resource2str(Nest.value(audit,"resourcetype").asInteger()));

			if (empty(Nest.value(audit,"details").$())) {
				params.put("auditid", Nest.value(audit,"auditid").$());
				Nest.value(audit,"details").$(DBselect(executor,
					"SELECT ad.table_name,ad.field_name,ad.oldvalue,ad.newvalue"+
					" FROM auditlog_details ad"+
					" WHERE ad.auditid=#{auditid}",
					params
				));
			}
			Nest.value(data,"actions",audit.get("auditid")).$(audit);
		}
		if (!empty(Nest.value(data,"actions").$())) {
			order_result(Nest.value(data,"actions").asCArray(), "clock", RDA_SORT_DOWN);
		}

		// get paging
		Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,Nest.value(data,"actions").asCArray()));

		// get timeline
		unset(sqlWhere,"from");
		unset(sqlWhere,"till");

		sql = "SELECT MIN(a.clock) AS clock"+
				" FROM auditlog a,users u"+
				" WHERE a.userid=u.userid"+
					implode("", sqlWhere);
		Map firstAudit = DBfetch(DBselect(executor, sql, Nest.value(config,"search_limit").asInteger() + 1, params));

		Nest.value(data,"timeline").$(map(
			"period", effectivePeriod,
			"starttime", date(TIMESTAMP_FORMAT, !empty(firstAudit) ? Nest.value(firstAudit,"clock").asLong() : null),
			"usertime", isset(_REQUEST,"stime") ? date(TIMESTAMP_FORMAT, rdaDateToTime(Nest.value(data,"stime").asString()) + effectivePeriod) : null
		));

		// render view
		CView auditView = new CView("administration.auditlogs.list", data);
		auditView.render(getIdentityBean(), executor);
		auditView.show();
	}

}
