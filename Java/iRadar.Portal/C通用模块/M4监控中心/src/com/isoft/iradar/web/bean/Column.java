package com.isoft.iradar.web.bean;

import com.isoft.types.CArray;

public class Column{
	private final static String SPLIT = " / ";
	private String name;
	private Key[] keys;
	private String format;
	private CArray attrs;
	
	public static Column column(String name, Key... keys) {
		return column(name, null, null, keys);
	}
	public static Column column(String name, String format, Key... keys) {
		return column(name, null, format, keys);
	}
	public static Column column(String name, CArray attrs, Key... keys) {
		return column(name, attrs, null, keys);
	}
	public static Column column(String name, CArray attrs, String format, Key... keys) {
		Column c = new Column();
		c.name = name;
		c.keys = keys;
		c.format = format;
		c.attrs = attrs;
		return c;
	}

	public Key[] keys() {
		return keys;
	}
	public String header() {
		return name;
	}
	public CArray attrs() {
		return attrs;
	}
	public Object cell(Long hostid) {
		int len = keys.length; 
		if(len == 0) return "";
		if(len == 1) return keys[0].show(hostid);
		
		if(format == null) {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<len; i++) {
				sb.append(keys[i].show(hostid));
				if(i+1 < len) {
					sb.append(SPLIT);
				}
			}
			return sb.toString();
		}else {
			Object[] args = new Object[len];
			for(int i=0; i<len; i++) {
				args[i] = keys[i].show(hostid);
			}
			return String.format(format, args);
		}
	}
	public String getName() {
		return name;
	}
}
