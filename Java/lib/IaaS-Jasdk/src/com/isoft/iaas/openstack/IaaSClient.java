package com.isoft.iaas.openstack;

import com.isoft.iaas.openstack.base.client.OpenStackResponseException;
import com.isoft.iaas.openstack.ceilometer.Ceilometer;
import com.isoft.iaas.openstack.glance.Glance;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.quantum.Quantum;

public class IaaSClient {

	private String region;
	private Keystone keystone;
	private Access access;

	protected IaaSClient(String region) {
		this.region = region;
	}

	public boolean loginWithTenantName(String tenantName, String username, String password) {
		this.keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		try {
			this.access = keystone.tokens().authenticate(new UsernamePassword(username, password)).withTenantName(tenantName).execute();
			keystone.token(access.getToken().getId());
			return true;
		} catch (OpenStackResponseException e) {
			this.keystone = null;
			this.access = null;
			return false;
		}
	}

	public boolean loginWithTenantId(String tenantId, String username, String password) {
		this.keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		try {
			this.access = keystone.tokens().authenticate(new UsernamePassword(username, password)).withTenantId(tenantId).execute();
			keystone.token(access.getToken().getId());
			return true;
		} catch (OpenStackResponseException e) {
			this.keystone = null;
			this.access = null;
			return false;
		}
	}

	public String getRegion() {
		return this.region;
	}

	public Access getAccess() {
		return this.access;
	}

	public Keystone getIdentityClient() {
		return this.keystone;
	}

	public Nova getComputeClient() {
		String endpoint = KeystoneUtils.findEndpointURL(this.access.getServiceCatalog(), "compute", this.region, Facing.ADMIN);
		Nova novaClient = new Nova(endpoint);
		novaClient.token(access.getToken().getId());
		return novaClient;
	}

	public Glance getImageClient() {
		String endpoint = KeystoneUtils.findEndpointURL(this.access.getServiceCatalog(), "image", this.region, Facing.ADMIN);
		Glance glanceClient = new Glance(endpoint);
		glanceClient.token(access.getToken().getId());
		return glanceClient;
	}

	public Quantum getNetworkClient() {
		String endpoint = KeystoneUtils.findEndpointURL(this.access.getServiceCatalog(), "network", this.region, Facing.ADMIN);
		Quantum networkClient = new Quantum(endpoint + "/v2.0");
		networkClient.token(access.getToken().getId());
		return networkClient;
	}

	public Ceilometer getTelemetryClient() {
		String endpoint = KeystoneUtils.findEndpointURL(this.access.getServiceCatalog(), "metering", this.region, Facing.ADMIN);
		Ceilometer geilometerClient = new Ceilometer(endpoint);
		geilometerClient.token(access.getToken().getId());
		return geilometerClient;
	}

}
