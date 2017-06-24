package com.isoft.iaas.openstack.glance.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class Images implements Iterable<Image>, Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("images")
	private List<Image> list;

	private String schema;

	private String first;

	/**
	 * @return the list
	 */
	public List<Image> getList() {
		return list;
	}

	@Override
	public Iterator<Image> iterator() {
		return list.iterator();
	}

	public String getSchema() {
		return schema;
	}

	public String getFirst() {
		return first;
	}

}
