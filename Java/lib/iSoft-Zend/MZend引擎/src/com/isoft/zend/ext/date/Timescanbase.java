package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Libc.strtod;
import static com.isoft.zend.ext.date.Libc.timelib_parse_tz_cor;
import static com.isoft.zend.ext.date.Libc.timelib_time_tz_abbr_update;
import static com.isoft.zend.ext.date.Parsedate.EOI;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_DAY;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_ERROR;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_HOUR;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_MINUTE;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_MONTH;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_SECOND;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_SPECIAL;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_WEEKDAY;
import static com.isoft.zend.ext.date.Parsedate.TIMELIB_YEAR;
import static com.isoft.zend.ext.date.Parsedate.add_error;
import static com.isoft.zend.ext.date.Parsedate.add_warning;
import static com.isoft.zend.ext.date.Parsedate.timelib_string;
import static com.isoft.zend.ext.date.Structs.TIMELIB_NO_CLONE;
import static com.isoft.zend.ext.date.Structs.TIMELIB_OVERRIDE_TIME;
import static com.isoft.zend.ext.date.Structs.TIMELIB_UNSET;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ABBR;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ID;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_OFFSET;
import static com.isoft.zend.ext.date.Timezonemap.timelib_month_lookup;
import static com.isoft.zend.ext.date.Timezonemap.timelib_reltext_lookup;
import static com.isoft.zend.ext.date.Timezonemap.timelib_relunit_lookup;
import static com.isoft.zend.ext.date.Timezonemap.timelib_timezone_fallbackmap;
import static com.isoft.zend.ext.date.Timezonemap.timelib_timezone_lookup;
import static com.isoft.zend.ext.date.Timezonemap.timelib_timezone_utc;
import static java.lang.Character.isWhitespace;

import java.lang.reflect.Method;

import com.isoft.lang.Clone;
import com.isoft.zend.ext.core.ZException;
import com.isoft.zend.ext.date.Structs.Scanner;
import com.isoft.zend.ext.date.Structs.timelib_lookup_table;
import com.isoft.zend.ext.date.Structs.timelib_relunit;
import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.timelib_tz_lookup_table;
import com.isoft.zend.ext.date.Structs.timelib_tzdb;
import com.isoft.zend.ext.date.Structs.timelib_tzinfo;
import com.isoft.zend.ext.date.Structs.tref;

public class Timescanbase {

	protected Scanner s;
	protected char[] ch;
	
	protected Method tz_get_wrapper;
	protected int cursor;
	protected int str, ptr;
	
	protected char yych;
	protected int yyaccept;
	
	protected int code;

	protected void TIMELIB_HAVE_TIME() {
		if (s.time.have_time > 0) {
			add_error(s, "Double time specification");
			timelib_string_free();
			throw new ZException(TIMELIB_ERROR);
		} else {
			s.time.have_time = 1;
			s.time.h = 0;
			s.time.i = 0;
			s.time.s = 0;
			s.time.f = 0;
		}
	}
	
	protected void TIMELIB_UNHAVE_TIME() {
		s.time.have_time = 0;
		s.time.h = 0;
		s.time.i = 0;
		s.time.s = 0;
		s.time.f = 0;
	}
	
	protected void TIMELIB_HAVE_DATE() {
		if (s.time.have_date>0) {
			add_error(s, "Double date specification");
			timelib_string_free();
			throw new ZException(TIMELIB_ERROR);
		} else {
			s.time.have_date = 1;
		}
	}
	
	protected void TIMELIB_UNHAVE_DATE() {
		s.time.have_date = 0;
		s.time.d = 0;
		s.time.m = 0;
		s.time.y = 0;
	}
	
	protected void TIMELIB_HAVE_RELATIVE() {
		s.time.have_relative = 1;
	}
	
	protected void TIMELIB_HAVE_WEEKDAY_RELATIVE() {
		s.time.have_relative = 1;
		s.time.relative.have_weekday_relative = 1;
	}
	
