package com.isoft.zend.ext.date;

import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ABBR;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_ID;
import static com.isoft.zend.ext.date.Structs.TIMELIB_ZONETYPE_OFFSET;
import static com.isoft.zend.ext.date.Timezonedb.timezonedb_builtin;
import static com.isoft.zend.ext.date.Timezonedb.timezonedb_idx_builtin;

import com.isoft.zend.ext.date.Structs.rlong;
import com.isoft.zend.ext.date.Structs.timelib_time;
import com.isoft.zend.ext.date.Structs.timelib_time_offset;
import com.isoft.zend.ext.date.Structs.timelib_tzdb;
import com.isoft.zend.ext.date.Structs.timelib_tzdb_index_entry;
import com.isoft.zend.ext.date.Structs.timelib_tzinfo;
import com.isoft.zend.ext.date.Structs.tlinfo;
import com.isoft.zend.ext.date.Structs.tref;
import com.isoft.zend.ext.date.Structs.ttinfo;

public 	class Parsetz {
	
	private Parsetz() {
	}
	
	protected static void read_preamble(char[] ch, tref<Integer> tzf, timelib_tzinfo tz){
		/* skip ID */
		tzf.v += 4;
		
		/* read BC flag */
		tz.bc = (char)(ch[tzf.v] == '\1'?1:0);
		tzf.v += 1;

		/* read country code */
		tz.location.country_code = String.valueOf(ch, tzf.v, 2);
		tzf.v += 2;

		/* skip read of preamble */
		tzf.v += 13;
	}
	
	protected static void read_header(char[] ch, tref<Integer> tzf, timelib_tzinfo tz) {
		char[] buffer=new char[24];
		System.arraycopy(ch, tzf.v, buffer, 0, 24);
		CharStream dis = new CharStream(buffer);
		tz.ttisgmtcnt = dis.readInt();
		tz.ttisstdcnt  = dis.readInt();
		tz.leapcnt     = dis.readInt();
		tz.timecnt     = dis.readInt();
		tz.typecnt     = dis.readInt();
		tz.charcnt     = dis.readInt();
		tzf.v += 24;
	}
	
	protected static void read_transistions(char[] ch, tref<Integer> tzf, timelib_tzinfo tz) {
		char[] chars = null;
		int[] buffer = null;
		int i;
		char[] cbuffer = null;

		if (tz.timecnt>0) {
			chars = new char[(int)tz.timecnt*4];
			buffer = new int[(int)tz.timecnt];

			System.arraycopy(ch, tzf.v, chars, 0, chars.length);
			tzf.v += chars.length;
			CharStream dis = new CharStream(chars);
			for (i = 0; i < tz.timecnt; i++) {
				buffer[i] = dis.readInt();
			}
			
			cbuffer = new char[(int)tz.timecnt];
			System.arraycopy(ch, tzf.v, cbuffer, 0, cbuffer.length);
			tzf.v += (int)tz.timecnt;
		}
		
		tz.trans = buffer;
		tz.trans_idx = cbuffer;
	}
	
	protected static void read_types(char[] ch, tref<Integer> tzf, timelib_tzinfo tz){
		int j;
		char[] buffer = new char[(int)tz.typecnt*6];
		System.arraycopy(ch, tzf.v, buffer, 0, buffer.length);
		tzf.v += buffer.length;

		tz.type = new ttinfo[(int)tz.typecnt];
		for (int i = 0; i < tz.typecnt; i++) {
			j = i * 6;
			tz.type[i] = new ttinfo();
			tz.type[i].offset = ((buffer[j]&0xff) * 16777216) + ((buffer[j + 1]&0xff) * 65536) + ((buffer[j + 2]&0xff) * 256) + (buffer[j + 3]&0xff);
			tz.type[i].isdst = buffer[j + 4]&0xff;
			tz.type[i].abbr_idx = buffer[j + 5]&0xff;
		}
		buffer = null;

		tz.timezone_abbr = String.valueOf(ch, tzf.v, (int)tz.charcnt);
		tzf.v += (int)tz.charcnt;

		if (tz.leapcnt>0) {
			char[] leap_buffer = new char[(int)tz.leapcnt * 2 * 4];
			System.arraycopy(ch, tzf.v, leap_buffer, 0, leap_buffer.length);
			tzf.v += leap_buffer.length;

			tz.leap_times = new tlinfo[(int)tz.leapcnt];
			CharStream dis = new CharStream(leap_buffer);
			for (int i = 0; i < tz.leapcnt; i++) {
				tz.leap_times[i] = new tlinfo();
				tz.leap_times[i].trans = dis.readInt();
				tz.leap_times[i].offset = dis.readInt();
			}
			leap_buffer = null;
		}

		if (tz.ttisstdcnt>0) {
			buffer = new char[(int)tz.ttisstdcnt];
			System.arraycopy(ch, tzf.v, buffer, 0, buffer.length);
			tzf.v += buffer.length;

			for (int i = 0; i < tz.ttisstdcnt; i++) {
				tz.type[i].isstdcnt = buffer[i]&0xff;
			}
			buffer = null;
		}

		if (tz.ttisgmtcnt>0) {
			buffer = new char[(int)tz.ttisgmtcnt];
			System.arraycopy(ch, tzf.v, buffer, 0, buffer.length);
			tzf.v += buffer.length;

			for (int i = 0; i < tz.ttisgmtcnt; i++) {
				tz.type[i].isgmtcnt = buffer[i]&0xff;
			}
			buffer = null;
		}
	}
	
