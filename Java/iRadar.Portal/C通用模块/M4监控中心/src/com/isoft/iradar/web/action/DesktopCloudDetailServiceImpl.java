package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class DesktopCloudDetailServiceImpl implements IHostDetailService {
	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		//分配的云主机数
		String vmPoolAllocVmCount = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.VMPOOLALLOCVMCOUNT_DESKTOPC.getValue()).values().sum().toString();
		//运行的云主机数
		String vmPoolRunningVmCount = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.VMPOOLRUNNINGVMCOUNT_DESKTOPC.getValue()).values().sum().toString();
		//空间总量
		String spTotalSize = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.SPTOTALSIZE_DESKTOPC.getValue()).values().sum().toString();
		//空间空闲
		String spFreeSize = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.SPFREESIZE_DESKTOPC.getValue()).values().sum().toString();
		
		Map diskMap = new HashMap();
		diskMap.put("分配的云主机数", vmPoolAllocVmCount);
		diskMap.put("运行的云主机数", vmPoolRunningVmCount);
		CDiv disk_read_write = this.getFusionChartsDesignModel("disk_read_write", "equipment_details", diskMap,"");		//磁盘读取
		
		Map netMap = new HashMap();
		netMap.put("空间总量", spTotalSize);
		netMap.put("空间空闲", spFreeSize);
		CDiv net_up_down = this.getFusionChartsDesignModel("net_up_down", "equipment_details", netMap,"");				//网络上下行
		
		CRow titleRowThrid = new CRow();
		titleRowThrid.addItem("分配的云主机数"+vmPoolAllocVmCount+" 运行的云主机数"+vmPoolRunningVmCount);
		titleRowThrid.addItem("空间总量"+spTotalSize+" 空间空闲"+spFreeSize);
		
		CRow thridRow = new CRow();
		thridRow.addItem(disk_read_write);
		thridRow.addItem(net_up_down);
		
		target_key.addRow(titleRowThrid,"speed_center");
		target_key.addRow(thridRow,"speed_center");
		
		return target_key;
	}
	
	/**
	 * fusionCharts横向柱状图
	 * @param divID	divID
	 * @param styleClass 样式
	 * @param num	正整数数值
	 * @return
	 */
	private CDiv getFusionChartsDesignModel(String divID,String styleClass,Map data,String title){
		String label = "";
		for(Object key : data.keySet()){			
			label += "<set label=\""+key+"\" value=\""+data.get(key)+"\" />";
		}
		CJSScript _fusionScript = new CJSScript(get_js("bar2dForHostDetail('" + divID + "','" + label + "','" + title +"')"));
		CDiv div = new CDiv(array( _fusionScript));
		div.setAttribute("id", divID);
		div.setAttribute("class",styleClass);
		return div;
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
		String hostNamm = rda_str2links(Nest.value(data,"host","host").asString());	//设备名称
		String hostActiveVmCount = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.HOSTACTIVEVMCOUNT_DESKTOPC.getValue()).values().sum().out().print();
		String hostMemoryUsage = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.HOSTMEMORYUSAGE_DESKTOPC.getValue()).values().sum().out().print();
		String hostCpuUsage = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.HOSTCPUUSAGE_DESKTOPC.getValue()).values().sum().out().print();
		
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostNamm);
		leftData.put("活动的云主机数", hostActiveVmCount);
		
		Map rightData = array();
		rightData.put("进程占用的CPU", hostCpuUsage);
		rightData.put("进程占用的内存", hostMemoryUsage);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}