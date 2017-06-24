package com.isoft.iaas.openstack.cinder.v2.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class EncryptionType implements Serializable {

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

	@JsonProperty
	private Boolean deleted;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("updated_at")
	private String updatedAt;

	@JsonProperty("deleted_at")
	private String deletedAt;

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

	public Boolean getDeleted() {
		return deleted;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getDeletedAt() {
		return deletedAt;
	}

}
