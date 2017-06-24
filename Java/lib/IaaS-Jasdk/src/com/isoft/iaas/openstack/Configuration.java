package com.isoft.iaas.openstack;

import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	public static final String KEYSTONE_USERNAME;

	public static final String KEYSTONE_PASSWORD;
	
	public static final String KEYSTONE_ADMIN_TOKEN;

	public static final String KEYSTONE_ADMIN_TENANT_NAME;

	public static final String KEYSTONE_AUTH_URL;
	
	public static final String REGION;

	private static Properties configure;
	static {
		configure = new Properties();
		try {
			InputStream is = Configuration.class.getResourceAsStream("/openstack.properties");
			configure.load(is);
			is.close();
			KEYSTONE_USERNAME = configure.getProperty("iaas.auth.username", "admin");
			KEYSTONE_PASSWORD = configure.getProperty("iaas.auth.password", "admin");
			KEYSTONE_ADMIN_TOKEN = configure.getProperty("iaas.identity.admin.token", "admin");
			KEYSTONE_ADMIN_TENANT_NAME  = configure.getProperty("iaas.identity.admin.tenantname", "admin");
			KEYSTONE_AUTH_URL  = configure.getProperty("iaas.identity.endpoint.auth.url", "http://192.168.137.150:5000/v2.0");
			REGION  = configure.getProperty("iaas.region", "RegionOne");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
