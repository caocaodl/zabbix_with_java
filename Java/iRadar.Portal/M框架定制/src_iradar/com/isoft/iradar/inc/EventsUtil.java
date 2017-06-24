package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.SORT_DESC;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_multisort;
import static com.isoft.iradar.Cphp.array_unique;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_bool;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.AcknowUtil.makeAckTab;
import static com.isoft.iradar.inc.ActionsUtil.getEventActionsStatHints;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_AUTOREGHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DHOST;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_DSERVICE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_ITEM;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_LLDRULE;
import static com.isoft.iradar.inc.Defines.EVENT_OBJECT_TRIGGER;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_AUTO_REGISTRATION;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_DISCOVERY;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_INTERNAL;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_WIDGET_ROWS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.MapsUtil.get_map_elements;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.addTriggerValueStyle;
import static com.isoft.iradar.inc.TriggersUtil.trigger_value2str;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CParamGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.lang.CodeConfirmed;
import com.isoft.lang.CodeTodo;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

@CodeConfirmed("benne.2.2.6")
public class EventsUtil {
	
	private EventsUtil() {
	}
	
	/**
	 * Returns the names of supported event sources.
	 *
	 * If the source parameter is passed, returns the name of the specific source, otherwise - returns an array of all
	 * supported sources.
	 *
	 * @param int source
	 *
	 * @return array|string
	 */
	public static CArray<String> eventSource() {
		CArray<String> sources = map(
			EVENT_SOURCE_TRIGGERS, _("trigger"),
			EVENT_SOURCE_DISCOVERY, _("discovery"),
			EVENT_SOURCE_AUTO_REGISTRATION, _("auto registration"),
			EVENT_SOURCE_INTERNAL, _x("internal", "event source")
		);
		return sources;
	}
	
