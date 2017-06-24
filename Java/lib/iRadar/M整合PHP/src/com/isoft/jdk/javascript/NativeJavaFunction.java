package com.isoft.jdk.javascript;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import sun.org.mozilla.javascript.internal.NativeJavaMethod;

public class NativeJavaFunction extends NativeJavaMethod {

	public NativeJavaFunction(Method m, String name) {
		super(m, name);
	}
	
	public NativeJavaFunction(Collection<Method> methods, String name) {
		super(methods.iterator().next(), name);
		
		if(methods.size() > 1) {
			try {
				Field f = NativeJavaMethod.class.getDeclaredField("methods");
				f.setAccessible(true);
				
				Object[] os = (Object[])f.get(this);
				
				Class memberBoxCls = os[0].getClass();
				Constructor c = memberBoxCls.getDeclaredConstructor(Method.class);
				c.setAccessible(true);
				
				Object memberBoxArray = Array.newInstance(memberBoxCls, methods.size());
				int index = 0;
				for(Method method: methods) {
					Object memberBox;
					if(index == 0) {
						memberBox = os[0];
					}else {
						memberBox = c.newInstance(method);
					}
					Array.set(memberBoxArray, index++, memberBox);
				}
				f.set(this, memberBoxArray);
				
				c.setAccessible(false);
				f.setAccessible(false);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static void main(String[] args) {
		List<Method> ls = Arrays.asList(NativeJavaFunction.class.getMethods());
		new NativeJavaFunction(ls, "xx");
	}
	
}
