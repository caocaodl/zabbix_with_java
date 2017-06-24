package com.isoft.iradar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.Factory;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.runtime.parser.node.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isoft.iradar.core.utils.EasyObject;
import com.isoft.iradar.core.utils.EasyServlet;
import com.isoft.iradar.inc.FuncsUtil;
import com.isoft.jdk.javascript.NativeJavaFunction;
import com.isoft.jdk.javascript.NativeMap;
import com.isoft.jdk.util.compare.NaturalOrderComparator;
import com.isoft.jdk.util.regex.IMatcher;
import com.isoft.jdk.util.regex.IPattern;
import com.isoft.lang.Clone;
import com.isoft.lang.CodeConfirmed;
import com.isoft.struts2.util.ArrayUtils;
import com.isoft.types.CArray;
import static com.isoft.types.CArray.*;
import com.isoft.types.CMap;
import com.isoft.types.IMap;
import com.isoft.types.Mapper.Nest;
import com.isoft.zend.ZendUtils;

public class Cphp {
	private static final Logger LOG = LoggerFactory.getLogger(Cphp.class);
	
	public final static int PREG_PATTERN_ORDER =  1;
	public final static int PREG_SET_ORDER =  2;
	public final static int PREG_OFFSET_CAPTURE =  256;
	public final static int PREG_SPLIT_NO_EMPTY =  1;
	public final static int PREG_SPLIT_DELIM_CAPTURE =  2;
	public final static int PREG_SPLIT_OFFSET_CAPTURE =  4;
	public final static int PREG_GREP_INVERT =  1;
	public final static int PREG_NO_ERROR =  0;
	public final static int PREG_INTERNAL_ERROR =  1;
	public final static int PREG_BACKTRACK_LIMIT_ERROR =  2;
	public final static int PREG_RECURSION_LIMIT_ERROR =  3;
	public final static int PREG_BAD_UTF8_ERROR =  4;
	public final static int PREG_BAD_UTF8_OFFSET_ERROR =  5;
	public final static String PCRE_VERSION =  "7.9 2009-04-11";

	public final static int SORT_ASC = 4;
	public final static int SORT_DESC = 3;
	public final static int SORT_REGULAR = 0;
	public final static int SORT_NUMERIC = 1;
	public final static int SORT_STRING = 2;
	public final static int SORT_LOCALE_STRING = 5;
	
	private static int BCSCALE = 7;  

	public static Map _page() {
		return RadarContext.page();
	}
	
	public static CMap<String, Object> $_REQUEST() {
		return (CMap)RadarContext._REQUEST();
	}
	public static Object $_REQUEST(Object key) {
		return RadarContext._REQUEST(String.valueOf(key));
	}
	public static void $_REQUEST(Object key, Object value) {
		RadarContext._REQUEST().put(String.valueOf(key), value);
	}
	
	public static <M,N> boolean equals(M a, N b) {
		if (a == null) {
			return b == null;
		} else if (b == null) {
			return false;
		} else {
			if (a.equals(b)) {
				return true;
			} else {
				return Nest.as(a).asString().equals(Nest.as(b).asString());
			}
		}
	}
	
	private static Properties FRONTEND = new Properties();
	static {
		InputStream bis = Cphp.class.getResourceAsStream("/frontend.xml");
		try {
			FRONTEND.loadFromXML(bis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isPrimitive(Object obj) {
		if (obj instanceof String 
				|| obj instanceof Boolean 
				|| obj instanceof Character
				|| obj instanceof Byte 
				|| obj instanceof Short
				|| obj instanceof Integer 
				|| obj instanceof Long
				|| obj instanceof Float 
				|| obj instanceof Double
				|| obj instanceof Void) {
			return true;
		}
		return false;
	}

	public static String _(String str) {
		String s = FRONTEND.getProperty(str);
		if(s == null || s.length()==0){
			s = str;
		}
		return s;
	}
	
	public static String _s(String str, Object ... args){
		String s = FRONTEND.getProperty(str);
		if(s == null || s.length()==0){
			s = str;
		}
		return String.format(s, args);
	}
	
	public static String _x(String str, Object ... args){
		String s = FRONTEND.getProperty(str);
		if(s == null || s.length()==0){
			s = str;
		}
		return String.format(s, Arrays.copyOfRange(args, 1, args.length));
	}
	
	public static String _n(String str, Object ... args){
		String s = FRONTEND.getProperty(str);
		if(s == null || s.length()==0){
			s = str;
		}
		return String.format(s, org.apache.commons.lang.ArrayUtils.subarray(args, 1, args.length));
	}
	
	public static boolean isset(Object value) {
		return value != null;
	}
	public static boolean issets(Object...keys) {
		for(Object k: keys) {
			if(k == null) {
				return false;
			}
		}
		return true;
	}

	public static boolean isset(Map container, Object key) {
		if (container == null) {
			return false;
		}
		if (container.containsKey(key)) {
			if (container.get(key) != null) {
				return true;
			}
		}
		return false;
	}
	
	public static void unset(Map container, Object... keys) {
		if (container==null || keys.length==0) {
			return;
		}
		
		Map c = container;
		for(int i=0, imax=keys.length-1; i<imax; i++) {
			c = (Map)c.get(keys[i]);
			if (c == null) {
				return;
			}
		}
		
		Object lastKey = keys[keys.length-1];
		if (c.containsKey(lastKey)) {
			c.remove(lastKey);
		}
	}
	
	public static void unsets(Map container, Object... keys) {
		for(Object key: keys) {
			unset(container, String.valueOf(key));
		}
	}
	
	/**
	 * 将 array 的内部指针倒回到第一个单元并返回第一个数组单元的值。
	 * 
	 * @param array	输入的数组。
	 * @return	返回数组第一个单元的值，如果数组为空则返回 FALSE。
	 */
	public static <T> T reset(List<T> array) {
		if(array==null || array.isEmpty()) return null; //FIXME: 把FALSE当成NULL吧，兄弟
		return array.get(0);
	}
	
	/**
	 * 将 array 的内部指针倒回到第一个单元并返回第一个数组单元的值。
	 * 
	 * @param array	输入的数组。
	 * @return	返回数组第一个单元的值，如果数组为空则返回 FALSE。
	 */
	public static <T> T reset(Map<?, T> array) {
		if(empty(array)) return null;
		return array.values().iterator().next();
	}
	
	/**
	 * 将 array 的内部指针倒回到第一个单元并返回第一个数组单元的值。
	 * 
	 * @param array	输入的数组。
	 * @return	返回数组第一个单元的值，如果数组为空则返回 FALSE。
	 */
	public static <T> T reset(T[] array) {
		if (array == null || array.length == 0){
			return null;
		}
		return array[0];
	}
	
	public static <T> T end(Collection<T> m) {
		if(empty(m)) return null;
		Object[] vs = m.toArray();
		return (T)vs[vs.length-1];
	}
	
	public static <T> T array_shift(CArray<T> array) {
		if(empty(array)){
			return null;
		}
		return array.shift();
	}
	public static int array_unshift(CArray array, Object...vars) {
		return array.unshift(vars).size();
	}

	public static boolean is_null(Object obj) {
		return obj == null;
	}
	
	/**
	 * 检测变量是否为数字或数字字符串
	 * 
	 * @param o
	 * @return
	 */
	public static boolean is_numeric(Object o) {
		if(o == null) return false;
		if(o instanceof Number) return true;
		if(o instanceof String) {
			String t = (String) o;
			if(t.length()>0){
				if(t.length()>1 && t.startsWith("0") && !t.startsWith("0.")) { //有的字段如颜色000000，不应该是数字；增加对0.0的过滤
					return false;
				}
				char[] cs = t.toCharArray();
				for (int i = 0; i < cs.length; i++) {
					if(Character.isSpaceChar(cs[i]) || cs[i] == '-'){
						continue;
					}
					return NumberUtils.isNumber(String.valueOf(cs, i, cs.length-i));
				}
			}
		}
		return false;
	}
	
	/**
	 * 检查提供的 string 和 text 里面的字符是不是都是数字。
	 * 
	 * @param o
	 * @return
	 */
	public static boolean ctype_digit(Object o) {
		return NumberUtils.isDigits(String.valueOf(o));
	}
	
	@Deprecated
	public static Integer toInt(Object o) {
		if(o instanceof Number) {
			return ((Number)o).intValue();
		}else if(o instanceof String) {
			return Integer.valueOf((String)o);
		}
		return null;
	}

	public static String[] explode(String separator, String str) {
		return explode(separator, str, 0);
	}

	public static String[] explode(String separator, String str, int limit) {
		return str.split(separator, limit);
	}

	public static String implode(String separator, Object[] array) {
		return StringUtils.join(array, separator);
	}
	public static String implode(String separator, CArray array) {
		return implode(separator, array.toArray());
	}

	public static <R,T> boolean inArray(R obj, T[] array) {
		if (obj == null || array == null || array.length == 0) {
			return false;
		}
		T tobj = (T)ConvertUtils.convert(obj, array[0].getClass());
		for (T t : array) {
			if (tobj.equals(t)) {
				return true;
			}
		}
		return false;
	}

	public static boolean strInArray(Object obj, String[] array) {
		if (obj == null || array == null || array.length == 0) {
			return false;
		}
		if (obj instanceof String) {
			for (String str : array) {
				if (str.equals(obj)) {
					return true;
				}
			}
		} else if (isJavaArray(obj)) {
			String[] objs = (String[]) obj;
			for (String s : objs) {
				if (!strInArray(s, array)) {
					return false;
				}
			}
		}
		return false;
	}

	private static boolean isJavaArray(Object obj) {
		if (obj == null) {
			return false;
		} else {
			return obj.getClass().isArray();
		}
	}
	
	public static boolean isArray(Object obj) {
		if(obj == null) return false;
		
		boolean r = obj.getClass().isArray();
		r |= obj instanceof CArray;
		r |= obj instanceof Map;
		r |= obj instanceof List;
		return r;
	}
	public static boolean is_array(Object obj) {
		return isArray(obj);
	}
	
	public static boolean in_array(Object needle , CArray haystack) {
		if(haystack.isEmpty()){
			return false;
		}
		if (needle != null && isPrimitive(needle)) {
			Object cv = null;
			for(Object v : haystack){
				if(v!=null){
					cv = ConvertUtils.convert(needle, v.getClass());
					if(v.equals(cv)){
						return true;
					}
				}				
			}
			return false;
		}
		return haystack.containsValue(needle);
	}
	
	public static <T> boolean in_array(T obj, T[] array) {
		if (obj == null || array == null || array.length == 0) {
			return false;
		}
		for (T t : array) {
			if (obj.equals(t)) {
				return true;
			}
		}
		return false;
	}

	public static boolean defined(String constantVar) {
		RadarContext ctx = RadarContext.getContext();
		return ctx.defined(constantVar);
	}
	
	public static Object define(String constantVar) {
		RadarContext ctx = RadarContext.getContext();
		return ctx.define(constantVar);
	}

	public static void define(String constantVar, Object value) {
		RadarContext ctx = RadarContext.getContext();
		ctx.define(constantVar, value);
	}
	
	public static boolean is_bool(Object o) {
		return o instanceof Boolean;
	}

	public static boolean is_object(Object obj) {
		if (obj == null) {
			return false;
		} else if (isPrimitive(obj)) {
			return false;
		} 
		return true;
	}
	
	public static String get_class(Object o) {
		return o.getClass().getSimpleName();
	}
	
	public static boolean is_string(Object obj) {
		return obj instanceof String;
	}
	
	public static boolean is_int(Object o) {
		return o instanceof Integer;
	}

	public static boolean empty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof String) {
			if(((String) obj).length() == 0) {
				return true;
			} else {
				if (is_numeric(obj)) {
					obj = NumberUtils.createNumber((String)obj);
				}
			}
		}
//		 * @see     java.lang.Byte
//		 * @see     java.lang.Double
//		 * @see     java.lang.Float
//		 * @see     java.lang.Integer
//		 * @see     java.lang.Long
//		 * @see     java.lang.Short
		if (obj instanceof Number
				&& (obj.equals(0) 
					|| obj.equals((long) 0)
					|| obj.equals((float) 0) 
					|| obj.equals((double) 0)
					|| obj.equals((short) 0) 
					|| obj.equals((byte) 0)
					|| obj.equals(Long.valueOf(0l)))) {
			return true;
		}
		if (obj instanceof Boolean && (!(Boolean) obj)) {
			return true;
		}
		if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
			return true;
		}
		if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
			return true;
		}
		if(obj instanceof Map && ((Map)obj).isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static int count(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		}
		if (obj instanceof Collection) {
			return ((Collection) obj).size();
		}
		if (obj instanceof Map) {
			return ((Map) obj).size();
		}
		return 1;
	}
	
