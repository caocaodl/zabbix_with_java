package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Network;
import com.isoft.iaas.openstack.nova.model.NetworkForCreate;
import com.isoft.iaas.openstack.nova.model.Networks;

public class NetworksExtension {

	private final OpenStackClient CLIENT;

	public NetworksExtension(OpenStackClient client) {
		CLIENT = client;
	}
	
	public Create create(NetworkForCreate network) {
		return new Create(network);
	}

	public List list() {
		return new List();
	}

	public Show show(String id) {
		return new Show(id);
	}

	public Delete delete(String id) {
		return new Delete(id);
	}
	
	public Associate associate(String networkId) {
		return new Associate(networkId);
	}

	public Disassociate disassociate(String networkId) {
		return new Disassociate(networkId);
	}

	public class List extends OpenStackRequest<Networks> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/os-networks", null, Networks.class);
		}

	}

	public class Create extends OpenStackRequest<Network> {

		public Create(NetworkForCreate network) {
			super(CLIENT, HttpMethod.POST, "/os-networks",
					Entity.json(network), Network.class);
		}

	}

	public class Show extends OpenStackRequest<Network> {

		public Show(String id) {
			super(CLIENT, HttpMethod.GET, new StringBuilder("/os-networks/")
					.append(id).toString(), null, Network.class);
		}

	}
	
	public class Associate extends OpenStackRequest<Void> {

		public Associate(String networkId) {
			super(CLIENT, HttpMethod.POST, new StringBuilder("/os-networks/add").toString(), 
					Entity.json("{\"id\":\""+networkId+"\"}"), Void.class);
		}

	}

	public class Disassociate extends OpenStackRequest<Void> {

		public Disassociate(String networkId) {
			super(CLIENT, HttpMethod.POST, new StringBuilder("/os-networks/")
					.append(networkId).append("/action").toString(), Entity
					.json("{\"disassociate_project\":null}"), Void.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String id) {
			super(CLIENT, HttpMethod.DELETE, new StringBuilder("/os-networks/")
					.append(id).toString(), null, Void.class);
		}

	}

}
