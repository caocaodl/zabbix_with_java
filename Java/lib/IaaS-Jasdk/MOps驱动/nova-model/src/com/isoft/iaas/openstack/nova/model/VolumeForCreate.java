package com.isoft.iaas.openstack.nova.model;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

@JsonRootName("volume")
public class VolumeForCreate implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("attach_status")
	private String attachStatus;
	
	@JsonProperty
	private String imageRef;

	private Integer size;

	@JsonProperty("availability_zone")
	private String availabilityZone;

	@JsonProperty("project_id")
	private String projectid;
	
	@JsonProperty("source_volid")
	private String sourceVolid;
	
	@JsonProperty("display_name")
	private String name;

	@JsonProperty("display_description")
	private String description;

	@JsonProperty("snapshot_id")
	private Integer snapshotId;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("user_id")
	private String userid;
	
	@JsonProperty("volume_type")
	private String volumeType;

	public String getAttachStatus() {
		return attachStatus;
	}

	public void setAttachStatus(String attachStatus) {
		this.attachStatus = attachStatus;
	}

	public String getImageRef() {
		return imageRef;
	}

	public void setImageRef(String imageRef) {
		this.imageRef = imageRef;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getSourceVolid() {
		return sourceVolid;
	}

	public void setSourceVolid(String sourceVolid) {
		this.sourceVolid = sourceVolid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getVolumeType() {
		return volumeType;
	}

	public void setVolumeType(String volumeType) {
		this.volumeType = volumeType;
	}

	private Map<String, String> metadata;

	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	/**
	 * @return the availabilityZone
	 */
	public String getAvailabilityZone() {
		return availabilityZone;
	}

	/**
	 * @param availabilityZone
	 *            the availabilityZone to set
	 */
	public void setAvailabilityZone(String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the snapshotId
	 */
	public Integer getSnapshotId() {
		return snapshotId;
	}

	/**
	 * @param snapshotId
	 *            the snapshotId to set
	 */
	public void setSnapshotId(Integer snapshotId) {
		this.snapshotId = snapshotId;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata
	 *            the metadata to set
	 */
	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VolumeForCreate [size=" + size + ", availabilityZone="
				+ availabilityZone + ", name=" + name + ", description="
				+ description + ", snapshotId=" + snapshotId + ", metadata="
				+ metadata + "]";
	}

}