	protected static void read_location(char[] ch, tref<Integer> tzf, timelib_tzinfo tz){
		char[] buffer = new char[12];
		int comments_len;
		System.arraycopy(ch, tzf.v, buffer, 0, 12);
		
		CharStream dis = new CharStream(buffer);
		tz.location.latitude = dis.readInt();
		tz.location.latitude = (tz.location.latitude / 100000) - 90;
		tz.location.longitude = dis.readInt();
		tz.location.longitude = (tz.location.longitude / 100000) - 180;
		comments_len = dis.readInt();
		tzf.v += buffer.length;
		tz.location.comments = String.valueOf(ch, tzf.v, comments_len);
		tzf.v += comments_len;
		buffer = null;
	}
	
	protected static void timelib_dump_tzinfo(timelib_tzinfo tz) {
		System.out.print(String.format("Country Code:      %s\n", tz.location.country_code));
		System.out.print(String.format("Geo Location:      %f,%f\n", tz.location.latitude, tz.location.longitude));
		System.out.print(String.format("Comments:\n%s\n",          tz.location.comments));
		System.out.print(String.format("BC:                %s\n",  tz.bc>0 ? "" : "yes"));
		System.out.print(String.format("UTC/Local count:   %d\n", tz.ttisgmtcnt));
		System.out.print(String.format("Std/Wall count:    %d\n", tz.ttisstdcnt));
		System.out.print(String.format("Leap.sec. count:   %d\n", tz.leapcnt));
		System.out.print(String.format("Trans. count:      %d\n", tz.timecnt));
		System.out.print(String.format("Local types count: %d\n", tz.typecnt));
		System.out.print(String.format("Zone Abbr. count:  %d\n", tz.charcnt));

		System.out.print(String.format("%8s (%12s) = %3d [%5d %1d %3d '%s' (%d,%d)]\n",
			"", "", 0,
			tz.type[0].offset,
			tz.type[0].isdst,
			tz.type[0].abbr_idx,
			String.valueOf(tz.timezone_abbr.charAt((int)tz.type[0].abbr_idx)),
			tz.type[0].isstdcnt,
			tz.type[0].isgmtcnt
			));
		for (int i = 0; i < tz.timecnt; i++) {
			System.out.print(String.format("%08d (%12d) = %3d [%5ld %1d %3d '%s' (%d,%d)]\n",
				tz.trans[i], tz.trans[i], tz.trans_idx[i],
				tz.type[tz.trans_idx[i]].offset,
				tz.type[tz.trans_idx[i]].isdst,
				tz.type[tz.trans_idx[i]].abbr_idx,
				String.valueOf(tz.timezone_abbr.charAt((int)tz.type[tz.trans_idx[i]].abbr_idx)),
				tz.type[tz.trans_idx[i]].isstdcnt,
				tz.type[tz.trans_idx[i]].isgmtcnt
				));
		}
		for (int i = 0; i < tz.leapcnt; i++) {
			System.out.print(String.format("%08d (%12d) = %d\n",
				tz.leap_times[i].trans,
				tz.leap_times[i].trans,
				tz.leap_times[i].offset));
		}
	}
	
	protected static boolean seek_to_tz_position(tref<Integer> tzf, String timezone, timelib_tzdb tzdb) {
		int left = 0, right = tzdb.index_size - 1;
		do {
			int mid = (left + right) >> 1;
			int cmp = timezone.compareToIgnoreCase(tzdb.index[mid].id);
			if (cmp < 0) {
				right = mid - 1;
			} else if (cmp > 0) {
				left = mid + 1;
			} else {
				tzf.v = tzdb.index[mid].pos;
				return true;
			}

		} while (left <= right);
		return false;
	}
	
	protected static timelib_tzdb timelib_builtin_db() {
		return timezonedb_builtin;
	}
	
	protected static timelib_tzdb_index_entry[] timelib_timezone_builtin_identifiers_list(tref<Integer> count) {
		count.v = timezonedb_idx_builtin.length;
		return timezonedb_idx_builtin;
	}
	
