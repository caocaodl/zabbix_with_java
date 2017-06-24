package com.isoft.iradar.tags;

import static com.isoft.iradar.Cgd.imagecopy;
import static com.isoft.iradar.Cgd.imagesx;
import static com.isoft.iradar.Cgd.imagesy;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.inc.GraphsUtil.imageText;
import static com.isoft.iradar.inc.GraphsUtil.imageTextSize;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CImageTextTable {
	
	public Graphics2D image;
	public int fontsize;
	public int color;
	public int align;
	public int x;
	public int y;

	/**
	 * Minimal row height. If the height of some row is bigger then given, the _rowheight will be set to this height.
	 *
	 * @var int
	 */
	public int rowheight;

	private CArray<CArray<Map>> table;
	private int numrows;

	public CImageTextTable(Graphics2D _image, int x, int y) {
		this.image = _image;
		this.fontsize = 8;
		this.rowheight = 0;
		this.color = 0;
		this.align = 0;
		this.x = x;
		this.y = y;
		this.table = array();
		this.numrows = 0;
	}

	public int getNumRows() {
		return numrows;
	}

	/**
	 * Adds a new table cell.
	 *
	 * Supported _cell options:
	 * - marginRight    - right margin, defaults to 20
	 * - image          - resource of the image to display in the cell
	 * - text           - text to display in the cell
	 * - color          - text color resource
	 * - align          - text alignment: 0 - left, 1 - center, 2 - right
	 * - fontsize       - text font size
	 *
	 * @param int   _numrow
	 * @param array _cell
	 *
	 * @return void
	 */
	public void addCell(int _numrow, CArray _cell) {
		if (_numrow >= numrows) {
			_numrow = numrows;
			numrows++;
			table.put(_numrow, array());
		}
		Nest.value(table, _numrow).push(_cell);
	}

	public void addRow(CArray _row) {
		table.put(numrows, _row);
		numrows++;
	}

	public void draw() {
		calcRows();

		int _coly = y;
		for(CArray<Map> _row: table) {
			int _rowx = x;
			int _height = 0;
			
			for(Map _col: _row) {
				Nest.value(_col,"marginRight").$((isset(Nest.value(_col,"marginRight").$())) ? Nest.value(_col,"marginRight").$() : 20);

				// draw image
				if (isset(Nest.value(_col,"image").$())) {
					int _imageWidth = imagesx((BufferedImage)Nest.value(_col,"image").$());
					int _imageHeight = imagesy((BufferedImage)Nest.value(_col,"image").$());

					imagecopy(
						image,
						(BufferedImage)Nest.value(_col,"image").$(),
						_rowx,
						_coly - _imageHeight + 1,
						0,
						0,
						_imageWidth,
						_imageHeight
					);
				}
				// draw text
				else {
					Color _text_color = isset(Nest.value(_col,"color").$()) ? (Color)Nest.value(_col,"color").$() : new Color(color);
					int _align = align;
					if (isset(Nest.value(_col,"align").$())) {
						if (Nest.value(_col,"align").asInteger() == 1) {
							_align = floor((Nest.value(_col,"width").asInteger() - Nest.value(_col,"size","width").asInteger()) / 2); // center
						}
						else if (Nest.value(_col,"align").asInteger() == 2) {
							_align = Nest.value(_col,"width").asInteger() - Nest.value(_col,"size","width").asInteger(); // right
						}
					}
					imageText(image, Nest.value(_col,"fontsize").asInteger(), 0, _rowx+_align, _coly, _text_color, Nest.value(_col,"text").asString());
				}

				_rowx += Nest.value(_col,"width").asInteger() + Nest.value(_col,"marginRight").asInteger();
				_height = Nest.value(_col,"height").asInteger();
			}
			_coly += _height;
		}
	}

	/**
	 * Calculates the size of each row and column.
	 *
	 * @return void
	 */
	private void calcRows() {
		int _rowHeight = 0;
		CArray<Integer> _colWidth = array();

		
		for(Entry<Object, CArray<Map>> entry: table.entrySet()) {
			Object y = entry.getKey();
			CArray<Map> _row = entry.getValue();
			
			for(Entry<Object, Map> entryX: _row.entrySet()) {
				Object x = entryX.getKey();
				Map _col = entryX.getValue();
			
				
				CArray _dims;
				// calculate size from image
				if (isset(Nest.value(_col,"image").$())) {
					_dims = map(
						"width" , imagesx((BufferedImage)Nest.value(_col,"image").$()),
						"height" , imagesy((BufferedImage)Nest.value(_col,"image").$())
					);
				}
				// calculate size from text
				else {
					if (!isset(Nest.value(_col,"fontsize").$())) {
						Nest.value(_col,"fontsize").$(fontsize);
					}
					table.put(y, x, "fontsize", Nest.value(_col,"fontsize").$());

					_dims = imageTextSize(Nest.value(_col,"fontsize").asInteger(), 0, Nest.value(_col,"text").asString());
				}

				table.put(y, x, "size", _dims);

				_rowHeight = (Nest.value(_dims,"height").asInteger() > _rowHeight) ? Nest.value(_dims,"height").asInteger() : _rowHeight;

				if (!isset(_colWidth, x)) {
					_colWidth.put(x, Nest.value(_dims,"width").$());
				}
				else if (Nest.value(_dims,"width").asInteger() > Nest.value(_colWidth, x).asDouble()) {
					_colWidth.put(x, Nest.value(_dims,"width").$());
				}
			}
		}

		if (_rowHeight < rowheight) {
			_rowHeight = rowheight;
		}
		else {
			rowheight = _rowHeight;
		}

		for(Entry<Object, CArray<Map>> entry: table.entrySet()) {
			Object y = entry.getKey();
			CArray<Map> _row = entry.getValue();
			
			for(Entry<Object, Map> entryC: _row.entrySet()) {
				Object x = entryC.getKey();
				//Map _col = entryC.getValue();
			
				table.put(y, x, "height", _rowHeight);
				table.put(y, x, "width", _colWidth.get(x));
			}
		}
	}
}
