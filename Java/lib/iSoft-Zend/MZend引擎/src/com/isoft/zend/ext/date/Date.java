package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Dow.timelib_day_of_week;
import static com.isoft.zend.ext.date.Dow.timelib_day_of_year;
import static com.isoft.zend.ext.date.Dow.timelib_days_in_month;
import static com.isoft.zend.ext.date.Dow.timelib_iso_day_of_week;
import static com.isoft.zend.ext.date.Dow.timelib_isoweek_from_date;
import static com.isoft.zend.ext.date.Errors.E_ERROR;
import static com.isoft.zend.ext.date.Libc.slprintf;
import static com.isoft.zend.ext.date.Libc.time;
import static com.isoft.zend.ext.date.Libc.timelib_date_to_int;
import static com.isoft.zend.ext.date.Parsedate.timelib_strtotime;
import static com.isoft.zend.ext.date.Parsetz.timelib_parse_tzfile;
import static com.isoft.zend.ext.date.Structs.TIMELIB_NO_CLONE;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ABBR;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ID;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_OFFSET;
import static com.isoft.zend.ext.date.Structs.timelib_is_leap;
import static com.isoft.zend.ext.date.Timescanbase.timelib_fill_holes;
import static com.isoft.zend.ext.date.Timezonedb.DATE_TIMEZONEDB;
import static com.isoft.zend.ext.date.Timezonedb.timezonedb_builtin;
import static com.isoft.zend.ext.date.Tm2unixtime.timelib_update_ts;
import static com.isoft.zend.ext.date.Unixtime2tm.timelib_unixtime2gmt;
import static com.isoft.zend.ext.date.Unixtime2tm.timelib_unixtime2local;
import static java.lang.Math.abs;
import static java.lang.Math.floor;

import java.lang.reflect.Method;
import java.util.TimeZone;

import com.isoft.zend.ext.core.ZException;
import com.isoft.zend.ext.date.Structs.rlong;
import com.isoft.zend.ext.date.Structs.timelib_error_container;
import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.timelib_time_offset;
import com.isoft.zend.ext.date.Structs.timelib_tzdb;
import com.isoft.zend.ext.date.Structs.timelib_tzinfo;
import com.isoft.zend.ext.date.Structs.tref;

public class Date {
	
	private Date() {
	}
	
	protected static Method php_date_parse_tzfile_wrapper ;
	
	static{
		try {
			php_date_parse_tzfile_wrapper = Date.class.getMethod("php_date_parse_tzfile_wrapper", String.class, timelib_tzdb.class);
		} catch (Exception e) {
			php_date_parse_tzfile_wrapper = null;
		}
	}
	
	protected static timelib_tzinfo php_date_parse_tzfile(String formal_tzname, timelib_tzdb tzdb) {
		return timelib_parse_tzfile(formal_tzname, tzdb);
	}
	
	protected static timelib_tzinfo php_date_parse_tzfile_wrapper(String formal_tzname, timelib_tzdb tzdb) {
		return php_date_parse_tzfile(formal_tzname, tzdb) ;
	}
	
	protected static String guess_timezone() {
		return TimeZone.getDefault().getID();
	}
	
	protected static timelib_tzinfo get_timezone_info() {
		String tz = guess_timezone();
		timelib_tzinfo tzi = php_date_parse_tzfile(tz, DATE_TIMEZONEDB);
		if (tzi == null) {
			throw new ZException(E_ERROR, "Timezone database is corrupt - this should *never* happen!");
		}
		return tzi;
	}
	
	private static String[] mon_full_names = {
		"January", "February", "March", "April",
		"May", "June", "July", "August",
		"September", "October", "November", "December"
	};

	private static String[] mon_short_names = {
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	};

	private static String[] day_full_names = {
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	};

	private static String[] day_short_names = {
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
	};

	protected static String english_suffix(long number) {
		if (number >= 10 && number <= 19) {
			return "th";
		} else {
			switch ((int) (number % 10)) {
			case 1: return "st";
			case 2: return "nd";
			case 3: return "rd";
			}
		}
		return "th";
	}

	protected static String php_date_full_day_name(long y, long m, long d) {
		int day_of_week = (int)timelib_day_of_week(y, m, d);
		if (day_of_week < 0) {
			return "Unknown";
		} 
		return day_full_names[day_of_week];	
	}

	protected static String php_date_short_day_name(long y, long m, long d) {
		int day_of_week = (int)timelib_day_of_week(y, m, d);
		if (day_of_week < 0) {
			return "Unknown";
		} 
		return day_short_names[day_of_week];	
	}

