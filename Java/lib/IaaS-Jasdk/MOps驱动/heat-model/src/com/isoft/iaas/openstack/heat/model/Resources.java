package com.isoft.iaas.openstack.heat.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Resources implements Iterable<Resource>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("resources")
	private List<Resource> list;

	public List<Resource> getList() {
		return list;
	}

	@Override
	public Iterator<Resource> iterator() {
		return list.iterator();
	}

	@Override
	public String toString() {
		return "Resources{" + "list=" + list + '}';
	}
}