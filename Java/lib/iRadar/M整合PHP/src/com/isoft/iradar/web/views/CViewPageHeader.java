package com.isoft.iradar.web.views;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.basename;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.ACCESS_DENY_PAGE;
import static com.isoft.iradar.inc.Defines.IRADAR_VERSION;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSS;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.RDA_DEFAULT_THEME;
import static com.isoft.iradar.inc.Defines.RDA_GUEST_USER;
import static com.isoft.iradar.inc.Defines.RDA_PAGE_NO_THEME;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.MenuUtil.rda_construct_menu;
import static com.isoft.iradar.inc.MenuUtil.rda_define_menu_restrictions;
import static com.isoft.iradar.inc.ProfilesUtil.get_user_history;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.UsersUtil.getUserTheme;
import static com.isoft.types.CArray.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.isoft.biz.dao.DAOCaller;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.html.CPageHeader;
import com.isoft.types.CArray;
import com.isoft.types.IList;
import com.isoft.types.Mapper.Nest;
import com.isoft.utils.DataSourceUtil;
import com.isoft.web.listener.DataSourceEnum;

public class CViewPageHeader extends CView {
	
	private CViewPageHeader() {
	}
	
	public static void renderAndShow(final IIdentityBean idBean){
		new DAOCaller() {
			@Override
			protected DataSource getDataSource() {
				return DataSourceUtil.getDataSource(DataSourceEnum.IRADAR.getDsName());
			}
			@Override
			protected void execute(SQLExecutor executor) {
				renderAndShow(idBean, executor);
			}
		};
	}
	
