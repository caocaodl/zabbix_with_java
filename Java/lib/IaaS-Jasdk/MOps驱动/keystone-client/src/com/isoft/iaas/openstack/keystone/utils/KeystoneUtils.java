package com.isoft.iaas.openstack.keystone.utils;

import java.util.List;

import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.Access.Service;

public class KeystoneUtils {

	public static String findEndpointURL(List<Service> serviceCatalog,
			String type, String region, Facing facing) {
		for (Service service : serviceCatalog) {
			if (type.equals(service.getType())) {
				for (Service.Endpoint endpoint : service.getEndpoints()) {
					if (region == null || region.equals(endpoint.getRegion())) {
						if (endpoint.getPublicURL() != null
								&& Facing.PUBLIC.equals(facing)) {
							return endpoint.getPublicURL();
						} else if (endpoint.getInternalURL() != null
								&& Facing.INTERNAL.equals(facing)) {
							return endpoint.getInternalURL();
						} else if (endpoint.getAdminURL() != null
								&& Facing.ADMIN.equals(facing)) {
							return endpoint.getAdminURL();
						}
					}
				}
			}
		}
		throw new RuntimeException("endpoint url not found");
	}

}