	public static CArray getdate() {
		return getdate(time());
	}
	
	public static CArray getdate(long _timestamp) {
		Date d = _time(_timestamp);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CArray r = array();
		r.put("seconds"  ,c.get(Calendar.SECOND));
		r.put("minutes"  ,c.get(Calendar.MINUTE));
		r.put("hours" ,c.get(Calendar.HOUR_OF_DAY));
		r.put("mday" ,c.get(Calendar.DAY_OF_MONTH));
		r.put("mon"  ,c.get(Calendar.MONTH)+1);
		r.put("year" ,c.get(Calendar.YEAR));
		r.put("wday" ,c.get(Calendar.DAY_OF_WEEK)-1);
		r.put("yday" ,c.get(Calendar.DAY_OF_YEAR));
		r.put("weekday", c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()));
		r.put("month", c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
		r.put(0, _timestamp);
		return r;
	}
	
	public static <T> T microtime() {
		return (T)microtime(false);
	}
	public static <T> T microtime(boolean _get_as_float) {
		long now = System.currentTimeMillis();
		double sec = now / TIME_PHP2JAVA;
		double micro = now % TIME_PHP2JAVA;
		
		Object o;
		if(_get_as_float) {
			o = (sec + micro/TIME_PHP2JAVA); 
		}else {
			o = String.format("%d %d", micro, sec);
		}
		return (T)o;
	}

	public final static int TIME_PHP2JAVA = 1000;
	public static Date _time(Long phpTime) {
		if(phpTime == null) return new Date();
		return new Date(phpTime*TIME_PHP2JAVA);
	}
	
	public static long time(Long time) {
		return ZendUtils.time(time);
	}
	
	public static long time() {
		return ZendUtils.time();
	}
	
	public static long strtotime(String time){
		return ZendUtils.strtotime(time);
	}
	
	public static long strtotime(String time, Long  preset_ts){
		return ZendUtils.strtotime(time, preset_ts);
	}
	
	/**
	 * 根据给出的参数返回 Unix 时间戳。时间戳是一个长整数，包含了从 Unix 纪元（January 1 2010 00:00:00
	 * GMT）到给定时间的秒数。 参数可以从右向左省略，任何省略的参数会被设置成本地日期和时间的当前值。
	 * 
	 * @param _hours
	 * @param _minutes
	 * @param _seconds
	 * @param _month
	 *            月份数。 The number of the month relative to the end of the
	 *            previous year. Values 1 to 12 reference the normal calendar
	 *            months of the year in question. Values less than 1 (including
	 *            negative values) reference the months in the previous year in
	 *            reverse order, so 0 is December, -1 is November, etc. Values
	 *            greater than 12 reference the appropriate month in the
	 *            following year(s).
	 * @param _date
	 * @param _year
	 * @return
	 */
	public static long mktime(Integer _hours, Integer _minutes, Integer _seconds, Integer _month, Integer _date, Integer _year) {
		if(_hours==null || _minutes==null || _seconds==null || _month==null || _date==null || _year==null) {
			return -1;
		}
		
		Calendar cale = Calendar.getInstance();
		cale.set(_year, _month-1, _date, _hours, _minutes, _seconds);
		return time(cale.getTimeInMillis());
	}
	
	public static CArray localtime() {
		return localtime(time());
	}
	public static CArray localtime(long _timestamp) {
		return localtime(_timestamp, false);
	}

	/**
	 * localtime() 函数返回一个数组，其结构和 C 函数调用返回的完全一样。
	 * 
	 * @param _timestamp
	 *            可选的 timestamp 参数是一个 integer 的 Unix
	 *            时间戳，如未指定，参数值默认为当前本地时间。也就是说，其值默认为 time() 的返回值。
	 * @param _is_associative
	 *            如果设为 FALSE 或未提供则返回的是普通的数字索引数组。如果该参数设为 TRUE 则 localtime()
	 *            函数返回包含有所有从 C 的 localtime 函数调用所返回的不同单元的关联数组。关联数组中不同的键名为：
	 * 
	 *            "tm_sec"  - 秒数， 0 到 59 
	 *            "tm_min"  - 分钟数， 0 到 59 
	 *            "tm_hour" - 小时， 0 到 23 
	 *            "tm_mday" - 月份中的第几日， 1 到 31 
	 *            "tm_mon"  - 年份中的第几个月， 0 (Jan) 到 11 (Dec) 
	 *            "tm_year" - 年份，从 1900 开始 
	 *            "tm_wday" - 星期中的第几天， 0 (Sun) 到 6 (Sat) 
	 *            "tm_yday" - 一年中的第几天， 0 到 365 
	 *            "tm_isdst" - 夏令时当前是否生效？ 如果是生效的是正数， 0 代表未生效，负数代表未知。
	 * @return
	 */
	public static CArray<Integer> localtime (long _timestamp, boolean _is_associative) {
		Date d = _time(_timestamp);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		CArray r = array();
		if(_is_associative) {
			r.put("tm_sec"  ,c.get(Calendar.SECOND));
			r.put("tm_min"  ,c.get(Calendar.MINUTE));
			r.put("tm_hour" ,c.get(Calendar.HOUR));
			r.put("tm_mday" ,c.get(Calendar.DAY_OF_MONTH));
			r.put("tm_mon"  ,c.get(Calendar.MONTH));
			r.put("tm_year" ,c.get(Calendar.YEAR));
			r.put("tm_wday" ,c.get(Calendar.DAY_OF_WEEK));
			r.put("tm_yday" ,c.get(Calendar.DAY_OF_YEAR));
			r.put("tm_isdst",c.get(Calendar.DST_OFFSET));
		}else {
			r.add(c.get(Calendar.SECOND));
			r.add(c.get(Calendar.MINUTE));
			r.add(c.get(Calendar.HOUR));
			r.add(c.get(Calendar.DAY_OF_MONTH));
			r.add(c.get(Calendar.MONTH));
			r.add(c.get(Calendar.YEAR));
			r.add(c.get(Calendar.DAY_OF_WEEK));
			r.add(c.get(Calendar.DAY_OF_YEAR));
			r.add(c.get(Calendar.DST_OFFSET));
		}
		return r;
	}
	
	public static String substr(String s, int start){
		return substr(s, start, s.length()-start);
	}
	
	/**
	 * 返回字符串 string 由 start 和 length 参数指定的子字符串。
	 * 
	 * @param s
	 * @param start
	 *            如果 start 是非负数，返回的字符串将从 string 的 start 位置开始，从 0 开始计算。例如，在字符串
	 *            “abcdef” 中，在位置 0 的字符是 “a”，位置 2 的字符串是 “c” 等等。
	 * 
	 *            如果 start 是负数，返回的字符串将从 string 结尾处向前数第 start 个字符开始。
	 * 
	 *            如果 string 的长度小于或等于 start，将返回 FALSE。
	 * @param length
	 *            如果提供了正数的 length，返回的字符串将从 start 处开始最多包括 length 个字符（取决于 string
	 *            的长度）。
	 * 
	 *            如果提供了负数的 length，那么 string 末尾处的许多字符将会被漏掉（若 start
	 *            是负数则从字符串尾部算起）。如果 start 不在这段文本中，那么将返回一个空字符串。
	 * 
	 *            如果提供了值为 0，FALSE 或 NULL 的 length，那么将返回一个空字符串。
	 * 
	 *            如果没有提供 length，返回的子字符串将从 start 位置开始直到字符串结尾。
	 * @return
	 */
	public static String substr(String s, int start, int length){
		if (s == null) {
			return s;
		}
		if (s.length() == 0) {
			return s;
		}
		
		if(length < 0) {
			length = s.length()+length-start;
		}
		
		if ((start + length) < s.length()) {
			return s.substring(start, start + length);
		} else {
			return s.substring(start);
		}
	}
	
	/**
	 * substr_replace() 在字符串 string 的副本中将由 start 和可选的 length 参数限定的子字符串使用 replacement 进行替换。
	 * 
	 * @param _string
	 *            输入字符串。
	 * 
	 *            An array of strings can be provided, in which case the
	 *            replacements will occur on each string in turn. In this case,
	 *            the replacement, start and length parameters may be provided
	 *            either as scalar values to be applied to each input string in
	 *            turn, or as arrays, in which case the corresponding array
	 *            element will be used for each input string.
	 * @param _replacement
	 *            替换字符串。
	 * @param _start
	 *            如果 start 为正数，替换将从 string 的 start 位置开始。
	 * 
	 *            如果 start 为负数，替换将从 string 的倒数第 start 个位置开始。
	 * @param _length
	 *            length 如果设定了这个参数并且为正数，表示 string
	 *            中被替换的子字符串的长度。如果设定为负数，它表示待替换的子字符串结尾处距离 string
	 *            末端的字符个数。如果没有提供此参数，那么它默认为 strlen( string ) （字符串的长度）。当然，如果
	 *            length 为 0，那么这个函数的功能为将 replacement 插入到 string 的 start 位置处。
	 * @return
	 */
	public static String substr_replace(String _string ,String _replacement, int _start, int length) {
		_start = _start<0? _string.length()+_start: _start;
		return _string.substring(0, _start)+ _replacement + _string.substring(_start+length, _string.length()); 
	}
	
	public static String basename(RadarContext ctx){
		String uri = ctx.getRequest().getRequestURI();
		return uri.substring(uri.lastIndexOf('/')+1);
	}
	
	public static String basename(String path, String suffix) {
		if (path != null && suffix!=null && path.endsWith(suffix)) {
			path = path.substring(0, path.length() - suffix.length());
		}
		return path;
	}

