package com.isoft.web.bean.reportForms;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.CTSeverity;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 告警中心报表
 * @author LiuBoTao
 *
 */
public class EventsReportForms extends RadarBaseAction{
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
			
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", _("Events_report")+time+".csv");
		}else{
			page("file", "events_report.action");	
			page("title", _("eventsReportForm"));	
			page("type", "ajax".equals(getParameter("output"))? Defines.PAGE_TYPE_JSON: Defines.PAGE_TYPE_HTML);
			page("css", new String[] { "tenant/edit.css", "lessor/reportcenter/report_performance.css"});
			page("js", new String[] {"imon/report/event_report.js","imon/report/echarts-all.js"});	//引入改变发现规则状态所需JS
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		CArray fields = map(
				"period",		array(T_RDA_STR, O_OPT, null,	null,		null),
				"csv_export",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
				"csv_period",		array(T_RDA_STR, O_OPT, null,	null,		null)
			);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		CArray data=map();
		AjaxResponse ajaxResponse = new AjaxResponse();
		if(isset(_REQUEST, "period")){
			 String period = Nest.value(_REQUEST, "period").asString();
			 data= getStatisticsData(executor,period);
			 ajaxResponse.success(data);
			 ajaxResponse.send();
			 return true;
		}
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		if (CSV_EXPORT) {
			csvRows.add(array("","新产生告警","恢复告警","平均新增告警","平均恢复告警"));
			String[] daytype=new String[]{"Day","Week","Month","Quarter"};
			String[] timestr=new String[]{"今日","最近一周","最近一月","最近一季度"};
			for(int i=0;i<daytype.length;i++){
				Map oneMap = getCountNum(executor,daytype[i]);
				csvRows.add(array(timestr[i],oneMap.get("totalNum").toString(),
				                       oneMap.get("restoreNum").toString(),
				                       oneMap.get("addavg").toString(),
				                       oneMap.get("restoreAvg").toString()));
			}
		}
		if (CSV_EXPORT) {
			 String period = Nest.value(_REQUEST, "csv_period").asString();
			csvRows.add("");
			CArray addnums   = statisticsTriggerNum(executor,period, false);//新增告警
			CArray solveNums = statisticsTriggerNum(executor,period, true);//恢复告警
				
			csvRows.add(array("时间","新增告警","恢复告警"));
			String solvestr = ReportUtil.getCurlybraces(solveNums.toString());
			String[] solvearr = solvestr.split(",");
			
			String addstr = ReportUtil.getCurlybraces(addnums.toString());
			String[] addarr = addstr.split(",");
			for(int i=0;i<addnums.size();i++ ){
				String[] solves=solvearr[i].split("=");
				String[] adds=addarr[i].split("=");
				csvRows.add(array("\r"+solves[0],adds[1],solves[1]));
			}
		}
		if (CSV_EXPORT) {
			 String period = Nest.value(_REQUEST, "csv_period").asString();
			csvRows.add("");
			csvRows.add(array("按照设备类型统计"));
			CArray<Map> pieForTypeDate = eventStatistic(executor,"hostType",period);
			csvRows.add(array("设备类型","产生告警数量"));
			String pieForTypestr = ReportUtil.getCurlybraces(pieForTypeDate.toString());
			String[] pieForTypearr = pieForTypestr.split(",");
			for(int i=0;i<pieForTypeDate.size();i++ ){
				String[] pieForType=pieForTypearr[i].split("=");
				csvRows.add(array(pieForType[0],pieForType[1]));
			}

			csvRows.add("");
			csvRows.add(array("按照事件严重等级统计"));
			CArray<Map> pieForLevelDate = eventStatistic(executor,"eventLevel",period);
			csvRows.add(array("事件严重等级","产生告警数量"));
			String pieForLevelstr = ReportUtil.getCurlybraces(pieForLevelDate.toString());
			String[] pieForLevelarr = pieForLevelstr.split(",");
			for(int i=0;i<pieForLevelDate.size();i++ ){
				String[] pieForLevels=pieForLevelarr[i].split("=");
				csvRows.add(array(pieForLevels[0],pieForLevels[1]));
			}
		}
		
		if (CSV_EXPORT) {
			print(rda_toCSV(csvRows));
			return;
		}
		
		CWidget widget = new CWidget();
		CPageFilter pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups" , map(
						"monitored_hosts", true,
						"with_monitored_triggers", true
					),
					"hosts", map(
						"editable", true,
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
		//导出按钮
		CButton csvexport = new CButton("csv_export", _("Export to CSV"),"","eventcsvexport export");
		CDiv ctn = new CDiv(csvexport, "event_csv_exportid");
		//add period
		CDiv period = new CDiv(null, "select_ctn topn_period");
		period.addItem(new CSpan("统计周期："));
		period.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "Period",
			"name", "Period",
			"value", "0"
		), array("日", "周", "月","季度"), false));
		period.addItem(ctn);

		//add table 
		CDiv div = new CDiv("","totalDivClass");	
		CDiv leftDiv = new CDiv("","leftDivClass");
		CDiv rightDiv = new CDiv("","rightDivClass");

		CDiv leftTopDiv = new CDiv(createEventGeneralTable(executor),"leftTopDivClass");
		CDiv leftBottomDiv = new CDiv("柱状图","leftbottomClass","leftbottom");
		CDiv rightTopDiv = new CDiv("饼状图","rightTopClass","rightTop");
		CDiv rightBottomoDiv = new CDiv("饼状图","rightbottomClass","rightbottom");

		
		leftDiv.addItem(array(period,leftTopDiv,leftBottomDiv));
		rightDiv.addItem(array(rightTopDiv,rightBottomoDiv));
		div.addItem(array(leftDiv,rightDiv));
		widget.addItem(div);
		widget.show();
	}
	
	/**
	 * 获取统计数据
	 * @param executor
	 * @param period
	 * @return
	 */
	private CArray getStatisticsData(SQLExecutor executor,String period){
		CArray ajaxData = array();
		ajaxData.put("triggerbarData", CArray.map(
			"addNum", statisticsTriggerNum(executor,period, false),
			"solveNum", statisticsTriggerNum(executor,period, true)
		));
		ajaxData.put("pieForTypeDate", eventStatistic(executor,"hostType",period));
		ajaxData.put("pieForLevelDate", eventStatistic(executor,"eventLevel",period));
		return ajaxData;
	}
	
	/**
	 * 生成告警概况 0：历史告警 1活动告警
	 * @return
	 */
	public CDiv createEventGeneralTable(SQLExecutor executor){
		CDiv eventDiv = new CDiv("","eventclass");
		Map oneMap = getCountNum(executor,"Day");

		CArray strArray = array();
		strArray.add("今日新产生告警");
		strArray.add(new CSpan(oneMap.get("totalNum")));
		/*strArray.add("条，被触发");
		strArray.add(new CSpan(oneMap.get("repeatNum")));*/
		strArray.add("条，今日恢复告警");
		strArray.add(new CSpan(oneMap.get("restoreNum")));
		strArray.add("条，平均每日新增");
		strArray.add(new CSpan(oneMap.get("addavg")));
		strArray.add("条，平均每日恢复");
		strArray.add(new CSpan(oneMap.get("restoreAvg")));
		strArray.add("条");
		eventDiv.addItem(new CDiv(strArray));
		
		oneMap = getCountNum(executor,"Week");
		strArray = array();
		strArray.add("最近一周新产生告警");
		strArray.add(new CSpan(oneMap.get("totalNum")));
		/*strArray.add("条，被触发");
		strArray.add(new CSpan(oneMap.get("repeatNum")));*/
		strArray.add("条，最近一周恢复告警");
		strArray.add(new CSpan(oneMap.get("restoreNum")));
		strArray.add("条，平均每日新增");
		strArray.add(new CSpan(oneMap.get("addavg")));
		strArray.add("条，平均每日恢复");
		strArray.add(new CSpan(oneMap.get("restoreAvg")));
		strArray.add("条");
		eventDiv.addItem(new CDiv(strArray));
		
		oneMap = getCountNum(executor,"Month");
		strArray = array();
		strArray.add("最近一月新产生告警");
		strArray.add(new CSpan(oneMap.get("totalNum")));
		/*strArray.add("条，被触发");
		strArray.add(new CSpan(oneMap.get("repeatNum")));*/
		strArray.add("条，最近一月恢复告警");
		strArray.add(new CSpan(oneMap.get("restoreNum")));
		strArray.add("条，平均每日新增");
		strArray.add(new CSpan(oneMap.get("addavg")));
		strArray.add("条，平均每日恢复");
		strArray.add(new CSpan(oneMap.get("restoreAvg")));
		strArray.add("条");
		eventDiv.addItem(new CDiv(strArray));
		
		oneMap = getCountNum(executor,"Quarter");
		strArray = array();
		strArray.add("最近一季度新产生告警");
		strArray.add(new CSpan(oneMap.get("totalNum")));
/*		strArray.add("条，被触发");
		strArray.add(new CSpan(oneMap.get("repeatNum")));*/
		strArray.add("条，最近一季度恢复告警");
		strArray.add(new CSpan(oneMap.get("restoreNum")));
		strArray.add("条，平均每日新增");
		strArray.add(new CSpan(oneMap.get("addavg")));
		strArray.add("条，平均每日恢复");
		strArray.add(new CSpan(oneMap.get("restoreAvg")));
		strArray.add("条");
		eventDiv.addItem(new CDiv(strArray));
		
		return eventDiv;
	}
	
	/**
	 * 统计某段时间内增长或者恢复的告警数和每日的平均数
	 * @isHistroy 0新增 1恢复
	 * @return
	 */
	private Map getCountNum(SQLExecutor executor,String flag){
		Map params = new HashMap();
		Map result = new HashMap();
		Calendar currentDate = Calendar.getInstance();  
		
		int day = 1;
		int addavg = 1;
		int restoreAvg = 1;
		//开始时间
		if(flag.equals("Day")){	//获取当天的开始时间毫秒数
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);
		}else if(flag.equals("Week")){ //获取本周的开始时间
			currentDate.add(Calendar.DATE, -7);
			day = 7;
		}else if(flag.equals("Month")){
			currentDate.add(Calendar.MONTH, -1);
			day = currentDate.getActualMaximum(Calendar.DATE);
		}else if(flag.equals("Quarter")){//计算出季度的天数		
			currentDate.add(Calendar.MONTH, -2);
			
			Calendar quarter = currentDate;
			day = quarter.getActualMaximum(Calendar.DATE);
			quarter.add(Calendar.MONTH, +1);
			day += quarter.getActualMaximum(Calendar.DATE);
			quarter.add(Calendar.MONTH, +1);
			day += quarter.getActualMaximum(Calendar.DATE);
			
			currentDate.add(Calendar.MONTH, -2);
		}
		
		params.put("starttime",currentDate.getTimeInMillis()/1000);
		params.put("tenantid",getIdentityBean().getTenantId());
		//统计新增告警数量
		String SQL = "SELECT COUNT(1) as num FROM (select 1 FROM events e WHERE e.clock >= #{starttime} AND e.source = 0 AND e.object = 0 AND e.tenantid=#{tenantid}) t";
		CArray<Map> hostcarray = DBselect(executor, SQL,params);
		if(!hostcarray.isEmpty()){
			result.put("totalNum", hostcarray.get(0).get("num"));
			addavg = Integer.parseInt(hostcarray.get(0).get("num").toString()) / day;
		}
		
		//统计恢复的告警数量
		SQL = "select count(1) num from ( " + 
				"  select * from (select * " + 
				"    from events e " + 
				"   where e.source = 0 and e.object = 0 and e.clock>=#{starttime} and e.tenantid=#{tenantid} " + 
				"   order by e.clock desc " + 
				"   ) tab   " + 
				") t  " + 
				"where t.value = " + Defines.TRIGGER_VALUE_FALSE;
		
		hostcarray = DBselect(executor, SQL,params);
		if(!hostcarray.isEmpty()){
			result.put("restoreNum", hostcarray.get(0).get("num"));
			restoreAvg = Integer.parseInt(hostcarray.get(0).get("num").toString()) / day;
		}
		
		/*//统计重复告警数
		SQL = "SELECT COUNT(1) as num FROM events e WHERE e.clock >= #{starttime} AND e.source = 0 AND e.object = 0 and e.tenantid = #{tenantid}";
		hostcarray = DBselect(executor, SQL,params);
		if(!hostcarray.isEmpty()){
			result.put("repeatNum", hostcarray.get(0).get("num"));
		}*/
		//均值
		result.put("addavg", addavg);
		result.put("restoreAvg", restoreAvg);
		
		return result;
	}
	
	
	/**
	 * 以类型和级别对告警进行统计(饼状图)
	 * @param type   统计类型	
	 * @param period 统计周期
	 * @return
	 */
	public CArray<Map> eventStatistic(SQLExecutor executor,String type ,String period){
		CArray<Map> result = array();
		Map params = CArray.map();
		Long startTime = getCalendar(period);
		params.put("starttime", startTime);//统计开始时间
		params.put("tenantid", getIdBean().getTenantId());
		CArray<Map> eventNums = array();
		
		if(type.equals("hostType")){	//按设备类型统计
			String query_eventid_sql = "" +
					"SELECT " +
					"	g.name groupName, " +
					"	count(tab.tid) eventNum " + 
					"  FROM groups g " + 
					"       INNER JOIN " + 
					"       (SELECT hg.groupid gid, e.eventid tid " + 
					"          FROM events e " + 
					"               INNER JOIN triggers t ON e.objectid = t.triggerid " + 
					"               INNER JOIN functions f ON f.triggerid = t.triggerid " + 
					"               INNER JOIN items i ON i.itemid = f.itemid " + 
					"               RIGHT JOIN hosts_groups hg ON hg.hostid = i.hostid " + 
					"         WHERE     hg.groupid > 100 " + 
					"               AND t.status = 0 " + 
					"               AND e.source = 0 " + 
					"              AND e.object = 0 " + 
					"               AND e.clock >= #{starttime} " + 
					"               AND e.tenantid = #{tenantid}" + 
					"        ) tab " + 
					"          ON tab.gid = g.groupid " + 
					"GROUP BY tab.gid";

			
			CArray<Map> events = DBselect(executor, query_eventid_sql,params);
			int i=0;
			for(Map event: events){
				String groupName = Nest.value(event, "groupName").asString();
				int eventNum = Nest.value(event, "eventNum").asInteger();
				result.put(groupName, eventNum);
				i++;
				if(i>13){
					break;
				}
			}
		}else if(type.equals("eventLevel")){ //根据等级统计告警数
			String levelSQl = "" +
					"SELECT " +
					"	t.priority level, " +
					"	count(t.triggerid) eventNum " + 
					"  FROM events e INNER JOIN triggers t ON e.objectid = t.triggerid " + 
					" WHERE     t.status = 0 " + 
					"       AND e.source = 0 " + 
					"       AND e.object = 0 " + 
					"       AND e.objectid = t.triggerid " + 
					"       AND e.clock >= #{starttime} " + 
					"       AND e.tenantid = #{tenantid} " + 
					"GROUP BY t.priority";
			Map<Integer, String> priorityMap = returnPriority(executor,getIdBean());
			
			eventNums = DBselect(executor, levelSQl,params);
			CArray hash = FuncsUtil.rda_toHash(eventNums, "level");
			for(Entry<Integer, String> entry: priorityMap.entrySet()){
				int level = entry.getKey();
				String levelName = entry.getValue();
				if(Nest.value(hash, level, "eventNum").asInteger() != 0){					
					result.put(levelName, Nest.value(hash, level, "eventNum").asInteger());
				}
			}
		}
		return result;
	}
	
	/**
	 * 统计每个时间段内新增和解决告警的个数
	 * @param executor
	 * @param period 周期
	 * @isHistroy 0新增 1恢复
	 * @return
	 */
	public CArray<Map> statisticsTriggerNum (SQLExecutor executor,String period, boolean isHistory){
		Long startTime = getCalendar(period);
		Map params = new HashMap();
		CArray<Map> reuslt = array();
		
		params.put("starttime", startTime);//统计开始时间
		params.put("isHistroy", Defines.TRIGGER_VALUE_FALSE);//新增、恢复标识
		
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
			timeBucket = 90*24*60*60l;
			sdf = new SimpleDateFormat("yyyy-MM");
		}
		
		params.put("timeBucket", timeBucket);//时间段
		params.put("tenantid", getIdBean().getTenantId());//tenantid
		
		if("Quarter".equals(period)) {
			String EVENT_SQL = 
					"select count(1) v from ( " +
					"select  " + 
					"    e.eventid" + 
					"  from " + 
					"    events e  " + 
					"  where e.source = 0 and e.object = 0 and e.clock>=#{starttime} and e.clock<#{endtime} and e.tenantid=#{tenantid} "+
					") t ";
			if(isHistory) {
				EVENT_SQL = 
					"select count(1) v from ( " + 
					"  select * from (select * " + 
					"    from events e " + 
					"   where e.source = 0 and e.object = 0 and e.clock>=#{starttime} and e.clock<#{endtime} and e.tenantid=#{tenantid} " + 
					"   order by e.clock desc " + 
					"   ) tab   " + 
					") t  " + 
					"where t.value = " + Defines.TRIGGER_VALUE_FALSE;
			}
			Calendar currentDate = Calendar.getInstance();
			currentDate.set(Calendar.DAY_OF_MONTH, 1);
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);
			
			int curMonth = currentDate.get(Calendar.MONTH);
			int curYear = currentDate.get(Calendar.YEAR);
			for(int i=2; i>=0; i--) {
				currentDate.set(Calendar.YEAR, curYear);
				currentDate.set(Calendar.MONTH, curMonth);
				currentDate.add(Calendar.MONTH, -i+1);
				params.put("endtime", currentDate.getTimeInMillis()/1000);
				
				currentDate.set(Calendar.YEAR, curYear);
				currentDate.set(Calendar.MONTH, curMonth);
				currentDate.add(Calendar.MONTH, -i);
				params.put("starttime", currentDate.getTimeInMillis()/1000);
				
				CArray<Map> eventsCountMap = DBselect(executor, EVENT_SQL, params);
				
				long vValue = Nest.value(eventsCountMap, 0, "v").asLong();
				Date date = currentDate.getTime();
				reuslt.put(sdf.format(date), vValue);
			}
		}else {
			String EVENT_SQL = 
					"select  " + 
					"  count(eid) v, " + 
					"  time_ t " + 
					"from " + 
					"  (select  " + 
					"    e.eventid eid, " + 
					"    floor((e.clock+28800) / #{timeBucket}) time_  " + 
					"  from " + 
					"    events e  " + 
					"  where e.source = 0 and e.object = 0 and e.clock>#{starttime} and e.tenantid=#{tenantid} "+
					"  group by eid, time_"+ 
					") t  " + 
					"  group by time_"+ 
					" order by time_ asc";
			if(isHistory) {
				EVENT_SQL = 
					"select  " + 
					"  count(eid) v, " + 
					"  time_ t " + 
					"from " +
					"	(select  " + 
					"		x.eventid eid, " + 
					"		floor((x.clock+28800) / #{timeBucket}) time_ " + 
					"	from ( " + 
					"		  select * from events e  " + 
					"		  where e.source = 0 and e.object = 0 and e.clock>#{starttime} and e.tenantid=#{tenantid} " + 
					"		  order by e.clock desc " + 
					"	)x  " + 
					"	where x.value = " + Defines.TRIGGER_VALUE_FALSE + 
					"	group by eid, time_  " + 
					"	order by x.clock desc " + 
					") t  " + 
					"  group by time_"+ 
					" order by time_ asc";
			}
			CArray<Map> eventsCountMap = DBselect(executor, EVENT_SQL, params);
			Long _value = 0l;
			Long _time = 0l;
			long prev = (startTime+28800)/timeBucket-1;
			
			Calendar currentDate = Calendar.getInstance();
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);
			currentDate.add(Calendar.DAY_OF_MONTH, 1);
			Long endTime = (currentDate.getTimeInMillis()/1000+28800)/timeBucket;
			if("Week".equals(period)) {
				endTime--;
			}
			
			Date date = new Date();
			eventsCountMap.push(CArray.map(//将最后一个时间加到最后，进行补齐
				"t", endTime,
				"v", 0
			));
			for(Map hostdata: eventsCountMap){
				_time = Nest.value(hostdata, "t").asLong();
				_value = Nest.value(hostdata, "v").asLong();
				
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
					reuslt.put(sdf.format(date), vValue);
				}
				prev = _time;
			}
		}
		
		return reuslt;
	}
	
	
	public static Long getCalendar(String period){
		Calendar currentDate = Calendar.getInstance();  
		if(period.equals("Day")){
			currentDate.set(Calendar.HOUR_OF_DAY, 0);  
			currentDate.set(Calendar.MINUTE, 0);  
			currentDate.set(Calendar.SECOND, 0);  
		}else if(period.equals("Week")){
			currentDate.add(Calendar.DATE, -7);
		}else if(period.equals("Month")){
			currentDate.add(Calendar.MONTH, -1);
		}else if(period.equals("Quarter")){
			currentDate.add(Calendar.MONTH, -3);
		}
		return currentDate.getTimeInMillis()/1000;
	}
	
	
	/**
	 * 返回告警级别Map
	 * @return
	 */
	public static Map returnPriority(SQLExecutor executor,IIdentityBean idBean){
		Map config = select_config(idBean, executor);
		Map map = new HashMap();
		map.put(0,  _(Nest.value(config,"severity_name_0").asString()));
		map.put(1,  _(Nest.value(config,"severity_name_1").asString()));
		map.put(2,  _(Nest.value(config,"severity_name_2").asString()));
		map.put(3,  _(Nest.value(config,"severity_name_3").asString()));
		map.put(4,  _(Nest.value(config,"severity_name_4").asString()));
		map.put(5,  _(Nest.value(config,"severity_name_5").asString()));
		return map;
	}
	
	
	/**
	 * 获取设备类型
	 * @param executor
	 * @return
	 */
	private CArray<Map> getHostGroupIds(SQLExecutor executor){
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setMonitoredHosts(true);
		hgoptions.setOutput(new String[]{"groupid", "name"});
		CArray<Map> _Ngroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		CArray<Map> _groups=new CArray<Map>();
		for(Map _group: _Ngroups){//隐藏设备类型为Discovered hosts
			if(!"Discovered hosts".equals(_group.get("name"))){	
				_groups.put(_group.get("groupid"), _group);
			}
		}
		return _groups;
	}
}
