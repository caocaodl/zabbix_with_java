package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.JsUtil;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 设备详情视图页面
 * @author HP Pro2000MT
 *
 */
public class CTenanView extends CViewSegment{

	/**
	 * 设备详情页面布局
	 */
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostInventoryWidget = new CWidget(null, "inventory-host");

		CTableInfo table = new CTableInfo();			//创建要显示的table
		CTableInfo hostDetail_table = new CTableInfo();	//设备详情表格
		hostDetail_table.setAttribute("class", hostDetail_table.getAttribute("class")+" detail");
		String hostid = Nest.value(data, "hostid").asString();
		//设备详情数据
		CFormList overviewFormList = getOverviewForm(data,idBean,executor,hostid);
		
		CRow hostDetailHeader = new CRow();
		CDiv divHead = new CDiv(_("Host detail"), "details");
		hostDetailHeader.addItem(divHead);
		hostDetailHeader.addItem(new CDiv("健康度", "health"));
		hostDetail_table.addRow(hostDetailHeader);
		//设备详情内容
		CRow hostDetailMain = new CRow();
		hostDetailMain.addItem(new CDiv(overviewFormList,"equipment_details"));

		//监控度需要的数据
		//String healthNum = "85";
		Map healthMap = CommonUtils.host_health(idBean, executor, hostid, IMonConsts.MON_VM.toString());
		//健康度模拟图片(仪表盘)
		CDiv div = new CDiv();
		div.setAttribute("id", "trend_chart");
		div.setAttribute("class","equipment_details");
		JSONArray  jsonObject = JSONArray.fromObject(healthMap); 
		CJSScript _fusionScript = new CJSScript(get_js("showHealth(trend_chart," + jsonObject.toString() +")"));
		
