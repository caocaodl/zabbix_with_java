package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_fill;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.ksort;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.JsUtil.getJsTemplate;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.web.action.TDashboardUtil.make_Announcement;
import static com.isoft.iradar.web.action.TDashboardUtil.make_event;
import static com.isoft.iradar.web.action.TDashboardUtil.make_monStaSts;
import static com.isoft.iradar.web.action.TDashboardUtil.make_resUsed;
import static com.isoft.iradar.web.action.TDashboardUtil.make_todayArtSts;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import org.apache.commons.collections.Factory;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CUIWidget;
import com.isoft.iradar.tags.CWidget;
import com.isoft.types.CArray;

public class TDashboardAction extends RadarBaseAction{

	protected void doInitPage() {
		page("title", _("Dashboard"));
		page("file", "TDashboard.action");
		page("hist_arg", new String[] {});
		page("scripts", new String[] {"class.pmaster.js"});
		page("css", new String[] {"dashboard.css","tenant/isoft.tenant.percent.css"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("js", new String[] {"imon/dashboard.js", 
				 "FusionCharts/widgets/FusionCharts.js",
				 "FusionCharts/commonFusionCharts.js",
				 "FusionCharts/widgets/fusioncharts.widgets.js",
				 "FusionCharts/FusionCharts.jqueryplugin.js",
				 "FusionCharts/T_dashboard.js"});	//引进仪表盘功能所需function widgetsXT js
		
	}

	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"type",			array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null)
		);
		check_fields(getIdentityBean(), fields);		
	}

	protected void doPermissions(SQLExecutor executor) {
	
	}

	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	protected void doAction(SQLExecutor executor) {
		
		/* Display */
		CWidget dashboardWidget = new CWidget("dashboard_wdgt");
		dashboardWidget.setClass("header");
		CDiv hidediv=new CDiv("","display:none","maskid");
		dashboardWidget.addItem(hidediv);
		CDiv headerActions = EnhancesUtil.get_table_header_actions(_("PERSONAL DASHBOARD"),"");
		dashboardWidget.addItem(headerActions);
		// js menu arrays
		CArray menu = array();
		CArray submenu = array();		
		insert_js("var page_menu="+rda_jsvalue(menu)+";\n"+"var page_submenu="+rda_jsvalue(submenu)+";\n");

		/* Columns */
		CArray<CArray> columns = array_fill(0, 3, new Factory() {
			 public Object create() {
				return array();
			}
		});

		//今日监控项目统计
		CUIWidget todayArtSts = new CUIWidget("hat_todayArtSts", make_todayArtSts(executor, getIdentityBean()));
		todayArtSts.setHeader("今日监控项目统计","");
		todayArtSts.setFooter(new CDiv(SPACE, "textwhite", "hat_todayArtSts_footer"));
		columns.put(0, 0, todayArtSts); //col row obj
		
		//服务应用监控项目状态统计
		CUIWidget monStaSts = new CUIWidget("hat_monStaSts", make_monStaSts(executor, getIdentityBean()));
		monStaSts.setHeader("服务应用监控项目状态统计","");
		monStaSts.setFooter(new CDiv(SPACE, "textwhite", "hat_monStaSts_footer"));
		columns.put(1, 0, monStaSts);	
		
		//资源使用情况
		CIcon resUsed_menu = get_icon(getIdentityBean(), executor, "menu", map("menu" , "hat_resUsed"));
		CUIWidget resUsed = new CUIWidget("hat_resUsed",  make_resUsed(getIdentityBean(),executor), 1);
		resUsed.setHeader("资源使用情况", array(resUsed_menu));
		resUsed.setFooter(new CDiv(SPACE, "textwhite", "hat_resUsed_footer"));
		CCol colre = new CCol(array(new CDiv(resUsed)), "", 3);
		//columns.put(2, 0, resUsed);	
		
		
		//公告
		CIcon Announcement_menu = get_icon(getIdentityBean(), executor, "menu", map("menu" , "hat_Announcement"));
		CUIWidget Announcement = new CUIWidget("hat_Announcement", make_Announcement(executor), 1);
		Announcement.setHeader("公告", array(Announcement_menu));
		Announcement.setFooter(new CDiv(SPACE, "textwhite", "hat_Announcement_footer"));
		CCol colan = new CCol(array(new CDiv(Announcement)), "", 3);
		
		//最近前5个事件
		CIcon noRepFault_menu = get_icon(getIdentityBean(), executor, "menu", map("menu" , "hat_noRepFault"));
		CUIWidget noRepFault = new CUIWidget("hat_noRepFault", make_event(executor, getIdentityBean(), 1), 1);
		noRepFault.setHeader(_("Top5 events"), array(noRepFault_menu));
		noRepFault.setFooter(new CDiv(SPACE, "textwhite", "hat_noRepFault_footer"));
		CCol colnrf = new CCol(array(new CDiv(noRepFault)), "", 3);
		
		for(CArray val: columns) {
			ksort(val);
		}
		CFormList cformzong=new CFormList();
		cformzong.addClass("tenant_first");
		CForm cformleft=new CForm();
		CForm cformright=new CForm();
		CTable dashboardTableleft = new CTable();
		CTable dashboardTableright = new CTable();
		dashboardTableleft.addRow(array(new CDiv(columns.get(0)), new CDiv(columns.get(1))), "top");
		dashboardTableright.addRow(colan);
		dashboardTableleft.addRow(colre);
		dashboardTableleft.addRow(colnrf);
		cformleft.addItem(dashboardTableleft);
		cformright.addItem(dashboardTableright);
		cformzong.addRow(cformleft,cformright);
		
		dashboardWidget.addItem(cformzong);
		dashboardWidget.show();

		// activating blinking
		rda_add_post_js("jqBlink.blink();");
		echo(getJsTemplate("javascript_for_dashboard"));		
	}

}
