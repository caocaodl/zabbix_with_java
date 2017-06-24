package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Structs.timelib_is_leap;

import com.isoft.zend.ext.date.Structs.rlong;

public class Dow {
	
	private Dow() {
	}
	
	private static int[] m_table_common = { -1, 0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5 }; /* 1 = jan */
	private static int[] m_table_leap =   { -1, 6, 2, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5 }; /* 1 = jan */
	
	private static long century_value(long j) {
		return 6 - (j % 4) * 2;
	}
	
	private static long timelib_day_of_week_ex(long y, long m, long d, boolean iso) {
		long c1, y1, m1, dow;

		/* Only valid for Gregorian calendar, commented out as we don't handle
		 * Julian calendar. We just return the 'wrong' day of week to be
		 * consistent. */
		c1 = century_value(y / 100);
		y1 = (y % 100);
		m1 = timelib_is_leap(y) ? m_table_leap[(int)m] : m_table_common[(int)m];
		dow = (c1 + y1 + m1 + (y1 / 4) + d) % 7;
		if (iso) {
			if (dow == 0) {
				dow = 7;
			}
		}
		return dow;
	}
	
	protected static long timelib_day_of_week(long y, long m, long d) {
		return timelib_day_of_week_ex(y, m, d, false);
	}

	protected static long timelib_iso_day_of_week(long y, long m, long d) {
		return timelib_day_of_week_ex(y, m, d, true);
	}
	
                                                    /*     jan  feb  mar  apr  may  jun  jul  aug  sep  oct  nov  dec */
	private static int[] d_table_common  = {  0,   0,  31,  59,  90, 120, 151, 181, 212, 243, 273, 304, 334 };
	private static int[] d_table_leap          = {  0,   0,  31,  60,  91, 121, 152, 182, 213, 244, 274, 305, 335 };
	private static int[] ml_table_common= {  0,  31,  28,  31,  30,  31,  30,  31,  31,  30,  31,  30,  31 };
	private static int[] ml_table_leap        = {  0,  31,  29,  31,  30,  31,  30,  31,  31,  30,  31,  30,  31 };
	
	protected static long timelib_day_of_year(long y, long m, long d) {
		return (timelib_is_leap(y) ? d_table_leap[(int)m] : d_table_common[(int)m]) + d - 1;
	}
	
	protected static long timelib_days_in_month(long y, long m) {
		return timelib_is_leap(y) ? ml_table_leap[(int)m] : ml_table_common[(int)m];
	}
	
	protected static void timelib_isoweek_from_date(long y, long m, long d, rlong iw, rlong iy) {
		boolean y_leap = timelib_is_leap(y);
		boolean prev_y_leap = timelib_is_leap(y-1);
		int doy = (int)timelib_day_of_year(y, m, d) + 1;
		if (y_leap && m > 2) {
			doy++;
		}
		int jan1weekday = (int)timelib_day_of_week(y, 1, 1);
		int weekday = (int)timelib_day_of_week(y, m, d);
		if (weekday == 0) weekday = 7;
		if (jan1weekday == 0) jan1weekday = 7;
		/* Find if Y M D falls in YearNumber Y-1, WeekNumber 52 or 53 */
		if (doy <= (8 - jan1weekday) && jan1weekday > 4) {
			iy.v = y - 1;
			if (jan1weekday == 5 || (jan1weekday == 6 && prev_y_leap)) {
				iw.v = 53;
			} else {
				iw.v = 52;
			}
		} else {
			iy.v = y;
		}
		/* 8. Find if Y M D falls in YearNumber Y+1, WeekNumber 1 */
		if (iy.v == y) {
			int i;

			i = y_leap ? 366 : 365;
			if ((i - (doy - (y_leap?1:0))) < (4 - weekday)) {
				iy.v = y + 1;
				iw.v = 1;
				return;
			}
		}
		/* 9. Find if Y M D falls in YearNumber Y, WeekNumber 1 through 53 */
		if (iy.v == y) {
			int j;

			j = doy + (7 - weekday) + (jan1weekday - 1);
			iw.v = j / 7;
			if (jan1weekday > 4) {
				iw.v -= 1;
			}
		}
	}
	
	protected static long timelib_daynr_from_weeknr(long y, long w, long d) {
		long dow, day;			
		/* Figure out the dayofweek for y-1-1 */
		dow = timelib_day_of_week(y, 1, 1);
		/* then use that to figure out the offset for day 1 of week 1 */
		day = 0 - (dow > 4 ? dow - 7 : dow);
		/* Add weeks and days */
		return day + ((w - 1) * 7) + d;
	}

	protected static boolean timelib_valid_time(long h, long i, long s) {
		if (h < 0 || h > 23 || i < 0 || i > 59 || s < 0 || s > 59) {
			return false;
		}
		return true;
	}

	protected static boolean timelib_valid_date(long y, long m, long d) {
		if (m < 1 || m > 12 || d < 1 || d > timelib_days_in_month(y, m)) {
			return false;
		}
		return true;
	}
}

