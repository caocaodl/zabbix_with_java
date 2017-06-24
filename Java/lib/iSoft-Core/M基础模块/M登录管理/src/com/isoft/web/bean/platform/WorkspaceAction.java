package com.isoft.web.bean.platform;

import com.isoft.biz.method.Role;
import com.isoft.web.common.IaasPageAction;

public class WorkspaceAction extends IaasPageAction {

	@Override
	public String execute() throws Exception {
		Role role = getIdentityBean().getTenantRole();
		if(Role.isLessor(role)){
			setAttribute("role", "lessor");
		} else {
			setAttribute("role", "tenant");
		}
		String funcId = getParameter("funcId");
		if (funcId != null && funcId.length() > 0) {
			return "funcNav";
		}
		return SUCCESS;
	}

	public String doLeftMenu(){
		return SUCCESS;
	}
}
