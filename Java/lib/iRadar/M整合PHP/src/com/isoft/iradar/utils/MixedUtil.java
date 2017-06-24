package com.isoft.iradar.utils;

import java.util.Map;

import com.isoft.types.CArray;
import com.isoft.types.IMap;

public class MixedUtil {

	private MixedUtil() {
	}

	/**
	 * recursive Map get
	 * @param obj
	 * @param keys
	 * @return
	 */
	@Deprecated
	protected static <T> T rmapGet(Map obj, Object... keys) {
		if (obj == null) {
			return null;
		}
		if(obj instanceof CArray) {
			return (T)((CArray)obj).getNested(keys);
		}
		
		Map ref = obj;
		Object key = null;
		for (int i = 0; i < keys.length - 1; i++) {
			key = keys[i];
			ref = (Map) ref.get(key);
			if (ref == null) {
				return null;
			}
		}
		return (T) ref.get(keys[keys.length - 1]);
	}
	
	/**
	 * recursive Map set
	 * @param obj
	 * @param kvs
	 * @return
	 */
	@Deprecated
	public static boolean rmapSet(Map obj, Object... kvs) {
		if (obj == null) {
			return false;
		}
		Map ref = obj;
		Object key = null;
		for (int i = 0; i < kvs.length - 2; i++) {
			key = kvs[i];
			if (!ref.containsKey(key)) {
				ref.put(key, new IMap());
			}
			ref = (Map) ref.get(key);
		}
		ref.put(kvs[kvs.length - 2], kvs[kvs.length - 1]);
		return true;
	}

	/**
	 * recursive Map remove
	 * @param obj
	 * @param keys
	 * @return
	 */
	public static <T> T rmapRemove(Map obj, Object... keys) {
		if (obj == null) {
			return null;
		}
		Map ref = obj;
		Object key = null;
		for (int i = 0; i < keys.length - 1; i++) {
			key = keys[i];
			ref = (Map) ref.get(key);
			if (ref == null) {
				return null;
			}
		}
		return (T) ref.remove(keys[keys.length - 1]);
	}
}
