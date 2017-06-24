package test.openstack.examples;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Tenant;

public class Configuration {

	public static final String KEYSTONE_AUTH_URL = "http://192.168.39.129:35357/v2.0";

	public static final String KEYSTONE_USERNAME = "admin";

	public static final String KEYSTONE_PASSWORD = "admin";

	public static final String KEYSTONE_ENDPOINT = "http://192.168.39.129:35357/v2.0";

	public static final String TENANT_NAME = "admin";

	public static final String NOVA_ENDPOINT = "http://192.168.137.150:8774/v2";

	public static final String CEILOMETER_ENDPOINT = "";

}
