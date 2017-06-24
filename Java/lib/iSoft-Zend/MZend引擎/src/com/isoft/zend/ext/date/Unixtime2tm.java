package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Libc.timelib_time_tz_abbr_update;
import static com.isoft.zend.ext.date.Parsetz.timelib_get_time_zone_info;
import static com.isoft.zend.ext.date.Structs.DAYS_PER_LYEAR;
import static com.isoft.zend.ext.date.Structs.DAYS_PER_LYEAR_PERIOD;
import static com.isoft.zend.ext.date.Structs.DAYS_PER_YEAR;
import static com.isoft.zend.ext.date.Structs.SECS_PER_DAY;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ABBR;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ID;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_OFFSET;
import static com.isoft.zend.ext.date.Structs.YEARS_PER_LYEAR_PERIOD;
import static com.isoft.zend.ext.date.Structs.timelib_is_leap;

import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.timelib_time_offset;
import com.isoft.zend.ext.date.Structs.timelib_tzinfo;
import com.isoft.zend.ext.date.Structs.tref;

public class Unixtime2tm {

	private final static int[] month_tab_leap = { -1, 30, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
	private final static int[] month_tab = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
	
	/* Converts a Unix timestamp value into broken down time, in GMT */
	protected static void timelib_unixtime2gmt(timelib_time tm, long ts){
		long days, remainder, tmp_days;
		long cur_year = 1970;
		long hours, minutes, seconds;
		int[] months;
		
		days = ts / SECS_PER_DAY;
		remainder = ts - (days * SECS_PER_DAY);
		if (ts < 0 && remainder == 0) {
			days++;
			remainder -= SECS_PER_DAY;
		}
		
		if (ts >= 0) {
			tmp_days = days + 1;
	
			if (tmp_days >= DAYS_PER_LYEAR_PERIOD || tmp_days <= -DAYS_PER_LYEAR_PERIOD) {
				cur_year += YEARS_PER_LYEAR_PERIOD * (tmp_days / DAYS_PER_LYEAR_PERIOD);
				tmp_days -= DAYS_PER_LYEAR_PERIOD * (tmp_days / DAYS_PER_LYEAR_PERIOD);
			}
	
			while (tmp_days >= DAYS_PER_LYEAR) {
				cur_year++;
				if (timelib_is_leap(cur_year)) {
					tmp_days -= DAYS_PER_LYEAR;
				} else {
					tmp_days -= DAYS_PER_YEAR;
				}
			}
		} else {
			tmp_days = days;
	
			/* Guess why this might be for, it has to do with a pope ;-). It's also
			 * only valid for Great Brittain and it's colonies. It needs fixing for
			 * other locales. *sigh*, why is this crap so complex! */
	
			while (tmp_days <= 0) {
				if (tmp_days < -1460970) {
					cur_year -= 4000;
					tmp_days += 1460970;
				} else {
					cur_year--;
					if (timelib_is_leap(cur_year)) {
						tmp_days += DAYS_PER_LYEAR;
					} else {
						tmp_days += DAYS_PER_YEAR;
					}
				}
			}
			remainder += SECS_PER_DAY;
		}
		
		months = timelib_is_leap(cur_year) ? month_tab_leap : month_tab;
		if (timelib_is_leap(cur_year) && cur_year < 1970) {
			tmp_days--;
		}
		int i = 11;
		while (i > 0) {
			if (tmp_days > months[i]) {
				break;
			}
			i--;
		}
		
		/* That was the date, now we do the tiiiime */
		hours = remainder / 3600;
		minutes = (remainder - hours * 3600) / 60;
		seconds = remainder % 60;
		
		tm.y = cur_year;
		tm.m = i + 1;
		tm.d = tmp_days - months[i];
		tm.h = hours;
		tm.i = minutes;
		tm.s = seconds;
		tm.z = 0;
		tm.dst = 0;
		tm.sse = ts;
		tm.sse_uptodate = 1;
		tm.tim_uptodate = 1;
		tm.is_localtime = 0;
	}
	
	protected static void timelib_unixtime2local(timelib_time tm, long ts) {
		timelib_time_offset gmt_offset;
		timelib_tzinfo  tz = tm.tz_info;

		switch (tm.zone_type) {
			case TIMELIB_ZONETYPE_ABBR:
			case TIMELIB_ZONETYPE_OFFSET: {
				long z = tm.z;
				long dst = tm.dst;
				
				timelib_unixtime2gmt(tm, ts - (tm.z * 60) + (tm.dst * 3600));

				tm.z = z;
				tm.dst = dst;
				break;
			}

			case TIMELIB_ZONETYPE_ID:
				gmt_offset = timelib_get_time_zone_info(ts, tz);
				timelib_unixtime2gmt(tm, ts + gmt_offset.offset);

				/* we need to reset the sse here as unixtime2gmt modifies it */
				tm.sse = ts; 
				tm.dst = gmt_offset.is_dst;
				tm.z = gmt_offset.offset;
				tm.tz_info = tz;

				tref<String> tz_abbr = new tref<String>(gmt_offset.abbr);
				timelib_time_tz_abbr_update(tm, tz_abbr);
				gmt_offset.abbr = tz_abbr.v;
				gmt_offset = null;
				break;

			default:
				tm.is_localtime = 0;
				tm.have_zone = 0;
				return;
		}

		tm.is_localtime = 1;
		tm.have_zone = 1;
	}
	
	public static void timelib_set_timezone(timelib_time t, timelib_tzinfo tz) {
		timelib_time_offset gmt_offset;

		gmt_offset = timelib_get_time_zone_info(t.sse, tz);
		t.z = gmt_offset.offset;

		t.dst = gmt_offset.is_dst;
		t.tz_info = tz;
		if (t.tz_abbr!=null) {
			t.tz_abbr = null;
		}
		t.tz_abbr = gmt_offset.abbr;
		gmt_offset = null;

		t.have_zone = 1;
		t.zone_type = TIMELIB_ZONETYPE_ID;
	}
}