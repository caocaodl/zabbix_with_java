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
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 中间件Tomcat设备类型详情页面
 * @author HP Pro2000MT
 *
 */
public class MiddelWebLogicServiceImpl implements IHostDetailService{

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo targetKey = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();

		CTenanView viewService = new CTenanView();

		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(ItemsKey.MID_WEBLOGIC_JMSCONNECTIONSCURRENTCOUNT.getName());
		titleRowFrist.addItem(ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEUSED.getName());
		targetKey.addRow(titleRowFrist,"speed_center");
		CRow imgRowFirst = new CRow();
		imgRowFirst.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_WEBLOGIC_JMSCONNECTIONSCURRENTCOUNT.getValue()));
		imgRowFirst.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEUSED.getValue()));
		targetKey.addRow(imgRowFirst,"speed_center");
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem(ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEUSED.getName());
		titleRowSecond.addItem(ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED.getName());
		targetKey.addRow(titleRowSecond,"speed_center");
		CRow imgRowSecond = new CRow();
		imgRowSecond.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEUSED.getValue()));
		imgRowSecond.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED.getValue()));
		targetKey.addRow(imgRowSecond,"speed_center");
		
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
		String hostNamm = rda_str2links(Nest.value(data,"host","host").asString());	//设备名称
		String startTime = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_STARTTIME.getValue()).value().out().format();
		String heapMemUsed = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEUSED.getValue()).value().out().format();
		String heapMemMaxs = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEMAX.getValue()).value().out().format();
		String psPemGenUsed = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEUSED.getValue()).value().out().format();
		String psPemGenMax = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEMAX.getValue()).value().out().format();
		String nonHeapMemUsed = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED.getValue()).value().out().format();
		String nonHeapMemMax = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEMAX.getValue()).value().out().format();

		Map leftData = CArray.map();
		leftData.put(_("the name Of Host"), hostNamm);
		leftData.put(ItemsKey.MID_WEBLOGIC_STARTTIME.getName(), startTime.length()>20?"--":startTime);
		leftData.put(ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEUSED.getName(), heapMemUsed);
		leftData.put(ItemsKey.MID_WEBLOGIC_HEAPMEMORYUSAGEMAX.getName(), heapMemMaxs);
		
		Map rightData = CArray.map();
		rightData.put(ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEUSED.getName(), nonHeapMemUsed);
		rightData.put(ItemsKey.MID_WEBLOGIC_NONHEAPMEMORYUSAGEMAX.getName(), nonHeapMemMax);
		rightData.put(ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEUSED.getName(), psPemGenUsed);
		rightData.put(ItemsKey.MID_WEBLOGIC_PSPERMGENUSAGEMAX.getName(), psPemGenMax);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
