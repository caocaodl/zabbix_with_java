package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_TYPE_NODATA;
import static com.isoft.iradar.inc.Defines.MAINTENANCE_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FormsUtil.get_timeperiod_form;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.MaintenancesUtil.shedule2str;
import static com.isoft.iradar.inc.MaintenancesUtil.timeperiod_type2str;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTweenBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationMaintenanceEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CWidget maintenanceWidget = new CWidget();

		// create form
		CForm maintenanceForm = new CForm();
		maintenanceForm.setName("maintenanceForm");
		maintenanceForm.addVar("form", Nest.value(data,"form").$());
		if (isset(data,"maintenanceid")) {
			maintenanceForm.addVar("maintenanceid", Nest.value(data,"maintenanceid").$());
		}

		/* Maintenance tab */
		CFormList maintenanceFormList = new CFormList("maintenanceFormList");
		CTextBox nameTextBox = new CTextBox("mname", Nest.value(data,"mname").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.setAttribute("maxlength", 128);
		nameTextBox.attr("autofocus", "autofocus");
		maintenanceFormList.addRow(_("Name"), nameTextBox);
		CComboBox typeComboBox = new CComboBox("maintenance_type", Nest.value(data,"maintenance_type").$());
		typeComboBox.addItem(MAINTENANCE_TYPE_NORMAL, _("With data collection"));
		typeComboBox.addItem(MAINTENANCE_TYPE_NODATA, _("No data collection"));
		maintenanceFormList.addRow(_("Maintenance type"), typeComboBox);

		// active since
		Object fromDate = null;
		Object activeSince = null;
		if (isset(_REQUEST,"active_since")) {
			String fromYear = get_request("active_since_year");
			String fromMonth = get_request("active_since_month");
			String fromDay = get_request("active_since_day");
			String fromHours = get_request("active_since_hour");
			String fromMinutes = get_request("active_since_minute");
			fromDate = map(
				"y", fromYear,
				"m", fromMonth,
				"d", fromDay,
				"h", fromHours,
				"i", fromMinutes
			);
			activeSince  = fromYear+fromMonth+fromDay+fromHours+fromMinutes;
		} else {
			fromDate = rdaDateToTime(Nest.value(data,"active_since").asString());
			activeSince = Nest.value(data,"active_since").$();
		}
		maintenanceForm.addVar("active_since", activeSince);

		// active till
		Object activeTill = null;
		Object toDate = null;
		if (isset(_REQUEST,"active_till")) {
			String toYear = get_request("active_till_year");
			String toMonth = get_request("active_till_month");
			String toDay = get_request("active_till_day");
			String toHours = get_request("active_till_hour");
			String toMinutes = get_request("active_till_minute");
			toDate  = map(
				"y", toYear,
				"m", toMonth,
				"d", toDay,
				"h", toHours,
				"i", toMinutes
			);
			activeTill  = toYear+toMonth+toDay+toHours+toMinutes;
		} else {
			toDate = rdaDateToTime(Nest.value(data,"active_till").asString());
			activeTill = Nest.value(data,"active_till").$();
		}
		maintenanceForm.addVar("active_till", activeTill);

		maintenanceFormList.addRow(_("Active since"), createDateSelector("active_since", fromDate, "active_till","{'before':true}"));
		maintenanceFormList.addRow(_("Active till"), createDateSelector("active_till", toDate, "active_since","{'before':true}"));

		CTextArea descriptionText = new CTextArea("description", Nest.value(data,"description").asString());
		descriptionText.setAttribute("maxlength", "255");
		maintenanceFormList.addRow(_("Description"), descriptionText);

		/* Maintenance period tab */
		CFormList maintenancePeriodFormList = new CFormList("maintenancePeriodFormList",IMonConsts.STYLE_CLASS_MULTLINE);
		CTable maintenancePeriodTable = new CTable(_("No maintenance periods defined."), "formElementTable");
		maintenancePeriodTable.setHeader(array(
			_("Period type"),
			_("Schedule"),
			_("Period"),
			_("Operations")
		));

		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"timeperiods").asCArray()).entrySet()) {
		    Object id = e.getKey();
		    Map timeperiod = e.getValue();
			maintenancePeriodTable.addRow(array(
				new CCol(timeperiod_type2str(Nest.value(timeperiod,"timeperiod_type").asInteger()), "nowrap"),
				new CCol(shedule2str(timeperiod), "wraptext"),
				new CCol(rda_date2age(0L, Nest.value(timeperiod,"period").asLong()), "nowrap"),
				new CCol(array(
					new CSubmit("edit_timeperiodid["+id+"]", _("Edit"), null, "link_menu icon edit"),
					SPACE+SPACE,
					new CSubmit("del_timeperiodid["+id+"]", _("Remove"), null, "link_menu icon remove")
				), "nowrap")
			));
			if (isset(timeperiod,"timeperiodid")) {
				maintenanceForm.addVar("timeperiods["+id+"][timeperiodid]", Nest.value(timeperiod,"timeperiodid").$());
			}
			maintenanceForm.addVar("timeperiods["+id+"][timeperiod_type]", Nest.value(timeperiod,"timeperiod_type").$());
			maintenanceForm.addVar("timeperiods["+id+"][every]", Nest.value(timeperiod,"every").$());
			maintenanceForm.addVar("timeperiods["+id+"][month]", Nest.value(timeperiod,"month").$());
			maintenanceForm.addVar("timeperiods["+id+"][dayofweek]", Nest.value(timeperiod,"dayofweek").$());
			maintenanceForm.addVar("timeperiods["+id+"][day]", Nest.value(timeperiod,"day").$());
			maintenanceForm.addVar("timeperiods["+id+"][start_time]", Nest.value(timeperiod,"start_time").$());
			maintenanceForm.addVar("timeperiods["+id+"][start_date]", Nest.value(timeperiod,"start_date").$());
			maintenanceForm.addVar("timeperiods["+id+"][period]", Nest.value(timeperiod,"period").$());
		}

		CDiv periodsDiv = new CDiv(maintenancePeriodTable, "objectgroup inlineblock border_dotted");
		CDiv periodsDivFooter = null;
		if (!isset(_REQUEST,"new_timeperiod")) {
			periodsDivFooter = new CDiv(new CSubmit("new_timeperiod", _("New"), null, "link_menu new"),"cmd_div");
		}
		maintenancePeriodFormList.addRow(_("Periods"), new CDiv(array(periodsDiv,periodsDivFooter), "objectgroup inlineblock border_dotted"));

		if (isset(_REQUEST,"new_timeperiod")) {
			String saveLabel = null;
			if (isArray(Nest.value(_REQUEST,"new_timeperiod").$()) && isset(Nest.value(_REQUEST,"new_timeperiod","id").$())) {
				saveLabel  = _("Save");
			} else {
				saveLabel = _("Add");
			}

			CDiv footer = new CDiv(array(
				new CSubmit("add_timeperiod", saveLabel, null, "link_menu add"),
				SPACE+SPACE,
				new CSubmit("cancel_new_timeperiod", _("Cancel"), null, "link_menu cancel")
			), IMonConsts.STYLE_CLASS_CMD_DIV);

			maintenancePeriodFormList.addRow(_("Maintenance period"),
				new CDiv(array(get_timeperiod_form(), footer), "objectgroup inlineblock border_dotted")
			);
		}

		/* Hosts & groups tab */
		CFormList hostsAndGroupsFormList = new CFormList("hostsAndGroupsFormList");
		CTweenBox hostTweenBox = new CTweenBox(maintenanceForm, "hostids", Nest.value(data,"hostids").$(), 20);
		for(Map host : (CArray<Map>)Nest.value(data,"hosts").asCArray()) {
			hostTweenBox.addItem(Nest.value(host,"hostid").$(), Nest.value(host,"name").asString());
		}
		CComboBox groupsComboBox = new CComboBox("twb_groupid", Nest.value(data,"twb_groupid").$(), "submit()");
		for(Map group : (CArray<Map>)Nest.value(data,"all_groups").asCArray()) {
			if(Nest.value(group, "groupid").asInteger()==IMonConsts.TEMPLATES||Nest.value(group, "groupid").asInteger()==IMonConsts.DISCOVERED_HOSTS||Nest.value(group, "groupid").asInteger()==IMonConsts.MON_VM){
				continue;
			}
			groupsComboBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}
		CTable hostTable = new CTable(null, "formElementTable");
		hostTable.addRow(hostTweenBox.get(_("In maintenance"), array(_("Other hosts | Group")+SPACE, groupsComboBox)));
		hostsAndGroupsFormList.addRow(_("Select host"), hostTable);

		/**
		 * 根据维护计划优化需求，去除设备类型的选择
		 */
		/*CTable groupTable = new CTable(null, "formElementTable");
		CTweenBox groupTweenBox = new CTweenBox(maintenanceForm, "groupids", Nest.value(data,"groupids").$(), 10);
		for(Map group : (CArray<Map>)Nest.value(data,"all_groups").asCArray()) {
			if(Nest.value(group, "groupid").asInteger()==IMonConsts.TEMPLATES||Nest.value(group, "groupid").asInteger()==IMonConsts.DISCOVERED_HOSTS||Nest.value(group, "groupid").asInteger()==IMonConsts.MON_VM){
				continue;	
			}
			groupTweenBox.addItem(Nest.value(group,"groupid").$(), Nest.value(group,"name").asString());
		}
		groupTable.addRow(groupTweenBox.get(_("In maintenance"), _("Other groups")));

		hostsAndGroupsFormList.addRow(_("Groups in maintenance"), groupTable);*/

		// append tabs to form
		CTabView maintenanceTab = new CTabView();
		if (Nest.value(data, "form_refresh").asInteger()==0) {
			maintenanceTab.setSelected("0");
		}
		maintenanceTab.addTab("maintenanceTab", _("Maintenance"), maintenanceFormList);
		maintenanceTab.addTab("periodsTab", _("Periods"), maintenancePeriodFormList);
		maintenanceTab.addTab("hostTab", _("Host"), hostsAndGroupsFormList);
		maintenanceForm.addItem(maintenanceTab);

		// append buttons to form
		if (empty(Nest.value(data,"maintenanceid").$())) {
			maintenanceForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		} else {
			maintenanceForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					new CButtonCancel()
				)
			));
		}

		maintenanceWidget.addItem(maintenanceForm);
		return maintenanceWidget;
	}

}
