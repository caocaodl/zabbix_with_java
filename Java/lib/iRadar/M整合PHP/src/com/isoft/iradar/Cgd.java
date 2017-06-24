package com.isoft.iradar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.imageio.ImageIO;

import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.types.CArray;
import static com.isoft.types.CArray.*;
import com.isoft.types.Mapper.Nest;

public class Cgd {
	public final static int IMG_GIF = 1;
	public final static int IMG_JPG = 2;
	public final static int IMG_JPEG = 2;
	public final static int IMG_PNG = 4;
	public final static int IMG_WBMP = 8;
	public final static int IMG_XPM = 16;
	public final static int IMG_COLOR_TILED = -5;
	public final static int IMG_COLOR_STYLED = -2;
	public final static int IMG_COLOR_BRUSHED = -3;
	public final static int IMG_COLOR_STYLEDBRUSHED = -4;
	public final static int IMG_COLOR_TRANSPARENT = -6;
	public final static int IMG_ARC_ROUNDED = 0;
	public final static int IMG_ARC_PIE = 0;
	public final static int IMG_ARC_CHORD = 1;
	public final static int IMG_ARC_NOFILL = 2;
	public final static int IMG_ARC_EDGED = 4;
	public final static int IMG_GD2_RAW = 1;
	public final static int IMG_GD2_COMPRESSED = 2;
	public final static int IMG_EFFECT_REPLACE = 0;
	public final static int IMG_EFFECT_ALPHABLEND = 1;
	public final static int IMG_EFFECT_NORMAL = 2;
	public final static int IMG_EFFECT_OVERLAY = 3;
	public final static int GD_BUNDLED = 1;
	public final static int IMG_FILTER_NEGATE = 0;
	public final static int IMG_FILTER_GRAYSCALE = 1;
	public final static int IMG_FILTER_BRIGHTNESS = 2;
	public final static int IMG_FILTER_CONTRAST = 3;
	public final static int IMG_FILTER_COLORIZE = 4;
	public final static int IMG_FILTER_EDGEDETECT = 5;
	public final static int IMG_FILTER_GAUSSIAN_BLUR = 7;
	public final static int IMG_FILTER_SELECTIVE_BLUR = 8;
	public final static int IMG_FILTER_EMBOSS = 6;
	public final static int IMG_FILTER_MEAN_REMOVAL = 9;
	public final static int IMG_FILTER_SMOOTH = 10;
	public final static String GD_VERSION = "2.0.35";
	public final static int GD_MAJOR_VERSION = 2;
	public final static int GD_MINOR_VERSION = 0;
	public final static int GD_RELEASE_VERSION = 35;
	public final static String GD_EXTRA_VERSION = "";
	public final static int PNG_NO_FILTER = 0;
	public final static int PNG_FILTER_NONE = 8;
	public final static int PNG_FILTER_SUB = 16;
	public final static int PNG_FILTER_UP = 32;
	public final static int PNG_FILTER_AVG = 64;
	public final static int PNG_FILTER_PAETH = 128;
	public final static int PNG_ALL_FILTERS = 248;
	
	public final static String RED = "red";
	public final static String GREEN = "green";
	public final static String BLUE = "blue";
	
	private final static String FONT_PATH = "/DejaVuSans.ttf";
	
	static Map<String, Font> FONT_CACHE = EasyMap.build();
	private static Font FONT_BASE = null;
	
