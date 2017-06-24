package com.isoft.biz.dao;

import org.apache.commons.lang.NotImplementedException;

public enum NameSpaceEnum {
	SYS_LOG("系统日志流水号"),
	SYS_USER("系统用户流水号"),
	SYS_USER_ROLE("系统用户角色流水号"),
	SYS_USER_ROLE_FUNC("系统用户角色功能流水号"),
	;

	static {
		if (NameSpaceEnum.values().length == 0) {
			System.out.println("根据开发框架约定，请自定义实现 com.isoft.biz.dao.NameSpaceEnum");
			throw new NotImplementedException(
					"根据开发框架约定，请自定义实现 com.isoft.biz.dao.NameSpaceEnum");
		}
	}

	private NameSpaceEnum(String note) {
		this.note = note;
	}

	private String note;

	public String note() {
		return this.note != null ? this.note : "";
	}
}
