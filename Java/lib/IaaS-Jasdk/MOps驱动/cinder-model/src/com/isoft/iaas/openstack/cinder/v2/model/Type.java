package com.isoft.iaas.openstack.cinder.v2.model;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("volume_type")
public class Type implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	@JsonProperty("extra_specs")
	private Map<String, Object> extraspecs;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map<String, Object> getExtraspecs() {
		return extraspecs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setExtraspecs(Map<String, Object> extraspecs) {
		this.extraspecs = extraspecs;
	}

}
