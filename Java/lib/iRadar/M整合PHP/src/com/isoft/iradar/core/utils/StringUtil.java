package com.isoft.iradar.core.utils;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public final class StringUtil {
    
    public final static String SPLIT_CHAR_HTML = "\0";

    private StringUtil() {}
    
    /**
     * 判断该字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isEmptyStr(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return (str != null) && (str.length() != 0);
    }

    public static boolean equals(String str1, String str2) {
        if (str1 != null && str2 != null) {
            if (str1.length() == str2.length()) {
                return str1.equals(str2);
            }
            return false;
        }
        if (str1 == null && str2 == null) {
            return true;
        }
        return false;
    }

    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static int getIntValue(String value, int defaultValue, String key) {

        int result;
        try {
            result = Integer.valueOf(value).intValue();
        } catch (Exception e) {
            result = defaultValue;
        }
        return result;
    }

    public static Color getColorValue(String value, Color defaultValue,
            String key) {
        Color result;
        try {
            char pre = value.charAt(0);
            if (pre != '#') {
                value = "#" + value;
            }
            result = Color.decode(value);
        } catch (Exception e) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * 截掉字符串后面的空格
     * 
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (!isEmptyStr(str)) {
            return str.trim();
        } else {
            return str;
        }
    }

    /**
     * 删除前后缀
     * 
     * @param value
     * @return
     */
    public static String trimPreSubfix(String value) {
        return value;
    }

    public static String urlEncoder(String str) {
        try {
            return URLEncoder.encode(str, "utf8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String urlDecoder(String str) {
        try {
            return URLDecoder.decode(str, "utf8");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String encodeValue(String v) {
        if (v != null && v.length() > 0) {
            v = v.replaceAll("%", "%25");
            v = v.replaceAll("#", "%23");
            v = v.replaceAll("&", "%26");
        }
        return v;
    }


    @SuppressWarnings("unchecked")
    public static List<String> splitAsList(String source, char delimiter) {
        return splitAsList(source, delimiter, false);
    }

    /**
     * Splits a String into substrings along the provided char delimiter and
     * returns the result as a List of Substrings.
     * <p>
     * 
     * @param source
     *            the String to split
     * @param delimiter
     *            the delimiter to split at
     * @param trim
     *            flag to indicate if leading and trailing white spaces should
     *            be omitted
     * 
     * @return the List of splitted Substrings
     */
    @SuppressWarnings("unchecked")
    public static List<String> splitAsList(String source, char delimiter, boolean trim) {

        List<String> result = new ArrayList<String>();
        int i = 0;
        int l = source.length();
        int n = source.indexOf(delimiter);
        while (n != -1) {
            // zero - length items are not seen as tokens at start or end
            if ((i < n) || ((i > 0) && (i < l))) {
                if (trim) {
                    result.add(source.substring(i, n).trim());
                } else {
                    result.add(source.substring(i, n));
                }
            }
            i = n + 1;
            n = source.indexOf(delimiter, i);
        }
        // is there a non - empty String to cut from the tail?
        if (n < 0) {
            n = source.length();
        }
        if (i < n) {
            if (trim) {
                result.add(source.substring(i).trim());
            } else {
                result.add(source.substring(i));
            }
        }
        return result;
    }


    // GENERAL_PUNCTUATION 判断中文的“号
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                /** GENERAL_PUNCTUATION 判断中文的“号 */
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                /** CJK_SYMBOLS_AND_PUNCTUATION判断中文的。号 */
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                /** HALFWIDTH_AND_FULLWIDTH_FORMS判断中文的，号 */
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
    
    public static List<String> splitStr2List(String val) {
        if (val != null) {
            String[] list = val.split("[ ]*,[ ]*");
            return Arrays.asList(list);
        } else {
            return Collections.EMPTY_LIST;
        }
    }
    
    public static String join(String[] array, String split) {
    	return StringUtils.join(array, split);
    }
    public static String joinHtml(Object... os) {
    	String[] ss = new String[os.length]; 
    	for(int i=0,ilen=os.length; i<ilen; i++) ss[i] = String.valueOf(os[i]);
    	return joinHtml(ss);
    }
    public static String joinHtml(String... ss) {
    	return join(ss, SPLIT_CHAR_HTML);
    }
    public static String[] splitHtml(String s) {
    	return s.split(SPLIT_CHAR_HTML);
    }
    
    public static int getByteLen(String s) {
    	return getByteLen(s, 3);
    }
    public static int getByteLen(String s, int rate) {
    	if(s == null) return 0;
	    int x=0;
	    for (int i=0, ilen=s.length(); i<ilen; i++) {
	    	x += (s.codePointAt(i) <= 128)? 1: rate;
	    }
	    return x;
    }
    
    public static int toInt(String s, int defaultValue) {
		try { 
			return Integer.valueOf(s);
		}catch(Exception e) {
			return defaultValue;
		}
    }
    
    private final static SimpleDateFormat FORMAT_INPUT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public final static Date parseDateT(String s) {
    	if(StringUtil.isEmptyStr(s)) return null;
    	try {
			return FORMAT_INPUT.parse(s);
		} catch (ParseException e) {
			return null;
		}
    }
    
    
    public final static char[] DIGITS_34 = {
        '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 
        'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' ,
        'J' , 'K' , 'L' , 'M' , 'N' ,
        'P' , 'Q' , 'R' , 'S' , 'T' ,
        'U' , 'V' , 'W' , 'X' , 'Y' , 'Z'
    };
   
    public final static String toStringBy34r(int i) {
    	int radix = DIGITS_34.length;
    	
        char buf[] = new char[33];
        boolean negative = (i < 0);
        int charPos = 32;

        if (!negative) {
            i = -i;
        }

        while (i <= -radix) {
            buf[charPos--] = DIGITS_34[-(i % radix)];
            i = i / radix;
        }
        buf[charPos] = DIGITS_34[-i];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (33 - charPos));
    }
}
