package com.isoft.common.db.impl;

import java.sql.Connection;

import com.isoft.common.Constants;
import com.isoft.common.db.AbstractConfig;
import com.isoft.common.db.DBEnquiry;

public class Oracle extends AbstractConfig {
	
	private final static String SQL_ALIVE = "SELECT 1 FROM dual";
	private final static String SQL_UPTIME = "select to_char((sysdate-startup_time)*86400, 'FM99999999999999990') retvalue from v$instance";
	private final static String SQL_VERSION = "select banner from v$version where rownum = 1";
	private final static String SESSION_ACTIVE = "select count(*) from v$session where TYPE!='BACKGROUND' and status='ACTIVE'";
	private final static String SESSION = "select count(*) from v$session";
	private final static String USERCONN = "select count(*) from v$session";
	
	
	@Override
	public String call(Connection conn, String cmd) {
		return this.call(conn, cmd, Constants.ORACLE.toLowerCase(), "", this.getClass());
	}
	
	public static String alive(Connection conn) {
		String r = DBEnquiry.ask(SQL_ALIVE, conn, false, false, 0);
		return r==null? "0": "1";
	}
	
	public static String uptime(Connection conn) {
		return DBEnquiry.ask(SQL_UPTIME, conn, false, false, 1);
	}
	
	public static String dbversion(Connection conn) {
		return DBEnquiry.ask(SQL_VERSION, conn, false, false, 1);
	}
	
	public static String session_active(Connection conn) {
		return DBEnquiry.ask(SESSION_ACTIVE, conn, false, false, 1);
	}
	
	public static String session(Connection conn) {
		return DBEnquiry.ask(SESSION, conn, false, false, 1);
	}
	
	public static String userconn(Connection conn) {
		return DBEnquiry.ask(USERCONN, conn, false, false, 1);
	}
	
}
