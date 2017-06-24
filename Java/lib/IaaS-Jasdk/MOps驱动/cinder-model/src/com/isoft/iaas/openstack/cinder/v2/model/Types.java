package com.isoft.iaas.openstack.cinder.v2.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Types implements Iterable<Type>, Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("volume_types")
	private List<Type> list;

	/**
	 * @return the list
	 */
	public List<Type> getList() {
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Volumes [list=" + list + "]";
	}

	@Override
	public Iterator<Type> iterator() {
		return list.iterator();
	}

}
