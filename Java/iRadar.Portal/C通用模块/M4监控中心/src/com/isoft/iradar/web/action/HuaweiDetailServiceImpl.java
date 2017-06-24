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
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;

public class HuaweiDetailServiceImpl implements IHostDetailService {
	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo targetKey = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		CTenanView viewService = new CTenanView();
		
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(_("NET_HUAWEI_CPU_RATE"));
		titleRowFrist.addItem(_("NET_HUAWEI_MEM_FREE"));
		targetKey.addRow(titleRowFrist,"speed_center");
		
		CRow firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.NET_HUAWEI_CPU_RATE.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.NET_HUAWEI_MEM_FREE.getValue()));
		targetKey.addRow(firstRow,"speed_center");
		
		titleRowFrist = new CRow();
		titleRowFrist.addItem(_("the net up"));
		titleRowFrist.addItem(_("the net down"));
		targetKey.addRow(titleRowFrist,"speed_center");
		
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.COMMON_NET_IFOUTOCTETS));
		firstRow.addItem(viewService.create_Favorite_Graphs_Prototype(idBean, executor, hostid, ItemsKey.COMMON_NET_IFINOCTETS));
		targetKey.addRow(firstRow,"speed_center");
		
		return targetKey;
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
		String hostname = rda_str2links(Nest.value(data, "host","host").asString());/*LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.COMMON_NET_SYSNAME.getValue()).value().out().format();//设备名称*/		String contract_number = rda_str2links(Nest.value(data,"host","inventory","contract_number").asString());			//别名
		String hardware = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","hardware").asString(),IMonConsts.DEPT));	//所属部门
		String software = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","software").asString(),IMonConsts.MOTOR_ROOM));//所在机房
		String url_a = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","url_a").asString(),IMonConsts.CABINET));		//所在机柜
		String os_full = rda_str2links(Nest.value(data,"host","inventory","os_full").asString());		//编号
		String vendor = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","vendor").asString(),IMonConsts.FIRM));		//厂商
		String remark = rda_str2links(Nest.value(data, "host","inventory","host_networks").asString());	//备注
		String ifNumber = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.COMMON_NET_IFNUMBER.getValue()).value().out().format();//cpu空闲率
		String memTotal = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.NET_HUAWEI_MEM_TOTAL.getValue()).value().out().format();
		String cpuAvg1 = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.NET_HUAWEI_CPU_AVG1.getValue()).value().out().format();
		String cpuAvg5 = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.NET_HUAWEI_CPU_AVG5.getValue()).value().out().format();

		Map leftData = array();
		leftData.put(_("the name Of Host"), hostname);
		leftData.put(_("the alias Of Host"), contract_number);
		leftData.put(_("the department Of Host"), hardware);
		leftData.put(_("the machineRoom Of Host"), software);
		leftData.put(_("the cabinet Of Host"), url_a);
		leftData.put(_("NET_HUAWEI_MEM_TOTAL"), memTotal);
		
		Map rightData = array();
		rightData.put(_("the manufacturer of Host"), vendor);
		rightData.put(_("the remark of Host"), remark);
		rightData.put(_("the serial number of Host"), os_full);
		rightData.put(_("ifNumber"), ifNumber);
		rightData.put(_("NET_HUAWEI_CPU_AVG1"), cpuAvg1);
		rightData.put(_("NET_HUAWEI_CPU_AVG5"), cpuAvg5);
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