	/**
     * Return the default RRD4J's default font for the given strength
     * @param type {@link java.awt.Font#BOLD} for a bold fond, any other value return plain style.
     * @param size the size for the new Font
     * @return a new {@link java.awt.Font} instance
     */
    public synchronized static Font getFont(String fontPath, int type, int size) {
    	String cacheKey = fontPath+"_"+type+"_"+size;
    	Font f = FONT_CACHE.get(cacheKey);
    	if(f != null) {
    		return f;
    	}
    	
    	if(FONT_BASE == null) {
    		InputStream fontstream = Cgd.class.getResourceAsStream(FONT_PATH);
            try {
            	FONT_BASE = Font.createFont(Font.TRUETYPE_FONT, fontstream);
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    if (fontstream != null) { 
                        fontstream.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    	}
    	
    	f = FONT_BASE.deriveFont(type == Font.BOLD ? Font.BOLD : Font.PLAIN, size);
    	FONT_CACHE.put(cacheKey, f);
        return f;
    }
	
	/**
	 * 本函数计算并返回一个包围着 TrueType 文本范围的虚拟方框的像素大小。
	 * 
	 * @param size
	 *            像素单位的字体大小。
	 * @param angle
	 *            text 将被度量的角度大小。
	 * @param fontfile
	 *            TrueType 字体文件的文件名（可以是 URL）。根据 PHP 所使用的 GD 库版本，可能尝试搜索那些不是以
	 *            '/' 开头的文件名并加上 '.ttf' 的后缀并搜索库定义的字体路径。
	 * @param text
	 *            要度量的字符串。
	 * 
	 * @return 返回一个含有 8 个单元的数组表示了文本外框的四个角： 0 左下角 X 位置 1 左下角 Y 位置 2 右下角 X 位置
	 *         3 右下角 Y 位置 4 右上角 X 位置 5 右上角 Y 位置 6 左上角 X 位置 7 左上角 Y 位置
	 *         这些点是相对于文本的而和角度无关，因此“左上角”指的是以水平方向看文字时其左上角。 本函数同时需要 GD 库和
	 *         FreeType 库。
	 */
	public static Rectangle2D imagettfbbox(int _size, double _angle, String _fontfile, String _text) {
		_size += 3;
		Font f = getFont(_fontfile, Font.PLAIN, _size);
		Rectangle2D r = f.getStringBounds(_text, new FontRenderContext(f.getTransform(), true, false));
		if(_angle != 0) {
			double ra = -Math.toRadians(_angle);
			double w = Math.abs(Math.cos(ra) * r.getWidth());
			double h = Math.abs(Math.sin(ra) * r.getWidth());
			r.setRect(r.getX(), r.getY(), w, h);
		}
		return r;
	}
	
	
	/**
	 * 使用 TrueType 字体将 指定的 text 写入图像。
	 * 
	 * @param _image
	 *            由图象创建函数(例如imagecreatetruecolor())返回的图象资源。
	 * @param _size
	 *            字体的尺寸。根据 GD 的版本，为像素尺寸（GD1）或点（磅）尺寸（GD2）。
	 * @param _angle
	 *            角度制表示的角度，0 度为从左向右读的文本。更高数值表示逆时针旋转。例如 90 度表示从下向上读的文本。
	 * @param x
	 *            由 x，y 所表示的坐标定义了第一个字符的基本点（大概是字符的左下角）。这和 imagestring() 不同，其
	 *            x，y 定义了第一个字符的左上角。例如 "top left" 为 0, 0。
	 * @param y
	 *            Y 坐标。它设定了字体基线的位置，不是字符的最底端。
	 * @param _color
	 *            颜色索引。使用负的颜色索引值具有关闭防锯齿的效果。见 imagecolorallocate()。
	 * @param _fontfile
	 *            是想要使用的 TrueType 字体的路径。
	 * 
	 *            根据 PHP 所使用的 GD 库的不同，当 fontfile 没有以 / 开头时则 .ttf
	 *            将被加到文件名之后并且会在库定义字体路径中尝试搜索该文件名。
	 * 
	 *            当使用的 GD 库版本低于 2.0.18 时，一个空格字符
	 *            而不是分号将被用来作为不同字体文件的“路径分隔符”。不小心使用了此特性将会导致一条警告信息：Warning:
	 *            Could not find/open
	 *            font。对受影响的版本来说唯一解决方案就是将字体移动到不包含空格的路径中去。
	 * 
	 *            很多情况下字体都放在脚本的同一个目录下。下面的小技巧可以减轻包含的问题。
	 * @param _text
	 *            UTF-8 编码的文本字符串。
	 * 
	 *            可以包含十进制数字化字符表示（形式为：&#8364;）来访问字体中超过位置 127 的字符。UTF-8
	 *            编码的字符串可以直接传递。
	 * 
	 *            命名实体，比如 &copy; 是不支持的。可以考虑使用 html_entity_decode() 来解码命名实体为
	 *            UTF-8 字符。 （自 PHP 5.0.0 开始 html_entity_decode() 开始支持）
	 * 
	 *            如果字符串中使用的某个字符不被字体支持，一个空心矩形将替换该字符。
	 */
	public static void imagettftext (Graphics2D g2d, int _size, double _angle, int x, int y, Color _color, String _fontfile, String _text) {
		_size += 3;
		Font originFont = g2d.getFont();
		Color originColor = g2d.getColor();
		
		g2d.setFont(getFont(_fontfile, Font.PLAIN, _size));
		g2d.setColor(_color);
		
		if(_angle != 0) {
			AffineTransform initialAffineTransform = g2d.getTransform();
			
			Rectangle2D r = imagettfbbox(_size-3, _angle, _fontfile, _text); 
			g2d.translate(x+4, y+r.getWidth());
			g2d.rotate(-Math.toRadians(_angle));
			
			g2d.drawString(_text, 0, 0);
			
			g2d.setTransform(initialAffineTransform);
		}else {
			g2d.drawString(_text, x, y);
		}
		
		g2d.setFont(originFont);
		g2d.setColor(originColor);
	}
	
	/**
	 * imagestring() 用 col 颜色将字符串 s 垂直地画到 image 所代表的图像的 x, y 座标处（图像的左上角为 0,
	 * 0）。如果 font 是 1，2，3，4 或 5，则使用内置字体。
	 * 
	 * 参见 imageloadfont()。
	 */
	public static void imagestringup (Graphics2D g2d, int _font, int x, int y, String _string, Color _color) {
//		Font originFont = g2d.getFont();
		Color originColor = g2d.getColor();
		
//		g2d.setFont(getFont(_fontfile, Font.PLAIN, _size));
		g2d.setColor(_color);
		
		int _angle = -90;
		AffineTransform initialAffineTransform = g2d.getTransform();
		
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(_angle));
		
		g2d.drawString(_string, 0, 0);
		
		g2d.setTransform(initialAffineTransform);
//        g2d.translate(-$x, -$y);
		
//		g2d.setFont(originFont);
		g2d.setColor(originColor);
	}
	
	/**
	 * Create a new palette based image
	 * @link http://www.php.net/manual/en/function.imagecreate.php
	 * @param width int <p>
	 * The image width.
	 * </p>
	 * @param height int <p>
	 * The image height.
	 * </p>
	 * @return resource an image resource identifier on success, false on errors.
	 */
	public static BufferedImage imagecreate (int _width, int _height) {
		BufferedImage img = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB);
		return img;
	}
	
