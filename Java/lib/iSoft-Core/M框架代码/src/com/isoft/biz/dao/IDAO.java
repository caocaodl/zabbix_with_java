package com.isoft.biz.dao;

import com.isoft.biz.dao.NameSpaceEnum;

import com.isoft.framework.persistlayer.SQLExecutor;

public interface IDAO {
	SQLExecutor getSqlExecutor();
	public String getFlowcode(NameSpaceEnum nameSpace);
}