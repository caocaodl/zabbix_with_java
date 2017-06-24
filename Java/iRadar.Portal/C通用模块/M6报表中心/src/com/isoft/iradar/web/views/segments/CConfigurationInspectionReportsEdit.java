package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.common.util.ReportUtil;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationInspectionReportsEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget inspectionReportWidget = new CWidget();
		// create form
		CForm inspectionReportForm = new CForm();
		inspectionReportForm.setName("inspectionReportForm");
		inspectionReportForm.addVar("form", Nest.value(data,"form").$());
		
		if (Nest.value(data,"inspectionReportid").$()!=null) {
			inspectionReportForm.addVar("inspectionReportid", Nest.value(data,"inspectionReportid").$());	
		}
		inspectionReportForm.addVar("executed", Nest.value(data,"executed").$());
		
		// create inspectionReport form list
		//名称
		CFormList inspectionReportFormList = new CFormList("inspectionReportFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE,false,64);
		nameTextBox.attr("autofocus", "autofocus");
		inspectionReportFormList.addRow(_("inspection report name"), nameTextBox);
		//巡检时间		
		Object fromDate = rdaDateToTime(Nest.value(data,"time").asString());
		inspectionReportFormList.addRow("巡检开始时间", createDateSelector("time", fromDate, null));
		
		Object tillfromDate = rdaDateToTime(Nest.value(data,"active_till").asString());
		inspectionReportFormList.addRow("巡检结束时间", createDateSelector("active_till", tillfromDate, null));
		ReportUtil.get_timeperiod_form(inspectionReportFormList);
		//设备类型
		CComboBox groupsComboBox = new CComboBox("groupid", Nest.value(data,"groupid").$(), "submit()");
		for(Map group : (CArray<Map>)Nest.value(data,"db_groups").$s()) {			
			groupsComboBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());	
		}		
	//	inspectionReportFormList.addRow(_("inspection report groups"), groupsComboBox);
		
		//设备
		CArray<Map> hosts_linked_to = null;
		if (Nest.value(data,"inspectionReportid").$()!=null) {
			CArray<Map> selectedhosts = DBselect(executor, 
					" SELECT hostid FROM i_inspection_report_items b WHERE b.reportid=#{reportid} and b.tenantid=#{tenantid} "
					, map("reportid",Nest.value(data,"inspectionReportid").asLong(),
						  "tenantid",idBean.getTenantId()));
			hosts_linked_to = rda_toHash(rda_objectValues(selectedhosts, "hostid"), "hostid");
		}else{
			hosts_linked_to = get_request("hosts",array());
		}
		CTweenBox hostsComboBox = new CTweenBox(inspectionReportForm, "hosts", hosts_linked_to, 20);

		// 待巡检
		CHostGet hoptions = new CHostGet();
		hoptions.setGroupIds(Nest.value(data,"groupid").asLong());
		hoptions.setEditable(true);
		hoptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> db_hosts = API.Host(idBean, executor).get(hoptions);
		order_result(db_hosts, "name");

		for(Map db_host : db_hosts) {
			if (isset(hosts_linked_to,db_host.get("hostid"))) {
				continue;
			} // add all except selected hosts
			hostsComboBox.addItem(Nest.value(db_host,"hostid").$(), Nest.value(db_host,"name").asString());
		}

		// 选中的巡检设备
		hoptions = new CHostGet();
		hoptions.setHostIds(hosts_linked_to.valuesAsLong());
		hoptions.setEditable(true);
		hoptions.setOutput(API_OUTPUT_EXTEND);

		db_hosts = API.Host(idBean, executor).get(hoptions);
		order_result(db_hosts, "name");
		for(Map db_host : db_hosts) {
			hostsComboBox.addItem(Nest.value(db_host,"hostid").$(), Nest.value(db_host,"name").asString(), null, (Nest.value(db_host,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL));
		}
		
		inspectionReportFormList.addRow(_("inspection report hosts"), hostsComboBox.get(_("inspection report hosts"), array(_("Hosts|XGroups"),groupsComboBox)));	
		
		// append tabs to form
		CTabView inspectionReportTab = new CTabView();
		inspectionReportTab.addTab("inspectionReportTab", _("inspectionReport"), inspectionReportFormList);
		inspectionReportForm.addItem(inspectionReportTab);

		// append buttons to form
		if (Nest.value(data,"inspectionReportid").$()!=null) {//修改
			inspectionReportForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					array(
						new CButtonCancel())
				));
			
		}else{//新增
			inspectionReportForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					new CButtonCancel()
				));			
		}
		
		inspectionReportWidget.addItem(inspectionReportForm);
		
		return inspectionReportWidget;
	}	
}
