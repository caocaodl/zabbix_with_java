package com.isoft.iradar.model.params;

public class CMediaTypeGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] userIds;
	private Long[] mediaIds;
	private Long[] mediaTypeIds;
	private Object selectUsers;

	public Long[] getUserIds() {
		return userIds;
	}

	public void setUserIds(Long... userIds) {
		this.userIds = userIds;
	}

	public Long[] getMediaIds() {
		return mediaIds;
	}

	public void setMediaIds(Long... mediaIds) {
		this.mediaIds = mediaIds;
	}

	public Long[] getMediaTypeIds() {
		return mediaTypeIds;
	}

	public void setMediaTypeIds(Long... mediaTypeIds) {
		this.mediaTypeIds = mediaTypeIds;
	}

	public Object getSelectUsers() {
		return selectUsers;
	}

	public void setSelectUsers(Object selectUsers) {
		this.selectUsers = selectUsers;
	}

}
