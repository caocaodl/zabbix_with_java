package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.STYLE_TOP;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.types.CArray.map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 监控中心-服务器监察
 * @author HP Pro2000MT
 *
 */
public class HostLatestAction extends AbstractLatestAction {

	@Override
	protected String getKey() {
		return "mon_server_trigger";
	}
	
	/**
	 * 初始化页面
	 */
	@Override
	protected void doInitPage() {
		page("title", _("Overview"));
		page("file", "mon_server_trigger.action");
		page("hist_arg", new String[] { "groupid", "type" });
		page("type", detect_page_type(PAGE_TYPE_HTML));

		define("RDA_PAGE_DO_REFRESH", 1);
		define("SHOW_TRIGGERS", 1);
		define("SHOW_DATA", 0);
	}
	
	@Override
	public void doAction(SQLExecutor executor){
		
		/* Display  */
		CArray data = map(
			"fullscreen", Nest.value(_REQUEST,"fullscreen").$()
		);
			
		Nest.value(data,"view_style").$(get_request("view_style", CProfile.get(getIdentityBean(), executor, "web.overview.view.style", STYLE_TOP)));
		Nest.value(data,"pageType").$(get_request("pageType", CProfile.get(getIdentityBean(), executor, "web.overview.type", define("SHOW_DATA"))));	//监察页面显示类型
		CProfile.update(getIdentityBean(), executor, "web.overview.view.style", Nest.value(data,"view_style").$(), PROFILE_TYPE_INT);

		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
			"groups", map("editable", true),
			"hosts", map("templated_hosts", true, "editable", true),
			"groupid", get_request("groupid", null),
			"hostid", get_request("hostid", null),
			"applications", map("templated", false),
			"application", get_request("application", null)
		));
		
		//获取简单页面action
		Nest.value(data, "actionName").$(get_request("actionName"));
		Nest.value(data, "groupid").$(pageFilter.$("groupid").$());
		Nest.value(data,"pageFilter").$(pageFilter);
		Nest.value(data,"hostid").$(pageFilter.$("hostid").$());
		Nest.value(_REQUEST,"hostid").$(get_request("hostid", pageFilter.$("hostid").$()));

		// render view
		CView overviewView = new CView("monitoring.overview", data);
		overviewView.render(getIdentityBean(), executor);
		overviewView.show();
	
	} 
}
