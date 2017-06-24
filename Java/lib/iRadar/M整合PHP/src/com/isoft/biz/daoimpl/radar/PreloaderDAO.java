package com.isoft.biz.daoimpl.radar;

import com.isoft.biz.dao.radar.IPreloaderDAO;
import com.isoft.biz.daoimpl.BaseDAO;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;

public class PreloaderDAO extends BaseDAO implements IPreloaderDAO {

	public PreloaderDAO(IIdentityBean idBean, SQLExecutor executor) {
		super(executor);
	}
	
}
