package com.isoft.framework.persistlayer;

import java.util.List;
import java.util.Map;


public class DBCompatible {
	public final static boolean mysql;
	public final static boolean oracle;
	public final static boolean postgresql;
	public final static boolean db2;
	public final static boolean sqlite3;
	public final static boolean kingbase;

	static {
		mysql = true;
		oracle = false;
		postgresql = false;
		db2 = false;
		sqlite3 = false;
		kingbase = false;
	}

	// public String dbstr(Object var){
	// return "";
	// }

	public String dbcast_2bigint(String field) {
		if (mysql) {
			return " CAST(" + field + " AS UNSIGNED) ";
		} else if (postgresql) {
			return " CAST(" + field + " AS BIGINT) ";
		} else if (oracle) {
			return " CAST(" + field + " AS NUMBER(20)) ";
		} else if (db2) {
			return " CAST(" + field + " AS BIGINT) ";
		} else if (sqlite3) {
			return " CAST(" + field + " AS BIGINT) ";
		}
		throw new UnsupportedOperationException("dbcast_2bigint");
	}

	public String dblimit(Integer min, Integer max, Boolean afterWhere) {
		if (min == null) {
			min = 1;
		}
		if (afterWhere == null) {
			afterWhere = true;
		}
		if (mysql) {
			return !empty(max) ? "LIMIT " + min + "," + max
					: "LIMIT " + min;
		} else if (postgresql) {
			return !empty(max) ? "LIMIT " + min + "," + max
					: "LIMIT " + min;
		} else if (oracle) {
			if (afterWhere) {
				return !empty(max) ? " AND ROWNUM BETWEEN " + min
						+ " AND " + max : " AND ROWNUM <=" + min;
			} else {
				return !empty(max) ? " WHERE ROWNUM BETWEEN " + min
						+ " AND " + max : " WHERE ROWNUM <=" + min;
			}
		} else if (db2) {
			if (afterWhere) {
				return !empty(max) ? " AND ROWNUM BETWEEN " + min
						+ " AND " + max : " AND ROWNUM <=" + min;
			} else {
				return !empty(max) ? " WHERE ROWNUM BETWEEN " + min
						+ " AND " + max : " WHERE ROWNUM <=" + min;
			}
		} else if (sqlite3) {
			return !empty(max) ? "LIMIT " + min + "," + max
					: "LIMIT " + min;
		}
		throw new UnsupportedOperationException("limit");
	}

	public String dbmod(Object x, Object y) {
		if (sqlite3) {
			return " ((" + x + ") % (" + y + "))";
		} else {
			return " MOD(" + x + "," + y + ")";
		}
	}
	
	public static boolean empty(Object obj) {
		if (obj == null) {
			return true;
		} else if(obj instanceof String){
			return ((String) obj).length() == 0;
		} else if(obj instanceof List){
			return ((List) obj).isEmpty();
		} else if(obj instanceof Map){
			return ((Map) obj).isEmpty();
		} else if(obj instanceof Number){
			return obj.toString().replace('.', '0').replaceAll("0", "").length()==0;
		} else if (obj.getClass().isArray()) {
			return ((Object[]) obj).length == 0;
		} else {
			return true;
		}
	}
}
