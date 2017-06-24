package com.isoft.web.bean.iface;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.junit.Test;

public class EventDataInterfaceActionTestCase extends NorthInterfaceTest{
	
	String subURI = "/eventData/5e4d0a6d39a44b9c906a3173b448aa4a/10226";
	
	@Test
	public void getMonitorData(){
		setTarget(subURI);
		
		setHeader("startTime","2014-10-18 15:30:00");
		setHeader("endTime","2016-10-18 15:30:00");
		setHeader("itemNumber","10");
		setHeader("eventLevel","1");
		setResponse();
		Object value = getValue(String.class);
		System.out.println(value);
		List<Map> valueList = JSONArray.toList(JSONArray.fromObject(value), Map.class);
		for(Map map:valueList){
			System.out.println(map.get("details"));
			System.out.println(map.get("hostip"));
		}
	}
	
}
