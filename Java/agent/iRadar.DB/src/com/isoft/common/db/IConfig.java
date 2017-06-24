package com.isoft.common.db;

import java.sql.Connection;

public interface IConfig {
	public String call(Connection conn, String cmd);
}
