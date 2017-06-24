package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_ID;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PERIOD;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.params.CHostGet;
import com.isoft.iradar.tags.graphdraw.CLineGraphDraw;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Chart2Action extends RadarBaseAction {
	
	@Override
	protected boolean isHtmlPage() {
		return false;
	}

	@Override
	protected void doInitPage() {
		page("file", "chart2.action");
		page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"graphid",			array(T_RDA_INT, O_MAND, P_SYS,		DB_ID,		null),
			"period",			array(T_RDA_INT, O_OPT, P_NZERO,		BETWEEN(RDA_MIN_PERIOD, RDA_MAX_PERIOD), null),
			"stime",			array(T_RDA_STR, O_OPT, P_SYS,		null,		null),
			"profileIdx",		array(T_RDA_STR, O_OPT, null,		null,		null),
			"profileIdx2",	array(T_RDA_STR, O_OPT, null,		null,		null),
			"updateProfile",array(T_RDA_STR, O_OPT, null,		null,		null),
			"border",			array(T_RDA_INT, O_OPT, P_NZERO,		IN("0,1"),	null),
			"width",			array(T_RDA_INT, O_OPT, P_NZERO,		"{}>0",		null),
			"height",			array(T_RDA_INT, O_OPT, P_NZERO,		"{}>0",		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		/* Permissions */
		CGraphGet goptions = new CGraphGet();
		goptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
		goptions.setOutput(API_OUTPUT_EXTEND);
		CArray<Map> dbGraphs = API.Graph(getIdentityBean(), executor).get(goptions);
		Map dbGraph = null;
		if (empty(dbGraphs)) {
			access_deny();
		} else {
			dbGraph = reset(dbGraphs);
		}
		
		CHostGet hoptions = new CHostGet();
		hoptions.setGraphIds(Nest.value(_REQUEST,"graphid").asLong());
		hoptions.setOutput(API_OUTPUT_EXTEND);
		hoptions.setTemplatedHosts(true);
		CArray<Map> hosts = API.Host(getIdentityBean(), executor).get(hoptions);
		Map host = reset(hosts);
		/* Display */
		Map timeline = CScreenBase.calculateTime(getIdentityBean(), executor, map(
			"profileIdx", get_request("profileIdx", "web.screens"),
			"profileIdx2", get_request("profileIdx2"),
			"updateProfile", get_request("updateProfile", true),
			"period", get_request("period"),
			"stime", get_request("stime")
		));

		CProfile.update(getIdentityBean(), executor, "web.screens.graphid", Nest.value(_REQUEST,"graphid").$(), PROFILE_TYPE_ID);

		String chartHeader = host.get("name")+NAME_DELIMITER+Nest.value(dbGraph,"name").$();

		CLineGraphDraw graph = new CLineGraphDraw(getIdentityBean(), executor, Nest.value(dbGraph,"graphtype").asInteger());
		graph.setHeader(chartHeader);
		graph.setPeriod(Nest.value(timeline,"period").asInteger());
		graph.setSTime(Nest.value(timeline,"stime").asLong());

		if (isset(_REQUEST,"border")) {
			graph.setBorder(0);
		}

		int width = get_request("width", 0);
		if (width <= 0) {
			width = Nest.value(dbGraph,"width").asInteger();
		}

		int height = get_request("height", 0);
		if (height <= 0) {
			height = Nest.value(dbGraph,"height").asInteger();
		}

		graph.showLegend(Nest.value(dbGraph,"show_legend").asBoolean());
		graph.showWorkPeriod(Nest.value(dbGraph,"show_work_period").asInteger());
		graph.showTriggers(Nest.value(dbGraph,"show_triggers").asInteger());
		graph.setWidth(width);
		graph.setHeight(height);
		graph.setYMinAxisType(Nest.value(dbGraph,"ymin_type").asInteger());
		graph.setYMaxAxisType(Nest.value(dbGraph,"ymax_type").asInteger());
		graph.setYAxisMin(Nest.value(dbGraph,"yaxismin").asDouble());
		graph.setYAxisMax(Nest.value(dbGraph,"yaxismax").asDouble());
		graph.setYMinItemId(Nest.value(dbGraph,"ymin_itemid").asString());
		graph.setYMaxItemId(Nest.value(dbGraph,"ymax_itemid").asString());
		graph.setLeftPercentage(Nest.value(dbGraph,"percent_left").asFloat());
		graph.setRightPercentage(Nest.value(dbGraph,"percent_right").asFloat());

		Map params = new HashMap();
		params.put("graphid", Nest.value(dbGraph,"graphid").$());
		CArray<Map> _dbGraphItems = DBselect(executor,
			"SELECT gi.*"+
			" FROM graphs_items gi"+
			" WHERE gi.graphid=#{graphid}"+
			" ORDER BY gi.sortorder,gi.itemid DESC",
			params
		);
		for (Map dbGraphItem: _dbGraphItems) {
			graph.addItem(
				Nest.value(dbGraphItem,"itemid").asString(),
				Nest.value(dbGraphItem,"yaxisside").asInteger(),
				Nest.value(dbGraphItem,"calc_fnc").asInteger(),
				Nest.value(dbGraphItem,"color").asString(),
				Nest.value(dbGraphItem,"drawtype").asInteger(),
				Nest.value(dbGraphItem,"type").asInteger()
			);
		}
	
		try {
			byte[] bs = graph.draw();
			this.getResponse().getOutputStream().write(bs);
		} catch (Exception e) {
			e.printStackTrace();
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
