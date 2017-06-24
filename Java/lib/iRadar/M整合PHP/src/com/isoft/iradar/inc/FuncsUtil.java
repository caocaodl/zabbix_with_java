package com.isoft.iradar.inc;

import static com.isoft.Feature.originalStyle;
import static com.isoft.iradar.Cgd.imagefontheight;
import static com.isoft.iradar.Cgd.imagefontwidth;
import static com.isoft.iradar.Cphp.$_REQUEST;
import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._n;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp._x;
import static com.isoft.iradar.Cphp.array_count_values;
import static com.isoft.iradar.Cphp.array_diff;
import static com.isoft.iradar.Cphp.array_keys;
import static com.isoft.iradar.Cphp.array_merge;
import static com.isoft.iradar.Cphp.array_pop;
import static com.isoft.iradar.Cphp.array_push;
import static com.isoft.iradar.Cphp.array_reverse;
import static com.isoft.iradar.Cphp.array_slice;
import static com.isoft.iradar.Cphp.array_unshift;
import static com.isoft.iradar.Cphp.array_values;
import static com.isoft.iradar.Cphp.arsort;
import static com.isoft.iradar.Cphp.basename;
import static com.isoft.iradar.Cphp.bcadd;
import static com.isoft.iradar.Cphp.bccomp;
import static com.isoft.iradar.Cphp.bcdiv;
import static com.isoft.iradar.Cphp.bcmul;
import static com.isoft.iradar.Cphp.bcpow;
import static com.isoft.iradar.Cphp.ceil;
import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.ctype_digit;
import static com.isoft.iradar.Cphp.date;
import static com.isoft.iradar.Cphp.dechex;
import static com.isoft.iradar.Cphp.defined;
import static com.isoft.iradar.Cphp.echo;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.explode;
import static com.isoft.iradar.Cphp.floor;
import static com.isoft.iradar.Cphp.hexdec;
import static com.isoft.iradar.Cphp.htmlspecialchars;
import static com.isoft.iradar.Cphp.implode;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.is_array;
import static com.isoft.iradar.Cphp.is_callable;
import static com.isoft.iradar.Cphp.is_null;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.Cphp.is_object;
import static com.isoft.iradar.Cphp.is_string;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.max;
import static com.isoft.iradar.Cphp.mb_strlen;
import static com.isoft.iradar.Cphp.mb_strtolower;
import static com.isoft.iradar.Cphp.mb_strtoupper;
import static com.isoft.iradar.Cphp.mktime;
import static com.isoft.iradar.Cphp.natcasesort;
import static com.isoft.iradar.Cphp.preg_match;
import static com.isoft.iradar.Cphp.preg_replace;
import static com.isoft.iradar.Cphp.reset;
import static com.isoft.iradar.Cphp.round;
import static com.isoft.iradar.Cphp.rtrim;
import static com.isoft.iradar.Cphp.sprintf;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.Cphp.strcmp;
import static com.isoft.iradar.Cphp.stripslashes;
import static com.isoft.iradar.Cphp.stristr;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.iradar.Cphp.strpos;
import static com.isoft.iradar.Cphp.strstr;
import static com.isoft.iradar.Cphp.strtolower;
import static com.isoft.iradar.Cphp.strtotime;
import static com.isoft.iradar.Cphp.strtoupper;
import static com.isoft.iradar.Cphp.substr;
import static com.isoft.iradar.Cphp.time;
import static com.isoft.iradar.Cphp.trim;
import static com.isoft.iradar.Cphp.uasort;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.core.utils.EasyObject.asCArray;
import static com.isoft.iradar.core.utils.EasyObject.asDouble;
import static com.isoft.iradar.core.utils.EasyObject.asFloat;
import static com.isoft.iradar.core.utils.EasyObject.asInteger;
import static com.isoft.iradar.core.utils.EasyObject.asString;
import static com.isoft.iradar.inc.DBUtil.DBfetch;
import static com.isoft.iradar.inc.DBUtil.DBselect;
import static com.isoft.iradar.inc.Defines.ACCESS_DENY_OBJECT;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_NOT_MONITORED;
import static com.isoft.iradar.inc.Defines.HOST_STATUS_TEMPLATE;
import static com.isoft.iradar.inc.Defines.IMAGE_FORMAT_PNG;
import static com.isoft.iradar.inc.Defines.ITEM_CONVERT_NO_UNITS;
import static com.isoft.iradar.inc.Defines.ITEM_CONVERT_WITH_UNITS;
import static com.isoft.iradar.inc.Defines.ITEM_STATE_NORMAL;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_ACTIVE;
import static com.isoft.iradar.inc.Defines.ITEM_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.ITEM_TYPE_HTTPTEST;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_FLOAT;
import static com.isoft.iradar.inc.Defines.ITEM_VALUE_TYPE_UINT64;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_CSS;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_HTML_BLOCK;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_IMAGE;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JS;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JSON;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_JSON_RPC;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_TEXT;
import static com.isoft.iradar.inc.Defines.PAGE_TYPE_XML;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_INT;
import static com.isoft.iradar.inc.Defines.PROFILE_TYPE_STR;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_NORMAL;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_PROTOTYPE;
import static com.isoft.iradar.inc.Defines.RDA_MAX_DATE;
import static com.isoft.iradar.inc.Defines.RDA_PRECISION_10;
import static com.isoft.iradar.inc.Defines.RDA_SESSION_ACTIVE;
import static com.isoft.iradar.inc.Defines.RDA_SOCKET_TIMEOUT;
import static com.isoft.iradar.inc.Defines.RDA_SORT_DOWN;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_LOWER_LIMIT;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_THRESHOLD;
import static com.isoft.iradar.inc.Defines.RDA_UNITS_ROUNDOFF_UPPER_LIMIT;
import static com.isoft.iradar.inc.Defines.RDA_USER_ONLINE_TIME;
import static com.isoft.iradar.inc.Defines.SEC_PER_DAY;
import static com.isoft.iradar.inc.Defines.SEC_PER_HOUR;
import static com.isoft.iradar.inc.Defines.SEC_PER_MIN;
import static com.isoft.iradar.inc.Defines.SEC_PER_MONTH;
import static com.isoft.iradar.inc.Defines.SEC_PER_YEAR;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_DISABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_STATUS_ENABLED;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_FALSE;
import static com.isoft.iradar.inc.Defines.TRIGGER_VALUE_TRUE;
import static com.isoft.iradar.inc.Defines.USER_TYPE_IRADAR_ADMIN;
import static com.isoft.iradar.inc.Defines.USER_TYPE_SUPER_ADMIN;
import static com.isoft.iradar.inc.JsUtil.insert_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.JsUtil.rda_jsvalue;
import static com.isoft.iradar.utils.StringUtils.trimTrailingCharacter;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.g;
import com.isoft.iradar.exception.ExitException;
import com.isoft.iradar.helpers.CArrayHelper;
import com.isoft.iradar.managers.CProfile;
import com.isoft.iradar.model.CMessage;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.model.sql.SqlBuilder;
import com.isoft.iradar.operator.COperator.CMapOperator;
import com.isoft.iradar.server.IRadarServer;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CJSON;
import com.isoft.iradar.tags.CLink;
import com.isoft.iradar.tags.CList;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTag;
import com.isoft.iradar.tags.Curl;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.lang.CodeTodo;
import com.isoft.types.CArray;
import com.isoft.types.IList;
import com.isoft.types.Mapper.Nest;
import com.isoft.types.Mapper.TObj;
import com.isoft.types.Mapper.TObject;

public class FuncsUtil {
	
	private FuncsUtil() {
	}
	
