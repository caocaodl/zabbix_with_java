package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_DOWNTIME;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_ONETIME_DOWNTIME;
import static com.isoft.iradar.inc.Defines.SERVICE_TIME_TYPE_UPTIME;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT_ZERO_TIME;
import static com.isoft.iradar.inc.FuncsUtil.dowHrMinToStr;
import static com.isoft.iradar.inc.FuncsUtil.getDayOfWeekCaption;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.createDateSelector;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.ServicesUtil;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CVar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationServicesEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.services.edit.js");

		Map service = Nest.value(data,"service").asCArray();

		CWidget servicesWidget = new CWidget();
		servicesWidget.setRootClass("services-edit");
		servicesWidget.addPageHeader(_("CONFIGURATION OF IT SERVICES"));

		// create form
		CForm servicesForm = new CForm();
		servicesForm.setName("servicesForm");
		servicesForm.addVar("form", Nest.value(data,"form").$());
		servicesForm.addVar("parentid", Nest.value(data,"parentid").$());
		servicesForm.addVar("parentname", Nest.value(data,"parentname").$());
		servicesForm.addVar("triggerid", Nest.value(data,"triggerid").$());
		if (isset(Nest.value(data,"service").$())) {
			servicesForm.addVar("serviceid", Nest.value(data,"service","serviceid").$());
		}

		// create form list
		CFormList servicesFormList = new CFormList("servicesFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE, false, 128);
		nameTextBox.attr("autofocus", "autofocus");
		servicesFormList.addRow(_("Name"), nameTextBox);

		// append parent link to form list
		servicesFormList.addRow(_("Parent service"), array(
			new CTextBox("parent_name", Nest.value(data,"parentname").asString(), RDA_TEXTBOX_STANDARD_SIZE, true, 128),
			new CButton("select_parent", _("Change"), "javascript: openWinCentered(\"services.action?pservices=1"+url_param(idBean, "serviceid")+"\", \"RDA_Services_List\", 740, 420, \"scrollbars=1, toolbar=0, menubar=0, resizable=1, dialog=0\");", "formlist")
		));

		// append algorithm to form list
		CComboBox algorithmComboBox = new CComboBox("algorithm", Nest.value(data,"algorithm").$());
		algorithmComboBox.addItems(ServicesUtil.serviceAlgorythm());
		servicesFormList.addRow(_("Status calculation algorithm"), algorithmComboBox);

		// append SLA to form list
		CCheckBox showslaCheckbox = new CCheckBox("showsla", (Nest.value(data,"showsla").asInteger() == 0) ? false : true, null, 1);
		CTextBox goodslaTextBox = new CTextBox("goodsla", Nest.value(data,"goodsla").asString(), 6, false, 8);
		if (!Nest.value(data,"showsla").asBoolean()) {
			goodslaTextBox.setAttribute("disabled", "disabled");
		}
		servicesFormList.addRow(_("Calculate SLA, acceptable SLA (in %)"), array(showslaCheckbox, goodslaTextBox));

		// append trigger to form list
		servicesFormList.addRow(_("Trigger"), array(
			new CTextBox("trigger", Nest.value(data,"trigger").asString(), RDA_TEXTBOX_STANDARD_SIZE, true),
			new CButton("btn1", _("Select"),
				"return PopUp(\"popup.action?"+
					"dstfrm="+servicesForm.getName()+
					"&dstfld1=triggerid"+
					"&dstfld2=trigger"+
					"&srctbl=triggers"+
					"&srcfld1=triggerid"+
					"&srcfld2=description"+
					"&real_hosts=1"+
					"&with_triggers=1\");",
				"formlist"
			)
		));
		servicesFormList.addRow(_("Sort order (0->999)"), new CTextBox("sortorder", Nest.value(data,"sortorder").asString(), 3, false, 3));

		/*
		 * Dependencies tab
		 */
		CTable servicesChildTable = new CTable(_("No dependencies defined."), "formElementTable");
		servicesChildTable.setAttribute("style", "min-width:500px;");
		servicesChildTable.setAttribute("id", "service_children");
		servicesChildTable.setHeader(array(_("Services"), _("Soft"), _("Trigger"), _("Action")));
		for(Map child : (CArray<Map>)Nest.value(data,"children").asCArray()) {
			CRow row = new CRow(array(
				array(
					new CLink(Nest.value(child,"name").$(), "services.action?form=1&serviceid="+Nest.value(child,"serviceid").$()),
					new CVar("children["+Nest.value(child,"serviceid").$()+"][name]", Nest.value(child,"name").$()),
					new CVar("children["+Nest.value(child,"serviceid").$()+"][serviceid]", Nest.value(child,"serviceid").$())
				),
				new CCheckBox(
					"children["+Nest.value(child,"serviceid").$()+"][soft]",
					isset(Nest.value(child,"soft").$()) && !empty(Nest.value(child,"soft").$()) ? true : false,
					null,
					1
				),
				!empty(Nest.value(child,"trigger").$()) ? Nest.value(child,"trigger").$() : "-",
				new CButton("remove", _("Remove"), "javascript: removeDependentChild(\""+Nest.value(child,"serviceid").$()+"\");", "link_menu")
			));
			row.setAttribute("id", "children_"+Nest.value(child,"serviceid").$());
			servicesChildTable.addRow(row);
		}
		CFormList servicesDependenciesFormList = new CFormList("servicesDependensiesFormList");
		servicesDependenciesFormList.addRow(
			_("Depends on"),
			new CDiv(array(
				servicesChildTable,
				new CButton("add_child_service", _("Add"), "javascript: openWinCentered(\"services.action?cservices=1"+url_param(idBean, "serviceid")+"\", \"RDA_Services_List\", 640, 520, \"scrollbars=1, toolbar=0, menubar=0, resizable=0\");", "link_menu")
			),
			"objectgroup inlineblock border_dotted ui-corner-all")
		);

		/*
		 * Service times tab
		 */
		CFormList servicesTimeFormList = new CFormList("servicesTimeFormList");
		CTable servicesTimeTable = new CTable(_("No times defined. Work 24x7."), "formElementTable");
		servicesTimeTable.setAttribute("style", "min-width: 500px;");
		servicesTimeTable.setHeader(array(_("Type"), _("Interval"), _("Note"), _("Action")));

		int i = 0;
		for(Map serviceTime : (CArray<Map>)Nest.value(data,"times").asCArray()) {
			CSpan type = null;
			String from = null;
			String to = null;
			switch (Nest.value(serviceTime,"type").asInteger()) {
				case SERVICE_TIME_TYPE_UPTIME:
					type = new CSpan(_("Uptime"), "enabled");
					from  = dowHrMinToStr(Nest.value(serviceTime,"ts_from").asLong());
					to  = dowHrMinToStr(Nest.value(serviceTime,"ts_to").asLong(), true);
					break;
				case SERVICE_TIME_TYPE_DOWNTIME:
					type = new CSpan(_("Downtime"), "disabled");
					from = dowHrMinToStr(Nest.value(serviceTime,"ts_from").asLong());
					to = dowHrMinToStr(Nest.value(serviceTime,"ts_to").asLong(), true);
					break;
				case SERVICE_TIME_TYPE_ONETIME_DOWNTIME:
					type = new CSpan(_("One-time downtime"), "disabled");
					from = rda_date2str(_("d M Y H:i"), Nest.value(serviceTime,"ts_from").asLong());
					to = rda_date2str(_("d M Y H:i"), Nest.value(serviceTime,"ts_to").asLong());
					break;
			}
			CRow row = new CRow(array(
				array(
					type,
					new CVar("times["+i+"][type]", Nest.value(serviceTime,"type").$()),
					new CVar("times["+i+"][ts_from]", Nest.value(serviceTime,"ts_from").$()),
					new CVar("times["+i+"][ts_to]", Nest.value(serviceTime,"ts_to").$()),
					new CVar("times["+i+"][note]", Nest.value(serviceTime,"note").$())
				),
				from+" - "+to,
				htmlspecialchars(Nest.value(serviceTime,"note").asString()),
				new CButton("remove", _("Remove"), "javascript: removeTime(\""+i+"\");", "link_menu")
			));
			row.setAttribute("id", "times_"+i);
			servicesTimeTable.addRow(row);
			i++;
		}
		servicesTimeFormList.addRow(
			_("Service times"),
			new CDiv(servicesTimeTable, "objectgroup inlineblock border_dotted ui-corner-all")
		);

		// create service time table
		CTable serviceTimeTable = new CTable(null, "formElementTable");
		if (Nest.value(data,"new_service_time","type").asInteger() == SERVICE_TIME_TYPE_ONETIME_DOWNTIME) {
			// downtime since
			Object fromDate = null;
			Object serviceTimeFrom = null;
			if (isset(Nest.value(_REQUEST,"new_service_time","from").$())) {
				String fromYear = get_request("new_service_time_from_year");
				String fromMonth = get_request("new_service_time_from_month");
				String fromDay = get_request("new_service_time_from_day");
				String fromHours = get_request("new_service_time_from_hour");
				String fromMinutes = get_request("new_service_time_from_minute");
				fromDate = map(
					"y" , fromYear,
					"m" , fromMonth,
					"d" , fromDay,
					"h" , fromHours,
					"i" , fromMinutes
				);
				serviceTimeFrom = fromYear+fromMonth+fromDay+fromHours+fromMinutes;
			} else {
				String downtimeSince = Nest.as(date(TIMESTAMP_FORMAT_ZERO_TIME)).asString();
				fromDate = rdaDateToTime(downtimeSince);
				serviceTimeFrom = downtimeSince;
			}
			servicesForm.addVar("new_service_time[from]", serviceTimeFrom);

			// downtime till
			Object toDate = null;
			Object serviceTimeTo = null;
			if (isset(Nest.value(_REQUEST,"new_service_time","to").$())) {
				String toYear = get_request("new_service_time_to_year");
				String toMonth = get_request("new_service_time_to_month");
				String toDay = get_request("new_service_time_to_day");
				String toHours = get_request("new_service_time_to_hour");
				String toMinutes = get_request("new_service_time_to_minute");
				toDate = map(
					"y" , toYear,
					"m" , toMonth,
					"d" , toDay,
					"h" , toHours,
					"i" , toMinutes
				);
				serviceTimeTo = toYear+toMonth+toDay+toHours+toMinutes;
			} else {
				String downtimeTill = Nest.as(date(TIMESTAMP_FORMAT_ZERO_TIME, time() + SEC_PER_DAY)).asString();
				toDate = rdaDateToTime(downtimeTill);
				serviceTimeTo = downtimeTill;
			}
			servicesForm.addVar("new_service_time[to]", serviceTimeTo);

			// create calendar table
			CTable timeCalendarTable = new CTable();

			CTextBox noteTextBox = new CTextBox("new_service_time[note]", "", RDA_TEXTBOX_STANDARD_SIZE);
			noteTextBox.setAttribute("placeholder", _("short description"));
			timeCalendarTable.addRow(array(_("Note"), noteTextBox));
			timeCalendarTable.addRow(array(_("From"), createDateSelector("new_service_time_from", fromDate, "new_service_time_to")));
			timeCalendarTable.addRow(array(_("Till"), createDateSelector("new_service_time_to", toDate, "new_service_time_from")));
			serviceTimeTable.addRow(timeCalendarTable);
		} else {
			CComboBox weekFromComboBox = new CComboBox("new_service_time[from_week]", isset(Nest.value(_REQUEST,"new_service_time","from_week").$())
					? Nest.value(_REQUEST,"new_service_time","from_week").$() : 0);
			CComboBox weekToComboBox = new CComboBox("new_service_time[to_week]", isset(Nest.value(_REQUEST,"new_service_time","from_week").$())
					? Nest.value(_REQUEST,"new_service_time","to_week").$() : 0);
			for (int dow = 0; dow < 7; dow++) {
				weekFromComboBox.addItem(dow, getDayOfWeekCaption(dow));
				weekToComboBox.addItem(dow, getDayOfWeekCaption(dow));
			}
			CTextBox timeFromHourTextBox = new CTextBox("new_service_time[from_hour]", isset(Nest.value(_REQUEST,"new_service_time","from_hour").$())
					? Nest.value(_REQUEST,"new_service_time","from_hour").asString() : "", 2, false, 2);
			timeFromHourTextBox.setAttribute("placeholder", _("hh"));
			CTextBox timeFromMinuteTextBox = new CTextBox("new_service_time[from_minute]", isset(Nest.value(_REQUEST,"new_service_time","from_minute").$())
					? Nest.value(_REQUEST,"new_service_time","from_minute").asString() : "", 2, false, 2);
			timeFromMinuteTextBox.setAttribute("placeholder", _("mm"));
			CTextBox timeToHourTextBox = new CTextBox("new_service_time[to_hour]", isset(Nest.value(_REQUEST,"new_service_time","to_hour").$())
					? Nest.value(_REQUEST,"new_service_time","to_hour").asString() : "", 2, false, 2);
			timeToHourTextBox.setAttribute("placeholder", _("hh"));
			CTextBox timeToMinuteTextBox = new CTextBox("new_service_time[to_minute]", isset(Nest.value(_REQUEST,"new_service_time","to_minute").$())
					? Nest.value(_REQUEST,"new_service_time","to_minute").asString() : "", 2, false, 2);
			timeToMinuteTextBox.setAttribute("placeholder", _("mm"));

			serviceTimeTable.addRow(array(_("From"), weekFromComboBox, new CCol(array(_("Time"), SPACE, timeFromHourTextBox, " : ", timeFromMinuteTextBox))));
			serviceTimeTable.addRow(array(_("Till"), weekToComboBox, new CCol(array(_("Time"), SPACE, timeToHourTextBox, " : ", timeToMinuteTextBox))));
			servicesForm.addVar("new_service_time[note]", "");
		}

		CComboBox timeTypeComboBox = new CComboBox("new_service_time[type]", Nest.value(data,"new_service_time","type").$(), "javascript: document.forms[0].action += \"?form=1\"; submit();");
		timeTypeComboBox.addItem(SERVICE_TIME_TYPE_UPTIME, _("Uptime"));
		timeTypeComboBox.addItem(SERVICE_TIME_TYPE_DOWNTIME, _("Downtime"));
		timeTypeComboBox.addItem(SERVICE_TIME_TYPE_ONETIME_DOWNTIME, _("One-time downtime"));
		servicesTimeFormList.addRow(
			_("New service time"),
			new CDiv(array(
				new CDiv(timeTypeComboBox, "time-type-combobox"),
				serviceTimeTable,
				new CButton("add_service_time", _("Add"), null, "link_menu")
			),
			"objectgroup inlineblock border_dotted ui-corner-all")
		);

		/*
		 * Append tabs to form
		 */
		CTabView servicesTab = new CTabView();
		if (!isset(data,"form_refresh")) {
			servicesTab.setSelected("0");
		}
		servicesTab.addTab("servicesTab", _("Service"), servicesFormList);
		servicesTab.addTab("servicesDependenciesTab", _("Dependencies"), servicesDependenciesFormList);
		servicesTab.addTab("servicesTimeTab", _("Time"), servicesTimeFormList);
		servicesForm.addItem(servicesTab);

		// append buttons to form
		CArray buttons = array();
		if (!empty(Nest.value(service,"serviceid").$()) && empty(Nest.value(service,"dependencies").$())) {
			buttons.add(new CButtonDelete("Delete selected service?", url_param(idBean, "form")+url_param(idBean, "serviceid")+"&saction=1"));
		}
		buttons.add(new CButtonCancel());

		servicesForm.addItem(makeFormFooter(
			new CSubmit("save_service", _("Save"), "javascript: document.forms[0].action += \"?saction=1\";"),
			buttons
		));

		// append form to widget
		servicesWidget.addItem(servicesForm);

		return servicesWidget;
	}

}
