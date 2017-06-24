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
import static com.isoft.iradar.common.util.IMonConsts.APP_NAME_MYSQL;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.EventsUtil.get_next_event;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.iradar.web.Util.TvmUtil.removeDefaultProfix;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.utils.EasyList;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.bean.reportForms.EventsReportForms;

/** 事件工具类
 * @author admin
 *
 */
public class TeventUtil {
	
	public static List getTrendByApp(IIdentityBean idBean, SQLExecutor executor,int _,String appid,String period){
		return getTrendByApp(idBean, executor, _,appid, period,false);
	}
	
	private final static CArray<String> PERIOD_MAP = CArray.map("0", "Day", "1", "Week", "2", "Month");
	public static List getTrendByApp(IIdentityBean idBean, SQLExecutor executor,int _,String appid,String period,boolean isweb){
		period = PERIOD_MAP.get(period);
		SimpleDateFormat sdf = new SimpleDateFormat();
		Long timeBucket  = 0l;	//时间段(比如说每周1天为1段 timeBucket=60*60*24)
		if("Day".equals(period)){
			timeBucket = 2*60*60l;
			sdf = new SimpleDateFormat("d日H时");
		}else if("Week".equals(period)){
			timeBucket = 24*60*60l;
			sdf = new SimpleDateFormat("MM-dd");
		}else if("Month".equals(period)){
			timeBucket = 7*24*60*60l;
			sdf = new SimpleDateFormat("M月第W周");
		}else if("Quarter".equals(period)){
			timeBucket = 24*60*60l;
			sdf = new SimpleDateFormat("yyyy-MM");
		}
		
		long startTime = EventsReportForms.getCalendar(period);
		
		Map sqlParam = EasyMap.build(
				"tenantid",idBean.getTenantId(),
				"starttime", startTime,
				"timeBucket", timeBucket);
	    String sql=	"	SELECT   "+
				"  floor((e.clock+28800)/#{timeBucket})   as moment,count(0) as eventsnum  "+
				" FROM                                            "+
				" events e                                        "+
				" WHERE e.object = 0                              "+
				"   AND e.clock >= #{starttime}                   "+
				"   AND e.tenantid = #{tenantid}                  "+
				" AND e.objectid in(                              "+
				"    select                                       "+
				"    t.triggerid                                  "+
				"    from                                         "+
				"    triggers t                                   "+
				"    inner join functions f                       "+
				"    on f.triggerid = t.triggerid                 ";
	    if(isweb){
	    	CHttpTestGet options = new CHttpTestGet();
	    	options.setHttptestIds(Long.valueOf(appid));
	    	options.setEditable(true);
			options.setOutput(new String[] { "httptestid","name" });
			options.setEditable(true);
			Map httpTests = reset((CArray<Map>)API.HttpTest(idBean, executor).get(options));
	    	String httpname =Nest.value(httpTests, "name").asString();
			String key1="web.test.time["+httpname+","+httpname+",resp]";
			String key2="web.test.fail["+httpname+"]";
			sqlParam.put("key1",key1);
			sqlParam.put("key2",key2);
	    	sql+="    inner join items i             "+
					"     on i.itemid =  f.itemid                   "+
	    			"   where i.key_ in(#{key1},#{key2})  "+
					"    group by moment  ";
	    }else{
	    	sqlParam.put("applicationid",appid);
	    	sql+= "    inner join items_applications a              "+
					"     on a.itemid =  f.itemid                   "+
	    			"   where a.applicationid =  #{applicationid})  "+
					"    group by moment  ";
	    }
	     CArray<Map> events = DBselect(executor, sql, sqlParam);
		 List reuslt = EasyList.build();
			
			Long _value = 0l;
			Long _time = 0l;
			long prev = (startTime + 28800) / timeBucket - 1;

			Calendar currentDate = Calendar.getInstance();
			currentDate.set(Calendar.HOUR_OF_DAY, 0);
			currentDate.set(Calendar.MINUTE, 0);
			currentDate.set(Calendar.SECOND, 0);
			currentDate.add(Calendar.DAY_OF_MONTH, 1);
			Long endTime = (currentDate.getTimeInMillis() / 1000 + 28800) / timeBucket;
			if ("Week".equals(period)) {
				endTime--;
			}

			Date date = new Date();
			events.push(CArray.map(// 将最后一个时间加到最后，进行补齐
					"moment", endTime, "eventsnum", 0));
			for (Map hostdata : events) {
				_time = Nest.value(hostdata, "moment").asLong();
				_value = Nest.value(hostdata, "eventsnum").asLong();

				long distance = _time - prev; // 补齐
				for (long i = 1; i <= distance; i++) {
					long vTime, vValue;
					if (i == distance) {
						vTime = timeBucket * _time;
						vValue = _value;
					} else {
						vTime = timeBucket * (prev + i);
						vValue = 0;
					}
					date.setTime((vTime - 28800) * 1000);
					reuslt.add(CArray.map("moment", sdf.format(date), "eventsnum", vValue));
				}
				prev = _time;
			}

			return reuslt;
	}
	
