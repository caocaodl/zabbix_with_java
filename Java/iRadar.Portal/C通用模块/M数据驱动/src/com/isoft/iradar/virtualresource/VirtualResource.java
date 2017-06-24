package com.isoft.iradar.virtualresource;

import java.util.List;

import com.isoft.iradar.core.utils.EasyList;


public class VirtualResource {
	private ResourceType type;
	private String id;
	private String name;
	private List<String> ips;
	private String tenantId;
	
	public ResourceType getType() {
		return type;
	}
	public void setType(ResourceType type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getIps() {
		return ips;
	}
	public void setIps(List<String> ips) {
		this.ips = ips;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
