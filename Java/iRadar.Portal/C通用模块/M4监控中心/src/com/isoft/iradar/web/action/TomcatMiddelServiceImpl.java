package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

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
import com.isoft.types.Mapper.Nest;
/**
 * 中间件Tomcat设备类型详情页面
 * @author HP Pro2000MT
 *
 */
public class TomcatMiddelServiceImpl implements IHostDetailService{

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();

		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		//当前线程、最大线程数
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(_("currentthreadcount"));
		titleRowFrist.addItem(_("maxthreads"));
		target_key.addRow(titleRowFrist,"speed_center");
		
		CRow firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.CURRENTTHREADCOUNT_TOMCAT.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MAXTHREADS_TOMCAT.getValue()));
		target_key.addRow(firstRow,"speed_center");
		
		//活动会话、总会话数
		titleRowFrist = new CRow();
		titleRowFrist.addItem(_("activesessions_tomcat"));
		titleRowFrist.addItem(_("totalsession_tomcat"));
		target_key.addRow(titleRowFrist,"speed_center");
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.ACTIVESESSIONS_TOMCAT.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.TOTALSESSION_TOMCAT.getValue()));
		target_key.addRow(firstRow,"speed_center");
		
		//堆当前使用量、最大使用量
		titleRowFrist = new CRow();
		titleRowFrist.addItem(_("heapmemoryusageused"));
		titleRowFrist.addItem(_("heapmemoryusagemax"));
		target_key.addRow(titleRowFrist,"speed_center");
		firstRow = new CRow();
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.HEAPMEMORYUSAGEUSED.getValue()));
		firstRow.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.HEAPMEMORYUSAGEMAX.getValue()));
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
		
		String currentThreadCount = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CURRENTTHREADCOUNT_TOMCAT.getValue()).value().out().format();
		String activeSessions = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.ACTIVESESSIONS_TOMCAT.getValue()).value().out().format();
		String currentThreadsBusy = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CURRENTTHREADSBUSY.getValue()).value().out().format();
		String errorCount = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.ERRORCOUNT_TOMCAT.getValue()).value().out().format();
		String maxThreads = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MAXTHREADS_TOMCAT.getValue()).value().out().format();
		String sessionCounter = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.TOTALSESSION_TOMCAT.getValue()).value().out().format();
		String uptime = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.UPTIME_TOMCAT.getValue()).value().out().format();
		//赋值
		Map leftData = array();
		leftData.put(_("the name Of Host"), hostNamm);
		leftData.put(_("currentthreadcount"), currentThreadCount);
		leftData.put(_("activesessions_tomcat"), activeSessions);
		leftData.put(_("currentthreadsbusy"), currentThreadsBusy);
		
		Map rightData = array();
		rightData.put(_("errorcount_tomcat"), errorCount);
		rightData.put(_("maxthreads"), maxThreads);
		rightData.put(_("totalsession_tomcat"), sessionCounter);
		rightData.put(_("Runing Time"), uptime);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
