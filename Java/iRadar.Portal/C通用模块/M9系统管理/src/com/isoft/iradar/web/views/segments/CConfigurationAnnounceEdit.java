package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.types.CArray.array;

import java.util.List;
import java.util.Map;

import com.isoft.biz.daoimpl.common.AnnouncementDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationAnnounceEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();

		// create form
		CForm maintenanceForm = new CForm();
		maintenanceForm.setName("maintenanceForm");
		maintenanceForm.addVar("form", Nest.value(data,"form").$());
		CFormList maintenanceFormList = new CFormList("maintenanceFormList");
		CArray<Map> map=new CArray<Map>();
		CArray<Map> announcement;
		if (isset(data,"announcementid")) {
			CArray<Map> parameter=new CArray<Map>();
			parameter.put("announcementid", Nest.value(data, "announcementid").asInteger());
			AnnouncementDAO an =new AnnouncementDAO(executor);
			List list=an.doListOne(parameter);
			int i=0;
			for (Object object : list) {
				map.put(i, object);
				
			}
		}
		announcement=Nest.value(map, "0").asCArray();
		if (isset(data,"active_till")&&isset(data,"active_since")) {
			announcement=new CArray<Map>();
			announcement.put("active_till", Nest.value(data, "active_till").asString());
			announcement.put("active_since", Nest.value(data, "active_since").asString());
			if(!empty(get_request("announcementid"))){
				CTextBox ID = new CTextBox("announcementid",get_request("announcementid").toString());
				ID.setType("hidden");
				maintenanceFormList.addRow("", ID );
			}
			
		}else{
			if(!empty(get_request("announcementid"))){
				Nest.value(announcement,"announcementid").$(get_request("announcementid"));
			}
			CTextBox ID = new CTextBox("announcementid",Nest.value(announcement,"announcementid").asString());
			ID.setType("hidden");
			maintenanceFormList.addRow("", ID );
			
		}
		
		/* Maintenance tab */
		if(!empty(get_request("title"))){
			Nest.value(announcement,"title").$(get_request("title"));
		}
		if(!empty(get_request("content"))){
			Nest.value(announcement,"content").$(get_request("content"));
		}
		CTextBox nameTextBox = new CTextBox("title", Nest.value(announcement,"title").asString());
		nameTextBox.setAttribute("maxlength", 20);
		if(!empty(Nest.value(data,"active_since").asString())){
			Nest.value(announcement,"active_since").$(Nest.value(data,"active_since").asString());
		}
		CTextArea contentarea = new CTextArea("content", Nest.value(announcement,"content").asString());
		contentarea.attr("style", "resize: none");
		
		maintenanceFormList.addRow("标题", nameTextBox);
		maintenanceFormList.addRow("内容", contentarea);
		
		// active since
		Object fromDate = null;
		Object activeSince = null;
		if(!empty(Nest.value(data,"active_since").asString())){
			Nest.value(announcement,"active_since").$(Nest.value(data,"active_since").asString());
		}
		if(!empty(Nest.value(data,"active_till").asString())){
			Nest.value(announcement,"active_till").$(Nest.value(data,"active_till").asString());
		}
		fromDate = rdaDateToTime(Nest.value(announcement,"active_since").asString());
		activeSince = Nest.value(announcement,"active_since").$();
		maintenanceForm.addVar("active_since", activeSince);
		Object activeTill = null;
		Object toDate = null;
		toDate = rdaDateToTime(Nest.value(announcement,"active_till").asString());
		activeTill = Nest.value(announcement,"active_till").$();
		maintenanceForm.addVar("active_till", activeTill);
		maintenanceFormList.addRow("生效时间", createDateSelector("active_since", fromDate, "active_till"));
		maintenanceFormList.addRow("停止时间", createDateSelector("active_till", toDate, "active_since"));
		
		// append tabs to form
		CTabView maintenanceTab = new CTabView();
		maintenanceTab.addTab("maintenanceTab", _("Maintenance"), maintenanceFormList);
		maintenanceForm.addItem(maintenanceTab);

		// append buttons to form
		if (empty(Nest.value(data,"announcementid").$())) {
			maintenanceForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		} else {
			maintenanceForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CButtonCancel()
				)
			));
		}
		maintenanceWidget.addItem(maintenanceForm);
		return maintenanceWidget;
	}
}
