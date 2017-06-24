package com.isoft.framework.persistlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDbMarshaller {
	Object marshal(ResultSet rs) throws SQLException;
}
