package com.isoft.iradar.tags.graphdraw;

import static com.isoft.iradar.Cgd.IMG_COLOR_STYLED;
import static com.isoft.iradar.Cgd.IMG_COLOR_TRANSPARENT;
import static com.isoft.iradar.Cgd.imagecolorallocate;
import static com.isoft.iradar.Cgd.imagecolorexactalpha;
import static com.isoft.iradar.Cgd.imagecreate;
import static com.isoft.iradar.Cgd.imageellipse;
import static com.isoft.iradar.Cgd.imagefill;
import static com.isoft.iradar.Cgd.imagefilledellipse;
import static com.isoft.iradar.Cgd.imagefilledpolygon;
import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cgd.imageline;
import static com.isoft.iradar.Cgd.imagepolygon;
import static com.isoft.iradar.Cgd.imagerectangle;
import static com.isoft.iradar.Cgd.imagesetpixel;
import static com.isoft.iradar.Cgd.imagesetstyle;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.bcadd;
import static com.isoft.iradar.Cphp.bcceil;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.bcdiv;
import static com.isoft.iradar.Cphp.bcfloor;
import static com.isoft.iradar.Cphp.bcmul;
import static com.isoft.iradar.Cphp.bcpow;
import static com.isoft.iradar.Cphp.bcsub;
import static com.isoft.iradar.Cphp.ceil;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.microtime;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.sort;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asDouble;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asLong;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.DBUtil.rda_dbcast_2bigint;
import static com.isoft.iradar.inc.DBUtil.rda_sql_mod;
import static com.isoft.iradar.inc.Defines.CALC_FNC_ALL;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_BOLD_DOT;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_BOLD_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_DASHED_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_DOT;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_FILLED_REGION;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_DRAWTYPE_LINE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SIMPLE;
import static com.isoft.iradar.inc.Defines.GRAPH_STACKED_ALFA;
import static com.isoft.iradar.inc.Defines.GRAPH_TRIGGER_LINE_OPPOSITE_COLOR;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_DEFAULT;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_LEFT;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_RIGHT;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_CALCULATED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_FIXED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_TYPE_ITEM_VALUE;
import static com.isoft.iradar.inc.Defines.GRAPH_ZERO_LINE_COLOR_LEFT;
import static com.isoft.iradar.inc.Defines.GRAPH_ZERO_LINE_COLOR_RIGHT;
import static com.isoft.iradar.inc.Defines.ITEM_CONVERT_NO_UNITS;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_TRAPPER;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.LINE_TYPE_BOLD;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.RDA_GRAPH_LEGEND_HEIGHT;
import static com.isoft.iradar.inc.Defines.RDA_GRAPH_MAX_SKIP_CELL;
import static com.isoft.iradar.inc.Defines.RDA_GRAPH_MAX_SKIP_DELAY;
import static com.isoft.iradar.inc.Defines.RDA_MAX_TREND_DIFF;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.DrawUtil.rda_imagealine;
import static com.isoft.iradar.inc.DrawUtil.rda_imageline;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.hex2rgb;
import static com.isoft.iradar.inc.FuncsUtil.parse_period;
import static com.isoft.iradar.inc.FuncsUtil.rda_avg;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_empty;
import static com.isoft.iradar.inc.FuncsUtil.show_error_message;
import static com.isoft.iradar.inc.GraphsUtil.calcMaxLengthAfterDot;
import static com.isoft.iradar.inc.GraphsUtil.convertToBase1024;
import static com.isoft.iradar.inc.GraphsUtil.dashedLine;
import static com.isoft.iradar.inc.GraphsUtil.dashedRectangle;
import static com.isoft.iradar.inc.GraphsUtil.find_period_end;
import static com.isoft.iradar.inc.GraphsUtil.find_period_start;
import static com.isoft.iradar.inc.GraphsUtil.getBase1024Interval;
import static com.isoft.iradar.inc.GraphsUtil.imageText;
import static com.isoft.iradar.inc.GraphsUtil.imageTextSize;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ItemsUtil.getItemDelay;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.iradar.inc.TriggersUtil.convert;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityColor;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;
import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cgd;
import com.isoft.iradar.Cphp;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.Manager;
import com.isoft.iradar.tags.CImageTextTable;
import com.isoft.lang.Clone;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;

public class CLineGraphDraw extends CGraphDraw {
	
	protected Double yaxismin = null;
	protected Double yaxismax = null;
	protected CArray<Map> triggers = array();
	protected int ymin_type = GRAPH_YAXIS_TYPE_CALCULATED;
	protected int ymax_type = GRAPH_YAXIS_TYPE_CALCULATED;
	protected int yaxisright = 0;
	protected int yaxisleft = 0;
	protected int skipLeftScale = 0; // in case if left axis should be drawn but doesn't contain any data
	protected int skipRightScale = 0; // in case if right axis should be drawn but doesn't contain any data
	protected String ymin_itemid = "0";
	protected String ymax_itemid = "0";
	protected int legendOffsetY = 90;
	protected CArray<CArray<Float>> percentile = map(
	 	"left", map(
	 		"percent", 0, // draw percentage line
	 		"value", 0 // calculated percentage value left y axis
	 	),
	 	"right", map(
	 		"percent", 0, // draw percentage line
	 		"value", 0 // calculated percentage value right y axis
	 	)
	 );
	protected int m_showWorkPeriod = 1;
	protected int m_showTriggers = 1;
	protected CArray<Integer> zero = array();
	protected CArray<String> graphOrientation = map(
	 	GRAPH_YAXIS_SIDE_LEFT, "",
	 	GRAPH_YAXIS_SIDE_RIGHT, ""
	 );
	protected CArray grid = array(); // vertical & horizontal grids params
	protected CArray<Integer> gridLinesCount = array(); // How many grids to draw
	protected CArray<Double> gridStep = array(); // grid step
	protected int gridPixels = 25; // optimal grid size
	protected int gridPixelsVert = 40;
	
