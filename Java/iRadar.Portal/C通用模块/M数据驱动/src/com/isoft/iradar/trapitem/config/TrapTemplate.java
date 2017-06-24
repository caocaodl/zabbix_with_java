package com.isoft.iradar.trapitem.config;

import java.util.List;

import com.isoft.iradar.core.utils.EasyList;

public class TrapTemplate {
	private String name;
	private String collector;
	private List<TrapItem> adminItems = EasyList.build();
	private List<TrapItem> tenantItems = EasyList.build();
	private List<TrapItem> ovirtItems = EasyList.build();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCollector() {
		return collector;
	}
	public void setCollector(String collector) {
		this.collector = collector;
	}
	
	public List<TrapItem> getAdminItems() {
		return adminItems;
	}
	public List<TrapItem> getTenantItems() {
		return tenantItems;
	}
	public List<TrapItem> getOvirtItems() {
		return ovirtItems;
	}
	
	public void addAdminItem(TrapItem item) {
		item.setTemplate(this);
		this.adminItems.add(item);
	}
	public void addTenantItem(TrapItem item) {
		item.setTemplate(this);
		this.tenantItems.add(item);
	}
	public void addOvirtItem(TrapItem item) {
		item.setCollector(this.getCollector());
		item.setTemplate(this);
		this.ovirtItems.add(item);
	}
}
