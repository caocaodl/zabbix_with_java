package com.isoft.iradar.web.action;

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
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.Mapper.Nest;
/**
 * Oracle数据库设备类型详情页面显示内容
 * @author HP Pro2000MT
 *
 */
public class OracleDBDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		//显示关键指标的空间或者图片
		CList datafile_read_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DATAFILE_READS_ORACLE.getValue());				
		CList datafile_writes_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DATAFILE_WRITES_ORACLE.getValue());			
		
		//关键指标
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem("数据文件读取 ");
		titleRowFrist.addItem("数据文件写入  ");
		CRow firstRow = new CRow();
		firstRow.addItem(datafile_read_img);
		firstRow.addItem(datafile_writes_img);
		
		target_key.addRow(titleRowFrist,"speed_center");
		target_key.addRow(firstRow,"speed_center");
		
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
		String dicthitratio = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.BUFFHITRATIO_ORACLE.getValue(), hostid, true, true);		//缓冲池命中率
		
		String users = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.USERS_ORACLE.getValue(), hostid, true, true);						//当前会话数
		String system_users = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.SESSION_SYSTEM_ORACLE.getValue(), hostid, true, true);				//系统会话数
		String version= LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.VERSION_ORACLE.getValue()).value().out().print();	//当前版本
		String uptime_oracle = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.UPTIME_ORACLE.getValue()).value().out().format();//运行时间
		String alive_oracle = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.ALIVE_ORACLE.getValue()).value().out().format();//运行状态
		String tableRate = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.TBL_USE_RATE_ORACLE.getValue()).value().out().print();//表空间利用率
		String alive_session = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.SESSION_ACTIVE_ORACLE.getValue()).value().out().format();//运行状态
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostNamm);
		leftData.put("表空间利用率", tableRate);
		leftData.put("系统会话个数", system_users);
		leftData.put("缓冲池命中率", dicthitratio);
		
		Map rightData = array();
		rightData.put("当前会话数", users);
		rightData.put("运行时间",uptime_oracle);
		rightData.put("运行状态",alive_oracle);
		rightData.put("活动会话数",alive_session);
		
		CFormList baseInfo = CommonUtils.assembleDetailDataForTable(leftData, rightData,true);
		CFormList versionInfo = new CFormList();
		CSpan versionSpan = new CSpan(version);
		
		versionSpan.setTitle(version);
		versionInfo.addRow("数据库版本　",versionSpan);
		
		form.addRow(baseInfo);
		form.addRow(versionInfo);
		return form;
	}
}