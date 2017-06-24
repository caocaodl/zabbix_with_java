package com.isoft.web.bean.reportForms;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JSON;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.isoft.biz.daoimpl.reportForms.ReportFormsDAO;
import com.isoft.biz.web.bean.common.SystemWordbook;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.tags.AjaxResponse;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.iradar.web.views.CView;
import com.isoft.iradar.web.views.segments.CConfigurationReportFormList;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * 资产报表
 * @author BT
 *
 */
public class ReportFormInverntoriesAction extends RadarBaseAction{
	private CArray csvRows = null;
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if(isset(_REQUEST, "refresh")){
			boolean isInventoryHostType = Nest.value(_REQUEST, "inventHostTypeRefresh").asBoolean();
			AjaxResponse ajaxResponse = new AjaxResponse();
			CArray<Map> arrayMap = new CArray<Map>();
			SystemWordbook Syswb  =new SystemWordbook();
			Map<String, String> paramMap = new HashMap<String, String>();
			CConfigurationReportFormList cr = new CConfigurationReportFormList();
			paramMap.put("type", "FIRM");
			List<Map> manufacturers = Syswb.doAll(paramMap);
			CArray dataArray = null;
			Integer hostTotalNum = 0;
			Integer netTotalNum = 0;
			Integer storgeTotalNum = 0;
			Map names = new LinkedMap();
			Map values = new LinkedMap();
			for(int i=0,length=manufacturers.size();i<length;i++){//遍历所有的厂商
				Map manufacturer = manufacturers.get(i);
				dataArray = cr.countForHostGroups(getIdentityBean(),executor,manufacturer.get("dlabel"),manufacturer.get("dkey"),true);
				if(!isInventoryHostType){
					hostTotalNum += Integer.parseInt(dataArray.get(1).toString());
					netTotalNum += Integer.parseInt(dataArray.get(2).toString());
					storgeTotalNum += Integer.parseInt(dataArray.get(3).toString());
				}else{
					int totalNum = Integer.parseInt(dataArray.get(1).toString())+Integer.parseInt(dataArray.get(2).toString())+Integer.parseInt(dataArray.get(3).toString());
					if(totalNum!=0){
						Map valueMap = new LinkedMap();
						valueMap.put("name", manufacturer.get("dlabel"));
						valueMap.put("values",totalNum);
						names.put(i, manufacturer.get("dlabel"));
						values.put(i, valueMap);
					}
				}
			}
			
			if(!isInventoryHostType){
				 names.put(0, _("SERVER"));
				 names.put(1, _("Net Hosts"));
				 names.put(2, _("Stroge Hosts"));
				 
				 Map hostmap = new HashMap();
				 hostmap.put("name", _("SERVER"));
				 hostmap.put("values",hostTotalNum);
				 
				 Map netmap = new HashMap();
				 netmap.put("name", _("Net Hosts"));
				 netmap.put("values",netTotalNum);
				 
				 Map storgemap = new HashMap();
				 storgemap.put("name", _("Stroge Hosts"));
				 storgemap.put("values",storgeTotalNum);
				 
				 values.put(0, hostmap);
				 values.put(1, netmap);
				 values.put(2, storgemap);
			}
			
			Map namesMap = new HashMap();
			namesMap.put("names", names);
			arrayMap.add(namesMap);
			Map valuessMap = new HashMap();
			valuessMap.put("values", values);
			arrayMap.add(valuessMap);
		 
			ajaxResponse.success(arrayMap);
			ajaxResponse.send();
			return true;
		}
		return false;
	}

	@Override
	protected void doCheckFields(SQLExecutor arg0) {
			
	}
	private String action;
	private String actionName;
	
	@Override
	protected void doInitPage() {
		ActionMapping actionMapping = ServletActionContext.getActionMapping();
		this.action = actionMapping.getName();
		actionName = action+"."+actionMapping.getExtension();
		page("title", _(""));
		page("file", "intvoisport.action");
		page("type", "ajax".equals(getParameter("output"))? Defines.PAGE_TYPE_JSON: Defines.PAGE_TYPE_HTML);
		page("js", new String[] {"imon/reportform.js","imon/report/echarts-all.js","imon/report/highcharts.js"});	//引入改变发现规则状态所需JS
		
//		page("js", new String[] { "imon/reportform.js" ,
//				"FusionCharts/widgets/FusionCharts.js",
//				 "FusionCharts/commonFusionCharts.js",
//				 "FusionCharts/widgets/fusioncharts.widgets.js",
//				 "FusionCharts/FusionCharts.jqueryplugin.js",});
		page("css",new String[]{"intvoisport.css"});
		
		if(isset(_REQUEST, "refresh")){
			page("type", detect_page_type(PAGE_TYPE_JSON));
		}
		if (isset(_REQUEST, "data_export")) {
			csvRows  = array();
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", _("Inverntories_export")+time+".csv");
		}
	}

	@Override
	protected void doPermissions(SQLExecutor arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void doAction(SQLExecutor executor) {
		if(isset(_REQUEST, "data_export")){
			ReportFormsDAO rf=new ReportFormsDAO(executor);
			List<Map> list  =rf.doForm();
			SystemWordbook Syswb  =new SystemWordbook();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("type", "FIRM");
			List<Map> manufacturers = Syswb.doAll(paramMap);
			CArray dataArray = null;
			Integer hostTotalNum = 0;
			Integer netTotalNum = 0;
			Integer storgeTotalNum = 0;
			CConfigurationReportFormList cr = new CConfigurationReportFormList();
			csvRows.add(array(_("Statistics of company")));
			csvRows.add(array(_("Belong company"),_("Number")));
			for(int i=0,length=manufacturers.size();i<length;i++){//遍历所有的厂商
				Map manufacturer = manufacturers.get(i);
				dataArray = cr.countForHostGroups(getIdentityBean(),executor,manufacturer.get("dlabel"),manufacturer.get("dkey"),true);
				hostTotalNum += Integer.parseInt(dataArray.get(1).toString());
				netTotalNum += Integer.parseInt(dataArray.get(2).toString());
				storgeTotalNum += Integer.parseInt(dataArray.get(3).toString());
				int totalNum = Integer.parseInt(dataArray.get(1).toString())+Integer.parseInt(dataArray.get(2).toString())+Integer.parseInt(dataArray.get(3).toString());
				csvRows.add(array(manufacturer.get("dlabel"),totalNum+""));
			}
			csvRows.add(array(""));
			
			csvRows.add(array(_("Statistics of physical type")));
			csvRows.add(array(_("Physical type"),_("Number")));
			csvRows.add(array(_("SERVER"),hostTotalNum.toString()));
			csvRows.add(array(_("Net Hosts"),netTotalNum.toString()));
			csvRows.add(array(_("Stroge Hosts"),storgeTotalNum.toString()));
			csvRows.add(array(""));
			
			
			csvRows.add(array(_("Asset monitoring type statistical table")));
			csvRows.add(array(_("Group"),_("Number")));
			for(Map map:list){
				csvRows.add(array(map.get("label"),map.get("value")));
			}
			print(rda_toCSV(csvRows));
		}else{
			CArray<Map > data=new CArray<Map>();
			Nest.value(data, "pageType").$(get_request("pageType","0"));
			Nest.value(data, "actionName").$(actionName);
			CView maintenanceView = new CView("configuration.ReportForm.list", data);
			maintenanceView.render(getIdentityBean(), executor);
			maintenanceView.show();
		}
		
	}
}
