package test.openstack.examples.compute;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.keystone.model.authentication.TokenAuthentication;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Image;
import com.isoft.iaas.openstack.nova.model.Images;

public class NovaListImages {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD)).execute();

		// use the token in the following requests
		keystone.token(access.getToken().getId());

		Tenants tenants = keystone.tenants().list().execute();

		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {

			access = keystone
					.tokens()
					.authenticate(
							new TokenAuthentication(access.getToken().getId()))
					.withTenantId(tenants.getList().get(0).getId()).execute();

			// NovaClient novaClient = new
			// NovaClient(KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
			// "compute", null, "public"), access.getToken().getId());
			Nova novaClient = new Nova(Configuration.NOVA_ENDPOINT.concat("/")
					.concat(tenants.getList().get(0).getId()));
			novaClient.token(access.getToken().getId());
			// novaClient.enableLogging(Logger.getLogger("nova"), 100 * 1024);

			Images images = novaClient.images().list(true).execute();
			for (Image image : images) {
				System.out.println(image);
			}

		} else {
			System.out.println("No tenants found!");
		}

	}

}
