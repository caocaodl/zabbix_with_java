package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_IN;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_RSPCODE;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_TIME;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.SBR;
import static com.isoft.iradar.inc.Defines.SCREEN_MODE_PREVIEW;
import static com.isoft.iradar.inc.Defines.SCREEN_RESOURCE_GRAPH;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.UNKNOWN_VALUE;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.detect_page_type;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.get_request_asLong;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.GraphsUtil.getGraphDims;
import static com.isoft.iradar.inc.GraphsUtil.get_min_itemclock_by_itemid;
import static com.isoft.iradar.inc.HtmlUtil.bold;
import static com.isoft.iradar.inc.HtmlUtil.get_icon;
import static com.isoft.iradar.inc.ItemsUtil.formatHistoryValue;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.model.params.CHttpTestGet;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.tags.screens.CScreenBuilder;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class HttpdetailsAction extends RadarBaseAction {
	
	@Override
	protected void doInitPage() {
		page("title", _("Details of scenario"));
		page("file", "httpdetails.action");
		page("hist_arg", new String[] { "httptestid" });
		page("scripts", new String[] {"class.calendar.js", "gtlc.js", "flickerfreescreen.js"});
		page("type", detect_page_type(PAGE_TYPE_HTML));
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"period",		array(T_RDA_INT, O_OPT, null,	null,		null),
			"stime",		array(T_RDA_STR, O_OPT, null,	null,		null),
			"reset",			array(T_RDA_STR, O_OPT, P_SYS|P_ACT, null,	null),
			"httptestid",	array(T_RDA_INT, O_MAND, P_SYS,	DB_ID,		null),
			"fullscreen",	array(T_RDA_INT, O_OPT, P_SYS,	IN("0,1"),	null),
			// ajax
			"favobj",		array(T_RDA_STR, O_OPT, P_ACT,	null,		null),
			"favref",		array(T_RDA_STR, O_OPT, P_ACT,	NOT_EMPTY,	null),
			"favid",			array(T_RDA_INT, O_OPT, P_ACT,	null,		null),
			"favstate",	array(T_RDA_INT, O_OPT, P_ACT,	NOT_EMPTY,	null)
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
			if ("filter".equals(Nest.value(_REQUEST,"favobj").asString())) {
				CProfile.update(getIdentityBean(), executor, "web.httpdetails.filter.state", Nest.value(_REQUEST,"favstate").$(), PROFILE_TYPE_INT);
			}

			// saving fixed/dynamic setting to profile
			if ("timelinefixedperiod".equals(Nest.value(_REQUEST,"favobj").asString())) {
				if (isset(_REQUEST,"favid")) {
					CProfile.update(getIdentityBean(), executor, "web.httptest.timelinefixed", Nest.value(_REQUEST,"favid").$(), PROFILE_TYPE_INT);
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
		/* Collect data */
		CHttpTestGet htoptions = new CHttpTestGet();
		htoptions.setHttptestIds(get_request_asLong("httptestid"));
		htoptions.setOutput(API_OUTPUT_EXTEND);
		htoptions.setPreserveKeys(true);
		CArray<Map> httpTests = API.HttpTest(getIdentityBean(), executor).get(htoptions);
		Map httpTest = reset(httpTests);
		if (empty(httpTest)) {
			access_deny();
		}

		Nest.value(httpTest,"lastfailedstep").$(0);
		Nest.value(httpTest,"error").$("");

		// fetch http test execution data
		CArray<Map> httpTestDatas = Manager.HttpTest(getIdentityBean(), executor).getLastData(Nest.value(httpTest,"httptestid").asLong());
		Map httpTestData = reset(httpTestDatas);

		// fetch HTTP step items
		Map params = new HashMap();
		params.put("httptestid", Nest.value(httpTest,"httptestid").$());
		CArray<Map> query = DBselect(executor,
			"SELECT i.value_type,i.valuemapid,i.units,i.itemid,hi.type AS httpitem_type,hs.httpstepid"+
			" FROM items i,httpstepitem hi,httpstep hs"+
			" WHERE hi.itemid=i.itemid"+
				" AND hi.httpstepid=hs.httpstepid"+
				" AND hs.httptestid=#{httptestid}",
			params
		);
		CArray<CArray<Map>> httpStepItems = array();
		CArray<Map> items = array();
		for(Map item : query) {
			items.add(item);
			if(!isset(httpStepItems,item.get("httpstepid"))){
				Nest.value(httpStepItems,item.get("httpstepid")).$(array());
			}
			Nest.value(httpStepItems,item.get("httpstepid"),item.get("httpitem_type")).$(item);
		}

		// fetch HTTP item history
		CArray<CArray<Map>> itemHistory = Manager.History(getIdentityBean(), executor).getLast(items);
		
		/* Display */
		CWidget httpdetailsWidget = new CWidget();
		httpdetailsWidget.addPageHeader(
			array(
				_("DETAILS OF SCENARIO"),
				SPACE,
				bold(CMacrosResolverHelper.resolveHttpTestName(this.getIdentityBean(), executor,Nest.value(httpTest,"hostid").asInteger(), Nest.value(httpTest,"name").asString())),
				isset(Nest.value(httpTestData,"lastcheck").$()) ? " ["+rda_date2str(_("d M Y H:i:s"), Nest.value(httpTestData,"lastcheck").asLong())+"]" : null
			),
			array(
				get_icon(getIdentityBean(), executor, "reset", map("id", get_request("httptestid"))),
				get_icon(getIdentityBean(), executor, "fullscreen", map("fullscreen", Nest.value(_REQUEST,"fullscreen").$()))
			)
		);
		
		// append table to widget
		CTableInfo httpdetailsTable = new CTableInfo();
		httpdetailsTable.setHeader(array(
			_("Step"),
			_("Speed"),
			_("Response time"),
			_("Response code"),
			_("Status")
		));

		params.put("httptestid", Nest.value(httpTest,"httptestid").$());
		CArray<Map> db_httpsteps = DBselect(executor,"SELECT * FROM httpstep WHERE httptestid=#{httptestid} ORDER BY no", params);

		Map totalTime = map(
			"value", 0,
			"value_type", null,
			"valuemapid", null,
			"units", null
		);
		
		Map status = null;
		CArray itemIds = array();
		for(Map httpstep_data : db_httpsteps) {
			CArray<Map> httpStepItemsByType = Nest.value(httpStepItems,httpstep_data.get("httpstepid")).asCArray(true);

			status = map();
			Nest.value(status,"msg").$(_("OK"));
			Nest.value(status,"style").$("enabled");
			Nest.value(status,"afterError").$(false);

			if (!isset(httpTestData,"lastfailedstep")) {
				Nest.value(status,"msg").$(_("Never executed"));
				Nest.value(status,"style").$("unknown");
			} else if (Nest.value(httpTestData,"lastfailedstep").asInteger() != 0) {
				if (Nest.value(httpTestData,"lastfailedstep").asInteger() == Nest.value(httpstep_data,"no").asInteger()) {
					Nest.value(status,"msg").$((Nest.value(httpTestData,"error").$() == null)
						? _("Unknown error")
						: _s("Error: %1$s", Nest.value(httpTestData,"error").$()));
					Nest.value(status,"style").$("disabled");
				} else if (Nest.value(httpTestData,"lastfailedstep").asInteger() < Nest.value(httpstep_data,"no").asInteger()) {
					Nest.value(status,"msg").$(_("Unknown"));
					Nest.value(status,"style").$("unknown");
					Nest.value(status,"afterError").$(true);
				}
			}

			for(Map httpStepItem : httpStepItemsByType) {
				// calculate the total time it took to execute the scenario
				// skip steps that come after a failed step
				if (!Nest.value(status,"afterError").asBoolean() && Nest.value(httpStepItem,"httpitem_type").asInteger() == HTTPSTEP_ITEM_TYPE_TIME) {
					Nest.value(totalTime,"value_type").$(Nest.value(httpStepItem,"value_type").$());
					Nest.value(totalTime,"valuemapid").$(Nest.value(httpStepItem,"valuemapid").$());
					Nest.value(totalTime,"units").$(Nest.value(httpStepItem,"units").$());

					if (isset(itemHistory,httpStepItem.get("itemid"))) {
						Map history = itemHistory.get(httpStepItem.get("itemid")).get(0);
						Nest.value(totalTime,"value").$(Nest.value(totalTime,"value").asDouble()+Nest.value(history,"value").asDouble());
					}
				}
				itemIds.add(Nest.value(httpStepItem,"itemid").$());
			}

			// step speed
			String speed;
			Map speedItem = httpStepItemsByType.get(HTTPSTEP_ITEM_TYPE_IN);
			if (!is_null(speedItem) && !Nest.value(status,"afterError").asBoolean() && isset(itemHistory,speedItem.get("itemid")) && Nest.value(itemHistory,speedItem.get("itemid"),0,"value").asInteger() > 0) {
				speed = formatHistoryValue(getIdentityBean(), executor, Nest.value(itemHistory,speedItem.get("itemid"),0,"value").asString(), speedItem);
			} else {
				speed = UNKNOWN_VALUE;
			}

			// step response time
			String respTime;
			Map respTimeItem = httpStepItemsByType.get(HTTPSTEP_ITEM_TYPE_TIME);
			if (!is_null(respTimeItem) && !Nest.value(status,"afterError").asBoolean() && isset(itemHistory,respTimeItem.get("itemid")) && Nest.value(itemHistory,respTimeItem.get("itemid"),0,"value").asFloat() > 0F) {
				respTime = formatHistoryValue(getIdentityBean(), executor, Nest.value(itemHistory,respTimeItem.get("itemid"),0,"value").asString(), respTimeItem);
			} else {
				respTime = UNKNOWN_VALUE;
			}

			// step response code
			String resp;
			Map respItem = httpStepItemsByType.get(HTTPSTEP_ITEM_TYPE_RSPCODE);
			if (!is_null(respItem) && !Nest.value(status,"afterError").asBoolean() && isset(itemHistory,respItem.get("itemid")) && Nest.value(itemHistory,respItem.get("itemid"),0,"value").asInteger() > 0) {
				resp = formatHistoryValue(getIdentityBean(), executor, Nest.value(itemHistory,respItem.get("itemid"),0,"value").asString(), respItem);
			} else {
				resp = UNKNOWN_VALUE;
			}

			httpdetailsTable.addRow(array(
				CMacrosResolverHelper.resolveHttpTestName(this.getIdentityBean(), executor,Nest.value(httpTest,"hostid").asInteger(), Nest.value(httpstep_data,"name").asString()),
				speed,
				respTime,
				resp,
				new CSpan(Nest.value(status,"msg").$(), Nest.value(status,"style").asString())
			));
		}
		
		if (!isset(httpTestData,"lastfailedstep")) {
			Nest.value(status,"msg").$(_("Never executed"));
			Nest.value(status,"style").$("unknown");
		} else if (Nest.value(httpTestData,"lastfailedstep").asInteger() != 0) {
			Nest.value(status,"msg").$((Nest.value(httpTestData,"error").$() == null)
				? _("Unknown error")
				: _s("Error: %1$s", Nest.value(httpTestData,"error").$()));
			Nest.value(status,"style").$("disabled");
		} else {
			Nest.value(status,"msg").$(_("OK"));
			Nest.value(status,"style").$("enabled");
		}
		
		httpdetailsTable.addRow(array(
			bold(_("TOTAL")),
			SPACE,
			bold((!empty(Nest.value(totalTime,"value").$())) ? formatHistoryValue(getIdentityBean(), executor,Nest.value(totalTime,"value").asString(), totalTime) : UNKNOWN_VALUE),
			SPACE,
			new CSpan(Nest.value(status,"msg").$(), Nest.value(status,"style").asString()+" bold")
		));

		httpdetailsWidget.addItem(httpdetailsTable);
		httpdetailsWidget.show();

		echo(SBR);
		
		// create graphs widget
		CWidget graphsWidget = new CWidget();
		graphsWidget.addFlicker(new CDiv(null, null, "scrollbar_cntr"), Nest.as(CProfile.get(getIdentityBean(), executor,"web.httpdetails.filter.state", 0)).asInteger());
		graphsWidget.addItem(SPACE);

		CTableInfo graphTable = new CTableInfo();
		graphTable.setAttribute("id", "graph");
		
		// dims
		Map graphDims = getGraphDims(getIdentityBean(), executor);
		Nest.value(graphDims,"shiftYtop").$(Nest.value(graphDims,"shiftYtop").asInteger()+1);
		Nest.value(graphDims,"width").$(Nest.value(graphDims,"width").asInteger()-120);
		Nest.value(graphDims,"graphHeight").$(150);
		
		/* Graph in */
		CScreenBase graphInScreen = new CScreenBase(getIdentityBean(), executor, map(
			"resourcetype", SCREEN_RESOURCE_GRAPH,
			"mode", SCREEN_MODE_PREVIEW,
			"dataId", "graph_in",
			"profileIdx", "web.httptest",
			"profileIdx2", get_request("httptestid"),
			"period", get_request("period"),
			"stime", get_request("stime")
		));
		Nest.value(graphInScreen.timeline,"starttime").$(date(TIMESTAMP_FORMAT, get_min_itemclock_by_itemid(getIdentityBean(), executor,itemIds.valuesAsString())));
		
		String src = "chart3.action?height=150"+
				"&name="+Nest.value(httpTest,"name").asString()+
				"&http_item_type="+HTTPSTEP_ITEM_TYPE_IN+
				"&httptestid="+Nest.value(httpTest,"httptestid").asString()+
				"&graphtype="+GRAPH_TYPE_STACKED+
				"&period="+Nest.value(graphInScreen.timeline,"period").asString()+
				"&stime="+Nest.value(graphInScreen.timeline,"stime").asString()+
				"&profileIdx="+graphInScreen.profileIdx+
				"&profileIdx2="+graphInScreen.profileIdx2;
		
		CDiv graphInContainer = new CDiv(new CLink(null, src), "flickerfreescreen", "flickerfreescreen_graph_in");
		graphInContainer.setAttribute("style", "position: relative");
		graphInContainer.setAttribute("data-timestamp", time());
		graphTable.addRow(array(bold(_("Speed")), graphInContainer));
		
		Map timeControlData = map(
			"id", "graph_in",
			"containerid", "flickerfreescreen_graph_in",
			"src", src,
			"objDims", graphDims,
			"loadSBox", 1,
			"loadImage", 1,
			"periodFixed", CProfile.get(getIdentityBean(), executor,"web.httptest.timelinefixed", 1),
			"sliderMaximumTimePeriod", RDA_MAX_PERIOD
		);
		rda_add_post_js("timeControl.addObject(\"graph_in\", "+rda_jsvalue(graphInScreen.timeline)+", "+rda_jsvalue(timeControlData)+");");
		graphInScreen.insertFlickerfreeJs();
	
		/*
		 * Graph time
		 */
		CScreenBase graphTimeScreen = new CScreenBase(getIdentityBean(), executor, map(
			"resourcetype", SCREEN_RESOURCE_GRAPH,
			"mode", SCREEN_MODE_PREVIEW,
			"dataId", "graph_time",
			"profileIdx", "web.httptest",
			"profileIdx2", get_request("httptestid"),
			"period", get_request("period"),
			"stime", get_request("stime")
		));
		
		src = "chart3.action?height=150"+
			"&name="+Nest.value(httpTest,"name").$()+
			"&http_item_type="+HTTPSTEP_ITEM_TYPE_TIME+
			"&httptestid="+Nest.value(httpTest,"httptestid").$()+
			"&graphtype="+GRAPH_TYPE_STACKED+
			"&period="+graphTimeScreen.timeline.get("period")+
			"&stime="+graphTimeScreen.timeline.get("stime")+
			"&profileIdx="+graphTimeScreen.profileIdx+
			"&profileIdx2="+graphTimeScreen.profileIdx2;
		
		CDiv graphTimeContainer = new CDiv(new CLink(null, src), "flickerfreescreen", "flickerfreescreen_graph_time");
		graphTimeContainer.setAttribute("style", "position: relative");
		graphTimeContainer.setAttribute("data-timestamp", time());
		graphTable.addRow(array(bold(_("Response time")), graphTimeContainer));
		
		
		timeControlData = map(
				"id", "graph_time",
				"containerid", "flickerfreescreen_graph_time",
				"src", src,
				"objDims", graphDims,
				"loadSBox", 1,
				"loadImage", 1,
				"periodFixed", CProfile.get(getIdentityBean(), executor,"web.httptest.timelinefixed", 1),
				"sliderMaximumTimePeriod", RDA_MAX_PERIOD
			);
		rda_add_post_js("timeControl.addObject(\"graph_time\", "+rda_jsvalue(graphInScreen.timeline)+", "+rda_jsvalue(timeControlData)+");");
		graphTimeScreen.insertFlickerfreeJs();
		
		// scroll
		CScreenBuilder.insertScreenScrollJs(getIdentityBean(), executor,map("timeline", graphInScreen.timeline));
		CScreenBuilder.insertScreenRefreshTimeJs();
		CScreenBuilder.insertProcessObjectsJs();

		graphsWidget.addItem(graphTable);
		graphsWidget.show();
	}

}
