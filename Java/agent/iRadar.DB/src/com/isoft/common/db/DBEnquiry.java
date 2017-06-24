package com.isoft.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;

public class DBEnquiry {
	
	public static String NA = "--";
	
	public static String ask(String _query, Connection _con, boolean trim, boolean space, Integer... _excludeColumns) {
		return ask(_query, _con, trim, space, Arrays.asList(_excludeColumns));
	}
	public static String ask(String _query, Connection _con, boolean trim, boolean space, List<Integer> _excludeColumnList) {
		String tempStr = "";
		try {
			ResultSet rs = null;
//			conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY)
			PreparedStatement p_stmt = _con.prepareStatement(_query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = p_stmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			while (rs.next()) {
				for (int r = 1; r < numColumns + 1; r++) {
					Integer tmpInteger = new Integer(r);
					if (!_excludeColumnList.contains(tmpInteger)) {
						if (trim) {
							Object v = rs.getObject(r);
							tempStr = tempStr + (v!=null?v.toString().trim():NA);
						} else {
							tempStr = tempStr + rs.getObject(r).toString();
						}
						if (space && (r < numColumns)) {
							tempStr = tempStr + ' ';
						}
					}
				}
				if (space && !rs.isLast()) {
					tempStr = tempStr + " | ";
				}
			}
			
			try {
				if (rs != null)
					rs.close();
			} catch (Exception ex) {
				if(Deamon.DEBUG) {
					Deamon.debug("Error on DBEnquiry while closing resultset " + ex.getMessage());
				}
			}
		} catch (Exception ex) {
			if(Deamon.DEBUG) {
				Deamon.debug("Error while executing " + "- Exception received " + ex.getMessage());
			}
			tempStr = null;
		}
		return tempStr;
	}

	
	public static String execute(Query _query, Connection _conn, String dbname) {
		if (_query == null) {
			return "";
		}
		
		String tempStr = new String("");
		// check if is the right time to execute the statements
		try {
			if (_query.getActive()) {
				/*
				 * execute RaceConditionQuery
				 */
				boolean racecond = true;
				String result = "";
				if (_query.getRaceQuery() != null) {
					if (_query.getRaceQuery().length() > 0) {
						Deamon.debug("INFO:" + _query.getName() + " RaceCondiftionQuery ->" + _query.getRaceQuery());
						result = ask(_query.getRaceQuery(), _conn, _query.getTrim(), _query.getSpace(), _query.getRaceExcludeColumnsList());
						if (result != null) {
							if (_query.getRaceValue() != null) {
								if (!result.equalsIgnoreCase(_query.getRaceValue())) {
									racecond = false;
								}
							}
						}
					}
				}
				result = "";
				if (racecond) {
					result = ask(_query.getSQL().toString(), _conn, _query.getTrim(), _query.getSpace(), _query.getExcludeColumnsList());
					if (result == null) {
						if (_query.getNoData().length() > 0 && _query.getNoData() != null) {
							result = _query.getNoData();
						}
					} else if (result.length() == 0) {
						if (_query.getNoData().length() > 0 && _query.getNoData() != null) {
							result = _query.getNoData();
						}
					}
					Deamon.debug("I'm going to return " + result + " for query " + _query.getName() + " on database=" + dbname);
				}
				tempStr = result;
			}
		} catch (Exception ex) {
			Deamon.debug("Error on DBEnquiry on query=" + _query.getName() + " on database=" + dbname + " Error returned is " + ex);
			if (_query.getNoData() != null) {
				if (_query.getNoData().length() > 0)
					tempStr = _query.getNoData();
			} else {
				tempStr = "";
			}
			Deamon.debug("I'm going to return " + tempStr + " for query " + _query.getName() + " on database=" + dbname);
		}
		
		return tempStr;
	}
}
