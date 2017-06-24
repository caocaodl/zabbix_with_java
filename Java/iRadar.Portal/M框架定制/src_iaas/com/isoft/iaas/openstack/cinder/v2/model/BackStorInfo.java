package com.isoft.iaas.openstack.cinder.v2.model;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

//all_cap_gb: "-1"
//cinder_status: true
//free_cap_gb: "-1"
//backend_stor_status: {}

@JsonRootName("volumes")
public class BackStorInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("cinder_status")
	private boolean status;

	@JsonProperty("all_cap_gb")
	private int allCapGb;

	@JsonProperty("free_cap_gb")
	private int freeCapGb;

	@JsonProperty("backend_stor_status")
	private Map<String, Object> backendStorStatus;

	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getAllCapGb() {
		return allCapGb;
	}

	public void setAllCapGb(int allCapGb) {
		this.allCapGb = allCapGb;
	}

	public int getFreeCapGb() {
		return freeCapGb;
	}

	public void setFreeCapGb(int freeCapGb) {
		this.freeCapGb = freeCapGb;
	}

	public Map<String, Object> getBackendStorStatus() {
		return backendStorStatus;
	}

	public void setBackendStorStatus(Map<String, Object> backendStorStatus) {
		this.backendStorStatus = backendStorStatus;
	}

}
