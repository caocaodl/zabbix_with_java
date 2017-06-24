package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_update_doll_script;
import static com.isoft.iradar.inc.FuncsUtil.make_refresh_menu;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.JsUtil.insertPagePostJs;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ScreensUtil.check_dynamic_items;
import static com.isoft.iradar.inc.ScreensUtil.get_slideshow;
import static com.isoft.iradar.inc.ScreensUtil.get_slideshow_by_slideshowid;
import static com.isoft.iradar.inc.ScreensUtil.slideshow_accessible;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ViewsUtil.includePageFooter;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class SlidesAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Custom slides"));
		page("file", "slides.action");
		page("hist_arg", new String[] { "elementid" });
		page("scripts", new String[] {"class.pmaster.js", "class.calendar.js", "gtlc.js", "flickerfreescreen.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));

		define("RDA_PAGE_DO_REFRESH", 1);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,	null),
			"hostid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,	null),
			"elementid",		array(T_RDA_INT, O_OPT, P_SYS|P_NZERO, DB_ID, null),
			"step",				array(T_RDA_INT, O_OPT, P_SYS,	BETWEEN(0, 65535), null),
			"period",			array(T_RDA_INT, O_OPT, P_SYS,	null,	null),
			"stime",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"reset",				array(T_RDA_STR, O_OPT, P_SYS,	IN("'reset'"), null),
			"fullscreen",		array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"), null),
			// ajax
			"favobj",			array(T_RDA_STR, O_OPT, P_ACT,	null,	null),
			"favref",			array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY, null),
			"favid",				array(T_RDA_INT, O_OPT, P_ACT,	null,	null),
			"favcnt",			array(T_RDA_STR, O_OPT, null,	null,	null),
			"pmasterid",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			// actions
			"favaction",		array(T_RDA_STR, O_OPT, P_ACT,	IN("'add','remove','refresh','flop'"), null),
			"favstate",		array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY, "isset({favaction})&&\"flop\"=={favaction}"),
			"upd_counter",	array(T_RDA_INT, O_OPT, P_ACT,	null,	null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"groupid").asLong())
				|| !empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("elementid"))) {
			Map slideshow = get_slideshow_by_slideshowid(getIdentityBean(), executor,Nest.value(_REQUEST,"elementid").asLong());
			if (empty(slideshow)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Actions */
		if (isset(_REQUEST,"favobj")) {
			Nest.value(_REQUEST,"pmasterid").$(get_request("pmasterid", "mainpage"));

			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.slides.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			} else if (str_in_array(Nest.value(_REQUEST,"favobj").$(), array("screenid", "slideshowid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.add(getIdentityBean(), executor, "web.favorite.screenids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Remove from")+" "+_("Favourites")+"\";\n"+
							"$(\"addrm_fav\").onclick = function() { rm4favorites(\""+Nest.value(_REQUEST,"favobj").$()+"\", \""+Nest.value(_REQUEST,"favid").$()+"\", 0); };\n");
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.screenids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
					if (result) {
						echo("$(\"addrm_fav\").title = \""+_("Add to")+" "+_("Favourites")+"\";\n"+
							"$(\"addrm_fav\").onclick = function() { add2favorites(\""+Nest.value(_REQUEST,"favobj").$()+"\", \""+Nest.value(_REQUEST,"favid").$()+"\"); };\n");
					}
				}
				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					echo("switchElementsClass(\"addrm_fav\", \"iconminus\", \"iconplus\");");
				}
			} else if ("hat".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if ("hat_slides".equals(Nest.value(_REQUEST,"favref").asString())) {
					String elementid = get_request("elementid");

					if (!is_null(elementid)) {
						Map slideshow = get_slideshow_by_slideshowid(getIdentityBean(), executor, Nest.as(elementid).asLong());
						Map screen = get_slideshow(getIdentityBean(), executor, Nest.as(elementid).asLong(), Nest.as(get_request("upd_counter")).asInteger());
						CScreenGet soptions = new CScreenGet();
						soptions.setScreenIds(Nest.value(screen,"screenid").asLong());
						CArray<Map> screens = API.Screen(getIdentityBean(), executor).get(soptions);

						if (empty(screens)) {
							insert_js("alert(\""+_("No permissions")+"\");");
						} else {
							Nest.value(page,"type").$(PAGE_TYPE_JS);

							// display screens
							soptions = new CScreenGet();
							soptions.setScreenIds(Nest.value(screen,"screenid").asLong());
							soptions.setOutput(API_OUTPUT_EXTEND);
							soptions.setSelectScreenItems(API_OUTPUT_EXTEND);
							screens = API.Screen(getIdentityBean(), executor).get(soptions);
							Map currentScreen = reset(screens);

							CScreenBuilder screenBuilder = new CScreenBuilder(getIdentityBean(), executor, map(
								"screen", currentScreen,
								"mode", SCREEN_MODE_PREVIEW,
								"profileIdx", "web.slides",
								"profileIdx2", elementid,
								"period", get_request("period"),
								"stime", get_request("stime")
							));

							CScreenBuilder.insertScreenCleanJs();

							echo(screenBuilder.show(getIdentityBean()).toString());

							CScreenBuilder.insertScreenStandardJs(getIdentityBean(), executor, map(
								"timeline", screenBuilder.timeline,
								"profileIdx", screenBuilder.profileIdx
							));

							insertPagePostJs();

							// insert slide show refresh js
							int refresh = (Nest.value(screen,"delay").asInteger() > 0) ? Nest.value(screen,"delay").asInteger() : Nest.value(slideshow,"delay").asInteger();
							int refresh_multipl = Nest.as(CProfile.get(getIdentityBean(), executor, "web.slides.rf_rate.hat_slides", 1, Nest.as(elementid).asLong())).asInteger();

							String script = get_update_doll_script("mainpage", Nest.value(_REQUEST,"favref").asString(), "frequency", Nest.as(refresh * refresh_multipl).asString())+"\n";
							script += get_update_doll_script("mainpage", Nest.value(_REQUEST,"favref").asString(), "restartDoll")+"\n";
							insert_js(script);
						}
					} else {
						echo(SBR+_("No slide shows defined."));
					}
				}
			} else if ("set_rf_rate".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (str_in_array(Nest.value(_REQUEST,"favref").$(), array("hat_slides"))) {
					String elementid = get_request("elementid");

					CProfile.update(getIdentityBean(), executor,"web.slides.rf_rate.hat_slides", Nest.value(_REQUEST,"favcnt").$(), PROFILE_TYPE_STR, Nest.as(elementid).asLong());

					CArray menu = array();
					CArray submenu = array();

					make_refresh_menu("mainpage", Nest.value(_REQUEST,"favref").asString(), Nest.value(_REQUEST,"favcnt").asDouble(), map("elementid", elementid), menu, submenu, 2);
					echo("page_menu[\"menu_"+Nest.value(_REQUEST,"favref").$()+"\"] = "+rda_jsvalue(menu.get("menu_"+Nest.value(_REQUEST,"favref").asString()))+";\n");
				}
			}

			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor,"web.slides.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
				}
			}
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			includePageFooter(getIdentityBean(), executor);
			return;
		}

		/* Display */
		Map data = map(
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$(),
			"slideshows", array()
		);

		// get slideshows
		CArray<Map> db_slideshows = DBselect(executor,
			"SELECT s.slideshowid,s.name"+
			" FROM slideshows s"
		);
		for(Map slideshow : db_slideshows) {
			if (slideshow_accessible(this.getIdentityBean(), executor, Nest.value(slideshow,"slideshowid").asLong(), PERM_READ)) {
				Nest.value(data,"slideshows",slideshow.get("slideshowid")).$(slideshow);
			}
		};
		order_result(Nest.value(data,"slideshows").asCArray(), "name");

		// get element id
		Nest.value(data,"elementid").$(get_request("elementid", CProfile.get(getIdentityBean(), executor, "web.slides.elementid", null)));

		CProfile.update(getIdentityBean(), executor, "web.slides.elementid", Nest.value(data,"elementid").$(), PROFILE_TYPE_ID);

		if (!isset(Nest.value(data,"slideshows",data.get("elementid")).$())) {
			Map slideshow = reset((CArray<Map>)Nest.value(data,"slideshows").asCArray());
			Nest.value(data,"elementid").$(Nest.value(slideshow,"slideshowid").$());
		}

		// get screen
		Nest.value(data,"screen").$(empty(Nest.value(data,"elementid").$()) ? array() : get_slideshow(getIdentityBean(), executor, Nest.value(data,"elementid").asLong(), 0));
		if (!empty(Nest.value(data,"screen").$())) {
			// get groups and hosts
			if (check_dynamic_items(getIdentityBean(), executor, Nest.value(data,"elementid").asLong(), 1)) {
				Nest.value(data,"isDynamicItems").$(true);

				Nest.value(data,"pageFilter").$(new CPageFilter(getIdentityBean(), executor, map(
					"groups", map(
						"monitored_hosts", true,
						"with_items", true
					),
					"hosts", map(
						"monitored_hosts", true,
						"with_items", true,
						"DDFirstLabel", _("Default")
					),
					"hostid", get_request("hostid", null),
					"groupid", get_request("groupid", null)
				)));
			}

			// get element
			Nest.value(data,"element").$(get_slideshow_by_slideshowid(getIdentityBean(), executor,Nest.value(data,"elementid").asLong()));
			if (Nest.value(data,"screen","delay").asInteger() > 0) {
				Nest.value(data,"element","delay").$(Nest.value(data,"screen","delay").$());
			}

			show_messages();

			// js menu
			Nest.value(data,"menu").$(array());
			Nest.value(data,"submenu").$(array());
			Nest.value(data,"refresh_multiplier").$(CProfile.get(getIdentityBean(), executor,"web.slides.rf_rate.hat_slides", 1, Nest.value(data,"elementid").asLong()));

			if (empty(Nest.value(data,"refresh_multiplier").$())) {
				Nest.value(data,"refresh_multiplier").$(1);
				CProfile.update(getIdentityBean(), executor,"web.slides.rf_rate.hat_slides", Nest.value(data,"refresh_multiplier").$(), PROFILE_TYPE_STR, Nest.value(data,"elementid").asLong());
			}

			make_refresh_menu("mainpage", "hat_slides", Nest.value(data,"refresh_multiplier").asDouble(), map("elementid", Nest.value(data,"elementid").$()), Nest.value(data,"menu").asCArray(), Nest.value(data,"submenu").asCArray(), 2);
		}

		// render view
		CView slidesView = new CView("monitoring.slides", data);
		slidesView.render(getIdentityBean(), executor);
		slidesView.show();
	}

}
