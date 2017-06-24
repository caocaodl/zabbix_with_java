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

public class DB2DBDetailServiceImpl implements IHostDetailService {

	@Override
	public CTableInfo getTargetKey(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CTableInfo target_key = new CTableInfo();
		String hostid = Nest.value(data, "hostid").asString();
		
		CTenanView viewService = new CTenanView();
		
		CList logSizeImg = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_DB2_TOTALLOGSPUSED.getValue());
		CList applCountImg = viewService.create_Favorite_Graphs(idBean, executor, hostid,ItemsKey.DB_DB2_APPLCOUNT.getValue());
		
		CRow titleRowSecond = new CRow();
		titleRowSecond.addItem(_("DB_DB2_TOTALLOGSPUSED"));
		titleRowSecond.addItem(_("DB_DB2_APPLCOUNT"));
		CRow seconedRow = new CRow();
		seconedRow.addItem(logSizeImg);
		seconedRow.addItem(applCountImg);	
		
		target_key.addRow(titleRowSecond,"speed_center");
		target_key.addRow(seconedRow,"speed_center");
		
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
		String hostName = rda_str2links(Nest.value(data,"host","host").asString());	
		String sortTime = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_SORTTIME.getValue(), hostid, true, false);		
		String logSize 	= CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_TOTALLOGSPUSED.getValue(), hostid, true, false);		
		String logAvaSize = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_TOTALLOGSPAVAIL.getValue(), hostid, true, false);
		String bpHitRatio = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_BPHITRATIO.getValue(), hostid, false, false);
		
		String appCount = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_APPLCOUNT.getValue(), hostid, true, false);	
		String version 	= CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_VERSION.getValue(), hostid, false, false);		
		String aveWaitForLock = CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_AVEWAITFORLOCK.getValue(), hostid, true, false);
		String status= CommonUtils.getTargetLastValue(executor, idBean, ItemsKey.DB_DB2_ALIVE.getValue(), hostid, true, false);				

		Map leftData = CArray.map();
		leftData.put(_("the name Of Host"), hostName);
		leftData.put(_("DB_DB2_SORTTIME"), sortTime);
		leftData.put(_("DB_DB2_TOTALLOGSPUSED"), logSize);
		leftData.put(_("DB_DB2_TOTALLOGSPAVAIL"), logAvaSize);
		leftData.put(_("DB_DB2_BPHITRATIO"), bpHitRatio);
		
		Map rightData = CArray.map();
		rightData.put(_("DB_DB2_APPLCOUNT"), appCount);
		rightData.put(_("db_version"), version);
		rightData.put(_("DB_DB2_AVEWAITFORLOCK"), aveWaitForLock);
		rightData.put(_("db_status"), status);
		
		return CommonUtils.assembleDetailDataForTable(leftData, rightData);
	}
}
