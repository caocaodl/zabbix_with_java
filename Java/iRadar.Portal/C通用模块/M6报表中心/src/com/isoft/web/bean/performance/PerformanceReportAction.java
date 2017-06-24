package com.isoft.web.bean.performance;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.common.util.DataUtil;
import com.isoft.common.util.ReportUtil;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.CTSeverity;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class PerformanceReportAction extends RadarBaseAction {
	
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
            String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", _("Performance_report")+time+".csv");
		}else if(isset(_REQUEST, "reporttype")){ 
			page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
		} else {
			page("file", "performance_report.action");
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("js", new String[] {"imon/report/report_new_performance.js","imon/report/echarts-all.js"});	//引入改变发现规则状态所需JS
			page("css", new String[] { "tenant/edit.css", "lessor/reportcenter/report_performance.css" });
		}
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		CArray data=map();
		if(isset(_REQUEST, "reporttype")){
			if("0".equals(Nest.value(_REQUEST, "type").$())){//top25
				String period = Nest.value(_REQUEST, "topnperiod").asString();
				int groupid = Nest.value(_REQUEST, "groupid").asInteger();
				String indicators = Nest.value(_REQUEST, "indicators").asString();
				data= getTopNData(executor,period,groupid,IMonConsts.REPOT_SHOWNUM_10,indicators);
			}else if("2".equals(Nest.value(_REQUEST, "type").$())){//设备展示
				String period = Nest.value(_REQUEST, "topnperiod").asString();
				int groupid = Nest.value(_REQUEST, "groupid").asInteger();
				data = getHostData(executor,period,groupid);
			}else{
				String period = Nest.value(_REQUEST, "trendperiod").asString();
				int groupid = Nest.value(_REQUEST, "groupid").asInteger();
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
		if (CSV_EXPORT) {
			int groupid = Nest.value(_REQUEST, "groupid").asInteger();
			int period = Nest.value(_REQUEST, "topnperiod").asInteger();
			String[] indicatorsarr = null;
			String[] titlenames = null;
			String[] unit=null;
			if(groupid == 1 || groupid == 2){
				indicatorsarr=  new String[]{"cpuused","memory"};
				titlenames=  new String[]{_("cpu avg rate")+_("TOP5"),_("memory avg rate")+_("TOP5")};
				unit=new String[]{"%","%"};
			}else {
				indicatorsarr=  new String[]{"netuppacket", "netdownpacket", "netuprate", "netupdown"};
				titlenames=  new String[]{_("netuppacket")+_("TOP5"),_("netdownpacket")+_("TOP5"),_("netuprate")+_("TOP5"),_("netupdown")+_("TOP5")};
				unit=new String[]{"%","%","Bps","Bps"};
			}
			for(int i=0;i<indicatorsarr.length;i++){
				CArray cpucarray = getTopNData(executor,String.valueOf(period),groupid,IMonConsts.REPOT_SHOWNUM_10,indicatorsarr[i]);
			    CArray cpus=Nest.value(cpucarray, "left0").asCArray();
			    csvRows.add(array(titlenames[i]));
			    csvRows.add(array(_("equipmentname"),_("AVG VALUE"),_("MAX VALUE")));
			    for(Map cpu:(CArray<Map>) cpus){
			    	csvRows.add(array(Nest.value(cpu, "name").asString(),Nest.value(cpu, "value").asString()+unit[i],Nest.value(cpu, "maxvalue").asString()+unit[i]));
			    }
			    csvRows.add(array(""));
			}
			
		}
		if (CSV_EXPORT) {
			int groupid = Nest.value(_REQUEST, "groupid").asInteger();
			String period = Nest.value(_REQUEST, "topnperiod").asString();
			int time = ReportUtil.getReportTime(period);
			String statisticalType = null;
			if(groupid == 1){
				statisticalType="server";
				csvRows.add(array(_("equipmentname"),_("IP address"),_("CPU_RATE"),_("CPU_Max_RATE"),_("memoryRate"),_("memoryMaxRate"),_("disk_Rate"),_("IO_Read_Rate"),_("IO_Write_Rate"),_("Net_Interface_Upside_IO"),_("Net_Interface_Downside_IO")));
			}else if(groupid == 3){
				statisticalType="switches";
				csvRows.add(array(_("equipmentname"),_("IP address"),_("net down error"),_("net up error"),_("the net down"),_("the net up")));
			}else{
				statisticalType="vmhost";
				csvRows.add(array(_("equipmentname"),_("IP address"),_("CPU_RATE"),_("CPU_Max_RATE"),_("memoryRate"),_("memoryMaxRate"),_("disk_Rate"),_("IO_Read_Rate"),_("IO_Write_Rate"),_("Net_Interface_Upside_IO"),_("Net_Interface_Downside_IO")));
			}
			  
			List<Map> hostdatas = DataUtil.getHostItemValueData(executor, getIdBean(), statisticalType, time,CSV_EXPORT);
		    for(Map hosts : hostdatas){
		    	CArray hostd=array();
		    	Iterator it=hosts.keySet().iterator();
		    	while(it.hasNext()){
		    	    String key;
		    	    Object value;
		    	    key=it.next().toString();
		    	    value = hosts.get(key);
		    	    hostd.add("\r"+value.toString());
		    	} 
		       csvRows.add(hostd);
		    }
			
			
		}
		
		if (CSV_EXPORT) {
			print(rda_toCSV(csvRows));
			return;
		}
		
		CWidget widget = new CWidget();
		
		CArray headers = new CArray();
		CPageFilter comboxFactory = new CPageFilter(getIdBean(), executor);
		
		//设备类型
		CDiv ctn = new CDiv(null, "select_ctn group");
		ctn.addItem(new CSpan(_("Monitor category")+SPACE+":"+SPACE));
		CComboBox cb = new CComboBox("group");
		cb.addItem("1",_("SERVER"));
		cb.addItem("2",_("Cloud host"));
		cb.addItem("3",_("Switches"));
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
		), array( _("Day"), _("Week"), _("Month")), false));
		headers.add(ctn);
		
		//设备
		ctn = new CDiv(null, "select_ctn host hidden");
		ctn.addItem(new CSpan(_("host")+SPACE+":"+SPACE));
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
	
	@Override
	protected void doCheckFields(SQLExecutor executor) {
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	private CDiv getBody(){
		CDiv bodydiv = new CDiv(null,"report_body");
		//展示部分
		CDiv splitDiv = new CDiv();
		splitDiv.addStyle("height:10px;");
		
		CDiv titlediv = new CDiv(null,"titletop","titlediv");
		bodydiv.addItem(titlediv);
		
		CDiv firstdiv = new CDiv(null,"top");
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
		
		
		CTableInfo ctable=new CTableInfo();
		ctable.attr("id", "cdata");
		
		CDiv hostdiv = new CDiv(ctable,"hostname");
		bodydiv.addItem(hostdiv);
		return bodydiv;
	}
	
	/**
	 * @param executor
	 * @param period
	 * @param groupid  设备类型
	 * @param showNum  显示数目
	 * @return
	 */
	private CArray getTopNData(SQLExecutor executor,String period,int groupid,int showNum,String indicators){
		CArray data=map();
		List<Map> left0 = new ArrayList<Map>();
		int time = ReportUtil.getReportTime(period);
		if(groupid  ==1){//服务器
				if("cpuused".equals(indicators)){//cpu使用率
					left0 = CommonUtils.getTOPNRateByCPU(executor, getIdentityBean(), 0L, time, null, showNum, EasyObject.asBoolean(1));
					data.put("area", "0");
				}else if("memory".equals(indicators)){
					left0 = CommonUtils.getTOPNRateByMemory(executor, getIdentityBean(), 0L, time, null, showNum, EasyObject.asBoolean(1));
					data.put("area", "1");
				}
		} else if(groupid == 2){//云主机
				if("cpuused".equals(indicators)){//cpu使用率
					left0 = CommonUtils.getTOPNRateByCPU(executor, getIdentityBean(), IMonGroup.MON_VM.id(), time, null, showNum, EasyObject.asBoolean(1));
					data.put("area", "0");
				}else if("memory".equals(indicators)){
					left0 = CommonUtils.getTOPNRateByMemory(executor, getIdentityBean(), IMonGroup.MON_VM.id(), time, null, showNum, EasyObject.asBoolean(1));
					data.put("area", "1");
				}
		}else if(groupid == 3){//交换机
			if("netuppacket".equals(indicators)){//cpu使用率
				left0 = DataUtil.getTOPNRate(executor, getIdentityBean(), "switches", time, "netuppacket", showNum, EasyObject.asBoolean(1));
				data.put("area", "0");
			}else if("netdownpacket".equals(indicators)){
				left0 = DataUtil.getTOPNRate(executor, getIdentityBean(), "switches", time, "netdownpacket", showNum, EasyObject.asBoolean(1));
				data.put("area", "1");
			}else if("netuprate".equals(indicators)){
				left0 = DataUtil.getTOPNRate(executor, getIdentityBean(), "switches", time, "netuprate", showNum, EasyObject.asBoolean(1));
				data.put("area", "2");
			}else if("netupdown".equals(indicators)){
				left0 = DataUtil.getTOPNRate(executor, getIdentityBean(), "switches", time, "netupdown", showNum, EasyObject.asBoolean(1));
				data.put("area", "3");
			}
		}
		data.put("left0", left0);
		return data;
	}
	
	private CArray getHostData(SQLExecutor executor,String period,int groupid){
		CArray data=map();
		String statisticalType = null;
		if(groupid == 1){
			statisticalType="server";
		}else if(groupid == 2){
			statisticalType="vmhost";
		}else if(groupid == 3){
			statisticalType="switches";
		}
		int time = ReportUtil.getReportTime(period);
		
		List<Map> hostdata = DataUtil.getHostItemValueData(executor, getIdBean(), statisticalType, time,CSV_EXPORT);
		data.put("hostdata",hostdata);
		return data;
	}

	
	private CArray getTrendData(SQLExecutor executor,String period,int groupid,String hostid){
		CArray data=map();
		if(hostid == null){
			List<Map> hostdata=new ArrayList<Map>();
			if(groupid  == 1){//服务器
				Long[] groupids = new Long[]{IMonGroup.MON_SERVER_LINUX.id(),IMonGroup.MON_SERVER_WINDOWS.id()};
				String[] keys = new String[]{ItemsKey.CPU_USER_RATE.getValue(),ItemsKey.CPU_USER_RATE_WINDOWS.getValue()};//内存使用率
				hostdata = CommonUtils.queryHostIDAndNameTwo(keys,groupids);
			}else if(groupid  == 2){//虚拟机
				hostdata = CommonUtils.queryHostIDAndName(IMonGroup.MON_VM.id());
			}else if(groupid  == 3){//交换机
				Long[] groupids = new Long[]{IMonGroup.MON_NET_CISCO.id(),IMonGroup.MON_COMMON_NET.id(),IMonGroup.MON_NET_HUAWEI_SWITCH.id()};
				String[] keys = new String[]{ItemsKey.COMMON_NET_IFINOCTETS.getValue(),ItemsKey.NET_UP_SPEED_NET_CISCO.getValue(),ItemsKey.COMMON_NET_IFINOCTETS.getValue()};//下行速率
				hostdata = CommonUtils.queryHostIDAndNameTwo(keys,groupids);
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
			String area1src = null;
			String area2src  = null;
			String area3src = null;
			String area4src  = null;
			if(!"-1".equals(hostid)){
				int time = ReportUtil.getReportTime(String.valueOf(Integer.valueOf(period)+1));
				try {
					if(groupid  == 1){//服务器
						if(hostid.contains("-")){
							String[] hostids=hostid.split("-");
							if((101L)==Long.valueOf(hostids[1].trim())){
								area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[0],ItemsKey.CPU_USER_RATE.getValue(),time);
								area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.MEMORY_USELV_KEY,time);
								area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.DISK_READ_SPEED,time);
								area4src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.DISK_WRITE_SPEED,time);
							}else{
								area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[0],ItemsKey.CPU_USER_RATE_WINDOWS.getValue(),time);
								area2src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(),executor, hostids[0],ItemsKey.MEMORY_USELV_KEY_WINDOWS.getValue(),time);
								area3src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor,hostids[0],ItemsKey.DISK_READ_REQUEST_BW_WINDOWS.getValue(),time);
								area4src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor,hostids[0],ItemsKey.DISK_WRITE_REQUEST_BW_WINDOWS.getValue(),time);
							}
						}
					}else if(groupid  == 2){//虚拟机
						String osType_vm = CommonUtils.getTargetLastValue( executor, getIdentityBean(), array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid, false, false);//操作系统类型
						if(osType_vm != null && osType_vm.contains("Windows")){
							area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.CPU_RATE_VM_WINDOWS.getValue(),time);	
							area3src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.DISK_READ_SPEED_VM_WINDOWS.getValue(),time);	
							area4src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.DISK_WRITE_SPEED_VM_WINDOWS.getValue(),time);	
						}else{
							area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.CPU_RATE_VM_LINUX.getValue(),time);
							area3src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.DISK_READ_SPEED_VM_LINUX.getValue(),time);	
							area4src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.DISK_WRITE_SPEED_VM_LINUX.getValue(),time);	
						}
						area2src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.MEMORY_RATE_VM.getValue(),time);
					}else if(groupid  == 3){//交换机
						if(hostid.contains("-")){
							String[] hostids=hostid.split("-");
							if((302L)==Long.valueOf(hostids[1].trim())){
								area1src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFINERRORS,time);
								area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFOUTERRORS,time);
								area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFINOCTETS,time);
								area4src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFOUTOCTETS,time);
							}else if((303L)==Long.valueOf(hostids[1].trim())){
								area1src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFINERRORS,time);
								area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFOUTERRORS,time);
								area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFINOCTETS,time);
								area4src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFOUTOCTETS,time);
							}else{
								area1src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.CISCO_NET_IFINERRORS,time);
								area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.CISCO_NET_IFOUTERRORS,time);
								area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_UP_SPEED_NET_CISCO,time);
								area4src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_DOWN_SPEED_NET_CISCO,time);
							}
						}
					}
				} catch (NumberFormatException e) {
					area1src = null;
					area2src  = null;
					area3src = null;
					area4src  = null;
					e.printStackTrace();
				}
			}
			data.put("area1src", area1src);
            data.put("area2src", area2src);
            data.put("area3src", area3src);
            data.put("area4src", area4src);
            
		}
		return data;
	}
}
