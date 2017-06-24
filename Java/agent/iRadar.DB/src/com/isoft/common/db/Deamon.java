package com.isoft.common.db;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import com.isoft.common.db.DBType.dbtype;


public class Deamon {
	public final static boolean DEBUG = false;
	static String DB_IP = "127.0.0.1";
	
	static dbtype DBTYPE = null; 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * oracle
		 * version:11gr2
		 */
//		args = new String[]{"oracle.tbl_use_rate","system","abc123","192.168.39.105","1521"};
		/**
		 * mssql
		 * version:2005
		 */
//		args = new String[]{"mssql.logsize","sa","abc123","192.168.39.13","1433"};
		/**
		 * db2
		 * version:
		 */
//		args = new String[]{"db2.alive","db2inst1","abcd1234","192.168.39.105","50000"};
		
		if(args.length >= 3) {
			String cmd = args[0];
			String user = args[1];
			String password = args[2];
			
			String type = "mysql";
			if(cmd.contains(".")) {
				String[] dbCmd = cmd.split("\\.");
				type = dbCmd[0];
				cmd = dbCmd[1];
			}
			DBTYPE = dbtype.valueOf(type);
			
			
			if(args.length >= 4) {
				DB_IP = args[3];
			}
			int dbPort = args.length>=5? Integer.valueOf(args[4]): DBTYPE.defaultPort();
			
			call(dbPort, user, password, cmd);
			return;
		}
		showUsage();
	}
	
	private static void call(int dbPort, String user, String password, String cmd) {
		Connection conn = null;
		String driverClass = DBTYPE.driverClass();
		try {
			try {
				Driver driver = (Driver)java.lang.Class.forName(driverClass).newInstance();
				if(driver != null) {
					java.util.Properties info = new java.util.Properties();
			        if (user != null) {
			            info.put("user", user);
			        }
			        if (password != null) {
			            info.put("password", password);
			        }
			        conn = driver.connect(DBTYPE.url(DB_IP, dbPort, ""), info);
				}
			}catch(Exception e) {
				if(Deamon.DEBUG) {
					Deamon.debug(e);
				}
			}
			
			if(conn!=null){
				Class cmdClass = DBTYPE.cmdClz();
				Object o;
				if(IConfig.class.isAssignableFrom(cmdClass)) {
					IConfig cfg = (IConfig)cmdClass.newInstance();
					o = cfg.call(conn, cmd);
				}else {
					Method m = cmdClass.getMethod(cmd, new Class[] {Connection.class});
					o = m.invoke(null, conn);
				}
				
				if(o != null) {
					System.out.println(o);
				}
			}
		}catch (Exception e) {
			if(Deamon.DEBUG) {
				e.printStackTrace();
			}
		}finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
				}
				conn = null;
			}
		}
	}
	
	private static void showUsage() {
		System.out.println("java -jar db.jar <cmd> <user> <password> [<db_ip> <db_port>]");
	}
	
	public static void debug(Object o) {
		if(!DEBUG) return;
		System.out.println(o);
	}
}