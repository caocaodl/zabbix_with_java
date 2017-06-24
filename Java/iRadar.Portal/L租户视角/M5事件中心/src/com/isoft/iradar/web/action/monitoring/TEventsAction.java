package com.isoft.iradar.web.action.monitoring;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_combine;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_map;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strval;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.EventsUtil.get_next_event;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageNumber;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.isoft.Feature;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.Cphp.ArrayMapCallback;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TEventsAction extends RadarBaseAction {

	private final static String GROUPID_WEB = String.valueOf(Integer.MAX_VALUE);
	public static String DEFAULTEVENT = "events.action";
	public static String TACTIVEALARM = "tactivealarm.action";//时间控件ajax提交不了，修改为常量方式
	public static String THISTORYALARM = "thistoryalarm.action";
	private  static int Timeline_fixed_default = 1;//时间控件动态固态默认值
	private final static String SQL_UPDATE_VLAUE_EVENT = "update events set value=#{value} where eventid=#{eventid} and tenantid=#{tenantid}";
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			String name=null;
			if(TACTIVEALARM.equals(getPageFile())){
				name = _("Active_events_export");
			}else {
				name = _("History_events_export");
			}
			page("file", name+time+".csv");
		}else if(isset(_REQUEST, "actAlarmFaultNum")){ //页面顶部活动告警、故障数目    设置输出json数据
			page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
		} else {
			CSV_EXPORT = false;
			page("title", _("Latest events"));
			page("file", getPageFile());
			page("hist_arg", new String[] { "groupid", "hostid" });
			page("scripts", new String[] { "class.calendar.js", "gtlc.js" });
			//page("style", ".flicker_c{display:none} div.scrollbar .info{margin-top: 0} .headeractions{position: absolute; top: 15px; right: 10px;} .headeractions table.ui-widget-header{padding:0} #csv_export{margin-right:0} BODY{position: relative;}");
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("css", new String[] { "lessor/eventcenter/event.css" });
			
			if (PAGE_TYPE_HTML == Nest.as(page("type")).asInteger()) {
				define("RDA_PAGE_DO_REFRESH", 1);
			}
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray<Integer> allowed_sources = array(EVENT_SOURCE_TRIGGERS);
		
		//		VAR			TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"source",		array(T_RDA_INT, O_OPT, P_SYS,	IN(allowed_sources.valuesAsInteger()), null),
			"groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"server",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggerid",	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"period",		array(T_RDA_INT, O_OPT, null,	null,		null),
			"dec",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"inc",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"left",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"right",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"stime",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"load",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"csv_export",array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"filter_rst",	array(T_RDA_INT, O_OPT, P_SYS,	IN(array(0,1).valuesAsInteger()), null),
			"filter_set",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"actAlarmFaultNum",		array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"isIgnore",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"eventid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").$())) {
				CProfile.update(getIdentityBean(), executor, "web.events.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").$())) {
				if (isset(Nest.value(_REQUEST,"favid").$())) {
					Timeline_fixed_default=Nest.value(_REQUEST,"favid").asInteger();
					CProfile.update(getIdentityBean(), executor, "web.events.timelinefixed", Nest.value(_REQUEST,"favid").$(), Timeline_fixed_default);
				}
			}
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		
		//页面顶部的活动 告警、故障数目
		if(isset(_REQUEST, "actAlarmFaultNum")){
			String actAlarmNum=getEventList(executor,true).get(0) ;//活动告警数目
			//String actFaultNum=getEventList(executor,false).get(0);//活动故障数
			
			echo("[{\"actAlarmNum\":\""+actAlarmNum+"\",\"actFaultNum\":\""+actAlarmNum+"\"}]");
			return true;
		}
			
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Filter */
		Nest.value(_REQUEST, "triggerid").$(0);
		
		String groupId = Nest.value(_REQUEST,"groupid").asString();
		
		CWidget eventsWidget = new CWidget();
		
		if(isset(_REQUEST, "isIgnore") && isset(_REQUEST, "eventid")){
			final Map eventmap = map(
					"eventid", Nest.value(_REQUEST ,"eventid").asString(),
					"tenantid", getIdentityBean().getTenantId(),
					"value", 0 );
			try {
				boolean result= DBUtil.DBexecute(executor, SQL_UPDATE_VLAUE_EVENT, eventmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			unset(_REQUEST,"eventid");
			unset(_REQUEST,"isIgnore");
		}
		
		
		// header
		CForm frmForm = new CForm();
		if (isset(_REQUEST,"source")) {
			frmForm.addVar("source", Nest.value(_REQUEST,"source").asString(), "source_csv");
		}
		if (isset(_REQUEST,"stime")) {
			frmForm.addVar("stime", Nest.value(_REQUEST,"stime").asString(), "stime_csv");
		}
		if (isset(_REQUEST,"period")) {
			frmForm.addVar("period", Nest.value(_REQUEST,"period").asString(), "period_csv");
		}
		frmForm.addVar("page", String.valueOf(getPageNumber(getIdentityBean(), executor)), "page_csv");
		frmForm.addVar("groupid", groupId, "groupid_csv");
		
		if(Feature.showExportCsvIcon){
			frmForm.addItem(new CSubmit("csv_export", _("Export to CSV"),"","orange export"));
		}
		
		CForm r_form = new CForm("get");
		r_form.addVar("fullscreen", Nest.value(_REQUEST,"fullscreen").asString());
		r_form.addVar("stime", get_request("stime"));
		r_form.addVar("period", get_request("period"));
		
		CPageFilter getpagefilter = new CPageFilter(getIdBean(), executor);
		
		CComboBox cob=getpagefilter.getCB("groupid", groupId, map());
		cob.addItem(IMonConsts.MON_VM, "云主机监察");
		cob.addItem(IMonConsts.DISCOVERED_HOSTS, "服务应用监察");
		cob.addItem(GROUPID_WEB, "网站监察");
		r_form.addItem(array(Cphp._("Group"), SPACE, cob));
		
		Long[] triggerIds = null;
		if(GROUPID_WEB.equals(groupId)) {
			groupId = IMonConsts.MON_VM.toString();
			Nest.value(_REQUEST, "groupid").$(groupId);
			String SQL_WEB_FIRSTEVENT = "" +
					"  SELECT f.triggerid triggerId" + 
					"  FROM functions f" + 
					"       LEFT JOIN items i ON f.itemid = i.itemid AND f.tenantid = i.tenantid" + 
					" WHERE i.key_ LIKE 'web.test.%'";
			
			CArray<Map> map = DBselect(executor, SQL_WEB_FIRSTEVENT);
			triggerIds = FuncsUtil.rda_toHash(map, "triggerId").keysAsLong();
		}else if(IMonConsts.MON_VM.toString().equals(groupId)) {
			groupId = IMonConsts.MON_VM.toString();
			Nest.value(_REQUEST, "groupid").$(groupId);
			String SQL_VM_FIRSTEVENT = "" +                                                       
					" SELECT tr.triggerid  triggerId                                              "+
					" FROM triggers tr                                                            "+
					" INNER JOIN functions f ON f.triggerid = tr.triggerid                        "+
					" INNER JOIN items i ON i.itemid = f.itemid                                   "+
					" INNER JOIN hosts h ON h.hostid = i.hostid                                   "+
					"   WHERE  h.hostid_os IS NOT NULL                                            "+
					"     AND i.key_ NOT LIKE 'web.test.%'                                    "+
					"     AND i.itemid not in(                                                    "+
					" 		SELECT DISTINCT(i.itemid)  FROM applications a,items_applications i   "+
					" 		WHERE i.applicationid = a.applicationid                               "+
					" 		AND  a.type in('2','3')                                               "+
					" 		AND  a.tenantid = #{tenantid} )                                       ";
			CArray<Map> map = DBselect(executor, SQL_VM_FIRSTEVENT,map("tenantid",getIdentityBean().getTenantId()));
			triggerIds = FuncsUtil.rda_toHash(map, "triggerId").keysAsLong();
		}else if(IMonConsts.DISCOVERED_HOSTS.toString().equals(groupId)){
			groupId = IMonConsts.MON_VM.toString();
			Nest.value(_REQUEST, "groupid").$(groupId);
			String SQL_APP_FIRSTEVENT = "" +
					" SELECT tr.triggerid   triggerId                                  " +
					" FROM triggers tr                                                 " +
					" INNER JOIN functions f ON f.triggerid = tr.triggerid             " + 
					" INNER JOIN items_applications i ON i.itemid = f.itemid           " +
					" INNER JOIN applications a ON i.applicationid = a.applicationid   " +
					"	WHERE a.type in('2','3')                                       " +
					"	    and a.tenantid = #{tenantid}                               " ; 
			CArray<Map> map = DBselect(executor, SQL_APP_FIRSTEVENT,map("tenantid",getIdentityBean().getTenantId()));
			triggerIds = FuncsUtil.rda_toHash(map, "triggerId").keysAsLong();
		}
				
		eventsWidget.addHeader(r_form);
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(frmForm));
		eventsWidget.addItem(headerActions);
		
		CDiv scroll = new CDiv();
		scroll.setAttribute("id", "scrollbar_cntr");
		eventsWidget.addFlicker(scroll, 1);
		
		/* Display */
		Object table = new CTableInfo(_("No events found."));

		String sourceName = "trigger";
		// trigger events

		Map firstEvent = null;
		CEventGet eget = new CEventGet();
		if(triggerIds != null) eget.setTriggerIds(triggerIds);
		eget.setSource(EVENT_SOURCE_TRIGGERS);
		eget.setObject(EVENT_OBJECT_TRIGGER);
		eget.setOutput(API_OUTPUT_EXTEND);
		eget.setSortfield("clock");
		eget.setSortorder(RDA_SORT_UP);
		eget.setLimit(1);
		CArray<Map> firstEvents = API.Event(getIdentityBean(), executor).get(eget);
		firstEvent = reset(firstEvents);
		
		if (isset(_REQUEST,"period")) {
			int period = get_request("period", RDA_PERIOD_DEFAULT);
			if(period>63072000){
				period=63072000;
			}
			Nest.value(_REQUEST,"period").$(period);
			CProfile.update(getIdentityBean(), executor, "web.events."+sourceName+".period", Nest.value(_REQUEST,"period").$(), PROFILE_TYPE_INT);
		} else {
			//Nest.value(_REQUEST,"period").$(CProfile.get(getIdentityBean(), executor, "web.events."+sourceName+".period"));
			Nest.value(_REQUEST,"period").$(63072000);//默认显示全部
		}
		
		int effectiveperiod = navigation_bar_calc(getIdentityBean(), executor);
		long from = rdaDateToTime(Nest.value(_REQUEST,"stime").asString());
		long till = from + effectiveperiod;
		
		
		CTable paging = null;
		Long starttime = null;
		CArray<Map> events = null;
		if (firstEvent == null) {
			events = array();
			//paging = getPagingLine(getIdentityBean(), executor, events);
		} else {
			Map config = select_config(getIdentityBean(), executor);
			starttime = Nest.value(firstEvent,"clock").asLong();
			
			// source not discovery i.e. trigger
			//CCol hostCCol = new CCol((Nest.value(_REQUEST,"hostid").asInteger() == 0) ? _("Host") : null,null,null,"5%");
			((CTableInfo)table).setHeader(array(
				_("Time"),
				(Nest.value(_REQUEST,"hostid").asInteger() == 0) ? make_sorting_header(_("By host"),"hostname") : null,
				_("Description"),
				_("Duration"),
				isShowHistory()? null:"忽略"));
			
			if (CSV_EXPORT) {
				csvRows.add( array(
					_("Time"),
					(Nest.value(_REQUEST,"hostid").asInteger() == 0) ? _("Host") : null,
					_("Description"),
					_("Duration")
				) );
			}
			
				
			CArray knownTriggerIds = array();
			CArray validTriggerIds = array();
			
			CTriggerGet triggerOptions = new CTriggerGet();
			if(triggerIds != null) triggerOptions.setTriggerIds(triggerIds);
			triggerOptions.setOutput(new String[]{"triggerid"});
			triggerOptions.setPreserveKeys(true);
			triggerOptions.setMonitored(true);
			//hook trigger options
			doTriggerFiler(triggerOptions);
			
			int allEventsSliceLimit = Nest.value(config,"search_limit").asInteger();
			
			CEventGet eventOptions = new CEventGet();
			if(triggerIds != null) eventOptions.setTriggerIds(triggerIds);
			eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
			eventOptions.setObject(EVENT_OBJECT_TRIGGER);
			eventOptions.setTimeFrom(from);
			eventOptions.setTimeTill(till);
			eventOptions.setOutput(new String[]{"eventid", "objectid"});
			eventOptions.setSortfield("clock", "eventid");
			eventOptions.setSortorder(RDA_SORT_DOWN);
			eventOptions.setLimit(allEventsSliceLimit + 1);
			//根据事件的条件过滤查询结果
			doEventsFiler(eventOptions);
			
			if(!Cphp.empty(Nest.value(_REQUEST,"groupid").asLong())) {
				eventOptions.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
				triggerOptions.setGroupIds(Nest.value(_REQUEST,"groupid").asLong());
			}
			
			events = array();
			
			while (true) {
				CArray<Map> allEventsSlice = API.Event(getIdentityBean(), executor).get(eventOptions);

				CArray triggerIdsFromSlice = array_keys(array_flip(rda_objectValues(allEventsSlice, "objectid")));

				CArray unknownTriggerIds = array_diff(triggerIdsFromSlice, knownTriggerIds);

				if (!empty(unknownTriggerIds)) {
					Nest.value(triggerOptions,"triggerids").$(unknownTriggerIds);
					CArray<Map> validTriggersFromSlice = API.Trigger(getIdentityBean(), executor).get(triggerOptions);

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
				if (count(events) >= Nest.value(config,"search_limit").asInteger() || count(allEventsSlice) <= allEventsSliceLimit) {
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
			events = array_slice(events, 0, Nest.value(config,"search_limit").asInteger() + 1);

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
			
			CArray<Map> eventsData = array();
			// events
			for (Map event : events) {
				CArray trigger = CArray.valueOf(triggers.get(event.get("objectid")));
				
				Map host = reset(Nest.value(trigger,"hosts").asCArray());
				if(empty(host))
					continue;
				host = hosts.get(host.get("hostid"));
				
				CArray triggerItems = array();
				Nest.value(trigger,"items").$(resolveItemNames(getIdentityBean(), executor, Nest.value(trigger,"items").$s()));
				
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
				
				/*String description = resolveEventDescription(getIdentityBean(), executor, rda_array_merge(trigger, map(
					"clock" , Nest.value(event,"clock").$(),
					"ns" , Nest.value(event,"ns").$()
				)));*/
				String description = TvmUtil.getZHDescription(Nest.value(trigger, "description").asString());//租户描述修改为中文表达式
				// duration
				Map nextEvent;
				Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(getIdentityBean(), executor, event, events))
					? rda_date2age(Nest.value(event,"clock").asLong(), Nest.value(nextEvent,"clock").asLong())
					: rda_date2age(Nest.value(event,"clock").asLong()));
				
				Object hostName = (Nest.value(_REQUEST,"hostid").asLong() == 0) ? Nest.value(host,"name").$() : null;
				Object clock = rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong());
				
				eventsData.add(map("clock",clock,
						   "hostname",hostName,
						   "description",description,
						   "duration",Nest.value(event,"duration").$(),
						   "showHistory",isShowHistory()? null:new CLink("忽略",getPageFile()+"?isIgnore=true&eventid="+Nest.value(event,"eventid").asString(),"action")));
		
				
				if (CSV_EXPORT) {
					csvRows.add( array(
						clock,
						hostName,
						description,
						//trigger_value2str(Nest.value(event,"value").asInteger()),//在导出中，删除问题严重性这列
						Nest.value(event,"duration").$()
					));
				}
			}
			
			order_result(eventsData, getPageSortField(getIdentityBean(), executor), empty(get_request("sortorder", null))?Defines.RDA_SORT_DOWN:getPageSortOrder(getIdentityBean(), executor));
			// get paging
			if(!CSV_EXPORT){					
				paging = getPagingLine(getIdentityBean(), executor, eventsData);
			}
			
			for(Map event:eventsData){
				((CTableInfo)table).addRow(array(
						Nest.value(event, "clock").$(),
						Nest.value(event, "hostname").$(),
						Nest.value(event, "description").$(),
						Nest.value(event,"duration").$(),
						Nest.value(event, "showHistory").$()
				));
			}
			
		}
		
		if (CSV_EXPORT) {
			print(rda_toCSV(csvRows));
			return;
		}
		
		table = array(table, paging);
		
		eventsWidget.addItem(table);
		
		CArray timeline = map(
				"period", effectiveperiod,
				"starttime", date(TIMESTAMP_FORMAT, starttime),
				"usertime", date(TIMESTAMP_FORMAT, till)
		);
		
		CArray objData = map(
				"id", "timeline_1",
				"loadSBox", 0,
				"loadImage", 0,
				"loadScroll", 1,
				"dynamic", 0,
				"mainObject", 1,
				"periodFixed", CProfile.get(getIdentityBean(), executor, "web.events.timelinefixed", Timeline_fixed_default),
				"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);
		
		rda_add_post_js("jqBlink.blink();");
		rda_add_post_js("timeControl.addObject(\"scroll_events_id\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(objData)+");");
		rda_add_post_js("timeControl.processObjects();");
		
		eventsWidget.show();
	}
	
	protected String getPageTitle() {
		return _("HISTORY OF EVENTS");
	}
	
	protected String getPageName() {
		return _("Events");
	}
	
	protected boolean isShowWarnings() {
		return true;
	}
	
	protected boolean isShowErrors() {
		return true;
	}
	
	protected String getPageFile(){
       return "events.action";
    }
	
	/**
     * hook TriggerFiler
     * priority 0、1、2 --> Warnings
     * priority 3、4、5 --> Erros
     * @param triggerOptions
     */
	private void doTriggerFiler(CTriggerGet triggerOptions) {
		if (isShowWarnings() && isShowErrors()) {
			//show all
		}else {
			if(isShowWarnings()) {
				triggerOptions.setFilter("priority", 
						Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED, 
						Defines.TRIGGER_SEVERITY_INFORMATION, 
						Defines.TRIGGER_SEVERITY_WARNING
					);
			}else {
				triggerOptions.setFilter("priority", 
						Defines.TRIGGER_SEVERITY_AVERAGE, 
						Defines.TRIGGER_SEVERITY_HIGH, 
						Defines.TRIGGER_SEVERITY_DISASTER,
						Defines.TRIGGER_SEVERITY_COUNT
					);
			}
		}
	}

	protected boolean isShowActive() {
		return true;
	}
	
	protected boolean isShowHistory() {
		return true;
	}
	
    /**
     * filter Events
     * value 0 --> History 
     * value 1 --> Active
     * @param cEventGet
     */
	private void doEventsFiler(CEventGet cEventGet) {
		if(isShowActive() && isShowHistory()) {
			//show all
		}else {
			cEventGet.setValue(String.valueOf(
					isShowHistory()? 
					Defines.TRIGGER_VALUE_FALSE: 
					Defines.TRIGGER_VALUE_TRUE
				));
		}
	}
    
    
	/**获取活动告警或者活动故障数目以及最近告警描述方法
	 * @param executor sql执行程序
	 * @param isShowWarnings true为告警，false则为故障
	 * @return
	 */
	public List<String> getEventList(SQLExecutor executor,boolean isShowWarnings){
		List<String> eventlist=new ArrayList<String>();
		//定义要查找的告警或者故障表
		CTriggerGet triggerOptions = new CTriggerGet();
		triggerOptions.setOutput(new String[]{"triggerid"});
		triggerOptions.setPreserveKeys(true);
		triggerOptions.setMonitored(true);
		
		//定义要查找的事件表
		CEventGet eventOptions = new CEventGet();
		eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
		eventOptions.setObject(EVENT_OBJECT_TRIGGER);
		eventOptions.setAcknowledged(Nest.as(Defines.EVENT_NOT_ACKNOWLEDGED).asBoolean());
		eventOptions.setOutput(new String[]{"eventid", "objectid"});
		eventOptions.setSortfield("clock", "eventid");//排序字段
		eventOptions.setSortorder(RDA_SORT_DOWN);//倒序
		//根据事件的条件过滤查询结果，1是活动数据，而非历史数据，这要是区分处理或未处理关键
		eventOptions.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));
		
		/*
		 * 以下是取得告警表和事件表的关键，总体思路是取两者的并集并过滤两者相同结果集
		 */
        CArray filterTriggerIds;
		
		CTriggerGet tget = new CTriggerGet();
		CArray knownTriggerIds = array();
		CArray validTriggerIds = array();
		
		tget.setOutput(new String[]{"triggerid"});
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
		
		eventOptions.setObjectIds(validTriggerIds.valuesAsLong());
		/*这段代码比较难懂，也是关键部分*/
		CArray<Map> allEventsSlice = API.Event(getIdentityBean(), executor).get(eventOptions);
		
		CArray triggerIdsFromSlice = array_keys(array_flip(rda_objectValues(allEventsSlice, "objectid")));

		CArray unknownTriggerIds = array_diff(triggerIdsFromSlice, knownTriggerIds);
		
		if (!empty(unknownTriggerIds)) {
			Nest.value(triggerOptions,"triggerids").$(unknownTriggerIds);
			CArray<Map> validTriggersFromSlice = API.Trigger(getIdentityBean(), executor).get(triggerOptions);

			for(Map trigger:validTriggersFromSlice) {
				validTriggerIds.put(trigger.get("triggerid"),trigger.get("triggerid"));
			}
		}
		
		CArray<Map> events = array();
		
		for(Map event:allEventsSlice) {
			if (isset(validTriggerIds,event.get("objectid"))) {
				events.add(map("eventid" , Nest.value(event,"eventid").$()));//符合条件的事件，这里只存有其id值
			}
		}
		int number = count(events);//取得事件总数目
		
		if(number>100){//如果数目超过100，则表示为100+
			eventlist.add("100+");
		}else{
			eventlist.add(number+"");
		}
		
		return eventlist;
	}
	  
	public static void main(String[] args){
		SimpleDateFormat smp=new SimpleDateFormat("yyyyMMddHHmmss");
		Date date=new Date();
        System.out.println(smp.format(date));
        
        String des = null;
        String[] str=des.split("=");
        System.out.println(str.length);
        if(str.length==2){
        	System.out.println(str[1]);
        }else{
        	 System.out.println(des);
        }
	}
	
}
