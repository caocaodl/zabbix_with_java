package test.openstack.examples.hpcloud;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.OpenStackResponse;
import com.isoft.iaas.openstack.keystone.v3.Keystone;
import com.isoft.iaas.openstack.keystone.v3.model.Authentication;
import com.isoft.iaas.openstack.keystone.v3.model.Authentication.Identity;
import com.isoft.iaas.openstack.keystone.v3.model.Token;

public class Keystone3Authentication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);

		Authentication auth = new Authentication();
		auth.setIdentity(Identity.password(Configuration.KEYSTONE_USERNAME,
				Configuration.KEYSTONE_PASSWORD));

		OpenStackResponse response = keystone.tokens().authenticate(auth)
				.request();

		String tokenId = response.header("X-Subject-Token");

		Token token = response.getEntity(Token.class);

		System.out.println(tokenId);

		System.out.println(token);

	}

}