	public static String file_get_contents(RadarContext ctx, String file) {
		String path = EasyServlet.getRealPath(ctx.getRequest(), "/platform/iradar/" + file);
		byte[] bytes = null;
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			int len = is.available();
			bytes = new byte[len];
			int off = 0;
			int size = 0;
			while ((size = is.read(bytes, off, len)) > 0) {
				off += size;
				len -= size;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (bytes != null) {
			return new String(bytes);
		} else {
			return "";
		}
	}
	
	public static Object[] array_unshift(Object[] src, Object obj) {
		Object[] dest = new Object[src.length+1];
		dest[0] = obj;
		System.arraycopy(src, 0, dest, 1, src.length);
		return dest;
	}
	
	public static String sprintf(String format, Object...args) {
		return vsprintf(format, args);
	}
	
	public static String urlencode(String url) {
		try {
			return URLEncoder.encode(url, "utf8");
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}
	
	public static String vsprintf(String format, Object...args) {
		return String.format(format, args);
	}
	
	/**
	 * 这个函数 sscanf() 输入类似 printf()。 sscanf() 读取字符串str 然后根据指定格式format解析, 格式的描述文档见
	 * sprintf()。
	 * 
	 * 指定的格式字符串中的任意空白匹配输入字符串的任意空白.也就是说即使是格式字符串中的一个制表符 \t 也能匹配输入 字符串中的一个单一空格字符
	 * 
	 * @param _str
	 *            将要被解析的 字符串.
	 * @param _format
	 *            The interpreted format for 解析str的格式, 除了以下不同外，其余的见
	 *            sprintf()的描述文档:
	 * 
	 *            函数不区分语言地区 F, g, G 和 b 不被支持. D 表示十进制数字. i stands for integer
	 *            with base detection. n stands for number of characters
	 *            processed so far. ... 可以选参数将以引用方式传入，它们的值将被设置为解析匹配的值
	 * @return
	 */
	public static CArray<String> sscanf(String _str , String _format) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(_str);
			scanner.useDelimiter(_format);
			CArray<String> r = array();
			while (scanner.hasNext()) {
				r.add(scanner.next());
			}
			return r;
		} finally {
			if(scanner!=null){
				scanner.close();
			}
		}
	}
	
	/**
	 * array_flip() 返回一个反转后的 array，例如 trans 中的键名变成了值，而 trans 中的值成了键名。
		注意 trans 中的值需要能够作为合法的键名，例如需要是 integer 或者 string。如果值的类型不对将发出一个警告，并且有问题的键／值对将不会反转。
		如果同一个值出现了多次，则最后一个键名将作为它的值，所有其它的都丢失了。
	 * 
	 * @param array
	 * @return
	 */
	public static <T> CArray<T> array_flip(CArray<T> array) {
		CArray<T> result = new CArray<T>();
		for(Entry<Object,T> entry: array.entrySet()) {
			result.put(entry.getValue(), entry.getKey());
		}
		return result;
	}
	
	
	
	/**
	 * array_keys() 返回 input 数组中的数字或者字符串的键名。
		如果指定了可选参数 search_value，则只返回该值的键名。否则 input 数组中的所有键名都会被返回。
	 * 
	 * @param array
	 * @return
	 */
	public static <T> CArray<Object> array_keys(CArray<T> array, T search_value, boolean strict){
		CArray<Object> keys = new CArray<Object>();
		for(Entry<Object, T> e: array.entrySet()) {
			boolean hit = search_value==null || search_value.equals(e.getValue());
			if(hit) {
				keys.add(e.getKey());
			}
		}
		return keys;
	}
	public static <T> CArray<Object> array_keys(CArray<T> array){
		return array_keys(array, null, false);
	}
	public static <T> CArray<Object> array_keys(CArray<T> array, T search_value){
		return array_keys(array, search_value, false);
	}
	
	public static String[] array_merge(String[] as, String[] bs){
		return com.isoft.util.StringUtil.mergeStringArrays(as, bs);
	}
	
	public static Long[] array_merge(Long[] as, Long[] bs){
		return array_merge(CArray.valueOf(as),CArray.valueOf(bs)).valuesAsLong();
	}
	
