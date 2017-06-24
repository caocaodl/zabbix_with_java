package com.isoft.json;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JsonUtil {

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

	/**
	 * 装换对象至JSON字符串
	 */
	public static String encodeObject2Json(Object obj) {
		if (obj instanceof Collection<?>) {
			return encodeCollection2Json((Collection<?>) obj,
					DEFAULT_DATE_FORMAT);
		} else if (obj instanceof Jsonable) {
			return ((Jsonable) obj).toJsonString();
		} else {
			return encodeObject2Json(obj, DEFAULT_DATE_FORMAT);
		}
	}

	/**
	 * 装换对象至JSON字符串
	 */
	public static JSON encodeObject2JsonObj(Object obj) {
		if (obj instanceof Jsonable) {
			return ((Jsonable) obj).toJson();
		} else {
			return encodeObject2JsonObj(obj, DEFAULT_DATE_FORMAT);
		}
	}

	/**
	 * 装换对象至JSON字符串
	 */
	public static String encodeObject2Json(Object obj, String dateFormatStr) {
		String jsonString = "{}";
		if (obj != null) {
			JsonConfig cfg = new JsonConfig();
			cfg.registerJsonValueProcessor(Date.class, new JsonDatePrecessor(
					dateFormatStr));

			JSONObject jsonObject = JSONObject.fromObject(obj, cfg);
			jsonString = jsonObject.toString();
		}
		return jsonString;
	}

	/**
	 * 装换对象至JSON对象
	 */
	public static JSONObject encodeObject2JsonObj(Object obj,
			String dateFormatStr) {
		JSONObject jsonObject = new JSONObject();
		if (obj != null) {
			JsonConfig cfg = new JsonConfig();
			cfg.registerJsonValueProcessor(Date.class, new JsonDatePrecessor(
					dateFormatStr));

			jsonObject = JSONObject.fromObject(obj, cfg);
		}
		return jsonObject;
	}

	/**
	 * 装换对象至JSON字符串
	 */
	public static String encodeCollection2Json(Collection<?> col,
			String dateFormatStr) {
		String jsonString = "[]";
		if (CollectionUtils.isNotEmpty(col)) {
			JSONArray jsonArray = new JSONArray();
			for (Object obj : col) {
				jsonArray.element(encodeObject2JsonObj(obj));
			}
			jsonString = jsonArray.toString();
		}
		return jsonString;
	}

	/**
	 * 装换对象至JSON数组
	 */
	public static JSONArray encodeCollection2Array(Collection<?> col) {
		return encodeCollection2Array(col, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 装换对象至JSON数组
	 */
	public static JSONArray encodeCollection2Array(Collection<?> col,
			String dateFormatStr) {
		JSONArray jsonArray = new JSONArray();

		if (CollectionUtils.isNotEmpty(col)) {
			for (Object obj : col) {
				jsonArray.element(encodeObject2JsonObj(obj));
			}
		}

		return jsonArray;
	}
}
