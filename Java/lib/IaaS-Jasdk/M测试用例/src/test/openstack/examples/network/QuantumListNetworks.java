package test.openstack.examples.network;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.authentication.TokenAuthentication;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.iaas.openstack.quantum.Quantum;
import com.isoft.iaas.openstack.quantum.model.Network;
import com.isoft.iaas.openstack.quantum.model.Networks;

public class QuantumListNetworks {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		// access with unscoped token
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD)).execute();
		// use the token in the following requests
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));

		Tenants tenants = keystone.tenants().list().execute();
		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {
			// access with tenant
			access = keystone
					.tokens()
					.authenticate(
							new TokenAuthentication(access.getToken().getId()))
					.withTenantId(tenants.getList().get(0).getId()).execute();

			Quantum quantum = new Quantum(KeystoneUtils.findEndpointURL(
					access.getServiceCatalog(), "network", null, Facing.PUBLIC));
			quantum.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));

			Networks networks = quantum.networks().list().execute();
			for (Network network : networks) {
				System.out.println(network);
			}
		} else {
			System.out.println("No tenants found!");
		}
	}
}
