package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.core.utils.EasyObject.asLong;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.HTTPSTEP_ITEM_TYPE_TIME;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PERIOD;
import static com.isoft.iradar.inc.Defines.T_RDA_DBL;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.asort_by_key;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.HttpTestUtil.get_httptest_by_httptestid;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.graphdraw.CLineGraphDraw;
import com.isoft.iradar.tags.graphdraw.CLineGraphDrawSum;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Chart3Action extends RadarBaseAction {
	
	private boolean isDataValid = false;
	
	@Override
	protected boolean isHtmlPage() {
		return false;
	}
	
	@Override
	protected void doInitPage() {
		page("file", "chart3.action");
		page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		Nest.value(_REQUEST, "percent_left").$(0);
		Nest.value(_REQUEST, "percent_right").$(0);
		
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"func" ,			array(T_RDA_STR, O_OPT, null,		null,				null),
			"period" ,					array(T_RDA_INT, O_OPT, P_NZERO,	BETWEEN(RDA_MIN_PERIOD, RDA_MAX_PERIOD), null),
			"stime" ,					array(T_RDA_INT, O_OPT, P_NZERO,	null,				null),
			"profileIdx" ,				array(T_RDA_STR, O_OPT, null,		null,				null),
			"profileIdx2" ,			array(T_RDA_STR, O_OPT, null,		null,				null),
			"httptestid" ,				array(T_RDA_INT, O_OPT, P_NZERO,	null,				null),
			"http_item_type" ,		array(T_RDA_INT, O_OPT, null,		null,				null),
			"name" ,					array(T_RDA_STR, O_OPT, null,		null,				null),
			"width" ,					array(T_RDA_INT, O_OPT, null,		BETWEEN(0, 65535),	null),
			"height" ,					array(T_RDA_INT, O_OPT, null,		BETWEEN(0, 65535),	null),
			"ymin_type" ,			array(T_RDA_INT, O_OPT, null,		IN("0,1,2"),		null),
			"ymax_type" ,			array(T_RDA_INT, O_OPT, null,		IN("0,1,2"),		null),
			"ymin_itemid" ,			array(T_RDA_INT, O_OPT, null,		DB_ID,				null),
			"ymax_itemid" ,		array(T_RDA_INT, O_OPT, null,		DB_ID,				null),
			"legend" ,					array(T_RDA_INT, O_OPT, null,		IN("0,1"),			null),
			"showworkperiod" ,	array(T_RDA_INT, O_OPT, null,		IN("0,1"),			null),
			"showtriggers" ,		array(T_RDA_INT, O_OPT, null,		IN("0,1"),			null),
			"graphtype" ,			array(T_RDA_INT, O_OPT, null,		IN("0,1"),			null),
			"yaxismin" ,				array(T_RDA_DBL, O_OPT, null,		null,				null),
			"yaxismax" ,				array(T_RDA_DBL, O_OPT, null,		null,				null),
			"percent_left" ,			array(T_RDA_DBL, O_OPT, null,		BETWEEN(0, 100),	null),
			"percent_right" ,		array(T_RDA_DBL, O_OPT, null,		BETWEEN(0, 100),	null),
			"items" ,					array(T_RDA_STR, O_OPT, null,		null,				null)
		);
		isDataValid = check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		/* Permissions */
		int httptestid = get_request("httptestid", 0);
		CArray<Map> items = new CArray();
		String name = "";
		if (!empty(httptestid)) {
			if (!API.HttpTest(getIdentityBean(), executor).isReadable(Nest.value(_REQUEST,"httptestid").asLong())) {
				access_deny();
			}

			CArray color = map(
				"current" , 0,
				0 , map("next" , "1"),
				1 , map("color" , "Red", "next" , "2"),
				2 , map("color" , "Dark Green", "next" , "3"),
				3 , map("color" , "Blue", "next" , "4"),
				4 , map("color" , "Dark Yellow", "next" , "5"),
				5 , map("color" , "Cyan", "next" , "6"),
				6 , map("color" , "Gray", "next" , "7"),
				7 , map("color" , "Dark Red", "next" , "8"),
				8 , map("color" , "Green", "next" , "9"),
				9 , map("color" , "Dark Blue", "next" , "10"),
				10 , map("color" , "Yellow", "next" , "11"),
				11 , map("color" , "Black", "next" , "1")
			);

			SqlBuilder sqlParts = new SqlBuilder();
			String sql = "SELECT i.itemid"+
					" FROM httpstepitem hi,items i,httpstep hs"+
					" WHERE i.itemid=hi.itemid"+
						" AND hs.httptestid="+sqlParts.marshalParam(httptestid)+
						" AND hs.httpstepid=hi.httpstepid"+
						" AND hi.type="+sqlParts.marshalParam(get_request("http_item_type", HTTPSTEP_ITEM_TYPE_TIME))+
					" ORDER BY hs.no DESC";
			Map<String, Object> params = sqlParts.getNamedParams();
			
			CArray<Map> dbItems = DBselect(executor,sql,params);
			int length  = dbItems.size();
			Map item;
			for(int i=0;i<length;i++){
				item = dbItems.get(i);
				
				String itemColor = Nest.value(color,Nest.value(color,"current").$(Nest.value(color, Nest.value(color,"current").$(),"next").$()).$(),"color").asString();
				items.add(map( 
								"itemid" , Nest.value(item,"itemid").$(), 
								"color" , itemColor));
			}
			Map httpTest = get_httptest_by_httptestid(executor, asLong(httptestid));

			name = CMacrosResolverHelper.resolveHttpTestName(getIdentityBean(), executor, Nest.value(httpTest,"hostid").asInteger(), Nest.value(httpTest,"name").asString());
		}else if (!empty(items = get_request("items", array()))) {
			asort_by_key(items, "sortorder");
			CItemGet option = new CItemGet();
			option.setItemIds(rda_objectValues(items, "itemid").valuesAsLong());
			option.setOutput(new String[]{"itemid"});
			option.setFilter("flags",String.valueOf(RDA_FLAG_DISCOVERY_NORMAL), String.valueOf(RDA_FLAG_DISCOVERY_PROTOTYPE), String.valueOf(RDA_FLAG_DISCOVERY_CREATED));
			option.setWebItems(true);
			option.setPreserveKeys(true);
			
			CArray<Map> dbItems = API.Item(getIdentityBean(), executor).get(option);
			for(Map _item : items) {
				if (!isset(dbItems,_item.get("itemid"))) {
					access_deny();
				}
			}
			name = get_request("name", "");
		} else {
			show_error_message(_("No items defined."));
			access_deny();
		}

		/* Display */
		if (isDataValid) {
			String profileIdx = get_request("profileIdx", "web.httptest");
			String profileIdx2 = get_request("httptestid", get_request("profileIdx2"));

			Map timeline = CScreenBase.calculateTime(getIdentityBean(), executor,map(
				"profileIdx" , profileIdx,
				"profileIdx2" , profileIdx2,
				"period" , get_request("period"),
				"stime" , get_request("stime")
			));

			CProfile.update(getIdentityBean(), executor,profileIdx+".httptestid", profileIdx2, PROFILE_TYPE_ID);

			String func = Nest.value(_REQUEST, "func").asString();
			CLineGraphDraw graph = null;
			if("sum".equals(func)) {
				graph = new CLineGraphDrawSum(getIdentityBean(), executor);
			}else {
				graph = new CLineGraphDraw(getIdentityBean(), executor,get_request("graphtype", GRAPH_TYPE_NORMAL));
			}
			
			graph.setHeader(name);
			graph.setPeriod(Nest.value(timeline,"period").asInteger());
			graph.setSTime(Nest.value(timeline,"stime").asLong());
			graph.setWidth(get_request("width", 900));
			graph.setHeight(get_request("height", 200));
			graph.showLegend(!empty(get_request("legend", 1)));
			graph.showWorkPeriod(get_request("showworkperiod", 1));
			graph.showTriggers(get_request("showtriggers", 1));
			graph.setYMinAxisType(get_request("ymin_type", GRAPH_YAXIS_TYPE_CALCULATED));
			graph.setYMaxAxisType(get_request("ymax_type", GRAPH_YAXIS_TYPE_CALCULATED));
			graph.setYAxisMin(get_request("yaxismin", 0.00));
			graph.setYAxisMax(get_request("yaxismax", 100.00));
			graph.setYMinItemId(get_request("ymin_itemid", 0).toString());
			graph.setYMaxItemId(get_request("ymax_itemid", 0).toString());
//			graph.setLeftPercentage(get_request("percent_left", 0f));
//			graph.setRightPercentage(get_request("percent_right", 0f));
			graph.setLeftPercentage(0f);
			graph.setRightPercentage(0f);

			for(Map item : items) {
				graph.addItem(
					Nest.value(item,"itemid").asString(),
					isset(item,"yaxisside") ? Nest.value(item,"yaxisside").asInteger() : null,
					isset(item,"calc_fnc") ? Nest.value(item,"calc_fnc").asInteger() : null,
					isset(item,"color") ? Nest.value(item,"color").asString() : null,
					isset(item,"drawtype") ? Nest.value(item,"drawtype").asInteger() : null,
					isset(item,"type") ? Nest.value(item,"type").asInteger() : null
				);
			}

			try {
				byte[] bs = graph.draw();
				this.getResponse().getOutputStream().write(bs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}
	
}
