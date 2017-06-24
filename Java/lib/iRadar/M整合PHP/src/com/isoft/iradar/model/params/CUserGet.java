package com.isoft.iradar.model.params;

public class CUserGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] usrgrpIds;
	private String[] userIds;
	private Long[] mediaIds;
	private Long[] mediaTypeIds;
	private Object selectUsrgrps;
	private Object selectMedias;
	private Object selectMediatypes;
	private Object access;

	public Long[] getUsrgrpIds() {
		return usrgrpIds;
	}

	public void setUsrgrpIds(Long... usrgrpIds) {
		this.usrgrpIds = usrgrpIds;
	}

	public String[] getUserIds() {
		return userIds;
	}

	public void setUserIds(String... userIds) {
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

	public void setMediaTypeIds(Long... mediatypeIds) {
		this.mediaTypeIds = mediatypeIds;
	}

	public Object getSelectUsrgrps() {
		return selectUsrgrps;
	}

	public void setSelectUsrgrps(Object selectUsrgrps) {
		this.selectUsrgrps = selectUsrgrps;
	}

	public Object getSelectMedias() {
		return selectMedias;
	}

	public void setSelectMedias(Object selectMedias) {
		this.selectMedias = selectMedias;
	}

	public Object getSelectMediatypes() {
		return selectMediatypes;
	}

	public void setSelectMediatypes(Object selectMediatypes) {
		this.selectMediatypes = selectMediatypes;
	}

	public Object getAccess() {
		return access;
	}

	public void setAccess(Object access) {
		this.access = access;
	}

}
