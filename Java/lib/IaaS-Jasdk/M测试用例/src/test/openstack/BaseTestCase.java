package test.openstack;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;

import junit.framework.TestCase;

public class BaseTestCase extends TestCase {

	protected Access access;
	
	@Override
	protected void setUp() throws Exception {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		this.access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName(Configuration.TENANT_NAME).execute();
	}

	@Override
	protected void tearDown() throws Exception {
	}

}
