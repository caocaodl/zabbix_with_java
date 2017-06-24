package com.isoft.testcase.dao;

import java.util.Map;

import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.api.API;
import com.isoft.iradar.model.params.CEventGet;
import com.isoft.testcase.BaseTestCase;
import com.isoft.types.CArray;

public class DAOTestCase extends BaseTestCase {
	
	public void testEvent(){
		CEventGet eget = new CEventGet();
		eget.setSource(EVENT_SOURCE_TRIGGERS);
		eget.setObject(EVENT_OBJECT_TRIGGER);
		eget.setOutput("eventid");
		eget.setSelectTriggers("priority");
		eget.setHostIds(10403L);
		
		IIdentityBean idBean = new IdentityBean();
		CArray<Map> events = API.Event(idBean, this.sqlExecutor).get(eget);
		
		System.out.println(events);
		
	}
	
}
