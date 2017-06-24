package test.openstack.examples.blockstore;

import java.util.List;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.cinder.v2.Cinder;
import com.isoft.iaas.openstack.cinder.v2.model.Type;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.Access.Service;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.test.Mapper;

public class CinderExample {
	public static void main(String[] args) throws InterruptedException {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName(Configuration.TENANT_NAME).execute();

		List<Service> serviceCatalogs = access.getServiceCatalog();
		String endpoint = KeystoneUtils.findEndpointURL(serviceCatalogs, "volumev2", "RegionOne", Facing.PUBLIC);
		System.out.println(endpoint);
		
		// use the token in the following requests
		keystone.token(access.getToken().getId());

		Cinder cinderClient = new Cinder(endpoint);
		cinderClient.token(access.getToken().getId());


		//		List<Volume> volumes = cinderClient.volumes().list(false).execute().getList();
//		
//		
//		
//		Volume v = cinderClient.volumes().show(volumes.get(0).getId()).execute();
//		
//		Mapper.f(v);
//		
//		//cinderClient.volumes().delete(v.getId()).execute();
		
//		VolumeForCreate volume = new VolumeForCreate();
//		volume.setName("t5");
//		volume.setSize(1);
//		Volume s = cinderClient.volumes().create(volume).execute();
//		Mapper.f(s);
		
//		Volume v = cinderClient.volumes().show("t11").execute();
//		v.setName("t11-1");
//		v.setDescription("hello");
//		v.getMetadata().put("age1", "11");
		
		//v=cinderClient.volumes().create(v).execute();
		//Mapper.f(v);
		
		List<Type> types = cinderClient.types().list().execute().getList();
		for (Type t : types) {
			Mapper.f(t);
		}
	}
}
