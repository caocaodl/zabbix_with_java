package com.isoft.iradar.model.sql;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.model.params.CParamGet;

public class DualPart extends TenantPart {

	public DualPart(SqlBuilder sqlBuilder) {
		super(sqlBuilder);
	}
	
	public String dbConditionTenants(IIdentityBean idBean, String tableName,
			String tableAlias, CParamGet params) {
		return super.doConditionTenants(idBean, tableName, tableAlias, params);
	}

	public String dbConditionTenants(IIdentityBean idBean, String tableName,
			String tableAlias) {
		return super.doConditionTenants(idBean, tableName, tableAlias);
	}

	public String dbConditionTenants(IIdentityBean idBean) {
		return super.doConditionTenants(idBean);
	}
	
}
