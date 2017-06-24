package com.isoft.iaas.openstack.ceilometer;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackClientConnector;
import com.isoft.iaas.openstack.ceilometer.v2.api.AlarmsResource;
import com.isoft.iaas.openstack.ceilometer.v2.api.MetersResource;
import com.isoft.iaas.openstack.ceilometer.v2.api.ResourcesResource;

public class Ceilometer extends OpenStackClient {

	private final MetersResource METERS;

	private final ResourcesResource RESOURCES;
	
	private final AlarmsResource ALARMS;

	public Ceilometer(String endpoint, OpenStackClientConnector connector) {
		super(endpoint+"/v2", connector);
		METERS = new MetersResource(this);
		RESOURCES = new ResourcesResource(this);
		ALARMS = new AlarmsResource(this);
	}

	public Ceilometer(String endpoint) {
		this(endpoint, null);
	}

	public ResourcesResource resources() {
		return RESOURCES;
	}

	public MetersResource meters() {
		return METERS;
	}
	
	public AlarmsResource alarms() {
		return ALARMS;
	}

}
