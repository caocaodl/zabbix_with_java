package com.isoft.iradar.web.action.core;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.Defines.ACCESS_DENY_ABSENCE_ITEM;
import static com.isoft.iradar.inc.Defines.CALC_FNC_ALL;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_DEFAULT;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
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

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.graphdraw.CLineGraphDraw;
import com.isoft.iradar.tags.screens.CScreenBase;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
//import static Configutil

public class ChartAction extends RadarBaseAction {
	@Override
	protected void doInitPage() {
		_page("file", "chart.action");
		_page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"itemid"  ,			array(T_RDA_INT, O_MAND, P_SYS,	DB_ID,		null),
			"period"  ,			array(T_RDA_INT, O_OPT, P_NZERO, BETWEEN(RDA_MIN_PERIOD, RDA_MAX_PERIOD), null),
			"stime"  ,			array(T_RDA_STR, O_OPT, P_SYS,	null,		null),
			"profileIdx"  ,		array(T_RDA_STR, O_OPT, null,	null,		null),
			"profileIdx2"  ,	array(T_RDA_STR, O_OPT, null,	null,		null),
			"updateProfile"  ,	array(T_RDA_STR, O_OPT, null,	null,		null),
			"from"  ,			array(T_RDA_INT, O_OPT, null,	"{}>=0",	null),
			"width"  ,			array(T_RDA_INT, O_OPT, null,	"{}>0",		null),
			"height"  ,			array(T_RDA_INT, O_OPT, null,	"{}>0",		null),
			"border"  ,			array(T_RDA_INT, O_OPT, null,	IN("0,1"),	null)
		);
		check_fields(getIdentityBean(), fields);
	}
	
	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		CItemGet params = new CItemGet();
		params.setOutput("itemid");
		params.setItemIds(Nest.array(_REQUEST,"itemid").asLong());
		params.setWebItems(true);
		
		CArray _dbItems = API.Item(getIdentityBean(), executor).get(params);
		if (empty(_dbItems)) {
			access_deny(ACCESS_DENY_ABSENCE_ITEM);
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		/* Display */
		Map _timeline = CScreenBase.calculateTime(getIdentityBean(), executor, map(
			"profileIdx" , get_request("profileIdx", "web.screens"),
			"profileIdx2" , get_request("profileIdx2"),
			"updateProfile" , get_request("updateProfile", true),
			"period" , get_request("period"),
			"stime" , get_request("stime")
		));

		CLineGraphDraw _graph = new CLineGraphDraw(getIdentityBean(), executor);
		_graph.setPeriod(Nest.value(_timeline,"period").asInteger());
		_graph.setSTime(Nest.value(_timeline,"stime").asLong());

		if (isset(Nest.value(_REQUEST,"from").$())) {
			_graph.setFrom(Nest.value(_REQUEST,"from").asInteger());
		}
		if (isset(Nest.value(_REQUEST,"width").$())) {
			_graph.setWidth(Nest.value(_REQUEST,"width").asInteger());
		}
		if (isset(Nest.value(_REQUEST,"height").$())) {
			_graph.setHeight(Nest.value(_REQUEST,"height").asInteger());
		}
		if (isset(Nest.value(_REQUEST,"border").$())) {
			_graph.setBorder(0);
		}
		_graph.addItem(Nest.value(_REQUEST,"itemid").asString(), GRAPH_YAXIS_SIDE_DEFAULT, CALC_FNC_ALL);
		try {
			byte[] bs = _graph.draw();
			this.getResponse().getOutputStream().write(bs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
