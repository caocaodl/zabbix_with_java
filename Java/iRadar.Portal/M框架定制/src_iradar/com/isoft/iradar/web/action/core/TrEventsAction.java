package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.AcknowUtil.makeAckTab;
import static com.isoft.iradar.inc.ActionsUtil.get_action_cmds_for_event;
import static com.isoft.iradar.inc.ActionsUtil.get_action_msgs_for_event;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.EventsUtil.make_event_details;
import static com.isoft.iradar.inc.EventsUtil.make_small_eventlist;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.make_trigger_details;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CUIWidget;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class TrEventsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Event details"));
		page("file", "tr_events.action");
		page("hist_arg", new String[] {"triggerid", "eventid"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
		page("css", new String[] {"lessor/eventcenter/trevents.css"});
		define("PAGE_SIZE", 100);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"triggerid",	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		PAGE_TYPE_HTML+"=="+Nest.value(page,"type").$()),
			"eventid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		PAGE_TYPE_HTML+"=="+Nest.value(page,"type").$()),
			"fullscreen",	array(T_RDA_INT, O_OPT,	P_SYS,	IN("0,1"),	null),
			// actions
			"save",			array(T_RDA_STR,O_OPT,	P_ACT|P_SYS, null,	null),
			"cancel",		array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	IN("'filter','hat'"), null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,  NOT_EMPTY,	"isset({favobj})"),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})")
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		/* Ajax */
		if (isset(_REQUEST,"favobj")) {
			if ("hat".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.tr_events.hats."+Nest.value(_REQUEST,"favref").asString()+".state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
		}

		if (PAGE_TYPE_JS == Nest.value(page,"type").asInteger() || PAGE_TYPE_HTML_BLOCK == Nest.value(page,"type").asInteger()) {
//			require_once dirname(__FILE__)."/include/page_footer.php";
//			exit();
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		// get triggers
		CTriggerGet toptions = new CTriggerGet();
		toptions.setTriggerIds(Nest.value(_REQUEST,"triggerid").asLong());
		toptions.setExpandData(true);
		toptions.setSelectHosts(API_OUTPUT_EXTEND);
		toptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);
		if (empty(triggers)) {
			access_deny();
		}
		Map trigger = reset(triggers);

		// get events
		CEventGet eoptions = new CEventGet();
		eoptions.setSource(EVENT_SOURCE_TRIGGERS);
		eoptions.setObject(EVENT_OBJECT_TRIGGER);
		eoptions.setEventIds(Nest.value(_REQUEST,"eventid").asLong());
		eoptions.setObjectIds(Nest.value(_REQUEST,"triggerid").asLong());
		eoptions.setSelectAlerts(API_OUTPUT_EXTEND);
		eoptions.setSelectAcknowledges(API_OUTPUT_EXTEND);
		eoptions.setOutput(API_OUTPUT_EXTEND);
		eoptions.setSelectHosts(API_OUTPUT_EXTEND);
		CArray<Map> events = API.Event(getIdentityBean(), executor).get(eoptions);
		Map event = reset(events);

		//border-bottom-style
		//ui-widget-content
		
		CWidget tr_event_wdgt = new CWidget();
		tr_event_wdgt.setClass("header");

		// Main widget header
		CArray<String> text = array(_("EVENT")+": \""+CMacrosResolverHelper.resolveTriggerName(this.getIdentityBean(), executor, trigger)+"\"");

		CIcon fs_icon = get_icon(getIdentityBean(), executor, "fullscreen", map("fullscreen", Nest.value(_REQUEST,"fullscreen").$()));
		tr_event_wdgt.addHeader(text, fs_icon);

		CArray detail_col = array();

		// tr details
		CUIWidget triggerDetails = new CUIWidget("hat_triggerdetails", make_trigger_details(this.getIdentityBean(), executor, trigger));
		triggerDetails.setHeader(_("Event source details"));
		detail_col.add(triggerDetails);

		// event details
		CArray acknowledges = Nest.value(event, "acknowledges").asCArray();
		CUIWidget eventDetails = new CUIWidget("hat_eventdetails", make_event_details(this.getIdentityBean(), executor, event, trigger));
		eventDetails.setHeader(_("Event details"));
		detail_col.add(eventDetails);
		Nest.value(event, "acknowledges").$(acknowledges);
		
		Map<String, Object> config = select_config(getIdentityBean(), executor);

		// if acknowledges are not disabled in configuration, let's show them
		if (Nest.value(config,"event_ack_enable").asBoolean()) {
			CUIWidget event_ack = new CUIWidget("hat_eventack", makeAckTab(event), Nest.as(CProfile.get(getIdentityBean(), executor,"web.tr_events.hats.hat_eventack.state", 1)).asInteger());
			event_ack.setHeader(_("Acknowledges Log"));
			detail_col.add(event_ack);
		}

		// event cmd actions
		CUIWidget actions_cmd = new CUIWidget("hat_eventactionmcmds", get_action_cmds_for_event(event), Nest.as(CProfile.get(getIdentityBean(), executor,"web.tr_events.hats.hat_eventactioncmds.state", 1)).asInteger());
		actions_cmd.setHeader(_("Command actions"));
		detail_col.add(actions_cmd);
		
		// event sms actions
		CUIWidget actions_sms = new CUIWidget("hat_eventactionmsgs", get_action_msgs_for_event(event), Nest.as(CProfile.get(getIdentityBean(), executor, "web.tr_events.hats.hat_eventactionmsgs.state", 1)).asInteger());
		actions_sms.setHeader(_("Message actions"));
		detail_col.add(actions_sms);

		// event history
		CUIWidget events_histry = new CUIWidget("hat_eventlist", make_small_eventlist(this.getIdentityBean(), executor,event), Nest.as(CProfile.get(getIdentityBean(), executor,"web.tr_events.hats.hat_eventlist.state", 1)).asInteger());
		events_histry.setHeader(_("Event list [previous 20]"));
		detail_col.add(events_histry);

		CDiv detailDiv = new CDiv(detail_col, "column");

		CTable ieTab = new CTable();
		ieTab.setAttribute("class", "tableinfo");
		ieTab.addRow(detailDiv, "top");
		
		tr_event_wdgt.addItem(ieTab);
		tr_event_wdgt.show();
	}
}
