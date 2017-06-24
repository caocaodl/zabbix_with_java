package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;

public class CiscoDetailServiceImpl implements IHostDetailService{

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		//网络上/下行流量
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem("网络上行速率");
		titleRowFrist.addItem("网络下行速率");
		target_key.addRow(titleRowFrist,"speed_center");
		
		CRow firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.CISCO_IFOUTOCTETS));
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.CISCO_IFINOCTETS));
		target_key.addRow(firstRow,"speed_center");
		
		//网络上/下行丢包率
		CRow fourRowThrid = new CRow();
		fourRowThrid.addItem("网络上行丢包率");
		fourRowThrid.addItem("网络下行丢包率");
		target_key.addRow(fourRowThrid,"speed_center");
		
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.CISCO_NET_IFOUTERRORS));
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.CISCO_NET_IFINERRORS));
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
		CJSScript _fusionScript = new CJSScript(get_js("angulargaugeForHostDetail('" + divID + "','" + data.get("healthNum") +"')"));
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
		
		String memoryFree = LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.CISCO_CISCOMEMORYPOOLFREE.getValue()).values().sum().out().format();//内存空闲大小
		String memoryUsed = LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.CISCO_CISCOMEMORYPOOLUSED.getValue()).values().sum().out().format();//内存已使用大小 
		String cpuRate = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CISCO_CPU_RATE.getValue()).value().out().format();//cpu使用率		
		String connec = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CISCO_ICMPPING.getValue()).value().out().format();//连通性
		
		//赋值
		Map leftData = array();
		leftData.put(_("the name Of Host"), hostname);
		leftData.put(_("the alias Of Host"), contract_number);
		leftData.put(_("the department Of Host"), hardware);
		leftData.put(_("the machineRoom Of Host"), software);
		leftData.put(_("the cabinet Of Host"), url_a);
		
		Map rightData = array();
		rightData.put("CPU使用率", cpuRate);
		rightData.put("连通性", connec);
		rightData.put("内存空闲大小",memoryFree);
		rightData.put("内存已使用大小", memoryUsed);
		
		form.addRow(CommonUtils.assembleDetailDataForTable(leftData, rightData,true));
		return form;
	}
}
