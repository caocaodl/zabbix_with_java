package com.isoft.iradar.web.views;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.basename;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
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
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.MenuUtil.rda_construct_menu;
import static com.isoft.iradar.inc.MenuUtil.rda_define_menu_restrictions;
import static com.isoft.iradar.inc.ProfilesUtil.get_user_history;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.UsersUtil.getUserTheme;
import static com.isoft.types.CArray.array;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.isoft.biz.dao.DAOCaller;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.ViewsUtil;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.html.CPageHeader;
import com.isoft.model.FuncItem;
import com.isoft.types.CArray;
import com.isoft.utils.CacheUtil;
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
				renderAndShow(idBean,executor);
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
			try {
				response.setHeader("Content-Disposition", "attachment; filename="+ new String(String.valueOf(page.get("file")).getBytes("gb2312"), "ISO8859-1"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
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
//			access_deny(ACCESS_DENY_PAGE); //修复因路径没有在iradar配置，而导致被拒绝的问题
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

					pageHeader.addStyle(BlocksUtil.getSeverityCss(idBean, executor));
					
					String style = (String)page.get("style");
					if(!Cphp.empty(style)) {
						pageHeader.addStyle(style);
					}
					

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
			css = "originalblue";
			pageHeader.addCssFile("styles/themes/"+css+"/main.css");
			pageHeader.addCssFile("styles/themes/"+css+"/iradar.css");
			
			//增加页面上添加自定义样式的功能
			String[] cssFiles = (String[])page.get("css");
			if(cssFiles!=null && cssFiles.length>0){
				for(String cssFile:cssFiles){
					pageHeader.addCssFile("styles/themes/"+css+"/"+cssFile);
				}
			}
			
			
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
			
			//增加页面上添加自定义脚本的功能
			String[] jsFiles = (String[])page.get("js");
			if(jsFiles!=null && jsFiles.length>0){
				for(String jsFile:jsFiles){
					pageHeader.addJsFile("js/"+jsFile);
				}
			}
			
			if(!defined("RDA_PAGE_NO_MENU")){
				pageHeader.addCssFile("styles/themes/originalblue/menu/menu.css");
			}
			
			pageHeader.display();
			
			ctx.write("<body class=\""+css+"\">");
			ctx.write("<div id=\"message-global-wrap\"><div id=\"message-global\"></div></div>");
			
			if(!defined("RDA_PAGE_NO_MENU")){
				ViewsUtil.includeSubView("rda_menu", renderMenu((IdentityBean)idBean));
			}
			
			define("PAGE_HEADER_LOADED", 1);
			if (defined("RDA_PAGE_NO_HEADER")) {
				return;
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
	
	
	private static Map renderMenu(IdentityBean idBean) {
		String ctxPath = RadarContext.getContextPath();
		String curUri = RadarContext.getContext().getRequest().getRequestURI();
		curUri = curUri.substring(ctxPath.length());
		
		int mask = 0;
		if (idBean != null && idBean.getTenantRole() != null) {
			mask = idBean.getTenantRole().magic();
		}
		boolean isAdmin = idBean.isAdmin();
		
		int mainIndex = 0, activeMainIndex = 0;
		CArray mainNavs = array();
		CArray subNavs = array();
		
		Map<String, Boolean> permModuleIds = idBean.getPermModuleIds();
		List<FuncItem> funcList = CacheUtil.getNavFuncList();
		for (FuncItem item : funcList) {
			if ((Integer.valueOf(item.getRole()) & mask) == 0) {
				continue;
			}
			if(!item.isEntrance()){
				continue;
			}
			if (!isAdmin) {
				if (permModuleIds == null || !permModuleIds.containsKey(item.getId())) {
					continue;
				}
			}
			mainNavs.push(item);
			
			if(item.contains(curUri)) {
				for(FuncItem subItem: item.getSubFuncList()) {
					encodeSubMenu(subNavs, subItem, mask, isAdmin, permModuleIds);
				}
				activeMainIndex = mainIndex;
			}
			mainIndex++;
		}
		
		return CArray.map(
			"idBean", idBean,
			"ctxPath", ctxPath,
			"curUri", curUri,
			"activeMainIndex", activeMainIndex,
			"main", mainNavs,
			"sub", subNavs
		);
	}
	
	private static boolean encodeSubMenu(CArray subNavs, FuncItem item, int mask, boolean isAdmin, Map<String, Boolean> permModuleIds) {
		if ((Integer.valueOf(item.getRole()) & mask) == 0) {
			return false;
		}
		if(!item.isEntrance()){
			return false;
		}
		if (!isAdmin) {
			if (permModuleIds == null || !permModuleIds.containsKey(item.getId())) {
				return false;
			}
		}
		
		subNavs.push(item);
		
		return true;
	}
}
