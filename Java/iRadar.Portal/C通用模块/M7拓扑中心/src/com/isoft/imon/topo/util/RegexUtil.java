package com.isoft.imon.topo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	
	/** 验证字符串是否为整数
	 * @param numberstr 数字字符串参数
	 * @return
	 */
	public static boolean isNumber(String numberstr){
		 String regEx = "^[0-9]*[1-9][0-9]*$";
		 return  regex(numberstr,regEx);
	}
	
	/**  验证日期输入格式是否为"yyyy-mm-dd hh:mm:ss"
	 * @param datestr 日期字符串参数
	 * @return
	 */
	public static boolean isDateFormate(String datestr){
		String regEx = "[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}";
		return  regex(datestr,regEx);
	}
	
	/**  JAVA 正则表达式通用部分
	 * @param str
	 * @param regEx
	 * @return
	 */
	private static boolean regex(String str,String regEx){
		 Pattern pattern = Pattern.compile(regEx);
		 Matcher matcher = pattern.matcher(str);
		 return  matcher.find();
	}
}
