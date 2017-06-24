package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Metadata;
import com.isoft.iaas.openstack.nova.model.Snapshot;
import com.isoft.iaas.openstack.nova.model.SnapshotForCreate;
import com.isoft.iaas.openstack.nova.model.Snapshots;

public class SnapshotsExtension {

	private final OpenStackClient CLIENT;

	public SnapshotsExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public List list(boolean detail) {
		return new List(detail);
	}

	public Create create(SnapshotForCreate snapshotForCreate) {
		return new Create(snapshotForCreate);
	}

	public Show show(String id) {
		return new Show(id);
	}

	public ShowMetadata showMetadata(String id) {
		return new ShowMetadata(id);
	}

	public Delete delete(String id) {
		return new Delete(id);
	}

	public class List extends OpenStackRequest<Snapshots> {

		public List(boolean detail) {
			super(CLIENT, HttpMethod.GET, detail ? "/os-snapshots/detail"
					: "/os-snapshots", null, Snapshots.class);
		}

	}

	public class Create extends OpenStackRequest<SnapshotForCreate> {

		public Create(SnapshotForCreate snapshotForCreate) {
			super(CLIENT, HttpMethod.POST, "/os-snapshots", Entity
					.json(snapshotForCreate), SnapshotForCreate.class);
		}

	}

	public class Show extends OpenStackRequest<Snapshot> {

		public Show(String id) {
			super(CLIENT, HttpMethod.GET, new StringBuilder("/os-snapshots/")
					.append(id).toString(), null, Snapshot.class);
		}

	}

	public class ShowMetadata extends OpenStackRequest<Metadata> {

		public ShowMetadata(String id) {
			super(CLIENT, HttpMethod.GET, new StringBuilder("/os-snapshots/")
					.append(id).append("/metadata").toString(), null,
					Metadata.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(CLIENT, HttpMethod.DELETE,
					new StringBuilder("/os-snapshots/").append(id).toString(),
					null, Void.class);
		}

	}

}
