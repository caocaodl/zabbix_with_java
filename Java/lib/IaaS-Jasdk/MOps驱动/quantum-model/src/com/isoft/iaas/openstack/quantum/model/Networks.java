package com.isoft.iaas.openstack.quantum.model;

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

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<Network> list) {
		this.list = list;
	}

	public String toString() {
		return "Networks [list=" + list + "]";
	}

	public Iterator<Network> iterator() {
		return list.iterator();
	}

}
