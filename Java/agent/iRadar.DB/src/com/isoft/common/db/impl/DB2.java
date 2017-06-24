package com.isoft.common.db.impl;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.isoft.common.Constants;
import com.isoft.common.QueryCache;
import com.isoft.common.db.AbstractConfig;
import com.isoft.common.db.DBEnquiry;
import com.isoft.common.db.IConfig;

public class DB2 extends AbstractConfig {

	private final static String SQL_ALIVE = "SELECT 1";
	private final static String AVEWAITFORLOCK = "select (ap.lock_wait_time/ap.lock_waits) as \"Avg Wait(ms)\" from sysibmadm.snapappl_info ai,sysibmadm.snapappl ap where ai.agent_id=ap.agent_id and ap.lock_waits>0 ";
	private final static String SQL_VERSION = "select VERSIONNUMBER from SYSIBM.SYSVERSIONS";
	private final static String TOTALLOGSPAVAIL = "select ( 1.0*TOTAL_LOG_AVAILABLE/1024/1024) from TABLE(SNAP_GET_DB(CAST (NULL AS VARCHAR(128)), -2))";
	private final static String APPLCOMMITSATTEMPTED = "select sum(total_app_commits) from sysibmadm.MON_CONNECTION_SUMMARY";
	
	@Override
	public String call(Connection conn, String cmd) {
		String result = "";
		try {
			result = DBEnquiry.execute(QueryCache.getQuery(Constants.DB2.toLowerCase(), cmd), conn, "");
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
	
	public static String alive(Connection conn) {
		String r = DBEnquiry.ask(SQL_ALIVE, conn, false, false, 0);
		return r==null? "0": "1";
	}
	
	public static String AveWaitForLock(Connection conn) {
		return DBEnquiry.ask(AVEWAITFORLOCK, conn, false, false, 1);
	}
	
	public static String dbversion(Connection conn) {
		return DBEnquiry.ask(SQL_VERSION, conn, false, false, 1);
	}
	
	public static String TotalLogSpAvail(Connection conn) {
		return DBEnquiry.ask(TOTALLOGSPAVAIL, conn, false, false, 1);
	}
	
	public static String ApplCommitsAttempted(Connection conn) {
		return DBEnquiry.ask(APPLCOMMITSATTEMPTED, conn, false, false, 1);
	}
	
}
