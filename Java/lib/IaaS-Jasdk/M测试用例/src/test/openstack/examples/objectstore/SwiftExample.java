package test.openstack.examples.objectstore;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Tenants;
import com.isoft.iaas.openstack.keystone.model.Access.Facing;
import com.isoft.iaas.openstack.keystone.model.authentication.TokenAuthentication;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;
import com.isoft.iaas.openstack.keystone.utils.KeystoneUtils;
import com.isoft.iaas.openstack.swift.Swift;
import com.isoft.iaas.openstack.swift.model.ObjectDownload;
import com.isoft.iaas.openstack.swift.model.ObjectForUpload;

public class SwiftExample {

	private static final File TEST_FILE = new File("pom.xml");

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		// access with unscoped token
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD)).execute();

		// use the token in the following requests
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));

		Tenants tenants = keystone.tenants().list().execute();

		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {

			access = keystone
					.tokens()
					.authenticate(
							new TokenAuthentication(access.getToken().getId()))
					.withTenantId(tenants.getList().get(0).getId()).execute();

			Swift swift = new Swift(KeystoneUtils.findEndpointURL(
					access.getServiceCatalog(), "object-store", null, Facing.PUBLIC));
			swift.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));

			// swiftClient.execute(new DeleteContainer("navidad2"));

			swift.containers().create("navidad2").execute();

			System.out.println(swift.containers().list());

			ObjectForUpload upload = new ObjectForUpload();
			upload.setContainer("navidad2");
			upload.setName("example2");
			upload.setInputStream(new FileInputStream(TEST_FILE));
			swift.containers().container("navidad2").upload(upload).execute();

			// System.out.println(swiftClient.execute(new
			// ListObjects("navidad2", new HashMap<String, String>() {{
			// put("path", "");
			// }})).get(0).getContentType());

			ObjectDownload download = swift.containers().container("navidad")
					.download("example2").execute();
			write(download.getInputStream(), "example2");
		}

	}

	private static void write(InputStream is, String path) {
		try {
			OutputStream stream = new BufferedOutputStream(
					new FileOutputStream(path));
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				stream.write(buffer, 0, len);
			}
			stream.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

}