	@CodeConfirmed("benne")
	public static CArray array_merge(CArray... adds) {
		if(adds == null || adds.length==0){
			return null;
		}
		CArray merge = new CArray();
		for(CArray<Object> add: adds) {
			if(add == null) {
				continue;
			}
			for(Entry<Object, Object> entry: add.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key instanceof Number) {
					merge.add(value);
				} else {
					merge.put(key, value);					
				}
			}
		}
		return merge;
	}
	
	@CodeConfirmed("benne")
	public static Map array_merge(Map... adds) {
		if(adds == null || adds.length==0){
			return null;
		}
		CArray merge = new CArray();
		for(Map<Object,Object> add: adds) {
			if(add == null) {
				continue;
			}
			for(Entry<Object, Object> entry: add.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				if (key instanceof Number) {
					merge.add(value);
				} else {
					merge.put(key, value);					
				}
			}
		}
		return merge;
	}
	
	public static CArray array_add(CArray left, Map right){
		if(right!=null){
			for(Object o: right.entrySet()) {
				Entry<Object, Object> entry = (Entry<Object, Object>)o;
				Object key = entry.getKey();
				if(!left.containsKey(key)) {
					Object value = entry.getValue();
					left.put(key, value);
				}
			}
		}
		return left;
	}
	
	public static <T> CArray<T> array_values(CArray<T> input) {
		return CArray.valueOf(input.values());
	}
	
	public static <T> T[] array_valuesN(CArray<T> input) {
		return input.toArray();
	}
	
	public static boolean array_key_exists(Object _key, CArray _search) {
		return _search.containsKey(_key);
	}
	
	public static boolean array_key_exists(Object _key, Map _search) {
		return _search.containsKey(_key);
	}
	
	public static <T> CArray<T> array_reverse(CArray<T> array) {
		return array.reverse();
	}
	public static <T> CArray<T> array_reverse(CArray<T> array, boolean preserve_keys) {
		return array.reverse(preserve_keys);
	}
	
	/**
	 * 返回一个 array，用来自 keys 数组的值作为键名，来自 values 数组的值作为相应的值。
	 * 
	 * @param _keys
	 *            将被作为新数组的键。非法的值将会被转换为字符串类型（string）。
	 * @param _values
	 *            将被作为数组的值。
	 * @return 返回合并的 array，如果两个数组的单元数不同则返回 FALSE。
	 */
	public static CArray array_combine(CArray _keys , CArray _values) {
		if(_keys.size() != _values.size()) {
			return null;
		}
		
		CArray combine = array();
		
		Iterator i_keys = _keys.iterator();
		Iterator i_values = _values.iterator();
		while(i_keys.hasNext() && i_values.hasNext()) {
			combine.put(i_keys.next(), i_values.next());
		}
		return combine;
	}
	
	
	public static interface ArrayMapCallback{
		Object call(Object... objs);
	}
	/**
	 * array_map() 返回一个数组，该数组包含了 arr1 中的所有单元经过 callback 作用过之后的单元。callback
	 * 接受的参数数目应该和传递给 array_map() 函数的数组数目一致。
	 * 
	 * @param callback
	 *            对每个数组的每个元素作用的回调函数。
	 * @param arr1
	 *            将被回调函数（callback）执行的数组。
	 * @param arrs
	 *            将被回调函数（callback）执行的数组列表。
	 * @return 返回一个数组，该数组的每个元素都数组（arr1）里面的每个元素经过回调函数（callback）处理了的。
	 */
	public static CArray array_map(ArrayMapCallback callback, CArray arr1, CArray... arrs) {
		CArray result = array();
		
		Iterator[] iterators = new Iterator[arrs.length+1];
		iterators[0] = arr1.iterator();
		for(int i=0,imax=arrs.length; i<imax; i++) {
			iterators[i+1] = arrs[i].iterator();
		}
		Object[] os = new Object[iterators.length];
		while(iterators[0].hasNext()) {
			for(int i=0,imax=iterators.length; i<imax; i++) {
				os[i] = iterators[i].next();
			}
			result.add(callback.call(os));
		}
		return result;
	}
	
	public static <T> CArray<T> natcasesort(CArray<T> array) {
		return array.sort(new Comparator<Entry<Object,T>>() {
			@Override 
			public int compare(Entry<Object, T> left, Entry<Object, T> right) {
				return ZendUtils.natcasesort(Nest.as(left.getValue()).asString(), Nest.as(right.getValue()).asString());
			}
		}, true);
	}

	/**
	 * 本函数实现了一个和人们通常对字母数字字符串进行排序的方法一样的排序算法并保持原有键／值的关联，这被称为“自然排序”。
	 * 本算法和通常的计算机字符串排序算法（用于 sort()）的区别见下面示例。
	 * 
	 * @param array
	 * @return
	 */
	public static <T> CArray<T> natsort(CArray<T> array) {
		return array.sort(new Comparator<Entry<Object,T>>() {
			@Override 
			public int compare(Entry<Object, T> left, Entry<Object, T> right) {
				return ZendUtils.natsort(Nest.as(left.getValue()).asString(), Nest.as(right.getValue()).asString());
			}
		}, true);
	}
	
	public static <T> CArray<T> ksort(CArray<T> array) {
		return ksort(array, SORT_REGULAR);
	}
	
	/**
	 * 对数组按照键名排序，保留键名到数据的关联。本函数主要用于关联数组。
	 * 
	 * @param array
	 *            输入的数组。
	 * @param _sort_flags
	 *            可以用可选参数 sort_flags 改变排序的行为，详情见 sort()。
	 * 
	 *            SORT_REGULAR - 正常比较单元（不改变类型） 
	 *            SORT_NUMERIC - 单元被作为数字来比较
	 *            SORT_STRING - 单元被作为字符串来比较 
	 *            SORT_LOCALE_STRING - 根据当前的区域（locale）设置来把单元当作字符串比较，可以用 setlocale() 来改变。 
	 *            SORT_NATURAL - 和 natsort() 类似对每个单元以“自然的顺序”对字符串进行排序。 PHP 5.4.0 中新增的。
	 *            SORT_FLAG_CASE - 能够与 SORT_STRING 或 SORT_NATURAL 合并（OR位运算），不区分大小写排序字符串。
	 * @return
	 */
	public static <T> CArray<T> ksort(CArray<T> array, final int _sort_flags) {
		return array.sort(new Comparator<Entry<Object,T>>() {
			@Override public int compare(Entry<Object, T> o1, Entry<Object, T> o2) {
				Object k1 = o1.getKey(), k2 = o2.getKey();
				
				if(_sort_flags == SORT_REGULAR) {
					return String.valueOf(k1).compareTo(String.valueOf(k2));
				}
				
				return String.valueOf(o1.getKey()).toLowerCase().compareTo(String.valueOf(o2.getValue()).toLowerCase());
			}
		}, true);
	}
	
	
	/**
	 * 对比返回在 array1 中但是不在 array2 及任何其它参数数组中的值。
	 * 两个单元仅在 (string) _elem1 === (string) _elem2 时被认为是相同的。也就是说，当字符串的表达是一样的时候。
	 * 
	 * @param _array1
	 * @param others
	 * @return	返回一个数组，该数组包括了所有在 array1 中但是不在任何其它参数数组中的值。注意键名保留不变。
	 */
	public static <T> CArray<T> array_diff(CArray<T> _array1, CArray...others) {
		CArray<T> result = array();
		for(Entry<Object, T> entry: _array1.entrySet()) {
			Object k = entry.getKey();
			T v = entry.getValue();
			
			boolean isHas = false;
			for(CArray other: others) {
				if(isHas) break;
				
				for(Object ov: other.values()) {
					if(String.valueOf(ov).equals(String.valueOf(v))) {
						isHas = true;
						break;
					}
				}
			}
			
			if(!isHas) {
				result.put(k, v);
			}
		}
		return result;
	}
	
	/**
	 * array_diff_assoc() 返回一个数组，该数组包括了所有在 array1 中但是不在任何其它参数数组中的值。注意和 array_diff() 不同的是键名也用于比较。
	 * 
	 * @param _array1
	 * @param others
	 * @return	返回一个数组，该数组包括了所有在 array1 中但是不在任何其它参数数组中的值。注意键名保留不变。
	 */
	public static <T> CArray<T> array_diff_assoc(CArray<T> _array1, CArray...others) {
		CArray<T> result = array();
		for(Entry<Object, T> entry: _array1.entrySet()) {
			Object k = entry.getKey();
			T v = entry.getValue();
			
			boolean isHas = false;
			for(CArray<Object> other: others) {
				if(isHas) break;
				
				for(Entry<Object, Object> entry_other: other.entrySet()) {
					Object ok = entry_other.getKey();
					Object ov = entry_other.getValue();
					if(String.valueOf(ov).equals(String.valueOf(v)) && k.equals(ok)) {
						isHas = true;
						break;
					}
				}
			}
			
			if(!isHas) {
				result.put(k, v);
			}
		}
		return result;
	}
	
	public static Map array_diff_assoc(Map<?, ?> _array1, Map<?, ?>... others) {
		Map result = map();
		for(Entry entry: _array1.entrySet()) {
			Object k = entry.getKey();
			Object v = entry.getValue();
			
			boolean isHas = false;
			for(Map<?, ?> other: others) {
				if(isHas) break;
				
				for(Entry entry_other: other.entrySet()) {
					Object ok = entry_other.getKey();
					Object ov = entry_other.getValue();
					if(String.valueOf(ov).equals(String.valueOf(v)) && k.equals(ok)) {
						isHas = true;
						break;
					}
				}
			}
			
			if(!isHas) {
				result.put(k, v);
			}
		}
		return result;
	}
	
	
	public static int count(CArray array) {
		return empty(array)? 0: array.size();
	}
	
	public static <T> CArray<T> array_unique(CArray<T> _array) {
		return _array.unique();
	}
	
	public static <T> T[] array_unique(T[] array) {
		Set<T> set = new HashSet<T>();
		set.addAll(Arrays.asList(array));
		T[] arrays = (T[])set.toArray((T[])Array.newInstance(array[0].getClass(), set.size()));
		Collections.sort((List) Arrays.asList(arrays));
		return arrays;
	}
	
	/**
	 * 从数组中取出一段
	 * 
	 * @param _array 输入的数组。
	 * @param _offset 如果 offset 非负，则序列将从 array 中的此偏移量开始。如果 offset 为负，则序列将从 array 中距离末端这么远的地方开始。
	 * @param _length 如果给出了 length 并且为正，则序列中将具有这么多的单元。如果给出了 length 并且为负，则序列将终止在距离数组末端这么远的地方。如果省略，则序列将从 offset 开始一直到 array 的末端。
	 * @param _preserve_keys 注意 array_slice() 默认会重新排序并重置数组的数字索引。你可以通过将 preserve_keys 设为 TRUE 来改变此行为。
	 * @return
	 */
	public static <T> CArray<T> array_slice(CArray<T> _array, int _offset, Integer _length, boolean _preserve_keys){
		Object[] keys = _array.keySet().toArray();
		int lastIndex = keys.length - 1;
		if(_offset < 0) {
			_offset = lastIndex + _offset;
		}
		
		int endIndex;
		if(_length == null) {
			endIndex = lastIndex;
		}else {
			if(_offset + _length > lastIndex) {
				_length = lastIndex - _offset + 1;
			}
			endIndex = _offset+_length;
		}
		
		
		CArray<T> r = new CArray<T>(); 
		for(; _offset<endIndex; _offset++) {
			Object key = keys[_offset];
			T v = _array.get(key);
			
			if(_preserve_keys) {
				r.put(key, v);
			}else {
				if(key instanceof Number) {
					r.add(v);
				}else {
					r.put(key, v);
				}
			}
		}
		return r;
	}
	
	public static <T> CArray<T> array_slice(CArray<T> _array, int _offset, Integer _length){
		return array_slice(_array, _offset, _length, false);
	}
	
	public static <T> CArray<T> array_slice(CArray<T> _array, int _offset){
		return array_slice(_array, _offset, null);
	}
	
	
	public static <V> V array_pop(CArray<V> _array) {
		if (empty(_array)){
			return null;
		}
		Object o = _array.keySet().toArray()[_array.size() - 1];
		return _array.remove(o);
	}
	
	public static <V> V array_pop(List<V> _array) {
		if (empty(_array)){
			return null;
		}
		return _array.remove(_array.size() - 1);
	}
	
	
	/**
	 * array_fill() 用 value 参数的值将一个数组填充 num 个条目，键名由 start_index 参数指定的开始。
	 * 
	 * @param _start_index
	 *            返回的数组的第一个索引值。
	 * 
	 *            如果 start_index 是负数， 那么返回的数组的第一个索引将会是 start_index ，而后面索引则从0开始。
	 *            (参见 例子)。
	 * @param _num
	 *            插入元素的数量。 必须大于 0。
	 * @param _value
	 *            用来填充的值。
	 * @return返回填充后的数组。
	 */
	public static CArray array_fill(int _start_index , int _num , Factory facotry) {
		CArray array = array();
		if(_start_index < 0) {
			array.put(_start_index, facotry.create());
			_num--;
			_start_index = 0;
		}
		
		for(int i=0; i<_num; i++) {
			array.put((i+_start_index), facotry.create());
		}
		return array;
	}
		
	public static String trim(String str, char... charlist) {
		if (str == null || str.length() == 0) {
			return str;
		}
		if (charlist == null || charlist.length == 0) {
			charlist = new char[] { '\0', '\t', '\n', 0x0B, '\r', ' ' };
		}
		String chars = String.valueOf(charlist);
		return StringUtils.strip(str, chars);
	}
	
	/**
	 * 该函数删除 str 末端的空白字符并返回。 <br/>
	 * 不使用第二个参数，rtrim() 仅删除以下字符： <br/>
	 * " " (ASCII 32 (0x20))，普通空白符。 <br/>
	 * "\t" (ASCII 9 (0x09))，制表符。 <br/>
	 * "\n" (ASCII 10 (0x0A))，换行符。 <br/>
	 * "\r" (ASCII 13 (0x0D))，回车符。 <br/>
	 * "\0" (ASCII 0 (0x00))，NUL 空字节符。 <br/>
	 * "\x0B" (ASCII 11 (0x0B))，垂直制表符。
	 * 
	 * @param s
	 *            输入字符串。
	 * @param charlist
	 *            通过指定 charlist，可以指定想要删除的字符列表。简单地列出你想要删除的全部字符。使用 .. 格式，可以指定一个范围。
	 * @return 返回改变后的字符串。
	 */
	public static String rtrim(String s, Character... charlist) {
		int len = s.length();
		int st = 0;
		char[] val = s.toCharArray();    /* avoid getfield opcode */

		boolean hasCharlist = charlist!=null && charlist.length>0;
		
		while ((st < len)) {
			char c = val[len - 1];
			if(hasCharlist) {
				if(ArrayUtils.contains(charlist, c)) {
					len--;
				}else {
					break;
				}
			}else if(c <= ' ') {
				len--;
			}else {
				break;
			}
		}
		return ((st > 0) || (len < s.length())) ? s.substring(0, len) : s;
	}
	
	public static String rtrim(String  inputStr, String  chars) {
		if (inputStr == null || inputStr.length()==0 || chars == null || chars.length()==0) {
			return inputStr;
		}
		int i;
		for (i = inputStr.length()-1; i >= 0; i--) {
			char c = inputStr.charAt(i);
			if (chars.indexOf(c) == -1) {
				break;
			}
		}
		return inputStr.substring(0, i+1);
	}
	
	public static String ltrim(String  inputStr, String  chars) {
		if (inputStr == null || inputStr.length()==0 || chars == null || chars.length()==0) {
			return inputStr;
		}
		int i;
		for (i = 0; i <inputStr.length(); i++) {
			char c = inputStr.charAt(i);
			if (chars.indexOf(c) == -1) {
				break;
			}
		}
		return inputStr.substring(i, inputStr.length());
	}
	
	public static String date(String format) {
		if("Z".equals(format)){
			return String.valueOf(TimeZone.getDefault().getRawOffset()/1000);
		}
		return ZendUtils.date(format);
	}
	
	public static String date(String format, Long ts) {
		if (ts == null) {
			ts = 0L;
		}
		return ZendUtils.date(format,ts);
	}
	
	/**
	 * ENT_COMPAT | ENT_HTML401
	 * 
	 * @param string
	 * @param flags
	 * @param charsetEncode
	 * @return
	 */
	public static String htmlentities(String string, String flags, String charsetEncode) {
		return htmlentities(string, false, false);
	}
    public static String htmlentities(String string, boolean encodeNewline, boolean encodeSubsequentBlanksToNbsp) {
        if (string == null) {
            return "";
        }

        StringBuffer sb = null; // create later on demand
        String app;
        char c;
        for (int i = 0; i < string.length(); ++i) {
            app = null;
            c = string.charAt(i);
            switch (c) {
            case '"':
                app = "&quot;";
                break; // "
            case '&':
                app = "&amp;";
                break; // &
            case '<':
                app = "&lt;";
                break; // <
            case '>':
                app = "&gt;";
                break; // >
            case ' ':
                if (encodeSubsequentBlanksToNbsp
                        && (i == 0 || (i - 1 >= 0 && string.charAt(i - 1) == ' '))) {
                    // Space at beginning or after another space
                    app = "&#160;";
                }
                break;
            case '\n':
                if (encodeNewline) {
                    app = "<br/>";
                }
                break;

            // german umlauts
            case '\u00E4':
                app = "&auml;";
                break;
            case '\u00C4':
                app = "&Auml;";
                break;
            case '\u00F6':
                app = "&ouml;";
                break;
            case '\u00D6':
                app = "&Ouml;";
                break;
            case '\u00FC':
                app = "&uuml;";
                break;
            case '\u00DC':
                app = "&Uuml;";
                break;
            case '\u00DF':
                app = "&szlig;";
                break;

            // misc
            // case 0x80: app = "&euro;"; break; sometimes euro symbol is ascii
            // 128, should we suport it?
            case '\u20AC':
                app = "&euro;";
                break;
            case '\u00AB':
                app = "&laquo;";
                break;
            case '\u00BB':
                app = "&raquo;";
                break;
            case '\u00A0':
                app = "&#160;";
                break;

            default:
                if (((int) c) >= 0x80) {
                    // encode all non basic latin characters
                    app = "&#" + ((int) c) + ";";
                }
                break;
            }
            if (app != null) {
                if (sb == null) {
                    sb = new StringBuffer(string.substring(0, i));
                }
                sb.append(app);
            } else {
                if (sb != null) {
                    sb.append(c);
                }
            }
        }

        if (sb == null) {
            return string;
        } else {
            return sb.toString();
        }
    }
    
    public static String htmlspecialchars(String html) {
    	//TODO:
    	return html;
    }
	
    public static String str_replace(Object search, Object replace, String subject) {
    	if(subject==null || subject.length()==0){
    		return subject;
    	}
    	if(search instanceof String) {
    		return unRegexReplace(subject, (String)search, (String)replace);
    	}else if(search instanceof CArray) {
    		int i=0;
    		for(String s: (CArray<String>)search) {
    			String r = ((CArray<String>)replace).get(i++);
				if (r == null) {
					r = "";
				}
    			subject = unRegexReplace(subject, s, r);
    		}
    		return subject;
    	}
    	return subject;
    }
    
    
    private static String unRegexReplace(String str, String search, String replace) {
    	int searchLen = search.length();
    	int index = -searchLen;
    	
    	Stack<Integer> splits = new Stack<Integer>();
    	while((index = str.indexOf(search, index+searchLen)) > -1) {
    		splits.push(index);
    	}
    	
    	StringBuffer sb = new StringBuffer(str);
    	while(!splits.isEmpty()) {
    		int i = splits.pop();
    		sb.replace(i, i+searchLen, replace);
    	}
    	
    	return sb.toString();
    }
	
    public static String strval(Object o) {
    	return String.valueOf(o);
    }
    
    public static int strcmp(String s1, String s2) {
    	return s1.compareTo(s2);
    }
    
	public static int strlen(String s) {
		if (s == null) {
			return 0;
		} else {
			//FIXME 需要考虑多字节情况，可能存在潜在bug
			return s.length();
			//return s.getBytes().length;
		}
	}
    public static int mb_strlen(String s) { //TODO:
    	return s.length();
    }
    
    public static String strtolower(String s) {
    	return s.toLowerCase();
    }
    public static String mb_strtolower(String s) { //TODO:
    	return s.toLowerCase();
    }
    
    public static String strtoupper(String s) {
    	return s.toUpperCase();
    }

	/**
	 * 
	 * 和 strtoupper() 不同的是，“字母”是通过 Unicode 字符属性来确定的。
	 * 因此这个函数不会受语言环境（locale）设置影响，能够转化任何具有“字母”属性的字符，例如 a 变音符号（ä）。
	 * 
	 * @param s
	 * @return
	 */
    public static String mb_strtoupper(String s) { //TODO:
    	return s.toUpperCase();
    }
    
	/**
	 * 
	 * @param _haystack
	 *            在该字符串中进行查找。
	 * @param _needle
	 *            如果 needle 不是一个字符串，那么它将被转换为整型并被视为字符的顺序值。
	 * @return 返回 needle 存在于 haystack 字符串起始的位置(独立于
	 *         offset)。同时注意字符串位置是从0开始，而不是从1开始的。 如果没找到 needle，将返回 -1。
	 */
    public static int strpos(String _haystack , String _needle) {
    	return _haystack.indexOf(_needle);
    }
    
    /**
     * Pad a string to a certain length with another string
     * @link http://www.php.net/manual/en/function.str-pad.php
     * @param input string <p>
     * The input string.
     * </p>
     * @param pad_length int <p>
     * If the value of pad_length is negative,
     * less than, or equal to the length of the input string, no padding
     * takes place.
     * </p>
     * @param pad_string string[optional] <p>
     * The pad_string may be truncated if the
     * required number of padding characters can't be evenly divided by the
     * pad_string's length.
     * </p>
     * @param pad_type int[optional] <p>
     * Optional argument pad_type can be
     * STR_PAD_RIGHT, STR_PAD_LEFT,
     * or STR_PAD_BOTH. If
     * pad_type is not specified it is assumed to be
     * STR_PAD_RIGHT.
     * </p>
     * @return string the padded string.
     */
    public static String str_pad(String _input, int _pad_length, String _pad_string, Integer _pad_type) {
    	if(_pad_type == STR_PAD_RIGHT) {
    		return StringUtils.rightPad(_input, _pad_length, _pad_string);
    	}else if(_pad_type == STR_PAD_LEFT) {
    		return StringUtils.leftPad(_input, _pad_length, _pad_string);
    	}else {
    		return StringUtils.center(_input, _pad_length, _pad_string);
    	}
    }
    public static String str_pad(String _input, int _pad_length, String _pad_string) {
    	return str_pad(_input, _pad_length, _pad_string, STR_PAD_RIGHT);
    }
    public static String str_pad(String _input, int _pad_length) {
    	return str_pad(_input, _pad_length, " ");
    }
    public final static int STR_PAD_RIGHT = 0;
    public final static int STR_PAD_LEFT = 1;
    public final static int STR_PAD_BOTH = 2;
    
    
    /**
     * 使用键名比较计算数组的交集
     * 
     * array_intersect_key() 返回一个数组，该数组包含了所有出现在 array1 中并同时出现在所有其它参数数组中的键名的值。
     * 
     * @param array
     * @param others
     * @return
     */
    public static <T> CArray<T> array_intersect_key(CArray<T> array, CArray...others){
    	CArray<T> r = new CArray<T>();
    	
    	for(Object key: array.keySet()) {
    		boolean has = true; 
    		for(CArray other: others) {
    			if(!other.containsKey(key)) {
    				has = false;
    				break;
    			}
        	}
    		if(has) {
    			r.put(key, array.get(key));
    		}
    	}
    	return r;
    }
    
    public static void array_push(CArray array, Object...vars) {
    	for(Object var: vars) {
    		array.add(var);
    	}
    }
    
    public static void echo(String msg) {
    	RadarContext.getContext().write(msg);
    }
    public static void print(String msg) {
    	echo(msg);
    }
    
    public static <T> T key(CArray a) {
    	return (T)a.keySet().iterator().next();
    }
    
	public static CArray sort(CArray array) {
		return array.sort();
	}
	
	/**
	 * array_multisort() 可以用来一次对多个数组进行排序，或者根据某一维或多维对多维数组进行排序。
	 * 
	 * 关联（string）键名保持不变，但数字键名会被重新索引。
	 * 
	 * 排序顺序标志：
	 * 
	 * SORT_ASC - 按照上升顺序排序 
	 * SORT_DESC - 按照下降顺序排序 排序类型标志：
	 * SORT_REGULAR - 将项目按照通常方法比较 
	 * SORT_NUMERIC - 将项目按照数值比较 
	 * SORT_STRING - 将项目按照字符串比较 每个数组之后不能指定两个同类的排序标志。每个数组后指定的排序标志仅对该数组有效 - 在此之前为默认值 SORT_ASC 和  SORT_REGULAR。
	 * 
	 * 输入数组被当成一个表的列并以行来排序——这类似于 SQL 的 ORDER BY
	 * 子句的功能。第一个数组是要排序的主要数组。数组中的行（值）比较为相同的话就按照下一个输入数组中相应值的大小来排序，依此类推。
	 * 
	 * 本函数的参数结构有些不同寻常，但是非常灵活。第一个参数必须是一个数组。接下来的每个参数可以是数组或者是下面列出的排序标志。
	 * 
	 * @param arr
	 *            要排序的一个 array。
	 * 
	 * @param arg 接下来的每个参数可以是另一个 array 或者是为之前 array 排序标志选项参数： SORT_ASC,
	 *            SORT_DESC, SORT_REGULAR, SORT_NUMERIC, SORT_STRING.
	 * 
	 * @param ... Additional arg's.
	 */
	public static <V> boolean array_multisort(CArray<V> array, Object...args) {
		CArray sortable = array;
		int iFlag = 0;
		int sortOrder = SORT_ASC;
		int sortBy = SORT_REGULAR;
		
		for(Object arg: args) {
			if(arg instanceof Integer) {
				int i = (Integer)arg;
				
				switch(iFlag) {
					case 0:
						if(i==SORT_ASC || i==SORT_DESC) {
							sortOrder = i;
						}else {
							throw new IllegalArgumentException();
						}
						break;
					case 1:
						if(i==SORT_REGULAR || i==SORT_NUMERIC || i==SORT_STRING) {
							sortBy = i;
						}else {
							throw new IllegalArgumentException();
						}
						break;
					default:
						throw new IllegalArgumentException();
				}
				iFlag++;
			}else if(arg instanceof CArray) {
				array_multisort(sortable, sortOrder, sortBy);
				
				iFlag = 0;
				sortOrder = SORT_ASC;
				sortBy = SORT_REGULAR;
				
				sortable = (CArray)arg;
			}
		}
		
		final int order = sortOrder==SORT_ASC? 1: -1; 
		final boolean byNumber = sortBy == SORT_NUMERIC;
		
		array.sort(new Comparator<Entry<Object,V>>() {
			@Override public int compare(Entry<Object, V> o1, Entry<Object, V> o2) {
				int r;
				if(byNumber) {
					r = EasyObject.asDouble(o1.getValue()).compareTo(EasyObject.asDouble(o2.getValue()));
				}else {
					r = String.valueOf(o1.getValue()).compareTo(String.valueOf(o2.getValue()));
				}
				return r * order;
			}
		}, true);
		
		return true;
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static int preg_match(String pattern, String subject) {
		return preg_match(pattern, subject, array());
	}
	
	public static int preg_match(IPattern pattern, String subject) {
		return preg_match(pattern, subject, array());
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static int preg_match(String pattern, String subject, CArray<?> matcheResult) {
		return preg_match(pattern, subject, matcheResult, PREG_PATTERN_ORDER);
	}
	
	public static int preg_match(IPattern pattern, String subject, CArray<?> matcheResult) {
		return preg_match(pattern, subject, matcheResult, PREG_PATTERN_ORDER);
	}	
	
	public static int preg_match(String pattern, String subject, CArray<?> matcheResult, int flags) {
		return preg_match(IPattern.compile(pattern), subject, matcheResult, flags);
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static int preg_match(int regflags, String pattern, String subject) {
		return preg_match(regflags, pattern, subject, array());
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static int preg_match(int regflags, String pattern, String subject, CArray<?> matcheResult) {
		return preg_match(regflags, pattern, subject, matcheResult, PREG_PATTERN_ORDER);
	}
	
	public static int preg_match(int regflags, String pattern, String subject, CArray<?> matcheResult, int flags) {
		return preg_match(IPattern.compile(pattern, regflags), subject, matcheResult, flags);
	}
	
	/**
	 * 搜索subject与pattern给定的正则表达式的一个匹配.
	 * 
	 * @param pattern
	 *            要搜索的模式，字符串类型。
	 * @param str
	 *            输入字符串。
	 * @param matcheResult
	 *            如果提供了参数matches，它将被填充为搜索结果。 _matches[0]将包含完整模式匹配到的文本，
	 *            _matches[1] 将包含第一个捕获子组匹配到的文本，以此类推。
	 * @param _flags
	 *            flags可以被设置为以下标记值：
	 *            PREG_OFFSET_CAPTURE
	 *            如果传递了这个标记，对于每一个出现的匹配返回时会附加字符串偏移量(相对于目标字符串的)。
	 *            注意：这会改变填充到matches参数的数组，使其每个元素成为一个由 第0个元素是匹配到的字符串，第1个元素是该匹配字符串
	 *            在目标字符串subject中的偏移量。
	 * @return 返回 pattern 的匹配次数。 它的值将是0次（不匹配）或1次，因为preg_match()在第一次匹配后
	 *         将会停止搜索。preg_match_all()不同于此，它会一直搜索subject 直到到达结尾。
	 *         如果发生错误preg_match()返回 FALSE。
	 */
	@CodeConfirmed("benne.2.2.4")
	public static int preg_match(IPattern pattern, String subject, CArray<?> matcheResult, int flags) {
		if (matcheResult == null) {
			throw new IllegalArgumentException("matcheResult can not be null !");
		}
		IPattern regexp = pattern;
		IMatcher matcher = regexp.matcher(subject);
		boolean result = false;
		if (matcher.find()) {
			int cnt = matcher.groupCount();
			for (int idx = 0; idx <= cnt; idx++) {
				if ((PREG_OFFSET_CAPTURE & flags) == PREG_OFFSET_CAPTURE) {
					CArray a = (CArray)matcheResult.get(idx);
					if(a == null) {
						a = array();
						matcheResult.put(idx, a);
					}
					a.add(array(matcher.group(idx), matcher.start(idx)));
				} else {
					matcheResult.put(matcher.group(idx));
				}
			}
			for(String name: regexp.groupNames()) {
				if ((PREG_OFFSET_CAPTURE & flags) == PREG_OFFSET_CAPTURE) {
					matcheResult.put(name, array(matcher.group(name), matcher.start(name)));
				} else {
					matcheResult.put(name, matcher.group(name));
				}
			}
			result = true;
		}
		return result? 1: 0;
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static boolean preg_match_all(String pattern, String subject) {
		return preg_match_all(pattern, subject, array());
	}
	public static boolean preg_match_all(IPattern pattern, String subject) {
		return preg_match_all(pattern, subject, array());
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static boolean preg_match_all(String pattern, String subject, CArray<CArray> matcheResult) {
		return preg_match_all(pattern, subject, matcheResult, PREG_PATTERN_ORDER);
	}
	public static boolean preg_match_all(IPattern pattern, String subject, CArray<CArray> matcheResult) {
		return preg_match_all(pattern, subject, matcheResult, PREG_PATTERN_ORDER);
	}
	
	public static boolean preg_match_all (IPattern pattern, String subject, CArray<CArray> matcheResult, int flags) {
		if (matcheResult == null) {
			throw new IllegalArgumentException("matcheResult can not be null !");
		}
		IPattern regexp = pattern;
		IMatcher matcher = regexp.matcher(subject);
		boolean result = false;
		if ((PREG_SET_ORDER & flags) == PREG_SET_ORDER) {
			Map<String, Integer> namedGrps = regexp.namedGroups();
			Map<Integer, String> flipGrps = new IMap();
			for (Entry<String, Integer> e : namedGrps.entrySet()) {
				flipGrps.put(e.getValue(), e.getKey());
			}
			int hits = 0;
			while (matcher.find()) {
				if (!matcheResult.containsKey(hits)) {
					matcheResult.put(hits, new CArray<String>());
				}
				int cnt = matcher.groupCount();
				for (int idx = 0; idx <= cnt; idx++) {
					if ((PREG_OFFSET_CAPTURE & flags) == PREG_OFFSET_CAPTURE) {
						matcheResult.get(hits).add(array(matcher.group(idx), matcher.start(idx)));
					} else {
						matcheResult.get(hits).add(matcher.group(idx));
					}
					if (flipGrps.containsKey(idx)) {
						if ((PREG_OFFSET_CAPTURE & flags) == PREG_OFFSET_CAPTURE) {
							matcheResult.get(hits).put(flipGrps.get(idx),array(matcher.group(idx), matcher.start(idx)));
						} else {
							matcheResult.get(hits).put(flipGrps.get(idx),matcher.group(idx));
						}
					}
				}
				hits++;
				result = true;
			}
		} else {
			while (matcher.find()) {
				int cnt = matcher.groupCount();
				for (int idx = 0; idx <= cnt; idx++) {
					if (!matcheResult.containsKey(idx)) {
						matcheResult.put(idx, new CArray<String>());
					}
					if ((PREG_OFFSET_CAPTURE & flags) == PREG_OFFSET_CAPTURE) {
						matcheResult.get(idx).add(array(matcher.group(idx), matcher.start(idx)));
					} else {
						matcheResult.get(idx).add(matcher.group(idx));
					}
				}
				for(String name: regexp.groupNames()) {
					if (!matcheResult.containsKey(name)) {
						matcheResult.put(name, new CArray<String>());
					}
					if ((PREG_OFFSET_CAPTURE & flags) == PREG_OFFSET_CAPTURE) {
						matcheResult.get(name).add(array(matcher.group(name), matcher.start(name)));
					} else {
						matcheResult.get(name).add(matcher.group(name));
					}
				}
				result = true;
			}
		} 
		return result;
	}
	
	/**
	 * 搜索subject与pattern给定的正则表达式的一个匹配.
	 * 
	 * @param pattern
	 *            要搜索的模式，字符串类型。
	 * @param str
	 *            输入字符串。
	 * @param matcheResult
	 *            如果提供了参数matches，它将被填充为搜索结果。 _matches[0]将包含完整模式匹配到的文本，
	 *            _matches[1] 将包含第一个捕获子组匹配到的文本，以此类推。
	 * @param _flags
	 *            flags可以被设置为以下标记值：
	 *            可以结合下面标记使用(注意不能同时使用PREG_PATTERN_ORDER和 PREG_SET_ORDER)：

					PREG_PATTERN_ORDER
					结果排序为_matches[0]保存完整模式的所有匹配, _matches[1] 保存第一个子组的所有匹配，以此类推。
					
					<?php
					preg_match_all("|<[^>]+>(.*)</[^>]+>|U",
					    "<b>example: </b><div align=left>this is a test</div>",
					    _out, PREG_PATTERN_ORDER);
					echo _out[0][0] . ", " . _out[0][1] . "\n";
					echo _out[1][0] . ", " . _out[1][1] . "\n";
					?>
					以上例程会输出：
					
					<b>example: </b>, <div align=left>this is a test</div>
					example: , this is a test
					因此, _out[0]是包含匹配完整模式的字符串的数组， _out[1]是包含闭合标签内的字符串的数组。
					
					PREG_SET_ORDER
					结果排序为_matches[0]包含第一次匹配得到的所有匹配(包含子组)， _matches[1]是包含第二次匹配到的所有匹配(包含子组)的数组，以此类推。
					
					<?php
					preg_match_all("|<[^>]+>(.*)</[^>]+>|U",
					    "<b>example: </b><div align=\"left\">this is a test</div>",
					    _out, PREG_SET_ORDER);
					echo _out[0][0] . ", " . _out[0][1] . "\n";
					echo _out[1][0] . ", " . _out[1][1] . "\n";
					?>
					以上例程会输出：
					
					<b>example: </b>, example:
					<div align="left">this is a test</div>, this is a test
					PREG_OFFSET_CAPTURE
					如果这个标记被传递，每个发现的匹配返回时会增加它相对目标字符串的偏移量。 注意这会改变matches中的每一个匹配结果字符串元素，使其 成为一个第0个元素为匹配结果字符串，第1个元素为 匹配结果字符串在subject中的偏移量。
					
					如果没有给定排序标记，假定设置为PREG_PATTERN_ORDER。
	 * @return 返回 pattern 的匹配次数。 它的值将是0次（不匹配）或1次，因为preg_match()在第一次匹配后
	 *         将会停止搜索。preg_match_all()不同于此，它会一直搜索subject 直到到达结尾。
	 *         如果发生错误preg_match()返回 FALSE。
	 */
	@CodeConfirmed("benne.2.2.4")
	public static boolean preg_match_all (String pattern, String subject, CArray<CArray> matcheResult, int flags) {
		return preg_match_all(IPattern.compile(pattern), subject, matcheResult, flags);
	}	
	
	/**
	 * 搜索subject中匹配pattern的部分， 以replacement进行替换。
	 * 
	 * @param pattern
	 *            要搜索的模式。可以使一个字符串或字符串数组。 可以使用一些PCRE修饰符，
	 *            包括被弃用的'e'(PREG_REPLACE_EVAL)，可以为这个函数指定。
	 * 
	 * @param replacement
	 *            用于替换的字符串或字符串数组。如果这个参数是一个字符串，并且pattern
	 *            是一个数组，那么所有的模式都使用这个字符串进行替换。如果pattern和replacement
	 *            都是数组，每个pattern使用replacement中对应的
	 *            元素进行替换。如果replacement中的元素比pattern中的少， 多出来的pattern使用空字符串进行替换。
	 * 
	 *            replacement中可以包含后向引用\\n 或(php 4.0.4以上可用)$n，语法上首选后者。 每个
	 *            这样的引用将被匹配到的第n个捕获子组捕获到的文本替换。 n 可以是0-99，\\0和$0代表完整的模式匹配文本。
	 *            捕获子组的序号计数方式为：代表捕获子组的左括号从左到右， 从1开始数。如果要在replacement
	 *            中使用反斜线，必须使用4个("\\\\"，译注：因为这首先是php的字符串，经过转义后，是两个，再经过
	 *            正则表达式引擎后才被认为是一个原文反斜线)。
	 * 
	 *            当在替换模式下工作并且后向引用后面紧跟着需要是另外一个数字(比如：在一个匹配模式后紧接着增加一个原文数字)，
	 *            不能使用\\1这样的语法来描述后向引用。比如， \\11将会使preg_replace()
	 *            不能理解你希望的是一个\\1后向引用紧跟一个原文1，还是 一个\\11后向引用后面不跟任何东西。
	 *            这种情况下解决方案是使用\${1}1。 这创建了一个独立的$1后向引用, 一个独立的原文1。
	 * 
	 *            当使用被弃用的 e 修饰符时, 这个函数会转义一些字符(即：'、"、 \ 和 NULL)
	 *            然后进行后向引用替换。当这些完成后请确保后向引用解析完后没有单引号或 双引号引起的语法错误(比如：
	 *            'strlen(\'$1\')+strlen("$2")')。确保符合PHP的
	 *            字符串语法，并且符合eval语法。因为在完成替换后，
	 *            引擎会将结果字符串作为php代码使用eval方式进行评估并将返回值作为最终参与替换的字符串。
	 * 
	 * @param subject
	 *            要进行搜索和替换的字符串或字符串数组。 如果subject是一个数组，搜索和替换回在subject 的每一个元素上进行,
	 *            并且返回值也会是一个数组。
	 * 
	 * @param limit
	 *            每个模式在每个subject上进行替换的最大次数。默认是 -1(无限)。
	 * 
	 * @param count
	 *            如果指定，将会被填充为完成的替换次数。
	 * @return 
	 * 			如果subject是一个数组， preg_replace()返回一个数组， 其他情况下返回一个字符串。
	 *         	如果匹配被查找到，替换后的subject被返回，其他情况下 返回没有改变的 subject。如果发生错误，返回 NULL 。
	 */
	@CodeConfirmed("benne.2.2.4")
	public static String preg_replace(String pattern, String replacement, String subject){
		return subject.replaceAll(pattern, replacement);
	}
	
	@CodeConfirmed("benne.2.2.4")
	public static String[] preg_replace(String pattern, String replacement, String[] subjects){
		for(int i=0;i<subjects.length;i++){
			subjects[i] = subjects[i].replaceAll(pattern, replacement);
		}
		return subjects;
	}
	
	public static String preg_replace(CArray<IPattern> patterns, CArray<String> replacements, String subject) {
		for(int i=0,imax=patterns.size(); i<imax; i++) {
			IPattern pattern = patterns.get(i);
			IMatcher matcher = pattern.matcher(subject);
			if(matcher.find()) {
				String replacement = Nest.value(replacements, i).asString(true);
				subject = matcher.replaceAll(replacement);
			}
		}
		return subject;
	}
	public static String preg_replace(IPattern pattern, String replacement, String subject) {
		return preg_replace(array(pattern), array(replacement), subject);
	}
	
	/**
	 * Compares the left_operand to the right_operand and returns the result as
	 * an integer.
	 * 
	 * @param _left_operand
	 *            The left operand, as a string.
	 * @param _right_operand
	 *            The right operand, as a string.
	 * @param _scale
	 *            The optional scale parameter is used to set the number of
	 *            digits after the decimal place which will be used in the
	 *            comparison.
	 * @return 0 if the two operands are equal, 1 if the left_operand is larger
	 *         than the right_operand, -1 otherwise.
	 */
	public static int bccomp (String _left_operand , String _right_operand, int _scale) {
//		return _left_operand.compareTo(_right_operand);
		return ZendUtils.natsort(_left_operand, _right_operand);
	}
	
	public static int bccomp(String _left_operand, String _right_operand ) {
		return bccomp(_left_operand, _right_operand, BCSCALE);
	}
	
	public static int bccomp(Object _left_operand, Object _right_operand ) {
		if(_left_operand instanceof Comparable && _left_operand.getClass().isInstance(_right_operand)) {
			return ((Comparable)_left_operand).compareTo(_right_operand);
		}else if(_left_operand instanceof Number && _right_operand instanceof Number) {
			return Double.compare(((Number)_left_operand).doubleValue(), ((Number)_right_operand).doubleValue());
		}
		return bccomp(String.valueOf(_left_operand), String.valueOf(_right_operand));
	}
	
	/**
	 * 返回一个数组，该数组用 input 数组中的值作为键名，该值在 input 数组中出现的次数作为值。
	 * 
	 * 对数组里面的每个不是 string 和 integer 类型的元素抛出一个警告错误（E_WARNING）。
	 * 
	 * @param input 统计这个数组的值
	 * @return array 返回一个关联数组，用 input 数组中的值作为键名，该值在数组中出现的次数作为值。
	 */
	public static CArray<Integer> array_count_values (CArray _input) {
		CArray<Integer> ret = array();
		for(Object v: _input) {
			Integer count = ret.get(v);
			if(count == null) {
				count = 0;
			}
			ret.put(v, count+1);
		}
		return ret;
	}
	
	public static CArray<Integer> array_count_values (Map _input) {
		CArray<Integer> ret = array();
		for(Object v: _input.values()) {
			Integer count = ret.get(v);
			if(count == null) {
				count = 0;
			}
			ret.put(v, count+1);
		}
		return ret;
	}
	
	/**
	 * 本函数对数组进行排序，数组的索引保持和单元的关联。 主要用于对那些单元顺序很重要的结合数组进行排序。
	 * 
	 * @param array
	 *            输入的数组。
	 * @param _sort_flags
	 *            可以用可选的参数 sort_flags 改变排序的行为，详情见 sort()。
	 * @return 成功时返回 TRUE， 或者在失败时返回 FALSE。
	 */
	public static <V> boolean arsort(CArray<V> array, int _sort_flags) {
		array.sort(new Comparator<Entry<Object,V>>() {
			@Override public int compare(Entry<Object, V> o1, Entry<Object, V> o2) {
				return String.valueOf(o2.getValue()).compareTo(String.valueOf(o1.getValue()));
			}
		}, true);
		return true;
	}
	public static boolean arsort(CArray array) {
		return arsort(array, SORT_REGULAR); //SORT_REGULAR
	}
	
	/**
	 * 本函数对数组进行排序，数组的索引保持和单元的关联。主要用于对那些单元顺序很重要的结合数组进行排序。
	 * 
	 * @param array 输入的数组。
	 * @param _sort_flags 可以用可选的参数 sort_flags 改变排序的行为，详情见 sort()。
	 * @return 成功时返回 TRUE， 或者在失败时返回 FALSE。
	 */
	public static boolean asort(CArray array, int _sort_flags) {
		array.sort(true);
		return true;
	}
	public static boolean asort(CArray array) {
		return arsort(array, SORT_REGULAR); //SORT_REGULAR
	}
	
	/**
	 * 本函数对数组排序并保持索引和单元之间的关联。
	 * 
	 * 主要用于对那些单元顺序很重要的结合数组进行排序。比较函数是用户自定义的。
	 * 
	 * @param cmp
	 * @param array
	 * @return
	 */
	public static <V> boolean uasort(CArray<V> array, final Comparator<V> cmp) {
		array.sort(new Comparator<Entry<Object, V>>() {
			@Override public int compare(Entry<Object, V> o1, Entry<Object, V> o2) {
				return cmp.compare(o1.getValue(), o2.getValue());
			}
		}, true);
		return true;
	}
	
	/**
	 * 检查文件或目录是否存在。
	 * 
	 * @param fileName
	 *            文件或目录的路径。
	 * @return 如果由 filename 指定的文件或目录存在则返回 TRUE，否则返回 FALSE。
	 */
	public static boolean file_exists(String fileName) {
		return true;
	}
	
	public static class CException extends RuntimeException{
		private static final long serialVersionUID = 1835258000944321069L;
		public CException(String message) {
			this(message, null);
		}
		public CException(String message, Object code) {
			super(message);
		}
	}
	
	public static void bcscale(int scale) {
		BCSCALE = scale;
	}
	
	public static Float bcmul(String _left_operand, String _right_operand ) {
		return bcmul(EasyObject.asDouble(_left_operand), EasyObject.asDouble(_right_operand));
	}
	public static Float bcmul(String _left_operand, String _right_operand, int _scale ) {
		return bcmul(EasyObject.asDouble(_left_operand), EasyObject.asDouble(_right_operand), _scale);
	}
	
	public static Float bcmul(double _left_operand, double _right_operand ) {
		return bcmul(_left_operand, _right_operand, BCSCALE);
	}
	/**
	 * Multiply the left_operand by the right_operand.
	 * 
	 * @param _left_operand
	 * 			The left operand, as a string.
	 * @param _right_operand
	 * 			The right operand, as a string.
	 * @param _scale
	 * 			此可选参数用于设置结果中小数点后的小数位数。也可通过使用 bcscale() 来设置全局默认的小数位数，用于所有函数。
	 * @return	Returns the result as a string.
	 */
	public static Float bcmul(double _left_operand, double _right_operand, int _scale ) {
		double result = _left_operand * _right_operand;
		NumberFormat formatter = getNumberFormatInstance();
		formatter.setMaximumFractionDigits(_scale);
		return Float.valueOf(formatter.format(result));
	}
	
	public static Float bcadd(double _left_operand, double _right_operand) {
		return bcadd(_left_operand, _right_operand, BCSCALE);
	}
	public static Float bcadd(double _left_operand, double _right_operand, int _scale ) {
		double result = _left_operand + _right_operand;
		NumberFormat formatter = getNumberFormatInstance();
		formatter.setMaximumFractionDigits(_scale);
		return Float.valueOf(formatter.format(result));
	}
	
	public static Float bcdiv(String _left_operand, String _right_operand ) {
		return bcdiv(EasyObject.asDouble(_left_operand), EasyObject.asDouble(_right_operand));
	}
	public static Float bcdiv(String _left_operand, String _right_operand, int _scale ) {
		return bcdiv(EasyObject.asDouble(_left_operand), EasyObject.asDouble(_right_operand), _scale);
	}
	
	public static Float bcdiv(double _left_operand, double _right_operand ) {
		return bcdiv(_left_operand, _right_operand, BCSCALE);
	}
	public static Float bcdiv(double _left_operand, double _right_operand, int _scale ) {
		double result = _left_operand / _right_operand;
		NumberFormat formatter = getNumberFormatInstance();
		formatter.setMaximumFractionDigits(_scale);
		return Float.valueOf(formatter.format(result));
	}
	
	public static Float bcsub(double _left_operand, double _right_operand ) {
		return bcsub(_left_operand, _right_operand, BCSCALE);
	}
	public static Float bcsub(double _left_operand, double _right_operand, int _scale ) {
		double result = _left_operand - _right_operand;
		NumberFormat formatter = getNumberFormatInstance();
		formatter.setMaximumFractionDigits(_scale);
		return Float.valueOf(formatter.format(result));
	}
	
	private static NumberFormat getNumberFormatInstance() {
		return new DecimalFormat("0.#");
	}
	
	/**
	 * bcpow — Raise an arbitrary precision number to another
	 * Raise left_operand to the power right_operand.
	 * 
	 * @param _left_operand The left operand, as a string.
	 * @param _right_operand The right operand, as a string.
	 * @param scale 此可选参数用于设置结果中小数点后的小数位数。也可通过使用 bcscale() 来设置全局默认的小数位数，用于所有函数。
	 * @return Returns the result as a string.
	 */
	public static double bcpow(double _left_operand, double _right_operand, int scale) {
		return Math.pow(_left_operand, _right_operand);
	}
	public static double bcpow(double _left_operand, double _right_operand) {
		return bcpow(_left_operand, _right_operand, BCSCALE);
	}
	
	public static double bcpow(String _left_operand, String _right_operand, int scale) {
		return bcpow(EasyObject.asDouble(_left_operand), EasyObject.asDouble(_right_operand), scale);
	}
	public static double bcpow(String _left_operand, String _right_operand) {
		return bcpow(EasyObject.asDouble(_left_operand), EasyObject.asDouble(_right_operand));
	}
	
	
	public static double bcceil(double _left_operand) {
		return Math.ceil(_left_operand);
	}
	
	public static double bcfloor(double _left_operand) {
		return Math.floor(_left_operand);
	}
	
	public static int round(double _val) {
		return (int)Math.round(_val);
	}
	public static double round(double val, int _precision) {
		BigDecimal v = new BigDecimal(val);
		v = v.setScale(_precision,  BigDecimal.ROUND_HALF_UP);
		return v.doubleValue();
	}
	public static int ceil(double v) {
		return (int)Math.ceil(v);
	}
	public static int floor(double v) {
		return (int)Math.floor(v);
	}
	public static double abs(double n) {
		return Math.abs(n);
	}
	
	public static double sin(double a) {
		return Math.sin(a);
	}
	public static double cos(double a) {
		return Math.cos(a);
	}
	
	public static <T extends Number> T min(Iterable<T> vs) {
		Iterator<T> iterator = vs.iterator();
		T min = iterator.next();
		if(iterator.hasNext()) {
			for(T v=iterator.next(); iterator.hasNext(); v=iterator.next()) {
				 if(min.doubleValue() > v.doubleValue()) {
					 min = v;
				 }
			}
		}
		return min;
	}
	
	public static <T extends Number> T min(T... vs) {
		if(vs==null || vs.length==0) return null;
		
		T r = vs[0];
		for(T v: vs) {
			r = MathUtils.compare(r, v) < 0 ? r : v;
		}
		return r;
	}
	
	public static <T extends Number> T max(T... vs) {
		if(vs==null || vs.length==0) return null;
		
		T r = vs[0];
		for(T v: vs) {
			r = MathUtils.compare(r, v) > 0 ? r : v;
		}
		return r;
	}
	
	public static <T extends Number> T max(Iterable<T> vs) {
		Iterator<T> iterator = vs.iterator();
		T max = iterator.next();
		if(iterator.hasNext()) {
			for(T v=iterator.next(); iterator.hasNext(); v=iterator.next()) {
				max = MathUtils.compare(max, v) > 0 ? max : v;
			}
		}
		return max;
	}
	
	public static double deg2rad(double deg) {
		return Math.toRadians(deg);
	}
	
	public static String stristr(String _haystack , String _needle) {
		return stristr(_haystack, _needle, false);
	}
	
	public static String stristr(String _haystack , int _needle) {
		return stristr(_haystack, _needle, false);
	}
	
	public static String stristr(String _haystack , int _needle,  boolean _before_needle) {
		return stristr(_haystack, String.valueOf((char)_needle), _before_needle);
	}
	
	/**
	 * 返回 haystack 字符串从 needle 第一次出现的位置开始到结尾的字符串。
	 * 
	 * 参数 needle 和 haystack 将以不区分大小写的方式对待。
	 * 
	 * @param _haystack 在该字符串中查找。
	 * @param _needle 如果 needle 不是一个字符串，那么它将被转换为整型并被视为字符顺序值。
	 * @param _before_needle 若为 TRUE，strstr() 将返回 needle 在 haystack 中的位置之前的部分(不包括 needle)。
	 * @return
	 */
	public static String stristr(String _haystack , String _needle,  boolean _before_needle) {
		String haystack = _haystack.toLowerCase();
		String needle = _needle.toLowerCase();
		
		int index = haystack.indexOf(needle);
		if(index == -1) {
			return null;
		}else {
			if(_before_needle) {
				return _haystack.substring(0, index);
			}else {
				return _haystack.substring(index);
			}
		}
	}
	
	/**
	 * 返回 haystack 字符串从 needle 第一次出现的位置开始到结尾的字符串。
	 * 
	 * @param haystack 在该字符串中查找。
	 * @param needle 如果 needle 不是一个字符串，那么它将被转换为整型并被视为字符顺序值。
	 * @return
	 */
	public static String strstr(String haystack, String needle) {
		int index = haystack.indexOf(needle);
		if (index == -1) {
			return null;
		} else {
			return haystack.substring(index);
		}
	}
	
	/**
	 * 
	 * 该函数实现了以人类习惯对数字型字符串进行排序的比较算法，这就是“自然顺序”。注意该比较区分大小写。
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int strnatcmp(Object s1, Object s2) {
		return NaturalOrderComparator.INSTANCE.compare(s1, s2);
	}
	
	/**
	 * 该函数实现了以人类习惯对数字型字符串进行排序的比较算法。除了不区分大小写，该函数的行为与 strnatcmp() 类似。更多信息，参见：Martin Pool 的» 自然顺序的字符串比较页面。
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int strnatcasecmp(Object s1, Object s2) {
		s1 = s1==null? null: String.valueOf(s1).toLowerCase();
		s2 = s2==null? null: String.valueOf(s2).toLowerCase();
		return strnatcmp(s1, s2);
	}
	
	private static final Pattern ADDSLASHES = Pattern.compile("('|\"|\\\\)");
	public static String addslashes(String str){
		if (str != null && str.length() > 0) {
			Matcher m = ADDSLASHES.matcher(str);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "\\\\");
				sb.append(m.group(1));
			}
			m.appendTail(sb);
			return sb.toString();
		} else {
			return str;
		}
	}
	
	private static final Pattern STRIPSLASHES = Pattern.compile("(\\\\)('|\"|\\\\)");
	public static String stripslashes(String str){
		if (str != null && str.length() > 0) {
			Matcher m = STRIPSLASHES.matcher(str);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, "");
				sb.append(m.group(2));
			}
			m.appendTail(sb);
			return sb.toString();
		} else {
			return str;
		}
	}
	
	/**
     * 返回字符串 string 第一个字符的 ASCII 码值。该函数是 chr() 的互补函数。
     * 
     * @param c 	一个字符。
     * @return		返回整型的 ASCII 码值。
     */
	@Deprecated
	public static int ord(char c){
		String s = String.valueOf(c);
		return s.getBytes()[0];
	}
	
	public static int ord(byte b) {
		if ((b & (1 << 7)) > 0) {
			return (1 << 7) + (b&(0xFF - (1<<7)));
		} else {
			return b;
		}
	}
	
	@Deprecated
	public static char chr(int c){
		return (char)c;
	}
	
	public static String dechex(int dec_number){
		return Integer.toHexString(dec_number);
	}
	public static int hexdec(String hex) {
		return Integer.valueOf(hex, 16);
	}

	public static CArray array_intersect(CArray _clear_templates,
			CArray<Object> array_keys) {
		// TODO Auto-generated method stub
		return _clear_templates;
	}

	public static Object unserialize(String asString) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 二进制转换为十进制
	 * 
	 * @param binstr
	 * @return
	 */
	public static Long bindec(String binstr) {
		return new BigInteger(binstr, 2).longValue();
	}

	public static CArray<String> range(int first, int second) {
		return range(first, second, 1);
	}
	
	public static CArray<String> range(int first, int second, int step) {
		CArray<String> arr = new CArray();
		for (int i = first; i <= second; i += step) {
			arr.add(String.valueOf(i));
		}
		return arr;
	}

	public static boolean is_callable(String _name) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static <T> CArray<T> array_filter(CArray<T> array) {
		CArrayFilter<T> filter = new CArrayFilter<T>(){
			@Override
			public boolean accept(T v) {
				return !empty(v);
			}};
		return array_filter(array, filter);
	}
	
	public static <T> CArray<T> array_filter(CArray<T> array, CArrayFilter<T> filter) {
		CArray<T> result = new CArray<T>();
		for (Entry<Object, T> e : array.entrySet()) {
			Object k = e.getKey();
			T v = e.getValue();
			if (filter.accept(v)) {
				result.put(k, v);
			}
		}
		return result;
	}

	public interface CArrayFilter<T> {
		boolean accept(T v);
	}
	
	public static CArray<String>preg_grep(String pattern, CArray<String> input) {
		return preg_grep(IPattern.compile(pattern), input);
	}
	
	public static CArray<String>preg_grep(IPattern pattern, CArray<String> input) {
		CArray<String> result = new CArray();
		 IMatcher m = null;
		if (input != null && !input.isEmpty()) {
			for (Entry<Object, String> e : input.entrySet()) {
				Object k = e.getKey();
				String v = e.getValue();
				m = pattern.matcher(v);
				if (m.find()) {
					result.put(k, v);
				}
			}
		}
		return result;
	}

	public static boolean in_array(int v, int[] ints, boolean b) {
		if (ints == null || ints.length == 0) {
			return false;
		}
		for (int i : ints) {
			if (i == v) {
				return true;
			}
		}
		return false;
	}

	public static <T> Integer array_search(T obj, T[] objs) {
		if (objs != null && objs.length > 0) {
			for(int i=0;i<objs.length;i++){
				if(obj == null && objs[i]==null){
					return i;
				}else	if(obj != null && objs[i]!=null && objs[i].equals(obj)){
					return i;
				}
			}
		}
		return null;
	}
	
	public static Object array_search(Object needle , CArray haystack) {
		if(haystack.isEmpty()){
			return null;
		}
		
		Object cv = null;
		for (Entry<Object, Object> e : ((CArray<Object>)haystack).entrySet()) {
		    Object k = e.getKey();
		    Object v = e.getValue();
		    if(needle == null && v==null){
		    	return k;
		    }		    
		    if(needle == null || v==null){
		    	continue;
		    }
			if(isPrimitive(needle)){
				cv = ConvertUtils.convert(needle, v.getClass());
				if(v.equals(cv)){
					return k;
				}
			} else {
				if(v.equals(needle)){
					return k;
				}
			}
		}
		return null;
	}

	public static IPattern PATTERN_STRIP_TAGS = IPattern.compile("<([^>]*)>", IPattern.CASE_INSENSITIVE);
	public static String strip_tags (String _str) {
		return strip_tags(_str, null);
	}
	public static String strip_tags(String _str, String _allowable_tags) {
		if(_allowable_tags != null) {
			throw new UnsupportedOperationException("strip_tags: " + _allowable_tags);
		}
		IMatcher m = PATTERN_STRIP_TAGS.matcher(_str);
		if(m.find()) {
			return m.replaceAll("");
		}
		return _str;
	}
	
	private final static Bindings BINDINGS_CPHP_METHODS = new SimpleBindings();
	static {
		Class[] clzs = {Cphp.class, FuncsUtil.class, CArray.class};
		
		Map<String, List<Method>> ms = array();
		List<Method> methods = new ArrayList();
		for(Class clz: clzs) {
			methods.addAll(Arrays.asList(clz.getMethods()));
		}
		
		for(Method m: methods) {
			String name = m.getName();
			List<Method> mList;
			if(ms.containsKey(name)) {
				mList = ms.get(name);
			}else {
				mList = new ArrayList<Method>();
				ms.put(name, mList);
			}
			mList.add(m);
		}
		
		for(Entry<String, List<Method>> entry: ms.entrySet()) {
			String name = entry.getKey();
			NativeJavaFunction f = new NativeJavaFunction(entry.getValue(), name);
			BINDINGS_CPHP_METHODS.put(entry.getKey(), f);
		}
	}
	
	public static Object eval(String s) {
		if(LOG.isDebugEnabled()) {
			LOG.debug(s + " \n" + RadarContext._REQUEST());
		}
		s = "(function() {"+s+"})()";
		
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("javascript");
		
		se.setBindings(BINDINGS_CPHP_METHODS, ScriptContext.ENGINE_SCOPE);
		
		Bindings b = new SimpleBindings();
		se.setBindings(b, ScriptContext.GLOBAL_SCOPE);
		
		Map request = RadarContext._REQUEST();
		b.put("_REQUEST", new NativeMap(request));
		
		try {
			return se.eval(s);
		} catch (ScriptException e) {
			if(LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		return null;
	}
	

	public static <T> CArray array_fill_keys(CArray keys, T value) {
		CArray<T> arr = new CArray<T>();
		if (keys != null && !keys.isEmpty()) {
			for (Object key : keys) {
				arr.put(key, Clone.deepcopy(value));
			}
		}
		return arr;
	}
	
	public static void main(String[] args) {
		String a = ltrim("10", "0");
		System.out.println(a);
	}

}
