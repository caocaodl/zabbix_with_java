package com.isoft.iradar.web.action;

import static com.isoft.iradar.inc.FuncsUtil.rda_str2links;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.web.views.segments.CTenanView;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import static com.isoft.iradar.Cphp.*;

public class MongoDBDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo targetKey = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		CTenanView viewService = new CTenanView();
		
		CList residentMemImg = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_MONGODB_MEM_RESIDENT.getValue());
		CList virtualMemImg = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_MONGODB_MEM_VIRTUAL.getValue());
		CList networkBytesOutImg = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_MONGODB_NETWORK_BYTESOUT.getValue());
		CList networkBytesInImg = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_MONGODB_NETWORK_BYTESIN.getValue());
		
		CRow titleRowFirst = new CRow();
		titleRowFirst.addItem(_("DB_MONGODB_MEM_RESIDENT"));
		titleRowFirst.addItem(_("DB_MONGODB_MEM_VIRTUAL"));
		CRow imgRowFirst = new CRow();
		imgRowFirst.addItem(residentMemImg);
		imgRowFirst.addItem(virtualMemImg);	
		targetKey.addRow(titleRowFirst,"speed_center");
		targetKey.addRow(imgRowFirst,"speed_center");
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem(_("DB_MONGODB_NETWORK_BYTESOUT"));
		titleRowSecond.addItem(_("DB_MONGODB_NETWORK_BYTESIN"));
		CRow imgRowSecond = new CRow();
		imgRowSecond.addItem(networkBytesOutImg);
		imgRowSecond.addItem(networkBytesInImg);	
		targetKey.addRow(titleRowSecond,"speed_center");
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
		String hostName = rda_str2links(Nest.value(data,"host","host").asString());	
		String insertOps = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_OPCOUNTERS_INSERT.getValue(), hostid, false, false);		
		String deleteOps = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_OPCOUNTERS_DELETE.getValue(), hostid, false, false);		
		String updateOps = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_OPCOUNTERS_UPDATE.getValue(), hostid, false, false);
		String queryOps = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_OPCOUNTERS_QUERY.getValue(), hostid, false, false);
		
		String datasValue = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_MEM_MAPPED.getValue(), hostid, true, true);	
		String flushAveMs = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_BACKGROUNDFLUSHING_AVERAGE_MS.getValue(), hostid, true, true);		
		String indexCounResets = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_INDEXCOUNTERS_RESETS.getValue(), hostid, false, false);
		String getmoreOps = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_OPCOUNTERS_GETMORE.getValue(), hostid, false, false);				
		String flushesOps = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_MONGODB_BACKGROUNDFLUSHING_FLUSHES.getValue(), hostid, false, false);				
		
		Map leftData = CArray.map();
		leftData.put(_("the name Of Host"), hostName);
		leftData.put(_("DB_MONGODB_OPCOUNTERS_INSERT"), insertOps);
		leftData.put(_("DB_MONGODB_OPCOUNTERS_DELETE"), deleteOps);
		leftData.put(_("DB_MONGODB_OPCOUNTERS_UPDATE"), updateOps);
		leftData.put(_("DB_MONGODB_OPCOUNTERS_QUERY"), queryOps);
		
		Map rightData = CArray.map();
		rightData.put(_("DB_MONGODB_MEM_MAPPED"), datasValue);
		rightData.put(_("DB_MONGODB_BACKGROUNDFLUSHING_AVERAGE_MS"), flushAveMs);
		rightData.put(_("DB_MONGODB_INDEXCOUNTERS_RESETS"), indexCounResets);
		rightData.put(_("DB_MONGODB_OPCOUNTERS_GETMORE"), getmoreOps);
		rightData.put(_("DB_MONGODB_BACKGROUNDFLUSHING_FLUSHES"), flushesOps);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
