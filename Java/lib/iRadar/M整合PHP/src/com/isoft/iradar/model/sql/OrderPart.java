package com.isoft.iradar.model.sql;

import java.util.ArrayList;
import java.util.List;

public class OrderPart extends SqlPart {

	public OrderPart(SqlBuilder sqlBuilder) {
		super(sqlBuilder);
	}

	@Override
	public String[] arrayUnique() {
		List<String> fieldList = new ArrayList<String>();
		for(String f:namedList){
			fieldList.add(namedMap.get(f));
		}
		return fieldList.toArray(new String[0]);
	}

	@Override
	public void keepKeyClear() {
		super.keepKeyClear();
		namedList.clear();
	}

}
