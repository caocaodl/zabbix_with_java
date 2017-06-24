package test.openstack.examples.hpcloud;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;

public class KeystoneAuthentication {

	private static final String KEYSTONE_AUTH_URL = "https://region-a.geo-1.identity.hpcloudsvc.com:35357/v2.0";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(KEYSTONE_AUTH_URL);

		// access with unscoped token
		Access access = keystone
				.tokens()
				.authenticate()
				.withUsernamePassword(Configuration.KEYSTONE_USERNAME,
						Configuration.KEYSTONE_PASSWORD).execute();

		System.out.println(access);

	}

}
