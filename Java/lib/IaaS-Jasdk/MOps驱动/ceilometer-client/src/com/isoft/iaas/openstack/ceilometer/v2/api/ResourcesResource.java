package com.isoft.iaas.openstack.ceilometer.v2.api;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.ceilometer.v2.model.Resource;

public class ResourcesResource {

	private final OpenStackClient CLIENT;

	public ResourcesResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}
	
	public class List extends OpenStackRequest<Resource[]> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/resources",
					null, Resource[].class);
		}

	}
	
	public Show show(String id) {
		return new Show(id);
	}
	
	public class Show extends OpenStackRequest<Resource> {

		public Show(String id) {
			super(CLIENT, HttpMethod.GET, 
					new StringBuilder("/resources/").append(id),
					null, Resource.class);
		}

	}

}
