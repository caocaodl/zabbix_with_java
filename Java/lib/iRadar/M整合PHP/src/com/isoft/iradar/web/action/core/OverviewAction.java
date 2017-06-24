package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class OverviewAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Overview"));
		page("file", "overview.action");
		page("hist_arg", new String[] { "groupid", "type" });
		page("type", detect_page_type(PAGE_TYPE_HTML));

		define("RDA_PAGE_DO_REFRESH", 1);
		define("SHOW_TRIGGERS", 0);
		define("SHOW_DATA", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid", array(T_RDA_INT, O_OPT, P_SYS, DB_ID,     null),
			"view_style", array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"), null),
			"type", array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"), null),
			"application", array(T_RDA_STR, O_OPT, P_SYS, null,	   null),
			"fullscreen", array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"), null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Display  */
		CArray data = map(
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$()
		);
		
		Nest.value(data,"view_style").$(get_request("view_style", CProfile.get(getIdentityBean(), executor, "web.overview.view.style", STYLE_TOP)));
		CProfile.update(getIdentityBean(), executor, "web.overview.view.style", Nest.value(data,"view_style").$(), PROFILE_TYPE_INT);
		
		Nest.value(data,"type").$(get_request("type", CProfile.get(getIdentityBean(), executor, "web.overview.type", define("SHOW_TRIGGERS"))));
		CProfile.update(getIdentityBean(), executor, "web.overview.type", Nest.value(data,"type").$(), PROFILE_TYPE_INT);
		
		Nest.value(data,"pageFilter").$(new CPageFilter(getIdentityBean(), executor, map(
			"groups", map(
				(Nest.value(data,"type").$().equals(define("SHOW_TRIGGERS")) ? "with_monitored_triggers" : "with_monitored_items"), true
			),
			"hosts", map(
				"monitored_hosts", true,
				(Nest.value(data,"type").$().equals(define("SHOW_TRIGGERS")) ? "with_monitored_triggers" : "with_monitored_items"), true
			),
			"applications", map("templated", false),
			"hostid", get_request("hostid", null),
			"groupid", get_request("groupid", null),
			"application", get_request("application", null)
		)));
		
		Nest.value(data,"groupid").$(((CPageFilter)data.get("pageFilter")).$("groupid").$());
		
		// render view
		CView overviewView = new CView("monitoring.overview", data);
		overviewView.render(getIdentityBean(), executor);
		overviewView.show();
	}

}
