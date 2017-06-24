package com.isoft.iaas.openstack.nova;

import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackClientConnector;
import com.isoft.iaas.openstack.nova.api.ExtensionsResource;
import com.isoft.iaas.openstack.nova.api.FlavorsResource;
import com.isoft.iaas.openstack.nova.api.ImagesResource;
import com.isoft.iaas.openstack.nova.api.QuotaSetsResource;
import com.isoft.iaas.openstack.nova.api.ServersResource;
import com.isoft.iaas.openstack.nova.api.extensions.AggregatesExtension;
import com.isoft.iaas.openstack.nova.api.extensions.FloatingIpsExtension;
import com.isoft.iaas.openstack.nova.api.extensions.HostsExtension;
import com.isoft.iaas.openstack.nova.api.extensions.KeyPairsExtension;
import com.isoft.iaas.openstack.nova.api.extensions.NetworksExtension;
import com.isoft.iaas.openstack.nova.api.extensions.SecurityGroupsExtension;
import com.isoft.iaas.openstack.nova.api.extensions.SnapshotsExtension;
import com.isoft.iaas.openstack.nova.api.extensions.VolumesExtension;

public class Nova extends OpenStackClient {

	private final ExtensionsResource EXTENSIONS;

	private final ServersResource SERVERS;

	private final ImagesResource IMAGES;

	private final FlavorsResource FLAVORS;

	private final KeyPairsExtension KEY_PAIRS;

	private final FloatingIpsExtension FLOATING_IPS;

	private final SecurityGroupsExtension SECURITY_GROUPS;

	private final SnapshotsExtension SNAPSHOTS;

	private final VolumesExtension VOLUMES;

	private final AggregatesExtension AGGREGATES;

	private final QuotaSetsResource QUOTA_SETS;

	private final HostsExtension HOSTS;
	
	private final NetworksExtension NETWORKS;

	public Nova(String endpoint, OpenStackClientConnector connector) {
		super(endpoint, connector);
		EXTENSIONS = new ExtensionsResource(this);
		SERVERS = new ServersResource(this);
		IMAGES = new ImagesResource(this);
		FLAVORS = new FlavorsResource(this);
		KEY_PAIRS = new KeyPairsExtension(this);
		FLOATING_IPS = new FloatingIpsExtension(this);
		SECURITY_GROUPS = new SecurityGroupsExtension(this);
		SNAPSHOTS = new SnapshotsExtension(this);
		VOLUMES = new VolumesExtension(this);
		AGGREGATES = new AggregatesExtension(this);
		QUOTA_SETS = new QuotaSetsResource(this);
		HOSTS = new HostsExtension(this);
		NETWORKS = new NetworksExtension(this);
	}

	public Nova(String endpoint) {
		this(endpoint, null);
	}

	public ExtensionsResource extensions() {
		return EXTENSIONS;
	}

	public ServersResource servers() {
		return SERVERS;
	}

	public ImagesResource images() {
		return IMAGES;
	}

	public FlavorsResource flavors() {
		return FLAVORS;
	}

	public KeyPairsExtension keyPairs() {
		return KEY_PAIRS;
	}

	public FloatingIpsExtension floatingIps() {
		return FLOATING_IPS;
	}

	public SecurityGroupsExtension securityGroups() {
		return SECURITY_GROUPS;
	}

	public SnapshotsExtension snapshots() {
		return SNAPSHOTS;
	}

	public VolumesExtension volumes() {
		return VOLUMES;
	}

	public AggregatesExtension aggregates() {
		return AGGREGATES;
	}

	public QuotaSetsResource quotaSets() {
		return QUOTA_SETS;
	}

	public HostsExtension hosts() {
		return HOSTS;
	}
	
	public NetworksExtension networks() {
		return NETWORKS;
	}

}
