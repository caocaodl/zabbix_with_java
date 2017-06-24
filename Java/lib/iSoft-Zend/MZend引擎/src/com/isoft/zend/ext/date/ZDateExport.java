package com.isoft.zend.ext.date;


public class ZDateExport {

	public static long time() {
		return Libc.time();
	}

	public static long time(Long time) {
		return Libc.time(time);
	}

	public static String date(String format) {
		return Date.php_date(format, null, true);
	}

	public static String date(String format, long ts) {
		return Date.php_date(format, ts, true);
	}
	
	public static Long strtotime(String time){
		return Date.strtotime(time, null);
	}
	
	public static Long strtotime(String time, Long  preset_ts){
		return Date.strtotime(time, preset_ts);
	}
}
