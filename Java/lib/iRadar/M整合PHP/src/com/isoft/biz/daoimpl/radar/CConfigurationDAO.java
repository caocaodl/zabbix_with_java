package com.isoft.biz.daoimpl.radar;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.params.CParamGet;

public class CConfigurationDAO extends CCoreLongKeyDAO<CParamGet> {

	public CConfigurationDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(idBean, executor, "", "", new String[]{});
	}

}
