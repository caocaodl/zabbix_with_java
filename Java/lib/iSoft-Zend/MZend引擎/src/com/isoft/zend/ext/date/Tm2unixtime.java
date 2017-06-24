package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Dow.timelib_day_of_week;
import static com.isoft.zend.ext.date.Parsetz.timelib_get_time_zone_info;
import static com.isoft.zend.ext.date.Structs.DAYS_PER_LYEAR;
import static com.isoft.zend.ext.date.Structs.DAYS_PER_LYEAR_PERIOD;
import static com.isoft.zend.ext.date.Structs.DAYS_PER_YEAR;
import static com.isoft.zend.ext.date.Structs.SECS_PER_DAY;
import static com.isoft.zend.ext.date.Structs.SECS_PER_ERA;
import static com.isoft.zend.ext.date.Structs.TIMELIB_SPECIAL_DAY_OF_WEEK_IN_MONTH;
import static com.isoft.zend.ext.date.Structs.TIMELIB_SPECIAL_LAST_DAY_OF_WEEK_IN_MONTH;
import static com.isoft.zend.ext.date.Structs.TIMELIB_SPECIAL_WEEKDAY;
import static com.isoft.zend.ext.date.Structs.TIMELIB_UNSET;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ABBR;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ID;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_OFFSET;
import static com.isoft.zend.ext.date.Structs.YEARS_PER_LYEAR_PERIOD;
import static com.isoft.zend.ext.date.Structs.timelib_is_leap;
import static com.isoft.zend.ext.date.Unixtime2tm.timelib_set_timezone;
import static java.lang.Math.abs;

import com.isoft.zend.ext.date.Structs.rlong;
import com.isoft.zend.ext.date.Structs.timelib_rel_time;
import com.isoft.zend.ext.date.Structs.timelib_special;
import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.timelib_time_offset;
import com.isoft.zend.ext.date.Structs.timelib_tzinfo;

public class Tm2unixtime {
	
	private Tm2unixtime() {
	}
	
	/*                                                                   jan  feb  mrt  apr  may  jun  jul  aug  sep  oct  nov  dec */
	private final static int[] month_tab_leap  = {  -1,  30,  59,  90, 120, 151, 181, 212, 243, 273, 304, 334 };
	private final static int[] month_tab          = {   0,  31,  59,  90, 120, 151, 181, 212, 243, 273, 304, 334 };
	
	/*                                                                         dec  jan  feb  mrt  apr  may  jun  jul  aug  sep  oct  nov  dec */
	private final static int[] days_in_month_leap = {  31,  31,  29,  31,  30,  31,  30,  31,  31,  30,  31,  30,  31 };
	private final static int[] days_in_month         = {  31,  31,  28,  31,  30,  31,  30,  31,  31,  30,  31,  30,  31 };
	
	protected static void do_range_limit(long start, long end, long adj, rlong a, rlong b) {
		if (a.v < start) {
			b.v -= (start - a.v - 1) / adj + 1;
			a.v += adj * ((start - a.v - 1) / adj + 1);
		}
		if (a.v >= end) {
			b.v += a.v / adj;
			a.v -= adj * (a.v / adj);
		}
	}
	
	protected static void inc_month(rlong y, rlong m) {
		(m.v)++;
		if (m.v > 12) {
			m.v -= 12;
			(y.v)++;
		}
	}
	
	protected static void dec_month(rlong y, rlong m) {
		(m.v)--;
		if (m.v < 1) {
			m.v += 12;
			(y.v)--;
		}
	}
	
