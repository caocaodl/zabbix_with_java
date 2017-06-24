package com.isoft.iaas.openstack.quantum.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Quotas implements Iterable<Quota>, Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("quotas")
	private List<Quota> list;

	/**
	 * @return the list
	 */
	public List<Quota> getList() {
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FloatingIps [list=" + list + "]";
	}

	@Override
	public Iterator<Quota> iterator() {
		return list.iterator();
	}

}
