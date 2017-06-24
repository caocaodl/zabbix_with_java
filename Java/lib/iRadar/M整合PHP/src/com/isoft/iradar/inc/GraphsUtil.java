package com.isoft.iradar.inc;

import static com.isoft.iradar.Cgd.IMG_COLOR_STYLED;
import static com.isoft.iradar.Cgd.IMG_COLOR_TRANSPARENT;
import static com.isoft.iradar.Cgd.imagefilledpolygon;
import static com.isoft.iradar.Cgd.imagepolygon;
import static com.isoft.iradar.Cgd.imagesetstyle;
import static com.isoft.iradar.Cgd.imagettfbbox;
import static com.isoft.iradar.Cgd.imagettftext;
import static com.isoft.iradar.Cphp.$_REQUEST;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.bcdiv;
import static com.isoft.iradar.Cphp.bcmul;
import static com.isoft.iradar.Cphp.bcpow;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.getdate;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asFloat;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.API_OUTPUT_EXTEND;
import static com.isoft.iradar.inc.Defines.CALC_FNC_ALL;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_LST;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_BOLD_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_DASHED_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_DOT;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_FILLED_REGION;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SIMPLE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SUM;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_DEFAULT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_LOG;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FONTPATH;
import static com.isoft.iradar.inc.Defines.RDA_FONT_NAME;
import static com.isoft.iradar.inc.Defines.RDA_GRAPH_FONT_NAME;
import static com.isoft.iradar.inc.Defines.RDA_MAX_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_MIN_PERIOD;
import static com.isoft.iradar.inc.Defines.RDA_PERIOD_DEFAULT;
import static com.isoft.iradar.inc.Defines.RDA_PRECISION_10;
import static com.isoft.iradar.inc.Defines.RDA_PREG_DEF_FONT_STRING;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_LOWER_LIMIT;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_YEAR;
import static com.isoft.iradar.inc.Defines.TIMESTAMP_FORMAT;
import static com.isoft.iradar.inc.DrawUtil.rda_imageline;
import static com.isoft.iradar.inc.FuncsUtil.error;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.info;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_array_merge;
import static com.isoft.iradar.inc.FuncsUtil.show_message;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_same_item_for_host;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cgd;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.api.API;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.params.CGraphGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TArray;
import com.isoft.types.Mapper.TObj;

public class GraphsUtil {
	
	public static <T> T graphType() {
		return (T)graphType(null);
	}
	
	public static <T> T graphType(Object type) {
		CArray types = map(
			GRAPH_TYPE_NORMAL, _("Normal Graph"),
			GRAPH_TYPE_STACKED, _("Stacked"),
			GRAPH_TYPE_PIE, _("Pie"),
			GRAPH_TYPE_EXPLODED, _("Exploded")
		);
	
		if (is_null(type)) {
			return (T) types;
		} else if (isset(types.get(type))) {
			return (T) types.get(type);
		} else {
			return (T) _("Unknown");
		}
	}
	
	public static String graph_item_type2str(int type) {
		switch (type) {
		case GRAPH_ITEM_SUM:
			return _("Graph sum");
		case GRAPH_ITEM_SIMPLE:
		default:
			return _("Simple");
		}
	}
	
	public static CArray graph_item_drawtypes() {
		return array(
			GRAPH_ITEM_DRAWTYPE_LINE,
			GRAPH_ITEM_DRAWTYPE_FILLED_REGION,
			GRAPH_ITEM_DRAWTYPE_BOLD_LINE,
			GRAPH_ITEM_DRAWTYPE_DOT,
			GRAPH_ITEM_DRAWTYPE_DASHED_LINE,
			GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE
		);
	}
	
	public static String graph_item_drawtype2str(int drawtype) {
		switch (drawtype) {
			case GRAPH_ITEM_DRAWTYPE_LINE:
				return _("Line");
			case GRAPH_ITEM_DRAWTYPE_FILLED_REGION:
				return _("Filled region");
			case GRAPH_ITEM_DRAWTYPE_BOLD_LINE:
				return _("Bold line");
			case GRAPH_ITEM_DRAWTYPE_DOT:
				return _("Dot");
			case GRAPH_ITEM_DRAWTYPE_DASHED_LINE:
				return _("Dashed line");
			case GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE:
				return _("Gradient line");
			default:
				return _("Unknown");
		}
	}
	
	public static String graph_item_calc_fnc2str(int calc_fnc) {
		switch (calc_fnc) {
			case 0:
				return _("Count");
			case CALC_FNC_ALL:
				return _("all");
			case CALC_FNC_MIN:
				return _("min");
			case CALC_FNC_MAX:
				return _("max");
			case CALC_FNC_LST:
				return _("last");
			case CALC_FNC_AVG:
			default:
				return _("avg");
		}
	}
	
	public static Map getGraphDims(IIdentityBean idBean, SQLExecutor executor) {
		return getGraphDims(idBean, executor, null);
	}
	
	public static Map getGraphDims(IIdentityBean idBean, SQLExecutor executor, Object graphid) {
		Map graphDims = array();
	
		Nest.value(graphDims,"shiftYtop").$(35);
		if (is_null(graphid)) {
			Nest.value(graphDims,"graphHeight").$(200);
			Nest.value(graphDims,"graphtype").$(0);
	
			if (GRAPH_YAXIS_SIDE_DEFAULT == 0) {
				Nest.value(graphDims,"shiftXleft").$(85) ;
				Nest.value(graphDims,"shiftXright").$(30);
			} else {
				Nest.value(graphDims,"shiftXleft").$(30);
				Nest.value(graphDims,"shiftXright").$(85);
			}
	
			return graphDims;
		}
	
		// zoom featers
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbGraphs = DBselect(executor,
			"SELECT MAX(g.graphtype) AS graphtype,MIN(gi.yaxisside) AS yaxissidel,MAX(gi.yaxisside) AS yaxissider,MAX(g.height) AS height"+
			" FROM graphs g,graphs_items gi"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "graphs", "g")+
				" AND g.graphid="+sqlParts.marshalParam(graphid)+
			    " AND gi.tenantid=g.tenantid"+
				" AND gi.graphid=g.graphid",
			sqlParts.getNamedParams()
		);
		int yaxis = 0;
		Map graph = DBfetch(dbGraphs);
		if (!empty(graph)) {
			yaxis = Nest.value(graph,"yaxissider").asInteger();
			yaxis = (Nest.value(graph,"yaxissidel").asInteger() == yaxis) ? yaxis : 2;
	
			Nest.value(graphDims,"yaxis").$(yaxis);
			Nest.value(graphDims,"graphtype").$(Nest.value(graph,"graphtype").$());
			Nest.value(graphDims,"graphHeight").$(Nest.value(graph,"height").$());
		}
	
