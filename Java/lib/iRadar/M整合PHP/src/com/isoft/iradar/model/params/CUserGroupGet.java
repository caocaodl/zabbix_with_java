package com.isoft.iradar.model.params;

public class CUserGroupGet extends CParamGet {

	private static final long serialVersionUID = 1L;

	private Long[] usrgrpIds;
	private String[] userIds;
	private Boolean status;
	private Object selectUsers;

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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Object getSelectUsers() {
		return selectUsers;
	}

	public void setSelectUsers(Object selectUsers) {
		this.selectUsers = selectUsers;
	}

}
