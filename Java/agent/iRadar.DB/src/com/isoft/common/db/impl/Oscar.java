package com.isoft.common.db.impl;

import java.sql.Connection;

import com.isoft.common.Constants;
import com.isoft.common.db.DBEnquiry;
import com.isoft.common.db.IConfig;

public class Oscar{
//	private final static String SQL_01 = "SELECT * FROM INFO_SCHEM.V_SYS_SETTINGS";//版本号已废弃
	private final static String SQL_ALIVE = "SELECT 1";//是否活着
	private final static String SQL_VERSION = "SELECT VERSION FROM INFO_SCHEM.V$INSTANCE";//版本号
	private final static String SQL_SESSION = "SELECT COUNT(*) AS SESSION FROM INFO_SCHEM.V_SYS_SESSIONS";//会话数
	private final static String SQL_RUNTIME = "SELECT SYSDATE-STARTUP_TIME AS RUNTIME FROM INFO_SCHEM.V$INSTANCE";//运行时间
	private final static String SQL_HIT_RATIOFROM = "SELECT 1 - SUM(DECODE(NAME, 'PHYSICAL READS', VALUE, 0)) /(SUM(DECODE(NAME, 'DB BLOCK GETS', VALUE, 0))+SUM(DECODE(NAME, 'CONSISTENT GETS', VALUE, 0))) HIT_RATIO FROM INFO_SCHEM.V$SYSSTAT WHERE NAME IN ('PHYSICAL READS', 'DB BLOCK GETS', 'CONSISTENT GETS')";//高速缓冲区命中率
	private final static String SQL_PROCESS = "SELECT COUNT(*) FROM INFO_SCHEM.V$PROCESS";//查询数据库当前连接数
	private final static String SQL_TABLESPACE = "SELECT TABLESPACE_NAME FROM INFO_SCHEM.DBA_TABLESPACES";//表空间列表
//	public String call(Connection conn, String cmd) {
//		return this.call(conn, cmd, Constants.OSCAR.toLowerCase(), "", this.getClass());
//	}
	public static int alive(Connection conn) {
		String r = DBEnquiry.ask(SQL_ALIVE, conn, false, false, 0);
		return r==null? 0: 1;
	}
	public static String version(Connection conn) {
		return DBEnquiry.ask(SQL_VERSION, conn, false, false, 1);
	}
	public static String session(Connection conn) {
		return DBEnquiry.ask(SQL_SESSION, conn, false, false, 1);
	}
	public static String runtime(Connection conn) {
		return DBEnquiry.ask(SQL_RUNTIME, conn, false, false, 1);
	}
	public static String hit_ratiofrom(Connection conn) {
		return DBEnquiry.ask(SQL_HIT_RATIOFROM, conn, false, false, 1);
	}
	public static String process(Connection conn) {
		return DBEnquiry.ask(SQL_PROCESS, conn, false, false, 1);
	}
	public static String tablespace(Connection conn) {
		return DBEnquiry.ask(SQL_TABLESPACE, conn, false, false, 1);
	}
}
