package test.openstack.examples.glance;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.glance.Glance;
import com.isoft.iaas.openstack.glance.model.Image;
import com.isoft.iaas.openstack.glance.model.Images;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.Access.Service;
import com.isoft.iaas.openstack.keystone.model.Access.Service.Endpoint;
import com.isoft.iaas.openstack.keystone.utils.KeystoneTokenProvider;

public class GlanceListImages {

	protected static String IMAGE_CONTENT = "Hello World!";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeystoneTokenProvider keystone = new KeystoneTokenProvider(
				Configuration.KEYSTONE_AUTH_URL,
				Configuration.KEYSTONE_USERNAME,
				Configuration.KEYSTONE_PASSWORD);

		Access access = keystone.getAccessByTenant(Configuration.TENANT_NAME);

		Service glanceService = null;

		for (Service service : access.getServiceCatalog()) {
			if (service.getType().equals("image")) {
				glanceService = service;
				break;
			}
		}

		if (glanceService == null) {
			throw new RuntimeException("Glance service not found");
		}

		for (Endpoint endpoint : glanceService.getEndpoints()) {
			// Glance glance = new Glance(endpoint.getPublicURL() + "/v1");
			Glance glance = new Glance(endpoint.getPublicURL());
			// Glance glance = new Glance("http://192.168.137.150:9292/v2");
			glance.setTokenProvider(keystone
					.getProviderByTenant(Configuration.TENANT_NAME));

			/*
			 * // Creating a new image Image newImage = new Image();
			 * newImage.setDiskFormat("raw");
			 * newImage.setContainerFormat("bare");
			 * newImage.setName("os-java-glance-test"); newImage =
			 * glance.images().create(newImage).execute();
			 * 
			 * // Uploading image ImageUpload uploadImage = new
			 * ImageUpload(newImage); uploadImage.setInputStream(new
			 * ByteArrayInputStream(IMAGE_CONTENT.getBytes()));
			 * glance.images().upload(newImage.getId(), uploadImage).execute();
			 * 
			 * // Downloading the image and displaying the image content try {
			 * byte[] imgContent = new byte[IMAGE_CONTENT.length()];
			 * ImageDownload downloadImage =
			 * glance.images().download(newImage.getId()).execute();
			 * downloadImage.getInputStream().read(imgContent, 0,
			 * imgContent.length); System.out.println(new String(imgContent)); }
			 * catch (IOException e) { e.printStackTrace(); }
			 */
			Images images = glance.images().list(false).execute();

			for (Image image : images) {
				System.out.println(image);
				// System.out.println(glance.images().show("a").execute());
			}
			/*
			 * glance.images().delete(newImage.getId()).execute();
			 */
		}
	}

}