	/**
	 * Verify that function exists and can be called as a function.
	 *
	 * @param array		_names
	 *
	 * @return bool
	 */
	public static boolean rda_is_callable(CArray<String> names) {
		for(String name : names) {
			if (!is_callable(name)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean hasRequest(String name) {
		return isset(RadarContext._REQUEST(), name);
	}
	
	public static String get_request(String name) {
		return Nest.as(get_request(name, null)).asString();
	}
	
	public static Long get_request_asLong(String name) {
		return Nest.as(get_request(name, null)).asLong(true);
	}
	
	public static <T> T get_request(String name, T def) {
		if (hasRequest(name)) {
			if (def != null) {
				return (T) ConvertUtils.convert(RadarContext._REQUEST().get(name), def.getClass());
			} else {
				return (T)RadarContext._REQUEST().get(name);
			}
		} else {
			return def;
		}
	}
	
	public static String get_request_real(String name) {
		return get_request_real(name, null);
	}
	
	public static String get_request_real(String name, Object def) {
		String content = asString(get_request(name, def));
		content = content.replaceAll("&amp;","&");
		content = content.replaceAll("&lt;","<");
		content = content.replaceAll("&gt;",">");
		content = content.replaceAll("&#039;","'");
		content = content.replaceAll("&quot;","\"");
		return content;
	}
	
	public static CArray<String> get_requests(String name) {
		return get_requests(name, array());
	}
	
	public static CArray<String> get_requests(String name, CArray<String> def) {
		return get_request(name, def);
	}
	
	public static String get_cookie(String name) {
		return get_cookie(name, null);
	}
	
	public static String get_cookie(String name, String default_value) {
		Cookie[] cookies = RadarContext.request().getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return default_value;
	}
	
	public static void rda_setcookie(String name, String value, int time) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(time);
		RadarContext.response().addCookie(cookie);
	}
	
	/************* DATE *************/
	@CodeConfirmed("benne.2.2.6")
	public static String getMonthCaption(int num) {
		switch (num) {
			case 1: return _("January");
			case 2: return _("February");
			case 3: return _("March");
			case 4: return _("April");
			case 5: return _("May");
			case 6: return _("June");
			case 7: return _("July");
			case 8: return _("August");
			case 9: return _("September");
			case 10: return _("October");
			case 11: return _("November");
			case 12: return _("December");
		}
		return _s("[Wrong value for month: \"%s\" ]", num);
	}
	
	@CodeConfirmed("benne.2.2.6")
	public static String getDayOfWeekCaption(int num) {
		switch (num) {
			case 1: return _("Monday");
			case 2: return _("Tuesday");
			case 3: return _("Wednesday");
			case 4: return _("Thursday");
			case 5: return _("Friday");
			case 6: return _("Saturday");
			case 0:
			case 7: return _("Sunday");
		}
		return _s("[Wrong value for day: \"%s\" ]", num);
	}
	
	@CodeConfirmed("benne.2.2.6")
	public static String dowHrMinToStr(long value) {
		return dowHrMinToStr(value, false);
	}
	
	// Convert seconds (0..SEC_PER_WEEK) to string representation. For example, 212400 -> "Tuesday 11:00"
	@CodeConfirmed("benne.2.2.6")
	public static String dowHrMinToStr(long value, boolean display24Hours) {
		long dow = value - value % SEC_PER_DAY;
		long hr = value - dow;
		hr -= hr % SEC_PER_HOUR;
		long min = value - dow - hr;
		min -= min % SEC_PER_MIN;

		dow /= SEC_PER_DAY;
		hr /= SEC_PER_HOUR;
		min /= SEC_PER_MIN;

		if (display24Hours && hr == 0 && min == 0) {
			dow--;
			hr = 24;
		}

		return sprintf("%s %02d:%02d", getDayOfWeekCaption((int)dow), hr, min);
	}
	
	// Convert Day Of Week, Hours and Minutes to seconds representation. For example, 2 11:00 -> 212400. false if error occurred
	@CodeConfirmed("benne.2.2.6")
	public static Long dowHrMinToSec(Long dow, Long hr, Long min) {
		if (rda_empty(dow) || rda_empty(hr) || rda_empty(min)) {
			return null;
		}

		if (dow == 7) {
			dow = 0L;
		}

		if (dow < 0 || dow > 6) {
			return null;
		}

		if (hr < 0 || hr > 24) {
			return null;
		}

		if (min < 0 || min > 59) {
			return null;
		}

		return dow * SEC_PER_DAY + hr * SEC_PER_HOUR + min * SEC_PER_MIN;
	}
	
	@CodeConfirmed("benne.2.2.6")
	public static String rda_date2str(String _format) {
		return rda_date2str(_format, null);
	}
	
	// Convert timestamp to string representation. Return "Never" if 0.
	@CodeConfirmed("benne.2.2.6")
	public static String rda_date2str(String format, Long value) {
		CArray<String> weekdaynames = g.weekdaynames.$();
		CArray<String> weekdaynameslong = g.weekdaynameslong.$();
		CArray<String> months = g.months.$();
		CArray<String> monthslong = g.monthslong.$();

		String prefix = "";

		if (value == null) {
			value = time();
		} else if (value > RDA_MAX_DATE) {
			prefix = "> ";
			value = (long)RDA_MAX_DATE;
		} else if (value==0) {
			return _("Never");
		}

		if (!isArray(weekdaynames)) {
			weekdaynames = map(
				0 , _("Sun"),
				1 , _("Mon"),
				2 , _("Tue"),
				3 , _("Wed"),
				4 , _("Thu"),
				5 , _("Fri"),
				6 , _("Sat")
			);
			g.weekdaynames.$(weekdaynames);
		}

		if (!isArray(weekdaynameslong)) {
			weekdaynameslong = map(
				0 , _("Sunday"),
				1 , _("Monday"),
				2 , _("Tuesday"),
				3 , _("Wednesday"),
				4 , _("Thursday"),
				5 , _("Friday"),
				6 , _("Saturday")
			);
			g.weekdaynameslong.$(weekdaynameslong);
		}

		if (!isArray(months)) {
			months = map(
				1 , _("Jan"),
				2 , _("Feb"),
				3 , _("Mar"),
				4 , _("Apr"),
				5 , _x("May", "May short"),
				6 , _("Jun"),
				7 , _("Jul"),
				8 , _("Aug"),
				9 , _("Sep"),
				10 , _("Oct"),
				11 , _("Nov"),
				12 , _("Dec")
			);
			g.months.$(months);
		}

		if (!isArray(monthslong)) {
			monthslong = map(
				1 , _("January"),
				2 , _("February"),
				3 , _("March"),
				4 , _("April"),
				5 , _("May"),
				6 , _("June"),
				7 , _("July"),
				8 , _("August"),
				9 , _("September"),
				10 , _("October"),
				11 , _("November"),
				12 , _("December")
			);
			g.monthslong.$(monthslong);
		}

		CArray<String> rplcs = map(
			"l" , weekdaynameslong.get(date("w", value)),
			"F" , monthslong.get(date("n", value)),
			"D" , weekdaynames.get(date("w", value)),
			"M" , months.get(date("n", value))
		);

		StringBuilder output = new StringBuilder();
		String part = "";
		int length = rda_strlen(format);

		String pch = null;
		String ch = null;
		for (int i = 0; i < length; i++) {
			pch = (i > 0) ? rda_substr(format, i - 1, 1) : "";
			ch = rda_substr(format, i, 1);

			if ( !"\\".equals(pch) && isset(rplcs,ch)) {
				output.append((rda_strlen(part)>0 ? date(part, value) : "")+rplcs.get(ch));
				part = "";
			} else {
				part+=ch;
			}
		}

		output.append((rda_strlen(part) > 0) ? date(part, value) : "");
		return prefix+output.toString();
	}
	
	// calculate and convert timestamp to string representation
	@CodeConfirmed("benne.2.2.6")
	public static String rda_date2age(long startDate) {
		return rda_date2age(startDate, 0);
	}
	
	// calculate and convert timestamp to string representation
	@CodeConfirmed("benne.2.2.6")
	public static String rda_date2age(long startDate, long endDate) {
		return rda_date2age(startDate, endDate, false);
	}
	
	// calculate and convert timestamp to string representation
	@CodeConfirmed("benne.2.2.6")
	public static String rda_date2age(long startDate, long endDate, boolean utime) {
		if (!utime) {
			startDate = Nest.as(date("U", startDate)).asLong();
			endDate = endDate>0 ? Nest.as(date("U", endDate)).asLong() : time();
		}

		return convertUnitsS((double)Math.abs(endDate - startDate));
	}
	
	private static Pattern DATE6 = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})");
	private static Pattern DATE5 = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})");
	@CodeConfirmed("benne.2.2.6")
	public static long rdaDateToTime(String strdate) {
		if(strdate == null){
			return time();
		} else {
			int year, month, date, hours, minutes, seconds;		
			Matcher matcher = DATE6.matcher(strdate);
			if (matcher.find()) {
				year = Integer.valueOf(matcher.group(1));
				month = Integer.valueOf(matcher.group(2));
				date = Integer.valueOf(matcher.group(3));
				hours = Integer.valueOf(matcher.group(4));
				minutes = Integer.valueOf(matcher.group(5));
				seconds = Integer.valueOf(matcher.group(6));
				return mktime(hours, minutes, seconds, month, date, year);
			} else {
				matcher = DATE5.matcher(strdate);
				if(matcher.find()) {
					year = Integer.valueOf(matcher.group(1));
					month = Integer.valueOf(matcher.group(2));
					date = Integer.valueOf(matcher.group(3));
					hours = Integer.valueOf(matcher.group(4));
					minutes = Integer.valueOf(matcher.group(5));
					seconds = 0;
					return mktime(hours, minutes, seconds, month, date, year);
				}else {
					return (!empty(strdate)&& is_numeric(strdate))? Nest.as(strdate).asLong(): time();
				}
			}
		}
	}
	
	/**
	 * Correcting adding one unix timestamp to another.
	 *
	 * @param int		_sec
	 * @param mixed		_unixtime	Can accept values:
	 *									1) int - unix timestamp,
	 *									2) string - date in YmdHis or YmdHi formats,
	 *									3) null - current unixtime stamp will be used
	 *
	 * @return int
	 */
	@CodeTodo
	public static long rdaAddSecondsToUnixtime(long _sec, Object _unixtime) {
		return strtotime("+"+_sec+" seconds", rdaDateToTime(Nest.as(_unixtime).asString()));
	}
	
	/*************** CONVERTING ******************/
	public static String rgb2hex(CArray<Integer> color) {
		CArray<String> hex = array(
			dechex(color.get(0)),
			dechex(color.get(1)),
			dechex(color.get(2))
		);
		for (Entry<Object, String> e : hex.entrySet()) {
		    Object id = e.getKey();
		    String value = e.getValue();
			if (rda_strlen(value) != 2) {
				hex.put(id,"0"+value);
			}
		}
		return hex.get(0)+hex.get(1)+hex.get(2);
	}
	
	public static int[] hex2rgb(String _color) {
		if (_color.charAt(0) == '#') {
			_color = substr(_color, 1);
		}

		String r, g, b;
		if (rda_strlen(_color) == 6) {
			r = _color.substring(0, 2);
			g = _color.substring(2, 4);
			b = _color.substring(4);
		} else if (rda_strlen(_color) == 3) {
			r = "" + _color.charAt(0) + _color.charAt(0);
			g = "" + _color.charAt(1) + _color.charAt(1);
			b = "" + _color.charAt(2) + _color.charAt(2);
		}
		else {
			return null;
		}

		return new int[] {hexdec(r), hexdec(g), hexdec(b)};
	}
	
	
	public static void $CODE$SEPARATOR(){}
	/*********************benne************************/
	
	public static String formatDomId(String value){
		return value.replace("[", "_").replace("]", "");
	}
	
	public static Object toArray(Object values) {
		if(values == null){
			return array();
		}
		return CArray.valueOf(values).toArray();
	}
	
	public static CArray toCArray(Object values) {
		return CArray.valueOf(values);
	}
	
	/**
	 * Converts the given value to a numeric array:
	 * - a scalar value will be converted to an array and added as the only element;
	 * - an array with first element key containing only numeric characters will be converted to plain zero-based numeric array.
	 * This is used for reseting nonsequential numeric arrays;
	 * - an associative array will be returned in an array as the only element, except if first element key contains only numeric characters.
	 *
	 * @param mixed _value
	 *
	 * @return array
	 */
	public static CArray rda_toArray(Object value) {
		if (value == null) {
			return null;
		}
	
		CArray result = array();
		if (isArray(value)) {
			CArray cvalue = CArray.valueOf(value);
			if (empty(cvalue)) {
				return cvalue;
			}
			
			Object key = cvalue.keySet().iterator().next();
			if (rda_ctype_digit(key)) {
				result = array_values(cvalue);
			} else if (!empty(value)) {
				result = array(value);
			}
		} else {
			result = CArray.valueOf(value);
		}
	
		return result;
	}
	
	public static CArray rda_toHash(Object value) {
		return rda_toHash(value, null);
	}
		
	// object or array of objects to hash
	public static CArray rda_toHash(Object value, Object field) {
		if (is_null(value)) {
			return null;
		}
		CArray result = array();

		if (!isArray(value)) {
			result = map(value, value);
		} else {
			CArray cvalue = CArray.valueOf(value);
			Object valueF = cvalue.get(field);
			if (isset(valueF)) {
				result.put(valueF, value);
			} else {
				for (Object val : cvalue) {
					if (!isArray(val)) {
						result.put(val, val);
					} else {
						CArray cval = CArray.valueOf(val);
						Object valF = cval.get(field);
						if (isset(valF)) {
							result.put(valF, val);
						}
					}
				}
			}
		}
		return result;
	}
	
	public static void rda_array_push(CArray array, CArray add) {
		for (Object o : array.entrySet()) {
			Object okey = ((Entry) o).getKey();
			//Object ovalue = ((Entry) o).getValue();

			for (Object e : add.entrySet()) {
				Object newKey = ((Entry) e).getKey();
				Object newValue = ((Entry) e).getValue();
				array.put(okey, newKey, newValue);
			}
		}
	}
	
	/**
	 * preserve keys
	 * @param args
	 * @return
	 */
	@CodeConfirmed("benne")
	public static <T> CArray<T> rda_array_merge(CArray<T>...args) {
		CArray<T> result = new CArray();
		for (CArray<T> arg : args) {
			if (!isArray(arg)) {
				return null;
			}
			for(Entry<Object, T> e: arg.entrySet()) {
				result.put(e.getKey(), e.getValue());
			}
		}
		return result;
	}
	
	public static Map rda_array_merge(Map... args) {
		Map result = new CArray();
		for (Map arg : args) {
			if (!isArray(arg)) {
				return null;
			}
			for (Entry e : ((Map<Object, Object>) arg).entrySet()) {
				result.put(e.getKey(), e.getValue());
			}
		}
		return result;
	}
	
	/**
	 * Look for two arrays field value and create 3 array lists, one with arrays where field value exists only in first array
	 * second with arrays where field values are only in second array and both where fiel values are in both arrays.
	 *
	 * @param array  _primary
	 * @param array  _secondary
	 * @param string _field field that is searched in arrays
	 *
	 * @return array
	 */
	public static <T> CArray<CArray<T>> rda_array_diff(CArray<? extends Map> _primary, CArray<? extends Map> _secondary, Object _field) {
		CArray _fields1 = rda_objectValues(_primary, _field);
		CArray _fields2 = rda_objectValues(_secondary, _field);
	
		CArray _first = array_diff(_fields1, _fields2);
		_first = rda_toHash(_first);
	
		CArray _second = array_diff(_fields2, _fields1);
		_second = rda_toHash(_second);
	
		
		CArray first = array();
		CArray second = array();
		CArray both = array();
		
		CArray<CArray<T>> _result = map(
			"first", first,
			"second", second,
			"both", both
		);
	
		for(Map _array: _primary) {
			Object v = _array.get(_field);
			if (!isset(v)) {
				first.add(_array);
			} else if (isset(_first.get(v))) {
				first.add(_array);
			} else {
				both.put(v, _array);
			}
		}
	
		for(Map _array: _secondary) {
			Object v = _array.get(_field);
			if (!isset(v)) {
				second.add(_array);
			}else if(isset(_second.get(v))) {
				second.add(_array);
			}
		}
	
		return _result;
	}
	
	// value OR object OR array of objects TO an array
	public static CArray rda_objectValues(Object _value, Object _field) {
		if (is_null(_value)) {
			return null;
		}
		CArray _result = array();

		if (!isArray(_value)) {
			_result = array(_value);
		} else {
			CArray value = CArray.valueOf(_value);
			if (isset(value.get(_field))) {
				_result = array(value.get(_field));
			} else {
				for(Object _val: value) {
					if (!isArray(_val)) {
						_result.add(_val);
					} else {
						CArray val = CArray.valueOf(_val);
						if (isset(val.get(_field))) {
							_result.add(val.get(_field));
						}
					}
				}
			}
		} 

		return _result;
	}
	
	public static boolean rda_ctype_digit(Object $x) {
		String t = String.valueOf($x);
		return (
				(t.length() == 1) || 
				(t.length() > 1 && t.charAt(0) != '0')
			) && NumberUtils.isDigits(t);
	}
	
	public static <T> CArray<T> rda_cleanHashes(CArray<T> _value) {
		if (isArray(_value)) {
			// reset() is needed to move internal array pointer to the beginning of the array
//			php.reset(_value);
			Iterator iterator = _value.keySet().iterator();
			if(iterator.hasNext()) {
				Object key = iterator.next();
				if (rda_ctype_digit(key)) {
					CArray<T> tmp = array_values(_value);
					_value.copy(tmp); 
				}
			}
		}

		return _value;
	}
	
	public static int rda_strpos(String haystack, String needle) {
		return rda_strpos(haystack, needle, 0);
	}
	
	public static int rda_strpos(String haystack, String needle, int offset) {
//		if (defined("RDA_MBSTRINGS_ENABLED")) {
//			return php.mb_strpos(haystack, needle, offset);
//		}
//		else {
			return strpos(haystack, needle);
//		}
	}
	
	public static String[] objectValues(Iterable<Map> datas, String field) {
		if (datas == null || !datas.iterator().hasNext()) {
			return null;
		}
		List<String> list = new ArrayList();
		for (Map data : datas) {
			if(data.containsKey(field)){
				list.add(String.valueOf(data.get(field)));
			}
		}
		if(list.isEmpty()){
			return null;
		} else {
			return list.toArray(new String[0]);
		}
	}
	
	// creates header col for sorting in table header
	public static CCol make_sorting_header(Object obj, String tabfield) {
		return make_sorting_header(obj, tabfield, "");
	}
	
	public static CCol make_sorting_header(Object obj, String tabfield, String url) {
		RadarContext ctx = RadarContext.getContext();
		Map<String, Object> page = RadarContext.page();
		String sort = ctx.getRequest().getParameter("sort");
		String order = ctx.getRequest().getParameter("sortorder");
		String sortorder = tabfield.equals(sort) && RDA_SORT_UP.equals(order)?RDA_SORT_DOWN : RDA_SORT_UP;

		Curl link = new Curl(url);
		if (empty(url)) {
			link.formatGetArguments();
		}
		link.setArgument("sort", tabfield);
		link.setArgument("sortorder", sortorder);

		url = link.getUrl();
		
		String script;
		int type = (Integer)page.get("type");
		if (type!= PAGE_TYPE_HTML && defined("RDA_PAGE_MAIN_HAT")) {
			script = "javascript: return updater.onetime_update('hat_latest', '"+url+"');";
		}
		else {
			script = "javascript: redirect(\""+url+"\");";
		}

		obj = FuncsUtil.toArray(obj);
		CSpan cont = new CSpan();
		
		int len = Array.getLength(obj);
		for (int i = 0; i < len; i++) {
			Object el = Array.get(obj, i);
			if (is_object(el) || SPACE.equals(el)) {
				cont.addItem(el);
			} else {
				cont.addItem(new CSpan(el, "underline"));
			}
		}
		cont.addItem(SPACE);
		CSpan img = null;
		if (isset(sort) && sort.equals(tabfield)) {
			if (RDA_SORT_UP.equals(sortorder)) {
				img = new CSpan(SPACE, "icon_sortdown");
			} else {
				img = new CSpan(SPACE, "icon_sortup");
			}
		}
		CCol col = new CCol(new CSpan[]{cont, img}, "nowrap hover_grey");
		col.setAttribute("onclick", script);

		return col;
	}
	
	/**
	 * 返回array中与keys相交的集合
	 * 
	 * @param keys
	 * @param array
	 * @return
	 */
	public static CArray rda_array_mintersect(CArray keys, Map array) {
		CArray result = array();

		for (Object field : keys) {
			if (isArray(field)) {
				for(Object sub_field: CArray.valueOf(field)) {
					if(isset(array,sub_field)) {
						result.put(sub_field, array.get(sub_field));
						break;
					}
				}
			} else if (isset(array.get(field))) {
				result.put(field, array.get(field));
			}
		}

		return result;
	}
	
	public static boolean rda_empty(Object _value) {
		if (is_null(_value)) {
			return true;
		}
		if (isArray(_value) && empty(_value)) {
			return true;
		}
		if (is_string(_value) && "".equals(_value)) {
			return true;
		}

		return false;
	}
	
	public static boolean rda_is_int(Object var) {
		if (var instanceof Integer || var instanceof Long || var instanceof BigInteger || var instanceof Short || var instanceof Byte) {
			return true;
		}
		if (var instanceof String) {
			if (NumberUtils.isDigits((String)var) || strcmp(Nest.as(var).asInteger(true).toString(), (String)var) == 0) {
				return true;
			}
		} else {
			if (!empty(var) && NumberUtils.isDigits(Nest.as(var).asString())) {
				return true;
			}
		}
		return preg_match("^\\-?\\d{1,20}+$", Nest.as(var).asString())>0;
	}
	
	public static CArray asort_by_key(CArray<Map> _array, String _key) {
		if (!is_array(_array)) {
			error(_("Incorrect type of asort_by_key."));
			return array();
		}
		_key = htmlspecialchars(_key);
		
		final String key = _key;
		uasort(_array, new Comparator<Map>() {
			@Override
			public int compare(Map a, Map b) {
				return String.valueOf(a.get(key)).compareTo(String.valueOf(b.get(key)));
			}
		});
		return _array;
	}
	
	public static CArray rda_rksort(CArray array) {
		array.ksort();
		return array;
	}
	
	/**
	 * Sorts the data using a natural sort algorithm.
	 *
	 * Not suitable for sorting macros, use order_macros() instead.
	 *
	 * @param _data
	 * @param null _sortfield
	 * @param string _sortorder
	 *
	 * @return bool
	 *
	 * @see order_macros()
	 */
	public static boolean order_result(CArray<Map> data, String sortfield, String sortorder) {
		if (empty(data)) {
			return false;
		}

		if (is_null(sortfield)) {
			natcasesort(data);
			if (!RDA_SORT_UP.equals(sortorder)) {
				data = array_reverse(data, true);
			}
			return true;
		}

		CArray<Object> _sort = array();
		for (Entry<Object, Map> e:data.entrySet()) {
			Object _key = e.getKey();
			Map arr = e.getValue();
			if (!isset(arr.get(sortfield))) {
				return false;
			}
			_sort.put(_key, arr.get(sortfield));
		}
		natcasesort(_sort);

		if (!RDA_SORT_UP.equals(sortorder)) {
			_sort = array_reverse(_sort, true);
		}

		CArray _tmp = Clone.deepcopy(data);
		data.clear();
		for (Entry<Object, Object> e: _sort.entrySet()) {
			Object _key = e.getKey();
			data.put(_key, _tmp.get(_key));
		}
		return true;
	}
	
	public static boolean order_result(CArray data, String sortfield) {
		return order_result(data, sortfield, RDA_SORT_UP);
	}
	
	public static boolean order_result(CArray data) {
		return order_result(data, null);
	}
	
	public static void info(String msg) {
		info(new String[] { msg });
	}
	
	public static void info(String[] msgs) {
		List<CMessage> RDA_MESSAGES = g.RDA_MESSAGES.$();
		if (RDA_MESSAGES == null) {
			RDA_MESSAGES = new IList();
			g.RDA_MESSAGES.$(RDA_MESSAGES);
		}
		for (String msg : msgs) {
			RDA_MESSAGES.add(new CMessage("info", msg));
		}
	}
	
	public static void error(String msg) {
		error(new String[] { msg });
	}
	
	public static void error(String[] msgs) {
		List<CMessage> RDA_MESSAGES = g.RDA_MESSAGES.$();
		if (RDA_MESSAGES == null) {
			RDA_MESSAGES = new IList();
			g.RDA_MESSAGES.$(RDA_MESSAGES);
		}
		for (String msg : msgs) {
			RDA_MESSAGES.add(new CMessage("error", msg));
		}
	}
	
	public static List<CMessage> clear_messages() {
		return clear_messages(null);
	}
	
	public static List<CMessage> clear_messages(Integer count) {
		List<CMessage> RDA_MESSAGES = g.RDA_MESSAGES.$();
		List<CMessage> result = new IList();
		if (!is_null(count)) {
			while (count-- > 0) {
				if (!RDA_MESSAGES.isEmpty()) {
					result.add(0, RDA_MESSAGES.remove(RDA_MESSAGES.size() - 1));
				}
			}
		} else {
			result.addAll(RDA_MESSAGES);
		}
		return result;
	}
	
	/**
	 * Clear page cookies on action.
	 *
	 * @param bool   clear
	 * @param string id	parent id, is used as cookie prefix
	 */
	public static void clearCookies(boolean clear, String id) {
		if (clear) {
			String file = Nest.as(RadarContext.getContext().getPage("file")).asString();
			insert_js("cookie.eraseArray(\""+basename(file, ".action")+(!empty(id) ? "_"+id : "")+"\")");
		}
	}
	
	public static void clearCookies() {
		clearCookies(false);
	}
	
	public static void clearCookies(boolean clear) {
		clearCookies(clear, null);
	}
	
	public static CArray<CArray> parse_period(String _str) {
		CArray<CArray> _out = array();
		_str = trim(_str, ';');
		String[] _periods = explode(";", _str);
		for(String _period: _periods) {
			CArray<String> _arr = array();
			if (0 == preg_match("^([1-7])-([1-7]),([0-9]{1,2}):([0-9]{1,2})-([0-9]{1,2}):([0-9]{1,2})$", _period, _arr)) {
				return null;
			}

			for (int i = asInteger(_arr.get(1)); i <= asInteger(_arr.get(2)); i++) {
				if (!isset(_out, i)) {
					_out.put(i, array());
				}
				array_push(_out.get(i), map(
					"start_h" , _arr.get(3),
					"start_m" , _arr.get(4),
					"end_h" , _arr.get(5),
					"end_m" , _arr.get(6)
				));
			}
		}
		return _out;
	}
	
	public static void rda_value2array(TObject obj) {
		CArray carray = obj.asCArray();
		rda_value2array(carray);
		obj.$(carray);
	}
	
	public static void rda_value2array(CArray _values) {
		if (!isArray(_values) && !is_null(_values)) {
			CArray _tmp = array();
			if (is_object(_values)) {
				_tmp.add(_values);
			} else {
				_tmp.put(_values, _values);
			}
			_values.copy(_tmp);
		}
	}
	
	/**
	 * Converts a time period to a human-readable format.
	 *
	 * The following units are used: years, months, days, hours, minutes, seconds and milliseconds.
	 *
	 * Only the three highest units are displayed: #y #m #d, #m #d #h, #d #h #mm and so on.
	 *
	 * If some value is equal to zero, it is omitted. For example, if the period is 1y 0m 4d, it will be displayed as
	 * 1y 4d, not 1y 0m 4d or 1y 4d #h.
	 *
	 * @param int _value	time period in seconds
	 * @param bool _ignoreMillisec	without ms (1s 200 ms = 1.2s)
	 *
	 * @return string
	 */
	public static String convertUnitsS(double value) {
		return convertUnitsS(value, false);
	}
	
	public static String convertUnitsS(double value, boolean ignoreMillisec) {
		String str;
		double secs = (round(value * 1000, RDA_UNITS_ROUNDOFF_UPPER_LIMIT)) / 1000;
		
		if (secs < 0) {
			secs = -secs;
			str = "-";
		} else {
			str = "";
		}
	
		CArray<Double> values = map("y", null, "m", null, "d", null, "h", null, "mm", null, "s", null, "ms", null);
		int n_unit = 0;
	
		double n = Math.floor(secs/SEC_PER_YEAR);
		if (n != 0) {
			secs -= n * SEC_PER_YEAR;
			if (n_unit == 0) {
				n_unit = 4;
			}
			values.put("y", n);
		}
	
		n = Math.floor(secs/SEC_PER_MONTH);
		if (n != 0) {
			secs -= n * SEC_PER_MONTH;
			// due to imprecise calculations it is possible that the remainder contains 12 whole months but no whole years
			if (n == 12) {
				values.put("y", values.get("y")+1);
				values.put("m", null);
				if (n_unit == 0) {
					n_unit = 4;
				}
			} else {
				values.put("m", n);
				if (n_unit == 0) {
					n_unit = 3;
				}
			}
		}
	
		n = Math.floor(secs/SEC_PER_DAY);
		if (n != 0) {
			secs -= n * SEC_PER_DAY;
			values.put("d", n);
			if (n_unit == 0) {
				n_unit = 2;
			}
		}
	
		n = Math.floor(secs/SEC_PER_HOUR);
		if (n_unit < 4 && n != 0) {
			secs -= n * SEC_PER_HOUR;
			values.put("h", n);
			if (n_unit == 0) {
				n_unit = 1;
			}
		}
		
		n = Math.floor(secs/SEC_PER_MIN);
		if (n_unit < 3 && n != 0) {
			secs -= n * SEC_PER_MIN;
			values.put("mm", n);
		}
	
		n = Math.floor(secs);
		if (n_unit < 2 && n != 0) {
			secs -= n;
			values.put("s", n);
		}
	
		if (ignoreMillisec) {
			n = round(secs, RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
			if (n_unit < 1 && n != 0) {
				values.put("s", Nest.value(values, "s").asInteger() + n);
			}
		} else {
			n = round(secs * 1000, RDA_UNITS_ROUNDOFF_UPPER_LIMIT);
			if (n_unit < 1 && n != 0) {
				values.put("ms", n);
			}
		}
	
//		str += isset(values,"y") ? truncNum(Nest.value(values,"y").asString())+_x("y", "year short")+" " : "";
//		str += isset(values,"m") ? truncNum(Nest.value(values,"m").asString())+_x("m", "month short")+" " : "";
//		str += isset(values,"d") ? truncNum(Nest.value(values,"d").asString())+_x("d", "day short")+" " : "";
//		str += isset(values,"h") ? truncNum(Nest.value(values,"h").asString())+_x("h", "hour short")+" " : "";
//		str += isset(values,"mm") ? truncNum(Nest.value(values,"mm").asString())+_x("m", "minute short")+" " : "";
//		str += isset(values,"s") ? truncNum(Nest.value(values,"s").asString())+_x("s", "second short")+" " : "";
//		str += isset(values,"ms") ? truncNum(Nest.value(values,"ms").asString())+_x("ms", "millisecond short")+" " : "";
		
		str += isset(values,"y") ? truncNum(Nest.value(values,"y").asString())+"年" : "";
		str += isset(values,"m") ? truncNum(Nest.value(values,"m").asString())+"月" : "";
		str += isset(values,"d") ? truncNum(Nest.value(values,"d").asString())+"天" : "";
		str += isset(values,"h") ? truncNum(Nest.value(values,"h").asString())+"小时" : "";
		str += isset(values,"mm") ? truncNum(Nest.value(values,"mm").asString())+"分钟" : "";
		str += isset(values,"s") ? truncNum(Nest.value(values,"s").asString())+"秒": "";
		str += isset(values,"ms") ? truncNum(Nest.value(values,"ms").asString())+"毫秒"+" " : "";
	
		return str;
	}
	
	private static String truncNum(String s) {
		if (s != null && s.length() > 0) {
			int pos = s.indexOf('.');
			if (pos > -1) {
				if (s.endsWith("0")) {
					s = trimTrailingCharacter(s, '0');
				}
				if (s.endsWith(".")) {
					s = trimTrailingCharacter(s, '.');
				}
			}
		}
		return s;
	}
		

	/**
	 * Returns the sort field for the current page.
	 * @param executor
	 * @param defaultVal
	 * @return
	 */
	public static String getPageSortField(IIdentityBean idBean, SQLExecutor executor, String defaultVal) {
		Object sort = get_request("sort", CProfile.get(idBean, executor, "web."+RadarContext.page().get("file")+".sort"));
		return (sort!=null) ? String.valueOf(sort) : defaultVal;
	}
	
	/**
	 * Returns the sort field for the current page.
	 * @param executor
	 * @return
	 */
	public static String getPageSortField(IIdentityBean idBean, SQLExecutor executor) {
		return getPageSortField(idBean, executor, null);
	}
	
	
	/**
	 * Returns the sort order for the current page.
	 *
	 * @param string _default
	 *
	 * @return string
	 */
	public static String getPageSortOrder(IIdentityBean idBean, SQLExecutor se, String _default) {
		Object _sortorder = get_request("sortorder", CProfile.get(idBean, se, "web."+RadarContext.page().get("file")+".sortorder", _default));
		return (_sortorder!=null) ? String.valueOf(_sortorder) : _default;
	}
	
	public static String getPageSortOrder(IIdentityBean idBean, SQLExecutor se) {
		return getPageSortOrder(idBean, se, RDA_SORT_UP);
	}
	
	
	/**
	 * Returns the list page number for the current page.
	 *
	 * The functions first looks for a page number in the HTTP request. If no number is given, falls back to the profile.
	 * Defaults to 1.
	 *
	 * @return int
	 */
	public static int getPageNumber(IIdentityBean idBean, SQLExecutor se) {
		String _pageNumber = get_request("page");
		if (!isset(_pageNumber)) {
			Object _lastPage = CProfile.get(idBean, se, "web.paging.lastpage");
			Object o = (RadarContext.page().get("file").equals(_lastPage)) 
					? CProfile.get(idBean, se, "web.paging.page", 1) 
					: 1;
			_pageNumber = String.valueOf(o);
		}
	
		return Integer.valueOf(_pageNumber);
	}
		
	public static CTable getPagingLine(IIdentityBean idBean, SQLExecutor se, CArray _items) {
		return getPagingLine(idBean, se, _items, new CArray<String>());
	}
	
	public static CTable getPagingLine(IIdentityBean idBean, SQLExecutor se, CArray _items, CArray<String> _removeUrlParams) {
		return getPagingLine(idBean, se, _items, _removeUrlParams, array());
	}
	
	/**
	 * Returns paging line.
	 *
	 * @param array _items				list of items
	 * @param array _removeUrlParams	params to remove from URL
	 * @param array _urlParams			params to add in URL
	 *
	 * @return CTable
	 */
	public static CTable getPagingLine(IIdentityBean idBean, SQLExecutor se, CArray _items, CArray<String> _removeUrlParams, CArray<?> _urlParams) {
		Map _page = RadarContext.page();
	
		Map _config = ProfilesUtil.select_config(idBean, se);
	
		String _searchLimit = "";
		if ((Integer)_config.get("search_limit") < count(_items)) {
			array_pop(_items);
			_searchLimit = "+";
		}
	
		Integer _rowsPerPage = asInteger(CWebUser.data().get("rows_per_page"));
		int _itemsCount = count(_items);
		int _pagesCount = (_itemsCount > 0) ? ceil((double)_itemsCount / _rowsPerPage) : 1;
	
		int _currentPage = getPageNumber(idBean, se);
		if (_currentPage < 1) {
			_currentPage = 1;
		}
	
		if (_itemsCount < ((_currentPage - 1) * _rowsPerPage)) {
			_currentPage = _pagesCount;
		}
	
		int _start = (_currentPage - 1) * _rowsPerPage;
	
		
		CProfile.update(idBean, se, "web.paging.lastpage", _page.get("file"), PROFILE_TYPE_STR);
		
		CProfile.update(idBean, se, "web.paging.page", _currentPage, PROFILE_TYPE_INT);
	
		// trim array with items to contain items for current page
		CArray<?> _temps = array_slice(_items, _start, _rowsPerPage, true);
		_items.clear();
		for (Object obj : _temps) {
			_items.add(obj);
		}
		
	
		// viewed pages (better to use not odd)
		int _pagingNavRange = 11;
	
		int _endPage = _currentPage + floor(_pagingNavRange / 2);
		if (_endPage < _pagingNavRange) {
			_endPage = _pagingNavRange;
		}
		if (_endPage > _pagesCount) {
			_endPage = _pagesCount;
		}
	
		int _startPage = (_endPage > _pagingNavRange) ? _endPage - _pagingNavRange + 1 : 1;
	
		CArray _pageLine = array();
	
		CTable _table = null;
	
		if (_pagesCount > 1) {
			Curl _url = new Curl();
	
			if (isArray(_urlParams) && !empty(_urlParams)) {
				for(Entry<Object, ?> e: _urlParams.entrySet()) {
					String key = (String)e.getKey();
					Object value = e.getValue();
					_url.setArgument(key, value);
				}
			}
	
			_removeUrlParams = array_merge(_removeUrlParams, array("go", "form", "delete", "cancel"));
			for(String _param: _removeUrlParams) {
				_url.removeArgument(_param);
			}
	
			if (_startPage > 1) {
				_url.setArgument("page", 1);
				_pageLine.add(new CLink("<< "+_x("First Page", "page navigation"), _url.getUrl(), null, null, true));
				_pageLine.add("&nbsp;&nbsp;");
			}
	
			if (_currentPage > 1) {
				_url.setArgument("page", _currentPage - 1);
				_pageLine.add(new CLink("< "+_x("Previous", "page navigation"), _url.getUrl(), null, null, true));
				_pageLine.add(" | ");
			}
	
			int p;
			for (p = _startPage; p <= _pagesCount; p++) {
				if (p > _endPage) {
					break;
				}
	
				CTag _pagespan;
				if (p == _currentPage) {
					_pagespan = new CSpan(p, "bold textcolorstyles");
				} else {
					_url.setArgument("page", p);
					_pagespan = new CLink(p, _url.getUrl(), null, null, true);
				}
	
				_pageLine.add(_pagespan);
				_pageLine.add(" | ");
			}
	
			array_pop(_pageLine);
	
			if (_currentPage < _pagesCount) {
				_pageLine.add(" | ");
	
				_url.setArgument("page", _currentPage + 1);
				_pageLine.add(new CLink(_x("Next", "page navigation")+" >", _url.getUrl(), null, null, true));
			}
	
			if (p < _pagesCount) {
				_pageLine.add("&nbsp;&nbsp;");
	
				_url.setArgument("page", _pagesCount);
				_pageLine.add(new CLink(_x("Last Page", "page navigation")+" >>", _url.getUrl(), null, null, true));
			}
	
			if (originalStyle) {
				_table = new CTable(null, "paging");
				_table.addRow(new CCol(_pageLine));
			}
		}
		
		if (!originalStyle) {
			CDiv numRows = new CDiv();
		    numRows.setAttribute("name", "numrows");
		    numRows.setAttribute("style", "float:right;");
		    
		    _pageLine.add(numRows);

			_table = new CTable(null, "paging");
			_table.addRow(new CCol(_pageLine));
		}
	
		int _viewFromPage = (_currentPage - 1) * _rowsPerPage + 1;
	
		int _viewTillPage = _currentPage * _rowsPerPage;
		if (_viewTillPage > _itemsCount) {
			_viewTillPage = _itemsCount;
		}
	
		CArray _pageView = array();
		_pageView.add(_("Displaying")+SPACE);
		if (_itemsCount > 0) {
			_pageView.add(new CSpan(_viewFromPage, "info"));
			_pageView.add(SPACE+_("to")+SPACE);
		}
	
		_pageView.add(new CSpan(_viewTillPage, "info"));
		_pageView.add(SPACE+_("of")+SPACE);
		_pageView.add(new CSpan(_itemsCount, "info"));
		_pageView.add(_searchLimit);
		_pageView.add(SPACE+_("found"));
	
		Object pageView = new CSpan(_pageView);
	
		JsUtil.rda_add_post_js("insertInElement(\"numrows\", "+JsUtil.rda_jsvalue(pageView.toString())+", \"div\");");
	
		return _table;
	}
	
	public static String num2letter(int num) {
		int start = 'A', base = 26, remainder = 0, level = 0;
		String str = "";
		do {
			if (level++ > 0) {
				num--;
			}
			remainder = num % base;
			num = (num - remainder) / base;
			str = (char) (start + remainder) + str;
		} while (num != 0);
		return str;
	}

	public static void show_messages() {
		show_messages(true, null, null);
	}

	public static void show_message(String msg) {
		show_messages(true, msg, "");
	}

	public static void show_messages(boolean bool) {
		show_messages(bool, null, null);
	}

	public static void show_messages(boolean bool, String okmsg) {
		show_messages(bool, okmsg, null);
	}

	public static void show_messages(boolean bool, String okmsg, String errmsg) {
		Map page = RadarContext.page();
		List<CMessage> rda_messages = g.RDA_MESSAGES.$();
		
	
		if (!defined("PAGE_HEADER_LOADED")) {
			return;
		}
		if (defined("RDA_API_REQUEST")) {
			return;
		}
		if (!isset(page.get("type"))) {
			page.put("type", PAGE_TYPE_HTML);
		}
	
		CArray message = array();
		int width = 0;
		int height= 0;
		
		String _msg = null;
		if (!bool && !is_null(errmsg)) {
			_msg = _("ERROR")+": "+errmsg;
		} else if (bool && !is_null(okmsg)) {
			_msg = okmsg;
		}
	
		CTable msg_tab = null;
		if (isset(_msg)) {
			switch ((Integer)page.get("type")) {
				case PAGE_TYPE_IMAGE:
					array_push(message, map(
						"text", _msg,
						"color", (!bool) ? map("R", 255, "G", 0, "B", 0) : map("R", 34, "G", 51, "B", 68),
						"font", 2
					));
					width = max(width, imagefontwidth(2) * rda_strlen(_msg) + 1).intValue();
					height += imagefontheight(2) + 1;
					break;
				case PAGE_TYPE_XML:
					echo(htmlspecialchars(_msg)+"\n");
					break;
				case PAGE_TYPE_HTML:
				default:
					msg_tab = new CTable(_msg, (bool ? "msgok" : "msgerr"));
					msg_tab.setCellPadding(0);
					msg_tab.setCellSpacing(0);
	
					CArray row = array();
	
					CCol msg_col = new CCol(HtmlUtil.bold(_msg), "msg_main msg");
					msg_col.setAttribute("id", "page_msg");
					row.add(msg_col);
	
					if (isset(rda_messages) && !empty(rda_messages)) {
						CDiv msg_details = new CDiv(_("Details"), "blacklink");
						msg_details.setAttribute("onclick", "javascript: showHide(\"msg_messages\", IE ? \"block\" : \"table\");");
						msg_details.setAttribute("title", _("Maximize")+"/"+_("Minimize"));
						array_unshift(row, new CCol(msg_details, "clr"));
					}
					msg_tab.addRow(row);
					msg_tab.show();
					break;
			}
		}
	
		if (isset(rda_messages) && !empty(rda_messages)) {
			if ((Integer)page.get("type") == PAGE_TYPE_IMAGE) {
				int _msg_font = 2;
				for(CMessage msg: rda_messages) {
					if ("error".equals(msg.getType())) {
						array_push(message, map(
							"text", msg.getMessage(),
							"color", map("R", 255, "G", 55, "B", 55),
							"font", _msg_font
						));
					} else {
						array_push(message, map(
							"text", msg.getMessage(),
							"color", map("R", 155, "G", 155, "B", 55),
							"font", _msg_font
						));
					}
					width = max(width, imagefontwidth(_msg_font) * rda_strlen(msg.getMessage()) + 1).intValue();
					height += imagefontheight(_msg_font) + 1;
				}
			}
			else if ((Integer)page.get("type") == PAGE_TYPE_XML) {
				for(CMessage msg: rda_messages) {
					echo("["+msg.getType()+"] "+msg.getMessage()+"\n");
				}
			}
			else {
				CList lst_error = new CList(null,"messages");
				for(CMessage msg: rda_messages) {
					lst_error.addItem(msg.getMessage(), msg.getType());
					bool = (bool && !"error".equals(rda_strtolower(msg.getType())));
				}
				int msg_show = 6;
				int msg_count = count(rda_messages);
				if (msg_count > msg_show) {
					msg_count = msg_show * 16;
					lst_error.setAttribute("style", "height: "+msg_count+"px;");
				}
				CTable tab = new CTable(null, (bool ? "msgok" : "msgerr"));
				tab.setCellPadding(0);
				tab.setCellSpacing(0);
				tab.setAttribute("id", "msg_messages");
				tab.setAttribute("style", "width: 100%;");
				if (isset(msg_tab) && bool) {
					tab.setAttribute("style", "display: none;");
				}
				tab.addRow(new CCol(lst_error, "msg"));
				tab.show();
			}
			rda_messages.clear();
		}
	
		if ((Integer)page.get("type") == PAGE_TYPE_IMAGE && count(message) > 0) {
			width += 2;
			height += 2;
//			canvas = php.imagecreate(_width, _height);
//			imagefilledrectangle(canvas, 0, 0, width, height, imagecolorallocate(canvas, 255, 255, 255));
//	
//			foreach (_message as _id => _msg) {
//				_message[_id]["y"] = 1 + (isset(_previd) ? _message[_previd]["y"] + _message[_previd]["h"] : 0);
//				_message[_id]["h"] = imagefontheight(_msg["font"]);
//				imagestring(
//					_canvas,
//					_msg["font"],
//					1,
//					_message[_id]["y"],
//					_msg["text"],
//					imagecolorallocate(_canvas, _msg["color"]["R"], _msg["color"]["G"], _msg["color"]["B"])
//				);
//				_previd = _id;
//			}
//			imageOut(_canvas);
//			imagedestroy(_canvas);
		}
	}
	
	public static void show_error_message(String msg) {
		show_messages(false, "", msg);
	}
	
	
	public static int rda_strlen(String _str) {
		if (defined("RDA_MBSTRINGS_ENABLED")) {
			return mb_strlen(_str);
		}
		else {
			return strlen(_str);
		}
	}
	
	public static int rda_strlen(char c) {
		return String.valueOf(c).getBytes().length;
	}
	
	public static String rda_strstr(String _haystack, String needle) {
		return strstr(_haystack, needle);
	}
	
	public static String rda_strtolower(String _str) {
		if (defined("RDA_MBSTRINGS_ENABLED")) {
			return mb_strtolower(_str);
		} else {
			return strtolower(_str);
		}
	}
	
	public static String rda_strtoupper(String _str) {
		if (defined("RDA_MBSTRINGS_ENABLED")) {
			return mb_strtoupper(_str);
		}
		else {
			return strtoupper(_str);
		}
	}
	
	//TODO
	public static String rda_str2links(String _str) {
		return _str != null ? _str : "";
	}
	
	public static CArray<String> rda_nl2br(String str) {
		CArray<String> str_res = array();
		if(str!=null && str.length()>0){
			String[] str_arr = explode("\n", str);
			for (String str_line:str_arr) {
				array_push(str_res, str_line, HtmlUtil.BR());
			}
		}
		return str_res;
	}
	
	public static String rda_formatDomId(String _value) {
		//return str_replace(array("\\[", "\\]"), array("_", ""), _value);
		//fixed for host edit by benne
		return str_replace(array("[", "]"), array("_", ""), _value);
	}
	
	public static boolean str_in_array(Object o, CArray array) {
		return in_array(String.valueOf(o), array);
	}
	
	/**
	 * Find if array has any duplicate values and return an array with info about them.
	 * In case of no duplicates, empty array is returned.
	 * Example of usage:
	 *     _result = rda_arrayFindDuplicates(
	 *         array('a', 'b', 'c', 'c', 'd', 'd', 'd', 'e')
	 *     );
	 *     array(
	 *         'd' => 3,
	 *         'c' => 2,
	 *     )
	 *
	 * @param array _array
	 *
	 * @return array
	 */
	public static CArray<Integer> rda_arrayFindDuplicates(CArray _array) {
		CArray<Integer> _countValues = array_count_values(_array); // counting occurrences of every value in array
		for(Entry<Object, Integer> entry: Clone.deepcopy(_countValues).entrySet()) {
			Object _value = entry.getKey();
			Integer _count = entry.getValue();
			if(_count <= 1) {
				unset(_countValues, _value);
			}
			arsort(_countValues); // sorting, so that the most duplicates would be at the top
		}
		return _countValues;
	}	

	public static int detect_page_type(int defval) {
		String output = (String) $_REQUEST("output");
		if (isset(output)) {
			output = strtolower(output);
			if ("text".equals(output)) {
				return PAGE_TYPE_TEXT;
			} else if ("ajax".equals(output)) {
				return PAGE_TYPE_JS;
			} else if ("json".equals(output)) {
				return PAGE_TYPE_JSON;
			} else if ("json-rpc".equals(output)) {
				return PAGE_TYPE_JSON_RPC;
			} else if ("html".equals(output)) {
				return PAGE_TYPE_HTML_BLOCK;
			} else if ("img".equals(output)) {
				return PAGE_TYPE_IMAGE;
			} else if ("css".equals(output)) {
				return PAGE_TYPE_CSS;
			}
		}
		return defval;
	}
	
	public static int detect_page_type() {
		return detect_page_type(PAGE_TYPE_HTML);
	}
	
	// creates chain of relation parent -> childs, for all chain levels
	public static void createParentToChildRelation(CArray _chain, Map _link, String _parentField, String _childField) {
		if (!isset(_chain.get(_link.get(_parentField)))) {
			_chain.put(_link.get(_parentField), array());
		}

		_chain.put(_link.get(_parentField), _link.get(_childField), _link.get(_childField));
		if (isset(_chain.get(_link.get(_childField)))) {
			_chain.put(_link.get(_parentField), rda_array_merge((CArray)_chain.get(_link.get(_parentField)), (CArray)_chain.get(_link.get(_childField))));
		}
	}

	/**
	 * Renders an "access denied" message and stops the execution of the script.
	 *
	 * The mode parameters controls the layout of the message:
	 * - ACCESS_DENY_OBJECT     - render the message when denying access to a specific object
	 * - ACCESS_DENY_PAGE       - render a complete access denied page
	 *
	 * @param int mode
	 */
	public static void access_deny(){
		access_deny(ACCESS_DENY_OBJECT);
	}
	
	/**
	 * Renders an "access denied" message and stops the execution of the script.
	 *
	 * The mode parameters controls the layout of the message:
	 * - ACCESS_DENY_OBJECT     - render the message when denying access to a specific object
	 * - ACCESS_DENY_PAGE       - render a complete access denied page
	 *
	 * @param int mode
	 */
	public static void access_deny(int mode){
		throw new ExitException(mode);
	}
	
	/**
	 * @param values 放值的CArray或值本身
	 * @return
	 */
	public static Float rda_avg(Object values) {
		CArray cvalues = CArray.valueOf(values);
		double sum = 0;
		for (Object value : cvalues) {
			sum = bcadd(asFloat(sum), asFloat(value));
		}
		return bcdiv(asFloat(sum), count(cvalues));
	}
	
	public static CArray getMenuPopupTrigger(Map trigger) {
		return getMenuPopupTrigger(trigger, null);
	}
	
	public static CArray getMenuPopupTrigger(Map trigger, CArray items) {
		return getMenuPopupTrigger(trigger, items, null);
	}
	
	public static CArray getMenuPopupTrigger(Map trigger, CArray items, CArray acknowledge) {
		return getMenuPopupTrigger(trigger, items, acknowledge, null);
	}
	
	/**
	 * Prepare data for trigger menu popup.
	 *
	 * @param array  _trigger						trigger data
	 * @param string _trigger["triggerid"]			trigger id
	 * @param int    _trigger["flags"]				trigger flags (TRIGGER_FLAG_DISCOVERY*)
	 * @param array  _trigger["hosts"]				hosts, used by trigger expression
	 * @param string _trigger["hosts"][]["hostid"]	host id
	 * @param string _trigger["url"]				url
	 * @param array  _items							trigger items (optional)
	 * @param string _items[]["name"]				item name
	 * @param array  _items[]["params"]				item url parameters (\"name\" => \"value\")
	 * @param array  _acknowledge					acknowledge link parameters (optional)
	 * @param string _acknowledge["eventid"]		event id
	 * @param string _acknowledge["screenid"]		screen id (optional)
	 * @param string _acknowledge["backurl"]		return url (optional)
	 * @param string _eventTime						event navigation time parameter (optional)
	 *
	 * @return array
	 */
	public static CArray getMenuPopupTrigger(Map trigger, CArray items, CArray acknowledge, String eventTime) {
		if (!empty(items)) {
			CArrayHelper.sort(items, array("name"));
		}

		if (acknowledge == null) {
			acknowledge = new CArray();
		}
		
		CArray data = map(
			"type", "trigger",
			"triggerid", Nest.value(trigger,"triggerid").$(),
			"items", items.values().toArray(),
			"acknowledge", acknowledge.toArray(),
			"eventTime", eventTime,
			"configuration", null,
			"url", TriggersUtil.resolveTriggerUrl(asCArray(trigger))
		);

		if ((CWebUser.getType() == USER_TYPE_IRADAR_ADMIN || CWebUser.getType() == USER_TYPE_SUPER_ADMIN)
				&& Nest.value(trigger,"flags").asInteger() == RDA_FLAG_DISCOVERY_NORMAL) {
			Map host = reset(Nest.value(trigger,"hosts").asCArray());
			Nest.value(data,"configuration").$(map(
					"hostid", Nest.value(host,"hostid").$()
				));
		}

		return data;
	}
	
	/**
	 * Converts value with suffix to actual value.
	 * Supported time suffixes: s, m, h, d, w
	 * Supported metric suffixes: K, M, G, T
	 *
	 * @param string _value
	 *
	 * @return string
	 */
	public static Double convertFunctionValue(String value) {
		char suffix = value.charAt(strlen(value) - 1);
		double v;
		if (!ctype_digit(suffix)) {
			value = substr(value, 0, strlen(value) - 1);

			v = asDouble(value);
			
			switch (suffix) {
				case 's':
					break;
				case 'm':
					v = bcmul(v, 60);
					break;
				case 'h':
					v = bcmul(v, 3600);
					break;
				case 'd':
					v = bcmul(v, 86400);
					break;
				case 'w':
					v = bcmul(v, 604800);
					break;
				case 'K':
					v = bcmul(v, 1024);
					break;
				case 'M':
					v = bcmul(v, 1048576);
					break;
				case 'G':
					v = bcmul(v, 1073741824);
					break;
				case 'T':
					v = bcmul(v, 1099511627776L);
					break;
			}
		} else {
			v = asDouble(value);
		}
		
		return v;
	}
	
	
	/**
	 * Converts value to actual value.
	 * Example:
	 * 	6442450944 B convert to 6 GB
	 *
	 * @param array  _options
	 * @param string _options['value']
	 * @param string _options['units']
	 * @param string _options['convert']
	 * @param string _options['byteStep']
	 * @param string _options['pow']
	 * @param bool   _options['ignoreMillisec']
	 * @param string _options['length']
	 *
	 * @return string
	 */
	public static String convert_units(CArray options) {
		CArray defOptions = map(
			"value", null,
			"units", null,
			"convert", ITEM_CONVERT_WITH_UNITS,
			"byteStep", false,
			"pow", false,
			"ignoreMillisec", false,
			"length", false
		);

		options = rda_array_merge(defOptions, options);

		// special processing for unix timestamps
		if ("unixtime".equals(Nest.value(options,"units").$())) {
			return rda_date2str(_("Y.m.d H:i:s"), Nest.value(options,"value").asLong());
		}

		// special processing of uptime
		if ("uptime".equals(Nest.value(options,"units").$())) {
			return convertUnitsUptime(Nest.value(options,"value").asString());
		}

		// special processing for seconds
		if ("s".equals(Nest.value(options,"units").$())) {
			return convertUnitsS(Nest.value(options,"value").asDouble(), Nest.value(options,"ignoreMillisec").asBoolean());
		}

		// any other unit
		// black list of units that should have no multiplier prefix (K, M, G etc) applied
		CArray blackList = array("%", "ms", "rpm", "RPM");

		if (in_array(Nest.value(options,"units").$(), blackList) || (rda_empty(Nest.value(options,"units").$())
				&& (Nest.value(options,"convert").asInteger() == ITEM_CONVERT_WITH_UNITS))) {
			if (Math.abs(Nest.value(options,"value").asDouble()) >= RDA_UNITS_ROUNDOFF_THRESHOLD) {
				Nest.value(options,"value").$(round(Nest.value(options,"value").asDouble(), RDA_UNITS_ROUNDOFF_UPPER_LIMIT));
			}
			Nest.value(options,"value").$(sprintf("%."+RDA_UNITS_ROUNDOFF_LOWER_LIMIT+"f", Nest.value(options,"value").asDouble()));
			Nest.value(options,"value").$(preg_replace("^([\\-0-9]+)(\\.)([0-9]*?)[0]+$", "$1$2$3", Nest.value(options,"value").asString()));
			Nest.value(options,"value").$(rtrim(Nest.value(options,"value").asString(), '.'));

			return trim(options.get("value")+" "+Nest.value(options,"units").$());
		}

		// if one or more items is B or Bps, then Y-scale use base 8 and calculated in bytes
		int step = 0;
		if (Nest.value(options,"byteStep").asBoolean()) {
			step = 1024;
		} else {
			String units = Nest.value(options,"units").asString();
			if("Bps".equals(units) || "B".equals(units)) {
				step = 1024;
				Nest.value(options,"convert").$(Nest.value(options,"convert").asBoolean() ? Nest.value(options,"convert").$() : ITEM_CONVERT_NO_UNITS);
			} else {
				if("bps".equals(units) || "b".equals(units)) {
					Nest.value(options,"convert").$(Nest.value(options,"convert").asBoolean() ? Nest.value(options,"convert").$() : ITEM_CONVERT_NO_UNITS);
				}
				step = 1000;
			}
		}

		double abs;
		if (Nest.value(options,"value").asDouble() < 0) {
			abs = bcmul(Nest.value(options,"value").asDouble(), -1);
		} else {
			abs = Nest.value(options,"value").asDouble();
		}
		
		//FIXME: 修复value==0时，出现0.0的情况
		if(abs == 0d) {
			return trim(0+" "+Nest.value(options,"units").$());
		}

		if (bccomp(abs, 1) == -1) {
			Nest.value(options,"value").$(round(Nest.value(options,"value").asDouble(), RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT));
			Nest.value(options,"value").$(
					(Nest.value(options,"length").asBoolean() && Nest.value(options,"value").asInteger() != 0)
					? sprintf("%."+options.get("length")+"f",Nest.value(options,"value").$()) 
					: Nest.value(options,"value").$()
				);
			
			return trim(options.get("value")+" "+Nest.value(options,"units").$());
		}

		// init intervals
		CArray<CArray<CArray>> digitUnits = null;
		if (is_null(digitUnits)) {
			digitUnits = array();
		}
		if (!isset(digitUnits.get(step))) {
			digitUnits.put(step, array(
				map("pow", 0, "short", "", "long", ""),
				map("pow", 1, "short", _x("K", "Kilo short"), "long", _("Kilo")),
				map("pow", 2, "short", _x("M", "Mega short"), "long", _("Mega")),
				map("pow", 3, "short", _x("G", "Giga short"), "long", _("Giga")),
				map("pow", 4, "short", _x("T", "Tera short"), "long", _("Tera")),
				map("pow", 5, "short", _x("P", "Peta short"), "long", _("Peta")),
				map("pow", 6, "short", _x("E", "Exa short"), "long", _("Exa")),
				map("pow", 7, "short", _x("Z", "Zetta short"), "long", _("Zetta")),
				map("pow", 8, "short", _x("Y", "Yotta short"), "long", _("Yotta"))
			));

			for(CArray data: digitUnits.get(step)) {

				// skip mili & micro for values without units
				Nest.value(data, "value").$(bcpow(step, Nest.value(data,"pow").asDouble(), 9));
			}
		}


		CArray valUnit = map("pow", 0, "short", "", "long", "", "value", Nest.value(options, "value").$());

		if (Nest.value(options,"pow").asBoolean() == false || Nest.value(options,"value").asInteger() == 0) {
			for(Entry<Object, CArray> entry: digitUnits.get(step).entrySet()) {
				//Object _dnum = entry.getKey();
				CArray _data = entry.getValue();
			
				if (bccomp(abs, Nest.value(_data,"value").asDouble()) > -1) {
					valUnit = _data;
				} else {
					break;
				}
			}
		} else {
			for(CArray data: digitUnits.get(step)) {
				if (Nest.value(options,"pow").asDouble() == Nest.value(data,"pow").asDouble()) {
					valUnit = data;
					break;
				}
			}
		}

		if (round(Nest.value(valUnit,"value").asDouble(), RDA_UNITS_ROUNDOFF_MIDDLE_LIMIT) > 0) {
			Nest.value(valUnit,"value").$(bcdiv(
					Double.valueOf(sprintf("%.10f",Nest.value(options,"value").asDouble())), 
					Double.valueOf(sprintf("%.10f", Nest.value(valUnit,"value").asDouble())), 
					RDA_PRECISION_10));
		} else {
			Nest.value(valUnit,"value").$(0);
		}

		Object desc = null;
		switch (Nest.value(options,"convert").asInteger()) {
			case 0: Nest.value(options,"units").$(trim(Nest.value(options,"units").asString()));
			case 1: desc = Nest.value(valUnit,"short").$(); break;
			case 2: desc = Nest.value(valUnit,"long").$(); break;
		}

		Nest.value(options,"value").$(preg_replace("^([\\-0-9]+)(\\.)([0-9]*?)[0]+$","$1$2$3", 
				round(Nest.value(valUnit,"value").asDouble(),RDA_UNITS_ROUNDOFF_UPPER_LIMIT)+""
			));

		Nest.value(options,"value").$(rtrim(Nest.value(options,"value").asString(), '.'));

		// fix negative zero
		if (bccomp(Nest.value(options,"value").$(), 0) == 0) {
			Nest.value(options,"value").$(0);
		}

		Object v = Nest.value(options, "length").asBoolean()
				? sprintf("%."+options.get("length")+"f",Nest.value(options,"value").asDouble())
				: Nest.value(options,"value").$();
		String s = sprintf("%s %s%s", v , desc, Nest.value(options,"units").$());
		return trim(s);
	}
	

	public static String convertUnitsUptime(String _value) {
		double _secs;
		if ((_secs = Math.round(TObj.as(_value).asDouble())) < 0) {
			_value = "-";
			_secs = -_secs;
		}
		else {
			_value = "";
		}
	
		double _days = floor(_secs / SEC_PER_DAY);
		_secs -= _days * SEC_PER_DAY;
	
		double _hours = floor(_secs / SEC_PER_HOUR);
		_secs -= _hours * SEC_PER_HOUR;
	
		double _mins = floor(_secs / SEC_PER_MIN);
		_secs -= _mins * SEC_PER_MIN;
	
		String value = "";
		if (_days != 0) {
			value += _n("%1$d day", "%1$d days", (int)_days)+", ";
		}
		value += sprintf("%02d:%02d:%02d", (int)_hours, (int)_mins, (int)_secs);
	
		return value;
	}

	/**
	 * Prepare data for item history menu popup.
	 *
	 * @param array _item				item data
	 * @param int   _item['itemid']		item id
	 * @param int   _item['value_type']	item value type
	 *
	 * @return array
	 */
	public static Map getMenuPopupHistory(Map item) {
		return map(
				"type", "history",
				"itemid", item.get("itemid"),
				"hasLatestGraphs", in_array(Nest.value(item,"value_type").asString(), array(String.valueOf(ITEM_VALUE_TYPE_UINT64), String.valueOf(ITEM_VALUE_TYPE_FLOAT)))
			);
	}
	

	public static Map getMenuPopupHost(Map _host) {
		return getMenuPopupHost(_host, null);
	}
	
	public static Map getMenuPopupHost(Map host, CArray<Map> scripts) {
		return getMenuPopupHost(host, scripts, true);
	}
	/**
	 * Prepare data for host menu popup.
	 *
	 * @param array  _host						host data
	 * @param string _host['hostid']			host id
	 * @param array  _host['screens']			host screens (optional)
	 * @param array  _scripts					host scripts (optional)
	 * @param string _scripts[]['name']			script name
	 * @param string _scripts[]['scriptid']		script id
	 * @param string _scripts[]['confirmation']	confirmation text
	 * @param bool   _hasGoTo					"Go to" block in popup
	 *
	 * @return array
	 */
	public static Map getMenuPopupHost(Map host, CArray<Map> scripts, boolean hasGoTo) {
		Map data = map(
			"type", "host",
			"hostid", Nest.value(host,"hostid").asString(),
			"hasScreens", (isset(host,"screens") && !empty(Nest.value(host,"screens").$())),
			"hasGoTo", hasGoTo
		);
		if (!empty(scripts)) {
			CArrayHelper.sort(scripts, array("name"));

			CArray<Map> cscripts = array();
			Nest.value(data,"scripts").$(cscripts);
			for(Map _script : scripts) {
				cscripts.add(map(
					"name", Nest.value(_script,"name").asString(),
					"scriptid", Nest.value(_script,"scriptid").asString(),
					"confirmation", Nest.value(_script,"confirmation").asString()
				));
			}
		}
		return data;
	}
	
	public static String rda_substr(String _string, int _start) {
		return rda_substr(_string, _start, null);
	}
	
	public static String rda_substr(String _string, int _start, Integer _length) {
		if(_length == null){
			return _string.substring(_start);
		} else {
			return _string.substring(_start,_start+_length);
		}
	}
	
	public static void validate_sort_and_sortorder(IIdentityBean idBean, SQLExecutor executor) {
		validate_sort_and_sortorder(idBean, executor, null);
	}
	
	public static void validate_sort_and_sortorder(IIdentityBean idBean, SQLExecutor executor, String _sort) {
		validate_sort_and_sortorder(idBean, executor, _sort, RDA_SORT_UP);
	}
	/**
	 * Get the sort and sort order parameters for the current page and save it into profiles.
	 *
	 * @param string _sort
	 * @param string _sortorder
	 *
	 * @return void
	 */
	public static void validate_sort_and_sortorder(IIdentityBean idBean, SQLExecutor executor, String _sort, String _sortorder) {
		Map _page = RadarContext.page(); 
		Nest.value($_REQUEST(),"sort").$(getPageSortField(idBean, executor, _sort));
		Nest.value($_REQUEST(),"sortorder").$(getPageSortOrder(idBean, executor,_sortorder));

		if (!is_null(Nest.value($_REQUEST(),"sort").$())) {
			Nest.value($_REQUEST(),"sort").$( preg_replace("[^a-z\\.\\_]", "", Nest.value($_REQUEST(),"sort").asString()));
			CProfile.update(idBean, executor, "web."+_page.get("file")+".sort", Nest.value($_REQUEST(),"sort").$(), PROFILE_TYPE_STR);
		}

		if (!str_in_array(Nest.value($_REQUEST(),"sortorder").$(), array(RDA_SORT_DOWN, RDA_SORT_UP))) {
			Nest.value($_REQUEST(),"sortorder").$(RDA_SORT_UP);
		}

		CProfile.update(idBean, executor, "web."+_page.get("file")+".sortorder", Nest.value($_REQUEST(),"sortorder").$(), PROFILE_TYPE_STR);
	}
	
	
	/**
	 * Transforms a single or an array of values to an array of objects, where the values are stored under the _field
	 * key.
	 *
	 * E.g:
	 * rda_toObject(array(1, 2), 'hostid')  // returns array(array('hostid' => 1), array('hostid' => 2))
	 * rda_toObject(3, 'hostid')            // returns array(array('hostid' => 3))
	 *
	 * @param _value
	 * @param _field
	 *
	 * @return array
	 */
	public static CArray rda_toObject(CArray _value, String _field) {
		if (is_null(_value)) {
			return _value;
		}
		CArray _result = array();

		// Value or Array to Object or Array of objects
		if (!isArray(_value)) {
			_result = array(map(_field, _value));
		}
		else if (!isset(_value.get(_field))) {
			for(Object _val: _value) {
				if (!isArray(_val)) {
					_result.add( map(_field, _val) );
				}
			}
		}

		return _result;
	}
	
	public static boolean uint_in_array(String needle, CArray<String> haystack) {
		for(String value: haystack) {
			if (bccomp(needle, value) == 0) {
				return true;
			}
		}

		return false;
	}
	
	public static <T> boolean uint_in_array(T _needle, CArray<T> _haystack) {
		for(T _value: _haystack) {
			if (bccomp(_needle, _value) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static String rda_stristr(String _haystack, String _needle) {
//		if (defined("RDA_MBSTRINGS_ENABLED")) {
//			_haystack_B = mb_strtoupper(_haystack);
//			_needle = mb_strtoupper(_needle);
//
//			_pos = mb_strpos(_haystack_B, _needle);
//			if (_pos !== false) {
//				_pos = mb_substr(_haystack, _pos);
//			}
//			return _pos;
//		}
//		else {
			return stristr(_haystack, _needle);
//		}
	}
	
	public static String rda_stristr(String _haystack, int _needle) {
		return stristr(_haystack, _needle);
	}

	public static void rda_swap(TObj t1, TObj t2) {
		Object o1 = t1.$();
		Object o2 = t2.$();
		t1.$(o2);
		t2.$(o1);
	}
	
	public static Map get_status(IIdentityBean idBean, SQLExecutor executor) {
		Map status = map(
			"triggers_count", 0,
			"triggers_count_enabled", 0,
			"triggers_count_disabled", 0,
			"triggers_count_off", 0,
			"triggers_count_on", 0,
			"items_count", 0,
			"items_count_monitored", 0,
			"items_count_disabled", 0,
			"items_count_not_supported", 0,
			"hosts_count", 0,
			"hosts_count_monitored", 0,
			"hosts_count_not_monitored", 0,
			"hosts_count_template", 0,
			"users_online", 0,
			"qps_total", 0
		);

		// server
		IRadarServer iradarServer = null;
		try {
			iradarServer = new IRadarServer(g.RDA_SERVER, g.RDA_SERVER_PORT, RDA_SOCKET_TIMEOUT, 0);
			Nest.value(status,"iradar_server").$(iradarServer.isRunning() ? _("Yes") : _("No"));
		} finally {
			if (iradarServer != null) {
				iradarServer.close();
			}
		}

		// triggers
		SqlBuilder sqlParts = new SqlBuilder();
		CArray<Map> dbTriggers = DBselect(executor,
			"SELECT COUNT(DISTINCT t.triggerid) AS cnt,t.status,t.value"+
				" FROM triggers t"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "triggers", "t")+
				" AND NOT EXISTS ("+
					"SELECT f.functionid FROM functions f"+
						" JOIN items i ON f.tenantid=i.tenantid AND f.itemid=i.itemid"+
						" JOIN hosts h ON i.tenantid=h.tenantid AND i.hostid=h.hostid"+
						" WHERE f.tenantid=t.tenantid AND f.triggerid=t.triggerid AND (i.status<>"+ITEM_STATUS_ACTIVE+" OR h.status<>"+HOST_STATUS_MONITORED+")"+
					")"+
				" AND t.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
				" GROUP BY t.status,t.value",
			sqlParts.getNamedParams());
		for(Map dbTrigger : dbTriggers) {
			switch (Nest.value(dbTrigger,"status").asInteger()) {
				case TRIGGER_STATUS_ENABLED:
					switch (Nest.value(dbTrigger,"value").asInteger()) {
						case TRIGGER_VALUE_FALSE:
							Nest.value(status,"triggers_count_off").$(Nest.value(dbTrigger,"cnt").$());
							break;
						case TRIGGER_VALUE_TRUE:
							Nest.value(status,"triggers_count_on").$(Nest.value(dbTrigger,"cnt").$());
							break;
					}
					break;
				case TRIGGER_STATUS_DISABLED:
					Nest.value(status,"triggers_count_disabled").$(
							Nest.value(status,"triggers_count_disabled").asInteger()
							+
							Nest.value(dbTrigger,"cnt").asInteger()
					);
					break;
			}
		}
		Nest.value(status,"triggers_count_enabled").$(Nest.value(status,"triggers_count_off").asInteger() + Nest.value(status,"triggers_count_on").asInteger());
		Nest.value(status,"triggers_count").$(Nest.value(status,"triggers_count_enabled").asInteger() + Nest.value(status,"triggers_count_disabled").asInteger());

		// items
		sqlParts = new SqlBuilder();
		CArray<Map> dbItems = DBselect(executor,
			"SELECT COUNT(i.itemid) AS cnt,i.status,i.state"+
					" FROM items i"+
					" INNER JOIN hosts h ON i.tenantid=h.tenantid AND i.hostid=h.hostid"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "items", "i")+
					    " AND h.status="+HOST_STATUS_MONITORED+
						" AND i.flags IN ("+RDA_FLAG_DISCOVERY_NORMAL+","+RDA_FLAG_DISCOVERY_CREATED+")"+
						" AND i.type<>"+ITEM_TYPE_HTTPTEST+
					" GROUP BY i.status,i.state",
			sqlParts.getNamedParams());
		for(Map dbItem : dbItems) {
			if (Nest.value(dbItem,"status").asInteger() == ITEM_STATUS_ACTIVE) {
				if (Nest.value(dbItem,"state").asInteger() == ITEM_STATE_NORMAL) {
					Nest.value(status,"items_count_monitored").$(Nest.value(dbItem,"cnt").asInteger());
				} else {
					Nest.value(status,"items_count_not_supported").$(Nest.value(dbItem,"cnt").asInteger());
				}
			} else if (Nest.value(dbItem,"status").asInteger() == ITEM_STATUS_DISABLED) {
				Nest.value(status,"items_count_disabled").$(
						Nest.value(status,"items_count_disabled").asInteger()
						+
						Nest.value(dbItem,"cnt").asInteger()
				);
			}
		}
		Nest.value(status,"items_count").$(
				Nest.value(status,"items_count_monitored").asInteger()
			+ Nest.value(status,"items_count_disabled").asInteger()
			+ Nest.value(status,"items_count_not_supported").asInteger()
		);

		// hosts
		sqlParts = new SqlBuilder();
		CArray<Map> dbHosts = DBselect(executor,
			"SELECT COUNT(*) AS cnt,h.status"+
			" FROM hosts h"+
			" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "hosts", "h")+
			    " AND "+sqlParts.dual.dbConditionInt("h.status", new int[]{HOST_STATUS_MONITORED, HOST_STATUS_NOT_MONITORED, HOST_STATUS_TEMPLATE})+
				" AND "+sqlParts.dual.dbConditionInt("h.flags", new int[]{RDA_FLAG_DISCOVERY_NORMAL, RDA_FLAG_DISCOVERY_CREATED})+
			" GROUP BY h.status",
			sqlParts.getNamedParams());
		for(Map dbHost : dbHosts) {
			switch (Nest.value(dbHost,"status").asInteger()) {
				case HOST_STATUS_MONITORED:
					Nest.value(status,"hosts_count_monitored").$(Nest.value(dbHost,"cnt").asInteger());
					break;
				case HOST_STATUS_NOT_MONITORED:
					Nest.value(status,"hosts_count_not_monitored").$(Nest.value(dbHost,"cnt").asInteger());
					break;
				case HOST_STATUS_TEMPLATE:
					Nest.value(status,"hosts_count_template").$(Nest.value(dbHost,"cnt").asInteger());
					break;
			}
		}
		Nest.value(status,"hosts_count").$(
				Nest.value(status,"hosts_count_monitored").asInteger() 
			+ Nest.value(status,"hosts_count_not_monitored").asInteger()
			+ Nest.value(status,"hosts_count_template").asInteger()
		);

		// users
		sqlParts = new SqlBuilder();
		Map row = DBfetch(DBselect(executor,
				"SELECT COUNT(*) AS usr_cnt"+
				" FROM users u"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "users", "u"),
				sqlParts.getNamedParams()
		));
		Nest.value(status,"users_count").$(Nest.value(row,"usr_cnt").asInteger());
		Nest.value(status,"users_online").$(0);

		sqlParts = new SqlBuilder();
		CArray<Map> db_sessions = DBselect(executor,
				"SELECT s.userid,s.status,MAX(s.lastaccess) AS lastaccess"+
				" FROM sessions s"+
				" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "sessions", "s")+
				   " AND s.status="+RDA_SESSION_ACTIVE+
				" GROUP BY s.userid,s.status",
			sqlParts.getNamedParams()
		);
		for(Map session : db_sessions) {
			if ((Nest.value(session,"lastaccess").asLong() + RDA_USER_ONLINE_TIME) >= time()) {
				Nest.value(status,"users_online").$(Nest.value(status,"users_online").asInteger()+1);
			}
		}

		// comments: !!! Don't forget sync code with C !!!
		sqlParts = new SqlBuilder();
		row = DBfetch(DBselect(executor,
				"SELECT SUM(CAST(1.0/i.delay AS DECIMAL(20,10))) AS qps"+
					" FROM items i,hosts h"+
					" WHERE "+sqlParts.dual.dbConditionTenants(idBean, "items", "i")+
					" AND i.status="+ITEM_STATUS_ACTIVE+
					" AND i.tenantid=h.tenantid"+
					" AND i.hostid=h.hostid"+
					" AND h.status="+HOST_STATUS_MONITORED+
					" AND i.delay<>0"+
					" AND i.flags<>"+RDA_FLAG_DISCOVERY_PROTOTYPE,
				sqlParts.getNamedParams()
		));
		Nest.value(status,"qps_total").$(round(Nest.value(row,"qps").asFloat(), 2));
		return status;
	}
	
	public static String get_update_doll_script(String _pmasterid, String _dollid, String _key, String _value) {
		return "PMasters["+rda_jsvalue(_pmasterid)+"].dolls["+rda_jsvalue(_dollid)+"]."+_key+"("+rda_jsvalue(_value)+");";
	}
	
	public static String get_update_doll_script(String _pmasterid, String _dollid, String _key) {
		return get_update_doll_script(_pmasterid, _dollid, _key, "");
	}
	
	public static void make_refresh_menu(String _pmid, String _dollid, double _cur_interval, Object _params, CArray _menu, CArray _submenu) {
		make_refresh_menu(_pmid, _dollid, _cur_interval, _params, _menu, _submenu, 1);
	}
	
	public static void make_refresh_menu(String _pmid, String _dollid, double _cur_interval, Object _params, CArray _menu, CArray _submenu, int _menu_type) {
		CArray<Object> _intervals = array();
		String _title = null;
		if (_menu_type == 1) {
			_intervals = map("10", 10, "30", 30, "60", 60, "120", 120, "600", 600, "900", 900);
			_title = _("Refresh time in seconds");
		}
		else if (_menu_type == 2) {
			_intervals = map("x0.25", 0.25, "x0.5", 0.5, "x1", 1, "x1.5", 1.5, "x2", 2, "x3", 3, "x4", 4, "x5", 5);
			_title = _("Refresh time multiplier");
		}

		Nest.value(_menu, "menu_"+_dollid).push( array(_title, null, null, map("outer" , array("pum_oheader"), "inner" , array("pum_iheader"))) );

		for(Entry<Object, Object> entry: _intervals.entrySet()) {
			Object _key = entry.getKey();
			Double _value = asDouble(entry.getValue());
			
			Nest.value(_menu, "menu_"+_dollid).push( array(
				_key,
				"javascript: setRefreshRate("+rda_jsvalue(_pmid)+", "+rda_jsvalue(_dollid)+", "+_value+", "+rda_jsvalue(_params)+");"+
				"void(0);",
				null,
				map("outer", (_value == _cur_interval) ? "pum_b_submenu" : "pum_o_submenu", "inner", array("pum_i_submenu")
			)) );
		}
		Nest.value(_submenu, "menu_"+_dollid).push( array() );
	}
	
	/************* DYNAMIC REFRESH *************/
	public static void add_doll_objects(CArray<Map> _ref_tab) {
		add_doll_objects(_ref_tab, "mainpage");
	}
	
	public static void add_doll_objects(CArray<Map> _ref_tab, String _pmid) {
		CArray _upd_script = array();
		for(Map _doll: _ref_tab) {
			_upd_script.put(_doll.get("id"), format_doll_init(_doll));
		}
		rda_add_post_js("initPMaster("+rda_jsvalue(_pmid)+", "+rda_jsvalue(_upd_script)+");");
	}
	
	public static CArray format_doll_init(Map _doll) {
		CArray<Object> _args = map(
			"frequency" , 60,
			"url" , "",
			"counter" , 0,
			"darken" , 0,
			"params" , array()
		);
		CArray _obj = array();
		for(Entry<Object, Object> entry: _args.entrySet()) {
			Object _key = entry.getKey();
			Object _def = entry.getValue();
			if (isset(_doll.get(_key))) {
				_obj.put(_key, _doll.get(_key));
			}
			else {
				_obj.put(_key, _def);
			}
		}
		Nest.value(_obj,"url").plus((rda_empty(Nest.value(_obj,"url").$()) ? "?" : "&")+"output=html");
		Nest.value(_obj,"params","favobj").$("hat");
		Nest.value(_obj,"params","favref").$(Nest.value(_doll,"id").$());
		Nest.value(_obj,"params","favaction").$("refresh");

		return _obj;
	}
	
	/**
	 * Splitting string using slashes with escape backslash support.
	 *
	 * @param string _path				string path to parse
	 * @param bool   _stripSlashes		remove escaped slashes from the path pieces
	 *
	 * @return array
	 */
	public static List<String> splitPath(String path) {
		return splitPath(path, true);
	}
	
	public static List<String> splitPath(String path, boolean stripSlashes) {
		List<String> items = new ArrayList();
		StringBuilder s = new StringBuilder();
		StringBuilder escapes = new StringBuilder();
		char[] bytes = path==null?(new char[0]):path.toCharArray();
		int size = bytes.length;
		for (int i = 0; i < size; i++) {
			if (bytes[i] == '/') {
				if (escapes.length()==0) {
					items.add(s.toString());
					s.setLength(0);
				} else {
					String es = escapes.toString();
					if (strlen(es) % 2 == 0) {
						s.append(stripSlashes ? stripslashes(es) : es);
						items.add(s.toString());
						s.setLength(0);
						escapes.setLength(0);
					} else {
						s.append(stripSlashes ? stripslashes(es) : es).append(bytes[i]);
						escapes.setLength(0);
					}
				}
			} else if (bytes[i] == '\\') {
				escapes.append(bytes[i]);
			} else {
				s.append(stripSlashes ? stripslashes(escapes.toString()) : escapes.toString()).append(bytes[i]);
				escapes.setLength(0);
			}
		}

		if (escapes.length()>0) {
			s.append(stripSlashes ? stripslashes(escapes.toString()) : escapes.toString());
		}
		items.add(s.toString());
		return items;
	}
	
	public static void rda_subarray_push(CArray mainArray, int sIndex) {
		rda_subarray_push(mainArray, sIndex, null);		
	}

	public static void rda_subarray_push(CArray mainArray, int sIndex, Object element) {
		rda_subarray_push(mainArray, sIndex, element, null);		
	}
	
	public static void rda_subarray_push(CArray mainArray, int sIndex, Object element, Object key) {
		if (!isset(mainArray,sIndex)) {
			Nest.value(mainArray,sIndex).$(array());
		}
		if (!empty(key)) {
			Nest.value(mainArray,sIndex,key).$(is_null(element) ? sIndex : element);
		} else {
			Nest.value(mainArray,sIndex).asCArray().add(is_null(element) ? sIndex : element);
		}
	}

	public static CArray order_macros(CArray asCArray, String string) {
		// TODO Auto-generated method stub
		return asCArray;
	}

	public static String dowHrMinToStr(Long asLong) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String dowHrMinToStr(Long asLong, boolean a) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String rda_num2bitstr(long num) {
		return rda_num2bitstr(num,false);
	}
	
	public static String rda_num2bitstr(long num, boolean rev) {
		int sbin = 0;
		StringBuilder strbin = new StringBuilder();

		int len = 32;
		if (num > 2147483647L) {
			len = 64;
		}

		char bit;
		for (int i = 0; i < len; i++) {
			sbin = 1 << i;
			bit = (sbin & num)>0 ? '1' : '0';
			if (rev) {
				strbin.append(bit);
			} else {
				strbin.insert(0, bit);
			}
		}

		return strbin.toString();
	}

	/**
	 * 实现字符串反转功能
	 * 
	 * @param _dayofweek
	 * @return
	 */
	public static String rda_str_revert(String _dayofweek) {
		char[] cs = _dayofweek.toCharArray();
		ArrayUtils.reverse(cs);
		return new String(cs);
	}

	public static boolean hasErrorMesssages() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static CArray<Map> selectByPattern(CArray<Map> table, String column, String pattern, int limit) {
		int chunk_size = limit;

		CArray<Map> rsTable = array();
		for (Entry<Object, Map> e : table.entrySet()) {
		    Object num = e.getKey();
		    Map row = e.getValue();
			if (rda_strtoupper(Nest.value(row,column).asString()).equals(rda_strtoupper(pattern))) {
				rsTable = CMapOperator.add((CArray)map(num, row), rsTable);
			} else if (limit > 0) {
				Nest.value(rsTable, num).$(row);
			} else {
				continue;
			}
			limit--;
		}

		if (!empty(rsTable)) {
			rsTable = array_slice(rsTable, 0, chunk_size, true);
		}

		return rsTable;
	}
	
	public static boolean natksort(CArray _array) {
		CArray _keys = array_keys(_array);
		natcasesort(_keys);

		CArray _new_array = array();

		for(Object k : _keys) {
			_new_array.put(k,_array.get(k));
		}

		_array.clear();
		_array.copy(_new_array);
		return true;
	}
	
	public static void jsRedirect(String url) {
		jsRedirect(url, null);
	}
	
	public static void jsRedirect(String url, Integer timeout) {
		String script = is_numeric(timeout)
			? "setTimeout('window.location=\""+url+"\"', "+(timeout * 1000)+")"
			: "window.location.replace(\""+url+"\");";

		insert_js(script);
	}
	
	public static void imageOut(BufferedImage _image) {
		imageOut(_image, null);
	}
	
	public static void imageOut(BufferedImage _image, String _format) {
		if (is_null(_format)) {
			_format = IMAGE_FORMAT_PNG;
		}
		int type = Nest.value(RadarContext.page(),"type").asInteger();
		String _imageId = null;
		if (PAGE_TYPE_IMAGE != type) {
//			_imageId = md5(strlen(_imageSource));
//			$_SESSION['image_id'] = array();
//			$_SESSION['image_id'][_imageId] = _imageSource;
		}
		switch (type) {
			case PAGE_TYPE_IMAGE:
				try {
					ImageIO.write(_image, _format, RadarContext.response().getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case PAGE_TYPE_JSON:
				CJSON _json = new CJSON();
				echo(_json.encode(map("result", _imageId)));
				break;
			case PAGE_TYPE_TEXT:
			default:
				echo(_imageId);
		}
		
	}

	public static CArray checkRequiredKeys(Map _httpTest, CArray<String> array) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String order_by(IIdentityBean idBean, SQLExecutor executor, String def) {
		return order_by(idBean, executor, def, "");
	}

	public static String order_by(IIdentityBean idBean, SQLExecutor executor, String def, String allways) {
		String orderString = "";

		String sortField = getPageSortField(idBean, executor);
		String[] sortable = explode(",", def);
		if (!str_in_array(sortField, CArray.valueOf(sortable))) {
			sortField = null;
		}
		if (sortField != null) {
			String sortOrder = getPageSortOrder(idBean, executor);
			orderString += sortField+' '+sortOrder;
		}
		if (!empty(allways)) {
			orderString += (sortField == null) ? "" : ",";
			orderString += allways;
		}

		return empty(orderString) ? "" : " ORDER BY "+orderString;
	}

	public static Object encode_log(String trim) {
		// TODO Auto-generated method stub
		return trim;
	}
	
	public static String rda_toCSV(CArray _values) {
		String _csv = "";
		String _glue = "\",\"";
		for(Object row: _values) {
			CArray<String> _row;
			if (!is_array(row)) {
				_row = array((String)row);
			}else {
				_row = (CArray)row;
			}
			
			Iterator<Entry<Object, String>> iterator = _row.entrySet().iterator();
			while(iterator.hasNext()) {
				Entry<Object, String> entry = iterator.next();
				Object _num = entry.getKey();
				String _value = entry.getValue();
				
				if (is_null(_value)) {
//					unset(_row, _num);
					iterator.remove();
				}
				else {
					Nest.value(_row, _num).$( str_replace("\"", "\"\"", _value) );
				}
			}
			_csv += "\""+implode(_glue, _row.toArray())+"\""+"\n";
		}

		return _csv;
	}
	
}
