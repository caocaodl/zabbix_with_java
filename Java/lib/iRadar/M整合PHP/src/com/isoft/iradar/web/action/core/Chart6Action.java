package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.$_REQUEST;
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
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PERIOD;
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
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.tags.graphdraw.CPieGraphDraw;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Chart6Action extends RadarBaseAction {
	@Override
	protected void doInitPage() {
		_page("file", "chart6.action");
		_page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"graphid",			array(T_RDA_INT, O_MAND, P_SYS,		DB_ID,		null),
			"period",			array(T_RDA_INT, O_OPT, P_NZERO,		BETWEEN(RDA_MIN_PERIOD, RDA_MAX_PERIOD), null),
			"stime",			array(T_RDA_STR, O_OPT, P_SYS,		null,		null),
			"profileIdx",		array(T_RDA_STR, O_OPT, null,		null,		null),
			"profileIdx2",		array(T_RDA_STR, O_OPT, null,		null,		null),
			"updateProfile",	array(T_RDA_STR, O_OPT, null,		null,		null),
			"border",			array(T_RDA_INT, O_OPT, P_NZERO,		IN("0,1"),	null),
			"width",			array(T_RDA_INT, O_OPT, P_NZERO,		"{}>0",		null),
			"height",			array(T_RDA_INT, O_OPT, P_NZERO,		"{}>0",		null),
			"graph3d",			array(T_RDA_INT, O_OPT, P_NZERO,		IN("0,1"),		null),
			"legend",			array(T_RDA_INT, O_OPT, P_NZERO,		IN("0,1"),		null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		/* Permissions */
		CGraphGet goptions = new CGraphGet();
		goptions.setGraphIds(Nest.value($_REQUEST(),"graphid").asLong());
		goptions.setSelectHosts(API_OUTPUT_EXTEND);
		goptions.setOutput(API_OUTPUT_EXTEND);
		Map _dbGraph = API.Graph(getIdentityBean(), executor).get(goptions);
		if (empty(_dbGraph)) {
			access_deny();
		} else {
			_dbGraph = reset(_dbGraph);
		}
		
		Map _host = reset((CArray)_dbGraph.get("hosts"));

		/* Display */
		Map _timeline = CScreenBase.calculateTime(getIdentityBean(), executor, map(
			"profileIdx", get_request("profileIdx", "web.screens"),
			"profileIdx2", get_request("profileIdx2"),
			"updateProfile", get_request("updateProfile", true),
			"period", get_request("period"),
			"stime", get_request("stime")
		));

		CPieGraphDraw _graph = new CPieGraphDraw(getIdentityBean(), executor, Nest.value(_dbGraph,"graphtype").asInteger());
		_graph.setPeriod(Nest.value(_timeline,"period").asInteger());
		_graph.setSTime(Nest.value(_timeline,"stime").asLong());

		if (isset(Nest.value($_REQUEST(),"border").$())) {
			_graph.setBorder(0);
		}

		int _width = get_request("width", 0);
		if (_width <= 0) {
			_width = Nest.value(_dbGraph,"width").asInteger();
		}

		int _height = get_request("height", 0);
		if (_height <= 0) {
			_height = Nest.value(_dbGraph,"height").asInteger();
		}

		_graph.setWidth(_width);
		_graph.setHeight(_height);
		_graph.setHeader(_host.get("name")+NAME_DELIMITER+Nest.value(_dbGraph,"name").$());

		if (Nest.value(_dbGraph,"show_3d").asBoolean()) {
			_graph.switchPie3D();
		}
		_graph.showLegend(Nest.value(_dbGraph,"show_legend").asBoolean());

		Map params = new HashMap();
		params.put("graphid", Nest.value(_dbGraph,"graphid").$());
		CArray<Map> _result = DBselect(executor,
			"SELECT gi.*"+
			" FROM graphs_items gi"+
			" WHERE gi.graphid=#{graphid}"+
			" ORDER BY gi.sortorder,gi.itemid DESC",
			params
		);
		for (Map dbGraph: _result) {
			_graph.addItem(
				Nest.value(dbGraph,"itemid").asString(),
				Nest.value(dbGraph,"calc_fnc").asInteger(),
				Nest.value(dbGraph,"color").asString(),
				Nest.value(dbGraph,"type").asString()
			);
		}
	
		try {
			byte[] bs = _graph.draw();
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