	/**统计最近一周每天产生的事件数目
	 * @param idBean
	 * @param executor
	 * @param applicationid
	 * @return
	 */
	public List<Map> statisticsEventNumByday (IIdentityBean idBean,SQLExecutor executor,Long applicationid){
		Long startTime = Cphp.time()-604800;
		List<Map> reuslt = new ArrayList<Map>();
		Map params = new HashMap();
		params.put("starttime", startTime);//统计开始时间
		
		SimpleDateFormat sdf = new SimpleDateFormat();
		Long timeBucket  =  24*60*60l;
		sdf = new SimpleDateFormat("d日");
		params.put("timeBucket", timeBucket);//时间段
		params.put("tenantid", idBean.getTenantId());//tenantid
		params.put("applicationid", applicationid);
		
		
		String eventsql = " SELECT floor((e.clock+28800)/ #{timeBucket}) time_,count(0) n from events e "+
				"   WHERE     e.object = 0         "+
				"   AND e.clock >= #{starttime}    "+
				"   AND e.tenantid = #{tenantid}   "+
				"   AND e.objectid in(             "+
				" SELECT t.triggerid               "+
				"  FROM triggers t                 "+
				" left JOIN functions f            "+
				"   ON f.triggerid = t.triggerid   "+
				" left join items_applications a   "+
				"   on a.itemid =  f.itemid        "+
				"   where a.applicationid =  #{applicationid}  )     "+
				"  group by time_                    ";
			CArray<Map> eventsCountMap = DBselect(executor, eventsql, params);
			Long _value = 0l;
			Long _time = 0l;
			long prev = (startTime+28800)/timeBucket-1;
			
			Calendar currentDate = Calendar.getInstance();
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);
			currentDate.add(Calendar.DAY_OF_MONTH, 1);
			Long endTime = (currentDate.getTimeInMillis()/1000+28800)/timeBucket;
			
			Date date = new Date();
			eventsCountMap.push(CArray.map(//将最后一个时间加到最后，进行补齐
				"time_", endTime,
				"n", 0
			));
			for(Map hostdata: eventsCountMap){
				_time = Nest.value(hostdata, "time_").asLong();
				_value = Nest.value(hostdata, "n").asLong();
				
				long distance = _time-prev; //补齐
				for(long i=1; i<=distance; i++){
					long vTime, vValue;
					if(i==distance){
						vTime = timeBucket * _time;
						vValue = _value;
					}else{
						vTime = timeBucket * (prev+i);
						vValue = 0;
					}
					date.setTime((vTime-28800)*1000);
					//reuslt.put(sdf.format(date), vValue);
					Map rs=new HashMap();
					rs.put("time", sdf.format(date));
					rs.put("num", vValue);
					reuslt.add(rs);
				}
				prev = _time;
			}
		
