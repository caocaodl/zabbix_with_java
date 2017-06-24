package com.isoft.iaas.openstack.cinder.v2.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("encryption")
public class EncryptionTypeForCreate extends EncryptionType {

	private static final long serialVersionUID = 1L;

	@JsonProperty("volume_type_id")
	private String typeid;

	@JsonProperty
	private String provider;

	@JsonProperty("control_location")
	private String controlLocation;

	@JsonProperty
	private String cipher;

	@JsonProperty("key_size")
	private Integer keysize;

	public String getTypeid() {
		return typeid;
	}

	public String getProvider() {
		return provider;
	}

	public String getControlLocation() {
		return controlLocation;
	}

	public String getCipher() {
		return cipher;
	}

	public Integer getKeysize() {
		return keysize;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public void setControlLocation(String controlLocation) {
		this.controlLocation = controlLocation;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

	public void setKeysize(Integer keysize) {
		this.keysize = keysize;
	}

}
