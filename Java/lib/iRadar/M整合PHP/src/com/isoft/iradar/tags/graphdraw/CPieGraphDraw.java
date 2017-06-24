package com.isoft.iradar.tags.graphdraw;

import static com.isoft.iradar.Cgd.IMG_ARC_EDGED;
import static com.isoft.iradar.Cgd.IMG_ARC_NOFILL;
import static com.isoft.iradar.Cgd.IMG_ARC_PIE;
import static com.isoft.iradar.Cgd.imagecreate;
import static com.isoft.iradar.Cgd.imagefilledarc;
import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cgd.imagerectangle;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.abs;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.cos;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.deg2rad;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.microtime;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.sin;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.str_pad;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.core.utils.EasyObject.asDouble;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_LST;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.GRAPH_3D_ANGLE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SIMPLE;
import static com.isoft.iradar.inc.Defines.GRAPH_ITEM_SUM;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_3D;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_3D_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_EXPLODED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_PIE;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.GraphsUtil.imageText;
import static com.isoft.iradar.inc.HostsUtil.get_host_by_hostid;
import static com.isoft.iradar.inc.ItemsUtil.get_item_by_itemid;
import static com.isoft.iradar.inc.ProfilesUtil.select_config;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cgd;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.managers.Manager;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CPieGraphDraw extends CGraphDraw {
	
	private Integer background = null;
	private int exploderad = 1;
	private int exploderad3d = 3;
	private int graphheight3d = 12;
	private int shiftlegendright = 17 * 7 + 7 + 10; // count of static chars * px/char + for color rectangle + space
	private Integer angle3d;
	private int shiftYLegend;
	
	public CPieGraphDraw(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, GRAPH_TYPE_PIE);
		this.sum = 0d;
	}
	public CPieGraphDraw(IIdentityBean idBean, SQLExecutor executor, int graphType) {
		super(idBean, executor, graphType);
	}

	@Override
	public byte[] draw() throws Exception {
		double _start_time = microtime(true);
//		set_image_header();

		selectData();

		shiftY = 30;
		shiftYLegend = 20;
		shiftXleft = 10;
		shiftXright = 0;
		fullSizeX = sizeX;
		fullSizeY = sizeY;

		if (sizeX < 300 || sizeY < 200) {
			showLegend(false);
		}

		if (drawLegend) {
			sizeX -= shiftXleft + shiftXright + shiftlegendright;
			sizeY -= shiftY + shiftYLegend + 12 * num + 8;
		}
		else {
			sizeX -= shiftXleft * 2;
			sizeY -= shiftY * 2;
		}

		sizeX = min((double)sizeX, (double)sizeY).intValue();
		sizeY = min((double)sizeX, (double)sizeY).intValue();

		calc3dheight(sizeY);

		exploderad = (int) sizeX / 100;
		exploderad3d = (int) sizeX / 60;

//		if (function_exists("ImageColorExactAlpha") && function_exists("ImageCreateTrueColor") && @imagecreatetruecolor(1, 1)) {
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

		// for each metric
		CArray _values = array();
		for (int i = 0; i < num; i++) {
			Object _type = Nest.value(items, i, "calc_type").$();

			Object _itemid = Nest.value(items, i, "itemid").$();
			Object _data = Nest.value(data, _itemid, _type).$();

			if (!isset(_data)) {
				continue;
			}

			String _fncName;
			switch (Nest.value(items, i, "calc_fnc").asInteger()) {
				case CALC_FNC_MIN:
					_fncName = "min";
					break;
				case CALC_FNC_MAX:
					_fncName = "max";
					break;
				case CALC_FNC_LST:
					_fncName = "last";
					break;
				case CALC_FNC_AVG:
				default:
					_fncName = "avg";
			}

			_values.put(i, empty(Nest.value(data, _itemid, _type, _fncName).$())
				? 0
				: abs(Nest.value(data, _itemid, _type, _fncName).asDouble())
			);
		}

		switch (type) {
			case GRAPH_TYPE_EXPLODED:
				drawElementPie(_values);
				break;
			case GRAPH_TYPE_3D:
				drawElementPie3D(_values);
				break;
			case GRAPH_TYPE_3D_EXPLODED:
				drawElementPie3D(_values);
				break;
			default:
				drawElementPie(_values);
		}

		drawLogo();
		if (drawLegend) {
			drawLegend();
		}

//		String _str = sprintf("%.2f", (Double)microtime(true) - _start_time);
//		_str = _s("Data from %1$s. Generated in %2$s sec.", dataFrom, _str);
//		CArray<Double> _strSize = imageTextSize(6, 0, _str);
//		imageText(
//			g2d,
//			6,
//			0,
//			fullSizeX - Nest.value(_strSize,"width").asInteger() - 5,
//			fullSizeY - 5,
//			getColor("Gray"),
//			_str
//		);

//		unset(items, data);
		items = null;
		data = null;

		return Cgd.imageOut(im);
	}
	
	/********************************************************************************************************/
	/* PRE CONFIG: ADD / SET / APPLY
	/********************************************************************************************************/
	public void addItem(String _itemid, int _calc_fnc, String _color, String _type) {
		CArray _items = CMacrosResolverHelper.resolveItemNames(this.idBean, this.executor, array(get_item_by_itemid(this.executor, _itemid)));

		items.put(num, reset(_items));

		Map _host = get_host_by_hostid(this.idBean, this.executor, Nest.value(items,num, "hostid").asLong());

		Nest.value(items, num, "host").$(Nest.value(_host,"host").$());
		Nest.value(items, num, "hostname").$(Nest.value(_host,"name").$());
		Nest.value(items, num, "color").$(is_null(_color) ? "Dark Green" : _color);
		Nest.value(items, num, "calc_fnc").$(is_null(_calc_fnc) ? CALC_FNC_AVG : _calc_fnc);
		Nest.value(items, num, "calc_type").$(is_null(_type) ? GRAPH_ITEM_SIMPLE : _type);

		num++;
	}
	public void addItem(String _itemid, int _calc_fnc, String _color) {
		addItem(_itemid, _calc_fnc, _color, null);
	}
	public void addItem(String _itemid, int _calc_fnc) {
		addItem(_itemid, _calc_fnc, null);
	}
	public void addItem(String _itemid) {
		addItem(_itemid, CALC_FNC_AVG);
	}

	public void set3DAngle(int _angle) {
		if (is_numeric(_angle) && _angle < 85 && _angle > 10) {
			angle3d = (int) _angle;
		}
		else {
			angle3d = 70;
		}
	}
	public void set3DAngle() {
		set3DAngle(70);
	}

	public int switchPie3D(boolean _type) {
		if (_type) {
			type = asInteger(_type);
		}
		else {
			switch (type) {
				case GRAPH_TYPE_EXPLODED:
					type = GRAPH_TYPE_3D_EXPLODED;
					break;
				case GRAPH_TYPE_3D_EXPLODED:
					type = GRAPH_TYPE_EXPLODED;
					break;
				case GRAPH_TYPE_3D:
					type = GRAPH_TYPE_PIE;
					break;
				case GRAPH_TYPE_PIE:
					type = GRAPH_TYPE_3D;
					break;
				default:
					type = GRAPH_TYPE_PIE;
			}
		}
		return type;
	}
	public int switchPie3D() {
		return switchPie3D(false);
	}

	public int switchPieExploded(boolean _type) {
		if (_type) {
			type = asInteger(_type);
		}
		else {
			switch (type) {
				case GRAPH_TYPE_EXPLODED:
					type = GRAPH_TYPE_PIE;
					break;
				case GRAPH_TYPE_3D_EXPLODED:
					type = GRAPH_TYPE_3D;
					break;
				case GRAPH_TYPE_3D:
					type = GRAPH_TYPE_3D_EXPLODED;
					break;
				case GRAPH_TYPE_PIE:
					type = GRAPH_TYPE_EXPLODED;
					break;
				default:
					type = GRAPH_TYPE_PIE;
			}
		}
		return type;
	}

	protected void calc3dheight(int _height) {
		graphheight3d = (int) (_height / 20);
	}

	protected CArray<Integer> calcExplodedCenter(double _anglestart, double _angleend, int x, int y, int _count) {
		_count *= exploderad;
		int _anglemid = (int) ((_anglestart + _angleend) / 2);

		y+= round(_count * sin(deg2rad(_anglemid)));
		x+= round(_count * cos(deg2rad(_anglemid)));

		return array(x, y);
	}

	protected CArray<Integer> calcExplodedRadius(int _sizeX, int _sizeY, int _count) {
		_count *= exploderad * 2;
		_sizeX -= _count;
		_sizeY -= _count;
		return array(_sizeX, _sizeY);
	}

	protected CArray<Integer> calc3DAngle(int _sizeX, int _sizeY) {
		_sizeY *= (double)GRAPH_3D_ANGLE / 90;
		return array(_sizeX, (int)round(_sizeY));
	}

	protected void selectData() {
		data = array();
		long _now = time(null);

		if (isset(stime)) {
			from_time = stime;
			to_time = stime + period;
		}
		else {
			to_time = _now - SEC_PER_HOUR * from;
			from_time = to_time - period;
		}

		int _strvaluelength = 0; // we need to know how long in px will be our legend

		// fetch values for items with the \"last\" function
		CArray _lastValueItems = array();
		for(Map _item: items) {
			if (Nest.value(_item,"calc_fnc").asInteger() == CALC_FNC_LST) {
				_lastValueItems.add(_item);
			}
		}
		Map _history = null;
		if (!empty(_lastValueItems)) {
			_history = Manager.History(idBean, executor).getLast(_lastValueItems);
		}

		Map _config = select_config(this.idBean, this.executor);		
		double _graph_sum = 0;
		Map params = new HashMap();
		for (int i = 0; i < num; i++) {
			Map _item = get_item_by_itemid(this.executor, Nest.value(items, i, "itemid").asString());
			Object _type =  Nest.value(items, i, "calc_type").$();
			Long _from_time = from_time;
			Long _to_time = to_time;
			
			String _itemid = Nest.value(items, i, "itemid").asString();
			params.put("_itemid", _itemid);
			params.put("from_time", _from_time);
			params.put("to_time", _to_time);

			CArray<String> _sql_arr = array();

			// override item history setting with housekeeping settings
			if (Nest.value(_config,"hk_history_global").asBoolean()) {
				Nest.value(_item,"history").$(Nest.value(_config,"hk_history").$());
			}

			boolean _trendsEnabled = Nest.value(_config,"hk_trends_global").asBoolean() ? (Nest.value(_config,"hk_trends").asInteger() > 0) : (Nest.value(_item,"trends").asInteger() > 0);

			if (!_trendsEnabled || ((Nest.value(_item,"history").asInteger() * SEC_PER_DAY) > (time() - (_from_time + period / 2)))) {
				this.dataFrom = "history";

				array_push(_sql_arr,
					"SELECT h.itemid,"+
						"AVG(h.value) AS avg,MIN(h.value) AS min,"+
						"MAX(h.value) AS max,MAX(h.clock) AS clock"+
					" FROM history h"+
					" WHERE h.itemid=#{_itemid}"+
						" AND h.clock>=#{from_time}"+
						" AND h.clock<=#{to_time}"+
					" GROUP BY h.itemid"
					,
					"SELECT hu.itemid,"+
						"AVG(hu.value) AS avg,MIN(hu.value) AS min,"+
						"MAX(hu.value) AS max,MAX(hu.clock) AS clock"+
					" FROM history_uint hu"+
					" WHERE hu.itemid=#{_itemid}"+
						" AND hu.clock>=#{from_time}"+
						" AND hu.clock<=#{to_time}"+
					" GROUP BY hu.itemid"
				);
			}
			else {
				this.dataFrom = "trends";

				array_push(_sql_arr,
					"SELECT t.itemid,"+
						"AVG(t.value_avg) AS avg,MIN(t.value_min) AS min,"+
						"MAX(t.value_max) AS max,MAX(t.clock) AS clock"+
					" FROM trends t"+
					" WHERE t.itemid=#{_itemid}"+
						" AND t.clock>=#{from_time}"+
						" AND t.clock<=#{to_time}"+
					" GROUP BY t.itemid"
					,
					"SELECT t.itemid,"+
						"AVG(t.value_avg) AS avg,MIN(t.value_min) AS min,"+
						"MAX(t.value_max) AS max,MAX(t.clock) AS clock"+
					" FROM trends_uint t"+
					" WHERE t.itemid=#{_itemid}"+
						" AND t.clock>=#{from_time}"+
						" AND t.clock<=#{to_time}"+
					" GROUP BY t.itemid"
				);
			}

			Object __history_itemid = _item.get("itemid");
			Nest.value(data, _itemid, _type, "last").$(isset(Nest.value(_history, __history_itemid).$())? Nest.value(_history, __history_itemid, 0, "value").$(): null);
			Nest.value(data, _itemid, _type, "shift_min").$(0);
			Nest.value(data, _itemid, _type, "shift_max").$(0);
			Nest.value(data, _itemid, _type, "shift_avg").$(0);

			for(String _sql: _sql_arr) {
				CArray<Map> _result = DBselect(this.executor, _sql, params);
				for(Map _row: _result) {
					Nest.value(data, _itemid, _type, "min").$(Nest.value(_row,"min").$());
					Nest.value(data, _itemid, _type, "max").$(Nest.value(_row,"max").$());
					Nest.value(data, _itemid, _type, "avg").$(Nest.value(_row,"avg").$());
					Nest.value(data, _itemid, _type, "clock").$(Nest.value(_row,"clock").$());
				}
//				unset(_row);
			}

			String _fncName;
			switch (Nest.value(items, i, "calc_fnc").asInteger()) {
				case CALC_FNC_MIN:
					_fncName = "min";
					break;
				case CALC_FNC_MAX:
					_fncName = "max";
					break;
				case CALC_FNC_LST:
					_fncName = "last";
					break;
				case CALC_FNC_AVG:
				default:
					_fncName = "avg";
			}

			double _item_value = empty(Nest.value(data, _itemid, _type, _fncName).$())
				? 0
				: abs(Nest.value(data, _itemid, _type, _fncName).asDouble());

			if (asInteger(_type) == GRAPH_ITEM_SUM) {
				background = i;
				_graph_sum = _item_value;
			}

			sum += _item_value;

			int _convertedUnit = rda_strlen(convert_units(map(
				"value", _item_value,
				"units", Nest.value(items, i, "units").$()
			)));
			_strvaluelength = Math.max(_strvaluelength, _convertedUnit);
		}

		if (_graph_sum != 0) {
			sum = _graph_sum;
		}
		shiftlegendright += _strvaluelength * 7;
	}

	protected void drawLegend() {
		int _shiftY = shiftY + shiftYLegend;
		int _max_host_len = 0;
		int _max_name_len = 0;

		for (int i = 0; i < num; i++) {
			if (rda_strlen(Nest.value(items, i, "hostname").asString()) > _max_host_len) {
				_max_host_len = rda_strlen(Nest.value(items, i, "hostname").asString());
			}
			if (rda_strlen(Nest.value(items, i, "name_expanded").asString()) > _max_name_len) {
				_max_name_len = rda_strlen(Nest.value(items, i, "name_expanded").asString());
			}
		}

		for (int i = 0; i < num; i++) {
			Color _color = getColor(Nest.value(items, i, "color").asString(), 0);
			Object _type = Nest.value(items, i, "calc_type").$();
			Object _itemid = Nest.value(items, i, "itemid").asString();
			
			CArray _data = Nest.value(data, _itemid, _type).asCArray();
			String _fncName, _fncRealName;
			
			switch (Nest.value(items, i, "calc_fnc").asInteger()) {
				case CALC_FNC_MIN:
					_fncName = "min";
					_fncRealName = _("min");
					break;
				case CALC_FNC_MAX:
					_fncName = "max";
					_fncRealName = _("max");
					break;
				case CALC_FNC_LST:
					_fncName = "last";
					_fncRealName = _("last");
					break;
				case CALC_FNC_AVG:
				default:
					_fncName = "avg";
					_fncRealName = _("avg");
			}
			double _datavalue = Nest.value(data, _itemid, _type, _fncName).asDouble();

			double _proc = sum == 0 ? 0 : (_datavalue * 100) / sum;

			String _strvalue, _str;
			if (isset(_data) && isset(_datavalue)) {
				_strvalue = sprintf(_("Value")+": %s ("+(round(_proc) != round(_proc, 2) ? "%.2f" : "%.0f")+"%%)",
					convert_units(map(
						"value", _datavalue,
						"units", Nest.value(items, i, "units").$()
					)),
					_proc
				);

				_str = sprintf("%s: %s [%s] ",
					str_pad(Nest.value(items, i, "hostname").asString(), _max_host_len, " "),
					str_pad(Nest.value(items, i, "name_expanded").asString(), _max_name_len, " "),
					_fncRealName
				);
			}
			else {
				_strvalue = sprintf(_("Value: no data"));
				_str = sprintf("%s: %s [ "+_("no data")+" ]",
					str_pad(Nest.value(items, i, "hostname").asString(), _max_host_len, " "),
					str_pad(Nest.value(items, i, "name_expanded").asString(), _max_name_len, " ")
				);
			}

			//图标内颜色
			imagefilledrectangle(
				g2d,
				shiftXleft,
				sizeY + _shiftY + 14 * i - 5,
				shiftXleft + 10,
				sizeY + _shiftY + 5 + 14 * i,
				_color
			);
			//图标外框
			imagerectangle(
				g2d,
				shiftXleft,
				sizeY + _shiftY + 14 * i - 5,
				shiftXleft + 10,
				sizeY + _shiftY + 5 + 14 * i,
				getColor("Black No Alpha")
			);

			imageText(
				g2d,
				8,
				0,
				shiftXleft + 15,
				sizeY + _shiftY + 14 * i + 5,
				getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
				_str
			);

			int _shiftX = fullSizeX - shiftlegendright - shiftXright + 25;

			imagefilledrectangle(
				g2d,
				_shiftX - 10,
				shiftY + 10 + 14 * i,
				_shiftX,
				shiftY + 10 + 10 + 14 * i,
				_color
			);

			imagerectangle(
				g2d,
				_shiftX - 10,
				shiftY + 10 + 14 * i,
				_shiftX,
				shiftY + 10 + 10 + 14 * i,
				getColor("Black No Alpha")
			);

			imageText(
				g2d,
				8,
				0,
				_shiftX + 5,
				shiftY + 10 + 14 * i + 10,
				getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
				_strvalue
			);
		}

		if (sizeY < 120) {
			return;
		}
	}

	protected void drawElementPie(CArray<Map> _values) {
		double _sum = sum;

		if (background != null) {
			double _least = 0;
			for(Entry<Object, Map> entry: _values.entrySet()) {
				Integer _item = asInteger(entry.getKey());
				double _value = asDouble(entry.getValue());
				if (_item != background) {
					_least += _value;
				}
			}
			_values.put(background, asDouble(_values.get(background)) - _least);
		}
		
		boolean _isEmptyData;

		if (_sum <= 0) {
			_values = map(0, 1);
			_sum = 1;
			_isEmptyData = true;
		}
		else {
			_isEmptyData = false;
		}

		int _sizeX = sizeX;
		int _sizeY = sizeY;

		if (type == GRAPH_TYPE_EXPLODED) {
			CArray<Integer> rs = calcExplodedRadius(_sizeX, _sizeY, count(_values));
			_sizeX = rs.get(0);
			_sizeY = rs.get(1);
		}
		else {
			_sizeX = (int) (_sizeX * 0.95);
			_sizeY = (int) (_sizeY * 0.95);
		}

		int _xc, x, _yc, y;
		_xc = x = (int) sizeX / 2 + shiftXleft;
		_yc = y = (int) sizeY / 2 + shiftY;

		double _anglestart = 0;
		double _angleend = 0;
		for(Entry<Object, Map> entry: _values.entrySet()) {
			Integer _item = asInteger(entry.getKey());
			double _value = asDouble(entry.getValue());
			_angleend += (int) (360 * _value / _sum) + 1;
			_angleend = (_angleend > 360) ? 360 : _angleend;
			if ((_angleend - _anglestart) < 1) {
				continue;
			}

			if (type == GRAPH_TYPE_EXPLODED) {
				CArray<Integer> rs = calcExplodedCenter(_anglestart, _angleend, _xc, _yc, count(_values));
				x = rs.get(0);
				y = rs.get(1);
			}

			imagefilledarc(
				g2d,
				x,
				y,
				_sizeX,
				_sizeY,
				(int)_anglestart,
				(int)_angleend,
				getColor((!_isEmptyData ? Nest.value(items, _item, "color").asString() : "FFFFFF"), 0),
				IMG_ARC_PIE
			);
			imagefilledarc(
				g2d,
				x,
				y,
				_sizeX,
				_sizeY,
				(int)_anglestart,
				(int)_angleend,
				getColor("Black"),
				IMG_ARC_PIE | IMG_ARC_EDGED | IMG_ARC_NOFILL
			);
			_anglestart = _angleend;
		}
	}

	protected void drawElementPie3D(CArray<Map> _values) {
		double _sum = sum;

		if (background != null) {
			double _least = 0;
			for(Entry<Object, Map> entry: _values.entrySet()) {
				Integer _item = asInteger(entry.getKey());
				double _value = asDouble(entry.getValue());
				if (!_item.equals(background)) {
					_least += _value;
				}
			}
			_values.put(background, asDouble(_values.get(background)) - _least);
		}
		
		boolean _isEmptyData;
		if (_sum <= 0) {
			_values = map(0, 1);
			_sum = 1;
			_isEmptyData = true;
		}
		else {
			_isEmptyData = false;
		}

		int _sizeX = sizeX;
		int _sizeY = sizeY;

		exploderad = exploderad3d;

		if (type == GRAPH_TYPE_3D_EXPLODED) {
			CArray<Integer> rs = calcExplodedRadius(_sizeX, _sizeY, count(_values));
			_sizeX = rs.get(0);
			_sizeY = rs.get(1);
		}

		CArray<Integer> rs = calc3DAngle(_sizeX, _sizeY);
		_sizeX = rs.get(0);
		_sizeY = rs.get(1);

		int _xc, x, _yc, y;
		_xc = x = (int) sizeX / 2 + shiftXleft;
		_yc = y = (int) sizeY / 2 + shiftY;

		// bottom angle line
		double _anglestart = 0;
		double _angleend = 0;
		for(Entry<Object, Map> entry: _values.entrySet()) {
			Integer _item = asInteger(entry.getKey());
			double _value = asDouble(entry.getValue());
			_angleend += (int) (360 * _value / _sum) + 1;
			_angleend = (_angleend > 360) ? 360 : _angleend;
			if ((_angleend - _anglestart) < 1) {
				continue;
			}

			if (type == GRAPH_TYPE_3D_EXPLODED) {
				rs = calcExplodedCenter(_anglestart, _angleend, _xc, _yc, count(_values));
				x = rs.get(0);
				y = rs.get(1);
			}
			imagefilledarc(
				g2d,
				x,
				y + graphheight3d + 1,
				_sizeX,
				_sizeY,
				(int)_anglestart,
				(int)_angleend,
				getShadow((!_isEmptyData ? Nest.value(items, _item, "color").asString(): "FFFFFF"), 0),
				IMG_ARC_PIE
			);
			imagefilledarc(
				g2d,
				x,
				y + graphheight3d + 1,
				_sizeX,
				_sizeY,
				(int)_anglestart,
				(int)_angleend,
				getColor("Black"),
				IMG_ARC_PIE | IMG_ARC_EDGED | IMG_ARC_NOFILL
			);
			_anglestart = _angleend;
		}

		// 3d effect
		for (int i = graphheight3d; i > 0; i--) {
			_anglestart = 0;
			_angleend = 0;
			for(Entry<Object, Map> entry: _values.entrySet()) {
				Integer _item = asInteger(entry.getKey());
				double _value = asDouble(entry.getValue());
				_angleend += (int) (360 * _value / _sum) + 1;
				_angleend = (_angleend > 360) ? 360 : _angleend;

				if ((_angleend - _anglestart) < 1) {
					continue;
				}
				else if (sum == 0) {
					continue;
				}

				if (type == GRAPH_TYPE_3D_EXPLODED) {
					rs = calcExplodedCenter(_anglestart, _angleend, _xc, _yc, count(_values));
					x = rs.get(0);
					y = rs.get(1);
				}

				imagefilledarc(
					g2d,
					x,
					y + i,
					_sizeX,
					_sizeY,
					(int)_anglestart,
					(int)_angleend,
					getShadow((!_isEmptyData ? Nest.value(items, _item, "color").asString() : "FFFFFF"), 0),
					IMG_ARC_PIE
				);
				_anglestart = _angleend;
			}
		}

		_anglestart = 0;
		_angleend = 0;
		for(Entry<Object, Map> entry: _values.entrySet()) {
			Integer _item = asInteger(entry.getKey());
			double _value = asDouble(entry.getValue());
			_angleend += (int) (360 * _value / _sum) + 1;
			_angleend = (_angleend > 360) ? 360 : _angleend;
			if ((_angleend - _anglestart) < 1) {
				continue;
			}

			if (type == GRAPH_TYPE_3D_EXPLODED) {
				rs = calcExplodedCenter(_anglestart, _angleend, _xc, _yc, count(_values));
				x = rs.get(0);
				y = rs.get(1);
			}

			imagefilledarc(
				g2d,
				x,
				y,
				_sizeX,
				_sizeY,
				(int)_anglestart,
				(int)_angleend,
				getColor((!_isEmptyData ? Nest.value(items, _item, "color").asString(): "FFFFFF"), 0),
				IMG_ARC_PIE
			);
			imagefilledarc(
				g2d,
				x,
				y,
				_sizeX,
				_sizeY,
				(int)_anglestart,
				(int)_angleend,
				getColor("Black"),
				IMG_ARC_PIE | IMG_ARC_EDGED | IMG_ARC_NOFILL
			);
			_anglestart = _angleend;
		}
	}
}