	protected static void do_range_limit_days_relative(rlong base_y, rlong base_m, rlong y, rlong m, rlong d, long invert) {
		boolean leapyear;
		rlong month=new rlong(0), year = new rlong(0);
		long days;

		do_range_limit(1, 13, 12, base_m, base_y);

		year.v = base_y.v;
		month.v = base_m.v;

		if (invert==0) {
			while (d.v < 0) {
				dec_month(year, month);
				leapyear = timelib_is_leap(year.v);
				days = leapyear ? days_in_month_leap[(int)month.v] : days_in_month[(int)month.v];
				d.v += days;
				(m.v)--;
			}
		} else {
			while (d.v < 0) {
				leapyear = timelib_is_leap(year.v);
				days = leapyear ? days_in_month_leap[(int)month.v] : days_in_month[(int)month.v];
				d.v += days;
				(m.v)--;
				inc_month(year, month);
			}
		}
	}
	
	protected static int do_range_limit_days(rlong y, rlong m, rlong d){
		boolean leapyear;
		long days_this_month;
		long last_month, last_year;
		long days_last_month;
		
		/* can jump an entire leap year period quickly */
		if (d.v >= DAYS_PER_LYEAR_PERIOD || d.v <= -DAYS_PER_LYEAR_PERIOD) {
			y.v += YEARS_PER_LYEAR_PERIOD * (d.v / DAYS_PER_LYEAR_PERIOD);
			d.v -= DAYS_PER_LYEAR_PERIOD * (d.v / DAYS_PER_LYEAR_PERIOD);
		}

		do_range_limit(1, 13, 12, m, y);

		leapyear = timelib_is_leap(y.v);
		days_this_month = leapyear ? days_in_month_leap[(int)m.v] : days_in_month[(int)m.v];
		last_month = (m.v) - 1;

		if (last_month < 1) {
			last_month += 12;
			last_year = (y.v) - 1;
		} else {
			last_year = (y.v);
		}
		leapyear = timelib_is_leap(last_year);
		days_last_month = leapyear ? days_in_month_leap[(int)last_month] : days_in_month[(int)last_month];

		if (d.v <= 0) {
			d.v += days_last_month;
			(m.v)--;
			return 1;
		}
		if (d.v > days_this_month) {
			d.v -= days_this_month;
			(m.v)++;
			return 1;
		}
		return 0;
	}
	
	protected static void do_adjust_for_weekday(timelib_time time) {
		long current_dow = timelib_day_of_week(time.y, time.m, time.d);
		if (time.relative.weekday_behavior == 2){
			if (time.relative.weekday == 0) {
				time.relative.weekday = 7;
			}
			time.d -= current_dow;
			time.d += time.relative.weekday;
			return;
		}
		long difference = time.relative.weekday - current_dow;
		if ((time.relative.d < 0 && difference < 0) || (time.relative.d >= 0 && difference <= -time.relative.weekday_behavior)) {
			difference += 7;
		}
		if (time.relative.weekday >= 0) {
			time.d += difference;
		} else {
			time.d -= (7 - (abs(time.relative.weekday) - current_dow));
		}
		time.relative.have_weekday_relative = 0;
	}
	
	protected static void timelib_do_rel_normalize(timelib_time base, timelib_rel_time rt) {
		rlong rts = new rlong(rt.s);
		rlong rti = new rlong(rt.i);
		rlong rth = new rlong(rt.h);
		rlong rtd = new rlong(rt.d);
		rlong rtm = new rlong(rt.m);
		rlong rty = new rlong(rt.y);
		rlong basey = new rlong(base.y);
		rlong basem = new rlong(base.m);
		
		do_range_limit(0, 60, 60, rts, rti);
		do_range_limit(0, 60, 60, rti, rth);
		do_range_limit(0, 24, 24, rth, rtd);
		do_range_limit(0, 12, 12, rtm, rty);
		
		do_range_limit_days_relative(basey, basem, rty, rtm, rtd, rt.invert);
		do_range_limit(0, 12, 12, rtm, rty);
		
		rt.s = rts.v;
		rt.i = rti.v;
		rt.h = rth.v;
		rt.d = rtd.v;
		rt.m = rtm.v;
		rt.y = rty.v;
		base.y = basey.v;
		base.m = basem.v;
	}
	
