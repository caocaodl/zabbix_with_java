package com.isoft.iradar.virtualresource;

import java.util.Collection;

import com.isoft.iaas.openstack.IaaSClient;

public interface ResourceCollector {
	
	public Collection<VirtualResource> collect(IaaSClient client, ResourceType type);
	
}
