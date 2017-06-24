package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("hypervisor_statistics")
public class Statistic implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("count")
	private int count;

	@JsonProperty("vcpus_used")
	private int vcpusUsed;

	@JsonProperty("local_gb_used")
	private int localGbUsed;

	@JsonProperty("memory_mb")
	private int memoryMb;

	@JsonProperty("current_workload")
	private int currentWorkload;

	@JsonProperty("vcpus")
	private int vcpus;

	@JsonProperty("running_vms")
	private int runningVms;

	@JsonProperty("free_disk_gb")
	private int freeDiskGb;

	@JsonProperty("disk_available_least")
	private int diskAvailableLeast;

	@JsonProperty("local_gb")
	private int localGb;

	@JsonProperty("free_ram_mb")
	private int freeRamMb;

	@JsonProperty("memory_mb_used")
	private int memoryMbUsed;

	public int getCount() {
		return count;
	}

	public int getVcpusUsed() {
		return vcpusUsed;
	}

	public int getLocalGbUsed() {
		return localGbUsed;
	}

	public int getMemoryMb() {
		return memoryMb;
	}

	public int getCurrentWorkload() {
		return currentWorkload;
	}

	public int getVcpus() {
		return vcpus;
	}

	public int getRunningVms() {
		return runningVms;
	}

	public int getFreeDiskGb() {
		return freeDiskGb;
	}

	public int getDiskAvailableLeast() {
		return diskAvailableLeast;
	}

	public int getLocalGb() {
		return localGb;
	}

	public int getFreeRamMb() {
		return freeRamMb;
	}

	public int getMemoryMbUsed() {
		return memoryMbUsed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		// TODO:
		return "Statistics [list=" + 1 + "]";
	}

}
