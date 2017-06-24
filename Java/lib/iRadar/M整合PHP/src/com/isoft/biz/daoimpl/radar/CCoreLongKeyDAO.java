package com.isoft.biz.daoimpl.radar;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.params.CParamGet;

public class CCoreLongKeyDAO<P extends CParamGet> extends CCoreDAO<P, Long> {

	public CCoreLongKeyDAO(IIdentityBean idBean, SQLExecutor executor,
			String tableName, String tableAlias, String[] sortColumns) {
		super(idBean, executor, tableName, tableAlias, sortColumns);
	}
	
}
