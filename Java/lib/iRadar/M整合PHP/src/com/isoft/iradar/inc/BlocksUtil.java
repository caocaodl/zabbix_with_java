package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.issets;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asBoolean;
import static com.isoft.iradar.core.utils.EasyObject.asCArray;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.ActionsUtil.getEventActionsStatHints;
import static com.isoft.iradar.inc.ActionsUtil.getEventActionsStatus;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_REFER;
import static com.isoft.iradar.inc.Defines.DEFAULT_LATEST_ISSUES_CNT;
import static com.isoft.iradar.inc.Defines.DHOST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.DRULE_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.EVENT_SOURCE_TRIGGERS;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_ALL;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_BOTH;
import static com.isoft.iradar.inc.Defines.EXTACK_OPTION_UNACK;
import static com.isoft.iradar.inc.Defines.HTTPTEST_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.PERM_READ;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.RDA_WIDGET_ROWS;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.TRIGGERS_OPTION_ONLYTRUE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_AVERAGE;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_DISASTER;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_HIGH;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_WARNING;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.EventsUtil.getEventAckState;
import static com.isoft.iradar.inc.EventsUtil.make_popup_eventlist;
import static com.isoft.iradar.inc.FuncsUtil.getMenuPopupHost;
import static com.isoft.iradar.inc.FuncsUtil.get_status;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rda_toHash;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.ScreensUtil.get_slideshow_by_slideshowid;
import static com.isoft.iradar.inc.ScreensUtil.slideshow_accessible;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.iradar.inc.TriggersUtil.resolveTriggerUrl;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.core.g;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.params.CDRuleGet;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CHostGroupGet;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.params.CMaintenanceGet;
import com.isoft.iradar.model.params.CMapGet;
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.FrontendSetup;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class BlocksUtil {
	
	public static CList make_favorite_graphs(IIdentityBean idBean, SQLExecutor executor) {
		CList favList = new CList(null, "favorites", _("No graphs added."));
		CArray graphids = array();
		CArray itemids = array();

		CArray<Map> fav_graphs = CFavorite.get(idBean, executor, "web.favorite.graphids");

		if (empty(fav_graphs)) {
			return favList;
		}

		for(Map favorite: fav_graphs) {
			if ("itemid".equals(Nest.value(favorite,"source").$())) {
				itemids.put(favorite.get("value"), Nest.value(favorite,"value").$());
			} else {
				graphids.put(favorite.get("value"), Nest.value(favorite,"value").$());
			}
		}

		CArray<Map> graphs = array(), items = array();
		if (!empty(graphids)) {
			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(graphids.valuesAsLong());
			goptions.setSelectHosts(new String[]{"hostid", "name"});
			goptions.setOutput(new String[]{"graphid", "name"});
			goptions.put("expandName", true);
			graphs = API.Graph(idBean, executor).get(goptions);
			graphs = rda_toHash(graphs, "graphid");
		}

		if (!empty(itemids)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setItemIds(itemids.valuesAsLong());
			ioptions.setSelectHosts(new String[]{"hostid", "name"});
			ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_"});
			ioptions.setWebItems(true);
			items = API.Item(idBean, executor).get(ioptions);
			items = rda_toHash(items, "itemid");

			items = CMacrosResolverHelper.resolveItemNames(idBean, executor, items);
		}

		for(Map favorite: fav_graphs) {
			Long sourceid = Nest.value(favorite,"value").asLong();

			CLink link;
			if ("itemid".equals(Nest.value(favorite,"source").asString())) {
				if (!isset(items.get(sourceid))) {
					continue;
				}

				Map item = items.get(sourceid);
				Map host = reset(Nest.value(item,"hosts").asCArray());

				link = new CLink(
					host.get("name")+NAME_DELIMITER+Nest.value(item,"name_expanded").$(),
					"history.action?action=showgraph&itemid="+sourceid
				);
				link.setTarget("blank");
			} else {
				if (!isset(graphs.get(sourceid))) {
					continue;
				}

				Map graph = graphs.get(sourceid);
				Map ghost = reset(Nest.value(graph,"hosts").asCArray());

				link = new CLink(
					ghost.get("name")+NAME_DELIMITER+Nest.value(graph,"name").$(),
					"charts.action?graphid="+sourceid
				);
				link.setTarget("blank");
			}

			favList.addItem(link, "nowrap");
		}

		return favList;
	}
	
	
	public static CList make_favorite_screens(IIdentityBean idBean, SQLExecutor executor) {
		CList favList = new CList(null, "favorites", _("No screens added."));
		CArray<Map> fav_screens = CFavorite.get(idBean, executor, "web.favorite.screenids");
	
		if (empty(fav_screens)) {
			return favList;
		}
	
		CArray screenids = array();
		for(Map favorite: fav_screens) {
			if ("screenid".equals(Nest.value(favorite,"source").$())) {
				screenids.put(favorite.get("value"), Nest.value(favorite,"value").$());
			}
		}
	
		CScreenGet soptions = new CScreenGet();
		soptions.setScreenIds(screenids.valuesAsLong());
		soptions.setOutput(new String[]{"screenid", "name"});
		CArray<Map> screens = API.Screen(idBean, executor).get(soptions);
		screens = rda_toHash(screens, "screenid");
	
		for(Map favorite: fav_screens) {
			Object source = Nest.value(favorite,"source").$();
			Long sourceid = Nest.value(favorite,"value").asLong();
	
			CLink link;
			if ("slideshowid".equals(source)) {
				if (!slideshow_accessible(idBean, executor, sourceid, PERM_READ)) {
					continue;
				}
				Map slide = get_slideshow_by_slideshowid(idBean, executor, sourceid);
				if (empty(slide)) {
					continue;
				}
	
				link = new CLink(Nest.value(slide,"name").$(), "slides.action?elementid="+sourceid);
				link.setTarget("blank");
			} else {
				if (!isset(screens.get(sourceid))) {
					continue;
				}
				Map screen = screens.get(sourceid);
	
				link = new CLink(Nest.value(screen,"name").$(), "screens.action?elementid="+sourceid);
				link.setTarget("blank");
			}
			favList.addItem(link, "nowrap");
		}
		return favList;
	}
	
	public static CList make_favorite_maps(IIdentityBean idBean, SQLExecutor executor) {
		CList _favList = new CList(null, "favorites", _("No maps added."));
		CArray<Map> _fav_sysmaps = CFavorite.get(idBean, executor, "web.favorite.sysmapids");
	
		if (empty(_fav_sysmaps)) {
			return _favList;
		}
	
		CArray _sysmapids = array();
		for(Map _favorite: _fav_sysmaps) {
			_sysmapids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
		}
	
		CMapGet moptions = new CMapGet();
		moptions.setSysmapIds(_sysmapids.valuesAsLong());
		moptions.setOutput(new String[]{"sysmapid", "name"});
		CArray<Map> _sysmaps = API.Map(idBean, executor).get(moptions);
		for(Map _sysmap: _sysmaps) {
			Long _sysmapid = Nest.value(_sysmap,"sysmapid").asLong();
	
			CLink _link = new CLink(Nest.value(_sysmap,"name").$(), "maps.action?sysmapid="+_sysmapid);
			_link.setTarget("blank");
	
			_favList.addItem(_link, "nowrap");
		}
		return _favList;
	}
	
	
	public static CDiv make_system_status(IIdentityBean idBean, SQLExecutor executor, CArray _filter) {
		CArray _ackParams = array();
		if (!empty(Nest.value(_filter,"screenid").$())) {
			Nest.value(_ackParams,"screenid").$(Nest.value(_filter,"screenid").$());
		}
	
		CTableInfo _table = new CTableInfo(_("No host groups found."));
		_table.setHeader(array(
			_("Host group"),
			(is_null(Nest.value(_filter,"severity").$()) || isset(_filter.getNested("severity",TRIGGER_SEVERITY_DISASTER))) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_DISASTER) : null,
			(is_null(Nest.value(_filter,"severity").$()) || isset(_filter.getNested("severity",TRIGGER_SEVERITY_HIGH))) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_HIGH) : null,
			(is_null(Nest.value(_filter,"severity").$()) || isset(_filter.getNested("severity",TRIGGER_SEVERITY_AVERAGE))) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_AVERAGE) : null,
			(is_null(Nest.value(_filter,"severity").$()) || isset(_filter.getNested("severity",TRIGGER_SEVERITY_WARNING))) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_WARNING) : null,
			(is_null(Nest.value(_filter,"severity").$()) || isset(_filter.getNested("severity",TRIGGER_SEVERITY_INFORMATION))) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_INFORMATION) : null,
			(is_null(Nest.value(_filter,"severity").$()) || isset(_filter.getNested("severity",TRIGGER_SEVERITY_NOT_CLASSIFIED))) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_NOT_CLASSIFIED) : null
		));
	
		// get host groups
		CHostGroupGet hgoptions = new CHostGroupGet();
		if(!empty(Nest.value(_filter,"groupids").$())){
			hgoptions.setGroupIds(Nest.array(_filter,"groupids").asLong());
		}
		if(isset(_filter,"hostids")){
			hgoptions.setHostIds(Nest.array(_filter,"hostids").asLong());
		}
		hgoptions.setMonitoredHosts(true);
		hgoptions.setOutput(new String[]{"groupid", "name"});
		hgoptions.setPreserveKeys(true);
		CArray<Map> _groups = API.HostGroup(idBean, executor).get(hgoptions);
	
		CArray _sortOptions = array();
		_sortOptions.add(array("field", "name", "order", RDA_SORT_UP) );
	
		CArrayHelper.sort(_groups, _sortOptions);
	
		CArray _groupIds = array();
		for(Map _group: _groups) {
			_groupIds.put(_group.get("groupid"), Nest.value(_group,"groupid").$());
	
			Nest.value(_group,"tab_priority").$(map(
					TRIGGER_SEVERITY_DISASTER, map("count" , 0, "triggers" , array(), "count_unack" , 0, "triggers_unack" , array()),
					TRIGGER_SEVERITY_HIGH, map("count" , 0, "triggers" , array(), "count_unack" , 0, "triggers_unack" , array()),
					TRIGGER_SEVERITY_AVERAGE, map("count" , 0, "triggers" , array(), "count_unack" , 0, "triggers_unack" , array()),
					TRIGGER_SEVERITY_WARNING, map("count" , 0, "triggers" , array(), "count_unack" , 0, "triggers_unack" , array()),
					TRIGGER_SEVERITY_INFORMATION, map("count" , 0, "triggers" , array(), "count_unack" , 0, "triggers_unack" , array()),
					TRIGGER_SEVERITY_NOT_CLASSIFIED, map("count" , 0, "triggers" , array(), "count_unack" , 0, "triggers_unack" , array())
				));
			_groups.put(_group.get("groupid"), _group);
		}
	
		// get triggers
		CTriggerGet toptions = new CTriggerGet();
		toptions.setGroupIds(_groupIds.valuesAsLong());
		if(isset(_filter,"hostids")){
			toptions.setHostIds(Nest.array(_filter,"hostids").asLong());
		}
		toptions.setMonitored(true);
		toptions.setMaintenance(Nest.value(_filter,"maintenance").asBoolean());
		toptions.setSkipDependent(true);
		if(Nest.value(_filter,"extAck").asInteger() == EXTACK_OPTION_UNACK){
			toptions.setWithLastEventUnacknowledged(true);
		}
		toptions.setSelectLastEvent(new String[]{"eventid", "acknowledged", "objectid"});
		toptions.setExpandDescription(true);
		if(isset(_filter,"severity")){
			toptions.setFilter("priority", Nest.value(_filter,"severity").asString());
		}
		toptions.setFilter("value", Nest.as(TRIGGER_VALUE_TRUE).asString());
		toptions.setSortfield("lastchange");
		toptions.setSortorder(RDA_SORT_DOWN);
		toptions.setOutput(new String[]{"triggerid", "priority", "state", "description", "error", "value", "lastchange"});
		toptions.setSelectHosts(new String[]{"name"});
		toptions.setPreserveKeys(true);		
		CArray<Map> _triggers = API.Trigger(idBean, executor).get(toptions);
	
		CArray _eventIds = array();
	
		for(Entry<Object, Map> entry: _triggers.entrySet()) {
			Object _triggerId = entry.getKey();
			Map _trigger = entry.getValue();
			if (Nest.value(_trigger,"lastEvent").asBoolean()) {
				_eventIds.put(Nest.value(_trigger, "lastEvent", "eventid").$(), Nest.value(_trigger,"lastEvent","eventid").$());
			}
	
			_triggers.put(_triggerId, "event", Nest.value(_trigger,"lastEvent").$());
			unset(_triggers, _triggerId, "lastEvent");
		}
	
		// get acknowledges
		CArray _eventAcknowledges = array();
		if (!empty(_eventIds)) {
			CEventGet eoptions = new CEventGet();
			eoptions.setEventIds(_eventIds.valuesAsLong());
			eoptions.setSelectAcknowledges(new String[]{"eventid", "clock", "message", "alias", "name", "surname"});
			eoptions.setPreserveKeys(true);
			_eventAcknowledges = API.Event(idBean, executor).get(eoptions);
		}
	
		// actions
		CArray _actions = getEventActionsStatus(idBean, executor, _eventIds);
	
		// triggers
		for(Map _trigger: _triggers) {
			// event
			if (!empty(Nest.value(_trigger,"event").$())) {
				Nest.value(_trigger,"event","acknowledges").$(
						isset(_eventAcknowledges.get(Nest.value(_trigger, "event", "eventid").$()))
						? Nest.value(_eventAcknowledges, Nest.value(_trigger, "event", "eventid").$(), "acknowledges").$()
						: 0
					);
			}
			else {
				Nest.value(_trigger,"event").$(map(
						"acknowledged", false,
						"clock", Nest.value(_trigger,"lastchange").$(),
						"value", _trigger.get("value")
					));
			}
	
			// groups
			for(Map _group: (CArray<Map>)Nest.value(_trigger,"groups").asCArray()) {
				if (!isset(_groups.get(_group.get("groupid")))) {
					continue;
				}
	
				Map _c = (Map)Nest.value(_groups, _group.get("groupid"), "tab_priority", _trigger.get("priority")).$(); 
				if (in_array(Nest.value(_filter,"extAck").$(), array(EXTACK_OPTION_ALL, EXTACK_OPTION_BOTH))) {
					Nest.value(_c, "count").plus(1);
	
					if (Nest.value(_c, "count").asInteger() < 30) {
						Nest.value(_c, "triggers").asCArray().add(_trigger);
					}
				}
	
				if (in_array(Nest.value(_filter,"extAck").$(), array(EXTACK_OPTION_UNACK, EXTACK_OPTION_BOTH))
						&& isset(Nest.value(_trigger,"event").$()) && !Nest.value(_trigger,"event","acknowledged").asBoolean()) {
					Nest.value(_c, "count_unack").plus(1);
	
					if (Nest.value(_c, "count_unack").asInteger() < 30) {
						Nest.value(_c, "triggers_unack").asCArray().add(_trigger);
					}
				}
			}
		}
	
		CArray _config = asCArray(select_config(idBean, executor));
	
		for(Map _group: _groups) {
			CRow _groupRow = new CRow();
	
			CLink _name = new CLink(Nest.value(_group,"name").$(), "tr_status.action?groupid="+_group.get("groupid")+"&hostid=0&show_triggers="+TRIGGERS_OPTION_ONLYTRUE);
			_groupRow.addItem(_name);
	
			for(Entry<Object, Map> entry: ((CArray<Map>)Nest.value(_group,"tab_priority").asCArray()).entrySet()) {
				int _severity = asInteger(entry.getKey());
				Map _data = entry.getValue();
				if (!is_null(Nest.value(_filter,"severity").$()) && !isset(_filter.getNested("severity", _severity))) {
					continue;
				}
	
				Object _allTriggersNum = Nest.value(_data,"count").$();
				if (!empty(_allTriggersNum)) {
					_allTriggersNum = new CSpan(_allTriggersNum, "pointer");
					((CSpan)_allTriggersNum).setHint(makeTriggersPopup(idBean, executor, Nest.value(_data,"triggers").asCArray(), _ackParams, _actions, _config));
				}
	
				Object _unackTriggersNum = Nest.value(_data,"count_unack").$();
				if (!empty(_unackTriggersNum)) {
					_unackTriggersNum = new CSpan(_unackTriggersNum, "pointer red bold");
					((CSpan)_allTriggersNum).setHint(makeTriggersPopup(idBean, executor, Nest.value(_data,"triggers_unack").asCArray(), _ackParams, _actions, _config));
				}
	
				switch (Nest.value(_filter,"extAck").asInteger()) {
					case EXTACK_OPTION_ALL:
						_groupRow.addItem(getSeverityCell(idBean, executor, _severity, _allTriggersNum, empty(_allTriggersNum)));
						break;
	
					case EXTACK_OPTION_UNACK:
						_groupRow.addItem(getSeverityCell(idBean, executor, _severity, _unackTriggersNum, empty(_unackTriggersNum)));
						break;
	
					case EXTACK_OPTION_BOTH:
						CSpan _span;
						if (!empty(_unackTriggersNum)) {
							_span = new CSpan(SPACE+_("of")+SPACE);
							_unackTriggersNum = new CSpan(_unackTriggersNum);
						}
						else {
							_span = null;
							_unackTriggersNum = null;
						}
	
						_groupRow.addItem(getSeverityCell(idBean, executor, _severity, array(_unackTriggersNum, _span, _allTriggersNum), empty(_allTriggersNum)));
						break;
				}
			}
			_table.addRow(_groupRow);
		}
	
		CJSScript _script = new CJSScript(get_js("jQuery('#hat_syssum_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
	
		return new CDiv(array(_table, _script));
	}
	
	
	public static CDiv make_hoststat_summary(IIdentityBean idBean, SQLExecutor executor, CArray _filter) {
		CTableInfo _table = new CTableInfo(_("No host groups found."));
		_table.setHeader(array(
			_("Host group"),
			_("Without problems"),
			_("With problems"),
			_("Total")
		));
	
		// get host groups
		CHostGroupGet hgoptions = new CHostGroupGet();
		if(!empty(Nest.value(_filter,"groupids").$())){
			hgoptions.setGroupIds(Nest.array(_filter,"groupids").asLong());
		}
		hgoptions.setMonitoredHosts(true);
		hgoptions.setOutput(new String[]{"groupid", "name"});
		CArray<Map> _groups = API.HostGroup(idBean, executor).get(hgoptions);
		_groups = rda_toHash(_groups, "groupid");
	
		CArrayHelper.sort(_groups, array(
			map("field", "name", "order", RDA_SORT_UP)
		));
	
		// get hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setGroupIds(rda_objectValues(_groups, "groupid").valuesAsLong());
		if(!empty(Nest.value(_filter,"hostids").$())){
			hoptions.setHostIds(Nest.array(_filter,"hostids").asLong());
		}
		hoptions.setMonitoredHosts(true);
		hoptions.setFilter("maintenance_status" , Nest.array(_filter,"maintenance").asString());
		hoptions.setOutput(new String[]{"hostid", "name"});
		hoptions.setSelectGroups(new String[]{"groupid"});
		CArray<Map> _hosts = API.Host(idBean, executor).get(hoptions);
		_hosts = rda_toHash(_hosts, "hostid");
		CArrayHelper.sort(_hosts, array("name"));
	
		// get triggers
		CTriggerGet toptions = new CTriggerGet();
		toptions.setMonitored(true);
		toptions.setMaintenance(Nest.value(_filter,"maintenance").asBoolean());
		toptions.setExpandData(true);
		toptions.setFilter("priority" , Nest.array(_filter,"severity").asString());
		toptions.setFilter("value" , Nest.as(TRIGGER_VALUE_TRUE).asString());
		toptions.setOutput(new String[]{"triggerid", "priority"});
		toptions.setSelectHosts(new String[]{"hostid"});
		CArray<Map> _triggers = API.Trigger(idBean, executor).get(toptions);
		
		CArray _hosts_with_unack_triggers = array();
		CArray<Map> _triggers_unack = array();
		if (Nest.value(_filter,"extAck").asBoolean()) {
			toptions = new CTriggerGet();
			toptions.setMonitored(true);
			toptions.setMaintenance(Nest.value(_filter,"maintenance").asBoolean());
			toptions.setWithLastEventUnacknowledged(true);
			toptions.setSelectHosts(API_OUTPUT_REFER);
			toptions.setFilter("priority" , Nest.array(_filter,"severity").asString());
			toptions.setFilter("value" , Nest.as(TRIGGER_VALUE_TRUE).asString());
			toptions.setOutput(API_OUTPUT_REFER);
			_triggers_unack = API.Trigger(idBean, executor).get(toptions);
			_triggers_unack = rda_toHash(_triggers_unack, "triggerid");
			for(Map _tunack: _triggers_unack) {
				for(Map _unack_host: (CArray<Map>)Nest.value(_tunack,"hosts").asCArray()) {
					_hosts_with_unack_triggers.put(_unack_host.get("hostid"), Nest.value(_unack_host,"hostid").$());
				}
			}
		}
	
		CArray _hosts_data = array();
		CArray _problematic_host_list = array();
		CArray _lastUnack_host_list = array();
		CArray _highest_severity = array();
		CArray _highest_severity2 = array();
	
		for(Map _trigger: _triggers) {
			for(Map _trigger_host: (CArray<Map>)Nest.value(_trigger,"hosts").asCArray()) {
				Map _host;
				if (!isset(_hosts.get(_trigger_host.get("hostid")))) {
					continue;
				} else {
					_host = (Map)_hosts.get(_trigger_host.get("hostid"));
				}
				Object _hostId = _host.get("hostid");
	
				if (Nest.value(_filter,"extAck").asBoolean() && isset(_hosts_with_unack_triggers.get(_hostId))) {
					CArray _severities = Nest.value(_lastUnack_host_list, _hostId).asCArray();
					if (!isset(_severities)) {
						CArray host = array();
						_lastUnack_host_list.put(_hostId, host);
						host.put("host", Nest.value(host,"name").$());
						host.put("hostid", Nest.value(host,"hostid").$());
						
						_severities = array();
						host.put("severities", _severities);
						_severities.put(TRIGGER_SEVERITY_DISASTER, 0);
						_severities.put(TRIGGER_SEVERITY_HIGH, 0);
						_severities.put(TRIGGER_SEVERITY_AVERAGE, 0);
						_severities.put(TRIGGER_SEVERITY_WARNING, 0);
						_severities.put(TRIGGER_SEVERITY_INFORMATION, 0);
						_severities.put(TRIGGER_SEVERITY_NOT_CLASSIFIED, 0);
					}
					if (isset(_triggers_unack.get(_trigger.get("triggerid")))) {
						Nest.value(_severities, _trigger.get("priority")).plus(1);
					}
					
					for(Entry<Object, Map> entry: ((CArray<Map>)Nest.value(_host,"groups").asCArray()).entrySet()) {
						//Object _gnum = entry.getKey();
						Map _group = entry.getValue();
	
						Object _groupId = _group.get("groupid");
						if (!isset(_highest_severity2.get(_groupId))) {
							_highest_severity2.put(_groupId, 0);
						}
	
						if (Nest.value(_trigger,"priority").asInteger() > Nest.value(_highest_severity2, _groupId).asInteger()) {
							_highest_severity2.put(_groupId, Nest.value(_trigger,"priority").$());
						}
	
						if (!isset(_hosts_data.get(_groupId))) {
							_hosts_data.put(_groupId, map(
								"problematic", 0,
								"ok", 0,
								"lastUnack", 0,
								"hostids_all", array(),
								"hostids_unack", array()
							));
						}
	
						
						if (!isset(_hosts_data.getNested(_groupId, "hostids_unack", _hostId))) {
							_hosts_data.put(_groupId, "hostids_unack", _hostId, Nest.value(_host,"hostid").$());
							Nest.value(_hosts_data, _groupId, "lastUnack").plus(1);
						}
					}
				}
	
				if (!isset(_problematic_host_list,_hostId)) {
					CArray host = array();
					_problematic_host_list.put(_hostId, host);
					host.put("host", Nest.value(_host,"name").$());
					host.put("hostid", Nest.value(_host,"hostid").$());
					
					CArray _severities = array();
					host.put("severities", _severities);
					_severities.put(TRIGGER_SEVERITY_DISASTER, 0);
					_severities.put(TRIGGER_SEVERITY_HIGH, 0);
					_severities.put(TRIGGER_SEVERITY_AVERAGE, 0);
					_severities.put(TRIGGER_SEVERITY_WARNING, 0);
					_severities.put(TRIGGER_SEVERITY_INFORMATION, 0);
					_severities.put(TRIGGER_SEVERITY_NOT_CLASSIFIED, 0);
				}
				Nest.value(_problematic_host_list, _hostId, "severities", _trigger.get("priority")).plus(1);
	
				for(Entry<Object, Map> entry: ((CArray<Map>)Nest.value(_host,"groups").asCArray()).entrySet()) {
					//Object _gnum = entry.getKey();
					Map _group = entry.getValue();
					
					Object _groupid = _group.get("groupid");
					if (!isset(_highest_severity.get(_groupid))) {
						_highest_severity.put(_groupid, 0);
					}
	
					if (Nest.value(_trigger,"priority").asInteger() > Nest.value(_highest_severity, _groupid).asInteger()) {
						_highest_severity.put(_groupid, Nest.value(_trigger,"priority").$());
					}
	
					if (!isset(_hosts_data.get(_groupid))) {
						_hosts_data.put(_groupid, map(
							"problematic", 0,
							"ok", 0,
							"lastUnack", 0,
							"hostids_all", array(),
							"hostids_unack", array()
						));
					}
	
					if (!isset(_hosts_data.getNested(_groupid, "hostids_all", _hostId))) {
						_hosts_data.put(_groupid, "hostids_all", _hostId, Nest.value(_host,"hostid").$());
						Nest.value(_hosts_data, _groupid, "problematic").plus(1);
					}
				}
			}
		}
	
		for(Map _host: _hosts) {
			for(Map _group: (CArray<Map>)Nest.value(_host,"groups").asCArray()) {
				Object _groupid = _group.get("groupid");
				if (!isset(_groups.get(_groupid))) {
					continue;
				}
	
				if (!isset(_groups.getNested(_groupid, "hosts"))) {
					_groups.put(_groupid, "hosts", array());
				}
				_groups.put(_groupid, "hosts", _host.get("hostid"), map("hostid", Nest.value(_host,"hostid").$()));
	
				if (!isset(_highest_severity.get(_groupid))) {
					_highest_severity.put(_groupid, 0);
				}
	
				if (!isset(_hosts_data.get(_groupid))) {
					_hosts_data.put(_groupid, map("problematic", 0, "ok", 0, "lastUnack", 0));
				}
	
				if (!isset(_problematic_host_list.get(_host.get("hostid")))) {
					Nest.value(_hosts_data, _groupid, "ok").plus(1);
				}
			}
		}
	
		for(Map _group: _groups) {
			Object _groupid = _group.get("groupid");
			if (!isset(_hosts_data.get(_groupid))) {
				continue;
			}
	
			CRow _group_row = new CRow();
	
			CLink _name = new CLink(Nest.value(_group,"name").$(), "tr_status.action?groupid="+_groupid+"&hostid=0&show_triggers="+TRIGGERS_OPTION_ONLYTRUE);
			_group_row.addItem(_name);
			_group_row.addItem(new CCol(_hosts_data.getNested(_groupid, "ok"), "normal"));
	
			Object _lastUnack_count = null;
			if (Nest.value(_filter,"extAck").asBoolean()) {
				if (Nest.value(_hosts_data, _groupid, "lastUnack").asBoolean()) {
					CTableInfo _table_inf = new CTableInfo();
					_table_inf.setAttribute("style", "width: 400px;");
					
					CArray _severity = Nest.value(_filter,"severity").asCArray();
					_table_inf.setHeader(array(
						_("Host"),
						is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_DISASTER)) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_DISASTER) : null,
						is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_HIGH)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_HIGH) : null,
						is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_AVERAGE)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_AVERAGE) : null,
						is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_WARNING)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_WARNING) : null,
						is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_INFORMATION)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_INFORMATION) : null,
						is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_NOT_CLASSIFIED)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_NOT_CLASSIFIED) : null
					));
					int _popup_rows = 0;
	
					for(Map _host: (CArray<Map>)Nest.value(_group,"hosts").asCArray()) {
						Object _hostid = Nest.value(_host,"hostid").$();
						if (!isset(_lastUnack_host_list.get(_hostid))) {
							continue;
						}
	
						if (_popup_rows >= RDA_WIDGET_ROWS) {
							break;
						}
						_popup_rows++;
	
						Map _host_data = (Map)_lastUnack_host_list.get(_hostid);
	
						CRow r = new CRow();
						r.addItem(new CLink(Nest.value(_host_data,"host").$(), "tr_status.action?groupid="+_groupid+"&hostid="+_hostid+"&show_triggers="+TRIGGERS_OPTION_ONLYTRUE));
	
						for(Entry<Object, Object> entry: ((CArray<Object>)Nest.value(_lastUnack_host_list, _host.get("hostid"), "severities").asCArray()).entrySet()) {
							Object severity = entry.getKey();
							Object _trigger_count = entry.getValue();
							if (!is_null(Nest.value(_filter,"severity").$()) && !isset(_filter.getNested("severity", severity))) {
								continue;
							}
							r.addItem(new CCol(_trigger_count, getSeverityStyle(asInteger(severity), asBoolean(_trigger_count))));
						}
						_table_inf.addRow(r);
					}
					_lastUnack_count = new CSpan(Nest.value(_hosts_data, _groupid, "lastUnack").$(), "pointer red bold");
					((CSpan)_lastUnack_count).setHint(_table_inf);
				}
				else {
					_lastUnack_count = 0;
				}
			}
	
			// if hostgroup contains problematic hosts, hint should be built
			Object _problematic_count;
			if (Nest.value(_hosts_data, _groupid, "problematic").asBoolean()) {
				CTableInfo _table_inf = new CTableInfo();
				_table_inf.setAttribute("style", "width: 400px;");
				CArray _severity = Nest.value(_filter,"severity").asCArray();
				_table_inf.setHeader(array(
					_("Host"),
					is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_DISASTER)) ? getSeverityCaption(idBean, executor, TRIGGER_SEVERITY_DISASTER) : null,
					is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_HIGH)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_HIGH) : null,
					is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_AVERAGE)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_AVERAGE) : null,
					is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_WARNING)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_WARNING) : null,
					is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_INFORMATION)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_INFORMATION) : null,
					is_null(Nest.value(_filter,"severity").$()) || isset(_severity.get(TRIGGER_SEVERITY_NOT_CLASSIFIED)) ? getSeverityCaption(idBean, executor,TRIGGER_SEVERITY_NOT_CLASSIFIED) : null
				));
				int _popup_rows = 0;
	
				for(Map _host: (CArray<Map>)Nest.value(_group,"hosts").asCArray()) {
					Object _hostid = Nest.value(_host,"hostid").$();
					if (!isset(_problematic_host_list.get(_hostid))) {
						continue;
					}
					if (_popup_rows >= RDA_WIDGET_ROWS) {
						break;
					}
					_popup_rows++;
	
					Map _host_data = (Map)_problematic_host_list.get(_hostid);
	
					CRow r = new CRow();
					r.addItem(new CLink(Nest.value(_host_data,"host").$(), "tr_status.action?groupid="+_groupid+"&hostid="+_hostid+"&show_triggers="+TRIGGERS_OPTION_ONLYTRUE));
	
					for(Entry<Object, Object> entry: ((CArray<Object>)Nest.value(_problematic_host_list, _hostid, "severities").asCArray()).entrySet()) {
						Object severity = entry.getKey();
						Object _trigger_count = entry.getValue();
						if (!is_null(Nest.value(_filter,"severity").$()) && !isset(_filter.getNested("severity", severity))) {
							continue;
						}
						r.addItem(new CCol(_trigger_count, getSeverityStyle(asInteger(severity), asBoolean(_trigger_count))));
					}
					_table_inf.addRow(r);
				}
				_problematic_count = new CSpan(_hosts_data.getNested(_groupid, "problematic"), "pointer");
				((CSpan)_problematic_count).setHint(_table_inf);
			} else {
				_problematic_count = 0;
			}
	
			switch (Nest.value(_filter,"extAck").asInteger()) {
				case EXTACK_OPTION_ALL:
					_group_row.addItem(new CCol(
						_problematic_count,
						getSeverityStyle(Nest.value(_highest_severity, _groupid).asInteger(), Nest.value(_hosts_data, _groupid, "problematic").asBoolean()))
					);
					_group_row.addItem(Nest.value(_hosts_data, _groupid, "problematic").asInteger() + Nest.value(_hosts_data, _groupid, "ok").asInteger());
					break;
				case EXTACK_OPTION_UNACK:
					_group_row.addItem(new CCol(
						_lastUnack_count,
						getSeverityStyle((isset(_highest_severity2.get(_groupid)) ? Nest.value(_highest_severity2, _groupid).asInteger() : 0),
							Nest.value(_hosts_data, _groupid, "lastUnack").asBoolean()))
					);
					_group_row.addItem(Nest.value(_hosts_data, _groupid, "lastUnack").asInteger() + Nest.value(_hosts_data, _groupid, "ok").asInteger());
					break;
				case EXTACK_OPTION_BOTH:
					Object _unackspan = !empty(_lastUnack_count) ? new CSpan(array(_lastUnack_count, SPACE+_("of")+SPACE)) : null;
					_group_row.addItem(new CCol(array(
						_unackspan, _problematic_count),
						getSeverityStyle(Nest.value(_highest_severity, _groupid).asInteger(), Nest.value(_hosts_data, _groupid, "problematic").asBoolean()))
					);
					_group_row.addItem(Nest.value(_hosts_data, _groupid, "problematic").asInteger() + Nest.value(_hosts_data, _groupid, "ok").asInteger());
					break;
			}
			_table.addRow(_group_row);
		}
	
		CJSScript _script = new CJSScript(get_js("jQuery('#hat_hoststat_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
				
		return new CDiv(array(_table, _script));
	}
	
	public static CDiv make_status_of_rda(IIdentityBean idBean, SQLExecutor executor) {
		CTableInfo table = new CTableInfo();
		table.setHeader(array(
			_("Parameter"),
			_("Value"),
			_("Details")
		));
	
		show_messages(); // because in function get_status(); function clear_messages() is called when fsockopen() fails.
		Map status = get_status(idBean, executor);
	
		table.addRow(array(
			_("iRadar server is running"),
			new CSpan(Nest.value(status,"iradar_server").$(), (Nest.value(status,"iradar_server").$() == _("Yes") ? "off" : "on")),
			issets(g.RDA_SERVER, g.RDA_SERVER_PORT) ? g.RDA_SERVER+":"+g.RDA_SERVER_PORT : _("iRadar server IP or port is not set!")
		));
		CSpan title = new CSpan(_("Number of hosts (monitored/not monitored/templates)"));
		title.setAttribute("title", "asdad");
		table.addRow(array(_("Number of hosts (monitored/not monitored/templates)"), Nest.value(status,"hosts_count").$(),
			array(
				new CSpan(Nest.value(status,"hosts_count_monitored").$(), "off"), " / ",
				new CSpan(Nest.value(status,"hosts_count_not_monitored").$(), "on"), " / ",
				new CSpan(Nest.value(status,"hosts_count_template").$(), "unknown")
			)
		));
		title = new CSpan(_("Number of items (monitored/disabled/not supported)"));
		title.setAttribute("title", _("Only items assigned to enabled hosts are counted"));
		table.addRow(array(title, Nest.value(status,"items_count").$(),
			array(
				new CSpan(Nest.value(status,"items_count_monitored").$(), "off"), " / ",
				new CSpan(Nest.value(status,"items_count_disabled").$(), "on"), " / ",
				new CSpan(Nest.value(status,"items_count_not_supported").$(), "unknown")
			)
		));
		title = new CSpan(_("Number of triggers (enabled/disabled) [problem/ok]"));
		title.setAttribute("title", _("Only triggers assigned to enabled hosts and depending on enabled items are counted"));
		table.addRow(array(title, Nest.value(status,"triggers_count").$(),
			array(
				Nest.value(status,"triggers_count_enabled").$(), " / ",
				Nest.value(status,"triggers_count_disabled").$(), " [",
				new CSpan(Nest.value(status,"triggers_count_on").$(), "on"), " / ",
				new CSpan(Nest.value(status,"triggers_count_off").$(), "off"), "]"
			)
		));
		table.addRow(array(_("Number of users (online)"), Nest.value(status,"users_count").$(), new CSpan(Nest.value(status,"users_online").$(), "green")));
		table.addRow(array(_("Required server performance, new values per second"), Nest.value(status,"qps_total").$(), " - "));
	
		// check requirements
		if (Nest.value(CWebUser.data(), "type").asInteger() == USER_TYPE_SUPER_ADMIN) {
			FrontendSetup frontendSetup = new FrontendSetup();
			CArray<Map> reqs = frontendSetup.checkRequirements();
			for(Map req: reqs) {
				if (Nest.value(req,"result").asInteger() != FrontendSetup.CHECK_OK) {
					String classstyle = (Nest.value(req,"result").asInteger() == FrontendSetup.CHECK_WARNING) ? "notice" : "fail";
					table.addRow(array(
						new CSpan(Nest.value(req,"name").$(), classstyle),
						new CSpan(Nest.value(req,"current").$(), classstyle),
						new CSpan(Nest.value(req,"error").$(), classstyle)
					));
				}
			}
		}
		CJSScript script = new CJSScript(get_js("jQuery('#hat_stsrda_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
		return new CDiv(array(table, script));
	}

	public static CDiv make_latest_issues(IIdentityBean idBean, SQLExecutor executor) {
		return make_latest_issues(idBean, executor, array());
	}


	/**
	 * Create DIV with latest problem triggers.
	 *
	 * If no sortfield and sortorder are defined, the sort indicater in the column name will not be displayed.
	 *
	 * @param array  filter["screenid"]
	 * @param array  filter["groupids"]
	 * @param array  filter["hostids"]
	 * @param array  filter["maintenance"]
	 * @param int    filter["extAck"]
	 * @param int    filter["severity"]
	 * @param int    filter["limit"]
	 * @param string filter["sortfield"]
	 * @param string filter["sortorder"]
	 * @param string filter["backUrl"]
	 *
	 * @return CDiv
	 */
	public static CDiv make_latest_issues(IIdentityBean idBean, SQLExecutor executor, CArray filter) {
		// hide the sort indicator if no sortfield and sortorder are given
		boolean showSortIndicator = isset(filter,"sortfield") || isset(filter,"sortorder");
	
		CArray sortField, sortOrder;
		if (isset(Nest.value(filter,"sortfield").$()) && !"lastchange".equals(Nest.value(filter,"sortfield").$())) {
			sortField = array(Nest.value(filter,"sortfield").$(), "lastchange");
			sortOrder = array(Nest.value(filter,"sortorder").$(), RDA_SORT_DOWN);
		} else {
			sortField = array("lastchange");
			sortOrder = array(RDA_SORT_DOWN);
		}
	
		CTriggerGet options = new CTriggerGet();
		if(!empty(Nest.value(filter,"groupids").$())){
			options.setGroupIds(Nest.array(filter,"groupids").asLong());
		}
		if(isset(filter,"hostids")){
			options.setHostIds(Nest.array(filter,"hostids").asLong());
		}
		options.setMonitored(true);
		options.setMaintenance(Nest.value(filter,"maintenance").asBoolean(true));
		options.setFilter("priority", Nest.array(filter,"severity").asString());
		options.setFilter("value", Nest.as(TRIGGER_VALUE_TRUE).asString());
		
		CTriggerGet toptions = Clone.deepcopy(options);
		if(isset(filter,"extAck") && Nest.value(filter,"extAck").asInteger() == EXTACK_OPTION_UNACK){
			toptions.setWithLastEventUnacknowledged(true);
		}
		toptions.setSkipDependent(true);
		toptions.setOutput(new String[]{"triggerid", "state", "error", "url", "expression", "description", "priority", "lastchange"});
		toptions.setSelectHosts(new String[]{"hostid", "name"});
		toptions.setSelectLastEvent(new String[]{"eventid", "acknowledged", "objectid", "clock", "ns"});
		toptions.setSortfield(sortField.valuesAsString());
		toptions.setSortorder(sortOrder.valuesAsString());
		toptions.setLimit(isset(filter,"limit") ? Nest.value(filter,"limit").asInteger() : DEFAULT_LATEST_ISSUES_CNT);
	
		CArray<Map> triggers = API.Trigger(idBean, executor).get(toptions);
	
		// don't use withLastEventUnacknowledged and skipDependent because of performance issues
		toptions = Clone.deepcopy(options);
		toptions.setCountOutput(true);
		Object triggersTotalCount = API.Trigger(idBean, executor).get(toptions);
	
		// get acknowledges
		CArray eventIds = array();
		for(Map trigger: triggers) {
			if (!empty(Nest.value(trigger,"lastEvent").$())) {
				eventIds.add(Nest.value(trigger,"lastEvent","eventid").$() );
			}
		}
		CArray<Map> eventAcknowledges=null;
		if (!empty(eventIds)) {
			CEventGet eoptions = new CEventGet();
			eoptions.setEventIds(eventIds.valuesAsLong());
			eoptions.setSelectAcknowledges(API_OUTPUT_EXTEND);
			eoptions.setPreserveKeys(true);
			eventAcknowledges = API.Event(idBean, executor).get(eoptions);
		}
	
		for(Entry<Object, Map> entry: triggers.entrySet()) {
			Object tnum = entry.getKey();
			Map trigger = entry.getValue();
			// if trigger is lost (broken expression) we skip it
			if (empty(Nest.value(trigger,"hosts").$())) {
				unset(triggers, tnum);
				continue;
			}
	
			Map host = reset(Nest.value(trigger,"hosts").asCArray());
			Nest.value(trigger,"hostid").$(Nest.value(host,"hostid").$());
			Nest.value(trigger,"hostname").$(Nest.value(host,"name").$());
	
			if (!empty(Nest.value(trigger,"lastEvent").$())) {
				Object k = Nest.value(trigger, "lastEvent", "eventid").$();
				Nest.value(trigger,"lastEvent","acknowledges").$(
					isset(eventAcknowledges,k)
					? Nest.value(eventAcknowledges, k, "acknowledges").$()
					: null
				);
			}
	
			triggers.put(tnum, trigger);
		}
	
		CArray hostIds = rda_objectValues(triggers, "hostid");
	
		// get hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setHostIds(hostIds.valuesAsLong());
		hoptions.setOutput(new String[]{"hostid", "name", "status", "maintenance_status", "maintenance_type", "maintenanceid"});
		hoptions.setSelectScreens(API_OUTPUT_COUNT);
		hoptions.setPreserveKeys(true);
		CArray<Map> hosts = API.Host(idBean, executor).get(hoptions);
	
		// actions
		CArray actions = getEventActionsStatHints(idBean, executor, eventIds);
	
		// ack params
		CArray ackParams = isset(Nest.value(filter,"screenid").$()) ? map("screenid", Nest.value(filter,"screenid").$()) : array();
	
		Map config = select_config(idBean, executor);
	
		// indicator of sort field
		CDiv sortDiv=null, hostHeaderDiv=null, issueHeaderDiv=null, lastChangeHeaderDiv=null;
		if (showSortIndicator) {
			sortDiv = new CDiv(SPACE, (Nest.value(filter,"sortorder").$() == RDA_SORT_DOWN) ? "icon_sortdown default_cursor" : "icon_sortup default_cursor");
			sortDiv.addStyle("float: left");
			hostHeaderDiv = new CDiv(array(_("Host"), SPACE));
			hostHeaderDiv.addStyle("float: left");
			issueHeaderDiv = new CDiv(array(_("Issue"), SPACE));
			issueHeaderDiv.addStyle("float: left");
			lastChangeHeaderDiv = new CDiv(array(_("Time"), SPACE));
			lastChangeHeaderDiv.addStyle("float: left");
		}
	
		CTableInfo table = new CTableInfo(_("No events found."));
		table.setHeader(array(
			(showSortIndicator && ("hostname".equals(Nest.value(filter,"sortfield").$()))) ? array(hostHeaderDiv, sortDiv) : _("Host"),
			(showSortIndicator && ("priority".equals(Nest.value(filter,"sortfield").$()))) ? array(issueHeaderDiv, sortDiv) : _("Issue"),
			(showSortIndicator && ("lastchange".equals(Nest.value(filter,"sortfield").$()))) ? array(lastChangeHeaderDiv, sortDiv) : _("Last change"),
			_("Age"),
			_("Info"),
			Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null,
			_("Actions")
		));
	
		CArray<CArray<Map>> scripts = API.Script(idBean, executor).getScriptsByHosts(hostIds.valuesAsLong());
		// triggers
		for(Map trigger: triggers) {
			Map host = Nest.value(hosts,trigger.get("hostid")).asCArray(true);
	
			CSpan hostName = new CSpan(Nest.value(host,"name").$(), "link_menu");
			hostName.setMenuPopup(getMenuPopupHost(host, Nest.value(scripts, host.get("hostid")).asCArray()));
	
			// add maintenance icon with hint if host is in maintenance
			CDiv maintenanceIcon = null;
	
			if (Nest.value(host,"maintenance_status").asBoolean()) {
				maintenanceIcon = new CDiv(null, "icon-maintenance-abs");
	
				// get maintenance
				CMaintenanceGet moptions = new CMaintenanceGet();
				moptions.setMaintenanceIds(Nest.value(host,"maintenanceid").asLong());
				moptions.setOutput(API_OUTPUT_EXTEND);
				moptions.setLimit(1);
				CArray<Map> maintenances = API.Maintenance(idBean, executor).get(moptions);
				Map maintenance = reset(maintenances);
				if (!empty(maintenance)) {
					String hint = maintenance.get("name")+" ["+(Nest.value(host, "maintenance_type").asBoolean()
						? _("Maintenance without data collection")
						: _("Maintenance with data collection"))+"]";
	
					if (isset(Nest.value(maintenance,"description").$())) {
						// double quotes mandatory
						hint += "\n"+maintenance.get("description");
					}
	
					maintenanceIcon.setHint(hint);
					maintenanceIcon.addClass("pointer");
				}
	
				hostName.addClass("left-to-icon-maintenance-abs");
			}
	
			CDiv hostDiv = new CDiv(array(hostName, maintenanceIcon), "maintenance-abs-cont");
	
			// unknown triggers
			Object unknown = SPACE;
			if (Nest.value(trigger,"state").asInteger() == TRIGGER_STATE_UNKNOWN) {
				unknown = new CDiv(SPACE, "status_icon iconunknown");
				((CDiv)unknown).setHint(Nest.value(trigger,"error").$(), "", "on");
			}
	
			// trigger has events
			Object description, ack;
			if (!empty(Nest.value(trigger,"lastEvent").$())) {
				// description
				description = CMacrosResolverHelper.resolveEventDescription(idBean, executor, rda_array_merge(asCArray(trigger), map(
					"clock", Nest.value(trigger,"lastEvent","clock").$(),
					"ns", Nest.value(trigger, "lastEvent", "ns").$()
				)));
	
				// ack
				ack = getEventAckState(idBean, executor, Nest.value(trigger,"lastEvent").asCArray(), empty(Nest.value(filter,"backUrl").$()) ? true : Nest.value(filter,"backUrl").$(),
					true, ackParams
				);
			}
			// trigger has no events
			else {
				// description
				description = CMacrosResolverHelper.resolveEventDescription(idBean, executor, rda_array_merge(trigger, map(
					"clock", Nest.value(trigger,"lastchange").$(),
					"ns", "999999999"
				)));
	
				// ack
				ack = new CSpan(_("No events"), "unknown");
			}
	
			// description
			if (!rda_empty(Nest.value(trigger,"url").$())) {
				description = new CLink(description, resolveTriggerUrl(trigger), null, null, true);
			}
			else {
				description = new CSpan(description, "pointer");
			}
			description = new CCol(description, getSeverityStyle(Nest.value(trigger,"priority").asInteger()));
			if (!empty(Nest.value(trigger,"lastEvent").$())) {
				((CCol)description).setHint(
					make_popup_eventlist(idBean, executor, Nest.value(trigger,"triggerid").asLong(), Nest.value(trigger,"lastEvent","eventid").asLong()),
					"", "", false
				);
			}
	
			// clock
			CLink clock = new CLink(rda_date2str(_("d M Y H:i:s"), Nest.value(trigger,"lastchange").asLong()),
				"events.action?triggerid="+trigger.get("triggerid")+"&source="+EVENT_SOURCE_TRIGGERS+"&show_unknown=1"+
					"&hostid="+trigger.get("hostid")+"&stime="+date(TIMESTAMP_FORMAT, Nest.value(trigger,"lastchange").asLong())+
					"&period="+RDA_PERIOD_DEFAULT
			);
	
			// actions
			Object eventDesc = actions.get(Nest.value(trigger, "lastEvent", "eventid").$());
			Object actionHint = (!empty(Nest.value(trigger,"lastEvent").$()) && isset(eventDesc))
				? eventDesc
				: SPACE;
	
			table.addRow(array(
				hostDiv,
				description,
				clock,
				rda_date2age(Nest.value(trigger,"lastchange").asLong()),
				unknown,
				ack,
				actionHint
			));
		}
	
		// initialize blinking
		rda_add_post_js("jqBlink.blink();");
	
		CJSScript script = new CJSScript(get_js("jQuery('#hat_lastiss_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
	
		CDiv infoDiv = new CDiv(_n("%1$d of %2$d issue is shown", "%1$d of %2$d issues are shown", count(triggers), triggersTotalCount));
		infoDiv.addStyle("text-align: right; padding-right: 3px;");
	
		return new CDiv(array(table, infoDiv, script));
	}

	/**
	 * Create and return a DIV with web monitoring overview.
	 *
	 * @param array _filter
	 * @param array _filter["groupids"]
	 * @param bool  _filter["maintenance"]
	 *
	 * @return CDiv
	 */
	public static CDiv make_webmon_overview(IIdentityBean idBean, SQLExecutor executor, CArray _filter) {
		CHostGroupGet hgoptions = new CHostGroupGet();
		if(!empty(Nest.value(_filter,"groupids").$())){
			hgoptions.setGroupIds(Nest.array(_filter,"groupids").asLong());
		}
		if(isset(_filter,"hostids")){
			hgoptions.setHostIds(Nest.array(_filter,"hostids").asLong());
		}
		hgoptions.setMonitoredHosts(true);
		hgoptions.setWithMonitoredHttpTests(true);
		hgoptions.setOutput(new String[]{"groupid", "name"});
		hgoptions.setPreserveKeys(true);
		CArray<Map> _groups = API.HostGroup(idBean, executor).get(hgoptions);
	
		CArrayHelper.sort(_groups, array(
			map("field", "name", "order", RDA_SORT_UP)
		));
	
		CArray _groupIds = array_keys(_groups);
	
		CHostGet hoptions = new CHostGet();
		hoptions.setGroupIds(_groupIds.valuesAsLong());
		if(isset(_filter,"hostids")){
			hoptions.setHostIds(Nest.array(_filter,"hostids").asLong());
		}
		hoptions.setMonitoredHosts(true);
		hoptions.setFilter("maintenance_status" , Nest.array(_filter,"maintenance").asString());
		hoptions.setOutput(new String[]{"hostid"});
		hoptions.setPreserveKeys(true);
		CArray<Map> _availableHosts = API.Host(idBean, executor).get(hoptions);
		CArray _availableHostIds = array_keys(_availableHosts);
	
		CTableInfo _table = new CTableInfo(_("No web scenarios found."));
		_table.setHeader(array(
			_("Host group"),
			_("Ok"),
			_("Failed"),
			_("Unknown")
		));
	
		CArray _data = array();
	
		// fetch links between HTTP tests and host groups
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> _result = DBselect(executor,
			"SELECT DISTINCT ht.httptestid,hg.groupid"+
			" FROM httptest ht,hosts_groups hg"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "httptest", "ht")+
			    " AND ht.tenantid=hg.tenantid"+
			    " AND ht.hostid=hg.hostid"+
				" AND "+sqlParts.dual.dbConditionInt("hg.hostid", _availableHostIds.valuesAsLong())+
				" AND "+sqlParts.dual.dbConditionInt("hg.groupid", _groupIds.valuesAsLong())+
				" AND ht.status="+HTTPTEST_STATUS_ACTIVE,
			sqlParts.getNamedParams()
		);
	
		// fetch HTTP test execution data
		CArray _httpTestData = Manager.HttpTest(idBean, executor).getLastData(rda_objectValues(_result, "httptestid").valuesAsLong());
	
		for(Map _row: _result) {
			Object _httptestid = _row.get("httptestid");
			Object groupid = _row.get("groupid");
			
			if (isset(_httpTestData.get(_httptestid)) && _httpTestData.getNested(_httptestid, "lastfailedstep") != null) {
				if (Nest.value(_httpTestData, _httptestid, "lastfailedstep").asInteger() != 0) {
					_data.put(groupid, "failed", isset(_data.getNested(groupid, "failed"))
						? Nest.value(_data, groupid, "failed").asInteger() + 1
						: 1
					);
				}
				else {
					_data.put(groupid, "ok", isset(_data.getNested(groupid, "ok"))
						? Nest.value(_data, groupid, "ok").asInteger() + 1
						: 1
					);
				}
			}
			else {
				_data.put(groupid, "unknown", isset(_data.getNested(groupid, "unknown"))
					? Nest.value(_data, groupid, "unknown").asInteger() + 1
					: 1
				);
			}
		}
	
		for(Map _group: _groups) {
			Object _groupid = _group.get("groupid");
			if (!empty(_data.get(_groupid))) {
				_table.addRow(array(
					Nest.value(_group,"name").$(),
					new CSpan(empty(_data.getNested(_groupid, "ok")) ? 0 : Nest.value(_data, _groupid, "ok").asInteger(), "off"),
					new CSpan(
						empty(_data.getNested(_groupid, "failed")) ? 0 : Nest.value(_data, _groupid, "failed").asInteger(),
						empty(_data.getNested(_groupid, "failed")) ? "off" : "on"
					),
					new CSpan(empty(_data.getNested(_groupid, "unknown")) ? 0 : Nest.value(_data, _groupid, "unknown").asInteger(), "unknown")
				));
			}
		}
	
		CJSScript _script = new CJSScript(get_js("jQuery('#hat_webovr_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
	
		return new CDiv(array(_table, _script));
	}
		
	
	public static CDiv make_discovery_status(IIdentityBean idBean, SQLExecutor executor) {
		CDRuleGet droptions = new CDRuleGet();
		droptions.setFilter("status" , Nest.as(DHOST_STATUS_ACTIVE).asString());
		droptions.setSelectDHosts(new String[]{"druleid", "dhostid", "status"});
		droptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> _drules = API.DRule(idBean, executor).get(droptions);
	
		// we need natural sort
		CArray _sortFields = array(
			map("field" , "name", "order" , RDA_SORT_UP)
		);
		CArrayHelper.sort(_drules, _sortFields);
	
	
		for(Entry<Object, Map> entry: _drules.entrySet()) {
			Object _drnum = entry.getKey();
			Map _drule = entry.getValue();
			Nest.value(_drules, _drnum, "up").$(0);
			Nest.value(_drules, _drnum, "down").$(0);
	
			for(Map _dhost: (CArray<Map>)Nest.value(_drule,"dhosts").asCArray()){
				if (DRULE_STATUS_DISABLED == Nest.value(_dhost,"status").asInteger()) {
					Nest.value(_drules, _drnum, "down").plus(1);
				}
				else {
					Nest.value(_drules, _drnum, "up").plus(1);
				}
			}
		}
	
		CArray _header = array(
			new CCol(_("Discovery rule"), "center"),
			new CCol(_x("Up", "discovery results in dashboard")),
			new CCol(_x("Down", "discovery results in dashboard"))
		);
	
		CTableInfo _table  = new CTableInfo();
		_table.setHeader(_header,"header");
		for(Map _drule: _drules) {
			_table.addRow(array(
				new CLink(Nest.value(_drule,"name").$(), "discovery.action?druleid="+Nest.value(_drule,"druleid").$()),
				new CSpan(Nest.value(_drule,"up").$(), "green"),
				new CSpan(Nest.value(_drule,"down").$(), (Nest.value(_drule,"down").asInteger() > 0) ? "red" : "green")
			));
		}
		CJSScript _script = new CJSScript(get_js("jQuery('#hat_dscvry_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
		return new CDiv(array(_table, _script));
	}
	

	public static void make_graph_menu(IIdentityBean idBean, SQLExecutor executor, CArray _menu, CArray _submenu) {
		CArray _menu_graphs = Nest.value(_menu, "menu_graphs").$s(true);
		_menu_graphs.add(array(
				_("Favourite graphs"),
				null,
				null,
				map("outer", array("pum_oheader"), "inner", array("pum_iheader"))
			));
	
		_menu_graphs.add( array(
			_("Add")+" "+_("Graph"),
			"javascript: PopUp('popup.action?srctbl=graphs&srcfld1=graphid&reference=graphid&multiselect=1&real_hosts=1',800,450); void(0);",
			null,
			map("outer", "pum_o_submenu", "inner", array("pum_i_submenu"))
		));
		_menu_graphs.add( array(
			_("Add")+" "+_("Simple graph"),
			"javascript: PopUp('popup.action?srctbl=items&srcfld1=itemid&reference=itemid&real_hosts=1"+
				"&multiselect=1&numeric=1&templated=0&with_simple_graph_items=1',800,450); void(0);",
			null,
			map("outer", "pum_o_submenu", "inner", array("pum_i_submenu"))
		));
		_menu_graphs.add( array(
			_("Remove"),
			null,
			null,
			map("outer" , "pum_o_submenu", "inner", array("pum_i_submenu"))
		));
		Nest.value(_submenu,"menu_graphs").$(make_graph_submenu(idBean, executor));
	}
	
	
	public static CArray make_graph_submenu(IIdentityBean idBean, SQLExecutor executor) {
		CArray graphids = array();
		CArray itemids = array();
		CArray favGraphs = array();
		CArray<Map> fav_graphs = CFavorite.get(idBean, executor, "web.favorite.graphids");
	
		if (empty(fav_graphs)) {
			return favGraphs;
		}
	
		for(Map favorite: fav_graphs) {
			if ("itemid".equals(Nest.value(favorite,"source").$())) {
				itemids.put(favorite.get("value"), Nest.value(favorite,"value").$());
			} else {
				graphids.put(favorite.get("value"), Nest.value(favorite,"value").$());
			}
		}
	
		CArray<Map> graphs = array();
		if (!empty(graphids)) {
			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(graphids.valuesAsLong());
			goptions.setSelectHosts(new String[]{"hostid", "host"});
			goptions.setOutput(new String[]{"graphid", "name"});
			goptions.put("expandName" , true);
			graphs = API.Graph(idBean, executor).get(goptions);
			graphs = rda_toHash(graphs, "graphid");
		}
	
		CArray<Map> items = array();
		if (!empty(itemids)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_"});
			ioptions.setSelectHosts(new String[]{"hostid", "host"});
			ioptions.setItemIds(itemids.valuesAsLong());
			ioptions.setWebItems(true);
			ioptions.setPreserveKeys(true);
			items = API.Item(idBean, executor).get(ioptions);	
			items = CMacrosResolverHelper.resolveItemNames(idBean, executor, items);
		}
	
		Boolean item_added = null, graph_added = null;
		for(Map favorite: fav_graphs) {
			Object source = Nest.value(favorite,"source").$();
			Object sourceid = Nest.value(favorite,"value").$();
	
			if ("itemid".equals(source)) {
				if (!isset(items,sourceid)) {
					continue;
				}
	
				item_added = true;
				Map item = items.get(sourceid);
				Map host = reset(Nest.value(item,"hosts").asCArray());
	
				favGraphs.add( map(
					"name", Nest.value(host,"host").$()+NAME_DELIMITER+Nest.value(item,"name_expanded").$(),
					"favobj", "itemid",
					"favid", sourceid,
					"favaction", "remove"
				));
			} else {
				if (!isset(graphs,sourceid)) {
					continue;
				}
	
				graph_added = true;
				Map graph = graphs.get(sourceid);
				Map ghost = reset(Nest.value(graph,"hosts").asCArray());
				favGraphs.add( map(
					"name", Nest.value(ghost,"host").$()+NAME_DELIMITER+Nest.value(graph,"name").$(),
					"favobj", "graphid",
					"favid", sourceid,
					"favaction", "remove"
				));
			}
		}
	
		if (isset(graph_added)) {
			favGraphs.add( map(
				"name", _("Remove")+" "+_("All")+" "+_("Graphs"),
				"favobj", "graphid",
				"favid", 0,
				"favaction", "remove"
			));
		}
	
		if (isset(item_added)) {
			favGraphs.add(map(
				"name", _("Remove")+" "+_("All")+" "+_("Simple graphs"),
				"favobj", "itemid",
				"favid", 0,
				"favaction", "remove"
			));
		}
	
		return favGraphs;
	}
	

	public static void make_sysmap_menu(IIdentityBean idBean, SQLExecutor executor, CArray _menu, CArray _submenu) {
		CArray menu_sysmaps = Nest.value(_menu, "menu_sysmaps").$s(true);
		menu_sysmaps.add( array(_("Favourite maps"), null, null, map("outer", array("pum_oheader"), "inner", array("pum_iheader"))) );
		menu_sysmaps.add( array(
			_("Add")+" "+_("Map"),
			"javascript: PopUp('popup.action?srctbl=sysmaps&srcfld1=sysmapid&reference=sysmapid&multiselect=1',800,450); void(0);",
			null,
			map("outer", "pum_o_submenu", "inner", array("pum_i_submenu")
		)));
		menu_sysmaps.add( array(_("Remove"), null, null, map("outer", "pum_o_submenu", "inner", array("pum_i_submenu"))));
		Nest.value(_submenu,"menu_sysmaps").$(make_sysmap_submenu(idBean, executor));
	}
	
	public static CArray make_sysmap_submenu(IIdentityBean idBean, SQLExecutor executor) {
		CArray<Map> _fav_sysmaps = CFavorite.get(idBean, executor, "web.favorite.sysmapids");
		CArray _favMaps = array();
		CArray _sysmapids = array();
		for(Map _favorite: _fav_sysmaps) {
			_sysmapids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
		}
	
		CMapGet moptions = new CMapGet();
		moptions.setSysmapIds(_sysmapids.valuesAsLong());
		moptions.setOutput(new String[]{"sysmapid", "name"});
		CArray<Map> _sysmaps = API.Map(idBean, executor).get(moptions);
		for(Map _sysmap: _sysmaps) {
			_favMaps.add( map(
				"name" , Nest.value(_sysmap,"name").$(),
				"favobj" , "sysmapid",
				"favid" , Nest.value(_sysmap,"sysmapid").$(),
				"favaction" , "remove"
			));
		}
	
		if (!empty(_favMaps)) {
			_favMaps.add( map(
				"name" , _("Remove")+" "+_("All")+" "+_("Maps"),
				"favobj" , "sysmapid",
				"favid" , 0,
				"favaction" , "remove"
			));
		}
		return _favMaps;
	}
	
	public static void make_screen_menu(IIdentityBean idBean, SQLExecutor executor, CArray _menu, CArray _submenu) {
		CArray menu_screens = Nest.value(_menu, "menu_screens").$s(true);
		menu_screens.add( array(_("Favourite screens"), null, null, map("outer", array("pum_oheader"), "inner", array("pum_iheader"))) );
		menu_screens.add( array(
			_("Add")+" "+_("Screen"),
			"javascript: PopUp('popup.action?srctbl=screens&srcfld1=screenid&reference=screenid&multiselect=1', 800, 450); void(0);",
			null,
			map("outer", "pum_o_submenu", "inner", array("pum_i_submenu")
		)));
		menu_screens.add( array(
			_("Add")+" "+_("Slide show"),
			"javascript: PopUp('popup.action?srctbl=slides&srcfld1=slideshowid&reference=slideshowid&multiselect=1', 800, 450); void(0);",
			null,
			map("outer", "pum_o_submenu", "inner", array("pum_i_submenu")
		)));
		menu_screens.add( array(_("Remove"), null, null, map("outer", "pum_o_submenu", "inner", array("pum_i_submenu"))));
		Nest.value(_submenu,"menu_screens").$(make_screen_submenu(idBean, executor));
	}
	
	public static CArray make_screen_submenu(IIdentityBean idBean, SQLExecutor executor) {
		CArray _favScreens = array();
		CArray<Map> _fav_screens = CFavorite.get(idBean, executor, "web.favorite.screenids");
	
		if (empty(_fav_screens)) {
			return _favScreens;
		}
	
		CArray _screenids = array();
		for(Map _favorite: _fav_screens) {
			if ("screenid".equals( Nest.value(_favorite,"source").$())) {
				_screenids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
			}
		}
	
		CScreenGet soptions = new CScreenGet();
		soptions.setScreenIds(_screenids.valuesAsLong());
		soptions.setOutput(new String[]{"screenid", "name"});
		CArray<Map> _screens = API.Screen(idBean, executor).get(soptions);
		_screens = rda_toHash(_screens, "screenid");
	
		boolean _slide_added = false, _screen_added= false;
		for(Map _favorite: _fav_screens) {
			Object _source = Nest.value(_favorite,"source").$();
			Long _sourceid = Nest.value(_favorite,"value").asLong();
			if ("slideshowid".equals(_source)) {
				if (!slideshow_accessible(idBean, executor, _sourceid, PERM_READ)) {
					continue;
				}
				Map _slide = get_slideshow_by_slideshowid(idBean, executor, _sourceid);
				if (empty(_slide)) {
					continue;
				}
				_slide_added = true;
				_favScreens.add( map(
					"name", Nest.value(_slide,"name").$(),
					"favobj", "slideshowid",
					"favid", Nest.value(_slide,"slideshowid").$(),
					"favaction", "remove"
				));
			}
			else {
				if (!isset(_screens.get(_sourceid))) {
					continue;
				}
				Map _screen = _screens.get(_sourceid);
				_screen_added = true;
				_favScreens.add( map(
					"name", Nest.value(_screen,"name").$(),
					"favobj", "screenid",
					"favid", Nest.value(_screen,"screenid").$(),
					"favaction", "remove"
				));
			}
		}
	
		if (isset(_screen_added)) {
			_favScreens.add( map(
				"name", _("Remove")+" "+_("All")+" "+_("Screens"),
				"favobj", "screenid",
				"favid", 0,
				"favaction", "remove"
			));
		}
	
		if (isset(_slide_added)) {
			_favScreens.add( map(
				"name" , _("Remove")+" "+_("All")+" "+_("Slides"),
				"favobj" , "slideshowid",
				"favid" , 0,
				"favaction" , "remove"
			));
		}
		return _favScreens;
	}
	
	/**
	 * Generate table for dashboard triggers popup.
	 *
	 * @see make_system_status
	 *
	 * @param array _triggers
	 * @param array _ackParams
	 * @param array _actions
	 * @param array _config
	 *
	 * @return CTableInfo
	 */
	public static CTableInfo makeTriggersPopup(IIdentityBean idBean, SQLExecutor executor, CArray<Map> _triggers, CArray _ackParams, CArray _actions, CArray _config) {
		CTableInfo _popupTable = new CTableInfo();
		_popupTable.setAttribute("style", "width: 400px;");
		_popupTable.setHeader(array(
			_("Host"),
			_("Issue"),
			_("Age"),
			_("Info"),
			Nest.value(_config,"event_ack_enable").asBoolean() ? _("Ack") : null,
			_("Actions")
		));
	
		CArrayHelper.sort(_triggers, array(map("field", "lastchange", "order", RDA_SORT_DOWN)));
	
		for(Map _trigger: _triggers) {
			// unknown triggers
			Object _unknown = SPACE;
			if (Nest.value(_trigger,"state").asInteger() == TRIGGER_STATE_UNKNOWN) {
				_unknown = new CDiv(SPACE, "status_icon iconunknown");
				((CDiv)_unknown).setHint(Nest.value(_trigger,"error").$(), "", "on");
			}
	
			// ack
			Object _ack;
			if (Nest.value(_config,"event_ack_enable").asBoolean()) {
				_ack = isset(Nest.value(_trigger,"event","eventid").$())
					? getEventAckState(idBean, executor, Nest.value(_trigger,"event").asCArray(), true, true, _ackParams)
					: _("No events");
			}
			else {
				_ack = null;
			}
	
			// action
			Object _action = (isset(Nest.value(_trigger,"event","eventid").$()) && isset(_actions.get(Nest.value(_trigger, "event", "eventid").$())))
				? _actions.get(Nest.value(_trigger, "event", "eventid").$())
				: _("-");
	
			_popupTable.addRow(array(
				Nest.value(_trigger,"hosts", 0, "name").$(),
				getSeverityCell(idBean, executor, Nest.value(_trigger,"priority").asInteger(), Nest.value(_trigger,"description").$()),
				rda_date2age(Nest.value(_trigger,"lastchange").asLong()),
				_unknown,
				_ack,
				_action
			));
		}
	
		return _popupTable;
	}
}
