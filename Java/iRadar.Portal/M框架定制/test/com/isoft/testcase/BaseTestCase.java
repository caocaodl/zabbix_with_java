package com.isoft.testcase;

import java.sql.Connection;
import java.util.Map;

import junit.framework.TestCase;

import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.cache.CacheFactory;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.model.CWebUser;
import com.isoft.server.RunParams;
import com.isoft.utils.DataSourceUtil;
import com.isoft.web.listener.DataSourceEnum;

public class BaseTestCase extends TestCase implements Defines {
	
	protected SQLExecutor sqlExecutor;

	@Override
	protected void setUp() throws Exception {
		RunParams.DEBUG = true;
        String filePath = "./WebRoot/WEB-INF/ehcache.xml";
        CacheFactory.createCacheManager(filePath);
        
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://192.168.30.81:3306/iradar";
		String user = "iradar";
		String password = "iradar";
		DataSourceEnum[] dataSources = DataSourceEnum.values();
		ProxoolDataSource ds  = new ProxoolDataSource();
		ds.setDriver(driver);
		ds.setDriverUrl(url);
		ds.setAlias("iradar");
		ds.setUser(user);
		ds.setPassword(password);
		ds.setMinimumConnectionCount(2);
		ds.setMaximumConnectionCount(5);

		for (DataSourceEnum dsItem : dataSources) {
			DataSourceUtil.setDataSource(dsItem.getDsName(), ds);
		}
		
		Connection conn = DataSourceUtil.getDefaultDataSource().getConnection();
		IIdentityBean idBean = new IdentityBean();
		this.sqlExecutor = new SQLExecutor(conn , idBean );
		
		if(RadarContext.getContext() == null) {
    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
    		RadarContext.setContext(ctx);
    		
    		Map userdata = EasyMap.build(
				"userid", 0L, 
				"type", Defines.USER_TYPE_SUPER_ADMIN
			);
    		CWebUser.set(userdata);
    	}
	}

	@Override
	protected void tearDown() throws Exception {
	}

}
