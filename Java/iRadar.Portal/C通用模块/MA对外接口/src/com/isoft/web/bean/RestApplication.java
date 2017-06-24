package com.isoft.web.bean;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

import com.isoft.Feature;

public class RestApplication extends ResourceConfig {
	public RestApplication() {
		// 服务类所在的包路径
		packages("com.isoft.web.bean.iface");
		
		if(Feature.debug) {
			// 打印访问日志，便于跟踪调试，正式发布可清除
			register(LoggingFilter.class);
		}
		
		register(JacksonJsonProvider.class);
	}
}