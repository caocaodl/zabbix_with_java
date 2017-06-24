package com.isoft.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UnixDateUtil {

	private final static SimpleDateFormat UNIXTIMESTAMP = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String formatUnixTimestamp(long timestamp) {
		return UNIXTIMESTAMP.format(new Date(timestamp * 1000));
	}

}