		div.addItem(_fusionScript);
		hostDetailMain.addItem(div);	
		hostDetail_table.addRow(hostDetailMain,"first_td");
		
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		CArray<Map> templateids =new CArray<Map>();
		templateids.put("templateid", Nest.value(data, "templateid").asString());
		String cpuUseRate="--";//CPU使用率
		String cPUse="--";//磁盘使用率
		String memoryUseRate="--";//内存使用率
		String memoryUsed="--";//内存空闲
		String totalMemory="--";//内存总量
		String reed="--";//硬盘读的速率
		String write="--";//硬盘写的速率
		cpuUseRate = CommonUtils.returnCPURate(executor, idBean, hostid,array(ItemsKey.CPU_RATE_VM_LINUX,ItemsKey.CPU_RATE_VM_WINDOWS), true,false);			//cpu使用率
		cPUse = CommonUtils.returnDiskRate(executor, idBean, hostid, ItemsKey.DISK_READ_SPEED_VM.getValue());
		memoryUseRate = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.MEMORY_RATE_VM.getValue(),hostid, true,false);//内存使用率
     	memoryUsed = CommonUtils.returnUsedMemory(executor, idBean, hostid, ItemsKey.AVAILABEL_MEMORY_VM.getValue(),true,true);
		totalMemory = CommonUtils.returnTotalMemory(executor, idBean, hostid, ItemsKey.TOTAL_MEMORY_VM.getValue(),true,true);
		reed = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.DISK_READ_SPEED_VM_LINUX,ItemsKey.DISK_READ_SPEED_VM_WINDOWS), hostid, true, false);//磁盘读取速率
		write = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.DISK_WRITE_SPEED_VM_LINUX,ItemsKey.DISK_WRITE_SPEED_VM_WINDOWS), hostid, true, false);//磁盘写入速率
		
		String osType_vm = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid, false, false);//操作系统类型
		CRow titleRow1 = new CRow();
		CRow firstRow1 = new CRow();
		titleRow1.addItem("CPU使用率 ");
		titleRow1.addItem("内存使用率 ");
		if(osType_vm != null && osType_vm.contains("Windows")){//依据操作系统不同添加不同的cpu使用率趋势图
			firstRow1.addItem(create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_RATE_VM_WINDOWS.getValue()));			
		}else{
			firstRow1.addItem(create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_RATE_VM_LINUX.getValue()));
		}
		//firstRow1.addItem(create_Favorite_Graphs(idBean, executor, hostid,CommonUtils.getTargetKey(executor, idBean, array(ItemsKey.CPU_RATE_VM_LINUX,ItemsKey.CPU_RATE_VM_WINDOWS), hostid, true, false)));		//cpu使用率);
		firstRow1.addItem(create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MEMORY_RATE_VM.getValue()));
		target_key.addRow(titleRow1, "speed_center");
		target_key.addRow(firstRow1, "speed_center");
		if(reed.equals("--")){
			reed="0";
		}
		if(write.equals("--")){
			write="0";
		}
		
		CArray<Map> diskRead=new CArray<Map>();
		diskRead.put(reed);
		CArray<Map> diskWrite=new CArray<Map>();
		diskWrite.put(write);
		CRow firstRow3 = new CRow();
		firstRow3.addItem(new CDiv("","","reedwrite"));
		firstRow3.addItem(new CDiv("","","netDownSpeedup"));
		target_key.addRow(firstRow3, "speed_center");
		getFusionChartsDesignModel("diskRead","硬盘读速率",diskRead,0);
		getFusionChartsDesignModel("diskWrite","硬盘写速率",diskWrite,1);
	
		//实现div分割区域
		CDiv splitDiv = new CDiv();
		splitDiv.addStyle("height:25px;");
		
		//最近告警列表数据
		CTable trigger_table = (CTable)Nest.value(data, "triggerForm").$();
		table.addRow(hostDetail_table);					//添加设备详情(包含健康度)
		
		table.addRow(splitDiv);							//添加分割区域
		
		table.addRow(new CDiv(_("Target Key"),"norm"));
		table.addRow(target_key);						//添加关键指标
		
		table.addRow(splitDiv);							//添加分割区域
		
		table.addRow(new CDiv(_("recentlyEvents"),"alarm"));
		table.addRow(trigger_table);					//添加告警列表
		
		//页面加载table
		hostInventoryWidget.addItem(table);
		return hostInventoryWidget;
	}	
	
	
	


	/**
	 * 组装要显示的设备详情信息
	 * @param data	数据源
	 * @param overviewFormList	要返回的表单
	 * @return
	 */
	public CFormList getOverviewForm(Map data,IIdentityBean idBean, SQLExecutor executor,String hostid){
		CFormList form = new CFormList();
		form.addClass("vmCommonClass");
		//根据键值获取相关数据
		String hostName = rda_str2links(Nest.value(data, "host", "0", "name").asString());
		String user_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.USER_VM.getValue(),hostid,false,false);//用户
		String floatingIPs_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.FLOATINGIPS_VM.getValue(), hostid, true, true);//浮动IP
		String cpuInfo_vm = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.CPU_MODEL_VM_LINUX, ItemsKey.CPU_MODEL_VM_WINDOWS), hostid, false, false);//CPU信息
		String Intranet_ip = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.FIXEDIPS_VM.getValue(), hostid, false, false);//内网IP
		String status_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.STATUS_VM.getValue(), hostid, true, true);//云主机状态
		
		String total_memory = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.TOTAL_MEMORY_VM.getValue(), hostid, true, true);	//内存总大小
		String mem_available_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.AVAILABEL_MEMORY_VM.getValue(), hostid, true, true);	//可用内存
		String disk_total_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DISK_VM.getValue(), hostid, true, true);	//磁盘总量
		String disk_used_vm = CommonUtils.getPrototypeTargetLastValue(ItemsKey.DISK_RATE_VM.getValue(), hostid).avg().round(2).out().format();
		
		
		String osType_vm = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid, false, false);//操作系统类型
		
		if ("ACTIVE".equals(status_vm) || " ".equals(status_vm)) {
			status_vm = _("ACTIVE"); // 运行
		}else if("SUPENDED".equals(status_vm)){
			status_vm = _("SUPENDED"); // 挂起
		}else if("PAUSED".equals(status_vm)){
			status_vm = _("PAUSED"); // 暂停
		}else if("SHUTOFF".equals(status_vm)){
			status_vm = _("SHUTOFF"); // 关机
		}else if ("--".equals(status_vm)) {
			status_vm = _("Unknown");
		}
		
		//赋值
		Map leftData = array();
		leftData.put(_("the name Of Host"), hostName);       
		leftData.put(_("the user Of Vm"), user_vm);             
		leftData.put(_("the floatingIps Of VM"), floatingIPs_vm); 
		leftData.put("内网 IP", Intranet_ip);
		leftData.put(_("the status Of VM"), status_vm);  
		
		Map rightData = array();
		rightData.put(_("the totalMem Of Host"),total_memory);   
		rightData.put(_("the availableMem Of Host"), mem_available_vm);
		rightData.put(_("disk_vm"), disk_total_vm);
		rightData.put(_("the diskUsage of host"), disk_used_vm);
		
		CFormList baseInfo = CommonUtils.assembleDetailDataForTable(leftData, rightData,true);
		
		CFormList cpuInfo = new CFormList();
		CSpan cpuInfo_s = new CSpan(cpuInfo_vm);
		
		cpuInfo_s.setTitle(cpuInfo_vm);
		cpuInfo.addRow(_("the cpuInfo of host"),cpuInfo_s);
		
		cpuInfo_s = new CSpan(osType_vm);
		cpuInfo_s.setTitle(osType_vm);
		cpuInfo.addRow(_("the osType Of VM"),cpuInfo_s);

		form.addRow(baseInfo);
		form.addRow(cpuInfo);
		return form;
	}	
	public  CList create_Favorite_Graphs(IIdentityBean idBean, SQLExecutor executor,String hostid,String key) {

		CList favList = new CList(null, "firstshape", _("No graphs added."));
		long hostids = Long.parseLong(hostid);
		String itemid = "";
		CHostGet ch = new CHostGet();
		ch.setOutput(new String[] { "hostid" });
		ch.setSelectItems(new String[] { "itemid", "key_" });
		ch.setHostIds(hostids);
		CArray<Map> item = API.Host(idBean, executor).get(ch);
		CArray<Map> items = Nest.value(item, "0", "items").asCArray();
		for (Entry<Object, Map> e : items.entrySet()) {
			if (e.getValue().get("key_").equals(key)) {
				itemid = e.getValue().get("itemid").toString();
			}
		}
		Date date = new Date();
		// Long curtime = date.getTime();
		// Calendar c = Calendar.getInstance();
		// c.add(Calendar.HOUR_OF_DAY, -1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(date.getTime());
		String timeG = mDateTime.substring(0, 16);
		timeG = timeG.replace("-", "");
		timeG = timeG.replace(":", "");
		timeG = timeG.replace(" ", "");
		timeG = timeG + "00";
		int time = 60*60*24;
		if(itemid == null || itemid.equals("")){
			itemid = "00000";
		}
		String src = "chart.action?itemid=" + itemid + "&period=" + time + "&stime=" + timeG + "&updateProfile=0&width=550";
		CImg img = new CImg(src, "");
		CDiv d = new CDiv();
		d.addItem(img);
		favList.addItem(d, "");

		return favList;
	}
	
	public  CList create_Favorite_Graphs_Prototype(IIdentityBean idBean, SQLExecutor executor,String hostid, ItemsKey key) {
		return create_Favorite_Graphs_Prototype(idBean,executor,hostid,key,null);
	}
	public  CList create_Favorite_Graphs_Prototype(IIdentityBean idBean, SQLExecutor executor,String hostid, ItemsKey key,String flag) {
		CList favList = new CList(null, "firstshape", _("No graphs added."));
		String func = "";
		if(flag != null){
			func = "&func="+flag;
		}
		int time = 60*60*24; //一天
		String timeG = Cphp.date(Defines.TIMESTAMP_FORMAT);
		
		String src = "chartprototype.action?hostid=" + hostid + "&key="+key.name()+"&period=" + time + "&stime=" + timeG + "&updateProfile=0&width=550"+func;
		CImg img = new CImg(src, "");
		CDiv d = new CDiv();
		d.addItem(img);
		favList.addItem(d, "");

		return favList;
	}
	/**
	 * fusionCharts横向柱状图
	 * @param divID	divID
	 * @param styleClass 样式
	 * @param num	正整数数值
	 * @return
	 */
	private void getFusionChartsDesignModel(String divID,String title,Map data,int duan){
		//健康度模拟图片(仪表盘)
		String	label="";
		int	i=0;
		int	j=0;
		if(duan==0){
			for(Object key : data.keySet()){
				
				if(i==0){
				label = label+"<set label=\"读的速率\" value=\""+data.get(key)+"\" />";
				i++;
				}
				else{
					label = label+"<set label=\"写的速率\" value=\""+data.get(key)+"\" />";
					}
			}
		}else{
			for(Object key : data.keySet()){
				
				if(j==0){
				label = label+"<set label=\"网络发送速率\" value=\""+data.get(key)+"\" />";
				j++;
				}
				else{
					label = label+"<set label=\"网络接收速率\" value=\""+data.get(key)+"\" />";
					}
			}
			
		}
		
		JsUtil.rda_add_post_js("jQuery(\"#"+divID+"\").insertFusionCharts({\n" + 
				"			        			type : \"bar2d\",\n" + 
				"			        			width : \"400\",\n" + 
				"			        			height : \"170\",\n" + 
				"			        			dataFormat : \"xml\",\n" + 
				"			        			dataSource : '<chart caption=\""+title+"\" subcaption=\"\" yaxisname=\"\" defaultAnimation=\"1\" numbersuffix=\"\" showvalues=\"1\" plotgradientcolor=\"\"  plotborderalpha=\"0\" alternatevgridalpha=\"0\" divlinealpha=\"0\" canvasborderalpha=\"0\" bgcolor=\"#FFFFFF\" numberscalevalue=\"1024,1024,1024\" numberscaleunit=\"GB,MB,KB\" basefontsize=\"12\" basefontcolor=\"#194920\" palettecolors=\"#f8bd19\" showyaxisvalues=\"0\" showborder=\"0\">\""+label+"\\</chart>'"+
				"								});");
		
	
	}	

}

