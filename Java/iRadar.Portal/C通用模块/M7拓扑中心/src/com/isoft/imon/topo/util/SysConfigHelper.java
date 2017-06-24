package com.isoft.imon.topo.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * 系统配置辅助
 * 
 * @author Administrator
 * 
 */
public class SysConfigHelper {
	public static String CONTEXT_PATH;
	private static Map<String, String> attributes = new HashMap<String, String>();

	public static void init(ServletContext sc) {
		CONTEXT_PATH = sc.getContextPath() + "/";

		String webRoot = sc.getRealPath("/");
		char lastChar = webRoot.charAt(webRoot.length()-1);
		if(lastChar!='/' && lastChar!='\\') {
			webRoot = webRoot + "/";
		}
		setAttributes("configPath", webRoot + "WEB-INF/config/");
	}

	public static void setAttributes(String key, String value) {
		attributes.put(key, value);
	}

	public static String getAttribute(String key) {
		return (String) attributes.get(key);
	}
}