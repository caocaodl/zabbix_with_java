package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.SORT_ASC;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_multisort;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_object;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.strtolower;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.core.utils.EasyObject.asBoolean;
import static com.isoft.iradar.core.utils.EasyObject.asCArray;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JSON;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JSON_RPC;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_TEXT_RETURN_JSON;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_JS;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_CHART;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_HISTORY;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.EventsUtil.getLastEvents;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.HtmlUtil.fatal_error;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.SoundsUtil.getMessageSettings;
import static com.isoft.iradar.inc.SoundsUtil.updateMessageSettings;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityColor;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import com.isoft.Feature;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.common.util.IMonGroup;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.core.utils.EasyServlet;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CAppGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTemplateGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.server.IRadarServer;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.utils.CJs;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class JsrpcAction extends RadarBaseAction {
	private CArray data;
	private Integer requestType;
	
	@Override
	protected void doInitPage() {
		page("type", detect_page_type(PAGE_TYPE_JSON_RPC));
		

		requestType = get_request("type", PAGE_TYPE_JSON);
		if (requestType == PAGE_TYPE_JSON) {
			String body = EasyServlet.getRequestPayload(getRequest()); 
			data = asCArray(CJs.decodeJson(body));
		}
		else {
			data = CArray.valueOf(_REQUEST);
		}
		
		page("title", "RPC");
		page("file", "jsrpc.action");
		page("hist_arg", array());
		page("type", detect_page_type(requestType));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		if (!isArray(data) || !isset(Nest.value(data,"method").$())
				|| (requestType == PAGE_TYPE_JSON && (!isset(Nest.value(data,"params").$()) || !isArray(Nest.value(data,"params").$())))) {
			fatal_error(getIdentityBean(), "Wrong RPC call to JS RPC!");
		}
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	
	private CArray doMessageGet(SQLExecutor executor) {
		CArray result = array();
		CArray msgsettings = getMessageSettings(getIdentityBean(), executor);
		
		// if no severity is selected, show nothing
		if (empty(msgsettings.get("triggers.severities"))) {
			return result;
		}

		// timeout
		long timeout = time() - Nest.value(msgsettings,"timeout").asLong();
		long lastMsgTime = 0;
		if (isset(Nest.value(data,"params","messageLast","events").$())) {
			lastMsgTime = Nest.value(data,"params","messageLast","events","time").asLong();
		}

		CParamGet options = new CParamGet();
		options.put("lastChangeSince" , max(lastMsgTime, Nest.value(msgsettings, "last.clock").asLong(), timeout));
		options.put("value" , new Integer[]{TRIGGER_VALUE_TRUE, TRIGGER_VALUE_FALSE});
		options.put("priority", array_keys(Nest.value(msgsettings,"triggers.severities").asCArray()).valuesAsString());
		options.put("triggerLimit", 15);
		if (empty(msgsettings.get("triggers.recovery"))) {
			options.put("triggerLimit", TRIGGER_VALUE_TRUE);
		}
		CArray<Map> events = getLastEvents(this.getIdentityBean(), executor, options);

		CArray sortClock = array();
		CArray sortEvent = array();

		CArray usedTriggers = array();
		for(Entry<Object, Map> entry: events.entrySet()) {
			Object number = entry.getKey();
			Map event = entry.getValue();
			if (count(usedTriggers) < 15) {
				if (!isset(usedTriggers.get(event.get("objectid")))) {
					CArray trigger = Nest.value(event,"trigger").asCArray();
					CArray host = Nest.value(event,"host").asCArray();

					int priority;
					String title;
					Object sound;
					if (Nest.value(event,"value").asInteger() == TRIGGER_VALUE_FALSE) {
						priority = 0;
						title = _("Resolved");
						sound = msgsettings.get("sounds.recovery");
					}
					else {
						priority = Nest.value(trigger,"priority").asInteger();
						title = _("Problem on");
						sound = msgsettings.get("sounds."+trigger.get("priority"));
					}

					String _url_tr_status = "tr_status.action?hostid="+Nest.value(host,"hostid").$();
					String _url_events = "events.action?triggerid="+event.get("objectid")+"&source="+EVENT_SOURCE_TRIGGERS;
					String _url_tr_events = "tr_events.action?eventid="+event.get("eventid")+"&triggerid="+Nest.value(event,"objectid").$();

					result.put(number, array(
						"type", 3,
						"caption", "events",
						"sourceid", Nest.value(event,"eventid").$(),
						"time", Nest.value(event,"clock").$(),
						"priority", priority,
						"sound", sound,
						"color", getSeverityColor(getIdentityBean(), executor, Nest.value(trigger,"priority").asInteger(), Nest.value(event,"value").asInteger()),
						"title", title+" "+"[url="+_url_tr_status+"]"+host.get("name")+"[/url]",
						"body", array(
							_("Details")+": [url="+_url_events+"]"+trigger.get("description")+"[/url]",
							_("Date")+": [b][url="+_url_tr_events+"]"+rda_date2str(_("d M Y H:i:s"), Nest.value(event,"clock").asLong())+"[/url][/b]"
						),
						"timeout", msgsettings.get("timeout")
					));

					sortClock.put(number, Nest.value(event,"clock").$());
					sortEvent.put(number, Nest.value(event,"eventid").$());
					usedTriggers.put(event.get("objectid"), true);
				}
			}
			else {
				break;
			}
		}
		array_multisort(sortClock, SORT_ASC, sortEvent, SORT_ASC, result);
		return result;
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		Object result = array();
		Object method = Nest.value(data,"method").$();
		
		if("host.get".equals(method)) {
			CHostGet hoptions = new CHostGet();
			hoptions.setStartSearch(true);
			hoptions.setSearch(Nest.value(data,"params","search").asCArray());
			hoptions.setOutput(new String[]{"hostid", "host", "name"});
			hoptions.setSortfield("name");
			hoptions.setLimit(15);
			result = API.Host(getIdentityBean(), executor).get(hoptions);
		}else if("message.mute".equals(method)) {
			CArray msgsettings = getMessageSettings(getIdentityBean(), executor);
			msgsettings.put("sounds.mute", 1);
			updateMessageSettings(getIdentityBean(), executor, msgsettings);
		}else if("message.unmute".equals(method)) {
			CArray msgsettings = getMessageSettings(getIdentityBean(), executor);
			msgsettings.put("sounds.mute", 0);
			updateMessageSettings(getIdentityBean(), executor, msgsettings);
		}else if("message.settings".equals(method)) {
			result = getMessageSettings(getIdentityBean(), executor);
		}else if("message.get".equals(method)) {
			result = doMessageGet(executor);
		}else if("message.closeAll".equals(method)) {
			CArray msgsettings = getMessageSettings(getIdentityBean(), executor);
			String _caption = strtolower(Nest.value(data,"params","caption").asString());
			if("events".equals(_caption)) {
				msgsettings.put("last.clock", Nest.value(data,"params","time").asInteger() + 1);
				updateMessageSettings(getIdentityBean(), executor, msgsettings);
			}
		}else if("iradar.status".equals(method)) {
			HttpSession session = getSession();
			if (!isset(session.getAttribute("serverCheckResult")) || (EasyObject.asLong(session.getAttribute("serverCheckTime")) + Defines.SERVER_CHECK_INTERVAL) <= time()) {
				IRadarServer iradarServer = new IRadarServer(Feature.iradarServer, Feature.iradarPort, Defines.RDA_SOCKET_TIMEOUT, 0);
				session.setAttribute("serverCheckResult", iradarServer.isRunning());
				session.setAttribute("serverCheckTime", time());
				iradarServer.close();
			}
	
			boolean _serverCheckResult = asBoolean( session.getAttribute("serverCheckResult") );
			result = map(
				"result", _serverCheckResult,
				"message", _serverCheckResult ? "" : Cphp._s("iRadar server is not running: the information displayed may not be current.", Cphp.date("Y-m-d H:i:s", EasyObject.asLong(session.getAttribute("serverCheckTime"))))
			);
		}else if("screen.get".equals(method)) {
			CArray options = map(
				"pageFile", !empty(Nest.value(data,"pageFile").$()) ? Nest.value(data,"pageFile").$() : null,
				"mode", !empty(Nest.value(data,"mode").$()) ? Nest.value(data,"mode").$() : null,
				"timestamp", !empty(Nest.value(data,"timestamp").$()) ? Nest.value(data,"timestamp").$() : time(),
				"resourcetype", !empty(Nest.value(data,"resourcetype").$()) ? Nest.value(data,"resourcetype").$() : null,
				"screenitemid", !empty(Nest.value(data,"screenitemid").$()) ? Nest.value(data,"screenitemid").$() : null,
				"groupid", !empty(Nest.value(data,"groupid").$()) ? Nest.value(data,"groupid").$() : null,
				"hostid", !empty(Nest.value(data,"hostid").$()) ? Nest.value(data,"hostid").$() : null,
				"period", !empty(Nest.value(data,"period").$()) ? Nest.value(data,"period").$() : null,
				"stime", !empty(Nest.value(data,"stime").$()) ? Nest.value(data,"stime").$() : null,
				"profileIdx", !empty(Nest.value(data,"profileIdx").$()) ? Nest.value(data,"profileIdx").$() : null,
				"profileIdx2", !empty(Nest.value(data,"profileIdx2").$()) ? Nest.value(data,"profileIdx2").$() : null,
				"updateProfile", isset(Nest.value(data,"updateProfile").$()) ? Nest.value(data,"updateProfile").$() : null
			);
			if (Nest.value(options,"resourcetype").asInteger() == SCREEN_RESOURCE_HISTORY) {
				Nest.value(options,"itemids").$(!empty(Nest.value(data,"itemids").$()) ? Nest.value(data,"itemids").$() : null);
				Nest.value(options,"action").$(!empty(Nest.value(data,"action").$()) ? Nest.value(data,"action").$() : null);
				Nest.value(options,"filter").$(!empty(Nest.value(data,"filter").$()) ? Nest.value(data,"filter").$() : null);
				Nest.value(options,"filter_task").$(!empty(Nest.value(data,"filter_task").$()) ? Nest.value(data,"filter_task").$() : null);
				Nest.value(options,"mark_color").$(!empty(Nest.value(data,"mark_color").$()) ? Nest.value(data,"mark_color").$() : null);
			}
			else if (Nest.value(options,"resourcetype").asInteger() == SCREEN_RESOURCE_CHART) {
				Nest.value(options,"graphid").$(!empty(Nest.value(data,"graphid").$()) ? Nest.value(data,"graphid").$() : null);
				Nest.value(options,"profileIdx2").$(Nest.value(options,"graphid").$());
			}
	
			CScreenBase screenBase = CScreenBuilder.getScreen(getIdentityBean(), executor, options);
			Object screen = null;
			if (!empty(screenBase)) {
				screen = screenBase.get();
			}
	
			if (!empty(screen)) {
				if (Nest.value(options,"mode").asInteger() == SCREEN_MODE_JS) {
					result = screen;
				}
				else {
					if (is_object(screen)) {
						result = screen.toString();
					}
				}
			}
			else {
				result = "";
			}
		}else if("multiselect.get".equals(method)) {
			doMultiselectGet(executor, data, (CArray)result);
		}else {
			fatal_error(getIdentityBean(), "Wrong RPC call to JS RPC!");
		}
		
		
		if (requestType == PAGE_TYPE_JSON) {
			if (isset(Nest.value(data,"id").$())) {
				echo( CJs.encodeJson(map(
					"jsonrpc" , "2.0",
					"result" , result,
					"id" , data.get("id")
				)));
			}
		}
		else if (requestType == PAGE_TYPE_TEXT_RETURN_JSON) {
			echo( CJs.encodeJson(map(
				"jsonrpc", "2.0",
				"result", result
			)) );
		}
		else if (requestType == PAGE_TYPE_TEXT || requestType == PAGE_TYPE_JS) {
			echo (result.toString());
		}
		return true;
	}
	
	/**
	 * Create multi select data.
	 * Supported objects: \"applications\", \"hosts\", \"hostGroup\", \"templates\", \"triggers\"
	 *
	 * @param string data["objectName"]
	 * @param string data["search"]
	 * @param int    data["limit"]
	 *
	 * @return array(int => array("value" => int, "text" => string))
	 */
	private void doMultiselectGet(SQLExecutor executor, CArray data, CArray result) {
		Map config = select_config(getIdentityBean(), executor);
		CArray sortFields = array();

		Object _objectName = Nest.value(data,"objectName").$();
		if("hostGroup".equals(_objectName)) {
			CHostGroupGet hgoptions = new CHostGroupGet();
			if(isset(data,"editable")){
				hgoptions.setEditable(Nest.value(data,"editable").asBoolean());
			}
			hgoptions.setOutput(new String[]{"groupid", "name"});
			if(isset(data,"search")){
				hgoptions.setSearch("name", Nest.value(data,"search").asString());
			}
			if(isset(data,"filter")){
				hgoptions.setFilter(Nest.value(data,"filter").asCArray());
			}
			if(isset(data,"limit")){
				hgoptions.setLimit(Nest.value(data,"limit").asInteger());
			}
			CArray<Map> hostGroups = API.HostGroup(getIdentityBean(), executor).get(hgoptions);

			if (!empty(hostGroups)) {
				sortFields.add( map("field", "name", "order", RDA_SORT_UP) );
				CArrayHelper.sort(hostGroups, sortFields);

				if (isset(Nest.value(data,"limit").$())) {
					hostGroups = array_slice(hostGroups, 0, Nest.value(data,"limit").asInteger());
				}

				for(Map hostGroup: hostGroups) {
					if(IMonGroup.MON_VM.id().equals(Nest.value(hostGroup,"groupid").asLong()))
						continue;
					result.add( map(
						"id" , Nest.value(hostGroup,"groupid").$(),
						"prefix" , "",
						"name" , hostGroup.get("name")
					));
				}
			}
		}else if("hosts".equals(_objectName)) {
			CHostGet hoptions = new CHostGet();
			if(isset(data,"editable")){
				hoptions.setEditable(Nest.value(data,"editable").asBoolean());
			}
			hoptions.setOutput(new String[]{"hostid", "name"});
			if(isset(data,"templated_hosts")){
				hoptions.setTemplatedHosts(Nest.value(data,"templated_hosts").asBoolean());
			}
			if(isset(data,"search")){
				hoptions.setSearch("name", Nest.value(data,"search").asString());
			}
			hoptions.setLimit(Nest.value(config,"search_limit").asInteger());
			CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);

			if (!empty(hosts)) {
				sortFields.add( map("field", "name", "order", RDA_SORT_UP) );
				CArrayHelper.sort(hosts, sortFields);

				if (isset(Nest.value(data,"limit").$())) {
					hosts = array_slice(hosts, 0, Nest.value(data,"limit").asInteger());
				}

				for(Map host: hosts) {
					result.add( map(
						"id", Nest.value(host,"hostid").$(),
						"prefix", "",
						"name", host.get("name")
					));
				}
			}
		}else if("templates".equals(_objectName)) {
			CTemplateGet toptions = new CTemplateGet();
			if(isset(data,"editable")){
				toptions.setEditable(Nest.value(data,"editable").asBoolean());
			}
			toptions.setOutput(new String[]{"templateid", "name"});
			if(isset(data,"search")){
				toptions.setSearch("name", Nest.value(data,"search").asString());
			}
			toptions.setLimit(Nest.value(config,"search_limit").asInteger());
			CArray<Map> templates = API.Template(getIdentityBean(), executor).get(toptions);

			if (!empty(templates)) {
				sortFields.add( map("field" , "name", "order" , RDA_SORT_UP) );
				CArrayHelper.sort(templates, sortFields);

				if (isset(Nest.value(data,"limit").$())) {
					templates = array_slice(templates, 0, Nest.value(data,"limit").asInteger());
				}

				for(Map template: templates) {
					result.add( map(
						"id" , Nest.value(template,"templateid").$(),
						"prefix" , "",
						"name" , template.get("name")
					));
				}
			}
		}else if("applications".equals(_objectName)) {
			CAppGet aoptions = new CAppGet();
			aoptions.setHostIds(Nest.array(data,"hostid").asLong());
			aoptions.setOutput(new String[]{"applicationid", "name"});
			if(isset(data,"search")){
				aoptions.setSearch("name", Nest.value(data,"search").asString());
			}
			aoptions.setLimit(Nest.value(config,"search_limit").asInteger());
			CArray<Map> applications = API.Application(getIdentityBean(), executor).get(aoptions);

			if (!empty(applications)) {
				sortFields.add( map("field" , "name", "order" , RDA_SORT_UP) );
				CArrayHelper.sort(applications, sortFields);

				if (isset(Nest.value(data,"limit").$())) {
					applications = array_slice(applications, 0, Nest.value(data,"limit").asInteger());
				}

				for(Map application: applications) {
					result.add( map(
						"id" , Nest.value(application,"applicationid").$(),
						"prefix" , "",
						"name" , application.get("name")
					));
				}
			}
		}else if("triggers".equals(_objectName)) {
			CTriggerGet toptions = new CTriggerGet();
			if(isset(data,"editable")){
				toptions.setEditable(Nest.value(data,"editable").asBoolean());
			}
			toptions.setOutput(new String[]{"triggerid", "description"});
			toptions.setSelectHosts(new String[]{"name"});
			if(isset(data,"search")){
				toptions.setSearch("name", Nest.value(data,"search").asString());
			}
			toptions.setLimit(Nest.value(config,"search_limit").asInteger());
			CArray<Map> triggers = API.Trigger(getIdentityBean(), executor).get(toptions);

			if (!empty(triggers)) {
				sortFields.add( map("field" , "description", "order" , RDA_SORT_UP) );
				CArrayHelper.sort(triggers, sortFields);

				if (isset(Nest.value(data,"limit").$())) {
					triggers = array_slice(triggers, 0, Nest.value(data,"limit").asInteger());
				}

				for(Map trigger: triggers) {
					String hostName = "";

					if (Nest.value(trigger,"hosts").asBoolean()) {
						Nest.value(trigger,"hosts").$(reset(Nest.value(trigger,"hosts").asCArray()));

						hostName = Nest.value(trigger, "hosts", "name").$()+NAME_DELIMITER;
					}

					result.add( map(
						"id" , Nest.value(trigger,"triggerid").$(),
						"prefix" , hostName,
						"name" , trigger.get("description")
					));
				}
			}
		}
		
	}

	@Override
	protected void doAction(SQLExecutor executor) {
	}
	
}
