package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Networks implements Iterable<Network>, Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("networks")
	private List<Network> list;

	/**
	 * @return the list
	 */
	public List<Network> getList() {
		return list;
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

	@Override
	public Iterator<Network> iterator() {
		return list.iterator();
	}

}
