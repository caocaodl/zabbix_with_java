package com.isoft.iaas.openstack.ceilometer.v2.api;

import java.util.Arrays;
import java.util.Comparator;

import com.isoft.iaas.openstack.base.client.HttpMethod;
import com.isoft.iaas.openstack.base.client.OpenStackClient;
import com.isoft.iaas.openstack.base.client.OpenStackRequest;
import com.isoft.iaas.openstack.ceilometer.v2.model.Meter;
import com.isoft.iaas.openstack.ceilometer.v2.model.Query;
import com.isoft.iaas.openstack.ceilometer.v2.model.Sample;
import com.isoft.iaas.openstack.ceilometer.v2.model.Statistic;

public class MetersResource {

	private final OpenStackClient CLIENT;

	public MetersResource(OpenStackClient client) {
		CLIENT = client;
	}

	public List list() {
		return new List();
	}
	
	public SamplesResource samples(String meter, Query ... queries) {
		SamplesResource samples = new SamplesResource(meter);
		if (queries != null && queries.length > 0) {
			for (Query q : queries) {
				q.marshal(samples);
			}
		}
		return samples;
	}
	
	public StatisticsResource statistics(String meter, Query ... queries) {
		StatisticsResource statistics = new StatisticsResource(meter);
		if (queries != null && queries.length > 0) {
			for (Query q : queries) {
				q.marshal(statistics);
			}
		}
		return statistics;
	}

	public class List extends OpenStackRequest<Meter[]> {

		public List() {
			super(CLIENT, HttpMethod.GET, "/meters",
					null, Meter[].class);
		}

	}
	
	public class SamplesResource extends OpenStackRequest<Sample[]> {

		public SamplesResource(String meter) {
			super(CLIENT, HttpMethod.GET, 
					new StringBuilder("/meters/").append(meter),
					null, Sample[].class);
		}
		
		public SamplesResource limit(int limits){
			this.queryParam("limit", limits);
			return this;
		}

		@Override
		public Sample[] execute() {
			Sample[] samples = super.execute();
			if (samples != null && samples.length > 0) {
				Arrays.sort(samples, new Comparator<Sample>() {
					@Override
					public int compare(Sample s1, Sample s2) {
						return s1.getRecordedAt().compareTo(s2.getRecordedAt());
					}
				});
			}
			return samples;
		}
		
	}
	
	public class StatisticsResource extends OpenStackRequest<Statistic[]> {

		public StatisticsResource(String meter) {
			super(CLIENT, HttpMethod.GET, 
					new StringBuilder("/meters/").append(meter).append("/statistics"),
					null, Statistic[].class);
		}

	}

}
