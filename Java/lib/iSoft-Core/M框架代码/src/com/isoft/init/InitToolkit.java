package com.isoft.init;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import com.isoft.biz.daoimpl.common.InitDAO;
import com.isoft.cache.CacheFactory;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;

public class InitToolkit {

	static {
		String separator = System.getProperty("file.separator");
		String rootPath = "E:/workspaces/iSoft-IaaS/WebRoot";
		String filePath = rootPath + separator + "WEB-INF" + separator
				+ CacheFactory.CACHE_CONFIG;
		CacheFactory.createCacheManager(filePath);
	}

	public static void main(String[] args) throws Exception {
		InitToolkit init = new InitToolkit();
		init.doInitSysFunc();
	}

	private Connection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://192.168.137.90:3306/wo_iaas?characterEncoding=utf8";
		String user = "root";
		String pswd = "root";
		return DriverManager.getConnection(url, user, pswd);
	}

	private void doInitSysFunc() throws Exception {
		Connection conn = null;
		try{
			conn = getConnection();
			conn.setAutoCommit(false);
			IdentityBean idBean = new IdentityBean();
			Map user = new HashMap();
			user.put("tenantId", "0");
			user.put("osTenantId", "8f25e51856ab4e2e97020976cfe22084");
			user.put("tenantRole", 2);
			user.put("userId", "500");
			user.put("userName", "root");
			idBean.init(user);
	
			SQLExecutor sqlExecutor = new SQLExecutor(conn, idBean);
			InitDAO idao = new InitDAO(sqlExecutor);
			idao.doInitSysFunc();
			conn.commit();
		} catch(Exception e){
			if(conn!=null){
				conn.rollback();
			}
			throw e;
		} finally {
			if(conn != null){
				conn.close();
			}
		}
	}

}
