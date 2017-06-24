package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.array_keys;
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
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCell;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.iradar.inc.TriggersUtil.resolveTriggerUrl;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.isoft.Feature;
import com.isoft.biz.daoimpl.platform.topo.TopoDAO;
import com.isoft.biz.vo.platform.topo.Topo;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.api.API.Wrapper;
import com.isoft.iradar.common.util.CommonUtils;
import com.isoft.iradar.common.util.IMonConsts;
import com.isoft.iradar.common.util.ItemsKey;
import com.isoft.iradar.common.util.LatestValueHelper;
import com.isoft.iradar.common.util.LatestValueHelper.Matcher;
import com.isoft.iradar.common.util.LatestValueHelper.NormalValue;
import com.isoft.iradar.common.util.LatestValueHelper.PrototypeValues;
import com.isoft.iradar.common.util.LatestValueHelper.ValuePrinter;
import com.isoft.iradar.common.util.MoncategoryUtil;
import com.isoft.iradar.common.util.TopNHelper;
import com.isoft.iradar.core.g;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.data.DataDriver;
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
import com.isoft.iradar.model.params.CScreenGet;
import com.isoft.iradar.model.params.CTriggerGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.FrontendSetup;
import com.isoft.iradar.web.action.core.EventsAction;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class BlocksUtil {
	
	private static final String controller = "controller";	//控制节点标识
	private static final String compute = "compute";		//计算节点标识
	private static final String network = "network";		//网络节点标识
	private static final String storage = "storage";		//存储节点标识
	private static final String web = "web";				//门户节点标识
	
	public static CList make_favorite_graphs(IIdentityBean idBean, SQLExecutor executor) {
		CList favList = new CList(null, "firstshape", _("No graphs added."));
		CArray graphids = array();
		CArray itemids = array();

		CArray<Map> fav_graphs = CFavorite.get(idBean, executor, "web.favorite.graphids");

		if (empty(fav_graphs)) {
			return favList;
		}

		for(Map favorite: fav_graphs) {
			if ("itemid".equals(Nest.value(favorite,"source").asString())) {
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
		
		String stime = Cphp.date(Defines.TIMESTAMP_FORMAT);
		int period = 3600;
		int index = 0;
		for(Map favorite: fav_graphs) {
			String source = Nest.value(favorite, "source").asString();
			Long sourceid = Nest.value(favorite,"value").asLong();
			
			String cls = (index++)%2==0? "left": "right"; 
			if ("itemid".equals(source)) {
				if (!isset(items.get(sourceid))) {
					continue;
				}

				String src = "chart.action?itemid=" + sourceid + "&period=" + period + "&stime=" + stime
						+ "&updateProfile=0&profileIdx=web.item.graph&profileIdx2=0&width=630";
				favList.addItem(new CImg(src), cls);
			}else if("graphid".equals(source)) {
				if (!isset(graphs.get(sourceid))) {
					continue;
				}

				String src = "chart2.action?graphid=" + sourceid + "&period=" + period + "&stime=" + stime
						+ "&updateProfile=0&profileIdx=web.screens&profileIdx2=0&width=630";
				favList.addItem(new CImg(src), cls);
			}
			
			if(index % 2 == 0) {
				favList.addItem("", "clearfix");
			}
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
				link = null;
//				if (!slideshow_accessible(idBean, executor, sourceid, PERM_READ)) {
//					continue;
//				}
//				Map slide = get_slideshow_by_slideshowid(idBean, executor, sourceid);
//				if (empty(slide)) {
//					continue;
//				}
//	
//				link = new CLink(Nest.value(slide,"name").$(), "slides.action?elementid="+sourceid);
//				link.setTarget("blank");
			} else {
				if (!isset(screens.get(sourceid))) {
					continue;
				}
				Map screen = screens.get(sourceid);
	
				link = new CLink(Nest.value(screen,"name").$(), "screens.action?elementid="+sourceid);
				link.setTarget("_blank");
			}
			favList.addItem(link, "nowrap");
		}
		return favList;
	}
	/**
	 * 首页概览-拓扑图
	 * @param idBean
	 * @param executor
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static CDiv make_favorite_maps(IIdentityBean idBean, SQLExecutor executor) {
		CDiv div = new CDiv();
		CArray<Map> _fav_sysmaps = CFavorite.get(idBean, executor, "web.favorite.sysmapids");
		if (empty(_fav_sysmaps)) {
			return div;
		}
	
		CArray _sysmapids = array();
		for(Map _favorite: _fav_sysmaps) {
			_sysmapids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
		}
	
		
		for(Long _sysmapid: _sysmapids.valuesAsLong()) {
			CLink _link = new CLink("/platform/iradar/NetTopoIndex.action?topoId="+_sysmapid);
			_link.setTarget("blank");
			String imgUrl = "topochart.action?topoId="+_sysmapid;
			CImg img = new CImg(imgUrl);
			//_link.addItem(img);
			div.addItem(img);
		}
		div.setAttribute("align", "center");
		return div;
	}
	
	/**
	 * 首页概览-拓扑图-测试专用
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static CDiv make_favorite_maps_new(IIdentityBean idBean, SQLExecutor executor) {
		String imgUrl = "topochart.action?topoId=600";//"images/gradients/tuopu_test.png";
		CImg img = new CImg(imgUrl);
		CDiv div = new CDiv(img);
		div.setAttribute("align", "center");
		return div;
	}	
	
	/**
	 * 首页概览-系统状态面板
	 * @param idBean
	 * @param executor
	 * @param _filter
	 * @return
	 */
	public static Map make_system_status(IIdentityBean idBean, SQLExecutor executor, CArray _filter) {
		Map allhostType = new HashMap<String,List>();
		CArray _ackParams = array();
		if (!empty(Nest.value(_filter,"screenid").$())) {
			Nest.value(_ackParams,"screenid").$(Nest.value(_filter,"screenid").$());
		}
	
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
		hgoptions.setLimit(11); //默认显示10条记录
		CArray<Map> _Ngroups = API.HostGroup(idBean, executor).get(hgoptions);
	
		//去掉 Discovered hosts 设备类型
		CArray<Map> _groups=new CArray<Map>();
		for(Map _group: _Ngroups){
			if(!"Discovered hosts".equals(_group.get("name"))){	
				_groups.put(_group.get("groupid"), _group);
			}
		}
		
		CArray _sortOptions = array();
		_sortOptions.add( array("field", "name", "order", RDA_SORT_UP) );
	
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
			toptions.setFilter("priority", Nest.value(_filter,"severity").asCArray().valuesAsLong());
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
		//CArray _actions = getEventActionsStatus(idBean, executor, _eventIds);
	
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
	
		String groupName = null;		//设备类型名称
		Map normalMap = new HashMap();
		for(Map _group: _groups) {		//循环所有的设备类型
			int zerolevel = 0;
			int onelevel = 0;
			int twolevel = 0;
			int threelevel = 0;
			int fourlevel = 0;
			int fivelevel = 0;
			
			CRow _groupRow = new CRow();
			groupName = _group.get("name").toString();
			CLink _name = new CLink(Nest.value(_group,"name").$(), "tr_status.action?groupid="+_group.get("groupid")+"&hostid=0&show_triggers="+TRIGGERS_OPTION_ONLYTRUE);
			_groupRow.addItem(_name);
	
			for(Entry<Object, Map> entry: ((CArray<Map>)Nest.value(_group,"tab_priority").asCArray()).entrySet()) {
				int _severity = asInteger(entry.getKey());
				Map _data = entry.getValue();
				if (!is_null(Nest.value(_filter,"severity").$()) && !isset(_filter.getNested("severity", _severity))) {
					continue;
				}
				
				Integer _allTriggersNum = Nest.value(_data,"count").asInteger();
				//Object _unackTriggersNum = Nest.value(_data,"count_unack").$();
				
				switch (_severity) {
				case 0:
					zerolevel+=_allTriggersNum;
					break;
				case 1:
					onelevel+=_allTriggersNum;
					break;
				case 2:
					twolevel+=_allTriggersNum;
					break;
				case 3:
					threelevel+=_allTriggersNum;
					break;
				case 4:
					fourlevel+=_allTriggersNum;
					break;
				case 5:
					fivelevel+=_allTriggersNum;
					break;

				default:
					break;
				}
			}
			normalMap = new HashMap();
			normalMap.put(0, zerolevel);
			normalMap.put(1, onelevel);
			normalMap.put(2, twolevel);
			normalMap.put(3, threelevel);
			normalMap.put(4, fourlevel);
			normalMap.put(5, fivelevel);
			allhostType.put(groupName, normalMap);
		}
		
		//根据事件告警等级返回对应的颜色
		Map config = select_config(idBean, executor);
		Map[] severities = new Map[6];
		severities[0] = map(
				"name",_(Nest.value(config,"severity_name_0").asString()),
				"color",config.get("severity_color_0"));
		severities[1] = map(
				"name",_(Nest.value(config,"severity_name_1").asString()),
				"color",config.get("severity_color_1"));
		severities[2] = map(
				"name",_(Nest.value(config,"severity_name_2").asString()),
				"color",config.get("severity_color_2"));
		severities[3] = map(
				"name",_(Nest.value(config,"severity_name_3").asString()),
				"color",config.get("severity_color_3"));
		severities[4] = map(
				"name",_(Nest.value(config,"severity_name_4").asString()),
				"color",config.get("severity_color_4"));
		severities[5] = map(
				"name",_(Nest.value(config,"severity_name_5").asString()),
				"color",config.get("severity_color_5"));
		
		Map rets = new HashMap();
		rets.put("severities", severities);
		rets.put("devices", allhostType);
		return rets;
	}
	
	public static CDiv make_system_status_div(IIdentityBean idBean, SQLExecutor executor, CArray _filter) {
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
			if(!Feature.hostGroupsNeedHide.containsKey(Nest.value(_group, "groupid").asLong())){
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
			if(!Feature.hostGroupsNeedHide.containsKey(Nest.value(_group, "groupid").asLong())){
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
		}
	
		CJSScript _script = new CJSScript(get_js("jQuery('#hat_syssum_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
	
		return new CDiv(array(_table, _script));
	}
	
	/**
	 * 设备状态面板
	 * @param idBean
	 * @param executor
	 * @param _filter
	 * @return
	 */
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
		if(isset(_filter,"hostids")){
			hgoptions.setHostIds(Nest.array(_filter,"hostids").asLong());
		}
		
		hgoptions.setMonitoredHosts(true);
		hgoptions.setOutput(new String[]{"groupid", "name"});
		CArray<Map> _Ngroups = API.HostGroup(idBean, executor).get(hgoptions);
		CArray<Map> _groups=new CArray<Map>();
		for(Map _group: _Ngroups){//隐藏设备类型为Discovered hosts
			if(!"Discovered hosts".equals(_group.get("name"))){	
				_groups.put(_group.get("groupid"), _group);
			}
		}
		_groups = rda_toHash(_groups, "groupid");
	
		CArrayHelper.sort(_groups, array(
			map("field", "nodename", "order", RDA_SORT_UP),
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
						host.put("host", Nest.value(_host,"name").$());
						host.put("hostid", Nest.value(_host,"hostid").$());
						
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
	
				if (!isset(_problematic_host_list.get(_hostId))) {
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
			CTableInfo _table_inf = new CTableInfo();
			if (Nest.value(_hosts_data, _groupid, "problematic").asBoolean()) {
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
				
			
				_problematic_count =new CSpan(_hosts_data.getNested(_groupid, "problematic"), "pointer");
				
			//	((CSpan)_problematic_count).setHint(_table_inf);
			} else {
				_problematic_count = 0;
			}
	
			switch (Nest.value(_filter,"extAck").asInteger()) {
				case EXTACK_OPTION_ALL:
				CCol cc = new CCol(_hosts_data.getNested(_groupid, "problematic"), "pointer");
				cc.setHint(_table_inf);
				if (!(_problematic_count.equals(0))) {
					cc.addClass(getSeverityStyle(Nest.value(_highest_severity, _groupid).asInteger(), Nest
							.value(_hosts_data, _groupid, "problematic").asBoolean()));
					_group_row.addItem(cc);
					_group_row.addItem(Nest.value(_hosts_data, _groupid, "problematic").asInteger()
							+ Nest.value(_hosts_data, _groupid, "ok").asInteger());
					break;
				} else {
					_group_row.addItem(new CCol(_problematic_count, getSeverityStyle(Nest.value(_highest_severity, _groupid).asInteger(),
							Nest.value(_hosts_data, _groupid, "problematic").asBoolean())));
					_group_row.addItem(Nest.value(_hosts_data, _groupid, "problematic").asInteger()
							+ Nest.value(_hosts_data, _groupid, "ok").asInteger());
					break;
				}

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
	
	/**
	 * 系统信息面板
	 * @param idBean
	 * @param executor
	 * @return
	 */
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
		//租户数、用户数容易混淆，先注释
		//table.addRow(array(_("Number of users (online)"), Nest.value(status,"users_count").$(), new CSpan(Nest.value(status,"users_online").$(), "green")));
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
		options.setEditable(true);
		
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
		//Object triggersTotalCount = API.Trigger(idBean, executor).get(toptions);
	
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
	
		CArray triggersData = array();
		for(Entry<Object, Map> entry: triggers.entrySet()) {
			Object tnum = entry.getKey();
			Map trigger = entry.getValue();
			// if trigger is lost (broken expression) we skip it
			if (empty(Nest.value(trigger,"hosts").$())) {
				unset(triggersData, tnum);
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
	
			triggersData.put(tnum, trigger);
		}
		triggers = triggersData;
	
		CArray hostIds = rda_objectValues(triggers, "hostid");
	
		// get hosts
		CHostGet hoptions = new CHostGet();
		hoptions.setHostIds(hostIds.valuesAsLong());
		hoptions.setOutput(new String[]{"hostid", "name", "status", "maintenance_status", "maintenance_type", "maintenanceid"});
		hoptions.setSelectScreens(API_OUTPUT_COUNT);
		hoptions.setSelectGroups(new String[]{"groupid","name"});
		hoptions.setPreserveKeys(true);
		CArray<Map> hosts = API.Host(idBean, executor).get(hoptions);
	
		// actions
		//CArray actions = getEventActionsStatHints(idBean, executor, eventIds);
	
		// ack params
		CArray ackParams = isset(Nest.value(filter,"screenid").$()) ? map("screenid", Nest.value(filter,"screenid").$()) : array();
	
		Map config = select_config(idBean, executor);
	
		// indicator of sort field
		CDiv sortDiv=null, hostHeaderDiv=null, severityHeaderDiv=null, lastChangeHeaderDiv=null;
		if (showSortIndicator) {
			sortDiv = new CDiv(SPACE, (Nest.value(filter,"sortorder").$() == RDA_SORT_DOWN) ? "icon_sortdown default_cursor" : "icon_sortup default_cursor");
			sortDiv.addStyle("float: left");
			hostHeaderDiv = new CDiv(array(_("Host"), SPACE));
			hostHeaderDiv.addStyle("float: left");
			severityHeaderDiv = new CDiv(array(_("severity level"), SPACE));
			severityHeaderDiv.addStyle("float: left");
			lastChangeHeaderDiv = new CDiv(array(_("Open time"), SPACE));
			lastChangeHeaderDiv.addStyle("float: left");
		}
	
		CTableInfo table = new CTableInfo(_("No events found."));
		table.setHeader(array(
			(showSortIndicator && ("hostname".equals(Nest.value(filter,"sortfield").$()))) ? array(hostHeaderDiv, sortDiv) : _("By host"),
			(showSortIndicator && ("priority".equals(Nest.value(filter,"sortfield").$()))) ? array(severityHeaderDiv, sortDiv) : _("severity level"),
			_("Template trigger Name"),			
			(showSortIndicator && ("lastchange".equals(Nest.value(filter,"sortfield").$()))) ? array(lastChangeHeaderDiv, sortDiv) : _("Open time"),
			_("Age"),
			Nest.value(config,"event_ack_enable").asBoolean() ? _("Ack") : null
		));
	
		// triggers
		for(Map ctrigger: triggers) {
			CArray trigger = asCArray(ctrigger);
			CArray host = asCArray(hosts.get(trigger.get("hostid")));
			//hostDiv
//			CLink hostDivLink = new CLink(Nest.value(host,"name").$(),"hostinventories.action?hostid="+host.get("hostid"));
//			String urlHost = "'00020002', '"+RadarContext.getContextPath()+"/platform/iradar/hostinventories.action?hostid="+host.get("hostid")+"'";
//			CLink hostDivLink = new CLink(Nest.value(host,"name").$(),"javascript:top.jQuery.workspace.openTab("+urlHost+")",null,null,Boolean.TRUE);

			CLink hostDivLink = EventsAction.getHostDetailUrl(host);
			
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
			description = new CCol(description);
			if (!empty(Nest.value(trigger,"lastEvent").$())) {
				((CCol)description).setHint(
					make_popup_eventlist(idBean, executor, Nest.value(trigger,"triggerid").asLong(), Nest.value(trigger,"lastEvent","eventid").asLong()),
					"", "", true
				);
			}
	
			// clock
//			boolean isAlarm = Nest.value(trigger,"priority").asInteger() <= Defines.TRIGGER_SEVERITY_WARNING;
//			String urlPrefix = isAlarm? "'00040001', '"+RadarContext.getContextPath()+"/platform/iradar/activealarm.action": "'00040003', '"+RadarContext.getContextPath()+"/platform/iradar/activefault.action";
//			String actionPrefix = isAlarm? "activealarm.action": "activefault.action";
			String urlPrefix = "'"+_("Active Trigger")+"', '"+RadarContext.getContextPath()+"/platform/iradar/activealarm.action";
			String actionPrefix = "activealarm.action";
			CLink clock = new CLink(
				rda_date2str(_("d M Y H:i:s"), Nest.value(trigger,"lastchange").asLong()),
				showSortIndicator? //监控大屏里用到
				actionPrefix+"?triggerid="+trigger.get("triggerid")+"&source="+EVENT_SOURCE_TRIGGERS+"&show_unknown=1"+
						"&hostid="+trigger.get("hostid")+"&stime="+date(TIMESTAMP_FORMAT, Nest.value(trigger,"lastchange").asLong())+
						"&period="+RDA_PERIOD_DEFAULT:
				"javascript:top.jQuery.workspace.openTab("+urlPrefix+"?triggerid="+trigger.get("triggerid")+"&source="+EVENT_SOURCE_TRIGGERS+"&show_unknown=1"+
					"&hostid="+trigger.get("hostid")+"&stime="+date(TIMESTAMP_FORMAT, Nest.value(trigger,"lastchange").asLong())+
					"&period="+RDA_PERIOD_DEFAULT+"')",
				null,
				null,
				Boolean.TRUE
			);
	
			// actions
			//Object eventDesc = actions.get(Nest.value(trigger, "lastEvent", "eventid").$());
			
			table.addRow(array(
				hostDivLink,
//				TriggersUtil.getSeverityCell(idBean, executor, Nest.value(trigger,"priority").asInteger()),
//				修改为文字加图标方式  
				getTriggerLevel(Nest.value(trigger,"priority").asInteger(),idBean,executor),
				description,
				clock,
				rda_date2age(Nest.value(trigger,"lastchange").asLong()),
				ack
			));
		}
	
		// initialize blinking
		rda_add_post_js("jqBlink.blink();");
		CJSScript script = new CJSScript(get_js("jQuery('#hat_lastiss_footer').html('"+_s("Updated: %s", rda_date2str(_("H:i:s")))+"')"));
		return new CDiv(array(table, script));
	}
	
	public static String getSeverityCss(IIdentityBean idBean, SQLExecutor executor) {
		Map config = select_config(idBean, executor);
		StringBuilder severityCss = new StringBuilder();
		severityCss.append(".disaster { background: #").append(config.get("severity_color_5")).append(" !important; }  ");
		severityCss.append(".high { background: #").append(config.get("severity_color_4")).append(" !important; }  ");
		severityCss.append(".average { background: #").append(config.get("severity_color_3")).append(" !important; }  ");
		severityCss.append(".warning { background: #").append(config.get("severity_color_2")).append(" !important; }  ");
		severityCss.append(".information { background: #").append(config.get("severity_color_1")).append(" !important; }  ");
		severityCss.append(".not_classified { background: #").append(config.get("severity_color_0")).append(" !important; }  ");
		
		severityCss.append(".c_disaster { background: #").append(config.get("severity_color_5")).append(" !important; }  ");
		severityCss.append(".c_high { background: #").append(config.get("severity_color_4")).append(" !important; }  ");
		severityCss.append(".c_average { background: #").append(config.get("severity_color_3")).append(" !important; }  ");
		severityCss.append(".c_warning { background: #").append(config.get("severity_color_2")).append(" !important; }  ");
		severityCss.append(".c_information { background: #").append(config.get("severity_color_1")).append(" !important; }  ");
		severityCss.append(".c_not_classified { background: #").append(config.get("severity_color_0")).append(" !important; }  ");
		
		severityCss.append(".circle { border-radius: 50%; width: 18px; height: 18px;margin-left:2px;}");
		return severityCss.toString();
	}
	
	/**
	 * 获取告警等级图标
	 * @param level
	 * @return
	 */
//	public static CDiv getTriggerLevel(Integer level){
//		return getTriggerLevel(level, null, null);
//	}
	
	public static CDiv getTriggerLevel(Integer level,IIdentityBean idBean, SQLExecutor executor){
		CDiv priority = new CDiv();
		priority.addClass("circle");
		priority.addClass("c_"+getSeverityStyle(level));
		Map config = select_config(idBean, executor);

		String serverityName = Nest.value(config, "severity_name_"+level).asString();
		String serverityColor = Nest.value(config, "severity_color_"+level).asString();;
		priority.addItem(_(serverityName));
		priority.addStyle("background:#"+serverityColor+" !important;");
		return priority;
//		CSpan span = new CSpan();
//		CImg img = new CImg("images/gradients/level0.png");
//		switch (level) {
//		case 0:
//			img = new CImg("images/gradients/level0.png");
//			span.addItem(array(img,_("Not classified")));
//			break;
//		case 1:
//			img = new CImg("images/gradients/level1.png");
//			span.addItem(array(img,_("Information")));
//			break;
//		case 2:
//			img = new CImg("images/gradients/level2.png");
//			span.addItem(array(img,_("Warning")));
//			break;
//		case 3:
//			img = new CImg("images/gradients/level3.png");
//			span.addItem(array(img,_("Average")));
//			break;
//		case 4:
//			img = new CImg("images/gradients/level4.png");
//			span.addItem(array(img,_("High")));
//			break;
//		case 5:
//			img = new CImg("images/gradients/level5.png");
//			span.addItem(array(img,_("Disaster")));
//			break;
//		default:
//			break;
//		}
//		
//		return span;
	}

	
	
	/**
	 *  Web服务监控面板
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
		CArray<Map> _Ngroups = API.HostGroup(idBean, executor).get(hgoptions);
	
		//去掉 Discovered hosts 设备类型
		CArray<Map> _groups=new CArray<Map>();
		for(Map _group: _Ngroups){
			if(!"Discovered hosts".equals(_group.get("name"))){	
				_groups.put(_group.get("groupid"), _group);
			}
		}
		
		CArrayHelper.sort(_groups, array(
			map("field", "nodename", "order", RDA_SORT_UP),
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
			map("field" , "nodename", "order" , RDA_SORT_UP),
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
			new CCol(_("Discovery rule"), ""),
			new CCol(_x("Up", "discovery results in dashboard")),
			new CCol(_x("Down", "discovery results in dashboard"))
		);
	
		CTableInfo _table  = new CTableInfo();
		_table.setHeader(_header,"header");
		for(Map _drule: _drules) {
			_table.addRow(array(
				Nest.value(_drule,"nodename").$(),
				new CLink(StringUtils.defaultString(Nest.value(_drule,"nodename").asString(), "")+(Nest.value(_drule,"nodename").asBoolean() ? NAME_DELIMITER : "")+Nest.value(_drule,"name").$(), "discovery.action?druleid="+Nest.value(_drule,"druleid").$()),
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
				map("outer", array("pum_oheader triangle"), "inner", array("pum_iheader user_figure"))
			));
	
		_menu_graphs.add( array(
			_("Add")+" "+_("Graph"),
			"javascript: PopUp('popup.action?srctbl=graphs&srcfld1=graphid&reference=graphid&multiselect=1&real_hosts=1',800,450); void(0);",
			null,
			map("outer", array("pum_o_submenu triangle"), "inner", array("pum_i_submenu add"))
		));
		_menu_graphs.add( array(
			_("Add")+" "+_("Simple graph"),
			"javascript: PopUp('popup.action?srctbl=items&srcfld1=itemid&reference=itemid&real_hosts=1"+
				"&multiselect=1&numeric=1&templated=0&with_simple_graph_items=1',800,450); void(0);",
			null,
			map("outer", array("pum_o_submenu triangle"), "inner", array("pum_i_submenu add"))
		));
		_menu_graphs.add( array(
			_("Remove"),
			null,
			null,
			map("outer" , array("pum_o_submenu triangle"), "inner", array("pum_i_submenu remove"))
		));
		Nest.value(_submenu,"menu_graphs").$(make_graph_submenu(idBean, executor));
	}
	
	
	public static CArray make_graph_submenu(IIdentityBean idBean, SQLExecutor executor) {
		CArray _graphids = array();
		CArray _itemids = array();
		CArray _favGraphs = array();
		CArray<Map> _fav_graphs = CFavorite.get(idBean, executor, "web.favorite.graphids");
	
		if (empty(_fav_graphs)) {
			return _favGraphs;
		}
	
		for(Map _favorite: _fav_graphs) {
			if ("itemid".equals(Nest.value(_favorite,"source").$())) {
				_itemids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
			}
			else {
				_graphids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
			}
		}
	
		CArray<Map> _graphs = array();
		if (!empty(_graphids)) {
			CGraphGet goptions = new CGraphGet();
			goptions.setGraphIds(_graphids.valuesAsLong());
			goptions.setSelectHosts(new String[]{"hostid", "host"});
			goptions.setOutput(new String[]{"graphid", "name"});
			goptions.put("expandName" , true);
			_graphs = API.Graph(idBean, executor).get(goptions);
			_graphs = rda_toHash(_graphs, "graphid");
		}
	
		CArray<Map> _items = array();
		if (!empty(_itemids)) {
			CItemGet ioptions = new CItemGet();
			ioptions.setOutput(new String[]{"itemid", "hostid", "name", "key_"});
			ioptions.setSelectHosts(new String[]{"hostid", "host"});
			ioptions.setItemIds(_itemids.valuesAsLong());
			ioptions.setWebItems(true);
			ioptions.setPreserveKeys(true);
			_items = API.Item(idBean, executor).get(ioptions);
	
			_items = CMacrosResolverHelper.resolveItemNames(idBean, executor, _items);
		}
	
		Boolean _item_added = null, _graph_added = null;
		for(Map _favorite: _fav_graphs) {
			Object _source = Nest.value(_favorite,"source").$();
			Object _sourceid = Nest.value(_favorite,"value").$();
	
			if ("itemid".equals(_source)) {
				if (!isset(_items.get(_sourceid))) {
					continue;
				}
	
				_item_added = true;
				Map _item = _items.get(_sourceid);
				Map _host = reset(Nest.value(_item,"hosts").asCArray());
	
				_favGraphs.add( map(
					"name", _host.get("host")+NAME_DELIMITER+Nest.value(_item,"name_expanded").$(),
					"favobj", "itemid",
					"favid", _sourceid,
					"favaction", "remove"
				));
			}
			else {
				if (!isset(_graphs.get(_sourceid))) {
					continue;
				}
	
				_graph_added = true;
				Map _graph = _graphs.get(_sourceid);
				Map _ghost = reset(Nest.value(_graph,"hosts").asCArray());
				_favGraphs.add( map(
					"name", _ghost.get("host")+NAME_DELIMITER+Nest.value(_graph,"name").$(),
					"favobj", "graphid",
					"favid", _sourceid,
					"favaction", "remove"
				));
			}
		}
	
		if (isset(_graph_added)) {
			_favGraphs.add( map(
				"name", _("Remove")+" "+_("All")+" "+_("Graphs"),
				"favobj", "graphid",
				"favid", 0,
				"favaction", "remove"
			));
		}
	
		if (isset(_item_added)) {
			_favGraphs.add( map(
				"name", _("Remove")+" "+_("All")+" "+_("Simple graphs"),
				"favobj", "itemid",
				"favid", 0,
				"favaction", "remove"
			));
		}
	
		return _favGraphs;
	}
	

	public static void make_sysmap_menu(IIdentityBean idBean, SQLExecutor executor, CArray _menu, CArray _submenu) {
		CArray menu_sysmaps = Nest.value(_menu, "menu_sysmaps").$s(true);
		menu_sysmaps.add( array(_("Favourite maps"), null, null, map("outer", array("pum_oheader triangle"), "inner", array("pum_iheader user_figure"))) );
		menu_sysmaps.add( array(
			_("Add")+" "+_("Map"),
			"javascript: PopUp('popup.action?srctbl=sysmaps&srcfld1=sysmapid&reference=sysmapid&multiselect=1',800,450); void(0);",
			null,
			map("outer", array("pum_o_submenu triangle"), "inner", array("pum_i_submenu add")
		)));
		menu_sysmaps.add( array(_("Remove"), null, null, map("outer", "pum_o_submenu triangle", "inner", array("pum_i_submenu remove"))));
		Nest.value(_submenu,"menu_sysmaps").$(make_sysmap_submenu(idBean, executor));
	}
	
	public static CArray make_sysmap_submenu(IIdentityBean idBean, SQLExecutor executor) {
		CArray<Map> _fav_sysmaps = CFavorite.get(idBean, executor, "web.favorite.sysmapids");
		CArray _favMaps = array();
		CArray _sysmapids = array();
		for(Map _favorite: _fav_sysmaps) {
			_sysmapids.put(_favorite.get("value"), Nest.value(_favorite,"value").$());
		}
		
		List<Long> topoIds = new ArrayList();
		for(Long topoId:_sysmapids.valuesAsLong()){
			topoIds.add(topoId);
		}
		List<Topo> topos = new ArrayList();
		if(!empty(topoIds)){
			TopoDAO topoDao = new TopoDAO(executor);
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("tenantId", idBean.getTenantId());
			tempMap.put("userId", idBean.getUserId());
			tempMap.put("topoType", "nettopo");
			tempMap.put("topoId", topoIds);
	        topos =  topoDao.doTopoList(tempMap);
		}
//		CMapGet moptions = new CMapGet();
//		moptions.setSysmapIds(_sysmapids.valuesAsLong());
//		moptions.setOutput(new String[]{"sysmapid", "name"});
//		CArray<Map> _sysmaps = API.Map(idBean, executor).get(moptions);
//		for(Map _sysmap: _sysmaps) {
//			_favMaps.add( map(
//				"name" , Nest.value(_sysmap,"name").$(),
//				"favobj" , "sysmapid",
//				"favid" , Nest.value(_sysmap,"sysmapid").$(),
//				"favaction" , "remove"
//			));
//		}
		for(Topo topo: topos) {
			_favMaps.add( map(
				"name" , topo.getTopoName(),
				"favobj" , "sysmapid",
				"favid" , topo.getId(),
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
		menu_screens.add( array(_("Favourite screens"), null, null, map("outer", array("pum_oheader triangle"), "inner", array("pum_iheader user_figure"))) );
		menu_screens.add( array(
			_("Add")+" "+_("Screen"),
			"javascript: PopUp('popup.action?srctbl=screens&srcfld1=screenid&reference=screenid&multiselect=1', 800, 450); void(0);",
			null,
			map("outer", "pum_o_submenu triangle", "inner", array("pum_i_submenu add")
		)));
//		menu_screens.add( array(
//			_("Add")+" "+_("Slide show"),
//			"javascript: PopUp('popup.action?srctbl=slides&srcfld1=slideshowid&reference=slideshowid&multiselect=1', 800, 450); void(0);",
//			null,
//			map("outer", "pum_o_submenu triangle", "inner", array("pum_i_submenu add")
//		)));
		menu_screens.add( array(_("Remove"), null, null, map("outer", "pum_o_submenu triangle", "inner", array("pum_i_submenu remove"))));
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
	
		//boolean _slide_added = false;
		boolean _screen_added= false;
		for(Map _favorite: _fav_screens) {
			Object _source = Nest.value(_favorite,"source").$();
			Long _sourceid = Nest.value(_favorite,"value").asLong();
			if ("slideshowid".equals(_source)) {
//				if (!slideshow_accessible(idBean, executor, _sourceid, PERM_READ)) {
//					continue;
//				}
//				Map _slide = get_slideshow_by_slideshowid(idBean, executor, _sourceid);
//				if (empty(_slide)) {
//					continue;
//				}
//				_slide_added = true;
//				_favScreens.add( map(
//					"name", Nest.value(_slide,"name").$(),
//					"favobj", "slideshowid",
//					"favid", Nest.value(_slide,"slideshowid").$(),
//					"favaction", "remove"
//				));
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
	
//		if (isset(_slide_added)) {
//			_favScreens.add( map(
//				"name" , _("Remove")+" "+_("All")+" "+_("Slides"),
//				"favobj" , "slideshowid",
//				"favid" , 0,
//				"favaction" , "remove"
//			));
//		}
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
	
	/**
	 * 系统健康度面板
	 * @param idBean	
	 * @param executor
	 * @param _filter
	 * @return
	 */
	public static Map make_totalSituation(IIdentityBean idBean, SQLExecutor executor) {
		Map healthMap = new HashMap();
		//获取计算节点下 所有的设备ID
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_COMPUTER);
		String[] hostids = hosts.keys();
		
		BigDecimal hostLinkHealth = new BigDecimal(CommonUtils.returnHostLinkHealthNum(idBean,executor,hostids));
		BigDecimal cpuHealth = new BigDecimal(CommonUtils.returnHostHealthNum(ItemsKey.CPU_USER_RATE.getValue(), hostids));
		BigDecimal memoryHealth = new BigDecimal(CommonUtils.returnHostHealthNum(ItemsKey.MEMORY_USELV_KEY.getValue(),true, hostids));
		BigDecimal diskHealth = new BigDecimal(CommonUtils.returnHostHealthNum(ItemsKey.DISK_USELV_KEY.getValue(),true, hostids));
		int systemHealth = (int) (hostLinkHealth.intValue()*0.4+cpuHealth.intValue()*0.2+memoryHealth.intValue()*0.2+diskHealth.intValue()*0.2);
		
		healthMap.put("hostLinkHealth", hostLinkHealth.intValue());
		healthMap.put("cpuHealth", cpuHealth);
		healthMap.put("memoryHealth", memoryHealth);
		healthMap.put("diskHealth", diskHealth);
		healthMap.put("systemHealth", systemHealth);
		return healthMap;
	}
	
	/**
	 * 物理资源利用率面板
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Map make_resUsedRate(IIdentityBean idBean, SQLExecutor executor) {
		Map rateMap = new HashMap<String,Double>();
		//获取计算节点下 所有的设备ID
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_COMPUTER);
		CArray<PrototypeValues> rateValues = CommonUtils.keysOfPrototypeValues(hosts, CommonUtils.KEYS_RATE);
		
		Double cpuUsedRate = rateValues.get("cpuUsedRate").avg().round(2).value().asDouble(); //CPU使用率
		
//		Double memoryRatedRate = rateValues.get("memoryRatedRate").avg().round(2).value().asDouble();//内存利用率
		ValuePrinter memUsedVP = rateValues.get("memoryUsedTotal").sum().round(2).out();
		ValuePrinter memTotalVP = rateValues.get("memoryTotal").sum().round(2).out();
		String memoryUsed = memUsedVP.format();
		String memoryTotal = memTotalVP.format();		 //内存总量
		Double memoryRatedRate = LatestValueHelper.NA.equals(memoryUsed)||LatestValueHelper.NA.equals(memoryTotal)?0.0:Cphp.round(Double.valueOf(Nest.as(memUsedVP.print()).asDouble()/Nest.as(memTotalVP.print()).asDouble())*100,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
		
//		Double diskUsedRate = rateValues.get("diskUsedRate").avg().round(2).value().asDouble();	//磁盘利用率
		ValuePrinter diskUsedVP = rateValues.get("diskUsedTotal").sum().round(2).out();
		ValuePrinter diskTotalVP = rateValues.get("diskTotal").sum().round(2).out();
		String diskUsed = diskUsedVP.format();	//磁盘使用率
		String diskTotal = diskTotalVP.format();		//磁盘总量
		Double diskUsedRate = LatestValueHelper.NA.equals(diskUsed)||LatestValueHelper.NA.equals(diskTotal)?0.0:Cphp.round(Double.valueOf(Nest.as(diskUsedVP.print()).asDouble()/Nest.as(diskTotalVP.print()).asDouble())*100,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
		
		//数据记录到系统中
		IMonConsts.CPU_RATE  = Double.valueOf(cpuUsedRate);
		IMonConsts.MEMORY_RATE  = memoryRatedRate;
		IMonConsts.DISK_RATE  = diskUsedRate;
		
		String memoryStr = "("+memoryUsed+"/"+memoryTotal+")";
		String diskStr = "("+diskUsed+"/"+diskTotal+")";
		
		rateMap.put("0", cpuUsedRate);
		rateMap.put("1", memoryRatedRate);
		rateMap.put("2", diskUsedRate);
		rateMap.put("memory", memoryStr);
		rateMap.put("disk", diskStr);
		
		return rateMap;
	}
	
	
	/**
	 * 资源使用趋势
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Map make_resource_use_trend(IIdentityBean idBean, SQLExecutor executor){
		Map dataMap = new HashMap();
		Long [] groups = {IMonConsts.MON_CLOUD_COMPUTER};
		Map cpuMap = getGroupTypeTrend(executor,ItemsKey.CPU_USER_RATE.getValue(),groups);
		Map memoryMap = getGroupTypeTrend(executor,new String[]{ItemsKey.MEMORY_USED_LINUX.getValue(),ItemsKey.TOTAL_MEMORY.getValue()},groups);
		Map diskMap = getGroupTypeTrend(executor,new String[]{ItemsKey.USED_DISK_SPACE_ON.getValue(),ItemsKey.TOTAL_DISK_SPACE_ON.getValue()},groups);

		dataMap.put("cpu", sortForMap(cpuMap,6,"MM-dd HH时"));
		dataMap.put("memory", sortForMap(memoryMap,6,"MM-dd HH时"));
		dataMap.put("disk", sortForMap(diskMap,6,"MM-dd HH时"));
		
		return dataMap;
	}
	
	/**
	 * 对Map进行排序并格式化时间
	 * @param map
	 * @return
	 */
	public static Map sortForMap(Map map){
		return sortForMap(map,24,"yyy-MM-dd");
	}
	
	public static Map sortForMap(Map map,int interval,String format){
		Long timeStart = Nest.value(map, "timeStart").asLong();
		Long timeEnd = Nest.value(map, "timeEnd").asLong();
		Map dataMap = !empty(Nest.value(map, "dataMap").$())?Nest.value(map, "dataMap").asCArray().toMap():map();
		Map<Long,String> result = new LinkedHashMap();
		List<Map.Entry<Long, String>> mappingList = new ArrayList<Map.Entry<Long,String>>(dataMap.entrySet());
		
		Collections.sort(mappingList, new Comparator<Map.Entry<Long,String>>(){ 
			public int compare(Map.Entry<Long,String> mapping1,Map.Entry<Long,String> mapping2){
				return mapping1.getKey().compareTo(mapping2.getKey()); 
			} 
		}); 
		
		for(Map.Entry<Long,String> mapping:mappingList){ 
			result.put(mapping.getKey(), mapping.getValue());
		}
		Long key = 0l;
		Map resultMap = new LinkedHashMap();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		for(;timeStart<=timeEnd;timeStart++){
			key = timeStart*3600*interval*1000;
			if(result.containsKey(timeStart))
				resultMap.put(sdf.format(key),result.get(timeStart));
			else
				resultMap.put(sdf.format(key),"0");
		}
		return resultMap;
	}
	
	/**
	 * 获取某个监控指标趋势图
	 * @param executor
	 * @param key
	 * @param groupids
	 * @return
	 */
	public static Map getGroupTypeTrend(SQLExecutor executor,String key,Long[] groupids){
		Map dataMap = new HashMap<String,String>();	
		Map params = new HashMap();
		String HISTORY_TIMESTAMP = "SELECT UNIX_TIMESTAMP()";
		Long timestamp = Nest.as(executor.executeNameParaQuery(HISTORY_TIMESTAMP, map(), Long.class).get(0)).asLong();
		Long timeStart = (timestamp-Defines.SEC_PER_WEEK)/(60 * 60 * 6);
		Long timeEnd = timestamp/(60 * 60 * 6);
		CArray items = getItemsIDs(executor,key,groupids);
		if(Cphp.empty(items)){
			return map("timeStart",timeStart,
					   "timeEnd",timeEnd,
					   "dataMap",dataMap); 
		}
		Integer value_type = getValueType(executor, Nest.value(items, 0).asLong());
		
		String HISTORY_UINT_SQL = getTrendSql(timestamp,value_type);
		if(Cphp.empty(HISTORY_UINT_SQL))	
			return map("timeStart",timeStart,
					   "timeEnd",timeEnd,
					   "dataMap",dataMap);
		
		params = new HashMap();
		params.put("itemid",items.toArray());
		CArray<Map> hostcarray = DBselect(executor, HISTORY_UINT_SQL,params);
		
		String _value = "";
		String _time = "";
		for(Map hostdata:hostcarray){
			_time = hostdata.get("t").toString();
			_value = Nest.as(Cphp.round(Nest.as(hostdata.get("v")).asDouble(),Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)).asString();
			dataMap.put(_time, _value);
		}
		return map("timeStart",timeStart,
				   "timeEnd",timeEnd,
				   "dataMap",dataMap);
	}
	
	public static Map getGroupTypeTrend(SQLExecutor executor,String[] keys,Long[] groupids){
		Map dataMap = new HashMap<String,String>();	
		Map params = new HashMap();
		String HISTORY_TIMESTAMP = "SELECT UNIX_TIMESTAMP()";
		Long timestamp = Nest.as(executor.executeNameParaQuery(HISTORY_TIMESTAMP, map(), Long.class).get(0)).asLong();
		Long timeStart = (timestamp-Defines.SEC_PER_WEEK)/(60 * 60 * 6);
		Long timeEnd = timestamp/(60 * 60 * 6);
		CArray itemsUsed = getItemsIDs(executor,keys[0],groupids);
		CArray itemsTotal = getItemsIDs(executor,keys[1],groupids);
		if(Cphp.empty(itemsUsed)||Cphp.empty(itemsTotal)){
			return map("timeStart",timeStart,
					   "timeEnd",timeEnd,
					   "dataMap",dataMap); 
		}
		Integer value_type_used = getValueType(executor, Nest.value(itemsUsed, 0).asLong());
		Integer value_type_total = getValueType(executor, Nest.value(itemsTotal, 0).asLong());
		
		String HISTORY_USED_UINT_SQL = getTrendSql(timestamp,value_type_used);
		String HISTORY_TOTAL_UINT_SQL = getTrendSql(timestamp,value_type_total);
		
		if(Cphp.empty(HISTORY_USED_UINT_SQL)||Cphp.empty(HISTORY_TOTAL_UINT_SQL))
			return map("timeStart",timeStart,
					   "timeEnd",timeEnd,
					   "dataMap",dataMap);
		params = new HashMap();
		params.put("itemid",itemsUsed.toArray());
		CArray<Map> usedCArray = DBselect(executor, HISTORY_USED_UINT_SQL,params);
		params.put("itemid",itemsTotal.toArray());
		CArray<Map> totalCArray = DBselect(executor, HISTORY_TOTAL_UINT_SQL,params);
		CArray totalHash = FuncsUtil.rda_toHash(totalCArray, "t");
		
		String _value = "";
		String _time = "";
		for(Map hostdata:usedCArray){
			_time = hostdata.get("t").toString();
			Double percent = 0.0;
			if(isset(totalHash,_time))
				percent = (Nest.as(hostdata.get("v")).asDouble()/Nest.value(totalHash, _time,"v").asDouble())*100;
			_value = Nest.as(Cphp.round(percent,Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)).asString();
			dataMap.put(_time, _value);
		}
		return map("timeStart",timeStart,
				   "timeEnd",timeEnd,
				   "dataMap",dataMap);
	}
	
	public static String getTrendSql(Long timestamp,Integer value_type){
		String HISTORY_UINT_SQL = "";
		if(Defines.ITEM_VALUE_TYPE_UINT64 == value_type){	//根据items表中的valueType判断所用键值是否是int类型（根据Hostid和key能确定唯一一个itemid）
			HISTORY_UINT_SQL = " SELECT SUM(avg_) v, time_ t " +
							   " FROM (SELECT AVG(h.value) avg_,FLOOR(h.clock / (60 * 60 * 6)) time_ "+
							   " 	   FROM history_uint h " +
							   "	   WHERE h.itemid IN (#{itemid}) " +
							   "       AND h.clock>("+(timestamp-Defines.SEC_PER_WEEK)+") GROUP BY time_) tab " +
							   " GROUP BY time_";
		}else if(Defines.ITEM_VALUE_TYPE_FLOAT == value_type){
			HISTORY_UINT_SQL = " SELECT SUM(avg_) v, time_ t " +
							   " FROM (SELECT AVG(h.value) avg_,FLOOR(h.clock / (60 * 60 * 6)) time_ "+
							   " 	   FROM history h " +
							   "	   WHERE h.itemid IN (#{itemid}) " +
							   " 	   AND h.clock>("+(timestamp-Defines.SEC_PER_WEEK)+") GROUP BY time_) tab " +
							   " GROUP BY time_";
		}
		return HISTORY_UINT_SQL;
	}
	
	/**
	 * 转换时间格式
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String formatDate(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(Long.valueOf(time)*3600*24*1000);
		return sdf.format(date);
	}
	
	/**
	 * 根据键值和设备分组获取对应的itemdid集合
	 * @param idBean
	 * @param executor
	 * @param key
	 * @param groupids
	 * @return
	 */
	private static CArray getItemsIDs(SQLExecutor executor,String key,Long[] groupids){
		Map params = new HashMap();
		params.put("groupid",groupids);
		params.put("key",key);
		String ITEMS_SQL;
		CArray itemsids = array();
		
		if(key.contains(CommonUtils.PROTOTYPE_KEY_MODEL)){ //prototype
			params.put("prototypeKey",key);
			ITEMS_SQL = "" +
					"select itemid,value_type,units,valuemapid,name from items where itemid in (" + 
					"  select itemid from item_discovery where parent_itemid in (" + 
					"    select itemid from items " +
					"		where " +
					"			hostid in(SELECT hg.hostid FROM hosts_groups hg WHERE hg.groupid in(#{groupid})) " +
					"		and key_ = #{prototypeKey}" + 
					"		and flags = " + Defines.RDA_FLAG_DISCOVERY_PROTOTYPE +
					"  )" + 
					" and flags = " + Defines.RDA_FLAG_DISCOVERY_CREATED +
					")";
			
		}else{//noraml
			ITEMS_SQL = "SELECT i.itemid FROM items i WHERE i.hostid IN "
					+ "(SELECT hg.hostid FROM hosts_groups hg WHERE hg.groupid in(#{groupid})) and i.key_=#{key}";
		}

		CArray<Map> itemsCarray = DBselect(executor, ITEMS_SQL,params);
		for(Map map:itemsCarray){
			itemsids.add(map.get("itemid"));
		}
		
		return itemsids;
	}
	
	/**
	 * 获取itemid的valueType
	 * @param executor
	 * @param itemid
	 * @return
	 */
	private static Integer getValueType(SQLExecutor executor,Long itemid){
		Map params = new HashMap();
		String ITEMS_SQL = "SELECT i.value_type FROM items i WHERE i.itemid=#{itemdid}";
		params.put("itemdid",itemid);
		CArray<Map> itemsCarray = DBselect(executor, ITEMS_SQL,params);
		return Nest.value(itemsCarray, 0, "value_type").asInteger(true);
	}
	
	
	/**
	 * 云服务健康状态面板
	 * @param idBean
	 * @param executor
	 * @param _filter
	 * @return
	 */
	public static Map make_cloudSerStat(IIdentityBean idBean, SQLExecutor executor) {
		Map map = new HashMap<String,Map>();
		CArray<Map> controlerMap  = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_CONTROLER);	//控制节点ID和name集合
		CArray<Map> computerMap  = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_COMPUTER);		//计算节点ID和name集合
		CArray<Map> cephMap  = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_CEPH);				//存储节点ID和name集合
		CArray<Map> networkMap  = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_NETWORK);		//网络节点ID和name集合
		CArray<Map> webMap  = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_WEB);				//门户节点ID和name集合
		
		Map<String,Integer> result = getHealthNumForCloud(controlerMap,controller);
		Map normalMap = new HashMap();
		Map errorMap = new HashMap();
		
		//计算节点
		result = getHealthNumForCloud(computerMap,compute);
		normalMap.put("1", result.get("normalNum"));
		errorMap.put("1", result.get("errorNum"));
		
		//存储节点
		result = getHealthNumForCloud(cephMap,storage);
		normalMap.put("2", result.get("normalNum"));
		errorMap.put("2", result.get("errorNum"));
		
		//网络节点
		result = getHealthNumForCloud(networkMap,network);
		normalMap.put("3", result.get("normalNum"));
		errorMap.put("3", result.get("errorNum"));
		
		//控制节点
		result = getHealthNumForCloud(controlerMap,controller);
		normalMap.put("4", result.get("normalNum"));
		errorMap.put("4", result.get("errorNum"));

		//门户节点
		result = getHealthNumForCloud(webMap,web);
		normalMap.put("5", result.get("normalNum"));
		errorMap.put("5", result.get("errorNum"));
		
		map.put("normal", normalMap);
		map.put("error", errorMap);
		return map;	
	}
	
	/**
	 *  计算出云服务节点的健康设备数量
	 * @param controlerMap
	 * @return
	 */
	private static Map<String,Integer> getHealthNumForCloud(CArray<Map> controlerMap,String flag){
		Map result = new HashMap<String, Integer>();
		int normalNum = 0;	//正常的设备数
		int errorNum = 0;	//异常的设备数
		Matcher mUnavailable = new Matcher(){
			@Override public boolean match(Object o) {
				return "0".equals(String.valueOf(o));
			}
		};
		Matcher mAvailable = new Matcher(){
			@Override public boolean match(Object o) {
				return EasyObject.asInteger(o) > 0;
			}
		};
		LatestValueHelper ceilometer = null;
		
		LatestValueHelper controllerAssist = null;	//辅助进行控制节点计算（CLOUD_SPICEHTML5PROXY，CLOUD_NOVNCPROXY）二选一
		for(Map map:controlerMap){
			Long hostid = Long.valueOf(map.get("hostid").toString());
			//循环当前设备下的所有服务 如果有一个服务是异常，则改设备为异常。
			if(controller.equals(flag)){	//控制节点
				ceilometer = LatestValueHelper.buildByNormalKey(hostid, 
					 ItemsKey.CLOUD_OPENSTACK_KEYSTONE,
					 ItemsKey.CLOUD_MEMCACHED,
					 ItemsKey.CLOUD_MYSQLD,
					 ItemsKey.CLOUD_OPENSTACK_GLANCE,
					 ItemsKey.CLOUD_OPENSTACK_GLANCE_REGISTRY,
					 ItemsKey.CLOUD_NOVA_API,
					 ItemsKey.CLOUD_NOVA_CERT,
					 ItemsKey.CLOUD_CONSOLEAUTH,
					 ItemsKey.CLOUD_SCHEDULER,
					 ItemsKey.CLOUD_CONDUCTOR,
					 ItemsKey.CLOUD_SERVER,
					 ItemsKey.CLOUD_CINDER_API,
					 ItemsKey.CLOUD_CINDER_SCHEDULER,
//					 ItemsKey.CLOUD_HEAT_API,
//					 ItemsKey.CLOUD_API_CFN,
//					 ItemsKey.CLOUD_ENGINE,
					 ItemsKey.CLOUD_CEILOMETER,
					 ItemsKey.CLOUD_NOTIFICATION,
					 ItemsKey.CLOUD_CENTRAL,
					 ItemsKey.CLOUD_COLLECTOR,
					 ItemsKey.CLOUD_EVALUATOR,
					 ItemsKey.CLOUD_NOTIFIER,
					 ItemsKey.CLOUD_HTTPD
//					 ItemsKey.CLOUD_SPS
				);
				controllerAssist = LatestValueHelper.buildByNormalKey(hostid,
						 ItemsKey.CLOUD_SPICEHTML5PROXY,
						 ItemsKey.CLOUD_NOVNCPROXY
						);
				 
			}else if(compute.equals(flag)){ //计算节点
				ceilometer = LatestValueHelper.buildByNormalKey(hostid, 
						 ItemsKey.CLOUD_LIBVIRTD,
						 ItemsKey.CLOUD_MESSAGEBUS,
						 ItemsKey.CLOUD_NOVA_COMPUTE,
						 ItemsKey.CLOUD_OPENVSWITCH_AGENT,
						 ItemsKey.CLOUD_OPENVSWITCH,
						 ItemsKey.CLOUD_VOLUME,
						 ItemsKey.CLOUD_TGTD,
						 ItemsKey.CLOUD_CEILOMETER_COMPUTE
//						 ItemsKey.CLOUD_SPSAGENT
						
					);
			}else if(network.equals(flag)){	//网络节点
				ceilometer = LatestValueHelper.buildByNormalKey(hostid,
						ItemsKey.CLOUD_NET_OPENVSWITCH,
						ItemsKey.CLOUD_NET_AGENT,
						ItemsKey.CLOUD_NET_L3_AGENT,
						ItemsKey.CLOUD_NET_DHCP,
						ItemsKey.CLOUD_NET_METADATA,
//						ItemsKey.CLOUD_NET_VPN,
						ItemsKey.CLOUD_NET_LBAAS,
						ItemsKey.CLOUD_NET_IPSEC
				);
			}else if(storage.equals(flag)){	//存储节点
				ceilometer = LatestValueHelper.buildByNormalKey(hostid,
						ItemsKey.CLOUD__CEPH_MON,
						ItemsKey.CLOUD_CEPH_OSD
				);
			}else if(web.equals(flag)){ //门户节点
				ceilometer = LatestValueHelper.buildByNormalKey(hostid,
						ItemsKey.CLOUD_WEB_HTTPD
				);
			}

			if(controller.equals(flag)){//控制节点特殊判断
				if(ceilometer.values().count(mUnavailable).value().asInteger() > 0 || controllerAssist.values().count(mAvailable).value().asInteger() <= 0){
					errorNum++;
				}else{
					normalNum++;
				}
			}else{				
				if(ceilometer.values().count(mUnavailable).value().asInteger() > 0){
					errorNum ++;
				}else{
					normalNum++;
				}
			}
		}
		result.put("normalNum", normalNum);
		result.put("errorNum", errorNum);
		return result;
	}
	
	/**
	 * 组装云服务健康面板数据
	 * @param cloudName
	 * @param url
	 * @param normalNum
	 * @param errorNum
	 * @param img
	 * @return
	 */
	@SuppressWarnings("unused")
	private static CArray getCloudSerStat(String cloudName,String url,int normalNum,int errorNum,CImg img){
		CLink namelink = new CLink();
		CLink numlink = new CLink();
		
		namelink.setTarget("_Blank");
		numlink.setTarget("_Blank");
		
		namelink.addItem(cloudName);
		namelink.setUrl(url);
		numlink.addItem(normalNum+"/"+errorNum);
		numlink.setUrl(url);
		
		return array(namelink,img,numlink);
	}
	
	/**
	 * 获取云服务设备类型中的一台设备
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Long getHostIdByMonCloud(IIdentityBean idBean, SQLExecutor executor){
		//获取云服务分组下的设备ID
		CHostGet cHostGet = new CHostGet();
		cHostGet.setOutput(new String[]{"hostid"});
		cHostGet.setGroupIds(IMonConsts.MON_CLOUD_CONTROLER.longValue());
		cHostGet.setPreserveKeys(true);		
		CArray<Map> _hosts = API.Host(idBean, executor).get(cHostGet);	
		return Nest.value(Cphp.reset(_hosts), "hostid").asLong();
	}
	
	/**
	 * 新的tonN面板-CPU负载面板
	 * @param idBean
	 * @param executor
	 * @param _filter
	 * @return
	 */
	public static Map make_topN(IIdentityBean idBean, SQLExecutor executor) {
		Map tonNMap = new HashMap<String,List>();
		TopNHelper helper = new TopNHelper(5, "value");
		//获取服务器分组下的所有设备ID
		CArray<Map> hosts = CommonUtils.queryMonServerHostIDAndName(IMonConsts.MON_CLOUD_COMPUTER);
		for(Entry<Object, Map> en: hosts.entrySet()){
			Long hostid = (Long)en.getKey();
			String hostName = Nest.value(en.getValue(), "name").asString();
			long groupId = Nest.value(en.getValue(),"groupid").asLong();
			
			ItemsKey key = null;
			if(IMonConsts.MON_CLOUD_COMPUTER == groupId){//云计算节点负载
				key = ItemsKey.CPU_LOAD;
			}else if(IMonConsts.MON_SERVER_LINUX == groupId){//linux服务器负载
				key = ItemsKey.CPU_LOAD;
			}else if(IMonConsts.MON_SERVER_WINDOWS == groupId){	//windows服务器负载
				key = ItemsKey.CPU_LOAD_WINDOWS;
			}
			
			if(key != null) {
				Double cpuLoad = LatestValueHelper.buildByPrototypeKey(hostid, key.getValue()).values().avg().round(2).value().asDouble();
				helper.put(EasyMap.build("name", hostName, "value", cpuLoad));
			}
		}
		
		Map cpuLoadMap = EasyMap.build();
		for(Map m: helper.getResult()) {
			cpuLoadMap.put(m.get("name"), m.get("value"));
		}
		
		Map cpuInfoMap = cpuLoadMap;

		if(isset(cpuInfoMap)&&cpuInfoMap.size()>0){
			//int length = cpuInfoMap.size();
			String cpuKey = null;
			String cpuValue = null;
			//int i=1;
			//获取服务器分组下CPU负载最高的前五项
			for(Object key :cpuInfoMap.keySet()){
				cpuKey = Nest.as(key).asString();
				cpuValue = Nest.as(cpuInfoMap.get(key)).asString();
				tonNMap.put(cpuKey,cpuValue);
			}
		}
		return tonNMap;
	}
	
	/**
	 * 平台信息
	 * @param idBean
	 * @param executor
	 * @return
	 */
	public static Map make_favorite_platformInfo(final IIdentityBean idBean, final SQLExecutor executor) {
//		float[] ratios = DataDriver.osConfigInfos();
		Map map = new HashMap<String,Integer>();
		//服务器
//		int hostServerNum = get_host_num_by_group(idBean,executor,new Long[]{IMonConsts.MON_SERVER_LINUX,IMonConsts.MON_SERVER_WINDOWS});
		int hostServerNum = get_host_num_by_group(idBean,executor,MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_SERVER).valuesAsLong());
		map.put("hostServerCount", Nest.as(hostServerNum).asString().concat(_("unit set")));
		
		//交换机
//		int hostSwitchNum = get_host_num_by_group(idBean,executor,new Long[]{IMonConsts.MON_NET_CISCO,IMonConsts.MON_NET_HUAWEI_SWITCH,IMonConsts.MON_NET_ZHONGXING_SWITCH,IMonConsts.MON_COMMON_NET});
		int hostSwitchNum = get_host_num_by_group(idBean,executor,MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_NET_DEV).valuesAsLong());
		map.put("hostSwitchCount", Nest.as(hostSwitchNum).asString().concat(_("unit set")));
		
		//存储设备
//		int hostStorageNum = get_host_num_by_group(idBean,executor,new Long[]{IMonConsts.MON_STORAGE});
		int hostStorageNum = get_host_num_by_group(idBean,executor,MoncategoryUtil.getGroupIdsByPId(executor, IMonConsts.MON_CATE_STORAGE).valuesAsLong());
		map.put("hostStorageNum", Nest.as(hostStorageNum).asString().concat(_("unit set")));
		
		Long iaasCtrlHostId = getHostIdByMonCloud(idBean,executor);
		//虚拟内核数
		int coreTotalNum = LatestValueHelper.buildByNormalKey(iaasCtrlHostId, ItemsKey.VM_CORE_TOTAL).value().value().asInteger();
//		int coreUsedNum = LatestValueHelper.buildByNormalKey(iaasCtrlHostId, ItemsKey.VM_CORE_USED).value().value().asInteger();
//		map.put("coreUsedCount", coreUsedNum+"/"+coreTotalNum+(empty(ratios)?"0":"("+coreTotalNum/ratios[0]+")"));
		map.put("coreUsedCount", Nest.as(coreTotalNum).asString().concat(_("unit pcs")));

		//可分配内存
		String memTotalNum = LatestValueHelper.buildByNormalKey(iaasCtrlHostId, ItemsKey.VM_MEMORY_TOTAL).value().out().format();
//		int memUsedNum = LatestValueHelper.buildByNormalKey(iaasCtrlHostId, ItemsKey.VM_MEMORY_USED).value().convertUnit(NormalValue.POW_M).value().asInteger();
//		int memUnuseNum = memTotalNum - memUsedNum;
//		map.put("memoryCount", memUnuseNum+"/"+(empty(ratios)?memTotalNum:Cphp.round(memTotalNum/ratios[1],Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
//		map.put("memoryCount", memUnuseNum+"/"+memTotalNum+(empty(ratios)?"0":"("+Cphp.round(memTotalNum/ratios[1],Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT)+")"));
		map.put("memoryCount", memTotalNum);
		
		//存储容量
		String cinderTotalNum = LatestValueHelper.buildByNormalKey(iaasCtrlHostId, ItemsKey.VM_CINDER_TOTAL).value().out().format();
		map.put("cinderCount", cinderTotalNum);
		
		//租户
		CArray<Map> users = com.isoft.iradar.data.DataDriver.getAllTenants();
		map.put("userCount", Nest.as(users.size()).asString().concat(_("unit pcs")));
		
		//云主机
		map.put("machineCount", Nest.as(get_host_num_by_group(idBean,executor,new Long[]{IMonConsts.MON_VM})).asString().concat(_("unit pcs")));
		
		//镜像
		int imagesNum = com.isoft.iradar.data.DataDriver.getAllImages().size();
		map.put("imagesCount", Nest.as(imagesNum).asString().concat(_("unit pcs")));
		
		//计算节点
//		final CHostGet operonse = new CHostGet();
//		Long[] groupids = {IMonConsts.MON_CLOUD_COMPUTER};
//		operonse.setGroupIds(groupids);
//		operonse.setOutput(API_OUTPUT_COUNT);
//		CArray<Map> physicss = API.Call(new Wrapper<CArray>() {
//			@Override
//			protected CArray doCall() throws Throwable {
//				return API.Host(idBean, executor).get(operonse);
//			}
//		}, array());
//		map.put("physicsCount", physicss.size());
		return map;
	}
	
	public static int get_host_num_by_group(final IIdentityBean idBean, final SQLExecutor executor,Long[] groupids){
		final CHostGet operonse = new CHostGet();
		operonse.setGroupIds(groupids);
		operonse.setOutput(API_OUTPUT_COUNT);
		CArray<Map> hostsNum = API.Call(new Wrapper<CArray>() {
			@Override
			protected CArray doCall() throws Throwable {
				return API.Host(idBean, executor).get(operonse);
			}
		}, array());
		return !empty(hostsNum)&&hostsNum.size()>0?hostsNum.size():0;
	}
	
}
