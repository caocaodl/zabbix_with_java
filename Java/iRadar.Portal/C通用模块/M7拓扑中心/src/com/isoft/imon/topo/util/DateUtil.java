package com.isoft.imon.topo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期辅助类
 * 
 * @author Administrator
 * 
 */
public final class DateUtil {
	public static final SimpleDateFormat datetimeFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
	public static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat yyyymmFormat = new SimpleDateFormat("yyyyMM");
	public static final SimpleDateFormat hourFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	public static final int LastOneHour = 1;
	public static final int LastOneDay = 2;
	public static final int LastOneWeek = 3;
	public static final int LastOneMonth = 4;

	/**
	 * 获取当前时间，格式为yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public static synchronized String getCurrentDateTime2() {
		return datetimeFormat2.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取当前时间，格式为 HH:mm:ss
	 * 
	 * @return
	 */
	public static synchronized String getCurrentTime() {
		return timeFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取当前时间，格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static synchronized String getCurrentDateTime() {
		return datetimeFormat.format(GregorianCalendar.getInstance().getTime());
	}

	/**
	 * 获取当前时间，格式为yyyy-MM-dd
	 * 
	 * @return
	 */
	public static synchronized String getCurrentDate() {
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取当前月，格式为yyyy-MM
	 * 
	 * @return
	 */
	public static synchronized String getCurrentMonth() {
		return monthFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取当前年份，格式为yyyy
	 * 
	 * @return
	 */
	public static synchronized String getCurrentYear() {
		return yearFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取当前小时，格式为yyyy-MM-dd HH
	 * 
	 * @return
	 */
	public static synchronized String getCurrentHour() {
		return hourFormat.format(Calendar.getInstance().getTime());
	}

	public static synchronized String getLastHour() {
		Calendar cal = Calendar.getInstance();
		cal.add(10, -1);

		return hourFormat.format(cal.getTime());
	}

	/**
	 * 获取月份，格式为yyyyMM
	 * 
	 * @return
	 */
	public static synchronized String getYYYYMM() {
		return yyyymmFormat.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取时间差值
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static long getDiffTwoTimeToLong(String time1, String time2) {
		if ((time1 == null) || (time2 == null)) {
			return 0L;
		}
		if ((time1.trim().length() == 0) || (time2.trim().length() == 0)) {
			return 0L;
		}
		return dateTimeToLong(time1) - dateTimeToLong(time2);
	}

	/**
	 * 获取时间差值
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String getDiffTwoTime(String time1, String time2) {
		if ((time1 == null) || (time2 == null)) {
			return "";
		}
		if ((time1.trim().length() == 0) || (time2.trim().length() == 0)) {
			return "";
		}
		long diffTime = dateTimeToLong(time2) - dateTimeToLong(time1);
		return secondToTimeString(diffTime);
	}

	/**
	 * 相差的天数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public synchronized static int getDiffDays(String time1, String time2) {
		try {
			long diffTime = datetimeFormat.parse(time2).getTime() - datetimeFormat.parse(time1).getTime();
			return (int) (diffTime / 1000L / 86400L);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 相差分钟数
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static synchronized int getDiffMinutes(String time1, String time2) {
		try {
			long diffTime = datetimeFormat.parse(time2).getTime() - datetimeFormat.parse(time1).getTime();
			return (int) (diffTime / 1000L / 60L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 时间类型转换为Long型
	 * 
	 * @param dateTime
	 * @return
	 */
	public synchronized static long dateTimeToLong(String dateTime) {
		if (dateTime == null || dateTime.trim().length() == 0) {
			return 0L;
		}
		try {
			Date date = datetimeFormat.parse(dateTime);
			return date.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * long转换为时间类型
	 * 
	 * @param timeLong
	 * @return
	 */
	public static synchronized String longToTime(long timeLong) {
		if (timeLong == 0L)
			return "";

		Date date = new Date(timeLong);
		return datetimeFormat.format(date);
	}

	/**
	 * 根据时间值计算多少天多少小时多少分钟多少秒
	 * 
	 * @param second
	 * @return
	 */
	public static String secondToTimeString(long second) {
		second /= 1000L;

		StringBuffer timeStr = new StringBuffer(10);
		long hh24 = second / 3600L;
		long day = hh24 / 24L;
		long surplus = second % 3600L;

		long mi = surplus / 60L;
		long ss = surplus % 60L;

		if (day > 0L) {
			timeStr.append(day);
			timeStr.append("天");
			hh24 -= day * 24L;
		}
		if (hh24 > 0L) {
			timeStr.append(hh24);
			timeStr.append("小时");
		}
		if (mi > 0L) {
			timeStr.append(mi);
			timeStr.append("分钟");
		}
		if ((timeStr.length() == 0) || (ss > 0L)) {
			timeStr.append(ss);
			timeStr.append("秒");
		}
		return timeStr.toString();
	}

	/**
	 * 当前时间的毫秒数
	 * 
	 * @return
	 */
	public static long getCurrentLongDateTime() {
		return new Date().getTime();
	}

	/**
	 * 当前时间转换为ID
	 * 
	 * @return
	 */
	public static synchronized String getCurrentTimeAsID() {
		return String.valueOf(System.nanoTime());
	}

	/**
	 * 获取星期几
	 * 
	 * @param day
	 * @return
	 */
	public static String getWeekDay(int day) {
		if (day == 1)
			return "星期一";
		if (day == 2)
			return "星期二";
		if (day == 3)
			return "星期三";
		if (day == 4)
			return "星期四";
		if (day == 5)
			return "星期五";
		if (day == 6) {
			return "星期六";
		}
		return "星期日";
	}

	/**
	 * 相差多少分钟
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static synchronized long getDiffSeconds(String time1, String time2) {
		try {
			long diffTime = Math.abs(datetimeFormat.parse(time2).getTime() - datetimeFormat.parse(time1).getTime());
			return diffTime / 1000L;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	/**
	 * 获取两个时间点
	 * 
	 * @param timeType
	 * @return
	 */
	public static synchronized String[] getTwoTimePoints(int timeType) {
		String startTime = null;
		String endTime = null;
		Calendar cal = null;
		if (timeType == LastOneHour) {
			endTime = getCurrentDateTime();
			cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, -1);
			startTime = longToTime(cal.getTimeInMillis());
		} else if (timeType == LastOneDay) {
			startTime = getCurrentDate() + " 00:00:00";
			endTime = getCurrentDate() + " 23:59:59";
		} else if (timeType == LastOneWeek) {
			cal = Calendar.getInstance();
			int day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY)
				day = 8;
			day -= 2;
			cal.add(Calendar.DATE, -day);
			startTime = dateFormat.format(cal.getTime()) + " 00:00:00";

			cal = Calendar.getInstance();
			day = cal.get(Calendar.DAY_OF_WEEK);
			if (day == Calendar.SUNDAY)
				day = 8;
			day = 8 - day;
			cal.add(Calendar.DATE, day);
			endTime = dateFormat.format(cal.getTime()) + " 23:59:59";
		} else {
			cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startTime = dateFormat.format(cal.getTime()) + " 00:00:00";

			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			endTime = dateFormat.format(cal.getTime()) + " 23:59:59";
		}
		return new String[] {
				startTime,
				endTime
		};
	}

	/**
	 * 获取标准时间
	 * 
	 * @param cst
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static synchronized String getUTCTime(String cst) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(cst));
		cal.add(10, 8);
		return datetimeFormat.format(cal.getTime());
	}
}