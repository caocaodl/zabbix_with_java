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
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;
/**
 * 云主机类型设备详情页面
 * @author HP Pro2000MT
 *
 */
public class VMHostDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		String osType_vm = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid, false, false);//操作系统类型
		//CPU、内存使用率
		CRow titleRow = new CRow();
		titleRow.addItem(_("the cpuUsage of host"));
		titleRow.addItem(_("the memoryUsage of host"));
		target_key.addRow(titleRow,"speed_center");

		CRow dataRow = new CRow();
		if(osType_vm != null && osType_vm.contains("Windows")){
			dataRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_RATE_VM_WINDOWS.getValue()));			
		}else{
			dataRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CPU_RATE_VM_LINUX.getValue()));
		}
		dataRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MEMORY_RATE_VM.getValue()));
		target_key.addRow(dataRow,"speed_center");
		
		return target_key;
	}
	
	/**
	 * 健康度仪表盘模块（仪表盘）
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
		//根据键值获取相关数据
		String hostName = rda_str2links(Nest.value(data,"host","name").asString());
		String total_memory = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.TOTAL_MEMORY_VM.getValue(), hostid, true, true);	//内存总大小
		String mem_available_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.AVAILABEL_MEMORY_VM.getValue(), hostid, true, true);	//可用内存
		String status_vm = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.STATUS_VM.getValue(), hostid, true, true);//云主机状态
		String systemUptime = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.UPTIME_VM.getValue()).value().out().format();//系统运行时长
		
		String osType_vm = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.OSTYPE_VM_LINUX,ItemsKey.OSTYPE_VM_WINDOWS), hostid, false, false);//操作系统类型
		String cpuInfo_vm = CommonUtils.getTargetLastValue(executor, idBean, array(ItemsKey.CPU_MODEL_VM_LINUX, ItemsKey.CPU_MODEL_VM_WINDOWS), hostid, false, false);//CPU信息
		
		String CPUNUM = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CPUS_VM.getValue()).value().out().format();//虚拟CPU个数
		String disk = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.DISK_VM.getValue()).value().out().format();//磁盘容量
		String host = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.HOSTS_VM.getValue()).value().out().format();//所属计算节点
		String tenant = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.TENANT_VM.getValue()).value().out().format();//所属租户
		String user = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.USER_VM.getValue()).value().out().format();//所属用户
		
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
		leftData.put(_("cpunum_vm"),CPUNUM);
		leftData.put(_("disk_vm"),disk);
		leftData.put(_("host_vm"),host);
		leftData.put(_("the tenant Of Vm"),tenant);
//		leftData.put(_("the cpuInfo of host"),cpuInfo_vm);
		
		Map rightData = array();
		rightData.put(_("the totalMem Of Host"), total_memory);
		rightData.put(_("the availableMem Of Host"), mem_available_vm);
		rightData.put(_("the status Of VM"), status_vm);
		rightData.put(_("the uptime Of Host"), systemUptime);
		rightData.put(_("the user Of Vm"),user);
//		rightData.put(_("the osType Of VM"),osType_vm);
		
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
}
