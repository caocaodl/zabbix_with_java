package com.isoft.common.db.impl;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.isoft.common.Constants;
import com.isoft.common.QueryCache;
import com.isoft.common.db.AbstractConfig;
import com.isoft.common.db.DBEnquiry;
import com.isoft.common.db.IConfig;

public class MSSql extends AbstractConfig {

	private final static String IS_ALIVE = "SELECT 1";
	private final static String LOG_USED_PERCENT = "SELECT cntr_value FROM master.sys.dm_os_performance_counters WHERE counter_name='Percent Log Used' AND instance_name='_Total'";
	private final static String MEMORY = "SELECT SUM(page_size_in_bytes*max_pages_allocated_count) FROM master.sys.dm_os_memory_objects";
	private final static String CPU = "SELECT round(100.0*@@CPU_BUSY/(@@CPU_BUSY+@@IDLE), 2)";
	private final static String SESSION = "SELECT COUNT(*) FROM master.sys.dm_exec_sessions";
	private final static String AVGWAITTIME = "SELECT (MAX(CAST(cntr_value as float))/MIN(CAST(cntr_value as float))) FROM sys.dm_os_performance_counters WHERE (counter_name='Average Wait Time (ms)' OR counter_name='Average Wait Time Base') AND instance_name='_Total'";
	private final static String DBSIZE = "SELECT cntr_value FROM master.sys.dm_os_performance_counters WHERE counter_name='Data File(s) Size (KB)' AND instance_name='_Total'";
	
	@Override
	public String call(Connection conn, String cmd) {
		String result = "";
		try {
			result = DBEnquiry.execute(QueryCache.getQuery(Constants.MSSQL.toLowerCase(), cmd), conn, "");
			if("".equals(result)){
				Method m = this.getClass().getMethod(cmd, new Class[] {Connection.class});
				result = String.valueOf(m.invoke(null, conn));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String is_alive(Connection conn) {
		return DBEnquiry.ask(IS_ALIVE, conn, false, false, 1);
	}
	
	public static String log_used_percent(Connection conn) {
		return DBEnquiry.ask(LOG_USED_PERCENT, conn, false, false, 1);
	}
	
	public static String memory(Connection conn) {
		return DBEnquiry.ask(MEMORY, conn, false, false, 1);
	}
	
	public static String cpu(Connection conn) {
		return DBEnquiry.ask(CPU, conn, false, false, 1);
	}
	
	public static String session(Connection conn) {
		return DBEnquiry.ask(SESSION, conn, false, false, 1);
	}
	
	public static String avgwaittime(Connection conn) {
		return DBEnquiry.ask(AVGWAITTIME, conn, false, false, 1);
	}
	
	public static String dbsize(Connection conn) {
		return DBEnquiry.ask(DBSIZE, conn, false, false, 1);
	}
	
}