	public static void renderAndShow(IIdentityBean idBean, SQLExecutor executor) {
		RadarContext ctx = RadarContext.getContext();
		HttpServletResponse response = ctx.getResponse();
		Map page = ctx.getPage();
		 
		if (!isset(page, "type")) {
			ctx.setPage("type", PAGE_TYPE_HTML);
		}
		
		if (!isset(page, "file")) {
			ctx.setPage("file", basename(ctx));
		}

		String fullscreen = get_request("fullscreen", "0");
		if ("1".equals(fullscreen)) {
			if (!defined("RDA_PAGE_NO_MENU")) {
				define("RDA_PAGE_NO_MENU", 1);
			}
			define("RDA_PAGE_FULLSCREEN", 1);
		}

		rda_define_menu_restrictions(page);
		
		if (!defined("RDA_PAGE_NO_THEME")) {
			define("RDA_PAGE_NO_THEME", false);
		}

		String pageTitle = null;
		int type = (Integer) page.get("type");
		switch (type) {
		case PAGE_TYPE_IMAGE:
			if (!defined("RDA_PAGE_NO_MENU")) {
				define("RDA_PAGE_NO_MENU", 1);
			}
			break;
		case PAGE_TYPE_JS:
			response.setContentType("application/javascript; charset=UTF-8");
			if (!defined("RDA_PAGE_NO_MENU")) {
				define("RDA_PAGE_NO_MENU", 1);
			}
			break;
		case PAGE_TYPE_CSS:
			response.setContentType("text/css; charset=UTF-8");
			if (!defined("RDA_PAGE_NO_MENU")) {
				define("RDA_PAGE_NO_MENU", 1);
			}
			break;
		case PAGE_TYPE_CSV:
			response.setContentType("text/csv; charset=GBK"); //if system uses UTF-8 charsetor setting, it would show uncorrect when openning csv file on windows by excel  
			response.setHeader("Content-Disposition", "attachment; filename="+page.get("file"));
			if (!defined("RDA_PAGE_NO_MENU")) {
				define("RDA_PAGE_NO_MENU", 1);
			}
			break;	
		case PAGE_TYPE_HTML:
		default:
			response.setContentType("text/html; charset=UTF-8");
			pageTitle = isset(page, "title") ? (String) page.get("title") : _("iRadar");
			break;
		}
		
		// construct menu
		List<CCol> main_menu = new ArrayList<CCol>();
		CArray<List<Map>> sub_menus = array();
		boolean denied_page_requested = rda_construct_menu(idBean, executor, main_menu, sub_menus, page);
		
		// render the "Deny access" page
		if (denied_page_requested) {
			access_deny(ACCESS_DENY_PAGE);
		}
		
		if(PAGE_TYPE_HTML == type){
			CPageHeader pageHeader = new CPageHeader(pageTitle);
			pageHeader.addCssInit();
			String css = RDA_DEFAULT_THEME;
			if (!RDA_PAGE_NO_THEME) {
				//TODO: 
//				if (!empty(DBUtil.db $DB['DB'])) {
					Map config = select_config(idBean, executor);
					css = getUserTheme(idBean, executor, CWebUser.data());

					String severityCss = ""+
						".disaster { background: #"+config.get("severity_color_5")+" !important; }      "+
						".high { background: #"+config.get("severity_color_4")+" !important; }          "+
						".average { background: #"+config.get("severity_color_3")+" !important; }       "+
						".warning { background: #"+config.get("severity_color_2")+" !important; }       "+
						".information { background: #"+config.get("severity_color_1")+" !important; }   "+
						".not_classified { background: #"+config.get("severity_color_0")+" !important; }"+
					"";
					pageHeader.addStyle(severityCss);

					// perform iRadar server check only for standard pages
					if ((!defined("RDA_PAGE_NO_MENU") || defined("RDA_PAGE_FULLSCREEN")) && !empty(config.get("server_check_interval"))
							&& !empty(config.get("RDA_SERVER")) && !empty(config.get("RDA_SERVER_PORT"))
							) {
						
						String[] scripts = (String[])page.get("scripts");
						CArray array = (scripts == null)? array(): CArray.valueOf(scripts);
						array.add("servercheck.js");
						page.put("scripts", array.valuesAsString());
					}
//				}
			}
			
			css = CHtml.encode(css);
			pageHeader.addCssFile("styles/themes/"+css+"/main.css");
			pageHeader.addJsFile("js/browsers.js");
			pageHeader.addJsBeforeScripts("var PHP_TZ_OFFSET = 28800;");
			
			int showGuiMessaging = (!defined("RDA_PAGE_NO_MENU") || "1".equals(fullscreen)) ? 1 : 0;
			String path = "jsLoader.action?ver="+IRADAR_VERSION+"&lang=en_US&showGuiMessaging="+showGuiMessaging;
			pageHeader.addJsFile(path);
			String[] scripts = (String[])page.get("scripts");
			if(scripts!=null && scripts.length>0){
				for(String script:scripts){
					path += "&files[]="+script;
				}
				pageHeader.addJsFile(path);
			}
			pageHeader.display();
			
			ctx.write("<body class=\""+css+"\">");
			ctx.write("<div id=\"message-global-wrap\"><div id=\"message-global\"></div></div>");
			
			define("PAGE_HEADER_LOADED", 1);
			if (defined("RDA_PAGE_NO_HEADER")) {
				return;
			}
			
			if(!defined("RDA_PAGE_NO_MENU")){
				CLink help = new CLink(_("Help"), "http://www.i-soft.com.cn/iradar/documentation", "small_font", null, "nosid");
				help.setTarget("_blank");
				CLink support = new CLink(_("Get support"), "http://www.i-soft.com.cn/iradar/support", "small_font", null, "nosid");
				support.setTarget("_blank");
				CLink printview = new CLink(_("Print"), "", "small_font print-link", null, "nosid");
				
				List page_header_r_col = new IList(help, "|", support, "|", printview, "|");
				
				if (!CWebUser.isGuest()) {
					page_header_r_col.add(new CLink(_("Profile"), "profile.action", "small_font", null, "nosid"));
					page_header_r_col.add("|");
				}
				
				if (CWebUser.isGuest()) {
					page_header_r_col.add(new CLink(_("Login"), "index.action?reconnect=1", "small_font", null, null));
				} else {
//					boolean chck= "authentication.action".equals(ctx.getPage("file"))
//							&& (php.isset(ctx._REQUEST(), "save") && php.isset(ctx._REQUEST(), "config"));
//					if((chck && RDA_AUTH_HTTP == (Integer)ctx._REQUEST("config"))
//						|| (!chck)){
//						
//					}
					//TODO
					page_header_r_col.add(new CLink(_("Logout"), "index.action?reconnect=1", "small_font", null, null));
				}
				
				CLink logo = new CLink(new CDiv(SPACE, "iradar_logo"),"http://www.i-soft.com.cn",null,"nosid");
				logo.setTarget("_blank");
				
				CCol[] top_page_row = new CCol[]{
						new CCol(logo, "page_header_l"),
						new CCol(page_header_r_col, "maxwidth page_header_r")
				};
				
				CTable table = new CTable(null, "maxwidth page_header");
				table.setCellSpacing(0);
				table.setCellPadding(5);
				table.addRow(top_page_row);
				table.show();
				
				CTable menu_table = new CTable(null, "menu pointer");
				menu_table.setCellSpacing(0);
				menu_table.setCellPadding(5);
				menu_table.addRow(main_menu.toArray(new CCol[0]));
				
				// 1st level menu
				table = new CTable(null, "maxwidth");
				CCol r_col = new CCol(null, "right");
				r_col.setAttribute("style", "line-height: 1.8em;");
				table.addRow(new Object[]{menu_table, r_col});
				
				CDiv page_menu = new CDiv(null, "textwhite");
				page_menu.setAttribute("id", "mmenu");
				page_menu.addItem(table);
				
				// 2nd level menu
				CTable sub_menu_table = new CTable(null, "sub_menu maxwidth ui-widget-header");
				List<CDiv> menu_divs = new ArrayList<CDiv>();
				boolean menu_selected = false;
				for(Entry<Object, List<Map>> e:sub_menus.entrySet()){
					Object label = e.getKey();
					List<Map> sub_menu = e.getValue();
					List sub_menu_row = new ArrayList();
					for (Map sub_page : sub_menu) {
						if(empty((String)sub_page.get("menu_text"))){
							sub_page.put("menu_text", SPACE);
						}
						sub_page.put("menu_url", (String)sub_page.get("menu_url")+"?ddreset=1");
						Object sub_menu_item = new CLink(sub_page.get("menu_text"),(String)sub_page.get("menu_url"),(String)sub_page.get("class")+" nowrap");
						if((Boolean)sub_page.get("selected")){
							sub_menu_item = new CSpan(sub_menu_item, "active nowrap");
						}
						sub_menu_row.add(sub_menu_item);
						sub_menu_row.add(new CSpan(SPACE+" |"+SPACE, "divider"));
					}
					
					if(!sub_menu_row.isEmpty()){
						sub_menu_row.remove(sub_menu_row.size()-1);
					}
					
					CDiv sub_menu_div = new CDiv(sub_menu_row.toArray());
					sub_menu_div.setAttribute("id", "sub_"+label);
					sub_menu_div.addAction("onmouseover", "javascript: MMenu.submenu_mouseOver();");
					sub_menu_div.addAction("onmouseout", "javascript: MMenu.mouseOut();");
					
					if(isset(page, "menu") && page.get("menu").equals(label)){
						menu_selected = true;
						sub_menu_div.setAttribute("style", "display: block;");
						insert_js("MMenu.def_label = '"+label+"';");
					} else {
						sub_menu_div.setAttribute("style", "display: none;");
					}
					
					menu_divs.add(sub_menu_div);
				}
				
				CDiv sub_menu_div = new CDiv(SPACE);
				sub_menu_div.setAttribute("id", "sub_empty");
				sub_menu_div.setAttribute("style", "display: "+(menu_selected ? "none;" : "block;"));
				menu_divs.add(sub_menu_div);
				
				Object search_div = null;				
				if (!Nest.value(page,"file").asString().equals("index.action") && Nest.value(CWebUser.data(),"userid").asLong() > 0L) {
					CView searchForm = new CView("general.search", new CArray());
					search_div = searchForm.render(idBean, executor);
				}
				
				sub_menu_table.addRow(array(menu_divs, search_div));
				page_menu.addItem(sub_menu_table);
				page_menu.show();
			}
		}
		
		// create history
		if (isset(page,"hist_arg") && !RDA_GUEST_USER.equals(CWebUser.get("alias")) && type == PAGE_TYPE_HTML && !defined("RDA_PAGE_NO_MENU")) {
			CTable table = new CTable(null, "history left");
			table.addRow(new CRow(array(
				new CCol(_("History")+":", "caption"),
				get_user_history(idBean, executor)
			)));
			table.show();
		} else if (type == PAGE_TYPE_HTML && !defined("RDA_PAGE_NO_MENU")) {
			echo(SBR);
		}
	}
}
