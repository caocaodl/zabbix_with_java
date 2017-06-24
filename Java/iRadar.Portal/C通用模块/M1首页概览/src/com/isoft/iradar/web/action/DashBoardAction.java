package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.BlocksUtil.make_graph_menu;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.types.CArray.array;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.opensymphony.xwork2.Action;

public class DashBoardAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		page("type", "ajax".equals(getParameter("output"))? Defines.PAGE_TYPE_JSON: Defines.PAGE_TYPE_HTML);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if(!"ajax".equals(getParameter("output"))) return false;
		String cmd = Nest.value(_REQUEST, "cmd").asString();
		AjaxResponse ajaxResponse = new AjaxResponse();
		
		if("showServerLoadTop5".equals(cmd)) {			//CPU负载
			ajaxResponse.success(BlocksUtil.make_topN(getIdentityBean(), executor));
		}else if("showCloudServiceState".equals(cmd)){	//云服务状态
			ajaxResponse.success(BlocksUtil.make_cloudSerStat(getIdentityBean(), executor));
		}else if("showResourceUseRate".equals(cmd)){	//资源利用率
			ajaxResponse.success(BlocksUtil.make_resUsedRate(getIdentityBean(), executor));
		}else if("showCloudServiceInfo".equals(cmd)){	//云平台信息
			ajaxResponse.success(BlocksUtil.make_favorite_platformInfo(getIdentityBean(), executor));
		}else if("showSystemStatus".equals(cmd)){		//系统状态
			CArray dashconf = array();
			Nest.value(dashconf,"extAck").$(0);
			Nest.value(dashconf,"filterEnable").$(0);
			ajaxResponse.success(BlocksUtil.make_system_status(getIdentityBean(), executor,dashconf));
		}else if("showHealth".equals(cmd)){				//系统健康度
			ajaxResponse.success(BlocksUtil.make_totalSituation(getIdentityBean(), executor));
		}else if("showResourceUseTrend".equals(cmd)){	//资源使用趋势
			ajaxResponse.success(BlocksUtil.make_resource_use_trend(getIdentityBean(), executor));
		}

		ajaxResponse.send();
		
		return true;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		CArray menu = array();
		CArray submenu = array();
		make_graph_menu(getIdentityBean(), executor,menu, submenu);
		this.getRequest().setAttribute("js", "var page_menu="+rda_jsvalue(menu)+";\n"+"var page_submenu="+rda_jsvalue(submenu)+";\n");
		this.getRequest().setAttribute("graph", BlocksUtil.make_favorite_graphs(getIdBean(), executor));
		//最近发生的告警
		this.getRequest().setAttribute("issues", BlocksUtil.make_latest_issues(getIdBean(), executor,CArray.map("limit",6)));
		this.getRequest().setAttribute("severityCss", BlocksUtil.getSeverityCss(getIdentityBean(),executor));
	}
	
	public String show() {
		this.doWork();
		
		if("ajax".equals(getParameter("output"))) {
			return null;
		}else {
			return Action.SUCCESS;
		}
	}
}
