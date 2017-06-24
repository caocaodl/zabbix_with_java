package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cphp.SORT_DESC;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_multisort;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.rda_dbcast_2bigint;
import static com.isoft.iradar.inc.DBUtil.rda_sql_mod;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_COLUMN;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.P_UNSET_EMPTY;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MONTH;
import static com.isoft.iradar.inc.Defines.SEC_PER_WEEK;
import static com.isoft.iradar.inc.Defines.SEC_PER_YEAR;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_DAILY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_HOURLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_MONTHLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_WEEKLY;
import static com.isoft.iradar.inc.Defines.TIMEPERIOD_TYPE_YEARLY;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_objectValues;
import static com.isoft.iradar.inc.FuncsUtil.rgb2hex;
import static com.isoft.iradar.inc.GraphsUtil.get_next_palette;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.ItemsUtil.get_same_item_for_host;
import static com.isoft.iradar.inc.TranslateDefines.CHARTBAR_DAILY_DATE_FORMAT;
import static com.isoft.iradar.inc.TranslateDefines.CHARTBAR_HOURLY_DATE_FORMAT;
import static com.isoft.iradar.inc.ValidateUtil.IN;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.params.CItemGet;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.tags.graphdraw.CBarGraphDraw;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class ChartBarAction extends RadarBaseAction {
	
	private CArray<Map> items;
	private CArray itemIds;
	
	@Override
	protected boolean isHtmlPage() {
		return false;
	}
		
	@Override
	protected void doInitPage() {
		page("file", "chart_bar.action");
		page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"config"  ,					array(T_RDA_INT, O_OPT,	P_SYS,			IN("0,1,2,3"),	null),
			"hostids"  ,				array(T_RDA_INT, O_OPT,	P_SYS,			DB_ID,			null),
			"groupids"  ,				array(T_RDA_INT, O_OPT,	P_SYS,			DB_ID,			null),
			"items"  ,					array(T_RDA_STR, O_OPT,	P_SYS,			Defines.NOT_EMPTY,	null),
			"title"  ,						array(T_RDA_STR, O_OPT, null,			null,			null),
			"xlabel"  ,					array(T_RDA_STR, O_OPT, null,			null,			null),
			"ylabel"  ,					array(T_RDA_STR, O_OPT, null,			null,			null),
			"showlegend"  ,		array(T_RDA_STR, O_OPT, null,			null,			null),
			"sorttype"  ,				array(T_RDA_INT, O_OPT,	null,			null,			null),
			"scaletype"  ,			array(T_RDA_INT, O_OPT,	null,			null,			null),
			"avgperiod"  ,			array(T_RDA_INT, O_OPT,	null,			null,			null),
			"periods"  ,				array(T_RDA_STR, O_OPT,	null,			null,			null),
			"report_timesince"  ,	array(T_RDA_INT, O_OPT,	P_UNSET_EMPTY,	null,			null),
			"report_timetill"  ,		array(T_RDA_INT, O_OPT,	P_UNSET_EMPTY,	null,			null),
			"palette" ,					array(T_RDA_INT, O_OPT,	null,			null,			null),
			"palettetype" ,			array(T_RDA_INT, O_OPT,	null,			null,			null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		// validate permissions
		items = get_request("items", array());
		itemIds = rda_objectValues(Nest.value(_REQUEST,"items").asCArray(true), "itemid");
		
		CItemGet params = new CItemGet();
		params.setItemIds(itemIds.valuesAsLong());
		params.setWebItems(true);
		params.setCountOutput(true);
		
		Long itemsCount = API.Item(getIdentityBean(), executor).get(params);
		if (count(itemIds) != itemsCount) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		int config = get_request("config", 1);
		String title = get_request("title", _("Report"));
		String xlabel = get_request("xlabel", "X");
		String ylabel = get_request("ylabel", "Y");

		int showlegend = get_request("showlegend", 0);
		int sorttype = get_request("sorttype", 0);
		
		CArray<CArray> db_values = array();
		CArray graph_data = array();
		CBarGraphDraw graph = null;
		int scaleperiod = 0;
		
		if (config == 1) {
			int scaletype = get_request("scaletype", TIMEPERIOD_TYPE_WEEKLY);

			long timesince = get_request("report_timesince", time() - SEC_PER_DAY);
			long timetill = get_request("report_timetill", time());

			CArray str_since = array(), str_till = array();
			Nest.value(str_since,"hour").$(date("H", timesince));
			Nest.value(str_since,"day").$(date("d", timesince));
			Nest.value(str_since,"weekday").$(date("w", timesince));
			if (Nest.value(str_since,"weekday").asInteger() == 0) {
				Nest.value(str_since,"weekday").$(7);
			}

			Nest.value(str_since,"mon").$(date("m", timesince));
			Nest.value(str_since,"year").$(date("Y", timesince));

			Nest.value(str_till,"hour").$(date("H", timetill));
			Nest.value(str_till,"day").$(date("d", timetill));
			Nest.value(str_till,"weekday").$(date("w", timetill));
			if (Nest.value(str_till,"weekday").asInteger() == 0) {
				Nest.value(str_till,"weekday").$(7);
			}

			Nest.value(str_till,"mon").$(date("m", timetill));
			Nest.value(str_till,"year").$(date("Y", timetill));

			String str;
			switch (scaletype) {
				case TIMEPERIOD_TYPE_HOURLY:
					scaleperiod = SEC_PER_HOUR;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-"+Nest.value(str_since, "day").asString()+" "+Nest.value(str_since, "hour").asString()+":00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-"+Nest.value(str_till, "day").asString()+" "+Nest.value(str_till, "hour").asString()+":00:00";
					timetill = strtotime(str) + scaleperiod;
					break;
				case TIMEPERIOD_TYPE_DAILY:
					scaleperiod = SEC_PER_DAY;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-"+Nest.value(str_since, "day").asString()+" 00:00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-"+Nest.value(str_till, "day").asString()+" 00:00:00";
					timetill = strtotime(str) + scaleperiod;
					break;
				case TIMEPERIOD_TYPE_WEEKLY:
					scaleperiod = SEC_PER_WEEK;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-"+Nest.value(str_since, "day").asString()+" 00:00:00";
					timesince = strtotime(str);
					timesince -= (Nest.value(str_since,"weekday").asInteger() - 1) * SEC_PER_DAY;

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-"+Nest.value(str_till, "day").asString()+" 00:00:00";
					timetill = strtotime(str);
					timetill -= (Nest.value(str_till,"weekday").asInteger() - 1) * SEC_PER_DAY;

					timetill += scaleperiod;
					break;
				case TIMEPERIOD_TYPE_MONTHLY:
					scaleperiod = SEC_PER_MONTH;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-01 00:00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-01 00:00:00";
					timetill = strtotime(str);
					timetill = strtotime("+1 month", timetill);
					break;
				case TIMEPERIOD_TYPE_YEARLY:
					scaleperiod = SEC_PER_YEAR;
					str = Nest.value(str_since, "year").asString()+"-01-01 00:00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-01-01 00:00:00";
					timetill = strtotime(str);
					timetill = strtotime("+1 year", timetill);
					break;
			}

			long p = timetill - timesince;				// graph size in time
			long z = p - (timesince % p);				// graphsize - mod(from_time,p) for Oracle...
			int x = round(p / scaleperiod);				// graph size in px
			String calc_field = "floor("+x+"*"+rda_sql_mod(rda_dbcast_2bigint("clock")+"+"+z, p)+"/("+p+"))";	// required for "group by" support of Oracle

//			int period_step = scaleperiod;

			graph = new CBarGraphDraw(getIdentityBean(), executor, GRAPH_TYPE_COLUMN);
			graph.setHeader(title);

			Nest.value(graph_data,"colors").$(array());
			Nest.value(graph_data,"legend").$(array());

			for(Map item: items) {
				Object itemid = Nest.value(item,"itemid").$();
				Map item_data = Nest.value(db_values, itemid).$s(true);

				Nest.value(graph_data, "legend").push(Nest.value(item,"caption").$());

				Map params = new HashMap();
				params.put("itemid", itemid);
				params.put("timesince", timesince);
				params.put("timetill", timetill);
				CArray<String> sql_arr = array();
				array_push(sql_arr,
					"SELECT itemid,"+calc_field+" as i,"+
						" sum(num) as count,avg(value_avg) as avg,min(value_min) as min,"+
						" max(value_max) as max,max(clock) as clock"+
					" FROM trends "+
					" WHERE itemid=#{itemid}"+
						" AND clock>=#{timesince}"+
						" AND clock<=#{timetill}"+
					" GROUP BY itemid,"+calc_field+
					" ORDER BY clock ASC"
					,

					"SELECT itemid,"+calc_field+" as i,"+
						" sum(num) as count,avg(value_avg) as avg,min(value_min) as min,"+
						" max(value_max) as max,max(clock) as clock"+
					" FROM trends_uint "+
					" WHERE itemid=#{itemid}"+
						" AND clock>=#{timesince}"+
						" AND clock<=#{timetill}"+
					" GROUP BY itemid,"+calc_field+
					" ORDER BY clock ASC"
					);

				
				CArray str_start = array();
				Map row = null;
				
				for(Entry<Object, String> entry: sql_arr.entrySet()) {
					//Object id = entry.getKey();
					String sql = entry.getValue();
					
					CArray<Map> result = DBselect(executor, sql, params);

					int i = 0;
					long start = 0;
					long end = timesince;
					while (end < timetill) {
						switch (scaletype) {
							case TIMEPERIOD_TYPE_HOURLY:
							case TIMEPERIOD_TYPE_DAILY:
							case TIMEPERIOD_TYPE_WEEKLY:
								start = end;
								end = start + scaleperiod;
								break;
							case TIMEPERIOD_TYPE_MONTHLY:
								start = end;
								Nest.value(str_start,"mon").$(date("m", start));
								Nest.value(str_start,"year").$(date("Y", start));
								str = Nest.value(str_start, "year").asString()+"-"+Nest.value(str_start, "mon").asString()+"-01 00:00:00";
								end = strtotime(str);
								end = strtotime("+1 month", end);
								break;
							case TIMEPERIOD_TYPE_YEARLY:
								start = end;
								Nest.value(str_start,"year").$(date("Y", start));
								str = Nest.value(str_start, "year").asString()+"-01-01 00:00:00";
								end = strtotime(str);
								end = strtotime("+1 year", end);
								break;
						}

						if (!isset(row) || (Nest.value(row, "clock").asLong()<start)) {
							row = DBfetch(result);
						}

						if (isset(row) && !empty(row) && (Nest.value(row,"clock").asLong() >= start) && (Nest.value(row,"clock").asLong() < end)) {
							Nest.value(item_data, "count", i).$(Nest.value(row,"count").$());
							Nest.value(item_data, "min", i).$(Nest.value(row,"min").$());
							Nest.value(item_data, "avg", i).$(Nest.value(row,"avg").$());
							Nest.value(item_data, "max", i).$(Nest.value(row,"max").$());
							Nest.value(item_data, "clock", i).$(start);
							Nest.value(item_data, "type", i).$(true);
						} else {
							if (isset(Nest.value(item_data, "type", i).$()) && !empty(Nest.value(item_data, "type", i).$())) {
								continue;
							}

							Nest.value(item_data, "count", i).$(0);
							Nest.value(item_data, "min", i).$(0);
							Nest.value(item_data, "avg", i).$(0);
							Nest.value(item_data, "max", i).$(0);
							Nest.value(item_data, "clock", i).$(start);
							Nest.value(item_data, "type", i).$(false);
						}
						i++;
					}
				}

				CArray tmp_value = null;
				switch (Nest.value(item,"calc_fnc").asInteger()) {
					case 0:
						tmp_value = Nest.value(item_data,"count").asCArray();
						break;
					case CALC_FNC_MIN:
						tmp_value = Nest.value(item_data,"min").asCArray();
						break;
					case CALC_FNC_AVG:
						tmp_value = Nest.value(item_data,"avg").asCArray();
						break;
					case CALC_FNC_MAX:
						tmp_value = Nest.value(item_data,"max").asCArray();
						break;
				}

				graph.addSeries(tmp_value, Nest.value(item,"axisside").asInteger());

				Nest.value(graph_data, "colors").push(Nest.value(item,"color").$());

				Map db_item = get_item_by_itemid(executor, Nest.value(item,"itemid").asString());
				if (!empty(db_item)) {
					graph.setUnits(Nest.value(db_item,"units").asString(), Nest.value(item,"axisside").asInteger());
					graph.setSideValueType(Nest.value(db_item,"value_type").asInteger(), Nest.value(item,"axisside").asInteger());
				}

				if (!isset(Nest.value(graph_data,"captions").$())) {
					String date_caption = (scaletype == TIMEPERIOD_TYPE_HOURLY)
						? CHARTBAR_HOURLY_DATE_FORMAT
						: CHARTBAR_DAILY_DATE_FORMAT;

					Nest.value(graph_data,"captions").$(array());
					
					for(Entry<Object, Long> entry: ((CArray<Long>)item_data.get("clock")).entrySet()) {
						Object id = entry.getKey();
						Long clock = entry.getValue();
						Nest.value(graph_data, "captions", id).$(rda_date2str(date_caption, clock));
					}
				}
			}
		}
		else if (config == 2) {
			CArray<Map> periods = get_request("periods", array());

			graph = new CBarGraphDraw(getIdentityBean(), executor, GRAPH_TYPE_COLUMN);
			graph.setHeader("REPORT 1");

			Nest.value(graph_data,"colors").$(array());
			Nest.value(graph_data,"captions").$(array());
			Nest.value(graph_data,"values").$(array());
			Nest.value(graph_data,"legend").$(array());

			for(Entry<Object, Map> entry: periods.entrySet()) {
				Object pid = entry.getKey();
				Map period = entry.getValue();
			
				Nest.value(graph_data, "colors").push(Nest.value(period,"color").$());
				Nest.value(graph_data, "legend").push(Nest.value(period,"caption").$());
				
				Map params = new HashMap();
				params.put("timesince", period.get("report_timesince"));
				params.put("timetill", period.get("report_timetill"));

				Nest.value(db_values, pid).$(array());				
				for(Map _item: items) {
					Object itemid = Nest.value(_item,"itemid").$();
					Map item_data = (Map)Nest.value(db_values, pid, itemid).$();

					params.put("itemid", itemid);
					String sql = "SELECT itemid, sum(num) as count,avg(value_avg) as avg,min(value_min) as min,"+
							" max(value_max) as max,max(clock) as clock"+
						" FROM trends "+
						" WHERE itemid=#{itemid}"+
							" AND clock>=#{timesince}"+
							" AND clock<=#{timetill}"+
						" GROUP BY itemid";
					CArray<Map> result = DBselect(executor, sql, params);
					Map row = DBfetch(result);
					if (!empty(row)) {
						Nest.value(item_data,"count").$(Nest.value(row,"count").$());
						Nest.value(item_data,"min").$(Nest.value(row,"min").$());
						Nest.value(item_data,"avg").$(Nest.value(row,"avg").$());
						Nest.value(item_data,"max").$(Nest.value(row,"max").$());
						Nest.value(item_data,"clock").$(Nest.value(row,"clock").$());
					}

					sql = "SELECT itemid, sum(num) as count,avg(value_avg) as avg,min(value_min) as min,"+
							" max(value_max) as max,max(clock) as clock"+
						" FROM trends_uint "+
						" WHERE itemid=#{itemid}"+
							" AND clock>=#{timesince}"+
							" AND clock<=#{timetill}"+
						" GROUP BY itemid";
					result = DBselect(executor, sql, params);
					row = DBfetch(result);
					if (!empty(row)) {
						if (!empty(item_data)) {
							Nest.value(item_data, "count").plus(Nest.value(row,"count").$());
							Nest.value(item_data,"min").$(min(Nest.value(item_data,"count").asDouble(), Nest.value(row,"min").asDouble()));
							Nest.value(item_data,"avg").$((Nest.value(item_data,"count").asDouble() + Nest.value(row,"avg").asDouble()) / 2);
							Nest.value(item_data,"max").$(max(Nest.value(item_data,"count").asDouble(), Nest.value(row,"max").asDouble()));
							Nest.value(item_data,"clock").$(max(Nest.value(item_data,"count").asDouble(), Nest.value(row,"clock").asDouble()));
						}
						else{
							Nest.value(item_data,"count").$(Nest.value(row,"count").$());
							Nest.value(item_data,"min").$(Nest.value(row,"min").$());
							Nest.value(item_data,"avg").$(Nest.value(row,"avg").$());
							Nest.value(item_data,"max").$(Nest.value(row,"max").$());
							Nest.value(item_data,"clock").$(Nest.value(row,"clock").$());
						}
					}

		// fixes bug #21788, due to Zend casting the array key as a numeric and then they are reassigned
					itemid = "0"+itemid;

					switch (Nest.value(_item,"calc_fnc").asInteger()) {
						case 0:
							Nest.value(graph_data, "values", itemid).$(Nest.value(item_data,"count").$());
							break;
						case CALC_FNC_MIN:
							Nest.value(graph_data, "values", itemid).$(Nest.value(item_data,"min").$());
							break;
						case CALC_FNC_AVG:
							Nest.value(graph_data, "values", itemid).$(Nest.value(item_data,"avg").$());
							break;
						case CALC_FNC_MAX:
							Nest.value(graph_data, "values", itemid).$(Nest.value(item_data,"max").$());
							break;
					}

					Nest.value(graph_data, "captions", itemid).$(Nest.value(_item,"caption").$());

					Map db_item = get_item_by_itemid(executor, Nest.value(_item,"itemid").asString());
					if (!empty(db_item)) {
						graph.setUnits(Nest.value(db_item,"units").asString(), Nest.value(_item,"axisside").asInteger());
						graph.setSideValueType(Nest.value(db_item,"value_type").asInteger(), Nest.value(_item,"axisside").asInteger());
					}
				}

				if (sorttype == 0 || count(periods) < 2) {
					array_multisort(Nest.value(graph_data,"captions").asCArray(), Nest.value(graph_data,"values").$());
				}
				else {
					array_multisort(Nest.value(graph_data,"values").asCArray(), SORT_DESC, Nest.value(graph_data,"captions").$());
				}

				graph.addSeries(Nest.value(graph_data,"values").asCArray());
			}
		}
		else if (config == 3) {
			CArray hostids = get_request("hostids", array());
			CArray groupids = get_request("groupids", array());

			// validate permissions
			if (!API.Host(getIdentityBean(), executor).isReadable(hostids.valuesAsLong()) || !API.HostGroup(getIdentityBean(), executor).isReadable(groupids.valuesAsLong())) {
				access_deny();
			}

			title = get_request("title","Report 2");
			xlabel = get_request("xlabel","");
			ylabel = get_request("ylabel","");

			int palette = get_request("palette",0);
			int palettetype = get_request("palettetype",0);

			int scaletype = get_request("scaletype", TIMEPERIOD_TYPE_WEEKLY);
			int avgperiod = get_request("avgperiod", TIMEPERIOD_TYPE_DAILY);

			if (!empty(groupids)) {
				SqlBuilder sb = new SqlBuilder();
				
				String sql = "SELECT DISTINCT hg.hostid"+
					" FROM hosts_groups hg,hosts h"+
					" WHERE h.hostid=hg.hostid"+
						" AND "+ sb.where.dbConditionInt("h.status", new int[]{HOST_STATUS_MONITORED, HOST_STATUS_NOT_MONITORED})+
						" AND "+sb.where.dbConditionInt("hg.groupid", groupids.valuesAsLong());
				CArray<Map> res = DBselect(executor, sql, sb.getNamedParams());
				for (Map db_host: res) {
					Nest.value(hostids, db_host.get("hostid")).$(Nest.value(db_host,"hostid").$());
				}
			}

			CArray<String> itemids = array();
			for(Map item: items){
				if (Nest.value(item,"itemid").asInteger() > 0) {
					itemids = get_same_item_for_host(executor, Nest.value(item,"itemid").asLong(), hostids.valuesAsLong());
					break;
				}
			}

			graph = new CBarGraphDraw(getIdentityBean(), executor, GRAPH_TYPE_COLUMN);
			graph.setHeader("REPORT 3");

			Nest.value(graph_data,"colors").$(array());
			Nest.value(graph_data,"captions").$(array());
			Nest.value(graph_data,"values").$(array());
			Nest.value(graph_data,"legend").$(array());

			long timesince = get_request("report_timesince", time() - SEC_PER_DAY);
			long timetill = get_request("report_timetill", time());
			
			CArray str_since = array(), str_till = array();
			Nest.value(str_since,"hour").$(date("H", timesince));
			Nest.value(str_since,"day").$(date("d", timesince));
			Nest.value(str_since,"weekday").$(date("w", timesince));
			if (Nest.value(str_since,"weekday").asInteger() == 0) {
				Nest.value(str_since,"weekday").$(7);
			}

			Nest.value(str_since,"mon").$(date("m", timesince));
			Nest.value(str_since,"year").$(date("Y", timesince));

			Nest.value(str_till,"hour").$(date("H", timetill));
			Nest.value(str_till,"day").$(date("d", timetill));
			Nest.value(str_till,"weekday").$(date("w", timetill));
			if (Nest.value(str_till,"weekday").asInteger() == 0) {
				Nest.value(str_till,"weekday").$(7);
			}

			Nest.value(str_till,"mon").$(date("m", timetill));
			Nest.value(str_till,"year").$(date("Y", timetill));

			String str;
			switch (scaletype) {
				case TIMEPERIOD_TYPE_HOURLY:
					scaleperiod = SEC_PER_HOUR;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-"+Nest.value(str_since, "day").asString()+" "+Nest.value(str_since, "hour").asString()+":00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-"+Nest.value(str_till, "day").asString()+" "+Nest.value(str_till, "hour").asString()+":00:00";
					timetill = strtotime(str) + scaleperiod;
					break;
				case TIMEPERIOD_TYPE_DAILY:
					scaleperiod = SEC_PER_DAY;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-"+Nest.value(str_since, "day").asString()+" 00:00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-"+Nest.value(str_till, "day").asString()+" 00:00:00";
					timetill = strtotime(str) + scaleperiod;
					break;
				case TIMEPERIOD_TYPE_WEEKLY:
					scaleperiod = SEC_PER_WEEK;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-"+Nest.value(str_since, "day").asString()+" 00:00:00";
					timesince = strtotime(str);
					timesince -= (Nest.value(str_since,"weekday").asInteger() - 1) * SEC_PER_DAY;

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-"+Nest.value(str_till, "day").asString()+" 00:00:00";
					timetill = strtotime(str);
					timetill -= (Nest.value(str_till,"weekday").asInteger() - 1) * SEC_PER_DAY;

					timetill+= scaleperiod;
					break;
				case TIMEPERIOD_TYPE_MONTHLY:
					scaleperiod = SEC_PER_MONTH;
					str = Nest.value(str_since, "year").asString()+"-"+Nest.value(str_since, "mon").asString()+"-01 00:00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-"+Nest.value(str_till, "mon").asString()+"-01 00:00:00";
					timetill = strtotime(str);
					timetill = strtotime("+1 month",timetill);
					break;
				case TIMEPERIOD_TYPE_YEARLY:
					scaleperiod = SEC_PER_YEAR;
					str = Nest.value(str_since, "year").asString()+"-01-01 00:00:00";
					timesince = strtotime(str);

					str = Nest.value(str_till, "year").asString()+"-01-01 00:00:00";
					timetill = strtotime(str);
					timetill = strtotime("+1 year",timetill);
					break;
			}

			int period = 0;
			// updating
			switch (avgperiod) {
				case TIMEPERIOD_TYPE_HOURLY:
					period = SEC_PER_HOUR;
					break;
				case TIMEPERIOD_TYPE_DAILY:
					period = SEC_PER_DAY;
					break;
				case TIMEPERIOD_TYPE_WEEKLY:
					period = SEC_PER_WEEK;
					break;
				case TIMEPERIOD_TYPE_MONTHLY:
					period = SEC_PER_MONTH;
					break;
				case TIMEPERIOD_TYPE_YEARLY:
					period = SEC_PER_YEAR;
					break;
			}

			CArray<Map> hosts = get_host_by_itemid(getIdentityBean(), executor, itemids.valuesAsString());

			String itemid = null;
			for(String _itemid: itemids) {
				itemid = _itemid;
				int count = 0;
				if (!isset(db_values, count)) {
					db_values.put(count, array());
				}
				Nest.value(graph_data, "captions", _itemid).$(hosts.getNested(_itemid, "host"));

				CArray str_start=array();
				
				long start = 0;
				long end = timesince;
				while (end < timetill) {
					switch (scaletype) {
						case TIMEPERIOD_TYPE_HOURLY:
						case TIMEPERIOD_TYPE_DAILY:
						case TIMEPERIOD_TYPE_WEEKLY:
							start = end;
							end = start + scaleperiod;
							break;
						case TIMEPERIOD_TYPE_MONTHLY:
							start = end;

							Nest.value(str_start,"mon").$(date("m",start));
							Nest.value(str_start,"year").$(date("Y",start));

							str = Nest.value(str_start, "year").asString()+"-"+Nest.value(str_start, "mon").asString()+"-01 00:00:00";
							end = strtotime(str);
							end = strtotime("+1 month",end);
							break;
						case TIMEPERIOD_TYPE_YEARLY:
							start = end;

							Nest.value(str_start,"year").$(date("Y",start));

							str = Nest.value(str_start, "year").asString()+"-01-01 00:00:00";
							end = strtotime(str);
							end = strtotime("+1 year",end);
							break;
					}

					long p = end - start;						// graph size in time
					long z = p - (start % p);				// graphsize - mod(from_time,p) for Oracle...
					int x = floor(scaleperiod / period);		// graph size in px
					String calc_field = "round("+x+"*"+rda_sql_mod(rda_dbcast_2bigint("clock")+"+"+z, p)+"/("+p+"),0)";	// required for "group by" support of Oracle

					Double item_data = null;
					Map params = new HashMap();
					params.put("itemid", _itemid);
					params.put("start", start);
					params.put("end", end);
					CArray<String> sql_arr = array();

					array_push(sql_arr,
						"SELECT itemid,"+calc_field+" as i,sum(num) as count,avg(value_avg) as avg "+
						" FROM trends "+
						" WHERE itemid=#{itemid}"+
							" AND clock>=#{start}"+
							" AND clock<=#{end}"+
						" GROUP BY itemid,"+calc_field
						,

						"SELECT itemid,"+calc_field+" as i,sum(num) as count,avg(value_avg) as avg "+
						" FROM trends_uint "+
						" WHERE itemid=#{itemid}"+
							" AND clock>=#{start}"+
							" AND clock<=#{end}"+
						" GROUP BY itemid,"+calc_field
						);

					for(String sql: sql_arr) {
						CArray<Map> result = DBselect(executor, sql, params);
						for (Map row : result) {
							if (Nest.value(row,"i").asInteger() == x) {
								continue;
							}
							if (!is_null(item_data)) {
								item_data = (item_data + Nest.value(row,"avg").asDouble()) / 2;
							} else {
								item_data = Nest.value(row,"avg").asDouble();
							}
						}

					}

					Nest.value(db_values, count, _itemid).$(is_null(item_data) ? 0 : item_data);

					CArray<Integer> _tmp_color = get_next_palette(palette,palettetype);

					if (!isset(Nest.value(graph_data, "colors", count).$())) {
						Nest.value(graph_data, "colors", count).$(rgb2hex(_tmp_color));
					}

					String date_caption = (scaletype == TIMEPERIOD_TYPE_HOURLY)
						? CHARTBAR_HOURLY_DATE_FORMAT
						: CHARTBAR_DAILY_DATE_FORMAT;
					Nest.value(graph_data, "legend", count).$(rda_date2str(date_caption, start));

					count++;
				}
			}

			for(CArray _item_data: db_values) {
				graph.addSeries(_item_data);
			}

			Map db_item;
			if (isset(itemid) && !empty(db_item = get_item_by_itemid(executor, itemid))) {
				graph.setUnits(Nest.value(db_item,"units").asString());
				graph.setSideValueType(Nest.value(db_item,"value_type").asInteger());
			}
		}

		if (!isset(graph_data,"captions")) {
			Nest.value(graph_data,"captions").$(array());
		}
		if (!isset(graph_data,"legend")) {
			Nest.value(graph_data,"legend").$("");
		}

		graph.setSeriesLegend(Nest.value(graph_data,"legend").asCArray());
		graph.setPeriodCaption(Nest.value(graph_data,"captions").asCArray());

		graph.setHeader(title);
		graph.setPeriod(scaleperiod);
		graph.setXLabel(xlabel);
		graph.setYLabel(ylabel);

		graph.setSeriesColor(Nest.value(graph_data,"colors").asCArray());

		graph.showLegend(showlegend);

		graph.setWidth(1024);
		graph.setHeight(400);

		try {
			byte[] bs = graph.draw();
			this.getResponse().getOutputStream().write(bs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