	/* date_format - (gm)date helper */
	protected static String date_format(String sformat, timelib_time t, boolean localtime) {
		char[] format = sformat != null ? sformat.toCharArray() : new char[0];
		int format_len = format.length;
		
		if (format_len==0) {
			return "";
		}
		
		StringBuilder buffer = new StringBuilder();
		timelib_time_offset offset = new timelib_time_offset();
		rlong isoweek = new rlong(0), isoyear = new rlong(0);
		int rfc_colon;
		boolean weekYearSet = false;
		
		for (int i = 0; i < format_len; i++) {
			rfc_colon = 0;
			switch (format[i]) {
			/* day */
			case 'd': slprintf(buffer, 32, "%02d", t.d); break;
			case 'D': slprintf(buffer, 32, "%s", php_date_short_day_name(t.y, t.m, t.d)); break;
			case 'j':   slprintf(buffer, 32, "%d", t.d); break;
			case 'l':   slprintf(buffer, 32, "%s", php_date_full_day_name(t.y, t.m, t.d)); break;
			case 'S':  slprintf(buffer, 32, "%s", english_suffix(t.d)); break;
			case 'w':  slprintf(buffer, 32, "%d", timelib_day_of_week(t.y, t.m, t.d)); break;
			case 'N':  slprintf(buffer, 32, "%d", timelib_iso_day_of_week(t.y, t.m, t.d)); break;
			case 'z':   slprintf(buffer, 32, "%d", timelib_day_of_year(t.y, t.m, t.d)); break;
			
			/* week */
			case 'W':
				if(!weekYearSet) { timelib_isoweek_from_date(t.y, t.m, t.d, isoweek, isoyear); weekYearSet = true; }
				slprintf(buffer, 32, "%02d", isoweek.v); break; /* iso weeknr */
			case 'o':
				if(!weekYearSet) { timelib_isoweek_from_date(t.y, t.m, t.d, isoweek, isoyear); weekYearSet = true; }
				slprintf(buffer, 32, "%d", isoyear.v); break; /* iso year */
				
			/* month */
			case 'F' : slprintf(buffer, 32, "%s", mon_full_names[(int) t.m - 1]); break;
			case 'm': slprintf(buffer, 32, "%02d", t.m); break;
			case 'M': slprintf(buffer, 32, "%s", mon_short_names[(int) t.m - 1]); break;
			case 'n' : slprintf(buffer, 32, "%d", t.m); break;
			case 't'  : slprintf(buffer, 32, "%d", timelib_days_in_month(t.y, t.m)); break;
			
			/* year */
			case 'L': slprintf(buffer, 32, "%d", timelib_is_leap(t.y)); break;
			case 'y': slprintf(buffer, 32, "%02d", t.y % 100); break;
			case 'Y': slprintf(buffer, 32, "%s%04d", t.y < 0 ? "-" : "", abs(t.y)); break;

			/* time */
			case 'a': slprintf(buffer, 32, "%s", t.h >= 12 ? "pm" : "am"); break;
			case 'A': slprintf(buffer, 32, "%s", t.h >= 12 ? "PM" : "AM"); break;
			case 'B': {
				long retval = (((t.sse-(t.sse - ((t.sse % 86400) + 3600))) * 10) / 864);			
				while (retval < 0) {
					retval += 1000;
				}
				retval = retval % 1000;
				slprintf(buffer, 32, "%03d", retval);
				break;
			}
			case 'g': slprintf(buffer, 32, "%d", (t.h % 12>0) ? t.h % 12 : 12); break;
			case 'G': slprintf(buffer, 32, "%d", t.h); break;
			case 'h': slprintf(buffer, 32, "%02d", (t.h % 12>0) ? t.h % 12 : 12); break;
			case 'H': slprintf(buffer, 32, "%02d", t.h); break;
			case 'i': slprintf(buffer, 32, "%02d", t.i); break;
			case 's': slprintf(buffer, 32, "%02d", t.s); break;
			case 'u': slprintf(buffer, 32, "%06d", floor(t.f * 1000000 + 0.5)); break;

			/* timezone */
			case 'I': slprintf(buffer, 32, "%d", localtime ? offset.is_dst : 0); break;
			case 'P': rfc_colon = 1; /* break intentionally missing */
			case 'O': slprintf(buffer, 32, "%c%02d%s%02d",
											localtime ? ((offset.offset < 0) ? '-' : '+') : '+',
											localtime ? abs(offset.offset / 3600) : 0,
											rfc_colon>0 ? ":" : "",
											localtime ? abs((offset.offset % 3600) / 60) : 0
							  );
					  break;
			case 'T': slprintf(buffer, 32, "%s", localtime ? offset.abbr : "GMT"); break;
			case 'e': if (!localtime) {
					      slprintf(buffer, 32, "%s", "UTC");
					  } else {
						  switch (t.zone_type) {
							  case TIMELIB_ZONETYPE_ID:
								  slprintf(buffer, 32, "%s", t.tz_info.name);
								  break;
							  case TIMELIB_ZONETYPE_ABBR:
								  slprintf(buffer, 32, "%s", offset.abbr);
								  break;
							  case TIMELIB_ZONETYPE_OFFSET:
								  slprintf(buffer, 32, "%c%02d:%02d",
												((offset.offset < 0) ? '-' : '+'),
												abs(offset.offset / 3600),
												abs((offset.offset % 3600) / 60)
										   );
								  break;
						  }
					  }
					  break;
			case 'Z': slprintf(buffer, 32, "%d", localtime ? offset.offset : 0); break;

			/* full date/time */
			case 'c': slprintf(buffer, 96, "%04d-%02d-%02dT%02d:%02d:%02d%c%02d:%02d",
							                t.y, t.m, t.d,
											t.h, t.i, t.s,
											localtime ? ((offset.offset < 0) ? '-' : '+') : '+',
											localtime ? abs(offset.offset / 3600) : 0,
											localtime ? abs((offset.offset % 3600) / 60) : 0
							  );
					  break;
			case 'r': slprintf(buffer, 96, "%3s, %02d %3s %04d %02d:%02d:%02d %c%02d%02d",
							                php_date_short_day_name(t.y, t.m, t.d),
											t.d, mon_short_names[(int)t.m - 1],
											t.y, t.h, t.i, t.s,
											localtime ? ((offset.offset < 0) ? '-' : '+') : '+',
											localtime ? abs(offset.offset / 3600) : 0,
											localtime ? abs((offset.offset % 3600) / 60) : 0
							  );
					  break;
			case 'U': slprintf(buffer, 32, "%d", t.sse); break;

			case '\\': if (i < format_len) i++; /* break intentionally missing */
			
			default: buffer.append(format[i]); break;
			}
		}
		
		return buffer.toString();
	}

