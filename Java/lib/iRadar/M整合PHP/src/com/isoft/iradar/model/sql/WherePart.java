package com.isoft.iradar.model.sql;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.model.params.CParamGet;

public class WherePart extends TenantPart {

	public WherePart(SqlBuilder sqlBuilder) {
		super(sqlBuilder);
	}

	public void dbConditionTenants(IIdentityBean idBean, String tableName,
			String tableAlias, CParamGet params) {
		doConditionTenants(idBean, tableName, tableAlias, params);
	}

	public void dbConditionTenants(IIdentityBean idBean, String tableName,
			String tableAlias) {
		doConditionTenants(idBean, tableName, tableAlias);
	}

	public void dbConditionTenants(IIdentityBean idBean) {
		doConditionTenants(idBean);
	}

}
