package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.KeyPair;
import com.isoft.iaas.openstack.nova.model.KeyPairs;

public class KeyPairsExtension {

	private final OpenStackClient CLIENT;

	public KeyPairsExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}

	public Create create(String name, String publicKey) {
		KeyPair keyPairForCreate = new KeyPair(name, publicKey);
		return new Create(keyPairForCreate);
	}

	public Create create(String name) {
		return create(name, null);
	}

	public Delete delete(String name) {
		return new Delete(name);
	}

	public class Create extends OpenStackRequest<KeyPair> {

		public Create(KeyPair keyPairForCreate) {
			super(CLIENT, HttpMethod.POST, "/os-keypairs", Entity
					.json(keyPairForCreate), KeyPair.class);
		}

	}

	public class Delete extends OpenStackRequest<Void> {

		public Delete(String name) {
			super(CLIENT, HttpMethod.DELETE, new StringBuilder("/os-keypairs/")
					.append(name).toString(), null, Void.class);
		}

	}

	public class List extends OpenStackRequest<KeyPairs> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/os-keypairs", null, KeyPairs.class);
		}

	}

}
