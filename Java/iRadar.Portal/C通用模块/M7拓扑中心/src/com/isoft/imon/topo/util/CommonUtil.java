package com.isoft.imon.topo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;

import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.iradar.inc.Defines;

/**
 * 公共方法
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("unchecked")
public class CommonUtil {

	/**
	 * 转化字符编码
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toZHEncoding(String value) {
		if (value != null && value.length() != 0) {
			try {
				return new String(value.getBytes("iso-8859-1"), "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	public static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}

	/**
	 * 转化字符编码
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String toEncoding(String value) {
		if (value != null && value.length() != 0) {
			try {
				return new String(value.getBytes("iso-8859-1"), "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 100867010 -> i-06031bc2
	 * 
	 * @param instanceId
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String buildInstanceId(int instanceId) {
		String trans = Integer.toHexString(instanceId);
		StringBuffer temp = new StringBuffer();
		for (int i = 0; trans.length() < 8; i++) {
			temp.append("0");
			if (temp.toString().length() + trans.length() == 8) {
				break;
			}
		}
		return "instance-" + temp.toString() + trans;
	}

	public static final DecimalFormat intFormatter = new DecimalFormat("#");
	public static final DecimalFormat floatFormatter = new DecimalFormat("#.##");
	public static final DecimalFormat floatFormatter2 = new DecimalFormat("##.##");
	/* for discovering process bar */
	public static final DecimalFormat floatFormatter3 = new DecimalFormat("##.###");

	public static final DecimalFormat doubleFormatter = new DecimalFormat("##.##");

	/**
	 * 是否为空
	 * 
	 * @param v
	 * @return
	 */
	public static boolean isEmpty(String v) {
		return (v == null) || (v.length() == 0) || ("null".equalsIgnoreCase(v));
	}

	/**
	 * 是否为空
	 * 
	 * @param objs
	 * @return
	 */
	public static boolean isEmpty(Object[] objs) {
		if ((objs == null) || (objs.length == 0))
			return true;
		Object[] arrayOfObject = objs;
		int j = objs.length;
		for (int i = 0; i < j; i++) {
			Object obj = arrayOfObject[i];
			if (obj == null)
				return true;
		}
		return false;
	}

	/**
	 * 获取参数
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> getParas(HttpServletRequest request) {
		HashMap map = new HashMap();
		for (Enumeration em = request.getParameterNames(); em.hasMoreElements();) {
			String name = (String) em.nextElement();
			map.put(name, request.getParameter(name));
		}
		return map;
	}

	/**
	 * 连接是否为空
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null) || (collection.isEmpty());
	}

	/**
	 * 获取实例
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getInstance(String className) {
		try {
			Class clazz = Class.forName(className);
			return clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("can not instance class:" + className);
		}
	}

	/**
	 * 获取Class类
	 * 
	 * @param className
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Class getClass(String className) {
		try {
			return Class.forName(className);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getRidQuote(String value) {
		if ((value != null) && (value.indexOf("'") >= 0))
			return value.replace("'", " ");
		return value;
	}

	/**
	 * 获取方法对象
	 * 
	 * @param classInstance
	 * @param methodName
	 * @return
	 */
	public static Object invokeMethod(Object classInstance, String methodName) {
		if ((classInstance == null) || (methodName == null)) {
			throw new IllegalArgumentException(methodName + " doesn't exist");
		}
		Method method = lookupMethod(classInstance.getClass(), methodName);
		if (method == null)
			throw new IllegalArgumentException(methodName + " is invoked error");
		try {
			return method.invoke(classInstance, new Object[0]);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

	}

	/**
	 * 查找方法，获取方法对象
	 * 
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Method lookupMethod(Class clazz, String methodName) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equalsIgnoreCase(methodName))
				return method;
		}
		return null;
	}

	/**
	 * 格式化数据，返回long
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static long formatLong(double val1, double val2) {
		return val2 == 0.0D ? 0L : Long.parseLong(intFormatter.format(val1 / val2));
	}

	/**
	 * 格式化数据，返回int
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static int formatInt(double val1, double val2) {
		return val2 == 0.0D ? 0 : Integer.parseInt(intFormatter.format(val1 / val2));
	}

	/**
	 * 格式化数据，返回float
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static float formatFloat(double val1, double val2) {
		return val2 == 0.0D ? 0.0F : Float.parseFloat(floatFormatter.format(val1 / val2));
	}

	/**
	 * 格式化数据，返回float
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static float formatFloat2(double val1, double val2) {
		return val2 == 0.0D ? 0.0F : Float.parseFloat(floatFormatter2.format(val1 / val2));
	}

	/**
	 * 格式化数据，返会float
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static float formatFloat3(double val1, double val2) {
		return val2 == 0.0D ? 0.0F : Float.parseFloat(floatFormatter3.format(val1 / val2));
	}

	/**
	 * 格式化数据，返回double
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static double formatdouble(double val1, double val2) {
		return val2 == 0.00D ? 0.00D : Double.parseDouble(doubleFormatter.format(val1 / val2));
	}

	/**
	 * map转化为字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToString(Map<String, String> map) {
		StringBuffer str = new StringBuffer(50);
		for (String temp : map.keySet()) {
			str.append(";").append(temp).append("=").append((String) map.get(temp));
		}
		return str.substring(1);
	}

	/**
	 * 数组转化为字符串
	 * 
	 * @param array
	 * @return
	 */
	public static String arrayToString(String[] array) {
		StringBuffer str = new StringBuffer(50);
		String[] arrayOfString = array;
		int j = array.length;
		for (int i = 0; i < j; i++) {
			String temp = arrayOfString[i];
			str.append(",").append(temp);
		}
		return str.substring(1);
	}

	/**
	 * 数组转化为引用字符串
	 * 
	 * @param array
	 * @return
	 */
	public static String arrayToQuotString(String[] array) {
		StringBuffer str = new StringBuffer(100);
		String[] arrayOfString = array;
		int j = array.length;
		for (int i = 0; i < j; i++) {
			String temp = arrayOfString[i];
			str.append(",'").append(temp).append("'");
		}
		return str.substring(1);
	}

	/**
	 * list转化为字符串
	 * 
	 * @param list
	 * @return
	 */
	public static String listToString(List<String> list) {
		if (!isEmpty(list)) {
			StringBuffer str = new StringBuffer(50);
			for (String temp : list)
				str.append(",").append(temp);
			return str.substring(1);
		}
		return "";
	}

	/**
	 * 复制对象
	 * 
	 * @param clazz
	 * @param src
	 * @return
	 */
	public static <T> T copyBean(Class<T> clazz, Object src) {
		T dest = null;
		try {
			dest = clazz.newInstance();
			BeanUtils.copyProperties(dest, src);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return dest;
	}

	/**
	 * 获取参数字符串
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getParaString(HttpServletRequest request) {
		StringBuffer paraStr = new StringBuffer(100);
		for (Enumeration em = request.getParameterNames(); em.hasMoreElements();) {
			String name = (String) em.nextElement();
			if ((!"random".equals(name)) && (!isEmpty(request.getParameter(name)))) {
				paraStr.append(name).append("=");
				paraStr.append(request.getParameter(name));
				paraStr.append("&");
			}
		}
		if (paraStr.length() > 0)
			paraStr.setLength(paraStr.length() - 1);
		return paraStr.toString();
	}

	/**
	 * 复制文件
	 * 
	 * @param in
	 * @param fileName
	 * @throws IOException
	 */
	public static void copyFile(InputStream in, String fileName) throws IOException {
		FileOutputStream fs = null;
		try {
			fs = new FileOutputStream(fileName);
			byte[] buffer = new byte[1048576];
			int byteread = 0;
			while ((byteread = in.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
				fs.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fs != null) {
				fs.close();
			}
			in.close();
		}
	}

	/**
	 * 创建文件或目录
	 * 
	 * @param filePath
	 * @param context
	 */
	public static void createFile(String filePath, String context) {
		FileWriter fw = null;
		try {
			File _file = new File(filePath);
			if (!_file.exists()) {
				_file.createNewFile();
			}
			fw = new FileWriter(_file);
			PrintWriter pw = new PrintWriter(fw);
			pw.println(context);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (Exception localException2) {
				localException2.printStackTrace();
			}
		}
	}

	/**
	 * 执行命令
	 * 
	 * @param cmd
	 */
	public static void exeCmd(String cmd) {
		// Process p = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			isr = new InputStreamReader(p.getErrorStream());
			br = new BufferedReader(isr);
			while (br.readLine() != null)
				p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}

			} catch (Exception localException2) {
				localException2.printStackTrace();
			} finally {
				try {
					if (isr != null) {
						isr.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 对象转化为Json格式
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) {
		if (obj != null)
			return JSONArray.fromObject(obj, new JsonConfig()).toString();
		return "[]";
	}

	/**
	 * 格式化日期时间格式，格式到毫秒级数据 2014-04-09 17:01:10.000000 --> 2014-04-09 17:01:10.000
	 * 2014-04-09 17:01:10 --> 2014-04-09 17:01:10.000
	 * 
	 * @param dateTime
	 * @return String
	 */
	public static String formatDateTimeMs(String dateTime) {
		if (dateTime != null && !"".equals(dateTime)) {
			int index = dateTime.indexOf(".");
			if (index > 0) {
				return dateTime.substring(0, index + 4);
			} else {
				return dateTime.concat(".000");
			}
		}
		return dateTime;
	}

	/**
	 * 根据设备返回SNMP的版本号
	 * 
	 * @param host
	 * @return int
	 */
	public static int getSnmpVersionByHost(Host host) {
		int version = Defines.SVC_SNMPv2c;
		// 获取Snmp 凭证信息
		SnmpCredence snmpCredence = (SnmpCredence) host.getCredence("SNMP");
		if (snmpCredence != null) {
			version = snmpCredence.getVersion();
		}
		return version;
	}

	/**
	 * 格式化double类型数据，四舍五入保留两位小数，L。
	 * 
	 * @param value
	 * @return
	 */
	public static double formatDouble(double value) {
		BigDecimal bd = new BigDecimal(value);
		double result = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return result;
	}

	/**
	 * 获取下一个UUID
	 * 
	 * @return String
	 */
	public static synchronized String getNextUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * String日期转换为UTC的Long型事件戳
	 * 
	 * @param formatDate
	 *            ("MM/dd/yyyy HH:mm:ss") "yyyy-mm-dd HH:mm:ss"
	 * @param date
	 *            ("12/31/2013 21:08:00")
	 * @return * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public static Long transferStringDateToLong(String formatDate, String date) throws ParseException {
		DateFormat sdf = new SimpleDateFormat(formatDate);
		Date dt = sdf.parse(date);
		// TimeZone.getDefault()获取主机的默认TimeZone，即时区偏移量。
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		cal.setTime(dt);
		return dt.getTime() + cal.getTimeZone().getRawOffset();
	}
}
