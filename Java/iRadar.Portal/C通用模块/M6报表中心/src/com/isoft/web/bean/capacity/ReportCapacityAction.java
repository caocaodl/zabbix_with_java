package com.isoft.web.bean.capacity;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.CTSeverity;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ReportCapacityAction extends RadarBaseAction {

	@Override
	protected void doInitPage() {
		if(isset(_REQUEST, "reporttype")){ 
			page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
		} else {
			page("title","容量报表");
			page("file", "capacity_report.action");
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("js", new String[] {"imon/report/report_capacity.js","imon/report/echarts-all.js"});	//引入改变发现规则状态所需JS
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
				String indicators = Nest.value(_REQUEST, "indicators").asString();
				//data= getTopNData(executor,period,groupid,IMonConsts.REPOT_SHOWNUM_10);
				data= getTopNData(executor,period,groupid,IMonConsts.REPOT_SHOWNUM_10,indicators);
			}else{
				String period = Nest.value(_REQUEST, "trendperiod").asString();
				int groupid = Nest.value(_REQUEST, "groupid").asInteger();
				String hostid = Nest.value(_REQUEST, "hostid").asString();
				String hostname = Nest.value(_REQUEST, "hostname").asString();
				data = getTrendData(executor,period,groupid,hostid,hostname);
			}
			echo(JSONArray.fromObject(data).toString());
			return true;
		}
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
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
		ctn.addItem(new CSpan("设备类型："));
		CComboBox cb = new CComboBox("group");
		cb.addItem("1","服务器");
		cb.addItem("2","云主机");
		cb.addItem("3","交换机");
		cb.addItem("4","云平台");
		cb.setAttribute("onchange", "");
		ctn.addItem(cb);
		headers.add(ctn);
		
		
		//报表类型
		ctn = new CDiv(null, "select_ctn type");
		ctn.addItem(new CSpan("报表类型："));
		ctn.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "type",
			"name", "type",
			"value",  "0"
		), array(_("TopN"), "趋势"), false));
		headers.add(ctn);
		
		
		//TOPN周期
		ctn = new CDiv(null, "select_ctn topn_period");
		ctn.addItem(new CSpan("周期："));
		ctn.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "topnPeriod",
			"name", "topnPeriod",
			"value", "0"
		), array("当前", "日", "周", "月"), false));
		headers.add(ctn);
		
		
		//趋势周期
		ctn = new CDiv(null, "select_ctn trend_period hidden");
		ctn.addItem(new CSpan("周期："));
		ctn.addItem(new CTSeverity(getIdentityBean(), executor, map(
			"id", "trendPeriod",
			"name", "trendPeriod",
			"value", "-1"
		), array("日", "周", "月"), false));
		headers.add(ctn);
		
		//设备
		ctn = new CDiv(null, "select_ctn host hidden");
		ctn.addItem(new CSpan("设备："));
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
		
		//CRow secnodRow = new CRow();
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
		
		
		CDiv thirddiv = new CDiv(null,"ttir");
		CDiv a5 = new CDiv(null,"ttirleft","a5");
		CDiv a6 = new CDiv(null,"ttirright","a6");
		
		CDiv area5 = new CDiv(null,"affix","area5");
		CDiv area6 = new CDiv(null,"affix","area6");
		CDiv area5title = new CDiv(null,"areatitle","area5_title");
		CDiv area6title = new CDiv(null,"areatitle","area6_title");
		a5.addItem(area5title);
		a5.addItem(area5);
		a6.addItem(area6title);
		a6.addItem(area6);
		
		thirddiv.addItem(a5);
		thirddiv.addItem(a6);
		bodydiv.addItem(thirddiv);
		
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
		int time = 3600;
		if("1".equals(period)){
			time=86400;
		}else if("2".equals(period)){
			time= 604800;
		}else if("3".equals(period)){
			time= 2592000;
		}
		
		List<Map> left0 = new ArrayList<Map>();
		List<Map> fleft0 = new ArrayList<Map>();
		
		List<Map> left1 = new ArrayList<Map>();
		List<Map> fleft1 = new ArrayList<Map>();
		
		List<Map> left2 = new ArrayList<Map>();
		List<Map> fleft2 = new ArrayList<Map>();
		
		List<Map> left3 = new ArrayList<Map>();
		List<Map> fleft3 = new ArrayList<Map>();
		
		List<Map> left4 = new ArrayList<Map>();
		List<Map> fleft4 = new ArrayList<Map>();
		if(groupid  ==1){//服务器
			if("0".equals(period)){
				if("cpuused".equals(indicators)){//cpu使用率
					left0 = CommonUtils.getTOPNRate(executor, getIdentityBean(),"CPU", showNum, EasyObject.asBoolean(1));//正序
					fleft0 = CommonUtils.getTOPNRate(executor, getIdentityBean(),"CPU", showNum, EasyObject.asBoolean(0));//正序
					data.put("area", "0");
				}else if("memory".equals(indicators)){
					left0 = CommonUtils.getTOPNRate(executor, getIdentityBean(),"内存", showNum, EasyObject.asBoolean(1));
					fleft0 = CommonUtils.getTOPNRate(executor, getIdentityBean(),"内存", showNum, EasyObject.asBoolean(0));
					data.put("area", "1");
				}else if("disk".equals(indicators)){
					left0 = CommonUtils.getTOPNRate(executor, getIdentityBean(),"磁盘", showNum, EasyObject.asBoolean(0));
					fleft0 = CommonUtils.getTOPNRate(executor, getIdentityBean(),"磁盘", showNum, EasyObject.asBoolean(0));
					data.put("area", "2");
				}else if("upipos".equals(indicators)){
					left0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "up", showNum, EasyObject.asBoolean(1));
					fleft0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "up", showNum, EasyObject.asBoolean(0));
					data.put("area", "3");
				}else if("downipos".equals(indicators)){
					left0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "down", showNum, EasyObject.asBoolean(1));
					fleft0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "down", showNum, EasyObject.asBoolean(0));
					data.put("area", "4");
				}
			}else{
				if("cpuused".equals(indicators)){//cpu使用率
					left0 = CommonUtils.getTOPNRateByCPU(executor, getIdentityBean(), 0L, time, null, showNum, EasyObject.asBoolean(1));
					fleft0 = CommonUtils.getTOPNRateByCPU(executor, getIdentityBean(), 0L, time, null, showNum, EasyObject.asBoolean(0));
					data.put("area", "0");
				}else if("memory".equals(indicators)){
					left0 = CommonUtils.getTOPNRateByMemory(executor, getIdentityBean(), 0L, time, null, showNum, EasyObject.asBoolean(1));
					fleft0 = CommonUtils.getTOPNRateByMemory(executor, getIdentityBean(), 0L, time, null, showNum, EasyObject.asBoolean(0));
					data.put("area", "1");
				}else if("disk".equals(indicators)){
					left0 = CommonUtils.getTOPNRateByDisk(executor, getIdentityBean(), 0L , time, null, showNum, EasyObject.asBoolean(1));
					fleft0 = CommonUtils.getTOPNRateByDisk(executor, getIdentityBean(), 0L , time, null, showNum, EasyObject.asBoolean(0));
					data.put("area", "2");
				}else if("upipos".equals(indicators)){
					left0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "up", showNum, EasyObject.asBoolean(1));
					fleft0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "up", showNum, EasyObject.asBoolean(0));
					data.put("area", "3");
				}else if("downipos".equals(indicators)){
					left0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "down", showNum, EasyObject.asBoolean(1));
					fleft0 = ReportUtil.getTOPNRateByUporDownIPOS(executor, getIdentityBean(), 0L , time, "down", showNum, EasyObject.asBoolean(0));
					data.put("area", "4");
				}
			}
		} else if(groupid == 2){//虚拟机
			if("cpuused".equals(indicators)){//cpu使用率
				left0 = CommonUtils.getTOPNRateByCPU(executor, getIdentityBean(), IMonGroup.MON_VM.id(), time, null, showNum, EasyObject.asBoolean(1));
				fleft0 = CommonUtils.getTOPNRateByCPU(executor, getIdentityBean(), IMonGroup.MON_VM.id(), time, null, showNum, EasyObject.asBoolean(0));
				data.put("area", "0");
			}else if("memory".equals(indicators)){
				left0 = CommonUtils.getTOPNRateByMemory(executor, getIdentityBean(), IMonGroup.MON_VM.id(), time, null, showNum, EasyObject.asBoolean(1));
				fleft0 = CommonUtils.getTOPNRateByMemory(executor, getIdentityBean(), IMonGroup.MON_VM.id(), time, null, showNum, EasyObject.asBoolean(0));
				data.put("area", "1");
			}else if("disk".equals(indicators)){
				left0 = CommonUtils.getTOPNRateByDisk(executor, getIdentityBean(), IMonGroup.MON_VM.id() , time, null, showNum, EasyObject.asBoolean(1));
				fleft0 = CommonUtils.getTOPNRateByDisk(executor, getIdentityBean(), IMonGroup.MON_VM.id() , time, null, showNum, EasyObject.asBoolean(0));
				data.put("area", "2");
			}
		}else if(groupid == 3){//交换机
			if("up".equals(indicators)){//cpu使用率
				left0 = ReportUtil.getTOPNRateByUporDownNet(executor, getIdentityBean(), "switches", time, "up", showNum, EasyObject.asBoolean(1));
				fleft0 = ReportUtil.getTOPNRateByUporDownNet(executor, getIdentityBean(), "switches", time, "up", showNum, EasyObject.asBoolean(0));
				data.put("area", "0");
			}else if("down".equals(indicators)){
				left0 = ReportUtil.getTOPNRateByUporDownNet(executor, getIdentityBean(), "switches", time, "down", showNum, EasyObject.asBoolean(1));
				fleft0 = ReportUtil.getTOPNRateByUporDownNet(executor, getIdentityBean(), "switches", time, "down", showNum, EasyObject.asBoolean(0));
				data.put("area", "1");
			}
				//left2 = CommonUtils.getTOPNRateByCommon(executor, getIdentityBean(), IMonGroup.MON_COMMON_NET.id(), time, "netuprate", showNum, EasyObject.asBoolean(1));
				//ReportUtil.GroupAndItemkey(executor, getIdentityBean(),"switches", "connectednum",time,showNum, EasyObject.asBoolean(1));
			
				//fleft2 = CommonUtils.getTOPNRateByCommon(executor, getIdentityBean(), IMonGroup.MON_COMMON_NET.id(), time, "netuprate", showNum, EasyObject.asBoolean(0));
		}else if(groupid == 4){//云平台
			if("core".equals(indicators)){//cpu使用率
				left0 = ReportUtil.getTOPNRateBySystem(executor, getIdentityBean(), IMonConsts.MON_CLOUD_CONTROLER.longValue(), time, "core", showNum, EasyObject.asBoolean(1));
				fleft0 = ReportUtil.getTOPNRateBySystem(executor, getIdentityBean(), IMonConsts.MON_CLOUD_CONTROLER.longValue(), time, "core", showNum, EasyObject.asBoolean(1));
				data.put("area", "0");
			}else if("nocore".equals(indicators)){
				left0 = ReportUtil.getTOPNRateBySystem(executor, getIdentityBean(), IMonConsts.MON_CLOUD_CONTROLER.longValue(), time, null, showNum, EasyObject.asBoolean(1));
				fleft0 = ReportUtil.getTOPNRateBySystem(executor, getIdentityBean(), IMonConsts.MON_CLOUD_CONTROLER.longValue(), time, null, showNum, EasyObject.asBoolean(1));
				data.put("area", "1");
			}
			
	    }
		
		data.put("left0", left0);
		data.put("fleft0", fleft0);
		/*data.put("left1", left1);
		data.put("fleft1", fleft1);
		
        if(!empty(left2)){
        	data.put("left2", left2);
        	data.put("fleft2", fleft2);
		}
        if(!empty(left3)){
        	data.put("left3", left3);
        	data.put("fleft3", fleft3);
		}
        if(!empty(left4)){
        	data.put("left4", left4);
        	data.put("fleft4", fleft4);
		}
		int datanum = data.size();
		data.put("datanum", String.valueOf(datanum));*/
		
		return data;
		
		
		/*CArray<Map> host1=array();
		 * int  tshowNum=0;
		if(host1.size()!=0){
			if(host1.size()>0 &&host1.size() <=showNum){
				tshowNum = host1.size();
			}else{
				tshowNum = showNum;
			}
			for(int i=0;i<tshowNum;i++){
				Map host=host1.get(i);
				left0.add(EasyMap.build("hostid", Nest.value(host, "hostid").asLong(), 
						                 "name", Nest.value(host, "name").asString(), 
						                 "value", Cphp.round(Nest.value(host, "avgvalue").asDouble(),2)));
			}
			for(int i=host1.size()-1;i>=host1.size()-tshowNum;i--){
				Map host=host1.get(i);
				fleft0.add(EasyMap.build("hostid", Nest.value(host, "hostid").asLong(), 
						                 "name", Nest.value(host, "name").asString(), 
						                 "value",  Cphp.round(Nest.value(host, "avgvalue").asDouble(),2)));
			}
		}
		data.put("left0", left0);
		data.put("fleft0", fleft0);
		long endTime=System.currentTimeMillis();;
		System.out.println("运行时间="+(startTime-endTime)+"ms");
		return data;*/
 }

	
	private CArray getTrendData(SQLExecutor executor,String period,int groupid,String hostid,String htname){
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
				Long[] groupids = new Long[]{IMonGroup.MON_NET_CISCO.id(),IMonGroup.MON_COMMON_NET.id()};
				String[] keys = new String[]{ItemsKey.COMMON_NET_IFINOCTETS.getValue(),ItemsKey.NET_UP_SPEED_NET_CISCO.getValue()};//下行速率
				hostdata = CommonUtils.queryHostIDAndNameTwo(keys,groupids);
			}
			if(groupid  == 4){//服务器
				String hoststr =null;
				String hostname = null;
				data.put("hostdata", hostdata);
				data.put("hostid", hoststr);
				data.put("hostname", hostname);
			}else{
				data.put("hostdata", hostdata);
				
				Map host=(Map)hostdata.get(0);
				String hoststr= Nest.value(host, "hostid").asString();
				String hostname= Nest.value(host, "name").asString();
				data.put("hostid", hoststr);
				data.put("hostname", hostname);
			}
            
		}else{
			//String hostid =String.valueOf(hostid);
			String area1src = null;
			String area2src  = null;
			String area3src = null;
			String area4src  = null;
			String area5src  = null;
			String stime = Cphp.date(Defines.TIMESTAMP_FORMAT);
			if(!"-1".equals(hostid)){
				int time=0;
				if("0".equals(period)){
					time=86400;
				}else if("1".equals(period)){
					time= 604800;
				}else if("2".equals(period)){
					time= 2592000;
				}
				if(htname != null){
					htname=htname+":";
				}
				if(groupid  == 1){//服务器
					String[] hostids=hostid.split("-");
					String[] names=new String[]{"CPU使用率","内存使用率","网络接口上行IO","网络接口下行IO","硬盘使用率"};
					if((101L)==Long.valueOf(hostids[1].trim())){
						area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[0],ItemsKey.CPU_USER_RATE.getValue(),time,htname+names[0]);
						area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.MEMORY_USELV_KEY,time,htname+names[1]);
						area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_INGERFACE_UPIPOS_LINUX,time,htname+names[2]);
						area4src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_INGERFACE_DOWNIPOS_LINUX,time,htname+names[3]);
						area5src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.DISK_USELV_KEY,time,htname+names[4]);
					}else{
						area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostids[0],ItemsKey.CPU_USER_RATE_WINDOWS.getValue(),time,htname+names[0]);
						area2src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(),executor, hostids[0],ItemsKey.MEMORY_USELV_KEY_WINDOWS.getValue(),time,htname+names[1]);
						area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_INGERFACE_UPIPOS_WINDOWS,time,htname+names[2]);
						area4src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_INGERFACE_DOWNIPOS_WINDOWS,time,htname+names[3]);
						area5src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.DISK_USELV_KEY_WINDOWS,time,htname+names[4]);
					}
				}else if(groupid  == 2){//虚拟机
					String[] names=new String[]{"CPU使用率","内存使用率","硬盘使用率"};
					String osType_vm = CommonUtils.getTargetLastValue( executor, getIdentityBean(), array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid, false, false);//操作系统类型
					if(osType_vm != null && osType_vm.contains("Windows")){
						area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.CPU_RATE_VM_WINDOWS.getValue(),time,htname+names[0]);	
					}else{
						area1src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.CPU_RATE_VM_LINUX.getValue(),time,htname+names[0]);
					}
					area2src = ReportUtil.create_Favorite_Graphs_STR(getIdentityBean(), executor, hostid,ItemsKey.MEMORY_RATE_VM.getValue(),time,htname+names[1]);
					area3src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostid,ItemsKey.DISK_RATE_VM,time,htname+"硬盘使用率");
				}else if(groupid  == 3){//交换机
					String[] names=new String[]{"发送利用率","接收利用率"};
					String[] hostids=hostid.split("-");
					if((302L)==Long.valueOf(hostids[1].trim())){
						area1src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFINOCTETS,time,htname+names[0]);
						area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.COMMON_NET_IFOUTOCTETS,time,htname+names[1]);
					}else{
						area1src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_UP_SPEED_NET_CISCO,time,htname+names[0]);
						area2src = ReportUtil.create_Favorite_Graphs_Prototype_STR(getIdentityBean(), executor, hostids[0], ItemsKey.NET_DOWN_SPEED_NET_CISCO,time,htname+names[1]);
					}
				
				}else if(groupid  == 4){//云平台
					Long iaasCtrlHostId = BlocksUtil.getHostIdByMonCloud(getIdentityBean(),executor);
					CArray<ItemsKey> keys =array();
					keys.add(ItemsKey.VM_CORE_USED);
					keys.add(ItemsKey.VM_CORE_TOTAL);
					area1src = CommonUtils.getActionUrlToMultiLine(String.valueOf(iaasCtrlHostId),keys,time,600);
					keys =array();
					keys.add(ItemsKey.VM_MEMORY_USED);
					keys.add(ItemsKey.VM_MEMORY_TOTAL);
					area2src = CommonUtils.getActionUrlToMultiLine(String.valueOf(iaasCtrlHostId),keys,time,600);
					keys =array();
					keys.add(ItemsKey.VM_CINDER_USED);
					keys.add(ItemsKey.VM_CINDER_TOTAL);
					area3src =  CommonUtils.getActionUrlToMultiLine(String.valueOf(iaasCtrlHostId),keys,time,600);
				}
			}	
			data.put("area1src", area1src);
            data.put("area2src", area2src);  
            if(!empty(area3src)){
            	data.put("area3src", area3src);
    		}
            if(!empty(area4src)){
            	data.put("area4src", area4src);
    		}
            if(!empty(area5src)){
            	data.put("area5src", area5src);
    		}
            
            int datanum = data.size();
    		data.put("datanum", String.valueOf(datanum));
		}
		return data;
	}

}
