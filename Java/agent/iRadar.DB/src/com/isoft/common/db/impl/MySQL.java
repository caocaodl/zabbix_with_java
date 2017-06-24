package com.isoft.common.db.impl;

import java.sql.Connection;

import com.isoft.common.db.DBEnquiry;

public class MySQL {
	
	private final static String SQL_ALIVE = "SELECT 1";
	private final static String SQL_UPTIME = "SHOW GLOBAL STATUS WHERE Variable_name = 'Uptime'";
	private final static String SQL_DATABASES = "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA";
	private final static String SQL_QUESTIONS = "SHOW GLOBAL STATUS WHERE Variable_name = 'Questions'";
	private final static String SQL_QCACHE_TOTAL = "show variables where Variable_name = 'query_cache_size'";
	private final static String SQL_QCACHE_FREE = "SHOW GLOBAL STATUS WHERE Variable_name = 'Qcache_free_memory'";
	private final static String SQL_VERSION = "SHOW VARIABLES LIKE 'version'";
	private final static String SQL_THREADS_CONNECTED = "SHOW GLOBAL STATUS WHERE Variable_name = 'Threads_connected'";
	private final static String SQL_THREADS_RUNNING = "SHOW GLOBAL STATUS WHERE Variable_name = 'Threads_running'";
	
	
	//并发线程数
	public static String threadsRunning(Connection conn) {
		return DBEnquiry.ask(SQL_THREADS_RUNNING, conn, false, false, 1);
	}
	
	//数据库版本
	public static String version(Connection conn) {
		return DBEnquiry.ask(SQL_VERSION, conn, false, false, 1);
	}
	
	//查询缓存可用量
	public static String qcacheFree(Connection conn) {
		return DBEnquiry.ask(SQL_QCACHE_FREE, conn, false, false, 1);
	}
	
	//查询缓存总量
	public static String qcacheTotal(Connection conn) {
		return DBEnquiry.ask(SQL_QCACHE_TOTAL, conn, false, false, 1);
	}
	
	//每秒查询量
	public static String questions(Connection conn) {
		return DBEnquiry.ask(SQL_QUESTIONS, conn, false, false, 1);
	}
	
	//系统会话数
	public static String threadsConnected(Connection conn) {
		return DBEnquiry.ask(SQL_THREADS_CONNECTED, conn, false, false, 1);
	}
	
	//表空间列表
	public static String databases(Connection conn) {
		return DBEnquiry.ask(SQL_DATABASES, conn, false, true, 0);
	}
	
	//运行状态
	public static int alive(Connection conn) {
		String r = DBEnquiry.ask(SQL_ALIVE, conn, false, false, 0);
		return r==null? 0: 1;
	}
	
	//运行时间
	public static String uptime(Connection conn) {
		return DBEnquiry.ask(SQL_UPTIME, conn, false, false, 1);
	}
}
