package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Services implements Iterable<Service>, Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("services")
	private List<Service> list;

	/**
	 * @return the list
	 */
	public List<Service> getList() {
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Services [list=" + list + "]";
	}

	@Override
	public Iterator<Service> iterator() {
		return list.iterator();
	}

}
