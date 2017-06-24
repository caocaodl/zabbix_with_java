package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;
/**
 * MySQL数据库设备类型详情页面显示内容
 * @author HP Pro2000MT
 *
 */
public class MySqlDBDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//显示关键指标的空间或者图片
//		CList mysql_uptime_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.LAST_START_MYSQL.getValue());				//正常运行时间
		CList mysql_queries_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.QUERIES_PER_MYSQL.getValue());	
		CList free_memory_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.FREE_MEMORY_MYSQL.getValue());		//缓存空闲内存
		CList threads_connected_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.THREADS_CONNECTED_MYSQL.getValue());	//当前连接数
		CList memoryrate = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.THREADS_RUNNING_MYSQL.getValue());		//线程数量
		
		//关键指标
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem(ItemsKey.QUERIES_PER_MYSQL.getName());
		titleRowFrist.addItem("缓存空闲内存 ");
		CRow firstRow = new CRow();
		firstRow.addItem(mysql_queries_img);
		firstRow.addItem(free_memory_img);
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem("系统会话个数 ");
		titleRowSecond.addItem("并发线程数 ");
		CRow secondRow = new CRow();
		secondRow.addItem(threads_connected_img);
		secondRow.addItem(memoryrate);
		
		target_key.addRow(titleRowFrist,"speed_center");
		target_key.addRow(firstRow,"speed_center");
		target_key.addRow(titleRowSecond,"speed_center");
		target_key.addRow(secondRow,"speed_center");
		
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
		CFormList form = new CFormList();
		form.addClass("vmCommonClass");
		//根据键值获取相关数据
		String hostNamm = rda_str2links(Nest.value(data,"host","host").asString());	//设备名称
		String threads_running = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.THREADS_RUNNING_MYSQL.getValue(), hostid, true, false);	//线程数量
		String last_start = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.LAST_START_MYSQL.getValue()).value().out().format();//运行时间
		String qps = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.QUERIES_PER_MYSQL.getValue()).value().out().format();//总连接数
		String memory_rate = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.TOTAL_BLOCKS_MYSQL.getValue()).value().out().format();//系统会话个数
		String dbversion = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DBVERSION_MYSQL.getValue(), hostid, true, false);		//当前版本		
		String status = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.IS_ALIVE_MYSQL.getValue()).value().out().format();//运行状态
		String databases = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DATABASES_MYSQL.getValue(), hostid, false, false);
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostNamm);
		leftData.put("并发线程数", threads_running);
		leftData.put("查询量/秒", qps);
		leftData.put("缓存总量", memory_rate);
		
		Map rightData = array();
		rightData.put("运行状态", status);
		rightData.put("当前版本", dbversion);
		rightData.put("运行时间", last_start);
		
		CFormList cpuInfo = new CFormList();
		CFormList baseInfo = CommonUtils.assembleDetailDataForTable(leftData, rightData,true);
		CSpan databases_s = new CSpan(databases);
		
		databases_s.setTitle(databases);
		cpuInfo.addRow("表空间列表",databases_s);
		
		form.addRow(baseInfo);
		form.addRow(cpuInfo);
		return form;
	}
}
