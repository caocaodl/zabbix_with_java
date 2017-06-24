package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strInArray;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_TEXT_FILE;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_XML;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_USER;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCol;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class MenuUtil {

	public static CArray<Menu> RDA_MENU = map(
			"view", new Menu(
				_("Monitoring"), 
				USER_TYPE_IRADAR_USER, 
				PERM_READ, 
				0, 
				new Page[]{
					new Page(
							"dashboard.action",
							_("Dashboard"),
							new String[]{"dashconf.action"}
					),
					new Page(
							"overview.action",
							_("Overview")
					),
					new Page(
							"httpmon.action",
							_("Web"),
							new String[]{"httpdetails.action"}
					),
					new Page(
							"latest.action",
							_("Latest data"),
							new String[]{"history.action", "chart.action"}
					),
					new Page(
							"tr_status.action",
							_("Triggers"),
							new String[]{"acknow.action", "tr_comments.action", "chart4.action", "scripts_exec.action"}
						),
						new Page(
							"events.action",
							_("Events"),
							new String[]{"tr_events.action"}
						),
						new Page(
							"charts.action",
							_("Graphs"),
							new String[]{"chart2.ph", "chart3.action", "chart6.action", "chart7.action"}
						),
						new Page(
							"screens.action",
							_("Screens"),
							new String[]{"slides.action"}
						),
//FIXME						
//						new Page(
//							"maps.action",
//							_("Maps"),
//							new String[]{"map.action"}
//						),
						new Page(
							"discovery.action",
							_("Discovery") 
						).setUser_type(USER_TYPE_IRADAR_ADMIN),
						new Page(
							"srv_status.action",
							_("IT services"),
							new String[]{"report3.action", "chart5.action"}
						),
						new Page(
							"chart3.action"
						),
						new Page(
							"imgstore.action"
						),
						new Page(
							"search.action"
						),
						new Page(
							"jsrpc.action"
						)
				}),
			"cm",  new Menu(
				_("Inventory"), 
				USER_TYPE_IRADAR_USER, 
				PERM_READ, 
				0, 
				new Page[]{
					new Page(
							"hostinventoriesoverview.action",
							_("Overview")
					),
					new Page(
							"hostinventories.action",
							_("Hosts")
					)
				}),
			"reports",  new Menu(
				_("Reports"), 
				USER_TYPE_IRADAR_USER, 
				PERM_READ, 
				0, 
				new Page[]{
					new Page(
							"report1.action",
							_("Status of iRadar")
					).setUser_type(USER_TYPE_SUPER_ADMIN),
					new Page(
							"report2.action",
							_("Availability report")
					),
					new Page(
							"report5.action",
							_("Triggers top 100")
					),
					new Page(
							"report6.action",
							_("Bar reports"),
							new String[]{"popup_period.action","popup_bitem.action","chart_bar.action"}
					),
					new Page(
							"popup.action"
					),
					new Page(
							"popup_right.action"
					),
				}),
				"config",  new Menu(
					_("Configuration"), 
					USER_TYPE_IRADAR_ADMIN, 
					PERM_READ, 
					0, 
					new Page[]{
							new Page(
								"conf.import.action"
							),
							new Page(
								"hostgroups.action",
								_("Host groups")
							),
							new Page(
								"templates.action",
								_("Templates")
							),
							new Page(
								"hosts.action",
								_("Hosts"),
								new String[]{
									"items.action",
									"triggers.action",
									"graphs.action",
									"applications.action",
									"tr_logform.action",
									"tr_testexpr.action",
									"popup_trexpr.action",
									"host_discovery.action",
									"disc_prototypes.action",
									"trigger_prototypes.action",
									"host_prototypes.action",
									"httpconf.action",
									"popup_httpstep.action"
								}
							),
							new Page(
								"maintenance.action",
								_("Maintenance")
							),
							new Page(
								"actionconf.action",
								_("Actions")
							),
							new Page(
								"screenconf.action",
								_("Screens"),
								new String[]{"screenedit.action"}
							),
							new Page(
								"slideconf.action",
								_("Slide shows")
							),
//							new Page(
//								"sysmaps.action",
//								_("Maps"),
//								new String[]{"image.action", "sysmap.action"}
//							),
							new Page(
								"discoveryconf.action",
								_("Discovery")
							),
							new Page(
								"services.action",
								_("IT services")
							)
					}),
				"admin",  new Menu(
						_("Administration"), 
						USER_TYPE_SUPER_ADMIN, 
						PERM_READ, 
						1, 
						new Page[]{
								new Page(
									"adm.gui.action",
									_("General"),
									new String[]{
										"adm.housekeeper.action",
//FIXME										
//										"adm.images.action",
//										"adm.iconmapping.action",
										"adm.regexps.action",
										"adm.macros.action",
										"adm.valuemapping.action",
										"adm.workingtime.action",
										"adm.triggerseverities.action",
										"adm.triggerdisplayoptions.action",
										"adm.other.action"
									}
								),
								new Page(
									"proxies.action",
									_("DM")
								),
//FIXME								
//								new Page(
//									"authentication.action",
//									_("Authentication")
//								),
								new Page(
									"usergrps.action",
									_("Users"),
									new String[]{"users.action", "popup_usrgrp.action"}
								),
								new Page(
									"media_types.action",
									_("Media types")
								),
								new Page(
									"scripts.action",
									_("Scripts")
								),
								new Page(
									"auditlogs.action",
									_("Audit"),
									new String[]{"auditacts.action"}
								),
								new Page(
									"queue.action",
									_("Queue")
								),
								new Page(
									"report4.action",
									_("Notifications")
								)
//FIXME								
//								new Page(
//									"setup.action",
//									_("Installation")
//								)
						}),
					"login",  new Menu(
						_("Login"), 
						0, 
						0, 
						0, 
						new Page[]{
							new Page(
									"index.action",
									null,
									new String[]{"profile.action","popup_media.action"}
							)
						})
	);
	
	public static boolean rda_construct_menu(IIdentityBean idBean, SQLExecutor executor, List<CCol>main_menu, CArray<List<Map>> sub_menus, Map page){
		boolean denied_page_requested = false;
		boolean page_exists = false;
		boolean deny = !defined("RDA_PAGE_NO_AUTHORIZATION");
		
		for (Entry<Object, Menu> e : RDA_MENU.entrySet()) {
			String label = Nest.as(e.getKey()).asString();
			Menu menu = e.getValue();
			boolean show_menu = true;
			
			if (isset(menu.user_type)) {
				show_menu &= (menu.user_type <= CWebUser.getType());
			}
			
			if("login".equals(label)){
				show_menu = false;
			}
			
			String menu_class = "horizontal_menu_n";
			sub_menus.put(label, new ArrayList<Map>());
			if (menu.pages != null && menu.pages.length > 0) {
				for (Page sub_page : menu.pages) {					
					boolean show_sub_menu = true;
					
					// show check
					if(!isset(sub_page.label)){
						show_sub_menu = false;
					}
					if(!isset(sub_page.user_type)){
						sub_page.user_type = menu.user_type;
					}
					if (CWebUser.getType() < sub_page.user_type) {
						show_sub_menu = false;
					}
					
					Map<String,Object> row = new HashMap<String,Object>();
					row.put("menu_text", isset(sub_page.label)?sub_page.label:"");
					row.put("menu_url", sub_page.url);
					row.put("class", "highlight");
					row.put("selected", false);
					
					boolean sub_menu_active = page.get("file").equals(sub_page.url);
					sub_menu_active |= (isset(sub_page.sub_pages) && strInArray(page.get("file"), sub_page.sub_pages));
					
					if(sub_menu_active){
						// permition check
						deny &= (CWebUser.getType() < menu.user_type || CWebUser.getType() < sub_page.user_type);
						
						menu_class = "active";
						page_exists = true;
						page.put("menu", label);
						row.put("selected", true);
						
						if (!defined("RDA_PAGE_NO_MENU")) {
							CProfile.update(idBean, executor, "web.menu."+label+".last", sub_page.url, PROFILE_TYPE_STR);
						}
					}
					
					if (show_sub_menu) {
						sub_menus.get(label).add(row);
					}
				}
			}			
			
			if (page_exists && deny) {
				denied_page_requested = true;
			}
			
			if (!show_menu) {
				unset(sub_menus, label);
				continue;
			}
			
			String menu_url = (String)sub_menus.get(label).get(menu.default_page_id).get("menu_url");
			CCol mmenu_entry = new CCol(menu.label, menu_class);
			mmenu_entry.setAttribute("id", label);
			mmenu_entry.addAction("onclick", "javascript: redirect('"+menu_url+"');");
			mmenu_entry.addAction("onmouseover", "javascript: MMenu.mouseOver('"+label+"');");
			mmenu_entry.addAction("onmouseout", "javascript: MMenu.mouseOut();");
			main_menu.add(mmenu_entry);
		}
		
		if (!page_exists) {
			int type = (Integer) page.get("type");
			if (type != PAGE_TYPE_XML && type != PAGE_TYPE_CSV && type != PAGE_TYPE_TEXT_FILE) {
				denied_page_requested = true;
			}
		}
		
		return denied_page_requested;
	}

	public static void rda_define_menu_restrictions(Map<String, Object> page) {
		for (Menu section : RDA_MENU.values()) {
			 Page[] pages = section.pages;
			 if(pages!=null && pages.length>0){
				 for(Page menu_page: pages){
					if (page.get("file").equals(menu_page.url)
							|| (isset(menu_page.sub_pages) && strInArray(page.get("file"), menu_page.sub_pages))) {
						return;
					}
				 }
			 }
		}
	}
}

class Menu {
	public Menu(String label, int user_type, int node_perm,
			int default_page_id, Page[] pages) {
		this.label = label;
		this.user_type = user_type;
		this.default_page_id = default_page_id;
		this.pages = pages;
	}

	public String label;
	public Integer user_type;
	public Integer default_page_id;
	public Page[] pages;
}

class Page {
	
	public Page(String url) {
		this.url = url;
	}
	
	public Page(String url, String label) {
		this.url = url;
		this.label = label;
	}
	
	public Page(String url, String label, String[] sub_pages) {
		this.url = url;
		this.label = label;
		this.sub_pages = sub_pages;
	}
	
	public String url;
	public String label;
	public Integer user_type;
	public String[] sub_pages;
	
	public Page setUser_type(int user_type) {
		this.user_type = user_type;
		return this;
	}
}