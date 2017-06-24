package com.isoft.iaas.openstack.cinder.v2.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("volume")
public class Volume implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String id;

	private String status;

	private String name;
	
	private String bootable;
	
	private Boolean encrypted;

	private String description;
	
	@JsonProperty("availability_zone")
	private String availabilityZone;

	@JsonProperty("volume_type")
	private String volumeType;

	@JsonProperty("snapshot_id")
	private String snapshotId;
	
	@JsonProperty("user_id")
	private String userid;
	
	@JsonProperty("os-vol-tenant-attr:tenant_id")
	private String tenantid;
	
	@JsonProperty("os-vol-host-attr:host")
	private String host;
		
	@JsonProperty("os-vol-mig-status-attr:migstat")
	private String migstat;

	@JsonProperty("os-vol-mig-status-attr:name_id")
	private String nameid;

	public String getTenantid() {
		return tenantid;
	}

	public String getHost() {
		return host;
	}

	public String getMigstat() {
		return migstat;
	}

	public String getNameid() {
		return nameid;
	}

	private List<Map<String, Object>> attachments;

	private Map<String, Object> metadata;

	@JsonProperty("created_at")
	private String createdAt;

	private Integer size;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the availabilityZone
	 */
	public String getAvailabilityZone() {
		return availabilityZone;
	}

	/**
	 * @return the volumeType
	 */
	public String getVolumeType() {
		return volumeType;
	}

	/**
	 * @return the snapshotId
	 */
	public String getSnapshotId() {
		return snapshotId;
	}

	/**
	 * @return the attachments
	 */
	public List<Map<String, Object>> getAttachments() {
		return attachments;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}

	/**
	 * @return the createdAt
	 */
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	public String getBootable() {
		return bootable;
	}

	public Boolean getEncrypted() {
		return encrypted;
	}
	
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserid() {
		return userid;
	}
}
