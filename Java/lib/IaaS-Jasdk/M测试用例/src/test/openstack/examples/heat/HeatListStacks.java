package test.openstack.examples.heat;

import java.util.Collections;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.heat.Heat;
import com.isoft.iaas.openstack.heat.model.CreateStackParam;
import com.isoft.iaas.openstack.heat.model.Stack;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.utils.KeystoneTokenProvider;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;

public class HeatListStacks {

	private static String TEMPLATE = "{\n"
			+ "    \"HeatTemplateFormatVersion\": \"2012-12-12\",\n"
			+ "    \"Parameters\": {},\n" + "    \"Mappings\": {},\n"
			+ "    \"Resources\": {\n" + "        \"my-test-server\": {\n"
			+ "            \"Type\": \"OS::Nova::Server\",\n"
			+ "            \"Properties\": {\n"
			+ "                \"flavor\": \"m1.small\",\n"
			+ "                \"image\": \"centos:latest\"\n"
			+ "            }\n" + "        }\n" + "    }\n" + "}";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException {
		KeystoneTokenProvider keystone = new KeystoneTokenProvider(
				Configuration.KEYSTONE_ENDPOINT,
				Configuration.KEYSTONE_USERNAME,
				Configuration.KEYSTONE_PASSWORD);

		Access access = keystone.getAccessByTenant(Configuration.TENANT_NAME);

		String endpointURL = KeystoneUtils.findEndpointURL(
				access.getServiceCatalog(), "orchestration", null, Facing.PUBLIC);

		Heat heat = new Heat(endpointURL);
		heat.setTokenProvider(keystone
				.getProviderByTenant(Configuration.TENANT_NAME));

		CreateStackParam param = new CreateStackParam();
		param.setStackName("helloWorld");
		param.setTimeoutMinutes(1);
		param.setParameters(Collections.<String, String> emptyMap());
		param.setTemplate(TEMPLATE);

		System.out
				.printf("Create: " + heat.getStacks().create(param).execute());
		Thread.sleep(3000);

		for (Stack s : heat.getStacks().list().execute()) {
			System.out.println(s.getDescription());
			System.out.println(s.getId());
			System.out.println(s.getStackName());
			System.out.println(s.getStackStatus());
			System.out.println(s.getCreationTime());
			System.out.println(s.getUpdatedTime());
			System.out.println(s.getLinks());

			System.out.println(heat.getStacks().byName(s.getStackName())
					.execute());

		}
	}
}
