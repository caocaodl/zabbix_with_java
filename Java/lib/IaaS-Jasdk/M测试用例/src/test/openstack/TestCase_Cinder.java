package test.openstack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isoft.iaas.openstack.cinder.v2.Cinder;
import com.isoft.iaas.openstack.cinder.v2.model.EncryptionType;
import com.isoft.iaas.openstack.cinder.v2.model.EncryptionTypeForCreate;
import com.isoft.iaas.openstack.cinder.v2.model.ExtraSpecs;
import com.isoft.iaas.openstack.cinder.v2.model.Type;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.Access.Service;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.test.Mapper;

public class TestCase_Cinder extends BaseTestCase {

	private Cinder client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		List<Service> serviceCatalogs = access.getServiceCatalog();
		String endpoint = KeystoneUtils.findEndpointURL(serviceCatalogs,
				"volumev2", "RegionOne", Facing.PUBLIC);
		System.out.println("publicURL => " + endpoint);

		this.client = new Cinder(endpoint);
		this.client.token(access.getToken().getId());
	}

	public void test_type_list() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			Mapper.f(t);
		}
	}

	public void test_type_delete() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				client.types().delete(t.getId()).execute();
			}
		}
	}

	public void test_type_create() throws Exception {
		int n = 5;
		Type type = null;
		Map<String, Object> extraspecs = null;
		for (int i = 0; i < n; i++) {
			type = new Type();
			type.setName("test_" + i);
			extraspecs = new HashMap();
			extraspecs.put("age", i);
			type.setExtraspecs(extraspecs);
			type = client.types().create(type).execute();
			Mapper.f(type);
		}
	}

	public void test_type_extra_specs_set() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				ExtraSpecs specs = new ExtraSpecs();
				specs.put("currenttimemillis", String.valueOf(System.currentTimeMillis()));
				specs =client.types().extraspecs(t.getId()).set(specs).execute();
				Mapper.f(specs);
			}
		}
	}
	
	public void test_type_extra_specs_unset() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				client.types().extraspecs(t.getId()).unset("currenttimemillis").execute();
			}
		}
	}

	public void test_type_extra_specs_list() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				ExtraSpecs specs = client.types().extraspecs(t.getId()).list().execute();
				Mapper.f(specs);
			}
		}
	}
	
	public void test_encryption_type_create() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				EncryptionTypeForCreate encryType = new EncryptionTypeForCreate();
				encryType.setProvider("LuksEncryptor");
				encryType.setKeysize(128);
				encryType.setCipher("aes-xts-plain64");
				encryType.setControlLocation("back-end");
				EncryptionTypeForCreate etype = client.types().encryptiontypes(t.getId()).set(encryType).execute();
				Mapper.f(etype);
			}
		}
	}
	
	public void test_encryption_type_delete() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				client.types().encryptiontypes(t.getId()).delete().execute();
			}
		}
	}
	
	public void test_encryption_type_show() throws Exception {
		List<Type> types = client.types().list().execute().getList();
		for (Type t : types) {
			if (t.getName().startsWith("test_")) {
				EncryptionType encryType = client.types().encryptiontypes(t.getId()).show().execute();
				Mapper.f(encryType);
			}
		}
	}
}
