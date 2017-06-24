package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;
/**
 * Linux设备类型关键指标显示表格
 * @author HP Pro2000MT
 *
 */
public class LinuxHostDetailServiceImpl implements IHostDetailService {
	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//关键指标部分
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(_("the cpuUsage of host"));
		titleRowFrist.addItem(_("CPU_LOAD"));
		target_key.addRow(titleRowFrist,"speed_center");
		
		CRow firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_USER_RATE.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.CPU_LOAD));
		target_key.addRow(firstRow,"speed_center");
		
		//磁盘使用率、内存使用率
		CRow fourRowThrid = new CRow();
		fourRowThrid.addItem(_("the diskUsage of host"));
		fourRowThrid.addItem(_("the memoryUsage of host"));
		target_key.addRow(fourRowThrid,"speed_center");
		
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.DISK_USELV_KEY));
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.MEMORY_USELV_KEY));
		target_key.addRow(firstRow,"speed_center");
		
		//磁盘读、写速率
		CRow titleRowThrid = new CRow();
		titleRowThrid.addItem(_("the diskInfo")+_("the read request BW of host"));
		titleRowThrid.addItem(_("the diskInfo")+_("the write request BW of host"));
		target_key.addRow(titleRowThrid,"speed_center");
		
		CRow thridRow = new CRow();
		thridRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.DISK_READ_SPEED));
		thridRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.DISK_WRITE_SPEED));
		target_key.addRow(thridRow,"speed_center");
		
		//网络上、下行速率
		titleRowThrid = new CRow();
		titleRowThrid.addItem(_("the netInfo")+_("the net up speed of host"));
		titleRowThrid.addItem(_("the netInfo")+_("the net down speed of host"));
		target_key.addRow(titleRowThrid,"speed_center");
		
		thridRow = new CRow();
		thridRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.NET_UP_SPEED));
		thridRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.NET_DOWN_SPEED));
		target_key.addRow(thridRow,"speed_center");
		
		
		return target_key;
	}

	/**
	 * 健康度仪表盘模块
	 */
	@Override
	public CDiv getHealthFunsionCharts(String divID, String styleClass,Map data) {
		JSONArray  jsonObject = JSONArray.fromObject(data); 
		CJSScript _fusionScript = new CJSScript(get_js("showHealth(" + divID + "," + jsonObject.toString() +")"));
		CDiv div = new CDiv(array( _fusionScript));
		div.setAttribute("id", divID);
		div.setAttribute("class",styleClass);
		return div;
	}
	
	/**
	 * 组装要显示的设备详情信息
	 * @param data	数据源
	 * @param overviewFormList	要返回的表单
	 * @return
	 */
	@Override
	public CFormList getOverviewForm(IIdentityBean idBean, SQLExecutor executor,Map data,String hostid){
		CFormList form = new CFormList();
		form.addClass("vmCommonClass");
		//获取数据
		String hostname = rda_str2links(Nest.value(data,"host","host").asString());					//设备名称
		String contract_number = rda_str2links(Nest.value(data,"host","inventory","contract_number").asString());			//别名
		String hardware = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","hardware").asString(),IMonConsts.DEPT));	//所属部门
		String software = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","software").asString(),IMonConsts.MOTOR_ROOM));//所在机房
		String url_a = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","url_a").asString(),IMonConsts.CABINET));		//所在机柜
		String os_full = rda_str2links(Nest.value(data,"host","inventory","os_full").asString());		//编号
		String vendor = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","vendor").asString(),IMonConsts.FIRM));		//厂商
		String remark = rda_str2links(Nest.value(data, "host","inventory","host_networks").asString());	//备注
		String cpu_model = LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.CPU_INFO_LINUX.getValue()).value().out().print().toString();//CPU信息
		String total_memory = LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.TOTAL_MEMORY.getValue()).values().sum().round(2).out().format().toString();//内存总大小
		String os = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.UPTIME_VM_LINUX.getValue()).value().value().asString();//操作系统类型
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostname);
		leftData.put("资产别名", contract_number);
		leftData.put("所属部门", hardware);
		leftData.put("所在机房", software);
		leftData.put("所在机柜", url_a);
		leftData.put(_("IP　Address"), Nest.value(data, "host","interfaces",0,"ip").asString());
		
		Map rightData = array();
		rightData.put("内存总量", total_memory);
		rightData.put(_("the status of Host"), Defines.HOST_STATUS_MONITORED==Nest.value(data, "host","status").asInteger()?Cphp._("Monitored"):Cphp._("Not monitored"));
		rightData.put("编号", os_full);
		rightData.put("厂商", vendor);
		rightData.put("备注", remark);
		
		CFormList baseInfo = CommonUtils.assembleDetailDataForTable(leftData, rightData,true);
		CFormList cpuInfo = new CFormList();
		CSpan cpuInfo_s = new CSpan(cpu_model);
		
		cpuInfo_s.setTitle(cpu_model);
		cpuInfo.addRow(_("the cpuInfo of host"),cpuInfo_s);
		
		cpuInfo_s = new CSpan(os);
		cpuInfo_s.setTitle(os);
		cpuInfo.addRow(_("the osType Of VM"),cpuInfo_s);
		
		form.addRow(baseInfo);
		form.addRow(cpuInfo);
		return form;
	}
}
