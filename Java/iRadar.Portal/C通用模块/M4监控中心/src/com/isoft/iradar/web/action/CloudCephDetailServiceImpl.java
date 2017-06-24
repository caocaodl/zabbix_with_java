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
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.core.utils.EasyObject;
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
/**
 * Ceph设备类型详情页面
 * @author HP Pro2000MT
 *
 */
public class CloudCephDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		//关键指标数据
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		String datafile_reads = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DATAFILE_READS_ORACLE.getValue(), hostid, true, false);	//数据文件读取
		String datafile_writes = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DATAFILE_WRITES_ORACLE.getValue(), hostid, true, false);	//数据文件写入
		
		//实例化生成图片方法
		CTenanView viewService = new CTenanView();
		
		//显示关键指标的空间或者图片
		CList datafile_read_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DATAFILE_READS_ORACLE.getValue());				
		CList datafile_writes_img = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DATAFILE_WRITES_ORACLE.getValue());			
		
		//关键指标
		CRow titleRowFrist = new CRow();
		titleRowFrist.addItem("数据文件读取:"+datafile_reads);
		titleRowFrist.addItem("数据文件写入: "+datafile_writes);
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
		String dbphysicalread = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.DBPHYSICALREAD_ORACLE.getValue()).value().out().format();	//磁盘上读取数据块的数量
		String session_active = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.SESSION_ACTIVE_ORACLE.getValue()).value().out().format();	//会话数利用率
		String dicthitratio = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.BUFFHITRATIO_ORACLE.getValue()).value().out().format();		//缓冲池命中率
		
		String users = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.USERS_ORACLE.getValue()).value().out().format();						//当前会话数
		String listenerstatus = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MAXSESSION_ORACLE.getValue()).value().out().format();	//监听状态
		String pctused = LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.MAXSESSION_ORACLE.getValue()).value().out().format();				//最大会话量
		String version= LatestValueHelper.buildByNormalKey(EasyObject.asLong(hostid), ItemsKey.VERSION_ORACLE.getValue()).value().out().format();					//当前版本
		
		//赋值
		Map leftData = array();
		leftData.put("设备名称", hostNamm);
		leftData.put("磁盘上读取数据块的数量", dbphysicalread);
		leftData.put("会话数利用率", session_active);
		leftData.put("缓冲池命中率", dicthitratio);
		
		Map rightData = array();
		rightData.put("当前会话数", users);
		rightData.put("监听状态", listenerstatus);
		rightData.put("最大会话量", pctused);
		rightData.put("当前版本", version);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}