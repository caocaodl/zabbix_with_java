package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.iradar.common.util.LatestValueHelper.Matcher;

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
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
/**
 * 集群设备类型详情页面
 * @author HP Pro2000MT
 *
 */
public class ClusterDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo target_key = new CTableInfo();
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
		String hostNamm = rda_str2links(Nest.value(data,"host","host").asString());	//设备名称

		Matcher matcher = new Matcher(){
			@Override public boolean match(Object o) {
				return "0".equals(String.valueOf(o));
			}
		};
		String dcName = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.DCNAME_CLUSTER.getValue()).values().count(matcher).out().print();
		String dcEvents = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.DCEVENTS_CLUSTER.getValue()).values().sum().out().print();
		
		String clusterDcName = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CLUSTERDCNAME_CLUSTER.getValue()).values().sum().out().print();
		String clusterMaxMemRequest = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CLUSTERMAXMEMREQUEST_CLUSTER.getValue()).values().avg().out().print();
		
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostNamm);
		leftData.put("数据中心数量", dcName);
		leftData.put("事件数量", dcEvents);
		
		Map rightData = array();
		rightData.put("集群个数", clusterDcName);
		rightData.put("最大内存数", clusterMaxMemRequest);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
