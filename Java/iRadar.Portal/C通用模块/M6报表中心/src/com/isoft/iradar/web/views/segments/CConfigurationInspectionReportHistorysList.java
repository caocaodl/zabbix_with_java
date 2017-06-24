package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.types.CArray.array;

import java.util.HashMap;
import java.util.Map;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationInspectionReportHistorysList extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget inspectionReportWidget = new CWidget();
		
		// create form
		CForm reportheaderForm = new CForm("get");
		CTableInfo reportheaderTable = new CTableInfo(_("No inspection report history found."));
		reportheaderTable.addClass("inspection_gauge");
		reportheaderTable.setName("historyRecords");
		reportheaderForm.addVar("form", "historyRecords");
		reportheaderForm.addVar("inspectionReportid", Nest.value(data, "inspectionReportid").$());
		
		String username = Nest.value(data, "username").asString();
		String name = Nest.value(data, "name").asString();
		String groupname = Nest.value(data, "groupname").asString();
		
		CDiv div=new CDiv(array(new CSubmit("csv_export", _("Export to CSV"),"","eventcsvexport export")),"body_header");
		//reportheaderForm.addItem();
		
		//巡检历史
		Map params = new HashMap();
		params.put("reportid", Nest.value(data, "inspectionReportid").$());
		params.put("tenantid", Nest.value(data, "tenantid").$());
		CArray<Map> batchmaps = DBselect(executor,
				" SELECT b.batchnum, b.batch_time " +
				" FROM i_inspection_report_batch b " +
				" WHERE reportid=#{reportid} AND tenantid=#{tenantid}"+
				" ORDER BY batchnum  DESC " 
			, params);
		if(!empty(batchmaps)){
			CComboBox batchbox =new CComboBox("batchnum",Nest.value(data, "batchnum").asString(),"javascript: submit();");
			for(Map batch : batchmaps){
				batchbox.addItem(Nest.value(batch, "batchnum").asInteger(), 
						rda_date2str(_("d M Y H:i:s"), Nest.value(batch,"batch_time").asLong())+SPACE+"第"+Nest.value(batch, "batchnum").asInteger()+"次巡检结果");
			}
			
			div.addItem(array(SPACE+"巡检历史"+SPACE,batchbox));
			//reportheaderForm.addItem();
		}
		reportheaderForm.addItem(div);
		//巡检报告
		CCol col = new CCol("巡检报告");
		col.setColSpan(4);
		reportheaderTable.setHeader(col);
		reportheaderTable.addRow(array("巡检人",username,"巡检时间",rda_date2str(_("d M Y H:i:s"), Nest.value(data,"time").asLong())), "header_sytle");
		reportheaderTable.addRow(array("巡检任务",name,"巡检类型",groupname), "header_sytle");
		
		CArray<Map> reporthistorys = (CArray<Map>)Nest.value(data,"reporthistorys").asCArray();
		reportheaderTable.addRow(array("巡检结果",array(Nest.value(data, "reportcontent").asString(),BR(),
				                                      Nest.value(data, "contenttwo").asString())), "header_sytle");
		
		reportheaderForm.addItem(reportheaderTable);
		inspectionReportWidget.addItem(reportheaderForm);
		
		CForm inspectionReportForm = new CForm();
		inspectionReportForm.setName("inspectionReportForm");
		
		// create table
		CTableInfo inspectionReportTable = new CTableInfo(_("No inspection report history found."));
		String tbClass = inspectionReportTable.getAttribute("class").toString();
		tbClass += " inspection";
		inspectionReportTable.setAttribute("class", tbClass);
		inspectionReportTable.setHeader(array(
			"巡检设备名",
			"监控指标",
			_("MonitoringValue"),
			"巡检结果"
		));
			
		for(Map item : reporthistorys) {
			inspectionReportTable.addRow(array(
					Nest.value(item, "hostname").$(),
					Nest.value(item, "itemname").$(),
					Nest.value(item, "value").asString(),
					ReportUtil.getNormalZH(Nest.value(item, "isproblem").asInteger())
				));
		}
		
		// append table to form
		inspectionReportForm.addItem(inspectionReportTable);
		inspectionReportWidget.addItem(inspectionReportForm);
		
		return inspectionReportWidget;
	}
	
	

}
