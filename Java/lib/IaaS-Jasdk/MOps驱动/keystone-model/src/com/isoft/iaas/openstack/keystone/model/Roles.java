package com.isoft.iaas.openstack.keystone.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Roles implements Iterable<Role>, Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("roles")
	private List<Role> list;

	/**
	 * @return the list
	 */
	public List<Role> getList() {
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Roles [list=" + list + "]";
	}

	@Override
	public Iterator<Role> iterator() {
		return list.iterator();
	}

}
