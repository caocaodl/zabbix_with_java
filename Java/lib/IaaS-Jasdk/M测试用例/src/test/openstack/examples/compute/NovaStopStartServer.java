package test.openstack.examples.compute;

import java.util.List;
import java.util.Map;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.Entity;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.Access.Service;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.iaas.openstack.nova.Nova;
import com.isoft.iaas.openstack.nova.model.Network;
import com.isoft.iaas.openstack.nova.model.NetworkForCreate;
import com.isoft.iaas.openstack.nova.model.QuotaSet;
import com.isoft.iaas.openstack.nova.model.ServerAction.LiveMigration;
import com.isoft.iaas.openstack.nova.model.Servers;
import com.isoft.iaas.openstack.nova.model.SimpleTenantUsage;

public class NovaStopStartServer {
	public static void main(String[] args) throws InterruptedException {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName(Configuration.TENANT_NAME).execute();

		List<Service> serviceCatalogs = access.getServiceCatalog();
		String endpoint = KeystoneUtils.findEndpointURL(serviceCatalogs, "compute", "RegionOne", Facing.PUBLIC);
		System.out.println(endpoint);
		
		// use the token in the following requests
		keystone.token(access.getToken().getId());

		Nova novaClient = new Nova(endpoint);
		novaClient.token(access.getToken().getId());

		//novaClient.servers().showMetadata("b85ae9a7-ff3e-43e9-9471-4436659d8675").execute();
//		Map ma = novaClient.servers().diagnostics("b85ae9a7-ff3e-43e9-9471-4436659d8675").execute();
//		System.out.println(ma);
		
		novaClient.volumes().list(true).execute();
		
		
	}
}
