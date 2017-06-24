package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_combine;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_flip;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_map;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.define;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.print;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.strval;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
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
import static com.isoft.iradar.inc.EventsUtil.getEventAckState;
import static com.isoft.iradar.inc.EventsUtil.get_next_event;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.getPageNumber;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortField;
import static com.isoft.iradar.inc.FuncsUtil.getPageSortOrder;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.make_sorting_header;
import static com.isoft.iradar.inc.FuncsUtil.order_result;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toCSV;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TranslateDefines.EVENTS_ACTION_TIME_FORMAT;
import static com.isoft.iradar.inc.TriggersUtil.addTriggerValueStyle;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.triggerExpression;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.iradar.macros.CMacrosResolverHelper.resolveItemNames;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.isoft.Feature;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.Cphp.ArrayMapCallback;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.DBUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.tags.AjaxResponse;
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
	
	private boolean SCROLL_PAGE = false;
	private static int  SCROLL_START = 0;
	private boolean CSV_EXPORT = false;
	private CArray csvRows = null;;
	private boolean allow_discovery = false;
	private final static String SQL_UPDATE_VLAUE_EVENT = "update events set value=#{value} where eventid=#{eventid} and tenantid=#{tenantid}";
	
//	public static String DEFAULTEVENT = "events.action";
	public static String ACTIVEALARM = "activealarm.action";