	public static void timelib_do_normalize(timelib_time time) {
		rlong times = new rlong(time.s);
		rlong timei = new rlong(time.i);
		rlong timeh = new rlong(time.h);
		rlong timed = new rlong(time.d);
		rlong timem = new rlong(time.m);
		rlong timey = new rlong(time.y);

		if (time.s != TIMELIB_UNSET) do_range_limit(0, 60, 60, times, timei);
		if (time.s != TIMELIB_UNSET) do_range_limit(0, 60, 60, timei, timeh);
		if (time.s != TIMELIB_UNSET) do_range_limit(0, 24, 24, timeh, timed);
		do_range_limit(1, 13, 12, timem, timey);

		do {} while (do_range_limit_days(timey, timem, timed)>0);
		do_range_limit(1, 13, 12, timem, timey);
		
		time.s = times.v;
		time.i = timei.v;
		time.h = timeh.v;
		time.d = timed.v;
		time.m = timem.v;
		time.y = timey.v;
	}
	
	protected static void do_adjust_relative(timelib_time time) {
		if (time.relative.have_weekday_relative>0) {
			do_adjust_for_weekday(time);
		}
		timelib_do_normalize(time);

		if (time.have_relative>0) {
			time.s += time.relative.s;
			time.i += time.relative.i;
			time.h += time.relative.h;

			time.d += time.relative.d;
			time.m += time.relative.m;
			time.y += time.relative.y;
		}
		switch (time.relative.first_last_day_of) {
			case 1: /* first */
				time.d = 1;
				break;
			case 2: /* last */
				time.d = 0;
				time.m++;
				break;
		}
		timelib_do_normalize(time);
	}
	
	protected static void do_adjust_special_weekday(timelib_time time) {
		long count = time.relative.special.amount;
		long dow = timelib_day_of_week(time.y, time.m, time.d);

		/* Add increments of 5 weekdays as a week, leaving the DOW unchanged. */
		time.d += (count / 5) * 7;

		/* Deal with the remainder. */
		long rem = (count % 5);

		if (count > 0) {
			if (rem == 0) {
				/* Head back to Friday if we stop on the weekend. */
				if (dow == 0) {
					time.d -= 2;
				} else if (dow == 6) {
					time.d -= 1;
				}
			} else if (dow == 6) {
				/* We ended up on Saturday, but there's still work to do, so move
				 * to Sunday and continue from there. */
				time.d += 1;
			} else if (dow + rem > 5) {
				/* We're on a weekday, but we're going past Friday, so skip right
				 * over the weekend. */
				time.d += 2;
			}
		} else {
			/* Completely mirror the forward direction. This also covers the 0
			 * case, since if we start on the weekend, we want to move forward as
			 * if we stopped there while going backwards. */
			if (rem == 0) {
				if (dow == 6) {
					time.d += 2;
				} else if (dow == 0) {
					time.d += 1;
				}
			} else if (dow == 0) {
				time.d -= 1;
			} else if (dow + rem < 1) {
				time.d -= 2;
			}
		}

		time.d += rem;
	}
	
	protected static void do_adjust_special(timelib_time time) {
		if (time.relative.have_special_relative>0) {
			switch (time.relative.special.type) {
				case TIMELIB_SPECIAL_WEEKDAY:
					do_adjust_special_weekday(time);
					break;
			}
		}
		timelib_do_normalize(time);
		time.relative.special = new timelib_special();
	}
	
	protected static void do_adjust_special_early(timelib_time time) {
		if (time.relative.have_special_relative>0) {
			switch (time.relative.special.type) {
				case TIMELIB_SPECIAL_DAY_OF_WEEK_IN_MONTH:
					time.d = 1;
					time.m += time.relative.m;
					time.relative.m = 0;
					break;
				case TIMELIB_SPECIAL_LAST_DAY_OF_WEEK_IN_MONTH:
					time.d = 1;
					time.m += time.relative.m + 1;
					time.relative.m = 0;
					break;
			}
		}
		timelib_do_normalize(time);
	}
	