	protected void TIMELIB_HAVE_SPECIAL_RELATIVE() {
		s.time.have_relative = 1;
		s.time.relative.have_special_relative = 1;
	}
	
	protected void TIMELIB_HAVE_TZ() { 
		s.cur = cursor; 
		if (s.time.have_zone>0) { 
			if (s.time.have_zone > 1)
				add_error(s, "Double timezone specification");
			else
				add_warning(s, "Double timezone specification");
			timelib_string_free(); 
			s.time.have_zone++;
			throw new ZException(TIMELIB_ERROR);
		} else {
			s.time.have_zone++;
		}
	}
	
	protected void TIMELIB_INIT(){
		s.cur = cursor; 
		ch = timelib_string(s);
		str = 0;
		ptr = str;
	}
	
	protected void TIMELIB_DEINIT(){
		timelib_string_free();
	}
	
	protected void TIMELIB_PROCESS_YEAR(tref<Long> x, int l) {
		if (((x.v) == TIMELIB_UNSET) || ((l) >= 4)) {
			/*	(x) = 0; */
		} else if ((x.v) < 100) {
			if ((x.v) < 70) {
				(x.v) += 2000;
			} else {
				(x.v) += 1900;
			}
		}
	}
	
	protected void YYFILL(int code) {
		throw new ZException(EOI);
	}
	
	protected void DEBUG_OUTPUT(String s){
		//System.out.println(s);
	}
	
	protected void timelib_string_free(){
		ptr = 0;
		ch = null;
	}
	
	protected long timelib_meridian(long h) {
		long retval = 0;
		while ("AaPp".indexOf(ch[ptr])==-1) {
			++ptr;
		}
		if (ch[ptr] == 'a' || ch[ptr] == 'A') {
			if (h == 12) {
				retval = -12;
			}
		} else if (h != 12) {
			retval = 12;
		}
		++ptr;
		if (ch[ptr] == '.') {
			ptr += 3;
		} else {
			++ptr;
		}
		return retval;
	}
	
	protected long timelib_get_nr_ex(int max_length, tref<Integer> scanned_length) {
		int begin, end;
		long tmp_nr = TIMELIB_UNSET;
		int len = 0;

		while ((ch[ptr] < '0') || (ch[ptr] > '9')) {
			if (ch[ptr] == '\0') {
				return TIMELIB_UNSET;
			}
			++ptr;
		}
		begin = ptr;
		while ((ch[ptr] >= '0') && (ch[ptr] <= '9') && len < max_length) {
			++ptr;
			++len;
		}
		end = ptr;
		if (scanned_length !=null && scanned_length.v>0) {
			scanned_length.v = end - begin;
		}
		String str = String.valueOf(ch, begin, end - begin);
		tmp_nr = Long.valueOf(str);
		str = null;
		return tmp_nr;
	}
	
	protected long timelib_get_nr(int max_length) {
		return timelib_get_nr_ex(max_length, null);
	}
	
	protected void timelib_skip_day_suffix() {
		if (isWhitespace(ch[ptr])) {
			return;
		}
		String suffix = String.valueOf(ch, ptr, 2);
		if (!suffix.equalsIgnoreCase("nd") || !suffix.equalsIgnoreCase("rd") ||!suffix.equalsIgnoreCase("st") || !suffix.equalsIgnoreCase("th")) {
			ptr += 2;
		}
		suffix = null;
	}
	
	protected double timelib_get_frac_nr(int max_length) {
		int begin, end;
		double tmp_nr = TIMELIB_UNSET;
		int len = 0;

		while ((ch[ptr] != '.') && (ch[ptr] != ':') && ((ch[ptr] < '0') || (ch[ptr] > '9'))) {
			if (ch[ptr] == '\0') {
				return TIMELIB_UNSET;
			}
			++ptr;
		}
		begin = ptr;
		while (((ch[ptr] == '.') || (ch[ptr] == ':') || ((ch[ptr] >= '0') && (ch[ptr] <= '9'))) && len < max_length) {
			++ptr;
			++len;
		}
		end = ptr;
		char[] str = String.valueOf(ch, begin, end - begin).toCharArray();
		if (str[0] == ':') {
			str[0] = '.';
		}
		tmp_nr = strtod(str, 0);
		str = null;
		return tmp_nr;
	}
	
