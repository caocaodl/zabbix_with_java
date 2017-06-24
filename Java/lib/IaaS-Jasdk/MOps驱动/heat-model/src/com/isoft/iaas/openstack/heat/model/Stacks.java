package com.isoft.iaas.openstack.heat.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Stacks implements Iterable<Stack>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("stacks")
	private List<Stack> list;

	@Override
	public Iterator<Stack> iterator() {
		return list.iterator();
	}
}
