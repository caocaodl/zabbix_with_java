package com.isoft.iradar.core.utils;

public abstract class EnumUtil {
	
	public static <T extends ValueableEnum<V>, V> T fetch(T[] ts, V v) {
		for(T t: ts) {
			V tv = t.v();
			if(tv.equals(v)){
				return t;
			}
		}
		return null;
	}
	
	
	public static interface ValueableEnum<T>{
		public T v();
	}
}
