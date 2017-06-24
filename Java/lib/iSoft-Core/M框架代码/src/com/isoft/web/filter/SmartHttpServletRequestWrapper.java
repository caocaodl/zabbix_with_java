package com.isoft.web.filter;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

public class SmartHttpServletRequestWrapper extends HttpServletRequestWrapper {

	public SmartHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		return transformXss(super.getParameter(name));
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return transformXss(super.getParameterMap());
	}

	@Override
	public String[] getParameterValues(String name) {
		return transformXss(super.getParameterValues(name));
	}

	private String transformXss(String content) {
		if (content != null && content.length() > 0) {
			content = StringUtils.trim(content);
			content = content.replaceAll("&amp;","&");
			content = content.replaceAll("&lt;","<");
			content = content.replaceAll("&gt;",">");
			content = content.replaceAll("&#039;","'");
			content = content.replaceAll("&quot;","\"");
			
			content = content.replaceAll("&", "&amp;");
			content = content.replaceAll("<", "&lt;");
			content = content.replaceAll(">", "&gt;");
			content = content.replaceAll("'", "&#039;");
			content = content.replaceAll("\"", "&quot;");
			return content;
		} else {
			return content;
		}
	}
	
	private String[] transformXss(String[] contents) {
		if(contents!=null && contents.length>0){
			for(int i=0;i<contents.length;i++){
				contents[i] = transformXss(contents[i]);
			}
		}
		return contents;
	}
	
	private Map<String, String[]> transformXss(Map<String, String[]> contents) {
		if(contents!=null && !contents.isEmpty()){
			Set<String> keys = contents.keySet();
			for(String key:keys){
				String[] values = contents.get(key);
				transformXss(values);
			}
		}
		return contents;
	}
}