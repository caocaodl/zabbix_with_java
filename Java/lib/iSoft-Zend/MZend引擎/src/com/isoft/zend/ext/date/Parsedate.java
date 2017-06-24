package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Structs.TIMELIB_UNSET;
import static java.lang.Character.isWhitespace;

import java.lang.reflect.Method;
import java.util.ArrayList;

import com.isoft.zend.ext.date.Structs.Scanner;
import com.isoft.zend.ext.date.Structs.timelib_error_container;
import com.isoft.zend.ext.date.Structs.timelib_error_message;
import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.timelib_tzdb;
import com.isoft.zend.ext.date.Structs.tref;

public class Parsedate {
	
	private Parsedate() {
	}
	
	private final static int YYMAXFILL = 31;
	
	protected final static int TIMELIB_SECOND = 1;
	protected final static int TIMELIB_MINUTE = 2;
	protected final static int TIMELIB_HOUR = 3;
	protected final static int TIMELIB_DAY = 4;
	protected final static int TIMELIB_MONTH = 5;
	protected final static int TIMELIB_YEAR = 6;
	protected final static int TIMELIB_WEEKDAY = 7;
	protected final static int TIMELIB_SPECIAL = 8;
	
	protected final static int EOI = 257;
	protected final static int TIME = 258;
	protected final static int DATE = 259;
	
	protected final static int TIMELIB_XMLRPC_SOAP = 260;
	protected final static int TIMELIB_TIME12 = 261;
	protected final static int TIMELIB_TIME24 = 262;
	protected final static int TIMELIB_GNU_NOCOLON = 263;
	protected final static int TIMELIB_GNU_NOCOLON_TZ = 264;
	protected final static int TIMELIB_ISO_NOCOLON = 265;
	
	protected final static int TIMELIB_AMERICAN = 266;
	protected final static int TIMELIB_ISO_DATE = 267;
	protected final static int TIMELIB_DATE_FULL = 268;
	protected final static int TIMELIB_DATE_TEXT = 269;
	protected final static int TIMELIB_DATE_NOCOLON = 270;
	protected final static int TIMELIB_PG_YEARDAY = 271;
	protected final static int TIMELIB_PG_TEXT = 272;
	protected final static int TIMELIB_PG_REVERSE = 273;
	protected final static int TIMELIB_CLF = 274;
	protected final static int TIMELIB_DATE_NO_DAY = 275;
	protected final static int TIMELIB_SHORTDATE_WITH_TIME = 276;
	protected final static int TIMELIB_DATE_FULL_POINTED = 277;
	protected final static int TIMELIB_TIME24_WITH_ZONE = 278;
	protected final static int TIMELIB_ISO_WEEK = 279;
	protected final static int TIMELIB_LF_DAY_OF_MONTH = 280;
	protected final static int TIMELIB_WEEK_DAY_OF_MONTH = 281;
	
	protected final static int TIMELIB_TIMEZONE = 300;
	protected final static int TIMELIB_AGO = 301;
	protected final static int TIMELIB_RELATIVE = 310;
	protected final static int TIMELIB_ERROR = 999;
	
	protected static void add_warning(Scanner s, String error) {
		s.errors.warning_count++;
		timelib_error_message e = new timelib_error_message();
		s.errors.warning_messages.add(e);
		e.position = s.tok;
		e.character = s.str[s.tok];
		e.message = error;
	}
	
	protected static void add_error(Scanner s, String error) {
		s.errors.error_count++;
		timelib_error_message w = new timelib_error_message();
		s.errors.error_messages.add(w);
		w.position = s.tok;
		w.character = s.str[s.tok];
		w.message = error;
	}
		
	protected static char[] timelib_string(Scanner s){
		char[] tmp = new char[s.cur - s.tok + 1];
		System.arraycopy(s.str, s.tok, tmp, 0, s.cur - s.tok);
		return tmp;
	}
	
	protected static timelib_time timelib_strtotime(char[] ch, tref<timelib_error_container> errors, timelib_tzdb tzdb, Method tz_get_wrapper){
		Scanner in = new Scanner();
		in.errors = new timelib_error_container();
		in.errors.warning_count = 0;
		in.errors.warning_messages = new ArrayList(0);
		in.errors.error_count = 0;
		in.errors.error_messages = new ArrayList(0);
		
		int len = ch.length;
		int s = 0;
		int e = s + len - 1;
		
		if (len > 0) {
			while (isWhitespace(ch[s]) && s < e) {
				s++;
			}
			while (isWhitespace(ch[e]) && e > s) {
				e--;
			}
		}
		if (e - s < 0) {
			in.time = new timelib_time();
			add_error(in, "Empty string");
			if (errors != null) {
				errors.v = in.errors;
			} else {
				in.errors = null;
			}
			in.time.y = in.time.d = in.time.m = in.time.h = in.time.i = in.time.s = TIMELIB_UNSET;
			in.time.dst = in.time.z = TIMELIB_UNSET;
			in.time.f = TIMELIB_UNSET;
			in.time.is_localtime = in.time.zone_type = 0;
			return in.time;
		}
		e++;
		in.str = new char[(e - s) + YYMAXFILL];
		System.arraycopy(ch, s, in.str, 0, (e - s));
		in.lim = (e - s) + YYMAXFILL;
		in.cur = 0;
		in.time = new timelib_time();
		in.time.y = TIMELIB_UNSET;
		in.time.d = TIMELIB_UNSET;
		in.time.m = TIMELIB_UNSET;
		in.time.h = TIMELIB_UNSET;
		in.time.i = TIMELIB_UNSET;
		in.time.s = TIMELIB_UNSET;
		in.time.f = TIMELIB_UNSET;
		in.time.z = (int)TIMELIB_UNSET;
		in.time.dst = (int)TIMELIB_UNSET;
		in.tzdb = tzdb;
		in.time.is_localtime = 0;
		in.time.zone_type = 0;
		in.time.relative.days = TIMELIB_UNSET;
		
		int t;
		Timescan timescan = new Timescan(in, tz_get_wrapper);
		do {
			t = timescan.scan();
		} while (t != EOI);
		
		if (errors != null) {
			errors.v = in.errors;
		} else {
			in.errors = null;
		}
		return in.time;
	}
}