package com.isoft.iaas.openstack.quantum.api;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.quantum.model.Quota;
import com.isoft.iaas.openstack.quantum.model.Quotas;

public class QuotasResource {

	private final OpenStackClient CLIENT;

	public QuotasResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}
	public class List extends OpenStackRequest<Quotas> {
		public List() {
			super(CLIENT, HttpMethod.GET, "quotas", null, Quotas.class);
		}
	}
	
	public Show show(String tenantId) {
		return new Show(tenantId);
	}
	public class Show extends OpenStackRequest<Quota> {
		public Show(String tenantId) {
			super(CLIENT, HttpMethod.GET, "quotas/"+tenantId, null, Quota.class);
		}
	}

}
