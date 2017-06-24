package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CLabel;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CAdministrationGeneralHousekeeperEdit extends CViewSegment {

	@Override
	public CForm doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/administration.general.housekeeper.edit.js");

		CFormList houseKeeperTab = new CFormList("scriptsTab");

		// events and alerts
		CTable eventAlertTab = new CTable(null, "formElementTable");
		CCheckBox eventsMode = new CCheckBox("hk_events_mode", Nest.value(data,"config","hk_events_mode").asBoolean(), null, 1);
		eventAlertTab.addRow(array(
			new CLabel(_("Enable internal housekeeping"), "hk_events_mode"),
			eventsMode
		));

		CNumericBox houseKeeperEventsTrigger = new CNumericBox("hk_events_trigger", Nest.value(data,"config","hk_events_trigger").asString(), 5);
//		CNumericBox houseKeeperEventsInternal = new CNumericBox("hk_events_internal", Nest.value(data,"config","hk_events_internal").asString(), 5);
		CNumericBox houseKeeperEventsDiscovery = new CNumericBox("hk_events_discovery", Nest.value(data,"config","hk_events_discovery").asString(), 5);
//		CNumericBox houseKeeperEventsAutoreg = new CNumericBox("hk_events_autoreg", Nest.value(data,"config","hk_events_autoreg").asString(), 5);
		if (!Nest.value(data,"config","hk_events_mode").asBoolean()) {
			houseKeeperEventsTrigger.setAttribute("disabled", "disabled");
			//houseKeeperEventsInternal.setAttribute("disabled", "disabled");
			houseKeeperEventsDiscovery.setAttribute("disabled", "disabled");
			//houseKeeperEventsAutoreg.setAttribute("disabled", "disabled");
		}
		eventAlertTab.addRow(array(
			new CLabel(_("Trigger data storage period (in days)"), "hk_events_trigger"),
			houseKeeperEventsTrigger
		));
//		eventAlertTab.addRow(array(
//			new CLabel(_("Internal data storage period (in days)"), "hk_events_internal"),
//			houseKeeperEventsInternal
//		));
		eventAlertTab.addRow(array(
			new CLabel(_("Network discovery data storage period (in days)"), "hk_events_discovery"),
			houseKeeperEventsDiscovery
		));
//		eventAlertTab.addRow(array(
//			new CLabel(_("Auto-registration data storage period (in days)"), "hk_events_autoreg"),
//			houseKeeperEventsAutoreg
//		));
		eventAlertTab.addClass("border_dotted objectgroup element-row element-row-first");
		houseKeeperTab.addRow(_("Events and alerts"), new CDiv(eventAlertTab));


		// IT services
		/*CTable itServicesTab = new CTable(null, "formElementTable");

		itServicesTab.addRow(array(
			new CLabel(_("Enable internal housekeeping"), "hk_services_mode"),
			new CCheckBox("hk_services_mode", Nest.value(data,"config","hk_services_mode").asBoolean(), null, 1)
		));

		CNumericBox houseKeeperServicesMode = new CNumericBox("hk_services", Nest.value(data,"config","hk_services").asString(), 5);
		if (!Nest.value(data,"config","hk_services_mode").asBoolean()) {
			houseKeeperServicesMode.setAttribute("disabled", "disabled");
		}
		itServicesTab.addRow(array(
			new CLabel(_("Data storage period (in days)"), "hk_services"),
			houseKeeperServicesMode
		));
		itServicesTab.addClass("border_dotted objectgroup element-row");
		houseKeeperTab.addRow(_("IT services"), new CDiv(itServicesTab));*/

		// audit
		CTable auditTab = new CTable(null, "formElementTable");

		auditTab.addRow(array(
			new CLabel(_("Enable internal housekeeping"), "hk_audit_mode"),
			new CCheckBox("hk_audit_mode", Nest.value(data,"config","hk_audit_mode").asBoolean(), null, 1)
		));

		CNumericBox houseKeeperAuditMode = new CNumericBox("hk_audit", Nest.value(data,"config","hk_audit").asString(), 5);
		if (!Nest.value(data,"config","hk_audit_mode").asBoolean()) {
			houseKeeperAuditMode.setAttribute("disabled", "disabled");
		}
		auditTab.addRow(array(
			new CLabel(_("Data storage period (in days)"), "hk_audit"),
			houseKeeperAuditMode
		));
		auditTab.addClass("border_dotted objectgroup element-row");
		houseKeeperTab.addRow(_("Audit"), new CDiv(auditTab));

//		// user session
//		CTable userSessionTab = new CTable(null, "formElementTable");
//
//		userSessionTab.addRow(array(
//			new CLabel(_("Enable internal housekeeping"), "hk_sessions_mode"),
//			new CCheckBox("hk_sessions_mode", Nest.value(data,"config","hk_sessions_mode").asBoolean(), null, 1)
//		));
//
//		CNumericBox houseKeeperSessionsMode = new CNumericBox("hk_sessions", Nest.value(data,"config","hk_sessions").asString(), 5);
//		if (!Nest.value(data,"config","hk_sessions_mode").asBoolean()) {
//			houseKeeperSessionsMode.setAttribute("disabled", "disabled");
//		}
//		userSessionTab.addRow(array(
//			new CLabel(_("Data storage period (in days)"), "hk_sessions"),
//			houseKeeperSessionsMode
//		));
//		userSessionTab.addClass("border_dotted objectgroup element-row");
//		houseKeeperTab.addRow(_("User sessions"), new CDiv(userSessionTab));

		// history
		CTable histortTab = new CTable(null, "formElementTable");

		histortTab.addRow(array(
			new CLabel(_("Enable internal housekeeping"), "hk_history_mode"),
			new CCheckBox("hk_history_mode", Nest.value(data,"config","hk_history_mode").asBoolean(), null, 1)
		));
		CCheckBox houseKeeperHistoryGlobal = new CCheckBox("hk_history_global", Nest.value(data,"config","hk_history_global").asBoolean(), null, 1);
		CNumericBox houseKeeperHistoryModeGlobal = new CNumericBox("hk_history", Nest.value(data,"config","hk_history").asString(), 5);
/*		if (!Nest.value(data,"config","hk_history_global").asBoolean()) {
			houseKeeperHistoryModeGlobal.setAttribute("disabled", "disabled");
		}*/
		histortTab.addRow(array(new CLabel(_("Override item history period"),
			"hk_history_global"), houseKeeperHistoryGlobal));
		histortTab.addRow(array(
			new CLabel(_("Data storage period (in days)"), "hk_history"),
			houseKeeperHistoryModeGlobal
		));
		histortTab.addClass("border_dotted objectgroup element-row");
		houseKeeperTab.addRow(_("History"), new CDiv(histortTab));

		// trend
		CTable trendTab = new CTable(null, "formElementTable");

		trendTab.addRow(array(
			new CLabel(_("Enable internal housekeeping"), "hk_trends_mode"),
			new CCheckBox("hk_trends_mode", Nest.value(data,"config","hk_trends_mode").asBoolean(), null, 1)
		));
		CCheckBox houseKeeperTrendGlobal = new CCheckBox("hk_trends_global", Nest.value(data,"config","hk_trends_global").asBoolean(), null, 1);
		CNumericBox houseKeeperTrendModeGlobal = new CNumericBox("hk_trends", Nest.value(data,"config","hk_trends").asString(), 5);
/*		if (!Nest.value(data,"config","hk_trends_global").asBoolean()) {
			houseKeeperTrendModeGlobal.setAttribute("disabled", "disabled");
		}*/
		trendTab.addRow(array(new CLabel(_("Override item trend period"),
			"hk_trends_global"), houseKeeperTrendGlobal));
		trendTab.addRow(array(
			new CLabel(_("Data storage period (in days)"), "hk_trends"),
			houseKeeperTrendModeGlobal
		));
		trendTab.addClass("border_dotted objectgroup element-row");
		houseKeeperTab.addRow(_("Trends"), new CDiv(trendTab));

		CTabView houseKeeperView = new CTabView();
		houseKeeperView.addTab("houseKeeper", _("Housekeeping"), houseKeeperTab);

		CForm houseKeeperForm = new CForm();
		houseKeeperForm.setName("houseKeeperForm");
		houseKeeperForm.addVar("form_refresh", Nest.value(data,"form_refresh").asInteger() + 1);
		houseKeeperForm.addItem(houseKeeperView);
		houseKeeperForm.addItem(makeFormFooter(new CSubmit("save", _("Save")), new CButton("resetDefaults", _("Reset defaults"))));

		return houseKeeperForm;
	}

}
