package com.isoft.iradar.web.action;

import static com.isoft.iradar.web.action.TDashboardUtil.make_Announcement;
import static com.isoft.iradar.web.action.TDashboardUtil.make_event;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.types.Mapper.Nest;
import com.opensymphony.xwork2.Action;

/**
 * 租户首页概览
 * @author LiuBoTao
 *
 */
public class TenantDashBoardAction extends RadarBaseAction{
	
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
		if("showCloudServiceInfo".equals(cmd)) {		//配额分配情况
			ajaxResponse.success(TvmUtil.make_resource_distribution(getIdentityBean(),executor));
		}else if("showCloudServiceState".equals(cmd)){	//监控状态
			ajaxResponse.success(TvmUtil.make_cloudSerStat(getIdentityBean(), executor));
		}else if("showCloudCPURateTop5".equals(cmd)){	//云主机CPU利用率TOP5
			ajaxResponse.success(TvmUtil.queryCloudCPURateTop5(getIdentityBean(), executor,"cpu"));
		}else if("showCloudmemoryRateTop5".equals(cmd)){//云主机内存利用率TOP5
			ajaxResponse.success(TvmUtil.queryCloudCPURateTop5(getIdentityBean(), executor,"memory"));
		}else if("showTriggerTrend".equals(cmd)){		//告警产生趋势
			ajaxResponse.success(TvmUtil.make_Trigger_use_trend(getIdentityBean(), executor));
		}
		ajaxResponse.send();
		return true;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		//公告
		this.getRequest().setAttribute("notice", make_Announcement(executor));
		//最近前5个事件
		this.getRequest().setAttribute("issues", make_event(executor, getIdentityBean(), 1));
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
