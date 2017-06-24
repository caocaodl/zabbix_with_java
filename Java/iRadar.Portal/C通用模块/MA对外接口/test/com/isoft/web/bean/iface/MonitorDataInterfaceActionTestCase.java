package com.isoft.web.bean.iface;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.collections.map.LinkedMap;
import org.junit.Test;

public class MonitorDataInterfaceActionTestCase extends NorthInterfaceTest{
	
	String subURI = "/monitorData/5e4d0a6d39a44b9c906a3173b448aa4a/192.168.30.69";
	
	@Test
	public void getMonitorData() throws MalformedURLException {
		setTarget(subURI);
		Map params = new LinkedMap();
		params.put("startTime","2014-10-18 15:30:00");
		params.put("endTime","2016-10-18 15:30:00");
		params.put("itemNumber","10");
		params.put("templatename","Server_Windows");
		setParams(params);
		setResponse();
		Object value = getValue(String.class);
		List<Map> valueList = JSONArray.toList(JSONArray.fromObject(value), Map.class);
		for(Map map:valueList){
			System.out.println(map.get("itemName"));
			System.out.println(map.get("hostIp"));
		}
	}
	
}
