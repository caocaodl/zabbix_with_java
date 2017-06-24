package com.isoft.ovirt;

import java.io.InputStream;
import java.util.Properties;

import org.ovirt.engine.sdk.Api;

public class OvirtUtil {
	
	private final static String URL;
	private final static String USERNAME;
	private final static String PASSWORD;
	private final static String KEYSTORE_PATH;
	private final static String KEYSTORE_PASSWORD;
	
	static {
		Properties configure = new Properties();
		try {
			InputStream is = OvirtUtil.class.getResourceAsStream("/ovirt.properties");
			configure.load(is);
			is.close();
			URL = configure.getProperty("ovirt.auth.url", "https://127.0.0.1:443/ovirt-engine/api");
			USERNAME = configure.getProperty("ovirt.auth.username", "admin@internal");
			PASSWORD = configure.getProperty("ovirt.auth.password", "password");
			KEYSTORE_PATH  = configure.getProperty("ovirt.auth.keystore.path", "jssecacerts");
			KEYSTORE_PASSWORD  = configure.getProperty("ovirt.auth.keystore.password", "changit");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public final static Api getOvirtClient() throws Exception {
		return new Api(URL, USERNAME, PASSWORD, true, false);
	}
	
	public final static Api getOvirtClientByKeyStore() throws Exception {
		return new Api(URL, USERNAME, PASSWORD, KEYSTORE_PATH, KEYSTORE_PASSWORD, false);
	}
	
	public static void main(String[] args) throws Exception{
		print(getOvirtClient());
		print(getOvirtClientByKeyStore());
	}
	
	private static void print(Api api) throws Exception {
		long start = System.currentTimeMillis();
		String o = api.getDataCenters().list().get(0).getDescription();
		System.out.println(o);
		System.out.println(System.currentTimeMillis() - start);
		api.close();
	}
}