	protected long timelib_get_unsigned_nr(int max_length) {
		long dir = 1;

		while (((ch[ptr] < '0') || (ch[ptr] > '9')) && (ch[ptr] != '+') && (ch[ptr] != '-')) {
			if (ch[ptr] == '\0') {
				return TIMELIB_UNSET;
			}
			++ptr;
		}

		while (ch[ptr] == '+' || ch[ptr] == '-') {
			if (ch[ptr] == '-') {
				dir *= -1;
			}
			++ptr;
		}
		return dir * timelib_get_nr(max_length);
	}
	
	protected long timelib_lookup_relative_text(tref<Integer> behavior) {
		int begin = ptr, end;
		long value = 0;

		while ((ch[ptr] >= 'A' && ch[ptr] <= 'Z') || (ch[ptr] >= 'a' && ch[ptr] <= 'z')) {
			++ptr;
		}
		end = ptr;
		String word = String.valueOf(ch, begin, end - begin);

		for (timelib_lookup_table tp : timelib_reltext_lookup) {
			if(tp.name == null){
				break;
			}
			if (tp.name.equalsIgnoreCase(word)) {
				value = tp.value;
				behavior.v = tp.type;
				break;
			}
		}

		word = null;
		return value;
	}
	
	protected long timelib_get_relative_text(tref<Integer> behavior) {
		while (ch[ptr] == ' ' || ch[ptr] == '\t' || ch[ptr] == '-' || ch[ptr] == '/') {
			++ptr;
		}
		return timelib_lookup_relative_text(behavior);
	}
	
	protected long timelib_lookup_month() {
		int begin = ptr, end;
		long  value = 0;
		while ((ch[ptr] >= 'A' && ch[ptr] <= 'Z') || (ch[ptr] >= 'a' && ch[ptr] <= 'z')) {
			++ptr;
		}
		end = ptr;
		String word = String.valueOf(ch, begin, end - begin);
		for (timelib_lookup_table tp : timelib_month_lookup) {
			if(tp.name == null){
				break;
			}
			if (tp.name.equalsIgnoreCase(word)) {
				value = tp.value;
				break;
			}
		}
		word = null;
		return value;
	}
		
	protected long timelib_get_month(){
		while (ch[ptr] == ' ' || ch[ptr] == '\t' || ch[ptr] == '-' || ch[ptr] == '.' || ch[ptr] == '/') {
			++ptr;
		}
		return timelib_lookup_month();
	}
	
	protected void timelib_eat_spaces() {
		while (ch[ptr] == ' ' || ch[ptr] == '\t') {
			++ptr;
		}
	}
	
	protected timelib_relunit timelib_lookup_relunit() {
		int begin = ptr, end;

		while (ch[ptr] != '\0' && ch[ptr] != ' ' && ch[ptr] != ',' && ch[ptr] != '\t' && ch[ptr] != ';' && ch[ptr] != ':' &&
				ch[ptr] != '/' && ch[ptr] != '.' && ch[ptr] != '-' && ch[ptr] != '(' && ch[ptr] != ')' ) {
			++ptr;
		}
		end = ptr;
		String word = String.valueOf(ch, begin, end - begin);

		timelib_relunit value = null;
		for (timelib_relunit tp: timelib_relunit_lookup) {
			if(tp.name==null){
				break;
			}
			if (tp.name.equalsIgnoreCase(word)) {
				value = tp;
				break;
			}
		}
		
		word = null;
		return value;
	}
	
