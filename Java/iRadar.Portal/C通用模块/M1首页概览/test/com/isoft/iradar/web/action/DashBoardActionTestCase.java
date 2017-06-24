package com.isoft.iradar.web.action;

import static com.isoft.iradar.Cphp.empty;
import static com.isoft.types.CArray.map;

import java.sql.Connection;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.springframework.mock.web.MockHttpServletRequest;

import com.isoft.Feature;
import com.isoft.biz.method.Role;
import com.isoft.cache.CacheFactory;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.RadarContext;
import com.isoft.iradar.common.util.IRadarContext;
import com.isoft.iradar.core.utils.EasyMap;
import com.isoft.iradar.inc.BlocksUtil;
import com.isoft.iradar.inc.Defines;
import com.isoft.iradar.managers.CFavorite;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CDiv;
import com.isoft.server.RunParams;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
import com.isoft.utils.DataSourceUtil;
import com.isoft.web.listener.DataSourceEnum;

public class DashBoardActionTestCase {
	
	protected static SQLExecutor sqlExecutor;
	protected static IdentityBean lessorIdBean;
	
	@Test
	public void make_favorite_maps() {
		IdentityBean  idBean = lessorIdBean;
		String idx = "web.favorite.sysmapids";
		CDiv result;
		
		//获取原值，准备后面恢复
		CArray<Map> origin_fav_sysmaps = CFavorite.get(idBean, sqlExecutor, idx);
		
		//测试为空状态
		//--准备数据
		CFavorite.remove(idBean, sqlExecutor, idx);
		//--执行单元测试代码
		result = BlocksUtil.make_favorite_maps(idBean, sqlExecutor);
		//--比较测试结果
		Assert.assertEquals(result.toString(), "<div></div>");
		
		
		//测试有值状态
		//--准备数据
		CFavorite.add(idBean, sqlExecutor, idx, 99L);
		CFavorite.add(idBean, sqlExecutor, idx, 100L);
		//--执行单元测试代码
		result = BlocksUtil.make_favorite_maps(idBean, sqlExecutor);
		//--比较测试结果
		Assert.assertEquals(result.toString(), "" +
			"<div align=\"center\">" +
			"<img alt=\"image\" name=\"image\" src=\"topochart.action?topoId=99\" border=\"0\"/>" +
			"<img alt=\"image\" name=\"image\" src=\"topochart.action?topoId=100\" border=\"0\"/>" +
			"</div>"
		);
		
		
		//还原数据
		CFavorite.remove(idBean, sqlExecutor, idx);
		if (!empty(origin_fav_sysmaps)) {
			for(Map fav: origin_fav_sysmaps) {
				Long v = Nest.value(fav, "value").asLong();
				CFavorite.add(idBean, sqlExecutor, idx, v);
			}
		}
	}
	
	@BeforeClass
	public static void init() throws Exception {
		Feature.idsUseConnection = false;
		RunParams.DEBUG = true;
        String filePath = "./WebRoot/WEB-INF/ehcache.xml";
        CacheFactory.createCacheManager(filePath);
        
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://172.31.2.53:3306/iradar";
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
		lessorIdBean = new IdentityBean();
		lessorIdBean.init(map(
				"tenantId", "5e4d0a6d39a44b9c906a3173b448aa4a",
				"osTenantId", "0",
				"tenantRole", Role.LESSOR.magic(),
				"userId",  "1",
				"userName", "platform_idbean_username",
				"admin", "Y",
				"osUser", null
			));
		DashBoardActionTestCase.sqlExecutor = new SQLExecutor(conn , lessorIdBean );
		
		if(RadarContext.getContext() == null) {
    		RadarContext ctx = new RadarContext(new MockHttpServletRequest(), null);
    		RadarContext.setContext(ctx);
    		
    		Map userdata = EasyMap.build(
				"userid", lessorIdBean.getUserId(), 
				"type", Defines.USER_TYPE_SUPER_ADMIN
			);
    		CWebUser.set(userdata);
    	}
	}
	
}
