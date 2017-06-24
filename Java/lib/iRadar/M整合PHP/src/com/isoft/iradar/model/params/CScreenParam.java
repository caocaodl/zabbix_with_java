package com.isoft.iradar.model.params;

import java.util.Map;

public class CScreenParam extends CParamWrapper {

	private static final long serialVersionUID = 1L;

	private Boolean isFlickerfree;
	private String pageFile;
	private Integer mode;
	private Long timestamp;
	private Integer resourcetype;
	private String groupId;
	private String hostId;
	private String dataId;
	private Long period;
	private Long stime;
	private String profileIdx;
	private Integer profileIdx2;
	private Boolean updateProfile;
	private String screenId;
	private Map screen;
	private String screenItemId;
	private Map screenitem;
	private Map timeline;
	private String action;

	public Boolean getIsFlickerfree() {
		return isFlickerfree;
	}

	public void setIsFlickerfree(Boolean isFlickerfree) {
		this.isFlickerfree = isFlickerfree;
	}

	public String getPageFile() {
		return pageFile;
	}

	public void setPageFile(String pageFile) {
		this.pageFile = pageFile;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getResourcetype() {
		return resourcetype;
	}

	public void setResourcetype(Integer resourcetype) {
		this.resourcetype = resourcetype;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public Long getStime() {
		return stime;
	}

	public void setStime(Long stime) {
		this.stime = stime;
	}

	public String getProfileIdx() {
		return profileIdx;
	}

	public void setProfileIdx(String profileIdx) {
		this.profileIdx = profileIdx;
	}

	public Integer getProfileIdx2() {
		return profileIdx2;
	}

	public void setProfileIdx2(Integer profileIdx2) {
		this.profileIdx2 = profileIdx2;
	}

	public Boolean getUpdateProfile() {
		return updateProfile;
	}

	public void setUpdateProfile(Boolean updateProfile) {
		this.updateProfile = updateProfile;
	}

	public String getScreenId() {
		return screenId;
	}

	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}

	public Map getScreen() {
		return screen;
	}

	public void setScreen(Map screen) {
		this.screen = screen;
	}

	public String getScreenItemId() {
		return screenItemId;
	}

	public void setScreenItemId(String screenItemId) {
		this.screenItemId = screenItemId;
	}

	public Map getScreenitem() {
		return screenitem;
	}

	public void setScreenitem(Map screenitem) {
		this.screenitem = screenitem;
	}

	public Map getTimeline() {
		return timeline;
	}

	public void setTimeline(Map timeline) {
		this.timeline = timeline;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
