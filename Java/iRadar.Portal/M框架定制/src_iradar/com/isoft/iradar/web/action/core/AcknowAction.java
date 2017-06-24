package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.api.API.Call;
import static com.isoft.iradar.inc.AuditUtil.add_audit;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.AUDIT_ACTION_UPDATE;
import static com.isoft.iradar.inc.Defines.AUDIT_RESOURCE_TRIGGER;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_NOT_ACKNOWLEDGED;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_BIG_WIDTH;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_STANDARD_ROWS;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_nl2br;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toObject;
import static com.isoft.iradar.inc.FuncsUtil.show_message;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.HtmlUtil.show_table_header;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.iradar.inc.UsersUtil.getUserFullname;
import static com.isoft.iradar.inc.AcknowUtil.getLocalUserFullname;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.inc.ViewsUtil.redirect;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class AcknowAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Acknowledges"));
		page("file", "acknow.action");
		page("hist_arg", new String[] {"eventid"});
		page("css", new String[] {"test.css"});
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		Nest.value(_REQUEST,"go").$(get_request("go", null));
		boolean bulk = ("bulkacknowledge".equals(Nest.value(_REQUEST,"go").asString()));

		//			VAR		TYPE	OPTIONAL FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"eventid",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggers",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggerid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"screenid",			array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"events",				array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"message",			array(T_RDA_STR, O_OPT, null,	bulk ? null : NOT_EMPTY, "isset({save})||isset({saveandreturn})", _("event notes")),
			"backurl",				array(T_RDA_STR, O_OPT, null,	null,		null),
			// actions
			"go",						array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"saveandreturn",	array(T_RDA_STR, O_OPT, P_ACT|P_SYS, null,	null),
			"save",					array(T_RDA_STR, O_OPT, P_ACT|P_SYS, null,	null),
			"cancel",				array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null)
		);
		check_fields(getIdentityBean(), fields);

		Nest.value(_REQUEST,"backurl").$(get_request("backurl", "tr_status.action"));
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Redirect */
		if (isset(_REQUEST,"cancel")) {
			if (in_array(Nest.value(_REQUEST,"backurl").$(), array("tr_events.action", "events.action"))) {
				redirect(Nest.value(_REQUEST,"backurl").asString()+"?eventid="+Nest.value(_REQUEST,"eventid").asString()+"&triggerid="+Nest.value(_REQUEST,"triggerid").asString()+
					"&source="+EVENT_SOURCE_TRIGGERS
				);
			} else if ("screenedit.action".equals(Nest.value(_REQUEST,"backurl").asString())) {
				redirect(Nest.value(_REQUEST,"backurl").asString()+"?screenid="+Nest.value(_REQUEST,"screenid").asString());
			} else if ("screens.action".equals(Nest.value(_REQUEST,"backurl").asString())) {
				redirect(Nest.value(_REQUEST,"backurl").asString()+"?elementid="+Nest.value(_REQUEST,"screenid").asString());
			} else {
				redirect(Nest.value(_REQUEST,"backurl").asString());
			}
		}
		
		/* Permissions */
		if (!isset(_REQUEST,"events") && !isset(_REQUEST,"eventid") && !isset(_REQUEST,"triggers")) {
			show_message(_("No events to acknowledge"));
		} else if (!empty(get_request("eventid"))) {
			CEventGet eoptions = new CEventGet();
			eoptions.setEventIds(get_request_asLong("eventid"));
			eoptions.setOutput(new String[]{"eventid"});
			eoptions.setLimit(1);
			CArray<Map> event = API.Event(getIdentityBean(), executor).get(eoptions);
			if (empty(event)) {
				access_deny();
			}
		} else if (!empty(get_request("triggers"))) {
			CTriggerGet toptions = new CTriggerGet();
			toptions.setTriggerIds(get_request("triggers",array()).valuesAsLong());
			toptions.setOutput(new String[]{"triggerid"});
			toptions.setLimit(1);
			CArray<Map> trigger = API.Trigger(getIdentityBean(), executor).get(toptions);
			if (empty(trigger)) {
				access_deny();
			}
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	public void doAction(final SQLExecutor executor) {
		/* Actions */
		Boolean eventAcknowledged = null;
		String eventTriggerName = null;

		boolean bulk = !isset(_REQUEST,"eventid");

		if (!bulk) {
			CEventGet eoptions = new CEventGet();
			eoptions.setEventIds(Nest.value(_REQUEST,"eventid").asLong());
			eoptions.setOutput(API_OUTPUT_EXTEND);
			eoptions.setSelectRelatedObject(API_OUTPUT_EXTEND);
			CArray<Map> events = API.Event(getIdentityBean(), executor).get(eoptions);

			if (!empty(events)) {
				Map event = reset(events);
				eventTriggerName = CMacrosResolverHelper.resolveTriggerName(this.getIdentityBean(), executor,Nest.value(event,"relatedObject").asCArray());
				eventAcknowledged = Nest.value(event,"acknowledged").asBoolean();
			}

			Nest.value(_REQUEST,"events").$(Nest.value(_REQUEST,"eventid").$());
		}

		if (isset(_REQUEST,"save") || isset(_REQUEST,"saveandreturn")) {
			if (bulk) {
				Nest.value(_REQUEST,"message").$(
						Nest.value(_REQUEST,"message").asString()
						+
						("".equals(Nest.value(_REQUEST,"message").$()) ? "":"\n\r"+_("----[BULK ACKNOWLEDGE]----"))
				);
			}

			if (isset(_REQUEST,"events")) {
				Nest.value(_REQUEST,"events").$(rda_toObject(Nest.value(_REQUEST,"events").asCArray(), "eventid"));
			} else if (isset(_REQUEST,"triggers")) {
				CEventGet eoptions = new CEventGet();
				eoptions.setSource(EVENT_SOURCE_TRIGGERS);
				eoptions.setObject(EVENT_OBJECT_TRIGGER);
				eoptions.setObjectIds(Nest.array(_REQUEST,"triggers").asLong());
				eoptions.setOutput(new String[]{"eventid"});
				eoptions.setAcknowledged(Nest.as(EVENT_NOT_ACKNOWLEDGED).asBoolean());
				Nest.value(_REQUEST,"events").$(API.Event(getIdentityBean(), executor).get(eoptions));
			}
			
			CArray<Long[]> acknowledgeEvent = Call(new Wrapper<CArray<Long[]>>() {
				@Override
				protected CArray<Long[]> doCall() throws Throwable {
					return API.Event(getIdentityBean(), executor).acknowledge(
							rda_objectValues(Nest.value(_REQUEST,"events").$(), "eventid").valuesAsLong(),
							Nest.value(_REQUEST,"message").asString());
				}
			}, null);

			show_messages(!empty(acknowledgeEvent), _("Event acknowledged"), _("Cannot acknowledge event"));

			if (!empty(acknowledgeEvent)) {
				eventAcknowledged = true;

				add_audit(getIdentityBean(), executor,AUDIT_ACTION_UPDATE, AUDIT_RESOURCE_TRIGGER, _("Acknowledge added")+
					" ["+(bulk ? " BULK ACKNOWLEDGE " : eventTriggerName)+"]"+
					" ["+Nest.value(_REQUEST,"message").asString()+"]");
			}

			if (isset(_REQUEST,"saveandreturn")) {

				if (in_array(Nest.value(_REQUEST,"backurl").$(), array("tr_events.action", "events.action"))) {
					redirect(Nest.value(_REQUEST,"backurl").asString()+"?eventid="+Nest.value(_REQUEST,"eventid").asString()+"&triggerid="+Nest.value(_REQUEST,"triggerid").asString()+
						"&source="+EVENT_SOURCE_TRIGGERS
					);
				} else if ("screenedit.action".equals(Nest.value(_REQUEST,"backurl").asString())) {
					redirect(Nest.value(_REQUEST,"backurl").asString()+"?screenid="+Nest.value(_REQUEST,"screenid").$());
				}
				else if ("screens.action".equals(Nest.value(_REQUEST,"backurl").asString())) {
					redirect(Nest.value(_REQUEST,"backurl").asString()+"?elementid="+Nest.value(_REQUEST,"screenid").$());
				} else {
					redirect(Nest.value(_REQUEST,"backurl").asString());
				}
			}
		}

		/* Display */
		show_table_header(array(_("ALARM ACKNOWLEDGES")+NAME_DELIMITER, (bulk ? " BULK ACKNOWLEDGE " : eventTriggerName)));

		echo(SBR);

		String title, saveAndReturnLabel, saveLabel = null;
		if (bulk) {
			title = _("Acknowledge alarm by");
			saveAndReturnLabel = _("Acknowledge and return");
		} else {
			Map params = new HashMap();
			params.put("eventid", Nest.value(_REQUEST,"eventid").$());
			CArray<Map> acknowledges = DBselect(executor,
				"SELECT a.*,u.alias,u.name,u.surname"+
				" FROM acknowledges a"+
					" LEFT JOIN users u ON u.userid=a.userid"+
				" WHERE a.eventid=#{eventid}",
				params
			);

			if (!empty(acknowledges)) {
				CTable acknowledgesTable = new CTable(null, "ack_msgs");

				for(Map acknowledge : acknowledges) {
					acknowledgesTable.addRow(array(
						new CCol(getUserFullname(acknowledge), "user"),
						new CCol(rda_date2str(_("d M Y H:i:s"), Nest.value(acknowledge,"clock").asLong()), "time")),
						"title"
					);
					acknowledgesTable.addRow(new CCol(rda_nl2br(CommonUtils.encode(Nest.value(acknowledge,"message").asString())), null, 2), "msg");
				}

				acknowledgesTable.show();
			}

			if (eventAcknowledged) {
				title = _("Add comment by");
				saveLabel = _("Save");
				saveAndReturnLabel = _("Save and return");
			} else {
				title = _("Acknowledge alarm by");
				saveLabel = _("Ack");
				saveAndReturnLabel = _("Acknowledge and return");
			}
		}

		CFormTable messageTable = new CFormTable(title+" \""+getLocalUserFullname(CWebUser.data())+"\"");
		messageTable.setTableClass("formtable messageClass");
		messageTable.addVar("backurl", Nest.value(_REQUEST,"backurl").$());

		if (in_array(Nest.value(_REQUEST,"backurl").$(), array("tr_events.action", "events.action"))) {
			messageTable.addVar("eventid", Nest.value(_REQUEST,"eventid").$());
			messageTable.addVar("triggerid", Nest.value(_REQUEST,"triggerid").$());
			messageTable.addVar("source", EVENT_SOURCE_TRIGGERS);
		} else if (in_array(Nest.value(_REQUEST,"backurl").$(), array("screenedit.action", "screens.action"))) {
			messageTable.addVar("screenid", Nest.value(_REQUEST,"screenid").$());
		}

		if (isset(_REQUEST,"eventid")) {
			messageTable.addVar("eventid", Nest.value(_REQUEST,"eventid").$());
		} else if (isset(_REQUEST,"triggers")) {
			for(Object triggerId : Nest.value(_REQUEST,"triggers").asCArray()) {
				messageTable.addVar("triggers["+triggerId+"]", triggerId);
			}
		} else if (isset(_REQUEST,"events")) {
			for(Object eventId : Nest.value(_REQUEST,"events").asCArray()) {
				messageTable.addVar("events["+eventId+"]", eventId);
			}
		}

		CTextArea message = new CTextArea("message", "", (Map)map(
			"rows", RDA_TEXTAREA_STANDARD_ROWS,
			"width", RDA_TEXTAREA_BIG_WIDTH,
			"maxlength", 255
		));
		message.attr("autofocus", "autofocus");

		messageTable.addRow(_("Message"), message);
		messageTable.addItemToBottomRow(new CSubmit("saveandreturn", saveAndReturnLabel,null,"buttonorange"));

		if (!bulk) {
			messageTable.addItemToBottomRow(new CSubmit("save", saveLabel,null,"buttonorange"));
		}

		messageTable.addItemToBottomRow(new CButtonCancel(url_params(getIdentityBean(), array("backurl", "eventid", "triggerid", "screenid")),null,"buttongray"));
		messageTable.show(false);
	}

}
