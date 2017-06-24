package com.isoft.framework.persistlayer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DbMarshaller implements IDbMarshaller {
	
	private ResultSetMetaData rsMeta = null;
	private int colCount = 0;
	private String propName = null;
	private Object propVale = null;

	@Override
	public Object marshal(ResultSet rset) throws SQLException {
		if (rsMeta == null) {
			rsMeta = rset.getMetaData();
			colCount = rsMeta.getColumnCount();
		}
		Map vo = new HashMap();
		for (int i = 0; i < colCount; i++) {
			propName = rsMeta.getColumnLabel(i + 1);
			propVale = rset.getObject(propName);
			vo.put(propName, propVale);
		}

		return vo;
	}
}