	/**
	 * Returns the names of supported event sources.
	 *
	 * If the source parameter is passed, returns the name of the specific source, otherwise - returns an array of all
	 * supported sources.
	 *
	 * @param int source
	 *
	 * @return string
	 */
	public static String eventSource(int source) {
		CArray<String> sources = map(
			EVENT_SOURCE_TRIGGERS, _("trigger"),
			EVENT_SOURCE_DISCOVERY, _("discovery"),
			EVENT_SOURCE_AUTO_REGISTRATION, _("auto registration"),
			EVENT_SOURCE_INTERNAL, _x("internal", "event source")
		);
		if (isset(sources,source)) {
			return sources.get(source);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Returns the names of supported event objects.
	 *
	 * If the source parameter is passed, returns the name of the specific object, otherwise - returns an array of all
	 * supported objects.
	 *
	 * @param int object
	 *
	 * @return array|string
	 */
	public static CArray<String> eventObject() {
		CArray<String> objects = map(
			EVENT_OBJECT_TRIGGER , _("trigger"),
			EVENT_OBJECT_DHOST , _("discovered host"),
			EVENT_OBJECT_DSERVICE , _("discovered service"),
			EVENT_OBJECT_AUTOREGHOST , _("auto-registered host"),
			EVENT_OBJECT_ITEM , _("item"),
			EVENT_OBJECT_LLDRULE , _("low-level discovery rule")
		);
		return objects;
	}
	
	/**
	 * Returns the names of supported event objects.
	 *
	 * If the source parameter is passed, returns the name of the specific object, otherwise - returns an array of all
	 * supported objects.
	 *
	 * @param int object
	 *
	 * @return string
	 */
	public static String eventObject(int object) {
		CArray<String> objects = map(
			EVENT_OBJECT_TRIGGER , _("trigger"),
			EVENT_OBJECT_DHOST , _("discovered host"),
			EVENT_OBJECT_DSERVICE , _("discovered service"),
			EVENT_OBJECT_AUTOREGHOST , _("auto-registered host"),
			EVENT_OBJECT_ITEM , _("item"),
			EVENT_OBJECT_LLDRULE , _("low-level discovery rule")
		);
		if (isset(objects,object)) {
			return objects.get(object);
		} else {
			return _("Unknown");
		}
	}
	
	/**
	 * Returns all supported event source-object pairs.
	 *
	 * @return array
	 */
	public static CArray<CArray<Integer>>  eventSourceObjects() {
		return (CArray)array(
			map("source" , EVENT_SOURCE_TRIGGERS, "object" , EVENT_OBJECT_TRIGGER),
			map("source" , EVENT_SOURCE_DISCOVERY, "object" , EVENT_OBJECT_DHOST),
			map("source" , EVENT_SOURCE_DISCOVERY, "object" , EVENT_OBJECT_DSERVICE),
			map("source" , EVENT_SOURCE_AUTO_REGISTRATION, "object" , EVENT_OBJECT_AUTOREGHOST),
			map("source" , EVENT_SOURCE_INTERNAL, "object" , EVENT_OBJECT_TRIGGER),
			map("source" , EVENT_SOURCE_INTERNAL, "object" , EVENT_OBJECT_ITEM),
			map("source" , EVENT_SOURCE_INTERNAL, "object" , EVENT_OBJECT_LLDRULE)
		);
	}
	
	public static Map get_tr_event_by_eventid(IIdentityBean idBean, SQLExecutor executor, String eventid) {
		SqlBuilder sqlParts = new SqlBuilder();
		String sql = "SELECT e.*,t.triggerid,t.description,t.expression,t.priority,t.status,t.type"+
							" FROM events e,triggers t"+
							" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "events", "e")+
							" AND e.eventid="+sqlParts.marshalParam(eventid)+
							" AND e.object="+EVENT_OBJECT_TRIGGER+
							" AND t.tenantid=e.tenantid"+
							" AND t.triggerid=e.objectid";
		return DBfetch(DBselect(executor, sql, sqlParts.getNamedParams()));
	}
	
	public static CArray<Map> get_events_unacknowledged(IIdentityBean idBean, SQLExecutor executor, Map db_element) {
		return get_events_unacknowledged(idBean, executor, db_element, null);
	}
	
	public static CArray<Map> get_events_unacknowledged(IIdentityBean idBean, SQLExecutor executor, Map db_element, Integer value_trigger) {
		return get_events_unacknowledged(idBean, executor, db_element, value_trigger, null);
	}
	
	public static CArray<Map> get_events_unacknowledged(IIdentityBean idBean, SQLExecutor executor, Map db_element, Integer value_trigger, Integer value_event) {
		return get_events_unacknowledged(idBean, executor, db_element, value_trigger, value_event, false);
	}
	
	public static CArray<Map> get_events_unacknowledged(IIdentityBean idBean, SQLExecutor executor, Map db_element, Integer value_trigger, Integer value_event, boolean ack) {
		CArray elements = map("hosts", array(), "hosts_groups", array(), "triggers", array());
		get_map_elements(idBean, executor, db_element, elements);
		
		if (empty(Nest.value(elements,"hosts_groups").$()) && empty(Nest.value(elements,"hosts").$()) && empty(Nest.value(elements,"triggers").$())) {
			return null;
		}
		
		Map<String, Object> config = select_config(idBean, executor);
		CTriggerGet options = new CTriggerGet();
		options.setOutput(new String[]{"triggerid"});
		options.setMonitored(true);
		options.setSkipDependent(true);
		options.setLimit(Nest.value(config,"search_limit").asInteger() + 1);
		if (!is_null(value_trigger)) {
			options.setFilter("value" , Nest.as(value_trigger).asString());
		}
		if (!empty(Nest.value(elements,"hosts_groups").$())) {
			options.setGroupIds(array_unique(Nest.value(elements,"hosts_groups").asCArray()).valuesAsLong());
		}
		if (!empty(Nest.value(elements,"hosts").$())) {
			options.setHostIds(array_unique(Nest.value(elements,"hosts").asCArray()).valuesAsLong());
		}
		if (!empty(Nest.value(elements,"triggers").$())) {
			options.setTriggerIds(array_unique(Nest.value(elements,"triggers").asCArray()).valuesAsLong());
		}
		CArray<Map> triggerids = API.Trigger(idBean, executor).get(options);
		CEventGet eoptions = new CEventGet();
		eoptions.setSource(EVENT_SOURCE_TRIGGERS);
		eoptions.setObject(EVENT_OBJECT_TRIGGER);
		eoptions.setCountOutput(true);
		eoptions.setObjectIds(rda_objectValues(triggerids, "triggerid").valuesAsLong());
		eoptions.setFilter("value", Nest.as(value_event).asString());
		eoptions.setFilter("acknowledged", Nest.as(ack ? 1 : 0).asString());
		return API.Event(idBean, executor).get(eoptions);
	}
	
	public static Map get_next_event(IIdentityBean idBean, SQLExecutor executor, Map currentEvent) {
		return get_next_event(idBean, executor, currentEvent, array());
	}
	
	public static Map get_next_event(IIdentityBean idBean, SQLExecutor executor, Map currentEvent, CArray<Map> eventList) {
		Map nextEvent = null;

		for (Map event : eventList) {
			// check only the events belonging to the same object
			// find the event with the smallest eventid but greater than the current event id
			if (Nest.value(event,"object").asInteger() == Nest.value(currentEvent,"object").asInteger() 
					&& Nest.value(event,"objectid").asLong()==Nest.value(currentEvent,"objectid").asLong()
					&& Nest.value(event,"eventid").asLong()>Nest.value(currentEvent,"eventid").asLong()
					&& (nextEvent==null || Nest.value(event,"eventid").asLong()<Nest.value(nextEvent,"eventid").asLong())) {
				nextEvent = event;
			}
		}
		if (nextEvent != null) {
			return nextEvent;
		}

		SqlBuilder sqlParts = new SqlBuilder();
		String sql =  "SELECT e.*"+
							" FROM events e"+
							" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "events", "e")+
								" AND e.source=#{source}"+
								" AND e.object=#{object}"+
								" AND e.objectid=#{objectid}"+
								" AND e.clock>=#{clock}"+
								" AND ((e.clock=#{clock} AND e.ns>#{ns}) OR e.clock>#{clock})"+
							" ORDER BY e.clock,e.eventid";
		Map params = sqlParts.getNamedParams();
		params.put("source", Nest.value(currentEvent,"source").$());
		params.put("object", Nest.value(currentEvent,"object").$());
		params.put("objectid", Nest.value(currentEvent,"objectid").$());
		params.put("clock", Nest.value(currentEvent,"clock").$());
		params.put("ns", Nest.value(currentEvent,"ns").$());
		return DBfetch(DBselect(executor, sql, 1, params));
	}
	
	@CodeTodo
	public static CTableInfo make_event_details(IIdentityBean idBean, SQLExecutor executor, Map event, Map trigger) {
		Map<String, Object> config = ProfilesUtil.select_config(idBean, executor);
		CTableInfo table = new CTableInfo();

		table.addRow(array(_("Event"), CMacrosResolverHelper.resolveEventDescription(idBean, executor, array_merge(trigger, event))));
		table.addRow(array(_("Time"), rda_date2str(_("d M Y H:i:s"), Nest.value(event,"clock").asLong())));

		if (Nest.value(config,"event_ack_enable").asBoolean()) {
			// to make resulting link not have hint with acknowledges
			Nest.value(event,"acknowledged").$(count(Nest.value(event,"acknowledges").$()));
			Nest.value(event, "acknowledges").$(Nest.value(event,"acknowledged").$());
			Object ack = getEventAckState(idBean, executor, event, true);
			table.addRow(array(_("Acknowledged"), ack));
		}
		return table;
	}
	
	public static CTableInfo make_small_eventlist(IIdentityBean idBean, SQLExecutor executor, Map startEvent) {
		Map<String, Object> config = select_config(idBean, executor);
		
		CTableInfo table = new CTableInfo(_("No events found."));
		table.setHeader(array(
			_("Time"),
			_("Status"),
			_("Duration"),
			_("Age"),
			Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null, // if we need to chow acks
			_("Actions")
		));
		
		long clock = Nest.value(startEvent,"clock").asLong();
		
		CEventGet options = new CEventGet();
		options.setSource(EVENT_SOURCE_TRIGGERS);
		options.setObject(EVENT_OBJECT_TRIGGER);
		options.setObjectIds(Nest.value(startEvent,"objectid").asLong());
		options.setEventIdTill(Nest.value(startEvent,"eventid").$());
		options.setOutput(API_OUTPUT_EXTEND);
		options.setSelectAcknowledges(API_OUTPUT_COUNT);
		options.setSortfield("clock", "eventid");
		options.setSortorder(RDA_SORT_DOWN);
		options.setLimit(20);
		CArray<Map> events = API.Event(idBean, executor).get(options);
		
		CArray sortFields = array(
			map("field", "clock", "order", RDA_SORT_DOWN),
			map("field", "eventid", "order", RDA_SORT_DOWN)
		);
		CArrayHelper.sort(events, sortFields);
		
		CArray actions = getEventActionsStatHints(idBean, executor, rda_objectValues(events, "eventid"));
		
		String duration = null;
		Map nextevent = null;
		CSpan eventStatusSpan = null;
		Object ack = null;
		for (Map event : events) {
			duration = rda_date2age(clock, Nest.value(event,"clock").asLong());
			clock = Nest.value(event,"clock").asLong();			
			
			if (Nest.value(startEvent,"eventid").$().equals(Nest.value(event,"eventid").$()) && (nextevent = get_next_event(idBean, executor, event, events))!=null) {
				duration = rda_date2age(Nest.value(nextevent,"clock").asLong(), clock);
			} else if (Nest.value(startEvent,"eventid").$().equals(Nest.value(event,"eventid").$())) {
				duration = rda_date2age(clock);
			}
			
			eventStatusSpan = new CSpan(trigger_value2str(Nest.value(event,"value").asInteger()));

			// add colors and blinking to span depending on configuration and trigger parameters
			addTriggerValueStyle(
				idBean, 
				executor,
				eventStatusSpan,
				Nest.value(event,"value").asInteger(),
				Nest.value(event,"clock").asInteger(),
				Nest.value(event,"acknowledged").asBoolean());
			
			ack = getEventAckState(idBean, executor, event, true);
			
			table.addRow(array(
					new CLink(
						rda_date2str(_("d M Y H:i:s"), Nest.value(event,"clock").asLong()),
						"tr_events.action?triggerid="+Nest.value(event,"objectid").asString()+"&eventid="+Nest.value(event,"eventid").asString(),
						"action"),
					eventStatusSpan,
					duration,
					rda_date2age(Nest.value(event,"clock").asLong()),
					Nest.value(config,"event_ack_enable").asBoolean() ? ack : null,
					isset(actions,event.get("eventid")) ? actions.get(event.get("eventid")) : SPACE));
		}
		return table;
	}
	
	public static CTableInfo make_popup_eventlist(IIdentityBean idBean, SQLExecutor executor, Long triggerId, Long eventId) {
		Map<String, Object> config = select_config(idBean, executor);
		
		CTableInfo table = new CTableInfo();
		table.setAttribute("style", "width: 400px;");

		// if acknowledges are turned on, we show "ack" column
		if (Nest.value(config,"event_ack_enable").asBoolean()) {
			table.setHeader(array(_("Time"), _("Status"), _("Duration"), _("Age"), _("Ack")));
		} else {
			table.setHeader(array(_("Time"), _("Status"), _("Duration"), _("Age")));
		}
		
		CEventGet options = new CEventGet();
		options.setSource(EVENT_SOURCE_TRIGGERS);
		options.setObject(EVENT_OBJECT_TRIGGER);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setObjectIds(triggerId);
		options.setEventIdTill(eventId);
		options.setSelectAcknowledges(API_OUTPUT_COUNT);
		options.setSortfield("clock", "eventid");
		options.setSortorder(RDA_SORT_DOWN);
		options.setLimit(RDA_WIDGET_ROWS);
		CArray<Map> events = API.Event(idBean, executor).get(options);
		
		long lclock = time();
		String duration = null;
		CSpan eventStatusSpan = null;
		for (Map event : events) {
			duration = rda_date2age(lclock, Nest.value(event,"clock").asLong());
			lclock = Nest.value(event,"clock").asLong();

			eventStatusSpan = new CSpan(trigger_value2str(Nest.value(event,"value").asInteger()));

			// add colors and blinking to span depending on configuration and trigger parameters
			addTriggerValueStyle(idBean, executor, eventStatusSpan, Nest.value(event,"value").asInteger(), Nest.value(event,"clock").asInteger(), Nest.value(event,"acknowledged").asBoolean());

			table.addRow(array(
				rda_date2str(_("d M Y H:i:s"), Nest.value(event,"clock").asLong()),
				eventStatusSpan,
				duration,
				rda_date2age(Nest.value(event,"clock").asLong()),
				getEventAckState(idBean, executor, event, false, false)
			));
		}
		return table;
	}
	
	/**
	 * Create element with event acknowledges info.
	 * If event has subarray 'acknowledges', returned link will have hint with acknowledges.
	 *
	 * @param array			event   event data
	 * @param int			event['acknowledged']
	 * @param int			event['eventid']
	 * @param int			event['objectid']
	 * @param array			event['acknowledges']
	 * @param bool|string	backUrl if true, add backurl param to link with current page file name
	 * @param bool			isLink  if true, return link otherwise span
	 * @param array			params  additional params for link
	 *
	 * @return array|CLink|CSpan|null|string
	 */
	public static Object getEventAckState(IIdentityBean idBean, SQLExecutor executor, Map event) {
		return getEventAckState(idBean, executor, event, false);
	}
	
	/**
	 * Create element with event acknowledges info.
	 * If event has subarray 'acknowledges', returned link will have hint with acknowledges.
	 *
	 * @param array			event   event data
	 * @param int			event['acknowledged']
	 * @param int			event['eventid']
	 * @param int			event['objectid']
	 * @param array			event['acknowledges']
	 * @param bool|string	backUrl if true, add backurl param to link with current page file name
	 * @param bool			isLink  if true, return link otherwise span
	 * @param array			params  additional params for link
	 *
	 * @return array|CLink|CSpan|null|string
	 */
	public static Object getEventAckState(IIdentityBean idBean, SQLExecutor executor, Map event, Object backUrl) {
		return getEventAckState(idBean, executor, event, backUrl, true);
	}
	
	/**
	 * Create element with event acknowledges info.
	 * If event has subarray 'acknowledges', returned link will have hint with acknowledges.
	 *
	 * @param array			event   event data
	 * @param int			event['acknowledged']
	 * @param int			event['eventid']
	 * @param int			event['objectid']
	 * @param array			event['acknowledges']
	 * @param bool|string	backUrl if true, add backurl param to link with current page file name
	 * @param bool			isLink  if true, return link otherwise span
	 * @param array			params  additional params for link
	 *
	 * @return array|CLink|CSpan|null|string
	 */
	public static Object getEventAckState(IIdentityBean idBean, SQLExecutor executor, Map event, Object backUrl, boolean isLink) {
		return getEventAckState(idBean, executor, event, backUrl, isLink, array());
	}
	
	/**
	 * Create element with event acknowledges info.
	 * If event has subarray "acknowledges", returned link will have hint with acknowledges.
	 *
	 * @param array			event   event data
	 * @param int			event["acknowledged"]
	 * @param int			event["eventid"]
	 * @param int			event["objectid"]
	 * @param array			event["acknowledges"]
	 * @param bool|string	backUrl if true, add backurl param to link with current page file name
	 * @param bool			isLink  if true, return link otherwise span
	 * @param array			params  additional params for link
	 *
	 * @return array|CLink|CSpan|null|string
	 */
	public static Object getEventAckState(IIdentityBean idBean, SQLExecutor executor, Map event, Object backUrl, boolean isLink, CArray params) {
		Map config = select_config(idBean, executor);

		if (!Nest.value(config,"event_ack_enable").asBoolean()) {
			return null;
		}

		Object ack = null;
		if (isLink) {
			String backurl = null;
			if (!empty(backUrl)) {
				if (is_bool(backUrl)) {
					Map<String, Object> page = RadarContext.getContext().getPage();
					backurl  = "&backurl="+Nest.value(page,"file").asString();
				} else {
					backurl = "&backurl="+backUrl;
				}
			} else {
				backurl = "";
			}

			String additionalParams = "";
			Object key = null, value = null;
			for(Entry e:(Set<Entry>)params.entrySet()) {
				key = e.getKey();
				value = e.getValue();
				additionalParams += "&"+key+"="+value;
			}

			if (Nest.value(event,"acknowledged").asInteger()==0) {
				ack = new CLink(_("No"), "acknow.action?eventid="+event.get("eventid")+"&triggerid="+event.get("objectid")+backurl+additionalParams, "disabled");
			} else {
				CLink ackLink = new CLink(_("Yes"), "acknow.action?eventid="+event.get("eventid")+"&triggerid="+event.get("objectid")+backurl+additionalParams, "enabled");
				if (isArray(Nest.value(event,"acknowledges").$())) {
					CTableInfo ackLinkHints = makeAckTab(event);
					if (!empty(ackLinkHints)) {
						ackLink.setHint(ackLinkHints, "", "", false);
					}
					ack = array(ackLink, " ("+count(Nest.value(event,"acknowledges").$())+")");
				} else {
					ack = array(ackLink, " ("+event.get("acknowledges")+")");
				}
			}
		} else {
			if (Nest.value(event,"acknowledged").asInteger() == 0) {
				ack = new CSpan(_("No"), "disabled");
			} else {
				ack = array(new CSpan(_("Yes"), "enabled"), " ("+(isArray(Nest.value(event,"acknowledges").$()) ? count(Nest.value(event,"acknowledges").$()) : Nest.value(event,"acknowledges").$())+")");
			}
		}
		return ack;
	}
	
	public static CArray<Map> getLastEvents(IIdentityBean idBean, SQLExecutor executor, CParamGet options) {
		if (!isset(options.getLimit())) {
			options.setLimit(15);
		}

		CTriggerGet triggerOptions = new CTriggerGet();
		triggerOptions.setFilter(new HashMap());
		triggerOptions.setSkipDependent(true);
		triggerOptions.setSelectHosts(new String[]{"hostid", "host"});
		triggerOptions.setOutput(API_OUTPUT_EXTEND);
		triggerOptions.setSortfield("lastchange");
		triggerOptions.setSortorder(RDA_SORT_DOWN);
		triggerOptions.setLimit(Nest.value(options, "triggerLimit").asInteger());
		
		CEventGet eventOptions = new CEventGet();
		eventOptions.setSource(EVENT_SOURCE_TRIGGERS);
		eventOptions.setObject(EVENT_OBJECT_TRIGGER);
		eventOptions.setOutput(API_OUTPUT_EXTEND);
		eventOptions.setSortfield("clock", "eventid");
		eventOptions.setSortorder(RDA_SORT_DOWN);

		if (isset(options,"eventLimit")) {
			eventOptions.setLimit(Nest.value(options, "eventLimit").asInteger());
		}
		if (isset(options,"priority")) {
			triggerOptions.setFilter("priority", Nest.array(options,"priority").asString());
		}
		if (isset(options,"monitored")) {
			triggerOptions.setMonitored(Nest.value(options,"monitored").asBoolean());
		}
		if (isset(options,"lastChangeSince")) {
			triggerOptions.setLastChangeSince(Nest.value(options,"lastChangeSince").asLong());
			eventOptions.setTimeFrom(Nest.value(options,"lastChangeSince").asLong());
		}
		if (isset(options,"value")) {
			triggerOptions.setFilter("value", Nest.array(options,"value").asString());
			eventOptions.setValue(Nest.value(options,"value").asString());
		}

		// triggers
		CArray<Map> triggerList = API.Trigger(idBean, executor).get(triggerOptions);
		CArray<Map> triggers = rda_toHash(triggerList, "triggerid");

		// events
		eventOptions.setObjectIds(rda_objectValues(triggers, "triggerid").valuesAsLong());
		CArray<Map> events = API.Event(idBean, executor).get(eventOptions);

		CArray sortClock = array();
		CArray sortEvent = array();
		Map merged_event = null;
		for(Entry<Object, Map> e:(Set<Entry<Object, Map>>)events.entrySet()) {
			Object num = e.getKey();
			Map event = e.getValue();
			if (!isset(triggers, event.get("objectid"))) {
				continue;
			}
			events.get(num).put("trigger", triggers.get(event.get("objectid")));
			events.get(num).put("host", reset((Map)((Map)events.get(num).get("trigger")).get("hosts")));
			sortClock.put(num, Nest.value(event, "clock").$());
			sortEvent.put(num, Nest.value(event, "eventid").$());
			
			//expanding description for the state where event was
			merged_event = array_merge(event, triggers.get(event.get("objectid")));
			Nest.value(events,num,"trigger","description").$(CMacrosResolverHelper.resolveEventDescription(idBean, executor, merged_event));
		}
		array_multisort(sortClock, SORT_DESC, sortEvent, SORT_DESC, events);

		return events;
	}
	
}
