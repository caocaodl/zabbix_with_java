package com.isoft.zend.ext.date;

import static java.lang.Character.isDigit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.tref;

public class Libc {
	
	private Libc() {
	}
	
	protected final static long LONG_MAX = 2147483647L;
	protected final static long LONG_MIN = (- LONG_MAX - 1);
	
	protected static long strtol(char[] ch, int ptr, int base){
		int endptr;
		for (endptr = ptr; endptr < ch.length; endptr++) {
			if (!isDigit(ch[endptr])) {
				break;
			}
		}
		return strtol(ch, ptr, endptr, base);
	}
	
	protected static long strtol(char[] ch, int ptr, int endptr, int base){
		return NumberUtils.toLong(String.valueOf(ch, ptr, endptr - ptr));
	}
	
	protected static double strtod(char[] ch, int ptr){
		int endptr;
		boolean dot = false;
		for (endptr = ptr; endptr < ch.length; endptr++) {
			if (!isDigit(ch[endptr]) && !(ch[endptr] == '.' && !dot)) {
				break;
			}
			if(ch[endptr] == '.'){
				dot = true;
			}
		}
		return strtod(ch, ptr, endptr);
	}
	
	protected static double strtod(char[] ch, int ptr, int endptr){
		return NumberUtils.toDouble(String.valueOf(ch, ptr, endptr - ptr));
	}
	
	protected static void slprintf(StringBuilder buffer, int i, String fmt, Object... args) {
		buffer.append(String.format(fmt, args));
	}
	
	protected final static int UNIXTIME_OFFSET = 1000;

	protected static long time(Long time) {
		if(time == null) {
			time = System.currentTimeMillis();
		}
		return Long.valueOf(time / UNIXTIME_OFFSET);
	}
	
	protected static long time() {
		return time(System.currentTimeMillis());
	}
	
	protected static long HOUR(long a) {
		return a * 60;
	}
	
	protected static void timelib_time_tz_abbr_update(timelib_time tm, tref<String> tz_abbr) {
		tm.tz_abbr = null;
		tm.tz_abbr = StringUtils.upperCase(tz_abbr.v);
	}
	
	protected static long timelib_date_to_int(timelib_time d, tref<Integer> error) {
		long ts = d.sse;
		if (ts < LONG_MIN || ts > LONG_MAX) {
			if (error != null) {
				error.v = 1;
			}
			return 0;
		}
		if (error != null) {
			error.v = 0;
		}
		return d.sse;
	}
	
	protected static long timelib_parse_tz_cor(char[] ch, int ptr) {
	        int begin = ptr, end;
	        long  tmp;
	        
	        while (isDigit(ch[ptr]) || ch[ptr] == ':') {
	                ++ptr;
	        }
	        end = ptr;
	        switch (end - begin) {
	                case 1:
	                case 2:
	                        return HOUR(strtol(ch, begin, 10));
	                case 3:
	                case 4:
	                        if (ch[begin+1] == ':') {
	                                tmp = HOUR(strtol(ch, begin, 10)) + strtol(ch, begin + 2, 10);
	                                return tmp;
	                        } else if (ch[begin+2] == ':') {
	                                tmp = HOUR(strtol(ch, begin, 10)) + strtol(ch, begin + 3, 10);
	                                return tmp;
	                        } else {
	                                tmp = strtol(ch, begin, 10);
	                                return HOUR(tmp / 100) + tmp % 100;
	                        }
	                case 5:
	                        tmp = HOUR(strtol(ch, begin, 10)) + strtol(ch, begin + 3, 10);
	                        return tmp;
	        }
	        return 0;
	}

}
