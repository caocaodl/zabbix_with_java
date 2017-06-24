package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Hypervisors implements Iterable<Hypervisors.Hypervisor>,
		Serializable {

	private static final long serialVersionUID = 1L;

	public static final class Hypervisor {

		@JsonProperty("id")
		private String id;

		@JsonProperty("hypervisor_hostname")
		private String hypervisorHostname;

		@JsonProperty("servers")
		private List<Server> servers;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Hypervisors [id=" + id + ", hypervisorHostname="
					+ hypervisorHostname + "]";
		}

		public String getHypervisorHostname() {
			return hypervisorHostname;
		}

		public String getId() {
			return id;
		}

		public List<Server> getServers() {
			return servers;
		}

	}

	@JsonProperty("hypervisors")
	private List<Hypervisor> hypervisors;

	public List<Hypervisor> getHypervisors() {
		return hypervisors;
	}

	@Override
	public Iterator<Hypervisors.Hypervisor> iterator() {
		return hypervisors.iterator();
	}
	
	public static final class Server {
		@JsonProperty("uuid")
		private String uuid;
		
		@JsonProperty("name")
		private String name;

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Hypervisors [list=" + hypervisors + "]";
	}

}
