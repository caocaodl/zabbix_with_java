package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.action.moncenter.I_LatestDataAction;
import com.isoft.iradar.web.bean.Column;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 云服务简单详情页面显示
 * @author HP Pro2000MT
 *
 */
public class CloudSimpleDetailServiceImpl extends LinuxHostDetailServiceImpl {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		return super.getTargetKey(idBean, executor, data);
	}
	
	/**
	 * 健康度仪表盘模块
	 */
	@Override
	public CDiv getHealthFunsionCharts(String divID, String styleClass, Map data) {
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
		//获取数据
		String hostname = rda_str2links(Nest.value(data,"host","host").asString());					//设备名称
		String contract_number = rda_str2links(Nest.value(data,"host","inventory","contract_number").asString());			//别名
		String hardware = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","hardware").asString(),IMonConsts.DEPT));	//所属部门
		String software = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","software").asString(),IMonConsts.MOTOR_ROOM));//所在机房
		String url_a = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","url_a").asString(),IMonConsts.CABINET));		//所在机柜
		String os_full = rda_str2links(Nest.value(data,"host","inventory","os_full").asString());		//编号
		String vendor = rda_str2links(CommonUtils.returnSystemDLabel(Nest.value(data, "host","inventory","vendor").asString(),IMonConsts.FIRM));		//厂商
		String remark = rda_str2links(Nest.value(data, "host","inventory","host_networks").asString());	//备注
		String groupid = Nest.value(data, "groupid").asString();
		//赋值
		Map leftData = array();
		Map rightData = array();
		leftData.put("设备名称", hostname);
		leftData.put("资产别名", contract_number);
		leftData.put("所属部门", hardware);
		leftData.put("所在机房", software);
		leftData.put("所在机柜", url_a);
		if(IMonConsts.MON_CLOUD_WEB.toString().equals(groupid)){	//门户节点		
			String httpdNum = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CLOUD_WEB_PROCNUM_HTTPD.getValue()).value().out().format();
			rightData.put(_("httpd进程数"), httpdNum);
			rightData.put(_("the serial number of Host"), os_full);
			rightData.put(_("the manufacturer of Host"), vendor);
			rightData.put(_("the remark of Host"), remark);
		}else{
			rightData = getOverviewFormRightData(data, hostid);
		}
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
	
	protected CArray getOverviewFormRightData(Map data,String hostid) {
		CArray rightData = array();
		Long groupid = Nest.value(data, "groupid").asLong();
		for(CArray config: (CArray<CArray>)I_LatestDataAction.CONFIGS) {
			IMonGroup group = (IMonGroup)Nest.value(config, "group").$();
			if(groupid == group.id().longValue()) {
				CArray<Column> columns = (CArray)Nest.value(config, "columns").$();
				for(Column column: columns) {
					ItemsKey key = column.keys()[0].itemKey();
					Integer value = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), key).value().value().asInteger();
					rightData.put(key.getName(), value);
				}
			}
		}
		return rightData;
	}
}
