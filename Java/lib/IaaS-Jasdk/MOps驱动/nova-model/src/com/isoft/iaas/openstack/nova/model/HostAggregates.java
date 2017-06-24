package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class HostAggregates implements Iterable<HostAggregate>, Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("aggregates")
	private List<HostAggregate> list;

	/**
	 * @return the list
	 */
	public List<HostAggregate> getList() {
		return list;
	}

	@Override
	public Iterator<HostAggregate> iterator() {
		return list.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HostAggregates [list=" + list + "]";
	}

}
