package com.isoft.iradar.utils;

import java.util.Map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

public class CJs {
	
	public static final ObjectMapper DEFAULT_MAPPER;
	
	static {
		DeserializationConfig desConfig = null;
		SerializationConfig serConfig = null;
		
//		String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
//		SimpleDateFormat dateFmt = new SimpleDateFormat(DATE_FORMAT);
//		dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		DEFAULT_MAPPER = new ObjectMapper();
		DEFAULT_MAPPER.setSerializationInclusion(Inclusion.NON_NULL);
//		DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		DEFAULT_MAPPER.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		desConfig = DEFAULT_MAPPER.getDeserializationConfig();
//		desConfig = desConfig.withDateFormat(dateFmt);
		DEFAULT_MAPPER.setDeserializationConfig(desConfig);
		serConfig = DEFAULT_MAPPER.getSerializationConfig();
//		serConfig = serConfig.withDateFormat(dateFmt);
		DEFAULT_MAPPER.setSerializationConfig(serConfig);
	}

	public static String encodeJson(Object o, boolean ig) {
		return encodeJson(o);
	}
	
	public static String encodeJson(Object o) {
		try {
			return CJs.DEFAULT_MAPPER.writeValueAsString(o);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static Map decodeJson(String s) {
		s = s.replaceAll("&amp;","&");
		s = s.replaceAll("&lt;","<");
		s = s.replaceAll("&gt;",">");
		s = s.replaceAll("&#039;","'");
		s = s.replaceAll("&quot;","\"");
		
		try {
			Map m = DEFAULT_MAPPER.readValue(s, Map.class);
			return m;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
