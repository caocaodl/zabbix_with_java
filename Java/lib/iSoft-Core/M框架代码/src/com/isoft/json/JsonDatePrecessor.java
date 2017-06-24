package com.isoft.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonDatePrecessor implements JsonValueProcessor {

	private String format;

	public JsonDatePrecessor(String format) {
		this.format = format;
	}

	@Override
	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String[] objs = null;

		if (value instanceof Date[]) {
			Date[] dateArray = (Date[]) value;
			objs = new String[dateArray.length];
			for (int i = 0; i < dateArray.length; i++) {
				objs[i] = dateFormat.format(dateArray[i]);
			}
		}

		return objs;
	}

	@Override
	public Object processObjectValue(String key, Object value,
			JsonConfig jsonConfig) {
		if (value instanceof Date) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(value);
		}
		return value != null ? value.toString() : null;
	}

}
