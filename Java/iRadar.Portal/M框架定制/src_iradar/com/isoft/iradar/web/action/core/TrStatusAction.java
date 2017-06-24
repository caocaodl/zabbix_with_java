package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DAY_IN_YEAR;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENTS_OPTION_ALL;
import static com.isoft.iradar.inc.Defines.EVENTS_OPTION_NOEVENT;
import static com.isoft.iradar.inc.Defines.EVENTS_OPTION_NOT_ACK;
import static com.isoft.iradar.inc.Defines.EVENT_ACK_DISABLED;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_ACK_STS_ANY;
import static com.isoft.iradar.inc.Defines.RDA_ACK_STS_WITH_LAST_UNACK;
import static com.isoft.iradar.inc.Defines.RDA_ACK_STS_WITH_UNACK;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.TRIGGERS_OPTION_ALL;
import static com.isoft.iradar.inc.Defines.TRIGGERS_OPTION_ONLYTRUE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.audio;
import static com.isoft.iradar.inc.EventsUtil.getEventAckState;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupTrigger;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.str_in_array;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HtmlUtil.BR;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.HtmlUtil.get_table_header;
import static com.isoft.iradar.inc.JsUtil.play_sound;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.addTriggerValueStyle;
import static com.isoft.iradar.inc.TriggersUtil.explode_exp;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CTriggersInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class TrStatusAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Status of triggers"));
		page("file", "tr_status.action");
		page("hist_arg", new String[] { "groupid", "hostid" });
		page("scripts", new String[] { "class.cswitcher.js" });
		page("type", detect_page_type(PAGE_TYPE_HTML));

		if (PAGE_TYPE_HTML == (Integer) page("type")) {
			define("RDA_PAGE_DO_REFRESH", 1);
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"groupid",			            array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",				        array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"fullscreen",			        array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"btnSelect",			        array(T_RDA_STR, O_OPT, null,	null,		null),
			// filter
			"filter_rst",		        	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"filter_set",			        array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"show_triggers",		    array(T_RDA_INT, O_OPT, null,	null,		null),
			"show_events",		        array(T_RDA_INT, O_OPT, P_SYS,	null,		null),
			"ack_status",			        array(T_RDA_INT, O_OPT, P_SYS,	null,		null),
			"show_severity",		    array(T_RDA_INT, O_OPT, P_SYS,	null,		null),
			"show_details",		        array(T_RDA_INT, O_OPT, null,	null,		null),
			"show_maintenance",	array(T_RDA_INT, O_OPT, null,	null,		null),
			"status_change_days",	array(T_RDA_INT, O_OPT, null,	BETWEEN(String.valueOf(1), String.valueOf(DAY_IN_YEAR * 2)), null,_("historymin")),
			"status_change",		    array(T_RDA_INT, O_OPT, null,	null,		null),
			"txt_select",			        array(T_RDA_STR, O_OPT, null,	null,		null),
			// ajax
			"favobj",				        array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",				        array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})"),
			"favstate",			        array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})")
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		if (!empty(get_request("groupid")) && !API.HostGroup(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"groupid").asLong())) {
			access_deny();
		}
		if (!empty(get_request("hostid")) && !API.Host(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"hostid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").$())) {
				CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.state", Nest.value(_REQUEST,"favstate").asInteger(), PROFILE_TYPE_INT);
			}
		}

		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}

	private CPageFilter pageFilter;
	
	@Override
	public void doPageFilter(SQLExecutor executor) {
		pageFilter = new CPageFilter(getIdentityBean(), executor, map(
			"groups", map(
				"monitored_hosts", true,
				"with_monitored_triggers", true
			),
			"hosts", map(
				"monitored_hosts", true,
				"with_monitored_triggers", true
			),
			"hostid", get_request("hostid", null),
			"groupid", get_request("groupid", null)
		));

		Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").$());
		Nest.value(_REQUEST,"hostid").$(pageFilter.$("hostid").$());
		
		if (isset(_REQUEST,"filter_rst")) {
			Nest.value(_REQUEST,"show_details").$(0);
			Nest.value(_REQUEST,"show_maintenance").$(1);
			Nest.value(_REQUEST,"show_triggers").$(TRIGGERS_OPTION_ONLYTRUE);
			Nest.value(_REQUEST,"show_events").$(EVENTS_OPTION_NOEVENT);
			Nest.value(_REQUEST,"ack_status").$(RDA_ACK_STS_ANY);
			Nest.value(_REQUEST,"show_severity").$(TRIGGER_SEVERITY_NOT_CLASSIFIED);
			Nest.value(_REQUEST,"txt_select").$("");
			Nest.value(_REQUEST,"status_change").$(0);
			Nest.value(_REQUEST,"status_change_days").$(14);
		}
	}

	@Override
	public void doAction(SQLExecutor executor) {
		Map config = select_config(getIdentityBean(), executor);
		// show triggers
		Nest.value(_REQUEST,"show_triggers").$(isset(_REQUEST,"show_triggers") ? Nest.value(_REQUEST,"show_triggers").asInteger() : TRIGGERS_OPTION_ONLYTRUE);

		// show events
		if (isset(_REQUEST,"show_events")) {
			if (EVENT_ACK_DISABLED.equals(Nest.value(config,"event_ack_enable").asString())) {
				if (!str_in_array(Nest.value(_REQUEST,"show_events").asInteger(), array(EVENTS_OPTION_NOEVENT, EVENTS_OPTION_ALL))) {
					Nest.value(_REQUEST,"show_events").$(EVENTS_OPTION_NOEVENT);
				}
			}
			CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.show_events", Nest.value(_REQUEST,"show_events").asInteger(), PROFILE_TYPE_INT);
		} else {
			Nest.value(_REQUEST,"show_events").$((EVENT_ACK_DISABLED.equals(Nest.value(config,"event_ack_enable").asString()))
					? EVENTS_OPTION_NOEVENT
					: CProfile.get(getIdentityBean(), executor, "web.tr_status.filter.show_events", EVENTS_OPTION_NOEVENT));
		}
		
		// show details
		if (isset(_REQUEST,"show_details")) {
			CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.show_details", Nest.value(_REQUEST,"show_details").asInteger(), PROFILE_TYPE_INT);
		} else {
			if (isset(_REQUEST,"filter_set")) {
				CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.show_details", 0, PROFILE_TYPE_INT);
				Nest.value(_REQUEST,"show_details").$(0);
			} else {
				Nest.value(_REQUEST,"show_details").$(CProfile.get(getIdentityBean(), executor, "web.tr_status.filter.show_details", 0));
			}
		}
		
		// show maintenance
		if (isset(_REQUEST,"show_maintenance")) {
			CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.show_maintenance", Nest.value(_REQUEST,"show_maintenance").asInteger(), PROFILE_TYPE_INT);
		} else {
			if (isset(_REQUEST,"filter_set")) {
				CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.show_maintenance", 0, PROFILE_TYPE_INT);
				Nest.value(_REQUEST,"show_maintenance").$(0);
			} else {
				Nest.value(_REQUEST,"show_maintenance").$(CProfile.get(getIdentityBean(), executor, "web.tr_status.filter.show_maintenance", 1));
			}
		}
		
		// show severity
		if (isset(_REQUEST,"show_severity")) {
			CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.show_severity", Nest.value(_REQUEST,"show_severity").asInteger(), PROFILE_TYPE_INT);
		} else {
			Nest.value(_REQUEST,"show_severity").$(CProfile.get(getIdentityBean(), executor, "web.tr_status.filter.show_severity", TRIGGER_SEVERITY_NOT_CLASSIFIED));
		}
		
		// status change
		if (isset(_REQUEST,"status_change")) {
			CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.status_change", Nest.value(_REQUEST,"status_change").asInteger(), PROFILE_TYPE_INT);
		} else {
			if (isset(_REQUEST,"filter_set")) {
				CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.status_change", 0, PROFILE_TYPE_INT);
				Nest.value(_REQUEST,"status_change").$(0);
			} else {
				Nest.value(_REQUEST,"status_change").$(CProfile.get(getIdentityBean(), executor, "web.tr_status.filter.status_change", 0));
			}
		}
		
		// status change days
		if (isset(_REQUEST,"status_change_days")) {
			int maxDays = DAY_IN_YEAR * 2;
			if (Nest.value(_REQUEST,"status_change_days").asInteger() > maxDays) {
				Nest.value(_REQUEST,"status_change_days").$(maxDays);
			}
			CProfile.update(getIdentityBean(), executor, "web.tr_status.filter.status_change_days", Nest.value(_REQUEST,"status_change_days").asInteger(), PROFILE_TYPE_INT);
		} else {
			Nest.value(_REQUEST,"status_change_days").$(CProfile.get(getIdentityBean(), executor, "web.tr_status.filter.status_change_days"));
			if (empty(Nest.value(_REQUEST,"status_change_days").asInteger())) {
				Nest.value(_REQUEST,"status_change_days").$(14);
			}
		}
		
		// ack status
		if (isset(_REQUEST,"ack_status")) {
			if (EVENT_ACK_DISABLED.equals(Nest.value(config,"event_ack_enable").asString())) {
				Nest.value(_REQUEST,"ack_status").$(RDA_ACK_STS_ANY);
			}
			CProfile.update(getIdentityBean(), executor,"web.tr_status.filter.ack_status", Nest.value(_REQUEST,"ack_status").asInteger(), PROFILE_TYPE_INT);
		} else {
			Nest.value(_REQUEST,"ack_status").$((EVENT_ACK_DISABLED.equals(Nest.value(config,"event_ack_enable").asString()))
					? RDA_ACK_STS_ANY
					: CProfile.get(getIdentityBean(), executor,"web.tr_status.filter.ack_status", RDA_ACK_STS_ANY));
		}

		// txt select
		if (isset(Nest.value(_REQUEST,"txt_select").$())) {
			CProfile.update(getIdentityBean(), executor,"web.tr_status.filter.txt_select", Nest.value(_REQUEST,"txt_select").asString(), PROFILE_TYPE_STR);
		} else {
			Nest.value(_REQUEST,"txt_select").$(CProfile.get(getIdentityBean(), executor,"web.tr_status.filter.txt_select", ""));
		}
		
		// Clean cookies
		if (get_request("show_events", 0) != CProfile.get(getIdentityBean(), executor,"web.tr_status.filter.show_events", 0)) {
			clearCookies(true);
		}

		/* Page sorting */
		validate_sort_and_sortorder(getIdentityBean(), executor, "lastchange", RDA_SORT_DOWN);
		
		/* Play sound  */
		boolean mute = Nest.as(CProfile.get(getIdentityBean(), executor,"web.tr_status.mute", 0)).asBoolean();
		if (isset(audio) && !mute) {
			play_sound(audio);
		}
		
		Integer showTriggers = Nest.value(_REQUEST,"show_triggers").asInteger();
		Integer showEvents = Nest.value(_REQUEST,"show_events").asInteger();
		Integer showSeverity = Nest.value(_REQUEST,"show_severity").asInteger();
		Integer ackStatus = Nest.value(_REQUEST,"ack_status").asInteger();
		
		CWidget triggerWidget = new CWidget();
		
		CForm rightForm = new CForm("get");
		rightForm.addItem(array(_("Group")+SPACE, pageFilter.getGroupsCB()));
		rightForm.addItem(array(SPACE+_("Host")+SPACE, pageFilter.getHostsCB()));

		triggerWidget.addHeader(new CDiv(_("EVENTS")+SPACE+"["+rda_date2str(_("d M Y H:i:s"))+"]", "bold"), rightForm);
		triggerWidget.addHeaderRowNumber();
		
		/* Filter */
		CFormTable filterForm = new CFormTable(null, null, "get");
		filterForm.setAttribute("name", "rda_filter");
		filterForm.setAttribute("id", "rda_filter");
		filterForm.addVar("fullscreen", Nest.value(_REQUEST,"fullscreen").asString());
		filterForm.addVar("groupid", Nest.value(_REQUEST,"groupid").asString());
		filterForm.addVar("hostid", Nest.value(_REQUEST,"hostid").asString());

		CComboBox statusComboBox = new CComboBox("show_triggers", showTriggers);
		statusComboBox.addItem(TRIGGERS_OPTION_ALL, _("Any"));
		statusComboBox.addItem(TRIGGERS_OPTION_ONLYTRUE, _("Problem"));
		filterForm.addRow(_("Triggers status"), statusComboBox);
		
		if (!empty(Nest.value(config,"event_ack_enable").$())) {
			CComboBox ackStatusComboBox = new CComboBox("ack_status", ackStatus);
			ackStatusComboBox.addItem(RDA_ACK_STS_ANY, _("Any"));
			ackStatusComboBox.addItem(RDA_ACK_STS_WITH_UNACK, _("With unacknowledged events"));
			ackStatusComboBox.addItem(RDA_ACK_STS_WITH_LAST_UNACK, _("With last event unacknowledged"));
			filterForm.addRow(_("Acknowledge status"), ackStatusComboBox);
		}
		
		CComboBox eventsComboBox = new CComboBox("show_events", Nest.value(_REQUEST,"show_events").$());
		eventsComboBox.addItem(EVENTS_OPTION_NOEVENT, _("Hide all"));
		eventsComboBox.addItem(EVENTS_OPTION_ALL, _("Show all")+" ("+ Nest.value(config,"event_expire").asInteger() +" "+((Nest.value(config,"event_expire").asInteger() > 1) ? _("Days") : _("Day"))+")");
		if (!empty(Nest.value(config,"event_ack_enable").$())) {
			eventsComboBox.addItem(EVENTS_OPTION_NOT_ACK, _("Show unacknowledged")+" ("+Nest.value(config,"event_expire").asInteger()+" "+((Nest.value(config,"event_expire").asInteger() > 1) ? _("Days") : _("Day"))+")");
		}
		filterForm.addRow(_("Events"), eventsComboBox);
		
		CComboBox severityComboBox = new CComboBox("show_severity", showSeverity);
		severityComboBox.addItems((CArray)map(
			TRIGGER_SEVERITY_NOT_CLASSIFIED, getSeverityCaption(getIdentityBean(), executor, TRIGGER_SEVERITY_NOT_CLASSIFIED),
			TRIGGER_SEVERITY_INFORMATION, getSeverityCaption(getIdentityBean(), executor, TRIGGER_SEVERITY_INFORMATION),
			TRIGGER_SEVERITY_WARNING, getSeverityCaption(getIdentityBean(), executor, TRIGGER_SEVERITY_WARNING),
			TRIGGER_SEVERITY_AVERAGE, getSeverityCaption(getIdentityBean(), executor, TRIGGER_SEVERITY_AVERAGE),
			TRIGGER_SEVERITY_HIGH, getSeverityCaption(getIdentityBean(), executor, TRIGGER_SEVERITY_HIGH),
			TRIGGER_SEVERITY_DISASTER, getSeverityCaption(getIdentityBean(), executor, TRIGGER_SEVERITY_DISASTER)
		));
		filterForm.addRow(_("Minimum trigger severity"), severityComboBox);
		
		CNumericBox statusChangeDays = new CNumericBox("status_change_days", Nest.value(_REQUEST,"status_change_days").asString(), 3, false, false, false);
		if (empty(Nest.value(_REQUEST,"status_change").$())) {
			statusChangeDays.setAttribute("disabled", "disabled");
		}
		statusChangeDays.addStyle("vertical-align: middle;");

		CCheckBox statusChangeCheckBox = new CCheckBox("status_change", Nest.value(_REQUEST,"status_change").asBoolean(), "javascript: this.checked ? $(\"status_change_days\").enable() : $(\"status_change_days\").disable()", "1");
		statusChangeCheckBox.addStyle("vertical-align: middle;");

		CSpan daysSpan = new CSpan(_("days"));
		daysSpan.addStyle("vertical-align: middle;");
		filterForm.addRow(_("Age less than"), array(statusChangeCheckBox, statusChangeDays, SPACE, daysSpan));
		filterForm.addRow(_("Show details"), new CCheckBox("show_details", Nest.value(_REQUEST,"show_details").asBoolean(), null, "1"));
		filterForm.addRow(_("Filter by name"), new CTextBox("txt_select", Nest.value(_REQUEST,"txt_select").asString(), 40));
		filterForm.addRow(_("Show hosts in maintenance"), new CCheckBox("show_maintenance", Nest.value(_REQUEST,"show_maintenance").asBoolean(), null, "1"));

		filterForm.addItemToBottomRow(new CSubmit("filter_set", _("GoFilter"), "chkbxRange.clearSelectedOnFilterChange();"));
		filterForm.addItemToBottomRow(new CSubmit("filter_rst", _("Reset"), "chkbxRange.clearSelectedOnFilterChange();","darkgray"));

		triggerWidget.addFlicker(filterForm, (Integer)CProfile.get(getIdentityBean(), executor,"web.tr_status.filter.state", 0));
		
		/* Form */
		if (!empty(Nest.value(_REQUEST,"fullscreen").$())) {
			CTriggersInfo triggerInfo = new CTriggersInfo(getIdentityBean(), executor, Nest.value(_REQUEST,"groupid").asLong(), Nest.value(_REQUEST,"hostid").asLong());
			triggerInfo.hideHeader();
			triggerInfo.show();
		}

		CForm triggerForm = new CForm("get", "acknow.action");
		triggerForm.setName("tr_status");
		triggerForm.addVar("backurl", Nest.value(page,"file").asString());
		
		/* Table */
		boolean showEventColumn = (Nest.value(config,"event_ack_enable").asBoolean() && Nest.value(_REQUEST,"show_events").asInteger() != EVENTS_OPTION_NOEVENT);

		String switcherName = "trigger_switchers";

		CCheckBox headerCheckBox = (showEventColumn)
			? new CCheckBox("all_events", false, "checkAll('"+triggerForm.getName()+"', 'all_events', 'events');")
			: new CCheckBox("all_triggers", false, "checkAll('"+triggerForm.getName()+"', 'all_triggers', 'triggers');");

		CDiv showHideAllDiv = null;
		if (showEvents != EVENTS_OPTION_NOEVENT) {
			showHideAllDiv  = new CDiv(SPACE, "filterclosed");
			showHideAllDiv.setAttribute("id", switcherName);
		}
		
		CTableInfo triggerTable = new CTableInfo(_("No triggers found."));
		triggerTable.setHeader(array(
			showHideAllDiv,
			Nest.value(config,"event_ack_enable").asBoolean() ? headerCheckBox : null,
			make_sorting_header(_("Severity"), "priority"),
			_("Status"),
			_("Info"),
			make_sorting_header(_("Last change"), "lastchange"),
			_("Age"),
			showEventColumn ? _("Duration") : null,
			Nest.value(config,"event_ack_enable").asBoolean() ? _("Acknowledged") : null,
			_("Host"),
			make_sorting_header(_("Name"), "description"),
			_("Comments")
		));
		
		// get triggers
		String sortfield = getPageSortField(getIdentityBean(), executor, "description");
		String sortorder = getPageSortOrder(getIdentityBean(), executor);
		CTriggerGet options = new CTriggerGet();
		options.setOutput(new String[]{"triggerid", sortfield});
		options.setMonitored(true);
		options.setSkipDependent(true);
		options.setSortfield(sortfield);
		options.setSortorder(sortorder);
		options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);

		if (pageFilter.$("hostsSelected").asBoolean()) {
			if (pageFilter.$("hostid").asInteger() > 0) {
				options.setHostIds(pageFilter.$("hostid").asLong());
			} else if (pageFilter.$("groupid").asInteger() > 0) {
				options.setGroupIds(pageFilter.$("groupid").asLong());
			}
		}
		
		if (!rda_empty(Nest.value(_REQUEST,"txt_select").$())) {
			Map<String, String> search = new HashMap();
			search.put("description", Nest.value(_REQUEST,"txt_select").asString());
			options.setSearch(search);
		}
		if (showTriggers == TRIGGERS_OPTION_ONLYTRUE) {
			options.setOnly_true(true);
		}
		if (ackStatus == RDA_ACK_STS_WITH_UNACK) {
			options.setWithUnacknowledgedEvents(true);
		}
		if (ackStatus == RDA_ACK_STS_WITH_LAST_UNACK) {
			options.setWithLastEventUnacknowledged(true);
		}
		if (showSeverity > TRIGGER_SEVERITY_NOT_CLASSIFIED) {
			options.setMinSeverity(showSeverity);
		}
		if (Nest.value(_REQUEST,"status_change").asBoolean()) {
			options.setLastChangeSince(time() - Nest.value(_REQUEST,"status_change_days").asInteger() * SEC_PER_DAY);
		}
		if (empty(get_request("show_maintenance",0))) {
			options.setMaintenance(false);
		}
		
		CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(options);
		CTable paging = getPagingLine(getIdentityBean(), executor, triggers);
		
		options = new CTriggerGet();
		options.setTriggerIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectHosts(new String[]{"hostid", "name", "maintenance_status", "maintenance_type", "maintenanceid", "description"});
		options.setSelectItems(new String[]{"itemid", "hostid", "key_", "name", "value_type"});
		options.setSelectDependencies(API_OUTPUT_EXTEND);
		options.setSelectLastEvent(true);
		options.setExpandDescription(true);
		options.setPreserveKeys(true);
		options.setSortfield(sortfield);
		options.setSortorder(sortorder);		
		triggers = API.Trigger(getIdentityBean(), executor).get(options);
		Long[] triggerIds = rda_objectValues(triggers, "triggerid").valuesAsLong();
		
		options = new CTriggerGet();
		options.setTriggerIds(triggerIds);
		options.setOutput(new String[]{"triggerid"});
		options.setEditable(true);
		options.setPreserveKeys(true);
		CArray<Map> triggerEditable = API.Trigger(getIdentityBean(), executor).get(options);
		
		// get events
		CEventGet eget = null;
		Long tnum = null;
		Map trigger = null;
		if (Nest.value(config,"event_ack_enable").asBoolean()) {
			// get all unacknowledged events, if trigger has unacknowledged even => it has events
			eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_TRIGGERS);
			eget.setObject(EVENT_OBJECT_TRIGGER);
			eget.setCountOutput(true);
			eget.setGroupCount(true);
			eget.setObjectIds(triggerIds);
			Map<String, Object[]> filter = new HashMap();
			filter.put("acknowledged", new String[]{"0"});
			filter.put("value", new Object[]{TRIGGER_VALUE_TRUE});
			eget.setFilter(filter);
			CArray<Map> eventCounts = API.Event(getIdentityBean(), executor).get(eget);
			for(Map eventCount:eventCounts) {
				triggers.get(eventCount.get("objectid")).put("hasEvents", true);
				triggers.get(eventCount.get("objectid")).put("event_count", eventCount.get("rowscount"));
			}

			// gather ids of triggers which don't have unack. events
			CArray triggerIdsWithoutUnackEvents = array();
			for(Entry<Object, Map> e:triggers.entrySet()) {
				tnum = (Long)e.getKey();
				trigger = e.getValue();
				if (!isset(trigger,"hasEvents")) {
					triggerIdsWithoutUnackEvents.add(Nest.value(trigger,"triggerid").$());
				}
				if (!isset(trigger,"event_count")) {
					triggers.get(tnum).put("event_count", 0);
				}
			}
			if (!empty(triggerIdsWithoutUnackEvents)) {
				// for triggers without unack. events we try to select any event
				eget = new CEventGet();
				eget.setSource(EVENT_SOURCE_TRIGGERS);
				eget.setObject(EVENT_OBJECT_TRIGGER);
				eget.setCountOutput(true);
				eget.setGroupCount(true);
				eget.setObjectIds(triggerIdsWithoutUnackEvents.valuesAsLong());
				CArray<Map> allEventCounts = API.Event(getIdentityBean(), executor).get(eget);
				allEventCounts = rda_toHash(allEventCounts, "objectid");

				for(Entry<Object, Map> e:triggers.entrySet()) {
					tnum = (Long)e.getKey();
					trigger = e.getValue();
					if (!isset(trigger,"hasEvents")) {
						triggers.get(tnum).put("hasEvents", isset(allEventCounts,trigger.get("triggerid")));
					}
				}
			}
		}

		if (showEvents != EVENTS_OPTION_NOEVENT) {
			eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_TRIGGERS);
			eget.setObject(EVENT_OBJECT_TRIGGER);
			eget.setObjectIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
			eget.setOutput(API_OUTPUT_EXTEND);
			eget.setSelectAcknowledges(API_OUTPUT_COUNT);
			eget.setTimeFrom(time() - Nest.value(config,"event_expire").asInteger() * SEC_PER_DAY);
			eget.setTimeTill(new Long(time()));
			eget.setSortfield(new String[]{"clock", "eventid"});
			eget.setSortorder(RDA_SORT_DOWN);
			switch (showEvents) {
				case EVENTS_OPTION_ALL:
					break;
				case EVENTS_OPTION_NOT_ACK:
					eget.setAcknowledged(false);
					eget.setValue(String.valueOf(TRIGGER_VALUE_TRUE));
					break;
			}
			CArray<Map> events = API.Event(getIdentityBean(), executor).get(eget);

			for(Map event:events) {
				Nest.value(triggers.get(event.get("objectid")),"events").$s(true).add(event);
			}
		}
		
		// get host ids
		CArray hostIds = array();
		for(Entry<Object, Map> e:triggers.entrySet()) {
			tnum = (Long)e.getKey();
			trigger = e.getValue();
			if(trigger.containsKey("hosts")){
				for(Map host:(CArray<Map>)Nest.value(trigger,"hosts").asCArray()) {
					hostIds.add(Nest.value(host,"hostid").$());
				}
			}
		}

		// get hosts
		CHostGet hget = new CHostGet();
		hget.setHostIds(hostIds.valuesAsLong());
		hget.setPreserveKeys(true);
		hget.setSelectScreens(API_OUTPUT_COUNT);
		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hget);
		
		// get host scripts
		CArray<CArray<Map>> scriptsByHosts = API.Script(getIdentityBean(), executor).getScriptsByHosts(hostIds.valuesAsLong());
		
		SqlBuilder query = new SqlBuilder();
		query.select.put("triggerid_down");
		query.select.put("triggerid_up");
		query.from.put("trigger_depends");
		query.where.dbConditionInt("triggerid_up", triggerIds);
		String sql = query.createSelectQueryFromParts();
		Map params = query.getNamedParams();
		CArray<Map> dbTriggerDependencies = DBselect(executor, sql, params);
		CArray triggerIdsDown = array();
		for (Map row:dbTriggerDependencies) {
			if(!triggerIdsDown.containsKey(row.get("triggerid_up"))){
				triggerIdsDown.put(row.get("triggerid_up"), new CArray());
			}
			triggerIdsDown.put(row.get("triggerid_up"), Nest.value(row,"triggerid_down").$());
		}
		
		CArray usedHosts = null, triggerItems = null;
		int usedHostCount = 0;
		for(Map _trigger: triggers) {
			trigger = _trigger;
			usedHosts = array();
			
			if(trigger.containsKey("hosts")){
				for(Map host:(CArray<Map>)Nest.value(trigger,"hosts").asCArray()) {
					usedHosts.put(host.get("hostid"), Nest.value(host,"name").$());
				}
			}
			usedHostCount = count(usedHosts);
			triggerItems = array();
			
			Nest.value(trigger,"items").$(CMacrosResolverHelper.resolveItemNames(getIdentityBean(), executor, Nest.value(trigger,"items").asCArray()));
			
			for(Map item:(CArray<Map>)Nest.value(trigger,"items").asCArray()) {
				triggerItems.add(map(
					"name", (usedHostCount > 1) ? usedHosts.get(item.get("hostid"))+NAME_DELIMITER+Nest.value(item,"name_expanded").asString() : Nest.value(item,"name_expanded").asString(),
					"params" , map(
						"itemid", Nest.value(item,"itemid").$(),
						"action", in_array(Nest.value(item,"value_type").$(), array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64))
							? "showgraph" : "showvalues"
					)
				));
			}
			
			Object description = new CSpan(Nest.value(trigger,"description").$(), "link_menu");
			((CSpan)description).setMenuPopup(getMenuPopupTrigger(trigger, triggerItems));

			if (Nest.value(_REQUEST,"show_details").asBoolean()) {
				description = array(description, BR(), explode_exp(this.getIdentityBean(), executor, Nest.value(trigger,"expression").asString(), true, true));
			}

			if (!empty(Nest.value(trigger,"dependencies").$())) {
				CTableInfo dependenciesTable = new CTableInfo();
				dependenciesTable.setAttribute("style", "width: 200px;");
				dependenciesTable.addRow(bold(_("Depends on")+NAME_DELIMITER));

				for(Map dependency:(CArray<Map>)Nest.value(trigger,"dependencies").asCArray()) {
					dependenciesTable.addRow(" - "+CMacrosResolverHelper.resolveTriggerNameById(getIdentityBean(), executor, Nest.value(dependency,"triggerid").asLong()));
				}

				CImg img = new CImg("images/general/arrow_down2.png", "DEP_UP");
				img.setAttribute("style", "vertical-align: middle; border: 0px;");
				img.setHint(dependenciesTable);

				description = array(img, SPACE, description);
			}
			
			boolean dependency = false;
			CTableInfo dependenciesTable = new CTableInfo();
			dependenciesTable.setAttribute("style", "width: 200px;");
			dependenciesTable.addRow(bold(_("Dependent")+NAME_DELIMITER));
			if (!empty(triggerIdsDown.get(trigger.get("triggerid")))) {
				CArray<Map> depTriggers = CMacrosResolverHelper.resolveTriggerNameByIds(this.getIdentityBean(), executor, Nest.value(triggerIdsDown,trigger.get("triggerid")).asCArray());

				for(Map depTrigger: depTriggers) {
					dependenciesTable.addRow(SPACE+"-"+SPACE+Nest.value(depTrigger,"description").$());
					dependency = true;
				}
			}

			if (dependency) {
				CImg img = new CImg("images/general/arrow_up2.png", "DEP_UP");
				img.setAttribute("style", "vertical-align: middle; border: 0px;");
				img.setHint(dependenciesTable);

				description = array(img, SPACE, description);
			}
			CSpan triggerDescription = new CSpan(description, "pointer");
			
			// host js menu
			CArray hostList = array();
			
			Iterator<Map> trigger_hosts = Nest.value(trigger,"hosts").asCArray().iterator();
			
			while(trigger_hosts.hasNext()) {
				Map triggerHost = trigger_hosts.next();
				// fetch scripts for the host js menu
				CArray scripts = array();
				if (isset(scriptsByHosts,triggerHost.get("hostid"))) {
					for(Map script: (CArray<Map>)scriptsByHosts.get(triggerHost.get("hostid"))) {
						scripts.add(script);
					}
				}

				CSpan hostName = new CSpan(Nest.value(triggerHost,"name").$(), "link_menu");
				hostName.setMenuPopup(getMenuPopupHost(Nest.value(hosts,triggerHost.get("hostid")).asCArray(), scripts));

				CDiv hostDiv = new CDiv(hostName);

				// add maintenance icon with hint if host is in maintenance
				if (Nest.value(triggerHost,"maintenance_status").asBoolean()) {
					CDiv maintenanceIcon = new CDiv(null, "icon-maintenance-inline");

					CMaintenanceGet moptions = new CMaintenanceGet();
					moptions.setMaintenanceIds(Nest.value(triggerHost,"maintenanceid").asLong());
					moptions.setOutput(API_OUTPUT_EXTEND);
					moptions.setLimit(1);
					CArray<Map> maintenances = API.Maintenance(getIdentityBean(), executor).get(moptions);

					Map maintenance;
					if (!empty(maintenance = reset(maintenances))) {
						String hint = maintenance.get("name")+" ["+(Nest.value(triggerHost,"maintenance_type").asBoolean()
							? _("Maintenance without data collection")
							: _("Maintenance with data collection"))+"]";

						if (isset(Nest.value(maintenance,"description").$())) {
							// double quotes mandatory
							hint += "\n"+Nest.value(maintenance,"description").$();
						}

						maintenanceIcon.setHint(hint);
						maintenanceIcon.addClass("pointer");
					}

					hostDiv.addItem(maintenanceIcon);
				}

				// add comma after hosts, except last
				if (trigger_hosts.hasNext()) {
					hostDiv.addItem(","+SPACE);
				}

				hostList.add(hostDiv);
			}
			
			// host
			CCol hostColumn = new CCol(hostList);
			hostColumn.addStyle("white-space: normal;");
			
			// status
			CSpan statusSpan = new CSpan(trigger_value2str(Nest.value(trigger,"value").asInteger()));

			// add colors and blinking to span depending on configuration and trigger parameters
			addTriggerValueStyle(
				getIdentityBean(), 
				executor, 
				statusSpan,
				Nest.value(trigger,"value").asInteger(),
				Nest.value(trigger,"lastchange").asInteger(),
				Nest.value(config,"event_ack_enable").asBoolean() ? (Nest.value(trigger,"event_count").asInteger() == 0) : false
			);
			
			String lastChangeDate = rda_date2str(_("d M Y H:i:s"), Nest.value(trigger,"lastchange").asLong());
			Object lastChange = empty(Nest.value(trigger,"lastchange").$())
				? lastChangeDate
				: new CLink(lastChangeDate,
					"events.action?triggerid="+trigger.get("triggerid")+"&stime="+date(TIMESTAMP_FORMAT, Nest.value(trigger,"lastchange").asLong())+
						"&period="+RDA_PERIOD_DEFAULT+"&source="+EVENT_SOURCE_TRIGGERS
				);

			// acknowledge
			CCol ackColumn = null;;
			if (Nest.value(config,"event_ack_enable").asBoolean()) {
				if (Nest.value(trigger,"hasEvents").asBoolean()) {
					if (!empty(Nest.value(trigger,"event_count").$())) {
						ackColumn  = new CCol(array(
							new CLink(
								_("Acknowledge"),
								"acknow.action?"+
									"triggers[]="+trigger.get("triggerid")+
									"&backurl="+Nest.value(page,"file").asString(),
								"on"
							), " ("+trigger.get("event_count")+")"
						));
					} else {
						ackColumn = new CCol(
							new CLink(
								_("Acknowledged"),
								"acknow.action?"+
									"eventid="+Nest.value(trigger,"lastEvent","eventid").$()+
									"&triggerid="+Nest.value(trigger,"lastEvent","objectid").$()+
									"&backurl="+Nest.value(page,"file").$(),
								"off"
						));
					}
				} else {
					ackColumn = new CCol(_("No events"), "unknown");
				}
			}
			
			Object openOrCloseDiv = null;
			// open or close
			if (showEvents != EVENTS_OPTION_NOEVENT && !empty(Nest.value(trigger,"events").$())) {
				openOrCloseDiv = new CDiv(SPACE, "filterclosed");
				((CDiv)openOrCloseDiv).setAttribute("data-switcherid", Nest.value(trigger,"triggerid").$());
			} else if (showEvents == EVENTS_OPTION_NOEVENT) {
				openOrCloseDiv = null;
			} else {
				openOrCloseDiv = SPACE;
			}
			
			// severity
			CCol severityColumn = getSeverityCell(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger(), null, !Nest.value(trigger,"value").asBoolean());
			if (showEventColumn) {
				severityColumn.setColSpan(2);
			}

			// unknown triggers
			Object unknown = SPACE;
			if (Nest.value(trigger,"state").asInteger() == TRIGGER_STATE_UNKNOWN) {
				unknown = new CDiv(SPACE, "status_icon iconunknown");
				((CDiv)unknown).setHint(Nest.value(trigger,"error").$(), "", "on");
			}

			// comments
			Object comments = null;
			if (isset(triggerEditable,trigger.get("triggerid"))) {
				comments  = new CLink(rda_empty(Nest.value(trigger,"comments").$()) ? _("Add") : _("Show"), "tr_comments.action?triggerid="+Nest.value(trigger,"triggerid").$());
			} else {
				comments = rda_empty(Nest.value(trigger,"comments").$())
					? new CSpan("-")
					: new CLink(_("Show"), "tr_comments.action?triggerid="+Nest.value(trigger,"triggerid").$());
			}
			
			triggerTable.addRow(array(
					openOrCloseDiv,
					Nest.value(config,"event_ack_enable").asBoolean() ?
						(showEventColumn ? null : new CCheckBox("triggers["+trigger.get("triggerid")+"]", false, null, Nest.value(trigger,"triggerid").asString())) : null,
					severityColumn,
					statusSpan,
					unknown,
					lastChange,
					empty(Nest.value(trigger,"lastchange").$()) ? "-" : rda_date2age(Nest.value(trigger,"lastchange").asLong()),
					showEventColumn ? SPACE : null,
					ackColumn,
					hostColumn,
					triggerDescription,
					comments
				), "even_row");
			
			if (showEvents != EVENTS_OPTION_NOEVENT && !empty(Nest.value(trigger,"events").$())) {
				int i = 1;
				for(Entry e:(Set<Entry>)Nest.value(trigger,"events").asCArray().entrySet()) {
					i++;
					long _enum = (Long)e.getKey();
					Map event = (Map)e.getValue();
					CSpan eventStatusSpan = new CSpan(trigger_value2str(Nest.value(event,"value").asInteger()));

					// add colors and blinking to span depending on configuration and trigger parameters
					addTriggerValueStyle(
						getIdentityBean(), 
						executor, 
						eventStatusSpan,
						Nest.value(event,"value").asInteger(),
						Nest.value(event,"clock").asInteger(),
						Nest.value(event,"acknowledged").asBoolean()
					);

					CCol estatusSpan = new CCol(eventStatusSpan);
					estatusSpan.setColSpan(2);

					Object ack = getEventAckState(getIdentityBean(), executor, event, true);

					Object ackCheckBox = (Nest.value(event,"acknowledged").asInteger() == 0 && Nest.value(event,"value").asInteger() == TRIGGER_VALUE_TRUE)
						? new CCheckBox("events["+Nest.value(event,"eventid").asString()+"]", false, null, Nest.value(event,"eventid").asString())
						: SPACE;

					CLink clock = new CLink(rda_date2str(_("d M Y H:i:s"), Nest.value(event,"clock").asLong()),
						"tr_events.action?triggerid="+Nest.value(trigger,"triggerid").asString()+"&eventid="+Nest.value(event,"eventid").asString());

					Long nextClock = isset(Nest.value(trigger, "events", String.valueOf(_enum - 1)).$()) ? Nest.value(trigger, "events", String.valueOf(_enum - 1),"clock").asLong() : time();

					CCol emptyColumn = new CCol(SPACE);
					emptyColumn.setColSpan(3);
					CCol ackCheckBoxColumn = new CCol(ackCheckBox);
					ackCheckBoxColumn.setColSpan(2);

					CRow row = new CRow(array(
						SPACE,
						Nest.value(config,"event_ack_enable").asBoolean() ? ackCheckBoxColumn : null,
						estatusSpan,
						clock,
						rda_date2age(Nest.value(event,"clock").asLong()),
						rda_date2age(nextClock, Nest.value(event,"clock").asLong()),
						(Nest.value(config,"event_ack_enable").asBoolean()) ? ack : null,
						emptyColumn
					), "odd_row");
					row.setAttribute("data-parentid", Nest.value(trigger,"triggerid").$());
					row.addStyle("display: none;");
					triggerTable.addRow(row);

					if (i > Nest.value(config,"event_show_max").asInteger()) {
						break;
					}
				}
			}
		}
		
		/* Go buttons */
		CTable footer = null;

		triggerForm.addItem(array(paging, triggerTable, paging, footer));
		triggerWidget.addItem(triggerForm);
		triggerWidget.show();
		rda_add_post_js("jqBlink.blink();");
		rda_add_post_js("var switcher = new CSwitcher(\""+switcherName+"\");");
	}
}
