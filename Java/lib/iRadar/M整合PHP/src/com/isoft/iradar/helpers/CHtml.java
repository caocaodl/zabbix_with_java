package com.isoft.iradar.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.utils.CJs;
import com.isoft.types.Mapper.Nest;

public class CHtml {

	public static Map<String, String> encode(Map<String, String> data) {
		Map<String, String> rs = new HashMap<String, String>();
		if (!Cphp.empty(data)) {
			for (Entry<String, String> e : data.entrySet()) {
				rs.put(Nest.as(e.getKey()).asString(), encode(e.getValue()));
			}
		}
		return rs;
	}

	public static String encode(String data) {
		return htmlspecialchars(data);
	}
	
	/**
	 * Encodes the data as a JSON string with HTML entities escaped.
	 *
	 * @return string
	 */
	public static String serialize(Object data) {
		return encode(CJs.encodeJson(data));
	}

	public static String htmlspecialchars(String data) {
		if (data != null && data.length() > 0) {
			data = data.replaceAll("&", "&amp;");
			data = data.replaceAll("\"", "&quot;");
			data = data.replaceAll("'", "&#039;");
			data = data.replaceAll("<", "&lt;");
			data = data.replaceAll(">", "&gt;");
		}
		return data;
	}
}
