package com.isoft.common.db;

import java.lang.reflect.Method;
import java.sql.Connection;

import com.isoft.common.QueryCache;

public abstract class AbstractConfig implements IConfig{
	
	public static String call(Connection conn, String cmd,String dbType,String tableName,Class clazz){
		String result = "";
		try {
			result = DBEnquiry.execute(QueryCache.getQuery(dbType, cmd), conn, tableName);
			if("".equals(result)||result==null){
				Method m = clazz.getMethod(cmd, new Class[] {Connection.class});
				result = String.valueOf(m.invoke(null, conn));
			}
		} catch (Exception e) {
			result = "";
		}finally{
			return result;
		}
	}
	
}
