package test.openstack.examples.keystone;

import java.util.List;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.api.ServicesResource;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Endpoint;
import com.isoft.iaas.openstack.keystone.model.Endpoints;
import com.isoft.iaas.openstack.keystone.model.Service;
import com.isoft.iaas.openstack.keystone.model.Services;
import com.isoft.iaas.openstack.keystone.model.Tenant;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.Users;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;

public class KeystoneCreateTenant {

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

//		//System.exit(0);
//		
//		List<Access.Service> ass = access.getServiceCatalog();
//		for (Access.Service as : ass) {
//			System.out.println(as);
//		}
//
		keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		keystone.token(access.getToken().getId());
//		ServicesResource servicesResource = keystone.services();
//		Services services = servicesResource.list().execute();
//		List<Service> ss = services.getList();
//		for (Service s : ss) {
//			System.out.println(s);
//		}
//
//		Endpoints endpoints = keystone.endpoints().list().execute();
//		List<Endpoint> eps = endpoints.getList();
//		for (Endpoint ep : eps) {
//			System.out.println(ep);
//		}

		keystone.tenants().list().execute();
//
//		keystone.roles().list().execute();
//
		Users usersEcho = keystone.users().list().execute();
		List<User> users = usersEcho.getList();
		for (User user : users) {
			System.out.println(user);
		}

//		Tenant tenant = new Tenant();
//		tenant.setName("benn.cs");
//		tenant.setDescription("benn.cs");
//		tenant.setEnabled(true);

	}
}
