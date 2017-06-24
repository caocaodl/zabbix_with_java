package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.collections.map.HashedMap;

import com.isoft.biz.web.bean.common.SystemWordbook;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 其他设备类型详情页面显示内容
 * @author HP Pro2000MT
 *
 */
public class OthersHostDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		String cpuRate = CommonUtils.returnCPURate(executor, idBean, hostid,ItemsKey.CPU_USER_RATE.getValue(), true,false);			//cpu使用率
		String cpuLoad = CommonUtils.returnCPULoad(executor, idBean, hostid, ItemsKey.CPU_LOAD.getValue(),true, false);				//cpu负载
		
		String memoryRate = CommonUtils.returnMemoryRate(executor, idBean, hostid,ItemsKey.MEMORY_USELV_KEY.getValue(),true,false);	//内存使用率
		String memoryUsed = CommonUtils.returnUsedMemory(executor, idBean,hostid,ItemsKey.MEMORY_USED_LINUX.getValue(),true,true);			//内存已使用量
		String memoryTotal = CommonUtils.returnTotalMemory(executor, idBean, hostid,ItemsKey.TOTAL_MEMORY.getValue(),true,true);				//内存总量
		
		String diskRate = CommonUtils.returnDiskRate(executor, idBean, hostid,ItemsKey.DISK_USELV_KEY.getValue(),true,false);	//磁盘使用率
		String diskUsed = CommonUtils.returnDiskUsed(executor, idBean, hostid,ItemsKey.USED_DISK_SPACE_ON.getValue(),true,true);	//磁盘已使用量
		String diskTotal = CommonUtils.returnDiskTotal(executor, idBean, hostid,ItemsKey.TOTAL_DISK_SPACE_ON.getValue(),true,false);//磁盘总量
		
		String diskReadSpeed = LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.DISK_READ_SPEED.getValue()).values().sum().round(2).out().format().toString(); //磁盘读取速率
		String diskWriteSpeed = LatestValueHelper.buildByPrototypeKey(EasyObject.asLong(hostid), ItemsKey.DISK_WRITE_SPEED.getValue()).values().sum().round(2).out().format().toString(); //磁盘写入速率
		
		
		String netUpSpeed = CommonUtils.returnNetRateForUp(executor, idBean, hostid,ItemsKey.NET_UP_SPEED.getValue(),false);		//网络上行速率
		String netDownSpeed = CommonUtils.returnNetRateForDown(executor, idBean, hostid,ItemsKey.NET_DOWN_SPEED.getValue(),false);  //网络下行速率
		
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//显示关键指标的空间或者图片
		CList cpu_rate = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_USER_RATE.getValue());		//cpu使用率
		CList memory_rate = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MEMORY_USELV_KEY.getValue());	//内存使用率
		CList disk_rate = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DISK_USELV_KEY.getValue());		//磁盘使用率
		CList cpu_load = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_LOAD.getValue());				//CPU负载
		
		Map diskMap = new HashMap();
		diskMap.put("磁盘读取速率", diskReadSpeed.equals("--")?0:diskReadSpeed);
		diskMap.put("磁盘写入速率", diskWriteSpeed.equals("--")?0:diskWriteSpeed);
		CDiv disk_read_write = this.getFusionChartsDesignModel("disk_read_write", "equipment_details", diskMap,"");		//磁盘读取
		
		Map netMap = new HashMap();
		netMap.put("网络上行速率", netUpSpeed.equals("--")?0:netUpSpeed);
		netMap.put("网络下行速率", netDownSpeed.equals("--")?0:netDownSpeed);
		CDiv net_up_down = this.getFusionChartsDesignModel("net_up_down", "equipment_details", netMap,"");				//网络上下行
		
		
		//关键指标
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem("CPU使用率"+cpuRate);
		titleRowFrist.addItem("CPU负载 "+cpuLoad);
		CRow firstRow = new CRow();
		firstRow.addItem(cpu_rate);
		firstRow.addItem(cpu_load);
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem("磁盘 "+diskUsed+"/"+diskTotal+"("+memoryRate+")");
		titleRowSecond.addItem("内存 "+memoryUsed+"/"+memoryTotal+"("+diskRate+")");
		CRow sencondRow = new CRow();
		sencondRow.addItem(disk_rate);
		sencondRow.addItem(memory_rate);
		
		CRow titleRowThrid = new CRow();
		titleRowThrid.addItem("磁盘  读取"+diskReadSpeed+" 写入"+diskWriteSpeed);
		titleRowThrid.addItem("网络 上行"+netUpSpeed+" 下行"+netDownSpeed);
		CRow thridRow = new CRow();
		thridRow.addItem(disk_read_write);
		
		thridRow.addItem(net_up_down);
		
		target_key.addRow(titleRowFrist,"speed_center");
		target_key.addRow(firstRow,"speed_center");
		target_key.addRow(titleRowSecond,"speed_center");
		target_key.addRow(sencondRow,"speed_center");
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
	public CDiv getHealthFunsionCharts(String divID, String styleClass,
			Map data) {
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
		//获取数据
		String hostname = rda_str2links(Nest.value(data,"host","host").asString());					//设备名称
		String contract_number = rda_str2links(Nest.value(data,"host","inventory","contract_number").asString());			//别名
		String hardware = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","hardware").asString(),IMonConsts.DEPT));	//所属部门
		String software = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","software").asString(),IMonConsts.MOTOR_ROOM));//所在机房
		String url_a = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","url_a").asString(),IMonConsts.CABINET));		//所在机柜
		String type_full = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","type_full").$(),IMonConsts.SYSTEM));		//操作系统
		String os_full = rda_str2links(Nest.value(data,"host","inventory","os_full").asString());		//编号
		String vendor = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","vendor").asString(),IMonConsts.FIRM));		//厂商
		String remark = rda_str2links(Nest.value(data, "host","inventory","host_networks").asString());	//备注
		
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostname);
		leftData.put("别名", contract_number);
		leftData.put("所属部门", hardware);
		leftData.put("所在机房", software);
		leftData.put("所在机柜", url_a);
		
		Map rightData = array();
		rightData.put("操作系统", type_full);
		rightData.put("编号", os_full);
		rightData.put("厂商", vendor);
		rightData.put("备注", remark);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}