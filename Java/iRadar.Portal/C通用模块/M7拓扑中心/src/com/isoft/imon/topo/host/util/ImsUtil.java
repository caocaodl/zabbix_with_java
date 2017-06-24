package com.isoft.imon.topo.host.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;

import com.isoft.imon.topo.engine.discover.NetElement;
import com.isoft.imon.topo.engine.discover.bag.CompositeBag;
import com.isoft.imon.topo.engine.discover.credence.SnmpCredence;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.util.DateUtil;
import com.isoft.imon.topo.util.SimpleXMLUtil;

/**
 * 项目中用到的一些转化等公用的操作类
 * 
 * @author soft
 * 
 */
public final class ImsUtil {
	@SuppressWarnings("unused")
	private static final String XML_HEAD_UTF8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final DecimalFormat floatFormatter = new DecimalFormat("#.00");

	private static final Pattern patternX = Pattern.compile("x\\d{3,4}");
	private static final Pattern patternM = Pattern.compile("M\\d{1}");
	private static final Pattern patternIOS = Pattern.compile("\\d{1,2}");

	public static boolean isEmpty(CompositeBag<?> bag) {
		return (bag == null) || (bag.getEntities().isEmpty());
	}

	public static boolean isNull(String oidStr) {
		if ((oidStr == null) || (oidStr.equalsIgnoreCase("Null")))
			return true;
		if (oidStr.equalsIgnoreCase("noSuchObject"))
			return true;
		if (oidStr.equalsIgnoreCase("noSuchInstance")) {
			return true;
		}
		return oidStr.equalsIgnoreCase("endOfMibView");
	}

	public static float formatFloat(double val1, double val2) {
		return val2 == 0.0D ? 0.0F : Float.parseFloat(floatFormatter.format(val1 / val2));
	}

	public static String getSimpleXML(Element root) {
		String xml = SimpleXMLUtil.doc2String(new Document(root));
		if (xml == null) {
			throw new NullPointerException();
		}
		xml = xml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "").replace("\"", "'").trim();
		return xml;
	}

	public static SnmpOperator getSnmp(NetElement ne) {
		SnmpCredence sc = (SnmpCredence) ne.getCredence("SNMP");
		if (sc != null) {
			SnmpOperator snmp = new SnmpOperator();
			snmp.setTarget(ne.getIpAddress(), sc);
			return snmp;
		}
		return null;
	}

	public static String[] getFourTimeSegments(String startTime, String endTime) {
		long diff = DateUtil.getDiffTwoTimeToLong(endTime, startTime) / 4L;
		String[] times = new String[5];
		times[0] = startTime;
		times[4] = endTime;
		long sp = DateUtil.dateTimeToLong(startTime);
		for (int i = 1; i < 4; i++) {
			sp += diff;
			times[i] = DateUtil.longToTime(sp);
		}
		int days = DateUtil.getDiffDays(startTime, endTime);
		if (days > 1)
			for (int i = 0; i < times.length; i++)
				times[i] = times[i].split(" ")[0];
		else {
			for (int i = 0; i < times.length; i++)
				times[i] = times[i].substring(10, 16);
		}
		return times;
	}

	public static int getTimeType(String startTime, String endTime) {
		int days = DateUtil.getDiffDays(startTime, endTime);
		if (days <= 1) {
			int min = DateUtil.getDiffMinutes(startTime, endTime);
			if (min > 60) {
				return 2;
			}
			return 1;
		}
		if (days <= 30)
			return 3;
		return 4;
	}

	protected static String getHexMacAddress(String address) {
		try {
			// String hexAddress = null;
			StringBuffer hexAddress = null;
			String[] adds = address.split("\\.");
			for (int i = 0; i < adds.length; i++) {
				String digit = Integer.toHexString(new Integer(adds[i]).intValue());
				if (digit.length() == 1)
					digit = "0" + digit;
				if (hexAddress == null) {
					// hexAddress = digit;
					hexAddress = new StringBuffer(digit);
				} else {
					// hexAddress = hexAddress + ":" + digit;
					hexAddress.append(":" + digit);
				}
			}
			if (hexAddress == null) {
				return "";
			}
			return hexAddress.toString().toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String convertUTC(int utc) {
		Calendar firstDay = Calendar.getInstance();
		firstDay.set(1, 2010);
		firstDay.set(2, 0);
		firstDay.set(5, 1);
		firstDay.set(10, 0);
		firstDay.set(12, 0);
		firstDay.set(13, 0);
		firstDay.set(9, 0);
		firstDay.add(13, 28800);

		firstDay.add(13, utc);
		return DateFormat.getDateTimeInstance().format(firstDay.getTime());
	}

	public static String getIBMModel(String modelString) {
		String model = null;
		Matcher matcher = patternX.matcher(modelString);
		if (matcher.find())
			model = modelString.substring(matcher.start(), matcher.end());
		Matcher matcher2 = patternM.matcher(modelString);
		if (matcher2.find())
			model = model + " " + modelString.substring(matcher2.start(), matcher2.end());
		if (model == null)
			return modelString;
		return model;
	}

	public static int getIOSNumber(String iosString) {
		int total = 0;
		try {
			Matcher matcher = patternIOS.matcher(iosString);
			int i = 0;
			while (matcher.find()) {
				String var = iosString.substring(matcher.start(), matcher.end());
				int temp = Integer.parseInt(var);
				if (i == 0)
					total += temp * 1000;
				else if (i == 1)
					total += temp * 100;
				else
					total += temp;
				i++;
			}
		} catch (Exception e) {
			System.out.println("getIOSNumber error:" + iosString);
		}
		return total;
	}
}
