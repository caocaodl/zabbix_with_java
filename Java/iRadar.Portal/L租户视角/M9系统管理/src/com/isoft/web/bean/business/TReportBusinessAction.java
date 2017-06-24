package com.isoft.web.bean.business;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.CTSeverity;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.action.TeventUtil;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TReportBusinessAction extends RadarBaseAction {
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
			
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", _("Business_export")+time+".csv");
		}else if(isset(_REQUEST, "reporttype")){ 
			page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
		} else {
			page("title","业务报表");
			page("file", "tbusiness_report.action");
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("js", new String[] {"imon/report/treport_business.js","imon/report/echarts-all.js"});	//引入改变发现规则状态所需JS
			page("css", new String[] { "tenant/edit.css", "lessor/reportcenter/report_performance.css" });
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		CArray data=map();
		if(isset(_REQUEST, "reporttype")){
			if("0".equals(Nest.value(_REQUEST, "type").$())){//top25
				String period = Nest.value(_REQUEST, "topnperiod").asString();
				int groupid = Nest.value(_REQUEST, "groupid").asInteger();
				 data= getTopNData(executor,period,groupid,IMonConsts.REPOT_SHOWNUM_10);
			}else{
				String period = Nest.value(_REQUEST, "trendperiod").asString();
				int groupid = Nest.value(_REQUEST, "groupid").asInteger();
				//int hostid = Nest.value(_REQUEST, "hostid").asInteger();
				String hostid = Nest.value(_REQUEST, "hostid").asString();
				data = getTrendData(executor,period,groupid,hostid);
			}
			echo(JSONArray.fromObject(data).toString());
			return true;
		}
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		CArray data=map();
		if (CSV_EXPORT) {
			List<Map> left0 = new ArrayList<Map>();
			List<Map> fleft0 = new ArrayList<Map>();
			
			List<Map> left1 = new ArrayList<Map>();
			List<Map> fleft1 = new ArrayList<Map>();
			
			List<Map> left2 = new ArrayList<Map>();
			List<Map> fleft2 = new ArrayList<Map>();

			String period = Nest.value(_REQUEST, "topnperiod").asString();
			String titlestr="";
			String titlestr2="";
			int groupid = Nest.value(_REQUEST, "groupid").asInteger();
			int time = ReportUtil.getReportTime(period);
			
			
			if(groupid  ==1){//数据库
				left0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"2", "mysql.threads_connected",time,IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(1));
				left1 = ReportUtil.getEventNumByApp(getIdentityBean(), executor, "2", time);
				
				fleft0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"2", "mysql.threads_connected",time,IMonConsts.REPOT_SHOWNUM_10 , EasyObject.asBoolean(1));
				titlestr=_("Connections_number");
			}else if(groupid == 2){//中间件
				left0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.currentThreadCount",time,IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(1));
				left1 = ReportUtil.getEventNumByApp(getIdentityBean(), executor, "3", time);
				left2 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.errorCount",time,IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(1));
				
				
				fleft0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.currentThreadCount",time,IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(0));
				fleft2 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.errorCount",time,IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(0));
				titlestr=_("Session_number");
				titlestr2=_("Second_Error_Number");
			}else if(groupid == 3){
				left0 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "responsetime", IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(1));
				left1 = ReportUtil.getEventNumByWeb(getIdentityBean(), executor, time);
				left2 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "avbRate", IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(1));
				
				fleft0 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "responsetime", IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(0));
				fleft2 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "avbRate", IMonConsts.REPOT_SHOWNUM_10, EasyObject.asBoolean(0));
				titlestr=_("Response time");
				titlestr2 = _("Availability");
			}
			csvRows.add(array(titlestr));
			csvRows.add(array(_("equipmentname"),_("Front")+_("TOPN"),"",_("equipmentname"),_("After")+_("TOPN")));
			int i=0;
			for(Map connectednum : left0){
				Map connecteddesc =  fleft0.get(i);
				csvRows.add(array(Nest.value(connectednum, "name").asString(),Nest.value(connectednum, "value").asString(),"",
						Nest.value(connecteddesc, "name").asString(),Nest.value(connecteddesc, "value").asString()));
			    i++;
			}
			
			csvRows.add(array(""));
			csvRows.add(array(_("Fault_NumBer")));
			csvRows.add(array(_("Fault_Level"),_("Fault_Num")));
			for(Map database : left1){
				csvRows.add(array(Nest.value(database, "priority").asString(),Nest.value(database, "eventsnum").asString()));
			}
			
			if(!empty(titlestr2)){
				csvRows.add(array(""));
				csvRows.add(array(titlestr2));
				csvRows.add(array(_("equipmentname"),_("Front")+_("TOPN"),"",_("equipmentname"),_("After")+_("TOPN")));
				int j=0;
				for(Map connectednum : left2){
					Map connecteddesc =  fleft2.get(j);
					csvRows.add(array(Nest.value(connectednum, "name").asString(),Nest.value(connectednum, "value").asString(),"",
							Nest.value(connecteddesc, "name").asString(),Nest.value(connecteddesc, "value").asString()));
				    j++;
				}
			}
			
			
		}
		
		if (CSV_EXPORT) {
			print(rda_toCSV(csvRows));
			return;
		}
		
		CWidget widget = new CWidget();
		
		CArray headers = new CArray();
		CPageFilter comboxFactory = new CPageFilter(getIdBean(), executor);
		
		CArray<Map> groupmap = map();
		CHostGroupGet hgoptions = new CHostGroupGet();
		hgoptions.setOutput(new String[]{"groupid","name"});
		hgoptions.setEditable(true);
		CArray<Map> groups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);
		for(Map group:groups){
			groupmap.put( Nest.value(group, "groupid").asLong(), Nest.value(group, "name").asString());
		}
		
		//设备类型
		CDiv ctn = new CDiv(null, "select_ctn group");
		ctn.addItem(new CSpan(_("Monitor category")+SPACE+":"+SPACE));
		CComboBox cb = new CComboBox("group");
		cb.addItem("1",_("MON_CATE_DB"));
		cb.addItem("2",_("MON_CATE_MIDWARE"));
		cb.addItem("3",_("Web Monitoring"));
		cb.setAttribute("onchange", "");
		ctn.addItem(cb);
		headers.add(ctn);
		
		
		//报表类型
		ctn = new CDiv(null, "select_ctn type");
		ctn.addItem(new CSpan(_("Report Type")+SPACE+":"+SPACE));
		ctn.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "type",
			"name", "type",
			"value",  "0"
		), array(_("TopN"), _("Trends")), false));
		headers.add(ctn);
		
		
		//TOPN周期
		ctn = new CDiv(null, "select_ctn topn_period");
		ctn.addItem(new CSpan(_("Cycle")+SPACE+":"+SPACE));
		ctn.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "topnPeriod",
			"name", "topnPeriod",
			"value", "0"
		), array(_("Current"), _("Day"), _("Week"), _("Month")), false));
		headers.add(ctn);
		
		//导出按钮
		CButton csvexport = new CButton("csv_export", _("Export to CSV"),"","orange export");
		ctn = new CDiv(csvexport, "csv_exportid");
		headers.add(ctn);
		
		//趋势周期
		ctn = new CDiv(null, "select_ctn trend_period hidden");
		ctn.addItem(new CSpan(_("Cycle")+SPACE+":"+SPACE));
		ctn.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "trendPeriod",
			"name", "trendPeriod",
			"value", "-1"
		), array(_("Day"), _("Week"), _("Month")), false));
		headers.add(ctn);
		
		//设备
		ctn = new CDiv(null, "select_ctn host hidden");
		ctn.addItem(new CSpan("应用名"+SPACE+":"+SPACE));
		cb = comboxFactory.getCB("selectHost", "0", map());
		cb.setAttribute("onchange", "");
		ctn.addItem(cb);
		headers.add(ctn);
		
		
		
		CForm f = new CForm();
		f.addItem(headers);
		widget.addHeader(f);
		widget.addItem(getBody());
		widget.show();

	}
	
	private CDiv getBody(){
		CDiv bodydiv = new CDiv(null,"report_body");
		//展示部分
		CDiv splitDiv = new CDiv();
		splitDiv.addStyle("height:10px;");
		
		CDiv firstdiv = new CDiv(null,"top");
		firstdiv.addStyle("min-width:1230px;");
		CDiv a1 = new CDiv(null,"topleft","a1");
		CDiv a2 = new CDiv(null,"topright","a2");
		
		CDiv area1 = new CDiv(null,"affix","area1");
		CDiv area2 = new CDiv(null,"affix","area2");
		CDiv area1title = new CDiv(null,"areatitle","area1_title");
		CDiv area2title = new CDiv(null,"areatitle","area2_title");
		a1.addItem(area1title);
		a1.addItem(area1);
		a2.addItem(area2title);
		a2.addItem(area2);
		
		
		firstdiv.addItem(a1);
		firstdiv.addItem(a2);
		bodydiv.addItem(firstdiv);
		
		CDiv seconddiv = new CDiv(null,"middle");
		seconddiv.addStyle("min-width:1230px;");
		CDiv a3 = new CDiv(null,"middleleft","a3");
		CDiv a4 = new CDiv(null,"middleright","a4");
		
		CDiv area3 = new CDiv(null,"affix","area3");
		CDiv area4 = new CDiv(null,"affix","area4");
		CDiv area4title = new CDiv(null,"areatitle","area4_title");
		CDiv area3title = new CDiv(null,"areatitle","area3_title");
		a3.addItem(area3title);
		a3.addItem(area3);
		a4.addItem(area4title);
		a4.addItem(area4);
		
		seconddiv.addItem(a3);
		seconddiv.addItem(a4);
		bodydiv.addItem(seconddiv);
		
		return bodydiv;
	}
	
	/**
	 * @param executor
	 * @param period
	 * @param groupid  设备类型
	 * @param showNum  显示数目
	 * @return
	 */
	private CArray getTopNData(SQLExecutor executor,String period,int groupid,int showNum){
		CArray data=map();
		List<Map> left0 = new ArrayList<Map>();
		List<Map> fleft0 = new ArrayList<Map>();
		
		List<Map> left1 = new ArrayList<Map>();
		List<Map> fleft1 = new ArrayList<Map>();
		
		List<Map> left2 = new ArrayList<Map>();
		List<Map> fleft2 = new ArrayList<Map>();
		
		int time = ReportUtil.getReportTime(period);
				
		if(groupid  ==1){//数据库
			left0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"2", "mysql.threads_connected",time,showNum, EasyObject.asBoolean(1));
			left1 = ReportUtil.getEventNumByApp(getIdentityBean(), executor, "2", time);
			
			fleft0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"2", "mysql.threads_connected",time,showNum, EasyObject.asBoolean(0));
			
			data.put("left0", left0);
			data.put("fleft0", fleft0);
			data.put("left1", left1);
		} else if(groupid == 2){//中间件
			left0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.currentThreadCount",time,showNum, EasyObject.asBoolean(1));
			left1 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.errorCount",time,showNum, EasyObject.asBoolean(1));
			left2 = ReportUtil.getEventNumByApp(getIdentityBean(), executor, "3", time);
			
			fleft0 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.currentThreadCount",time,showNum, EasyObject.asBoolean(0));
			fleft1 = ReportUtil.TopNByApp(executor, getIdentityBean(),"3", "tomcat.errorCount",time,showNum, EasyObject.asBoolean(0));
			
			data.put("left0", left0);
			data.put("fleft0", fleft0);
			data.put("left1", left1);
			data.put("fleft1", fleft1);
			data.put("left2", left2);
		}else if(groupid == 3){//Web服务  
			left0 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "responsetime", showNum, EasyObject.asBoolean(1));
			left1 = ReportUtil.getEventNumByWeb(getIdentityBean(), executor, time);
			left2 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "avbRate", showNum, EasyObject.asBoolean(1));
			
			fleft0 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "responsetime", showNum, EasyObject.asBoolean(0));
			fleft2 = ReportUtil.getTopNRateByWeb(executor, getIdentityBean(), time, "avbRate", showNum, EasyObject.asBoolean(0));
			data.put("left0", left0);
			data.put("fleft0", fleft0);
			data.put("left1", left1);
			data.put("left2", left2);
			data.put("fleft2", fleft2);
		}
		
		int datanum = data.size();
		data.put("datanum", String.valueOf(datanum));
		return data;
	}

	
	private CArray getTrendData(SQLExecutor executor,String period,int groupid,String hostid){
		
		CArray data=map();
		List<Map> left0 = new ArrayList<Map>();
		if(hostid == null){
			List<Map> hostdata=new ArrayList<Map>();
			if(groupid  == 1){//数据库  
				hostdata = ReportUtil.gethostByApps(executor,getIdentityBean(),"2");
			}else if(groupid  == 2){//中间件
				hostdata = ReportUtil.gethostByApps(executor,getIdentityBean(),"3");
			}else if(groupid  == 3){//Web服务
				CArray<Map> https = CommonUtils.queryWebidAndName();
			    for(Map http:https){
			    	Map hostdt =new HashMap();
				    hostdt.put("name", Nest.value(http, "name").asString());
				    hostdt.put("hostid", Nest.value(http, "httptestid").asString());
				    hostdt.put("groupid",  Nest.value(http, "hostid").asLong());
				    hostdata.add(hostdt);
			    }
			}	
			data.put("hostdata", hostdata);
		
			if(!empty(hostdata)){
				Map host=(Map)hostdata.get(0);
				String hoststr= Nest.value(host, "hostid").asString();
				data.put("hostid", hoststr);
			}else{
				data.put("hostid", "-1");
			}
		}else{
			String hostidstr =String.valueOf(hostid);
			
			String area1src = null;
			String area2src  = null;
			String area3src = null;
		   if(!"-1".equals(hostidstr)){
				int time= ReportUtil.getReportTime(String.valueOf(Integer.valueOf(period)+1));
				
				if(groupid  == 1){//数据库
					String[] hostids=hostidstr.split(",");
					 CItemGet itemGet = new CItemGet();
					 itemGet.setApplicationIds(Long.valueOf(hostids[0]));
					 itemGet.setOutput(new String[] {"itemid", "name", "state","key_"});
					 itemGet.setEditable(true);
					 CArray<Map> items = API.Item(getIdentityBean(), executor).get(itemGet);
					 String sessionnum = null;
					 for(Map item : items){
						 String key = Nest.value(item, "key_").asString();
						 if("mysql.threads_connected".equals(key.substring(0, key.indexOf("[")))){
							 sessionnum=key;
							 break;
						  }
					 }
					area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[1],sessionnum,time);
					left0 = TeventUtil.getTrendByApp(getIdentityBean(), executor, time, hostids[0], period);

					data.put("area1src", area1src); 
				    data.put("left0", left0); 
				}else if(groupid  == 2){//中间件
					 String[] hostids=hostidstr.split(",");
					 CItemGet itemGet = new CItemGet();
					 itemGet.setApplicationIds(Long.valueOf(hostids[0]));
					 itemGet.setOutput(new String[] {"itemid", "name", "state","key_"});
					 itemGet.setEditable(true);
					 CArray<Map> items = API.Item(getIdentityBean(), executor).get(itemGet);
					 String sessionnum = null;
					 String errorcount=null;
					 for(Map item : items){
						 String key = Nest.value(item, "key_").asString();
						 if("tomcat.currentThreadCount".equals(key.substring(0, key.indexOf("[")))){
							 sessionnum=key;
						  }
						 if("tomcat.errorCount".equals(key.substring(0, key.indexOf("[")))){
							 errorcount=key;
						  }
					 }
					area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[1],sessionnum,time);	
					area2src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[1],errorcount,time);

					left0 = TeventUtil.getTrendByApp(getIdentityBean(), executor, time, hostids[0], period);
					 
					data.put("area1src", area1src);
		            data.put("area2src", area2src);  
		            data.put("left0", left0);  
				}else if(groupid  == 3){//Web服务
					Long lhostid = Long.parseLong(hostidstr);
					
					CHttpTestGet options = new CHttpTestGet();
					options.setOutput(new String[]{"httptestid","name","hostid"});
					options.setHttptestIds(lhostid);
					options.setSelectSteps(new String[]{"name"});
					options.setTemplated(false);
					options.setEditable(true);
					Map http=reset((CArray<Map>)API.HttpTest(getIdentityBean(), executor).get(options));
					String hostids = Nest.value(http, "hostid").asString();
					String httpName = Nest.value(http, "name").asString();
					String stepName = Nest.value(http, "steps","0","name").asString();
					String responsetime ="web.test.time["+httpName+","+stepName+",resp]";//响应时间
					String avbRate = "web.test.fail["+httpName+"]";//可用率
				
					area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids,responsetime,time);
					area2src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids,avbRate,time);
					left0 = TeventUtil.getTrendByApp(getIdentityBean(), executor, time, hostidstr, period);
					data.put("area1src", area1src);
		            data.put("area2src", area2src); 
		            data.put("left0", left0); 
				} 
		   }
			
            int datanum = data.size();
    		data.put("datanum", String.valueOf(datanum));
		}
		return data;
	}

}
