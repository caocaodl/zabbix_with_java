package com.isoft.zend.ext.date;

import java.util.List;

public class Structs {
	
	private Structs() {
	}

	protected static class tref<T> {
		protected tref(T v) {
			this.v = v;
		}
		protected T v;
	}

	protected static class rlong {

		protected rlong(long v) {
			this.v = v;
		}

		protected long v;
	}
	
	protected static class ttinfo {
		public int offset;
		public int isdst;
		public long abbr_idx;

		public long isstdcnt;
		public long isgmtcnt;
	}
	
	protected static class tlinfo {
		public int trans;
		public int offset;
	}
	
	protected static class tlocinfo {
		public String country_code;
		public double latitude;
		public double longitude;
		public String comments;
	}
	
	protected static class timelib_tzinfo {
		protected timelib_tzinfo(){
			this.location = new tlocinfo();
		}
		public String name;
		public long ttisgmtcnt;
		public long ttisstdcnt;
		public long leapcnt;
		public long timecnt;
		public long typecnt;
		public long charcnt;

		public int[] trans;
		public char[] trans_idx;

		public ttinfo[] type;
		public String timezone_abbr;

		public tlinfo[] leap_times;
		public char bc;
		public tlocinfo location;
	}
	
	protected static class timelib_special {
		public int type;
		public long amount;
	}
	
	protected static class timelib_rel_time {
		protected timelib_rel_time(){
			this.special = new timelib_special();
		}
		public long y, m, d; /* Years, Months and Days */
		public long h, i, s; /* Hours, mInutes and Seconds */

		public int weekday; /* Stores the day in 'next monday' */
		public int weekday_behavior; /* 0: the current day should *not* be counted when advancing forwards; 1: the current day *should* be counted */

		public int first_last_day_of;
		public int invert; /* Whether the difference should be inverted */
		public long days; /* Contains the number of *days*, instead of Y-M-D differences */

		public timelib_special  special;
		public int have_weekday_relative, have_special_relative;
	}

	protected static class timelib_time_offset {
		public long offset;
		public long leap_secs;
		public long is_dst;
		public String abbr;
		public long transistion_time;
	}

	protected static class timelib_time {			
		protected timelib_time(){
			this.relative = new timelib_rel_time();
		}			
		public long y, m, d;     	/* Year, Month, Day */
		public long h, i, s;       	/* Hour, mInute, Second */
		public double f;           	/* Fraction */
		public long z;           			/* GMT offset in minutes */
		public String tz_abbr;     /* Timezone abbreviation (display only) */
		public timelib_tzinfo  tz_info;     /* Timezone structure */
		public long dst;         			/* Flag if we were parsing a DST zone */
		public timelib_rel_time relative;
	
		public long sse;         		/* Seconds since epoch */
	
		public int have_time, have_date, have_zone, have_relative, have_weeknr_day;
	
		public long sse_uptodate; 	/* !0 if the sse member is up to date with the date/time members */
		public long tim_uptodate; 	/* !0 if the date/time members are up to date with the sse member */
		public long is_localtime; 		/*  1 if the current struct represents localtime, 0 if it is in GMT */
		public int zone_type;    	/*  1 time offset,
		                              	 			 *  3 TimeZone identifier,
		                              	 			 *  2 TimeZone abbreviation */
	}
	
	protected static class timelib_abbr_info {
		public long utc_offset;
		public String abbr;
		public int dst;
	}
	
	protected static class timelib_tz_lookup_table {			
		protected timelib_tz_lookup_table(String name, int type, float gmtoffset, String full_tz_name) {
			this.name = name;
			this.type = type;
			this.gmtoffset = gmtoffset;
			this.full_tz_name = full_tz_name;
		}

		public String name;
		public int type;
		public float gmtoffset;
		public String full_tz_name;
	}
	
	protected static class timelib_tzdb_index_entry {

		public timelib_tzdb_index_entry(String id, int pos) {
			this.id = id;
			this.pos = pos;
		}

		public String id;
		public int pos;
	}
	
	protected static class timelib_tzdb {

		public timelib_tzdb(String version, int index_size, timelib_tzdb_index_entry[] index, char[] data) {
			this.version = version;
			this.index_size = index_size;
			this.index = index;
			this.data = data;
		}

		public String version;
		public int index_size;
		public timelib_tzdb_index_entry[] index;
		public char[] data;
	}
	
	public final static int TIMELIB_ZONETYPE_OFFSET = 1;
	public final static int TIMELIB_ZONETYPE_ABBR = 2;
	public final static int TIMELIB_ZONETYPE_ID = 3;
	
	public final static long SECS_PER_ERA = 12622780800L;
	public final static int SECS_PER_DAY = 86400;
	public final static int DAYS_PER_YEAR = 365;
	public final static int DAYS_PER_LYEAR = 366;
	/* 400*365 days + 97 leap days */
	public final static int DAYS_PER_LYEAR_PERIOD = 146097;
	public final static int YEARS_PER_LYEAR_PERIOD = 400;
	
	public final static int TIMELIB_NONE = 0x00;
	public final static int TIMELIB_OVERRIDE_TIME = 0x01;
	public final static int TIMELIB_NO_CLONE = 0x02;
	
	public final static long TIMELIB_UNSET = -99999;
	
	public final static int TIMELIB_SPECIAL_WEEKDAY = 0x01;
	public final static int TIMELIB_SPECIAL_DAY_OF_WEEK_IN_MONTH = 0x02;
	public final static int TIMELIB_SPECIAL_LAST_DAY_OF_WEEK_IN_MONTH = 0x03;
	
	public static boolean timelib_is_leap(long y) {
		return (y % 4 == 0 && (y % 100 != 0 || y % 400 == 0));
	}
	
	protected static class timelib_error_message {
		public int position;
		public char character;
		public String message;
	}
	
	protected static class timelib_error_container {
		public int warning_count;
		public List<timelib_error_message> warning_messages;
		public int error_count;
		public List<timelib_error_message> error_messages;
	}
	
	protected static class Scanner {
		public int fd;
		public char[] str;
		public int lim, ptr, cur, tok, pos;
		public int line, len;
		public timelib_error_container errors;

		public timelib_time time;
		public timelib_tzdb tzdb;
	}
	
	protected static class timelib_lookup_table {
		protected timelib_lookup_table(String name, int type, int value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}
		public String name;
		public int type;
		public int value;
	}
	
	protected static class timelib_relunit {
		protected timelib_relunit(String name, int unit, int multiplier) {
			this.name = name;
			this.unit = unit;
			this.multiplier = multiplier;
		}

		public String name;
		public int unit;
		public int multiplier;
	}
	
}