//	public static String ACTIVEFAULT = "activefault.action";
//	public static String HISTORYALARM = "historyalarm.action";
//	public static String HISTORYFAULT = "historyfault.action";
//	public static CArray eventsAlarmActionNames = array(ACTIVEALARM,HISTORYALARM);
//	public static CArray eventsFaultActionNames = array(ACTIVEFAULT,HISTORYFAULT);
//	public static String[] eventsActionNames = new String[]{DEFAULTEVENT,ACTIVEALARM,ACTIVEFAULT,HISTORYALARM,HISTORYFAULT};
//	private String profileEventFlag = getActionNameFromPageFile();
	
	@Override
	protected void doInitPage() {
		SCROLL_PAGE = isset(_REQUEST, "scroll");
		SCROLL_START = Nest.value(_REQUEST, "start").asInteger();
		if (isset(_REQUEST, "csv_export")) {
			CSV_EXPORT = true;
			csvRows  = array();
			
			String time= rda_date2str(_("d M Y"),Cphp.time());
			page("type", detect_page_type(PAGE_TYPE_CSV));
			String name=null;
			if(ACTIVEALARM.equals(getPageFile())){
				name = _("Active_events_export");
			}else {
				name = _("History_events_export");
			}
			page("file", name+time+".csv");
		}else if(isset(_REQUEST, "actAlarmFaultNum") || SCROLL_PAGE){ //页面顶部活动告警、故障数目    设置输出json数据
			page("type", detect_page_type(Defines.PAGE_TYPE_JSON));
			page("file", getPageFile());
			if(SCROLL_PAGE) {
				_REQUEST.put("page", Nest.value(_REQUEST, "start").asInteger());
			}
		} else {
			CSV_EXPORT = false;
			page("title", _("Latest events"));
			page("file", getPageFile());
			page("hist_arg", new String[] { "groupid", "hostid" });
			page("scripts", new String[] { "class.calendar.js", "gtlc.js" });
			page("js", new String[] {"imon/scrollpagination.js", "imon/event/events.js"});
			page("type", detect_page_type(PAGE_TYPE_HTML));
			page("css", new String[] { "lessor/eventcenter/event.css" });
			
			if (PAGE_TYPE_HTML == Nest.as(page("type")).asInteger()) {
				define("RDA_PAGE_DO_REFRESH", 1);
			}
		}
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		allow_discovery = isNeedDiscovery() && check_right_on_discovery();
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
			"right",		array(T_RDA_INT, O_OPT, null,	null,		null),
			"stime",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"load",			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			"csv_export",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"filter_rst",	array(T_RDA_INT, O_OPT, P_SYS,	IN(array(0,1).valuesAsInteger()), null),
			"filter_set",	array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favstate",		array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	"isset({favobj})&&\"filter\"=={favobj}"),
			"favid",		array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"actAlarmFaultNum",		array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"isIgnore",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"eventid",		array(T_RDA_INT, O_OPT, P_SYS,	DB_ID,		null)
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
		
		if(SCROLL_PAGE) {
			doAction(executor);
		}
		
		if (Nest.value(page,"type").asInteger() == PAGE_TYPE_JS || Nest.value(page,"type").asInteger() == PAGE_TYPE_HTML_BLOCK) {
			return true;
		}
		
		//页面顶部的活动 告警、故障数目
		if(isset(_REQUEST, "actAlarmFaultNum")){
			String actAlarmNum=getEventList(executor).get(0) ;//活动告警数目
			echo("[{\"actAlarmNum\":\""+actAlarmNum+"\"}]");
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
		
		if(isset(_REQUEST, "isIgnore") && isset(_REQUEST, "eventid")){
			final Map eventmap = map(
					"eventid", Nest.value(_REQUEST ,"eventid").asString(),
					"tenantid", getIdentityBean().getTenantId(),
					"value", 0 );
			try {
				boolean result= DBUtil.DBexecute(executor, SQL_UPDATE_VLAUE_EVENT, eventmap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			unset(_REQUEST,"eventid");
			unset(_REQUEST,"isIgnore");
		}
		
		int source = getEventSource(executor);
		
		Nest.value(_REQUEST,"triggerid").$(
				(source == EVENT_SOURCE_DISCOVERY)
				? 0
				: get_request("triggerid", Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.triggerid", 0)).asInteger()));
		
		// change triggerId filter if change hostId
		if (Nest.value(_REQUEST,"triggerid").asInteger() > 0 && isset(_REQUEST,"hostid") && !SCROLL_PAGE) {
			Long hostid = Nest.as(get_request("hostid")).asLong();

			CTriggerGet options = new CTriggerGet();
			options.setOutput(new String[]{"triggerid", "description", "expression"});
			options.setSelectHosts(new String[]{"hostid", "host"});
			options.setSelectItems(new String[]{"itemid", "hostid", "key_", "type", "flags", "status"});
			options.setSelectFunctions(API_OUTPUT_EXTEND);
			options.setTriggerIds(Nest.array(_REQUEST,"triggerid").asLong());
			options.setEditable(true);
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
				options.setEditable(true);
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
					"editable", true,
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
			frmForm.addItem(new CSubmit("csv_export", _("Export to CSV"),"","orange export"));
		}
		
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
				
		eventsWidget.addHeader(r_form);
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(frmForm));
		eventsWidget.addItem(headerActions);
		
		CFormTable filterForm = null;
		if (source == EVENT_SOURCE_TRIGGERS) {
			filterForm = new CFormTable(null, null, "get");
			filterForm.setAttribute("class", "unFloter");
			filterForm.setAttribute("name", "rda_filter");
			filterForm.setAttribute("id", "rda_filter");
			filterForm.addVar("triggerid", get_request("triggerid",""));
			filterForm.addVar("stime", get_request("stime"));
			filterForm.addVar("period", get_request("period"));
			
			String trigger = null;
			if (isset(_REQUEST,"triggerid") && Nest.value(_REQUEST,"triggerid").asInteger() > 0 && !SCROLL_PAGE) {
				CTriggerGet tget = new CTriggerGet();
				tget.setTriggerIds(Nest.array(_REQUEST,"triggerid").asLong());
				tget.setOutput(new String[]{"description", "expression"});
				tget.setSelectHosts(new String[]{"name"});
				tget.setPreserveKeys(true);
				tget.setExpandDescription(true);
				tget.setEditable(true);
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
						new CTextBox("trigger", trigger, 92, true),
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
								"\",\"\",\"450\");",
							"T"
						),
						new CSubmit("filter_set", _("GoFilter")),
						new CButton("filter_rst", _("Reset"),
								"javascript: var uri = new Curl(location.href); uri.setArgument(\"filter_rst\", 1); location.href = uri.getUrl();","darkgray")
					), "form_row_r")
				)));
		}
		
		eventsWidget.addFlicker(filterForm, Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.state", 0)).asInteger());
		
		CDiv scroll = new CDiv();
		scroll.setAttribute("id", "scrollbar_cntr");
		eventsWidget.addFlicker(scroll, Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.filter.state", 0)).asInteger());
		
		/* Display */
		CTableInfo table = new CTableInfo(_("No events found."));
		String hostidflag = get_request("hostid");
		if("0".equals(hostidflag)){
			table = new CTableInfo(_("No events found."),"tableinfo eventsAll_table");
		}
		table.setAttribute("id", "scrollPagBody");

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
			eget.setEditable(true);
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
			//Nest.value(_REQUEST,"period").$(CProfile.get(getIdentityBean(), executor, "web.events."+sourceName+".period"));
			Nest.value(_REQUEST,"period").$(63072000);//默认显示全部
		}
		
		int effectiveperiod = navigation_bar_calc(getIdentityBean(), executor);
		long from = rdaDateToTime(Nest.value(_REQUEST,"stime").asString());
		long till = from + effectiveperiod;
		
		boolean csv_disabled = true;
		
		Long starttime = null;
		CArray<Map> events = null;
		if (firstEvent == null) {
			events = array();
			
			if(SCROLL_PAGE) {
				int start = (Integer)CProfile.get(this.getIdBean(), executor, "web.paging.page", 1);
				AjaxResponse ajaxResponse = new AjaxResponse();
				Map response = map("start",start, "contenant", "");
				ajaxResponse.success(response);
				ajaxResponse.send();
				return;
			}
		} else {
			Map config = select_config(getIdentityBean(), executor);
			starttime = Nest.value(firstEvent,"clock").asLong();
			
			// source not discovery i.e. trigger
			table.setHeader(array(
				make_sorting_header(_("Open time"),"clock"),
				(Nest.value(_REQUEST,"hostid").asInteger() == 0) ? make_sorting_header(_("By host"),"hostname") : null,
				make_sorting_header(_("severity level"),"priority"),
				make_sorting_header(_("Template trigger Name"),"description"),//_("eventDescription"),
				_("Status"),
				_("Duration"),
				Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null,
				isShowHistory()? null:"忽略"
			));
			
			if (CSV_EXPORT) {
				csvRows.add( array(
					_("Open time"),
					(Nest.value(_REQUEST,"hostid").asInteger() == 0) ? _("By host") : null,
					_("severity level"),
					_("Template trigger Name"),//_("eventDescription"),
					_("Status"),
					_("Duration"),
					Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null
				) );
			}
			
			if (pageFilter.$("hostsSelected").asBoolean() && (SCROLL_PAGE || CSV_EXPORT)) {
				CArray knownTriggerIds = array();
				CArray validTriggerIds = array();
				
				CTriggerGet triggerOptions = new CTriggerGet();
				triggerOptions.setOutput(new String[]{"triggerid"});
				triggerOptions.setPreserveKeys(true);
				triggerOptions.setMonitored(true);
				triggerOptions.setEditable(true);
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
				eventOptions.setEditable(true);
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
					doTriggerFiler(tget);
					
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
				eget.setEditable(true);
				events = API.Event(getIdentityBean(), executor).get(eget);
				
				csv_disabled = rda_empty(events);
				
				CTriggerGet tget = new CTriggerGet();
				tget.setTriggerIds(rda_objectValues(events, "objectid").valuesAsLong());
				tget.setSelectHosts(new String[]{"hostid"});
				tget.setSelectItems(new String[]{"itemid", "hostid", "name", "key_", "value_type"});
				tget.setOutput(new String[]{"description", "expression", "priority", "flags", "url"});
				tget.setEditable(true);
				CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(tget);
				triggers = rda_toHash(triggers, "triggerid");
				
				// fetch hosts
				CArray<Map> hosts = array();
				for(Map trigger : triggers) {
					hosts.add((Map)reset(Nest.value(trigger,"hosts").asCArray()));
				}
				CArray hostids = Cphp.array_filter(rda_objectValues(hosts, "hostid"));
				CHostGet hget = new CHostGet();
				hget.setOutput(new String[]{"name", "hostid", "status"});
				hget.setSelectGroups(new String[]{"groupid","name"});
				hget.setHostIds(hostids.valuesAsLong());
				hget.setSelectScreens(API_OUTPUT_COUNT);
				hget.setPreserveKeys(true);
				hget.setEditable(true);
				hosts = API.Host(getIdentityBean(), executor).get(hget);

				// fetch scripts for the host JS menu
				CArray<CArray<Map>> scripts = null;
				if (Nest.value(_REQUEST,"hostid").asInteger() == 0) {
					scripts = API.Script(getIdentityBean(), executor).getScriptsByHosts(hostids.valuesAsLong());
				}

				// events
				CArray<Map> tableDatas = array();
				CArray<Map> eventsData = array();
				for (Map event : events) {
					CArray trigger = CArray.valueOf(triggers.get(event.get("objectid")));
					
					Map host = reset(Nest.value(trigger,"hosts").asCArray());
					if(empty(host))
						continue;
					host = hosts.get(host.get("hostid"));
					
					Nest.value(trigger,"items").$(resolveItemNames(getIdentityBean(), executor, Nest.value(trigger,"items").$s()));
					
					String description = CMacrosResolverHelper.resolveTriggerName(getIdentityBean(), executor, CArray.valueOf(trigger));
						/*	resolveEventDescription(getIdentityBean(), executor, rda_array_merge(trigger, map(
						"clock" , Nest.value(event,"clock").$(),
						"ns" , Nest.value(event,"ns").$()
					)));*/
					
					
					String descriptionUrl=getDescriptionUrl(trigger);//去除描述列弹出框，修改为跳转到告警规则界面方法
					String common_action_with_context = RadarContext.getContextPath()+IMonConsts.COMMON_ACTION_PREFIX;
					String url = "'"+_("Triggers relevance")+"', '"+common_action_with_context+descriptionUrl+"'";
					Object triggerDescription = empty(descriptionUrl)?new CSpan(description):new CLink(description,IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL),"pointer link_menu",null,Boolean.TRUE);
					
					Object ack = getEventAckState(getIdentityBean(), executor, event, true);
					
					// duration
					Map nextEvent;
					Nest.value(event,"duration").$(!empty(nextEvent = get_next_event(getIdentityBean(), executor, event, events))
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
					Object hostName =null;
					
					if (Nest.value(_REQUEST,"hostid").asInteger() == 0) {
						hostName = Nest.value(host,"name").$();//设备管理列去除弹出框
					}
					
					// action
//					((CTableInfo)table).addRow(array(
//						new CLink(rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),
//								"tr_events.action?triggerid="+Nest.value(event,"objectid").asString()+"&eventid="+Nest.value(event,"eventid").asString(),
//							"action"
//						),
//						hostName,
//						TriggersUtil.getSeverityCell(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger()),//BlocksUtil.getTriggerLevelImg(Nest.value(trigger,"priority").asInteger()),
//						triggerDescription,
//						statusSpan,
//						Nest.value(event,"duration").$(),
//						Nest.value(config,"event_ack_enable").asBoolean() ? ack : null,
//						isShowHistory()? null:new CLink("忽略",getPageFile()+"?isIgnore=true&eventid="+Nest.value(event,"eventid").asString(),"action")		
//					));
					eventsData.add(map("clock",Nest.value(event,"clock").asLong(),
									   "objectid",Nest.value(event,"objectid").asString(),
									   "eventid",Nest.value(event,"eventid").asString(),
									   "hostname",hostName,
									   "hostid",Nest.value(host, "hostid").asString(),
									   "groups",Nest.value(host, "groups").asCArray(),
									   "priority",Nest.value(trigger,"priority").asInteger(),
									   "description",description,
									   "triggerDescription",triggerDescription,
									   "statusSpan",statusSpan,
									   "duration",Nest.value(event,"duration").$(),
									   "event_ack_enable",Nest.value(config,"event_ack_enable").asBoolean() ? ack : null,
									   "showHistory",isShowHistory()? null:new CLink("忽略",getPageFile()+"?isIgnore=true&eventid="+Nest.value(event,"eventid").asString(),"action")));
					
					if (CSV_EXPORT) {
						csvRows.add( array(
							rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),
							(Nest.value(_REQUEST,"hostid").asLong() == 0) ? Nest.value(host,"name").$() : null,
							getSeverityCaption(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger()),
							description,
							trigger_value2str(Nest.value(event,"value").asInteger()),
							Nest.value(event,"duration").$(),
							Nest.value(config,"event_ack_enable").asBoolean() ? (Nest.value(event,"acknowledges").asBoolean() ? _("Yes") 
									+" ("+(isArray(Nest.value(event,"acknowledges").$()) ? count(Nest.value(event,"acknowledges").$()) : Nest.value(event,"acknowledges").$())+")"
									: _("No")) : null
						));
					}
				}

				order_result(eventsData, empty(get_request("sort", null))?"clock":getPageSortField(getIdentityBean(), executor), empty(get_request("sortorder", null))?Defines.RDA_SORT_DOWN:getPageSortOrder(getIdentityBean(), executor));
				// get paging
				if(!CSV_EXPORT){					
					getPagingLine(getIdentityBean(), executor, eventsData);
				}
				
				if(SCROLL_PAGE) {
					String contenant = "";
					int i=0;
					for(Map event:eventsData){
						contenant +=(table.prepareRow(array(                                                                                                                                                     
							new CLink(rda_date2str(EVENTS_ACTION_TIME_FORMAT, Nest.value(event,"clock").asLong()),                                                                                            
									"tr_events.action?triggerid="+Nest.value(event,"objectid").asString()+"&eventid="+Nest.value(event,"eventid").asString(),                                                 
								"action"                                                                                                                                                                      
							),                                                                                                                                                                                
							empty(Nest.value(event, "hostname").asString())?null:getHostDetailUrl(map("hostid",Nest.value(event, "hostid").asString(),
																									  "name",Nest.value(event, "hostname").asString(),
																									  "groups",Nest.value(event, "groups").asCArray())),                                                                                                                                                                       
//									TriggersUtil.getSeverityCell(getIdentityBean(), executor, Nest.value(event,"priority").asInteger()),
//									修改为文字加图标方式  
							BlocksUtil.getTriggerLevel( Nest.value(event,"priority").asInteger(),getIdentityBean(),executor),																		  
							Nest.value(event, "triggerDescription").$(),                                                                                                                                                               
							Nest.value(event, "statusSpan").$(),                                                                                                                                                                       
							Nest.value(event,"duration").$(),                                                                                                                                                 
							Nest.value(event,"event_ack_enable").$(),                                                                                                                   
							Nest.value(event,"showHistory").$()             
						), (i++%2==0? "odd_row": "even_row"), null).toString());                                                                                                                                                                                   
					}
					
					int start = (Integer)CProfile.get(this.getIdBean(), executor, "web.paging.page", 1)+1;
					if(SCROLL_START==start){
						contenant = "";
					}
					AjaxResponse ajaxResponse = new AjaxResponse();
					Map response = map("start",start, "contenant", contenant);
					ajaxResponse.success(response);
					ajaxResponse.send();
					return;
				}
			} else  {
				events = array();
			}
			
		}
		
		if (CSV_EXPORT) {
			print(rda_toCSV(csvRows));
			return;
		}
		
		eventsWidget.addItem(array(table));
		
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
		
		if(!SCROLL_PAGE)
			eventsWidget.show();
		
		if (csv_disabled) {
//			rda_add_post_js("jQuery(\"#csv_export\").addClass(\"disabled\");");
//			rda_add_post_js("document.getElementById(\"csv_export\").disabled = true;");
		}
		
	}
	
	/**
	 * 监察事件是否已经存在
	 * @param description
	 * @param eventNames
	 * @return
	 */
	private boolean checkEventIsHave(String description,CArray eventNames){
		if(eventNames.values().contains(description)){
			return true;
		}
		return false;
	}
	
	protected String getPageTitle() {
		return _("HISTORY OF EVENTS");
	}
	
	protected String getPageName() {
		return _("Events");
	}
	
	protected boolean isNeedDiscovery() {
		return true;
	}
	
	protected boolean isShowWarnings() {
		return true;
	}
	
	protected boolean isShowErrors() {
		return true;
	}
	
	protected String getPageFile(){
       return "events.action";
    }
	
	public final static String[] PRIORITIES_WARNING = new String[] {
		""+Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED,
		""+Defines.TRIGGER_SEVERITY_INFORMATION, 
		""+Defines.TRIGGER_SEVERITY_WARNING
	};
	
	public final static String[] PRIORITIES_ERROR = new String[] {
		""+Defines.TRIGGER_SEVERITY_AVERAGE, 
		""+Defines.TRIGGER_SEVERITY_HIGH, 
		""+Defines.TRIGGER_SEVERITY_DISASTER
//		,Defines.TRIGGER_SEVERITY_COUNT
	};
	
	/**
     * hook TriggerFiler
     * priority 0、1、2 --> Warnings
     * priority 3、4、5 --> Erros
     * @param triggerOptions
     */
	private void doTriggerFiler(CTriggerGet triggerOptions) {
		if (isShowWarnings() && isShowErrors()) {
			//show all
		}else {
			if(isShowWarnings()) {
				triggerOptions.setFilter("priority", PRIORITIES_WARNING);
			}else {
				triggerOptions.setFilter("priority", PRIORITIES_ERROR);
			}
		}
	}

	protected boolean isShowActive() {
		return true;
	}
	
	protected boolean isShowHistory() {
		return true;
	}
	
    /**
     * filter Events
     * value 0 --> History 
     * value 1 --> Active
     * @param cEventGet
     */
	private void doEventsFiler(CEventGet cEventGet) {
		if(isShowActive() && isShowHistory()) {
			//show all
		}else {
			cEventGet.setValue(String.valueOf(
					isShowHistory()? 
					Defines.TRIGGER_VALUE_FALSE: 
					Defines.TRIGGER_VALUE_TRUE
				));
		}
	}
    
    private int getEventSource(SQLExecutor executor){
    	if(isNeedDiscovery()) {
    		return get_request("source", Nest.as(CProfile.get(getIdentityBean(), executor, "web.events.source", EVENT_SOURCE_TRIGGERS)).asInteger());
    	}else {
    		return Defines.EVENT_SOURCE_TRIGGERS;
    	}
    }
    
	/**获取活动告警或者活动故障数目以及最近告警描述方法
	 * @param executor sql执行程序
	 * @param isWarnings true为告警，false则为故障
	 * @return
	 */
	public List<String> getEventList(SQLExecutor executor){
		
		List<String> eventlist=new ArrayList<String>();
		//定义要查找的告警或者故障表
		CTriggerGet triggerOptions = new CTriggerGet();
		triggerOptions.setOutput(new String[]{"triggerid"});
		triggerOptions.setPreserveKeys(true);
		triggerOptions.setMonitored(true);
		//定义要查找的事件表
		CEventGet eventOptions = new CEventGet();
		eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
		eventOptions.setObject(EVENT_OBJECT_TRIGGER);
		eventOptions.setOutput(new String[]{"eventid", "objectid"});
		eventOptions.setSortfield("clock", "eventid");//排序字段
		eventOptions.setSortorder(RDA_SORT_DOWN);//倒序
		//根据事件的条件过滤查询结果，1是活动数据，而非历史数据，这要是区分处理或未处理关键
		eventOptions.setValue(String.valueOf(Defines.TRIGGER_VALUE_TRUE));
		eventOptions.setEditable(true);

		CArray knownTriggerIds = array();
		CArray validTriggerIds = array();
		
		/*这段代码比较难懂，也是关键部分*/
		CArray<Map> allEventsSlice = API.Event(getIdentityBean(), executor).get(eventOptions);
		
		CArray triggerIdsFromSlice = array_keys(array_flip(rda_objectValues(allEventsSlice, "objectid")));

		CArray unknownTriggerIds = array_diff(triggerIdsFromSlice, knownTriggerIds);
		
		if (!empty(unknownTriggerIds)) {
			Nest.value(triggerOptions,"triggerids").$(unknownTriggerIds);
			CArray<Map> validTriggersFromSlice = API.Trigger(getIdentityBean(), executor).get(triggerOptions);

			for(Map trigger:validTriggersFromSlice) {
				validTriggerIds.put(trigger.get("triggerid"),trigger.get("triggerid"));
			}
		}
		
		CArray<Map> events = array();
		
		for(Map event:allEventsSlice) {
			if (isset(validTriggerIds,event.get("objectid"))) {
				events.add(map("eventid" , Nest.value(event,"eventid").$()));//符合条件的事件，这里只存有其id值
			}
		}
		int number = count(events);//取得事件总数目
		
		if(number>100){//如果数目超过100，则表示为100+
			eventlist.add("100+");
		}else{
			eventlist.add(number+"");
		}
		
		return eventlist;
	}
	
	
	 
	 /** 获取描述列跳转到告警规则界面
	 * @param trigger
	 * @return
	 */
	private String getDescriptionUrl(Map trigger){
        if ((CWebUser.getType() == Defines.USER_TYPE_IRADAR_ADMIN ) || (CWebUser.getType() == Defines.USER_TYPE_SUPER_ADMIN)) {//必须有管理员或超级管理员权限
    		if (Nest.value(trigger,"flags").asInteger() == Defines.TRIGGER_VALUE_FLAG_NORMAL ) {//flags表示有保留事件的才是有效的
    			String url="triggers.action?triggerid="+Nest.value(trigger,"triggerid").asString();
    			Map host = (Map)Cphp.reset(Nest.value(trigger,"hosts").asCArray());
    			String hostidpram = "&hostid="+Nest.value(host,"hostid").asString();
    			url+=hostidpram;//将hostid参数加入，因为为管理员编辑等操作需要用到
    			url+="&form=update";
    			return url; 
    		}
    	}
		return null;
	}
	
	public static CLink getHostDetailUrl(Map host){
		String hostId = Nest.value(host, "hostid").asString();
		String url = null;
		CLink link = null;
		CArray<Map> groups = Nest.value(host, "groups").asCArray();
		if(!empty(get_request("groupid", null)))
			url = "'"+_("Host detail")+"', '"+String.format(RadarContext.getContextPath()+IMonConsts.MONITOR_CENTER_HOST_DETAIL, hostId,get_request("groupid", null))+"'";
		else if(!empty(groups)){
			for(Map group:groups){
				if(IMonConsts.MON_GROUP_DEFAULT.containsValue(Nest.value(group, "groupid").asLong())){
					url = "'"+_("Host detail")+"', '"+String.format(RadarContext.getContextPath()+IMonConsts.MONITOR_CENTER_HOST_DETAIL, hostId,Nest.value(group, "groupid").asString())+"'";
					break;
				}
			}
			if(empty(url))
				url = "'"+Nest.value(groups, 0,"name").asString()+"', '"+String.format(RadarContext.getContextPath()+IMonConsts.MONITOR_CENTER_OTHER_DETAIL, hostId,Nest.value(groups, 0,"groupid").asString())+"'";
		}
		link = new CLink(Nest.value(host, "name").asString(),IMonConsts.JS_OPEN_TAB_HEAD.concat(url).concat(IMonConsts.JS_OPEN_TAB_TAIL), null,null,Boolean.TRUE);
		return link;
	}
	
}

