package com.isoft.biz.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.LinkedMap;

@SuppressWarnings("unchecked")
public final class DB {

	private DB() {
	}

	private static String flowcodeTab = "sys_m_id";
	private final static Map<String, String> subtitudes = new LinkedMap();

	static {
		Properties gprops = new Properties();
		InputStream is = DB.class.getResourceAsStream("db.global.xml");
		if (is != null) {
			try {
				gprops.loadFromXML(is);
			} catch (Exception e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		if (!gprops.isEmpty()) {
			if (gprops.containsKey("SUBTITUDES")) {
				String subs = gprops.getProperty("SUBTITUDES");
				if (subs != null && subs.length() > 0) {
					Pattern pattern = Pattern.compile("([\\w.-]+)=([\\w.-]+)");
					Matcher matcher = pattern.matcher(subs);
					while (matcher.find()) {
						subtitudes.put(matcher.group(1), matcher.group(2));
					}
				}
			}
			if (gprops.containsKey("FLOWCODETAB")) {
				String tname = gprops.getProperty("FLOWCODETAB");
				if (tname != null && tname.length() > 0) {
					flowcodeTab = tname;
				}
			}
		}
	}

	public static Map<String, String> getSubtitudes() {
		return subtitudes;
	}

	public static String getFlowcodeTabName() {
		return flowcodeTab;
	}

}
