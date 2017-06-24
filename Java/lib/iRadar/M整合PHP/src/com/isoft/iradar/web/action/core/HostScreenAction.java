package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HostScreenAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("title", _("Host screens"));
		page("file", "screens.action");
		page("hist_arg", new String[] { "elementid" });
		page("scripts", new String[] {"effects.js", "dragdrop.js", "class.calendar.js", "gtlc.js", "flickerfreescreen.js"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
		
		define("RDA_PAGE_DO_JS_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"hostid",		array(T_RDA_INT, O_OPT, P_SYS, DB_ID,		null),
			"tr_groupid",	array(T_RDA_INT, O_OPT, P_SYS, DB_ID,		null),
			"tr_hostid",	array(T_RDA_INT, O_OPT, P_SYS, DB_ID,		null),
			"screenid",	array(T_RDA_INT, O_OPT, P_SYS|P_NZERO, DB_ID, null),
			"step",		    array(T_RDA_INT, O_OPT, P_SYS, BETWEEN(0, 65535), null),
			"period",		array(T_RDA_INT, O_OPT, P_SYS, null,		null),
			"stime",		array(T_RDA_STR, O_OPT, P_SYS, null,		null),
			"reset",			array(T_RDA_STR, O_OPT, P_SYS, IN("'reset'"), null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS, IN("0,1"),	null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT, null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT, NOT_EMPTY,	null),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT, null,		null),
			"favaction",	array(T_RDA_STR, O_OPT, P_ACT, IN("'add','remove','flop'"), null),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT, NOT_EMPTY,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/** Ajax*/
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.hostscreen.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}

			if ("timeline".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"elementid") && isset(_REQUEST,"period")) {
					navigation_bar_calc(getIdentityBean(), executor, "web.hostscreen", Nest.value(_REQUEST,"elementid").asLong(), true);
				}
			}

			if (str_in_array(Nest.value(_REQUEST,"favobj").$(), array("screenid", "slideshowid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.add(getIdentityBean(), executor, "web.favorite.screenids",Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Remove from favourites")+"\";\n");
						echo("$(\"addrm_fav\").onclick = function() { rm4favorites(\""+Nest.value(_REQUEST,"favobj").asString()+"\", \""+Nest.value(_REQUEST,"favobj").asString()+"\", 0); }\n");
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor,"web.favorite.screenids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());

					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Add to favourites")+"\";\n");
						echo("$(\"addrm_fav\").onclick = function() { add2favorites(\""+Nest.value(_REQUEST,"favobj").asString()+"\", \""+Nest.value(_REQUEST,"favobj").asString()+"\"); }\n");
					}
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					echo("switchElementsClass(\"addrm_fav\", \"iconminus\", \"iconplus\");");
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
		/* Display */
		Map data = map(
			"hostid", get_request("hostid", 0),
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$(),
			"screenid", get_request("screenid", CProfile.get(getIdentityBean(), executor, "web.hostscreen.screenid", null)),
			"period", get_request("period"),
			"stime", get_request("stime")
		);
		CProfile.update(getIdentityBean(), executor, "web.hostscreen.screenid", Nest.value(data,"screenid").$(), PROFILE_TYPE_ID);

		// get screen list
		CTemplateScreenGet tsoptions = new CTemplateScreenGet();
		tsoptions.setHostIds(Nest.value(data,"hostid").asLong());
		tsoptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
		screens = rda_toHash(screens, "screenid");
		Nest.value(data,"screens").$(screens);
		order_result(screens, "name");

		// get screen
		Long screenid = null;
		if (!empty(screens)) {
			Map screen = !isset(screens,data.get("screenid")) ? reset(screens) : screens.get(data.get("screenid"));
			if (!empty(Nest.value(screen,"screenid").$())) {
				screenid = Nest.value(screen,"screenid").asLong();
			}
		}

		tsoptions = new CTemplateScreenGet();
		tsoptions.setScreenIds(screenid);
		tsoptions.setHostIds(Nest.value(data,"hostid").asLong());
		tsoptions.setOutput(API_OUTPUT_EXTEND);
		tsoptions.setSelectScreenItems(API_OUTPUT_EXTEND);
		screens = API.TemplateScreen(getIdentityBean(), executor).get(tsoptions);
		Map screen = reset(screens);
		Nest.value(data,"screen").$(screen);

		// get host
		if (!empty(Nest.value(data,"screen","hostid").$())) {
			Nest.value(data,"host").$(get_host_by_hostid(getIdentityBean(), executor,Nest.value(screen,"hostid").asLong()));
		}

		// render view
		CView screenView = new CView("monitoring.hostscreen", data);
		screenView.render(getIdentityBean(), executor);
		screenView.show();
	}
}
