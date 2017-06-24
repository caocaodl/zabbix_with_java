package test.openstack.examples.metering.v2;

import test.openstack.examples.Configuration;

import com.isoft.iaas.openstack.base.client.OpenStackSimpleTokenProvider;
import com.isoft.iaas.openstack.ceilometer.Ceilometer;
import com.isoft.iaas.openstack.keystone.Keystone;
import com.isoft.iaas.openstack.keystone.model.Access;
import com.isoft.iaas.openstack.keystone.model.authentication.UsernamePassword;

public class TestAll {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Keystone keystone = new Keystone(Configuration.KEYSTONE_AUTH_URL);
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(Configuration.KEYSTONE_USERNAME,
								Configuration.KEYSTONE_PASSWORD))
				.withTenantName("admin").execute();

		Ceilometer ceilometer = new Ceilometer(
				Configuration.CEILOMETER_ENDPOINT);
		ceilometer.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));

		/*
		 * List<Resource> resources = ceilometer.execute(new
		 * ResourceList().eq("resource_id",
		 * "23b55841eedd41e99d5f3f32149ca086"));
		 * 
		 * 
		 * for(Resource r : resources) { Resource resource =
		 * ceilometer.execute(new ResourceShow().id(r.getResource())); }
		 */

		/*
		 * //List<Meter> meters = ceilometer.meters().list().execute();
		 * //execute(new MeterList().eq("project_id",
		 * "948eeb593acd4223ad572c49e1ef5709"));
		 * 
		 * 
		 * for(Meter m : meters) { System.out.println(m);
		 * 
		 * // List<Sample> samples = ceilometer.execute(new
		 * MeterShow().name(m.getName())); // for(Sample s : samples) { //
		 * System.out.println("\t" + s); // }
		 * 
		 * List<Statistics> stats = ceilometer.meters().statistics().execute();
		 * // (new MeterStatistics().name(m.getName())); for(Statistics s :
		 * stats) { System.out.println("\t\t" + s); }
		 * 
		 * 
		 * }
		 */

	}

}
