package com.isoft.iradar.tags.graphdraw;

import static com.isoft.iradar.Cgd.imagecreate;
import static com.isoft.iradar.Cgd.imagefilledpolygon;
import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cgd.imagepolygon;
import static com.isoft.iradar.Cgd.imagerectangle;
import static com.isoft.iradar.Cphp.STR_PAD_LEFT;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.abs;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.microtime;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.str_pad;
import static com.isoft.iradar.core.utils.EasyObject.asBoolean;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_BAR;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_BAR_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_COLUMN;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_COLUMN_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_LEFT;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_RIGHT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.DrawUtil.rda_imagealine;
import static com.isoft.iradar.inc.DrawUtil.rda_imageline;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.iradar.inc.FuncsUtil.uint_in_array;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.GraphsUtil.dashedLine;
import static com.isoft.iradar.inc.GraphsUtil.dashedRectangle;
import static com.isoft.iradar.inc.GraphsUtil.imageText;
import static com.isoft.iradar.inc.GraphsUtil.imageTextSize;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.RenderingHints;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.Cgd;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CBarGraphDraw extends CGraphDraw {
	
	private Object background = false;
	private int 	opacity = 15; // bar/column opacity
	private Double 	shiftlegendright = 0d; // count of static chars * px/char + for color rectangle + space
	private Double 	shiftCaption = 0d;
	private Double 	maxCaption = 0d;
	private CArray<CArray<Map>> 	series = array();
	private boolean stacked = false;
	private CArray<String> 	periodCaption = array();
	private CArray<String> seriesLegend = array();
	private CArray<String> seriesColor = array();
	private int 	seriesCount = 0;
	private Double 	columnWidth = 10d; // bar/column width per serie
	private Double 	seriesWidth = 10d; // overal per serie bar/column width
	private Double 	seriesDistance = 10d;
	private boolean axisSideLeft = false; // do values for axis left/top persist
	private boolean axisSideRight = false; // do values for axis right/bottom persist
	private String 	xLabel = null;
	private String 	yLabel = null;
	private CArray<Double> 	yaxismin = map(GRAPH_YAXIS_SIDE_LEFT , 0, 	GRAPH_YAXIS_SIDE_RIGHT , 0);
	private CArray<Double> 	yaxismax = map(GRAPH_YAXIS_SIDE_LEFT , 100, 	GRAPH_YAXIS_SIDE_RIGHT , 100);
	private CArray 	minValue = map(GRAPH_YAXIS_SIDE_LEFT , 0, 	GRAPH_YAXIS_SIDE_RIGHT , 0);
	private CArray 	maxValue = map(GRAPH_YAXIS_SIDE_LEFT , null, 	GRAPH_YAXIS_SIDE_RIGHT , null);
	private Integer gridLinesCount = null; // how many grids to draw
	private Double 	gridPixels = 30d; // optimal grid size
	private CArray<Double>	gridStep = map(GRAPH_YAXIS_SIDE_LEFT , null, GRAPH_YAXIS_SIDE_RIGHT , null); // set value
	private CArray<Integer>	side_values = map(GRAPH_YAXIS_SIDE_LEFT , ITEM_VALUE_TYPE_UINT64, GRAPH_YAXIS_SIDE_RIGHT, ITEM_VALUE_TYPE_UINT64); // 0 - float, 3 - uint
	private boolean column = false;
	private CArray<String> units = map(GRAPH_YAXIS_SIDE_LEFT , "", GRAPH_YAXIS_SIDE_RIGHT , ""); // units for values

	private double shiftXCaptionLeft;
	private double shiftXCaptionRight;
	private double shiftYCaptionTop;
	private double shiftYCaptionBottom;
	private double shiftYLegend;
	private CArray<Double> unit2px = array();
	private CArray<Double> zero = array();
	private CArray<Double> oxy = array();
		
	public CBarGraphDraw(IIdentityBean idBean, SQLExecutor executor) {
		this(idBean, executor, GRAPH_TYPE_COLUMN);
		sum = null;                                                                                                                                    
		drawLegend = false;                                                                                                                                 
		shiftY = 46;                                                                                                                                   
	}
	
	public CBarGraphDraw(IIdentityBean idBean, SQLExecutor executor, int graphType) {
		super(idBean, executor, graphType);
	}

	/********************************************************************************************************/
	// PRE CONFIG:	ADD / SET / APPLY
	/********************************************************************************************************/
	public void setGridStep(double _step) {
		setGridStep(_step, GRAPH_YAXIS_SIDE_LEFT);
	}
	public void setGridStep(double _step, int _axis) {
		gridStep.put(_axis, _step);
	}

	public void setUnits(String _units) {
		setUnits(_units, GRAPH_YAXIS_SIDE_LEFT);
	}
	public void setUnits(String _units, int _axis) {
		units.put(_axis, _units);
	}

	public void setSideValueType(int _type) {
		setSideValueType(_type, GRAPH_YAXIS_SIDE_LEFT);
	}
	public void setSideValueType(int _type, int _axis) {
		side_values.put(_axis, _type);
	}

	@Override
	public void showLegend() {
		showLegend(null);
	}
	public boolean showLegend(Object _type) {
		if (!is_null(_type)) {
			drawLegend = asBoolean(_type);
			return drawLegend;
		}
		else if (drawLegend == false) {
			drawLegend = true;
		}
		else {
			drawLegend = false;
		}
		return drawLegend;
	}

	public void setXLabel(String _label) {
		xLabel = _label;
	}

	public void setYLabel(String _label) {
		yLabel = _label;
	}

	public int addSeries(CArray _serie) {
		return addSeries(_serie, GRAPH_YAXIS_SIDE_LEFT);
	}
	public int addSeries(CArray<?> _serie, int _axis) {
		if (GRAPH_YAXIS_SIDE_LEFT == _axis) {
			axisSideLeft = true;
		}
		else {
			axisSideRight = true;
		}

		for(Entry<Object, ?> entry: _serie.entrySet()) {
			Object _key = entry.getKey();
			Object _value = entry.getValue();
			
			periodCaption.put(_key, _key);

			if (!isset(series, _key)) {
				series.put(_key, array());
			}
			series.put(_key, seriesCount, map("axis" , _axis, "value" , _value));
		}

		seriesCount++;
		return seriesCount;
	}

	public void setPeriodCaption(CArray<String> _periodCaption) {
		for(Entry<Object, String> entry: _periodCaption.entrySet()) {
			Object _key = entry.getKey();
			String _value = entry.getValue();
			
			periodCaption.put(_key, _value);

			CArray _tmp = imageTextSize(8, 0, _value);
			if (Nest.value(_tmp,"width").asDouble() > maxCaption) {
				maxCaption = Nest.value(_tmp,"width").asDouble();
			}
		}
		shiftCaption = maxCaption;
	}

	public void setSeriesLegend(CArray<String> _seriesLegend) {
		for(Entry<Object, String> entry: _seriesLegend.entrySet()) {
			Object _key = entry.getKey();
			String _value = entry.getValue();
			
			seriesLegend.put(_key, _value);

			double _tmp = rda_strlen(_value) * 7 + 8; // count of chars * font size + color box
			if (_tmp > shiftlegendright) {
				shiftlegendright = _tmp;
			}
		}
	}

	public void setSeriesColor(CArray<String> _seriesColor) {
		for(Entry<Object, String> entry: _seriesColor.entrySet()) {
			Object _key = entry.getKey();
			String _value = entry.getValue();
			seriesColor.put(_key, String.valueOf(_value));
		}
	}
	
	protected void calcShifts() {
		shiftXleft = 10 + (is_null(xLabel) ? 0 : 16);
		shiftXright = 10;

		if (!drawLegend) {
			shiftlegendright = 0d;
		}

		if (column) {
			shiftXCaptionLeft = axisSideLeft ? 100 : 50;
			shiftXCaptionRight = axisSideRight ? 100 : 50;

			shiftYCaptionTop = 0;
			shiftYCaptionBottom = shiftCaption;
		}
		else {
			shiftYCaptionTop = axisSideLeft ? 100 : 50;
			shiftYCaptionBottom = axisSideRight ? 100 : 50;

			shiftXCaptionLeft = shiftCaption;
			shiftXCaptionRight = 0;
		}

		shiftYLegend =  0 + (is_null(yLabel) ? 0 : 16);
	}
	
	protected void calcSeriesWidth() {
		int _serieLength = count(periodCaption);

		double _seriesSizeX, _seriesSizeY, _tmp;
		if (column) {
			_seriesSizeX = sizeX - (seriesDistance * _serieLength);

			// division by zero
			_tmp = _serieLength * seriesCount;
			if (_tmp == 0) {
				_tmp = 1;
			}

			columnWidth = _seriesSizeX / _tmp;

			if (_serieLength == 0) {
				_serieLength = 1;
			}
			seriesWidth = _seriesSizeX / _serieLength;
		}
		else {
			_seriesSizeY = sizeY - (seriesDistance * _serieLength);

			// division by zero
			_tmp = _serieLength * seriesCount;
			if (_tmp == 0) {
				_tmp = 1;
			}

			columnWidth = _seriesSizeY / _tmp;

			if (_serieLength == 0) {
				_serieLength = 1;
			}
			seriesWidth = _seriesSizeY / _serieLength;
		}
	}
	
	// calculation of minimum Y axis
	protected void calcMiniMax() {
		if (stacked) {
			for (int i = 0; i < seriesCount; i++) {
				int _axis = GRAPH_YAXIS_SIDE_LEFT;
				double _stackedMinValue = 0;
				double _stackedMaxValue = 0;

				for(CArray _series: series) {
					Double _value = Nest.value(_series, i, "value").asDouble();

					if (_value > 0) {
						_stackedMaxValue += _value;
					}
					else {
						_stackedMinValue += _value;
					}
				}

				if (Nest.value(minValue, _axis).asDouble() > _stackedMinValue) {
					minValue.put(_axis, _stackedMinValue);
				}

				if (Nest.value(maxValue, _axis).asDouble() < _stackedMaxValue || is_null(Nest.value(maxValue, _axis).asDouble())) {
					maxValue.put(_axis, _stackedMaxValue);
				}
			}
		}
		else {
			for(CArray<Map> _series: series) {
				for(Map _serie: _series) {
					if (Nest.value(minValue, _serie.get("axis")).asDouble() > Nest.value(_serie,"value").asDouble()) {
						minValue = Nest.value(_serie,"value").asCArray();
					}
					
					if (Nest.value(maxValue, _serie.get("axis")).asDouble() < Nest.value(_serie,"value").asDouble() || is_null(Nest.value(maxValue, _serie.get("axis")).$())) {
						maxValue.put(_serie.get("axis"), Nest.value(_serie,"value").$());
					}
				}
			}
		}
	}
	
	protected void calcZero() {
		int _left = GRAPH_YAXIS_SIDE_LEFT;
		int _right = GRAPH_YAXIS_SIDE_RIGHT;

		unit2px.put(_right, (Nest.value(m_maxY, _right).asDouble() - Nest.value(m_minY, _right).asDouble()) / sizeY);
		unit2px.put(_left, (Nest.value(m_maxY, _left).asDouble() - Nest.value(m_minY, _left).asDouble()) / sizeY);

		if (m_minY.get(_right) > 0) {
			zero.put(_right, sizeY + shiftY);
			oxy.put(_right, min(Nest.value(m_minY, _right).asDouble(), Nest.value(m_maxY, _right).asDouble()));
		}
		else if (m_maxY.get(_right) < 0) {
			zero.put(_right, shiftY);
			oxy.put(_right, max(Nest.value(m_minY, _right).asDouble(), Nest.value(m_maxY, _right).asDouble()));
		}
		else {
			zero.put(_right, sizeY + shiftY - (int)abs(m_minY.get(_right) / unit2px.get(_right)));
			oxy.put(_right, 0);
		}

		if (m_minY.get(_left) > 0) {
			zero.put(_left, sizeY + shiftY);
			oxy.put(_left, min(Nest.value(m_minY, _left).asDouble(), Nest.value(m_maxY, _left).asDouble()));
		}
		else if (m_maxY.get(_left) < 0) {
			zero.put(_left, shiftY);
			oxy.put(_left, max(Nest.value(m_minY, _left).asDouble(), Nest.value(m_maxY, _left).asDouble()));
		}
		else {
			zero.put(_left, sizeY + shiftY - (int)abs(m_minY.get(_left) / unit2px.get(_left)));
			oxy.put(_left, 0);
		}
	}
	
	protected void correctMiniMax() {
		CArray _sides = array();
		if (axisSideLeft) {
			_sides.add( GRAPH_YAXIS_SIDE_LEFT );
		}
		if (axisSideRight) {
			_sides.add( GRAPH_YAXIS_SIDE_RIGHT );
		}

		for(Object _axis: _sides) {
			if (is_null(gridStep.get(_axis))) {
				if (column) {
					gridLinesCount = round(sizeY/gridPixels) + 1;
				}
				else {
					gridLinesCount = round(sizeX/gridPixels) + 1;
				}

				double _maxValue = Nest.value(maxValue, _axis).asDouble();
				double _minValue = Nest.value(minValue, _axis).asDouble();

				if (side_values.get(_axis) == ITEM_VALUE_TYPE_UINT64) {
					if (_maxValue < gridLinesCount) {
						return; //return true;
					}

					_maxValue = round(_maxValue);
					_minValue = floor(_minValue);

					double _value_delta = round(_maxValue - _minValue);

					double _step = floor(((_value_delta/gridLinesCount) + 1)); // round to top
					double _value_delta2 = _step * gridLinesCount;

					double _first_delta = round((_value_delta2 - _value_delta) / 2);
					double _second_delta = _value_delta2 - _value_delta - _first_delta;

					if (_minValue >= 0) {
						if (_minValue < _second_delta) {
							_first_delta += _second_delta - _minValue;
							_second_delta = _minValue;
						}
					}
					else if (_maxValue <= 0) {
						if (_maxValue > _first_delta) {
							_second_delta += _first_delta - _maxValue;
							_first_delta = _maxValue;
						}
					}

					_maxValue += _first_delta;
					_minValue -= _value_delta2 - _value_delta - _first_delta;
				}
				else if (((Integer)ITEM_VALUE_TYPE_FLOAT).equals(side_values)) {
					if (_maxValue > 0) {
						_maxValue = round(_maxValue, 1) + round(_maxValue, 1) * 0.1 + 0.05;
					}
					else if (_maxValue < 0) {
						_maxValue = round(_maxValue, 1) - round(_maxValue, 1) * 0.1 + 0.05;
					}

					if (_minValue > 0) {
						_minValue = _minValue - (_minValue * 0.2) - 0.05;
					}
					else if (_minValue < 0) {
						_minValue = _minValue + (_minValue * 0.2) - 0.05;
					}
					_minValue = round(_minValue, 1);
				}

				minValue.put(_axis, _minValue);
				maxValue.put(_axis, _maxValue); //_this->miaxValue[_axis] = _maxValue;
			}
			else {
				if (is_null(gridLinesCount)) {
					gridLinesCount = floor(Nest.value(maxValue, _axis).asDouble() / gridStep.get(_axis)) + 1;
				}

				// needs to be fixed!!!
				// via gridLinesCount can't be different for each axis,
				// due to this, gridStep must be some how normalised before calculations
				maxValue.put(_axis, gridStep.get(_axis) * gridLinesCount);
			}
		}
	}
	
	//***************************************************************************
	//									DRAW									*
	//***************************************************************************
	public void drawSmallRectangle() {
		Color _gbColor = getColor(Nest.value(graphtheme,"gridbordercolor").asString(), 0);

		imagefilledrectangle(g2d,
			(int)(shiftXleft + shiftXCaptionLeft - 1),
			(int)(shiftY - 1 + shiftYCaptionTop),
			(int)(sizeX + shiftXleft + shiftXCaptionLeft - 1),
			(int)(sizeY + shiftY + 1 + shiftYCaptionTop),
			getColor(Nest.value(graphtheme,"graphcolor").asString(), 0)
		);

		dashedRectangle(g2d,
			(int)(shiftXleft + shiftXCaptionLeft - 1),
			(int)(shiftY - 1 + shiftYCaptionTop),
			(int)(sizeX + shiftXleft + shiftXCaptionLeft - 1),
			(int)(sizeY + shiftY + 1 + shiftYCaptionTop),
			getColor(Nest.value(graphtheme,"gridcolor").asString(), 0).getRGB()
		);

		rda_imageline(g2d,
			shiftXleft + shiftXCaptionLeft - 1,
			shiftY - 5,
			shiftXleft + shiftXCaptionLeft - 1,
			sizeY + shiftY + 4,
			_gbColor.getRGB()
		);

		CArray<Integer> _sides = array();
		if (axisSideLeft) {
			_sides.add( GRAPH_YAXIS_SIDE_LEFT );
		}
		if (axisSideRight) {
			_sides.add( GRAPH_YAXIS_SIDE_RIGHT );
		}

		for(int _axis: _sides){
			int _sideCorrection, _triangle;
			if (_axis == GRAPH_YAXIS_SIDE_LEFT) {
				_sideCorrection = -1;
				_triangle = 0;
			}
			else {
				_sideCorrection = sizeX;
				_triangle = sizeX + 1;
			}
			rda_imageline(g2d,
				shiftXCaptionLeft + shiftXleft + _sideCorrection,
				shiftY - 5,
				shiftXCaptionLeft + shiftXleft + _sideCorrection,
				sizeY + shiftY + 4,
				getColor(Nest.value(graphtheme,"gridbordercolor").asString(), 0).getRGB()
				);

			imagefilledpolygon(g2d,
					(CArray)array(
						shiftXleft + shiftXCaptionLeft + _triangle - 4, shiftY - 5,
						shiftXleft + shiftXCaptionLeft + _triangle + 2, shiftY - 5,
						shiftXleft + shiftXCaptionLeft + _triangle - 1, shiftY - 10
					),
					3,
					getColor("White")
				);

			imagepolygon(g2d,
				(CArray)array(
					shiftXleft + shiftXCaptionLeft + _triangle - 4, shiftY - 5,
					shiftXleft + shiftXCaptionLeft + _triangle + 2, shiftY - 5,
					shiftXleft + shiftXCaptionLeft + _triangle - 1, shiftY - 10
				),
				3,
				getColor(Nest.value(graphtheme,"gridbordercolor").asString(), 0).getRGB()
			);
		}

		rda_imageline(g2d,
			shiftXleft + shiftXCaptionLeft - 4,
			sizeY + shiftY + 1,
			sizeX + shiftXleft + shiftXCaptionLeft + 5,
			sizeY + shiftY + 1,
			_gbColor.getRGB()
		);

		imagefilledpolygon(g2d,
			(CArray)array(
				sizeX + shiftXleft + shiftXCaptionLeft + 5, sizeY + shiftY - 2,
				sizeX + shiftXleft + shiftXCaptionLeft + 5, sizeY + shiftY + 4,
				sizeX + shiftXleft + shiftXCaptionLeft + 10, sizeY + shiftY + 1
			),
			3,
			getColor("White")
		);

		// draw X axis triangle
		rda_imageline(g2d, sizeX + shiftXleft + shiftXCaptionLeft + 5, sizeY + shiftY - 2,
			sizeX + shiftXleft + shiftXCaptionLeft + 5, sizeY + shiftY + 4,
			_gbColor.getRGB());
		rda_imagealine(im, g2d, sizeX + shiftXleft + (int)shiftXCaptionLeft + 5, sizeY + shiftY + 4,
			sizeX + shiftXleft + (int)shiftXCaptionLeft + 10, sizeY + shiftY + 1,
			_gbColor.getRGB());
		rda_imagealine(im, g2d, sizeX + shiftXleft + (int)shiftXCaptionLeft + 10, sizeY + shiftY + 1,
			sizeX + shiftXleft + (int)shiftXCaptionLeft + 5, sizeY + shiftY - 2,
			_gbColor.getRGB());
	}

	protected void drawGrid() {
		drawSmallRectangle();

		if (column) {
			int _hline_count = Nest.as(gridLinesCount).asInteger();

			for (int i = 1; i < _hline_count; i++) {
				dashedLine(g2d,
					(int)(shiftXleft + shiftXCaptionLeft),
					(int)(i * (sizeY / _hline_count) + shiftY + shiftYCaptionTop),
					(int)(sizeX + shiftXleft + shiftXCaptionLeft),
					(int)(i * (sizeY / _hline_count) + shiftY + shiftYCaptionTop),
					getColor("Gray").getRGB()
				);
			}

			int i = 0;
			for(Entry<Object, CArray<Map>> entry: series.entrySet()) {
				Object _key = entry.getKey();
//				CArray<Map> _serie = entry.getValue();
				
				String _caption = periodCaption.get(_key);
				CArray<Double> _dims = imageTextSize(7, 90, _caption);

				imageText(
					g2d,
					7,
					90,
					(int)(i*(seriesWidth+seriesDistance)+shiftXleft+shiftXCaptionLeft+round(seriesWidth/2)+_dims.get("width")*2),
					(int)(sizeY+shiftY + Nest.value(_dims,"height").asDouble() +6),
					getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
					_caption
				);
				i++;
			}
		}
		else {
			int _vline_count = gridLinesCount;

			for(int i = 1;i < _vline_count; i++) {
				dashedLine(g2d,
					(int)(i * (sizeX / _vline_count) + shiftXleft + shiftXCaptionLeft),
					(int)(shiftY + shiftYCaptionTop),
					(int)(i * (sizeX / _vline_count) + shiftXleft + shiftXCaptionLeft),
					(int)(sizeY + shiftY + shiftYCaptionTop),
					getColor("Gray")
				);
			}

			int i = 0;
			for(Entry<Object, CArray<Map>> entry: series.entrySet()) {
				Object _key = entry.getKey();
				CArray<Map> _serie = entry.getValue();
				
				String _caption = periodCaption.get(_key);
				_caption = str_pad(_caption, maxCaption.intValue(), " ", STR_PAD_LEFT);

				imageText(g2d, 8, 0,
					shiftXleft,
					(int)((sizeY + shiftY + shiftYCaptionTop) - (i * (seriesWidth + seriesDistance) + seriesDistance + round(seriesWidth / 2))),
					getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
					_caption
				);
				i++;
			}
		}
	}
	
	protected void drawSideValues() {
		CArray<Integer> _sides = array();
		if (axisSideLeft) {
			_sides.add( GRAPH_YAXIS_SIDE_LEFT );
		}
		if (axisSideRight) {
			_sides.add( GRAPH_YAXIS_SIDE_RIGHT );
		}

		for(Integer _axis: _sides) {
			double _min = Nest.value(minValue,_axis).asDouble();
			double _max = Nest.value(maxValue,_axis).asDouble();

			int _hstr_count = gridLinesCount;

			if (column) {
				for (int i = 0;i <= _hstr_count; i++) {
					String _str = convert_units(map(
						"value" , (double)sizeY * i / _hstr_count * (_max - _min) / sizeY + _min,
						"units" , units.get(_axis)
					));

					int _sideShift = 0;
					if (GRAPH_YAXIS_SIDE_LEFT == _axis) {
						CArray<Double> _dims = imageTextSize(8, 0, _str);
						_sideShift = -1 * (Nest.value(_dims,"width").asInteger() + 10);
					}
					else {
						_sideShift = sizeX + 10;
					}

					imageText(g2d, 8, 0,
						(int)(shiftXleft + shiftXCaptionLeft + _sideShift),
						(int)(sizeY - (double)sizeY * i / _hstr_count + shiftY + shiftYCaptionTop + 6),
						getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
						_str
					);
				}
			}
			else if (uint_in_array(type, array(GRAPH_TYPE_BAR, GRAPH_TYPE_BAR_STACKED))) {
				double _shiftYBottom;
				if (GRAPH_YAXIS_SIDE_LEFT == _axis) {
					_shiftYBottom = shiftY + shiftYCaptionTop - 2; // -2 because of some mistake somewhere in calculations! FIX IT!
				}
				else {
					_shiftYBottom = shiftY + sizeY + shiftYCaptionTop + shiftYCaptionBottom;
				}

				for (int i = 0; i <= _hstr_count; i++) {
					String _str = convert_units(map(
						"value" , (double)sizeX * i / _hstr_count * (_max - _min) / sizeX + _min,
						"units" , units.get(_axis)
					));

					int _sideShift = 0;
					if (GRAPH_YAXIS_SIDE_LEFT == _axis) {
						CArray<Double> _dims = imageTextSize(8, 90, _str);
						_sideShift = Nest.value(_dims,"height").asInteger();
					}

					imageText(g2d, 8, 90,
						(int)(shiftXleft + ((double)sizeX * i / _hstr_count - 4) + shiftXCaptionLeft),
						(int)(_shiftYBottom - _sideShift),
						getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
						_str
					);
				}
			}
		}

		if (!is_null(xLabel)) {
			CArray<Double> _dims = imageTextSize(10, 0, xLabel);
			imageText(g2d, 10, 0,
				(int)(shiftXCaptionLeft + shiftXleft + (double)sizeX / 2 - Nest.value(_dims,"width").asDouble() / 2),
				(int)(fullSizeY - 10 - Nest.value(_dims,"height").asDouble()),
				getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
				xLabel
			);
		}

		if (!is_null(yLabel)) {
			CArray<Double> _dims = imageTextSize(10, 90, yLabel);
			imageText(g2d, 10, 90,
				(int)(shiftXleft + Nest.value(_dims,"width").asDouble()),
				(int)(shiftY + (double)sizeY / 2 + Nest.value(_dims,"height").asDouble() / 2),
				getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
				yLabel
			);
		}
	}
	
	protected void drawLegend() {
		if (!drawLegend) {
			return;
		}

		int _shiftY = shiftY;
		double _shiftX = fullSizeX - shiftlegendright - shiftXright;

		int _count = 0;
		
		for(Entry<Object, CArray<Map>> entry: series.entrySet()) {
			Object _key = entry.getKey();
			CArray<Map> _serie = entry.getValue();
			
			for(Entry<Object, Map> entryS: _serie.entrySet()) {
				Object _num = entryS.getKey();
				Map _value = entryS.getValue();
			
				String _caption = seriesLegend.get(_num);
				Color _color = getColor(seriesColor.get(_num), 0);

				imagefilledrectangle(
					g2d,
					(int)_shiftX - 5,
					_shiftY + 14 * _count - 5,
					(int)_shiftX + 5,
					_shiftY + 5 + 14 * _count,
					_color
				);

				imagerectangle(
					g2d,
					(int)_shiftX - 5,
					_shiftY + 14 * _count - 5,
					(int)_shiftX + 5,
					_shiftY + 5 + 14 * _count,
					getColor("Black No Alpha")
				);

				imageText(g2d, 8, 0,
					(int)_shiftX + 10,
					_shiftY - 5 + 14 * _count + 10,
					getColor(Nest.value(graphtheme,"textcolor").asString(), 0),
					_caption
				);

				_count++;
			}
			break;
		}
	}
	
	@Override
	public byte[] draw() throws Exception {
		double _start_time = microtime(true);
//		set_image_header();

		column = uint_in_array(type, array(GRAPH_TYPE_COLUMN, GRAPH_TYPE_COLUMN_STACKED));

		fullSizeX = sizeX;
		fullSizeY = sizeY;

		if (sizeX < 300 || sizeY < 200) {
			showLegend(0);
		}

		calcShifts();

		sizeX -= shiftXleft + shiftXright + shiftlegendright + shiftXCaptionLeft + shiftXCaptionRight;
		sizeY -= shiftY + shiftYLegend + shiftYCaptionBottom + shiftYCaptionTop;

		calcSeriesWidth();
		calcMiniMax();
		correctMiniMax();

//		if (function_exists("imagecolorexactalpha") && function_exists("imagecreatetruecolor") && @imagecreatetruecolor(1, 1)) {
//			im = imagecreatetruecolor(fullSizeX, fullSizeY);
//		}
//		else {
			im = imagecreate(fullSizeX, fullSizeY);
			g2d = im.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	//		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	//		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//		}

		initColors();
		drawRectangle();
		drawHeader();
		drawGrid();
		drawSideValues();
		drawLogo();
		drawLegend();

		int _count = 0;
		double _start;
		if (column) {
			_start = shiftXleft + shiftXCaptionLeft + floor(seriesDistance / 2);
		}
		else {
			_start = sizeY + shiftY + shiftYCaptionTop - floor(seriesDistance / 2);
		}

		for(Entry<Object, CArray<Map>> entry: series.entrySet()) {
			Object _key = entry.getKey();
			CArray<Map> _series = entry.getValue();
			
			for(Entry<Object, Map> entryS: _series.entrySet()) {
				Object _num = entryS.getKey();
				Map _serie = entryS.getValue();
				
				int _axis = Nest.value(_serie,"axis").asInteger();
				double _value = Nest.value(_serie,"value").asDouble();

				Color _color = getColor(seriesColor.get(_num), opacity);
				if (column) {
					imagefilledrectangle(
						g2d,
						(int)(_start),
						(int)(sizeY + shiftY + shiftYCaptionTop - round((sizeY / Nest.value(maxValue, _axis).asDouble()) * _value)),
						(int)(_start + columnWidth),
						(int)(sizeY + shiftY + shiftYCaptionTop),
						_color
					);

					imagerectangle(
						g2d,
						(int)(_start),
						(int)(sizeY + shiftY + shiftYCaptionTop - round((sizeY / Nest.value(maxValue, _axis).asDouble()) * _value)),
						(int)(_start + columnWidth),
						(int)(sizeY + shiftY + shiftYCaptionTop),
						getColor("Black No Alpha")
					);
				}
				else {
					imagefilledrectangle(
						g2d,
						(int)(shiftXleft + shiftXCaptionLeft),
						(int)(_start - columnWidth),
						(int)(shiftXleft + shiftXCaptionLeft + round((sizeX / Nest.value(maxValue, _axis).asDouble()) * _value)),
						(int)(_start),
						_color
					);

					imagerectangle(
						g2d,
						(int)(shiftXleft + shiftXCaptionLeft),
						(int)(_start - columnWidth),
						(int)(shiftXleft + shiftXCaptionLeft + round((sizeX / Nest.value(maxValue, _axis).asDouble()) * _value)),
						(int)(_start),
						getColor("Black No Alpha")
					);
				}
				_start = column ? _start + columnWidth : _start - columnWidth;
			}

			_count++;
			if (column) {
				_start = _count * (seriesWidth + seriesDistance) + shiftXleft + shiftXCaptionLeft + floor(seriesDistance / 2);
			}
			else {
				_start = (sizeY + shiftY + shiftYCaptionTop) - (_count * (seriesWidth + seriesDistance)) - floor(seriesDistance / 2);
			}
		}

		String _str = sprintf("%.2f", (Double)microtime(true) - _start_time);
		_str = _s("Generated in %s sec", _str);
		CArray<Double> _strSize = imageTextSize(6, 0, _str);
		imageText(g2d, 6, 0, fullSizeX - Nest.value(_strSize,"width").asInteger() - 5, fullSizeY - 5, getColor("Gray"), _str);

//		unset(items, data);
		items = data = null;

		g2d.dispose();
		
		return Cgd.imageOut(im);
	}
}