	protected void timelib_set_relative(long amount, int behavior, Scanner s){
		timelib_relunit relunit = null;

		if ((relunit = timelib_lookup_relunit())==null) {
			return;
		}

		switch (relunit.unit) {
			case TIMELIB_SECOND: s.time.relative.s += amount * relunit.multiplier; break;
			case TIMELIB_MINUTE: s.time.relative.i += amount * relunit.multiplier; break;
			case TIMELIB_HOUR:   s.time.relative.h += amount * relunit.multiplier; break;
			case TIMELIB_DAY:    s.time.relative.d += amount * relunit.multiplier; break;
			case TIMELIB_MONTH:  s.time.relative.m += amount * relunit.multiplier; break;
			case TIMELIB_YEAR:   s.time.relative.y += amount * relunit.multiplier; break;

			case TIMELIB_WEEKDAY:
				TIMELIB_HAVE_WEEKDAY_RELATIVE();
				TIMELIB_UNHAVE_TIME();
				s.time.relative.d += (amount > 0 ? amount - 1 : amount) * 7;
				s.time.relative.weekday = relunit.multiplier;
				s.time.relative.weekday_behavior = behavior;
				break;

			case TIMELIB_SPECIAL:
				TIMELIB_HAVE_SPECIAL_RELATIVE();
				TIMELIB_UNHAVE_TIME();
				s.time.relative.special.type = relunit.multiplier;
				s.time.relative.special.amount = amount;
		}
	}
	
	protected timelib_tz_lookup_table abbr_search(String word, long gmtoffset, int isdst) {
		boolean first_found = false;
		timelib_tz_lookup_table  first_found_elem = null;

		if ("utc".equalsIgnoreCase(word) || "gmt".equalsIgnoreCase(word)) {
			return timelib_timezone_utc;
		}
		
		for (timelib_tz_lookup_table tp : timelib_timezone_lookup) {
			if(tp.name==null){
				break;
			}
			if (tp.name.equalsIgnoreCase(word)) {
				if (!first_found) {
					first_found = true;
					first_found_elem = tp;
					if (gmtoffset == -1) {
						return tp;
					}
				}
				if (tp.gmtoffset == gmtoffset) {
					return tp;
				}
			}
		}
		if (first_found) {
			return first_found_elem;
		}

		/* Still didn't find anything, let's find the zone solely based on
		 * offset/isdst then */
		for (timelib_tz_lookup_table fmp : timelib_timezone_fallbackmap) {
			if(fmp.name==null){
				break;
			}
			if ((fmp.gmtoffset * 60) == gmtoffset && fmp.type == isdst) {
				return fmp;
			}
		}
		return null;
	}
	
	protected long timelib_lookup_abbr(char[] ch, tref<Long> dst, tref<String> tz_abbr, tref<Integer> found) {
		int begin = ptr, end;
		long  value = 0;

		while (ch[ptr] != '\0' && ch[ptr] != ')' && ch[ptr] != ' ') {
			++ptr;
		}
		end = ptr;
		String word = String.valueOf(ch, begin, end - begin);

		timelib_tz_lookup_table tp;
		if ((tp = abbr_search(word, -1, 0))!=null) {
			value = -(long)(tp.gmtoffset / 60);
			dst.v = (long)tp.type;
			value += tp.type * 60;
			found.v = 1;
		} else {
			found.v = 0;
		}

		tz_abbr.v = word;
		return value;
	}
	
