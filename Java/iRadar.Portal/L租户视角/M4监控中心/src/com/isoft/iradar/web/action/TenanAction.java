package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_combine;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_map;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strval;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.EventsUtil.get_next_event;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.hasRequest;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.HostsUtil.getHostInventories;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveEventDescription;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp.ArrayMapCallback;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.CView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 设备详情页面 
 * @author HP Pro2000MT
 *
 */
public class TenanAction extends RadarBaseAction{
	
	@Override
	protected void doInitPage() {
		page("title", _("Host detail"));
		page("file", "tenan.action");
//		page("js", new String[] {"FusionCharts/widgets/FusionCharts.js","FusionCharts/commonFusionCharts.js","FusionCharts/widgets/fusioncharts.widgets.js","FusionCharts/FusionCharts.jqueryplugin.js"});	//引进仪表盘功能所需function widgetsXT js
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("js", new String[] {"../../../assets/c/import/echarts-2.2.1/echarts-all.js","FusionCharts/commonFusionCharts.js"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR		templateid	TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
			"hostid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
			"templateid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
			// filter
			"filter_set",				array(T_RDA_STR, O_OPT,	P_SYS,	null,		null),
			"filter_field",				array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_field_value",	array(T_RDA_STR, O_OPT, null,	null,		null),
			"filter_exact",        	array(T_RDA_INT, O_OPT, null,	"IN(0,1)",	null),
			//ajax
			"favobj",					array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",					array(T_RDA_STR, O_OPT, P_ACT,  NOT_EMPTY,	"isset({favobj})"),
			"favstate",				array(T_RDA_INT, O_OPT, P_ACT,  NOT_EMPTY,	"isset({favobj})&&(\"filter\"=={favobj})")
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
		
		if (hasRequest("favobj")) {
			if("filter".equals(Nest.value(_REQUEST,"favobj").asString())){
				CProfile.update(getIdentityBean(), executor, "web.hostinventories.filter.state", Nest.as(get_request("favstate")).asString(), PROFILE_TYPE_INT);
			}
		}

		if ((PAGE_TYPE_JS == Nest.value(page,"type").asInteger()) || (PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger())) {
			return false;
		}
		
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		Long hostid = get_request("hostid", 0L);
		CArray data = array();
		
		/* Display */
		if (hostid > 0) {
			CTable triggersForm = new  CTable();
			triggersForm.addItem(getLastTriggers(executor,hostid,0l));
			Nest.value(data, "triggerForm").$(triggersForm);
			CHostGet options = new CHostGet();
			options.setOutput(new String[] { "name", "hostid", "status", "host" });
			options.setHostIds(hostid);
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(options);
			data.put("hostid", hostid);
			data.put("host", hosts);
			data.put("templateid", Nest.value(_REQUEST, "templateid").asString());
			CView hostinventoriesView = new CView("tenan.view", data);
			hostinventoriesView.render(getIdentityBean(), executor);
			hostinventoriesView.show();
		} 
	}
	
	

	/** 获取最近告警事件
	 * @param executor
	 * @param hostid
	 * @param groupid
	 * @return
	 */
	public CTableInfo getLastTriggers(SQLExecutor executor,long hostid,long groupid){
		TeventUtil events= new TeventUtil();
		return (CTableInfo) events.getEvent(executor, getIdentityBean(), 1, hostid, groupid);
		
	}
}
