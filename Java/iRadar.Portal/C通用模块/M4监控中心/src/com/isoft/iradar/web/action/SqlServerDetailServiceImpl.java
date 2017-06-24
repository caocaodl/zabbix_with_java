package com.isoft.iradar.web.action;

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
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;

public class SqlServerDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//显示关键指标的空间或者图片
		CList cache_hit = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_SQLSERVER_CACHEHIT.getValue());		//缓存命中率
		CList io_pending = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_SQLSERVER_IOPENDING.getValue());		//IO挂起数目
		
		CList page_read = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_SQLSERVER_PAGEREADS.getValue());		//页读出
		CList page_write = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_SQLSERVER_PAGEWRITES.getValue());		//页写入
		
		//关键指标
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem("缓存命中率  ");
		titleRowFrist.addItem("IO挂起数   ");
		CRow firstRow = new CRow();
		firstRow.addItem(cache_hit);
		firstRow.addItem(io_pending);
		
		CRow titleRowThrid = new CRow();
		titleRowThrid.addItem("页读出速率");
		titleRowThrid.addItem("页写入速率");
		CRow thridRow = new CRow();
		thridRow.addItem(page_read);
		thridRow.addItem(page_write);
		
		target_key.addRow(titleRowFrist,"speed_center");
		target_key.addRow(firstRow,"speed_center");
		target_key.addRow(titleRowThrid,"speed_center");
		target_key.addRow(thridRow,"speed_center");
		
		return target_key;
	}
	
	/**
	 * 健康度仪表盘模块（仪表盘）
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
		//获取数据
		String hostname = rda_str2links(Nest.value(data,"host","host").asString());																			//设备名称
		String dbSize = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_SQLSERVER_DBSIZE.getValue(), hostid, true, false);						//数据库大小
		String logSize = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_SQLSERVER_LOGSIZE.getValue(), hostid, true, true);					//日志大小
		String logUsedSize = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_SQLSERVER_LOGUSEDSIZE.getValue(), hostid, true, true);			//日志占用空间大小
		
		String log_used_percent = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.LOG_USED_PERCENT_PAGEWRITES.getValue()).value().out().format();//日志空间利用率
		String cpuRate = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.CPURATE_PAGEWRITES.getValue()).value().out().format();//日志空间利用率
		String pagereads = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.PAGEREADS_PAGEWRITES.getValue()).value().out().format();//读取IO速率
		String pagewrites = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.PAGEWRITES_PAGEWRITES.getValue()).value().out().format();//写入IO速率
		String status = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.IS_ALIVE_PAGEWRITES.getValue()).value().out().format();//运行状态
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostname);
		leftData.put("读取IO速率", pagereads);
		leftData.put("写入IO速率", pagewrites);
		leftData.put("日志空间利用率", log_used_percent);
		leftData.put("CPU使用率", cpuRate);
		
		Map rightData = array();
		rightData.put("数据库大小", dbSize);
		rightData.put("日志文件大小", logSize);
		rightData.put("日志占用空间大小", logUsedSize);
		rightData.put("数据库的运行状态", status);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