	public static Color imagecolorexactalpha(Graphics2D g2d, int r, int g, int b, int alpha) {
		return new Color(r, g, b, alpha);
	}
	
	/**
	 * imagefilledrectangle() 在 image 图像中画一个用 color 颜色填充了的矩形，其左上角坐标为 x1，y1，右下角坐标为 x2，y2。0, 0 是图像的最左上角。
	 */
	public static void imagefilledrectangle(Graphics2D g2d, int x1, int y1, int x2, int y2, Color color) {
		Color originColor = g2d.getColor();
		
		g2d.setColor(color);
		g2d.fillRect(x1, y1, x2-x1, y2-y1);
		
		g2d.setColor(originColor);
	}
	
	/**
	 * imagerectangle() 用 col 颜色在 image 图像中画一个矩形，其左上角坐标为 x1, y1，右下角坐标为 x2, y2。图像的左上角坐标为 0, 0。
	 */
	public static void imagerectangle(Graphics2D g2d, int i, int j, int k, int l, Color color) {
		Color originColor = g2d.getColor();
		
		g2d.setColor(color);
		int width = k - i;
		int height = l - j;
		g2d.drawRect(i, j, width, height);
		
		g2d.setColor(originColor);
	}
	
	public static byte[] imageOut(BufferedImage img) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		img.flush();
		ImageIO.write(img, "PNG", baos);
		baos.flush();
		return baos.toByteArray();
	}
	
	
	
	
	/**
	 * 在指定的 image 上画一椭圆弧且填充。
	 * 
	 * @param _image
	 *            由图象创建函数(例如imagecreatetruecolor())返回的图象资源。
	 * @param _cx
	 *            中间的 x 坐标。
	 * @param _cy
	 *            中间的 y 坐标。
	 * @param _width
	 *            椭圆弧的宽度。
	 * @param _height
	 *            椭圆弧的高度。
	 * @param _start
	 *            起点角度。
	 * @param _end
	 *            终点角度。 0° is located at the three-o'clock position, and the
	 *            arc is drawn clockwise.
	 * @param _color
	 *            imagecolorallocate() 创建的颜色标识符。
	 * @param _style
	 *            值可以是下列值的按位或（OR）： IMG_ARC_PIE IMG_ARC_CHORD IMG_ARC_NOFILL IMG_ARC_EDGED  
	 *            <p/>IMG_ARC_PIE 和 IMG_ARC_CHORD 是互斥的；
	 *            <p/>IMG_ARC_CHORD 只是用直线连接了起始和结束点，
	 *            <p/>IMG_ARC_PIE 则产生圆形边界。 
	 *            <p/>IMG_ARC_NOFILL 指明弧或弦只有轮廓，不填充。 
	 *            <p/>IMG_ARC_EDGED 指明用直线将起始和结束点与中心点相连，和 IMG_ARC_NOFILL 一起使用是画饼状图轮廓的好方法（而不用填充）。
	 */
	public static void imagefilledarc(Graphics2D g2d, int _cx , int _cy , int _width , int _height , int _start , int _end , Color _color , int _style ) {
		Color originColor = g2d.getColor();
		
		_start = -_start;
		_end = -_end;
		
		g2d.setColor(_color);
		int leftTopX = _cx-_width/2;
		int leftTopY = _cy-_height/2;
		int arcAngle = _end-_start;
		
		if((_style & IMG_ARC_NOFILL) != 0) {
			g2d.drawArc(leftTopX, leftTopY, _width, _height, _start, arcAngle);
		}else {
			g2d.fillArc(leftTopX, leftTopY, _width, _height, _start, arcAngle);
		}
		
		if((_style & IMG_ARC_EDGED) != 0) {
			double raidan = -Math.toRadians(_start);
			double rightTopX = _width/2*Math.cos(raidan)+_cx;
			double rightTopY = _height/2*Math.sin(raidan)+_cy;
			g2d.drawLine(_cx, _cy, (int)rightTopX, (int)rightTopY);
			
			raidan = -Math.toRadians(_end);
			rightTopX = _width/2*Math.cos(raidan)+_cx;
			rightTopY = _height/2*Math.sin(raidan)+_cy;
			g2d.drawLine(_cx, _cy, (int)rightTopX, (int)rightTopY);
		}
		
		g2d.setColor(originColor);
	}
	
	/**
	 * 本函数可以保证对所请求的颜色返回一个颜色索引，要么是确切值要么是所能得到最接近的替代值。
	 * 
	 * @param g2d
	 *            由图象创建函数(例如imagecreatetruecolor())返回的图象资源。
	 * @param _red
	 *            红色成分的值。
	 * @param _green
	 *            绿色成分的值。
	 * @param _blue
	 *            蓝色成分的值。
	 * @param _alpha
	 *            A value between 0 and 127. 0 indicates completely opaque while
	 *            127 indicates completely transparent.
	 * @return 颜色值
	 */
	public static int imagecolorresolvealpha(Graphics2D g2d, int _red, int _green, int _blue, int _alpha) {
		return new Color(_red, _green, _blue, _alpha).getRGB();
	}
	
	/**
	 * 用 color 颜色在图像 image 中从坐标 x1，y1 到 x2，y2（图像左上角为 0, 0）画一条线段。
	 * 
	 * @param g2d
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param _color
	 */
	public static void imageline(Graphics2D g2d, int x1, int y1, int x2, int y2, int _color, boolean hasAlpha) { 
		if(_color != IMG_COLOR_STYLED) {
			g2d.setColor(new Color(_color, hasAlpha));
		}else {
			//注意：java里的dash_phase如果大于dash[0]的话，就会取余，所以需要对com.isoft.iradar.Cgd.imagesetstyle进行修正
			Stroke stroke = g2d.getStroke();
			if(stroke instanceof BasicStroke) {
				BasicStroke bs = (BasicStroke)stroke;
				float dashPhase = bs.getDashPhase();
				if(dashPhase != 0f) {
					float[] dashs = bs.getDashArray();
					float dash0 = dashs[0];
					if(dashPhase >= dash0) {
						if(y1 == y2) {
							x1 += (dashPhase -2 - dashPhase%dash0);
						}else {
							//TODO: Y不相等则为斜线，这里需要做计算
						}
					}
				}
			}
		}
		g2d.drawLine(x1, y1, x2, y2);
		g2d.setStroke(new BasicStroke());
	}
	public static void imageline(Graphics2D g2d, int x1, int y1, int x2, int y2, Integer _color) {
		imageline(g2d, x1, y1, x2, y2, _color, false);
	}
	public static void imageline(Graphics2D g2d, int x1, int y1, int x2, int y2, Color color) {
		g2d.setColor(color);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.setStroke(new BasicStroke());
	}
	
	/**
	 * 在 image 图像中用 color 颜色在 x，y 坐标（图像左上角为 0，0）上画一个点。
	 * 
	 * @param g2d
	 * @param x
	 * @param y
	 * @param _color
	 */
	public static void imagesetpixel(Graphics2D g2d, int x, int y, Color _color) {
		g2d.setColor(_color);
		g2d.drawRect(x, y, 0, 0);
	}
	
	public static void imagesetpixel(Graphics2D g2d, int x, int y, int _color) {
		imagesetpixel(g2d, x, y, new Color(_color, false));
	}
	
	/**
	 * 取得某索引的颜色——改成直接对颜色值进行转化
	 * 
	 * @param _image
	 * @param color
	 * @return
	 */
	public static CArray<Integer> imagecolorsforindex(Graphics2D _image, int color) {
		Color c = new Color(color);
		return map(
				RED, c.getRed(),
				GREEN, c.getGreen(),
				BLUE, c.getBlue()
			);
	}
	
	/**
	 * 返回 image 所指定的图形中指定位置像素的颜色索引值。
	 * 
	 * @param i
	 * @param x
	 * @param y
	 * @return
	 */
	public static int imagecolorat(BufferedImage _image, int x , int y) {
//		SunGraphics2D g2d = (SunGraphics2D)_image;
//		WritableRaster r = (WritableRaster)g2d.getSurfaceData().getRaster(0, 0, x, y);
//		return g2d.getDeviceColorModel().getRGB(r.getDataElements(x, y, null));
		return _image.getRGB(x, y);
	}
	
	/**
	 * 在 image 图像中画一个填充了的多边形。
	 * 
	 * @param _image
	 * @param _points 参数是一个按顺序包含有多边形各顶点的 x 和 y 坐标的数组。
	 * @param _num_points 参数是顶点的总数，必须大于 3。
	 * @param _color
	 * @return
	 */
	public static void imagefilledpolygon (Graphics2D _image , CArray<Integer> _points , int _num_points , Color _color ) {
		int[] xPoints = new int[_num_points];
		int[] yPoints = new int[_num_points];
		
		boolean isSameX = true;
		int preX = -1;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for(int i=0; i<_num_points; i++) {
			xPoints[i] = Nest.value(_points, i*2).asInteger();
			yPoints[i] = Nest.value(_points, i*2+1).asInteger();
			
			if(preX == -1) {
				preX = xPoints[i];
			}else if(isSameX) {
				isSameX = (preX == xPoints[i]);
				preX = xPoints[i];
			}
			
			if(isSameX) {
				minY = Math.min(minY, yPoints[i]);
				maxY = Math.max(maxY, yPoints[i]);
			}
		}
		
		_image.setColor(_color);
		if(isSameX) {
			_image.drawLine(preX, minY, preX, maxY);
		}else {
			_image.fillPolygon(xPoints, yPoints, _num_points);
		}
	}
	
	/**
	 * @see com.isoft.iradar.Cgd.imagefilledpolygon(Graphics2D, CArray<Integer>, int, int)
	 */
	public static void imagefilledpolygon (Graphics2D _image , double[] _points , int _num_points , Color _color ) {
		CArray<Integer> cs = array();
		for(double c: _points) {
			cs.add(Double.valueOf(c).intValue());
		}
		imagefilledpolygon(_image, cs, _num_points, _color);
	}
	
	
	/**
	 * 在图像中创建一个多边形。points 是一个 PHP 数组，包含了多边形的各个顶点坐标，即 points[0] = x0，points[1] = y0，points[2] = x1，points[3] = y1，以此类推。num_points 是顶点的总数。
	 * 
	 * @param _image
	 * @param _points 参数是一个按顺序包含有多边形各顶点的 x 和 y 坐标的数组。
	 * @param _num_points 参数是顶点的总数，必须大于 3。
	 * @param _color
	 * @return
	 */
	public static void imagepolygon(Graphics2D _image , CArray<Integer> _points , int _num_points , int _color ) {
		int[] xPoints = new int[_num_points];
		int[] yPoints = new int[_num_points];
		for(int i=0; i<_num_points; i++) {
			xPoints[i] = Nest.value(_points, i*2).asInteger();
			yPoints[i] = Nest.value(_points, i*2+1).asInteger();
		}
		
		if(_color != IMG_COLOR_STYLED) {
			_image.setColor(new Color(_color));
		}
		
		_image.drawPolygon(xPoints, yPoints, _num_points);
	}
	
	/**
	 * 设定所有画线的函数（例如 imageline() 和 imagepolygon()）在使用特殊颜色 IMG_COLOR_STYLED 或者用 IMG_COLOR_STYLEDBRUSHED 画一行图像时所使用的风格。
	 * 
	 * @param _image
	 * @param _style 像素组成的数组。你可以通过常量 IMG_COLOR_TRANSPARENT 来添加一个透明像素。
	 */
	public static void imagesetstyle(Graphics2D _image , CArray<Object> _style ) {
		List<Float> dashs = new ArrayList<Float>();
		
		int color = IMG_COLOR_TRANSPARENT;
		
		float dash_phase = 0;
		int preC = -1;
		float dashSize = 0;
		for(Object o: _style) {
			int c;
			if(o instanceof Color) {
				Color co = (Color)o;
				c = rgba(co);
			}else {
				c = EasyObject.asInteger(o);
			}
			
			if(preC==-1 && c==IMG_COLOR_TRANSPARENT) {
				dash_phase++;
				continue;
			}
			
			if(c != IMG_COLOR_TRANSPARENT) {
				if(color != c) {
					color = c;
				}
				c = 1;
			}
			
			if(preC != c) {
				if(preC != -1) {
					dashs.add(dashSize);
				}
				dashSize=1;
			}else {
				dashSize++;
			}
			
			preC = c;
		}
		if(dashSize != 0) {
			dashs.add(dashSize);
		}
		if(dash_phase != 0) {
			dashs.add(dash_phase);
		}
		
		float[] dash = new float[dashs.size()];
		for(int i=0,imax=dash.length; i<imax; i++) {
			int fix = (i%2==0)? -1: 1;
			dash[i] = dashs.get(i) + fix;
		}
		
		//注意：java里的dash_phase如果大于dash[0]的话，就会取余，所以需要在com.isoft.iradar.Cgd.imageline里进行修正
		Stroke s = new BasicStroke(0.8f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1f, dash, dash_phase);
		_image.setStroke(s);
		_image.setColor(new Color(color, true));
	}
	
	/**
	 * 返回一个标识符，代表了由给定的 RGB 成分组成的颜色。red，green 和 blue 分别是所需要的颜色的红，绿，蓝成分。这些参数是 0 到 255 的整数或者十六进制的 0x00 到 0xFF。imagecolorallocate() 必须被调用以创建每一种用在 image 所代表的图像中的颜色。
	 * 
	 * @param g2d
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static int imagecolorallocate(Graphics2D g2d, int r, int g, int b) {
		return new Color(r, g, b).getRGB();
	}
	
	/**
	 * 取得图像宽度
	 * 
	 * @param img
	 * @return
	 */
	public static int imagesx(BufferedImage img) {
		return img.getWidth();
	}
	
	/**
	 * 取得图像高度
	 * 
	 * @param img
	 * @return
	 */
	public static int imagesy(BufferedImage img) {
		return img.getHeight();
	}
	
	/**
	 * 将 src_im 图像中坐标从 src_x，src_y 开始，宽度为 src_w，高度为 src_h 的一部分拷贝到 dst_im 图像中坐标为 dst_x 和 dst_y 的位置上。
	 * 
	 * @param _dst_im
	 * @param _src_im
	 * @param _dst_x
	 * @param _dst_y
	 * @param _src_x
	 * @param _src_y
	 * @param _src_w
	 * @param _src_h
	 */
	public static void imagecopy(Graphics2D _dst_im , BufferedImage _src_im , int _dst_x , int _dst_y , int _src_x , int _src_y , int _src_w , int _src_h ) {
		_dst_im.drawImage(_src_im, _dst_x, _dst_y, _dst_x+_src_w, _dst_y+_src_h, _src_x, _src_y, _src_x+_src_w, _src_y+_src_h, null);
	}
	
	/**
	 * 在 image 图像的坐标 x，y（图像左上角为 0, 0）处用 color 颜色执行区域填充（即与 x, y 点颜色相同且相邻的点都会被填充）。
	 * 
	 * @param _im
	 * @param $x
	 * @param $y
	 * @param _nc
	 */
	public static void imagefill(BufferedImage im , int x , int y , int nc ) {
		int wx2 = im.getWidth()-1, wy2 = im.getHeight()-1;
		int oc = im.getRGB(x, y);
		
		/* Do not use the 4 neighbors implementation with
		 * small images
		 */
		if (wx2 < 4) {
			int ix = x, iy = y, c;
			do {
				do {
					c = im.getRGB(ix, iy);
					if (c != oc) {
						return;
					}
					im.setRGB(ix, iy, nc);
				} while(ix++ < (wx2 -1));
				ix = x;
			} while(iy++ < (wy2 -1));
			return;
		}
		
		Stack<ImageFillHelper> stack = new Stack<ImageFillHelper>();
		
		/* required! */
		stack.push(ImageFillHelper.valueOf(y,x,x,1));
		/* seed segment (popped 1st) */
		stack.push(ImageFillHelper.valueOf(y+1, x, x, -1));
		while (!stack.empty()) {
			ImageFillHelper helper = stack.pop(); //FILL_POP(y, x1, x2, dy);
			y = helper.y; 
			int x1 = helper.xl, 
				x2 = helper.xr,
				dy = helper.dy;
			

			for (x=x1; x>=0 && im.getRGB(x, y)==oc; x--) {
				im.setRGB(x, y, nc);
			}
			
			int l;
			if (x>=x1) {
				for (x++; x<=x2 && (im.getRGB(x, y)!=oc); x++) {
					;
				}
				l = x;
				
				if(!(x<=x2)) {
					return;
				}
			}else {
				l = x+1;
				
				/* leak on left? */
				if (l<x1) {
					stack.push(ImageFillHelper.valueOf(y, l, x1-1, -dy)); //FILL_PUSH(y, l, x1-1, -dy);
				}
				x = x1+1;
			}
	        
			do {
				for (; x<=wx2 && im.getRGB(x, y)==oc; x++) {
					im.setRGB(x, y, nc);
				}
				stack.push(ImageFillHelper.valueOf(y, l, x-1, dy)); //FILL_PUSH(y, l, x-1, dy);
				
				/* leak on right? */
				if (x>x2+1) {
					stack.push(ImageFillHelper.valueOf(y, x2+1, x-1, -dy)); //FILL_PUSH(y, x2+1, x-1, -dy);
				}
				for (x++; x<=x2 && (im.getRGB(x, y)!=oc); x++) {
					;
				}
				l = x;
			} while (x<=x2);
		}
	}
	
	
	/**
	 * 画一椭圆并填充到指定的 image。
	 * 
	 * @param _image
	 * @param _cx
	 * @param _cy
	 * @param _width
	 * @param _height
	 * @param _color
	 */
	public static void imagefilledellipse (Graphics2D _image , int _cx , int _cy , int _width , int _height , Color _color ) {
		_image.setColor(_color);
		_image.fillOval(_cx, _cy, _width, _height);
	}
	
	/**
	 * 在指定的坐标上画一个椭圆。
	 * 
	 * @param _image
	 * @param _cx
	 * @param _cy
	 * @param _width
	 * @param _height
	 * @param _color
	 */
	public static void imageellipse ( Graphics2D _image , int _cx , int _cy , int _width , int _height , Color _color ) {
		_image.setColor(_color);
		_image.drawOval(_cx, _cy, _width, _height);
	}
	
	private final static String SINGLE_TEXT_SIZE_TEST_STRING = "W";
    /**
     * 返回指定字体一个字符宽度的像素值。
     * 
     * @param font PHP里指的是对应字体，这里需要改成字体大小
     * @return
     */
    public static int imagefontwidth(int font) {
    	return (int)imagettfbbox(font, 0, FONT_PATH, SINGLE_TEXT_SIZE_TEST_STRING).getWidth();
    }
    
    /**
     * 返回指定字体一个字符高度的像素值。
     * 
     * @param font  PHP里指的对应是字体，这里需要改成字体大小
     * @return
     */
    public static int imagefontheight(int font) {
    	return (int)imagettfbbox(font, 0, FONT_PATH, SINGLE_TEXT_SIZE_TEST_STRING).getHeight();
    }
    
    /**
     * 获取字体的宽度
     * 
     * @param fontSize	字体大小
     * @param text		测量字符串
     * @return
     */
    public static int imagefontwidth(int fontSize, String text) {
    	return (int)imagettfbbox(fontSize, 0, FONT_PATH, text).getWidth();
    }
    
    /**
     * 获取字体的高度
     * 
     * @param fontSize	字体大小
     * @param text		测量字符串
     * @return
     */
    public static int imagefontheight(int fontSize, String text) {
    	return (int)imagettfbbox(fontSize, 0, FONT_PATH, text).getHeight();
    }
    
    /**
     * 获取颜色的透明 rgba 值
     * 
     * @param c
     * @return
     */
    public static int rgba(Color c) {
    	return c.getRGB() + (c.getAlpha()<<24 & 0xff000000);
    }
}

class ImageFillHelper{
	public int y;
	public int xl;
	public int xr;
	public int dy;
	
	public static ImageFillHelper valueOf(int y, int xl, int xr, int dy) {
		ImageFillHelper o = new ImageFillHelper();
		o.y = y;
		o.xl = xl;
		o.xr = xr;
		o.dy = dy;
		return o;
	}
}
