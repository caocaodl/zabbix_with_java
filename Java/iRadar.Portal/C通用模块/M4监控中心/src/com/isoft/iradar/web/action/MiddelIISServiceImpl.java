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
public class MiddelIISServiceImpl implements IHostDetailService{

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo targetKey = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();

		CTenanView viewService = new CTenanView();

		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(ItemsKey.MID_IIS_ASPNET_APPLICATIONS_TOTAL_REQUESTS_SEC.getName());
		titleRowFrist.addItem(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_GET_REQUESTS.getName());
		targetKey.addRow(titleRowFrist,"speed_center");
		CRow imgRowFirst = new CRow();
		imgRowFirst.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_IIS_ASPNET_APPLICATIONS_TOTAL_REQUESTS_SEC.getValue()));
		imgRowFirst.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_GET_REQUESTS.getValue()));
		targetKey.addRow(imgRowFirst,"speed_center");
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_HEAD_REQUESTS.getName());
		titleRowSecond.addItem(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_POST_REQUESTS.getName());
		targetKey.addRow(titleRowSecond,"speed_center");
		CRow imgRowSecond = new CRow();
		imgRowSecond.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_HEAD_REQUESTS.getValue()));
		imgRowSecond.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_POST_REQUESTS.getValue()));
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
		String aspNetAppRes = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_ASPNET_APPLICATION_RESTARTS.getValue()).value().out().format();
		String astNetWorkProRes = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_ASPNET_WORKER_PROCESS_RESTARTS.getValue()).value().out().format();
		String webSerTotalCurCon = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_WEBSERVICE_TOTAL_CURRENT_CONNECTIONS.getValue()).value().out().print();
		String webSerTotalGetReq = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_GET_REQUESTS.getValue()).value().out().print();
		String webSerTotalHeadReq = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_HEAD_REQUESTS.getValue()).value().out().print();
		String webSerTotalPostReq = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_POST_REQUESTS.getValue()).value().out().print();
		String w3vc = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MID_IIS_W3SVC.getValue()).value().out().format();

		Map leftData = CArray.map();
		leftData.put(_("the name Of Host"), hostNamm);
		leftData.put(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_CURRENT_CONNECTIONS.getName(), webSerTotalCurCon);
		leftData.put(ItemsKey.MID_IIS_ASPNET_APPLICATION_RESTARTS.getName(), aspNetAppRes);
		leftData.put(ItemsKey.MID_IIS_ASPNET_WORKER_PROCESS_RESTARTS.getName(), astNetWorkProRes);
		
		Map rightData = CArray.map();
		rightData.put(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_GET_REQUESTS.getName(), webSerTotalGetReq);
		rightData.put(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_HEAD_REQUESTS.getName(), webSerTotalHeadReq);
		rightData.put(ItemsKey.MID_IIS_WEBSERVICE_TOTAL_TOTAL_POST_REQUESTS.getName(), webSerTotalPostReq);
		rightData.put(ItemsKey.MID_IIS_W3SVC.getName(), w3vc);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
