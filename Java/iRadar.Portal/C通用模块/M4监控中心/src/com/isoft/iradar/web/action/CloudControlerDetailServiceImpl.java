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
import com.isoft.iradar.inc.JsUtil;
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
 * 云服务设备类型详情页面
 * @author HP Pro2000MT
 *
 */
public class CloudControlerDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//显示关键指标的空间或者图片
		CList vm_tenant_count_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.VM_TENANT_COUNT.getValue());	//租户个数
		CList vm_alert_count_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.VM_ALERT_COUNT.getValue());		//告警个数
		CList vm_hypervior_count_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.VM_HYPERVIOR_COUNT.getValue());//宿主机个数
		CList vm_image_count_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.VM_IMAGE_COUNT.getValue());			//镜像总个数
		
		
		//关键指标
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem("租户个数");
		titleRowFrist.addItem("云服务告警个数");
		CRow firstRow = new CRow();
		firstRow.addItem(vm_tenant_count_img);
		firstRow.addItem(vm_alert_count_img);
		
		CRow titleRowSeconed = new CRow();
		titleRowSeconed.addItem("宿主机个数");
		titleRowSeconed.addItem("镜像总个数");
		CRow seconedRow = new CRow();
		seconedRow.addItem(vm_hypervior_count_img);
		seconedRow.addItem(vm_image_count_img);
		
		
		target_key.addRow(titleRowFrist,"speed_center");
		target_key.addRow(firstRow,"speed_center");
		
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
		//根据键值获取相关数据
		String hostNamm = rda_str2links(Nest.value(data,"host","host").asString());	//设备名称
		String vm_core_used = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VM_CORE_USED.getValue(), hostid, true, true);	//虚拟内核已使用
		String vm_count_error = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VM_COUNT_ERROR.getValue(), hostid, true, true);//实例个数-故障
		String vm_vol_used = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VM_VOL_USED.getValue(), hostid, true, true);//卷存储已使用
		
		String vm_core_total = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VM_CORE_TOTAL.getValue(), hostid, true, true);	//虚拟内核总量
		String vm_count_active = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VM_COUNT_ACTIVE.getValue(), hostid, true, true);//实例个数-活动
		String vm_count_stop = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VM_COUNT_STOP.getValue(), hostid, true, true);	//实例个数-停止
		
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostNamm);
		leftData.put("虚拟内核已使用 ", vm_core_used);
		leftData.put("故障的实例个数 ", vm_count_error);
		leftData.put("卷存储已使用", vm_vol_used);
		
		Map rightData = array();
		rightData.put("虚拟内核总量", vm_core_total);
		rightData.put("活动的实例个数", vm_count_active);
		rightData.put("停止的实例个数", vm_count_stop);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}