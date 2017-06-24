package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp.$_REQUEST;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.urlencode;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_COUNT;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_FALSE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_TRUE;
import static com.isoft.iradar.inc.Defines.HOST_AVAILABLE_UNKNOWN;
import static com.isoft.iradar.inc.Defines.HOST_MAINTENANCE_STATUS_ON;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.ArrayUtils;

import com.isoft.Feature;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.api.API;
import com.isoft.iradar.helpers.CHtml;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.model.params.CDiscoveryRuleGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.model.params.CTemplateScreenGet;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CIcon;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.Curl;
import com.isoft.iradar.web.views.CViewPageFooter;
import com.isoft.iradar.web.views.CViewPageHeader;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HtmlUtil {

	public static CArray italic(CArray strs) {
		CArray rets = map();
		if (isArray(strs)) {
			for(Entry<Object, Object> entry: ((CArray<Object>)strs).entrySet()) {
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (is_string(val)) {
					CTag em = new CTag("em", "yes");
					em.addItem(val);
					rets.put(key, em);
				}
			}
		}
		return rets;
	}
	
	public static CTag italic(String str) {
		if (is_string(str)) {
			CTag em = new CTag("em", "yes", "");
			em.addItem(str);
			return em;
		}
		return null;
	}
	
	public static CArray bold(CArray strs) {
		CArray rets = map();
		if (isArray(strs)) {
			for(Entry<Object, Object> entry: ((CArray<Object>)strs).entrySet()) {
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (is_string(val)) {
					CTag b = new CTag("strong", "yes");
					b.addItem(val);
					rets.put(key, b);
				}
			}
		}
		return rets;
	}
	
	public static CTag bold(String str) {
		if (is_string(str)) {
			CTag b = new CTag("strong", "yes", "");
			b.addItem(str);
			return b;
		}
		return null;
	}
	
	public static Object make_decoration(String haystack, String needle) {
		return make_decoration(haystack, needle, null);
	}
	
	public static Object make_decoration(String haystack, String needle, String classStyle) {
		Object result = haystack;
		int pos = haystack.indexOf(needle);
		if (pos != -1) {
			String start = CHtml.encode(haystack.substring(0, pos));
			String end = CHtml.encode(haystack.substring(pos + rda_strlen(needle)));
			String found = CHtml.encode(haystack.substring(pos,pos + rda_strlen(needle)));
			if (is_null(classStyle)) {
				result = array(start, bold(found), end);
			} else {
				result = array(start, new CSpan(found, classStyle), end);
			}
		}
		return result;
	}

	/**
	 * Create CDiv with host/template information and references to it's elements
	 *
	 * @param string currentElement
	 * @param int hostid
	 *
	 * @return object
	 */
	public static CDiv get_header_host_table(IIdentityBean idBean, SQLExecutor executor, String currentElement, Long hostid){
		return get_header_host_table(idBean, executor, currentElement, hostid, null);
	}
	
	/**
	 * Create CDiv with host/template information and references to it's elements
	 *
	 * @param string currentElement
	 * @param int hostid
	 * @param int discoveryid
	 *
	 * @return object
	 */
	public static CDiv get_header_host_table(IIdentityBean idBean, SQLExecutor executor, String currentElement, Long hostid, Long discoveryid) {
		Map elements = null;
		// LLD rule header
		if (!empty(discoveryid)) {
			elements = map(
				"items", "items",
				"triggers", "triggers",
				"graphs", "graphs",
				"hosts", "hosts"
			);
		}
		// host header
		else {
			elements = map(
				"items", "items",
				"triggers", "triggers",
				"graphs", "graphs",
				"applications", "applications",
				"screens", "screens",
				"discoveries", "discoveries",
				"web", "web"
			);
		}
		
		CHostGet options = new CHostGet();
		options.setHostIds(hostid);
		options.setOutput(API_OUTPUT_EXTEND);
		options.setTemplatedHosts(true);
		options.setSelectHostDiscovery(new String[] { "ts_delete" });
		
		if (isset(elements,"items")) {
			options.setSelectItems(API_OUTPUT_COUNT);
		}
		if (isset(elements,"triggers")) {
			options.setSelectTriggers(API_OUTPUT_COUNT);
		}
		if (isset(elements,"graphs")) {
			options.setSelectGraphs(API_OUTPUT_COUNT);
		}
		if (isset(elements,"applications")) {
			options.setSelectApplications(API_OUTPUT_COUNT);
		}
		if (isset(elements,"discoveries")) {
			options.setSelectDiscoveries(API_OUTPUT_COUNT);
		}
		if (isset(elements,"web")) {
			options.setSelectHttpTests(API_OUTPUT_COUNT);
		}
		
		// get hosts
		CArray<Map> dbHosts = API.Host(idBean, executor).get(options);
		Map dbHost = reset(dbHosts);
		if (empty(dbHost)) {
			return null;
		}
		
		// get discoveries
		Map dbDiscovery = null;
		if (!empty(discoveryid)) {
			CDiscoveryRuleGet droptions = new CDiscoveryRuleGet();
			droptions.setOutput(new String[]{"name"});
			droptions.setItemIds(discoveryid);
			
			if (isset(elements,"items")) {
				droptions.setSelectItems(API_OUTPUT_COUNT);
			}
			if (isset(elements,"triggers")) {
				droptions.setSelectTriggers(API_OUTPUT_COUNT);
			}
			if (isset(elements,"graphs")) {
				droptions.setSelectGraphs(API_OUTPUT_COUNT);
			}
			if (isset(elements,"hosts")) {
				droptions.setSelectHostPrototypes(API_OUTPUT_COUNT);
			}

			CArray<Map> dbDiscoverys = API.DiscoveryRule(idBean, executor).get(droptions);
			dbDiscovery = reset(dbDiscoverys);
		}
		
		/* Back */
		CList list = new CList(null, "objectlist");
		if (Nest.value(dbHost,"status").asInteger() == HOST_STATUS_TEMPLATE) {
			list.addItem(array("&laquo; ", new CLink(_("Template list"), "templates.action?templateid="+Nest.value(dbHost,"hostid").asString()+url_param(idBean, "groupid"))));

			CTemplateScreenGet tsoptions = new CTemplateScreenGet();
			tsoptions.setEditable(true);
			tsoptions.setCountOutput(true);
			tsoptions.setGroupCount(true);
			tsoptions.setTemplateIds(Nest.value(dbHost, "hostid").asLong());
			CArray<Map> screens = API.TemplateScreen(idBean, executor).get(tsoptions);
			Nest.value(dbHost,"screens").$(isset(Nest.value(screens,0,"rowscount").$()) ? Nest.value(screens,0,"rowscount").asLong() : 0L);
		} else {
			list.addItem(array("&laquo; ", new CLink(_("Host list"), "hosts.action?hostid="+Nest.value(dbHost,"hostid").asString()+url_param(idBean, "groupid"))));
		}
		
		/* Name */
		String proxyName = "";
		if (!empty(Nest.value(dbHost,"proxy_hostid").$())) {
			Map proxy = get_host_by_hostid(idBean, executor, Nest.value(dbHost,"proxy_hostid").asLong());
			proxyName = CHtml.encode(Nest.value(proxy,"host").asString())+NAME_DELIMITER;
		}

		String name = proxyName+CHtml.encode(Nest.value(dbHost,"name").asString());

		if (Nest.value(dbHost,"status").asInteger() == HOST_STATUS_TEMPLATE) {
			list.addItem(array(bold(_("Template")+NAME_DELIMITER), new CLink(name, "templates.action?form=update&templateid="+Nest.value(dbHost,"hostid").asString())));
		} else {
			Object status = null;
			switch (Nest.value(dbHost,"status").asInteger()) {
				case HOST_STATUS_MONITORED:
					if (Nest.value(dbHost,"maintenance_status").asInteger() == HOST_MAINTENANCE_STATUS_ON) {
						status = new CSpan(_("In maintenance"), "orange");
					} else {
						status = new CSpan(_("Monitored"), "enabled");
					}
					break;
				case HOST_STATUS_NOT_MONITORED:
					status = new CSpan(_("Not monitored"), "on");
					break;
				default:
					status = _("Unknown");
					break;
			}

			list.addItem(array(bold(_("Host")+NAME_DELIMITER), new CLink(name, "hosts.action?form=update&hostid="+Nest.value(dbHost,"hostid").asString())));
			list.addItem(status);
			list.addItem(getAvailabilityTable(dbHost));
		}
		
		if (!empty(dbDiscovery)) {
			list.addItem(array("&laquo; ", new CLink(_("Discovery list"), "host_discovery.action?hostid="+Nest.value(dbHost,"hostid").asString()+url_param(idBean, "groupid"))));
			list.addItem(array(
				bold(_("Discovery")+NAME_DELIMITER),
				new CLink(CHtml.encode(Nest.value(dbDiscovery,"name").asString()), "host_discovery.action?form=update&itemid="+Nest.value(dbDiscovery,"itemid").asString())
			));
		}
		
		/* Rowcount */
		if (isset(elements,"applications")) {
			if ("applications".equals(currentElement)) {
				list.addItem(_("Applications")+" ("+Nest.value(dbHost,"applications").asLong()+")");
			} else {
				list.addItem(array(
					new CLink(_("Applications"), "applications.action?hostid="+Nest.value(dbHost,"hostid").asString()),
					" ("+Nest.value(dbHost,"applications").asLong()+")"
				));
			}
		}
		
		if (isset(elements,"items")) {
			if (!empty(dbDiscovery)) {
				if ("items".equals(currentElement)) {
					list.addItem(_("Item prototypes")+" ("+Nest.value(dbDiscovery,"items").asLong()+")");
				} else {
					list.addItem(array(
						new CLink(_("Item prototypes"), "disc_prototypes.action?hostid="+Nest.value(dbHost,"hostid").asString()+"&parent_discoveryid="+Nest.value(dbDiscovery,"itemid").asString()),
						" ("+Nest.value(dbDiscovery,"items").asLong()+")"
					));
				}
			} else {
				if ("items".equals(currentElement)) {
					list.addItem(_("Items")+" ("+Nest.value(dbHost,"items").asLong()+")");
				} else {
					list.addItem(array(
						new CLink(_("Items"), "items.action?filter_set=1&hostid="+Nest.value(dbHost,"hostid").asString()),
						" ("+Nest.value(dbHost,"items").$()+")"
					));
				}
			}
		}

		if (isset(elements,"triggers")) {
			if (!empty(dbDiscovery)) {
				if ("triggers".equals(currentElement)) {
					list.addItem(_("Trigger prototypes")+" ("+Nest.value(dbDiscovery,"triggers").asLong()+")");
				} else {
					list.addItem(array(
						new CLink(_("Trigger prototypes"), "trigger_prototypes.action?hostid="+Nest.value(dbHost,"hostid").asString()+"&parent_discoveryid="+Nest.value(dbDiscovery,"itemid").asString()),
						" ("+Nest.value(dbDiscovery,"triggers").asLong()+")"
					));
				}
			} else {
				if ("triggers".equals(currentElement)) {
					list.addItem(_("Triggers")+" ("+Nest.value(dbHost,"triggers").asLong()+")");
				} else {
					list.addItem(array(
						new CLink(_("Triggers"), "triggers.action?hostid="+Nest.value(dbHost,"hostid").asString()),
						" ("+Nest.value(dbHost,"triggers").asLong()+")"
					));
				}
			}
		}

		if (isset(elements,"graphs")) {
			if (!empty(dbDiscovery)) {
				if ("graphs".equals(currentElement)) {
					list.addItem(_("Graph prototypes")+" ("+Nest.value(dbDiscovery,"graphs").asLong()+")");
				} else {
					list.addItem(array(
						new CLink(_("Graph prototypes"), "graphs.action?hostid="+Nest.value(dbHost,"hostid").asString()+"&parent_discoveryid="+Nest.value(dbDiscovery,"itemid").asString()),
						" ("+Nest.value(dbDiscovery,"graphs").asLong()+")"
					));
				}
			} else {
				if ("graphs".equals(currentElement)) {
					list.addItem(_("Graphs")+" ("+Nest.value(dbHost,"graphs").asLong()+")");
				} else {
					list.addItem(array(
						new CLink(_("Graphs"), "graphs.action?hostid="+Nest.value(dbHost,"hostid").asString()),
						" ("+Nest.value(dbHost,"graphs").asLong()+")"
					));
				}
			}
		}

		if (isset(elements,"hosts") && Nest.value(dbHost,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) {
			if ("hosts".equals(currentElement)) {
				list.addItem(_("Host prototypes")+" ("+Nest.value(dbDiscovery,"hostPrototypes").asLong()+")");
			} else {
				list.addItem(array(
					new CLink(_("Host prototypes"), "host_prototypes.action?parent_discoveryid="+Nest.value(dbDiscovery,"itemid").asString()),
					" ("+Nest.value(dbDiscovery,"hostPrototypes").asLong()+")"
				));
			}
		}

		if (isset(elements,"screens") && Nest.value(dbHost,"status").asInteger() == HOST_STATUS_TEMPLATE) {
			if ("screens".equals(currentElement)) {
				list.addItem(_("Screens")+" ("+Nest.value(dbHost,"screens").asLong()+")");
			} else {
				list.addItem(array(
					new CLink(_("Screens"), "screenconf.action?templateid="+Nest.value(dbHost,"hostid").asString()),
					" ("+Nest.value(dbHost,"screens").asLong()+")"
				));
			}
		}

		if (isset(elements,"discoveries")) {
			if ("discoveries".equals(currentElement)) {
				list.addItem(_("Discovery rules")+" ("+Nest.value(dbHost,"discoveries").asLong()+")");
			} else {
				list.addItem(array(
					new CLink(_("Discovery rules"), "host_discovery.action?hostid="+Nest.value(dbHost,"hostid").asString()),
					" ("+Nest.value(dbHost,"discoveries").asLong()+")"
				));
			}
		}

		if (isset(elements,"web")) {
			if ("web".equals(currentElement)) {
				list.addItem(_("Web scenarios")+" ("+Nest.value(dbHost,"httpTests").asLong()+")");
			} else {
				list.addItem(array(
					new CLink(_("Web scenarios"), "httpconf.action?hostid="+Nest.value(dbHost,"hostid").asString()),
					" ("+Nest.value(dbHost,"httpTests").asLong()+")"
				));
			}
		}

		return new CDiv(list, "objectgroup top ui-widget-content ui-corner-all");
	}
	
	public static CTable get_table_header(Object columnLeft) {
		return get_table_header(columnLeft, Defines.SPACE);
	}
	public static CTable get_table_header(Object columnLeft, Object columnRights) {
		List<CDiv> rights = new ArrayList<CDiv>();
		if (columnRights != null) {
			if (!columnRights.getClass().isArray()) {
				columnRights = CArray.valueOf(columnRights).toArray();
			}
			
			int len = Array.getLength(columnRights);
			for (int i = 0; i < len; i++) {
				Object columnRight = Array.get(columnRights, i);
				rights.add(new CDiv(columnRight, "floatright"));
			}
		}
		
		CTable table = new CTable(null, "ui-widget-header ui-corner-all header maxwidth");
		table.setCellSpacing(0);
		table.setCellPadding(1);
		CDiv[] rdivs = rights.toArray(new CDiv[0]);
		ArrayUtils.reverse(rdivs);
		table.addRow(new CCol[]{new CCol(columnLeft, "header_l left"),
				new CCol(rdivs, "header_r right")});
		return table;
	}
	
	public static void show_table_header(Object columnLeft){
		show_table_header(columnLeft, Defines.SPACE);
	}
	
	public static void show_table_header(Object columnLeft, Object columnRights){
		CTable _table = get_table_header(columnLeft, columnRights);
		_table.show();
	}

	public static String nbsp(String str) {
		return str.replaceAll(" ", Defines.SPACE);
	}
	
	public static CTag BR() {
		return new CTag("br", "no");
	}
	
	public static CDiv makeFormFooter() {
		return makeFormFooter(null);
	}
	public static CDiv makeFormFooter(CButton _main) {
		return makeFormFooter(_main, null);
	}
	public static CDiv makeFormFooter(CButton _main, Object _others) {
		if (_main != null) {
			_main.useJQueryStyle("main");
		}
	
		CDiv _othersButtons = new CDiv(_others);
		_othersButtons.useJQueryStyle();
	
		return new CDiv(
			new CDiv(
				new CDiv(
					array(
						new CDiv(_main, "dt right"),
						new CDiv(_othersButtons, "dd left")
					),
					"formrow"
				),
				"formtable"
			),
			"objectgroup footer ui-widget-content ui-corner-all"
		);
	}
	
	public static void fatal_error(IIdentityBean idBean, String _msg) {
		CViewPageHeader.renderAndShow(idBean);
		FuncsUtil.show_error_message(_msg);
		CViewPageFooter.renderAndShow(idBean);
	}
	
	/**
	 * Get ready for url params.
	 *
	 * @param mixed  _param				param name or array with data depend from _getFromRequest
	 * @param bool   _getFromRequest	detect data source - input array or _REQUEST variable
	 * @param string _name				if _REQUEST variable is used this variable not used
	 *
	 * @return string
	 */
	public static String url_param(IIdentityBean idBean, Object _param, boolean _getFromRequest, String _name) {
		if (Cphp.isArray(_param)) {
			if (_getFromRequest) {
				fatal_error(idBean, Cphp._("URL parameter cannot be array."));
			}
		}
		else {
			if (Cphp.is_null(_name)) {
				if (!_getFromRequest) {
					fatal_error(idBean, Cphp._("URL parameter name is empty."));
				}
				_name = String.valueOf(_param);
			}
		}
	
		Object _value;
		if (_getFromRequest) {
			_value = RadarContext._REQUEST(String.valueOf(_param));
		} else {
			_value = _param;
		}
	
		return Cphp.isset(_value) ? prepareUrlParam(_value, _name) : "";
	}
	
	public static String url_param(IIdentityBean idBean, Object _param, boolean _getFromRequest) {
		return url_param(idBean, _param, _getFromRequest, null);
	}
	
	public static String url_param(IIdentityBean idBean, Object _param) {
		return url_param(idBean, _param, true);
	}

	public static String url_params(IIdentityBean idBean, CArray _params) {
		String _result = "";
	
		for(Object _param: _params) {
			_result += url_param(idBean, _param);
		}
	
		return _result;
	}

	
	public static String prepareUrlParam(Object _value, String _name) {
		String _result;
		if (Cphp.isArray(_value)) {
			_result = "";

			for(Entry<Object, Object> e: ((Map<Object,Object>)_value).entrySet()) {
				String _key = Nest.as(e.getKey()).asString();
				Object _param = e.getValue();
				_result += prepareUrlParam(_param, Cphp.isset(_name) ? _name+'['+_key+']' : _key);
			}
		}
		else {
			_result = '&'+_name+'='+urlencode(Nest.as(_value).asString());
		}

		return _result;
	}
	public static String prepareUrlParam(Object _value) {
		return prepareUrlParam(_value, null);
	}
	
	public static CIcon get_icon(IIdentityBean idBean, SQLExecutor executor, String name) {
		return get_icon(idBean, executor, name, array());
	}
	
	public static CIcon get_icon(IIdentityBean idBean, SQLExecutor executor, String name, CArray params) {
		CIcon icon = null;
		if("favourite".equals(name)){
			if(Feature.showFavouriteIcon){
				if (CFavorite.exists(idBean, executor, Nest.value(params,"fav").asString(), Nest.value(params,"elid").asLong(), Nest.value(params,"elname").asString())) {
					icon = new CIcon(
						_("Remove from favourites"),
						"iconminus",
						"rm4favorites(\""+params.get("elname")+"\", \""+params.get("elid")+"\", 0);"
					);
				} else {
					icon = new CIcon(
						_("Add to favourites"),
						"iconplus",
						"add2favorites(\""+params.get("elname")+"\", \""+params.get("elid")+"\");"
					);
				}
				icon.setAttribute("id", "addrm_fav");
			} else {
				icon = null;
			}
		} else if("fullscreen".equals(name)){
			if(Feature.showFullscreenIcon){
				Curl url = new Curl();
				url.setArgument("fullscreen", !empty($_REQUEST("fullscreen")) ? "0" : "1");
				icon = new CIcon(
					!empty($_REQUEST("fullscreen")) ? _("Normal view") : _("Fullscreen"),
					"fullscreen",
					"document.location = \""+url.getUrl()+"\";"
				);
			} else {
				icon = null;
			}
		} else if("menu".equals(name)){
				icon = new CIcon(_("Menu"), "iconmenu", "create_page_menu(event, \""+params.get("menu")+"\");");
		} else if("reset".equals(name)){
				icon = new CIcon(_("Reset"), "iconreset", "timeControl.objectReset();");
		}
		return icon;
	}
	
	/**
	 * Returns rda, snmp, jmx, ipmi availability status icons and the discovered host lifetime indicator.
	 *
	 * @param type _host
	 *
	 * @return CDiv
	 */
	public static CDiv getAvailabilityTable(Map host) {
		CArray<String> arrArray = array("rda", "snmp");
		if(Feature.enableJMX) arrArray.push("jmx");
		if(Feature.enableIPMI) arrArray.push("ipmi");
		String[] arr = arrArray.valuesAsString();

		// for consistency in foreach loop
		Nest.value(host,"rda_available").$(Nest.value(host,"available").$());
		Nest.value(host,"rda_error").$(Nest.value(host,"error").$());

		CDiv ad = new CDiv(null, "invisible");
		CDiv ai = null;
		for (String val : arr) {
			switch (Nest.value(host,val+"_available").asInteger()) {
				case HOST_AVAILABLE_TRUE:
					ai  = new CDiv(SPACE, "status_icon status_icon_extra icon"+val+"available");
					break;
				case HOST_AVAILABLE_FALSE:
					ai = new CDiv(SPACE, "status_icon status_icon_extra icon"+val+"unavailable");
					ai.setHint(Nest.value(host,val+"_error").asString(), "", "on");
					break;
				case HOST_AVAILABLE_UNKNOWN:
					ai = new CDiv(SPACE, "status_icon status_icon_extra icon"+val+"unknown");
					break;
			}
			ad.addItem(ai);
		}

		// discovered host lifetime indicator
		if (Nest.value(host,"flags").asInteger() == RDA_FLAG_DISCOVERY_CREATED && Nest.value(host,"hostDiscovery","ts_delete").asBoolean()) {
			CDiv deleteError = new CDiv(SPACE, "status_icon status_icon_extra iconwarning");
			deleteError.setHint(
				_s("The host is not discovered anymore and will be deleted in %1$s (on %2$s at %3$s).",
						rda_date2age(Nest.value(host,"hostDiscovery","ts_delete").asLong()), rda_date2str(_("d M Y"), Nest.value(host,"hostDiscovery","ts_delete").asLong()),
						rda_date2str(_("H:i:s"), Nest.value(host,"hostDiscovery","ts_delete").asLong())
				));
			ad.addItem(deleteError);
		}
		return ad;
	}
	
	/**
	 * Create array with all inputs required for date selection and calendar.
	 *
	 * @param string      name
	 * @param int|array  date unix timestamp/date array(Y,m,d,H,i)
	 * @param string|null relatedCalendar name of the calendar which must be closed when this calendar opens
	 *
	 * @return array
	 */
	public static CArray createDateSelector(String name, Object date, String relatedCalendar,String beforeShow) {
		CImg calendarIcon = new CImg("images/general/bar/cal.gif", "calendar", 16, 12, "pointer");
		String onClick = "var pos = getPosition(this); pos.top += 10; pos.left += 16; CLNDR[\""+name+
			"_calendar\"].clndr.clndrshow(pos.top, pos.left);";
		if (!empty(relatedCalendar)) {
			onClick += " CLNDR[\""+relatedCalendar+"_calendar\"].clndr.clndrhide();";
		}

		calendarIcon.onClick(onClick);
		String y, m, d, h, i;
		if (date instanceof Map) {
			Map cdate = (Map)date;
			y = Nest.value(cdate,"y").asString();
			m = Nest.value(cdate,"m").asString();
			d = Nest.value(cdate,"d").asString();
			h = Nest.value(cdate,"h").asString();
			i = Nest.value(cdate,"i").asString();
		} else {
			long cdate = Nest.as(date).asLong();
			y = date("Y", cdate);
			m = date("m", cdate);
			d = date("d", cdate);
			h = date("H", cdate);
			i = date("i", cdate);
		}

		CNumericBox day = new CNumericBox(name+"_day", d, 2);
		day.attr("placeholder", _("dd"));
		CNumericBox month = new CNumericBox(name+"_month", m, 2);
		month.attr("placeholder", _("mm"));
		CNumericBox year = new CNumericBox(name+"_year", y, 4);
		year.attr("placeholder", _("yyyy"));
		CNumericBox hour = new CNumericBox(name+"_hour", h, 2);
		hour.attr("placeholder", _("hh"));
		CNumericBox minute = new CNumericBox(name+"_minute", i, 2);
		minute.attr("placeholder", _("mm"));

		CArray fields = array(year, "/", month, "/", day, SPACE, hour, ":", minute, calendarIcon);

		rda_add_post_js("create_calendar(null,"+
			"[\""+name+"_day\",\""+name+"_month\",\""+name+"_year\",\""+name+"_hour\",\""+name+"_minute\"],"+
			"\""+name+"_calendar\","+
			"\""+name+"\",null,"+beforeShow+");"
		);

		return fields;
	}
	public static CArray createDateSelector(String name, Long date) {
		return createDateSelector(name, date, null,null);
	}
	
	public static CArray createDateSelector(String name, Object date, String relatedCalendar) {
		return createDateSelector(name, date, relatedCalendar,null);
	}
}