	protected static long do_years(long year) {
		long res = 0;

		long eras = (year - 1970) / 40000;
		if (eras != 0) {
			year = year - (eras * 40000);
			res += (SECS_PER_ERA * eras * 100);
		}

		if (year >= 1970) {
			for (long i = year - 1; i >= 1970; i--) {
				if (timelib_is_leap(i)) {
					res += (DAYS_PER_LYEAR * SECS_PER_DAY);
				} else {
					res += (DAYS_PER_YEAR * SECS_PER_DAY);
				}
			}
		} else {
			for (long i = 1969; i >= year; i--) {
				if (timelib_is_leap(i)) {
					res -= (DAYS_PER_LYEAR * SECS_PER_DAY);
				} else {
					res -= (DAYS_PER_YEAR * SECS_PER_DAY);
				}
			}
		}
		return res;
	}
	
	protected static long do_months(long month, long year) {
		if (timelib_is_leap(year)) {
			return ((month_tab_leap[(int)month - 1] + 1) * SECS_PER_DAY);
		} else {
			return ((month_tab[(int)month - 1]) * SECS_PER_DAY);
		}
	}
	
	protected static long do_days(long day) {
		return ((day - 1) * SECS_PER_DAY);
	}
	
	protected static long do_time(long hour, long minute, long second) {
		long res = 0;

		res += hour * 3600;
		res += minute * 60;
		res += second;
		return res;
	}
	
	protected static long do_adjust_timezone(timelib_time tz, timelib_tzinfo tzi) {
		switch (tz.zone_type) {
			case TIMELIB_ZONETYPE_OFFSET:

				tz.is_localtime = 1;
				return tz.z * 60;

			case TIMELIB_ZONETYPE_ABBR:
				long tmp;

				tz.is_localtime = 1;
				tmp = tz.z;
				tmp -= tz.dst * 60;
				tmp *= 60;
				return tmp;

			case TIMELIB_ZONETYPE_ID:
				tzi = tz.tz_info;
				/* Break intentionally missing */

			default:
				/* No timezone in struct, fallback to reference if possible */
				if (tzi!=null) {
					timelib_time_offset before, after;
					boolean in_transistion;
					
					tz.is_localtime = 1;
					before = timelib_get_time_zone_info(tz.sse, tzi);
					after = timelib_get_time_zone_info(tz.sse - before.offset, tzi);
					timelib_set_timezone(tz, tzi);

					in_transistion = (
						((tz.sse - after.offset) >= (after.transistion_time + (before.offset - after.offset))) &&
						((tz.sse - after.offset) < after.transistion_time)
					);
					
					if ((before.offset != after.offset) && !in_transistion) {
						tmp = -after.offset;
					} else {
						tmp = -tz.z;
					}
					before = null;
					after = null;

					timelib_time_offset gmt_offset = timelib_get_time_zone_info(tz.sse + tmp, tzi);
					tz.z = gmt_offset.offset;

					tz.dst = gmt_offset.is_dst;
					if (tz.tz_abbr!=null) {
						tz.tz_abbr = null;
					}
					tz.tz_abbr = gmt_offset.abbr;
					gmt_offset = null;
					return tmp;
				}
		}
		return 0;
	}
	
	protected static void timelib_update_ts(timelib_time time, timelib_tzinfo tzi) {
		long res = 0;

		do_adjust_special_early(time);
		do_adjust_relative(time);
		do_adjust_special(time);
		res += do_years(time.y);
		res += do_months(time.m, time.y);
		res += do_days(time.d);
		res += do_time(time.h, time.i, time.s);
		time.sse = res;

		res += do_adjust_timezone(time, tzi);
		time.sse = res;

		time.sse_uptodate = 1;
		time.have_relative = time.relative.have_weekday_relative = time.relative.have_special_relative = 0;
	}
}