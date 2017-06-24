package com.isoft.iradar.web.action.core;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.EventsUtil.eventSourceObjects;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CAlertGet;
import com.isoft.iradar.model.params.CUserGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AuditactsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Audit actions"));
		page("file", "auditacts.action");
		page("hist_arg", new String[] {});
		page("scripts", new String[] { "class.calendar.js", "gtlc.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}	

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			// filter
			"filter_rst",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"filter_set",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"alias",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"period",		array(T_RDA_INT, O_OPT, null,	null,		null),
			"dec",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"inc",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"left",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"right",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"stime",		array(T_RDA_STR, O_OPT, null,	null,		null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT,	null,		null)
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
				CProfile.update(getIdentityBean(), executor, "web.auditacts.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor, "web.auditacts.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
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
		Nest.value(_REQUEST,"alias").$(isset(_REQUEST,"filter_rst")
			? ""
			: get_request("alias", CProfile.get(getIdentityBean(), executor, "web.auditacts.filter.alias", "")));

		if (isset(_REQUEST,"filter_set") || isset(_REQUEST,"filter_rst")) {
			CProfile.update(getIdentityBean(), executor, "web.auditacts.filter.alias", Nest.value(_REQUEST,"alias").$(), PROFILE_TYPE_STR);
		}

		/* Display */
		int effectivePeriod = navigation_bar_calc(getIdentityBean(), executor, "web.auditacts.timeline", 0L, true);
		Map data = map(
			"stime", get_request("stime"),
			"alias", get_request("alias"),
			"alerts", array()
		);

		long from = rdaDateToTime(Nest.value(data,"stime").asString());
		long till = from + effectivePeriod;

		Map user = null;
		boolean queryData = true;
		Map firstAlert = null;

		if (!empty(Nest.value(data,"alias").$())) {
			CUserGet uoptions = new CUserGet();
			uoptions.setOutput(new String[]{"userid"});
			uoptions.setFilter("alias", Nest.value(data,"alias").asString());
			CArray<Map> users = API.User(getIdentityBean(), executor).get(uoptions);
			if (!empty(users)) {
				user = reset(users);
			} else {
				queryData = false;
			}
		}

		// fetch alerts for different objects and sources and combine them in a single stream
		if (queryData) {
			Map<String, Object> config = select_config(getIdentityBean(), executor);
			for(CArray<Integer> eventSource : eventSourceObjects()) {
				CAlertGet aoptions = new CAlertGet();
				aoptions.setOutput(API_OUTPUT_EXTEND);
				aoptions.setSelectMediatypes(API_OUTPUT_EXTEND);
				if(!empty(Nest.value(data,"alias").$())){
					aoptions.setUserIds(Nest.value(user,"userid").asString());
				}
				aoptions.setTimeFrom(from);
				aoptions.setTimeTill(till);
				aoptions.setEventSource(Nest.value(eventSource,"source").asInteger());
				aoptions.setEventObject(Nest.value(eventSource,"object").asInteger());
				aoptions.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				Nest.value(data,"alerts").$(array_merge(Nest.value(data,"alerts").asCArray(), (CArray)API.Alert(getIdentityBean(), executor).get(aoptions)));
			}

			CArrayHelper.sort(Nest.value(data,"alerts").asCArray(), array(
				map("field", "alertid", "order", RDA_SORT_DOWN)
			));

			Nest.value(data,"alerts").$(array_slice(Nest.value(data,"alerts").asCArray(), 0, Nest.value(config,"search_limit").asInteger() + 1));

			// get first alert
			if (!empty(user)) {
				Map params = new HashMap();
				params.put("userid", Nest.value(user,"userid").$());
				firstAlert = DBfetch(DBselect(executor,
					"SELECT MIN(a.clock) AS clock"+
					" FROM alerts a"+
					" WHERE a.userid=#{userid}",
					params
				));
			}
		}

		// padding
		Nest.value(data,"paging").$(getPagingLine(getIdentityBean(), executor,Nest.value(data,"alerts").asCArray()));

		// timeline
		Nest.value(data,"timeline").$(map(
			"period", effectivePeriod,
			"starttime", date(TIMESTAMP_FORMAT, (!empty(firstAlert) ? Nest.value(firstAlert,"clock").asLong() : RDA_MAX_PERIOD)),
			"usertime", isset(Nest.value(data,"stime").$()) ? date(TIMESTAMP_FORMAT, rdaDateToTime(Nest.value(data,"stime").asString()) + effectivePeriod) : null
		));

		// render view
		CView auditView = new CView("administration.auditacts.list", data);
		auditView.render(getIdentityBean(), executor);
		auditView.show();
	}

}
