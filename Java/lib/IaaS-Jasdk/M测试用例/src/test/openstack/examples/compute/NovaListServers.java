package test.openstack.examples.compute;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.Servers;

public class NovaListServers {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName("admin").execute();

		// use the token in the following requests
		keystone.token(access.getToken().getId());

		// NovaClient novaClient = new
		// NovaClient(KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
		// "compute", null, "public"), access.getToken().getId());
		Nova novaClient = new Nova(Configuration.NOVA_ENDPOINT.concat("/")
				.concat(access.getToken().getTenant().getId()));
		novaClient.token(access.getToken().getId());
		// novaClient.enableLogging(Logger.getLogger("nova"), 100 * 1024);

		Servers servers = novaClient.servers().list(true).execute();
		for (Server server : servers) {
			System.out.println(server);
		}

	}

}
