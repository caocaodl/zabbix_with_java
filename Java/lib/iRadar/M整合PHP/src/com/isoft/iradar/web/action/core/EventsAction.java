package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_combine;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_map;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.strip_tags;
import static com.isoft.iradar.Cphp.strval;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.ActionsUtil.getEventActionsStatus;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSV;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.DiscoveryUtil.check_right_on_discovery;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_check_type2str;
import static com.isoft.iradar.inc.DiscoveryUtil.discovery_port2str;
import static com.isoft.iradar.inc.EventsUtil.getEventAckState;
import static com.isoft.iradar.inc.EventsUtil.get_next_event;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.getPageNumber;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_DISCOVERY_TIME_FORMAT;
import static com.isoft.iradar.inc.TriggersUtil.addTriggerValueStyle;
import static com.isoft.iradar.inc.TriggersUtil.discovery_value;
import static com.isoft.iradar.inc.TriggersUtil.discovery_value_style;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.triggerExpression;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveEventDescription;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Set;

import com.isoft.Feature;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp.ArrayMapCallback;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormTable;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CPageFilter;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class EventsAction extends RadarBaseAction {
	
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;;
	private boolean allow_discovery = false;
	
	@Override
	protected void doInitPage() {
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
			
			page("type", detect_page_type(PAGE_TYPE_CSV));
			page("file", "rda_events_export.csv");
		} else {
			CSV_EXPORT = false;
			page("title", _("Latest events"));
			page("file", getPageFile());
			page("hist_arg", new String[] { "groupid", "hostid" });
			page("scripts", new String[] { "class.calendar.js", "gtlc.js" });
			page("type", detect_page_type(PAGE_TYPE_HTML));
			
			if (PAGE_TYPE_HTML == Nest.as(page("type")).asInteger()) {
				define("RDA_PAGE_DO_REFRESH", 1);
			}
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		allow_discovery = check_right_on_discovery();
		CArray<Integer> allowed_sources = array(EVENT_SOURCE_TRIGGERS);
		if (allow_discovery) {
			allowed_sources.add(EVENT_SOURCE_DISCOVERY);
		}
		
		//		VAR			TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"source",		array(T_RDA_INT, O_OPT, P_SYS,	IN(allowed_sources.valuesAsInteger()), null),
			"groupid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"hostid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"triggerid",	array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null),
			"period",		array(T_RDA_INT, O_OPT, null,	null,		null),
			"dec",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"inc",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"left",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"right",			array(T_RDA_INT, O_OPT, null,	null,		null),
			"stime",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"load",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"csv_export",array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"filter_rst",	array(T_RDA_INT, O_OPT, P_SYS,	IN(array(0,1).valuesAsInteger()), null),
			"filter_set",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT,	null,		null)
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
		if (!empty(get_request("triggerid")) && !API.Trigger(getIdentityBean(), executor).isReadable(Nest.array(_REQUEST,"triggerid").asLong())) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		if (isset(_REQUEST,"favobj")) {
			if ("filter".equals(Nest.value(_REQUEST,"favobj").$())) {
				CProfile.update(getIdentityBean(), executor, "web.events.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}
			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").$())) {
				if (isset(Nest.value(_REQUEST,"favid").$())) {
					CProfile.update(getIdentityBean(), executor, "web.events.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
				}
			}
		}
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		return false;
	}

	@Override
	public void doAction(SQLExecutor executor) {
		/* Filter */
		if (isset(_REQUEST, "filter_rst")) {
			Nest.value(_REQUEST, "triggerid").$(0);
		}
		
		int source = getEventSource(executor);
		
		Nest.value(_REQUEST,"triggerid").$(
				(source == EVENT_SOURCE_DISCOVERY)
				? 0
				: get_request("triggerid", Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.triggerid", 0)).asInteger()));
		
		// change triggerId filter if change hostId
		if (Nest.value(_REQUEST,"triggerid").asInteger() > 0 && isset(_REQUEST,"hostid")) {
			Long hostid = Nest.as(get_request("hostid")).asLong();

			CTriggerGet options = new CTriggerGet();
			options.setOutput(new String[]{"triggerid", "description", "expression"});
			options.setSelectHosts(new String[]{"hostid", "host"});
			options.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
			options.setSelectFunctions(API_OUTPUT_EXTEND);
			options.setTriggerIds(Nest.array(_REQUEST,"triggerid").asLong());
			CArray<Map> oldTriggers = API.Trigger(getIdentityBean(), executor).get(options);

			for (Map oldTrigger : oldTriggers) {
				Nest.value(_REQUEST,"triggerid").$(0);
				Nest.value(oldTrigger,"hosts").$(rda_toHash(Nest.value(oldTrigger,"hosts").$(), "hostid"));
				Nest.value(oldTrigger,"items").$(rda_toHash(Nest.value(oldTrigger,"items").$(), "itemid"));
				Nest.value(oldTrigger,"functions").$(rda_toHash(Nest.value(oldTrigger,"functions").$(), "functionid"));
				String oldExpression = (String)triggerExpression(CArray.valueOf(oldTrigger), false);

				if (isset(Nest.value(oldTrigger,"hosts",hostid))) {
					break;
				}

				options = new CTriggerGet();
				options.setOutput(new String[]{"triggerid", "description", "expression"});
				options.setSelectHosts(new String[]{"hostid", "host"});
				options.setSelectItems(new String[]{"itemid", "key_"});
				options.setSelectFunctions(API_OUTPUT_EXTEND);
				options.setFilter((Map)map("description", new String[]{Nest.value(oldTrigger,"description").asString()}));
				options.setHostIds(hostid);
				CArray<Map> newTriggers = API.Trigger(getIdentityBean(), executor).get(options);

				for(Map newTrigger : newTriggers) {
					if (count(Nest.value(oldTrigger,"items").$()) != count(Nest.value(newTrigger,"items").$())) {
						continue;
					}

					Nest.value(newTrigger,"items").$(rda_toHash(Nest.value(newTrigger,"items").$(), "itemid"));
					Nest.value(newTrigger,"hosts").$(rda_toHash(Nest.value(newTrigger,"hosts").$(), "hostid"));
					Nest.value(newTrigger,"functions").$(rda_toHash(Nest.value(newTrigger,"functions").$(), "functionid"));

					boolean found = false;
					for(Map.Entry ne:(Set<Map.Entry>)Nest.value(newTrigger,"functions").asCArray().entrySet()) {
						Object fnum = ne.getKey();
						CArray function = (CArray)ne.getValue();
						for(Map.Entry oe:(Set<Map.Entry>)Nest.value(oldTrigger,"functions").asCArray().entrySet()) {
							Object ofnum = oe.getKey();
							CArray oldFunction = (CArray)oe.getValue();
							// compare functions
							if ((!Nest.value(function,"function").$().equals(Nest.value(oldFunction,"function").$())) || (!Nest.value(function,"parameter").$().equals(Nest.value(oldFunction,"parameter").$()))) {
								continue;
							}
							// compare that functions uses same item keys
							if (!Nest.value(newTrigger,"items",Nest.value(function, "itemid").asString(),"key_").$().equals(Nest.value(oldTrigger,"items",Nest.value(oldFunction, "itemid").asString(),"key_").$())) {
								continue;
							}
							// rewrite itemid so we could compare expressions
							// of two triggers form different hosts
							Nest.value(newTrigger,"functions",fnum.toString(),"itemid").$(Nest.value(oldFunction,"itemid").$());
							found = true;

							unset(Nest.value(oldTrigger, "functions").asCArray(),ofnum);
							break;
						}
						if (!found) {
							break;
						}
					}
					if (!found) {
						continue;
					}

					// if we found same trigger we overwriting it's hosts and items for expression compare
					Nest.value(newTrigger,"hosts").$(Nest.value(oldTrigger,"hosts").$());
					Nest.value(newTrigger,"items").$(Nest.value(oldTrigger,"items").$());

					String newExpression = (String)triggerExpression(array(newTrigger), false);

					if (strcmp(oldExpression, newExpression) == 0) {
						Nest.value(_REQUEST,"triggerid").$(Nest.value(newTrigger,"triggerid").$());
						Nest.value(_REQUEST,"filter_set").$(1);
						break;
					}
				}
			}
		}
		
		if (isset(_REQUEST,"filter_set") || isset(_REQUEST,"filter_rst")) {
			CProfile.update(getIdentityBean(), executor, "web.events.filter.triggerid", Nest.value(_REQUEST,"triggerid").$(), PROFILE_TYPE_ID);
		}

		CProfile.update(getIdentityBean(), executor, "web.events.source", source, PROFILE_TYPE_INT);
		
		// page filter
		CPageFilter pageFilter = null;
		if (source == EVENT_SOURCE_TRIGGERS) {
			pageFilter = new CPageFilter(getIdentityBean(), executor, map(
				"groups" , map(
					"monitored_hosts", true,
					"with_monitored_triggers", true
				),
				"hosts", map(
					"monitored_hosts", true,
					"with_monitored_triggers", true
				),
				"triggers", map(),
				"hostid", get_request("hostid", null),
				"groupid", get_request("groupid", null),
				"triggerid", get_request("triggerid", null)
			));
			Nest.value(_REQUEST,"groupid").$(pageFilter.$("groupid").asString());
			Nest.value(_REQUEST,"hostid").$(pageFilter.$("hostid").asString());
			if (pageFilter.$("triggerid").asLong() > 0) {
				Nest.value(_REQUEST,"triggerid").$(pageFilter.$("triggerid").asString());
			}
		}
		
		CWidget eventsWidget = new CWidget();
		
		// header
		CForm frmForm = new CForm();
		if (isset(_REQUEST,"source")) {
			frmForm.addVar("source", Nest.value(_REQUEST,"source").asString(), "source_csv");
		}
		if (isset(_REQUEST,"stime")) {
			frmForm.addVar("stime", Nest.value(_REQUEST,"stime").asString(), "stime_csv");
		}
		if (isset(_REQUEST,"period")) {
			frmForm.addVar("period", Nest.value(_REQUEST,"period").asString(), "period_csv");
		}
		frmForm.addVar("page", String.valueOf(getPageNumber(getIdentityBean(), executor)), "page_csv");
		if (source == EVENT_SOURCE_TRIGGERS) {
			if (get_request("triggerid", 0) != 0) {
				frmForm.addVar("triggerid", Nest.value(_REQUEST,"triggerid").asString(), "triggerid_csv");
			} else {
				frmForm.addVar("groupid", Nest.value(_REQUEST,"groupid").asString(), "groupid_csv");
				frmForm.addVar("hostid", Nest.value(_REQUEST,"hostid").asString(), "hostid_csv");
			}
		}
		if(Feature.showExportCsvIcon){
			frmForm.addItem(new CSubmit("csv_export", _("Export to CSV")));
		}
		
		eventsWidget.addPageHeader(
			_("HISTORY OF EVENTS")+SPACE+"["+rda_date2str(_("d M Y H:i:s"))+"]",
			array(
				frmForm,
				SPACE,
				get_icon(getIdentityBean(), executor, "fullscreen", map("fullscreen" , Nest.value(_REQUEST,"fullscreen").$()))
			)
		);
		
		CForm r_form = new CForm("get");
		r_form.addVar("fullscreen", Nest.value(_REQUEST,"fullscreen").asString());
		r_form.addVar("stime", get_request("stime"));
		r_form.addVar("period", get_request("period"));
		
		// add host and group filters to the form
		if (source == EVENT_SOURCE_TRIGGERS) {
			if (get_request("triggerid", 0) != 0) {
				r_form.addVar("triggerid", get_request("triggerid"));
			}

			r_form.addItem(array(
				_("Group")+SPACE,
				pageFilter.getGroupsCB()
			));
			r_form.addItem(array(
				SPACE+_("Host")+SPACE,
				pageFilter.getHostsCB()
			));
		}
		
		if (allow_discovery) {
			CComboBox cmbSource = new CComboBox("source", Nest.as(source).asString(), "submit()");
			cmbSource.addItem(EVENT_SOURCE_TRIGGERS, _("Trigger"));
			cmbSource.addItem(EVENT_SOURCE_DISCOVERY, _("Discovery"));
			r_form.addItem(array(SPACE+_("Source")+SPACE, cmbSource));
		}
				
		eventsWidget.addHeader(_("Events"), r_form);
		eventsWidget.addHeaderRowNumber();
		
		CFormTable filterForm = null;
		if (source == EVENT_SOURCE_TRIGGERS) {
			filterForm = new CFormTable(null, null, "get");
			filterForm.setAttribute("name", "rda_filter");
			filterForm.setAttribute("id", "rda_filter");
			filterForm.addVar("triggerid", get_request("triggerid",""));
			filterForm.addVar("stime", get_request("stime"));
			filterForm.addVar("period", get_request("period"));
			
			String trigger = null;
			if (isset(_REQUEST,"triggerid") && Nest.value(_REQUEST,"triggerid").asInteger() > 0) {
				CTriggerGet tget = new CTriggerGet();
				tget.setTriggerIds(Nest.array(_REQUEST,"triggerid").asLong());
				tget.setOutput(new String[]{"description", "expression"});
				tget.setSelectHosts(new String[]{"name"});
				tget.setPreserveKeys(true);
				tget.setExpandDescription(true);
				CArray<Map> dbTriggera = API.Trigger(getIdentityBean(), executor).get(tget);
				Map dbTrigger = null;
				Map host = null;
				if (dbTriggera!=null && !dbTriggera.isEmpty()) {
					dbTrigger = reset(dbTriggera);
					host = reset(Nest.value(dbTrigger,"hosts").asCArray());
					trigger = Nest.value(host,"name").asString()+NAME_DELIMITER+Nest.value(dbTrigger,"description").asString();
				} else {
					Nest.value(_REQUEST,"triggerid").$(0);
				}
			}
			
			if (!isset(trigger)) {
				trigger = "";
			}
			
			filterForm.addRow(new CRow(array(
					new CCol(_("Trigger"), "form_row_l"),
					new CCol(array(
						new CTextBox("trigger", trigger, 96, true),
						new CButton("btn1", _("Select"),
							"return PopUp(\"popup.action?"+
								"dstfrm="+filterForm.getName()+
								"&dstfld1=triggerid"+
								"&dstfld2=trigger"+
								"&srctbl=triggers"+
								"&srcfld1=triggerid"+
								"&srcfld2=description"+
								"&real_hosts=1"+
								"&monitored_hosts=1"+
								"&with_monitored_triggers=1"+
								(!empty(Nest.value(_REQUEST,"hostid").$()) ? "&only_hostid="+Nest.value(_REQUEST,"hostid").asString() : "")+
								"\");",
							"T"
						)
					), "form_row_r")
				)));
			
			filterForm.addItemToBottomRow(new CSubmit("filter_set", _("GoFilter")));
			filterForm.addItemToBottomRow(new CButton("filter_rst", _("Reset"),
				"javascript: var uri = new Curl(location.href); uri.setArgument(\"filter_rst\", 1); location.href = uri.getUrl();"));
		}
		
		eventsWidget.addFlicker(filterForm, Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.state", 0)).asInteger());
		
		CDiv scroll = new CDiv();
		scroll.setAttribute("id", "scrollbar_cntr");
		eventsWidget.addFlicker(scroll, Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.state", 0)).asInteger());
		
		/* Display */
		Object table = new CTableInfo(_("No events found."));

		Map firstEvent = null;
		String sourceName = null;
		CEventGet eget = null;
		CArray<Map> firstEvents = null;
		// trigger events
		if (source == EVENT_OBJECT_TRIGGER) {
			sourceName = "trigger";

			eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_TRIGGERS);
			eget.setObject(EVENT_OBJECT_TRIGGER);
			eget.setOutput(API_OUTPUT_EXTEND);
			if(!empty(Nest.value(_REQUEST,"triggerid").$())){
				eget.setObjectIds(Nest.array(_REQUEST,"triggerid").asLong());
			}
			eget.setSortfield("clock");
			eget.setSortorder(RDA_SORT_UP);
			eget.setLimit(1);
			firstEvents = API.Event(getIdentityBean(), executor).get(eget);
			firstEvent = reset(firstEvents);
		} else {// discovery events
			sourceName = "discovery";

			eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_DISCOVERY);
			eget.setObject(EVENT_OBJECT_DHOST);
			eget.setOutput(API_OUTPUT_EXTEND);
			eget.setSortfield("clock");
			eget.setSortorder(RDA_SORT_UP);
			eget.setLimit(1);
			
			firstEvents = API.Event(getIdentityBean(), executor).get(eget);
			firstEvent = reset(firstEvents);

			eget = new CEventGet();
			eget.setSource(EVENT_SOURCE_DISCOVERY);
			eget.setObject(EVENT_OBJECT_DSERVICE);
			eget.setOutput(API_OUTPUT_EXTEND);
			eget.setSortfield("clock");
			eget.setSortorder(RDA_SORT_UP);
			eget.setLimit(1);
			
			CArray<Map> firstDServiceEvents = API.Event(getIdentityBean(), executor).get(eget);
			firstEvent = reset(firstDServiceEvents);
			Map firstDServiceEvent = reset(firstDServiceEvents);

			if (firstDServiceEvent!=null && (firstEvent==null || Nest.value(firstDServiceEvent,"eventid").asInteger() < Nest.value(firstEvent,"eventid").asInteger())) {
				firstEvent = firstDServiceEvent;
			}
		}
		
		if (isset(_REQUEST,"period")) {
			Nest.value(_REQUEST,"period").$(get_request("period", RDA_PERIOD_DEFAULT));
			CProfile.update(getIdentityBean(), executor, "web.events."+sourceName+".period", Nest.value(_REQUEST,"period").$(), PROFILE_TYPE_INT);
		} else {
			Nest.value(_REQUEST,"period").$(CProfile.get(getIdentityBean(), executor, "web.events."+sourceName+".period"));
		}
		
		int effectiveperiod = navigation_bar_calc(getIdentityBean(), executor);
		long from = rdaDateToTime(Nest.value(_REQUEST,"stime").asString());
		long till = from + effectiveperiod;
		
		boolean csv_disabled = true;
		
		CTable paging = null;
		Long starttime = null;
		CArray<Map> events = null;
		if (firstEvent == null) {
			events = array();
			paging = getPagingLine(getIdentityBean(), executor, events);
		} else {
			Map config = select_config(getIdentityBean(), executor);
			starttime = Nest.value(firstEvent,"clock").asLong();
			
			if (source == EVENT_SOURCE_DISCOVERY) {
				// fetch discovered service and discovered host events separately
				eget = new CEventGet();
				eget.setSource(EVENT_SOURCE_DISCOVERY);
				eget.setObject(EVENT_OBJECT_DHOST);
				eget.setTimeFrom(from);
				eget.setTimeTill(till);
				eget.setOutput(new String[]{"eventid", "object", "objectid", "clock", "value"});
				eget.setSortfield("clock", "eventid");
				eget.setSortorder(RDA_SORT_DOWN);
				eget.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				CArray dHostEvents = API.Event(getIdentityBean(), executor).get(eget);				
				
				eget = new CEventGet();
				eget.setSource(EVENT_SOURCE_DISCOVERY);
				eget.setObject(EVENT_OBJECT_DSERVICE);
				eget.setTimeFrom(from);
				eget.setTimeTill(till);
				eget.setOutput(new String[]{"eventid", "object", "objectid", "clock", "value"});
				eget.setSortfield("clock", "eventid");
				eget.setSortorder(RDA_SORT_DOWN);
				eget.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
				CArray<Map>  dServiceEvents = API.Event(getIdentityBean(), executor).get(eget);
				CArray<Map>  dsc_events = array_merge(dHostEvents, dServiceEvents);
				CArrayHelper.sort(dsc_events, (CArray)array(
					map("field", "clock", "order", RDA_SORT_DOWN),
					map("field", "eventid", "order", RDA_SORT_DOWN)
				));
				dsc_events = array_slice(dsc_events, 0, Nest.value(config,"search_limit").asInteger() + 1);
				paging = getPagingLine(getIdentityBean(), executor, dsc_events);

				// do we need to make CVS export button enabled?
				csv_disabled = rda_empty(dsc_events);

				CArray<Map> objectids = array();
				for(Map event_data:dsc_events) {
					objectids.put(event_data.get("objectid"),event_data.get("objectid"));
				}

				// object dhost
				CArray<Map> dhosts = array();
				SqlBuilder sqlParts = new SqlBuilder();		
				sqlParts.select.put("s.dserviceid");
				sqlParts.select.put("s.dhostid");
				sqlParts.select.put("s.ip");
				sqlParts.select.put("s.dns");
				sqlParts.from.put("dservices s");
				sqlParts.where.dbConditionInt("s.dhostid", objectids.valuesAsLong());
				
				String sql = sqlParts.createSelectQueryFromParts();
				Map paraMap = sqlParts.getNamedParams();
				CArray<Map> res = DBselect(executor, sql, paraMap);
				for (Map dservices :res) {
					dhosts.put(dservices.get("dhostid"), dservices);
				}

				// object dservice
				CArray dservices = array();
				sqlParts = new SqlBuilder();		
				sqlParts.select.put("s.dserviceid");
				sqlParts.select.put("s.ip");
				sqlParts.select.put("s.dns");
				sqlParts.select.put("s.type");
				sqlParts.select.put("s.port");
				sqlParts.from.put("dservices s");
				sqlParts.where.dbConditionInt("s.dserviceid", objectids.valuesAsLong());
				sql = sqlParts.createSelectQueryFromParts();
				paraMap = sqlParts.getNamedParams();
				res = DBselect(executor, sql, paraMap);
				for (Map dservice :res) {
					dservices.put(dservice.get("dserviceid"), dservice);
				}

				((CTableInfo)table).setHeader(array(
					_("Time"),
					_("IP"),
					_("DNS"),
					_("Description"),
					_("Status")
				));

				if (CSV_EXPORT) {
					csvRows.add(array(
						_("Time"),
						_("IP"),
						_("DNS"),
						_("Description"),
						_("Status")
					));
				}
				
				for(Map event_data:dsc_events) {
					switch (Nest.value(event_data,"object").asInteger()) {
						case EVENT_OBJECT_DHOST:
							if (isset(dhosts,event_data.get("objectid"))) {
								Nest.value(event_data,"object_data").$(dhosts.get(event_data.get("objectid")));
							} else {
								Nest.value(event_data,"object_data","ip").$(_("Unknown"));
								Nest.value(event_data,"object_data","dns").$(_("Unknown"));
							}
							Nest.value(event_data,"description").$(_("Host"));
							break;

						case EVENT_OBJECT_DSERVICE:
							if (isset(dservices,event_data.get("objectid"))) {
								Nest.value(event_data,"object_data").$(dservices.get(event_data.get("objectid")));
							} else {
								Nest.value(event_data,"object_data","ip").$(_("Unknown"));
								Nest.value(event_data,"object_data","dns").$(_("Unknown"));
								Nest.value(event_data,"object_data","type").$(_("Unknown"));
								Nest.value(event_data,"object_data","port").$(_("Unknown"));
							}

							Nest.value(event_data,"description").$(_("Service")+NAME_DELIMITER+
									discovery_check_type2str(Nest.value(event_data,"object_data","type").asInteger())+
									discovery_port2str(Nest.value(event_data,"object_data","type").asInteger(), Nest.value(event_data,"object_data","port").asInteger()));
							break;

						default:
							continue;
					}

					if (!isset(Nest.value(event_data,"object_data").$())) {
						continue;
					}
					((CTableInfo)table).addRow(array(
						rda_date2str(EVENTS_DISCOVERY_TIME_FORMAT, Nest.value(event_data,"clock").asLong()),
						Nest.value(event_data,"object_data","ip").$(),
						rda_empty(Nest.value(event_data,"object_data","dns").$()) ? SPACE : Nest.value(event_data,"object_data","dns").$(),
						Nest.value(event_data,"description").$(),
						new CCol(discovery_value(Nest.value(event_data,"value").asInteger()), discovery_value_style(Nest.value(event_data,"value").asInteger()))
					));

					if (CSV_EXPORT) {
						csvRows.add( array(
							rda_date2str(EVENTS_DISCOVERY_TIME_FORMAT, Nest.value(event_data,"clock").asLong()),
							Nest.value(event_data,"object_data","ip").$(),
							Nest.value(event_data,"object_data","dns").$(),
							Nest.value(event_data,"description").$(),
							discovery_value(Nest.value(event_data,"value").asInteger())
						));
					}
				}
			}
			// source not discovery i.e. trigger
			else {
				((CTableInfo)table).setHeader(array(
					_("Time"),
					(Nest.value(_REQUEST,"hostid").asInteger() == 0) ? _("Host") : null,
					_("Description"),
					_("Status"),
					_("Severity"),
					_("Duration"),
					Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null,
					_("Actions")
				));
				
				if (CSV_EXPORT) {
					csvRows.add( array(
						_("Time"),
						(Nest.value(_REQUEST,"hostid").asInteger() == 0) ? _("Host") : null,
						_("Description"),
						_("Status"),
						_("Severity"),
						_("Duration"),
						Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null,
						_("Actions")
					) );
				}
				
				if (pageFilter.$("hostsSelected").asBoolean()) {
					CArray knownTriggerIds = array();
					CArray validTriggerIds = array();
					
					CTriggerGet triggerOptions = new CTriggerGet();
					triggerOptions.setOutput(new String[]{"triggerid"});
					triggerOptions.setPreserveKeys(true);
					triggerOptions.setMonitored(true);
					//hook trigger options
					doTriggerFiler(triggerOptions);
					
					int allEventsSliceLimit = Nest.value(config,"search_limit").asInteger();
					
					CEventGet eventOptions = new CEventGet();
					eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
					eventOptions.setObject(EVENT_OBJECT_TRIGGER);
					eventOptions.setTimeFrom(from);
					eventOptions.setTimeTill(till);
					eventOptions.setOutput(new String[]{"eventid", "objectid"});
					eventOptions.setSortfield("clock", "eventid");
					eventOptions.setSortorder(RDA_SORT_DOWN);
					eventOptions.setLimit(allEventsSliceLimit + 1);
					//根据事件的条件过滤查询结果
					doEventsFiler(eventOptions);
					
					CArray filterTriggerIds;
					if (!empty(get_request("triggerid",0))) {
						filterTriggerIds = array(get_request("triggerid"));
						knownTriggerIds = array_combine(filterTriggerIds, filterTriggerIds);
						validTriggerIds = knownTriggerIds;
						eventOptions.setObjectIds(filterTriggerIds.valuesAsLong());
					} else if (pageFilter.$("hostid").asLong() > 0) {
						CTriggerGet tget = new CTriggerGet();
						tget.setOutput(new String[]{"triggerid"});
						tget.setHostIds(pageFilter.$("hostid").asLong());
						tget.setMonitored(true);
						tget.setPreserveKeys(true);
						
						CArray<Map> hostTriggers = API.Trigger(getIdentityBean(), executor).get(tget);
						filterTriggerIds = array_map(new ArrayMapCallback() {
							@Override public Object call(Object... objs) {
								return strval(objs[0]);
							}
						}, array_keys(hostTriggers));
						knownTriggerIds = array_combine(filterTriggerIds, filterTriggerIds);
						validTriggerIds = knownTriggerIds;
						
						eventOptions.setHostIds(pageFilter.$("hostid").asLong());
						eventOptions.setObjectIds(validTriggerIds.valuesAsLong());
					} else if (pageFilter.$("groupid").asLong() > 0) {
						eventOptions.setGroupIds(pageFilter.$("groupid").asLong());
						triggerOptions.setGroupIds(pageFilter.$("groupid").asLong());
					}
					
					events = array();
					
					while (true) {
						CArray<Map> allEventsSlice = API.Event(getIdentityBean(), executor).get(eventOptions);

						CArray triggerIdsFromSlice = array_keys(array_flip(rda_objectValues(allEventsSlice, "objectid")));

						CArray unknownTriggerIds = array_diff(triggerIdsFromSlice, knownTriggerIds);

						if (!empty(unknownTriggerIds)) {
							Nest.value(triggerOptions,"triggerids").$(unknownTriggerIds);
							CArray<Map> validTriggersFromSlice = API.Trigger(getIdentityBean(), executor).get(triggerOptions);

							for(Map trigger:validTriggersFromSlice) {
								validTriggerIds.put(trigger.get("triggerid"),trigger.get("triggerid"));
							}

							for(Object id:unknownTriggerIds.values()) {
								knownTriggerIds.put(id,id);
							}
						}
						Map lastEvent = null;
						for(Map event:allEventsSlice) {
							if (isset(validTriggerIds,event.get("objectid"))) {
								events.add(map("eventid" , Nest.value(event,"eventid").$()));
							}
							lastEvent = event;
						}

						// break loop when either enough events have been retrieved, or last slice was not full
						if (count(events) >= Nest.value(config,"search_limit").asInteger() || count(allEventsSlice) <= allEventsSliceLimit) {
							break;
						}

						/*
						 * Because events in slices are sorted descending by eventid (i.e. bigger eventid),
						 * first event in next slice must have eventid that is previous to last eventid in current slice.
						 */
						Nest.value(eventOptions,"eventid_till").$(Nest.value(lastEvent,"eventid").asLong() - 1);
					}
					
					/*
					 * At this point it is possible that more than Nest.value(_config,"search_limit").$() events are selected,
					 * therefore at most only first Nest.value(_config,"search_limit").$() + 1 events will be used for pagination.
					 */
					events = array_slice(events, 0, Nest.value(config,"search_limit").asInteger() + 1);

					// get paging
					paging = getPagingLine(getIdentityBean(), executor, events);

					// query event with extend data
					eget = new CEventGet();
					eget.setSource(EVENT_SOURCE_TRIGGERS);
					eget.setObject(EVENT_OBJECT_TRIGGER);
					eget.setEventIds(rda_objectValues(events, "eventid").valuesAsLong());
					eget.setOutput(API_OUTPUT_EXTEND);
					eget.setSelectAcknowledges(API_OUTPUT_COUNT);
					eget.setSortfield("clock", "eventid");
					eget.setSortorder(RDA_SORT_DOWN);
					eget.setNopermissions(true);
					events = API.Event(getIdentityBean(), executor).get(eget);
					
					csv_disabled = rda_empty(events);
					
					CTriggerGet tget = new CTriggerGet();
					tget.setTriggerIds(rda_objectValues(events, "objectid").valuesAsLong());
					tget.setSelectHosts(new String[]{"hostid"});
					tget.setSelectItems(new String[]{"itemid", "hostid", "name", "key_", "value_type"});
					tget.setOutput(new String[]{"description", "expression", "priority", "flags", "url"});
					CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(tget);
					triggers = rda_toHash(triggers, "triggerid");
					
					// fetch hosts
					CArray<Map> hosts = array();
					for(Map trigger : triggers) {
						hosts.add((Map)reset(Nest.value(trigger,"hosts").asCArray()));
					}
					CArray hostids = rda_objectValues(hosts, "hostid");
					CHostGet hget = new CHostGet();
					hget.setOutput(new String[]{"name", "hostid", "status"});
					hget.setHostIds(hostids.valuesAsLong());
					hget.setSelectScreens(API_OUTPUT_COUNT);
					hget.setPreserveKeys(true);
					hosts = API.Host(getIdentityBean(), executor).get(hget);

					// fetch scripts for the host JS menu
					CArray<CArray<Map>> scripts = null;
					if (Nest.value(_REQUEST,"hostid").asInteger() == 0) {
						scripts = API.Script(getIdentityBean(), executor).getScriptsByHosts(hostids.valuesAsLong());
					}

					// actions
					CArray actions = getEventActionsStatus(getIdentityBean(), executor, rda_objectValues(events, "eventid"));
					
					// events
					for (Map event : events) {
						CArray trigger = CArray.valueOf(triggers.get(event.get("objectid")));
						
						Map host = reset(Nest.value(trigger,"hosts").asCArray());
						host = hosts.get(host.get("hostid"));
						
						CArray triggerItems = array();
						Nest.value(trigger,"items").$(resolveItemNames(getIdentityBean(), executor, Nest.value(trigger,"items").$s()));
						
						CArray<Map> items = Nest.value(trigger,"items").$s();
						for (Map item : items) {
							triggerItems.add(map(
								"name" , Nest.value(item,"name_expanded").$(),
								"params" , map(
									"itemid" , Nest.value(item,"itemid").$(),
									"action" , in_array(Nest.value(item,"value_type").$(), array(ITEM_VALUE_TYPE_FLOAT, ITEM_VALUE_TYPE_UINT64))
										? "showgraph" : "showvalues"
								)
							));
						}
						
						String description = resolveEventDescription(this.getIdentityBean(), executor, rda_array_merge(trigger, map(
							"clock" , Nest.value(event,"clock").$(),
							"ns" , Nest.value(event,"ns").$()
						)));
						
						CSpan triggerDescription = new CSpan(description, "pointer link_menu");
						triggerDescription.setMenuPopup(FuncsUtil.getMenuPopupTrigger(trigger, triggerItems, null, Nest.value(event,"clock").asString()));
						
						// acknowledge
						Object ack = getEventAckState(getIdentityBean(), executor, event, true);
						
						// duration
						Map nextEvent;
						Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(this.getIdentityBean(), executor, event, events))
							? rda_date2age(Nest.value(event,"clock").asLong(), Nest.value(nextEvent,"clock").asLong())
							: rda_date2age(Nest.value(event,"clock").asLong()));
						
						CSpan statusSpan = new CSpan(trigger_value2str(Nest.value(event,"value").asInteger()));
						
						// add colors and blinking to span depending on configuration and trigger parameters
						addTriggerValueStyle(
							getIdentityBean(), 
							executor, 
							statusSpan,
							Nest.value(event,"value").asInteger(),
							Nest.value(event,"clock").asInteger(),
							Nest.value(event,"acknowledged").asBoolean()
						);
						
						// host JS menu link
						CSpan hostName = null;
						
						if (Nest.value(_REQUEST,"hostid").asInteger() == 0) {
							hostName = new CSpan(Nest.value(host,"name").$(), "link_menu");
							hostName.setMenuPopup(getMenuPopupHost(host, scripts.get(host.get("hostid"))));
						}
						
						// action
						Object action = isset(actions.get(event.get("eventid"))) ? actions.get(event.get("eventid")) : " - ";
						
						((CTableInfo)table).addRow(array(
							new CLink(rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),
									"tr_events.action?triggerid="+Nest.value(event,"objectid").asString()+"&eventid="+Nest.value(event,"eventid").asString(),
								"action"
							),
							hostName,
							triggerDescription,
							statusSpan,
							getSeverityCell(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger(), null, !Nest.value(event,"value").asBoolean()),
							Nest.value(event,"duration").$(),
							Nest.value(config,"event_ack_enable").asBoolean() ? ack : null,
							action
						));
						
						if (CSV_EXPORT) {
							csvRows.add(array(
								rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),
								(Nest.value(_REQUEST,"hostid").asLong() == 0) ? Nest.value(host,"name").$() : null,
								description,
								trigger_value2str(Nest.value(event,"value").asInteger()),
								getSeverityCaption(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger()),
								Nest.value(event,"duration").$(),
								Nest.value(config,"event_ack_enable").asBoolean() ? (Nest.value(event,"acknowledges").asBoolean() ? _("Yes") : _("No")) : null,
								strip_tags(Nest.as(action).asString())
							));
						}
					}

				} else {
					events = array();
					paging = getPagingLine(getIdentityBean(), executor, events);
				}
				
			}
			
			if (CSV_EXPORT) {
				print(rda_toCSV(csvRows));
				return;
			}
			
			table = array(paging, table, paging);
		}
		
		eventsWidget.addItem(table);
		
		CArray timeline = map(
				"period", effectiveperiod,
				"starttime", date(TIMESTAMP_FORMAT, starttime),
				"usertime", date(TIMESTAMP_FORMAT, till)
		);
		
		CArray objData = map(
				"id", "timeline_1",
				"loadSBox", 0,
				"loadImage", 0,
				"loadScroll", 1,
				"dynamic", 0,
				"mainObject", 1,
				"periodFixed", CProfile.get(getIdentityBean(), executor, "web.events.timelinefixed", 1),
				"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);
		
		rda_add_post_js("jqBlink.blink();");
		rda_add_post_js("timeControl.addObject(\"scroll_events_id\", "+rda_jsvalue(timeline)+", "+rda_jsvalue(objData)+");");
		rda_add_post_js("timeControl.processObjects();");
		
		eventsWidget.show();
		
		if (csv_disabled) {
			rda_add_post_js("document.getElementById(\"csv_export\").disabled = true;");
		}
		
	}
	
	 /**
     * hook TriggerFiler
     * priority 0、1、2 --> Warnings
     * priority 3、4、5 --> Erros
     * @param triggerOptions
     */
	protected void doTriggerFiler(CTriggerGet triggerOptions) {
	};

    /**
     * filter Events
     * value 0 --> History 
     * value 1 --> Active
     * @param cEventGet
     */
	protected void doEventsFiler(CEventGet cEventGet) {
	};
    
    protected String getPageFile(){
       return "events.action";
    } 
    
    protected int getEventSource(SQLExecutor executor){
    	return get_request("source", Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.source", EVENT_SOURCE_TRIGGERS)).asInteger());
    }

}

