package com.isoft.web.listener;

import org.apache.commons.lang.NotImplementedException;

public enum DataSourceEnum {
	IRADAR("iradar"),
	;

	static {
		if (DataSourceEnum.values().length == 0) {
			System.out.println("根据开发框架约定，请自定义实现 com.isoft.web.listener.DataSourceEnum");
			throw new NotImplementedException(
					"根据开发框架约定，请自定义实现 com.isoft.web.listener.DataSourceEnum");
		}
	}

	private static final String JNDI_PREFIX = "java:comp/env/jdbc/";

	private String dsName;

	private DataSourceEnum(String dsName) {
		this.dsName = dsName;
	}

	public String getDsName() {
		return dsName;
	}

	public String getJndiName() {
		return JNDI_PREFIX + dsName;
	}

	@Override
	public String toString() {
		return this.dsName;
	}
}
