package com.isoft.iaas.openstack.base.client;

public interface OpenStackTokenProvider {

	String getToken();

	void expireToken();

}
