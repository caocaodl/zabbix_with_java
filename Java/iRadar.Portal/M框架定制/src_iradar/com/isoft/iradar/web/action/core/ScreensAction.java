package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_filter;
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
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
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
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class ScreensAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Custom screens"));
		page("file", "screens.action");
		page("hist_arg", new String[] { "elementid", "screenname" });
		page("scripts", new String[] { "class.calendar.js", "gtlc.js", "flickerfreescreen.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("css", new String[] {"lessor/strategy/screen_common.css", "lessor/strategy/screenshow.css"});

		define("RDA_PAGE_DO_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"tr_groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"tr_hostid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"elementid",		array(T_RDA_INT, O_OPT, P_SYS|P_NZERO, DB_ID, null),
			"screenname",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"step",				array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(0, 65535), null),
			"period",			array(T_RDA_INT, O_OPT, P_SYS,	null,		null),
			"stime",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"reset",				array(T_RDA_STR, O_OPT, P_SYS,	IN("'reset'"), null),
			"fullscreen",		array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"), null),
			// ajax
			"favobj",			array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",			array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	null),
			"favid",				array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"favaction",		array(T_RDA_STR, O_OPT, P_ACT,	IN("'add','remove','flop'"), null),
			"favstate",		array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		// validate group IDs
		CArray validateGroupIds = array_filter(array(
			get_request("groupid"),
			get_request("tr_groupid")
		));
		if (!empty(validateGroupIds) && !API.HostGroup(getIdentityBean(), executor).isReadable(validateGroupIds.valuesAsLong())) {
			access_deny();
		}

		// validate host IDs
		CArray validateHostIds = array_filter(array(
			get_request("hostid"),
			get_request("tr_hostid")
		));
		if (!empty(validateHostIds) && !API.Host(getIdentityBean(), executor).isReadable(validateHostIds.valuesAsLong())) {
			access_deny();
		}

		if (!empty(get_request("elementid"))) {
			CScreenGet soptions = new CScreenGet();
			soptions.setScreenIds(Nest.value(_REQUEST,"elementid").asLong());
			soptions.setOutput(new String[]{"screenid"});
			CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);
			if (empty(screens)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return false;
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Filter */
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.screens.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}

			if ("timeline".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"elementid") && isset(_REQUEST,"period")) {
					navigation_bar_calc(getIdentityBean(), executor, "web.screens", Nest.value(_REQUEST,"elementid").asLong(), true);
				}
			}

			if (str_in_array(Nest.value(_REQUEST,"favobj").asString(), array("screenid", "slideshowid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.add(getIdentityBean(), executor, "web.favorite.screenids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Remove from favourites")+"\";\n"+
							"$(\"addrm_fav\").onclick = function() { rm4favorites(\""+Nest.value(_REQUEST,"favobj").asString()+"\", \""+Nest.value(_REQUEST,"favid").asString()+"\", 0); }\n");
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.screenids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Add to favourites")+"\";\n"+
							"$(\"addrm_fav\").onclick = function() { add2favorites(\""+Nest.value(_REQUEST,"favobj").asString()+"\", \""+Nest.value(_REQUEST,"favid").asString()+"\"); }\n");
					}
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					echo("switchElementsClass(\"addrm_fav\", \"iconminus\", \"iconplus\");");
				}
			}

			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor, "web.screens.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
				}
			}
		}
		/* Display */
		Map data = map(
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$(),
			"period", get_request("period"),
			"stime", get_request("stime"),
			"elementid", get_request("elementid"),

			// whether we should use screen name to fetch a screen (if this is false, elementid is used)
			"use_screen_name", isset(_REQUEST,"screenname")
		);

		// if none is provided
		if (empty(Nest.value(data,"elementid").$()) && !Nest.value(data,"use_screen_name").asBoolean()) {
			// get element id saved in profile from the last visit
			Nest.value(data,"elementid").$(CProfile.get(getIdentityBean(), executor, "web.screens.elementid", null));
		}

		CScreenGet soptions = new CScreenGet();
		soptions.setOutput(new String[]{"screenid", "name"});
		CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);
		Nest.value(data,"screens").$(screens);

		// if screen name is provided it takes priority over elementid
		if (Nest.value(data,"use_screen_name").asBoolean()) {
			Nest.value(data,"screens").$(rda_toHash(screens, "name"));
			Nest.value(data,"elementIdentifier").$(get_request("screenname"));
		} else {
			Nest.value(data,"screens").$(rda_toHash(screens, "screenid"));
			Nest.value(data,"elementIdentifier").$(Nest.value(data,"elementid").$());
		}
		order_result(Nest.value(data,"screens").asCArray(), "name");

		// render view
		CView screenView = new CView("monitoring.screen", data);
		screenView.render(getIdentityBean(), executor);
		screenView.show();
	}

}
