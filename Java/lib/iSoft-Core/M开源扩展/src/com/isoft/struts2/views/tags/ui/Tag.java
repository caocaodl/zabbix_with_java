package com.isoft.struts2.views.tags.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.LinkedMap;

@SuppressWarnings("unchecked")
public final class Tag {

	private Tag() {
	}

	private final static Map<String, List<String>> resources = new LinkedMap();

	static {
		Pattern pattern = Pattern.compile("([\\S]+js|[\\S]+css)");
		Properties gprops = new Properties();
		InputStream is = Tag.class.getResourceAsStream("tag.global.xml");
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
			for (Entry<Object, Object> e : gprops.entrySet()) {
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if (value != null && value.length() > 0) {
					List<String> res = new LinkedList<String>();
					Matcher matcher = pattern.matcher(value);
					while (matcher.find()) {
						res.add(matcher.group(1));
					}
					resources.put(key, res);
				}
			}
		}

		Properties eprops = new Properties();
		is = Tag.class.getResourceAsStream("tag.extend.xml");
		if (is != null) {
			try {
				eprops.loadFromXML(is);
			} catch (Exception e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		if (!eprops.isEmpty()) {
			for (Entry<Object, Object> e : eprops.entrySet()) {
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				if (value != null && value.length() > 0) {
					List<String> res = resources.get(key);
					if (res == null) {
						res = new LinkedList<String>();
						resources.put(key, res);
					}
					Matcher matcher = pattern.matcher(value);
					while (matcher.find()) {
						String r = matcher.group(1);
						if (!res.contains(r)) {
							res.add(r);
						}
					}
				}
			}
		}
	}

	public static List<String> getResource(String key){
		return resources.get(key);
	}

}