	protected CArray<Double> unit2px = array();
	protected CArray<Double> oxy = array();
	protected CArray<Double> gridStepX = array();
	protected long diffTZ;
	protected Object itemsHost;
	
	
	public CLineGraphDraw(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, GRAPH_TYPE_NORMAL);
	}
	public CLineGraphDraw(IIdentityBean idBean, SQLExecutor executor, int graphType) {
		super(idBean, executor, graphType);
	}

	/********************************************************************************************************/
	// PRE CONFIG:	ADD / SET / APPLY
	/********************************************************************************************************/
	public void updateShifts() {
		if (yaxisleft == 1 && yaxisright == 1) {
			shiftXleft = 85;
			shiftXright = 85;
		}
		else if (yaxisleft == 1) {
			shiftXleft = 85;
			shiftXright = 30;
		}
		else if (yaxisright == 1) {
			shiftXleft = 30;
			shiftXright = 85;
		}
	}
	
	public CArray getShifts() {
		CArray _shifts = array();
		Nest.value(_shifts,"shiftXleft").$(shiftXleft);
		Nest.value(_shifts,"shiftXright").$(shiftXright);
		Nest.value(_shifts,"shiftY").$(shiftY);
		Nest.value(_shifts,"height").$(sizeY);
		Nest.value(_shifts,"width").$(sizeX);
		return _shifts;
	}
	
	public void showWorkPeriod(int _value) {
		m_showWorkPeriod = (_value == 1) ? 1 : 0;
	}

	public void showTriggers(int _value) {
		m_showTriggers = (_value == 1) ? 1 : 0;
	}
	
	public void addItem(String _itemid) {
		addItem(_itemid, GRAPH_YAXIS_SIDE_DEFAULT);
	}
	public void addItem(String _itemid, int _axis) {
		addItem(_itemid, _axis, CALC_FNC_AVG);
	}
	public void addItem(String _itemid, int _axis, int _calc_fnc) {
		addItem(_itemid, _axis, _calc_fnc, null);
	}
	public void addItem(String _itemid, int _axis, int _calc_fnc, String _color) {
		addItem(_itemid, _axis, _calc_fnc, _color, null);
	}
	public void addItem(String _itemid, int _axis, int _calc_fnc, String _color, Integer _drawtype) {
		addItem(_itemid, _axis, _calc_fnc, _color, _drawtype, null);
	}
	public void addItem(String _itemid, Integer _axis, Integer _calc_fnc, String _color, Integer _drawtype, Integer _type) {
		if (type == GRAPH_TYPE_STACKED) {
			_drawtype = GRAPH_ITEM_DRAWTYPE_FILLED_REGION;
		}

		CArray<Map> _items = CMacrosResolverHelper.resolveItemNames(this.idBean, this.executor, array(get_item_by_itemid(this.executor, _itemid)));
		Map _item = reset(_items);

		Nest.value(_item,"name").$(Nest.value(_item,"name_expanded").$());

		items.put(num, _item);
		items.put(num, "delay", getItemDelay(Nest.value(_item,"delay").asInteger(), Nest.value(_item,"delay_flex").asString()));

		if (strpos(Nest.value(_item,"units").asString(), ",") == -1) {
			items.put(num, "unitsLong", "");
		}
		else {
			String[] ss = explode(",", Nest.value(_item,"units").asString());
			items.put(num, "units", ss[0]);
			items.put(num, "unitsLong", ss[1]);
		}

		Map _host = get_host_by_hostid(this.idBean, this.executor, Nest.value(_item,"hostid").asLong());

		items.put(num, "host", Nest.value(_host,"host").$());
		items.put(num, "hostname", Nest.value(_host,"name").$());
		items.put(num, "color", is_null(_color) ? "Dark Green" : _color);
		items.put(num, "drawtype", is_null(_drawtype) ? GRAPH_ITEM_DRAWTYPE_LINE : _drawtype);
		items.put(num, "axisside", is_null(_axis) ? GRAPH_YAXIS_SIDE_DEFAULT : _axis);
		items.put(num, "calc_fnc", is_null(_calc_fnc) ? CALC_FNC_AVG : _calc_fnc);
		items.put(num, "calc_type", is_null(_type) ? GRAPH_ITEM_SIMPLE : _type);

		if (Nest.value(items, asString(num), "axisside").asInteger() == GRAPH_YAXIS_SIDE_LEFT) {
			yaxisleft = 1;
		}

		if (Nest.value(items, asString(num), "axisside").asInteger() == GRAPH_YAXIS_SIDE_RIGHT) {
			yaxisright = 1;
		}

		num++;
	}
	


	public String setGraphOrientation(int _value, int _axisside) {
		if (_value < 0) {
			graphOrientation.put(_axisside, "-");
		}
		else if (rda_empty(graphOrientation.get(_axisside)) && _value > 0) {
			graphOrientation.put(_axisside, "+");
		}
		return graphOrientation.get(_axisside);
	}

	public void setYMinAxisType(int _yaxistype) {
		ymin_type = _yaxistype;
	}

	public void setYMaxAxisType(int _yaxistype) {
		ymax_type = _yaxistype;
	}

	public void setYAxisMin(Double _yaxismin) {
		yaxismin = _yaxismin;
	}

	public void setYAxisMax(Double _yaxismax) {
		yaxismax = _yaxismax;
	}

	public void setYMinItemId(String _itemid) {
		ymin_itemid = _itemid;
	}

	public void setYMaxItemId(String _itemid) {
		ymax_itemid = _itemid;
	}

	public void setLeftPercentage(float _percentile) {
		Nest.value(percentile,"left","percent").$(_percentile);
	}

	public void setRightPercentage(float _percentile) {
		Nest.value(percentile,"right","percent").$(_percentile);
	}

	protected void selectData() {
		data = array();
		long _now = time(null);

		if (!isset(stime)) {
			stime = _now - period;
		}

		diffTZ = (asLong(date("Z", stime)) - asLong(date("Z", stime + period)));
		from_time = stime; // + timeZone offset
		to_time = stime + period; // + timeZone offset

		long p = to_time - from_time; // graph size in time
		long z = p - from_time % p; // graphsize - mod(from_time,p) for Oracle...
		int x = sizeX; // graph size in px

		itemsHost = null;

		Map _config = select_config(this.idBean, this.executor);
		Map params = new HashMap();
		for (int i = 0; i < num; i++) {
			Map _item = get_item_by_itemid(this.executor, Nest.value(items, i, "itemid").asString());

			if (itemsHost == null) {
				itemsHost = Nest.value(_item,"hostid").$();
			}
			else if (!itemsHost.equals(Nest.value(_item,"hostid").$())) {
				itemsHost = false;
			}

			if (!isset(axis_valuetype, items.getNested(i, "axisside"))) {
				axis_valuetype.put(items.getNested(i, "axisside"), Nest.value(_item,"value_type").$());
			}
			else if (!axis_valuetype.get(items.getNested(i, "axisside")).equals(Nest.value(_item,"value_type").$())) {
				axis_valuetype.put(items.getNested(i, "axisside"), ITEM_VALUE_TYPE_FLOAT);
			}

			int _type = Nest.value(items, i, "calc_type").asInteger();
			long _from_time = from_time;
			long _to_time = to_time;
			String _calc_field = "round("+x+"*"+rda_sql_mod(rda_dbcast_2bigint("clock")+"+"+z, p)+"/("+p+"),0)"; // required for "group by" support of Oracle

			CArray<String> _sql_arr = array();

			// override item history setting with housekeeping settings
			if (Nest.value(_config,"hk_history_global").asBoolean()) {
				Nest.value(_item,"history").$(Nest.value(_config,"hk_history").$());
			}

			boolean _trendsEnabled = Nest.value(_config,"hk_trends_global").asBoolean() ? (Nest.value(_config,"hk_trends").asInteger() > 0) : (Nest.value(_item,"trends").asInteger() > 0);

			Object _itemId = items.getNested(i, "itemid");
			
			params.put("_itemId", _itemId);
			params.put("from_time", _from_time);
			params.put("to_time", _to_time);
			
			if (!_trendsEnabled
					|| ((Nest.value(_item,"history").asInteger() * SEC_PER_DAY) > (time() - (from_time + period / 2))
						&& (period / sizeX) <= (RDA_MAX_TREND_DIFF / RDA_GRAPH_MAX_SKIP_CELL))) {
				dataFrom = "history";

				array_push(_sql_arr,
					"SELECT itemid,"+_calc_field+" AS i,"+
						"COUNT(*) AS count,AVG(value) AS avg,MIN(value) as min,"+
						"MAX(value) AS max,MAX(clock) AS clock"+
					" FROM history "+
					" WHERE itemid=#{_itemId}"+
						" AND clock>=#{from_time}"+
						" AND clock<=#{to_time}"+
					" GROUP BY itemid,"+_calc_field
					,
					"SELECT itemid,"+_calc_field+" AS i,"+
						"COUNT(*) AS count,AVG(value) AS avg,MIN(value) AS min,"+
						"MAX(value) AS max,MAX(clock) AS clock"+
					" FROM history_uint "+
					" WHERE itemid=#{_itemId}"+
						" AND clock>=#{from_time}"+
						" AND clock<=#{to_time}"+
					" GROUP BY itemid,"+_calc_field
				);
			}
			else {
				dataFrom = "trends";

				array_push(_sql_arr,
					"SELECT itemid,"+_calc_field+" AS i,"+
						"SUM(num) AS count,AVG(value_avg) AS avg,MIN(value_min) AS min,"+
						"MAX(value_max) AS max,MAX(clock) AS clock"+
					" FROM trends"+
					" WHERE itemid=#{_itemId}"+
						" AND clock>=#{from_time}"+
						" AND clock<=#{to_time}"+
					" GROUP BY itemid,"+_calc_field
					,
					"SELECT itemid,"+_calc_field+" AS i,"+
						"SUM(num) AS count,AVG(value_avg) AS avg,MIN(value_min) AS min,"+
						"MAX(value_max) AS max,MAX(clock) AS clock"+
					" FROM trends_uint "+
					" WHERE itemid=#{_itemId}"+
						" AND clock>=#{from_time}"+
						" AND clock<=#{to_time}"+
					" GROUP BY itemid,"+_calc_field
				);

				Nest.value(items, i, "delay").$(max(Nest.value(items, i, "delay").asInteger(), SEC_PER_HOUR));
			}

			if (!isset(data, _itemId)) {
				data.put(_itemId, array());
			}

			if (!isset(Nest.value(data, _itemId, _type).$())) {
				data.put(_itemId, _type, array());
			}

			CArray _curr_data = Nest.value(data, _itemId, _type).$s(true);

			Nest.value(_curr_data,"count").$(null);
			Nest.value(_curr_data,"min").$(null);
			Nest.value(_curr_data,"max").$(null);
			Nest.value(_curr_data,"avg").$(null);
			Nest.value(_curr_data,"clock").$(null);

			for(String _sql: _sql_arr) {
				CArray<Map> _result = DBselect(this.executor, _sql, params);
				for (Map _row: _result) {
					int _idx = Nest.value(_row,"i").asInteger() - 1;
					if (_idx < 0) {
						continue;
					}

					/* --------------------------------------------------
						We are taking graph on 1px more than we need,
						and here we are skiping first px, because of MOD (in SELECT),
						it combines prelast point (it would be last point if not that 1px in begining)
						and first point, but we still losing prelast point :(
						but now we've got the first point.
					--------------------------------------------------*/
					_curr_data.put("count",_idx, Nest.value(_row,"count").$());
					_curr_data.put("min",_idx, Nest.value(_row,"min").$());
					_curr_data.put("max",_idx, Nest.value(_row,"max").$());
					_curr_data.put("avg",_idx, Nest.value(_row,"avg").$());
					_curr_data.put("clock",_idx, Nest.value(_row,"clock").$());
					_curr_data.put("shift_min",_idx, 0);
					_curr_data.put("shift_max",_idx, 0);
					_curr_data.put("shift_avg",_idx, 0);
				}

				int _loc_min = is_array(Nest.value(_curr_data,"min").$()) ? min(Nest.array(_curr_data,"min").asDouble()).intValue() : 0;
				setGraphOrientation(_loc_min, Nest.value(items, i, "axisside").asInteger());
//				unset(_row);
			}
			Nest.value(_curr_data,"avg_orig").$(is_array(Nest.value(_curr_data,"avg").$()) ? rda_avg(Nest.value(_curr_data,"avg").$()) : null);

			// calculate missed points
			int _first_idx = 0;

			int _ci, _cj;
			/*
				first_idx - last existing point
				ci - current index
				cj - count of missed in one go
				dx - offset to first value (count to last existing point)
			*/
			for (_ci = 0, _cj = 0; _ci < sizeX; _ci++) {
				if (!isset(_curr_data.getNested("count", _ci)) || (Nest.value(_curr_data, "count", _ci).asInteger() == 0)) {
					_curr_data.put("count",_ci, 0);
					_curr_data.put("shift_min",_ci, 0);
					_curr_data.put("shift_max",_ci, 0);
					_curr_data.put("shift_avg",_ci, 0);
					_cj++;
					continue;
				}

				if (_cj == 0) {
					continue;
				}

				int _dx = _cj + 1;
				_first_idx = _ci - _dx;

				if (_first_idx < 0) {
					_first_idx = _ci; // if no data from start of graph get current data as first data
				}

				for(; _cj > 0; _cj--) {
					if (_dx < (sizeX / 20) && type == GRAPH_TYPE_STACKED) {
						_curr_data.put("count", _ci - (_dx - _cj), 1);
					}

					for(String _var_name: array("clock", "min", "max", "avg")) {
						Map _var = (Map)_curr_data.get(_var_name);

						if (_first_idx == _ci && "clock".equals(_var_name)) {
							_var.put(_ci - (_dx - _cj), Nest.value(_var, _first_idx).asInteger() - (((double)p / sizeX) * (_dx - _cj)));
							continue;
						}

						double _dy = Nest.value(_var, _ci).asDouble() - Nest.value(_var, _first_idx).asDouble();
						_var.put(_ci - (_dx - _cj), bcadd(Nest.value(_var, _first_idx).asDouble() , bcdiv((_cj * _dy) , _dx)));
					}
				}
			}

			if (_cj > 0 && _ci > _cj) {
				int _dx = _cj + 1;
				_first_idx = _ci - _dx;

				for(;_cj > 0; _cj--) {
					for(String _var_name: array("clock", "min", "max", "avg")) {
						Map _var = (Map)_curr_data.get(_var_name);

						if ("clock".equals(_var_name)) {
							_var.put(_first_idx + (_dx - _cj), Nest.value(_var, _first_idx).asInteger() + (((double)p / sizeX) * (_dx - _cj)));
							continue;
						}
						_var.put(_first_idx + (_dx - _cj), _var.get(_first_idx));
					}
				}
			}
		}

		// calculate shift for stacked graphs
		if (type == GRAPH_TYPE_STACKED) {
			for (int i = 1; i < num; i++) {
				Map _curr_data = (Map)data.getNested(items.getNested(i, "itemid"), items.getNested(i, "calc_type"));

				if (!isset(_curr_data)) {
					continue;
				}

				for (int j = i - 1; j >= 0; j--) {
					if (Nest.value(items, j, "axisside").asInteger() != Nest.value(items, i, "axisside").asInteger()) {
						continue;
					}

					Map _prev_data = (Map)data.getNested(items.getNested(j, "itemid"), items.getNested(j, "calc_type"));

					if (!isset(_prev_data)) {
						continue;
					}

					for (int _ci = 0; _ci < sizeX; _ci++) {
						for(String _var_name: array("min", "max", "avg")) {
							String _shift_var_name = "shift_"+_var_name;
							Map _curr_shift = (Map)_curr_data.get(_shift_var_name);
							//Map _curr_var = (Map)_curr_data.get(_var_name);
							Map _prev_shift = (Map)_prev_data.get(_shift_var_name);
							Map _prev_var = (Map)_prev_data.get(_var_name);
							_curr_shift.put(_ci, Nest.value(_prev_var, _ci).asFloat() + Nest.value(_prev_shift, _ci).asFloat());
						}
					}
					break;
				}
			}
		}
	}
	
	/********************************************************************************************************/
	// CALCULATIONS
	/********************************************************************************************************/
	protected void calcTriggers(SQLExecutor executor) {
		if (m_showTriggers != 1) {
			return;
		}

		int _max = 3;
		int _cnt = 0;

		Map params = new HashMap();
		for(Entry<Object, Map> entry: items.entrySet()) {
			Object _inum = entry.getKey();
			Map _item = entry.getValue();
			params.put("itemid", Nest.value(_item,"itemid").$());
			CArray<Map> _db_triggers = DBselect( executor,
				"SELECT DISTINCT h.host,tr.description,tr.triggerid,tr.expression,tr.priority,tr.value"+
				" FROM triggers tr,functions f,items i,hosts h"+
				" WHERE tr.triggerid=f.triggerid"+
					" AND f.function IN (\"last\",\"min\",\"avg\",\"max\")"+
					" AND tr.status="+TRIGGER_STATUS_ENABLED+
					" AND i.itemid=f.itemid"+
					" AND h.hostid=i.hostid"+
					" AND f.itemid=#{itemid}"+
				" ORDER BY tr.priority",
				params
			);
			for(Map _trigger: _db_triggers) {
				if(!(_cnt < _max)) break;
				
				CArray<Map> _db_fnc_cnt = DBselect(executor, "SELECT COUNT(*) AS cnt FROM functions f WHERE f.triggerid="+Nest.value(_trigger,"triggerid").$());
				Map _fnc_cnt = DBfetch(_db_fnc_cnt);

				if (Nest.value(_fnc_cnt,"cnt").asInteger() != 1) {
					continue;
				}

				Nest.value(_trigger,"expression").$(CMacrosResolverHelper.resolveTriggerExpressionUserMacro(this.idBean, executor, CArray.valueOf(_trigger)));

				CArray<String> _arr = array();
				if (0 == preg_match("^\\{([0-9]+)\\}\\s*?([\\<\\>\\=]{1})\\s*?([\\-0-9\\.]+)([TGMKsmhdw]?)$", Nest.value(_trigger,"expression").asString(), _arr)) {
					continue;
				}

				int _val = asInteger(convert(_arr.get(3)+_arr.get(4)));

				double _minY =  Nest.value(m_minY, items.getNested(_inum, "axisside")).asDouble();
				double _maxY = Nest.value(m_maxY, items.getNested(_inum, "axisside")).asDouble();

				triggers.add(map(
					"skipdraw", (_val <= _minY || _val >= _maxY),
					"y", sizeY - ((_val - _minY) / (_maxY - _minY)) * sizeY + shiftY,
					"color", getSeverityColor(this.idBean, this.executor, Nest.value(_trigger,"priority").asInteger()),
					"description", _("Trigger")+NAME_DELIMITER+CMacrosResolverHelper.resolveTriggerName(this.idBean, executor, _trigger),
					"constant", "["+_arr.get(2)+" "+_arr.get(3)+_arr.get(4)+"]"
				));
				++_cnt;
			}
		}
	}
	
	// calculates percentages for left & right Y axis
	protected void calcPercentile() {
		if (type != GRAPH_TYPE_NORMAL) {
			return ;
		}

		CArray<CArray> _values = map(
			"left" , array(),
			"right", array()
		);

		int _maxX = sizeX;

		// for each metric
		for (int _item = 0; _item < num; _item++) {
			Map _data = (Map)data.getNested(items.getNested(_item, "itemid"), items.getNested(_item, "calc_type"));

			if (!isset(_data)) {
				continue;
			}

			// for each X
			for (int i = 0; i < _maxX; i++) { // new point
				if (Nest.value(_data, "count", i).asInteger() == 0 && i != (_maxX - 1)) {
					continue;
				}

				double _min = Nest.value(_data, "min", i).asDouble();
				double _max = Nest.value(_data, "max", i).asDouble();
				double _avg = Nest.value(_data, "avg", i).asDouble();

				double _value;
				switch (Nest.value(items, _item, "calc_fnc").asInteger()) {
					case CALC_FNC_MAX:
						_value = _max;
						break;
					case CALC_FNC_MIN:
						_value = _min;
						break;
					case CALC_FNC_ALL:
					case CALC_FNC_AVG:
					default:
						_value = _avg;
				}

				if (Nest.value(items, _item, "axisside").asInteger() == GRAPH_YAXIS_SIDE_LEFT) {
					Nest.value(_values, "left").push(_value);
				}
				else {
					Nest.value(_values, "right").push(_value);
				}
			}
		}

		for(Entry<Object, CArray<Float>> entry: percentile.entrySet()) {
			Object _side = entry.getKey();
			CArray<Float> _percentile = entry.getValue();
			
			if (Nest.value(_percentile,"percent").asInteger() > 0 && !empty(_values.get(_side))) {
				sort(_values.get(_side));
				
				int _percent = (int) ((count(_values.get(_side)) * Nest.value(percentile,"percent").asDouble() / 100) + 0.5d);
				Nest.value(percentile, _side, "value").$(Nest.value(_values,_side,_percent).$());
				unset(_values, _side);
			}
		}
	}
	
	// calculation of minimum Y axis
	protected Double calculateMinY(SQLExecutor executor, int _side) {
		if (ymin_type == GRAPH_YAXIS_TYPE_FIXED) {
			return yaxismin;
		}

		if (ymin_type == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
			Map _item = get_item_by_itemid(executor, ymin_itemid);
			CArray _history = Manager.History(idBean, executor).getLast(array(_item));
			if (isset(_history, _item.get("itemid"))) {
				return Nest.value(_history, _item.get("itemid"), 0, "value").asDouble();
			}
		}

		Double _minY = null;
		for (int i = 0; i < num; i++) {
			if (Nest.value(items, i, "axisside").asInteger() != _side) {
				continue;
			}

			if (!isset(data.getNested(items.getNested(i, "itemid"), GRAPH_ITEM_SIMPLE))) {
				continue;
			}

			Map _data = (Map)data.getNested(items.getNested(i, "itemid"), GRAPH_ITEM_SIMPLE);

			if (!isset(_data)) {
				continue;
			}
			
			_data = Clone.deepcopy(_data);

			int _calc_fnc = Nest.value(items, i, "calc_fnc").asInteger();

			CArray<Double> _val, _shift_val;
			switch (_calc_fnc) {
				case CALC_FNC_ALL:
				case CALC_FNC_MIN:
					_val = Nest.value(_data,"min").asCArray();
					_shift_val = Nest.value(_data,"shift_min").asCArray();
					break;
				case CALC_FNC_MAX:
					_val = Nest.value(_data,"max").asCArray();
					_shift_val = Nest.value(_data,"shift_max").asCArray();
					break;
				case CALC_FNC_AVG:
				default:
					_val = Nest.value(_data,"avg").asCArray();
					_shift_val = Nest.value(_data,"shift_avg").asCArray();
			}

			if (!isset(_val)) {
				continue;
			}

			if (type == GRAPH_TYPE_STACKED) {
				double _min_val_shift = min(count(_val), count(_shift_val));
				for (int _ci = 0; _ci < _min_val_shift; _ci++) {
					if (Nest.value(_shift_val, _ci).asDouble() < 0) {
						Nest.value(_val, _ci).plus(bcadd(Nest.value(_shift_val, _ci).asDouble(), Nest.value(_val, _ci).asDouble()));
					}
				}
			}

			if (!isset(_minY)) {
				if (isset(_val) && count(_val) > 0) {
					_minY = asDouble(min(_val));
				}
			}
			else {
				_minY = asDouble(min((double)_minY, asDouble(min(_val))));
			}
		}

		return _minY;
	}
	
	// calculation of maximum Y of a side (left/right)
	protected Double calculateMaxY(SQLExecutor executor, int _side) {
		if (ymax_type == GRAPH_YAXIS_TYPE_FIXED) {
			return yaxismax;
		}

		if (ymax_type == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
			Map _item = get_item_by_itemid(executor, ymax_itemid);
			CArray _history = Manager.History(idBean, executor).getLast(array(_item));
			if (isset(_history, _item.get("itemid"))) {
				return Nest.value(_history, _item.get("itemid"), 0, "value").asDouble();
			}
		}

		Double _maxY = null;
		for (int i = 0; i < num; i++) {
			if (Nest.value(items, i, "axisside").asInteger() != _side) {
				continue;
			}

			if (!isset(Nest.value(data, items.getNested(i, "itemid"), GRAPH_ITEM_SIMPLE).$())) {
				continue;
			}

			Map _data = (Map)Nest.value(data, items.getNested(i, "itemid"), GRAPH_ITEM_SIMPLE).$();

			if (!isset(_data)) {
				continue;
			}
			
			_data = Clone.deepcopy(_data);

			int _calc_fnc = Nest.value(items, i, "calc_fnc").asInteger();

			CArray<Double> _val, _shift_val;
			switch (_calc_fnc) {
				case CALC_FNC_ALL:
				case CALC_FNC_MAX:
					_val = Nest.value(_data,"max").asCArray();
					_shift_val = Nest.value(_data,"shift_max").asCArray();
					break;
				case CALC_FNC_MIN:
					_val = Nest.value(_data,"min").asCArray();
					_shift_val = Nest.value(_data,"shift_min").asCArray();
					break;
				case CALC_FNC_AVG:
				default:
					_val = Nest.value(_data,"avg").asCArray();
					_shift_val = Nest.value(_data,"shift_avg").asCArray();
			}

			if (!isset(_val)) {
				continue;
			}
			
			for (int _ci = 0; _ci < min(count(_val), count(_shift_val)); _ci++) {
				if (Nest.value(_data, "count", _ci).asInteger() == 0) {
					continue;
				}

				_val.put(_ci, bcadd(Nest.value(_shift_val, _ci).asDouble(), Nest.value(_val, _ci).asDouble()));
			}

			if (!isset(_maxY)) {
				if (isset(_val) && count(_val) > 0) {
					_maxY = asDouble(max(_val));
				}
			}
			else {
				_maxY = asDouble(max(_maxY, asDouble(max(_val))));
			}
		}

		return _maxY;
	}
	
	/**
	 * Check if Y axis min value is larger than Y axis max value. Show error instead of graph if true.
	 *
	 * @param float _min		Y axis min value
	 * @param float _max		Y axis max value
	 */
	protected void validateMinMax(double _min, double _max) {
		if (bccomp(_min, _max) == 0 || bccomp(_min, _max) == 1) {
			show_error_message(_("Y axis MAX value must be greater than Y axis MIN value."));
			throw new RuntimeException("validateMinMax "+ _min + " - " + _max);
		}
	}

	protected void calcZero() {
		CArray<Integer> _sides = array();
		if (isset(axis_valuetype, GRAPH_YAXIS_SIDE_RIGHT)) {
			_sides.add(GRAPH_YAXIS_SIDE_RIGHT);
		}

		if (isset(axis_valuetype, GRAPH_YAXIS_SIDE_LEFT) || !isset(_sides)) {
			_sides.add(GRAPH_YAXIS_SIDE_LEFT);
		}
		
		for(Entry<Object, Integer> entry: _sides.entrySet()) {
			//Object _num = entry.getKey();
			Integer _side = entry.getValue();

			unit2px.put(_side, (Nest.value(m_maxY, _side).asDouble() - Nest.value(m_minY, _side).asDouble()) / sizeY);
			if (Nest.value(gridStep, _side).asDouble() == 0) {
				unit2px.put(_side, 1);
			}

			if (Nest.value(m_minY, _side).asDouble() > 0) {
				zero.put(_side, sizeY + shiftY);
				if (bccomp(Nest.value(m_minY, _side).asDouble(), Nest.value(m_maxY, _side).asDouble()) == 1) {
					oxy.put(_side, Nest.value(m_maxY, _side).asDouble());
				}
				else {
					oxy.put(_side, Nest.value(m_minY, _side).asDouble());
				}
			}
			else if (Nest.value(m_maxY, _side).asDouble() < 0) {
				zero.put(_side, shiftY);
				if (bccomp(Nest.value(m_minY, _side).asDouble(), Nest.value(m_maxY, _side).asDouble()) == 1) {
					oxy.put(_side, Nest.value(m_minY, _side).asDouble());
				}
				else {
					oxy.put(_side, Nest.value(m_maxY, _side).asDouble());
				}
			}
			else {
				zero.put(_side, sizeY + shiftY - (int) abs(Nest.value(m_minY, _side).asDouble() / Nest.value(gridStep, _side).asDouble()));
				oxy.put(_side, 0);
			}
		}
	}
	
	protected void calcMinMaxInterval() {
		// init intervals
		CArray<Float> _intervals = array();
		for(int _num: array(1, 2, 3, 4)) {
			double _dec = Math.pow(0.1, _num);
			for(int _int: array(1, 2, 5)) {
				_intervals.add(bcmul(_int, _dec));
			}
		}

		// check if items use B or Bps units
		boolean _leftBase1024 = false;
		boolean _rightBase1024 = false;

		for (int _item = 0; _item < num; _item++) {
			if (Nest.value(items, asString(_item), "units").asString().equals("B") ||  Nest.value(items, asString(_item), "units").asString().equals("Bps")) {
				if (Nest.value(items, asString(_item), "axisside").asInteger() == GRAPH_YAXIS_SIDE_LEFT) {
					_leftBase1024 = true;
				}
				else {
					_rightBase1024 = true;
				}
			}
		}

		for(int _num: array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)) {
			double _dec = Math.pow(10, _num);
			for(Integer _int: array(1, 2, 5)) {
				_intervals.add(bcmul(_int, _dec));
			}
		}

		CArray<Integer> _sides = array();
		if (isset(axis_valuetype.get(GRAPH_YAXIS_SIDE_RIGHT))) {
			_sides.add(GRAPH_YAXIS_SIDE_RIGHT);
		}

		if (isset(axis_valuetype.get(GRAPH_YAXIS_SIDE_LEFT)) || !isset(_sides)) {
			_sides.add(GRAPH_YAXIS_SIDE_LEFT);
		}


		for(Entry<Object, Integer> entry: _sides.entrySet()) {
			//Object _snum = entry.getKey();
			Integer _side = entry.getValue();
			if (!isset(axis_valuetype.get(_side))) {
				continue;
			}

			if ((ymin_type != GRAPH_YAXIS_TYPE_FIXED || ymax_type != GRAPH_YAXIS_TYPE_CALCULATED)
					&& type == GRAPH_TYPE_STACKED) {
				m_minY.put(_side, min(Nest.value(m_minY, _side).asDouble(), 0));
				validateMinMax(Nest.value(m_minY, _side).asDouble(), Nest.value(m_maxY, _side).asDouble());
				continue;
			}

			if (ymax_type == GRAPH_YAXIS_TYPE_FIXED) {
				m_maxY.put(_side, yaxismax);
				if (ymin_type == GRAPH_YAXIS_TYPE_CALCULATED
						&& (Nest.value(m_minY, _side).$() == null || bccomp(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble()) == 0
								|| bccomp(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble()) == -1)) {
					if (Nest.value(m_maxY, _side).asDouble() == 0) {
						m_minY.put(_side, -1);
					}
					else if (Nest.value(m_maxY, _side).asDouble() > 0) {
						m_minY.put(_side, bcmul(Nest.value(m_maxY, _side).asDouble(), 0.8));
					}
					else {
						m_minY.put(_side, bcmul(Nest.value(m_maxY, _side).asDouble(), 1.2));
					}
				}
			}

			if (ymin_type == GRAPH_YAXIS_TYPE_FIXED) {
				m_minY.put(_side, yaxismin);
				if (ymax_type == GRAPH_YAXIS_TYPE_CALCULATED
						&& (Nest.value(m_maxY, _side).$() == null || bccomp(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble()) == 0
								|| bccomp(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble()) == -1)) {
					if (Nest.value(m_minY, _side).asDouble() > 0) {
						m_maxY.put(_side, bcmul(Nest.value(m_minY, _side).asDouble(), 1.2));
					}
					else {
						m_maxY.put(_side, bcmul(Nest.value(m_minY, _side).asDouble(), 0.8));
					}
				}
			}

			validateMinMax(Nest.value(m_minY, _side).asDouble(), Nest.value(m_maxY, _side).asDouble());
		}

		int _side = GRAPH_YAXIS_SIDE_LEFT;
		int _other_side = GRAPH_YAXIS_SIDE_RIGHT;

		// invert sides and it bases, if left side not exist
		if (!isset(axis_valuetype.get(GRAPH_YAXIS_SIDE_LEFT))) {
			_side = GRAPH_YAXIS_SIDE_RIGHT;
			_other_side = GRAPH_YAXIS_SIDE_LEFT;
			boolean _tempBase = _leftBase1024;
			_leftBase1024 = _rightBase1024;
			_rightBase1024 = _tempBase;
		}

		if (!isset(Nest.value(m_minY, _side).asDouble())) {
			m_minY.put(_side, 0);
		}
		if (!isset(Nest.value(m_maxY, _side).asDouble())) {
			m_maxY.put(_side, 0);
		}

		if (!isset(Nest.value(m_minY, _other_side).asDouble())) {
			m_minY.put(_other_side, 0);
		}
		if (!isset(Nest.value(m_maxY, _other_side).asDouble())) {
			m_maxY.put(_other_side, 0);
		}

		CArray<Double> _tmp_minY = Clone.deepcopy(m_minY);
		CArray<Double> _tmp_maxY = Clone.deepcopy(m_maxY);

		// calc interval
		Float _columnInterval = bcdiv(bcmul(gridPixelsVert, (bcsub(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble()))), sizeY);

		double _dist = bcmul(5, bcpow(10, 18));

		float _interval = 0;
		for(float _int: _intervals) {
			float t;
			// we must get a positive number
			if (bccomp(_int, _columnInterval) == -1) {
				t = bcsub(_columnInterval, _int);
			}
			else {
				t = bcsub(_int, _columnInterval);
			}

			if (bccomp(t, _dist) == -1) {
				_dist = t;
				_interval = _int;
			}
		}

		// calculate interval, if left side use B or Bps
		if (_leftBase1024) {
			_interval = getBase1024Interval(_interval, Nest.value(m_minY, _side).asInteger(), Nest.value(m_maxY, _side).asInteger());
		}

		_columnInterval = bcdiv(bcmul(gridPixelsVert, bcsub(Nest.value(m_maxY, _other_side).asDouble(), Nest.value(m_minY, _other_side).asDouble())), sizeY);

		_dist = bcmul(5, bcpow(10, 18));

		float _interval_other_side = 0;
		for(float _int: _intervals) {
			float t;
			// we must get a positive number
			if (bccomp(_int, _columnInterval) == -1) {
				t = bcsub(_columnInterval, _int);
			}
			else {
				t = bcsub(_int, _columnInterval);
			}

			if (bccomp(t,_dist) == -1) {
				_dist = t;
				_interval_other_side = _int;
			}
		}

		// calculate interval, if right side use B or Bps
		if (_rightBase1024) {
			_interval_other_side = getBase1024Interval(_interval_other_side, Nest.value(m_minY, _other_side).asInteger(), Nest.value(m_maxY, _other_side).asInteger());
		}

		// save original min and max items values
		CArray<Integer> _minY = array(), _maxY = array();
		for(Integer _graphSide: _sides) {
			_minY.put(_graphSide, Nest.value(m_minY, _graphSide).asDouble());
			_maxY.put(_graphSide, Nest.value(m_maxY, _graphSide).asDouble());
		}

		if (!isset(_minY.get(_side))) {
			_minY.put(_side, 0);
		}
		if (!isset(_maxY.get(_side))) {
			_maxY.put(_side, 0);
		}

		// correcting MIN & MAX
		m_minY.put(_side, bcmul(bcfloor(bcdiv(Nest.value(m_minY, _side).asDouble(), _interval)), _interval));
		m_maxY.put(_side, bcmul(bcceil(bcdiv(Nest.value(m_maxY, _side).asDouble(), _interval)), _interval));
		m_minY.put(_other_side, bcmul(bcfloor(bcdiv(Nest.value(m_minY, _other_side).asDouble(), _interval_other_side)), _interval_other_side));
		m_maxY.put(_other_side, bcmul(bcceil(bcdiv(Nest.value(m_maxY, _other_side).asDouble(), _interval_other_side)), _interval_other_side));

		float _tmpInterval;
		// add intervals so min/max Y wouldn't be at the top
		for(Integer _graphSide: _sides) {
			if (_graphSide == _side) {
				_tmpInterval = _interval;
			}
			else {
				_tmpInterval = _interval_other_side;
			}

			if (bccomp(Nest.value(m_minY, _graphSide).asDouble(), _minY.get(_side)) == 0
					&& Nest.value(m_minY, _graphSide).$() != null && Nest.value(m_minY, _graphSide).asDouble() != 0) {
				m_minY.put(_graphSide,  bcsub(Nest.value(m_minY, _graphSide).asDouble(), _tmpInterval));
			}

			if (bccomp(Nest.value(m_maxY, _graphSide).asDouble(), _maxY.get(_graphSide)) == 0
					&& Nest.value(m_maxY, _graphSide).$() != null && Nest.value(m_maxY, _graphSide).asDouble() != 0) {
				m_maxY.put(_graphSide,  bcadd(Nest.value(m_maxY, _graphSide).asDouble(), _tmpInterval));
			}
		}

		// calculate interval count for main and other side
		gridLinesCount.put(_side, bcceil(bcdiv(bcsub(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble()), _interval)));
		gridLinesCount.put(_other_side, bcceil(bcdiv(bcsub(Nest.value(m_maxY, _other_side).asDouble(), Nest.value(m_minY, _other_side).asDouble()), _interval_other_side)));

		m_maxY.put(_side, bcadd(Nest.value(m_minY, _side).asDouble(), bcmul(_interval, Nest.value(gridLinesCount, _side).asDouble()))); 
		gridStep.put(_side, _interval);

		if (isset(axis_valuetype.get(_other_side))) {
			// other side correction
			_dist = bcsub(Nest.value(m_maxY, _other_side).asDouble(), Nest.value(m_minY, _other_side).asDouble());
			_interval = 1;

			for(float _int: _intervals) {
				if (bccomp(_dist, bcmul(Nest.value(gridLinesCount, _side).asDouble(), _int)) == -1) {
					_interval = _int;
					break;
				}
			}

			// correcting MIN & MAX
			m_minY.put(_other_side, bcmul(bcfloor(bcdiv(Nest.value(m_minY, _other_side).asDouble(), _interval)), _interval));
			m_maxY.put(_other_side, bcmul(bcceil(bcdiv(Nest.value(m_maxY, _other_side).asDouble(), _interval)), _interval));

			// if we lowered min more than highed max - need additional recalculating
			if (bccomp(_tmp_maxY.get(_other_side), Nest.value(m_maxY, _other_side).asDouble()) == 1 || bccomp(_tmp_minY.get(_other_side), Nest.value(m_minY, _other_side).asDouble()) == -1) {
				_dist = bcsub(Nest.value(m_maxY, _other_side).asDouble(), Nest.value(m_minY, _other_side).asDouble());
				_interval = 0;
				for(float _int: _intervals) {
					if (bccomp(_dist, bcmul(Nest.value(gridLinesCount, _side).asDouble(), _int)) == -1) {
						_interval = _int;
						break;
					}
				}

				// recorrecting MIN & MAX
				m_minY.put(_other_side, bcmul(bcfloor(bcdiv(Nest.value(m_minY, _other_side).asDouble(), _interval)), _interval));
				m_maxY.put(_other_side, bcmul(bcceil(bcdiv(Nest.value(m_maxY, _other_side).asDouble(), _interval)), _interval));
			}

			// calculate interval, if right side use B or Bps
			if (isset(_rightBase1024)) {
				_interval = getBase1024Interval(_interval, Nest.value(m_minY, _side).asInteger(), Nest.value(m_maxY, _side).asInteger());
				// recorrecting MIN & MAX
				m_minY.put(_other_side, bcmul(bcfloor(bcdiv(Nest.value(m_minY, _other_side).asDouble(), _interval)), _interval));
				m_maxY.put(_other_side, bcmul(bcceil(bcdiv(Nest.value(m_maxY, _other_side).asDouble(), _interval)), _interval));
			}

			gridLinesCount.put(_other_side, Nest.value(gridLinesCount, _side).asDouble());
			m_maxY.put(_other_side, bcadd(Nest.value(m_minY, _other_side).asDouble(), bcmul(_interval, Nest.value(gridLinesCount, _other_side).asDouble())));
			gridStep.put(_other_side, _interval);
		}

		for(int _graphSide: _sides) {
			if (!isset(axis_valuetype.get(_graphSide))) {
				continue;
			}

			if (type == GRAPH_TYPE_STACKED) {
				m_minY.put(_graphSide,  bccomp(_tmp_minY.get(GRAPH_YAXIS_SIDE_LEFT), 0) == -1 ? _tmp_minY.get(GRAPH_YAXIS_SIDE_LEFT) : 0);
			}

			if (ymax_type == GRAPH_YAXIS_TYPE_FIXED) {
				m_maxY.put(_graphSide,  yaxismax);
			}
			else if (ymax_type == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				m_maxY.put(_graphSide,  Nest.as(_tmp_maxY.get(_graphSide)).asDouble());
			}

			if (ymin_type == GRAPH_YAXIS_TYPE_FIXED) {
				m_minY.put(_graphSide,  yaxismin);
			}
			else if (ymin_type == GRAPH_YAXIS_TYPE_ITEM_VALUE) {
				m_minY.put(_graphSide,  Nest.as(_tmp_minY.get(_graphSide)).asDouble());
			}

			validateMinMax(Nest.value(m_minY, _graphSide).asDouble(), Nest.value(m_maxY, _graphSide).asDouble());
		}

		// division by zero
		float _diff_val = bcsub(Nest.value(m_maxY, _side).asDouble(), Nest.value(m_minY, _side).asDouble());
		if (bccomp(_diff_val, 0) == 0) {
			_diff_val = 1;
		}

		gridStepX.put(_side, bcdiv(bcmul(Nest.value(gridStep, _side).asDouble(), sizeY), _diff_val));

		if (isset(axis_valuetype.get(_other_side))) {
			_diff_val = bcsub(Nest.value(m_maxY, _other_side).asDouble(), Nest.value(m_minY, _other_side).asDouble());
			if (bccomp(_diff_val, 0) == 0) {
				_diff_val = 1;
			}
			gridStepX.put(_other_side, bcdiv(bcmul(Nest.value(gridStep, _other_side).asDouble(), sizeY), _diff_val));
		}
	}

	private void calcTimeInterval() {
		this.grid.put("horizontal", map("sub", array(), "main", array()));
		
		// align to the closest human time interval
		double _raw_time_interval = (double)(gridPixels*period)/sizeX;
		CArray<CArray<Object>> _intervals = array(
				map("main", 3600, "sub", 60			),// 1 minute     
				map("main", 3600, "sub", 120			),// 5 minutes    
				map("main", 3600, "sub", 300			),// 5 minutes    
				map("main", 3600, "sub", 900			),// 15 minutes   
				map("main", 3600, "sub", 1800		),// 30 minutes   
				map("main", 86400, "sub", 3600		),// 1 hour       
				map("main", 86400, "sub", 10800		),// 3 hours      
				map("main", 86400, "sub", 21600		),// 6 hours      
				map("main", 86400, "sub", 43200		),// 12 hours     
				map("main", 604800, "sub", 86400		),// 1 day        
				map("main", 1209600, "sub", 604800	),// 1 week       
				map("main", 2419200, "sub", 1209600	),// 2 weeks      
				map("main", 4838400, "sub", 2419200	),// 4 weeks      
				map("main", 9676800, "sub", 4838400	),// 8 weeks      
				map("main", 19353600, "sub", 9676800	) // 16 weeks     
			);

		int _dist = 19353600; //def week;
		double _sub_interval = 0;
		double _main_interval = 0;

		int t;
		for(CArray _int: _intervals) {
			t = (int)abs(Nest.value(_int,"sub").asInteger() - _raw_time_interval);

			if (t < _dist) {
				_dist = t;
				_sub_interval = Nest.value(_int,"sub").asInteger();
				_main_interval = Nest.value(_int,"main").asInteger();
			}
		}

		// sub
		double _intervalX = (_sub_interval * sizeX) / period;
		
		double _offset;
		double _offsetX;
		if (_sub_interval > SEC_PER_DAY) {
			_offset = (7 - asInteger(date("w", from_time))) * SEC_PER_DAY;
			_offset += diffTZ;

			long _next = from_time + (long)_offset;

			_offset = mktime(0, 0, 0, asInteger(date("M", _next)), asInteger(date("d", _next)), asInteger(date("YYYY", _next))) - from_time;
			_offsetX = (double)_offset * (sizeX / period);
		} else {
			_offset = _sub_interval - ((from_time + asInteger(date("Z", from_time))) % _sub_interval);
			_offsetX = (double)(_offset * sizeX) / period;
		}
		
		int _vline_count = floor((period-_offset) / _sub_interval);

		int _start_i = 0;
		if (_offsetX < 12) {
			_start_i++;
		}

		while ((sizeX - (_offsetX + (_vline_count*_intervalX))) < 12) {
			_vline_count--;
		}

		Map _sub = Nest.value(grid,"horizontal","sub").$s();
		Nest.value(_sub,"interval").$(_sub_interval);
		Nest.value(_sub,"linecount").$(_vline_count);
		Nest.value(_sub,"intervalx").$(_intervalX);
		Nest.value(_sub,"offset").$(_offset);
		Nest.value(_sub,"offsetx").$(_offsetX);
		Nest.value(_sub,"start").$(_start_i);

		// main
		_intervalX = (_main_interval * sizeX) / period;

		if (_main_interval > SEC_PER_DAY) {
			_offset = (7 - asInteger(date("w", from_time))) * SEC_PER_DAY;
			_offset += diffTZ;
			long _next = from_time + (long)_offset;

			_offset = mktime(0, 0, 0, asInteger(date("m", _next)), asInteger(date("d", _next)), asInteger(date("Y", _next))) - from_time;
			_offsetX = (double)_offset * (sizeX / period);
		}
		else {
			_offset = _main_interval - ((from_time + (asInteger(date("Z", from_time)))) % _main_interval);
			_offset += diffTZ;
			_offsetX = (double)_offset * (sizeX / period);
		}

		_vline_count = floor((period-_offset) / _main_interval);

		_start_i = 0;
		if (_offsetX < 12) {
			_start_i++;
		}

		while ((sizeX - (_offsetX + (_vline_count*_intervalX))) < 12) {
			_vline_count--;
		}

		Map _main = Nest.value(grid,"horizontal","main").$s();
		Nest.value(_main,"interval").$(_main_interval);
		Nest.value(_main,"linecount").$(_vline_count);
		Nest.value(_main,"intervalx").$(_intervalX);
		Nest.value(_main,"offset").$(_offset);
		Nest.value(_main,"offsetx").$(_offsetX);
		Nest.value(_main,"start").$(_start_i);
	}
	
	/********************************************************************************************************/
	// DRAW ELEMENTS
	/********************************************************************************************************/
	public void drawXYAxisScale() {
		int _gbColor = getColor(Nest.value(graphtheme,"gridbordercolor").asString(), 0).getRGB();
		
		dashedRectangle(
			g2d,
			shiftXleft + shiftXCaption - 1,
			shiftY - 1,
			sizeX + shiftXleft + shiftXCaption,
			sizeY + shiftY + 1,
			getColor(Nest.value(graphtheme,"gridcolor").asString(), 0).getRGB()
		);

		if (!empty(yaxisleft)) {
			rda_imageline(
				g2d,
				shiftXleft + shiftXCaption - 1,
				shiftY - 5,
				shiftXleft + shiftXCaption - 1,
				sizeY + shiftY + 4,
				_gbColor
			);

			imagefilledpolygon(
				g2d,
				array(
					shiftXleft + shiftXCaption - 4, shiftY - 5,
					shiftXleft + shiftXCaption + 2, shiftY - 5,
					shiftXleft + shiftXCaption - 1, shiftY - 10
				),
				3,
				getColor("White")
			);

			/* draw left axis triangle */
			rda_imageline(g2d, shiftXleft + shiftXCaption - 4, shiftY - 5,
					shiftXleft + shiftXCaption + 2, shiftY - 5,
					_gbColor);
			rda_imagealine(im, g2d, shiftXleft + shiftXCaption - 4, shiftY - 5,
					shiftXleft + shiftXCaption - 1, shiftY - 10,
					_gbColor);
			rda_imagealine(im, g2d, shiftXleft + shiftXCaption + 2, shiftY - 5,
					shiftXleft + shiftXCaption - 1, shiftY - 10,
					_gbColor);
		}
		
		if (!empty(yaxisright)) {
			rda_imageline(
					g2d,
				sizeX + shiftXleft + shiftXCaption,
				shiftY - 5,
				sizeX + shiftXleft + shiftXCaption,
				sizeY + shiftY + 4,
				_gbColor
			);

			imagefilledpolygon(
					g2d,
				array(
					sizeX + shiftXleft + shiftXCaption - 3, shiftY - 5,
					sizeX + shiftXleft + shiftXCaption + 3, shiftY - 5,
					sizeX + shiftXleft + shiftXCaption, shiftY - 10
				),
				3,
				getColor("White")
			);

			/* draw right axis triangle */
			rda_imageline(g2d, sizeX + shiftXleft + shiftXCaption - 3, shiftY - 5,
				sizeX + shiftXleft + shiftXCaption + 3, shiftY - 5,
				_gbColor);
			rda_imagealine(im, g2d, sizeX + shiftXleft + shiftXCaption + 3, shiftY - 5,
				sizeX + shiftXleft + shiftXCaption, shiftY - 10,
				_gbColor);
			rda_imagealine(im, g2d, sizeX + shiftXleft + shiftXCaption - 3, shiftY - 5,
				sizeX + shiftXleft + shiftXCaption, shiftY - 10,
				_gbColor);
		}

		rda_imageline(
			g2d,
			shiftXleft + shiftXCaption - 4,
			sizeY + shiftY + 1,
			sizeX + shiftXleft + shiftXCaption + 5,
			sizeY + shiftY + 1,
			_gbColor
		);

		imagefilledpolygon(
				g2d,
			array(
				sizeX + shiftXleft + shiftXCaption + 5, sizeY + shiftY - 2,
				sizeX + shiftXleft + shiftXCaption + 5, sizeY + shiftY + 4,
				sizeX + shiftXleft + shiftXCaption + 10, sizeY + shiftY + 1
			),
			3,
			getColor("White")
		);

		/* draw X axis triangle */
		rda_imageline(g2d, sizeX + shiftXleft + shiftXCaption + 5, sizeY + shiftY - 2,
			sizeX + shiftXleft + shiftXCaption + 5, sizeY + shiftY + 4,
			_gbColor);
		rda_imagealine(im, g2d, sizeX + shiftXleft + shiftXCaption + 5, sizeY + shiftY + 4,
			sizeX + shiftXleft + shiftXCaption + 10, sizeY + shiftY + 1,
			_gbColor);
		rda_imagealine(im, g2d, sizeX + shiftXleft + shiftXCaption + 10, sizeY + shiftY + 1,
			sizeX + shiftXleft + shiftXCaption + 5, sizeY + shiftY - 2,
			_gbColor);
	}
	
	/**
	 * Draws Y scale grid.
	 */
	private void drawHorizontalGrid() {
		int _yAxis = !empty(yaxisleft) ? GRAPH_YAXIS_SIDE_LEFT : GRAPH_YAXIS_SIDE_RIGHT;

		double _stepY = Nest.value(gridStepX, _yAxis).asDouble();

		if (Nest.value(gridLinesCount, _yAxis).asDouble() < round(sizeY / gridPixels)) {
			_stepY = _stepY / 2;
		}

		int _xLeft = shiftXleft;
		int _xRight = shiftXleft + sizeX;
		int _lineColor = getColor(Nest.value(graphtheme,"maingridcolor").asString(), 0).getRGB();

		for (double y = shiftY + sizeY - _stepY; y > shiftY; y -= _stepY) {
			dashedLine(g2d, _xLeft, (int)Math.round(y), _xRight, (int)Math.round(y), _lineColor);
		}
	}

	private void drawTimeGrid() {
		calcTimeInterval();
		drawSubTimeGrid();
	}
	
	private void drawSubTimeGrid() {
		int _main_interval = Nest.value(grid,"horizontal","main","interval").asInteger();
		double _main_intervalX = Nest.value(grid,"horizontal","main","intervalx").asDouble();
		int _main_offset = Nest.value(grid,"horizontal","main","offset").asInteger();

		Map _sub = (Map)Nest.value(grid,"horizontal","sub").$();
		int _interval = Nest.value(_sub,"interval").asInteger();
		int _vline_count = Nest.value(_sub,"linecount").asInteger();
		double _intervalX = Nest.value(_sub,"intervalx").asDouble();

		int _offset = Nest.value(_sub,"offset").asInteger();
		double _offsetX = Nest.value(_sub,"offsetx").asInteger();
		int _start_i = Nest.value(_sub,"start").asInteger();

		if (_interval == _main_interval) {
			return;
		}

		CArray<Double> _test_dims = imageTextSize(7, 90, "WWW");
		for (int i = _start_i; i <= _vline_count; i++) {
			long _new_time = from_time + i * _interval + _offset;
			int _new_pos = (int)(i * _intervalX + _offsetX);
			
			long _tz;
			
			// dayLightSave
			if (_interval > SEC_PER_HOUR) {
				_tz = asInteger(date("Z", from_time)) - asInteger(date("Z", _new_time));
				_new_time += _tz;
			}

			// main interval checks
			if (_interval < SEC_PER_HOUR && asInteger(date("i", _new_time)) == 0) {
				drawMainPeriod(_new_time, _new_pos);
				continue;
			}

			if (_interval >= SEC_PER_HOUR && _interval < SEC_PER_DAY && date("H", _new_time) == "00") {
				drawMainPeriod(_new_time, _new_pos);
				continue;
			}

			if (_interval == SEC_PER_DAY && asInteger(date("N", _new_time)) == 7) {
				drawMainPeriod(_new_time, _new_pos);
				continue;
			}

			if (_interval > SEC_PER_DAY && (i * _interval % _main_interval + _offset) == _main_offset) {
				drawMainPeriod(_new_time, _new_pos);
				continue;
			}
			
			dashedLine(
				g2d,
				shiftXleft + _new_pos,
				shiftY,
				shiftXleft + _new_pos,
				sizeY + shiftY,
				getColor(Nest.value(graphtheme,"gridcolor").asString(), 0).getRGB()
			);

			if (_main_intervalX < floor((_main_interval / _interval) * _intervalX)) {
				continue;
			}
			else if (_main_intervalX < (ceil(_main_interval / _interval + 1) * Nest.value(_test_dims,"width").asInteger())) {
				continue;
			}

			String _date_format = null;
			if (_interval == SEC_PER_DAY) {
				_date_format = _("D");
			}
			else if (_interval > SEC_PER_DAY) {
				_date_format = _("d.m");
			}
			else if (_interval < SEC_PER_DAY) {
				_date_format = _("H:i");
			}

			String _str = rda_date2str(_date_format, _new_time);
			CArray<Double> _dims = imageTextSize(7, 90, _str);

			imageText(
				g2d,
				7,
				90,
				shiftXleft + _new_pos+round(Nest.value(_dims,"width").asInteger() / 2),
				sizeY + shiftY + Nest.value(_dims,"height").asInteger() + 6,
				getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
				_str
			);
		}

		// first && last
		// start
		String _str = rda_date2str(_("d.m H:i"), stime);
		CArray<Double> _dims = imageTextSize(8, 90, _str);
		imageText(
			g2d,
			8,
			90,
			shiftXleft + round(Nest.value(_dims,"width").asInteger() / 2),
			sizeY + shiftY + Nest.value(_dims,"height").asInteger() + 6,
			getColor(Nest.value(graphtheme,"highlightcolor").asString(), 0),
			_str
		);

		// end
		long _endtime = to_time;

		_str = rda_date2str(_("d.m H:i"), _endtime);
		_dims = imageTextSize(8, 90, _str);
		imageText(
			g2d,
			8,
			90,
			sizeX + shiftXleft + round(Nest.value(_dims,"width").asInteger() / 2),
			sizeY + shiftY + Nest.value(_dims,"height").asInteger() + 6,
			getColor(Nest.value(graphtheme,"highlightcolor").asString(), 0),
			_str
		);
	}
	
	private void drawMainPeriod(long _new_time, int _new_pos) {
		String _date_format;
		String _color;
		if (asInteger(date("H",_new_time)) == 0) {
			if (asInteger(date("Hi", _new_time)) == 0) {
				_date_format = _("d.m");
			}
			else {
				_date_format = _("d.m H:i");
			}

			_color = Nest.value(graphtheme,"highlightcolor").asString();
		}
		else {
			_date_format = _("H:i");
			_color = Nest.value(graphtheme,"highlightcolor").asString();
		}

		String _str = rda_date2str(_date_format, _new_time);
		CArray<Double> _dims = imageTextSize(8, 90, _str);

		imageText(
			g2d,
			8,
			90,
			shiftXleft + _new_pos + round(Nest.value(_dims,"width").asInteger() / 2),
			sizeY + shiftY + Nest.value(_dims,"height").asInteger() + 6,
			getColor(_color, 0),
			_str
		);

		dashedLine(
			g2d,
			shiftXleft + _new_pos,
			shiftY,
			shiftXleft + _new_pos,
			sizeY + shiftY,
			getColor(Nest.value(graphtheme,"maingridcolor").asString(), 0).getRGB()
		);
	}
	
	private void drawSides() {
		CArray<Integer> _sides = array();
		if (isset(axis_valuetype, GRAPH_YAXIS_SIDE_RIGHT)
				&& (yaxisright != 0 || skipRightScale != 1)) {
			_sides.add( GRAPH_YAXIS_SIDE_RIGHT );
		}

		if (((isset(axis_valuetype, GRAPH_YAXIS_SIDE_LEFT))
				&& (yaxisleft != 0 || skipLeftScale != 1)) || !isset(_sides)) {
			_sides.add( GRAPH_YAXIS_SIDE_LEFT );
		}

		for(Integer _side: _sides) {
			double _minY = Nest.value(m_minY, _side).asDouble();
			double _maxY = Nest.value(m_maxY, _side).asDouble();
			Object _units = null;
			String _unitsLong = null;
			boolean _byteStep = false;

			for (int _item = 0; _item < num; _item++) {
				if (Nest.value(items, _item, "axisside").asInteger() == _side) {
					// check if items use B or Bps units
					if ("B".equals(Nest.value(items, _item, "units").$()) || "Bps".equals(Nest.value(items, _item, "units").$())) {
						_byteStep = true;
					}
					if (is_null(_units)) {
						_units = Nest.value(items, _item, "units").$();
					}
					else if (!Cphp.equals(Nest.value(items, _item, "units").$(), _units)) {
						_units = "";
					}
				}
			}

			if (is_null(_units) || Boolean.FALSE.equals(_units)) {
				_units = "";
			}
			else {
				for (int _item = 0; _item < num; _item++) {
					if (Nest.value(items, _item, "axisside").asInteger() == _side && !empty(items.getNested(_item, "unitsLong"))) {
						_unitsLong = Nest.value(items, _item, "unitsLong").asString();
						break;
					}
				}
			}

			if (!empty(_unitsLong)) {
				CArray<Double> _dims = imageTextSize(9, 90, _unitsLong);

				int _tmpY = sizeY / 2 + shiftY + Nest.value(_dims,"height").asInteger() / 2;
				if (_tmpY < Nest.value(_dims,"height").asInteger()) {
					_tmpY = Nest.value(_dims,"height").asInteger() + 6;
				}

				int _tmpX = _side == GRAPH_YAXIS_SIDE_LEFT ? Nest.value(_dims,"width").asInteger() + 8 : fullSizeX - Nest.value(_dims,"width").asInteger();

				imageText(
					g2d,
					9,
					90,
					_tmpX,
					_tmpY,
					getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
					_unitsLong
				);
			}

			double _step = Nest.value(gridStep, _side).asDouble();
			int _hstr_count = Nest.value(gridLinesCount, _side).asInteger();

			// ignore milliseconds if  -1 <= maxY => 1 or -1 <= minY => 1
			boolean _ignoreMillisec = (bccomp(_maxY, -1) <= 0 || bccomp(_maxY, 1) >= 0
					|| bccomp(_minY, -1) <= 0 || bccomp(_minY, 1) >= 0);

			CArray<Float> _maxYPow, _minYPow;
			int _powStep;
			
			Object _newPow = false;
			if (_byteStep) {
				_maxYPow = convertToBase1024(_maxY, 1024);
				_minYPow = convertToBase1024(_minY, 1024);
				_powStep = 1024;
			} else {
				_maxYPow = convertToBase1024(_maxY);
				_minYPow = convertToBase1024(_minY);
				_powStep = 1000;
			}

			if (abs(Nest.value(_maxYPow,"pow").asDouble()) > abs(Nest.value(_minYPow,"pow").asDouble()) && Nest.value(_maxYPow,"value").asDouble() != 0) {
				_newPow = Nest.value(_maxYPow,"pow").asDouble();
				if (abs(bcdiv(Nest.value(_minYPow,"value").asDouble(), bcpow(_powStep, Nest.value(_maxYPow,"pow").asDouble()))) > 1000) {
					_newPow = Nest.value(_minYPow,"pow").$();
				}
			}
			if (abs(Nest.value(_maxYPow,"pow").asDouble()) < abs(Nest.value(_minYPow,"pow").asDouble()) && Nest.value(_minYPow,"value").asDouble() != 0) {
				_newPow = Nest.value(_minYPow,"pow").$();
				if (abs(bcdiv(Nest.value(_maxYPow,"value").asDouble(), bcpow(_powStep, Nest.value(_minYPow,"pow").asDouble()))) > 1000) {
					_newPow = Nest.value(_maxYPow,"pow").asDouble();
				}
			}
			if (Nest.value(_maxYPow,"pow").asDouble() == Nest.value(_minYPow,"pow").asDouble()) {
				_newPow = Nest.value(_maxYPow,"pow").asDouble();
			}

			Object _maxLength = false;
			// get all values in y-axis if units != "s"
			if (!"s".equals(_units)) {
				CArray _calcValues = array();
				for (int i = 0; i <= _hstr_count; i++) {
					_hstr_count = (_hstr_count == 0) ? 1 : _hstr_count;

					double _val = bcadd(bcmul(i, _step), _minY);

					if (bccomp(bcadd(_val, bcdiv(_step,2)), _maxY) == 1) {
						continue;
					}

					_calcValues.add( convert_units(map(
						"value" , _val,
						"convert" , ITEM_CONVERT_NO_UNITS,
						"byteStep" , _byteStep,
						"pow" , _newPow
					)));
				}

				_calcValues.add( convert_units(array(
					"value" , _maxY,
					"convert" , ITEM_CONVERT_NO_UNITS,
					"byteStep" , _byteStep,
					"pow" , _newPow
				)));

				_maxLength = calcMaxLengthAfterDot(_calcValues);
			}

			for (int i = 0; i <= _hstr_count; i++) {
				_hstr_count = (_hstr_count == 0) ? 1 : _hstr_count;

				double _val = bcadd(bcmul(i, _step), _minY);

				if (bccomp(bcadd(_val, bcdiv(_step, 2)), _maxY) == 1) {
					continue;
				}

				String _str = convert_units(map(
					"value" , _val,
					"units" , _units,
					"convert" , ITEM_CONVERT_NO_UNITS,
					"byteStep" , _byteStep,
					"pow" , _newPow,
					"ignoreMillisec" , _ignoreMillisec,
					"length" , _maxLength
				));
				
				int _posX;
				if (_side == GRAPH_YAXIS_SIDE_LEFT) {
					CArray _dims = imageTextSize(8, 0, _str);
					_posX = shiftXleft - Nest.value(_dims,"width").asInteger() - 9;
				}
				else {
					_posX = sizeX + shiftXleft + 12;
				}

				// marker Y coordinate
				double _posY = sizeY + shiftY - Nest.value(gridStepX, _side).asDouble() * i + 4;

				imageText(
					g2d,
					8,
					0,
					_posX,
					(int)_posY,
					getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
					_str
				);
			}

			String _str = convert_units(map(
				"value" , _maxY,
				"units" , _units,
				"convert" , ITEM_CONVERT_NO_UNITS,
				"byteStep" , _byteStep,
				"pow" , _newPow,
				"ignoreMillisec" , _ignoreMillisec,
				"length" , _maxLength
			));

			int _posX;
			Color _color;
			if (_side == GRAPH_YAXIS_SIDE_LEFT) {
				CArray<Double> _dims = imageTextSize(8, 0, _str);
				_posX = shiftXleft - Nest.value(_dims,"width").asInteger() - 9;
				_color = getColor(GRAPH_ZERO_LINE_COLOR_LEFT);
			}
			else {
				_posX = sizeX + shiftXleft + 12;
				_color = getColor(GRAPH_ZERO_LINE_COLOR_RIGHT);
			}

			imageText(
				g2d,
				8,
				0,
				_posX,
				shiftY + 4,
				getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
				_str
			);

			if (zero.get(_side) != sizeY + shiftY && zero.get(_side) != shiftY) {
				rda_imageline(
					g2d,
					shiftXleft,
					zero.get(_side),
					shiftXleft + sizeX,
					zero.get(_side),
					_color.getRGB()
				);
			}
		}
	}
	
	protected void drawWorkPeriod(SQLExecutor executor) {
		imagefilledrectangle(g2d,
			shiftXleft + 1,
			shiftY,
			sizeX + shiftXleft-1, // -2 border
			sizeY + shiftY,
			getColor(Nest.value(graphtheme,"graphcolor").asString(), 0)
		);

		if (m_showWorkPeriod != 1) {
			return;
		}
		if (period > 8035200) { // 31*24*3600*3 (3*month*3)
			return;
		}

		CArray<Map> _db_work_period = DBselect(executor, "SELECT c.work_period FROM config c WHERE tenantid='-'");
		Map _work_period = DBfetch(_db_work_period);
		if (empty(_work_period)) {
			return;
		}

		CArray<CArray> _periods = parse_period(Nest.value(_work_period,"work_period").asString());
		if (empty(_periods)) {
			return;
		}

		imagefilledrectangle(
			g2d,
			shiftXleft + 1,
			shiftY,
			sizeX + shiftXleft - 1, // -1 border
			sizeY + shiftY,
			getColor(Nest.value(graphtheme,"nonworktimecolor").asString(), 0)
		);

		long _now = time();
		if (isset(stime)) {
			from_time = stime;
			to_time = stime + period;
		}
		else {
			to_time = _now - SEC_PER_HOUR * from;
			from_time = to_time - period;
		}

		long _from = from_time;
		long _max_time = to_time;

		long _start = find_period_start(_periods, _from);
		long _end = -1;
		while (_start < _max_time && _start > 0) {
			_end = find_period_end(_periods, _start, _max_time);

			int x1 = round(((_start - _from) * sizeX) / period) + shiftXleft;
			int x2 = ceil(((_end - _from) * sizeX) / period) + shiftXleft;

			// draw rectangle
			imagefilledrectangle(
				g2d,
				x1,
				shiftY,
				x2 - 1, // -1 border
				sizeY + shiftY,
				getColor(Nest.value(graphtheme,"graphcolor").asString(), 0)
			);

			_start = find_period_start(_periods, _end);
		}
	}
	

	protected void drawPercentile() {
		if (type != GRAPH_TYPE_NORMAL) {
			return ;
		}

		for(Entry<Object, CArray<Float>> entry: percentile.entrySet()) {
			Object _side = entry.getKey();
			CArray<Float> _percentile = entry.getValue();
			
			if (Nest.value(_percentile,"percent").asInteger() > 0 && Nest.value(_percentile,"value").asBoolean()) {
				int _minY, _maxY;
				String _color;
				if ("left".equals(_side)) {
					_minY = Nest.value(m_minY, GRAPH_YAXIS_SIDE_LEFT).asInteger();
					_maxY = Nest.value(m_maxY, GRAPH_YAXIS_SIDE_LEFT).asInteger();

					_color = Nest.value(graphtheme,"leftpercentilecolor").asString();
				}
				else {
					_minY = Nest.value(m_minY, GRAPH_YAXIS_SIDE_RIGHT).asInteger();
					_maxY = Nest.value(m_maxY, GRAPH_YAXIS_SIDE_RIGHT).asInteger();

					_color = Nest.value(graphtheme,"rightpercentilecolor").asString();
				}

				double y = sizeY - ((Nest.value(_percentile,"value").asDouble() - _minY) / (_maxY - _minY)) * sizeY + shiftY;
				rda_imageline(
					g2d,
					shiftXleft,
					y,
					sizeX + shiftXleft,
					y,
					getColor(_color)
				);
			}
		}
	}
	
	protected void drawTriggers() {
		if (m_showTriggers != 1) {
			return;
		}

		int[] _opposite = hex2rgb(GRAPH_TRIGGER_LINE_OPPOSITE_COLOR);
		int _oppColor = imagecolorallocate(g2d, _opposite[0], _opposite[1], _opposite[2]);
		
		for(Entry<Object, Map> entry: triggers.entrySet()) {
			//Object _tnum = entry.getKey();
			Map _trigger = entry.getValue();
			if (Nest.value(_trigger,"skipdraw").asBoolean()) {
				continue;
			}

			Color _triggerColor = getColor(Nest.value(_trigger,"color").asString());
			CArray _lineStyle = array(_triggerColor, _triggerColor, _triggerColor, _triggerColor, _triggerColor, _oppColor, _oppColor, _oppColor);

			dashedLine(
				g2d,
				shiftXleft,
				Nest.value(_trigger,"y").asInteger(),
				sizeX + shiftXleft,
				Nest.value(_trigger,"y").asInteger(),
				_lineStyle
			);

			dashedLine(
				g2d,
				shiftXleft,
				Nest.value(_trigger,"y").asInteger() + 1,
				sizeX + shiftXleft,
				Nest.value(_trigger,"y").asInteger() + 1,
				_lineStyle
			);
		}
	}
	
	protected void drawLegend() {
		int _leftXShift = 20;
		CArray<Integer> _units = map("left" , 0, "right" , 0);

		// draw item legend
		CImageTextTable _legend = new CImageTextTable(g2d, _leftXShift - 5, sizeY + shiftY + legendOffsetY);
		_legend.color = getColor(Nest.value(graphtheme,"textcolor").asString(), 0).getRGB();
		_legend.rowheight = 14;
		_legend.fontsize = 9;

		// item legend table header
		CArray<CArray<Object>> _row = array(
			map("text" , "", "marginRight" , 5),
			map("text" , ""),
			map("text" , ""),
			map("text" , _("last"),"align" , 1, "fontsize" , 9),
			map("text" , _("min"), "align" , 1, "fontsize" , 9),
			map("text" , _("avg"), "align" , 1, "fontsize" , 9),
			map("text" , _("max"), "align" , 1, "fontsize" , 9)
		);

		_legend.addRow(_row);
		int _rowNum = _legend.getNumRows();

		int i = (type == GRAPH_TYPE_STACKED) ? num - 1 : 0;
		while (i >= 0 && i < num) {
			Color _color = getColor(Nest.value(items, i, "color").asString(), GRAPH_STACKED_ALFA);
			String _fncRealName;
			switch (Nest.value(items, i, "calc_fnc").asInteger()) {
				case CALC_FNC_MIN:
					_fncRealName = _("min");
					break;
				case CALC_FNC_MAX:
					_fncRealName = _("max");
					break;
				case CALC_FNC_ALL:
					_fncRealName = _("all");
					break;
				case CALC_FNC_AVG:
				default:
					_fncRealName = _("avg");
			}

			Map _data = (Map)data.getNested(items.getNested(i, "itemid"), items.getNested(i, "calc_type"));

			BufferedImage _colorSquare;
			// draw color square
//			if (function_exists("imagecolorexactalpha") && function_exists("imagecreatetruecolor") && @imagecreatetruecolor(1, 1)) {
//				_colorSquare = imagecreatetruecolor(11, 11);
//			}
//			else {
				_colorSquare = imagecreate(11, 11);
//			}
				
			Graphics2D g2d_colorSquare = _colorSquare.createGraphics();

			imagefill(_colorSquare, 0, 0, getColor(Nest.value(graphtheme,"backgroundcolor").asString(), 0).getRGB());
			imagefilledrectangle(g2d_colorSquare, 0, 0, 10, 10, _color);
			imagerectangle(g2d_colorSquare, 0, 0, 10, 10, getColor("Black"));

			// caption
			String _itemCaption = !empty(itemsHost)
				? Nest.value(items, i, "name_expanded").asString()
				: items.getNested(i, "hostname") + NAME_DELIMITER + items.getNested(i, "name_expanded");

			// draw legend of an item with data
			if (isset(_data) && isset(Nest.value(_data,"min").$())) {
				if (Nest.value(items, i, "axisside").asInteger() == GRAPH_YAXIS_SIDE_LEFT) {
					Nest.value(_units,"left").$(items.getNested(i, "units"));
				}
				else {
					Nest.value(_units,"right").$(items.getNested(i, "units"));
				}

				_legend.addCell(_rowNum, map("image" , _colorSquare, "marginRight" , 5));
				_legend.addCell(_rowNum, map("text" , _itemCaption));
				_legend.addCell(_rowNum, map("text" , "["+_fncRealName+"]"));
				_legend.addCell(_rowNum, map(
					"text" , convert_units(map(
						"value" , getLastValue(i),
						"units" , items.getNested(i, "units"),
						"convert" , ITEM_CONVERT_NO_UNITS
					)),
					"align" , 2
				));
				_legend.addCell(_rowNum, map(
					"text" , convert_units(map(
						"value" , min(Nest.array(_data,"min").asDouble()),
						"units" , items.getNested(i, "units"),
						"convert" , ITEM_CONVERT_NO_UNITS
					)),
					"align" , 2
				));
				_legend.addCell(_rowNum, map(
					"text" , convert_units(map(
						"value" , Nest.value(_data,"avg_orig").asDouble(),
						"units" , items.getNested(i, "units"),
						"convert" , ITEM_CONVERT_NO_UNITS
					)),
					"align" , 2
				));
				_legend.addCell(_rowNum, map(
					"text" , convert_units(map(
						"value" , max(Nest.array(_data,"max").asDouble()),
						"units" , items.getNested(i, "units"),
						"convert" , ITEM_CONVERT_NO_UNITS
					)),
					"align" , 2
				));
			}
			// draw legend of an item without data
			else {
				_legend.addCell(_rowNum, map("image" , _colorSquare, "marginRight" , 5));
				_legend.addCell(_rowNum, map("text" , _itemCaption));
				_legend.addCell(_rowNum, map("text" , "[ "+_("no data")+" ]"));
			}

			_rowNum++;

			if (type == GRAPH_TYPE_STACKED) {
				i--;
			}
			else {
				i++;
			}
		}

		_legend.draw();

		// if graph is small, we are not drawing percent line and trigger legends
		if (sizeY < RDA_GRAPH_LEGEND_HEIGHT) {
			return; //			return true;
		}

		_legend = new CImageTextTable(
			g2d,
			_leftXShift + 10,
			sizeY + shiftY + 14 * _rowNum + legendOffsetY
		);
		_legend.color = getColor(Nest.value(graphtheme,"textcolor").asString(), 0).getRGB();
		_legend.rowheight = 14;
		_legend.fontsize = 9;

		// draw percentile
		if (type == GRAPH_TYPE_NORMAL) {
			for(Entry<Object, CArray<Float>> entry: percentile.entrySet()) {
				Object _side = entry.getKey();
				CArray<Float> _percentile = entry.getValue();
				
				if (Nest.value(_percentile,"percent").asInteger() > 0 && Nest.value(_percentile,"value").asBoolean()) {
					Nest.value(_percentile,"percent").$((Float) Nest.value(_percentile,"percent").$());
					String _convertedUnit = convert_units(map(
						"value" , Nest.value(_percentile,"value").$(),
						"units" , _units.get(_side)
					));
					_legend.addCell(_rowNum, map(
						"text" , Nest.value(_percentile, "percent").asInteger() +"th percentile: "+_convertedUnit+" ("+_side+")"
					).push(ITEM_CONVERT_NO_UNITS));
					
					String _color;
					if (_side == "left") {
						_color = Nest.value(graphtheme,"leftpercentilecolor").asString();
					}
					else {
						_color = Nest.value(graphtheme,"rightpercentilecolor").asString();
					}

					imagefilledpolygon(
						g2d,
						array(
							_leftXShift + 5, sizeY + shiftY + 14 * _rowNum + legendOffsetY,
							_leftXShift - 5, sizeY + shiftY + 14 * _rowNum + legendOffsetY,
							_leftXShift, sizeY + shiftY + 14 * _rowNum + legendOffsetY - 10
						),
						3,
						getColor(_color)
					);

					imagepolygon(
						g2d,
						array(
							_leftXShift + 5, sizeY + shiftY + 14 * _rowNum + legendOffsetY,
							_leftXShift - 5, sizeY + shiftY + 14 * _rowNum + legendOffsetY,
							_leftXShift, sizeY + shiftY + 14 * _rowNum + legendOffsetY - 10
						),
						3,
						getColor("Black No Alpha").getRGB()
					);
					_rowNum++;
				}
			}
		}

		_legend.draw();

		_legend = new CImageTextTable(
			g2d,
			_leftXShift + 10,
			sizeY + shiftY + 14 * _rowNum + legendOffsetY + 5
		);
		_legend.color = getColor(Nest.value(graphtheme,"textcolor").asString(), 0).getRGB();
		_legend.rowheight = 14;
		_legend.fontsize = 9;

		// draw triggers
		for(Map _trigger: triggers) {
			imagefilledellipse(
				g2d,
				_leftXShift-5,
				sizeY + shiftY + 14 * _rowNum + legendOffsetY-5,
				10,
				10,
				getColor(Nest.value(_trigger,"color").asString())
			);

			imageellipse(
				g2d,
				_leftXShift-5,
				sizeY + shiftY + 14 * _rowNum + legendOffsetY-5,
				10,
				10,
				getColor("Black No Alpha")
			);

			_legend.addRow(array(
				map("text" , Nest.value(_trigger,"description").$()),
				map("text" , Nest.value(_trigger,"constant").$())
			));
			_rowNum++;
		}

		_legend.draw();
	}
	
	protected boolean limitToBounds(TObj<Double> _value1, TObj<Double> _value2, double _min, double _max, int _drawtype) {
		// fixes graph out of bounds problem
		if (((_value1.$() > (_max + _min)) && (_value2.$() > (_max + _min))) || (_value1.$() < _min && _value2.$() < _min)) {
			if (!in_array(_drawtype, array(GRAPH_ITEM_DRAWTYPE_FILLED_REGION, GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE))) {
				return false;
			}
		}

		boolean _y_first = _value1.$() > (_max + _min) || _value1.$() < _min;
		boolean _y_second = _value2.$() > (_max + _min) || _value2.$() < _min;

		if (_y_first) {
			_value1.$((_value1.$() > (_max + _min)) ? _max + _min : _min);
		}

		if (_y_second) {
			_value2.$((_value2.$() > (_max + _min)) ? _max + _min : _min);
		}

		return true;
	}
	
	protected void drawElement(CArray _data, int _from, int _to, double _minX, double _maxX, double _minY, double _maxY, int _drawtype, 
			Color _max_color, Color _avg_color, Color _min_color, Color _minmax_color, int _calc_fnc, int _axisside) {
		if (!isset(_data.getNested("max", _from)) || !isset(_data.getNested("max", _to))) {
			return;
		}

		double _oxy = Nest.value(oxy, _axisside).asDouble();
		double _zero = Nest.value(zero, _axisside).asDouble();
		double _unit2px = Nest.value(unit2px, _axisside).asDouble();

		double _shift_min_from, _shift_min_to, _shift_max_from, _shift_max_to, _shift_avg_from, _shift_avg_to;
		_shift_min_from = _shift_min_to = 0;
		_shift_max_from = _shift_max_to = 0;
		_shift_avg_from = _shift_avg_to = 0;

		if (isset(_data.getNested("shift_min", _from))) {
			_shift_min_from = Nest.value(_data, "shift_min", _from).asDouble();
		}
		if (isset(Nest.value(_data, "shift_min", _to).$())) {
			_shift_min_to = Nest.value(_data, "shift_min", _to).asDouble();
		}

		if (isset(Nest.value(_data, "shift_max", _from).$())) {
			_shift_max_from = Nest.value(_data, "shift_max", _from).asDouble();
		}
		if (isset(Nest.value(_data, "shift_max", _to).$())) {
			_shift_max_to = Nest.value(_data, "shift_max", _to).asDouble();
		}

		if (isset(Nest.value(_data, "shift_avg", _from).$())) {
			_shift_avg_from = Nest.value(_data, "shift_avg", _from).asDouble();
		}
		if (isset(Nest.value(_data, "shift_avg", _to).$())) {
			_shift_avg_to = Nest.value(_data, "shift_avg", _to).asDouble();
		}

		double _min_from = Nest.value(_data, "min", _from).asDouble() + _shift_min_from;
		double _min_to = Nest.value(_data, "min", _to).asDouble() + _shift_min_to;

		double _max_from = Nest.value(_data, "max", _from).asDouble() + _shift_max_from;
		double _max_to = Nest.value(_data, "max", _to).asDouble() + _shift_max_to;

		double _avg_from = Nest.value(_data, "avg", _from).asDouble() + _shift_avg_from;
		double _avg_to = Nest.value(_data, "avg", _to).asDouble() + _shift_avg_to;

		int x1 = _from + shiftXleft - 1;
		int x2 = _to + shiftXleft;

		double y1min = _zero - (_min_from - _oxy) / _unit2px;
		double y2min = _zero - (_min_to - _oxy) / _unit2px;

		double y1max = _zero - (_max_from - _oxy) / _unit2px;
		double y2max = _zero - (_max_to - _oxy) / _unit2px;

		double y1avg = _zero - (_avg_from - _oxy) / _unit2px;
		double y2avg = _zero - (_avg_to - _oxy) / _unit2px;

		double _y1, _y2, _shift_from, _shift_to;
		double[] a = new double[8];
		boolean y1x, y2x, y1n, y2n;
		y1x = y2x = y1n = y2n = false;
		
		switch (_calc_fnc) {
			case CALC_FNC_MAX:
				_y1 = y1max;
				_y2 = y2max;
				_shift_from = _shift_max_from;
				_shift_to = _shift_max_to;
				break;
			case CALC_FNC_MIN:
				_y1 = y1min;
				_y2 = y2min;
				_shift_from = _shift_min_from;
				_shift_to = _shift_min_to;
				break;
			case CALC_FNC_ALL:
				// max
				y1x = ((y1max > (sizeY + shiftY)) || y1max < shiftY);
				y2x = ((y2max > (sizeY + shiftY)) || y2max < shiftY);

				if (y1x) {
					y1max = (y1max > (sizeY + shiftY)) ? sizeY + shiftY : shiftY;
				}
				if (y2x) {
					y2max = (y2max > (sizeY + shiftY)) ? sizeY + shiftY : shiftY;
				}

				// min
				y1n = ((y1min > (sizeY + shiftY)) || y1min < shiftY);
				y2n = ((y2min > (sizeY + shiftY)) || y2min < shiftY);

				if (y1n) {
					y1min = (y1min > (sizeY + shiftY)) ? sizeY + shiftY : shiftY;
				}
				if (y2n) {
					y2min = (y2min > (sizeY + shiftY)) ? sizeY + shiftY : shiftY;
				}

				a[0] = x1;
				a[1] = y1max;
				a[2] = x1;
				a[3] = y1min;
				a[4] = x2;
				a[5] = y2min;
				a[6] = x2;
				a[7] = y2max;

			// don't use break, avg must be drawn in this statement
			case CALC_FNC_AVG:

			// don't use break, avg must be drawn in this statement
			default:
				_y1 = y1avg;
				_y2 = y2avg;
				_shift_from = _shift_avg_from ;
				_shift_to = _shift_avg_to;
		}

		_shift_from -= (_shift_from != 0) ? _oxy : 0;
		_shift_to -= (_shift_to != 0) ? _oxy : 0;

		double _y1_shift = _zero - _shift_from / _unit2px;
		double _y2_shift = _zero - _shift_to / _unit2px;

		TObj<Double> y1=Nest.as(_y1), y2=Nest.as(_y2);
		if (!limitToBounds(y1, y2, shiftY, sizeY, _drawtype)) {
			return; //return true;
		}
		_y1 = y1.$();
		_y2 = y2.$();
		
		
		TObj<Double> y1_shift=Nest.as(_y1_shift), y2_shift=Nest.as(_y2_shift);
		if (!limitToBounds(y1_shift, y2_shift, shiftY, sizeY, _drawtype)) {
			return; //return true;
		}
		_y1_shift = y1_shift.$();
		_y2_shift = y2_shift.$();

		// draw main line
		switch (_drawtype) {
			case GRAPH_ITEM_DRAWTYPE_BOLD_LINE:
				if (_calc_fnc == CALC_FNC_ALL) {
					imagefilledpolygon(g2d, a, 4, _minmax_color);
					if (!y1x || !y2x) {
						rda_imagealine(im, g2d, x1, (int)y1max, x2, (int)y2max, _max_color.getRGB(), LINE_TYPE_BOLD);
					}

					if (!y1n || !y2n) {
						rda_imagealine(im, g2d, x1, (int)y1min, x2, (int)y2min, _min_color.getRGB(), LINE_TYPE_BOLD);
					}
				}

				rda_imagealine(im, g2d, x1, (int)_y1, x2, (int)_y2, _avg_color.getRGB(), LINE_TYPE_BOLD);
				break;
			case GRAPH_ITEM_DRAWTYPE_LINE:
				if (_calc_fnc == CALC_FNC_ALL) {
					imagefilledpolygon(g2d, a, 4, _minmax_color);
					if (!y1x || !y2x) {
						rda_imagealine(im, g2d, x1, (int)y1max, x2, (int)y2max, _max_color.getRGB());
					}
					if (!y1n || !y2n) {
						rda_imagealine(im, g2d, x1, (int)y1min, x2, (int)y2min, _min_color.getRGB());
					}
				}

				rda_imagealine(im, g2d, x1, (int)_y1, x2, (int)_y2, _avg_color.getRGB());
				break;
			case GRAPH_ITEM_DRAWTYPE_FILLED_REGION:
				a[0] = x1;
				a[1] = _y1;
				a[2] = x1;
				a[3] = _y1_shift;
				a[4] = x2;
				a[5] = _y2_shift;
				a[6] = x2;
				a[7] = _y2;

				imagefilledpolygon(g2d, a, 4, _avg_color);
				break;
			case GRAPH_ITEM_DRAWTYPE_DOT:
				imagefilledrectangle(g2d, x1 - 1, (int)_y1 - 1, x1, (int)_y1, _avg_color);
				break;
			case GRAPH_ITEM_DRAWTYPE_BOLD_DOT:
				imagefilledrectangle(g2d, x2 - 1, (int)_y2 - 1, x2 + 1, (int)_y2 + 1, _avg_color);
				break;
			case GRAPH_ITEM_DRAWTYPE_DASHED_LINE:
//				if (function_exists("imagesetstyle")) {
					// use imagesetstyle+imageline instead of bugged imagedashedline
					CArray _style = array(_avg_color, _avg_color, IMG_COLOR_TRANSPARENT, IMG_COLOR_TRANSPARENT);
					imagesetstyle(g2d, _style);
					rda_imageline(g2d, x1, _y1, x2, _y2, IMG_COLOR_STYLED);
//				}
//				else {
//					imagedashedline(g2d, x1, y1, x2, y2, _avg_color);
//				}
				break;
			case GRAPH_ITEM_DRAWTYPE_GRADIENT_LINE: //渐变
				imageline(g2d, x1, (int)_y1, x2, (int)_y2, _avg_color.getRGB()); // draw the initial line
//				imageline(g2d, x1, (int)y1 - 1, x2, (int)y2 - 1, _avg_color.getRGB()); //初始线条变细

				int _bitmask = 255;
				int _blue = _avg_color.getRGB() & _bitmask;

				// _blue_diff = 255 - _blue;
				_bitmask = _bitmask << 8;
				int _green = (_avg_color.getRGB() & _bitmask) >> 8;

				// _green_diff = 255 - _green;
				_bitmask = _bitmask << 8;
				int _red = (_avg_color.getRGB() & _bitmask) >> 16;
				// _red_diff = 255 - _red;

				// note: though gradients on the chart looks ok, the formula used is completely incorrect
				// if you plan to fix something here, it would be better to start from scratch
				double _maxAlpha = 110;
				double _startAlpha = 50;
				double _alphaRatio = _maxAlpha / (sizeY - _startAlpha);

				int _diffX = x1 - x2;
				for (int i = 0; i <= _diffX; i++) {
					double yincr = (_diffX > 0) ? (abs(_y2 - _y1) / _diffX) : 0;

					double _gy = (_y1 > _y2) ? (_y2 + yincr * i) : (_y2 - yincr * i);
					double _steps = sizeY + shiftY - _gy + 1;

					for (int j = 0; j < _steps; j++) {
						double _alpha;
						if ((_gy + j) < (shiftY + _startAlpha)) {
							_alpha = 0;
						}
						else {
							_alpha = 127 - abs(127 - (_alphaRatio * (_gy + j - shiftY - _startAlpha)));
						}

						Color _color = imagecolorexactalpha(g2d, _red, _green, _blue, (int)_alpha);
						imagesetpixel(g2d, x2 + i, (int)_gy + j, _color);
					}
				}
			break;
		}
	}
	

	@Override
	public byte[] draw() throws Exception {
		double _start_time = microtime(true);

//		set_image_header();

		selectData();

		CArray<Integer> _sides = array();
		if (isset(axis_valuetype, GRAPH_YAXIS_SIDE_RIGHT)) {
			_sides.add(GRAPH_YAXIS_SIDE_RIGHT);
		}

		if (isset(axis_valuetype, GRAPH_YAXIS_SIDE_LEFT) || !isset(_sides)) {
			_sides.add(GRAPH_YAXIS_SIDE_LEFT);
		}

		for(int _graphSide: _sides) {
			m_minY.put(_graphSide, calculateMinY(executor, _graphSide));
			m_maxY.put(_graphSide, calculateMaxY(executor, _graphSide));

			if (Nest.value(m_minY, _graphSide).$() == null) {
				m_minY.put(_graphSide, 0);
			}
			if (Nest.value(m_maxY, _graphSide).$() == null) {
				m_maxY.put(_graphSide, 1);
			}

			if (Nest.value(m_minY, _graphSide).asDouble() == Nest.value(m_maxY, _graphSide).asDouble()) {
				if ("-".equals(graphOrientation.get(_graphSide))) {
					m_maxY.put(_graphSide, 0);
				}
				else if (Nest.value(m_minY, _graphSide).asDouble() == 0) {
					m_maxY.put(_graphSide, 1);
				}
				else {
					m_minY.put(_graphSide, 0);
				}
			}
			else if (Nest.value(m_minY, _graphSide).asDouble() > Nest.value(m_maxY, _graphSide).asDouble()) {
				if ("-".equals(graphOrientation.get(_graphSide))) {
					m_minY.put(_graphSide, bcmul(Nest.value(m_maxY, _graphSide).asDouble(), 0.2));
				}
				else {
					m_minY.put(_graphSide, 0);
				}
			}

			// If max Y-scale bigger min Y-scale only for 10% or less, then we don't allow Y-scale duplicate
			if (Nest.value(m_maxY, _graphSide).asBoolean() && Nest.value(m_minY, _graphSide).asBoolean()) {
				double _absMinY, _absMaxY;
				if (Nest.value(m_minY, _graphSide).asDouble() < 0) {
					_absMinY = bcmul(Nest.value(m_minY, _graphSide).asDouble(), -1);
				}
				else {
					_absMinY = Nest.value(m_minY, _graphSide).asDouble();
				}
				if (Nest.value(m_maxY, _graphSide).asDouble() < 0) {
					_absMaxY = bcmul(Nest.value(m_maxY, _graphSide).asDouble(), -1);
				}
				else {
					_absMaxY = Nest.value(m_maxY, _graphSide).asDouble();
				}

				if (_absMaxY < _absMinY) {
					double _oldAbMaxY = _absMaxY;
					_absMaxY = _absMinY;
					_absMinY = _oldAbMaxY;
				}

				if (bcdiv((bcsub(_absMaxY, _absMinY)), _absMaxY) <= 0.1) {
					if (Nest.value(m_minY, _graphSide).asDouble() > 0) {
						m_minY.put(_graphSide, bcmul(Nest.value(m_minY, _graphSide).asDouble(), 0.95));
					}
					else {
						m_minY.put(_graphSide, bcmul(Nest.value(m_minY, _graphSide).asDouble(), 1.05));
					}
					if (Nest.value(m_maxY, _graphSide).asDouble() > 0) {
						m_maxY.put(_graphSide, bcmul(Nest.value(m_maxY, _graphSide).asDouble(), 1.05));
					}
					else {
						m_maxY.put(_graphSide, bcmul(Nest.value(m_maxY, _graphSide).asDouble(), 0.95));
					}
				}
			}
		}

		calcMinMaxInterval();
		updateShifts();
		calcTriggers(executor);
		calcZero();
		calcPercentile();

		fullSizeX = sizeX + shiftXleft + shiftXright + 1;
		fullSizeY = sizeY + shiftY + legendOffsetY;

		if (drawLegend) {
			fullSizeY += 14 * (num + 1 + ((sizeY < 120) ? 0 : count(triggers))) + 8;
		}

		// if graph height is big enough, we reserve space for percent line legend
		if (sizeY >= RDA_GRAPH_LEGEND_HEIGHT) {
			for(CArray _percentile: percentile) {
				if (Nest.value(_percentile,"percent").asInteger() > 0 && Nest.value(_percentile,"value").asBoolean()) {
					fullSizeY += 14;
				}
			}
		}

//		if (function_exists("imagecolorexactalpha") && function_exists("imagecreatetruecolor") && @imagecreatetruecolor(1, 1)) {
//			im = imagecreatetruecolor(fullSizeX, fullSizeY);
//		}
//		else {
			im = imagecreate(fullSizeX, fullSizeY);
			g2d = im.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		}

		initColors();
		drawRectangle();
		drawHeader();
		drawWorkPeriod(executor);
		drawTimeGrid();
		drawHorizontalGrid();
		drawXYAxisScale(); //drawXYAxisScale(Nest.value(graphtheme,"gridbordercolor").$());

		int _maxX = sizeX;

		// for each metric
		for (int _item = 0; _item < num; _item++) {
			double _minY = Nest.value(m_minY, items.getNested(_item, "axisside")).asDouble();
			double _maxY = Nest.value(m_maxY, items.getNested(_item, "axisside")).asDouble();

			Map _data = (Map)data.getNested(items.getNested(_item, "itemid"), items.getNested(_item, "calc_type"));

			if (!isset(_data)) {
				continue;
			}

			int _drawtype, _calc_fnc;
			Color _max_color, _avg_color, _min_color, _minmax_color;
			
			if (type == GRAPH_TYPE_STACKED) {
				_drawtype = Nest.value(items, _item, "drawtype").asInteger();
				_max_color = getColor("ValueMax", GRAPH_STACKED_ALFA*10);
				_avg_color = getColor(Nest.value(items, _item, "color").asString(), GRAPH_STACKED_ALFA*10);
				_min_color = getColor("ValueMin", GRAPH_STACKED_ALFA*10);
				_minmax_color = getColor("ValueMinMax", GRAPH_STACKED_ALFA*10);

				_calc_fnc = Nest.value(items, _item, "calc_fnc").asInteger();
			}
			else {
				_drawtype = Nest.value(items, _item, "drawtype").asInteger();
				_max_color = getColor("ValueMax", GRAPH_STACKED_ALFA*10);
				_avg_color = getColor(Nest.value(items, _item, "color").asString(), GRAPH_STACKED_ALFA*10);
				_min_color = getColor("ValueMin", GRAPH_STACKED_ALFA*10);
				_minmax_color = getColor("ValueMinMax", GRAPH_STACKED_ALFA*10);

				_calc_fnc = Nest.value(items, _item, "calc_fnc").asInteger();
			}
			
			// for each X
			boolean _draw = true;
			boolean _prevDraw = true;
			for (int i = 1, j = 0; i < _maxX; i++) { // new point
				if (Nest.value(_data, "count", i).asInteger() == 0 && i != (_maxX - 1)) {
					continue;
				}

				long _diff = abs(Nest.value(_data, "clock", i).asLong() - Nest.value(_data, "clock", j).asLong());
				long _cell = (to_time - from_time) / sizeX;
				int _delay = Nest.value(items, _item, "delay").asInteger();

				if (_cell > _delay) {
					_draw = (boolean) (_diff < (RDA_GRAPH_MAX_SKIP_CELL * _cell));
				}
				else {
					_draw = (boolean) (_diff < (RDA_GRAPH_MAX_SKIP_DELAY * _delay));
				}

				if (Nest.value(items, _item, "type").asInteger() == ITEM_TYPE_TRAPPER) {
					_draw = true;
				}

				int _valueDrawType;
				if (!_draw && !_prevDraw) {
					_draw = true;
					_valueDrawType = GRAPH_ITEM_DRAWTYPE_BOLD_DOT;
				}
				else {
					_valueDrawType = _drawtype;
					_prevDraw = _draw;
				}

				if (_draw) {
					drawElement(
						CArray.valueOf(_data),
						i,
						j,
						0,
						sizeX,
						_minY,
						_maxY,
						_valueDrawType,
						_max_color,
						_avg_color,
						_min_color,
						_minmax_color,
						_calc_fnc,
						Nest.value(items, _item, "axisside").asInteger()
					);
				}

				j = i;
			}
		}

		drawSides();

		if (drawLegend) {
			drawTriggers();
			drawPercentile();
			drawLegend();
		}

		drawLogo();

//		String _str = sprintf("%.2f", (Double)microtime(true) - _start_time);
//		_str = _s("Data from %1$s. Generated in %2$s sec.", dataFrom, _str);
//		CArray<Double> _strSize = imageTextSize(6, 0, _str);
//		imageText(g2d, 6, 0, fullSizeX - Nest.value(_strSize,"width").asInteger() - 5, fullSizeY - 5, getColor("Gray"), _str);

//		unset(items, data);
		items = data = null;

		g2d.dispose();
		
		return Cgd.imageOut(im);
	}
}
