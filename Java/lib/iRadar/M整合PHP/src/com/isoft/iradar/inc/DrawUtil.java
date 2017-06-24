package com.isoft.iradar.inc;

import static com.isoft.iradar.Cgd.BLUE;
import static com.isoft.iradar.Cgd.GREEN;
import static com.isoft.iradar.Cgd.RED;
import static com.isoft.iradar.Cgd.imagecolorat;
import static com.isoft.iradar.Cgd.imagecolorresolvealpha;
import static com.isoft.iradar.Cgd.imagecolorsforindex;
import static com.isoft.iradar.Cgd.imageline;
import static com.isoft.iradar.Cgd.imagesetpixel;
import static com.isoft.iradar.Cphp.abs;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.inc.Defines.LINE_TYPE_BOLD;
import static com.isoft.iradar.inc.Defines.LINE_TYPE_NORMAL;
import static com.isoft.iradar.inc.FuncsUtil.rda_swap;
import static com.isoft.types.CArray.array;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;

public class DrawUtil {
	
	/**
	 * Calculate new color based on bg/fg colors and transparency index
	 *
	 * @param   resource  image      image reference
	 * @param   array     bgColor    background color, array of RGB
	 * @param   array     fgColor    foreground color, array of RGB
	 * @param   float     alpha      transparency index in range of 0-1, 1 returns unchanged fgColor color
	 *
	 * @return  array                 new color
	 */
	public static int rda_colormix(Graphics2D image, CArray<Integer> bgColor, CArray<Integer> fgColor, double alpha) {
		double r = bgColor.get(0) + (fgColor.get(0) - bgColor.get(0)) * alpha;
		double g = bgColor.get(1) + (fgColor.get(1) - bgColor.get(1)) * alpha;
		double b = bgColor.get(2) + (fgColor.get(2) - bgColor.get(2)) * alpha;
		return imagecolorresolvealpha(image, (int)r, (int)g, (int)b, 255);
	}
	
	/**
	 * Draw normal line.
	 * PHP imageline() function is broken because it drops fraction instead of correct rounding of X/Y coordinates.
	 * All calls to imageline() must be replaced by the wrapper function everywhere in the code.
	 *
	 * @param resource  _image  image reference
	 * @param int       x1     first x coordinate
	 * @param int       y1     first y coordinate
	 * @param int       x2     second x coordinate
	 * @param int       y2     second y coordinate
	 * @param int       color  line color
	 */
	public static void rda_imageline(Graphics2D image, double x1, double y1, double x2, double y2, int color) {
		rda_imageline(image, x1, y1, x2, y2, color, false);
	}
	public static void rda_imageline(Graphics2D image, double x1, double y1, double x2, double y2, int color, boolean hasAlpha) {
		imageline(image, round(x1), round(y1), round(x2), round(y2), color, hasAlpha);
	}
	public static void rda_imageline(Graphics2D image, double x1, double y1, double x2, double y2, Color color) {
		imageline(image, round(x1), round(y1), round(x2), round(y2), color);
	}

	public static void rda_imagealine(BufferedImage img, Graphics2D image, int x1, int y1, int x2, int y2, int color) {
		rda_imagealine(img, image, x1, y1, x2, y2, color, LINE_TYPE_NORMAL);
	}
	
	/**
	 * Draw antialiased line
	 *
	 * @param resource  _image  image reference
	 * @param int       x1     first x coordinate
	 * @param int       y1     first y coordinate
	 * @param int       x2     second x coordinate
	 * @param int       y2     second y coordinate
	 * @param int       color  line color
	 * @param int       style  line style, one of LINE_TYPE_NORMAL (default), LINE_TYPE_BOLD (bold line)
	 */
	public static void rda_imagealine(BufferedImage img, Graphics2D image, int x1, int y1, int x2, int y2, int color, int style) {
		x1 = round(x1);
		y1 = round(y1);
		x2 = round(x2);
		y2 = round(y2);

		if (x1 == x2 && y1 == y2) {
			imagesetpixel(image, x1, y1, color);
			return;
		}

		// Get foreground line color
		CArray lc = imagecolorsforindex(image, color);
		lc = array(Nest.value(lc,RED).$(), Nest.value(lc,GREEN).$(), Nest.value(lc,BLUE).$());

		int dx = x2 - x1;
		int dy = y2 - y1;
		
		CArray bc;
		if (abs(dx) > abs(dy)) {
			if (dx < 0) {
				TObj<Integer> rx1 = Nest.as(x1), rx2 = Nest.as(x2);
				rda_swap(rx1, rx2);
				x1 = rx1.$();
				x2 = rx2.$();
				y1 = y2;
			}
			for (double x = x1, y = y1; x <= x2; x++, y = y1 + (x - x1) * dy / dx) {
				int yint = floor(y);
				double yfrac = y - yint;

				if (LINE_TYPE_BOLD == style) {
					bc = imagecolorsforindex(image, imagecolorat(img, (int)x, yint - 1));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, (int)x, yint - 1, rda_colormix(image, lc, bc, yfrac));

					bc = imagecolorsforindex(image, imagecolorat(img, (int)x, yint + 1));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, (int)x, yint + 1, rda_colormix(image, lc, bc, 1 - yfrac));

					imagesetpixel(image, (int)x, yint, color);
				} else {
					bc = imagecolorsforindex(image, imagecolorat(img, (int)x, yint));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, (int)x, yint, rda_colormix(image, lc, bc, yfrac));

					bc = imagecolorsforindex(image, imagecolorat(img, (int)x, yint + 1));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, (int)x, yint + 1, rda_colormix(image, lc, bc, 1 - yfrac));
				}
			}
		} else {
			if (dy < 0) {
				TObj<Integer> ry1 = Nest.as(y1), ry2 = Nest.as(y2);
				rda_swap(ry1, ry2);
				y1 = ry1.$();
				y2 = ry2.$();
				x1 = x2;
			}
			for (double y = y1, x = x1; y <= y2; y++, x = x1 + (y - y1) * dx / dy) {
				int xint = floor(x);
				double xfrac = x - xint;

				if (LINE_TYPE_BOLD == style) {
					bc = imagecolorsforindex(image, imagecolorat(img, xint - 1, (int)y));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, xint - 1, (int)y, rda_colormix(image, lc, bc, xfrac));

					bc = imagecolorsforindex(image, imagecolorat(img, xint + 1, (int)y));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, xint + 1, (int)y, rda_colormix(image, lc, bc, 1 - xfrac));

					imagesetpixel(image, xint, (int)y, color);
				} else {
					bc = imagecolorsforindex(image, imagecolorat(img, xint, (int)y));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, xint, (int)y, rda_colormix(image, lc, bc, xfrac));

					bc = imagecolorsforindex(image, imagecolorat(img, xint + 1, (int)y));
					bc = array(Nest.value(bc,RED).$(), Nest.value(bc,GREEN).$(), Nest.value(bc,BLUE).$());
					imagesetpixel(image, xint + 1, (int)y, rda_colormix(image, lc, bc, 1 - xfrac));
				}
			}
		}
	}
	
}
