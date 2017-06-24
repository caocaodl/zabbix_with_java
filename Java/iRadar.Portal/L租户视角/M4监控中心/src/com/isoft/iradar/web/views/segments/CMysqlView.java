package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.common.util.IMonConsts.APP_NAME_MYSQL;
import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.iradar.web.Util.TvmUtil.removeDefaultProfix;
import static com.isoft.types.CArray.array;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.TeventUtil;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 设备详情视图页面
 * 
 * @author HP Pro2000MT
 * 
 */
public class CMysqlView extends CViewSegment {

	/**
	 * 设备详情页面布局
	 */
	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget hostInventoryWidget = new CWidget(null, "inventory-host");
		String hostid = Nest.value(data, "hostid").asString();
		Long applicationid = Nest.value(data, "applicationid").asLong();
		List<String> strlist=new ArrayList<String>();
		CItemGet itemGet = new CItemGet();
		itemGet.setApplicationIds(applicationid);
		itemGet.setOutput(new String[] {"itemid", "name", "state","key_"});
		itemGet.setEditable(true);
		itemGet.setSortfield("itemid");
		CArray<Map> items = API.Item(idBean, executor).get(itemGet);
		int i=0;
		for(Map item : items){
			 String itemid = Nest.value(item, "itemid").asString();
			 Nest.value(data, "key"+i).$(Nest.value(item, "key_").asString());
			 strlist.add(create_Favorite_Graphs_bystr(idBean, executor,itemid));
			 i++;
		 }
		
		CTableInfo table = new CTableInfo(); // 创建要显示的table
		CTableInfo hostDetail_table = new CTableInfo(); // 设备详情表格
		hostDetail_table.setAttribute("class", hostDetail_table.getAttribute("class") + " tbClass");
		// 设备详情数据
		CFormList overviewFormList = new CFormList();
		overviewFormList = getOverviewForm(data, overviewFormList,idBean,executor,hostid);

		CRow hostDetailHeader = new CRow();
		CDiv divHead = new CDiv(_("Host detail"), "details");
		hostDetailHeader.addItem(divHead);
		hostDetailHeader.addItem(new CDiv("事件每日统计", "health"));
		hostDetail_table.addRow(hostDetailHeader);
		// 设备详情内容
		CRow hostDetailMain = new CRow();
		hostDetailMain.addItem(new CDiv(overviewFormList, "leftClass equipment_details"));

		// 健康度模拟图片(仪表盘)
		TeventUtil ev=new TeventUtil();
		List<Map> eventmap=ev.statisticsEventNumByday(idBean, executor, applicationid);
		String divid = "trend_chart";
		CDiv div = new CDiv();
		div.setAttribute("id", divid);
		div.setAttribute("class", "equipment_details");
		CJSScript _fusionScript = new CJSScript(get_js("angulargaugeForHostDetail('" + divid + "','" + JSONArray.fromObject(eventmap) +"')"));
		div.addItem(_fusionScript);
		hostDetailMain.addItem(div);
		
		hostDetail_table.addRow(hostDetailMain, "rightClass first_td");

		// 关键指标数据
		CTableInfo target_key = new CTableInfo();
		
		CArray<Map> templateids = new CArray<Map>();
		templateids.put("templateid", Nest.value(data, "templateid").asString());
		CTenanView viewService = new CTenanView();//实例化生成图片方法
		
		CRow titleRowFirst = new CRow();
		titleRowFirst.addItem("运行时间");
		titleRowFirst.addItem("查询缓存可用量");
		CRow graphsRowFirst = new CRow();
		//graphsRowFirst.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid, strlist.get(5)));
		//graphsRowFirst.addItem(viewService.create_Favorite_Graphs(idBean, executor, hostid, strlist.get(3)));
		graphsRowFirst.addItem(create_Favorite_Graphs_list(strlist.get(4)));
		graphsRowFirst.addItem(create_Favorite_Graphs_list(strlist.get(3)));
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem( "系统会话数 ");
		titleRowSecond.addItem("每秒查询量");
		CRow graphsRowSecond = new CRow();
		graphsRowSecond.addItem(create_Favorite_Graphs_list(strlist.get(1)));
		graphsRowSecond.addItem(create_Favorite_Graphs_list(strlist.get(0)));
		
		target_key.addRow(titleRowFirst,"speed_center");
		target_key.addRow(graphsRowFirst,"speed_center");
		target_key.addRow(titleRowSecond,"speed_center");
		target_key.addRow(graphsRowSecond,"speed_center");

		// 实现div分割区域
		CDiv splitDiv = new CDiv();
		splitDiv.addStyle("height:25px;");

		// 最近告警列表数据
		CTable trigger_table = (CTable) Nest.value(data, "triggerForm").$();
		table.addRow(hostDetail_table); // 添加设备详情(包含健康度)

		table.addRow(splitDiv); // 添加分割区域

		table.addRow(new CDiv(_("Target Key"), "norm"));
		table.addRow(target_key); // 添加关键指标

		table.addRow(splitDiv); // 添加分割区域

		table.addRow(new CDiv(_("recentlyEvents"), "alarm"));
		table.addRow(trigger_table); // 添加告警列表

		// 页面加载table
		hostInventoryWidget.addItem(table);
		return hostInventoryWidget;
	}

	
	public  String create_Favorite_Graphs_bystr(IIdentityBean idBean, SQLExecutor executor,String itemid) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(date.getTime());
		String timeG = mDateTime.substring(0, 16);
		timeG = timeG.replace("-", "");
		timeG = timeG.replace(":", "");
		timeG = timeG.replace(" ", "");
		timeG = timeG + "00";
		int time = 60*60*24;
		if(itemid == null || itemid.equals("")){
			itemid = "00000";
		}
		String src = "chart.action?itemid=" + itemid + "&period=" + time + "&stime=" + timeG + "&updateProfile=0&width=550";
		return src;
	}
	
	public  CList create_Favorite_Graphs_list(String graphssrc) {
		CList favList = new CList(null, "firstshape", _("No graphs added."));
		CImg img = new CImg(graphssrc, "");
		CDiv d = new CDiv();
		d.addItem(img);
		favList.addItem(d, "");
		return favList;
	}
	
	/**
	 * 组装要显示的设备详情信息
	 * 
	 * @param data
	 *            数据源
	 * @param overviewFormList
	 *            要返回的表单
	 * @return
	 */
	public CFormList getOverviewForm(Map data, CFormList overviewFormList,IIdentityBean idBean, SQLExecutor executor,String hostid) {
		//根据键值获取相关数据
		String appNamm = rda_str2links(Nest.value(data,"application","0","name").asString());	//设备名称
		String elapsedtime = CommonUtils.getTargetLastValue(executor, idBean, Nest.value(data, "key4").asString(),hostid,false,false);//运行时间
		if("--".equals(elapsedtime)){
        	elapsedtime = "0";
        }
		//赋值
		String app_name = removeDefaultProfix(appNamm,APP_NAME_MYSQL);
		
		Map leftData = array();
		leftData.put("设备名称", app_name);
		leftData.put("运行时间(小时)",  Cphp.round(Float.valueOf(elapsedtime)/3600,2));  
		
		String hostname =Nest.value(data, "application","0","hosts","0","name").asString();
		String threads = CommonUtils.getTargetLastValue(executor, idBean, Nest.value(data, "key5").asString(), hostid, true, false);		//当前版本
		Map rightData = array();
		rightData.put("所属云主机", hostname);
		rightData.put("并发线程数", threads);
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
	
}
