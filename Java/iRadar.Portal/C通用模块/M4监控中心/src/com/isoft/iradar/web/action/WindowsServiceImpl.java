package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
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
 * windows服务器设备类型详情页面
 * @author HP Pro2000MT
 *
 */
public class WindowsServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		//cpu利用率、cpu负载
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(_("the cpuUsage of host"));
		titleRowFrist.addItem(_("CPU_LOAD"));
		target_key.addRow(titleRowFrist,"speed_center");
		
		CRow firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_USER_RATE_WINDOWS.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_LOAD_WINDOWS.getValue()));
		target_key.addRow(firstRow,"speed_center");
		
		//磁盘使用率、内存使用率
		CRow fourRowThrid = new CRow();
		fourRowThrid.addItem(_("the diskUsage of host"));
		fourRowThrid.addItem(_("the memoryUsage of host"));
		target_key.addRow(fourRowThrid,"speed_center");
		
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.DISK_USELV_KEY_WINDOWS));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MEMORY_USELV_KEY_WINDOWS.getValue()));
		target_key.addRow(firstRow,"speed_center");
		
		//磁盘读、写速率
		titleRowFrist = new CRow();
		titleRowFrist.addItem(_("the diskInfo")+_("the read request BW of host"));
		titleRowFrist.addItem(_("the diskInfo")+_("the write request BW of host"));
		target_key.addRow(titleRowFrist,"speed_center");
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DISK_READ_REQUEST_BW_WINDOWS.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DISK_WRITE_REQUEST_BW_WINDOWS.getValue()));
		target_key.addRow(firstRow,"speed_center");
		
		//网络上、下行速率
		titleRowFrist = new CRow();
		titleRowFrist.addItem(_("the netInfo")+_("the net up flow of host"));
		titleRowFrist.addItem(_("the netInfo")+_("the net down flow of host"));
		target_key.addRow(titleRowFrist,"speed_center");
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.NET_UP_FLOW_WINDOWS));
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.NET_DOWN_FLOW_WINDOWS));
		target_key.addRow(firstRow,"speed_center");
		
		CRow thridRow = new CRow();
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
		String total_memory = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.MEMORY_TOTAL_WINDOWS.getValue(), hostid, true, true);	//内存总大小
		String cpu_model = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CPU_MODEL_WINDOWS.getValue()).value().value().asString();//CPU信息
		String os = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.OSTYPE_VM_WINDOWS.getValue()).value().value().asString();//操作系统类型
		//赋值
		Map leftData = array();
		leftData.put(_("the name Of Host"), hostname);
		leftData.put(_("the alias Of Host"), contract_number);
		leftData.put(_("the department Of Host"), hardware);
		leftData.put(_("the machineRoom Of Host"), software);
		leftData.put(_("the cabinet Of Host"), url_a);
		leftData.put(_("IP　Address"), Nest.value(data, "host","interfaces",0,"ip").asString());
		
		Map rightData = array();
		rightData.put(_("the totalMem of Host"), total_memory);
		rightData.put(_("the status of Host"), Defines.HOST_STATUS_MONITORED==Nest.value(data, "host","status").asInteger()?Cphp._("Monitored"):Cphp._("Not monitored"));
		rightData.put(_("the serial number of Host"), os_full);
		rightData.put(_("the manufacturer of Host"), vendor);
		rightData.put(_("the remark of Host"), remark);
		
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