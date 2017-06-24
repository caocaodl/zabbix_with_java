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
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.Cphp.ArrayMapCallback;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
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
public class HostDetailAction extends RadarBaseAction{
	
	@Override
	protected void doInitPage() {
		page("title", _("Host detail"));
		page("file", "host_detail.action");
//		page("js", new String[] {"FusionCharts/widgets/FusionCharts.js","FusionCharts/commonFusionCharts.js","FusionCharts/widgets/fusioncharts.widgets.js","FusionCharts/FusionCharts.jqueryplugin.js"});	//引进仪表盘功能所需function widgetsXT js
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("css", new String[] { "lessor/supervisecenter/hostdetail.css" });
		page("js", new String[] {"../../../assets/c/import/echarts-2.2.1/echarts-all.js","FusionCharts/widgets/FusionCharts.js","FusionCharts/commonFusionCharts.js","FusionCharts/widgets/fusioncharts.widgets.js","FusionCharts/FusionCharts.jqueryplugin.js"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		//		VAR			TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
			"hostid",					array(T_RDA_INT, O_OPT,	P_SYS,	DB_ID,		null),
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
		Long groupid = get_request("groupid", 0L);
		if(groupid.equals(0L)){
			CHostGroupGet option = new CHostGroupGet();
			option.setHostIds(hostid);
			option.setOutput(new String[]{"groupid"});
			option.setEditable(false);
			CArray<Map> resultCA = API.HostGroup(getIdentityBean(), executor).get(option);
			if(!empty(resultCA)){
				int length = resultCA.size();
				for(int i=0;i<length;i++){
					Map resultMap = Nest.value(resultCA, i).asCArray().toMap();
					if(TopoUtil.groupsCA.containsValue(Nest.value(resultMap, "groupid").asLong())){
						groupid = Nest.value(resultMap, "groupid").asLong();
						break;
					}
					if(i==length-1){
						groupid = Nest.value(resultCA, 0,"groupid").asLong();
					}
				}
			}
		}
		CArray data = array();
		
		/* Display */
		if (hostid > 0) {
			// host scripts
			Nest.value(data,"hostScripts").$(API.Script(getIdentityBean(), executor).getScriptsByHosts(hostid));
			
			// inventory info
			Nest.value(data,"tableTitles").$(getHostInventories());
			Nest.value(data,"tableTitles").$(rda_toHash(Nest.value(data,"tableTitles").$(), "db_field"));
			CArray inventoryFields = array_keys(Nest.value(data,"tableTitles").asCArray());
			
			//进程表单
//			CForm courseForm = getCourseData(executor);
//			Nest.value(data, "courseForm").$(courseForm);
			//最近告警表单
			CTable triggersForm = getLastTriggers(executor);
			Nest.value(data, "triggerForm").$(triggersForm);
			
			// overview tab
			CHostGet params = new CHostGet();
			params.setHostIds(hostid);
			params.setOutput(new String[]{"hostid", "host", "name", "maintenance_status","status"});
			params.setSelectInterfaces(API_OUTPUT_EXTEND);
			params.setSelectItems(API_OUTPUT_COUNT);
			params.setSelectTriggers(API_OUTPUT_COUNT);
			params.setSelectScreens(API_OUTPUT_COUNT);
			params.setSelectInventory(inventoryFields.valuesAsString());
			params.setSelectGraphs(API_OUTPUT_COUNT);
			params.setSelectApplications(API_OUTPUT_COUNT);
			params.setSelectDiscoveries(API_OUTPUT_COUNT);
			params.setSelectHttpTests(API_OUTPUT_COUNT);
			params.setPreserveKeys(true);
			Nest.value(data,"host").$(reset((CArray<Map>)API.Host(getIdentityBean(), executor).get(params)));
			unset(data,"host","inventory","hostid");
			
			data.put("hostid",hostid);
			data.put("groupid",groupid);
			
			// resolve macros
//			Nest.value(data,"host","interfaces").$(CMacrosResolverHelper.resolveHostInterfaces(getIdentityBean(), executor, Nest.value(data,"host","interfaces").asCArray().entryValueFromMap2CArray()));
			
			// get permissions
			int userType = CWebUser.getType();
			if (userType == USER_TYPE_SUPER_ADMIN) {
				Nest.value(data,"rwHost").$(true);
			} else if (userType == USER_TYPE_IRADAR_ADMIN) {
				params = new CHostGet();
				params.setHostIds(hostid);
				params.setEditable(true);
				CArray<Map> rwHost = API.Host(getIdentityBean(), executor).get(params);
				Nest.value(data,"rwHost").$( (!empty(rwHost)) ? true : false);
			} else {
				Nest.value(data,"rwHost").$(false);
			}

			// view generation
			CView hostinventoriesView = new CView("host.detail.view", data);
			hostinventoriesView.render(getIdentityBean(), executor);
			hostinventoriesView.show();
		} 
	}
	
	
	/**
	 * 获取最近告警分页信息
	 * @param executor
	 * @return 告警信息表单
	 */
	public CTable getLastTriggers(SQLExecutor executor){
		
		int source = getEventSource(executor);
		Nest.value(_REQUEST,"triggerid").$(
				(source == EVENT_SOURCE_DISCOVERY)
				? 0
				: get_request("triggerid", Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.triggerid", 0)).asInteger()));
		

		CProfile.update(getIdentityBean(), executor, "web.events.source", source, PROFILE_TYPE_INT);
		
		// page filter
		CPageFilter pageFilter = null;
		if (source == EVENT_SOURCE_TRIGGERS) {
			pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups" , map(
					"monitored_hosts", true,
					"with_monitored_triggers", true
				),
				"hosts", map(
					"monitored_hosts", true,
					"with_monitored_triggers", true
				),
				"triggers", map(),
				"hostid", get_request("hostid", null),
				"groupid", get_request("groupid", null),
				"triggerid", get_request("triggerid", null)
			));
			Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").asString());
			Nest.value(_REQUEST,"hostid").$(pageFilter.$("hostid").asString());
		}
		
		/* Display */
		Object table = new CTableInfo(_("No events found."));

		Map firstEvent = null;
		String sourceName = null;
		CEventGet eget = null;
		CArray<Map> firstEvents = null;
		// trigger events
		if (source == EVENT_OBJECT_TRIGGER) {
			sourceName = "trigger";
			eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_TRIGGERS);
			eget.setObject(EVENT_OBJECT_TRIGGER);
			eget.setOutput(API_OUTPUT_EXTEND);
			if(!empty(Nest.value(_REQUEST,"triggerid").$())){
				eget.setObjectIds(Nest.array(_REQUEST,"triggerid").asLong());
			}
			eget.setSortfield("clock");
			eget.setSortorder(RDA_SORT_UP);
			eget.setLimit(1);
			firstEvents = API.Event(getIdentityBean(), executor).get(eget);
			firstEvent = reset(firstEvents);
		}
		if (isset(_REQUEST,"period")) {
			Nest.value(_REQUEST,"period").$(get_request("period", RDA_PERIOD_DEFAULT));
			CProfile.update(getIdentityBean(), executor, "web.events."+sourceName+".period", Nest.value(_REQUEST,"period").$(), PROFILE_TYPE_INT);
		} else {
			Nest.value(_REQUEST,"period").$(CProfile.get(getIdentityBean(), executor, "web.events."+sourceName+".period"));
		}
		
		int effectiveperiod = navigation_bar_calc(getIdentityBean(), executor);
		long from = rdaDateToTime(Nest.value(_REQUEST,"stime").asString());
		long till = from + effectiveperiod;
		
		
		CTable paging = null;
		CArray<Map> events = null;
		if (firstEvent == null) {
			events = array();
			paging = getPagingLine(getIdentityBean(), executor, events);
		} else {
			Map config = select_config(getIdentityBean(), executor);
			((CTableInfo)table).setHeader(array(
				_("Time"),
				_("Host"),
				_("eventDescription"),
				_("Status"),
				_("severity level"),
				_("Duration")
			));
			
			if (pageFilter.$("hostsSelected").asBoolean()) {
				CArray knownTriggerIds = array();
				CArray validTriggerIds = array();
				
				CTriggerGet triggerOptions = new CTriggerGet();
				triggerOptions.setOutput(new String[]{"triggerid"});
				triggerOptions.setPreserveKeys(true);
				triggerOptions.setMonitored(true);
				
				int allEventsSliceLimit = Nest.value(config,"search_limit").asInteger();
				
				CEventGet eventOptions = new CEventGet();
				eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
				eventOptions.setObject(EVENT_OBJECT_TRIGGER);
//				eventOptions.setTimeFrom(from);
//				eventOptions.setTimeTill(till);
				eventOptions.setOutput(new String[]{"eventid", "objectid"});
				eventOptions.setSortfield("clock", "eventid");
				eventOptions.setSortorder(RDA_SORT_DOWN);
//				eventOptions.setLimit(allEventsSliceLimit + 1);
				eventOptions.setLimit(5);
				//根据事件的条件过滤查询结果
				CArray filterTriggerIds;
				if (!empty(get_request("triggerid",0))) {
					filterTriggerIds = array(get_request("triggerid"));
					knownTriggerIds = array_combine(filterTriggerIds, filterTriggerIds);
					validTriggerIds = knownTriggerIds;
					eventOptions.setObjectIds(filterTriggerIds.valuesAsLong());
				} else if (pageFilter.$("hostid").asLong() > 0) {
					CTriggerGet tget = new CTriggerGet();
					tget.setOutput(new String[]{"triggerid"});
					tget.setHostIds(pageFilter.$("hostid").asLong());
					tget.setMonitored(true);
					tget.setPreserveKeys(true);
					
					CArray<Map> hostTriggers = API.Trigger(getIdentityBean(), executor).get(tget);
					filterTriggerIds = array_map(new ArrayMapCallback() {
						@Override public Object call(Object... objs) {
							return strval(objs[0]);
						}
					}, array_keys(hostTriggers));
					knownTriggerIds = array_combine(filterTriggerIds, filterTriggerIds);
					validTriggerIds = knownTriggerIds;
					
					eventOptions.setHostIds(pageFilter.$("hostid").asLong());
					eventOptions.setObjectIds(validTriggerIds.valuesAsLong());
				} else if (pageFilter.$("groupid").asLong() > 0) {
					eventOptions.setGroupIds(pageFilter.$("groupid").asLong());
					triggerOptions.setGroupIds(pageFilter.$("groupid").asLong());
				}
				
				events = array();
				
				while (true) {
					CArray<Map> allEventsSlice = API.Event(getIdentityBean(), executor).get(eventOptions);

					Map lastEvent = null;
					for(Map event:allEventsSlice) {
						if (isset(validTriggerIds,event.get("objectid"))) {
							events.add(map("eventid" , Nest.value(event,"eventid").$()));
						}
						lastEvent = event;
					}
	
					if (count(events) >= Nest.value(config,"search_limit").asInteger() || count(allEventsSlice) <= allEventsSliceLimit) {
						break;
					}
					Nest.value(eventOptions,"eventid_till").$(Nest.value(lastEvent,"eventid").asLong() - 1);
				}
				
				events = array_slice(events, 0, Nest.value(config,"search_limit").asInteger() + 1);
	
				// get paging
				paging = getPagingLine(getIdentityBean(), executor, events);
	
				// query event with extend data
				eget = new CEventGet();
				eget.setSource(EVENT_SOURCE_TRIGGERS);
				eget.setObject(EVENT_OBJECT_TRIGGER);
				eget.setEventIds(rda_objectValues(events, "eventid").valuesAsLong());
				eget.setOutput(API_OUTPUT_EXTEND);
				eget.setSelectAcknowledges(API_OUTPUT_COUNT);
				eget.setSortfield("clock", "eventid");
				eget.setSortorder(RDA_SORT_DOWN);
				eget.setNopermissions(true);
				eget.setLimit(5);
				events = API.Event(getIdentityBean(), executor).get(eget);
				
				
				CTriggerGet tget = new CTriggerGet();
				tget.setTriggerIds(rda_objectValues(events, "objectid").valuesAsLong());
				tget.setSelectHosts(new String[]{"hostid"});
				tget.setSelectItems(new String[]{"itemid", "hostid", "name", "key_", "value_type"});
				tget.setOutput(new String[]{"description", "expression", "priority", "flags", "url"});
				CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(tget);
				triggers = rda_toHash(triggers, "triggerid");
				
				// fetch hosts
				CArray<Map> hosts = array();
				for(Map trigger : triggers) {
					hosts.add((Map)reset(Nest.value(trigger,"hosts").asCArray()));
				}
				CArray hostids = rda_objectValues(hosts, "hostid");
				CHostGet hget = new CHostGet();
				hget.setOutput(new String[]{"name", "hostid", "status"});
				hget.setHostIds(hostids.valuesAsLong());
				hget.setSelectScreens(API_OUTPUT_COUNT);
				hget.setPreserveKeys(true);
				hosts = API.Host(getIdentityBean(), executor).get(hget);
	
				// fetch scripts for the host JS menu
				CArray<CArray<Map>> scripts = null;
				if (Nest.value(_REQUEST,"hostid").asInteger() == 0) {
					scripts = API.Script(getIdentityBean(), executor).getScriptsByHosts(hostids.valuesAsLong());
				}
	
				// events
				for (Map event : events) {
					CArray trigger = CArray.valueOf(triggers.get(event.get("objectid")));
					
					Map host = reset(Nest.value(trigger,"hosts").asCArray());
					if(empty(host))
						continue;
					host = hosts.get(host.get("hostid"));
					
					CArray triggerItems = array();
					Nest.value(trigger,"items").$(resolveItemNames(getIdentityBean(), executor, Nest.value(trigger,"items").$s()));
					
					String description = resolveEventDescription(getIdentityBean(), executor, rda_array_merge(trigger, map(
						"clock" , Nest.value(event,"clock").$(),
						"ns" , Nest.value(event,"ns").$()
					)));
					
//					CSpan triggerDescription = new CSpan(description, "pointer link_menu");
//					triggerDescription.setMenuPopup(FuncsUtil.getMenuPopupTrigger(trigger, triggerItems, null, Nest.value(event,"clock").asString()));
					
					// duration
					Map nextEvent;
					Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(getIdentityBean(), executor, event, events))
						? rda_date2age(Nest.value(event,"clock").asLong(), Nest.value(nextEvent,"clock").asLong())
						: rda_date2age(Nest.value(event,"clock").asLong()));
					
					CSpan statusSpan = new CSpan(trigger_value2str(Nest.value(event,"value").asInteger()));

					CSpan hostName = new CSpan(Nest.value(host,"name").$(), "");
					
					((CTableInfo)table).addRow(array(
//						new CLink(rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),
//								"tr_events.action?triggerid="+Nest.value(event,"objectid").asString()+"&eventid="+Nest.value(event,"eventid").asString(),
//							"action"
//						),
						rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),
						hostName,
//						triggerDescription,
						description,
						statusSpan,
//						getSeverityCell(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger(), null, !Nest.value(event,"value").asBoolean()),
						BlocksUtil.getTriggerLevel( Nest.value(trigger,"priority").asInteger(),getIdentityBean(),executor),
						Nest.value(event,"duration").$()
					));
				}
			} else {
				events = array();
				paging = getPagingLine(getIdentityBean(), executor, events);
			}
		}
		return (CTable) table;
	}

	/** 
	 * 页面填充数据方法
	 */
	protected CArray<String> getRowData(String level,String description,String earliestTime,String latestTime,String state) {
		return array(
			level,
			description,
			earliestTime,
			latestTime,
			state
		);
	}
	
	/**设置列表表头
	 * @param form
	 * @return
	 */
	protected CArray getHeader(String type){
		if(type != null){
			if("course".equals(type)){
				return array(
						_("courseID"),		//进程ID
						_("courseName"),	//进程名称
						_("CPU"),			//CPU	
						_("memory"),		//内存
						_("status")			//状态
					);
			}else if("trigger".equals(type)){
				return array(
						_("Time"),
						_("Description"),
						_("Status"),
						_("Severity"),
						_("Duration")
					);			
				
			}else{
				return null;
			}
		}else{
			return null;
		}
	}	
	
    private int getEventSource(SQLExecutor executor){
    	return get_request("source", Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.source", EVENT_SOURCE_TRIGGERS)).asInteger());
    }
}
