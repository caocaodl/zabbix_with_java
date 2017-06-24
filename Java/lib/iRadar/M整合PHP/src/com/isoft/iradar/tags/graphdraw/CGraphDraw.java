package com.isoft.iradar.tags.graphdraw;


import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cgd.imagerectangle;
import static com.isoft.iradar.Cgd.imagestringup;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.inc.Defines.CALC_FNC_ALL;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_NORMAL;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.FuncsUtil.rdaDateToTime;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2age;
import static com.isoft.iradar.inc.GraphsUtil.dashedRectangle;
import static com.isoft.iradar.inc.GraphsUtil.imageText;
import static com.isoft.iradar.inc.GraphsUtil.imageTextSize;
import static com.isoft.iradar.inc.UsersUtil.getUserTheme;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.radar.CDB;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.model.CWebUser;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public abstract class CGraphDraw {
	
	protected Long stime = null;
	protected Integer fullSizeX = null;
	protected Integer fullSizeY = null;
	protected CArray<Double> m_minY = array();
	protected CArray<Double> m_maxY = array();
	protected CArray<Map> data = array();
	protected CArray<Map> items = array();
	protected Object min = null;
	protected Object max = null;
	protected Object avg = null;
	protected Object clock = null;
	protected Object count = null;
	protected String header = null;
	protected Long from_time = null;
	protected Long to_time = null;
	protected CArray<Color> colors = array();
	protected BufferedImage im = null;
	protected Graphics2D g2d = null;
	protected int period = SEC_PER_HOUR;
	protected int from = 0;
	protected int sizeX = 900; // default graph size X
	protected int sizeY = 200; // default graph size Y
	protected int shiftXleft = 100;
	protected int shiftXright = 50;
	protected int shiftXCaption = 0;
	protected int shiftY = 36;
	protected int border = 1;
	protected int num = 0;
	protected Integer type; // graph type
	protected boolean drawLegend = true;
	protected CArray axis_valuetype = array(); // overal items type (int/float)
	protected CArray graphtheme = map(
		"description", "default",
		"frontendtheme", "default.css",
		"textcolor", "202020",
		"highlightcolor", "aa4444",
		"backgroundcolor", "f0f0f0",
		"graphcolor", "ffffff",
		"graphbordercolor", "333333",
		"gridcolor", "cccccc",
		"maingridcolor", "aaaaaa",
		"gridbordercolor", "000000",
		"nonworktimecolor", "eaeaea",
		"leftpercentilecolor", "00AA00",
		"righttpercentilecolor", "AA0000",
		"legendview", "1",
		"gridview", "1"
	);
	
	protected CArray<Color> colorsrgb;
	
	protected IIdentityBean idBean;
	protected SQLExecutor executor;
	protected String dataFrom;
	protected Double sum = 0d;
	
	public CGraphDraw(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, GRAPH_TYPE_NORMAL);
	}
	
	public CGraphDraw(IIdentityBean idBean, SQLExecutor executor, int graphType) {
		this.idBean = idBean;
		this.executor = executor;
		this.type = graphType;
		this.applyGraphTheme();
	}
	
	protected void initColors() {
		this.colorsrgb = map(
			"Red"				, new Color(255, 0, 0, 50),
			"Dark Red"			, new Color(150, 0, 0, 50),
			"Green"				, new Color(0, 255, 0, 50),
			"Dark Green"		, new Color(0, 150, 0, 50),
			"Blue"				, new Color(0, 0, 255, 50),
			"Dark Blue"			, new Color(0, 0, 150, 50),
			"Yellow"			, new Color(255, 255, 0, 50),
			"Dark Yellow"		, new Color(150, 150, 0, 50),
			"Cyan"				, new Color(0, 255, 255, 50),
			"Dark Cyan"			, new Color(0, 150, 150, 50),
			"Black"				, new Color(0, 0, 0, 50),
			"Gray"				, new Color(150, 150, 150, 50),
			"White"				, new Color(255, 255, 255),
			"Dark Red No Alpha"	, new Color(150, 0, 0),
			"Black No Alpha"	, new Color(0, 0, 0),
			"HistoryMinMax"		, new Color(90, 150, 185, 50),
			"HistoryMax"		, new Color(255, 100, 100, 50),
			"HistoryMin"		, new Color(50, 255, 50, 50),
			"HistoryAvg"		, new Color(50, 50, 50, 50),
			"ValueMinMax"		, new Color(255, 255, 150, 50),
			"ValueMax"			, new Color(255, 180, 180, 50),
			"ValueMin"			, new Color(100, 255, 100, 50),
			"Not Work Period"	, new Color(230, 230, 230),
			"UnknownData"		, new Color(130, 130, 130, 50)
		);
		
		for(Entry<Object, Color> entry: colorsrgb.entrySet()) {
			Color c = entry.getValue();
			int alpha = c.getAlpha();
			if(alpha != 255) {
				entry.setValue(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha^255));
			}
		}
		
		// i should rename no alpha to alpha at some point to get rid of some confusion
		for (Entry<Object, Color> entry: this.colorsrgb.entrySet()) {
			Object _name = entry.getKey();
			Color RGBA = entry.getValue(); 
			
//			if (isset($RGBA[3]) && function_exists('imagecolorexactalpha')
//					&& function_exists('imagecreatetruecolor') && @imagecreatetruecolor(1, 1)) {
//				_this->colors[_name] = imagecolorexactalpha(_this->im, $RGBA[0], $RGBA[1], $RGBA[2], $RGBA[3]);
//			}
//			else {
//				_this->colors[_name] = imagecolorallocate(_this->im, $RGBA[0], $RGBA[1], $RGBA[2]);
//			}
			
			this.colors.put(_name, RGBA);
		}
	}
	
	/**
	 * Load the graph theme from the database.
	 */
	public void applyGraphTheme() {
		Map _theme = CDB.find(this.executor, "graph_theme", map(
			"theme", getUserTheme(this.idBean, this.executor, CWebUser.data())
		));
		_theme = reset(_theme);
		if (!empty(_theme)) {
			this.graphtheme = CArray.valueOf(_theme);
		}
	}
	
	public void showLegend() {
		this.showLegend(true);
	}
	public void showLegend(boolean $) {
		this.drawLegend = $;
	}
	
	public void setPeriod(int $) {
		this.period = $;
	}
	
	public void setSTime(Long _stime) {
		if (_stime > 19000000000000L && _stime < 21000000000000L) {
			this.stime = rdaDateToTime(Nest.as(_stime).asString());
		}
		else {
			this.stime = _stime;
		}
	}
	
	public void setFrom(int _from) {
		from = _from;
	}
	
	public void setWidth() {
		setWidth(null);
	}
	public void setWidth(Integer _value) {
		// avoid sizex==0, to prevent division by zero later
		if (_value == 0) {
			_value = null;
		}
		if (is_null(_value)) {
			_value = 900;
		}
		this.sizeX = _value;
		
	}

	public void setHeight() {
		setHeight(null);
	}
	public void setHeight(Integer _value) {
		if (_value == 0) {
			_value = null;
		}
		if (is_null(_value)) {
			_value = 900;
		}
		this.sizeY = _value;
	}
	
	public void setBorder(int i) {
		this.border = i;
	}
	
	public int getLastValue(int _num) {
		CArray _data = Nest.value(data, items.getNested(_num, "itemid"), items.getNested(_num, "calc_type")).asCArray();

		if (isset(_data)) {
			for (int i = sizeX - 1; i >= 0; i--) {
				if (!empty(_data.getNested("count", i))) {
					switch (Nest.value(items, _num, "calc_fnc").asInteger()) {
						case CALC_FNC_MIN:
							return Nest.value(_data, "min", i).asInteger();
						case CALC_FNC_MAX:
							return Nest.value(_data, "max", i).asInteger();
						case CALC_FNC_ALL:
						case CALC_FNC_AVG:
						default:
							return Nest.value(_data, "avg", i).asInteger();
					}
				}
			}
		}

		return 0;
	}
	
	protected void drawRectangle() {
//		imagefilledrectangle(g2d, 0, 0,
//			this.fullSizeX,
//			this.fullSizeY,
//			this.getColor((String)this.graphtheme.get("backgroundcolor"), 0)
//		);

		if (this.border == 1) {
			imagerectangle(g2d, 0, 0,
				this.fullSizeX - 1,
				this.fullSizeY - 1,
				this.getColor((String)this.graphtheme.get("backgroundcolor"), 0)
			);
		}
	}
	
	protected void drawSmallRectangle() {
		dashedRectangle(g2d,
			shiftXleft + shiftXCaption - 1,
			shiftY - 1,
			sizeX + shiftXleft + shiftXCaption - 1,
			sizeY + shiftY + 1,
			getColor("Black No Alpha").getRGB()
		);
	}
	
	public String period2str(long _period) {
		return " ("+rda_date2age(0, _period)+")";
	}
	
	protected void drawHeader() {
		String _str;
		if (!isset(header)) {
			_str = items.getNested(0, "hostname")+NAME_DELIMITER+items.getNested(0, "name");
		}
		else {
			_str = CMacrosResolverHelper.resolveGraphName(this.idBean, this.executor, header, items);
		}

		_str += period2str(period);
		
		int x=0, _fontsize;
		// calculate largest font size that can fit graph header
		// TODO: font size must be dynamic in other parts of the graph as well, like legend, timeline, etc
		for (_fontsize = 11; _fontsize > 7; _fontsize--) {
			Map _dims = imageTextSize(_fontsize, 0, _str);
			x = fullSizeX / 2 - (Nest.value(_dims,"width").asInteger() / 2);

			// most important information must be displayed, period can be out of the graph
			if (x < 2) {
				x = 2;
			}
			if (Nest.value(_dims,"width").asInteger() <= fullSizeX) {
				break;
			}
		}

		imageText(g2d, _fontsize, 0, x, 24, getColor(Nest.value(graphtheme,"textcolor").asString(), 0), _str);
	}
	
	public void setHeader(String _chartHeader) {
		this.header = _chartHeader;
	}
	
	public void drawLogo() {
//		imagestringup(g2d, 0,
//			this.fullSizeX - 10,
//			this.fullSizeY - 50,
//			"http://www.i-soft.com.cn",
//			this.getColor("Gray")
//		);
	}
	
	public Color getColor(String _color) {
		return getColor(_color, 50);
	}
	public Color getColor(String _color, int _alfa) {
		if (isset(colors.get(_color))) {
			return colors.get(_color);
		}

		Color color = Color.decode("0x"+_color);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), _alfa^255);
	}

	public Color getShadow(String _color) {
		return getShadow(_color, 0);
	}
	public Color getShadow(String _color, int _alfa) {
		Color c = getColor(_color, _alfa);

		if (this.sum > 0) {
			double r = c.getRed()*0.6;
			double g = c.getGreen()*0.6;
			double b = c.getBlue()*0.6;
			c = new Color((int)r, (int)g, (int)b, c.getAlpha());
		}
		
		return c;
	}
	
	public abstract byte[] draw() throws Exception;
}