	protected static String php_date(String format, Long ts, boolean localtime){
		if (ts == null) {
			ts = time();
		}
		return php_format_date(format, ts, localtime);
	}
	
	protected static String php_format_date(String format, long ts, boolean localtime) {
		timelib_time  t = new timelib_time();
		timelib_tzinfo tzi;
		if (localtime) {
			tzi = get_timezone_info();
			t.tz_info = tzi;
			t.zone_type = TIMELIB_ZONETYPE_ID;
			timelib_unixtime2local(t, ts);
		} else {
			tzi = null;
			timelib_unixtime2gmt(t, ts);
		}
		return date_format(format, t, localtime);
	}
	
	protected static Long strtotime(String time, Long  preset_ts){
		timelib_time t, now;
		
		char[] times = time.toCharArray();
		int time_len = times.length;
		
		timelib_tzinfo tzi = get_timezone_info();
		if (preset_ts != null) {
			/* We have an initial timestamp */
			now = new timelib_time();
			String initial_ts = String.format("@%d UTC", preset_ts);
			t = timelib_strtotime(initial_ts.toCharArray(), null, timezonedb_builtin, null);
			timelib_update_ts(t, tzi);
			now.tz_info = tzi;
			now.zone_type = TIMELIB_ZONETYPE_ID;
			timelib_unixtime2local(now, t.sse);
			t = null;
		} else {
			/* We have no initial timestamp */
			now = new timelib_time();
			now.tz_info = tzi;
			now.zone_type = TIMELIB_ZONETYPE_ID;
			timelib_unixtime2local(now, time());
		}
		
		if (time_len==0) {
			now = null;	
			return null;
		}
		
		tref<timelib_error_container> error = new tref<timelib_error_container>(null);
		t = timelib_strtotime(times, error,  DATE_TIMEZONEDB, php_date_parse_tzfile_wrapper);
		int error1 = error.v.error_count;
		error = null;
		timelib_fill_holes(t, now, TIMELIB_NO_CLONE);
		timelib_update_ts(t, tzi);
		tref<Integer> error2 = new tref<Integer>(0);
		long ts = timelib_date_to_int(t, error2);
		
		if (error1>0 || error2.v>0) {
			return null;
		}
		return ts;
	}
}
