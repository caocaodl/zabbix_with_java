package com.isoft.iradar.biz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.isoft.testcase.BaseTestCase;

public class TemplatesDAOTestCase extends BaseTestCase {
	
	public void testDoGetStatusList(){
		Map param = new HashMap();
		List list = new ArrayList();
//		list.add("1");
//		list.add("2");
//		param.put("hostIdList", list);
		
//		List dataList =  new TemplatesDAO(sqlExecutor).doGetStatusList(param);
//		for(int i=0; i<dataList.size(); i++){
//			System.out.println(dataList.get(i));
//		}
		
		list.add("1");
		list.add("2");
		param.put("hostIdList", list);
		param.put("statusType", "start_service");

		List dataList =  new TemplatesDAO(sqlExecutor).doUpdateStatus(param);
		for(int i=0; i<dataList.size(); i++){
			System.out.println(dataList.get(i));
		}
		
	}
	
}
