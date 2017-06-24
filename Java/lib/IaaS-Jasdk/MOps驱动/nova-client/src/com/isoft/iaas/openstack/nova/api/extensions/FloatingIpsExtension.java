package com.isoft.iaas.openstack.nova.api.extensions;

import java.util.HashMap;
import java.util.Map;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.FloatingIp;
import com.isoft.iaas.openstack.nova.model.FloatingIps;

public class FloatingIpsExtension {

	private final OpenStackClient CLIENT;

	public FloatingIpsExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public class List extends OpenStackRequest<FloatingIps> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/os-floating-ips", null,
					FloatingIps.class);
		}

	}

	public class Allocate extends OpenStackRequest<FloatingIp> {

		public Allocate(Entity<?> entity) {
			super(CLIENT, HttpMethod.POST, "/os-floating-ips", entity,
					FloatingIp.class);
		}

	}

	public class Deallocate extends OpenStackRequest<Void> {

		public Deallocate(String id) {
			super(CLIENT, HttpMethod.DELETE, new StringBuffer(
					"/os-floating-ips/").append(id).toString(), null,
					Void.class);
		}

	}

	public List list() {
		return new List();
	}

	public Allocate allocate(String pool) {
		Entity<?> entity = null;
		if (pool != null) {
			Map<String, String> body = new HashMap<String, String>();
			body.put("pool", pool);
			entity = Entity.json(body);
		}
		return new Allocate(entity);
	}

	public Deallocate deallocate(String id) {
		return new Deallocate(id);
	}

}
