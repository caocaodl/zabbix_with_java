package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.EventsUtil.get_next_event;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveEventDescription;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.common.AnnouncementDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TDashboardUtil {
	
	/**
	 * 今日监控项目统计
	 * @param executor
	 * @return
	 */
	public static CDiv make_todayArtSts(SQLExecutor executor, IIdentityBean idBean) {
		CTableInfo _table = new CTableInfo(_("NO found Data"));
		_table.setAttribute("id", "t_mon");
//		
//		//取网站监察数目
		CHttpTestGet hoptions = new CHttpTestGet();
		hoptions.setCountOutput("httptestid");

		Long httptestNum = API.HttpTest(idBean, executor).get(hoptions);
		
		//监控中的服务应用监察数目
		CHostGet soptions = new CHostGet();
		soptions.setCountOutput("hostid");
		soptions.setGroupIds(IMonConsts.DISCOVERED_HOSTS); 
		soptions.setMonitoredHosts(true);
		
		Long serviceAppNum = API.Host(idBean, executor).get(soptions);
		//监控中的云主机监察数目
		CHostGet voptions = new CHostGet();
		voptions.setCountOutput("hostid");
		voptions.setGroupIds(IMonConsts.MON_VM);
		soptions.setMonitoredHosts(true);
		
		Long vmNum = API.Host(idBean, executor).get(voptions);		
		
		
		CJSScript _fusionScript = new CJSScript(get_js("todayMonItem("+httptestNum+","+serviceAppNum+","+vmNum+" )"));
		
		CDiv newdiv = new CDiv();
		newdiv.setAttribute("id", "todayMonItemChart");
		
		
		_table.addRow(array(newdiv, _fusionScript));
			
		return new CDiv(_table);	
	}	
	
	/**
	 * 服务应用监控项目状态统计
	 * @param executor
	 * @return
	 */
	public static CDiv make_monStaSts(SQLExecutor executor, IIdentityBean idBean) {
		CTableInfo _table = new CTableInfo(_("NO found Data"));
		_table.setAttribute("id", "t_mon");
		//监控中的服务应用监察数目
		CHostGet soptions = new CHostGet();
		soptions.setCountOutput("hostid");
		soptions.setGroupIds(IMonConsts.DISCOVERED_HOSTS); 
		soptions.setMonitoredHosts(true);
		
		Long serviceAppNum = API.Host(idBean, executor).get(soptions);		
		
		//产生了活动事件的监控中服务应用监察数目
		CEventGet eventOptions = new CEventGet();
		eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
		eventOptions.setObject(EVENT_OBJECT_TRIGGER);
		eventOptions.setValue("1"); //0 --> History   1 --> Active
		eventOptions.setGroupIds(IMonConsts.DISCOVERED_HOSTS);
		eventOptions.setOutput(new String[]{"objectid"});
		eventOptions.setSortfield("clock");
		eventOptions.setSortorder(RDA_SORT_DOWN);
		eventOptions.setLimit(1001);
		
		CArray<Map> allEventsSlice = API.Event(idBean, executor).get(eventOptions);
		
		Long eventserviceAppNum=0l; //记录数目
		if(!empty(allEventsSlice)){
			CTriggerGet triggerOptions = new CTriggerGet();
			triggerOptions.setGroupIds(IMonConsts.DISCOVERED_HOSTS);
			triggerOptions.setTriggerIds(rda_objectValues(allEventsSlice, "objectid").valuesAsLong());
			triggerOptions.setSelectHosts(new String[]{"hostid"});
			triggerOptions.setOutput(new String[]{"triggerid"});
			triggerOptions.setMonitored(true);
			
			CArray<Map> triggers = API.Trigger(idBean, executor).get(triggerOptions);
			triggers = rda_toHash(triggers, "triggerid");
			
			if(!empty(triggers)){
				// fetch hosts
				CArray<Map> hosts = array();
				for(Map trigger : triggers) {
					hosts.add((Map)reset(Nest.value(trigger,"hosts").asCArray()));
				}		
				
				CHostGet hget = new CHostGet();
				hget.setCountOutput("hostid");
				hget.setGroupIds(IMonConsts.DISCOVERED_HOSTS); 
				hget.setHostIds(rda_objectValues(hosts, "hostid").valuesAsLong());
				hget.setMonitoredHosts(true);
				eventserviceAppNum = API.Host(idBean, executor).get(hget);
			}		
		}
		
		CJSScript _fusionScript = new CJSScript(get_js("serviceApp(" +(serviceAppNum-eventserviceAppNum)+ "," + eventserviceAppNum + ")"));
		
		CDiv newdiv = new CDiv();
		newdiv.setAttribute("id", "serviceAppChart");

		
		_table.addRow(array(newdiv,_fusionScript));
		
		return new CDiv(_table);	
	}	
	
	/**
	 * 资源使用情况
	 * @param executor
	 * @return
	 */
	public static CDiv make_resUsed(IIdentityBean idBean,SQLExecutor executor) {
		CTableInfo _table = new CTableInfo(_("NO found Data"));
		CRow row = new CRow();
		// 通过算法获取特定指标数据
		String cpuUsedRate = TvmUtil.returnCPURate(executor, idBean,false,false);		//CPU利用率
		String memoryRatedRate = TvmUtil.returnUsedMemory(executor, idBean,false,false);	//内存利用率
		String diskUsedRate = TvmUtil.returnUsedDiskRate(executor, idBean,false,false);	//磁盘利用率
		
		//cpu利用率仪表盘
		CDiv cpuUsedRateDiv = new CDiv();
		cpuUsedRateDiv.setAttribute("id", "cpuUsedRateChart");
		row.addItem(cpuUsedRateDiv);

		// 内存利用率仪表盘
		CDiv memoryUsedRateDiv = new CDiv();
		memoryUsedRateDiv.setAttribute("id", "memoryUsedRateChart");
		row.addItem(memoryUsedRateDiv);

		// 硬盘利用率仪表盘
		CDiv diskUsedRateDiv = new CDiv();
		diskUsedRateDiv.setAttribute("id", "diskUsedRateChart");
		row.addItem(diskUsedRateDiv);
		CJSScript _cpuFusionScript = new CJSScript(get_js("useFusionCharts(" + cpuUsedRate + "," + IMonConsts.CPUUSEDRATE_FLAG + ")"));
		CJSScript _memoryFusionScript = new CJSScript(get_js("useFusionCharts(" + memoryRatedRate + "," + IMonConsts.MEMORYUSEDRATE_FLAG + ")"));
		CJSScript _diskFusionScript = new CJSScript(get_js("useFusionCharts(" + diskUsedRate + "," + IMonConsts.DISKUSEDRATE_FLAG + ")"));
		_table.addRow(row, "centertext");
		_table.addRow(array( cpuUsedRate+"(%)",memoryRatedRate+"(%)",diskUsedRate+"(%)"), "centertext");
		_table.addRow(array("CPU利用率", "内存利用率", "硬盘利用率"), "centertext_one");

		return new CDiv(array(_table,_cpuFusionScript,_memoryFusionScript,_diskFusionScript));	
	}	
	
/*	*//**
	 * 故障历史
	 * @param executor
	 * @return
	 *//*
	public static CDiv make_faultHis(SQLExecutor executor) {
		CTableInfo _table = new CTableInfo(_("NO found Data"));
			
		return new CDiv(_table);	
	}	
	
	*//**
	 * 尚未修复故障
	 * 
	 *//*
	public static CDiv make_noRepFault(SQLExecutor executor) {
		CTableInfo _table = new CTableInfo(_("NO found Data"));
			
		return new CDiv(_table);	
	}	*/
	
	

	 /**  获取公告
    * @param executor
    * @return
    */
   public static CDiv make_Announcement(SQLExecutor executor) {
		AnnouncementDAO an =new AnnouncementDAO(executor);
		List<Map> list=an.doeffectiveList();
		CTableInfo _table = new CTableInfo(_("NO announcement for the monment"));
		CCol  ctitle = new CCol("标题","title_class");
		CCol  ctime = new CCol("时间","time_class");
		_table.addRow(array(ctitle,ctime),"titleClass");
		for(Map maintenance : list){
			String title = Nest.value(maintenance, "title").asString();
			String id= Nest.value(maintenance, "announcementid").asString();
			
			String activeSince = Nest.value(maintenance,"active_since").asString();
			Long timestamp = Long.parseLong(activeSince)*1000;
			String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(timestamp));  
		
			Map  datamap=new HashMap();
			datamap.put("title", title);
	        datamap.put("date", date);
			CSpan span=new CSpan(title,"tenantitle");
			CSpan span1=new CSpan(date," announcement_time");
			span.setAttribute("onclick", "return PopUp(\"alertfist.action?tenanid="+id+"&title="+title+"\", 550, 300)");
            	_table.addRow(array(span,span1));	
		}
		return new CDiv(_table);		
	}
   
   /**
    * 事件列表,最近前5个事件
    * 
    * eventType 0：活动事件     1: 历史事件
    */
   public static CDiv make_event(SQLExecutor executor, IIdentityBean idBean, int eventType){
	   Object table = new CTableInfo(_("No events found."));
	   ((CTableInfo)table).setHeader(array(_("Time"), /*_("VM Manage"),*/ _("Description"), _("Duration")));
		
		CArray knownTriggerIds = array();
		CArray validTriggerIds = array();
		
		CTriggerGet triggerOptions = new CTriggerGet();
		triggerOptions.setOutput(new String[]{"triggerid"});
		//triggerOptions.setFilter("priority", Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED);
		triggerOptions.setPreserveKeys(true);
		triggerOptions.setMonitored(true);

		CEventGet eventOptions = new CEventGet();
		eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
		eventOptions.setObject(EVENT_OBJECT_TRIGGER);
		eventOptions.setValue(String.valueOf(eventType));
		eventOptions.setOutput(new String[]{"eventid", "objectid"});
		eventOptions.setSortfield("clock", "eventid");
		eventOptions.setSortorder(RDA_SORT_DOWN);
		eventOptions.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));
		eventOptions.setLimit(1001);
		
		CArray<Map> events = array();
		
		while (true) {
			CArray<Map> allEventsSlice = API.Event(idBean, executor).get(eventOptions);

			CArray triggerIdsFromSlice = array_keys(array_flip(rda_objectValues(allEventsSlice, "objectid")));

			CArray unknownTriggerIds = array_diff(triggerIdsFromSlice, knownTriggerIds);

			if (!empty(unknownTriggerIds)) {
				Nest.value(triggerOptions,"triggerids").$(unknownTriggerIds);
				CArray<Map> validTriggersFromSlice = API.Trigger(idBean, executor).get(triggerOptions);

				for(Map trigger:validTriggersFromSlice) {
					validTriggerIds.put(trigger.get("triggerid"),trigger.get("triggerid"));
				}

				for(Object id:unknownTriggerIds.values()) {
					knownTriggerIds.put(id,id);
				}
			}
			Map lastEvent = null;
			for(Map event:allEventsSlice) {
				if (isset(validTriggerIds,event.get("objectid"))) {
					events.add(map("eventid" , Nest.value(event,"eventid").$()));
				}
				lastEvent = event;
			}

			// break loop when either enough events have been retrieved, or last slice was not full
			if (count(events) >=1000 || count(allEventsSlice) <= 1000) {
				break;
			}

			/*
			 * Because events in slices are sorted descending by eventid (i.e. bigger eventid),
			 * first event in next slice must have eventid that is previous to last eventid in current slice.
			 */
			Nest.value(eventOptions,"eventid_till").$(Nest.value(lastEvent,"eventid").asLong() - 1);
		}
		
		/*
		 * At this point it is possible that more than Nest.value(_config,"search_limit").$() events are selected,
		 * therefore at most only first Nest.value(_config,"search_limit").$() + 1 events will be used for pagination.
		 */
		events = array_slice(events, 0, 1001);

		// get paging
		//CTable paging = getPagingLine(idBean, executor, events);
		
		// query event with extend data
		CEventGet eget = new CEventGet();
		eget.setSource(EVENT_SOURCE_TRIGGERS);
		eget.setObject(EVENT_OBJECT_TRIGGER);
		eget.setEventIds(rda_objectValues(events, "eventid").valuesAsLong());
		eget.setOutput(API_OUTPUT_EXTEND);
		eget.setSelectAcknowledges(API_OUTPUT_COUNT);
		eget.setSortfield("clock", "eventid");
		eget.setSortorder(RDA_SORT_DOWN);
		eget.setNopermissions(true);
		events = API.Event(idBean, executor).get(eget);
		
		CTriggerGet tget = new CTriggerGet();
		tget.setTriggerIds(rda_objectValues(events, "objectid").valuesAsLong());
		tget.setSelectHosts(new String[]{"hostid"});
		tget.setSelectItems(new String[]{"itemid", "hostid", "name", "key_", "value_type"});
		tget.setOutput(new String[]{"description", "expression", "priority", "flags", "url"});
		CArray<Map> triggers = API.Trigger(idBean, executor).get(tget);
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
		hosts = API.Host(idBean, executor).get(hget);
		
		// events
		int i=0;
		for (Map event : events) {
			i++;
			CArray trigger = CArray.valueOf(triggers.get(event.get("objectid")));
			
			Map host = reset(Nest.value(trigger,"hosts").asCArray());
			if(empty(host))
				continue;
			host = hosts.get(host.get("hostid"));
			
			CArray triggerItems = array();
			Nest.value(trigger,"items").$(resolveItemNames(idBean, executor, Nest.value(trigger,"items").$s()));
			
			CArray<Map> items = Nest.value(trigger,"items").$s();
			for (Map item : items) {
				triggerItems.add(map(
					"name" , Nest.value(item,"name_expanded").$(),
					"params" , map(
						"itemid" , Nest.value(item,"itemid").$(),
						"action" , in_array(Nest.value(item,"value_type").$(), array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64))
							? "showgraph" : "showvalues"
					)
				));
			}
			
			String description = TvmUtil.getZHDescription(Nest.value(trigger, "description").asString());
			CSpan descriptionpan =new CSpan(description);
			descriptionpan.setTitle(description);
			
			// duration
			Map nextEvent;
			Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(idBean, executor, event, events))
				? rda_date2age(Nest.value(event,"clock").asLong(), Nest.value(nextEvent,"clock").asLong())
				: rda_date2age(Nest.value(event,"clock").asLong()));
			
			//Object hostName = Nest.value(host,"name").$();
			Object clock = rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong());
			
			((CTableInfo)table).addRow(array(
				clock,
			//	hostName,
				descriptionpan,
				Nest.value(event,"duration").$()
			));
			if(i>4){
				break;//保证只获取最近前5个事件
			}
		}
		
		table = array(table);
		
		return new CDiv(table);	   
   }
   
}
