package com.isoft.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class Mapper {
	private static int n =0;
	public static void f(Object o) {
		System.out.println("["+(++n)+"]-----------------------------------");
		Class<? extends Object> clazz = o.getClass();
		Field[] fields = clazz.getDeclaredFields();
		Object v = null;
		for (Field f : fields) {
			if ((f.getModifiers() & Modifier.FINAL) == 0) {
				f.setAccessible(true);
				try {
					v = f.get(o);
					System.out.println(f.getName() + " => " + v);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void f(Map o) {
		System.out.println("["+(++n)+"]-----------------------------------");
		System.out.println(o);
	}
	
	public static void f(String o) {
		System.out.println("["+(++n)+"]-----------------------------------");
		System.out.println(o);
	}

}