		if (yaxis == 2) {
			Nest.value(graphDims,"shiftXleft").$(85);
			Nest.value(graphDims,"shiftXright").$(85);
		} else if (yaxis == 0) {
			Nest.value(graphDims,"shiftXleft").$(85);
			Nest.value(graphDims,"shiftXright").$(30);
		} else {
			Nest.value(graphDims,"shiftXleft").$(30);
			Nest.value(graphDims,"shiftXright").$(85);
		}
	
		return graphDims;
	}
	
	public static CArray get_graphs_by_hostid(IIdentityBean idBean, SQLExecutor executor, Object hostid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor,
			"SELECT DISTINCT g.*"+
			" FROM graphs g,graphs_items gi,items i"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "graphs", "g")+
			    " AND g.tenantid=gi.tenantid"+
			    " AND g.graphid=gi.graphid"+
				" AND gi.tenantid=i.tenantid"+
				" AND gi.itemid=i.itemid"+
				" AND i.hostid="+sqlParts.marshalParam(hostid),
			sqlParts.getNamedParams()
		);
	}
	
	public static CArray<Map> get_realhosts_by_graphid(IIdentityBean idBean, SQLExecutor executor, long graphid) {
		Map graph = getGraphByGraphId(idBean, executor, graphid);
		if (!empty(Nest.value(graph,"templateid").$())) {
			return get_realhosts_by_graphid(idBean, executor, Nest.value(graph,"templateid").asLong());
		}
		return get_hosts_by_graphid(idBean, executor, graphid);
	}
	
	public static CArray<Map> get_hosts_by_graphid(IIdentityBean idBean, SQLExecutor executor, long graphid) {
		SqlBuilder sqlParts = new SqlBuilder();
		return DBselect(executor,
			"SELECT DISTINCT h.*"+
			" FROM graphs_items gi,items i,hosts h"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
			    " AND h.tenantid=i.tenantid"+
			    " AND h.hostid=i.hostid"+
				" AND gi.tenantid=i.tenantid"+
				" AND gi.itemid=i.itemid"+
				" AND gi.graphid="+sqlParts.marshalParam(graphid),
			sqlParts.getNamedParams()
		);
	}
	
	/**
	 * Description:
	 *	Return the time of the 1st appearance of items included in graph in trends
	 * Comment:
	 *	sql is split to many sql's to optimize search on history tables
	 */
	public static Long get_min_itemclock_by_graphid(IIdentityBean idBean, SQLExecutor executor, Object graphid) {
		CArray itemids = array();
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbItems = DBselect(executor,
			"SELECT DISTINCT gi.itemid"+
			" FROM graphs_items gi"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "graphs_items", "gi")+
			    " AND gi.graphid="+sqlParts.marshalParam(graphid),
			sqlParts.getNamedParams()
		);
		for (Map item: dbItems) {
			itemids.put(item.get("itemid"), Nest.value(item,"itemid").$());
		}	
		return get_min_itemclock_by_itemid(idBean, executor, itemids.valuesAsString());
	}
	
	/**
	 * Return the time of the 1st appearance of item in trends.
	 *
	 * @param array itemIds
	 *
	 * @return int (unixtime)
	 */
	public static Long get_min_itemclock_by_itemid(IIdentityBean idBean, SQLExecutor executor, String... itemIds) {
	
		Long min = null;
		Long result = time() - SEC_PER_YEAR;
	
		CArray<CArray> itemTypes = map(
			ITEM_VALUE_TYPE_FLOAT, array(),
			ITEM_VALUE_TYPE_STR, array(),
			ITEM_VALUE_TYPE_LOG, array(),
			ITEM_VALUE_TYPE_UINT64, array(),
			ITEM_VALUE_TYPE_TEXT, array()
		);
	
		SqlBuilder sqlParts = new SqlBuilder();
		sqlParts.select.put("i.itemid,i.value_type");
		sqlParts.from.put("items i");
		sqlParts.where.dbConditionTenants(idBean, "items", "i");
		sqlParts.where.dbConditionInt("i.itemid", TArray.as(itemIds).asLong());
		
		CArray<Map> dbItems = DBselect(executor, sqlParts);
	
		for (Map _item:dbItems) {
			itemTypes.put(_item.get("value_type"), _item.get("itemid"), Nest.value(_item,"itemid").$());
		}
	
		// data for ITEM_VALUE_TYPE_FLOAT and ITEM_VALUE_TYPE_UINT64 can be stored in trends tables or history table
		// get max trends and history values for such type items to find out in what tables to look for data
		String _sqlFrom = "history";
		String _sqlFromNum = "";
	
		if (!empty(itemTypes.get(ITEM_VALUE_TYPE_FLOAT)) || !empty(itemTypes.get(ITEM_VALUE_TYPE_UINT64))) {
			CArray _itemIdsNumeric = rda_array_merge(itemTypes.get(ITEM_VALUE_TYPE_FLOAT), itemTypes.get(ITEM_VALUE_TYPE_UINT64));
	
			sqlParts = new SqlBuilder();
			sqlParts.select.put("MAX(i.history) AS history,MAX(i.trends) AS trends");
			sqlParts.from.put("items i");
			sqlParts.where.dbConditionTenants(idBean, "items", "i");
			sqlParts.where.dbConditionInt("i.itemid", _itemIdsNumeric.valuesAsLong());
			
			Map _tableForNumeric = DBfetch(DBselect(executor, sqlParts));
			if (!empty(_tableForNumeric)) {
				_sqlFromNum = (Nest.value(_tableForNumeric,"history").asInteger() > Nest.value(_tableForNumeric,"trends").asInteger()) ? "history" : "trends";
				result = time() - (SEC_PER_DAY * Math.max(Nest.value(_tableForNumeric,"history").asInteger(), Nest.value(_tableForNumeric,"trends").asInteger()));
			}
		}

		for(Entry<Object, CArray> entry: itemTypes.entrySet()) {
			int _type = asInteger(entry.getKey());
			CArray _items = entry.getValue();
			
			if (empty(_items)) {
				continue;
			}
	
			switch (_type) {
				case ITEM_VALUE_TYPE_FLOAT:
					_sqlFrom = _sqlFromNum;
					break;
				case ITEM_VALUE_TYPE_STR:
					_sqlFrom = "history_str";
					break;
				case ITEM_VALUE_TYPE_LOG:
					_sqlFrom = "history_log";
					break;
				case ITEM_VALUE_TYPE_UINT64:
					_sqlFrom = _sqlFromNum+"_uint";
					break;
				case ITEM_VALUE_TYPE_TEXT:
					_sqlFrom = "history_text";
					break;
				default:
					_sqlFrom = "history";
			}
	
			CArray<String> _sqlUnions = array();
			sqlParts = new SqlBuilder();
			for(String _itemId: itemIds) {
				_sqlUnions.add("SELECT MIN(ht.clock) AS c FROM "+_sqlFrom+" ht "+
						" WHERE "+sqlParts.dual.dbConditionTenants(idBean, _sqlFrom, "ht")+
						   " AND ht.itemid="+sqlParts.marshalParam(_itemId));
			}
	
			Map _dbMin = DBfetch(DBselect(executor,
				"SELECT MIN(ht.c) AS min_clock"+
				" FROM ("+implode(" UNION ALL ", _sqlUnions)+") ht",
				sqlParts.getNamedParams()
			));
	
			min = !empty(min) ? Math.min(min, Nest.value(_dbMin,"min_clock").asInteger()) : Nest.value(_dbMin,"min_clock").asInteger();
		}
	
		return !empty(min) ? min: result;
	}
	
	public static Map getGraphByGraphId(IIdentityBean idBean, SQLExecutor executor, long graphId) {
		SqlBuilder sqlParts = new SqlBuilder();
		Map dbGraph = DBfetch(DBselect(executor, "SELECT g.* FROM graphs g "+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "graphs", "g")+
				" AND g.graphid="+sqlParts.marshalParam(graphId),
				sqlParts.getNamedParams()));
			if (!empty(dbGraph)) {
			return dbGraph;
		}
		error(_s("No graph item with graphid \"%s\".", graphId));
		return null;
	}
	
	
	/**
	 * Search items by same key in destination host.
	 *
	 * @param array  gitems
	 * @param string destinationHostId
	 * @param bool   error					if false error won't be thrown when item does not exist
	 * @param array  flags
	 *
	 * @return array|bool
	 */
	public static CArray<Map> getSameGraphItemsForHost(IIdentityBean idBean, SQLExecutor executor, CArray<Map> gitems, Long _destinationHostId, boolean error, CArray flags) {
		CArray<Map> result = array();
	
		for(Map gitem: gitems) {
			SqlBuilder sqlParts = new SqlBuilder();
			sqlParts.select.put("dest.itemid,src.key_");
			sqlParts.from.put("items dest,items src");
			sqlParts.where.dbConditionTenants(idBean, "items", "dest");
			sqlParts.where.put("dest.key_=src.key_");
//			sqlParts.where.put("dest.tenantid=src.tenantid");  //因租户的虚拟机会使用运营商的模型，所以这里删除
			sqlParts.where.put("dest.hostid="+sqlParts.marshalParam(_destinationHostId));
			sqlParts.where.put("src.itemid="+sqlParts.marshalParam(Nest.value(gitem,"itemid").$()));
			if(!empty(flags)) {
				sqlParts.where.dbConditionInt("dest.flags", flags.valuesAsLong());
			}
			
			Map dbItem = DBfetch(DBselect(executor,sqlParts));
			if (!empty(dbItem)) {
				Nest.value(gitem,"itemid").$(Nest.value(dbItem,"itemid").$());
				Nest.value(gitem,"key_").$(Nest.value(dbItem,"key_").$());
			} else if (error) {
				Map item = get_item_by_itemid(executor, Nest.value(gitem,"itemid").asString());
				Map host = get_host_by_hostid(idBean, executor, _destinationHostId);
				error(_s("Missing key \"%1$s\" for host \"%2$s\".", Nest.value(item,"key_").$(), Nest.value(host,"host").$()));
				return null;
			} else {
				continue;
			}
			result.add(gitem);
		}
	
		return result;
	}
	
	public static CArray<Map> getSameGraphItemsForHost(IIdentityBean idBean, SQLExecutor executor, CArray<Map> gitems, Long destinationHostId, boolean error) {
		return getSameGraphItemsForHost(idBean, executor, gitems, destinationHostId, error, array());
	}
	
	public static CArray<Map> getSameGraphItemsForHost(IIdentityBean idBean, SQLExecutor executor, CArray<Map> gitems, Long destinationHostId) {
		return getSameGraphItemsForHost(idBean, executor, gitems, destinationHostId, true);
	}
	
	/**
	 * Copy specified graph to specified host.
	 *
	 * @param string graphId
	 * @param string hostId
	 *
	 * @return array
	 */
	public static CArray copyGraphToHost(IIdentityBean idBean, SQLExecutor executor, Long graphId, Long hostId) {
		CGraphGet goptions = new CGraphGet();
		goptions.setGraphIds(graphId);
		goptions.setOutput(API_OUTPUT_EXTEND);
		goptions.setSelectHosts(new String[] { "hostid", "name" });
		goptions.setSelectGraphItems(API_OUTPUT_EXTEND);
		CArray<Map> graphs = API.Graph(idBean, executor).get(goptions);
		Map graph = reset(graphs);
		Map graphHost = reset(Nest.value(graph,"hosts").asCArray());
	
		if (Cphp.equals(Nest.value(graphHost,"hostid").$(), hostId)) {
			error(_s("Graph \"%1$s\" already exists on \"%2$s\".", Nest.value(graph,"name").$(), Nest.value(graphHost,"name").$()));
				return null;
		}
	
		Nest.value(graph,"gitems").$(getSameGraphItemsForHost(idBean, executor, 
			Nest.value(graph,"gitems").asCArray(),
			hostId,
			true,
			array(RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED)
		));
	
		if (empty(Nest.value(graph,"gitems").$())) {
			Map host = get_host_by_hostid(idBean, executor, hostId);	
			info(_s("Skipped copying of graph \"%1$s\" to host \"%2$s\".", Nest.value(graph,"name").$(), Nest.value(host,"host").$()));
				return null;
		}
	
		// retrieve actual ymax_itemid and ymin_itemid
		Long itemId = get_same_item_for_host(executor, Nest.value(graph,"ymax_itemid").asLong(), hostId);
		if (Nest.value(graph,"ymax_itemid").asBoolean() && !empty(itemId)) {
			Nest.value(graph,"ymax_itemid").$(itemId);
		}
	
		itemId = get_same_item_for_host(executor, Nest.value(graph,"ymin_itemid").asLong(), hostId);
		if (Nest.value(graph,"ymin_itemid").asBoolean() && !empty(itemId)) {
			Nest.value(graph,"ymin_itemid").$(itemId);
		}
	
		unset(graph, "templateid");
	
		return API.Graph(idBean, executor).create(array(graph));
	}
	
	public static int navigation_bar_calc(IIdentityBean idBean, SQLExecutor executor) {
		return navigation_bar_calc(idBean, executor, null);
	}
	
	public static int navigation_bar_calc(IIdentityBean idBean, SQLExecutor executor, String idx) {
		return navigation_bar_calc(idBean, executor, idx, 0L);
	}
	
	public static int navigation_bar_calc(IIdentityBean idBean, SQLExecutor executor, String idx, Long idx2) {
		return navigation_bar_calc(idBean, executor, idx, idx2, false);
	}
	
	public static int navigation_bar_calc(IIdentityBean idBean, SQLExecutor executor, String idx, Long idx2, boolean update) {
		if (!empty(idx)) {
			if (update) {
				if (!empty(Nest.value($_REQUEST(),"period").$()) && Nest.value($_REQUEST(),"period").asInteger() >= RDA_MIN_PERIOD) {
					CProfile.update(idBean, executor, idx+".period", Nest.value($_REQUEST(),"period").$(), PROFILE_TYPE_INT, idx2);
				}
				if (!empty(Nest.value($_REQUEST(),"stime").$())) {
					CProfile.update(idBean, executor, idx+".stime", Nest.value($_REQUEST(),"stime").$(), PROFILE_TYPE_STR, idx2);
				}
			}
			Nest.value($_REQUEST(),"period").$(get_request("period", CProfile.get(idBean, executor, idx+".period", RDA_PERIOD_DEFAULT, idx2)));
			Nest.value($_REQUEST(),"stime").$(get_request("stime", CProfile.get(idBean, executor, idx+".stime", null, idx2)));
		}
	
		Nest.value($_REQUEST(),"period").$(get_request("period", RDA_PERIOD_DEFAULT));
		Nest.value($_REQUEST(),"stime").$(get_request("stime", null));
	
		if (Nest.value($_REQUEST(),"period").asInteger() < RDA_MIN_PERIOD) {
			show_message(_n("Minimum time period to display is %1$s hour.",
				"Minimum time period to display is %1$s hours.",
				(int) RDA_MIN_PERIOD / SEC_PER_HOUR
			));
			Nest.value($_REQUEST(),"period").$(RDA_MIN_PERIOD);
		} else if (Nest.value($_REQUEST(),"period").asInteger() > RDA_MAX_PERIOD) {
			show_message(_n("Maximum time period to display is %1$s day.",
				"Maximum time period to display is %1$s days.",
				(int) RDA_MAX_PERIOD / SEC_PER_DAY
			));
			Nest.value($_REQUEST(),"period").$(RDA_MAX_PERIOD);
		}
	
		if (!empty(Nest.value($_REQUEST(),"stime").$())) {
			long _time = rdaDateToTime(Nest.value($_REQUEST(),"stime").asString());
			if ((_time + Nest.value($_REQUEST(),"period").asInteger()) > time()) {
				Nest.value($_REQUEST(),"stime").$(date(TIMESTAMP_FORMAT, time() - Nest.value($_REQUEST(),"period").asInteger()));
			}
		} else {
			Nest.value($_REQUEST(),"stime").$(date(TIMESTAMP_FORMAT, time() - Nest.value($_REQUEST(),"period").asInteger()));
		}
	
		return Nest.value($_REQUEST(),"period").asInteger();
	}
	
	public static CArray<Integer> get_next_color() {
		return get_next_color(0);
	}
	
	public static CArray<Integer> get_next_color(int palettetype) {
		CArray prev_color = map("dark", true, "color", 0, "grad", 0);

		CArray grad;
		switch (palettetype) {
			case 1:
				grad = array(200, 150, 255, 100, 50, 0);
				break;
			case 2:
				grad = array(100, 50, 200, 150, 250, 0);
				break;
			case 0:
			default:
				grad = array(255, 200, 150, 100, 50, 0);
				break;
		}

		int set_grad = (Integer)grad.get(prev_color.get("grad"));

		int r, g, b;
		r = g = b = (100 < set_grad) ? 0 : 255;
		

		switch (Nest.value(prev_color,"color").asInteger()) {
			case 0:
				r = set_grad;
				break;
			case 1:
				g = set_grad;
				break;
			case 2:
				b = set_grad;
				break;
			case 3:
				r = b = set_grad;
				break;
			case 4:
				g = b = set_grad;
				break;
			case 5:
				r = g = set_grad;
				break;
			case 6:
				r = g = b = set_grad;
				break;
		}

		Nest.value(prev_color,"dark").$(!Nest.value(prev_color,"dark").asBoolean());
		if (Nest.value(prev_color,"color").asInteger() == 6) {
			Nest.value(prev_color,"grad").$((Nest.value(prev_color,"grad").asInteger() + 1) % 6);
		}
		Nest.value(prev_color,"color").$((Nest.value(prev_color,"color").asInteger() + 1) % 7);

		return array(r, g, b);
	}
	
	public static CArray<Integer> get_next_palette() {
		return get_next_palette(0);
	}
	
	public static CArray<Integer> get_next_palette(int palette) {
		return get_next_palette(palette, 0);
	}
	
	public static CArray<Integer> get_next_palette(int palette, int palettetype) {
		CArray prev_color = array(0, 0, 0, 0);

		CArray palettes;
		switch (palette) {
			case 0:
				palettes = array(
					array(150, 0, 0), array(0, 100, 150), array(170, 180, 180), array(152, 100, 0), array(130, 0, 150),
					array(0, 0, 150), array(200, 100, 50), array(250, 40, 40), array(50, 150, 150), array(100, 150, 0)
				);
				break;
			case 1:
				palettes = array(
					array(0, 100, 150), array(153, 0, 30), array(100, 150, 0), array(130, 0, 150), array(0, 0, 100),
					array(200, 100, 50), array(152, 100, 0), array(0, 100, 0), array(170, 180, 180), array(50, 150, 150)
				);
				break;
			case 2:
				palettes = array(
					array(170, 180, 180), array(152, 100, 0), array(50, 200, 200), array(153, 0, 30), array(0, 0, 100),
					array(100, 150, 0), array(130, 0, 150), array(0, 100, 150), array(200, 100, 50), array(0, 100, 0)
				);
				break;
			case 3:
			default:
				return get_next_color(palettetype);
		}

		CArray<Integer> result;
		
		if (isset(palettes, prev_color.get(palette))) {
			result = (CArray)palettes.get(prev_color.get(palette));
		}
		else {
			return get_next_color(palettetype);
		}

		int diff = 0;
		switch (palettetype) {
			case 0:
				diff = 0;
				break;
			case 1:
				diff = -50;
				break;
			case 2:
				diff = 50;
				break;
		}

		for(Entry<Object, Integer> entry: result.entrySet()) {
			Object n = entry.getKey();
			Integer _color = entry.getValue();
		
			if ((_color + diff) < 0) {
				result.put(n, 0);
			}
			else if ((_color + diff) > 255) {
				result.put(n, 255);
			}
			else {
				Nest.value(result, n).plus(diff);
			}
		}
		
		Nest.value(prev_color, palette).plus(1);

		return result;
	}
	
	
	public static void imageDiagonalMarks(Graphics2D im, int x, int y, int offset, Color color, CArray colors) {
		CArray<CArray<Integer>> gims = map(
			"lt", array(0, 0, -9, 0, -9, -3, -3, -9, 0, -9),
			"rt", array(0, 0, 9, 0, 9, -3, 3,-9, 0, -9),
			"lb", array(0, 0, -9, 0, -9, 3, -3, 9, 0, 9),
			"rb", array(0, 0, 9, 0, 9, 3, 3, 9, 0, 9)
		);
		
		for(Entry<Object,Integer> entry: gims.get("lt").entrySet()) {
			Integer num = (Integer)entry.getKey();
			Integer px = entry.getValue();

			if ((num % 2) == 0) {
				Nest.value(gims, "lt", num).$(px + x - offset);
			} else {
				Nest.value(gims, "lt", num).$(px + y - offset);
			}
		}
		
		for(Entry<Object,Integer> entry: gims.get("rt").entrySet()) {
			Integer num = (Integer)entry.getKey();
			Integer px = entry.getValue();

			if ((num % 2) == 0) {
				Nest.value(gims, "rt", num).$(px + x - offset);
			} else {
				Nest.value(gims, "rt", num).$(px + y - offset);
			}
		}
		
		for(Entry<Object,Integer> entry: gims.get("lb").entrySet()) {
			Integer num = (Integer)entry.getKey();
			Integer px = entry.getValue();

			if ((num % 2) == 0) {
				Nest.value(gims, "lb", num).$(px + x - offset);
			} else {
				Nest.value(gims, "lb", num).$(px + y - offset);
			}
		}
		
		for(Entry<Object,Integer> entry: gims.get("rb").entrySet()) {
			Integer num = (Integer)entry.getKey();
			Integer px = entry.getValue();

			if ((num % 2) == 0) {
				Nest.value(gims, "rb", num).$(px + x - offset);
			} else {
				Nest.value(gims, "rb", num).$(px + y - offset);
			}
		}

		imagefilledpolygon(im, Nest.value(gims,"lt").asCArray(), 5, color);
		imagepolygon(im, Nest.value(gims,"lt").asCArray(), 5, Nest.value(colors, "Dark Red").asInteger());

		imagefilledpolygon(im, Nest.value(gims,"rt").asCArray(), 5, color);
		imagepolygon(im, Nest.value(gims,"rt").asCArray(), 5, Nest.value(colors, "Dark Red").asInteger());

		imagefilledpolygon(im, Nest.value(gims,"lb").asCArray(), 5, color);
		imagepolygon(im, Nest.value(gims,"lb").asCArray(), 5, Nest.value(colors, "Dark Red").asInteger());

		imagefilledpolygon(im, Nest.value(gims,"rb").asCArray(), 5, color);
		imagepolygon(im, Nest.value(gims,"rb").asCArray(), 5, Nest.value(colors, "Dark Red").asInteger());
	}
	
	/**
	 * Draw trigger recent change markers.
	 *
	 * @param resource im
	 * @param int      x
	 * @param int      y
	 * @param int      offset
	 * @param string   color
	 * @param string   marks	\"t\" - top, \"r\" - right, \"b\" - bottom, \"l\" - left
	 */
	public static void imageVerticalMarks(Graphics2D im, int x, int y, int offset, Color color, String marks, CArray colors) {
		int _polygons = 5;
		CArray<CArray<Integer>> _gims = map(
			"t", array(0, 0, -6, -6, -3, -9, 3, -9, 6, -6),
			"r", array(0, 0, 6, -6, 9, -3, 9, 3, 6, 6),
			"b", array(0, 0, 6, 6, 3, 9, -3, 9, -6, 6),
			"l", array(0, 0, -6, 6, -9, 3, -9, -3, -6, -6)
		);

		
		for(Entry<Object,Integer> entry: _gims.get("t").entrySet()) {
			Integer _num = (Integer)entry.getKey();
			Integer _px = entry.getValue();

			if ((_num % 2) == 0) {
				Nest.value(_gims, "t", _num).$(_px + x);
			}
			else {
				Nest.value(_gims, "t", _num).$(_px + y - offset);
			}
		}
		
		for(Entry<Object,Integer> entry: _gims.get("r").entrySet()) {
			Integer _num = (Integer)entry.getKey();
			Integer _px = entry.getValue();

			if ((_num % 2) == 0) {
				Nest.value(_gims, "r", _num).$(_px + x + offset);
			}
			else {
				Nest.value(_gims, "r", _num).$(_px + y);
			}
		}
		
		for(Entry<Object,Integer> entry: _gims.get("b").entrySet()) {
			Integer _num = (Integer)entry.getKey();
			Integer _px = entry.getValue();

			if ((_num % 2) == 0) {
				Nest.value(_gims, "b", _num).$(_px + x);
			}
			else {
				Nest.value(_gims, "b", _num).$(_px + y + offset);
			}
		}

		for(Entry<Object,Integer> entry: _gims.get("l").entrySet()) {
			Integer _num = (Integer)entry.getKey();
			Integer _px = entry.getValue();

			if ((_num % 2) == 0) {
				Nest.value(_gims, "l", _num).$(_px + x - offset);
			}
			else {
				Nest.value(_gims, "l", _num).$(_px + y);
			}
		}

		if (strpos(marks, "t") != -1) {
			imagefilledpolygon(im, Nest.value(_gims,"t").asCArray(), _polygons, color);
			imagepolygon(im, Nest.value(_gims,"t").asCArray(), _polygons, Nest.value(colors, "Dark Red").asInteger());
		}
		if (strpos(marks, "r") != -1) {
			imagefilledpolygon(im, Nest.value(_gims,"r").asCArray(), _polygons, color);
			imagepolygon(im, Nest.value(_gims,"r").asCArray(), _polygons, Nest.value(colors, "Dark Red").asInteger());
		}
		if (strpos(marks, "b") != -1) {
			imagefilledpolygon(im, Nest.value(_gims,"b").asCArray(), _polygons, color);
			imagepolygon(im, Nest.value(_gims,"b").asCArray(), _polygons, Nest.value(colors, "Dark Red").asInteger());
		}
		if (strpos(marks, "l") != -1) {
			imagefilledpolygon(im, Nest.value(_gims,"l").asCArray(), _polygons, color);
			imagepolygon(im, Nest.value(_gims,"l").asCArray(), _polygons, Nest.value(colors, "Dark Red").asInteger());
		}
	}
	
	/**
	 * Draws a text on an image. Supports TrueType fonts.
	 *
	 * @param resource 	_image
	 * @param int		_fontsize
	 * @param int 		_angle
	 * @param int		x
	 * @param int 		y
	 * @param int		_color		a numeric color identifier from imagecolorallocate() or imagecolorallocatealpha()
	 * @param string	_string
	 */
	public static void imageText(Graphics2D g2d, int _fontsize, int _angle, int x, int y, Color _color, String _string) {
		String _ttf;
		if ((preg_match(RDA_PREG_DEF_FONT_STRING, _string)>0 && _angle != 0) || RDA_FONT_NAME == RDA_GRAPH_FONT_NAME) {
			_ttf = RDA_FONTPATH+"/"+RDA_FONT_NAME+".ttf";
			imagettftext(g2d, _fontsize, _angle, x, y, _color, _ttf, _string);
		}
//		else if (_angle == 0) {
//			_ttf = RDA_FONTPATH+"/"+RDA_GRAPH_FONT_NAME+".ttf";
//			imagettftext(g2d, _fontsize, _angle, x, y, _color, _ttf, _string);
//		}
//		else {
//			_ttf = RDA_FONTPATH+"/"+RDA_GRAPH_FONT_NAME+".ttf";
//			CArray _size = imageTextSize(_fontsize, 0, _string);
//
//			_imgg = imagecreatetruecolor(Nest.value(_size,"width").asInteger() + 1, Nest.value(_size,"height").$());
//			_transparentColor = imagecolorallocatealpha(_imgg, 200, 200, 200, 127);
//			imagefill(_imgg, 0, 0, _transparentColor);
//			imagettftext(_imgg, _fontsize, 0, 0, Nest.value(_size,"height").$(), _color, _ttf, _string);
//
//			_imgg = imagerotate(_imgg, _angle, _transparentColor);
//			imagealphablending(_imgg, false);
//			imagesavealpha(_imgg, true);
//			imagecopy(_image, _imgg, x - Nest.value(_size,"height").$(), y - Nest.value(_size,"width").$(), 0, 0, Nest.value(_size,"height").$(), Nest.value(_size,"width").$() + 1);
//			imagedestroy(_imgg);
//		}
	}
	
	/**
	 * Calculates the size of the given string.
	 *
	 * Returns the following data:
	 * - height 	- height of the text;
	 * - width		- width of the text;
	 * - baseline	- baseline Y coordinate (can only be used for horizontal text, can be negative).
	 *
	 * @param int 		fontsize
	 * @param int 		angle
	 * @param string 	str
	 *
	 * @return array
	 */
	public static CArray<Double> imageTextSize(int fontsize, int angle, String str) {
		String ttf;
		if (preg_match(RDA_PREG_DEF_FONT_STRING, str)>0 && angle != 0) {
			ttf = RDA_FONTPATH+"/"+RDA_FONT_NAME+".ttf";
		}
		else {
			ttf = RDA_FONTPATH+"/"+RDA_GRAPH_FONT_NAME+".ttf";
		}
	
		Rectangle2D _ar = imagettfbbox(fontsize, angle, ttf, str);
	
		return map(
			"height", _ar.getHeight()-2,
			"width", _ar.getWidth(),
			"baseline", _ar.getMinY()
		);
	}
	
	public static void dashedLine(Graphics2D image, int x1, int y1, int x2, int y2, Object color) {
		// style for dashed lines
		if (!is_array(color)) {
			CArray style = array(color, color, IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT);
			imagesetstyle(image, style);
			rda_imageline(image, x1, y1, x2, y2, IMG_COLOR_STYLED);
		} else {
			CArray cs = CArray.valueOf(color);
			int maxLen = cs.size();
			
			Integer preC = null;
			int index = 0;
			int startIndex = 0;
			int endIndex = 0;
			for(Object co: cs) {
				int c;
				if(co instanceof Color) {
					c = Cgd.rgba((Color)co);
				}else {
					c = TObj.as(co).asInteger();
				}
				
				if(preC==null || preC!=c) {
					if(preC == null) {
						startIndex = index;
						preC = c;
					}else{
						endIndex = index;
						
						if(preC != IMG_COLOR_TRANSPARENT) {
							CArray style = CArray.array();
							for(int i=0; i<maxLen; i++) {
								if(i>=startIndex && i<endIndex) {
									style.add(preC);
								}else {
									style.add(IMG_COLOR_TRANSPARENT);
								}
							}
							imagesetstyle(image, style);
							rda_imageline(image, x1, y1, x2, y2, IMG_COLOR_STYLED);
						}
						
						startIndex = index;
						preC = c;
					}
				}
				
				index++;
			}
			
			if(preC != null) {
				endIndex = index;
				
				if(preC != IMG_COLOR_TRANSPARENT) {
					CArray style = CArray.array();
					for(int i=0; i<maxLen; i++) {
						if(i>=startIndex && i<endIndex) {
							style.add(preC);
						}else {
							style.add(IMG_COLOR_TRANSPARENT);
						}
					}
					imagesetstyle(image, style);
					rda_imageline(image, x1, y1, x2, y2, IMG_COLOR_STYLED);
				}
				
				preC = null;
			}
		}
	}
	
	public static void dashedRectangle(Graphics2D image, int x1, int y1, int x2, int y2, int _color) {
		dashedLine(image, x1, y1, x1, y2, _color);
		dashedLine(image, x1, y2, x2, y2, _color);
		dashedLine(image, x2, y2, x2, y1, _color);
		dashedLine(image, x2, y1, x1, y1, _color);
	}
	
	public static long find_period_start(CArray periods, long time) {
		CArray date = getdate(time);
		int wday = Nest.value(date,"wday").asInteger() == 0 ? 7 : Nest.value(date,"wday").asInteger();
		int curr = Nest.value(date,"hours").asInteger() * 100 + Nest.value(date,"minutes").asInteger();

		if (isset(periods, wday)) {
			int next_h = -1;
			int next_m = -1;
			for(Map _period: (CArray<Map>)Nest.value(periods, wday).asCArray()) {
				int per_start = Nest.value(_period,"start_h").asInteger() * 100 + Nest.value(_period,"start_m").asInteger();
				if (per_start > curr) {
					if ((next_h == -1 && next_m == -1) || (per_start < (next_h * 100 + next_m))) {
						next_h = Nest.value(_period,"start_h").asInteger();
						next_m = Nest.value(_period,"start_m").asInteger();
					}
					continue;
				}

				int per_end = Nest.value(_period,"end_h").asInteger() * 100 + Nest.value(_period,"end_m").asInteger();
				if (per_end <= curr) {
					continue;
				}
				return time;
			}

			if (next_h >= 0 && next_m >= 0) {
				return mktime(next_h, next_m, 0, Nest.value(date,"mon").asInteger(), Nest.value(date,"mday").asInteger(), Nest.value(date,"year").asInteger());
			}
		}

		for (int days = 1; days < 7 ; ++days) {
			int new_wday = (wday + days - 1) % 7 + 1;
			if (isset(periods, new_wday)) {
				int next_h = -1;
				int next_m = -1;

				for(Map period: (CArray<Map>)Nest.value(periods, new_wday).asCArray()) {
					int per_start = Nest.value(period,"start_h").asInteger() * 100 + Nest.value(period,"start_m").asInteger();

					if ((next_h == -1 && next_m == -1) || (per_start < (next_h * 100 + next_m))) {
						next_h = Nest.value(period,"start_h").asInteger();
						next_m = Nest.value(period,"start_m").asInteger();
					}
				}

				if (next_h >= 0 && next_m >= 0) {
					return mktime(next_h, next_m, 0, Nest.value(date,"mon").asInteger(), Nest.value(date,"mday").asInteger() + days, Nest.value(date,"year").asInteger());
				}
			}
		}
		return -1;
	}
	
	public static long find_period_end(CArray periods, long time, long max_time) {
		CArray date = getdate(time);
		int wday = Nest.value(date,"wday").asInteger() == 0 ? 7 : Nest.value(date,"wday").asInteger();
		int curr = Nest.value(date,"hours").asInteger() * 100 + Nest.value(date,"minutes").asInteger();

		if (isset(periods, wday)) {
			int next_h = -1;
			int next_m = -1;

			for(Map _period: (CArray<Map>)Nest.value(periods, wday).asCArray()) {
				int per_start = Nest.value(_period,"start_h").asInteger() * 100 + Nest.value(_period,"start_m").asInteger();
				int per_end = Nest.value(_period,"end_h").asInteger() * 100 + Nest.value(_period,"end_m").asInteger();
				if (per_start > curr) {
					continue;
				}
				if (per_end < curr) {
					continue;
				}

				if ((next_h == -1 && next_m == -1) || (per_end > (next_h * 100 + next_m))) {
					next_h = Nest.value(_period,"end_h").asInteger();
					next_m = Nest.value(_period,"end_m").asInteger();
				}
			}

			if (next_h >= 0 && next_m >= 0) {
				long new_time = mktime(next_h, next_m, 0, Nest.value(date,"mon").asInteger(), Nest.value(date,"mday").asInteger(), Nest.value(date,"year").asInteger());

				if (new_time == time) {
					return time;
				}
				if (new_time > max_time) {
					return max_time;
				}

				long next_time = find_period_end(periods, new_time, max_time);
				if (next_time < 0) {
					return new_time;
				}
				else {
					return next_time;
				}
			}
		}

		return -1;
	}
	
	public static CArray<Float> convertToBase1024 (double value) {
		return convertToBase1024(value, false);
	}
	
	/**
	 * Converts Base1000 values to Base1024 and calculate pow
	 * Example:
	 * 	204800 (200 KBytes) with "1024" step convert to 209715,2 (0.2MB (204.8 KBytes))
	 *
	 * @param string    value
	 * @param bool|int step
	 *
	 * @return array
	 */
	public static CArray<Float> convertToBase1024 (double value, Object step) {
		if (empty(step)) {
			step = 1000;
		}

		double abs;
		if (value < 0) {
			abs = bcmul(value, -1).intValue();
		}
		else {
			abs = value;
		}

		// set default values
		CArray valData = array();
		Nest.value(valData,"pow").$(0);
		Nest.value(valData,"value").$(0);

		// supported pows ("-2" - "8")
		for (int i = -2; i < 9; i++) {
			double _val = bcpow(asInteger(step), i);
			if (bccomp(abs, _val) > -1) {
				Nest.value(valData,"pow").$(i);
				Nest.value(valData,"value").$(_val);
			} else {
				break;
			}
		}

		if (Nest.value(valData,"pow").asDouble() >= 0) {
			if (Nest.value(valData,"value").asDouble() != 0) {
				Nest.value(valData,"value").$(bcdiv(sprintf("%.10f",value), sprintf("%.10f", Nest.value(valData,"value").$()), RDA_PRECISION_10));
				Nest.value(valData,"value").$(sprintf("%.10f", round(bcmul(Nest.value(valData,"value").asDouble(), bcpow(1024, Nest.value(valData,"pow").asDouble())), RDA_PRECISION_10)));
			}
		} else {
			Nest.value(valData,"pow").$(0);
			if (round(Nest.value(valData,"value").asDouble(), RDA_UNITS_ROUNDOFF_LOWER_LIMIT) > 0) {
				Nest.value(valData,"value").$(value);
			} else {
				Nest.value(valData,"value").$(0);
			}
		}

		return valData;
	}
	
	/**
	 * Calculate interval for base 1024 values.
	 * Example:
	 * 	Convert 1000 to 1024
	 *
	 * @param interval
	 * @param minY
	 * @param maxY
	 *
	 * @return float|int
	 */
	public static float getBase1024Interval(float interval, double minY, double maxY) {
		CArray<Float> _intervalData = convertToBase1024(interval);
		interval = Nest.value(_intervalData,"value").asFloat();

		double absMaxY, absMinY;
		if (maxY > 0) {
			absMaxY = maxY;
		} else {
			absMaxY = bcmul(maxY, -1);
		}

		if (minY > 0) {
			absMinY = minY;
		} else {
			absMinY = bcmul(minY, -1);
		}

		CArray sideMaxData;
		if (absMaxY > absMinY) {
			sideMaxData = convertToBase1024(maxY);
		} else {
			sideMaxData = convertToBase1024(minY);
		}

		if (Nest.value(sideMaxData,"pow").asDouble() != Nest.value(_intervalData,"pow").asDouble()) {
			// interval correction, if Max Y have other unit, then interval unit = Max Y unit
			if (Nest.value(_intervalData,"pow").asDouble() < 0) {
				interval = asFloat(sprintf("%.10f", bcmul(interval, 1.024, 10)));
			} else {
				interval = asFloat(sprintf("%.6f", round(bcmul(interval, 1.024), RDA_UNITS_ROUNDOFF_UPPER_LIMIT)));
			}
		}

		return interval;
	}
	
	/**
	 * Returns digit count for the item with most digit after point in given array.
	 * Example:
	 *	Input: array(0, 0.1, 0.25, 0.005)
	 *	Return 3
	 *
	 * @param array calcValues
	 *
	 * @return int
	 */
	public static int calcMaxLengthAfterDot(CArray<String> calcValues) {
		int maxLength = 0;
	
		for(String calcValue: calcValues) {
			CArray matches = array();
			preg_match("^-?[0-9].?([0-9]*)\\s?", calcValue, matches);
			if (Nest.value(matches,"1").asInteger() != 0 && strlen(Nest.value(matches,"1").asString()) > maxLength) {
				maxLength = strlen(Nest.value(matches,"1").asString());
			}
		}
	
		return maxLength;
	}
}
