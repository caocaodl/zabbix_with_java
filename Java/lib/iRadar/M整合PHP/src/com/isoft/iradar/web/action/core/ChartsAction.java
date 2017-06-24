package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ViewsUtil.redirect;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ChartsAction extends RadarBaseAction {
	
	private CPageFilter pageFilter = null;

	@Override
	protected void doInitPage() {
		page("title", _("Custom graphs"));
		page("file", "charts.action");
		page("hist_arg", new String[] { "hostid", "groupid", "graphid" });
		page("scripts", new String[] { "class.calendar.js", "gtlc.js", "flickerfreescreen.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
		
		define("RDA_PAGE_DO_JS_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,		null),
			"hostid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,		null),
			"graphid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,		null),
			"period",		array(T_RDA_INT, O_OPT, P_SYS, null,		null),
			"stime",		array(T_RDA_STR, O_OPT, P_SYS, null,		null),
			"action",		array(T_RDA_STR, O_OPT, P_SYS, IN("'go','add','remove'"), null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"),	null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT, null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT, NOT_EMPTY,	null),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT, null,		null),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT, NOT_EMPTY,	null),
			"favaction",	array(T_RDA_STR, O_OPT, P_ACT, IN("'add','remove'"), null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("graphid"))) {
			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
			goptions.setOutput(new String[]{"graphid"});
			CArray<Map> graphs = API.Graph(getIdentityBean(), executor).get(goptions);
			if (empty(graphs)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected void doPageFilter(SQLExecutor executor) {
		if(this.pageFilter == null){
			this.pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups", map("real_hosts", true, "with_graphs", true),
				"hosts", map("with_graphs", true),
				"groupid", get_request("groupid", null),
				"hostid", get_request("hostid", null),
				"graphs", map("templated", 0),
				"graphid", get_request("graphid", null)
			));
		}
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		
		doPageFilter(executor);
		
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.charts.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor, "web.screens.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
				}
			}
			if (str_in_array(Nest.value(_REQUEST,"favobj").$(), array("itemid", "graphid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.add(getIdentityBean(), executor, "web.favorite.graphids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Remove from favourites")+"\";\n");
						echo("$(\"addrm_fav\").onclick = function() { rm4favorites(\"graphid\", \""+Nest.value(_REQUEST,"favid").asString()+"\", 0); }\n");
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.graphids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());

					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Add to favourites")+"\";\n");
						echo("$(\"addrm_fav\").onclick = function() { add2favorites(\"graphid\", \""+Nest.value(_REQUEST,"favid").asInteger()+"\"); }\n");
					}
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					echo("switchElementsClass(\"addrm_fav\", \"iconminus\", \"iconplus\");");
				}
			}
		}
		if (!empty(Nest.value(_REQUEST,"period").$()) || !empty(Nest.value(_REQUEST,"stime").$())) {
			CScreenBase.calculateTime(getIdentityBean(), executor, map(
				"profileIdx", "web.screens",
				"profileIdx2", pageFilter.$("graphid").$(),
				"updateProfile", true,
				"period", get_request("period"),
				"stime", get_request("stime")
			));

			Curl curl = new Curl();
			curl.removeArgument("period");
			curl.removeArgument("stime");
			//TODO
			//ob_end_clean();

			CProfile.flush(getIdentityBean(), executor);

			redirect(curl.getUrl());
		}

		//ob_end_flush();

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}
	
	@Override
	protected void doAction(SQLExecutor executor) {
		/* Display */
		CArray data = map(
			"pageFilter", pageFilter,
			"graphid", pageFilter.$("graphid").$(),
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$()
		);

		// render view
		CView chartsView = new CView("monitoring.charts", data);
		chartsView.render(getIdentityBean(), executor);
		chartsView.show();
	}
}
