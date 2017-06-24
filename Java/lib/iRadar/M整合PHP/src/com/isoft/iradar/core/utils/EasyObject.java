package com.isoft.iradar.core.utils;

import org.apache.commons.beanutils.ConvertUtils;

import com.isoft.types.CArray;
import com.isoft.types.Mapper.TObj;

public class EasyObject {
	
	public static <T> T as(Object obj, Class<T> targetClz) {
		return (T)ConvertUtils.convert(obj, targetClz);
	}
	
	public static CArray asCArray(Object obj) {
		return TObj.as(obj).asCArray();
	}

	public static String asString(Object obj) {
		return TObj.as(obj).asString();
	}

	public static Byte asByte(Object obj) {
		return TObj.as(obj).asByte();
	}

	public static Double asDouble(Object obj) {
		return TObj.as(obj).asDouble();
	}

	public static Float asFloat(Object obj) {
		return TObj.as(obj).asFloat();
	}

	public static Integer asInteger(Object obj) {
		return TObj.as(obj).asInteger();
	}

	public static Long asLong(Object obj) {
		return TObj.as(obj).asLong();
	}

	public static Short asShort(Object obj) {
		return TObj.as(obj).asShort();
	}
	
	public static Boolean asBoolean(Object obj) {
		return TObj.as(obj).asBoolean();
	}
}