		return reuslt;
	}
	

	public Object getEventByAppid(SQLExecutor executor, IIdentityBean idBean, int eventType,long hostid,Long groupid,Long applicationid,String defprofix){

		   Object table = new CTableInfo(_("No events found."));
		   ((CTableInfo)table).setHeader(array(_("Time"), _("VM Name"), _("Description"), _("Duration")));
		   CArray<Map> events = array();
		   CTable paging = null;;
		   CArray knownTriggerIds = array();
		   CArray validTriggerIds = array();
			
			if(hostid > 0 && applicationid >0){
				try {
					CTriggerGet triggerOptions = new CTriggerGet();
					triggerOptions.setOutput(new String[]{"triggerid"});
					triggerOptions.setApplicationIds(applicationid);
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
					//eventOptions.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));
					eventOptions.setLimit(5);//只显示最近前5个
					
					if (hostid > 0) {
						eventOptions.setHostIds(hostid);
					} else if (groupid > 0) {
						triggerOptions.setGroupIds(groupid);
					}
					
					
					
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

						if (count(events) >=1000 || count(allEventsSlice) <= 1000) {
							break;
						}

						Nest.value(eventOptions,"eventid_till").$(Nest.value(lastEvent,"eventid").asLong() - 1);
					}
					
					events = array_slice(events, 0, 1001);

					// get paging
					paging = getPagingLine(idBean, executor, events);
					
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
					for (Map event : events) {
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
						
						String description =TvmUtil.getZHDescription(Nest.value(trigger, "description").asString());
						/*resolveEventDescription(idBean, executor, rda_array_merge(trigger, map(
							"clock" , Nest.value(event,"clock").$(),
							"ns" , Nest.value(event,"ns").$()
						)));*/
						
						
						// duration
						Map nextEvent;
						Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(idBean, executor, event, events))
							? rda_date2age(Nest.value(event,"clock").asLong(), Nest.value(nextEvent,"clock").asLong())
							: rda_date2age(Nest.value(event,"clock").asLong()));
						
						//String  hostName = removeDefaultProfix(Nest.value(host,"name").asString(),defprofix);
						Object hostName = Nest.value(host, "name").$();
						Object clock = rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong());
						
						((CTableInfo)table).addRow(array(
							clock,
							hostName,
							description,
							Nest.value(event,"duration").$()
						));
					}
				} catch (Exception e) {
					events = array();
					paging = getPagingLine(idBean, executor, events);
					e.printStackTrace();
				}
			}else {
				events = array();
				paging = getPagingLine(idBean, executor, events);
			}
			table = array(table,paging);
			return table;	   
	   
	
	}
	
	
	/**  租户获取单个设备最近事件
	 * @param executor
	 * @param idBean
	 * @param eventType 活动or历史
	 * @param hostid  设备id
	 * @param groupid  设备类型
	 * @return
	 */
	public Object getEvent(SQLExecutor executor, IIdentityBean idBean, int eventType,long hostid,Long groupid){
		   Object table = new CTableInfo(_("No events found."));
		   ((CTableInfo)table).setHeader(array(_("Time"), _("VM Name"), _("Description"), _("Duration")));
		   CArray<Map> events = array();
		   CTable paging = null;;
		   CArray knownTriggerIds = array();
		   CArray validTriggerIds = array();
			
			if(hostid > 0){
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
				//eventOptions.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));
				eventOptions.setLimit(1001);
				
				if (hostid > 0) {
					eventOptions.setHostIds(hostid);
				} else if (groupid > 0) {
					triggerOptions.setGroupIds(groupid);
				}
				
				
				
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

					if (count(events) >=1000 || count(allEventsSlice) <= 1000) {
						break;
					}

					Nest.value(eventOptions,"eventid_till").$(Nest.value(lastEvent,"eventid").asLong() - 1);
				}
				
				events = array_slice(events, 0, 1001);

				// get paging
				paging = getPagingLine(idBean, executor, events);
				
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
				eget.setLimit(5);//只显示最近前5个
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
				for (Map event : events) {
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
					
					String description =TvmUtil.getZHDescription(Nest.value(trigger, "description").asString());
					/*resolveEventDescription(idBean, executor, rda_array_merge(trigger, map(
						"clock" , Nest.value(event,"clock").$(),
						"ns" , Nest.value(event,"ns").$()
					)));*/
					
					
					// duration
					Map nextEvent;
					Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(idBean, executor, event, events))
						? rda_date2age(Nest.value(event,"clock").asLong(), Nest.value(nextEvent,"clock").asLong())
						: rda_date2age(Nest.value(event,"clock").asLong()));
					
					Object hostName = Nest.value(host,"name").$();
					Object clock = rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong());
					
					((CTableInfo)table).addRow(array(
						clock,
						hostName,
						description,
						Nest.value(event,"duration").$()
					));
				}
			}else {
				events = array();
				paging = getPagingLine(idBean, executor, events);
			}
			//table = array(table,paging);
			return table;	   
	   
	}
}
