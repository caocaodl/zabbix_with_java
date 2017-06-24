package com.isoft.iaas.openstack.cinder.v2;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackClientConnector;

public class Cinder extends OpenStackClient {

	private final VolumesResource VOLUMES;
	private final TypesResource TYPES;
	private final SnapshotsResource SNAPSHOTS;
	private final QosResource QOS;
	private final QosExtensionsResource QOSEXTENSIONS;
	private final LimitsResource LIMITS;

	public Cinder(String endpoint, OpenStackClientConnector connector) {
		super(endpoint, connector);
		VOLUMES = new VolumesResource(this);
		TYPES = new TypesResource(this);
		SNAPSHOTS = new SnapshotsResource(this);
		QOS = new QosResource(this);
		QOSEXTENSIONS = new QosExtensionsResource(this);
		LIMITS = new LimitsResource(this);
	}

	public Cinder(String endpoint) {
		this(endpoint, null);
	}

	public final VolumesResource volumes() {
		return VOLUMES;
	}

	public TypesResource types() {
		return TYPES;
	}

	public SnapshotsResource snapshots() {
		return SNAPSHOTS;
	}

	public QosResource qos() {
		return QOS;
	}

	public QosExtensionsResource qosExtensions() {
		return QOSEXTENSIONS;
	}

	public LimitsResource limits() {
		return LIMITS;
	}

}