	protected long timelib_parse_zone(tref<Long> dst, timelib_time t, tref<Integer> tz_not_found, timelib_tzdb tzdb, Method tz_wrapper) {
		timelib_tzinfo res = null;
		long retval = 0;

		tz_not_found.v = 0;

		while (ch[ptr] == ' ' || ch[ptr] == '\t' || ch[ptr] == '(') {
			++ptr;
		}
		if (ch[ptr] == 'G' && ch[ptr+1] == 'M' && ch[ptr+2] == 'T' && (ch[ptr+3] == '+' || ch[ptr+3] == '-')) {
			ptr += 3;
		}
		if (ch[ptr] == '+') {
			++ptr;
			t.is_localtime = 1;
			t.zone_type = TIMELIB_ZONETYPE_OFFSET;
			tz_not_found.v = 0;
			t.dst = 0;
			retval = -1 * timelib_parse_tz_cor(ch, ptr);
		} else if (ch[ptr] == '-') {
			++ptr;
			t.is_localtime = 1;
			t.zone_type = TIMELIB_ZONETYPE_OFFSET;
			tz_not_found.v = 0;
			t.dst = 0;
			retval = timelib_parse_tz_cor(ch, ptr);
		} else {
			tref<Integer> found = new tref<Integer>(0);
			tref<String> tz_abbr = new tref<String>(null);

			t.is_localtime = 1;

			/* First, we lookup by abbreviation only */
			long offset = timelib_lookup_abbr(ch, dst, tz_abbr, found);
			if (found.v>0) {
				t.zone_type = TIMELIB_ZONETYPE_ABBR;
				timelib_time_tz_abbr_update(t, tz_abbr);
			}

			/* Otherwise, we look if we have a TimeZone identifier */
			if (found.v==0 || "UTC".equals(tz_abbr.v)) {
				if(tz_wrapper != null){
					try {
						res = (timelib_tzinfo)tz_wrapper.invoke(null, tz_abbr, tzdb);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (res != null) {
					t.tz_info = res;
					t.zone_type = TIMELIB_ZONETYPE_ID;
					found.v++;
				}
			}
			tz_abbr = null;
			tz_not_found.v = (found.v == 0 ? 1 : 0);
			retval = offset;
		}
		while (ch[ptr] == ')') {
			++ptr;
		}
		return retval;
	}
	
	protected static void timelib_fill_holes(timelib_time parsed, timelib_time now, int options){
		if (!((options & TIMELIB_OVERRIDE_TIME)>0) && parsed.have_date>0 && !(parsed.have_time>0)) {
			parsed.h = 0;
			parsed.i = 0;
			parsed.s = 0;
			parsed.f = 0;
		}
		if (parsed.y == TIMELIB_UNSET) parsed.y = now.y != TIMELIB_UNSET ? now.y : 0;
		if (parsed.d == TIMELIB_UNSET) parsed.d = now.d != TIMELIB_UNSET ? now.d : 0;
		if (parsed.m == TIMELIB_UNSET) parsed.m = now.m != TIMELIB_UNSET ? now.m : 0;
		if (parsed.h == TIMELIB_UNSET) parsed.h = now.h != TIMELIB_UNSET ? now.h : 0;
		if (parsed.i == TIMELIB_UNSET) parsed.i = now.i != TIMELIB_UNSET ? now.i : 0;
		if (parsed.s == TIMELIB_UNSET) parsed.s = now.s != TIMELIB_UNSET ? now.s : 0;
		if (parsed.f == TIMELIB_UNSET) parsed.f = now.f != TIMELIB_UNSET ? now.f : 0;
		if (parsed.z == TIMELIB_UNSET) parsed.z = now.z != TIMELIB_UNSET ? now.z : 0;
		if (parsed.dst == TIMELIB_UNSET) parsed.dst = now.dst != TIMELIB_UNSET ? now.dst : 0;

		if (parsed.tz_abbr==null) {
			parsed.tz_abbr = now.tz_abbr!=null ? now.tz_abbr : null;
		}
		if (parsed.tz_info==null) {
			parsed.tz_info = now.tz_info!=null ? (!((options & TIMELIB_NO_CLONE)>0) ? Clone.deepcopy(now.tz_info) : now.tz_info) : null;
		}
		if (parsed.zone_type == 0 && now.zone_type != 0) {
			parsed.zone_type = now.zone_type;
			parsed.is_localtime = 1;
		}
	}

	
	protected int scan() {
		this.cursor = s.cur;
		this.ch = null;
		this.str = 0;
		this.ptr = 0;
		try {
			std();
		} catch (ZException e) {
			this.code = e.getCode();
		}
		s.cur = this.cursor;
		return this.code;
	}

	protected void std() {
	}

}
