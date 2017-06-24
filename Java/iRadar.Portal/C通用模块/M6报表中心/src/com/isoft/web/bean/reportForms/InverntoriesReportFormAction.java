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
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.core.utils.EasyMap;
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
public class InverntoriesReportFormAction extends RadarBaseAction{
	private CArray csvRows = null;
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if(isset(_REQUEST, "refresh")){
			AjaxResponse ajaxResponse = new AjaxResponse();
			ReportFormsDAO rf=new ReportFormsDAO(executor);
			List<Map> list  =rf.doForm();
			int i=0;
			CArray<Map> arrayMap = new CArray<Map>();
			for(Map map:list){
				arrayMap.add(EasyMap.build("names", map.get("label"), "values", map.get("value")));
				i++;
				if(i>30){
					break;
				}
			}		
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
			CConfigurationReportFormList result = new CConfigurationReportFormList();
			csvRows = (CArray)result.inverntForReportFirm(getIdentityBean(), executor, false).get("data");	
			print(rda_toCSV(csvRows));
		}else{
			CArray<Map > data=new CArray<Map>();
			Nest.value(data, "pageType").$(get_request("pageType","0"));
			Nest.value(data, "actionName").$(actionName);
			CView maintenanceView = new CView("configuration.Inverntories.list", data);
			maintenanceView.render(getIdentityBean(), executor);
			maintenanceView.show();
		}
		
	}
}
