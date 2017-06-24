package com.isoft.iaas.openstack.keystone.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Endpoints implements Iterable<Endpoint>, Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("endpoints")
	private List<Endpoint> list;

	/**
	 * @return the list
	 */
	public List<Endpoint> getList() {
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Endpoints [list=" + list + "]";
	}

	@Override
	public Iterator<Endpoint> iterator() {
		return list.iterator();
	}

}
