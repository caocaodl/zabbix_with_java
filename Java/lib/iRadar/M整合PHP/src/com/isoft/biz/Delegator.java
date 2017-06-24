package com.isoft.biz;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;

public abstract class Delegator<T> {

	public abstract T doDelegate(IIdentityBean idBean, SQLExecutor executor) throws Exception;
	
}
