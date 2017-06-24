package test.openstack.examples.keystone;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.User;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;

public class KeystoneCreateUser {

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
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName("admin").execute();

		User user = new User();
		user.setEmail("luis@woorea.es");
		user.setUsername("luis.gervaso");
		user.setPassword("password.0");
		user.setName("Luis");
		user.setEnabled(Boolean.TRUE);

		keystone = new Keystone("http://keystone.x.org/v2.0");
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));
		// keystone.enableLogging(Logger.getLogger("keystone"), 10000);
		user = keystone.users().create(user).execute();
		System.out.println(user);
		keystone.users().delete(user.getId()).execute();
	}
}
