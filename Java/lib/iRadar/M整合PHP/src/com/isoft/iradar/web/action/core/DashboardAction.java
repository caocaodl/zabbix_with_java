package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_fill;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.ksort;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.inc.BlocksUtil.make_discovery_status;
import static com.isoft.iradar.inc.BlocksUtil.make_favorite_graphs;
import static com.isoft.iradar.inc.BlocksUtil.make_favorite_maps;
import static com.isoft.iradar.inc.BlocksUtil.make_favorite_screens;
import static com.isoft.iradar.inc.BlocksUtil.make_graph_menu;
import static com.isoft.iradar.inc.BlocksUtil.make_graph_submenu;
import static com.isoft.iradar.inc.BlocksUtil.make_hoststat_summary;
import static com.isoft.iradar.inc.BlocksUtil.make_latest_issues;
import static com.isoft.iradar.inc.BlocksUtil.make_screen_menu;
import static com.isoft.iradar.inc.BlocksUtil.make_screen_submenu;
import static com.isoft.iradar.inc.BlocksUtil.make_status_of_rda;
import static com.isoft.iradar.inc.BlocksUtil.make_sysmap_menu;
import static com.isoft.iradar.inc.BlocksUtil.make_sysmap_submenu;
import static com.isoft.iradar.inc.BlocksUtil.make_system_status;
import static com.isoft.iradar.inc.BlocksUtil.make_webmon_overview;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.DEFAULT_LATEST_ISSUES_CNT;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.DiscoveryUtil.check_right_on_discovery;
import static com.isoft.iradar.inc.FuncsUtil.add_doll_objects;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_update_doll_script;
import static com.isoft.iradar.inc.FuncsUtil.make_refresh_menu;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.rda_value2array;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.JsUtil.getJsTemplate;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.Factory;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CUIWidget;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class DashboardAction extends RadarBaseAction {
	
	private CArray dashconf;
	
	@Override
	protected void doInitPage() {
		page("title", _("Dashboard"));
		page("file", "dashboard.action");
		page("hist_arg", new String[] {});
		page("scripts", new String[] {"class.pmaster.js"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"view_style",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"type",			array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"output",		array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"jsscriptid",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"favcnt",		array(T_RDA_INT, O_OPT, null,	null,		null),
			"pmasterid",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"favaction",	array(T_RDA_STR, O_OPT, P_ACT,	IN("'add','remove','refresh','flop','sort'"), null),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favaction})&&(\"flop\"=={favaction})"),
			"favdata",		array(T_RDA_STR, O_OPT, null,	null,		null)
		);
		check_fields(getIdentityBean(), fields);
	}
	
	@Override
	protected void doPageFilter(SQLExecutor executor) {
		dashconf = array();
		Nest.value(dashconf,"groupids").$(null);
		Nest.value(dashconf,"maintenance").$(null);
		Nest.value(dashconf,"severity").$(null);
		Nest.value(dashconf,"extAck").$(0);
		Nest.value(dashconf,"filterEnable").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.filter.enable", 0));
		if (Nest.value(dashconf,"filterEnable").asInteger() == 1) {
			// groups
			Nest.value(dashconf,"grpswitch").$(CProfile.get(getIdentityBean(), executor, "web.dashconf.groups.grpswitch", 0));
			if (Nest.value(dashconf,"grpswitch").asInteger() == 0) {
				Nest.value(dashconf,"groupids").$(null); // null mean all groups
			} else {
				Nest.value(dashconf,"groupids").$(rda_objectValues(CFavorite.get(getIdentityBean(), executor, "web.dashconf.groups.groupids"), "value"));
				CArray hideGroupIds = rda_objectValues(CFavorite.get(getIdentityBean(), executor, "web.dashconf.groups.hide.groupids"), "value");

				if (!empty(hideGroupIds)) {
					// get all groups if no selected groups defined
					if (empty(Nest.value(dashconf,"groupids").$())) {
						CHostGroupGet hgoptions = new CHostGroupGet();
						hgoptions.setOutput(new String[]{"groupid"});
						CArray groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
						Nest.value(dashconf,"groupids").$(rda_objectValues(groups, "groupid"));
					}

					Nest.value(dashconf,"groupids").$(array_diff(Nest.value(dashconf,"groupids").asCArray(), hideGroupIds));

					// get available hosts
					CHostGet hoptions = new CHostGet();
					hoptions.setGroupIds(Nest.array(dashconf,"groupids").asLong());
					hoptions.setOutput(new String[]{"hostid"});
					CArray<Map> availableHosts = API.Host(getIdentityBean(), executor).get(hoptions);
					CArray availableHostIds = rda_objectValues(availableHosts, "hostid");

					hoptions = new CHostGet();
					hoptions.setGroupIds(hideGroupIds.valuesAsLong());
					hoptions.setOutput(new String[]{"hostid"});
					CArray<Map> disabledHosts = API.Host(getIdentityBean(), executor).get(hoptions);
					CArray disabledHostIds = rda_objectValues(disabledHosts, "hostid");

					Nest.value(dashconf,"hostids").$(array_diff(availableHostIds, disabledHostIds));
				} else {
					if (empty(Nest.value(dashconf,"groupids").$())) {
						Nest.value(dashconf,"groupids").$(null); // null mean all groups
					}
				}
			}

			// hosts
			int maintenance = Nest.as(CProfile.get(getIdentityBean(), executor, "web.dashconf.hosts.maintenance", 1)).asInteger();
			Nest.value(dashconf,"maintenance").$(maintenance == 0 ? 0 : null);

			// triggers
			String severity = Nest.as(CProfile.get(getIdentityBean(), executor, "web.dashconf.triggers.severity", null)).asString();
			Nest.value(dashconf,"severity").$(rda_empty(severity) ? null : explode(";", severity));
			Nest.value(dashconf,"severity").$(rda_toHash(Nest.value(dashconf,"severity").$()));

			Map config = select_config(getIdentityBean(), executor);
			Nest.value(dashconf,"extAck").$(Nest.value(config,"event_ack_enable").asBoolean() ? CProfile.get(getIdentityBean(), executor, "web.dashconf.events.extAck", 0) : 0);
		}
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj")) {
			Nest.value(_REQUEST,"pmasterid").$(get_request("pmasterid", "mainpage"));

			if ("hat".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if ("flop".equals(Nest.value(_REQUEST,"favaction").asString())) {
					String widgetName = substr(Nest.value(_REQUEST,"favref").asString(), 4);
					CProfile.update(getIdentityBean(), executor, "web.dashboard.widget."+widgetName+".state", Nest.value(_REQUEST,"favstate").asInteger(), PROFILE_TYPE_INT);
				} else if ("sort".equals(get_request("favaction"))) {
					Map<?, Map<?, ?>> favdata = CJs.decodeJson(get_request("favdata"));
					
					for(Entry<?, Map<?, ?>> entry: favdata.entrySet()) {
						int col = Nest.as(entry.getKey()).asInteger();
						Map<?, ?> column = entry.getValue();
						
						for(Entry<?, ?> entry_column: column.entrySet()) {
							int row = Nest.as(entry_column.getKey()).asInteger();
							String widgetName = Nest.as(entry_column.getValue()).asString();
							widgetName = substr(widgetName, 4, -7);
							CProfile.update(getIdentityBean(), executor,"web.dashboard.widget."+widgetName+".col", col, PROFILE_TYPE_INT);
							CProfile.update(getIdentityBean(), executor,"web.dashboard.widget."+widgetName+".row", row, PROFILE_TYPE_INT);
						}
					}
				} else if ("refresh".equals(Nest.value(_REQUEST,"favaction").asString())) {
					String _favref = Nest.value(_REQUEST,"favref").asString();
					if("hat_syssum".equals(_favref)) {
						CDiv syssum = make_system_status(this.getIdentityBean(), executor, dashconf);
						syssum.show();
					}else if("hat_hoststat".equals(_favref)) {
						CDiv hoststat = make_hoststat_summary(this.getIdentityBean(), executor, dashconf);
						hoststat.show();
					}else if("hat_stsrda".equals(_favref)) {
						CDiv stsrda = make_status_of_rda(getIdentityBean(), executor);
						stsrda.show();
					}else if("hat_lastiss".equals(_favref)) {
						CDiv lastiss = make_latest_issues(getIdentityBean(), executor, dashconf);
						lastiss.show();
					}else if("hat_webovr".equals(_favref)) {
						CDiv webovr = make_webmon_overview(getIdentityBean(), executor, dashconf);
						webovr.show();
					}else if("hat_dscvry".equals(_favref)) {
						CDiv dscvry = make_discovery_status(this.getIdentityBean(), executor);
						dscvry.show();
					}
				}
			}

			if ("set_rf_rate".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (str_in_array(Nest.value(_REQUEST,"favref").$(), array("hat_syssum", "hat_stsrda", "hat_lastiss", "hat_webovr", "hat_dscvry", "hat_hoststat"))) {
					String widgetName = substr(Nest.value(_REQUEST,"favref").asString(), 4);
					CProfile.update(getIdentityBean(), executor,"web.dashboard.widget."+widgetName+".rf_rate", Nest.value(_REQUEST,"favcnt").asInteger(), PROFILE_TYPE_INT);
					Nest.value(_REQUEST,"favcnt").$(CProfile.get(getIdentityBean(), executor, "web.dashboard.widget."+widgetName+".rf_rate", 60));

					echo (get_update_doll_script("mainpage", Nest.value(_REQUEST,"favref").asString(), "frequency", Nest.value(_REQUEST,"favcnt").asString())
						+get_update_doll_script("mainpage", Nest.value(_REQUEST,"favref").asString(), "stopDoll")
						+get_update_doll_script("mainpage", Nest.value(_REQUEST,"favref").asString(), "startDoll")
					);

					CArray menu = array();
					CArray submenu = array();
					make_refresh_menu("mainpage", Nest.value(_REQUEST,"favref").asString(), Nest.value(_REQUEST,"favcnt").asDouble(), null, menu, submenu);

					echo("page_menu[\"menu_"+_REQUEST.get("favref")+"\"] = "+rda_jsvalue(menu.get("menu_"+_REQUEST.get("favref")))+";");
				}
			}

			if (str_in_array(Nest.value(_REQUEST,"favobj").$(), array("itemid", "graphid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").$())) {
					rda_value2array(Nest.value(_REQUEST,"favid"));

					for(Object sourceid: Nest.value(_REQUEST,"favid").asCArray()) {
						result = CFavorite.add(getIdentityBean(), executor, "web.favorite.graphids", Nest.as(sourceid).asLong(), Nest.value(_REQUEST,"favobj").asString());
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").$())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.graphids", Nest.value(_REQUEST,"favid").asLong(), Nest.value(_REQUEST,"favobj").asString());
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					Object innerHTML = make_favorite_graphs(this.getIdentityBean(), executor);
					innerHTML = innerHTML.toString();
					echo("$(\"hat_favgrph\").update("+rda_jsvalue(innerHTML)+");");
					echo( "page_submenu[\"menu_graphs\"] = "+rda_jsvalue(make_graph_submenu(this.getIdentityBean(), executor))+";" );
				}
			}

			if ("sysmapid".equals(Nest.value(_REQUEST,"favobj").asString())) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					rda_value2array(Nest.value(_REQUEST,"favid"));
					for(Object sourceid: Nest.value(_REQUEST,"favid").asCArray()) {
						result = CFavorite.add(getIdentityBean(), executor, "web.favorite.sysmapids", Nest.as(sourceid).asInteger(), Nest.value(_REQUEST,"favobj").asString());
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.sysmapids", Nest.value(_REQUEST,"favid").asInteger(), Nest.value(_REQUEST,"favobj").asString());
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					Object innerHTML = make_favorite_maps(this.getIdentityBean(), executor);
					innerHTML = innerHTML.toString();
					echo( "$(\"hat_favmap\").update("+rda_jsvalue(innerHTML)+");" );
					echo("page_submenu[\"menu_sysmaps\"] = "+rda_jsvalue(make_sysmap_submenu(this.getIdentityBean(), executor))+";");
				}
			}

			if (str_in_array(Nest.value(_REQUEST,"favobj").$(), array("screenid", "slideshowid"))) {
				boolean result = false;
				if ("add".equals(Nest.value(_REQUEST,"favaction").asString())) {
					rda_value2array(Nest.value(_REQUEST,"favid"));
					for(Object sourceid: Nest.value(_REQUEST,"favid").asCArray()) {
						result = CFavorite.add(getIdentityBean(), executor, "web.favorite.screenids", Nest.as(sourceid).asInteger(), Nest.value(_REQUEST,"favobj").asString());
					}
				} else if ("remove".equals(Nest.value(_REQUEST,"favaction").asString())) {
					result = CFavorite.remove(getIdentityBean(), executor, "web.favorite.screenids", Nest.value(_REQUEST,"favid").asInteger(), Nest.value(_REQUEST,"favobj").asString());
				}

				if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS && result) {
					Object innerHTML = make_favorite_screens(this.getIdentityBean(), executor);
					innerHTML = innerHTML.toString();
					echo("$(\"hat_favscr\").update("+rda_jsvalue(innerHTML)+");");
					echo("page_submenu[\"menu_screens\"] = "+rda_jsvalue(make_screen_submenu(this.getIdentityBean(), executor))+";");
				}
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return ;
		}

		/* Display */
		CWidget dashboardWidget = new CWidget("dashboard_wdgt");
		dashboardWidget.setClass("header");
		dashboardWidget.addHeader(_("PERSONAL DASHBOARD"), array(
			new CIcon(
				_s("Configure (Filter %s)", Nest.value(dashconf,"filterEnable").asBoolean() ? _("Enabled") : _("Disabled")),
				Nest.value(dashconf,"filterEnable").asBoolean() ? "iconconfig_hl" : "iconconfig",
				"document.location = 'dashconf.action';"
			),
			SPACE,
			get_icon(getIdentityBean(), executor, "fullscreen", map("fullscreen", Nest.value(_REQUEST,"fullscreen").$())))
		);

		// js menu arrays
		CArray menu = array();
		CArray submenu = array();
		make_graph_menu(this.getIdentityBean(), executor,menu, submenu);
		make_sysmap_menu(this.getIdentityBean(), executor,menu, submenu);
		make_screen_menu(this.getIdentityBean(), executor,menu, submenu);

		make_refresh_menu("mainpage", "hat_syssum", Nest.as(CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.syssum.rf_rate", 60)).asDouble(), null, menu, submenu);
		make_refresh_menu("mainpage", "hat_hoststat", Nest.as(CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.hoststat.rf_rate", 60)).asDouble(), null, menu, submenu);
		make_refresh_menu("mainpage", "hat_stsrda", Nest.as(CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.stsrda.rf_rate", 60)).asDouble(), null, menu, submenu);
		make_refresh_menu("mainpage", "hat_lastiss", Nest.as(CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.lastiss.rf_rate", 60)).asDouble(), null, menu, submenu);
		make_refresh_menu("mainpage", "hat_webovr", Nest.as(CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.webovr.rf_rate", 60)).asDouble(), null, menu, submenu);
		make_refresh_menu("mainpage", "hat_dscvry", Nest.as(CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.dscvry.rf_rate", 60)).asDouble(), null, menu, submenu);

		insert_js("var page_menu="+rda_jsvalue(menu)+";\n"+"var page_submenu="+rda_jsvalue(submenu)+";\n");

		/* Columns */
		CArray<CArray> columns = array_fill(0, 3, new Factory() {
			@Override public Object create() {
				return array();
			}
		});

		// refresh tab
		 CArray refresh_tab = array(
			map("id" , "hat_syssum", "frequency" , CProfile.get(getIdentityBean(), executor,"web.dashboard.widget.syssum.rf_rate", 120)),
			map("id" , "hat_stsrda", "frequency" , CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.stsrda.rf_rate", 120)),
			map("id" , "hat_lastiss", "frequency" , CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.lastiss.rf_rate", 60)),
			map("id" , "hat_webovr", "frequency" , CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.webovr.rf_rate", 60)),
			map("id" , "hat_hoststat", "frequency" , CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.hoststat.rf_rate", 60))
		);

		// favorite graphs
		CIcon graph_menu = get_icon(getIdentityBean(), executor, "menu", map("menu" , "graphs"));
		CUIWidget fav_grph = new CUIWidget("hat_favgrph", make_favorite_graphs(this.getIdentityBean(), executor), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favgrph.state", 1));
		fav_grph.setHeader(_("Favourite graphs"), array(graph_menu));
		fav_grph.setFooter(new CLink(_("Graphs")+" &raquo;", "charts.action", "highlight"), true);
		Object col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favgrph.col", "0");
		Object row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favgrph.row", "0");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, fav_grph);
		} else {
			Nest.value(columns, col).push( fav_grph );
		}

		// favorite screens
		CIcon screen_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "screens"));
		CUIWidget fav_scr = new CUIWidget("hat_favscr", make_favorite_screens(this.getIdentityBean(), executor), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favscr.state", 1));
		fav_scr.setHeader(_("Favourite screens"), array(screen_menu));
		fav_scr.setFooter(new CLink(_("Screens")+" &raquo;", "screens.action", "highlight"), true);
		col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favscr.col", "0");
		row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favscr.row", "1");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, fav_scr);
		} else {
			Nest.value(columns, col).push( fav_scr );
		}

		// favorite sysmaps
		CIcon sysmap_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "sysmaps"));
		CUIWidget fav_maps = new CUIWidget("hat_favmap", make_favorite_maps(this.getIdentityBean(), executor), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favmap.state", 1));
		fav_maps.setHeader(_("Favourite maps"), array(sysmap_menu));
		fav_maps.setFooter(new CLink(_("Maps")+" &raquo;", "maps.action", "highlight"), true);
		col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favmap.col", "0");
		row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.favmap.row", "2");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, fav_maps);
		} else {
			Nest.value(columns, col).push( fav_maps );
		}
		
		// status of rda
		if (CWebUser.getType() == USER_TYPE_SUPER_ADMIN) {
			CIcon refresh_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "hat_stsrda"));
			CUIWidget rdaStatus = new CUIWidget("hat_stsrda", new CSpan(_("Loading..."), "textcolorstyles"), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.stsrda.state", 1));
			rdaStatus.setHeader(_("Status of iRadar"), array(refresh_menu));
			rdaStatus.setFooter(new CDiv(SPACE, "textwhite", "hat_stsrda_footer"));
			col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.stsrda.col", "1");
			row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.stsrda.row", "0");
			if (!isset(columns.getNested(col, row))) {
				columns.put(col, row, rdaStatus);
			} else {
				Nest.value(columns, col).push( rdaStatus );
			}
		}

		// system status
		CIcon refresh_menu = get_icon(getIdentityBean(), executor, "menu", map("menu" , "hat_syssum"));
		CUIWidget sys_stat = new CUIWidget("hat_syssum", new CSpan(_("Loading..."), "textcolorstyles"), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.syssum.state", 1));
		sys_stat.setHeader(_("System status"), array(refresh_menu));
		sys_stat.setFooter(new CDiv(SPACE, "textwhite", "hat_syssum_footer"));
		col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.syssum.col", "1");
		row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.syssum.row", "1");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, sys_stat);
		} else {
			Nest.value(columns, col).push( sys_stat );
		}

		// host status
		refresh_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "hat_hoststat"));
		CUIWidget hoststat = new CUIWidget("hat_hoststat", new CSpan(_("Loading..."), "textcolorstyles"), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.hoststat.state", 1));
		hoststat.setHeader(_("Host status"), array(refresh_menu));
		hoststat.setFooter(new CDiv(SPACE, "textwhite", "hat_hoststat_footer"));
		col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.hoststat.col", "1");
		row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.hoststat.row", "2");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, hoststat);
		} else {
			Nest.value(columns, col).push( hoststat );
		}

		// last issues
		refresh_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "hat_lastiss"));
		CUIWidget lastiss = new CUIWidget("hat_lastiss", new CSpan(_("Loading..."), "textcolorstyles"), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.lastiss.state", 1));
		lastiss.setHeader(_n("Last %1$d issue", "Last %1$d issues", DEFAULT_LATEST_ISSUES_CNT), array(refresh_menu));
		lastiss.setFooter(new CDiv(SPACE, "textwhite", "hat_lastiss_footer"));
		col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.lastiss.col", "1");
		row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.lastiss.row", "3");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, lastiss);
		} else {
			Nest.value(columns, col).push( lastiss );
		}

		// web monitoring
		refresh_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "hat_webovr"));
		CUIWidget web_mon = new CUIWidget("hat_webovr", new CSpan(_("Loading..."), "textcolorstyles"), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.webovr.state", 1));
		web_mon.setHeader(_("Web monitoring"), array(refresh_menu));
		web_mon.setFooter(new CDiv(SPACE, "textwhite", "hat_webovr_footer"));
		col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.webovr.col", "1");
		row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.webovr.row", "4");
		if (!isset(columns.getNested(col, row))) {
			columns.put(col, row, web_mon);
		} else {
			Nest.value(columns, col).push( web_mon );
		}

		// discovery info
		Map drules = DBfetch(DBselect(executor,
				"SELECT COUNT(d.druleid) AS cnt"+
				" FROM drules d"+
				" WHERE d.status="+DRULE_STATUS_ACTIVE
		));
		if (Nest.value(drules,"cnt").asInteger() > 0 && check_right_on_discovery()) {
			refresh_tab.add( map("id", "hat_dscvry", "frequency", CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.dscvry.rf_rate", 60)) );

			refresh_menu = get_icon(getIdentityBean(), executor, "menu", map("menu", "hat_dscvry"));
			CUIWidget dcvr_mon = new CUIWidget("hat_dscvry", new CSpan(_("Loading..."), "textcolorstyles"), (Integer)CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.dscvry.state", 1));
			dcvr_mon.setHeader(_("Discovery status"), array(refresh_menu));
			dcvr_mon.setFooter(new CDiv(SPACE, "textwhite", "hat_dscvry_footer"));
			col = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.dscvry.col", "1");
			row = CProfile.get(getIdentityBean(), executor, "web.dashboard.widget.dscvry.row", "5");
			if (!isset(columns.getNested(col, row))) {
				columns.put(col, row, dcvr_mon);
			} else {
				Nest.value(columns, col).push( dcvr_mon );
			}
		}

		add_doll_objects(refresh_tab);
		for(CArray val: columns) {
			ksort(val);
		}

		CTable dashboardTable = new CTable();
		dashboardTable.addRow(array(new CDiv(columns.get(0), "column"), new CDiv(columns.get(1), "column"), new CDiv(columns.get(2), "column")), "top");

		dashboardWidget.addItem(dashboardTable);
		dashboardWidget.show();

		// activating blinking
		rda_add_post_js("jqBlink.blink();");
		echo(getJsTemplate("javascript_for_dashboard"));
	}

}
