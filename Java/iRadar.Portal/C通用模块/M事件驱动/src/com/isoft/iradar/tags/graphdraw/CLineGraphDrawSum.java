package com.isoft.iradar.tags.graphdraw;

import static com.isoft.iradar.Cgd.imagecreate;
import static com.isoft.iradar.Cgd.imagefill;
import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cgd.imagerectangle;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.min;
import static com.isoft.iradar.inc.Defines.CALC_FNC_ALL;
import static com.isoft.iradar.inc.Defines.CALC_FNC_AVG;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MAX;
import static com.isoft.iradar.inc.Defines.CALC_FNC_MIN;
import static com.isoft.iradar.inc.Defines.GRAPH_STACKED_ALFA;
import static com.isoft.iradar.inc.Defines.GRAPH_TYPE_STACKED;
import static com.isoft.iradar.inc.Defines.GRAPH_YAXIS_SIDE_LEFT;
import static com.isoft.iradar.inc.Defines.ITEM_CONVERT_NO_UNITS;
import static com.isoft.iradar.inc.Defines.NAME_DELIMITER;
import static com.isoft.iradar.inc.FuncsUtil.convert_units;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CImageTextTable;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CLineGraphDrawSum extends CLineGraphDraw {
	
	private int originNum;
	
	public CLineGraphDrawSum(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor);
		this.type = GRAPH_TYPE_STACKED;
	}

	protected void selectData() {
		super.selectData();
		
		Map item = (Map)Nest.value(this.items, this.num-1).$();
		Map _curr_data = (Map)data.getNested(item.get("itemid"), item.get("calc_type"));

		if (!isset(_curr_data)) {
			return;
		}

		for (int _ci = 0; _ci < sizeX; _ci++) {
			for(String _var_name: array("min", "max")) {
				String _shift_var_name = "shift_"+_var_name;
				Map _curr_shift = (Map)_curr_data.get(_shift_var_name);
				Map _curr_var = (Map)_curr_data.get(_var_name);
				
				float _shift = Nest.value(_curr_shift, _ci).asFloat();
				float _var =  Nest.value(_curr_var, _ci).asFloat();
				_curr_var.put(_ci, _shift+_var);
			}
		}
		
		float avg_orig=0; 
		for(int i=0; i<this.num; i++) {
			Map i_item = (Map)Nest.value(this.items, i).$();
			Map i_curr_data = (Map)data.getNested(i_item.get("itemid"), i_item.get("calc_type"));
			
			avg_orig += Nest.value(i_curr_data, "avg_orig").asFloat();
		}
		_curr_data.put("avg_orig", avg_orig);
		
		this.originNum = this.num;
		this.num = 1;
		Nest.value(items, this.num-1).$(item);
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

				
				float data_cur = 0;
				for(int di=0; di<this.originNum; di++) {
					data_cur += getLastValue(i);
				}
				
				_legend.addCell(_rowNum, map("image" , _colorSquare, "marginRight" , 5));
				_legend.addCell(_rowNum, map("text" , _itemCaption));
				_legend.addCell(_rowNum, map("text" , "["+_fncRealName+"]"));
				_legend.addCell(_rowNum, map(
					"text" , convert_units(map(
						"value" , data_cur,
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
	}
}
