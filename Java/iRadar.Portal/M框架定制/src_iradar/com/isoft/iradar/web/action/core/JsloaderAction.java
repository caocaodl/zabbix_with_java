package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.file_get_contents;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.TranslateDefines.FILTER_TIMEBAR_DATE_FORMAT;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.iradar.RadarContext;
import com.isoft.iradar.inc.MD5Util;
import com.isoft.types.CArray;
import com.isoft.types.CMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.web.common.BasePageAction;

public class JsloaderAction extends BasePageAction {

	public void doWork() {
		CMap<Object, Object> _REQUEST = RadarContext._REQUEST();
		CArray<String> files = Nest.value(_REQUEST,"files").asCArray();
		if (empty(files)) {
			files = array(
				"prototype.js",
				"jquery.js",
				"jquery-ui.js",
				"activity-indicator.js",
				"common.js",
				"class.cdebug.js",
				"class.cdate.js",
				"class.cookie.js",
				"class.curl.js",
				"class.rpc.js",
				"class.bbcode.js",
				"class.csuggest.js",
				"main.js",
				"functions.js",
				"menu.js",
				"menupopup.js",
				"init.js",
                "calendar.position.js"
			);
			// load frontend messaging only for some pages
			String showGuiMessaging = Nest.value(_REQUEST, "showGuiMessaging").asString();
			if (isset(showGuiMessaging) && Nest.as(showGuiMessaging).asBoolean()) {
				files.add("class.cmessages.js");
			}
		}
		
		StringBuilder js = new StringBuilder();
		js.append("if (typeof(locale) == 'undefined') { var locale = {}; }").append('\n');
		
		for(String file : files) {
			if (isset(tranStrings,file)) {
				for (Entry<Object, String> e : tranStrings.get(file).entrySet()) {
				    String origStr = e.getKey().toString();
				    String str = e.getValue();
				    js.append("locale['"+origStr+"'] = "+rda_jsvalue(str)+";");
				}
			}
		}
		
		RadarContext ctx = RadarContext.getContext();
		for(String file : files) {
			if (isset(availableJScripts,file)) {
				js.append(file_get_contents(ctx, "js/"+availableJScripts.get(file)+file)).append('\n');
			}
		}

		int jsLength = js.length();
		String etag = MD5Util.MD5(String.valueOf(jsLength));
		
		HttpServletResponse response = this.getResponse();
		HttpServletRequest request = this.getRequest();
		String ifNoneMatch = request.getHeader("If-None-Match");
		
		if (isset(ifNoneMatch) && ifNoneMatch.equals(etag)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			response.setHeader("ETag",etag);
			return;
		}
		
		response.setContentType("application/javascript; charset=UTF-8");
		response.setHeader("Cache-Control", "public, must-revalidate");
		response.setHeader("ETag",etag);
		
		try {
			response.getWriter().write(js.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// available scripts "scriptFileName", "path relative to js/"
	private final static CArray<String> availableJScripts = map(
		"common.js", "",
		"menu.js", "",
		"menupopup.js", "",
		"gtlc.js", "",
		"functions.js", "",
		"main.js", "",
		"dom.js", "",
		"servercheck.js", "",
		"flickerfreescreen.js", "",
		"multiselect.js", "",
		// vendors
		"prototype.js", "",
		"jquery.js", "jquery/",
		"jquery-ui.js", "jquery/",
		"activity-indicator.js", "vendors/",
		// classes
		"class.bbcode.js", "",
		"class.calendar.js", "",
		"class.cdate.js", "",
		"class.cdebug.js", "",
		"class.cmap.js", "",
		"class.cmessages.js", "",
		"class.cookie.js", "",
		"class.cscreen.js", "",
		"class.csuggest.js", "",
		"class.cswitcher.js", "",
		"class.ctree.js", "",
		"class.curl.js", "",
		"class.rpc.js", "",
		"class.pmaster.js", "",
		"class.cviewswitcher.js", "",
		"init.js", "",
		// templates
		"sysmap.tpl.js", "templates/",
		"calendar.position.js",""
	);
	
	private final static CArray<CArray<String>> tranStrings = map(
		"gtlc.js", map(
			"S_ALL_S", _("All"),
			"S_ZOOM", _("Zoom"),
			"S_FIXED_SMALL", _("fixed"),
			"S_DYNAMIC_SMALL", _("dynamic"),
			"S_NOW_SMALL", _("now"),
			"S_YEAR_SHORT", _x("y", "year short"),
			"S_MONTH_SHORT", _x("m", "month short"),
			"S_DAY_SHORT", _x("d", "day short"),
			"S_HOUR_SHORT", _x("h", "hour short"),
			"S_DATE_FORMAT", FILTER_TIMEBAR_DATE_FORMAT
		),
		"functions.js", map(
			"Cancel", _("Cancel"),
			"DO_YOU_REPLACE_CONDITIONAL_EXPRESSION_Q", _("Do you wish to replace the conditional expression?"),
			"Events", _("Events"),
			"Execute", _("Execute"),
			"Execution confirmation", _("Execution confirmation"),
			"History", _("History"),
			"History and simple graphs", _("History and simple graphs"),
			"S_INSERT_MACRO", _("Insert macro"),
			"S_CREATE_LOG_TRIGGER", _("Create trigger"),
			"S_DELETE", _("Delete"),
			"S_DELETE_KEYWORD_Q", _("Delete keyword?"),
			"S_DELETE_EXPRESSION_Q", _("Delete expression?"),
			"Simple graphs", _("Simple graphs"),
			"Triggers", _("Triggers")
		),
		"class.calendar.js", map(
			"S_JANUARY", _("January"),
			"S_FEBRUARY", _("February"),
			"S_MARCH", _("March"),
			"S_APRIL", _("April"),
			"S_MAY", _("May"),
			"S_JUNE", _("June"),
			"S_JULY", _("July"),
			"S_AUGUST", _("August"),
			"S_SEPTEMBER", _("September"),
			"S_OCTOBER", _("October"),
			"S_NOVEMBER", _("November"),
			"S_DECEMBER", _("December"),
			"S_MONDAY_SHORT_BIG", _x("M", "Monday short"),
			"S_TUESDAY_SHORT_BIG", _x("T", "Tuesday short"),
			"S_WEDNESDAY_SHORT_BIG", _x("W", "Wednesday short"),
			"S_THURSDAY_SHORT_BIG", _x("T", "Thursday short"),
			"S_FRIDAY_SHORT_BIG", _x("F", "Friday short"),
			"S_SATURDAY_SHORT_BIG", _x("S", "Saturday short"),
			"S_SUNDAY_SHORT_BIG", _x("S", "Sunday short"),
			"S_NOW", _("Now"),
			"S_DONE", _("Done"),
			"S_TIME", _("Time")
		),
		"class.cmap.js", map(
			"S_ON", _("On"),
			"S_OFF", _("Off"),
			"S_HIDDEN", _("Hidden"),
			"S_SHOWN", _("Shown"),
			"S_HOST", _("Host"),
			"S_MAP", _("Map"),
			"S_TRIGGER", _("Trigger"),
			"S_HOST_GROUP", _("Host group"),
			"S_IMAGE", _("Image"),
			"S_DEFAULT", _("Default"),
			"S_CLOSE", _("Close"),
			"S_PLEASE_SELECT_TWO_ELEMENTS", _("Please select two elements"),
			"S_DOT", _("Dot"),
			"S_TWO_ELEMENTS_SHOULD_BE_SELECTED", _("Two elements should be selected"),
			"S_DELETE_SELECTED_ELEMENTS_Q", _("Delete selected elements?"),
			"S_NEW_ELEMENT", _("New element"),
			"S_INCORRECT_ELEMENT_MAP_LINK", _("All links should have \"Name\" and \"URL\" specified"),
			"S_EACH_URL_SHOULD_HAVE_UNIQUE", _("Each URL should have a unique name. Please make sure there is only one URL named"),
			"S_DELETE_LINKS_BETWEEN_SELECTED_ELEMENTS_Q", _("Delete links between selected elements?"),
			"S_NO_IMAGES", "You need to have at least one image uploaded to create map element. Images can be uploaded in Administration->General->Images section.",
			"S_ICONMAP_IS_NOT_ENABLED", _("Iconmap is not enabled"),
			"Colour \"%1$s\" is not correct: expecting hexadecimal colour code (6 symbols).", _("Colour \"%1$s\" is not correct: expecting hexadecimal colour code (6 symbols).")
		),
		"class.cmessages.js", map(
			"S_MUTE", _("Mute"),
			"S_UNMUTE", _("Unmute"),
			"S_MESSAGES", _("Messages"),
			"S_CLEAR", _("Clear"),
			"S_SNOOZE", _("Snooze"),
			"S_MOVE", _("Move")
		),
		"class.cookie.js", map(
			"S_MAX_COOKIE_SIZE_REACHED", _("We are sorry, the maximum possible number of elements to remember has been reached.")
		),
		"main.js", map(
			"S_CLOSE", _("Close"),
			"S_NO_ELEMENTS_SELECTED", _("No elements selected!")
		),
		"init.js", map(
			"Host screens", _("Host screens"),
			"Go to", _("Go to"),
			"Latest data", _("Latest data"),
			"Scripts", _("Scripts"),
			"Host inventories", _("Host inventories"),
			"Add service", _("Add service"),
			"Edit service", _("Edit service"),
			"Delete service", _("Delete service"),
			"Delete the selected service?", _("Delete the selected service?")
		),
		"multiselect.js", map(
			"No matches found", _("No matches found"),
			"More matches found...", _("More matches found..."),
			"type here to search", _("type here to search"),
			"new", _("new"),
			"Select", _("Select")
		),
		"menupopup.js", map(
			"Acknowledge", _("Acknowledge"),
			"Configuration", _("Configuration"),
			"Events", _("Events"),
			"Go to", _("Go to"),
			"History", _("History"),
			"Host inventory", _("Host inventory"),
			"Host screens", _("Host screens"),
			"Latest data", _("Latest data"),
			"Latest events", _("Latest events"),
			"Latest values", _("Latest values"),
			"Last hour graph", _("Last hour graph"),
			"Last month graph", _("Last month graph"),
			"Last week graph", _("Last week graph"),
			"Scripts", _("Scripts"),
			"Status of triggers", _("Status of triggers"),
			"Submap", _("Submap"),
			"Trigger", _("Trigger"),
			"URL", _("URL"),
			"URLs", _("URLs")
		)
	);
}
