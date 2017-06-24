package com.isoft.framework.common.interfaces;

import java.io.Serializable;

import com.isoft.biz.method.Role;

public interface IIdentityBean extends Serializable {
	String getTenantId();
	String getOsTenantId();
	String getUserId();
	String getUserName();
	Role getTenantRole();
}