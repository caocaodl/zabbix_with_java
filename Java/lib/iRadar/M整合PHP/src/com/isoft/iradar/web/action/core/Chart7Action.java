package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.core.utils.EasyObject.asBoolean;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SUM;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.P_NZERO;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PERIOD;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.asort_by_key;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.GraphsUtil.navigation_bar_calc;
import static com.isoft.iradar.inc.ValidateUtil.BETWEEN;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.tags.graphdraw.CPieGraphDraw;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Chart7Action extends RadarBaseAction {
	
	private boolean isDataValid;
	private CArray<Map> items;
	
	@Override
	protected void doInitPage() {
		_page("file", "chart7.action");
		_page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"period"  ,		array(T_RDA_INT, O_OPT, P_NZERO,	BETWEEN(RDA_MIN_PERIOD, RDA_MAX_PERIOD), null),
			"from"  ,		array(T_RDA_INT, O_OPT, P_NZERO,	null,				null),
			"stime"  ,		array(T_RDA_INT, O_OPT, P_NZERO,	null,				null),
			"border"  ,		array(T_RDA_INT, O_OPT, P_NZERO,	IN("0,1"),			null),
			"name"  ,		array(T_RDA_STR, O_OPT, null,		null,				null),
			"width"  ,		array(T_RDA_INT, O_OPT, null,		BETWEEN(0, 65535),	null),
			"height"  ,		array(T_RDA_INT, O_OPT, null,		BETWEEN(0, 65535),	null),
			"graphtype"  ,	array(T_RDA_INT, O_OPT, null,		IN("2,3"),			null),
			"graph3d"  ,	array(T_RDA_INT, O_OPT, P_NZERO,	IN("0,1"),			null),
			"legend"  ,		array(T_RDA_INT, O_OPT, P_NZERO,	IN("0,1"),			null),
			"items"  ,		array(T_RDA_STR, O_OPT, null,		null,				null)
		);
		isDataValid = check_fields(getIdentityBean(), fields);

		items = get_request("items", array());
		asort_by_key(items, "sortorder");
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/* Permissions */
		CItemGet params = new CItemGet();
		params.setItemIds(rda_objectValues(items, "itemid").valuesAsLong());
		params.setFilter("flags", new Integer[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_PROTOTYPE, RDA_FLAG_DISCOVERY_CREATED});
		params.setOutput(array("itemid"));
		params.setWebItems(true);
		params.setPreserveKeys(true);
		
		CArray<Map> _dbItems = API.Item(getIdentityBean(), executor).get(params);

		for(Map _item: items) {
			if (!isset(_dbItems, _item.get("itemid"))) {
				access_deny();
			}
		}
		
		/*
		 * Validation
		 */
		CArray _types = array();
		for(Map _item: items) {
			if (Nest.value(_item,"type").asInteger() == GRAPH_ITEM_SUM) {
				if (!in_array(Nest.value(_item,"type").$(), _types)) {
					array_push(_types, Nest.value(_item,"type").$());
				}
				else {
					show_error_message(_("Cannot display more than one item with type \"Graph sum\"."));
					break;
				}
			}
		}
	}

	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		/*
		 * Display
		 */
		if (isDataValid) {
			navigation_bar_calc(getIdentityBean(), executor);

			CPieGraphDraw _graph = new CPieGraphDraw(getIdentityBean(), executor, get_request("graphtype", GRAPH_TYPE_NORMAL));
			_graph.setHeader(get_request("name", ""));

			if (!empty(Nest.value(_REQUEST,"graph3d").$())) {
				_graph.switchPie3D();
			}
			_graph.showLegend(asBoolean(get_request("legend", 0)));

			if (isset(Nest.value(_REQUEST,"period").$())) {
				_graph.setPeriod(Nest.value(_REQUEST,"period").asInteger());
			}
			if (isset(Nest.value(_REQUEST,"from").$())) {
				_graph.setFrom(Nest.value(_REQUEST,"from").asInteger());
			}
			if (isset(Nest.value(_REQUEST,"stime").$())) {
				_graph.setSTime(Nest.value(_REQUEST,"stime").asLong());
			}
			if (isset(Nest.value(_REQUEST,"border").$())) {
				_graph.setBorder(0);
			}
			_graph.setWidth(get_request("width", 400));
			_graph.setHeight(get_request("height", 300));

			for(Map _item: items) {
				_graph.addItem(Nest.value(_item,"itemid").asString(), Nest.value(_item,"calc_fnc").asInteger(), Nest.value(_item,"color").asString(), Nest.value(_item,"type").asString());
			}
			
			try {
				byte[] bs = _graph.draw();
				this.getResponse().getOutputStream().write(bs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		
	}
	
}
