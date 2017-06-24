package test.openstack.examples.compute;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Flavors;
import com.isoft.iaas.openstack.nova.model.Images;
import com.isoft.iaas.openstack.nova.model.KeyPairs;
import com.isoft.iaas.openstack.nova.model.Server;
import com.isoft.iaas.openstack.nova.model.ServerForCreate;

public class NovaCreateServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		// access with unscoped token
		Access access = keystone
				.tokens()
				.authenticate()
				.withUsernamePassword(Configuration.KEYSTONE_USERNAME,
						Configuration.KEYSTONE_PASSWORD).execute();

		// use the token in the following requests
		keystone.token(access.getToken().getId());

		Tenants tenants = keystone.tenants().list().execute();

		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {

			access = keystone.tokens().authenticate()
					.withToken(access.getToken().getId())
					.withTenantId(tenants.getList().get(0).getId()).execute();

			// NovaClient novaClient = new
			// NovaClient(KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
			// "compute", null, "public"), access.getToken().getId());
			Nova nova = new Nova(Configuration.NOVA_ENDPOINT.concat(tenants
					.getList().get(0).getId()));
			nova.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));

			// novaClient.enableLogging(Logger.getLogger("nova"), 100 * 1024);
			// create a new keypair
			// KeyPair keyPair =
			// novaClient.execute(KeyPairsExtension.createKeyPair("mykeypair"));
			// System.out.println(keyPair.getPrivateKey());

			// create security group
			// SecurityGroup securityGroup =
			// novaClient.execute(SecurityGroupsExtension.createSecurityGroup("mysecuritygroup",
			// "description"));

			// novaClient.execute(SecurityGroupsExtension.createSecurityGroupRule(securityGroup.getId(),
			// "UDP", 9090, 9092, "0.0.0.0/0"));
			// novaClient.execute(SecurityGroupsExtension.createSecurityGroupRule(securityGroup.getId(),
			// "TCP", 8080, 8080, "0.0.0.0/0"));

			KeyPairs keysPairs = nova.keyPairs().list().execute();

			Images images = nova.images().list(true).execute();

			Flavors flavors = nova.flavors().list(true).execute();

			ServerForCreate serverForCreate = new ServerForCreate();
			serverForCreate.setName("woorea");
			serverForCreate.setFlavorRef(flavors.getList().get(0).getId());
			serverForCreate.setImageRef(images.getList().get(1).getId());
			serverForCreate.setKeyName(keysPairs.getList().get(0).getName());
			serverForCreate.getSecurityGroups().add(
					new ServerForCreate.SecurityGroup("default"));
			// serverForCreate.getSecurityGroups().add(new
			// ServerForCreate.SecurityGroup(securityGroup.getName()));

			Server server = nova.servers().boot(serverForCreate).execute();
			System.out.println(server);

		} else {
			System.out.println("No tenants found!");
		}

	}

}
