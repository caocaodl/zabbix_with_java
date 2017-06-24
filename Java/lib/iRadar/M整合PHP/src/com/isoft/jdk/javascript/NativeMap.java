package com.isoft.jdk.javascript;

import java.util.Map;

import sun.org.mozilla.javascript.internal.Scriptable;

import com.isoft.types.CArray;

public class NativeMap implements Scriptable {

	private CArray map;
	
	public NativeMap(Map m) {
		this.map = CArray.valueOf(m);
	}
	
	@Override
	public void delete(int arg0) {
		map.remove(arg0);
	}

	@Override
	public void delete(String arg0) {
		map.remove(arg0);
	}

	@Override
	public Object get(int arg0, Scriptable arg1) {
		return get(arg0);
	}

	@Override
	public Object get(String arg0, Scriptable arg1) {
		return get(arg0);
	}
	
	private Object get(Object key) {
		Object o = map.get(key);
		if(o instanceof Map) {
			o = new NativeMap((Map)o);
		}
		return o;
	}

	@Override
	public String getClassName() {
		return "NativeMap";
	}

	@Override
	public Object getDefaultValue(Class<?> arg0) {
		return String.valueOf(map);
	}

	@Override
	public Object[] getIds() {
		return map.keySet().toArray();
	}

	@Override
	public boolean has(int arg0, Scriptable arg1) {
		return map.containsKey(arg0);
	}

	@Override
	public boolean has(String arg0, Scriptable arg1) {
		return map.containsKey(arg0);
	}

	@Override
	public boolean hasInstance(Scriptable arg0) {
		return false;
	}

	@Override
	public void put(int arg0, Scriptable arg1, Object arg2) {
		map.put(arg0, arg2);
	}

	@Override
	public void put(String arg0, Scriptable arg1, Object arg2) {
		map.put(arg0, arg2);
	}

	
	
	@Override
	public Scriptable getParentScope() {
		return null;
	}

	@Override
	public Scriptable getPrototype() {
		return null;
	}
	
	@Override
	public void setParentScope(Scriptable arg0) {
	}

	@Override
	public void setPrototype(Scriptable arg0) {
	}

	
}
