package com.isoft.iaas.openstack;

import com.isoft.framework.common.interfaces.IIdentityBean;

public class OpsUtils {

	protected OpsUtils() {
	}
	
	public static IaaSClient getOpenStackClient(String tenantId) {
		return getOpenStackClient(Configuration.REGION, tenantId);
	}
	
	public static IaaSClient getOpenStackClient(String region, String tenantId) {
		IaaSClient osClient = new IaaSClient(region);
		osClient.loginWithTenantId(tenantId, Configuration.KEYSTONE_USERNAME, Configuration.KEYSTONE_PASSWORD);
		return osClient;
	}
	
	public static IaaSClient getOpenStackClientForAdmin(){
		return getOpenStackClientForAdmin(Configuration.REGION);
	}
	
	public static IaaSClient getOpenStackClientForAdmin(String region){
		IaaSClient osClient = new IaaSClient(region);
		osClient.loginWithTenantName(Configuration.KEYSTONE_ADMIN_TENANT_NAME, Configuration.KEYSTONE_USERNAME, Configuration.KEYSTONE_PASSWORD);
		return osClient;
	}
	
	public static IaaSClient getOpenStackClient(IIdentityBean identityBean) {
		return getOpenStackClient(Configuration.REGION, identityBean.getOsTenantId());
	}

}
