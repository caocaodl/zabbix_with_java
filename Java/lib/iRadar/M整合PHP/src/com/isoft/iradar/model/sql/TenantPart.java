package com.isoft.iradar.model.sql;

import com.isoft.biz.method.Role;
import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.model.params.CParamGet;

public class TenantPart extends SqlPart {

	public TenantPart(SqlBuilder sqlBuilder) {
		super(sqlBuilder);
	}
	
	private String fieldId(String fieldName, String tableAlias) {
		return tableAlias+"."+fieldName;
	}
	
	protected String doConditionTenants(IIdentityBean idBean, String tableName , String tableAlias, CParamGet params) {
		if (Role.isTenant(idBean.getTenantRole()) || params.getEditable()) {
			String fieldKey = fieldId("tenantid", tableName);
			String fieldId = fieldId("tenantid", tableAlias);
			return dbConditionStringAhead(fieldKey, fieldId, new String[] { idBean.getTenantId() });
		} else if (Role.isLessor(idBean.getTenantRole())) {
			return "TRUE";
		} else {
			return "FALSE";
		}
	}
	
	protected String doConditionTenants(IIdentityBean idBean, String tableName , String tableAlias) {
		if (Role.isTenant(idBean.getTenantRole())) {
			String fieldKey = fieldId("tenantid", tableName);
			String fieldId = fieldId("tenantid", tableAlias);
			return dbConditionStringAhead(fieldKey, fieldId, new String[] { idBean.getTenantId() });
		} else if (Role.isLessor(idBean.getTenantRole())) {
			return "TRUE";
		} else {
			return "FALSE";
		}
	}
	
	protected String doConditionTenants(IIdentityBean idBean) {
		if (Role.isTenant(idBean.getTenantRole())) {
			return dbConditionStringAhead("tenantid", "tenantid", new String[] { idBean.getTenantId() });
		} else if (Role.isLessor(idBean.getTenantRole())) {
			return "TRUE";
		} else {
			return "FALSE";
		}
	}

}
