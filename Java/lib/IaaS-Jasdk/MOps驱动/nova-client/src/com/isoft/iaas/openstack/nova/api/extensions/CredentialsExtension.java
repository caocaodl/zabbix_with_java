package com.isoft.iaas.openstack.nova.api.extensions;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.nova.model.Certificate;

public class CredentialsExtension {

	private final OpenStackClient CLIENT;

	public CredentialsExtension(OpenStackClient client) {
		CLIENT = client;
	}

	public Create createCertificate(String id) {
		return new Create(id);
	}

	public Show showCertificate(String id) {
		return new Show();
	}

	public class Create extends OpenStackRequest<Certificate> {

		public Create(String id) {
			super(CLIENT, HttpMethod.GET, new StringBuffer("/os-certificates")
					.append(id).toString(), null, Certificate.class);
		}

	}

	public class Show extends OpenStackRequest<Certificate> {

		public Show() {
			super(CLIENT, HttpMethod.GET, "/os-certificates", null,
					Certificate.class);
		}

	}

}
