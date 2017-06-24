package com.isoft.common.db.impl;

import java.sql.Connection;

import com.isoft.common.db.DBEnquiry;

public class KingbaseES {
	private final static String SQL_ALIVE = "SELECT 1";//是否存活
	private final static String SQL_VERSION = "select version()";//版本号
//	private final static String SQL_PROCESS="SELECT DATNAME,PROCPID,CURRENT_QUERY FROM SYS_STAT_ACCTIVITY";//查看线程数
	private final static String SQL_PROCESS="select connections()";//查看当前线程
	private final static String SQL_SESSION="select count(*) from v$session";//查看所有会话数
	private final static String SQL_JOBS_RUNNING="SELECT count(*)  FROM DBA_JOBS_RUNNING";//查看作业数
	private final static String SQL_ACTVE="select count(*) from v$session where status='ACTIVE'";//并发连接数
//	private final static String SQL_RUNTIME="SELECT CURRENT_TIME";//运行时间
//	private final static String SQL_RUNTIME="SELECT CURRENT_DATE()";//运行时间
	private final static String SQL_RUNTIME="select (sysdate-startup_time)*24*3600 as running_time from v$instance";
	private final static String SQL_DATABASE="SELECT CURRENT_DATABASE()";//数据库名字
	
	
	public static int alive(Connection conn) {
		String r = DBEnquiry.ask(SQL_ALIVE, conn, false, false, 0);
		return r==null? 0: 1;
	}
	public static String version(Connection conn) {
		return DBEnquiry.ask(SQL_VERSION, conn, false, false, 0);
	}
//	public static String process(Connection conn) {
//		return DBEnquiry.ask(SQL_PROCESS, conn, false, false, 0);
//	}
	public static String session(Connection conn) {
		return DBEnquiry.ask(SQL_SESSION, conn, false, false, 0);
	}
	public static String jobs_running(Connection conn) {
		return DBEnquiry.ask(SQL_JOBS_RUNNING, conn, false, false, 0);
	}
	public static String active(Connection conn) {
		return DBEnquiry.ask(SQL_ACTVE, conn, false, false, 0);
	}
	public static String runtime(Connection conn) {
		return DBEnquiry.ask(SQL_RUNTIME, conn, false, false, 0);
	}
	public static String database(Connection conn) {
		return DBEnquiry.ask(SQL_DATABASE, conn, false, false, 0);
	}
}
