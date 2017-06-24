package com.isoft.zend;

import com.isoft.zend.ext.date.ZDateExport;
import com.isoft.zend.ext.standard.ZStandardExport;

public class ZendUtils {
	
	public static int natcasesort(String left, String right) {
		return ZStandardExport.natcasesort(left, right);
	}
	
	public static int natsort(String left, String right) {
		return ZStandardExport.natsort(left, right);
	}
	
	public static long time() {
		return ZDateExport.time();
	}
	
	public static long time(Long time) {
		return ZDateExport.time(time);
	}
	
	public static String date(String format) {
		return ZDateExport.date(format);
	}

	public static String date(String format, long ts) {
		return ZDateExport.date(format, ts);
	}
	
}
