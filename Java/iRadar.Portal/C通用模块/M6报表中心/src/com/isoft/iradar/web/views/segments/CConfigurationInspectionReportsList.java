package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_APPROACH;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_STATUS_EXPIRED;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.text.ParseException;
import java.util.Map;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.Util.TvmUtil;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationInspectionReportsList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget inspectionReportWidget = new CWidget();
		
		// create form
		CForm inspectionReportForm = new CForm();
		inspectionReportForm.setName("inspectionReportForm");
		
		CToolBar tb = new CToolBar(inspectionReportForm);
		tb.addSubmit("form", _("Create inspection report"), "", "orange create");
		
		CArray<CComboItem> goComboBox = array();
		
		
		CComboItem goOption = new CComboItem("delete", _("Delete selected"));
		goOption.setAttribute("confirm", _("Delete selected inspection reports?"));
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("activate", _("Enable selected"));
		goOption.setAttribute("confirm", _("Enable selected"));
		goOption.setAttribute("class", "orange activate");
		goComboBox.add(goOption);
		
		goOption = new CComboItem("disable", _("Disable selected"));
		goOption.setAttribute("confirm", _("Disable selected"));
		goOption.setAttribute("class", "orange disable");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"inspectionReports\";");
		rda_add_post_js("chkbxRange.prefix = \""+Nest.value(data,"reportid").$()+"\";");		
		rda_add_post_js("cookie.prefix = \""+Nest.value(data,"reportid").$()+"\";");
		
		// header
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		inspectionReportWidget.addItem(headerActions);
		
		// create table
		CTableInfo inspectionReportTable = new CTableInfo(_("No inspection report found."));
		inspectionReportTable.setHeader(array(
			new CCheckBox("all_inspectionReports", false, "checkAll(\""+inspectionReportForm.getName()+"\", \"all_inspectionReports\", \"inspectionReports\");"),
			make_sorting_header("巡检"+_("Name"), "name"),
			//_("inspection report time"),
			"创建时间",
			"巡检开始时间",
			"巡检结束时间",
			"下次巡检时间",
			_("Status"),
			"执行情况",
			_("history records")
		));
		/*CSpan status= new CSpan("已过期"," font-color:red ");
		status.addStyle("color:#FF0000");*/
		//巡检时间
		ReportUtil util = new ReportUtil(); 
		CArray<Map> tempInspectionReports = (CArray<Map>)Nest.value(data,"inspectionReports").asCArray();
		order_result(tempInspectionReports, get_request("sort", "name"), empty(get_request("sortorder", null))?Defines.RDA_SORT_UP:getPageSortOrder(idBean, executor));
		for(Map inspectionReport : tempInspectionReports) {
			CCol status = new CCol(new CDiv(new CLink(
					TvmUtil.status2str(Nest.value(inspectionReport,"status").asInteger()),
					"?inspectionReports="+Nest.value(inspectionReport,"reportid").asString()+"&sortorder=ASC"//+sortorder
					 +(Nest.value(inspectionReport,"status").asInteger() == HOST_STATUS_MONITORED ? "&go=disable" : "&go=activate"),//添加templateid、设备类型、模型ID参数，保证触发时间后还跳转到本页面
					 TvmUtil.status2style(Nest.value(inspectionReport,"status").asInteger())
				), "switch"));
			if (Nest.value(inspectionReport,"active_till").asLong() < time()) {
				Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_EXPIRED);
			} else if (Nest.value(inspectionReport,"time").asLong() > time()) {
				Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_APPROACH);
			} else {
				Nest.value(inspectionReport,"performstatus").$(MAINTENANCE_STATUS_ACTIVE);
			}
			try {
				Nest.value(inspectionReport, "nextMaintenanceTime").$(util.get_nexttime_form(inspectionReport));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			CSpan maintenanceStatus = null;
			switch (Nest.value(inspectionReport,"performstatus").asInteger()) {
				case MAINTENANCE_STATUS_EXPIRED:
					maintenanceStatus  = new CSpan(_x("Expired", "maintenance status"), "red");
					break;
				case MAINTENANCE_STATUS_APPROACH:
					maintenanceStatus = new CSpan(_x("Approaching", "maintenance status"), "blue");
					break;
				case MAINTENANCE_STATUS_ACTIVE:
					maintenanceStatus = new CSpan(_x("Active", "maintenance status"), "green");
					break;
			}
			long active_till = Nest.value(inspectionReport, "active_till").asLong();
			long nextMaintenanceTime = Nest.value(inspectionReport, "nextMaintenanceTime").asLong();
			inspectionReportTable.addRow(array(
					new CCheckBox("inspectionReports["+Nest.value(inspectionReport,"reportid").$()+"]", false, null, Nest.value(inspectionReport,"reportid").asInteger()),
					new CLink(Nest.value(inspectionReport,"name").$(), "inspectionReport.action?form=update&inspectionReportid="+Nest.value(inspectionReport,"reportid").$()),
					rda_date2str(_("d M Y H:i:s"), Nest.value(inspectionReport, "create_time").asLong()),
					rda_date2str(_("d M Y H:i:s"), Nest.value(inspectionReport, "time").asLong()),
					rda_date2str(_("d M Y H:i:s"), active_till),	
					nextMaintenanceTime==active_till?"--":rda_date2str(_("d M Y H:i:s"), nextMaintenanceTime),
					status,
					maintenanceStatus,
					new CLink(_("history records"), "inspectionReport.action?form=historyRecords&inspectionReportid="+Nest.value(inspectionReport,"reportid").$()+"&batchnum="+Nest.value(inspectionReport,"batchnum").asInteger())
				));
		}
		
		// append table to form
		inspectionReportForm.addItem(array(inspectionReportTable, Nest.value(data,"paging").$()));

		// append form to widget
		inspectionReportWidget.addItem(inspectionReportForm);
		
		return inspectionReportWidget;
	}
}