	protected static boolean timelib_timezone_id_is_valid(String timezone, timelib_tzdb tzdb) {
		tref<Integer> tzf = new tref<Integer>(null);
		return (seek_to_tz_position(tzf, timezone, tzdb));
	}
	
	protected static timelib_tzinfo timelib_parse_tzfile(String timezone, timelib_tzdb tzdb){
		tref<Integer> tzf = new tref<Integer>(0);
		timelib_tzinfo tmp = null;
		if (seek_to_tz_position(tzf, timezone, tzdb)) {
			tmp = new timelib_tzinfo();
			tmp.name = timezone;
			read_preamble(tzdb.data, tzf, tmp);
			read_header(tzdb.data, tzf, tmp);
			read_transistions(tzdb.data, tzf, tmp);
			read_types(tzdb.data, tzf, tmp);
			read_location(tzdb.data, tzf, tmp);
		} else {
			tmp = null;
		}
		return tmp;
	}
	
	protected static ttinfo fetch_timezone_offset(timelib_tzinfo tz, long ts, rlong transition_time) {
		/* If there is no transistion time, we pick the first one, if that doesn't
		 * exist we return NULL */
		if (tz.timecnt==0 || tz.trans==null) {
			transition_time.v = 0;
			if (tz.typecnt == 1) {
				return tz.type[0];
			}
			return null;
		}

		/* If the TS is lower than the first transistion time, then we scan over
		 * all the transistion times to find the first non-DST one, or the first
		 * one in case there are only DST entries. Not sure which smartass came up
		 * with this idea in the first though :) */
		if (ts < tz.trans[0]) {
			transition_time.v = 0;
			int j = 0;
			while (j < tz.timecnt && tz.type[j].isdst>0) {
				++j;
			}
			if (j == tz.timecnt) {
				j = 0;
			}
			return tz.type[j];
		}

		/* In all other cases we loop through the available transtion times to find
		 * the correct entry */
		for (int i = 0; i < tz.timecnt; i++) {
			if (ts < tz.trans[i]) {
				transition_time.v = tz.trans[i - 1];
				return tz.type[tz.trans_idx[i - 1]];
			}
		}
		transition_time.v = tz.trans[(int)(tz.timecnt - 1)];
		return tz.type[tz.trans_idx[(int)(tz.timecnt - 1)]];
	}
	
	protected static tlinfo fetch_leaptime_offset(timelib_tzinfo tz, long ts) {
		if (tz.leapcnt==0 || tz.leap_times==null) {
			return null;
		}

		for (int i = (int)(tz.leapcnt - 1); i > 0; i--) {
			if (ts > tz.leap_times[i].trans) {
				return tz.leap_times[i];
			}
		}
		return null;
	}
	
	protected static int timelib_timestamp_is_in_dst(long ts, timelib_tzinfo tz) {
		ttinfo to = null;
		rlong dummy = new rlong(0);		
		if ((to = fetch_timezone_offset(tz, ts, dummy))!=null) {
			return to.isdst;
		}
		return -1;
	}
	
	protected static timelib_time_offset timelib_get_time_zone_info(long ts, timelib_tzinfo tz) {
		ttinfo to = null;
		tlinfo tl = null;
		long offset = 0, leap_secs = 0;
		String abbr = null;
		timelib_time_offset tmp = new timelib_time_offset();
		rlong transistion_time = new rlong(0);
		if ((to = fetch_timezone_offset(tz, ts, transistion_time))!=null) {
			offset = to.offset;
			abbr = String.valueOf(tz.timezone_abbr.charAt((int)to.abbr_idx));
			tmp.is_dst = to.isdst;
			tmp.transistion_time = transistion_time.v;
		} else {
			offset = 0;
			abbr = tz.timezone_abbr;
			tmp.is_dst = 0;
			tmp.transistion_time = 0;
		}

		if ((tl = fetch_leaptime_offset(tz, ts))!=null) {
			leap_secs = -tl.offset;
		}

		tmp.offset = offset;
		tmp.leap_secs = leap_secs;
		tmp.abbr = abbr!=null ? abbr : "GMT";

		return tmp;
	}
	
	protected static long timelib_get_current_offset(timelib_time t) {
		timelib_time_offset gmt_offset = null;
		long retval;
				
		switch (t.zone_type) {
			case TIMELIB_ZONETYPE_ABBR:
			case TIMELIB_ZONETYPE_OFFSET:
				return (t.z + t.dst) * -60;
				
			case TIMELIB_ZONETYPE_ID:
				gmt_offset = timelib_get_time_zone_info(t.sse, t.tz_info);
				retval = gmt_offset.offset;
				gmt_offset = null;
				return retval;

			default:
				return 0;
		}
	}

	
}
