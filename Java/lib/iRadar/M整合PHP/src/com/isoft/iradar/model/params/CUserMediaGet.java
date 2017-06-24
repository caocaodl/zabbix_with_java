package com.isoft.iradar.model.params;

public class CUserMediaGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] usrgrpIds;
	private Long[] userIds;
	private Long[] mediaIds;
	private Long[] mediatypeIds;

	public Long[] getUsrgrpIds() {
		return usrgrpIds;
	}

	public void setUsrgrpIds(Long... usrgrpIds) {
		this.usrgrpIds = usrgrpIds;
	}

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

	public Long[] getMediatypeIds() {
		return mediatypeIds;
	}

	public void setMediatypeIds(Long... mediatypeIds) {
		this.mediatypeIds = mediatypeIds;
	}

}
