package com.isoft.iradar.core.utils;

import org.apache.commons.beanutils.ConvertUtils;

public abstract class EasyArray {
	public final static String[] EMPTY_STRINGS = new String[0];
	
	public static <T> T[] build(T... t) {
		return t;
	}
	
	public static String[] toStrs(Object o) {
		if(o instanceof String[]) {
			return (String[]) o;
		}
		return (String[])ConvertUtils.convert(o, String[].class);
	}
}
