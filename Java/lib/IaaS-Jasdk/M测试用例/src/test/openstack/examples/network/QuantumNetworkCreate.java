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
import com.isoft.iaas.openstack.quantum.model.NetworkForCreate;
import com.isoft.iaas.openstack.quantum.model.Networks;
import com.isoft.iaas.openstack.quantum.model.Router;
import com.isoft.iaas.openstack.quantum.model.RouterForAddInterface;
import com.isoft.iaas.openstack.quantum.model.RouterForCreate;
import com.isoft.iaas.openstack.quantum.model.Subnet;
import com.isoft.iaas.openstack.quantum.model.SubnetForCreate;

public class QuantumNetworkCreate {

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
		keystone.token(access.getToken().getId());
		Tenants tenants = keystone.tenants().list().execute();
		// try to exchange token using the first tenant

		if (tenants.getList().size() > 0) {
			// access with tenant
			Network network = new Network();
			access = keystone
					.tokens()
					.authenticate(
							new TokenAuthentication(access.getToken().getId()))
					.withTenantId("tenantId").execute();
			Quantum quantum = new Quantum(KeystoneUtils.findEndpointURL(
					access.getServiceCatalog(), "network", null, Facing.PUBLIC));
			quantum.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));
			NetworkForCreate netcreate = new NetworkForCreate();
			netcreate.setTenantId("tenantId");
			netcreate.setName("net2");
			netcreate.setAdminStateUp(true);

			network = quantum.networks().create(netcreate).execute();

			// Creating Subnet
			try {
				Subnet sub = new Subnet();
				SubnetForCreate subnet = new SubnetForCreate();
				subnet.setCidr("");
				subnet.setName("");
				subnet.setNetworkId(network.getId());
				subnet.setIpVersion(4);
				sub = quantum.subnets().create(subnet).execute();
				RouterForCreate routerForCreate = new RouterForCreate();
				routerForCreate.setName("routerName");
				routerForCreate.setTenantId("tenantId");
				Router router = quantum.routers().create(routerForCreate)
						.execute();
				RouterForAddInterface routerForAdd = new RouterForAddInterface();
				routerForAdd.setSubnetId(sub.getId());
				routerForAdd.setRouterId(router.getId());
				quantum.routers().addInterface(routerForAdd).execute();

				// System.out.println(sub);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

			Networks networks = quantum.networks().list().execute();

			for (Network network1 : networks) {
				System.out.println(network1);
			}
		} else {
			System.out.println("No tenants found!");
		}

	}
}
