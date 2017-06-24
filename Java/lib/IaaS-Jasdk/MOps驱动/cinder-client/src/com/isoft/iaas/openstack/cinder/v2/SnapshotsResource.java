package com.isoft.iaas.openstack.cinder.v2;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.cinder.v2.model.Volume;
import com.isoft.iaas.openstack.cinder.v2.model.VolumeForCreate;
import com.isoft.iaas.openstack.cinder.v2.model.Volumes;

public class SnapshotsResource {

	private final OpenStackClient CLIENT;

	public SnapshotsResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list(boolean detail) {
		return new List(detail);
	}

	public Create create(VolumeForCreate volume) {
		return new Create(volume);
	}

	public Show show(String id) {
		return new Show(id);
	}

	public Update update(String id, Volume image) {
		return new Update(id, image);
	}

	public Delete delete(String id) {
		return new Delete(id);
	}

	public class List extends OpenStackRequest<Volumes> {

		public List(boolean detail) {
			super(CLIENT, HttpMethod.GET, detail ? "/volumes/detail" : "/volumes",
					null, Volumes.class);
		}

	}

	public class Create extends OpenStackRequest<Volume> {

		public Create(VolumeForCreate volume) {
			super(CLIENT, HttpMethod.POST, "/volumes", 
					Entity.json(volume),	Volume.class);
		}

	}

	public class Update extends OpenStackRequest<Volume> {

		public Update(String id, Volume volume) {
			super(CLIENT, HttpMethod.PUT, new StringBuilder("/volumes/").append(
					id).toString(), Entity.json(volume), Volume.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(CLIENT, HttpMethod.DELETE, new StringBuilder("/volumes/")
					.append(id).toString(), null, Void.class);
		}

	}

	public class Show extends OpenStackRequest<Volume> {
		
		private List list;
		private String id;

		public Show(String id) {
			this.id = id;
			this.list = new List(true);
		}

		@Override
		public Volume execute() {
			java.util.List<Volume> volumes = this.list.execute().getList();
			for (Volume v : volumes) {
				if (this.id.equals(v.getId()) || this.id.equals(v.getName())) {
					return v;
				}
			}
			return null;
		}

	}

}
