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

public class SqlServerDBDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		String transaction = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.TRANSACTION_DM.getValue(), hostid, true, false);		//活动事务
		String service_state = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.SERVICE_STATE_DM.getValue(), hostid, true, false);	//进程状态

		
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//显示关键指标的空间或者图片
		CList transaction_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.TRANSACTION_DM.getValue());
		CList service_state_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.SERVICE_STATE_DM.getValue());
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem("活动事务:"+transaction);
		titleRowSecond.addItem("进程状态: "+service_state);
		CRow seconedRow = new CRow();
		seconedRow.addItem(transaction_img);
		seconedRow.addItem(service_state_img);	
		
		target_key.addRow(titleRowSecond,"speed_center");
		target_key.addRow(seconedRow,"speed_center");
		
		return target_key;
	}
	
	/**
	 * 健康度仪表盘模块（柱状图）
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
		//根据键值获取相关数据
		String hostNamm = rda_str2links(Nest.value(data,"host","host").asString());	//设备名称
		String uptime = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.UPTIME_DM.getValue(), hostid, true, false);		//正常运行时间
		String status = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.STATUS_DM.getValue(), hostid, true, true);		//数据库实例状态
		String table_usage = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.TABLE_USAGE_DM.getValue(), hostid, true, true);//表空间使用率
		
		String temp_usage = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.TEMP_USAGE_DM.getValue(), hostid, true, false);	//临时表空间使用率
		String sessions = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.SESSIONS_DM.getValue(), hostid, true, true);		//当前会话数
		String active_session = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.ACTIVE_SESSION_DM.getValue(), hostid, true, true);//活跃会话数
		String version= CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.VERSION_DM.getValue(), hostid, true, false);				//当前版本
		//赋值
		Map leftData = array();
		leftData.put("服务器名称", hostNamm);
		leftData.put("正常运行时间", uptime);
		leftData.put("数据库实例状态", status);
		leftData.put("表空间使用率", table_usage);
		
		Map rightData = array();
		rightData.put("临时表空间使用率", temp_usage);
		rightData.put("当前会话数", sessions);
		rightData.put("活跃会话数", active_session);
		rightData.put("当前版本", version);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
