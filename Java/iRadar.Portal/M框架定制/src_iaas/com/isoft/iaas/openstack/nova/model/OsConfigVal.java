package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("val")
public class OsConfigVal implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("id")
	private int id;

	@JsonProperty("key")
	private String key;
	
	@JsonProperty("name")
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getKeyNum() {
		return Float.valueOf(getKey());
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
