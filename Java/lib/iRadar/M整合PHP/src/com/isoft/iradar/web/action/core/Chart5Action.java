package com.isoft.iradar.web.action.core;

import static com.isoft.iradar.Cgd.imagecolorallocate;
import static com.isoft.iradar.Cgd.imagecreate;
import static com.isoft.iradar.Cgd.imagefilledrectangle;
import static com.isoft.iradar.Cgd.imagerectangle;
import static com.isoft.iradar.Cgd.imagestringup;
import static com.isoft.iradar.Cgd.imagesx;
import static com.isoft.iradar.Cgd.imagesy;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cgd.imagefontwidth;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.microtime;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.inc.Defines.DB_ID;
import static com.isoft.iradar.inc.Defines.O_MAND;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.FuncsUtil.access_deny;
import static com.isoft.iradar.inc.FuncsUtil.rda_date2str;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.GraphsUtil.dashedLine;
import static com.isoft.iradar.inc.GraphsUtil.imageText;
import static com.isoft.iradar.inc.GraphsUtil.imageTextSize;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.api.API;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.iradar.model.params.CServiceGet;
import com.isoft.iradar.model.params.CSlaGet;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class Chart5Action extends RadarBaseAction {
	private Map _service;
	
	
	@Override
	protected void doInitPage() {
		_page("file", "chart5.action");
		_page("type", PAGE_TYPE_IMAGE);
	}

	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"serviceid" , array(T_RDA_INT, O_MAND, P_SYS, DB_ID, null)
		);
		check_fields(getIdentityBean(), fields);
	}

	@Override
	protected void doPermissions(SQLExecutor executor) {
		/*
		 * Permissions
		 */
		CServiceGet params = new CServiceGet();
		params.setOutput(array("serviceid", "name"));
		params.setServiceIds(Nest.array(_REQUEST, "serviceid").asLong());
		_service = API.Service(getIdentityBean(), executor).get(params);
		_service = reset(_service);
		if (empty(_service)) {
			access_deny();
		}
	}
	
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}

	@Override
	protected void doAction(SQLExecutor executor) {
		/*
		 * Display
		 */
		Double _start_time = microtime(true);

		int _sizeX = 900;
		int _sizeY = 300;

		int _shiftX = 12;
		int _shiftYup = 17;
		int _shiftYdown = 25 + 15 * 3;

		BufferedImage img = imagecreate(_sizeX + _shiftX + 61, _sizeY + _shiftYup + _shiftYdown + 10);
		Graphics2D _im = img.createGraphics();
		_im.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		_im.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		_im.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		_im.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//		_im.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		Color _red = new Color(imagecolorallocate(_im, 255, 0, 0));
		Color _darkred = new Color(imagecolorallocate(_im, 150, 0, 0));
		Color _green = new Color(imagecolorallocate(_im, 0, 255, 0));
		Color _darkgreen = new Color(imagecolorallocate(_im, 0, 150, 0));
		Color _blue = new Color(imagecolorallocate(_im, 0, 0, 255));
		Color _darkblue = new Color(imagecolorallocate(_im, 0, 0, 150));
		Color _yellow = new Color(imagecolorallocate(_im, 255, 255, 0));
		Color _darkyellow = new Color(imagecolorallocate(_im, 150, 150, 0));
		Color _cyan = new Color(imagecolorallocate(_im, 0, 255, 255));
		Color _black = new Color(imagecolorallocate(_im, 0, 0, 0));
		Color _gray = new Color(imagecolorallocate(_im, 150, 150, 150));
		Color _white = new Color(imagecolorallocate(_im, 255, 255, 255));
		Color _bg = new Color(imagecolorallocate(_im, 6 + 6 * 16, 7 + 7 * 16, 8 + 8 * 16));

		int x = imagesx(img);
		int y = imagesy(img);

		imagefilledrectangle(_im, 0, 0, x, y, _white);
		imagerectangle(_im, 0, 0, x-1, y-1, _black);

		String d = rda_date2str("Y");
		String _str = _s("%1$s (year %2$s)", Nest.value(_service,"name").$(), d);
		x = imagesx(img) / 2 - imagefontwidth(4) * rda_strlen(_str) / 2;
		imageText(_im, 10, 0, x, 14, _darkred, _str);

		long _now = time(null);
		long _to_time = _now;

		CArray _count_now = array();
		CArray _problem = array();
		CArray _ok=array();

		long _start = mktime(0, 0, 0, 1, 1, asInteger(date("Y")));

		int _wday = asInteger(date("w", _start));
		if (_wday == 0) {
			_wday = 7;
		}
		_start = _start - (_wday - 1) * 24 * 3600;

		int _weeks = (int) asInteger(date("W")) + (!empty(_wday) ? 1 : 0);

		CArray _intervals = array();
		for (int i = 0; i < 52; i++) {
			long _period_start, _period_end;
			if ((_period_start = _start + 7 * 24 * 3600 * i) > time()) {
				break;
			}

			if ((_period_end = _start + 7 * 24 * 3600 * (i + 1)) > time()) {
				_period_end = time();
			}

			_intervals.add( map(
				"from" , _period_start,
				"to" , _period_end
			));
		}
		
		
		CSlaGet params = new CSlaGet();
		params.setServiceIds(Nest.array(_service,"serviceid").asLong());
		params.setIntervals(_intervals);
		Map _sla = API.Service(getIdentityBean(), executor).getSla(params);
		_sla = reset(_sla);

		for(Entry<Object, Map> entry: ((Map<Object, Map>)_sla.get("sla")).entrySet()) {
			Object i = entry.getKey();
			Map _intervalSla = entry.getValue();
			
			_problem.put(i, 100 - Nest.value(_intervalSla,"problemTime").asLong());
			_ok.put(i, Nest.value(_intervalSla,"sla").$());
			_count_now.put(i, 1);
		}

		for (int i = 0; i <= _sizeY; i += _sizeY / 10) {
			dashedLine(_im, _shiftX, i + _shiftYup, _sizeX + _shiftX, i + _shiftYup, _gray);
		}

		for (long i = 0, _period_start = _start; i <= _sizeX; i += _sizeX / 52) {
			dashedLine(_im, (int)i + _shiftX, _shiftYup, (int)i + _shiftX, _sizeY + _shiftYup, _gray);
			imageText(_im, 6, 90, (int)i + _shiftX + 4, _sizeY + _shiftYup + 35, _black, rda_date2str(_("d.M"), _period_start));
			_period_start += 7 * 24 * 3600;
		}

		int _maxY = max(max(_problem), 100).intValue();
		int _minY = 0;

		int _maxX = _sizeX;
		int _minX = 0;

		for (int i = 1; i <= _weeks; i++) {
			if (!isset(_ok, i-1)) {
				continue;
			}
			double x2 = ((double)_sizeX / 52) * (i - 1 - _minX) * _sizeX / (_maxX - _minX);
			double y2 = _sizeY * (Nest.value(_ok, i - 1).asDouble() - _minY) / (_maxY - _minY);

			double _maxSizeY = _sizeY;
			if (i == _weeks) {
				_maxSizeY = _sizeY * (asInteger(date("w")) / 7);
				y2 = _maxSizeY * (Nest.value(_ok, i - 1).asDouble() - _minY) / (_maxY - _minY);
			}

			imagefilledrectangle(
				_im,
				(int)(x2 + _shiftX), (int)(_shiftYup + _sizeY - y2),
				(int)(x2 + _shiftX + 8), (int)(_shiftYup + _sizeY),
				new Color(imagecolorallocate(_im, 120, 235, 120))
			);
//			imagerectangle(
//				_im,
//				(int)(x2 + _shiftX), (int)(_shiftYup + _sizeY - y2),
//				(int)(x2 + _shiftX + 8), (int)(_shiftYup + _sizeY),
//				_black
//			);
//			imagefilledrectangle(
//				_im,
//				(int)(x2 + _shiftX), (int)(_shiftYup + _sizeY - _maxSizeY),
//				(int)(x2 + _shiftX + 8), (int)(_shiftYup + _sizeY - y2),
//				new Color(imagecolorallocate(_im, 235, 120, 120))
//			);
//			imagerectangle(
//				_im,
//				(int)(x2 + _shiftX), (int)(_shiftYup + _sizeY - _maxSizeY),
//				(int)(x2 + _shiftX + 8), (int)(_shiftYup + _sizeY - y2),
//				_black
//			);
		}

		for (int i = 0; i <= _sizeY; i += _sizeY / 10) {
			imageText(_im, 7, 0, _sizeX + 5 + _shiftX, _sizeY - i - 4 + _shiftYup + 8, _darkred, (i * (_maxY - _minY) / _sizeY + _minY)+"%");
		}

		imagefilledrectangle(_im, _shiftX, _sizeY + _shiftYup + 34 + 15 * 1, _shiftX + 5, _sizeY + _shiftYup + 30 + 9 + 15 * 1, new Color(imagecolorallocate(_im, 120, 235, 120)));
		imagerectangle(_im, _shiftX, _sizeY + _shiftYup + 34 + 15 * 1, _shiftX + 5, _sizeY + _shiftYup + 30 + 9 + 15 * 1, _black);
		imageText(_im, 8, 0, _shiftX + 9, _sizeY + _shiftYup + 15 * 1 + 41, _black, _("OK")+" (%)");

		imagefilledrectangle(_im, _shiftX, _sizeY + _shiftYup + 34 + 15 * 2, _shiftX + 5, _sizeY + _shiftYup + 30 + 9 + 15 * 2, _darkred);
		imagerectangle(_im, _shiftX, _sizeY + _shiftYup + 34 + 15 * 2, _shiftX + 5, _sizeY + _shiftYup + 30 + 9 + 15 * 2, _black);
		imageText(_im, 8, 0, _shiftX + 9, _sizeY + _shiftYup + 15 * 2 + 41, _black, _("PROBLEM")+" (%)");
		imagestringup(_im, 0, imagesx(img) - 10, imagesy(img) - 50, "http://www.i-soft.com.cn", _gray);

		_str = sprintf("%.2f", (Double)microtime(true) - _start_time);
		_str = _s("Generated in %s sec", _str);
		CArray _strSize = imageTextSize(6, 0, _str);
		imageText(_im, 6, 0, imagesx(img) - Nest.value(_strSize,"width").asInteger() - 5, imagesy(img) - 5, _gray, _str);

		FuncsUtil.imageOut(img);;
	}
	
}
