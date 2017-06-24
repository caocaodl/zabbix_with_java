package com.isoft.web;

import com.isoft.biz.Delegator;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.web.common.DelegateAction;

public class CDelegator {

	public static <T> T doDelegate(IIdentityBean idBean, Delegator<T> delegator) {
		DelegateAction action = new DelegateAction(idBean);
		try {
			return action.doDelegate(delegator);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
