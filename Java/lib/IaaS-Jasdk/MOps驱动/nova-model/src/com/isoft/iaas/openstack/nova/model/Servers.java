package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Servers implements Iterable<Server>, Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("servers")
	private List<Server> list;

	/**
	 * @return the list
	 */
	public List<Server> getList() {
		return list;
	}

	@Override
	public Iterator<Server> iterator() {
		return list.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Servers [list=" + list + "]";
	}

}
